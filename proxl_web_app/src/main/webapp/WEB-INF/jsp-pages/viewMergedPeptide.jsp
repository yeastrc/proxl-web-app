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
		
		<script type="text/javascript" src="js/libs/d3.min.js"></script>
		<script type="text/javascript" src="js/libs/venn.js"></script>
	
		
<%--  Start of Lorikeet Core Parts --%>		

		<script src="js/libs/jquery-ui-1.12.1.min.js"></script>
		
		<%--  Only load the excanvas.min.js if it is IE 8 or lower.  IE 8 does not support HTML5 so this is a way to have HTML5 canvas support --%>
		<!--[if lte IE 8]><script language="javascript" type="text/javascript" src="js/lorikeet_google_code/excanvas.min.js"></script><![endif]-->
		
		<script src="js/lorikeet/jquery.flot.js"></script>
		<script src="js/lorikeet/jquery.flot.selection.js"></script>
		
		<script src="js/lorikeet/specview.js?x=${cacheBustValue}"></script>
		<script src="js/lorikeet/peptide.js?x=${cacheBustValue}"></script>
		<script src="js/lorikeet/aminoacid.js?x=${cacheBustValue}"></script>
		<script src="js/lorikeet/ion.js?x=${cacheBustValue}"></script>		
		
<%--  End of Lorikeet Core Parts --%>		
				
		
		<!-- Handlebars templating library   -->
		
		<%--  
		<script type="text/javascript" src="js/libs/handlebars-v2.0.0.js"></script>
		--%>
		
		<!-- use minimized version  -->
		<script type="text/javascript" src="js/libs/handlebars-v2.0.0.min.js"></script>

		
				
				
				
		<script type="text/javascript" src="js/libs/snap.svg-min.js"></script> <%--  Used by lorikeetPageProcessing.js --%>
				
				<%-- 
					The Struts Action for this page must call GetProteinNamesTooltipConfigData
					This include is required on this page:
					/WEB-INF/jsp-includes/proteinNameTooltipDataForJSCode.jsp
				  --%>
<%--  Replaced with the JS bundle listed next
			<script type="text/javascript" src="js/createTooltipForProteinNames.js?x=${cacheBustValue}"></script>
  --%>
  		

				
<%--  Replaced with the JS bundle listed next
			 
		<script type="text/javascript" src="js/lorikeetPageProcessing.js?x=${cacheBustValue}"></script>
		
		<script type="text/javascript" src="js/handleServicesAJAXErrors.js?x=${cacheBustValue}"></script> 
 
		<script type="text/javascript" src="js/toggleVisibility.js?x=${cacheBustValue}"></script>
		
		<script type="text/javascript" src="js/psmPeptideCutoffsCommon.js?x=${cacheBustValue}"></script>
		<script type="text/javascript" src="js/psmPeptideAnnDisplayDataCommon.js?x=${cacheBustValue}"></script>
		<script type="text/javascript" src="js/minimumPSM_Count_Filter.js?x=${cacheBustValue}"></script>
		
		<script type="text/javascript" src="js/webserviceDataParamsDistribution.js?x=${cacheBustValue}"></script>

		<script type="text/javascript" src="js/viewPsmPerPeptideLoadedFromWebServiceTemplate.js?x=${cacheBustValue}"></script>
		<script type="text/javascript" src="js/viewPsmsLoadedFromWebServiceTemplate.js?x=${cacheBustValue}"></script>
		<script type="text/javascript" src="js/viewMergedPeptidePerSearchData.js?x=${cacheBustValue}"></script>
			 
		<script type="text/javascript" src="js/viewMergedPeptide.js?x=${cacheBustValue}"></script>
		
		<script type="text/javascript" src="js/sharePageURLShortener.js?x=${cacheBustValue}"></script>

		<script type="text/javascript" src="js/mergedSearchesVennDiagramCreator.js?x=${cacheBustValue}"></script>
--%>
	
		<%--  Bundle version of core page JS --%>
		<script type="text/javascript" src="static/js_generated_bundles/data_pages/peptideMergedView-bundle.js?x=${cacheBustValue}"></script>
	
		
		
		

		
		
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


	<input type="hidden" id="project_id" value="<c:out value="${ project_id }"></c:out>"> 
	
	<c:forEach var="projectSearchId" items="${ projectSearchIds }">
			<%--  Put Project_Search_Ids on the page for the JS code --%>
		<input type="hidden" class=" project_search_id_jq " value="<c:out value="${ projectSearchId }"></c:out>">
	</c:forEach>	

	<%--  used by createTooltipForProteinNames.js --%>
	<%@ include file="/WEB-INF/jsp-includes/proteinNameTooltipDataForJSCode.jsp" %>



	<div id="data_per_search_between_searches_html" >
		
		<div class=" data-per-search-between-searches " ></div>
	</div>



	<script id="peptide_data_per_search_block_template"  type="text/x-handlebars-template">

			<%--  include the template text  --%>
			<%@ include file="/WEB-INF/jsp_template_fragments/For_jsp_pages/viewMergedPeptide.jsp_page_templates/peptide_data_per_search_block_template.jsp" %>	
								

	</script>
	
	
	<script id="peptide_data_per_search_data_row_template"  type="text/x-handlebars-template">

			<%--  include the template text  --%>
			<%@ include file="/WEB-INF/jsp_template_fragments/For_jsp_pages/viewMergedPeptide.jsp_page_templates/peptide_data_per_search_data_row_template.jsp" %>	
								

	</script>
	

	
	<script id="peptide_data_per_search_child_row_template"  type="text/x-handlebars-template">

			<%--  include the template text  --%>
			<%@ include file="/WEB-INF/jsp_template_fragments/For_jsp_pages/viewMergedPeptide.jsp_page_templates/peptide_data_per_search_child_row_template.jsp" %>	
								

	</script>
	
	
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
								
			<table style="border-width:0px;">

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
			
			<div style="height: 10px;">&nbsp;</div>
			
			<div >
	
				<h3 style="display:inline;">Peptides (<bean:write name="peptideListSize" />):</h3>

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
			<c:if test="${ anyReportedPeptideEntriesWereCombined }">
			  <div style="">
			  	Note: scores noted with a 
			  	<%= ReportedPeptideCombined_IdentifierFlag_Constants.REPORTED_PEPTIDE_COMBINED__IDENTIFIER_FLAG %> 
			  	represent the best of multiple scores for this peptide in the search.
			  </div>
			</c:if>


			<%--  Create via javascript the parts that will be above the main table --%>
			<script type="text/javascript">
			
				viewMergedPeptidePageCode.createPartsAboveMainTable();
				
			</script>
			
			
				<table style="" id="crosslink-table" class="tablesorter top_data_table_jq ">
				
					<thead>
					<tr>

						<c:forEach items="${ searches }" var="search"  varStatus="searchVarStatus">
		
							<th id="search_header_${ search.projectSearchId }" style="text-align:left;font-weight:bold;width:25px;"
								><bean:write name="search" property="searchId" /></th>
								
							<script >
								$("#search_header_${ search.projectSearchId }").qtip( {
							        content: {
							            text: '<bean:write name="search" property="name" />&nbsp;(<bean:write name="search" property="searchId" />)'
							        },
							        position: {
							            my: 'bottom left',
							            at: 'top center',
							            viewport: $(window)
							         }
							    });								
								
							</script>
								
						</c:forEach>

						<th data-tooltip="Number of selected searches containing peptide" 
								class="tool_tip_attached_jq integer-number-column-header" 
								style="width:5%;font-weight:bold;padding-right: 15px;">
							Searches
						</th>
						<th data-tooltip="Type of peptide (e.g., crosslink, looplink, or unlinked)" 
								class="tool_tip_attached_jq" 
								style="text-align:left;width:10%;font-weight:bold;">
							Type
						</th>
						<th data-tooltip="Sequence of matched peptide (or first peptide in case of crosslinks)" 
								class="tool_tip_attached_jq" 
								style="text-align:left;width:10%;font-weight:bold;">
							Peptide 1
						</th>
						<th data-tooltip="Linked position in first peptide (or starting position of looplink)" 
								class="tool_tip_attached_jq" 
								style="width:5%;font-weight:bold;">
							Pos
						</th>
						<th data-tooltip="Dynamic modifications in first peptide" 
								class="tool_tip_attached_jq" 
								style="width:5%;font-weight:bold;">
							Mods
						</th>
						<c:if test="${ anyResultsHaveIsotopeLabels  }">
							<th data-tooltip="Isotope Labels for first peptide" 
									class="tool_tip_attached_jq" 
									style="width:5%;font-weight:bold;">
								Isotope Labels
							</th>
						</c:if>
						<th data-tooltip="Sequenced of second matched peptide in crosslink" 
								class="tool_tip_attached_jq" 
								style="text-align:left;width:10%;font-weight:bold;">
							Peptide 2
						</th>
						<th data-tooltip="Linked position in second peptide (or ending position of looplink)" 
								class="tool_tip_attached_jq" 
								style="width:5%;font-weight:bold;">
							Pos
						</th>
						<th data-tooltip="Dynamic modifications in second peptide" 
								class="tool_tip_attached_jq" 
								style="width:5%;font-weight:bold;">
							Mods
						</th>
						<c:if test="${ anyResultsHaveIsotopeLabels  }">
							<th data-tooltip="Isotope Labels for second peptide" 
									class="tool_tip_attached_jq" 
									style="width:5%;font-weight:bold;">
								Isotope Labels
							</th>
						</c:if>
						<th data-tooltip="Proteins (and positions) matched by first peptide and position" 
								class="tool_tip_attached_jq" 
								style="text-align:left;width:5%;font-weight:bold;">
							Protein 1
						</th>
						<th data-tooltip="Proteins (and positions) matched by second peptide and position" 
								class="tool_tip_attached_jq" 
								style="text-align:left;width:5%;font-weight:bold;">
							Protein 2
						</th>
						
						<th data-tooltip="Number of PSMs matched to this peptide (or linked pair) in all selected searches" 
								class="tool_tip_attached_jq integer-number-column-header" 
								style="width:5%;font-weight:bold;">
							Psms
						</th>
						
						
						<%--  Set to true to show color block before search for key --%>
						<c:set var="showSearchColorBlock" value="${ true }" />
						
						<c:forEach var="peptidePsmAnnotationNameDescListsForASearch"
										items="${ peptidePsmAnnotationNameDescListsForEachSearch }" 
									 	varStatus="searchVarStatus">

							<%--  Include file is dependent on containing loop having varStatus="searchVarStatus"  --%>
							<%@ include file="/WEB-INF/jsp-includes/mergedSearch_SearchIndexToSearchColorCSSClassName.jsp" %>
							

<%-- 							
							  <c:set var="outputBackgroundColorClassName" value="" />

							  <c:choose>
								<c:when test="${ isMarked.contained }">
									<c:set var="outputBackgroundColorClassName" value="${ backgroundColorClassName }" />
									<td class="${ backgroundColorClassName }">*</td>
								</c:when>
								<c:otherwise>
									<td>&nbsp;</td>
								</c:otherwise>
							  </c:choose>
--%>
							
		
							<c:forEach var="annotationDisplayNameDescription" 
									items="${ peptidePsmAnnotationNameDescListsForASearch.peptideAnnotationNameDescriptionList }">

										
									<%--  Consider displaying the description somewhere   annotationDisplayNameDescription.description --%>
								<th data-tooltip="Peptide-level <c:out value="${ annotationDisplayNameDescription.displayName }"></c:out> for this peptide (or linked pair)" 
										class=" ${ backgroundColorClassName }  tool_tip_attached_jq  " 
										style="width:10%;font-weight:bold;">
									<span style="white-space: nowrap">Peptide</span>
									<span style="white-space: nowrap"><c:out value="${ annotationDisplayNameDescription.displayName }"></c:out></span>
								</th>
								
							</c:forEach>
							

							<c:forEach var="annotationDisplayNameDescription" 
									items="${ peptidePsmAnnotationNameDescListsForASearch.psmAnnotationNameDescriptionList }">

										
									<%--  Consider displaying the description somewhere   annotationDisplayNameDescription.description --%>
								<th data-tooltip="Best PSM-level <c:out value="${ annotationDisplayNameDescription.displayName }"></c:out> for this peptide (or linked pair)" 
										class=" ${ backgroundColorClassName }  tool_tip_attached_jq  " 
										style="width:10%;font-weight:bold;">
									<span style="white-space: nowrap">Best PSM</span>
									<span style="white-space: nowrap"><c:out value="${ annotationDisplayNameDescription.displayName }"></c:out></span>
								</th>

							</c:forEach>
														
						</c:forEach>

					</tr>
					</thead>
						
					<logic:iterate id="peptideEntry" name="peptideList">

							<tr id="unified-reported-peptide-<bean:write name="peptideEntry" property="unifiedReportedPeptideId"/>"
								style="cursor: pointer; "
								onclick="viewMergedPeptidePerSearchDataFromWebServiceTemplate.showHideReportedPeptidesPerSearch( { clickedElement : this } )"
								data-unified_reported_peptide_id="<bean:write name="peptideEntry" property="unifiedReportedPeptideId"/>"
							>

								<c:forEach items="${ peptideEntry.searchContainsPeptide }" var="isMarked"  varStatus="searchVarStatus">
								
									<%--  Include file is dependent on containing loop having varStatus="searchVarStatus"  --%>
									<%@ include file="/WEB-INF/jsp-includes/mergedSearch_SearchIndexToSearchColorCSSClassName.jsp" %>
									
									<c:choose>
										<c:when test="${ isMarked.contained }">
											<td class="${ backgroundColorClassName }">*</td>
										</c:when>
										<c:otherwise>
											<td>&nbsp;</td>
										</c:otherwise>
									</c:choose>

								</c:forEach>								

								<td class="integer-number-column"><a class="show-child-data-link   " 
										href="javascript:"
										><bean:write name="peptideEntry" property="numSearches" 
											/><span class="toggle_visibility_expansion_span_jq" 
												><img src="images/icon-expand-small.png" 
													class=" icon-expand-contract-in-data-table "
													></span><span class="toggle_visibility_contraction_span_jq" 
														style="display: none;" 
														><img src="images/icon-collapse-small.png"
															class=" icon-expand-contract-in-data-table "
															></span>
									</a>
								</td>
								
								<td>
									<c:choose>
										<c:when test="${ not empty peptideEntry.mergedSearchPeptideCrosslink }">Crosslink</c:when>
										<c:when test="${ not empty peptideEntry.mergedSearchPeptideLooplink }">Looplink</c:when>
										
										<c:when test="${ not empty peptideEntry.mergedSearchPeptideUnlinked }"
											>Unlinked</c:when>
										<c:when test="${ not empty peptideEntry.mergedSearchPeptideDimer }"
											>Dimer</c:when>
										<c:otherwise>Unknown</c:otherwise>
									</c:choose>
								
								</td>								
																
								<td><bean:write name="peptideEntry" property="peptide1.sequence" /></td>
								
								<td class="integer-number-column">
									<bean:write name="peptideEntry" property="peptide1Position" />
								</td>
								<td class="">
								<%-- 	peptide1DynamicMods
									--%>
									<bean:write name="peptideEntry" property="modsStringPeptide1" />
								</td>

								<c:if test="${ anyResultsHaveIsotopeLabels  }">
									<td class="">
										<bean:write name="peptideEntry" property="isotopeLabelsStringPeptide1" />
									</td>
								</c:if>
								
								<td>
								  <c:if test="${ not empty peptideEntry.peptide2 }">
									<bean:write name="peptideEntry" property="peptide2.sequence" />
								  </c:if>
								</td>
								<td class="integer-number-column">
									<bean:write name="peptideEntry" property="peptide2Position" />
								</td>
								<td class="">
								<%-- 	peptide2DynamicMods
									--%>
									<bean:write name="peptideEntry" property="modsStringPeptide2" />
								</td>

								<c:if test="${ anyResultsHaveIsotopeLabels  }">
									<td class="">
										<bean:write name="peptideEntry" property="isotopeLabelsStringPeptide2" />
									</td>
								</c:if>
								
								
								<td>
								  <c:if test="${ not empty peptideEntry.peptide1ProteinPositions }">
									<logic:iterate id="pp" name="peptideEntry" property="peptide1ProteinPositions">
										<span class="proteinName" id="protein-id-<bean:write name="pp" property="protein.proteinSequenceVersionObject.proteinSequenceVersionId" />">
											<bean:write name="pp" property="protein.name" 
												/><c:if test="${ not empty pp.position1 }"
														>(<bean:write name="pp" property="position1" 
													/><c:if test="${ not empty pp.position2 }"
														>, <bean:write name="pp" property="position2" 
														/></c:if>)</c:if><br>
										</span>
									</logic:iterate>
								  </c:if>
								</td>
								<td>
								  <c:if test="${ not empty peptideEntry.peptide2ProteinPositions }">
									<logic:iterate id="pp" name="peptideEntry" property="peptide2ProteinPositions">
										<span class="proteinName" id="protein-id-<bean:write name="pp" property="protein.proteinSequenceVersionObject.proteinSequenceVersionId" />">
											<bean:write name="pp" property="protein.name" 
												/><c:if test="${ not empty pp.position1 }"
														>(<bean:write name="pp" property="position1" 
													/><c:if test="${ not empty pp.position2 }"
														>, <bean:write name="pp" property="position2" 
														/></c:if>)</c:if><br>
										</span>
									</logic:iterate>
								  </c:if>
								</td>								
								
								<td class="integer-number-column"><bean:write name="peptideEntry" property="numPsms" /></td>
								
<%-- 
For the details below:									
peptidePsmAnnotationValueListsForEachSearch
								
private int searchId;

	private List<String> psmAnnotationValueList;
	private List<String> peptideAnnotationValueList;

private List<String> psmAnnotationValueList;
private List<String> peptideAnnotationValueList;
--%>								

						
						<%--  Adjust colspan of <td> of child row that displays the PSMs for number of columns in current table --%>
								
							<%--  Page variable for the number of columns added to display 
									Peptide and PSM annotations for the searches --%>
							<%--   Init to zero --%>
						<c:set var="columnsAddedForAnnotationData" value="${ 0 }" />



						<c:forEach var="peptidePsmAnnotationValueListsForASearch"
										items="${ peptideEntry.peptidePsmAnnotationValueListsForEachSearch }" 
									 	varStatus="searchVarStatus">

							<%--  Include file is dependent on containing loop having varStatus="searchVarStatus"  --%>
							<%@ include file="/WEB-INF/jsp-includes/mergedSearch_SearchIndexToSearchColorCSSClassName.jsp" %>
							

<%-- 							
							  <c:set var="outputBackgroundColorClassName" value="" />

							  <c:choose>
								<c:when test="${ isMarked.contained }">
									<c:set var="outputBackgroundColorClassName" value="${ backgroundColorClassName }" />
									<td class="${ backgroundColorClassName }">*</td>
								</c:when>
								<c:otherwise>
									<td>&nbsp;</td>
								</c:otherwise>
							  </c:choose>
--%>
							<c:set var="peptidesLength" value="${ fn:length(  peptidePsmAnnotationValueListsForASearch.peptideAnnotationValueList ) }" />
							
							<c:set var="psmsLength" value="${ fn:length(  peptidePsmAnnotationValueListsForASearch.psmAnnotationValueList ) }" />

							<%--  Add to value for length of Peptide and PSM value lists --%>
							<c:set var="columnsAddedForAnnotationData" 
								value="${ columnsAddedForAnnotationData + peptidesLength + psmsLength }" />
							
							<%-- Display Peptide Annotation values --%>							
		
							<c:forEach var="annotationDisplayValue" 
									items="${ peptidePsmAnnotationValueListsForASearch.peptideAnnotationValueList }">
								<td  class=" ${ backgroundColorClassName }    " 
										style="width:10%;font-weight:bold;">
									<span style="white-space: nowrap"><c:out value="${ annotationDisplayValue }"></c:out></span>
								</td>
							</c:forEach>
							
							<%-- Display Psm Annotation values --%>							

							<c:forEach var="annotationDisplayValue" 
									items="${ peptidePsmAnnotationValueListsForASearch.psmAnnotationValueList }">
								<td class=" ${ backgroundColorClassName }  tool_tip_attached_jq  " 
										style="width:10%;font-weight:bold;">
									<span style="white-space: nowrap"><c:out value="${ annotationDisplayValue }"></c:out></span>
								</td>
							</c:forEach>
							


						</c:forEach>						
														
							</tr>

							<tr class="expand-child" style="display:none;">
														
								<%--  colspan set to the number of searches plus the number of other columns --%>
								<td colspan="<c:out value="${ fn:length( searches ) + 11 + columnsAddedForAnnotationData }"
									></c:out>" align="center"
									class=" child_data_container_jq ">
								
									<div style="color: green; font-size: 16px; padding-top: 10px; padding-bottom: 10px;" >
										Loading...
									</div>
								
									
								</td>
							</tr>
							
					</logic:iterate>
				</table>
	


		</div>
	
	

<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>

<%

WebappTiming webappTiming = (WebappTiming)request.getAttribute( "webappTiming" );

if ( webappTiming != null ) {
		
	webappTiming.markPoint( "At end of JSP" );
	
	webappTiming.logTiming();
}



%>