
		<%-- !!!   Handlebars template   !!!!!!!!!   --%>
	
	<%--  Protein Data Per Search Template --%>
<%-- 
containing div id:  protein_data_per_search_data_row_entry_template
--%>
			
<tr class=" main_data_row_jq " 
	style="cursor: pointer; " 
	show_children_if_one_row="true"
	data-project_search_id="{{ projectSearchId }}"

  {{#if isLooplink }}
	onclick="viewLooplinkReportedPeptidesLoadedFromWebServiceTemplate.showHideLooplinkReportedPeptides( { clickedElement : this })" 
	data-protein_id="{{ from_protein_id }}"
	data-protein_position_1="{{ from_protein_position }}"
	data-protein_position_2="{{ to_protein_position }}"
	data-children_mgmt_object="viewLooplinkReportedPeptidesLoadedFromWebServiceTemplate"
  {{/if}}

  {{#if isCrosslink }}
	onclick="viewCrosslinkReportedPeptidesLoadedFromWebServiceTemplate.showHideCrosslinkReportedPeptides( { clickedElement : this })" 
	data-protein_1_id="{{ from_protein_id }}"
	data-protein_2_id="{{ to_protein_id }}"
	data-protein_1_position="{{ from_protein_position }}"
	data-protein_2_position="{{ to_protein_position }}"
	data-children_mgmt_object="viewCrosslinkReportedPeptidesLoadedFromWebServiceTemplate"
  {{/if}}  
  
  
  {{#if isMonolink }}
	onclick="viewMonolinkReportedPeptidesLoadedFromWebServiceTemplate.showHideMonolinkReportedPeptides( { clickedElement : this })" 
	data-protein_id="{{ from_protein_id }}"
	data-protein_position="{{ from_protein_position }}"
	data-children_mgmt_object="viewMonolinkReportedPeptidesLoadedFromWebServiceTemplate"
  {{/if}}
  
	>
			
			<td>{{ data.searchName }}</td>
			
			<td class="integer-number-column" style="" >
				<a class="show-child-data-link   "
					href="javascript:"
					>{{ data.numPeptides }}<%-- << actual data in the cell --%><span class=" toggle_visibility_expansion_span_jq" 
								style="" 
							><img src="${contextPath}/images/icon-expand-small.png" 
								class=" icon-expand-contract-in-data-table "
								></span><span class="toggle_visibility_contraction_span_jq" 
									style=" display: none;" 
									><img src="${contextPath}/images/icon-collapse-small.png"
										class=" icon-expand-contract-in-data-table "
										></span>
				</a>
			</td>												
			
			<td class="integer-number-column" style="" >{{ data.numUniquePeptides }}</td>
			
			<td class="integer-number-column" style="" >{{ data.numPsms }}</td>
			

	{{#each data.peptideAnnotationValueList}}
		<td style="text-align: left; white-space: nowrap; "  class=" "  
	 			>{{this}}</td>
	{{/each}}
				

	{{#each data.psmAnnotationValueList}}
		<td style="text-align: left; white-space: nowrap; "  class=" "  
	 			>{{this}}</td>
	{{/each}}	
				
		</tr>

		
		