<%@page import="org.yeastrc.xlink.www.webapp_timing.WebappTiming"%>
<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %>
<%@page import="org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants"%>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

 <c:set var="pageTitle">Looplink Proteins - <c:out value="${ headerProject.projectTblData.title }"></c:out></c:set>

 <c:set var="pageBodyClass" >project-page</c:set>

 <c:set var="helpURLExtensionForSpecificPage" >en/latest/using/protein.html</c:set>

 <c:set var="headerAdditions">
 
 		<script type="text/javascript" src="js/libs/jquery.tablesorter_Modified.js"></script> 
		<script type="text/javascript" src="js/libs/jquery.qtip.min.js"></script>
		
		<script src="js/libs/jquery-ui-1.12.1.min.js"></script>
		
				<%-- 
					The Struts Action for this page must call GetProteinNamesTooltipConfigData
					This include is required on this page:
					/WEB-INF/jsp-includes/proteinNameTooltipDataForJSCode.jsp
				  --%>

		<link rel="stylesheet" href="css/tablesorter.css" type="text/css" media="print, projection, screen" />
		<link type="text/css" rel="stylesheet" href="css/jquery.qtip.min.css" />
		
		<%--  some classes in this stylesheet collide with some in the lorikeet file since they are set to specific values for lorikeet drag and drop --%>
		<%-- <link REL="stylesheet" TYPE="text/css" HREF="css/jquery-ui-1.10.2-Themes/ui-lightness/jquery-ui.min.css"> --%>

		<link REL="stylesheet" TYPE="text/css" HREF="css/lorikeet.css">
		
</c:set>



<%@ include file="/WEB-INF/jsp-includes/header_main.jsp" %>

	<%@ include file="/WEB-INF/jsp-includes/body_section_data_pages_after_header_main.jsp" %>

	<%--  used by createTooltipForProteinNames.js --%>
	<%@ include file="/WEB-INF/jsp-includes/proteinNameTooltipDataForJSCode.jsp" %>

	<input type="hidden" id="project_id" value="<c:out value="${ project_id }"></c:out>"> 
	
	<%--  Put Project_Search_Id on the page for the JS code --%>
	<input type="hidden" class=" project_search_id_jq " value="<c:out value="${ projectSearchId }"></c:out>">
	
		<%--  loading spinner location --%>
	<div style="opacity:1.0;position:absolute;left:50%;top:50%; z-index: 20001" id="coverage-map-loading-spinner" style="" ></div>
	
		<%@ include file="/WEB-INF/jsp-includes/defaultPageViewFragment.jsp" %>
				
		<%@ include file="/WEB-INF/jsp-includes/viewPsmsLoadedFromWebServiceTemplateFragment.jsp" %>
		<%@ include file="/WEB-INF/jsp-includes/viewPsmPerPeptideLoadedFromWebServiceTemplateFragment.jsp" %>
		
		<%@ include file="/WEB-INF/jsp-includes/viewLooplinkReportedPeptidesLoadedFromWebServiceTemplateFragment.jsp" %>
				
				
			<%@ include file="/WEB-INF/jsp-includes/lorikeet_overlay_section.jsp" %>	
				
		<div class="overall-enclosing-block">
			
			
			<h2 style="margin-bottom:5px;">List search proteins:</h2>
	

			<div  class=" navigation-links-block "> 

				<%-- Navigation link to QC Page --%>
				
				<%@ include file="/WEB-INF/jsp-includes/qc_NavLinks.jsp" %>
				
				[<a class="tool_tip_attached_jq" data-tooltip="View peptides" 
					href="<proxl:defaultPageUrl pageName="/peptide" projectSearchId="${ search.projectSearchId }"
						>peptide.do?projectSearchId=<bean:write name="search" property="projectSearchId" 
						/>&queryJSON=<c:out value="${ peptidePageQueryJSON }" escapeXml="false" 
						></c:out></proxl:defaultPageUrl>"
						>Peptide View</a>]
						 									
				[<a class="tool_tip_attached_jq" data-tooltip="View protein coverage report" 
						href="<proxl:defaultPageUrl pageName="/proteinCoverageReport" projectSearchId="${ search.projectSearchId }">proteinCoverageReport.do?<bean:write name="queryString" /></proxl:defaultPageUrl>"
						>Coverage Report</a>]
				
				<%-- Navigation links to Merged Image and Merged Structure --%>
				
				<%@ include file="/WEB-INF/jsp-includes/imageAndStructureNavLinks.jsp" %>

			</div>
				
			<%-- query JSON in field outside of form for input to Javascript --%>
				
			<input type="hidden" id="query_json_field_outside_form" value="<c:out value="${ queryJSONToForm }" ></c:out>" > 

			<%--  A block outside any form for PSM Peptide cutoff JS code --%>
			<%@ include file="/WEB-INF/jsp-includes/psmPeptideCutoffBlock_outsideAnyForm.jsp" %>

			<script type="text/text" id="form_get_for_updated_parameters__id_to_use">form_get_for_updated_parameters_single_search</script>

			<%--  Single search version, used by add/remove searches JS code --%>
			<html:form action="looplinkProtein" method="get" styleId="form_get_for_updated_parameters_single_search" >
						
				<input type="hidden" name="projectSearchId" class=" project_search_id_in_update_form_jq " 
					value="${ search.projectSearchId }">
				<%-- cannot use <html:hidden property="projectSearchId" /> since projectSearchId is an array --%>
				
				<input type="hidden" name="queryJSON"  id="query_json_field"  value="<c:out value="${ queryJSONToForm }" ></c:out>" />
				
				<%--  A block in the submitted form for PSM Peptide cutoff JS code --%> <%--  Currently empty --%>
				<%@ include file="/WEB-INF/jsp-includes/psmPeptideCutoffBlock_inSubmitForm.jsp" %>

			</html:form>			
							
			<html:form action="mergedLooplinkProtein" method="get" styleId="form_get_for_updated_parameters_multiple_searches" >
			
				<input type="hidden" name="queryJSON" value="<c:out value="${ queryJSONToForm }" ></c:out>" />
				
				<%--  A block in the submitted form for PSM Peptide cutoff JS code --%>
				<%@ include file="/WEB-INF/jsp-includes/psmPeptideCutoffBlock_inSubmitForm.jsp" %>

			</html:form>


			<table id="search_details_and_main_filter_criteria_main_page_root" style=" border-width: 0px; display: none; ">
				
				<%--  Set to false to not show color block before search for key --%>
				<c:set var="showSearchColorBlock" value="${ false }" />
				
				<%--  Include file is dependent on containing loop having varStatus="searchVarStatus"  --%>
				<%@ include file="/WEB-INF/jsp-includes/searchDetailsBlock.jsp" %>

				<%--  Minimum PSM filter --%>
				<%@ include file="/WEB-INF/jsp-includes/minimumPSM_Count_Filter.jsp" %>

				<tr>
					<td>Exclude links with:</td>
					<td>
						 <%--  Checkboxes --%>
						<%@ include file="/WEB-INF/jsp-includes/excludeLinksWith_Checkboxes_ProteinPages_Fragment.jsp" %>
					</td>
				</tr>

				<tr>
					<td>Exclude organisms:</td>
					<td id="excludeTaxonomies"> <%--  Populated in JS --%>
					</td>
				</tr>

				<tr>
					<td>Exclude protein(s):</td>
					<td>
						<%-- 
						All <option> values must be parsable as integers:
						--%>
						<select name="excludedProteins" multiple="multiple" id="excludeProtein" 
								onchange=" if ( window.defaultPageView ) { window.defaultPageView.searchFormChanged_ForDefaultPageView(); } ; if ( window.saveView_dataPages ) { window.saveView_dataPages.searchFormChanged_ForSaveView(); }" >  
							<%-- Populated in JS --%>
	  					</select>
					</td>
				</tr>
				
				<tr>
					<td>&nbsp;</td>
					<td>
						<%@ include file="/WEB-INF/jsp-includes/sharePageURLShortenerOverlayFragment.jsp" %>
					
						<c:set var="UpdateButtonText" value="Update"/>
						<input type="button" value="${ UpdateButtonText }"  onclick="viewSearchLooplinkProteinPageCode.updatePageForFormParams()" >
						
						<c:set var="projectSearchId" value="${ search.projectSearchId }"/>	
						<c:set var="page_JS_Object" value="viewSearchProteinPageCommonCrosslinkLooplinkCoverage"/>
												
						<%@ include file="/WEB-INF/jsp-includes/defaultPageViewButtonFragment.jsp" %>
						<%@ include file="/WEB-INF/jsp-includes/savePageViewButtonFragment.jsp" %>
						<%@ include file="/WEB-INF/jsp-includes/sharePageURLShortenerButtonFragment.jsp" %>
					</td>
				</tr>
			</table>
			
			<%--  <div> to contain all the remaining items above the main data display table --%>
			<div id="main_items_below_filter_criteria_table_above_main_display_table" style="display: none;"> 
				
				<div style="height: 10px;">&nbsp;</div>
					
				<h3 style="display:inline;">Looplinks (<span id="numLooplinks" ></span>):</h3>
				<div style="display:inline;">
	
					[<a class="tool_tip_attached_jq" data-tooltip="View crosslinks (instead of looplinks)" 
							href="<proxl:defaultPageUrl pageName="/crosslinkProtein" projectSearchId="${ search.projectSearchId }">crosslinkProtein.do?<bean:write name="queryString" /></proxl:defaultPageUrl>"
							>View Crosslinks (<span id="numCrosslinks"></span>)</a>]
	
					[<a class="tool_tip_attached_jq" data-tooltip="View Protein List" 
							href="<proxl:defaultPageUrl pageName="/allProtein" projectSearchId="${ search.projectSearchId }">allProtein.do?<bean:write name="queryString" /></proxl:defaultPageUrl>"
							>Protein List</a>]
	
	
							<span id="data-download">
								<a
									data-tooltip="Download data" style="font-size:10pt;white-space:nowrap;" 
									href="#" class="tool_tip_attached_jq download-link">[Download Data]</a>
									
								<span id="data-download-options">
									Choose file format:
									<a data-tooltip="Download all cross-links and mono-links as a tab-delimited file." id="download-protein-data" class="download-option tool_tip_attached_jq" href="downloadMergedProteins.do?<bean:write name="queryString" />" style="margin-top:5px;"
										>Download all cross-links and mono-links (<span id="numLinks"></span>)</a>
									<a data-tooltip="Download all distinct unique distance restraints (cross-links and loop-links) as tab-delimited text." id="download-protein-udrs" class="download-option tool_tip_attached_jq" href="downloadMergedProteinUDRs.do?<bean:write name="queryString" />"
										>Download distinct UDRs (<span id="numDistinctLinks"></span>)</a>
									
									<c:if test="${ showDownloadLinks_Skyline}">
										<br><span style="font-size:15px;">Skyline export</span><br>
										<c:if test="${ showDownloadLink_SkylineShulman }">
											<a data-tooltip="Export peptides for listed proteins for import into Skyline quant. tool. (Shulman et al)" id="download-protein-shulman" class="download-option tool_tip_attached_jq" href="downloadMergedProteinsPeptidesSkylineShulman.do?<bean:write name="queryString" />">Export peptides for Skyline quant (Shulman et al)</a>
										</c:if>
										<a data-tooltip="Export peptides for listed proteins for Skyline PRM analysis. (Chavez et al)" id="download-protein-shulman" class="download-option tool_tip_attached_jq" href="downloadMergedProteinsPeptidesSkylineEng.do?<bean:write name="queryString" />">Export peptides for Skyline PRM (Chavez et al)</a>
									</c:if>
									
									<br><span style="font-size:15px;">xiNET / xiVIEW export</span><br>
									<a data-tooltip="Download FASTA file for proteins found in cross-links or loop-links." id="download-protein-udrs" class="download-option tool_tip_attached_jq" href="downloadMergedProteinsFASTA.do?<bean:write name="queryString" />">Download FASTA file</a>
									<a data-tooltip="View CLMS-CSV formatted data for use in xiNET (http://crosslinkviewer.org/)" id="download-protein-xinet" class="download-option tool_tip_attached_jq" href="downloadMergedProteinsCLMS_CSV.do?<bean:write name="queryString" />">Export data for xiNET visualization</a>
									<a data-tooltip="View CLMS-CSV formatted data for use in xiVIEW (http://crosslinkviewer.org/)" id="download-protein-xinet" class="download-option tool_tip_attached_jq" href="downloadMergedProteinsCLMS_CSV.do?<bean:write name="queryString" />&format=xiview">Export data for xiVIEW visualization</a>

									<br><span style="font-size:15px;">xVis export</span><br>
									<a data-tooltip="Export protein lengths file for cross-links and loop-links. For use in xVis (https://xvis.genzentrum.lmu.de/)" id="download-protein-lengths" class="download-option tool_tip_attached_jq" href="downloadMergedProteinsLengths.do?<bean:write name="queryString" />">Export protein lengths for use in xVis.</a>
									<a data-tooltip="Export cross-links and loop-links for use in xVis (https://xvis.genzentrum.lmu.de/)" id="download-links-for-xvis" class="download-option tool_tip_attached_jq" href="downloadMergedProteinsXvis.do?<bean:write name="queryString" />">Download cross-links and loop-links for use in xVis.</a>
								</span>
							</span>
	
	
				</div>
	
				<%--  Block for user choosing which annotation types to display  --%>
				<%@ include file="/WEB-INF/jsp-includes/annotationDisplayManagementBlock.jsp" %>

			</div> <%--  END: <div id="main_items_below_filter_criteria_table_above_main_display_table">  --%>
			
				<%--  Main Table of Protein Data will be placed here --%>
			<div id="proteins_from_webservice_container" style="display: none;"></div>
			
		</div>


	<script type="text/javascript" src="static/js_generated_bundles/data_pages/looplinkProteinView-bundle.js?x=${cacheBustValue}"></script>
		
<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>


<%

WebappTiming webappTiming = (WebappTiming)request.getAttribute( "webappTiming" );

if ( webappTiming != null ) {
		
	webappTiming.markPoint( "At end of JSP" );
	
	webappTiming.logTiming();
}



%>