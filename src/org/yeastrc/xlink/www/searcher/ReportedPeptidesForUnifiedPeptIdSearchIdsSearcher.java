package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.ReportedPeptideDAO;
import org.yeastrc.xlink.dao.SearchDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.constants.DefaultQValueCutoffConstants;
import org.yeastrc.xlink.www.objects.ReportedPeptidesForUnifiedPeptIdMergedPeptidePage;

/**
 * Get the Reported Peptides / Search combinations for a given Unified Reported Peptide id and searchIds
 *
 * This list is used on the Merged Peptide page as the details for each top level entry
 */
public class ReportedPeptidesForUnifiedPeptIdSearchIdsSearcher {

	private static final Logger log = Logger.getLogger(ReportedPeptidesForUnifiedPeptIdSearchIdsSearcher.class);

	private ReportedPeptidesForUnifiedPeptIdSearchIdsSearcher() { }
	public static ReportedPeptidesForUnifiedPeptIdSearchIdsSearcher getInstance() { return new ReportedPeptidesForUnifiedPeptIdSearchIdsSearcher(); }
	
	

//	private static final String getReportedPeptideSearchCombinationsSQL = 
//			"SELECT search_reported_peptide.search_id,  search_reported_peptide.reported_peptide_id, search_reported_peptide.q_value,"
//			+ 				" count(*) AS num_psms, min(psm.q_value) AS best_psm_q_value " 
//			+ 				" , psrp.svm_score, psrp.pep, psrp.p_value " 
//
//			+ " FROM search_reported_peptide  "
//			+ " INNER JOIN psm  " 
//			+ " ON (search_reported_peptide.search_id = psm.search_id AND search_reported_peptide.reported_peptide_id = psm.reported_peptide_id) "
//			
//			+ " LEFT OUTER JOIN percolator_search_reported_peptide AS psrp "
//			+ " ON (search_reported_peptide.search_id = psrp.search_id AND search_reported_peptide.reported_peptide_id = psrp.reported_peptide_id) "
//			
//			+ " WHERE  search_reported_peptide.unified_reported_peptide_id = ? AND search_reported_peptide.search_id IN ( #SEARCHES# )  "
//			
//			+      " AND ( search_reported_peptide.q_value <= ? OR search_reported_peptide.q_value IS NULL )  "			
//			+      " AND psm.q_value <=?  "
//			
//			+ " GROUP BY search_reported_peptide.search_id, search_reported_peptide.reported_peptide_id "
//			
//			+ " ORDER BY search_reported_peptide.search_id, search_reported_peptide.reported_peptide_id";
//	

	private static final String getReportedPeptideSearchCombinationsSQL = 
			"SELECT unified_rep_pep__reported_peptide__search_lookup.unified_reported_peptide_id, "
			
			+ " unified_rep_pep__reported_peptide__search_lookup.reported_peptide_id, "
			+ " unified_rep_pep__reported_peptide__search_lookup.search_id, "
			+ " unified_rep_pep__reported_peptide__search_lookup.peptide_q_value_for_search, "
//			
//			+ " unified_rep_pep__reported_peptide__search_lookup.best_psm_q_value,"
//			+ " unified_rep_pep__reported_peptide__search_lookup.link_type, "
			+ " unified_rep_pep__reported_peptide__search_lookup.psm_num_at_pt_01_q_cutoff, "
			+ " psrp.svm_score, psrp.pep, psrp.p_value " 
			
			+ " FROM "
			
			+ " unified_rep_pep__reported_peptide__search_lookup "
			+ " LEFT OUTER JOIN percolator_search_reported_peptide AS psrp "
			+ " ON (unified_rep_pep__reported_peptide__search_lookup.search_id = psrp.search_id AND unified_rep_pep__reported_peptide__search_lookup.reported_peptide_id = psrp.reported_peptide_id) "
			
			
			+ " WHERE unified_rep_pep__reported_peptide__search_lookup.search_id IN ( #SEARCHES# ) "
			
					+ " AND unified_rep_pep__reported_peptide__search_lookup.unified_reported_peptide_id = ? "
			
					+ " AND ( unified_rep_pep__reported_peptide__search_lookup.peptide_q_value_for_search <= ? "
					+ 		"	OR unified_rep_pep__reported_peptide__search_lookup.peptide_q_value_for_search IS NULL )   "
					
					+ " AND ( unified_rep_pep__reported_peptide__search_lookup.best_psm_q_value <= ? )   "
	  		;

			  
	/**
	 * Get the list of Reported Peptides / Search combinations for a given Unified Reported Peptide id and searchIds
	 * @param search
	 * @param psmQValueCutoff
	 * @param peptideQValueCutoff
	 * @param reportedPeptideId
	 * @return
	 * @throws Exception
	 */
	public List<ReportedPeptidesForUnifiedPeptIdMergedPeptidePage> getReportedPeptideSearchCombinations( List<Integer> searchIdList, double psmQValueCutoff, double peptideQValueCutoff, int unifiedReportedPeptideId ) throws Exception {
		
		if ( searchIdList == null || searchIdList.isEmpty() ) {
			
			throw new IllegalArgumentException( "searchIdList cannot be null or empty" );
		}
		
		List<ReportedPeptidesForUnifiedPeptIdMergedPeptidePage> results = new ArrayList<>();
				
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String searchIdsForInClause = StringUtils.join( searchIdList, "," );
		
		final String sql = getReportedPeptideSearchCombinationsSQL.replace( "#SEARCHES#", searchIdsForInClause );

		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			
			pstmt = conn.prepareStatement( sql );

			pstmt.setInt( 1, unifiedReportedPeptideId );
			pstmt.setDouble( 2, peptideQValueCutoff );
			pstmt.setDouble( 3, psmQValueCutoff );
			
			rs = pstmt.executeQuery();


			//  Search temp cache 
			Map<Integer, SearchDTO> searches = new HashMap<Integer, SearchDTO>();
			

			while( rs.next() ) {
				
				ReportedPeptidesForUnifiedPeptIdMergedPeptidePage reportedPeptideData = new ReportedPeptidesForUnifiedPeptIdMergedPeptidePage();
				
				reportedPeptideData.setSearchId( rs.getInt( "search_id" ) );
				

				//  Get searchDTOs for the search ids
				Integer searchId = reportedPeptideData.getSearchId();

				SearchDTO search = searches.get( searchId );

				if ( search == null ) {

					search = SearchDAO.getInstance().getSearch( searchId );
					searches.put( searchId, search );
				}

				reportedPeptideData.setSearchName( search.getName() );


				int reportedPeptideId = rs.getInt( "reported_peptide_id" );
				
				reportedPeptideData.setReportedPeptide ( ReportedPeptideDAO.getInstance().getReportedPeptideFromDatabase( reportedPeptideId ) );

				reportedPeptideData.setPeptideQValue( rs.getDouble( "peptide_q_value_for_search" ) );
				if ( rs.wasNull() ) {
					reportedPeptideData.setPeptideQValue( null );
				}

				reportedPeptideData.setPeptidePEP( rs.getDouble( "pep" ) );
				if ( rs.wasNull() ) {
					reportedPeptideData.setPeptidePEP( null );
				}
				
				
				reportedPeptideData.setPeptideSVMScore( rs.getDouble( "svm_score" ) );
				if ( rs.wasNull() ) {
					reportedPeptideData.setPeptideSVMScore( null );
				}
				
				reportedPeptideData.setPeptidePValue( rs.getDouble( "p_value" ) );
				if ( rs.wasNull() ) {
					reportedPeptideData.setPeptidePValue( null );
				}
				
				if ( psmQValueCutoff == DefaultQValueCutoffConstants.PSM_Q_VALUE_CUTOFF_DEFAULT ) {

					//  associated PSM data
					reportedPeptideData.setNumPSMs( rs.getInt( "psm_num_at_pt_01_q_cutoff" ) );
				
				} else {
					
					int numPsms = 
						PsmCountForSearchIdReportedPeptideIdSearcher.getInstance()
						.getPsmCountForSearchIdReportedPeptideId( reportedPeptideId, searchId, psmQValueCutoff);
					
					reportedPeptideData.setNumPSMs( numPsms );
				}
					
				results.add( reportedPeptideData );

			}
			
		} catch ( Exception e ) {
			
			String msg = "Exception in search( SearchDTO search, double psmCutoff, double peptideCutoff, ReportedPeptideDTO peptide ), sql: " + sql;
			
			log.error( msg, e );
			
			throw e;
			
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
		
		return results;
	}
}
