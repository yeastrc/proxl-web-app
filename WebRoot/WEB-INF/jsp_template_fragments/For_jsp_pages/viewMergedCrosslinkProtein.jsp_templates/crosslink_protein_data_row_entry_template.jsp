

		<%-- !!!   Handlebars template   !!!!!!!!!   --%>
		
		
	
	<%--  Protein Data Per Search Template --%>



<tr id=""
	style="cursor: pointer; "
	
	onclick="viewCrosslinkReportedPeptidesLoadedFromWebServiceTemplate.showHideCrosslinkReportedPeptides( { clickedElement : this })"
	search_id="{{ data.searchId }}"
	project_id="${ projectId }"
	peptide_q_value_cutoff="{{ data.peptideQValueCutoff }}"
	psm_q_value_cutoff="{{ data.psmQValueCutoff }}"
	protein_1_id="{{ data.proteinId_1 }}"
	protein_2_id="{{ data.proteinId_2 }}"
	protein_1_position="{{ data.protein1Position }}"
	protein_2_position="{{ data.protein2Position }}"
>
	<td>{{ data.searchName }}</td>

	<td class="integer-number-column-header"
		><a class="show-child-data-link   " 
			href="javascript:"
			>{{ data.searchProteinCrosslink.numLinkedPeptides }}<span class="toggle_visibility_expansion_span_jq" 
					><img src="${contextPath}/images/icon-expand-small.png" 
						class=" icon-expand-contract-in-data-table "
						></span><span class="toggle_visibility_contraction_span_jq" 
							style="display: none;" 
							><img src="${contextPath}/images/icon-collapse-small.png"
								class=" icon-expand-contract-in-data-table "
								></span>
		</a>
	</td>
	
	<td class="integer-number-column-header">{{ data.searchProteinCrosslink.numUniqueLinkedPeptides }}</td>
	<td class="integer-number-column-header">{{ data.searchProteinCrosslink.numPsms }}</td>
	{{#if bestPeptideQValueSetAnyRows }}
		<td style="white-space: nowrap">{{ data.searchProteinCrosslink.bestPeptideQValue }}</td>
	{{/if}}
	<td style="white-space: nowrap">{{ data.searchProteinCrosslink.bestPSMQValue }}</td>
</tr>


