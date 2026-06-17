package org.yeastrc.xlink.www.spring_controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.yeastrc.xlink.www.forms.SingleRequestJSONStringFieldForm;
import org.yeastrc.xlink.www.spring_controllers__logic.DownloadQC_Summary_PsmChartDataAction;
import org.yeastrc.xlink.www.spring_controllers__logic.DownloadQC_Summary_PeptideChartDataAction;
import org.yeastrc.xlink.www.spring_controllers__logic.DownloadQC_Summary_ProteinChartDataAction;
import org.yeastrc.xlink.www.spring_controllers__logic.DownloadQC_Digestion_PeptideMissedCleavageChartDataAction;
import org.yeastrc.xlink.www.spring_controllers__logic.DownloadQC_Digestion_MissedCleavagePerPeptideChartDataAction;
import org.yeastrc.xlink.www.spring_controllers__logic.DownloadQC_Digestion_PsmMissedCleavageChartDataAction;
import org.yeastrc.xlink.www.spring_controllers__logic.DownloadQC_PSM_Count_Vs_RetentionTime_ChartDataAction;
import org.yeastrc.xlink.www.spring_controllers__logic.DownloadQC_PsmChargeChartDataAction;
import org.yeastrc.xlink.www.spring_controllers__logic.DownloadQC_Psm_M_Over_Z_ChartDataAction;
import org.yeastrc.xlink.www.spring_controllers__logic.DownloadQC_PeptideLengthVsPSMCountHistogramSingleSearchChartDataAction;
import org.yeastrc.xlink.www.spring_controllers__logic.DownloadQC_PeptideLengthVsPSMCountBoxplotChartDataAction;
import org.yeastrc.xlink.www.spring_controllers__logic.DownloadQC_Psm_PeptideLength_VS_RT_ChartDataAction;
import org.yeastrc.xlink.www.spring_controllers__logic.DownloadQC_Psm_PPM_Error_ChartDataAction;
import org.yeastrc.xlink.www.spring_controllers__logic.DownloadQC_Psm_PPM_Error_VS_RT_ChartDataAction;
import org.yeastrc.xlink.www.spring_controllers__logic.DownloadQC_Psm_PPM_Error_VS_MZ_ChartDataAction;
import org.yeastrc.xlink.www.spring_controllers__logic.DownloadQC_PsmModificationChartDataAction;
import org.yeastrc.xlink.www.spring_controllers__logic.DownloadQC_PeptideLengthChartDataAction;
import org.yeastrc.xlink.www.spring_controllers__logic.DownloadQC_MS1_VS_RetentionTime_ChartDataAction;
import org.yeastrc.xlink.www.spring_controllers__logic.DownloadQC_MS1_VS_M_Over_Z_ChartDataAction;
import org.yeastrc.xlink.www.spring_controllers__logic.DownloadQC_MS1_VS_RetentionTime_VS_M_Over_Z_ChartDataAction;
import org.yeastrc.xlink.www.spring_controllers__logic.QC_Scan_MS1_All_IntensityHeatmapImageAction;

/**
 * Spring MVC controller for the /downloadQC_*.do data-export endpoints (17 paths).
 *
 * <p>These are downloads: each Struts action writes the file straight to the
 * HttpServletResponse and returns {@code null} on success, or
 * {@code mapping.findForward(globalForwardName)} on an error/auth condition.
 * Rather than duplicate 17 complex export routines, this controller reuses the
 * existing (now Struts-runtime-independent) action classes by invoking
 * {@code execute(...)} through a tiny SpringForwardResolver whose
 * {@code findForward} simply carries the forward name back.
 *
 * <p>Return-value contract (relies on the {@code HttpServletResponse} parameter,
 * which makes Spring mark the request as handled):
 * <ul>
 *   <li>action returns null -> the response was written -> return null (no view)</li>
 *   <li>action returns a forward -> translate its name to the matching
 *       SpringForwardResolver-mapped forward:/redirect: string</li>
 * </ul>
 * The mapped actions use only {@code mapping.findForward}, so the adapter is sufficient.
 */
@Controller
public class DownloadQCController {

	//  ====== Summary ======

	@RequestMapping( A__SpringMVC_Controller_Paths.DownloadQCController_summaryPsm )
	public String summaryPsm( @ModelAttribute( "singleRequestJSONStringFieldForm" ) SingleRequestJSONStringFieldForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolveDownload( new DownloadQC_Summary_PsmChartDataAction().execute( form, request, response ) );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.DownloadQCController_summaryPeptide )
	public String summaryPeptide( @ModelAttribute( "singleRequestJSONStringFieldForm" ) SingleRequestJSONStringFieldForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolveDownload( new DownloadQC_Summary_PeptideChartDataAction().execute( form, request, response ) );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.DownloadQCController_summaryProtein )
	public String summaryProtein( @ModelAttribute( "singleRequestJSONStringFieldForm" ) SingleRequestJSONStringFieldForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolveDownload( new DownloadQC_Summary_ProteinChartDataAction().execute( form, request, response ) );
	}

	//  ====== Digestion ======

	@RequestMapping( A__SpringMVC_Controller_Paths.DownloadQCController_digestionPeptideMissedCleavage )
	public String digestionPeptideMissedCleavage( @ModelAttribute( "singleRequestJSONStringFieldForm" ) SingleRequestJSONStringFieldForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolveDownload( new DownloadQC_Digestion_PeptideMissedCleavageChartDataAction().execute( form, request, response ) );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.DownloadQCController_digestionMissedCleavagePerPeptide )
	public String digestionMissedCleavagePerPeptide( @ModelAttribute( "singleRequestJSONStringFieldForm" ) SingleRequestJSONStringFieldForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolveDownload( new DownloadQC_Digestion_MissedCleavagePerPeptideChartDataAction().execute( form, request, response ) );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.DownloadQCController_digestionPsmMissedCleavage )
	public String digestionPsmMissedCleavage( @ModelAttribute( "singleRequestJSONStringFieldForm" ) SingleRequestJSONStringFieldForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolveDownload( new DownloadQC_Digestion_PsmMissedCleavageChartDataAction().execute( form, request, response ) );
	}

	//  ====== PSM Level Statistics ======

	@RequestMapping( A__SpringMVC_Controller_Paths.DownloadQCController_psmCountVsRetentionTime )
	public String psmCountVsRetentionTime( @ModelAttribute( "singleRequestJSONStringFieldForm" ) SingleRequestJSONStringFieldForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolveDownload( new DownloadQC_PSM_Count_Vs_RetentionTime_ChartDataAction().execute( form, request, response ) );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.DownloadQCController_psmCharge )
	public String psmCharge( @ModelAttribute( "singleRequestJSONStringFieldForm" ) SingleRequestJSONStringFieldForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolveDownload( new DownloadQC_PsmChargeChartDataAction().execute( form, request, response ) );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.DownloadQCController_psmMOverZ )
	public String psmMOverZ( @ModelAttribute( "singleRequestJSONStringFieldForm" ) SingleRequestJSONStringFieldForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolveDownload( new DownloadQC_Psm_M_Over_Z_ChartDataAction().execute( form, request, response ) );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.DownloadQCController_peptideLengthVsPSMCountHistogram )
	public String peptideLengthVsPSMCountHistogram( @ModelAttribute( "singleRequestJSONStringFieldForm" ) SingleRequestJSONStringFieldForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolveDownload( new DownloadQC_PeptideLengthVsPSMCountHistogramSingleSearchChartDataAction().execute( form, request, response ) );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.DownloadQCController_peptideLengthVsPSMCountBoxplot )
	public String peptideLengthVsPSMCountBoxplot( @ModelAttribute( "singleRequestJSONStringFieldForm" ) SingleRequestJSONStringFieldForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolveDownload( new DownloadQC_PeptideLengthVsPSMCountBoxplotChartDataAction().execute( form, request, response ) );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.DownloadQCController_psmPeptideLengthVsRT )
	public String psmPeptideLengthVsRT( @ModelAttribute( "singleRequestJSONStringFieldForm" ) SingleRequestJSONStringFieldForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolveDownload( new DownloadQC_Psm_PeptideLength_VS_RT_ChartDataAction().execute( form, request, response ) );
	}

	//  ====== PSM Error Estimates ======

	@RequestMapping( A__SpringMVC_Controller_Paths.DownloadQCController_psmPpmError )
	public String psmPpmError( @ModelAttribute( "singleRequestJSONStringFieldForm" ) SingleRequestJSONStringFieldForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolveDownload( new DownloadQC_Psm_PPM_Error_ChartDataAction().execute( form, request, response ) );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.DownloadQCController_psmPpmErrorVsRT )
	public String psmPpmErrorVsRT( @ModelAttribute( "singleRequestJSONStringFieldForm" ) SingleRequestJSONStringFieldForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolveDownload( new DownloadQC_Psm_PPM_Error_VS_RT_ChartDataAction().execute( form, request, response ) );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.DownloadQCController_psmPpmErrorVsMZ )
	public String psmPpmErrorVsMZ( @ModelAttribute( "singleRequestJSONStringFieldForm" ) SingleRequestJSONStringFieldForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolveDownload( new DownloadQC_Psm_PPM_Error_VS_MZ_ChartDataAction().execute( form, request, response ) );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.DownloadQCController_psmModification )
	public String psmModification( @ModelAttribute( "singleRequestJSONStringFieldForm" ) SingleRequestJSONStringFieldForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolveDownload( new DownloadQC_PsmModificationChartDataAction().execute( form, request, response ) );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.DownloadQCController_peptideLength )
	public String peptideLength( @ModelAttribute( "singleRequestJSONStringFieldForm" ) SingleRequestJSONStringFieldForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolveDownload( new DownloadQC_PeptideLengthChartDataAction().execute( form, request, response ) );
	}

	//  ====== QC: Scan File (MS1) data downloads ======

	@RequestMapping( A__SpringMVC_Controller_Paths.DownloadQCController_ms1VsRetentionTime )
	public String ms1VsRetentionTime( @ModelAttribute( "singleRequestJSONStringFieldForm" ) SingleRequestJSONStringFieldForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolveDownload( new DownloadQC_MS1_VS_RetentionTime_ChartDataAction().execute( form, request, response ) );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.DownloadQCController_ms1VsMOverZ )
	public String ms1VsMOverZ( @ModelAttribute( "singleRequestJSONStringFieldForm" ) SingleRequestJSONStringFieldForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolveDownload( new DownloadQC_MS1_VS_M_Over_Z_ChartDataAction().execute( form, request, response ) );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.DownloadQCController_ms1VsRetentionTimeVsMOverZ )
	public String ms1VsRetentionTimeVsMOverZ( @ModelAttribute( "singleRequestJSONStringFieldForm" ) SingleRequestJSONStringFieldForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolveDownload( new DownloadQC_MS1_VS_RetentionTime_VS_M_Over_Z_ChartDataAction().execute( form, request, response ) );
	}

	//  ====== QC: Scan File (MS1) intensity heatmap image ======
	//  Writes an image to the response (no form; reads scan_file_id / project_search_id / image_width params).

	@RequestMapping( A__SpringMVC_Controller_Paths.DownloadQCController_qcScanMS1AllIntensityHeatmapImage )
	public String qcScanMS1AllIntensityHeatmapImage( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolveDownload( new QC_Scan_MS1_All_IntensityHeatmapImageAction().execute( request, response ) );
	}
}
