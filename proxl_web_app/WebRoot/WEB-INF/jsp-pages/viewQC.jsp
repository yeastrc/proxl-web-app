<%@page import="org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants"%>
<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

<%-- viewQC.jsp --%>

<%--   

		!!!!  Currently only works for single search.  

		The page is designed to work with multiple merged searches 
		but the code and SQL need to be reviewed to determine that the results returned are what the user expects,
		especially for reported peptide level results. 

 --%>

<%--  In searchDetailsBlock.jsp, suppress display of link "Change searches"  --%>
<c:set var="doNotDisplayChangeSearchesLink" value="${ true }"/>

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
		
		<script type="text/javascript" src="${ contextPath }/js/viewQC.js?x=${cacheBustValue}"></script>
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
						<%--  onclick="viewQCPageCode.refreshData()" > --%>

						<%@ include file="/WEB-INF/jsp-includes/sharePageURLShortenerButtonFragment.jsp" %>
					</td>
				</tr>
							
			</table>
			
		</div>
	
		<hr>
		
		<%--  Summary level Statistics --%>
	
		<div >

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
			  
			  <!-- <select> For Multiple Scan Files -->
			  <h2 id="scan_file_selector_block" style="display: none;">
			    File: <select id="scan_file_selector" style="font-size: inherit; color: inherit;" ></select>
			  </h2>
			  <!-- Display Single scan file -->
			  <h2 id="scan_file_single_file_block" style="display: none;">
				File: <span id="scan_file_single_file_filename"></span>
			  </h2>
			  
			  <!--  Everything displayed once a scan file is selected -->
			  <div id="scan_file_selected_file_statistics_display_block" style="display: none;">
				
	 		    <table class="table-no-border-no-cell-spacing-no-cell-padding" style="">
	 		     <tr>
	 		      <td style="padding: 4px;">

				    <div style=""
				    	 class="chart-standard-container-div  qc-data-block"> <!-- Scan File Statistics outer block -->
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
	
					  <div class="chart-standard-container-div" style="width: 1520px;">
					  	<div style="text-align: center; padding-top: 15px; padding-bottom: 0px; font-size: 15px; font-weight: bold;">
					  		MS1 Binned Ion Current: m/z vs/ Retention Time (click to view full size)
					  	</div>
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
			
			</div> <%-- close <div class="project-info-block  collapsable_jq" > --%>
		  </div> <%-- close <div class="top-level-container collapsable_container_jq" > --%>

		</div>   <%-- END: PSM level Statistics --%>



		<%--  PSM Error Estimates --%>
	
		<div >

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
			 
			<%--  PSM per Modification Counts --%>
								
			</div> <%-- close <div class="project-info-block  collapsable_jq" > --%>
		  </div> <%-- close <div class="top-level-container collapsable_container_jq" > --%>

		</div>   <%-- END: Modification Stats --%>


		<%--  Peptide level Statistics --%>
		<div >

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
	  <%@ include file="/WEB-INF/jsp-includes/chartDownloadHTMLBlock.jsp" %>
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
		
							
<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>
