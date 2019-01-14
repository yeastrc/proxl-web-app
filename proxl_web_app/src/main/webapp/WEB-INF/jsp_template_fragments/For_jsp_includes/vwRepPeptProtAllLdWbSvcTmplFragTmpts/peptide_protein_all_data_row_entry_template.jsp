


		<%-- !!!   Handlebars template   !!!!!!!!!   --%>
		

<tr 
	style="cursor: pointer; "
	onclick="viewPsmsLoadedFromWebServiceTemplate.showHidePsms( { clickedElement : this } )"
	
	<%--  Values to pass to get PSMs for this reported peptide --%>
	
	data-reported_peptide_id="{{ data.reportedPeptide.id }}"
	data-project_search_id="{{ projectSearchId }}"
	>

	<%-- Link Type --%>	 
	{{#if data.linkTypeCrosslink}} 
		<td>crosslink</td>
	{{/if}}
	{{#if data.linkTypeLooplink}} 
		<td>looplink</td>
	{{/if}}
	{{#if data.linkTypeUnlinked}} 
		<td>unlinked</td>
	{{/if}}
	{{#if data.linkTypeDimer}} 
		<td>dimer</td>
	{{/if}}

	<td>{{ data.reportedPeptide.sequence }}</td>

	{{#each data.peptideAnnotationValueList}}
		<td style="text-align: left; white-space: nowrap; "  class=" "  
	 			>{{this}}</td>
	{{/each}}
				
	<td class="integer-number-column-right-most-column-no-ts" style="" >
		<a class="show-child-data-link   "
			href="javascript:"
			>{{ data.numPsms }}<%-- << actual data in the cell --%><span class="toggle_visibility_expansion_span_jq" 
						style="" 
					><img src="images/icon-expand-small.png" 
						class=" icon-expand-contract-in-data-table "
						></span><span class="toggle_visibility_contraction_span_jq" 
							style="display: none; " 
							><img src="images/icon-collapse-small.png"
								class=" icon-expand-contract-in-data-table "
								></span>
		</a>
	</td>												


	<%-- Only show column if any values are not null --%>
	{{#if showNumberNonUniquePSMs}} 
		<td class="integer-number-column {{#if data.numNonUniquePsms }} highlight-cell {{/if}}" 
			>{{ data.numNonUniquePsms }}
		</td>
	{{/if}}
	

	{{#each data.psmAnnotationValueList}}
		<td style="text-align: left; white-space: nowrap; "  class=" "  
	 			>{{this}}</td>
	{{/each}}
				
</tr>