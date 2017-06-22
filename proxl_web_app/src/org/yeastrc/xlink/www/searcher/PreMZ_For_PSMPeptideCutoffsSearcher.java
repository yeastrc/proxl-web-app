package org.yeastrc.xlink.www.searcher;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbcp.DelegatingPreparedStatement;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.base.constants.Database_OneTrueZeroFalse_Constants;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;
import org.yeastrc.xlink.enum_classes.Yes_No__NOT_APPLICABLE_Enum;
import org.yeastrc.xlink.searcher_constants.SearcherGeneralConstants;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesAnnotationLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.constants.DynamicModificationsSelectionConstants;
import org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants;
import org.yeastrc.xlink.www.searcher_utils.DefaultCutoffsExactlyMatchAnnTypeDataToSearchData;
import org.yeastrc.xlink.www.searcher_utils.DefaultCutoffsExactlyMatchAnnTypeDataToSearchData.DefaultCutoffsExactlyMatchAnnTypeDataToSearchDataResult;

/**
 * Get PreMZ from Scans where associated PSMs meet on criteria, including Reported Peptide and PSM cutoffs 
 *
 */
public class PreMZ_For_PSMPeptideCutoffsSearcher {

	private static final Logger log = Logger.getLogger(PreMZ_For_PSMPeptideCutoffsSearcher.class);
	private PreMZ_For_PSMPeptideCutoffsSearcher() { }
	private static final PreMZ_For_PSMPeptideCutoffsSearcher _INSTANCE = new PreMZ_For_PSMPeptideCutoffsSearcher();
	public static PreMZ_For_PSMPeptideCutoffsSearcher getInstance() { return _INSTANCE; }
	
	private final String PSM_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS = "psm_fltrbl_tbl_";
	private final String PEPTIDE_VALUE_FILTER_TABLE_ALIAS = "srch__rep_pept_fltrbl_tbl_";
	
	private final String SQL_FIRST_PART = 
			"SELECT unified_rp__search__rep_pept__generic_lookup.link_type,"
			+ " scan.preMZ \n";
	
	private final String SQL_MAIN_FROM_START = " FROM unified_rp__search__rep_pept__generic_lookup \n"
			+ " INNER JOIN psm ON unified_rp__search__rep_pept__generic_lookup.search_id = psm.search_id"
			+ 					" AND unified_rp__search__rep_pept__generic_lookup.reported_peptide_id = psm.reported_peptide_id \n"
			+ " INNER JOIN scan ON psm.scan_id = scan.id ";
		
	
	private final String SQL_LAST_PART = "  ";
	
	// Removed since not needed.  
	// A WARN log message will be written if duplicate reported_peptide_id are found in the result set
//			" GROUP BY unified_rp__search__rep_pept__generic_lookup.reported_peptide_id ";
	
	/**
	 *   If Dynamic Mods are selected, this gets added after the Join to the Dynamic Mods subselect
	 */
	private final String SQL_MAIN_WHERE_START = 
			" WHERE unified_rp__search__rep_pept__generic_lookup.search_id = ? \n";

	//  If Dynamic Mods are selected, one of these three gets added after the main where clause 
	//  No Mods Only
	private static final String SQL_NO_MODS_ONLY__MAIN_WHERE_CLAUSE = 
			" AND  unified_rp__search__rep_pept__generic_lookup.has_dynamic_modifictions  = " 
					+ Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_FALSE + " \n";
	
	//  Yes Mods Only
	private static final String SQL_YES_MODS_ONLY_MAIN_WHERE_CLAUSE = 
			" AND  unified_rp__search__rep_pept__generic_lookup.has_dynamic_modifictions  = " 
					+	 Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE + " \n";
	
	private static final String SQL_NO_AND_YES_MODS__MAIN_WHERE_CLAUSE = 
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
	private static final String SQL_LINK_TYPE_START = "  unified_rp__search__rep_pept__generic_lookup.link_type IN ( ";
	private static final String SQL_LINK_TYPE_END = " ) \n";
	
	//  Dynamic Mod processing
	private static final String SQL_DYNAMIC_MOD_JOIN_START = 
			" INNER JOIN (";
	private static final String SQL_DYNAMIC_MOD_AND_NO_MODS_JOIN_START = 
			" LEFT OUTER JOIN (";
	private static final String SQL_DYNAMIC_MOD_INNER_SELECT_START = 
			" SELECT DISTINCT search_id, reported_peptide_id "
			+		" FROM search__reported_peptide__dynamic_mod_lookup "
			+		" WHERE search_id = ? AND dynamic_mod_mass IN ( ";
	private static final String SQL_DYNAMIC_MOD_JOIN_AFTER_MOD_MASSES = // After Dynamic Mod Masses 
			" )  \n";
	private static final String SQL_DYNAMIC_MOD_JOIN_START_LINK_TYPES = // After Dynamic Mod Masses 
						" AND  ( ";
	private static final String SQL_DYNAMIC_MOD_LINK_TYPE_START = "  search__reported_peptide__dynamic_mod_lookup.link_type IN ( ";
	private static final String SQL_DYNAMIC_MOD_LINK_TYPE_END = " ) ";
	private static final String SQL_DYNAMIC_MOD_JOIN_AFTER_LINK_TYPES = // After Link Types
						" )  \n";
	private static final String SQL_DYNAMIC_MOD_JOIN_END = 
			" ) AS srch_id_rep_pep_id_for_mod_masses "
			+ " ON unified_rp__search__rep_pept__generic_lookup.search_id = srch_id_rep_pep_id_for_mod_masses.search_id "
			+ "    AND unified_rp__search__rep_pept__generic_lookup.reported_peptide_id = srch_id_rep_pep_id_for_mod_masses.reported_peptide_id \n";
	
	
	
	/**
	 * 
	 *
	 */
	public static class PreMZ_For_PSMPeptideCutoffsResult {
		/**
		 * Map <{Link Type},List<{preMZ}>>
		 */
		private Map<String,List<BigDecimal>> resultsPreMZList_Map_KeyedOnLinkType;

		/**
		 * @return Map <{Link Type},List<{preMZ}>>
		 */
		public Map<String, List<BigDecimal>> getResultsPreMZList_Map_KeyedOnLinkType() {
			return resultsPreMZList_Map_KeyedOnLinkType;
		}

	}
	
	/**
	 * @param searchId
	 * @param searcherCutoffValuesSearchLevel
	 * @param linkTypes
	 * @param modMassSelections
	 * @return
	 * @throws Exception 
	 */
	public PreMZ_For_PSMPeptideCutoffsResult getPreMZ_For_PSMPeptideCutoffs(
			int searchId, 
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel,
			String[] linkTypes,
			String[] modMassSelections  ) throws Exception {

		PreMZ_For_PSMPeptideCutoffsResult result = new PreMZ_For_PSMPeptideCutoffsResult();

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
		sqlSB.append( SQL_FIRST_PART );

		sqlSB.append( SQL_MAIN_FROM_START );
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
				sqlSB.append( SQL_DYNAMIC_MOD_AND_NO_MODS_JOIN_START );
			} else {
				sqlSB.append( SQL_DYNAMIC_MOD_JOIN_START );
			}
			//   Start Dynamic Mods subselect
			sqlSB.append( SQL_DYNAMIC_MOD_INNER_SELECT_START );
			sqlSB.append( modMassSelectionsWithoutNoMods.get( 0 ) );
			// start at the second entry
			for ( int index = 1; index < modMassSelectionsWithoutNoMods.size(); index++ ) {
				sqlSB.append( ", " );
				sqlSB.append( modMassSelectionsWithoutNoMods.get( index ) );
			}
			sqlSB.append( SQL_DYNAMIC_MOD_JOIN_AFTER_MOD_MASSES );
			//  Process link types for Dynamic Mod subselect
			if ( linkTypes != null && ( linkTypes.length > 0 ) ) {
				sqlSB.append( SQL_DYNAMIC_MOD_JOIN_START_LINK_TYPES );
				sqlSB.append( SQL_DYNAMIC_MOD_LINK_TYPE_START );   //   ...  IN  (
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
				sqlSB.append( SQL_DYNAMIC_MOD_LINK_TYPE_END );
				sqlSB.append( SQL_DYNAMIC_MOD_JOIN_AFTER_LINK_TYPES );
			}
			sqlSB.append( SQL_DYNAMIC_MOD_JOIN_END );
			sqlSB.append( "\n" );
		}
		//////////
		sqlSB.append( SQL_MAIN_WHERE_START );
		//////////
		//  Process link types
		if ( linkTypes != null && ( linkTypes.length > 0 ) ) {
			sqlSB.append( " AND ( " );
			sqlSB.append( SQL_LINK_TYPE_START );  //  ...  IN (
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
			sqlSB.append( SQL_LINK_TYPE_END );  //   )
			sqlSB.append( " ) \n" );
		}		
		//  add modifications condition on unified_rep_pep__reported_peptide__search_lookup to main where clause
		if ( modMassSelectionsIncludesYesModifications && modMassSelectionsIncludesNoModifications ) {
			sqlSB.append( SQL_NO_AND_YES_MODS__MAIN_WHERE_CLAUSE );
		} else if ( modMassSelectionsIncludesNoModifications) {
			sqlSB.append( SQL_NO_MODS_ONLY__MAIN_WHERE_CLAUSE );
		} else if ( modMassSelectionsIncludesYesModifications ) {
			sqlSB.append( SQL_YES_MODS_ONLY_MAIN_WHERE_CLAUSE );
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
		sqlSB.append( SQL_LAST_PART );
		final String sql = sqlSB.toString();
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			int paramCounter = 0;
			if ( modMassSelectionsIncludesYesModifications && modMassSelectionsWithoutNoMods != null ) {
				//  If Yes modifications, have search id in subselect
				paramCounter++;
				pstmt.setInt( paramCounter, searchId );
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
//			if ( log.isDebugEnabled() ) {
//				log.debug( "Executed Statement: " + ((DelegatingPreparedStatement)pstmt).getDelegate().toString() );
//			}
			if ( log.isDebugEnabled() ) {
				log.debug( "Executed Statement: " + ((DelegatingPreparedStatement)pstmt).getDelegate().toString() );
			}
//			log.warn( "Executed Statement: " + ((DelegatingPreparedStatement)pstmt).getDelegate().toString() );

			rs = pstmt.executeQuery();
			
			/**
			 * Map <{Link Type},List<{preMZ}>>
			 */
			Map<String,List<BigDecimal>> resultsPreMZList_Map_KeyedOnLinkType = new HashMap<>();
			
			result.resultsPreMZList_Map_KeyedOnLinkType = resultsPreMZList_Map_KeyedOnLinkType;

			while( rs.next() ) {
				String linkType = rs.getString( "link_type" );
				BigDecimal preMZ = rs.getBigDecimal( "preMZ" );
				
				List<BigDecimal> resultsPreMZList = resultsPreMZList_Map_KeyedOnLinkType.get( linkType );
				if ( resultsPreMZList == null ) {
					resultsPreMZList = new ArrayList<>( 1000000 );
					resultsPreMZList_Map_KeyedOnLinkType.put( linkType, resultsPreMZList );
				}
				
				resultsPreMZList.add( preMZ );
			}
		} catch ( Exception e ) {
			String msg = "Exception in search( ... ), \n sql: " + sql
					+ "\n Executed Statement: " + ((DelegatingPreparedStatement)pstmt).getDelegate().toString();
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
		
		return result;
	
	}
}
