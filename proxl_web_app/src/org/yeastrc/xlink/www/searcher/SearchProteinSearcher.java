package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.xlink.www.factories.ProteinSequenceObjectFactory;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.objects.SearchProtein;
import org.yeastrc.xlink.www.objects.SearchProteinPosition;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.CrosslinkProteinPositionsFor_CrosslinkPeptide_Result_Entry;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.LooplinkProteinPositionsFor_LooplinkPeptide_Result_Entry;

public class SearchProteinSearcher {


	private SearchProteinSearcher() { }
	private static final SearchProteinSearcher _INSTANCE = new SearchProteinSearcher();
	public static SearchProteinSearcher getInstance() { return _INSTANCE; }
	

	public List<SearchProteinPosition> getProteinForUnlinked(
			SearchDTO search, 
			int reportedPeptideId, 
			int peptideId ) throws Exception {
		
		
		List<SearchProteinPosition> proteinPositions = new ArrayList<SearchProteinPosition>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = 
				"SELECT DISTINCT protein_sequence_id " 
						+ " FROM srch_rep_pept__peptide "
						+ "  INNER JOIN srch_rep_pept__prot_seq_id_unlinked "
						+ 	" ON srch_rep_pept__peptide.id = "
						+ 		" srch_rep_pept__prot_seq_id_unlinked.search_reported_peptide_peptide_id" 
						+ " WHERE srch_rep_pept__peptide.search_id = ? "
						+ 	" AND srch_rep_pept__peptide.reported_peptide_id = ? "
						+ 	" AND srch_rep_pept__peptide.peptide_id = ? ";

		try {

			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			pstmt = conn.prepareStatement( sql );


			int counter = 0;

			counter++;
			pstmt.setInt( counter, search.getSearchId() );
			counter++;
			pstmt.setInt( counter, reportedPeptideId );
			counter++;
			pstmt.setInt( counter, peptideId );

			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				
				SearchProteinPosition prpp = new SearchProteinPosition();
				prpp.setProtein( new SearchProtein( search, ProteinSequenceObjectFactory.getProteinSequenceObject( rs.getInt( 1 ) ) ) );
				
				proteinPositions.add( prpp );
			}
			

			//  Sort on protein sequence id
			
			Collections.sort( proteinPositions, new Comparator<SearchProteinPosition>() {

				@Override
				public int compare(SearchProteinPosition o1, SearchProteinPosition o2) {
					
					return o1.getProtein().getProteinSequenceObject().getProteinSequenceId() - o2.getProtein().getProteinSequenceObject().getProteinSequenceId();
				}
			});;
			
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
		
		return proteinPositions;
	}
	
	

	/**
	 * @param search
	 * @param reportedPeptideId
	 * @param peptideId
	 * @return
	 * @throws Exception
	 */
	public List<SearchProteinPosition> getProteinForDimer( SearchDTO search, int reportedPeptideId, int peptideId ) throws Exception {
		
		List<SearchProteinPosition> proteinPositions = new ArrayList<SearchProteinPosition>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		

		final String sql = 
					"SELECT DISTINCT protein_sequence_id " 
					+ " FROM srch_rep_pept__peptide "
					+ "  INNER JOIN srch_rep_pept__prot_seq_id_dimer "
					+ 	" ON srch_rep_pept__peptide.id = "
					+ 		" srch_rep_pept__prot_seq_id_dimer.search_reported_peptide_peptide_id" 
					+ " WHERE srch_rep_pept__peptide.search_id = ? "
					+ 	" AND srch_rep_pept__peptide.reported_peptide_id = ? "
					+ 	" AND srch_rep_pept__peptide.peptide_id = ? ";

		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
//						+ "ORDER BY nseq, pos";
			
			pstmt = conn.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, search.getSearchId() );
			counter++;
			pstmt.setInt( counter, reportedPeptideId );
			counter++;
			pstmt.setInt( counter, peptideId );


			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				SearchProteinPosition prpp = new SearchProteinPosition();
				prpp.setProtein( new SearchProtein( search, ProteinSequenceObjectFactory.getProteinSequenceObject( rs.getInt( "protein_sequence_id" ) ) ) );
				
				proteinPositions.add( prpp );
			}

			//  Sort on protein sequence id
			
			Collections.sort( proteinPositions, new Comparator<SearchProteinPosition>() {

				@Override
				public int compare(SearchProteinPosition o1, SearchProteinPosition o2) {
					
					return o1.getProtein().getProteinSequenceObject().getProteinSequenceId() - o2.getProtein().getProteinSequenceObject().getProteinSequenceId();
				}
			});;
			
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
		
		return proteinPositions;
	}
	

	public List<CrosslinkProteinPositionsFor_CrosslinkPeptide_Result_Entry> getCrosslinkProteinPositions( 
			int searchId, int reportedPeptideId, int peptideId, int position ) throws Exception {
		
		List<CrosslinkProteinPositionsFor_CrosslinkPeptide_Result_Entry> proteinPositions = new ArrayList<>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = 
					"SELECT DISTINCT protein_sequence_id, protein_sequence_position " 
					+ " FROM srch_rep_pept__peptide "
					+ "  INNER JOIN srch_rep_pept__prot_seq_id_pos_crosslink "
					+ 	" ON srch_rep_pept__peptide.id = "
					+ 		" srch_rep_pept__prot_seq_id_pos_crosslink.search_reported_peptide_peptide_id" 
					+ " WHERE srch_rep_pept__peptide.search_id = ? "
					+ 	" AND srch_rep_pept__peptide.reported_peptide_id = ? "
					+ 	" AND srch_rep_pept__peptide.peptide_id = ? "
					+ 	" AND srch_rep_pept__peptide.peptide_position_1 = ? ";

		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
//						+ "ORDER BY nseq, pos";
			
			pstmt = conn.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, searchId );
			counter++;
			pstmt.setInt( counter, reportedPeptideId );
			counter++;
			pstmt.setInt( counter, peptideId );
			counter++;
			pstmt.setInt( counter, position );
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				CrosslinkProteinPositionsFor_CrosslinkPeptide_Result_Entry prpp = new CrosslinkProteinPositionsFor_CrosslinkPeptide_Result_Entry();
				prpp.setProteinSequencePosition( rs.getInt( "protein_sequence_position" ) );
				prpp.setProteinSequenceId( rs.getInt( "protein_sequence_id" ) );
				
				proteinPositions.add( prpp );
			}
			
			//  Sort on protein sequence id, position
			Collections.sort( proteinPositions, new Comparator<CrosslinkProteinPositionsFor_CrosslinkPeptide_Result_Entry>() {
				@Override
				public int compare(CrosslinkProteinPositionsFor_CrosslinkPeptide_Result_Entry o1, CrosslinkProteinPositionsFor_CrosslinkPeptide_Result_Entry o2) {
					if ( o1.getProteinSequenceId() != o2.getProteinSequenceId() ) {
						return o1.getProteinSequenceId() - o2.getProteinSequenceId();
					}
					return o1.getProteinSequencePosition() - o2.getProteinSequencePosition();
				}
			});
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
		
		return proteinPositions;
	}
	
	/**
	 * @param search
	 * @param reportedPeptideId
	 * @param peptideId
	 * @param position1
	 * @param position2
	 * @return
	 * @throws Exception
	 */
	public List<LooplinkProteinPositionsFor_LooplinkPeptide_Result_Entry> getLooplinkProteinPositions( 
			int searchId, 
			int reportedPeptideId, 
			int peptideId, 
			int position1,
			int position2 ) throws Exception {

		List<LooplinkProteinPositionsFor_LooplinkPeptide_Result_Entry> proteinPositions = new ArrayList<LooplinkProteinPositionsFor_LooplinkPeptide_Result_Entry>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = 
					"SELECT DISTINCT protein_sequence_id, protein_sequence_position_1, protein_sequence_position_2 " 
					+ " FROM srch_rep_pept__peptide "
					+ "  INNER JOIN srch_rep_pept__prot_seq_id_pos_looplink "
					+ 	" ON srch_rep_pept__peptide.id = "
					+ 		" srch_rep_pept__prot_seq_id_pos_looplink.search_reported_peptide_peptide_id" 
					+ " WHERE srch_rep_pept__peptide.search_id = ? "
					+ 	" AND srch_rep_pept__peptide.reported_peptide_id = ? "
					+ 	" AND srch_rep_pept__peptide.peptide_id = ? "
					+ 	" AND srch_rep_pept__peptide.peptide_position_1 = ? "
					+ 	" AND srch_rep_pept__peptide.peptide_position_2 = ? ";

		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );

			int counter = 0;
			counter++;
			pstmt.setInt( counter, searchId );
			counter++;
			pstmt.setInt( counter, reportedPeptideId );
			counter++;
			pstmt.setInt( counter, peptideId );
			counter++;
			pstmt.setInt( counter, position1 );
			counter++;
			pstmt.setInt( counter, position2 );
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				LooplinkProteinPositionsFor_LooplinkPeptide_Result_Entry entry = new LooplinkProteinPositionsFor_LooplinkPeptide_Result_Entry();
				entry.setProteinSequenceId( rs.getInt( "protein_sequence_id" ) );
				entry.setProteinSequencePosition_1( rs.getInt( "protein_sequence_position_1" ) );
				entry.setProteinSequencePosition_2( rs.getInt( "protein_sequence_position_2" ) );
				proteinPositions.add( entry );
			}
			
			//  Sort on protein sequence id, protein_position_1, protein_position_2
			
			Collections.sort( proteinPositions, new Comparator<LooplinkProteinPositionsFor_LooplinkPeptide_Result_Entry>() {

				@Override
				public int compare(LooplinkProteinPositionsFor_LooplinkPeptide_Result_Entry o1, LooplinkProteinPositionsFor_LooplinkPeptide_Result_Entry o2) {
					if ( o1.getProteinSequenceId() != o2.getProteinSequenceId() ) {
						return o1.getProteinSequenceId() - o2.getProteinSequenceId();
					}
					if ( o1.getProteinSequencePosition_1() != o2.getProteinSequencePosition_1() ) {
						return o1.getProteinSequencePosition_1() - o2.getProteinSequencePosition_1();
					}
					return o1.getProteinSequencePosition_2() - o2.getProteinSequencePosition_2();
				}
			});;
			
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
		
		return proteinPositions;
	}
	
	
}
