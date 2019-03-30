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
import org.yeastrc.xlink.www.internal_services.GetInvitedPeopleDisplayListForSharedObjectId;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.InvitedPersonDisplay;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;

@Path("/user")
public class ListInvitedPeopleForProjectIdService {

	private static final Logger log = LoggerFactory.getLogger( ListInvitedPeopleForProjectIdService.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/listInvitedPeopleForProjectId") 
	public List<InvitedPersonDisplay> getViewerData( @QueryParam( "projectId" ) Integer projectId,
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
			UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();
			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();
			if ( ! authAccessLevel.isAssistantProjectOwnerAllowed() 
					&& ! authAccessLevel.isAssistantProjectOwnerIfProjectNotLockedAllowed() ) {
				//  No Access Allowed for this project id
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			Integer projectSharedObjectId = ProjectDAO.getInstance().getAuthShareableObjectIdForProjectId( projectId );
			if ( projectSharedObjectId == null ) {
				log.warn( "projectId is not in database: " + projectId );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			List<InvitedPersonDisplay> inviteList = GetInvitedPeopleDisplayListForSharedObjectId.getInstance().getInvitedPersonDisplayListForSharedObjectId( projectSharedObjectId );
			//  Sort on email
			Collections.sort( inviteList, new Comparator<InvitedPersonDisplay>() {
				@Override
				public int compare(InvitedPersonDisplay o1, InvitedPersonDisplay o2) {
					return o1.getInvitedUserEmail().compareTo( o2.getInvitedUserEmail() );
				}
			});
			return inviteList;
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}
}
