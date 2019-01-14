

		<%-- !!!   Handlebars template   !!!!!!!!!   --%>
		
	<%-- all_protein_data_row_entry_template.jsp --%>
	
	<%--  Protein Data Per Search Template --%>



<tr id=""
	style="cursor: pointer; "
	
	onclick="viewReportedPeptidesForProteinAllLoadedFromWebServiceTemplate.showHideReportedPeptides( { clickedElement : this })"
	data-project_search_id="{{ data.projectSearchId }}"
	data-protein_id="{{ protein_id }}"
>

	<td>{{ data.searchName }}</td>

	<td class="integer-number-column-header"
		><a class="show-child-data-link   " 
			href="javascript:"
			>{{ data.numPeptides }}<span class="toggle_visibility_expansion_span_jq" 
					><img src="images/icon-expand-small.png" 
						class=" icon-expand-contract-in-data-table "
						></span><span class="toggle_visibility_contraction_span_jq" 
							style="display: none;" 
							><img src="images/icon-collapse-small.png"
								class=" icon-expand-contract-in-data-table "
								></span>
		</a>
	</td>
	
	<td class="integer-number-column-header">{{ data.numUniquePeptides }}</td>
	<td class="integer-number-column-header">{{ data.numPsms }}</td>

	{{#each data.peptideAnnotationValueList}}
		<td style="text-align: left; white-space: nowrap; "  class=" "  
	 			>{{this}}</td>
	{{/each}}
				

	{{#each data.psmAnnotationValueList}}
		<td style="text-align: left; white-space: nowrap; "  class=" "  
	 			>{{this}}</td>
	{{/each}}
	
</tr>


