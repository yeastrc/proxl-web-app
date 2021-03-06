package org.yeastrc.auth.searchers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.auth.dao.AuthUserInviteTrackingDAO;
import org.yeastrc.auth.db.AuthLibraryDBConnectionFactory;
import org.yeastrc.auth.dto.AuthUserInviteTrackingDTO;
/**
 * 
 *
 */
public class AuthUserInvitesActiveUnusedNotReplacedNotRevokedSearcher {
	
	private static final Logger log = LoggerFactory.getLogger( AuthUserInvitesActiveUnusedNotReplacedNotRevokedSearcher.class);
	//  private constructor
	private AuthUserInvitesActiveUnusedNotReplacedNotRevokedSearcher() { }
	/**
	 * @return newly created instance
	 */
	public static AuthUserInvitesActiveUnusedNotReplacedNotRevokedSearcher getInstance() { 
		return new AuthUserInvitesActiveUnusedNotReplacedNotRevokedSearcher(); 
	}
	
	/**
	 * Get invite tracking records where invite has not been used, replaced, or revoked, All records 
	 * @return
	 * @throws Exception
	 */
	public List<AuthUserInviteTrackingDTO> getAuthUserInvitesActiveAllInvites(  ) throws Exception {
		return getAuthUserInvitesActiveUnused( true /* getAllInvites */, 0 /* sharedObjectId */, false /* useSharedObjectId */);
	}
	/**
	 * Get invite tracking records for sharedObjectId where invite has not been used, replaced, or revoked 
	 * @param sharedObjectId
	 * @return
	 * @throws Exception
	 */
	public List<AuthUserInviteTrackingDTO> getAuthUserInvitesActiveForSharedObjectId( int sharedObjectId ) throws Exception {
		return getAuthUserInvitesActiveUnused( false /* getAllInvites */, sharedObjectId, true /* useSharedObjectId */);
	}
	/**
	 * Get invite tracking records where sharedObjectId is NULL where invite has not been used, replaced, or revoked 
	 * @return
	 * @throws Exception
	 */
	public List<AuthUserInviteTrackingDTO> getAuthUserInvitesActiveForSharedObjectIdNULL(  ) throws Exception {
		return getAuthUserInvitesActiveUnused( false /* getAllInvites */, 0 /* sharedObjectId */, false /* useSharedObjectId */);
	}

	private final String sqlBase = "SELECT id " 
			+ " FROM auth_user_invite_tracking WHERE  "
			+ " ( invite_used IS NULL OR invite_used = 0 )"
			+ " AND ( code_replaced_by_newer IS NULL OR code_replaced_by_newer = 0 )"
			+ " AND ( invite_revoked IS NULL OR invite_revoked = 0 )";
	
	private final String sqlAllInvites = sqlBase;
	private final String sqlWhereSharedObjectIdIsNULL = sqlBase  
			+ " AND invited_shared_object_id IS NULL ";
	private final String sqlWithSharedObjectId = sqlBase 
			+ " AND invited_shared_object_id = ? ";
	
	/**
	 * Get invite tracking records  where invite has not been used, replaced, or revoked 
	 * @param sharedObjectId
	 * @param useSharedObjectId
	 * @return
	 * @throws Exception
	 */
	private List<AuthUserInviteTrackingDTO> getAuthUserInvitesActiveUnused( boolean getAllInvites, int sharedObjectId, boolean useSharedObjectId ) throws Exception {
		
		List<AuthUserInviteTrackingDTO> returnList = new ArrayList<AuthUserInviteTrackingDTO>();
		AuthUserInviteTrackingDAO authUserInviteTrackingDAO = AuthUserInviteTrackingDAO.getInstance(); 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = sqlWithSharedObjectId;
		if ( getAllInvites ) {
			sql = sqlAllInvites;
		} else if ( ! useSharedObjectId ) { 
			sql = sqlWhereSharedObjectIdIsNULL;
		}
		try {
			conn = AuthLibraryDBConnectionFactory.getConnection(  );
			pstmt = conn.prepareStatement( sql );
			if ( useSharedObjectId ) {
				pstmt.setInt( 1, sharedObjectId );
			}
			rs = pstmt.executeQuery();
			while( rs.next() ) {
				int id = rs.getInt( "id" );
				AuthUserInviteTrackingDTO item = authUserInviteTrackingDAO.getForInviteTrackingId(id);
				returnList.add(item);
			}
		} catch ( Exception e ) {
			String msg = "Failed to query, sharedObjectId: " + sharedObjectId
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
		return returnList; 
	}
}
