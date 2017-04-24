package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * Get the taxonomies present in a search
 * @author Mike
 *
 */
public class SearchTaxonomySearcher {
	
	private static final Logger log = Logger.getLogger( SearchTaxonomySearcher.class );
	private SearchTaxonomySearcher() { }
	public static SearchTaxonomySearcher getInstance() { return new SearchTaxonomySearcher(); }
	
	//  SQL for taxonomy ids for cross links and looplinks
	
	private static final String SINGLE_SEARCH_CROSSLINKS_LOOPLINKS_SQL =
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
	 * Get the taxonomies present in the search
	 * @param searchId
	 * @return A map of taxonomy IDs=>name 
	 * @throws Exception
	 */
	public Set<Integer> getTaxonomyIdsForSearchId( int searchId ) throws Exception {
		
		Set<Integer> taxonomyIds = new HashSet<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = SINGLE_SEARCH_CROSSLINKS_LOOPLINKS_SQL;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			int paramCounter = 0;
			paramCounter++;
			pstmt.setInt( paramCounter, searchId );
			paramCounter++;
			pstmt.setInt( paramCounter, searchId );
			paramCounter++;
			pstmt.setInt( paramCounter, searchId );
			paramCounter++;
			pstmt.setInt( paramCounter, searchId );
			
			rs = pstmt.executeQuery();
			while( rs.next() ) {
				taxonomyIds.add( rs.getInt( 1 ) );
			}
		} catch ( Exception e ) {
			String msg = "getTaxonomyIdsForSearchId(), sql: " + sql;
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
		return taxonomyIds;
	}
	
	//  SQL for taxonomy ids for all link types, including unlinked and dimer
	private static final String SINGLE_SEARCH_ALL_LINK_TYPES_SQL =
			"SELECT DISTINCT  annotation.taxonomy "
					+ " FROM  annotation "
					+ " INNER JOIN search_protein_sequence_annotation AS spsa "
					+ " ON annotation.id = spsa.annotation_id "
			+ " WHERE spsa.search_id = ? ";
	
	/**
	 * Get the taxonomies present in the percolator search
	 * @param search The search
	 * @return A map of taxonomy IDs=>name 
	 * @throws Exception
	 */
	public Set<Integer> getTaxonomiesForAllLinkTypes( int searchId ) throws Exception {
		Set<Integer> taxonomyIds = new HashSet<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = SINGLE_SEARCH_ALL_LINK_TYPES_SQL;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			int paramCounter = 0;
			paramCounter++;
			pstmt.setInt( paramCounter, searchId );
			rs = pstmt.executeQuery();
			while( rs.next() ) {
				taxonomyIds.add( rs.getInt( "taxonomy" ) );
			}
		} catch ( Exception e ) {
			String msg = "getTaxonomiesForAllLinkTypes(), sql: " + sql;
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
		return taxonomyIds;
	}
}
