
<%@page import="org.yeastrc.xlink.base.proxl_xml_file_import.enum_classes.ProxlXMLFileImportFileType"%>
<%@page import="org.yeastrc.xlink.www.proxl_xml_file_import.constants.ProxlXMLFileUploadWebConstants"%>
<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

  
<input type="hidden" id="proxl_xml_file_max_file_upload_size" value="<%=ProxlXMLFileUploadWebConstants.get_MAX_PROXL_XML_FILE_UPLOAD_SIZE_AS_STRING()%>">
<input type="hidden" id="proxl_xml_file_max_file_upload_size_formatted" value="<%=ProxlXMLFileUploadWebConstants.get_MAX_PROXL_XML_FILE_UPLOAD_SIZE_FORMATTED()%>">

<input type="hidden" id="proxl_import_scan_file_max_file_upload_size" value="<%=ProxlXMLFileUploadWebConstants.get_MAX_SCAN_FILE_UPLOAD_SIZE_AS_STRING()%>">
<input type="hidden" id="proxl_import_scan_file_max_file_upload_size_formatted" value="<%=ProxlXMLFileUploadWebConstants.get_MAX_SCAN_FILE_UPLOAD_SIZE_FORMATTED()%>">



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
	<div id="proxl_xml_file_upload_overlay_body" class="proxl-xml-file-upload-overlay-body" style="text-align:left;" >

		
		
		<h3>
			Upload a Proxl XML file and optional associated scan files for import
		</h3>
		
		<div style="margin-bottom: 5px;">
			Search Name: 
			<input style="width: 500px;" id="import_proxl_xml_file_search_name">
		</div>
		
		 <div id="import_proxl_xml_file_main_import_block">
		
		      <%--  Proxl XML file: select a file to import block --%>
		      
		   <div id="import_proxl_xml_file_select_a_proxl_xml_file_to_import_block" >
		
		    <div id="import_proxl_xml_choose_proxl_xml_file_block" >
		    
		    	<a href="javascript:"  id="import_proxl_xml_choose_proxl_xml_file_button"
		    		 >+Add Proxl XML File</a>
		    		 
				<input type="file"  accept=".xml"
					id="import_proxl_xml_proxl_xml_file_field" style="display: none;"
					data-file_type="<%=ProxlXMLFileImportFileType.PROXL_XML_FILE.value()%>"/>
<%-- 		    
				
--%>				
				
				<div style="font-size: 80%;">
					(Max filesize: <%=ProxlXMLFileUploadWebConstants.get_MAX_PROXL_XML_FILE_UPLOAD_SIZE_FORMATTED()%> bytes)
				</div>
			</div>
			
			<div id="import_proxl_xml_chosen_proxl_xml_file_block" style="display: none;"
				class=" import_proxl_xml_file_scan_file_entry_block_jq "
				data-file_index=""  <%-- set in JS --%> 
				data-file_type="<%=ProxlXMLFileImportFileType.PROXL_XML_FILE.value()%>">
			  
			  	<%--  Remove Icon --%>
			  <div style="float: left; padding-right: 10px;">
				<input type="image" src="${ contextPath }/images/icon-circle-x.png" 
					data-tooltip="Remove File" 
					id="import_proxl_xml_remove_proxl_xml_file_button"
					class="tool_tip_attached_jq "/>
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
				<span id="import_proxl_xml_chosen_proxl_xml_file_name" ></span>
			  </div>
			  <div style="clear: both;"></div>
			</div>
			
		  </div>
		  
		  <div id="import_proxl_xml_scan_files_block" style="padding-top: 10px;" >
		  
		  
		  </div>
		  
		  
		    <div id="import_proxl_xml_choose_scan_file_block" style="display: none;">
			  <div style="display:inline-block;position:relative;"> <%-- outer div to support overlay div when button disabled --%>
			  
		    	<a href="javascript:"  id="import_proxl_xml_choose_scan_file_button"
		    		 >+Add Scan File</a>
					
				</div>
				
		    	
				<input type="file" accept=".mzML,.mzXML"  
					id="import_proxl_xml_scan_file_field" style="display: none;"
					data-file_type="<%=ProxlXMLFileImportFileType.SCAN_FILE.value()%>"/>
				
				<div style="font-size: 80%;">
					(Max filesize: <%=ProxlXMLFileUploadWebConstants.get_MAX_SCAN_FILE_UPLOAD_SIZE_FORMATTED()%> bytes)
				</div>
			</div>
			
			<div style="margin-top: 12px;" >
			
			  <div style="display:inline-block;position:relative;"> <%-- outer div to support overlay div when button disabled --%>
			  
					<input type="button" value="Submit Upload" disabled="disabled"  id="import_proxl_xml_file_submit_button">
					
							<%-- overlay div to provide tooltip for button --%>
					<div id="import_proxl_xml_file_submit_button_disabled_overlay"
							style="position:absolute;left:0;right:0;top:0;bottom:0;" 
							class="tool_tip_attached_jq "
							data-tooltip="Submit. Enabled when Proxl XML file is uploaded and scan files are uploaded if any are selected" ></div>
				</div>
				
				<input type="button" value="Close" id="import_proxl_xml_file_close_button">
				
		  </div>   		
		  
		 </div> <%-- END <div id="import_proxl_xml_file_main_import_block">  --%>

		 <div id="import_proxl_xml_file_submit_import_success_block" style="display: none;">

			<%--  Displayed on successful submission --%>
			
			<div style="margin-bottom: 10px;">
				The import has been submitted:
			</div>
			
			<div >
				Proxl XML filename: <span id="import_proxl_xml_file_submit_import_success_proxl_xml_filename"></span> 
			</div>
			<div id="import_proxl_xml_file_submit_import_success_scan_filename_container">
				
			</div>
			
			<div style="margin-top: 10px;">
				<input type="button" value="Submit Another Import" 
					id="import_proxl_xml_file_submit_import_success_submit_another_import_button">
				<input type="button" value="Close" class=" proxl_xml_file_upload_overlay_close_parts_jq ">
				
			</div>

		 </div> <%-- END <div id="import_proxl_xml_file_submit_import_success_block">  --%>


	</div>  <%--  END  <div id="proxl_xml_file_upload_overlay_body" --%>
</div>

	
	<!-- END:   Modal dialog for uploading a Proxl XML file -->
	
	
	<%--  Per Scan File listing --%>
			
	<script id="import_proxl_xml_file_scan_file_entry_template"  type="text/x-handlebars-template">

			<%--  include the template text  --%>
			<%@ include file="/WEB-INF/jsp_template_fragments/For_jsp_includes/proxl_XML_Upload_Overlay.jsp/perUploadFileTemplate.jsp" %>

	</script>
	
	<%--  Successful Submit Per Scan File listing, NOT Handlebars template --%>
		
	<script id="import_proxl_xml_file_scan_file_submitted_entry_template"  type="text">
	  <div >
		Scan filename: <span class=" scan_filename_jq "></span>
	  </div> 
	</script>

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
	
		
	
	