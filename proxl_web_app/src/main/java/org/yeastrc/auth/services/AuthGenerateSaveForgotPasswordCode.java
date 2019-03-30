package org.yeastrc.auth.services;


import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.auth.dao.AuthForgotPasswordTrackingDAO;
import org.yeastrc.auth.dto.AuthForgotPasswordTrackingDTO;
import org.yeastrc.auth.utils.GenerateRandomStringForCode;


/**
 * 
 *	Generates a password code and saves it in the auth_forgot_password_tracking table
 */
public class AuthGenerateSaveForgotPasswordCode {
	
	private static final Logger log = LoggerFactory.getLogger( AuthGenerateSaveForgotPasswordCode.class);
	
	private AuthGenerateSaveForgotPasswordCode() { }
	private static final AuthGenerateSaveForgotPasswordCode _INSTANCE = new AuthGenerateSaveForgotPasswordCode();
	public static AuthGenerateSaveForgotPasswordCode getInstance() { return _INSTANCE; }
	
	/**
	 * Generates a password code and saves it in the auth_forgot_password_tracking table
	 * @param authUserId
	 * @param submitIP
	 * @return
	 * @throws Exception 
	 */
	public String generateSaveForgotPasswordCode( int authUserId, String submitIP ) throws Exception {
		try {
			String forgotPasswordTrackingCode = GenerateRandomStringForCode.getInstance().generateRandomStringForCode();

			AuthForgotPasswordTrackingDTO item = new AuthForgotPasswordTrackingDTO();
			item.setUserId( authUserId );
			item.setSubmitIP( submitIP );
			item.setForgotPasswordTrackingCode( forgotPasswordTrackingCode );

			AuthForgotPasswordTrackingDAO authForgotPasswordTrackingDAO = AuthForgotPasswordTrackingDAO.getInstance();

			authForgotPasswordTrackingDAO.save( item );
			
			authForgotPasswordTrackingDAO.updateCodeReplacedByNewer( item.getId(), true /* codeReplacedByNewer */ );

			return forgotPasswordTrackingCode;
			
		} catch ( Exception e ) {
			String msg = "Exception  authUserId: " + authUserId + ", submitIP: " +  submitIP + ", Exception: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}
}
