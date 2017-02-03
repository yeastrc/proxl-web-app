package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.AnnotationDataBaseDTO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.PsmAnnotationDTO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.dto.SearchReportedPeptideAnnotationDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;
import org.yeastrc.xlink.enum_classes.Yes_No__NOT_APPLICABLE_Enum;
import org.yeastrc.xlink.searcher_constants.SearcherGeneralConstants;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesAnnotationLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.objects.SearchPeptideCrosslink;
import org.yeastrc.xlink.www.objects.SearchPeptideCrosslinkAnnDataWrapper;
import org.yeastrc.xlink.www.searcher_utils.DefaultCutoffsExactlyMatchAnnTypeDataToSearchData;
import org.yeastrc.xlink.www.searcher_utils.DefaultCutoffsExactlyMatchAnnTypeDataToSearchData.DefaultCutoffsExactlyMatchAnnTypeDataToSearchDataResult;



/**
 * Version of SearchPeptideCrosslinkSearcher for Linked Position tables
 *
 */
public class SearchPeptideCrosslink_LinkedPosition_Searcher {
	
	private static final Logger log = Logger.getLogger(SearchPeptideCrosslink_LinkedPosition_Searcher.class);

	private SearchPeptideCrosslink_LinkedPosition_Searcher() { }
	private static final SearchPeptideCrosslink_LinkedPosition_Searcher _INSTANCE = new SearchPeptideCrosslink_LinkedPosition_Searcher();
	public static SearchPeptideCrosslink_LinkedPosition_Searcher getInstance() { return _INSTANCE; }
	


	
	/**
	 * Should it use the optimization of Peptide and PSM defaults to skip joining the tables with the annotation values?
	 */
	private final boolean USE_PEPTIDE_PSM_DEFAULTS_TO_SKIP_JOIN_ANNOTATION_DATA_VALUES_TABLES = false;  // UNTESTED for a value of "true"
	
	
	/**
	 * UNTESTED for a value of "true"
	 * 
	 * If make true, need to change calling code since best PSM annotation values will not be populated
	 * 
	 * Also, test web page and/or webservice 
	 */
//	private final boolean USE_PEPTIDE_PSM_DEFAULTS_TO_SKIP_JOIN_ANNOTATION_DATA_VALUES_TABLES = true;  //  UNTESTED for a value of "true"
	
	
	

	private final String PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS = "psm_best_value_tbl_";

	private final String PEPTIDE_FILTER_TABLE_ALIAS = "pept_value_tbl_";
	
	
	


	//  The data returned from THIS SEARCHER IS CORRECT 
	//  since Duplicate reported_peptide_id values 
	//  are checked for in the Java code and dropped.
	//  (See use of retrieved_reported_peptide_id_values_Set)
	//  The following WARNING only applies if using this SQL elsewhere.
	
	//  WARNING:  This SQL returns duplicate records 
	//				when Prot_Seq_Id_1 = Prot_Seq_Id_2  AND Prot_Pos_1 = Prot_Pos_2
	

	private final String SQL_FIRST_PART = 
			
			"SELECT unified_rp__search__rep_pept__generic_lookup.reported_peptide_id, "
			
			+ " unified_rp__search__rep_pept__generic_lookup.link_type, "
			+ " unified_rp__search__rep_pept__generic_lookup.psm_num_at_default_cutoff ";

	private final String SQL_MAIN_FROM_START = 
			
			" FROM "
			
			+ " srch_rep_pept__prot_seq_id_pos_crosslink AS srpnipc_1 "
			+ " INNER JOIN "
			+ " srch_rep_pept__prot_seq_id_pos_crosslink AS srpnipc_2 "
			+ " ON srpnipc_1.reported_peptide_id = srpnipc_2.reported_peptide_id"
			+ 	" AND  srpnipc_1.search_reported_peptide_peptide_id "
			+ 		" != srpnipc_2.search_reported_peptide_peptide_id "

			+ " INNER JOIN unified_rp__search__rep_pept__generic_lookup "
			+ 	" ON srpnipc_1.search_id = unified_rp__search__rep_pept__generic_lookup.search_id "
			+ 		" AND srpnipc_1.reported_peptide_id "
			+ 			" = unified_rp__search__rep_pept__generic_lookup.reported_peptide_id ";


	private final String SQL_MAIN_WHERE_START = 
					
			" WHERE unified_rp__search__rep_pept__generic_lookup.search_id = ? "
			+ " AND unified_rp__search__rep_pept__generic_lookup.link_type = '" + XLinkUtils.CROSS_TYPE_STRING + "' "
			+ " AND srpnipc_1.search_id = ? AND srpnipc_1.protein_sequence_id = ? AND srpnipc_1.protein_sequence_position = ? "
			+ " AND srpnipc_2.search_id = ? AND srpnipc_2.protein_sequence_id = ? AND srpnipc_2.protein_sequence_position = ?  ";

	
	private final String SQL_LAST_PART = 

			"";
			
	// Removed since not needed.  
	// A WARN log message will be written if duplicate reported_peptide_id are found in the result set

//			" GROUP BY unified_rp__search__rep_pept__generic_lookup.reported_peptide_id ";

			// Sort in Java	
	
	
	
	
	
	/**
	 * Get all crosslink peptides corresponding to the given Criteria
	 * 
	 * @param searchId
	 * @param searcherCutoffValuesSearchLevel
	 * @param protein1Id
	 * @param protein2Id
	 * @param protein1Position
	 * @param protein2Position
	 * @return
	 * @throws Exception
	 */
	public List<SearchPeptideCrosslinkAnnDataWrapper> searchOnSearchProteinCrosslink( 
			SearchDTO searchDTO,
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel,
			int protein1Id,
			int protein2Id,
			int protein1Position,
			int protein2Position
			
			) throws Exception {
	
		
		List<SearchPeptideCrosslinkAnnDataWrapper> wrappedLinks = new ArrayList<>();

		int searchId = searchDTO.getSearchId();
		


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
		
		

		//////////////////////
		
		/////   Start building the SQL
		
		

		
		StringBuilder sqlSB = new StringBuilder( 1000 );
		
		


		sqlSB.append( SQL_FIRST_PART );
		

		
		///////   Add fields to result from best PSM annotation values
		
		{
			

			
			if ( ( ( ! defaultCutoffsExactlyMatchAnnTypeDataToSearchData ) )
					|| ( ! USE_PEPTIDE_PSM_DEFAULTS_TO_SKIP_JOIN_ANNOTATION_DATA_VALUES_TABLES ) ) {

				
				//  Non-Default PSM or Peptide cutoffs so have to query on the cutoffs



				//  Add Field retrieval for each PSM cutoff

				for ( int counter = 1; counter <= psmCutoffValuesList.size(); counter++ ) {

					sqlSB.append( " , " );

					sqlSB.append( PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".annotation_type_id " );
					sqlSB.append( " AS "  );
					sqlSB.append( PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( "_annotation_type_id " );

					sqlSB.append( " , " );
					
					sqlSB.append( PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".best_psm_value_for_ann_type_id " );
					sqlSB.append( " AS "  );
					sqlSB.append( PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( "_best_psm_value_for_ann_type_id " );
				}
			}
			

			//  Add Field retrieval for each Peptide cutoff

			for ( int counter = 1; counter <= peptideCutoffValuesList.size(); counter++ ) {
				
				sqlSB.append( " , " );

				sqlSB.append( PEPTIDE_FILTER_TABLE_ALIAS );
				sqlSB.append( Integer.toString( counter ) );
				sqlSB.append( ".annotation_type_id " );
				sqlSB.append( " AS "  );
				sqlSB.append( PEPTIDE_FILTER_TABLE_ALIAS );
				sqlSB.append( Integer.toString( counter ) );
				sqlSB.append( "_annotation_type_id " );

				sqlSB.append( " , " );
				
				sqlSB.append( PEPTIDE_FILTER_TABLE_ALIAS );
				sqlSB.append( Integer.toString( counter ) );
				sqlSB.append( ".peptide_value_for_ann_type_id " );
				sqlSB.append( " AS "  );
				sqlSB.append( PEPTIDE_FILTER_TABLE_ALIAS );
				sqlSB.append( Integer.toString( counter ) );
				sqlSB.append( "_peptide_value_for_ann_type_id " );

			}
		}
		
		
		
		
		
		
		sqlSB.append( SQL_MAIN_FROM_START );
		
		
		
		{
			

			
			if ( ( ( ! defaultCutoffsExactlyMatchAnnTypeDataToSearchData ) )
					|| ( ! USE_PEPTIDE_PSM_DEFAULTS_TO_SKIP_JOIN_ANNOTATION_DATA_VALUES_TABLES ) ) {

				
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
			

			
			if ( ( ( ! defaultCutoffsExactlyMatchAnnTypeDataToSearchData ) )
					|| ( ! USE_PEPTIDE_PSM_DEFAULTS_TO_SKIP_JOIN_ANNOTATION_DATA_VALUES_TABLES ) ) {

				
				//  Non-Default PSM or Peptide cutoffs so have to query on the cutoffs


				//  Add inner join for each Peptide cutoff

				for ( int counter = 1; counter <= peptideCutoffValuesList.size(); counter++ ) {

					sqlSB.append( " INNER JOIN " );
			
					sqlSB.append( " unified_rp__search_reported_peptide_fltbl_value_generic_lookup AS " );
					sqlSB.append( PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );

					sqlSB.append( " ON "  );

					sqlSB.append( " unified_rp__search__rep_pept__generic_lookup.search_id = "  );

					sqlSB.append( PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".search_id" );

					sqlSB.append( " AND " );



					sqlSB.append( " unified_rp__search__rep_pept__generic_lookup.reported_peptide_id = "  );

					sqlSB.append( PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".reported_peptide_id" );

				}
			}
		}
		

		
		
		
		//////////
		
		sqlSB.append( SQL_MAIN_WHERE_START );
		
		//////////
		
		

		// Process PSM Cutoffs for WHERE

		{
			

			
			if ( ( ( ! defaultCutoffsExactlyMatchAnnTypeDataToSearchData ) )
					|| ( ! USE_PEPTIDE_PSM_DEFAULTS_TO_SKIP_JOIN_ANNOTATION_DATA_VALUES_TABLES ) ) {

				
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

			if ( ( ( ! defaultCutoffsExactlyMatchAnnTypeDataToSearchData ) )
					|| ( ! USE_PEPTIDE_PSM_DEFAULTS_TO_SKIP_JOIN_ANNOTATION_DATA_VALUES_TABLES ) ) {

				//  Non-Default PSM or Peptide cutoffs so have to query on the cutoffs

				int counter = 0; 

				for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesReportedPeptideAnnotationLevel : peptideCutoffValuesList ) {

					AnnotationTypeDTO srchPgmFilterableReportedPeptideAnnotationTypeDTO = searcherCutoffValuesReportedPeptideAnnotationLevel.getAnnotationTypeDTO();

					counter++;

					sqlSB.append( " AND " );

					sqlSB.append( " ( " );


					sqlSB.append( PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".search_id = ? AND " );

					sqlSB.append( PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".annotation_type_id = ? AND " );

					sqlSB.append( PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".peptide_value_for_ann_type_id " );

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
		
		
		
		sqlSB.append( SQL_LAST_PART );
		
		
		String sql = sqlSB.toString();
		
		
		
		//////////////////////////////////
		
		
		
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			
	

			pstmt = conn.prepareStatement( sql );
			


			int paramCounter = 0;
			
			
			paramCounter++;
			pstmt.setInt( paramCounter, searchId );
			
			paramCounter++;
			pstmt.setInt( paramCounter, searchId );

			paramCounter++;
			pstmt.setInt( paramCounter, protein1Id );
			paramCounter++;
			pstmt.setInt( paramCounter, protein1Position );

			paramCounter++;
			pstmt.setInt( paramCounter, searchId );
			
			paramCounter++;
			pstmt.setInt( paramCounter, protein2Id );
			paramCounter++;
			pstmt.setInt( paramCounter, protein2Position );


			// Process PSM Cutoffs for WHERE


			{
				if ( ( ( ! defaultCutoffsExactlyMatchAnnTypeDataToSearchData ) )
						|| ( ! USE_PEPTIDE_PSM_DEFAULTS_TO_SKIP_JOIN_ANNOTATION_DATA_VALUES_TABLES ) ) {

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
				if ( ( ( ! defaultCutoffsExactlyMatchAnnTypeDataToSearchData ) )
						|| ( ! USE_PEPTIDE_PSM_DEFAULTS_TO_SKIP_JOIN_ANNOTATION_DATA_VALUES_TABLES ) ) {

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
			
			
			rs = pstmt.executeQuery();
			
			Set<Integer> retrieved_reported_peptide_id_values_Set = new HashSet<>();
			

			while( rs.next() ) {
				
				
				int reportedPeptideId = rs.getInt( "reported_peptide_id" );

				
				if ( ! retrieved_reported_peptide_id_values_Set.add( reportedPeptideId ) ) {
					
//					String msg = "Already processed result entry for reportedPeptideId: " + reportedPeptideId;
//
//					log.warn( msg );
					
					continue;  //   EARY CONTINUE
					
//					log.error( msg );
//					throw new ProxlWebappDataException(msg);
				}

				
				
				
				SearchPeptideCrosslinkAnnDataWrapper wrappedLink = new SearchPeptideCrosslinkAnnDataWrapper();
				
				SearchPeptideCrosslink link = new SearchPeptideCrosslink();
				
				wrappedLink.setSearchPeptideCrosslink( link );
				

				link.setSearch( searchDTO );
				
				link.setSearcherCutoffValuesSearchLevel( searcherCutoffValuesSearchLevel );
				
				link.setReportedPeptideId( reportedPeptideId );
				

				
				
				if ( defaultCutoffsExactlyMatchAnnTypeDataToSearchData ) {
										
					int numPsmsForDefaultCutoffs = rs.getInt( "psm_num_at_default_cutoff" );
					if ( ! rs.wasNull() ) {
					
						link.setNumPsms( numPsmsForDefaultCutoffs );
					}
				}
				

				if ( peptideCutoffsAnnotationTypeDTOList.size() > 1 
						|| psmCutoffsAnnotationTypeDTOList.size() > 1 ) {
					
					if ( link.getNumPsms() <= 0 ) {

						//  !!!!!!!   Number of PSMs is zero this this isn't really a peptide that meets the cutoffs
						
						continue;  //  EARY LOOP ENTRY EXIT
					}
				}
				

				if ( ( ( ! defaultCutoffsExactlyMatchAnnTypeDataToSearchData ) )
						|| ( ! USE_PEPTIDE_PSM_DEFAULTS_TO_SKIP_JOIN_ANNOTATION_DATA_VALUES_TABLES ) ) {

					//  Get PSM best values from DB query, since psm best value table was joined

					Map<Integer, AnnotationDataBaseDTO> bestPsmAnnotationDTOFromQueryMap =
							getPSMBestValuesFromDBQuery( rs, psmCutoffsAnnotationTypeDTOList );

					wrappedLink.setPsmAnnotationDTOMap( bestPsmAnnotationDTOFromQueryMap );
					

					//  Get Peptide values from DB query, since Peptide value table was joined

					Map<Integer, AnnotationDataBaseDTO> peptideAnnotationDTOFromQueryMap =
							getPeptideValuesFromDBQuery( rs, peptideCutoffsAnnotationTypeDTOList );

					wrappedLink.setPeptideAnnotationDTOMap(  peptideAnnotationDTOFromQueryMap );
				}
				
				wrappedLinks.add( wrappedLink );
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
		
		return wrappedLinks;
	}
	



	//  Get PSM best values from DB query, since psm best value table was joined

	/**
	 * @param rs
	 * @param psmCutoffsAnnotationTypeDTOList
	 * @return
	 * @throws SQLException
	 */
	private Map<Integer, AnnotationDataBaseDTO> getPSMBestValuesFromDBQuery( 


			ResultSet rs,

			List<AnnotationTypeDTO> psmCutoffsAnnotationTypeDTOList

			) throws SQLException { 

		Map<Integer, AnnotationDataBaseDTO> bestPsmAnnotationDTOFromQueryMap = new HashMap<>();

		//  Add inner join for each PSM cutoff

		for ( int counter = 1; counter <= psmCutoffsAnnotationTypeDTOList.size(); counter++ ) {

			PsmAnnotationDTO item = new PsmAnnotationDTO();

			String annotationTypeIdField = PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS + counter + "_annotation_type_id";

			String valueDoubleField = PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS + counter + "_best_psm_value_for_ann_type_id";

			item.setAnnotationTypeId( rs.getInt( annotationTypeIdField ) );

			item.setValueDouble( rs.getDouble( valueDoubleField ) );
			item.setValueString( Double.toString( item.getValueDouble() ) );

			bestPsmAnnotationDTOFromQueryMap.put( item.getAnnotationTypeId(),  item );

		}
		
		return bestPsmAnnotationDTOFromQueryMap;
	}

	

	//  Get Peptide values from DB query, since Peptide value table was joined

	/**
	 * @param rs
	 * @param psmCutoffsAnnotationTypeDTOList
	 * @return
	 * @throws SQLException
	 */
	private Map<Integer, AnnotationDataBaseDTO> getPeptideValuesFromDBQuery( 


			ResultSet rs,

			List<AnnotationTypeDTO> psmCutoffsAnnotationTypeDTOList

			) throws SQLException { 

		Map<Integer, AnnotationDataBaseDTO> peptideAnnotationDTOFromQueryMap = new HashMap<>();

		//  Add inner join for each PSM cutoff

		for ( int counter = 1; counter <= psmCutoffsAnnotationTypeDTOList.size(); counter++ ) {

			SearchReportedPeptideAnnotationDTO item = new SearchReportedPeptideAnnotationDTO();

			String annotationTypeIdField = PEPTIDE_FILTER_TABLE_ALIAS + counter + "_annotation_type_id";

			String valueDoubleField = PEPTIDE_FILTER_TABLE_ALIAS + counter + "_peptide_value_for_ann_type_id";

			item.setAnnotationTypeId( rs.getInt( annotationTypeIdField ) );

			item.setValueDouble( rs.getDouble( valueDoubleField ) );
			item.setValueString( Double.toString( item.getValueDouble() ) );

			peptideAnnotationDTOFromQueryMap.put( item.getAnnotationTypeId(),  item );

		}
		
		return peptideAnnotationDTOFromQueryMap;
	}

	
	
}
