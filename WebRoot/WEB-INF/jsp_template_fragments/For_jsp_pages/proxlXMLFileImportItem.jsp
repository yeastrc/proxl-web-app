
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

		<%-- !!!   Handlebars template   !!!!!!!!!   --%>
		
		
<%--   proxlXMLFileImportItem.jsp  --%>



<tr class=" proxl_xml_import_item_row_jq " 
  	 	data-tracking_id="{{ trackingId  }}"
  	 	data-status_id="{{ statusId }}" >

  <%--  Only project owner allowed to cancel queued or remove failed --%>
  <c:if test="${authAccessLevel.projectOwnerAllowed}" >
  
	<td style="padding-right: 5px; vertical-align: top;">
  	  {{#if statusQueued }}
		<input type="image" src="${ contextPath }/images/icon-circle-x.png" 
			data-tooltip="Cancel Queued Item" 
			class="tool_tip_attached_jq proxl_xml_import_item_cancel_queued_jq"/>
  	  {{/if}}
  	  {{#if statusReQueued }}
		<input type="image" src="${ contextPath }/images/icon-circle-x.png" 
			data-tooltip="Cancel ReQueued Item" 
			class="tool_tip_attached_jq proxl_xml_import_item_cancel_re_queued_jq"/>
  	  {{/if}}
  	  
  	  {{#if statusFailed }}
		<input type="image" src="${ contextPath }/images/icon-circle-x.png" 
			data-tooltip="Remove Failed Item" 
			class="tool_tip_attached_jq proxl_xml_import_item_remove_failed_jq"/>
  	  {{/if}}
 	</td>
  </c:if>

  <td>
  
    <table>
	  <tr>
  	 	<td>File:</td>
		<td>{{ uploadedFilename }} {{#if searchName }}({{ searchName }}){{/if}}</td>
	  </tr>
	  <tr>
  	 	<td>status:</td>
		<td>{{ status }}</td>
	  </tr>
	  
	  <%-- Not currently populated 
	  <tr>
  	 	<td>uploadDateTime:</td>
		<td>{{ uploadDateTime }}</td>
	  </tr>
	  --%>
	  <tr>
  	 	<td>upload person:</td>
		<td>{{ nameOfUploadUser }}</td>
	  </tr>	  
	  {{#if scanFilenames }}
	  <tr>
  	 	<td style="padding-right: 5px; vertical-align: top;" >scan files:</td>
		<td>
		  {{#each scanFilenames}}
			<div >
				{{this}}
			</div>
		  {{/each}}
		</td>
	  </tr>	  
	  {{/if}}	  
	  
   </table>

</tr>

<tr>
	<td colspan="10">
		<div class="top-level-label-bottom-border"></div>
	</td>
</tr>
