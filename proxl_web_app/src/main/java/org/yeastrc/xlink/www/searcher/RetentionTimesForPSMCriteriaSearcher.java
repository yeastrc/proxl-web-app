package org.yeastrc.xlink.www.searcher;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yeastrc.xlink.base.constants.Database_OneTrueZeroFalse_Constants;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;
import org.yeastrc.xlink.searcher_constants.SearcherGeneralConstants;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesAnnotationLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.constants.DynamicModificationsSelectionConstants;
import org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants;

/**
 * Return Retention Time List based on parameters.
 * Return one entry for each psm record that meets filter.
 */
public class RetentionTimesForPSMCriteriaSearcher {
	
	private static final Log log = LogFactory.getLog(RetentionTimesForPSMCriteriaSearcher.class);
	private RetentionTimesForPSMCriteriaSearcher() { }
	private static final RetentionTimesForPSMCriteriaSearcher _INSTANCE = new RetentionTimesForPSMCriteriaSearcher();
	public static RetentionTimesForPSMCriteriaSearcher getInstance() { return _INSTANCE; }

	private final String PSM_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS = "psm_fltrbl_tbl_";
	private final String PEPTIDE_VALUE_FILTER_TABLE_ALIAS = "srch__rep_pept_fltrbl_tbl_";
	
	private final String SQL_MAIN_FIRST_PART = 
			"SELECT retention_time \n"
			+ "FROM scan INNER JOIN ( ";
	
	//   Inner subquery between these parts is from the 'INNER_QUERY' variables listed next
	
	private final String SQL_MAIN_AFTER_INNER_QUERY_JOIN = " ) AS scan_ids ON scan.id = scan_ids.scan_id ";

	private final String SQL_MAIN_MAIN_WHERE_SCAN_FILE_ID = 
			" WHERE scan.scan_file_id = ? \n";
	
	private final String SQL_MAIN_MAIN_WHERE_RETENTION_TIME = " AND retention_time < ?  ";
	
	//  SQL for inner query between SQL_MAIN_FIRST_PART and SQL_MAIN_AFTER_INNER_QUERY_JOIN
	
	private final String SQL_INNER_QUERY_FIRST_PART = 
			"SELECT psm.scan_id \n";
	
	private final String SQL_INNER_QUERY_MAIN_FROM_START = " FROM unified_rp__search__rep_pept__generic_lookup \n"
			+ " INNER JOIN psm on unified_rp__search__rep_pept__generic_lookup.search_id = psm.search_id"
			+ 					" AND unified_rp__search__rep_pept__generic_lookup.reported_peptide_id = psm.reported_peptide_id \n";
	
	/**
	 *   If Dynamic Mods are selected, this gets added after the Join to the Dynamic Mods subselect
	 */
	private final String SQL_INNER_QUERY_MAIN_WHERE_START = 
			" WHERE unified_rp__search__rep_pept__generic_lookup.search_id = ? \n";

	//  If Dynamic Mods are selected, one of these three gets added after the main where clause 
	//  No Mods Only
	private static final String SQL_INNER_QUERY_NO_MODS_ONLY__MAIN_WHERE_CLAUSE = 
			" AND  unified_rp__search__rep_pept__generic_lookup.has_dynamic_modifictions  = " 
					+ Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_FALSE + " \n";
	
	//  Yes Mods Only
	private static final String SQL_INNER_QUERY_YES_MODS_ONLY_MAIN_WHERE_CLAUSE = 
			" AND  unified_rp__search__rep_pept__generic_lookup.has_dynamic_modifictions  = " 
					+	 Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE + " \n";
	
	private static final String SQL_INNER_QUERY_NO_AND_YES_MODS__MAIN_WHERE_CLAUSE = 
			" AND ( "
			// 		 No Mods
			+ 		"unified_rp__search__rep_pept__generic_lookup.has_dynamic_modifictions  = " 
			+ 		Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_FALSE 
			+ 		" OR "
			//  	 Yes Mods: 
			//				need srch_id_rep_pep_id_for_mod_masses.search_id IS NOT NULL 
			//				since doing LEFT OUTER JOIN when both Yes and No Mods
			+ 			" ( unified_rp__search__rep_pept__generic_lookup.has_dynamic_modifictions  = " 
			+ 				Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE 
			+ 				" AND"
			+ 				" srch_id_rep_pep_id_for_mod_masses.search_id IS NOT NULL "
			+     		 " ) " 
			+ " ) \n";
	
	///////////////////
	//  Additional SQL parts
	private static final String SQL_INNER_QUERY_LINK_TYPE_START = "  unified_rp__search__rep_pept__generic_lookup.link_type IN ( ";
	private static final String SQL_INNER_QUERY_LINK_TYPE_END = " ) \n";
	
	//  Dynamic Mod processing
	private static final String SQL_INNER_QUERY_DYNAMIC_MOD_JOIN_START = 
			" INNER JOIN (";
	private static final String SQL_INNER_QUERY_DYNAMIC_MOD_AND_NO_MODS_JOIN_START = 
			" LEFT OUTER JOIN (";
	private static final String SQL_INNER_QUERY_DYNAMIC_MOD_INNER_SELECT_START = 
			" SELECT DISTINCT search_id, reported_peptide_id "
			+		" FROM search__reported_peptide__dynamic_mod_lookup "
			+		" WHERE search_id = ? AND dynamic_mod_mass IN ( ";
	private static final String SQL_INNER_QUERY_DYNAMIC_MOD_JOIN_AFTER_MOD_MASSES = // After Dynamic Mod Masses 
			" )  \n";
	private static final String SQL_INNER_QUERY_DYNAMIC_MOD_JOIN_START_LINK_TYPES = // After Dynamic Mod Masses 
						" AND  ( ";
	private static final String SQL_INNER_QUERY_DYNAMIC_MOD_LINK_TYPE_START = "  search__reported_peptide__dynamic_mod_lookup.link_type IN ( ";
	private static final String SQL_INNER_QUERY_DYNAMIC_MOD_LINK_TYPE_END = " ) ";
	private static final String SQL_INNER_QUERY_DYNAMIC_MOD_JOIN_AFTER_LINK_TYPES = // After Link Types
						" )  \n";
	private static final String SQL_INNER_QUERY_DYNAMIC_MOD_JOIN_END = 
			" ) AS srch_id_rep_pep_id_for_mod_masses "
			+ " ON unified_rp__search__rep_pept__generic_lookup.search_id = srch_id_rep_pep_id_for_mod_masses.search_id "
			+ "    AND unified_rp__search__rep_pept__generic_lookup.reported_peptide_id = srch_id_rep_pep_id_for_mod_masses.reported_peptide_id \n";
	

	///////////////
	
	//   Filter on Proteins
	private static final String SQL_PROTEIN_FILTER_JOIN_START = 
			" INNER JOIN (";
	private static final String SQL_PROTEIN_FILTER_INNER_SELECT_START = 
			" SELECT DISTINCT search_id, reported_peptide_id "
			+		" FROM protein_coverage "
			+		" WHERE search_id = ? AND protein_sequence_version_id IN ( ";
	private static final String SQL_PROTEIN_FILTER_JOIN_AFTER_PROTEIN_SEQUENCE_VERSION_IDS = // After protein_sequence_version_id 
			" )  \n";

	private static final String SQL_PROTEIN_FILTER_JOIN_END = 
			" ) AS srch_id_rep_pep_id_for_protein_seq_v_ids "
			+ " ON unified_rp__search__rep_pept__generic_lookup.search_id = srch_id_rep_pep_id_for_protein_seq_v_ids.search_id "
			+ "    AND unified_rp__search__rep_pept__generic_lookup.reported_peptide_id = srch_id_rep_pep_id_for_protein_seq_v_ids.reported_peptide_id \n";
	
	
	
	/**
	 * Return Retention Time List based on parameters.
	 * Return one entry for each psm record that meets filter.
	 * 
	 * @param searchId
	 * @param scanFileId - null if not use
	 * @param searcherCutoffValuesSearchLevel
	 * @param linkTypes
	 * @param modMassSelections
	 * @param retentionTimeInSecondsCutoff - null if not use
	 * @return
	 * @throws Exception 
	 */
	public List<BigDecimal> getRetentionTimes(
			int searchId, 
			Integer scanFileId,
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel,
			String[] linkTypes,
			String[] modMassSelections,
			List<Integer> includeProteinSeqVIdsDecodedArray,
			Double retentionTimeInSecondsCutoff ) throws Exception {

		List<BigDecimal> resultList = new ArrayList<>();

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
		
		///  Do not use optimization on Default Cutoff since need to apply cutoffs to individual PSM records
		
		//  Determine if can use PSM count at Default Cutoff
//		DefaultCutoffsExactlyMatchAnnTypeDataToSearchDataResult defaultCutoffsExactlyMatchAnnTypeDataToSearchDataResult =
//				DefaultCutoffsExactlyMatchAnnTypeDataToSearchData.getInstance()
//				.defaultCutoffsExactlyMatchAnnTypeDataToSearchData( searchId, searcherCutoffValuesSearchLevel );
//		boolean defaultCutoffsExactlyMatchAnnTypeDataToSearchData =
//				defaultCutoffsExactlyMatchAnnTypeDataToSearchDataResult.isDefaultCutoffsExactlyMatchAnnTypeDataToSearchData();
		
		//////////////////////////////////
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		//  Pre-process the dynamic mod masses selections
		boolean modMassSelectionsIncludesNoModifications = false;
		boolean modMassSelectionsIncludesYesModifications = false;
		List<String> modMassSelectionsWithoutNoMods = null; 
		if ( modMassSelections != null ) {
			for ( String modMassSelection : modMassSelections ) {
				if ( DynamicModificationsSelectionConstants.NO_DYNAMIC_MODIFICATIONS_SELECTION_ITEM.equals( modMassSelection ) ) {
					modMassSelectionsIncludesNoModifications = true;
				} else {
					modMassSelectionsIncludesYesModifications = true;
					if ( modMassSelectionsWithoutNoMods == null ) {
						modMassSelectionsWithoutNoMods = new ArrayList<>( modMassSelections.length );
					}
					modMassSelectionsWithoutNoMods.add( modMassSelection );
				}
			}
		}
		//////////////////////
		/////   Start building the SQL
		StringBuilder sqlSB = new StringBuilder( 1000 );
		
		sqlSB.append( SQL_MAIN_FIRST_PART );
		
		sqlSB.append( SQL_INNER_QUERY_FIRST_PART );

		sqlSB.append( SQL_INNER_QUERY_MAIN_FROM_START );
		{
//			if ( ! defaultCutoffsExactlyMatchAnnTypeDataToSearchData ) {
				//  Non-Default PSM or Peptide cutoffs so have to query on the cutoffs
				//  Add inner join for each PSM cutoff
				for ( int counter = 1; counter <= psmCutoffValuesList.size(); counter++ ) {
					sqlSB.append( " INNER JOIN " );
					sqlSB.append( " psm_filterable_annotation__generic_lookup AS " );
					sqlSB.append( PSM_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( " ON "  );
					sqlSB.append( " psm.id = "  );
					sqlSB.append( PSM_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".psm_id \n" );
				}
//			}
		}
		{
//			if ( ! defaultCutoffsExactlyMatchAnnTypeDataToSearchData ) {
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
					sqlSB.append( ".reported_peptide_id \n" );
				}
//			}
		}
		//  If Yes modifications, join to get records for those modifications
		if ( modMassSelectionsIncludesYesModifications && modMassSelectionsWithoutNoMods != null ) {
			if ( modMassSelectionsIncludesNoModifications) {
				sqlSB.append( SQL_INNER_QUERY_DYNAMIC_MOD_AND_NO_MODS_JOIN_START );
			} else {
				sqlSB.append( SQL_INNER_QUERY_DYNAMIC_MOD_JOIN_START );
			}
			//   Start Dynamic Mods subselect
			sqlSB.append( SQL_INNER_QUERY_DYNAMIC_MOD_INNER_SELECT_START );
			
			int modMassSelectionsWithoutNoModsSize = modMassSelectionsWithoutNoMods.size();
			for ( int counter = 0; counter < modMassSelectionsWithoutNoModsSize; counter++ ) {
				if ( counter != 0 ) {
					sqlSB.append( ", " );
				}
				sqlSB.append( "? " );
			}
			
			sqlSB.append( SQL_INNER_QUERY_DYNAMIC_MOD_JOIN_AFTER_MOD_MASSES );
			
			//  Process link types for Dynamic Mod subselect
			if ( linkTypes != null && ( linkTypes.length > 0 ) ) {
				sqlSB.append( SQL_INNER_QUERY_DYNAMIC_MOD_JOIN_START_LINK_TYPES );
				sqlSB.append( SQL_INNER_QUERY_DYNAMIC_MOD_LINK_TYPE_START );   //   ...  IN  (
				boolean firstLinkType = true;
				for ( String linkType : linkTypes ) {
					if ( firstLinkType ) {
						firstLinkType = false;
					} else {
						sqlSB.append( ", " );
					}
					if ( PeptideViewLinkTypesConstants.CROSSLINK_PSM.equals( linkType ) ) {
						sqlSB.append( "'" );
						sqlSB.append( XLinkUtils.CROSS_TYPE_STRING );
						sqlSB.append( "'" );
					} else if ( PeptideViewLinkTypesConstants.LOOPLINK_PSM.equals( linkType ) ) {
						sqlSB.append( "'" );
						sqlSB.append( XLinkUtils.LOOP_TYPE_STRING );
						sqlSB.append( "'" );
					} else if ( PeptideViewLinkTypesConstants.UNLINKED_PSM.equals( linkType ) ) {
						sqlSB.append( "'" );
						sqlSB.append( XLinkUtils.UNLINKED_TYPE_STRING );
						sqlSB.append( "'" );
						sqlSB.append( ", " );
						sqlSB.append( "'" );
						sqlSB.append( XLinkUtils.DIMER_TYPE_STRING );
						sqlSB.append( "'" );
					} else {
						String msg = "linkType is invalid, linkType: " + linkType;
						log.error( linkType );
						throw new Exception( msg );
					}
				}
				sqlSB.append( SQL_INNER_QUERY_DYNAMIC_MOD_LINK_TYPE_END );
				sqlSB.append( SQL_INNER_QUERY_DYNAMIC_MOD_JOIN_AFTER_LINK_TYPES );
			}
			sqlSB.append( SQL_INNER_QUERY_DYNAMIC_MOD_JOIN_END );
			sqlSB.append( "\n" );
		}

		//  Selected Protein Sequence Version Ids to filter on 
		
		if ( includeProteinSeqVIdsDecodedArray != null && ( ! includeProteinSeqVIdsDecodedArray.isEmpty() ) ) {

			sqlSB.append( SQL_PROTEIN_FILTER_JOIN_START );
			sqlSB.append( SQL_PROTEIN_FILTER_INNER_SELECT_START );
			
			int includeProteinSeqVIdsDecodedArraySize = includeProteinSeqVIdsDecodedArray.size();
			for ( int counter = 0; counter < includeProteinSeqVIdsDecodedArraySize; counter++ ) {
				if ( counter != 0 ) {
					sqlSB.append( ", " );
				}
				sqlSB.append( "? " );
			}
			
			sqlSB.append( SQL_PROTEIN_FILTER_JOIN_AFTER_PROTEIN_SEQUENCE_VERSION_IDS );
			
			sqlSB.append( SQL_PROTEIN_FILTER_JOIN_END );
		}
		
		//////////
		sqlSB.append( SQL_INNER_QUERY_MAIN_WHERE_START );
		//////////
		//  Process link types
		if ( linkTypes != null && ( linkTypes.length > 0 ) ) {
			sqlSB.append( " AND ( " );
			sqlSB.append( SQL_INNER_QUERY_LINK_TYPE_START );  //  ...  IN (
			boolean firstLinkType = true;
			for ( String linkType : linkTypes ) {
				if ( firstLinkType ) {
					firstLinkType = false;
				} else {
					sqlSB.append( ", " );
				}
				if ( PeptideViewLinkTypesConstants.CROSSLINK_PSM.equals( linkType ) ) {
					sqlSB.append( "'" );
					sqlSB.append( XLinkUtils.CROSS_TYPE_STRING );
					sqlSB.append( "'" );
				} else if ( PeptideViewLinkTypesConstants.LOOPLINK_PSM.equals( linkType ) ) {
					sqlSB.append( "'" );
					sqlSB.append( XLinkUtils.LOOP_TYPE_STRING );
					sqlSB.append( "'" );
				} else if ( PeptideViewLinkTypesConstants.UNLINKED_PSM.equals( linkType ) ) {
					sqlSB.append( "'" );
					sqlSB.append( XLinkUtils.UNLINKED_TYPE_STRING );
					sqlSB.append( "'" );
					sqlSB.append( ", " );
					sqlSB.append( "'" );
					sqlSB.append( XLinkUtils.DIMER_TYPE_STRING );
					sqlSB.append( "'" );
				} else {
					String msg = "linkType is invalid, linkType: " + linkType;
					log.error( linkType );
					throw new Exception( msg );
				}
			}
			sqlSB.append( SQL_INNER_QUERY_LINK_TYPE_END );  //   )
			sqlSB.append( " ) \n" );
		}		
		//  add modifications condition on unified_rep_pep__reported_peptide__search_lookup to main where clause
		if ( modMassSelectionsIncludesYesModifications && modMassSelectionsIncludesNoModifications ) {
			sqlSB.append( SQL_INNER_QUERY_NO_AND_YES_MODS__MAIN_WHERE_CLAUSE );
		} else if ( modMassSelectionsIncludesNoModifications) {
			sqlSB.append( SQL_INNER_QUERY_NO_MODS_ONLY__MAIN_WHERE_CLAUSE );
		} else if ( modMassSelectionsIncludesYesModifications ) {
			sqlSB.append( SQL_INNER_QUERY_YES_MODS_ONLY_MAIN_WHERE_CLAUSE );
		}
		// Process PSM Cutoffs for WHERE
		{
//			if ( ! defaultCutoffsExactlyMatchAnnTypeDataToSearchData ) {
				//  Non-Default PSM or Peptide cutoffs so have to query on the cutoffs
				int counter = 0; 
				for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesPsmAnnotationLevel : psmCutoffValuesList ) {
					AnnotationTypeDTO srchPgmFilterablePsmAnnotationTypeDTO = searcherCutoffValuesPsmAnnotationLevel.getAnnotationTypeDTO();
					counter++;
					sqlSB.append( " AND " );
					sqlSB.append( " ( " );
					sqlSB.append( PSM_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".search_id = ? AND " );
					sqlSB.append( PSM_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".annotation_type_id = ? AND " );
					sqlSB.append( PSM_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".value_double " );
					if ( srchPgmFilterablePsmAnnotationTypeDTO.getAnnotationTypeFilterableDTO().getFilterDirectionType() 
							== FilterDirectionType.ABOVE ) {
						sqlSB.append( SearcherGeneralConstants.SQL_END_BIGGER_VALUE_BETTER );
					} else {
						sqlSB.append( SearcherGeneralConstants.SQL_END_SMALLER_VALUE_BETTER );
					}
					sqlSB.append( " ? " );
					sqlSB.append( " ) \n" );
				}
//			} else {
//				//   Only Default PSM Cutoffs chosen so criteria simply the Peptides where the PSM count for the default cutoffs is > zero
//				sqlSB.append( " AND " );
//				sqlSB.append( " unified_rp__search__rep_pept__generic_lookup.psm_num_at_default_cutoff > 0 \n" );
//			}
		}
		//  Process Peptide Cutoffs for WHERE
		{
//			if ( ! defaultCutoffsExactlyMatchAnnTypeDataToSearchData ) {
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
					sqlSB.append( " ) \n" );
				}
//			} else {
//				//   Only Default Peptide Cutoffs chosen so criteria simply the Peptides where the defaultPeptideCutoffs is yes
//				//  WARNING:  This code is currently not run for set value of USE_PEPTIDE_PSM_DEFAULTS_TO_SKIP_JOIN_ANNOTATION_DATA_VALUES_TABLES
//				//  WARNING   This is possibly still WRONG and needs testing before using.
//				//  For certain inputs, the right value to search for is: Yes_No__NOT_APPLICABLE_Enum.NOT_APPLICABLE
//				sqlSB.append( " AND " );
//				sqlSB.append( " unified_rp__search__rep_pept__generic_lookup.peptide_meets_default_cutoffs = '" );
//				if ( peptideCutoffValuesList.isEmpty() ) {
//					sqlSB.append( Yes_No__NOT_APPLICABLE_Enum.NOT_APPLICABLE.value() );
//				} else {
//					sqlSB.append( Yes_No__NOT_APPLICABLE_Enum.YES.value() );
//				}
//				sqlSB.append( "' \n" );
//			}
		}		
		
		sqlSB.append( SQL_MAIN_AFTER_INNER_QUERY_JOIN );
		
		if ( scanFileId != null ) {
			// scanFileId passed in so use it in query
			sqlSB.append( SQL_MAIN_MAIN_WHERE_SCAN_FILE_ID );
		}
		if ( retentionTimeInSecondsCutoff != null ) {
			sqlSB.append( SQL_MAIN_MAIN_WHERE_RETENTION_TIME );
		}

		final String sql = sqlSB.toString();
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			int paramCounter = 0;

			if ( modMassSelectionsIncludesYesModifications && modMassSelectionsWithoutNoMods != null ) {
				//  If Yes modifications, have search id in subselect
				paramCounter++;
				pstmt.setInt( paramCounter, searchId );

				for ( String entry : modMassSelectionsWithoutNoMods ) {
					paramCounter++; 
					pstmt.setString( paramCounter, entry );
				}
			}
			
			if ( includeProteinSeqVIdsDecodedArray != null && ( ! includeProteinSeqVIdsDecodedArray.isEmpty() ) ) {
				
				//  Protein Sequence Version Ids selected to filter on
				paramCounter++;
				pstmt.setInt( paramCounter, searchId );

				// search id in subselect
				for ( Integer entry : includeProteinSeqVIdsDecodedArray ) {
					paramCounter++; 
					pstmt.setInt( paramCounter, entry );
				}
			}
			
			//   For:   unified_rp__search__rep_pept__generic_lookup.search_id = ? 
			paramCounter++;
			pstmt.setInt( paramCounter, searchId );
			// Process PSM Cutoffs for WHERE
			{
//				if ( ! defaultCutoffsExactlyMatchAnnTypeDataToSearchData ) {
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
//				}
			}
			// Process Peptide Cutoffs for WHERE
			{
//				if ( ! defaultCutoffsExactlyMatchAnnTypeDataToSearchData ) {
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
//				}
			}
			if ( scanFileId != null ) {
				//  scanFileId passed in so use it in query
				paramCounter++;
				pstmt.setInt( paramCounter, scanFileId );
			}
			if ( retentionTimeInSecondsCutoff != null ) {
				//  Retention Time passed in so use it in query
				paramCounter++;
				pstmt.setDouble( paramCounter, retentionTimeInSecondsCutoff );
			}


//			if ( log.isDebugEnabled() ) {
//				log.debug( "Executed Statement: " + ((DelegatingPreparedStatement)pstmt).getDelegate().toString() );
//			}
//			if ( log.isDebugEnabled() ) {
//			//  Commented out since is specific DBCP dependency
//				log.debug( "Executed Statement: " 
//						+ ((org.apache.commons.dbcp2.DelegatingPreparedStatement)pstmt).getDelegate().toString() );
//			}
//			log.warn( "Executed Statement: " + ((DelegatingPreparedStatement)pstmt).getDelegate().toString() );

			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				resultList.add( rs.getBigDecimal( "retention_time" ) );
			}
			
		} catch ( Exception e ) {
			String msg = "Exception in search( ... ), \n sql: " + sql
				//  Commented out since is specific DBCP dependency
//					+ "\n Executed Statement: " 
//					+ ((org.apache.commons.dbcp2.DelegatingPreparedStatement)pstmt).getDelegate().toString()
					;
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
