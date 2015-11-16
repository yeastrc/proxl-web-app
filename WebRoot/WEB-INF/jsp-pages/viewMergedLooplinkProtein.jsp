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
		
		
				
		<script type="text/javascript" src="${ contextPath }/js/createTooltipForProteinNames.js"></script>
		
		<script type="text/javascript" src="${ contextPath }/js/toggleVisibility.js"></script>
			
		<script type="text/javascript" src="${ contextPath }/js/viewLooplinkProteinsLoadedFromWebServiceTemplate.js"></script>
		<script type="text/javascript" src="${ contextPath }/js/viewPsmsLoadedFromWebServiceTemplate.js"></script>
		<script type="text/javascript" src="${ contextPath }/js/viewLooplinkReportedPeptidesLoadedFromWebServiceTemplate.js"></script>
	
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
	
		<%@ include file="/WEB-INF/jsp-includes/viewLooplinkReportedPeptidesLoadedFromWebServiceTemplateFragment.jsp" %>
	

		
	
	<%--  Looplink Protein Template --%>


		
		<script id="looplink_protein_block_template"  type="text/x-handlebars-template">

			<%--  include the template text  --%>
			<%@ include file="/WEB-INF/jsp_template_fragments/For_jsp_pages/viewMergedLooplinkProtein.jsp_templates/looplink_protein_block_template.jsp" %>

		</script>
	

	<%--  Looplink Protein Entry Template --%>



		<%-- !!!   Handlebars template:  Looplink Protein Entry Template  !!!!!!!!!   --%>
		
		
		<script id="looplink_protein_data_row_entry_template"  type="text/x-handlebars-template">

			<%--  include the template text  --%>
			<%@ include file="/WEB-INF/jsp_template_fragments/For_jsp_pages/viewMergedLooplinkProtein.jsp_templates/looplink_protein_data_row_entry_template.jsp" %>

		</script>



	<%--  Looplink Protein Child row Entry Template --%>

		
		<script id="looplink_protein_child_row_entry_template"  type="text/x-handlebars-template">

			<%--  include the template text  --%>
			<%@ include file="/WEB-INF/jsp_template_fragments/For_jsp_pages/viewMergedLooplinkProtein.jsp_templates/looplink_protein_child_row_entry_template.jsp" %>

		</script>

		
		
		<div class="overall-enclosing-block">
			
			<h2 style="margin-bottom:5px;">List merged search proteins:</h2>
	
			<div style="margin-bottom:20px;">

				[<a class="tool_tip_attached_jq" data-tooltip="View peptides" href="${ contextPath }/viewMergedPeptide.do?<bean:write name="queryString" />&linkType=<%= PeptideViewLinkTypesConstants.LOOPLINK_PSM %>"
					>Peptide View</a>]
			
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
	
			<html:form action="viewMergedLooplinkProtein" method="get"  styleId="form_get_for_updated_parameters" >
			
			
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
					<td><html:text property="psmQValueCutoff"  styleId="psmQValueCutoff" onchange="searchFormChanged()" ></html:text></td>
				</tr>
				
				<tr>
					<td>Peptide <span style="white-space: nowrap">Q-value</span> cutoff:</td>
					<td><html:text property="peptideQValueCutoff"  styleId="peptideQValueCutoff" onchange="searchFormChanged()" ></html:text></td>
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
			
			<div >
	
				<h3 style="display:inline;">Merged Looplinks: <bean:write name="numLooplinks" />
				</h3>			
				<div style="display:inline;">
					[<a class="tool_tip_attached_jq" data-tooltip="View crosslinks (instead of looplinks)" href="${ contextPath }/viewMergedCrosslinkProtein.do?<bean:write name="queryString" />">View Crosslinks (<bean:write name="numCrosslinks" />)</a>]
					[<a class="tool_tip_attached_jq" data-tooltip="Download all looplinks as tab-delimited text" href="${ contextPath }/downloadMergedProteins.do?<bean:write name="queryString" />">Download Data (<bean:write name="numLinks" />)</a>]
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

						<th data-tooltip="Number of selected searches containing link" class="tool_tip_attached_jq integer-number-column-header" style="width:45px;font-weight:bold;">Searches</th>
						<th data-tooltip="Name of the looplinked protein" class="tool_tip_attached_jq" style="text-align:left;font-weight:bold;">Protein</th>
						<th data-tooltip="Beginning position of the looplink" class="tool_tip_attached_jq integer-number-column-header" style="width:10%;font-weight:bold;">Position&nbsp;1</th>
						<th data-tooltip="Ending position of the looplink" class="tool_tip_attached_jq integer-number-column-header" style="width:10%;font-weight:bold;">Position&nbsp;2</th>
						<th data-tooltip="Number of peptide spectrum matches showing this link" class="tool_tip_attached_jq integer-number-column-header" style="width:10%;font-weight:bold;">PSMs</th>
						<th data-tooltip="Number of distinct peptides showing link" class="tool_tip_attached_jq integer-number-column-header" style="width:10%;font-weight:bold;">#&nbsp;Peptides</th>
						<th data-tooltip="Number of found peptides that uniquely map to this protein from the FASTA file" class="tool_tip_attached_jq integer-number-column-header" style="width:10%;font-weight:bold;">#&nbsp;Unique Peptides</th>
						
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
						
					<logic:iterate id="looplink" name="looplinks">

<%-- 
							<tr id="<bean:write name="looplink" property="mergedSearchProteinLooplink.protein.nrProtein.nrseqId" />-<bean:write name="looplink" property="mergedSearchProteinLooplink.proteinPosition1" />-<bean:write name="looplink" property="mergedSearchProteinLooplink.protein.nrProtein.nrseqId" />-<bean:write name="looplink" property="mergedSearchProteinLooplink.proteinPosition2" />"
								style="cursor: pointer; "
								onclick="toggleVisibility(this)"
								toggle_visibility_associated_element_id="<bean:write name="looplink" property="mergedSearchProteinLooplink.protein.nrProtein.nrseqId" />-<bean:write name="looplink" property="mergedSearchProteinLooplink.proteinPosition1" />-<bean:write name="looplink" property="mergedSearchProteinLooplink.protein.nrProtein.nrseqId" />-<bean:write name="looplink" property="mergedSearchProteinLooplink.proteinPosition2" />"
							>
--%>



							<tr 
								style="cursor: pointer; "
								
								onclick="viewLooplinkProteinsLoadedFromWebServiceTemplate.showHideLooplinkProteins( { clickedElement : this })"
								project_id="${ projectId }"
								search_ids="<c:forEach var="searchEntryForThisRow" items="${ looplink.mergedSearchProteinLooplink.searches }">,${ searchEntryForThisRow.id }</c:forEach>"
								peptide_q_value_cutoff="${ peptideQValueCutoff }"
								psm_q_value_cutoff="${ psmQValueCutoff }"
								protein_id="<bean:write name="looplink" property="mergedSearchProteinLooplink.protein.nrProtein.nrseqId" />"
								protein_position_1="<bean:write name="looplink" property="mergedSearchProteinLooplink.proteinPosition1" />"
								protein_position_2="<bean:write name="looplink" property="mergedSearchProteinLooplink.proteinPosition2" />"
							>
									
								<c:forEach items="${ looplink.searchContainsLooplink }" var="isMarked"  varStatus="searchVarStatus">
								
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
										><bean:write name="looplink" property="mergedSearchProteinLooplink.numSearches" 
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
																				
								<td><span class="proteinName" id="protein-id-<bean:write name="looplink" property="mergedSearchProteinLooplink.protein.nrProtein.nrseqId" />"><bean:write name="looplink" property="mergedSearchProteinLooplink.protein.name" /></span></td>
								<td class="integer-number-column"><bean:write name="looplink" property="mergedSearchProteinLooplink.proteinPosition1" /></td>
								<td class="integer-number-column"><bean:write name="looplink" property="mergedSearchProteinLooplink.proteinPosition2" /></td>
								<td class="integer-number-column"><bean:write name="looplink" property="mergedSearchProteinLooplink.numPsms" /></td>
								<td class="integer-number-column"><bean:write name="looplink" property="mergedSearchProteinLooplink.numPeptides" /></td>
								<td class="integer-number-column"><bean:write name="looplink" property="mergedSearchProteinLooplink.numUniquePeptides" /></td>
																
								<c:if test="${ showTopLevelBestPeptideQValue  }">
									<td style="white-space: nowrap"><bean:write name="looplink" property="mergedSearchProteinLooplink.bestPeptideQValue" /></td>
								</c:if>
								
								<td style="white-space: nowrap"><bean:write name="looplink" property="mergedSearchProteinLooplink.bestPSMQValue" /></td>
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
								<td colspan="<c:out value="${ fn:length( searches ) + 8 + colspanPeptidesAdded }"></c:out>" align="center" class=" child_data_container_jq ">
								
<%-- 								
									<table class="tablesorter" style="width:80%">

									  <thead>
										<tr>
											<th style="text-align:left;width:60%;font-weight:bold;">Name</th>
											<th class="integer-number-column-header" style="width:10%;font-weight:bold;">Peptides</th>
											<th class="integer-number-column-header" style="width:10%;font-weight:bold;">Unique peptides</th>
											<th class="integer-number-column-header" style="width:10%;font-weight:bold;">Psms</th>
											<c:if test="${ looplink.mergedSearchProteinLooplink.anyLinksHaveBestPeptideQValue  }">
												<th style="text-align:left;width:10%;font-weight:bold;"><span style="white-space: nowrap">Best Peptide</span> <span style="white-space: nowrap">Q-value</span></th>
											</c:if>
											<th style="text-align:left;width:10%;font-weight:bold;"><span style="white-space: nowrap">Best PSM</span> <span style="white-space: nowrap">Q-value</span></th>
										</tr>
									  </thead>

										<logic:iterate id="searchLooplink" name="looplink" property="mergedSearchProteinLooplink.searchProteinLooplinks">
										
											<tr id="<bean:write name="searchLooplink" property="key.id" />-<bean:write name="looplink" property="mergedSearchProteinLooplink.protein.nrProtein.nrseqId" />-<bean:write name="looplink" property="mergedSearchProteinLooplink.proteinPosition1" />-<bean:write name="looplink" property="mergedSearchProteinLooplink.protein.nrProtein.nrseqId" />-<bean:write name="looplink" property="mergedSearchProteinLooplink.proteinPosition2" />" class="mark_search_<bean:write name="searchLooplink" property="key.id" />"
												style="cursor: pointer; "
												
								
												onclick="viewLooplinkReportedPeptidesLoadedFromWebServiceTemplate.showHideLooplinkReportedPeptides( { clickedElement : this })"
												search_id="${ searchLooplink.key.id }"
												project_id="${ projectId }"
												peptide_q_value_cutoff="${ peptideQValueCutoff }"
												psm_q_value_cutoff="${ psmQValueCutoff }"
												protein_id="<bean:write name="looplink" property="mergedSearchProteinLooplink.protein.nrProtein.nrseqId" />"
												protein_position_1="<bean:write name="looplink" property="mergedSearchProteinLooplink.proteinPosition1" />"
												protein_position_2="<bean:write name="looplink" property="mergedSearchProteinLooplink.proteinPosition2" />"
												
												onclick="toggleVisibility(this)"
												toggle_visibility_associated_element_id="<bean:write name="searchLooplink" property="key.id" />-<bean:write name="looplink" property="mergedSearchProteinLooplink.protein.nrProtein.nrseqId" />-<bean:write name="looplink" property="mergedSearchProteinLooplink.proteinPosition1" />-<bean:write name="looplink" property="mergedSearchProteinLooplink.protein.nrProtein.nrseqId" />-<bean:write name="looplink" property="mergedSearchProteinLooplink.proteinPosition2" />"
											>
												<td><bean:write name="searchLooplink" property="key.name" /></td>
												<td class="integer-number-column"><a class="show-child-data-link mark_search_<bean:write name="searchLooplink" property="key.id" />  " 
														href="javascript:"
														><bean:write name="searchLooplink" property="value.numPeptides" 
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
																								
												<td class="integer-number-column"><bean:write name="searchLooplink" property="value.numUniquePeptides" /></td>
												<td class="integer-number-column"><bean:write name="searchLooplink" property="value.numPsms" /></td>
												<c:if test="${ looplink.mergedSearchProteinLooplink.anyLinksHaveBestPeptideQValue  }">
													<td style="white-space: nowrap"><bean:write name="searchLooplink" property="value.bestPeptideQValue" /></td>
												</c:if>
												<td style="white-space: nowrap"><bean:write name="searchLooplink" property="value.bestPSMQValue" /></td>
											</tr>
							
											<tr class="expand-child" style="display:none;">
--%>											
												<%--  Adjust colspan for number of columns in current table --%>
								
												<%--  Init to zero --%>
<%--												
												<c:set var="colspanPSMsAdded" value="${ 0 }" />
--%>												
												<%--   Now add 1 for each column being displayed --%>
<%--												
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
--%>									
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

