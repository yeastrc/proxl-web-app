


<%-- viewPsmsLoadedFromWebServiceTemplateFragment.jsp --%>

	<%--  PSM Table Template --%>


	<script id="psm_block_template"  type="text/x-handlebars-template">

			<%--  include the template text  --%>
			<%@ include file="/WEB-INF/jsp_template_fragments/For_jsp_includes/viewPsmsLoadedFromWebServiceTemplateFragment.jsp_templates/psm_block_template.jsp" %>

	</script>
	

	<%--  PSM Entry Template --%>
			
	<script id="psm_data_row_entry_template"  type="text/x-handlebars-template">

			<%--  include the template text  --%>
			<%@ include file="/WEB-INF/jsp_template_fragments/For_jsp_includes/viewPsmsLoadedFromWebServiceTemplateFragment.jsp_templates/psm_data_row_entry_template.jsp" %>

	</script>
	
	
	<script id="psm_data_row_entry_no_annotation_data_no_scan_data_row"  type="text/x-handlebars-template">
		<tr><td>PSM</td></tr>
	
	</script>
	
	
	
	<%-- include the overlay for when click on the "N" for is PSM Unique --%>
	
	<%@ include file="/WEB-INF/jsp-includes/viewPeptidesRelatedToPSMsByScanId.jsp" %>
	