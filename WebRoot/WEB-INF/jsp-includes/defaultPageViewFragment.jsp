

<%--  defaultPageViewFragment.jsp    /WEB-INF/jsp-includes/defaultPageViewFragment.jsp

	  This is used by the Javascript code in defaultPageView.js

	  This is included in every page where the "Save As Default" button is located.
	  
	  This needs to be included inside the <body> tag and not inside a <div> with position: relative;
--%>


<%@page import="org.yeastrc.xlink.www.constants.WebConstants"%>
<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>


<div class="success-message-container " style="text-align: left; width: 200px;"
		 id="current_url_saved_as_default_page_view_success">
		 
	<div class="success-message-inner-container" >
		<div class="success-message-close-x " onclick="hideSaveOrUpdateDefaultPageViewMsg( this )">X</div>
		<div class="success-message-text" >Default Saved</div>
	</div>
</div>