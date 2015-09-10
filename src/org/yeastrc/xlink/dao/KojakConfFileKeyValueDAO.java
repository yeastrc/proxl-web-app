package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * Table kojak_conf_file_key_value
 *
 */
public class KojakConfFileKeyValueDAO {

	private static final Logger log = Logger.getLogger(KojakConfFileKeyValueDAO.class);

	private KojakConfFileKeyValueDAO() { }
	public static KojakConfFileKeyValueDAO getInstance() { return new KojakConfFileKeyValueDAO(); }
	
	
	

	private static String insertSQL = "INSERT INTO kojak_conf_file_key_value " 
			+ " ( kojak_conf_file_id, kojak_conf_file_key, value )"
			+ " VALUES ( ?, ?, ? )";

//	CREATE TABLE kojak_conf_file_key_value (
//			  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
//			  kojak_conf_file_id INT(10) UNSIGNED NOT NULL,
//			  kojak_conf_file_key VARCHAR(255) NOT NULL,
//			  value VARCHAR(2000) NULL DEFAULT NULL,

	/**
	 * @param item
	 * @return
	 * @throws Throwable
	 */
	public void save( int kojak_conf_file_id, String kojak_conf_file_key , String value, Connection connection ) throws Exception {

//		Connection connection = null;

		PreparedStatement pstmt = null;

		ResultSet rsGenKeys = null;

		try {


//			connection = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			pstmt = connection.prepareStatement( insertSQL );

			int counter = 0;

			counter++;
			pstmt.setInt( counter, kojak_conf_file_id );
			counter++;
			pstmt.setString( counter, kojak_conf_file_key );
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

//			if (connection != null) {
//				try {
//					connection.close();
//				} catch (Exception ex) {
//					// ignore
//				}
//			}
		}

//		return item.getId();


	}
}
