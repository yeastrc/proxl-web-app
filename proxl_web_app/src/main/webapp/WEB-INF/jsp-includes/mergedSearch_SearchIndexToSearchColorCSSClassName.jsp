<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

<%-- mergedSearch_SearchIndexToSearchColorCSSClassName.jsp  --%>

<%--  Compute the CSS Class name for the background color for this search

		The number for the modulo ("%") must equal the number class name entries in the globals.css file
		for css names starting with "merged-search-search-background-color-"

		The "+ 1" is added since the "index" starts at zero
		
		This must match the code in computeMergedSearchColorIndex.js
 --%>

<c:set var="backgroundColorClassName" >merged-search-search-background-color-<c:out value="${ ( searchVarStatus.index % 9 ) + 1 }" ></c:out></c:set>