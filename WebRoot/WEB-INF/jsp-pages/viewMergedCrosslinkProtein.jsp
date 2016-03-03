<%@page import="org.yeastrc.xlink.www.webapp_timing.WebappTiming"%>
<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

 <c:set var="pageTitle">View Search</c:set>

 <c:set var="pageBodyClass" >project-page</c:set>

 <c:set var="headerAdditions">
 
		<script type="text/javascript" src="${ contextPath }/js/handleServicesAJAXErrors.js"></script> 
 
 		<script type="text/javascript" src="${ contextPath }/js/libs/jquery.tablesorter.min.js"></script> 
		
		<script type="text/javascript" src="${ contextPath }/js/libs/d3.min.js"></script>
		<script type="text/javascript" src="${ contextPath }/js/libs/venn.js"></script>
	
		
<%--  Start of Lorikeet Core Parts --%>		

		<script src="${contextPath}/js/libs/jquery-ui-1.10.4.min.js"></script>
		
		<%--  Only load the excanvas.min.js if it is IE 8 or lower.  IE 8 does not support HTML5 so this is a way to have HTML5 canvas support --%>
		<!--[if lte IE 8]><script language="javascript" type="text/javascript" src="${contextPath}/js/lorikeet_google_code/excanvas.min.js"></script><![endif]-->
		
		<script src="${contextPath}/js/lorikeet/jquery.flot.js"></script>
		<script src="${contextPath}/js/lorikeet/jquery.flot.selection.js"></script>
		
		<script src="${contextPath}/js/lorikeet/specview.js"></script>
		<script src="${contextPath}/js/lorikeet/peptide.js"></script>
		<script src="${contextPath}/js/lorikeet/aminoacid.js"></script>
		<script src="${contextPath}/js/lorikeet/ion.js"></script>		
		
<%--  End of Lorikeet Core Parts --%>		
			
			
						
		<!-- Handlebars templating library   -->
		
		<%--  
		<script type="text/javascript" src="${ contextPath }/js/libs/handlebars-v2.0.0.js"></script>
		--%>
		
		<!-- use minimized version  -->
		<script type="text/javascript" src="${ contextPath }/js/libs/handlebars-v2.0.0.min.js"></script>

				
		<script type="text/javascript" src="${ contextPath }/js/libs/snap.svg-min.js"></script> <%--  Used by lorikeetPageProcessing.js --%>
				
		<script type="text/javascript" src="${ contextPath }/js/lorikeetPageProcessing.js"></script>
				
				<%-- 
					The Struts Action for this page must call GetProteinNamesTooltipConfigData
					This input is required on this page:
					<input type="hidden" id="protein_listing_webservice_base_url" value="<c:out value="${ protein_listing_webservice_base_url }"></c:out>">
				  --%>
		<script type="text/javascript" src="${ contextPath }/js/createTooltipForProteinNames.js"></script>
		
		<script type="text/javascript" src="${ contextPath }/js/toggleVisibility.js"></script>
					
					
		<script type="text/javascript" src="${ contextPath }/js/viewCrosslinkProteinsLoadedFromWebServiceTemplate.js"></script>
					
		<script type="text/javascript" src="${ contextPath }/js/viewPsmsLoadedFromWebServiceTemplate.js"></script>
		<script type="text/javascript" src="${ contextPath }/js/viewCrosslinkReportedPeptidesLoadedFromWebServiceTemplate.js"></script>

		<script type="text/javascript" src="${ contextPath }/js/psmPeptideCutoffsCommon.js"></script>
		
		<script type="text/javascript" src="${ contextPath }/js/viewProteinPageCommonCrosslinkLooplinkCoverageSearchMerged.js"></script>
		
		<script type="text/javascript" src="${ contextPath }/js/viewMergedCrosslinkProteinPage.js"></script>
		
		<script type="text/javascript" src="${ contextPath }/js/mergedSearchesVennDiagramCreator.js"></script>

		<link rel="stylesheet" href="${ contextPath }/css/tablesorter.css" type="text/css" media="print, projection, screen" />
		<link type="text/css" rel="stylesheet" href="${ contextPath }/css/jquery.qtip.min.css" />

		<%--  some classes in this stylesheet collide with some in the lorikeet file since they are set to specific values for lorikeet drag and drop --%>
		<%-- 
		<link REL="stylesheet" TYPE="text/css" HREF="${contextPath}/css/jquery-ui-1.10.2-Themes/ui-lightness/jquery-ui.min.css">
		--%>

		<link REL="stylesheet" TYPE="text/css" HREF="${contextPath}/css/lorikeet.css">
		
	<script>



		<c:if test="${ not empty vennDiagramDataToJSON }">
 				
			//  function called to put Venn diagram of proteins onto the page
		
			function createMergedSearchesLinkCountsVennDiagram_PageFunction() {

				//  This data is used in the script mergedSearchesVennDiagramCreator.js
				var searchesLinkCountsVennDiagramData = <c:out value="${ vennDiagramDataToJSON }" escapeXml="false"></c:out>;
	
		 		
		 		createMergedSearchesLinkCountsVennDiagram( searchesLinkCountsVennDiagramData );
			}
		 	
	 	</c:if>
	 	
		
		

	</script>


</c:set>



<%@ include file="/WEB-INF/jsp-includes/header_main.jsp" %>

	<%--  protein name data webservice base URL, used by createTooltipForProteinNames.js --%>
	<input type="hidden" id="protein_listing_webservice_base_url" value="<c:out value="${ protein_listing_webservice_base_url }"></c:out>">

		<%@ include file="/WEB-INF/jsp-includes/viewPsmsLoadedFromWebServiceTemplateFragment.jsp" %>
		<%@ include file="/WEB-INF/jsp-includes/viewCrosslinkReportedPeptidesLoadedFromWebServiceTemplateFragment.jsp" %>
		

			<%@ include file="/WEB-INF/jsp-includes/lorikeet_overlay_section.jsp" %>	
		
		
	
	<%--  Crosslink Protein Template --%>


		
		<script id="crosslink_protein_block_template"  type="text/x-handlebars-template">

			<%--  include the template text  --%>
			<%@ include file="/WEB-INF/jsp_template_fragments/For_jsp_pages/viewMergedCrosslinkProtein.jsp_templates/crosslink_protein_block_template.jsp" %>

		</script>
	

	<%--  Crosslink Protein Entry Template --%>



		<%-- !!!   Handlebars template:  Crosslink Protein Entry Template  !!!!!!!!!   --%>
		
		
		<script id="crosslink_protein_data_row_entry_template"  type="text/x-handlebars-template">

			<%--  include the template text  --%>
			<%@ include file="/WEB-INF/jsp_template_fragments/For_jsp_pages/viewMergedCrosslinkProtein.jsp_templates/crosslink_protein_data_row_entry_template.jsp" %>

		</script>



	<%--  Crosslink Protein Child row Entry Template --%>

		
		<script id="crosslink_protein_child_row_entry_template"  type="text/x-handlebars-template">

			<%--  include the template text  --%>
			<%@ include file="/WEB-INF/jsp_template_fragments/For_jsp_pages/viewMergedCrosslinkProtein.jsp_templates/crosslink_protein_child_row_entry_template.jsp" %>

		</script>

	
		
		<div class="overall-enclosing-block">
	
			<h2 style="margin-bottom:5px;">List merged search proteins:</h2>
	
			<div style="margin-bottom:20px;">
			
				[<a class="tool_tip_attached_jq" data-tooltip="View peptides" 
						href="${ contextPath }/mergedPeptide.do?<bean:write name="queryString" />">Peptide View</a>]
			
				[<a class="tool_tip_attached_jq" data-tooltip="View protein coverage report" 
						href="${ contextPath }/mergedProteinCoverageReport.do?<bean:write name="queryString" />">Coverage Report</a>]


				<%-- Navigation links to Merged Image and Merged Structure --%>
				
				<%@ include file="/WEB-INF/jsp-includes/imageAndStructureNavLinks.jsp" %>

		
			</div>
	
			<%-- query JSON in field outside of form for input to Javascript --%>
				
			<input type="hidden" id="query_json_field_outside_form" value="<c:out value="${ queryJSONToForm }" ></c:out>" > 

				<%--  A block outside any form for PSM Peptide cutoff JS code --%>
				<%@ include file="/WEB-INF/jsp-includes/psmPeptideCutoffBlock_outsideAnyForm.jsp" %>
				
	
			<html:form action="mergedCrosslinkProtein" method="get" styleId="form_get_for_updated_parameters" >
			
						
						<logic:iterate name="searches" id="search">
							<input type="hidden" name="searchIds" value="<bean:write name="search" property="id" />">
						</logic:iterate>

				<input type="hidden" name="queryJSON" id="query_json_field" />
				
				<%--  A block in the submitted form for PSM Peptide cutoff JS code --%>
				<%@ include file="/WEB-INF/jsp-includes/psmPeptideCutoffBlock_inSubmitForm.jsp" %>

			</html:form>
			
<%--
		Moved JS call to the "Update" button
		 	
			<form action="javascript:viewMergedCrosslinkProteinPageCode.updatePageForFormParams()" method="get" >
--%>					
												
			<table style="border-width:0px;">

				<%--  Set to true to show color block before search for key --%>
				<c:set var="showSearchColorBlock" value="${ true }" />
				
				<%--  Include file is dependent on containing loop having varStatus="searchVarStatus"  --%>
				<%@ include file="/WEB-INF/jsp-includes/searchDetailsBlock.jsp" %>

				
				<tr>
					<td>Exclude links with:</td>
					<td>
						 <label><span style="white-space:nowrap;" >
							<input type="checkbox" id="filterNonUniquePeptides" > 					
						 	 no unique peptides
						 </span></label>
						 <label><span style="white-space:nowrap;" >
							<input type="checkbox" id="filterOnlyOnePSM" > 					
						 	 only one PSM
						 </span></label>
						 <label><span style="white-space:nowrap;" >
							<input type="checkbox" id="filterOnlyOnePeptide" > 					
						 	 only one peptide
						 </span></label>
					</td>
				</tr>

				<tr>
					<td>Exclude organisms:</td>
					<td>
						<logic:iterate id="taxonomy" name="taxonomies">
						
<%-- 						
						 <label style="white-space: nowrap" >
						  <html:multibox property="excludeTaxonomy" styleClass="excludeTaxonomy_jq"  >
						   <bean:write name="taxonomy" property="key"/> 
						  </html:multibox> 
						   <span style="font-style:italic;"><bean:write name="taxonomy" property="value"/></span>
						 </label> 
--%>						 
						 <label style="white-space: nowrap" >
						  <input type="checkbox" name="excludeTaxonomy" value="<bean:write name="taxonomy" property="key"/>" 
						  		class=" excludeTaxonomy_jq "  >  
						  
						   <span style="font-style:italic;"><bean:write name="taxonomy" property="value"/></span>
						 </label> 						 
						</logic:iterate>				
					</td>
				</tr>

				<tr>
					<td>Exclude protein(s):</td>
					<td>
						<%--  shortened property from "excludeProtein" to "excP" to shorten the URL  --%>
						<%-- TODO   TEMP
						<html:select property="excP" multiple="true" styleId="excludeProtein" >
							<html:options collection="proteins" property="nrProtein.nrseqId" labelProperty="name" />
						</html:select>
						--%>
						
						
						<%--  New version:  Commented out since not getting the list of proteins in the action yet 
						
						All <option> values must be parsable as integers:
						--%>
						<select name="excludedProteins" multiple="multiple" id="excludeProtein" >  
						  
	  						<logic:iterate id="protein" name="allProteinsForCrosslinksAndLooplinksUnfilteredList">
	  						  <option value="<c:out value="${ protein.nrProtein.nrseqId }"></c:out>"><c:out value="${ protein.name }"></c:out></option>
	  						</logic:iterate>
	  					</select>
									
						<%--  TEMP for testing
						<select name="excludedProteins" multiple="multiple" id="excludeProtein"> 
							<option value="1" >TEMP_1</option>
							<option value="2" >TEMP_2</option>
						</select>
						--%>
					</td>
				</tr>
				
				<tr>
					<td>&nbsp;</td>
					<td>

<%--   WAS 						
						<input type="submit" value="Update" >
--%>						
						<input type="button" value="Update"  onclick="viewMergedCrosslinkProteinPageCode.updatePageForFormParams()" >
											
					</td>
				</tr>
			
			</table>
			
<%-- 			
			</form>
--%>
			
			
		<div>
			<h3 style="display:inline;">Merged Crosslinks: <bean:write name="numCrosslinks" />
			</h3>

			<div style="display:inline;">
				[<a class="tool_tip_attached_jq" data-tooltip="View looplinks (instead of crosslinks)" href="${ contextPath }/mergedLooplinkProtein.do?<bean:write name="queryString" />">View Looplinks (<bean:write name="numLooplinks" />)</a>]
				[<a class="tool_tip_attached_jq" data-tooltip="Download all crosslinks as tab-delimited text" href="${ contextPath }/downloadMergedProteins.do?<bean:write name="queryString" />">Download Data (<bean:write name="numLinks" />)</a>]
				[<a class="tool_tip_attached_jq" data-tooltip="Download all distinct UDRs (crosslinks and looplinks) as tab-delimited text" href="${ contextPath }/downloadMergedProteinUDRs.do?<bean:write name="queryString" />">Download UDRs (<bean:write name="numDistinctLinks" />)</a>]
			</div>
		</div>

			
			<c:choose>
	 		 <c:when test="${ not empty vennDiagramDataToJSON }">

				<%-- Have Venn Diagram, list legend to the left in vertical list --%>

				<div style="float:left;">
				
	
				 <c:forEach items="${ searchCounts }" var="searchCount"  varStatus="searchVarStatus">
	
					<%--  Include file is dependent on containing loop having varStatus="searchVarStatus"  --%>
					<%@ include file="/WEB-INF/jsp-includes/mergedSearch_SearchIndexToSearchColorCSSClassName.jsp" %>
								
						
				  <div style="margin-top: 5px;">	
						<span style="margin-right: 10px; padding-left: 10px; padding-right: 10px;" class="${ backgroundColorClassName }"></span>
					  (Search <bean:write name="searchCount" property="searchId" />: <bean:write name="searchCount" property="count" />)
				  </div>
				 </c:forEach>
				</div>
	
				<div id="searches_intersection_venn_diagram" >
				
				
				</div>
				
				<div style="clear:both;"></div>
			 
			 </c:when>
			 <c:otherwise>

				<%-- No Venn Diagram, list legend in horizontal list list --%>
			 
			  <div style="margin-top: 5px;">	

				 <c:forEach items="${ searchCounts }" var="searchCount"  varStatus="searchVarStatus">
				 
				  <span style="white-space: nowrap;  padding-right: 20px;">
	
					<%--  Include file is dependent on containing loop having varStatus="searchVarStatus"  --%>
					<%@ include file="/WEB-INF/jsp-includes/mergedSearch_SearchIndexToSearchColorCSSClassName.jsp" %>
								
						
						<span style="margin-right: 10px; padding-left: 10px; padding-right: 10px;" class="${ backgroundColorClassName }"></span>
					  (Search <bean:write name="searchCount" property="searchId" />: <bean:write name="searchCount" property="count" />)
					  
				  </span>
				  
				 </c:forEach>

			  </div>
			 
			 </c:otherwise>
			</c:choose>
 			
			<div style="clear:both;"></div>

			<%--  Create via javascript the parts that will be above the main table --%>
			<script type="text/javascript">
			
			viewMergedCrosslinkProteinPageCode.createPartsAboveMainTable();
				
			</script>

				<table style="" id="main_page_data_table" class="tablesorter top_data_table_jq ">
				
					<thead>
					<tr>

						<c:forEach items="${ searches }" var="search"  varStatus="searchVarStatus">
		
							<th id="search_header_<bean:write name="search" property="id" />" style="text-align:left;font-weight:bold;width:25px;"
								><bean:write name="search" property="id" /></th>
								
							<script >
								$("#search_header_<bean:write name="search" property="id" />").qtip( {
							        content: {
							            text: '<bean:write name="search" property="name" />&nbsp;(<bean:write name="search" property="id" />)'
							        },
							        position: {
							            my: 'bottom left',
							            at: 'top center',
							            viewport: $(window)
							         }
							    });								
								
							</script>
								
						</c:forEach>

						<th data-tooltip="Number of selected searches containing link" class="tool_tip_attached_jq integer-number-column-header" style="left;width:45px;font-weight:bold;">Searches</th>
						<th data-tooltip="Name of first protein" class="tool_tip_attached_jq" style="text-align:left;font-weight:bold;">Protein 1</th>
						<th data-tooltip="Linked position in first protein" class="tool_tip_attached_jq integer-number-column-header" style="width:10%;font-weight:bold;">Position</th>
						<th data-tooltip="Name of second protein" class="tool_tip_attached_jq" style="text-align:left;font-weight:bold;">Protein 2</th>
						<th data-tooltip="Linked position in second protein" class="tool_tip_attached_jq integer-number-column-header" style="width:10%;font-weight:bold;">Position</th>
						<th data-tooltip="Number of peptide spectrum matches showing this link" class="tool_tip_attached_jq integer-number-column-header" style="width:10%;font-weight:bold;">PSMs</th>
						<th data-tooltip="Number of distinct pairs of peptides showing link" class="tool_tip_attached_jq integer-number-column-header" style="width:10%;font-weight:bold;">#&nbsp;Peptides</th>
						<th data-tooltip="Number of found peptide pairs that uniquely map to these two proteins from the FASTA file" class="tool_tip_attached_jq integer-number-column-header" style="width:10%;font-weight:bold;">#&nbsp;Unique Peptides</th>


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
						
					<logic:iterate id="crosslink" name="crosslinks">

						<%--  crosslink is a wrapped mergedSearchProteinCrosslink --%>
						
						<c:set var="proteinEntry" value="${ crosslink.mergedSearchProteinCrosslink }" />

							<tr 
								style="cursor: pointer; "
								
								onclick="viewCrosslinkProteinsLoadedFromWebServiceTemplate.showHideCrosslinkProteins( { clickedElement : this })"
								project_id="${ projectId }"
								search_ids="<c:forEach var="searchEntryForThisRow" items="${ proteinEntry.searches }">,${ searchEntryForThisRow.id }</c:forEach>"
								protein_1_id="<bean:write name="proteinEntry" property="protein1.nrProtein.nrseqId" />"
								protein_2_id="<bean:write name="proteinEntry" property="protein2.nrProtein.nrseqId" />"
								protein_1_position="<bean:write name="proteinEntry" property="protein1Position" />"
								protein_2_position="<bean:write name="proteinEntry" property="protein2Position" />"
							>
										

								<c:forEach items="${ crosslink.searchContainsCrosslink }" var="isMarked"  varStatus="searchVarStatus">
								
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
										><bean:write name="proteinEntry" property="numSearches" 
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
								
								<td><span class="proteinName" id="protein-id-<bean:write name="proteinEntry" property="protein1.nrProtein.nrseqId" />"><bean:write name="proteinEntry" property="protein1.name" /></span></td>
								<td class="integer-number-column"><bean:write name="proteinEntry" property="protein1Position" /></td>
								<td><span class="proteinName" id="protein-id-<bean:write name="proteinEntry" property="protein2.nrProtein.nrseqId" />"><bean:write name="proteinEntry" property="protein2.name" /></span></td>
								<td class="integer-number-column"><bean:write name="proteinEntry" property="protein2Position" /></td>
								<td class="integer-number-column"><bean:write name="proteinEntry" property="numPsms" /></td>
								<td class="integer-number-column"><bean:write name="proteinEntry" property="numLinkedPeptides" /></td>
								<td class="integer-number-column"><bean:write name="proteinEntry" property="numUniqueLinkedPeptides" /></td>
						


								
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
											items="${ proteinEntry.peptidePsmAnnotationValueListsForEachSearch }" 
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
								<td colspan="<c:out value="${ fn:length( searches ) + 9 + columnsAddedForAnnotationData }"
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