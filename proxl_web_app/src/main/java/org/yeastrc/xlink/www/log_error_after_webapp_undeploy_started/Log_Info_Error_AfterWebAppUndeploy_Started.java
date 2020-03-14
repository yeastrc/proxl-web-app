package org.yeastrc.xlink.www.log_error_after_webapp_undeploy_started;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.www.servlet_context.CurrentContext;
import org.yeastrc.xlink.www.servlet_context.Webapp_Undeploy_Started_Completed;

/**
 * Log4J2 has shut down before ServletContext::contextDestroyed(...) is called
 * 
 * So this provides a way to write to sysout/syserr with a prefix of this webapp and the webapp context.
 * 
 * In Tomcat, this will be written to file catalina.out
 *
 */
public class Log_Info_Error_AfterWebAppUndeploy_Started {
	
	private static final Logger log = LoggerFactory.getLogger( Log_Info_Error_AfterWebAppUndeploy_Started.class );

	/**
	 * @param msg
	 * @throws ProxlWebappInternalErrorException 
	 */
	public static void log_INFO_AfterWebAppUndeploy_Started( String msg ) throws ProxlWebappInternalErrorException {
		
		if ( ! Webapp_Undeploy_Started_Completed.isWebapp_Undeploy_Started() ) {
			
			String errorMsg = "Invalid to call when NOT Undeploy started.  true: if ( ! Webapp_Undeploy_Started_Completed.isWebapp_Undeploy_Started() ).  msg: " + msg;
			log.error(errorMsg);
			throw new ProxlWebappInternalErrorException( errorMsg );
		}
		
		String logMsg = getLogMsgPrefix() + " INFO:  " + msg;
		
		System.out.println( logMsg );
	}

	/**
	 * @param msg
	 * @throws ProxlWebappInternalErrorException 
	 */
	public static void log_ERROR_AfterWebAppUndeploy_Started( String msg ) throws ProxlWebappInternalErrorException {
		
		log_ERROR_AfterWebAppUndeploy_Started( msg, null /* throwable */ );
	}
	
	/**
	 * @param msg
	 * @param throwable
	 * @throws ProxlWebappInternalErrorException 
	 */
	public static void log_ERROR_AfterWebAppUndeploy_Started( String msg, Throwable throwable ) throws ProxlWebappInternalErrorException {
		
		if ( ! Webapp_Undeploy_Started_Completed.isWebapp_Undeploy_Started() ) {
			
			String errorMsg = "Invalid to call when NOT Undeploy started.  true: if ( ! Webapp_Undeploy_Started_Completed.isWebapp_Undeploy_Started() ).  msg: " + msg;
			log.error(errorMsg);
			throw new ProxlWebappInternalErrorException( errorMsg );
		}
		
		String logMsg = getLogMsgPrefix() + " ERROR:  " + msg;
		
		System.err.println( logMsg );
		
		if ( throwable != null )  {
			throwable.printStackTrace();
		}
	}
	
	/**
	 * @return
	 */
	private static String getLogMsgPrefix() {
		
		String now = new Date().toString();
		
		String prefix = now + ":  Proxl Webapp: CurrentContext: " + CurrentContext.getCurrentWebAppContext() 
		+ ". ";
		
		return prefix;
	}
}
