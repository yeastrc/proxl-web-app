/**
 * qcPageChartScanFileStatistics_MS2_Count_Per_MS1_Scan.js
 * 
 * Javascript for the viewQC.jsp page - Chart Scan File Statistics
 * 
 * Chart for:
 * 
 *     	MS2 Count per MS1 Scan:  Scatter Plot
 * 
 * This code has been updated to cancel existing active AJAX calls when "Update from Database" button is clicked.
 *   This is done so that previous AJAX responses don't overlay new AJAX responses.
 */

//JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";


//   Variables Stored on 'window' 

const reportWebErrorToServer = window.reportWebErrorToServer;
const addToolTips = window.addToolTips;

const _PROXL_DEFAULT_FONT_COLOR = window._PROXL_DEFAULT_FONT_COLOR;


import { webserviceCallStandardPost } from 'page_js/webservice_call_common/webserviceCallStandardPost.js';

import { qc_pages_Single_Merged_Common } from './qc_pages_Single_Merged_Common.js';

import { qcChartDownloadHelp } from './qcChart_Download_Help_HTMLBlock.js';


/**
 * Constructor 
 */
var QCPageChartScanFileStatistics_MS2_Count_Per_MS1_Scan = function() {

	//  Download data URL
	// var _downloadStrutsAction = "downloadQC_PsmChargeChartData.do";

	/**
	 * Overridden for Specific elements like Chart Title and X and Y Axis labels
	 */
	var _CHART_GLOBALS = {
			_CHART_DEFAULT_FONT_SIZE : 12,  //  Default font size - using to set font size for tick marks.
			_TITLE_FONT_SIZE : 15, // In PX
			_AXIS_LABEL_FONT_SIZE : 14, // In PX
			_TICK_MARK_TEXT_FONT_SIZE : 14 // In PX

			, _ENTRY_ANNOTATION_TEXT_SIGNIFICANT_DIGITS : 2
	}
	
	//  From QCPageMain
	var _OVERALL_GLOBALS;

	var _project_search_ids = undefined;

	var _anySearchesHaveScanDataYes = undefined;

	//  Contains {{link_type}} to replace with link type.  Contains {{link_type}}_chart_outer_container_jq chart_outer_container_jq
	var _common_chart_outer_entry_templateHTML = undefined;

	var _common_chart_inner_entry_templateHTML = undefined;

	var _dummy_chart_entry_for_message_templateHTML = undefined;


	var _link_type_crosslink_constant = undefined;
	var _link_type_looplink_constant = undefined;
	var _link_type_unlinked_constant = undefined;
	var _link_type_default_selected = undefined;

	var _link_type_crosslink_LOWER_CASE_constant = undefined;
	var _link_type_looplink_LOWER_CASE_constant = undefined;
	var _link_type_unlinked_LOWER_CASE_constant = undefined;

	var _link_type_combined_LOWER_CASE_constant = undefined;

	//   These will have the link type added in between prefix and suffix, adding a space after link type.
	//       There is no space at start of suffix to support no link type
	var _DUMMY_CHART_STATUS_TEXT_PREFIX_LOADING = undefined;
	var _DUMMY_CHART_STATUS_TEXT_SUFFIX_LOADING = undefined;
	var _DUMMY_CHART_STATUS_TEXT_PREFIX_NO_DATA = undefined;
	var _DUMMY_CHART_STATUS_TEXT_SUFFIX_NO_DATA = undefined;
	var _DUMMY_CHART_STATUS_TEXT_PREFIX_ERROR_LOADING = undefined;
	var _DUMMY_CHART_STATUS_TEXT_SUFFIX_ERROR_LOADING = undefined;

	var _DUMMY_CHART_STATUS_WHOLE_TEXT_SCANS_NOT_UPLOADED = undefined;

	var _IS_LOADED_YES = "YES";
	var _IS_LOADED_NO = "NO";
	var _IS_LOADED_LOADING = "LOADING";
	

	//  passed in functions

	//  Copy references to qcPageMain functions to here
	this._passAJAXErrorTo_handleAJAXError = undefined;
	this._addChartOuterTemplate = undefined;
	this._addChartInnerTemplate = undefined;
	this._placeEmptyDummyChartForMessage = undefined;
	this.getColorAndBarColorFromLinkType = undefined;
	
	var _get_hash_json_Contents = undefined; // function on qcPageMain
	
	
	///////////
	
	//   Variables for this chart
	
	var _chart_isLoaded = _IS_LOADED_NO;


	this.set_qcPageChartScanFileStatistics_ScanFileSelector = function( qcPageChartScanFileStatistics_ScanFileSelector ) {

		this.qcPageChartScanFileStatistics_ScanFileSelector = qcPageChartScanFileStatistics_ScanFileSelector;
	}
	
	/**
	 * Init page Actual - Called from qcPageChartScanFileStatistics_ScanFileSelector_Driven_Subsection.initActual
	 */
	this.initActual = function( params ) {
		try {
			var objectThis = this;

			_OVERALL_GLOBALS = params.OVERALL_GLOBALS;

			_project_search_ids = params.project_search_ids;

			_anySearchesHaveScanDataYes = params.anySearchesHaveScanDataYes;

			//  Contains {{link_type}} to replace with link type.  Contains {{link_type}}_chart_outer_container_jq chart_outer_container_jq
			_common_chart_outer_entry_templateHTML = params.common_chart_outer_entry_templateHTML;
			_common_chart_inner_entry_templateHTML = params.common_chart_inner_entry_templateHTML;
			_dummy_chart_entry_for_message_templateHTML = params.dummy_chart_entry_for_message_templateHTML;

			_link_type_crosslink_constant = params.link_type_crosslink_constant;
			_link_type_looplink_constant = params.link_type_looplink_constant;
			_link_type_unlinked_constant = params.link_type_unlinked_constant;
			_link_type_default_selected = params.link_type_default_selected;

			_link_type_crosslink_LOWER_CASE_constant = params.link_type_crosslink_LOWER_CASE_constant;
			_link_type_looplink_LOWER_CASE_constant = params.link_type_looplink_LOWER_CASE_constant;
			_link_type_unlinked_LOWER_CASE_constant = params.link_type_unlinked_LOWER_CASE_constant;

			_link_type_combined_LOWER_CASE_constant = params.link_type_combined_LOWER_CASE_constant;

			//   These will have the link type added in between prefix and suffix, adding a space after link type.
			//       There is no space at start of suffix to support no link type
			_DUMMY_CHART_STATUS_TEXT_PREFIX_LOADING = params.DUMMY_CHART_STATUS_TEXT_PREFIX_LOADING;
			_DUMMY_CHART_STATUS_TEXT_SUFFIX_LOADING = params.DUMMY_CHART_STATUS_TEXT_SUFFIX_LOADING;
			_DUMMY_CHART_STATUS_TEXT_PREFIX_NO_DATA = params.DUMMY_CHART_STATUS_TEXT_PREFIX_NO_DATA;
			_DUMMY_CHART_STATUS_TEXT_SUFFIX_NO_DATA = params.DUMMY_CHART_STATUS_TEXT_SUFFIX_NO_DATA;
			_DUMMY_CHART_STATUS_TEXT_PREFIX_ERROR_LOADING = params.DUMMY_CHART_STATUS_TEXT_PREFIX_ERROR_LOADING;
			_DUMMY_CHART_STATUS_TEXT_SUFFIX_ERROR_LOADING = params.DUMMY_CHART_STATUS_TEXT_SUFFIX_ERROR_LOADING;

			_DUMMY_CHART_STATUS_WHOLE_TEXT_SCANS_NOT_UPLOADED = params.DUMMY_CHART_STATUS_WHOLE_TEXT_SCANS_NOT_UPLOADED;
			
			//  Copy references to qcPageMain functions to here
			this._passAJAXErrorTo_handleAJAXError = params._passAJAXErrorTo_handleAJAXError;
			this._addChartOuterTemplate = params._addChartOuterTemplate;
			this._addChartInnerTemplate = params._addChartInnerTemplate;
			this._placeEmptyDummyChartForMessage = params._placeEmptyDummyChartForMessage;
			this.getColorAndBarColorFromLinkType = params.getColorAndBarColorFromLinkType

			//  Do not store what is returned from function _get_hash_json_Contents since it can change
			_get_hash_json_Contents = params.get_hash_json_Contents; // function

			this.addClickAndOnChangeHandlers();

		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}

	};

	/**
	 * Add Click and onChange handlers 
	 */
	this.addClickAndOnChangeHandlers = function() {
		var objectThis = this;

		const $Scan_Statistics_MS2_Count_Per_MS1_Scan_Block_Create_Charts_Link = $("#Scan_Statistics_MS2_Count_Per_MS1_Scan_Block_Create_Charts_Link");
		$Scan_Statistics_MS2_Count_Per_MS1_Scan_Block_Create_Charts_Link.click( ( event ) => { 
			try {
				//  Get value for Scan File Id
				const scanFileId_GetCurrentSelectedValue = this.qcPageChartScanFileStatistics_ScanFileSelector.scanFileId_GetCurrentSelectedValue(); 

				objectThis._loadDataForScanFileId_Internal({ scanFileId : scanFileId_GetCurrentSelectedValue })
				event.preventDefault();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
	};

	///////////////////////////////////////////

	/**
	 * Clear data for ChargeState_Statistics_Counts
	 */
	this.clearChart = function() {

		_chart_isLoaded = _IS_LOADED_NO;

		var $Scan_Statistics_MS2_Count_Per_MS1_Scan_Block_Create_Charts_Block = $("#Scan_Statistics_MS2_Count_Per_MS1_Scan_Block_Create_Charts_Block");
		$Scan_Statistics_MS2_Count_Per_MS1_Scan_Block_Create_Charts_Block.empty();

		if ( _activeAjax ) {
			_activeAjax.abort();
			_activeAjax = null;
		}
	};


	var _activeAjax = null;

	/**
	 * Clear the charts.  User will click link to display the charts
	 */
	this.loadDataForScanFileId = function( params ) {

		this.clearChart();
	}


	/**
	 * Load the data for Charts
	 */
	this._loadDataForScanFileId_Internal = function( params ) {

		var scanFileId = params.scanFileId;

		_chart_isLoaded = _IS_LOADED_LOADING;

		var $Scan_Statistics_MS2_Count_Per_MS1_Scan_Block_Create_Charts_Block = $("#Scan_Statistics_MS2_Count_Per_MS1_Scan_Block_Create_Charts_Block");
		$Scan_Statistics_MS2_Count_Per_MS1_Scan_Block_Create_Charts_Block.empty();
		
		// const hash_json_Contents = _get_hash_json_Contents();

		// var selectedLinkTypes = hash_json_Contents.linkTypes;

		// Show cells for selected link types
		// selectedLinkTypes.forEach( function ( currentArrayValue, index, array ) {
		// 	var selectedLinkType = currentArrayValue;

			//  Add empty chart with Loading message
			var $chart_outer_container_jq =
				this._addChartOuterTemplate( { /* linkType : selectedLinkType, */ $chart_group_container_table_jq : $Scan_Statistics_MS2_Count_Per_MS1_Scan_Block_Create_Charts_Block } );

			//  Add empty chart with Loading message
			this._placeEmptyDummyChartForMessage( { 
				$chart_outer_container_jq : $chart_outer_container_jq, 
				//				linkType : selectedLinkType, 
				messagePrefix:  _DUMMY_CHART_STATUS_TEXT_PREFIX_LOADING,
				messageSuffix:  _DUMMY_CHART_STATUS_TEXT_SUFFIX_LOADING
			} );

		// }, this /* passed to function as this */ );


		if ( _activeAjax ) {
			_activeAjax.abort();
			_activeAjax = null;
		}

		var ajaxRequestData = {
			projectSearchIds : _project_search_ids,
			scanFileId : scanFileId
		};

		const url = "services/qc/dataPage/getScanData_AllScans_AllButPeaks";

		const webserviceCallStandardPostResult = webserviceCallStandardPost({ dataToSend : ajaxRequestData, url }); //  External Function

		const promise_webserviceCallStandardPost = webserviceCallStandardPostResult.promise; 
		_activeAjax = webserviceCallStandardPostResult.api;

		promise_webserviceCallStandardPost.catch( ( ) => { _activeAjax = null; } );

		promise_webserviceCallStandardPost.then( ({ responseData }) => {
			try {
				_activeAjax = null;
				this.loadChartData_ProcessResponse({ ajaxResponseData : responseData, url });
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
	};

	/**
	 * Process AJAX response
	 */
	this.loadChartData_ProcessResponse = function({ ajaxResponseData, url }) {

		var scansFromServer = ajaxResponseData.scans;

		if ( ! scansFromServer ) {

			const msg = "AJAX ajaxResponseData.scans not populated for url: " + url;
			console.warn( msg );
			throw Error( msg );
		}

		var $Scan_Statistics_MS2_Count_Per_MS1_Scan_Block_Create_Charts_Block = $("#Scan_Statistics_MS2_Count_Per_MS1_Scan_Block_Create_Charts_Block");
		if ( $Scan_Statistics_MS2_Count_Per_MS1_Scan_Block_Create_Charts_Block.length === 0 ) {
			throw Error( "unable to find HTML element with id 'Scan_Statistics_MS2_Count_Per_MS1_Scan_Block_Create_Charts_Block'" );
		}

		$Scan_Statistics_MS2_Count_Per_MS1_Scan_Block_Create_Charts_Block.empty();


		// for ( const scanLevel of scanLevels ) {

			// const $chart_outer_container_jq = this._addChartOuterTemplate( { $chart_group_container_table_jq : $Scan_Statistics_MS2_Count_Per_MS1_Scan_Block_Create_Charts_Block } );
			// var $chart_container_jq = this._addChartInnerTemplate( { $chart_outer_container_jq : $chart_outer_container_jq } );

			{
				const $div = $("<div></div>");

				$Scan_Statistics_MS2_Count_Per_MS1_Scan_Block_Create_Charts_Block.append( $div );

				this._addSingleChart( { 
					scansFromServer, 

					$chartContainer : $div 
				} );
			}

			//  WAS

			// this._addSingleChart( { perScan_BoxplotDataArrayFromServer : perScan_IonInjectionTime_BoxplotDataArrayFromServer, $chartContainer : $chart_container_jq } );

			//  Download Data
			
			// var hash_json_Contents = _get_hash_json_Contents();
			// //  Set link types to chart link type
			// hash_json_Contents.linkTypes = [ linkType ];
							
// 			var downloadDataCallback = function( params ) {
// //					var clickedThis = params.clickedThis;

// 				window.alert("Download Data NOT supported YET")

// 				//  Download the data for params
// 				// const dataToSend = { projectSearchIds : _project_search_ids, qcPageQueryJSONRoot : hash_json_Contents };
// 				// qc_pages_Single_Merged_Common.submitDownloadForParams( { downloadStrutsAction : _downloadStrutsAction, dataToSend } );

// 				////  Prev Commented out: qc_pages_Single_Merged_Common.submitDownloadForParams( { downloadStrutsAction : _downloadStrutsAction, project_search_ids : _project_search_ids, hash_json_Contents : hash_json_Contents } );
// 			};
			
			//  Get Help tooltip HTML
			
			// var elementId = "psm_level_block_help_tooltip_charge_state_" + linkType
			
			// var $psm_level_block_help_tooltip_charge_state_LinkType = $("#" + elementId );
			// if ( $psm_level_block_help_tooltip_charge_state_LinkType.length === 0 ) {
			// 	throw Error( "No element found with id '" + elementId + "' " );
			// }
			// var helpTooltipHTML = $psm_level_block_help_tooltip_charge_state_LinkType.html();

			// qcChartDownloadHelp.add_DownloadClickHandlers_HelpTooltip( { 
			// 	$chart_outer_container_for_download_jq :  $chart_outer_container_jq, 
			// 	downloadDataCallback : downloadDataCallback,
			// 	helpTooltipHTML : helpTooltipHTML, 
			// 	helpTooltip_Wide : false 
			// } );
			
			// Add tooltips for download links

			//  COMMENTED OUT
			// addToolTips( $chart_outer_container_jq );
			
		// }

		_chart_isLoaded = _IS_LOADED_YES;

	};

	/**
	 * Overridden for Specific elements like Chart Title and X and Y Axis labels
	 */
	var _CHARGE_CHART_GLOBALS = {
			_CHART_DEFAULT_FONT_SIZE : 12,  //  Default font size - using to set font size for tick marks.
			_TITLE_FONT_SIZE : 15, // In PX
			_AXIS_LABEL_FONT_SIZE : 14, // In PX
			_TICK_MARK_TEXT_FONT_SIZE : 14 // In PX
	};

	/**
	 * Add single Charge Chart
	 */
	this._addSingleChart = function( { scansFromServer, $chartContainer } ) {

		var chartData = [  ['Retention Time (minutes)', 'Count'] ];

		let retentionTime_MinutesMax = 0;

		const createChartEntry = ({ ms2Count_Per_MS1_ScanLocal, prev_ms1_scanLocal }) => {

			const retentionTime_Seconds = prev_ms1_scanLocal.retentionTime_Seconds
			const retentionTime_Minutes = retentionTime_Seconds / 60;
			const retentionTime_MinutesToFixed = retentionTime_Minutes.toFixed( 3 );
			const retentionTime_MinutesToFixed_Number = Number.parseFloat( retentionTime_MinutesToFixed );
			if ( retentionTime_MinutesMax < retentionTime_MinutesToFixed_Number ) {
				retentionTime_MinutesMax = retentionTime_MinutesToFixed_Number;
			}

			const chartEntryLocal = [ retentionTime_MinutesToFixed_Number, ms2Count_Per_MS1_ScanLocal ];
			return chartEntryLocal;
		}

		let prev_ms1_scan = undefined;
		let ms2Count_Per_MS1_Scan = undefined;

		let ms1Count_Total = 0;

		for ( const scan of scansFromServer ) {

			const scanLevel = scan.level;
			if ( scanLevel === 1 ) {

				ms1Count_Total++;
				
				if ( prev_ms1_scan !== undefined ) {

					const chartEntry = createChartEntry({ prev_ms1_scanLocal : prev_ms1_scan, ms2Count_Per_MS1_ScanLocal : ms2Count_Per_MS1_Scan });
					chartData.push( chartEntry );
				}
				prev_ms1_scan = scan;
				ms2Count_Per_MS1_Scan = 0;
			} else if ( scanLevel === 2 ) {
				if ( prev_ms1_scan === undefined ) {
					const msg = "scanLevel === 2  and prev_ms1_scan === undefined";
					console.warn( msg );
					throw Error( msg );
				}
				ms2Count_Per_MS1_Scan++;
			}
		}
		if ( prev_ms1_scan !== undefined ) {

			//  Add last entry:

			const chartEntry = createChartEntry({ prev_ms1_scanLocal : prev_ms1_scan, ms2Count_Per_MS1_ScanLocal : ms2Count_Per_MS1_Scan });
			chartData.push( chartEntry );
		}

		//  Change retentionTime_MinutesMax to next greater multiple of 10
		retentionTime_MinutesMax = ( Math.ceil( ( retentionTime_MinutesMax / 10 ) ) ) * 10;

		// const chartArea_Width = ms1Count_Total;

		var chartOptions = {
			title: 'MS2 Count for MS1 Scans',
			hAxis: { title: 'Retention Time (minutes)', minValue: 0, maxValue: retentionTime_MinutesMax },
			vAxis: { title: 'Count', minValue: 0 },
			legend: 'none'

			// ,chartArea : { 
					
			// 	//  Force chart to be based on number of scans in some rough way
			// 	width : chartArea_Width
			// }

			// //  based on chartArea_Width
			// , width : chartArea_Width + 200
		};

		// create the chart
		var data = google.visualization.arrayToDataTable( chartData );

		var chartFullsize = new google.visualization.ScatterChart( $chartContainer[0] );

		//  Register for chart errors
		var errorDrawingChart = function( err ) {
			//  Properties of err object
	//			id [Required] - The ID of the DOM element containing the chart, or an error message displayed instead of the chart if it cannot be rendered.
	//			message [Required] - A short message string describing the error.
	//			detailedMessage [Optional] - A detailed explanation of the error.
	//			options [Optional]- An object containing custom parameters appropriate to this error and chart type.
			
			//  This thrown string is displayed on the chart on the page as well as logged to browser console and logged to the server 
			throw Error("Chart Error: " + err.message + " :: detailed error msg: " + err.detailedMessage ); 
		}
		google.visualization.events.addListener(chartFullsize, 'error', errorDrawingChart);

		chartFullsize.draw(data, chartOptions );
		
		

	};

};

/**
 * Class Instance
 */

var qcPageChartScanFileStatistics_MS2_Count_Per_MS1_Scan = new QCPageChartScanFileStatistics_MS2_Count_Per_MS1_Scan();

 export { qcPageChartScanFileStatistics_MS2_Count_Per_MS1_Scan }
 