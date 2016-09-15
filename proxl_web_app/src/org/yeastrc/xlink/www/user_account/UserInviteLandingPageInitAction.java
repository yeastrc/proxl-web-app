package org.yeastrc.xlink.www.user_account;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.yeastrc.auth.dto.AuthUserInviteTrackingDTO;
import org.yeastrc.xlink.www.dao.ProjectDAO;
import org.yeastrc.xlink.www.dto.ProjectDTO;
import org.yeastrc.xlink.www.user_web_utils.ValidateUserInviteTrackingCode;
import org.yeastrc.xlink.www.constants.WebConstants;

/**
 * 
 *
 */

public class UserInviteLandingPageInitAction extends Action {
	
	private static final Logger log = Logger.getLogger(UserInviteLandingPageInitAction.class);
	
	private static final int MAX_TITLE_DISPLAY_LENGTH = 40;
	

	public ActionForward execute( ActionMapping mapping,
			  ActionForm actionForm,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {

		try {

			String inviteTrackingCode = request.getParameter( WebConstants.PARAMETER_INVITE_CODE );

			
			ValidateUserInviteTrackingCode validateUserInviteTrackingCode = ValidateUserInviteTrackingCode.getInstance( inviteTrackingCode );

			if ( ! validateUserInviteTrackingCode.validateInviteTrackingCode() ) {

				String errorMsgKey = validateUserInviteTrackingCode.getErrorMsgKey();
	
				ActionMessages messages = new ActionMessages();
				messages.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage( errorMsgKey ) );
				saveErrors( request, messages );
				
				return mapping.findForward("Failure");
			}
			
			
			AuthUserInviteTrackingDTO authUserInviteTrackingDTO =  validateUserInviteTrackingCode.getAuthUserInviteTrackingDTO();

			Integer inviteSharedObjectId = authUserInviteTrackingDTO.getInvitedSharedObjectId();
			
			if ( inviteSharedObjectId != null ) {
				
				//  Get the project title
				
				ProjectDTO project = ProjectDAO.getInstance().getProjectDTOForAuthShareableObjectId( inviteSharedObjectId );
				
				if ( project == null ) {
					
					ActionErrors errors = new ActionErrors();
					errors.add("username", new ActionMessage("error.invite.process.project.not.exist"));
					saveErrors( request, errors );
					
					return mapping.findForward("Failure");
				}
				
				String titleDisplay = project.getTitle();
				
				if ( titleDisplay == null ) {
					
					ActionErrors errors = new ActionErrors();
					errors.add("username", new ActionMessage("error.invite.process.code.general"));
					saveErrors( request, errors );
					
					return mapping.findForward("Failure");
				}
				
				if ( titleDisplay.length() > MAX_TITLE_DISPLAY_LENGTH ) {
					
					titleDisplay = titleDisplay.substring( 0, MAX_TITLE_DISPLAY_LENGTH );
				}
				
				request.setAttribute( "titleDisplay", titleDisplay );
			}
			
			
			request.setAttribute( "inviteCode", inviteTrackingCode );
			
			
			return mapping.findForward("Success");
			
			
			
		} catch ( Exception e ) {
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
		}

	}

		
}
