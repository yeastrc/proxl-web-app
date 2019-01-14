<%@page import="org.yeastrc.xlink.www.constants.CacheSizeConfigStringsConstants"%>
<%@page import="org.yeastrc.xlink.base.config_system_table_common_access.ConfigSystemsValuesSharedConstants"%>
<%@page import="org.yeastrc.xlink.base.config_system_table_common_access.ConfigSystemsKeysSharedConstants"%>
<%@page import="org.yeastrc.xlink.www.constants.ConfigSystemsKeysConstants"%>
<%@page import="org.yeastrc.xlink.www.constants.UserSignupConstants"%>
<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %><%-- Always put this directive at the very top of the page --%>
<%@page import="org.yeastrc.xlink.www.constants.AuthAccessLevelConstants"%>
<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

<%--  configureProxlForAdmin.jsp --%>


 <c:set var="pageTitle">Manage Configuration</c:set>

 <c:set var="pageBodyClass" >manage-configuration-page</c:set>

 <c:set var="headerAdditions">

			<!-- Handlebars templating library   -->
			
			<%--  
			<script type="text/javascript" src="js/libs/handlebars-v2.0.0.js"></script>
			--%>
			
			<!-- use minimized version  -->
<%-- 			
			<script type="text/javascript" src="js/libs/handlebars-v2.0.0.min.js"></script>
--%>
			
	<script type="text/javascript" src="static/js_generated_bundles/proxl_config_page/configureProxlForAdminPage-bundle.js?x=${ cacheBustValue }"></script>

<%--  Moved to Front End Build Bundles		
	
	<script type="text/javascript" src="js/handleServicesAJAXErrors.js?x=${cacheBustValue}"></script> 
	
	<script type="text/javascript" src="js/configureProxlForAdminPage.js?x=${cacheBustValue}"></script> 
--%>

 </c:set>


<%@ include file="/WEB-INF/jsp-includes/header_main.jsp" %>


  <div class="overall-enclosing-block">

	
	<div class="top-level-label manage-config-title">Manage Configuration</div>
	
	<div class="top-level-label-bottom-border" ></div>

	<div style="margin-bottom: 10px;">
	  <div style="margin-bottom: 3px;">
		Allow Account Registration WITHOUT Invite: 
		<input type="checkbox" class=" config_checkbox_inputs_jq " id="input_user_signup_allow_without_invite" 
			data-config-key="<%= ConfigSystemsKeysConstants.USER_SIGNUP_ALLOW_WITHOUT_INVITE_KEY %>"
			data-value-checked="<%= UserSignupConstants.USER_SIGNUP_ALLOW_WITHOUT_INVITE_KEY__TRUE %>" 
			data-value-not-checked="<%= UserSignupConstants.USER_SIGNUP_ALLOW_WITHOUT_INVITE_KEY__FALSE %>" > 
	  </div> 
	</div>
	
	
	<div style="margin-bottom: 10px;">
	  <div style="margin-bottom: 3px;">
	  	<div>
			Google Recaptcha (Not used if either not configured):
		</div>
		<div style="margin-left: 20px;">
		
			<div style="margin-bottom: 3px;">
				Site key: 
				<input type="text" class=" config_text_inputs_jq " style="width: 450px;"
					data-config-key="<%= ConfigSystemsKeysConstants.GOOGLE_RECAPTCHA_SITE_KEY_KEY %>"
					data-FOOTER_CENTER_OF_PAGE_HTML="true">
			</div>
			<div>
				Secret key: 
				<input type="text" class=" config_text_inputs_jq " style="width: 450px;"
					data-config-key="<%= ConfigSystemsKeysConstants.GOOGLE_RECAPTCHA_SECRET_KEY_KEY %>"
					data-FOOTER_CENTER_OF_PAGE_HTML="true">
			</div>
		</div>
	  </div> 
	</div>
	
	<div style="margin-bottom: 10px;">
	  <div style="margin-bottom: 3px;">
		HTML to put at center of bottom of web page: 
		<input type="text" class=" config_text_inputs_jq " id="input_footer_center_of_page_html" style="width: 650px;"
			data-config-key="<%= ConfigSystemsKeysConstants.FOOTER_CENTER_OF_PAGE_HTML_KEY %>"
			data-FOOTER_CENTER_OF_PAGE_HTML="true"> 
	  </div> 
	</div>

	<div style="margin-bottom: 10px;" >
	  <div style="margin-bottom: 3px;">
		From Address for emails sent: 
		<input type="text" class=" config_text_inputs_jq " id="input_email_from_address" style="width: 450px;"
			data-config-key="<%= ConfigSystemsKeysConstants.EMAIL_FROM_ADDRESS_URL_KEY %>">
	  </div>
	</div>
					

	<div style="margin-bottom: 10px;" >
	  <div style="margin-bottom: 3px;">
		SMTP Server URL for emails sent: 
		<input type="text" class=" config_text_inputs_jq " id="input_email_smtp_server_url" style="width: 450px;"
			data-config-key="<%= ConfigSystemsKeysConstants.EMAIL_SMTP_SERVER_URL_KEY %>"> 
	  </div> 
	</div>
	
	<div style="margin-bottom: 10px;" >
	  <div style="margin-bottom: 3px;">
		Google Analytics Tracking Code: 
		<input type="text" class=" config_text_inputs_jq " id="input_google_analytics_tracking_code" style="width: 450px;"
			data-config-key="<%= ConfigSystemsKeysConstants.GOOGLE_ANALYTICS_TRACKING_CODE_KEY %>"> 
	  </div> 
	</div>
	
	
	<div style="margin-bottom: 10px;" >
	  <div style="margin-bottom: 3px;">
		Protein Annotation Service URL: 
		<input type="text" class=" config_text_inputs_jq " id="input_protein_annotation_webservice_url" style="width: 450px;"
			data-config-key="<%= ConfigSystemsKeysConstants.PROTEIN_ANNOTATION_WEBSERVICE_URL_KEY %>"> 
	  </div> 
	</div>
	
	<div style="margin-bottom: 10px;" >
	  <div style="margin-bottom: 3px;">
		Protein Listing Service URL: 
		<input type="text" class=" config_text_inputs_jq " id="input_protein_listing_from_sequence_taxonomy_webservice_url" style="width: 450px;"
			data-config-key="<%= ConfigSystemsKeysConstants.PROTEIN_LISTING_FROM_SEQUENCE_TAXONOMY_WEBSERVICE_URL_KEY %>"> 
	  </div> 
	</div>

	<div style="margin-bottom: 10px;" >
		<div style="margin-bottom: 3px;">
			Cache Files Root Directory (Must be writable by account running web app): 
			<input type="text" class=" config_text_inputs_jq " style="width: 650px;"
				data-config-key="<%= ConfigSystemsKeysSharedConstants.CACHE_FILES_ROOT_DIRECTORY %>"
				>
		</div>
	</div>
	
	<div style="margin-bottom: 10px;">
	  <div style="margin-bottom: 3px;">
	  	<div>
			Submit Search Upload on Website (Requires running the "Run Importer" process):
		</div>
		<div style="margin-left: 20px;">
		
			<div style="margin-bottom: 3px;">
				Run Importer Workspace: 
				<input type="text" class=" config_text_inputs_jq " style="width: 650px;"
					data-config-key="<%= ConfigSystemsKeysSharedConstants.file_import_proxl_xml_scans_TEMP_DIR_KEY %>"
					>
			</div>
			<div>
				Allow Scan file Upload: 
				<input type="checkbox" class=" config_checkbox_inputs_jq "  id="allow_scan_file_upload_checkbox"
					data-config-key="<%= ConfigSystemsKeysSharedConstants.SCAN_FILE_IMPORT_ALLOWED_VIA_WEB_SUBMIT_KEY %>"
					data-value-checked="<%= ConfigSystemsValuesSharedConstants.TRUE %>" 
					data-value-not-checked="<%= ConfigSystemsValuesSharedConstants.FALSE %>" > 
			
			</div>
			
			<%--  Error Message for 'Allow Scan file Upload' checked 
			      but 'Spectral Storage Service Base URL' not populated,
				  or vice versa
			 --%>
			<div style="position: relative;"> <%--  container div for error-message-container which is position absolute --%>
			  <div style="position: absolute; top: -60px;">
		  		<div class="error-message-container error_message_container_jq" 
		  				id="error_message_spectral_storage_only_one_has_value"
		  				style="text-align: left; margin-left: 50px;width: 400px;" >
		  			<div class="error-message-inner-container" >
		  				<div class="error-message-close-x error_message_close_x_jq">X</div>
			  			<div class="error-message-text" 
			  			  >If one 'Spectral Storage Service Base URL' has a value BOTH must have a value.</div>
		  			</div>
			  	</div>
			  </div>
			  <div style="position: absolute; top: -60px;">
		  		<div class="error-message-container error_message_container_jq" 
		  				id="error_message_allow_scan_file_selected_spectral_storage_empty"
		  				style="text-align: left; margin-left: 50px;width: 400px;" >
		  			<div class="error-message-inner-container" >
		  				<div class="error-message-close-x error_message_close_x_jq">X</div>
			  			<div class="error-message-text" 
			  			  >'Allow Scan file Upload' is selected so BOTH 'Spectral Storage Service Base URL' must have a value.</div>
		  			</div>
			  	</div>
			  </div>
			  <div style="position: absolute; top: -60px;">
		  		<div class="error-message-container error_message_container_jq" 
		  				id="error_message_allow_scan_file_not_selected_spectral_storage_not_empty"
		  				style="text-align: left; margin-left: 50px;width: 400px;" >
		  			<div class="error-message-inner-container" >
		  				<div class="error-message-close-x error_message_close_x_jq">X</div>
			  			<div class="error-message-text" 
			  			  >BOTH 'Spectral Storage Service Base URL' has a value so 'Allow Scan file Upload' must be selected.</div>
		  			</div>
			  	</div>
			  </div>
		    </div>
		   
			<div>
				Delete uploaded files after Successful Import: 
				<input type="checkbox" class=" config_checkbox_inputs_jq "  
					data-config-key="<%= ConfigSystemsKeysSharedConstants.IMPORT_DELETE_UPLOADED_FILES %>"
					data-value-checked="<%= ConfigSystemsValuesSharedConstants.TRUE %>" 
					data-value-not-checked="<%= ConfigSystemsValuesSharedConstants.FALSE %>" > 
			
			</div>
		</div>
	  </div> 
	</div>	
	
	<div style="margin-bottom: 10px;" >
		<div style="margin-bottom: 3px;">
			Spectral Storage Service Accept Import Base URL (required if allow scan file uploads): 
			<input type="text" class=" config_text_inputs_jq " id="spectral_storage_service_accept_import_base_url_input_field" 
				style="width: 650px;"
				data-config-key="<%= ConfigSystemsKeysSharedConstants.SPECTRAL_STORAGE_SERVICE_ACCEPT_IMPORT_BASE_URL %>"
				>
		</div>
	</div>
	
	<div style="margin-bottom: 10px;" >
		<div style="margin-bottom: 3px;">
			Spectral Storage Service Get Data Base URL (required if allow scan file uploads): 
			<input type="text" class=" config_text_inputs_jq " id="spectral_storage_service_get_data_base_url_input_field" 
				style="width: 650px;"
				data-config-key="<%= ConfigSystemsKeysSharedConstants.SPECTRAL_STORAGE_SERVICE_GET_DATA_BASE_URL %>"
				>
		</div>
	</div>
	<div >
		<input type="button" value="Save" id="save_button">
		<input type="button" value="Reset" id="reset_button">
	</div>
	
 	<%-- Values successfully saved message --%>

	<div style="position: relative;"> <%--  container div for success-message-container which is position absolute --%>
	  <div style="position: absolute; top: 12px;">
		<div id="success_message_values_updated"
				class="success-message-container error_message_container_jq" 
				style="text-align: left; margin-left: 130px;width: 200px;">
			<div class="success-message-inner-container"  style="text-align: center;">
				<div class="success-message-close-x error_message_close_x_jq">X</div>
				<div class="success-message-text" >Values Saved</div>
			</div>
	 	</div>	  
	  </div>
	</div>
	
	<%--  Manage Terms of Service (TOS) --%>
	
	<div style="margin-top: 20px;">
	
		<div >
			<span class=" terms-of-service-config-title ">Terms of Service Management:</span>
		</div>
		<div >
			<span id="tos_not_exist" style=";"
				><input type="button" id="tos_add_button" 
					value="Add Terms of Service"></span>
				
			<span id="tos_enabled" style="display: none;"
				><input type="button"  id="tos_change_button" 
					value="Change Terms of Service">
				<input type="button"  id="tos_disable_button" 
					value="Disable Terms of Service"></span>	
				
			<span id="tos_not_enabled" style="display: none;"
					><input type="button" id="tos_enable_button" 
						value="Enable Terms of Service"></span>	
								
		</div>
	</div>
	

	<div class="top-level-container collapsable_container_jq" >
	
		<div  class="collapsable-link-container top-level-collapsable-link-container collapsable_link_container_jq" style="">
			<a href="javascript:" class="top-level-collapsable-link collapsable_collapse_link_jq" style="display: none;"
				><img  src="images/icon-collapse.png"></a>
			<a href="javascript:" class="top-level-collapsable-link collapsable_expand_link_jq"
				><img  src="images/icon-expand.png"></a>
		
		</div>
	
		<div class="top-level-label">
		  Advanced Proxl Configuration
		</div>
	
		  <div class="advanced-body collapsable_jq " style="display: none;">
		  	<div >
		  		Click this when change the "config_system" table in the database directly: 
				<input type="button" id="clear_cached_config_data_button" 
									value="Clear Cached Config Data">
			</div>
		  	<div >
		  		Click this when change any data in the database directly:
				<input type="button" id="clear_all_cached_data_button" 
									value="Clear All Cached Data">
			</div>
		  	<div >
		  		Click this to have current sizes of data caches to the log file:
				<input type="button" id="write_all_data_cache_sizes_to_log_button" 
									value="Write Cached Data Sizes to log">
			</div>			
			
		  </div>
	  </div>

  </div>  <%--  Close <div class="overall-enclosing-block">  --%>

	<%--  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! --%>
	
	<%--  !!!!!!!!!!    Overlays               !!!!!!!!!! --%>

	<%--  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! --%>
	
	
	
  <%--  Terms of Service Overlay --%>
		

	<%--  Terms of Service Overlay Background --%>
	
	
	<div id="terms_of_service_modal_dialog_overlay_background" class="terms-of-service-config-modal-dialog-overlay-background" style="display: none;"  >
	
	</div>
	
	
	<%--  Terms of Service Overlay Div --%>
	
	<div id="terms_of_service_overlay_div" class=" terms-of-service-config-overlay-div " style="display: none; "  >
	
		<div id="terms_of_service_overlay_header" class="terms-of-service-config-overlay-header" style="width:100%; " >
			<h1 id="terms_of_service_overlay_X_for_exit_overlay" class="terms-of-service-config-overlay-X-for-exit-overlay" >X</h1>
			<h1 id="terms_of_service_overlay_header_text" class="terms-of-service-config-overlay-header-text" 
				><span  class=" add_tos_parts_jq " >Add Terms of Service</span
				><span  class=" change_tos_parts_jq " style="display: none;" >Change Terms of Service</span
				></h1>
		</div>
		<div id="terms_of_service_overlay_body" class="terms-of-service-config-overlay-body" >
		
		  <div style=" margin-bottom: 10px;" >
			<div class=" add_tos_parts_jq ">
				Enter a Terms of Service and click "Add"
			</div>
		
			<div class=" change_tos_parts_jq " style="display: none;">
				Change the Terms of Service and click "Change"
			</div>
			
			<div >
				The terms of service can contain HTML
			</div>
		  </div>
		
			<textarea id="terms_of_service_user_text" 
				rows="20" cols="" 
				style="width: 400px;"></textarea>
		
			<div class=" add_tos_parts_jq " 
				style=" margin-top: 10px;" >
				<input type="button" value="Add"  id="terms_of_service_overlay_add_button" 
					 class="tool_tip_attached_jq" data-tooltip="Add Terms of Service" />
			</div> 

			<div class=" change_tos_parts_jq " 
				style=" margin-top: 10px;" >
				<input type="button" value="Change"  id="terms_of_service_overlay_change_button" 
					 class="tool_tip_attached_jq" data-tooltip="Change Terms of Service" />
			</div> 			
			
		</div> <%--  END  <div id="terms_of_service_overlay_body"  --%>
		
	</div>  <%--  END  <div id="terms_of_service_overlay_div"  --%>



<script type="text/text">
<!--
	Values for key "cache_size_config_label":
  "<%= CacheSizeConfigStringsConstants.CACHE_SIZE_VALUE_FULL %>"
  "<%= CacheSizeConfigStringsConstants.CACHE_SIZE_VALUE_HALF %>"
  "<%= CacheSizeConfigStringsConstants.CACHE_SIZE_VALUE_SMALL %>"
  "<%= CacheSizeConfigStringsConstants.CACHE_SIZE_VALUE_FEW %>"
	
//-->
</script>
<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>

