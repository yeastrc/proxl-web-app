package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.ScanRetentionTimeDTO;

/**
 * 
 *
 */
public class ScanRetentionTimeDAO {
	
	private static final Logger log = Logger.getLogger(ScanRetentionTimeDAO.class);
	
	
	
//
//	public static ScanRetentionTimeDTO getScanFromId( int scanId ) throws Exception {
//
//
//		ScanRetentionTimeDTO scanRetentionTimeDTO = null;;
//
//		// Get our connection to the database.
//		Connection connection = null;
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//
//		// Our SQL statement
//		final String sqlStr =  "SELECT UNCOMPRESS( spectrum_data ) AS spectrum_data, scan.* "
//			+ " FROM scan INNER JOIN scan_spectrum_data AS ssd ON  scan.id = ssd.scan_id "
//			+ " WHERE id = ?  " ;
//
//
//	
//		try {
//			
//
//			connection = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
//
//
//			pstmt = connection.prepareStatement( sqlStr );
//
//			pstmt.setInt( 1, scanId );
//
//			// Our results
//			rs = pstmt.executeQuery();
//
//			if ( rs.next() ) {
//
//				scanRetentionTimeDTO = new ScanRetentionTimeDTO();
//
//				scanRetentionTimeDTO.setId( rs.getInt( "id" ) );
//				scanRetentionTimeDTO.setScanFileId( rs.getInt( "scan_file_id" ) );
//				scanRetentionTimeDTO.setStartScanNumber( rs.getInt( "start_scan_number" ) );
//				scanRetentionTimeDTO.setEndScanNumber( rs.getInt( "end_scan_number" ) );
//				scanRetentionTimeDTO.setLevel( rs.getInt( "level" ) );
//				scanRetentionTimeDTO.setPreMZ( rs.getBigDecimal( "preMZ" ) );
//				scanRetentionTimeDTO.setPrecursorScanNum( rs.getInt( "precursor_scan_number" ) );
//				scanRetentionTimeDTO.setPrecursorScanId( rs.getInt( "precursor_scan_id" ) );
//				scanRetentionTimeDTO.setRetentionTime( rs.getBigDecimal( "retention_time" ) );
//				scanRetentionTimeDTO.setPeakCount( rs.getInt( "peak_count" ) );
//				scanRetentionTimeDTO.setFragmentationType( rs.getString( "fragmentation_type" ) );;
//				scanRetentionTimeDTO.setIsCentroid( rs.getString( "is_centroid" ) );
//				
//				scanRetentionTimeDTO.setMzIntListAsString( rs.getString( "spectrum_data" ) );
//			}
//			
//	CREATE TABLE scan_retention_time (
//			  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
//			  scan_file_id INT UNSIGNED NOT NULL,
//			  scan_number INT NOT NULL,
//			  precursor_scan_number INT NULL,
//			  scan_level INT NOT NULL,
//			  retention_time DECIMAL(18,9) NOT NULL,
//
//
//
//			
//		} catch ( Exception e ) {
//			
//			log.error( "ERROR: database connection: '" + DBConnectionFactory.CROSSLINKS + "' sql: " + sqlStr, e );
//			
//			throw e;
//			
//		}
//		
//		
//		finally {
//
//			// Always make sure result sets and statements are closed,
//			// and the connection is returned to the pool
//			if (rs != null) {
//				try { rs.close(); } catch (SQLException e) { ; }
//				rs = null;
//			}
//			if (pstmt != null) {
//				try { pstmt.close(); } catch (SQLException e) { ; }
//				pstmt = null;
//			}
//			if (connection != null) {
//				try { connection.close(); } catch (SQLException e) { ; }
//				connection = null;
//			}
//		}
//
//		return scanRetentionTimeDTO;
//	}
//	
	
	

	private final static String getForScanFileIdExcludeScanLevel =  "SELECT *  FROM scan_retention_time WHERE scan_file_id = ? AND scan_level <> ?  " ;
	
	private final static String getForScanFileIdExcludeScanLevelRetentionTimeCutoff =  getForScanFileIdExcludeScanLevel + " AND retention_time < ?  " ;

	/**
	 * @param scanFileId
	 * @param retentionTimeInSecondsCutoff
	 * @param excludeScanLevel - exclude records with this scan level
	 * @return
	 * @throws Exception
	 */
	public static List<ScanRetentionTimeDTO> getForScanFileIdExcludeScanLevel( int scanFileId, Double retentionTimeInSecondsCutoff, int excludeScanLevel ) throws Exception {


		List<ScanRetentionTimeDTO> results = new ArrayList<>();

		// Get our connection to the database.
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;


		String sqlStr = getForScanFileIdExcludeScanLevel;
		
		if ( retentionTimeInSecondsCutoff != null ) {
			
			sqlStr = getForScanFileIdExcludeScanLevelRetentionTimeCutoff;
		}


	
		try {
			

			connection = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );


			pstmt = connection.prepareStatement( sqlStr );

			pstmt.setInt( 1, scanFileId );
			pstmt.setInt( 2, excludeScanLevel );

			if ( retentionTimeInSecondsCutoff != null ) {
				
				pstmt.setDouble( 3, retentionTimeInSecondsCutoff );
			}


			
			rs = pstmt.executeQuery();

			while ( rs.next() ) {

				ScanRetentionTimeDTO scanRetentionTimeDTO = processResult(rs);
				
				results.add(scanRetentionTimeDTO);
			}
			


			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.CROSSLINKS + "' sql: " + sqlStr, e );
			
			throw e;
			
		}
		
		
		finally {

			// Always make sure result sets and statements are closed,
			// and the connection is returned to the pool
			if (rs != null) {
				try { rs.close(); } catch (SQLException e) { ; }
				rs = null;
			}
			if (pstmt != null) {
				try { pstmt.close(); } catch (SQLException e) { ; }
				pstmt = null;
			}
			if (connection != null) {
				try { connection.close(); } catch (SQLException e) { ; }
				connection = null;
			}
		}

		return results;
	}



//	CREATE TABLE scan_retention_time (
//			  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
//			  scan_file_id INT UNSIGNED NOT NULL,
//			  scan_number INT NOT NULL,
//			  precursor_scan_number INT NULL,
//			  scan_level INT NOT NULL,
//			  retention_time DECIMAL(18,9) NOT NULL,

	/**
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private static ScanRetentionTimeDTO processResult(ResultSet rs)
			throws SQLException {
		
		
		ScanRetentionTimeDTO scanRetentionTimeDTO = new ScanRetentionTimeDTO();

		scanRetentionTimeDTO.setId( rs.getInt( "id" ) );
		scanRetentionTimeDTO.setScanFileId( rs.getInt( "scan_file_id" ) );
		scanRetentionTimeDTO.setScanNumber( rs.getInt( "scan_number" ) );
		scanRetentionTimeDTO.setScanLevel( rs.getInt( "scan_level" ) );
		scanRetentionTimeDTO.setPrecursorScanNumber( rs.getInt( "precursor_scan_number" ) );
		scanRetentionTimeDTO.setRetentionTime( rs.getBigDecimal( "retention_time" ) );
		return scanRetentionTimeDTO;
	}
	
	
	
	/**
	 * Are there any records for the scan_file_id
	 * @param scanFileId
	 * @return
	 * @throws Exception
	 */
	public static boolean doesAnyRecordsExistForScanFileId( int scanFileId ) throws Exception {


		boolean anyRecordsExist = false;

		// Get our connection to the database.
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		// Our SQL statement
		final String sqlStr =  "SELECT id "
			+ " FROM scan_retention_time "
			+ " WHERE scan_file_id = ? LIMIT 1,1 " ;


//		CREATE TABLE scan_retention_time (
//				  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
//				  scan_file_id INT UNSIGNED NOT NULL,
//				  scan_number INT NOT NULL,
//				  precursor_scan_number INT NULL,
//				  scan_level INT NOT NULL,
//				  retention_time DECIMAL(18,9) NOT NULL,
	
		try {
			

			connection = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );


			pstmt = connection.prepareStatement( sqlStr );

			pstmt.setInt( 1, scanFileId );

			// Our results
			rs = pstmt.executeQuery();

			if ( rs.next() ) {

				anyRecordsExist = true;
			}
			



			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.CROSSLINKS + "' sql: " + sqlStr, e );
			
			throw e;
			
		}
		
		
		finally {

			// Always make sure result sets and statements are closed,
			// and the connection is returned to the pool
			if (rs != null) {
				try { rs.close(); } catch (SQLException e) { ; }
				rs = null;
			}
			if (pstmt != null) {
				try { pstmt.close(); } catch (SQLException e) { ; }
				pstmt = null;
			}
			if (connection != null) {
				try { connection.close(); } catch (SQLException e) { ; }
				connection = null;
			}
		}

		return anyRecordsExist;
	}
	
	


//	CREATE TABLE scan_retention_time (
//			  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
//			  scan_file_id INT UNSIGNED NOT NULL,
//			  scan_number INT NOT NULL,
//			  precursor_scan_number INT NULL,
//			  scan_level INT NOT NULL,
//			  retention_time DECIMAL(18,9) NOT NULL,



	private static String insertSQL = "INSERT INTO scan_retention_time "
			+ "( scan_file_id, scan_number, precursor_scan_number, scan_level, retention_time )"
			+ " VALUES ( ?, ?, ?, ?, ? )";
	
	
	/**
	 * @param item
	 * @return
	 * @throws Exception
	 */
	public static int save( ScanRetentionTimeDTO item ) throws Exception {

		
		Connection connection = null;
		
		PreparedStatement pstmtSave = null;
		
		PreparedStatement pstmtSaveSpectrumData = null;

		ResultSet rsGenKeys = null;

		try {

			connection = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			
			pstmtSave = connection.prepareStatement( insertSQL );

			int counter = 0;

			counter++;
			pstmtSave.setInt( counter, item.getScanFileId() );
			counter++;
			pstmtSave.setInt( counter, item.getScanNumber() );
			counter++;
			pstmtSave.setInt( counter, item.getPrecursorScanNumber() );
			counter++;
			pstmtSave.setInt( counter, item.getScanLevel() );
			counter++;
			pstmtSave.setBigDecimal( counter, item.getRetentionTime() );

			
			int rowsUpdated = pstmtSave.executeUpdate();

			if ( rowsUpdated == 0 ) {

			}

			rsGenKeys = pstmtSave.getGeneratedKeys();

			if ( rsGenKeys.next() ) {

				item.setId( rsGenKeys.getInt( 1 ) );
			}


		} catch (Exception sqlEx) {
			
			String msg = "save:Exception '" + sqlEx.toString() + ".\nSQL = " + insertSQL;


			System.out.println( msg );
			System.err.println( msg );
			
			log.error( msg , sqlEx);

			
			throw sqlEx;

		} finally {

			if (rsGenKeys != null) {
				try {
					rsGenKeys.close();
				} catch (Exception ex) {
					// ignore
				}
			}

			if (pstmtSave != null) {
				try {
					pstmtSave.close();
				} catch (Exception ex) {
					// ignore
				}
			}
			if (pstmtSaveSpectrumData != null) {
				try {
					pstmtSaveSpectrumData.close();
				} catch (Exception ex) {
					// ignore
				}
			}
			if (connection != null) {
				
				try {
					connection.close();
				} catch (Exception ex) {
					// ignore
				}
			}

		}

		return item.getId();


	}

}
