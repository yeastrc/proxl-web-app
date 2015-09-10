package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * Table kojak_file_scan_file - maps kojak_file to scan_file
 *
 */
public class KojakFileScanFileDAO {

	private static final Logger log = Logger.getLogger(KojakFileScanFileDAO.class);



	private KojakFileScanFileDAO() { }
	public static KojakFileScanFileDAO getInstance() { return new KojakFileScanFileDAO(); }

	/**
	 * @param kojakFileId
	 * @return
	 * @throws Exception
	 */
	public Integer getScanFileIdFromKojakFileId( int kojakFileId ) throws Exception {
		
		
		Integer result = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		


		String sql = "SELECT scan_file_id FROM kojak_file_scan_file WHERE kojak_file_id = ?";
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );


//	CREATE TABLE kojak_file_scan_file (
//	  kojak_file_id INT(10) UNSIGNED NOT NULL,
//	  scan_file_id INT(10) UNSIGNED NOT NULL,



			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, kojakFileId );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				result = rs.getInt( "scan_file_id" );
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
	 * @param scanFileId
	 * @return
	 * @throws Exception
	 */
	public Integer getKojakFileIdFromScanFileId( int scanFileId ) throws Exception {
		
		
		Integer result = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		

		String sql = "SELECT kojak_file_id FROM kojak_file_scan_file WHERE scan_file_id = ?";
		
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );


//	CREATE TABLE kojak_file_scan_file (
//	  kojak_file_id INT(10) UNSIGNED NOT NULL,
//	  scan_file_id INT(10) UNSIGNED NOT NULL,




			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, scanFileId );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				result = rs.getInt( "kojak_file_id" );
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



	
	
	private static String insertSQL = "INSERT INTO kojak_file_scan_file ( kojak_file_id, scan_file_id )"
		+ " VALUES ( ?, ? )";

//	CREATE TABLE kojak_file_scan_file (
//	  kojak_file_id INT(10) UNSIGNED NOT NULL,
//	  scan_file_id INT(10) UNSIGNED NOT NULL,




	/**
	 * @param kojak_file_id
	 * @param scan_file_id
	 * @return
	 * @throws Throwable
	 */
	public void save( int kojak_file_id, int scan_file_id ) throws Exception {

		Connection connection = null;

		PreparedStatement pstmt = null;

//		ResultSet rsGenKeys = null;

		try {


			connection = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			pstmt = connection.prepareStatement( insertSQL );

			int counter = 0;

			counter++;
			pstmt.setInt( counter, kojak_file_id );
			counter++;
			pstmt.setInt( counter, scan_file_id );

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
