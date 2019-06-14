<%--
	body_section_data_pages_after_header_main.jsp
	
	Placed in the <body> section after the include of 'header_main.jsp'.
	
	Included in "Data" Pages: All Project Search Id based pages (Peptide, Protein, QC, Image, Structure) 

--%>

<%-- The Project Search Id / Search Id pairs in JSON from the request, in the Order they are Displayed in Search Details section --%>

<%--  Page variable set in Java class  ProjectSearchIdsSearchIds_SetRequestParameter --%>
<script type="text/text" id="project_search_id_search_id_pairs_display_order_list_json"
	>${ project_search_id_search_id_pairs_display_order_list_json }</script>

