


		<%-- !!!   Handlebars template   !!!!!!!!!   --%>
		

<tr 
	style="cursor: pointer; "
	onclick="viewPsmsLoadedFromWebServiceTemplate.showHidePsms( { clickedElement : this } )"
	
	<%--  Values to pass to get PSMs for this reported peptide --%>
	
	data-reported_peptide_id="{{ data.reportedPeptide.id }}"
	data-project_search_id="{{ projectSearchId }}"
	>
	 
	<td>{{ data.reportedPeptide.sequence }}</td>
	<td style="text-align:left;" >{{ data.peptide1.sequence }}</td>
	<td class="integer-number-column" style="" >{{ data.peptide1Position }}</td>
	<td style="text-align:left;" >{{ data.peptide2.sequence }}</td>
	<td class="integer-number-column" style="" >{{ data.peptide2Position }}</td>
	

	{{#each data.peptideAnnotationValueList}}
		<td style="text-align: left; white-space: nowrap; "  class=" "  
	 			>{{this}}</td>
	{{/each}}
				
	<td class="integer-number-column-right-most-column-no-ts" style="" >
		<a class="show-child-data-link   "
			href="javascript:"
			>{{ data.numPsms }}<%-- << actual data in the cell --%><span class="toggle_visibility_expansion_span_jq" 
						style="" 
					><img src="${contextPath}/images/icon-expand-small.png" 
						class=" icon-expand-contract-in-data-table "
						></span><span class="toggle_visibility_contraction_span_jq" 
							style="display: none; " 
							><img src="${contextPath}/images/icon-collapse-small.png"
								class=" icon-expand-contract-in-data-table "
								></span>
		</a>
	</td>												


	<%-- Only show column if any values are not null --%>
	{{#if showNumberUniquePSMs}} 
		<td class="integer-number-column" 
			>{{ data.numUniquePsms }}
		</td>
	{{/if}}
	

	{{#each data.psmAnnotationValueList}}
		<td style="text-align: left; white-space: nowrap; "  class=" "  
	 			>{{this}}</td>
	{{/each}}
				
</tr>