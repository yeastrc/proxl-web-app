
<%--  excludeLinksWith_Checkboxes_ProteinPages_Fragment.jsp

	Set of Checkbox choices for "Exclude links with:" in filter for pages:
		Protein, merged protein, Coverage, Image, Structure

--%>
<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

<!-- In Page fragment -->
 <label><span style="white-space:nowrap;" >
	<input type="checkbox" id="filterNonUniquePeptides" 
	onchange=" if ( window.defaultPageView ) { window.defaultPageView.searchFormChanged_ForDefaultPageView(); }; if ( window.saveView_dataPages ) { window.saveView_dataPages.searchFormChanged_ForSaveView(); }" > 					
 	 no unique peptides
 </span></label>
 <label><span style="white-space:nowrap;" >
	<input type="checkbox" id="filterOnlyOnePeptide"  
	onchange=" if ( window.defaultPageView ) { window.defaultPageView.searchFormChanged_ForDefaultPageView(); }; if ( window.saveView_dataPages ) { window.saveView_dataPages.searchFormChanged_ForSaveView(); }" > 					
 	 only one peptide
 </span></label>
 <%--  Checkbox for removeNonUniquePSMs --%>
<%@ include file="/WEB-INF/jsp-includes/excludeLinksWith_Remove_NonUniquePSMs_Checkbox_Fragment.jsp" %>