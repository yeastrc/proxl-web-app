package org.yeastrc.xlink.www.user_account_webservices;


import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.auth.dao.AuthSharedObjectUsersDAO;
import org.yeastrc.auth.dto.AuthSharedObjectUsersDTO;
import org.yeastrc.xlink.www.constants.AuthAccessLevelConstants;
import org.yeastrc.xlink.www.dao.ProjectDAO;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.objects.UpdateUserAccessToProjectResult;
import org.yeastrc.xlink.www.user_session_management.UserSession;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import org.yeastrc.xlink.www.user_web_utils.ValidateUserAccessLevel;



@Path("/user")
public class UpdateUserAccessToProjectService {

	private static final Logger log = LoggerFactory.getLogger( UpdateUserAccessToProjectService.class);
	
	
	
	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/updateAccessToProject") 
	public UpdateUserAccessToProjectResult updateUserAccessToProject(   @FormParam( "personId" ) String personIdString,
			@FormParam( "personAccessLevel" ) String personAccessLevelString,
			@FormParam( "projectId" ) String projectIdString,
			@Context HttpServletRequest request )
	throws Exception {
		
		//  Restricted to users with ACCESS_LEVEL_ASSISTANT_PROJECT_OWNER or better
		

		int personId = 0;
		int projectId = 0;
		int personAccessLevel = 0;
		
		
		if ( StringUtils.isEmpty( personIdString ) ) {

			log.warn( "UpdateUserAccessToProjectService:  personId empty: " + personIdString );

			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		

		try {
			
			personId = Integer.parseInt( personIdString );
			
		} catch (Exception e) {

			log.warn( "UpdateUserAccessToProjectService:  personId is not valid integer: " + personIdString, e );

			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		

		if ( StringUtils.isEmpty( personAccessLevelString ) ) {

			log.warn( "UpdateUserAccessToProjectService:  addPersonAccessLevel empty: " + personAccessLevelString );

			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}

		try {
			
			personAccessLevel = Integer.parseInt( personAccessLevelString );
			
		} catch (Exception e) {

			log.warn( "UpdateUserAccessToProjectService:  invitedPersonAccessLevel is not valid integer: " + personAccessLevelString, e );

			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		

		if ( StringUtils.isEmpty( projectIdString ) ) {

			log.warn( "UpdateUserAccessToProjectService:  projectId empty: " + projectIdString );

			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		

		try {

			projectId = Integer.parseInt( projectIdString );

		} catch (Exception e) {

			log.warn( "UpdateUserAccessToProjectService:  projectId is not valid integer: " + projectIdString, e );

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
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );

			if ( accessAndSetupWebSessionResult.isNoSession() ) {

				//  No User session 

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			UserSession userSession = accessAndSetupWebSessionResult.getUserSession();
			
			
			WebSessionAuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getWebSessionAuthAccessLevel();

						
			if ( userSession.getAuthUserId() == personId ) {

				//  Not allowed to update own access
				
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}

			
			//  Test access to the project id


			if ( ! authAccessLevel.isAssistantProjectOwnerAllowed() ) {

				//  No Access Allowed for this project id

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}

			if ( ! authAccessLevel.isProjectOwnerAllowed() 
					&& personAccessLevel <= AuthAccessLevelConstants.ACCESS_LEVEL_ASSISTANT_PROJECT_OWNER_AKA_RESEARCHER ) {

				//  Not authorized to update a person to this access level if not project owner

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			

			Integer projectAuthShareableObjectIdFromDB = ProjectDAO.getInstance().getAuthShareableObjectIdForProjectId( projectId );
			
			if ( projectAuthShareableObjectIdFromDB == null ) {

				log.warn( "UpdateUserAccessToProjectService:  projectId is not in database: " + projectIdString );

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			

			ValidateUserAccessLevel.UserAccessLevelTypes userAccessLevelType = ValidateUserAccessLevel.UserAccessLevelTypes.GLOBAL;
			
			if ( projectAuthShareableObjectIdFromDB != null ) {
				
				userAccessLevelType = ValidateUserAccessLevel.UserAccessLevelTypes.PROJECT;
			}
			
			if ( ! ValidateUserAccessLevel.validateUserAccessLevel( personAccessLevel, userAccessLevelType ) ) {
				
				//  The personAccessLevel is not valid

				log.warn( "UserInviteService:  personAccessLevel is not valid: " + personAccessLevel );

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			


			AuthSharedObjectUsersDAO authSharedObjectUsersDAO = AuthSharedObjectUsersDAO.getInstance();
			
			int sharedObjectId = projectAuthShareableObjectIdFromDB;
			int userId = personId;
			
			AuthSharedObjectUsersDTO existingAuthSharedObjectUsersDTO = authSharedObjectUsersDAO.getAuthSharedObjectUsersDTOForSharedObjectIdAndUserId( sharedObjectId, userId );

			if ( existingAuthSharedObjectUsersDTO != null ) {
				
				//  only update if exists

				int personExistingAccessLevel = existingAuthSharedObjectUsersDTO.getAccessLevel();

				if ( ! authAccessLevel.isProjectOwnerAllowed() 
						&& personExistingAccessLevel <= AuthAccessLevelConstants.ACCESS_LEVEL_ASSISTANT_PROJECT_OWNER_AKA_RESEARCHER ) {

					//  Not authorized to update a person with this access level if not project owner

					throw new WebApplicationException(
							Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
							.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
							.build()
							);
				}


				AuthSharedObjectUsersDTO authSharedObjectUsersDTO = new AuthSharedObjectUsersDTO();

				authSharedObjectUsersDTO.setSharedObjectId( projectAuthShareableObjectIdFromDB );
				authSharedObjectUsersDTO.setAccessLevel( personAccessLevel );
				authSharedObjectUsersDTO.setUserId( personId );


				authSharedObjectUsersDAO.updateUserAccessLevel( authSharedObjectUsersDTO );
			}

			UpdateUserAccessToProjectResult updateUserAccessToProjectResult = new UpdateUserAccessToProjectResult();
			
			updateUserAccessToProjectResult.setStatus(true);
			
			return updateUserAccessToProjectResult;
			
		} catch ( WebApplicationException e ) {

			throw e;
			
		} catch ( Exception e ) {
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
		}
				
	}
	

}
