package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.ProteinSequenceAnnotationDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.ProteinSequenceAnnotationDTO;

/**
 * Find relevant ProteinSequenceAnnotationDTO objects
 * 
 * @author mriffle
 *
 */
public class ProteinSequenceAnnotationSearcher {

	private static final Logger log = Logger.getLogger(ProteinSequenceAnnotationSearcher.class);
	private ProteinSequenceAnnotationSearcher() { }
	private static final ProteinSequenceAnnotationSearcher _INSTANCE = new ProteinSequenceAnnotationSearcher();
	public static ProteinSequenceAnnotationSearcher getInstance() { return _INSTANCE; }
	
	private static final String SQL = 
			"SELECT DISTINCT annotation_id FROM search__protein_sequence_version__annotation "
			+ " WHERE search_id IN (#SEARCHES#) AND protein_sequence_version_id = ? "
			+ " ORDER BY annotation_id";

	/**
	 * @param searchIds
	 * @param proteinSequenceVersionId
	 * @return
	 * @throws Exception
	 */
	public Collection<ProteinSequenceAnnotationDTO> getProteinSequenceAnnotationsForSearchAndProtein( Collection<Integer> searchIds, int proteinSequenceVersionId ) throws Exception {
		Collection<ProteinSequenceAnnotationDTO> annotations = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = SQL.replaceAll( "#SEARCHES#", StringUtils.join( searchIds, "," ) );
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, proteinSequenceVersionId );
			rs = pstmt.executeQuery();
			while( rs.next() )
				annotations.add( ProteinSequenceAnnotationDAO.getInstance().getProteinSequenceAnnotationDTOForAnnotationId( rs.getInt( "annotation_id" ) ) );
		} catch ( Exception e ) {
			String msg = "getProteinSequenceAnnotationsForSearchAndProtein(), sql: " + sql;
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
		return annotations;
	}
}
