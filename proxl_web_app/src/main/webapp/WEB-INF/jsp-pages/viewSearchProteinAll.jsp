<%@page import="org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants"%>
<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

 <c:set var="pageTitle">Proteins - <c:out value="${ headerProject.projectTblData.title }"></c:out></c:set>

 <c:set var="pageBodyClass" >project-page</c:set>

 <c:set var="helpURLExtensionForSpecificPage" >en/latest/using/protein.html</c:set>

 <c:set var="headerAdditions">

		<script type="text/javascript" src="js/libs/jquery.tablesorter_Modified.js"></script> 
		<script type="text/javascript" src="js/libs/jquery.qtip.min.js"></script>
				
		
<%--  Start of Lorikeet Core Parts --%>		

		<script src="js/libs/jquery-ui-1.12.1.min.js"></script>
		
<%--  End of Lorikeet Core Parts --%>		

		
		<script type="text/javascript" src="js/libs/snap.svg-min.js"></script> <%--  Used by lorikeetPageProcessing.js --%>

		<script type="text/javascript" src="js/libs/spin.min.js"></script> 
		
				
				<%-- 
					The Struts Action for this page must call GetProteinNamesTooltipConfigData
					This include is required on this page:
					/WEB-INF/jsp-includes/proteinNameTooltipDataForJSCode.jsp
				  --%>

	
		<link rel="stylesheet" href="css/tablesorter.css" type="text/css" media="print, projection, screen" />
		<link type="text/css" rel="stylesheet" href="css/jquery.qtip.min.css" />

		<%--  some classes in this stylesheet collide with some in the lorikeet file since they are set to specific values for lorikeet drag and drop --%>
		<%-- 
		<link REL="stylesheet" TYPE="text/css" HREF="css/jquery-ui-1.10.2-Themes/ui-lightness/jquery-ui.min.css">
		--%>
		<link REL="stylesheet" TYPE="text/css" HREF="css/lorikeet.css">



</c:set>



<%@ include file="/WEB-INF/jsp-includes/header_main.jsp" %>
		
	<%@ include file="/WEB-INF/jsp-includes/body_section_data_pages_after_header_main.jsp" %>
		

	<input type="hidden" id="project_id" value="<c:out value="${ project_id }"></c:out>"> 
	
	<%--  Put Project_Search_Id on the page for the JS code --%>
	<input type="hidden" class=" project_search_id_jq " value="<c:out value="${ projectSearchId }"></c:out>">
		
		<%--  loading spinner location --%>
	<div style="opacity:1.0;position:absolute;left:50%;top:50%; z-index: 20001" id="coverage-map-loading-spinner" style="" ></div>
			
	<%--  used by createTooltipForProteinNames.js --%>
	<%@ include file="/WEB-INF/jsp-includes/proteinNameTooltipDataForJSCode.jsp" %>
	
		<%@ include file="/WEB-INF/jsp-includes/defaultPageViewFragment.jsp" %>
		
		<%@ include file="/WEB-INF/jsp-includes/viewPsmsLoadedFromWebServiceTemplateFragment.jsp" %>
		<%@ include file="/WEB-INF/jsp-includes/viewPsmPerPeptideLoadedFromWebServiceTemplateFragment.jsp" %>
		
		<%@ include file="/WEB-INF/jsp-includes/viewReportedPeptidesForProteinAllLoadedFromWebServiceTemplateFragment.jsp" %>
		
		
			<%@ include file="/WEB-INF/jsp-includes/lorikeet_overlay_section.jsp" %>	
		
		<div class="overall-enclosing-block">
	
			<h2 style="margin-bottom:5px;">List search proteins:</h2>
	
			<div  class=" navigation-links-block "> 

				<%-- Navigation link to QC Page --%>
				
				<%@ include file="/WEB-INF/jsp-includes/qc_NavLinks.jsp" %>
				
				[<a class="tool_tip_attached_jq" data-tooltip="View peptides" 
					href="<proxl:defaultPageUrl pageName="/peptide" projectSearchId="${ search.projectSearchId }"
						>peptide.do?projectSearchId=<bean:write name="search" property="projectSearchId" 
						/>&queryJSON=<c:out value="${ peptidePageQueryJSON }" escapeXml="false" 
						></c:out></proxl:defaultPageUrl>"
						>Peptide View</a>]
						 
				[<a class="tool_tip_attached_jq" data-tooltip="View protein coverage report" 
					href="<proxl:defaultPageUrl pageName="/proteinCoverageReport" projectSearchId="${ search.projectSearchId }">proteinCoverageReport.do?<bean:write name="queryString" /></proxl:defaultPageUrl>"
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
			
			
			
			<table id="search_details_and_main_filter_criteria_main_page_root" style=" border-width: 0px; display: none; ">
				
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
							onchange="if ( window.defaultPageView ) { window.defaultPageView.searchFormChanged_ForDefaultPageView(); } ; if ( window.saveView_dataPages ) { window.saveView_dataPages.searchFormChanged_ForSaveView(); }"
							value="<%= PeptideViewLinkTypesConstants.CROSSLINK_PSM %>"   >
						crosslinks
					  </label>
					  <label >
						<input type="checkbox" class=" link_type_jq " 
							onchange="if ( window.defaultPageView ) { window.defaultPageView.searchFormChanged_ForDefaultPageView(); } ; if ( window.saveView_dataPages ) { window.saveView_dataPages.searchFormChanged_ForSaveView(); }"
							value="<%= PeptideViewLinkTypesConstants.LOOPLINK_PSM %>" >
						looplinks
					  </label> 
					  <label >
						<input type="checkbox" class=" link_type_jq " 
							onchange="if ( window.defaultPageView ) { window.defaultPageView.searchFormChanged_ForDefaultPageView(); } ; if ( window.saveView_dataPages ) { window.saveView_dataPages.searchFormChanged_ForSaveView(); }"
							value="<%= PeptideViewLinkTypesConstants.UNLINKED_PSM %>" >
						 unlinked
					  </label>

					</td>
				</tr>
				

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
						<select name="excludedProteins" multiple="multiple" id="excludeProtein" onchange=" if ( window.defaultPageView ) { window.defaultPageView.searchFormChanged_ForDefaultPageView(); } ; if ( window.saveView_dataPages ) { window.saveView_dataPages.searchFormChanged_ForSaveView(); }" >  
							<%-- Populated in JS --%>
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
						<%@ include file="/WEB-INF/jsp-includes/savePageViewButtonFragment.jsp" %>
						<%@ include file="/WEB-INF/jsp-includes/sharePageURLShortenerButtonFragment.jsp" %>

					</td>
				</tr>
			</table>
			
			<%--  <div> to contain all the remaining items above the main data display table --%>
			<div id="main_items_below_filter_criteria_table_above_main_display_table" style="display: none;"> 
							
				<div style="height: 10px;">&nbsp;</div>
							
				<h3 style="display:inline;">Proteins (<span id="numProteins_copy1" ></span>):</h3>
				<div style="display:inline;">
					[<a class="tool_tip_attached_jq" data-tooltip="View crosslinks" 
							href="<proxl:defaultPageUrl pageName="/crosslinkProtein" projectSearchId="${ search.projectSearchId }">crosslinkProtein.do?<bean:write name="queryString" /></proxl:defaultPageUrl>"
							>View Crosslinks</a>]
					[<a class="tool_tip_attached_jq" data-tooltip="View looplinks" 
							href="<proxl:defaultPageUrl pageName="/looplinkProtein" projectSearchId="${ search.projectSearchId }">looplinkProtein.do?<bean:write name="queryString" /></proxl:defaultPageUrl>"
							>View Looplinks</a>]
					[<a class="tool_tip_attached_jq" data-tooltip="Download all proteins as tab-delimited text" 
						href="downloadMergedProteinsAll.do?<bean:write name="queryString" />"
						>Download Data (<span id="numProteins_copy2" ></span>)</a>]
				</div>
				
				<%--  Block for user choosing which annotation types to display  --%>
				<%@ include file="/WEB-INF/jsp-includes/annotationDisplayManagementBlock.jsp" %>

				
			</div> <%--  END: <div id="main_items_below_filter_criteria_table_above_main_display_table">  --%>
			
				<%--  Main Table of Protein Data will be placed here --%>
			<div id="proteins_from_webservice_container" style="display: none;"></div>

		</div>
	


		<script type="text/javascript" src="static/js_generated_bundles/data_pages/proteinsAllView-bundle.js?x=${cacheBustValue}"></script>
	
		
<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>
