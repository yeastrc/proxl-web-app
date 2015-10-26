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
				
		<script type="text/javascript" src="${ contextPath }/js/nagWhenFormChangedButNotUpdated.js"></script>				
		
				
		<script type="text/javascript" src="${ contextPath }/js/createTooltipForProteinNames.js"></script>
		
		<script type="text/javascript" src="${ contextPath }/js/toggleVisibility.js"></script>
					
		<script type="text/javascript" src="${ contextPath }/js/viewPsmsLoadedFromWebServiceTemplate.js"></script>
		<script type="text/javascript" src="${ contextPath }/js/viewCrosslinkReportedPeptidesLoadedFromWebServiceTemplate.js"></script>
		
		<script type="text/javascript" src="${ contextPath }/js/mergedSearchesVennDiagramCreator.js"></script>

		<link rel="stylesheet" href="${ contextPath }/css/tablesorter.css" type="text/css" media="print, projection, screen" />
		<link type="text/css" rel="stylesheet" href="${ contextPath }/css/jquery.qtip.min.css" />

		<%--  some classes in this stylesheet collide with some in the lorikeet file since they are set to specific values for lorikeet drag and drop --%>
		<%-- 
		<link REL="stylesheet" TYPE="text/css" HREF="${contextPath}/css/jquery-ui-1.10.2-Themes/ui-lightness/jquery-ui.min.css">
		--%>

		<link REL="stylesheet" TYPE="text/css" HREF="${contextPath}/css/lorikeet.css">
		
	<script>

		var searches = <bean:write name="searchJSON" filter="false" />;



		$(document).ready(function() { 

				setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else
					  
			       	$("#crosslink-table").tablesorter(); // gets exception if there are no data rows
				},10);

		 }); 
		
		
		//  function called after all HTML above main table is generated
		
		function createPartsAboveMainTable() {

	 		createImageViewerLink();
	 		
	 		createStructureViewerLink();


	 		<c:if test="${ not empty vennDiagramDataToJSON }">
		 		
				//  This data is used in the script mergedSearchesVennDiagramCreator.js
				var searchesLinkCountsVennDiagramData = <c:out value="${ vennDiagramDataToJSON }" escapeXml="false"></c:out>;
	
		 		
		 		createMergedSearchesLinkCountsVennDiagram( searchesLinkCountsVennDiagramData );
		 	
		 	</c:if>
		 	
		 	
		   setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else
				  
				initNagUser();
		   },10);	 		
		}
		
		

		function imageViewerJSON() {
			var json = { };
			
			json.psmQValueCutoff = <bean:write name="psmQValueCutoff" />;
			json.peptideQValueCutoff = <bean:write name="peptideQValueCutoff" />;
			
			return "#" + encodeURI( JSON.stringify( json ) );
		}
		
		function createImageViewerLink() {
			var html = "";
			
			html += "[<a href='${ contextPath }/";
	          
			   
			html += "viewMergedImage.do?project_id=<c:out value="${ projectId }"></c:out>" 
						
				<c:forEach var="searchId" items="${ searchIds }">
						+ "&searchIds=<c:out value="${ searchId }"></c:out>"
				</c:forEach>
						
						+ imageViewerJSON() ;
						
			html += "'>Image View</a>]";
			
				
			$( "span#image-viewer-link-span" ).empty();
			$( "span#image-viewer-link-span" ).html (html );
		}

		function createStructureViewerLink() {
			
			var $structure_viewer_link_span = $("#structure-viewer-link-span");
			
			if ( $structure_viewer_link_span.length > 0 ) {
				
				var html = "";
				
				html += "[<a href='${ contextPath }/";
		          
				   
				html += "viewMergedStructure.do?project_id=<c:out value="${ projectId }"></c:out>" 
							
					<c:forEach var="searchId" items="${ searchIds }">
							+ "&searchIds=<c:out value="${ searchId }"></c:out>"
					</c:forEach>
							
							+ imageViewerJSON() ;
							
				html += "'>Structure View</a>]";
			
				$structure_viewer_link_span.empty();
				$structure_viewer_link_span.html( html );
			}
		}

		function getValuesFromForm() {
			
			var psmQValueCutoff = $("#psmQValueCutoff").val();
			var peptideQValueCutoff = $("#peptideQValueCutoff").val();
			
			var filterNonUniquePeptides = $("#filterNonUniquePeptides").is( ':checked' );
			
			var excludeTaxonomy = [];

			var $excludeTaxonomy_jq = $(".excludeTaxonomy_jq");
			
			$excludeTaxonomy_jq.each(function () {
				
				var $this = $(this);
				
			    if ( $this.is( ':checked' ) ) {
			    	
			    	var value = $this.val();
			    	
			    	excludeTaxonomy.push( value );
			    }
			    
			});
			
			
			var excludeProtein = $("#excludeProtein").val();

			
			var formValues = {
					psmQValueCutoff : psmQValueCutoff,
					peptideQValueCutoff : peptideQValueCutoff,
					filterNonUniquePeptides : filterNonUniquePeptides,
					excludeTaxonomy : excludeTaxonomy,
					excludeProtein : excludeProtein
			};
			return formValues;
		}		
		


	</script>


</c:set>



<%@ include file="/WEB-INF/jsp-includes/header_main.jsp" %>

		<%@ include file="/WEB-INF/jsp-includes/viewPsmsLoadedFromWebServiceTemplateFragment.jsp" %>
		<%@ include file="/WEB-INF/jsp-includes/viewCrosslinkReportedPeptidesLoadedFromWebServiceTemplateFragment.jsp" %>
		
		<div class="overall-enclosing-block">
	
			<h2 style="margin-bottom:5px;">List merged search proteins:</h2>
	
			<div style="margin-bottom:20px;">
			
				[<a class="tool_tip_attached_jq" data-tooltip="View peptides" href="${ contextPath }/viewMergedPeptide.do?<bean:write name="queryString" />">Peptide View</a>]
			
				[<a class="tool_tip_attached_jq" data-tooltip="View protein coverage report" href="${ contextPath }/viewMergedProteinCoverageReport.do?<bean:write name="queryString" />">Coverage Report</a>]
				<span class="tool_tip_attached_jq" data-tooltip="Graphical view of links between proteins" id="image-viewer-link-span"></span>
				

				<c:choose>
				 <c:when test="${ showStructureLink }">
					
					<span class="tool_tip_attached_jq" data-tooltip="View data on 3D structures" id="structure-viewer-link-span"></span>
	
				 </c:when>
				 <c:otherwise>
				 	
										 	
					<%@ include file="/WEB-INF/jsp-includes/structure_link_non_link.jsp" %>
										 	
				 </c:otherwise>
				</c:choose>
								
			</div>
	
			<html:form action="viewMergedCrosslinkProtein" method="get" styleId="form_get_for_updated_parameters" >
			
			
				<html:hidden property="project_id"/>
						
						<logic:iterate name="searches" id="search">
							<input type="hidden" name="searchIds" value="<bean:write name="search" property="id" />">
						</logic:iterate>
			
			<table style="border-width:0px;">
				<tr>
					<td valign="top">Searches:</td>
					<td>
						<%--  Set to true to show color block before search for key --%>
						<c:set var="showSearchColorBlock" value="${ true }" />
						
						<%--  Include file is dependent on containing loop having varStatus="searchVarStatus"  --%>
						<%@ include file="/WEB-INF/jsp-includes/searchDetailsBlock.jsp" %>

					</td>
				</tr>

				<tr>
					<td>PSM <span style="white-space: nowrap">Q-value</span> cutoff:</td>
					<td><html:text property="psmQValueCutoff" styleId="psmQValueCutoff" onchange="searchFormChanged()" ></html:text></td>
				</tr>
				
				<tr>
					<td>Peptide <span style="white-space: nowrap">Q-value</span> cutoff:</td>
					<td><html:text property="peptideQValueCutoff" styleId="peptideQValueCutoff" onchange="searchFormChanged()" ></html:text></td>
				</tr>
				

				<tr>
					<td>Exclude xlinks with:</td>
					<td>
						 <label><span style="white-space:nowrap;" >
							<html:checkbox property="filterNonUniquePeptides" styleId="filterNonUniquePeptides" onchange="searchFormChanged()" ></html:checkbox>					
						 	 no unique peptides
						 </span></label>
						 <label><span style="white-space:nowrap;" >
							<html:checkbox property="filterOnlyOnePSM" styleId="filterOnlyOnePSM" onchange="searchFormChanged()" ></html:checkbox>					
						 	 only one PSM
						 </span></label>
						 <label><span style="white-space:nowrap;" >
							<html:checkbox property="filterOnlyOnePeptide" styleId="filterOnlyOnePeptide" onchange="searchFormChanged()" ></html:checkbox>					
						 	 only one peptide
						 </span></label>
					</td>
				</tr>
								
<%-- 						 
				<tr>
					<td>Filter out xlinks with no unique peptides:</td>
					<td>
						<html:checkbox property="filterNonUniquePeptides" styleId="filterNonUniquePeptides" onchange="searchFormChanged()" ></html:checkbox>					
					</td>
				</tr>
--%>						 

				<tr>
					<td>Exclude organisms:</td>
					<td>
						<logic:iterate id="taxonomy" name="taxonomies">
						 <label style="white-space: nowrap" >
						  <html:multibox property="excludeTaxonomy" styleClass="excludeTaxonomy_jq" onchange="searchFormChanged()" >
						   <bean:write name="taxonomy" property="key"/> 
						  </html:multibox> 
						   <span style="font-style:italic;"><bean:write name="taxonomy" property="value"/></span>
						 </label> 
						</logic:iterate>				
					</td>
				</tr>

				<tr>
					<td>Exclude protein(s):</td>
					<td>
						<%--  shortened property from "excludeProtein" to "excP" to shorten the URL  --%>
						<html:select property="excP" multiple="true" styleId="excludeProtein" onchange="searchFormChanged();" >
							<html:options collection="proteins" property="nrProtein.nrseqId" labelProperty="name" />
						</html:select>			
					</td>
				</tr>
				
				<tr>
					<td>&nbsp;</td>
					<td><input type="submit" value="Update" onclick="searchFormUpdateButtonPressed()"></td>
				</tr>
			
			</table>
			</html:form>
	
		<div>
			<h3 style="display:inline;">Merged Crosslinks: <bean:write name="numCrosslinks" />
			</h3>

			<div style="display:inline;">
				[<a class="tool_tip_attached_jq" data-tooltip="View looplinks (instead of crosslinks)" href="${ contextPath }/viewMergedLooplinkProtein.do?<bean:write name="queryString" />">View Looplinks (<bean:write name="numLooplinks" />)</a>]
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
			
				createPartsAboveMainTable();
				
			</script>

				<table style="" id="crosslink-table" class="tablesorter">
				
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
						<th data-tooltip="Number of found peptide pairs that uniquely map to these two proteins from the FASTA file"" class="tool_tip_attached_jq integer-number-column-header" style="width:10%;font-weight:bold;">#&nbsp;Unique Peptides</th>
						<c:if test="${ showTopLevelBestPeptideQValue  }">
							<th data-tooltip="Best peptide-level q-value for peptides found showing this link" 
									class="tool_tip_attached_jq" style="width:10%;font-weight:bold;">
										<span style="white-space: nowrap">Best Peptide</span>
										<span style="white-space: nowrap">Q-value</span>
							</th>
						</c:if>
						<th data-tooltip="Best PSM-level q-value for PSMs matched to peptides that show this link" class="tool_tip_attached_jq" style="width:10%;font-weight:bold;">Best&nbsp;PSM <span style="white-space: nowrap">Q-value</span></th>
					</tr>
					</thead>
						
					<logic:iterate id="crosslink" name="crosslinks">

							<tr id="<bean:write name="crosslink" property="mergedSearchProteinCrosslink.protein1.nrProtein.nrseqId" />-<bean:write name="crosslink" property="mergedSearchProteinCrosslink.protein1Position" />-<bean:write name="crosslink" property="mergedSearchProteinCrosslink.protein2.nrProtein.nrseqId" />-<bean:write name="crosslink" property="mergedSearchProteinCrosslink.protein2Position" />"
								style="cursor: pointer; "
								onclick="toggleVisibility(this)"
								toggle_visibility_associated_element_id="<bean:write name="crosslink" property="mergedSearchProteinCrosslink.protein1.nrProtein.nrseqId" />-<bean:write name="crosslink" property="mergedSearchProteinCrosslink.protein1Position" />-<bean:write name="crosslink" property="mergedSearchProteinCrosslink.protein2.nrProtein.nrseqId" />-<bean:write name="crosslink" property="mergedSearchProteinCrosslink.protein2Position" />"
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
										><bean:write name="crosslink" property="mergedSearchProteinCrosslink.numSearches" 
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
								
								<td><span class="proteinName" id="protein-id-<bean:write name="crosslink" property="mergedSearchProteinCrosslink.protein1.nrProtein.nrseqId" />"><bean:write name="crosslink" property="mergedSearchProteinCrosslink.protein1.name" /></span></td>
								<td class="integer-number-column"><bean:write name="crosslink" property="mergedSearchProteinCrosslink.protein1Position" /></td>
								<td><span class="proteinName" id="protein-id-<bean:write name="crosslink" property="mergedSearchProteinCrosslink.protein2.nrProtein.nrseqId" />"><bean:write name="crosslink" property="mergedSearchProteinCrosslink.protein2.name" /></span></td>
								<td class="integer-number-column"><bean:write name="crosslink" property="mergedSearchProteinCrosslink.protein2Position" /></td>
								<td class="integer-number-column"><bean:write name="crosslink" property="mergedSearchProteinCrosslink.numPsms" /></td>
								<td class="integer-number-column"><bean:write name="crosslink" property="mergedSearchProteinCrosslink.numLinkedPeptides" /></td>
								<td class="integer-number-column"><bean:write name="crosslink" property="mergedSearchProteinCrosslink.numUniqueLinkedPeptides" /></td>
		
								<c:if test="${ showTopLevelBestPeptideQValue  }">
									<td style="white-space: nowrap"><bean:write name="crosslink" property="mergedSearchProteinCrosslink.bestPeptideQValue" /></td>
								</c:if>

								<td style="white-space: nowrap"><bean:write name="crosslink" property="mergedSearchProteinCrosslink.bestPSMQValue" /></td>
							</tr>

							<tr class="expand-child" style="display:none;">

								<%--  Adjust colspan for number of columns in current table --%>
								
								
								<%--  Init to zero --%>
								<c:set var="colspanPeptidesAdded" value="${ 0 }" />
								
								<%--   Now add 1 for each column being displayed --%>
								<c:if test="${ showTopLevelBestPeptideQValue }">
									<c:set var="colspanPeptidesAdded" value="${ colspanPeptidesAdded + 1 }" />
								</c:if>
																		
								
							
								<%--  colspan set to the number of searches plus the number of other columns --%>
								<td colspan="<c:out value="${ fn:length( searches ) + 9 + colspanPeptidesAdded }"></c:out>" align="center">
								
									<table class="tablesorter" style="width:80%">
									
									  <thead>
										<tr>
											<th style="text-align:left;width:60%;font-weight:bold;">Name</th>
											<th class="integer-number-column-header" style="width:10%;font-weight:bold;">Peptides</th>
											<th class="integer-number-column-header" style="width:10%;font-weight:bold;">Unique peptides</th>
											<th class="integer-number-column-header" style="width:10%;font-weight:bold;">Psms</th>
											<c:if test="${ crosslink.mergedSearchProteinCrosslink.anyLinksHaveBestPeptideQValue  }">
												<th style="text-align:left;width:10%;font-weight:bold;"><span style="white-space: nowrap">Best Peptide</span> <span style="white-space: nowrap">Q-value</span></th>
											</c:if>
											<th style="text-align:left;width:10%;font-weight:bold;"><span style="white-space: nowrap">Best PSM</span> <span style="white-space: nowrap">Q-value</span></th>
										</tr>
									  </thead>

										<logic:iterate id="searchCrosslink" name="crosslink" property="mergedSearchProteinCrosslink.searchProteinCrosslinks">
										
											<tr id="<bean:write name="searchCrosslink" property="key.id" />-<bean:write name="crosslink" property="mergedSearchProteinCrosslink.protein1.nrProtein.nrseqId" />-<bean:write name="crosslink" property="mergedSearchProteinCrosslink.protein1Position" />-<bean:write name="crosslink" property="mergedSearchProteinCrosslink.protein2.nrProtein.nrseqId" />-<bean:write name="crosslink" property="mergedSearchProteinCrosslink.protein2Position" />" class="mark_search_<bean:write name="searchCrosslink" property="key.id" />"
												style="cursor: pointer; "
												
												onclick="viewCrosslinkReportedPeptidesLoadedFromWebServiceTemplate.showHideCrosslinkReportedPeptides( { clickedElement : this })"
												search_id="${ searchCrosslink.key.id }"
												project_id="${ projectId }"
												peptide_q_value_cutoff="${ peptideQValueCutoff }"
												psm_q_value_cutoff="${ psmQValueCutoff }"
												protein_1_id="<bean:write name="crosslink" property="mergedSearchProteinCrosslink.protein1.nrProtein.nrseqId" />"
												protein_2_id="<bean:write name="crosslink" property="mergedSearchProteinCrosslink.protein2.nrProtein.nrseqId" />"
												protein_1_position="<bean:write name="crosslink" property="mergedSearchProteinCrosslink.protein1Position" />"
												protein_2_position="<bean:write name="crosslink" property="mergedSearchProteinCrosslink.protein2Position" />"
											>
											
												<td><bean:write name="searchCrosslink" property="key.name" /></td>

												<td class="integer-number-column-header"
													><a class="show-child-data-link  mark_search_<bean:write name="searchCrosslink" property="key.id" />   " 
														href="javascript:"
														><bean:write name="searchCrosslink" property="value.numLinkedPeptides" 
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
												
												<td class="integer-number-column-header"><bean:write name="searchCrosslink" property="value.numUniqueLinkedPeptides" /></td>
												<td class="integer-number-column-header"><bean:write name="searchCrosslink" property="value.numPsms" /></td>
												<c:if test="${ crosslink.mergedSearchProteinCrosslink.anyLinksHaveBestPeptideQValue  }">
													<td style="white-space: nowrap"><bean:write name="searchCrosslink" property="value.bestPeptideQValue" /></td>
												</c:if>
												<td style="white-space: nowrap"><bean:write name="searchCrosslink" property="value.bestPSMQValue" /></td>
											</tr>

							
											<tr class="expand-child" style="display:none;">
											

												<%--  Adjust colspan for number of columns in current table --%>
								
												<%--  Init to zero --%>
												<c:set var="colspanPSMsAdded" value="${ 0 }" />
												
												<%--   Now add 1 for each column being displayed --%>
												<c:if test="${ crosslink.mergedSearchProteinCrosslink.anyLinksHaveBestPeptideQValue }">
													<c:set var="colspanPSMsAdded" value="${ colspanPSMsAdded + 1 }" />
												</c:if>
							
												<td colspan="${ 5 + colspanPSMsAdded }" align="center" class=" child_data_container_jq ">
												
													<div style="color: green; font-size: 16px; padding-top: 10px; padding-bottom: 10px;" >
														Loading...
													</div>
												</td>
											</tr>											
											
										</logic:iterate>
										
									</table>
								</td>
							</tr>


					</logic:iterate>
				</table>

	

			<%@ include file="/WEB-INF/jsp-includes/lorikeet_overlay_section.jsp" %>	

			<%@ include file="/WEB-INF/jsp-includes/nagWhenFormChangedButNotUpdated_Overlay.jsp" %>
	


		</div>
	
	

<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>

<%

WebappTiming webappTiming = (WebappTiming)request.getAttribute( "webappTiming" );

if ( webappTiming != null ) {
		
	webappTiming.markPoint( "At end of JSP" );
	
	webappTiming.logTiming();
}



%>

