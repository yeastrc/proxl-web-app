
		<%-- !!!   Handlebars template   !!!!!!!!!   --%>
		
	<%--  Crosslink Peptide Template --%>

<div > <%--  top level div in the template --%>

	<table  class=" tablesorter crosslink_peptide_table_jq " style="width:60%"  >
		<thead>
		<tr>
				<th style="text-align:left; font-weight:bold;">Reported peptide</th>
				<th style="text-align:left; font-weight:bold;">Peptide 1</th>
				<th class="integer-number-column-header" style=" font-weight:bold;">Pos</th>
				<th style="text-align:left; font-weight:bold;">Peptide 2</th>
				<th class="integer-number-column-header" style=" font-weight:bold;">Pos</th>
				

				{{#each peptideAnnotationDisplayNameDescriptionList}}
				 	<th style="text-align:left;font-weight:bold;"
				 		><span style="white-space: nowrap" <%-- TODO Add Description as tool tip, {{this.description}} --%>
				 			>{{this.displayName}}</span></th>
				{{/each}}
				
				<th class="integer-number-column-right-most-column-no-ts-header" style="font-weight:bold; white-space: nowrap;"># PSMs</th>

				
					<%-- Only show column if any values are not null --%>
				{{#if showNumberNonUniquePSMs}} 
					<th class=" integer-number-column-right-most-column-no-ts-header " style="font-weight:bold; white-space: nowrap;"># Non-unique</th>
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
