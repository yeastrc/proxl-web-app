
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

		<%-- !!!   Handlebars template   !!!!!!!!!   --%>
		
		
<%--   proxlXMLFileImportItem.jsp  --%>



<tr class=" proxl_xml_import_item_row_jq " 
  	 	data-tracking_id="{{ trackingId  }}"
  	 	data-status_id="{{ statusId }}"
  	 	data-filename="{{ uploadedFilename }} " >

  <%--  Only project owner allowed to cancel queued or remove failed --%>
  <c:if test="${authAccessLevel.projectOwnerAllowed}" >
  
	<td style="padding-right: 5px; vertical-align: top;">
  	  {{#if statusQueued }}
		<input type="image" src="${ contextPath }/images/icon-circle-x.png" 
			data-tooltip="Cancel upload request" 
			class="tool_tip_attached_jq  proxl_xml_import_item_cancel_queued_jq"/>
  	  {{/if}}
  	  {{#if statusReQueued }}
		<input type="image" src="${ contextPath }/images/icon-circle-x.png" 
			data-tooltip="Cancel upload request" 
			class="tool_tip_attached_jq  proxl_xml_import_item_cancel_re_queued_jq"/>
  	  {{/if}}
  	  
  	  {{#if statusFailed }}
		<input type="image" src="${ contextPath }/images/icon-circle-x.png" 
			data-tooltip="Remove from upload history" 
			class="tool_tip_attached_jq  proxl_xml_import_item_remove_failed_jq"/>
  	  {{/if}}

  	  {{#if statusComplete }}
		<input type="image" src="${ contextPath }/images/icon-circle-x.png" 
			data-tooltip="Remove from upload history" 
			class="tool_tip_attached_jq  proxl_xml_import_item_remove_completed_jq"/>
  	  {{/if}}  	  
  	  {{#if statusStarted }}
  	  	<%--  Hidden icon for placeholder --%>
		<input type="image" src="${ contextPath }/images/icon-circle-x.png" 
			style="visibility: hidden;"/>  	  
  	  {{/if}}  	  
  	  
 	</td>
  </c:if>
  
  <td class=" clickable proxl_xml_import_item_expand_collapse_row_clickable_jq " style="vertical-align: top; padding-top: 2px;">
	  <img src="${ contextPath }/images/icon-expand-small.png" class=" proxl_xml_import_item_expand_row_icon_jq tool_tip_attached_jq "
	  		data-tooltip="Show Details" >
	  <img src="${ contextPath }/images/icon-collapse-small.png" class=" proxl_xml_import_item_collapse_row_icon_jq tool_tip_attached_jq " 
	  		data-tooltip="Hide Details"  style="display: none;">
  </td>

   <td style="width: 100%; vertical-align: top;" class=" clickable filename_status_cell_jq proxl_xml_import_item_expand_collapse_row_clickable_jq tool_tip_attached_jq "
   			data-tooltip="Click to Show/Hide Details" >
   	<div >
	  	{{#if searchName }}
	  		{{ searchName }} 
	  	{{else}}
	  		{{ uploadedFilename }} 
	  	{{/if}}
  		{{#if statusQueuedOrRequeued }}(Position in queue: {{queuePosition}}){{/if}}
  		{{#if statusStarted }}(Running){{/if}}
  		{{#if statusComplete }}(Success){{/if}}
  		{{#if statusFailed }}(Error){{/if}}
  	</div> 
   </td>
   <td style="white-space: nowrap; vertical-align: top;" class=" clickable submit_process_date_time_jq proxl_xml_import_item_expand_collapse_row_clickable_jq tool_tip_attached_jq "
   			data-tooltip="Click to Show/Hide Details" >
	 	{{#if importEndDateTime }}
	  	  	Processed: {{ importEndDateTime }}
		{{else}}	
			Submitted: {{ importSubmitDateTime }}
		{{/if}}	
   </td>
</tr>
