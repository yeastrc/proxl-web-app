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
 * Return a list of users in the database for 
 * 	a query and not in the project id and not Global No access allowed and Not User Disabled
 *
 *
 */
public class UserSearcherForSearchStringNotSharedObjectIdNotUserDisabled {

	private static final Logger log = Logger.getLogger(UserSearcherForSearchStringNotSharedObjectIdNotUserDisabled.class);
	
	private UserSearcherForSearchStringNotSharedObjectIdNotUserDisabled() { }
	private static final UserSearcherForSearchStringNotSharedObjectIdNotUserDisabled _INSTANCE = new UserSearcherForSearchStringNotSharedObjectIdNotUserDisabled();
	public static UserSearcherForSearchStringNotSharedObjectIdNotUserDisabled getInstance() { return _INSTANCE; }
	
	
	

	
	/**
	 * Return a list of users in the database for 
	 * 	a query and not in the project id and not Global No access allowed and Not User Disabled
	 * 
	 * @param lastName
	 * @return
	 * @throws Exception
	 */
	public List<Integer> getAuthUserIdListForLastNameAndNotSharedObjectIdNotUserDisabled( String lastName, int notSharedObjectId ) throws Exception {
		
		String queryForLike = lastName + "%";
		
		
		List<Integer> userIds = new ArrayList<Integer>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT DISTINCT auth_user.id FROM auth_user"

		 		+ " INNER JOIN xl_user ON auth_user.id = xl_user.auth_user_id "
		 		
				+ " WHERE "
				+ "  ( xl_user.last_name LIKE ? ) "
				
		 		+ "  AND auth_user.id NOT IN "
		 		
		 		+    " ( "
				
		 		+     " SELECT DISTINCT auth_user.id FROM auth_user"
				+     " LEFT OUTER JOIN auth_shared_object_users ON auth_user.id = auth_shared_object_users.user_id "
		 		
				+      " WHERE "
				+      "   auth_shared_object_users.shared_object_id = ?  "
				+      "   OR ( auth_user.user_access_level = " + AuthAccessLevelConstants.ACCESS_LEVEL_NONE
				+       		" OR  auth_user.enabled = 0"
				+            " ) "
				+     " ) ";



		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			
			pstmt = conn.prepareStatement( sql );
			
			int count = 0;
			
			count++;
			pstmt.setString( count, queryForLike );
		
			count++;
			pstmt.setInt( count, notSharedObjectId );
			

			
			rs = pstmt.executeQuery();

			while( rs.next() ) {

				userIds.add( rs.getInt( 1 ) );
			}
			
		} catch ( Exception e ) {
			
			String msg = "getAuthUserIdForQuery(), sql: " + sql;
			
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
	
	

	
	/**
	 * Return a list of users in the database for 
	 * 	a query and not in the project id and not Global No access allowed and Not User Disabled
	 * 
	 * @param email
	 * @return
	 * @throws Exception
	 */
	public List<Integer> getAuthUserIdListForEmailAndNotSharedObjectIdNotUserDisabled( String email, int notSharedObjectId ) throws Exception {
		
		String queryForLike = email + "%";
		
		
		List<Integer> userIds = new ArrayList<Integer>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT DISTINCT auth_user.id FROM auth_user"

		 		+ " INNER JOIN xl_user ON auth_user.id = xl_user.auth_user_id "
		 		
				+ " WHERE "
				+ "  ( auth_user.email LIKE ? ) "
				
		 		+ "  AND auth_user.id NOT IN "
		 		
		 		+    " ( "
				
		 		+     " SELECT DISTINCT auth_user.id FROM auth_user"
				+     " LEFT OUTER JOIN auth_shared_object_users ON auth_user.id = auth_shared_object_users.user_id "
		 		
				+      " WHERE "
				+      "   auth_shared_object_users.shared_object_id = ?  "
				+      "   OR ( auth_user.user_access_level = " + AuthAccessLevelConstants.ACCESS_LEVEL_NONE
				+       		" OR  auth_user.enabled = 0"
				+            " ) "
				+     " ) ";



		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			
			pstmt = conn.prepareStatement( sql );
			
			int count = 0;
			
			count++;
			pstmt.setString( count, queryForLike );
		
			count++;
			pstmt.setInt( count, notSharedObjectId );
			

			
			rs = pstmt.executeQuery();

			while( rs.next() ) {

				userIds.add( rs.getInt( 1 ) );
			}
			
		} catch ( Exception e ) {
			
			String msg = "getAuthUserIdForQuery(), sql: " + sql;
			
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
	
	
	

	
	/**
	 * Return a list of users in the database for 
	 * 	a query and not in the project id and not Global No access allowed and Not User Disabled
	 * 
	 * @param query
	 * @return
	 * @throws Exception
	 */
	public List<Integer> getAuthUserIdListForQueryAndNotSharedObjectIdNotUserDisabled( String query, int notSharedObjectId ) throws Exception {
		
		String queryForLike = query + "%";
		
		
		List<Integer> userIds = new ArrayList<Integer>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT DISTINCT auth_user.id FROM auth_user"

		 		+ " INNER JOIN xl_user ON auth_user.id = xl_user.auth_user_id "
		 		
				+ " WHERE "
				+ "  ( auth_user.username LIKE ? OR auth_user.email LIKE ? "
				+ "       OR xl_user.first_name LIKE ? OR xl_user.last_name LIKE ? ) "
				
		 		+ "  AND auth_user.id NOT IN "
		 		
		 		+    " ( "
				
		 		+     " SELECT DISTINCT auth_user.id FROM auth_user"
				+     " LEFT OUTER JOIN auth_shared_object_users ON auth_user.id = auth_shared_object_users.user_id "
		 		
				+      " WHERE "
				+      "   auth_shared_object_users.shared_object_id = ?  "
				+      "   OR ( auth_user.user_access_level = " + AuthAccessLevelConstants.ACCESS_LEVEL_NONE
				+       		" OR  auth_user.enabled = 0"
				+            " ) "
				+     " ) ";



		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			
			pstmt = conn.prepareStatement( sql );
			
			int count = 0;
			
			count++;
			pstmt.setString( count, queryForLike );

			count++;
			pstmt.setString( count, queryForLike );
			
			count++;
			pstmt.setString( count, queryForLike );
			
			count++;
			pstmt.setString( count, queryForLike );
			
			count++;
			pstmt.setInt( count, notSharedObjectId );
			

			
			rs = pstmt.executeQuery();

			while( rs.next() ) {

				userIds.add( rs.getInt( 1 ) );
			}
			
		} catch ( Exception e ) {
			
			String msg = "getAuthUserIdForQuery(), sql: " + sql;
			
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
