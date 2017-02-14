package org.yeastrc.xlink.www.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * Table search_comment
 *
 */
public class SearchComment_WebDAO {
	
	private static final Logger log = Logger.getLogger(SearchComment_WebDAO.class);
	
	private SearchComment_WebDAO() { }
	public static SearchComment_WebDAO getInstance() { return new SearchComment_WebDAO(); }
	
	/**
	 * Copy records from projectSearchId with new projectSearchId
	 * @param search
	 * @param name
	 * @throws Exception
	 */
	public void duplicateRecordsForProjectSearchIdWithNewProjectSearchId( 
			int oldProjectSearchId,
			int newProjectSearchId,
			Connection dbConnection
			) throws Exception {
		
//		Connection dbConnection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = 
				" INSERT INTO search_comment " 
				+ "( project_search_id, comment, commentTimestamp, auth_user_id, commentCreatedTimestamp, created_auth_user_id ) " 
				+ " SELECT " 
				 + newProjectSearchId + ", comment, commentTimestamp, auth_user_id, commentCreatedTimestamp, created_auth_user_id"
				+ " FROM search_comment "
				+ " WHERE search_comment.project_search_id = " + oldProjectSearchId;
				;
		try {
//			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = dbConnection.prepareStatement( sql );
			pstmt.executeUpdate();
		} catch ( Exception e ) {
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sql, e );
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
//			if( dbConnection != null ) {
//				try { dbConnection.close(); } catch( Throwable t ) { ; }
//				dbConnection = null;
//			}
		}
	}
	
}
