package org.yeastrc.xlink.www.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.db.IDBConnectionFactory;
import org.yeastrc.xlink.www.cached_data_mgmt.CachedDataCentralRegistry;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.db_web.DBConnectionFactoryWeb;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.user_session_management.UserSession;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import org.yeastrc.xlink.www.web_utils.TestIsUserSignedIn;
/**
 * 
 *
 */
public class CacheDataAllLogCurrentCacheSizesAction extends Action {
	
	private static final Logger log = LoggerFactory.getLogger(  CacheDataAllLogCurrentCacheSizesAction.class );
	
	@Override
	public ActionForward execute( ActionMapping mapping,
			  ActionForm form,
			  HttpServletRequest request,
			  HttpServletResponse response ) throws Exception {
		
		GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
				GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionNoProjectId( request, response );
		if ( accessAndSetupWebSessionResult.isNoSession() ) {
			//  No User session 
			response.setStatus( 401 );
			return mapping.findForward( StrutsGlobalForwardNames.NO_USER_SESSION );
		}
		UserSession userSession = accessAndSetupWebSessionResult.getUserSession();
		if ( ! TestIsUserSignedIn.getInstance().testIsUserSignedIn( userSession ) ) {
			//  No User session 
			response.setStatus( 401 );
			return mapping.findForward( StrutsGlobalForwardNames.NO_USER_SESSION );
		}
		WebSessionAuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getWebSessionAuthAccessLevel();
		if ( authAccessLevel == null || ( ! authAccessLevel.isAdminAllowed() ) ) {
			response.setStatus( 403 );
			return mapping.findForward( StrutsGlobalForwardNames.INSUFFICIENT_ACCESS_PRIVILEGE );
		}
		
		try {
			CachedDataCentralRegistry.getInstance().writeToLogAllCacheSizes();
			// Log GetDBConnection counts
			IDBConnectionFactory idbConnectionFactory = DBConnectionFactory.getDbConnectionFactoryImpl();
			if ( idbConnectionFactory != null && idbConnectionFactory instanceof DBConnectionFactoryWeb ) {
				DBConnectionFactoryWeb dbConnectionFactoryWeb = (DBConnectionFactoryWeb) idbConnectionFactory;
				dbConnectionFactoryWeb.printCurrentGetConnectionCounts();
			}
			return mapping.findForward( "Success" );
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			response.setStatus( 500 );
			return mapping.findForward( StrutsGlobalForwardNames.GENERAL_ERROR );
		}
	}
}
