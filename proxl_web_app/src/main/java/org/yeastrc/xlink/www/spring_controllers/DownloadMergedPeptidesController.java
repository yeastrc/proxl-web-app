package org.yeastrc.xlink.www.spring_controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.yeastrc.xlink.www.forms.MergedSearchViewPeptidesForm;
import org.yeastrc.xlink.www.spring_controllers__logic.DownloadPSMsForMergedPeptidesAction;
import org.yeastrc.xlink.www.spring_controllers__logic.DownloadMergedSearchPeptidesAction;
import org.yeastrc.xlink.www.spring_controllers__logic.DownloadMergedPeptidesForSkylinePRMAction;
import org.yeastrc.xlink.www.spring_controllers__logic.DownloadMergedPeptidesSkylineShulman;

/**
 * Spring MVC controller for the merged-peptide data-export downloads (form mergedSearchViewPeptideForm).
 *
 * <p>These are downloads: each Struts action writes the file straight to the response and returns
 * null on success / mapping.findForward(globalForward) on error. The existing action classes are
 * reused via { SpringForwardResolver#resolveDownload} (see that class for the contract).
 */
@Controller
public class DownloadMergedPeptidesController {

	@RequestMapping( A__SpringMVC_Controller_Paths.DownloadMergedPeptidesController_downloadMergedPSMsForPeptides )
	public String downloadMergedPSMsForPeptides( @ModelAttribute( "mergedSearchViewPeptideForm" ) MergedSearchViewPeptidesForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolveDownload( new DownloadPSMsForMergedPeptidesAction().execute( form, request, response ) );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.DownloadMergedPeptidesController_downloadMergedPeptides )
	public String downloadMergedPeptides( @ModelAttribute( "mergedSearchViewPeptideForm" ) MergedSearchViewPeptidesForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolveDownload( new DownloadMergedSearchPeptidesAction().execute( form, request, response ) );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.DownloadMergedPeptidesController_downloadMergedPeptidesForSkylinePRM )
	public String downloadMergedPeptidesForSkylinePRM( @ModelAttribute( "mergedSearchViewPeptideForm" ) MergedSearchViewPeptidesForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolveDownload( new DownloadMergedPeptidesForSkylinePRMAction().execute( form, request, response ) );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.DownloadMergedPeptidesController_downloadMergedPeptidesForSkylineShulman )
	public String downloadMergedPeptidesForSkylineShulman( @ModelAttribute( "mergedSearchViewPeptideForm" ) MergedSearchViewPeptidesForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolveDownload( new DownloadMergedPeptidesSkylineShulman().execute( form, request, response ) );
	}
}
