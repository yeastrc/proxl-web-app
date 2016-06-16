package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yeastrc.xlink.base.constants.Database_OneTrueZeroFalse_Constants;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.dto.SearchDTO;

/**
 * Return a list of all searches in the database, ordered by upload date
 * @author Mike
 *
 */
public class SearchSearcher {

	private static final Log log = LogFactory.getLog(SearchSearcher.class);
	
	private SearchSearcher() { }
	private static final SearchSearcher _INSTANCE = new SearchSearcher();
	public static SearchSearcher getInstance() { return _INSTANCE; }
	
	
	
	
	

	

	/**
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public List<SearchDTO> getSearchsForProjectId( int projectId ) throws Exception {
		
		
		List<SearchDTO> searches = new ArrayList<SearchDTO>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = "SELECT search.id FROM search"

			+ " INNER JOIN project ON search.project_id = project.id   "

			+ " WHERE project.id = ? AND insert_complete = " + Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE

			+ " ORDER BY search.display_order , search.id DESC";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			
			pstmt = conn.prepareStatement( sql );
			
			pstmt.setInt( 1, projectId );
			
			rs = pstmt.executeQuery();

			while( rs.next() )
				searches.add( SearchDAO.getInstance().getSearch( rs.getInt( 1 ) ) );
			
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
	
	
	
	
	
	
	/**
	 * @return
	 * @throws Exception
	 */
	public List<SearchDTO> getAllSearchs() throws Exception {
		
		
		List<SearchDTO> searches = new ArrayList<SearchDTO>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			String sql = "SELECT id FROM search" +
					" ORDER BY id DESC";
			
			pstmt = conn.prepareStatement( sql );
			
			rs = pstmt.executeQuery();

			while( rs.next() )
				searches.add( SearchDAO.getInstance().getSearch( rs.getInt( 1 ) ) );

			
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
	
}
