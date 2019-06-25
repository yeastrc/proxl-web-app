package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * Query project_level_default_fltr_min_psms_tbl by project id
 *
 */
public class ProjectLevelDefaultFltr_MinPSMs_Searcher {

	private static final Logger log = LoggerFactory.getLogger( ProjectLevelDefaultFltr_MinPSMs_Searcher.class);
	private ProjectLevelDefaultFltr_MinPSMs_Searcher() { }
	private static final ProjectLevelDefaultFltr_MinPSMs_Searcher _INSTANCE = new ProjectLevelDefaultFltr_MinPSMs_Searcher();
	public static ProjectLevelDefaultFltr_MinPSMs_Searcher getInstance() { return _INSTANCE; }
	

	private static final String SQL = 
			"SELECT min_psms "
			+ " FROM project_level_default_fltr_min_psms_tbl "
			+ " WHERE project_id = ?";

	/**
	 * @param projectId
	 * @return 
	 * @throws Exception
	 */
	public Integer getMinPSMsForProjectId( int projectId ) throws Exception {
		
		Integer result = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = SQL;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, projectId );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				result = rs.getInt( "min_psms" );
			}
		} catch ( Exception e ) {
			String msg = "Failed getMinPSMsForProjectId(...), projectId: " + projectId + ", sql: " + sql;
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

}
