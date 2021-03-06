package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.yeastrc.xlink.dao.SearchCommentDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchCommentDTO;
import org.yeastrc.xlink.www.dto.SearchDTO;
/**
 * 
 *
 */
public class SearchCommentSearcher {
	
	private SearchCommentSearcher() { }
	private static final SearchCommentSearcher _INSTANCE = new SearchCommentSearcher();
	public static SearchCommentSearcher getInstance() { return _INSTANCE; }
	
	/**
	 * @param search
	 * @return
	 * @throws Exception
	 */
	public List<SearchCommentDTO> getCommentsForSearch( SearchDTO search ) throws Exception {
		List<SearchCommentDTO> comments = new ArrayList<SearchCommentDTO>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			final String sql = "SELECT id FROM search_comment WHERE project_search_id = ? ORDER BY commentTimestamp";
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, search.getProjectSearchId() );
			rs = pstmt.executeQuery();
			while( rs.next() ) {
				comments.add( SearchCommentDAO.getInstance().load( rs.getInt( 1 ) ) );
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
		return comments;
	}
}
