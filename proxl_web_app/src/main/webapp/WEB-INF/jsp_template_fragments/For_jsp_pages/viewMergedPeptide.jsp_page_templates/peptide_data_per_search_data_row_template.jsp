<%--
		peptide_data_per_search_data_row_template.jsp

		!!!   Handlebars template   !!!!!!!!!   

	Peptide Data Per Search Template 
	
--%>

<tr class="" 
	id=""
	style="cursor: pointer; "
	onclick="viewPsmsLoadedFromWebServiceTemplate.showHidePsms( { clickedElement : this } )"
	data-reported_peptide_id="{{ data.reportedPeptide.id }}"
	data-project_search_id="{{  data.searchId }}"
	>
	<td>{{ data.searchName }}</td>
	<td>
		{{ data.reportedPeptide.sequence }}
	</td>
	
	{{#each data.peptideAnnotationValues}}
		<td style="text-align: left; white-space: nowrap; "  class="  "  
	 			>{{this}}</td>
	{{/each}}
			
	<td style="text-align: right;" ><a class="show-child-data-link   " 
			href="javascript:"
			>{{ data.numPSMs }}<span class="toggle_visibility_expansion_span_jq" 
					><img src="images/icon-expand-small.png" 
						class=" icon-expand-contract-in-data-table "
						></span><span class="toggle_visibility_contraction_span_jq" 
							style="display: none;" 
							><img src="images/icon-collapse-small.png"
								class=" icon-expand-contract-in-data-table "
								></span>
		</a>
	</td>
	

	{{#each data.psmAnnotationValues}}
		<td style="text-align: left; white-space: nowrap; "  class="  "  
	 			>{{this}}</td>
	{{/each}}	
</tr>

													