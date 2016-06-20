package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.web_utils.TaxonomyUtils;

/**
 * Get the taxonomies present in a search
 * @author Mike
 *
 */
public class SearchTaxonomySearcher {
	
	private static final Logger log = Logger.getLogger( SearchTaxonomySearcher.class );

	private SearchTaxonomySearcher() { }
	public static SearchTaxonomySearcher getInstance() { return new SearchTaxonomySearcher(); }
	
	
	private static final String SINGLE_SEARCH_SQL =
			"SELECT DISTINCT  tblProtein.speciesID "
			+ " FROM YRC_NRSEQ.tblProtein AS tblProtein"
			+ " INNER JOIN srch_rep_pept__nrseq_id_pos_crosslink AS srpnipc "
			+ 	" ON tblProtein.id = srpnipc.nrseq_id "
			+ " WHERE srpnipc.search_id = ? ";
	
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
		
		final String sql = SINGLE_SEARCH_SQL;
		
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, search.getId() );
			
			rs = pstmt.executeQuery();

			while( rs.next() ) {
				if( !taxonomies.containsKey( rs.getInt( 1 ) ) )
					taxonomies.put( rs.getInt(1), TaxonomyUtils.getTaxonomyName( rs.getInt( 1 ) ) );
			}

		} catch ( Exception e ) {
			
			String msg = "getTaxonomies(), sql: " + sql;
			
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
		
		
		return taxonomies;
		
	}
	

	private static final String MULTIPLE_SEARCHES_SQL =
			"SELECT  tblProtein.speciesID "
			+ " FROM YRC_NRSEQ.tblProtein AS tblProtein"
			+ " INNER JOIN srch_rep_pept__nrseq_id_pos_crosslink AS srpnipc "
			+ 	" ON tblProtein.id = srpnipc.nrseq_id "
			+ " WHERE srpnipc.search_id  IN (#SEARCHES#) ";
	
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

		Collection<Integer> searchIds = new HashSet<Integer>();
		for( SearchDTO search : searches ) {
			searchIds.add( search.getId() );
		}
		String sql = MULTIPLE_SEARCHES_SQL.replaceAll( "#SEARCHES#", StringUtils.join( searchIds, "," ) );

		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			pstmt = conn.prepareStatement( sql );
			
			rs = pstmt.executeQuery();

			while( rs.next() ) {
				
				Integer taxonomyId = rs.getInt( 1 );
				
				if( !taxonomies.containsKey( taxonomyId ) ) {
					taxonomies.put( taxonomyId, TaxonomyUtils.getTaxonomyName( taxonomyId ) );
				}
			}

		} catch ( Exception e ) {
			
			String msg = "getTaxonomies(), sql: " + sql;
			
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
		
		
		return taxonomies;
		
	}
	
}
