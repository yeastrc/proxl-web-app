

		<%-- !!!   Handlebars template   !!!!!!!!!   --%>
		
		
	
	<%--  Scan File Upload Template --%>



<%-- 
containing <script> id:  import_proxl_xml_file_scan_file_entry_template
--%>
		

<div class=" import_proxl_xml_file_scan_file_entry_block_jq " 
   		data-file_index="{{ fileIndex }}" data-file_type="{{ fileType }}"
<%--  TEMP style="border-color: red; border-width: 1px; border-style: solid;"  --%>
   		>
   		
		<%--  Remove Icon --%>
	  <div style="float: left; padding-right: 10px;">
		<input type="image" src="${ contextPath }/images/icon-circle-x.png" 
			data-tooltip="Remove File" 
			class="tool_tip_attached_jq  upload_file_remove_button_jq "/>
	  </div>
	  	<%--  Progress Bar --%>
	  <div style="float: right; padding-left: 10px;">
		  <div class=" import-proxl-xml-file-progress-bar-container progress_bar_container_jq " 
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
	  <div class="proxl-xml-file-upload-filename-containing-div">
		<span >{{ fileName }}</span>
	  </div>
	  <div style="clear: both;"></div>
  </div>		
  