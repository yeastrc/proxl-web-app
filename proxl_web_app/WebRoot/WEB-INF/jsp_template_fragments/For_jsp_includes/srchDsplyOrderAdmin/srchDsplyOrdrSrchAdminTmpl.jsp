
		<%-- !!!   Handlebars template   !!!!!!!!!   --%>
		
<%--  srchDsplyOrdrSrchAdminTmpl.jsp 

  Display order of folders and searches

  Single Search entry

Containing element: <script id="organize_searches_single_search_template"  type="text/x-handlebars-template">
 --%>


<div class="outer-item-container  search_display_order_item_jq" 
		  	 data-project_search_id="{{id}}">

  <div class=" sort-handle-and-text " > 
	 <div class="sort-handle-float">
		<span class=" sort_handle_jq  tool_tip_attached_jq" 
			data-tooltip='<div>Drag to re-order</div>'
			style=""
		  ><img src="${ contextPath }/images/icon-draggable-small.png" 
		  ></span>
	 </div >
     <div class="item-display-name search_display_name_jq " 
	  	>{{name}} ({{ searchId }})</div>
     <div style="clear: both;"></div>
  </div>
  <div class="search-entry-bottom-border"></div>
</div>   		  