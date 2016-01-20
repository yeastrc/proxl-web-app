<%@page import="org.apache.log4j.Logger"%>
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
		
		
		<script type="text/javascript" src="${ contextPath }/js/defaultPageView.js"></script>
		
		<script type="text/javascript" src="${ contextPath }/js/toggleVisibility.js"></script>
		
		<script type="text/javascript" src="${ contextPath }/js/viewPsmsLoadedFromWebServiceTemplate.js"></script>
		
		
		<link rel="stylesheet" href="${ contextPath }/css/tablesorter.css" type="text/css" media="print, projection, screen" />
		<link type="text/css" rel="stylesheet" href="${ contextPath }/css/jquery.qtip.min.css" />


		<%--  some classes in this stylesheet collide with some in the lorikeet file since they are set to specific values for lorikeet drag and drop --%>
		<%-- 
		<link REL="stylesheet" TYPE="text/css" HREF="${contextPath}/css/jquery-ui-1.10.2-Themes/ui-lightness/jquery-ui.min.css">
		--%>

		<link REL="stylesheet" TYPE="text/css" HREF="${contextPath}/css/lorikeet.css">

	<script>
		$(document).ready(function() 
		    { 

			
			   setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else
				  
		       	$("#crosslink-table").tablesorter(); // gets exception if there are no data rows
			   },10);
		    } 
		); // end $(document).ready(function() 
		
		
		//  function called after all HTML above main table is generated
		
		function createPartsAboveMainTable() {

	 		createImageViewerLink();
	 		
	 		createStructureViewerLink();
	 		
			
			   setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else
					  
					initNagUser();
			   },10);
			   
			   setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else
					
					initDefaultPageView() ;
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
			          
			var defaultURL = $("#viewMergedImageDefaultPageUrl").val();
			
			if ( defaultURL === "" ) {
				      
				html += "viewMergedImage.do?project_id=<c:out value="${ projectId }"></c:out>" 
						+ "&searchIds=<bean:write name="search" property="id" />"
						+ imageViewerJSON() ;
						
			} else {
				
				html += defaultURL;
				
			}
			html += "'>Image View</a>]";
			
			
			$( "span#image-viewer-link-span" ).empty();
			$( "span#image-viewer-link-span" ).html (html );
		}
		
		function createStructureViewerLink() {
			
			var $structure_viewer_link_span = $("#structure-viewer-link-span");
			
			if ( $structure_viewer_link_span.length > 0 ) {
				
				var html = "";
				
				html += "[<a href='${ contextPath }/";
				  
				var defaultURL = $("#viewMergedStructureDefaultPageUrl").val();
				
				if ( defaultURL === "" ) {
				
					html += "viewMergedStructure.do?project_id=<c:out value="${ projectId }"></c:out>" 
								+ "&searchIds=<bean:write name="search" property="id" />"
								+ imageViewerJSON() ;
								
				} else {
					
					html += defaultURL;
				}
					
				html += "'>Structure View</a>]";						
							
				$structure_viewer_link_span.empty();
				$structure_viewer_link_span.html( html );
			}
		}

		function getValuesFromForm() {
			
			var psmQValueCutoff = $("#psmQValueCutoff").val();
			var peptideQValueCutoff = $("#peptideQValueCutoff").val();
			
			var crosslinksTypeFilter = $("#crosslinksTypeFilter").is( ':checked' );
			var looplinksTypeFilter = $("#looplinksTypeFilter").is( ':checked' );
			var unlinkedTypeFilter = $("#unlinkedTypeFilter").is( ':checked' );
			

			var modMassFilter = [];

			var $modMassFilter_jq = $(".modMassFilter_jq");
			
			$modMassFilter_jq.each(function () {
				
				var $this = $(this);
				
			    if ( $this.is( ':checked' ) ) {
			    	
			    	var value = $this.val();
			    	
			    	modMassFilter.push( value );
			    }
			    
			});
						

			var formValues = {
					psmQValueCutoff : psmQValueCutoff,
					peptideQValueCutoff : peptideQValueCutoff,
					
					crosslinksTypeFilter : crosslinksTypeFilter,
					looplinksTypeFilter : looplinksTypeFilter,
					unlinkedTypeFilter : unlinkedTypeFilter,
					modMassFilter: modMassFilter
			};
			return formValues;
		}

	</script>


</c:set>



<%@ include file="/WEB-INF/jsp-includes/header_main.jsp" %>

		
		<%@ include file="/WEB-INF/jsp-includes/defaultPageViewFragment.jsp" %>
		
		<%@ include file="/WEB-INF/jsp-includes/viewPsmsLoadedFromWebServiceTemplateFragment.jsp" %>
		
			
		<div class="overall-enclosing-block">
	
			<h2 style="margin-bottom:5px;">List search peptides:</h2>
	
			<div style="margin-bottom:20px;">
				[<a class="tool_tip_attached_jq" data-tooltip="View proteins" href="${ contextPath }/<proxl:defaultPageUrl pageName="viewSearchCrosslinkProtein.do" searchId="${ search.id }">viewSearchCrosslinkProtein.do?<bean:write name="queryString" /></proxl:defaultPageUrl>"
					>Protein View</a>]
				[<a class="tool_tip_attached_jq" data-tooltip="View protein coverage report" href="${ contextPath }/<proxl:defaultPageUrl pageName="viewProteinCoverageReport.do" searchId="${ search.id }">viewProteinCoverageReport.do?<bean:write name="queryString" /></proxl:defaultPageUrl>"
						>Coverage Report</a>]

				<span class="tool_tip_attached_jq" data-tooltip="Graphical view of links between proteins" id="image-viewer-link-span"></span>
				
				<input type="hidden" id="viewMergedImageDefaultPageUrl" 
					value="<proxl:defaultPageUrl pageName="viewMergedImage.do" searchId="${ search.id }"></proxl:defaultPageUrl>">
				
				
				
				<c:choose>
				 <c:when test="${ showStructureLink }">
					
					<span class="tool_tip_attached_jq" data-tooltip="View data on 3D structures" id="structure-viewer-link-span"></span>
									
					<input type="hidden" id="viewMergedStructureDefaultPageUrl" 
						value="<proxl:defaultPageUrl pageName="viewMergedStructure.do" searchId="${ search.id }"></proxl:defaultPageUrl>">
	
				 </c:when>
				 <c:otherwise>
				 	
					<%@ include file="/WEB-INF/jsp-includes/structure_link_non_link.jsp" %>
				 </c:otherwise>
				</c:choose>
				
				
				
			</div>
	
			<html:form action="viewSearchPeptide" method="get" styleId="form_get_for_updated_parameters">
			
			
				<html:hidden property="project_id"/>
						
				<html:hidden property="searchId"/>
			
			<table style="border-width:0px;">
				<tr>
					<td valign="top">Search:</td>
					<td>
					
					
						<%--  Set to false to not show color block before search for key --%>
						<c:set var="showSearchColorBlock" value="${ false }" />
						
						<%--  Include file is dependent on containing loop having varStatus="searchVarStatus"  --%>
						<%@ include file="/WEB-INF/jsp-includes/searchDetailsBlock.jsp" %>


					</td>
				</tr>


				<tr>
					<td>PSM <span style="white-space: nowrap">Q-value</span> cutoff:</td>
					<td><html:text property="psmQValueCutoff" styleId="psmQValueCutoff" onchange="searchFormChanged_ForNag(); searchFormChanged_ForDefaultPageView();" ></html:text></td>
				</tr>
				
				<tr>
					<td>Peptide <span style="white-space: nowrap">Q-value</span> cutoff:</td>
					<td><html:text property="peptideQValueCutoff" styleId="peptideQValueCutoff" onchange="searchFormChanged_ForNag(); searchFormChanged_ForDefaultPageView();" ></html:text></td>
				</tr>
				<tr>
					<td>Type Filter:</td>
					<td>
						<%--  Update TestAllWebLinkTypesSelected if add another option --%>

					  <label >
						<html:multibox property="linkType" value="<%= PeptideViewLinkTypesConstants.CROSSLINK_PSM %>"  styleId="crosslinksTypeFilter" onchange="searchFormChanged_ForNag(); searchFormChanged_ForDefaultPageView();" ></html:multibox>
						crosslinks
					  </label>
					  <label >
						<html:multibox property="linkType" value="<%= PeptideViewLinkTypesConstants.LOOPLINK_PSM %>"  styleId="looplinksTypeFilter" onchange="searchFormChanged_ForNag(); searchFormChanged_ForDefaultPageView();" ></html:multibox>
						looplinks
					  </label> 
					  <label >
						<html:multibox property="linkType" value="<%= PeptideViewLinkTypesConstants.UNLINKED_PSM %>"  styleId="unlinkedTypeFilter" onchange="searchFormChanged_ForNag(); searchFormChanged_ForDefaultPageView();" ></html:multibox>
						 unlinked
					  </label>

					</td>
				</tr>
				<tr>
					<td valign="top" >Modification Filter:</td>
					<td>
					  <label >
						<html:multibox property="modMassFilter" value=""   styleClass="modMassFilter_jq"  onchange="searchFormChanged_ForNag(); searchFormChanged_ForDefaultPageView();" ></html:multibox>
						No modifications
					  </label>
					  
						<logic:iterate id="modMassFilter" name="modMassFilterList">
						
						 <label style="white-space: nowrap" >
						  <html:multibox property="modMassFilter" styleClass="modMassFilter_jq" onchange="searchFormChanged_ForNag(); searchFormChanged_ForDefaultPageView();" >
						   <bean:write name="modMassFilter"/> 
						  </html:multibox> 
						   <bean:write name="modMassFilter" />
						 </label>
						  
						</logic:iterate>				
					</td>
				</tr>				
				
				<tr>
					<td>&nbsp;</td>
					<td colspan="5">
						<input type="submit" value="Update" onclick="searchFormUpdateButtonPressed()">
						<c:if test="${ authAccessLevel.projectOwnerAllowed }" >
							<input type="button" value="Save As Default" id="mergedImageSaveOrUpdateDefaultPageView"
								onclick="saveOrUpdateDefaultPageView( { clickedThis : this, searchId: <bean:write name="search" property="id" /> } )">
						</c:if>
					</td>
				</tr>
			
			</table>
			</html:form>
	
			<h3 style="display:inline;">Peptides (<bean:write name="peptideListSize" />):</h3>
			<div style="display:inline;">
				[<a class="tool_tip_attached_jq" data-tooltip="Download data as tab-delimited text" 
						href="${ contextPath }/downloadMergedPeptides.do?<bean:write name="mergedQueryString" />"
					>Download Data</a>]
					
				[<a class="tool_tip_attached_jq" data-tooltip="Download PSM data as tab-delimited text" 
						href="${ contextPath }/downloadMergedPSMsForPeptides.do?<bean:write name="mergedQueryString" />"
					>Download PSM Data</a>]
			</div>
			

			<%--  Create via javascript the parts that will be above the main table --%>
			<script type="text/javascript">
			
				createPartsAboveMainTable();
				
			</script>
			
  
			  
				<table style="" id="crosslink-table" class="tablesorter  top_data_table_jq ">
				
					<thead>
					<tr>
						<th data-tooltip="Type of peptide (e.g., crosslink, looplink, or unlinked)" class="tool_tip_attached_jq" style="text-align:left;width:10%;font-weight:bold;">Type</th>
						<th data-tooltip="The peptide as reported by search program (e.g., Kojak or XQuest)" class="tool_tip_attached_jq" style="text-align:left;width:10%;font-weight:bold;">Reported peptide</th>
						<th data-tooltip="Sequence of matched peptide (or first peptide in case of crosslinks)" class="tool_tip_attached_jq" style="text-align:left;width:10%;font-weight:bold;">Peptide 1</th>
						<th data-tooltip="Linked position in first peptide (or starting position of looplink)" class="tool_tip_attached_jq integer-number-column-header" style="width:5%;font-weight:bold;">Pos</th>
						<th data-tooltip="Sequenced of second matched peptide in crosslink" class="tool_tip_attached_jq" style="text-align:left;width:10%;font-weight:bold;">Peptide 2</th>
						<th data-tooltip="Linked position in second peptide (or ending position of looplink)" class="tool_tip_attached_jq integer-number-column-header" style="width:5%;font-weight:bold;">Pos</th>
						<th data-tooltip="Proteins (and positions) matched by first peptide and position" class="tool_tip_attached_jq" style="text-align:left;width:5%;font-weight:bold;">Protein 1</th>
						<th data-tooltip="Proteins (and positions) matched by second peptide and position" class="tool_tip_attached_jq" style="text-align:left;width:5%;font-weight:bold;">Protein 2</th>
						
						<c:if test="${ showTopLevelPeptideQValue }">
							<th data-tooltip="Peptide-level q-value for this peptide (or linked pair)" 
									class="tool_tip_attached_jq" 
									style="width:10%;font-weight:bold;">
								<span style="white-space: nowrap">Q-value</span>
							</th>
						</c:if>
						
						<th data-tooltip="Number of PSMs matched to this peptide (or linked pair)" class="tool_tip_attached_jq integer-number-column-header" style="width:10%;font-weight:bold; white-space: nowrap;"># PSMs</th>
						
						<c:if test="${ showNumberUniquePSMs }">
							<th data-tooltip="Number of scans that uniquely matched to this reported peptide" class="tool_tip_attached_jq integer-number-column-header" style="width:10%;font-weight:bold; white-space: nowrap;"># Unique</th>
						</c:if>
						
						<th data-tooltip="Best q-value among PSMs that matched this peptide (or linked pair)" class="tool_tip_attached_jq" style="width:10%;font-weight:bold;">Best&nbsp;PSM <span style="white-space: nowrap">Q-value</span></th>
					</tr>
					</thead>
						
					<logic:iterate id="peptideEntry" name="peptideList">

							<tr id="reported-peptide-<bean:write name="peptideEntry" property="reportedPeptide.id"/>"
								style="cursor: pointer; "
								onclick="viewPsmsLoadedFromWebServiceTemplate.showHidePsms( { clickedElement : this } )"
								reported_peptide_id="${ peptideEntry.reportedPeptide.id }"
								search_id="${ search.id }"
								project_id="${ projectId }"
								peptide_q_value_cutoff="${ peptideQValueCutoff }"  <%-- JSP EL value --%>
								psm_q_value_cutoff="${ psmQValueCutoff }"
								>
								
								<td>
									<c:choose>
										<c:when test="${ not empty peptideEntry.searchPeptideCrosslink }">Crosslink</c:when>
										<c:when test="${ not empty peptideEntry.searchPeptideLooplink }">Looplink</c:when>
<%-- 										
										<c:when test="${ not empty peptideEntry.searchPeptideUnlinked or not empty peptideEntry.searchPeptideDimer }"
											>Unlinked</c:when>
--%>											
										<c:when test="${ not empty peptideEntry.searchPeptideUnlinked }"
											>Unlinked</c:when>
										<c:when test="${ not empty peptideEntry.searchPeptideDimer }"
											>Dimer</c:when>
										<c:otherwise>Unknown</c:otherwise>
									</c:choose>
								
								</td>
								
								<td><bean:write name="peptideEntry" property="reportedPeptide.sequence" /></td>
								<td><bean:write name="peptideEntry" property="peptide1.sequence" /></td>
								<td class="integer-number-column" ><bean:write name="peptideEntry" property="peptide1Position" /></td>
								<td><c:if test="${ not empty peptideEntry.peptide2 }" ><bean:write name="peptideEntry" property="peptide2.sequence" /></c:if></td>
								<td class="integer-number-column" ><bean:write name="peptideEntry" property="peptide2Position" /></td>
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
								<c:if test="${ showTopLevelPeptideQValue }"
								><td style="white-space: nowrap"><c:out  value="${ peptideEntry.qValue }" /></td></c:if>

								<td class="integer-number-column" ><a class="show-child-data-link  " 
										href="javascript:"
										<%--
										onclick="toggleVisibility(this)" 
										toggle_visibility_associated_element_id="reported-peptide-<bean:write name="peptideEntry" property="reportedPeptide.id"/>"
										--%>
										><bean:write name="peptideEntry" property="numPsms" 
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

								<c:if test="${ showNumberUniquePSMs }">
								
									<td class="integer-number-column" 
										><bean:write name="peptideEntry" property="numUniquePsms" />
									</td>
								</c:if>					
								
								
								<td><c:out  value="${ peptideEntry.bestPsmQValue }" /></td>
							</tr>

							<tr class="expand-child" style="display:none;">
							 
								<%--  Adjust colspan for number of columns in current table --%>
								
								
								<%--  Init to zero --%>
								<c:set var="colspanPeptidesAdded" value="${ 0 }" />
								
								<%--   Now add 1 for each column being displayed --%>
								<c:if test="${ showTopLevelPeptideQValue }">
									<c:set var="colspanPeptidesAdded" value="${ colspanPeptidesAdded + 1 }" />
								</c:if>
																		
								
															 
							 <td colspan="<c:out value="${ 11 + colspanPeptidesAdded }"></c:out>" align="center" class=" child_data_container_jq ">
									
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
	

<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>



<%

WebappTiming webappTiming = (WebappTiming)request.getAttribute( "webappTiming" );

if ( webappTiming != null ) {
		
	webappTiming.markPoint( "At end of JSP" );
	
	webappTiming.logTiming();
}



%>


