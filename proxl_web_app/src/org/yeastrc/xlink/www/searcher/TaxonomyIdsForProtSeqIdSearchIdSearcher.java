package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.objects.ProteinSequenceObject;

/**
 * 
 *
 */
public class TaxonomyIdsForProtSeqIdSearchIdSearcher {

	private static final Logger log = Logger.getLogger( TaxonomyIdsForProtSeqIdSearchIdSearcher.class );
	private TaxonomyIdsForProtSeqIdSearchIdSearcher() { }
	public static TaxonomyIdsForProtSeqIdSearchIdSearcher getInstance() { return new TaxonomyIdsForProtSeqIdSearchIdSearcher(); }
	
	private static final String SINGLE_SEARCH_SQL =
			"SELECT DISTINCT  annotation.taxonomy "
					+ " FROM annotation "
					+ " INNER JOIN search_protein_sequence_annotation AS spsa "
					+ " ON annotation.id = spsa.annotation_id "
			+ " WHERE spsa.search_id = ? AND spsa.protein_sequence_id = ? ";
	
	/**
	 * Get the taxonomy ids For the protein sequence id and search id
	 * 
	 * @param proteinSequenceId
	 * @param searchId
	 * @return
	 * @throws Exception
	 */
	public Set<Integer> getTaxonomyIdsSingleSearch( int proteinSequenceId, int searchId ) throws Exception {
		
		Set<Integer> taxonomyIds = new HashSet<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = SINGLE_SEARCH_SQL;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, searchId );
			pstmt.setInt( 2, proteinSequenceId );
			rs = pstmt.executeQuery();
			while( rs.next() ) {
				taxonomyIds.add( rs.getInt( "taxonomy" ) );
			}
		} catch ( Exception e ) {
			String msg = "getTaxonomyIdsSingleSearch(), sql: " + sql;
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
	
	private static final String MULTIPLE_SEARCHES_SQL =
			"SELECT DISTINCT  annotation.taxonomy "
					+ " FROM annotation "
					+ " INNER JOIN search_protein_sequence_annotation AS spsa "
					+ " ON annotation.id = spsa.annotation_id "
			+ " WHERE spsa.search_id IN (#SEARCHES#)  AND spsa.protein_sequence_id = ? ";

	/**
	 * Get the taxonomy ids For the protein sequence id and search id
	 * 
	 * @param proteinSequenceObject
	 * @param searchId
	 * @return
	 * @throws Exception
	 */
	public Set<Integer> getTaxonomyIdsMultipleSearches( ProteinSequenceObject proteinSequenceObject, Collection<Integer> searchIds ) throws Exception {
		
		Set<Integer> taxonomyIds = new HashSet<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = MULTIPLE_SEARCHES_SQL.replaceAll( "#SEARCHES#", StringUtils.join( searchIds, "," ) );
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, proteinSequenceObject.getProteinSequenceId() );
			rs = pstmt.executeQuery();
			while( rs.next() ) {
				taxonomyIds.add( rs.getInt( "taxonomy" ) );
			}
		} catch ( Exception e ) {
			String msg = "getTaxonomyIdsMultipleSearches(), sql: " + sql;
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
