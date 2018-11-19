
<%-- qcChart_Download_Help_HTMLBlock.jsp

	Block to include to add to chart:
	
		download icon and menu
		help
	
	Include inside of outer containing <div> that has class " chart-standard-container-div chart_outer_container_for_download_jq"
	
	     "chart-standard-container-div" is required for at least "position: relative;"
	     "chart_outer_container_for_download_jq" is required for the Javascript in chartDownload.js
 --%>
 
	<div class=" svg_download_outer_block_jq " style="position: absolute; top: 4px; right: 4px;">
	
	  <div class=" help-image-for-qc-chart-block ">
	  	<img src="images/icon-help.png"  class=" help-image-for-qc-chart help_image_for_qc_chart_jq ">
	  </div>
	  
	  <div class="svg-download-block">
		<a href="javascript:" class=" tool_tip_attached_jq  " data-tooltip="Download graphic as file." 
			><img src="images/icon-download-small.png" /></a>

		<!-- Overlay that goes under main overlay: display on hover of download icon -->
		<div class="svg-download-options-backing-block svg_download_backing_block_jq ">
		</div>
		<!-- Overlay: display on hover of download icon -->
		<span class=" svg-download-options-block svg_download_block_jq ">
			Choose download file format:
			<a data-tooltip="Download as a JPEG image file." 
				data-download_type="jpeg"
				class="svg-download-option tool_tip_attached_jq chart_download_link_jq " href="javascript:" style="margin-top:5px;"
				>JPEG</a>
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
				class="svg-download-option tool_tip_attached_jq chart_download_link_jq " href="javascript:" style="margin-top:5px; margin-bottom: 5px;"
				>SVG</a>

			<%-- Download the Summary data for the chart. Used for box plots. Hidden until shown in each JS chart code since then a click handler is attached. --%>
			<a data-tooltip="Download summary data as text, tab delimited." 
				class="svg-download-option tool_tip_attached_jq chart_summary_data_download_link_jq " href="javascript:" style="margin-top:5px; margin-bottom: 5px; display: none;"
				>Summary Text</a>
								
			<%-- Download the data for the chart, hidden until shown in each JS chart code since then a click handler is attached. --%>
			<a data-tooltip="Download as text, tab delimited." 
				class="svg-download-option tool_tip_attached_jq chart_data_download_link_jq " href="javascript:" style="margin-top:5px; margin-bottom: 5px; display: none;"
				>Text/Tab Delimited</a>
		</span>
	  </div>
	</div>	