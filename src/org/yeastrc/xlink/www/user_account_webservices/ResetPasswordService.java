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
import org.apache.log4j.Logger;
import org.yeastrc.auth.dao.AuthForgotPasswordTrackingDAO;
import org.yeastrc.auth.dao.AuthUserDAO;
import org.yeastrc.auth.dto.AuthForgotPasswordTrackingDTO;
import org.yeastrc.auth.hash_password.HashedPasswordProcessing;
import org.yeastrc.auth.services.AuthGenerateSaveForgotPasswordCode;
import org.yeastrc.xlink.www.dao.XLinkUserDAO;
import org.yeastrc.xlink.www.dto.XLinkUserDTO;
import org.yeastrc.xlink.www.constants.StrutsActionPathsConstants;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.objects.ResetPasswordChangePasswordResult;
import org.yeastrc.xlink.www.objects.ResetPasswordGenEmailResult;
import org.yeastrc.xlink.www.send_email.GetEmailConfig;
import org.yeastrc.xlink.www.send_email.SendEmail;
import org.yeastrc.xlink.www.send_email.SendEmailDTO;
import org.yeastrc.xlink.www.user_web_utils.ValidateUserResetPasswordCode;
import org.yeastrc.xlink.www.web_utils.GetMessageTextFromKeyFrom_web_app_application_properties;



@Path("/user")
public class ResetPasswordService {

	private static final Logger log = Logger.getLogger(ResetPasswordService.class);
	
	
	
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


			// Make sure this username exists!		
			XLinkUserDTO userDatabaseRecord;

			if ( StringUtils.isNotEmpty( username ) ) {
				
				userDatabaseRecord = XLinkUserDAO.getInstance().getXLinkUserDTOForUsername( username );

			} else {

				userDatabaseRecord = XLinkUserDAO.getInstance().getXLinkUserDTOForEmail( email );
			}
			
			
			if ( userDatabaseRecord == null ) {
				
		        resetPasswordGenEmailResult.setInvalidUsernameOrEmail(true);
				
				return resetPasswordGenEmailResult;  //  Early Exit
			}


			int authUserId = userDatabaseRecord.getAuthUser().getId();
			

			String authCode = AuthGenerateSaveForgotPasswordCode.getInstance().generateSaveForgotPasswordCode( authUserId, submitIP );

			// Generate and send the email to the user.
	        try {
	        	
	        	SendEmailDTO sendEmailDTO = createMailMessageToSend( userDatabaseRecord, authCode, request );
	        	
	        	SendEmail.getInstance().sendEmail( sendEmailDTO );
	        }
	        catch (Exception e) {

	        	log.error( "resetPasswordGetEmailService: Exception: user email: " + userDatabaseRecord.getAuthUser().getEmail(), e );

	        	return resetPasswordGenEmailResult;  //  Early Exit
	        }

			
			
			
			
	        resetPasswordGenEmailResult.setStatus(true);
			
			return resetPasswordGenEmailResult;
			
		} catch ( WebApplicationException e ) {

			throw e;
			
		} catch ( Exception e ) {
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
		}
				
	}
	

	/**
	 * @param user
	 * @param authCode
	 * @return
	 * @throws Exception 
	 * @throws SQLException 
	 */
	private SendEmailDTO createMailMessageToSend( XLinkUserDTO userDatabaseRecord, String authCode, HttpServletRequest request )
	throws Exception {


		
		//  Does NOT include slash after web app context
		String requestURLIncludingWebAppContext = (String) request.getAttribute( WebConstants.REQUEST_URL_ONLY_UP_TO_WEB_APP_CONTEXT );
		
		String newURL = requestURLIncludingWebAppContext + StrutsActionPathsConstants.USER_RESET_PASSWORD_PROCESS_CODE
				+ "?" + WebConstants.PARAMETER_RESET_PASSWORD_CODE + "=" + authCode;
		
		
		// set the message body
		String text = "Click this link to change your password: " + newURL + "\n\n"
			+ "\n\n"
		 	+ "Username: " + userDatabaseRecord.getAuthUser().getUsername() + "\n\n"

		 	+ "Thank you\n\nThe ProXL DB";



		String fromEmailAddress = GetEmailConfig.getFromAddress();
		String toEmailAddress = userDatabaseRecord.getAuthUser().getEmail();
		String emailSubject = "Reset Password Email For ProXL DB Webapp"; 
		String emailBody = text;


		SendEmailDTO sendEmailDTO = new SendEmailDTO();
		
		sendEmailDTO.setFromEmailAddress( fromEmailAddress );
		sendEmailDTO.setToEmailAddress( toEmailAddress );
		sendEmailDTO.setEmailSubject( emailSubject );
		sendEmailDTO.setEmailBody( emailBody );
		
		return sendEmailDTO;
	}
	


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
			
			XLinkUserDTO userDatabaseRecord = validateUserResetPasswordCode.getUserDatabaseRecord();

			int authUserId = userDatabaseRecord.getAuthUser().getId();
			
			AuthForgotPasswordTrackingDTO forgotPwdTrk = validateUserResetPasswordCode.getForgotPwdTrk();
			
			
			String passwordHashed = HashedPasswordProcessing.getInstance().createNewHashedPasswordHex( password );
		
			
			AuthUserDAO.getInstance().updatePasswordHashed( authUserId, passwordHashed );
			
			
			AuthForgotPasswordTrackingDAO.getInstance().updateUsedDateUseIP( forgotPwdTrk.getId(), submitIP );

			
	        resetPasswordChangePasswordResult.setStatus(true);
			
			return resetPasswordChangePasswordResult;
			
		} catch ( WebApplicationException e ) {

			throw e;
			
		} catch ( Exception e ) {
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
		}
				
	}


}
