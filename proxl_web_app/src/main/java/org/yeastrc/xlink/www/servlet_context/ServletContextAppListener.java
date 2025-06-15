package org.yeastrc.xlink.www.servlet_context;

import java.util.Properties;
import javax.servlet.*;
import javax.servlet.http.*;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.auth.db.AuthLibraryDBConnectionFactory;
import org.yeastrc.session_mgmt.main.YRCSessionMgmtMain;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.base.config_system_table_common_access.ConfigSystemTableGetValueCommon;
import org.yeastrc.xlink.base.config_system_table_common_access.IConfigSystemTableGetValue;
import org.yeastrc.xlink.www.async_action_via_executor_service.AsyncActionViaExecutorService;
import org.yeastrc.xlink.www.auth_db.AuthLibraryDBConnectionFactoryForWeb;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmtRegistration;
import org.yeastrc.xlink.www.cached_data_mgmt.CachedDataCentralRegistry;
import org.yeastrc.xlink.www.config_properties_file.ProxlConfigFileReader;
import org.yeastrc.xlink.www.config_system_table.AppContextConfigSystemValuesRetrieval;
import org.yeastrc.xlink.www.config_system_table.ConfigSystemCaching;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.db_web.DBConnectionFactoryWeb;
import org.yeastrc.xlink.www.db_web.DBSet_JNDI_Name_FromConfigFile;
import org.yeastrc.xlink.www.file_import_proxl_xml_scans.constants.ProxlXMLFileUploadMaxFileSizeConstants;
import org.yeastrc.xlink.www.log_error_after_webapp_undeploy_started.Log_Info_Error_AfterWebAppUndeploy_Started;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtCentralWebappWebserviceAccess;
import org.yeastrc.xlink.www.no_data_validation.ThrowExceptionOnNoDataConfig;
import org.yeastrc.xlink.www.user_mgmt_db.UserMgmtCentralMainDBConnectionFactory_For_Proxl;
import org.yeastrc.xlink.www.web_utils.GetJsCssCacheBustString;
/**
 * This class is loaded and the method "contextInitialized" is called when the web application is first loaded by the container
 *
 */
public class ServletContextAppListener extends HttpServlet implements ServletContextListener {
	
	private static final Logger log = LoggerFactory.getLogger(  ServletContextAppListener.class );
	private static final long serialVersionUID = 1L;
	
	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
		log.warn( "INFO:  !!!!!!!!!!!!!!!   Start up of web app  'Proxl' beginning  !!!!!!!!!!!!!!!!!!!! " );
		boolean isDevEnv = false;
		Properties prop = System.getProperties();
		String devEnv = prop.getProperty("devEnv");
		if ( "Y".equals(devEnv ) ) {
			isDevEnv = true;
		}
		try {
			ProxlConfigFileReader.getInstance().populateProxlConfigFileValuesFromConfigFiles();
		} catch (Exception e) {
			//  already logged
			throw new RuntimeException( e );
		} 
		try {
			ThrowExceptionOnNoDataConfig.getInstance().init();
		} catch (Exception e) {
			//  already logged
			throw new RuntimeException( e );
		} 
		try {
			DBSet_JNDI_Name_FromConfigFile.getInstance().dbSet_JNDI_Name_FromConfigFile();
		} catch (Exception e) {
			//  already logged
			throw new RuntimeException( e );
		} 
		//   Set   to use DBConnectionFactoryWeb;
		DBConnectionFactoryWeb dbConnectionFactoryWeb = new DBConnectionFactoryWeb();
		DBConnectionFactory.setDbConnectionFactoryImpl( dbConnectionFactoryWeb );

		ServletContext context = event.getServletContext();
		String contextPath = context.getContextPath();
		CurrentContext.setCurrentWebAppContext( contextPath );
		
		String jsCssCacheBustString = GetJsCssCacheBustString.getInstance().getJsCssCacheBustString();
		if ( isDevEnv ) {
			jsCssCacheBustString = "devEnv";
		}
		context.setAttribute( WebConstants.APP_CONTEXT_JS_CSS_CACHE_BUST, jsCssCacheBustString );

		try {
			YRCSessionMgmtMain.getInstance().init();
		} catch (Exception e) {
			//  
			log.error( "Exception in YRCSessionMgmtMain.init():", e  );
			throw new RuntimeException( e );
		} 
		
		AuthLibraryDBConnectionFactoryForWeb dbConnectFactory = new AuthLibraryDBConnectionFactoryForWeb();
		AuthLibraryDBConnectionFactory.setDbConnectionFactoryImpl(dbConnectFactory);
		
		//  Remove validation so web app will start up with no configuration records
//		try {
//			GetEmailConfig.validateEmailConfig(); // throws Exception if error
//		} catch (Exception e) {
//			//  already logged
//			throw new RuntimeException( e );
//		} 
		
		//  Set iConfigSystemTableGetValue on ConfigSystemTableGetValueCommon
		try {
			IConfigSystemTableGetValue iConfigSystemTableGetValue = ConfigSystemCaching.getInstance();
			ConfigSystemTableGetValueCommon.getInstance().setIConfigSystemTableGetValue( iConfigSystemTableGetValue );
		} catch (Exception e) {
			//  already logged
			throw new RuntimeException( e );
		} 
		
		UserMgmtCentralMainDBConnectionFactory_For_Proxl userMgmtCentralMainDBConnectionFactory_For_Proxl = new UserMgmtCentralMainDBConnectionFactory_For_Proxl();

		UserMgmtCentralWebappWebserviceAccess.getInstance().init( userMgmtCentralMainDBConnectionFactory_For_Proxl );
		
		AppContextConfigSystemValuesRetrieval appContextConfigSystemValuesRetrieval = 
				new AppContextConfigSystemValuesRetrieval();
		context.setAttribute( WebConstants.CONFIG_SYSTEM_VALUES_HTML_KEY, appContextConfigSystemValuesRetrieval );
		
		AsyncActionViaExecutorService.initInstance();
		
		try {
			CachedDataInFileMgmtRegistration.getSingletonInstance().init();
		} catch (Exception e) {
			//  already logged
			throw new RuntimeException( e );
		} 
		
		try {
			ProxlXMLFileUploadMaxFileSizeConstants.getValuesAndLog();
		} catch (Throwable e) {
			//  Eat Exception
//			log.error( "Exception in FileUploadMaxFileSize_Config_WithConstantsDefaults.getValuesAndLog():", e  );
//			throw new RuntimeException( e );
		} 
		
		
		log.warn( "INFO:  !!!!!!!!!!!!!!!   Start up of web app  'Proxl' complete  !!!!!!!!!!!!!!!!!!!! " );
		log.warn( "INFO: Application context:" + contextPath );
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		
//		ServletContext context = event.getServletContext();

		Webapp_Undeploy_Started_Completed.setWebapp_Undeploy_Started(true);
		
		//  Nothing output since Log4J2 has stopped logging
		log.warn("INFO:  !!!!!!!!  Web app Undeploying STARTING  !!!!!!!!");

		Log_Info_Error_AfterWebAppUndeploy_Started.log_INFO_AfterWebAppUndeploy_Started("  !!!!!!!!" );
		Log_Info_Error_AfterWebAppUndeploy_Started.log_INFO_AfterWebAppUndeploy_Started("  !!!!!!!!  Web app Undeploying STARTING  !!!!!!!!" );

		
		try {
			CachedDataCentralRegistry.getInstance().writeToLogAllCacheSizes();
		} catch (Exception e) {
			String msg = "CachedDataCentralRegistry.getInstance().writeToLogAllCacheSizes() threw exception while app undeploying.";
			//  Nothing output since Log4J2 has stopped logging
			log.error( msg, e );
			Log_Info_Error_AfterWebAppUndeploy_Started.log_ERROR_AfterWebAppUndeploy_Started( msg, e );
		}

		try {
			CachedDataInFileMgmtRegistration.getSingletonInstance().shutdownNow();
		} catch ( Exception e ) {
			
			String msg = "In contextDestroyed(ServletContextEvent event), CachedDataInFileMgmtRegistration.getSingletonInstance().shutdownNow();";
			//  Nothing output since Log4J2 has stopped logging
			log.error( msg, e );
			Log_Info_Error_AfterWebAppUndeploy_Started.log_ERROR_AfterWebAppUndeploy_Started( msg, e );
		}
		
		try {
			AsyncActionViaExecutorService.getInstance().shutdownNow();
		} catch ( Exception e ) {
			
			String msg = "In contextDestroyed(ServletContextEvent event), AsyncActionViaExecutorService.getInstance().shutdownNow();";
			//  Nothing output since Log4J2 has stopped logging
			log.error( msg, e );
			Log_Info_Error_AfterWebAppUndeploy_Started.log_ERROR_AfterWebAppUndeploy_Started( msg, e );
		}
		
		
		//  Nothing output since Log4J2 has stopped logging
		log.warn("INFO:  !!!!!!!!  Web app Undeploying FINISHED  !!!!!!!!");

		Log_Info_Error_AfterWebAppUndeploy_Started.log_INFO_AfterWebAppUndeploy_Started(
				"  !!!!!!!!  Web app Undeploying: Initial run of contextDestroyed(...) is complete." );
		
		Log_Info_Error_AfterWebAppUndeploy_Started.log_INFO_AfterWebAppUndeploy_Started("  !!!!!!!!" );
		
		Webapp_Undeploy_Started_Completed.setWebapp_Undeploy_Completed(true);
	}


}
