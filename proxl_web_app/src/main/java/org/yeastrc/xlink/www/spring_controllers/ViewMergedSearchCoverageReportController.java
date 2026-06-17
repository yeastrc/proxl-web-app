package org.yeastrc.xlink.www.spring_controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.yeastrc.xlink.www.spring_controllers__helpers.ProteinsMergedProteinsCommon;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.nav_links_image_structure.PopulateRequestDataForImageAndStructureAndQC_NavLinks;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.user_session_management.UserSession;
import org.yeastrc.xlink.www.user_session_management.UserSessionManager;
import org.yeastrc.xlink.www.constants.SpringMvc_Config_Parameter_Values_Constants;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.ProteinQueryJSONRoot;
import org.yeastrc.xlink.www.form_utils.GetProteinQueryJSONRootFromFormData;
import org.yeastrc.xlink.www.forms.MergedSearchViewProteinsForm;
import org.yeastrc.xlink.www.forms.PeptideProteinCommonForm;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import org.yeastrc.xlink.www.web_utils.AnyPDBFilesForProjectId;
import org.yeastrc.xlink.www.web_utils.ExcludeLinksWith_Remove_NonUniquePSMs_Checkbox_PopRequestItems;
import org.yeastrc.xlink.www.web_utils.GetAnnotationDisplayUserSelectionDetailsData;
import org.yeastrc.xlink.www.web_utils.GetMinimumPSMsDefaultForProject_PutInRequestScope;
import org.yeastrc.xlink.www.web_utils.GetPageHeaderData;
import org.yeastrc.xlink.www.web_utils.GetSearchDetailsData;
import org.yeastrc.xlink.www.web_utils.ProjectSearchIdsSearchIds_SetRequestParameter;
import org.yeastrc.xlink.www.web_utils.ProteinListingTooltipConfigUtil;
import org.yeastrc.xlink.www.web_utils.URLEncodeDecodeAURL;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Spring MVC controller for paths /proteinCoverageReport.do and /mergedProteinCoverageReport.do
 *
 * <p>Both paths use the same form, logic and Success JSP
 * (viewMergedProteinCoverageReport.jsp); the Struts {@code parameter} attribute
 * ("mergedPage" / "notMergedPage"), previously read via {@code mapping.getParameter()},
 * only controls whether the "mergedPage" request attribute is set.
 */
@Controller
public class ViewMergedSearchCoverageReportController {

	private static final Logger log = LoggerFactory.getLogger( ViewMergedSearchCoverageReportController.class );

	@RequestMapping( A__SpringMVC_Controller_Paths.ViewMergedSearchCoverageReportController_mergedProteinCoverageReport )
	public String mergedProteinCoverageReport(
			@ModelAttribute( "mergedSearchViewProteinForm" ) MergedSearchViewProteinsForm form,
			HttpServletRequest request,
			HttpServletResponse response ) throws Exception {
		return handle( SpringMvc_Config_Parameter_Values_Constants.PARAMETER__MERGED_PROTEIN_COVERAGE_PAGE, form, request, response );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.ViewMergedSearchCoverageReportController_proteinCoverageReport )
	public String proteinCoverageReport(
			@ModelAttribute( "mergedSearchViewProteinForm" ) MergedSearchViewProteinsForm form,
			HttpServletRequest request,
			HttpServletResponse response ) throws Exception {
		return handle( SpringMvc_Config_Parameter_Values_Constants.PARAMETER__NOT__MERGED_PROTEIN_COVERAGE_PAGE, form, request, response );
	}

	private String handle(
			String actionMappingParameter,
			MergedSearchViewProteinsForm form,
			HttpServletRequest request,
			HttpServletResponse response ) throws Exception {

		Integer projectId = null;

		try {
			// our form
			request.setAttribute( "mergedSearchViewCrosslinkProteinForm", form );
			request.setAttribute( "actionForm", form );
			request.setAttribute( "queryString",  request.getQueryString() );
			if ( SpringMvc_Config_Parameter_Values_Constants.PARAMETER__MERGED_PROTEIN_COVERAGE_PAGE.equals( actionMappingParameter ) ) {
				request.setAttribute( "mergedPage", true );
			}
			int[] projectSearchIds = form.getProjectSearchId();
			if ( projectSearchIds == null || projectSearchIds.length == 0 ) {
				return SpringMvcForwards.INVALID_REQUEST_DATA;
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
				String msg = "No project ids for projectSearchIds: ";
				for ( int projectSearchId : projectSearchIds ) {
					msg += projectSearchId + ", ";
				}
				log.warn( msg );
				return SpringMvcForwards.INVALID_REQUEST_DATA;
			}
			if ( projectIdsFromSearchIds.size() > 1 ) {
				//  Invalid request, searches across projects
				return SpringMvcForwards.INVALID_REQUEST_SEARCHES_ACROSS_PROJECTS;
			}

			projectId = projectIdsFromSearchIds.get( 0 );

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
					return SpringMvcForwards.NO_USER_SESSION;
				}
				return SpringMvcForwards.INSUFFICIENT_ACCESS_PRIVILEGE;
			}
			request.setAttribute( WebConstants.REQUEST_AUTH_ACCESS_LEVEL, authAccessLevel );

			///    Done Processing Auth Check and Auth Level
			//////////////////////////////

			request.setAttribute( "projectSearchIds", projectSearchIdsListDeduppedSorted );

			//  Jackson JSON Mapper object for JSON deserialization and serialization
			ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object

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
					return SpringMvcForwards.HOME;  //  EARLY EXIT from Method
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
							projectId,
							projectSearchIdsListDeduppedSorted,
							searchIds, mapProjectSearchIdToSearchId );

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

			return "viewMergedProteinCoverageReport";

		} catch ( ProxlWebappDataException e ) {

			Integer authUserId = null;
			Integer userMgmtUserId = null;
			String username = null;

			try {
				UserSession userSession = UserSessionManager.getSinglesonInstance().getUserSession(request);

				if ( userSession != null ) {

					authUserId = userSession.getAuthUserId();
					userMgmtUserId = userSession.getUserMgmtUserId();
					username = userSession.getUsername();
				}
			} catch ( Exception e2 ) {
				log.error( "In Main } catch ( Exception e ) {: Error getting User Id and Username: ", e2 );
			}

			String msg = "Exception processing request data. authUserId (null if no session): "
					+ authUserId
					+ ", userMgmtUserId (null if no session): " + userMgmtUserId
					+ ", username (null if no session): " + username
					+ e.toString();
			log.error( msg, e );

			return SpringMvcForwards.INVALID_REQUEST_DATA;

		} catch ( Exception e ) {

			Integer authUserId = null;
			Integer userMgmtUserId = null;
			String username = null;

			try {
				UserSession userSession = UserSessionManager.getSinglesonInstance().getUserSession(request);

				if ( userSession != null ) {

					authUserId = userSession.getAuthUserId();
					userMgmtUserId = userSession.getUserMgmtUserId();
					username = userSession.getUsername();
				}
			} catch ( Exception e2 ) {
				log.error( "In Main } catch ( Exception e ) {: Error getting User Id and Username: ", e2 );
			}

			String msg = "Exception caught. authUserId (null if no session): "
					+ authUserId
					+ ", userMgmtUserId (null if no session): " + userMgmtUserId
					+ ", username (null if no session): " + username
					+ e.toString();
			log.error( msg, e );

			throw e;
		}
	}
}
