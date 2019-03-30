package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * 
 *
 */
public class ReportedPeptideIdsForSearchIdsUnifiedPeptideIdSearcher {
	
	private static final Logger log = LoggerFactory.getLogger( ReportedPeptideIdsForSearchIdsUnifiedPeptideIdSearcher.class);
	private ReportedPeptideIdsForSearchIdsUnifiedPeptideIdSearcher() { }
	public static ReportedPeptideIdsForSearchIdsUnifiedPeptideIdSearcher getInstance() { return new ReportedPeptideIdsForSearchIdsUnifiedPeptideIdSearcher(); }
	
	private static final String getReportedPeptideIdsForSearchIdsAndUnifiedReportedPeptideIdSQL = 
			" SELECT reported_peptide_id FROM unified_rp__search__rep_pept__generic_lookup "
			 + " WHERE unified_reported_peptide_id = ? AND search_id = ?";
	/**
	 * Get a list of reported peptide id for the search id and unified reported peptide id
	 * @param searchId
	 * @param unifiedReportedPeptideId
	 * @return - list of reported peptide id
	 * @throws Exception
	 */
	public List<Integer> getReportedPeptideIdsForSearchIdsAndUnifiedReportedPeptideId( int searchId, int unifiedReportedPeptideId ) throws Exception {
		List<Integer>  resultList = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = getReportedPeptideIdsForSearchIdsAndUnifiedReportedPeptideIdSQL;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, unifiedReportedPeptideId );
			pstmt.setInt( 2, searchId );
			rs = pstmt.executeQuery();
			while ( rs.next() ) {
				resultList.add( rs.getInt( "reported_peptide_id" ) );
			}
		} catch ( Exception e ) {
			log.error( "ERROR: sql: " + sql, e );
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
		return resultList;
	}
}
