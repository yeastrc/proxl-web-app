package org.yeastrc.xlink.searchers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.xlink.dao.LinkerDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.LinkerDTO;
import org.yeastrc.xlink.dto.SearchDTO;

public class SearchLinkerSearcher {

	private SearchLinkerSearcher() { }
	private static final SearchLinkerSearcher _INSTANCE = new SearchLinkerSearcher();
	public static SearchLinkerSearcher getInstance() { return _INSTANCE; }
	
	public List<LinkerDTO> getLinkersForSearch( SearchDTO search ) throws Exception {
		List<LinkerDTO> comments = new ArrayList<LinkerDTO>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			String sql = "SELECT linker_id FROM search_linker WHERE search_id = ?";
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, search.getId() );
			
			rs = pstmt.executeQuery();
			while( rs.next() ) {
				comments.add( LinkerDAO.getInstance().getLinkerDTOForId( rs.getInt( 1 ) ) );
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
