package org.yeastrc.xlink.www.user_web_utils;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.auth.dao.AuthForgotPasswordTrackingDAO;
import org.yeastrc.auth.dao.AuthUserDAO;
import org.yeastrc.auth.dto.AuthForgotPasswordTrackingDTO;
import org.yeastrc.auth.dto.AuthUserDTO;
import org.yeastrc.xlink.www.constants.AllowedTimeConstants;
import org.yeastrc.xlink.www.dao.XLinkUserDAO;
import org.yeastrc.xlink.www.dto.XLinkUserDTO;

/**
 * 
 *
 */
public class ValidateUserResetPasswordCode {

	private static final Logger log = Logger.getLogger(ValidateUserResetPasswordCode.class);
	
	private String resetPasswordTrackingCode = null;
	
	private boolean validationSearch = false;
	
	private XLinkUserDTO userDatabaseRecord = null;
	private AuthForgotPasswordTrackingDTO forgotPwdTrk = null;

	private String errorMsgKey = null;


	
	private ValidateUserResetPasswordCode( String resetPasswordTrackingCode ) { 

		if ( StringUtils.isEmpty( resetPasswordTrackingCode ) ) {
			
			String msg = "'resetPasswordTrackingCode' is empty";
			throw new IllegalArgumentException( msg );
		}

		this.resetPasswordTrackingCode = resetPasswordTrackingCode;
	}
	
	public static ValidateUserResetPasswordCode getInstance( String resetPasswordTrackingCode ) { 
		return new ValidateUserResetPasswordCode( resetPasswordTrackingCode); 
	}
	
	
	
	public XLinkUserDTO getUserDatabaseRecord() throws IllegalStateException {

		if ( ! validationSearch ) {
			
			String msg = "'validationSearch' is false";
			throw new IllegalStateException( msg );
		}

		return userDatabaseRecord;
	}
	
	
	public AuthForgotPasswordTrackingDTO getForgotPwdTrk() {

		if ( ! validationSearch ) {
			
			String msg = "'validationSearch' is false";
			throw new IllegalStateException( msg );
		}

		return forgotPwdTrk;
	}


	public String getErrorMsgKey() throws IllegalStateException {

		if ( ! validationSearch ) {
			
			String msg = "'validationSearch' is false";
			throw new IllegalStateException( msg );
		}

		return errorMsgKey;
	}

	

	/**
	 * Search the validation
	 * @throws Exception
	 */
	public boolean validateResetPasswordCode() throws Exception {

		validationSearch = true;
		
		try {

			if ( StringUtils.isEmpty( resetPasswordTrackingCode ) ) {
				
				String msg = "'resetPasswordTrackingCode' is empty";
				throw new IllegalStateException( msg );
			}
			
			if ( StringUtils.isEmpty( resetPasswordTrackingCode ) ) {
				errorMsgKey = "error.resetpassword.process.invalid.code";
				return false;
			}

			
			forgotPwdTrk = AuthForgotPasswordTrackingDAO.getInstance().getForForgotPasswordTrackingCode( resetPasswordTrackingCode );
			
			if ( forgotPwdTrk == null ) {
				errorMsgKey = "error.resetpassword.process.invalid.code";
				return false;
			}
			
			
			if ( forgotPwdTrk.getUsedDate() != null ) {
				errorMsgKey = "error.resetpassword.process.code.already.used";
				return false;
			}
			
			long createDateMilliSec = forgotPwdTrk.getCreateDate().getTime();
			
			long nowInMilliSec = System.currentTimeMillis();
			
			
			
			
			if ( createDateMilliSec + AllowedTimeConstants.DURATION_FORGOT_PASSWORD_CODE_VALID < nowInMilliSec ) {
				errorMsgKey = "error.resetpassword.process.code.too.old";
				return false;
			}
			
			
			if ( forgotPwdTrk.isCodeReplacedByNewer() ) {
				errorMsgKey = "error.resetpassword.process.code.replaced.by.newer";
				return false;
			}
					
			
			AuthUserDTO authUser = AuthUserDAO.getInstance().getAuthUserDTOForId( forgotPwdTrk.getUserId() );

			if ( authUser == null ) {
				errorMsgKey = "error.resetpassword.process.code.general";
				return false;
			}

			userDatabaseRecord = XLinkUserDAO.getInstance().getXLinkUserDTOForAuthUserId( authUser.getId() );

			if ( userDatabaseRecord == null ) {

				errorMsgKey = "error.resetpassword.process.code.general";
				return false;
			}
			
			
			return true;
			
			
			
		} catch ( Exception e ) {
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
		}

	}

}
