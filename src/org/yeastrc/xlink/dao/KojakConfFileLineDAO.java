package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * Table kojak_conf_file
 *
 */
public class KojakConfFileLineDAO {

	private static final Logger log = Logger.getLogger(KojakConfFileLineDAO.class);

	private KojakConfFileLineDAO() { }
	public static KojakConfFileLineDAO getInstance() { return new KojakConfFileLineDAO(); }
	
	
	

	private static String insertSQL = "INSERT INTO kojak_conf_file_line " 
			+ " ( kojak_conf_file_id, line )"
			+ " VALUES ( ?, ? )";

//	CREATE TABLE kojak_conf_file_line (
//	  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
//	  kojak_conf_file_id INT(10) UNSIGNED NOT NULL,
//	  line VARCHAR(2000) NULL DEFAULT NULL,

	/**
	 * @param item
	 * @return
	 * @throws Throwable
	 */
	public void save( int kojak_file_id, String line, Connection connection ) throws Exception {

//		Connection connection = null;

		PreparedStatement pstmt = null;

		ResultSet rsGenKeys = null;

		try {


//			connection = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			pstmt = connection.prepareStatement( insertSQL );

			int counter = 0;

			counter++;
			pstmt.setInt( counter, kojak_file_id );
			counter++;
			pstmt.setString( counter, line );

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
