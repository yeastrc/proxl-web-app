<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

<%--  mergedPeptideProteinSearchesListVennDiagramSection.jsp  --%>

<%--  The Venn Diagram with searches to left on Merged Peptide, Merged Crosslink Protein, and Merged Loop Protein Pages --%>


<%--  If no Venn diagram data, lists the searches and their counts and color blocks --%>

<script type="text/javascript" src="${ contextPath }/js/mergedPeptideProteinSearchesListVennDiagramSection.js?x=${cacheBustValue}"></script>

<c:choose>
	 <c:when test="${ not empty vennDiagramDataToJSON }">

	<%-- Have Venn Diagram, list legend to the left in vertical list --%>

	<table  class="table-no-border-no-cell-spacing-no-cell-padding">
	 <tr>
	  <td style="vertical-align: top">
		<div  class=" searches_sort_list_container_jq " >
		
	
		 <c:forEach items="${ searchCounts }" var="searchCount"  varStatus="searchVarStatus">
	
			<%--  Include file is dependent on containing loop having varStatus="searchVarStatus"  --%>
			<%@ include file="/WEB-INF/jsp-includes/mergedSearch_SearchIndexToSearchColorCSSClassName.jsp" %>
						
				
		  <div class=" searches_sort_list_item_jq " style="margin-top: 5px;"
		  	data-project_search_id="${ searchCount.projectSearchId }">	
				<span style="margin-right: 10px; padding-left: 10px; padding-right: 10px; cursor: pointer;" 
					class="${ backgroundColorClassName } search_sort_handle_jq tool_tip_attached_jq "
					data-tooltip="Drag to re-order searches"
					></span>
			  (Search <bean:write name="searchCount" property="searchId" />: <bean:write name="searchCount" property="count" />)
		  </div>
		 </c:forEach>
		</div>	  
	  </td>
	  <td style="vertical-align: top">
	  
		<div  id="searches_intersection_venn_diagram_outer_container" style="position: relative;" >
			<div class=" svg_download_outer_block_jq " style="position: absolute; top: 4px; right: 4px;">
			  <div class="svg-download-block">
				<a href="javascript:" class=" tool_tip_attached_jq  " data-tooltip="Download graphic as file." 
					><img src="images/icon-download-small.png" /></a>
		
				<!-- Overlay that goes under main overlay: display on hover of download icon -->
				<div class="svg-download-options-backing-block">
				</div>
				<!-- Overlay: display on hover of download icon -->
				<span class="svg-download-options-block">
					Choose download file format:
					<a data-tooltip="Download as a JPEG image file." 
						data-download_type="jpeg"
						class="svg-download-option tool_tip_attached_jq venn_diagram_download_link_jq " href="javascript:" style="margin-top:5px;"
						>JPEG</a>
					<a data-tooltip="Download as PDF file suitable for use in Adobe Illustrator or printing." 
						data-download_type="pdf"
						class="svg-download-option tool_tip_attached_jq venn_diagram_download_link_jq " href="javascript:" style="margin-top:5px;"
						>PDF</a>
					<a data-tooltip="Download as PNG image file." 
						data-download_type="png"
						class="svg-download-option tool_tip_attached_jq venn_diagram_download_link_jq " href="javascript:" style="margin-top:5px;"
						>PNG</a>
					<a data-tooltip="Download as scalable vector graphics file suitable for use in Inkscape or other compatible software." 
						data-download_type="svg"
						class="svg-download-option tool_tip_attached_jq venn_diagram_download_link_jq " href="javascript:" style="margin-top:5px;"
						>SVG</a>
				</span>
			  </div>
			</div>	
		 			
			<div id="searches_intersection_venn_diagram" >
			</div>
		
		<%--
		--%>
		</div>
	  </td>
	 </tr>
	</table>
		
 
 </c:when>
 <c:otherwise>

	<%-- No Venn Diagram, list legend in horizontal list list --%>
 
  <div  class=" searches_sort_list_container_jq "  style="margin-top: 5px;">	

	 <c:forEach items="${ searchCounts }" var="searchCount"  varStatus="searchVarStatus">
	 
	  <span class=" searches_sort_list_item_jq " style="white-space: nowrap;  padding-right: 20px;"
	  	data-project_search_id="${ searchCount.projectSearchId }">

		<%--  Include file is dependent on containing loop having varStatus="searchVarStatus"  --%>
		<%@ include file="/WEB-INF/jsp-includes/mergedSearch_SearchIndexToSearchColorCSSClassName.jsp" %>
					
			
			<span style="margin-right: 10px; padding-left: 10px; padding-right: 10px; cursor: pointer;" 
				class="${ backgroundColorClassName } search_sort_handle_jq tool_tip_attached_jq "
				data-tooltip="Drag to re-order searches"
				></span>
		  (Search <bean:write name="searchCount" property="searchId" />: <bean:write name="searchCount" property="count" />)
		  
	  </span>
	  
	 </c:forEach>

  </div>
 
 </c:otherwise>
</c:choose>
	
