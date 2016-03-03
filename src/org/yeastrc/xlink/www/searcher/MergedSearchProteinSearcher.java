package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.NRProteinDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.objects.MergedSearchProtein;
import org.yeastrc.xlink.www.objects.MergedSearchProteinDoublePosition;
import org.yeastrc.xlink.www.objects.MergedSearchProteinPosition;


public class MergedSearchProteinSearcher {

	private static final Logger log = Logger.getLogger(MergedSearchProteinSearcher.class);

	private MergedSearchProteinSearcher() { }
	private static final MergedSearchProteinSearcher _INSTANCE = new MergedSearchProteinSearcher();
	public static MergedSearchProteinSearcher getInstance() { return _INSTANCE; }
	
	
	
	/**
	 * For the given collection of searches, the given peptide, and given position in that peptide, find all proteins
	 * and the respective position(s) in those proteins to which that peptide and position match.
	 * @param searches
	 * @param peptide
	 * @param position
	 * @return
	 * @throws Exception
	 */
	public List<MergedSearchProteinPosition> getProteinPositions( Collection<SearchDTO> searches, PeptideDTO peptide, int position ) throws Exception {
		
		if (  peptide == null ) {
			
			String msg = "'peptide' parameter cannot be null";
			
			log.error( msg );
			
			throw new IllegalArgumentException( msg );
		}
		
		
		
		List<MergedSearchProteinPosition> proteinPositions = new ArrayList<MergedSearchProteinPosition>();
				
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = 
						"(SELECT a.nrseq_id_1 AS nseq, a.protein_1_position AS pos " +
						"FROM crosslink AS a INNER JOIN psm AS b ON a.psm_id = b.id " +
						"WHERE b.search_id IN (#SEARCHES#) AND a.peptide_1_id = ? AND a.peptide_1_position = ?) " +

						"UNION " +

						"(SELECT a.nrseq_id_2 AS nseq, a.protein_2_position AS pos " +
						"FROM crosslink AS a INNER JOIN psm AS b ON a.psm_id = b.id " +
						"WHERE b.search_id IN (#SEARCHES#) AND a.peptide_2_id = ? AND a.peptide_2_position = ?) " +

						"ORDER BY nseq, pos";

			Collection<Integer> searchIds = new HashSet<Integer>();
			for( SearchDTO search : searches )
				searchIds.add( search.getId() );
			
			sql = sql.replaceAll( "#SEARCHES#", StringUtils.join( searchIds, "," ) );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, peptide.getId() );
			pstmt.setInt( 2, position );
			pstmt.setInt( 3, peptide.getId() );
			pstmt.setInt( 4, position );
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				MergedSearchProteinPosition mrpp = new MergedSearchProteinPosition();
				mrpp.setPosition( rs.getInt( 2 ) );
				mrpp.setProtein( new MergedSearchProtein( searches, NRProteinDAO.getInstance().getNrProtein( rs.getInt( 1 ) ) ) );
				
				proteinPositions.add( mrpp );
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
	 * For the given collection of searches, the given peptide, and given position in that peptide, find all proteins
	 * and the respective position(s) in those proteins to which that peptide and position match.
	 * @param searches
	 * @param peptide
	 * @param position
	 * @return
	 * @throws Exception
	 */
	public List<MergedSearchProteinDoublePosition> getProteinPositions( Collection<SearchDTO> searches, PeptideDTO peptide, int position1, int position2 ) throws Exception {
		List<MergedSearchProteinDoublePosition> proteinPositions = new ArrayList<MergedSearchProteinDoublePosition>();
				
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = 
						"SELECT DISTINCT a.nrseq_id AS nseq, a.protein_position_1 AS pos1, a.protein_position_2 AS pos2 " +
						"FROM looplink AS a INNER JOIN psm AS b ON a.psm_id = b.id " +
						"WHERE b.search_id IN (#SEARCHES#) AND a.peptide_id = ? AND a.peptide_position_1 = ? AND a.peptide_position_2 = ?" +

						" ORDER BY nseq, pos1, pos2";

			Collection<Integer> searchIds = new HashSet<Integer>();
			for( SearchDTO search : searches )
				searchIds.add( search.getId() );
			
			sql = sql.replaceAll( "#SEARCHES#", StringUtils.join( searchIds, "," ) );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, peptide.getId() );
			pstmt.setInt( 2, position1 );
			pstmt.setInt( 3, position2 );
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				MergedSearchProteinDoublePosition mrpp = new MergedSearchProteinDoublePosition();
				mrpp.setPosition1( rs.getInt( 2 ) );
				mrpp.setPosition2( rs.getInt( 3 ) );
				mrpp.setProtein( new MergedSearchProtein( searches, NRProteinDAO.getInstance().getNrProtein( rs.getInt( 1 ) ) ) );
				
				proteinPositions.add( mrpp );
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

	

	public List<MergedSearchProteinPosition> getProteinForUnlinked( Collection<SearchDTO> searches, PeptideDTO peptide ) throws Exception {
		
		
		List<MergedSearchProteinPosition> proteinPositions = new ArrayList<MergedSearchProteinPosition>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = 
						"(SELECT DISTINCT a.nrseq_id AS nseq " +
						"FROM unlinked AS a INNER JOIN psm AS b ON a.psm_id = b.id " +
						"WHERE b.search_id IN (#SEARCHES#) AND a.peptide_id = ?) " +

						"ORDER BY nseq";
			
			Collection<Integer> searchIds = new HashSet<Integer>();
			for( SearchDTO search : searches )
				searchIds.add( search.getId() );
			
			sql = sql.replaceAll( "#SEARCHES#", StringUtils.join( searchIds, "," ) );
			
			pstmt = conn.prepareStatement( sql );

			pstmt.setInt( 1, peptide.getId() );
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				MergedSearchProteinPosition mrpp = new MergedSearchProteinPosition();
				mrpp.setProtein( new MergedSearchProtein( searches, NRProteinDAO.getInstance().getNrProtein( rs.getInt( 1 ) ) ) );

				
				proteinPositions.add( mrpp );
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
	

	public List<MergedSearchProteinPosition> getProteinForDimer( Collection<SearchDTO> searches, PeptideDTO peptide ) throws Exception {
		List<MergedSearchProteinPosition> proteinPositions = new ArrayList<MergedSearchProteinPosition>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = 
					"(SELECT a.nrseq_id_1 AS nseq " +
					"FROM dimer AS a INNER JOIN psm AS b ON a.psm_id = b.id " +
					"WHERE b.search_id  IN (#SEARCHES#) AND a.peptide_1_id = ? ) " +

					"UNION DISTINCT " +

					"(SELECT a.nrseq_id_2 AS nseq " +
					"FROM dimer AS a INNER JOIN psm AS b ON a.psm_id = b.id " +
					"WHERE b.search_id  IN (#SEARCHES#) AND a.peptide_2_id = ? ) " +

					"ORDER BY nseq";
			
			
			Collection<Integer> searchIds = new HashSet<Integer>();
			for( SearchDTO search : searches )
				searchIds.add( search.getId() );
			
			sql = sql.replaceAll( "#SEARCHES#", StringUtils.join( searchIds, "," ) );
			
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, peptide.getId() );
			pstmt.setInt( 2, peptide.getId() );
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				MergedSearchProteinPosition mrpp = new MergedSearchProteinPosition();
				mrpp.setProtein( new MergedSearchProtein( searches, NRProteinDAO.getInstance().getNrProtein( rs.getInt( 1 ) ) ) );

				
				proteinPositions.add( mrpp );
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
	
}
