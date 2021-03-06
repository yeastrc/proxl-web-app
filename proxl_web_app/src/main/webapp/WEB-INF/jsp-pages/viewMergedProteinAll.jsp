<%@page import="org.yeastrc.xlink.www.webapp_timing.WebappTiming"%>
<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %>
<%@page import="org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants"%>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

<%--  viewMergedProteinAll.jsp  --%>

 <c:set var="pageTitle">Proteins - <c:out value="${ headerProject.projectTblData.title }"></c:out></c:set>

 <c:set var="pageBodyClass" >project-page</c:set>
 
 <c:set var="helpURLExtensionForSpecificPage" >en/latest/using/merged-protein.html</c:set>

 <c:set var="headerAdditions">
 
 		<script type="text/javascript" src="js/libs/jquery.tablesorter_Modified.js"></script> 
		<script type="text/javascript" src="js/libs/jquery.qtip.min.js"></script>
		
			<%--  Old version of d3 used in venn.js and mergedSearchesVennDiagramCreator.js
						to create the Venn Diagram --%>
		<script type="text/javascript" src="js/libs/d3_OLD_VERSION.min.js"></script>
	

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
	
		<%@ include file="/WEB-INF/jsp-includes/viewReportedPeptidesForProteinAllLoadedFromWebServiceTemplateFragment.jsp" %>
	
			<%@ include file="/WEB-INF/jsp-includes/lorikeet_overlay_section.jsp" %>	
	
	<input type="hidden" id="project_id" value="<c:out value="${ project_id }"></c:out>"> 
	
	<c:forEach var="projectSearchId" items="${ projectSearchIds }">
	
		<%--  Put Project_Search_Ids on the page for the JS code --%>
		<input type="hidden" class=" project_search_id_jq " value="<c:out value="${ projectSearchId }"></c:out>">
	</c:forEach>			
		
		<div class="overall-enclosing-block">
			
			<h2 style="margin-bottom:5px;">List merged search proteins:</h2>
	
			<div  class=" navigation-links-block ">

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

	
			<html:form action="mergedAllProtein" method="get" styleId="form_get_for_updated_parameters_multiple_searches" >
			
				<logic:iterate name="searches" id="search">
					<input type="hidden" name="projectSearchId"
						class=" project_search_id_in_update_form_jq "
						value="<bean:write name="search" property="projectSearchId" />">
				</logic:iterate>

				<input type="hidden" name="queryJSON" id="query_json_field"  value="<c:out value="${ queryJSONToForm }" ></c:out>" />
				
				<%--  A block in the submitted form for PSM Peptide cutoff JS code --%>
				<%@ include file="/WEB-INF/jsp-includes/psmPeptideCutoffBlock_inSubmitForm.jsp" %>

			</html:form>

			<%--  Single search version, used by add/remove searches JS code --%>
			<html:form action="allProtein" method="get" styleId="form_get_for_updated_parameters_single_search" >
						
				<input type="hidden" name="queryJSON"  value="<c:out value="${ queryJSONToForm }" ></c:out>" />
				
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
				</tr>

				<tr>
					<td>Exclude protein(s):</td>
					<td>
						<%--   
						All <option> values must be parsable as integers:
						--%>
						<select name="excludedProteins" multiple="multiple" id="excludeProtein"  
								onchange="if ( window.saveView_dataPages ) { window.saveView_dataPages.searchFormChanged_ForSaveView(); }" >  
							<%-- Populated in JS --%>
	  					</select>
					</td>
				</tr>
				
				<tr>
					<td>&nbsp;</td>
					<td>
						<%@ include file="/WEB-INF/jsp-includes/sharePageURLShortenerOverlayFragment.jsp" %>
					
						<c:set var="UpdateButtonText" value="Update"/>
						
						<input type="button" value="${ UpdateButtonText }"  onclick="viewMergedProteinAllPageCode.updatePageForFormParams()" >

						<c:set var="page_JS_Object" value="viewSearchProteinPageCommonCrosslinkLooplinkCoverage"/>
						
						<%@ include file="/WEB-INF/jsp-includes/savePageViewButtonFragment.jsp" %>
						<%@ include file="/WEB-INF/jsp-includes/sharePageURLShortenerButtonFragment.jsp" %>
					</td>
				</tr>
			
			</table>
			
			<%--  <div> to contain all the remaining items above the main data display table --%>
			<div id="main_items_below_filter_criteria_table_above_main_display_table" style="display: none;"> 

				<div style="height: 10px;">&nbsp;</div>
				
				<div >
					<h3 style="display:inline;">Merged Proteins: <span id="numProteins_1" ></span>
					</h3>			
					<div style="display:inline;">
						[<a class="tool_tip_attached_jq" data-tooltip="View crosslinks" href="mergedCrosslinkProtein.do?<bean:write name="queryString" />">View Crosslinks</a>]
						[<a class="tool_tip_attached_jq" data-tooltip="View looplinks" href="mergedLooplinkProtein.do?<bean:write name="queryString" />">View Looplinks</a>]
						[<a class="tool_tip_attached_jq" data-tooltip="Download all proteins as tab-delimited text" 
							href="downloadMergedProteinsAll.do?<bean:write name="queryString" />"
							>Download Data (<span id="numProteins_2" ></span>)</a>]
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
	
	
		<script type="text/javascript" src="static/js_generated_bundles/data_pages/proteinAllMergedView-bundle.js?x=${cacheBustValue}"></script>
	

<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>

<%

WebappTiming webappTiming = (WebappTiming)request.getAttribute( "webappTiming" );

if ( webappTiming != null ) {
		
	webappTiming.markPoint( "At end of JSP" );
	
	webappTiming.logTiming();
}



%>