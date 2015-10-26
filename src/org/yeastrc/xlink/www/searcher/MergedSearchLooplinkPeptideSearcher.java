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
import org.yeastrc.xlink.dao.LooplinkDAO;
import org.yeastrc.xlink.dao.PsmDAO;
import org.yeastrc.xlink.dao.ReportedPeptideDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.LooplinkDTO;
import org.yeastrc.xlink.dto.PsmDTO;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.objects.MergedSearchProteinLooplink;

public class MergedSearchLooplinkPeptideSearcher {
	
	private static final Logger log = Logger.getLogger(MergedSearchLooplinkPeptideSearcher.class);

	private MergedSearchLooplinkPeptideSearcher() { }
	public static MergedSearchLooplinkPeptideSearcher getInstance() { return new MergedSearchLooplinkPeptideSearcher(); }

	
	public List<ReportedPeptideDTO> getPeptides( MergedSearchProteinLooplink looplink ) throws Exception {
		List<ReportedPeptideDTO> peptides = new ArrayList<ReportedPeptideDTO>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = "SELECT distinct a.reported_peptide_id " +
					"FROM psm AS a INNER JOIN looplink AS b ON a.id = b.psm_id " +
					"INNER JOIN search_reported_peptide AS c ON a.reported_peptide_id = c.reported_peptide_id " +
					"WHERE a.q_value <= ? AND a.search_id IN (#SEARCHES#) AND  ( c.q_value <= ? OR c.q_value IS NULL )  AND c.search_id IN (#SEARCHES#) AND b.nrseq_id = ? AND b.protein_position_1 = ? AND b.protein_position_2 = ?";
			
			
			Collection<Integer> searchIds = new HashSet<Integer>();
			for( SearchDTO search : looplink.getSearchProteinLooplinks().keySet() )
				searchIds.add( search.getId() );
			
			sql = sql.replaceAll( "#SEARCHES#", StringUtils.join( searchIds, "," ) );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setDouble( 1, looplink.getPsmCutoff() );
			pstmt.setDouble( 2, looplink.getPeptideCutoff() );
			pstmt.setInt( 3, looplink.getProtein().getNrProtein().getNrseqId() );
			pstmt.setInt( 4, looplink.getProteinPosition1() );
			pstmt.setInt( 5, looplink.getProteinPosition2() );
			
			rs = pstmt.executeQuery();
			while( rs.next() ) {
				peptides.add(ReportedPeptideDAO.getInstance().getReportedPeptideFromDatabase( rs.getInt( 1 ) ) );
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
		
		
		return peptides;
	}
	
	/**
	 * Get the number of distinct peptides (that is, distinct pair of crosslinked peptides) found that identified the given crosslinked proteins/positions
	 * @param crosslink
	 * @return
	 * @throws Exception
	 */
	public int getNumPeptides( MergedSearchProteinLooplink looplink ) throws Exception {
		int count = 0;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = "SELECT COUNT(distinct a.reported_peptide_id) " +
					"FROM psm AS a INNER JOIN looplink AS b ON a.id = b.psm_id " +
					"INNER JOIN search_reported_peptide AS c ON a.reported_peptide_id = c.reported_peptide_id " +
					"WHERE a.q_value <= ? AND a.search_id IN (#SEARCHES#) AND  ( c.q_value <= ? OR c.q_value IS NULL )  AND c.search_id IN (#SEARCHES#) AND b.nrseq_id = ? AND b.protein_position_1 = ? AND b.protein_position_2 = ?";
			
			
			Collection<Integer> searchIds = new HashSet<Integer>();
			for( SearchDTO search : looplink.getSearchProteinLooplinks().keySet() )
				searchIds.add( search.getId() );
			
			sql = sql.replaceAll( "#SEARCHES#", StringUtils.join( searchIds, "," ) );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setDouble( 1, looplink.getPsmCutoff() );
			pstmt.setDouble( 2, looplink.getPeptideCutoff() );
			pstmt.setInt( 3, looplink.getProtein().getNrProtein().getNrseqId() );
			pstmt.setInt( 4, looplink.getProteinPosition1() );
			pstmt.setInt( 5, looplink.getProteinPosition2() );
			
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
	public int getNumUniquePeptides( MergedSearchProteinLooplink looplink ) throws Exception {
		

		Collection<SearchDTO> searches = looplink.getSearchProteinLooplinks().keySet();
		
		
		int count = 0;
		
		// iterate over each peptide, see which are unique in the contest of the FASTAs represented by
		// this merged search set
		for( ReportedPeptideDTO reportedPeptide : getPeptides( looplink ) ) {
			

			PsmDTO psm = null;
			
			for ( SearchDTO search : searches ) {

				psm = PsmDAO.getInstance().getOnePsmDTOForSearchIdAndReportedPeptideId( reportedPeptide.getId(), search.getId() );
				
				if ( psm != null ) {
					
					break;
				}
			}
			
			if ( psm == null ) {
				

				String msg = "Skipping Reported Peptide:  No PSMs found for reportedPeptide.getId(): " + reportedPeptide.getId();
				
				log.warn( msg );
				
				continue;
			}
			

			LooplinkDTO looplinkDTO = LooplinkDAO.getInstance().getLooplinkDTOByPsmId( psm.getId() );
			
			if ( looplinkDTO == null ) {
				
				String msg = "No Looplink found for psm.getId(): " + psm.getId();
				
				log.error( msg );
				
				throw new Exception( msg );
			}
			
			Collection<Integer> peptideIds = new ArrayList<>();
			
			peptideIds.add( looplinkDTO.getPeptideId() );
			
			
			if( ReportedPeptideSearcher.getInstance().isUnique( reportedPeptide, peptideIds, looplink.getSearchProteinLooplinks().keySet() ) ) {
				count++;			
			}
		}		
		
		return count;
	}
	
}
