<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %><%-- Always put this directive at the very top of the page --%>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>

<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>


 <c:set var="pageTitle">Project List</c:set>

 <c:set var="pageBodyClass" >projects-list-page</c:set>

 <c:set var="headerAdditions">
 
	<script type="text/javascript" src="static/js_generated_bundles/data_pages/projectsListPage-bundle.js?x=${ cacheBustValue }"></script>

 </c:set>


<%@ include file="/WEB-INF/jsp-includes/header_main.jsp" %>


<div class="overall-enclosing-block">
	
	<div class="top-level-label your-projects-title" >Your Projects</div>	
	
	

		<%--  The list of projects will be put in this div by the Javascript --%>
	<table border="0" width="100%"  id="project_list">
		
		 <tr>
		 
		  <td valign="top" style="width: 1px;"> <!-- Width will expand to fully display the icon -->
			<a href="javascript:" class="new_project_expand_jq tool_tip_attached_jq" id="new_project_expand_link"
				title="New Project" data-tooltip="Add new project"><img src="images/icon-circle-plus.png"></a>
			<a href="javascript:"  class="new_project_cancel_jq tool_tip_attached_jq"  id="new_project_cancel_link"
				title="Close New Project" style="display: none;" data-tooltip="Cancel adding new project"><img src="images/icon-circle-x.png" ></a>
		  </td>
		  <td>

			<div  id="new_project_collapsed" >
			
				<div class="new-project-text" >
					<a href="javascript:" class="new-project-text-link new_project_expand_jq tool_tip_attached_jq" data-tooltip="Add new project">New Project</a>
				</div>
			</div>
			
			<div  id="new_project_expanded" style="display: none;position: relative;">
						
		  		<div class="error-message-container error_message_container_jq" id="error_message_project_title_required">
		  		
		  			<div class="error-message-inner-container" style="width: 300px;">
		  				<div class="error-message-close-x error_message_close_x_jq">X</div>
			  			<div class="error-message-text" >Project Title cannot be empty</div>
		  			</div>
			  	</div>
		  		<div class="error-message-container error_message_container_jq" id="error_message_project_abstract_required">
		  		
		  			<div class="error-message-inner-container" style="width: 300px;">
		  				<div class="error-message-close-x error_message_close_x_jq">X</div>
			  			<div class="error-message-text" >Project Abstract cannot be empty</div>
		  			</div>
			  	</div>
									
				<div class="new-project-text" >
					<div style="margin-bottom: 5px;"><input id="new_project_title" type="text" placeholder="Title" title="Title"></div>
					<div style="margin-bottom: 5px;"><textarea id="new_project_abstract" rows="10" cols="100" placeholder="Abstract" title="Abstract" ></textarea></div>
				</div>
				<input type="button" value="Add Project" id="add_project_button">
				<input type="button" value="Cancel" id="add_project_cancel_button" class="new_project_cancel_jq" >						   	

			
			</div> <%-- End of new project expanded --%>
	
		  </td>
		 </tr>
		 <tr>
		  <td colspan="2">
			<div class="new-project-container-bottom-border" ></div>
		  </td>
		 </tr>
		
		<%--  Project rows get appended to the end --%>
	
	</table>



		<%--  Template used by the Javascript for a single project entry --%>	

	<%-- This table is just a container and will not be placed into the final output --%>
	<table id="project_template" style="display: none;" >
		
	 <tr class="project_root_container_jq">	

		 <td style="text-align: center; padding-right: 3px;">
			<%-- 	"x" icon should only appear if they're admins for that project/have permission to delete it and the project isn't locked --%>
		 	{{#if canDelete}}
				<a class="delete_project_link_jq tool_tip_attached_jq" href="javascript:" title="Delete Project"
						project_id="{{project.id}}"
						data-tooltip="Delete project"><img src="images/icon-circle-x.png" ></a>
			{{/if}}
			{{#if project.projectLocked}}
				<img class="tool_tip_attached_jq" data-tooltip="Project is locked" src="images/icon-locked-small.png" >
			{{/if}}
		</td>
		<td>			
				<a class="project-text-link  project_title_jq tool_tip_attached_jq" data-tooltip="View project" 
					href="viewProject.do?<%= WebConstants.PARAMETER_PROJECT_ID %>={{project.id}}" 
					>{{project.title}}</a>
		</td>
	 </tr>
	 <tr  class="project_separator_row_jq">
	   <td colspan="2">	
		<div class="project-container-bottom-border" ></div>
	  </td>
	 </tr>
	  
	</table>
	

			<!--  Modal dialog for confirming deleting a search -->
		
				<!--  Div behind modal dialog div -->
		
			<div class="modal-dialog-overlay-background   mark_project_for_deletion_overlay_show_hide_parts_jq  mark_project_for_deletion_overlay_cancel_parts_jq  overlay_show_hide_parts_jq" 
				id="mark_project_for_deletion_overlay_background" ></div>
			
					<!--  Inline div for positioning modal dialog on page -->
			<div class="mark-project-for-deletion-overlay-containing-outermost-div " id="mark_project_for_deletion_overlay_containing_outermost_div_inline_div"  >
		
			  <div class="mark-project-for-deletion-overlay-containing-outer-div " >
			
		
					<!--  Div overlay for confirming removing a search -->
				<div class="modal-dialog-overlay-container mark-project-for-deletion-overlay-container   mark_project_for_deletion_overlay_show_hide_parts_jq  overlay_show_hide_parts_jq" 
					 id="mark_project_for_deletion_overlay_container" >
		
					<div class="top-level-label" style="margin-left: 0px;">Delete Project</div>
		
					<div class="top-level-label-bottom-border" ></div>
					
					<div >
					
						<div >Delete the project <span style="font-weight: bold;" id="mark_project_for_deletion_overlay_project_title"></span>?</div>
						
						<div style="margin-top: 10px">
							<input type="button" value="Yes" id="mark_project_for_deletion_confirm_button" >
							<input type="button" value="Cancel" class="mark_project_for_deletion_overlay_cancel_parts_jq" >
						</div>
							
					</div>
					
				</div>
			
			  </div>
			</div>
			
			
			<!-- END:   Modal dialog for confirming deleting a search -->
			
								
	
</div>

<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>
