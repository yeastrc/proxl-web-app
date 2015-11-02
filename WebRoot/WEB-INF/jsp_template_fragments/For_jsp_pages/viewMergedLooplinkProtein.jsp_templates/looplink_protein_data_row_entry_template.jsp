

		<%-- !!!   Handlebars template   !!!!!!!!!   --%>
		
		
	
	<%--  Protein Data Per Search Template --%>



<tr id=""
	style="cursor: pointer; "
	
	onclick="viewLooplinkReportedPeptidesLoadedFromWebServiceTemplate.showHideLooplinkReportedPeptides( { clickedElement : this })"
	search_id="{{ data.searchId }}"
	project_id="${ projectId }"
	peptide_q_value_cutoff="{{ data.peptideQValueCutoff }}"
	psm_q_value_cutoff="{{ data.psmQValueCutoff }}"
	protein_id="{{ data.proteinId }}"
	protein_position_1="{{ data.proteinPosition1 }}"
	protein_position_2="{{ data.proteinPosition2 }}"
>
	<td>{{ data.searchName }}</td>

	<td class="integer-number-column-header"
		><a class="show-child-data-link   " 
			href="javascript:"
			>{{ data.searchProteinLooplink.numPeptides }}<span class="toggle_visibility_expansion_span_jq" 
					><img src="${contextPath}/images/icon-expand-small.png" 
						class=" icon-expand-contract-in-data-table "
						></span><span class="toggle_visibility_contraction_span_jq" 
							style="display: none;" 
							><img src="${contextPath}/images/icon-collapse-small.png"
								class=" icon-expand-contract-in-data-table "
								></span>
		</a>
	</td>
	
	<td class="integer-number-column-header">{{ data.searchProteinLooplink.numUniquePeptides }}</td>
	<td class="integer-number-column-header">{{ data.searchProteinLooplink.numPsms }}</td>
	{{#if bestPeptideQValueSetAnyRows }}
		<td style="white-space: nowrap">{{ data.searchProteinLooplink.bestPeptideQValue }}</td>
	{{/if}}
	<td style="white-space: nowrap">{{ data.searchProteinLooplink.bestPSMQValue }}</td>
</tr>


