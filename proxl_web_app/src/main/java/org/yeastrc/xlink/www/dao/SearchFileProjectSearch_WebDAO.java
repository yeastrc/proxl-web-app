package org.yeastrc.xlink.www.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * Table search_file__project_search
 *
 */
public class SearchFileProjectSearch_WebDAO {
	
	private static final Logger log = LoggerFactory.getLogger( SearchFileProjectSearch_WebDAO.class);
	
	private SearchFileProjectSearch_WebDAO() { }
	public static SearchFileProjectSearch_WebDAO getInstance() { return new SearchFileProjectSearch_WebDAO(); }
	
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

//		  CREATE TABLE  search_file__project_search (
//				  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
//				  search_file_id INT UNSIGNED NOT NULL,
//				  project_search_id INT UNSIGNED NOT NULL,
//				  display_filename VARCHAR(255) NOT NULL,
//		
		String sql = 
				" INSERT INTO search_file__project_search " 
				+ "( project_search_id, search_file_id, display_filename ) " 
				+ " SELECT " 
				 + newProjectSearchId + ", search_file_id, display_filename"
				+ " FROM search_file__project_search "
				+ " WHERE search_file__project_search.project_search_id = " + oldProjectSearchId;
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
