<html>
 <head>
 	<title>ProXL DB</title>
 </head>
 <body>
 
  	
	<%--  Store the context path of the web app in a javascript variable named contextPathJSVar --%>

	<script type="text/javascript" >
	  var contextPathJSVar = "${contextPath}";
	  </script>
	  
	<%-- field populated with action, computed from other data in URL --%>
	<input type="hidden" id="project_id" value="${ project_id }">

 	<script type="text/javascript" src="${contextPath}/js/z_redirect_project_id_FromPageToQueryString.js"></script>

 
 </body>
 

</html>