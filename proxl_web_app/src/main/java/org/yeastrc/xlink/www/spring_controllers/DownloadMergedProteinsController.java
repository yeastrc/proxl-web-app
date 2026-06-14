package org.yeastrc.xlink.www.spring_controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.yeastrc.xlink.www.forms.DownloadMergedSearchViewProteinsForm;
import org.yeastrc.xlink.www.forms.DownloadProteinCLMSForm;
import org.yeastrc.xlink.www.forms.MergedSearchViewProteinsForm;
import org.yeastrc.xlink.www.spring_controllers__logic.DownloadMergedProteinsFASTAAction;
import org.yeastrc.xlink.www.spring_controllers__logic.DownloadMergedProteinsCLMS_CSVAction;
import org.yeastrc.xlink.www.spring_controllers__logic.DownloadMergedProteinsPeptidesSkylineShulmanAction;
import org.yeastrc.xlink.www.spring_controllers__logic.DownloadMergedProteinsPeptidesSkylineEngAction;
import org.yeastrc.xlink.www.spring_controllers__logic.DownloadMergedProteinsXvisAction;
import org.yeastrc.xlink.www.spring_controllers__logic.DownloadMergedProteinsXvisLengthsAction;
import org.yeastrc.xlink.www.spring_controllers__logic.DownloadMergedSearchProteinsAction;
import org.yeastrc.xlink.www.spring_controllers__logic.DownloadMergedSearchUDRsAction;
import org.yeastrc.xlink.www.spring_controllers__logic.DownloadMergedSearchProteinsAllAction;
import org.yeastrc.xlink.www.spring_controllers__logic.DownloadProteinCoverageReportAction;

/**
 * Spring MVC controller for the merged-protein data-export downloads.
 *
 * <p>These are downloads: each Struts action writes the file straight to the response and returns
 * null on success / mapping.findForward(globalForward) on error. The existing action classes are
 * reused via { SpringForwardResolver#resolveDownload} (see that class for the contract).
 *
 * <p>Three form beans are involved, matching the old Struts form-bean names so the bound POJO is
 * exposed under the same request attribute:
 * <ul>
 *   <li>downloadMergedSearchViewProteinsForm -> {@link DownloadMergedSearchViewProteinsForm}</li>
 *   <li>downloadProteinCLMSForm -> {@link DownloadProteinCLMSForm}</li>
 *   <li>mergedSearchViewProteinForm -> {@link MergedSearchViewProteinsForm}</li>
 * </ul>
 */
@Controller
public class DownloadMergedProteinsController {

	@RequestMapping( A__SpringMVC_Controller_Paths.DownloadMergedProteinsController_downloadMergedProteinsFASTA )
	public String downloadMergedProteinsFASTA( @ModelAttribute( "downloadMergedSearchViewProteinsForm" ) DownloadMergedSearchViewProteinsForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolveDownload( new DownloadMergedProteinsFASTAAction().execute( form, request, response ) );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.DownloadMergedProteinsController_downloadMergedProteinsCLMS_CSV )
	public String downloadMergedProteinsCLMS_CSV( @ModelAttribute( "downloadProteinCLMSForm" ) DownloadProteinCLMSForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolveDownload( new DownloadMergedProteinsCLMS_CSVAction().execute( form, request, response ) );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.DownloadMergedProteinsController_downloadMergedProteinsPeptidesSkylineShulman )
	public String downloadMergedProteinsPeptidesSkylineShulman( @ModelAttribute( "downloadMergedSearchViewProteinsForm" ) DownloadMergedSearchViewProteinsForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolveDownload( new DownloadMergedProteinsPeptidesSkylineShulmanAction().execute( form, request, response ) );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.DownloadMergedProteinsController_downloadMergedProteinsPeptidesSkylineEng )
	public String downloadMergedProteinsPeptidesSkylineEng( @ModelAttribute( "downloadMergedSearchViewProteinsForm" ) DownloadMergedSearchViewProteinsForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolveDownload( new DownloadMergedProteinsPeptidesSkylineEngAction().execute( form, request, response ) );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.DownloadMergedProteinsController_downloadMergedProteinsXvis )
	public String downloadMergedProteinsXvis( @ModelAttribute( "downloadProteinCLMSForm" ) DownloadProteinCLMSForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolveDownload( new DownloadMergedProteinsXvisAction().execute( form, request, response ) );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.DownloadMergedProteinsController_downloadMergedProteinsLengths )
	public String downloadMergedProteinsLengths( @ModelAttribute( "downloadMergedSearchViewProteinsForm" ) DownloadMergedSearchViewProteinsForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolveDownload( new DownloadMergedProteinsXvisLengthsAction().execute( form, request, response ) );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.DownloadMergedProteinsController_downloadMergedProteins )
	public String downloadMergedProteins( @ModelAttribute( "downloadMergedSearchViewProteinsForm" ) DownloadMergedSearchViewProteinsForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolveDownload( new DownloadMergedSearchProteinsAction().execute( form, request, response ) );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.DownloadMergedProteinsController_downloadMergedProteinUDRs )
	public String downloadMergedProteinUDRs( @ModelAttribute( "downloadMergedSearchViewProteinsForm" ) DownloadMergedSearchViewProteinsForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolveDownload( new DownloadMergedSearchUDRsAction().execute( form, request, response ) );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.DownloadMergedProteinsController_downloadMergedProteinsAll )
	public String downloadMergedProteinsAll( @ModelAttribute( "mergedSearchViewProteinForm" ) MergedSearchViewProteinsForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolveDownload( new DownloadMergedSearchProteinsAllAction().execute( form, request, response ) );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.DownloadMergedProteinsController_downloadProteinCoverageReport )
	public String downloadProteinCoverageReport( @ModelAttribute( "mergedSearchViewProteinForm" ) MergedSearchViewProteinsForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolveDownload( new DownloadProteinCoverageReportAction().execute( form, request, response ) );
	}
}
