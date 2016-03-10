package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.base.constants.Database_OneTrueZeroFalse_Constants;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;
import org.yeastrc.xlink.enum_classes.Yes_No__NOT_APPLICABLE_Enum;
import org.yeastrc.xlink.searcher_constants.SearcherGeneralConstants;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesAnnotationLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.constants.DynamicModificationsSelectionConstants;
import org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants;
import org.yeastrc.xlink.www.objects.MergedSearchPeptideCrosslink;
import org.yeastrc.xlink.www.objects.MergedSearchPeptideDimer;
import org.yeastrc.xlink.www.objects.MergedSearchPeptideLooplink;
import org.yeastrc.xlink.www.objects.MergedSearchPeptideUnlinked;
import org.yeastrc.xlink.www.objects.WebMergedReportedPeptide;

/**
 * 
 *
 */
public class PeptideMergedWebPageSearcher {
	
	private static final Logger log = Logger.getLogger(PeptideMergedWebPageSearcher.class);

	private PeptideMergedWebPageSearcher() { }
	private static final PeptideMergedWebPageSearcher _INSTANCE = new PeptideMergedWebPageSearcher();
	public static PeptideMergedWebPageSearcher getInstance() { return _INSTANCE; }
	

	private final String SEARCH_ID_GROUP_SEPARATOR = ","; //  separator as search ids are combined by the group by

	

	private final String SQL_FIRST_PART = 
			

			"SELECT subquery_result.unified_reported_peptide_id, "
			
			+ " GROUP_CONCAT( DISTINCT subquery_result.search_id SEPARATOR '" + SEARCH_ID_GROUP_SEPARATOR + "' ) AS search_ids, "

			+ " subquery_result.link_type, "
			+ " SUM( subquery_result.psm_num_at_default_cutoff ) AS psm_num_at_default_cutoff "
			
			+ " FROM "
			
			+ " ( ";
			

	  
	private final String SQL_LAST_PART = 
			
		  " ) AS subquery_result  GROUP BY subquery_result.unified_reported_peptide_id ";
		
			
			//  No "ORDER BY".  Sorted in Java
	

	private final String SQL_EACH_UNION_FIRST_PART_PART = 
			

			"SELECT unified_rp__rep_pept__search__generic_lookup.unified_reported_peptide_id, "
			
			+ " unified_rp__rep_pept__search__generic_lookup.search_id, "

			+ " unified_rp__rep_pept__search__generic_lookup.link_type, "
			+ " unified_rp__rep_pept__search__generic_lookup.psm_num_at_default_cutoff "
			
			+ " FROM "

			+ " unified_rp__rep_pept__search__generic_lookup ";

			
	
	/**
	 *   If Dynamic Mods are selected, this gets added after the Join to the Dynamic Mods subselect
	 */
	

	private final String SQL_SUB_PER_UNION_SELECT_WHERE_START = 
					
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
	
	//  Link Type processing

	private static final String SQL_LINK_TYPE_START = "  unified_rp__rep_pept__search__generic_lookup.link_type IN ( ";
	private static final String SQL_LINK_TYPE_END = " )  ";



	
	
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
	 * Internal class for holding the cutoffs for a specific search
	 *
	 */
	private static class CutoffsPerSearchHolder {
		
		int searchId;
		
		List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList;
		List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesList;
		
		boolean onlyDefaultPsmCutoffs;

		Yes_No__NOT_APPLICABLE_Enum   defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.NOT_APPLICABLE;
		
	}
	

	
	/**
	 * Get the peptides corresponding to the given parameters
	 * @param search The search we're searching
	 * @param searcherCutoffValuesRootLevel - PSM and Peptide cutoffs for all search ids
	 * @param linkTypes Which link types to include in the results
	 * @param modMassSelections Which modified masses to include.  Null if include all. element "" means no modifications
	 * @return
	 * @throws Exception
	 */
	public List<WebMergedReportedPeptide> searchOnSearchIdPsmCutoffPeptideCutoff( 
			Collection<SearchDTO> searchesParam, 
			SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel,
			String[] linkTypes, 
			String[] modMassSelections ) throws Exception {

		List<WebMergedReportedPeptide> links = new ArrayList<WebMergedReportedPeptide>();
		
		
		
		List<SearchDTO> searches = new ArrayList<>( searchesParam );
		
		Collections.sort( searches ); //  ensure in id order
		
		

		
		//  Copy cutoff values to lists (need to guarantee order since process same objects in multiple places)

		List<CutoffsPerSearchHolder> cutoffsPerSearchHolderList = new ArrayList<>( searches.size() );
		
		//  Process cutoffs per search
		
		for ( SearchDTO searchDTO : searches ) {
			
			int searchId = searchDTO.getId();
		
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = searcherCutoffValuesRootLevel.getPerSearchCutoffs( searchId );
			
			if ( searcherCutoffValuesSearchLevel == null ) {
				
				String msg = "Unable to get cutoffs for search id: " + searchId;
				log.error( msg );
				throw new Exception(msg);
			}


			List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesPerSearchList = 
					searcherCutoffValuesSearchLevel.getPeptidePerAnnotationCutoffsList();
			
			List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesPerSearchList = 
					searcherCutoffValuesSearchLevel.getPsmPerAnnotationCutoffsList();


			//  All cutoffs for search id are default?
			

			Yes_No__NOT_APPLICABLE_Enum   defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.NOT_APPLICABLE;
				
			boolean onlyDefaultPsmCutoffs = true;
			

			for ( SearcherCutoffValuesAnnotationLevel entry  : psmCutoffValuesPerSearchList ) {

				if ( ! entry.annotationValueMatchesDefault() ) {
					
					onlyDefaultPsmCutoffs = false;
					break;
				}
			}
			
			

			//   Check if any Peptide Cutoffs are default filters
			
			for ( SearcherCutoffValuesAnnotationLevel item : peptideCutoffValuesPerSearchList ) {

				if ( item.getAnnotationTypeDTO().getAnnotationTypeFilterableDTO() == null ) {
					
					String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + item.getAnnotationTypeDTO().getId();
					log.error( msg );
					throw new Exception(msg);
				}
				
				if ( item.getAnnotationTypeDTO().getAnnotationTypeFilterableDTO().isDefaultFilter() ) {
					
					defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.YES;
					break;
				}
			}
			
			
			//   Check if all Peptide Cutoffs are default values
			
			for ( SearcherCutoffValuesAnnotationLevel item : peptideCutoffValuesPerSearchList ) {
				
				if ( ! item.annotationValueMatchesDefault() ) {
					
					defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.NO;
					break;
				}
			}

			
			CutoffsPerSearchHolder cutoffsPerSearchHolder = new CutoffsPerSearchHolder();
			
			cutoffsPerSearchHolder.searchId = searchId;
			
			cutoffsPerSearchHolder.peptideCutoffValuesList = peptideCutoffValuesPerSearchList;
			cutoffsPerSearchHolder.psmCutoffValuesList = psmCutoffValuesPerSearchList;
			
			cutoffsPerSearchHolder.onlyDefaultPsmCutoffs = onlyDefaultPsmCutoffs;
			
			cutoffsPerSearchHolder.defaultPeptideCutoffs = defaultPeptideCutoffs;
			
			cutoffsPerSearchHolderList.add( cutoffsPerSearchHolder );
		}

		////////////
		
		//  All cutoffs are default?

		boolean onlyDefaultPsmCutoffsAllSearches = true;
		


		//   Check if all Psm Cutoffs are default values
		
		for ( CutoffsPerSearchHolder item : cutoffsPerSearchHolderList ) {
			
			if ( ! item.onlyDefaultPsmCutoffs ) {
				
				onlyDefaultPsmCutoffsAllSearches = false;
				break;
			}
		}
		
		
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
		
		boolean firstCutoffsPerSearchHolder = true;
		

		for ( CutoffsPerSearchHolder cutoffsPerSearchHolder : cutoffsPerSearchHolderList ) {

			if ( firstCutoffsPerSearchHolder ) {
				
				firstCutoffsPerSearchHolder = false;
			} else {
				
				sqlSB.append( " UNION " );
			}
			
			sqlSB.append( SQL_EACH_UNION_FIRST_PART_PART );
			

			{

				//  Non-Default PSM cutoffs so have to query on the cutoffs


				int tableIndexCounter = 0;

				if ( ! cutoffsPerSearchHolder.onlyDefaultPsmCutoffs ) {


					//  Add inner join for each PSM cutoff

					for ( int index = 1; index <= cutoffsPerSearchHolder.psmCutoffValuesList.size(); index++ ) {


						tableIndexCounter++;

						sqlSB.append( " INNER JOIN " );

						sqlSB.append( " unified_rp__rep_pept__search__best_psm_value_generic_lookup AS psm_fltrbl_tbl_" );
						sqlSB.append( Integer.toString( tableIndexCounter ) );

						sqlSB.append( " ON "  );

						sqlSB.append( " unified_rp__rep_pept__search__generic_lookup.search_id = "  );

						sqlSB.append( "psm_fltrbl_tbl_" );
						sqlSB.append( Integer.toString( tableIndexCounter ) );
						sqlSB.append( ".search_id" );

						sqlSB.append( " AND " );


						sqlSB.append( " unified_rp__rep_pept__search__generic_lookup.reported_peptide_id = "  );

						sqlSB.append( "psm_fltrbl_tbl_" );
						sqlSB.append( Integer.toString( tableIndexCounter ) );
						sqlSB.append( ".reported_peptide_id" );
					}
				}

			}

			{
				if ( cutoffsPerSearchHolder.defaultPeptideCutoffs == Yes_No__NOT_APPLICABLE_Enum.NO ) {

					//  Non-Default PSM cutoffs so have to query on the cutoffs


					//  Add inner join for each Peptide cutoff

					int tableIndexCounter = 0;


					for ( int index = 1; index <= cutoffsPerSearchHolder.peptideCutoffValuesList.size(); index++ ) {

						tableIndexCounter++;

						sqlSB.append( " INNER JOIN " );

						sqlSB.append( " srch__rep_pept__annotation AS srch__rep_pept_fltrbl_tbl_" );
						sqlSB.append( Integer.toString( tableIndexCounter ) );

						sqlSB.append( " ON "  );

						sqlSB.append( " unified_rp__rep_pept__search__generic_lookup.search_id = "  );

						sqlSB.append( "srch__rep_pept_fltrbl_tbl_" );
						sqlSB.append( Integer.toString( tableIndexCounter ) );
						sqlSB.append( ".search_id" );

						sqlSB.append( " AND " );


						sqlSB.append( " unified_rp__rep_pept__search__generic_lookup.reported_peptide_id = "  );

						sqlSB.append( "srch__rep_pept_fltrbl_tbl_" );
						sqlSB.append( Integer.toString( tableIndexCounter ) );
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

			sqlSB.append( SQL_SUB_PER_UNION_SELECT_WHERE_START );

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


				if ( cutoffsPerSearchHolder.onlyDefaultPsmCutoffs ) {

					//   Only Default PSM Cutoffs chosen so criteria simply the Peptides where the PSM count for the default cutoffs is > zero


					sqlSB.append( " AND " );


					sqlSB.append( " unified_rp__rep_pept__search__generic_lookup.psm_num_at_default_cutoff > 0 " );


				} else {


					//  Non-Default PSM cutoffs so have to query on the cutoffs

					int counter = 0; 

					for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesPsmAnnotationLevel : cutoffsPerSearchHolder.psmCutoffValuesList ) {


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

						if ( srchPgmFilterablePsmAnnotationTypeDTO.getAnnotationTypeFilterableDTO().getFilterDirectionType()
								== FilterDirectionType.ABOVE ) {

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
				

				if ( cutoffsPerSearchHolder.defaultPeptideCutoffs == Yes_No__NOT_APPLICABLE_Enum.NOT_APPLICABLE ) {

					//  No WHERE criteria for defaultPeptideCutoffs == Yes_No__NOT_APPLICABLE_Enum.NOT_APPLICABLE
					
					//     There are no Peptide cutoffs to apply
					
					
				
				} else if ( cutoffsPerSearchHolder.defaultPeptideCutoffs == Yes_No__NOT_APPLICABLE_Enum.YES ) {

					//   Only Default Peptide Cutoffs chosen so criteria simply the Peptides where the defaultPeptideCutoffs is yes

					sqlSB.append( " AND " );


					sqlSB.append( " unified_rp__rep_pept__search__generic_lookup.peptide_meets_default_cutoffs = '" );
					sqlSB.append( Yes_No__NOT_APPLICABLE_Enum.YES.value() );
					sqlSB.append( "' " );

					
				} else if ( cutoffsPerSearchHolder.defaultPeptideCutoffs == Yes_No__NOT_APPLICABLE_Enum.NO ) {

					
					//  Non-Default Peptide cutoffs so have to query on the cutoffs

					int counter = 0; 

					for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesReportedPeptideAnnotationLevel : cutoffsPerSearchHolder.peptideCutoffValuesList ) {

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

						if ( srchPgmFilterableReportedPeptideAnnotationTypeDTO.getAnnotationTypeFilterableDTO().getFilterDirectionType() 
								== FilterDirectionType.ABOVE ) {

							sqlSB.append( SearcherGeneralConstants.SQL_END_BIGGER_VALUE_BETTER );

						} else {

							sqlSB.append( SearcherGeneralConstants.SQL_END_SMALLER_VALUE_BETTER );

						}

						sqlSB.append( "? " );

						sqlSB.append( " ) " );
					}
				}
			}
		}
		
		sqlSB.append( SQL_LAST_PART );
		
		
		
		
		String sql = sqlSB.toString();
		
//		Collection<Integer> searchIds = new HashSet<Integer>();
//		for( SearchDTO search : searches )
//			searchIds.add( search.getId() );
//		
//		sql = sql.replaceAll( "#SEARCHES#", StringUtils.join( searchIds, "," ) );

		
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			

			int paramCounter = 0;
			


			for ( CutoffsPerSearchHolder cutoffsPerSearchHolder : cutoffsPerSearchHolderList ) {


				//  If Yes modifications, set search id for inner join subselect

				if ( modMassSelectionsIncludesYesModifications && modMassSelectionsWithoutNoMods != null ) {

					paramCounter++;
					pstmt.setInt( paramCounter, cutoffsPerSearchHolder.searchId );
				}

				
				paramCounter++;
				pstmt.setInt( paramCounter, cutoffsPerSearchHolder.searchId );
				
				

				// Process PSM Cutoffs for WHERE


				{

					if ( ! cutoffsPerSearchHolder.onlyDefaultPsmCutoffs ) {

						//  PSM Cutoffs are not the default 

						for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesPsmAnnotationLevel : cutoffsPerSearchHolder.psmCutoffValuesList ) {

							AnnotationTypeDTO srchPgmFilterablePsmAnnotationTypeDTO = searcherCutoffValuesPsmAnnotationLevel.getAnnotationTypeDTO();

							paramCounter++;
							pstmt.setInt( paramCounter, cutoffsPerSearchHolder.searchId );

							paramCounter++;
							pstmt.setInt( paramCounter, srchPgmFilterablePsmAnnotationTypeDTO.getId() );

							paramCounter++;
							pstmt.setDouble( paramCounter, searcherCutoffValuesPsmAnnotationLevel.getAnnotationCutoffValue() );
						}

					}
				}




				// Process Peptide Cutoffs for WHERE


				{

					if ( cutoffsPerSearchHolder.defaultPeptideCutoffs == Yes_No__NOT_APPLICABLE_Enum.NO ) {
						
						//  Non-Default Peptide cutoffs so have to query on the cutoffs

						for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesReportedPeptideAnnotationLevel : cutoffsPerSearchHolder.peptideCutoffValuesList ) {

							AnnotationTypeDTO srchPgmFilterableReportedPeptideAnnotationTypeDTO = searcherCutoffValuesReportedPeptideAnnotationLevel.getAnnotationTypeDTO();

							paramCounter++;
							pstmt.setInt( paramCounter, cutoffsPerSearchHolder.searchId );

							paramCounter++;
							pstmt.setInt( paramCounter, srchPgmFilterableReportedPeptideAnnotationTypeDTO.getId() );

							paramCounter++;
							pstmt.setDouble( paramCounter, searcherCutoffValuesReportedPeptideAnnotationLevel.getAnnotationCutoffValue() );
						}

					}
				}
			}
			
			
			rs = pstmt.executeQuery();

			while( rs.next() ) {
				

				WebMergedReportedPeptide item = new WebMergedReportedPeptide();
				

				String linkTypeFromDBField = rs.getString( "link_type" );
				
				int unifiedReportedPeptideId = rs.getInt( "unified_reported_peptide_id" );
				
				item.setUnifiedReportedPeptideId( unifiedReportedPeptideId );

				
				//  Build collection of SearchDTO objects for the search ids found for this unified_reported_peptide_id
				
				String searchIdsCommaDelimString = rs.getString( "search_ids" );
				List<SearchDTO> searchesFoundInCurrentRecord = getSearchDTOsForCurrentResultRecord( searches, searchIdsCommaDelimString );
				
				item.setNumSearches( searchesFoundInCurrentRecord.size() );
				
				List<Integer> searchIdsFoundInCurrentRecord = new ArrayList<>( searchesFoundInCurrentRecord.size() );
				
				for ( SearchDTO searchDTO : searchesFoundInCurrentRecord ) {
					
					searchIdsFoundInCurrentRecord.add( searchDTO.getId() );
				}
				
				item.setSearchIds( searchIdsFoundInCurrentRecord );
				
				//  pass through cutoffs
				
				item.setSearcherCutoffValuesRootLevel( searcherCutoffValuesRootLevel );

				

				if ( onlyDefaultPsmCutoffsAllSearches ) {
					
					int numPsmsForDefaultCutoffs = rs.getInt( "psm_num_at_default_cutoff" );
					if ( ! rs.wasNull() ) {
					
						item.setNumPsms( numPsmsForDefaultCutoffs );
					}
				}
				
				if ( XLinkUtils.CROSS_TYPE_STRING.equals(linkTypeFromDBField) ) {
					
					MergedSearchPeptideCrosslink link = new MergedSearchPeptideCrosslink();

					link.setUnifiedReportedPeptideId( unifiedReportedPeptideId );
					link.setSearches( searchesFoundInCurrentRecord );
					
					item.setMergedSearchPeptideCrosslink(link);
					
				} else if ( XLinkUtils.LOOP_TYPE_STRING.equals(linkTypeFromDBField) ) {
					
					MergedSearchPeptideLooplink link = new MergedSearchPeptideLooplink();
					

					link.setUnifiedReportedPeptideId( unifiedReportedPeptideId );
					link.setSearches( searchesFoundInCurrentRecord );
					
					item.setMergedSearchPeptideLooplink(link);
					
				} else if ( XLinkUtils.DIMER_TYPE_STRING.equals(linkTypeFromDBField) ) {
					
					MergedSearchPeptideDimer link = new MergedSearchPeptideDimer();

					link.setUnifiedReportedPeptideId( unifiedReportedPeptideId );
					link.setSearches( searchesFoundInCurrentRecord );
					
					item.setMergedSearchPeptideDimer( link );
					
					
				} else if ( XLinkUtils.UNLINKED_TYPE_STRING.equals(linkTypeFromDBField) ) {
					
					MergedSearchPeptideUnlinked link = new MergedSearchPeptideUnlinked();

					link.setUnifiedReportedPeptideId( unifiedReportedPeptideId );
					link.setSearches( searchesFoundInCurrentRecord );
					
					item.setMergedSearchPeptideUnlinked(link);
					
				} else {
					
					String msg = "linkType is invalid, linkType: " + linkTypeFromDBField;
					
					log.error( linkTypeFromDBField );
					
					throw new Exception( msg );
				}
				
				links.add( item );
			}
			
		} catch ( Exception e ) {
			
			String msg = "Exception in search( Collection<SearchDTO> searches, ... ), sql: " + sql;
			
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
		
		return links;
	}



	//////////////////////////////////////////////////////////////////////////////

	//  Build collection of SearchDTO objects for the search ids found for this unified_reported_peptide_id
	
	private List<SearchDTO> getSearchDTOsForCurrentResultRecord( List<SearchDTO> searches, String searchIdsCommaDelimString ) throws SQLException, Exception {
		
		
		List<SearchDTO> searchesFoundInCurrentRecord = new ArrayList<>( searches.size() );
		
		
		if ( searchIdsCommaDelimString != null  ) {
		
			String[] searchIdsCommaDelimStringSplit = searchIdsCommaDelimString.split( SEARCH_ID_GROUP_SEPARATOR );
			
			for ( String searchIdString : searchIdsCommaDelimStringSplit ) {
				
				int searchIdFoundInCurrentRecord = 0;
				
				try {
					
					searchIdFoundInCurrentRecord = Integer.parseInt( searchIdString );
				} catch ( Exception e ) {
					
					String msg = "Failed to parse search id from comma delim query result.  searchIdString: |"
							+ searchIdString + "|, searchIdsCommaDelimString from DB: |" + searchIdsCommaDelimString + "|.";
					
					log.error( msg, e );
					
					throw new Exception(msg);
				}
				
				// get SearchDTO from passed in collection.
				
				SearchDTO searchesItemForSearchIdFoundInCurrentRecord = null;
				
				for ( SearchDTO searchesItem : searches ) {
					
					if ( searchesItem.getId() == searchIdFoundInCurrentRecord ) {
						
						searchesItemForSearchIdFoundInCurrentRecord = searchesItem;
						break;
					}
				}
				
				if ( searchesItemForSearchIdFoundInCurrentRecord == null ) {
					
					String msg = "Failed to search id from comma delim query result in list of passed in SearchDTOs."
							+ "  searchId from comma delim query result: " + searchIdFoundInCurrentRecord;
					
					log.error( msg );
					
					throw new Exception(msg);
				}
				
				searchesFoundInCurrentRecord.add( searchesItemForSearchIdFoundInCurrentRecord );
			}
		}
		return searchesFoundInCurrentRecord;
	}
	
}
