package org.yeastrc.xlink.www.webservices;

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
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.auth.services.AuthSharedObjectAdminPublicAccessCodeAndEnabled;
import org.yeastrc.xlink.www.dao.ProjectDAO;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.objects.ProjectPublicAccessData;
import org.yeastrc.xlink.www.constants.AuthAccessLevelConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.user_session_management.UserSession;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.common.AccessControl_GetUserSession_RefreshAccessEnabled;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import org.yeastrc.xlink.www.web_utils.GetProjectPublicAccessData;

@Path("/project/publicAccessAdmin")
public class ProjectPublicAccessAdminService {

	private static final Logger log = LoggerFactory.getLogger( ProjectPublicAccessAdminService.class);
	
	/**
	 * @param projectId
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getData")
	public ProjectPublicAccessData getData( @QueryParam("projectId") int projectId, @Context HttpServletRequest request ) throws Exception {
		try {
			if ( projectId == 0 ) {
				String msg = "Provided projectId is zero, is = " + projectId;
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
			ProjectPublicAccessData projectPublicAccessData =
					GetProjectPublicAccessData.getInstance().getProjectPublicAccessData( projectId );
			if ( projectPublicAccessData == null ) {
				log.warn( "projectId is not in database: " + projectId );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			return projectPublicAccessData;
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}
	
	/**
	 * @param projectId
	 * @param require_public_access_code_String
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/enable")
	public ProjectPublicAccessData enable( 
			@FormParam("projectId") int projectId, 
			@FormParam("require_public_access_code") String require_public_access_code_String, 
			@Context HttpServletRequest request ) throws Exception {
		try {
			if ( projectId == 0 ) {
				String msg = "Provided projectId is zero, is = " + projectId;
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			boolean require_public_access_code = false;
			if ( "true".equals( require_public_access_code_String ) ) {
				require_public_access_code = true;
			} else if ( "false".equals( require_public_access_code_String ) ) {
			} else {
				String msg = "Provided require_public_access_code is not 'true' or 'false', is = " + require_public_access_code_String;
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
			if ( ! authAccessLevel.isProjectOwnerAllowed() ) {
				//  No Access Allowed for this project id
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			///    Done Processing Auth Check and Auth Level
			//////////////////////////////
			Integer authShareableObjectId = ProjectDAO.getInstance().getAuthShareableObjectIdForProjectId( projectId );
			if ( authShareableObjectId == null ) {
				log.warn( "projectId is not in database: " + projectId );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			if ( require_public_access_code ) {
//				String publicAccessCode = 
				AuthSharedObjectAdminPublicAccessCodeAndEnabled.getInstance().activatePublicAccessCode( authShareableObjectId );
				Integer publicAccessLevel = null;
				ProjectDAO.getInstance().updatePublicAccessLevel( projectId, publicAccessLevel );
			} else {
				Integer publicAccessLevel = AuthAccessLevelConstants.ACCESS_LEVEL__PUBLIC_ACCESS_CODE_READ_ONLY__PUBLIC_PROJECT_READ_ONLY;
				ProjectDAO.getInstance().updatePublicAccessLevel( projectId, publicAccessLevel );
				// Commented out so:  Do NOT disable public access code if user switches to Public Access (no code required)
				//  disable public access code if user switches to Public Access
//				AuthSharedObjectAdminPublicAccessCodeAndEnabled.getInstance().deactivatePublicAccessCode( authShareableObjectId );
			}
			//  Get the updated data from the db
			ProjectPublicAccessData projectPublicAccessData =
					GetProjectPublicAccessData.getInstance().getProjectPublicAccessData( projectId );
			if ( projectPublicAccessData == null ) {
				log.warn( "projectId is not in database: " + projectId );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			return projectPublicAccessData;
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}
	
	/**
	 * @param projectId
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/disable")
	public ProjectPublicAccessData disable( @FormParam("projectId") int projectId, @Context HttpServletRequest request ) throws Exception {
		try {
			if ( projectId == 0 ) {
				String msg = "Provided projectId is zero, is = " + projectId;
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}

			UserSession userSession =
					AccessControl_GetUserSession_RefreshAccessEnabled.getSinglesonInstance()
					.getUserSession_RefreshAccessEnabled( request );
			
			if ( userSession == null || ( ! userSession.isActualUser() ) ) {
				//  No User session 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result getWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance()
					.getAccessAndSetupWebSessionWithProjectId( projectId, request );
			WebSessionAuthAccessLevel authAccessLevel = getWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result.getWebSessionAuthAccessLevel();
			if ( ! authAccessLevel.isProjectOwnerAllowed() ) {
				//  No Access Allowed for this project id
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			///    Done Processing Auth Check and Auth Level
			//////////////////////////////
			Integer authShareableObjectId = ProjectDAO.getInstance().getAuthShareableObjectIdForProjectId( projectId );
			if ( authShareableObjectId == null ) {
				log.warn( "projectId is not in database: " + projectId );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			AuthSharedObjectAdminPublicAccessCodeAndEnabled.getInstance().deactivatePublicAccessCode( authShareableObjectId );
			Integer publicAccessLevel = null;
			ProjectDAO.getInstance().updatePublicAccessLevel( projectId, publicAccessLevel );
			//  Get the updated data from the db
			ProjectPublicAccessData projectPublicAccessData =
					GetProjectPublicAccessData.getInstance().getProjectPublicAccessData( projectId );
			if ( projectPublicAccessData == null ) {
				log.warn( "projectId is not in database: " + projectId );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			return projectPublicAccessData;
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}
	
	/**
	 * @param projectId
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/generateNewPublicAccessCode")
	public ProjectPublicAccessData regenerate( @FormParam("projectId") int projectId, @Context HttpServletRequest request ) throws Exception {
		try {
			if ( projectId == 0 ) {
				String msg = "Provided projectId is zero, is = " + projectId;
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}

			UserSession userSession =
					AccessControl_GetUserSession_RefreshAccessEnabled.getSinglesonInstance()
					.getUserSession_RefreshAccessEnabled( request );
			
			if ( userSession == null || ( ! userSession.isActualUser() ) ) {
				//  No User session 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result getWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance()
					.getAccessAndSetupWebSessionWithProjectId( projectId, request );
			WebSessionAuthAccessLevel authAccessLevel = getWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result.getWebSessionAuthAccessLevel();
			if ( ! authAccessLevel.isProjectOwnerAllowed() ) {
				//  No Access Allowed for this project id
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			///    Done Processing Auth Check and Auth Level
			//////////////////////////////
			Integer authShareableObjectId = ProjectDAO.getInstance().getAuthShareableObjectIdForProjectId( projectId );
			if ( authShareableObjectId == null ) {
				log.warn( "projectId is not in database: " + projectId );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}
//			String publicAccessCode = 
			AuthSharedObjectAdminPublicAccessCodeAndEnabled.getInstance().regeneratePublicAccessCode( authShareableObjectId );
			//  Get the updated data from the db
			ProjectPublicAccessData projectPublicAccessData =
					GetProjectPublicAccessData.getInstance().getProjectPublicAccessData( projectId );
			if ( projectPublicAccessData == null ) {
				log.warn( "projectId is not in database: " + projectId );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			return projectPublicAccessData;
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}
	
	/**
	 * @param projectId
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/lock")
	public ProjectPublicAccessData lock( @FormParam("projectId") int projectId, @Context HttpServletRequest request ) throws Exception {
		try {
			if ( projectId == 0 ) {
				String msg = "Provided projectId is zero, is = " + projectId;
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}

			UserSession userSession =
					AccessControl_GetUserSession_RefreshAccessEnabled.getSinglesonInstance()
					.getUserSession_RefreshAccessEnabled( request );
			
			if ( userSession == null || ( ! userSession.isActualUser() ) ) {
				//  No User session 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result getWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance()
					.getAccessAndSetupWebSessionWithProjectId( projectId, request );
			WebSessionAuthAccessLevel authAccessLevel = getWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result.getWebSessionAuthAccessLevel();
			if ( ! authAccessLevel.isProjectOwnerAllowed() ) {
				//  No Access Allowed for this project id
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			///    Done Processing Auth Check and Auth Level
			//////////////////////////////
			Integer authShareableObjectId = ProjectDAO.getInstance().getAuthShareableObjectIdForProjectId( projectId );
			if ( authShareableObjectId == null ) {
				log.warn( "projectId is not in database: " + projectId );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			ProjectDAO.getInstance().updatePublicAccessLocked( projectId, true /* publicAccessLocked */ );
			//  Get the updated data from the db
			ProjectPublicAccessData projectPublicAccessData =
					GetProjectPublicAccessData.getInstance().getProjectPublicAccessData( projectId );
			if ( projectPublicAccessData == null ) {
				log.warn( "projectId is not in database: " + projectId );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			return projectPublicAccessData;
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}
	
	/**
	 * @param projectId
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/unlock")
	public ProjectPublicAccessData unlock( @FormParam("projectId") int projectId, @Context HttpServletRequest request ) throws Exception {
		try {
			if ( projectId == 0 ) {
				String msg = "Provided projectId is zero, is = " + projectId;
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}

			UserSession userSession =
					AccessControl_GetUserSession_RefreshAccessEnabled.getSinglesonInstance()
					.getUserSession_RefreshAccessEnabled( request );
			
			if ( userSession == null || ( ! userSession.isActualUser() ) ) {
				//  No User session 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result getWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance()
					.getAccessAndSetupWebSessionWithProjectId( projectId, request );
			WebSessionAuthAccessLevel authAccessLevel = getWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result.getWebSessionAuthAccessLevel();
			if ( ! authAccessLevel.isProjectOwnerAllowed() ) {
				//  No Access Allowed for this project id
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			///    Done Processing Auth Check and Auth Level
			//////////////////////////////
			Integer authShareableObjectId = ProjectDAO.getInstance().getAuthShareableObjectIdForProjectId( projectId );
			if ( authShareableObjectId == null ) {
				log.warn( "projectId is not in database: " + projectId );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			ProjectDAO.getInstance().updatePublicAccessLocked( projectId, false /* publicAccessLocked */ );
			//  Get the updated data from the db
			ProjectPublicAccessData projectPublicAccessData =
					GetProjectPublicAccessData.getInstance().getProjectPublicAccessData( projectId );
			if ( projectPublicAccessData == null ) {
				log.warn( "projectId is not in database: " + projectId );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			return projectPublicAccessData;
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}
}
