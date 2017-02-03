

		<%-- !!!   Handlebars template   !!!!!!!!!   --%>
		
		
	
	<%--  Protein Data Per Search Template --%>

<%-- 
	Include in <script id="crosslink_protein_data_row_entry_template"  type="text/x-handlebars-template">
--%>

<tr id=""
	style="cursor: pointer; "
	
	onclick="viewCrosslinkReportedPeptidesLoadedFromWebServiceTemplate.showHideCrosslinkReportedPeptides( { clickedElement : this })"
	search_id="{{ data.searchId }}"
	protein_1_id="{{ protein_1_id }}"
	protein_2_id="{{ protein_2_id }}"
	protein_1_position="{{ protein_1_position }}"
	protein_2_position="{{ protein_2_position }}"
	ssssssssss="sssssssssee"
>

	<td>{{ data.searchName }}</td>

	<td class="integer-number-column-header"
		><a class="show-child-data-link   " 
			href="javascript:"
			>{{ data.numPeptides }}<span class="toggle_visibility_expansion_span_jq" 
					><img src="${contextPath}/images/icon-expand-small.png" 
						class=" icon-expand-contract-in-data-table "
						></span><span class="toggle_visibility_contraction_span_jq" 
							style="display: none;" 
							><img src="${contextPath}/images/icon-collapse-small.png"
								class=" icon-expand-contract-in-data-table "
								></span>
		</a>
	</td>
	
	<td class="integer-number-column-header">{{ data.numUniquePeptides }}</td>
	<td class="integer-number-column-header">{{ data.numPsms }}</td>

	{{#each data.peptideAnnotationValueList}}
		<td style="text-align: left; white-space: nowrap; "  class=" "  
	 			>{{this}}</td>
	{{/each}}
				

	{{#each data.psmAnnotationValueList}}
		<td style="text-align: left; white-space: nowrap; "  class=" "  
	 			>{{this}}</td>
	{{/each}}				
</tr>


