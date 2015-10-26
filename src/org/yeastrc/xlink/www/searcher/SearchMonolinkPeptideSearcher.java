package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.objects.SearchProteinMonolink;
import org.yeastrc.xlink.utils.YRC_NRSEQUtils;

public class SearchMonolinkPeptideSearcher {

	private SearchMonolinkPeptideSearcher() { }
	public static SearchMonolinkPeptideSearcher getInstance() { return new SearchMonolinkPeptideSearcher(); }

	/**
	 * Get the number of distinct peptides (that is, distinct pair of crosslinked peptides) found that identified the given monolink protein
	 * @param crosslink
	 * @return
	 * @throws Exception
	 */
	public int getNumPeptides( SearchProteinMonolink monolink ) throws Exception {
		int count = 0;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = "SELECT COUNT(distinct a.reported_peptide_id) " +
					"FROM psm AS a INNER JOIN monolink AS b ON a.id = b.psm_id " +
					"INNER JOIN search_reported_peptide AS c ON a.reported_peptide_id = c.reported_peptide_id " +
					"WHERE a.q_value <= ? AND a.search_id = ? AND ( c.q_value <= ? OR c.q_value IS NULL )   AND c.search_id = ? AND b.nrseq_id = ? AND b.protein_position = ?";
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setDouble( 1, monolink.getPsmCutoff() );
			pstmt.setInt( 2, monolink.getSearch().getId() );
			pstmt.setDouble( 3, monolink.getPeptideCutoff() );
			pstmt.setInt( 4, monolink.getSearch().getId() );
			pstmt.setInt( 5, monolink.getProtein().getNrProtein().getNrseqId() );
			pstmt.setInt( 6, monolink.getProteinPosition() );
			
			rs = pstmt.executeQuery();
			if( rs.next() )
				count = rs.getInt( 1 );
			
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
		
		
		return count;
	}
	
	/**
	 * Get the number of peptides (pair of peptides) that UNIQUELY identified the pair of proteins+positions represented by this crosslink
	 * @param crosslink
	 * @return
	 * @throws Exception
	 */
	public int getNumUniquePeptides( SearchProteinMonolink monolink ) throws Exception {
		int count = 0;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = " SELECT COUNT(DISTINCT a.reported_peptide_id) "+
						 "FROM psm AS a INNER JOIN monolink AS b ON a.id = b.psm_id "+
						 "INNER JOIN search_reported_peptide AS d ON a.reported_peptide_id = d.reported_peptide_id "+
						 "INNER JOIN nrseq_database_peptide_protein AS c1 ON b.peptide_id = c1.peptide_id "+
						 "WHERE a.q_value <= ? AND a.search_id = ? AND ( d.q_value <= ? OR d.q_value IS NULL )   AND d.search_id = ? AND b.nrseq_id = ? " +
						 "AND b.protein_position = ? AND c1.nrseq_database_id = ? AND " +
						 "c1.is_unique = 'Y'";
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setDouble( 1, monolink.getPsmCutoff() );
			pstmt.setInt( 2, monolink.getSearch().getId() );
			pstmt.setDouble( 3, monolink.getPeptideCutoff() );
			pstmt.setInt( 4, monolink.getSearch().getId() );
			pstmt.setInt( 5, monolink.getProtein().getNrProtein().getNrseqId() );
			pstmt.setInt( 6, monolink.getProteinPosition() );
			pstmt.setInt( 7, YRC_NRSEQUtils.getDatabaseIdFromName( monolink.getSearch().getFastaFilename() ) );

			
			rs = pstmt.executeQuery();
			if( rs.next() )
				count = rs.getInt( 1 );
			
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
		
		
		return count;
	}
	
}
