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

import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.searcher.UserSearcherAll;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtCentralWebappWebserviceAccess;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtGetUserDataRequest;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtGetUserDataResponse;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.user_session_management.UserSession;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.auth.dao.AuthUserDAO;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;

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
			//  Test access at global level
			WebSessionAuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getWebSessionAuthAccessLevel();
			if ( ! authAccessLevel.isAdminAllowed() ) {
				//  No Access Allowed 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			// int currentlyLoggedInAccountId = userSession.getAuthUserId();
			List<UserItem> userAccountDBList = this.getUserItemListForAllUsers();
			//  Sort on last name then first name
			Collections.sort( userAccountDBList, new Comparator<UserItem>() {
				@Override
				public int compare(UserItem o1, UserItem o2) {
					int lastNameCompare = o1.getLastName().compareTo( o2.getLastName() );
					if ( lastNameCompare != 0 ) {
						return lastNameCompare;
					}
					return o1.getFirstName().compareTo( o2.getFirstName() );
				}
			});
			ListAllUsersResponse listAllUsersResponse = new ListAllUsersResponse();
			listAllUsersResponse.users = userAccountDBList;
			listAllUsersResponse.status = true;
			return listAllUsersResponse;
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}
	

	/**
	 * @return
	 * @throws Exception
	 */
	public List<UserItem> getUserItemListForAllUsers( ) throws Exception {
		
		List<Integer> userIds = UserSearcherAll.getInstance().getAllAuthUserIds();
		List<UserItem> returnList = new ArrayList<UserItem>( userIds.size() );
		for ( int authUserId : userIds ) {

			//  Get User Mgmt User Id for authUserId
			Integer userMgmtUserId = AuthUserDAO.getInstance().getUserMgmtUserIdForId( authUserId );
			if ( userMgmtUserId == null ) {
				String msg = "Failed to get userMgmtUserId for Proxl auth user id: " + authUserId;
				log.warn( msg );
		        return null;  //  Early Exit
			}
			
			//  Get full user data
			UserMgmtGetUserDataRequest userMgmtGetUserDataRequest = new UserMgmtGetUserDataRequest();
//			userMgmtGetUserDataRequest.setSessionKey( userMgmtLoginResponse.getSessionKey() );
			userMgmtGetUserDataRequest.setUserId( userMgmtUserId );
			UserMgmtGetUserDataResponse userMgmtGetUserDataResponse = 
					UserMgmtCentralWebappWebserviceAccess.getInstance().getUserData( userMgmtGetUserDataRequest );
			if ( ! userMgmtGetUserDataResponse.isSuccess() ) {
				String msg = "Failed to get Full user data from User Mgmt Webapp for authUserId: " + authUserId
						+ ", userMgmtUserId: " + userMgmtUserId;
				log.error( msg );
				continue;  //  EARLY CONTINUE to next entry
			}
			
			Boolean enabledAppSpecific =
					AuthUserDAO.getInstance().getUserEnabledAppSpecific( authUserId );
			if ( enabledAppSpecific == null ) {
				String msg = "Failed to get enabledAppSpecific from proxl auth_user table for user id: " + authUserId;
				log.error( msg );
				continue;  //  EARLY CONTINUE to next entry
			}
			
			//  Get user Access level at account level from proxl db
			Integer userAccessLevel = AuthUserDAO.getInstance().getUserAccessLevel( authUserId );
			if ( userAccessLevel == null ) {
				String msg = "Failed to get userAccessLevel from proxl auth_user table for user id: " + authUserId;
				log.error( msg );
				continue;  //  EARLY CONTINUE to next entry
			}
			
			UserItem userItem = new UserItem();
			userItem.authUserId = authUserId;
			userItem.firstName = userMgmtGetUserDataResponse.getFirstName();
			userItem.lastName = userMgmtGetUserDataResponse.getLastName();
			userItem.userAccessLevel = userAccessLevel;
			userItem.enabledAppSpecific = enabledAppSpecific;
			userItem.enabledUserMgmtGlobalLevel = userMgmtGetUserDataResponse.isEnabled();
			
			returnList.add( userItem );
		}
		return returnList;
	}

	public static  class ListAllUsersResponse {
	
		private boolean status;
		private List<UserItem> users;
		
		public boolean isStatus() {
			return status;
		}
		public List<UserItem> getUsers() {
			return users;
		}
	}
	
	public static  class UserItem {
		
		private int authUserId;
		private String firstName;
		private String lastName;
		private Integer userAccessLevel;
		private Boolean enabledAppSpecific;
		private Boolean enabledUserMgmtGlobalLevel;
		
		public int getAuthUserId() {
			return authUserId;
		}
		public String getFirstName() {
			return firstName;
		}
		public String getLastName() {
			return lastName;
		}
		public Integer getUserAccessLevel() {
			return userAccessLevel;
		}
		public Boolean getEnabledAppSpecific() {
			return enabledAppSpecific;
		}
		public Boolean getEnabledUserMgmtGlobalLevel() {
			return enabledUserMgmtGlobalLevel;
		}
	}
	
}
