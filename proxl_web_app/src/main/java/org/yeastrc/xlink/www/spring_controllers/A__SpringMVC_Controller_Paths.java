package org.yeastrc.xlink.www.spring_controllers;

/**
 * Central holder for all Spring MVC controller @RequestMapping path strings.
 *
 * Each constant is named &lt;ControllerClassName&gt;_&lt;methodName&gt; for the method
 * whose @RequestMapping it provides. Generated to consolidate path literals.
 * 
 * 
 * 
 * !!!!!!!!!!!!!    SPECIAL NOTE:   ALL paths MUST end in '.do'.  The web.xml has the Spring MVC controller for '*.do' paths.
 */
public class A__SpringMVC_Controller_Paths {

	private A__SpringMVC_Controller_Paths() {}

	//  CacheDataAdminController
	public static final String CacheDataAdminController_cacheDataAllLogCurrentCacheSizes = "/cacheDataAllLogCurrentCacheSizesDFUIEWORU.do";
	public static final String CacheDataAdminController_cacheDataClearAll = "/cacheDataClearAllDFUIEWORU.do";
	public static final String CacheDataAdminController_cacheDataClearConfigData = "/cacheDataClearConfigDataDFUIEWORU.do";

	//  DownloadFileController
	public static final String DownloadFileController_downloadSearchFile = "/downloadSearchFile.do";
	public static final String DownloadFileController_downloadPDBFile = "/downloadPDBFile.do";

	//  DownloadMergedPeptidesController
	public static final String DownloadMergedPeptidesController_downloadMergedPSMsForPeptides = "/downloadMergedPSMsForPeptides.do";
	public static final String DownloadMergedPeptidesController_downloadMergedPeptides = "/downloadMergedPeptides.do";
	public static final String DownloadMergedPeptidesController_downloadMergedPeptidesForSkylinePRM = "/downloadMergedPeptidesForSkylinePRM.do";
	public static final String DownloadMergedPeptidesController_downloadMergedPeptidesForSkylineShulman = "/downloadMergedPeptidesForSkylineShulman.do";

	//  DownloadMergedProteinsController
	public static final String DownloadMergedProteinsController_downloadMergedProteinsFASTA = "/downloadMergedProteinsFASTA.do";
	public static final String DownloadMergedProteinsController_downloadMergedProteinsCLMS_CSV = "/downloadMergedProteinsCLMS_CSV.do";
	public static final String DownloadMergedProteinsController_downloadMergedProteinsPeptidesSkylineShulman = "/downloadMergedProteinsPeptidesSkylineShulman.do";
	public static final String DownloadMergedProteinsController_downloadMergedProteinsPeptidesSkylineEng = "/downloadMergedProteinsPeptidesSkylineEng.do";
	public static final String DownloadMergedProteinsController_downloadMergedProteinsXvis = "/downloadMergedProteinsXvis.do";
	public static final String DownloadMergedProteinsController_downloadMergedProteinsLengths = "/downloadMergedProteinsLengths.do";
	public static final String DownloadMergedProteinsController_downloadMergedProteins = "/downloadMergedProteins.do";
	public static final String DownloadMergedProteinsController_downloadMergedProteinUDRs = "/downloadMergedProteinUDRs.do";
	public static final String DownloadMergedProteinsController_downloadMergedProteinsAll = "/downloadMergedProteinsAll.do";
	public static final String DownloadMergedProteinsController_downloadProteinCoverageReport = "/downloadProteinCoverageReport.do";

	//  DownloadQCController
	public static final String DownloadQCController_summaryPsm = "/downloadQC_SummaryPsmChartData.do";
	public static final String DownloadQCController_summaryPeptide = "/downloadQC_SummaryPeptideChartData.do";
	public static final String DownloadQCController_summaryProtein = "/downloadQC_SummaryProteinChartData.do";
	public static final String DownloadQCController_digestionPeptideMissedCleavage = "/downloadQC_Digestion_PeptideMissedCleavageChartData.do";
	public static final String DownloadQCController_digestionMissedCleavagePerPeptide = "/downloadQC_Digestion_MissedCleavagePerPeptideChartData.do";
	public static final String DownloadQCController_digestionPsmMissedCleavage = "/downloadQC_Digestion_PsmMissedCleavageChartData.do";
	public static final String DownloadQCController_psmCountVsRetentionTime = "/downloadQC_PsmCountVsRetentionTimeChartData.do";
	public static final String DownloadQCController_psmCharge = "/downloadQC_PsmChargeChartData.do";
	public static final String DownloadQCController_psmMOverZ = "/downloadQC_Psm_M_Over_Z_ChartData.do";
	public static final String DownloadQCController_peptideLengthVsPSMCountHistogram = "/downloadQC_PeptideLengthVsPSMCountHistogramSingleSearchChartData.do";
	public static final String DownloadQCController_peptideLengthVsPSMCountBoxplot = "/downloadQC_PeptideLengthVsPSMCountBoxplotChartData.do";
	public static final String DownloadQCController_psmPeptideLengthVsRT = "/downloadQC_Psm_PeptideLength_VS_RT_ChartData.do";
	public static final String DownloadQCController_psmPpmError = "/downloadQC_Psm_PPM_Error_ChartData.do";
	public static final String DownloadQCController_psmPpmErrorVsRT = "/downloadQC_Psm_PPM_Error_VS_RT_ChartData.do";
	public static final String DownloadQCController_psmPpmErrorVsMZ = "/downloadQC_Psm_PPM_Error_VS_MZ_ChartData.do";
	public static final String DownloadQCController_psmModification = "/downloadQC_PsmModificationChartData.do";
	public static final String DownloadQCController_peptideLength = "/downloadQC_PeptideLengthChartData.do";
	public static final String DownloadQCController_ms1VsRetentionTime = "/download_MS1_VS_RetentionTime_ChartData.do";
	public static final String DownloadQCController_ms1VsMOverZ = "/download_MS1_VS_M_Over_Z_ChartData.do";
	public static final String DownloadQCController_ms1VsRetentionTimeVsMOverZ = "/download_MS1_VS_RetentionTime_VS_M_Over_Z_ChartData.do";
	public static final String DownloadQCController_qcScanMS1AllIntensityHeatmapImage = "/qc_Scan_MS1_All_IntensityHeatmapImage.do";

	//  HomeController
	public static final String HomeController_home = "/home.do";

	//  InternetExplorerNotSupportedController
	public static final String InternetExplorerNotSupportedController_internetExplorerNotSupported = "/internet_explorer_not_supported.do";

	//  InvalidRequestController
	public static final String InvalidRequestController_invalidRequestSearchesAcrossProjects = "/invalidRequestSearchesAcrossProjects.do";
	public static final String InvalidRequestController_invalidRequestData = "/invalidRequestData.do";

	//  ListProjectsController
	public static final String ListProjectsController_listProjects = "/listProjects.do";

	//  ManageConfigurationController
	public static final String ManageConfigurationController_manageConfiguration = "/manageConfiguration.do";

	//  PreGenericPeptideRedirectController
	public static final String PreGenericPeptideRedirectController_viewSearchPeptide = "/viewSearchPeptide.do";
	public static final String PreGenericPeptideRedirectController_viewMergedPeptide = "/viewMergedPeptide.do";

	//  PreGenericProteinRedirectController
	public static final String PreGenericProteinRedirectController_viewSearchCrosslinkProtein = "/viewSearchCrosslinkProtein.do";
	public static final String PreGenericProteinRedirectController_viewSearchLooplinkProtein = "/viewSearchLooplinkProtein.do";
	public static final String PreGenericProteinRedirectController_viewProteinCoverageReport = "/viewProteinCoverageReport.do";
	public static final String PreGenericProteinRedirectController_viewMergedCrosslinkProtein = "/viewMergedCrosslinkProtein.do";
	public static final String PreGenericProteinRedirectController_viewMergedLooplinkProtein = "/viewMergedLooplinkProtein.do";
	public static final String PreGenericProteinRedirectController_viewMergedProteinCoverageReport = "/viewMergedProteinCoverageReport.do";
	public static final String PreGenericProteinRedirectController_viewMergedImage = "/viewMergedImage.do";
	public static final String PreGenericProteinRedirectController_viewMergedStructure = "/viewMergedStructure.do";

	//  ProjectReadProcessCodeController
	public static final String ProjectReadProcessCodeController_projectReadProcessCode = "/projectReadProcessCode.do";

	//  ProjectsSearchListController
	public static final String ProjectsSearchListController_projectSearchList = "/projectSearchList.do";

	//  ProxlExternalViewerController
	public static final String ProxlExternalViewerController_proxlExternalViewer = "/proxlExternalViewer.do";

	//  ShortcutNotFoundController
	public static final String ShortcutNotFoundController_shortcutNotFound = "/shortcutNotFound.do";

	//  TermsOfServiceController
	public static final String TermsOfServiceController_termsOfService = "/termsOfService.do";

	//  UploadFileForImportController
	public static final String UploadFileForImportController_uploadProxlXmlOrScanFileForImport = "/uploadProxlXmlOrScanFileForImport.do";

	//  UploadPDBFileController
	public static final String UploadPDBFileController_uploadPDBFileService = "/uploadPDBFileService.do";

	//  UserAccountController
	public static final String UserAccountController_userLoginPage = "/user_loginPage.do";
	public static final String UserAccountController_userLogout = "/user_logout.do";
	public static final String UserAccountController_userNoSession = "/user_noSession.do";
	public static final String UserAccountController_userResetPasswordPage = "/user_resetPasswordPage.do";
	public static final String UserAccountController_userInsufficientAccessPrivilege = "/user_insufficient_access_privilege.do";
	public static final String UserAccountController_accountDisabled = "/account_disabled.do";
	public static final String UserAccountController_userResetPasswordProcessCode = "/user_resetPasswordProcessCode.do";
	public static final String UserAccountController_userSignupPage = "/user_signupPage.do";
	public static final String UserAccountController_userInviteProcessCode = "/user_inviteProcessCode.do";
	public static final String UserAccountController_userInviteLandingPage = "/user_inviteLandingPage.do";
	public static final String UserAccountController_userInviteCreateNewUserPage = "/user_inviteCreateNewUserPage.do";
	public static final String UserAccountController_accountPage = "/accountPage.do";
	public static final String UserAccountController_manageUsersPage = "/manageUsersPage.do";

	//  ViewMergedSearchCoverageReportController
	public static final String ViewMergedSearchCoverageReportController_mergedProteinCoverageReport = "/mergedProteinCoverageReport.do";
	public static final String ViewMergedSearchCoverageReportController_proteinCoverageReport = "/proteinCoverageReport.do";

	//  ViewMergedSearchImageController
	public static final String ViewMergedSearchImageController_image = "/image.do";

	//  ViewMergedSearchPeptidesController
	public static final String ViewMergedSearchPeptidesController_mergedPeptide = "/mergedPeptide.do";

	//  ViewMergedSearchProteinsAllController
	public static final String ViewMergedSearchProteinsAllController_mergedAllProtein = "/mergedAllProtein.do";

	//  ViewMergedSearchProteinsController
	public static final String ViewMergedSearchProteinsController_mergedCrosslinkProtein = "/mergedCrosslinkProtein.do";
	public static final String ViewMergedSearchProteinsController_mergedLooplinkProtein = "/mergedLooplinkProtein.do";

	//  ViewMergedSearchQCController
	public static final String ViewMergedSearchQCController_qc = "/qc.do";
	public static final String ViewMergedSearchQCController_qcAlex = "/qc_Alex.do";

	//  ViewMergedStructureController
	public static final String ViewMergedStructureController_structure = "/structure.do";

	//  ViewProjectController
	public static final String ViewProjectController_viewProject = "/viewProject.do";

	//  ViewSearchPeptidesController
	public static final String ViewSearchPeptidesController_peptide = "/peptide.do";

	//  ViewSearchProteinsAllController
	public static final String ViewSearchProteinsAllController_allProtein = "/allProtein.do";

	//  ViewSearchProteinsController
	public static final String ViewSearchProteinsController_crosslinkProtein = "/crosslinkProtein.do";
	public static final String ViewSearchProteinsController_looplinkProtein = "/looplinkProtein.do";

}
