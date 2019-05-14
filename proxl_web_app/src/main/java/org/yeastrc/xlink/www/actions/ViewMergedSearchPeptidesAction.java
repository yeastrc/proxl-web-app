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
import java.util.TreeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.mutable.MutableInt;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.nav_links_image_structure.PopulateRequestDataForImageAndStructureAndQC_NavLinks;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.objects.IMergedSearchLink;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.SearchModMassDistinctSearcher;
import org.yeastrc.xlink.www.actions.PeptidesMergedCommonPageDownload.PeptidesMergedCommonPageDownloadResult;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.forms.MergedSearchViewPeptidesForm;
import org.yeastrc.xlink.www.forms.PeptideProteinCommonForm;
import org.yeastrc.xlink.www.objects.SearchBooleanWrapper;
import org.yeastrc.xlink.www.objects.SearchCount;
import org.yeastrc.xlink.www.objects.VennDiagramDataToJSON;
import org.yeastrc.xlink.www.objects.WebMergedReportedPeptide;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import org.yeastrc.xlink.www.web_utils.ExcludeLinksWith_Remove_NonUniquePSMs_Checkbox_PopRequestItems;
import org.yeastrc.xlink.www.web_utils.GenerateVennDiagramDataToJSON;
import org.yeastrc.xlink.www.web_utils.GetAnnotationDisplayUserSelectionDetailsData;
import org.yeastrc.xlink.www.web_utils.GetPageHeaderData;
import org.yeastrc.xlink.www.web_utils.GetSearchDetailsData;
import org.yeastrc.xlink.www.web_utils.IsShowDownloadLinks_Skyline_SetRequestParameters;
import org.yeastrc.xlink.www.web_utils.ProteinListingTooltipConfigUtil;
import org.yeastrc.xlink.www.webapp_timing.WebappTiming;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 *
 */
public class ViewMergedSearchPeptidesAction extends Action {
	
	private static final Logger log = LoggerFactory.getLogger( ViewMergedSearchPeptidesAction.class);
	
	@Override
	public ActionForward execute( ActionMapping mapping,
			ActionForm actionForm,
			HttpServletRequest request,
			HttpServletResponse response )
					throws Exception {
		WebappTiming webappTiming = null;
		if ( log.isDebugEnabled() ) {
			webappTiming = WebappTiming.getInstance( log );
			request.setAttribute( "webappTiming", webappTiming );
		}
		try {
			// our form
			MergedSearchViewPeptidesForm form = (MergedSearchViewPeptidesForm) actionForm;
			request.setAttribute( "mergedSearchViewCrosslinkPeptideForm", form );
			request.setAttribute( "strutsActionForm", form );
			int[] projectSearchIdsFromForm = form.getProjectSearchId();
			if ( projectSearchIdsFromForm.length == 0 ) {
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			}
			//   Get the project id for these searches
			Set<Integer> projectSearchIdsSet = new HashSet<Integer>( );
			for ( int projectSearchId : projectSearchIdsFromForm ) {
				projectSearchIdsSet.add( projectSearchId );
			}
			List<Integer> projectSearchIdsListDeduppedSorted = new ArrayList<>( projectSearchIdsSet );
			Collections.sort( projectSearchIdsListDeduppedSorted );
			List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsSet );
			if ( projectIdsFromSearchIds.isEmpty() ) {
				// should never happen
				String msg = "No project ids for search ids: ";
				for ( int projectSearchId : projectSearchIdsFromForm ) {
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
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request, response );
			//  Test access to the project id
			WebSessionAuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getWebSessionAuthAccessLevel();
			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {
				//  No Access Allowed for this project id
				if ( accessAndSetupWebSessionResult.isNoSession() ) {
					//  No User session 
					return mapping.findForward( StrutsGlobalForwardNames.NO_USER_SESSION );
				}
				return mapping.findForward( StrutsGlobalForwardNames.INSUFFICIENT_ACCESS_PRIVILEGE );
			}
			request.setAttribute( WebConstants.REQUEST_AUTH_ACCESS_LEVEL, authAccessLevel );
			if ( webappTiming != null ) {
				webappTiming.markPoint( "After Auth" );
			}
			
			///    Done Processing Auth Check and Auth Level
			//////////////////////////////

			request.setAttribute( "projectSearchIds", projectSearchIdsListDeduppedSorted );

			Set<Integer> projectSearchIdsProcessedFromForm = new HashSet<>(); // add each projectSearchId as process in loop next
			
			List<SearchDTO> searches = new ArrayList<SearchDTO>( projectSearchIdsListDeduppedSorted.size() );
			Map<Integer, SearchDTO> searchesMapOnSearchId = new HashMap<>();
			Collection<Integer> searchIds = new HashSet<>();
			int[] searchIdsArray = new int[ projectSearchIdsListDeduppedSorted.size() ];
			int searchIdsArrayIndex = 0;
			for ( int projectSearchId : projectSearchIdsFromForm ) {
				if ( projectSearchIdsProcessedFromForm.add( projectSearchId ) ) {
					//  Haven't processed this projectSearchId yet in this loop so process it now
					SearchDTO search = SearchDAO.getInstance().getSearchFromProjectSearchId( projectSearchId );
					if ( search == null ) {
						String msg = "projectSearchId '" + projectSearchId + "' not found in the database. User taken to home page.";
						log.warn( msg );
						//  Search not found, the data on the page they are requesting does not exist.
						//  The data on the user's previous page no longer reflects what is in the database.
						//  Take the user to the home page
						return mapping.findForward( StrutsGlobalForwardNames.HOME );  //  EARLY EXIT from Method
					}
					searches.add( search );
					searchesMapOnSearchId.put( search.getSearchId(), search );
					searchIds.add( search.getSearchId() );
					searchIdsArray[ searchIdsArrayIndex ] = search.getSearchId();
					searchIdsArrayIndex++;
				}
			}
			
			if ( ! PeptideProteinCommonForm.DO_NOT_SORT_PROJECT_SEARCH_IDS_YES.equals( form.getDs() ) ) {

				// Sort searches list
				Collections.sort( searches, new Comparator<SearchDTO>() {
					@Override
					public int compare(SearchDTO o1, SearchDTO o2) {
						return o1.getSearchId() - o2.getSearchId();
					}
				});
			}
			
			//  Jackson JSON Mapper object for JSON deserialization and serialization
			ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object
			
			//  Populate request objects for Standard Header Display
			GetPageHeaderData.getInstance().getPageHeaderDataWithProjectId( projectId, request );
			//  Populate request objects for Protein Name Tooltip JS
			ProteinListingTooltipConfigUtil.getInstance().putProteinListingTooltipConfigForPage( projectSearchIdsSet, request );
			//  Search Ids already sorted
			request.setAttribute( "searches", searches );
			
			GetSearchDetailsData.SearchesAreUserSorted searchesAreUserSorted  = GetSearchDetailsData.SearchesAreUserSorted.NO;
			if ( PeptideProteinCommonForm.DO_NOT_SORT_PROJECT_SEARCH_IDS_YES.equals( form.getDs() ) ) {
				searchesAreUserSorted  = GetSearchDetailsData.SearchesAreUserSorted.YES;
			}
			//  Populate request objects for Standard Search Display
			GetSearchDetailsData.getInstance().getSearchDetailsData( searches, searchesAreUserSorted, request );
			
			//  Populate request objects for User Selection of Annotation Data Display
			GetAnnotationDisplayUserSelectionDetailsData.getInstance().getSearchDetailsData( searches, request );
			//  Populate request objects for excludeLinksWith_Remove_NonUniquePSMs_Checkbox_Fragment.jsp
			ExcludeLinksWith_Remove_NonUniquePSMs_Checkbox_PopRequestItems.getInstance().excludeLinksWith_Remove_NonUniquePSMs_Checkbox_PopRequestItems( searches, request );

			//  Populates request attribute
			IsShowDownloadLinks_Skyline_SetRequestParameters.getInstance().isShowDownloadLinks_Skyline_SetRequestParameters( searchIds, request );
			
			List<Double> modMassDistinctForSearchesList = SearchModMassDistinctSearcher.getInstance().getDistinctDynamicModMassesForSearchId( searchIdsArray );
			if ( webappTiming != null ) {
				webappTiming.markPoint( "After get Distinct Dynamic Mod Masses For SearchId" );
			}
			List<String> modMassStringsList = new ArrayList<>( modMassDistinctForSearchesList.size() );
			for ( Double modMass : modMassDistinctForSearchesList ) {
				String modMassAsString = modMass.toString();
				modMassStringsList.add( modMassAsString );
			}
			request.setAttribute( "modMassFilterList", modMassStringsList );
			
			////////     Get Merged Peptides
			PeptidesMergedCommonPageDownloadResult peptidesMergedCommonPageDownloadResult =
					PeptidesMergedCommonPageDownload.getInstance()
					.getWebMergedPeptideRecords(
							form,
							projectSearchIdsListDeduppedSorted,
							searches,
							searchesMapOnSearchId,
							PeptidesMergedCommonPageDownload.FlagCombinedReportedPeptideEntries.YES );
			
			boolean anyResultsHaveIsotopeLabels = peptidesMergedCommonPageDownloadResult.isAnyResultsHaveIsotopeLabels();
			
			request.setAttribute( "peptidePsmAnnotationNameDescListsForEachSearch", peptidesMergedCommonPageDownloadResult.getPeptidePsmAnnotationNameDescListsForEachSearch() );
			request.setAttribute( "anyReportedPeptideEntriesWereCombined", peptidesMergedCommonPageDownloadResult.isAnyReportedPeptideEntriesWereCombined() );
			request.setAttribute( "anyResultsHaveIsotopeLabels", anyResultsHaveIsotopeLabels );
			
			List<WebMergedReportedPeptide> webMergedReportedPeptideList = peptidesMergedCommonPageDownloadResult.getWebMergedReportedPeptideList();
			for ( WebMergedReportedPeptide link : webMergedReportedPeptideList ) {
				List<SearchBooleanWrapper> searchContainsPeptide = new ArrayList<SearchBooleanWrapper>( searches.size() );
				for( SearchDTO search : searches ) {
					if( link.getSearches().contains( search ) ) {
						searchContainsPeptide.add( new SearchBooleanWrapper( search, true ) );
					} else {
						searchContainsPeptide.add( new SearchBooleanWrapper( search, false ) );
					}					
				}
				link.setSearchContainsPeptide( searchContainsPeptide );
			}
			request.setAttribute( "peptideListSize", webMergedReportedPeptideList.size() );
			request.setAttribute( "peptideList", webMergedReportedPeptideList );
			request.setAttribute( "queryString", request.getQueryString() );
			// build the JSON data structure for searches
			ObjectMapper mapper = new ObjectMapper();  //  Jackson JSON library object
			VennDiagramDataToJSON vennDiagramDataToJSON =
					GenerateVennDiagramDataToJSON.createVennDiagramDataToJSON( webMergedReportedPeptideList, searches );
			if ( vennDiagramDataToJSON != null ) {
				String vennDiagramDataJSON = mapper.writeValueAsString( vennDiagramDataToJSON );
				request.setAttribute( "vennDiagramDataToJSON", vennDiagramDataJSON );
			}
			// get the counts for the number of links for each search, save to map, save to request
			Map<Integer, MutableInt> searchCounts = new TreeMap<Integer, MutableInt>();
			for( IMergedSearchLink link : webMergedReportedPeptideList ) {
				for( SearchDTO search : link.getSearches() ) {
					Integer projectSearchId = search.getSearchId();
					MutableInt searchCount = searchCounts.get( projectSearchId );
					if ( searchCount == null ) {
						searchCount = new MutableInt( 1 );
						searchCounts.put( projectSearchId, searchCount );
					} else {
						searchCount.increment();
					}
				}
			}
			List<SearchCount> SearchCountList = new ArrayList<>();
			for ( SearchDTO search : searches  ) {
				Integer searchId = search.getSearchId();
				MutableInt searchCountMapValue = searchCounts.get( searchId );
				SearchCount searchCount = new SearchCount();
				SearchCountList.add(searchCount);
				searchCount.setSearchId( searchId );
				searchCount.setProjectSearchId( search.getProjectSearchId() );
				if ( searchCountMapValue != null ) {
					searchCount.setCount( searchCountMapValue.intValue() );
				} else {
					searchCount.setCount( 0 );
				}
			}
			request.setAttribute( "searchCounts", SearchCountList );
			/////////////////////
			//  clear out form so value doesn't go back on the page in the form
			form.setQueryJSON( "" );
			/////////////////////
			////  Put Updated queryJSON on the page
			{
				try {
					String queryJSONToForm = jacksonJSON_Mapper.writeValueAsString( peptidesMergedCommonPageDownloadResult.getMergedPeptideQueryJSONRoot() );
					//  Set queryJSON in request attribute to put on page outside of form
					request.setAttribute( "queryJSONToForm", queryJSONToForm );
				} catch ( JsonProcessingException e ) {
					String msg = "Failed to write as JSON 'queryJSONToForm', JsonProcessingException.  queryJSONFromForm: " + form.getQueryJSON();
					log.error( msg, e );
					throw new ProxlWebappDataException( msg, e );
				} catch ( Exception e ) {
					String msg = "Failed to write as JSON 'queryJSONToForm', Exception.  queryJSONFromForm: " + form.getQueryJSON();
					log.error( msg, e );
					throw new ProxlWebappDataException( msg, e );
				}
			}
			//  Create data for Links for Image and Structure pages and put in request
			PopulateRequestDataForImageAndStructureAndQC_NavLinks.getInstance()
			.populateRequestDataForImageAndStructureAndQC_NavLinksForPeptide( peptidesMergedCommonPageDownloadResult.getMergedPeptideQueryJSONRoot(), projectId, authAccessLevel, form, request );
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
}
