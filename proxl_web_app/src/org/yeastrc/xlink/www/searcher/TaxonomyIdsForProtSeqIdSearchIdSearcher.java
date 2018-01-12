package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;

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
					+ " INNER JOIN search__protein_sequence_version__annotation AS spsva "
					+ " ON annotation.id = spsva.annotation_id "
			+ " WHERE spsva.search_id = ? AND spsva.protein_sequence_version_id = ? ";
	
	/**
	 * Get the taxonomy ids For the protein sequence version id and search id
	 * 
	 * @param proteinSequenceVersionId
	 * @param searchId
	 * @return
	 * @throws Exception
	 */
	public Set<Integer> getTaxonomyIdsSingleSearch( int proteinSequenceVersionId, int searchId ) throws Exception {
		
		Set<Integer> taxonomyIds = new HashSet<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = SINGLE_SEARCH_SQL;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, searchId );
			pstmt.setInt( 2, proteinSequenceVersionId );
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
	
//	private static final String MULTIPLE_SEARCHES_SQL =
//			"SELECT DISTINCT  annotation.taxonomy "
//					+ " FROM annotation "
//					+ " INNER JOIN search__protein_sequence_version__annotation AS spsva "
//					+ " ON annotation.id = spsva.annotation_id "
//			+ " WHERE spsa.search_id IN (#SEARCHES#)  AND spsva.protein_sequence_version_id = ? ";

//	/**
//	 * Get the taxonomy ids For the protein sequence id and search id
//	 * 
//	 * @param proteinSequenceVersionObject
//	 * @param searchId
//	 * @return
//	 * @throws Exception
//	 */
//	public Set<Integer> getTaxonomyIdsMultipleSearches( SSSS ssss, Collection<Integer> searchIds ) throws Exception {
//		
//		Set<Integer> taxonomyIds = new HashSet<>();
//		Connection conn = null;
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//		String sql = MULTIPLE_SEARCHES_SQL.replaceAll( "#SEARCHES#", StringUtils.join( searchIds, "," ) );
//		try {
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
//			pstmt = conn.prepareStatement( sql );
//			pstmt.setInt( 1, proteinSequenceVersionObject.getProteinSequenceVersionId() );
//			rs = pstmt.executeQuery();
//			while( rs.next() ) {
//				taxonomyIds.add( rs.getInt( "taxonomy" ) );
//			}
//		} catch ( Exception e ) {
//			String msg = "getTaxonomyIdsMultipleSearches(), sql: " + sql;
//			log.error( msg, e );
//			throw e;
//		} finally {
//			// be sure database handles are closed
//			if( rs != null ) {
//				try { rs.close(); } catch( Throwable t ) { ; }
//				rs = null;
//			}
//			if( pstmt != null ) {
//				try { pstmt.close(); } catch( Throwable t ) { ; }
//				pstmt = null;
//			}
//			if( conn != null ) {
//				try { conn.close(); } catch( Throwable t ) { ; }
//				conn = null;
//			}
//		}
//		return taxonomyIds;
//	}
}
