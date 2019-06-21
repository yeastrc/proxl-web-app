<%@page import="org.yeastrc.xlink.www.webapp_timing.WebappTiming"%>
<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

 <c:set var="pageTitle">Crosslink Proteins - <c:out value="${ headerProject.projectTblData.title }"></c:out></c:set>

 <c:set var="pageBodyClass" >project-page</c:set>
 
 <c:set var="helpURLExtensionForSpecificPage" >en/latest/using/merged-protein.html</c:set>

 <c:set var="headerAdditions">
 
 		<script type="text/javascript" src="js/libs/jquery.tablesorter_Modified.js"></script> 
		
			<%--  Old version of d3 used in venn.js and mergedSearchesVennDiagramCreator.js
						to create the Venn Diagram --%>
		<script type="text/javascript" src="js/libs/d3_OLD_VERSION.min.js"></script>
	
		
<%--  Start of Lorikeet Core Parts --%>		

		<script src="js/libs/jquery-ui-1.12.1.min.js"></script>
		
		<%--  Only load the excanvas.min.js if it is IE 8 or lower.  IE 8 does not support HTML5 so this is a way to have HTML5 canvas support --%>
		<!--[if lte IE 8]><script language="javascript" type="text/javascript" src="js/lorikeet_google_code/excanvas.min.js"></script><![endif]-->
		
		<script src="js/lorikeet/jquery.flot.js"></script>
		<script src="js/lorikeet/jquery.flot.selection.js"></script>
		
		<script src="js/lorikeet/specview.js"></script>
		<script src="js/lorikeet/peptide.js"></script>
		<script src="js/lorikeet/aminoacid.js"></script>
		<script src="js/lorikeet/ion.js"></script>		
		
<%--  End of Lorikeet Core Parts --%>		
			
		<script type="text/javascript" src="js/libs/snap.svg-min.js"></script> <%--  Used by lorikeetPageProcessing.js --%>

		<script type="text/javascript" src="js/libs/spin.min.js"></script> 

				<%-- 
					The Struts Action for this page must call GetProteinNamesTooltipConfigData
					This include is required on this page:
					/WEB-INF/jsp-includes/proteinNameTooltipDataForJSCode.jsp
				  --%>
	

		<link rel="stylesheet" href="css/tablesorter.css" type="text/css" media="print, projection, screen" />
		<link type="text/css" rel="stylesheet" href="css/jquery.qtip.min.css" />

		<%--  some classes in this stylesheet collide with some in the lorikeet file since they are set to specific values for lorikeet drag and drop --%>
		<%-- 
		<link REL="stylesheet" TYPE="text/css" HREF="css/jquery-ui-1.10.2-Themes/ui-lightness/jquery-ui.min.css">
		--%>

		<link REL="stylesheet" TYPE="text/css" HREF="css/lorikeet.css">
		
	<c:if test="${ not empty vennDiagramDataToJSON }">
		<script type="text/text" id="venn_diagram_data_JSON"
			><c:out value="${ vennDiagramDataToJSON }" escapeXml="false"></c:out></script>
	</c:if>
	
</c:set>



<%@ include file="/WEB-INF/jsp-includes/header_main.jsp" %>

	<%@ include file="/WEB-INF/jsp-includes/body_section_data_pages_after_header_main.jsp" %>
	
		<%--  loading spinner location --%>
	<div style="opacity:1.0;position:absolute;left:50%;top:50%; z-index: 20001" id="coverage-map-loading-spinner" style="" ></div>
	
	<%--  used by createTooltipForProteinNames.js --%>
	<%@ include file="/WEB-INF/jsp-includes/proteinNameTooltipDataForJSCode.jsp" %>
	

		<%@ include file="/WEB-INF/jsp-includes/viewPsmsLoadedFromWebServiceTemplateFragment.jsp" %>
		<%@ include file="/WEB-INF/jsp-includes/viewPsmPerPeptideLoadedFromWebServiceTemplateFragment.jsp" %>
		<%@ include file="/WEB-INF/jsp-includes/viewCrosslinkReportedPeptidesLoadedFromWebServiceTemplateFragment.jsp" %>
		

			<%@ include file="/WEB-INF/jsp-includes/lorikeet_overlay_section.jsp" %>	
		

	<input type="hidden" id="project_id" value="<c:out value="${ project_id }"></c:out>"> 
	
	<c:forEach var="projectSearchId" items="${ projectSearchIds }">
	
		<%--  Put Project_Search_Ids on the page for the JS code --%>
		<input type="hidden" class=" project_search_id_jq " value="<c:out value="${ projectSearchId }"></c:out>">
	</c:forEach>	
		
		<div class="overall-enclosing-block">
	
			<h2 style="margin-bottom:5px;">List merged search proteins:</h2>
	
			<div class=" navigation-links-block ">
			
				<%-- Navigation link to QC Page --%>
				
				<%@ include file="/WEB-INF/jsp-includes/qc_NavLinks.jsp" %>
				
				[<a class="tool_tip_attached_jq" data-tooltip="View peptides" 
						href="mergedPeptide.do?<bean:write name="queryString" />">Peptide View</a>]
			
				[<a class="tool_tip_attached_jq" data-tooltip="View protein coverage report" 
						href="mergedProteinCoverageReport.do?<bean:write name="queryString" />">Coverage Report</a>]


				<%-- Navigation links to Merged Image and Merged Structure --%>
				
				<%@ include file="/WEB-INF/jsp-includes/imageAndStructureNavLinks.jsp" %>

		
			</div>
	
			<%-- query JSON in field outside of form for input to Javascript --%>
				
			<input type="hidden" id="query_json_field_outside_form" value="<c:out value="${ queryJSONToForm }" ></c:out>" > 

				<%--  A block outside any form for PSM Peptide cutoff JS code --%>
				<%@ include file="/WEB-INF/jsp-includes/psmPeptideCutoffBlock_outsideAnyForm.jsp" %>
				
	
			<html:form action="mergedCrosslinkProtein" method="get" styleId="form_get_for_updated_parameters_multiple_searches" >
						
				<logic:iterate name="searches" id="search">
					<input type="hidden" name="projectSearchId"
						class=" project_search_id_in_update_form_jq "
						value="<bean:write name="search" property="projectSearchId" />">
				</logic:iterate>

				<input type="hidden" name="queryJSON" id="query_json_field" value="<c:out value="${ queryJSONToForm }" ></c:out>"  />
				
				<%--  A block in the submitted form for PSM Peptide cutoff JS code --%>
				<%@ include file="/WEB-INF/jsp-includes/psmPeptideCutoffBlock_inSubmitForm.jsp" %>

			</html:form>
			
			<%--  Single search version, used by add/remove searches JS code --%>
			<html:form action="crosslinkProtein" method="get" styleId="form_get_for_updated_parameters_single_search" >
						
				<input type="hidden" name="queryJSON" value="<c:out value="${ queryJSONToForm }" ></c:out>"  />
				
				<%--  A block in the submitted form for PSM Peptide cutoff JS code --%> <%--  Currently empty --%>
				<%@ include file="/WEB-INF/jsp-includes/psmPeptideCutoffBlock_inSubmitForm.jsp" %>

			</html:form>			
				
			<table id="search_details_and_main_filter_criteria_main_page_root" style=" border-width: 0px; display: none; ">
			
				<%--  Set to true to show color block before search for key --%>
				<c:set var="showSearchColorBlock" value="${ true }" />
				
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
							onchange="if ( window.saveView_dataPages ) { window.saveView_dataPages.searchFormChanged_ForSaveView(); }">  
							<%-- Populated in JS --%>
	  					</select>
					</td>
				</tr>
				
				<tr>
					<td>&nbsp;</td>
					<td>
						<%@ include file="/WEB-INF/jsp-includes/sharePageURLShortenerOverlayFragment.jsp" %>

						<c:set var="UpdateButtonText" value="Update"/>
						
						<input type="button" value="${ UpdateButtonText }"  onclick="viewMergedCrosslinkProteinPageCode.updatePageForFormParams()" >

						<c:set var="page_JS_Object" value="viewSearchProteinPageCommonCrosslinkLooplinkCoverage"/>
						
						<%@ include file="/WEB-INF/jsp-includes/savePageViewButtonFragment.jsp" %>
						<%@ include file="/WEB-INF/jsp-includes/sharePageURLShortenerButtonFragment.jsp" %>
					</td>
				</tr>
			</table>
					
			<%--  <div> to contain all the remaining items above the main data display table --%>
			<div id="main_items_below_filter_criteria_table_above_main_display_table" style="display: none;"> 
					
				<div style="height: 10px;">&nbsp;</div>
						
				<div>
					<h3 style="display:inline;">Merged Crosslinks: <span id="numCrosslinks"></span>
					</h3>
		
					<div style="display:inline;">
						[<a class="tool_tip_attached_jq" data-tooltip="View looplinks (instead of crosslinks)" href="mergedLooplinkProtein.do?<bean:write name="queryString" />">View Looplinks (<span id="numLooplinks"></span>)</a>]
						[<a class="tool_tip_attached_jq" data-tooltip="View Protein List" href="mergedAllProtein.do?<bean:write name="queryString" />">Protein List</a>]

						<span id="data-download">
							<a
								data-tooltip="Download data" style="font-size:10pt;white-space:nowrap;" 
								href="#" class="tool_tip_attached_jq download-link">[Download Data]</a>
								
							<span id="data-download-options">
								Choose file format:
								<a data-tooltip="Download all cross-links and mono-links as a tab-delimited file." id="download-protein-data" class="download-option tool_tip_attached_jq" 
									href="downloadMergedProteins.do?<bean:write name="queryString" />" style="margin-top:5px;"
									>Download all cross-links and mono-links (<span id="numLinks"></span>)</a>
								<a data-tooltip="Download all distinct unique distance restraints (cross-links and loop-links) as tab-delimited text." id="download-protein-udrs" class="download-option tool_tip_attached_jq" 
									href="downloadMergedProteinUDRs.do?<bean:write name="queryString" />"
									>Download distinct UDRs (<span id="numDistinctLinks"></span>)</a>
								
								<c:if test="${ showDownloadLinks_Skyline}">
									<br><span style="font-size:15px;">Skyline export</span><br>
									<c:if test="${ showDownloadLink_SkylineShulman }">
										<a data-tooltip="Export peptides for listed proteins for import into Skyline quant. tool. (Shulman et al)" id="download-protein-shulman" class="download-option tool_tip_attached_jq" 
											href="downloadMergedProteinsPeptidesSkylineShulman.do?<bean:write name="queryString" />"
											>Export peptides for Skyline quant (Shulman et al)</a>
									</c:if>
									<a data-tooltip="Export peptides for listed proteins for Skyline PRM analysis. (Chavez et al)" id="download-protein-shulman" class="download-option tool_tip_attached_jq" 
										href="downloadMergedProteinsPeptidesSkylineEng.do?<bean:write name="queryString" />"
										>Export peptides for Skyline PRM (Chavez et al)</a>
								</c:if>
								
								<br><span style="font-size:15px;">xiNET export</span><br>
								<a data-tooltip="Download FASTA file for proteins found in cross-links or loop-links." id="download-protein-udrs" class="download-option tool_tip_attached_jq" 
									href="downloadMergedProteinsFASTA.do?<bean:write name="queryString" />"
									>Download FASTA file</a>
								<a data-tooltip="View CLMS-CSV formatted data for use in xiNET (http://crosslinkviewer.org/)" id="download-protein-xinet" class="download-option tool_tip_attached_jq" 
									href="downloadMergedProteinsCLMS_CSV.do?<bean:write name="queryString" />"
									>Export data for xiNET visualization</a>
								
								<br><span style="font-size:15px;">xVis export</span><br>
								<a data-tooltip="Export protein lengths file for cross-links and loop-links. For use in xVis (https://xvis.genzentrum.lmu.de/)" id="download-protein-lengths" class="download-option tool_tip_attached_jq" 
									href="downloadMergedProteinsLengths.do?<bean:write name="queryString" />"
									>Export protein lengths for use in xVis.</a>
								<a data-tooltip="Export cross-links and loop-links for use in xVis (https://xvis.genzentrum.lmu.de/)" id="download-links-for-xvis" class="download-option tool_tip_attached_jq" 
									href="downloadMergedProteinsXvis.do?<bean:write name="queryString" />"
									>Download cross-links and loop-links for use in xVis.</a>
							</span>
						</span>

					</div>
				</div>
		
				<%--  Block for Search lists and Venn Diagram  --%>
				<%@ include file="/WEB-INF/jsp-includes/mergedPeptideProteinSearchesListVennDiagramSection.jsp" %>
	 			
				<div style="clear:both;"></div>
	
				<%--  Block for user choosing which annotation types to display  --%>
				<%@ include file="/WEB-INF/jsp-includes/annotationDisplayManagementBlock.jsp" %>

			</div> <%--  END: <div id="main_items_below_filter_criteria_table_above_main_display_table">  --%>

				<%--  Main Table of Protein Data will be placed here --%>
			<div id="proteins_from_webservice_container" style="display: none;"></div>

		</div>
	
	

		<%--  Bundle version of core page JS --%>
		<script type="text/javascript" src="static/js_generated_bundles/data_pages/proteinCrosslinkMergedView-bundle.js?x=${cacheBustValue}"></script>
	
	
<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>

<%

WebappTiming webappTiming = (WebappTiming)request.getAttribute( "webappTiming" );

if ( webappTiming != null ) {
		
	webappTiming.markPoint( "At end of JSP" );
	
	webappTiming.logTiming();
}



%>