package org.yeastrc.auth.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import org.apache.log4j.Logger;
import org.yeastrc.auth.db.AuthLibraryDBConnectionFactory;
import org.yeastrc.auth.dto.AuthForgotPasswordTrackingDTO;

/**
 * DAO for auth_forgot_password_tracking table
 *
 */
public class AuthForgotPasswordTrackingDAO {

	private static final Logger log = Logger.getLogger(AuthForgotPasswordTrackingDAO.class);
	
	//  private constructor
	private AuthForgotPasswordTrackingDAO() { }
	/**
	 * @return newly created instance
	 */
	public static AuthForgotPasswordTrackingDAO getInstance() { 
		return new AuthForgotPasswordTrackingDAO(); 
	}
	
	/**
	 * @param forgotPasswordTrackingCode
	 * @return null if not found
	 * @throws Exception
	 */
	public AuthForgotPasswordTrackingDTO getForForgotPasswordTrackingCode( String forgotPasswordTrackingCode ) throws Exception {
		AuthForgotPasswordTrackingDTO returnItem = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = 
				"SELECT * FROM auth_forgot_password_tracking WHERE forgot_password_tracking_code = ? ";
		try {
			conn = AuthLibraryDBConnectionFactory.getConnection(  );
			pstmt = conn.prepareStatement( sql );
			pstmt.setString( 1, forgotPasswordTrackingCode );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				returnItem = new AuthForgotPasswordTrackingDTO();
				returnItem.setId( rs.getInt( "id" ) );
				returnItem.setUserId( rs.getInt( "auth_user_id" ) );
				returnItem.setCreateDate( rs.getDate( "create_date" ) );
				returnItem.setUsedDate( rs.getDate( "used_date" ) );
				returnItem.setForgotPasswordTrackingCode( rs.getString( "forgot_password_tracking_code" ) );
				returnItem.setSubmitIP( rs.getString( "submit_ip" ) );
				returnItem.setUseIP( rs.getString( "use_ip" ) );
				returnItem.setCodeReplacedByNewer( rs.getBoolean( "code_replaced_by_newer" ));
			}
		} catch ( Exception e ) {
			String msg = "Failed to select AuthForgotPasswordTrackingDTO, forgotPasswordTrackingCode: " + forgotPasswordTrackingCode
					 + ", sql: " + sql;
			log.error( msg, e );
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
		return returnItem;
	}
	
	/**
	 * @param item
	 * @throws Exception
	 */
	public void save( AuthForgotPasswordTrackingDTO item ) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "INSERT INTO auth_forgot_password_tracking "
				+ " ( auth_user_id, forgot_password_tracking_code, submit_ip, create_date ) " 
				+ " VALUES ( ?, ?, ?, NOW() )";
		try {
			conn = AuthLibraryDBConnectionFactory.getConnection(  );
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			int counter = 0;
			counter++;
			pstmt.setInt( counter, item.getUserId() );
			counter++;
			pstmt.setString( counter, item.getForgotPasswordTrackingCode() );
			counter++;
			pstmt.setString( counter, item.getSubmitIP() );
			pstmt.executeUpdate();
			rs = pstmt.getGeneratedKeys();
			if( rs.next() ) {
				item.setId( rs.getInt( 1 ) );
			} else {
				throw new Exception( "Failed to insert AuthForgotPasswordTrackingDTO" );
			}
		} catch ( Exception e ) {
			String msg = "Failed to insert AuthForgotPasswordTrackingDTO, sql: " + sql;
			log.error( msg, e );
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
	}
	
	/**
	 * Update used_date = NOW() , useIP = ?
	 * @param id
	 * @param useIP
	 * @throws Exception
	 */
	public void updateUsedDateUseIP( int id, String useIP ) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "UPDATE auth_forgot_password_tracking SET use_ip = ?, used_date = NOW() WHERE id = ?";
		try {
			conn = AuthLibraryDBConnectionFactory.getConnection(  );
			pstmt = conn.prepareStatement( sql );
			int counter = 0;
			counter++;
			pstmt.setString( counter, useIP );
			counter++;
			pstmt.setInt( counter, id );
			pstmt.executeUpdate();
//			int rowsUpdated = pstmt.executeUpdate();
		} catch ( Exception e ) {
			String msg = "Failed to update used_date, sql: " + sql;
			log.error( msg, e );
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
	}
	
	/**
	 * Update code_replaced_by_newer = ?
	 * @param id - ID to use in id < ? comparison
	 * @param codeReplacedByNewer
	 * @throws Exception
	 */
	public void updateCodeReplacedByNewer( int id, boolean codeReplacedByNewer ) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "UPDATE auth_forgot_password_tracking SET code_replaced_by_newer = ? WHERE id < ?";
		try {
			conn = AuthLibraryDBConnectionFactory.getConnection(  );
			pstmt = conn.prepareStatement( sql );
			int counter = 0;
			counter++;
			pstmt.setBoolean( counter, codeReplacedByNewer );
			counter++;
			pstmt.setInt( counter, id );
			pstmt.executeUpdate();
		} catch ( Exception e ) {
			String msg = "Failed to update used_date, sql: " + sql;
			log.error( msg, e );
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
	}
}
