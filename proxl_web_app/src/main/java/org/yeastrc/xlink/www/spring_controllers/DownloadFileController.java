package org.yeastrc.xlink.www.spring_controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.yeastrc.xlink.www.spring_controllers__logic.DownloadSearchFileAction;
import org.yeastrc.xlink.www.spring_controllers__logic.DownloadPDBFileAction;

/**
 * Spring MVC controller for the plain stored-file downloads.
 *
 * <p>These actions have no form bean - they read their id straight from request parameters
 * (downloadSearchFile: "fileId"; downloadPDBFile: "id"), write the file to the response, and
 * return null on success / mapping.findForward(globalForward) on error. The existing action
 * classes are reused via { SpringForwardResolver#resolveDownload}; since they never use
 * the ActionForm argument, null is passed for it.
 */
@Controller
public class DownloadFileController {

	@RequestMapping( A__SpringMVC_Controller_Paths.DownloadFileController_downloadSearchFile )
	public String downloadSearchFile( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolveDownload( new DownloadSearchFileAction().execute( request, response ) );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.DownloadFileController_downloadPDBFile )
	public String downloadPDBFile( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolveDownload( new DownloadPDBFileAction().execute( request, response ) );
	}
}
