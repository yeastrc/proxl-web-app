/**
 * qcChart_Download_Help_HTMLBlock.js
 * 
 * Javascript for the qcChart_Download_Help_HTMLBlock.jsp page fragment
 * 
 * page variable qcChartDownloadHelp
 * 
 * !!!!   Page Requirements:
 * 
 * The element containing the include of qcChart_Download_Help_HTMLBlock.jsp 
 *   has to have class "chart_outer_container_for_download_jq".
 * 
 * The 
 * 
 */

//  JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";


import { downloadStringAsFile } from 'page_js/data_pages/project_search_ids_driven_pages/common/download-string-as-file.js';
import {SVGDownloadUtils} from "../common/svgDownloadUtils";


///////  removed since init() body is empty
// $(document).ready(function() { 
// 	qcChartDownloadHelp.init();

// } ); // end $(document).ready(function() 


/**
 * Constructor 
 */
var QC_ChartDownloadHelp = function() {

	/**
	 * Init page on load 
	 */
	// this.init = function() {
	// 	var objectThis = this;
	// };

	/**
	 * params: { $chart_outer_container_for_download_jq : element with ".chart_outer_container_for_download_jq"
	 * 			 helpTooltipHTML : String with text/HTML for tooltip for help icon on chart
	 * 
	 * If the element $chart_outer_container_for_download_jq was dynamically added, need to run to add tool tips on download links: 
	 *			addToolTips( $chartOuterContainer );
	 *
	 * addDownloadClickHandlers
	 */
	this.add_DownloadClickHandlers_HelpTooltip = function( params ) {
		var objectThis = this;
		
		var $chart_outer_container_for_download_jq = params.$chart_outer_container_for_download_jq;
		var downloadDataCallback = params.downloadDataCallback;
		var downloadSummaryDataCallback = params.downloadSummaryDataCallback;
		
		var helpTooltipHTML = params.helpTooltipHTML;
		var helpTooltip_Wide = params.helpTooltip_Wide;
		
		var $chart_download_link_jq_All = $chart_outer_container_for_download_jq.find(".chart_download_link_jq");
		$chart_download_link_jq_All.click( function( event ) { 
			objectThis._downloadChart( { clickedThis : this } ); 
			event.preventDefault();
			event.stopPropagation();
		});
		
		if ( downloadDataCallback ) {
			//  Download data callback function provided so show link and attach click handler

			//  Add Data Download Click Handler and show the download data link
			var $chart_data_download_link_jq = $chart_outer_container_for_download_jq.find(".chart_data_download_link_jq");
			$chart_data_download_link_jq.click( function( event ) { 
				try {
					var $this = $( this );
//					var $chart_outer_container_for_download_jq = $this.closest(".chart_outer_container_for_download_jq");
//					var linkType = $chart_outer_container_for_download_jq.attr( "data-link_type" ); //  Not populated in HTML
//					downloadDataCallback( { clickedThis : this, linkType : linkType } ); 
					downloadDataCallback( { clickedThis : this } ); 
					event.preventDefault();
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			});
			$chart_data_download_link_jq.show();
		}
		
		if ( downloadSummaryDataCallback ) {
			//  Download summary data callback function provided so show link and attach click handler

			//  Add Data Download Click Handler and show the download data link
			var $chart_summary_data_download_link_jq = $chart_outer_container_for_download_jq.find(".chart_summary_data_download_link_jq");
			$chart_summary_data_download_link_jq.click( function( event ) { 
				try {
					var $this = $( this );
//					var $chart_outer_container_for_download_jq = $this.closest(".chart_outer_container_for_download_jq");
//					var linkType = $chart_outer_container_for_download_jq.attr( "data-link_type" ); //  Not populated in HTML
//					downloadSummaryDataCallback( { clickedThis : this, linkType : linkType } ); 
					downloadSummaryDataCallback( { clickedThis : this } ); 
					event.preventDefault();
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			});
			$chart_summary_data_download_link_jq.show();
		}
		
		//  Eat any clicks that occur on these elements or their children
		
		var $svg_download_block_jq = $chart_outer_container_for_download_jq.find(".svg_download_block_jq");
		$svg_download_block_jq.click( function( event ) {  
			event.preventDefault();
			event.stopPropagation();
		});

		var $svg_download_backing_block_jq = $chart_outer_container_for_download_jq.find(".svg_download_backing_block_jq");
		$svg_download_backing_block_jq.click( function( event ) {  
			event.preventDefault();
			event.stopPropagation();
		});
				
		//  Add tooltip to ? with circle located upper right corner of chart
		
		var helpTooltipClasses = " help-for-qc-chart-tooltip ";
		
		if ( helpTooltip_Wide ) {
			helpTooltipClasses += " help-for-qc-chart-tooltip-wide ";
		}
		
		var $help_image_for_qc_chart_jq = $chart_outer_container_for_download_jq.find(".help_image_for_qc_chart_jq");

		$help_image_for_qc_chart_jq.qtip( {
	        content: {
	            text: helpTooltipHTML
	        },
	        style : {
	        	def : false,  // Do not add class 'qtip-default'.  Class 'qtip' is still added, which contains font-size
	        	classes : helpTooltipClasses //  Add this/these class to the tooltip
	        },
			position: {
				target: 'mouse',
				adjust: { x: 5, y: 5 }, // Offset it slightly from under the mouse
	            viewport: $(window)
	         }
	    });		
		
	};


	/**
	 * 
	 */
	this._downloadChart = function( params ) {
		try {
			const clickedThis = params.clickedThis;

			const $clickedThis = $( clickedThis );
			const download_type = $clickedThis.attr("data-download_type");
			const $chart_outer_container_for_download_jq = $clickedThis.closest(".chart_outer_container_for_download_jq");
			const $svgRoot = $chart_outer_container_for_download_jq.find("svg");

			SVGDownloadUtils.downloadSvgAsImageType($svgRoot[0], download_type);

		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
		
	};

	/**
	 * 
	 */
	this._downloadBoxplotChartSummaryData = function( params ) {
		var filenamePartChartName = params.filenamePartChartName;
		var entryForLinkType = params.entryForLinkType;
		var _project_search_ids = params._project_search_ids;
		try {

			var linkType = entryForLinkType.linkType;
			var dataForChartPerSearchIdMap_KeyProjectSearchId = entryForLinkType.dataForChartPerSearchIdMap_KeyProjectSearchId;
			
			var headerLine = "SEARCH ID\tLINK TYPE\tMAX\tMIN\tMEDIAN\tFIRST QUARTILE\tTHIRD QUARTILE" ;

			var outputStringArray = [ headerLine ];

			var searchIds = [];

			_project_search_ids.forEach( function ( _project_search_ids_ArrayValue, indexForProjectSearchId, array ) {
				
				var dataForChartPerSearchIdEntry = dataForChartPerSearchIdMap_KeyProjectSearchId[ _project_search_ids_ArrayValue ];
				
				var searchId = dataForChartPerSearchIdEntry.searchId;
				
				searchIds.push( searchId );
				
				var ouputLine = 
					searchId + "\t" +
					linkType + "\t" +
					dataForChartPerSearchIdEntry.chartIntervalMax + "\t" +
					dataForChartPerSearchIdEntry.chartIntervalMin + "\t" +
					dataForChartPerSearchIdEntry.median + "\t" +
					dataForChartPerSearchIdEntry.firstQuartile + "\t" +
					dataForChartPerSearchIdEntry.thirdQuartile;
				
				outputStringArray.push( ouputLine );
			} );
			

			var searchIdsString = searchIds.join("-");
			
			var content = outputStringArray.join("\n");

			var filename = "proxl-" + filenamePartChartName + "-" + linkType + "-search-" + searchIdsString + ".txt";
			var mimetype = "text/plain";

			downloadStringAsFile( filename, mimetype, content );
			
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
		
	};

};

var qcChartDownloadHelp = new QC_ChartDownloadHelp();

export { qcChartDownloadHelp }

