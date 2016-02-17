
		<%-- !!!   Handlebars template   !!!!!!!!!   --%>
		
	<%--  PSM Entry Template --%>

	

<tr  class=" {{#if scanIdMatchesInitialScanId }}   highlight-row  {{/if}} "  >
  <%-- 
	<td><a href="javascript:" psmId="{{id}}" class="view_spectrum_open_spectrum_link_jq">{{psmId}}</a></td>
  --%>

	{{#if scanDataAnyRows}}
		<td style="white-space: nowrap; "  class=" {{#if scanIdMatchesInitialScanId }}   highlight-row  {{/if}} "  
			><a href="javascript:" psmId="{{psmDTO.id}}" class="view_spectrum_open_spectrum_link_jq" 
					psm_type="{{psmDTO.type}}"
				>View Spectrum</a></td>
	{{/if}}
<%-- 
			<td style="text-align: left; white-space: nowrap; " class=" percolatorPsm_columns_jq " >{{psmDTO.percolatorPsm.psmId}}</td>
--%>			
	{{#if scanDataAnyRows}}
		<td style="text-align: right; white-space: nowrap; "  class="integer-number-column {{#if scanIdMatchesInitialScanId }}   highlight-row  {{/if}}">
			{{scanNumber}}
		</td>
	{{/if}}
	
	{{#if scanDataAnyRows}}
		<td style="text-align: right; white-space: nowrap; "  class="integer-number-column {{#if scanIdMatchesInitialScanId }}   highlight-row  {{/if}}">
			{{#if uniquePSM}}
			
			<%--  TEMP --%>
			<%-- 
				{{#if show_associated_peptides_link_true }}
				 {{#if psmDTO.scanId }}
				  <a href="javascript:" psm_id="{{ psmDTO.id }}" scan_id="{{ psmDTO.scanId }}" 
				  	initial_reported_peptide_id="{{ reported_peptide_id }}"
					peptide_q_value_cutoff="{{ peptide_q_value_cutoff }}" psm_q_value_cutoff="{{ psm_q_value_cutoff }}"
					project_id="{{ project_id }}" search_id="{{ search_id }}"
				 	onclick="viewPeptidesRelatedToPSMsByScanId.openOverlayForPeptidesRelatedToPSMsByScanId( { clickedElement : this } )"
				 	>{{/if}}{{/if}}TEMP_N{{#if show_associated_peptides_link_true }}{{#if psmDTO.scanId }}</a>{{/if}}{{/if}}
			--%>
			<%-- 
			--%>
				Y
			{{else}}
				{{#if show_associated_peptides_link_true }}
				  <a href="javascript:" psm_id="{{ psmDTO.id }}" scan_id="{{ psmDTO.scanId }}" 
				  	initial_reported_peptide_id="{{ reported_peptide_id }}"
					peptide_q_value_cutoff="{{ peptide_q_value_cutoff }}" psm_q_value_cutoff="{{ psm_q_value_cutoff }}"
					project_id="{{ project_id }}" search_id="{{ search_id }}"
				 	onclick="viewPeptidesRelatedToPSMsByScanId.openOverlayForPeptidesRelatedToPSMsByScanId( { clickedElement : this } )"
				 	>{{/if}}N{{#if show_associated_peptides_link_true }}</a>{{/if}}
			{{/if}}
		</td>
	{{/if}}
	
	{{#if chargeDataAnyRows}}
		<td style="text-align: right; white-space: nowrap; " class="integer-number-column {{#if scanIdMatchesInitialScanId }}   highlight-row  {{/if}}" >
			{{charge}}
		</td>
	{{/if}}
	
	{{#if scanDataAnyRows}}
		<td style="text-align: right; white-space: nowrap; " class="integer-number-column{{#if scanIdMatchesInitialScanId }}   highlight-row  {{/if}}" >
			{{preMZRounded}}
		</td>
		<td style="text-align: right; white-space: nowrap; " class="integer-number-column{{#if scanIdMatchesInitialScanId }}   highlight-row  {{/if}}" >
			{{retentionTimeMinutesRoundedString}}
		</td>
		<td style="text-align: left; white-space: nowrap; " >
			{{scanFilename}}
		</td>
	{{/if}}

	{{#each psmAnnotationValueList}}
		<td style="text-align: left; white-space: nowrap; "  class=" {{#if scanIdMatchesInitialScanId }}   highlight-row  {{/if}} "  
	 			>{{this}}</td>
	{{/each}}
			

</tr>

	  