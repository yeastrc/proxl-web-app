
<%--  sharePageURLShortenerButtonFragment.jsp    /WEB-INF/jsp-includes/sharePageURLShortenerButtonFragment.jsp

		This contains the "Share Page" Button

	  This is used by the Javascript code in sharePageURLShortener.js
--%>


<%@page import="org.yeastrc.xlink.www.constants.WebConstants"%>
<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>



	  <div style="display: inline-block; position: relative; ">
	  
		<input type="button" value="Share Page"
			id="sharePageButton"
			class=" tool_tip_attached_jq  "
			data-tooltip="create a shortened link for accessing this page" 
			data-search_id="${ searchId }"
			onclick="sharePageURLShortener.sharePage( { clickedThis : this } )" />
			
	  </div>
	