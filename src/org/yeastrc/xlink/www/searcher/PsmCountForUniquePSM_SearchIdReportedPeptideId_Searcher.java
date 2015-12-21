package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * Get count of PSMs where only those PSMs are associated with their scan ids 
 * for the current search and meet the current Peptide and PSM cutoffs 
 *
 */
public class PsmCountForUniquePSM_SearchIdReportedPeptideId_Searcher {

	private PsmCountForUniquePSM_SearchIdReportedPeptideId_Searcher() { }
	public static PsmCountForUniquePSM_SearchIdReportedPeptideId_Searcher getInstance() { return new PsmCountForUniquePSM_SearchIdReportedPeptideId_Searcher(); }

	private static final Logger log = Logger.getLogger(PsmCountForUniquePSM_SearchIdReportedPeptideId_Searcher.class);
	

	
	private static final String SQL = 
			"SELECT COUNT(*) AS count FROM ( "
		
			+ 	"SELECT psm.scan_id " 
			
			+ 	"FROM psm  "
			+ 	"INNER JOIN psm AS psm_other ON psm.scan_id = psm_other.scan_id "
			+ 	"INNER JOIN search_reported_peptide  "
			+ 		"ON psm_other.search_id = search_reported_peptide.search_id " 
			+ 			"AND psm_other.reported_peptide_id = search_reported_peptide.reported_peptide_id "
		
			+ 	"WHERE psm.reported_peptide_id = ? AND psm.search_id = ? AND psm.q_value <= ? "
			+ 	"AND psm_other.search_id = ? AND psm_other.q_value <= ? "
			+ 	"AND search_reported_peptide.q_value <= ? "
		
			+ 	"GROUP BY psm.scan_id "
			+ 	"HAVING COUNT(*) < 2"
		
			+ ") AS unique_psms";


			
			
	
	/**
	 * @param reportedPeptideId
	 * @param searchId
	 * @param psmQValueCutoff
	 * @return
	 * @throws Exception
	 */
	public int getPsmCountForUniquePSM_SearchIdReportedPeptideId( int reportedPeptideId, int searchId, double peptideQValueCutoff, double psmQValueCutoff ) throws Exception {
		
		int numPsms = 0;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = SQL;
		
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			pstmt = conn.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, reportedPeptideId );

			counter++;
			pstmt.setInt( counter, searchId );

			counter++;
			pstmt.setDouble( counter, psmQValueCutoff );
			
			counter++;
			pstmt.setInt( counter, searchId );
			
			counter++;
			pstmt.setDouble( counter, psmQValueCutoff );
			

			counter++;
			pstmt.setDouble( counter, peptideQValueCutoff );
			
			
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
