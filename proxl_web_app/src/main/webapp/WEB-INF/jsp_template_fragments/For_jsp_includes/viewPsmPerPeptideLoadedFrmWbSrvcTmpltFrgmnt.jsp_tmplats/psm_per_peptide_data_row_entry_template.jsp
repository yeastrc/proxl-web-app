
<%--  psm_per_peptide_data_row_entry_template.jsp   --%>


		<%-- !!!   Handlebars template   !!!!!!!!!   --%>
		
	<%--  PSM Per Peptide Entry Template --%>

	

<tr  class=""  >
	<td style="white-space: nowrap; "  class=""  
			>{{ peptideRow.peptideSequence }}</td>

	{{#if peptideLinkPosition_1_AnyRows}}
		<td style="text-align: left; white-space: nowrap; "  class=""  
	 			>{{ peptideRow.peptideLinkPosition_1 }}</td>
	{{/if}}
	
	{{#if peptideLinkPosition_2_AnyRows}}
		<td style="text-align: left; white-space: nowrap; "  class=""  
	 			>{{ peptideRow.peptideLinkPosition_2 }}</td>
	{{/if}}
	
	<td style=" "  class=""  
			>{{ mods }}</td>

	{{#each  peptideRow.annotationValues }}
		<td style="text-align: left; white-space: nowrap; "  class=""  
	 			>{{ this }}</td>
	{{/each}}
			

</tr>

	  