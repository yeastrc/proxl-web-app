package org.yeastrc.xlink.www.spring_controllers;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.yeastrc.xlink.www.constants.SpringMvcGlobalForwardNames;
import org.yeastrc.xlink.www.spring_controllers__logic.HomeAction;

/**
 * Spring MVC controller for path /home.do (the home / welcome entry point).
 */
@Controller
public class HomeController {

	private static final Map<String,String> FORWARDS = new HashMap<>();
	static {
		//  struts-config: Success -> /listProjects.do redirect="true"
		FORWARDS.put( "Success", "redirect:/listProjects.do" );
		FORWARDS.put( SpringMvcGlobalForwardNames.NO_USER_SESSION, SpringMvcForwards.NO_USER_SESSION );
		FORWARDS.put( SpringMvcGlobalForwardNames.LOGIN, SpringMvcForwards.LOGIN );
		FORWARDS.put( SpringMvcGlobalForwardNames.GENERAL_ERROR, "generalError" );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.HomeController_home )
	public String home( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolve( new HomeAction().execute( request, response ), FORWARDS );
	}
}
