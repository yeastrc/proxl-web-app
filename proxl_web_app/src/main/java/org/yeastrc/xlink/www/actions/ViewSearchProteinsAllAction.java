package org.yeastrc.xlink.www.actions;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.nav_links_image_structure.PopulateRequestDataForImageAndStructureAndQC_NavLinks;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.PeptideQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.ProteinQueryJSONRoot;
import org.yeastrc.xlink.www.form_utils.GetProteinQueryJSONRootFromFormData;
import org.yeastrc.xlink.www.forms.PeptideProteinCommonForm;
import org.yeastrc.xlink.www.forms.SearchViewProteinsForm;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import org.yeastrc.xlink.www.web_utils.ExcludeLinksWith_Remove_NonUniquePSMs_Checkbox_PopRequestItems;
import org.yeastrc.xlink.www.web_utils.GetAnnotationDisplayUserSelectionDetailsData;
import org.yeastrc.xlink.www.web_utils.GetPageHeaderData;
import org.yeastrc.xlink.www.web_utils.GetSearchDetailsData;
import org.yeastrc.xlink.www.web_utils.ProjectSearchIdsSearchIds_SetRequestParameter;
import org.yeastrc.xlink.www.web_utils.ProteinListingTooltipConfigUtil;
import org.yeastrc.xlink.www.web_utils.URLEncodeDecodeAURL;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Used for Showing all proteins on a page 
 * 
 * Separate from Crosslink Proteins and Looplink Proteins pages 
 *
 */
public class ViewSearchProteinsAllAction extends Action {
	
	private static final Logger log = LoggerFactory.getLogger( ViewSearchProteinsAllAction.class);

	/* (non-Javadoc)
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public ActionForward execute( ActionMapping mapping,
			  ActionForm actionForm,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {
		try {
			// our form
			SearchViewProteinsForm form = (SearchViewProteinsForm)actionForm;
			request.setAttribute( "searchViewProteinsForm", form );
			int projectSearchId = form.getProjectSearchIdSingle();
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
			
			//   Get Query JSON from the form and if not empty, deserialize it
			ProteinQueryJSONRoot proteinQueryJSONRoot = 
					GetProteinQueryJSONRootFromFormData.getInstance()
					.getProteinQueryJSONRootFromFormData( form, projectSearchIdsSet, searchIdsSet, mapProjectSearchIdToSearchId );
		
			
			//  Convert the protein sequence ids that come from the JS code to standard integers and put
			//   in the property excludeproteinSequenceVersionIds.
			//      Do this here since may have to convert old NRSeqProteinIds.
			ProteinsMergedProteinsCommon.getInstance().processExcludeproteinSequenceVersionIdsFromJS( proteinQueryJSONRoot );
			
			
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
			
			//  Populate request objects for User Selection of Annotation Data Display
			GetAnnotationDisplayUserSelectionDetailsData.getInstance().getSearchDetailsData( search, request );
			//  Populate request objects for excludeLinksWith_Remove_NonUniquePSMs_Checkbox_Fragment.jsp
			ExcludeLinksWith_Remove_NonUniquePSMs_Checkbox_PopRequestItems.getInstance().excludeLinksWith_Remove_NonUniquePSMs_Checkbox_PopRequestItems( search, request );

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
					peptideQueryJSONRoot.setMinPSMs( proteinQueryJSONRoot.getMinPSMs() );
					String[] peptidePageLinkTypes = null;
//					String[] peptidePageLinkTypes = new String[ 1 ];
//					if ( Struts_Config_Parameter_Values_Constants.STRUTS__PARAMETER__CROSSLINK.equals( strutsActionMappingParameter ) ) {
//						peptidePageLinkTypes[ 0 ] = XLinkUtils.CROSS_TYPE_STRING_UPPERCASE; 
//					} else if ( Struts_Config_Parameter_Values_Constants.STRUTS__PARAMETER__LOOPLINK.equals( strutsActionMappingParameter ) ) {
//						peptidePageLinkTypes[ 0 ] = XLinkUtils.LOOP_TYPE_STRING_UPPERCASE; 
//					}
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

}
