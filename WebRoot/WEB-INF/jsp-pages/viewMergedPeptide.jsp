<%@page import="org.yeastrc.xlink.www.webapp_timing.WebappTiming"%>
<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %>
<%@page import="org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants"%>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

 <c:set var="pageTitle">View Search</c:set>

 <c:set var="pageBodyClass" >project-page</c:set>

 <c:set var="headerAdditions">
 
		<script type="text/javascript" src="${ contextPath }/js/handleServicesAJAXErrors.js"></script> 
 
 		<script type="text/javascript" src="${ contextPath }/js/libs/jquery.tablesorter.min.js"></script> 
		<script type="text/javascript" src="${ contextPath }/js/libs/jquery.qtip.min.js"></script>
		
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
		
		<script type="text/javascript" src="${ contextPath }/js/nagWhenFormChangedButNotUpdated.js"></script>				
		
		
				<%-- 
					The Struts Action for this page must call GetProteinNamesTooltipConfigData
					This input is required on this page:
					<input type="hidden" id="protein_listing_webservice_base_url" value="<c:out value="${ protein_listing_webservice_base_url }"></c:out>">
				  --%>
		<script type="text/javascript" src="${ contextPath }/js/createTooltipForProteinNames.js"></script>

		<script type="text/javascript" src="${ contextPath }/js/toggleVisibility.js"></script>
		
				
		<script type="text/javascript" src="${ contextPath }/js/psmPeptideCutoffsCommon.js"></script>
		
		<script type="text/javascript" src="${ contextPath }/js/viewMergedPeptide.js"></script>
		
		<script type="text/javascript" src="${ contextPath }/js/viewMergedPeptidePerSearchData.js"></script>

		<script type="text/javascript" src="${ contextPath }/js/viewPsmsLoadedFromWebServiceTemplate.js"></script>
		
		<script type="text/javascript" src="${ contextPath }/js/mergedSearchesVennDiagramCreator.js"></script>

		
		
		<link rel="stylesheet" href="${ contextPath }/css/tablesorter.css" type="text/css" media="print, projection, screen" />
		<link type="text/css" rel="stylesheet" href="${ contextPath }/css/jquery.qtip.min.css" />

		<%--  some classes in this stylesheet collide with some in the lorikeet file since they are set to specific values for lorikeet drag and drop --%>
		<%-- 
		<link REL="stylesheet" TYPE="text/css" HREF="${contextPath}/css/jquery-ui-1.10.2-Themes/ui-lightness/jquery-ui.min.css">
		--%>

		<link REL="stylesheet" TYPE="text/css" HREF="${contextPath}/css/lorikeet.css">

	<script>


	function createVennDiagramIfNeeded() {
	
		<c:if test="${ not empty vennDiagramDataToJSON }">
	
			//  This data is used in the script mergedSearchesVennDiagramCreator.js
			var searchesLinkCountsVennDiagramData = <c:out value="${ vennDiagramDataToJSON }" escapeXml="false"></c:out>;
	
	 		
	 		createMergedSearchesLinkCountsVennDiagram( searchesLinkCountsVennDiagramData );
	 		
	 	</c:if>
	}

	</script>



</c:set>



<%@ include file="/WEB-INF/jsp-includes/header_main.jsp" %>

					<%--  protein name data webservice base URL, used by createTooltipForProteinNames.js --%>
	<input type="hidden" id="protein_listing_webservice_base_url" value="<c:out value="${ protein_listing_webservice_base_url }"></c:out>">

				
		<%@ include file="/WEB-INF/jsp-includes/viewPsmsLoadedFromWebServiceTemplateFragment.jsp" %>
		
		<%--  Put values on the page for the Javascript --%>
		
		<input type="hidden" id="project_id_for_js" value="${ projectId }" >
		
		<c:forEach var="searchId" items="${ searchIds }">
			<input type="hidden" class=" search_id_input_field_jq " value="<c:out value="${ searchId }"></c:out>" >
		</c:forEach>		

		
		<div class="overall-enclosing-block">
	
			<h2 style="margin-bottom:5px;">List merged search peptides:</h2>
	
			<div style="margin-bottom:20px;">
				[<a class="tool_tip_attached_jq" data-tooltip="View proteins" 
					href="${ contextPath }/mergedCrosslinkProtein.do?<bean:write name="queryString" />">Protein View</a>]
					
			
				[<a class="tool_tip_attached_jq" data-tooltip="View protein coverage report" 
						href="${ contextPath }/mergedProteinCoverageReport.do?<bean:write name="queryString" />">Coverage Report</a>]
				

				<%-- Navigation links to Merged Image and Merged Structure --%>
				
				<%@ include file="/WEB-INF/jsp-includes/imageAndStructureNavLinks.jsp" %>

								
			</div>
			

			<%-- query JSON in field outside of form for input to Javascript --%>
				
			<input type="hidden" id="query_json_field_outside_form" value="<c:out value="${ queryJSONToForm }" ></c:out>" > 
				
			<%--  A block outside any form for PSM Peptide cutoff JS code --%>
			<%@ include file="/WEB-INF/jsp-includes/psmPeptideCutoffBlock_outsideAnyForm.jsp" %>

	
			<html:form action="mergedPeptide" method="get" styleId="form_get_for_updated_parameters">
			
			
				<logic:iterate name="searches" id="search">
					<input type="hidden" name="searchIds" value="<bean:write name="search" property="id" />">
				</logic:iterate>
				
				
				<html:hidden property="queryJSON" styleId="query_json_field" />

				<%--  A block in the submitted form for PSM Peptide cutoff JS code --%>
				<%@ include file="/WEB-INF/jsp-includes/psmPeptideCutoffBlock_inSubmitForm.jsp" %>
			
			</html:form>
			
<%--
		Moved JS call to the "Update" button
		 			
			<form action="javascript:viewMergedPeptidePageCode.updatePageForFormParams()" method="get" >
						
--%>	
							
			<table style="border-width:0px;">

				<%--  Set to true to show color block before search for key --%>
				<c:set var="showSearchColorBlock" value="${ true }" />
				
				<%--  Include file is dependent on containing loop having varStatus="searchVarStatus"  --%>
				<%@ include file="/WEB-INF/jsp-includes/searchDetailsBlock.jsp" %>



				<tr>
					<td>Type Filter:</td>
					<td colspan="2">
						<%--  Update TestAllWebLinkTypesSelected if add another option --%>

					  <label >
						<input type="checkbox" class=" link_type_jq " 
							value="<%= PeptideViewLinkTypesConstants.CROSSLINK_PSM %>"   >
						crosslinks
					  </label>
					  <label >
						<input type="checkbox" class=" link_type_jq " 
							value="<%= PeptideViewLinkTypesConstants.LOOPLINK_PSM %>" >
						looplinks
					  </label> 
					  <label >
						<input type="checkbox" class=" link_type_jq " 
							value="<%= PeptideViewLinkTypesConstants.UNLINKED_PSM %>" >
						 unlinked
					  </label>

					</td>
				</tr>
				<tr>
					<td valign="top" >Modification Filter:</td>
					<td colspan="2">
					  <label >
						<input type="checkbox" class=" mod_mass_filter_jq " 
							value="" >
						No modifications
					  </label>
					  
						<logic:iterate id="modMassFilter" name="modMassFilterList">
						
						 <label style="white-space: nowrap" >
							<input type="checkbox" class=" mod_mass_filter_jq " 
						  		value="<bean:write name="modMassFilter" />" > 
						   <bean:write name="modMassFilter" />
						 </label>
						  
						</logic:iterate>				
					</td>
				</tr>				
				
					
				<tr>
					<td>&nbsp;</td>
					
					<td>
						
<%--   WAS 						
						<input type="submit" value="Update" >
--%>						
						<input type="button" value="Update"  onclick="viewMergedPeptidePageCode.updatePageForFormParams()" >
											
					</td>
				</tr>
			
			</table>
			
<%-- 			
			</form>
--%>
			
			<div >
	
				<h3 style="display:inline;">Peptides (<bean:write name="peptideListSize" />):</h3>
				
				<div style="display:inline;">
					[<a  class="tool_tip_attached_jq" data-tooltip="Download as tab-delimited text" 
							href="${ contextPath }/downloadMergedPeptides.do?<bean:write name="queryString" />"
						>Download Data</a>]
					
					[<a class="tool_tip_attached_jq" data-tooltip="Download PSM data as tab-delimited text" 
							href="${ contextPath }/downloadMergedPSMsForPeptides.do?<bean:write name="queryString" />"
						>Download PSM Data</a>]
						
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
 			


			<%--  Create via javascript the parts that will be above the main table --%>
			<script type="text/javascript">
			
				viewMergedPeptidePageCode.createPartsAboveMainTable();
				
			</script>
			
			
				<table style="" id="crosslink-table" class="tablesorter top_data_table_jq ">
				
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
												><img src="${contextPath}/images/icon-expand-small.png" 
													class=" icon-expand-contract-in-data-table "
													></span><span class="toggle_visibility_contraction_span_jq" 
														style="display: none;" 
														><img src="${contextPath}/images/icon-collapse-small.png"
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
								
								<td>
								  <c:if test="${ not empty peptideEntry.peptide1ProteinPositions }">
									<logic:iterate id="pp" name="peptideEntry" property="peptide1ProteinPositions">
										<span class="proteinName" id="protein-id-<bean:write name="pp" property="protein.nrProtein.nrseqId" />">
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
										<span class="proteinName" id="protein-id-<bean:write name="pp" property="protein.nrProtein.nrseqId" />">
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
	

			<%@ include file="/WEB-INF/jsp-includes/lorikeet_overlay_section.jsp" %>	

			<%@ include file="/WEB-INF/jsp-includes/nagWhenFormChangedButNotUpdated_Overlay.jsp" %>
			

		</div>
	
	

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
	
	

<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>

<%

WebappTiming webappTiming = (WebappTiming)request.getAttribute( "webappTiming" );

if ( webappTiming != null ) {
		
	webappTiming.markPoint( "At end of JSP" );
	
	webappTiming.logTiming();
}



%>