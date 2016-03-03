<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

<html>
<head>

 <%@ include file="/WEB-INF/jsp-includes/head_section_include_every_page.jsp" %>
 
 	<title>ProXL DB - Sign In</title>

 <link REL="stylesheet" TYPE="text/css" HREF="${ contextPath }/css/global.css">

	<%--  Loaded in head_section_include_every_page.jsp   --%>
	<%-- <script type="text/javascript" src="${ contextPath }/js/jquery-1.11.0.min.js"></script>  --%>
	 
	
	
<%-- 
		<script type="text/javascript" src="${ contextPath }/js/handleServicesAJAXErrors.js"></script>
--%>		
		<script type="text/javascript" src="${ contextPath }/js/user_account/userLoginPage.js"></script>
		
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
  		<div class="error-message-container error_message_container_jq" id="error_message_username_required">
  			<div class="error-message-inner-container" >
  				<div class="error-message-close-x error_message_close_x_jq">X</div>
	  			<div class="error-message-text" >Username is required</div>
  			</div>
	  	</div>
  		<div class="error-message-container error_message_container_jq" id="error_message_password_required">
  			<div class="error-message-inner-container" >
  				<div class="error-message-close-x error_message_close_x_jq">X</div>
	  			<div class="error-message-text" >Password is required</div>
  			</div>
	  	</div>
  		<div class="error-message-container error_message_container_jq" id="error_message_username_or_password_invalid">
  			<div class="error-message-inner-container" >
  				<div class="error-message-close-x error_message_close_x_jq">X</div>
	  			<div class="error-message-text" >Username or Password is invalid</div>
  			</div>
	  	</div>
  		<div class="error-message-container error_message_container_jq" id="error_message_user_disabled">
  			<div class="error-message-inner-container" >
  				<div class="error-message-close-x error_message_close_x_jq">X</div>
	  			<div class="error-message-text" >User disabled</div>
  			</div>
	  	</div>
  		<div class="error-message-container error_message_container_jq" id="error_message_system_error">
  			<div class="error-message-inner-container" >
  				<div class="error-message-close-x error_message_close_x_jq">X</div>
	  			<div class="error-message-text" >System Error</div>
  			</div>
	  	</div>
  		Please Sign In
  	</div>
  	

 	<form action="javascript:loginPersonFormSubmit()" >
		<input type="hidden" id="requestedURL" value="<c:out value="${ param.requestedURL }" />"/>
		<input type="hidden" id="useDefaultURL"  value="<c:out value="${ param.useDefaultURL }" />"/>
	    <input type="hidden" id="defaultURL" value="home.do"/>

		<input type="text" id="username" placeholder="Username" class="input-field input_field_jq" maxlength="40"/><br> <!--  size="20" size controlled by CSS -->
		<input type="password" id="password" placeholder="Password" class="input-field input_field_jq" maxlength="40" /><br> <!--  size="20" size controlled by CSS -->
		
	 	<INPUT TYPE="submit" class="submit-button" VALUE="Sign In" id="login_person_button">
	</form>


 <%--  	
	<form action="" >
		<input type="hidden" id="requestedURL" value="<c:out value="${ param.requestedURL }" />"/>
		<input type="hidden" id="useDefaultURL"  value="<c:out value="${ param.useDefaultURL }" />"/>
	    <input type="hidden" id="defaultURL" value="home.do"/>

		<input type="text" id="username" placeholder="Username" class="input-field input_field_jq" maxlength="40"/><br> <!--  size="20" size controlled by CSS -->
		<input type="password" id="password" placeholder="Password" class="input-field input_field_jq" maxlength="40" /><br> <!--  size="20" size controlled by CSS -->
		
	 	<INPUT TYPE="button" class="submit-button" VALUE="Sign In" id="login_person_button">
	</form>
--%>


	
  </div>
  <div class="bottom-tab">
		<a href="user_resetPasswordPage.do" >Reset Password</a>  		
  </div>

  
 </div>
</div>
  
<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>
