package org.yeastrc.xlink.www.spring_controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.yeastrc.xlink.www.spring_mvc_controller_services.UploadPDBFileActionService;

/**
 * Spring MVC controller for path /uploadPDBFileService.do
 *
 * <p>The {@code UploadPDBFileActionService} is effectively a
 * webservice: it has no form, reads the uploaded file from the raw POST body
 * ({@code request.getInputStream()}), writes the status + response body itself, and always
 * returns null (it never forwards). The (now de-Struts-ed) action's execute(request, response) is
 * invoked directly; {@link SpringForwardResolver#resolveDownload} maps the null return to a null
 * Spring view (request handled).
 *
 * <p>No multipartResolver bean is configured, so Spring does not consume the request body - the
 * action reads getInputStream() directly, as before.
 */
@Controller
public class UploadPDBFileController {

	@RequestMapping( A__SpringMVC_Controller_Paths.UploadPDBFileController_uploadPDBFileService )
	public String uploadPDBFileService( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolveDownload( new UploadPDBFileActionService().execute( request, response ) );
	}
}
