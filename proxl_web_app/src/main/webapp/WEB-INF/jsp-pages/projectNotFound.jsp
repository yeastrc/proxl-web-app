<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

<%--  projectNotFound.jsp --%>
<%
	response.setStatus( 404 );
%>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>


<html>
<head>

 <%@ include file="/WEB-INF/jsp-includes/head_section_include_every_page.jsp" %>

 	<title>ProXL DB - Project not found</title>

 <link REL="stylesheet" TYPE="text/css" HREF="static/css_generated/global.css?x=${cacheBustValue}">

	<%--  Loaded in head_section_include_every_page.jsp   --%>
	<%-- <script type="text/javascript" src="js/jquery-1.11.0.min.js"></script>  --%>
	 
	
	
</head>

<body class="reset-password-code-fail-page inset-page"> <%-- "inset-page" is for pages with an 'inset' look --%>

 <%@ include file="/WEB-INF/jsp-includes/body_section_start_include_every_page.jsp" %>


<div class="inset-page-main-outermost-div"> <%--  Closed in footer_main.jsp --%>


<div class="page-content-outer-container" >	
 <div class="page-content-container" style="width: 699px;" >	
  <div class="page-content" >	

	<div class="logo-large-container" >
		<img src="images/logo-large.png" />
	</div>
  	
  	<div  style="position: relative; margin-left: 10px; margin-right: 10px; text-align: center;" class="page-label">
  	
	  	<div >
		  There is no project with the id you are accessing: <c:out value="${ projectId_FromViewProjectAction }"></c:out>
		</div>
		<c:if test="${ not empty adminEmailAddress }">
			<div style="margin-top: 15px;">
				If you believe this is in error, please email us at
				<a href="mailto:<c:out value="${ adminEmailAddress }"></c:out>" target="_top"><c:out value="${ adminEmailAddress }"></c:out></a>.
			</div> 
		</c:if>
		<div style="margin-top: 15px; margin-bottom: 20px;"> 
			<a href="home.do" >Go back to proxl home page</a>.
		</div> 

	</div>
  
  </div>
  </div>
  
 </div>
  
<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>


