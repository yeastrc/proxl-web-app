
<%--  qc_NavLinks.jsp --%>

<%@page import="org.yeastrc.xlink.www.constants.PageLinkTextAndTooltipConstants"%>
<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

		
	<%--  Build a set of the query params for QC links --%>
			
	<c:url value="" var="qc_QueryParams">
		<c:forEach var="projectSearchId" items="${ formDataForImageStructure_QC.projectSearchId }" >
			<c:param name="projectSearchId"   value="${ projectSearchId }" />
		</c:forEach>
		<c:if test="${ not empty formDataForImageStructure_QC.ds }" >
			<c:param name="ds"   value="${ formDataForImageStructure_QC.ds }" />
		</c:if>
	</c:url>
	
	<c:set var="qc_PageURL">qc.do<c:out
					value="${ qc_QueryParams }" ></c:out>#<c:out
					value="${ imageAndStructureAndQC_QueryJSON }" ></c:out></c:set>
	
	<%--  Use for Merged searches --%>
	<c:set var="qc_PageURLFinal" value="${ qc_PageURL }" />
	
	<c:choose>
		<c:when test="${ not empty ImageAndStructure_QC_SingleProjectSearchId }">
	
			<%--  Single searches so look up default Page URL --%>
 		
			<c:set var="qc_PageURLFinal"><proxl:defaultPageUrl pageName="/qc" projectSearchId="${ ImageAndStructure_QC_SingleProjectSearchId }">${ qc_PageURL }</proxl:defaultPageUrl></c:set>
		
		</c:when>
	</c:choose>

	<%--  QC Link --%>	

	[<a class="tool_tip_attached_jq" data-tooltip="<%= PageLinkTextAndTooltipConstants.QC_LINK_TOOLTIP %>" 
		href="${ contextPath }/${ qc_PageURLFinal }"
				
		><%= PageLinkTextAndTooltipConstants.QC_LINK_TEXT %></a>]  					
		

