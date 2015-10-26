package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;

public class ProjectIdsForSearchIdsSearcher {
	
	private static final Logger log = Logger.getLogger(ProjectIdsForSearchIdsSearcher.class);

	private ProjectIdsForSearchIdsSearcher() { }
	public static ProjectIdsForSearchIdsSearcher getInstance() { return new ProjectIdsForSearchIdsSearcher(); }

	/**
	 * Get a list of project ids for the collection of search ids
	 * @param searchIds
	 * @return
	 * @throws Exception
	 */
	public List<Integer> getProjectIdsForSearchIds( Collection<Integer> searchIds ) throws Exception {

		List<Integer>  resultList = new ArrayList<>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sqlMain = " SELECT project_id FROM ( SELECT DISTINCT project_id AS project_id "
				 + " FROM search "
				 + " WHERE id IN (";
		
		final String sqlEnd = ") ) AS project_ids INNER JOIN project ON project_ids.project_id = project.id WHERE project.enabled = 1 AND project.marked_for_deletion = 0  ";
		
		String sqlSearchIdsString = null;
		
		for ( Integer searchId : searchIds ) {
			
			if ( sqlSearchIdsString == null ) {
				
				sqlSearchIdsString = searchId.toString();
			} else {
				
				sqlSearchIdsString += "," + searchId.toString();
			}
		}
		
		final String sql = sqlMain + sqlSearchIdsString + sqlEnd;
		
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
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
