package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * Is only this PSM associated with it's scan ids 
 * for the current search and meet the current Peptide and PSM cutoffs 
 *
 */
public class Psm_ScanCountForAssociatedScanId_From_PsmId_SearchId_Searcher {

	private Psm_ScanCountForAssociatedScanId_From_PsmId_SearchId_Searcher() { }
	public static Psm_ScanCountForAssociatedScanId_From_PsmId_SearchId_Searcher getInstance() { return new Psm_ScanCountForAssociatedScanId_From_PsmId_SearchId_Searcher(); }

	private static final Logger log = Logger.getLogger(Psm_ScanCountForAssociatedScanId_From_PsmId_SearchId_Searcher.class);
	

	
	private static final String SQL = 
		
			"SELECT COUNT(*) AS count " 
			
			+ 	"FROM proxl.psm  "
			+ 	"INNER JOIN psm AS psm_other ON psm.scan_id = psm_other.scan_id "
			+ 	"INNER JOIN search_reported_peptide  "
			+ 		"ON psm_other.search_id = search_reported_peptide.search_id " 
			+ 			"AND psm_other.reported_peptide_id = search_reported_peptide.reported_peptide_id "
		
			+ 	"WHERE psm.id = ? AND psm.search_id = ? AND psm.q_value <= ? "
			+ 	"AND psm_other.search_id = ? AND psm_other.q_value <= ? "
			+ 	"AND search_reported_peptide.q_value <= ? ";
		

			
			
	
	/**
	 * @param psmId
	 * @param searchId
	 * @param peptideQValueCutoff
	 * @param psmQValueCutoff
	 * @return
	 * @throws Exception
	 */
	public int scanCountForAssociatedScanId( int psmId, int searchId, double peptideQValueCutoff, double psmQValueCutoff ) throws Exception {
		
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
			pstmt.setInt( counter, psmId );

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
		
//		if ( numPsms )
		
		return numPsms;		
	}
	
}
