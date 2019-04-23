package org.yeastrc.xlink.www.webservices;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.DataPageSavedViewDAO;
import org.yeastrc.xlink.www.dto.DataPageSavedViewDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.www.objects.GenericWebserviceResult;
import org.yeastrc.xlink.www.user_session_management.UserSession;
import org.yeastrc.xlink.www.webservices_utils.Unmarshal_RestRequest_JSON_ToObject;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;


@Path("/savedView")
public class SavedView_Delete_Service {
	
	private static final Logger log = LoggerFactory.getLogger( SavedView_Delete_Service.class);
	
	public static class WebserviceRequest {
    	private Integer id;
		public void setId(Integer id) {
			this.id = id;
		}
	}
	
	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/delete")
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
		
		Integer savedViewId = webserviceRequest.id;

		GenericWebserviceResult genericWebserviceResult = new GenericWebserviceResult();
		try {
			if ( savedViewId == null || savedViewId == 0 ) {
				String msg = "Provided projectSearchIds is not provided or is zero";
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}

    		DataPageSavedViewDTO dataPageSavedViewDTO = DataPageSavedViewDAO.getInstance().getNumericFieldsById( savedViewId );
    		if ( dataPageSavedViewDTO == null ) {
    			log.warn( "Id (savedViewId) not in DB: " + savedViewId );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        // .entity( msg )
			    	        .build()
			    	        );
    		}

    		int projectId = dataPageSavedViewDTO.getProjectId();

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

    		Integer authUserId = null;
    		
    		if ( userSession != null ) {
    			authUserId = userSession.getAuthUserId();
    		}
    		if ( authUserId == null ) {
    			throw new ProxlWebappInternalErrorException( "userId == null and passed access check" );
    		}
			if ( authAccessLevel.isProjectOwnerAllowed() ) {
				// Project owner can change or delete this entry
			} else if ( authAccessLevel.isAssistantProjectOwnerAllowed() ) {
				if ( dataPageSavedViewDTO.getAuthUserIdCreated() != authUserId ) {
    				log.warn( "Access Error: Researcher can only delete own saved views, throwing NOT_AUTHORIZED_STATUS_CODE. savedViewId: " + savedViewId + ". userId: " + authUserId );
    				throw new WebApplicationException(
    						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
    						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
    						.build()
    						);
    			}
    		}
			
			// Auth check complete
			
			////////////////////
    		
    		DataPageSavedViewDAO.getInstance().delete( savedViewId );
    		
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
