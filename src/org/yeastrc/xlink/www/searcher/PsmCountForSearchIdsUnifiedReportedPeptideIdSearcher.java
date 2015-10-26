package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * 
 *
 */
public class PsmCountForSearchIdsUnifiedReportedPeptideIdSearcher {

	private PsmCountForSearchIdsUnifiedReportedPeptideIdSearcher() { }
	public static PsmCountForSearchIdsUnifiedReportedPeptideIdSearcher getInstance() { return new PsmCountForSearchIdsUnifiedReportedPeptideIdSearcher(); }

	private static final Logger log = Logger.getLogger(PsmCountForSearchIdsUnifiedReportedPeptideIdSearcher.class);
	

	
	private static final String SQL = 
			"SELECT COUNT(ID) AS count FROM psm WHERE search_id IN ( #SEARCHES# ) "
			+ "  AND q_value <= ? "
			+ "  AND reported_peptide_id IN "
			+ " ( SELECT reported_peptide_id FROM unified_rep_pep__reported_peptide__search_lookup "
	
			+ 	" WHERE "
			+ 		" unified_reported_peptide_id = ? "
			+ 		" AND unified_rep_pep__reported_peptide__search_lookup.search_id IN ( #SEARCHES# ) "
	
					+ " AND ( unified_rep_pep__reported_peptide__search_lookup.peptide_q_value_for_search <= ? "
					+ 		" OR unified_rep_pep__reported_peptide__search_lookup.peptide_q_value_for_search IS NULL )   "
					
					+ " AND ( unified_rep_pep__reported_peptide__search_lookup.best_psm_q_value <= ? )   "
			+  " ) "
	  		;

			
			
	
	/**
	 * @param unifiedReportedPeptideId
	 * @param searchIds
	 * @param psmQValueCutoff
	 * @param peptideQValueCutoff
	 * @return
	 * @throws Exception
	 */
	public int getPsmCountForSearchIdsUnifiedReportedPeptideId( int unifiedReportedPeptideId, Collection<Integer> searchIds, double psmQValueCutoff, double peptideQValueCutoff ) throws Exception {
		
		int numPsms = 0;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = SQL;
		
		sql = sql.replaceAll( "#SEARCHES#", StringUtils.join( searchIds, "," ) );
		

		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			pstmt = conn.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setDouble( counter, psmQValueCutoff );
			
			counter++;
			pstmt.setInt( counter, unifiedReportedPeptideId );
			
			counter++;
			pstmt.setDouble( counter, peptideQValueCutoff );
			
			counter++;
			pstmt.setDouble( counter, psmQValueCutoff );
			
			
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				
				numPsms = rs.getInt( "count" );
			}


		} catch ( Exception e ) {

			log.error( "ERROR:  SQL: " + sql, e );

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
		
		return numPsms;		
	}
	
}
