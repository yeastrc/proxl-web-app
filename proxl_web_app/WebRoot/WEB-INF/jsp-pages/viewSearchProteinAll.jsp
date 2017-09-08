<%@page import="org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants"%>
<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

 <c:set var="pageTitle">Proteins - <c:out value="${ headerProject.projectTblData.title }"></c:out></c:set>

 <c:set var="pageBodyClass" >project-page</c:set>

 <c:set var="helpURLExtensionForSpecificPage" >en/latest/using/protein.html</c:set>

 <c:set var="headerAdditions">

		<script type="text/javascript" src="${ contextPath }/js/handleServicesAJAXErrors.js?x=${cacheBustValue}"></script> 

		<script type="text/javascript" src="${ contextPath }/js/libs/jquery.tablesorter_Modified.js"></script> 
		<script type="text/javascript" src="${ contextPath }/js/libs/jquery.qtip.min.js"></script>
				
		
<%--  Start of Lorikeet Core Parts --%>		

		<script src="${contextPath}/js/libs/jquery-ui-1.10.4.min.js"></script>
		
		<%--  Only load the excanvas.min.js if it is IE 8 or lower.  IE 8 does not support HTML5 so this is a way to have HTML5 canvas support --%>
		<!--[if lte IE 8]><script language="javascript" type="text/javascript" src="${contextPath}/js/lorikeet_google_code/excanvas.min.js"></script><![endif]-->
		
		<script src="${contextPath}/js/lorikeet/jquery.flot.js"></script>
		<script src="${contextPath}/js/lorikeet/jquery.flot.selection.js"></script>
		
		<script src="${contextPath}/js/lorikeet/specview.js?x=${cacheBustValue}"></script>
		<script src="${contextPath}/js/lorikeet/peptide.js?x=${cacheBustValue}"></script>
		<script src="${contextPath}/js/lorikeet/aminoacid.js?x=${cacheBustValue}"></script>
		<script src="${contextPath}/js/lorikeet/ion.js?x=${cacheBustValue}"></script>		
		
<%--  End of Lorikeet Core Parts --%>		

		
		
		<!-- Handlebars templating library   -->
		
		<%--  
		<script type="text/javascript" src="${ contextPath }/js/libs/handlebars-v2.0.0.js"></script>
		--%>
		
		<!-- use minimized version  -->
		<script type="text/javascript" src="${ contextPath }/js/libs/handlebars-v2.0.0.min.js"></script>

		
				
		
		<script type="text/javascript" src="${ contextPath }/js/libs/snap.svg-min.js"></script> <%--  Used by lorikeetPageProcessing.js --%>

		<script type="text/javascript" src="${ contextPath }/js/lorikeetPageProcessing.js?x=${cacheBustValue}"></script>
		
		<script type="text/javascript" src="${ contextPath }/js/defaultPageView.js?x=${cacheBustValue}"></script>
		<script type="text/javascript" src="${ contextPath }/js/sharePageURLShortener.js?x=${cacheBustValue}"></script>
		
				
				<%-- 
					The Struts Action for this page must call GetProteinNamesTooltipConfigData
					This include is required on this page:
					/WEB-INF/jsp-includes/proteinNameTooltipDataForJSCode.jsp
				  --%>
		<script type="text/javascript" src="${ contextPath }/js/createTooltipForProteinNames.js?x=${cacheBustValue}"></script>
				
		<script type="text/javascript" src="${ contextPath }/js/toggleVisibility.js?x=${cacheBustValue}"></script>
		
		<script type="text/javascript" src="${ contextPath }/js/viewPsmsLoadedFromWebServiceTemplate.js?x=${cacheBustValue}"></script>
		<script type="text/javascript" src="${ contextPath }/js/viewReportedPeptidesForProteinAllLoadedFromWebServiceTemplate.js?x=${cacheBustValue}"></script>
			
		<script type="text/javascript" src="${ contextPath }/js/psmPeptideCutoffsCommon.js?x=${cacheBustValue}"></script>
		<script type="text/javascript" src="${ contextPath }/js/psmPeptideAnnDisplayDataCommon.js?x=${cacheBustValue}"></script>
		
		<script type="text/javascript" src="${ contextPath }/js/viewProteinPageCommonCrosslinkLooplinkCoverageSearchMerged.js?x=${cacheBustValue}"></script>

		<script type="text/javascript" src="${ contextPath }/js/webserviceDataParamsDistribution.js?x=${cacheBustValue}"></script>
				
		<script type="text/javascript" src="${ contextPath }/js/viewSearchProteinAllPage.js?x=${cacheBustValue}"></script>

	
		<link rel="stylesheet" href="${ contextPath }/css/tablesorter.css" type="text/css" media="print, projection, screen" />
		<link type="text/css" rel="stylesheet" href="${ contextPath }/css/jquery.qtip.min.css" />

		<%--  some classes in this stylesheet collide with some in the lorikeet file since they are set to specific values for lorikeet drag and drop --%>
		<%-- 
		<link REL="stylesheet" TYPE="text/css" HREF="${contextPath}/css/jquery-ui-1.10.2-Themes/ui-lightness/jquery-ui.min.css">
		--%>
		<link REL="stylesheet" TYPE="text/css" HREF="${contextPath}/css/lorikeet.css">



</c:set>



<%@ include file="/WEB-INF/jsp-includes/header_main.jsp" %>
		

	<input type="hidden" id="project_id" value="<c:out value="${ project_id }"></c:out>"> 
	
	<%--  Put Project_Search_Id on the page for the JS code --%>
	<input type="hidden" class=" project_search_id_jq " value="<c:out value="${ projectSearchId }"></c:out>">
		
		
	<%--  used by createTooltipForProteinNames.js --%>
	<%@ include file="/WEB-INF/jsp-includes/proteinNameTooltipDataForJSCode.jsp" %>
	
		<%@ include file="/WEB-INF/jsp-includes/defaultPageViewFragment.jsp" %>
		
		<%@ include file="/WEB-INF/jsp-includes/viewPsmsLoadedFromWebServiceTemplateFragment.jsp" %>
		
		<%@ include file="/WEB-INF/jsp-includes/viewReportedPeptidesForProteinAllLoadedFromWebServiceTemplateFragment.jsp" %>
		
		
			<%@ include file="/WEB-INF/jsp-includes/lorikeet_overlay_section.jsp" %>	
		
		<div class="overall-enclosing-block">
	
			<h2 style="margin-bottom:5px;">List search proteins:</h2>
	
			<div  class=" navigation-links-block "> 

				<%-- Navigation link to QC Page --%>
				
				<%@ include file="/WEB-INF/jsp-includes/qc_NavLinks.jsp" %>
				
				[<a class="tool_tip_attached_jq" data-tooltip="View peptides" 
					href="${ contextPath }/<proxl:defaultPageUrl pageName="/peptide" projectSearchId="${ search.projectSearchId }"
						>peptide.do?projectSearchId=<bean:write name="search" property="projectSearchId" 
						/>&queryJSON=<c:out value="${ peptidePageQueryJSON }" escapeXml="false" 
						></c:out></proxl:defaultPageUrl>"
						>Peptide View</a>]
						 
				[<a class="tool_tip_attached_jq" data-tooltip="View protein coverage report" 
					href="${ contextPath }/<proxl:defaultPageUrl pageName="/proteinCoverageReport" projectSearchId="${ search.projectSearchId }">proteinCoverageReport.do?<bean:write name="queryString" /></proxl:defaultPageUrl>"
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
			<html:form action="allProtein" method="get" styleId="form_get_for_updated_parameters_single_search" >
						
				<input type="hidden" name="projectSearchId" class=" project_search_id_in_update_form_jq " 
					value="${ search.projectSearchId }">
				<%-- cannot use <html:hidden property="projectSearchId" /> since projectSearchId is an array --%>
				
				<input type="hidden" name="queryJSON" id="query_json_field" value="<c:out value="${ queryJSONToForm }" ></c:out>"  />
				
				<%--  A block in the submitted form for PSM Peptide cutoff JS code --%> <%--  Currently empty --%>
				<%@ include file="/WEB-INF/jsp-includes/psmPeptideCutoffBlock_inSubmitForm.jsp" %>

			</html:form>			
				
			<html:form action="mergedAllProtein" method="get" styleId="form_get_for_updated_parameters_multiple_searches" >
						
				<input type="hidden" name="queryJSON" value="<c:out value="${ queryJSONToForm }" ></c:out>"  />
				
				<%--  A block in the submitted form for PSM Peptide cutoff JS code --%>
				<%@ include file="/WEB-INF/jsp-includes/psmPeptideCutoffBlock_inSubmitForm.jsp" %>

			</html:form>
				
			
			
<%--
		Moved JS call to the "Update" button
		 						
			<form action="javascript:viewSearchProteinAllPageCode.updatePageForFormParams()" method="get" > 
			
				--%>	 <%-- id="form_get_for_updated_parameters" --%>
			
			
			
			<table style="border-width:0px;">
					
				<%--  Set to false to not show color block before search for key --%>
				<c:set var="showSearchColorBlock" value="${ false }" />
				
				<%--  Include file is dependent on containing loop having varStatus="searchVarStatus"  --%>
				<%@ include file="/WEB-INF/jsp-includes/searchDetailsBlock.jsp" %>

				<tr>
					<td>Type Filter:</td>
					<td colspan="2">
						<%--  Update TestAllWebLinkTypesSelected if add another option --%>

					  <label >
						<input type="checkbox" class=" link_type_jq " 
							onchange="defaultPageView.searchFormChanged_ForDefaultPageView();"
							value="<%= PeptideViewLinkTypesConstants.CROSSLINK_PSM %>"   >
						crosslinks
					  </label>
					  <label >
						<input type="checkbox" class=" link_type_jq " 
							onchange="defaultPageView.searchFormChanged_ForDefaultPageView();"
							value="<%= PeptideViewLinkTypesConstants.LOOPLINK_PSM %>" >
						looplinks
					  </label> 
					  <label >
						<input type="checkbox" class=" link_type_jq " 
							onchange="defaultPageView.searchFormChanged_ForDefaultPageView();"
							value="<%= PeptideViewLinkTypesConstants.UNLINKED_PSM %>" >
						 unlinked
					  </label>

					</td>
				</tr>
				<tr>
					<td>Exclude links with:</td>
					<td>
						 <%--  Checkboxes --%>
						<%@ include file="/WEB-INF/jsp-includes/excludeLinksWith_Checkboxes_ProteinPages_Fragment.jsp" %>
					</td>
				</tr>

				<tr>
					<td>Exclude organisms:</td>
					<td>
						<logic:iterate id="taxonomy" name="taxonomies">
						 <label style="white-space: nowrap" >
						  <input type="checkbox" name="excludeTaxonomy" value="<bean:write name="taxonomy" property="key"/>" class=" excludeTaxonomy_jq " onchange=" defaultPageView.searchFormChanged_ForDefaultPageView();" >  
						  
						   <span style="font-style:italic;"><bean:write name="taxonomy" property="value"/></span>
						 </label> 						 
						</logic:iterate>				
					</td>
				</tr>

				<tr>
					<td>Exclude protein(s):</td>
					<td>
						<%--   
						All <option> values must be parsable as integers:
						--%>
						<select name="excludedProteins" multiple="multiple" id="excludeProtein" onchange=" defaultPageView.searchFormChanged_ForDefaultPageView();" >  
						  
	  						<logic:iterate id="protein" name="proteins">
	  						  <option value="<c:out value="${ protein.proteinSequenceObject.proteinSequenceId }"></c:out>"><c:out value="${ protein.name }"></c:out></option>
	  						</logic:iterate>
	  					</select>
					</td>
				</tr>
				
				<tr>
					<td>&nbsp;</td>
					<td>
						<%@ include file="/WEB-INF/jsp-includes/sharePageURLShortenerOverlayFragment.jsp" %>
					
						<c:set var="UpdateButtonText" value="Update"/>
						
						<input type="button" value="${ UpdateButtonText }"  onclick="viewSearchProteinAllPageCode.updatePageForFormParams()" >
						
						<c:set var="projectSearchId" value="${ search.projectSearchId }"/>	

						<c:set var="page_JS_Object" value="viewSearchProteinPageCommonCrosslinkLooplinkCoverage"/>
						
						<%@ include file="/WEB-INF/jsp-includes/defaultPageViewButtonFragment.jsp" %>
						<%@ include file="/WEB-INF/jsp-includes/sharePageURLShortenerButtonFragment.jsp" %>

					</td>
				</tr>
			</table>
			
			<div style="height: 10px;">&nbsp;</div>
						
			<h3 style="display:inline;">Proteins (<bean:write name="numProteins" />):</h3>
			<div style="display:inline;">
				[<a class="tool_tip_attached_jq" data-tooltip="View crosslinks" 
						href="${ contextPath }/<proxl:defaultPageUrl pageName="/crosslinkProtein" projectSearchId="${ search.projectSearchId }">crosslinkProtein.do?<bean:write name="queryString" /></proxl:defaultPageUrl>"
						>View Crosslinks</a>]
				[<a class="tool_tip_attached_jq" data-tooltip="View looplinks" 
						href="${ contextPath }/<proxl:defaultPageUrl pageName="/looplinkProtein" projectSearchId="${ search.projectSearchId }">looplinkProtein.do?<bean:write name="queryString" /></proxl:defaultPageUrl>"
						>View Looplinks</a>]
				[<a class="tool_tip_attached_jq" data-tooltip="Download all proteins as tab-delimited text" 
					href="${ contextPath }/downloadMergedProteinsAll.do?<bean:write name="queryString" />"
					>Download Data (<bean:write name="numProteins" />)</a>]
			</div>
			
			<%--  Block for user choosing which annotation types to display  --%>
			<%@ include file="/WEB-INF/jsp-includes/annotationDisplayManagementBlock.jsp" %>


			<%--  Create via javascript the parts that will be above the main table --%>
			<script type="text/javascript">
				
				//  If object exists, call function on it now, otherwise call the function on document ready
				if ( window.viewSearchProteinAllPageCode ) {
					window.viewSearchProteinAllPageCode.createPartsAboveMainTable();
				} else {
	
					$(document).ready(function() 
					    { 
						   setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else
							  
							   window.viewSearchProteinAllPageCode.createPartsAboveMainTable();
						   },10);
					    } 
					); // end $(document).ready(function() 
				}
							
			</script>
			

			
				<table style="" id="main_page_data_table" class="tablesorter top_data_table_jq ">
				
					<thead>
					<tr>
						<th data-tooltip="Name of protein" class="tool_tip_attached_jq" style="text-align:left;width:10%;font-weight:bold;">Protein</th>
						<th data-tooltip="Number of peptide spectrum matches showing this link" class="tool_tip_attached_jq integer-number-column-header" style="width:10%;font-weight:bold;">PSMs</th>
						<th data-tooltip="Number of distinct pairs of peptides showing link" class="tool_tip_attached_jq integer-number-column-header" style="width:10%;font-weight:bold;">#&nbsp;Peptides</th>
						<th data-tooltip="Number of found peptide pairs that uniquely map to these two proteins from the FASTA file" class="tool_tip_attached_jq integer-number-column-header" style="width:10%;font-weight:bold;">#&nbsp;Unique Peptides</th>
						

						<c:forEach var="peptideAnnotationDisplayNameDescription" items="${ peptideAnnotationDisplayNameDescriptionList }">

								<%--  Consider displaying the description somewhere   peptideAnnotationDisplayNameDescription.description --%>
							<th data-tooltip="Best Peptide-level <c:out value="${ peptideAnnotationDisplayNameDescription.displayName }"></c:out> for this peptide (or linked pair)" 
									class="tool_tip_attached_jq" 
									style="width:10%;font-weight:bold;">
								<span style="white-space: nowrap">Best Peptide</span>
								<span style="white-space: nowrap"><c:out value="${ peptideAnnotationDisplayNameDescription.displayName }"></c:out></span>
							</th>
							
						</c:forEach>
										

						<c:forEach var="psmAnnotationDisplayNameDescription" items="${ psmAnnotationDisplayNameDescriptionList }">

								<%--  Consider displaying the description somewhere   psmAnnotationDisplayNameDescription.description --%>
						  <th data-tooltip="Best PSM-level <c:out value="${ psmAnnotationDisplayNameDescription.displayName }"></c:out> for PSMs matched to peptides that show this link" class="tool_tip_attached_jq" style="width:10%;font-weight:bold;"
							><span style="white-space: nowrap">Best PSM</span> 
								<span style="white-space: nowrap"><c:out value="${ psmAnnotationDisplayNameDescription.displayName }"></c:out></span></th>

						</c:forEach>
							
					</tr>
					</thead>
						
					<logic:iterate id="proteinMain" name="proteinsMainList">
							<tr id="${ proteinMain.proteinSequenceId }"
								style="cursor: pointer; "
								
								onclick="viewReportedPeptidesForProteinAllLoadedFromWebServiceTemplate.showHideReportedPeptides( { clickedElement : this })"
								data-project_search_id="${ search.projectSearchId }"
								data-protein_id="${ proteinMain.proteinSequenceId }"
							>
								<td><span class="proteinName" id="protein-id-<bean:write name="proteinMain" property="searchProtein.proteinSequenceObject.proteinSequenceId" />"
									><bean:write name="proteinMain" property="searchProtein.name" /></span></td>

								<td class="integer-number-column"><bean:write name="proteinMain" property="numPsms" /></td>
								
								<td class="integer-number-column"><a class="show-child-data-link   " 
										href="javascript:"
										><bean:write name="proteinMain" property="numPeptides" 
											/><span class="toggle_visibility_expansion_span_jq" 
												><img src="${contextPath}/images/icon-expand-small.png" 
													class=" icon-expand-contract-in-data-table "
													></span><span class="toggle_visibility_contraction_span_jq" 
														style="display: none;" 
														><img src="${contextPath}/images/icon-collapse-small.png"
															class=" icon-expand-contract-in-data-table "
															></span>
									</a>
								</td>								
								
								<td class="integer-number-column"><bean:write name="proteinMain" property="numUniquePeptides" /></td>
								
						
								<c:forEach var="annotationValue" items="${ proteinMain.peptideAnnotationValueList }">
			
									<td style="white-space: nowrap"><c:out  value="${ annotationValue }" /></td>
								</c:forEach>	

								<c:forEach var="annotationValue" items="${ proteinMain.psmAnnotationValueList }">
			
									<td><c:out  value="${ annotationValue }" /></td>
								</c:forEach>															
								
							</tr>
							
							<tr class="expand-child" style="display:none;">
							
	
									<%--  Adjust colspan for number of columns in current table --%>
									
								<%--  Add to value for length of Peptide and PSM value lists --%>
								<c:set var="columnsAddedForAnnotationData" 
									value="${ fn:length( proteinMain.peptideAnnotationValueList ) + fn:length( proteinMain.psmAnnotationValueList ) }" />
																								
															
								<td colspan="${ 8 + columnsAddedForAnnotationData }" align="center" class=" child_data_container_jq ">
								
									<div style="color: green; font-size: 16px; padding-top: 10px; padding-bottom: 10px;" >
										Loading...
									</div>
								</td>
							</tr>

						
					</logic:iterate>
				</table>

		</div>
	

<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>
