
<%--  viewPeptidesRelatedToPSMsByScanId.jsp  --%>


		<script type="text/javascript" src="${ contextPath }/js/viewPeptidesRelatedToPSMsByScanId.js"></script>
	

		



	<%--  View Peptides Related to PSM Info Overlay Div ,  Click on a line for a link and this div pops up --%>


	
	
	<%--  View Peptides Related to PSM Info Overlay Background --%>
	
	
	
	<div id="view_data_related_to_psm_data_modal_dialog_overlay_background" class="view-data-related-to-psm-data-modal-dialog-overlay-background" style="display: none;"  >
	
	</div>
	
	
	<%--  View Peptides Related to PSM Info Overlay Div --%>

	
	<div id="view_data_related_to_psm_data_overlay_div" class=" view-data-related-to-psm-data-overlay-div " style="display: none; "  >
	
	
		<div id="view-data-related-to-psm-data-overlay-header" class="view-data-related-to-psm-data-overlay-header" style="width:100%; " >
			<h1 id="view-data-related-to-psm-data-overlay-X-for-exit-overlay" class="view-data-related-to-psm-data-overlay-X-for-exit-overlay" >X</h1>
			<h1 id="view-data-related-to-psm-data-overlay-header-text" class="view-data-related-to-psm-data-overlay-header-text" >Peptides for Scan</h1>
		</div>
		<div id="view-data-related-to-psm-data-overlay-body" class="view-data-related-to-psm-data-overlay-body" >
		
		
			<div id="view_data_related_to_psm_data_overlay_data_container"  >
		
		
		
		
			</div>			
		  
		</div>
	</div>
	

	
	
	<%--  Peptides Related to PSM Table Template --%>


	<script id="peptides_related_to_psm_block_template"  type="text/x-handlebars-template">

			<%--  include the template text  --%>
			<%@ include file="/WEB-INF/jsp_template_fragments/For_jsp_includes/viewPeptidesRelatedToPSMsByScanId.jsp_templates/peptides_related_to_psm_block_template.jsp" %>

	</script>
	

	<%--  Peptides Related to PSM Entry Template --%>
			
	<script id="peptides_related_to_psm_row_entry_template"  type="text/x-handlebars-template">

			<%--  include the template text  --%>
			<%@ include file="/WEB-INF/jsp_template_fragments/For_jsp_includes/viewPeptidesRelatedToPSMsByScanId.jsp_templates/peptides_related_to_psm_row_entry_template.jsp" %>

	</script>
				


	<%--  Peptides Related to PSM Entry Template --%>
			
	<script id="peptides_related_to_psm_child_row_template"  type="text/x-handlebars-template">

			<%--  include the template text  --%>
			<%@ include file="/WEB-INF/jsp_template_fragments/For_jsp_includes/viewPeptidesRelatedToPSMsByScanId.jsp_templates/peptides_related_to_psm_child_row_template.jsp" %>

	</script>
								
				

			
			
			
			
			
