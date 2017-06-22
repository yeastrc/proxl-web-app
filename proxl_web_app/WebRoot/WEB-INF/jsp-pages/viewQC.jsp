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
			
			  <div >
			  </div>
			  <div id="missingCleavageReportedPeptidesCountLoadingBlock">
			  	Loading
			  </div>
 		      <table  id="missingCleavageReportedPeptidesCountBlock" class="table-no-border-no-cell-spacing-no-cell-padding" style="">
			  </table>
			</div>
		  </div>

		</div>  <%--  END:  Digestion Statistics --%>

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
			
			  <h2>Summary Statistics</h2>
			  
			  <div id="PSM_Summary_CountsLoadingBlock">
			  	Loading
			  </div>
			  
	 		  <table  id="PSM_Summary_CountsBlock" class="table-no-border-no-cell-spacing-no-cell-padding" style="display: none;">
	 		   <tr>
				<td style="padding: 4px;">
				 <div class=" chart-standard-container-div chart_outer_container_jq chart_outer_container_for_download_jq " > 
				  <div class=" qc-data-block chart_container_jq chart_container_for_download_jq">
				  </div>
				  <%@ include file="/WEB-INF/jsp-includes/chartDownloadHTMLBlock.jsp" %>
				 </div>
				</td>
			   </tr>
			  </table>			
			  
			   <h2>Charge State Statistics</h2>
			   
			  <div id="PSMChargeStatesCountsLoadingBlock">
			  	Loading
			  </div>
			  
	 		  <table  id="PSMChargeStatesCountsBlock" class="table-no-border-no-cell-spacing-no-cell-padding" style="">
			  </table>
			  
			  <%-- M/Z Statistics  --%>
			<c:choose>
			 <c:when test="${ anySearchesHaveScanData }">
			  
			  <h2>M/Z Statistics</h2>
			  
			  <div id="PSM_M_Over_Z_CountsLoadingBlock">
			  	Loading
			  </div>
			  
	 		  <table  id="PSM_M_Over_Z_CountsBlock" class="table-no-border-no-cell-spacing-no-cell-padding" style="">
			  </table>			  
			 
			 </c:when>
			 <c:otherwise>
			 	<%-- 
			   		No Scans so not showing "M/Z for PSMs Per Link Type"
			   	--%>
			   	
			   	<h2>M/Z Statistics</h2>
			   	
			   <script id="NO_PSM_M_Over_Z_CountsBlock" type="text/text">__ContentsNotRead__</script>
				
				<%-- No Data Found Charts --%>
				<%-- --%>  
				 <table class="table-no-border-no-cell-spacing-no-cell-padding" style="">
				  <tr>
				  	<c:set var="noDataLinkType" value="crosslink" />
				  	<%@ include file="/WEB-INF/jsp-includes/viewQC_MZ_Data_NoDataAvailable.jsp" %>
				  	<c:set var="noDataLinkType" value="looplink" />
				  	<%@ include file="/WEB-INF/jsp-includes/viewQC_MZ_Data_NoDataAvailable.jsp" %>
				  	<c:set var="noDataLinkType" value="unlinked" />
				  	<%@ include file="/WEB-INF/jsp-includes/viewQC_MZ_Data_NoDataAvailable.jsp" %>
				   </tr>
				  </table>			   

			 </c:otherwise>
			</c:choose>
			

			  <%--  PPM Error --%>
			<c:choose>
			 <c:when test="${ anySearchesHaveScanData }">
			  
			  <h2>PPM Error</h2>
			  
			  <div id="PSM_PPM_Error_CountsLoadingBlock">
			  	Loading
			  </div>
			  
	 		  <table  id="PSM_PPM_Error_CountsBlock" class="table-no-border-no-cell-spacing-no-cell-padding" style="">
			  </table>			  
			 
			 </c:when>
			 <c:otherwise>
			 	<%-- 
			   		No Scans so not showing "M/Z for PSMs Per Link Type"
			   	--%>
			   	
			   	<h2>PPM Error</h2>
			   	
			   <script id="NO_PSM_PPM_Error_CountsBlock" type="text/text">__ContentsNotRead__</script>
				
				No Scans so not showing "PPM Error for PSMs Per Link Type"
				
				<%-- No Data Found Charts --%>
				<%-- --%>  
				<%-- 
				 <table class="table-no-border-no-cell-spacing-no-cell-padding" style="">
				  <tr>
				  	<c:set var="noDataLinkType" value="crosslink" />
				  	<%@ include file="/WEB-INF/jsp-includes/viewQC_MZ_Data_NoDataAvailable.jsp" %>
				  	<c:set var="noDataLinkType" value="looplink" />
				  	<%@ include file="/WEB-INF/jsp-includes/viewQC_MZ_Data_NoDataAvailable.jsp" %>
				  	<c:set var="noDataLinkType" value="unlinked" />
				  	<%@ include file="/WEB-INF/jsp-includes/viewQC_MZ_Data_NoDataAvailable.jsp" %>
				   </tr>
				  </table>
				--%>			   
		
			 </c:otherwise>
			</c:choose>
						
			
			</div> <%-- close <div class="project-info-block  collapsable_jq" > --%>
		  </div> <%-- close <div class="top-level-container collapsable_container_jq" > --%>

		</div>   <%-- END: PSM level Statistics --%>

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
			  <%-- 
			  <div >
			  	Peptide lengths Per Link Type
			  </div>
			  --%>
			  <div id="PeptideLengthsCountsLoadingBlock">
			  	Loading
			  </div>
			  
	 		  <table  id="PeptideLengthsCountsBlock" class="table-no-border-no-cell-spacing-no-cell-padding" style="">
			  </table>			  
			  
			</div> <%-- close <div class="project-info-block  collapsable_jq" > --%>
		  </div> <%-- close <div class="top-level-container collapsable_container_jq" > --%>

		</div>   <%-- END: Peptide level Statistics --%>

	</div>  <%--  Close   <div class="overall-enclosing-block">  --%>
	
<script id="PeptideCleavageEntryTemplate" type="text/text">
	<td style="padding: 4px;">
	 <div class=" chart-standard-container-div chart_outer_container_for_download_jq " > 
	  <div class="chart_container_jq chart_container_for_download_jq">
	  </div>
	  <%@ include file="/WEB-INF/jsp-includes/chartDownloadHTMLBlock.jsp" %>
	 </div>
	</td>
</script>	
	
<script id="PSMChargeStatesCountsEntryTemplate" type="text/text">
	<td style="padding: 4px;">
	 <div class=" chart-standard-container-div chart_outer_container_for_download_jq " > 
	  <div class="chart_container_jq chart_container_for_download_jq">
	  </div>
	  <%@ include file="/WEB-INF/jsp-includes/chartDownloadHTMLBlock.jsp" %>
	 </div>
	</td>
</script>

<script id="PSM_M_Over_Z_CountsEntryTemplate" type="text/text">
	<td style="padding: 4px;">
	 <div class=" chart-standard-container-div chart_outer_container_for_download_jq " > 
	  <div class="chart_container_jq chart_container_for_download_jq">
	  </div>
	  <%@ include file="/WEB-INF/jsp-includes/chartDownloadHTMLBlock.jsp" %>
	 </div>
	</td>
</script>


<script id="PSM_PPM_Error_CountsEntryTemplate" type="text/text">
	<td style="padding: 4px;">
	 <div class=" chart-standard-container-div chart_outer_container_for_download_jq " > 
	  <div class="chart_container_jq chart_container_for_download_jq">
	  </div>
	  <%@ include file="/WEB-INF/jsp-includes/chartDownloadHTMLBlock.jsp" %>
	 </div>
	</td>
</script>

<script id="PeptideLengthsCountsEntryTemplate" type="text/text">
	<td style="padding: 4px;">
	 <div class=" chart-standard-container-div chart_outer_container_for_download_jq " > 
	  <div class="chart_container_jq chart_container_for_download_jq">
	  </div>
	  <%@ include file="/WEB-INF/jsp-includes/chartDownloadHTMLBlock.jsp" %>
	 </div>
	</td>
</script>
							
<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>
