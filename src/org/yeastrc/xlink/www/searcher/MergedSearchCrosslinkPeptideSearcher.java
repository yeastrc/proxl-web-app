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
import org.yeastrc.xlink.dao.CrosslinkDAO;
import org.yeastrc.xlink.dao.PsmDAO;
import org.yeastrc.xlink.dao.ReportedPeptideDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.CrosslinkDTO;
import org.yeastrc.xlink.dto.PsmDTO;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.objects.MergedSearchProteinCrosslink;



public class MergedSearchCrosslinkPeptideSearcher {
	
	private static final Logger log = Logger.getLogger(MergedSearchCrosslinkPeptideSearcher.class);
			

	private MergedSearchCrosslinkPeptideSearcher() { }
	public static MergedSearchCrosslinkPeptideSearcher getInstance() { return new MergedSearchCrosslinkPeptideSearcher(); }

	
	public List<ReportedPeptideDTO> getLinkedPeptides( MergedSearchProteinCrosslink crosslink ) throws Exception {
		List<ReportedPeptideDTO> reportedPeptides = new ArrayList<ReportedPeptideDTO>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = "SELECT DISTINCT a.reported_peptide_id " +
					"FROM psm AS a INNER JOIN crosslink AS b ON a.id = b.psm_id " +
					"INNER JOIN search_reported_peptide AS c ON a.reported_peptide_id = c.reported_peptide_id " +
					"WHERE a.q_value <= ? AND a.search_id IN (#SEARCHES#) AND ( c.q_value <= ? OR c.q_value IS NULL ) " +
					"   AND c.search_id IN (#SEARCHES#) " +
					"   AND b.nrseq_id_1 = ? AND b.nrseq_id_2 = ? AND b.protein_1_position = ? AND b.protein_2_position = ?";
			
			Collection<Integer> searchIds = new HashSet<Integer>();
			for( SearchDTO search : crosslink.getSearchProteinCrosslinks().keySet() )
				searchIds.add( search.getId() );
			
			sql = sql.replaceAll( "#SEARCHES#", StringUtils.join( searchIds, "," ) );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setDouble( 1, crosslink.getPsmCutoff() );
			pstmt.setDouble( 2, crosslink.getPeptideCutoff() );
			pstmt.setInt( 3, crosslink.getProtein1().getNrProtein().getNrseqId() );
			pstmt.setInt( 4, crosslink.getProtein2().getNrProtein().getNrseqId() );
			pstmt.setInt( 5, crosslink.getProtein1Position() );
			pstmt.setInt( 6, crosslink.getProtein2Position() );
			
			rs = pstmt.executeQuery();

			while( rs.next() )
				reportedPeptides.add(ReportedPeptideDAO.getInstance().getReportedPeptideFromDatabase( rs.getInt( 1 ) ) );
			
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
		
		return reportedPeptides;
	}
	
	/**
	 * Get the number of distinct peptides (that is, distinct pair of crosslinked peptides) found that identified the given crosslinked proteins/positions
	 * @param crosslink
	 * @return
	 * @throws Exception
	 */
	public int getNumLinkedPeptides( MergedSearchProteinCrosslink crosslink ) throws Exception {
		int count = 0;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = "SELECT COUNT(distinct a.reported_peptide_id) " +
					"FROM psm AS a INNER JOIN crosslink AS b ON a.id = b.psm_id " +
					"INNER JOIN search_reported_peptide AS c ON a.reported_peptide_id = c.reported_peptide_id " +
					"WHERE a.q_value <= ? AND a.search_id IN (#SEARCHES#) AND  ( c.q_value <= ? OR c.q_value IS NULL ) " +
					"   AND c.search_id IN (#SEARCHES#) " +
					"   AND b.nrseq_id_1 = ? AND b.nrseq_id_2 = ? AND b.protein_1_position = ? AND b.protein_2_position = ?";
			
			Collection<Integer> searchIds = new HashSet<Integer>();
			for( SearchDTO search : crosslink.getSearchProteinCrosslinks().keySet() )
				searchIds.add( search.getId() );
			
			sql = sql.replaceAll( "#SEARCHES#", StringUtils.join( searchIds, "," ) );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setDouble( 1, crosslink.getPsmCutoff() );
			pstmt.setDouble( 2, crosslink.getPeptideCutoff() );
			pstmt.setInt( 3, crosslink.getProtein1().getNrProtein().getNrseqId() );
			pstmt.setInt( 4, crosslink.getProtein2().getNrProtein().getNrseqId() );
			pstmt.setInt( 5, crosslink.getProtein1Position() );
			pstmt.setInt( 6, crosslink.getProtein2Position() );
			
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
	 * Get the number of peptides (pair of peptides) that UNIQUELY identified the pair of proteins+positions represented by this
	 * crosslink in the context of the merged searches
	 * @param crosslink
	 * @return
	 * @throws Exception
	 */
	public int getNumUniqueLinkedPeptides( MergedSearchProteinCrosslink crosslink ) throws Exception {
		
		try {

			int count = 0;


			Collection<SearchDTO> searches = crosslink.getSearchProteinCrosslinks().keySet();

			// iterate over each peptide, see which are unique in the contest of the FASTAs represented by
			// this merged search set
			for( ReportedPeptideDTO reportedPeptide : getLinkedPeptides( crosslink ) ) {

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


				CrosslinkDTO crosslinkDTO = CrosslinkDAO.getInstance().getCrosslinkDTOByPsmId( psm.getId() );

				if ( crosslinkDTO == null ) {

					String msg = "No Crosslink found for psm.getId(): " + psm.getId();

					log.error( msg );

					throw new Exception( msg );
				}

				Collection<Integer> peptideIds = new ArrayList<>();

				peptideIds.add( crosslinkDTO.getPeptide1Id() );

				if ( crosslinkDTO.getPeptide1Id() != crosslinkDTO.getPeptide2Id() ) {

					peptideIds.add( crosslinkDTO.getPeptide2Id() );
				}


				if( ReportedPeptideSearcher.getInstance().isUnique( reportedPeptide, peptideIds, crosslink.getSearchProteinCrosslinks().keySet() ) ) {

					count++;
				}
			}		

			return count;

		} catch ( Exception e ) {
			
			String msg = "Exception in getNumUniqueLinkedPeptides( MergedSearchProteinCrosslink crosslink ): " 
					+ " crosslink.getProtein1().getNrProtein().getNrseqId(): " + crosslink.getProtein1().getNrProtein().getNrseqId()
					+ " crosslink.getProtein2().getNrProtein().getNrseqId(): " + crosslink.getProtein2().getNrProtein().getNrseqId();
			
			log.error( msg, e );
			
			throw e;
		}
	}
	
	
}
