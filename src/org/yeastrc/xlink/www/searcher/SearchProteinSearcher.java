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
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.objects.SearchProtein;
import org.yeastrc.xlink.www.objects.SearchProteinDoublePosition;
import org.yeastrc.xlink.www.objects.SearchProteinPosition;

public class SearchProteinSearcher {


	private SearchProteinSearcher() { }
	private static final SearchProteinSearcher _INSTANCE = new SearchProteinSearcher();
	public static SearchProteinSearcher getInstance() { return _INSTANCE; }
	

	public List<SearchProteinPosition> getProteinForUnlinked( SearchDTO search, PeptideDTO peptide ) throws Exception {
		List<SearchProteinPosition> proteinPositions = new ArrayList<SearchProteinPosition>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			String sql = 
						"(SELECT DISTINCT a.nrseq_id AS nseq " +
						"FROM unlinked AS a INNER JOIN psm AS b ON a.psm_id = b.id " +
						"WHERE b.search_id = ? AND a.peptide_id = ?) ";

//						"ORDER BY nseq";
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, search.getId() );
			pstmt.setInt( 2, peptide.getId() );
			
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
	
	

	public List<SearchProteinPosition> getProteinForDimer( SearchDTO search, PeptideDTO peptide ) throws Exception {
		List<SearchProteinPosition> proteinPositions = new ArrayList<SearchProteinPosition>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			String sql = 
					"(SELECT a.nrseq_id_1 AS nseq " +
					"FROM dimer AS a INNER JOIN psm AS b ON a.psm_id = b.id " +
					"WHERE b.search_id = ? AND a.peptide_1_id = ? ) " +

					"UNION DISTINCT " +

					"(SELECT a.nrseq_id_2 AS nseq " +
					"FROM dimer AS a INNER JOIN psm AS b ON a.psm_id = b.id " +
					"WHERE b.search_id = ? AND a.peptide_2_id = ? ) ";

//					"ORDER BY nseq";
			
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, search.getId() );
			pstmt.setInt( 2, peptide.getId() );
			pstmt.setInt( 3, search.getId() );
			pstmt.setInt( 4, peptide.getId() );
			
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
	
	
	
	public List<SearchProteinPosition> getProteinPositions( SearchDTO search, PeptideDTO peptide, int position ) throws Exception {
		List<SearchProteinPosition> proteinPositions = new ArrayList<SearchProteinPosition>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			String sql = 
						"(SELECT a.nrseq_id_1 AS nseq, a.protein_1_position AS pos " +
						"FROM crosslink AS a INNER JOIN psm AS b ON a.psm_id = b.id " +
						"WHERE b.search_id = ? AND a.peptide_1_id = ? AND a.peptide_1_position = ?) " +

						"UNION " +

						"(SELECT a.nrseq_id_2 AS nseq, a.protein_2_position AS pos " +
						"FROM crosslink AS a INNER JOIN psm AS b ON a.psm_id = b.id " +
						"WHERE b.search_id = ? AND a.peptide_2_id = ? AND a.peptide_2_position = ?) "; 

//						+ "ORDER BY nseq, pos";
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, search.getId() );
			pstmt.setInt( 2, peptide.getId() );
			pstmt.setInt( 3, position );
			pstmt.setInt( 4, search.getId() );
			pstmt.setInt( 5, peptide.getId() );
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
	
	public List<SearchProteinDoublePosition> getProteinDoublePositions( SearchDTO search, PeptideDTO peptide, int position1, int position2 ) throws Exception {
		List<SearchProteinDoublePosition> proteinPositions = new ArrayList<SearchProteinDoublePosition>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			String sql = 
						"SELECT DISTINCT a.nrseq_id AS nseq, a.protein_position_1 AS pos1, a.protein_position_2 AS pos2 " +
						"FROM looplink AS a INNER JOIN psm AS b ON a.psm_id = b.id " +
						"WHERE b.search_id = ? AND a.peptide_id = ? AND a.peptide_position_1 = ? AND a.peptide_position_2 = ? ";

//						"ORDER BY nseq, pos1, pos2";
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, search.getId() );
			pstmt.setInt( 2, peptide.getId() );
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
