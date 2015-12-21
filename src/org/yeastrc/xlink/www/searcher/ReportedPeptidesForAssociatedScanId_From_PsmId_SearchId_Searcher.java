package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.ReportedPeptideDAO;
import org.yeastrc.xlink.dao.SearchDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.constants.DefaultQValueCutoffConstants;
import org.yeastrc.xlink.www.objects.SearchPeptideCrosslink;
import org.yeastrc.xlink.www.objects.SearchPeptideDimer;
import org.yeastrc.xlink.www.objects.SearchPeptideLooplink;
import org.yeastrc.xlink.www.objects.SearchPeptideUnlink;
import org.yeastrc.xlink.www.objects.WebReportedPeptide;
import org.yeastrc.xlink.www.objects.WebReportedPeptideWebserviceWrapper;

/**
 * Is only this PSM associated with it's scan ids 
 * for the current search and meet the current Peptide and PSM cutoffs 
 *
 */
public class ReportedPeptidesForAssociatedScanId_From_PsmId_SearchId_Searcher {

	private ReportedPeptidesForAssociatedScanId_From_PsmId_SearchId_Searcher() { }
	public static ReportedPeptidesForAssociatedScanId_From_PsmId_SearchId_Searcher getInstance() { return new ReportedPeptidesForAssociatedScanId_From_PsmId_SearchId_Searcher(); }

	private static final Logger log = Logger.getLogger(ReportedPeptidesForAssociatedScanId_From_PsmId_SearchId_Searcher.class);
	

	
	private static final String SQL = 
		

			"SELECT unified_rep_pep__reported_peptide__search_lookup.reported_peptide_id, "
			
			+ " unified_rep_pep__reported_peptide__search_lookup.peptide_q_value_for_search, "
			
			+ " unified_rep_pep__reported_peptide__search_lookup.best_psm_q_value,"
			+ " unified_rep_pep__reported_peptide__search_lookup.link_type, "
			+ " unified_rep_pep__reported_peptide__search_lookup.psm_num_at_pt_01_q_cutoff "
			
			+ " FROM "
			
	
			+ 	"psm  "
			+ 	"INNER JOIN psm AS psm_other ON psm.scan_id = psm_other.scan_id "
			+ 	"INNER JOIN unified_rep_pep__reported_peptide__search_lookup  "
			+ 		"ON psm_other.search_id = unified_rep_pep__reported_peptide__search_lookup.search_id " 
			+ 			"AND psm_other.reported_peptide_id = unified_rep_pep__reported_peptide__search_lookup.reported_peptide_id "
		
			+ 	"WHERE psm.id = ? AND psm.search_id = ? AND psm.q_value <= ? "
			+ 	"AND psm_other.search_id = ? AND psm_other.q_value <= ? "
			+ 	"AND unified_rep_pep__reported_peptide__search_lookup.peptide_q_value_for_search <= ? ";
		

			
			
	
	/**
	 * @param psmId
	 * @param searchId
	 * @param peptideQValueCutoff
	 * @param psmQValueCutoff
	 * @return
	 * @throws Exception
	 */
	public List<WebReportedPeptideWebserviceWrapper> reportedPeptideRecordsForAssociatedScanId( int psmId, int searchId, double peptideQValueCutoff, double psmQValueCutoff ) throws Exception {
		
		
		List<WebReportedPeptideWebserviceWrapper> results = new ArrayList<>();
		
		SearchDTO search = null;
		
		try {
			
			search = SearchDAO.getInstance().getSearch( searchId );
			
		} catch ( Exception e ) {
			
			String msg = "Failed to get searchDTO";
			
			log.error( msg, e );
			
			throw e;
			
		}
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = SQL;
		
		
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			pstmt = conn.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, psmId );

			counter++;
			pstmt.setInt( counter, searchId );

			counter++;
			pstmt.setDouble( counter, psmQValueCutoff );
			
			counter++;
			pstmt.setInt( counter, searchId );
			
			counter++;
			pstmt.setDouble( counter, psmQValueCutoff );
			

			counter++;
			pstmt.setDouble( counter, peptideQValueCutoff );
			
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				

				WebReportedPeptide item = new WebReportedPeptide();
				
				item.setPeptideQValueCutoff( peptideQValueCutoff );
				item.setPsmQValueCutoff( psmQValueCutoff );
				

				String linkType = rs.getString( "link_type" );
				
				int reportedPeptideId = rs.getInt( "reported_peptide_id" );
				
				
				item.setSearchId( searchId );
				item.setReportedPeptideId( reportedPeptideId );
				
				ReportedPeptideDTO reportedPeptideDTO = 
						ReportedPeptideDAO.getInstance().getReportedPeptideFromDatabase( reportedPeptideId );

				
				item.setqValue( rs.getDouble( "peptide_q_value_for_search" ) );
				if ( rs.wasNull() ) {
					item.setqValue( null );
				}

				item.setBestPsmQValue( rs.getDouble( "best_psm_q_value" ) );

				int numPsmsForpt01Cutoff = rs.getInt( "psm_num_at_pt_01_q_cutoff" );
				
				if ( DefaultQValueCutoffConstants.PSM_Q_VALUE_CUTOFF_DEFAULT == psmQValueCutoff ) {
					
					item.setNumPsms( numPsmsForpt01Cutoff ); // code is needed in WebMergedReportedPeptide for when psmCutoff is not default
				}
				

				if ( XLinkUtils.CROSS_TYPE_STRING.equals(linkType) ) {
					
					SearchPeptideCrosslink link = new SearchPeptideCrosslink();

					link.setSearch( search );
					link.setReportedPeptide( reportedPeptideDTO );

					item.setSearchPeptideCrosslink(link);
					
				} else if ( XLinkUtils.LOOP_TYPE_STRING.equals(linkType) ) {
					
					
					SearchPeptideLooplink link = new SearchPeptideLooplink();
					
					link.setSearch( search );
					link.setReportedPeptide ( reportedPeptideDTO );

					item.setSearchPeptideLooplink(link);
					

				} else if ( XLinkUtils.UNLINKED_TYPE_STRING.equals(linkType) ) {
					
					
					SearchPeptideUnlink link = new SearchPeptideUnlink();
					
					link.setSearch( search );
					link.setReportedPeptide ( reportedPeptideDTO );

					item.setSearchPeptideUnlinked(link);
					
				} else if ( XLinkUtils.DIMER_TYPE_STRING.equals(linkType) ) {
					
					
					SearchPeptideDimer link = new SearchPeptideDimer();
					
					link.setSearch( search );
					link.setReportedPeptide ( reportedPeptideDTO );

					item.setSearchPeptideDimer(link);
										
					
				} else {
					
					
					String msg = "Unknown link type in search( SearchDTO search, double psmCutoff, double peptideCutoff, linkTypes ), linkType: " + linkType + ", sql: " + sql;
					
					log.error( msg );
					
					
					continue;  //  EARLY CONTINUE:    skip over other types for now
				}
				
				WebReportedPeptideWebserviceWrapper webReportedPeptideWebserviceWrapper = new WebReportedPeptideWebserviceWrapper();
				
				webReportedPeptideWebserviceWrapper.setWebReportedPeptide( item );
				
				webReportedPeptideWebserviceWrapper.setLinkType( linkType );
				
				results.add( webReportedPeptideWebserviceWrapper );
			}
			

		} catch ( Exception e ) {

			log.error( "ERROR:  SQL: " + sql, e );

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
