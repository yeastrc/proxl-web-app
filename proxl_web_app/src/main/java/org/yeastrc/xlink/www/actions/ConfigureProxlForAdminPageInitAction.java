package org.yeastrc.xlink.www.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.file_import_proxl_xml_scans.constants.ProxlXMLFileUploadMaxFileSizeConstants;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.user_session_management.UserSession;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import org.yeastrc.xlink.www.web_utils.GetPageHeaderData;
import org.yeastrc.xlink.www.web_utils.TestIsUserSignedIn;

public class ConfigureProxlForAdminPageInitAction extends Action {
	
	private static final Logger log = LoggerFactory.getLogger( ConfigureProxlForAdminPageInitAction.class);
	
	@Override
	public ActionForward execute( ActionMapping mapping,
			  ActionForm form,
			  HttpServletRequest httpServletRequest,
			  HttpServletResponse httpServletResponse )
					  throws Exception {
		try {
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionNoProjectId( httpServletRequest, httpServletResponse );
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				return mapping.findForward( StrutsGlobalForwardNames.NO_USER_SESSION );
			}
			UserSession userSession = accessAndSetupWebSessionResult.getUserSession();
			if ( ! TestIsUserSignedIn.getInstance().testIsUserSignedIn( userSession ) ) {
				//  No User session 
				return mapping.findForward( StrutsGlobalForwardNames.NO_USER_SESSION );
			}
			WebSessionAuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getWebSessionAuthAccessLevel();
			if ( authAccessLevel == null || ( ! authAccessLevel.isAdminAllowed() ) ) {
				return mapping.findForward( StrutsGlobalForwardNames.INSUFFICIENT_ACCESS_PRIVILEGE );
			}
			


			httpServletRequest.setAttribute( 
					"ProxlXML_FileSize_From_Environment_Or_JVM_dashD_Property", 
					ProxlXMLFileUploadMaxFileSizeConstants.get_Max_ProxlXML_FileSize_From_Environment_Or_JVM_dashD_Property() );
			
			
			httpServletRequest.setAttribute( 
					"Max_Scan_FileSize_From_Environment_Or_JVM_dashD_Property", 
					ProxlXMLFileUploadMaxFileSizeConstants.get_Max_Scan_FileSize_From_Environment_Or_JVM_dashD_Property() );

			
			GetPageHeaderData.getInstance().getPageHeaderDataWithoutProjectId( httpServletRequest );
			return mapping.findForward( "Success" );
			
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}
}
