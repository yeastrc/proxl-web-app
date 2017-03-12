package org.yeastrc.xlink.www.servlet_context;

import java.util.Properties;
import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.log4j.Logger;
import org.yeastrc.auth.db.AuthLibraryDBConnectionFactory;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.base.config_system_table_common_access.ConfigSystemTableGetValueCommon;
import org.yeastrc.xlink.base.config_system_table_common_access.IConfigSystemTableGetValue;
import org.yeastrc.xlink.www.auth_db.AuthLibraryDBConnectionFactoryForWeb;
import org.yeastrc.xlink.www.config_properties_file.ProxlConfigFileReader;
import org.yeastrc.xlink.www.config_system_table.AppContextConfigSystemValuesRetrieval;
import org.yeastrc.xlink.www.config_system_table.ConfigSystemCaching;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.db_web.DBConnectionFactoryWeb;
import org.yeastrc.xlink.www.db_web.DBSet_JNDI_Name_FromConfigFile;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtCentralWebappWebserviceAccess;
import org.yeastrc.xlink.www.no_data_validation.ThrowExceptionOnNoDataConfig;
import org.yeastrc.xlink.www.web_utils.GetJsCssCacheBustString;
/**
 * This class is loaded and the method "contextInitialized" is called when the web application is first loaded by the container
 *
 */
public class ServletContextAppListener extends HttpServlet implements ServletContextListener {
	
	private static Logger log = Logger.getLogger( ServletContextAppListener.class );
	private static final long serialVersionUID = 1L;
	
	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
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
		context.setAttribute( WebConstants.APP_CONTEXT_CONTEXT_PATH, contextPath );
		CurrentContext.setCurrentWebAppContext( contextPath );
		String jsCssCacheBustString = GetJsCssCacheBustString.getInstance().getJsCssCacheBustString();
		if ( isDevEnv ) {
			jsCssCacheBustString = "devEnv";
		}
		context.setAttribute( WebConstants.APP_CONTEXT_JS_CSS_CACHE_BUST, jsCssCacheBustString );
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
		UserMgmtCentralWebappWebserviceAccess.getInstance().init();
		AppContextConfigSystemValuesRetrieval appContextConfigSystemValuesRetrieval = 
				new AppContextConfigSystemValuesRetrieval();
		context.setAttribute( WebConstants.CONFIG_SYSTEM_VALUES_HTML_KEY, appContextConfigSystemValuesRetrieval );
		log.warn( "INFO:  !!!!!!!!!!!!!!!   Start up of web app  'Proxl' complete  !!!!!!!!!!!!!!!!!!!! " );
		log.warn( "INFO: Application context values set.  Key = " + WebConstants.APP_CONTEXT_CONTEXT_PATH + ": value = " + contextPath
				+ "" );
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent event) {
		//ServletContext context = event.getServletContext();
//		LastLoginUpdaterQueue.endProcessing();
//		
//		try {
//			LastLoginUpdaterThread.getInstance().join();
//		} catch (InterruptedException e) {
//
//			log.error( "In contextDestroyed(ServletContextEvent event), LastLoginUpdaterThread.getInstance().join();", e );
//			
//			
//		}
	}
}
