package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.objects.SearchProgramDisplay;

/**
 * Return a list of all search search program entries for the search id
 * 
 *
 */
public class SearchProgramDisplaySearcher {

	private static final Log log = LogFactory.getLog(SearchProgramDisplaySearcher.class);
	
	private SearchProgramDisplaySearcher() { }
	private static final SearchProgramDisplaySearcher _INSTANCE = new SearchProgramDisplaySearcher();
	public static SearchProgramDisplaySearcher getInstance() { return _INSTANCE; }
	
	
	
	
	

	private final String SQL = "SELECT display_name , version "
			
			+ "FROM search_programs_per_search "

		+ " WHERE search_id = ? "

		+ " ORDER BY  display_name, version ";

	/**
	 * @param searchId
	 * @return
	 * @throws Exception
	 */
	public List<SearchProgramDisplay>  getSearchProgramDisplay( int searchId ) throws Exception {
		
		
		List<SearchProgramDisplay> results = new ArrayList<SearchProgramDisplay>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = SQL;

		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			
			pstmt = conn.prepareStatement( sql );
			
			pstmt.setInt( 1, searchId );
			
			rs = pstmt.executeQuery();

			while( rs.next() ) {
				SearchProgramDisplay item = new SearchProgramDisplay();

				item.setDisplayName( rs.getString( "display_name" ) );
				item.setVersion( rs.getString( "version" ) );
				
				results.add( item );
			}
			
		} catch ( Exception e ) {
			
			String msg = "getSearchProgramDisplay(), sql: " + sql;
			
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
		
		
		
		return results;
	}
	
	
	
	
	
	
	
}
