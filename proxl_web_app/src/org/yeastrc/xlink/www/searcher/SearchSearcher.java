package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.enum_classes.SearchRecordStatus;
import org.yeastrc.xlink.www.dto.SearchDTO;

/**
 * Return a list of all searches for the project, ordered by upload date
 * @author Mike
 *
 */
public class SearchSearcher {

	private static final Log log = LogFactory.getLog(SearchSearcher.class);
	
	private SearchSearcher() { }
	private static final SearchSearcher _INSTANCE = new SearchSearcher();
	public static SearchSearcher getInstance() { return _INSTANCE; }
	
	
	
	
	
	private final static String SEARCH_SQL =
			"SELECT project_search.id FROM project_search"

			+ " INNER JOIN project ON project_search.project_id = project.id   "

			+ " WHERE project.id = ? AND project_search.status_id = " + SearchRecordStatus.IMPORT_COMPLETE_VIEW.value()
			+   " AND active_project_id_search_id_unique_record IS NOT NULL "

			+ " ORDER BY project_search.search_display_order , project_search.search_id DESC";

	/**
	 * Return a list of all searches for the project, ordered by upload date
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public List<SearchDTO> getSearchsForProjectId( int projectId ) throws Exception {
		
		
		List<SearchDTO> searches = new ArrayList<SearchDTO>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = SEARCH_SQL;

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			
			pstmt = conn.prepareStatement( sql );
			
			pstmt.setInt( 1, projectId );
			
			rs = pstmt.executeQuery();

			while( rs.next() ) {
				searches.add( SearchDAO.getInstance().getSearchFromProjectSearchId( rs.getInt( 1 ) ) );
			}
			
		} catch ( Exception e ) {
			
			String msg = "getSearchsForProjectId(), sql: " + sql;
			
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
		
		
		
		return searches;
	}
	
	
	
	
	//   Commented out since not maintained
	
//	/**
//	 * @return
//	 * @throws Exception
//	 */
//	public List<SearchDTO> getAllSearchs() throws Exception {
//		
//		
//		List<SearchDTO> searches = new ArrayList<SearchDTO>();
//		
//		Connection conn = null;
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//		try {
//			
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
//			String sql = "SELECT id FROM project_search" +
//					" ORDER BY id DESC";
//			
//			pstmt = conn.prepareStatement( sql );
//			
//			rs = pstmt.executeQuery();
//
//			while( rs.next() )
//				searches.add( SearchDAO.getInstance().getSearchFromProjectSearchId( rs.getInt( 1 ) ) );
//
//			
//		} finally {
//			
//			// be sure database handles are closed
//			if( rs != null ) {
//				try { rs.close(); } catch( Throwable t ) { ; }
//				rs = null;
//			}
//			
//			if( pstmt != null ) {
//				try { pstmt.close(); } catch( Throwable t ) { ; }
//				pstmt = null;
//			}
//			
//			if( conn != null ) {
//				try { conn.close(); } catch( Throwable t ) { ; }
//				conn = null;
//			}
//			
//		}
//		
//		
//		
//		return searches;
//	}
	
}
