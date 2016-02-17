
		<%-- !!!   Handlebars template   !!!!!!!!!   --%>
		
	<%--  Looplink Peptide Entry Template --%>

	
 
<tr 
	style="cursor: pointer; "
	onclick="viewPsmsLoadedFromWebServiceTemplate.showHidePsms( { clickedElement : this } )"
	reported_peptide_id="{{ data.reportedPeptide.id }}"
	search_id="{{ searchId }}"
	project_id="${ projectId }"  <%-- JSP EL value --%>
>
	 
	<td>{{ data.reportedPeptide.sequence }}</td>
	<td>{{ data.peptide.sequence }}</td>
	<td class="integer-number-column" style="" >{{ data.peptidePosition1 }}</td>
	<td class="integer-number-column" style="" >{{ data.peptidePosition2 }}</td>
	

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


	{{#if showNumberUniquePSMs}} <%-- Only show column if any values are not null --%>
		<td class="integer-number-column" 
			>{{ data.numUniquePsms }}
		</td>
	{{/if}}	
	
	{{#each data.psmAnnotationValueList}}
		<td style="text-align: left; white-space: nowrap; "  class=" "  
	 			>{{this}}</td>
	{{/each}}	
</tr>