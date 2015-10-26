
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
						{{#if qvalueSetAnyRows}} <%-- Only show peptide q value column if any peptide q value are not null --%>
						<th style="text-align:left; font-weight:bold;"><span style="white-space: nowrap">Q-value</span></th>
						{{/if}}
						<th class="integer-number-column-right-most-column-no-ts-header" style="font-weight:bold;">#&nbsp;PSMs</th>
				</tr>
				</thead>
				<tbody></tbody>
			</table>
		</div>		
		