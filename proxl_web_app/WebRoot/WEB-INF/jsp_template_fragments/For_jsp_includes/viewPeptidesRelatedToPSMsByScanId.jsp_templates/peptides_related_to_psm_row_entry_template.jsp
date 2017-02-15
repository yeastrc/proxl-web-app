



		<%-- !!!   Handlebars template   !!!!!!!!!   --%>
		
		
	
	<%--  Reported Peptide in Related PSM Overlay Template --%>



	<tr 
		style="cursor: pointer; "
		onclick="viewPsmsLoadedFromWebServiceTemplate.showHidePsms( { clickedElement : this } )"

		data-initial_scan_id="{{ initial_scan_id }}"  <%-- For the Related Peptides Overlay, this is the initial scan id clicked on  --%>
		data-reported_peptide_id="{{ data.reportedPeptide_Id }}"
		data-project_search_id="{{ projectSearchId }}"
		data-skip_associated_peptides_link="true"
		>
		
		<td class=" {{#if scanIdMatchesInitialScanId }}   highlight-row  {{/if}} " >
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
		
		<td class=" {{#if scanIdMatchesInitialScanId }}   highlight-row  {{/if}} " >{{ data.reportedPeptide_Sequence }}</td>
		<td class=" {{#if scanIdMatchesInitialScanId }}   highlight-row  {{/if}} " >{{ data.peptide_1_Sequence }}</td>
		<td class="integer-number-column {{#if scanIdMatchesInitialScanId }}   highlight-row  {{/if}}" >{{ data.peptide_1_Position }}</td>
		<td class=" {{#if scanIdMatchesInitialScanId }}   highlight-row  {{/if}} " >{{ data.peptide_2_Sequence }}</td>
		<td class="integer-number-column {{#if scanIdMatchesInitialScanId }}   highlight-row  {{/if}}" >{{ data.peptide_2_Position }}</td>
		<td class=" {{#if scanIdMatchesInitialScanId }}   highlight-row  {{/if}} " >
		   {{#each data.peptide_1_ProteinPositions}}
				<span class=" {{#if scanIdMatchesInitialScanId }}   highlight-row  {{/if}}  protein_name_jq " 
					data-protein-id="{{ this.proteinId }}">
					{{ this.name }}{{#if this.position_1 }}({{ this.position_1 }}{{#if this.position_2 }}, {{ this.position_2 }}{{/if}}){{/if}}
				</span>
		   {{/each}}
		</td>
		<td class=" {{#if scanIdMatchesInitialScanId }}   highlight-row  {{/if}} " >
		   {{#each data.peptide_2_ProteinPositions}}
				<span class=" protein_name_jq " data-protein-id="{{ this.proteinId }}">
					{{ this.name }}{{#if this.position_1 }}({{ this.position_1 }}{{#if this.position_2 }}, {{ this.position_2 }}{{/if}}){{/if}}
				</span>
		   {{/each}}
		</td>
		
		<%-- showPeptideQValue removed from JS code
		{{#if showPeptideQValue}}
			<td style="white-space: nowrap" class=" {{#if scanIdMatchesInitialScanId }}   highlight-row  {{/if}} " >{{ data.qValue }}</td>
		{{/if}}
		--%>

	{{#each data.peptideAnnotationValueList}}
			<%--  "../" on "scanIdMatchesInitialScanId" to get to parent context outside of "each" loop --%>
		<td style="text-align: left; white-space: nowrap; "  
				class=" {{#if ../scanIdMatchesInitialScanId }}   highlight-row  {{/if}} "  
	 			>{{this}}</td>
	{{/each}}		
		<td class="integer-number-column {{#if scanIdMatchesInitialScanId }}   highlight-row  {{/if}}" >
			<a class="show-child-data-link  " 
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
		
		<td class="integer-number-column {{#if scanIdMatchesInitialScanId }}   highlight-row  {{/if}}" 
			>{{ data.numUniquePsms }} 
		</td>
			
		<%-- 
		
		<td class=" {{#if scanIdMatchesInitialScanId }}   highlight-row  {{/if}} " >{{ data.bestPsmQValue }}</td>
		
		--%>
		

	{{#each data.psmAnnotationValueList}}
		<td style="text-align: left; white-space: nowrap; "  
			<%--  "../" on "scanIdMatchesInitialScanId" to get to parent context outside of "each" loop --%>
			class=" {{#if ../scanIdMatchesInitialScanId }}   highlight-row  {{/if}}  "  
	 			>{{this}}</td>
	{{/each}}
			
	</tr>

