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
import org.yeastrc.xlink.www.objects.SearchProtein;
import org.yeastrc.xlink.www.objects.SearchProteinLooplink;

public class SearchProteinLooplinkSearcher {

	private SearchProteinLooplinkSearcher() { }
	private static final SearchProteinLooplinkSearcher _INSTANCE = new SearchProteinLooplinkSearcher();
	public static SearchProteinLooplinkSearcher getInstance() { return _INSTANCE; }
	
	public List<SearchProteinLooplink> search( SearchDTO search, double psmCutoff, double peptideCutoff ) throws Exception {
		
		List<SearchProteinLooplink> links = new ArrayList<SearchProteinLooplink>();
				
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = "SELECT nrseq_id, protein_position_1, protein_position_2, bestPSMQValue, bestPeptideQValue, " 
					+ "num_psm_at_pt_01_q_cutoff, num_peptides_at_pt_01_q_cutoff, num_unique_peptides_at_pt_01_q_cutoff "

					+ "FROM search_looplink_lookup WHERE search_id = ? AND bestPSMQValue <= ? AND  ( bestPeptideQValue <= ? OR bestPeptideQValue IS NULL )  "
					+ "ORDER BY nrseq_id, protein_position_1, protein_position_2";			
			
			pstmt = conn.prepareStatement( sql );
			
			pstmt.setDouble( 1, search.getId() );
			pstmt.setDouble( 2, psmCutoff );
			pstmt.setDouble( 3, peptideCutoff );
			
			rs = pstmt.executeQuery();

			while( rs.next() ) {
				SearchProteinLooplink link = new SearchProteinLooplink();
				link.setPsmCutoff( psmCutoff );
				link.setPeptideCutoff( peptideCutoff );
				link.setProtein( new SearchProtein( search, NRProteinDAO.getInstance().getNrProtein( rs.getInt( 1 ) ) ) );
				
				link.setProteinPosition1( rs.getInt( 2 ) );
				link.setProteinPosition2( rs.getInt( 3 ) );
				link.setBestPSMQValue( rs.getDouble( 4 ) );
				
				link.setBestPeptideQValue( rs.getDouble( 5 ) );
				if ( rs.wasNull() ) {
					link.setBestPeptideQValue( null );
				}
				

				//  These counts are only valid for PSM and Peptide Q value cutoff of default 0.01
				
				if ( DefaultQValueCutoffConstants.PSM_Q_VALUE_CUTOFF_DEFAULT == psmCutoff 
						&& DefaultQValueCutoffConstants.PEPTIDE_Q_VALUE_CUTOFF_DEFAULT == peptideCutoff ) {

					link.setNumPsms( rs.getInt( "num_psm_at_pt_01_q_cutoff" ) );
					link.setNumPeptides( rs.getInt( "num_peptides_at_pt_01_q_cutoff" ) );
					link.setNumUniquePeptides( rs.getInt( "num_unique_peptides_at_pt_01_q_cutoff" ) );
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
	
	public SearchProteinLooplink search( SearchDTO search, double psmCutoff, double peptideCutoff, NRProteinDTO protein, int position1, int position2 ) throws Exception {
		SearchProteinLooplink link = null;
				
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = "SELECT bestPSMQValue, bestPeptideQValue, " 
					+ "num_psm_at_pt_01_q_cutoff, num_peptides_at_pt_01_q_cutoff, num_unique_peptides_at_pt_01_q_cutoff "

					+ "FROM search_looplink_lookup WHERE search_id = ? AND bestPSMQValue <= ? AND  ( bestPeptideQValue <= ? OR bestPeptideQValue IS NULL )  AND "
					+ "nrseq_id = ? AND protein_position_1 = ? AND protein_position_2 = ?";	
			
			pstmt = conn.prepareStatement( sql );
			
			pstmt.setDouble( 1, search.getId() );
			pstmt.setDouble( 2, psmCutoff );
			pstmt.setDouble( 3, peptideCutoff );
			pstmt.setInt( 4, protein.getNrseqId() );
			pstmt.setInt( 5, position1 );
			pstmt.setInt( 6, position2 );
			
			rs = pstmt.executeQuery();

			if( rs.next() ) {
				link = new SearchProteinLooplink();

				link.setPsmCutoff( psmCutoff );
				link.setPeptideCutoff( peptideCutoff );
				link.setProtein( new SearchProtein( search, protein ) );
				
				link.setProteinPosition1( position1 );
				link.setProteinPosition2( position2 );
				link.setBestPSMQValue( rs.getDouble( 1 ) );
				
				link.setBestPeptideQValue( rs.getDouble( 2 ) );
				if ( rs.wasNull() ) {
					link.setBestPeptideQValue( null );
				}
				

				
				//  These counts are only valid for PSM and Peptide Q value cutoff of default 0.01
				
				if ( DefaultQValueCutoffConstants.PSM_Q_VALUE_CUTOFF_DEFAULT == psmCutoff 
						&& DefaultQValueCutoffConstants.PEPTIDE_Q_VALUE_CUTOFF_DEFAULT == peptideCutoff ) {

					link.setNumPsms( rs.getInt( "num_psm_at_pt_01_q_cutoff" ) );
					link.setNumPeptides( rs.getInt( "num_peptides_at_pt_01_q_cutoff" ) );
					link.setNumUniquePeptides( rs.getInt( "num_unique_peptides_at_pt_01_q_cutoff" ) );
				}
				
				
				
				link.setSearch( search );
				
				if( rs.next() )
					throw new Exception( "Should only have gotten one row..." );
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
