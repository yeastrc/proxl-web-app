<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

<%--  resetPasswordChangePassword.jsp


/WEB-INF/jsp-pages/user_account/resetPasswordChangePassword.jsp

 --%>
<html>
<head>

 <%@ include file="/WEB-INF/jsp-includes/head_section_include_every_page.jsp" %>

 	<title>ProXL DB - Reset Password</title>

 <link REL="stylesheet" TYPE="text/css" HREF="${ contextPath }/css/global.css">

	<%--  Loaded in head_section_include_every_page.jsp   --%>
	<%-- <script type="text/javascript" src="${ contextPath }/js/jquery-1.11.0.min.js"></script>  --%>
	 
	
	
<%-- 
		<script type="text/javascript" src="${ contextPath }/js/handleServicesAJAXErrors.js"></script>
--%>		
		<script type="text/javascript" src="${ contextPath }/js/user_account/userResetPasswordChangePasswordPage.js"></script>
			
</head>

<body class="reset-password-page inset-page"> <%-- "inset-page" is for pages with an 'inset' look --%>

 <%@ include file="/WEB-INF/jsp-includes/body_section_start_include_every_page.jsp" %>


<input type="hidden" id="resetPasswordTrackingCode" value="<c:out value="${ resetPasswordTrackingCode }"></c:out>"> 

<div class="page-content-outer-container" >	
 <div class="page-content-container" >	
  <div class="page-content" >	

	<div class="logo-large-container" >
		<img src="${ contextPath }/images/logo-large.png" />
	</div>
  	
  	<div  style="position: relative;" class="page-label">
  	
  		<div class="error-message-container error_message_container_jq" id="error_message_all_fields_required">
  			<div class="error-message-inner-container" >
  				<div class="error-message-close-x error_message_close_x_jq">X</div>
	  			<div class="error-message-text" >All fields are required</div>
	  		</div>
	  	</div>
  		<div class="error-message-container error_message_container_jq" id="error_message_password_confirm_password_not_match">
  			<div class="error-message-inner-container" >
  				<div class="error-message-close-x error_message_close_x_jq">X</div>
	  			<div class="error-message-text" >Password and Confirm Password must match</div>
	  		</div>
	  	</div>

  		<div class="error-message-container error_message_container_jq" id="error_message_from_server">
  			<div class="error-message-inner-container" >
  				<div class="error-message-close-x error_message_close_x_jq">X</div>
	  			<div class="error-message-text" id="error_message_from_server_text"></div>
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
	  			<div class="success-message-text" >Password updated</div>
		  	</div>
	  	</div>	  
	  		  	
  		Enter new password
  	</div>
  	

  	
 	<form action="javascript:resetPasswordChangePasswordFormSubmit()" >
 				<input type="password" value="" placeholder="New Pasword" class="input-field input_field_jq"  id="password_change_field"  maxlength="40"><br>
				<input type="password" value="" placeholder="Confirm New Pasword" class="input-field input_field_jq"  id="password_confirm_field"  maxlength="40"><br>
		
	 	<INPUT TYPE="submit" class="submit-button" VALUE="Change Password" id="change_password_button">
	</form>

<%-- 
	<form action="" >
				<input type="password" value="" placeholder="New Pasword" class="input-field input_field_jq"  id="password_change_field"  maxlength="40"><br>
				<input type="password" value="" placeholder="Confirm New Pasword" class="input-field input_field_jq"  id="password_confirm_field"  maxlength="40"><br>
		
	 	<INPUT TYPE="button" class="submit-button" VALUE="Change Password" id="change_password_button">
	</form>
--%>
	
  </div>
  <div class="bottom-tab">
		<a href="user_loginPage.do?useDefaultURL=true" >Sign In</a>
  </div>
  
 </div>
</div>

<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>
