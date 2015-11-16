


		<%-- !!!   Handlebars template   !!!!!!!!!   --%>
		

		<tr 
			style="cursor: pointer; "
			onclick="viewPsmsLoadedFromWebServiceTemplate.showHidePsms( { clickedElement : this } )"
			
			<%--  Values to pass to get PSMs for this reported peptide --%>
			reported_peptide_id="{{ data.reportedPeptide.id }}"
			search_id="{{ searchId }}"
			project_id="${ projectId }"  <%-- JSP EL value --%>
			peptide_q_value_cutoff="${ peptideQValueCutoff }"  <%-- JSP EL value --%>
			psm_q_value_cutoff="${ psmQValueCutoff }"  <%-- JSP EL value --%>
		>
			 
			<td>{{ data.reportedPeptide.sequence }}</td>
			<td style="text-align:left;" >{{ data.peptide1.sequence }}</td>
			<td class="integer-number-column" style="" >{{ data.peptide1Position }}</td>
			<td style="text-align:left;" >{{ data.peptide2.sequence }}</td>
			<td class="integer-number-column" style="" >{{ data.peptide2Position }}</td>
			
			{{#if qvalueSetAnyRows}} <%-- Only show peptide q value column if any peptide q value are not null --%>
				<td style="text-align:left; white-space: nowrap" >{{ data.qvalue }}</td>
			{{/if}}
			
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
		</tr>