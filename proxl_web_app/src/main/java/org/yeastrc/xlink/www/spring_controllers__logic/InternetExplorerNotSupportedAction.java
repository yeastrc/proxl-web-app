package org.yeastrc.xlink.www.spring_controllers__logic;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.constants.SpringMvcGlobalForwardNames;
/**
 * Internet Explorer Not Supported page action
 *
 */
public class InternetExplorerNotSupportedAction {
	
	private static final Logger log = LoggerFactory.getLogger( InternetExplorerNotSupportedAction.class);
	
		public String execute( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		try {
			return "Success";
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			return SpringMvcGlobalForwardNames.GENERAL_ERROR;
		}
	}
}
