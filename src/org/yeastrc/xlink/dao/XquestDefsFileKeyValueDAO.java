package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * Table xquest_defs_file_key_value
 *
 */
public class XquestDefsFileKeyValueDAO {

	private static final Logger log = Logger.getLogger(XquestDefsFileKeyValueDAO.class);

	private XquestDefsFileKeyValueDAO() { }
	public static XquestDefsFileKeyValueDAO getInstance() { return new XquestDefsFileKeyValueDAO(); }
	
	
	

	private static String insertSQL = "INSERT INTO xquest_defs_file_key_value " 
			+ " ( xquest_defs_file_id, xquest_defs_file_key, value )"
			+ " VALUES ( ?, ?, ? )";

//	CREATE TABLE xquest_defs_file_key_value (
//			  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
//			  xquest_defs_file_id INT(10) UNSIGNED NOT NULL,
//			  xquest_defs_file_key VARCHAR(255) NOT NULL,
//			  value VARCHAR(2000) NULL DEFAULT NULL,


	/**
	 * @param xquest_defs_file_id
	 * @param xquest_defs_file_key
	 * @param value
	 * @throws Exception
	 */
	public void save( int xquest_defs_file_id, String xquest_defs_file_key , String value ) throws Exception {

		Connection connection = null;

		PreparedStatement pstmt = null;

		ResultSet rsGenKeys = null;

		try {


			connection = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			pstmt = connection.prepareStatement( insertSQL );

			int counter = 0;

			counter++;
			pstmt.setInt( counter, xquest_defs_file_id );
			counter++;
			pstmt.setString( counter, xquest_defs_file_key );
			counter++;
			pstmt.setString( counter, value );

			int rowsUpdated = pstmt.executeUpdate();

			if ( rowsUpdated == 0 ) {

			}

//			rsGenKeys = pstmt.getGeneratedKeys();
//
//			if ( rsGenKeys.next() ) {
//
//				item.setId( rsGenKeys.getInt( 1 ) );
//			}



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

			if (connection != null) {
				try {
					connection.close();
				} catch (Exception ex) {
					// ignore
				}
			}
		}

//		return item.getId();


	}

}
