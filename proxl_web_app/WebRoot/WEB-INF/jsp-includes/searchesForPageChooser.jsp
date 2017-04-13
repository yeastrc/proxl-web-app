<%@page import="org.yeastrc.xlink.www.constants.StrutsActionPathsConstants"%>

<%--   searchesForPageChooser.jsp  --%>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

		<%--  Searches For Page Chooser Overlay,   Overlay for user choosing which searches to merge --%>
			
		<script type="text/text" id="searches_for_page_chooser_modal_dialog_overlay_image_uri_path"><%= StrutsActionPathsConstants.IMAGE_PAGE_ACTION %></script> 		
		<script type="text/text" id="searches_for_page_chooser_modal_dialog_overlay_structure_uri_path"><%= StrutsActionPathsConstants.STRUCTURE_PAGE_ACTION %></script> 		
		
<%--   Searches For Page Chooser Overlay Background --%>


<div id="searches_for_page_chooser_modal_dialog_overlay_background" 
	class="searches-for-page-chooser-modal-dialog-overlay-background searches_for_page_chooser_modal_dialog_overlay_display_parts_jq " style="display: none;"  >

</div>


<%--  Searches For Page Chooser Overlay Div --%>

<div id="searches_for_page_chooser_overlay_div" 
	class=" searches-for-page-chooser-overlay-div  searches_for_page_chooser_modal_dialog_overlay_display_parts_jq " style="display: none; "  >

	<div id="searches_for_page_chooser_overlay_header" class="searches-for-page-chooser-overlay-header" style="width:100%; " >
		<h1 id="searches_for_page_chooser_overlay_X_for_exit_overlay" 
			class="searches-for-page-chooser-overlay-X-for-exit-overlay searches_for_page_chooser_modal_dialog_overlay_hide_parts_jq" >X</h1>
		<h1 id="searches_for_page_chooser_overlay_header_text" class="searches-for-page-chooser-overlay-header-text" 
			>Choose the searches to display</h1>
	</div>
	<div id="searches_for_page_chooser_overlay_body" class="searches-for-page-chooser-overlay-body" >

	  <div id="searches_for_page_chooser_list_outer_container" class="searches-list-outer-container">
		<div id="searches_for_page_chooser_list_container" class=" searches-list-container">
		</div>
	  </div>
	  	
		<div id="searches_for_page_chooser_overlay_change_button_container"
			style=" margin-top: 10px;" >
			<div style="display:inline-block;position:relative;"> <%-- outer div to support overlay div when button disabled --%>
				<input type="button" value="Change"  id="searches_for_page_chooser_overlay_change_button" 
					 class="tool_tip_attached_jq" data-tooltip="Change Searches Merged" />
					<%-- overlay div to provide tooltip for button --%>
				<div id="searches_for_page_chooser_overlay_change_button_disabled_cover_div" 
					class=" " 
					style="position:absolute;left:0;right:0;top:0;bottom:0; display: none;" 
					data-tooltip_get_in_init="Click on # or more searches above and click here to view updated page." ></div>
			</div>
			<input type="button" value="Cancel" 
				class=" searches_for_page_chooser_modal_dialog_overlay_hide_parts_jq " />
		</div> 
		
	</div> <%--  END  <div id="searches_for_page_chooser_overlay_body"  --%>
	
</div>  <%--  END  <div id="searches_for_page_chooser_overlay_div"  --%>



<%-- !!!   Handlebars template Searches For Page Chooser  !!!!!!!!!   --%>

<%--  Protein Item to put in Overlay --%>		
<script id="searches_for_page_chooser_overlay_search_entry_template"  type="text/x-handlebars-template">
  <div class=" search_select_jq single-search-select-item " data-project_search_id="{{ id }}" >
	{{ name }} ({{ searchId }})
  </div>
</script>

<%-- !!  Plain html template --%>
<script id="searches_for_page_chooser_overlay_project_search_id_input_template"  type="text/text">
	<input type="hidden" name="projectSearchId" value="#" class=" project_search_id_in_update_form_jq " />
</script>

		<%--  Keep at end since requires everything on the DOM first --%>
		<script type="text/javascript" src="${ contextPath }/js/searchesForPageChooser.js?x=${cacheBustValue}"></script>

