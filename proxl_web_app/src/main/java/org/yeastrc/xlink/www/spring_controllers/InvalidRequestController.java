package org.yeastrc.xlink.www.spring_controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Spring MVC controller for the invalid-request landing pages.
 *
 * <p>Forwards to invalidRequestSearchesAcrossProjects.jsp / invalidRequestData.jsp. These are the
 * targets of the global-forwards "invalidRequestSearchesAcrossProjects" / "invalidRequestData" (and
 * the {@code SpringMvcForwards} equivalents that other controllers forward to).
 */
@Controller
public class InvalidRequestController {

	@RequestMapping( A__SpringMVC_Controller_Paths.InvalidRequestController_invalidRequestSearchesAcrossProjects )
	public String invalidRequestSearchesAcrossProjects() {
		return "invalidRequestSearchesAcrossProjects";
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.InvalidRequestController_invalidRequestData )
	public String invalidRequestData() {
		return "invalidRequestData";
	}
}
