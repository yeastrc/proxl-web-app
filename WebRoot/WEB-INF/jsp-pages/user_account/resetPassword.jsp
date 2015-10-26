<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

<%--  resetPassword.jsp


/WEB-INF/jsp-pages/user_account/resetPassword.jsp

 --%>
<html>
<head>

 <%@ include file="/WEB-INF/jsp-includes/head_section_include_every_page.jsp" %>

 	<title>ProXL DB - Reset Password</title>

 <link REL="stylesheet" TYPE="text/css" HREF="${ contextPath }/css/global.css">

	<%--  Loaded in head_section_include_every_page.jsp   --%>
	<%-- <script type="text/javascript" src="${ contextPath }/js/jquery-1.11.0.min.js"></script>  --%>
	 
	<%-- <script type="text/javascript" src="${ contextPath }/js/connectToNoOperationService.js"></script> --%>
	
	
<%-- 
		<script type="text/javascript" src="${ contextPath }/js/handleServicesAJAXErrors.js"></script>
--%>		
		<script type="text/javascript" src="${ contextPath }/js/user_account/userResetPasswordPage.js"></script>
			
</head>

<body class="reset-password-page inset-page"> <%-- "inset-page" is for pages with an 'inset' look --%>

 <%@ include file="/WEB-INF/jsp-includes/body_section_start_include_every_page.jsp" %>



<div class="page-content-outer-container" >	
 <div class="page-content-container" >	
  <div class="page-content" >	

	<div class="logo-large-container" >
		<img src="${ contextPath }/images/logo-large.png" />
	</div>
  	
  	<div  style="position: relative;" class="page-label">
  	
  		<div class="error-message-container error_message_container_jq" id="error_message_username_or_email_required">
  			<div class="error-message-inner-container" >
  				<div class="error-message-close-x error_message_close_x_jq">X</div>
	  			<div class="error-message-text" >Username or Email Address is required</div>
		  	</div>
	  	</div>
  		<div class="error-message-container error_message_container_jq" id="error_message_username_and_email_both_populated">
  			<div class="error-message-inner-container" >
  				<div class="error-message-close-x error_message_close_x_jq">X</div>
	  			<div class="error-message-text" >Cannot populate both Username and Email Address</div>
		  	</div>
	  	</div>
  		<div class="error-message-container error_message_container_jq" id="error_message_username_invalid">
  			<div class="error-message-inner-container" >
  				<div class="error-message-close-x error_message_close_x_jq">X</div>
	  			<div class="error-message-text" >Username provided is invalid</div>
		  	</div>
	  	</div>
  		<div class="error-message-container error_message_container_jq" id="error_message_email_invalid">
  			<div class="error-message-inner-container" >
  				<div class="error-message-close-x error_message_close_x_jq">X</div>
	  			<div class="error-message-text" >Email Address provided is invalid</div>
		  	</div>
	  	</div>
  		<div class="error-message-container error_message_container_jq" id="error_message_system_error">
  			<div class="error-message-inner-container" >
  				<div class="error-message-close-x error_message_close_x_jq">X</div>
	  			<div class="error-message-text" >System Error</div>
		  	</div>
	  	</div>
	  	
  		<div class="success-message-container error_message_container_jq" id="success_message_system_success">
  			<div class="success-message-inner-container" >
  				<div class="success-message-close-x error_message_close_x_jq">X</div>
	  			<div class="success-message-text" >Email sent</div>
		  	</div>
	  	</div>	  	
  		Reset your password
  	</div>
  	
  	
	<form action="javascript:resetPasswordFormSubmit()" >
	  <div >
		<input type="text" id="username" placeholder="Username" class="input-field input_field_jq" maxlength="40"/><br> <!--  size="20" size controlled by CSS -->
	  </div>
	  <div  class="page-label" style="margin: 0px; padding: 0px; margin-bottom: 3px;">
	  	or
	  </div>
	  <div >
		<input type="text" id="email" placeholder="Email Address" class="input-field input_field_jq" maxlength="255" /><br> <!--  size="20" size controlled by CSS -->
	  </div>
		
	 	<INPUT TYPE="submit" class="submit-button" VALUE="Reset Password" id="reset_password_button">
	</form>
	
<%--   	
	<form action="" >
	  <div >
		<input type="text" id="username" placeholder="Username" class="input-field input_field_jq" maxlength="40"/><br> <!--  size="20" size controlled by CSS -->
	  </div>
	  <div  class="page-label" style="margin: 0px; padding: 0px; margin-bottom: 3px;">
	  	or
	  </div>
	  <div >
		<input type="text" id="email" placeholder="Email Address" class="input-field input_field_jq" maxlength="255" /><br> <!--  size="20" size controlled by CSS -->
	  </div>
		
	 	<INPUT TYPE="button" class="submit-button" VALUE="Reset Password" id="reset_password_button">
	</form>
--%>

	<div class="page-text" style="margin-bottom: 10px;">
		A link will be emailed to you for resetting your password. The link is valid for 24 hours.
	</div>
	
  </div>
  <div class="bottom-tab">
  		<%-- specify where to go after logging in. --%>
		<a href="listProjects.do" >Sign In</a>  		
  </div>
  
 </div>
</div>

<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>
