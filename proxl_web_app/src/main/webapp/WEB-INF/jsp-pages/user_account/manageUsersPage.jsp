<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %><%-- Always put this directive at the very top of the page --%>
<%@page import="org.yeastrc.xlink.www.constants.AuthAccessLevelConstants"%>
<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

<%--  user_account/manageUsersPage.jsp --%>


 <c:set var="pageTitle">Manage Users</c:set>

 <c:set var="pageBodyClass" >manage-users-page</c:set>

 <c:set var="headerAdditions">

 </c:set>


<%@ include file="/WEB-INF/jsp-includes/header_main.jsp" %>


  <div class="overall-enclosing-block">


	<%--  This is set in the filter so this can/should go on every page --%>
	<input type="hidden" id="logged_in_user_id" value="${ loggedInUserId }" >
	
	
	<div class="top-level-label">Manage Users</div>
	
	<div class="top-level-label-bottom-border" ></div>

	<div id="invite_user_block" >

		<div  id="invite_user_collapsed" >

			<div style="float: left; padding-left: 3px; padding-right: 10px;">
			  <a href="javascript:" class="invite_user_expand_link_jq">
				<img src="images/icon-add-user.png">
			  </a>
			</div>
			<div style="padding-top: 2px; ">
			  <a href="javascript:" class="invite-user-text-link invite_user_expand_link_jq">
				Invite User
			  </a>
			</div>

		</div>		
	
	
		<div  id="invite_user_expanded" style="display: none;"  > <%-- style="display: none;"  --%>
	
		  <div style="position: relative; width: 500px;" >
		  		
	  		<div class="error-message-container error_message_container_jq" id="error_message_field_empty">
	  			<div class="error-message-inner-container" >
		  			<span class="error-message-text" >A value is required
			  			<span class="error-message-close-x error_message_close_x_jq">X</span></span>
			  	</div>
		  	</div>
		  	
	  		<div class="error-message-container error_message_container_jq" id="error_message_email_already_exists">
	  			<div class="error-message-inner-container" >
		  			<span class="error-message-text" >A user with that email already exists.
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
	
			<div style="float: left; padding-left: 3px; padding-right: 10px;">
		  	  	<a href="javascript:"  class="invite_user_cancel_button_jq">
					<img src="images/icon-circle-x.png" title="Cancel User Invite">
			  	</a>
		  	</div>

			<div style="padding-top: 0px; ">
				<input placeholder="Email Address" id="invite_user_email"  title="Email Address" >
	
				<select id="invite_person_access_level_entry_field">
							<option value="<%= AuthAccessLevelConstants.ACCESS_LEVEL_ADMIN %>" >Administrator</option>
							<option value="<%= AuthAccessLevelConstants.ACCESS_LEVEL_CREATE_NEW_PROJECT_AKA_USER %>" >User</option>
				</select>
	
				<input type="button" value="Invite User" id="invite_user_button">
				<input type="button" value="Cancel" class="invite_user_cancel_button_jq">
			</div>
									   	
		</div>
	
		<div class="top-level-label-bottom-border" style="width: 100%" ></div>
	</div>
   

	<div id="create_user_block" >

		<div  id="create_user_collapsed" >

			<div style="float: left; padding-left: 3px; padding-right: 10px;">
			  <a href="javascript:" class="create_user_expand_link_jq">
				<img src="images/icon-add-user.png">
			  </a>
			</div>
			<div style="padding-top: 2px; ">
			  <a href="javascript:" class="create-user-text-link create_user_expand_link_jq">
				Create User
			  </a>
			</div>

		</div>		
	
	
		<div  id="create_user_expanded" style="display: none;"  > <%-- style="display: none;"  --%>
	
		  <div style="position: relative; width: 500px;" >
		  		
	  		<div class="error-message-container error_message_container_jq" id="error_message_field_empty">
	  			<div class="error-message-inner-container" >
		  			<span class="error-message-text" >A value is required
			  			<span class="error-message-close-x error_message_close_x_jq">X</span></span>
			  	</div>
		  	</div>
		  	
	  		<div class="error-message-container error_message_container_jq" id="error_message_email_already_exists">
	  			<div class="error-message-inner-container" >
		  			<span class="error-message-text" >A user with that email already exists.
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
		
	  	<div  style="position: relative;" class="page-label">
	  		<div class="error-message-container error_message_container_jq" id="error_message_recaptcha_required">
	  			<div class="error-message-inner-container" >
		  			<span class="error-message-text" >Recaptcha must be completed
			  			<span class="error-message-close-x error_message_close_x_jq">X</span></span>
			  	</div>
		  	</div>
	  		<div class="error-message-container error_message_container_jq" id="error_message_all_fields_required">
	  			<div class="error-message-inner-container" >
		  			<span class="error-message-text" >All fields are required
			  			<span class="error-message-close-x error_message_close_x_jq">X</span></span>
			  	</div>
		  	</div>
	  		<div class="error-message-container error_message_container_jq" id="error_message_password_confirm_password_not_match">
	  			<div class="error-message-inner-container" >
		  			<span class="error-message-text" >Password and Confirm Password must match
			  			<span class="error-message-close-x error_message_close_x_jq">X</span></span>
			  	</div>
		  	</div>
	
	  		<div class="error-message-container error_message_container_jq" id="error_message_username_taken">
	  			<div class="error-message-inner-container" >
		  			<span class="error-message-text" >Username already taken
			  			<span class="error-message-close-x error_message_close_x_jq">X</span></span>
		  		</div>
		  	</div>
	  		<div class="error-message-container error_message_container_jq" id="error_message_email_taken">
	  			<div class="error-message-inner-container" >
		  			<span class="error-message-text" >Email address already taken
			  			<span class="error-message-close-x error_message_close_x_jq">X</span></span>
		  		</div>
		  	</div>
	  		<div class="error-message-container error_message_container_jq" id="error_message_username_email_taken">
	  			<div class="error-message-inner-container" >
		  			<span class="error-message-text" >Username already taken.  Email address already taken.
			  			<span class="error-message-close-x error_message_close_x_jq">X</span></span>
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
		  			<span class="error-message-text" >System Error
			  			<span class="error-message-close-x error_message_close_x_jq">X</span></span>
			  	</div>
		  	</div>
		  	
	<%-- 	  	
		  	UNUSED  
	--%>	  		  	
		  	
	  		<div class="success-message-container error_message_container_jq" id="success_message_system_success">
	  			<div class="success-message-inner-container" >
	  				<div class="success-message-close-x error_message_close_x_jq">X</div>
		  			<div class="success-message-text" >Account Created</div>
			  	</div>
		  	</div>
		  		  
	  	</div>
	  	
	  	<div class="page-text">
			Fill out the form below to create an account.
		</div>
	  	
	  	
		<form action="javascript:createAccountFormSubmit()" >
	
			<select id="create_person_access_level_entry_field">
						<option value="<%= AuthAccessLevelConstants.ACCESS_LEVEL_ADMIN %>" >Administrator</option>
						<option value="<%= AuthAccessLevelConstants.ACCESS_LEVEL_CREATE_NEW_PROJECT_AKA_USER %>" >User</option>
			</select><br>
				
			<input type="text" id="firstName" placeholder="First name" title="First name" class="input-field input_field_jq" maxlength="40"/><br>
			<input type="text" id="lastName" placeholder="Last name" title="Last name" class="input-field input_field_jq" maxlength="60" /><br>
			<input type="text" id="organization" placeholder="Organization" title="Organization" class="input-field input_field_jq" maxlength="2000" /><br>
			
			<input type="text" id="email" placeholder="Email address" title="Email address" class="input-field input_field_jq" maxlength="255" /><br>
	
			<input type="text" id="username" placeholder="Username" title="Username" class="input-field input_field_jq" maxlength="40" /><br>
	
			<input type="password" id="password" placeholder="Password" title="Password" class="input-field input_field_jq" maxlength="40" /><br>
			<input type="password" id="passwordConfirm" placeholder="Confirm Password" title="Confirm Password" class="input-field input_field_jq" maxlength="40" /><br>
			
			
			<c:if test="${ configSystemValues.googleRecaptchaConfigured }">
	
			  <div style="text-align: center;" id="proxl_google_recaptcha_container_div"> <%--  div "id" is used in JS code --%>
			   <div class="page-text">
				 <div class="g-recaptcha"  
				 	data-sitekey="<c:out value="${ configSystemValues.googleRecaptchaSiteCode }"></c:out>"></div>
			   </div>
			  </div>
	
			</c:if>
			
			
		 	<INPUT TYPE="submit" class="submit-button" VALUE="Create Account" id="create_account_button">
		 	<input type="button" value="Cancel" class="create_user_cancel_button_jq">
		 	
		</form>

					   	
		</div>
	
		<div class="top-level-label-bottom-border" style="width: 100%" ></div>
	</div>
      
   
	<div id="no_users" style="display: none;" >
	
		<%--  Duplicate from above and add "visibility: hidden" to shift the text right to match the text for invite user --%>
		
			<div style="visibility: hidden;  float: left; padding-left: 3px; padding-right: 10px;">
			  <a href="javascript:" class="invite_user_expand_link_jq">
				<img src="images/icon-add-user.png">
			  </a>
			</div>

	
		No Users other than current user
	</div>
	
					
					<!--  Modal dialog for confirming revoking an invite to the project -->
			
						<!--  Div behind modal dialog div -->
			
					<div class="modal-dialog-overlay-background   revoke_invite_to_project_overlay_show_hide_parts_jq revoke_invite_to_project_overlay_cancel_parts_jq  overlay_show_hide_parts_jq" 
						id="revoke_invite_to_project_overlay_background" ></div>
					
							<!--  Inline div for positioning modal dialog on page -->
					<div class="revoke-invite-to-project-overlay-containing-outermost-div " >

					  <div class="revoke-invite-to-project-overlay-containing-outer-div " >
					
			
							<!--  Div overlay for confirming removing a user from the project -->
						<div class="modal-dialog-overlay-container revoke-invite-to-project-overlay-container   revoke_invite_to_project_overlay_show_hide_parts_jq  overlay_show_hide_parts_jq" 
							 id="revoke_invite_to_project_overlay_container" >

							<div class="top-level-label" style="margin-left: 0px;">Revoke invite to project</div>
			
							<div class="top-level-label-bottom-border" ></div>
							
							<div >
							
								<div >Are you sure you want to revoke invite to <span style="font-weight: bold;" id="revoke_invite_to_project_overlay_email"></span>?</div>
								
								<div style="margin-top: 10px">
									<input type="button" value="Yes" id="revoke_invite_to_project_confirm_button" >
									<input type="button" value="Cancel" class="revoke_invite_to_project_overlay_cancel_parts_jq" >
								</div>
									
							</div>
							
						</div>
					
					  </div>
					</div>
					
						
	<%--  The table the invited and current users will be put in by the Javascript --%>
	
	 <table border="0" padding="0" margin="0" id="invited_people_current_users" width="100%">
		
	 
	 </table>

					
	<%-- For the javascript to read --%>
	<input type="hidden" id="access_level_id_administrator" value="<%= AuthAccessLevelConstants.ACCESS_LEVEL_ADMIN %>">
	
	<%--  For 'project level' access, specify the empty string and null will be passed to the server and NULL will be put in the database --%>
	<input type="hidden" id="access_level_id_user" value="<%=AuthAccessLevelConstants.ACCESS_LEVEL_CREATE_NEW_PROJECT_AKA_USER%>">

	<input type="hidden" id="access-level-id-project-owner" value="<%= AuthAccessLevelConstants.ACCESS_LEVEL_PROJECT_OWNER %>">
	<input type="hidden" id="access-level-id-project-researcher" value="<%= AuthAccessLevelConstants.ACCESS_LEVEL_ASSISTANT_PROJECT_OWNER_AKA_RESEARCHER %>">
					



	<%--  Invited People Template --%>

	<%-- This div is just a container and will not be placed into the final output --%>
	<table id="invited_person_entry_template" style="display: none;" >


	  <tr class="invited_person_entry_root_div_jq update_remove_access_div_jq" inviteId="{{inviteId}}" >
	   <td nowrap  style="padding-right: 5px;">
		<input type="image" src="images/icon-circle-x.png" class="invited_person_entry_access_level_remove_button_jq"
			title="Cancel User Invite" />
	   </td>
	   <td nowrap>
	   	<span class="invited_person_entry_email_address_jq" >{{invitedUserEmail}}</span>
	   </td>
	   
	   <%-- 
	   <td></td>  <!-- Empty to match current users where change user icon is -->
	   --%>
	   
		 <td nowrap style="padding-left: 5px; padding-right: 5px;">
		 	<!-- One of these spans will be shown  -->
			<span class="access_level_administrator_jq" style="display: none;">
				<input type="image" src="images/icon-arrow-down.png" 
					title="Decrease User Access Level"
					class="invited_person_entry_access_level_update_button_jq" inviteId="{{inviteId}}" style="display: none;" />
			</span>		
			<span class="access_level_user_jq" style="display: none;">
				<input type="image" src="images/icon-arrow-up.png" 
					title="Increase User Access Level"
					class="invited_person_entry_access_level_update_button_jq" inviteId="{{inviteId}}" style="display: none;" />
			</span>
			
		 </td>

		 <td nowrap>
		 	<!-- One of these spans will be shown  -->
			<span class="role-of-user access_level_administrator_jq" style="display: none;">Administrator</span>		
			<span class="role-of-user access_level_user_jq" style="display: none;">User</span>

			<%--  NOT USED 
		 	<!-- Access levels for Project level invites  -->
			<span class="access_level_owner_jq" style="display: none;">Owner</span>		
			<span class="access_level_researcher_jq" style="display: none;">Researcher</span>
			--%>

		 </td>
		 

	   <td nowrap class="invited-person-invite-date-block" >
	   	Invited 
	   	<span class="invited_to_project_text_jq" style="display: none;">to <a href="viewProject.do?project_id={{projectId}}">project</a></span> 
	   	<span class="invited_to_project_text__no_project_id_jq" style="display: none;">to project</span> 
	   	on {{inviteDate}}
	   </td> 
	   <td width="100%"></td>
	  </tr>
		
		<tr>
			<td colspan="10" >
				<div class="top-level-label-bottom-border" ></div>
			</td>
		</tr>
	  
	</table>  <%-- END Invited People Template --%>		
		  

	<%--  User Template --%>

	<%-- This div is just a container and will not be placed into the final output --%>
	<table id="user_entry_template" style="display: none;" >

		<tr class="current_user_entry_root_div_jq" userId="{{ authUserId }}" >
		
		 <td nowrap  style="padding-right: 5px;">
		 		 	<!-- One of these inputs will be shown  -->
		 	<input type="image" src="images/icon-circle-x.png" class="user_disable_button_jq"  
		 		title="Disable User" userId="{{ authUserId }}"  style="display: none;" />
		 	<input type="image" src="images/icon-circle-plus.png" class="user_enable_button_jq"  
		 		title="Enable User" userId="{{ authUserId }}"  style="display: none;"/>
		 </td>
		 <td nowrap>
		 	<%--  Add class 'name-of-user-disabled-user' if user is disabled in Javascript --%>
		 	<span class="name-of-user  name_of_user_jq" >{{ firstName }} {{ lastName }}</span>
		 </td>
		 
		 <td nowrap style="padding-left: 5px; padding-right: 5px;">
		 	<!-- One of these spans will be shown  -->
			<span class="access_level_administrator_jq" style="display: none;">
				<input type="image" src="images/icon-arrow-down.png" 
					title="Decrease User Access Level"
					class="user_entry_access_level_update_button_jq" userId="{{ authUserId }}"  />
			</span>		
			<span class="access_level_user_jq" style="display: none;">
				<input type="image" src="images/icon-arrow-up.png" 
					title="Increase User Access Level"
					class="user_entry_access_level_update_button_jq" userId="{{ authUserId }}"  />
			</span>
			<span class="user_disabled_jq" style="display: none; visibility: hidden;">
				<input type="image" src="images/icon-arrow-up.png" 
					class="" userId="{{ authUserId }}"  />
			</span>			
		 </td>

		 <td nowrap>
		 	<!-- One of these spans will be shown  -->
			<span class="role-of-user access_level_administrator_jq" style="display: none;">Administrator</span>		
			<span class="role-of-user access_level_user_jq" style="display: none;">User</span>
			<span class="role-of-user user_disabled_jq" style="display: none;">Disabled For This App</span>
			<span class="role-of-user user_disabled_global_jq" style="display: none;">Disabled For All Apps</span>
		 </td>
		 
		 <td></td>
		 <td width="100%"></td>
		
		</tr>
		
		<tr>
			<td colspan="10" >
				<div class="top-level-label-bottom-border" ></div>
			</td>
		</tr>
			<%--  END User Template --%>			
	</table>
				
</div>
			

	<script type="text/javascript" src="static/js_generated_bundles/user_pages/manageUsersPage-bundle.js?x=${ cacheBustValue }"></script>

  
<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>

