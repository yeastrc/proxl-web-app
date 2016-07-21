<%@page import="org.yeastrc.xlink.www.constants.ConfigSystemsKeysConstants"%>
<%@page import="org.yeastrc.xlink.www.constants.UserSignupConstants"%>
<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %><%-- Always put this directive at the very top of the page --%>
<%@page import="org.yeastrc.xlink.www.constants.AuthAccessLevelConstants"%>
<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

<%--  manageConfiguration.jsp --%>


 <c:set var="pageTitle">Manage Configuration</c:set>

 <c:set var="pageBodyClass" >manage-configuration-page</c:set>

 <c:set var="headerAdditions">

			<!-- Handlebars templating library   -->
			
			<%--  
			<script type="text/javascript" src="${ contextPath }/js/libs/handlebars-v2.0.0.js"></script>
			--%>
			
			<!-- use minimized version  -->
<%-- 			
			<script type="text/javascript" src="${ contextPath }/js/libs/handlebars-v2.0.0.min.js"></script>
--%>
			
	<script type="text/javascript" src="${ contextPath }/js/handleServicesAJAXErrors.js?x=${cacheBustValue}"></script> 

	<script type="text/javascript" src="${ contextPath }/js/manageConfigurationPage.js?x=${cacheBustValue}"></script> 

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
		HTML to put at center of bottom of web page: 
		<input type="text" class=" config_text_inputs_jq " id="input_footer_center_of_page_html" style="width: 450px;"
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
			
  </div>  <%--  Close <div class="overall-enclosing-block">  --%>

  
<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>

