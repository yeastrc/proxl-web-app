package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.dbcp.DelegatingPreparedStatement;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;
import org.yeastrc.xlink.enum_classes.Yes_No__NOT_APPLICABLE_Enum;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.protein_coverage.PeptideProteinPositionsForCutoffsAndProtSeqIdsResultItem;
import org.yeastrc.xlink.www.searcher_utils.DefaultCutoffsExactlyMatchAnnTypeDataToSearchData;
import org.yeastrc.xlink.www.searcher_utils.DefaultCutoffsExactlyMatchAnnTypeDataToSearchData.DefaultCutoffsExactlyMatchAnnTypeDataToSearchDataResult;
import org.yeastrc.xlink.searcher_constants.SearcherGeneralConstants;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesAnnotationLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;

/**
 * 
 *
 */
public class PeptideProteinPositionsForCutoffsAndProtSeqIdsSearcher {
	
	private static final Logger log = Logger.getLogger(PeptideProteinPositionsForCutoffsAndProtSeqIdsSearcher.class);

	private PeptideProteinPositionsForCutoffsAndProtSeqIdsSearcher() { }
	private static final PeptideProteinPositionsForCutoffsAndProtSeqIdsSearcher _INSTANCE = new PeptideProteinPositionsForCutoffsAndProtSeqIdsSearcher();
	public static PeptideProteinPositionsForCutoffsAndProtSeqIdsSearcher getInstance() { return _INSTANCE; }
	
	
	private final String PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS = "psm_fltrbl_tbl_";
	

	private final String PEPTIDE_VALUE_FILTER_TABLE_ALIAS = "srch__rep_pept_fltrbl_tbl_";
	

	private final String SQL_FIRST_PART = 
			

			"SELECT peptide_protein_position.reported_peptide_id, "
			
			+ " peptide_protein_position.peptide_id, "
			+ " peptide_protein_position.protein_sequence_id, "
			+ " peptide_protein_position.protein_start_position, "
			+ " peptide_protein_position.protein_end_position ";
	
			
	private final String SQL_MAIN_FROM_START = 			
			
			" FROM "
			
			+ " unified_rp__search__rep_pept__generic_lookup "
			
			+ " INNER JOIN peptide_protein_position "
			+ 		" ON unified_rp__search__rep_pept__generic_lookup.search_id"
			+ 				" =  peptide_protein_position.search_id  "
			+ 			" AND unified_rp__search__rep_pept__generic_lookup.reported_peptide_id"
			+ 				" =  peptide_protein_position.reported_peptide_id  "
			;

	private final String SQL_MAIN_WHERE_START = 
			
			" WHERE peptide_protein_position.search_id = ? ";

	
	
	/**
	 * Get data from peptide_protein_position
	 * @param proteinSequenceIds The Protein Sequence Ids
	 * @param search The search we're searching
	 * @param searcherCutoffValuesSearchLevel - PSM and Peptide cutoffs for a search id
	 * @return
	 * @throws Exception
	 */
	public List<PeptideProteinPositionsForCutoffsAndProtSeqIdsResultItem> searchOnSearchIdPsmCutoffPeptideCutoff( 
			Set<Integer> proteinSequenceIds,
			SearchDTO search, 
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel ) throws Exception {

		List<PeptideProteinPositionsForCutoffsAndProtSeqIdsResultItem> resultList = new ArrayList<>();
		

		int searchId = search.getSearchId();

		

		List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesList = 
				searcherCutoffValuesSearchLevel.getPeptidePerAnnotationCutoffsList();
		
		List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList = 
				searcherCutoffValuesSearchLevel.getPsmPerAnnotationCutoffsList();
		
		//  If null, create empty lists
		
		if ( peptideCutoffValuesList == null ) {
			
			peptideCutoffValuesList = new ArrayList<>();
		}
		
		if ( psmCutoffValuesList == null ) {
			
			psmCutoffValuesList = new ArrayList<>();
		}


		List<AnnotationTypeDTO> peptideCutoffsAnnotationTypeDTOList = new ArrayList<>( psmCutoffValuesList.size() );

		List<AnnotationTypeDTO> psmCutoffsAnnotationTypeDTOList = new ArrayList<>( psmCutoffValuesList.size() );

		
		for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesAnnotationLevel : peptideCutoffValuesList ) {

			peptideCutoffsAnnotationTypeDTOList.add( searcherCutoffValuesAnnotationLevel.getAnnotationTypeDTO() );
		}

				
		for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesAnnotationLevel : psmCutoffValuesList ) {

			psmCutoffsAnnotationTypeDTOList.add( searcherCutoffValuesAnnotationLevel.getAnnotationTypeDTO() );
		}

		//  Determine if can use PSM count at Default Cutoff
		DefaultCutoffsExactlyMatchAnnTypeDataToSearchDataResult defaultCutoffsExactlyMatchAnnTypeDataToSearchDataResult =
				DefaultCutoffsExactlyMatchAnnTypeDataToSearchData.getInstance()
				.defaultCutoffsExactlyMatchAnnTypeDataToSearchData( searchId, searcherCutoffValuesSearchLevel );
		
		boolean defaultCutoffsExactlyMatchAnnTypeDataToSearchData =
				defaultCutoffsExactlyMatchAnnTypeDataToSearchDataResult.isDefaultCutoffsExactlyMatchAnnTypeDataToSearchData();
			
		
		//////////////////////////////////
		
		
		
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		
		
		//////////////////////
		
		/////   Start building the SQL
		
		

		
		StringBuilder sqlSB = new StringBuilder( 1000 );
		
		


		sqlSB.append( SQL_FIRST_PART );
		
		

		sqlSB.append( SQL_MAIN_FROM_START );
		
		
		
		{
			
			if ( ( ( ! defaultCutoffsExactlyMatchAnnTypeDataToSearchData ) ) ) {


				//  Non-Default PSM or Peptide cutoffs so have to query on the cutoffs



				//  Add inner join for each PSM cutoff

				for ( int counter = 1; counter <= psmCutoffValuesList.size(); counter++ ) {

					sqlSB.append( " INNER JOIN " );

					sqlSB.append( " unified_rp__search__rep_pept__best_psm_value_generic_lookup AS " );
					
					sqlSB.append( PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );

					sqlSB.append( " ON "  );

					sqlSB.append( " unified_rp__search__rep_pept__generic_lookup.search_id = "  );

					sqlSB.append( PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".search_id" );

					sqlSB.append( " AND " );


					sqlSB.append( " unified_rp__search__rep_pept__generic_lookup.reported_peptide_id = "  );

					sqlSB.append( PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".reported_peptide_id" );
				}
			}
		}
		
		{
			
			if ( ( ( ! defaultCutoffsExactlyMatchAnnTypeDataToSearchData ) ) ) {


				//  Non-Default PSM cutoffs so have to query on the cutoffs

				//  Add inner join for each Peptide cutoff

				for ( int counter = 1; counter <= peptideCutoffValuesList.size(); counter++ ) {

					sqlSB.append( " INNER JOIN " );

					sqlSB.append( " srch__rep_pept__annotation AS " );
					sqlSB.append( PEPTIDE_VALUE_FILTER_TABLE_ALIAS );

					sqlSB.append( Integer.toString( counter ) );

					sqlSB.append( " ON "  );

					sqlSB.append( " unified_rp__search__rep_pept__generic_lookup.search_id = "  );

					sqlSB.append( PEPTIDE_VALUE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".search_id" );

					sqlSB.append( " AND " );


					sqlSB.append( " unified_rp__search__rep_pept__generic_lookup.reported_peptide_id = "  );

					sqlSB.append( PEPTIDE_VALUE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".reported_peptide_id" );

				}
			}
		}


		//////////
		
		sqlSB.append( SQL_MAIN_WHERE_START );
		
		//////////
		
		///   Add protein sequence ids to WHERE
		
		
		sqlSB.append( " AND peptide_protein_position.protein_sequence_id IN ( " );
		
		boolean firstProtSeqId = true;
		
		for ( Integer proteinSequenceId : proteinSequenceIds ) {
			
			if ( firstProtSeqId ) {
				firstProtSeqId = false;
			} else {
				sqlSB.append( "," );
			}
			
			sqlSB.append( proteinSequenceId );
		}
		
		sqlSB.append( " ) " );
		
		
		
		// Process PSM Cutoffs for WHERE

		{
			
			if ( ( ( ! defaultCutoffsExactlyMatchAnnTypeDataToSearchData ) ) ) {


				//  Non-Default PSM or Peptide cutoffs so have to query on the cutoffs


				int counter = 0; 

				for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesPsmAnnotationLevel : psmCutoffValuesList ) {


					AnnotationTypeDTO srchPgmFilterablePsmAnnotationTypeDTO = searcherCutoffValuesPsmAnnotationLevel.getAnnotationTypeDTO();

					counter++;

					sqlSB.append( " AND " );

					sqlSB.append( " ( " );


					sqlSB.append( PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".search_id = ? AND " );

					sqlSB.append( PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".annotation_type_id = ? AND " );

					sqlSB.append( PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".best_psm_value_for_ann_type_id " );

					if ( srchPgmFilterablePsmAnnotationTypeDTO.getAnnotationTypeFilterableDTO().getFilterDirectionType() 
							== FilterDirectionType.ABOVE ) {

						sqlSB.append( SearcherGeneralConstants.SQL_END_BIGGER_VALUE_BETTER );

					} else {

						sqlSB.append( SearcherGeneralConstants.SQL_END_SMALLER_VALUE_BETTER );

					}

					sqlSB.append( " ? " );

					sqlSB.append( " ) " );
				}
				
			} else {


				//   Only Default PSM Cutoffs chosen so criteria simply the Peptides where the PSM count for the default cutoffs is > zero
				

				sqlSB.append( " AND " );


				sqlSB.append( " unified_rp__search__rep_pept__generic_lookup.psm_num_at_default_cutoff > 0 " );

				
			}
		}
		
		//  Process Peptide Cutoffs for WHERE

		{

			if ( ( ( ! defaultCutoffsExactlyMatchAnnTypeDataToSearchData ) ) ) {

				//  Non-Default PSM or Peptide cutoffs so have to query on the cutoffs

				int counter = 0; 

				for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesReportedPeptideAnnotationLevel : peptideCutoffValuesList ) {

					AnnotationTypeDTO srchPgmFilterableReportedPeptideAnnotationTypeDTO = searcherCutoffValuesReportedPeptideAnnotationLevel.getAnnotationTypeDTO();

					counter++;

					sqlSB.append( " AND " );

					sqlSB.append( " ( " );


					sqlSB.append( PEPTIDE_VALUE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".search_id = ? AND " );

					sqlSB.append( PEPTIDE_VALUE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".annotation_type_id = ? AND " );

					sqlSB.append( PEPTIDE_VALUE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".value_double " );

					if ( srchPgmFilterableReportedPeptideAnnotationTypeDTO.getAnnotationTypeFilterableDTO().getFilterDirectionType() 
							== FilterDirectionType.ABOVE ) {

						sqlSB.append( SearcherGeneralConstants.SQL_END_BIGGER_VALUE_BETTER );

					} else {

						sqlSB.append( SearcherGeneralConstants.SQL_END_SMALLER_VALUE_BETTER );

					}

					sqlSB.append( "? " );

					sqlSB.append( " ) " );
				}
				
			} else {
				

				//   Only Default Peptide Cutoffs chosen so criteria simply the Peptides where the defaultPeptideCutoffs is yes

				sqlSB.append( " AND " );


				sqlSB.append( " unified_rp__search__rep_pept__generic_lookup.peptide_meets_default_cutoffs = '" );
				sqlSB.append( Yes_No__NOT_APPLICABLE_Enum.YES.value() );
				sqlSB.append( "' " );

			}
		}		
		
		
		
		
		final String sql = sqlSB.toString();
		
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			

			int paramCounter = 0;
			
			

			//   For:   unified_rp__search__rep_pept__generic_lookup.search_id = ? 

			paramCounter++;
			pstmt.setInt( paramCounter, searchId );

			
			// Process PSM Cutoffs for WHERE


			{
				if ( ( ( ! defaultCutoffsExactlyMatchAnnTypeDataToSearchData ) ) ) {

					
					//  Non-Default PSM or Peptide cutoffs so have to query on the cutoffs

					

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

				if ( ( ( ! defaultCutoffsExactlyMatchAnnTypeDataToSearchData ) ) ) {

					
					//  Non-Default PSM or Peptide cutoffs so have to query on the cutoffs

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
			}
			
			
			if ( log.isDebugEnabled() ) {

				log.debug( "Executed Statement: " + ((DelegatingPreparedStatement)pstmt).getDelegate().toString() );
			}
			
			rs = pstmt.executeQuery();
			
			Map<Integer, Boolean> foundAtLeastOnePSMForSearchReportedPeptideIdMap = new HashMap<>();

			while( rs.next() ) {
				
				int reportedPeptideId = rs.getInt( "reported_peptide_id" );
				
				if ( psmCutoffsAnnotationTypeDTOList.size() > 1 
						&& ( ! defaultCutoffsExactlyMatchAnnTypeDataToSearchData ) ) {

					Boolean foundAtLeastOnePSMForSearchReportedPeptideId = 
							foundAtLeastOnePSMForSearchReportedPeptideIdMap.get( reportedPeptideId );

					if ( foundAtLeastOnePSMForSearchReportedPeptideId != null ) {

						if ( ! foundAtLeastOnePSMForSearchReportedPeptideId.booleanValue() ) {

							//  No PSMs found that match all PSM criteria for this reported peptide id so skip it.

							foundAtLeastOnePSMForSearchReportedPeptideIdMap.put( reportedPeptideId, false );

							continue;  //  EARLY Continue
						}
						
					} else {
						if ( ! PeptideAtLeastOnePSMSearcher.getInstance().peptideAtLeastOnePSMSearcher( reportedPeptideId, searchId, searcherCutoffValuesSearchLevel ) ) {

							//  No PSMs found that match all PSM criteria for this reported peptide id so skip it.

							foundAtLeastOnePSMForSearchReportedPeptideIdMap.put( reportedPeptideId, false );
							
							continue;  //  EARLY Continue
						} else {
							
							foundAtLeastOnePSMForSearchReportedPeptideIdMap.put( reportedPeptideId, true );
						}
					}
				}
						

				PeptideProteinPositionsForCutoffsAndProtSeqIdsResultItem item = new PeptideProteinPositionsForCutoffsAndProtSeqIdsResultItem();

				item.setSearchId( searchId );
				
				item.setReportedPeptideId( reportedPeptideId );
				item.setPeptideId( rs.getInt( "peptide_id" ) );
				item.setProteinSequenceId( rs.getInt( "protein_sequence_id" ) );
				item.setProteinStartPosition( rs.getInt( "protein_start_position" ) );
				item.setProteinEndPosition( rs.getInt( "protein_end_position" ) );

				resultList.add( item );
			}
			
		} catch ( Exception e ) {
			
			String msg = "Exception in search( SearchDTO search, ... ), sql: " + sql;
			
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
		
		return resultList;
	}


	
		
}
