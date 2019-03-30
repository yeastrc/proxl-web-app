package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
/**
 * Return a list of users in the database that match the access level
 *
 *
 */
public class UserSearcherForAccessLevel {
	
	private static final Logger log = LoggerFactory.getLogger( UserSearcherForAccessLevel.class);
	private UserSearcherForAccessLevel() { }
	private static final UserSearcherForAccessLevel _INSTANCE = new UserSearcherForAccessLevel();
	public static UserSearcherForAccessLevel getInstance() { return _INSTANCE; }
	
	/**
	 * @param authAccessLevel
	 * @return
	 * @throws Exception
	 */
	public List<Integer> getAuthUserIdsForAuthAccessLevel( int authAccessLevel ) throws Exception {
		List<Integer> userIds = new ArrayList<Integer>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT id FROM auth_user WHERE user_access_level = ?"; 
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			int count = 0;
			count++;
			pstmt.setInt( count, authAccessLevel );
			rs = pstmt.executeQuery();
			while( rs.next() ) {
				userIds.add( rs.getInt( 1 ) );
			}
		} catch ( Exception e ) {
			String msg = "getAuthUserIdsForAuthAccessLevel(), sql: " + sql;
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
