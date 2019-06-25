package org.yeastrc.xlink.www.actions;

import java.util.ArrayList;
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
import org.yeastrc.xlink.www.nav_links_image_structure.PopulateRequestDataForImageAndStructureAndQC_NavLinks;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.objects.MergedSearchProtein;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.ProteinQueryJSONRoot;
import org.yeastrc.xlink.www.form_utils.GetProteinQueryJSONRootFromFormData;
import org.yeastrc.xlink.www.forms.MergedSearchViewProteinsForm;
import org.yeastrc.xlink.www.forms.PeptideProteinCommonForm;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import org.yeastrc.xlink.www.web_utils.ExcludeLinksWith_Remove_NonUniquePSMs_Checkbox_PopRequestItems;
import org.yeastrc.xlink.www.web_utils.GetAnnotationDisplayUserSelectionDetailsData;
import org.yeastrc.xlink.www.web_utils.GetMinimumPSMsDefaultForProject_PutInRequestScope;
import org.yeastrc.xlink.www.web_utils.GetPageHeaderData;
import org.yeastrc.xlink.www.web_utils.GetSearchDetailsData;
import org.yeastrc.xlink.www.web_utils.ProjectSearchIdsSearchIds_SetRequestParameter;
import org.yeastrc.xlink.www.web_utils.ProteinListingTooltipConfigUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Used for Showing all Merged Proteins on a page 
 * 
 * Separate from Merged Crosslinks and Merged Looplinks pages
 *
 */
public class ViewMergedSearchProteinsAllAction extends Action {

	private static final Logger log = LoggerFactory.getLogger( ViewMergedSearchProteinsAllAction.class);
	
	/* (non-Javadoc)
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public ActionForward execute( ActionMapping mapping,
			ActionForm actionForm,
			HttpServletRequest request,
			HttpServletResponse response )	throws Exception {
		
		request.setAttribute( "queryString", request.getQueryString() );
		
		try {
			// our form
			MergedSearchViewProteinsForm form = (MergedSearchViewProteinsForm)actionForm;
			request.setAttribute( "mergedSearchViewCrosslinkProteinForm", form );
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

			request.setAttribute( "projectSearchIds", projectSearchIdsListDeduppedSorted );
			

			//  Jackson JSON Mapper object for JSON deserialization and serialization
			ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object
			
			Set<Integer> projectSearchIdsProcessedFromForm = new HashSet<>(); // add each projectSearchId as process in loop next

			List<SearchDTO> searches = new ArrayList<SearchDTO>();
			Map<Integer, SearchDTO> searchesMapOnSearchId = new HashMap<>();
			Set<Integer> searchIdsSet = new HashSet<>();
			Map<Integer,Integer> mapProjectSearchIdToSearchId = new HashMap<>();
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
					searchIdsSet.add( search.getSearchId() );
					mapProjectSearchIdToSearchId.put( projectSearchId, search.getSearchId() );
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

			request.setAttribute( "searches", searches );

			//  Populate request objects for Standard Header Display
			GetPageHeaderData.getInstance().getPageHeaderDataWithProjectId( projectId, request );
			//  Populate request objects for Protein Name Tooltip JS
			ProteinListingTooltipConfigUtil.getInstance().putProteinListingTooltipConfigForPage( projectSearchIdsSet, request );

			{
				ProjectSearchIdsSearchIds_SetRequestParameter.SearchesAreUserSorted searchesAreUserSorted  = ProjectSearchIdsSearchIds_SetRequestParameter.SearchesAreUserSorted.NO;
				if ( PeptideProteinCommonForm.DO_NOT_SORT_PROJECT_SEARCH_IDS_YES.equals( form.getDs() ) ) {
					searchesAreUserSorted  = ProjectSearchIdsSearchIds_SetRequestParameter.SearchesAreUserSorted.YES;
				}
				//  Populate request objects for Project Search Id / Search Id pairs in display order in JSON on Page for Javascript
				ProjectSearchIdsSearchIds_SetRequestParameter.getSingletonInstance().populateProjectSearchIdsSearchIds_SetRequestParameter( searches, searchesAreUserSorted, request );
			}
			{
				GetSearchDetailsData.SearchesAreUserSorted searchesAreUserSorted  = GetSearchDetailsData.SearchesAreUserSorted.NO;
				if ( PeptideProteinCommonForm.DO_NOT_SORT_PROJECT_SEARCH_IDS_YES.equals( form.getDs() ) ) {
					searchesAreUserSorted  = GetSearchDetailsData.SearchesAreUserSorted.YES;
				}
				//  Populate request objects for Standard Search Display
				GetSearchDetailsData.getInstance().getSearchDetailsData( searches, searchesAreUserSorted, request );
			}

			GetMinimumPSMsDefaultForProject_PutInRequestScope.getSingletonInstance().getMinimumPSMsDefaultForProject_PutInRequestScope( projectId, request );
			
			//  Populate request objects for User Selection of Annotation Data Display
			GetAnnotationDisplayUserSelectionDetailsData.getInstance().getSearchDetailsData( searches, request );
			//  Populate request objects for excludeLinksWith_Remove_NonUniquePSMs_Checkbox_Fragment.jsp
			ExcludeLinksWith_Remove_NonUniquePSMs_Checkbox_PopRequestItems.getInstance().excludeLinksWith_Remove_NonUniquePSMs_Checkbox_PopRequestItems( searches, request );

			
			//   Get Query JSON from the form and if not empty, deserialize it
			ProteinQueryJSONRoot proteinQueryJSONRoot = 
					GetProteinQueryJSONRootFromFormData.getInstance()
					.getProteinQueryJSONRootFromFormData( form, projectId, projectSearchIdsSet, searchIdsSet, mapProjectSearchIdToSearchId );
			
			//  Convert the protein sequence ids that come from the JS code to standard integers and put
			//   in the property excludeproteinSequenceVersionIds.
			//      Do this here since may have to convert old NRSeqProteinIds.
			ProteinsMergedProteinsCommon.getInstance().processExcludeproteinSequenceVersionIdsFromJS( proteinQueryJSONRoot );
			
			//////////////////////////////////////////////
			/////////////////////
			//  clear out form so value doesn't go back on the page in the form
			form.setQueryJSON( "" );
			/////////////////////
			////  Put Updated queryJSON on the page
			{
				try {
					String queryJSONToPage = jacksonJSON_Mapper.writeValueAsString( proteinQueryJSONRoot );
					//  Set queryJSON in request attribute to put on page outside of form
					request.setAttribute( "queryJSONToForm", queryJSONToPage );
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
			PopulateRequestDataForImageAndStructureAndQC_NavLinks.getInstance()
			.populateRequestDataForImageAndStructureNavLinksForProtein( proteinQueryJSONRoot, projectId, authAccessLevel, form, request );

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
	
	/////////////////////////////////////////////////
	private class SortMergedSearchProtein implements Comparator<MergedSearchProtein> {
		@Override
		public int compare(MergedSearchProtein o1, MergedSearchProtein o2) {
			try { 
				return o1.getNameLowerCase().compareTo(o2.getNameLowerCase()); 
			}
			catch( Exception e ) { 
				return 0; 
			}
		}
	}
}
