



		<%-- !!!   Handlebars template   !!!!!!!!!   --%>
		
		
	
	<%--  Reported Peptide in Related PSM Overlay Template --%>



	<tr 
		style="cursor: pointer; "
		onclick="viewPsmsLoadedFromWebServiceTemplate.showHidePsms( { clickedElement : this } )"
		initial_scan_id="{{ initial_scan_id }}"
		reported_peptide_id="{{ data.reportedPeptide_Id }}"
		search_id="{{ searchId }}"
		project_id="{{ projectId }}"
		peptide_q_value_cutoff="{{ peptide_q_value_cutoff }}"
		psm_q_value_cutoff="{{ psm_q_value_cutoff }}"
		skip_associated_peptides_link="true"
		>
		
		<td>
			{{ data.linkType }}
<%-- 										
			<c:choose>
				<c:when test="{{ not empty peptideEntry.searchPeptideCrosslink }}">Crosslink</c:when>
				<c:when test="{{ not empty peptideEntry.searchPeptideLooplink }}">Looplink</c:when>
				<c:when test="{{ not empty peptideEntry.searchPeptideUnlinked }}"
					>Unlinked</c:when>
				<c:when test="{{ not empty peptideEntry.searchPeptideDimer }}"
					>Dimer</c:when>
				<c:otherwise>Unknown</c:otherwise>
			</c:choose>
--%>											
		
		</td>
		
		<td>{{ data.reportedPeptide_Sequence }}</td>
		<td>{{ data.peptide_1_Sequence }}</td>
		<td class="integer-number-column" >{{ data.peptide_1_Position }}</td>
		<td>{{ data.peptide_2_Sequence }}</td>
		<td class="integer-number-column" >{{ data.peptide_2_Position }}</td>
		<td>
		   {{#each data.peptide_1_ProteinPositions}}
				<span class=" protein_name_jq " data-protein-id="{{ this.proteinId }}">
					{{ this.name }}{{#if this.position_1 }}({{ this.position_1 }}{{#if this.position_2 }}, {{ this.position_2 }}{{/if}}){{/if}}
				</span>
		   {{/each}}
		</td>
		<td>
		   {{#each data.peptide_2_ProteinPositions}}
				<span class=" protein_name_jq " data-protein-id="{{ this.proteinId }}">
					{{ this.name }}{{#if this.position_1 }}({{ this.position_1 }}{{#if this.position_2 }}, {{ this.position_2 }}{{/if}}){{/if}}
				</span>
		   {{/each}}
		</td>
		{{#if showPeptideQValue}}
			<td style="white-space: nowrap">{{ data.qValue }}</td>
		{{/if}}

		<td class="integer-number-column" ><a class="show-child-data-link  " 
				href="javascript:"
				>{{ data.numPsms }}<span class="toggle_visibility_expansion_span_jq" 
						><img src="${contextPath }/images/icon-expand-small.png" 
							class=" icon-expand-contract-in-data-table "
							></span><span class="toggle_visibility_contraction_span_jq" 
								style="display: none;" 
								><img src="${contextPath }/images/icon-collapse-small.png"
									class=" icon-expand-contract-in-data-table "
									></span>
			</a>
		</td>
		
		<td class="integer-number-column" 
			>{{ data.numUniquePsms }} 
		</td>
			
		
		
		<td>{{ data.bestPsmQValue }}</td>
	</tr>

