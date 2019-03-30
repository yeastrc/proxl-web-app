package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * 
 *
 */
public class SearchUtils {
	
	private static final Logger log = LoggerFactory.getLogger( SearchUtils.class);
	
	private static final String NAME_SQL =
			"SELECT DISTINCT  annotation.name "
					+ " FROM annotation "
					+ " INNER JOIN search__protein_sequence_version__annotation AS spsva "
					+ " ON annotation.id = spsva.annotation_id "
			+ " WHERE spsva.search_id = ? AND spsva.protein_sequence_version_id = ? ";
	/**
	 * Get the name for this protein in the context of its search 
	 * @param searchId
	 * @param proteinSequenceVersionId
	 * @return
	 * @throws Exception
	 */
	public static String getProteinNameForSearchIdProteinSequenceVersionId( int searchId, int proteinSequenceVersionId ) throws Exception {
		String result = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = NAME_SQL;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, searchId );
			pstmt.setInt( 2, proteinSequenceVersionId );
			rs = pstmt.executeQuery();
			StringBuilder resultsSB = new StringBuilder( 1000 );
			while( rs.next() ) {
				if ( resultsSB.length() > 0 ) {
					resultsSB.append( "," );
				}
				resultsSB.append( rs.getString( "name" ) );
			}
			if ( resultsSB.length() > 0 ) {
				result = resultsSB.toString();
			}
		} catch ( Exception e ) {
			log.error( "ERROR getting protein name for search.  sql: " + sql, e );
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
		return result;		
	}
	
	private static final String DESCRIPTION_SQL =
			"SELECT DISTINCT  annotation.description "
					+ " FROM annotation "
					+ " INNER JOIN search__protein_sequence_version__annotation AS spsva "
					+ " ON annotation.id = spsva.annotation_id "
			+ " WHERE spsva.search_id = ? AND spsva.protein_sequence_version_id = ? ";
	/**
	 * Get the description for this protein in the context of its search (based on
	 * the FASTA file used to do the search.
	 * @param searchId
	 * @param proteinSequenceVersionId
	 * @return
	 * @throws Exception
	 */
	public static String getProteinDescriptionForSearchIdproteinSequenceVersionId( int searchId, int proteinSequenceVersionId ) throws Exception {
		String result = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = DESCRIPTION_SQL;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, searchId );
			pstmt.setInt( 2, proteinSequenceVersionId );
			rs = pstmt.executeQuery();
			StringBuilder resultsSB = new StringBuilder( 1000 );
			while( rs.next() ) {
				if ( resultsSB.length() > 0 ) {
					resultsSB.append( ", " );
				}
				resultsSB.append( rs.getString( "name" ) );
			}
			if ( resultsSB.length() > 0 ) {
				result = resultsSB.toString();
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
		if( result == null || result.equals( "" ) )
			result = "No description in FASTA.";
		return result;		
	}
}
