
		<%-- !!!   Handlebars template   !!!!!!!!!   --%>
		
	<%--  PSM Entry Template --%>

	

<tr >
  <%-- 
	<td><a href="javascript:" psmId="{{id}}" class="view_spectrum_open_spectrum_link_jq">{{psmId}}</a></td>
  --%>

	{{#if scanDataAnyRows}}
		<td style="white-space: nowrap; "
			><a href="javascript:" psmId="{{psmDTO.id}}" class="view_spectrum_open_spectrum_link_jq" psm_type="{{psmDTO.type}}"
				>View Spectrum</a></td>
	{{/if}}
<%-- 
			<td style="text-align: left; white-space: nowrap; " class=" percolatorPsm_columns_jq " >{{psmDTO.percolatorPsm.psmId}}</td>
--%>			
	{{#if scanDataAnyRows}}
		<td style="text-align: right; white-space: nowrap; "  class="integer-number-column">{{scanNumber}}</td>
	{{/if}}
	
	{{#if chargeDataAnyRows}}
		<td style="text-align: right; white-space: nowrap; " class="integer-number-column" >{{charge}}</td>
	{{/if}}
	
	{{#if scanDataAnyRows}}
		<td style="text-align: right; white-space: nowrap; " class="integer-number-column" >{{preMZRounded}}</td>
		<td style="text-align: right; white-space: nowrap; " class="integer-number-column" >{{retentionTimeMinutesRoundedString}}</td>
		<td style="text-align: left; white-space: nowrap; " >{{scanFilename}}</td>
	{{/if}}
		
	<td style="text-align: left; white-space: nowrap; " >{{psmDTO.qValue}}</td>
	
	{{#if percolatorDataAnyRows}} <%-- Only show percolator data columns if any percolator data value are not null --%>
		<td style="text-align: left; white-space: nowrap; " >{{psmDTO.percolatorPsm.pep}}</td>
		<td style="text-align: left; white-space: nowrap; " >{{psmDTO.percolatorPsm.svmScore}}</td>
<%-- 		
		<td style="text-align: left; white-space: nowrap; " >{{psmDTO.percolatorPsm.calcMass}}</td>
--%>		
	{{/if}}
</tr>

	  