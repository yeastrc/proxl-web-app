


		<%-- !!!   Handlebars template   !!!!!!!!!   --%>
		
		
	
	<%--  Protein Data Per Search Template --%>

<div >





		
	<table  class="tablesorter  top_data_table_jq  link_info_table_jq " >
		<thead>
		<tr>
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
		<tbody id="link_info_table__tbody"></tbody>
	</table>


	<div class=" data_per_search_between_searches_html_jq " style="display: none;"> <%-- This is a template to be inserted --%>
	
		<div class=" data-per-search-between-searches " ></div>
	</div>


</div>	 
