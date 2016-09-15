package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * On the project table, is the project_locked field not set to zero
 *
 */
public class ProjectLockedChecker {

	private static final Logger log = Logger.getLogger(ProjectLockedChecker.class);

	private ProjectLockedChecker() { }
	public static ProjectLockedChecker getInstance() { return new ProjectLockedChecker(); }
	
	/**
	 * @return true if the project is locked, false otherwise, null if project id not found
	 * @throws Exception
	 */
	public Boolean isProjectLocked( int projectId ) throws Exception {
		
		Boolean isProjectLocked = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT project_locked FROM project WHERE id = ?";
		
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			
			pstmt = conn.prepareStatement( sql );

			pstmt.setInt( 1, projectId );
			
			rs = pstmt.executeQuery();
			
			if ( rs.next() ) {
				
				isProjectLocked = rs.getBoolean( "project_locked" );
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

		return isProjectLocked;
	}
	
}
