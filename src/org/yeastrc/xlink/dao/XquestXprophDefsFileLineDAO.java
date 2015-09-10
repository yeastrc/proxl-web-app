package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * Table xquest_xproph_defs_file_line
 *
 */
public class XquestXprophDefsFileLineDAO {

	private static final Logger log = Logger.getLogger(XquestXprophDefsFileLineDAO.class);

	private XquestXprophDefsFileLineDAO() { }
	public static XquestXprophDefsFileLineDAO getInstance() { return new XquestXprophDefsFileLineDAO(); }
	
	
	

	private static String insertSQL = "INSERT INTO xquest_xproph_defs_file_line " 
			+ " ( xquest_xproph_defs_file_id, line )"
			+ " VALUES ( ?, ? )";

//	CREATE TABLE xquest_xproph_defs_file_line (
//			  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
//			  xquest_xproph_defs_file_id INT(10) UNSIGNED NOT NULL,
//			  line VARCHAR(2000) NULL DEFAULT NULL,

	/**
	 * @param item
	 * @return
	 * @throws Throwable
	 */
	public void save( int xquest_xproph_defs_file_id, String line ) throws Exception {

		Connection connection = null;

		PreparedStatement pstmt = null;

		ResultSet rsGenKeys = null;

		try {


			connection = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			pstmt = connection.prepareStatement( insertSQL );

			int counter = 0;

			counter++;
			pstmt.setInt( counter, xquest_xproph_defs_file_id );
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
