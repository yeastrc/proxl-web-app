

<%--  defaultPageViewButtonFragment.jsp    /WEB-INF/jsp-includes/defaultPageViewButtonFragment.jsp

		This contains the Save As Default Button

	  This is used by the Javascript code in defaultPageView.js

	 Page object 'UpdateButtonText' is required.
	 Page object 'page_JS_Object' is required.
	 
	 Page object 'searchId' is optional.  if left blank, JS object in page_JS_Object must provide it. 
	
	  
--%>


<%@page import="org.yeastrc.xlink.www.constants.WebConstants"%>
<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

						
	<c:if test="${ authAccessLevel.projectOwnerAllowed }" >
	  
	  <div style="display: inline-block; position: relative; ">
	  
	  
	  
		<input type="button" value="Save As Default"
			id="setDefaultPageViewButton"
			class=" tool_tip_attached_jq  "
			data-tooltip="set the current data as the default view" 
			data-project_search_id="${ projectSearchId }"
			onclick="defaultPageView.saveOrUpdateDefaultPageView( { clickedThis : this, page_JS_Object : ${ page_JS_Object } } )">
			
		<div class=" tool_tip_attached_jq  "
			id="setDefaultPageViewButtonDisabledOverlay" 
			style="position:absolute;left:0;right:0;top:0;bottom:0; display: none;" 
			data-tooltip="The 'Set As Default' is disabled until '${ UpdateButtonText }' is clicked." 
			></div>
	  </div>
	</c:if>