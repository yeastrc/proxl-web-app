package org.yeastrc.xlink.www.user_account_webservices;

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
import org.yeastrc.auth.dao.AuthForgotPasswordTrackingDAO;
import org.yeastrc.auth.dao.AuthUserDAO;
import org.yeastrc.auth.dto.AuthForgotPasswordTrackingDTO;
import org.yeastrc.auth.services.AuthGenerateSaveForgotPasswordCode;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.www.constants.StrutsActionPathsConstants;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.objects.ResetPasswordChangePasswordResult;
import org.yeastrc.xlink.www.objects.ResetPasswordGenEmailResult;
import org.yeastrc.xlink.www.send_email.GetEmailConfig;
import org.yeastrc.xlink.www.send_email.SendEmail;
import org.yeastrc.xlink.www.send_email.SendEmailDTO;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtCentralWebappWebserviceAccess;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtGetUserDataRequest;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtGetUserDataResponse;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtResetPasswordRequest;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtResetPasswordResponse;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtSearchUserDataRequest;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtSearchUserDataResponse;
import org.yeastrc.xlink.www.user_web_utils.ValidateUserResetPasswordCode;
import org.yeastrc.xlink.www.web_utils.GetMessageTextFromKeyFrom_web_app_application_properties;

@Path("/user")
public class ResetPasswordService {

	private static final Logger log = LoggerFactory.getLogger( ResetPasswordService.class);
	
	//   Webservice that accepts username or email and sends an email to the user with a code in a URL for setting/changing the user's password
	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/resetPasswordGenEmail") 
	public ResetPasswordGenEmailResult resetPasswordGenEmailService(   
			@FormParam( "username" ) String username,
			@FormParam( "email" ) String email,
			@Context HttpServletRequest request )
	throws Exception {
		String submitIP = request.getRemoteAddr();
		ResetPasswordGenEmailResult resetPasswordGenEmailResult = new ResetPasswordGenEmailResult();
		if ( StringUtils.isEmpty( username ) && StringUtils.isEmpty(email) ) {
			log.warn( "ResetPasswordService:  username and email both empty " );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		if ( StringUtils.isNotEmpty( username ) && StringUtils.isNotEmpty( email ) ) {
			log.warn( "ResetPasswordService:  username and email both have values " );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
//		if (true)
//		throw new Exception("Forced Error");
		try {
			UserMgmtSearchUserDataResponse userMgmtSearchUserDataResponse = null;
			// Make sure this username exists!		
			if ( StringUtils.isNotEmpty( username ) ) {
				//  Get User Mgmt User id list for username, exact match to username
				UserMgmtSearchUserDataRequest userMgmtSearchUserDataRequest = new UserMgmtSearchUserDataRequest();
				userMgmtSearchUserDataRequest.setSearchString( username );
				userMgmtSearchUserDataResponse = 
						UserMgmtCentralWebappWebserviceAccess.getInstance().searchUserDataByUsernameExactMatchNoUserSession( userMgmtSearchUserDataRequest );
				if ( ! userMgmtSearchUserDataResponse.isSuccess() ) {
					String msg = "Failed to look up username: " + username;
					log.error( msg );
					throw new ProxlWebappInternalErrorException(msg);
				}
				if ( userMgmtSearchUserDataResponse.getUserIdList() != null
						&& userMgmtSearchUserDataResponse.getUserIdList().size() > 1 ) {
					//  More than 1 user id returned.  Should not happen
					String msg = "More than one user mgmt user id returned for searching for exact match for username: " + username;
					log.error( msg );
					throw new ProxlWebappInternalErrorException(msg);
				}
			} else {
				//  Get User Mgmt User id list for email, exact match to email
				UserMgmtSearchUserDataRequest userMgmtSearchUserDataRequest = new UserMgmtSearchUserDataRequest();
				userMgmtSearchUserDataRequest.setSearchString( email );
				userMgmtSearchUserDataResponse = 
						UserMgmtCentralWebappWebserviceAccess.getInstance().searchUserDataByEmailExactMatchNoUserSession( userMgmtSearchUserDataRequest );
				if ( ! userMgmtSearchUserDataResponse.isSuccess() ) {
					String msg = "Failed to look up email: " + email;
					log.error( msg );
					throw new ProxlWebappInternalErrorException(msg);
				}
				if ( userMgmtSearchUserDataResponse.getUserIdList() != null
						&& userMgmtSearchUserDataResponse.getUserIdList().size() > 1 ) {
					//  More than 1 user id returned.  Should not happen
					String msg = "More than one user mgmt user id returned for searching for exact match for email: " + email;
					log.error( msg );
					throw new ProxlWebappInternalErrorException(msg);
				}
			}
			List<Integer> userMgmtUserIdListFromSearch = userMgmtSearchUserDataResponse.getUserIdList();
			if ( userMgmtUserIdListFromSearch == null || userMgmtUserIdListFromSearch.isEmpty() ) {
				//  username or email not found
				resetPasswordGenEmailResult.setInvalidUsernameOrEmail(true);
				return resetPasswordGenEmailResult;  //  Early Exit
			}
			// User Mgmt user Id
			int userMgmtUserIdFromSearch = userMgmtUserIdListFromSearch.get(0);
			//  Get Proxl Auth Uer Id from User Mgmt user Id
			Integer authUserId = AuthUserDAO.getInstance().getIdForUserMgmtUserId( userMgmtUserIdFromSearch );
			if ( authUserId == null ) {
				//  No Proxl Auth User Id for userMgmtUserIdFromSearch for username or email found
				resetPasswordGenEmailResult.setInvalidUsernameOrEmail(true);
				return resetPasswordGenEmailResult;  //  Early Exit
			}
			//  Get full user data
			UserMgmtGetUserDataRequest userMgmtGetUserDataRequest = new UserMgmtGetUserDataRequest();
			userMgmtGetUserDataRequest.setUserId( userMgmtUserIdFromSearch );
			UserMgmtGetUserDataResponse userMgmtGetUserDataResponse = 
					UserMgmtCentralWebappWebserviceAccess.getInstance().getUserData( userMgmtGetUserDataRequest );
			if ( ! userMgmtGetUserDataResponse.isSuccess() ) {
				String msg = "Failed to get Full user data from User Mgmt Webapp for user id: " + userMgmtUserIdFromSearch;
				log.error( msg );
				throw new ProxlWebappInternalErrorException(msg);
			}
			String authCode = AuthGenerateSaveForgotPasswordCode.getInstance().generateSaveForgotPasswordCode( authUserId, submitIP );
			// Generate and send the email to the user.
	        try {
	        	SendEmailDTO sendEmailDTO = createMailMessageToSend( userMgmtGetUserDataResponse, authCode, request );
	        	SendEmail.getInstance().sendEmail( sendEmailDTO );
	        }
	        catch (Exception e) {
	        	log.error( "resetPasswordGetEmailService: Exception: user email: " + userMgmtGetUserDataResponse.getEmail(), e );
	        	return resetPasswordGenEmailResult;  //  Early Exit
	        }
	        resetPasswordGenEmailResult.setStatus(true);
			return resetPasswordGenEmailResult;
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught in resetPasswordGenEmailService(...): " + e.toString();
			log.error( msg, e );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT ) // This string will be passed to the client
					.build()
					);
		}
	}
	/**
	 * @param user
	 * @param authCode
	 * @return
	 * @throws Exception 
	 * @throws SQLException 
	 */
	private SendEmailDTO createMailMessageToSend( UserMgmtGetUserDataResponse userMgmtGetUserDataResponse, String authCode, HttpServletRequest request )
	throws Exception {
		//  Does NOT include slash after web app context
		String requestURLIncludingWebAppContext = (String) request.getAttribute( WebConstants.REQUEST_URL_ONLY_UP_TO_WEB_APP_CONTEXT );
		String newURL = requestURLIncludingWebAppContext + StrutsActionPathsConstants.USER_RESET_PASSWORD_PROCESS_CODE
				+ "?" + WebConstants.PARAMETER_RESET_PASSWORD_CODE + "=" + authCode;
		// set the message body
		String text = "Click this link to change your password: " + newURL + "\n\n"
			+ "\n\n"
		 	+ "Username: " + userMgmtGetUserDataResponse.getUsername() + "\n\n"
		 	+ "Thank you\n\nThe ProXL DB";
		String fromEmailAddress = GetEmailConfig.getFromAddress();
		String toEmailAddress = userMgmtGetUserDataResponse.getEmail();
		String emailSubject = "Reset Password Email For ProXL DB Webapp"; 
		String emailBody = text;
		SendEmailDTO sendEmailDTO = new SendEmailDTO();
		sendEmailDTO.setFromEmailAddress( fromEmailAddress );
		sendEmailDTO.setToEmailAddress( toEmailAddress );
		sendEmailDTO.setEmailSubject( emailSubject );
		sendEmailDTO.setEmailBody( emailBody );
		return sendEmailDTO;
	}
	//////    Webservice to change the password
	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/resetPasswordUpdatePassword") 
	public ResetPasswordChangePasswordResult resetPasswordUpdatePasswordService(   
			@FormParam( "resetPasswordTrackingCode" ) String resetPasswordTrackingCode,
			@FormParam( "password" ) String password,
			@Context HttpServletRequest request )
	throws Exception {
		String submitIP = request.getRemoteAddr();
		ResetPasswordChangePasswordResult resetPasswordChangePasswordResult = new ResetPasswordChangePasswordResult();
		if ( StringUtils.isEmpty( resetPasswordTrackingCode ) ) {
			log.warn( "ResetPasswordService: resetPasswordUpdatePassword: resetPasswordTrackingCode empty " );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		if ( StringUtils.isEmpty( password ) ) {
			log.warn( "ResetPasswordService: resetPasswordUpdatePassword:  password empty " );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
//		if (true)
//		throw new Exception("Forced Error");
		try {
			ValidateUserResetPasswordCode validateUserResetPasswordCode = ValidateUserResetPasswordCode.getInstance( resetPasswordTrackingCode );
			if ( ! validateUserResetPasswordCode.validateResetPasswordCode() ) {
				String errorMsgKey = validateUserResetPasswordCode.getErrorMsgKey();
				String errorMessage = GetMessageTextFromKeyFrom_web_app_application_properties.getInstance().getMessageForKey( errorMsgKey );
		        resetPasswordChangePasswordResult.setStatus(false);
		        resetPasswordChangePasswordResult.setErrorMessage( errorMessage );
				return resetPasswordChangePasswordResult;  //  !!!!!  EARLY EXIT
			}
			AuthForgotPasswordTrackingDTO forgotPwdTrk = validateUserResetPasswordCode.getForgotPwdTrk();
			//  Make call to User Mgmt webapp to change the password
			int userMgmtUserId = validateUserResetPasswordCode.getUserMgmtUserId(); // userMgmtUserId assoc with Forgot Pwd Tracking code
			UserMgmtResetPasswordRequest userMgmtResetPasswordRequest = new UserMgmtResetPasswordRequest();
			userMgmtResetPasswordRequest.setUserMgmtUserId( userMgmtUserId );
			userMgmtResetPasswordRequest.setNewPassword( password );
			userMgmtResetPasswordRequest.setUserRemoteIP( request.getRemoteAddr() );
			UserMgmtResetPasswordResponse userMgmtResetPasswordResponse = 
					UserMgmtCentralWebappWebserviceAccess.getInstance().resetPassword( userMgmtResetPasswordRequest );
			if ( ! userMgmtResetPasswordResponse.isSuccess() ) {
				if ( userMgmtResetPasswordResponse.isUserIdNotValid() ) {
					String msg = "Failed to update password in User Mgmt Webapp.  User id not valid for user id: " + userMgmtUserId;
					log.error( msg );
					throw new ProxlWebappInternalErrorException(msg);
				}
				String msg = "Failed to update password in User Mgmt Webapp for user id: " + userMgmtUserId;
				log.error( msg );
				throw new ProxlWebappInternalErrorException(msg);
			}
			AuthForgotPasswordTrackingDAO.getInstance().updateUsedDateUseIP( forgotPwdTrk.getId(), submitIP );
	        resetPasswordChangePasswordResult.setStatus(true);
			return resetPasswordChangePasswordResult;
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught in resetPasswordUpdatePasswordService(...): " + e.toString();
			log.error( msg, e );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT ) // This string will be passed to the client
					.build()
					);
		}
	}
}
