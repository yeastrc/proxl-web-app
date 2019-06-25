package org.yeastrc.xlink.www.actions;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.SearchModMassDistinctSearcher;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.nav_links_image_structure.PopulateRequestDataForImageAndStructureAndQC_NavLinks;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.objects.ViewSearchPeptidesPageDataRoot;
import org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.cutoff_processing_web.Set__A_QueryBase_JSONRoot__Defaults;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.PeptideQueryJSONRoot;
import org.yeastrc.xlink.www.form_utils.Update__A_QueryBase_JSONRoot__ForCurrentSearchIds;
import org.yeastrc.xlink.www.forms.PeptideProteinCommonForm;
import org.yeastrc.xlink.www.forms.SearchViewPeptidesForm;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import org.yeastrc.xlink.www.web_utils.ExcludeLinksWith_Remove_NonUniquePSMs_Checkbox_PopRequestItems;
import org.yeastrc.xlink.www.web_utils.GetAnnotationDisplayUserSelectionDetailsData;
import org.yeastrc.xlink.www.web_utils.GetMinimumPSMsDefaultForProject_PutInRequestScope;
import org.yeastrc.xlink.www.web_utils.GetPageHeaderData;
import org.yeastrc.xlink.www.web_utils.GetSearchDetailsData;
import org.yeastrc.xlink.www.web_utils.IsShowDownloadLinks_Skyline_SetRequestParameters;
import org.yeastrc.xlink.www.web_utils.ProjectSearchIdsSearchIds_SetRequestParameter;
import org.yeastrc.xlink.www.web_utils.ProteinListingTooltipConfigUtil;
import org.yeastrc.xlink.www.webapp_timing.WebappTiming;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 *
 */
public class ViewSearchPeptidesAction extends Action {
	
	private static final Logger log = LoggerFactory.getLogger( ViewSearchPeptidesAction.class);
	
	/* (non-Javadoc)
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
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
		// Root object of everything placed on the ViewSearchPeptides page by the JSP
		ViewSearchPeptidesPageDataRoot viewSearchPeptidesPageDataRoot = new ViewSearchPeptidesPageDataRoot();
		request.setAttribute( "viewSearchPeptidesPageDataRoot", viewSearchPeptidesPageDataRoot );
		try {
			// our form
			SearchViewPeptidesForm form = (SearchViewPeptidesForm)actionForm;
			request.setAttribute( "strutsActionForm", form );
			int projectSearchId = form.getProjectSearchIdSingle();
			viewSearchPeptidesPageDataRoot.setProjectSearchId( projectSearchId );
			//   Get the project id for this search
			Collection<Integer> projectSearchIdsSet = new HashSet<>();
			projectSearchIdsSet.add( projectSearchId );
			List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsSet );
			if ( projectIdsFromSearchIds.isEmpty() ) {
				// should never happen
				String msg = "No project ids for projectSearchId: " + projectSearchId;
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
			if ( webappTiming != null ) {
				webappTiming.markPoint( "After Auth" );
			}
			///    Done Processing Auth Check and Auth Level
			//////////////////////////////

			request.setAttribute( "projectSearchId", projectSearchId );
			
			//  Jackson JSON Mapper object for JSON deserialization and serialization
			ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object
			
			SearchDTO search = SearchDAO.getInstance().getSearchFromProjectSearchId( projectSearchId );
			if ( search == null ) {
				String msg = ": No searchId found for projectSearchId: " + projectSearchId;
				log.warn( msg );
				throw new ProxlWebappDataException( msg );
			}
			int searchId = search.getSearchId();
			viewSearchPeptidesPageDataRoot.setSearchId( searchId );
			
			Collection<Integer> searchIdsSet = new HashSet<>();
			searchIdsSet.add( searchId );
			Map<Integer,Integer> mapProjectSearchIdToSearchId = new HashMap<>();
			mapProjectSearchIdToSearchId.put( projectSearchId, searchId );
			
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
				ProjectSearchIdsSearchIds_SetRequestParameter.getSingletonInstance().populateProjectSearchIdsSearchIds_SetRequestParameter( search, searchesAreUserSorted, request );
			}
			{
				GetSearchDetailsData.SearchesAreUserSorted searchesAreUserSorted  = GetSearchDetailsData.SearchesAreUserSorted.NO;
				if ( PeptideProteinCommonForm.DO_NOT_SORT_PROJECT_SEARCH_IDS_YES.equals( form.getDs() ) ) {
					searchesAreUserSorted  = GetSearchDetailsData.SearchesAreUserSorted.YES;
				}
				//  Populate request objects for Standard Search Display
				GetSearchDetailsData.getInstance().getSearchDetailsData( search, searchesAreUserSorted, request );
			}

			GetMinimumPSMsDefaultForProject_PutInRequestScope.getSingletonInstance().getMinimumPSMsDefaultForProject_PutInRequestScope( projectId, request );
			
			//  Populate request objects for User Selection of Annotation Data Display
			GetAnnotationDisplayUserSelectionDetailsData.getInstance().getSearchDetailsData( search, request );
			//  Populate request objects for excludeLinksWith_Remove_NonUniquePSMs_Checkbox_Fragment.jsp
			ExcludeLinksWith_Remove_NonUniquePSMs_Checkbox_PopRequestItems.getInstance().excludeLinksWith_Remove_NonUniquePSMs_Checkbox_PopRequestItems( search, request );
			
			//  Populates request attribute
			IsShowDownloadLinks_Skyline_SetRequestParameters.getInstance().isShowDownloadLinks_Skyline_SetRequestParameters( searchIdsSet, request );
			
			///  Get list of all possible Dynamic Mod Masses.  Do here so if convert existing Query Param Data, have it here.
			int[] searchIdsArray = { searchId };
			List<Double> modMassDistinctForSearchesList = SearchModMassDistinctSearcher.getInstance().getDistinctDynamicModMassesForSearchId( searchIdsArray );
			if ( webappTiming != null ) {
				webappTiming.markPoint( "After get Distinct Dynamic Mod Masses For SearchId" );
			}
			List<String> modMassFilterList = new ArrayList<>( modMassDistinctForSearchesList.size() );
			for ( Double modMass : modMassDistinctForSearchesList ) {
				String modMassAsString = modMass.toString();
				modMassFilterList.add( modMassAsString );
			}
			viewSearchPeptidesPageDataRoot.setModMassFilterList( modMassFilterList );
			
			//   Get Query JSON from the form and if not empty, deserialize it
			String queryJSONFromForm = form.getQueryJSON();
			PeptideQueryJSONRoot peptideQueryJSONRoot = null;
			if ( StringUtils.isNotEmpty( queryJSONFromForm ) ) {
				try {
					peptideQueryJSONRoot = jacksonJSON_Mapper.readValue( queryJSONFromForm, PeptideQueryJSONRoot.class );
				} catch ( JsonParseException e ) {
					String msg = "Failed to parse 'queryJSONFromForm', JsonParseException.  queryJSONFromForm: " + queryJSONFromForm;
					log.error( msg, e );
					throw new ProxlWebappDataException( msg, e );
				} catch ( JsonMappingException e ) {
					String msg = "Failed to parse 'queryJSONFromForm', JsonMappingException.  queryJSONFromForm: " + queryJSONFromForm;
					log.error( msg, e );
					throw new ProxlWebappDataException( msg, e );
				} catch ( IOException e ) {
					String msg = "Failed to parse 'queryJSONFromForm', IOException.  queryJSONFromForm: " + queryJSONFromForm;
					log.error( msg, e );
					throw new ProxlWebappDataException( msg, e );
				}

				//  Update peptideQueryJSONRoot for current search ids and project search ids
				Update__A_QueryBase_JSONRoot__ForCurrentSearchIds.getInstance()
				.update__A_QueryBase_JSONRoot__ForCurrentSearchIds( peptideQueryJSONRoot, mapProjectSearchIdToSearchId, projectId );
				
			} else {
				//  Query JSON in the form is empty so create an empty object that will be populated.
				peptideQueryJSONRoot = new PeptideQueryJSONRoot();
				Set__A_QueryBase_JSONRoot__Defaults.getInstance().set__A_QueryBase_JSONRoot__Defaults( peptideQueryJSONRoot, projectId, projectSearchIdsSet, searchIdsSet, mapProjectSearchIdToSearchId );
				
			}   //   END  ELSE of  if ( StringUtils.isNotEmpty( queryJSONFromForm ) ) {
			
			//   Update Link Type to default to Crosslink if no value was set
			String[] linkTypesInForm = peptideQueryJSONRoot.getLinkTypes();
			if ( linkTypesInForm == null || linkTypesInForm.length == 0 ) {
				String[] linkTypesCrosslink = { PeptideViewLinkTypesConstants.CROSSLINK_PSM };
				linkTypesInForm = linkTypesCrosslink;
				peptideQueryJSONRoot.setLinkTypes( linkTypesInForm );
			}

			if ( search.isHasScanData() ) {
				viewSearchPeptidesPageDataRoot.setShowNumberUniquePSMs( true );
			}
			request.setAttribute( "queryString",  request.getQueryString() );
			/////////////////////
			//  clear out form so value doesn't go back on the page in the form
			form.setQueryJSON( "" );
			////  Put Updated queryJSON on the page
			{
				try {
					String queryJSONToForm = jacksonJSON_Mapper.writeValueAsString( peptideQueryJSONRoot );
					//  Set queryJSON in request attribute to put on page outside of form
					viewSearchPeptidesPageDataRoot.setQueryJSONToForm( queryJSONToForm );
				} catch ( JsonProcessingException e ) {
					String msg = "Failed to write as JSON 'queryJSONToForm', JsonProcessingException.  queryJSONFromForm: " + queryJSONFromForm;
					log.error( msg, e );
					throw new ProxlWebappDataException( msg, e );
				} catch ( Exception e ) {
					String msg = "Failed to write as JSON 'queryJSONToForm', Exception.  queryJSONFromForm: " + queryJSONFromForm;
					log.error( msg, e );
					throw new ProxlWebappDataException( msg, e );
				}
			}
			//  Create data for Links for Image and Structure pages and put in request
			PopulateRequestDataForImageAndStructureAndQC_NavLinks.getInstance()
			.populateRequestDataForImageAndStructureAndQC_NavLinksForPeptide( peptideQueryJSONRoot, projectId, authAccessLevel, form, request );
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