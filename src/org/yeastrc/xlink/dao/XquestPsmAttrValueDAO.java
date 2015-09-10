package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * Table xquest_psm_spectrum_search_attr_value and 
 *
 */
public class XquestPsmAttrValueDAO {

	private static final Logger log = Logger.getLogger(XquestPsmAttrValueDAO.class);



	private XquestPsmAttrValueDAO() { }
	public static XquestPsmAttrValueDAO getInstance() { return new XquestPsmAttrValueDAO(); }

	/**
	 * @param xquest_psm_id
	 * @param attr
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public int save_xquest_psm_spectrum_search_attr_value( int xquest_psm_id, String attr, String value ) throws Exception {

		Connection connection = null;

		try {


			connection = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			return save_xquest_psm_spectrum_search_attr_value( xquest_psm_id, attr, value, connection );
			
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
	
	private static String insert_xquest_psm_spectrum_search_attr_value_SQL = "INSERT INTO xquest_psm_spectrum_search_attr_value "
			+ "( xquest_psm_id, attr, value ) "
			+ " VALUES ( ?, ?, ? ) ";

//	CREATE TABLE xquest_psm_spectrum_search_attr_value (
//			  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
//			  xquest_psm_id INT UNSIGNED NULL,
//			  attr VARCHAR(200) NULL,
//			  value VARCHAR(2000) NULL,


	/**
	 * @param xquest_psm_id
	 * @param attr
	 * @param value
	 * @param connection
	 * @return
	 * @throws Exception
	 */
	public int save_xquest_psm_spectrum_search_attr_value(int xquest_psm_id, String attr, String value, Connection connection ) throws Exception {

		int id = 0;
		
//		Connection connection = null;

		PreparedStatement pstmt = null;

		ResultSet rsGenKeys = null;

		try {


//			connection = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			pstmt = connection.prepareStatement( insert_xquest_psm_spectrum_search_attr_value_SQL );

			int counter = 0;

			counter++;
			pstmt.setInt( counter, xquest_psm_id );

			counter++;
			pstmt.setString( counter, attr );

			counter++;
			pstmt.setString( counter, value );

			int rowsUpdated = pstmt.executeUpdate();

			if ( rowsUpdated == 0 ) {

			}

			rsGenKeys = pstmt.getGeneratedKeys();

			if ( rsGenKeys.next() ) {

				id = rsGenKeys.getInt( 1 );
			}



		} catch (Exception sqlEx) {
			log.error("save:Exception '" + sqlEx.toString() + ".\nSQL = " + insert_xquest_psm_spectrum_search_attr_value_SQL , sqlEx);
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

		return id;


	}
	
	
	


	/**
	 * @param xquest_psm_id
	 * @param attr
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public int save_xquest_psm_search_hit_attr_value( int xquest_psm_id, String attr, String value ) throws Exception {

		Connection connection = null;

		try {


			connection = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			return save_xquest_psm_search_hit_attr_value( xquest_psm_id, attr, value, connection );

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

	private static String insert_xquest_psm_search_hit_attr_value_SQL = "INSERT INTO xquest_psm_search_hit_attr_value "
			+ "( xquest_psm_id, attr, value ) "
			+ " VALUES ( ?, ?, ? ) ";

//		CREATE TABLE xquest_psm_search_hit_attr_value (
//				  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
//				  xquest_psm_id INT UNSIGNED NULL,
//				  attr VARCHAR(200) NULL,
//				  value VARCHAR(2000) NULL,


	/**
	 * @param xquest_psm_id
	 * @param attr
	 * @param value
	 * @param connection
	 * @return
	 * @throws Exception
	 */
	public int save_xquest_psm_search_hit_attr_value(int xquest_psm_id, String attr, String value, Connection connection ) throws Exception {

		int id = 0;

		//				Connection connection = null;

		PreparedStatement pstmt = null;

		ResultSet rsGenKeys = null;

		try {


			//					connection = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			pstmt = connection.prepareStatement( insert_xquest_psm_search_hit_attr_value_SQL );

			int counter = 0;

			counter++;
			pstmt.setInt( counter, xquest_psm_id );

			counter++;
			pstmt.setString( counter, attr );

			counter++;
			pstmt.setString( counter, value );

			int rowsUpdated = pstmt.executeUpdate();

			if ( rowsUpdated == 0 ) {

			}

			rsGenKeys = pstmt.getGeneratedKeys();

			if ( rsGenKeys.next() ) {

				id = rsGenKeys.getInt( 1 );
			}



		} catch (Exception sqlEx) {
			log.error("save:Exception '" + sqlEx.toString() + ".\nSQL = " + insert_xquest_psm_search_hit_attr_value_SQL , sqlEx);
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

			//					if (connection != null) {
			//						try {
			//							connection.close();
			//						} catch (Exception ex) {
			//							// ignore
			//						}
			//					}
		}

		return id;


	}
			
			
}
