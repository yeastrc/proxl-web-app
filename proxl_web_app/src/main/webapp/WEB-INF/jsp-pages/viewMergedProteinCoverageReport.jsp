<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

 <c:set var="pageTitle">Protein Coverage - <c:out value="${ headerProject.projectTblData.title }"></c:out></c:set>

 <c:set var="pageBodyClass" >project-page</c:set>

 <c:set var="headerAdditions">

		<script type="text/javascript" src="js/libs/jquery.tablesorter_Modified.js"></script> 
		<script type="text/javascript" src="js/libs/jquery.qtip.min.js"></script>

		<%--  For change order of Searches "Re-order searches" --%>
		<script src="js/libs/jquery-ui-1.12.1.min.js"></script>
				
				<%-- 
					The Struts Action for this page must call GetProteinNamesTooltipConfigData
					This include is required on this page:
					/WEB-INF/jsp-includes/proteinNameTooltipDataForJSCode.jsp
				  --%>
				  
		<link rel="stylesheet" href="css/tablesorter.css" type="text/css" media="print, projection, screen" />
		<link type="text/css" rel="stylesheet" href="css/jquery.qtip.min.css" />

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

	
		<%@ include file="/WEB-INF/jsp-includes/defaultPageViewFragment.jsp" %>
			
		<div class="overall-enclosing-block">
	
			<h2 style="margin-bottom:5px;">List search protein coverage:</h2>
			
					<%--  Set Navigatation data --%>
				<c:choose>
				 <c:when test="${ mergedPage }"> 
				 
				 	<c:set var="peptideNav">mergedPeptide</c:set>
				 	<c:set var="proteinNav">mergedCrosslinkProtein</c:set>
				 
				 </c:when>
				 <c:otherwise>

				 	<c:set var="peptideNav">peptide</c:set>
				 	<c:set var="proteinNav">crosslinkProtein</c:set>
				 
				 </c:otherwise>
				</c:choose> 

			<div  class=" navigation-links-block ">
			
				<%-- Navigation link to QC Page --%>
				
				<%@ include file="/WEB-INF/jsp-includes/qc_NavLinks.jsp" %>
										
				[<a class="tool_tip_attached_jq" data-tooltip="View peptides" href="${ peptideNav }.do?<bean:write name="queryString" />">Peptide View</a>]
		
				[<a class="tool_tip_attached_jq" data-tooltip="View proteins" href="${ proteinNav }.do?<bean:write name="queryString" />">Protein View</a>]
				
				<%-- Navigation links to Merged Image and Merged Structure --%>
				
				<%@ include file="/WEB-INF/jsp-includes/imageAndStructureNavLinks.jsp" %>

			</div>
	
			<%-- query JSON in field outside of form for input to Javascript --%>
				
			<input type="hidden" id="query_json_field_outside_form" value="<c:out value="${ queryJSONToForm }" ></c:out>" > 
	
			<%--  A block outside any form for PSM Peptide cutoff JS code --%>
			<%@ include file="/WEB-INF/jsp-includes/psmPeptideCutoffBlock_outsideAnyForm.jsp" %>

			<%-- Coding to handle both merged an unmerged with 1 JSP --%>
			<c:choose>
			 <c:when test="${ mergedPage }"> 	
				<script type="text/text" id="form_get_for_updated_parameters__id_to_use">form_get_for_updated_parameters_multiple_searches</script>
			 </c:when>
			 <c:otherwise>
				<script type="text/text" id="form_get_for_updated_parameters__id_to_use">form_get_for_updated_parameters_single_search</script>
			 </c:otherwise>
			</c:choose>
		 
			<form action="mergedProteinCoverageReport.do" method="get" id="form_get_for_updated_parameters_multiple_searches">

				<logic:iterate name="searches" id="search">
					<input type="hidden" name="projectSearchId"
						class=" project_search_id_in_update_form_jq "
						value="<bean:write name="search" property="projectSearchId" />">
				</logic:iterate>

				<input type="hidden" name="queryJSON" class="query_json_field_jq"   value="<c:out value="${ queryJSONToForm }" ></c:out>" />
							
				<%--  A block in the submitted form for PSM Peptide cutoff JS code --%>
				<%@ include file="/WEB-INF/jsp-includes/psmPeptideCutoffBlock_inSubmitForm.jsp" %>

			</form>
	

			<form action="proteinCoverageReport.do" method="get" id="form_get_for_updated_parameters_single_search">
									
				<logic:iterate name="searches" id="search">
					<input type="hidden" name="projectSearchId"
						class=" project_search_id_in_update_form_jq "
						value="<bean:write name="search" property="projectSearchId" />">
				</logic:iterate>
			
				<input type="hidden" name="queryJSON" class="query_json_field_jq"  value="<c:out value="${ queryJSONToForm }" ></c:out>" />
			
				<%--  A block in the submitted form for PSM Peptide cutoff JS code --%>
				<%@ include file="/WEB-INF/jsp-includes/psmPeptideCutoffBlock_inSubmitForm.jsp" %> <%--  Currently empty --%>

			</form>
		 
			
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
						
						<input type="button" value="${ UpdateButtonText }"  onclick="viewProteinCoverageReportPageCode.updatePageForFormParams()" >
						
						<c:if test="${ not mergedPage }"> 	
							<c:set var="projectSearchId" value="${ search.projectSearchId }"/>	
							<c:set var="page_JS_Object" value="viewSearchProteinPageCommonCrosslinkLooplinkCoverage"/>
						
							<%@ include file="/WEB-INF/jsp-includes/defaultPageViewButtonFragment.jsp" %>
						</c:if>		
						
						<c:set var="page_JS_Object" value="viewSearchProteinPageCommonCrosslinkLooplinkCoverage"/>
					
						<%@ include file="/WEB-INF/jsp-includes/savePageViewButtonFragment.jsp" %>

						<%@ include file="/WEB-INF/jsp-includes/sharePageURLShortenerButtonFragment.jsp" %>
					</td>
				</tr>
			</table>
				
			<%--  <div> to contain all the remaining items --%>

			<div id="main_rest_after_filters_table_container"  style=" display: none;"> <%--  Main Table Container --%>
			
				<div style="height: 10px;">&nbsp;</div>			
		
				<h3 style="display:inline;">Protein Coverage Report:</h3>
	
				<div style="display:inline;">
					[<a class="tool_tip_attached_jq" data-tooltip="Download as tab-delimited text" href="downloadProteinCoverageReport.do?<bean:write name="queryString" />">Download Coverage Report</a>]
				</div>
	
				<c:set var="coveragePageForAnnDispMgmt" value="${ true }"/>
				
				<%--  Block for user choosing which annotation types to display  --%>
				<%@ include file="/WEB-INF/jsp-includes/annotationDisplayManagementBlock.jsp" %>

				<table style="" id="main_page_data_table" class="tablesorter">
				
					<thead>
					<tr>
						<th style="text-align:left;width:15%;font-weight:bold;" >
							Protein
						</th>
						<th style="width:5%;text-align:left;font-weight:bold;" class=" tool_tip_attached_jq "
								data-tooltip="Residues" >
							Residues
						</th>
						<th style="width:5%;font-weight:bold;" class=" tool_tip_attached_jq "
								data-tooltip="Sequence Coverage" >
							Sequence Coverage
						</th>
						<th style="width:5%;text-align:left;font-weight:bold;" class=" tool_tip_attached_jq "
								data-tooltip="Count of Linkable Residues" >
							Count of Linkable Residues
						</th>
						<th style="width:5%;text-align:left;font-weight:bold;" class=" tool_tip_attached_jq "
								data-tooltip="Count of Linkable Residues covered by Sequence Coverage" >
							Linkable Residues Covered
						</th>
						<th style="width:5%;text-align:left;font-weight:bold;" class=" tool_tip_attached_jq "
								data-tooltip="Fraction of Linkable Residues covered by Sequence Coverage" >
							Linkable Residues Coverage
						</th>

						<th style="width:6%;font-weight:bold;" class=" tool_tip_attached_jq "
								data-tooltip="Count of Monolinks, Looplinks and Crosslinks Residues" >
							M+L+C Residues
						</th>
						<th style="width:6%;font-weight:bold;" class=" tool_tip_attached_jq "
								data-tooltip="Fraction of Linkable Residues covered by Monolinks, Looplinks and Crosslinks" >
							M+L+C Coverage
						</th>

						<th style="width:6%;font-weight:bold;" class=" tool_tip_attached_jq "
								data-tooltip="Count of Looplinks and Crosslinks Residues" >
							L+C Residues
						</th>
						<th style="width:6%;font-weight:bold;" class=" tool_tip_attached_jq "
								data-tooltip="Fraction of Linkable Residues covered by Looplinks and Crosslinks" >
							L+C Coverage						
						</th>

						<th style="width:6%;font-weight:bold;" class=" tool_tip_attached_jq "
								data-tooltip="Count of Monolinks Residues" >
							M Residues
						</th>
						<th style="width:6%;font-weight:bold;" class=" tool_tip_attached_jq "
								data-tooltip="Fraction of Linkable Residues covered by Monolinks" >
							M Coverage
						</th>

						<th style="width:6%;font-weight:bold;" class=" tool_tip_attached_jq "
								data-tooltip="Count of Looplinks Residues" >
							L Residues
						</th>
						<th style="width:6%;font-weight:bold;" class=" tool_tip_attached_jq "
								data-tooltip="Fraction of Linkable Residues covered by Looplinks" >
							L Coverage
						</th>

						<th style="width:6%;font-weight:bold;" class=" tool_tip_attached_jq "
								data-tooltip="Count of Crosslinks Residues" >
							C Residues
						</th>
						<th style="width:6%;font-weight:bold;" class=" tool_tip_attached_jq "
								data-tooltip="Fraction of Linkable Residues covered by Crosslinks" >
							C Coverage
						</th>
						
					</tr>
					</thead>
					<tbody>
					</tbody>
						
				</table>
					
			<style>
			
			   .header-def-label { padding-right: 5px; }
			   .header-def-description { padding-left: 5px; }
			</style>
					
					
					<table >
					
						<tr>
							<td class="header-def-label" style="padding-bottom: 5px;">
								Column Header
							</td>
							<td class="header-def-description" style="padding-bottom: 5px;">
								Column Description
							</td>
						</tr>	
					
						<tr>
							<td class="header-def-label">
								Protein
							</td>
						</tr>				
						<tr>
							<td class="header-def-label">
								Residues
							</td>
							<td class="header-def-description">
									Count of Residues
							</td>
						</tr>				
						<tr>
							<td class="header-def-label">
								Sequence Coverage
							</td>
							<td class="header-def-description">
									Sequence Coverage
							</td>
						</tr>				
						<tr>
							<td class="header-def-label">
								Linkable Residues
							</td>
							<td class="header-def-description">
									Count of Linkable Residues
							</td>
						</tr>			
				
						<tr>
							<td class="header-def-label">
								Linkable Residues Covered
							</td>
							<td class="header-def-description">
									Count of Linkable Residues covered by Sequence Coverage
							</td>
						</tr>	
						<tr>
							<td class="header-def-label">
								Linkable Residues Coverage
							</td>
							<td class="header-def-description">
									Fraction of Linkable Residues covered by Sequence Coverage
							</td>
						</tr>				
						<tr>
	
							<td class="header-def-label">
								M+L+C Residues
							</td>
							<td class="header-def-description">
									Count of Monolinks, Looplinks and Crosslinks Residues
							</td>
						</tr>				
						<tr>
							<td class="header-def-label">
								M+L+C Coverage
							</td>
							<td class="header-def-description">
									Fraction of Linkable Residues covered by Monolinks, Looplinks and Crosslinks   
							</td>
						</tr>				
						<tr>
	
							<td class="header-def-label">
								L+C Residues
							</td>
							<td class="header-def-description">
									Count of Looplinks and Crosslinks Residues
							</td>
						</tr>				
						<tr>
							<td class="header-def-label">
								L+C Coverage	
							</td>					
							<td class="header-def-description">
									Fraction of Linkable Residues covered by Looplinks and Crosslinks
							</td>
						</tr>				
						<tr>
	
							<td class="header-def-label">
								M Residues
							</td>
							<td class="header-def-description">
									Count of Monolinks Residues
							</td>
						</tr>				
						<tr>
							<td class="header-def-label">
								M Coverage
							</td>
							<td class="header-def-description">
									Fraction of Linkable Residues covered by Monolinks 
							</td>
						</tr>				
						<tr>
	
							<td class="header-def-label">
								L Residues
							</td>
							<td class="header-def-description">
									Count of Looplinks Residues
							</td>
						</tr>				
						<tr>
							<td class="header-def-label">
								L Coverage
							</td>
							<td class="header-def-description">
									Fraction of Linkable Residues covered by Looplinks
							</td>
						</tr>				
						<tr>
	
							<td class="header-def-label">
								C Residues
							</td>
							<td class="header-def-description">
									Count of Crosslinks Residues
							</td>
						</tr>				
						<tr>
							<td class="header-def-label">
								C Coverage
							</td>
							<td class="header-def-description">
									Fraction of Linkable Residues covered by Crosslinks
							</td>
							
						</tr>				
					</table>
	
			</div>
						
		</div>
		
		<script type="text/javascript" src="static/js_generated_bundles/data_pages/proteinCoverageReportView-bundle.js?x=${cacheBustValue}"></script>
	

<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>
