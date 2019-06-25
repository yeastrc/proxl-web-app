package org.yeastrc.xlink.www.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.dto.ProjectLevelDefaultFltr_MinPSMs_DTO;

/**
 * Table project_level_default_fltr_min_psms_tbl
 *
 */
public class ProjectLevelDefaultFltr_MinPSMs_DAO {

	private static final Logger log = LoggerFactory.getLogger( ProjectLevelDefaultFltr_MinPSMs_DAO.class);
	private ProjectLevelDefaultFltr_MinPSMs_DAO() { }
	public static ProjectLevelDefaultFltr_MinPSMs_DAO getInstance() { return new ProjectLevelDefaultFltr_MinPSMs_DAO(); }

	/**
	 * @param item
	 * @throws Exception
	 */
	public void save( ProjectLevelDefaultFltr_MinPSMs_DTO item ) throws Exception {
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
	
	private static final String INSERT_SQL = 
			"INSERT INTO project_level_default_fltr_min_psms_tbl "
			+ "(project_id, min_psms, created_auth_user_id, last_updated_auth_user_id ) "
			+ "VALUES ( ?, ?, ?, ? )";
	
	/**
	 * @param item
	 * @throws Exception
	 */
	public void save( ProjectLevelDefaultFltr_MinPSMs_DTO item, Connection dbConnection ) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = INSERT_SQL;
		try {

			pstmt = dbConnection.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			int counter = 0;
			counter++;
			pstmt.setInt( counter, item.getProjectId() );
			counter++;
			pstmt.setInt( counter, item.getMinPSMs() );
			
			counter++;
			pstmt.setInt( counter, item.getCreatedAuthUserId() );
			counter++;
			pstmt.setInt( counter, item.getLastUpdatedAuthUserId() );

			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();
			if( rs.next() ) {
				item.setId( rs.getInt( 1 ) );
			} else {
				String msg = "Failed to insert record, generated key not found.";
				log.error( msg );
				throw new Exception( msg );
			}
		} catch ( Exception e ) {
			String msg = "Failed to insert record, sql: " + sql;
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

	private static final String COPY_TO_PREV_TBL_FOR_PROJECT_ID_SQL = 
		
			"INSERT INTO project_level_default_fltr_min_psms_prev_tbl "
			 + " ( id, project_id, min_psms, "
			 +   " created_auth_user_id, created_date_time, last_updated_auth_user_id, last_updated_date_time, " 
			 +   " id_prev_record, copy_create_date ) "
			+ " SELECT "
			+   " id, project_id, min_psms, "
			+   " created_auth_user_id, created_date_time, last_updated_auth_user_id, last_updated_date_time, null, now() " 
			+   " FROM project_level_default_fltr_min_psms_tbl "
			+ " WHERE project_id = ?";
	
	/**
	 * @param item
	 * @throws Exception
	 */
	public void copyToPrevTable_ForProjectId( int projectId, Connection dbConnection ) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = COPY_TO_PREV_TBL_FOR_PROJECT_ID_SQL;
		try {
			pstmt = dbConnection.prepareStatement( sql );
			int counter = 0;
			counter++;
			pstmt.setInt( counter, projectId );

			pstmt.executeUpdate();
			
		} catch ( Exception e ) {
			String msg = "Failed copyToPrevTable_ForProjectId(...), sql: " + sql;
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
	 * @param id
	 * @throws Exception
	 */
	public void deleteId( int id ) throws Exception {
		Connection dbConnection = null;
		try {
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			deleteId( id, dbConnection );
		} finally {
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
		}
	}
	
	/**
	 * Delete the project_level_default_fltr_min_psms_tbl record for the id
	 * @param id
	 * @throws Exception
	 */
	public void deleteId( int id, Connection dbConnection ) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "DELETE FROM project_level_default_fltr_min_psms_tbl WHERE id = ?";
				
		try {
			pstmt = dbConnection.prepareStatement( sql );
			pstmt.setInt( 1, id );
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
	 * Delete All project_level_default_fltr_min_psms_tbl record for project_id
	 * @param project_id
	 * @throws Exception
	 */
	public void deleteAllFor_ProjectId( int project_id, Connection dbConnection ) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "DELETE FROM project_level_default_fltr_min_psms_tbl WHERE project_id = ?";
				
		try {
			pstmt = dbConnection.prepareStatement( sql );
			pstmt.setInt( 1, project_id );
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
