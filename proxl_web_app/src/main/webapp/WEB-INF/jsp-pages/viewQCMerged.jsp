<%@page import="org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants"%>
<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

<%-- viewQCMerged.jsp --%>

<%--   
		QC page for Merged Searches
		

			!!!!!!!!!!!!!!!   Warning:   request attribute 'cutoffsAppliedOnImportAllAsString' is NOT set in Action when this page is rendered.
			
 --%>

<%--  In searchDetailsBlock.jsp, suppress display of link "Change searches"  --%>
<%--
<c:set var="doNotDisplayChangeSearchesLink" value="${ true }"/>
--%>

 <c:set var="pageTitle">View QC - <c:out value="${ headerProject.projectTblData.title }"></c:out></c:set>

 <c:set var="pageBodyClass" >project-page view-qc-page</c:set>
 
 <%--  Additions to Google Chart Package Load. Used in header_main.jsp.  Requires starting ',' --%>
 <%--  Not currently Used: "scatter" Material Design Scatter Plot --%>
 <%--  
 <c:set var="googleChartPackagesLoadAdditions">,"scatter"</c:set>
 --%>
 
  <c:set var="headerAdditions">
 
		<script type="text/javascript" src="${ contextPath }/js/libs/base64.js"></script> 

		<%--  Compression --%>
		
		<%--  Used by lz-string.min.js --%>
		<script type="text/javascript" src="${ contextPath }/js/libs/lz-string/base64-string.js"></script>
		
		<script type="text/javascript" src="${ contextPath }/js/libs/lz-string/lz-string.min.js"></script>
		
		<%--  Non-Minified version --%>
		<%-- 
		<script type="text/javascript" src="${ contextPath }/js/libs/lz-string/lz-string.js"></script>
		--%>
		
		<script src="${contextPath}/js/libs/jquery-ui-1.10.4.min.js"></script>
					
		<%--  On this page Snap used by qcMergedPageMain.js for Snap.hsb2rgb(...) to get color --%>				
		<script type="text/javascript" src="${ contextPath }/js/libs/snap.svg-min.js"></script> <%--  Used by lorikeetPageProcessing.js --%>
		
		<!-- Handlebars templating library   -->
		
		<%--  
		<script type="text/javascript" src="${ contextPath }/js/libs/handlebars-v2.0.0.js"></script>
		--%>
		
		<!-- use minimized version  -->
		<script type="text/javascript" src="${ contextPath }/js/libs/handlebars-v2.0.0.min.js"></script>

		<script type="text/javascript" src="${ contextPath }/js/handleServicesAJAXErrors.js?x=${cacheBustValue}"></script> 
		 
		<script type="text/javascript" src="${ contextPath }/js/toggleVisibility.js?x=${cacheBustValue}"></script>
		<script type="text/javascript" src="${ contextPath }/js/sharePageURLShortener.js?x=${cacheBustValue}"></script>
		
		<script type="text/javascript" src="${ contextPath }/js/spinner.js?x=${cacheBustValue}"></script> 
		
		<script type="text/javascript" src="${ contextPath }/js/psmPeptideCutoffsCommon.js?x=${cacheBustValue}"></script>
		<script type="text/javascript" src="${ contextPath }/js/webserviceDataParamsDistribution.js?x=${cacheBustValue}"></script>
		
		<script type="text/javascript" src="${ contextPath }/js/psmPeptideAnnDisplayDataCommon.js?x=${cacheBustValue}"></script>
		
		<script type="text/javascript" src="${ contextPath }/js/download-string-as-file.js?x=${cacheBustValue}"></script>
		
		<script type="text/javascript" src="${ contextPath }/js/qc_pages_Single_Merged_Common.js?x=${cacheBustValue}"></script>
		
		<script type="text/javascript" src="${ contextPath }/js/qcChart_Download_Help_HTMLBlock.js?x=${cacheBustValue}"></script>
		
		<script type="text/javascript" src="${ contextPath }/js/mergedPeptideProteinSearchesListVennDiagramSection.js?x=${cacheBustValue}"></script>
		

		<script type="text/javascript" src="${ contextPath }/js/qcMergedPageChartSummaryStatistics.js?x=${cacheBustValue}"></script>
		<script type="text/javascript" src="${ contextPath }/js/qcMergedPageChartDigestionStatistics.js?x=${cacheBustValue}"></script>
		<script type="text/javascript" src="${ contextPath }/js/qcMergedPageChartScanFileStatistics.js?x=${cacheBustValue}"></script>
		
		<script type="text/javascript" src="${ contextPath }/js/qcMergedPageChartChargeStateStatistics.js?x=${cacheBustValue}"></script>
		<script type="text/javascript" src="${ contextPath }/js/qcMergedPageChart_M_Over_Z_Statistics_PSM.js?x=${cacheBustValue}"></script>
		<script type="text/javascript" src="${ contextPath }/js/qcMergedPageChart_Peptide_Length_Vs_PSM_Count_Boxplot.js?x=${cacheBustValue}"></script>
		
		<script type="text/javascript" src="${ contextPath }/js/qcMergedPageChart_PPM_Error_PSM.js?x=${cacheBustValue}"></script>
		<script type="text/javascript" src="${ contextPath }/js/qcMergedPageChart_PSM_Per_Modification.js?x=${cacheBustValue}"></script>
		<script type="text/javascript" src="${ contextPath }/js/qcMergedPageChart_Peptide_Lengths.js?x=${cacheBustValue}"></script>
		
		<script type="text/javascript" src="${ contextPath }/js/qcMergedPageSectionSummaryStatistics.js?x=${cacheBustValue}"></script>
		<script type="text/javascript" src="${ contextPath }/js/qcMergedPageSectionDigestionStatistics.js?x=${cacheBustValue}"></script>
		<script type="text/javascript" src="${ contextPath }/js/qcMergedPageSectionScanFileStatistics.js?x=${cacheBustValue}"></script>
		<script type="text/javascript" src="${ contextPath }/js/qcMergedPageSection_PSM_Level_Statistics.js?x=${cacheBustValue}"></script>
		<script type="text/javascript" src="${ contextPath }/js/qcMergedPageSection_PSM_Error_Estimates.js?x=${cacheBustValue}"></script>
		<script type="text/javascript" src="${ contextPath }/js/qcMergedPageSectionModificationStatistics.js?x=${cacheBustValue}"></script>
		<script type="text/javascript" src="${ contextPath }/js/qcMergedPageSection_Peptide_Level_Statistics.js?x=${cacheBustValue}"></script>
		
		<script type="text/javascript" src="${ contextPath }/js/qcMergedPageMain.js?x=${cacheBustValue}"></script>
				<%-- 
					The Struts Action for this page must call GetProteinNamesTooltipConfigData
					This include is required on this page:
					/WEB-INF/jsp-includes/proteinNameTooltipDataForJSCode.jsp
				  --%>

		<style>
			.count-display { padding-left: 10px; padding-right: 10px; text-align: right;  }
		</style>				
</c:set>



<%@ include file="/WEB-INF/jsp-includes/header_main.jsp" %>
	
	<div class="overall-enclosing-block">
	
	<input type="hidden" id="annotation_data_webservice_base_url" value="<c:out value="${ annotation_data_webservice_base_url }"></c:out>"> 

	<input type="hidden" id="project_id" value="<c:out value="${ project_id }"></c:out>"> 
	
	
	<c:forEach var="search" items="${ searches }">
	
		<%--  Put Project_Search_Ids on the page for the JS code --%>
		<input type="hidden" class=" project_search_id__search_id_pair_jq " value="<c:out value="${ search.projectSearchId }"></c:out>:<c:out value="${ search.searchId }"></c:out>">
	</c:forEach>
	
	<input type="hidden" id="userOrderedProjectSearchIds" value="<c:out value="${ userOrderedProjectSearchIds }"></c:out>"> 
	
	<c:forEach var="projectSearchId" items="${ projectSearchIds }">
	
		<%--  Put Project_Search_Ids on the page for the JS code --%>
		<input type="hidden" class=" project_search_id_jq " value="<c:out value="${ projectSearchId }"></c:out>">
	</c:forEach>
	
	<c:if test="${ not empty onlySingleProjectSearchId }">
	
		<input type="hidden" id="viewSearchPeptideDefaultPageUrl" 
			value="<proxl:defaultPageUrl pageName="/peptide" projectSearchId="${ onlySingleProjectSearchId }"></proxl:defaultPageUrl>">
		<input type="hidden" id="viewSearchCrosslinkProteinDefaultPageUrl" 
			value="<proxl:defaultPageUrl pageName="/crosslinkProtein" projectSearchId="${ onlySingleProjectSearchId }"></proxl:defaultPageUrl>">
		<input type="hidden" id="viewProteinCoverageReportDefaultPageUrl" 
			value="<proxl:defaultPageUrl pageName="/proteinCoverageReport" projectSearchId="${ onlySingleProjectSearchId }"></proxl:defaultPageUrl>">
		<input type="hidden" id="viewMergedImageDefaultPageUrl" 
			value="<proxl:defaultPageUrl pageName="/image" projectSearchId="${ onlySingleProjectSearchId }"></proxl:defaultPageUrl>">
		<input type="hidden" id="viewMergedStructureDefaultPageUrl" 
			value="<proxl:defaultPageUrl pageName="/structure" projectSearchId="${ onlySingleProjectSearchId }"></proxl:defaultPageUrl>">
	</c:if>
	
	<c:choose>
	 <c:when test="${ anySearchesHaveScanData }">
	 	<script id="anySearchesHaveScanDataYes"></script>
	 </c:when>
	 <c:otherwise>
	 	<script id="anySearchesHaveScanDataNo"></script>
	 </c:otherwise>
	</c:choose>
			  
					
	
		<div>
	
			<h2 style="margin-bottom:5px;">View <c:if test="${ empty onlySingleProjectSearchId }">merged </c:if>QC data:</h2>
	
			<div id="navigation-links"  class=" navigation-links-block ">
			
				<span id="navigation_links_except_structure"></span>

				<c:choose>
				 <c:when test="${ showStructureLink }">
					
					<span id="structure_viewer_link_span"></span>
	
				 </c:when>
				 <c:otherwise>
					<%@ include file="/WEB-INF/jsp-includes/structure_link_non_link.jsp" %>
				 </c:otherwise>
				</c:choose>
								
			</div>
				
			<%--  Hidden fields to pass data to JS --%>
			
			<input type="hidden" id="cutoffValuesRootLevelCutoffDefaults" value="<c:out value="${ cutoffValuesRootLevelCutoffDefaults }"></c:out>"> 
			
				<%--  A block outside any form for PSM Peptide cutoff JS code --%>
				<%@ include file="/WEB-INF/jsp-includes/psmPeptideCutoffBlock_outsideAnyForm.jsp" %>
			
				<%--  A block in the submitted form for PSM Peptide cutoff JS code --%>
				<%--   In the Merged Image and Merged Structure Pages, this will not be in any form  --%>
				<%@ include file="/WEB-INF/jsp-includes/psmPeptideCutoffBlock_inSubmitForm.jsp" %>

	
			<table style="border-width:0px;">

				<%--  Set to true TO show color block before search for key --%>
				<c:set var="showSearchColorBlock" value="${ true }" />
				
				<%--  Set to true to NOT set color for color block before search for key --%>
				<c:set var="do_NOT_SetSearchColorBlockColor" value="${ true }" />
				
				<%--  Include file is dependent on containing loop having varStatus="searchVarStatus"  --%>
				<%@ include file="/WEB-INF/jsp-includes/searchDetailsBlock.jsp" %>


				<tr>
					<td>Type Filter:</td>
					<td colspan="2">
						<%--  Update TestAllWebLinkTypesSelected if add another option --%>

					  <label >
						<input type="checkbox" class=" link_type_jq " id="link_type_crosslink_selector"
							 <%-- checked="checked" TODO TEMP --%>
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

					  <script type="text/text" id="link_type_crosslink_constant"
					  		><%= PeptideViewLinkTypesConstants.CROSSLINK_PSM %></script>
					  <script type="text/text" id="link_type_looplink_constant"
					  		><%= PeptideViewLinkTypesConstants.LOOPLINK_PSM %></script>
					  <script type="text/text" id="link_type_unlinked_constant"
					  		><%= PeptideViewLinkTypesConstants.UNLINKED_PSM %></script>
					</td>
				</tr>
				<tr>
					<td valign="top" style="white-space: nowrap;">Modification Filter:</td>
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
						<%@ include file="/WEB-INF/jsp-includes/sharePageURLShortenerOverlayFragment.jsp" %>
					
						<c:set var="UpdateButtonText" value="Update From Database"/>
						
						<input id="update_from_database_button"
							type="button" value="${ UpdateButtonText }" > 

						<%@ include file="/WEB-INF/jsp-includes/sharePageURLShortenerButtonFragment.jsp" %>
					</td>
				</tr>
							
			</table>
			
		</div>
							
			<%--  Block for user choosing which annotation types to display  --%>
			<%@ include file="/WEB-INF/jsp-includes/annotationDisplayManagementBlock.jsp" %>
	
		<hr>
	
		<input type="button" value="Expand All" id="qc_page_expand_all_button">
		 	
		<%--  Summary level Statistics --%>
	
		<div >

			<%--  Help Icon tooltip HTML for these charts --%>
			<script id="summary_block_help_tooltip_psm_count_chart" type="text/text"
				><div  For each class of peptide, the number of PSMs for each search that meet the current filtering criteria.</div></script>
			<script id="summary_block_help_tooltip_peptide_count_chart" type="text/text"
				><div class="">For each class of peptide, the number of distinct peptide identifications for each search that meet the current filtering criteria.</div></script>
			<script id="summary_block_help_tooltip_protein_count_chart" type="text/text"
				><div class="">For each class of peptide, the number of distinct proteins for each search for which peptides were found that meet the current filtering criteria.</div></script>

		  <div class="top-level-container qc_top_level_container_jq" >
			
			<div  class="collapsable-link-container top-level-collapsable-link-container" > 
				<a id="summary_collapse_link" href="javascript:" class="top-level-collapsable-link" 
						style="display: none;"
					><img  src="${ contextPath }/images/icon-collapse.png"></a>
				<a id="summary_expand_link" href="javascript:" class="top-level-collapsable-link" 
					><img  src="${ contextPath }/images/icon-expand.png"></a>
			</div>
			<div class="top-level-label">
			  Summary Statistics
			</div>

			<div class="top-level-label-bottom-border" ></div>
								
			<div id="summary_display_block" class="project-info-block" style="display: none;"  >
			
	 		  <table  id="Summary_Statistics_CountsBlock" class="table-no-border-no-cell-spacing-no-cell-padding" >
			  </table>			
			</div>
		  </div>

		</div>  <%--  END:  Summary Statistics --%>

		<%--  Digestion Statistics --%>
		
		<div >
			<%--  Help Icon tooltip HTML for these charts --%>
			<script id="digestion_block_help_tooltip_peptides_with_missed_cleavage_chart" type="text/text"
				><div class="">For each class of peptide, the fraction of distinct peptide identifications in each search that meet the current filtering criteria that contain at least one missed cleavage.</div></script>
			<script id="digestion_block_help_tooltip_missed_cleavage_chart" type="text/text"
				><div class="">For each class of peptide, the total number of missed cleavages in each search divided by the total number of distinct peptides in each search. (For peptides that meet the current filtering criteria.)</div></script>
			<script id="digestion_block_help_tooltip_missed_cleavage_psm_count_chart" type="text/text"
				><div class="">For each class of peptide, the fraction of PSMs in each search that meet the current filtering criteria that match a peptide that contains at least one missed cleavage.</div></script>

		  <div class="top-level-container qc_top_level_container_jq" >
			
			<div  class="collapsable-link-container top-level-collapsable-link-container " > 
				<a id="digestion_collapse_link" href="javascript:" class="top-level-collapsable-link" 
						style="display: none;"
					><img  src="${ contextPath }/images/icon-collapse.png"></a>
				<a id="digestion_expand_link" href="javascript:" class="top-level-collapsable-link " 
					><img  src="${ contextPath }/images/icon-expand.png"></a>
			</div>
			<div class="top-level-label">
			  Digestion Statistics
			</div>

			<div class="top-level-label-bottom-border" ></div>
								
			<div id="digestion_display_block" class="project-info-block" style="display: none;" >
			
 		      <table  id="missingCleavageReportedPeptidesCountBlock" 
 		      	class="table-no-border-no-cell-spacing-no-cell-padding chart_group_container_table_jq " style="">
			  </table>
			</div>
		  </div>

		</div>  <%--  END:  Digestion Statistics --%>
			  

		<%--  Scan level Statistics --%>
	
		<div >

<%--  Help Icon tooltip HTML for these charts --%>
<script id="scan_level_block_help_tooltip_overall_statistics_section" type="text/text">
<div>
<div style="margin-bottom: 10px;">A table comparing ion current summary statistics for the scan files uploaded for each search. </div>
<div style="margin-bottom: 10px;">"Total ion current" is calculated as the sum of all peak intensities of the respective scan type in each search. </div>
<div style="margin-bottom: 10px;">"Number of scans" is the total number of scans in each search for the respective type. </div>
<div style="margin-bottom: 10px;">"MS2 scans with a PSM meeting cutoffs" is the number of MS2 scans in each search that resulted in a PSM meeting the current filtering criteria.</div> 
<div >The percentage indicates the percentage of all MS2 scans in the respective search that resulted in a PSM meeting the current filtering criteria.</div>
</div>
</script>

		  <div class="top-level-container qc_top_level_container_jq" >
			
			<div  class="collapsable-link-container top-level-collapsable-link-container " > 
				<a id="scan_level_collapse_link" href="javascript:" class="top-level-collapsable-link " 
						style="display: none;"
					><img  src="${ contextPath }/images/icon-collapse.png"></a>
				<a id="scan_level_expand_link" href="javascript:" class="top-level-collapsable-link " 
					><img  src="${ contextPath }/images/icon-expand.png"></a>
			
			</div>
			<div class="top-level-label">
			  Scan File Statistics
			</div>

			<div class="top-level-label-bottom-border" ></div>

								
			<div id="scan_level_display_block" class="project-info-block " style="display: none;"  > <%--  --%>

			  <div id="scan_file_files_loading_block">
			  	Loading scan data
			  </div>

			  <div id="scan_file_no_files_block" style="display: none;">
			  	No Scan Data
			  </div>
			  
			  <!--  Everything displayed once a scan file is selected -->
			  <div id="scan_file_selected_file_statistics_display_block" style="display: none;">
				
	 		    <table class="table-no-border-no-cell-spacing-no-cell-padding" style="">
	 		     <tr>
	 		      <td style="padding: 4px;">

				    <div style=" position: relative;" class="chart-standard-container-div  "> <!-- qc-data-block Scan File Statistics outer block -->

						<!-- Help Image for 'Scan File Statistics' section -->
					 <div id="scan_file_overall_statistics_help_block" class="  " style="position: absolute; top: 4px; right: 4px;">
						  <div class=" help-image-for-qc-chart-block ">
						  	<img src="images/icon-help.png" class=" help-image-for-qc-chart help_image_for_qc_chart_jq ">
						  </div>
					 </div>
				    
				     <div class="" >
	
					  <h3  style="text-align: center; font-size: 22px; margin-top: 10px; margin-bottom: 10px;">
					  	Scan Statistics
					  </h3>

					    <div id="scan_file_overall_statistics_loading_block" 
					  	 		style="font-size: 20px; font-weight: bold; margin-top: 95px; text-align: center; display: none;">
					  		<span class=" message_text_jq ">Loading Data</span>
					    </div>
					    <div id="scan_file_overall_statistics_no_data_block" 
					  	 		style="font-size: 20px; font-weight: bold; margin-top: 95px; text-align: center; display: none;">
					  		<span class=" message_text_jq ">No Data Found</span>
					    </div>
	
					  <!-- style following table -->
					  <style > 
					    #scan_file_overall_statistics_block td { font-size: 14px; font-weight: bold; padding-bottom: 5px; }
					    #scan_file_overall_statistics_block .scan-file-overall-statistics-label-cell { padding-right: 30px; padding-left: 15px;}
					    #scan_file_overall_statistics_block .scan-file-overall-statistics-data-cell { text-align: right; padding-right: 15px; }
					    #scan_file_overall_statistics_block .ms2-scans-psm-cutoff-label { font-size: 16px; }
					    #scan_file_overall_statistics_block .search-id-row td { padding-bottom: 14px; }
					    #scan_file_overall_statistics_block .scan-file-overall-statistics-block-end-row td { padding-bottom: 14px; }
					  </style>
					  
					  <%--  Container to put the table with data into --%>
					  <div id="scan_file_overall_statistics_block" style="">
					  
					  </div>


	<%--  Handlebars template for displaying Scan File overall statistics  --%>		

<script id="scan_file_overall_statistics_template"  type="text/x-handlebars-template">
			
		<%--  Use of {{#each ... }}  For each search --%>
  
	  <table class="table-no-border-no-cell-spacing-no-cell-padding" 
	  		style="margin-left: auto; margin-right: auto; padding-bottom: 5px;">
	   <tr class="search-id-row">
	    <td class="scan-file-overall-statistics-label-cell">Search Id:</td>
		{{#each perSearchDataList }}
		 	<td style="white-space: nowrap" class="scan-file-overall-statistics-data-cell">{{this.searchId}}</td>
		{{/each}}	    
	   </tr>
	   <tr>
	    <td class="scan-file-overall-statistics-label-cell">Total Ion Current:</td>
		{{#each perSearchDataList }}
		 	<td style="white-space: nowrap" class="scan-file-overall-statistics-data-cell">{{this.totalIonCurrent}}</td>
		{{/each}}	    
	   </tr>
	   <tr>
	    <td class="scan-file-overall-statistics-label-cell">Total MS1 Ion Current:</td>
		{{#each perSearchDataList }}
		 	<td style="white-space: nowrap" class="scan-file-overall-statistics-data-cell">{{this.total_MS_1_IonCurrent}}</td>
		{{/each}}	    
	   </tr>
	   <tr class="scan-file-overall-statistics-block-end-row">
	    <td class="scan-file-overall-statistics-label-cell">Total MS2 Ion Current:</td>
		{{#each perSearchDataList }}
		 	<td style="white-space: nowrap" class="scan-file-overall-statistics-data-cell">{{this.total_MS_2_IonCurrent}}</td>
		{{/each}}	    
	   </tr>
	   <tr>
	    <td class="scan-file-overall-statistics-label-cell">Number MS1 Scans:</td>
		{{#each perSearchDataList }}
		 	<td style="white-space: nowrap" class="scan-file-overall-statistics-data-cell">{{this.number_MS_1_scans}}</td>
		{{/each}}	    
	   </tr>
	   <tr class="scan-file-overall-statistics-block-end-row">
	    <td class="scan-file-overall-statistics-label-cell">Number MS2 Scans:</td>
		{{#each perSearchDataList }}
		 	<td style="white-space: nowrap" class="scan-file-overall-statistics-data-cell">{{this.number_MS_2_scans}}</td>
		{{/each}}	    
	   </tr>		 		   
	   <tr>
	    <td colspan="{{ searchCountPlusOne }}" style="text-align: center;" class="ms2-scans-psm-cutoff-label"
	    	>MS2 scans with a PSM meeting cutoffs</td>
	   </tr>
	   <tr>
	    <td class="scan-file-overall-statistics-label-cell font-color-link-type-crosslink">Crosslink:</td>
		{{#each perSearchDataList }}
		 	<td style="white-space: nowrap"  class="scan-file-overall-statistics-data-cell font-color-link-type-crosslink"
		 		>{{this.crosslink_MS_2_scansMeetsCutoffsDisplay}}</td>
		{{/each}}	    
	   </tr>
	   <tr>
	    <td class="scan-file-overall-statistics-label-cell font-color-link-type-looplink">Looplink:</td>
		{{#each perSearchDataList }}
		 	<td style="white-space: nowrap"  class="scan-file-overall-statistics-data-cell font-color-link-type-looplink"
		 		>{{this.looplink_MS_2_scansMeetsCutoffsDisplay}}</td>
		{{/each}}	    
	   </tr>
	   <tr>
	    <td class="scan-file-overall-statistics-label-cell font-color-link-type-unlinked"
	    	>Unlinked:</td>
		{{#each perSearchDataList }}
		 	<td style="white-space: nowrap"  class="scan-file-overall-statistics-data-cell font-color-link-type-unlinked"
		 		>{{this.unlinked_MS_2_scansMeetsCutoffsDisplay}}</td>
		{{/each}}	    
	   </tr>
	   <tr>
	    <td class="scan-file-overall-statistics-label-cell"
	    	>Combined:</td>
		{{#each perSearchDataList }}
		 	<td style="white-space: nowrap"  class="scan-file-overall-statistics-data-cell "
		 		>{{this.combinedLinkTypes_MS_2_scansMeetsCutoffsDisplay}}</td>
		{{/each}}	    
	   </tr>
  </table>
			  
</script>				     
				     </div> <!--  Close  <div class="qc-data-block" > -->
				    </div> <!--  close Scan File Statistics outer block - Fixed Height div -->

	 		      </td>
	 		     </tr>
	 		    </table>			  
				    
			  </div>  <!--  close <div id="scan_file_selected_file_statistics_display_block" style="display: none;">  -->
 
			</div> <!-- close <div class="project-info-block  collapsable_jq" > -->
		  </div> <!-- close <div class="top-level-container collapsable_container_jq" > -->
		  
		</div>   <!-- END: Scan level Statistics -->

		<%--  PSM level Statistics --%>
	
		<div >

<script id="psm_level_block_help_tooltip_charge_state_crosslink" type="text/text">
<div >A bar chart comparing the fraction of PSMs with given precursor ion charges for crosslink peptides that meet the current filtering criteria.</div>
</script>
<script id="psm_level_block_help_tooltip_charge_state_looplink" type="text/text">
<div >A bar chart comparing the fraction of PSMs with given precursor ion charges for looplink peptides that meet the current filtering criteria.</div>
</script>
<script id="psm_level_block_help_tooltip_charge_state_unlinked" type="text/text">
<div >A bar chart comparing the fraction of PSMs with given precursor ion charges for unlinked peptides that meet the current filtering criteria.</div>
</script>

<script id="psm_level_block_help_tooltip_m_over_z_statistics_crosslink" type="text/text">
<div >A box plot comparing distribution of the number of PSMs for crosslink peptides that meet the current filtering criteria in each search as a function of the m/z of the precursor ion.</div>
</script>
<script id="psm_level_block_help_tooltip_m_over_z_statistics_looplink" type="text/text">
<div >A box plot comparing distribution of the number of PSMs for looplink peptides that meet the current filtering criteria in each search as a function of the m/z of the precursor ion.</div>
</script>
<script id="psm_level_block_help_tooltip_m_over_z_statistics_unlinked" type="text/text">
<div >A box plot comparing distribution of the number of PSMs for unlinked peptides that meet the current filtering criteria in each search as a function of the m/z of the precursor ion.</div>
</script>

<script id="psm_level_block_help_tooltip_peptide_length_vs_psm_count_boxplot_tooltip_crosslink" type="text/text">
<div >Box plots comparing the distributions of the lengths peptides for all PSMs that meet current filtering criteria.
This is the length of both linked peptides added together.</div>
</script>
<script id="psm_level_block_help_tooltip_peptide_length_vs_psm_count_boxplot_tooltip_looplink" type="text/text">
<div >Box plots comparing the distributions of the lengths peptides for all PSMs that meet current filtering criteria.</div>
</script>
<script id="psm_level_block_help_tooltip_peptide_length_vs_psm_count_boxplot_tooltip_unlinked" type="text/text">
<div >Box plots comparing the distributions of the lengths peptides for all PSMs that meet current filtering criteria.</div>
</script>

		  <div class="top-level-container qc_top_level_container_jq" >
			
			<div  class="collapsable-link-container top-level-collapsable-link-container" > 
				<a id="psm_level_collapse_link" href="javascript:" class="top-level-collapsable-link" 
						style="display: none;"
					><img  src="${ contextPath }/images/icon-collapse.png"></a>
				<a id="psm_level_expand_link" href="javascript:" class="top-level-collapsable-link" 
					><img  src="${ contextPath }/images/icon-expand.png"></a>
			</div>
			<div class="top-level-label">
			  PSM Level Statistics
			</div>

			<div class="top-level-label-bottom-border" ></div>
								
			<div id="psm_level_display_block" class="project-info-block" style="display: none;"  >

			   <h2>Charge State Statistics</h2>
			   
	 		  <table  id="PSMChargeStatesCountsBlock" class="table-no-border-no-cell-spacing-no-cell-padding" style="">
			  </table>
			  
			  <%-- M/Z Statistics  --%>
			  <h2>M/Z Statistics</h2>
			  
	 		  <table  id="PSM_M_Over_Z_CountsBlock" class="table-no-border-no-cell-spacing-no-cell-padding" style="">
			  </table>			  

			  <%--  Peptide Lengths Vs PSM Counts --%>
			  <h2>Distribution of PSM peptide lengths</h2>

	 		  <table  id="PeptideLengthVsPSMCountBlock" class="table-no-border-no-cell-spacing-no-cell-padding" style="">
			  </table>		
			  			
			</div> <%-- close <div class="project-info-block  collapsable_jq" > --%>
		  </div> <%-- close <div class="top-level-container collapsable_container_jq" > --%>

		</div>   <%-- END: PSM level Statistics --%>


		<%--  PSM Error Estimates --%>
	
		<div >
		
<script id="psm_error_block_help_tooltip_ppm_error_crosslink" type="text/text">
<div >A box plot comparing the distributions in each search of the number of PSMs for crosslink peptides as a function of estimated PPM error. 
PPM error is calculated as: 1,000,000 * (precursor m/z - calculated m/z) / calculated m/z. 
Several isotopic compositions are compared for calculating m/z, and the minimum PPM error is used.</div>
</script>
<script id="psm_error_block_help_tooltip_ppm_error_looplink" type="text/text">
<div >A box plot comparing the distributions in each search of the number of PSMs for looplink peptides as a function of estimated PPM error. 
PPM error is calculated as: 1,000,000 * (precursor m/z - calculated m/z) / calculated m/z. 
Several isotopic compositions are compared for calculating m/z, and the minimum PPM error is used.</div>
</script>
<script id="psm_error_block_help_tooltip_ppm_error_unlinked" type="text/text">
<div >A box plot comparing the distributions in each search of the number of PSMs for unlinked peptides as a function of estimated PPM error. 
PPM error is calculated as: 1,000,000 * (precursor m/z - calculated m/z) / calculated m/z. 
Several isotopic compositions are compared for calculating m/z, and the minimum PPM error is used.</div>
</script>

		  <div class="top-level-container qc_top_level_container_jq" >
			
			<div  class="collapsable-link-container top-level-collapsable-link-container" > 
				<a id="psm_error_estimates_collapse_link" href="javascript:" class="top-level-collapsable-link" 
						style="display: none;"
					><img  src="${ contextPath }/images/icon-collapse.png"></a>
				<a id="psm_error_estimates_expand_link" href="javascript:" class="top-level-collapsable-link" 
					><img  src="${ contextPath }/images/icon-expand.png"></a>
			</div>
			<div class="top-level-label">
			  PSM Error Estimates
			</div>

			<div class="top-level-label-bottom-border" ></div>
								
			<div id="psm_error_estimates_display_block" class="project-info-block" style="display: none;"  >
			
			  <%--  PPM Error --%>
			  
			<h2>PPM Error</h2>
			  
	 		<table  id="PSM_PPM_Error_CountsBlock" class="table-no-border-no-cell-spacing-no-cell-padding" style="">
			</table>			  
			 
			<%--  End PPM Error --%>
								
			</div> <%-- close <div class="project-info-block  collapsable_jq" > --%>
		  </div> <%-- close <div class="top-level-container collapsable_container_jq" > --%>

		</div>   <%-- END: PSM Error Estimates --%>

		<%--  Modification Stats --%>
	
		<div >

<script id="modification_stats_block_help_tooltip_crosslink" type="text/text">
<div >A bar chart comparing the fraction of PSMs for crosslink peptides in each search that were found to contain the indicated mass modification. 
The mass modifications shown are those found in the search from all identified peptides.</div>
</script>
<script id="modification_stats_block_help_tooltip_looplink" type="text/text">
<div >A bar chart comparing the fraction of PSMs for looplink peptides in each search that were found to contain the indicated mass modification. 
The mass modifications shown are those found in the search from all identified peptides.</div>
</script>
<script id="modification_stats_block_help_tooltip_unlinked" type="text/text">
<div >A bar chart comparing the fraction of PSMs for unlinked peptides in each search that were found to contain the indicated mass modification. 
The mass modifications shown are those found in the search from all identified peptides.</div>
</script>

		  <div class="top-level-container qc_top_level_container_jq" >
			
			<div  class="collapsable-link-container top-level-collapsable-link-container" > 
				<a id="modification_stats_collapse_link" href="javascript:" class="top-level-collapsable-link" 
						style="display: none;"
					><img  src="${ contextPath }/images/icon-collapse.png"></a>
				<a id="modification_stats_expand_link" href="javascript:" class="top-level-collapsable-link" 
					><img  src="${ contextPath }/images/icon-expand.png"></a>
			</div>
			<div class="top-level-label">
			  Modification Stats
			</div>

			<div class="top-level-label-bottom-border" ></div>
								
			<div id="modification_stats_display_block" class="project-info-block" style="display: none;"  >
			
			  <%--  PSM per Modification Counts  --%>
			  
			<h2>PSM per Modification</h2>
			  
	 		<table  id="PSM_Per_Modification_Counts_Block" class="table-no-border-no-cell-spacing-no-cell-padding" style="">
			</table>			  
			 	  			
								
			</div> <%-- close <div class="project-info-block  collapsable_jq" > --%>
		  </div> <%-- close <div class="top-level-container collapsable_container_jq" > --%>

		</div>   <%-- END: Modification Stats --%>


		<%--  Peptide level Statistics --%>
		<div >

<script id="peptide_level_block_help_tooltip_crosslink" type="text/text">
<div >Box plots comparing the distributions of the lengths of distinct peptides in each search. 
This is the length of both linked peptides added together.</div>
</script>
<script id="peptide_level_block_help_tooltip_looplink" type="text/text">
<div >Box plots comparing the distributions of the lengths of distinct peptides in each search.</div>
</script>
<script id="peptide_level_block_help_tooltip_unlinked" type="text/text">
<div >Box plots comparing the distributions of the lengths of distinct peptides in each search.</div>
</script>

		  <div class="top-level-container qc_top_level_container_jq" >
			
			<div  class="collapsable-link-container top-level-collapsable-link-container " > 
				<a id="peptide_level_collapse_link" href="javascript:" class="top-level-collapsable-link " 
						style="display: none;"
					><img  src="${ contextPath }/images/icon-collapse.png"></a>
				<a id="peptide_level_expand_link" href="javascript:" class="top-level-collapsable-link " 
					><img  src="${ contextPath }/images/icon-expand.png"></a>
			
			</div>
			<div class="top-level-label">
			  Peptide Level Statistics
			</div>

			<div class="top-level-label-bottom-border" ></div>
								
			<div id="peptide_level_display_block" class="project-info-block " style="display: none;"  >

	 		  <table  id="PeptideLengthsCountsBlock" class="table-no-border-no-cell-spacing-no-cell-padding" style="">
			  </table>			  
			  
			</div> <%-- close <div class="project-info-block" > --%>
		  </div> <%-- close <div class="top-level-container collapsable_container_jq" > --%>

		</div>   <%-- END: Peptide level Statistics --%>


	</div>  <!--  Close   <div class="overall-enclosing-block">  -->
	

	<%-- qc-data-block is here since it has the width and height of the chart --%>
<script id="common_chart_outer_entry_template" type="text/text"> 
	<td style="padding: 4px;">
	 <div class=" chart-standard-container-div qc-data-block chart_outer_container_for_download_jq {{link_type}}_chart_outer_container_jq chart_outer_container_jq" > 
	 </div>
	</td>
</script>	
	
	<%-- qc-data-block is here since it has the width and height of the chart --%>
<script id="common_chart_inner_entry_template" type="text/text">
	 <div> 
	  <div class=" qc-data-block chart_container_jq chart_container_for_download_jq">
	  </div>
	  <%@ include file="/WEB-INF/jsp-includes/qcChart_Download_Help_HTMLBlock.jsp" %>
	 </div>
</script>	
	
	<%--  Put inside contents of common_chart_outer_entry_template --%>
<script id="dummy_chart_entry_for_message_template" type="text/text">
	<div style="position: relative;"  class=" qc-data-block ">
	  <div class=" message_text_containing_div_jq "
	  	 style="position: absolute; text-align: center; z-index: 1; font-size: 20px; font-weight: bold;">
	  	<span class=" message_text_jq "></span>
	  </div>
	  <div  style="opacity: .5" class=" qc-data-block dummy_chart_container_jq ">
	  </div>
	</div>
</script>


	  <!-- style following table -->
	  <style > 
	    .boxplot-wholechart-tooltip-table td { font-size: 14px; font-weight: bold; padding-bottom: 5px; }
	    .boxplot-wholechart-tooltip-table .label-cell { padding-right: 30px; }
	    .boxplot-wholechart-tooltip-table .data-cell { text-align: right; padding-right: 15px; }
	    .boxplot-wholechart-tooltip-table .search-id-row td { padding-bottom: 10px; }
	  </style>
					  
	<%--  Handlebars template for displaying Info in Tooltip for Box Plots  --%>		

<script id="boxplot_chart_whole_chart_tooltip_template"  type="text/x-handlebars-template">

		<%--  Consider creating CSS classes for this table --%>
		<%--  Use of {{#each ... }}  For each search --%>
 <div >		
  	{{#if chartTitle}}
	    <div style="text-align: center; white-space: nowrap; font-size: 16px; font-weight: bold; padding-top: 10px; padding-bottom: 10px;" class=""
	    	>{{ chartTitle }}</div>
	{{/if}}
   <div >
	  <table class="table-no-border-no-cell-spacing-no-cell-padding boxplot-wholechart-tooltip-table  boxplot_wholechart_tooltip_table_jq " 
	  		style="margin-left: auto; margin-right: auto; padding-bottom: 5px;">
	   <tr class="search-id-row">
	    <td class="label-cell">Search Number:</td>
		{{#each perSearchDataList }}
		 	<td style="white-space: nowrap; color: {{ this.searchColor }}; " class="data-cell"
				>{{ this.searchId }}</td>
		{{/each}}	    
	   </tr>
	   <tr>
	    <td class="label-cell">Max:</td>
		{{#each perSearchDataList }}
		 	<td style="white-space: nowrap; color: {{ this.searchColor }}; " class="data-cell">{{ this.max }}</td>
		{{/each}}	    
	   </tr>
	   <tr>
	    <td class="label-cell">Third Quartile:</td>
		{{#each perSearchDataList }}
		 	<td style="white-space: nowrap; color: {{ this.searchColor }}; " class="data-cell">{{ this.thirdQuartile }}</td>
		{{/each}}	    
	   </tr>
	   <tr>
	    <td class="label-cell">Median:</td>
		{{#each perSearchDataList }}
		 	<td style="white-space: nowrap; color: {{ this.searchColor }}; " class="data-cell">{{ this.median }}</td>
		{{/each}}	    
	   </tr>
	   <tr>
	    <td class="label-cell">First Quartile:</td>
		{{#each perSearchDataList }}
		 	<td style="white-space: nowrap; color: {{ this.searchColor }}" class="data-cell">{{ this.firstQuartile }}</td>
		{{/each}}	    
	   </tr>
	   <tr>
	    <td class="label-cell">Min:</td>
		{{#each perSearchDataList }}
		 	<td style="white-space: nowrap; color: {{ this.searchColor }}" class="data-cell">{{ this.min  }}</td>
		{{/each}}	    
	   </tr>
	  </table>
   </div>
 </div>
</script>

							
<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>
