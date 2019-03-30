package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.ProjectSearch_Shared_DTO;

/**
 * Table project_search
 * 
 * Shared between web app and Importer
 *
 */
public class ProjectSearch_Shared_DAO {

	private static final Logger log = LoggerFactory.getLogger( ProjectSearch_Shared_DAO.class);
	
	private ProjectSearch_Shared_DAO() { }
	public static ProjectSearch_Shared_DAO getInstance() { return new ProjectSearch_Shared_DAO(); }
	

	/**
	 * Get the given record from the database
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ProjectSearch_Shared_DTO getFromId( int id ) throws Exception {
		
		ProjectSearch_Shared_DTO result = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "SELECT project_id, search_id, status_id, search_name, search_display_order FROM project_search WHERE id = ? ";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				result = new ProjectSearch_Shared_DTO();
				result.setId( id );
				result.setProjectId( rs.getInt( "project_id" ) );
				result.setSearchId( rs.getInt( "search_id" ) );
				result.setStatusId( rs.getInt( "status_id" ) );
				result.setSearchName( rs.getString( "search_name" ) );
				result.setSearchDisplayOrder( rs.getInt( "search_display_order" ) );
			}
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
		return result;
	}
	
	/**
	 * This will INSERT the given ProjectSearch_Shared_DTO into the database... even if an id is already set.
	 * This will result in a new id being set in the object.
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabase( ProjectSearch_Shared_DTO item ) throws Exception {
		Connection dbConnection = null;
		try {
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			saveToDatabase( item, dbConnection );
		} finally {
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
		}
	}
	private static final String INSERT_SQL =
			"INSERT INTO project_search "
			+ " ( project_id, search_id, status_id, search_name, search_display_order ) "
			+ " VALUES (?, ?, ?, ?, ? )";
	
	/**
	 * This will INSERT the given ProjectSearch_Shared_DTO into the database... even if an id is already set.
	 * This will result in a new id being set in the object.
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabase( ProjectSearch_Shared_DTO item, Connection conn ) throws Exception {
//		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = INSERT_SQL;
		try {
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			int counter = 0;
//
//	CREATE TABLE IF NOT EXISTS `proxl`.`project_search` (
//			  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
//			  `project_id` INT UNSIGNED NOT NULL,
//			  `search_id` INT UNSIGNED NOT NULL,
//			  `status_id` TINYINT UNSIGNED NOT NULL DEFAULT 1,
//			  `search_name` VARCHAR(2000) NULL,
//			  `search_display_order` INT NOT NULL DEFAULT 0,
//			  `marked_for_deletion_auth_user_id` INT UNSIGNED NULL,
//			  `marked_for_deletion_timestamp` DATETIME NULL,
//	
//			+ " ( project_id, search_id, status_id, search_name, search_display_order ) "
//			+ " VALUES (?, ?, ?, ?, ?, ?, )";
			counter++;
			pstmt.setInt( counter, item.getProjectId() );
			counter++;
			pstmt.setInt( counter, item.getSearchId() );
			counter++;
			pstmt.setInt( counter, item.getStatusId() );
			counter++;
			pstmt.setString( counter, item.getSearchName() );
			counter++;
			pstmt.setInt( counter, item.getSearchDisplayOrder() );

			pstmt.executeUpdate();
			rs = pstmt.getGeneratedKeys();
			if( rs.next() ) {
				item.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert project_search for searchId: " + item.getSearchId() );
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
//			if( conn != null ) {
//				try { conn.close(); } catch( Throwable t ) { ; }
//				conn = null;
//			}
		}
	}
	

	/**
	 * Update the status_id associated with this projectSearchId
	 * @param projectSearchId
	 * @param statusId
	 * @throws Exception
	 */
	public void updateStatusId( int projectSearchId, int statusId ) throws Exception {
		
		Connection dbConnection = null;
		try {
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			updateStatusId( projectSearchId, statusId, dbConnection );
		} finally {
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
		}
	}
	
	/**
	 * Update the status_id associated with this projectSearchId
	 * @param projectSearchId
	 * @param newProjectId
	 * @throws Exception
	 */
	public void updateStatusId( int projectSearchId, int statusId, Connection dbConnection ) throws Exception {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "UPDATE project_search SET status_id = ? WHERE id = ?";
		try {
			pstmt = dbConnection.prepareStatement( sql );
			pstmt.setInt( 1, statusId );
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
}
