package org.yeastrc.xlink.www.spring_controllers__logic;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.web_utils.GetPageHeaderData;
/**
 * 
 *
 */
public class ShortcutNotFoundPageInitAction{
	
	private static final Logger log = LoggerFactory.getLogger( ShortcutNotFoundPageInitAction.class);
	
		public String execute( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		try {
			// Get their session first.  
			GetPageHeaderData.getInstance().getPageHeaderDataWithoutProjectId( request );
			return "Success";
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}
}
