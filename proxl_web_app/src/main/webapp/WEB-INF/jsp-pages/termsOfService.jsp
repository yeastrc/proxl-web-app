<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>


<%--  termsOfService.jsp  --%>


<html>
<head>

 <%@ include file="/WEB-INF/jsp-includes/head_section_include_every_page.jsp" %>
 
 	<title>ProXL DB - Terms of Service</title>

 <link REL="stylesheet" TYPE="text/css" HREF="static/css_generated/global.css?x=${cacheBustValue}">

	<%--  Loaded in head_section_include_every_page.jsp   --%>
	<%-- <script type="text/javascript" src="js/jquery-1.11.0.min.js"></script>  --%>
	 
	
	
</head>

<body class="terms-of-service-page inset-page"> <%-- "inset-page" is for pages with an 'inset' look --%>

 <%@ include file="/WEB-INF/jsp-includes/body_section_start_include_every_page.jsp" %>


<div class="inset-page-main-outermost-div"> <%--  Closed in footer_main.jsp --%>

<div class="page-content-outer-container" >	
 <div class=" page-terms-of-service-container page-content-container" >	
  <div class="page-content" >	

	<div class="logo-large-container" >
		<img src="images/logo-large.png" />
	</div>
  	
  	<div  style="position: relative;" class="page-label">
	  <c:choose>
	   <c:when test="${ not empty termsOfServiceText }">
	   
		  <div class=" terms-of-service-header ">
		  	Terms of Service
		  </div>
		  
	   	  <div class=" terms-of-service-close-button ">
	   	  	<input type="button" value="Close" onclick="window.close();">
	   	  </div>
	   	 
		  <div class=" terms-of-service-text ">
	  	  	<c:out value="${ termsOfServiceText }" escapeXml="false"></c:out>
	      </div>
	      
	   	  <div class=" terms-of-service-close-button ">
	   	  	<input type="button" value="Close" onclick="window.close();">
	   	  </div>	      
	   </c:when>
	   <c:otherwise>
		  <div class=" terms-of-service-text ">
	   		No Terms of Service
	   	  </div>
	   	  <div class=" terms-of-service-close-button ">
	   	  	<input type="button" value="Close" onclick="window.close();">
	   	  </div>	      
	   </c:otherwise>
	  </c:choose>
  	</div>
  	
  </div>
 </div>
</div>

<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>
