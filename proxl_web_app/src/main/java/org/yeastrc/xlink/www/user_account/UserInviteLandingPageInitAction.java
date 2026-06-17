package org.yeastrc.xlink.www.user_account;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.spring_controllers.SpringActionMessages;
import org.yeastrc.auth.dto.AuthUserInviteTrackingDTO;
import org.yeastrc.xlink.www.dao.ProjectDAO;
import org.yeastrc.xlink.www.dto.ProjectDTO;
import org.yeastrc.xlink.www.user_web_utils.ValidateUserInviteTrackingCode;
import org.yeastrc.xlink.www.constants.WebConstants;
/**
 * 
 *
 */
public class UserInviteLandingPageInitAction {

	private static final Logger log = LoggerFactory.getLogger( UserInviteLandingPageInitAction.class);

	private static final int MAX_TITLE_DISPLAY_LENGTH = 40;

	public String execute( HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {
		try {
			String inviteTrackingCode = request.getParameter( WebConstants.PARAMETER_INVITE_CODE );
			ValidateUserInviteTrackingCode validateUserInviteTrackingCode = ValidateUserInviteTrackingCode.getInstance( inviteTrackingCode );
			if ( ! validateUserInviteTrackingCode.validateInviteTrackingCode() ) {
				String errorMsgKey = validateUserInviteTrackingCode.getErrorMsgKey();
				SpringActionMessages.setErrorMessageKey( request, errorMsgKey );
				return "Failure";
			}
			AuthUserInviteTrackingDTO authUserInviteTrackingDTO =  validateUserInviteTrackingCode.getAuthUserInviteTrackingDTO();
			Integer inviteSharedObjectId = authUserInviteTrackingDTO.getInvitedSharedObjectId();
			if ( inviteSharedObjectId != null ) {
				//  Get the project title
				ProjectDTO project = ProjectDAO.getInstance().getProjectDTOForAuthShareableObjectId( inviteSharedObjectId );
				if ( project == null ) {
					SpringActionMessages.setErrorMessageKey( request, "error.invite.process.project.not.exist" );
					return "Failure";
				}
				String titleDisplay = project.getTitle();
				if ( titleDisplay == null ) {
					SpringActionMessages.setErrorMessageKey( request, "error.invite.process.code.general" );
					return "Failure";
				}
				if ( titleDisplay.length() > MAX_TITLE_DISPLAY_LENGTH ) {
					titleDisplay = titleDisplay.substring( 0, MAX_TITLE_DISPLAY_LENGTH );
				}
				request.setAttribute( "titleDisplay", titleDisplay );
			}
			request.setAttribute( "inviteCode", inviteTrackingCode );
			return "Success";
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}
}
