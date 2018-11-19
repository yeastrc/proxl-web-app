


		<%-- !!!   Handlebars template   !!!!!!!!!   --%>
		
		
	
	<%--  Protein Data Per Search Template --%>

<div >
	<table class="tablesorter looplink_protein_table_jq " style="width:80%">
	
	  <thead>
		<tr>
<%-- 
			<th style="text-align:left;width:{{ pageFormatting.nameWidthPercent }}%;font-weight:bold;">Name</th>
--%>		
			<th style="text-align:left;width:60%;font-weight:bold;">Name</th>
			
			<th class="integer-number-column-header" style="width:10%;font-weight:bold;">Peptides</th>
			<th class="integer-number-column-header" style="width:10%;font-weight:bold;">Unique peptides</th>
			<th class="integer-number-column-header" style="width:10%;font-weight:bold;">Psms</th>

				{{#each peptideAnnotationDisplayNameDescriptionList}}
				 	<th style="text-align:left;font-weight:bold;"
				 		><span style="white-space: nowrap">Best Peptide</span>
				 		<span style="white-space: nowrap" <%-- TODO Add Description as tool tip, {{this.description}} --%>
				 			>{{this.displayName}}</span></th>
				{{/each}}

				{{#each psmAnnotationDisplayNameDescriptionList}}
				 	<th style="text-align:left;font-weight:bold;"
				 		><span style="white-space: nowrap">Best PSM</span>
				 		<span style="white-space: nowrap" <%-- TODO Add Description as tool tip, {{this.description}} --%>
				 			>{{this.displayName}}</span></th>
				{{/each}}					
		</tr>
	  </thead>
	  
	 </table>

	<div class=" data_per_search_between_searches_html_jq " style="display: none;"> <%-- This is a template to be inserted --%>
	
		<div class=" data-per-search-between-searches " ></div>
	</div>
</div>	 
