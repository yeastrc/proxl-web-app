


		<%-- !!!   Handlebars template   !!!!!!!!!   --%>
		
		
	
	<%--  Peptide Data Per Search Template --%>

<div >
	<table class="tablesorter peptide_table_jq " style="width:80%">
	
	  <thead>
		<tr>
			<th style="text-align:left;width:{{ pageFormatting.nameWidthPercent }}%;font-weight:bold;">Name</th>
			<th <%-- data-tooltip="The peptide as reported by respective search program (e.g., Kojak or XQuest)"  --%> 
					class=" <%-- tool_tip_attached_jq  --%> " 
					style="text-align:left;width:{{ pageFormatting.reportedPeptideWidthPercent }}%;font-weight:bold;">
				Reported peptide
			</th>

			{{#each peptideAnnotationDisplayNameDescriptionList}}
			 	<th style="text-align:left;font-weight:bold;"
			 		><span style="white-space: nowrap" <%-- TODO Add Description as tool tip, {{this.description}} --%>
			 			>{{this.displayName}}</span></th>
			{{/each}}
			
				
			<th style="text-align:right;width:7%;font-weight:bold;">Psms</th>

			{{#each psmAnnotationDisplayNameDescriptionList}}
			 	<th style="text-align:left;font-weight:bold;"
			 		><span style="white-space: nowrap" >Best PSM</span> <span style="white-space: nowrap" <%-- TODO Add Description as tool tip, {{this.description}} --%>
			 			>{{this.displayName}}</span></th>
			{{/each}}
						
		</tr>
	  </thead>
	  
	 </table>

</div>	 
