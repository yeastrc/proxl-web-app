package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * 
 *
 */
public class ProjectIdsForProjectSearchIdsSearcher {
	
	private static final Logger log = LoggerFactory.getLogger( ProjectIdsForProjectSearchIdsSearcher.class);
	private ProjectIdsForProjectSearchIdsSearcher() { }
	public static ProjectIdsForProjectSearchIdsSearcher getInstance() { return new ProjectIdsForProjectSearchIdsSearcher(); }
	
	private static final String sqlMain = 
	  "SELECT DISTINCT project_id FROM project_search " 
	  + " INNER JOIN project ON project_search.project_id = project.id " 
	  + " WHERE project.enabled = 1 AND project.marked_for_deletion = 0 AND project_search.id IN ( ";
	  
	  private static final String sqlEnd = " )";

	/**
	 * Get a list of project ids for the collection of project_search ids
	 * @param projectSearchIds
	 * @return
	 * @throws Exception
	 */
	public List<Integer> getProjectIdsForProjectSearchIds( Collection<Integer> projectSearchIds ) throws Exception {
		List<Integer>  resultList = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sqlSearchIdsString = null;
		for ( Integer searchId : projectSearchIds ) {
			if ( sqlSearchIdsString == null ) {
				sqlSearchIdsString = searchId.toString();
			} else {
				sqlSearchIdsString += "," + searchId.toString();
			}
		}
		final String sql = sqlMain + sqlSearchIdsString + sqlEnd;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			rs = pstmt.executeQuery();
			while ( rs.next() ) {
				int projectId = rs.getInt( "project_id" );
				resultList.add( projectId );
			}
		} catch ( Exception e ) {
			log.error( "ERROR: sql: " + sql, e );
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
		return resultList;
	}
}
