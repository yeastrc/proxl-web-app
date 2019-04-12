package org.yeastrc.xlink.www.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.auth.dao.AuthSharedObjectDAO;
import org.yeastrc.auth.dto.AuthSharedObjectDTO;
import org.yeastrc.xlink.www.access_control.common.AccessControl_GetUserSession_RefreshAccessEnabled;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.servlet_context.CurrentContext;
import org.yeastrc.xlink.www.user_session_management.UserSession;
import org.yeastrc.xlink.www.user_session_management.UserSessionAlterSession;
import org.yeastrc.xlink.www.dao.ProjectDAO;
import org.yeastrc.xlink.www.dto.ProjectDTO;
/**
 * 
 *
 */
public class ProjectReadProcessCodeAction extends Action {
	
	private static final Logger log = LoggerFactory.getLogger( ProjectReadProcessCodeAction.class);
	
	@Override
	public ActionForward execute( ActionMapping mapping,
			  ActionForm form,
			  HttpServletRequest request,
			  HttpServletResponse response ) throws Exception {
		try {
			if ( true ) {
				int z = 0;
			}

			UserSession userSession =
					AccessControl_GetUserSession_RefreshAccessEnabled.getSinglesonInstance()
					.getUserSession_RefreshAccessEnabled( request );
			String projectPublicAccessCode = request.getParameter( WebConstants.PARAMETER_PROJECT_READ_CODE );
			if ( StringUtils.isEmpty( projectPublicAccessCode ) ) {
				return mapping.findForward( "Failure" );
			}
			AuthSharedObjectDTO authSharedObjectDTO = AuthSharedObjectDAO.getInstance().getForPublicAccessCode( projectPublicAccessCode );
			if ( authSharedObjectDTO == null ) {
				return mapping.findForward( "Failure" );
			}
			if ( ! authSharedObjectDTO.isPublicAccessCodeEnabled() ) {
				return mapping.findForward( "Failure" );
			}
			ProjectDTO projectDTO = ProjectDAO.getInstance().getProjectDTOForAuthShareableObjectId( authSharedObjectDTO.getSharedObjectId() );
			if ( projectDTO == null ) {
				return mapping.findForward( "Failure" );
			}
			
			UserSessionAlterSession.getSinglesonInstance()
			.add_AllowedReadAccessProjectId_AllowedReadAccessProjectPublicAccessCodes(
					projectDTO.getId(), 
					projectPublicAccessCode, 
					userSession,  //  userSession can be null, handled in the method
					request );
			
			request.setAttribute( WebConstants.REQUEST_PROJECT_ID, projectDTO.getId() );
			String redirectAfterProcess = request.getParameter( WebConstants.PARAMETER_REDIRECT_AFTER_PROCESS_PROJECT_READ_CODE );
			if ( WebConstants.PARAMETER_REDIRECT_AFTER_PROCESS_PROJECT_READ_CODE_TRUE.equalsIgnoreCase( redirectAfterProcess ) ) {
				ActionForward actionForward = mapping.findForward( "Success" );
				String actionForwardPath = actionForward.getPath();
				String redirectPath = 
						CurrentContext.getCurrentWebAppContext()
						+ actionForwardPath + "?" + WebConstants.PARAMETER_PROJECT_ID + "=" + projectDTO.getId();
				response.sendRedirect( redirectPath );
				return null;
			}
			return mapping.findForward( "Success" );
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}
}
