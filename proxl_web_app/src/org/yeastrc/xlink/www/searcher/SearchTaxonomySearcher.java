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
	
	//  SQL for taxonomy ids for all link types, including unlinked and dimer
//	private static final String SINGLE_SEARCH_SQL =
//	
//	"SELECT DISTINCT annotation.taxonomy "
//	+ " FROM annotation "
//	+ " INNER JOIN search_protein_sequence_annotation AS spsa "
//	+ " ON annotation.id = spsa.annotation_id "
//	+ " WHERE spsa.search_id = ? "
	
	//  SQL for taxonomy ids for cross links and looplinks
	private static final String SINGLE_SEARCH_SQL =
			"SELECT   annotation.taxonomy "
			+ " FROM annotation "
			+ " INNER JOIN search_protein_sequence_annotation AS spsa "
			+ 	" ON annotation.id = spsa.annotation_id "
			+ " INNER JOIN srch_rep_pept__prot_seq_id_pos_crosslink AS srpppsipc "
			+	" ON spsa.protein_sequence_id = srpppsipc.protein_sequence_id "
			+ " WHERE spsa.search_id = ? AND srpppsipc.search_id = ? "
			+ " UNION DISTINCT "
			+ "SELECT   annotation.taxonomy "
			+ " FROM annotation "
			+ " INNER JOIN search_protein_sequence_annotation AS spsa "
			+ 	" ON annotation.id = spsa.annotation_id "
			+ " INNER JOIN srch_rep_pept__prot_seq_id_pos_looplink AS srpppsipl "
			+	" ON spsa.protein_sequence_id = srpppsipl.protein_sequence_id "
			+ " WHERE spsa.search_id = ? AND srpppsipl.search_id = ? "
			;

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
			int paramCounter = 0;
			paramCounter++;
			pstmt.setInt( paramCounter, search.getSearchId() );
			paramCounter++;
			pstmt.setInt( paramCounter, search.getSearchId() );
			paramCounter++;
			pstmt.setInt( paramCounter, search.getSearchId() );
			paramCounter++;
			pstmt.setInt( paramCounter, search.getSearchId() );
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
	
	//  SQL for taxonomy ids for all link types, including unlinked and dimer
//	private static final String MULTIPLE_SEARCHES_SQL =
//			"SELECT DISTINCT  annotation.taxonomy "
//					+ " FROM  annotation "
//					+ " INNER JOIN search_protein_sequence_annotation AS spsa "
//					+ " ON annotation.id = spsa.annotation_id "
//			+ " WHERE spsa.search_id IN (#SEARCHES#) ";
	
	//  SQL for taxonomy ids for cross links and looplinks
	private static final String MULTIPLE_SEARCHES_SQL =
			"SELECT   annotation.taxonomy "
			+ " FROM annotation "
			+ " INNER JOIN search_protein_sequence_annotation AS spsa "
			+ 	" ON annotation.id = spsa.annotation_id "
			+ " INNER JOIN srch_rep_pept__prot_seq_id_pos_crosslink AS srpppsipc "
			+	" ON spsa.protein_sequence_id = srpppsipc.protein_sequence_id "
			+ " WHERE spsa.search_id IN (#SEARCHES#) AND srpppsipc.search_id IN (#SEARCHES#) "
			+ " UNION DISTINCT "
			+ "SELECT   annotation.taxonomy "
			+ " FROM annotation "
			+ " INNER JOIN search_protein_sequence_annotation AS spsa "
			+ 	" ON annotation.id = spsa.annotation_id "
			+ " INNER JOIN srch_rep_pept__prot_seq_id_pos_looplink AS srpppsipl "
			+	" ON spsa.protein_sequence_id = srpppsipl.protein_sequence_id "
			+ " WHERE spsa.search_id IN (#SEARCHES#) AND srpppsipl.search_id IN (#SEARCHES#) "
			;
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
			searchIds.add( search.getSearchId() );
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
