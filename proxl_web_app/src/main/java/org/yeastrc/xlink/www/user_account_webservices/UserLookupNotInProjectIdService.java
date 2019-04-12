package org.yeastrc.xlink.www.user_account_webservices;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
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

import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.searcher.UserSearcherForSharedObjectIdNotUserGlobalNoAccess;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtCentralWebappWebserviceAccess;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtGetUserDataRequest;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtGetUserDataResponse;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtSearchUserDataRequest;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtSearchUserDataResponse;
import org.yeastrc.auth.dao.AuthUserDAO;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;


@Path("/user")
public class UserLookupNotInProjectIdService {
	private static final Logger log = LoggerFactory.getLogger( UserLookupNotInProjectIdService.class);
	
	public static class WebserviceResponseItem {
		private String firstName;
		private String lastName;
		private String email;
		private Integer authUserId;
		
		public String getFirstName() {
			return firstName;
		}
		public String getLastName() {
			return lastName;
		}
		public String getEmail() {
			return email;
		}
		public Integer getAuthUserId() {
			return authUserId;
		}
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/lookupLastNameNotInProjectId") 
	public UserQueryResult lookupLastNameNotInProjectId( @QueryParam( "query" ) String query,
										  @QueryParam( "projectId" ) Integer projectId,
										  @Context HttpServletRequest request )
	throws Exception {
		//  Restricted to users with ACCESS_LEVEL_ASSISTANT_PROJECT_OWNER_AKA_RESEARCHER or better
		try {
			if ( projectId == null ) {
				//  No Project Id 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			} 
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
			WebSessionAuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getWebSessionAuthAccessLevel();
			//  Test access to the project id
			if ( ! authAccessLevel.isAssistantProjectOwnerAllowed() ) {
				//  No Access Allowed for this project id
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			///   Auth Check Complete
			////////////////////////////
			
			String sessionKey = accessAndSetupWebSessionResult.getUserSession().getUserMgmtSessionKey();
			UserMgmtSearchUserDataRequest userMgmtSearchUserDataRequest = new UserMgmtSearchUserDataRequest();
			userMgmtSearchUserDataRequest.setSessionKey( sessionKey );
			userMgmtSearchUserDataRequest.setSearchString( query );
			
			UserMgmtSearchUserDataResponse userMgmtSearchUserDataResponse = 
					UserMgmtCentralWebappWebserviceAccess.getInstance().searchUserDataByLastName( userMgmtSearchUserDataRequest );
			
			if ( ! userMgmtSearchUserDataResponse.isSuccess() ) {
				if ( userMgmtSearchUserDataResponse.isSessionKeyNotValid() ) {
					String msg = "Session Key invalid for call to UserMgmtCentralWebappWebserviceAccess.getInstance().searchUserDataByLastName(...)";
					log.error( msg );
					throw new ProxlWebappInternalErrorException( msg );
				}
				String msg = "call to UserMgmtCentralWebappWebserviceAccess.getInstance().searchUserDataByLastName(...) not successful, query: " + query;
				log.error( msg );
				throw new ProxlWebappInternalErrorException( msg );
			}
			
			List<Integer> userMgmtUserIdsForQueryParam = userMgmtSearchUserDataResponse.getUserIdList();
			
			List<WebserviceResponseItem> queryResultList = getUserResultList( projectId, userMgmtUserIdsForQueryParam );
			
			UserQueryResult userQueryResult = new UserQueryResult();
			userQueryResult.setQueryResultList( queryResultList );
			return userQueryResult;
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT ) // This string will be passed to the client
					.build()
					);
		}
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/lookupEmailNotInProjectId") 
	public UserQueryResult lookupEmailNotInProjectId( @QueryParam( "query" ) String query,
										  @QueryParam( "projectId" ) Integer projectId,
										  @Context HttpServletRequest request )
	throws Exception {
		//  Restricted to users with ACCESS_LEVEL_ASSISTANT_PROJECT_OWNER_AKA_RESEARCHER or better
		try {
			if ( projectId == null ) {
				//  No Project Id 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			} 
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
			WebSessionAuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getWebSessionAuthAccessLevel();
			//  Test access to the project id
			if ( ! authAccessLevel.isAssistantProjectOwnerAllowed() ) {
				//  No Access Allowed for this project id
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			///   Auth Check Complete
			////////////////////////////
			
			String sessionKey = accessAndSetupWebSessionResult.getUserSession().getUserMgmtSessionKey();
			UserMgmtSearchUserDataRequest userMgmtSearchUserDataRequest = new UserMgmtSearchUserDataRequest();
			userMgmtSearchUserDataRequest.setSessionKey( sessionKey );
			userMgmtSearchUserDataRequest.setSearchString( query );
			
			UserMgmtSearchUserDataResponse userMgmtSearchUserDataResponse = 
					UserMgmtCentralWebappWebserviceAccess.getInstance().searchUserDataByEmail( userMgmtSearchUserDataRequest );
			
			if ( ! userMgmtSearchUserDataResponse.isSuccess() ) {
				if ( userMgmtSearchUserDataResponse.isSessionKeyNotValid() ) {
					String msg = "Session Key invalid for call to UserMgmtCentralWebappWebserviceAccess.getInstance().searchUserDataByEmail(...)";
					log.error( msg );
					throw new ProxlWebappInternalErrorException( msg );
				}
				String msg = "call to UserMgmtCentralWebappWebserviceAccess.getInstance().searchUserDataByEmail(...) not successful, query: " + query;
				log.error( msg );
				throw new ProxlWebappInternalErrorException( msg );
			}
			
			List<Integer> userMgmtUserIdsForQueryParam = userMgmtSearchUserDataResponse.getUserIdList();
			
			List<WebserviceResponseItem> queryResultList = getUserResultList( projectId, userMgmtUserIdsForQueryParam );
			
			UserQueryResult userQueryResult = new UserQueryResult();
			userQueryResult.setQueryResultList( queryResultList );
			return userQueryResult;
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
	 * @param userMgmtUserIdsForQueryParam
	 * @return
	 * @throws Exception
	 * @throws ProxlWebappInternalErrorException
	 * @throws WebApplicationException
	 */
	private List<WebserviceResponseItem> getUserResultList(Integer projectId, List<Integer> userMgmtUserIdsForQueryParam )
			throws Exception, ProxlWebappInternalErrorException, WebApplicationException {

		Integer authShareableObjectIdForProjectId = ProjectDAO.getInstance().getAuthShareableObjectIdForProjectId( projectId );
		if ( authShareableObjectIdForProjectId == null ) {
			log.warn( " projectId is not in database: " + projectId );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		
		if ( userMgmtUserIdsForQueryParam == null || userMgmtUserIdsForQueryParam.isEmpty() ) {
			//  return empty list since no user ids returned from User Mgmt Webapp
			return new ArrayList<>(); //  EARLY EXIT  
		}
		
		//  Get Set of authUserIds already in project or Global no Access.  
		//    These are excluded from the list of authUserIds to display to the user
		Set<Integer> authUserIdsInProjectOrGlobalNoAccessSet = 
				UserSearcherForSharedObjectIdNotUserGlobalNoAccess.getInstance().
				getUserMgmtUserIdListForSharedObjectId( authShareableObjectIdForProjectId );
		
		List<WebserviceResponseItem> queryResultList = new ArrayList<WebserviceResponseItem>( userMgmtUserIdsForQueryParam.size() );

		for ( int userMgmtUserId : userMgmtUserIdsForQueryParam ) {
			if ( authUserIdsInProjectOrGlobalNoAccessSet.contains( userMgmtUserId ) ) {
				//  Project already contains this auth user id so skip to the next one
				continue;
			}

			//  Get authUserId (Local Proxl User Id) for userMgmtUserId
			Integer authUserId = AuthUserDAO.getInstance().getIdForUserMgmtUserId( userMgmtUserId );
			if ( authUserId == null ) {
				//  userMgmtUserId not in Proxl so omit from list
				continue;  //  EARLY Continue
				
//				String msg = "Failed to get id from proxl auth_user table for userMgmtUserId: " + userMgmtUserId;
//				log.error( msg );
//				throw new ProxlWebappInternalErrorException(msg);
			}
			
			Boolean userEnabledAppSpecific = AuthUserDAO.getInstance().getUserEnabledAppSpecific( authUserId );
			if ( userEnabledAppSpecific == null ) {
				String msg = "Failed to get getUserEnabledAppSpecific from proxl auth_user table for authUserId: " + authUserId;
				log.error( msg );
				throw new ProxlWebappInternalErrorException(msg);
			}			
			
			if ( ! userEnabledAppSpecific ) {
				//  User disabled in Proxl so exclude
				continue;  //  EARLY Continue
			}
			
			//  Get full user data
			UserMgmtGetUserDataRequest userMgmtGetUserDataRequest = new UserMgmtGetUserDataRequest();
//				userMgmtGetUserDataRequest.setSessionKey( userMgmtLoginResponse.getSessionKey() );
			userMgmtGetUserDataRequest.setUserId( userMgmtUserId );
			
			UserMgmtGetUserDataResponse userMgmtGetUserDataResponse = 
					UserMgmtCentralWebappWebserviceAccess.getInstance().getUserData( userMgmtGetUserDataRequest );
			
			if ( ! userMgmtGetUserDataResponse.isSuccess() ) {
				String msg = "Failed to get Full user data from User Mgmt Webapp for userMgmtUserId: " + userMgmtUserId;
				log.error( msg );
				throw new ProxlWebappInternalErrorException(msg);
			}
			

			//  Get user Access level at account level from proxl db
			
			Integer userAccessLevel = AuthUserDAO.getInstance().getUserAccessLevel( authUserId );
			if ( userAccessLevel == null ) {
				String msg = "Failed to get userAccessLevel from proxl auth_user table for user id: " + authUserId;
				log.error( msg );
				throw new ProxlWebappInternalErrorException(msg);
			}

			WebserviceResponseItem webserviceResponseItem = new WebserviceResponseItem();
			webserviceResponseItem.authUserId = authUserId;
			webserviceResponseItem.email = userMgmtGetUserDataResponse.getEmail();
			webserviceResponseItem.firstName = userMgmtGetUserDataResponse.getFirstName();
			webserviceResponseItem.lastName = userMgmtGetUserDataResponse.getLastName();
			
			queryResultList.add( webserviceResponseItem );
		}
		return queryResultList;
	}
	
	
	public static class UserQueryResult {
	
		private List<WebserviceResponseItem> queryResultList;
	
		public List<WebserviceResponseItem> getQueryResultList() {
			return queryResultList;
		}
	
		public void setQueryResultList(List<WebserviceResponseItem> queryResultList) {
			this.queryResultList = queryResultList;
		}
	
	}
}
