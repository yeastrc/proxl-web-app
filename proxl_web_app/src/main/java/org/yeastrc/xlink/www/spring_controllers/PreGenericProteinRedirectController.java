package org.yeastrc.xlink.www.spring_controllers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.yeastrc.xlink.www.constants.SpringMvcGlobalForwardNames;
import org.yeastrc.xlink.www.pre_generic_url_handling.PreGenericProteinForm;
import org.yeastrc.xlink.www.pre_generic_url_handling.PreGenericProteinRedirectAction;
import org.yeastrc.xlink.www.pre_generic_url_handling.PreGenericMergedImageMergedStructureAction;

/**
 * Spring MVC controller for the pre-generic protein / image / structure URL redirect paths
 * (old URLs before the change to Generic).
 *
 * <p>Two actions back these paths, both using form preGenericProteinForm and a
 * redirect-target string (previously the Struts {@code parameter} attribute):
 * <ul>
 *   <li>{@link PreGenericProteinRedirectAction} (6 paths) - writes response.sendRedirect(...) and
 *       returns null on success / GENERAL_ERROR on error.</li>
 *   <li>{@link PreGenericMergedImageMergedStructureAction} (2 paths) - returns the "Success"
 *       forward to the redirect helper JSP / GENERAL_ERROR.</li>
 * </ul>
 * Forward names are translated to Spring targets by {@link SpringForwardResolver} (a null name
 * means the response was already handled by sendRedirect).
 */
@Controller
public class PreGenericProteinRedirectController {

	//  ====== PreGenericProteinRedirectAction (sendRedirect + null) ======

	//  Only forward the action can return is GENERAL_ERROR; success is sendRedirect + null.
	private static final Map<String,String> REDIRECT_FORWARDS =
			Collections.singletonMap( SpringMvcGlobalForwardNames.GENERAL_ERROR, "generalError" );

	@RequestMapping( A__SpringMVC_Controller_Paths.PreGenericProteinRedirectController_viewSearchCrosslinkProtein )
	public String viewSearchCrosslinkProtein( @ModelAttribute( "preGenericProteinForm" ) PreGenericProteinForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolve( new PreGenericProteinRedirectAction().execute( "/crosslinkProtein", form, request, response ), REDIRECT_FORWARDS );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.PreGenericProteinRedirectController_viewSearchLooplinkProtein )
	public String viewSearchLooplinkProtein( @ModelAttribute( "preGenericProteinForm" ) PreGenericProteinForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolve( new PreGenericProteinRedirectAction().execute( "/looplinkProtein", form, request, response ), REDIRECT_FORWARDS );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.PreGenericProteinRedirectController_viewProteinCoverageReport )
	public String viewProteinCoverageReport( @ModelAttribute( "preGenericProteinForm" ) PreGenericProteinForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolve( new PreGenericProteinRedirectAction().execute( "/proteinCoverageReport", form, request, response ), REDIRECT_FORWARDS );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.PreGenericProteinRedirectController_viewMergedCrosslinkProtein )
	public String viewMergedCrosslinkProtein( @ModelAttribute( "preGenericProteinForm" ) PreGenericProteinForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolve( new PreGenericProteinRedirectAction().execute( "/mergedCrosslinkProtein", form, request, response ), REDIRECT_FORWARDS );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.PreGenericProteinRedirectController_viewMergedLooplinkProtein )
	public String viewMergedLooplinkProtein( @ModelAttribute( "preGenericProteinForm" ) PreGenericProteinForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolve( new PreGenericProteinRedirectAction().execute( "/mergedLooplinkProtein", form, request, response ), REDIRECT_FORWARDS );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.PreGenericProteinRedirectController_viewMergedProteinCoverageReport )
	public String viewMergedProteinCoverageReport( @ModelAttribute( "preGenericProteinForm" ) PreGenericProteinForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolve( new PreGenericProteinRedirectAction().execute( "/mergedProteinCoverageReport", form, request, response ), REDIRECT_FORWARDS );
	}

	//  ====== PreGenericMergedImageMergedStructureAction (Success -> helper JSP) ======

	//  struts-config: Success -> special_redirect_pages/redirect_pre_generic_image_structure_ToGenericURL.jsp
	private static final Map<String,String> IMAGE_STRUCTURE_FORWARDS = new HashMap<>();
	static {
		IMAGE_STRUCTURE_FORWARDS.put( "Success", "special_redirect_pages/redirect_pre_generic_image_structure_ToGenericURL" );
		IMAGE_STRUCTURE_FORWARDS.put( SpringMvcGlobalForwardNames.GENERAL_ERROR, "generalError" );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.PreGenericProteinRedirectController_viewMergedImage )
	public String viewMergedImage( @ModelAttribute( "preGenericProteinForm" ) PreGenericProteinForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolve( new PreGenericMergedImageMergedStructureAction().execute( "/image", form, request, response ), IMAGE_STRUCTURE_FORWARDS );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.PreGenericProteinRedirectController_viewMergedStructure )
	public String viewMergedStructure( @ModelAttribute( "preGenericProteinForm" ) PreGenericProteinForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolve( new PreGenericMergedImageMergedStructureAction().execute( "/structure", form, request, response ), IMAGE_STRUCTURE_FORWARDS );
	}
}
