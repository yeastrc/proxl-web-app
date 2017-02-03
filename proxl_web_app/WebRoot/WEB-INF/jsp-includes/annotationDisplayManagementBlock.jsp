
<%--  annotationDisplayManagementBlock.jsp  --%>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

<%--  
	Block for user choosing which annotation types to display

--%>

<c:if test="${ coveragePageForAnnDispMgmt }">
	<script type="text/text" id="annotation_mgmt_coverage_page_script_tag"></script>

</c:if>

<c:if test="${ not coveragePageForAnnDispMgmt }">

 <c:choose>
  <c:when test="${ imageOrStructurePageForAnnDispMgmt }"> 
  		<%--  Format for image and structure pages --%>
	<div style="margin-top: 10px; margin-bottom: 10px;">
	  <table><tr><td>
		<a href="javascript:" onclick="annotationDataDisplayProcessingCommonCode.openAnnDisplayDataOverlay( { clickedThis : this } )" 
			>Change Displayed Peptide and PSM data</a>
	  </td></tr></table>		
	</div>
  </c:when>
  <c:otherwise>
		<%--  Format for Other Than image and structure pages --%>
	<div style="margin-top: 10px; margin-bottom: 10px;">
		<a href="javascript:" onclick="annotationDataDisplayProcessingCommonCode.openAnnDisplayDataOverlay( { clickedThis : this } )" 
			>Change Displayed Peptide and PSM data</a>
	</div>
  </c:otherwise>
 </c:choose>
 
</c:if>

<%--  Annotation Display Overlay --%>

<%--  Annotation Display Overlay Background --%>

<div id="annotation_displays_modal_dialog_overlay_background_jq"
	class="annotation-data-display-modal-dialog-overlay-background  " style="display: none;"  >
</div>

<%--  Annotation Display Overlay Div --%>

<div id="annotation_displays_modal_dialog_overlay_div_jq"
	class=" annotation-data-display-modal-dialog-overlay-div overlay-outer-div  " style="display: none; "  >


	<div class="annotation-data-display-modal-dialog-overlay-header" style="width:100%; " >
		
		<h1 class="annotation-data-display-modal-dialog-overlay-X-for-exit-overlay " 
			onclick="annotationDataDisplayProcessingCommonCode.cancel_RestoreUserValues( { clickedThis : this } )"
			>X</h1>
		
		<h1 class="annotation-data-display-modal-dialog-overlay-header-text" 
			>Change Displayed Data</h1>
			
	</div>

	<div id="annotation_displays_modal_dialog_overlay_body" 
		class="annotation-data-display-modal-dialog-overlay-body" >
	
	 <c:if test="${ not imageOrStructurePageForAnnDispMgmt }">
		<div style="text-align: center;">
			Changing these values and saving will cause a page reload
		</div>
	 </c:if>
	  
	 <c:if test="${ fn:length(annDisplayDataUserSelctn.annotationDisplayUserSelectionDetailsPerSearchList) > 1 }">
		<c:set var="moreThanOneSearch" value="${ true }" />
		<c:set var="initialHidePerSearch" value="display:none;" />
	 </c:if>

	  <c:if test="${ moreThanOneSearch }">
	  	<%-- put a <select> on the page for the user to choose which search to ... --%>
		<div >
			Search: 
			<select onchange="annotationDataDisplayProcessingCommonCode.searchSelectorChanged( { selectThis : this } )">
			  <c:forEach  var="annDisplayPerSearchItem"  
			  	items="${ annDisplayDataUserSelctn.annotationDisplayUserSelectionDetailsPerSearchList }" varStatus="annDisplItemVarStatus">
				<c:set var="search" value="${ annDisplayPerSearchItem.searchDTO }"></c:set>
				<option value="${ search.projectSearchId }"><c:out value="${ search.name }"></c:out></option>
			  </c:forEach>
			</select>
		</div>
	  </c:if>
		  	
	  <c:forEach  var="annDisplayPerSearchItem"  items="${ annDisplayDataUserSelctn.annotationDisplayUserSelectionDetailsPerSearchList }" varStatus="annDisplItemVarStatus">
				
		<c:set var="search" value="${ annDisplayPerSearchItem.searchDTO }"></c:set>
		
		<%-- Data for one search --%>
		
		<div id="annotation_displays_modal_dialog_overlay_div_search_id_${ search.projectSearchId }"
			style="${ initialHidePerSearch }"
			class=" per_search_container_jq "
			data-search_id="${ search.projectSearchId }">
<%-- 		
		  <c:if test="${ moreThanOneSearch }">
			<div >
				Search: <c:out value="${ search.name }"></c:out>
			</div>
		  </c:if>
--%>		  
	 <%-- 
		<table class="table-no-border-no-cell-spacing-no-cell-padding" style="" >
	--%>							
		 		<%--  Display Annotation Display data --%>

	  		<%--   PSM Block --%>
	  				 
			<c:set var="filterTypeDisplay" >PSM</c:set>
			<c:set var="filterTypeHTML_Id" >psm</c:set>
			<c:set var="annotationDisplayDataList" value="${ annDisplayPerSearchItem.allPsmAnnotationTypeDisplay }"/> 
			
			<c:if test="${ not empty annotationDisplayDataList }">
				<%@ include file="/WEB-INF/jsp-includes/annotationDisplayBlock_PsmPeptide_SubPart.jsp" %>
			</c:if>

	  		<%--   Peptide Block --%>
	  				 
			<c:set var="filterTypeDisplay" >Peptide</c:set>
			<c:set var="filterTypeHTML_Id" >peptide</c:set>
			<c:set var="annotationDisplayDataList" value="${ annDisplayPerSearchItem.allPeptideAnnotationTypeDisplay }"/> 
			
			<c:if test="${ not empty annotationDisplayDataList }">
				<%@ include file="/WEB-INF/jsp-includes/annotationDisplayBlock_PsmPeptide_SubPart.jsp" %>
			</c:if>

		</div>
			
	
	  </c:forEach>
	  
	  <c:choose>
		<c:when test="${ moreThanOneSearch }">
		  <c:set var="tooltipAddition" >(for all searches)</c:set>
		</c:when>
		<c:otherwise>
		</c:otherwise>
	  </c:choose>
		  
		<div >							
			<input type="button" value="Save" 
				class=" save_user_annotation_display_values_button_jq tool_tip_attached_jq "
				onclick="annotationDataDisplayProcessingCommonCode.saveUserValues( { clickedThis : this } )"
				data-tooltip="Save ${ tooltipAddition }"
				>
			<input type="button" value="Cancel" 
				class=" tool_tip_attached_jq "
				onclick="annotationDataDisplayProcessingCommonCode.cancel_RestoreUserValues( { clickedThis : this } )" 
				data-tooltip="Cancel ${ tooltipAddition }"
				>
			<input type="button" value="Reset to Defaults"
				class=" tool_tip_attached_jq " 
				onclick="annotationDataDisplayProcessingCommonCode.setToDefaultValues( { clickedThis : this } )" 
				data-tooltip="Reset to Defaults ${ tooltipAddition }"
				>
		</div>				  

	</div>
</div>  
							
<script id="annotation_displays_defaults_json_jq"
	 type="text/text">${ annDisplayDataUserSelctn.defaultAnnTypeIdDisplayJSONString }</script>

		<%--  Handlebars template for displaying a Single Annotation Data Display value, on the main page --%>
		
<%--			NOT USED

	<script id="annotation_data_display_single_value_display_template"  type="text/x-handlebars-template">
			
	<span class="annotation-data-single-value-display-block {{#if data.description }} tool_tip_attached_jq {{/if}} " 
		{{#if data.description }} data-tooltip="{{ data.description }}" {{/if}}
		 style=" cursor: pointer;" onclick="annotationDataDisplayProcessingCommonCode.openAnnDisplayDataOverlay( { clickedThis : this } )"
				>{{ data.name }}</span>

	</script >		
--%>	

		<%--  Handlebars template for displaying a Single Annotation Data Display value, in the overlay sorting block --%>		
	<script id="annotation_data_display_sort_block_single_value_display_template"  type="text/x-handlebars-template">

		<%@ include file="/WEB-INF/jsp_template_fragments/For_jsp_includes/srchDtlsBlk_tmplat/annDataDsplSrtBlkSnglValDsplTemplate.jsp" %>
	</script >
		  