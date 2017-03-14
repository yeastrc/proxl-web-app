package org.yeastrc.proxl.import_xml_to_db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dto.ProjectSearchDTO_Importer;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.enum_classes.SearchRecordStatus;

/**
 * Table project_search
 *
 */
public class ProjectSearchDAO_Importer {

	private static final Logger log = Logger.getLogger(ProjectSearchDAO_Importer.class);
	
	private ProjectSearchDAO_Importer() { }
	public static ProjectSearchDAO_Importer getInstance() { return new ProjectSearchDAO_Importer(); }
	
	/**
	 * This will INSERT the given ProjectSearchDTO into the database... even if an id is already set.
	 * This will result in a new id being set in the object.
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabase( ProjectSearchDTO_Importer item ) throws Exception {
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
			+ " (project_id, search_id, search_name, status_id, created_by_user_id ) "
			+ " VALUES (?, ?, ?, " +  SearchRecordStatus.IMPORTING.value() + ", ? "
			+ " )";
	/**
	 * This will INSERT the given ProjectSearchDTO into the database... even if an id is already set.
	 * This will result in a new id being set in the object.
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabase( ProjectSearchDTO_Importer item, Connection conn ) throws Exception {
//		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = INSERT_SQL;
		try {
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			int counter = 0;
//			project_id, search_id, search_name
			counter++;
			pstmt.setInt( counter, item.getProjectId() );
			counter++;
			pstmt.setInt( counter, item.getSearchId() );
			counter++;
			pstmt.setString( counter, item.getSearchName() );
			counter++;
			if ( item.getCreatedByUserId() != null ) {
				pstmt.setInt( counter, item.getCreatedByUserId() );
			} else {
				pstmt.setNull(counter, java.sql.Types.INTEGER );
			}

			pstmt.executeUpdate();
			rs = pstmt.getGeneratedKeys();
			if( rs.next() ) {
				item.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert project_search for SearchId " + item.getSearchId() );
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
	 * Update the status_id associated with this search
	 * @param projectSearchId
	 * @param status
	 * @throws Exception
	 */
	public void updateStatus( int projectSearchId, SearchRecordStatus status ) throws Exception {
		Connection dbConnection = null;
		try {
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			updateStatus( projectSearchId, status, dbConnection );
		} finally {
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
		}
	}
	
	/**
	 * Update the status_id associated with this search
	 * @param projectSearchId
	 * @param status
	 * @throws Exception
	 */
	public void updateStatus( int projectSearchId, SearchRecordStatus status, Connection dbConnection ) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "UPDATE project_search SET status_id = ? WHERE id = ?";
		try {
			pstmt = dbConnection.prepareStatement( sql );
			pstmt.setInt( 1, status.value() );
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
