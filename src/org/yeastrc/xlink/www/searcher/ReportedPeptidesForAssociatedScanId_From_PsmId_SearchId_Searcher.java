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
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;
import org.yeastrc.xlink.searcher_constants.SearcherGeneralConstants;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesAnnotationLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.utils.XLinkUtils;
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
	

	
	private static final String SQL_FIRST_PART = 
		

//			"SELECT unified_rep_pep__reported_peptide__search_lookup.reported_peptide_id, "
//			
//			+ " unified_rep_pep__reported_peptide__search_lookup.peptide_q_value_for_search, "
//			
//			+ " unified_rep_pep__reported_peptide__search_lookup.best_psm_q_value,"
//			+ " unified_rep_pep__reported_peptide__search_lookup.link_type, "
//			+ " unified_rep_pep__reported_peptide__search_lookup.psm_num_at_pt_01_q_cutoff "
			


			"SELECT unified_rp__rep_pept__search__generic_lookup.reported_peptide_id, "
			
			+ " unified_rp__rep_pept__search__generic_lookup.link_type, "
			+ " unified_rp__rep_pept__search__generic_lookup.psm_num_at_default_cutoff "
			
			+ " FROM "
			
			+ 	"psm  "
			+ 	"INNER JOIN unified_rp__rep_pept__search__generic_lookup  "
			+ 		"ON psm.search_id = unified_rp__rep_pept__search__generic_lookup.search_id " 
			+ 			"AND psm.reported_peptide_id = unified_rp__rep_pept__search__generic_lookup.reported_peptide_id ";
		
	private static final String SQL_MAIN_WHERE_START =
	
			" WHERE psm.scan_id = ? AND psm.search_id = ? ";
		

	private static final String SQL_LAST_PART = " ORDER BY psm.id";
			
			
	
	/**
	 * @param psmId
	 * @param scanId
	 * @param searchId
	 * @param searcherCutoffValuesSearchLevel
	 * @return
	 * @throws Exception
	 */
	public List<WebReportedPeptideWebserviceWrapper> reportedPeptideRecordsForAssociatedScanId( int psmId, int scanId, int searchId, SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel ) throws Exception {
		
		
		List<WebReportedPeptideWebserviceWrapper> results = new ArrayList<>();
		
		SearchDTO search = null;
		
		try {
			
			search = SearchDAO.getInstance().getSearch( searchId );
			
		} catch ( Exception e ) {
			
			String msg = "Failed to get searchDTO";
			
			log.error( msg, e );
			
			throw e;
			
		}
		
		

		List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesList = 
				searcherCutoffValuesSearchLevel.getPeptidePerAnnotationCutoffsList();
		
		List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList = 
				searcherCutoffValuesSearchLevel.getPsmPerAnnotationCutoffsList();


		////////////
		
		//  All cutoffs are default?
		
//		boolean onlyDefaultPeptideCutoffs = true;
		
		boolean onlyDefaultPsmCutoffs = true;
		
		
		
		//   Check if all Peptide Cutoffs are default values
		
//		for ( SearcherCutoffValuesAnnotationLevel item : peptideCutoffValuesList ) {
//			
//			if ( ! item.annotationValueMatchesDefault() ) {
//				
//				onlyDefaultPeptideCutoffs = false;
//				break;
//			}
//		}

		//   Check if all Psm Cutoffs are default values
		
		for ( SearcherCutoffValuesAnnotationLevel item : psmCutoffValuesList ) {
			
			if ( ! item.annotationValueMatchesDefault() ) {
				
				onlyDefaultPsmCutoffs = false;
				break;
			}
		}
		
		
		//////////////////////////////////
		
		
		
		
		
		
		
		
		
		
		
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		
		

		//////////////////////
		
		/////   Start building the SQL
		
		

		
		StringBuilder sqlSB = new StringBuilder( 1000 );
		
		
		
		

		sqlSB.append( SQL_FIRST_PART );
		

		{
			
			if ( ! onlyDefaultPsmCutoffs ) {
				
				
				//  Non-Default PSM cutoffs so have to query on the cutoffs


				//  Add inner join for each PSM cutoff

				for ( int counter = 1; counter <= psmCutoffValuesList.size(); counter++ ) {

					sqlSB.append( " INNER JOIN " );

					sqlSB.append( " unified_rp__rep_pept__search__best_psm_value_generic_lookup AS psm_fltrbl_tbl_" );
					sqlSB.append( Integer.toString( counter ) );

					sqlSB.append( " ON "  );

					sqlSB.append( " unified_rp__rep_pept__search__generic_lookup.search_id = "  );

					sqlSB.append( "psm_fltrbl_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".search_id" );

					sqlSB.append( " AND " );


					sqlSB.append( " unified_rp__rep_pept__search__generic_lookup.reported_peptide_id = "  );

					sqlSB.append( "psm_fltrbl_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".reported_peptide_id" );
				}
			}
		}
		
		
		

		{
			
			//  Add inner join for each Peptide cutoff
			
			for ( int counter = 1; counter <= peptideCutoffValuesList.size(); counter++ ) {
				
				sqlSB.append( " INNER JOIN " );
				
				sqlSB.append( " srch__rep_pept__annotation AS srch__rep_pept_fltrbl_tbl_" );
				sqlSB.append( Integer.toString( counter ) );

				sqlSB.append( " ON "  );

				sqlSB.append( " unified_rp__rep_pept__search__generic_lookup.search_id = "  );

				sqlSB.append( "srch__rep_pept_fltrbl_tbl_" );
				sqlSB.append( Integer.toString( counter ) );
				sqlSB.append( ".search_id" );

				sqlSB.append( " AND " );


				sqlSB.append( " unified_rp__rep_pept__search__generic_lookup.reported_peptide_id = "  );

				sqlSB.append( "srch__rep_pept_fltrbl_tbl_" );
				sqlSB.append( Integer.toString( counter ) );
				sqlSB.append( ".reported_peptide_id" );

			}
		}

		

		
		//////////
		
		sqlSB.append( SQL_MAIN_WHERE_START );
		
		//////////
		
		

		// Process PSM Cutoffs for WHERE

		{

			
			if ( onlyDefaultPsmCutoffs ) {
				
				//   Only Default PSM Cutoffs chosen so criteria simply the Peptides where the PSM count for the default cutoffs is > zero
				

				sqlSB.append( " AND " );


				sqlSB.append( " unified_rp__rep_pept__search__generic_lookup.psm_num_at_default_cutoff > 0 " );

				
			} else {

				
				//  Non-Default PSM cutoffs so have to query on the cutoffs

				int counter = 0; 

				for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesPsmAnnotationLevel : psmCutoffValuesList ) {


					AnnotationTypeDTO srchPgmFilterablePsmAnnotationTypeDTO = searcherCutoffValuesPsmAnnotationLevel.getAnnotationTypeDTO();

					counter++;

					sqlSB.append( " AND " );

					sqlSB.append( " ( " );


					sqlSB.append( "psm_fltrbl_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".search_id = ? AND " );

					sqlSB.append( "psm_fltrbl_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".annotation_type_id = ? AND " );

					sqlSB.append( "psm_fltrbl_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".best_psm_value_for_ann_type_id " );

					if ( srchPgmFilterablePsmAnnotationTypeDTO.getAnnotationTypeFilterableDTO() == null ) {
						
						String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + srchPgmFilterablePsmAnnotationTypeDTO.getId();
						log.error( msg );
						throw new Exception(msg);
					}
						
					if ( srchPgmFilterablePsmAnnotationTypeDTO.getAnnotationTypeFilterableDTO().getFilterDirectionType() == FilterDirectionType.ABOVE ) {

						sqlSB.append( SearcherGeneralConstants.SQL_END_BIGGER_VALUE_BETTER );

					} else {

						sqlSB.append( SearcherGeneralConstants.SQL_END_SMALLER_VALUE_BETTER );

					}

					sqlSB.append( " ? " );

					sqlSB.append( " ) " );
				}
			}
		}
		
		//  Process Peptide Cutoffs for WHERE

		{
			int counter = 0; 
			
			for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesReportedPeptideAnnotationLevel : peptideCutoffValuesList ) {

				AnnotationTypeDTO srchPgmFilterableReportedPeptideAnnotationTypeDTO = searcherCutoffValuesReportedPeptideAnnotationLevel.getAnnotationTypeDTO();
				
				counter++;

				sqlSB.append( " AND " );
				
				sqlSB.append( " ( " );


				sqlSB.append( "srch__rep_pept_fltrbl_tbl_" );
				sqlSB.append( Integer.toString( counter ) );
				sqlSB.append( ".search_id = ? AND " );

				sqlSB.append( "srch__rep_pept_fltrbl_tbl_" );
				sqlSB.append( Integer.toString( counter ) );
				sqlSB.append( ".annotation_type_id = ? AND " );

				sqlSB.append( "srch__rep_pept_fltrbl_tbl_" );
				sqlSB.append( Integer.toString( counter ) );
				sqlSB.append( ".value_double " );

				if ( srchPgmFilterableReportedPeptideAnnotationTypeDTO.getAnnotationTypeFilterableDTO() == null ) {
					
					String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + srchPgmFilterableReportedPeptideAnnotationTypeDTO.getId();
					log.error( msg );
					throw new Exception(msg);
				}
					
				if ( srchPgmFilterableReportedPeptideAnnotationTypeDTO.getAnnotationTypeFilterableDTO().getFilterDirectionType() == FilterDirectionType.ABOVE ) {

					sqlSB.append( SearcherGeneralConstants.SQL_END_BIGGER_VALUE_BETTER );

				} else {

					sqlSB.append( SearcherGeneralConstants.SQL_END_SMALLER_VALUE_BETTER );

				}

				sqlSB.append( "? " );

				sqlSB.append( " ) " );
			}
		}
		
		
		
		sqlSB.append( SQL_LAST_PART );
		
		
		

		
		String sql = sqlSB.toString();
		
		
		
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			pstmt = conn.prepareStatement( sql );
			
			int paramCounter = 0;
			
			paramCounter++;
			pstmt.setInt( paramCounter, scanId );
			
			paramCounter++;
			pstmt.setInt( paramCounter, searchId );
			



			// Process PSM Cutoffs for WHERE


			{
				
				if ( ! onlyDefaultPsmCutoffs ) {
					
					//  PSM Cutoffs are not the default 
					

					for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesPsmAnnotationLevel : psmCutoffValuesList ) {

						AnnotationTypeDTO srchPgmFilterablePsmAnnotationTypeDTO = searcherCutoffValuesPsmAnnotationLevel.getAnnotationTypeDTO();

						paramCounter++;
						pstmt.setInt( paramCounter, searchId );

						paramCounter++;
						pstmt.setInt( paramCounter, srchPgmFilterablePsmAnnotationTypeDTO.getId() );

						paramCounter++;
						pstmt.setDouble( paramCounter, searcherCutoffValuesPsmAnnotationLevel.getAnnotationCutoffValue() );
					}
				}
			}
			



			// Process Peptide Cutoffs for WHERE


			{

				for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesReportedPeptideAnnotationLevel : peptideCutoffValuesList ) {

					AnnotationTypeDTO srchPgmFilterableReportedPeptideAnnotationTypeDTO = searcherCutoffValuesReportedPeptideAnnotationLevel.getAnnotationTypeDTO();
					
					paramCounter++;
					pstmt.setInt( paramCounter, searchId );

					paramCounter++;
					pstmt.setInt( paramCounter, srchPgmFilterableReportedPeptideAnnotationTypeDTO.getId() );

					paramCounter++;
					pstmt.setDouble( paramCounter, searcherCutoffValuesReportedPeptideAnnotationLevel.getAnnotationCutoffValue() );
				}
			}
			
		
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				

				WebReportedPeptide item = new WebReportedPeptide();
				
				item.setSearcherCutoffValuesSearchLevel( searcherCutoffValuesSearchLevel );
				

				String linkType = rs.getString( "link_type" );
				
				int reportedPeptideId = rs.getInt( "reported_peptide_id" );
				
				
				item.setSearchId( searchId );
				item.setReportedPeptideId( reportedPeptideId );
				
				item.setSearch( search );

				
				ReportedPeptideDTO reportedPeptideDTO = 
						ReportedPeptideDAO.getInstance().getReportedPeptideFromDatabase( reportedPeptideId );

				
//				item.setqValue( rs.getDouble( "peptide_q_value_for_search" ) );
//				if ( rs.wasNull() ) {
//					item.setqValue( null );
//				}
//
//				item.setBestPsmQValue( rs.getDouble( "best_psm_q_value" ) );
				
				

				if ( onlyDefaultPsmCutoffs ) {
					
					int numPsmsForDefaultCutoffs = rs.getInt( "psm_num_at_default_cutoff" );
					if ( ! rs.wasNull() ) {
					
						item.setNumPsms( numPsmsForDefaultCutoffs );
					}
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
