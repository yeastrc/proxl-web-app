package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.XquestDefsFileDTO;

/**
 * Table xquest_defs_file
 *
 */
public class XquestDefsFileDAO {

	private static final Logger log = Logger.getLogger(XquestDefsFileDAO.class);



	private XquestDefsFileDAO() { }
	public static XquestDefsFileDAO getInstance() { return new XquestDefsFileDAO(); }

	

	/**
	 * @param item
	 * @return
	 * @throws Throwable
	 */
	public int save(XquestDefsFileDTO item ) throws Exception {

		Connection connection = null;

		try {


			connection = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
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
	
	private static String insertSQL = "INSERT INTO xquest_defs_file ( xquest_file_id, filename, path )"
		+ " VALUES ( ?, ?, ? )";

//	CREATE TABLE xquest_defs_file (
//	  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
//	  xquest_file_id INT(10) UNSIGNED NOT NULL,
//	  filename VARCHAR(255) NOT NULL,
//	  path VARCHAR(2000) NOT NULL,


	/**
	 * @param item
	 * @return
	 * @throws Throwable
	 */
	public int save(XquestDefsFileDTO item, Connection connection ) throws Exception {

//		Connection connection = null;

		PreparedStatement pstmt = null;

		ResultSet rsGenKeys = null;

		try {


//			connection = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			pstmt = connection.prepareStatement( insertSQL );

			int counter = 0;

			counter++;
			pstmt.setInt( counter, item.getXquestFileId() );
			counter++;
			pstmt.setString( counter, item.getFilename() );
			counter++;
			pstmt.setString( counter, item.getPath() );

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
//				} catch (Exception ex) {
//					// ignore
//				}
//			}
		}

		return item.getId();


	}
}
