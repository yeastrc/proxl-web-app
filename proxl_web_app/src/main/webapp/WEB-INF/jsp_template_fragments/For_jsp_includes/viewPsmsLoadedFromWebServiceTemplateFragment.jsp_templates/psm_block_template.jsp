

		<%-- !!!   Handlebars template   !!!!!!!!!   --%>
		
		
	
	<%--  PSM Template --%>

<div > <%--  top level div in the template --%>

	<div class=" psm-qc-charts-container psm_qc_charts_container_jq " style="">
	 <table class="table-no-border-no-cell-spacing-no-cell-padding" style=""> <%--  no width: 100% --%>
	  <tr style="">
	   <td style="border: 0px; padding: 4px;">
			<div class=" psm_qc_charge_chart_outer_container_jq psm_qc_either_chart_outer_container_jq " 
				style="position: relative; display: none; border-width: 1px; border-style: solid; border-color: #CDCDCD;"
				data-chart_type="charge" >
				<!-- Container for Charge Count Chart -->
				<div style=" width: 300px; height: 240px; " class=" psm_qc_charge_chart_container_jq psm_qc_either_chart_container_jq " >
				</div>
				<div class="" style="position: absolute; top: 4px; right: 4px;">
				  <div class="svg-download-block">
					<a href="javascript:" class=" tool_tip_attached_jq  " data-tooltip="Download graphic as file." 
						><img src="images/icon-download-small.png" /></a>
						
					<!-- Overlay that goes under main overlay: display on hover of download icon -->
					<div class="svg-download-options-backing-block">
					</div>
					<!-- Overlay: display on hover of download icon -->
					<div class="svg-download-options-block">
						Choose download file format:
						<a data-tooltip="Download as PDF file suitable for use in Adobe Illustrator or printing." 
							data-download_type="pdf"
							class="svg-download-option tool_tip_attached_jq chart_download_link_jq " href="javascript:" style="margin-top:5px;"
							>PDF</a>
						<a data-tooltip="Download as PNG image file." 
							data-download_type="png"
							class="svg-download-option tool_tip_attached_jq chart_download_link_jq " href="javascript:" style="margin-top:5px;"
							>PNG</a>
						<a data-tooltip="Download as scalable vector graphics file suitable for use in Inkscape or other compatible software." 
							data-download_type="svg"
							class="svg-download-option tool_tip_attached_jq chart_download_link_jq " href="javascript:" style="margin-top:5px;"
							>SVG</a>
					</div>
			
				  </div>
				</div>			
			</div>
	   </td>
	   <td style="border: 0px; padding: 4px;">
			<div class=" psm_qc_retention_time_chart_outer_container_jq psm_qc_either_chart_outer_container_jq "  
				style="position: relative; display: none; border-width: 1px; border-style: solid; border-color: #CDCDCD;"
				data-chart_type="charge">
				<!-- Container for Retention Time Count Chart -->
				<div style="width: 300px; height: 240px; " class=" psm_qc_retention_time_chart_container_jq psm_qc_either_chart_container_jq " >
				</div>
				<div class="" style="position: absolute; top: 4px; right: 4px;">
				  <div class="svg-download-block">
					<a href="javascript:" class=" tool_tip_attached_jq  " data-tooltip="Download graphic as file." 
						><img src="images/icon-download-small.png" /></a>

					<!-- Overlay that goes under main overlay: display on hover of download icon -->
					<div class="svg-download-options-backing-block">
					</div>
					<!-- Overlay: display on hover of download icon -->
					<span class="svg-download-options-block">
						Choose download file format:
						<a data-tooltip="Download as PDF file suitable for use in Adobe Illustrator or printing." 
							data-download_type="pdf"
							class="svg-download-option tool_tip_attached_jq chart_download_link_jq " href="javascript:" style="margin-top:5px;"
							>PDF</a>
						<a data-tooltip="Download as PNG image file." 
							data-download_type="png"
							class="svg-download-option tool_tip_attached_jq chart_download_link_jq " href="javascript:" style="margin-top:5px;"
							>PNG</a>
						<a data-tooltip="Download as scalable vector graphics file suitable for use in Inkscape or other compatible software." 
							data-download_type="svg"
							class="svg-download-option tool_tip_attached_jq chart_download_link_jq " href="javascript:" style="margin-top:5px;"
							>SVG</a>
					</span>
				  </div>
				</div>			
			</div>
	   </td>
	  </tr>
	 </table>
	</div>

	<table  class=" tablesorter psm_table_jq" style="width:60%"  >
		<thead>
		<tr>
			{{#if showViewSpectrumLinkColumn}}
				<th style="text-align:left;font-weight:bold;"><!-- spectrum link for Lorikeet --></th>
			{{/if}}
<%-- 					
					<th style="text-align:left;font-weight:bold;" class=" percolatorPsm_columns_jq " >PSM ID</th>
--%>					
			{{#if scanNumberAnyRows}}
				<th style="text-align:right;font-weight:bold;" class="integer-number-column"><span style="white-space: nowrap">Scan Num.</span></th>
			{{/if}}
			
			{{#if scanDataAnyRows}}
				<th style="text-align:right;font-weight:bold;" class=" tool_tip_attached_jq "
						data-tooltip="PSM is unique for scan"  
					><span style="white-space: nowrap">U</span>
				</th>
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

			
			
			{{#each annotationDisplayNameDescriptionList}}

					<%--  Consider displaying the description somewhere   peptideAnnotationDisplayNameDescription.description --%>
				<th data-tooltip="PSM-level {{this.displayName}} for this PSM (or linked pair)" 
						class=" tool_tip_attached_jq " 
				 		 style="text-align:left;font-weight:bold;"
			 		><span style="white-space: nowrap" <%-- TODO Add Description as tool tip, {{this.description}} --%>
			 			>{{this.displayName}}</span></th>
			{{/each}}
			
			
		</tr>
		</thead>
		<tbody></tbody>
	</table>
</div>		