package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.objects.ReportedPeptideIdsForSearchIdsUnifiedPeptideIdResult;

public class ReportedPeptideIdsForSearchIdsUnifiedPeptideIdSearcher {
	
	private static final Logger log = Logger.getLogger(ReportedPeptideIdsForSearchIdsUnifiedPeptideIdSearcher.class);

	private ReportedPeptideIdsForSearchIdsUnifiedPeptideIdSearcher() { }
	public static ReportedPeptideIdsForSearchIdsUnifiedPeptideIdSearcher getInstance() { return new ReportedPeptideIdsForSearchIdsUnifiedPeptideIdSearcher(); }

	/**
	 * Get a list of reported peptide id, search id pairs for the collection of search ids and unified reported peptide id
	 * @param searchIds
	 * @param unifiedReportedPeptideId
	 * @return
	 * @throws Exception
	 */
	public List<ReportedPeptideIdsForSearchIdsUnifiedPeptideIdResult> getReportedPeptideIdsForSearchIdsAndUnifiedReportedPeptideId( Collection<Integer> searchIds, int unifiedReportedPeptideId ) throws Exception {

		List<ReportedPeptideIdsForSearchIdsUnifiedPeptideIdResult>  resultList = new ArrayList<>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		
		final String sqlMain = " SELECT reported_peptide_id, search_id FROM unified_rp__rep_pept__search__generic_lookup "
				 + " WHERE unified_reported_peptide_id = ? AND search_id IN (";
		
		final String sqlEnd = ")  ";
		
		String sqlSearchIdsString = null;
		
		for ( Integer searchId : searchIds ) {
			
			if ( sqlSearchIdsString == null ) {
				
				sqlSearchIdsString = searchId.toString();
			} else {
				
				sqlSearchIdsString += "," + searchId.toString();
			}
		}
		
		final String sql = sqlMain + sqlSearchIdsString + sqlEnd;
		
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			
			pstmt.setInt( 1, unifiedReportedPeptideId );
			
			rs = pstmt.executeQuery();
			
			while ( rs.next() ) {
				
				ReportedPeptideIdsForSearchIdsUnifiedPeptideIdResult result = new ReportedPeptideIdsForSearchIdsUnifiedPeptideIdResult();
				
				result.setSearchId( rs.getInt( "search_id" ) );
				result.setReportedPeptideId( rs.getInt( "reported_peptide_id" ) );
				
				resultList.add( result );
				
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
