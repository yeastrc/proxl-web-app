package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.xlink.dao.NRProteinDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.objects.SearchProtein;
import org.yeastrc.xlink.www.objects.SearchProteinDoublePosition;
import org.yeastrc.xlink.www.objects.SearchProteinPosition;
import org.yeastrc.xlink.utils.XLinkUtils;

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
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = 
						"(SELECT DISTINCT a.nrseq_id AS nseq " +
						"FROM unlinked AS a INNER JOIN psm AS b ON a.psm_id = b.id " +
						"WHERE b.search_id = ? AND a.peptide_id = ?) " +

						"ORDER BY nseq";
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, search.getId() );
			pstmt.setInt( 2, peptide.getId() );
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				SearchProteinPosition prpp = new SearchProteinPosition();
				prpp.setProtein( new SearchProtein( search, NRProteinDAO.getInstance().getNrProtein( rs.getInt( 1 ) ) ) );
				
				proteinPositions.add( prpp );
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
		
		return proteinPositions;
	}
	
	

	public List<SearchProteinPosition> getProteinForDimer( SearchDTO search, PeptideDTO peptide ) throws Exception {
		List<SearchProteinPosition> proteinPositions = new ArrayList<SearchProteinPosition>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = 
					"(SELECT a.nrseq_id_1 AS nseq " +
					"FROM dimer AS a INNER JOIN psm AS b ON a.psm_id = b.id " +
					"WHERE b.search_id = ? AND a.peptide_1_id = ? ) " +

					"UNION DISTINCT " +

					"(SELECT a.nrseq_id_2 AS nseq " +
					"FROM dimer AS a INNER JOIN psm AS b ON a.psm_id = b.id " +
					"WHERE b.search_id = ? AND a.peptide_2_id = ? ) " +

					"ORDER BY nseq";
			
			
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
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = 
						"(SELECT a.nrseq_id_1 AS nseq, a.protein_1_position AS pos " +
						"FROM crosslink AS a INNER JOIN psm AS b ON a.psm_id = b.id " +
						"WHERE b.search_id = ? AND a.peptide_1_id = ? AND a.peptide_1_position = ?) " +

						"UNION " +

						"(SELECT a.nrseq_id_2 AS nseq, a.protein_2_position AS pos " +
						"FROM crosslink AS a INNER JOIN psm AS b ON a.psm_id = b.id " +
						"WHERE b.search_id = ? AND a.peptide_2_id = ? AND a.peptide_2_position = ?) " +

						"ORDER BY nseq, pos";
			
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
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = 
						"SELECT DISTINCT a.nrseq_id AS nseq, a.protein_position_1 AS pos1, a.protein_position_2 AS pos2 " +
						"FROM looplink AS a INNER JOIN psm AS b ON a.psm_id = b.id " +
						"WHERE b.search_id = ? AND a.peptide_id = ? AND a.peptide_position_1 = ? AND a.peptide_position_2 = ? " +

						"ORDER BY nseq, pos1, pos2";
			
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
	 * Determine whether or not the given position in the given protein has a given link type in the given search with the given cutoffs
	 * @param nrseqId
	 * @param position
	 * @param search
	 * @param type
	 * @param psmQValueCutoff
	 * @param peptideQValueCutoff
	 * @return
	 * @throws Exception
	 */
	public boolean isLinked( int nrseqId, int position, SearchDTO search, int type, double psmQValueCutoff, double peptideQValueCutoff ) throws Exception {
		boolean isLinked = false;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			if( type == XLinkUtils.TYPE_CROSSLINK ) {
				String sql = "SELECT COUNT(*) FROM search_crosslink_lookup WHERE "
						+ "( ( nrseq_id_1 = ? AND protein_1_position = ? ) OR ( nrseq_id_2 = ? AND protein_2_position = ? ) ) "
						+ "AND bestPSMQValue <= ? AND  ( bestPeptideQValue <= ? OR bestPeptideQValue IS NULL )  AND search_id = ?";
				
				pstmt = conn.prepareStatement( sql );
				pstmt.setInt( 1,  nrseqId );
				pstmt.setInt( 2,  position );
				pstmt.setInt( 3,  nrseqId );
				pstmt.setInt( 4,  position );
				pstmt.setDouble( 5,  psmQValueCutoff );
				pstmt.setDouble( 6,  peptideQValueCutoff );
				pstmt.setInt( 7,  search.getId() );
				
			} else if( type == XLinkUtils.TYPE_LOOPLINK ) {
				String sql = "SELECT COUNT(*) FROM search_looplink_lookup WHERE "
						+ "nrseq_id = ? AND ( protein_position_1 = ? OR protein_position_2 = ? ) "
						+ "AND bestPSMQValue <= ? AND  ( bestPeptideQValue <= ? OR bestPeptideQValue IS NULL )  AND search_id = ?";
				
				pstmt = conn.prepareStatement( sql );
				pstmt.setInt( 1,  nrseqId );
				pstmt.setInt( 2,  position );
				pstmt.setInt( 3,  position );
				pstmt.setDouble( 4,  psmQValueCutoff );
				pstmt.setDouble( 5,  peptideQValueCutoff );
				pstmt.setInt( 6,  search.getId() );
			} else if( type == XLinkUtils.TYPE_MONOLINK ) {
				String sql = "SELECT COUNT(*) FROM search_monolink_lookup WHERE "
						+ "nrseq_id = ? AND protein_position = ? "
						+ "AND bestPSMQValue <= ? AND  ( bestPeptideQValue <= ? OR bestPeptideQValue IS NULL )  AND search_id = ?";
				
				pstmt = conn.prepareStatement( sql );
				pstmt.setInt( 1,  nrseqId );
				pstmt.setInt( 2,  position );
				pstmt.setDouble( 3,  psmQValueCutoff );
				pstmt.setDouble( 4,  peptideQValueCutoff );
				pstmt.setInt( 5,  search.getId() );
			} else {
				return false;
			}

			
			rs = pstmt.executeQuery();
			rs.next();
			
			if( rs.getInt( 1 ) > 0 )
				isLinked = true;
			
			
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
		
		
		return isLinked;
	}
	
}
