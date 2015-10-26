package org.yeastrc.xlink.www.user_web_utils;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.auth.dao.AuthUserInviteTrackingDAO;
import org.yeastrc.auth.dto.AuthUserInviteTrackingDTO;
import org.yeastrc.xlink.www.constants.AllowedTimeConstants;
import org.yeastrc.xlink.www.dao.ProjectDAO;
import org.yeastrc.xlink.www.dto.ProjectDTO;

/**
 * 
 *
 */
public class ValidateUserInviteTrackingCode {

	private static final Logger log = Logger.getLogger(ValidateUserInviteTrackingCode.class);
	
	private String inviteTrackingCode = null;
	
	private boolean validationSearch = false;
	
	private AuthUserInviteTrackingDTO authUserInviteTrackingDTO = null;

	private String errorMsgKey = null;


	
	private ValidateUserInviteTrackingCode( String inviteTrackingCode ) { 

		if ( StringUtils.isEmpty( inviteTrackingCode ) ) {
			
			String msg = "'inviteTrackingCode' is empty";
			throw new IllegalArgumentException( msg );
		}

		this.inviteTrackingCode = inviteTrackingCode;
	}
	
	public static ValidateUserInviteTrackingCode getInstance( String inviteTrackingCode ) { 
		return new ValidateUserInviteTrackingCode( inviteTrackingCode); 
	}
	
	
	public AuthUserInviteTrackingDTO getAuthUserInviteTrackingDTO() {

		if ( ! validationSearch ) {
			
			String msg = "'validationSearch' is false";
			throw new IllegalStateException( msg );
		}

		return authUserInviteTrackingDTO;
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
	public boolean validateInviteTrackingCode() throws Exception {

		validationSearch = true;
		
		try {

			if ( StringUtils.isEmpty( inviteTrackingCode ) ) {
				errorMsgKey = "error.invite.process.invalid.code";
				return false;
			}

			
			authUserInviteTrackingDTO = AuthUserInviteTrackingDAO.getInstance().getForInviteTrackingCode( inviteTrackingCode );
			
			if ( authUserInviteTrackingDTO == null ) {
				errorMsgKey = "error.invite.process.invalid.code";
				return false;
			}
			
			
			if ( authUserInviteTrackingDTO.isInviteUsed() ) {
				errorMsgKey = "error.invite.process.code.already.used";
				return false;
			}

			
			
//			long createDateMilliSec = authUserInviteTrackingDTO.getInviteCreateDate().getTime();
//			
//			long nowInMilliSec = System.currentTimeMillis();
//			
//			if ( createDateMilliSec + AllowedTimeConstants.DURATION_FORGOT_PASSWORD_CODE_VALID < nowInMilliSec ) {
//				errorMsgKey = "error.invite.process.code.too.old";
//				return false;
//			}
			
			
			if ( authUserInviteTrackingDTO.isCodeReplacedByNewer() ) {
				errorMsgKey = "error.invite.process.code.replaced.by.newer";
				return false;
			}
					
			
			if ( authUserInviteTrackingDTO.isInviteRevoked() ) {
				
				errorMsgKey = "error.invite.process.code.revoked";
				return false;
			}
			
			Integer inviteSharedObjectId = authUserInviteTrackingDTO.getInvitedSharedObjectId();
			
			if ( inviteSharedObjectId != null ) {
				
				ProjectDTO project = ProjectDAO.getInstance().getProjectDTOForAuthShareableObjectId( inviteSharedObjectId );

				if ( project == null ) {

					errorMsgKey = "error.invite.process.project.not.exist";
					return false;

				}	

				if ( project.isMarkedForDeletion() || ( ! project.isEnabled() ) ) {

					errorMsgKey = "error.invite.process.project.not.exist";
					return false;
				}		
			}
			
			return true;
			
			
			
		} catch ( Exception e ) {
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
		}

	}

}
