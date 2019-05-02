
<%--  excludeLinksWith_Remove_NonUniquePSMs_Checkbox_Fragment.jsp

--%>
<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

 <c:choose>
  <c:when test="${ excludeLinksWith_Remove_NonUniquePSMs_Checkbox_searchHasScanData }">
   <label><span style="white-space:nowrap;" >
		<input type="checkbox" id="removeNonUniquePSMs"  
		onchange="if ( window.defaultPageView ) { window.defaultPageView.searchFormChanged_ForDefaultPageView(); }; if ( window.saveView_dataPages ) { window.saveView_dataPages.searchFormChanged_ForSaveView(); }" > 					
	 	 only non-unique PSMs
	 </span></label>
  </c:when>
  <c:otherwise>
   <label ><span style="white-space:nowrap;" class="disabled-checkbox tool_tip_attached_jq " 
			data-tooltip="Not supported since no scans were uploaded">
		<input disabled="disabled" type="checkbox" id="removeNonUniquePSMs"> 					
	 	 only non-unique PSMs
	 </span></label>
  </c:otherwise>
 </c:choose>