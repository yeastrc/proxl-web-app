package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.enum_classes.SearchLinkerProteinTerminusType;

/**
 * For Search Id:
 * 
 * search_linker_tbl.id AS search_linker_id,
 * search_linker_per_side_definition_tbl.id AS side_id, 
 * search_linker_per_side_linkable_protein_termini_tbl.n_terminus_c_terminus
 * search_linker_per_side_linkable_protein_termini_tbl.distance_from_terminus
 *
 */
public class SearchLinkerPerSideLinkableProteinTerminiSearcher {
	
	private SearchLinkerPerSideLinkableProteinTerminiSearcher() { }
	private static final SearchLinkerPerSideLinkableProteinTerminiSearcher _INSTANCE = new SearchLinkerPerSideLinkableProteinTerminiSearcher();
	public static SearchLinkerPerSideLinkableProteinTerminiSearcher getInstance() { return _INSTANCE; }
	
	public static class SearchLinkerPerSideLinkableProteinTerminiSearcher_ResponseItem {
		public int searchLinkerId;
		public int sideId;
		public SearchLinkerProteinTerminusType n_terminus_c_terminus;
		public int distanceFromTerminus; // 0 indicates at the terminus
	}
	
	private static final String SQL =
			
			"SELECT sl.id AS search_linker_id, slpsd.id AS side_id, slpslpt.n_terminus_c_terminus, slpslpt.distance_from_terminus" 
			+ " FROM search_linker_tbl AS sl" 
			+ " INNER JOIN search_linker_per_side_definition_tbl AS slpsd ON sl.id = slpsd.search_linker_id" 
			+ " INNER JOIN search_linker_per_side_linkable_protein_termini_tbl AS slpslpt ON slpsd.id = slpslpt.search_linker_per_side_definition_id" 
			+ " WHERE sl.search_id = ?";
	
	/**
	 * @param search
	 * @return
	 * @throws Exception
	 */
	public List<SearchLinkerPerSideLinkableProteinTerminiSearcher_ResponseItem> getPerSideLinkableProteinTerminiForSearch( int searchId ) throws Exception {
		
		List<SearchLinkerPerSideLinkableProteinTerminiSearcher_ResponseItem> resultList = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( SQL );
			pstmt.setInt( 1, searchId );
			rs = pstmt.executeQuery();
			while( rs.next() ) {
				SearchLinkerPerSideLinkableProteinTerminiSearcher_ResponseItem item = new SearchLinkerPerSideLinkableProteinTerminiSearcher_ResponseItem();
				item.searchLinkerId =rs.getInt( "search_linker_id" );
				item.sideId =rs.getInt( "side_id" );
				String nTermCTermString = rs.getString( "n_terminus_c_terminus" );
				item.n_terminus_c_terminus = SearchLinkerProteinTerminusType.fromValue( nTermCTermString );
				item.distanceFromTerminus =rs.getInt( "distance_from_terminus" );
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
