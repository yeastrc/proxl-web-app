<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %>

<%-- projectReadProcessCodeFailure.jsp 

  The Public Access Code Struts Action  Failure page

--%>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>


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
  	
  	
  		<div style="font-weight: bold;">That link is not valid.</div>

  		<br>
  		
  		<div >
		  	<logic:messagesPresent message="false">
		  		
			     <html:messages id="message" >
			     	<div>
			         <bean:write name="message" filter="false"/>
			        </div>
			     </html:messages>
		 	</logic:messagesPresent>
  		</div>
	</div>
	
  </div>
  
 </div>
</div>



</body>

</html>
