


		<%-- !!!   Handlebars template   !!!!!!!!!   --%>
		
		
	
	<%--  Protein Data Per Search Template --%>

<div >
	<table class="tablesorter crosslink_protein_table_jq " style="width:80%">
	
	  <thead>
		<tr>
<%-- 
			<th style="text-align:left;width:{{ pageFormatting.nameWidthPercent }}%;font-weight:bold;">Name</th>
--%>		
			<th style="text-align:left;width:60%;font-weight:bold;">Name</th>
			
			<th class="integer-number-column-header" style="width:10%;font-weight:bold;">Peptides</th>
			<th class="integer-number-column-header" style="width:10%;font-weight:bold;">Unique peptides</th>
			<th class="integer-number-column-header" style="width:10%;font-weight:bold;">Psms</th>
			
			{{#if bestPeptideQValueSetAnyRows }}
				<th style="text-align:left;width:10%;font-weight:bold;"><span style="white-space: nowrap">Best Peptide</span> <span style="white-space: nowrap">Q-value</span></th>
			{{/if}}
			
			<th style="text-align:left;width:10%;font-weight:bold;"><span style="white-space: nowrap">Best PSM</span> <span style="white-space: nowrap">Q-value</span></th>
		
		</tr>
	  </thead>
	  
	 </table>

</div>	 
