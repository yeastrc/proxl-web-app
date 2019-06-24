<%@page import="org.yeastrc.xlink.www.constants.ReportedPeptideCombined_IdentifierFlag_Constants"%>
<%@page import="org.yeastrc.xlink.www.webapp_timing.WebappTiming"%>
<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %>
<%@page import="org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants"%>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

 <c:set var="mergedPeptidePage" value="${ true }" />

 <c:set var="pageTitle">Peptides - <c:out value="${ headerProject.projectTblData.title }"></c:out></c:set>

 <c:set var="pageBodyClass" >project-page</c:set>

 <c:set var="helpURLExtensionForSpecificPage" >en/latest/using/merged-peptide.html</c:set>

 <c:set var="headerAdditions">
 
 		<script type="text/javascript" src="js/libs/jquery.tablesorter_Modified.js"></script> 
		<script type="text/javascript" src="js/libs/jquery.qtip.min.js"></script>
		
			<%--  Old version of d3 used in venn.js and mergedSearchesVennDiagramCreator.js
						to create the Venn Diagram --%>
		<script type="text/javascript" src="js/libs/d3_OLD_VERSION.min.js"></script>
	
		
<%--  Start of Lorikeet Core Parts --%>		

		<script src="js/libs/jquery-ui-1.12.1.min.js"></script>
		
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


	<input type="hidden" id="project_id" value="<c:out value="${ project_id }"></c:out>"> 
	
	<c:forEach var="projectSearchId" items="${ projectSearchIds }">
			<%--  Put Project_Search_Ids on the page for the JS code --%>
		<input type="hidden" class=" project_search_id_jq " value="<c:out value="${ projectSearchId }"></c:out>">
	</c:forEach>	

		<%--  loading spinner location --%>
	<div style="opacity:1.0;position:absolute;left:50%;top:50%; z-index: 20001" id="coverage-map-loading-spinner" style="" ></div>
	
	<%--  used by createTooltipForProteinNames.js --%>
	<%@ include file="/WEB-INF/jsp-includes/proteinNameTooltipDataForJSCode.jsp" %>

	
			<%@ include file="/WEB-INF/jsp-includes/lorikeet_overlay_section.jsp" %>	
	
				
		<%@ include file="/WEB-INF/jsp-includes/viewPsmsLoadedFromWebServiceTemplateFragment.jsp" %>
		<%@ include file="/WEB-INF/jsp-includes/viewPsmPerPeptideLoadedFromWebServiceTemplateFragment.jsp" %>
		
		<%--  Put values on the page for the Javascript --%>
		
		<input type="hidden" id="project_id_for_js" value="${ projectId }" >
		
		<c:forEach var="projectSearchId" items="${ projectSearchIds }">
			<input type="hidden" class=" project_search_id_input_field_jq " value="<c:out value="${ projectSearchId }"></c:out>" >
		</c:forEach>		

		
		<div class="overall-enclosing-block">
	
			<h2 style="margin-bottom:5px;">List merged search peptides:</h2>
	
			<div  class=" navigation-links-block ">

				<%-- Navigation link to QC Page --%>
				
				<%@ include file="/WEB-INF/jsp-includes/qc_NavLinks.jsp" %>
							
				[<a class="tool_tip_attached_jq" data-tooltip="View proteins" 
					href="mergedCrosslinkProtein.do?<bean:write name="queryString" />">Protein View</a>]
				[<a class="tool_tip_attached_jq" data-tooltip="View protein coverage report" 
						href="mergedProteinCoverageReport.do?<bean:write name="queryString" />">Coverage Report</a>]
				<%-- Navigation links to Merged Image and Merged Structure --%>
				<%@ include file="/WEB-INF/jsp-includes/imageAndStructureNavLinks.jsp" %>
			</div>

			<%-- query JSON in field outside of form for input to Javascript --%>
				
			<input type="hidden" id="query_json_field_outside_form" value="<c:out value="${ queryJSONToForm }" ></c:out>" > 
				
			<%--  A block outside any form for PSM Peptide cutoff JS code --%>
			<%@ include file="/WEB-INF/jsp-includes/psmPeptideCutoffBlock_outsideAnyForm.jsp" %>

			<html:form action="mergedPeptide" method="get" styleId="form_get_for_updated_parameters_multiple_searches" >
						
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
			<html:form action="peptide" method="get" styleId="form_get_for_updated_parameters_single_search" >
						
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
					<td>Type Filter:</td>
					<td colspan="2">
						<%--  Update TestAllWebLinkTypesSelected if add another option --%>

					  <label >
						<input type="checkbox" class=" link_type_jq " 
							onchange="if ( window.saveView_dataPages ) { window.saveView_dataPages.searchFormChanged_ForSaveView(); }" 
							value="<%= PeptideViewLinkTypesConstants.CROSSLINK_PSM %>"   >
						crosslinks
					  </label>
					  <label >
						<input type="checkbox" class=" link_type_jq " 
							onchange="if ( window.saveView_dataPages ) { window.saveView_dataPages.searchFormChanged_ForSaveView(); }" 
							value="<%= PeptideViewLinkTypesConstants.LOOPLINK_PSM %>" >
						looplinks
					  </label> 
					  <label >
						<input type="checkbox" class=" link_type_jq " 
							onchange="if ( window.saveView_dataPages ) { window.saveView_dataPages.searchFormChanged_ForSaveView(); }" 
							value="<%= PeptideViewLinkTypesConstants.UNLINKED_PSM %>" >
						 unlinked
					  </label>

					</td>
				</tr>
				<tr>
					<td valign="top" style="white-space: nowrap;">Modification Filter:</td>
					<td colspan="2">
					  <label >
						<input type="checkbox" class=" mod_mass_filter_jq " 
							onchange="if ( window.saveView_dataPages ) { window.saveView_dataPages.searchFormChanged_ForSaveView(); }" 
							value="" >
						No modifications
					  </label>
					  
						<logic:iterate id="modMassFilter" name="modMassFilterList">
						
						 <label style="white-space: nowrap" >
							<input type="checkbox" class=" mod_mass_filter_jq " 
								onchange="if ( window.saveView_dataPages ) { window.saveView_dataPages.searchFormChanged_ForSaveView(); }" 
						  		value="<bean:write name="modMassFilter" />" > 
						   <bean:write name="modMassFilter" />
						 </label>
						  
						</logic:iterate>				
					</td>
				</tr>				

				<tr>
					<td>Exclude links with:</td>
					<td>
						 <%--  Checkbox for removeNonUniquePSMs --%>
						<%@ include file="/WEB-INF/jsp-includes/excludeLinksWith_Remove_NonUniquePSMs_Checkbox_Fragment.jsp" %>

						 <%--  Checkbox for removeIntraProteinLinks --%>
						   <label><span style="white-space:nowrap;" >
								<input type="checkbox" id="removeIntraProteinLinks"  
									onchange="if ( window.defaultPageView ) { window.defaultPageView.searchFormChanged_ForDefaultPageView(); } ; if ( window.saveView_dataPages ) { window.saveView_dataPages.searchFormChanged_ForSaveView(); }" > 					
							 	 intra-protein links
							 </span></label>
					</td>
				</tr>				
					
				<tr>
					<td>&nbsp;</td>
					
					<td>
						<%@ include file="/WEB-INF/jsp-includes/sharePageURLShortenerOverlayFragment.jsp" %>
					
						<c:set var="UpdateButtonText" value="Update"/>
						
						<input type="button" value="${ UpdateButtonText }"  onclick="viewMergedPeptidePageCode.updatePageForFormParams()" >

						<c:set var="page_JS_Object" value="viewMergedPeptidePageCode"/>
						
						<%@ include file="/WEB-INF/jsp-includes/savePageViewButtonFragment.jsp" %>
						<%@ include file="/WEB-INF/jsp-includes/sharePageURLShortenerButtonFragment.jsp" %>
					</td>
				</tr>
			
			</table>

			<%--  <div> to contain all the remaining items above the main data display table --%>
			<div id="main_items_below_filter_criteria_table_above_main_display_table" style="display: none;"> 
								
				<div style="height: 10px;">&nbsp;</div>
				
				<div >
		
					<h3 style="display:inline;">Peptides (<span id="peptide_count_display"></span>):</h3>
	
					<div style="display:inline;">
					
								<span id="data-download">
									<a
										data-tooltip="Download data" style="font-size:10pt;white-space:nowrap;" 
										href="#" class="tool_tip_attached_jq download-link">[Download Data]</a>
										
									<span id="data-download-options">
										Choose file format:
										<a data-tooltip="Download peptide results as tab-delimited text." id="download-peptide-tld" class="download-option tool_tip_attached_jq" href="downloadMergedPeptides.do?<bean:write name="queryString" />" style="margin-top:5px;">Tab-delimited peptide data</a>
										<a data-tooltip="Download all PSMs and associated statistics." id="download-peptide-psm" class="download-option tool_tip_attached_jq" href="downloadMergedPSMsForPeptides.do?<bean:write name="queryString" />">Tab-delimited PSM data</a>
										<c:if test="${ showDownloadLinks_Skyline}">
											<a data-tooltip="Download peptide list for Skyline PRM methods (Chavez et al)." id="download-peptide-skyline" class="download-option tool_tip_attached_jq" href="downloadMergedPeptidesForSkylinePRM.do?<bean:write name="queryString" />">Peptides for Skyline PRM methods (Chavez et al)</a>
											<c:if test="${ showDownloadLink_SkylineShulman }">
												<a data-tooltip="Download peptide list for Skyline Quantitation (Shulman et al)." id="download-peptide-shulman" class="download-option tool_tip_attached_jq" href="downloadMergedPeptidesForSkylineShulman.do?<bean:write name="queryString" />">Peptides for Skyline Quantitation (Shulman et al)</a>
											</c:if>
										</c:if>
									</span>
								</span>
					</div>
				</div>
				
	
				<%--  Block for Search lists and Venn Diagram  --%>
				<%@ include file="/WEB-INF/jsp-includes/mergedPeptideProteinSearchesListVennDiagramSection.jsp" %>
	 			
							
				<%--  Block for user choosing which annotation types to display  --%>
				<%@ include file="/WEB-INF/jsp-includes/annotationDisplayManagementBlock.jsp" %>
	
	
				<%--  Show this when any of the reported Peptide entries were combined.
					  Reported Peptide entries are combined for a specific search id when there are
					  more than 1 reported peptides for a search id, unified reported peptide id pair.
					  See the code for more info.
				 --%>
				  <div id="anyReportedPeptideEntriesWereCombined" style=" display: none;"   >
				  	Note: scores noted with a 
				  	<%= ReportedPeptideCombined_IdentifierFlag_Constants.REPORTED_PEPTIDE_COMBINED__IDENTIFIER_FLAG %> 
				  	represent the best of multiple scores for this peptide in the search.
				  </div>
	
		</div> <%--  END: <div id="main_items_below_filter_criteria_table_above_main_display_table">  --%>
		
		<%--  Main Table of Peptide Data will be placed here --%>
		<div id="peptides_from_webservice_container" style="display: none;"></div>
 			
	
		<script type="text/javascript" src="static/js_generated_bundles/data_pages/peptideMergedView-bundle.js?x=${cacheBustValue}"></script>
	
		

<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>

<%

WebappTiming webappTiming = (WebappTiming)request.getAttribute( "webappTiming" );

if ( webappTiming != null ) {
		
	webappTiming.markPoint( "At end of JSP" );
	
	webappTiming.logTiming();
}



%>