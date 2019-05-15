<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %>
<%--
		InternetExplorer_NotSupported_Error.jsp
--%>	

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>


<!DOCTYPE html>

<html>
<head>

 <%@ include file="/WEB-INF/jsp-includes/head_section_include_every_page.jsp" %>
 
 	<title>ProXL DB - Internet Explorer Not Supported</title>

 <link REL="stylesheet" TYPE="text/css" HREF="static/css_generated/global.css?x=${cacheBustValue}">

</head>

<body class=" inset-page"> <%-- "inset-page" is for pages with an 'inset' look --%>

 <%@ include file="/WEB-INF/jsp-includes/body_section_start_include_every_page.jsp" %>


<div class="inset-page-main-outermost-div"> <%--  Closed in footer_main.jsp --%>


	
<div class="page-content-outer-container" >	
 <div class="page-content-container" >	
  <div class="page-content" >	

	<div class="logo-large-container" >
		<img src="images/logo-large.png" />
	</div>
  	
		<div style="margin-top: 20px; margin-bottom: 10px; ">
	  	
	  	  <div style="text-align: center; ">
	  		<div style="font-size: 24px; font-weight: bold; text-align: left; margin-top: 24px; width: 300px; margin-left: auto; margin-right: auto;">
	  		  <div >
	  		  	Internet Explorer 
	  		  </div>
	  		  <div >
	  		  	is not supported. 
	  		  </div>
	  		  <div style="margin-top: 20px;">
	  		  	Please use a different browser like Edge, Chrome, or Firefox.
	  		  </div>
	  		</div>
	  	  </div>
	
		</div>
		
	  </div>

 </div>
</div>
	
<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>

	