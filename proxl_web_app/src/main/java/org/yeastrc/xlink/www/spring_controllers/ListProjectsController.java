package org.yeastrc.xlink.www.spring_controllers;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.yeastrc.xlink.www.constants.SpringMvcGlobalForwardNames;
import org.yeastrc.xlink.www.spring_controllers__logic.ListProjectsAction;

/**
 * Spring MVC controller for path /listProjects.do
 */
@Controller
public class ListProjectsController {

	private static final Map<String,String> FORWARDS = new HashMap<>();
	static {
		//  struts-config: Success/Failure -> listProjects.jsp
		FORWARDS.put( "Success", "listProjects" );
		FORWARDS.put( "Failure", "listProjects" );
		FORWARDS.put( SpringMvcGlobalForwardNames.NO_USER_SESSION, SpringMvcForwards.NO_USER_SESSION );
		FORWARDS.put( SpringMvcGlobalForwardNames.INSUFFICIENT_ACCESS_PRIVILEGE, SpringMvcForwards.INSUFFICIENT_ACCESS_PRIVILEGE );
		FORWARDS.put( SpringMvcGlobalForwardNames.ACCOUNT_DISABLED, SpringMvcForwards.ACCOUNT_DISABLED );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.ListProjectsController_listProjects )
	public String listProjects( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolve( new ListProjectsAction().execute( request, response ), FORWARDS );
	}
}
