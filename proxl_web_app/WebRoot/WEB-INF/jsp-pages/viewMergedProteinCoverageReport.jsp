<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

 <c:set var="pageTitle">View Search</c:set>

 <c:set var="pageBodyClass" >project-page</c:set>

 <c:set var="headerAdditions">

		<script type="text/javascript" src="${ contextPath }/js/handleServicesAJAXErrors.js?x=${cacheBustValue}"></script> 
		
		
		<!-- Handlebars templating library   -->
		
		<%--  
		<script type="text/javascript" src="${ contextPath }/js/libs/handlebars-v2.0.0.js"></script>
		--%>
		
		<!-- use minimized version  -->
		<script type="text/javascript" src="${ contextPath }/js/libs/handlebars-v2.0.0.min.js"></script>

		
		<script type="text/javascript" src="${ contextPath }/js/libs/jquery.tablesorter.min.js"></script> 
		<script type="text/javascript" src="${ contextPath }/js/libs/jquery.qtip.min.js"></script>

				<%-- 
					The Struts Action for this page must call GetProteinNamesTooltipConfigData
					This include is required on this page:
					/WEB-INF/jsp-includes/proteinNameTooltipDataForJSCode.jsp
				  --%>
		<script type="text/javascript" src="${ contextPath }/js/createTooltipForProteinNames.js?x=${cacheBustValue}"></script>

		<script type="text/javascript" src="${ contextPath }/js/defaultPageView.js?x=${cacheBustValue}"></script>
		
		<script type="text/javascript" src="${ contextPath }/js/toggleVisibility.js?x=${cacheBustValue}"></script>
		
		<script type="text/javascript" src="${ contextPath }/js/psmPeptideCutoffsCommon.js?x=${cacheBustValue}"></script>
		<script type="text/javascript" src="${ contextPath }/js/psmPeptideAnnDisplayDataCommon.js?x=${cacheBustValue}"></script>
				
		<script type="text/javascript" src="${ contextPath }/js/webserviceDataParamsDistribution.js?x=${cacheBustValue}"></script>
			
		<script type="text/javascript" src="${ contextPath }/js/viewProteinPageCommonCrosslinkLooplinkCoverageSearchMerged.js?x=${cacheBustValue}"></script>
		
		<script type="text/javascript" src="${ contextPath }/js/viewProteinCoverageReport.js?x=${cacheBustValue}"></script>
		
		<link rel="stylesheet" href="${ contextPath }/css/tablesorter.css" type="text/css" media="print, projection, screen" />
		<link type="text/css" rel="stylesheet" href="${ contextPath }/css/jquery.qtip.min.css" />




</c:set>



<%@ include file="/WEB-INF/jsp-includes/header_main.jsp" %>

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

			<div style="margin-bottom:20px;">
				[<a class="tool_tip_attached_jq" data-tooltip="View peptides" href="${ contextPath }/${ peptideNav }.do?<bean:write name="queryString" />">Peptide View</a>]
		
				[<a class="tool_tip_attached_jq" data-tooltip="View proteins" href="${ contextPath }/${ proteinNav }.do?<bean:write name="queryString" />">Protein View</a>]
				
				<%-- Navigation links to Merged Image and Merged Structure --%>
				
				<%@ include file="/WEB-INF/jsp-includes/imageAndStructureNavLinks.jsp" %>

			</div>
	
			<%-- query JSON in field outside of form for input to Javascript --%>
				
			<input type="hidden" id="query_json_field_outside_form" value="<c:out value="${ queryJSONToForm }" ></c:out>" > 
	
			<%--  A block outside any form for PSM Peptide cutoff JS code --%>
			<%@ include file="/WEB-INF/jsp-includes/psmPeptideCutoffBlock_outsideAnyForm.jsp" %>


			<c:choose>
			 <c:when test="${ mergedPage }"> 	

				<form action="mergedProteinCoverageReport.do" method="get" id="form_get_for_updated_parameters">

					<logic:iterate name="searches" id="search">
						<input type="hidden" name="searchIds" value="<bean:write name="search" property="id" />">
					</logic:iterate>

					<input type="hidden" name="queryJSON" id="query_json_field" />
								
					<%--  A block in the submitted form for PSM Peptide cutoff JS code --%>
					<%@ include file="/WEB-INF/jsp-includes/psmPeptideCutoffBlock_inSubmitForm.jsp" %>
	
				</form>
		

			 </c:when>
			 <c:otherwise>
				<form action="proteinCoverageReport.do" method="get" id="form_get_for_updated_parameters">
										
					<logic:iterate name="searches" id="search">
						<input type="hidden" name="searchId" value="<bean:write name="search" property="id" />">
					</logic:iterate>
				
					<input type="hidden" name="queryJSON" id="query_json_field" />
				
					<%--  A block in the submitted form for PSM Peptide cutoff JS code --%>
					<%@ include file="/WEB-INF/jsp-includes/psmPeptideCutoffBlock_inSubmitForm.jsp" %>
	
				</form>
			 
			 </c:otherwise>
			</c:choose>
			
			
						
			
						
			<%-- WAS		
			

			<html:form action="viewProteinCoverageReport" method="get" styleId="form_get_for_updated_parameters">
							
				<html:hidden property="searchId"/>
			
								
			--%>
			
			
			<%--
		Moved JS call to the "Update" button
		 			
			<form action="javascript:viewProteinCoverageReportPageCode.updatePageForFormParams()" method="get" > 
			
				--%>	 <%-- id="form_get_for_updated_parameters" --%>
			
			
			<table style="border-width:0px;">
					
				<%--  Set to false to not show color block before search for key --%>
				<c:set var="showSearchColorBlock" value="${ false }" />
				
				<%--  Include file is dependent on containing loop having varStatus="searchVarStatus"  --%>
				<%@ include file="/WEB-INF/jsp-includes/searchDetailsBlock.jsp" %>

				<tr>
					<td>Exclude links with:</td>
					<td>
						 <label><span style="white-space:nowrap;" >
							<input type="checkbox" id="filterNonUniquePeptides" onchange=" defaultPageView.searchFormChanged_ForDefaultPageView();" > 					
						 	 no unique peptides
						 </span></label>
						 <label><span style="white-space:nowrap;" >
							<input type="checkbox" id="filterOnlyOnePSM"  onchange=" defaultPageView.searchFormChanged_ForDefaultPageView();" > 					
						 	 only one PSM
						 </span></label>
						 <label><span style="white-space:nowrap;" >
							<input type="checkbox" id="filterOnlyOnePeptide"  onchange=" defaultPageView.searchFormChanged_ForDefaultPageView();" > 					
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
						  <html:multibox property="excludeTaxonomy" styleClass="excludeTaxonomy_jq" onchange=" defaultPageView.searchFormChanged_ForDefaultPageView();" >
						   <bean:write name="taxonomy" property="key"/> 
						  </html:multibox> 
						   <span style="font-style:italic;"><bean:write name="taxonomy" property="value"/></span>
						 </label> 
--%>						 
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
						<%--  shortened property from "excludeProtein" to "excP" to shorten the URL  --%>
						<%-- TODO   TEMP
						<html:select property="excP" multiple="true" styleId="excludeProtein" onchange=" defaultPageView.searchFormChanged_ForDefaultPageView();" >
							<html:options collection="proteins" property="proteinSequenceObject.proteinSequenceId" labelProperty="name" />
						</html:select>
						--%>
						
						
						<%--  New version:  
						
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

						<c:set var="UpdateButtonText" value="Update"/>
						
						<input type="button" value="${ UpdateButtonText }"  onclick="viewProteinCoverageReportPageCode.updatePageForFormParams()" >

						<c:if test="${ not mergedPage }"> 	
							
							<c:set var="searchId" value="${ search.id }"/>	
	
							<c:set var="page_JS_Object" value="viewSearchProteinPageCommonCrosslinkLooplinkCoverage"/>
						
							<%@ include file="/WEB-INF/jsp-includes/defaultPageViewButtonFragment.jsp" %>
						
						</c:if>						
					</td>
				</tr>
			
			</table>
			
<%-- 			
			</form>
--%>
	
			<h3 style="display:inline;">Protein Coverage Report:</h3>

			<div style="display:inline;">
				[<a class="tool_tip_attached_jq" data-tooltip="Download as tab-delimited text" href="${ contextPath }/downloadProteinCoverageReport.do?<bean:write name="mergedQueryString" />">Download Coverage Report</a>]
			</div>

			<c:set var="coveragePageForAnnDispMgmt" value="${ true }"/>
			
			<%--  Block for user choosing which annotation types to display  --%>
			<%@ include file="/WEB-INF/jsp-includes/annotationDisplayManagementBlock.jsp" %>

			<%--  Create via javascript the parts that will be above the main table --%>
			<script type="text/javascript">

			//  If object exists, call function on it now, otherwise call the function on document ready
			if ( window.viewProteinCoverageReportPageCode ) {
				window.viewProteinCoverageReportPageCode.createPartsAboveMainTable();
			} else {

				$(document).ready(function() 
				    { 
					   setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else
						  
						   window.viewProteinCoverageReportPageCode.createPartsAboveMainTable();
					   },10);
				    } 
				); // end $(document).ready(function() 
			}
									
			</script>
			
			
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
						
					<logic:iterate id="coverage" name="proteinCoverageData">

						<tr>
							<td><bean:write name="coverage" property="name" /></td>
							<td><bean:write name="coverage" property="numResidues" /></td>
							<td>
								<bean:write name="coverage" property="sequenceCoverage" />
							</td>
							<td><bean:write name="coverage" property="numLinkableResidues" /></td>
							<td><bean:write name="coverage" property="numLinkableResiduesCovered" /></td>
							<td><bean:write name="coverage" property="linkableResiduesCoverageFmt" /></td>

							<td><bean:write name="coverage" property="numMLCResidues" /></td>
							<td><bean:write name="coverage" property="MLCSequenceCoverage" /></td>
							
							<td><bean:write name="coverage" property="numLCResidues" /></td>
							<td><bean:write name="coverage" property="LCSequenceCoverage" /></td>
							
							<td><bean:write name="coverage" property="monolinkedResidues" /></td>
							<td><bean:write name="coverage" property="MSequenceCoverage" /></td>
							
							<td><bean:write name="coverage" property="looplinkedResidues" /></td>
							<td><bean:write name="coverage" property="LSequenceCoverage" /></td>
							
							<td><bean:write name="coverage" property="crosslinkedResidues" /></td>
							<td><bean:write name="coverage" property="CSequenceCoverage" /></td>
							
						</tr>



					</logic:iterate>
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
	

<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>