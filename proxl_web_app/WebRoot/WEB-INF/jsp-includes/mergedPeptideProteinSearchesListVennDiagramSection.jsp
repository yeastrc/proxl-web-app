<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

<%--  mergedPeptideProteinSearchesListVennDiagramSection.jsp  --%>

<%--  The Venn Diagram with searches to left on Merged Peptide, Merged Crosslink Protein, and Merged Loop Protein Pages --%>


<%--  If no Venn diagram data, lists the searches and their counts and color blocks --%>

<script type="text/javascript" src="${ contextPath }/js/mergedPeptideProteinSearchesListVennDiagramSection.js?x=${cacheBustValue}"></script>

<c:choose>
	 <c:when test="${ not empty vennDiagramDataToJSON }">

	<%-- Have Venn Diagram, list legend to the left in vertical list --%>

	<div  class=" searches_sort_list_container_jq " style="float:left;">
	

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

	<div id="searches_intersection_venn_diagram" >
	
	
	</div>
	
	<div style="clear:both;"></div>
 
 </c:when>
 <c:otherwise>

	<%-- No Venn Diagram, list legend in horizontal list list --%>
 
  <div style="margin-top: 5px;">	

	 <c:forEach items="${ searchCounts }" var="searchCount"  varStatus="searchVarStatus">
	 
	  <span style="white-space: nowrap;  padding-right: 20px;">

		<%--  Include file is dependent on containing loop having varStatus="searchVarStatus"  --%>
		<%@ include file="/WEB-INF/jsp-includes/mergedSearch_SearchIndexToSearchColorCSSClassName.jsp" %>
					
			
			<span style="margin-right: 10px; padding-left: 10px; padding-right: 10px;" class="${ backgroundColorClassName }"></span>
		  (Search <bean:write name="searchCount" property="searchId" />: <bean:write name="searchCount" property="count" />)
		  
	  </span>
	  
	 </c:forEach>

  </div>
 
 </c:otherwise>
</c:choose>
	
