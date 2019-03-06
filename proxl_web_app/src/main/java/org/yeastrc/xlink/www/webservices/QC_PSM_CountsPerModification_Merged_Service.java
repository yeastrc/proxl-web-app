package org.yeastrc.xlink.www.webservices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.qc_data.modification_statistics_merged.main.QC_PSM_CountsPerModification_Merged;
import org.yeastrc.xlink.www.qc_data.modification_statistics_merged.objects.QC_PSM_CountsPerModification_Merged_Results;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;


@Path("/qc/dataPage")
public class QC_PSM_CountsPerModification_Merged_Service {

	private static final Logger log = Logger.getLogger(QC_PSM_CountsPerModification_Merged_Service.class);

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/psmCountsPerModification_Merged") 
	public WebserviceResult_getQC_psmCountsPerModification_Merged
		getQC_psmCountsPerModification_Merged_GET( 
				@QueryParam( "project_search_id" ) List<Integer> projectSearchIdList,
				@QueryParam( "filterCriteria" ) String filterCriteria_JSONString,
				@Context HttpServletRequest request ) {
	
		return getQC_psmCountsPerModification_Merged_Internal( projectSearchIdList, filterCriteria_JSONString, request );
	}

	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/psmCountsPerModification_Merged") 
	public WebserviceResult_getQC_psmCountsPerModification_Merged
		getQC_psmCountsPerModification_Merged_POST( 
				@FormParam( "project_search_id" ) List<Integer> projectSearchIdList,
				@FormParam( "filterCriteria" ) String filterCriteria_JSONString,
				@Context HttpServletRequest request ) {
	
		return getQC_psmCountsPerModification_Merged_Internal( projectSearchIdList, filterCriteria_JSONString, request );
	}
	
	/**
	 * @param projectSearchIdList
	 * @param filterCriteria_JSONString
	 * @param request
	 * @return
	 */
	private WebserviceResult_getQC_psmCountsPerModification_Merged
		getQC_psmCountsPerModification_Merged_Internal( 
				List<Integer> projectSearchIdList,
				String filterCriteria_JSONString,
				HttpServletRequest request ) {
	
		if ( projectSearchIdList == null || projectSearchIdList.isEmpty() ) {
			String msg = "Provided project_search_id is null or project_search_id is missing";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		if ( StringUtils.isEmpty( filterCriteria_JSONString ) ) {
			String msg = "Provided filterCriteria is null or filterCriteria is missing";
			log.error( msg );
			throw new WebApplicationException(
					Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
					.entity( msg )
					.build()
					);
		}
		try {
			// Get the session first.  
//			HttpSession session = request.getSession();
			//   Get the project id for this search
			//   Get the project id for these searches
			Set<Integer> projectSearchIdsSet = new HashSet<Integer>( );
			for ( int projectSearchId : projectSearchIdList ) {
				projectSearchIdsSet.add( projectSearchId );
			}
			List<Integer> projectSearchIdsListDeduppedSorted = new ArrayList<>( projectSearchIdsSet );
			Collections.sort( projectSearchIdsListDeduppedSorted );

			List<Integer> projectIdsFromProjectSearchIds = 
					ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsSet );
			if ( projectIdsFromProjectSearchIds.isEmpty() ) {
				// should never happen
				@SuppressWarnings("unchecked")
				String msg = "No project ids for project search ids: " + StringUtils.join( projectSearchIdList );
				log.error( msg );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			if ( projectIdsFromProjectSearchIds.size() > 1 ) {
				//  Invalid request, searches across projects
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			int projectId = projectIdsFromProjectSearchIds.get( 0 );
			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
//			UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			//  Test access to the project id
			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();
			//  Test access to the project id
			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {
				//  No Access Allowed for this project id
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			////////   Auth complete
			//////////////////////////////////////////
			

			Set<Integer> projectSearchIdsProcessedFromForm = new HashSet<>(); // add each projectSearchId as process in loop next
			
			List<SearchDTO> searches = new ArrayList<SearchDTO>();
			Map<Integer, SearchDTO> searchesMapOnSearchId = new HashMap<>();
			int[] searchIdsArray = new int[ projectSearchIdsListDeduppedSorted.size() ];
			int searchIdsArrayIndex = 0;
			for ( int projectSearchId : projectSearchIdsListDeduppedSorted ) {
				if ( projectSearchIdsProcessedFromForm.add( projectSearchId ) ) {
					//  Haven't processed this projectSearchId yet in this loop so process it now
					SearchDTO search = SearchDAO.getInstance().getSearchFromProjectSearchId( projectSearchId );
					if ( search == null ) {
						String msg = "projectSearchId '" + projectSearchId + "' not found in the database. User taken to home page.";
						log.warn( msg );
						//  Search not found, the data on the page they are requesting does not exist.
					    throw new WebApplicationException(
					    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
					    	        .entity( msg )
					    	        .build()
					    	        );			
					}
					searches.add( search );
					searchesMapOnSearchId.put( search.getSearchId(), search );
					searchIdsArray[ searchIdsArrayIndex ] = search.getSearchId();
					searchIdsArrayIndex++;
				}
			}
			
			QC_PSM_CountsPerModification_Merged_Results qc_PSM_CountsPerModification_Merged_Results = 
					QC_PSM_CountsPerModification_Merged.getInstance()
					.getQC_PSM_CountsPerModification_Merged(
							filterCriteria_JSONString, searches );

			WebserviceResult_getQC_psmCountsPerModification_Merged serviceResult = new WebserviceResult_getQC_psmCountsPerModification_Merged();
			
			serviceResult.qc_PSM_CountsPerModification_Merged_Results = qc_PSM_CountsPerModification_Merged_Results;
			
			return serviceResult;
			
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( ProxlWebappDataException e ) {
			String msg = "Exception processing request data, msg: " + e.toString();
			log.error( msg, e );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );			
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT ) // This string will be passed to the client
					.build()
					);
		}
	}
	
	/**
	 * 
	 *
	 */
	public static class WebserviceResult_getQC_psmCountsPerModification_Merged {
		private QC_PSM_CountsPerModification_Merged_Results qc_PSM_CountsPerModification_Merged_Results;

		public QC_PSM_CountsPerModification_Merged_Results getQc_PSM_CountsPerModification_Merged_Results() {
			return qc_PSM_CountsPerModification_Merged_Results;
		}

		public void setQc_PSM_CountsPerModification_Merged_Results(
				QC_PSM_CountsPerModification_Merged_Results qc_PSM_CountsPerModification_Merged_Results) {
			this.qc_PSM_CountsPerModification_Merged_Results = qc_PSM_CountsPerModification_Merged_Results;
		}

	}
	
}