

		<%-- !!!   Handlebars template   !!!!!!!!!   --%>
		
		
	
	<%--  Reported Peptide in Related PSM Overlay Template --%>

<div > <%--  top level div in the template --%>


	<table id="peptides_related_to_psm_table" class="tablesorter  peptides_related_to_psm_table_jq "
		 style="min-width: 1100px; width: 1100px;" >
	
	
	
	
		<thead>
		<tr>
			<th data-tooltip="Type of peptide (e.g., crosslink, looplink, or unlinked)" class=" <%-- tool_tip_attached_jq --%> " style="text-align:left;width:10%;font-weight:bold;">Type</th>
			<th data-tooltip="The peptide as reported by search program (e.g., Kojak or XQuest)" class=" <%-- tool_tip_attached_jq --%> " style="text-align:left;width:10%;font-weight:bold;">Reported peptide</th>
			<th data-tooltip="Sequence of matched peptide (or first peptide in case of crosslinks)" class=" <%-- tool_tip_attached_jq --%> " style="text-align:left;width:10%;font-weight:bold;">Peptide 1</th>
			<th data-tooltip="Linked position in first peptide (or starting position of looplink)" class=" <%-- tool_tip_attached_jq --%>  integer-number-column-header" style="width:5%;font-weight:bold;">Pos</th>
			<th data-tooltip="Sequenced of second matched peptide in crosslink" class=" <%-- tool_tip_attached_jq --%> " style="text-align:left;width:10%;font-weight:bold;">Peptide 2</th>
			<th data-tooltip="Linked position in second peptide (or ending position of looplink)" class=" <%-- tool_tip_attached_jq --%>  integer-number-column-header" style="width:5%;font-weight:bold;">Pos</th>
			<th data-tooltip="Proteins (and positions) matched by first peptide and position" class=" <%-- tool_tip_attached_jq --%> " style="text-align:left;width:5%;font-weight:bold;">Protein 1</th>
			<th data-tooltip="Proteins (and positions) matched by second peptide and position" class=" <%-- tool_tip_attached_jq --%> " style="text-align:left;width:5%;font-weight:bold;">Protein 2</th>
			
			<%-- showPeptideQValue removed from JS code
			
			{{#if showPeptideQValue}}
				<th data-tooltip="Peptide-level q-value for this peptide (or linked pair)" 
						class=" " 
						style="width:10%;font-weight:bold;">
					<span style="white-space: nowrap">Q-value</span>
				</th>
			{{/if}}
			--%> <%-- tool_tip_attached_jq --%>
			
			
				{{#each peptideAnnotationDisplayNameDescriptionList}}
				 	<th style="text-align:left;font-weight:bold;"
				 		><span style="white-space: nowrap" <%-- TODO Add Description as tool tip, {{this.description}} --%>
				 			>{{this.displayName}}</span></th>
				{{/each}}
				
			
			<th data-tooltip="Number of PSMs matched to this peptide (or linked pair)" class=" <%-- tool_tip_attached_jq --%>  integer-number-column-header" style="width:10%;font-weight:bold; white-space: nowrap;"># PSMs</th>
			<th data-tooltip="Number of scans that do not uniquely match to this reported peptide" class=" <%-- tool_tip_attached_jq --%>  integer-number-column-header" style="width:10%;font-weight:bold; white-space: nowrap;"># Non-unique</th>
			
				{{#each psmAnnotationDisplayNameDescriptionList}}
				 	<th style="text-align:left;font-weight:bold;"
				 		><span style="white-space: nowrap">Best PSM</span>
				 		<span style="white-space: nowrap" <%-- TODO Add Description as tool tip, {{this.description}} --%>
				 			>{{this.displayName}}</span></th>
				{{/each}}				
			<%-- 
			<th data-tooltip="Best q-value among PSMs that matched this peptide (or linked pair)" class=" tool_tip_attached_jq  " style="width:10%;font-weight:bold;">Best&nbsp;PSM <span style="white-space: nowrap">Q-value</span></th>
			--%>
		</tr>
		</thead>
	</table>

</div>