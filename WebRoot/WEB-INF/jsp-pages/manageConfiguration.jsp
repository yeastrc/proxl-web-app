<%@page import="org.yeastrc.xlink.www.constants.ConfigSystemsKeysConstants"%>
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
			
	<script type="text/javascript" src="${ contextPath }/js/handleServicesAJAXErrors.js"></script> 

	<script type="text/javascript" src="${ contextPath }/js/manageConfigurationPage.js"></script> 

 </c:set>


<%@ include file="/WEB-INF/jsp-includes/header_main.jsp" %>


  <div class="overall-enclosing-block">

	
	<div class="top-level-label">Manage Configuration</div>
	
	<div class="top-level-label-bottom-border" ></div>

	<div style="margin-bottom: 10px;">
	  <div style="margin-bottom: 3px;">
		HTML to put at center of bottom of web page: <input type="text" id="input_footer_center_of_page_html" style="width: 450px;"> 
	  </div> 
	  <div style="margin-left: 50px;">
	  	comment: <input type="text" id="input_footer_center_of_page_html_comment" style="width: 450px;">
	  </div> 
	</div>

	<div style="margin-bottom: 10px;" >
	  <div style="margin-bottom: 3px;">
		From Address for emails sent: <input type="text" id="input_email_from_address" style="width: 450px;">
	  </div>
	  <div style="margin-left: 50px;">
	  	comment: <input type="text" id="input_email_from_address_comment" style="width: 450px;">
	  </div> 
	</div>
					

	<div style="margin-bottom: 10px;" >
	  <div style="margin-bottom: 3px;">
		SMTP Server URL for emails sent: <input type="text" id="input_email_smtp_server_url" style="width: 450px;"> 
	  </div> 
	  <div style="margin-left: 50px;">
	  	comment: <input type="text" id="input_email_smtp_server_url_comment" style="width: 450px;">
	  </div> 
	</div>
	
	<div >
		<input type="button" value="Save" id="save_button">
		<input type="button" value="Reset" id="reset_button">
	</div>
						
	<%-- For the javascript to read --%>
	<input type="hidden" id="config_key_footer_center_of_page_html" value="<%= ConfigSystemsKeysConstants.FOOTER_CENTER_OF_PAGE_HTML_KEY %>">
	
	<input type="hidden" id="config_key_email_from_address" value="<%= ConfigSystemsKeysConstants.EMAIL_FROM_ADDRESS_URL_KEY %>">

	<input type="hidden" id="config_key_email_smtp_server_url" value="<%= ConfigSystemsKeysConstants.EMAIL_SMTP_SERVER_URL_KEY %>">


  </div>  <%--  Close <div class="overall-enclosing-block">  --%>

  
<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>

