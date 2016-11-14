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
import org.yeastrc.auth.dao.AuthUserDAO;
import org.yeastrc.auth.dto.AuthUserDTO;
import org.yeastrc.auth.hash_password.HashedPasswordProcessing;
import org.yeastrc.xlink.base.config_system_table_common_access.ConfigSystemsKeysSharedConstants;
import org.yeastrc.xlink.base.config_system_table_common_access.ConfigSystemsValuesSharedConstants;
import org.yeastrc.xlink.www.dao.TermsOfServiceTextVersionsDAO;
import org.yeastrc.xlink.www.dao.TermsOfServiceUserAcceptedVersionHistoryDAO;
import org.yeastrc.xlink.www.dao.XLinkUserDAO;
import org.yeastrc.xlink.www.dto.TermsOfServiceTextVersionsDTO;
import org.yeastrc.xlink.www.dto.TermsOfServiceUserAcceptedVersionHistoryDTO;
import org.yeastrc.xlink.www.dto.XLinkUserDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappConfigException;
import org.yeastrc.xlink.www.config_system_table.ConfigSystemCaching;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.objects.LoginResult;
import org.yeastrc.xlink.www.user_account.UserSessionObject;


@Path("/user")
public class LoginService {

	private static final Logger log = Logger.getLogger(LoginService.class);
	
	private static final String RETURN_TOS_TRUE = "true";
	
	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/login") 
	public Response loginService(   
			@FormParam( "username" ) String username,
			@FormParam( "password" ) String password,
			@FormParam( "return_tos" ) String returnTOS,
			@FormParam( "tos_key" ) String tosAcceptedKey,
			@Context HttpServletRequest request )
	throws Exception {

		LoginResult loginResult = loginServiceLocal( username, password, returnTOS, tosAcceptedKey, request );
//		.cookie(new NewCookie("name", "Hello, world!"))
		return Response.ok(loginResult).build();
	}
	
	/**
	 * @param username
	 * @param password
	 * @param returnTOS
	 * @param tosAcceptedKey
	 * @param request
	 * @return
	 */
	private LoginResult loginServiceLocal(   
			String username,
			String password,
			String returnTOS,
			String tosAcceptedKey,
			HttpServletRequest request ) {
	
		LoginResult loginResult = new LoginResult();
		if ( StringUtils.isEmpty( username ) ) {
			log.warn( "LoginService:  username empty: " + username );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);  //  Early Exit with Data Exception
		}
		if ( StringUtils.isEmpty( password ) ) {
			log.warn( "LoginService:  password empty: " + password );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);  //  Early Exit with Data Exception
		}
//		if (true)
//		throw new Exception("Forced Error");
		try {
			// Get their session first.  
			HttpSession session = request.getSession();
			// Make sure this username exists!		
			XLinkUserDTO userDatabaseRecord;
			userDatabaseRecord = XLinkUserDAO.getInstance().getXLinkUserDTOForUsername( username );
			if ( userDatabaseRecord == null ) {
		        loginResult.setInvalidUserOrPassword(true);
				return loginResult;  //  Early Exit
			}
			String userDatabasePasswordHashed = AuthUserDAO.getInstance().getPasswordHashedForId( userDatabaseRecord.getAuthUser().getId() );
			if ( ! HashedPasswordProcessing.getInstance().comparePasswordToHashedPasswordHex( password, userDatabasePasswordHashed ) ) {
				// Invalid password
		        loginResult.setInvalidUserOrPassword(true);
				return loginResult;  //  Early Exit
			}
			AuthUserDTO authUserDTO = userDatabaseRecord.getAuthUser();
			if ( ! authUserDTO.isEnabled() ) {
		        loginResult.setDisabledUser(true);
				return loginResult;  //  Early Exit
			}
			
			//  Is terms of service enabled?
			String termsOfServiceEnabledString =
					ConfigSystemCaching.getInstance()
					.getConfigValueForConfigKey( ConfigSystemsKeysSharedConstants.TERMS_OF_SERVICE_ENABLED );
			boolean termsOfServiceEnabled = false;
			if ( ConfigSystemsValuesSharedConstants.TRUE.equals(termsOfServiceEnabledString) ) {
				termsOfServiceEnabled = true;
			}
			
			if ( termsOfServiceEnabled ) {
				// terms of service is enabled
				//  Has user accepted latest version
				Integer tosLatestVersionId = TermsOfServiceTextVersionsDAO.getInstance().getLatestVersionId();
				if ( tosLatestVersionId == null ) {
					String msg = "Config/Terms Of Service Error. terms of service is enabled but no terms of service text record ";
					log.error( msg );
					throw new ProxlWebappConfigException(msg);
				} else {
					TermsOfServiceUserAcceptedVersionHistoryDTO tosUserAccepted =
							TermsOfServiceUserAcceptedVersionHistoryDAO.getInstance()
							.getForAuthUserIdTermsOfServiceVersionId( userDatabaseRecord.getAuthUser().getId(), tosLatestVersionId );
					if ( tosUserAccepted == null ) {
						if ( StringUtils.isEmpty( tosAcceptedKey ) ) {
							//  User has not accepted latest TOS.
							//   User is not logged in.
							loginResult.setTermsOfServiceAcceptanceRequired(true);
							if ( RETURN_TOS_TRUE.equals( returnTOS ) ) {
								TermsOfServiceTextVersionsDTO termsOfServiceTextVersionsDTO = 
										TermsOfServiceTextVersionsDAO.getInstance().getLatest();
								loginResult.setTermsOfServiceKey( termsOfServiceTextVersionsDTO.getIdString() );
								loginResult.setTermsOfServiceText( termsOfServiceTextVersionsDTO.getTermsOfServiceText() );
							}
							return loginResult;  //  Early Exit
						} else {
							Integer termsOfServiceVersionIdForIdString =
									TermsOfServiceTextVersionsDAO.getInstance().getVersionIdForIdString( tosAcceptedKey );
							if ( termsOfServiceVersionIdForIdString == null ) {
								String msg = "No record for tosAcceptedKey: " + tosAcceptedKey;
								log.warn( msg );
								throw new WebApplicationException(
										Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
										.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
										.build() );  //  Early Exit with Data Exception
							}
							int authUserId = authUserDTO.getId();
							tosUserAccepted = new TermsOfServiceUserAcceptedVersionHistoryDTO();
							tosUserAccepted.setAuthUserId( authUserId );
							tosUserAccepted.setTermsOfServiceVersionId( termsOfServiceVersionIdForIdString );
							TermsOfServiceUserAcceptedVersionHistoryDAO.getInstance().save( tosUserAccepted );
							//  Next validate that user accepted the LATEST Terms of Service
							if ( termsOfServiceVersionIdForIdString != tosLatestVersionId ) {
								//  User has not accepted latest TOS.
								//   User is not logged in.
								loginResult.setTermsOfServiceAcceptanceRequired(true);
								if ( RETURN_TOS_TRUE.equals( returnTOS ) ) {
									TermsOfServiceTextVersionsDTO termsOfServiceTextVersionsDTONewLatest = 
											TermsOfServiceTextVersionsDAO.getInstance().getLatest();
									loginResult.setTermsOfServiceKey( termsOfServiceTextVersionsDTONewLatest.getIdString() );
									loginResult.setTermsOfServiceText( termsOfServiceTextVersionsDTONewLatest.getTermsOfServiceText() );
								}
								return loginResult;  //  Early Exit
							}
						}
					}
				}
			}
			// Save the login info in the user.
			userDatabaseRecord.getAuthUser().setLastLogin(new java.util.Date());
			userDatabaseRecord.getAuthUser().setLastLoginIP( request.getRemoteAddr() );
			XLinkUserDAO.getInstance().updateLastLogin( userDatabaseRecord.getAuthUser().getId(),  request.getRemoteAddr() );
//			LastLoginUpdaterObject lastLoginUpdaterObject = new LastLoginUpdaterObject();
//			
//			lastLoginUpdaterObject.setAuthUserDTO( authUserDTO );
//			
//			LastLoginUpdaterQueue.addLastLoginUpdaterObject(lastLoginUpdaterObject);
			UserSessionObject userSessionObject = new UserSessionObject();
			userSessionObject.setUserDBObject( userDatabaseRecord );
			session.setAttribute( WebConstants.SESSION_CONTEXT_USER_LOGGED_IN, userSessionObject );
			loginResult.setStatus(true);
			
			return loginResult;
			
		} catch ( WebApplicationException e ) {
			throw e; //  Data exception so just rethrow
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
