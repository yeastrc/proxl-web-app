<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>
<%--

	Page include to go with JS code createTooltipForProteinNames.js
	
<%@ include file="/WEB-INF/jsp-includes/proteinNameTooltipDataForJSCode.jsp" %>

 --%>
<input type="hidden" id="protein_listing_webservice_base_url_set" value="<c:out value="${ protein_listing_webservice_base_url_set }"></c:out>">

<c:forEach var="searchId" items="${ searchIdsForProteinListing }">
	<input type="hidden" class=" search_id_input_field_protein_listing_tooltip_jq " value="<c:out value="${ searchId }"></c:out>" >
</c:forEach>		
