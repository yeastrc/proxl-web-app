package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.constants.AuthAccessLevelConstants;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.objects.ProjectTblSubPartsForProjectLists;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_ProjectTblSubPartsForProjectLists;

/**
 * Return a list of projects in the database this session can access, ordered by project title
 *
 *
 */
public class ProjectSearcher {

	private static final Logger log = Logger.getLogger(ProjectSearcher.class);
	
	private ProjectSearcher() { }
	private static final ProjectSearcher _INSTANCE = new ProjectSearcher();
	public static ProjectSearcher getInstance() { return _INSTANCE; }
	
	
	

	
	/**
	 * @param authUserId
	 * @return
	 * @throws Exception
	 */
	public List<ProjectTblSubPartsForProjectLists> getProjectsForAuthUserId( int authUserId ) throws Exception {
		
		List<ProjectTblSubPartsForProjectLists> projects = new ArrayList<>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT project.id FROM project"
				+ " INNER JOIN auth_shared_object ON project.auth_shareable_object_id = auth_shared_object.shared_object_id "
				+ " INNER JOIN auth_shared_object_users ON auth_shared_object.shared_object_id = auth_shared_object_users.shared_object_id "
				+ " WHERE  ( auth_shared_object_users.user_id = ? AND auth_shared_object_users.access_level <= " 
				+              AuthAccessLevelConstants.ACCESS_LEVEL__PUBLIC_ACCESS_CODE_READ_ONLY__PUBLIC_PROJECT_READ_ONLY +   " ) "
				+           " AND project.enabled = 1 AND project.marked_for_deletion = 0 ";
		
				
				
				
		sql += " ORDER BY project.id DESC";
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			
			pstmt = conn.prepareStatement( sql );
			
			pstmt.setInt( 1, authUserId );
			
			rs = pstmt.executeQuery();

			while( rs.next() ) { 
				projects.add( Cached_ProjectTblSubPartsForProjectLists.getInstance().getProjectTblSubPartsForProjectLists( rs.getInt( 1 ) ) );
			}
			
		} catch ( Exception e ) {
			
			String msg = "getProjectsForAuthUserId(), sql: " + sql;
			
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
		
		return projects;
	}
	
	

	
	

	
//	/**
//	 * @param authUserId
//	 * @param allowedProjectIds
//	 * @return
//	 * @throws Exception
//	 */
//	public List<ProjectDTO> getProjectsForAuthUserIdORAllowedProjectIds( Integer authUserId, Set<Integer> allowedProjectIds ) throws Exception {
//		
//
//		//CREATE TABLE IF NOT EXISTS crosslinks.project (
////				  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
////				  auth_shareable_object_id INT UNSIGNED NOT NULL,
////				  title VARCHAR(255) NULL,
////				  abstract TEXT NULL,
//		
////		CREATE TABLE IF NOT EXISTS crosslinks.auth_shared_object (
////				  shared_object_id INT UNSIGNED NOT NULL,
////				  public_access_code_enabled TINYINT(1) NOT NULL DEFAULT false,
////				  public_access_code VARCHAR(255) NULL,
//
////		CREATE TABLE IF NOT EXISTS auth_shared_object_users (
////				  shared_object_id INT UNSIGNED NOT NULL,
////				  user_id INT UNSIGNED NOT NULL,
////				  access_level SMALLINT UNSIGNED NOT NULL,
//			
//		
//		List<ProjectDTO> projects = new ArrayList<ProjectDTO>();
//		
//		Connection conn = null;
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//		
//		String sql = "SELECT project.id FROM project";
//		
//
//		if ( authUserId == null && ( allowedProjectIds == null || ( allowedProjectIds.isEmpty() ) ) ) {
//				
//			String msg = "Input not allowed: authUserId == null && ( allowedProjectIds == null || ( allowedProjectIds.isEmpty() ) )";
//			
//			throw new IllegalArgumentException(msg);
//		}
//
//
//		if ( authUserId != null ) {
//			
//			//  auth_shared_object.access_level is for that user_id so with access_level zero is admin, user access_level must be <= read
//
//			sql += " INNER JOIN auth_shared_object ON project.auth_shareable_object_id = auth_shared_object.shared_object_id "
//					+ " INNER JOIN auth_shared_object_users ON auth_shared_object.shared_object_id = auth_shared_object_users.shared_object_id "
//					+ " WHERE  ( auth_shared_object_users.user_id = ? AND auth_shared_object_users.access_level <= " 
//					+              AuthAccessLevelConstants.ACCESS_LEVEL__PUBLIC_ACCESS_CODE_READ_ONLY__PUBLIC_PROJECT_READ_ONLY +   " ) ";
//		}
//
//		if ( allowedProjectIds != null && ( ! allowedProjectIds.isEmpty() ) ) {
//
//			if ( authUserId != null ) {
//
//				sql += " OR ";
//
//			} else {
//
//				sql += " WHERE ";
//			}
//
//			sql += " ( project.id IN (";
//
//			Iterator<Integer> projectIdIter = allowedProjectIds.iterator();
//
//			sql += projectIdIter.next();
//
//			while ( projectIdIter.hasNext() ) {
//
//				sql += ", " + projectIdIter.next();
//			}
//
//			sql += " ) ) ";
//		}
//			
//		
//				
//				
//				
//		sql += " ORDER BY project.title";
//		
//		try {
//			
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
//
//			
//			pstmt = conn.prepareStatement( sql );
//			
//
//			if ( authUserId != null ) {
//
//				pstmt.setInt( 1, authUserId );
//			}
//			
//			rs = pstmt.executeQuery();
//
//			while( rs.next() )
//				projects.add( ProjectDAO.getInstance().getProjectDTOForProjectId( rs.getInt( 1 ) ) );
//			
//		} catch ( Exception e ) {
//			
//			String msg = "getProjectsForAuthUserIdORAllowedProjectIds(), sql: " + sql;
//			
//			log.error( msg, e );
//			
//			throw e;
//			
//		} finally {
//			
//			// be sure database handles are closed
//			if( rs != null ) {
//				try { rs.close(); } catch( Throwable t ) { ; }
//				rs = null;
//			}
//			
//			if( pstmt != null ) {
//				try { pstmt.close(); } catch( Throwable t ) { ; }
//				pstmt = null;
//			}
//			
//			if( conn != null ) {
//				try { conn.close(); } catch( Throwable t ) { ; }
//				conn = null;
//			}
//			
//		}
//		
//		
//		
//		return projects;
//	}
	
	
	
	
	
	
	/**
	 * @return
	 * @throws Exception
	 */
	public List<ProjectTblSubPartsForProjectLists> getAllProjects() throws Exception {
		
		
		List<ProjectTblSubPartsForProjectLists> projects = new ArrayList<>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT id FROM project" 
				+ " WHERE project.enabled = 1 AND project.marked_for_deletion = 0 "
				+ " ORDER BY project.title";
		
		try {

	
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			pstmt = conn.prepareStatement( sql );
			
			rs = pstmt.executeQuery();

			while( rs.next() ) {
				projects.add( Cached_ProjectTblSubPartsForProjectLists.getInstance().getProjectTblSubPartsForProjectLists( rs.getInt( 1 ) ) );
			}
			
		} catch ( Exception e ) {
			
			String msg = "ERROR: getAllProjects(), sql: " + sql;
			
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
		
		
		
		return projects;
	}
	
}
