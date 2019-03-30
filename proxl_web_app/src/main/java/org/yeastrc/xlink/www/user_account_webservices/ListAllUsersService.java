package org.yeastrc.xlink.www.user_account_webservices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.dto.XLinkUserDTO;
import org.yeastrc.xlink.www.internal_services.GetXLinkUserDTOListForUsers;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.ListAllUsersResponse;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;

@Path("/user")
public class ListAllUsersService {

	private static final Logger log = LoggerFactory.getLogger( ListAllUsersService.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/listAll") 
	public ListAllUsersResponse getViewerData( @Context HttpServletRequest request )
	throws Exception {
		//  Restricted to users with ACCESS_LEVEL_ADMIN or better
		try {
			// Get the session first.  
//			HttpSession session = request.getSession();
			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionNoProjectId( request );
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();
			//  Test access at global level
			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();
			if ( ! authAccessLevel.isAdminAllowed() ) {
				//  No Access Allowed 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			int currentlyLoggedInAccountId = userSessionObject.getUserDBObject().getAuthUser().getId();
			List<XLinkUserDTO> userAccountDBList = GetXLinkUserDTOListForUsers.getInstance().getXLinkUserDTOListForAllUsers();
			//  Sort on last name then first name
			Collections.sort( userAccountDBList, new Comparator<XLinkUserDTO>() {
				@Override
				public int compare(XLinkUserDTO o1, XLinkUserDTO o2) {
					int lastNameCompare = o1.getLastName().compareTo( o2.getLastName() );
					if ( lastNameCompare != 0 ) {
						return lastNameCompare;
					}
					return o1.getFirstName().compareTo( o2.getFirstName() );
				}
			});
			List<XLinkUserDTO> users = new ArrayList<XLinkUserDTO>( userAccountDBList.size() );
			ListAllUsersResponse listAllUsersResponse = new ListAllUsersResponse();
			listAllUsersResponse.setUsers( users );
			for ( XLinkUserDTO xLinkUserDTO : userAccountDBList ) {
				users.add( xLinkUserDTO );
			}
			listAllUsersResponse.setStatus(true);
			return listAllUsersResponse;
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}
}
