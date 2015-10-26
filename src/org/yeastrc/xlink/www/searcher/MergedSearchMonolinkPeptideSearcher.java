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
import org.yeastrc.xlink.dao.MonolinkDAO;
import org.yeastrc.xlink.dao.PsmDAO;
import org.yeastrc.xlink.dao.ReportedPeptideDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.MonolinkDTO;
import org.yeastrc.xlink.dto.PsmDTO;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.objects.MergedSearchProteinMonolink;

public class MergedSearchMonolinkPeptideSearcher {

	
	private static final Logger log = Logger.getLogger(MergedSearchMonolinkPeptideSearcher.class);
			
			
	private MergedSearchMonolinkPeptideSearcher() { }
	public static MergedSearchMonolinkPeptideSearcher getInstance() { return new MergedSearchMonolinkPeptideSearcher(); }

	
	public List<ReportedPeptideDTO> getPeptides( MergedSearchProteinMonolink monolink ) throws Exception {
		List<ReportedPeptideDTO> peptides = new ArrayList<ReportedPeptideDTO>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = "SELECT distinct a.reported_peptide_id " +
					"FROM psm AS a INNER JOIN monolink AS b ON a.id = b.psm_id " +
					"INNER JOIN search_reported_peptide AS c ON a.reported_peptide_id = c.reported_peptide_id " +
					"WHERE a.q_value <= ? AND a.search_id IN (#SEARCHES#) AND  ( c.q_value <= ? OR c.q_value IS NULL )  AND c.search_id IN (#SEARCHES#) AND b.nrseq_id = ? AND b.protein_position = ?";
			
			
			Collection<Integer> searchIds = new HashSet<Integer>();
			for( SearchDTO search : monolink.getSearchProteinMonolinks().keySet() )
				searchIds.add( search.getId() );
			
			sql = sql.replaceAll( "#SEARCHES#", StringUtils.join( searchIds, "," ) );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setDouble( 1, monolink.getPsmCutoff() );
			pstmt.setDouble( 2, monolink.getPeptideCutoff() );
			pstmt.setInt( 3, monolink.getProtein().getNrProtein().getNrseqId() );
			pstmt.setInt( 4, monolink.getProteinPosition() );
			
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
	 * Get the number of distinct peptides (that is, distinct pair of monolinked peptides) found that identified the given monolinked proteins/positions
	 * @param monolink
	 * @return
	 * @throws Exception
	 */
	public int getNumPeptides( MergedSearchProteinMonolink monolink ) throws Exception {
		int count = 0;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = "SELECT COUNT(distinct a.reported_peptide_id) " +
					"FROM psm AS a INNER JOIN monolink AS b ON a.id = b.psm_id " +
					"INNER JOIN search_reported_peptide AS c ON a.reported_peptide_id = c.reported_peptide_id " +
					"WHERE a.q_value <= ? AND a.search_id IN (#SEARCHES#) AND  ( c.q_value <= ? OR c.q_value IS NULL )  AND c.search_id IN (#SEARCHES#) AND b.nrseq_id = ? AND b.protein_position = ?";
			
			
			Collection<Integer> searchIds = new HashSet<Integer>();
			for( SearchDTO search : monolink.getSearchProteinMonolinks().keySet() )
				searchIds.add( search.getId() );
			
			sql = sql.replaceAll( "#SEARCHES#", StringUtils.join( searchIds, "," ) );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setDouble( 1, monolink.getPsmCutoff() );
			pstmt.setDouble( 2, monolink.getPeptideCutoff() );
			pstmt.setInt( 3, monolink.getProtein().getNrProtein().getNrseqId() );
			pstmt.setInt( 4, monolink.getProteinPosition() );
			
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
	 * Get the number of peptides (pair of peptides) that UNIQUELY identified the pair of proteins+positions represented by this monolink
	 * @param monolink
	 * @return
	 * @throws Exception
	 */
	public int getNumUniquePeptides( MergedSearchProteinMonolink monolink ) throws Exception {
		
		int count = 0;
		

		Collection<SearchDTO> searches = monolink.getSearchProteinMonolinks().keySet();
		
		
		// iterate over each peptide, see which are unique in the contest of the FASTAs represented by
		// this merged search set
		for( ReportedPeptideDTO reportedPeptide : getPeptides( monolink ) ) {
			

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
			

			MonolinkDTO monolinkDTO = MonolinkDAO.getInstance().getMonolinkDTOByPsmId( psm.getId() );
			
			if ( monolinkDTO == null ) {
				
				String msg = "No Monolink found for psm.getId(): " + psm.getId();
				
				log.error( msg );
				
				throw new Exception( msg );
			}
			
			Collection<Integer> peptideIds = new ArrayList<>();
			
			peptideIds.add( monolinkDTO.getPeptideId() );
			
			
			
			if( ReportedPeptideSearcher.getInstance().isUnique( reportedPeptide, peptideIds, monolink.getSearchProteinMonolinks().keySet() ) ) {
				count++;			
			}
		}		
		
		return count;
	}
	
}
