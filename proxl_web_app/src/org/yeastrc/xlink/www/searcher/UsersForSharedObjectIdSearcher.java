package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.constants.AuthAccessLevelConstants;
import org.yeastrc.xlink.db.DBConnectionFactory;
/**
 * Return a list of users in the database for a shared object id
 *
 * This is not in Auth_Library since it uses access level constants in the Crosslinking web app
 *
 */
public class UsersForSharedObjectIdSearcher {
	
	private static final Logger log = Logger.getLogger(UsersForSharedObjectIdSearcher.class);
	private UsersForSharedObjectIdSearcher() { }
	private static final UsersForSharedObjectIdSearcher _INSTANCE = new UsersForSharedObjectIdSearcher();
	public static UsersForSharedObjectIdSearcher getInstance() { return _INSTANCE; }
	
	private final String SQL = "SELECT auth_shared_object_users.user_id FROM "
			+ " auth_shared_object  "
			+ " INNER JOIN auth_shared_object_users ON auth_shared_object.shared_object_id = auth_shared_object_users.shared_object_id "
			+ " WHERE  auth_shared_object.shared_object_id = ? "
	 		+ "  AND auth_shared_object_users.user_id NOT IN "
	 		+    " ( "
	 		+     " SELECT DISTINCT auth_user.id FROM auth_user"
			+      " WHERE "
			+      "     auth_user.user_access_level = " + AuthAccessLevelConstants.ACCESS_LEVEL_NONE
			+     " ) ";
	/**
	 * @param sharedObjectId
	 * @return
	 * @throws Exception
	 */
	public List<Integer> getAuthUserIdsExcludeGlobalNoAccessAccountsForSharedObjectId( int sharedObjectId ) throws Exception {
		List<Integer> userIds = new ArrayList<Integer>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = SQL;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, sharedObjectId );
			rs = pstmt.executeQuery();
			while( rs.next() ) {
				userIds.add( rs.getInt( 1 ) );
			}
		} catch ( Exception e ) {
			String msg = "getAuthUserIdForProjectId(), sql: " + sql;
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
