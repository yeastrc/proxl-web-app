package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.yeastrc.xlink.db.DBConnectionFactory;

public class SearchLinkerSearcher {
	
	private SearchLinkerSearcher() { }
	private static final SearchLinkerSearcher _INSTANCE = new SearchLinkerSearcher();
	public static SearchLinkerSearcher getInstance() { return _INSTANCE; }
	
	/**
	 * @param search
	 * @return
	 * @throws Exception
	 */
	public List<Integer> getLinkerIdsForSearch( int searchId ) throws Exception {
		
		List<Integer> resultList = new ArrayList<Integer>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			String sql = "SELECT linker_id FROM search_linker WHERE search_id = ?";
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, searchId );
			rs = pstmt.executeQuery();
			while( rs.next() ) {
				resultList.add(  rs.getInt( 1 ) );
			}
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
		
		return resultList;
	}
}
