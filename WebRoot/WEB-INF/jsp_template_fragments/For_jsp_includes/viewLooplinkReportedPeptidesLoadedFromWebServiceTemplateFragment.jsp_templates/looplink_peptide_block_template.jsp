

		<%-- !!!   Handlebars template   !!!!!!!!!   --%>
		
		
	
	<%--  Looplink Peptide Template --%>

<div > 

	<table  class=" tablesorter looplink_peptide_table_jq " style="width:60%"  >
		<thead>
		<tr>
				<th style="text-align:left; font-weight:bold;">Reported peptide</th>
				<th style="text-align:left; font-weight:bold;">Peptide</th>
				<th class="integer-number-column-header" style="font-weight:bold; white-space: nowrap;">Pos 1</th>
				<th class="integer-number-column-header" style="font-weight:bold; white-space: nowrap;">Pos 2</th>


				{{#each peptideAnnotationDisplayNameDescriptionList}}
				 	<th style="text-align:left;font-weight:bold;"
				 		><span style="white-space: nowrap" <%-- TODO Add Description as tool tip, {{this.description}} --%>
				 			>{{this.displayName}}</span></th>
				{{/each}}
				
				
				<th class="integer-number-column-right-most-column-no-ts-header" style="font-weight:bold; white-space: nowrap;"># PSMs</th>
				

					<%-- Only show column if any values are not null --%>
				{{#if showNumberUniquePSMs}} 
					<th class=" integer-number-column-right-most-column-no-ts-header " style="font-weight:bold; white-space: nowrap;"># Unique</th>
				{{/if}}	

				{{#each psmAnnotationDisplayNameDescriptionList}}
				 	<th style="text-align:left;font-weight:bold;"
				 		><span style="white-space: nowrap">Best PSM</span>
				 		<span style="white-space: nowrap" <%-- TODO Add Description as tool tip, {{this.description}} --%>
				 			>{{this.displayName}}</span></th>
				{{/each}}					
		</tr>
		</thead>
		<tbody></tbody>
	</table>
</div>