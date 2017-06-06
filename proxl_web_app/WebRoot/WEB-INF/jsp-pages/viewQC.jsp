<%@page import="org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants"%>
<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

<%-- viewQC.jsp --%>

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
						
						<input type="button" value="${ UpdateButtonText }"  onclick="viewQCPageCode.refreshData()" >

						<%@ include file="/WEB-INF/jsp-includes/sharePageURLShortenerButtonFragment.jsp" %>
					</td>
				</tr>
							
			</table>
			
		</div>
	
		<hr>
		
		<div >

		  <div class="top-level-container collapsable_container_jq" >
			
			<div  class="collapsable-link-container top-level-collapsable-link-container collapsable_link_container_jq" > 
				<a href="javascript:" class="top-level-collapsable-link collapsable_collapse_link_jq" 
						<%-- style="display: none;" --%>
					><img  src="${ contextPath }/images/icon-collapse.png"></a>
				<a href="javascript:" class="top-level-collapsable-link collapsable_expand_link_jq" 
						style="display: none;"
					><img  src="${ contextPath }/images/icon-expand.png"></a>
			
			</div>
			<div class="top-level-label">
			  Digestion Statistics
			</div>

			<div class="top-level-label-bottom-border" ></div>
								
			<div class="project-info-block  collapsable_jq" > <%-- style="display: none;" --%>
			
			  <div >
			  </div>
			  <div id="missingCleavageReportedPeptidesCountLoadingBlock">
			  	Loading
			  </div>
			  <div id="missingCleavageReportedPeptidesCountBlock" style="display: none;">
			  	<table  id="missingCleavageReportedPeptidesCountTable" >
			  	 <thead>
			  	  <tr>
			  	   <th>Link Type</th>
			  	   <th class=" count-display " >Peptides w/ Missed Cleavage</th>
			  	   <th class=" count-display " >Missed Cleavages</th>
			  	   <th class=" count-display " >Total Peptides</th>
			  	   <th class=" count-display " >Missed Cleavage PSM Count</th>
			  	  </tr>
			  	 </thead>
			  	 <tbody>
			  	 </tbody>
			  	</table>
			  </div>
			</div>
		  </div>

		</div>

	</div>

<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>
