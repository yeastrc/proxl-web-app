package org.yeastrc.xlink.www.browser_type_logging;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.config_system_table.ConfigSystemCaching;
import org.yeastrc.xlink.www.constants.ConfigSystemsKeysConstants;

public class LogIfBrowserIsInternetExplorer {

	private static final Logger log = Logger.getLogger(LogIfBrowserIsInternetExplorer.class);
	
	private static LogIfBrowserIsInternetExplorer _INSTANCE = new LogIfBrowserIsInternetExplorer();
	
	//  private constructor
	private LogIfBrowserIsInternetExplorer() { }
	/**
	 * @return Singleton instance
	 */
	public static LogIfBrowserIsInternetExplorer getSingletonInstance() { 
		return _INSTANCE; 
	}
	

	/**
	 * @param request
	 */
	public void logBrowserIsInternetExplorerIfConfigured( ServletRequest request ) {
		
		if ( log.isDebugEnabled() ) {
			try {
				
				HttpServletRequest httpRequest = (HttpServletRequest) request;
				
				String userAgentString = httpRequest.getHeader("User-Agent");

				//  Works up to IE 10
				boolean isIE = userAgentString.contains("MSIE");
				
				//For IE 11
				boolean isIE11 = userAgentString.contains("rv:11.0");
				
				
				if ( isIE || isIE11 ) {
					
					String requestURL = httpRequest.getRequestURL().toString();
					
					if ( requestURL.contains(".do") ) {
						
						
						log.debug( "Browser is Internet Explorer.  "
								+ "UserAgent: \t" + userAgentString
								+ "\t, requested URL: \t" + requestURL
								+ "\t, remote IP: \t" + request.getRemoteAddr() );
					}
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
}
