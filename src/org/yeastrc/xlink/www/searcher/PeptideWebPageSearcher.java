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
import org.yeastrc.xlink.base.constants.Database_OneTrueZeroFalse_Constants;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.AnnotationDataBaseDTO;
import org.yeastrc.xlink.dto.PsmAnnotationDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.SearchReportedPeptideAnnotationDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;
import org.yeastrc.xlink.enum_classes.Yes_No__NOT_APPLICABLE_Enum;
import org.yeastrc.xlink.www.constants.DynamicModificationsSelectionConstants;
import org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.objects.SearchPeptideCrosslink;
import org.yeastrc.xlink.www.objects.SearchPeptideDimer;
import org.yeastrc.xlink.www.objects.SearchPeptideLooplink;
import org.yeastrc.xlink.www.objects.SearchPeptideUnlink;
import org.yeastrc.xlink.www.objects.WebReportedPeptide;
import org.yeastrc.xlink.www.objects.WebReportedPeptideWrapper;
import org.yeastrc.xlink.searcher_constants.SearcherGeneralConstants;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesAnnotationLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.utils.XLinkUtils;

/**
 * 
 *
 */
public class PeptideWebPageSearcher {
	
	private static final Logger log = Logger.getLogger(PeptideWebPageSearcher.class);

	private PeptideWebPageSearcher() { }
	private static final PeptideWebPageSearcher _INSTANCE = new PeptideWebPageSearcher();
	public static PeptideWebPageSearcher getInstance() { return _INSTANCE; }
	
	
	
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
	
	

	private final String PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS = "psm_fltrbl_tbl_";
	

	private final String PEPTIDE_VALUE_FILTER_TABLE_ALIAS = "srch__rep_pept_fltrbl_tbl_";
	

	private final String SQL_FIRST_PART = 
			

			"SELECT unified_rp__rep_pept__search__generic_lookup.reported_peptide_id, "
			
			+ " unified_rp__rep_pept__search__generic_lookup.unified_reported_peptide_id, "
			
			
			+ " unified_rp__rep_pept__search__generic_lookup.link_type, "
			+ " unified_rp__rep_pept__search__generic_lookup.psm_num_at_default_cutoff, "
			+ " unified_rp__rep_pept__search__generic_lookup.num_unique_psm_at_default_cutoff ";
	
			
	private final String SQL_MAIN_FROM_START = 			
			
			" FROM "
			
			+ " unified_rp__rep_pept__search__generic_lookup ";


	private final String SQL_LAST_PART = 

			"";
	
	// Removed since not needed.  
	// A WARN log message will be written if duplicate reported_peptide_id are found in the result set
//			" GROUP BY unified_rp__rep_pept__search__generic_lookup.reported_peptide_id ";

	
	
	/**
	 *   If Dynamic Mods are selected, this gets added after the Join to the Dynamic Mods subselect
	 */
	private final String SQL_MAIN_WHERE_START = 
					
			" WHERE unified_rp__rep_pept__search__generic_lookup.search_id = ? ";


	//  If Dynamic Mods are selected, one of these three gets added after the main where clause 
	
	//  No Mods Only
	private static final String SQL_NO_MODS_ONLY__MAIN_WHERE_CLAUSE = 
			" AND  unified_rp__rep_pept__search__generic_lookup.has_dynamic_modifictions  = " 
					+ Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_FALSE + " ";

	//  Yes Mods Only
	private static final String SQL_YES_MODS_ONLY_MAIN_WHERE_CLAUSE = 
			" AND  unified_rp__rep_pept__search__generic_lookup.has_dynamic_modifictions  = " 
					+	 Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE + " ";

	private static final String SQL_NO_AND_YES_MODS__MAIN_WHERE_CLAUSE = 
			" AND ( "
			// 		 No Mods
			+ 		"unified_rp__rep_pept__search__generic_lookup.has_dynamic_modifictions  = " 
			+ 		Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_FALSE 

			+ 		" OR "
			
			//  	 Yes Mods: 
			//				need srch_id_rep_pep_id_for_mod_masses.search_id IS NOT NULL 
			//				since doing LEFT OUTER JOIN when both Yes and No Mods
			+ 			" ( unified_rp__rep_pept__search__generic_lookup.has_dynamic_modifictions  = " 
			+ 				Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE 
			+ 				" AND"
			+ 				" srch_id_rep_pep_id_for_mod_masses.search_id IS NOT NULL "
			+     		 " ) " 
			+ " ) ";
	

	
	
			  

	
	///////////////////
	
	//  Additional SQL parts

	private static final String SQL_LINK_TYPE_START = "  unified_rp__rep_pept__search__generic_lookup.link_type IN ( ";
	private static final String SQL_LINK_TYPE_END = " ) ";



	
	
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
			" )  ";

	private static final String SQL_DYNAMIC_MOD_JOIN_START_LINK_TYPES = // After Dynamic Mod Masses 
						" AND  ( ";
	
	private static final String SQL_DYNAMIC_MOD_LINK_TYPE_START = "  search__reported_peptide__dynamic_mod_lookup.link_type IN ( ";
	private static final String SQL_DYNAMIC_MOD_LINK_TYPE_END = " ) ";


	private static final String SQL_DYNAMIC_MOD_JOIN_AFTER_LINK_TYPES = // After Link Types
						" )  ";

	private static final String SQL_DYNAMIC_MOD_JOIN_END = 
			" ) AS srch_id_rep_pep_id_for_mod_masses "
			+ " ON unified_rp__rep_pept__search__generic_lookup.search_id = srch_id_rep_pep_id_for_mod_masses.search_id "
			+ "    AND unified_rp__rep_pept__search__generic_lookup.reported_peptide_id = srch_id_rep_pep_id_for_mod_masses.reported_peptide_id";
	
	
	
	/**
	 * Get the peptides corresponding to the given parameters
	 * @param search The search we're searching
	 * @param searcherCutoffValuesSearchLevel - PSM and Peptide cutoffs for a search id
	 * @param linkTypes Which link types to include in the results
	 * @param modMassSelections Which modified masses to include.  Null if include all. element "" means no modifications
	 * @return
	 * @throws Exception
	 */
	public List<WebReportedPeptideWrapper> searchOnSearchIdPsmCutoffPeptideCutoff( 
			SearchDTO search, 
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel, 
			String[] linkTypes, 
			String[] modMassSelections ) throws Exception {

		List<WebReportedPeptideWrapper> wrappedLinks = new ArrayList<>();
		
		
		

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

		
		
		////////////
		
		//  All cutoffs are default?
		
		boolean onlyDefaultPeptideCutoffs = false;
		
		boolean onlyDefaultPsmCutoffs = false;
		
		
		if ( ! peptideCutoffValuesList.isEmpty()  ) {

			//   Check if any Peptide Cutoffs are default filters
			
			onlyDefaultPeptideCutoffs = true;


			for ( SearcherCutoffValuesAnnotationLevel item : peptideCutoffValuesList ) {

				if ( item.getAnnotationTypeDTO().getAnnotationTypeFilterableDTO() == null ) {

					String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + item.getAnnotationTypeDTO().getId();
					log.error( msg );
					throw new Exception(msg);
				}

				if ( ! item.annotationValueMatchesDefault() ) {

					//  Non-default filter value found so set to false

					onlyDefaultPeptideCutoffs = false;
					break;
				}
			}
		}


		if ( ! psmCutoffValuesList.isEmpty()  ) {

			//   Check if all Psm Cutoffs are default values
			
			onlyDefaultPsmCutoffs = true;

			for ( SearcherCutoffValuesAnnotationLevel item : psmCutoffValuesList ) {

				if ( ! item.annotationValueMatchesDefault() ) {

					onlyDefaultPsmCutoffs = false;
					break;
				}
			}
		}		
		
		//////////////////////////////////
		
		
		
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		
		int searchId = search.getId();

		
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
		
		
		///////   Add fields to result from best PSM annotation values
		
		{
			
			if ( ( ( ! onlyDefaultPsmCutoffs ) || ( ! onlyDefaultPeptideCutoffs ) )
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

					sqlSB.append( " , " );
					
					sqlSB.append( PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".best_psm_value_string_for_ann_type_id " );
					sqlSB.append( " AS "  );
					sqlSB.append( PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( "_best_psm_value_string_for_ann_type_id " );


				}
			}
		}
		
		///////   Add fields to result from best Peptide annotation values

		{
			
			if ( ( ( ! onlyDefaultPsmCutoffs ) || ( ! onlyDefaultPeptideCutoffs ) )
					|| ( ! USE_PEPTIDE_PSM_DEFAULTS_TO_SKIP_JOIN_ANNOTATION_DATA_VALUES_TABLES ) ) {


				//  Non-Default PSM or Peptide cutoffs so have to query on the cutoffs

				//  Add inner join for each Peptide cutoff

				for ( int counter = 1; counter <= peptideCutoffValuesList.size(); counter++ ) {

					sqlSB.append( " , " );

					sqlSB.append( PEPTIDE_VALUE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".annotation_type_id " );
					sqlSB.append( " AS "  );
					sqlSB.append( PEPTIDE_VALUE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( "_annotation_type_id " );

					sqlSB.append( " , " );

					sqlSB.append( PEPTIDE_VALUE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".value_double " );
					sqlSB.append( " AS "  );
					sqlSB.append( PEPTIDE_VALUE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( "_value_double " );

					sqlSB.append( " , " );
					
					sqlSB.append( PEPTIDE_VALUE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".value_string " );
					sqlSB.append( " AS "  );
					sqlSB.append( PEPTIDE_VALUE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( "_value_string " );

				}
			}
		}

		
		
		

		sqlSB.append( SQL_MAIN_FROM_START );
		
		
		
		{
			
			if ( ( ( ! onlyDefaultPsmCutoffs ) || ( ! onlyDefaultPeptideCutoffs ) )
					|| ( ! USE_PEPTIDE_PSM_DEFAULTS_TO_SKIP_JOIN_ANNOTATION_DATA_VALUES_TABLES ) ) {


				//  Non-Default PSM or Peptide cutoffs so have to query on the cutoffs



				//  Add inner join for each PSM cutoff

				for ( int counter = 1; counter <= psmCutoffValuesList.size(); counter++ ) {

					sqlSB.append( " INNER JOIN " );

					sqlSB.append( " unified_rp__rep_pept__search__best_psm_value_generic_lookup AS " );
					
					sqlSB.append( PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );

					sqlSB.append( " ON "  );

					sqlSB.append( " unified_rp__rep_pept__search__generic_lookup.search_id = "  );

					sqlSB.append( PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".search_id" );

					sqlSB.append( " AND " );


					sqlSB.append( " unified_rp__rep_pept__search__generic_lookup.reported_peptide_id = "  );

					sqlSB.append( PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".reported_peptide_id" );
				}
			}
		}
		
		{
			
			if ( ( ( ! onlyDefaultPsmCutoffs ) || ( ! onlyDefaultPeptideCutoffs ) )
					|| ( ! USE_PEPTIDE_PSM_DEFAULTS_TO_SKIP_JOIN_ANNOTATION_DATA_VALUES_TABLES ) ) {


				//  Non-Default PSM cutoffs so have to query on the cutoffs

				//  Add inner join for each Peptide cutoff

				for ( int counter = 1; counter <= peptideCutoffValuesList.size(); counter++ ) {

					sqlSB.append( " INNER JOIN " );

					sqlSB.append( " srch__rep_pept__annotation AS " );
					sqlSB.append( PEPTIDE_VALUE_FILTER_TABLE_ALIAS );

					sqlSB.append( Integer.toString( counter ) );

					sqlSB.append( " ON "  );

					sqlSB.append( " unified_rp__rep_pept__search__generic_lookup.search_id = "  );

					sqlSB.append( PEPTIDE_VALUE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".search_id" );

					sqlSB.append( " AND " );


					sqlSB.append( " unified_rp__rep_pept__search__generic_lookup.reported_peptide_id = "  );

					sqlSB.append( PEPTIDE_VALUE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".reported_peptide_id" );

				}
			}
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

			sqlSB.append( " ) " );
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
			
			if ( ( ( ! onlyDefaultPsmCutoffs ) || ( ! onlyDefaultPeptideCutoffs ) )
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


				sqlSB.append( " unified_rp__rep_pept__search__generic_lookup.psm_num_at_default_cutoff > 0 " );

				
			}
		}
		
		//  Process Peptide Cutoffs for WHERE

		{

			if ( ( ( ! onlyDefaultPsmCutoffs ) || ( ! onlyDefaultPeptideCutoffs ) )
					|| ( ! USE_PEPTIDE_PSM_DEFAULTS_TO_SKIP_JOIN_ANNOTATION_DATA_VALUES_TABLES ) ) {

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


				sqlSB.append( " unified_rp__rep_pept__search__generic_lookup.peptide_meets_default_cutoffs = '" );
				sqlSB.append( Yes_No__NOT_APPLICABLE_Enum.YES.value() );
				sqlSB.append( "' " );

			}
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

			//   For:   unified_rp__rep_pept__search__generic_lookup.search_id = ? 

			paramCounter++;
			pstmt.setInt( paramCounter, searchId );

			
			// Process PSM Cutoffs for WHERE


			{
				if ( ( ( ! onlyDefaultPsmCutoffs ) || ( ! onlyDefaultPeptideCutoffs ) )
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

				if ( ( ( ! onlyDefaultPsmCutoffs ) || ( ! onlyDefaultPeptideCutoffs ) )
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

				WebReportedPeptideWrapper item = populateFromResultSet( 
						rs, 
						search, 
						searcherCutoffValuesSearchLevel, 
						
						peptideCutoffsAnnotationTypeDTOList,
						psmCutoffsAnnotationTypeDTOList,

						onlyDefaultPeptideCutoffs,
						onlyDefaultPsmCutoffs, 
						sql );
				
				if ( item != null ) {
					
					int itemReportedPeptideId = item.getWebReportedPeptide().getReportedPeptideId();
					
					if ( ! retrieved_reported_peptide_id_values_Set.add( itemReportedPeptideId ) ) {
						
						String msg = "Already processed result entry for itemReportedPeptideId: " + itemReportedPeptideId;

						log.warn( msg );
						
//						log.error( msg );
//						throw new ProxlWebappDataException(msg);
						
					} else {

						wrappedLinks.add( item );
						
					}
				}
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
		
		return wrappedLinks;
	}


	/**
	 * @param rs
	 * @param search
	 * @param searcherCutoffValuesSearchLevel
	 * @param onlyDefaultPsmCutoffs - true if the only PSM cutoffs are default cutoffs at default cutoff values
	 * @param sql
	 * @return - null if PSM count is zero or link type unknown, otherwise a populated object 
	 * @throws SQLException
	 * @throws Exception
	 */
	private WebReportedPeptideWrapper populateFromResultSet(
			
			ResultSet rs,
			SearchDTO search,
			
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel, 
			

			List<AnnotationTypeDTO> peptideCutoffsAnnotationTypeDTOList,

			List<AnnotationTypeDTO> psmCutoffsAnnotationTypeDTOList,

			boolean onlyDefaultPeptideCutoffs,
			boolean onlyDefaultPsmCutoffs,
			
			String sql
			
			) throws SQLException, Exception {
		
		
		
		WebReportedPeptide item = new WebReportedPeptide();
		

		WebReportedPeptideWrapper wrappedItem = new WebReportedPeptideWrapper();
		
		wrappedItem.setWebReportedPeptide( item );
		
		
		item.setSearcherCutoffValuesSearchLevel( searcherCutoffValuesSearchLevel );
		

		String linkType = rs.getString( "link_type" );
		
		int reportedPeptideId = rs.getInt( "reported_peptide_id" );
		
		int unifiedReportedPeptideId = rs.getInt( "unified_reported_peptide_id" );
		
		
		
		item.setSearchId( search.getId() );
		item.setReportedPeptideId( reportedPeptideId );
		
		item.setUnifiedReportedPeptideId( unifiedReportedPeptideId );
		
		item.setSearch( search );
		

		
		if ( onlyDefaultPsmCutoffs && onlyDefaultPeptideCutoffs ) {
			
			int numPsmsForDefaultCutoffs = rs.getInt( "psm_num_at_default_cutoff" );
			if ( ! rs.wasNull() ) {
			
				item.setNumPsms( numPsmsForDefaultCutoffs );
			}

			int numUniquePsmsForDefaultCutoffs = rs.getInt( "num_unique_psm_at_default_cutoff" );
			if ( ! rs.wasNull() ) {
			
				item.setNumUniquePsms( numUniquePsmsForDefaultCutoffs );
			}
			
		}
		

		if ( peptideCutoffsAnnotationTypeDTOList.size() > 1 
				|| psmCutoffsAnnotationTypeDTOList.size() > 1 ) {
			
			if ( item.getNumPsms() <= 0 ) {

				//  !!!!!!!   Number of PSMs is zero this this isn't really a peptide that meets the cutoffs
				
				return null;  //  EARY EXIT
			}
		}
		
		
		//  Get Peptide and PSM annotations
		
		//  Peptide annotations are for Peptide annotations searched for
		
		//  PSM annotations are for PSM annotations searched for and are best values for the peptides


		if ( ( ( ! onlyDefaultPeptideCutoffs ) || ( ! onlyDefaultPsmCutoffs ) )
			|| ( ! USE_PEPTIDE_PSM_DEFAULTS_TO_SKIP_JOIN_ANNOTATION_DATA_VALUES_TABLES ) ) {

			Map<Integer, AnnotationDataBaseDTO> searchedForPeptideAnnotationDTOFromQueryMap = getPeptideValuesFromDBQuery( rs, peptideCutoffsAnnotationTypeDTOList );
			
			wrappedItem.setPeptideAnnotationDTOMap( searchedForPeptideAnnotationDTOFromQueryMap );
			
			
		} else {
			
			//  Get Peptide values in separate DB query, since peptide value table was not joined
			
//			List<Integer> peptideCutoffsAnnotationTypeIdList = new ArrayList<>( peptideCutoffsAnnotationTypeDTOList.size() );
//			
//			for ( AnnotationTypeDTO peptideAnnotationTypeDTO : peptideCutoffsAnnotationTypeDTOList ) {
//				
//				peptideCutoffsAnnotationTypeIdList.add( peptideAnnotationTypeDTO.getId() );
//			}
//			
//
//			List<SearchReportedPeptideAnnotationDTO> searchedForPeptideAnnotationDTOList =
//					SearchReportedPeptideAnnotationDataSearcher.getInstance().
//					getSearchReportedPeptideAnnotationDTOList( search.getId(), reportedPeptideId, peptideCutoffsAnnotationTypeIdList );
//			
//			item.setSearchedForPeptideAnnotationDTOList( searchedForPeptideAnnotationDTOList );
			
			
		}
		
		if ( ( ( ! onlyDefaultPeptideCutoffs )|| ( ! onlyDefaultPsmCutoffs ) )
				|| ( ! USE_PEPTIDE_PSM_DEFAULTS_TO_SKIP_JOIN_ANNOTATION_DATA_VALUES_TABLES ) ) {

			//  Get PSM best values from DB query, since psm best value table was joined

			Map<Integer, AnnotationDataBaseDTO> bestPsmAnnotationDTOFromQueryMap =
					getPSMBestValuesFromDBQuery( rs, psmCutoffsAnnotationTypeDTOList );

			wrappedItem.setPsmAnnotationDTOMap( bestPsmAnnotationDTOFromQueryMap );

		} else {
			

			//  Get PSM best values in separate DB query, since psm best value table was not joined

//			List<PsmAnnotationDTO> bestPsmAnnotationDTOList = 
//					PsmAnnotationDataBestValueForPeptideSearcher.getInstance()
//					.getPsmAnnotationDataBestValueForPeptideList( search.getId(), reportedPeptideId, psmCutoffsAnnotationTypeDTOList );
//
//			item.setBestPsmAnnotationDTOList( bestPsmAnnotationDTOList );

			int z = 0;
		}
		
		
		

		if ( XLinkUtils.CROSS_TYPE_STRING.equals(linkType) ) {
			
			SearchPeptideCrosslink link = new SearchPeptideCrosslink();

			link.setSearch( search );
			link.setReportedPeptideId( reportedPeptideId );

			item.setSearchPeptideCrosslink(link);
			
		} else if ( XLinkUtils.LOOP_TYPE_STRING.equals(linkType) ) {
			
			
			SearchPeptideLooplink link = new SearchPeptideLooplink();
			
			link.setSearch( search );
			link.setReportedPeptideId ( reportedPeptideId );

			item.setSearchPeptideLooplink(link);
			

		} else if ( XLinkUtils.UNLINKED_TYPE_STRING.equals(linkType) ) {
			
			
			SearchPeptideUnlink link = new SearchPeptideUnlink();
			
			link.setSearch( search );
			link.setReportedPeptideId ( reportedPeptideId );

			item.setSearchPeptideUnlinked(link);
			
		} else if ( XLinkUtils.DIMER_TYPE_STRING.equals(linkType) ) {
			
			
			SearchPeptideDimer link = new SearchPeptideDimer();
			
			link.setSearch( search );
			link.setReportedPeptideId ( reportedPeptideId );

			item.setSearchPeptideDimer(link);
								
			
		} else {
			
			
			String msg = "Unknown link type in populateFromResultSet( ... ), linkType: " + linkType + ", sql: " + sql;
			
			log.error( msg );
			
			
			return null;  //  EARLY RETURN:    skip over other types for now
		}
		
		
		return wrappedItem;
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
			String valueStringField = PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS + counter + "_best_psm_value_string_for_ann_type_id";

			item.setAnnotationTypeId( rs.getInt( annotationTypeIdField ) );

			item.setValueDouble( rs.getDouble( valueDoubleField ) );
			item.setValueString( rs.getString( valueStringField ) );

			bestPsmAnnotationDTOFromQueryMap.put( item.getAnnotationTypeId(),  item );

		}
		
		return bestPsmAnnotationDTOFromQueryMap;
	}


	//  Get Peptide values from DB query, since peptide value table was joined

	/**
	 * @param rs
	 * @param peptideCutoffsAnnotationTypeDTOList
	 * @return
	 * @throws SQLException
	 */
	private Map<Integer, AnnotationDataBaseDTO> getPeptideValuesFromDBQuery( 


			ResultSet rs,

			List<AnnotationTypeDTO> peptideCutoffsAnnotationTypeDTOList

			) throws SQLException { 

		Map<Integer, AnnotationDataBaseDTO> searchedForPeptideAnnotationDTOFromQueryMap = new HashMap<>();

		//  Add inner join for each Peptide cutoff

		for ( int counter = 1; counter <= peptideCutoffsAnnotationTypeDTOList.size(); counter++ ) {

			SearchReportedPeptideAnnotationDTO item = new SearchReportedPeptideAnnotationDTO();

			String annotationTypeIdField = PEPTIDE_VALUE_FILTER_TABLE_ALIAS + counter + "_annotation_type_id";

			String valueDoubleField = PEPTIDE_VALUE_FILTER_TABLE_ALIAS + counter + "_value_double";
			String valueStringField = PEPTIDE_VALUE_FILTER_TABLE_ALIAS + counter + "_value_string";
		
			item.setAnnotationTypeId( rs.getInt( annotationTypeIdField ) );

			item.setValueDouble( rs.getDouble( valueDoubleField ) );
			item.setValueString( rs.getString( valueStringField ) );

			searchedForPeptideAnnotationDTOFromQueryMap.put( item.getAnnotationTypeId(), item );

		}
		
		return searchedForPeptideAnnotationDTOFromQueryMap;
	}

	
		
}
