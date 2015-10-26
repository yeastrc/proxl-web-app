


		<%-- !!!   Handlebars template   !!!!!!!!!   --%>
		
		
	
	<%--  Peptide Data Per Search Template --%>



			


<tr class="" 
	id=""
	style="cursor: pointer; "
	onclick="viewPsmsLoadedFromWebServiceTemplate.showHidePsms( { clickedElement : this } )"
	reported_peptide_id="{{ data.reportedPeptide.id }}"
	search_id="{{  data.searchId }}"
	project_id="{{  project_id }}"
	psm_q_value_cutoff="{{  psm_q_value_cutoff }}"
>
	<td>{{ data.searchName }}</td>

	<td>
		{{ data.reportedPeptide.sequence }}
	</td>
	
	{{#if anyLinksHavePeptideQValue }}
		<td style="white-space: nowrap">{{ data.peptideQValue }}</td>
	{{/if}}
	
	{{#if anyLinksHavePeptidePEPValue }}
		<td style="white-space: nowrap">
			{{#if data.pepPopulated }}
			{{/if}}
				{{ data.peptidePEP }}
		</td>
	{{/if}}
	

	{{#if anyLinksHavePeptideSVMValue }}
		<td style="white-space: nowrap">
			{{#if data.svmScorePopulated }}
			{{/if}}
				{{ data.peptideSVMScore }}
		</td>
	{{/if}}

	<td style="text-align: right;" ><a class="show-child-data-link   " 
			href="javascript:"
			>{{ data.numPSMs }}<span class="toggle_visibility_expansion_span_jq" 
					><img src="${contextPath}/images/icon-expand-small.png" 
						class=" icon-expand-contract-in-data-table "
						></span><span class="toggle_visibility_contraction_span_jq" 
							style="display: none;" 
							><img src="${contextPath}/images/icon-collapse-small.png"
								class=" icon-expand-contract-in-data-table "
								></span>
		</a>
	</td>
</tr>

													