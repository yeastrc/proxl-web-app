package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.ScanFileHeaderDTO;


/**
 * Table scan_file_header
 *
 */
public class ScanFileHeaderDAO {
	
	private static final Logger log = LoggerFactory.getLogger( ScanFileHeaderDAO.class);

	

	/**
	 * @param item
	 * @return
	 * @throws Throwable
	 */
	public static int save(ScanFileHeaderDTO item ) throws Exception {

		Connection connection = null;

		try {


			connection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			return save( item, connection );
			
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

	private static String insertSQL = "INSERT INTO scan_file_header (scan_file_id, header, value " +
	" ) VALUES ( ?, ?, ? )";
	//id INT UNSIGNED NOT NULL AUTO_INCREMENT ,
	//ms2_file_id INT UNSIGNED NOT NULL ,
	//header VARCHAR(255) NOT NULL ,
	//value TEXT NULL ,
	
	
	public static int save(ScanFileHeaderDTO item, Connection connection ) throws Exception {

//		Connection connection = null;

		PreparedStatement pstmt = null;

		ResultSet rsGenKeys = null;

		try {

//			connection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			pstmt = connection.prepareStatement( insertSQL, Statement.RETURN_GENERATED_KEYS );

			int counter = 0;

			counter++;
			pstmt.setInt( counter, item.getScanFileId() );
			counter++;
			pstmt.setString( counter, item.getHeader() );
			counter++;
			pstmt.setString( counter, item.getValue() );

			int rowsUpdated = pstmt.executeUpdate();

			if ( rowsUpdated == 0 ) {

			}
			
			rsGenKeys = pstmt.getGeneratedKeys();

			if ( rsGenKeys.next() ) {

				item.setId( rsGenKeys.getInt( 1 ) );
			}

			

		} catch (Exception sqlEx) {
			log.error("save:Exception '" + sqlEx.toString() + ".\nSQL = " + insertSQL , sqlEx);
			throw sqlEx;

		} finally {

			if (rsGenKeys != null) {
				try {
					rsGenKeys.close();
				} catch (Exception ex) {
					// ignore
				}
			}
			
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
		
		return item.getId();
		
		
	}
}
