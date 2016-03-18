
<%--  protein_data_per_search_block_template.jsp --%>

		<%-- !!!   Handlebars template   !!!!!!!!!   --%>
		
		
	
	<%--  Protein Data Per Search Template --%>
	
	<%--  For <script id="protein_data_per_search_block_template"  type="text/x-handlebars-template"> --%>

<div >
		
	<table  class="tablesorter  top_data_table_jq  link_info_table_jq " >
		<thead>
		<tr>
			<th data-tooltip="Search Name"
				class=" tool_tip_attached_jq "
				style="text-align:left;width:60%;font-weight:bold;">Name</th>
			
			<th  data-tooltip="Number of distinct peptides showing link"
				class="integer-number-column-header tool_tip_attached_jq " style="width:10%;font-weight:bold;">Peptides</th>
				
			<th data-tooltip="Number of found peptides that uniquely map to this protein from the FASTA file"
			 	class="integer-number-column-header tool_tip_attached_jq " style="width:10%;font-weight:bold;">Unique peptides</th>
			 	
			<th data-tooltip="Number of PSMs matched to this peptide (or linked pair)"
				class="integer-number-column-header tool_tip_attached_jq " style="width:10%;font-weight:bold;">Psms</th>


				{{#each peptideAnnotationDisplayNameDescriptionList}}
				 	<th data-tooltip="Best Peptide-level {{this.displayName}} for this peptide (or linked pair)"
				 		class=" tool_tip_attached_jq " 
				 		style="text-align:left;font-weight:bold;"
				 		><span style="white-space: nowrap">Best Peptide</span>
				 		<span style="white-space: nowrap" <%-- TODO Add Description as tool tip, {{this.description}} --%>
				 			>{{this.displayName}}</span></th>
				{{/each}}

				{{#each psmAnnotationDisplayNameDescriptionList}}
				 	<th data-tooltip="Best PSM-level {{this.displayName}} for PSMs matched to peptides that show this link"
				 		class=" tool_tip_attached_jq " 
				 		style="text-align:left;font-weight:bold;"
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
