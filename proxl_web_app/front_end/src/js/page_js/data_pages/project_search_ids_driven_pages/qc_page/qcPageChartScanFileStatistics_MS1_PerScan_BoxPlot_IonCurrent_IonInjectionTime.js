/**
 * qcPageChartScanFileStatistics_MS1_PerScan_BoxPlot_IonCurrent_IonInjectionTime.js
 * 
 * Javascript for the viewQC.jsp page - Chart Scan File Statistics
 * 
 * Charts for:
 * 
 *     	MS1 Per Scan:  BoxPlot of MS2  Scan Total Ion Current and Scan Ion Injection Time (2 charts)
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
var QCPageChartScanFileStatistics_MS1_PerScan_BoxPlot_IonCurrent_IonInjectionTime = function() {

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

		const $Scan_Statistics_Per_Scan_Ion_Injection_Time_BoxPlot_Charts_Block_Create_Charts_Link = $("#Scan_Statistics_Per_Scan_Ion_Injection_Time_BoxPlot_Charts_Block_Create_Charts_Link");
		$Scan_Statistics_Per_Scan_Ion_Injection_Time_BoxPlot_Charts_Block_Create_Charts_Link.click( ( event ) => { 
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

		var $Scan_Statistics_Per_Scan_Ion_Injection_Time_BoxPlot_Charts_Block = $("#Scan_Statistics_Per_Scan_Ion_Injection_Time_BoxPlot_Charts_Block");
		$Scan_Statistics_Per_Scan_Ion_Injection_Time_BoxPlot_Charts_Block.empty();

		const $Scan_Statistics_Per_Scan_Ion_Injection_Time_BoxPlot_Charts_Block_Charts_Label = $("#Scan_Statistics_Per_Scan_Ion_Injection_Time_BoxPlot_Charts_Block_Charts_Label");
		$Scan_Statistics_Per_Scan_Ion_Injection_Time_BoxPlot_Charts_Block_Charts_Label.hide();

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

		var $Scan_Statistics_Per_Scan_Ion_Injection_Time_BoxPlot_Charts_Block = $("#Scan_Statistics_Per_Scan_Ion_Injection_Time_BoxPlot_Charts_Block");
		$Scan_Statistics_Per_Scan_Ion_Injection_Time_BoxPlot_Charts_Block.empty();
		
		const $Scan_Statistics_Per_Scan_Ion_Injection_Time_BoxPlot_Charts_Block_Charts_Label = $("#Scan_Statistics_Per_Scan_Ion_Injection_Time_BoxPlot_Charts_Block_Charts_Label");
		$Scan_Statistics_Per_Scan_Ion_Injection_Time_BoxPlot_Charts_Block_Charts_Label.hide();

		// const hash_json_Contents = _get_hash_json_Contents();

		// var selectedLinkTypes = hash_json_Contents.linkTypes;

		// Show cells for selected link types
		// selectedLinkTypes.forEach( function ( currentArrayValue, index, array ) {
		// 	var selectedLinkType = currentArrayValue;

			//  Add empty chart with Loading message
			var $chart_outer_container_jq =
				this._addChartOuterTemplate( { /* linkType : selectedLinkType, */ $chart_group_container_table_jq : $Scan_Statistics_Per_Scan_Ion_Injection_Time_BoxPlot_Charts_Block } );

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

		const url = "services/qc/dataPage/getScan_MS2_IonInjectionTime_TIC_BoxPlotData_Per_MS1_Scan";

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

		const perScan_IonInjectionTime_BoxplotDataArrayFromServer = ajaxResponseData.perScan_IonInjectionTime_BoxplotData;
		const perScan_TotalIonCurrent_BoxplotData = ajaxResponseData.perScan_TotalIonCurrent_BoxplotData;

		var $Scan_Statistics_Per_Scan_Ion_Injection_Time_BoxPlot_Charts_Block = $("#Scan_Statistics_Per_Scan_Ion_Injection_Time_BoxPlot_Charts_Block");
		if ( $Scan_Statistics_Per_Scan_Ion_Injection_Time_BoxPlot_Charts_Block.length === 0 ) {
			throw Error( "unable to find HTML element with id 'Scan_Statistics_Per_Scan_Ion_Injection_Time_BoxPlot_Charts_Block'" );
		}
		$Scan_Statistics_Per_Scan_Ion_Injection_Time_BoxPlot_Charts_Block.empty();


		if ( ( ! perScan_IonInjectionTime_BoxplotDataArrayFromServer ) || ( ! perScan_TotalIonCurrent_BoxplotData ) ) {

			//  Do NOT have data for at least 1 of the charts so do not display either chart

			const msg = "Chart data not populated for perScan_IonInjectionTime_BoxplotDataArrayFromServer or perScan_TotalIonCurrent_BoxplotData or both.  Probably missing data needed for chart.";
			console.log( msg );

			const $Scan_Statistics_Per_Scan_Ion_Injection_Time_BoxPlot_Charts_Block_Charts_DataNotAvailable = $("#Scan_Statistics_Per_Scan_Ion_Injection_Time_BoxPlot_Charts_Block_Charts_DataNotAvailable");
			$Scan_Statistics_Per_Scan_Ion_Injection_Time_BoxPlot_Charts_Block_Charts_DataNotAvailable.show();

			return;   //   EARLY RETURN
		}


		const $Scan_Statistics_Per_Scan_Ion_Injection_Time_BoxPlot_Charts_Block_Charts_DataNotAvailable = $("#Scan_Statistics_Per_Scan_Ion_Injection_Time_BoxPlot_Charts_Block_Charts_DataNotAvailable");
		$Scan_Statistics_Per_Scan_Ion_Injection_Time_BoxPlot_Charts_Block_Charts_DataNotAvailable.hide();

		const $Scan_Statistics_Per_Scan_Ion_Injection_Time_BoxPlot_Charts_Block_Charts_Label = $("#Scan_Statistics_Per_Scan_Ion_Injection_Time_BoxPlot_Charts_Block_Charts_Label");
		$Scan_Statistics_Per_Scan_Ion_Injection_Time_BoxPlot_Charts_Block_Charts_Label.show();

		const Scan_Statistics_Per_Scan_Ion_Injection_Time_BoxPlot_Charts_Block_Charts_Label_HTML = $Scan_Statistics_Per_Scan_Ion_Injection_Time_BoxPlot_Charts_Block_Charts_Label.html()

		// for ( const scanLevel of scanLevels ) {

			// const $chart_outer_container_jq = this._addChartOuterTemplate( { $chart_group_container_table_jq : $Scan_Statistics_Per_Scan_Ion_Injection_Time_BoxPlot_Charts_Block } );
			// var $chart_container_jq = this._addChartInnerTemplate( { $chart_outer_container_jq : $chart_outer_container_jq } );

			// const chartTitleAddition = ".  X Axis labels are Scan Numbers of MS1 Scans"

			const chartTitleAddition = ".  X Axis labels are Retention Time (minutes) of MS1 Scans.   !!!! Disclaimer: For Scans with ONLY Dots at Zero, that means there is NO Data !!!!"

			{
				const $div = $("<div></div>");

				$Scan_Statistics_Per_Scan_Ion_Injection_Time_BoxPlot_Charts_Block.append( $div );

				this._addSingleChart( { 
					perScan_BoxplotDataArrayFromServer : perScan_IonInjectionTime_BoxplotDataArrayFromServer, 

					chartTitle : "MS2 Ion Injection Time Per MS1 Scan Boxplot (Quartiles, Max, Min, Outliers)" + chartTitleAddition, 
					chart_X_Axis_Label : "MS2 Ion Injection Time",
					$chartContainer : $div 
				} );
			}
			{
				//  Append Disclaimer HTML 
				$Scan_Statistics_Per_Scan_Ion_Injection_Time_BoxPlot_Charts_Block.append( Scan_Statistics_Per_Scan_Ion_Injection_Time_BoxPlot_Charts_Block_Charts_Label_HTML );
			}
			{
				const $div = $("<div></div>");

				$Scan_Statistics_Per_Scan_Ion_Injection_Time_BoxPlot_Charts_Block.append( $div );
					
				this._addSingleChart( { 
					perScan_BoxplotDataArrayFromServer : perScan_TotalIonCurrent_BoxplotData, 

					chartTitle : "MS2 Total Ion Current (TIC) Per MS1 Scan Boxplot (Quartiles, Max, Min, Outliers)" + chartTitleAddition, 
					chart_X_Axis_Label : "MS2 TIC",
					$chartContainer : $div 
				} );
			}
			{
				//  Append Disclaimer HTML 
				$Scan_Statistics_Per_Scan_Ion_Injection_Time_BoxPlot_Charts_Block.append( Scan_Statistics_Per_Scan_Ion_Injection_Time_BoxPlot_Charts_Block_Charts_Label_HTML );
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
	this._addSingleChart = function( { perScan_BoxplotDataArrayFromServer, chartTitle, chart_X_Axis_Label, $chartContainer } ) {

		//  Chart Area Width

		//  Uncomment when use below
		const chartArea_Width = perScan_BoxplotDataArrayFromServer.length * 20;


		//  Get max outlierValues length
		
		var outlierValues_Max_Length = 0;

		for ( const singleScan_IonInjectionTime_BoxplotData of perScan_BoxplotDataArrayFromServer ) {

			if ( singleScan_IonInjectionTime_BoxplotData.outlierValues ) {
				if ( outlierValues_Max_Length === 0 ) {
					outlierValues_Max_Length = singleScan_IonInjectionTime_BoxplotData.outlierValues.length;
				} else {
					if ( outlierValues_Max_Length < singleScan_IonInjectionTime_BoxplotData.outlierValues.length ) {
						outlierValues_Max_Length = singleScan_IonInjectionTime_BoxplotData.outlierValues.length;
					}
				}
			}
		}

		const chartDataRows = [];

		let counter = 0;

		for ( const singleScan_IonInjectionTime_BoxplotData of perScan_BoxplotDataArrayFromServer ) {

			counter++;

			const counterCopy = counter;

			const ms1_ScanNumber = singleScan_IonInjectionTime_BoxplotData.ms1_ScanNumber;
			const ms1_RetentionTime = singleScan_IonInjectionTime_BoxplotData.ms1_RetentionTime;
			
			const ms2_dataFound = singleScan_IonInjectionTime_BoxplotData.ms2_dataFound;

			//  Box chart values
			const chartIntervalMax = singleScan_IonInjectionTime_BoxplotData.chartIntervalMax;
			const chartIntervalMin = singleScan_IonInjectionTime_BoxplotData.chartIntervalMin;
			const firstQuartile = singleScan_IonInjectionTime_BoxplotData.firstQuartile;
			const median = singleScan_IonInjectionTime_BoxplotData.median;
			const thirdQuartile = singleScan_IonInjectionTime_BoxplotData.thirdQuartile;

			const outlierValues = singleScan_IonInjectionTime_BoxplotData.outlierValues;
		}


		
		//  chart data for Google charts
		var chartData = [];

		var chartDataHeaderEntry = [ 
			'MS 1 Scan', 
			
			//  Putting style here doesn't work since that styles the lines which are hidden
			
			'Max',
			'Min',
			'First Quartile',
			'Median',
			'Third Quartile',

			//  Putting style here doesn't work since that styles the intervals
			
			{id:'max', type:'number', role:'interval'},
			{id:'min', type:'number', role:'interval'},
			
			{id:'firstQuartile', type:'number', role:'interval'},
			{id:'median', type:'number', role:'interval'},
			{id:'thirdQuartile', type:'number', role:'interval'},
			
			{role: "tooltip", 'p': {'html': true} }, // tooltip for top of top box
			
			{type:'string', role: 'style' } // Color for all of interval parts/entries for current X axis entry
			
		];
				
//		!!!!!!!!!!   Adding a variable number of outliers does not work when putting null for missing outliers for a given search id
		
		//   Add header entries for max number of outliers found across all link types
		for ( let counter = 0; counter < outlierValues_Max_Length; counter++ ) {
			chartDataHeaderEntry.push( 'Outlier Point' );
			chartDataHeaderEntry.push( {type:'string', role: 'style' } );
		}
				
		chartData.push( chartDataHeaderEntry );

		var _CHART_SIGNIFICANT_DIGITS = 5;

		var searchCountPlusOne = _project_search_ids.length + 1;
		
		// var wholeChartTooltipData = [];

		for ( const singleScan_IonInjectionTime_BoxplotData of perScan_BoxplotDataArrayFromServer ) {

			var ms1_ScanNumber = singleScan_IonInjectionTime_BoxplotData.ms1_ScanNumber;
			const ms1_RetentionTime_Seconds = singleScan_IonInjectionTime_BoxplotData.ms1_RetentionTime;

			const ms1_RetentionTime_Minutes = ms1_RetentionTime_Seconds / 60;

			let ms1_RetentionTime_MinutesDisplay = ms1_RetentionTime_Minutes;
			try {
				ms1_RetentionTime_MinutesDisplay = ms1_RetentionTime_Minutes.toFixed(3)
			} catch (e) {
			}

			//  TODO  TEMP
			// if ( ms1_ScanNumber >= 23 ) {
			// 	console.warn(" TODO  TEMP if ( ms1_ScanNumber >= 23 ) { break; } ")
			// 	break;
			// }

			// var colorForSearchEntry = _colorsPerSearch[ indexForProjectSearchId ];

			
			var chartIntervalMaxString = singleScan_IonInjectionTime_BoxplotData.chartIntervalMax;
			var thirdQuartileString = singleScan_IonInjectionTime_BoxplotData.thirdQuartile;
			var medianString = singleScan_IonInjectionTime_BoxplotData.median;
			var firstQuartileString = singleScan_IonInjectionTime_BoxplotData.firstQuartile;
			var chartIntervalMinString = singleScan_IonInjectionTime_BoxplotData.chartIntervalMin;

			// const tooltipForRetentionTime = "ms1_ScanNumber: " + ms1_ScanNumber + "\n\n" +
			// "ms1_RetentionTime_Minutes" + ms1_RetentionTime_MinutesDisplay + "\n\n";
			

			var mainBoxPlotTooltip =
					"ms1_ScanNumber: " + ms1_ScanNumber + "\n\n" +
					"ms1_RetentionTime_Minutes: " + ms1_RetentionTime_MinutesDisplay + "\n\n" +
					"Max: " + chartIntervalMaxString + "\n" +
					"Third Quartile: " + thirdQuartileString + "\n" +
					"Median: " + medianString + "\n" +
					"First Quartile: " + firstQuartileString + "\n" +
					"Min: " + chartIntervalMinString
					;

			// var wholeChartTooltipEntry = {
			// 	ms1_RetentionTime_MinutesDisplay : ms1_RetentionTime_MinutesDisplay,
			// 	max : chartIntervalMaxString,
			// 	thirdQuartile : thirdQuartileString,
			// 	median : medianString,
			// 	firstQuartile : firstQuartileString,
			// 	min : chartIntervalMinString,
			// 	// searchColor : colorForSearchEntry
			// };
			
			// wholeChartTooltipData.push( wholeChartTooltipEntry );
			
			var chartEntry = [ 
				{ v: ms1_RetentionTime_MinutesDisplay, f: ms1_RetentionTime_MinutesDisplay }, //  X axis label as well as for tooltip
				//  First list for charting for tool tips
				{ v: singleScan_IonInjectionTime_BoxplotData.chartIntervalMax , f: '\nMax Value: ' + chartIntervalMaxString },
				{ v: singleScan_IonInjectionTime_BoxplotData.chartIntervalMin , f: '\nMin Value: ' + chartIntervalMinString },
				{ v: singleScan_IonInjectionTime_BoxplotData.firstQuartile , f: '\nFirst Quartile Value: ' + firstQuartileString },
				{ v: singleScan_IonInjectionTime_BoxplotData.median , f: '\nMedian Value: ' + medianString },
				{ v: singleScan_IonInjectionTime_BoxplotData.thirdQuartile , f: '\nThird Quartile Value: ' + thirdQuartileString },
				
				//  Next list for Box Chart
				singleScan_IonInjectionTime_BoxplotData.chartIntervalMax,
				singleScan_IonInjectionTime_BoxplotData.chartIntervalMin,
				singleScan_IonInjectionTime_BoxplotData.firstQuartile,
				singleScan_IonInjectionTime_BoxplotData.median,
				singleScan_IonInjectionTime_BoxplotData.thirdQuartile,

				mainBoxPlotTooltip, // tooltip for top of top box

				'color: blue;'  // style required to make visible :  color: blue; opacity: 1;
				
				//  WAS
				// 'color: ' + colorForSearchEntry + ';'  // style required to make visible :  color: blue; opacity: 1;
			
			];
			
			if ( singleScan_IonInjectionTime_BoxplotData.outlierValues  && singleScan_IonInjectionTime_BoxplotData.outlierValues.length > 0 ) {
				//  outlierValues is not null and not empty array

				//  Place first so under the visible outlier point
				//  Add the last outlier point for each search to the max length of outlier.  Done so this X-axis entry has the same number of Y-axis entries as for Max Outliers X-axis entry
				var outlierValues_lastEntry = singleScan_IonInjectionTime_BoxplotData.outlierValues[ singleScan_IonInjectionTime_BoxplotData.outlierValues.length - 1 ];
				for ( let counter = singleScan_IonInjectionTime_BoxplotData.outlierValues.length; counter < outlierValues_Max_Length; counter++ ) {
					chartEntry.push( outlierValues_lastEntry );
					chartEntry.push( 'point { visible: false; size: 0; }' ); // style as hidden, size zero since not an actual valid point 
				}

				//  Add each outlier
				singleScan_IonInjectionTime_BoxplotData.outlierValues.forEach( function ( currentArrayValue, indexForSearchId, array ) {
					var chartOutlierString = currentArrayValue;
					chartEntry.push( { v: currentArrayValue , f: '\nOutlier Value: ' + chartOutlierString } );
					chartEntry.push( 'point { visible: true; size: 2; color: blue }' ); // style required to make visible :  color: blue; opacity: 1; 
					//  WAS
					// chartEntry.push( 'point { visible: true; size: 2; color: ' + colorForSearchEntry + ' }' ); // style required to make visible :  color: blue; opacity: 1; 
				}, this /* passed to function as this */ );

			} else {
				//  No outliers so add invisible point at the chartIntervalMax position
				for ( let counter = 0; counter < outlierValues_Max_Length; counter++ ) {
					chartEntry.push( singleScan_IonInjectionTime_BoxplotData.chartIntervalMax );
					chartEntry.push( 'point { visible: false; size: 0; }' ); // style as hidden, size zero since not an actual valid point 
				}
			}
			
			chartData.push( chartEntry );

		}

		var optionsFullsize = {


			chartArea : { 
				
				//  Force chart to be 1 pixel per scan.  Need to fix chart container DOM elements to show full chart width
				width : chartArea_Width

				//  From Prev Chart:

				// left : 140, 
				// top: 60, 
				// width: objectThis.RETENTION_TIME_COUNT_CHART_WIDTH - 200 ,  //  was 720 as measured in Chrome
				// height : objectThis.RETENTION_TIME_COUNT_CHART_HEIGHT - 120 //  was 530 as measured in Chrome
			}

			//  Force chart to be 1 pixel per scan.  Need to fix chart container DOM elements to show full chart width
			, width : chartArea_Width + 200

			,
			height : 700
			,
			//  Overridden for Specific elements like Chart Title and X and Y Axis labels
			fontSize: _CHART_GLOBALS._CHART_DEFAULT_FONT_SIZE,  //  Default font size - using to set font size for tick marks.

			title: chartTitle, // Title above chart
			titleTextStyle: {
				color : _PROXL_DEFAULT_FONT_COLOR, //  Set default font color
//					color: <string>,    // any HTML string color ('red', '#cc00cc')
//					fontName: <string>, // i.e. 'Times New Roman'
				fontSize: _CHART_GLOBALS._TITLE_FONT_SIZE, // 12, 18 whatever you want (don't specify px)
//					bold: <boolean>,    // true or false
//					italic: <boolean>   // true of false
			},
			legend: {position: 'none'},
			hAxis: {
				title: 'Retention Time on Each Scan (Minutes)'
					, titleTextStyle: { color: 'black', fontSize: _CHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
//					gridlines: {color: '#fff'}
			},
			vAxis: 
			{ 	title: chart_X_Axis_Label
				, titleTextStyle: { color: 'black', fontSize: _CHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
			},
			
			lineWidth: 0,  //  Hide lines
			
			interpolateNulls: true,   //  Supposed to continue a line when there is no value for x-axis point.  Doesn't appear to work when using for outliers
			
//				series: [{'color': '#D3362D'}],
			//  Series overrides colors when there are enough entries to cover the interval entries
//				series: [{'color': '#00FF00'},{'color': '#00FF00'},{'color': '#00FF00'},{'color': '#00FF00'},{'color': '#00FF00'},{'color': '#00FF00'},{'color': '#00FF00'},{'color': '#00FF00'},{'color': '#00FF00'}],
//				colors: [ '#0000FF' ],
			
			intervals: {
				barWidth: 1,
				boxWidth: 1,
				lineWidth: 2,
				style: 'boxes'
			},
			interval: {
				max: {
					style: 'bars',
					barWidth: 0.75, // length of horizontal bars, as a fraction of total width ( '1' for same width as boxes )
					fillOpacity: 1,
//						,
//						color: '#777'  //  Removed since overridden on a per search basis
				},
				min: {
					style: 'bars',
					barWidth: 0.75, // length of horizontal bars, as a fraction of total width ( '1' for same width as boxes )
					fillOpacity: 1
//						,
//						color: '#777'  //  Removed since overridden on a per search basis
				}
			}
		};        
		// create the chart
		var data = google.visualization.arrayToDataTable( chartData );

		var chartFullsize = new google.visualization.LineChart( $chartContainer[0] );

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

		chartFullsize.draw(data, optionsFullsize);
		
		

	};

};

/**
 * Class Instance
 */

var qcPageChartScanFileStatistics_MS1_PerScan_BoxPlot_IonCurrent_IonInjectionTime = new QCPageChartScanFileStatistics_MS1_PerScan_BoxPlot_IonCurrent_IonInjectionTime();

 export { qcPageChartScanFileStatistics_MS1_PerScan_BoxPlot_IonCurrent_IonInjectionTime }
 