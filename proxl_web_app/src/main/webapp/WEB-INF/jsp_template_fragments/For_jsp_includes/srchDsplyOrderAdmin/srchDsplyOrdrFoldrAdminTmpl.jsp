
		<%-- !!!   Handlebars template   !!!!!!!!!   --%>
		
<%--  srchDsplyOrdrFoldrAdminTmpl.jsp 

  Display order of folders and searches

  Single Folder entry

Containing element: <script id="organize_searches_single_folder_template"  type="text/x-handlebars-template">
 --%>

<div class="outer-item-container  folder_display_order_item_jq folder_root_jq  "
	data-in_organize_overlay="true" 
	data-folder_id="{{id}}">

	 <div class="success-message-container search_moved_to_folder_msg_jq " style="text-align: left; width: 300px;"
			 >
		<div class="success-message-inner-container" >
			<div class="success-message-close-x " onclick="organizeSearches.hideMovedSearchToFolderMessage( this )">X</div>
			<div class="success-message-text" >Search moved to Folder</div>
		</div>
	 </div>
	 
  <div class=" sort-handle-and-text folder_display_order_inner_item_jq " >
	  
	 <div class="sort-handle-float">
		<span class=" sort_handle_jq  tool_tip_attached_jq" 
			data-tooltip='<div>Drag to re-order</div>'
			style=""
		  ><img src="${ contextPath }/images/icon-draggable-small.png" class="folder-row-icon"
		  ></span>
	 </div >
     <div class="item-display-name folder_display_name_jq folder_name_jq " 
	  	>{{name}}</div>
     <div class="edit-delete-icons">
		<a href="javascript:" class="folder_rename_button_jq tool_tip_attached_jq" 
			data-tooltip="Edit name of folder"
			><img  src="${ contextPath }/images/icon-edit-small.png" class="folder-row-icon"></a>
		<a href="javascript:" class="folder_delete_button_jq tool_tip_attached_jq" 
			data-tooltip="Delete folder.  Searches in it will become 'Unfiled'."
			><img  src="${ contextPath }/images/icon-delete-small.png" class="folder-row-icon"></a>
	 </div> 
     <div style="clear: both;"></div>
  </div>
  <div class="search-entry-bottom-border"></div>
</div>   		  


<%--
	 <div class="sort-handle-float">
		<span class=" sort_handle_jq  tool_tip_attached_jq" 
			data-tooltip='<div>Drag to re-order</div>'
			style=""
		  ><img src="${ contextPath }/images/icon-draggable-small.png" class="folder-row-icon"
		  ></span>
	 </div >
     <div class="item-display-name folder_display_name_jq folder_name_jq " 
	  	>{{name}}</div>
     <div class="edit-delete-icons-float">
		<a href="javascript:" class="folder_rename_button_jq tool_tip_attached_jq" 
			data-tooltip="Edit name of folder"
			><img  src="${ contextPath }/images/icon-edit-small.png" class="folder-row-icon"></a>
		<a href="javascript:" class="folder_delete_button_jq tool_tip_attached_jq" 
			data-tooltip="Delete folder"
			><img  src="${ contextPath }/images/icon-delete-small.png" class="folder-row-icon"></a>
	 </div> 
     <div style="clear: both;"></div>
--%>