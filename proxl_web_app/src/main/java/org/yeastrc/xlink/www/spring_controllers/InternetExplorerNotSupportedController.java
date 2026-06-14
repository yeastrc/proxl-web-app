package org.yeastrc.xlink.www.spring_controllers;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.yeastrc.xlink.www.constants.SpringMvcGlobalForwardNames;
import org.yeastrc.xlink.www.spring_controllers__logic.InternetExplorerNotSupportedAction;

/**
 * Spring MVC controller for path /internet_explorer_not_supported.do
 */
@Controller
public class InternetExplorerNotSupportedController {

	private static final Map<String,String> FORWARDS = new HashMap<>();
	static {
		//  struts-config: Success -> InternetExplorer_NotSupported_Error.jsp
		FORWARDS.put( "Success", "InternetExplorer_NotSupported_Error" );
		FORWARDS.put( SpringMvcGlobalForwardNames.GENERAL_ERROR, "generalError" );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.InternetExplorerNotSupportedController_internetExplorerNotSupported )
	public String internetExplorerNotSupported( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolve( new InternetExplorerNotSupportedAction().execute( request, response ), FORWARDS );
	}
}
