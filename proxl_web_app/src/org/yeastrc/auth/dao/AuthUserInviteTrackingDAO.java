package org.yeastrc.auth.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.yeastrc.auth.db.AuthLibraryDBConnectionFactory;
import org.yeastrc.auth.dto.AuthUserInviteTrackingDTO;

/**
 * DAO for auth_user_invite_tracking table
 *
 */
public class AuthUserInviteTrackingDAO {
	
	private static final Logger log = Logger.getLogger(AuthUserInviteTrackingDAO.class);

	//  private constructor
	private AuthUserInviteTrackingDAO() { }
	
	/**
	 * @return newly created instance
	 */
	public static AuthUserInviteTrackingDAO getInstance() { 
		return new AuthUserInviteTrackingDAO(); 
	}
	
	
	/**
	 * @param inviteTrackingId
	 * @return null if not found
	 * @throws Exception
	 */
	public AuthUserInviteTrackingDTO getForInviteTrackingId( int inviteTrackingId ) throws Exception {
		
		AuthUserInviteTrackingDTO returnItem = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = "SELECT * " 
				+ " FROM auth_user_invite_tracking WHERE id = ? ";
	  
		
		try {
			
			conn = AuthLibraryDBConnectionFactory.getConnection(  );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, inviteTrackingId );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {

				returnItem = populateFromResultSet(rs);
			}
			
		} catch ( Exception e ) {
			
			String msg = "Failed to select AuthUserInviteTrackingDTO, inviteTrackingId: " + inviteTrackingId
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
	 * @param inviteTrackingCode
	 * @return null if not found
	 * @throws Exception
	 */
	public AuthUserInviteTrackingDTO getForInviteTrackingCode( String inviteTrackingCode ) throws Exception {
		
		AuthUserInviteTrackingDTO returnItem = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = "SELECT * " 
				+ " FROM auth_user_invite_tracking WHERE invite_tracking_code = ? ";
	  
		
		try {
			
			conn = AuthLibraryDBConnectionFactory.getConnection(  );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setString( 1, inviteTrackingCode );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {

				returnItem = populateFromResultSet(rs);
			}
			
		} catch ( Exception e ) {
			
			String msg = "Failed to select AuthUserInviteTrackingDTO, inviteTrackingCode: " + inviteTrackingCode
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
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private AuthUserInviteTrackingDTO populateFromResultSet(ResultSet rs)
			throws SQLException {
		
		

//		CREATE TABLE auth_user_invite_tracking (
//		  id int(10) unsigned NOT NULL AUTO_INCREMENT,
//		  submitting_auth_user_id int(10) unsigned NOT NULL,
//		  submit_ip varchar(255) NOT NULL,
//		  invite_tracking_code varchar(255) NOT NULL,
//		  invited_user_email varchar(255) NOT NULL,
//		  invited_user_access_level smallint(6) NOT NULL,
//		  invited_shared_object_id int(10) unsigned DEFAULT NULL,
//		  invite_create_date datetime NOT NULL,
//		  invite_used tinyint(4) DEFAULT NULL,
//		  invite_used_date datetime DEFAULT NULL,
//		  invite_used_auth_user_id int(10) unsigned DEFAULT NULL,
//		  use_ip varchar(255) DEFAULT NULL,
//		  code_replaced_by_newer tinyint(4) DEFAULT NULL,
//		  invite_revoked tinyint(4) DEFAULT NULL,
//		  revoking_auth_user_id int(10) unsigned DEFAULT NULL,
//		  revoked_date datetime DEFAULT NULL,
			
		
		AuthUserInviteTrackingDTO returnItem;
		returnItem = new AuthUserInviteTrackingDTO();

		returnItem.setId( rs.getInt( "id" ) );
		returnItem.setSubmittingAuthUserId( rs.getInt( "submitting_auth_user_id" ) );
		
		returnItem.setInvitedUserEmail( rs.getString( "invited_user_email" ) );
		returnItem.setInvitedUserAccessLevel( rs.getInt( "invited_user_access_level" ) );
		
		int invitedSharedObjectId = rs.getInt( "invited_shared_object_id" );
		
		if ( rs.wasNull() ) {
			
			returnItem.setInvitedSharedObjectId(null);
		} else {
			
			returnItem.setInvitedSharedObjectId( invitedSharedObjectId );
		}
		
		returnItem.setInviteCreateDate( rs.getDate( "invite_create_date" ) );
		
		returnItem.setInviteUsed( rs.getBoolean( "invite_used" ) );
		returnItem.setInviteUsedDate( rs.getDate( "invite_used_date" ) );
		
		returnItem.setInviteTrackingCode( rs.getString( "invite_tracking_code" ) );
		
		returnItem.setSubmitIP( rs.getString( "submit_ip" ) );
		returnItem.setUseIP( rs.getString( "use_ip" ) );
		returnItem.setCodeReplacedByNewer( rs.getBoolean( "code_replaced_by_newer" ) );
		
		returnItem.setInviteRevoked( rs.getBoolean( "invite_revoked" ) );
		
		int revokingAuthUserId = rs.getInt( "revoking_auth_user_id" );
		
		if ( rs.wasNull() ) {
			
			returnItem.setRevokingAuthUserId(null);
		} else {
			
			returnItem.setRevokingAuthUserId( revokingAuthUserId );
		}
		
		returnItem.setRevokedDate( rs.getDate( "revoked_date" ) );
		return returnItem;
	}
	

	/**
	 * @param item
	 * @throws Exception
	 */
	public void save( AuthUserInviteTrackingDTO item ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = 
				"INSERT INTO auth_user_invite_tracking " 
				+ " ( submitting_auth_user_id, invited_user_email, invited_user_access_level, invited_shared_object_id, " 
				+ "   invite_tracking_code, submit_ip, code_replaced_by_newer, invite_create_date ) "
				+ " VALUES ( ?, ?, ?, ?, ?, ?, ?, NOW() )";
				

//		CREATE TABLE auth_user_invite_tracking (
//		  id int(10) unsigned NOT NULL AUTO_INCREMENT,
//		  submitting_auth_user_id int(10) unsigned NOT NULL,
//		  submit_ip varchar(255) NOT NULL,
//		  invite_tracking_code varchar(255) NOT NULL,
//		  invited_user_email varchar(255) NOT NULL,
//		  invited_user_access_level smallint(6) NOT NULL,
//		  invited_shared_object_id int(10) unsigned DEFAULT NULL,
//		  invite_create_date datetime NOT NULL,
//		  invite_used tinyint(4) DEFAULT NULL,
//		  invite_used_date datetime DEFAULT NULL,
//		  invite_used_auth_user_id int(10) unsigned DEFAULT NULL,
//		  use_ip varchar(255) DEFAULT NULL,
//		  code_replaced_by_newer tinyint(4) DEFAULT NULL,
//		  invite_revoked tinyint(4) DEFAULT NULL,
//		  revoking_auth_user_id int(10) unsigned DEFAULT NULL,
//		  revoked_date datetime DEFAULT NULL,

		  			  
		
		try {
			
			conn = AuthLibraryDBConnectionFactory.getConnection(  );

			
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, item.getSubmittingAuthUserId() );

			counter++;
			pstmt.setString( counter, item.getInvitedUserEmail() );
			counter++;
			pstmt.setInt( counter, item.getInvitedUserAccessLevel() );

			counter++;
			if ( item.getInvitedSharedObjectId() != null ) {
				pstmt.setInt( counter, item.getInvitedSharedObjectId() );
			} else {
				pstmt.setNull( counter, java.sql.Types.INTEGER );
			}

			counter++;
			pstmt.setString( counter, item.getInviteTrackingCode() );

			counter++;
			pstmt.setString( counter, item.getSubmitIP() );


			counter++;
			pstmt.setBoolean( counter, item.isCodeReplacedByNewer() );
			
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();

			if( rs.next() ) {
				item.setId( rs.getInt( 1 ) );
			} else {
				throw new Exception( "Failed to insert AuthUserInviteTrackingDTO" );
			}
			
		} catch ( Exception e ) {
			
			String msg = "Failed to insert AuthUserInviteTrackingDTO, sql: " + sql;
			
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
	 * @param useAuthUserId
	 * @param useIP
	 * @throws Exception
	 */
	public void updateUsedInviteFields( int id, int useAuthUserId, String useIP ) throws Exception {
		
		Connection dbConnection = null;

		try {
			
			dbConnection = AuthLibraryDBConnectionFactory.getConnection(  );

			updateUsedInviteFields( id, useAuthUserId, useIP, dbConnection );

		} finally {
			
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
			
		}
	}
	



	/**
	 * Update used_date = NOW() , useIP = ?
	 * @param id
	 * @param useAuthUserId
	 * @param useIP
	 * @throws Exception
	 */
	public void updateUsedInviteFields( int id, int useAuthUserId, String useIP, Connection dbConnection  ) throws Exception {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = "UPDATE auth_user_invite_tracking SET invite_used_auth_user_id = ?, use_ip = ?, invite_used = 1, invite_used_date = NOW() WHERE id = ?";

		
		try {
			
//			CREATE TABLE auth_user_invite_tracking (
//					  id int(10) unsigned NOT NULL AUTO_INCREMENT,
//					  submitting_auth_user_id int(10) unsigned NOT NULL,
//					  submit_ip varchar(255) NOT NULL,
//					  invite_tracking_code varchar(255) NOT NULL,
//					  invited_user_email varchar(255) NOT NULL,
//					  invited_user_access_level smallint(6) NOT NULL,
//					  invited_shared_object_id int(10) unsigned DEFAULT NULL,
//					  invite_create_date datetime NOT NULL,
//					  invite_used tinyint(4) DEFAULT NULL,
//					  invite_used_date datetime DEFAULT NULL,
//					  invite_used_auth_user_id int(10) unsigned DEFAULT NULL,
//					  use_ip varchar(255) DEFAULT NULL,
//					  code_replaced_by_newer tinyint(4) DEFAULT NULL,
//					  invite_revoked tinyint(4) DEFAULT NULL,
//					  revoking_auth_user_id int(10) unsigned DEFAULT NULL,
//					  revoked_date datetime DEFAULT NULL,

	
			
			pstmt = dbConnection.prepareStatement( sql );
			
			int counter = 0;

			counter++;
			pstmt.setInt( counter, useAuthUserId );
			
			counter++;
			pstmt.setString( counter, useIP );

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
			
		}
		
	}
	
	
	

	/**
	 * Update revoking_auth_user_id = ?, invite_revoked = 1, revoked_date = NOW()
	 * @param id
	 * @param revokeAuthUserId
	 * @param useIP
	 * @throws Exception
	 */
	public void updateRevokedInviteFields( int id, int revokeAuthUserId ) throws Exception {
		
		Connection dbConnection = null;

		try {
			
			dbConnection = AuthLibraryDBConnectionFactory.getConnection(  );

			updateRevokedInviteFields( id, revokeAuthUserId, dbConnection );

		} finally {
			
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
			
		}
	}
	



	/**
	 * Update revoking_auth_user_id = ?, invite_revoked = 1, revoked_date = NOW()
	 * @param id
	 * @param revokeAuthUserId
	 * @param useIP
	 * @throws Exception
	 */
	public void updateRevokedInviteFields( int id, int revokeAuthUserId, Connection dbConnection  ) throws Exception {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = "UPDATE auth_user_invite_tracking SET revoking_auth_user_id = ?, invite_revoked = 1, revoked_date = NOW() WHERE id = ?";

		
		try {
			
//			CREATE TABLE auth_user_invite_tracking (
//					  id int(10) unsigned NOT NULL AUTO_INCREMENT,
//					  submitting_auth_user_id int(10) unsigned NOT NULL,
//					  submit_ip varchar(255) NOT NULL,
//					  invite_tracking_code varchar(255) NOT NULL,
//					  invited_user_email varchar(255) NOT NULL,
//					  invited_user_access_level smallint(6) NOT NULL,
//					  invited_shared_object_id int(10) unsigned DEFAULT NULL,
//					  invite_create_date datetime NOT NULL,
//					  invite_used tinyint(4) DEFAULT NULL,
//					  invite_used_date datetime DEFAULT NULL,
//					  invite_used_auth_user_id int(10) unsigned DEFAULT NULL,
//					  use_ip varchar(255) DEFAULT NULL,
//					  code_replaced_by_newer tinyint(4) DEFAULT NULL,
//					  invite_revoked tinyint(4) DEFAULT NULL,
//					  revoking_auth_user_id int(10) unsigned DEFAULT NULL,
//					  revoked_date datetime DEFAULT NULL,

	
			
			pstmt = dbConnection.prepareStatement( sql );
			
			int counter = 0;

			counter++;
			pstmt.setInt( counter, revokeAuthUserId );
			
			counter++;
			pstmt.setInt( counter, id );
			
			pstmt.executeUpdate();
			
		} catch ( Exception e ) {
			
			String msg = "Failed to update invite_revoked, sql: " + sql;
			
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
			
		}
		
	}
	
	


	/**
	 * Update invited_user_access_level = ?
	 * @param id
	 * @param invitedUserAccessLevel
	 * @throws Exception
	 */
	public void updateInvitedUserAccessLevelFields( int id, int invitedUserAccessLevel ) throws Exception {
		
		Connection dbConnection = null;

		try {
			
			dbConnection = AuthLibraryDBConnectionFactory.getConnection(  );

			updateInvitedUserAccessLevelFields( id, invitedUserAccessLevel, dbConnection );

		} finally {
			
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
			
		}
	}
	



	/**
	 * Update invited_user_access_level = ?
	 * @param id
	 * @param invitedUserAccessLevel
	 * @throws Exception
	 */
	public void updateInvitedUserAccessLevelFields( int id, int invitedUserAccessLevel, Connection dbConnection  ) throws Exception {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = "UPDATE auth_user_invite_tracking SET invited_user_access_level = ? WHERE id = ?";

		
		try {
			
//			CREATE TABLE auth_user_invite_tracking (
//					  id int(10) unsigned NOT NULL AUTO_INCREMENT,
//					  submitting_auth_user_id int(10) unsigned NOT NULL,
//					  submit_ip varchar(255) NOT NULL,
//					  invite_tracking_code varchar(255) NOT NULL,
//					  invited_user_email varchar(255) NOT NULL,
//					  invited_user_access_level smallint(6) NOT NULL,
//					  invited_shared_object_id int(10) unsigned DEFAULT NULL,
//					  invite_create_date datetime NOT NULL,
//					  invite_used tinyint(4) DEFAULT NULL,
//					  invite_used_date datetime DEFAULT NULL,
//					  invite_used_auth_user_id int(10) unsigned DEFAULT NULL,
//					  use_ip varchar(255) DEFAULT NULL,
//					  code_replaced_by_newer tinyint(4) DEFAULT NULL,
//					  invite_revoked tinyint(4) DEFAULT NULL,
//					  revoking_auth_user_id int(10) unsigned DEFAULT NULL,
//					  revoked_date datetime DEFAULT NULL,

	
			
			pstmt = dbConnection.prepareStatement( sql );
			
			int counter = 0;

			counter++;
			pstmt.setInt( counter, invitedUserAccessLevel );
			
			counter++;
			pstmt.setInt( counter, id );
			
			pstmt.executeUpdate();
			
		} catch ( Exception e ) {
			
			String msg = "Failed to update auth_user_invite_tracking, sql: " + sql;
			
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
			
		}
		
	}
	
	

	private final String updateCodeReplacedByNewerToTrueForPrevInvitesMainPartSQL 
		= "UPDATE auth_user_invite_tracking SET code_replaced_by_newer = 1 "
			+ " WHERE invited_user_email = ? AND id < ? AND ";
	
	private final String updateCodeReplacedByNewerToTrueForPrevInvitesSharedObjectValuePartSQL 
		= "invited_shared_object_id = ? ";
	
	private final String updateCodeReplacedByNewerToTrueForPrevInvitesSharedObjectNULLPartSQL 
		= "invited_shared_object_id IS NULL ";

	private final String updateCodeReplacedByNewerToTrueForPrevInvitesSharedObjectValueSQL 
	= updateCodeReplacedByNewerToTrueForPrevInvitesMainPartSQL + updateCodeReplacedByNewerToTrueForPrevInvitesSharedObjectValuePartSQL;


	private final String updateCodeReplacedByNewerToTrueForPrevInvitesSharedObjectNULLSQL 
	= updateCodeReplacedByNewerToTrueForPrevInvitesMainPartSQL + updateCodeReplacedByNewerToTrueForPrevInvitesSharedObjectNULLPartSQL;



	/**
	 * Update code_replaced_by_newer = ? for same shared_object_id and user_email and id < supplied id

	 * @param authUserInviteTrackingDTO
	 * @throws Exception
	 */
	public void updateCodeReplacedByNewerToTrueForPrevInvites( AuthUserInviteTrackingDTO authUserInviteTrackingDTO ) throws Exception {
		
		int rowsUpdated = 0; 
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = updateCodeReplacedByNewerToTrueForPrevInvitesSharedObjectValueSQL;
		
		if ( authUserInviteTrackingDTO.getInvitedSharedObjectId() == null ) {
			
			sql = updateCodeReplacedByNewerToTrueForPrevInvitesSharedObjectNULLSQL;
		}


		
		try {
			
			conn = AuthLibraryDBConnectionFactory.getConnection(  );
			
			
//			CREATE TABLE auth_user_invite_tracking (
//			  id int(10) unsigned NOT NULL AUTO_INCREMENT,
//			  submitting_auth_user_id int(10) unsigned NOT NULL,
//			  submit_ip varchar(255) NOT NULL,
//			  invite_tracking_code varchar(255) NOT NULL,
//			  invited_user_email varchar(255) NOT NULL,
//			  invited_user_access_level smallint(6) NOT NULL,
//			  invited_shared_object_id int(10) unsigned DEFAULT NULL,
//			  invite_create_date datetime NOT NULL,
//			  invite_used tinyint(4) DEFAULT NULL,
//			  invite_used_date datetime DEFAULT NULL,
//			  invite_used_auth_user_id int(10) unsigned DEFAULT NULL,
//			  use_ip varchar(255) DEFAULT NULL,
//			  code_replaced_by_newer tinyint(4) DEFAULT NULL,
//			  invite_revoked tinyint(4) DEFAULT NULL,
//			  revoking_auth_user_id int(10) unsigned DEFAULT NULL,
//			  revoked_date datetime DEFAULT NULL,
	
			
			pstmt = conn.prepareStatement( sql );
			
			int counter = 0;

			counter++;
			pstmt.setString( counter, authUserInviteTrackingDTO.getInvitedUserEmail() );

			counter++;
			pstmt.setInt( counter, authUserInviteTrackingDTO.getId() );


			if ( authUserInviteTrackingDTO.getInvitedSharedObjectId() != null ) {
				counter++;
				pstmt.setInt( counter, authUserInviteTrackingDTO.getInvitedSharedObjectId() );
			}

			rowsUpdated = pstmt.executeUpdate();
			
			
			
		} catch ( Exception e ) {
			
			String msg = "Failed updateCodeReplacedByNewerToTrueForPrevInvites(...), sql: " + sql;
			
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
	 * @param id
	 * @throws Exception
	 */
	public void delete( int id ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = "DELETE FROM auth_user_invite_tracking WHERE id = ?";

		
		try {
			
			conn = AuthLibraryDBConnectionFactory.getConnection(  );
			
			
//			CREATE TABLE auth_user_invite_tracking (
//			  id int(10) unsigned NOT NULL AUTO_INCREMENT,
//			  submitting_auth_user_id int(10) unsigned NOT NULL,
//			  submit_ip varchar(255) NOT NULL,
//			  invite_tracking_code varchar(255) NOT NULL,
//			  invited_user_email varchar(255) NOT NULL,
//			  invited_user_access_level smallint(6) NOT NULL,
//			  invited_shared_object_id int(10) unsigned DEFAULT NULL,
//			  invite_create_date datetime NOT NULL,
//			  invite_used tinyint(4) DEFAULT NULL,
//			  invite_used_date datetime DEFAULT NULL,
//			  invite_used_auth_user_id int(10) unsigned DEFAULT NULL,
//			  use_ip varchar(255) DEFAULT NULL,
//			  code_replaced_by_newer tinyint(4) DEFAULT NULL,
//			  invite_revoked tinyint(4) DEFAULT NULL,
//			  revoking_auth_user_id int(10) unsigned DEFAULT NULL,
//			  revoked_date datetime DEFAULT NULL,
	
			
			pstmt = conn.prepareStatement( sql );
			
			int counter = 0;

			counter++;
			pstmt.setInt( counter, id );
			
			pstmt.executeUpdate();
			
		} catch ( Exception e ) {
			
			String msg = "Failed to delete, sql: " + sql;
			
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
