<%@page import="org.yeastrc.xlink.www.constants.StrutsActionPathsConstants"%>

<%--   searchesChangeDisplayOrder.jsp  --%>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

		<%--  Searches Change Display Order Overlay,   Overlay for user changes order searches are displayed --%>
			
<%--   Searches Change Display Order Overlay Background --%>

<div id="searches_change_display_order_modal_dialog_overlay_background" 
	class="searches-change-display-order-modal-dialog-overlay-background searches_change_display_order_modal_dialog_overlay_display_parts_jq " style="display: none;"  >

</div>


<%--  Searches Change Display Order Overlay Div --%>

<div id="searches_change_display_order_overlay_div" 
	class=" searches-change-display-order-overlay-div  searches_change_display_order_modal_dialog_overlay_display_parts_jq " style="display: none; "  >

	<div id="searches_change_display_order_overlay_header" class="searches-change-display-order-overlay-header" style="width:100%; " >
		<h1 id="searches_change_display_order_overlay_X_for_exit_overlay" 
			class="searches-change-display-order-overlay-X-for-exit-overlay searches_change_display_order_modal_dialog_overlay_hide_parts_jq" >X</h1>
		<h1 id="searches_change_display_order_overlay_header_text" class="searches-change-display-order-overlay-header-text" 
			>Change the display order of the searches</h1>
	</div>
	<div id="searches_change_display_order_overlay_body" class="searches-change-display-order-overlay-body" >

	  <div id="searches_change_display_order_list_outer_container" class="searches-list-outer-container">
		<div id="searches_change_display_order_list_container" class=" searches-list-container">
		</div>
	  </div>
	  	
		<div id="searches_change_display_order_overlay_change_button_container"
			style=" margin-top: 10px;" >
			<div style="display:inline-block;position:relative;"> <%-- outer div to support overlay div when button disabled --%>
				<input type="button" value="Change"  id="searches_change_display_order_overlay_change_button" 
					 class="tool_tip_attached_jq" data-tooltip="Change Searches Merged" />
			</div>
			<input type="button" value="Cancel" 
				class=" searches_change_display_order_modal_dialog_overlay_hide_parts_jq " />
		</div> 
		
	</div> <%--  END  <div id="searches_change_display_order_overlay_body"  --%>
	
</div>  <%--  END  <div id="searches_change_display_order_overlay_div"  --%>



<%-- !!!   Handlebars template Searches Change Display Order  !!!!!!!!!   --%>

<%--  Search Item to put in Overlay --%>		
<script id="searches_change_display_order_overlay_search_entry_template"  type="text/x-handlebars-template">

<div class="outer-item-container  search_display_order_item_jq" 
		  	 data-project_search_id="{{ projectSearchId }}">

  <div class=" sort-handle-and-text " > 
	 <div class="sort-handle-float">
		<span class=" sort_handle_jq  tool_tip_attached_jq" 
			data-tooltip='<div>Drag to re-order</div>'
			style=""
		  ><img src="images/icon-draggable-small.png" 
		  ></span>
	 </div >
     <div class="item-display-name search_display_name_jq tool_tip_attached_jq" 
     	data-tooltip="Drag to re-order"
	  	>{{ searchNameAndSearchId }}</div>
     <div style="clear: both;"></div>
  </div>
  <div class="search-entry-bottom-border"></div>
</div>   		  
</script>
	

<%-- !!  Plain html template --%>
<script id="searches_change_display_order_overlay_project_search_id_input_template"  type="text/text">
	<input type="hidden" name="projectSearchId" value="#" class=" project_search_id_in_update_form_jq " />
</script>

<%--  Keep at end since requires everything on the DOM first --%>
<script type="text/javascript" >
	try {
		searchesChangeDisplayOrder.init();
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
</script>
