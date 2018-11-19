
<%--  imageAndStructureNavLinks.jsp --%>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

		
	<%--  Build a set of the query params for Image and Structure links --%>
													
	<c:url value="" var="imageAndStructureQueryParams">
			
		<c:forEach var="projectSearchId" items="${ formDataForImageStructure_QC.projectSearchId }" >
			<c:param name="projectSearchId"   value="${projectSearchId}" />
		</c:forEach>
		
		<%--  Do Not Sort Value if equal PeptideProteinCommonForm.DO_NOT_SORT_PROJECT_SEARCH_IDS_YES in URL --%>
		<c:if test="${ not empty ImageAndStructure_QC_DoNotSortValue }">
			<c:param name="ds"   value="${ImageAndStructure_QC_DoNotSortValue}" />
		</c:if>
	</c:url>
	
	<c:set var="imagePageURL">image.do<c:out
					value="${ imageAndStructureQueryParams }" ></c:out>#<c:out
					value="${ imageAndStructureAndQC_QueryJSON }" ></c:out></c:set>
	
	<c:set var="structurePageURL">structure.do<c:out
					value="${ imageAndStructureQueryParams }" ></c:out>#<c:out
					value="${ imageAndStructureAndQC_QueryJSON }" ></c:out></c:set>


	<%--  Use for Merged searches --%>
	<c:set var="imagePageURLFinal" value="${ imagePageURL }" />
	<c:set var="structurePageURLFinal" value="${ structurePageURL }" />
	
	<c:choose>
		<c:when test="${ not empty ImageAndStructure_QC_SingleProjectSearchId }">
	
			<%--  Single searches so look up default Page URL --%>
 		
			<c:set var="imagePageURLFinal"><proxl:defaultPageUrl pageName="/image" projectSearchId="${ ImageAndStructure_QC_SingleProjectSearchId }">${ imagePageURL }</proxl:defaultPageUrl></c:set>

			<c:set var="structurePageURLFinal"><proxl:defaultPageUrl pageName="/structure" projectSearchId="${ ImageAndStructure_QC_SingleProjectSearchId }">${ structurePageURL }</proxl:defaultPageUrl></c:set>
		
		</c:when>
	</c:choose>

	<%--  Image Link --%>	

	[<a class="tool_tip_attached_jq" data-tooltip="Graphical view of links between proteins" 
		href="${ contextPath }/${ imagePageURLFinal }"
				
		>Image View</a>]  					
		
					
				<%--  Structure Link --%>	

	<c:choose>
	 <c:when test="${ showStructureLink }">
			
		[<a class="tool_tip_attached_jq" data-tooltip="View data on 3D structures" 
			href="${ contextPath }/${ structurePageURLFinal }"
					
					
			>Structure View</a>]  
								
	 </c:when>
	 <c:otherwise>
	 	
							 	
		<%@ include file="/WEB-INF/jsp-includes/structure_link_non_link.jsp" %>
							 	
	 </c:otherwise>
	</c:choose>

