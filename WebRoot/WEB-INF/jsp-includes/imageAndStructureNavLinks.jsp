
<%--  imageAndStructureNavLinks.jsp --%>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

		
	<%--  Build a set of the query params for Image and Structure links --%>
													
	<c:url value="" var="imageAndStructureQueryParams">
			
		<c:forEach var="searchId" items="${ formDataForImageStructure.searchIds }" >
			<c:param name="searchIds"   value="${searchId}" />
		</c:forEach>
	</c:url>
	
	<c:set var="imagePageURL">image.do<c:out
					value="${ imageAndStructureQueryParams }" ></c:out>#<c:out
					value="${ imageAndStructureQueryJSON }" ></c:out></c:set>
	
	<c:set var="structurePageURL">structure.do<c:out
					value="${ imageAndStructureQueryParams }" ></c:out>#<c:out
					value="${ imageAndStructureQueryJSON }" ></c:out></c:set>


	<%--  Use for Merged searches --%>
	<c:set var="imagePageURLFinal" value="${ imagePageURL }" />
	<c:set var="structurePageURLFinal" value="${ structurePageURL }" />
	
	<c:choose>
		<c:when test="${ not empty ImageAndStructureSingleSearchId }">
	
			<%--  Single searches so look up default Page URL --%>
		
			<c:set var="imagePageURLFinal"><proxl:defaultPageUrl pageName="/image" searchId="${ ImageAndStructureSingleSearchId }">${ imagePageURL }</proxl:defaultPageUrl></c:set>

			<c:set var="structurePageURLFinal"><proxl:defaultPageUrl pageName="/structure" searchId="${ ImageAndStructureSingleSearchId }">${ structurePageURL }</proxl:defaultPageUrl></c:set>
		
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

