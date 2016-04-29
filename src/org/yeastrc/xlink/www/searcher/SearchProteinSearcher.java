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
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.objects.SearchProtein;
import org.yeastrc.xlink.www.objects.SearchProteinDoublePosition;
import org.yeastrc.xlink.www.objects.SearchProteinPosition;

public class SearchProteinSearcher {


	private SearchProteinSearcher() { }
	private static final SearchProteinSearcher _INSTANCE = new SearchProteinSearcher();
	public static SearchProteinSearcher getInstance() { return _INSTANCE; }
	

	public List<SearchProteinPosition> getProteinForUnlinked( int psmId, int peptideId, SearchDTO search ) throws Exception {
		List<SearchProteinPosition> proteinPositions = new ArrayList<SearchProteinPosition>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = 
		"(SELECT DISTINCT nrseq_id AS nseq " +
		" FROM unlinked " +
		" WHERE psm_id = ? AND peptide_id = ?) "; 
				
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );


			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, psmId );
			pstmt.setInt( 2, peptideId );
			
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
	
	

	public List<SearchProteinPosition> getProteinForDimer( int psmId, int peptideId, SearchDTO search ) throws Exception {
		
		List<SearchProteinPosition> proteinPositions = new ArrayList<SearchProteinPosition>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		

		final String sql = 
					"(SELECT nrseq_id_1 AS nseq " +
					" FROM dimer " +
					" WHERE psm_id = ? AND peptide_1_id = ?) " +

					"UNION " +

					"(SELECT nrseq_id_2 AS nseq " +
					"FROM dimer " +
					"WHERE psm_id = ? AND peptide_2_id = ?) "; 

		
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, psmId );
			pstmt.setInt( 2, peptideId );
			pstmt.setInt( 3, psmId );
			pstmt.setInt( 4, peptideId );
			
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
	
	
	
	public List<SearchProteinPosition> getProteinPositions( int psmId, int peptideId, int position, SearchDTO search ) throws Exception {
		
		List<SearchProteinPosition> proteinPositions = new ArrayList<SearchProteinPosition>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		

		final String sql = 
					"(SELECT nrseq_id_1 AS nseq, protein_1_position AS pos " +
					" FROM crosslink " +
					" WHERE psm_id = ? AND peptide_1_id = ? AND peptide_1_position = ?) " +

					"UNION " +

					"(SELECT nrseq_id_2 AS nseq, protein_2_position AS pos " +
					"FROM crosslink " +
					"WHERE psm_id = ? AND peptide_2_id = ? AND peptide_2_position = ?) "; 

		
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
//						+ "ORDER BY nseq, pos";
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, psmId );
			pstmt.setInt( 2, peptideId );
			pstmt.setInt( 3, position );
			pstmt.setInt( 4, psmId );
			pstmt.setInt( 5, peptideId );
			pstmt.setInt( 6, position );
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				SearchProteinPosition prpp = new SearchProteinPosition();
				prpp.setPosition( rs.getInt( 2 ) );
				prpp.setProtein( new SearchProtein( search, NRProteinDAO.getInstance().getNrProtein( rs.getInt( 1 ) ) ) );
				
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
	
	public List<SearchProteinDoublePosition> getProteinDoublePositions( int psmId, int peptideId, int position1, int position2, SearchDTO search ) throws Exception {
		
		List<SearchProteinDoublePosition> proteinPositions = new ArrayList<SearchProteinDoublePosition>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = 
				"SELECT DISTINCT nrseq_id AS nseq, protein_position_1 AS pos1, protein_position_2 AS pos2 " +
				"FROM looplink " +
				"WHERE psm_id = ? AND peptide_id = ? AND peptide_position_1 = ? AND peptide_position_2 = ? ";

		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, psmId);
			pstmt.setInt( 2, peptideId );
			pstmt.setInt( 3, position1 );
			pstmt.setInt( 4, position2 );
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				
				SearchProteinDoublePosition prpp = new SearchProteinDoublePosition();

				prpp.setProtein( new SearchProtein( search, NRProteinDAO.getInstance().getNrProtein( rs.getInt( 1 ) ) ) );
				prpp.setPosition1( rs.getInt( 2 ) );
				prpp.setPosition2( rs.getInt( 3 ) );
				
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
