package org.yeastrc.xlink.www.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.xlink.www.web_utils.GetPageHeaderData;

/**
 * 
 *
 */
public class ShortcutNotFoundPageInitAction extends Action{

	private static final Logger log = Logger.getLogger(ShortcutNotFoundPageInitAction.class);
	
	public ActionForward execute( ActionMapping mapping,
			  ActionForm form,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {
		
		try {
			// Get their session first.  

			GetPageHeaderData.getInstance().getPageHeaderDataWithoutProjectId( request );
			
			return mapping.findForward( "Success" );


		} catch ( Exception e ) {
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
		}
	}


}
