package org.yeastrc.xlink.www.spring_controllers;

import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.yeastrc.xlink.www.constants.SpringMvcGlobalForwardNames;
import org.yeastrc.xlink.www.spring_controllers__logic.TermsOfServicePageAction;

/**
 * Spring MVC controller for path /termsOfService.do
 */
@Controller
public class TermsOfServiceController {

	private static final Map<String,String> FORWARDS = new HashMap<>();
	static {
		//  struts-config: Success -> termsOfService.jsp
		FORWARDS.put( "Success", "termsOfService" );
		FORWARDS.put( SpringMvcGlobalForwardNames.GENERAL_ERROR, "generalError" );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.TermsOfServiceController_termsOfService )
	public String termsOfService( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		//  Action refactored off Struts: returns a forward name String; resolve it to a Spring target.
		return SpringForwardResolver.resolve( new TermsOfServicePageAction().execute( request, response ), FORWARDS );
	}
}
