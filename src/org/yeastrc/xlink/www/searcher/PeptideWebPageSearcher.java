package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.base.constants.Database_OneTrueZeroFalse_Constants;
import org.yeastrc.xlink.dao.ReportedPeptideDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.constants.DefaultQValueCutoffConstants;
import org.yeastrc.xlink.www.constants.DynamicModificationsSelectionConstants;
import org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants;
import org.yeastrc.xlink.www.objects.SearchPeptideDimer;
import org.yeastrc.xlink.www.objects.SearchPeptideLooplink;
import org.yeastrc.xlink.www.objects.SearchPeptideUnlink;
import org.yeastrc.xlink.www.objects.WebReportedPeptide;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.objects.SearchPeptideCrosslink;

/**
 * 
 *
 */
public class PeptideWebPageSearcher {
	
	private static final Logger log = Logger.getLogger(PeptideWebPageSearcher.class);

	private PeptideWebPageSearcher() { }
	private static final PeptideWebPageSearcher _INSTANCE = new PeptideWebPageSearcher();
	public static PeptideWebPageSearcher getInstance() { return _INSTANCE; }
	

	private final String SQL_FIRST_PART = 
			
			"SELECT unified_rep_pep__reported_peptide__search_lookup.reported_peptide_id, "
			
			+ " unified_rep_pep__reported_peptide__search_lookup.peptide_q_value_for_search, "
			
			+ " unified_rep_pep__reported_peptide__search_lookup.best_psm_q_value,"
			+ " unified_rep_pep__reported_peptide__search_lookup.link_type, "
			+ " unified_rep_pep__reported_peptide__search_lookup.psm_num_at_pt_01_q_cutoff "
			
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
			
		  " GROUP BY unified_rep_pep__reported_peptide__search_lookup.reported_peptide_id "
		
		+ " ORDER BY peptide_q_value_for_search, best_psm_q_value, unified_rep_pep__reported_peptide__search_lookup.reported_peptide_id ";

	
	///////////////////
	
	//  Additional SQL parts

	private static final String SQL_LINK_TYPE_START = "  unified_rep_pep__reported_peptide__search_lookup.link_type IN ( ";
	private static final String SQL_LINK_TYPE_END = " ) ";



	
	
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
	
	private static final String SQL_DYNAMIC_MOD_LINK_TYPE_START = "  search__reported_peptide__dynamic_mod_lookup.link_type IN ( ";
	private static final String SQL_DYNAMIC_MOD_LINK_TYPE_END = " ) ";


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
	public List<WebReportedPeptide> searchOnSearchIdPsmCutoffPeptideCutoff( 
			SearchDTO search, 
			double psmQValueCutoff, 
			double peptideQValueCutoff,
			List<String> linkTypes, 
			String[] modMassSelections ) throws Exception {

		List<WebReportedPeptide> links = new ArrayList<WebReportedPeptide>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		
		int searchId = search.getId();

		
		StringBuilder sqlSB = new StringBuilder( 1000 );
		
		

		
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
		boolean unlinkedTypeRequested = false;
		
		
		if ( linkTypes != null && ( ! linkTypes.isEmpty() ) ) {

			for ( String linkType : linkTypes ) {

				if ( PeptideViewLinkTypesConstants.CROSSLINK_PSM.equals( linkType ) ) {
					
					crosslinkTypeRequested = true;

				} else if ( PeptideViewLinkTypesConstants.LOOPLINK_PSM.equals( linkType ) ) {

					looplinkTypeRequested = true;

				} else if ( PeptideViewLinkTypesConstants.UNLINKED_PSM.equals( linkType ) ) {

					unlinkedTypeRequested = true;

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
			
			if ( linkTypes != null && ( ! linkTypes.isEmpty() ) ) {

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
		
		
		sqlSB.append( SQL_MAIN_WHERE_START );

		//  Process link types
		
		if ( linkTypes != null && ( ! linkTypes.isEmpty() ) ) {

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

		
		
		
		sqlSB.append( SQL_LAST_PART );
		
		
		
		
		String sql = sqlSB.toString();
		
		sql = sql.replaceAll( "#SEARCHES#", Integer.toString( searchId ) );

		
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
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
				
				links.add( item );
			}
			
		} catch ( Exception e ) {
			
			String msg = "Exception in search( SearchDTO search, double psmCutoff, double peptideCutoff, linkTypes ), sql: " + sql;
			
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



	
		
}
