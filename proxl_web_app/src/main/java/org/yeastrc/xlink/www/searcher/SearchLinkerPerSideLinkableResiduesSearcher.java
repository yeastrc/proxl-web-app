package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * For Search Id:
 * 
 * search_linker_tbl.id AS search_linker_id,
 * search_linker_per_side_definition_tbl.id AS side_id, 
 * search_linker_per_side_linkable_residues_tbl.residue
 *
 */
public class SearchLinkerPerSideLinkableResiduesSearcher {
	
	private SearchLinkerPerSideLinkableResiduesSearcher() { }
	private static final SearchLinkerPerSideLinkableResiduesSearcher _INSTANCE = new SearchLinkerPerSideLinkableResiduesSearcher();
	public static SearchLinkerPerSideLinkableResiduesSearcher getInstance() { return _INSTANCE; }
	
	public static class SearchLinkerPerSideLinkableResiduesSearcher_ResponseItem {
		public int searchLinkerId;
		public int sideId;
		public String residue;
	}
	
	private static final String SQL =
			
			"SELECT sl.id AS search_linker_id, slpsd.id AS side_id, slpslr.residue" 
			+ " FROM search_linker_tbl AS sl" 
			+ " INNER JOIN search_linker_per_side_definition_tbl AS slpsd ON sl.id = slpsd.search_linker_id" 
			+ " INNER JOIN search_linker_per_side_linkable_residues_tbl AS slpslr ON slpsd.id = slpslr.search_linker_per_side_definition_id" 
			+ " WHERE sl.search_id = ?";
	
	/**
	 * @param search
	 * @return
	 * @throws Exception
	 */
	public List<SearchLinkerPerSideLinkableResiduesSearcher_ResponseItem> getPerSideLinkableResidesForSearch( int searchId ) throws Exception {
		
		List<SearchLinkerPerSideLinkableResiduesSearcher_ResponseItem> resultList = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( SQL );
			pstmt.setInt( 1, searchId );
			rs = pstmt.executeQuery();
			while( rs.next() ) {
				SearchLinkerPerSideLinkableResiduesSearcher_ResponseItem item = new SearchLinkerPerSideLinkableResiduesSearcher_ResponseItem();
				item.searchLinkerId =rs.getInt( "search_linker_id" );
				item.sideId =rs.getInt( "side_id" );
				item.residue = rs.getString( "residue" );
				resultList.add( item );
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
