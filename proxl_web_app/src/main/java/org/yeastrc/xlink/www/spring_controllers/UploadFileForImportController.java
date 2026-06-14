package org.yeastrc.xlink.www.spring_controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.yeastrc.xlink.www.file_import_proxl_xml_scans.spring_mvc_controllers_as_webservices.UploadFileForImportWebserviceAction;

/**
 * Spring MVC controller for path /uploadProxlXmlOrScanFileForImport.do
 *
 * <p>The {@code UploadFileForImportWebserviceAction} is a webservice-style action.
 * Like the PDB upload service it has no form, never forwards (always returns null), reads
 * the uploaded Proxl XML / scan file from the raw POST body ({@code request.getInputStream()}),
 * and writes its own status + JSON response. The (now de-Struts-ed) action's execute(request,
 * response) is invoked directly; {@link SpringForwardResolver#resolveDownload} maps the null return
 * to a null Spring view (request handled).
 *
 * <p>No multipartResolver bean is configured, so Spring does not consume the request body.
 */
@Controller
public class UploadFileForImportController {

	@RequestMapping( A__SpringMVC_Controller_Paths.UploadFileForImportController_uploadProxlXmlOrScanFileForImport )
	public String uploadProxlXmlOrScanFileForImport( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolveDownload( new UploadFileForImportWebserviceAction().execute( request, response ) );
	}
}
