<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %>
<%@page import="org.yeastrc.xlink.www.constants.WebConstants"%>


<%--   !!!!!!!!!!!!!!!   NOT USED  !!!!!!!!!!!!!!!!! --%>




<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>
<html>
<head>

 <%@ include file="/WEB-INF/jsp-includes/head_section_include_every_page.jsp" %>
 
 	<title>ProXL DB</title>

 <link REL="stylesheet" TYPE="text/css" HREF="static/css_generated/global.css?x=${cacheBustValue}">

	<%--  Loaded in head_section_include_every_page.jsp   --%>
	<%-- <script type="text/javascript" src="js/jquery-1.11.0.min.js"></script>  --%>
	 
	
</head>
<body class="login-page inset-page"> <%-- "inset-page" is for pages with an 'inset' look --%>


 <%@ include file="/WEB-INF/jsp-includes/body_section_start_include_every_page.jsp" %>


<div class="inset-page-main-outermost-div"> <%--  Closed in footer_main.jsp --%>

		<%--  This script will update the URL in the input field to include the hash value --%>
		<script>
			$(document).ready(function()  { 
				$("#ajax_error_no_session_saved_url").val( document.URL );
			});
			
		</script>
   
   
<div class="page-content-outer-container" >	
 <div class="page-content-container" >	
  <div class="page-content" >	

	<div class="logo-large-container" >
		<img src="images/logo-large.png" />
	</div>

   			<form action="user_loginPage.do" id="ajax_error_no_session_form" >
			
				<input id="ajax_error_no_session_saved_url" name="<%= WebConstants.PARAMETER_ORIGINAL_REQUESTED_URL %>" type="hidden"
					value="${ intialIncomingURL }">  <%-- 'intialIncomingURL' is set in a Servlet Filter --%>
			</form>

   No User Session.<br><br> 

   If you signed in, you will need to 
	<a id="ajax_error_no_session_login_link" href="javascript:" onclick="$('#ajax_error_no_session_form').submit()" 
				>sign in</a>
    again.<br><br>
   
   If you were given a link to view data on this website, you will need to click that link again
   to restart your session.<br><br>
   
  </div>
  <div class="bottom-tab">
		<a href="http://www.yeastrc.org/proxl_docs/" >Get Help</a>  		
  </div>
  <div class="bottom-tab" style="border-right-width: 0px;">
		<a id="ajax_error_no_session_login_link" href="javascript:" onclick="$('#ajax_error_no_session_form').submit()" 
				>Sign in</a>
  </div>
  
 </div>
</div>
   
<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>
