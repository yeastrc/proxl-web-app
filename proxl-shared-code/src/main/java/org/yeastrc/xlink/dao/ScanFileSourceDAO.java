package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.ScanFileSourceDTO;

/**
 * Table scan_file_source
 *
 */
public class ScanFileSourceDAO {
	
	private static final Logger log = LoggerFactory.getLogger( ScanFileSourceDAO.class);
	
	/**
	 * @param item
	 * @return
	 * @throws Throwable
	 */
	public static void save(ScanFileSourceDTO item ) throws Exception {
		Connection connection = null;
		try {
			connection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			save( item, connection );
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception ex) {
					// ignore
				}
			}
		}
	}
	private static String insertSQL = "INSERT INTO scan_file_source "
			+ " (scan_file_id, path, canonical_filename_w_path_on_submit_machine, absolute_filename_w_path_on_submit_machine) "
			+ " VALUES ( ?, ?, ?, ?  )";

	/**
	 * @param item
	 * @param connection
	 * @return
	 * @throws Exception
	 */
	public static void save( ScanFileSourceDTO item, Connection connection ) throws Exception {
//		Connection connection = null;
		PreparedStatement pstmt = null;
//		ResultSet rsGenKeys = null;
		try {
//			connection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = connection.prepareStatement( insertSQL, Statement.RETURN_GENERATED_KEYS );
			int counter = 0;
			counter++;
			pstmt.setInt( counter, item.getScanFileId() );
			counter++;
			pstmt.setString( counter, item.getPath() );
			counter++;
			pstmt.setString( counter, item.getCanonicalFilename_W_Path_OnSubmitMachine() );
			counter++;
			pstmt.setString( counter, item.getAbsoluteFilename_W_Path_OnSubmitMachine() );
	
			int rowsUpdated = pstmt.executeUpdate();
			if ( rowsUpdated == 0 ) {
			}
//			rsGenKeys = pstmt.getGeneratedKeys();
//			if ( rsGenKeys.next() ) {
//				item.setId( rsGenKeys.getInt( 1 ) );
//			}
		} catch (Exception sqlEx) {
			log.error("save:Exception '" + sqlEx.toString() + ".\nSQL = " + insertSQL , sqlEx);
			throw sqlEx;
		} finally {
//			if (rsGenKeys != null) {
//				try {
//					rsGenKeys.close();
//				} catch (Exception ex) {
//					// ignore
//				}
//			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					// ignore
				}
			}
//			if (connection != null) {
//				try {
//					connection.close();
//				} catch (SQLException ex) {
//					// ignore
//				}
//			}
		}
	}
}
