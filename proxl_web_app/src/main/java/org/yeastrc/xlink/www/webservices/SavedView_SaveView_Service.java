package org.yeastrc.xlink.www.webservices;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.data_page_saved_view.GetURLAfterWebappServletContextFromPageURL;
import org.yeastrc.xlink.www.database_update_with_transaction_services.DataPageSavedView_UsingDBTransactionService;
import org.yeastrc.xlink.www.dto.DataPageSavedViewDTO;
import org.yeastrc.xlink.www.objects.GenericWebserviceResult;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.user_session_management.UserSession;
import org.yeastrc.xlink.www.webservices_utils.Unmarshal_RestRequest_JSON_ToObject;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;


@Path("/savedView")
public class SavedView_SaveView_Service {
	
	private static final Logger log = LoggerFactory.getLogger( SavedView_SaveView_Service.class);
	
	public static class WebserviceRequest {
		List<Integer> projectSearchIds; 
		String viewLabel;
		String pageCurrentURL; 
		String pageQueryJSON;
		//  REMOVED boolean setDefault;  
		public void setProjectSearchIds(List<Integer> projectSearchIds) {
			this.projectSearchIds = projectSearchIds;
		}
		public void setPageQueryJSON(String pageQueryJSON) {
			this.pageQueryJSON = pageQueryJSON;
		}
		public void setViewLabel(String viewLabel) {
			this.viewLabel = viewLabel;
		}
		public void setPageCurrentURL(String pageCurrentURL) {
			this.pageCurrentURL = pageCurrentURL;
		}
	}
	
	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/saveView")
	public GenericWebserviceResult webserviceMethod( 
			byte[] requestJSONBytes,
			@Context HttpServletRequest request ) throws Exception {
		
		if ( requestJSONBytes == null || requestJSONBytes.length == 0 ) {
			String msg = "No Request body";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		
		WebserviceRequest webserviceRequest = 
				Unmarshal_RestRequest_JSON_ToObject.getInstance() // throws WebApplicationException if fail
				.getObjectFromJSONByteArray( requestJSONBytes, WebserviceRequest.class );
		
		List<Integer> projectSearchIds = webserviceRequest.projectSearchIds;
		String viewLabel = webserviceRequest.viewLabel;
		String pageCurrentURL = webserviceRequest.pageCurrentURL; 
		String pageQueryJSON = webserviceRequest.pageQueryJSON;

		GenericWebserviceResult genericWebserviceResult = new GenericWebserviceResult();
		try {
			if ( projectSearchIds == null || projectSearchIds.isEmpty() ) {
				String msg = "Provided projectSearchIds is not provided or is empty";
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			if ( StringUtils.isEmpty( viewLabel ) ) {
				String msg = "viewLabel is empty";
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			if ( StringUtils.isEmpty( pageCurrentURL ) ) {
				String msg = "pageCurrentURL is empty";
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			if ( StringUtils.isEmpty( pageQueryJSON ) ) {
				String msg = "pageQueryJSON is empty";
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			
//			if ( setDefault && projectSearchIds.size() != 1 ) {
//				String msg = "Cannot set default when > 1 project search id.  Project search Ids: " + projectSearchIds;
//				log.error( msg );
//			    throw new WebApplicationException(
//			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
//			    	        .entity( msg )
//			    	        .build()
//			    	        );
//			}
			
			//   Get the project id for this search
			Collection<Integer> projectSearchIdsCollection = new HashSet<Integer>( projectSearchIds );
			List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsCollection );
			if ( projectIdsFromSearchIds.isEmpty() ) {
				// should never happen
				String msg = "No project ids for projectSearchIds: " + projectSearchIds;
				log.warn( msg );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			if ( projectIdsFromSearchIds.size() > 1 ) {
				//  Invalid request, searches across projects
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			int projectId = projectIdsFromSearchIds.get( 0 );
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
			UserSession userSession = accessAndSetupWebSessionResult.getUserSession();
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			//  Test access to the project id
			WebSessionAuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getWebSessionAuthAccessLevel();
			if ( ! authAccessLevel.isAssistantProjectOwnerAllowed() ) {
				//  No Access Allowed for this project id
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
//			if ( setDefault ) {
//				// Project owner access required to set default
//				if ( ! authAccessLevel.isProjectOwnerAllowed() ) {
//					//  No Access Allowed for this project id
//					throw new WebApplicationException(
//							Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
//							.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
//							.build()
//							);
//				}
//			}
			
			// Auth check complete
			
			////////////////////
			
			String urlAfterWebappServletContext = GetURLAfterWebappServletContextFromPageURL.getInstance().getURLAfterWebappServletContextFromPageURL( pageCurrentURL );
			String pageName = GetURLAfterWebappServletContextFromPageURL.getInstance().getPageNameFromStrutsActionInURL( pageCurrentURL );
			
			DataPageSavedViewDTO dataPageSavedViewDTO = new DataPageSavedViewDTO();
			
			dataPageSavedViewDTO.setLabel( viewLabel );
			dataPageSavedViewDTO.setPageName( pageName );
			dataPageSavedViewDTO.setUrlStartAtPageName( urlAfterWebappServletContext );
			dataPageSavedViewDTO.setPageQueryJSONString(pageQueryJSON);
			dataPageSavedViewDTO.setProjectId( projectId );
			dataPageSavedViewDTO.setAuthUserIdCreated( userSession.getAuthUserId() );
			dataPageSavedViewDTO.setAuthUserIdLastUpdated( userSession.getAuthUserId() );
			DataPageSavedView_UsingDBTransactionService.getInstance()
			.addNew_dataPageSavedView_UsingDBTransactionService( dataPageSavedViewDTO, projectSearchIds );
			
			genericWebserviceResult.setStatus(true);
			return genericWebserviceResult;
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}
}
