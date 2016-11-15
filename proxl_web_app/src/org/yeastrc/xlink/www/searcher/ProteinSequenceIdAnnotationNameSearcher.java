package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.objects.ProteinSequenceIdProteinAnnotationName;

/**
 * Find Protein Sequence Id and Annotation (Protein) Name for Search Id
 */
public class ProteinSequenceIdAnnotationNameSearcher {

	private static final Logger log = Logger.getLogger(ProteinSequenceIdAnnotationNameSearcher.class);
	
	private ProteinSequenceIdAnnotationNameSearcher() { }
	private static final ProteinSequenceIdAnnotationNameSearcher _INSTANCE = new ProteinSequenceIdAnnotationNameSearcher();
	public static ProteinSequenceIdAnnotationNameSearcher getInstance() { return _INSTANCE; }
	

	private static final String SQL = 
			"SELECT DISTINCT annotation.name, spsa.protein_sequence_id "
			+ " FROM search_protein_sequence_annotation AS spsa "
			+ " INNER JOIN annotation on spsa.annotation_id = annotation.id "
			+ " WHERE spsa.search_id = ? ";

	
	/**
	 * @param searchId
	 * @return
	 * @throws Exception
	 */
	public List<ProteinSequenceIdProteinAnnotationName> getProteinSequenceIdAnnotationNameForSearch( int searchId ) throws Exception {

		List<ProteinSequenceIdProteinAnnotationName> results = new ArrayList<>();
		
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
				ProteinSequenceIdProteinAnnotationName item = new ProteinSequenceIdProteinAnnotationName();
				item.setProteinSequenceId( rs.getInt( "protein_sequence_id" ) );
				item.setAnnotationName( rs.getString( "name" ) );
				results.add( item );
			}
			
		} catch ( Exception e ) {
			
			String msg = "getProteinSequenceIdAnnotationNameForSearch(), sql: " + sql;
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
