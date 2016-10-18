<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>


<%--  termsOfService.jsp  --%>


<html>
<head>

 <%@ include file="/WEB-INF/jsp-includes/head_section_include_every_page.jsp" %>
 
 	<title>ProXL DB - Terms of Service</title>

 <link REL="stylesheet" TYPE="text/css" HREF="${ contextPath }/css/global.css?x=${cacheBustValue}">

	<%--  Loaded in head_section_include_every_page.jsp   --%>
	<%-- <script type="text/javascript" src="${ contextPath }/js/jquery-1.11.0.min.js"></script>  --%>
	 
	
	
</head>

<body class="login-page inset-page"> <%-- "inset-page" is for pages with an 'inset' look --%>

 <%@ include file="/WEB-INF/jsp-includes/body_section_start_include_every_page.jsp" %>



<div class="page-content-outer-container" >	
 <div class="page-content-container" >	
  <div class="page-content" >	

	<div class="logo-large-container" >
		<img src="${ contextPath }/images/logo-large.png" />
	</div>
  	
  	<div  style="position: relative;" class="page-label">
	  <c:choose>
	   <c:when test="${ not empty termsOfServiceText }">
		  <div style="margin-bottom: 10px;">
		  	Terms of Service
		  </div>
		  <div >
	  	  	<c:out value="${ termsOfServiceText }" escapeXml="false"></c:out>
	      </div>
	   </c:when>
	   <c:otherwise>
	   		No Terms of Service
	   </c:otherwise>
	  </c:choose>
  	</div>
  	
  </div>
 </div>
</div>

<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>
