

		<%-- !!!   Handlebars template   !!!!!!!!!   --%>
		
	<%--  perUploadFileTemplate.jsp --%>
	
	<%--  Scan File Upload Template --%>



<%-- 
containing <script> id:  import_proxl_xml_file_scan_file_entry_template
--%>
		
<%--  Most of this file upload block is duplicated in proxl_XML_Upload_Overlay.jsp for Proxl XML files --%>
		
<tr class=" import_proxl_xml_file_scan_file_entry_block_jq " 
   		data-file_index="{{ fileIndex }}" data-file_type="{{ fileType }}">
  <td class="column-1">
  	<%--  Remove Icon --%>
	<input type="image" src="${ contextPath }/images/icon-circle-x.png" 
		data-tooltip="Remove File" 
		class="tool_tip_attached_jq  scan_file_remove_button_jq "/>
  </td>
  <td class="column-2 column-filename">	
	<div class="proxl-xml-file-upload-filename-containing-div">
		{{ fileName }}
	</div>
  </td>
  <td class="column-3">
 		 <%--  Progress Bar --%>
	<div class="progress_bar_container_jq" style="">
	  <div class=" import-proxl-xml-file-progress-bar-container " 
	  	style="position: relative;">        
	     <div class=" import-proxl-xml-file-progress-outer ">
	        <div class=" import-proxl-xml-file-progress progress_bar_jq "></div>
	     </div>
				<%-- overlay div to provide progress percentage text --%>
		 <div 	style="position:absolute;left:0;right:0;top:0;bottom:0; text-align: center;" 
				class=" progress_bar_text_jq tool_tip_attached_jq " 
				data-tooltip="Upload Progress"
				></div>
	     
	  </div>
	</div>	
	  	<%--  Upload Complete --%>
	<span class=" upload_complete_msg_jq  import-proxl-xml-file-upload-complete " style="display: none;">
			Complete			  
	</span>
	  
  </td>
</tr>
