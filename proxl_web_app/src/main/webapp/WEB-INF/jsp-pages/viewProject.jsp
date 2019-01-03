<%@page import="org.yeastrc.xlink.base.file_import_proxl_xml_scans.enum_classes.ProxlXMLFileImportStatus"%>
<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %>
<%@page import="org.yeastrc.xlink.www.constants.StrutsActionPathsConstants"%>
<%@page import="org.yeastrc.xlink.www.constants.WebConstants"%>
<%@page import="org.yeastrc.xlink.www.constants.AuthAccessLevelConstants"%>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>


<%--  viewProject.jsp   


 --%>


 <c:set var="pageTitle">project - <c:out value="${ project.title }" ></c:out></c:set>

 <c:set var="pageBodyClass" >project-page</c:set>

 <c:set var="helpURLExtensionForSpecificPage" >en/latest/using/project.html</c:set>

 <c:set var="headerAdditions">
 

		<link REL="stylesheet" TYPE="text/css" HREF="${ contextPath }/css/jquery-ui-1.10.2-Themes/base/jquery.ui.all.css">


		<script type="text/javascript" src="${ contextPath }/js/libs/jquery-ui-1.10.4.min.js"></script>
		<script type="text/javascript" src="${ contextPath }/js/libs/base64.js"></script> 
		<script type="text/javascript" src="${ contextPath }/js/libs/jquery.qtip.min.js"></script>
		<script type="text/javascript" src="${ contextPath }/js/libs/spin.min.js"></script> 
		


		<!-- Handlebars templating library   -->
		
		<%--  
		<script type="text/javascript" src="${ contextPath }/js/libs/handlebars-v2.0.0.js"></script>
		--%>
		
		<!-- use minimized version  -->
		<script type="text/javascript" src="${ contextPath }/js/libs/handlebars-v2.0.0.min.js"></script>
		
			
<%--  Moved to Front End Build Bundles
		
		<script type="text/javascript" src="${ contextPath }/js/viewProjectPage.js?x=${cacheBustValue}"></script> 

		<script type="text/javascript" src="${ contextPath }/js/handleServicesAJAXErrors.js?x=${cacheBustValue}"></script> 
		
		<script type="text/javascript" src="${ contextPath }/js/spinner.js"></script>
				
 --%>
 
 
	<%-- Choose which Javascript bundle to load, based on user type --%>
<c:choose>
  <c:when test="${ authAccessLevel.projectOwnerAllowed }">
  	<%--  Project Owner and Project is NOT locked --%>
	<script type="text/javascript" src="static/js_generated_bundles/data_pages/projectViewPage_ProjectOwner_W_User-bundle.js?x=${ cacheBustValue }"></script>
  </c:when>
  <c:when test="${ authAccessLevel.projectOwnerIfProjectNotLockedAllowed }">
  	<%--  Project Owner and Project IS locked --%>
	<script type="text/javascript" src="static/js_generated_bundles/data_pages/projectViewPage_ProjectLocked_ProjectOwner_W_User-bundle.js?x=${ cacheBustValue }"></script>
  </c:when>
  <c:when test="${ authAccessLevel.assistantProjectOwnerAllowed }">
  	<%--  Researcher (NOT Project Owner) and Project is NOT locked --%>
	<script type="text/javascript" src="static/js_generated_bundles/data_pages/projectViewPage_Researcher_W_User-bundle.js?x=${ cacheBustValue }"></script>
  </c:when>
  <c:when test="${ authAccessLevel.assistantProjectOwnerIfProjectNotLockedAllowed }">
  	<%--  Researcher (NOT Project Owner) and Project IS locked --%>
	<script type="text/javascript" src="static/js_generated_bundles/data_pages/projectViewPage_ProjectLocked_Researcher_W_User-bundle.js?x=${ cacheBustValue }"></script>
  </c:when>
  <c:otherwise>
  	<%--  Project is public and not signed in user --%>
	<script type="text/javascript" src="static/js_generated_bundles/data_pages/projectViewPage_PublicUser-bundle.js?x=${ cacheBustValue }"></script>
  </c:otherwise>

</c:choose>
 
 		
		<link type="text/css" rel="stylesheet" href="${ contextPath }/css/jquery.qtip.min.css" />
		
		
		<%--  Project Admin Javascript is added at the bottom of the page --%>

		<%--  Project Upload Proxl XML File to import Javascript is added at the bottom of the page --%>

</c:set>



<%@ include file="/WEB-INF/jsp-includes/header_main.jsp" %>


<div class="overall-enclosing-block">

		<%--  This is set in the filter so this can/should go on every page --%>
		<input type="hidden" id="logged_in_user_id" value="${ loggedInUserId }" >
	
	
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
							<input class="tool_tip_attached_jq" data-tooltip="Edit project title" 
								id="maint_title_init_button" type="image" src="${ contextPath }/images/icon-edit-small.png"  
								value="Update">
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

			  <div class="top-level-container " >
			
				<div  class="collapsable-link-container top-level-collapsable-link-container " style="">
					<a href="javascript:" id="researchers_in_project_block_hide" class="top-level-collapsable-link " style="display: none;"
						><img  src="${ contextPath }/images/icon-collapse.png"></a>
					<a href="javascript:"  id="researchers_in_project_block_show" class="top-level-collapsable-link " 
						><img  src="${ contextPath }/images/icon-expand.png"></a>
				
				</div>
			
				<div class="top-level-label">Researchers</div>

				<div class="top-level-label-bottom-border" ></div>
					
									
				<div  id="researchers_in_project_block" class="researchers-block " style="display: none;" > 


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
										  	<%-- Re-Invite email successfully sent message --%>
				<div style="position: relative;">
			  		<div class="success-message-container error_message_container_jq" id="success_message_invite_email_re_sent">
			  			<div class="success-message-inner-container"  style="width: 800px;">
			  				<div class="success-message-close-x error_message_close_x_jq">X</div>
				  			<div class="success-message-text" >Email re-sent inviting them to this project</div>
					  	</div>
				  	</div>	  
	
				<%-- Re-Invite email NOT successfully sent message --%>
			  		<div class="error-message-container error_message_container_jq" id="error_message_invite_email_re_send_sytem_error">
			  		
			  			<div class="error-message-inner-container" style="width: 400px;">
			  				<div class="error-message-close-x error_message_close_x_jq">X</div>
				  			<div class="error-message-text" >Unable to send email, system error.</div>
			  			</div>
				  	</div>
				</div>		
						
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
				 
			   <td nowrap class="invited-person-invite-date-block" >Invited on {{inviteDate}} </td> 

			   <td style="padding-left: 20px;" >
			   	<input type="button" value="Resend Email" class=" submit-button tool_tip_attached_jq invited_person_entry_resend_invite_email_button_jq " 
			   		data-tooltip="Resend Invite Email"  inviteId="{{inviteId}}">
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
				 <td></td> <%-- Invite has button in this <td> --%>
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
		
			<div id="public_access_data_top_level_container" class="top-level-container collapsable_container_jq" >
			
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
						(<span class="show_when_public_access_or_public_access_code_enabled_jq" 
							style="${show_when_public_access_code_enabled_div_style_display_control}"
							 >Enabled</span
							><span class="show_when_public_access_or_public_access_code_disabled_jq" 
								style="${show_when_public_access_code_disabled_div_style_display_control}" 
							 	>Disabled</span>)</div>

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



		<c:if test="${ configSystemValues.proxlXMLFileImportFullyConfigured }" >
		
		  <c:if test="${authAccessLevel.projectOwnerAllowed}" >
							
			<!--  Upload Data -->

			<div id="upload_data_top_level_container" class="top-level-container " >
			
				<div  class="collapsable-link-container top-level-collapsable-link-container " style="">
					<a href="javascript:" class="top-level-collapsable-link " style="display: none;"
						id="upload_data_collapse_hide_data"
						><img  src="${ contextPath }/images/icon-collapse.png"></a>
					<a href="javascript:" class="top-level-collapsable-link " 
						id="upload_data_expand_show_data"
						><img  src="${ contextPath }/images/icon-expand.png"></a>
				
				</div>
					
				<div class="top-level-label" >
					Upload Data
						<span id="upload_data_pending_block" >
						 (Pending <span id="upload_data_pending_number">${ proxlXMLFileImportTrackingPendingCount }</span>)</span>
				</div>
	
				<div class="top-level-label-bottom-border" ></div>

				<div class="upload-search-block " id="upload_data_main_collapsable_jq" style="display: none;">
															
				  <c:if test="${authAccessLevel.projectOwnerAllowed}" >

					<div style="margin-bottom: 10px;">
						<input type="button" value="Import Proxl XML File" 
							class=" open_proxl_file_upload_overlay_jq tool_tip_attached_jq "
							data-tooltip="Upload a Proxl XML file to this project" >
							
						<input type="button" value="Refresh" 
							id="upload_data_refresh_data"
							class="  tool_tip_attached_jq "
							data-tooltip="Refresh this list" >
					</div>


					<div style="margin-top:10px;margin-bottom:15px;">
						Data must be converted to proxl XML before upload.
						Please <a href="https://proxl-web-app.readthedocs.io/en/latest/using/upload_data.html" target="_docs">visit our upload help page</a>
						to find a converter for your data and to learn more.
					</div>					
					

				  </c:if>
				  
				  <%-- Pending and History --%>
				  
				  <div   id="upload_data_pending_and_history_items_block"> <%-- Keep div with this id, used in JS --%>
				  
				   <div   id="upload_data_pending_items_block"> <%-- Keep div with this id, used in JS --%>

					<div >
					  <span  style="font-size: 18px;">
						<a  id="upload_data_pending_items_show_link"
							class="tool_tip_attached_jq " 
							data-tooltip="Show pending" 
							style="display: none;" 
							><img src="${ contextPath }/images/icon-expand-small.png"
								style="cursor: pointer;"
							></a>

						<a id="upload_data_pending_items_hide_link"
							class="tool_tip_attached_jq " 
							data-tooltip="Hide pending" 
							style="" 
							><img src="${ contextPath }/images/icon-collapse-small.png"
								style="cursor: pointer;"
							></a>
														
							Pending
					  </span>

							
					</div>
					
					<div class="top-level-label-bottom-border"></div>
					
					<div  id="upload_data_pending_items_container" style="padding-bottom: 10px; padding-left: 17px;">
					
					  <div id="upload_data_pending_items_no_pending_text" >
					  	No uploads pending
					  </div>
					  
					  
					  <div  id="upload_data_pending_items_outer_container">
					   
					   <div style="margin-bottom: 5px;"  >
					  	 <a id="upload_data_pending_items_show_all_details_link" href="javascript:" class=" tool_tip_attached_jq "  
					  		data-tooltip="Show details for all items" 
					  		data-container_id="upload_data_pending_items_container" 
					  		>[Expand All]</a>
					   </div>
					   
					   <table id="upload_data_pending_items_table" style="width: 100%;">
					   
					   	<tr><td>LOADING</td></tr>
					   	
					   </table>
					 </div>
					</div>

				   </div>  <%-- END <div   id="upload_data_pending_items_block">  --%>

				   <div   id="upload_data_history_items_block"> <%-- Keep div with this id, used in JS --%>

					<div >
					  <span  style="font-size: 18px;">
						<a  id="upload_data_history_items_show_link"
							class="tool_tip_attached_jq " 
							data-tooltip="Show history" 
							><img src="${ contextPath }/images/icon-expand-small.png"
								style="cursor: pointer;"
							></a>

						<a id="upload_data_history_items_hide_link"
							class="tool_tip_attached_jq " 
							data-tooltip="Hide history" 
							style="display: none;" 
							><img src="${ contextPath }/images/icon-collapse-small.png"
								style="cursor: pointer;"
							></a>
								
								History						
					  </span>
							
					</div>				
					
					<div class="top-level-label-bottom-border"></div>	  
					   
					<div  id="upload_data_history_items_container" >

						<%--  Spacer for icon --%>
					  <div  id="upload_data_history_items_table_outer_container" style="padding-left: 17px;">
					   
					   <div style="margin-bottom: 5px;" id="upload_data_history_items_show_all_details_container" >
					  	 <a id="upload_data_history_items_show_all_details_link" href="javascript:"  class=" tool_tip_attached_jq "  
					  		data-tooltip="Show details for all items" 
					  		data-container_id="upload_data_history_items_container" 
					  		>[Expand All]</a>
					   </div>

					   <table id="upload_data_history_items_table" style="width: 100%;" >
					   
					   	<tr><td>LOADING</td></tr>
					   	
					   </table>

					   </div>					   
					</div>					  
					  			
				   </div>  <%-- END <div   id="upload_data_history_items_block">  --%>


										  
				  </div>  <%-- END Pending and History --%>
			
			  </div>	<%--  END  <div class="upload-search-block " id="upload_data_main_collapsable_jq" --%>
			  
			</div>  <%--  END   <div id="upload_data_top_level_container"  class="top-level-container " > --%>



			<%--  Proxl XML File Import Entry Template --%>
			<script id="proxl_xml_import_item_template"  type="text/x-handlebars-template">
				<%--  include the template text  --%>
				<%@ include file="/WEB-INF/jsp_template_fragments/For_jsp_pages/proxlXMLFileImportItem.jsp" %>
			</script>	
			
			<%--  Proxl XML File Import Entry Separator Template --%>
			<script id="proxl_xml_import_item_separator_template"  type="text/x-handlebars-template">
			 <tr>
			  <td colspan="{{ numCols }}" <%-- colspan so occupies all but first column --%>
					style="">

				<div class="search-entry-bottom-border"></div>

			  </td>
			 </tr>
			</script>
			
						
			<%--  File Import Row 'expanded' to show details --%>
			
			<script id="proxl_xml_import_item_row_details_template"  type="text/x-handlebars-template">

				<%--  include the template text  --%>
				<%@ include file="/WEB-INF/jsp_template_fragments/For_jsp_pages/proxlXMLFileImportItemDetails.jsp" %>

			</script>


			<!--  END  Upload Data -->
			
		  </c:if>
		  
		  <%--  Only project owner allowed to cancel queued or remove failed --%>
		  <c:if test="${authAccessLevel.projectOwnerAllowed}" >			
			
			<!-- Modal dialog Confirm remove upload item overlay -->
			
			<!--  Div behind modal dialog div -->
			
			<div class="modal-dialog-overlay-background   import_proxl_xml_file_confirm_remove_upload_overlay_show_hide_parts_jq import_proxl_xml_file_confirm_remove_upload_overlay_cancel_parts_jq  overlay_show_hide_parts_jq" 
				id="import_proxl_xml_file_confirm_remove_upload_overlay_background" ></div>
			
					<!--  Inline div for positioning modal dialog on page -->
			<div class="import-proxl-xml-file-confirm-remove-upload-overlay-containing-outermost-div " id="import_proxl_xml_file_confirm_remove_upload_overlay_containing_outermost_div_inline_div"  >
			
			  <div class="import-proxl-xml-file-confirm-remove-upload-overlay-containing-outer-div " >
			
			
					<!--  Div overlay for confirming canceling file import -->
				<div class="modal-dialog-overlay-container import-proxl-xml-file-confirm-remove-upload-overlay-container   import_proxl_xml_file_confirm_remove_upload_overlay_show_hide_parts_jq  overlay_show_hide_parts_jq" 
					 id="import_proxl_xml_file_confirm_remove_upload_overlay_container" >
			
					<div class="top-level-label" style="margin-left: 0px;">
						<span  class=" cancel_queued_item_jq cancel_re_queued_item_jq  any_item_jq ">
							Cancel Upload Request?
						</span>
						<span  class=" remove_failed_item_jq remove_completed_item_jq  any_item_jq ">
							Remove From History?
						</span>
					</div>
			
					<div class="top-level-label-bottom-border" ></div>
					
					<div >
					
						<div>
							<span  class=" cancel_queued_item_jq cancel_re_queued_item_jq any_item_jq ">
								Remove <span class=" filename_jq " ></span> 
								from upload queue?
							</span>
							<span  class=" remove_failed_item_jq remove_completed_item_jq any_item_jq ">
								Remove <span class=" filename_jq " ></span> 
								from upload history?
							</span>  
						</div>
						
						<div style="margin-top: 10px">
							<input type="button" value="Yes"  class=" cancel_queued_item_yes_button_jq any_item_jq " >
							<input type="button" value="Yes"  class=" cancel_re_queued_item_yes_button_jq any_item_jq " >
							<input type="button" value="Yes"  class=" remove_failed_item_yes_button_jq any_item_jq " >
							<input type="button" value="Yes"  class=" remove_completed_item_yes_button_jq any_item_jq " >
							<input type="button" value="Cancel" 
								class="import_proxl_xml_file_confirm_remove_upload_overlay_cancel_parts_jq" >
						</div>
							
					</div>
					
				</div>
			
			  </div>
			</div>
			
			<!-- END:  Modal dialog Confirm remove upload item overlay -->
		  
		  </c:if>

		</c:if>
							
	

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

	
				<c:if test="${authAccessLevel.projectOwnerAllowed and otherProjectsExistForUser}" >
			
					<%--  Modal Dialog block for copying searches to another project --%>
			
						<%--  Div behind modal dialog div --%>
			
					<div class="modal-dialog-overlay-background   copy_searches_overlay_show_hide_parts_jq copy_searches_overlay_cancel_parts_jq  overlay_show_hide_parts_jq" 
						id="copy-searches-overlay-background" ></div>
			
						<%--  Inline div for positioning modal dialog on page --%>
					<div class="copy-searches-overlay-containing-full-width-div " >
					
							<%--  Inline div for positioning modal dialog on page --%>
					  <div class="copy-searches-overlay-containing-relative-div " >
					
							<%--  Div overlay for moving searches --%>
					
						<div class="modal-dialog-overlay-container copy-searches-overlay-container   copy_searches_overlay_show_hide_parts_jq  overlay_show_hide_parts_jq" 
							 id="copy-searches-overlay-container" style="top: -30px;">

							<div class="top-level-label" style="margin-left: 0px;"
								><span class=" copy_searches_display_copy_part_jq " >Copy</span
								><span class=" copy_searches_display_move_part_jq " >Move</span> Search Data</div>
			
							<div class="top-level-label-bottom-border" ></div>
							
							<div id="copy-searches-overlay-select-project-block">
							
								<div >Click the title of the project to which you would like to 
									<span class=" copy_searches_display_copy_part_jq " >copy</span
									><span class=" copy_searches_display_move_part_jq " >move</span> 
									 the selected searches to.</div>
								
								<div class="copy-searches-overlay-project-list-block">
	
									<div class="top-level-label-bottom-border" ></div>
									
									<div id="copy_searches_other_project_list_loading_msg">
										Loading Projects
									</div>
									<div id="copy_searches_other_project_list_no_projects_msg">
										No other projects to 
										<span class=" copy_searches_display_copy_part_jq " >copy</span
										><span class=" copy_searches_display_move_part_jq " >move</span> 
										 to for selected searches.
										All searches are in all other projects.
									</div>
									<div id="copy_searches_other_project_list">
										<%-- The other projects to copy to will be inserted here --%>
									</div>
										
									<div style="float: right;">
										<input type="button" value="Cancel" class="copy_searches_overlay_cancel_parts_jq" >
									</div>
									
								</div>	
							</div>
							
							<%-- Other project Handlebars template --%>
							<div id="copy_searches_other_project_template" style="display: none;">
							  <div >
							  	<div >
									<a href="javascript:"  data-other_project_id="{{ projectId }}"
										class="copy_search_project_choice_jq" 
										>{{ projectTitle }}</a> 
								</div>
								<div class="top-level-label-bottom-border" ></div>
							  </div>
							</div>
							
							<div id="copy-searches-overlay-confirm-project-block"  class="copy_searches_overlay_confirmation_steps_jq" style="display: none;">
							
								<div style="margin-bottom: 10px;">
									<span class=" copy_searches_display_copy_part_jq ">Copy</span
									><span class=" copy_searches_display_move_part_jq " style="display: none;">Move</span>
										selected searches to "<span id="copy-searches-overlay-project-to-move-title"></span>"?
									
								</div>
								<div >
									<input type="button" value="Yes" id="copy_search_confirm_button" >
									
									<input type="button" value="Copy" 
										class=" copy_searches_display_copy_part_jq "
										id="copy_search_copy_searches_not_in_new_project_confirm_button" style="display: none;">
									
									<input type="button" value="Move" 
										class=" copy_searches_display_move_part_jq "
										id="copy_search_move_searches_not_in_new_project_confirm_button" style="display: none;">
										
									<input type="button" value="Cancel" class="copy_searches_overlay_cancel_parts_jq" >
								</div>							
								
								<div id="copy_searches_searches_in_new_project_outer_block" 
									style="display: none; margin-top: 10px;">
									<div style="margin-bottom: 10px;">
										The following searches are already in the destination project and will not be 
										<span class=" copy_searches_display_copy_part_jq ">copied</span
										><span class=" copy_searches_display_move_part_jq " style="display: none;">moved</span>:
									</div>
									<div id="copy_searches_searches_in_new_project_list_block" style="margin-left: 10px; ">
									</div>
								</div>
								<%-- Handlebars template --%>	
								<div id="copy_searches_searches_in_new_project_entry_template" style="display: none;">
									<div >
									  	<div >
											{{ searchNameDisplay }} 
										</div>
										<div class="top-level-label-bottom-border" ></div>
									</div>
								</div>
									
							</div>
							
							<div id="copy-searches-overlay-confirmation-project-block" class="copy_searches_overlay_confirmation_steps_jq" style="display: none;">

								<div >
									Searches copied successfully
								</div>
														
								<input type="button" value="Go to project the searches were copied to" id="show-project-searches-copied-to" >

								<input type="button" value="Return to Project" id="copy_searches_return_to_project" >

							</div>

							<div id="copy-searches-overlay-move-to-project-marked-for-deletion-block"  class="copy_searches_overlay_confirmation_steps_jq" style="display: none;">
							
								<div  style="margin-bottom: 10px;">
									Copy Searches Failed.  Project copying to is marked for deletion.  Please reload the page to get a current list of valid projects to copy to.
								</div>
								<div >
									<input type="button" value="Cancel" class="copy_searches_overlay_cancel_parts_jq" >
								</div>
							</div>
							
							<div id="copy-searches-overlay-move-to-project-disabled-block"  class="copy_searches_overlay_confirmation_steps_jq" style="display: none;">
							
								<div  style="margin-bottom: 10px;">
									Copy Searches Failed.  Project copying to is disabled.  Please reload the page to get a current list of valid projects to copy to.
								</div>
								<div >
									<input type="button" value="Cancel" class="copy_searches_overlay_cancel_parts_jq" >
								</div>
							</div>
							
							<div id="copy-searches-overlay-move-project-failed-block"  class="copy_searches_overlay_confirmation_steps_jq" style="display: none;">
							
								<div  style="margin-bottom: 10px;">
									Copy Searches Failed.  Please reload the page to get a current list of valid projects to copy to.  
									If you continue to get this error message, contact the administrator.
								</div>
								<div >
									<input type="button" value="Cancel" class="copy_searches_overlay_cancel_parts_jq" >
								</div>
							</div>
							
						</div>
					
					  </div>
					</div>
					
					
						
			
				</c:if>
				
				
				<%--   Start of overall block under "Explore Data" section --%>
									
			 <div id="explore_data_overall_block"
				  class="searches-block collapsable_jq">
															
				 <c:choose>
				  <c:when test="${noAccess}">
				    
				    	No access is allowed to any data on this website.
				    
				  </c:when>
				  <c:when test="${ projectPageFoldersSearches.noSearchesFound }">
				    
				    	<div  class="no-searches-in-project-text">
				    		No searches in this project.
				    	</div>
				    
				  </c:when>
				  <c:otherwise>		
				  
				  	<c:if test="${authAccessLevel.assistantProjectOwnerAllowed or authAccessLevel.assistantProjectOwnerIfProjectNotLockedAllowed }" >

					 <!--  Modal dialog for Rename a Folder -->
			
						<!--  Div behind modal dialog div -->
			
					 <div class="modal-dialog-overlay-background   rename_folder_overlay_show_hide_parts_jq rename_folder_overlay_cancel_parts_jq  overlay_show_hide_parts_jq" 
						id="rename_folder_overlay_background" ></div>
					
							<!--  Inline div for positioning modal dialog on page -->
					 <div class="rename-folder-overlay-containing-outermost-div " id="rename_folder_overlay_containing_outermost_div_inline_div"  >
					  <div class="rename-folder-overlay-containing-outer-div " >
							<!--  Div overlay for confirming removing a folder -->
						<div class="modal-dialog-overlay-container rename-folder-overlay-container   rename_folder_overlay_show_hide_parts_jq  overlay_show_hide_parts_jq" 
							 id="rename_folder_overlay_container" >
							<div class="top-level-label" style="margin-left: 0px;">Edit Name of Folder</div>
							<div class="top-level-label-bottom-border" ></div>
							<div >
								<div >Enter new name for the folder</div>
								<div style="margin-top: 5px;"
								  ><input type="text" id="rename_folder_overlay_folder_name"
								    maxlength="400"
									class="rename-folder-overlay-folder-name-input-field"></div>
								<div style="margin-top: 10px">
									<input type="button" value="Save" id="rename_folder_save_button" >
									<input type="button" value="Cancel" class="rename_folder_overlay_cancel_parts_jq" >
								</div>
							</div>
						</div>
					  </div>
					 </div>
					 <!-- END:   Modal dialog for confirming deleting a folder -->
							 
					 <!--  Modal dialog for confirming deleting a Folder -->
			
						<!--  Div behind modal dialog div -->
			
					 <div class="modal-dialog-overlay-background   delete_folder_overlay_show_hide_parts_jq delete_folder_overlay_cancel_parts_jq  overlay_show_hide_parts_jq" 
						id="delete_folder_overlay_background" ></div>
					
							<!--  Inline div for positioning modal dialog on page -->
					 <div class="delete-folder-overlay-containing-outermost-div " id="delete_folder_overlay_containing_outermost_div_inline_div"  >
					  <div class="delete-folder-overlay-containing-outer-div " >
							<!--  Div overlay for confirming removing a folder -->
						<div class="modal-dialog-overlay-container delete-folder-overlay-container   delete_folder_overlay_show_hide_parts_jq  overlay_show_hide_parts_jq" 
							 id="delete_folder_overlay_container" >
							<div class="top-level-label" style="margin-left: 0px;">Delete Folder</div>
							<div class="top-level-label-bottom-border" ></div>
							<div >
								<div >Delete the folder <span style="font-weight: bold;" id="delete_folder_overlay_folder_name"></span>?</div>
								<div style="margin-top: 10px">
									<input type="button" value="Yes" id="delete_folder_confirm_button" >
									<input type="button" value="Cancel" class="delete_folder_overlay_cancel_parts_jq" >
								</div>
							</div>
						</div>
					  </div>
					 </div>
					 <!-- END:   Modal dialog for confirming deleting a folder -->
		
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

				 		<%--  Main Data Block  Under "Explore Data" section  --%>

			       <div id="explore_data_main_data_block">   <%--   Displayed HTML Under "Explore Data" section  --%>
														  									
									  
				  	<c:if test="${authAccessLevel.assistantProjectOwnerAllowed or authAccessLevel.assistantProjectOwnerIfProjectNotLockedAllowed }" >
					  
					  <div style="margin-bottom:10px;">
						<input class="submit-button" type="button" value="Expand All" onClick="javascript:expandAll()">
						<input class="submit-button" type="button" value="Collapse All" onClick="javascript:collapseAll()">

				  	    <c:if test="${authAccessLevel.projectOwnerAllowed }" >
			
							<c:if test="${otherProjectsExistForUser}" >
								
								<div style="display:inline-block;position:relative;"> <%-- outer div to support overlay div when button disabled --%>
									<input class="submit-button tool_tip_attached_jq " type="button" id="copy_search_button"
											 data-tooltip="Click here to copy the selected searches to another project."
											value="Copy Searches" disabled>
										<%-- overlay div to provide tooltip for button --%>
									<div class=" tool_tip_attached_jq   " id="copy_search_button_cover_when_disabled" 
										style="position:absolute;left:0;right:0;top:0;bottom:0;" 
										data-tooltip="Select one or more searches below and click here to copy the selected searches to another project." ></div>
								</div>
								
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
							
							<c:if test="${authAccessLevel.projectOwnerAllowed }" >

								<input class="submit-button tool_tip_attached_jq " type="button" 
											id="organize_searches_button"
											data-tooltip="Click here to organize the searches. Put in folders and change the order."
											value="Organize Searches" >
							</c:if>
							
						</c:if>
				
					  </div>
					  
					</c:if>

							<%-- Display the searches --%>
					<div>
					  <c:if test="${ not projectPageFoldersSearches.noSearchesFound }">

					    <%-- Searches in Folders --%>
						<c:forEach var="folder" items="${ projectPageFoldersSearches.folders }">
			
						  <div class=" folder-container folder_root_jq collapsable_container_jq"
						  	data-folder_id="${ folder.id }"
						   >
						
							<div  class="collapsable-link-container folder-collapsable-link-container collapsable_link_container_jq" style="">
							
								<a href="javascript:" 
									class=" folder_hide_contents_link_jq collapsable_collapse_link_jq tool_tip_attached_jq " 
									style="display: none;"
									data-tooltip="Close folder"
									><img  src="${ contextPath }/images/icon-folder-click-to-close.png"></a>
								<a href="javascript:" 
									class=" folder_show_contents_link_jq collapsable_expand_link_jq tool_tip_attached_jq "
									data-tooltip="Open folder"
									><img  src="${ contextPath }/images/icon-folder-click-to-open.png"></a>
	
							</div>
				
							<div >
								<span class=" folder-name-display folder_name_jq "
									><c:out value="${ folder.folderName }"></c:out></span>
									
								<c:if test="${ authAccessLevel.projectOwnerAllowed }" >
									<a href="javascript:" class="folder_rename_button_jq tool_tip_attached_jq" 
										data-tooltip="Edit name of folder"
										><img  src="${ contextPath }/images/icon-edit-small.png"></a>
									<a href="javascript:" class="folder_delete_button_jq tool_tip_attached_jq" 
										data-tooltip="Delete folder.  Searches in it become 'Unfiled'."
										><img  src="${ contextPath }/images/icon-delete-small.png"></a>
								</c:if>
							</div>

									
							<div class=" searches-under-folder-block collapsable_jq folder_contents_block_jq " 
									style="display: none;" >
							
								<c:forEach var="search_wrapper" items="${ folder.searches }" varStatus="search_wrapper_varStatus">
								  <c:choose>
									<c:when test="${ search_wrapper_varStatus.last }">
										<c:set var="SingleSearch_SkipBottomSeperator" value="${ true }" />
									</c:when>
									<c:otherwise>
										<c:set var="SingleSearch_SkipBottomSeperator" value="${ false }" />
									</c:otherwise>
								  </c:choose>
									
									<%-- Include for a displaying a single search --%>
									<%@ include file="/WEB-INF/jsp-includes/viewProject_SingleSearch.jsp" %>
									
								</c:forEach>
							</div>
							
							<div class="search-entry-bottom-border"></div>
							
						  </div>
						</c:forEach>
						
						<%--  Clear value after last folder loop --%>
						<c:set var="SingleSearch_SkipBottomSeperator" value="${ false }" />

<%-- 					    
					    <div >
					    	Searches Not in Any Folder
					    </div>
--%>					    
					    <%-- Searches Not in Any Folder --%>
						<c:forEach var="search_wrapper" items="${ projectPageFoldersSearches.searchesNotInFolders }">
						
							<%-- Include for a displaying a single search --%>
							<%@ include file="/WEB-INF/jsp-includes/viewProject_SingleSearch.jsp" %>
							
						</c:forEach>
					  </c:if>
					</div>
					
					<div style="display:inline-block;position:relative;"> <%-- outer div to support overlay div when button disabled --%>
						<input onClick="viewMergedQCPage()" class="merge-button submit-button" type="button" value="View Merged Stats/QC" disabled>
							<%-- overlay div to provide tooltip for button --%>
						<div class=" tool_tip_attached_jq   merge_button_disabled_cover_div_jq "
							style="position:absolute;left:0;right:0;top:0;bottom:0;" 
							data-tooltip="Click on two or more searches above and click here to view combined results." ></div>
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
					

					
				   </div> <%--   Main Data Block Under "Explore Data" section  <div id="explore_data_main_data_block">  --%>
						
				   <c:if test="${ authAccessLevel.projectOwnerAllowed }" >

						<%-- Organize searches Block, only for project owners  --%>
						<%@ include file="/WEB-INF/jsp-includes/projectOrganizeSearches.jsp" %>
				   </c:if>
										
				  </c:otherwise>
				 </c:choose>
					 					
			 </div>   <%--   END of overall block under "Explore Data" section  <div id="explore_data_overall_block"> --%>
								
			</div>
		</form>

		</div>

		<%--   Overlays for Proxl XML file uploading and status display --%>
	

		<c:if test="${ configSystemValues.proxlXMLFileImportFullyConfigured }" >
		
		  <c:if test="${authAccessLevel.projectOwnerAllowed}" >
		  <%--
		  <c:if test="${authAccessLevel.assistantProjectOwnerAllowed or authAccessLevel.assistantProjectOwnerIfProjectNotLockedAllowed }" >
		  --%>			

			<%--   Overlay for uploading Proxl XML file, and Javascript includes --%>

			<%@ include file="/WEB-INF/jsp-includes/proxl_XML_Upload_Overlay.jsp" %>

			<%--  Moved to Front End Build Bundles	
			<script type="text/javascript" src="${ contextPath }/js/proxlXMLFileImport.js?x=${cacheBustValue}"></script>
			--%>
		
			<%--  Separate JS for Project owner to cancel queued or remove failed import tracking item --%>
		
			<%--  Moved to Front End Build Bundles	
			<script type="text/javascript" src="${ contextPath }/js/proxlXMLFileImportUserUpdates.js?x=${cacheBustValue}"></script>
			--%>
		  </c:if>
		  
		  <%--  Overlay and JS for notifying user that one the Proxl XML Import items imported successfully --%>
		  
		  <c:if test="${authAccessLevel.projectOwnerAllowed }" >

			<input type="hidden" id="proxl_xml_file_upload_complete_successfully_status_id_queued"
				value="<%= ProxlXMLFileImportStatus.QUEUED.value() %>">

			<input type="hidden" id="proxl_xml_file_upload_complete_successfully_status_id_re_queued"
				value="<%= ProxlXMLFileImportStatus.RE_QUEUED.value() %>">

			<input type="hidden" id="proxl_xml_file_upload_complete_successfully_status_id_started"
				value="<%= ProxlXMLFileImportStatus.STARTED.value() %>">

			<input type="hidden" id="proxl_xml_file_upload_complete_successfully_status_id_complete"
				value="<%= ProxlXMLFileImportStatus.COMPLETE.value() %>">

			<input type="hidden" id="proxl_xml_file_upload_complete_successfully_status_id_failed"
				value="<%= ProxlXMLFileImportStatus.FAILED.value() %>">
																
			 <!--  Modal dialog for notifying user that one the Proxl XML Import items imported successfully -->
	
				<!--  Div behind modal dialog div -->
	
			 <div class="modal-dialog-overlay-background   proxl_xml_file_upload_complete_successfully_overlay_show_hide_parts_jq proxl_xml_file_upload_complete_successfully_overlay_cancel_parts_jq  overlay_show_hide_parts_jq" 
				id="proxl_xml_file_upload_complete_successfully_overlay_background" ></div>
			
					<!--  Inline div for positioning modal dialog on page -->
			 <div class="proxl-xml-file-upload-complete-successfully-overlay-containing-outermost-div " id="proxl_xml_file_upload_complete_successfully_overlay_containing_outermost_div_inline_div"  >
	
			  <div class="proxl-xml-file-upload-complete-successfully-overlay-containing-outer-div " >
	
					<!--  Div overlay for confirming removing a search -->
				<div class="modal-dialog-overlay-container proxl-xml-file-upload-complete-successfully-overlay-container   proxl_xml_file_upload_complete_successfully_overlay_show_hide_parts_jq  overlay_show_hide_parts_jq" 
					 id="proxl_xml_file_upload_complete_successfully_overlay_container" >
	
					<div class="top-level-label" style="margin-left: 0px;">Import Completed</div>
	
					<div class="top-level-label-bottom-border" ></div>
					
					<div >
					
						<div >An import has completed.  Refresh page to view the search.</div>
						
						<div style="margin-top: 10px">
							<input type="button" value="Refresh Page" id="proxl_xml_file_upload_complete_successfully_refresh_page_button" >
							<input type="button" value="Cancel" class="proxl_xml_file_upload_complete_successfully_overlay_cancel_parts_jq" >
						</div>
							
					</div>
					
				</div>
			
			  </div>
			 </div>
			
			
			 <!-- END:   Modal dialog for notifying user that the status has changed on one of the Proxl XML Import items -->
			
			<%--  Moved to Front End Build Bundles	
			<script type="text/javascript" src="${ contextPath }/js/proxlXMLFileImportStatusDisplay.js?x=${cacheBustValue}"></script>
			--%>
		
									
		  </c:if> <%--  END test="${ authAccessLevel.assistantProjectOwnerAllowed }"  --%>
		  
		</c:if> <%--  END test="${ configSystemValues.proxlXMLFileImportFullyConfigured }"  --%>
		
		
	
		<%--  If Not locked and user allowed to change search data, include the Javascript for it --%>
		<%--  Moved to Front End Build Bundles	
		<c:if test="${authAccessLevel.assistantProjectOwnerAllowed }" >
			<script type="text/javascript" src="${ contextPath }/js/viewProject_SearchMaint.js?x=${cacheBustValue}"></script>
		</c:if> 
		--%>
		<%--  If admin section rendered, include the Javascript for it --%>
		<%--  Moved to Front End Build Bundles	
		<c:if test="${authAccessLevel.assistantProjectOwnerAllowed or authAccessLevel.assistantProjectOwnerIfProjectNotLockedAllowed}" >
			<script type="text/javascript" src="${ contextPath }/js/viewProject_ProjectAdminSection.js?x=${cacheBustValue}"></script>
		</c:if> 
		--%>
		<%--  If project owner, include the Javascript for Project Search Order admin --%>
		<%--  Moved to Front End Build Bundles	
		<c:if test="${ authAccessLevel.projectOwnerAllowed }" >
			<script type="text/javascript" src="${ contextPath }/js/viewProject_OrganizeSearchesAndFoldersAdmin.js?x=${cacheBustValue}"></script>
		</c:if>		
		--%>
		<%--  If project owner, include the Javascript for Project Lock admin --%>
		<%--  Moved to Front End Build Bundles	
		<c:if test="${authAccessLevel.projectOwnerAllowed or authAccessLevel.projectOwnerIfProjectNotLockedAllowed}" >
			<script type="text/javascript" src="${ contextPath }/js/viewProject_ProjectLockAdmin.js?x=${cacheBustValue}"></script>
		</c:if>
		--%>
	
	</div>

<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>
