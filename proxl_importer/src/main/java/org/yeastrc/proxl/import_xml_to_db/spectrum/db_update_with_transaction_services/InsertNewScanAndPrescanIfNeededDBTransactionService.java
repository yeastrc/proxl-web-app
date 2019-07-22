package org.yeastrc.proxl.import_xml_to_db.spectrum.db_update_with_transaction_services;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import org.slf4j.LoggerFactory;import org.slf4j.Logger;
import org.yeastrc.xlink.dao.ScanDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.ScanDTO;
import org.yeastrc.xlink.dto.ScanFileDTO;
import org.yeastrc.proxl.import_xml_to_db.spectrum.common.searchers.ScanTableScanFileIdScanNumberGetIdSearcher;
import org.yeastrc.proxl.import_xml_to_db.spectrum.common.utils.IsCentroidNumberToCharConversion;
import org.yeastrc.proxl.import_xml_to_db.spectrum.mzml_mzxml.dto.MzML_MzXmlScan;
import org.yeastrc.proxl.import_xml_to_db.utils.RoundDecimalFieldsIfNecessary;

/**
 * Service for adding a new Scan and prev scan data as a single DB transaction
 *
 */
public class InsertNewScanAndPrescanIfNeededDBTransactionService {
	
	private static final Logger log = LoggerFactory.getLogger( InsertNewScanAndPrescanIfNeededDBTransactionService.class);
	
	InsertNewScanAndPrescanIfNeededDBTransactionService() { }
	private static InsertNewScanAndPrescanIfNeededDBTransactionService _INSTANCE = new InsertNewScanAndPrescanIfNeededDBTransactionService();
	public static InsertNewScanAndPrescanIfNeededDBTransactionService getInstance() { return _INSTANCE; }
	
	

	/**
	 * Inserts the scan file and headers records if needed
	 * 
	 * @param scanIn
	 * @param scanFileDTO
	 * @param saveSpectrumData
	 * @param mapOfScanNumbersToScanIds
	 * @return true if inserted ms2 record
	 * @throws Exception
	 */
	public boolean insertNewScanAndPrescanIfNeededDBTransactionService( 
			
			MzML_MzXmlScan scanIn, 
			ScanFileDTO scanFileDTO, 
			Map<Integer,Integer> mapOfScanNumbersToScanIds  ) throws Exception {
		
		
		boolean insertedRecord = false;;

		Connection dbConnection = null;
		
		
		try {
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			dbConnection.setAutoCommit(false);
			
			lockRequiredTables( dbConnection );
			
		 	
    		if ( ! isScanInDB( scanIn.getStartScanNum(), scanFileDTO.getId(), mapOfScanNumbersToScanIds, dbConnection ) ) { 
    			
    			insertedRecord = true;
    			
    			//  Not in DB so insert
    		
    			//  insert this ms2 scan and the previous ms1 scan if necessary
    			processScan( scanIn, scanFileDTO, mapOfScanNumbersToScanIds, dbConnection );
    		}
			
			
			dbConnection.commit();
			
		} catch ( Exception e ) {
			
			String msg = "Failed insertNewScanAndPrescanIfNeededDBTransactionService(...)";


			System.out.println( msg );
			System.err.println( msg );
			
			log.error( msg , e);

			if ( dbConnection != null ) {
				
				try {
					dbConnection.rollback();
					
				} catch (Exception ex) {
					
					String msgRollback = "Rollback Exception:  insertNewScanAndPrescanIfNeededDBTransactionService(...) Exception:  See Syserr or Sysout for original exception: Rollback Exception, tables 'scan' is in an inconsistent state. '" + ex.toString();

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
		
		return insertedRecord;
	}
	
	
	

	/**
	 * @param scanIn
	 * @param scanFileDTO
	 * @param mapOfScanNumbersToScanIds
	 * @param dbConnection
	 * @throws Exception
	 */
	public void processScan( 
			
			MzML_MzXmlScan scanIn, 
			ScanFileDTO scanFileDTO, 
			Map<Integer,Integer> mapOfScanNumbersToScanIds,
			
			Connection dbConnection ) throws Exception {


		ScanDTO scanDTO = createScanDTO(scanIn, scanFileDTO);

		ScanDAO.save( scanDTO, dbConnection );

		//  Put scan number and scan id in Map for inserting psm table records later
		
		mapOfScanNumbersToScanIds.put( scanDTO.getStartScanNumber(), scanDTO.getId() );
	}


	/**
	 * @param scanIn
	 * @param scanFileDTO
	 * @return
	 */
	private ScanDTO createScanDTO(MzML_MzXmlScan scanIn, ScanFileDTO scanFileDTO) {
		

		BigDecimal retentionTime = 
				RoundDecimalFieldsIfNecessary.roundDecimalFieldsIfNecessary_18comma9( scanIn.getRetentionTime() );
		
		BigDecimal preMZ = RoundDecimalFieldsIfNecessary.roundDecimalFieldsIfNecessary_18comma9( scanIn.getPrecursorMz() );
		
		
		ScanDTO scanDTO = new ScanDTO();

		scanDTO.setScanFileId( scanFileDTO.getId() );

		scanDTO.setLevel( scanIn.getMsLevel() );

		scanDTO.setStartScanNumber( scanIn.getStartScanNum() );
		scanDTO.setEndScanNumber( scanIn.getEndScanNum() );
		
		scanDTO.setPreMZ( preMZ );
		
		scanDTO.setPrecursorScanNum( scanIn.getPrecursorScanNum() );
		scanDTO.setRetentionTime( retentionTime );
		scanDTO.setFragmentationType( scanIn.getFragmentationType() );
		
		scanDTO.setPeakCount( scanIn.getPeakCount() );
		
		String isCentroidString = IsCentroidNumberToCharConversion.convertMzMLisCentroidNumberToChar( scanIn.getIsCentroided() );
		
		scanDTO.setIsCentroid( isCentroidString );
		
		return scanDTO;
	}
	
	/**
	 * @param scanNumber
	 * @param scanFileId
	 * @return - true if the params are in the DB
	 * @throws Exception
	 */
	private boolean isScanInDB( int scanNumber, int scanFileId, Map<Integer,Integer> mapOfScanNumbersToScanIds, Connection dbConnection ) throws Exception {
		
		int recordIdForScanFileIdScanNumberInScanTable = 
				ScanTableScanFileIdScanNumberGetIdSearcher.recordIdForScanFileIdScanNumberInScanTable( scanFileId, scanNumber, dbConnection );
		
		if ( recordIdForScanFileIdScanNumberInScanTable == ScanTableScanFileIdScanNumberGetIdSearcher.RECORD_NOT_FOUND_VALUE ) {

			return false;
		}

		//  Put scan number and scan id in Map for inserting psm table records later
		
		mapOfScanNumbersToScanIds.put( scanNumber, recordIdForScanFileIdScanNumberInScanTable );
		
		
		return true;
	}
	
	private static String lockTablesForWriteSQL = "LOCK TABLES scan WRITE";

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
