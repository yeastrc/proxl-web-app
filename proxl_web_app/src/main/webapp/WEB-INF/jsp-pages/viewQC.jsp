<%@page import="org.yeastrc.xlink.www.constants.QC_Plot_ScoreVsScore_Constants"%>
<%@page import="org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants"%>
<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

<%-- viewQC.jsp --%>

<%--   
		Single Search QC Page
 --%>

<%--  In searchDetailsBlock.jsp, suppress display of link "Change searches"  --%>
<%-- 
<c:set var="doNotDisplayChangeSearchesLink" value="${ true }"/>
--%>

 <c:set var="pageTitle">View QC - <c:out value="${ headerProject.projectTblData.title }"></c:out></c:set>

 <c:set var="pageBodyClass" >project-page view-qc-page</c:set>
 
  <c:set var="headerAdditions">
 
		<script type="text/javascript" src="js/libs/base64.js"></script> 

		<%--  Compression --%>
		
		<%--  Used by lz-string.min.js --%>
		<script type="text/javascript" src="js/libs/lz-string/base64-string.js"></script>
		
		<script type="text/javascript" src="js/libs/lz-string/lz-string.min.js"></script>
		
		<%--  Non-Minified version --%>
		<%-- 
		<script type="text/javascript" src="js/libs/lz-string/lz-string.js"></script>
		--%>
		
		<script src="js/libs/jquery-ui-1.12.1.min.js"></script>
		
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
	
	<%@ include file="/WEB-INF/jsp-includes/body_section_data_pages_after_header_main.jsp" %>
	
	<div class="overall-enclosing-block">
	
	<input type="hidden" id="annotation_data_webservice_base_url" value="<c:out value="${ annotation_data_webservice_base_url }"></c:out>"> 

	<input type="hidden" id="project_id" value="<c:out value="${ project_id }"></c:out>"> 
	
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

				<%--  Set to false to NOT show color block before search for key --%>
				<c:set var="showSearchColorBlock" value="${ false }" />
				
				<%--  Include file is dependent on containing loop having varStatus="searchVarStatus"  --%>
				<%@ include file="/WEB-INF/jsp-includes/searchDetailsBlock.jsp" %>


				<tr>
					<td>Type Filter:</td>
					<td colspan="2">
						<%--  Update TestAllWebLinkTypesSelected if add another option --%>

					  <label >
						<input type="checkbox" class=" link_type_jq " id="link_type_crosslink_selector"
							 onchange="if ( window.saveView_dataPages ) { window.saveView_dataPages.searchFormChanged_ForSaveView(); }" 
							value="<%= PeptideViewLinkTypesConstants.CROSSLINK_PSM %>"   >
						crosslinks
					  </label>
					  <label >
						<input type="checkbox" class=" link_type_jq " 
							onchange="if ( window.saveView_dataPages ) { window.saveView_dataPages.searchFormChanged_ForSaveView(); }" 
							value="<%= PeptideViewLinkTypesConstants.LOOPLINK_PSM %>" >
						looplinks
					  </label> 
					  <label >
						<input type="checkbox" class=" link_type_jq " 
							onchange="if ( window.saveView_dataPages ) { window.saveView_dataPages.searchFormChanged_ForSaveView(); }" 
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
							onchange="if ( window.saveView_dataPages ) { window.saveView_dataPages.searchFormChanged_ForSaveView(); }"  
							value="" >
						No modifications
					  </label>
					  
						<logic:iterate id="modMassFilter" name="modMassFilterList">
						
						 <label style="white-space: nowrap" >
							<input type="checkbox" class=" mod_mass_filter_jq " 
								onchange="if ( window.saveView_dataPages ) { window.saveView_dataPages.searchFormChanged_ForSaveView(); }" 
						  		value="<bean:write name="modMassFilter" />" > 
						   <bean:write name="modMassFilter" />
						 </label>
						  
						</logic:iterate>
					</td>
				</tr>				
				<tr>
					<td valign="top" style="white-space: nowrap;">Only Include:</td>
					<td colspan="2">
				
						<select name="includedProteins" multiple="multiple" id="includeProtein"
							onchange="if ( window.saveView_dataPages ) { window.saveView_dataPages.searchFormChanged_ForSaveView(); }"  >  
						  	<c:forEach var="protein" items="${ proteinIdsAndNames  }">
	  						  <option value="<c:out value="${ protein.proteinSequenceVersionId }"></c:out>"><c:out value="${ protein.annotationName }"></c:out></option>
						  	</c:forEach>
	  					</select>
					</td>
				</tr>	
				<tr>
					<td>&nbsp;</td>
					<td>
						<%@ include file="/WEB-INF/jsp-includes/sharePageURLShortenerOverlayFragment.jsp" %>
					
						<c:set var="UpdateButtonText" value="Update From Database"/>
						
						<input id="update_from_database_button"
							type="button" value="${ UpdateButtonText }" > 

						<c:set var="page_JS_Object" value="qcPageMainPageObject"/>
							
						<%@ include file="/WEB-INF/jsp-includes/savePageViewButtonFragment.jsp" %>

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
				><div class="">For each class of peptide, the number of PSMs that meet the current filtering criteria.</div></script>
			<script id="summary_block_help_tooltip_peptide_count_chart" type="text/text"
				><div class="">For each class of peptide, the number of distinct peptide identifications that meet the current filtering criteria.</div></script>
			<script id="summary_block_help_tooltip_protein_count_chart" type="text/text"
				><div class="">For each class of peptide, the number of distinct proteins for which peptides were found that meet the current filtering criteria.</div></script>

		  <div class="top-level-container qc_top_level_container_jq" >
			
			<div  class="collapsable-link-container top-level-collapsable-link-container" > 
				<a id="summary_collapse_link" href="javascript:" class="top-level-collapsable-link" 
						style="display: none;"
					><img  src="images/icon-collapse.png"></a>
				<a id="summary_expand_link" href="javascript:" class="top-level-collapsable-link" 
					><img  src="images/icon-expand.png"></a>
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
				><div class="">For each class of peptide, the fraction of distinct peptide identifications that meet the current filtering criteria that contain at least one missed cleavage.</div></script>
			<script id="digestion_block_help_tooltip_missed_cleavage_chart" type="text/text"
				><div class="">For each class of peptide, the total number of missed cleavages divided by the total number of distinct peptides. (For peptides that meet the current filtering criteria.)</div></script>
			<script id="digestion_block_help_tooltip_missed_cleavage_psm_count_chart" type="text/text"
				><div class="">For each class of peptide, the fraction of PSMs that meet the current filtering criteria that match a peptide that contains at least one missed cleavage.</div></script>

		  <div class="top-level-container qc_top_level_container_jq" >
			
			<div  class="collapsable-link-container top-level-collapsable-link-container " > 
				<a id="digestion_collapse_link" href="javascript:" class="top-level-collapsable-link" 
						style="display: none;"
					><img  src="images/icon-collapse.png"></a>
				<a id="digestion_expand_link" href="javascript:" class="top-level-collapsable-link " 
					><img  src="images/icon-expand.png"></a>
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
<div >
<div style="margin-bottom: 10px;">Ion current summary statistics for the scan file(s) uploaded with the search results. </div>
<div style="margin-bottom: 10px;">"Total ion current" is calculated as the sum of all peak intensities of the respective scan type. </div>
<div style="margin-bottom: 10px;">"Number of scans" is the total number of scans in the file of the respective type. </div>
<div style="margin-bottom: 10px;">"MS2 scans with a PSM meeting cutoffs" is the number of MS2 scans in the file that resulted in a PSM meeting the current filtering criteria.</div> 
<div >The percentage indicates the percentage of all MS2 scans in the file that resulted in a PSM meeting the current filtering criteria.</div>
</div>
</script>

<script id="scan_level_block_help_tooltip_ion_current_vs_retention_time_chart" type="text/text">
A histogram of binned total ion current of MS1 scans as a function of retention time.
</script>

<script id="scan_level_block_help_tooltip_ion_current_vs_m_over_z_chart" type="text/text">
A histogram of binned total ion current of MS1 scans as a function of the m/z of the peak.
</script>

<script id="scan_level_block_help_tooltip_ms1_binned_ion_current_chart" type="text/text">
<div class="">
A two-dimensional density plot showing showing total MS1 intensity in a m/z + retention time bin. 
Intensities are binned for each 1 m/z and each second of retention time. 
The total ion current in the bin is indicated by color, as indicated by the legend.
</div>
</script>

		  <div class="top-level-container qc_top_level_container_jq" >
			
			<div  class="collapsable-link-container top-level-collapsable-link-container " > 
				<a id="scan_level_collapse_link" href="javascript:" class="top-level-collapsable-link " 
						style="display: none;"
					><img  src="images/icon-collapse.png"></a>
				<a id="scan_level_expand_link" href="javascript:" class="top-level-collapsable-link " 
					><img  src="images/icon-expand.png"></a>
			
			</div>
			<div class="top-level-label">
			  Scan File Statistics
			</div>

			<div class="top-level-label-bottom-border" ></div>

								
			<div id="scan_level_display_block" class="project-info-block " style="display: none;"  > <%--  --%>
			  <%-- 
			  <div >
			  	Scan MS1  M/Z and Retention Time
			  </div>
			  --%>
			  <div id="scan_file_files_loading_block">
			  	Loading scan file data
			  </div>

			  <div id="scan_file_no_files_block" style="display: none;">
			  	No Scan Files
			  </div>
			  
			  
			  <h2 id="scan_level_display_scan_file_selector_block" style="display: none;">
			    File:
			    	<!-- <select> For Multiple Scan Files --> 
			    	<select id="scan_level_display_scan_file_selector" class=" scan_file_selector_jq " style="font-size: inherit; color: inherit; display: none;" ></select>
			    	<!-- Display Single scan file -->
			    	<span id="scan_level_display_single_file_filename" class=" single_scan_file_display_jq " style="display: none;"></span>
			  </h2>
			  
			  <!--  Everything displayed once a scan file is selected -->
			  <div id="scan_file_selected_file_statistics_display_block" style="display: none;">
				
	 		    <table class="table-no-border-no-cell-spacing-no-cell-padding" style="">
	 		     <tr>
	 		      <td style="padding: 4px;">

				    <div style=" position: relative;"
				    	 class="chart-standard-container-div  qc-data-block"> <!-- Scan File Statistics outer block -->
				    	 
						<!-- Help Image for 'Scan File Statistics' section -->
					 <div id="scan_file_overall_statistics_help_block" class="  " style="position: absolute; top: 4px; right: 4px;">
						  <div class=" help-image-for-qc-chart-block ">
						  	<img src="images/icon-help.png" class=" help-image-for-qc-chart help_image_for_qc_chart_jq ">
						  </div>
					 </div>

				     <div class="" >
	
					  <h3  style="text-align: center; font-size: 22px; margin-top: 10px; margin-bottom: 10px;">
					  	Scan File Statistics
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
					    #scan_file_overall_statistics_block .scan-file-overall-statistics-label-cell { padding-right: 30px;}
					    #scan_file_overall_statistics_block .scan-file-overall-statistics-data-cell { text-align: right; }
					    #scan_file_overall_statistics_block .ms2-scans-psm-cutoff-label { font-size: 16px; }
					    #scan_file_overall_statistics_block .scan-file-overall-statistics-block-end-row td { padding-bottom: 14px; }
					  </style>
					  
			 		  <table  id="scan_file_overall_statistics_block" class="table-no-border-no-cell-spacing-no-cell-padding" 
			 		  		style="display: none; margin-left: auto; margin-right: auto; padding-bottom: 5px;">
			 		   <tr>
			 		    <td class="scan-file-overall-statistics-label-cell">Total Ion Current:</td>
			 		    <td class="scan-file-overall-statistics-data-cell"
			 		    	><span id="scan_file_overall_statistics_total_ion_current"></span></td>
			 		   </tr>
			 		   <tr>
			 		    <td class="scan-file-overall-statistics-label-cell">Total MS1 Ion Current:</td>
			 		    <td class="scan-file-overall-statistics-data-cell"
			 		    	><span id="scan_file_overall_statistics_total_ms1_ion_current"></span></td>
			 		   </tr>
			 		   <tr class="scan-file-overall-statistics-block-end-row">
			 		    <td class="scan-file-overall-statistics-label-cell"
			 		    	style=""
			 		    	>Total MS2 Ion Current:</td>
			 		    <td class="scan-file-overall-statistics-data-cell"
			 		    	><span id="scan_file_overall_statistics_total_ms2_ion_current"></span></td>
			 		   </tr>
			 		   <tr>
			 		    <td class="scan-file-overall-statistics-label-cell">Number MS1 Scans:</td>
			 		    <td class="scan-file-overall-statistics-data-cell"
			 		    	><span id="scan_file_overall_statistics_number_ms1_scans"></span></td>
			 		   </tr>
			 		   <tr class="scan-file-overall-statistics-block-end-row">
			 		    <td class="scan-file-overall-statistics-label-cell"
			 		    	style=""
			 		    	>Number MS2 Scans:</td>
			 		    <td class="scan-file-overall-statistics-data-cell"
			 		    	><span id="scan_file_overall_statistics_number_ms2_scans"></span></td>
			 		   </tr>		 		   
			 		   <tr>
			 		    <td colspan="2" style="text-align: center;" class="ms2-scans-psm-cutoff-label"
			 		    	>MS2 scans with a PSM meeting cutoffs</td>
			 		   </tr>
			 		   <tr>
			 		    <td class="scan-file-overall-statistics-label-cell font-color-link-type-crosslink"
			 		    	>Crosslink:</td>
			 		    <td	class="scan-file-overall-statistics-data-cell font-color-link-type-crosslink"
			 		    	><span id="scan_file_overall_statistics_number_ms2_scans_crosslink"></span></td>
			 		   </tr>
			 		   <tr>
			 		    <td class="scan-file-overall-statistics-label-cell font-color-link-type-looplink"
			 		    	>Looplink:</td>
			 		    <td class="scan-file-overall-statistics-data-cell font-color-link-type-looplink"
			 		    	><span id="scan_file_overall_statistics_number_ms2_scans_looplink"></span></td>
			 		   </tr>
			 		   <tr>
			 		    <td class="scan-file-overall-statistics-label-cell font-color-link-type-unlinked"
			 		    	>Unlinked:</td>
			 		    <td class="scan-file-overall-statistics-data-cell font-color-link-type-unlinked"
			 		    	><span id="scan_file_overall_statistics_number_ms2_scans_unlinked"></span></td>
			 		   </tr>
			 		   <tr>
			 		    <td class="scan-file-overall-statistics-label-cell"
			 		    	>Combined:</td>
			 		    <td  class="scan-file-overall-statistics-data-cell "
			 		    	><span id="scan_file_overall_statistics_number_ms2_scans_all_types"></span></td>
			 		   </tr>
					  </table>			  
				     
				     </div> <!--  Close  <div class="qc-data-block" > -->
				    </div> <!--  close Scan File Statistics outer block - Fixed Height div -->

	 		      </td>
					<%--  Copied from below from: <script id="common_chart_outer_entry_template" type="text/text">  --%> 
					<td style="padding: 4px;">
					 <div id="MS_1_IonCurrent_RetentionTime_Histogram_Container"
					 	 class=" chart-standard-container-div qc-data-block chart_outer_container_for_download_jq chart_outer_container_jq" > 
					 </div>
					</td>
					<%--  Copied from below from: <script id="common_chart_outer_entry_template" type="text/text">  --%> 
					<td style="padding: 4px;">
					 <div id="MS_1_IonCurrent_M_Over_Z_Histogram_Container" 
					 	 class=" chart-standard-container-div qc-data-block chart_outer_container_for_download_jq chart_outer_container_jq" > 
					 </div>
					</td>

	 		     </tr>
	 		    </table>			  
				  
			  	<div id="" style="margin-top: 10px;">

	 		      <table class="table-no-border-no-cell-spacing-no-cell-padding" style="">
	 		       <tr>
	 		        <td style="padding: 4px;">
				  
				  		
				  	  <script id="MS_1_IonCurrent_Heatmap_full_size_link_template" type="text/text" 
				  		><a target="_blank"></a></script>
				  		<%--  Has Image Width in it.  Add &cbv=${cacheBustValue} to ensure get latest in case image changes --%>

					  <script id="MS_1_IonCurrent_Heatmap_fullsize_href_prefix" type="text/text"
					  	>qc_Scan_MS1_All_IntensityHeatmapImage.do?</script>
				  							
				  		<%--  Has Image Width in it.  Add &cbv=${cacheBustValue} to ensure get latest in case image changes --%>
					  <script id="MS_1_IonCurrent_Heatmap_href_prefix" type="text/text"
					  	>qc_Scan_MS1_All_IntensityHeatmapImage.do?&image_width=1500&cbv=${cacheBustValue}</script>
	
						<!-- MS1 Binned data - Image as Chart -->
					  <div  id="MS_1_IonCurrent_Heatmap_image_outer_container" class="chart-standard-container-div" style="width: 1520px;">
					  	<div style="text-align: center; padding-top: 15px; padding-bottom: 0px; font-size: 15px; font-weight: bold;">
					  		MS1 Binned Ion Current: m/z vs/ Retention Time (click to view full size)
					  		<img src="images/icon-help.png" class=" help-image-for-qc-chart help_image_for_qc_chart_jq ">
					  		
						  <div class="svg-download-block">
							<a href="javascript:" class=" tool_tip_attached_jq  " data-tooltip="Download graphic as file." 
								><img src="images/icon-download-small.png" /></a>
					
							<!-- Overlay that goes under main overlay: display on hover of download icon -->
							<div class="svg-download-options-backing-block svg_download_backing_block_jq ">
							</div>
							<!-- Overlay: display on hover of download icon -->
							<span class=" svg-download-options-block svg_download_block_jq ">
								Choose download file format:
								<%-- Download the data for the chart, hidden until shown in each JS chart code since then a click handler is attached. --%>
								<a id="MS_1_IonCurrent_Heatmap_image_download_data_link"
									data-tooltip="Download as text, tab delimited." 
									class="svg-download-option tool_tip_attached_jq " href="javascript:" style="margin-top:5px; margin-bottom: 5px;"
									>Text/Tab Delimited</a>
							</span>
						  </div>					  		
					  		
					  	</div>
					  	<!-- img tag will be inserted here -->
						<div id="MS_1_IonCurrent_Heatmap_image_container" >
						</div>
					    <div id="MS_1_IonCurrent_Heatmap_Loading" 
					  	 		style="font-size: 20px; font-weight: bold; margin-top: 35px; margin-bottom: 50px;  text-align: center; display: none;">
					  		<span class=" message_text_jq ">Loading Data</span>
					    </div>
					    <div id="MS_1_IonCurrent_Heatmap_No_Data" 
					  	 		style="font-size: 20px; font-weight: bold; margin-top: 35px; margin-bottom: 50px;  text-align: center; display: none;">
					  		<span class=" message_text_jq ">No Data Found</span>
					    </div>
					  </div>

	 		      </td>
	 		     </tr>
	 		    </table>
					  
			  </div>  <!--  close <div id="scan_file_selected_file_statistics_display_block" style="display: none;">  -->
 
			</div> <!-- close <div class="project-info-block  collapsable_jq" > -->
		  </div> <!-- close <div class="top-level-container collapsable_container_jq" > -->
		  
		</div>   <!-- END: Scan level Statistics -->


		<%--  PSM level Statistics --%>
	
		<div >

			<%--  Help Icon tooltip HTML for these charts --%>
				<%--  Main Page Chart --%>
<script id="psm_level_block_help_tooltip_psm_counts_vs_retention_time" type="text/text">
<div >
A histogram of the distribution of MS2 scans as a function of retention time (light pink). 
Another histogram of the number of MS2 scans that meet the current filtering criteria is overlaid in dark red. 
Click on the plot to launch it in interactive mode.
</div>
</script>
				<%--  Overlay Interactive Chart --%>
<script id="psm_level_block_help_tooltip_psm_counts_vs_retention_time_overlay_interactive" type="text/text">
<div >
A histogram of the distribution of MS2 scans as a function of retention time (light pink). 
Another histogram of the number of MS2 scans that meet the current filtering criteria is overlaid in dark red. 
</div>
</script>
				<%--  Main Page Chart --%>
<script id="psm_level_block_help_tooltip_psm_counts_vs_score" type="text/text">
<div >
For each type of peptide, the cumulative number of PSMs identified as a function of a user-choosable score from the search. 
All numeric, filterable scores are available for plotting. 
Click on the plot to launch it in interactive mode.
</div>
</script>
				<%--  Overlay Interactive Chart --%>
<script id="psm_level_block_help_tooltip_psm_counts_vs_score_overlay_interactive" type="text/text">
<div >
For each type of peptide, the cumulative number of PSMs identified as a function of a user-choosable score from the search. 
All numeric, filterable scores are available for plotting. 
</div>
</script>
				<%--  Main Page Chart --%>
<script id="psm_level_block_help_tooltip_psm_score_vs_score" type="text/text">
<div >
For each type of peptide, a scatter plot of a user-choosable score vs/ another user-choosable score for PSMs from the search. 
All numeric, filterable scores are available for plotting. 
Click on the plot to launch it in interactive mode.
</div>
</script>
				<%--  Overlay Interactive Chart --%>
<script id="psm_level_block_help_tooltip_psm_score_vs_score_overlay_interactive" type="text/text">
<div >
For each type of peptide, a scatter plot of a user-choosable score vs/ another user-choosable score for PSMs from the search. 
All numeric, filterable scores are available for plotting. 
</div>
</script>

<script id="psm_level_block_help_tooltip_charge_state_crosslink" type="text/text">
<div >A bar chart comparing the number of PSMs for crosslink peptides that meet the current filtering criteria for each identified charge for precursor ions.</div>
</script>
<script id="psm_level_block_help_tooltip_charge_state_looplink" type="text/text">
<div >A bar chart comparing the number of PSMs for looplink peptides that meet the current filtering criteria for each identified charge for precursor ions.</div>
</script>
<script id="psm_level_block_help_tooltip_charge_state_unlinked" type="text/text">
<div >A bar chart comparing the number of PSMs for unlinked peptides that meet the current filtering criteria for each identified charge for precursor ions.</div>
</script>

<script id="psm_level_block_help_tooltip_m_over_z_statistics_crosslink" type="text/text">
<div >A histogram of the number of PSMs for crosslink peptides that meet the current filtering criteria versus m/z of the precursor ion.</div>
</script>
<script id="psm_level_block_help_tooltip_m_over_z_statistics_looplink" type="text/text">
<div >A histogram of the number of PSMs for looplink peptides that meet the current filtering criteria versus m/z of the precursor ion.</div>
</script>
<script id="psm_level_block_help_tooltip_m_over_z_statistics_unlinked" type="text/text">
<div >A histogram of the number of PSMs for unlinked peptides that meet the current filtering criteria versus m/z of the precursor ion.</div>
</script>

<script id="psm_level_block_help_tooltip_peptide_length_vs_psm_count_tooltip_crosslink" type="text/text">
<div >Histogram depicting the distribution of the lengths of peptides for all PSMs that meet current filtering criteria.  
This is the length of both linked peptides added together.</div>
</script>
<script id="psm_level_block_help_tooltip_peptide_length_vs_psm_count_tooltip_looplink" type="text/text">
<div >Histogram depicting the distribution of the lengths of peptides for all PSMs that meet current filtering criteria.  </div>
</script>
<script id="psm_level_block_help_tooltip_peptide_length_vs_psm_count_tooltip_unlinked" type="text/text">
<div >Histogram depicting the distribution of the lengths of peptides for all PSMs that meet current filtering criteria. </div>
</script>

<script id="psm_level_block_help_tooltip_peptide_length_vs_retention_time_tooltip_crosslink" type="text/text">
<div >Two-dimensional density plot of peptide length as a function of binned retention time. 
The color and point size indicate the number of PSMs in a given retention time bin with a given peptide length.</div>
</script>
<script id="psm_level_block_help_tooltip_peptide_length_vs_retention_time_tooltip_looplink" type="text/text">
<div >Two-dimensional density plot of peptide length as a function of binned retention time. 
The color and point size indicate the number of PSMs in a given retention time bin with a given peptide length.</div>
</script>
<script id="psm_level_block_help_tooltip_peptide_length_vs_retention_time_tooltip_unlinked" type="text/text">
<div >Two-dimensional density plot of peptide length as a function of binned retention time. 
The color and point size indicate the number of PSMs in a given retention time bin with a given peptide length.</div>
</script>


		  <div class="top-level-container qc_top_level_container_jq" >
			
			<div  class="collapsable-link-container top-level-collapsable-link-container" > 
				<a id="psm_level_collapse_link" href="javascript:" class="top-level-collapsable-link" 
						style="display: none;"
					><img  src="images/icon-collapse.png"></a>
				<a id="psm_level_expand_link" href="javascript:" class="top-level-collapsable-link" 
					><img  src="images/icon-expand.png"></a>
			</div>
			<div class="top-level-label">
			  PSM Level Statistics
			</div>

			<div class="top-level-label-bottom-border" ></div>
								
			<div id="psm_level_display_block" class="project-info-block" style="display: none;"  >

			   <h2>Interactive QC Plots</h2>
	 		  <table  id="" class="table-no-border-no-cell-spacing-no-cell-padding" style="">
	 		   <tr>
	 		   		<%-- Retention Time Chart --%>
				<td style="padding: 4px; vertical-align: top;" >
				 <div id="retention_time_outer_container_div"
				 	class=" chart-standard-container-div qc-data-block chart_outer_container_for_download_jq chart_outer_container_jq" 
				 	> 
				 </div>
				 <div style="text-align: center; margin-top: 5px;">
				   <a id="qc_plot_scan_retention_time_link"
				   		href="javascript:" data-tooltip="View PSM counts as function of retention time" class="tool_tip_attached_jq " 
					   	>Click to launch interactive viewer</a>
				  </div>
				</td>
					<%-- PSM Count Vs Score Chart --%>
				<td style="padding: 4px; vertical-align: top;">
				 <div id="psm_count_vs_score_outer_container_div"
				 	class=" chart-standard-container-div qc-data-block chart_outer_container_for_download_jq chart_outer_container_jq" 
				 	> 
				 </div>
				 <div style="text-align: center; margin-top: 5px;">
				   <a id="qc_plot_psm_count_vs_score_link"
				   		href="javascript:" data-tooltip="View PSM counts as function of score" class="tool_tip_attached_jq " 
					   	>Click to launch interactive viewer</a>
				  </div>
				</td>				
					<%-- PSM Score Vs Score Chart --%>
				<td style="padding: 4px; vertical-align: top;">
				 <div id="psm_score_vs_score_outer_container_div" 
				 	class=" chart-standard-container-div qc-data-block chart_outer_container_for_download_jq chart_outer_container_jq" 
				 	> 
				 </div>
				 <%--  Template for when insert <img> into DOM --%>
				 <script type="text/text" id="psm_score_vs_score_image_uri_template"
				 	><img src="{{image_uri}}" data-tooltip="Click to launch interactive viewer" style="cursor: pointer;" class="tool_tip_attached_jq "
						id="psm_score_vs_score_thumbnail_chart_image"></script>
				 <div style="text-align: center; margin-top: 5px;">
				   <a id="qc_plot_psm_score_vs_score_link"
				   		href="javascript:" data-tooltip="View 2 PSM scores plotted together" class="tool_tip_attached_jq " 
					   	>Click to launch interactive viewer</a>
				  </div>
				</td>				
			   </tr>			   
			  </table>
			   
	 		  <table  id="PSMInteractiveQCPlotsBlock" class="table-no-border-no-cell-spacing-no-cell-padding" style="">
			  </table>
			  			
			   <h2>Charge State Statistics</h2>
			   
	 		  <table  id="PSMChargeStatesCountsBlock" class="table-no-border-no-cell-spacing-no-cell-padding" style="">
			  </table>
			  
			  <%-- M/Z Statistics  --%>
			  <h2>M/Z Statistics</h2>
			  
	 		  <table  id="PSM_M_Over_Z_CountsBlock" class="table-no-border-no-cell-spacing-no-cell-padding" style="">
			  </table>			  

			  <%--  Peptide Lengths Vs PSM Counts --%>
			  <h2>Peptide Length Statistics</h2>

	 		  <table  id="PeptideLengthVsPSMCountBlock" class="table-no-border-no-cell-spacing-no-cell-padding" style="">
			  </table>	

			  <%--  Peptide Lengths Vs Retention Time --%>
			  <h2>Chromatography Statistics</h2>
			  
	 		  <table  id="PeptideLengthVsRetentionTimeBlock" class="table-no-border-no-cell-spacing-no-cell-padding" style="">
			  </table>				  
			  			
			</div> <%-- close <div class="project-info-block  collapsable_jq" > --%>
		  </div> <%-- close <div class="top-level-container collapsable_container_jq" > --%>

		</div>   <%-- END: PSM level Statistics --%>



		<%--  PSM Error Estimates --%>
	
		<div >
		
<script id="psm_error_block_help_tooltip_ppm_error_crosslink" type="text/text">
<div >A histogram of the number of PSMs for crosslink peptides that meet the current filtering criteria vs/ PPM error of the PSM. 
PPM error is calculated as: 1,000,000 * (precursor m/z - calculated m/z) / calculated m/z. 
Several isotopic compositions are compared for calculating m/z, and the minimum PPM error is used.</div>
</script>
<script id="psm_error_block_help_tooltip_ppm_error_looplink" type="text/text">
<div >A histogram of the number of PSMs for looplink peptides that meet the current filtering criteria vs/ PPM error of the PSM. 
PPM error is calculated as: 1,000,000 * (precursor m/z - calculated m/z) / calculated m/z. 
Several isotopic compositions are compared for calculating m/z, and the minimum PPM error is used.</div>
</script>
<script id="psm_error_block_help_tooltip_ppm_error_unlinked" type="text/text">
<div >A histogram of the number of PSMs for unlinked peptides that meet the current filtering criteria vs/ PPM error of the PSM. 
PPM error is calculated as: 1,000,000 * (precursor m/z - calculated m/z) / calculated m/z. 
Several isotopic compositions are compared for calculating m/z, and the minimum PPM error is used.</div>
</script>

<script id="psm_error_block_help_tooltip_error_vs_retention_time_crosslink" type="text/text">
<div >A two-dimensional density plot indicating the number of PSMs for crosslink peptides that meet the current filtering criteria that have the indicated estimated PPM error as a function of retention time. 
PPM error is calculated as: 1,000,000 * (precursor m/z - calculated m/z) / calculated m/z. 
Several isotopic compositions are compared for calculating m/z, and the minimum PPM error is used.</div>
</script>
<script id="psm_error_block_help_tooltip_error_vs_retention_time_looplink" type="text/text">
<div >A two-dimensional density plot indicating the number of PSMs for looplink peptides that meet the current filtering criteria that have the indicated estimated PPM error as a function of retention time. 
PPM error is calculated as: 1,000,000 * (precursor m/z - calculated m/z) / calculated m/z. 
Several isotopic compositions are compared for calculating m/z, and the minimum PPM error is used.</div>
</script>
<script id="psm_error_block_help_tooltip_error_vs_retention_time_unlinked" type="text/text">
<div >A two-dimensional density plot indicating the number of PSMs for unlinked peptides that meet the current filtering criteria that have the indicated estimated PPM error as a function of retention time. 
PPM error is calculated as: 1,000,000 * (precursor m/z - calculated m/z) / calculated m/z. 
Several isotopic compositions are compared for calculating m/z, and the minimum PPM error is used.</div>
</script>

<script id="psm_error_block_help_tooltip_error_vs_m_over_z_crosslink" type="text/text">
<div >A two-dimensional density plot indicating the number of PSMs for crosslink peptides that meet the current filtering criteria that have the indicated estimated PPM error 
as a function of the measured m/z of the precursor ion. 
PPM error is calculated as: 1,000,000 * (precursor m/z - calculated m/z) / calculated m/z. 
Several isotopic compositions are compared for calculating m/z, and the minimum PPM error is used.</div>
</script>
<script id="psm_error_block_help_tooltip_error_vs_m_over_z_looplink" type="text/text">
<div >A two-dimensional density plot indicating the number of PSMs for looplink peptides that meet the current filtering criteria that have the indicated estimated PPM error 
as a function of the measured m/z of the precursor ion. 
PPM error is calculated as: 1,000,000 * (precursor m/z - calculated m/z) / calculated m/z. 
Several isotopic compositions are compared for calculating m/z, and the minimum PPM error is used.</div>
</script>
<script id="psm_error_block_help_tooltip_error_vs_m_over_z_unlinked" type="text/text">
<div >A two-dimensional density plot indicating the number of PSMs for unlinked peptides that meet the current filtering criteria that have the indicated estimated PPM error 
as a function of the measured m/z of the precursor ion. 
PPM error is calculated as: 1,000,000 * (precursor m/z - calculated m/z) / calculated m/z. 
Several isotopic compositions are compared for calculating m/z, and the minimum PPM error is used.</div>
</script>

		  <div class="top-level-container qc_top_level_container_jq" >
			
			<div  class="collapsable-link-container top-level-collapsable-link-container" > 
				<a id="psm_error_estimates_collapse_link" href="javascript:" class="top-level-collapsable-link" 
						style="display: none;"
					><img  src="images/icon-collapse.png"></a>
				<a id="psm_error_estimates_expand_link" href="javascript:" class="top-level-collapsable-link" 
					><img  src="images/icon-expand.png"></a>
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
			
			
			  <%--  PPM Error Vs Retention Time --%>
			<h2>Error Vs Retention Time</h2>
			  
	 		<table  id="PSM_PPM_Error_Vs_RetentionTime_CountsBlock" class="table-no-border-no-cell-spacing-no-cell-padding" style="">
	 		</table>			  
			<%-- END PPM Error Vs Retention Time  --%>
						
			  <%--  PPM Error Vs M/Z --%>
			<h2>Error Vs M/Z</h2>
			  
	 		<table  id="PSM_PPM_Error_Vs_M_over_Z_CountsBlock" class="table-no-border-no-cell-spacing-no-cell-padding" style="">
			</table>			  
			<%-- END PPM Error Vs M/Z  --%>
					
			</div> <%-- close <div class="project-info-block  collapsable_jq" > --%>
		  </div> <%-- close <div class="top-level-container collapsable_container_jq" > --%>

		</div>   <%-- END: PSM Error Estimates --%>



		<%--  Modification Stats --%>
	
		<div >
		
<script id="modification_stats_block_help_tooltip_crosslink" type="text/text">
<div >A bar chart indicating the fraction of PSMs for crosslink peptides that were found to contain the indicated mass modification. 
The mass modifications shown are those found in the search from all identified peptides.</div>
</script>
<script id="modification_stats_block_help_tooltip_looplink" type="text/text">
<div >A bar chart indicating the fraction of PSMs for looplink peptides that were found to contain the indicated mass modification. 
The mass modifications shown are those found in the search from all identified peptides.</div>
</script>
<script id="modification_stats_block_help_tooltip_unlinked" type="text/text">
<div >A bar chart indicating the fraction of PSMs for unlinked peptides that were found to contain the indicated mass modification. 
The mass modifications shown are those found in the search from all identified peptides.</div>
</script>

		  <div class="top-level-container qc_top_level_container_jq" >
			
			<div  class="collapsable-link-container top-level-collapsable-link-container" > 
				<a id="modification_stats_collapse_link" href="javascript:" class="top-level-collapsable-link" 
						style="display: none;"
					><img  src="images/icon-collapse.png"></a>
				<a id="modification_stats_expand_link" href="javascript:" class="top-level-collapsable-link" 
					><img  src="images/icon-expand.png"></a>
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
			 
			<%--  PSM per Modification Counts --%>
								
			</div> <%-- close <div class="project-info-block  collapsable_jq" > --%>
		  </div> <%-- close <div class="top-level-container collapsable_container_jq" > --%>

		</div>   <%-- END: Modification Stats --%>


		<%--  Peptide level Statistics --%>
		<div >

<script id="peptide_level_block_help_tooltip_crosslink" type="text/text">
<div >A histogram of the count of distinct crosslink peptides that meet the current filtering criteria that were found for respective peptide lengths. 
This is the length of both linked peptides added together.</div>
</script>
<script id="peptide_level_block_help_tooltip_looplink" type="text/text">
<div >A histogram of the count of distinct looplink peptides that meet the current filtering criteria that were found for respective peptide lengths. </div>
</script>
<script id="peptide_level_block_help_tooltip_unlinked" type="text/text">
<div >A histogram of the count of distinct unlinked peptides that meet the current filtering criteria that were found for respective peptide lengths.</div>
</script>

		  <div class="top-level-container qc_top_level_container_jq" >
			
			<div  class="collapsable-link-container top-level-collapsable-link-container " > 
				<a id="peptide_level_collapse_link" href="javascript:" class="top-level-collapsable-link " 
						style="display: none;"
					><img  src="images/icon-collapse.png"></a>
				<a id="peptide_level_expand_link" href="javascript:" class="top-level-collapsable-link " 
					><img  src="images/icon-expand.png"></a>
			
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



		<!--  Modal dialog for displaying the Retention Time QC plot -->


			<%--   Overlay Background --%>
			
		<div id="scan_retention_time_qc_plot_overlay_background" 
			class=" qc-plot-overlay-background   scan_retention_time_qc_plot_overlay_show_hide_parts_jq scan_retention_time_qc_plot_overlay_close_parts_jq  overlay_show_hide_parts_jq"  
			style="display: none;"  >
		
		</div>
			
			<%--  Retention Time QC plot Overlay Div --%>
			
				<!--  Inline div for positioning modal dialog on page -->
		<div class="qc-plot-overlay-containing-outermost-div " id="scan_retention_time_qc_plot_overlay_containing_outermost_div_inline_div" >

		  <div class="qc-plot-overlay-containing-outer-div " style="position: relative;" >
			
			<div id="scan_retention_time_qc_plot_overlay_container" 
				class=" qc-plot-overlay-div overlay-outer-div   scan_retention_time_qc_plot_overlay_show_hide_parts_jq  overlay_show_hide_parts_jq" 
				style="display: none; "  >
			
				<div id="scan_retention_time_qc_plot_overlay_header" class="qc-plot-overlay-header" style="width:100%; " >
				
					<h1 id="scan_retention_time_qc_plot_overlay_X_for_exit_overlay" 
						class="qc-plot-overlay-X-for-exit-overlay  scan_retention_time_qc_plot_overlay_close_parts_jq" 
						>X</h1>
						
					<h1 id="scan_retention_time_qc_plot_overlay_header_text" class="qc-plot-overlay-header-text" >QC Plot: Retention Time</h1>
				</div>
				<div id="scan_retention_time_qc_plot_overlay_body" class="qc-plot-overlay-body" >
				  <div >
					<table style="border-width:0px;">
						<tr id="scan_retention_time_qc_plot_overlay_scan_file_selector_row" >
							<td>Scan File:</td>
							<td>
								<select id="scan_retention_time_qc_plot_scan_file_id"  class=" scan_file_selector_jq  scan_retention_time_qc_plot_on_change_jq">
								</select>
								<span class=" single_scan_file_display_jq "></span> <%-- When only 1 scan file, display here --%>
							</td>
						</tr>
						<tr>
							<td>Scans with:</td>
							<td>
							  <label >
								<input type="checkbox" class="scan_retention_time_qc_plot_scans_include_jq scan_retention_time_qc_plot_on_change_jq"
									id="scan_retention_time_qc_plot_link_type_checkbox_<%= PeptideViewLinkTypesConstants.CROSSLINK_PSM %>"
									value="<%= PeptideViewLinkTypesConstants.CROSSLINK_PSM %>" >
								crosslinks
							  </label>
							  <label >
								<input type="checkbox" class="scan_retention_time_qc_plot_scans_include_jq scan_retention_time_qc_plot_on_change_jq"
									id="scan_retention_time_qc_plot_link_type_checkbox_<%= PeptideViewLinkTypesConstants.LOOPLINK_PSM %>"
									value="<%= PeptideViewLinkTypesConstants.LOOPLINK_PSM %>" >
								looplinks
							  </label> 
							  <label >
								<input type="checkbox" class="scan_retention_time_qc_plot_scans_include_jq scan_retention_time_qc_plot_on_change_jq"
									id="scan_retention_time_qc_plot_link_type_checkbox_<%= PeptideViewLinkTypesConstants.UNLINKED_PSM %>"
									value="<%= PeptideViewLinkTypesConstants.UNLINKED_PSM %>" >
								unlinked
							  </label>
							</td>
						</tr>
						<tr>
							<td>Max: </td>
							<td>
								X:
								<input type="text" id="scan_retention_time_qc_plot_max_x" 
									class="scan_retention_time_qc_plot_on_change_jq" 
									size="8"> 
								Y:
								<input type="text" id="scan_retention_time_qc_plot_max_y" 
									class="scan_retention_time_qc_plot_on_change_jq"
									size="8">
				
								<input type="button" id="scan_retention_time_qc_plot_max_reset_button" value="Reset">
							</td>
						</tr>	
					</table>

					<%-- Error Messages, hidden by default --%>
					<h1 class="scan_retention_time_qc_plot_filter_psms_by_param_not_a_number_jq" 
						style="display: none;  ">
						"Filter PSMs by" is not a number
					</h1>
					<h1 class="scan_retention_time_qc_plot_param_not_a_number_jq" 
						style="display: none;  ">
						Max X or Max Y is not empty and is not a number
					</h1>

					<div class="scan_retention_time_qc_plot_have_data_jq" >
					</div>						
					
					<h1 class="scan_retention_time_qc_plot_no_data_jq" style="display: none;  ">
						No Data
					</h1>
					
					<div id="scan_retention_time_qc_plot_chartDiv_Container" 
						class=" chart-standard-container-div scan_retention_time_qc_plot_have_data_jq chart_outer_container_for_download_jq chart_outer_container_jq "
						style="width: 100%; height: 650px; margin-top: 20px; position: relative; " >

					  <div id="scan_retention_time_qc_plot_chartDiv" style="width: 100%; height: 100%; " >
					  </div>
					  <%@ include file="/WEB-INF/jsp-includes/qcChart_Download_Help_HTMLBlock.jsp" %>
					</div>
												
				</div>
			</div>
		   
		   </div>
	
		  </div>
		</div>
		
		<!-- END:   Modal dialog for displaying the Retention Time QC plot -->
								

		<!--  Modal dialog for displaying the PSM Q Value Counts QC plot -->


			<%--   Overlay Background --%>
			
		<div id="psm_count_vs_score_qc_plot_overlay_background" 
			class=" qc-plot-overlay-background   psm_count_vs_score_qc_plot_overlay_show_hide_parts_jq psm_count_vs_score_qc_plot_overlay_close_parts_jq  overlay_show_hide_parts_jq"  
			style="display: none;"  >
		
		</div>
			
			<%--  PSM Q Value Counts QC plot Overlay Div --%>
			
				<!--  Inline div for positioning modal dialog on page -->
		<div class="qc-plot-overlay-containing-outermost-div " id="psm_count_vs_score_qc_plot_overlay_containing_outermost_div_inline_div" >

		  <div class="qc-plot-overlay-containing-outer-div " style="position: relative;" >
			
			
			
			<div id="psm_count_vs_score_qc_plot_overlay_container" 
				class=" qc-plot-overlay-div overlay-outer-div   psm_count_vs_score_qc_plot_overlay_show_hide_parts_jq  overlay_show_hide_parts_jq" 
				style="display: none; "  >
			
				<div id="psm_count_vs_score_qc_plot_overlay_header" class="qc-plot-overlay-header" style="width:100%; " >
				
					<h1 id="psm_count_vs_score_qc_plot_overlay_X_for_exit_overlay" 
						class="qc-plot-overlay-X-for-exit-overlay  psm_count_vs_score_qc_plot_overlay_close_parts_jq" 
						>X</h1>
						
					<h1 id="psm_count_vs_score_qc_plot_overlay_header_text" class="qc-plot-overlay-header-text" 
						>QC Plot: PSM <span id="psm_count_vs_score_qc_plot_overlay_header_text_count_type" ></span> Counts</h1>
				</div>
				<div id="psm_count_vs_score_qc_plot_overlay_body" class="qc-plot-overlay-body" >
			
				<div >
					<table style="border-width:0px;">
						<tr id="psm_count_vs_score_qc_plot_overlay_scan_file_selector_row" >
							<td>Scan File:</td>
							<td colspan="3">
								<select id="psm_count_vs_score_qc_plot_scan_file_id"  class=" scan_file_selector_jq psm_count_vs_score_qc_plot_on_change_jq">
								</select>
								<span class=" single_scan_file_display_jq "></span> <%-- When only 1 scan file, display here --%>
							</td>
						</tr>
						<tr>
							<td>Choose score:</td>
							<td>
								<select id="psm_count_vs_score_qc_plot_score_type_id" >
								</select>							
							</td>
						</tr>
						<tr>
							<td valign="top" style="white-space: nowrap;">Include Protein filter:</td>
							<td>
								<select id="psm_count_vs_score_qc_plot_protein_seq_id_include_select" multiple="multiple" 
									class=" psm_count_vs_score_qc_plot_on_change_jq " >
								</select>					
							</td>
<%-- 							
						</tr>
						
						<tr>
--%>						
							<td valign="top" style="white-space: nowrap; padding-left: 10px;">Exclude Protein filter:</td>
							<td>
								<select id="psm_count_vs_score_qc_plot_protein_seq_id_exclude_select" multiple="multiple" 
									class=" psm_count_vs_score_qc_plot_on_change_jq " >
								</select>					
							</td>
						</tr>
						
						<c:if test="${ not empty cutoffsAppliedOnImportAllAsString }">
						  <tr id="psm_count_vs_score_qc_plot_score_cutoffs_on_import_row" 
							style="">
							<td id="psm_count_vs_score_qc_plot_score_cutoffs_on_import_message"
								class=" qc-plot-filter-on-import-notice "
								colspan="2" >
								Note: Filtered on Import:
								<%-- Block for annotations and values text --%>
								<c:out value="${ cutoffsAppliedOnImportAllAsString }"></c:out>
							</td>	
						  </tr>
						</c:if>
						
						<tr>
							<td>View as:</td>
							<td>
							  <label >
								<input type="radio"
									id="psm_count_vs_score_qc_plot_y_axis_as_percentage"
									name="psm_count_vs_score_qc_plot_y_axis_choice"
									class=" psm_count_vs_score_qc_plot_on_change_jq"
									value="" >
									percentage
							  </label>
							  
							  <label >
								<input type="radio"
									id="psm_count_vs_score_qc_plot_y_axis_as_raw_counts"
									name="psm_count_vs_score_qc_plot_y_axis_choice"
									class=" psm_count_vs_score_qc_plot_on_change_jq"
									checked="checked"
									value="" >
									raw counts
							  </label>
							<td>
						</tr>
						
						<tr>
							<td>PSMs with:</td>
							<td colspan="3">
							  <label >
								<input type="checkbox" class="psm_count_vs_score_qc_plot_link_type_include_jq psm_count_vs_score_qc_plot_on_change_jq"
									id="psm_count_vs_score_qc_plot_link_type_checkbox_<%= PeptideViewLinkTypesConstants.CROSSLINK_PSM %>"
									value="<%=PeptideViewLinkTypesConstants.CROSSLINK_PSM%>" >
								crosslinks
							  </label>
							  <label >
								<input type="checkbox" class="psm_count_vs_score_qc_plot_link_type_include_jq psm_count_vs_score_qc_plot_on_change_jq"
									id="psm_count_vs_score_qc_plot_link_type_checkbox_<%= PeptideViewLinkTypesConstants.LOOPLINK_PSM %>"
									value="<%=PeptideViewLinkTypesConstants.LOOPLINK_PSM%>" >
								looplinks
							  </label> 
							  
							  <label >
								<input type="checkbox" class="psm_count_vs_score_qc_plot_link_type_include_jq psm_count_vs_score_qc_plot_on_change_jq"
									id="psm_count_vs_score_qc_plot_link_type_checkbox_<%= PeptideViewLinkTypesConstants.UNLINKED_PSM %>"
									value="<%=PeptideViewLinkTypesConstants.UNLINKED_PSM%>" >
								unlinked
							  </label>
							  <label >
								<input type="checkbox" class="psm_count_vs_score_qc_plot_link_type_include_jq psm_count_vs_score_qc_plot_on_change_jq"
									value="<%=PeptideViewLinkTypesConstants.ALL_PSM%>" >
								all
							  </label> 							  
							</td>
						</tr>
	
						<tr>
							<td>Max: </td>
							<td colspan="3">
								X:
								<input type="text" id="psm_count_vs_score_qc_plot_max_x" 
									class="psm_count_vs_score_qc_plot_on_change_jq" 
									size="8"> 
								Y:
								<input type="text" id="psm_count_vs_score_qc_plot_max_y" 
									class="psm_count_vs_score_qc_plot_on_change_jq"
									size="8">
				
								<input type="button" id="psm_count_vs_score_qc_plot_max_reset_button" value="Reset">
							</td>
						</tr>	
					</table>
					
					<h1 class="psm_count_vs_score_qc_plot_no_data_jq" 
						style="display: none;  ">
						No Data
					</h1>
					<h1 class="psm_count_vs_score_qc_plot_param_not_a_number_jq" 
						style="display: none;  ">
						Max X or Max Y is not empty and is not a number
					</h1>
					
					<div id="psm_count_vs_score_qc_plot_chartDiv_Container" 
						class=" chart-standard-container-div psm_count_vs_score_qc_plot_have_data_jq chart_outer_container_for_download_jq chart_outer_container_jq "
						style="width: 100%; height: 650px; margin-top: 20px; position: relative; " >

					  <div id="psm_count_vs_score_qc_plot_chartDiv" style="width: 100%; height: 100%; " >
					  </div>
					  <%@ include file="/WEB-INF/jsp-includes/qcChart_Download_Help_HTMLBlock.jsp" %>
					</div>					
					
				</div>
				
			</div>
		   
		   </div>
	
		  </div>
		</div>
		
		
		<!-- END:   Modal dialog for displaying the PSM Q Value Counts QC plot -->
								

		
		<!--  Modal dialog for displaying the PSM Score VS Score QC plot -->


			<%--   Overlay Background --%>
			
		<div id="psm_score_vs_score_qc_plot_overlay_background" 
			class=" qc-plot-overlay-background   psm_score_vs_score_qc_plot_overlay_show_hide_parts_jq psm_score_vs_score_qc_plot_overlay_close_parts_jq  overlay_show_hide_parts_jq"  
			style="display: none;"  >
		
		</div>
			
			<%--  PSM Score VS Score QC plot Overlay Div --%>
			
				<!--  Inline div for positioning modal dialog on page -->
		<div class="qc-plot-overlay-containing-outermost-div " id="psm_score_vs_score_qc_plot_overlay_containing_outermost_div_inline_div" >

		  <div class="qc-plot-overlay-containing-outer-div " style="position: relative;" >
			
			<div id="psm_score_vs_score_qc_plot_overlay_container" 
				class=" qc-plot-overlay-div overlay-outer-div   psm_score_vs_score_qc_plot_overlay_show_hide_parts_jq  overlay_show_hide_parts_jq" 
				style="display: none; "  >
			
				<div id="psm_score_vs_score_qc_plot_overlay_header" class="qc-plot-overlay-header" style="width:100%; " >
				
					<h1 id="psm_score_vs_score_qc_plot_overlay_X_for_exit_overlay" 
						class="qc-plot-overlay-X-for-exit-overlay  psm_score_vs_score_qc_plot_overlay_close_parts_jq" 
						>X</h1>
						
					<h1 id="psm_score_vs_score_qc_plot_overlay_header_text" class="qc-plot-overlay-header-text" 
						>QC Plot: PSM Score Vs Score</h1>
				</div>
				<div id="psm_score_vs_score_qc_plot_overlay_body" class="qc-plot-overlay-body" >
			
				<div >
					<table style="border-width:0px;">
						<tr id="psm_score_vs_score_qc_plot_overlay_scan_file_selector_row">
							<td>Scan File:</td>
							<td>
								<select id="psm_score_vs_score_qc_plot_scan_file_id"  class=" scan_file_selector_jq psm_score_vs_score_qc_plot_on_change_jq">
								</select>
								<span class=" single_scan_file_display_jq "></span> <%-- When only 1 scan file, display here --%>
							</td>
						</tr>				
								
						<tr>
							<td>X-Axis Score:</td>
							<td>
								<select id="psm_score_vs_score_qc_plot_score_type_id_1" >
								</select>							
							</td>
						</tr>
						
						<tr>
							<td>Y-Axis Score:</td>
							<td>
								<select id="psm_score_vs_score_qc_plot_score_type_id_2" >
									
								</select>							
							</td>
						</tr>
						<c:if test="${ not empty cutoffsAppliedOnImportAllAsString }">
						  <tr id="psm_score_vs_score_qc_plot_score_cutoffs_on_import_row" 
							style="">
							<td id="psm_score_vs_score_qc_plot_score_cutoffs_on_import_message"
								class=" qc-plot-filter-on-import-notice "
								colspan="2" >
								Note: Filtered on Import:
								<%-- Block for annotations and values text --%>
								<c:out value="${ cutoffsAppliedOnImportAllAsString }"></c:out>
							</td>	
						  </tr>
						</c:if>
						<tr>
							<td>PSMs with:</td>
							<td>
							  <label >
								<input type="checkbox" class="psm_score_vs_score_qc_plot_link_type_include_jq psm_score_vs_score_qc_plot_on_change_jq"
									id="psm_score_vs_score_qc_plot_link_type_checkbox_<%= PeptideViewLinkTypesConstants.CROSSLINK_PSM %>"
									value="<%=PeptideViewLinkTypesConstants.CROSSLINK_PSM%>" >
								crosslinks
							  </label>
							  <label >
								<input type="checkbox" class="psm_score_vs_score_qc_plot_link_type_include_jq psm_score_vs_score_qc_plot_on_change_jq"
									id="psm_score_vs_score_qc_plot_link_type_checkbox_<%= PeptideViewLinkTypesConstants.LOOPLINK_PSM %>"
									value="<%=PeptideViewLinkTypesConstants.LOOPLINK_PSM%>" >
								looplinks
							  </label> 
							  
							  <label >
								<input type="checkbox" class="psm_score_vs_score_qc_plot_link_type_include_jq psm_score_vs_score_qc_plot_on_change_jq"
									id="psm_score_vs_score_qc_plot_link_type_checkbox_<%= PeptideViewLinkTypesConstants.UNLINKED_PSM %>"
									value="<%=PeptideViewLinkTypesConstants.UNLINKED_PSM%>" >
								unlinked
							  </label>
							</td>
						</tr>
						<tr>
							<td>Max: </td>
							<td>
								X:
								<input type="text" id="psm_score_vs_score_qc_plot_max_x" 
									class="psm_score_vs_score_qc_plot_on_change_jq" 
									size="8"> 
								Y:
								<input type="text" id="psm_score_vs_score_qc_plot_max_y" 
									class="psm_score_vs_score_qc_plot_on_change_jq"
									size="8">
				
								<input type="button" id="psm_score_vs_score_qc_plot_max_reset_button" value="Reset">
							</td>
						</tr>	
														
					</table>
					
					<h1 class="psm_score_vs_score_qc_plot_no_data_jq" style="display: none;  ">
						No Data
					</h1>
					<h1 class="psm_score_vs_score_qc_plot_param_not_a_number_jq" style="display: none;  ">
						Max X or Max Y is not empty and is not a number
					</h1>

					<div id="psm_score_vs_score_qc_plot_chartDiv_Container" 
						class=" chart-standard-container-div psm_score_vs_score_qc_plot_have_data_jq chart_outer_container_for_download_jq chart_outer_container_jq "
						style="width: 100%; height: 650px; margin-top: 20px; position: relative; " >

					  <div id="psm_score_vs_score_qc_plot_chartDiv" style="width: 100%; height: 100%; " >
					  </div>
					  <%@ include file="/WEB-INF/jsp-includes/qcChart_Download_Help_HTMLBlock.jsp" %>
					</div>					
																		
				</div>
				
			</div>
		   
		   </div>
	
		  </div>
		</div>
		
		<%-- Values sent to server --%>
		<script id="psm_score_vs_score_qc_plot_choice_value__retention_time" type="text/text"><%= QC_Plot_ScoreVsScore_Constants.SCORE_SELECTION_RETENTION_TIME %></script>
		<script id="psm_score_vs_score_qc_plot_choice_value__charge" type="text/text"><%= QC_Plot_ScoreVsScore_Constants.SCORE_SELECTION_CHARGE %></script>
		<script id="psm_score_vs_score_qc_plot_choice_value__pre_mz" type="text/text"><%= QC_Plot_ScoreVsScore_Constants.SCORE_SELECTION_PRE_MZ %></script>

		<%-- Labels put on select for user --%>
		<script id="psm_score_vs_score_qc_plot_choice_label__retention_time" type="text/text">Retention Time</script>
		<script id="psm_score_vs_score_qc_plot_choice_label__charge" type="text/text">Charge</script>
		<script id="psm_score_vs_score_qc_plot_choice_label__pre_mz" type="text/text">Precursor M/Z</script>
		
		<!-- END:   Modal dialog for displaying the PSM Score VS Score QC plot -->
						

	</div>  <!--  Close   <div class="overall-enclosing-block">  -->
	

	<%-- qc-data-block is here since it has the width and height of the chart --%>
<script id="common_chart_outer_entry_template" type="text/text"> 
	<td style="padding: 4px;">
	 <div class=" chart-standard-container-div qc-data-block chart_outer_container_for_download_jq {{ link_type }}_chart_outer_container_jq chart_outer_container_jq" data-link_type="{{ link_type }}" > 
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
		
	
		<%--  Bundle version of core page JS --%>
		<script type="text/javascript" src="static/js_generated_bundles/data_pages/qcView-bundle.js?x=${cacheBustValue}"></script>
										
<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>
