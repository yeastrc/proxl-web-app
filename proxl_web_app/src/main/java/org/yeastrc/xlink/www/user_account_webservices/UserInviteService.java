package org.yeastrc.xlink.www.user_account_webservices;

import java.sql.SQLException;
import java.util.List;
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
import org.yeastrc.auth.dao.AuthUserDAO;
import org.yeastrc.auth.dao.AuthUserInviteTrackingDAO;
import org.yeastrc.auth.dto.AuthSharedObjectUsersDTO;
import org.yeastrc.auth.dto.AuthUserDTO;
import org.yeastrc.auth.dto.AuthUserInviteTrackingDTO;
import org.yeastrc.auth.services.GenerateInviteCodeSaveInviteRecordService;
import org.yeastrc.xlink.www.constants.AuthAccessLevelConstants;
import org.yeastrc.xlink.www.dao.ProjectDAO;
import org.yeastrc.xlink.www.database_update_with_transaction_services.AddNewUserUsingDBTransactionService;
import org.yeastrc.xlink.www.dto.ProjectDTO;
import org.yeastrc.xlink.www.dto.XLinkUserDTO;
import org.yeastrc.xlink.www.dto.ZzUserDataMirrorDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.constants.StrutsActionPathsConstants;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.objects.UserInviteResult;
import org.yeastrc.xlink.www.send_email.GetEmailConfig;
import org.yeastrc.xlink.www.send_email.SendEmail;
import org.yeastrc.xlink.www.send_email.SendEmailDTO;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtCentralWebappWebserviceAccess;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtGetUserDataRequest;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtGetUserDataResponse;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtSearchUserDataRequest;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtSearchUserDataResponse;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.user_web_utils.ValidateUserAccessLevel;

/**
 * Search @Path for webservices
 * 
 * Current webservices:
 * 
 * userInviteService @Path("/invite") 
 * userResendInviteEmailService  @Path("/resendInviteEmail") 
 *
 */
@Path("/user") // Root Path
public class UserInviteService {
	
	private static final Logger log = LoggerFactory.getLogger( UserInviteService.class);

	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/invite") 
	public UserInviteResult userInviteService(   
			@FormParam( "invitedPersonUserId" ) String invitedPersonUserIdString,
			@FormParam( "invitedPersonLastName" ) String invitedPersonLastName,
			@FormParam( "invitedPersonEmail" ) String invitedPersonEmail,
			@FormParam( "invitedPersonAccessLevel" ) String invitedPersonAccessLevelString,
			@FormParam( "projectId" ) String projectIdString,
			@Context HttpServletRequest request )
	throws Exception {
		UserInviteResult userInviteResult = new UserInviteResult();
		//  Restricted to users with ACCESS_LEVEL_ASSISTANT_PROJECT_OWNER or better
		if ( invitedPersonUserIdString != null ) {
			invitedPersonUserIdString = invitedPersonUserIdString.trim();
		}		
		if ( invitedPersonLastName != null ) {
			invitedPersonLastName = invitedPersonLastName.trim();
		}
		if ( invitedPersonEmail != null ) {
			invitedPersonEmail = invitedPersonEmail.trim();
		}
		if ( invitedPersonAccessLevelString != null ) {
			invitedPersonAccessLevelString = invitedPersonAccessLevelString.trim();
		}
		if ( projectIdString != null ) {
			projectIdString = projectIdString.trim();
		}
		Integer invitedPersonUserId = null;
		if ( StringUtils.isNotEmpty( invitedPersonUserIdString ) ) {
			try {
				invitedPersonUserId = Integer.parseInt( invitedPersonUserIdString );
			} catch (Exception e) {
				log.warn( "UserInviteService:  invitedPersonUserId is not valid integer: " + invitedPersonUserIdString, e );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}
		} else {
			if ( StringUtils.isEmpty( invitedPersonLastName ) && StringUtils.isEmpty( invitedPersonEmail )) {
				log.warn( "UserInviteService: invitedPersonLastName and invitedPersonEmail both empty" );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			} else if ( StringUtils.isNotEmpty( invitedPersonLastName ) && StringUtils.isNotEmpty( invitedPersonEmail )) {
				log.warn( "UserInviteService: invitedPersonLastName and invitedPersonEmail both not empty" );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}
		}
		int invitedPersonAccessLevel = 0;
		Integer projectId = null;
		if ( StringUtils.isEmpty( invitedPersonAccessLevelString ) ) {
			log.warn( "UserInviteService:  invitedPersonAccessLevelString empty: " + invitedPersonAccessLevelString );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		try {
			invitedPersonAccessLevel = Integer.parseInt( invitedPersonAccessLevelString );
		} catch (Exception e) {
			log.warn( "UserInviteService:  invitedPersonAccessLevel is not valid integer: " + invitedPersonAccessLevelString, e );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		if ( StringUtils.isNotEmpty( projectIdString ) ) {
			try {
				projectId = Integer.parseInt( projectIdString );
			} catch (Exception e) {
				log.warn( "UserInviteService:  projectId is not valid integer: " + projectIdString, e );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}
		}
		try {
			// Get the session first.  
//			HttpSession session = request.getSession();
			//  Test for Admin level 
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
			AuthAccessLevel authAccessLevelGlobal = accessAndSetupWebSessionResult.getAuthAccessLevel();
			Integer projectAuthShareableObjectId = null;
			if ( authAccessLevelGlobal.isAdminAllowed() ) {
				// User is Admin so always allow
				if ( projectId != null ) {
					Integer projectAuthShareableObjectIdFromDB = ProjectDAO.getInstance().getAuthShareableObjectIdForProjectId( projectId );
					projectAuthShareableObjectId = projectAuthShareableObjectIdFromDB;
				}
			} else {
				if ( projectId == null ) {
					//  Admin level is required for invite without project id
					if ( ! authAccessLevelGlobal.isAdminAllowed() ) {
						//  No Access Allowed for this user
						throw new WebApplicationException(
								Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
								.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
								.build()
								);
					}
				} else {
					//  Test access to the project id
					AccessAndSetupWebSessionResult accessAndSetupWebSessionResultProjectLevel =
							GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
					AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResultProjectLevel.getAuthAccessLevel();
					if ( ! authAccessLevel.isAssistantProjectOwnerAllowed() ) {
						//  No Access Allowed for this project id
						throw new WebApplicationException(
								Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
								.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
								.build()
								);
					}
					if ( ! authAccessLevel.isProjectOwnerAllowed() 
							&& invitedPersonAccessLevel <= AuthAccessLevelConstants.ACCESS_LEVEL_PROJECT_OWNER ) {
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
					Integer projectAuthShareableObjectIdFromDB = ProjectDAO.getInstance().getAuthShareableObjectIdForProjectId( projectId );
					if ( projectAuthShareableObjectIdFromDB == null ) {
						log.warn( "UserInviteService:  projectId is not in database: " + projectIdString );
						throw new WebApplicationException(
								Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
								.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
								.build()
								);
					}
					projectAuthShareableObjectId = projectAuthShareableObjectIdFromDB;
				}
				ValidateUserAccessLevel.UserAccessLevelTypes userAccessLevelType = ValidateUserAccessLevel.UserAccessLevelTypes.GLOBAL;
				if ( projectAuthShareableObjectId != null ) {
					userAccessLevelType = ValidateUserAccessLevel.UserAccessLevelTypes.PROJECT;
				}
				if ( ! ValidateUserAccessLevel.validateUserAccessLevel( invitedPersonAccessLevel, userAccessLevelType ) ) {
					//  The invitedPersonAccessLevel is not valid
					log.warn( "UserInviteService:  invitedPersonAccessLevel is not valid: " + invitedPersonAccessLevel );
					throw new WebApplicationException(
							Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
							.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
							.build()
							);
				}
			}
			//   DONE  validating the request
			////////////////////////

			String sessionKey = accessAndSetupWebSessionResult.getUserSessionObject().getUserLoginSessionKey();

			//   Process the request
			if ( invitedPersonUserId != null ) {
				//  process the user id
				if ( projectId == null ) {
					log.warn( "UserInviteService:  Adding existing user but no project id provided, invitedPersonUserId: " + invitedPersonUserId );
					throw new WebApplicationException(
							Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
							.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
							.build()
							);
				}
				addExistingUserToProjectUsingProjectId( invitedPersonUserId, invitedPersonAccessLevel, projectAuthShareableObjectId, userInviteResult );
			} else if ( StringUtils.isNotEmpty( invitedPersonLastName ) ) {
				//  process the last name
				if ( projectId == null ) {
					log.warn( "UserInviteService:  Adding existing user but no project id provided, invitedPersonUserId: " + invitedPersonUserId );
					throw new WebApplicationException(
							Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
							.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
							.build()
							);
				}
				addExistingUserToProjectUsingLastName( invitedPersonLastName, invitedPersonAccessLevel, projectAuthShareableObjectId, userInviteResult, sessionKey );
			} else {
				//  Process the email
				UserMgmtSearchUserDataRequest userMgmtSearchUserDataRequest = new UserMgmtSearchUserDataRequest();
				userMgmtSearchUserDataRequest.setSessionKey( sessionKey );
				userMgmtSearchUserDataRequest.setSearchString( invitedPersonEmail );
				userMgmtSearchUserDataRequest.setSearchStringExactMatch(true);
				
				UserMgmtSearchUserDataResponse userMgmtSearchUserDataResponse = 
						UserMgmtCentralWebappWebserviceAccess.getInstance().searchUserDataByEmail( userMgmtSearchUserDataRequest );
				
				if ( ! userMgmtSearchUserDataResponse.isSuccess() ) {
					if ( userMgmtSearchUserDataResponse.isSessionKeyNotValid() ) {
						String msg = "Session Key invalid for call to UserMgmtCentralWebappWebserviceAccess.getInstance().searchUserDataByEmail(...)";
						log.error( msg );
						throw new ProxlWebappInternalErrorException( msg );
					}
					String msg = "call to UserMgmtCentralWebappWebserviceAccess.getInstance().searchUserDataByEmail(...) not successful, invitedPersonEmail: " + invitedPersonEmail;
					log.error( msg );
					throw new ProxlWebappInternalErrorException( msg );
				}
				List<Integer> userIdList = userMgmtSearchUserDataResponse.getUserIdList();
				if ( userIdList != null && ! userIdList.isEmpty() ) {
					// account with this email already exists
					if ( projectId == null ) {
						log.warn( "UserInviteService:  Adding existing user but no project id provided, invitedPersonUserId: " + invitedPersonUserId );
						userInviteResult.setStatus(false);
						userInviteResult.setEmailAddressDuplicateError(true);
						return userInviteResult;  //  EARLY EXIT
//						throw new WebApplicationException(
//								Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
//								.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
//								.build()
//								);
					}
					Integer userIdEntry = userIdList.get(0);
					int invitedPersonUserIdFromEmail = userIdEntry;
					addExistingUserToProjectUsingProjectId( invitedPersonUserIdFromEmail, invitedPersonAccessLevel, projectAuthShareableObjectId, userInviteResult );
				} else {
					//  no account with this email exists
					inviteNewUserUsingEmail( invitedPersonEmail, request, 
												invitedPersonAccessLevel, userSessionObject, projectAuthShareableObjectId,
												userInviteResult );
				}
			}
			return userInviteResult;
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
	
	/**
	 * @param invitedPersonLastName
	 * @param invitedPersonAccessLevel
	 * @param projectAuthShareableObjectId
	 * @param userInviteResult
	 * @throws Exception
	 */
	private void addExistingUserToProjectUsingLastName( 
			String invitedPersonLastName, 
			int invitedPersonAccessLevel,
			int projectAuthShareableObjectId,
			UserInviteResult userInviteResult,
			String sessionKey ) throws Exception {
		
		UserMgmtSearchUserDataRequest userMgmtSearchUserDataRequest = new UserMgmtSearchUserDataRequest();
		userMgmtSearchUserDataRequest.setSessionKey( sessionKey );
		userMgmtSearchUserDataRequest.setSearchString( invitedPersonLastName );
		userMgmtSearchUserDataRequest.setSearchStringExactMatch(true);
		
		UserMgmtSearchUserDataResponse userMgmtSearchUserDataResponse = 
				UserMgmtCentralWebappWebserviceAccess.getInstance().searchUserDataByEmail( userMgmtSearchUserDataRequest );
		
		if ( ! userMgmtSearchUserDataResponse.isSuccess() ) {
			if ( userMgmtSearchUserDataResponse.isSessionKeyNotValid() ) {
				String msg = "Session Key invalid for call to UserMgmtCentralWebappWebserviceAccess.getInstance().searchUserDataByEmail(...)";
				log.error( msg );
				throw new ProxlWebappInternalErrorException( msg );
			}
			String msg = "call to UserMgmtCentralWebappWebserviceAccess.getInstance().searchUserDataByEmail(...) not successful, invitedPersonLastName: " + invitedPersonLastName;
			log.error( msg );
			throw new ProxlWebappInternalErrorException( msg );
		}
		List<Integer> userIdList = userMgmtSearchUserDataResponse.getUserIdList();
		if ( ! userIdList.isEmpty() ) {
			userInviteResult.setLastNameNotFoundError(true);
		} else if ( userIdList.size() > 1 ) {
			userInviteResult.setLastNameDuplicateError(true);
		} else {
			int invitedPersonUserId = userIdList.get( 0 );
			addExistingUserToProjectUsingProjectId( invitedPersonUserId, invitedPersonAccessLevel, projectAuthShareableObjectId, userInviteResult );
		}
	}
	
	/**
	 * @param invitedPersonUserId
	 * @param invitedPersonAccessLevel
	 * @param projectAuthShareableObjectId
	 * @param userInviteResult
	 * @throws Exception
	 */
	private void addExistingUserToProjectUsingProjectId( 
			int invitedPersonUserId, 
			int invitedPersonAccessLevel,
			int projectAuthShareableObjectId,
			UserInviteResult userInviteResult ) throws Exception {

		//  Get User Mgmt User Id for authUserId
		Integer userMgmtUserId = AuthUserDAO.getInstance().getUserMgmtUserIdForId( invitedPersonUserId );
		if ( userMgmtUserId == null ) {
			String msg = "Failed to get userMgmtUserId for Proxl auth user id: " + invitedPersonUserId;
			log.error( msg );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		
		//  Get full user data
		
		UserMgmtGetUserDataRequest userMgmtGetUserDataRequest = new UserMgmtGetUserDataRequest();
//		userMgmtGetUserDataRequest.setSessionKey(  );
		userMgmtGetUserDataRequest.setUserId( userMgmtUserId );
		
		UserMgmtGetUserDataResponse userMgmtGetUserDataResponse = 
				UserMgmtCentralWebappWebserviceAccess.getInstance().getUserData( userMgmtGetUserDataRequest );
		
		if ( ! userMgmtGetUserDataResponse.isSuccess() ) {
			String msg = "Failed to get Full user data from User Mgmt Webapp for Proxl user id: " + invitedPersonUserId
					+ ", userMgmtUserId: " + userMgmtUserId;
			log.error( msg );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT ) // This string will be passed to the client
					.build()
					);
		}

		if ( ! userMgmtGetUserDataResponse.isEnabled() ) {
			log.warn( "AddExistingUserToProjectService:  user is disabled: " + invitedPersonUserId );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		Integer userAccessLevel = AuthUserDAO.getInstance().getUserAccessLevel(invitedPersonUserId);
		if ( userAccessLevel != null && userAccessLevel == AuthAccessLevelConstants.ACCESS_LEVEL_NONE ) {
			log.warn( "AddExistingUserToProjectService:  user is global acess level none: " + invitedPersonUserId );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		AuthSharedObjectUsersDTO authSharedObjectUsersDTO = new AuthSharedObjectUsersDTO();
		authSharedObjectUsersDTO.setSharedObjectId( projectAuthShareableObjectId );
		authSharedObjectUsersDTO.setAccessLevel( invitedPersonAccessLevel );
		authSharedObjectUsersDTO.setUserId( invitedPersonUserId );
		AuthSharedObjectUsersDAO authSharedObjectUsersDAO = AuthSharedObjectUsersDAO.getInstance();
		try {
			Integer proxlUserId = AuthUserDAO.getInstance().getIdForId( invitedPersonUserId );
			if ( proxlUserId == null ) {
				// No account in proxl for this user id.  
				// Create one
				AuthUserDTO authUserDTO = new AuthUserDTO();
				authUserDTO.setId( invitedPersonUserId );
				authUserDTO.setEnabledAppSpecific(true);
				if ( userMgmtGetUserDataResponse.isGlobalAdminUser() ) {
					//  User is marked Global Admin User so create account with full admin rights
					authUserDTO.setUserAccessLevel( AuthAccessLevelConstants.ACCESS_LEVEL_ADMIN );
				} else {
					authUserDTO.setUserAccessLevel( AuthAccessLevelConstants.ACCESS_LEVEL_CREATE_NEW_PROJECT_AKA_USER );
				}
				ZzUserDataMirrorDTO zzUserDataMirrorDTO = new ZzUserDataMirrorDTO();
				// zzUserDataMirrorDTO.setAuthUserId( XXX );  AuthUserId set later
				zzUserDataMirrorDTO.setUsername( userMgmtGetUserDataResponse.getUsername() );
				zzUserDataMirrorDTO.setEmail( userMgmtGetUserDataResponse.getEmail() );
				zzUserDataMirrorDTO.setFirstName( userMgmtGetUserDataResponse.getFirstName() );
				zzUserDataMirrorDTO.setLastName( userMgmtGetUserDataResponse.getLastName() );
				zzUserDataMirrorDTO.setOrganization( userMgmtGetUserDataResponse.getOrganization() );
				try {
					AddNewUserUsingDBTransactionService.getInstance().addNewUser( authUserDTO, zzUserDataMirrorDTO );
				} catch ( Exception e ) {
					String msg = "Failed to add new user for userId in User Mgmt but not in Proxl.  UserId: " + invitedPersonUserId;
					log.error( msg, e );
					throw e;
				}
			}
			
			XLinkUserDTO existingUserThatWasAdded = new XLinkUserDTO();
			AuthUserDTO authUserDTO = new AuthUserDTO();
			existingUserThatWasAdded.setAuthUser(authUserDTO);
			
			authUserDTO.setId( invitedPersonUserId );
			authUserDTO.setUsername( userMgmtGetUserDataResponse.getUsername() );
			authUserDTO.setEmail( userMgmtGetUserDataResponse.getEmail() );
			authUserDTO.setUserAccessLevel( userAccessLevel );
			
			existingUserThatWasAdded.setFirstName( userMgmtGetUserDataResponse.getFirstName() );
			existingUserThatWasAdded.setLastName( userMgmtGetUserDataResponse.getLastName() );
			existingUserThatWasAdded.setOrganization( userMgmtGetUserDataResponse.getOrganization() );
			
			authSharedObjectUsersDAO.save( authSharedObjectUsersDTO );
			userInviteResult.setStatus(true);
			userInviteResult.setAddedExistingUser(true);
			
			userInviteResult.setExistingUserThatWasAdded( existingUserThatWasAdded );
		} catch ( SQLException sqlException ) {
			String exceptionMessage = sqlException.getMessage();
			if ( exceptionMessage != null && exceptionMessage.startsWith( "Duplicate entry" ) ) {
				AuthSharedObjectUsersDTO existingAuthSharedObjectUsersDTO = authSharedObjectUsersDAO.getAuthSharedObjectUsersDTOForSharedObjectIdAndUserId( projectAuthShareableObjectId, invitedPersonUserId );
				if ( existingAuthSharedObjectUsersDTO != null ) {
					userInviteResult.setDuplicateInsertError(true);
				}
			} else {
				throw sqlException;
			}
		}
	}
	
	/**
	 * @param invitedPersonEmail
	 * @param request
	 * @param invitedPersonAccessLevel
	 * @param userSessionObject
	 * @param projectAuthShareableObjectId
	 * @throws Exception
	 */
	private void inviteNewUserUsingEmail( 
			String invitedPersonEmail,
			HttpServletRequest request, 
			int invitedPersonAccessLevel,
			UserSessionObject userSessionObject,
			Integer projectAuthShareableObjectId,
			UserInviteResult userInviteResult ) throws Exception {
		AuthUserInviteTrackingDTO authUserInviteTrackingDTO = new AuthUserInviteTrackingDTO();
		authUserInviteTrackingDTO.setInvitedUserAccessLevel( invitedPersonAccessLevel);
		authUserInviteTrackingDTO.setInvitedUserEmail( invitedPersonEmail );
		authUserInviteTrackingDTO.setSubmitIP( request.getRemoteAddr() );
		if ( projectAuthShareableObjectId != null ) {
			authUserInviteTrackingDTO.setInvitedSharedObjectId( projectAuthShareableObjectId );
		}
		XLinkUserDTO userDatabaseRecord = userSessionObject.getUserDBObject();
		authUserInviteTrackingDTO.setSubmittingAuthUserId( userDatabaseRecord.getAuthUser().getId() );
		GenerateInviteCodeSaveInviteRecordService.getInstance().generateInviteCodeSaveInviteRecordService( authUserInviteTrackingDTO );
		//  Generate email with invite code
		// Generate and send the email to the user.
		try {
        	SendEmailDTO sendEmailDTO = createMailMessageToSend( authUserInviteTrackingDTO, userDatabaseRecord, request );
        	SendEmail.getInstance().sendEmail( sendEmailDTO );
			userInviteResult.setStatus(true);
			userInviteResult.setEmailSent(true);
		}
		catch (Exception e) {
			log.error( "UserInviteService: Exception: invitedPersonEmail: " + invitedPersonEmail, e );
			removeInvite( authUserInviteTrackingDTO );
			userInviteResult.setUnableToSendEmailError(true);
		}
	}
	
	/**
	 * @param authUserInviteTrackingDTO
	 */
	private void removeInvite( AuthUserInviteTrackingDTO authUserInviteTrackingDTO ) {
		if ( authUserInviteTrackingDTO.getId() != 0 ) {
			try {
				AuthUserInviteTrackingDAO.getInstance().delete( authUserInviteTrackingDTO.getId() );
			} catch ( Exception e ) {
				log.warn( "Failed to remove invite for id: " + authUserInviteTrackingDTO.getId(), e );
			}
		}
	}
	
	/**
	 * @param authUserInviteTrackingDTO
	 * @param userDatabaseRecord
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	private SendEmailDTO createMailMessageToSend( AuthUserInviteTrackingDTO authUserInviteTrackingDTO, XLinkUserDTO userDatabaseRecord, HttpServletRequest request )
	throws Exception {
		//  Does NOT include slash after web app context
		String requestURLIncludingWebAppContext = (String) request.getAttribute( WebConstants.REQUEST_URL_ONLY_UP_TO_WEB_APP_CONTEXT );
		String newURL = requestURLIncludingWebAppContext + StrutsActionPathsConstants.USER_INVITE_PROCESS_CODE
				+ "?" + WebConstants.PARAMETER_INVITE_CODE + "=" + authUserInviteTrackingDTO.getInviteTrackingCode();
		// set the message body
		String text = 
				"You have been invited to the ProXL DB web application by "
				+ userDatabaseRecord.getFirstName()
				+ " "
				+ userDatabaseRecord.getLastName()
				+ " at "
				+ userDatabaseRecord.getOrganization()
				+ ".\n\n"
				+ "To create an account follow this link: " + newURL + "\n\n"
			+ "\n\n"
		 	+ "Thank you\n\nThe ProXL DB";
		String fromEmailAddress = GetEmailConfig.getFromAddress();
		String toEmailAddress = authUserInviteTrackingDTO.getInvitedUserEmail();
		String emailSubject = "Invite Email For ProXL DB Webapp"; 
		String emailBody = text;
		SendEmailDTO sendEmailDTO = new SendEmailDTO();
		sendEmailDTO.setFromEmailAddress( fromEmailAddress );
		sendEmailDTO.setToEmailAddress( toEmailAddress );
		sendEmailDTO.setEmailSubject( emailSubject );
		sendEmailDTO.setEmailBody( emailBody );
		return sendEmailDTO;
	}
	
	/**
	 * 
	 *
	 */
	public static class ResendInviteEmailResult {
		
		private boolean status;

		public boolean isStatus() {
			return status;
		}

		public void setStatus(boolean status) {
			this.status = status;
		}
		
	}

	/**
	 * Resend Invite Email
	 * 
	 * @param inviteIdString
	 * @param projectIdString
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/resendInviteEmail") 
	public ResendInviteEmailResult userResendInviteEmailService(   
			@FormParam( "inviteId" ) String inviteIdString,
			@FormParam( "projectId" ) String projectIdString,
			@Context HttpServletRequest request )
	throws Exception {
		
		ResendInviteEmailResult resendInviteEmailResult = new ResendInviteEmailResult();
		
		//  Restricted to users with ACCESS_LEVEL_ASSISTANT_PROJECT_OWNER or better

		Integer projectId = null;
		if ( StringUtils.isEmpty( projectIdString ) ) {
			log.warn( "UserInviteService: userResendInviteEmailService():  projectId empty or not populated: " + projectIdString );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		try {
			projectId = Integer.parseInt( projectIdString );
		} catch (Exception e) {
			log.warn( "UserInviteService: userResendInviteEmailService():  projectId is not valid integer: " + projectIdString, e );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		Integer inviteTrackingId = null;
		if ( StringUtils.isEmpty( inviteIdString ) ) {
			log.warn( "UserInviteService: userResendInviteEmailService():  inviteId empty or not populated: " + inviteIdString );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		try {
			inviteTrackingId = Integer.parseInt( inviteIdString );
		} catch (Exception e) {
			log.warn( "UserInviteService: userResendInviteEmailService():  inviteId is not valid integer: " + inviteIdString, e );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		
		try {

			///////////
			//  Auth Check for Project:

			// Get the session first.  
//			HttpSession session = request.getSession();
			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
			UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			//  Test access to the project id
			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();
			if ( ! authAccessLevel.isAssistantProjectOwnerAllowed() ) {
				//  No Access Allowed for this project id
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			AuthUserInviteTrackingDTO authUserInviteTrackingDTO = AuthUserInviteTrackingDAO.getInstance().getForInviteTrackingId( inviteTrackingId );
			if ( authUserInviteTrackingDTO == null ) {
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}
		
			Integer invitedSharedObjectId = authUserInviteTrackingDTO.getInvitedSharedObjectId();
			if ( invitedSharedObjectId == null ) {
				//  For now, reject request to send email for
				log.warn( "Currently not support re-sending invite email for invite not tied to project" );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			ProjectDAO projectDAO = ProjectDAO.getInstance();
			ProjectDTO projectDTO = projectDAO.getProjectDTOForProjectId( projectId );
			if ( projectDTO == null ) {
				log.warn( "projectId is not in database: " + projectId );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			if ( projectDTO.getAuthShareableObjectId() != invitedSharedObjectId.intValue() ) {
				log.warn( "projectDTO.getAuthShareableObjectId() != authUserInviteTrackingDTO.getInvitedSharedObjectId()" );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}
		
			XLinkUserDTO userDatabaseRecord = userSessionObject.getUserDBObject();

			//  Generate email with invite code
			// Generate and send the email to the user.
			try {
	        	SendEmailDTO sendEmailDTO = createMailMessageToSend( authUserInviteTrackingDTO, userDatabaseRecord, request );
	        	SendEmail.getInstance().sendEmail( sendEmailDTO );
	        	resendInviteEmailResult.setStatus(true);
			}
			catch (Exception e) {
				log.error( "UserInviteService: userResendInviteEmailService(...): Exception: inviteIdString: " + inviteIdString, e );
			}
			
			return resendInviteEmailResult;
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
}
