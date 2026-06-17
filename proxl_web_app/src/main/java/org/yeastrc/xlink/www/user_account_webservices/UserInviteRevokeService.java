package org.yeastrc.xlink.www.user_account_webservices;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.auth.dao.AuthUserInviteTrackingDAO;
import org.yeastrc.auth.dto.AuthUserInviteTrackingDTO;
import org.yeastrc.xlink.www.constants.AuthAccessLevelConstants;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.objects.GenericWebserviceResult;
import org.yeastrc.xlink.www.user_session_management.UserSession;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;



@Path("/user")
public class UserInviteRevokeService {

	private static final Logger log = LoggerFactory.getLogger( UserInviteRevokeService.class);
	
	
	
	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/revokeInvite") 
	public GenericWebserviceResult revokeInvite(   @FormParam( "inviteId" ) String inviteIdString,
			@Context HttpServletRequest request )
	throws Exception {
		
		//  Restricted to users with ACCESS_LEVEL_ASSISTANT_PROJECT_OWNER or better
		

		int inviteId = 0;
		
		
		if ( StringUtils.isEmpty( inviteIdString ) ) {

			log.warn( "RevokeInviteService:  inviteId empty: " + inviteIdString );

			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		

		try {
			
			inviteId = Integer.parseInt( inviteIdString );
			
		} catch (Exception e) {

			log.warn( "RevokeInviteService:  inviteId is not valid integer: " + inviteIdString, e );

			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		

		
		

		
		
		
//		if (true)
//		throw new Exception("Forced Error");
		
		try {
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionNoProjectId( request );

			if ( accessAndSetupWebSessionResult.isNoSession() ) {

				//  No User session 

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			UserSession userSession = accessAndSetupWebSessionResult.getUserSession();

			if ( userSession == null || ( ! userSession.isActualUser() ) ) {

				//  No User session 

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			//  Test access at global level

			WebSessionAuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getWebSessionAuthAccessLevel();

			
			AuthUserInviteTrackingDAO authUserInviteTrackingDAO = AuthUserInviteTrackingDAO.getInstance();
			
			
			AuthUserInviteTrackingDTO inviteDTO = authUserInviteTrackingDAO.getForInviteTrackingId( inviteId );
			
			//  TODO  Test for already used, already revoked, 
			
			if ( inviteDTO == null ) {
				
				
				
				
			}
			
			if ( inviteDTO.isInviteRevoked() ) {
				
				
			}
			
			if ( inviteDTO.isCodeReplacedByNewer() ) {
				
				
				
			}
			
			
			
			Integer invitedSharedObjectId = inviteDTO.getInvitedSharedObjectId();
			
			
			//  Check for Admin global access level
			
			if ( authAccessLevel.isAdminAllowed() ) {
				
				//  Always allow if Admin
				
			} else {


				if ( invitedSharedObjectId == null ) {
					
					if ( ! authAccessLevel.isAdminAllowed() ) {

						//  No Access Allowed at global access level

						throw new WebApplicationException(
								Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
								.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
								.build()
								);
					}

				} else {
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result getWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result_invitedSharedObjectId =
							GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance()
							.getAccessAndSetupWebSessionWithProjectId( invitedSharedObjectId, request );

					authAccessLevel = getWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result_invitedSharedObjectId.getWebSessionAuthAccessLevel();


					if ( ! authAccessLevel.isAssistantProjectOwnerAllowed() ) {

						//  No Access Allowed for this project

						throw new WebApplicationException(
								Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
								.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
								.build()
								);
					}


					if ( ! authAccessLevel.isProjectOwnerAllowed() 
							&& inviteDTO.getInvitedUserAccessLevel() <= AuthAccessLevelConstants.ACCESS_LEVEL_PROJECT_OWNER ) {

						//  Not authorized to add a person with ACCESS_LEVEL_PROJECT_OWNER if not project owner

						//  Tested 12/12/2014 and errors properly if logged in user is Researcher and trying to add Owner
						//					if ( ! authAccessLevel.isProjectOwnerAllowed() 
						//						&& personExistingAccessLevel <= AuthAccessLevelConstants.ACCESS_LEVEL_PROJECT_OWNER  ) {

						throw new WebApplicationException(
								Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
								.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
								.build()
								);
					}

				}
			}
			

			int revokeAuthUserId = userSession.getAuthUserId();
			
			authUserInviteTrackingDAO.updateRevokedInviteFields( inviteId, revokeAuthUserId );
			
			GenericWebserviceResult genericWebserviceResult = new GenericWebserviceResult();
			
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
