package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.base.constants.Database_OneTrueZeroFalse_Constants;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.constants.DefaultQValueCutoffConstants;
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
			
			"SELECT unified_rep_pep__reported_peptide__search_lookup.unified_reported_peptide_id, "
			
			+ " GROUP_CONCAT( DISTINCT unified_rep_pep__reported_peptide__search_lookup.search_id SEPARATOR '" + SEARCH_ID_GROUP_SEPARATOR + "' ) AS search_ids, "
			
			+ " MIN( unified_rep_pep__reported_peptide__search_lookup.peptide_q_value_for_search ) AS min_q_value, "
			
			+ " MIN( unified_rep_pep__reported_peptide__search_lookup.best_psm_q_value ) AS best_psm_q_value,"
			+ " unified_rep_pep__reported_peptide__search_lookup.link_type, "
			+ " SUM( unified_rep_pep__reported_peptide__search_lookup.psm_num_at_pt_01_q_cutoff ) AS psm_num_at_pt_01_q_cutoff "
			
			+ " FROM "
			
			+ " unified_rep_pep__reported_peptide__search_lookup ";
	
	
	
	
	/**
	 *   If Dynamic Mods are selected, this gets added after the Join to the Dynamic Mods subselect
	 */
	private final String SQL_MAIN_WHERE_START = 
			
			" WHERE unified_rep_pep__reported_peptide__search_lookup.search_id IN ( #SEARCHES# ) "
			
					+ " AND ( unified_rep_pep__reported_peptide__search_lookup.peptide_q_value_for_search <= ? "
					+ 		" OR unified_rep_pep__reported_peptide__search_lookup.peptide_q_value_for_search IS NULL )   "
					
					+ " AND ( unified_rep_pep__reported_peptide__search_lookup.best_psm_q_value <= ? )   "
	  		;


	//  If Dynamic Mods are selected, one of these three gets added after the main where clause 
	
	//  No Mods Only
	private static final String SQL_NO_MODS_ONLY__MAIN_WHERE_CLAUSE = 
			" AND  unified_rep_pep__reported_peptide__search_lookup.has_dynamic_modifictions  = " 
					+ Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_FALSE + " ";

	//  Yes Mods Only
	private static final String SQL_YES_MODS_ONLY_MAIN_WHERE_CLAUSE = 
			" AND  unified_rep_pep__reported_peptide__search_lookup.has_dynamic_modifictions  = " 
					+	 Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE + " ";

	private static final String SQL_NO_AND_YES_MODS__MAIN_WHERE_CLAUSE = 
			" AND ( "
			// 		 No Mods
			+ 		"unified_rep_pep__reported_peptide__search_lookup.has_dynamic_modifictions  = " 
			+ 		Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_FALSE 

			+ 		" OR "
			
			//  	 Yes Mods: 
			//				need srch_id_rep_pep_id_for_mod_masses.search_id IS NOT NULL 
			//				since doing LEFT OUTER JOIN when both Yes and No Mods
			+ 			" ( unified_rep_pep__reported_peptide__search_lookup.has_dynamic_modifictions  = " 
			+ 				Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE 
			+ 				" AND"
			+ 				" srch_id_rep_pep_id_for_mod_masses.search_id IS NOT NULL "
			+     		 " ) " 
			+ " ) ";
	

	
	
			  
			  
	private final String SQL_LAST_PART = 
			
		  " GROUP BY unified_rep_pep__reported_peptide__search_lookup.unified_reported_peptide_id "
		+ " ORDER BY min_q_value, unified_rep_pep__reported_peptide__search_lookup.unified_reported_peptide_id";
	
	

	private static final String SQL_LINK_TYPE_START = "  unified_rep_pep__reported_peptide__search_lookup.link_type = '";
	private static final String SQL_LINK_TYPE_END = "' ";


	private static final String SQL_MONOLINK_INCLUDE = "  unified_rep_pep__reported_peptide__search_lookup.has_monolinks = " + Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE;

	private static final String SQL_NO_LINKS_INCLUDE = 
			" ( unified_rep_pep__reported_peptide__search_lookup.has_monolinks = " + Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_FALSE
			+ " AND ( unified_rep_pep__reported_peptide__search_lookup.link_type = '" + XLinkUtils.UNLINKED_TYPE_STRING + "' " 
			+             " OR  unified_rep_pep__reported_peptide__search_lookup.link_type = '" + XLinkUtils.DIMER_TYPE_STRING + "' "
			+     " ) "
			+ " ) ";

	
	
	//  Dynamic Mod processing
	
	private static final String SQL_DYNAMIC_MOD_JOIN_START = 
			" INNER JOIN (";

	private static final String SQL_DYNAMIC_MOD_AND_NO_MODS_JOIN_START = 
			" LEFT OUTER JOIN (";

	private static final String SQL_DYNAMIC_MOD_INNER_SELECT_START = 

			" SELECT DISTINCT search_id, reported_peptide_id "
			+		" FROM search__reported_peptide__dynamic_mod_lookup "
			+		" WHERE search_id IN ( #SEARCHES# ) AND best_psm_q_value <= ? AND dynamic_mod_mass IN ( ";

	private static final String SQL_DYNAMIC_MOD_JOIN_AFTER_MOD_MASSES = // After Dynamic Mod Masses 
			" )  ";

	private static final String SQL_DYNAMIC_MOD_JOIN_START_LINK_TYPES = // After Dynamic Mod Masses 
						" AND  ( ";
	
	private static final String SQL_DYNAMIC_MOD_LINK_TYPE_START = "  search__reported_peptide__dynamic_mod_lookup.link_type = '";
	private static final String SQL_DYNAMIC_MOD_LINK_TYPE_END = "' ";


	private static final String SQL_DYNAMIC_MOD_JOIN_AFTER_LINK_TYPES = // After Link Types
						" )  ";

	private static final String SQL_DYNAMIC_MOD_JOIN_END = 
			" ) AS srch_id_rep_pep_id_for_mod_masses "
			+ " ON unified_rep_pep__reported_peptide__search_lookup.search_id = srch_id_rep_pep_id_for_mod_masses.search_id "
			+ "    AND unified_rep_pep__reported_peptide__search_lookup.reported_peptide_id = srch_id_rep_pep_id_for_mod_masses.reported_peptide_id";
	
	

	/**
	 * Get the peptides corresponding to the given parameters
	 * @param search The search we're searching
	 * @param psmQValueCutoff The q-value cutoff to use for PSMs
	 * @param peptideQValueCutoff The q-value cutoff to use for peptides
	 * @param linkTypes Which link types to include in the results
	 * @param modMassSelections Which modified masses to include.  Null if include all. element "" means no modifications
	 * @return
	 * @throws Exception
	 */
	public List<WebMergedReportedPeptide> search( Collection<SearchDTO> searchesParam, double psmQValueCutoff, double peptideQValueCutoff,
			List<String> linkTypes, String[] modMassSelections ) throws Exception {
		

		List<WebMergedReportedPeptide> links = new ArrayList<WebMergedReportedPeptide>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		

		
		StringBuilder sqlSB = new StringBuilder( 1000 );
		
		
		List<SearchDTO> searches = new ArrayList<>( searchesParam );
		
		Collections.sort( searches ); //  ensure in id order
		
		
		//  Pre-process the dynamic mod masses selections
		
		boolean modMassSelectionsIncludesNoModifications = false;

		boolean modMassSelectionsIncludesYesModifications = false;

		List<String> modMassSelectionsWithoutNoMods = null; 

		if ( modMassSelections != null ) {
			
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
		}
		
		//  Pre-process the request link types.  These are web page link types that are not the same as DB link types
		
		boolean crosslinkTypeRequested = false;
		boolean looplinkTypeRequested = false;
		boolean monolinkTypeRequested = false;
		boolean nolinksRequested = false;
		
		
		if ( linkTypes != null && ( ! linkTypes.isEmpty() ) ) {

			for ( String linkType : linkTypes ) {

				if ( PeptideViewLinkTypesConstants.CROSSLINK_PSM.equals( linkType ) ) {
					
					crosslinkTypeRequested = true;

				} else if ( PeptideViewLinkTypesConstants.LOOPLINK_PSM.equals( linkType ) ) {

					looplinkTypeRequested = true;

				} else if ( PeptideViewLinkTypesConstants.MONOLINK_PSM.equals( linkType ) ) {

					monolinkTypeRequested = true;

				} else if ( PeptideViewLinkTypesConstants.NO_LINK_PSM.equals( linkType ) ) {

					nolinksRequested = true;

				} else {

					String msg = "linkType is invalid, linkType: " + linkType;

					log.error( linkType );

					throw new Exception( msg );
				}
			}
		}		
		
		
		
		
		
		
		//////////////////////
		
		/////   Start building the SQL
		
		
		sqlSB.append( SQL_FIRST_PART );
		
		
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

			if ( ! monolinkTypeRequested && ! nolinksRequested ) {
			
				//  Only can use this performance improvement approach 
				//  if ! monolinkTypeRequested  and ! nolinksRequested 

				if ( crosslinkTypeRequested || looplinkTypeRequested ) {

					sqlSB.append( SQL_DYNAMIC_MOD_JOIN_START_LINK_TYPES );

					if ( crosslinkTypeRequested ) {

						sqlSB.append( SQL_DYNAMIC_MOD_LINK_TYPE_START );
						sqlSB.append( XLinkUtils.CROSS_TYPE_STRING );
						sqlSB.append( SQL_DYNAMIC_MOD_LINK_TYPE_END );
					}

					if ( looplinkTypeRequested ) {

						if ( crosslinkTypeRequested ) {

							sqlSB.append( " OR " );
						}

						sqlSB.append( SQL_DYNAMIC_MOD_LINK_TYPE_START );
						sqlSB.append( XLinkUtils.LOOP_TYPE_STRING );
						sqlSB.append( SQL_DYNAMIC_MOD_LINK_TYPE_END );
					}

					sqlSB.append( SQL_DYNAMIC_MOD_JOIN_AFTER_LINK_TYPES );
				}		
			}

			sqlSB.append( SQL_DYNAMIC_MOD_JOIN_END );
		}

		
		sqlSB.append( SQL_MAIN_WHERE_START );

		//  Process link types
		
		if ( linkTypes != null && ( ! linkTypes.isEmpty() ) ) {

			sqlSB.append( " AND ( " );

			boolean firstLinkType = true;
			
			for ( String linkType : linkTypes ) {
				
				if ( firstLinkType ) {
					
					firstLinkType = false;
				} else {
					
					sqlSB.append( " OR " );
				}

				if ( PeptideViewLinkTypesConstants.CROSSLINK_PSM.equals( linkType ) ) {
					
					sqlSB.append( SQL_LINK_TYPE_START );
					sqlSB.append( XLinkUtils.CROSS_TYPE_STRING );
					sqlSB.append( SQL_LINK_TYPE_END );

				} else if ( PeptideViewLinkTypesConstants.LOOPLINK_PSM.equals( linkType ) ) {

					sqlSB.append( SQL_LINK_TYPE_START );
					sqlSB.append( XLinkUtils.LOOP_TYPE_STRING );
					sqlSB.append( SQL_LINK_TYPE_END );

				} else if ( PeptideViewLinkTypesConstants.MONOLINK_PSM.equals( linkType ) ) {

					sqlSB.append( SQL_MONOLINK_INCLUDE );

				} else if ( PeptideViewLinkTypesConstants.NO_LINK_PSM.equals( linkType ) ) {

					sqlSB.append( SQL_NO_LINKS_INCLUDE );

				} else {

					String msg = "linkType is invalid, linkType: " + linkType;

					log.error( linkType );

					throw new Exception( msg );
				}
			}

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

		
		
		
		sqlSB.append( SQL_LAST_PART );
		
		
		
		////////   SQL created.
		
		String sql = sqlSB.toString();
		
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			Collection<Integer> searchIds = new HashSet<Integer>();
			for( SearchDTO search : searches )
				searchIds.add( search.getId() );
			
			sql = sql.replaceAll( "#SEARCHES#", StringUtils.join( searchIds, "," ) );
			
			pstmt = conn.prepareStatement( sql );
			
			
			int paramCounter = 0;
			
			if ( modMassSelectionsIncludesYesModifications ) {
			
				paramCounter++;
				pstmt.setDouble( paramCounter, psmQValueCutoff );
			}
			
			paramCounter++;
			pstmt.setDouble( paramCounter, peptideQValueCutoff );
			
			paramCounter++;
			pstmt.setDouble( paramCounter, psmQValueCutoff );

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
				
				item.setPsmQValueCutoff( psmQValueCutoff );
				item.setPeptideQValueCutoff( peptideQValueCutoff );
				

				int numPsmsForpt01Cutoff = rs.getInt( "psm_num_at_pt_01_q_cutoff" );
				
				if ( DefaultQValueCutoffConstants.PSM_Q_VALUE_CUTOFF_DEFAULT == psmQValueCutoff ) {
					
					item.setNumPsms( numPsmsForpt01Cutoff ); // code is needed in WebMergedReportedPeptide for when psmCutoff is not default
				}
				
				Double peptideBestQValue = rs.getDouble( "min_q_value" );
				if ( rs.wasNull() ) {
					peptideBestQValue = null;
				}
				
				item.setBestPeptideQValue( peptideBestQValue );
				

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
			
			String msg = "Exception in search( Collection<SearchDTO> searches, double psmCutoff, double peptideCutoff, linkTypes ), sql: " + sql;
			
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
