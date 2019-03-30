package org.yeastrc.xlink.www.user_account_webservices;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.dao.ProjectDAO;
import org.yeastrc.xlink.www.internal_services.GetUserDisplayListForSharedObjectId;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.UserDisplay;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;

@Path("/user")
public class ListUsersForProjectIdService {

	private static final Logger log = LoggerFactory.getLogger( ListUsersForProjectIdService.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/listForProjectId") 
	public List<UserDisplay> getViewerData( @QueryParam( "projectId" ) Integer projectId,
										  @Context HttpServletRequest request )
	throws Exception {
		//  Restricted to users with ACCESS_LEVEL_ASSISTANT_PROJECT_OWNER or better
		try {
			// Get the session first.  
			HttpSession session = request.getSession();
			if ( projectId == null ) {
				//  No Project Id 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			} 
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
			if ( ! authAccessLevel.isAssistantProjectOwnerAllowed()
					&& ! authAccessLevel.isAssistantProjectOwnerIfProjectNotLockedAllowed() ) {
				//  No Access Allowed for this project id
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			///  Auth check complete
			///////////
			Integer projectSharedObjectId = ProjectDAO.getInstance().getAuthShareableObjectIdForProjectId( projectId );
			if ( projectSharedObjectId == null ) {
				log.warn( "ListUsersForProjectIdService:  projectId is not in database: " + projectId );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			List<UserDisplay> userListForProject = GetUserDisplayListForSharedObjectId.getInstance().getUserDisplayListExcludeAdminGlobalNoAccessAccountsForSharedObjectId( projectSharedObjectId );
			//  Sort on last name then first name
			Collections.sort( userListForProject, new Comparator<UserDisplay>() {
				@Override
				public int compare(UserDisplay o1, UserDisplay o2) {
					int lastNameCompare = o1.getxLinkUserDTO().getLastName().compareTo( o2.getxLinkUserDTO().getLastName() );
					if ( lastNameCompare != 0 ) {
						return lastNameCompare;
					}
					return o1.getxLinkUserDTO().getFirstName().compareTo( o2.getxLinkUserDTO().getFirstName() );
				}
			});
			return userListForProject;
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}
}
