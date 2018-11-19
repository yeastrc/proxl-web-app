package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
/**
 *
 */
public class SearchIdForProjectSearchIdSearcher {
	
	private static final Logger log = Logger.getLogger(SearchIdForProjectSearchIdSearcher.class);
	private SearchIdForProjectSearchIdSearcher() { }
	private static final SearchIdForProjectSearchIdSearcher _INSTANCE = new SearchIdForProjectSearchIdSearcher();
	public static SearchIdForProjectSearchIdSearcher getInstance() { return _INSTANCE; }
	
	/**
	 * @param projectSearchId
	 * @return
	 * @throws Exception
	 */
	public Integer getSearchIdForProjectSearchId( int projectSearchId ) throws Exception {
		Integer result = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "SELECT search_id FROM project_search WHERE id = ? ";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, projectSearchId );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				result = rs.getInt( "search_id" );
			}
		} catch ( Exception e ) {
			String msg = "getSearchIdForProjectSearchId(...), sql: " + sql;
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
		return result;
	}
	}
