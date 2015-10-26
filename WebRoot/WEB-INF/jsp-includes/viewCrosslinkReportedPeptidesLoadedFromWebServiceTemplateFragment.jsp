


<%-- viewCrosslinkReportedPeptidesLoadedFromWebServiceTemplateFragment.jsp --%>



<%-- 
	bestPsmQValue: 0
	numPsms: 1
	pValue: 0
	pValuePopulated: false
	pep: 0
	pepPopulated: false
	peptide1: Object
	peptide1Position: 1
	peptide1ProteinPositions: Array[2]
	peptide1ProteinPositionsString: "gi|24638940|ref|NP_726693.1|(136), Skp1dd-human(123)"
	peptide2: Object
	peptide2Position: 7
	peptide2ProteinPositions: Array[2]
	peptide2ProteinPositionsString: "gi|24638940|ref|NP_726693.1|(127), Skp1dd-human(114)"
	peptideQValueCutoff: 0.01
	psmQValueCutoff: 0.01
	qvalue: 0
	reportedPeptide: Object
		c: "-"
		id: 1012571
		n: "-"
	sequence: "TVANMIKGK(7)--KTFNIK(1)"
	__proto__: Object
	singlePsmId: 267427
	svmScore: 0
	svmScorePopulated: false
--%>

<%-- 
	Reported peptide	Peptide 1	Pos	Peptide 2	Pos	Protein 1	Protein 2	Q-value	# PSMs			
--%>

	
	<%--  Crosslink Peptide Template --%>


		
		<script id="crosslink_peptide_block_template"  type="text/x-handlebars-template">

			<%--  include the template text  --%>
			<%@ include file="/WEB-INF/jsp_template_fragments/For_jsp_includes/viewCrosslinkReportedPeptidesLoadedFromWebServiceTemplateFragment.jsp_templates/crosslink_peptide_block_template.jsp" %>

		</script>
	

	<%--  Crosslink Peptide Entry Template --%>



		<%-- !!!   Handlebars template:  Crosslink Peptide Entry Template  !!!!!!!!!   --%>
		
		
		<script id="crosslink_peptide_data_row_entry_template"  type="text/x-handlebars-template">

			<%--  include the template text  --%>
			<%@ include file="/WEB-INF/jsp_template_fragments/For_jsp_includes/viewCrosslinkReportedPeptidesLoadedFromWebServiceTemplateFragment.jsp_templates/crosslink_peptide_data_row_entry_template.jsp" %>

		</script>



	<%--  Crosslink Peptide Child row Entry Template --%>

		
		<script id="crosslink_peptide_child_row_entry_template"  type="text/x-handlebars-template">

			<%--  include the template text  --%>
			<%@ include file="/WEB-INF/jsp_template_fragments/For_jsp_includes/viewCrosslinkReportedPeptidesLoadedFromWebServiceTemplateFragment.jsp_templates/crosslink_peptide_child_row_entry_template.jsp" %>

		</script>

	

	