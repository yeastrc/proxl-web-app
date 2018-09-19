

<%--  projectOrganizeSearches.jsp  --%>

<%--  Organize searches block, on project page --%>


  <div id="organize_searches_data_block" style="display: none;">

	<div id="organize_searches_loading_message" style="color: green;">
		Loading Data
	</div>

	<div id="organize_searches_re_loading_page_message" style="color: green; display: none;">
		Re-loading page
	</div>	
	
	<div id="organize_searches_main_data_block">	
	 <div >
	   <input id="organize_searches_done_organizing_button" value="Done Organizing Searches" type="button" class="submit-button  " >
	 </div>
	 
	 <div >
	   <ul>
	 	<li>Drag and drop folders and searches to rearrange order.</li> 
	 	<li>Drag searches onto folders to place searches in that folder.</li> 
	 	<li>Click on a folder to view searches in that folder.</li>
	   </ul>
	 </div>
	 
	 <div style="margin-top: 12px;">
	  <table  id="organize_searches_folder_searches_table" >
	   <tr>
	    <td  id="organize_searches_folder_total_block_container" style=" width: 400px; min-width: 400px; max-width: 400px;" >
	     
	     <div id="organize_searches_folder_total_block">
		     
		     <div id="organize_searches_folder_total_block_drag_handle"
		     	style="cursor: move; margin-bottom: 15px; font-weight: bold; font-size: 16px;"
		     	class="tool_tip_attached_jq" data-tooltip="Drag to move Folder List" 
		     	>
		     	<img src="${ contextPath }/images/icon-draggable-small.png" >
		     	Folder List
		     </div>
		     <div class=" outer-item-container clickable folder_display_order_item_jq tool_tip_attached_jq " 
		     		id="organize_searches_folder_searches_not_in_any_folder_outer_item"
		     		data-tooltip="Click to view Unfiled Searches" 
		     		data-searches_not_in_any_folder="true" 
		     		>

				 <div class="success-message-container search_moved_to_folder_msg_jq " style="text-align: left; width: 300px;"
						 >
					<div class="success-message-inner-container" >
						<div class="success-message-close-x " onclick="organizeSearches.hideMovedSearchToFolderMessage( this )">X</div>
						<div class="success-message-text" >Search moved to Unfiled</div>
					</div>
				 </div>
	 		       <div class=" folder_display_order_inner_item_jq "  
		       		id="organize_searches_folder_searches_not_in_any_folder_inner_item" >
			     <div id="organize_searches_folder_searches_not_in_any_folder" 
			     	data-searches_not_in_any_folder="true"
			     	>Unfiled Searches</div>
		       </div>
		     </div>
			 <div class="search-entry-bottom-border"></div>
			 
			 <div id="organize_searches_folder_entries_block" >	
			 </div>
			 
			 <div style="margin-top: 10px;">
				 <div id="organize_searches_folder_new_folder_block" >
				   <input id="organize_searches_new_folder_button" 
				   	value="New Folder" type="button" class="submit-button  " >
				 </div>
				 <div id="organize_searches_folder_add_new_folder_block" style="display: none;">
				 	<input type="text" id="organize_searches_folder_new_folder_name"
				 		maxlength="400" 
				 		style="width: 70%;">
				   <input id="organize_searches_add_new_folder_button" 
				   	value="Add Folder" type="button" class="submit-button  " >
				 </div>
			 </div>
		 </div>
	    </td>
	    <td style="">
	     <div style="margin-bottom: 15px; font-weight: bold; font-size: 16px;">
	     	Search List
	     </div>
		 <div id="organize_searches_search_entries_block">	
		 </div>
		 
	    </td>
	   </tr>
	  </table>
	 </div>
	 
	</div>
	
	<%--  Single Folder Entry HTML Template --%>
	<script id="organize_searches_single_folder_template"  type="text/x-handlebars-template">
	 <%--  include the template text  --%>
	 <%@ include file="/WEB-INF/jsp_template_fragments/For_jsp_includes/srchDsplyOrderAdmin/srchDsplyOrdrFoldrAdminTmpl.jsp" %>
	</script>

	<%--  Single Folder Tooltip Entry HTML Template --%>
	<script id="organize_searches_single_folder_tooltip_template"  type="text/x-handlebars-template">
	 <%--  include the template text  --%>
	 <%@ include file="/WEB-INF/jsp_template_fragments/For_jsp_includes/srchDsplyOrderAdmin/srchDsplyOrdrFoldrTooltipAdminTmpl.jsp" %>
	</script>
				
	<%--  Single Search Entry HTML Template --%>
	<script id="organize_searches_single_search_template"  type="text/x-handlebars-template">
	 <%--  include the template text  --%>
	 <%@ include file="/WEB-INF/jsp_template_fragments/For_jsp_includes/srchDsplyOrderAdmin/srchDsplyOrdrSrchAdminTmpl.jsp" %>
	</script>
	
	<%--  Tooltip for Single Search Entry HTML Template --%>
	<script id="organize_searches_single_search_tooltip_template"  type="text/x-handlebars-template">
	 <%--  include the template text  --%>
	 <%@ include file="/WEB-INF/jsp_template_fragments/For_jsp_includes/srchDsplyOrderAdmin/srchTooltipDsplyOrdrSrchAdminTmpl.jsp" %>
	</script>
	
  </div> <%-- Organize searches block   <div id="explore_data_organize_searches_data_block">  --%>
  