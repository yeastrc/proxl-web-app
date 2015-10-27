package org.yeastrc.xlink.www.servlet_context;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.log4j.Logger;
import org.yeastrc.auth.db.AuthLibraryDBConnectionFactory;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.auth_db.AuthLibraryDBConnectionFactoryForWeb;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.send_email.GetEmailConfig;


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

		///  Uncomment this for using the other database for old Crosslinks Runs
		
//		DBConnectionFactory.setProxlJNDINameTo_proxl_old_crosslinks_runs();;
		
		
		

		ServletContext context = event.getServletContext();

		String contextPath = context.getContextPath();

		context.setAttribute( WebConstants.APP_CONTEXT_CONTEXT_PATH, contextPath );

		CurrentContext.setCurrentWebAppContext( contextPath );
		
		
		AuthLibraryDBConnectionFactoryForWeb dbConnectFactory = new AuthLibraryDBConnectionFactoryForWeb();
		
		AuthLibraryDBConnectionFactory.setDbConnectionFactoryImpl(dbConnectFactory);

		try {
			GetEmailConfig.validateEmailConfig(); // throws Exception if error
		} catch (Exception e) {
			//  already logged
			throw new RuntimeException( e );
		} 
		
		
//		LastLoginUpdaterThread.getInstance().start();

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