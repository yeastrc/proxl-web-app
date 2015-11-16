


<%-- viewPsmsLoadedFromWebServiceTemplateFragment.jsp --%>



<%--
//		psms: Array[4]
//			0: Object
//			calcMass: 0
//			charge: 2
//			chargeSet: true
//			id: 382114
//			pep: 0.0009191
//			reportedPeptideId: 1012223
//			searchId: 308
//			psmId: "T-28418.pf.2015-01-08-Q_2013_1016_RJ_08-relD500odb-percolatorIn.txt"
//			qValue: 0.001051
//			scanId: 635969
//			svmScore: 0.733
//			type: 2
--%>					
	
	
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
	
	
	<%-- include the overlay for when click on the "N" for is PSM Unique --%>
	
	<%@ include file="/WEB-INF/jsp-includes/viewPeptidesRelatedToPSMsByScanId.jsp" %>
	