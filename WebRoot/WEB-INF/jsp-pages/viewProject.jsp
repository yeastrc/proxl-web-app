<%@page import="org.yeastrc.xlink.www.constants.QCPlotConstants"%>
<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %>
<%@page import="org.yeastrc.xlink.www.constants.StrutsActionPathsConstants"%>
<%@page import="org.yeastrc.xlink.www.constants.WebConstants"%>
<%@page import="org.yeastrc.xlink.www.constants.AuthAccessLevelConstants"%>
<%@page import="org.yeastrc.xlink.www.constants.QCPlotConstants"%>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>


<%--  viewProject.jsp   


 --%>


 <c:set var="pageTitle">Project Page - <c:out value="${ project.title }" ></c:out></c:set>

 <c:set var="pageBodyClass" >project-page</c:set>

 <c:set var="helpURLExtensionForSpecificPage" >en/latest/using/project.html</c:set>

 <c:set var="headerAdditions">
 

		<link REL="stylesheet" TYPE="text/css" HREF="${ contextPath }/css/jquery-ui-1.10.2-Themes/base/jquery.ui.all.css">


		<script type="text/javascript" src="${ contextPath }/js/libs/jquery-ui-1.10.4.min.js"></script>
		<script type="text/javascript" src="${ contextPath }/js/libs/base64.js"></script> 
		<script type="text/javascript" src="${ contextPath }/js/libs/jquery.qtip.min.js"></script>
		

		<script type="text/javascript" src="${ contextPath }/js/handleServicesAJAXErrors.js"></script> 
		
		<script type="text/javascript" src="${ contextPath }/js/viewProjectPage.js"></script> 
		
		<link type="text/css" rel="stylesheet" href="${ contextPath }/css/jquery.qtip.min.css" />
		
		
		<%--  Project Admin Javascript is added at the bottom of the page --%>

</c:set>



<%@ include file="/WEB-INF/jsp-includes/header_main.jsp" %>


<div class="overall-enclosing-block">

		<%--  This is set in the filter so this can/should go on every page --%>
		<input type="hidden" id="logged_in_user_id" value="${ loggedInUserId }" >
	
		
		<div id="ajax_error_no_session_msg" style="display: none;" >
		
			<h1 style="color: red;">Sign in Session Expired</h1>
			
			Your sign in session has expired.<br><br>
			
			<form action="user_loginPage.do" id="ajax_error_no_session_form" >
			
				<input id="ajax_error_no_session_saved_url" name="requestedURL" type="hidden">
			</form>

			<a id="ajax_error_no_session_login_link" href="javascript:" onclick="$('#ajax_error_no_session_form').submit()" >sign in</a><br><br>
		
			<br><br>
			<br><br>

		
		</div>
		
	
		<div id="ajax_error_not_authorized_msg" style="display: none;" >
		
			<h1 style="color: red;">Access Not Authorized</h1>
			
			Access to this data is not authorized.<br><br>
			
			<br><br>
			<br><br>
		
		</div>		
	
	
		<div>

		
			<br>
			<br>
			
			<div class="top-level-container collapsable_container_jq" >
			
				<div  class="collapsable-link-container top-level-collapsable-link-container collapsable_link_container_jq" style="">
					<a href="javascript:" class="top-level-collapsable-link collapsable_collapse_link_jq"
						><img  src="${ contextPath }/images/icon-collapse.png"></a>
					<a href="javascript:" class="top-level-collapsable-link collapsable_expand_link_jq" style="display: none;"
						><img  src="${ contextPath }/images/icon-expand.png"></a>
				
				</div>
	
			
				<div class="top-level-label">
				  
				  Project Information

				  <c:choose>
				   <c:when test="${authAccessLevel.projectOwnerAllowed or authAccessLevel.projectOwnerIfProjectNotLockedAllowed}" >
				  
				  
				    <c:choose>
				     <c:when test="${ project.projectLocked }">
				    	<c:set var="project_locked_link_display_control" ></c:set>
				    	<c:set var="project_unlocked_link_display_control" >display: none;</c:set>
				     </c:when>
				     <c:otherwise>
				    	<c:set var="project_locked_link_display_control" >display: none;</c:set>
				    	<c:set var="project_unlocked_link_display_control" ></c:set>
				     </c:otherwise>
				   
				    </c:choose>
				  
				  	<a href="javascript:unlockProject(this)" id="project_locked_link" class="" style="${ project_locked_link_display_control }" 
						><img  src="${ contextPath }/images/icon-locked.png"></a>

				  	<a href="javascript:lockProject(this)" id="project_unlocked_link" class="" style="${ project_unlocked_link_display_control }" 
						><img  src="${ contextPath }/images/icon-unlocked.png"></a>


					<script type="text/javascript">
					
						$("#project_locked_link").qtip({ 
		 			        content: {
		 			            text: "Project is locked, click to unlock. <br>While locked, no data may be changed, added to, or deleted from the project."
		 			        }
		 			    });
						
						$("#project_unlocked_link").qtip({ 
		 			        content: {
		 			            text: "Project is unlocked, click to lock. <br>While locked, no data may be changed, added to, or deleted from the project."
		 			        }
		 			    });
					
					</script>
					
			
					<div id="project_just_locked_message" style="display: none; color: green;" >
						Project Locked.  Reloading page to reflect changes
					</div>			
				
					<div id="project_just_unlocked_message" style="display: none; color: green;" >
						Project Unlocked.  Reloading page to reflect changes
					</div>
				
				   </c:when>
				   <c:when test="${authAccessLevel.assistantProjectOwnerIfProjectNotLockedAllowed }">
				   
				   	<%-- Researcher so display lock symbol if project is locked --%>
				   
				    <c:choose>
				     <c:when test="${ project.projectLocked }">
					  	<img id="project_locked_image" src="${ contextPath }/images/icon-locked.png">
					  	

						<script type="text/javascript">
						
							$("#project_locked_image").qtip({ 
			 			        content: {
			 			            text: "Project is locked. <br>While locked, no data may be changed, added to, or deleted from the project.<br>Contact the Project Owner regarding the locking and unlocking of the project."
			 			        }
			 			    });		
						</script>
												
				     </c:when>
				     <c:otherwise>
				     </c:otherwise>
				    </c:choose>
				   
				   
				   </c:when>
				  </c:choose>


					
				</div>
				
				<div class="top-level-label-bottom-border" ></div>
									
				<div class="project-info-block  collapsable_jq">
				
				  <div class="project-info-single-block" >
													
					<div class="second-level-label project-info-label">Title:</div>
					<div id="title_container_div"  class="second-level-text project-info-text" >

					  <c:choose>
						<c:when test="${ authAccessLevel.publicAccessCodeReadAccessLevel }">
						  <c:set var="title_style_addition" value="font-weight: bold;"></c:set>
						</c:when>
						<c:otherwise>
						  <c:set var="title_style_addition" value=""></c:set>
						</c:otherwise>
					  </c:choose>

						<span id="title_span" style="<c:out value="${title_style_addition}" ></c:out>" ><c:out value="${project.title}" ></c:out></span>
						
						<c:if test="${authAccessLevel.assistantProjectOwnerAllowed}" >
							<input class="tool_tip_attached_jq" data-tooltip="Edit project title" id="maint_title_init_button" type="image" src="${ contextPath }/images/icon-edit-small.png"  value="Update">
						</c:if>
					</div>
					<div  id="maint_title_div"  class="second-level-text project-info-text"  style="display: none; position: relative;">

				  		<div class="error-message-container error_message_container_jq" id="error_message_project_title_required">
				  		
				  			<div class="error-message-inner-container" style="width: 300px;">
				  				<div class="error-message-close-x error_message_close_x_jq">X</div>
					  			<div class="error-message-text" >Project Title cannot be empty</div>
				  			</div>
					  	</div>
					
					
						<input id="maint_title_field" type="text" size="100" maxlength="255">
						<input id="maint_title_save_button" class="submit-button" type="button" value="Save">
						<input id="maint_title_cancel_button" class="submit-button" type="button" value="Cancel">
						<input id="maint_title_reset_button" class="submit-button" type="button" value="Reset">
					</div>
				  </div>
				  
				 <c:if test="${ not empty project.abstractText or authAccessLevel.assistantProjectOwnerAllowed }" >	
				  
				  <div class="project-info-single-block" >
					<div class="second-level-label project-info-label">Abstract:</div>
					
					<div id="abstract_container_div"  class="second-level-text project-info-text" >
						<span id="abstract_span" ><c:out value="${project.abstractText}" ></c:out></span>
						
						<c:if test="${authAccessLevel.assistantProjectOwnerAllowed}" >
							<input class="tool_tip_attached_jq" data-tooltip="Edit project abstract" id="maint_abstract_init_button" type="image" src="${ contextPath }/images/icon-edit-small.png"  value="Update">
						</c:if>
					</div>
					<div  id="maint_abstract_div" class="second-level-text project-info-text" style="display: none; position: relative;">

				  		<div class="error-message-container error_message_container_jq" id="error_message_project_abstract_required">
				  		
				  			<div class="error-message-inner-container" style="width: 300px;">
				  				<div class="error-message-close-x error_message_close_x_jq">X</div>
					  			<div class="error-message-text" >Project Abstract cannot be empty</div>
				  			</div>
					  	</div>
					
					
					  <div >
						<textarea id="maint_abstract_field" rows="10" cols="100" maxlength="5000"></textarea>
					  </div>
					  <div >
						<input id="maint_abstract_save_button" class="submit-button" type="button" value="Save">
						<input id="maint_abstract_cancel_button" class="submit-button" type="button" value="Cancel">
						<input id="maint_abstract_reset_button" class="submit-button" type="button"  value="Reset">
					  </div>
					</div>
					
					<div class="project-info-end-block-clear-float"></div>
				  </div>
				  
				 </c:if>
				  
				  
				 <c:if test="${ not empty notes or authAccessLevel.assistantProjectOwnerAllowed }" >

				  <div class="project-info-single-block" >
					<div class="second-level-label project-info-label">Notes:</div>
					
					<%-- 
							 !!!!!!  IMPORTANT  !!!!!!!!!! 
					
					
					
							The content of  <div class="note_root_container_div_jq"
							
							under  <div id="notes_template_div"  must be kept in sync with
							 
							<div class="note_root_container_div_jq" under <div id="notes_list_container_div" 
					
							Or move initial populate to either calling a web service or put JSON on the page
					
					--%>
		
				  <c:if test="${authAccessLevel.assistantProjectOwnerAllowed}" >
		
					<!--  Modal dialog for confirming deleting a note -->
			
						<!--  Div behind modal dialog div -->
			
					<div class="modal-dialog-overlay-background   delete_note_overlay_show_hide_parts_jq delete_note_overlay_cancel_parts_jq  overlay_show_hide_parts_jq" 
						id="delete_note_overlay_background" ></div>
					
							<!--  Inline div for positioning modal dialog on page -->
					<div class="delete-note-overlay-containing-outermost-div " id="delete_note_overlay_containing_outermost_div_inline_div"  >
		
					  <div class="delete-note-overlay-containing-outer-div " >
					
			
							<!--  Div overlay for confirming removing a note -->
						<div class="modal-dialog-overlay-container delete-note-overlay-container   delete_note_overlay_show_hide_parts_jq  overlay_show_hide_parts_jq" 
							 id="delete_note_overlay_container" >
		
							<div class="top-level-label" style="margin-left: 0px;">Delete Note</div>
			
							<div class="top-level-label-bottom-border" ></div>
							
							<div >
							
								<div >Are you sure you want to remove this note?</div>
								
								<div style="margin-top: 10px">
									<input type="button" value="Yes" id="delete_note_confirm_button" >
									<input type="button" value="Cancel" class="delete_note_overlay_cancel_parts_jq" >
								</div>
									
							</div>
							
						</div>
					
					  </div>
					</div>
					
					
					<!-- END:   Modal dialog for confirming deleting a note -->
					
										
					<!-- template for building new notes entries -->
					<div id="notes_template_div" style="display:none;">
				
							<div class="note_root_container_div_jq" style="" note_id="{{noteId}}">
								<div class="note-display-div  note_display_container_div_jq" >
									<span class="notes_text_jq" >{{noteText}}</span>
									
								  <c:if test="${authAccessLevel.assistantProjectOwnerAllowed}" >
									<input class="notes_update_button_jq tool_tip_attached_jq" data-tooltip="Edit note" type="image" src="${ contextPath }/images/icon-edit-small.png"   value="Update">
									<input class="notes_remove_button_jq tool_tip_attached_jq" data-tooltip="Remove note" type="image" src="${ contextPath }/images/icon-delete-small.png"  value="Remove">
								  </c:if>
								</div>
								<div  class="note_maint_container_div_jq" style=" display: none; position: relative;">

							  		<div class="error-message-container error_message_container_jq error_message_project_note_required_jq" >
							  		
							  			<div class="error-message-inner-container" style="width: 300px;">
							  				<div class="error-message-close-x error_message_close_x_jq">X</div>
								  			<div class="error-message-text" >Project Note cannot be empty</div>
							  			</div>
								  	</div>
								
								  <div >
									<textarea class="note_maint_textarea_jq" rows="10" cols="100" maxlength="5000"></textarea>
								  </div>
								  <div >
									<input class="submit-button note_maint_save_button_jq" type="button" value="Save">
									<input class="submit-button note_maint_cancel_button_jq" type="button" value="Cancel">
									<input class="submit-button note_maint_reset_button_jq" type="button" value="Reset">
								  </div>
								</div>					
							</div>
							
					</div>
					
					</c:if>  <%--  END <c:if test="${authAccessLevel.assistantProjectOwnerAllowed}" > --%>
					
					<div id="notes_list_container_div" class="second-level-text project-info-text">
		
						<c:forEach var="note" items="${ notes }">
						
							<div class="note_root_container_div_jq" style="" note_id="${ note.id }">
								<div class="note-display-div  note_display_container_div_jq" >
									<span class="notes_text_jq" ><c:out value="${ note.noteText }" ></c:out></span>
									
								  <c:if test="${authAccessLevel.assistantProjectOwnerAllowed}" >
									<input class="tool_tip_attached_jq notes_update_button_jq" data-tooltip="Edit note" type="image" src="${ contextPath }/images/icon-edit-small.png" value="Update">
									<input class="tool_tip_attached_jq notes_remove_button_jq" data-tooltip="Remove note" type="image" src="${ contextPath }/images/icon-delete-small.png" value="Remove">
								  </c:if>
								</div>

				  				<c:if test="${authAccessLevel.assistantProjectOwnerAllowed}" >
				  								
								 <div  class="note_maint_container_div_jq" style=" display: none; position: relative;">
								
							  		<div class="error-message-container error_message_container_jq error_message_project_note_required_jq" >
							  		
							  			<div class="error-message-inner-container" style="width: 300px;">
							  				<div class="error-message-close-x error_message_close_x_jq">X</div>
								  			<div class="error-message-text" >Project Note cannot be empty</div>
							  			</div>
								  	</div>
								
								  <div >
									<textarea class="note_maint_textarea_jq" rows="10" cols="100" maxlength="5000"></textarea>
								  </div>
								  <div >
									<input class="submit-button note_maint_save_button_jq" type="button" value="Save">
									<input class="submit-button note_maint_cancel_button_jq" type="button" value="Cancel">
									<input class="submit-button note_maint_reset_button_jq" type="button" value="Reset">
								  </div>
								 
								 </div>
								  
								</c:if>  <%--  END <c:if test="${authAccessLevel.assistantProjectOwnerAllowed}" > --%>
								  					
							</div>
						
						
						</c:forEach>
		
		
					</div>
					<div class="second-level-text project-info-text">
					 
					 <c:choose>
				   	  <c:when test="${authAccessLevel.assistantProjectOwnerAllowed}" >		
						<div  id="add_note_button_container_div" >
							[<a class="tool_tip_attached_jq" data-tooltip="Add note to project" id="add_note_init_button" href="javascript:" style="font-size: 80%;text-decoration: none;">+Note</a>]
						</div>
						<div  id="add_note_div" style=" display: none; position: relative;">
						
					  		<div class="error-message-container error_message_container_jq" id="error_message_project_note_required">
					  		
					  			<div class="error-message-inner-container" style="width: 300px;">
					  				<div class="error-message-close-x error_message_close_x_jq">X</div>
						  			<div class="error-message-text" >Project Note cannot be empty</div>
					  			</div>
						  	</div>
					
							<textarea id="add_note_field" rows="10" cols="100" maxlength="5000"></textarea>
							<br>
							<input id="add_note_save_button" class="submit-button" type="button" value="Add">
							<input id="add_note_cancel_button" class="submit-button" type="button" value="Cancel">
						</div>
				   	  </c:when>
				   	  <c:otherwise>
				   	   <c:if test="${ empty notes }">
				   	    &nbsp;  <%-- If there are no notes, take up some space since the label is floated to the left --%>
				   	   </c:if>
				   	  </c:otherwise>
				   	 </c:choose>		

					</div>
					
					<div class="project-info-end-block-clear-float" ></div>

				  </div>
				  
				 </c:if>
					
				</div>
				

			</div> <%--  End Project information --%>

			
<%-- 			
			<c:if test="${authAccessLevel.projectOwnerAllowed}" >
--%>			
			<c:if test="${authAccessLevel.assistantProjectOwnerAllowed or authAccessLevel.assistantProjectOwnerIfProjectNotLockedAllowed }" >
			
												<%--  logged_in_user_id is set in the filter  --%>
			  <div id="view_searches_project_admin_div" logged_in_user_id="${ loggedInUserId }" 
			  		logged_in_user_access_level_owner_or_better="<c:if test="${authAccessLevel.projectOwnerAllowed}" >true</c:if>" >

			  <div class="top-level-container collapsable_container_jq" >
			
				<div  class="collapsable-link-container top-level-collapsable-link-container collapsable_link_container_jq" style="">
					<a href="javascript:" class="top-level-collapsable-link collapsable_collapse_link_jq" style="display: none;"
						><img  src="${ contextPath }/images/icon-collapse.png"></a>
					<a href="javascript:" class="top-level-collapsable-link collapsable_expand_link_jq" 
						><img  src="${ contextPath }/images/icon-expand.png"></a>
				
				</div>
			
				<div class="top-level-label">Researchers</div>

				<div class="top-level-label-bottom-border" ></div>
					
									
				<div class="researchers-block collapsable_jq" style="display: none;" > <%--  style="display: none;" TODO  TEMP --%>


				<%--  Only allow Invite if not locked --%>
				<c:if test="${authAccessLevel.assistantProjectOwnerAllowed }" >

				 <table border="0" width="100%">
				 
				  <tr>
				   <td colspan="10">
			
					<div id="invite_user_block" >
	
						<div  id="invite_user_collapsed" >
						 
						 <div class="invite-user-expand-icon-container" >
						  <a href="javascript:" data-tooltip="Invite new or existing user to project" class="tool_tip_attached_jq second-level-label overide-text-color-to-base-color invite_user_expand_link_jq">
							<img src="${ contextPath }/images/icon-add-user.png">
						  </a>
						 </div>
						 <div >
						  <a href="javascript:" data-tooltip="Invite new or existing user to project" class="tool_tip_attached_jq second-level-label overide-text-color-to-base-color invite_user_expand_link_jq">
							Invite User
						  </a>
						 </div>
						 
						</div>		
					
						<div  id="invite_user_expanded" style="display: none;"  > <%-- style="display: none;"  --%>
						
						 <table >
						 
						  <tr>
						   <td nowrap>
						  	<div class="researchers-icon">
						  	  <a href="javascript:" title="Close Invite User" class="invite_user_cancel_button_jq">
								<img src="${ contextPath }/images/icon-circle-x.png">
							  </a>
								
							</div>
						   </td>
						   <td colspan="15" nowrap>
							
							<div style="padding-left: 4px;" >
							
								<div style="position: relative;">Invite user to this project:</div>
								
						 		<div style="position: relative;">
						 		
							  		<div class="error-message-container error_message_container_jq" id="error_message_invite_name_or_email_required">
							  		
							  			<div class="error-message-inner-container" style="width: 380px;">
							  				<div class="error-message-close-x error_message_close_x_jq">X</div>
								  			<div class="error-message-text" >Last Name or Email must be specified</div>
							  			</div>
								  	</div>
						 		
							  		<div class="error-message-container error_message_container_jq" id="error_message_invite_name_and_email_have_values">
							  		
							  			<div class="error-message-inner-container" style="width: 420px;">
							  				<div class="error-message-close-x error_message_close_x_jq">X</div>
								  			<div class="error-message-text" >Last Name and Email cannot both be specified</div>
							  			</div>
								  	</div>
						 		
							  		<div class="error-message-container error_message_container_jq" id="error_message_invite_name_not_found">
							  		
							  			<div class="error-message-inner-container" style="width: 380px; text-align: left;">
							  				<div class="error-message-close-x error_message_close_x_jq">X</div>
								  			<div class="error-message-text" >Last Name entered cannot be found.<br>  Please choose a user from the dropdown list.</div>
							  			</div>
								  	</div>
								  	
						 		
							  		<div class="error-message-container error_message_container_jq" id="error_message_invite_name_duplicate">
							  		
							  			<div class="error-message-inner-container" style="width: 480px; text-align: left;">
							  				<div class="error-message-close-x error_message_close_x_jq">X</div>
								  			<div class="error-message-text" >Last Name entered matches more than one user.<br>  Please choose a user from the dropdown list.</div>
							  			</div>
								  	</div>
								  	
						 		
							  		<div class="error-message-container error_message_container_jq" id="error_message_invite_already_has_access">
							  		
							  			<div class="error-message-inner-container" style="width: 380px;">
							  				<div class="error-message-close-x error_message_close_x_jq">X</div>
								  			<div class="error-message-text" >User already has access to this project</div>
							  			</div>
								  	</div>
						 		
							  		<div class="error-message-container error_message_container_jq" id="error_message_invite_email_address_invalid">
							  		
							  			<div class="error-message-inner-container" style="width: 440px;">
							  				<div class="error-message-close-x error_message_close_x_jq">X</div>
								  			<div class="error-message-text" >Unable to send email, email address is invalid</div>
							  			</div>
								  	</div>
						 		
							  		<div class="error-message-container error_message_container_jq" id="error_message_invite_email_send_sytem_error">
							  		
							  			<div class="error-message-inner-container" style="width: 400px;">
							  				<div class="error-message-close-x error_message_close_x_jq">X</div>
								  			<div class="error-message-text" >Unable to send email, system error.</div>
							  			</div>
								  	</div>
						 		
							  		<div class="error-message-container error_message_container_jq" id="error_message_invite_error_adding_user_to_project">
							  		
							  			<div class="error-message-inner-container" style="width: 380px;">
							  				<div class="error-message-close-x error_message_close_x_jq">X</div>
								  			<div class="error-message-text" >Error adding user to project</div>
							  			</div>
								  	</div>
								  	
								  	<%-- Invite email successfully sent message --%>
	
							  		<div class="success-message-container error_message_container_jq" id="success_message_invite_email_sent">
							  			<div class="success-message-inner-container"  style="width: 800px;">
							  				<div class="success-message-close-x error_message_close_x_jq">X</div>
								  			<div class="success-message-text" >Email sent to <span id="invite_user_email_that_was_sent"></span> inviting them to this project</div>
									  	</div>
								  	</div>	  
	
	
						 		</div>
						 	  
						 	  </div>
						 	  
						   </td>
						  </tr>
						  <tr>
						   <td nowrap></td> <!-- Empty to match X icon in first row -->
						   <td nowrap>
						    <table border="0" margin="0" padding="0" id="invite_user_input_fields">
						     <tr>
						      <td nowrap>
						      	<input placeholder="Last Name" id="invite_user_last_name"  class="autocomplete-entry-field"  
											title="Last Name" >
						      </td>
						      <td>
						      	or
						      </td>
						      <td nowrap>
								<input placeholder="Email Address" id="invite_user_email"  class="autocomplete-entry-field" 
											title="Email Address" >
						      </td>
						     </tr>
						     <tr>
						      <td nowrap>
						      	Existing user only
						      </td>
						      <td></td>
						      <td nowrap>
						      	New or existing user
						      </td>
						    
						    
						    </table>

							<span id="invite_user_auto_complete_display" style="display: none;">
						  	  <a href="javascript:" title="Clear chosen Last Name or Email Address" id="close_invite_user_auto_complete_display">
								<img src="${ contextPath }/images/icon-delete-small.png">
							  </a>
							  <span id="invite_user_auto_complete_value"></span>
							</span>
						    
						   </td>
						   <td nowrap valign="top" style="padding-top: 5px;">
									<select id="invite_person_to_project_access_level_entry_field">
<%-- 
										<option value="<%= AuthAccessLevelConstants.ACCESS_LEVEL__PUBLIC_ACCESS_CODE_READ_ONLY__PUBLIC_PROJECT_READ_ONLY %>" >Read</option>
										<option value="<%= AuthAccessLevelConstants.ACCESS_LEVEL_WRITE %>" >Update</option>
										<option value="<%= AuthAccessLevelConstants.ACCESS_LEVEL_SEARCH_DELETE %>" >Update and Delete Searches</option>
--%>									
										<option value="<%= AuthAccessLevelConstants.ACCESS_LEVEL_ASSISTANT_PROJECT_OWNER_AKA_RESEARCHER %>" >Researcher</option>
										
										<c:if  test="${  authAccessLevel.projectOwnerAllowed }">									
											<option value="<%= AuthAccessLevelConstants.ACCESS_LEVEL_PROJECT_OWNER %>" >Owner</option>
										</c:if>
										
									</select>
	
									<input type="button" value="Invite User" id="invite_user_button">
									<input type="button" value="Cancel" class="invite_user_cancel_button_jq">						   	
						   </td>
						  </tr>
						 
						 </table>

						</div>
					
						<div class="top-level-label-bottom-border" style="width: 100%" ></div>
					</div>
				   
				   </td>
				  </tr>
				 </table>
				 
				<%--  Only allow Invite if not locked --%>
				</c:if>
								
					
					<%-- For the javascript to read --%>
					<input type="hidden" id="access-level-id-project-owner" value="<%= AuthAccessLevelConstants.ACCESS_LEVEL_PROJECT_OWNER %>">
					<input type="hidden" id="access-level-id-project-researcher" value="<%= AuthAccessLevelConstants.ACCESS_LEVEL_ASSISTANT_PROJECT_OWNER_AKA_RESEARCHER %>">
					

					
					<!--  Modal dialog for confirming revoking an invite to the project -->
			
						<!--  Div behind modal dialog div -->
			
					<div class="modal-dialog-overlay-background   revoke_invite_to_project_overlay_show_hide_parts_jq revoke_invite_to_project_overlay_cancel_parts_jq  overlay_show_hide_parts_jq" 
						id="revoke_invite_to_project_overlay_background" ></div>
					
							<!--  Inline div for positioning modal dialog on page -->
					<div class="revoke-invite-to-project-overlay-containing-outermost-div " id="revoke_invite_to_project_overlay_containing_outermost_div_inline_div" >

					  <div class="revoke-invite-to-project-overlay-containing-outer-div " style="position: relative;" >
					
			
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
					
					
					<!-- END:   Modal dialog for confirming removing a user from the project -->
										
					
					<!--  Modal dialog for confirming removing a user from the project -->
			
						<!--  Div behind modal dialog div -->
			
					<div class="modal-dialog-overlay-background   remove_user_from_project_overlay_show_hide_parts_jq remove_user_from_project_overlay_cancel_parts_jq  overlay_show_hide_parts_jq" 
						id="remove_user_from_project_overlay_background" ></div>
					
							<!--  Inline div for positioning modal dialog on page -->
					<div class="remove-user-from-project-overlay-containing-outermost-div "  id="remove_user_from_project_overlay_containing_outermost_div_inline_div" >

					  <div class="remove-user-from-project-overlay-containing-outer-div "  style="position: relative;" >
					
			
							<!--  Div overlay for confirming removing a user from the project -->
						<div class="modal-dialog-overlay-container remove-user-from-project-overlay-container   remove_user_from_project_overlay_show_hide_parts_jq  overlay_show_hide_parts_jq" 
							 id="remove_user_from_project_overlay_container" >

							<div class="top-level-label" style="margin-left: 0px;">Remove user from project</div>
			
							<div class="top-level-label-bottom-border" ></div>
							
							<div >
							
								<div >Remove the user <span style="font-weight: bold;" id="remove_user_from_project_overlay_name_of_user"></span> from the project?</div>
								
								<div style="margin-top: 10px">
									<input type="button" value="Yes" id="remove_user_from_project_confirm_button" >
									<input type="button" value="Cancel" class="remove_user_from_project_overlay_cancel_parts_jq" >
								</div>
									
							</div>
							
						</div>
					
					  </div>
					</div>
					
					
					<!-- END:   Modal dialog for confirming removing a user from the project -->
					
					
					<%--   Table of invited users and current users, loaded via ajax.
					
						The templates for invited user and current user are in this file.
							See comment "Invited People Template" down just a bit. 
							See comment "Current User Template" down just a bit. 
					
					  --%>
						
				 <table border="0" id="invited_people_current_users" width="100%">
					
				 
				 </table>
					
		
					<%-- !!!!  Handlebars templates   !!!! --%>



			<%--  Invited People Template --%>

			<%-- This table is just a container and will not be placed into the final output --%>
			<table id="invited_person_entry_template" style="display: none;" >
		
		
			  <tr class="invited_person_entry_root_div_jq update_remove_access_div_jq" inviteId="{{inviteId}}" >
			   <td nowrap  style="padding-right: 5px;">
				<input type="image" src="${ contextPath }/images/icon-circle-x.png" data-tooltip="Revoke invitation" class="tool_tip_attached_jq invited_person_entry_access_level_remove_button_jq"/>
			   </td>
			   <td nowrap>
			   	 <span class="invited_person_entry_email_address_jq" >{{invitedUserEmail}}</span>
			   </td>
			
			   
				 <td nowrap style="padding-left: 5px; padding-right: 5px;">
				 	<!-- One of these spans will be shown  -->
					<span class="access_level_owner_jq" style="display: none;">
						<c:if test="${authAccessLevel.projectOwnerAllowed}" >
							<input type="image" src="${ contextPath }/images/icon-arrow-down.png" 
								class="tool_tip_attached_jq invited_person_entry_access_level_update_button_jq" data-tooltip="Demote to researcher" inviteId="{{inviteId}}"  />
						</c:if>
					</span>		
					<span class="access_level_researcher_jq" style="display: none;">
						<input type="image" src="${ contextPath }/images/icon-arrow-up.png" 
							class="tool_tip_attached_jq invited_person_entry_access_level_update_button_jq" data-tooltip="Promote to owner" inviteId="{{inviteId}}"  />
					</span>
				 </td>

				 <td nowrap>
				 	<!-- One of these spans will be shown  -->
					<span class="access_level_owner_jq" style="display: none;">Owner</span>		
					<span class="access_level_researcher_jq" style="display: none;">Researcher</span>
				 </td>
				 
			   <td nowrap class="invited-person-invite-date-block" >
			   	Invited on {{inviteDate}}
			   </td> 
			   <td width="100%"></td>
			  </tr>
				
				<tr>
					<td colspan="10" >
						<div class="top-level-label-bottom-border" ></div>
					</td>
				</tr>
			  
			</table>  <%-- END Invited People Template --%>		
				  
				  

				<%--  template for when no users (admin users still have access) --%>

			<div id="current_user_entry_no_data_template_div" style="display: none;" >
				
				<div >
					No users have access to this project specifically.
				</div>
				
				<div class="top-level-label-bottom-border" ></div>
				
			</div>



			<%--  Current User Template --%>

			<%-- This div is just a container and will not be placed into the final output --%>
			<table id="current_user_entry_template" style="display: none;" >

				<tr class="current_user_entry_root_div_jq" userId="{{userId}}" >
				
				<c:if test="${authAccessLevel.assistantProjectOwnerAllowed }" >
				 <td nowrap  style="padding-right: 5px;">
				 	<input type="image" src="${ contextPath }/images/icon-circle-x.png" data-tooltip="Remove from project" class="tool_tip_attached_jq current_user_entry_access_level_remove_button_jq"  
				 		title="Remove from project" userId="{{userId}}" />
				 </td>
				</c:if>
				
				 <td nowrap>
				 	<span class="current_user_entry_name_jq">{{xLinkUserDTO.firstName}} {{xLinkUserDTO.lastName}}</span>
				 </td>
				 
				 <td nowrap style="padding-left: 5px; padding-right: 5px;">
				 	<!-- One of these spans will be shown  -->
					<span class="access_level_owner_jq" style="display: none;">
						<c:if test="${authAccessLevel.projectOwnerAllowed}" >
							<input type="image" src="${ contextPath }/images/icon-arrow-down.png" 
								class="tool_tip_attached_jq current_user_entry_access_level_update_button_jq" data-tooltip="Demote to researcher" userId="{{userId}}"  />
						</c:if>
					</span>		
					<span class="access_level_researcher_jq" style="display: none;">
						<input type="image" src="${ contextPath }/images/icon-arrow-up.png" 
							class="tool_tip_attached_jq current_user_entry_access_level_update_button_jq" data-tooltip="Promote to owner" userId="{{userId}}"  />
					</span>
				 </td>

				 <td nowrap>
				 	<!-- One of these spans will be shown  -->
					<span class="access_level_owner_jq" style="display: none;">Owner</span>		
					<span class="access_level_researcher_jq" style="display: none;">Researcher</span>
				 </td>
				 <td width="100%"></td>
				
				</tr>
				
				<tr>
					<td colspan="10" >
						<div class="top-level-label-bottom-border" ></div>
					</td>
				</tr>
					<%--  END Current User Template --%>			
			</table>
				
				
				</div>
				
				</div>
							
			  </div>  <!--  End Researchers Admin -->


			<%--  Public access Management --%>			
		
			<div class="top-level-container collapsable_container_jq" >
			
				<div  class="collapsable-link-container top-level-collapsable-link-container collapsable_link_container_jq" style="">
					<a href="javascript:" id="public_access_collapse_link_jq" class="top-level-collapsable-link " style="display: none;" <%-- collapsable_collapse_link_jq --%>
						><img  src="${ contextPath }/images/icon-collapse.png"></a>
					<a href="javascript:" id="public_access_expand_link_jq" class="top-level-collapsable-link " <%-- collapsable_expand_link_jq --%>
						><img  src="${ contextPath }/images/icon-expand.png"></a>
				
				</div>
			
				<%--  Create page variables with the display control to go in the "style"
						for the spans with text "Enabled" and "Disabled".       
				--%>
				<c:choose>
					<c:when test="${ projectPublicAccessData.anyPublicAccessEnabled }" >
						<c:set var="show_when_public_access_code_disabled_div_style_display_control" value="display:none;"></c:set>
						<c:set var="show_when_public_access_code_enabled_div_style_display_control" value=""></c:set>
					</c:when>
					<c:otherwise>
						<c:set var="show_when_public_access_code_disabled_div_style_display_control" value=""></c:set>
						<c:set var="show_when_public_access_code_enabled_div_style_display_control" value="display:none;"></c:set>
					</c:otherwise>
				</c:choose>			
				
				<%-- Create page variable indicating that allowed to enable buttons since meet access control requirements --%>
				<c:choose>
					<c:when test="${ authAccessLevel.projectOwnerAllowed }" >
						<c:set var="allowed_to_enable" value="true"></c:set>
					</c:when>
					<c:otherwise>
						<c:set var="allowed_to_enable" value="false"></c:set>
					</c:otherwise>
				</c:choose>						
				
				
																
				<div class="top-level-label">
					Public Access 
						(<span class="show_when_public_access_or_public_access_code_enabled_jq" style="${show_when_public_access_code_enabled_div_style_display_control}"
							 >Enabled</span
							><span class="show_when_public_access_or_public_access_code_disabled_jq" style="${show_when_public_access_code_disabled_div_style_display_control}" >Disabled</span>)</div>

				<div class="top-level-label-bottom-border" ></div>
									
				<div class="public-access-block collapsable_jq" style="display: none;">
				
					
					<div class="second-level-label ">Public access is currently 
						<span class=" show_when_public_access_or_public_access_code_enabled_jq " style="${show_when_public_access_code_enabled_div_style_display_control}" 
								>enabled</span
							><span class=" show_when_public_access_or_public_access_code_disabled_jq " style="${show_when_public_access_code_disabled_div_style_display_control}" 
								>disabled</span>.
					</div>

					<br>
											
					<div >
						Enable public access to allow users who do not have Proxl accounts to view project data.
					</div>

						<br>
						
					<div class=" show_when_public_access_or_public_access_code_enabled_jq ">
					
					  <c:if test="${ authAccessLevel.projectOwnerAllowed or authAccessLevel.projectOwnerIfProjectNotLockedAllowed }" > 
						Require public access code:
						 
						<label ><input type="radio" value="yes" checked
							name="require_public_access_code_radio_button"
							id="require_public_access_code_yes_radio_button"
							class=" first_enable_when_public_access_not_locked_jq "
							allowed_to_enable="${ allowed_to_enable }"
							<c:if test="${not authAccessLevel.projectOwnerAllowed}" > disabled </c:if>
							>Yes</label>
							
						<label ><input type="radio" value="no" 
							name="require_public_access_code_radio_button"
							id="require_public_access_code_no_radio_button"
							class=" first_enable_when_public_access_not_locked_jq "
							allowed_to_enable="${ allowed_to_enable }"
							<c:if test="${not authAccessLevel.projectOwnerAllowed}" > disabled </c:if>
							>No</label>
						
						<div style="font-size: 80%;">
							Enabling the public access code will require the public to use the
							specially formatted URL below (which contains an unguessable key) to access
							any project data. 
							Disabling this access code means that the public may access
							the project or any of its data directly without the need to first use the special
							URL. 
							This is useful for directly sharing pages from specific searches or runs.
  							
						</div>
					  </c:if>
						
						
						<div id="project_public_access_code_outer_div" 
							style="margin-top: 15px;  " >
	
							Project public access URL:
							<br>
							<div id="project_public_access_code_div" class="public-access-url">

							</div>
	
							<div id="project_public_access_code_template_div" style="display: none;" 
								><bean:write name="<%= WebConstants.REQUEST_URL_ONLY_UP_TO_WEB_APP_CONTEXT %>" 
								/><%= StrutsActionPathsConstants.PROJECT_READ_PROCESS_CODE %>?<%= WebConstants.PARAMETER_PROJECT_READ_CODE %>=</div>
							
							<br>
						</div>
						
					</div>
		
						<c:if test="${authAccessLevel.projectOwnerAllowed or authAccessLevel.projectOwnerIfProjectNotLockedAllowed}" >

		
							<!--  Modal dialog for confirming Generating new public access code -->
					
								<!--  Div behind modal dialog div -->
					
							<div class="modal-dialog-overlay-background   generate_new_pub_access_code_overlay_show_hide_parts_jq generate_new_pub_access_code_overlay_cancel_parts_jq  overlay_show_hide_parts_jq" 
								id="generate_new_pub_access_code_overlay_background" ></div>
							
									<!--  Inline div for positioning modal dialog on page -->
							<div class="generate-new-pub-access-code-overlay-containing-outermost-div " id="generate_new_pub_access_code_overlay_containing_outermost_div_inline_div"  >
				
							  <div class="generate-new-pub-access-code-overlay-containing-outer-div " >
							
					
									<!--  Div overlay for confirming removing a note -->
								<div class="modal-dialog-overlay-container generate-new-pub-access-code-overlay-container   generate_new_pub_access_code_overlay_show_hide_parts_jq  overlay_show_hide_parts_jq" 
									 id="generate_new_pub_access_code_overlay_container" >
				
									<div class="top-level-label" style="margin-left: 0px;">Generate New Public Access Code</div>
					
									<div class="top-level-label-bottom-border" ></div>
									
									<div >
									
										<div >Are you sure you want to generate a new public access code?</div>
										
										<div style="margin-top: 10px">
											<input type="button" value="Yes" id="generate_new_pub_access_code_confirm_button" >
											<input type="button" value="Cancel" class="generate_new_pub_access_code_overlay_cancel_parts_jq" >
										</div>
											
									</div>
									
								</div>
							
							  </div>
							</div>
							
							
							<!-- END:   Modal dialog for confirming deleting a note -->
							
									
	
						<div >
							<input class="submit-button first_enable_when_public_access_not_locked_jq " 
								type="button" value="Enable Public Access" 
								id="enable_project_public_access_button"
								style="display: none;"
								allowed_to_enable="${ allowed_to_enable }"
								<c:if test="${not authAccessLevel.projectOwnerAllowed}" > disabled </c:if>
							>
					
							<input class="submit-button first_enable_when_public_access_not_locked_jq " 
								type="button" value="Disable Public Access" 
								id="disable_project_public_access_button"
								style="display: none;"
								allowed_to_enable="${ allowed_to_enable }"
								<c:if test="${not authAccessLevel.projectOwnerAllowed}" > disabled </c:if>
							>
		
							
							<input class="submit-button first_enable_when_public_access_not_locked_jq " 
								type="button" value="Generate New Public Access Code" 
								id="generate_new_public_access_code_button"
								style="display: none;"
								allowed_to_enable="${ allowed_to_enable }"
								<c:if test="${not authAccessLevel.projectOwnerAllowed}" > disabled </c:if>
							>
									
							<input class="submit-button " 
								type="button" value="Lock Public Access" 
								id="lock_project_public_access_button"
								style="display: none;"
								allowed_to_enable="${ allowed_to_enable }"
								<c:if test="${not authAccessLevel.projectOwnerAllowed}" > disabled </c:if>
							>
							
																					
							<input class="submit-button " 
								type="button" value="Unlock Public Access" 
								id="unlock_project_public_access_button"
								style="display: none;"
								allowed_to_enable="${ allowed_to_enable }"
								<c:if test="${not authAccessLevel.projectOwnerAllowed}" > disabled </c:if>
							>						
						
						</div>	
						
						<br>	
		
		
						<div >
						
							Disabling public access prevents access to the project. 
							Generating a new public	access key invalidates the code above and replaces it with a new code. 
							Locking	public access helps prevent accidentally changing public access settings.

						</div>
						<br>
						
					</c:if> <%-- END  test="${authAccessLevel.projectOwnerAllowed}" --%>
					
				</div>
			</div>

			<!--  END Public Access -->

				
			</c:if>	  <%-- END  <c:if test="${authAccessLevel.assistantProjectOwnerAllowed}" >  --%>


		<%-- Place outside of submitted form --%>
		<input type="hidden" id="project_id" value="<c:out value="${project_id}" />" />

			
				
			<!--  Search Data -->

			<form action="" method="get" id="viewMergedDataForm">


			
			<div class="top-level-container collapsable_container_jq" >
			
				<div  class="collapsable-link-container top-level-collapsable-link-container collapsable_link_container_jq" style="">
					<a href="javascript:" class="top-level-collapsable-link collapsable_collapse_link_jq"
						><img  src="${ contextPath }/images/icon-collapse.png"></a>
					<a href="javascript:" class="top-level-collapsable-link collapsable_expand_link_jq" style="display: none;"
						><img  src="${ contextPath }/images/icon-expand.png"></a>
				
				</div>
					
				<div class="top-level-label" >Explore Data</div>
	
				<div class="top-level-label-bottom-border" ></div>

	
				<%--  If admin section rendered, include the Javascript for it --%>
				<c:if test="${authAccessLevel.assistantProjectOwnerAllowed and not empty otherProjectList}" >
			
						<%--  Div behind modal dialog div --%>
			
					<div class="modal-dialog-overlay-background   move_searches_overlay_show_hide_parts_jq move_searches_overlay_cancel_parts_jq  overlay_show_hide_parts_jq" 
						id="move-searches-overlay-background" ></div>
			
						<%--  Inline div for positioning modal dialog on page --%>
					<div class="move-searches-overlay-containing-full-width-div " >
					
							<%--  Inline div for positioning modal dialog on page --%>
					  <div class="move-searches-overlay-containing-relative-div " >
					
							<%--  Div overlay for moving searches --%>
					
						<div class="modal-dialog-overlay-container move-searches-overlay-container   move_searches_overlay_show_hide_parts_jq  overlay_show_hide_parts_jq" 
							 id="move-searches-overlay-container" style="top: -30px;">

							<div class="top-level-label" style="margin-left: 0px;">Move Search Data</div>
			
							<div class="top-level-label-bottom-border" ></div>
							
							<div id="move-searches-overlay-select-project-block">
							
								<div >Click the title of the project to which you would like to move the selected searches.</div>
								
								<div class="move-searches-overlay-project-list-block">
	
								<div class="top-level-label-bottom-border" ></div>
									
									<c:forEach var="otherProject" items="${otherProjectList}">
									
									  <div >
										<a href="javascript:"  otherProjectId="${otherProject.id}"
											class="move_search_project_choice_jq" 
											><c:out value="${otherProject.title}"></c:out></a> 
								
										<div class="top-level-label-bottom-border" ></div>
									  </div>
									</c:forEach>
									
									<div style="float: right;">
										<input type="button" value="Cancel" class="move_searches_overlay_cancel_parts_jq" >
									</div>
									
								</div>	
							</div>
							
							<div id="move-searches-overlay-confirm-project-block"  class="move_searches_overlay_confirmation_steps_jq" style="display: none;">
							
								<div style="margin-bottom: 10px;">Move selected searches to "<span id="move-searches-overlay-project-to-move-title"></span>"?</div>
								<div >
									<input type="button" value="Yes" id="move_search_confirm_button" >
									<input type="button" value="Cancel" class="move_searches_overlay_cancel_parts_jq" >
								</div>								
									
							</div>
							
							<div id="move-searches-overlay-confirmation-project-block" class="move_searches_overlay_confirmation_steps_jq" style="display: none;">

								<div >
									Searches moved successfully
								</div>
														
								<input type="button" value="Go to project the searches were moved to" id="show-project-searches-moved-to" >

								<input type="button" value="Return to Project" class="move_searches_overlay_cancel_parts_jq" >

							</div>

							<div id="move-searches-overlay-move-to-project-marked-for-deletion-block"  class="move_searches_overlay_confirmation_steps_jq" style="display: none;">
							
								<div  style="margin-bottom: 10px;">
									Move Searches Failed.  Project moving to is marked for deletion.  Please reload the page to get a current list of valid projects to move to.
								</div>
								<div >
									<input type="button" value="Cancel" class="move_searches_overlay_cancel_parts_jq" >
								</div>
							</div>
							
							<div id="move-searches-overlay-move-to-project-disabled-block"  class="move_searches_overlay_confirmation_steps_jq" style="display: none;">
							
								<div  style="margin-bottom: 10px;">
									Move Searches Failed.  Project moving to is disabled.  Please reload the page to get a current list of valid projects to move to.
								</div>
								<div >
									<input type="button" value="Cancel" class="move_searches_overlay_cancel_parts_jq" >
								</div>
							</div>
							
							<div id="move-searches-overlay-move-project-failed-block"  class="move_searches_overlay_confirmation_steps_jq" style="display: none;">
							
								<div  style="margin-bottom: 10px;">
									Move Searches Failed.  Please reload the page to get a current list of valid projects to move to.  
									If you continue to get this error message, contact the administrator.
								</div>
								<div >
									<input type="button" value="Cancel" class="move_searches_overlay_cancel_parts_jq" >
								</div>
							</div>
							
						</div>
					
					  </div>
					</div>
					
					
						
			
				</c:if>
									
				<div class="searches-block collapsable_jq">
															
				 <c:choose>
				  <c:when test="${noAccess}">
				    
				    	No access is allowed to any data on this website.
				    
				  </c:when>
				  <c:when test="${empty SearchDTODetailsDisplayWrapperList}">
				    
				    	<div  class="no-searches-in-project-text">
				    		No searches in this project.
				    	</div>
				    
				  </c:when>
				  <c:otherwise>		
				  
				  	<c:if test="${authAccessLevel.assistantProjectOwnerAllowed or authAccessLevel.assistantProjectOwnerIfProjectNotLockedAllowed }" >
		
					 <!--  Modal dialog for confirming deleting a search -->
			
						<!--  Div behind modal dialog div -->
			
					 <div class="modal-dialog-overlay-background   delete_search_overlay_show_hide_parts_jq delete_search_overlay_cancel_parts_jq  overlay_show_hide_parts_jq" 
						id="delete_search_overlay_background" ></div>
					
							<!--  Inline div for positioning modal dialog on page -->
					 <div class="delete-search-overlay-containing-outermost-div " id="delete_search_overlay_containing_outermost_div_inline_div"  >
		
					  <div class="delete-search-overlay-containing-outer-div " >
					
			
							<!--  Div overlay for confirming removing a search -->
						<div class="modal-dialog-overlay-container delete-search-overlay-container   delete_search_overlay_show_hide_parts_jq  overlay_show_hide_parts_jq" 
							 id="delete_search_overlay_container" >
		
							<div class="top-level-label" style="margin-left: 0px;">Delete Search</div>
			
							<div class="top-level-label-bottom-border" ></div>
							
							<div >
							
								<div >Delete the search <span style="font-weight: bold;" id="delete_search_overlay_search_name"></span>?</div>
								
								<div style="margin-top: 10px">
									<input type="button" value="Yes" id="delete_search_confirm_button" >
									<input type="button" value="Cancel" class="delete_search_overlay_cancel_parts_jq" >
								</div>
									
							</div>
							
						</div>
					
					  </div>
					 </div>
					
					
					 <!-- END:   Modal dialog for confirming deleting a search -->
					
									

					 <!--  Modal dialog for confirming deleting a search comment -->
			
						<!--  Div behind modal dialog div -->
			
					 <div class="modal-dialog-overlay-background   delete_search_comment_overlay_show_hide_parts_jq delete_search_comment_overlay_cancel_parts_jq  overlay_show_hide_parts_jq" 
						id="delete_search_comment_overlay_background" ></div>
					
							<!--  Inline div for positioning modal dialog on page -->
					 <div class="delete-search-comment-overlay-containing-outermost-div " id="delete_search_comment_overlay_containing_outermost_div_inline_div"  >
		
					  <div class="delete-search-comment-overlay-containing-outer-div " >
					
			
							<!--  Div overlay for confirming removing a search comment -->
						<div class="modal-dialog-overlay-container delete-search-comment-overlay-container   delete_search_comment_overlay_show_hide_parts_jq  overlay_show_hide_parts_jq" 
							 id="delete_search_comment_overlay_container" >
		
							<div class="top-level-label" style="margin-left: 0px;">Delete Search Comment</div>
			
							<div class="top-level-label-bottom-border" ></div>
							
							<div >
							
								<div >Delete the comment?</div>
								
								<div style="margin-top: 10px">
									<input type="button" value="Yes" id="delete_search_comment_confirm_button" >
									<input type="button" value="Cancel" class="delete_search_comment_overlay_cancel_parts_jq" >
								</div>
									
							</div>
							
						</div>
					
					  </div>
					 </div>
					
					
					 <!-- END:   Modal dialog for confirming deleting a search comment -->
					


					 <!--  Modal dialog for confirming deleting a search Web Link -->
			
						<!--  Div behind modal dialog div -->
			
					 <div class="modal-dialog-overlay-background   delete_search_web_link_overlay_show_hide_parts_jq delete_search_web_link_overlay_cancel_parts_jq  overlay_show_hide_parts_jq" 
						id="delete_search_web_link_overlay_background" ></div>
					
							<!--  Inline div for positioning modal dialog on page -->
					 <div class="delete-search-web-link-overlay-containing-outermost-div " id="delete_search_web_link_overlay_containing_outermost_div_inline_div"  >
		
					  <div class="delete-search-web-link-overlay-containing-outer-div " >
					
			
							<!--  Div overlay for confirming removing a search Web Link -->
						<div class="modal-dialog-overlay-container delete-search-web-link-overlay-container   delete_search_web_link_overlay_show_hide_parts_jq  overlay_show_hide_parts_jq" 
							 id="delete_search_web_link_overlay_container" >
		
							<div class="top-level-label" style="margin-left: 0px;">Delete Search Web Link</div>
			
							<div class="top-level-label-bottom-border" ></div>
							
							<div >
							
								<div >Delete the Web Link?</div>
								
								<div style="margin-top: 10px">
									<input type="button" value="Yes" id="delete_search_web_link_confirm_button" >
									<input type="button" value="Cancel" class="delete_search_web_link_overlay_cancel_parts_jq" >
								</div>
									
							</div>
							
						</div>
					
					  </div>
					 </div>
					
					
					 <!-- END:   Modal dialog for confirming deleting a search Web Link -->
					 
					</c:if>
														  									
									  
				  	<c:if test="${authAccessLevel.assistantProjectOwnerAllowed or authAccessLevel.assistantProjectOwnerIfProjectNotLockedAllowed }" >
						<div style="margin-bottom:10px;">
							<input class="submit-button" type="button" value="Expand All" onClick="javascript:expandAll()">
							<input class="submit-button" type="button" value="Collapse All" onClick="javascript:collapseAll()">
			
							<c:if test="${not empty otherProjectList}" >
								
								<div style="display:inline-block;position:relative;"> <%-- outer div to support overlay div when button disabled --%>
									<input class="submit-button tool_tip_attached_jq " type="button" id="move_search_button"
											 data-tooltip="Click here to move the selected searches to another project."
											value="Move Searches" disabled>
										<%-- overlay div to provide tooltip for button --%>
									<div class=" tool_tip_attached_jq   " id="move_search_button_cover_when_disabled" 
										style="position:absolute;left:0;right:0;top:0;bottom:0;" 
										data-tooltip="Select one or more searches below and click here to move the selected searches to another project." ></div>
								</div>
								
							</c:if>
				
						</div>
					</c:if>
					
					
					<div>
					    
						<logic:iterate id="search_wrapper" name="SearchDTODetailsDisplayWrapperList">
						
							<c:set var="search" value="${ search_wrapper.searchDTO }" />
						
						  <div searchId="<bean:write name="search" property="id" />" class=" search_row_jq" >

							<table style="padding:0px;margin-top:0px;margin-bottom:0px;width:100%;">
							
								<tr searchId="<bean:write name="search" property="id" />" class=" search_root_jq ">
		
									<td style="width:10px;" valign="top" class="search-checkbox-cell">
										<input id="search-checkbox-<bean:write name="search" property="id" />" onChange="javascript:checkSearchCheckboxes(<bean:write name="search" property="id" />)" class="search-checkbox" type="checkbox" name="searchIds" value="<bean:write name="search" property="id" />"/>
									</td>
									<td>
						
		
									  <div style="float: right;" >
										
										[<a data-tooltip="View peptides found in search" class="tool_tip_attached_jq" 
											href="${ contextPath }/<proxl:defaultPageUrl pageName="/peptide" searchId="${ search.id }">peptide.do?searchId=<bean:write name="search" property="id" /></proxl:defaultPageUrl>"
												>Peptides</a>]
										
										[<a data-tooltip="View proteins found in search" class="tool_tip_attached_jq" 
											href="${ contextPath }/<proxl:defaultPageUrl pageName="/crosslinkProtein" searchId="${ search.id }">crosslinkProtein.do?searchId=<bean:write name="search" property="id" /></proxl:defaultPageUrl>"
												>Proteins</a>]

										[<a data-tooltip="Graphical view of links between proteins" class="tool_tip_attached_jq" 
											href="${ contextPath }/<proxl:defaultPageUrl pageName="/image" searchId="${ search.id }">image.do?searchIds=<bean:write name="search" property="id" /></proxl:defaultPageUrl>"
												>Image</a>]

										<c:choose>
										 <c:when test="${ showStructureLink }">
										

											[<a data-tooltip="View data on 3D structures" class="tool_tip_attached_jq" 
												href="${ contextPath }/<proxl:defaultPageUrl pageName="/structure" searchId="${ search.id }">structure.do?searchIds=<bean:write name="search" property="id" /></proxl:defaultPageUrl>"
													>Structure</a>]
																							
										 </c:when>
										 <c:otherwise>
										 	
											<%@ include file="/WEB-INF/jsp-includes/structure_link_non_link.jsp" %>
										 	
										 </c:otherwise>
										</c:choose>
		
			
			
										<c:if test="${authAccessLevel.searchDeleteAllowed}" >
											<a href="javascript:" data-tooltip="Delete search" class="tool_tip_attached_jq delete_search_link_jq"
											 		<%-- WAS  href="javascript:confirmDelete(<bean:write name="search" property="id" />)"  --%>
												><img src="${ contextPath }/images/icon-delete-small.png" ></a>
										</c:if>
										
									  </div>
		
									  <div>
		
										<a class="tool_tip_attached_jq expand-link" data-tooltip="Show or hide more details" id="search-details-link-<bean:write name="search" property="id" />" style="font-size:80%;color:#4900d4;text-decoration:none;" href="javascript:showSearchDetails(<bean:write name="search" property="id" />)"
											><img src="${ contextPath }/images/icon-expand-small.png" <%-- This image src is changed in the Javascript --%>
											></a>
										
										<span id="search-name-normal-<bean:write name="search" property="id" />"
											><span class="search-name-display  search_name_display_jq" id="search-name-display-<bean:write name="search" property="id" />"
												><bean:write name="search" property="name" /></span
											 ><c:if test="${authAccessLevel.writeAllowed}" 
											 		> <span class="search-name-display search_number_in_parens_display_jq ">(<bean:write name="search" property="id" />)</span></c:if><c:if test="${authAccessLevel.writeAllowed}" 
											 		><a class="tool_tip_attached_jq" data-tooltip="Edit name of search" href="javascript:showSearchNameForm(<bean:write name="search" property="id" />)"
												><img class="edit-icon" src="${ contextPath }/images/icon-edit-small.png" 
													></a></c:if></span>
													
										<span style="display:none;" id="search-name-edit-<bean:write name="search" property="id" />"><input id="search-name-value-<bean:write name="search" property="id" />" type="text" style="width:200px;" value="<bean:write name="search" property="name" />"><input class="submit-button" type="button" value="Save" onClick="saveName(<bean:write name="search" property="id" />)"><input class="submit-button" type="button" value="Cancel" onClick="cancelNameEdit(<bean:write name="search" property="id" />)"></span>
										
									  </div>
									  <div style="clear: right;"  class="search-details-container-div">
																	
										<table class="search-details" id="search-details-<bean:write name="search" property="id" />" style="display:none;margin-left:15px;">

										  <c:if test="${ authAccessLevel.writeAllowed or authAccessLevel.assistantProjectOwnerIfProjectNotLockedAllowed }" >
											<tr>
												<td>Path:</td>
												<td><bean:write name="search" property="path" /></td>
											</tr>
										  </c:if>
											
											
											<tr>
												<td>Linker:</td>
												<td><c:out value="${ search_wrapper.linkersDisplayString }"></c:out></td>
											</tr>								  

											<tr>
												<td valign="top"  >
													Search 
													Program<c:if test="${ fn:length( search_wrapper.searchPrograms ) > 1 }" >s</c:if>:
												</td>
												
												 <c:choose>
												  <c:when test="${ empty search_wrapper.searchPrograms }">
													<td  style="padding-top: 2px;">
													  	Not Found
													</td>
												  </c:when>
												  <c:otherwise>
												    <td style="border-width:0px; padding: 0px;">

													<table  style="border-width:0px; border-spacing: 0px; ">

												   <c:forEach var="searchProgram" items="${ search_wrapper.searchPrograms }">
												     <tr>
												      <td style="padding-right: 5px;">
												     	<c:out value="${ searchProgram.displayName }"></c:out>
												      </td>
												      <td >
												     	<c:out value="${ searchProgram.version }"></c:out>
												      </td>
												     </tr>
												   </c:forEach>

												    </table>
												  
													</td>
												  </c:otherwise> 
												 </c:choose>
											</tr>								  
											
											<tr>
												<td>Upload:</td>
												<td><bean:write name="search" property="formattedLoadTime" /></td>
											</tr>
											
											<tr>
												<td>QC Plots:</td>
												<td>
													<c:if test="${ not search.noScanData }" >
														[<a href="javascript:" data-tooltip="View scan counts as function of retention time" class="tool_tip_attached_jq qc_plot_scan_retention_time_link_jq" >Retention Time</a>]
													</c:if>
												
													[<a href="javascript:" data-tooltip="View PSM counts as function of score" class="tool_tip_attached_jq qc_plot_psm_count_vs_score_link_jq" >PSM Count vs/ Score</a>]
												</td>
											</tr>
											

											<c:if test="${ not ( not authAccessLevel.writeAllowed and empty search.webLinks ) }" >
											 
													<%--  Hide this block if no Web Links and user unable to add Web Links --%>
											<tr>
												<td valign="top">Raw MS data files:</td>
												<td id="search-web-links-<bean:write name="search" property="id" />">
													
													<div style="position: relative;">			 
												  		<div class="error-message-container error_message_container_jq" 
												  				id="error_message_web_link_url_invalid_<bean:write name="search" property="id" />"
												  				style="width: 600px;">
												  			<div class="error-message-inner-container" >
												  				<div class="error-message-close-x error_message_close_x_jq">X</div>
													  			<div class="error-message-text" >Web Link URL is invalid</div>
												  			</div>
													  	</div>	
													  </div>
	  	
	  	
													<logic:iterate name="search" property="webLinks" id="webLink" >
													
														<%--  Keep this block in sync with the Template just below --%>
														
														<div id="web-links-<bean:write name="webLink" property="id" />"
															class="search_web_link_root_jq"
															searchwebLinkId="<bean:write name="webLink" property="id" />" 
															style="margin-bottom:5px; margin-top:0px;">
															<c:if test="${authAccessLevel.writeAllowed}" >
																<a id="web-links-delete-<bean:write name="webLink" property="id" />" 
																	style="color:#d40000;font-size:80%;" 
																	class="tool_tip_attached_jq delete_search_webLink_link_jq"
																	data-tooltip="Delete link to RAW file"
																	href="javascript:"
																		><img src="${ contextPath }/images/icon-delete-small.png"></a>
															</c:if>
															<a  target="_blank" href="<bean:write name="webLink" property="linkUrl" />" 
																><bean:write name="webLink" property="linkLabel" /></a>
														</div>
													</logic:iterate>
		
													<c:if test="${authAccessLevel.writeAllowed}" >
													 <div >
														<div id="add-web-links-link-span-<bean:write name="search" property="id" />"
															>[<a id="add-web-link-link-<bean:write name="search" property="id" />" 
																style="font-size:80%;text-decoration:none;" 
																href="javascript:showAddWebLink(<bean:write name="search" property="id" />)"
																class="tool_tip_attached_jq" data-tooltip="Add URL for a RAW file">+Link to Raw file</a>]</div>
														<div style="display:none;" id="add-web-links-form-span-<bean:write name="search" property="id" />" >
														 <div>
														  URL:
														  <input id="web-links-url-input-<bean:write name="search" property="id" />" 
																type="text" style="font-size:80%;width:200px;">
														  Label:
														  <input id="web-links-label-input-<bean:write name="search" property="id" />" 
																type="text" style="font-size:80%;width:200px;">
														  <input style="font-size:80%;" class="submit-button" type="button" value="Add Web Link" 
																	onClick="addWebLink(<bean:write name="search" property="id" />)">
														  <input style="font-size:80%;" class="submit-button" type="button" value="Cancel" 
																 	onClick="cancelWebLink(<bean:write name="search" property="id" />)" >
														  </div>
														  <div style="font-size: 80%;">
														  	The URL must start with "http://", "https://", "ftp://" or some other transport protocal
														  </div>
														</div>
													 </div>
													</c:if>
													
													
													<%--  Template for links added by Javascript --%>
													<div id="web_link_template" style="display: none;">
														<div id="web-links-{{id}}"
															class="search_web_link_root_jq"
															searchwebLinkId="{{id}}" 
															style="margin-bottom:5px; margin-top:0px;">
															<c:if test="${authAccessLevel.writeAllowed}" >
																<a  id="web-links-delete-{{id}}" 
																	style="color:#d40000;font-size:80%;" 
																	class="tool_tip_attached_jq delete_search_webLink_link_jq"
																	data-tooltip="Delete link to RAW file"
																	href="javascript:"
																		><img src="${ contextPath }/images/icon-delete-small.png"></a>
															</c:if>
															<a  target="_blank" href="{{linkUrl}}" >{{linkLabel}}</a>
														</div>
													</div>
													
												</td>
											</tr>

											</c:if>
																						
											
											<c:if test="${ not empty search.files }">
											  <tr>
												<td valign="top">Additional files:</td>
												<td >
												  <logic:iterate name="search" property="files" id="searchFile" >
													
													<div class=" display_search_filename_outer_container_jq " search_file_id="${ searchFile.id }" search_id="${ search.id }"> 
													
													 <div class="display_search_filename_container_jq">
													 
													  <%--  Normal display of link with filename --%>

													  <a href="downloadSearchFile.do?project_id=<c:out value="${project_id}" />&fileId=<bean:write name="searchFile" property="id" />" 
													  	class="tool_tip_attached_jq search_file_link_for_tooltip_jq" data-tooltip="Download file">
													  		
													  	<span class="search_filename_jq">
															<bean:write name="searchFile" property="displayFilename" />
														</span>
														<c:if test="${authAccessLevel.projectOwnerAllowed}"> 
															<a class="tool_tip_attached_jq" data-tooltip="Edit name" href="javascript:" onclick="showSearchFilenameForm( this )"
																><img class="edit-icon" src="${ contextPath }/images/icon-edit-small.png" 
																	></a>
														</c:if>
																	
															<c:if test="${authAccessLevel.assistantProjectOwnerAllowed or authAccessLevel.assistantProjectOwnerIfProjectNotLockedAllowed }">
																<span class="search_file_link_tooltip_jq"  style="display: none;"
																	><bean:write name="searchFile" property="description" /></span>
															</c:if>
													  </a>
													 </div>
												
												  	<c:if test="${authAccessLevel.assistantProjectOwnerAllowed}" >
			 
													 <div class=" edit_search_filename_container_jq " style="display: none;">

													  <%--  Edit filename --%>

														<input type="text" style="width:200px;" value="<bean:write name="searchFile" property="displayFilename" />"
															class=" edit_search_filename_input_field_jq "
															><input class="submit-button" type="button" value="Save" onClick="saveSearchFilename( this )"
															><input class="submit-button" type="button" value="Cancel" onClick="cancelSearchFilenameEdit( this )">													 
													 
													 </div>
													
													</c:if>  <%--  END <c:if test="${authAccessLevel.assistantProjectOwnerAllowed}" > --%>
													  
													</div>
												  </logic:iterate>
		
												</td>
											  </tr>
											</c:if>
											
											<c:if test="${ not ( not authAccessLevel.writeAllowed and empty search.comments ) }" >
											 
													<%--  Hide this block if no comments and user unable to add comments --%>
											  <tr>
												<td valign="top">Comments:</td>
												<td id="search-comments-<bean:write name="search" property="id" />">
		
													<logic:iterate name="search" property="comments" id="comment" >

														<%--  Keep this block in sync with the Template just below --%>
																											
														<div id="comment-<bean:write name="comment" property="id" />"
															class="search_comment_root_jq"
															searchCommentId="<bean:write name="comment" property="id" />" 
															style="margin-bottom:5px; margin-top:0px;">
															
														  <div class=" search_comment_display_jq ">
															<c:if test="${authAccessLevel.writeAllowed}" >
																<a class="tool_tip_attached_jq" data-tooltip="Delete comment" style="color:#d40000;font-size:80%;" 
																	onclick="deleteSearchCommentClickHandler(this);return false;" 
																	href="javascript:"
																		><img src="${ contextPath }/images/icon-delete-small.png"></a>
															</c:if>
															<span class=" search_comment_string_jq "
																><bean:write name="comment" property="comment" 
															/></span>
															
															<c:if test="${authAccessLevel.writeAllowed}" >
															  <a class="tool_tip_attached_jq" data-tooltip="Edit comment"  onclick="showSearchCommentEditForm( this ); return false;" href="javascript:" >
																<img class="edit-icon" src="${ contextPath }/images/icon-edit-small.png">
															  </a> 
															</c:if>
															
															(<span class=" search_comment_date_jq "
																><bean:write name="comment" property="dateTimeString" /></span>)
														  </div>
														  
														  <c:if test="${authAccessLevel.writeAllowed}" >
														  
														  	<%--  For editing the comment value --%>
														  	<div class=" search_comment_edit_jq " style="display: none;">
														  
														  		<input type="text" class="search_comment_input_field_jq" style="width:200px;" >
														  		<input class="submit-button" type="button" value="Save" 
														  			onclick="updateSearchComment( this ); return false;" >
														  		<input class="submit-button" type="button" value="Cancel" 
														  			onclick="cancelSearchCommentEditForm( this ); return false;" >


														  	</div>
														  </c:if>
														  
														  
														</div>
													</logic:iterate>
		
													<c:if test="${authAccessLevel.writeAllowed}" >
													
														<%--  Template for search comments added by Javascript --%>
														<div id="search_comment_template" style="display: none;">
															<div id="comment-{{id}}"
																class="search_comment_root_jq"
																searchCommentId="{{id}}" 
																style="margin-bottom:5px; margin-top:0px;">
																
														  	  <div class=" search_comment_display_jq ">
																
																<c:if test="${authAccessLevel.writeAllowed}" >
																	<a class="tool_tip_attached_jq" data-tooltip="Delete comment" style="color:#d40000;font-size:80%;" 
																		onclick="deleteSearchCommentClickHandler(this);return false;" 
																		href="javascript:"
																			><img src="${ contextPath }/images/icon-delete-small.png"></a>
																</c:if>
																<span class=" search_comment_string_jq "
																	>{{comment}}</span>
																
																<c:if test="${authAccessLevel.writeAllowed}" >
																  <a class="tool_tip_attached_jq" data-tooltip="Edit comment" onclick="showSearchCommentEditForm( this ); return false;" href="javascript:" >
																	<img class="edit-icon" src="${ contextPath }/images/icon-edit-small.png">
																  </a> 
																</c:if>
																																
																(<span class=" search_comment_date_jq "
																	>{{dateTimeString}}</span>)

														  	  </div>
														  	  
															  
															  <c:if test="${authAccessLevel.writeAllowed}" >
															  
															  	<%--  For editing the comment value --%>
															  	<div class=" search_comment_edit_jq " style="display: none;">
															  
															  		<input type="text" class="search_comment_input_field_jq" style="width:200px;" >
															  		<input class="submit-button" type="button" value="Save" 
															  			onclick="updateSearchComment( this ); return false;" >
															  		<input class="submit-button" type="button" value="Cancel" 
															  			onclick="cancelSearchCommentEditForm( this ); return false;" >
	
	
															  	</div>
															  </c:if>
															  	  
																	
															</div>
														</div>													
													
														<span id="add-comment-link-span-<bean:write name="search" property="id" />"
															>[<a class="tool_tip_attached_jq" data-tooltip="Add a comment" id="add-comment-link-<bean:write name="search" property="id" />" style="font-size:80%;text-decoration:none;" href="javascript:showAddComment(<bean:write name="search" property="id" />)"
																>+Comment</a>]</span>
														<span style="display:none;" id="add-comment-form-span-<bean:write name="search" property="id" />"
															><input id="comment-input-<bean:write name="search" property="id" />" type="text" style="font-size:80%;width:200px;"
															><input style="font-size:80%;" class="submit-button" type="button" value="Add Comment" onClick="addComment(<bean:write name="search" property="id" />)"
															><input style="font-size:80%;" class="submit-button" type="button" value="Cancel" onClick="cancelComment(<bean:write name="search" property="id" />)"
															></span>
													</c:if>
													
												</td>
											  </tr>

											</c:if>
											
											
							 											
										</table>
									  </div>
			
									 </td>
							</table>
			
							<div class="search-entry-bottom-border"></div>
			
						  </div>
						  
						</logic:iterate>
		
					</div>
					
					<div style="display:inline-block;position:relative;"> <%-- outer div to support overlay div when button disabled --%>
						<input onClick="viewMergedPeptides()" class="merge-button submit-button" type="button" value="View Merged Peptides" disabled>
							<%-- overlay div to provide tooltip for button --%>
						<div class=" tool_tip_attached_jq  merge_button_disabled_cover_div_jq " 
							style="position:absolute;left:0;right:0;top:0;bottom:0;" 
							data-tooltip="Click on two or more searches above and click here to view combined results." ></div>
					</div>
					<div style="display:inline-block;position:relative;"> <%-- outer div to support overlay div when button disabled --%>
						<input onClick="viewMergedProteins()" class="merge-button submit-button" type="button" value="View Merged Proteins" disabled>
							<%-- overlay div to provide tooltip for button --%>
						<div class=" tool_tip_attached_jq   merge_button_disabled_cover_div_jq "
							style="position:absolute;left:0;right:0;top:0;bottom:0;" 
							data-tooltip="Click on two or more searches above and click here to view combined results." ></div>
					</div>
					<div style="display:inline-block;position:relative;"> <%-- outer div to support overlay div when button disabled --%>
						<input onClick="viewMergedImage()" class="merge-button submit-button" type="button" value="View Merged Image" disabled>
							<%-- overlay div to provide tooltip for button --%>
						<div class=" tool_tip_attached_jq   merge_button_disabled_cover_div_jq "
							style="position:absolute;left:0;right:0;top:0;bottom:0;" 
							data-tooltip="Click on two or more searches above and click here to view combined results." ></div>
					</div>
					

					<c:choose>
					 <c:when test="${ showStructureLink }">
					
					  <div style="display:inline-block;position:relative;"> <%-- outer div to support overlay div when button disabled --%>
						<input onClick="viewMergedStructure()" class="merge-button submit-button" type="button" value="View Merged Structure" disabled>
							<%-- overlay div to provide tooltip for button --%>
						<div class=" tool_tip_attached_jq   merge_button_disabled_cover_div_jq " 
							style="position:absolute;left:0;right:0;top:0;bottom:0;" 
							data-tooltip="Click on two or more searches above and click here to view combined results." ></div>
					  </div>
					
					 </c:when>
					 <c:otherwise>
					 	<%-- Permanently disabled button since no structure to view for this project.  Removed the class "merge-button" --%>
						
						<div style="display:inline-block;position:relative;"> <%-- outer div to support overlay div when button disabled --%>
								<input onClick="" type="button" class="submit-button  " value="View Merged Structure" disabled>										
									<%-- overlay div to provide tooltip for button --%>
								<div class=" tool_tip_attached_jq " style="position:absolute;left:0;right:0;top:0;bottom:0;" data-tooltip="A structure has not been uploaded to this project." ></div>
						</div>
					 </c:otherwise>
					</c:choose>
					
				
				  </c:otherwise>
				 </c:choose>
				 					
				</div>
			</div>
		</form>

		</div>
	
	
	


		
		<!-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! -->
		
		<!--   Overlays for displaying QC Quality Control Plots/Charts   -->

		
		<!--  Modal dialog for displaying the Retention Time QC plot -->


			<%--   Overlay Background --%>
			
		<div id="scan_retention_time_qc_plot_overlay_background" 
			class=" qc-plot-overlay-background   scan_retention_time_qc_plot_overlay_show_hide_parts_jq scan_retention_time_qc_plot_overlay_close_parts_jq  overlay_show_hide_parts_jq"  
			style="display: none;"  >
		
		</div>
			
			<%--  Retention Time QC plot Overlay Div --%>
			
				<!--  Inline div for positioning modal dialog on page -->
		<div class="qc-plot-overlay-containing-outermost-div " id="scan_retention_time_qc_plot_overlay_containing_outermost_div_inline_div" >

		  <div class="qc-plot-overlay-containing-outer-div " style="position: relative;" >
			
			
			
			<div id="scan_retention_time_qc_plot_overlay_container" 
				class=" qc-plot-overlay-div overlay-outer-div   scan_retention_time_qc_plot_overlay_show_hide_parts_jq  overlay_show_hide_parts_jq" 
				style="display: none; "  >
			
				<div id="scan_retention_time_qc_plot_overlay_header" class="qc-plot-overlay-header" style="width:100%; " >
				
					<h1 id="scan_retention_time_qc_plot_overlay_X_for_exit_overlay" 
						class="qc-plot-overlay-X-for-exit-overlay  scan_retention_time_qc_plot_overlay_close_parts_jq" 
						>X</h1>
						
					<h1 id="scan_retention_time_qc_plot_overlay_header_text" class="qc-plot-overlay-header-text" >QC Plot: Retention Time</h1>
				</div>
				<div id="scan_retention_time_qc_plot_overlay_body" class="qc-plot-overlay-body" >
			
			
				<div >
				
					<input type="hidden" id="scan_retention_time_qc_plot_current_search_id" >
		
					
					<table style="border-width:0px;">
						
						<tr>
							<td>Scan File:</td>
							<td>
								<select id="scan_retention_time_qc_plot_scan_file_id"  class="scan_retention_time_qc_plot_on_change_jq">
								
								</select>
							</td>
							

						</tr>
								
						<tr>
							<td>Filter PSMs by:</td>
							<td>
								<select id="scan_retention_time_qc_plot_score_type_id" >
									
								</select>							
							
								<input type="text" id="scan_retention_time_qc_plot_psm_score_cutoff"  
									class="scan_retention_time_qc_plot_on_change_jq"
									size="4">
									
								(min: <span id="scan_retention_time_qc_plot_min_value_for_ann_type_id"
									></span>, max: <span id="scan_retention_time_qc_plot_max_value_for_ann_type_id"></span>)
							</td>
						</tr>

				
						<tr>
							<td>Scans with:</td>
							<td>
							  <label >
								<input type="checkbox" class="scan_retention_time_qc_plot_scans_include_jq scan_retention_time_qc_plot_on_change_jq"
									value="<%=QCPlotConstants.RETENTION_TIME_PLOT_TYPE_SCANS_CONFIDENT_CROSSLINK_PSM%>" >
								crosslinks
							  </label>
							  <label >
								<input type="checkbox" class="scan_retention_time_qc_plot_scans_include_jq scan_retention_time_qc_plot_on_change_jq"
									value="<%=QCPlotConstants.RETENTION_TIME_PLOT_TYPE_SCANS_CONFIDENT_LOOPLINK_PSM%>" >
								looplinks
							  </label> 
							  <label >
								<input type="checkbox" class="scan_retention_time_qc_plot_scans_include_jq scan_retention_time_qc_plot_on_change_jq"
									value="<%=QCPlotConstants.RETENTION_TIME_PLOT_TYPE_SCANS_CONFIDENT_UNLINKED_PSM%>" >
								unlinked
							  </label>

							</td>
						</tr>
								
												
						<tr>
							<td>Max: </td>
							<td>
								X:
								<input type="text" id="scan_retention_time_qc_plot_max_x" 
									class="scan_retention_time_qc_plot_on_change_jq" 
									size="8"> 
								Y:
								<input type="text" id="scan_retention_time_qc_plot_max_y" 
									class="scan_retention_time_qc_plot_on_change_jq"
									size="8">
				
								<input type="button" id="scan_retention_time_qc_plot_max_reset_button" value="Reset">
							</td>
						</tr>	
						
					</table>


					<h1 class="scan_retention_time_qc_plot_filter_psms_by_param_not_a_number_jq" 
						style="display: none;  ">
					
						"Filter PSMs by" is not a number
					</h1>

					<h1 class="scan_retention_time_qc_plot_param_not_a_number_jq" 
						style="display: none;  ">
					
						Max X or Max Y is not empty and is not a number
					</h1>


					<div class="scan_retention_time_qc_plot_have_data_jq" >
	
						<div style="float: right; padding-right: 110px; ">
							
							<div >
								<svg width="10" height="10">
									<rect x="0" y="0" width="10" height="10" stroke="none" stroke-width="0" fill="#fff0f0"></rect>
								</svg>
								
								Scans
							</div>
												
							<div >
								<svg width="10" height="10">
									<rect x="0" y="0" width="10" height="10" stroke="none" stroke-width="0" fill="#a55353"></rect>
								</svg>
								Filtered PSMs
								
							</div>
	
						
						</div>
							
						
						<a style="font-size:10pt;white-space:nowrap;" href="javascript:" id="scan_retention_time_qc_plot_download_svg"
								title="Download a SVG file of the image">[Download SVG]</a>
					</div>						
					
					<div style="clear: both; " >
					
					
					
					</div>
					
					
					<h1 class="scan_retention_time_qc_plot_no_data_jq" 
						style="display: none;  ">
					
						No Data
					</h1>
					
					
					<%-- overflow: hidden; and left: -50px; to clip off padding from left side of chart --%>
					
<%-- 				commented out since on redraw shifts further left and hides vertical axis label
 	
					<div style="position: relative; width: 920px; height: 650px; overflow: hidden;" >
						<div style="position: absolute; top: 0px; left: -50px;">
--%>						
							<div id="scan_retention_time_qc_plot_chartDiv"
								style="width: 920px; height: 650px;"  
									<%-- style="width: 1050px; height: 800px;"   
										Keep this width and height in sync with the chart create config in the 
													JS as RETENTION_TIME_COUNT_CHART_WIDTH and RETENTION_TIME_COUNT_CHART_HEIGHT  --%>
								></div>
												
<%-- 					
						</div>
					</div>
--%>
					
									
				</div>
				
			</div>
		   
		   </div>
	
		  </div>
		</div>
		
		
		<!-- END:   Modal dialog for displaying the Retention Time QC plot -->
								
	
	

		
		<!--  Modal dialog for displaying the PSM Q Value Counts QC plot -->


			<%--   Overlay Background --%>
			
		<div id="psm_count_vs_score_qc_plot_overlay_background" 
			class=" qc-plot-overlay-background   psm_count_vs_score_qc_plot_overlay_show_hide_parts_jq psm_count_vs_score_qc_plot_overlay_close_parts_jq  overlay_show_hide_parts_jq"  
			style="display: none;"  >
		
		</div>
			
			<%--  PSM Q Value Counts QC plot Overlay Div --%>
			
				<!--  Inline div for positioning modal dialog on page -->
		<div class="qc-plot-overlay-containing-outermost-div " id="psm_count_vs_score_qc_plot_overlay_containing_outermost_div_inline_div" >

		  <div class="qc-plot-overlay-containing-outer-div " style="position: relative;" >
			
			
			
			<div id="psm_count_vs_score_qc_plot_overlay_container" 
				class=" qc-plot-overlay-div overlay-outer-div   psm_count_vs_score_qc_plot_overlay_show_hide_parts_jq  overlay_show_hide_parts_jq" 
				style="display: none; "  >
			
				<div id="psm_count_vs_score_qc_plot_overlay_header" class="qc-plot-overlay-header" style="width:100%; " >
				
					<h1 id="psm_count_vs_score_qc_plot_overlay_X_for_exit_overlay" 
						class="qc-plot-overlay-X-for-exit-overlay  psm_count_vs_score_qc_plot_overlay_close_parts_jq" 
						>X</h1>
						
					<h1 id="psm_count_vs_score_qc_plot_overlay_header_text" class="qc-plot-overlay-header-text" 
						>QC Plot: PSM <span id="psm_count_vs_score_qc_plot_overlay_header_text_count_type" ></span> Counts</h1>
				</div>
				<div id="psm_count_vs_score_qc_plot_overlay_body" class="qc-plot-overlay-body" >
			
			
				<div >
				
					<input type="hidden" id="psm_count_vs_score_qc_plot_current_search_id" >
					
					<input type="hidden" id="psm_count_vs_score_qc_plot_current_search_name_and_id" >
		
					
					<table style="border-width:0px;">
						
						
						<tr>
							<td>Choose score:</td>
							<td>
								<select id="psm_count_vs_score_qc_plot_score_type_id" >
									
								</select>							
							

							</td>
						</tr>
						<tr>

							<td>View as:</td>
							<td>
							 
							  <label >
								<input type="radio"
									id="psm_count_vs_score_qc_plot_y_axis_as_percentage"
									name="psm_count_vs_score_qc_plot_y_axis_choice"
									class=" psm_count_vs_score_qc_plot_on_change_jq"
									value="" >
									percentage
							  </label>
							  
							  <label >
								<input type="radio"
									id="psm_count_vs_score_qc_plot_y_axis_as_raw_counts"
									name="psm_count_vs_score_qc_plot_y_axis_choice"
									class=" psm_count_vs_score_qc_plot_on_change_jq"
									checked="checked"
									value="" >
									raw counts
							  </label>
							 
							<td>
						</tr>
						
						<tr>
							<td>PSMs with:</td>
							<td>
							  <label >
								<input type="checkbox" class="psm_count_vs_score_qc_plot_link_type_include_jq psm_count_vs_score_qc_plot_on_change_jq"
									checked="checked"
									value="<%=QCPlotConstants.PSM_COUNT_VS_SCORE_PLOT__CROSSLINK_PSM%>" >
								crosslinks
							  </label>
							  <label >
								<input type="checkbox" class="psm_count_vs_score_qc_plot_link_type_include_jq psm_count_vs_score_qc_plot_on_change_jq"
									checked="checked"
									value="<%=QCPlotConstants.PSM_COUNT_VS_SCORE_PLOT__LOOPLINK_PSM%>" >
								looplinks
							  </label> 
							  
							  <label >
								<input type="checkbox" class="psm_count_vs_score_qc_plot_link_type_include_jq psm_count_vs_score_qc_plot_on_change_jq"
									checked="checked"
									value="<%=QCPlotConstants.PSM_COUNT_VS_SCORE_PLOT__UNLINKED_PSM%>" >
								unlinked
							  </label>
							  <label >
								<input type="checkbox" class="psm_count_vs_score_qc_plot_link_type_include_jq psm_count_vs_score_qc_plot_on_change_jq"
									value="<%=QCPlotConstants.PSM_COUNT_VS_SCORE_PLOT__ALL_PSM%>" >
								all
							  </label> 							  
<%-- 							  
							  <label >
								<input type="checkbox" class="psm_count_vs_score_qc_plot_link_type_include_jq psm_count_vs_score_qc_plot_on_change_jq"
									value="<%=QCPlotConstants.Q_VALUE_PSM_COUNT_PLOT__MONOLINK_PSM%>" >
								monolinks
							  </label>
							  <label >
								<input type="checkbox" class="psm_count_vs_score_qc_plot_link_type_include_jq psm_count_vs_score_qc_plot_on_change_jq"
									value="<%=QCPlotConstants.Q_VALUE_PSM_COUNT_PLOT__NO_LINK_PSM%>" >
								no&nbsp;links
							  </label>
--%>
							</td>
						</tr>
			
												
						<tr>
							<td>Max: </td>
							<td>
								X:
								<input type="text" id="psm_count_vs_score_qc_plot_max_x" 
									class="psm_count_vs_score_qc_plot_on_change_jq" 
									size="8"> 
								Y:
								<input type="text" id="psm_count_vs_score_qc_plot_max_y" 
									class="psm_count_vs_score_qc_plot_on_change_jq"
									size="8">
				
								<input type="button" id="psm_count_vs_score_qc_plot_max_reset_button" value="Reset">
							</td>
						</tr>	
														
					</table>

					
					
					<a style="font-size:10pt;white-space:nowrap;" href="javascript:" id="psm_count_vs_score_qc_plot_download_svg"
							title="Download a SVG file of the image">[Download SVG]</a>
					
					
					<div style="clear: both; " >
					
					
					
					</div>
					
					
					<h1 class="psm_count_vs_score_qc_plot_no_data_jq" 
						style="display: none;  ">
					
						No Data
					</h1>
					
					
					<h1 class="psm_count_vs_score_qc_plot_param_not_a_number_jq" 
						style="display: none;  ">
					
						Max X or Max Y is not empty and is not a number
					</h1>
					
					
					<%-- overflow: hidden; and left: -50px; to clip off padding from left side of chart --%>
					
<%-- 				commented out since on redraw shifts further left and hides vertical axis label
 	
					<div style="position: relative; width: 920px; height: 650px; overflow: hidden;" >
						<div style="position: absolute; top: 0px; left: -50px;">
--%>						
					<div class=" psm_count_vs_score_qc_plot_have_data_jq ">
					
							<div id="psm_count_vs_score_qc_plot_chartDiv"
								style="width: 920px; height: 650px;"  
									<%-- style="width: 1050px; height: 800px;"   
										Keep this width and height in sync with the chart create config in the 
													JS as RETENTION_TIME_COUNT_CHART_WIDTH and RETENTION_TIME_COUNT_CHART_HEIGHT  --%>
								></div>
												
					</div>
<%-- 					
						</div>
					</div>
--%>
					
									
				</div>
				
			</div>
		   
		   </div>
	
		  </div>
		</div>
		
		
		<!-- END:   Modal dialog for displaying the PSM Q Value Counts QC plot -->
								
		
		
		<!--  END:    Overlays for displaying QC Quality Control Plots/Charts   -->
	
		<!-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! -->
	
	
	
	
	
		<%--  If admin section rendered, include the Javascript for it --%>
		<c:if test="${authAccessLevel.assistantProjectOwnerAllowed or authAccessLevel.assistantProjectOwnerIfProjectNotLockedAllowed}" >
	
			<%-- --%>	
	
	
	
			<!-- Handlebars templating library   -->
			
			<%--  
			<script type="text/javascript" src="${ contextPath }/js/libs/handlebars-v2.0.0.js"></script>
			--%>
			
			<!-- use minimized version  -->
			<script type="text/javascript" src="${ contextPath }/js/libs/handlebars-v2.0.0.min.js"></script>
			
	
			<script type="text/javascript" src="${ contextPath }/js/viewProject_ProjectAdminSection.js"></script>

		</c:if> 
		

		<%--  If project owner, include the Javascript for Project Lock admin --%>
		<c:if test="${authAccessLevel.projectOwnerAllowed or authAccessLevel.projectOwnerIfProjectNotLockedAllowed}" >
			
			<script type="text/javascript" src="${ contextPath }/js/viewProject_ProjectLockAdmin.js"></script>
		
		</c:if>

	
	</div>
	
	
	
	
	
	

<%-- Google Chart API import, for use on experimentDetails.jsp  --%>
<script type="text/javascript" src="https://www.google.com/jsapi"></script>
<script type="text/javascript">
  google.load("visualization", "1", {packages:["corechart"]});
  
  var googleOnLoadCallbackFunction = function() {
	  

		
	   setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else
		  
			  initQCCharts();
			  
			  initQCChartPSMCountVsScore();
	   },10);
	  
  };
  
  //  Do NOT call a method on an object here.  The "this" gets set to the window.
  google.setOnLoadCallback(googleOnLoadCallbackFunction);
</script>		

	
			<script type="text/javascript" src="${ contextPath }/js/qcChartsViewProjectPage.js"></script>
			<script type="text/javascript" src="${ contextPath }/js/qcChartPSMCountsVsScore.js"></script>
	

<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>