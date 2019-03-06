
<%--  psm_per_peptide_data_row_entry_template.jsp   --%>


		<%-- !!!   Handlebars template   !!!!!!!!!   --%>
		
	<%--  PSM Per Peptide Entry Template --%>

	

<tr  class=""  >

<%--  
	Hide since code not done to display when click on "View Spectrum" 
	{{#if showViewSpectrumLinkColumn}}
		<td style="white-space: nowrap; "  class=" {{#if psm.scanIdMatchesInitialScanId }}   highlight-row  {{/if}} "  
			><a href="javascript:" psmId="{{ psm.psmDTO.id }}" class="view_spectrum_open_spectrum_link_jq" 
					psm_type="{{ psmDTO.type }}"
				>View Spectrum</a></td>
	{{/if}}
 --%>
	
	<td style="white-space: nowrap; "  class=""  
			>{{ peptideRow.peptideSequence }}</td>

	{{#if peptideLinkPosition_1_AnyRows}}
		<td style="text-align: left; white-space: nowrap; "  class=""  
	 			>{{ peptideRow.peptideLinkPosition_1 }}</td>
	{{/if}}
	
	{{#if peptideLinkPosition_2_AnyRows}}
		<td style="text-align: left; white-space: nowrap; "  class=""  
	 			>{{ peptideRow.peptideLinkPosition_2 }}</td>
	{{/if}}
	
	<td style=" "  class=""  
			>{{ mods }}</td>

	{{#if scanNumberAnyRows}}
		<td style="text-align: right; white-space: nowrap; "  class="integer-number-column ">
			{{  peptideRow.scanNumber }}
		</td>
	{{/if}}
	
	{{#if scanDataAnyRows}}
		<td style="text-align: right; white-space: nowrap; " class="integer-number-column " >
			{{ peptideRow.preMZRounded }}
		</td>
	{{/if}}
	
	{{#if scanDataAnyRows}}
		<td style="text-align: right; white-space: nowrap; " class="integer-number-column " >
			{{  peptideRow.retentionTimeMinutesRoundedString }}
		</td>
	{{/if}}

	{{#if scanFilenameAnyRows}}
		<td style="text-align: left; white-space: nowrap; " >
			{{ peptideRow.scanFilename }}
		</td>
	{{/if}}
	
	{{#each  peptideRow.annotationValues }}
		<td style="text-align: left; white-space: nowrap; "  class=""  
	 			>{{ this }}</td>
	{{/each}}
			

</tr>

	  