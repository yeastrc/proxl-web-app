package org.yeastrc.xlink.www.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.dto.ProjectLevelDefaultFltrAnnCutoffs_CutoffAsStringValue_DTO;

/**
 * Table project_level_default_fltr_ann_cutoffs_cutoff_as_string_tbl
 *
 */
public class ProjectLevelDefaultFltrAnnCutoffs_CutoffAsStringValue_DAO {

	private static final Logger log = LoggerFactory.getLogger( ProjectLevelDefaultFltrAnnCutoffs_CutoffAsStringValue_DAO.class);
	private ProjectLevelDefaultFltrAnnCutoffs_CutoffAsStringValue_DAO() { }
	public static ProjectLevelDefaultFltrAnnCutoffs_CutoffAsStringValue_DAO getInstance() { return new ProjectLevelDefaultFltrAnnCutoffs_CutoffAsStringValue_DAO(); }

	/**
	 * @param item
	 * @throws Exception
	 */
	public void save( ProjectLevelDefaultFltrAnnCutoffs_CutoffAsStringValue_DTO item ) throws Exception {
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
			"INSERT INTO project_level_default_fltr_ann_cutoffs_cutoff_as_string_tbl "
			+ "( project_level_default_fltr_ann_cutoffs_id, annotation_cutoff_value_string ) "
			+ "VALUES ( ?, ? )";
	
	/**
	 * @param item
	 * @throws Exception
	 */
	public void save( ProjectLevelDefaultFltrAnnCutoffs_CutoffAsStringValue_DTO item, Connection dbConnection ) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = INSERT_SQL;
		try {

			pstmt = dbConnection.prepareStatement( sql );
			int counter = 0;
			counter++;
			pstmt.setInt( counter, item.getProjectLevelDefaultFltrAnnCutoffs_CutoffAsStringValue_Id() );
			counter++;
			pstmt.setString( counter, item.getAnnotationCutoffValueString() );

			pstmt.executeUpdate();
			
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
			"INSERT INTO project_level_default_fltr_ann_cutoffs_cutoff_as_string_prev_tbl "
			+ " (project_level_default_fltr_ann_cutoffs_id, annotation_cutoff_value_string, project_id) "
			+ " SELECT project_level_default_fltr_ann_cutoffs_id, annotation_cutoff_value_string, project_id "
			+ " FROM project_level_default_fltr_ann_cutoffs_cutoff_as_string_tbl AS string_tbl"
			+ " INNER JOIN project_level_default_fltr_ann_cutoffs_tbl AS main_tbl ON string_tbl.project_level_default_fltr_ann_cutoffs_id = main_tbl.id "
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
	 * Delete the project_level_default_fltr_ann_cutoffs_cutoff_as_string_tbl record for the id
	 * @param id
	 * @throws Exception
	 */
	public void deleteId( int id, Connection dbConnection ) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "DELETE FROM project_level_default_fltr_ann_cutoffs_cutoff_as_string_tbl WHERE id = ?";
				
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
	
}
