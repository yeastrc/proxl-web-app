<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %><%-- Always put this directive at the very top of the page --%>

<%--  account.jsp --%>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>


 <c:set var="pageTitle">Manage Account</c:set>

 <c:set var="pageBodyClass" >manage-account-page</c:set>

 <c:set var="headerAdditions">
	
	
	<script type="text/javascript" src="static/js_generated_bundles/user_pages/accountManagementPage-bundle.js?x=${ cacheBustValue }"></script>

<%--  Moved to Front End Build Bundles		
	
	<script type="text/javascript" src="js/handleServicesAJAXErrors.js?x=${cacheBustValue}"></script>

	<script type="text/javascript" src="js/user_account/accountManagementPage.js?x=${cacheBustValue}"></script>
--%>
	
 </c:set>


<%@ include file="/WEB-INF/jsp-includes/header_main.jsp" %>


  <div class="overall-enclosing-block">


	<div class="top-level-label">Manage Account</div>
	
	<div class="top-level-label-bottom-border" ></div>

	<div class="account-info-block" style="position: relative;" >

	  <div style="position: relative; width: 500px;" >
	  		
  		<div class="error-message-container error_message_container_jq" id="error_message_field_empty">
  			<div class="error-message-inner-container" >
	  			<span class="error-message-text" >A value is required
		  			<span class="error-message-close-x error_message_close_x_jq">X</span></span>
		  	</div>
	  	</div>

  		<div class="error-message-container error_message_container_jq" id="error_message_system_error">
  			<div class="error-message-inner-container" >
	  			<span class="error-message-text" >System Error
		  			<span class="error-message-close-x error_message_close_x_jq">X</span></span>
		  	</div>
	  	</div>

	  </div>


		<div  class="value-container value-container-keep-with-next value_container_jq" style="position: relative;">
		
			<div class="value-label value_label_jq">First Name: </div>
			<div  class="current-value-container current_value_container_jq">
				<span class="current_value_span_jq"><c:out value="${ loggedInUser.firstName }"></c:out></span> 
				<a href="javascript:" class="edit_value_jq" ><img src="images/icon-edit-small.png" title="Change First Name"></a>
			</div>
			<div  class="edit-value-container edit_value_container_jq">
				<input type="text" value="" class="edit-value-input-field  edit_value_input_field_jq" id="first-name-change-field"  maxlength="40" >
				<input type="button" value="Submit" id="submit-first-name-change-button">
				<input type="button" value="Cancel" class="cancel_button_jq">
			</div>			
		</div>
		
		
		<div  class="value-container value_container_jq" >
			<div class="value-label value_label_jq">Last Name: </div>
			<div  class="current-value-container current_value_container_jq">
				<span class="current_value_span_jq"><c:out value="${ loggedInUser.lastName }"></c:out></span> 
				<a href="javascript:" class="edit_value_jq" ><img src="images/icon-edit-small.png" title="Change Last Name"></a>
			</div>
			<div  class="edit-value-container edit_value_container_jq">
				<input type="text" value="" class="edit-value-input-field  edit_value_input_field_jq"  id="last-name-change-field"  maxlength="60" >
				<input type="button" value="Submit" id="submit-last-name-change-button">
				<input type="button" value="Cancel" class="cancel_button_jq">
			</div>			
		</div>

		
		<div  class="value-container value_container_jq">
			<div class="value-label value_label_jq" style="position: relative;" >
		
		  		<div class="error-message-container error_message_container_jq" id="error_message_email_already_exists">
		  			<div class="error-message-inner-container" >
			  			<span class="error-message-text" >Another acccount already has that email address
				  			<span class="error-message-close-x error_message_close_x_jq">X</span></span>
				  	</div>
			  	</div>			
				Email Address: 
			</div>
			<div  class="current-value-container current_value_container_jq">
				<span class="current_value_span_jq"><c:out value="${ loggedInUser.email }"></c:out></span> 
				<a href="javascript:" class="edit_value_jq" ><img src="images/icon-edit-small.png" title="Change Email"></a>
			</div>
			<div  class="edit-value-container edit_value_container_jq">
				<input type="text" value="" class="edit-value-input-field  edit_value_input_field_jq" id="email-change-field" maxlength="255">
				<input type="button" value="Submit" id="submit-email-change-button">
				<input type="button" value="Cancel" class="cancel_button_jq">
			</div>			
		</div>

		
		<div  class="value-container value_container_jq">
			<div class="value-label value_label_jq">Organization: </div>
			<div  class="current-value-container current_value_container_jq">
				<span class="current_value_span_jq"><c:out value="${ loggedInUser.organization }"></c:out></span> 
				<a href="javascript:" class="edit_value_jq" ><img src="images/icon-edit-small.png" title="Change Organization"></a>
			</div>
			<div  class="edit-value-container edit_value_container_jq">
				<input type="text" value="" class="edit-value-input-field  edit_value_input_field_jq" id="organization-change-field"  maxlength="2000">
				<input type="button" value="Submit" id="submit-organization-change-button">
				<input type="button" value="Cancel" class="cancel_button_jq">
			</div>			
		</div>

		
		<div  class="value-container value_container_jq">
			<div class="value-label value_label_jq" style="position: relative;" >
		
		  		<div class="error-message-container error_message_container_jq" id="error_message_username_already_exists">
		  			<div class="error-message-inner-container" >
			  			<span class="error-message-text" >Another acccount already has that username
				  			<span class="error-message-close-x error_message_close_x_jq">X</span></span>
				  	</div>
			  	</div>			
			  	Username: 
			</div>
			
			<div  class="current-value-container current_value_container_jq">
				<span class="current_value_span_jq"><c:out value="${ loggedInUser.username }"></c:out></span> 
				<a href="javascript:" class="edit_value_jq" ><img src="images/icon-edit-small.png" title="Change Username"></a>
			</div>
			<div  class="edit-value-container edit_value_container_jq">
				<input type="text" value="" class="edit-value-input-field  edit_value_input_field_jq" id="username-change-field"  maxlength="40">
				<input type="button" value="Submit" id="submit-username-change-button">
				<input type="button" value="Cancel" class="cancel_button_jq">
			</div>			
		</div>

		
		<div  class="value-container value_container_jq">
			<div class="value-label value_label_jq" style="position: relative;" >
	  	
		  		<div class="error-message-container error_message_container_jq" id="error_message_old_password_invalid">
		  			<div class="error-message-inner-container"  style="width:500px;" >
			  			<span class="error-message-text" >Old Password is Invalid
				  			<span class="error-message-close-x error_message_close_x_jq">X</span></span>
				  	</div>
			  	</div>
		
		  		<div class="error-message-container error_message_container_jq" id="error_message_password_confirm_not_match">
		  			<div class="error-message-inner-container" style="width:500px;" >
			  			<span class="error-message-text" >The new password and confirm password do not match
				  			<span class="error-message-close-x error_message_close_x_jq">X</span></span>
				  	</div>
			  	</div>			
			  	Password: 
			</div>
			
			<div  class="current-value-container current_value_container_jq">
				<a href="javascript:" class="edit_value_jq" >Change Password</a> 
			</div>
			<div  class="edit-value-container edit_value_container_jq">
			  <div >
				<input type="password" value="" placeholder="Old Password" title="Old Password"  
						class="edit-value-input-field  " id="password-change-old-password-field"  maxlength="40">
			  </div>
			  <div style="margin-top: 5px;">
				<input type="password" value="" placeholder="New Password" title="New Password"  
						class="edit-value-input-field  " id="password-change-field"  maxlength="40">
			  </div>
			  <div style="margin-top: 5px; margin-bottom: 6px;">
				<input type="password" value="" placeholder="Confirm New Password" title="Confirm New Password"  
						class="edit-value-input-field  " id="password-confirm-field"  maxlength="40"><br>
			  </div>
			  <div >
				<input type="button" value="Submit" id="submit-password-change-button"> 
				<input type="button" value="Cancel" class="cancel_button_jq">
			  </div>
			</div>			
		</div>

	</div>

  </div>
  
<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>
