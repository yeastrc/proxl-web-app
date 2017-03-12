package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.objects.ProjectToCopyToResultItem;

/**
 * Find projects that can copy to
 *
 *
 */
public class ProjectToCopyToSearcher {

	private static final Logger log = Logger.getLogger(ProjectToCopyToSearcher.class);
	private ProjectToCopyToSearcher() { }
	private static final ProjectToCopyToSearcher _INSTANCE = new ProjectToCopyToSearcher();
	public static ProjectToCopyToSearcher getInstance() { return _INSTANCE; }
	
	private static final String anyProjectsExistExcludingProjectId_SQL =
			"SELECT id FROM project "
			+ " WHERE id != ? "
			+ " AND enabled = 1 AND marked_for_deletion = 0 AND project_locked = 0 LIMIT 1";
	/**
	 * @param projectId
	 * @return 
	 * @throws Exception
	 */
	public boolean anyProjectsExistExcludingProjectId( int projectId ) throws Exception {
		boolean result = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = anyProjectsExistExcludingProjectId_SQL;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, projectId );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				result = true;
			}
		} catch ( Exception e ) {
			String msg = "anyProjectsExistExcludingProjectId(...), projectId: " + projectId + ", sql: " + sql;
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
		return result;
	}
	
	private static final String getAllExcludingProjectId_SQL =
			"SELECT project.id, project.title "
			+ " FROM project WHERE id != ?"
			+ " AND enabled = 1 AND marked_for_deletion = 0 AND project_locked = 0"
			+ " ORDER BY project.title";
	/**
	 * @param projectId
	 * @return 
	 * @throws Exception
	 */
	public List<ProjectToCopyToResultItem> getAllExcludingProjectId( int projectId ) throws Exception {
		List<ProjectToCopyToResultItem>  returnList = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = getAllExcludingProjectId_SQL;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, projectId );
			rs = pstmt.executeQuery();
			while( rs.next() ) {
				ProjectToCopyToResultItem returnItem = populateProjectToCopyToResultItemFromResultObject( rs );
				returnList.add(returnItem);
			}
		} catch ( Exception e ) {
			String msg = "Failed to select ProjectDTO, projectId: " + projectId + ", sql: " + sql;
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
		return returnList;
	}
	
	private static final String anyProjectsExistForAuthUserExcludingProjectId_SQL = "SELECT project.id FROM project " 
			+ " INNER JOIN auth_shared_object_users ON project.auth_shareable_object_id = auth_shared_object_users.shared_object_id"
			+ " WHERE auth_shared_object_users.user_id = ? AND auth_shared_object_users.access_level <= ?"
			+ " AND project.id != ? "
			+ " AND project.enabled = 1 AND project.marked_for_deletion = 0 AND project_locked = 0 "
			+ " LIMIT 1";
	/**
	 * @param authUserId
	 * @param maxAuthLevel
	 * @param projectId
	 * @return 
	 * @throws Exception
	 */
	public boolean anyProjectsExistForAuthUserExcludingProjectId( int authUserId, int maxAuthLevel, int projectId ) throws Exception {
		boolean result = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = anyProjectsExistForAuthUserExcludingProjectId_SQL; 
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, authUserId );
			pstmt.setInt( 2, maxAuthLevel );
			pstmt.setInt( 3, projectId );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				result = true;
			}
		} catch ( Exception e ) {
			String msg = "anyProjectsExistForAuthUserExcludingProjectId(...), projectId: " + projectId + ", sql: " + sql;
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
		return result;
	}
	
	///////////
	private static final String getForAuthUserExcludingProjectId_SQL = "SELECT project.id, project.title FROM project " 
			+ " INNER JOIN auth_shared_object_users ON project.auth_shareable_object_id = auth_shared_object_users.shared_object_id"
			+ " WHERE auth_shared_object_users.user_id = ? AND auth_shared_object_users.access_level <= ?"
			+ " AND project.id != ? "
			+ " AND project.enabled = 1 AND project.marked_for_deletion = 0 AND project_locked = 0"
			+ " ORDER BY project.title";
	/**
	 * @param authUserId
	 * @param maxAuthLevel
	 * @param projectId
	 * @return 
	 * @throws Exception
	 */
	public List<ProjectToCopyToResultItem> getForAuthUserExcludingProjectId( int authUserId, int maxAuthLevel, int projectId ) throws Exception {
		List<ProjectToCopyToResultItem>  returnList = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = getForAuthUserExcludingProjectId_SQL; 
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, authUserId );
			pstmt.setInt( 2, maxAuthLevel );
			pstmt.setInt( 3, projectId );
			rs = pstmt.executeQuery();
			while( rs.next() ) {
				ProjectToCopyToResultItem returnItem = populateProjectToCopyToResultItemFromResultObject( rs );
				returnList.add(returnItem);
			}
		} catch ( Exception e ) {
			String msg = "Failed to select ProjectDTO, projectId: " + projectId + ", sql: " + sql;
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
		return returnList;
	}
	
	/**
	 * @param rs
	 * @return
	 * @throws SQLException 
	 */
	private ProjectToCopyToResultItem populateProjectToCopyToResultItemFromResultObject( ResultSet rs ) throws SQLException {
		ProjectToCopyToResultItem item = new ProjectToCopyToResultItem();
		item.setProjectId( rs.getInt( "id" ) );
		item.setProjectTitle( rs.getString( "title" ) );
		return item;
	}
}
