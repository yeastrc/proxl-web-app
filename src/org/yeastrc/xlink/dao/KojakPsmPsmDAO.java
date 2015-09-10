package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * Table kojakpsm_psm - maps kojak_psm to psm
 *
 */
public class KojakPsmPsmDAO {

	private static final Logger log = Logger.getLogger(KojakPsmPsmDAO.class);



	private KojakPsmPsmDAO() { }
	public static KojakPsmPsmDAO getInstance() { return new KojakPsmPsmDAO(); }

	/**
	 * @param kojakPsmId
	 * @return
	 * @throws Exception
	 */
	public Integer getPsmIdFromKojakPsmId( int kojakPsmId ) throws Exception {
		
		
		Integer result = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;


		String sql = "SELECT psm_id FROM kojakpsm_psm WHERE kojakpsm_id = ?";
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

//	CREATE TABLE kojakpsm_psm (
//			  kojakpsm_id INT UNSIGNED NOT NULL,
//			  psm_id INT UNSIGNED NOT NULL,



			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, kojakPsmId );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				result = rs.getInt( "psm_id" );
			}

		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.CROSSLINKS + "' sql: " + sql, e );
			
			throw e;
			
		} finally {
			
			// be sure database handles are closed
			if( rs != null ) {
				try { rs.close(); } catch( Throwable t ) { ; }
				rs = null;
			}
			
			if( pstmt != null ) {
				try { pstmt.close(); } catch( Throwable t ) { ; }
				pstmt = null;
			}
			
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
			
		}
		
		
		return result;
	}

	

	/**
	 * @param psmId
	 * @return
	 * @throws Exception
	 */
	public Integer getKojakPsmIdFromPsmId( int psmId ) throws Exception {
		
		
		Integer result = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT kojakpsm_id FROM kojakpsm_psm WHERE psm_id = ?";
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );


//			CREATE TABLE kojakpsm_psm (
//			  kojakpsm_id INT UNSIGNED NOT NULL,
//			  psm_id INT UNSIGNED NOT NULL,

			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, psmId );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				result = rs.getInt( "kojakpsm_id" );
			}

		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.CROSSLINKS + "' sql: " + sql, e );
			
			throw e;
			
		} finally {
			
			// be sure database handles are closed
			if( rs != null ) {
				try { rs.close(); } catch( Throwable t ) { ; }
				rs = null;
			}
			
			if( pstmt != null ) {
				try { pstmt.close(); } catch( Throwable t ) { ; }
				pstmt = null;
			}
			
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
			
		}
		
		
		return result;
	}



	
	
	private static String insertSQL = "INSERT INTO kojakpsm_psm ( kojakpsm_id, psm_id )"
		+ " VALUES ( ?, ? )";

//	CREATE TABLE kojakpsm_psm (
//	  kojakpsm_id INT UNSIGNED NOT NULL,
//	  psm_id INT UNSIGNED NOT NULL,





	/**
	 * @param kojakpsm_id
	 * @param psm_id
	 * @return
	 * @throws Throwable
	 */
	public void save( int kojakpsm_id, int psm_id ) throws Exception {

		Connection connection = null;

		PreparedStatement pstmt = null;

//		ResultSet rsGenKeys = null;

		try {


			connection = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			pstmt = connection.prepareStatement( insertSQL );

			int counter = 0;

			counter++;
			pstmt.setInt( counter, kojakpsm_id );
			counter++;
			pstmt.setInt( counter, psm_id );

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

			if (connection != null) {
				try {
					connection.close();
				} catch (Exception ex) {
					// ignore
				}
			}
		}

	}
}
