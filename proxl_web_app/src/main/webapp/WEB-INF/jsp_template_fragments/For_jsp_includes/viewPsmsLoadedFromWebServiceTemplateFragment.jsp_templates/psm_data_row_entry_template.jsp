
		<%-- !!!   Handlebars template   !!!!!!!!!   --%>
		
	<%--  PSM Entry Template  - Enclosed with  <script id="psm_data_row_entry_template" --%>

	

<tr  class=" {{#if scanIdMatchesInitialScanId }}   highlight-row  {{/if}} " 
	style=" {{#if searchHasPsmPerPeptideAnnTypes }} cursor: pointer; {{/if}} "
	{{#if searchHasPsmPerPeptideAnnTypes }} 
		onclick="viewPsmPerPeptideLoadedFromWebServiceTemplate.showHidePsmPerPeptide( { clickedElement : this } )"
	{{/if}}
	data-project_search_id="{{  project_search_id }}"
	data-psm_id="{{ psm.psmDTO.id }}" >
  <%-- 
	<td><a href="javascript:" psmId="{{id}}" class="view_spectrum_open_spectrum_link_jq">{{psmId}}</a></td>
  --%>

	{{#if showViewSpectrumLinkColumn}}
		<td style="white-space: nowrap; "  class=" {{#if psm.scanIdMatchesInitialScanId }}   highlight-row  {{/if}} "  
			><a href="javascript:" psmId="{{ psm.psmDTO.id }}" class="view_spectrum_open_spectrum_link_jq" 
					psm_type="{{ psmDTO.type }}"
				>View Spectrum</a></td>
	{{/if}}
<%-- 
			<td style="text-align: left; white-space: nowrap; " class=" percolatorPsm_columns_jq " >{{ psm.psmDTO.percolatorPsm.psmId}}</td>
--%>			
	{{#if scanNumberAnyRows}}
		<td style="text-align: right; white-space: nowrap; "  class="integer-number-column {{#if  psm.scanIdMatchesInitialScanId }}   highlight-row  {{/if}}">
			{{  psm.scanNumber }}
		</td>
	{{/if}}
	
	{{#if scanDataAnyRows}}
	  {{#if  uniquePSM }}
		<td style="text-align: right; white-space: nowrap; "  class="integer-number-column {{#if  psm.scanIdMatchesInitialScanId }}   highlight-row  {{/if}}">
			
			<%--  TEMP --%>
			<%-- 
				{{#if  show_associated_peptides_link_true }}
				 {{#if  psm.psmDTO.scanId }}
				  <a href="javascript:" 
				  	data-psm_id="{{  psm.psmDTO.id }}" 
				  	data-scan_id="{{  psm.psmDTO.scanId }}" 
				  	data-initial_reported_peptide_id="{{  reported_peptide_id }}"
					data-project_search_id="{{  project_search_id }}"
				 	onclick="viewPeptidesRelatedToPSMsByScanId.openOverlayForPeptidesRelatedToPSMsByScanId( { clickedElement : this } )"
				 	>{{/if}}{{/if}}TEMP_N{{#if  show_associated_peptides_link_true }}{{#if  psm.psmDTO.scanId }}</a>{{/if}}{{/if}}
			--%>
			<%-- 
			--%>
				Y
		</td>
	  {{else}}
		<td style="text-align: right; white-space: nowrap; "  class="integer-number-column highlight-cell {{#if  psm.scanIdMatchesInitialScanId }}   highlight-row  {{/if}}">
				{{#if  show_associated_peptides_link_true }}
				  <a href="javascript:" 
				    data-psm_id="{{  psm.psmDTO.id }}" 
				    data-scan_id="{{  psm.psmDTO.scanId }}" 
				  	data-initial_reported_peptide_id="{{  reported_peptide_id }}"
					data-project_search_id="{{  project_search_id }}"
				 	onclick="viewPeptidesRelatedToPSMsByScanId.openOverlayForPeptidesRelatedToPSMsByScanId( { clickedElement : this } )"
				 	>{{/if}}N{{#if  show_associated_peptides_link_true }}</a>{{/if}}
		</td>
	  {{/if}}
	{{/if}}
					precursorMZ_DataAnyRows,
				precursorRetentionTime_DataAnyRows,
	
	{{#if precursorMZ_DataAnyRows}}
		<td style="text-align: right; white-space: nowrap; " class="integer-number-column{{#if  psm.scanIdMatchesInitialScanId }}   highlight-row  {{/if}}" >
			{{ psm.preMZRounded }}
		</td>
	{{/if}}

	{{#if chargeDataAnyRows}}
		<td style="text-align: right; white-space: nowrap; " class="integer-number-column {{#if  psm.scanIdMatchesInitialScanId }}   highlight-row  {{/if}}" >
			{{  psm.charge }}
		</td>
	{{/if}}
	

	{{#if precursorRetentionTime_DataAnyRows}}
		<td style="text-align: right; white-space: nowrap; " class="integer-number-column{{#if  psm.scanIdMatchesInitialScanId }}   highlight-row  {{/if}}" >
			{{  psm.retentionTimeMinutesRoundedString }}
		</td>
	{{/if}}

	{{#if scanFilenameAnyRows}}
		<td style="text-align: left; white-space: nowrap; " >
			{{ psm.scanFilename }}
		</td>
	{{/if}}

	{{#each  psm.psmAnnotationValueList }}
		<td style="text-align: left; white-space: nowrap; "  class=" {{#if  psm.scanIdMatchesInitialScanId }}   highlight-row  {{/if}} "  
	 			>{{ this }}</td>
	{{/each}}
			

</tr>

	  