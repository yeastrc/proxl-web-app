package org.yeastrc.proxl.import_xml_to_db.spectrum.db_update_with_transaction_services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.ScanFileDAO;
import org.yeastrc.xlink.dao.ScanFileHeaderDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.ScanFileDTO;
import org.yeastrc.xlink.dto.ScanFileHeaderDTO;

/**
 * Service for adding a new Scan file and Scan file header data as a single DB transaction
 *
 */
public class AddNewScanFileAndHeadersIfNeededDBTransactionService {
	
	private static final Logger log = Logger.getLogger(AddNewScanFileAndHeadersIfNeededDBTransactionService.class);
	
	AddNewScanFileAndHeadersIfNeededDBTransactionService() { }
	private static AddNewScanFileAndHeadersIfNeededDBTransactionService _INSTANCE = new AddNewScanFileAndHeadersIfNeededDBTransactionService();
	public static AddNewScanFileAndHeadersIfNeededDBTransactionService getInstance() { return _INSTANCE; }
	
	

	/**
	 * Inserts the scan file and headers records if needed, otherwise updates scanFileDTO.id with the id from the database
	 * 
	 * @param scanFileDTO
	 * @param scanFileHeaderDTOList
	 * @return true if added record, otherwise false
	 * @throws Exception
	 */
	public boolean addNewScanFileAndHeadersDBTransactionService( ScanFileDTO scanFileDTO, List<ScanFileHeaderDTO> scanFileHeaderDTOList  ) throws Exception {
		
		
		boolean insertedNewScanFileDTO = false;

		Connection dbConnection = null;
		
		
		ScanFileDAO scanFileDAO = ScanFileDAO.getInstance();

		ScanFileDTO scanFileDTOFromDB = null;
		
		try {

			dbConnection = getConnectionWithAutocommitTurnedOff();
			
			lockRequiredTables( dbConnection );
			

			List<ScanFileDTO> scanFileDTOList = scanFileDAO.getScanFileDTOListByFilenameSha1Sum( scanFileDTO.getFilename(), scanFileDTO.getSha1sum(), dbConnection );
			
			if ( scanFileDTOList.isEmpty() ) {

			
			} else {
				
				if ( scanFileDTOList.size() > 1 ) {
					
					//  More than one record in database for Filename and SHA1Sum combination, throw an exception 
					
					String msg = "More than one ScanFile record found for scanFileName: '" + scanFileDTO.getFilename() + "', SHA1Sum: '" + scanFileDTO.getSha1sum();
					
					log.error( msg );
					
					throw new Exception( msg );  // EARLY EXIT  throw exception
				}
				
				if ( log.isInfoEnabled() ) {

					log.info( "Importing MzMl or MzXml file.  The file:  " + scanFileDTO.getFilename()
						+ " for path: " + scanFileDTO.getPath()
						+ "  has already been loaded so processing to ensure all scans for this proxl xml file are loaded."  );
				}
				
				//  Filename and SHA1Sum combination is in database, so use existing record
				
				scanFileDTOFromDB = scanFileDTOList.get( 0 );
				
			}
			

			if ( scanFileDTOFromDB != null ) {
				
				//  Record found for filename and sha1sum
				
				//  Copy the id to the passed in scanFileDTO
				
				scanFileDTO.setId( scanFileDTOFromDB.getId() );
				
			} else {
				
				//  Not found in DB so save the provided scan file and scan file headers
			
				insertedNewScanFileDTO = true;
				
				
				if ( log.isInfoEnabled() ) {
					
					log.info( "Adding to scan_file table, name: " + scanFileDTO.getFilename()
							+ ", path: " + scanFileDTO.getPath() );
				}

				ScanFileDAO.getInstance().save( scanFileDTO, dbConnection );

				for ( ScanFileHeaderDTO scanFileHeaderDTO : scanFileHeaderDTOList ) {

					scanFileHeaderDTO.setScanFileId( scanFileDTO.getId() );
					ScanFileHeaderDAO.save( scanFileHeaderDTO, dbConnection );
				}

			}
			
			
			dbConnection.commit();
			
		} catch ( Exception e ) {
			
			String msg = "Failed addNewScanFileDBTransactionService(...)";


			System.out.println( msg );
			System.err.println( msg );
			
			log.error( msg , e);

			if ( dbConnection != null ) {
				
				try {
					dbConnection.rollback();
					
				} catch (Exception ex) {
					
					String msgRollback = "Rollback Exception:  addNewScanFileDBTransactionService(...) Exception:  See Syserr or Sysout for original exception: Rollback Exception, tables 'scan' and 'scan_spectrum_data' are in an inconsistent state. '" + ex.toString();

					System.out.println( msgRollback );
					System.err.println( msgRollback );
					
					log.error( msgRollback, ex );

					throw new Exception( msgRollback, ex );
				}
			}
			
			throw e;
			
		} finally {
			
			try {

				if( dbConnection != null ) {
					unlockAllTable(dbConnection);
				}
				
				
			} finally {
			
				if( dbConnection != null ) {

					try {
						dbConnection.setAutoCommit(true);  /// reset for next user of connection
					} catch (Exception ex) {
						String msg = "Failed dbConnection.setAutoCommit(true) in addNewScanFileDBTransactionService(...)";

						System.out.println( msg );
						System.err.println( msg );

						throw new Exception(msg);
					}

					try { dbConnection.close(); } 
					catch(Throwable t ) { ; }
					dbConnection = null;
				}
			}
		}
		
		return insertedNewScanFileDTO;
	}
	
	
	
	/**
	 * @return
	 * @throws Exception
	 */
	private Connection getConnectionWithAutocommitTurnedOff(  ) throws Exception {
		
		Connection dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
		
		dbConnection.setAutoCommit(false);
		
		return dbConnection;
	}
	
	

	private static String lockTablesForWriteSQL = "LOCK TABLES scan_file WRITE, scan_file_header WRITE";


	/**
	 * @param dbConnection
	 * @throws Exception
	 */
	private void lockRequiredTables( Connection dbConnection ) throws Exception {
		

		PreparedStatement pstmt = null;

		try {

			pstmt = dbConnection.prepareStatement( lockTablesForWriteSQL );

			pstmt.executeUpdate();

		} catch (Exception sqlEx) {

			log.error("lockRequiredTables: Exception '" + sqlEx.toString() + ".\nSQL = " + lockTablesForWriteSQL , sqlEx);

			throw sqlEx;

		} finally {

			if (pstmt != null) {

				try {

					pstmt.close();

				} catch (SQLException ex) {

					// ignore

				}

			}

		}
		
		
	}
	


	private static String unlockAllTableSQL = "UNLOCK TABLES";

	/**

	 * Unlock All Tables

	 * @throws Exception

	 */

	public void unlockAllTable( Connection connection ) throws Exception {

		PreparedStatement pstmt = null;

		try {

			pstmt = connection.prepareStatement( unlockAllTableSQL );

			pstmt.executeUpdate();

		} catch (Exception sqlEx) {

			log.error("unlockAllTable: Exception '" + sqlEx.toString() + ".\nSQL = " + unlockAllTableSQL , sqlEx);

			throw sqlEx;

		} finally {

			if (pstmt != null) {

				try {

					pstmt.close();

				} catch (SQLException ex) {

					// ignore

				}

			}


		}

	}

	
	
	
}
