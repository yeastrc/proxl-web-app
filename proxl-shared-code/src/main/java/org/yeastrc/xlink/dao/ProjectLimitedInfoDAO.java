package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.yeastrc.xlink.db.DBConnectionFactory;


/**
 * data for specific fields from the project table
 *
 */
public class ProjectLimitedInfoDAO {

	private static final Logger log = LoggerFactory.getLogger( ProjectLimitedInfoDAO.class);

	private ProjectLimitedInfoDAO() { }
	public static ProjectLimitedInfoDAO getInstance() { return new ProjectLimitedInfoDAO(); }
	
	/**
	 * @return true if the project is enabled, false otherwise, null if project id not found
	 * @throws Exception
	 */
	public Boolean isProjectEnabled( int projectId ) throws Exception {
		
		Boolean isProjectEnabled = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT enabled FROM project WHERE id = ?";
		
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			
			pstmt = conn.prepareStatement( sql );

			pstmt.setInt( 1, projectId );
			
			rs = pstmt.executeQuery();
			
			if ( rs.next() ) {
				
				isProjectEnabled = rs.getBoolean( "enabled" );
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

		return isProjectEnabled;
	}
	
	
	/**
	 * @return true if the project is marked_for_deletion, false otherwise, null if project id not found
	 * @throws Exception
	 */
	public Boolean isProjectMarkedForDeletion( int projectId ) throws Exception {
		
		Boolean isProjectMarkedForDeletion = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT marked_for_deletion FROM project WHERE id = ?";
		
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			
			pstmt = conn.prepareStatement( sql );

			pstmt.setInt( 1, projectId );
			
			rs = pstmt.executeQuery();
			
			if ( rs.next() ) {
				
				isProjectMarkedForDeletion = rs.getBoolean( "marked_for_deletion" );
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

		return isProjectMarkedForDeletion;
	}
	
}
