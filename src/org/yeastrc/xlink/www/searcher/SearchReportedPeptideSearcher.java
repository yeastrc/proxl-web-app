package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.xlink.dao.ReportedPeptideDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;

/**
 * 
 *
 */
public class SearchReportedPeptideSearcher {

	private SearchReportedPeptideSearcher() { }
	private static final SearchReportedPeptideSearcher _INSTANCE = new SearchReportedPeptideSearcher();
	public static SearchReportedPeptideSearcher getInstance() { return _INSTANCE; }
	
	
	
	/**
	 * @param searchId
	 * @param unifiedReportedPeptideId
	 * @return
	 * @throws Exception
	 */
	public List<ReportedPeptideDTO> getReportedPeptideForSearchIdUnifiedReportedPeptideId( int searchId, int unifiedReportedPeptideId ) throws Exception {
		
		List<ReportedPeptideDTO> results = new ArrayList<ReportedPeptideDTO>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			String sql = "SELECT reported_peptide_id FROM search_reported_peptide WHERE unified_reported_peptide_id = ? AND search_id = ?";
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, unifiedReportedPeptideId );
			pstmt.setInt( 2, searchId );
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				
				int reported_peptide_id = rs.getInt( 1 );
				results.add( ReportedPeptideDAO.getInstance().getReportedPeptideFromDatabase( reported_peptide_id ) );
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
		
		return results;
	}
	
}
