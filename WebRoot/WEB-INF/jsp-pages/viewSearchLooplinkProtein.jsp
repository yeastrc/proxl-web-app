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
		<script type="text/javascript" src="${ contextPath }/js/viewLooplinkReportedPeptidesLoadedFromWebServiceTemplate.js"></script>
		
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
		); 

		
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
			
			var filterNonUniquePeptides = $("#filterNonUniquePeptides").is( ':checked' );
			
			var filterOnlyOnePSM = $("#filterOnlyOnePSM").is( ':checked' );
			var filterOnlyOnePeptide = $("#filterOnlyOnePeptide").is( ':checked' );
			
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
					filterOnlyOnePSM : filterOnlyOnePSM,
					filterOnlyOnePeptide : filterOnlyOnePeptide,
					excludeTaxonomy : excludeTaxonomy,
					excludeProtein : excludeProtein
			};
			return formValues;
		}		
		    
	</script>



</c:set>



<%@ include file="/WEB-INF/jsp-includes/header_main.jsp" %>

	
		<%@ include file="/WEB-INF/jsp-includes/defaultPageViewFragment.jsp" %>
				
		<%@ include file="/WEB-INF/jsp-includes/viewPsmsLoadedFromWebServiceTemplateFragment.jsp" %>
		
		<%@ include file="/WEB-INF/jsp-includes/viewLooplinkReportedPeptidesLoadedFromWebServiceTemplateFragment.jsp" %>
				
		<div class="overall-enclosing-block">
			
			<h2 style="margin-bottom:5px;">List search proteins:</h2>
	
			<div style="margin-bottom:20px;">
				[<a class="tool_tip_attached_jq" data-tooltip="View peptides" href="${ contextPath }/<proxl:defaultPageUrl pageName="viewSearchPeptide.do" searchId="${ search.id }">viewSearchPeptide.do?project_id=<c:out value="${projectId}" />&searchId=<bean:write name="search" property="id" />&linkType=<%= PeptideViewLinkTypesConstants.LOOPLINK_PSM %></proxl:defaultPageUrl>"
						>Peptide View</a>]
									
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
	
			<html:form action="viewSearchLooplinkProtein" method="get" styleId="form_get_for_updated_parameters">
			
			
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
					<td>Exclude xlinks with:</td>
					<td>
						 <label><span style="white-space:nowrap;" >
							<html:checkbox property="filterNonUniquePeptides" styleId="filterNonUniquePeptides" onchange="searchFormChanged_ForNag(); searchFormChanged_ForDefaultPageView();" ></html:checkbox>					
						 	 no unique peptides
						 </span></label>
						 <label><span style="white-space:nowrap;" >
							<html:checkbox property="filterOnlyOnePSM" styleId="filterOnlyOnePSM" onchange="searchFormChanged_ForNag(); searchFormChanged_ForDefaultPageView();" ></html:checkbox>					
						 	 only one PSM
						 </span></label>
						 <label><span style="white-space:nowrap;" >
							<html:checkbox property="filterOnlyOnePeptide" styleId="filterOnlyOnePeptide" onchange="searchFormChanged_ForNag(); searchFormChanged_ForDefaultPageView();" ></html:checkbox>					
						 	 only one peptide
						 </span></label>
					</td>
				</tr>
								
<%-- 						 
				<tr>
					<td>Filter out xlinks with no unique peptides:</td>
					<td>
						<html:checkbox property="filterNonUniquePeptides" styleId="filterNonUniquePeptides" onchange="searchFormChanged_ForNag(); searchFormChanged_ForDefaultPageView();" ></html:checkbox>					
					</td>
				</tr>
--%>					
				
				<tr>
					<td>Exclude organisms:</td>
					<td>
						<logic:iterate id="taxonomy" name="taxonomies">
						 <label style="white-space: nowrap" >
						  <html:multibox property="excludeTaxonomy" styleClass="excludeTaxonomy_jq" onchange="searchFormChanged_ForNag(); searchFormChanged_ForDefaultPageView();" >
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
						<html:select property="excP" multiple="true" styleId="excludeProtein" onchange="searchFormChanged_ForNag(); searchFormChanged_ForDefaultPageView();" >
							<html:options collection="proteins" property="nrProtein.nrseqId" labelProperty="name" />
						</html:select>			
					</td>
				</tr>
				
				<tr>
					<td>&nbsp;</td>
					<td>
						<input type="submit" value="Update" onclick="searchFormUpdateButtonPressed()">

						<c:if test="${ authAccessLevel.projectOwnerAllowed }" >
							<input type="button" value="Save As Default" id="mergedImageSaveOrUpdateDefaultPageView"
								onclick="saveOrUpdateDefaultPageView( { clickedThis : this, searchId: <bean:write name="search" property="id" /> } )">
						</c:if>
					</td>
				</tr>
			
			</table>
			</html:form>
	
			<h3 style="display:inline;">Looplinks (<bean:write name="numLooplinks" />):</h3>
			<div style="display:inline;">

				[<a class="tool_tip_attached_jq" data-tooltip="View crosslinks (instead of looplinks)" href="${ contextPath }/<proxl:defaultPageUrl pageName="viewSearchCrosslinkProtein.do" searchId="${ search.id }">viewSearchCrosslinkProtein.do?<bean:write name="queryString" /></proxl:defaultPageUrl>"
						>View Crosslinks (<bean:write name="numCrosslinks" />)</a>]
				[<a class="tool_tip_attached_jq" data-tooltip="Download all looplinks as tab-delimited text" href="${ contextPath }/downloadMergedProteins.do?<bean:write name="mergedQueryString" />">Download Data (<bean:write name="numLinks" />)</a>]
				[<a class="tool_tip_attached_jq" data-tooltip="Download all distinct UDRs (crosslinks and looplinks) as tab-delimited text" href="${ contextPath }/downloadMergedProteinUDRs.do?<bean:write name="mergedQueryString" />">Download UDRs (<bean:write name="numDistinctLinks" />)</a>]
			</div>

			<%--  Create via javascript the parts that will be above the main table --%>
			<script type="text/javascript">
			
				createPartsAboveMainTable();
				
			</script>
			
			
				<table style="" id="crosslink-table" class="tablesorter">
				
					<thead>
					<tr>
						<th data-tooltip="Name of the looplinked protein" class="tool_tip_attached_jq" style="text-align:left;width:10%;font-weight:bold;">Protein</th>
						<th data-tooltip="Beginning position of the looplink" class="tool_tip_attached_jq integer-number-column-header" style="width:10%;font-weight:bold;">Position&nbsp;1</th>
						<th data-tooltip="Ending position of the looplink" class="tool_tip_attached_jq integer-number-column-header" style="width:10%;font-weight:bold;">Position&nbsp;2</th>
						<th data-tooltip="Number of peptide spectrum matches showing this link" class="tool_tip_attached_jq integer-number-column-header" style="width:10%;font-weight:bold;">PSMs</th>
						<th data-tooltip="Number of distinct peptides showing link" class="tool_tip_attached_jq integer-number-column-header" style="width:10%;font-weight:bold;">#&nbsp;Peptides</th>
						<th data-tooltip="Number of found peptides that uniquely map to this protein from the FASTA file" class="tool_tip_attached_jq integer-number-column-header" style="width:10%;font-weight:bold;">#&nbsp;Unique Peptides</th>
						
						<c:if test="${ showTopLevelBestPeptideQValue }">
							<th data-tooltip="Best peptide-level q-value for peptides found showing this link" class="tool_tip_attached_jq" style="width:10%;font-weight:bold;">Best&nbsp;Peptide <span style="white-space: nowrap">Q-value</span></th>
						</c:if>
						
						<th data-tooltip="Best PSM-level q-value for PSMs matched to peptides that show this link" class="tool_tip_attached_jq" style="width:10%;font-weight:bold;">Best&nbsp;PSM <span style="white-space: nowrap">Q-value</span></th>
					</tr>
					</thead>
						
					<logic:iterate id="looplink" name="looplinks">
							<tr id="<bean:write name="looplink" property="protein.nrProtein.nrseqId" />-<bean:write name="looplink" property="proteinPosition1" />-<bean:write name="looplink" property="protein.nrProtein.nrseqId" />-<bean:write name="looplink" property="proteinPosition2" />"
								style="cursor: pointer; "
								
								
								onclick="viewLooplinkReportedPeptidesLoadedFromWebServiceTemplate.showHideLooplinkReportedPeptides( { clickedElement : this })"
								search_id="${ search.id }"
								project_id="${ projectId }"
								peptide_q_value_cutoff="${ peptideQValueCutoff }"
								psm_q_value_cutoff="${ psmQValueCutoff }"
								protein_id="<bean:write name="looplink" property="protein.nrProtein.nrseqId" />"
								protein_position_1="<bean:write name="looplink" property="proteinPosition1" />"
								protein_position_2="<bean:write name="looplink" property="proteinPosition2" />"
							>
								<td><span class="proteinName" id="protein-id-<bean:write name="looplink" property="protein.nrProtein.nrseqId" />"><bean:write name="looplink" property="protein.name" /></span></td>
								<td class="integer-number-column"><bean:write name="looplink" property="proteinPosition1" /></td>
								<td class="integer-number-column"><bean:write name="looplink" property="proteinPosition2" /></td>
								<td class="integer-number-column"><bean:write name="looplink" property="numPsms" /></td>
				
								<td class="integer-number-column"><a class="show-child-data-link   " 
										href="javascript:"
										><bean:write name="looplink" property="numPeptides"  
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
																
								<td class="integer-number-column"><bean:write name="looplink" property="numUniquePeptides" /></td>
								
								<c:if test="${ showTopLevelBestPeptideQValue }">
									<td style="white-space: nowrap"><bean:write name="looplink" property="bestPeptideQValue" /></td>
								</c:if>
								
								<td style="white-space: nowrap"><bean:write name="looplink" property="bestPSMQValue" /></td>
							</tr>
							
							<tr class="expand-child" style="display:none;">
							

								<%--  Adjust colspan for number of columns in current table --%>
								
								
								<%--  Init to zero --%>
								<c:set var="colspanPeptidesAdded" value="${ 0 }" />
								
								<%--   Now add 1 for each column being displayed --%>
								<c:if test="${ showTopLevelBestPeptideQValue }">
									<c:set var="colspanPeptidesAdded" value="${ colspanPeptidesAdded + 1 }" />
								</c:if>
												
								<td colspan="${ 7 + colspanPeptidesAdded }" align="center" class=" child_data_container_jq ">
															
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
