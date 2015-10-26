package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.utils.TaxonomyUtils;

/**
 * Get the taxonomies present in a percolator search
 * @author Mike
 *
 */
public class SearchTaxonomySearcher {

	private SearchTaxonomySearcher() { }
	public static SearchTaxonomySearcher getInstance() { return new SearchTaxonomySearcher(); }
	
	/**
	 * Get the taxonomies present in the percolator search
	 * @param search The search
	 * @return A map of taxonomy IDs=>name 
	 * @throws Exception
	 */
	public Map<Integer, String> getTaxonomies( SearchDTO search ) throws Exception {
		
		Map<Integer, String> taxonomies = new TreeMap<Integer, String>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = "SELECT DISTINCT a.speciesID FROM YRC_NRSEQ.tblProtein AS a INNER JOIN crosslink AS b ON a.id = b.nrseq_id_1 INNER JOIN psm AS c ON b.psm_id = c.id WHERE c.search_id = ? " +
						 "UNION " +
						 "SELECT DISTINCT a.speciesID FROM YRC_NRSEQ.tblProtein AS a INNER JOIN crosslink AS b ON a.id = b.nrseq_id_1 INNER JOIN psm AS c ON b.psm_id = c.id WHERE c.search_id = ?";
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, search.getId() );
			pstmt.setInt( 2, search.getId() );
			
			rs = pstmt.executeQuery();

			while( rs.next() ) {
				if( !taxonomies.containsKey( rs.getInt( 1 ) ) )
					taxonomies.put( rs.getInt(1), TaxonomyUtils.getTaxonomyName( rs.getInt( 1 ) ) );
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
		
		
		return taxonomies;
		
	}
	
	/**
	 * Get the taxonomies present in the percolator search
	 * @param search The search
	 * @return A map of taxonomy IDs=>name 
	 * @throws Exception
	 */
	public Map<Integer, String> getTaxonomies( Collection<SearchDTO> searches ) throws Exception {
		
		Map<Integer, String> taxonomies = new TreeMap<Integer, String>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = "SELECT DISTINCT a.speciesID FROM YRC_NRSEQ.tblProtein AS a INNER JOIN crosslink AS b ON a.id = b.nrseq_id_1 INNER JOIN psm AS c ON b.psm_id = c.id WHERE c.search_id IN (#SEARCHES#) " +
						 "UNION " +
						 "SELECT DISTINCT a.speciesID FROM YRC_NRSEQ.tblProtein AS a INNER JOIN crosslink AS b ON a.id = b.nrseq_id_1 INNER JOIN psm AS c ON b.psm_id = c.id WHERE c.search_id IN (#SEARCHES#)";

			Collection<Integer> searchIds = new HashSet<Integer>();
			for( SearchDTO search : searches )
				searchIds.add( search.getId() );
			sql = sql.replaceAll( "#SEARCHES#", StringUtils.join( searchIds, "," ) );
			
			pstmt = conn.prepareStatement( sql );
			
			rs = pstmt.executeQuery();

			while( rs.next() ) {
				if( !taxonomies.containsKey( rs.getInt( 1 ) ) )
					taxonomies.put( rs.getInt(1), TaxonomyUtils.getTaxonomyName( rs.getInt( 1 ) ) );
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
		
		
		return taxonomies;
		
	}
	
}
