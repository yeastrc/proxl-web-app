package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.enum_classes.SearchRecordStatus;

/**
 * Is the SearchId associated with a ProjectSearchId in the ProjectId where project_search is not marked for deletion
 *
 */
public class ProjectSearchIdAssocSearchIdInProjectIdSearcher {

	private static final Logger log = Logger.getLogger(ProjectSearchIdAssocSearchIdInProjectIdSearcher.class);
	ProjectSearchIdAssocSearchIdInProjectIdSearcher() { }
	private static ProjectSearchIdAssocSearchIdInProjectIdSearcher _INSTANCE = new ProjectSearchIdAssocSearchIdInProjectIdSearcher();
	public static ProjectSearchIdAssocSearchIdInProjectIdSearcher getInstance() { return _INSTANCE; }
	
	private static final String isSearchIdAssocWithProjectSearchIdInProjectId_SQL =
			" SELECT from_project_search.id "
			+ " FROM project_search AS from_project_search "
			+ " INNER JOIN project_search AS to_project_search "
			+    " ON from_project_search.search_id = to_project_search.search_id "
			+ " WHERE from_project_search.id = ? AND to_project_search.project_id = ? "
			+ " AND to_project_search.status_id = " + SearchRecordStatus.IMPORT_COMPLETE_VIEW.value();
	/**
	 * Is the SearchId associated with a ProjectSearchId in the ProjectId where project_search is not marked for deletion
	 * 
	 * @param projectSearchId
	 * @param projectId
	 * @return
	 * @throws Exception 
	 */
	public boolean isSearchIdAssocWithProjectSearchIdInProjectId( int projectSearchId, int projectId ) throws Exception {
		boolean result = false;;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = isSearchIdAssocWithProjectSearchIdInProjectId_SQL;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, projectSearchId );
			pstmt.setInt( 2, projectId );
			rs = pstmt.executeQuery();
			if( rs.next() )
				result = true;
		} catch ( Exception e ) {
			String msg = "Exception in isSearchIdAssocWithProjectSearchIdInProjectId( ... ): sql: " + sql;
			log.error( msg );
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
		return result;
	}
}
