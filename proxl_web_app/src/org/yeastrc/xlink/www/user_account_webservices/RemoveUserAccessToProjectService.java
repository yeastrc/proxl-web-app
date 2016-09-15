package org.yeastrc.xlink.www.user_account_webservices;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
import org.apache.log4j.Logger;
import org.yeastrc.auth.dao.AuthSharedObjectUsersDAO;
import org.yeastrc.auth.dto.AuthSharedObjectUsersDTO;
import org.yeastrc.xlink.www.constants.AuthAccessLevelConstants;
import org.yeastrc.xlink.www.dao.ProjectDAO;
import org.yeastrc.xlink.www.dto.ProjectDTO;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.objects.UpdateUserAccessToProjectResult;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;



@Path("/user")
public class RemoveUserAccessToProjectService {

	private static final Logger log = Logger.getLogger(RemoveUserAccessToProjectService.class);
	
	
	
	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/removeAccessToProject") 
	public UpdateUserAccessToProjectResult updateUserAccessToProject(   @FormParam( "personId" ) String personIdString,
			@FormParam( "projectId" ) String projectIdString,
			@Context HttpServletRequest request )
	throws Exception {
		
		//  Restricted to users with ACCESS_LEVEL_ASSISTANT_PROJECT_OWNER or better
		

		int personId = 0;
		int projectId = 0;
		
		
		if ( StringUtils.isEmpty( personIdString ) ) {

			log.warn( "RemoveUserAccessToProjectService:  personId empty: " + personIdString );

			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		

		try {
			
			personId = Integer.parseInt( personIdString );
			
		} catch (Exception e) {

			log.warn( "RemoveUserAccessToProjectService:  personId is not valid integer: " + personIdString, e );

			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		

		

		if ( StringUtils.isEmpty( projectIdString ) ) {

			log.warn( "RemoveUserAccessToProjectService:  projectId empty: " + projectIdString );

			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		

		try {

			projectId = Integer.parseInt( projectIdString );

		} catch (Exception e) {

			log.warn( "RemoveUserAccessToProjectService:  projectId is not valid integer: " + projectIdString, e );

			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		

		
		
		
//		if (true)
//		throw new Exception("Forced Error");
		
		try {

			// Get the session first.  
			HttpSession session = request.getSession();




			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );

			if ( accessAndSetupWebSessionResult.isNoSession() ) {

				//  No User session 

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			
			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();

			
			//  Test access to the project id

			if ( ! authAccessLevel.isAssistantProjectOwnerAllowed() ) {

				//  No Access Allowed for this project id

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}

			

			ProjectDTO projectDTO = ProjectDAO.getInstance().getProjectDTOForProjectId( projectId );

			if ( projectDTO == null ) {

				log.warn( "RemoveUserAccessToProjectService:  projectId is not in database: " + projectIdString );

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}

			AuthSharedObjectUsersDAO authSharedObjectUsersDAO = AuthSharedObjectUsersDAO.getInstance();
			
			int sharedObjectId = projectDTO.getAuthShareableObjectId();
			int userId = personId;
			
			AuthSharedObjectUsersDTO existingAuthSharedObjectUsersDTO = authSharedObjectUsersDAO.getAuthSharedObjectUsersDTOForSharedObjectIdAndUserId( sharedObjectId, userId );

			if ( existingAuthSharedObjectUsersDTO != null ) {
				
				//  only remove if exists

				int personExistingAccessLevel = existingAuthSharedObjectUsersDTO.getAccessLevel();

				if ( ! authAccessLevel.isProjectOwnerAllowed() 
						&& personExistingAccessLevel <= AuthAccessLevelConstants.ACCESS_LEVEL_PROJECT_OWNER ) {

					//  Not authorized to remove a person with ACCESS_LEVEL_PROJECT_OWNER if not project owner
					
					//  Tested 12/12/2014 and errors properly if logged in user is Researcher and trying to remove Owner
//					if ( ! authAccessLevel.isProjectOwnerAllowed() 
//						&& personExistingAccessLevel <= AuthAccessLevelConstants.ACCESS_LEVEL_PROJECT_OWNER  ) {

					throw new WebApplicationException(
							Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
							.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
							.build()
							);
				}

				AuthSharedObjectUsersDTO authSharedObjectUsersDTO = new AuthSharedObjectUsersDTO();

				authSharedObjectUsersDTO.setSharedObjectId( projectDTO.getAuthShareableObjectId() );
				authSharedObjectUsersDTO.setUserId( personId );


				authSharedObjectUsersDAO.delete( authSharedObjectUsersDTO );
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
