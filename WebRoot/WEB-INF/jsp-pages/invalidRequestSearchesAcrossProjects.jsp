<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %>

<%--  invalidRequestSearchesAcrossProjects.jsp  --%>


<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>


<html>
<head>

 <%@ include file="/WEB-INF/jsp-includes/head_section_include_every_page.jsp" %>

 	<title>ProXL DB</title>

 <link REL="stylesheet" TYPE="text/css" HREF="${ contextPath }/css/global.css">

	<%--  Loaded in head_section_include_every_page.jsp   --%>
	<%-- <script type="text/javascript" src="${ contextPath }/js/jquery-1.11.0.min.js"></script>  --%>
	 
	
</head>

<body class="reset-password-code-fail-page inset-page"> <%-- "inset-page" is for pages with an 'inset' look --%>

 <%@ include file="/WEB-INF/jsp-includes/body_section_start_include_every_page.jsp" %>



<div class="page-content-outer-container" >	
 <div class="page-content-container" >	
  <div class="page-content" >	

	<div class="logo-large-container" >
		<img src="${ contextPath }/images/logo-large.png" />
	</div>
  	
  	<div  style="position: relative;" class="page-label">
  	
  		<div style="font-weight: bold;">There was a problem:</div>

  		<br>
  		
  		<div >
		  The list of searches requested spans projects which is not allowed.
		  <br><br>
		  Please start over.
		    <br><br>
	  
		  <c:choose>
		   <c:when test="${ not empty param.project_id }">
		   	
		   
		   		 <a href="viewProject.do?project_id=<c:out value="${ param.project_id }"></c:out>" >home</a>
		   </c:when>
		   <c:otherwise>
		   
			  <a href="home.do" >home</a>
			
		   </c:otherwise>
		  
		  </c:choose>
  		</div>
	</div>
	
  </div>
  
 </div>
</div>



</body>

</html>


