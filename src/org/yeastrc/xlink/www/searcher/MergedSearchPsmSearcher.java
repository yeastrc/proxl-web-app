package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.lang3.StringUtils;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.objects.MergedSearchProteinCrosslink;
import org.yeastrc.xlink.www.objects.MergedSearchProteinLooplink;
import org.yeastrc.xlink.www.objects.MergedSearchProteinMonolink;

public class MergedSearchPsmSearcher {

	private MergedSearchPsmSearcher() { }
	private static final MergedSearchPsmSearcher _INSTANCE = new MergedSearchPsmSearcher();
	public static MergedSearchPsmSearcher getInstance() { return _INSTANCE; }
	
	/**
	 * Get the number of PSMs in the database corresponding to the given monolinks with its given cutoffs
	 * @param monolink
	 * @return
	 * @throws Exception
	 */
	public int getNumPsms( MergedSearchProteinMonolink monolink ) throws Exception {
		int count = 0;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = 
						"SELECT COUNT(*) FROM psm AS a INNER JOIN search_reported_peptide AS b ON ( a.search_id = b.search_id AND a.reported_peptide_id = b.reported_peptide_id ) "
						+ "INNER JOIN monolink AS c ON a.id = c.psm_id WHERE a.search_id IN (#SEARCHES#) AND a.q_value <= ? AND  ( b.q_value <= ? OR b.q_value IS NULL )  AND "
						+ "c.nrseq_id = ? AND c.protein_position = ?";

			
			Collection<Integer> searchIds = new HashSet<Integer>();
			for( SearchDTO search : monolink.getSearches() )
				searchIds.add( search.getId() );
			
			sql = sql.replaceAll( "#SEARCHES#", StringUtils.join( searchIds, "," ) );
			
			
			pstmt = conn.prepareStatement( sql );

			pstmt.setDouble( 1,  monolink.getPsmCutoff() );
			pstmt.setDouble( 2,  monolink.getPeptideCutoff() );
			pstmt.setInt( 3,  monolink.getProtein().getNrProtein().getNrseqId() );
			pstmt.setInt( 4,  monolink.getProteinPosition() );

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
	
	/**
	 * Get the number of PSMs in the database corresponding to the given looplinks with its given cutoffs
	 * @param looplink
	 * @return
	 * @throws Exception
	 */
	public int getNumPsms( MergedSearchProteinLooplink looplink ) throws Exception {
		int count = 0;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = 
						"SELECT COUNT(*) FROM psm AS a INNER JOIN search_reported_peptide AS b ON ( a.search_id = b.search_id AND a.reported_peptide_id = b.reported_peptide_id ) "
						+ "INNER JOIN looplink AS c ON a.id = c.psm_id WHERE a.search_id IN (#SEARCHES#) AND a.q_value <= ? AND  ( b.q_value <= ? OR b.q_value IS NULL )   AND "
						+ "c.nrseq_id = ? AND c.protein_position_1 = ? AND c.protein_position_2 = ? ";

			
			Collection<Integer> searchIds = new HashSet<Integer>();
			for( SearchDTO search : looplink.getSearches() )
				searchIds.add( search.getId() );
			
			sql = sql.replaceAll( "#SEARCHES#", StringUtils.join( searchIds, "," ) );
			
			
			pstmt = conn.prepareStatement( sql );

			pstmt.setDouble( 1,  looplink.getPsmCutoff() );
			pstmt.setDouble( 2,  looplink.getPeptideCutoff() );
			pstmt.setInt( 3,  looplink.getProtein().getNrProtein().getNrseqId() );
			pstmt.setInt( 4,  looplink.getProteinPosition1() );
			pstmt.setInt( 5,  looplink.getProteinPosition2() );

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
	
	/**
	 * Get the number of PSMs in the database corresponding to the given crosslinks with its given cutoffs
	 * @param crosslink
	 * @return
	 * @throws Exception
	 */
	public int getNumPsms( MergedSearchProteinCrosslink crosslink ) throws Exception {
		int count = 0;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = 
						"SELECT COUNT(*) FROM psm AS a INNER JOIN search_reported_peptide AS b ON ( a.search_id = b.search_id AND a.reported_peptide_id = b.reported_peptide_id ) "
						+ "INNER JOIN crosslink AS c ON a.id = c.psm_id WHERE a.search_id IN (#SEARCHES#) AND a.q_value <= ? AND  ( b.q_value <= ? OR b.q_value IS NULL )   AND "
						+ "c.nrseq_id_1 = ? AND c.nrseq_id_2 = ? AND c.protein_1_position = ? AND c.protein_2_position = ? ";

			
			Collection<Integer> searchIds = new HashSet<Integer>();
			for( SearchDTO search : crosslink.getSearches() )
				searchIds.add( search.getId() );
			
			sql = sql.replaceAll( "#SEARCHES#", StringUtils.join( searchIds, "," ) );
			
			
			pstmt = conn.prepareStatement( sql );

			pstmt.setDouble( 1,  crosslink.getPsmCutoff() );
			pstmt.setDouble( 2,  crosslink.getPeptideCutoff() );
			pstmt.setInt( 3,  crosslink.getProtein1().getNrProtein().getNrseqId() );
			pstmt.setInt( 4,  crosslink.getProtein2().getNrProtein().getNrseqId() );
			pstmt.setInt( 5,  crosslink.getProtein1Position() );
			pstmt.setInt( 6,  crosslink.getProtein2Position() );

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
	
}
