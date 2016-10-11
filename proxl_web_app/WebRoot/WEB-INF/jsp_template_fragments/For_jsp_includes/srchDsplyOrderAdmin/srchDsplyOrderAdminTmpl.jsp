
		<%-- !!!   Handlebars template   !!!!!!!!!   --%>
		
<%--  srchDsplyOrderAdminTmpl.jsp  --%>


<div class="outer-item-container  search_display_order_item_jq" 
		  	 data-search_id="{{id}}">

  <div class=" sort-handle-and-text protein_select_protein_item_and_sort_handle_block_jq " 
   		data-position_index="{{positionIndex}}"  data-protein_id="{{proteinId}}"
   		 >
	 <div class="sort-handle-float">
		<span class=" sort_handle_jq  tool_tip_attached_jq" 
			data-tooltip='<div>Drag to re-order</div>'
			style=""
		  ><img src="${ contextPath }/images/icon-draggable-small.png" 
		  ></span>
	 </div >
     <div class="search-name" >
	  {{name}}
     </div>
     <div style="clear: both;"></div>
  </div>
  <div class="search-entry-bottom-border"></div>
</div>   		  