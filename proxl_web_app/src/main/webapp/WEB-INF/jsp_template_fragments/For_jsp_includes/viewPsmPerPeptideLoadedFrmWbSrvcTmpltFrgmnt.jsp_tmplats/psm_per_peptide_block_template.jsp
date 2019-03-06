
<%--  psm_per_peptide_block_template.jsp  --%>



		<%-- !!!   Handlebars template   !!!!!!!!!   --%>
		
		
	
	<%--  PSM Per Peptide Template --%>

<div > <%--  top level div in the template --%>

	<table  class=" tablesorter psm_per_peptide_table_jq" style="width:60%"  >
		<thead>
		<tr>
<%--  
	Hide since code not done to display when click on "View Spectrum" 
			{{#if showViewSpectrumLinkColumn}}
				<th style="text-align:left;font-weight:bold;"><!-- spectrum link for Lorikeet --></th>
			{{/if}}
 --%>
			<th style="text-align:left;font-weight:bold;" class=""><span style="white-space: nowrap">Peptide Sequence</span></th>

			{{#if peptideLinkPosition_1_AnyRows}}
				<th style="text-align:right;font-weight:bold;" class=" tool_tip_attached_jq "
						data-tooltip=""  
					><span style="white-space: nowrap">Pos 1</span>
				</th>
			{{/if}}
			
			{{#if peptideLinkPosition_2_AnyRows}}
				<th style="text-align:right;font-weight:bold;" class=" tool_tip_attached_jq "
						data-tooltip=""  
					><span style="white-space: nowrap">Pos 2</span>
				</th>
			{{/if}}
			
			<th style="text-align:right;font-weight:bold;" class=""><span style="white-space: nowrap">Mods</span></th>

			{{#if scanNumberAnyRows}}
				<th style="text-align:right;font-weight:bold;" class="integer-number-column"><span style="white-space: nowrap">Scan Num.</span></th>
			{{/if}}

			{{#if scanDataAnyRows}}
				<th style="text-align:right;font-weight:bold;" class="integer-number-column"><span style="white-space: nowrap">Obs. m/z</span></th>
			{{/if}}

			{{#if chargeDataAnyRows}}
				<th style="text-align:right;font-weight:bold;" class="integer-number-column"><span style="white-space: nowrap">Charge</span></th>
			{{/if}}
			
			{{#if scanDataAnyRows}}
				<th style="text-align:right;font-weight:bold;" class="integer-number-column"><span style="white-space: nowrap">RT (min)<%-- Retention Time --%></span></th>
			{{/if}}
				
			{{#if scanFilenameAnyRows}}
				<th style="text-align:left;font-weight:bold;"><span style="white-space: nowrap">Scan Filename</span></th>
			{{/if}}
			
			{{#each annotationLabels}}

					<%--  Consider displaying the description somewhere   peptideAnnotationDisplayNameDescription.description --%>
				<th data-tooltip="PSM-Per-Peptide-level {{this}} for this PSM (or linked pair)" 
						class=" tool_tip_attached_jq " 
				 		 style="text-align:left;font-weight:bold;"
			 		><span style="white-space: nowrap" <%-- TODO Add Description as tool tip, {{this.description}} --%>
			 			>{{this}}</span></th>
			{{/each}}
			
			
		</tr>
		</thead>
		<tbody></tbody>
	</table>
</div>		