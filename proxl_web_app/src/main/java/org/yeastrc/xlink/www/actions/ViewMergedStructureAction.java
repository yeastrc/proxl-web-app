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
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.constants.ConfigSystemsKeysConstants;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.cutoff_processing_web.GetDefaultPsmPeptideCutoffs;
import org.yeastrc.xlink.www.dao.ConfigSystemDAO;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.forms.MergedSearchViewProteinsForm;
import org.yeastrc.xlink.www.forms.PeptideProteinCommonForm;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import org.yeastrc.xlink.www.web_utils.ExcludeLinksWith_Remove_NonUniquePSMs_Checkbox_PopRequestItems;
import org.yeastrc.xlink.www.web_utils.GetAnnotationDisplayUserSelectionDetailsData;
import org.yeastrc.xlink.www.web_utils.GetPageHeaderData;
import org.yeastrc.xlink.www.web_utils.GetSearchDetailsData;
import org.yeastrc.xlink.www.web_utils.ProteinListingTooltipConfigUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Handle showing the user a web page for viewing and analyzing how
 * crosslinks, looplinks, and monolinks map to a PDB containing one
 * or more of the proteins found in the experiment.
 * 
 * @author Michael Riffle
 *
 */
public class ViewMergedStructureAction extends Action {
	
	private static final Logger log = LoggerFactory.getLogger( ViewMergedStructureAction.class);
	
	@Override
	public ActionForward execute( ActionMapping mapping,
			  ActionForm actionForm,
			  HttpServletRequest request,
			  HttpServletResponse response ) throws Exception {
		try {
			MergedSearchViewProteinsForm form = (MergedSearchViewProteinsForm) actionForm;
			/*
			 * HANDLE ALL AUTHENTICATION HERE
			 */
			int[] projectSearchIdsFromForm = form.getProjectSearchId();
			if ( projectSearchIdsFromForm.length == 0 ) {
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			}
			//   Get the project id for these searches
			Set<Integer> projectSearchIdsSet = new HashSet<Integer>( );
			for ( int projectSearchId : projectSearchIdsFromForm ) {
				projectSearchIdsSet.add( projectSearchId );
			}
			
//			List<Integer> projectSearchIdsListDeduppedSorted = new ArrayList<>( projectSearchIdsSet );
//			Collections.sort( projectSearchIdsListDeduppedSorted );
			
			List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsSet );
			if ( projectIdsFromSearchIds.isEmpty() ) {
				// should never happen
				String msg = "No project ids for projectSearchIds: ";
				for ( int projectSearchId : projectSearchIdsFromForm ) {
					msg += projectSearchId + ", ";
				}
				log.warn( msg );
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

			///    Done Processing Auth Check and Auth Level
			//////////////////////////////

			Set<Integer> projectSearchIdsProcessedFromForm = new HashSet<>(); // add each projectSearchId as process in loop next
			
			List<SearchDTO> searches = new ArrayList<SearchDTO>( projectSearchIdsFromForm.length );
			Map<Integer, SearchDTO> searchesMapOnSearchId = new HashMap<>();
			
//			int[] searchIdsArray = new int[ projectSearchIdsListDeduppedSorted.size() ];
//			int searchIdsArrayIndex = 0;
			
			for( int projectSearchId : projectSearchIdsFromForm ) {

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
//					searchIdsArray[ searchIdsArrayIndex ] = search.getSearchId();
//					searchIdsArrayIndex++;
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
				
				request.setAttribute( "projectSearchIdsUserOrdered", "" );
			} else {

				request.setAttribute( "projectSearchIdsUserOrdered", PeptideProteinCommonForm.DO_NOT_SORT_PROJECT_SEARCH_IDS_YES );
			}
			
			List<Integer> projectSearchIdsListDedupped_SortedIfNeeded = new ArrayList<>( searches.size() );
			for ( SearchDTO search : searches ) {
				projectSearchIdsListDedupped_SortedIfNeeded.add( search.getProjectSearchId() );
			}

			request.setAttribute( "projectSearchIds", projectSearchIdsListDedupped_SortedIfNeeded );
			
			if ( projectSearchIdsListDedupped_SortedIfNeeded.size() == 1 ) {
				Integer onlySingleProjectSearchId = projectSearchIdsListDedupped_SortedIfNeeded.get( 0 );
				request.setAttribute( "onlySingleProjectSearchId", onlySingleProjectSearchId );	
			}
			
			
			//  Jackson JSON Mapper object for JSON deserialization and serialization
			ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object


			Collection<Integer> searchIds = new HashSet<>();
			Map<Integer,Integer> mapProjectSearchIdToSearchId = new HashMap<>();
			List<Integer> searchIdsListDeduppedSorted = new ArrayList<>( searches.size() );
			
			for ( SearchDTO search : searches ) {
				searchIds.add( search.getSearchId() );
				searchIdsListDeduppedSorted.add( search.getSearchId() );
				mapProjectSearchIdToSearchId.put( search.getProjectSearchId(), search.getSearchId() );
			}

			GetPageHeaderData.getInstance().getPageHeaderDataWithProjectId( projectId, request );
			//  Populate request objects for Protein Name Tooltip JS
			ProteinListingTooltipConfigUtil.getInstance().putProteinListingTooltipConfigForPage( projectSearchIdsSet, request );

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

			String annotation_data_webservice_base_url = 
					ConfigSystemDAO.getInstance().getConfigValueForConfigKey( ConfigSystemsKeysConstants.PROTEIN_ANNOTATION_WEBSERVICE_URL_KEY );
			request.setAttribute( "annotation_data_webservice_base_url", annotation_data_webservice_base_url );
			
			/////////////////////////////////////////////////////////////////////////////
			////////   Generic Param processing
			CutoffValuesRootLevel cutoffValuesRootLevelCutoffDefaults =
					GetDefaultPsmPeptideCutoffs.getInstance()
					.getDefaultPsmPeptideCutoffs( projectSearchIdsListDedupped_SortedIfNeeded, searchIds, mapProjectSearchIdToSearchId );
			String cutoffValuesRootLevelCutoffDefaultsJSONString = jacksonJSON_Mapper.writeValueAsString( cutoffValuesRootLevelCutoffDefaults );
			request.setAttribute( "cutoffValuesRootLevelCutoffDefaults", cutoffValuesRootLevelCutoffDefaultsJSONString );

		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
		return mapping.findForward( "Success" );
	}	
}
