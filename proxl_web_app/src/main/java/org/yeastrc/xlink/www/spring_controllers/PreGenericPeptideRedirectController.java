package org.yeastrc.xlink.www.spring_controllers;

import java.util.Collections;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.yeastrc.xlink.www.constants.SpringMvcGlobalForwardNames;
import org.yeastrc.xlink.www.pre_generic_url_handling.PreGenericPeptideForm;
import org.yeastrc.xlink.www.pre_generic_url_handling.PreGenericPeptideRedirectAction;

/**
 * Spring MVC controller for the pre-generic peptide URL redirect paths (old URLs before the change
 * to Generic): /viewSearchPeptide.do and /viewMergedPeptide.do
 *
 * <p>The {@code PreGenericPeptideRedirectAction} reads
 * the old-style form params, builds the new generic URL, and writes it with
 * {@code response.sendRedirect(...)} (returning null), or returns GENERAL_ERROR on error.
 *
 * <p>The redirect target ("/peptide" or "/mergedPeptide"), previously read via the Struts
 * {@code mapping.getParameter()}, is passed explicitly to the action's execute method. A null
 * return means the response was handled by sendRedirect; {@link SpringForwardResolver} maps that to
 * a null Spring view (no further forwarding) and GENERAL_ERROR to the generalError view.
 */
@Controller
public class PreGenericPeptideRedirectController {

	private static final Map<String,String> FORWARDS =
			Collections.singletonMap( SpringMvcGlobalForwardNames.GENERAL_ERROR, "generalError" );

	@RequestMapping( A__SpringMVC_Controller_Paths.PreGenericPeptideRedirectController_viewSearchPeptide )
	public String viewSearchPeptide(
			@ModelAttribute( "preGenericPeptideForm" ) PreGenericPeptideForm form,
			HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolve(
				new PreGenericPeptideRedirectAction().execute( "/peptide", form, request, response ), FORWARDS );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.PreGenericPeptideRedirectController_viewMergedPeptide )
	public String viewMergedPeptide(
			@ModelAttribute( "preGenericPeptideForm" ) PreGenericPeptideForm form,
			HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolve(
				new PreGenericPeptideRedirectAction().execute( "/mergedPeptide", form, request, response ), FORWARDS );
	}
}
