package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.xlink.dao.NRProteinDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.NRProteinDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.constants.DefaultQValueCutoffConstants;
import org.yeastrc.xlink.www.objects.SearchProteinCrosslink;
import org.yeastrc.xlink.www.objects.SearchProtein;

public class SearchProteinCrosslinkSearcher {

	private SearchProteinCrosslinkSearcher() { }
	private static final SearchProteinCrosslinkSearcher _INSTANCE = new SearchProteinCrosslinkSearcher();
	public static SearchProteinCrosslinkSearcher getInstance() { return _INSTANCE; }
	
	/**
	 * Get the number of crosslinks found in the given search with the given cutoffs
	 * @param search
	 * @param psmCutoff
	 * @param peptideCutoff
	 * @return
	 * @throws Exception
	 */
	public int getCount( SearchDTO search, double psmCutoff, double peptideCutoff ) throws Exception {
		int count = 0;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = "SELECT COUNT(*) " +
					"FROM search_crosslink_lookup WHERE search_id = ? AND bestPSMQValue <= ? AND  ( bestPeptideQValue <= ? OR bestPeptideQValue IS NULL )  "
					+ "ORDER BY nrseq_id_1, nrseq_id_2, protein_1_position, protein_2_position";			
			
			pstmt = conn.prepareStatement( sql );
			
			pstmt.setDouble( 1, search.getId() );
			pstmt.setDouble( 2, psmCutoff );
			pstmt.setDouble( 3, peptideCutoff );
			
			rs = pstmt.executeQuery();
			rs.next();
			
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
	
	public List<SearchProteinCrosslink> search( SearchDTO search, double psmCutoff, double peptideCutoff ) throws Exception {
		List<SearchProteinCrosslink> links = new ArrayList<SearchProteinCrosslink>();
				
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			final String sql = "SELECT nrseq_id_1, nrseq_id_2, protein_1_position, protein_2_position, "
					+ "bestPSMQValue, bestPeptideQValue, "
					+ "num_psm_at_pt_01_q_cutoff, num_linked_peptides_at_pt_01_q_cutoff, num_unique_peptides_linked_at_pt_01_q_cutoff "

					+ " FROM search_crosslink_lookup WHERE search_id = ? AND bestPSMQValue <= ? AND  ( bestPeptideQValue <= ? OR bestPeptideQValue IS NULL )  "
					+ "ORDER BY nrseq_id_1, nrseq_id_2, protein_1_position, protein_2_position";			
			
			pstmt = conn.prepareStatement( sql );
			
			pstmt.setDouble( 1, search.getId() );
			pstmt.setDouble( 2, psmCutoff );
			pstmt.setDouble( 3, peptideCutoff );
			
			rs = pstmt.executeQuery();

			while( rs.next() ) {
				SearchProteinCrosslink link = new SearchProteinCrosslink();
				link.setPsmCutoff( psmCutoff );
				link.setPeptideCutoff( peptideCutoff );
				link.setProtein1( new SearchProtein( search, NRProteinDAO.getInstance().getNrProtein( rs.getInt( "nrseq_id_1" ) ) ) );
				link.setProtein2( new SearchProtein( search, NRProteinDAO.getInstance().getNrProtein( rs.getInt( "nrseq_id_2" ) ) ) );
				
				link.setProtein1Position( rs.getInt( "protein_1_position" ) );
				link.setProtein2Position( rs.getInt( "protein_2_position" ) );
				
				link.setBestPSMQValue( rs.getDouble( "bestPSMQValue" ) );
				
				link.setBestPeptideQValue( rs.getDouble( "bestPeptideQValue" ) );
				if ( rs.wasNull() ) {
					link.setBestPeptideQValue( null );
				}
				
				
				//  These counts are only valid for PSM and Peptide Q value cutoff of default 0.01
				
				if ( DefaultQValueCutoffConstants.PSM_Q_VALUE_CUTOFF_DEFAULT == psmCutoff 
						&& DefaultQValueCutoffConstants.PEPTIDE_Q_VALUE_CUTOFF_DEFAULT == peptideCutoff ) {

					link.setNumPsms( rs.getInt( "num_psm_at_pt_01_q_cutoff" ) );
					link.setNumLinkedPeptides( rs.getInt( "num_linked_peptides_at_pt_01_q_cutoff" ) );
					link.setNumUniqueLinkedPeptides( rs.getInt( "num_unique_peptides_linked_at_pt_01_q_cutoff" ) );
				}
				
				
				link.setSearch( search );
				
				links.add( link );
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
		
		return links;
	}
	
	
	public SearchProteinCrosslink search( SearchDTO search, double psmCutoff, double peptideCutoff, NRProteinDTO protein1, NRProteinDTO protein2, int position1, int position2 ) throws Exception {
		SearchProteinCrosslink link = null;
				
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = "SELECT bestPSMQValue, bestPeptideQValue, "
					+ "num_psm_at_pt_01_q_cutoff, num_linked_peptides_at_pt_01_q_cutoff, num_unique_peptides_linked_at_pt_01_q_cutoff "

					+ "FROM search_crosslink_lookup WHERE search_id = ? AND bestPSMQValue <= ? AND  ( bestPeptideQValue <= ? OR bestPeptideQValue IS NULL )  AND "
					+ "nrseq_id_1 = ? AND nrseq_id_2 = ? AND protein_1_position = ? AND protein_2_position = ?";	
			
			pstmt = conn.prepareStatement( sql );
			
			pstmt.setDouble( 1, search.getId() );
			pstmt.setDouble( 2, psmCutoff );
			pstmt.setDouble( 3, peptideCutoff );
			pstmt.setInt( 4, protein1.getNrseqId() );
			pstmt.setInt( 5, protein2.getNrseqId() );
			pstmt.setInt( 6, position1 );
			pstmt.setInt( 7, position2 );
						
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				link = new SearchProteinCrosslink();
				
				link.setPsmCutoff( psmCutoff );
				link.setPeptideCutoff( peptideCutoff );
				link.setProtein1( new SearchProtein( search, protein1 ) );
				link.setProtein2( new SearchProtein( search, protein2 ) );
				
				link.setProtein1Position( position1 );
				link.setProtein2Position( position2 );
				link.setBestPSMQValue( rs.getDouble( 1 ) );
				
				link.setBestPeptideQValue( rs.getDouble( 2 ) );
				if ( rs.wasNull() ) {
					link.setBestPeptideQValue( null );
				}
				

				
				//  These counts are only valid for PSM and Peptide Q value cutoff of default 0.01
				
				if ( DefaultQValueCutoffConstants.PSM_Q_VALUE_CUTOFF_DEFAULT == psmCutoff 
						&& DefaultQValueCutoffConstants.PEPTIDE_Q_VALUE_CUTOFF_DEFAULT == peptideCutoff ) {

					link.setNumPsms( rs.getInt( "num_psm_at_pt_01_q_cutoff" ) );
					link.setNumLinkedPeptides( rs.getInt( "num_linked_peptides_at_pt_01_q_cutoff" ) );
					link.setNumUniqueLinkedPeptides( rs.getInt( "num_unique_peptides_linked_at_pt_01_q_cutoff" ) );
				}
				
				
				
				link.setSearch( search );
				
				if( rs.next() )
					throw new Exception( "Should not have gotten more than one row." );
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
		
		return link;
	}
	
	
}
