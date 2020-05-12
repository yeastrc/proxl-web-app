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
import org.yeastrc.xlink.www.dao.ConfigSystemDAO;
import org.yeastrc.xlink.www.form_query_json_objects.ImageStructure_QC_QueryJSONRoot;
import org.yeastrc.xlink.www.forms.MergedSearchViewProteinsForm;
import org.yeastrc.xlink.www.forms.PeptideProteinCommonForm;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.objects.ProteinSequenceVersionIdProteinAnnotationName;
import org.yeastrc.xlink.www.constants.ConfigSystemsKeysConstants;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.cutoff_processing_web.Set__A_QueryBase_JSONRoot__Defaults;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.ProteinSequenceVersionIdAnnotationNameSearcher;
import org.yeastrc.xlink.www.searcher.SearchModMassDistinctSearcher;
import org.yeastrc.xlink.www.user_session_management.UserSession;
import org.yeastrc.xlink.www.user_session_management.UserSessionManager;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import org.yeastrc.xlink.www.web_utils.AnyPDBFilesForProjectId;
import org.yeastrc.xlink.www.web_utils.CombineProteinAnnNamesForSameSeqVId;
import org.yeastrc.xlink.www.web_utils.GetAnnotationDisplayUserSelectionDetailsData;
import org.yeastrc.xlink.www.web_utils.GetCutoffsAppliedOnImport;
import org.yeastrc.xlink.www.web_utils.GetMinimumPSMsDefaultForProject_PutInRequestScope;
import org.yeastrc.xlink.www.web_utils.GetPageHeaderData;
import org.yeastrc.xlink.www.web_utils.GetSearchDetailsData;
import org.yeastrc.xlink.www.web_utils.ProjectSearchIdsSearchIds_SetRequestParameter;
import org.yeastrc.xlink.www.web_utils.ProteinListingTooltipConfigUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * QC data page - Single and Multiple searches
 *
 */
public class ViewMergedSearchQCAction extends Action {
	
	private static final Logger log = LoggerFactory.getLogger( ViewMergedSearchQCAction.class);
	
	private static final String STRUTS_PARAMETER_VALUE_ALEX = "Alex";
	
	@Override
	public ActionForward execute( ActionMapping mapping,
			  ActionForm actionForm,
			  HttpServletRequest request,
			  HttpServletResponse response ) throws Exception {

		Integer projectId = null;

		try {
			//  Detect which Struts action mapping was called by examining the value of the "parameter" attribute
			//     accessed by calling mapping.getParameter()
			String strutsActionMappingParameter = mapping.getParameter();
			
			boolean strutsParameterAlex = false;
			
			if ( STRUTS_PARAMETER_VALUE_ALEX.equals(strutsActionMappingParameter) ) {
				strutsParameterAlex = true;
			}
			
			request.setAttribute( "strutsParameterAlex", strutsParameterAlex );
			
			
			
			MergedSearchViewProteinsForm form = (MergedSearchViewProteinsForm) actionForm;
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
					return mapping.findForward( StrutsGlobalForwardNames.NO_USER_SESSION );
				}
				return mapping.findForward( StrutsGlobalForwardNames.INSUFFICIENT_ACCESS_PRIVILEGE );
			}
			request.setAttribute( WebConstants.REQUEST_AUTH_ACCESS_LEVEL, authAccessLevel );

			///    Done Processing Auth Check and Auth Level
			//////////////////////////////

			if ( projectSearchIdsListDeduppedSorted.size() == 1 ) {
				int onlySingleProjectSearchId = projectSearchIdsListDeduppedSorted.get( 0 );
				request.setAttribute( "onlySingleProjectSearchId", onlySingleProjectSearchId );	
			}

			Set<Integer> projectSearchIdsProcessedFromForm = new HashSet<>(); // add each projectSearchId as process in loop next
			
			boolean anySearchesHaveScanData = true;
			
			List<SearchDTO> searches = new ArrayList<SearchDTO>();
			Map<Integer, SearchDTO> searchesMapOnSearchId = new HashMap<>();

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

					searchIdsArray[ searchIdsArrayIndex ] = search.getSearchId();
					searchIdsArrayIndex++;
					
					if ( ! search.isHasScanData() ) {
						anySearchesHaveScanData = false;
					}
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

			//  Create projectSearchIdsList in the same order as the searches
			List<Integer> projectSearchIdsList = new ArrayList<Integer>();

			Collection<Integer> searchIds = new HashSet<>();
			Map<Integer,Integer> mapProjectSearchIdToSearchId = new HashMap<>();
			List<Integer> searchIdsListDeduppedSorted = new ArrayList<>( searches.size() );
			
			for ( SearchDTO search : searches ) {
				searchIds.add( search.getSearchId() );
				searchIdsListDeduppedSorted.add( search.getSearchId() );
				mapProjectSearchIdToSearchId.put( search.getProjectSearchId(), search.getSearchId() );

				projectSearchIdsList.add( search.getProjectSearchId() );
			}

			request.setAttribute( "projectSearchIds", projectSearchIdsList );

			
			request.setAttribute( "anySearchesHaveScanData", anySearchesHaveScanData );
			

			request.setAttribute( "searches", searches );
			
			if ( PeptideProteinCommonForm.DO_NOT_SORT_PROJECT_SEARCH_IDS_YES.equals( form.getDs() ) ) {
				request.setAttribute( "userOrderedProjectSearchIds", PeptideProteinCommonForm.DO_NOT_SORT_PROJECT_SEARCH_IDS_YES );
			}
			
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
			
			boolean showStructureLink = true;
			if ( authAccessLevel.isAssistantProjectOwnerAllowed()
					|| authAccessLevel.isAssistantProjectOwnerIfProjectNotLockedAllowed() ) {
			} else {
				//  Public access user:
				showStructureLink = AnyPDBFilesForProjectId.getInstance().anyPDBFilesForProjectId( projectId );
			}
			request.setAttribute( WebConstants.REQUEST_SHOW_STRUCTURE_LINK, showStructureLink );
			String annotation_data_webservice_base_url = 
					ConfigSystemDAO.getInstance().getConfigValueForConfigKey( ConfigSystemsKeysConstants.PROTEIN_ANNOTATION_WEBSERVICE_URL_KEY );
			request.setAttribute( "annotation_data_webservice_base_url", annotation_data_webservice_base_url );

			List<Double> modMassDistinctForSearchesList = SearchModMassDistinctSearcher.getInstance().getDistinctDynamicModMassesForSearchId( searchIdsArray );
			List<String> modMassStringsList = new ArrayList<>( modMassDistinctForSearchesList.size() );
			for ( Double modMass : modMassDistinctForSearchesList ) {
				String modMassAsString = modMass.toString();
				modMassStringsList.add( modMassAsString );
			}
			request.setAttribute( "modMassFilterList", modMassStringsList );
			

			//  Get  proteinSequenceVersionId and AnnotationName for search
			
			Set<ProteinSequenceVersionIdProteinAnnotationName> proteinSequenceVersionIdProteinAnnotationNameSet = null;
			
			for ( Integer searchId : searchIds ) {
				Set<ProteinSequenceVersionIdProteinAnnotationName> proteinSequenceVersionIdProteinAnnotationNameSet_SingleSearch = 
						ProteinSequenceVersionIdAnnotationNameSearcher.getInstance()
						.getProteinSequenceVersionIdAnnotationNameForSearch( searchId );

				if ( proteinSequenceVersionIdProteinAnnotationNameSet == null ) {
					proteinSequenceVersionIdProteinAnnotationNameSet = proteinSequenceVersionIdProteinAnnotationNameSet_SingleSearch;
				} else {
					proteinSequenceVersionIdProteinAnnotationNameSet.addAll( proteinSequenceVersionIdProteinAnnotationNameSet_SingleSearch );
				}
			}
			
			Set<ProteinSequenceVersionIdProteinAnnotationName> proteinSequenceVersionIdProteinAnnotationNameCombinedSet = 
				CombineProteinAnnNamesForSameSeqVId.getInstance()
				.combineProteinAnnNamesForSameSeqVId( proteinSequenceVersionIdProteinAnnotationNameSet );
			
			List<ProteinSequenceVersionIdProteinAnnotationName> proteinSequenceVersionIdProteinAnnotationNameList =
					new ArrayList<>( proteinSequenceVersionIdProteinAnnotationNameCombinedSet );

			Collections.sort( proteinSequenceVersionIdProteinAnnotationNameList, new Comparator<ProteinSequenceVersionIdProteinAnnotationName>() {
				@Override
				public int compare(ProteinSequenceVersionIdProteinAnnotationName o1, ProteinSequenceVersionIdProteinAnnotationName o2) {
					return o1.getAnnotationName().compareToIgnoreCase( o2.getAnnotationName() );
				}
			});
			
			request.setAttribute( "proteinIdsAndNames", proteinSequenceVersionIdProteinAnnotationNameList );
			
			if ( searchIdsArray.length == 1 ) {
				String cutoffsAppliedOnImportAllAsString =
						GetCutoffsAppliedOnImport.getInstance().getCutoffsAppliedOnImportAllAsString( searchIdsArray[ 0 ] );
				request.setAttribute( "cutoffsAppliedOnImportAllAsString", cutoffsAppliedOnImportAllAsString );
//			} else {
//				String msg = "Code for cutoffsAppliedOnImportAllAsString currently only supports 1 search id.";
//				log.error( msg );
//				throw new ProxlWebappInternalErrorException(msg);
			}
			
			{
				/////////////////////////////////////////////////////////////////////////////
				////////   Defaults to put on page for Javascript to read
				ImageStructure_QC_QueryJSONRoot defaultsQueryJSON = new ImageStructure_QC_QueryJSONRoot();
				Set__A_QueryBase_JSONRoot__Defaults.getInstance().set__A_QueryBase_JSONRoot__Defaults( defaultsQueryJSON, projectId, projectSearchIdsListDeduppedSorted, searchIdsListDeduppedSorted, mapProjectSearchIdToSearchId) ;
				String defaultsQueryJSONString = jacksonJSON_Mapper.writeValueAsString( defaultsQueryJSON );
				request.setAttribute( "default_values_cutoffs_others", defaultsQueryJSONString );
			}
			
			//  So certain sections treat this like coverage page.  ie: no selection of annotation data to display
			request.setAttribute( "coveragePageForAnnDispMgmt", true );
			
			if ( searchIdsArray.length == 1 ) {
				return mapping.findForward( "SuccessSingle" );
			}
			
			return mapping.findForward( "SuccessMerged" );

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