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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.ProjectDAO;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import org.yeastrc.xlink.www.web_utils.MarshalObjectToJSON;
import org.yeastrc.xlink.www.webservices_utils.Unmarshal_RestRequest_JSON_ToObject;

@Path("/project/userCustomLabelAdmin")
public class ProjectUserCustomLabelAdmin_Get {

	private static final Logger log = LoggerFactory.getLogger( ProjectUserCustomLabelAdmin_Get.class );
		
	/**
	 * @param projectId
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces( MediaType.APPLICATION_JSON )
	@Path("/getData")
	public byte[] getData( 
			byte[] requestJSONBytes,
			@Context HttpServletRequest request ) throws Exception {
		try {
			if ( requestJSONBytes == null || requestJSONBytes.length == 0 ) {
				String msg = "requestJSONBytes is null or requestJSONBytes is empty";
				log.warn( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
//			    	        .entity(  )
			    	        .build()
			    	        );
			}
			WebServiceRequest webServiceRequest =
					Unmarshal_RestRequest_JSON_ToObject.getInstance()
					.getObjectFromJSONByteArray(requestJSONBytes, WebServiceRequest.class );
			
			Integer projectId = webServiceRequest.projectId;
			
			if ( projectId == null || projectId == 0 ) {
				String msg = "Provided projectId is null or zero, is = " + projectId;
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
//			UserSession userSession = accessAndSetupWebSessionResult.getUserSession();
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
			if ( ! authAccessLevel.isAssistantProjectOwnerAllowed()
					&& ! authAccessLevel.isAssistantProjectOwnerIfProjectNotLockedAllowed() ) {
				//  No Access Allowed for this project id
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			///    Done Processing Auth Check and Auth Level
			//////////////////////////////
			
			String labelText = ProjectDAO.getInstance().get_ShortName_ForId( projectId );

    		
    		WebserviceResult webserviceResult = new WebserviceResult();
    		webserviceResult.status = true;
    		webserviceResult.labelText = labelText;
    		
    		byte[] responseAsJSON = MarshalObjectToJSON.getInstance().getJSONByteArray( webserviceResult );
    		
    		return responseAsJSON;
			
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}


	/**
	 * Webservice request JSON Mapping
	 */
	public static class WebServiceRequest {
		private Integer projectId;

		public Integer getProjectId() {
			return projectId;
		}
	}
	
	/**
	 * Webservice response JSON Mapping
	 */
    public static class WebserviceResult {

    	private boolean status;
    	private String labelText;

		public boolean isStatus() {
			return status;
		}
		public String getLabelText() {
			return labelText;
		}
    }	
}
