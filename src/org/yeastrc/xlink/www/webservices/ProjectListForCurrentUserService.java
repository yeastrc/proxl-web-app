package org.yeastrc.xlink.www.webservices;


import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.yeastrc.auth.dao.AuthUserDAO;
import org.yeastrc.auth.dto.AuthUserDTO;
import org.yeastrc.xlink.www.dto.ProjectDTO;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.objects.ProjectWithUserAccessLevel;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.user_web_utils.GetAuthAccessLevelForWebRequest;
import org.yeastrc.xlink.www.web_utils.GetProjectListForCurrentLoggedInUser;

@Path("/project")
public class ProjectListForCurrentUserService {

	private static final Logger log = Logger.getLogger(ProjectListForCurrentUserService.class);


	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/listForCurrentUser")
	public List<ProjectWithUserAccessLevel>  listProjects( 
			@Context HttpServletRequest request ) throws Exception {

		try {

			// Get the session first.  
			HttpSession session = request.getSession();



			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionNoProjectId( request );
			
			UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();

			if ( accessAndSetupWebSessionResult.isNoSession() ) {

				//  No User session 

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			

			if ( userSessionObject.getUserDBObject() == null || userSessionObject.getUserDBObject().getAuthUser() == null  ) {

				//  No Access Allowed since not a logged in user

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}

			List<ProjectDTO> projects = GetProjectListForCurrentLoggedInUser.getInstance().getProjectListForCurrentLoggedInUser( request );


			AuthUserDTO authUser = userSessionObject.getUserDBObject().getAuthUser();

			///  Refresh with latest

			authUser = AuthUserDAO.getInstance().getAuthUserDTOForId( authUser.getId() );

			userSessionObject.getUserDBObject().setAuthUser( authUser );


			if ( authUser != null ) {
				
				//  Add projects user invited to
				
				//  Mike has a mockup but invites are associated with emails, not accounts
				
			}
			
			List<ProjectWithUserAccessLevel> results = new ArrayList<ProjectWithUserAccessLevel>( projects.size() );
			
			for ( ProjectDTO project : projects ) {
				
				ProjectWithUserAccessLevel projectWithUserAccessLevel = new ProjectWithUserAccessLevel();
				results.add( projectWithUserAccessLevel );
				
				projectWithUserAccessLevel.setProject( project );
				

				//  Test access to the project id
				
				AuthAccessLevel authAccessLevelPerProject = GetAuthAccessLevelForWebRequest.getInstance().getAuthAccessLevelForWebRequestProjectId( userSessionObject, project.getId() );

				if ( authAccessLevelPerProject.isProjectOwnerAllowed() && ! project.isProjectLocked() ) {

					//  Delete access allowed to Project Owner or Admin

					projectWithUserAccessLevel.setCanDelete(true);
				}
			}
			
			return results;

		} catch ( WebApplicationException e ) {

			throw e;

		} catch ( Exception e ) {

			String msg = "Exception caught: " + e.toString();

			log.error( msg, e );

			throw e;
		}


	}
}