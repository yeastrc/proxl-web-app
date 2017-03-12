package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.constants.AuthAccessLevelConstants;
import org.yeastrc.xlink.db.DBConnectionFactory;
/**
 * Return a list of users in the database for 
 * 	a query and not in the project id and not Global No access allowed and Not User Disabled
 *
 *
 */
public class UserSearcherForSharedObjectIdNotUserGlobalNoAccess {
	
	private static final Logger log = Logger.getLogger(UserSearcherForSharedObjectIdNotUserGlobalNoAccess.class);
	private UserSearcherForSharedObjectIdNotUserGlobalNoAccess() { }
	private static final UserSearcherForSharedObjectIdNotUserGlobalNoAccess _INSTANCE = new UserSearcherForSharedObjectIdNotUserGlobalNoAccess();
	public static UserSearcherForSharedObjectIdNotUserGlobalNoAccess getInstance() { return _INSTANCE; }
	
	private static final String SQL = " SELECT auth_user.user_mgmt_user_id FROM auth_user"
			+ " INNER JOIN auth_shared_object_users ON auth_user.id = auth_shared_object_users.user_id "
			+ " WHERE "
			+  " auth_shared_object_users.shared_object_id = ?  "
			+  " OR auth_user.user_access_level = " + AuthAccessLevelConstants.ACCESS_LEVEL_NONE;
	/**
	 * Return a list of auth_user.id in the database for 
	 * in the project id and not Global No access allowed
	 * 
	 * @param sharedObjectId
	 * @return
	 * @throws Exception
	 */
	public Set<Integer> getUserMgmtUserIdListForSharedObjectId( int sharedObjectId ) throws Exception {
		Set<Integer> userIds = new HashSet<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = SQL;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			int count = 0;
			count++;
			pstmt.setInt( count, sharedObjectId );
			rs = pstmt.executeQuery();
			while( rs.next() ) {
				userIds.add( rs.getInt( 1 ) );
			}
		} catch ( Exception e ) {
			String msg = "getAuthUserIdListForSharedObjectId(), sql: " + sql;
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
		return userIds;
	}
}
