package org.yeastrc.xlink.www.spring_controllers;

import java.util.Collections;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.yeastrc.xlink.www.spring_controllers__logic.ShortcutNotFoundPageInitAction;

/**
 * Spring MVC controller for path /shortcutNotFound.do
 */
@Controller
public class ShortcutNotFoundController {

	//  struts-config: Success -> shortcutNotFound.jsp
	private static final Map<String,String> FORWARDS = Collections.singletonMap( "Success", "shortcutNotFound" );

	@RequestMapping( A__SpringMVC_Controller_Paths.ShortcutNotFoundController_shortcutNotFound )
	public String shortcutNotFound( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolve( new ShortcutNotFoundPageInitAction().execute( request, response ), FORWARDS );
	}
}
