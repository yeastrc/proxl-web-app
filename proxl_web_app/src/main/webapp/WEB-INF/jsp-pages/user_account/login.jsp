<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

<html>
<head>

 <%@ include file="/WEB-INF/jsp-includes/head_section_include_every_page.jsp" %>
 
 	<title>ProXL DB - Sign In</title>

 <link REL="stylesheet" TYPE="text/css" HREF="css/global.css?x=${cacheBustValue}">

	<%--  Loaded in head_section_include_every_page.jsp   --%>
	<%-- <script type="text/javascript" src="js/jquery-1.11.0.min.js"></script>  --%>
	 
	
	
	<script type="text/javascript" src="static/js_generated_bundles/user_pages/userLoginPage-bundle.js?x=${ cacheBustValue }"></script>

<%--  Moved to Front End Build Bundles		
	
		<script type="text/javascript" src="js/user_account/userLoginPage.js?x=${cacheBustValue}"></script>
--%>	
		
</head>

<body class="login-page inset-page"> <%-- "inset-page" is for pages with an 'inset' look --%>

 <%@ include file="/WEB-INF/jsp-includes/body_section_start_include_every_page.jsp" %>


<div class="inset-page-main-outermost-div"> <%--  Closed in footer_main.jsp --%>



	<%--  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! --%>
	
	<%--  !!!!!!!!!!    Overlays               !!!!!!!!!! --%>

	<%--  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! --%>
	
	
	
  <%--  Terms of Service Overlay --%>
		

	<%--  Terms of Service Overlay Background --%>
	
	
	<div id="terms_of_service_modal_dialog_overlay_background" class="terms-of-service-modal-dialog-overlay-background" style="display: none;"  >
	
	</div>
	
	
	<%--  Terms of Service Overlay Div --%>
	
	<div style="text-align: center; position: relative;">
	  <div style="text-align: center; position: relative;" >
		<div id="terms_of_service_overlay_div" class=" terms-of-service-overlay-div " style="display: none; "  >
		
			<div id="terms_of_service_overlay_header" class="terms-of-service-overlay-header" style="width:100%; " >
				<h1 id="terms_of_service_overlay_header_text" class="terms-of-service-overlay-header-text" 
					>Terms of Service</h1>
			</div>
			<div id="terms_of_service_overlay_body" class="terms-of-service-overlay-body" >
			
		
				<div style="margin-bottom: 10px;">
					Terms of Service Acceptance required.
				</div>
				
				<div id="terms_of_service_acceptance_required_text" class=" terms-of-service-overlay-text ">
				</div>
				
				<div style="margin-top: 10px; margin-bottom: 10px;">
					<input id="terms_of_service_acceptance_yes_button" class="submit-button terms-of-service-overlay-button"
						type="button" value="Accept Terms of Service">
		
					<input id="terms_of_service_acceptance_no_button"  class="submit-button terms-of-service-overlay-button"
						type="button" value="Reject Terms of Service">
				</div>
				
			</div> <%--  END  <div id="terms_of_service_overlay_body"  --%>
			
		</div>  <%--  END  <div id="terms_of_service_overlay_div"  --%>
	  </div>
	</div>



<div class="page-content-outer-container" >	
 <div class="page-content-container" >	
  <div class="page-content" >	

	<div class="logo-large-container" >
		<img src="images/logo-large.png" />
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
  		<div class="error-message-container error_message_container_jq" id="error_message_no_proxl_account">
  			<div class="error-message-inner-container" >
  				<div class="error-message-close-x error_message_close_x_jq">X</div>
	  			<div class="error-message-text" >No account on Proxl</div>
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
	    <input type="hidden" id="inviteTrackingCode" value="<c:out value="${ param.inviteTrackingCode }" ></c:out>">

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
		<a href="http://www.yeastrc.org/proxl_docs/" >Get Help</a>  		
  </div>
  
  <div class="bottom-tab" style="border-right-width: 0px;">
		<a href="user_resetPasswordPage.do" >Reset Password</a>  		
  </div>
  
  <c:if test="${ userSignupAllowWithoutInvite }">  
  	<div class="bottom-tab" style="border-right-width: 0px;">
		<a href="user_signupPage.do" >Signup</a>
	</div>
  </c:if>
  
 </div>
</div>

<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>
