package org.yeastrc.xlink.www.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.SearchTaxonomySearcher;
import org.yeastrc.xlink.www.annotation.sort_display_records_on_annotation_values.SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchId;
import org.yeastrc.xlink.www.annotation.sort_display_records_on_annotation_values.SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchIdReslt;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeData;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesAnnotationLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.www.linked_positions.CrosslinkLinkedPositions;
import org.yeastrc.xlink.www.linked_positions.LooplinkLinkedPositions;
import org.yeastrc.xlink.www.nav_links_image_structure.PopulateRequestDataForImageAndStructureNavLinks;
import org.yeastrc.xlink.www.no_data_validation.ThrowExceptionOnNoDataConfig;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.SearchProtein;
import org.yeastrc.xlink.www.objects.SearchProteinCrosslink;
import org.yeastrc.xlink.www.objects.SearchProteinCrosslinkWrapper;
import org.yeastrc.xlink.www.objects.SearchProteinLooplink;
import org.yeastrc.xlink.www.objects.SearchProteinLooplinkWrapper;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.Struts_Config_Parameter_Values_Constants;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.exceptions.ProxlWebappNoDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesSearchLevel;
import org.yeastrc.xlink.www.form_query_json_objects.PeptideQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.ProteinQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_PerSearchResult;
import org.yeastrc.xlink.www.form_utils.GetProteinQueryJSONRootFromFormData;
import org.yeastrc.xlink.www.forms.SearchViewProteinsForm;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.web_utils.ExcludeOnTaxonomyForProteinSequenceIdSearchId;
import org.yeastrc.xlink.www.web_utils.GetAnnotationDisplayUserSelectionDetailsData;
import org.yeastrc.xlink.www.web_utils.GetPageHeaderData;
import org.yeastrc.xlink.www.web_utils.GetSearchDetailsData;
import org.yeastrc.xlink.www.web_utils.ProteinListingTooltipConfigUtil;
import org.yeastrc.xlink.www.web_utils.URLEncodeDecodeAURL;
import org.yeastrc.xlink.www.web_utils.XLinkWebAppUtils;
import org.yeastrc.xlink.www.webapp_timing.WebappTiming;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Used for Crosslink and Looplinks
 *
 */
public class ViewSearchProteinsAction extends Action {
	
	private static final Logger log = Logger.getLogger(ViewSearchProteinsAction.class);
	
	public ActionForward execute( ActionMapping mapping,
			  ActionForm actionForm,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {
		//  Detect which Struts action mapping was called by examining the value of the "parameter" attribute
		//     accessed by calling mapping.getParameter()
		String strutsActionMappingParameter = mapping.getParameter();
		WebappTiming webappTiming = null;
		if ( log.isDebugEnabled() ) {
			webappTiming = WebappTiming.getInstance( log );
			request.setAttribute( "webappTiming", webappTiming );
		}
		try {
			// our form
			SearchViewProteinsForm form = (SearchViewProteinsForm)actionForm;
			request.setAttribute( "searchViewCrosslinkProteinForm", form );
			int projectSearchId = form.getProjectSearchIdSingle();
			// Get the session first.  
//			HttpSession session = request.getSession();
			//   Get the project id for this search
			Collection<Integer> projectSearchIdsSet = new HashSet<>();
			projectSearchIdsSet.add( projectSearchId );
			List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsSet );
			if ( projectIdsFromSearchIds.isEmpty() ) {
				// should never happen
				String msg = "No project ids for search id: " + projectSearchId;
				log.error( msg );
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			}
			if ( projectIdsFromSearchIds.size() > 1 ) {
				//  Invalid request, searches across projects
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_SEARCHES_ACROSS_PROJECTS );
			}
			int projectId = projectIdsFromSearchIds.get( 0 );
			request.setAttribute( "projectId", projectId ); 
			request.setAttribute( "project_id", projectId );
			///////////////////////
			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request, response );
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				return mapping.findForward( StrutsGlobalForwardNames.NO_USER_SESSION );
			}
			//  Test access to the project id
			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();
			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {
				//  No Access Allowed for this project id
				return mapping.findForward( StrutsGlobalForwardNames.INSUFFICIENT_ACCESS_PRIVILEGE );
			}
			request.setAttribute( WebConstants.REQUEST_AUTH_ACCESS_LEVEL, authAccessLevel );
			if ( webappTiming != null ) {
				webappTiming.markPoint( "After Auth" );
			}
			
			///    Done Processing Auth Check and Auth Level

			request.setAttribute( "projectSearchId", projectSearchId );
			
			//////////////////////////////
			//  Jackson JSON Mapper object for JSON deserialization and serialization
			ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object
			//  Populate request objects for Standard Header Display
			GetPageHeaderData.getInstance().getPageHeaderDataWithProjectId( projectId, request );
			//  Populate request objects for Protein Name Tooltip JS
			ProteinListingTooltipConfigUtil.getInstance().putProteinListingTooltipConfigForPage( projectSearchIdsSet, request );
			request.setAttribute( "queryString",  request.getQueryString() );
			
			SearchDTO search = SearchDAO.getInstance().getSearchFromProjectSearchId( projectSearchId );
			if ( search == null ) {
				String msg = ": No searchId found for projectSearchId: " + projectSearchId;
				log.warn( msg );
				throw new ProxlWebappDataException( msg );
			}
			request.setAttribute( "search", search );
			int searchId = search.getSearchId();
			Collection<Integer> searchIdsSet = new HashSet<>();
			searchIdsSet.add( searchId );
			Map<Integer,Integer> mapProjectSearchIdToSearchId = new HashMap<>();
			mapProjectSearchIdToSearchId.put( projectSearchId, searchId );
			
			// build list of taxonomies to show in exclusion list
			request.setAttribute("taxonomies", SearchTaxonomySearcher.getInstance().getTaxonomies( search ) );
			if ( webappTiming != null ) {
				webappTiming.markPoint( "After Taxonomy Searcher:  SearchTaxonomySearcher.getInstance().getTaxonomies( search )" );
			}
			//   Get Query JSON from the form and if not empty, deserialize it
			ProteinQueryJSONRoot proteinQueryJSONRoot = 
					GetProteinQueryJSONRootFromFormData.getInstance()
					.getProteinQueryJSONRootFromFormData( form, projectSearchIdsSet, searchIdsSet, mapProjectSearchIdToSearchId );
			////////////
			//  Copy Exclude Taxonomy and Exclude Protein Sets for lookup
			Set<Integer> excludeTaxonomy_Ids_Set_UserInput = new HashSet<>();
			Set<Integer> excludeProteinSequenceIds_Set_UserInput = new HashSet<>();
			if ( proteinQueryJSONRoot.getExcludeTaxonomy() != null ) {
				for ( Integer taxonomyId : proteinQueryJSONRoot.getExcludeTaxonomy() ) {
					excludeTaxonomy_Ids_Set_UserInput.add( taxonomyId );
				}
			}
			//  First convert the protein sequence ids that come from the JS code to standard integers and put
			//   in the property excludeProteinSequenceIds
			ProteinsMergedProteinsCommon.getInstance().processExcludeProteinSequenceIdsFromJS( proteinQueryJSONRoot );
			if ( proteinQueryJSONRoot.getExcludeProteinSequenceIds() != null ) {
				for ( Integer proteinId : proteinQueryJSONRoot.getExcludeProteinSequenceIds() ) {
					excludeProteinSequenceIds_Set_UserInput.add( proteinId );
				}
			}
			/////////////////////////////////////////////////////////////////////////////
			/////////////////////////////////////////////////////////////////////////////
			////////   Generic Param processing
			//  Get Annotation Type records for PSM and Peptide
			//  Get  Annotation Type records for PSM
			Map<Integer, Map<Integer, AnnotationTypeDTO>> 
			srchPgm_Filterable_Psm_AnnotationType_DTOListPerSearchIdMap =
					GetAnnotationTypeData.getInstance().getAll_Psm_Filterable_ForSearchIds( searchIdsSet );
			Map<Integer, AnnotationTypeDTO> srchPgm_Filterable_Psm_AnnotationType_DTOMap = 
					srchPgm_Filterable_Psm_AnnotationType_DTOListPerSearchIdMap.get( searchId );
			if ( srchPgm_Filterable_Psm_AnnotationType_DTOMap == null ) {
				//  No records were found, probably an error   TODO
				srchPgm_Filterable_Psm_AnnotationType_DTOMap = new HashMap<>();
			}
			//  Get  Annotation Type records for Reported Peptides
			Map<Integer, Map<Integer, AnnotationTypeDTO>> 
			srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOListPerSearchIdMap =
					GetAnnotationTypeData.getInstance().getAll_Peptide_Filterable_ForSearchIds( searchIdsSet );
			Map<Integer, AnnotationTypeDTO> srchPgmFilterableReportedPeptideAnnotationTypeDTOMap = 
					srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOListPerSearchIdMap.get( searchId );
			if ( srchPgmFilterableReportedPeptideAnnotationTypeDTOMap == null ) {
				//  No records were found, allowable for Reported Peptides
				srchPgmFilterableReportedPeptideAnnotationTypeDTOMap = new HashMap<>();
			}
			//  Populate request objects for Standard Search Display
			GetSearchDetailsData.getInstance().getSearchDetailsData( search, request );
			//  Populate request objects for User Selection of Annotation Data Display
			GetAnnotationDisplayUserSelectionDetailsData.getInstance().getSearchDetailsData( search, request );
			String projectSearchIdAsString = Integer.toString( projectSearchId );
			CutoffValuesSearchLevel cutoffValuesSearchLevel = proteinQueryJSONRoot.getCutoffs().getSearches().get( projectSearchIdAsString );
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = null;
			if ( cutoffValuesSearchLevel == null ) {
				//  Create empty object for default values
				searcherCutoffValuesSearchLevel = new SearcherCutoffValuesSearchLevel();
			} else {
				Z_CutoffValuesObjectsToOtherObjects_PerSearchResult z_CutoffValuesObjectsToOtherObjects_PerSearchResult = 
						Z_CutoffValuesObjectsToOtherObjectsFactory.createSearcherCutoffValuesSearchLevel( searchIdsSet, cutoffValuesSearchLevel );
				searcherCutoffValuesSearchLevel = z_CutoffValuesObjectsToOtherObjects_PerSearchResult.getSearcherCutoffValuesSearchLevel();
				if ( searcherCutoffValuesSearchLevel == null ) {
					//  Create empty object for default values
					searcherCutoffValuesSearchLevel = new SearcherCutoffValuesSearchLevel();
				}
			}
			////////////////////////////////////////////////
			//////     Code common to Crosslinks and Looplinks
			//  Create list of Peptide and Best PSM annotation names to display as column headers
			List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesList = 
					searcherCutoffValuesSearchLevel.getPeptidePerAnnotationCutoffsList();
			List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList = 
					searcherCutoffValuesSearchLevel.getPsmPerAnnotationCutoffsList();
			List<AnnotationTypeDTO> peptideCutoffsAnnotationTypeDTOList = new ArrayList<>( peptideCutoffValuesList.size() );
			List<AnnotationTypeDTO> psmCutoffsAnnotationTypeDTOList = new ArrayList<>( psmCutoffValuesList.size() );
			for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesAnnotationLevel : peptideCutoffValuesList ) {
				peptideCutoffsAnnotationTypeDTOList.add( searcherCutoffValuesAnnotationLevel.getAnnotationTypeDTO() );
			}
			for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesAnnotationLevel : psmCutoffValuesList ) {
				psmCutoffsAnnotationTypeDTOList.add( searcherCutoffValuesAnnotationLevel.getAnnotationTypeDTO() );
			}
			/////////////////////////////////////////////////////////////
			//////////////////   Get Crosslinks data from DATABASE  from database
			List<SearchProteinCrosslinkWrapper> wrappedCrosslinks = 
					CrosslinkLinkedPositions.getInstance()
					.getSearchProteinCrosslinkWrapperList( search, searcherCutoffValuesSearchLevel );
			if ( webappTiming != null ) {
				webappTiming.markPoint( "After Crosslink Searcher:  SearchProteinCrosslinkSearcher.getInstance().search( ... )" );
			}
			//  If configured, throw exception if no peptides found
			if ( ThrowExceptionOnNoDataConfig.getInstance().isThrowExceptionNoData() ) {
				if ( wrappedCrosslinks.isEmpty() ) {
					String msg = "No Crosslinks found and config set for ThrowExceptionNoData";
					log.error( msg );
					throw new ProxlWebappNoDataException( msg );
				}
			}
			/////////////////////////////////////////////////////////////
			//////////////////   Get Looplinks data from DATABASE   from database
			List<SearchProteinLooplinkWrapper> wrappedLooplinks = 
					LooplinkLinkedPositions.getInstance()
					.getSearchProteinLooplinkWrapperList( search, searcherCutoffValuesSearchLevel );
			if ( webappTiming != null ) {
				webappTiming.markPoint( "After Looplink Searcher:  SearchProteinLooplinkSearcher.getInstance().search( ... )" );
			}
			//  If configured, throw exception if no peptides found
			if ( ThrowExceptionOnNoDataConfig.getInstance().isThrowExceptionNoData() ) {
				if ( wrappedLooplinks.isEmpty() ) {
					String msg = "No Looplinks found and config set for ThrowExceptionNoData";
					log.error( msg );
					throw new ProxlWebappNoDataException( msg );
				}
			}
			// all possible proteins included in this search for this type
			Collection<SearchProtein> prProteins = new HashSet<SearchProtein>();
			for ( SearchProteinCrosslinkWrapper  searchProteinCrosslinkWrapper : wrappedCrosslinks ) {
				SearchProteinCrosslink searchProteinCrosslink = searchProteinCrosslinkWrapper.getSearchProteinCrosslink();
				prProteins.add( searchProteinCrosslink.getProtein1() );
				prProteins.add( searchProteinCrosslink.getProtein2() );
			}
			for ( SearchProteinLooplinkWrapper searchProteinLooplinkWrapper : wrappedLooplinks ) {
				SearchProteinLooplink searchProteinLooplink = searchProteinLooplinkWrapper.getSearchProteinLooplink();
				prProteins.add( searchProteinLooplink.getProtein() );
			}
			//////////////////////////////////////////////////////////////////
			// Filter out links if requested
			if( proteinQueryJSONRoot.isFilterNonUniquePeptides() 
					|| proteinQueryJSONRoot.isFilterOnlyOnePSM() 
					|| proteinQueryJSONRoot.isFilterOnlyOnePeptide()
					|| ( proteinQueryJSONRoot.getExcludeTaxonomy() != null && proteinQueryJSONRoot.getExcludeTaxonomy().length > 0 ) ||
					( ! excludeProteinSequenceIds_Set_UserInput.isEmpty() ) ) {
				///////  Output Lists, Results After Filtering
				List<SearchProteinCrosslinkWrapper> wrappedCrosslinksAfterFilter = new ArrayList<>( wrappedLooplinks.size() );
				List<SearchProteinLooplinkWrapper> wrappedLooplinksAfterFilter = new ArrayList<>( wrappedLooplinks.size() );
				///  Filter CROSSLINKS
				for ( SearchProteinCrosslinkWrapper searchProteinCrosslinkWrapper : wrappedCrosslinks ) {
					SearchProteinCrosslink link = searchProteinCrosslinkWrapper.getSearchProteinCrosslink();
					// did they request to removal of non unique peptides?
					if( proteinQueryJSONRoot.isFilterNonUniquePeptides()  ) {
						if( link.getNumUniqueLinkedPeptides() < 1 ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}
					// did they request to removal of links with only one PSM?
					if( proteinQueryJSONRoot.isFilterOnlyOnePSM()  ) {
						int psmCountForSearchId = link.getNumPsms();
						if ( psmCountForSearchId <= 1 ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}
					// did they request to removal of links with only one Reported Peptide?
					if( proteinQueryJSONRoot.isFilterOnlyOnePeptide() ) {
						int peptideCountForSearchId = link.getNumLinkedPeptides();
						if ( peptideCountForSearchId <= 1 ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}
					// did user request removal of certain taxonomy IDs?
					if( ! excludeTaxonomy_Ids_Set_UserInput.isEmpty() ) {
						boolean excludeOnProtein_1 =
								ExcludeOnTaxonomyForProteinSequenceIdSearchId.getInstance()
								.excludeOnTaxonomyForProteinSequenceIdSearchId( 
										excludeTaxonomy_Ids_Set_UserInput, 
										link.getProtein1().getProteinSequenceObject(), 
										searchId );
						boolean excludeOnProtein_2 =
								ExcludeOnTaxonomyForProteinSequenceIdSearchId.getInstance()
								.excludeOnTaxonomyForProteinSequenceIdSearchId( 
										excludeTaxonomy_Ids_Set_UserInput, 
										link.getProtein2().getProteinSequenceObject(), 
										searchId );
						if ( excludeOnProtein_1 || excludeOnProtein_2 ) {
//						int taxonomyId_1 = link.getProtein1().getProteinSequenceObject().getTaxonomyId();
//						int taxonomyId_2 = link.getProtein2().getProteinSequenceObject().getTaxonomyId();
//						
//						if ( excludeTaxonomy_Ids_Set_UserInput.contains( taxonomyId_1 ) 
//								|| excludeTaxonomy_Ids_Set_UserInput.contains( taxonomyId_2 ) ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}
					// did user request removal of certain protein IDs?
					if( ! excludeProteinSequenceIds_Set_UserInput.isEmpty() ) {
						int proteinId_1 = link.getProtein1().getProteinSequenceObject().getProteinSequenceId();
						int proteinId_2 = link.getProtein2().getProteinSequenceObject().getProteinSequenceId();
						if ( excludeProteinSequenceIds_Set_UserInput.contains( proteinId_1 ) 
								|| excludeProteinSequenceIds_Set_UserInput.contains( proteinId_2 ) ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}		
					wrappedCrosslinksAfterFilter.add( searchProteinCrosslinkWrapper );
				}
				///  Filter LOOPLINKS
				for ( SearchProteinLooplinkWrapper searchProteinLooplinkWrapper : wrappedLooplinks ) {
					SearchProteinLooplink link = searchProteinLooplinkWrapper.getSearchProteinLooplink();
					// did they request to removal of non unique peptides?
					if( proteinQueryJSONRoot.isFilterNonUniquePeptides()  ) {
						if( link.getNumUniquePeptides() < 1 ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}
					// did they request to removal of links with only one PSM?
					if( proteinQueryJSONRoot.isFilterOnlyOnePSM()  ) {
						int psmCountForSearchId = link.getNumPsms();
						if ( psmCountForSearchId <= 1 ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}
					// did they request to removal of links with only one Reported Peptide?
					if( proteinQueryJSONRoot.isFilterOnlyOnePeptide() ) {
						int peptideCountForSearchId = link.getNumPeptides();
						if ( peptideCountForSearchId <= 1 ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}
					// did user request removal of certain taxonomy IDs?
					if( ! excludeTaxonomy_Ids_Set_UserInput.isEmpty() ) {
						boolean excludeOnProtein =
								ExcludeOnTaxonomyForProteinSequenceIdSearchId.getInstance()
								.excludeOnTaxonomyForProteinSequenceIdSearchId( 
										excludeTaxonomy_Ids_Set_UserInput, 
										link.getProtein().getProteinSequenceObject(), 
										searchId );
						if ( excludeOnProtein ) {
//						int taxonomyId = link.getProtein().getProteinSequenceObject().getTaxonomyId();
//						
//						if ( excludeTaxonomy_Ids_Set_UserInput.contains( taxonomyId ) ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}
					// did user request removal of certain protein IDs?
					if( ! excludeProteinSequenceIds_Set_UserInput.isEmpty() ) {
						int proteinId = link.getProtein().getProteinSequenceObject().getProteinSequenceId();
						if ( excludeProteinSequenceIds_Set_UserInput.contains( proteinId ) ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}									
					wrappedLooplinksAfterFilter.add( searchProteinLooplinkWrapper );
				}
				//  Copy new filtered lists to original input variable names to overlay them
				wrappedCrosslinks = wrappedCrosslinksAfterFilter;
				wrappedLooplinks = wrappedLooplinksAfterFilter;
			}
			//////////////////////////////////////////////////////////////////
			///////////////////////////
			///   Process Crosslinks and Looplinks to get annotations and sort.
			//              Process Crosslinks or Looplinks, depending on which page is being displayed
			List<SearchProteinCrosslink> crosslinks = null;
			if ( ! Struts_Config_Parameter_Values_Constants.STRUTS__PARAMETER__CROSSLINK.equals( strutsActionMappingParameter ) ) {
				//  NOT Crosslink page:    Struts config action mapping:    NOT parameter="crosslink"
				//  Simply unwrap the crosslinks  
				crosslinks = new ArrayList<>( wrappedCrosslinks.size() );
				for ( SearchProteinCrosslinkWrapper searchProteinCrosslinkWrapper : wrappedCrosslinks ) {
					crosslinks.add( searchProteinCrosslinkWrapper.getSearchProteinCrosslink() );
				}
			} else {
				//	For  Struts config action mapping:     parameter="crosslink"
				//  Order so:  ( protein_1 name < protein_2 name) or ( protein_1 name == protein_2 name and pos1 <= pos2 )
				for ( SearchProteinCrosslinkWrapper searchProteinCrosslinkWrapper : wrappedCrosslinks ) {
					SearchProteinCrosslink searchProteinCrosslink = searchProteinCrosslinkWrapper.getSearchProteinCrosslink();
					int protein1CompareProtein2 = searchProteinCrosslink.getProtein1().getName().compareToIgnoreCase( searchProteinCrosslink.getProtein2().getName() );
					if ( protein1CompareProtein2 > 0 
							|| ( protein1CompareProtein2 == 0
								&& searchProteinCrosslink.getProtein1Position() > searchProteinCrosslink.getProtein2Position() ) ) {
						//  Protein_1 name > protein_2 name or ( Protein_1 name == protein_2 name and pos1 > pos2 )
						//  so re-order
						SearchProtein searchProteinTemp = searchProteinCrosslink.getProtein1();
						searchProteinCrosslink.setProtein1( searchProteinCrosslink.getProtein2() );
						searchProteinCrosslink.setProtein2( searchProteinTemp );
						int linkPositionTemp = searchProteinCrosslink.getProtein1Position();
						searchProteinCrosslink.setProtein1Position( searchProteinCrosslink.getProtein2Position() );
						searchProteinCrosslink.setProtein2Position( linkPositionTemp );
					}
				}
				//  Sort "wrappedCrosslinks" since removed ORDER BY from SQL
				Collections.sort( wrappedCrosslinks, new Comparator<SearchProteinCrosslinkWrapper>() {
					@Override
					public int compare(SearchProteinCrosslinkWrapper o1,
							SearchProteinCrosslinkWrapper o2) {
						if ( o1.getSearchProteinCrosslink().getProtein1().getProteinSequenceObject().getProteinSequenceId()
								!= o2.getSearchProteinCrosslink().getProtein1().getProteinSequenceObject().getProteinSequenceId() ) {
							return o1.getSearchProteinCrosslink().getProtein1().getProteinSequenceObject().getProteinSequenceId() 
									- o2.getSearchProteinCrosslink().getProtein1().getProteinSequenceObject().getProteinSequenceId();
						}
						if ( o1.getSearchProteinCrosslink().getProtein2().getProteinSequenceObject().getProteinSequenceId()
								!= o2.getSearchProteinCrosslink().getProtein2().getProteinSequenceObject().getProteinSequenceId() ) {
							return o1.getSearchProteinCrosslink().getProtein2().getProteinSequenceObject().getProteinSequenceId() 
									- o2.getSearchProteinCrosslink().getProtein2().getProteinSequenceObject().getProteinSequenceId();
						}
						if ( o1.getSearchProteinCrosslink().getProtein1Position()
								!= o2.getSearchProteinCrosslink().getProtein1Position() ) {
							return o1.getSearchProteinCrosslink().getProtein1Position() 
									- o2.getSearchProteinCrosslink().getProtein1Position();
						}
						return o1.getSearchProteinCrosslink().getProtein2Position() 
								- o2.getSearchProteinCrosslink().getProtein2Position();
					}
				} );
				////   Crosslinks  - Prepare for Web display
				//      Get Annotation data and Sort by Annotation data
				SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchIdReslt sortCrosslinksResult =
						SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchId.getInstance()
						.sortOnBestPeptideBestPSMAnnValuesPopulateAnnValueListsReturnTableHeadersSingleSearchId(
								searchId, 
								wrappedCrosslinks, 
								peptideCutoffsAnnotationTypeDTOList, 
								psmCutoffsAnnotationTypeDTOList );
				request.setAttribute( "peptideAnnotationDisplayNameDescriptionList", sortCrosslinksResult.getPeptideAnnotationDisplayNameDescriptionList() );
				request.setAttribute( "psmAnnotationDisplayNameDescriptionList", sortCrosslinksResult.getPsmAnnotationDisplayNameDescriptionList() );
				crosslinks = new ArrayList<>( wrappedCrosslinks.size() );
				//  Copy of out wrapper for processing below
				for ( SearchProteinCrosslinkWrapper searchProteinCrosslinkWrapper : wrappedCrosslinks ) {
					SearchProteinCrosslink searchProteinCrosslink = searchProteinCrosslinkWrapper.getSearchProteinCrosslink();
					crosslinks.add( searchProteinCrosslink );
				}
			}
			/////////////////////////////////////////////////////////////
			//////////////////   Get Looplinks data
			List<SearchProteinLooplink> looplinks = null;
			if ( ! Struts_Config_Parameter_Values_Constants.STRUTS__PARAMETER__LOOPLINK.equals( strutsActionMappingParameter ) ) {
				//  NOT Looplink page:    Struts config action mapping:    NOT parameter="looplink"
				//  Simply unwrap the looplinks  
				looplinks = new ArrayList<>( wrappedLooplinks.size() );
				for ( SearchProteinLooplinkWrapper searchProteinLooplinkWrapper : wrappedLooplinks ) {
					looplinks.add( searchProteinLooplinkWrapper.getSearchProteinLooplink() );
				}
			} else {
				//	For  Struts config action mapping:     parameter="looplink"
				////   Looplinks  - Prepare for Web display
				//      Get Annotation data and Sort by Annotation data
				looplinks = new ArrayList<>( wrappedLooplinks.size() );
				Collections.sort( wrappedLooplinks, new Comparator<SearchProteinLooplinkWrapper>() {
					@Override
					public int compare(SearchProteinLooplinkWrapper o1,
							SearchProteinLooplinkWrapper o2) {
						if ( o1.getSearchProteinLooplink().getProtein().getProteinSequenceObject().getProteinSequenceId()
								!= o2.getSearchProteinLooplink().getProtein().getProteinSequenceObject().getProteinSequenceId() ) {
							return o1.getSearchProteinLooplink().getProtein().getProteinSequenceObject().getProteinSequenceId() 
									- o2.getSearchProteinLooplink().getProtein().getProteinSequenceObject().getProteinSequenceId();
						}
						if ( o1.getSearchProteinLooplink().getProteinPosition1()
								!= o2.getSearchProteinLooplink().getProteinPosition1() ) {
							return o1.getSearchProteinLooplink().getProteinPosition1() 
									- o2.getSearchProteinLooplink().getProteinPosition1();
						}
						return o1.getSearchProteinLooplink().getProteinPosition2() 
								- o2.getSearchProteinLooplink().getProteinPosition2();
					}
				} );
				//      Get Annotation data and Sort by Annotation data
				SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchIdReslt sortLooplinksResult =
						SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchId.getInstance()
						.sortOnBestPeptideBestPSMAnnValuesPopulateAnnValueListsReturnTableHeadersSingleSearchId(
								searchId, 
								wrappedLooplinks, 
								peptideCutoffsAnnotationTypeDTOList, 
								psmCutoffsAnnotationTypeDTOList );
				request.setAttribute( "peptideAnnotationDisplayNameDescriptionList", sortLooplinksResult.getPeptideAnnotationDisplayNameDescriptionList() );
				request.setAttribute( "psmAnnotationDisplayNameDescriptionList", sortLooplinksResult.getPsmAnnotationDisplayNameDescriptionList() );
				/////////////////////////////////
				//  Copy of out wrapper for output
				for ( SearchProteinLooplinkWrapper searchProteinLooplinkWrapper : wrappedLooplinks ) {
					SearchProteinLooplink searchProteinLooplink = searchProteinLooplinkWrapper.getSearchProteinLooplink();
					looplinks.add( searchProteinLooplink );
				}
			}			
			///////////////////////////////////////////////////////
			Collection<SearchProtein> prProteins2 = new HashSet<SearchProtein>();
			prProteins2.addAll( prProteins );
			// build a collection of protein IDs to include
			for( SearchProtein prp : prProteins2 ) {
				// did they request removal of certain taxonomy IDs?
				if( proteinQueryJSONRoot.getExcludeTaxonomy() != null && proteinQueryJSONRoot.getExcludeTaxonomy().length > 0 ) {
					boolean excludeOnProtein =
							ExcludeOnTaxonomyForProteinSequenceIdSearchId.getInstance()
							.excludeOnTaxonomyForProteinSequenceIdSearchId( 
									excludeTaxonomy_Ids_Set_UserInput, 
									prp.getProteinSequenceObject(), 
									searchId );
					if ( excludeOnProtein ) {
						prProteins.remove( prp );
					}
//					for( int taxonomyIdToExclude : proteinQueryJSONRoot.getExcludeTaxonomy() ) {
//						
//						if( taxonomyIdToExclude == prp.getProteinSequenceObject().getTaxonomyId() ) {
//							prProteins.remove( prp );
//							break;
//						}
//					}
				}
			}
			List<SearchProtein> sortedProteins = new ArrayList<SearchProtein>();
			sortedProteins.addAll( prProteins );
			Collections.sort( sortedProteins, new SortSearchProtein() );
			request.setAttribute( "proteins", sortedProteins );
			request.setAttribute( "numCrosslinks", crosslinks.size() );
			int numDistinctLinks =  XLinkWebAppUtils.getNumUDRs( crosslinks, looplinks );
			request.setAttribute( "numLooplinks", looplinks.size() );
			request.setAttribute( "numLinks", looplinks.size() + crosslinks.size() );
			request.setAttribute( "numDistinctLinks", numDistinctLinks );
			request.setAttribute( "crosslinks", crosslinks );
			request.setAttribute( "looplinks", looplinks );
			/////////////////////
			//  clear out form so value doesn't go back on the page in the form
			form.setQueryJSON( "" );
			/////////////////////
			////  Put Updated queryJSON on the page
			{
				try {
					String queryJSONToForm = jacksonJSON_Mapper.writeValueAsString( proteinQueryJSONRoot );
					//  Set queryJSON in request attribute to put on page outside of form
					request.setAttribute( "queryJSONToForm", queryJSONToForm );
				} catch ( JsonProcessingException e ) {
					String msg = "Failed to write as JSON 'queryJSONToForm', JsonProcessingException.   queryString" +  request.getQueryString();
					log.error( msg, e );
					throw new ProxlWebappDataException( msg, e );
				} catch ( Exception e ) {
					String msg = "Failed to write as JSON 'queryJSONToForm', Exception.   queryString" +  request.getQueryString();
					log.error( msg, e );
					throw new ProxlWebappDataException( msg, e );
				}
			}
			/////////////////////
			////  Put queryJSON for Peptide Link on the page
			{
				try {
					PeptideQueryJSONRoot peptideQueryJSONRoot = new PeptideQueryJSONRoot();
					peptideQueryJSONRoot.setCutoffs( proteinQueryJSONRoot.getCutoffs() );
					String[] peptidePageLinkTypes = new String[ 1 ];
					if ( Struts_Config_Parameter_Values_Constants.STRUTS__PARAMETER__CROSSLINK.equals( strutsActionMappingParameter ) ) {
						peptidePageLinkTypes[ 0 ] = XLinkUtils.CROSS_TYPE_STRING_UPPERCASE; 
					} else if ( Struts_Config_Parameter_Values_Constants.STRUTS__PARAMETER__LOOPLINK.equals( strutsActionMappingParameter ) ) {
						peptidePageLinkTypes[ 0 ] = XLinkUtils.LOOP_TYPE_STRING_UPPERCASE; 
					}
					peptideQueryJSONRoot.setLinkTypes( peptidePageLinkTypes );
					String peptideQueryJSONRootJSONString = jacksonJSON_Mapper.writeValueAsString( peptideQueryJSONRoot );
					//  Create URI Encoded JSON for passing to Image and Structure pages in hash 
					String peptideQueryJSONRootJSONStringURIEncoded = URLEncodeDecodeAURL.urlEncodeAURL( peptideQueryJSONRootJSONString );
					request.setAttribute( "peptidePageQueryJSON", peptideQueryJSONRootJSONStringURIEncoded );
				} catch ( JsonProcessingException e ) {
					String msg = "Failed to write as JSON 'queryJSONToForm', JsonProcessingException.   queryString" +  request.getQueryString();
					log.error( msg, e );
					throw new ProxlWebappDataException( msg, e );
				} catch ( Exception e ) {
					String msg = "Failed to write as JSON 'queryJSONToForm', Exception.   queryString" +  request.getQueryString();
					log.error( msg, e );
					throw new ProxlWebappDataException( msg, e );
				}
			}
			//  Create data for Links for Image and Structure pages and put in request
			PopulateRequestDataForImageAndStructureNavLinks.getInstance()
			.populateRequestDataForImageAndStructureNavLinksForProtein( proteinQueryJSONRoot, projectId, authAccessLevel, form, request );
			if ( webappTiming != null ) {
				webappTiming.markPoint( "Before send to JSP" );
			}
			return mapping.findForward( "Success" );
		} catch ( ProxlWebappDataException e ) {
			String msg = "Exception processing request data";
			log.error( msg, e );
			return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}
	
	//////////////////////////////////////////////////////////////
    /**
     * 
     *
     */
    public class SortSearchProtein implements Comparator<SearchProtein> {
        public int compare(SearchProtein o1, SearchProtein o2) {
            try { return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase()); }
            catch( Exception e ) { return 0; }
        }
    }
}
