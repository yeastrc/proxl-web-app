package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
			connection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
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
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sqlStr, e );
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
	
//	/**
//	 * Are there any records for the scan_file_id
//	 * @param scanFileId
//	 * @return
//	 * @throws Exception
//	 */
//	public static boolean doesAnyRecordsExistForScanFileId( int scanFileId ) throws Exception {
//		
//		boolean anyRecordsExist = false;
//		// Get our connection to the database.
//		Connection connection = null;
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//		
//		// Our SQL statement
//		final String sqlStr =  "SELECT id "
//			+ " FROM scan_retention_time "
//			+ " WHERE scan_file_id = ? LIMIT 1,1 " ;
//		
//		try {
//			connection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
//			pstmt = connection.prepareStatement( sqlStr );
//			pstmt.setInt( 1, scanFileId );
//			// Our results
//			rs = pstmt.executeQuery();
//			if ( rs.next() ) {
//				anyRecordsExist = true;
//			}
//		} catch ( Exception e ) {
//			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sqlStr, e );
//			throw e;
//		}
//		finally {
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
//		return anyRecordsExist;
//	}

	private static String insertSQL = "INSERT IGNORE INTO scan_retention_time "
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
			connection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmtSave = connection.prepareStatement( insertSQL, Statement.RETURN_GENERATED_KEYS );
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
			String msg = "save:Exception '" + sqlEx.toString() 
					+ ".  Data being inserted: " + item
					+ ".\nSQL = " + insertSQL;
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
