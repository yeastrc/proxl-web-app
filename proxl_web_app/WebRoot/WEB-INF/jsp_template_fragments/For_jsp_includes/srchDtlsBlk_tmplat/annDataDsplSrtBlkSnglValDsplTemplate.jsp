

		<%-- !!!   Handlebars template   !!!!!!!!!   --%>
		
		
	
	<%--  Annotation Display Sort Block Single Value Display Template --%>



<%-- 
containing div id:  "annotation_data_display_sort_block_single_value_display_template"
--%>
		

	<div class="annotation_data_single_sort_entry_jq  annotation-data-single-value-display-block  " 
		 style="" <%--   cursor: pointer; --%> 
		 data-annotation_type_id="{{ data.annotationTypeId }}"
		 data-ann_name="{{data.name}}"
		 data-ann_description="{{ data.description }}"
				>
		<div class=" sort-handle-and-text " >
			<div class=" sort-handle-float " > <%-- style="float: left;padding-top: 1px; padding-right: 3px;"  --%>
				<img src="${ contextPath }/images/icon-draggable-small.png" 
					style="background-color: white; "
					class=" drag_handle_jq tool_tip_attached_jq " <%-- drag_handle_jq for jQuery .sortable --%>
					data-tooltip="Drag to change order of displayed data"
					>
			</div>
			<div class="delete-icon-float">
			  <input type="image" class=" tool_tip_attached_jq" data-tooltip="Remove item from display"
			  	onclick="annotationDataDisplayProcessingCommonCode.removeFromSelectedAnnotationTypeList( { clickedThis : this } )" 
			  	 src="${ contextPath }/images/icon-delete-small.png" >
		    </div>
		   	<div class="text-sortable {{#if data.description }} tool_tip_attached_jq {{/if}} "  
				{{#if data.description }} data-tooltip="{{ data.description }}" {{/if}}
		   	>
				{{data.name}}
		   	</div>		
		 </div>
	</div>		

			