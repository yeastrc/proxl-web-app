package org.yeastrc.xlink.www.spring_controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Spring MVC controller for path /proxlExternalViewer.do
 *
 * <p>Forwards to /WEB-INF/jsp-pages/proxl-external-viewer.jsp.
 */
@Controller
public class ProxlExternalViewerController {

	@RequestMapping( A__SpringMVC_Controller_Paths.ProxlExternalViewerController_proxlExternalViewer )
	public String proxlExternalViewer() {
		return "proxl-external-viewer";
	}
}
