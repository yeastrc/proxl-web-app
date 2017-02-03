package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.enum_classes.SearchRecordStatus;

/**

 *
 */
public class ProjectSearchIdsForSearchIdSearcher {

	private static final Logger log = Logger.getLogger(ProjectSearchIdsForSearchIdSearcher.class);

	private ProjectSearchIdsForSearchIdSearcher() { }
	private static final ProjectSearchIdsForSearchIdSearcher _INSTANCE = new ProjectSearchIdsForSearchIdSearcher();
	public static ProjectSearchIdsForSearchIdSearcher getInstance() { return _INSTANCE; }

	private static final String getProjectSearchIdsForSearchIdSQL =
			"SELECT id FROM project_search "
			+ " WHERE search_id = ? AND status_id = " + SearchRecordStatus.IMPORT_COMPLETE_VIEW.value();
	
	/**
	 * @param projectSearchId
	 * @return
	 * @throws Exception
	 */
	public List<Integer> getProjectSearchIdsForSearchId( int searchId ) throws Exception {

		List<Integer> resultList = new ArrayList<>();

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = getProjectSearchIdsForSearchIdSQL;
		
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, searchId );
			rs = pstmt.executeQuery();

			while( rs.next() ) {
				resultList.add( rs.getInt( "id" ) );
			}

		} catch ( Exception e ) {
			String msg = "getProjectSearchIdForSearchId(...), sql: " + sql;
			log.error( msg, e );
			throw e;
		} finally {
			// be sure database handles are closed
			if( rs != null ) {
				try { rs.close(); } catch( Throwable t ) { ; }
				rs = null;
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
		return resultList;
	}
}
