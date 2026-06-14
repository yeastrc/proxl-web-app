package org.yeastrc.xlink.www.spring_controllers;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.yeastrc.xlink.www.spring_controllers__logic.ProjectReadProcessCodeAction;

/**
 * Spring MVC controller for path /projectReadProcessCode.do
 *
 * <p>Success forwards to /viewProject.do (the action sets the project id as a request attribute,
 * which survives the forward to the Spring ViewProjectController).
 */
@Controller
public class ProjectReadProcessCodeController {

	private static final Map<String,String> FORWARDS = new HashMap<>();
	static {
		//  struts-config: Success -> /viewProject.do (redirect=false) ; Failure -> projectReadProcessCodeFailure.jsp
		FORWARDS.put( "Success", "forward:/viewProject.do" );
		FORWARDS.put( "Failure", "projectReadProcessCodeFailure" );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.ProjectReadProcessCodeController_projectReadProcessCode )
	public String projectReadProcessCode( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolve( new ProjectReadProcessCodeAction().execute( request, response ), FORWARDS );
	}
}
