package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
/**
 * Return a list of all users in the database 
 *
 *
 */
public class UserSearcherAll {
	
	private static final Logger log = Logger.getLogger(UserSearcherAll.class);
	private UserSearcherAll() { }
	private static final UserSearcherAll _INSTANCE = new UserSearcherAll();
	public static UserSearcherAll getInstance() { return _INSTANCE; }
	
	/**
	 * @return
	 * @throws Exception
	 */
	public List<Integer> getAllAuthUserIds( ) throws Exception {
		List<Integer> userIds = new ArrayList<Integer>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT auth_user.id FROM auth_user"; 
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			rs = pstmt.executeQuery();
			while( rs.next() ) {
				userIds.add( rs.getInt( 1 ) );
			}
		} catch ( Exception e ) {
			String msg = "getAllAuthUserIds(), sql: " + sql;
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
