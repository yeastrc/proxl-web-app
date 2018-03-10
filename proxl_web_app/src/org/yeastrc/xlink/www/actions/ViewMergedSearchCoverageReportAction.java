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
//import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.objects.ProteinSequenceVersionObject;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.linked_positions.CrosslinkLinkedPositions;
import org.yeastrc.xlink.www.linked_positions.LinkedPositions_FilterExcludeLinksWith_Param;
import org.yeastrc.xlink.www.linked_positions.LooplinkLinkedPositions;
import org.yeastrc.xlink.www.linked_positions.UnlinkedDimerPeptideProteinMapping;
import org.yeastrc.xlink.www.linked_positions.UnlinkedDimerPeptideProteinMapping.UnlinkedDimerPeptideProteinMappingResult;
import org.yeastrc.xlink.www.nav_links_image_structure.PopulateRequestDataForImageAndStructureAndQC_NavLinks;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.MergedSearchProtein;
import org.yeastrc.xlink.www.objects.SearchProtein;
import org.yeastrc.xlink.www.objects.SearchProteinCrosslink;
import org.yeastrc.xlink.www.objects.SearchProteinCrosslinkWrapper;
import org.yeastrc.xlink.www.objects.SearchProteinDimer;
import org.yeastrc.xlink.www.objects.SearchProteinDimerWrapper;
import org.yeastrc.xlink.www.objects.SearchProteinLooplink;
import org.yeastrc.xlink.www.objects.SearchProteinLooplinkWrapper;
import org.yeastrc.xlink.www.objects.SearchProteinUnlinked;
import org.yeastrc.xlink.www.objects.SearchProteinUnlinkedWrapper;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.Struts_Config_Parameter_Values_Constants;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.ProteinQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_RootResult;
import org.yeastrc.xlink.www.form_utils.GetProteinQueryJSONRootFromFormData;
import org.yeastrc.xlink.www.forms.MergedSearchViewProteinsForm;
import org.yeastrc.xlink.www.objects.ProteinCoverageData;
import org.yeastrc.xlink.www.protein_coverage.ProteinCoverageCompute;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.web_utils.AnyPDBFilesForProjectId;
import org.yeastrc.xlink.www.web_utils.ExcludeLinksWith_Remove_NonUniquePSMs_Checkbox_PopRequestItems;
import org.yeastrc.xlink.www.web_utils.ExcludeOnTaxonomyForProteinSequenceVersionIdSearchId;
import org.yeastrc.xlink.www.web_utils.GetAnnotationDisplayUserSelectionDetailsData;
import org.yeastrc.xlink.www.web_utils.GetPageHeaderData;
import org.yeastrc.xlink.www.web_utils.GetSearchDetailsData;
import org.yeastrc.xlink.www.web_utils.ProteinListingTooltipConfigUtil;
import org.yeastrc.xlink.www.web_utils.TaxonomiesForSearchOrSearches;
import org.yeastrc.xlink.www.web_utils.URLEncodeDecodeAURL;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 *
 */
public class ViewMergedSearchCoverageReportAction extends Action {
	
	private static final Logger log = Logger.getLogger(ViewMergedSearchCoverageReportAction.class);
	
	public ActionForward execute( ActionMapping mapping,
			  ActionForm actionForm,
			  HttpServletRequest request,
			  HttpServletResponse response ) throws Exception {
		
		//  Detect which Struts action mapping was called by examining the value of the "parameter" attribute
		//     accessed by calling mapping.getParameter()
		String strutsActionMappingParameter = mapping.getParameter();
		try {
			// our form
			MergedSearchViewProteinsForm form = (MergedSearchViewProteinsForm)actionForm;
			request.setAttribute( "mergedSearchViewCrosslinkProteinForm", form );
			request.setAttribute( "strutsActionForm", form );
			request.setAttribute( "queryString",  request.getQueryString() );
			if ( Struts_Config_Parameter_Values_Constants.STRUTS__PARAMETER__MERGED_PROTEIN_COVERAGE_PAGE.equals( strutsActionMappingParameter ) ) {
				request.setAttribute( "mergedPage", true );
			}
			// Get the session first.  
//			HttpSession session = request.getSession();
			int[] projectSearchIds = form.getProjectSearchId();
			if ( projectSearchIds == null || projectSearchIds.length == 0 ) {
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			}
			//   Get the project id for these searches
			Set<Integer> projectSearchIdsSet = new HashSet<Integer>( );
			for ( int projectSearchId : projectSearchIds ) {
				projectSearchIdsSet.add( projectSearchId );
			}
			List<Integer> projectSearchIdsListDeduppedSorted = new ArrayList<>( projectSearchIdsSet );
			Collections.sort( projectSearchIdsListDeduppedSorted );
			List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsSet );
			if ( projectIdsFromSearchIds.isEmpty() ) {
				// should never happen
				String msg = "No project ids for projectSearchIds: ";
				for ( int projectSearchId : projectSearchIds ) {
					msg += projectSearchId + ", ";
				}
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
			
			///    Done Processing Auth Check and Auth Level
			//////////////////////////////

			request.setAttribute( "projectSearchIds", projectSearchIdsListDeduppedSorted );
			
			//  Jackson JSON Mapper object for JSON deserialization and serialization
			ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object
			
			request.setAttribute( "searchIds", projectSearchIdsListDeduppedSorted );
			
			List<SearchDTO> searches = new ArrayList<SearchDTO>();
			Map<Integer, SearchDTO> searchesMapOnSearchId = new HashMap<>();

			Collection<Integer> searchIds = new HashSet<>();
			Map<Integer,Integer> mapProjectSearchIdToSearchId = new HashMap<>();
			
			int[] searchIdsArray = new int[ projectSearchIdsListDeduppedSorted.size() ];
			int searchIdsArrayIndex = 0;
			for( int projectSearchId : projectSearchIdsListDeduppedSorted ) {
				SearchDTO search = SearchDAO.getInstance().getSearchFromProjectSearchId( projectSearchId );
				if ( search == null ) {
					String msg = "search id '" + projectSearchId + "' not found in the database. User taken to home page.";
					log.warn( msg );
					//  Search not found, the data on the page they are requesting does not exist.
					//  The data on the user's previous page no longer reflects what is in the database.
					//  Take the user to the home page
					return mapping.findForward( StrutsGlobalForwardNames.HOME );  //  EARLY EXIT from Method
				}
				Integer searchId = search.getSearchId();
				searches.add( search );
				searchesMapOnSearchId.put( searchId, search );
				searchIds.add( searchId );
				mapProjectSearchIdToSearchId.put( search.getProjectSearchId(), searchId );
				searchIdsArray[ searchIdsArrayIndex ] = searchId;
				searchIdsArrayIndex++;
			}
			// Sort searches list
			Collections.sort( searches, new Comparator<SearchDTO>() {
				@Override
				public int compare(SearchDTO o1, SearchDTO o2) {
					return o1.getSearchId() - o2.getSearchId();
				}
			});
			
			//  Populate request objects for Standard Header Display
			GetPageHeaderData.getInstance().getPageHeaderDataWithProjectId( projectId, request );
			//  Populate request objects for Protein Name Tooltip JS
			ProteinListingTooltipConfigUtil.getInstance().putProteinListingTooltipConfigForPage( projectSearchIdsSet, request );
			//  Populate request objects for Standard Search Display
			GetSearchDetailsData.getInstance().getSearchDetailsData( searches, request );
			//  Populate request objects for User Selection of Annotation Data Display
			GetAnnotationDisplayUserSelectionDetailsData.getInstance().getSearchDetailsData( searches, request );
			//  Populate request objects for excludeLinksWith_Remove_NonUniquePSMs_Checkbox_Fragment.jsp
			ExcludeLinksWith_Remove_NonUniquePSMs_Checkbox_PopRequestItems.getInstance().excludeLinksWith_Remove_NonUniquePSMs_Checkbox_PopRequestItems( searches, request );

			boolean showStructureLink = true;
			if ( authAccessLevel.isAssistantProjectOwnerAllowed()
					|| authAccessLevel.isAssistantProjectOwnerIfProjectNotLockedAllowed() ) {
			} else {
				//  Public access user:
				showStructureLink = AnyPDBFilesForProjectId.getInstance().anyPDBFilesForProjectId( projectId );
			}
			request.setAttribute( WebConstants.REQUEST_SHOW_STRUCTURE_LINK, showStructureLink );
			// Set values for general page functionality
			request.setAttribute( "queryString", request.getQueryString() );
			request.setAttribute( "searches", searches );

			//   Get Query JSON from the form and if not empty, deserialize it
			ProteinQueryJSONRoot proteinQueryJSONRoot = 
					GetProteinQueryJSONRootFromFormData.getInstance()
					.getProteinQueryJSONRootFromFormData( 
							form, 
							projectSearchIdsListDeduppedSorted,
							searchIds,
							mapProjectSearchIdToSearchId );

			/////////////////////////////////////////////////////////////////////////////
			/////////////////////////////////////////////////////////////////////////////
			////////   Generic Param processing
			////////////
			//  Copy Exclude Taxonomy and Exclude Protein Sets for lookup
			Set<Integer> excludeTaxonomy_Ids_Set_UserInput = new HashSet<>();
			Set<Integer> excludeProtein_Ids_Set_UserInput = new HashSet<>();
			if ( proteinQueryJSONRoot.getExcludeTaxonomy() != null ) {
				for ( Integer taxonomyId : proteinQueryJSONRoot.getExcludeTaxonomy() ) {
					excludeTaxonomy_Ids_Set_UserInput.add( taxonomyId );
				}
			}
			//  First convert the protein sequence ids that come from the JS code to standard integers and put
			//   in the property excludeproteinSequenceVersionIds
			ProteinsMergedProteinsCommon.getInstance().processExcludeproteinSequenceVersionIdsFromJS( proteinQueryJSONRoot );
			if ( proteinQueryJSONRoot.getExcludeproteinSequenceVersionIds() != null ) {
				for ( Integer proteinId : proteinQueryJSONRoot.getExcludeproteinSequenceVersionIds() ) {
					excludeProtein_Ids_Set_UserInput.add( proteinId );
				}
			}
			CutoffValuesRootLevel cutoffValuesRootLevel = proteinQueryJSONRoot.getCutoffs();
			Z_CutoffValuesObjectsToOtherObjects_RootResult cutoffValuesObjectsToOtherObjects_RootResult =
					Z_CutoffValuesObjectsToOtherObjectsFactory.createSearcherCutoffValuesRootLevel( searchIds, cutoffValuesRootLevel );
			SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel = cutoffValuesObjectsToOtherObjects_RootResult.getSearcherCutoffValuesRootLevel();
			
			LinkedPositions_FilterExcludeLinksWith_Param linkedPositions_FilterExcludeLinksWith_Param = new LinkedPositions_FilterExcludeLinksWith_Param( proteinQueryJSONRoot );
			
			//   Get the Protein Coverage Data
			ProteinCoverageCompute pcs = new ProteinCoverageCompute();
			pcs.setExcludedproteinSequenceVersionIds( proteinQueryJSONRoot.getExcludeproteinSequenceVersionIds() );
			pcs.setExcludedTaxonomyIds( proteinQueryJSONRoot.getExcludeTaxonomy() );
			pcs.setMinPSMs( proteinQueryJSONRoot.getMinPSMs() );
			pcs.setFilterNonUniquePeptides( proteinQueryJSONRoot.isFilterNonUniquePeptides() );
			pcs.setFilterOnlyOnePeptide( proteinQueryJSONRoot.isFilterOnlyOnePeptide() );
			pcs.setSearcherCutoffValuesRootLevel( searcherCutoffValuesRootLevel );
			pcs.setLinkedPositions_FilterExcludeLinksWith_Param( linkedPositions_FilterExcludeLinksWith_Param );
			pcs.setSearches( searches );
			List<ProteinCoverageData> pcd = pcs.getProteinCoverageData();
			request.setAttribute( "proteinCoverageData", pcd );
			//   Build list of proteins for the protein Exclusion list
			{
				// all possible proteins across all searches (for "Exclude Protein" list on web page)
				Map<Integer,Set<Integer>> allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds = new HashMap<>();
				for ( SearchDTO search : searches ) {
					Integer projectSearchId = search.getProjectSearchId();
//					Integer searchId = search.getSearchId();
					SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = 
							searcherCutoffValuesRootLevel.getPerSearchCutoffs( projectSearchId );
					if ( searcherCutoffValuesSearchLevel == null ) {
						String msg = "searcherCutoffValuesRootLevel.getPerSearchCutoffs(projectSearchId) returned null for:  " + projectSearchId;
						log.error( msg );
						throw new ProxlWebappDataException( msg );
					}
					{
						List<SearchProteinCrosslinkWrapper> wrappedCrosslinks = 
								CrosslinkLinkedPositions.getInstance()
								.getSearchProteinCrosslinkWrapperList( search, searcherCutoffValuesSearchLevel, linkedPositions_FilterExcludeLinksWith_Param );
						for ( SearchProteinCrosslinkWrapper wrappedCrosslink : wrappedCrosslinks ) {
							SearchProteinCrosslink crosslink = wrappedCrosslink.getSearchProteinCrosslink();
							SearchProtein searchProtein_1 = crosslink.getProtein1();
							SearchProtein searchProtein_2 = crosslink.getProtein2();
							Integer searchProtein_id_1 = searchProtein_1.getProteinSequenceVersionObject().getProteinSequenceVersionId();
							Integer searchProtein_id_2 = searchProtein_2.getProteinSequenceVersionObject().getProteinSequenceVersionId();
							{
								Set<Integer> searchIdsForProtein_1 =
										allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.get( searchProtein_id_1 );
								if ( searchIdsForProtein_1 == null ) {
									searchIdsForProtein_1 = new HashSet<>();
									allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.put( searchProtein_id_1, searchIdsForProtein_1 );
								}
								searchIdsForProtein_1.add( search.getSearchId() );
							}
							{
								Set<Integer> searchIdsForProtein_2 =
										allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.get( searchProtein_id_2 );
								if ( searchIdsForProtein_2 == null ) {
									searchIdsForProtein_2 = new HashSet<>();
									allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.put( searchProtein_id_2, searchIdsForProtein_2 );
								}
								searchIdsForProtein_2.add( search.getSearchId() );
							}
						}
					}
					{
						List<SearchProteinLooplinkWrapper> wrappedLooplinkLinks = 
								LooplinkLinkedPositions.getInstance()
								.getSearchProteinLooplinkWrapperList( search, searcherCutoffValuesSearchLevel, linkedPositions_FilterExcludeLinksWith_Param );
						for ( SearchProteinLooplinkWrapper wrappedLooplink : wrappedLooplinkLinks ) {
							SearchProteinLooplink looplink = wrappedLooplink.getSearchProteinLooplink();
							SearchProtein searchProtein = looplink.getProtein();
							Integer searchProtein_id = searchProtein.getProteinSequenceVersionObject().getProteinSequenceVersionId();
							{
								Set<Integer> searchIdsForProtein =
										allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.get( searchProtein_id );
								if ( searchIdsForProtein == null ) {
									searchIdsForProtein = new HashSet<>();
									allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.put( searchProtein_id, searchIdsForProtein );
								}
								searchIdsForProtein.add( search.getSearchId() );
							}
						}
					}
					{
						UnlinkedDimerPeptideProteinMappingResult unlinkedDimerPeptideProteinMappingResult =
								UnlinkedDimerPeptideProteinMapping.getInstance()
								.getSearchProteinUnlinkedAndDimerWrapperLists( search, searcherCutoffValuesSearchLevel, linkedPositions_FilterExcludeLinksWith_Param );
						List<SearchProteinDimerWrapper> wrappedDimerLinks = 
								unlinkedDimerPeptideProteinMappingResult.getSearchProteinDimerWrapperList();
						for ( SearchProteinDimerWrapper wrappedDimer : wrappedDimerLinks ) {
							SearchProteinDimer dimer = wrappedDimer.getSearchProteinDimer();
							SearchProtein searchProtein_1 = dimer.getProtein1();
							SearchProtein searchProtein_2 = dimer.getProtein2();
							Integer searchProtein_id_1 = searchProtein_1.getProteinSequenceVersionObject().getProteinSequenceVersionId();
							Integer searchProtein_id_2 = searchProtein_2.getProteinSequenceVersionObject().getProteinSequenceVersionId();
							{
								Set<Integer> searchIdsForProtein_1 =
										allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.get( searchProtein_id_1 );
								if ( searchIdsForProtein_1 == null ) {
									searchIdsForProtein_1 = new HashSet<>();
									allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.put( searchProtein_id_1, searchIdsForProtein_1 );
								}
								searchIdsForProtein_1.add( search.getSearchId() );
							}
							{
								Set<Integer> searchIdsForProtein_2 =
										allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.get( searchProtein_id_2 );
								if ( searchIdsForProtein_2 == null ) {
									searchIdsForProtein_2 = new HashSet<>();
									allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.put( searchProtein_id_2, searchIdsForProtein_2 );
								}
								searchIdsForProtein_2.add( search.getSearchId() );
							}
						}
						List<SearchProteinUnlinkedWrapper> wrappedUnlinkedLinks = 
								unlinkedDimerPeptideProteinMappingResult.getSearchProteinUnlinkedWrapperList();
						for ( SearchProteinUnlinkedWrapper wrappedUnlinked : wrappedUnlinkedLinks ) {
							SearchProteinUnlinked unlinked = wrappedUnlinked.getSearchProteinUnlinked();
							SearchProtein searchProtein = unlinked.getProtein();
							Integer searchProtein_id = searchProtein.getProteinSequenceVersionObject().getProteinSequenceVersionId();
							{
								Set<Integer> searchIdsForProtein =
										allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.get( searchProtein_id );
								if ( searchIdsForProtein == null ) {
									searchIdsForProtein = new HashSet<>();
									allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.put( searchProtein_id, searchIdsForProtein );
								}
								searchIdsForProtein.add( search.getSearchId() );
							}
						}
					}
				}
				List<MergedSearchProtein> allProteinsUnfilteredList = new ArrayList<>( allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.size() );
				for ( Map.Entry<Integer, Set<Integer>> entry : allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.entrySet() ) {
					Integer proteinId = entry.getKey();
					Set<Integer> searchIdsForProtein = entry.getValue();
					List<SearchDTO> searchesForProtein = new ArrayList<>( searchIdsForProtein.size() );
					for ( Integer searchIdForProtein : searchIdsForProtein ) {
						SearchDTO searchForProtein = searchesMapOnSearchId.get( searchIdForProtein );
						if ( searchForProtein == null ) {
							String msg = "Processing searchIdsForProtein, no search found in searchesMapOnId for searchIdForProtein : " + searchIdForProtein;
							log.error( msg );
							throw new ProxlWebappDataException( msg );
						}
						searchesForProtein.add(searchForProtein);
					}
					ProteinSequenceVersionObject ProteinSequenceObject = new ProteinSequenceVersionObject();
					ProteinSequenceObject.setProteinSequenceVersionId( proteinId );
					MergedSearchProtein mergedSearchProtein = new MergedSearchProtein( searchesForProtein, ProteinSequenceObject );
					//  Exclude protein if excluded for all searches
					boolean excludeTaxonomyIdAllSearches = true;
					for ( SearchDTO searchDTO : searchesForProtein ) {
						boolean excludeOnProtein =
								ExcludeOnTaxonomyForProteinSequenceVersionIdSearchId.getInstance()
								.excludeOnTaxonomyForProteinSequenceVersionIdSearchId( 
										excludeTaxonomy_Ids_Set_UserInput, 
										mergedSearchProtein.getProteinSequenceVersionObject(), 
										searchDTO.getSearchId() );
						if ( ! excludeOnProtein ) {
							excludeTaxonomyIdAllSearches = false;
							break;
						}
					}
					if ( excludeTaxonomyIdAllSearches ) {
						//////////  Taxonomy Id in list of excluded taxonomy ids so drop the record
						continue;  //   EARLY Continue
					}
//					int mergedSearchProteinTaxonomyId = mergedSearchProtein.getProteinSequenceVersionObject().getTaxonomyId(); 
//
//					if ( excludeTaxonomy_Ids_Set_UserInput.contains( mergedSearchProteinTaxonomyId ) ) {
//						
//						//////////  Taxonomy Id in list of excluded taxonomy ids so drop the record
//						
//						continue;  //   EARLY Continue
//					}
					allProteinsUnfilteredList.add( mergedSearchProtein );
				}
				Collections.sort( allProteinsUnfilteredList, new SortMergedSearchProtein() );
				request.setAttribute( "proteins", allProteinsUnfilteredList );
			}
			// build list of taxonomies to show in exclusion list
			//    puts Map<Integer, String> into request attribute where key is taxonomy id, value is taxonomy name
			Map<Integer, String> taxonomies = 
					TaxonomiesForSearchOrSearches.getInstance().getTaxonomiesForSearchIds( searchIds );
			request.setAttribute("taxonomies", taxonomies );
			//////////////////////////////////////////////
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
					//  Create URI Encoded JSON for passing to Image and Structure pages in hash 
					String queryJSONToFormURIEncoded = URLEncodeDecodeAURL.urlEncodeAURL( queryJSONToForm );
					request.setAttribute( "queryJSONToFormURIEncoded", queryJSONToFormURIEncoded );
				} catch ( JsonProcessingException e ) {
					String msg = "Failed to write as JSON 'proteinQueryJSONRoot', JsonProcessingException.";
					log.error( msg, e );
					throw e;
				} catch ( Exception e ) {
					String msg = "Failed to write as JSON 'proteinQueryJSONRoot', Exception. ";
					log.error( msg, e );
					throw e;
				}
			}
			//////////////////////////////////////
			//  Create data for Links for Image and Structure pages and put in request
			PopulateRequestDataForImageAndStructureAndQC_NavLinks.getInstance()
			.populateRequestDataForImageAndStructureNavLinksForProtein( proteinQueryJSONRoot, projectId, authAccessLevel, form, request );
			//////////////////////////////////////
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
	
    public class SortMergedSearchProtein implements Comparator<MergedSearchProtein> {
        public int compare(MergedSearchProtein o1, MergedSearchProtein o2) {
            try { return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase()); }
            catch( Exception e ) { return 0; }
        }
    }
}