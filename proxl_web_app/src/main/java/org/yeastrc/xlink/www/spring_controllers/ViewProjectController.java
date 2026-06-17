package org.yeastrc.xlink.www.spring_controllers;

import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.yeastrc.xlink.www.constants.SpringMvcGlobalForwardNames;
import org.yeastrc.xlink.www.spring_controllers__logic.ViewProjectAction;

/**
 * Spring MVC controller for path /viewProject.do
 */
@Controller
public class ViewProjectController {

	private static final Map<String,String> FORWARDS = new HashMap<>();
	static {
		//  struts-config: Success/Failure -> project_page/viewProject.jsp ; ProjectNotFound -> projectNotFound.jsp
		FORWARDS.put( "Success", "project_page/viewProject" );
		FORWARDS.put( "Failure", "project_page/viewProject" );
		FORWARDS.put( "ProjectNotFound", "projectNotFound" );
		FORWARDS.put( SpringMvcGlobalForwardNames.NO_USER_SESSION, SpringMvcForwards.NO_USER_SESSION );
		FORWARDS.put( SpringMvcGlobalForwardNames.INSUFFICIENT_ACCESS_PRIVILEGE, SpringMvcForwards.INSUFFICIENT_ACCESS_PRIVILEGE );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.ViewProjectController_viewProject )
	public String viewProject( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolve( new ViewProjectAction().execute( request, response ), FORWARDS );
	}
}
