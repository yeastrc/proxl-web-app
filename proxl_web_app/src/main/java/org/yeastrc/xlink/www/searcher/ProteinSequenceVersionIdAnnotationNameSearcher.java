package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.objects.ProteinSequenceVersionIdProteinAnnotationName;

/**
 * Find Protein Sequence Id and Annotation (Protein) Name for Search Id
 */
public class ProteinSequenceVersionIdAnnotationNameSearcher {

	private static final Logger log = LoggerFactory.getLogger( ProteinSequenceVersionIdAnnotationNameSearcher.class);
	private ProteinSequenceVersionIdAnnotationNameSearcher() { }
	private static final ProteinSequenceVersionIdAnnotationNameSearcher _INSTANCE = new ProteinSequenceVersionIdAnnotationNameSearcher();
	public static ProteinSequenceVersionIdAnnotationNameSearcher getInstance() { return _INSTANCE; }
	
	private static final String SQL = 
			"SELECT annotation.name, spsva.protein_sequence_version_id " //  Removed DISTINCT after Select since return Set 
			+ " FROM search__protein_sequence_version__annotation AS spsva "
			+ " INNER JOIN annotation on spsva.annotation_id = annotation.id "
			+ " WHERE spsva.search_id = ? ";
	/**
	 * @param searchId
	 * @return
	 * @throws Exception
	 */
	public Set<ProteinSequenceVersionIdProteinAnnotationName> getProteinSequenceVersionIdAnnotationNameForSearch( int searchId ) throws Exception {
		Set<ProteinSequenceVersionIdProteinAnnotationName> results = new HashSet<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = SQL;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, searchId );
			rs = pstmt.executeQuery();
			while( rs.next() ) {
				ProteinSequenceVersionIdProteinAnnotationName item = new ProteinSequenceVersionIdProteinAnnotationName();
				item.setProteinSequenceVersionId( rs.getInt( "protein_sequence_version_id" ) );
				item.setAnnotationName( rs.getString( "name" ) );
				results.add( item );
			}
		} catch ( Exception e ) {
			String msg = "getProteinSequenceVersionIdAnnotationNameForSearch(), sql: " + sql;
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
		return results;
	}
}
