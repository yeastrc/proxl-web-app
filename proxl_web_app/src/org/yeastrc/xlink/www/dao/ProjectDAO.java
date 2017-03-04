package org.yeastrc.xlink.www.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.dto.ProjectDTO;
import org.yeastrc.xlink.www.objects.ProjectTblSubPartsForProjectLists;

/**
 * DAO for project table
 *
 */
public class ProjectDAO {
	
	private static final Logger log = Logger.getLogger(ProjectDAO.class);
	
	//  private constructor
	private ProjectDAO() { }
	/**
	 * @return newly created instance
	 */
	public static ProjectDAO getInstance() { 
		return new ProjectDAO(); 
	}

	/**
	 * @param projectId
	 * @return null if not found
	 * @throws Exception
	 */
	public ProjectDTO getProjectDTOForProjectId( int projectId ) throws Exception {
		
		ProjectDTO returnItem = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "SELECT * FROM project WHERE id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, projectId );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				returnItem = populateResultObject( rs );
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
		return returnItem;
	}
	
	

	/**
	 * @param projectId
	 * @return null if not found
	 * @throws Exception
	 */
	public ProjectTblSubPartsForProjectLists getProjectTblSubPartsForProjectListsForProjectId( int projectId ) throws Exception {
		
		ProjectTblSubPartsForProjectLists returnItem = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "SELECT title, project_locked FROM project WHERE id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, projectId );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				returnItem = new ProjectTblSubPartsForProjectLists();
				returnItem.setId( projectId );
				returnItem.setTitle( rs.getString( "title" ) );
				returnItem.setProjectLocked( rs.getBoolean( "project_locked" ) );
			}
		} catch ( Exception e ) {
			String msg = "Failed to select ProjectTblSubPartsForProjectLists, projectId: " + projectId + ", sql: " + sql;
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
		return returnItem;
	}
	
	/**
	 * @param authShareableObjectId
	 * @return null if not found
	 * @throws Exception
	 */
	public ProjectDTO getProjectDTOForAuthShareableObjectId( int authShareableObjectId ) throws Exception {
		
		ProjectDTO returnItem = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "SELECT * FROM project WHERE auth_shareable_object_id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, authShareableObjectId );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				returnItem = populateResultObject( rs );
			}
		} catch ( Exception e ) {
			String msg = "Failed to select ProjectDTO, projectId: " + authShareableObjectId + ", sql: " + sql;
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
		return returnItem;
	}
	
	/**
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private ProjectDTO populateResultObject(ResultSet rs) throws SQLException {
		
		ProjectDTO returnItem = new ProjectDTO();
		returnItem.setId( rs.getInt( "id" ) );
		returnItem.setAuthShareableObjectId( rs.getInt( "auth_shareable_object_id" ) );
		returnItem.setTitle( rs.getString( "title" ) );
		returnItem.setAbstractText( rs.getString( "abstract" ) );
		returnItem.setEnabled( rs.getBoolean( "enabled" ) );
		returnItem.setMarkedForDeletion( rs.getBoolean( "marked_for_deletion" ) );;
		int markedForDeletionAuthUserId = rs.getInt( "marked_for_deletion_auth_user_id" );
		returnItem.setMarkedForDeletionTimstamp( rs.getDate( "marked_for_deletion_timestamp" ) );
		if (rs.wasNull()) {
			returnItem.setMarkedForDeletionAuthUserId( null );
		} else {
			returnItem.setMarkedForDeletionAuthUserId( markedForDeletionAuthUserId );
		}
		returnItem.setProjectLocked( rs.getBoolean( "project_locked" ) );
		int publicAccessLevel = rs.getInt( "public_access_level" );
		if (rs.wasNull()) {
			returnItem.setPublicAccessLevel( null );
		} else {
			returnItem.setPublicAccessLevel( publicAccessLevel );
		}
		returnItem.setPublicAccessLocked( rs.getBoolean( "public_access_locked" ) );
		return returnItem;
	}
	
	/**
	 * @param projectId
	 * @return null if not found
	 * @throws Exception
	 */
	public Integer getAuthShareableObjectIdForProjectId( int projectId ) throws Exception {
		
		Integer authShareableObjectId = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "SELECT auth_shareable_object_id FROM project WHERE id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, projectId );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				authShareableObjectId =  rs.getInt( "auth_shareable_object_id" );
			}
		} catch ( Exception e ) {
			String msg = "Failed to select auth_shareable_object_id, projectId: " + projectId + ", sql: " + sql;
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
		return authShareableObjectId;
	}
	
	/**
	 * !!!  Only populates properties projectLocked, publicAccessLevel, public_access_locked, enabled, markedForDeletion,
	 * 
	 * @param projectId
	 * @return null if not found
	 * @throws Exception
	 */
	public ProjectDTO getProjectLockedPublicAccessLevelPublicAccessLockedForProjectId( int projectId ) throws Exception {
		
		ProjectDTO returnItem = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "SELECT project_locked, public_access_level, public_access_locked, enabled, marked_for_deletion, auth_shareable_object_id FROM project WHERE id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, projectId );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				returnItem = new ProjectDTO();
				returnItem.setId( projectId );
				returnItem.setAuthShareableObjectId( rs.getInt( "auth_shareable_object_id" ) );
				returnItem.setProjectLocked( rs.getBoolean( "project_locked" ) );
				int publicAccessLevel = rs.getInt( "public_access_level" );
				if (rs.wasNull()) {
					returnItem.setPublicAccessLevel( null );
				} else {
					returnItem.setPublicAccessLevel( publicAccessLevel );
				}
				returnItem.setPublicAccessLocked( rs.getBoolean( "public_access_locked" ) );
				returnItem.setEnabled( rs.getBoolean( "enabled" ) );
				returnItem.setMarkedForDeletion( rs.getBoolean( "marked_for_deletion" ) );
			}
		} catch ( Exception e ) {
			String msg = "Failed to select subset, projectId: " + projectId + ", sql: " + sql;
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
		return returnItem;
	}
	
	/**
	 * @param item
	 * @throws Exception
	 */
	public void save( ProjectDTO item ) throws Exception {
		Connection dbConnection = null;
		try {
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			save( item, dbConnection );
		} finally {
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
		}
	}
	
	/**
	 * @param item
	 * @throws Exception
	 */
	public void save( ProjectDTO item, Connection dbConnection ) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "INSERT INTO project (auth_shareable_object_id, title, abstract ) VALUES ( ?, ?, ? )";
		try {
			pstmt = dbConnection.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			int counter = 0;
			counter++;
			pstmt.setInt( counter, item.getAuthShareableObjectId() );
			counter++;
			pstmt.setString( counter, item.getTitle() );
			counter++;
			pstmt.setString( counter, item.getAbstractText() );
			pstmt.executeUpdate();
			rs = pstmt.getGeneratedKeys();
			if( rs.next() ) {
				item.setId( rs.getInt( 1 ) );
			} else {
				String msg = "Failed to insert ProjectDTO, generated key not found.";
				log.error( msg );
				throw new Exception( msg );
			}
		} catch ( Exception e ) {
			String msg = "Failed to insert ProjectDTO, sql: " + sql;
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
		}
	}
	
	/**
	 * Update title = ? 
	 * @param id
	 * @param title
	 * @throws Exception
	 */
	public void updateTitle( int id, String title ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "UPDATE project SET title = ? WHERE id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			int counter = 0;
			counter++;
			pstmt.setString( counter, title );
			counter++;
			pstmt.setInt( counter, id );
			pstmt.executeUpdate();
		} catch ( Exception e ) {
			String msg = "Failed to update title, sql: " + sql;
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
	}
	
	/**
	 * Update abstract = ? 
	 * @param id
	 * @param abstractText
	 * @throws Exception
	 */
	public void updateAbstract( int id, String abstractText ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "UPDATE project SET abstract = ? WHERE id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			int counter = 0;
			counter++;
			pstmt.setString( counter, abstractText );
			counter++;
			pstmt.setInt( counter, id );
			pstmt.executeUpdate();
		} catch ( Exception e ) {
			String msg = "Failed to update abstract, sql: " + sql;
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
	}
	
	/**
	 * Update enabled = 0, marked_for_deletion = 1
	 * @param id
	 * @throws Exception
	 */
	public void updateSetEnabledZeroAndMarkToDeleteOne( int id, int authUserId ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "UPDATE project SET enabled = 0, marked_for_deletion = 1, marked_for_deletion_auth_user_id = ?, marked_for_deletion_timestamp = NOW() WHERE id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			int counter = 0;
			counter++;
			pstmt.setInt( counter, authUserId );
			counter++;
			pstmt.setInt( counter, id );
			pstmt.executeUpdate();
		} catch ( Exception e ) {
			String msg = "Failed to update setEnabledZeroAndMarkToDeleteOne, sql: " + sql;
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
	}
	
	/**
	 * Update public_access_level = ? 
	 * @param id
	 * @param publicAccessLevel
	 * @throws Exception
	 */
	public void updatePublicAccessLevel( int id, Integer publicAccessLevel ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "UPDATE project SET public_access_level = ? WHERE id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			int counter = 0;
			counter++;
			if( publicAccessLevel != null  ) {
				pstmt.setInt( counter, publicAccessLevel);
			} else {
				pstmt.setNull( counter, java.sql.Types.INTEGER);
			}
			counter++;
			pstmt.setInt( counter, id );
			pstmt.executeUpdate();
		} catch ( Exception e ) {
			String msg = "Failed to update public_access_level, sql: " + sql;
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
	}
	
	/**
	 * Update public_access_locked = ? 
	 * @param id
	 * @param publicAccessLocked
	 * @throws Exception
	 */
	public void updatePublicAccessLocked( int id, boolean publicAccessLocked ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "UPDATE project SET public_access_locked = ? WHERE id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			int counter = 0;
			counter++;
			pstmt.setBoolean( counter, publicAccessLocked );
			counter++;
			pstmt.setInt( counter, id );
			pstmt.executeUpdate();
		} catch ( Exception e ) {
			String msg = "Failed to update public_access_locked, sql: " + sql;
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
	}
	
	/**
	 * Update project_locked = ? 
	 * @param id
	 * @param projectLocked
	 * @throws Exception
	 */
	public void updateProjectLocked( int id, boolean projectLocked ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "UPDATE project SET project_locked = ? WHERE id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			int counter = 0;
			counter++;
			pstmt.setBoolean( counter, projectLocked );
			counter++;
			pstmt.setInt( counter, id );
			pstmt.executeUpdate();
		} catch ( Exception e ) {
			String msg = "Failed to update , sql: " + sql;
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
	}
}
