
<%--  redirect_pre_generic_image_structure_ToGenericURL.jsp --%>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>


<html>
 <head>
 
 	<%--  Include file that is really included into <head> of every page --%>
  	
  	<%@ include file="/WEB-INF/jsp-includes/head_section_include_every_page_light.jsp" %>
  	
	
 	<title>ProXL DB</title>
 </head>
 <body>
 
  	
	<input type="hidden" id="redirectURL" value="<c:out value="${ redirectURL }"></c:out>">
	  
	<input type="hidden" id="cutoffValuesRootLevelJSONRootString" value="<c:out value="${ cutoffValuesRootLevelJSONRootString }"></c:out>">

 	<script type="text/javascript" src="js/z_redirect_pre_generic_image_structure_ToGenericURL.js?x=${cacheBustValue}"></script>

 
 </body>
 

</html>