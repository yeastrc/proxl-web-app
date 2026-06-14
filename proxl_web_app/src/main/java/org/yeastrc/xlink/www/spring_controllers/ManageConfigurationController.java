package org.yeastrc.xlink.www.spring_controllers;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.yeastrc.xlink.www.constants.SpringMvcGlobalForwardNames;
import org.yeastrc.xlink.www.spring_controllers__logic.ConfigureProxlForAdminPageInitAction;

/**
 * Spring MVC controller for path /manageConfiguration.do
 */
@Controller
public class ManageConfigurationController {

	private static final Map<String,String> FORWARDS = new HashMap<>();
	static {
		//  struts-config: Success -> configureProxlForAdmin.jsp
		FORWARDS.put( "Success", "configureProxlForAdmin" );
		FORWARDS.put( SpringMvcGlobalForwardNames.NO_USER_SESSION, SpringMvcForwards.NO_USER_SESSION );
		FORWARDS.put( SpringMvcGlobalForwardNames.INSUFFICIENT_ACCESS_PRIVILEGE, SpringMvcForwards.INSUFFICIENT_ACCESS_PRIVILEGE );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.ManageConfigurationController_manageConfiguration )
	public String manageConfiguration( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolve( new ConfigureProxlForAdminPageInitAction().execute( request, response ), FORWARDS );
	}
}
