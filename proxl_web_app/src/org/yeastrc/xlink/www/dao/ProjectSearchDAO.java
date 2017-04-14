package org.yeastrc.xlink.www.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.enum_classes.SearchRecordStatus;

/**
 * Table project_search
 *
 * !!! SearchDAO also has queries against project_search table
 */
public class ProjectSearchDAO {

	private static final Logger log = Logger.getLogger(ProjectSearchDAO.class);
	private ProjectSearchDAO() { }
	public static ProjectSearchDAO getInstance() { return new ProjectSearchDAO(); }
	
	/**
	 * Update the name associated with this search
	 * @param search
	 * @param name
	 * @throws Exception
	 */
	public void updateName( int projectSearchId, String name, int authUserId ) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "UPDATE project_search SET search_name = ?, updated_by_user_id = ? WHERE id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setString( 1, name );
			pstmt.setInt( 2, authUserId );
			pstmt.setInt( 3, projectSearchId );
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
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
		}
	}
	
	/**
	 * Update the project_id associated with this projectSearchId
	 * @param projectSearchId
	 * @param newProjectId
	 * @throws Exception
	 */
	public void updateProjectIdForProjectSearch( int projectSearchId, int newProjectId ) throws Exception {
		Connection dbConnection = null;
		try {
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			updateProjectIdForProjectSearch( projectSearchId, newProjectId, dbConnection );
		} finally {
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
		}
	}
	
	/**
	 * Update the project_id associated with this projectSearchId
	 * @param projectSearchId
	 * @param newProjectId
	 * @throws Exception
	 */
	public void updateProjectIdForProjectSearch( int projectSearchId, int newProjectId, Connection dbConnection ) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "UPDATE project_search SET project_id = ? WHERE id = ?";
		try {
			pstmt = dbConnection.prepareStatement( sql );
			pstmt.setInt( 1, newProjectId );
			pstmt.setInt( 2, projectSearchId );
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
		}
	}
	
	/**
	 * Update the display_order associated with this search
	 * @param projectSearchId
	 * @param newDisplayOrder
	 * @throws Exception
	 */
	public void updateDisplayOrderForProjectSearch( int projectSearchId, int newDisplayOrder, Connection dbConnection ) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "UPDATE project_search SET search_display_order = ? WHERE id = ?";
		try {
			pstmt = dbConnection.prepareStatement( sql );
			pstmt.setInt( 1, newDisplayOrder );
			pstmt.setInt( 2, projectSearchId );
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
		}
	}
	
	private static final String resetDisplayOrderForFolderId_SQL =
			
			"UPDATE project_search INNER JOIN folder_project_search " 
			+ 		" ON project_search.id = folder_project_search.project_search_id "
			+ "SET project_search.search_display_order = 0 "
			+ "WHERE folder_project_search.folder_id = ?";

	/**
	 * Update the display_order associated with this search
	 * @param folderId
	 * @param newDisplayOrder
	 * @throws Exception
	 */
	public void resetDisplayOrderForFolderId( int folderId, Connection dbConnection ) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = resetDisplayOrderForFolderId_SQL;
		try {
			pstmt = dbConnection.prepareStatement( sql );
			pstmt.setInt( 1, folderId );
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
		}
	}
	
	
	/**
	 * Mark search as deleted
	 * @param searchId
	 * @param deletionAuthUserId
	 * @throws Exception
	 */
	public void markAsDeleted( int searchId, int deletionAuthUserId ) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection dbConnection = null;
		//  active_project_id_search_id_unique_record = NULL  to remove this record from UNIQUE index on project_id, search_id, active_project_id_search_id_unique_record
		String sql = "UPDATE project_search SET status_id = " + SearchRecordStatus.MARKED_FOR_DELETION.value()
				+ ", active_project_id_search_id_unique_record = NULL "
				+ ", marked_for_deletion_auth_user_id = ?, marked_for_deletion_timestamp = NOW() "
				+ " WHERE id = ?";
		try {
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = dbConnection.prepareStatement( sql );
			pstmt.setInt( 1, deletionAuthUserId );
			pstmt.setInt( 2, searchId );
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
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
		}
	}
}
