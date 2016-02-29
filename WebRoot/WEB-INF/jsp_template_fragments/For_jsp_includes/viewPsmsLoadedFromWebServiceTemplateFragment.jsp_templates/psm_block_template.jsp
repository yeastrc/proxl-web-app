

		<%-- !!!   Handlebars template   !!!!!!!!!   --%>
		
		
	
	<%--  PSM Template --%>

<div > <%--  top level div in the template --%>

	<table  class=" tablesorter psm_table_jq" style="width:60%"  >
		<thead>
		<tr>
			{{#if scanDataAnyRows}}
				<th style="text-align:left;font-weight:bold;"><!-- spectrum link for Lorikeet --></th>
			{{/if}}
<%-- 					
					<th style="text-align:left;font-weight:bold;" class=" percolatorPsm_columns_jq " >PSM ID</th>
--%>					
			{{#if scanDataAnyRows}}
				<th style="text-align:right;font-weight:bold;" class="integer-number-column"><span style="white-space: nowrap">Scan Num.</span></th>
			{{/if}}
			
			{{#if scanDataAnyRows}}
				<th style="text-align:right;font-weight:bold;" class=""><span style="white-space: nowrap">U</span></th>
			{{/if}}


			{{#if chargeDataAnyRows}}
				<th style="text-align:right;font-weight:bold;" class="integer-number-column"><span style="white-space: nowrap">Charge</span></th>
			{{/if}}


			{{#if scanDataAnyRows}}
				<th style="text-align:right;font-weight:bold;" class="integer-number-column"><span style="white-space: nowrap">Obs. m/z</span></th>
				<th style="text-align:right;font-weight:bold;" class="integer-number-column"><span style="white-space: nowrap">RT (min)<%-- Retention Time --%></span></th>
				<th style="text-align:left;font-weight:bold;"><span style="white-space: nowrap">Scan Filename</span></th>
			{{/if}}

			
			
			{{#each annotationDisplayNameDescriptionList}}
			 	<th style="text-align:left;font-weight:bold;"
			 		><span style="white-space: nowrap" <%-- TODO Add Description as tool tip, {{this.description}} --%>
			 			>{{this.displayName}}</span></th>
			{{/each}}
			
			
		</tr>
		</thead>
		<tbody></tbody>
	</table>
</div>		