
<%@page import="org.yeastrc.xlink.www.file_import_proxl_xml_scans.constants.ProxlXMLFileUploadMaxFileSizeConstants"%>
<%@page import="org.yeastrc.xlink.base.file_import_proxl_xml_scans.enum_classes.ProxlXMLFileImportFileType"%>
<%@page import="org.yeastrc.xlink.www.file_import_proxl_xml_scans.constants.ProxlXMLFileUploadWebConstants"%>
<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>


<%--   proxl_XML_Upload_Overlay.jsp --%>

<c:if test="${ configSystemValues.scanFileImportAllowedViaWebSubmit }" >

	<%-- Only populated when true so the string in the "value" doesn't matter, just cannot be empty string --%>
	<input type="hidden" id="proxl_xml_file_upload_overlay_upload_scan_files" value="true">

</c:if>
  
<input type="hidden" id="proxl_xml_file_max_file_upload_size" value="<%=ProxlXMLFileUploadMaxFileSizeConstants.get_MAX_PROXL_XML_FILE_UPLOAD_SIZE_AS_STRING()%>">
<input type="hidden" id="proxl_xml_file_max_file_upload_size_formatted" value="<%=ProxlXMLFileUploadMaxFileSizeConstants.get_MAX_PROXL_XML_FILE_UPLOAD_SIZE_FORMATTED()%>">

<c:if test="${ configSystemValues.scanFileImportAllowedViaWebSubmit }" >
  <input type="hidden" id="proxl_import_scan_file_max_file_upload_size" value="<%=ProxlXMLFileUploadMaxFileSizeConstants.get_MAX_SCAN_FILE_UPLOAD_SIZE_AS_STRING()%>">
  <input type="hidden" id="proxl_import_scan_file_max_file_upload_size_formatted" value="<%=ProxlXMLFileUploadMaxFileSizeConstants.get_MAX_SCAN_FILE_UPLOAD_SIZE_FORMATTED()%>">
</c:if>


<!--  Modal dialog for uploading a Proxl XML file  -->

<div id="proxl_xml_file_upload_modal_dialog_overlay_background" 
	class="proxl-xml-file-upload-modal-dialog-overlay-background  proxl_xml_file_upload_overlay_show_hide_parts_jq   " style="display: none;"  >

</div>

<%--  The div showing all the Proxl XML file upload dialog --%>

<div id="proxl_xml_file_upload_overlay_container_div" 
	class=" proxl-xml-file-upload-overlay-div overlay-outer-div  proxl_xml_file_upload_overlay_show_hide_parts_jq " style="display: none; "  >


	<div id="proxl_xml_file_upload_overlay_header" class="proxl-xml-file-upload-overlay-header" style="width:100%; " >
		<h1 id="proxl_xml_file_upload_overlay_X_for_exit_overlay" 
			class="proxl-xml-file-upload-overlay-X-for-exit-overlay proxl_xml_file_upload_overlay_close_parts_jq " >X</h1>
		<h1 id="proxl_xml_file_upload_overlay_header_text" class="proxl-xml-file-upload-overlay-header-text" >Import Proxl XML File</h1>
	</div>
	<div id="proxl_xml_file_upload_overlay_body" class="proxl-xml-file-upload-overlay-body" style="text-align:left; position: relative;" >

						<%-- overlay div to provide Submit in progress --%>
		<div id="proxl_xml_file_upload_submit_in_progress" >
			<%--  grey out under spinner --%>
			<div style="position:absolute;left:0;right:0;top:0;bottom:0;background-color:grey;opacity:0.5;" 
					class="tool_tip_attached_jq "
					data-tooltip="Submit in progress" >
					
			</div>
				<%-- id of next div as used by code in spinner.js --%>
			<div style="opacity:1.0;position:absolute;left:20%;top:100px; z-index: 20001" id="coverage-map-loading-spinner" style="" ></div>
		</div>
		
		<h3>
			Upload a Proxl XML file 
			<c:if test="${ configSystemValues.scanFileImportAllowedViaWebSubmit }" >
			 and optional associated scan files 
			</c:if> 
			for import
		</h3>
		
		
				<%--  Search Name input --%>

		<table class="proxl-xml-file-upload-overlay-main-table" style="margin-bottom: 10px;">
		 <tr>
		  <td class="column-1">
		  	<%--  Delete icon column --%>
		  </td>
		  <td class="column-2">
			  <div style="padding-top: 2px;"> <%-- padding-top to align with input text area --%>
			   <div >
				Description:
			   </div>
			   <div style="color: #A55353; font-size: 80%;">
			     Brief description of the search
			   </div> 
			  </div>
		  </td>
		  <td class="column-3">
			<textarea  id="import_proxl_xml_file_search_name" 
				rows="1"  style="width: 300px;" maxlength="2000"></textarea>
				<%--  maxlength="2000"  Keep in sync with database field size --%>
		  </td>
		 </tr>
		</table>


			<%--  Proxl XML Choose File input --%>
		<table id="import_proxl_xml_choose_proxl_xml_file_block" class="proxl-xml-file-upload-overlay-main-table"
				>
		 <tr>
		  <td class="column-1">
		  </td>
		  <td class="column-2">
		    <div >
	    	  <a href="javascript:"  id="import_proxl_xml_choose_proxl_xml_file_button"
		    		 >+Add Proxl XML File</a>
		    </div>
			<div style="font-size: 80%;">
				(Max filesize: <%=ProxlXMLFileUploadMaxFileSizeConstants.get_MAX_PROXL_XML_FILE_UPLOAD_SIZE_FORMATTED()%>)
			</div>
		    		 <%-- Hidden input file element --%>
			<input type="file"  accept=".xml"
				id="import_proxl_xml_proxl_xml_file_field" style="display: none;"
				data-file_type="<%=ProxlXMLFileImportFileType.PROXL_XML_FILE.value()%>"/>
		  </td>
		  <td class="column-3">
		  </td>
		 </tr>
		</table>
	  	
			<%--  Proxl XML Chosen File input --%>

				<%--  Most of this file upload block is duplicated in perUploadFileTemplate.jsp for scan files --%>
			
		<table id="import_proxl_xml_chosen_proxl_xml_file_block" 
			class=" import_proxl_xml_file_scan_file_entry_block_jq  proxl-xml-file-upload-overlay-main-table"  
				style="display: none;"
				data-file_index=""
				data-file_type="<%=ProxlXMLFileImportFileType.PROXL_XML_FILE.value()%>">
		 <tr>
		  <td class="column-1">
		  	<%--  Remove Icon --%>
			<input type="image" src="images/icon-circle-x.png" 
				data-tooltip="Remove File" 
				id="import_proxl_xml_remove_proxl_xml_file_button"
				class="tool_tip_attached_jq "/>
		  </td>
		  <td class="column-2 column-filename">	
			  <div class="proxl-xml-file-upload-filename-containing-div">
				<span id="import_proxl_xml_chosen_proxl_xml_file_name" ></span>
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
		</table>
		
		<c:if test="${ configSystemValues.scanFileImportAllowedViaWebSubmit }" >
	
			<%--  Table uploaded scan files will be displayed in --%>
				
			<table id="import_proxl_xml_scan_files_block" style="padding-top: 10px;" 
				class="proxl-xml-file-upload-overlay-main-table"  >
				
			</table>
			  
	
				<%--  Scan File Choose File --%>
			<table id="import_proxl_xml_choose_scan_file_block" class="proxl-xml-file-upload-overlay-main-table"
				style="display: none;">
			 <tr  >
			  <td class="column-1">
			  </td>
			  <td class="column-2">
			    <div >
		    	  <a href="javascript:"  id="import_proxl_xml_choose_scan_file_button"
			    		 >+Add Scan File</a>
				</div>
				<div style="font-size: 80%;">
						(Max filesize: <%=ProxlXMLFileUploadMaxFileSizeConstants.get_MAX_SCAN_FILE_UPLOAD_SIZE_FORMATTED()%>)
			    		 
					<input type="file" accept=".mzML,.mzXML"  
						id="import_proxl_xml_scan_file_field" style="display: none;"
						data-file_type="<%=ProxlXMLFileImportFileType.SCAN_FILE.value()%>"/>
				</div>
			  </td>
			  <td class="column-3">
			  </td>
			 </tr>
			</table>
			 
		</c:if>
				  

			<%--  Submit and Cancel buttons --%>
		<table id="" class="proxl-xml-file-upload-overlay-main-table"
			style="margin-top: 12px;">
		 <tr  >
		  <td class="column-1">
		  </td>
		  <td >	<%-- class="column-2" --%>  
		  
		    <%--  tooltip for overlay when submit button disabled --%>
		    
		    <c:set var="submitDisabledTooltip">Submit. Enabled when Proxl XML file is uploaded</c:set>
		    
		    <c:if test="${ configSystemValues.scanFileImportAllowedViaWebSubmit }" >
		       <c:set var="submitDisabledTooltip">Submit. Enabled when Proxl XML file is uploaded and scan files are uploaded if any are selected</c:set>
		    </c:if>
		    
			<div id="import_proxl_xml_file_submit_button_container_block"
				style="display:inline-block;position:relative;"> <%-- outer div to support overlay div when button disabled --%>
			  
					<input type="button" value="Submit Upload" disabled="disabled"  id="import_proxl_xml_file_submit_button">
					
							<%-- overlay div to provide tooltip for button --%>
					<div id="import_proxl_xml_file_submit_button_disabled_overlay"
							style="position:absolute;left:0;right:0;top:0;bottom:0;" 
							class="tool_tip_attached_jq "
							data-tooltip="<c:out value="${ submitDisabledTooltip }"></c:out>" ></div>
			</div>
				
			<input type="button" value="Cancel" id="import_proxl_xml_file_close_button">
		  </td>
		 </tr>
		</table> 

	</div>  <%--  END  <div id="proxl_xml_file_upload_overlay_body" --%>
</div>

	
	<!-- END:   Modal dialog for uploading a Proxl XML file -->
	
<c:if test="${ configSystemValues.scanFileImportAllowedViaWebSubmit }" >
	
	<%--  Per Scan File listing --%>
			
	<script id="import_proxl_xml_file_scan_file_entry_template"  type="text/x-handlebars-template">

			<%--  include the template text  --%>
			<%@ include file="/WEB-INF/jsp_template_fragments/For_jsp_includes/proxl_XML_Upload_Overlay.jsp/perUploadFileTemplate.jsp" %>

	</script>

</c:if>	

<!--  Modal dialog for display upload error -->

	<!--  Div behind modal dialog div -->
	
	<div class="modal-dialog-overlay-background   import_proxl_xml_file_upload_error_overlay_show_hide_parts_jq import_proxl_xml_file_upload_error_overlay_cancel_parts_jq  overlay_show_hide_parts_jq" 
		id="import_proxl_xml_file_upload_error_overlay_background" ></div>
	
			<!--  Inline div for positioning modal dialog on page -->
	<div class="import-proxl-xml-file-upload-error-overlay-containing-outermost-div " id="import_proxl_xml_file_upload_error_overlay_containing_outermost_div_inline_div"  >
	
	  <div class="import-proxl-xml-file-upload-error-overlay-containing-outer-div " >
	
	
			<!--  Div overlay for confirming canceling file import -->
		<div class="modal-dialog-overlay-container import-proxl-xml-file-upload-error-overlay-container   import_proxl_xml_file_upload_error_overlay_show_hide_parts_jq  overlay_show_hide_parts_jq" 
			 id="import_proxl_xml_file_upload_error_overlay_container" >
	
			<div class="top-level-label" style="margin-left: 0px; color: #A55353; text-align: center;">
				File Upload Error
			</div>
	
			<div class="top-level-label-bottom-border" ></div>
			
			<div >
			
				<div style="margin-bottom: 10px;">An error has occurred with the file 
					<span id="import_proxl_xml_file_error_message_filename" style="font-weight: bold;"></span> 
					while uploading it or processing it on the server.</div>
				
				<div id="import_proxl_xml_file_file_error_message"></div>
				
				<div style="margin-top: 10px">
					<input type="button" value="Close" class="import_proxl_xml_file_upload_error_overlay_cancel_parts_jq" >
				</div>
					
			</div>
			
		</div>
	
	  </div>
	</div>
	


<!--  Modal dialog for display choose file error -->

	<!--  Div behind modal dialog div -->
	
	<div class="modal-dialog-overlay-background   import_proxl_xml_choose_file_error_overlay_show_hide_parts_jq import_proxl_xml_choose_file_error_overlay_cancel_parts_jq  overlay_show_hide_parts_jq" 
		id="import_proxl_xml_choose_file_error_overlay_background" ></div>
	
			<!--  Inline div for positioning modal dialog on page -->
	<div class="import-proxl-xml-choose-file-error-overlay-containing-outermost-div " id="import_proxl_xml_choose_file_error_overlay_containing_outermost_div_inline_div"  >
	
	  <div class="import-proxl-xml-choose-file-error-overlay-containing-outer-div " >
	
	
			<!--  Div overlay for confirming canceling file import -->
		<div class="modal-dialog-overlay-container import-proxl-xml-choose-file-error-overlay-container   import_proxl_xml_choose_file_error_overlay_show_hide_parts_jq  overlay_show_hide_parts_jq" 
			 id="import_proxl_xml_choose_file_error_overlay_container" >
	
			<div class="top-level-label" style="margin-left: 0px; color: #A55353; text-align: center;">
				File Error
			</div>
	
			<div class="top-level-label-bottom-border" ></div>
			
			<div >
			
				<div id="import_proxl_xml_file_choose_file_error_message"></div>
				
				<div style="margin-top: 10px">
					<input type="button" value="Close" class="import_proxl_xml_choose_file_error_overlay_cancel_parts_jq" >
				</div>
					
			</div>
			
		</div>
	
	  </div>
	</div>
	
	<%--  File chosen error messages --%>
	
	<div id="import_proxl_xml_file_choose_file_error_message_filename_already_chosen" style="display: none;">
	 <div >
		The selected filename <span class=" chosen_file_jq " style="font-weight: bold"></span>
		has already been selected for this upload.
	 </div>
	</div>

	
	<div id="import_proxl_xml_file_choose_file_error_message_file_too_large" style="display: none;">
	 <div >
		The selected filename <span class=" chosen_file_jq " style="font-weight: bold"></span>
		is too large.  It exceeeds <span class=" file_limit_jq " style="font-weight: bold"></span> bytes.
	 </div>
	</div>

	
	<!-- END:   Modal dialog for confirming cancel the upload send -->
	

<!--  Modal dialog for confirm abandon upload -->

	<!--  Div behind modal dialog div -->
	
	<div class="modal-dialog-overlay-background   import_proxl_xml_file_confirm_abandon_upload_overlay_show_hide_parts_jq import_proxl_xml_file_confirm_abandon_upload_overlay_cancel_parts_jq  overlay_show_hide_parts_jq" 
		id="import_proxl_xml_file_confirm_abandon_upload_overlay_background" ></div>
	
			<!--  Inline div for positioning modal dialog on page -->
	<div class="import-proxl-xml-file-confirm-abandon-upload-overlay-containing-outermost-div " id="import_proxl_xml_file_confirm_abandon_upload_overlay_containing_outermost_div_inline_div"  >
	
	  <div class="import-proxl-xml-file-confirm-abandon-upload-overlay-containing-outer-div " >
	
	
			<!--  Div overlay for confirming canceling file import -->
		<div class="modal-dialog-overlay-container import-proxl-xml-file-confirm-abandon-upload-overlay-container   import_proxl_xml_file_confirm_abandon_upload_overlay_show_hide_parts_jq  overlay_show_hide_parts_jq" 
			 id="import_proxl_xml_file_confirm_abandon_upload_overlay_container" >
	
			<div class="top-level-label" style="margin-left: 0px;">Abandon Upload?</div>
	
			<div class="top-level-label-bottom-border" ></div>
			
			<div >
			
				<div >Are you sure you want to abandon your upload?</div>
				
				<div style="margin-top: 10px">
					<input type="button" value="Yes" id="import_proxl_xml_file_confirm_abandon_upload_confirm_button" >
					<input type="button" value="Cancel" class="import_proxl_xml_file_confirm_abandon_upload_overlay_cancel_parts_jq" >
				</div>
					
			</div>
			
		</div>
	
	  </div>
	</div>
	
	
	<!-- END:   Modal dialog for confirming cancel the upload send -->
	
		
	
	