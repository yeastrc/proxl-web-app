

<%--  savePageViewButtonFragment.jsp    /WEB-INF/jsp-includes/savePageViewButtonFragment.jsp

		This contains the 'Save View' Button

	  This is used by the Javascript code in defaultPageView.js

	 Page object 'UpdateButtonText' is required.
	 Page object 'page_JS_Object' is required.
	 
	 Page object 'projectSearchId' is optional.  if left blank, JS object in page_JS_Object must provide it. 
	
	  
--%>


<%@page import="org.yeastrc.xlink.www.constants.WebConstants"%>
<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

						
	<c:if test="${ authAccessLevel.assistantProjectOwnerAllowed }" >
	  
	  <div style="display: inline-block; position: relative; ">
	  
	  
	  
		<input type="button" value="Save View"
			id="savePageViewButton"
			class=" tool_tip_attached_jq  "
			data-tooltip="Save the current data in the 'Saved Views' section of the Project page" 
			<%-- data-project_search_id="${ projectSearchId }" --%>
			onclick="saveView_dataPages.savePageView( { clickedThis : this, page_JS_Object : ${ page_JS_Object } } )">
			
		<div class=" tool_tip_attached_jq  "
			id="savePageViewButtonDisabledOverlay" 
			style="position:absolute;left:0;right:0;top:0;bottom:0; display: none;" 
			data-tooltip="The 'Save View' is disabled until '${ UpdateButtonText }' is clicked." 
			></div>
	  </div>
	</c:if>