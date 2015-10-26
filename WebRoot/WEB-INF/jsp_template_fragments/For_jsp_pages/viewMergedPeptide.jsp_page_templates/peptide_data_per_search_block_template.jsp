


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
			{{#if anyLinksHavePeptideQValue }}
				<th style="text-align:left;width:7%;font-weight:bold;"><span style="white-space: nowrap">Q-value</span></th>
			{{/if}}
			{{#if anyLinksHavePeptidePEPValue }}
				<th style="text-align:left;width:7%;font-weight:bold;">PEP</th>
			{{/if}}
			{{#if anyLinksHavePeptideSVMValue }}
				<th style="text-align:left;width:7%;font-weight:bold;">SVM Score</th>
			{{/if}}
				
			<th style="text-align:right;width:7%;font-weight:bold;">Psms</th>
		</tr>
	  </thead>
	  
	 </table>

</div>	 