
<%--  redirect_pre_generic_image_structure_ToGenericURL.jsp --%>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>


<html>
 <head>
 	<title>ProXL DB</title>
 </head>
 <body>
 
  	
	<%--  Store the context path of the web app in a javascript variable named contextPathJSVar --%>

	<script type="text/javascript" >
	  var contextPathJSVar = "${contextPath}";
	  </script>
	  
	<input type="hidden" id="redirectURL" value="<c:out value="${ redirectURL }"></c:out>">
	  
	<input type="hidden" id="cutoffValuesRootLevelJSONRootString" value="<c:out value="${ cutoffValuesRootLevelJSONRootString }"></c:out>">

 	<script type="text/javascript" src="${contextPath}/js/z_redirect_pre_generic_image_structure_ToGenericURL.js?x=${cacheBustValue}"></script>

 
 </body>
 

</html>