package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.xlink.dao.NRProteinDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.objects.SearchProtein;
import org.yeastrc.xlink.www.objects.SearchProteinDoublePosition;
import org.yeastrc.xlink.www.objects.SearchProteinPosition;

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
				"SELECT DISTINCT nrseq_id " 
						+ " FROM srch_rep_pept__peptide "
						+ "  INNER JOIN srch_rep_pept__nrseq_id_pos_unlinked_dimer "
						+ 	" ON srch_rep_pept__peptide.id = "
						+ 		" srch_rep_pept__nrseq_id_pos_unlinked_dimer.search_reported_peptide_peptide_id" 
						+ " WHERE srch_rep_pept__peptide.search_id = ? "
						+ 	" AND srch_rep_pept__peptide.reported_peptide_id = ? "
						+ 	" AND srch_rep_pept__peptide.peptide_id = ? ";

		try {

			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			pstmt = conn.prepareStatement( sql );


			int counter = 0;

			counter++;
			pstmt.setInt( counter, search.getId() );
			counter++;
			pstmt.setInt( counter, reportedPeptideId );
			counter++;
			pstmt.setInt( counter, peptideId );

			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				
				SearchProteinPosition prpp = new SearchProteinPosition();
				prpp.setProtein( new SearchProtein( search, NRProteinDAO.getInstance().getNrProtein( rs.getInt( 1 ) ) ) );
				
				proteinPositions.add( prpp );
			}
			

			//  Sort on nrseq id
			
			Collections.sort( proteinPositions, new Comparator<SearchProteinPosition>() {

				@Override
				public int compare(SearchProteinPosition o1, SearchProteinPosition o2) {
					
					return o1.getProtein().getNrProtein().getNrseqId() - o2.getProtein().getNrProtein().getNrseqId();
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
	
	

	public List<SearchProteinPosition> getProteinForDimer( SearchDTO search, int reportedPeptideId, int peptideId ) throws Exception {
		
		List<SearchProteinPosition> proteinPositions = new ArrayList<SearchProteinPosition>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		

		final String sql = 
					"SELECT DISTINCT nrseq_id " 
					+ " FROM srch_rep_pept__peptide "
					+ "  INNER JOIN srch_rep_pept__nrseq_id_pos_unlinked_dimer "
					+ 	" ON srch_rep_pept__peptide.id = "
					+ 		" srch_rep_pept__nrseq_id_pos_unlinked_dimer.search_reported_peptide_peptide_id" 
					+ " WHERE srch_rep_pept__peptide.search_id = ? "
					+ 	" AND srch_rep_pept__peptide.reported_peptide_id = ? "
					+ 	" AND srch_rep_pept__peptide.peptide_id = ? ";

		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
//						+ "ORDER BY nseq, pos";
			
			pstmt = conn.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, search.getId() );
			counter++;
			pstmt.setInt( counter, reportedPeptideId );
			counter++;
			pstmt.setInt( counter, peptideId );


			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				SearchProteinPosition prpp = new SearchProteinPosition();
				prpp.setProtein( new SearchProtein( search, NRProteinDAO.getInstance().getNrProtein( rs.getInt( "nrseq_id" ) ) ) );
				
				proteinPositions.add( prpp );
			}

			//  Sort on nrseq id
			
			Collections.sort( proteinPositions, new Comparator<SearchProteinPosition>() {

				@Override
				public int compare(SearchProteinPosition o1, SearchProteinPosition o2) {
					
					return o1.getProtein().getNrProtein().getNrseqId() - o2.getProtein().getNrProtein().getNrseqId();
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
	
	
	public List<SearchProteinPosition> getProteinPositions( SearchDTO search, int reportedPeptideId, int peptideId, int position ) throws Exception {
		
		List<SearchProteinPosition> proteinPositions = new ArrayList<SearchProteinPosition>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		

		final String sql = 
					"SELECT DISTINCT nrseq_id, nrseq_position " 
					+ " FROM srch_rep_pept__peptide "
					+ "  INNER JOIN srch_rep_pept__nrseq_id_pos_crosslink "
					+ 	" ON srch_rep_pept__peptide.id = "
					+ 		" srch_rep_pept__nrseq_id_pos_crosslink.search_reported_peptide_peptide_id" 
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
			pstmt.setInt( counter, search.getId() );
			counter++;
			pstmt.setInt( counter, reportedPeptideId );
			counter++;
			pstmt.setInt( counter, peptideId );
			counter++;
			pstmt.setInt( counter, position );
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				SearchProteinPosition prpp = new SearchProteinPosition();
				prpp.setPosition( rs.getInt( "nrseq_position" ) );
				prpp.setProtein( new SearchProtein( search, NRProteinDAO.getInstance().getNrProtein( rs.getInt( "nrseq_id" ) ) ) );
				
				proteinPositions.add( prpp );
			}
			
			//  Sort on nrseq id, position
			
			Collections.sort( proteinPositions, new Comparator<SearchProteinPosition>() {

				@Override
				public int compare(SearchProteinPosition o1, SearchProteinPosition o2) {
					
					if ( o1.getProtein().getNrProtein().getNrseqId() != o2.getProtein().getNrProtein().getNrseqId() ) {
						
						return o1.getProtein().getNrProtein().getNrseqId() - o2.getProtein().getNrProtein().getNrseqId();
					}
					return o1.getPosition() - o2.getPosition();
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
	 * @param position1
	 * @param position2
	 * @return
	 * @throws Exception
	 */
	public List<SearchProteinDoublePosition> getProteinDoublePositions( 
			SearchDTO search, 
			int reportedPeptideId, 
			int peptideId, 
			int position1,
			int position2 ) throws Exception {

		
		List<SearchProteinDoublePosition> proteinPositions = new ArrayList<SearchProteinDoublePosition>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		

		final String sql = 
					"SELECT DISTINCT nrseq_id, nrseq_position_1, nrseq_position_2 " 
					+ " FROM srch_rep_pept__peptide "
					+ "  INNER JOIN srch_rep_pept__nrseq_id_pos_looplink "
					+ 	" ON srch_rep_pept__peptide.id = "
					+ 		" srch_rep_pept__nrseq_id_pos_looplink.search_reported_peptide_peptide_id" 
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
			pstmt.setInt( counter, search.getId() );
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
				
				SearchProteinDoublePosition prpp = new SearchProteinDoublePosition();

				prpp.setProtein( new SearchProtein( search, NRProteinDAO.getInstance().getNrProtein( rs.getInt( "nrseq_id" ) ) ) );
				prpp.setPosition1( rs.getInt( "nrseq_position_1" ) );
				prpp.setPosition2( rs.getInt( "nrseq_position_2" ) );
				
				proteinPositions.add( prpp );
			}
			
			//  Sort on nrseq id, protein_position_1, protein_position_2
			
			Collections.sort( proteinPositions, new Comparator<SearchProteinDoublePosition>() {

				@Override
				public int compare(SearchProteinDoublePosition o1, SearchProteinDoublePosition o2) {
					
					if ( o1.getProtein().getNrProtein().getNrseqId() != o2.getProtein().getNrProtein().getNrseqId() ) {
						
						return o1.getProtein().getNrProtein().getNrseqId() - o2.getProtein().getNrProtein().getNrseqId();
					}
					if ( o1.getPosition1() != o2.getPosition1() ) {
						return o1.getPosition1() - o2.getPosition1();
					}
					return o1.getPosition1() - o2.getPosition1();
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
