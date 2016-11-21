
<%--  sharePageURLShortenerButtonFragment.jsp    /WEB-INF/jsp-includes/sharePageURLShortenerButtonFragment.jsp

		This contains the "Share Page" Button

	  This is used by the Javascript code in sharePageURLShortener.js
	  
	  Page variable "search_id_comma_delim_list" is set in Java class GetSearchDetailsData
--%>


<%@page import="org.yeastrc.xlink.www.constants.WebConstants"%>
<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>



	  <div style="display: inline-block; position: relative; ">
	  
		<input type="button" value="Share Page"
			id="sharePageButton"
			class=" tool_tip_attached_jq  "
			data-tooltip="Create and share a link to this view of the data." 
			data-search_id_comma_delim_list="${ search_id_comma_delim_list }"
			onclick="sharePageURLShortener.sharePage( { clickedThis : this } )" />
			
	  </div>
	