


<%-- viewPsmsLoadedFromWebServiceTemplateFragment.jsp --%>



<%--
//		psms: Array[4]
//			0: Object
//			calcMass: 0
//			charge: 2
//			chargeSet: true
//			id: 382114
//			pep: 0.0009191
//			reportedPeptideId: 1012223
//			searchId: 308
//			psmId: "T-28418.pf.2015-01-08-Q_2013_1016_RJ_08-relD500odb-percolatorIn.txt"
//			qValue: 0.001051
//			scanId: 635969
//			svmScore: 0.733
//			type: 2
--%>					
	
	
	<%--  PSM Table Template --%>

	<%-- This table is just a container and will not be placed into the final output --%>
	<div id="psm_block_template" style="display: none;" >
	
		<%--  top level <div> in the template so can reference the inserted element with jQuery after insert it .
				
				var $psm_block_template = $(handlebarsSource_psm_block_template).appendTo($psm_data_container);
			
				$psm_block_template can then be used.  If no top level <div> in the template, cannot use $psm_block_template 
				
		--%>
				
		<div > <%--  top level div in the template --%>

			<table  class=" tablesorter psm_table_jq" style="width:60%"  >
				<thead>
				<tr>
					<th style="text-align:left;font-weight:bold;"><!-- spectrum link for Lorikeet --></th>
<%-- 					
					<th style="text-align:left;font-weight:bold;" class=" percolatorPsm_columns_jq " >PSM ID</th>
--%>					
					<th style="text-align:right;font-weight:bold;" class="integer-number-column"><span style="white-space: nowrap">Scan Num.</span></th>
					<th style="text-align:right;font-weight:bold;" class="integer-number-column"><span style="white-space: nowrap">Charge</span></th>
					<th style="text-align:right;font-weight:bold;" class="integer-number-column"><span style="white-space: nowrap">Obs. m/z</span></th>
					<th style="text-align:right;font-weight:bold;" class="integer-number-column"><span style="white-space: nowrap">RT (min)<%-- Retention Time --%></span></th>
					<th style="text-align:left;font-weight:bold;"><span style="white-space: nowrap">Scan Filename</span></th>
					<th style="text-align:left;font-weight:bold;"><span style="white-space: nowrap">Q-value</span></th>
					<th style="text-align:left;font-weight:bold;" class=" percolatorPsm_columns_jq " >PEP</th>
					<th style="text-align:left;font-weight:bold;" class=" percolatorPsm_columns_jq " >SVM Score</th>
					<th style="text-align:left;font-weight:bold;" class=" percolatorPsm_columns_jq " >Calc. Mass</th>
				</tr>
				</thead>
				<tbody></tbody>
			</table>
		</div>
		
	</div> <%--  end id="psm_block_template" --%>
	
	
	
<%--	
		PSM ID	Q-value	PEP	SVM Score	Calc. Mass
--%>	
	<%--  PSM Entry Template --%>

	<%-- This table is just a container and will not be placed into the final output --%>
	<table id="psm_entry_template" style="display: none;" >

		<tr >
		  <%-- 
			<td><a href="javascript:" psmId="{{id}}" class="view_spectrum_open_spectrum_link_jq">{{psmId}}</a></td>
		  --%>

			<td style="white-space: nowrap; "
				><a href="javascript:" psmId="{{psmDTO.id}}" class="view_spectrum_open_spectrum_link_jq" psm_type="{{psmDTO.type}}">View Spectrum</a></td>
<%-- 			
			<td style="text-align: left; white-space: nowrap; " class=" percolatorPsm_columns_jq " >{{psmDTO.percolatorPsm.psmId}}</td>
--%>			
			<td style="text-align: right; white-space: nowrap; "  class="integer-number-column">{{scanNumber}}</td>
			<td style="text-align: right; white-space: nowrap; " class="integer-number-column" >{{charge}}</td>
			<td style="text-align: right; white-space: nowrap; " class="integer-number-column" >{{preMZRounded}}</td>
			<td style="text-align: right; white-space: nowrap; " class="integer-number-column" >{{retentionTimeMinutesRoundedString}}</td>
			<td style="text-align: left; white-space: nowrap; " >{{scanFilename}}</td>
			<td style="text-align: left; white-space: nowrap; " >{{psmDTO.qValue}}</td>
			<td style="text-align: left; white-space: nowrap; " class=" percolatorPsm_columns_jq " >{{psmDTO.percolatorPsm.pep}}</td>
			<td style="text-align: left; white-space: nowrap; " class=" percolatorPsm_columns_jq " >{{psmDTO.percolatorPsm.svmScore}}</td>
			<td style="text-align: left; white-space: nowrap; " class=" percolatorPsm_columns_jq " >{{psmDTO.percolatorPsm.calcMass}}</td>
		</tr>
	</table>	
	
	

<%--   This would work to allow template conditionals outside of <td> but the lost editor help makes it problematic to use 	
	<script id="ScriptTest__psm_entry_template">
	/*
	ScriptTest__psm_entry_template

			<td><a href="javascript:" psmId="{{id}}" class="view_spectrum_open_spectrum_link_jq" psm_type="{{type}}">View Spectrum</a></td>
			<td style="text-align: left; white-space: nowrap; " >{{qValue}}</td>
			
			{{#if hasPercolatorPsm}}
				<td style="text-align: left; white-space: nowrap; " class=" percolatorPsm_columns_jq " >{{percolatorPsm.pep}}</td>
				<td style="text-align: left; white-space: nowrap; " class=" percolatorPsm_columns_jq " >{{percolatorPsm.svmScore}}</td>
				<td style="text-align: left; white-space: nowrap; " class=" percolatorPsm_columns_jq " >{{percolatorPsm.calcMass}}</td>
			{{/if}}

	*/
	</script>
--%>
	
	