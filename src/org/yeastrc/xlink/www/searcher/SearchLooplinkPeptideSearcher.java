package org.yeastrc.xlink.www.searcher;

//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//
//import org.yeastrc.xlink.db.DBConnectionFactory;
//import org.yeastrc.xlink.www.objects.SearchProteinLooplink;
//import org.yeastrc.xlink.utils.YRC_NRSEQUtils;

public class SearchLooplinkPeptideSearcher {

	private SearchLooplinkPeptideSearcher() { }
	public static SearchLooplinkPeptideSearcher getInstance() { return new SearchLooplinkPeptideSearcher(); }

//	/**
//	 * Get the number of distinct peptides (that is, distinct pair of crosslinked peptides) found that identified the given crosslinked proteins/positions
//	 * @param crosslink
//	 * @return
//	 * @throws Exception
//	 */
//	public int getNumPeptides( SearchProteinLooplink looplink ) throws Exception {
//		int count = 0;
//		
//		Connection conn = null;
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//		try {
//						
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
//			String sql = "SELECT COUNT(distinct a.reported_peptide_id) " +
//					"FROM psm AS a INNER JOIN looplink AS b ON a.id = b.psm_id " +
//					"INNER JOIN search_reported_peptide AS c ON a.reported_peptide_id = c.reported_peptide_id " +
//					"WHERE a.q_value <= ? AND a.search_id = ? AND ( c.q_value <= ? OR c.q_value IS NULL )   AND c.search_id = ? AND b.nrseq_id = ? AND b.protein_position_1 = ? AND b.protein_position_2 = ?";
//			
//			pstmt = conn.prepareStatement( sql );
//			pstmt.setDouble( 1, looplink.getPsmCutoff() );
//			pstmt.setInt( 2, looplink.getSearch().getId() );
//			pstmt.setDouble( 3, looplink.getPeptideCutoff() );
//			pstmt.setInt( 4, looplink.getSearch().getId() );
//			pstmt.setInt( 5, looplink.getProtein().getNrProtein().getNrseqId() );
//			pstmt.setInt( 6, looplink.getProteinPosition1() );
//			pstmt.setInt( 7, looplink.getProteinPosition2() );
//			
//			rs = pstmt.executeQuery();
//			if( rs.next() )
//				count = rs.getInt( 1 );
//			
//		} finally {
//			
//			// be sure database handles are closed
//			if( rs != null ) {
//				try { rs.close(); } catch( Throwable t ) { ; }
//				rs = null;
//			}
//			
//			if( pstmt != null ) {
//				try { pstmt.close(); } catch( Throwable t ) { ; }
//				pstmt = null;
//			}
//			
//			if( conn != null ) {
//				try { conn.close(); } catch( Throwable t ) { ; }
//				conn = null;
//			}
//			
//		}
//		
//		
//		return count;
//	}
//	
//	/**
//	 * Get the number of peptides (pair of peptides) that UNIQUELY identified the pair of proteins+positions represented by this crosslink
//	 * @param crosslink
//	 * @return
//	 * @throws Exception
//	 */
//	public int getNumUniquePeptides( SearchProteinLooplink looplink ) throws Exception {
//		int count = 0;
//		
//		Connection conn = null;
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//		try {
//						
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
//			String sql = " SELECT COUNT(DISTINCT a.reported_peptide_id) "+
//						 "FROM psm AS a INNER JOIN looplink AS b ON a.id = b.psm_id "+
//						 "INNER JOIN search_reported_peptide AS d ON a.reported_peptide_id = d.reported_peptide_id "+
//						 "INNER JOIN nrseq_database_peptide_protein AS c1 ON b.peptide_id = c1.peptide_id "+
//						 "WHERE a.q_value <= ? AND a.search_id = ? AND ( d.q_value <= ? OR d.q_value IS NULL )   AND d.search_id = ? AND b.nrseq_id = ? " +
//						 "AND b.protein_position_1 = ? AND b.protein_position_2 = ? AND c1.nrseq_database_id = ? AND " +
//						 "c1.is_unique = 'Y'";
//			
//			pstmt = conn.prepareStatement( sql );
//			pstmt.setDouble( 1, looplink.getPsmCutoff() );
//			pstmt.setInt( 2, looplink.getSearch().getId() );
//			pstmt.setDouble( 3, looplink.getPeptideCutoff() );
//			pstmt.setInt( 4, looplink.getSearch().getId() );
//			pstmt.setInt( 5, looplink.getProtein().getNrProtein().getNrseqId() );
//			pstmt.setInt( 6, looplink.getProteinPosition1() );
//			pstmt.setInt( 7, looplink.getProteinPosition2() );
//			pstmt.setInt( 8, YRC_NRSEQUtils.getDatabaseIdFromName( looplink.getSearch().getFastaFilename() ) );
//
//			
//			rs = pstmt.executeQuery();
//			if( rs.next() )
//				count = rs.getInt( 1 );
//			
//		} finally {
//			
//			// be sure database handles are closed
//			if( rs != null ) {
//				try { rs.close(); } catch( Throwable t ) { ; }
//				rs = null;
//			}
//			
//			if( pstmt != null ) {
//				try { pstmt.close(); } catch( Throwable t ) { ; }
//				pstmt = null;
//			}
//			
//			if( conn != null ) {
//				try { conn.close(); } catch( Throwable t ) { ; }
//				conn = null;
//			}
//			
//		}
//		
//		
//		return count;
//	}
	
}
