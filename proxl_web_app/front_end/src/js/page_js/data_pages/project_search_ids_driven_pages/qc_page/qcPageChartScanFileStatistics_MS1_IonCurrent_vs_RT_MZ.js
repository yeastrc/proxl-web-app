/**
 * qcPageChartScanFileStatistics_MS1_IonCurrent_vs_RT_MZ.js
 * 
 * Javascript for the viewQC.jsp page - Chart Scan File Statistics
 * 
 * Charts for:
 * 
 *     	MS1 Ion Current vs/ Retention Time
 * 		MS1 Ion Current vs/ M/Z
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
var QCPageChartScanFileStatistics_MS1_IonCurrent_vs_RT_MZ = function() {

	//  Download data URL
	var _download_MS1_VS_RetentionTime_StrutsAction = "download_MS1_VS_RetentionTime_ChartData.do";

	//  Download data URL
	var _download_MS1_VS_M_Over_Z_StrutsAction = "download_MS1_VS_M_Over_Z_ChartData.do";

	/**
	 * Overridden for Specific elements like Chart Title and X and Y Axis labels
	 */
	// var _CHART_GLOBALS = {
	// 		_CHART_DEFAULT_FONT_SIZE : 12,  //  Default font size - using to set font size for tick marks.
	// 		_TITLE_FONT_SIZE : 15, // In PX
	// 		_AXIS_LABEL_FONT_SIZE : 14, // In PX
	// 		_TICK_MARK_TEXT_FONT_SIZE : 14 // In PX

	// 		, _ENTRY_ANNOTATION_TEXT_SIGNIFICANT_DIGITS : 2
	// }
	
	//  From QCPageMain
	var _OVERALL_GLOBALS;

	var _project_search_ids = undefined;

	var _anySearchesHaveScanDataYes = undefined;

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
	var _getScanFilesForProjectSearchId = undefined;
	
	
	///////////
	
	//   Variables for this chart
	
	var _chart_isLoaded = _IS_LOADED_NO;
	
	var _currentScanFileId = undefined;

	//  Saved webservice response data
	var _scanOverallStatistics = null;
	

	var _ionCurrentVsRetentionTime_helpTooltipHTML = undefined;
	var _ionCurrentVs_M_Over_Z_helpTooltipHTML = undefined;
	
	/**
	 * Init page Actual - Called from qcPageMain.initActual
	 */
	this.initActual = function( params ) {
		try {
			var objectThis = this;

			_OVERALL_GLOBALS = params.OVERALL_GLOBALS;

			_project_search_ids = params.project_search_ids;

			_anySearchesHaveScanDataYes = params.anySearchesHaveScanDataYes;

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

			_getScanFilesForProjectSearchId = params.getScanFilesForProjectSearchId; // function

			this.addClickAndOnChangeHandlers();
			
			//   Save help tooltip HTML
			
			var $scan_level_block_help_tooltip_ion_current_vs_retention_time_chart = $("#scan_level_block_help_tooltip_ion_current_vs_retention_time_chart");
			if ( $scan_level_block_help_tooltip_ion_current_vs_retention_time_chart.length === 0 ) {
				throw Error( "No element found with id 'scan_level_block_help_tooltip_ion_current_vs_retention_time_chart' " );
			}
			
			var $scan_level_block_help_tooltip_ion_current_vs_m_over_z_chart = $("#scan_level_block_help_tooltip_ion_current_vs_m_over_z_chart");
			if ( $scan_level_block_help_tooltip_ion_current_vs_m_over_z_chart.length === 0 ) {
				throw Error( "No element found with id 'scan_level_block_help_tooltip_ion_current_vs_m_over_z_chart' " );
			}
			
			_ionCurrentVsRetentionTime_helpTooltipHTML = $scan_level_block_help_tooltip_ion_current_vs_retention_time_chart.html();
			_ionCurrentVs_M_Over_Z_helpTooltipHTML = $scan_level_block_help_tooltip_ion_current_vs_m_over_z_chart.html();

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
	
	};

	//////////////////////////////////////////////////////////////////

	//     MS1 Ion Current Histograms

	/**
	 * Clear data for  MS1 Ion Current Histograms
	 */
	this.clearChart = function() {

		var $MS_1_IonCurrent_RetentionTime_Histogram_Container = $("#MS_1_IonCurrent_RetentionTime_Histogram_Container");
		if ( $MS_1_IonCurrent_RetentionTime_Histogram_Container.length === 0 ) {
			throw Error( "unable to find HTML element with id 'MS_1_IonCurrent_RetentionTime_Histogram_Container'" );
		}
		$MS_1_IonCurrent_RetentionTime_Histogram_Container.empty();

		if ( _load_MS_1_IonCurrent_HistogramsActiveAjax ) {
			_load_MS_1_IonCurrent_HistogramsActiveAjax.abort();
			_load_MS_1_IonCurrent_HistogramsActiveAjax = null;
		}
	};

	var _load_MS_1_IonCurrent_HistogramsActiveAjax = null;

	/**
	 * Load the data for  MS1 Ion Current Histograms
	 */
	this.loadDataForScanFileId = function( params ) {
		var scanFileId = params.scanFileId;

		var objectThis = this;

		//  The 2 containers for the Histograms

		var $MS_1_IonCurrent_RetentionTime_Histogram_Container = $("#MS_1_IonCurrent_RetentionTime_Histogram_Container");
		if ( $MS_1_IonCurrent_RetentionTime_Histogram_Container.length === 0 ) {
			throw Error( "unable to find HTML element with id 'MS_1_IonCurrent_RetentionTime_Histogram_Container'" );
		}
		$MS_1_IonCurrent_RetentionTime_Histogram_Container.empty();

		var $MS_1_IonCurrent_M_Over_Z_Histogram_Container = $("#MS_1_IonCurrent_M_Over_Z_Histogram_Container");
		if ( $MS_1_IonCurrent_M_Over_Z_Histogram_Container.length === 0 ) {
			throw Error( "unable to find HTML element with id 'MS_1_IonCurrent_M_Over_Z_Histogram_Container'" );
		}
		$MS_1_IonCurrent_M_Over_Z_Histogram_Container.empty();

		if ( ! _anySearchesHaveScanDataYes ) {
			//  No Searches have data so show this:

			// Show cells 
			//  Add empty chart with Loading message
			this._placeEmptyDummyChartForMessage( { 
				$chart_outer_container_jq : $MS_1_IonCurrent_RetentionTime_Histogram_Container, 
//				linkType : selectedLinkType, 
				messageWhole:  _DUMMY_CHART_STATUS_WHOLE_TEXT_SCANS_NOT_UPLOADED
			} );
			//  Add empty chart with Loading message
			this._placeEmptyDummyChartForMessage( { 
				$chart_outer_container_jq : $MS_1_IonCurrent_M_Over_Z_Histogram_Container, 
//				linkType : selectedLinkType, 
				messageWhole:  _DUMMY_CHART_STATUS_WHOLE_TEXT_SCANS_NOT_UPLOADED
			} );

			//  Exit since no data to display

			return;  //  EARLY EXIT
		}

		//  Add empty chart with Loading message
		this._placeEmptyDummyChartForMessage( { 
			$chart_outer_container_jq : $MS_1_IonCurrent_RetentionTime_Histogram_Container, 
			//				linkType : selectedLinkType, 
			messagePrefix:  _DUMMY_CHART_STATUS_TEXT_PREFIX_LOADING,
			messageSuffix:  _DUMMY_CHART_STATUS_TEXT_SUFFIX_LOADING
		} );

		//  Add empty chart with Loading message
		this._placeEmptyDummyChartForMessage( { 
			$chart_outer_container_jq : $MS_1_IonCurrent_M_Over_Z_Histogram_Container, 
			//				linkType : selectedLinkType, 
			messagePrefix:  _DUMMY_CHART_STATUS_TEXT_PREFIX_LOADING,
			messageSuffix:  _DUMMY_CHART_STATUS_TEXT_SUFFIX_LOADING
		} );

		var ajaxRequestData = {
			projectSearchIds : _project_search_ids,
			scanFileId : scanFileId
		};

		if ( _load_MS_1_IonCurrent_HistogramsActiveAjax ) {
			_load_MS_1_IonCurrent_HistogramsActiveAjax.abort();
			_load_MS_1_IonCurrent_HistogramsActiveAjax = null;
		}

		const url = "services/qc/dataPage/getScan_MS_1_IonCurrent_Histograms";

		const webserviceCallStandardPostResult = webserviceCallStandardPost({ dataToSend : ajaxRequestData, url }); //  External Function

		const promise_webserviceCallStandardPost = webserviceCallStandardPostResult.promise; 
		_load_MS_1_IonCurrent_HistogramsActiveAjax = webserviceCallStandardPostResult.api;

		promise_webserviceCallStandardPost.catch( ( ) => { _load_MS_1_IonCurrent_HistogramsActiveAjax = null; } );

		promise_webserviceCallStandardPost.then( ({ responseData }) => {
			try {
				_load_MS_1_IonCurrent_HistogramsActiveAjax = null;
				var responseParams = {
					ajaxResponseData : responseData, 
					scanFileId : scanFileId
				};
				objectThis.load_MS_1_IonCurrent_HistogramsResponse( responseParams );
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
	};

	/**
	 * Process AJAX Response
	 */
	this.load_MS_1_IonCurrent_HistogramsResponse = function( params ) {
		var ajaxResponseData = params.ajaxResponseData;
		var scanFileId = params.scanFileId;

//		var scan_MS_1_IonCurrent_HistogramsResult = ajaxResponseData.scan_MS_1_IonCurrent_HistogramsResult;
		var scan_MS_1_IonCurrent_HistogramsResult = ajaxResponseData;

		var dataForRetentionTimeChart = scan_MS_1_IonCurrent_HistogramsResult.dataForRetentionTimeChart;
		var dataFor_M_Over_Z_Chart = scan_MS_1_IonCurrent_HistogramsResult.dataFor_M_Over_Z_Chart;

		var $MS_1_IonCurrent_RetentionTime_Histogram_Container = $("#MS_1_IonCurrent_RetentionTime_Histogram_Container");
		if ( $MS_1_IonCurrent_RetentionTime_Histogram_Container.length === 0 ) {
			throw Error( "unable to find HTML element with id 'MS_1_IonCurrent_RetentionTime_Histogram_Container'" );
		}
		$MS_1_IonCurrent_RetentionTime_Histogram_Container.empty();

		var $MS_1_IonCurrent_M_Over_Z_Histogram_Container = $("#MS_1_IonCurrent_M_Over_Z_Histogram_Container");
		if ( $MS_1_IonCurrent_M_Over_Z_Histogram_Container.length === 0 ) {
			throw Error( "unable to find HTML element with id 'MS_1_IonCurrent_M_Over_Z_Histogram_Container'" );
		}
		$MS_1_IonCurrent_M_Over_Z_Histogram_Container.empty();

		if ( ( dataForRetentionTimeChart === null || dataForRetentionTimeChart === undefined ) &&
				( dataFor_M_Over_Z_Chart === null || dataFor_M_Over_Z_Chart === undefined ) ) {

			//  No Chart Data

			//  Add empty chart with No Data message
			this._placeEmptyDummyChartForMessage( { 
				$chart_outer_container_jq : $MS_1_IonCurrent_RetentionTime_Histogram_Container, 
//				linkType : selectedLinkType, 
				messagePrefix:  _DUMMY_CHART_STATUS_TEXT_PREFIX_NO_DATA,
				messageSuffix:  _DUMMY_CHART_STATUS_TEXT_SUFFIX_NO_DATA
			} );

			//  Add empty chart with No Data message
			this._placeEmptyDummyChartForMessage( { 
				$chart_outer_container_jq : $MS_1_IonCurrent_M_Over_Z_Histogram_Container, 
//				linkType : selectedLinkType, 
				messagePrefix:  _DUMMY_CHART_STATUS_TEXT_PREFIX_NO_DATA,
				messageSuffix:  _DUMMY_CHART_STATUS_TEXT_SUFFIX_NO_DATA
			} );

			return;  //  EARLY EXIT
		}

		if ( _project_search_ids.length !== 1 ) {
			throw Error( "project_search_ids length must be 1.  Download code server side only supports 1 project_search_id" );
		}
		
		var retentionTimeTooltip = ""; 

		//  Download Data Setup
		var download_MS1_VS_RetentionTime_DataCallback = function( params ) {
//			var clickedThis = params.clickedThis;
			//  Download the data for params
			const dataToSend = { projectSearchId : _project_search_ids[ 0 ], scanFileId : scanFileId };
			qc_pages_Single_Merged_Common.submitDownloadForParams( { downloadStrutsAction : _download_MS1_VS_RetentionTime_StrutsAction, dataToSend } );
		};

		var retentionTimeParams = { 
				chartData: dataForRetentionTimeChart,
				chartTitle : "MS1 Ion Current vs/ Retention Time",
				chart_X_Axis_Label : "Retention Time (minutes)",
				tooltip: retentionTimeTooltip,
				$chart_outer_container_jq : $MS_1_IonCurrent_RetentionTime_Histogram_Container,
				downloadDataCallback : download_MS1_VS_RetentionTime_DataCallback,
				helpTooltipHTML : _ionCurrentVsRetentionTime_helpTooltipHTML
		};

		this._process_MS_1_IonCurrent_Histograms_Data_OneChartType( retentionTimeParams );

		var mOverZ_Tooltip = ""; 

		//  Download Data Setup
		var download_MS1_VS_M_Over_Z_DataCallback = function( params ) {
//			var clickedThis = params.clickedThis;
			//  Download the data for params
			const dataToSend = { projectSearchId : _project_search_ids[ 0 ], scanFileId : scanFileId };
			qc_pages_Single_Merged_Common.submitDownloadForParams( { downloadStrutsAction : _download_MS1_VS_M_Over_Z_StrutsAction, dataToSend } );
		};
		
		var mOverZ_Params = { 
				chartData: dataFor_M_Over_Z_Chart,
				chartTitle : "MS1 Ion Current vs/ M/Z",
				chart_X_Axis_Label : "M/Z",
				tooltip: mOverZ_Tooltip,
				$chart_outer_container_jq : $MS_1_IonCurrent_M_Over_Z_Histogram_Container,
				downloadDataCallback : download_MS1_VS_M_Over_Z_DataCallback,
				helpTooltipHTML : _ionCurrentVs_M_Over_Z_helpTooltipHTML
		};

		this._process_MS_1_IonCurrent_Histograms_Data_OneChartType( mOverZ_Params );
	};

	/**
	 * 
	 */
	this._process_MS_1_IonCurrent_Histograms_Data_OneChartType = function( params ) {
		var chartData = params.chartData;
		var $chart_outer_container_jq = params.$chart_outer_container_jq;
		var downloadDataCallback = params.downloadDataCallback;
		var helpTooltipHTML = params.helpTooltipHTML;

		var chartBuckets = chartData.chartBuckets;

		if ( chartBuckets === undefined ||chartBuckets === null || chartBuckets.length === 0 ) {
			//  No data for this link type

			//  Add empty chart with No Data message
			this._placeEmptyDummyChartForMessage( { 
				$chart_outer_container_jq : $chart_outer_container_jq, 
//				linkType : linkType, 
				messagePrefix:  _DUMMY_CHART_STATUS_TEXT_PREFIX_NO_DATA,
				messageSuffix:  _DUMMY_CHART_STATUS_TEXT_SUFFIX_NO_DATA
			} );

			return;  //  EARLY exit for this array element
		}
		var $chart_container_jq = this._addChartInnerTemplate( { $chart_outer_container_jq : $chart_outer_container_jq } );

		this._add_MS_1_IonCurrent_Histograms_Chart( { chartDataParams : params, $chartContainer : $chart_container_jq } );

		//  Use for adding Help
		qcChartDownloadHelp.add_DownloadClickHandlers_HelpTooltip( { 
			$chart_outer_container_for_download_jq :  $chart_outer_container_jq, 
			downloadDataCallback : downloadDataCallback,
			helpTooltipHTML : helpTooltipHTML, 
			helpTooltip_Wide : false 
		} );

		// Add tooltips for download links
		addToolTips( $chart_outer_container_jq );
	};
	
	

	//  Overridden for Specific elements like Chart Title and X and Y Axis labels
	var _MS_1_IonCurrent_Histograms_CHART_GLOBALS = {
			_CHART_DEFAULT_FONT_SIZE : 12,  //  Default font size - using to set font size for tick marks.
			_TITLE_FONT_SIZE : 15, // In PX
			_AXIS_LABEL_FONT_SIZE : 14, // In PX
			_TICK_MARK_TEXT_FONT_SIZE : 14, // In PX
	}

	/**
	 * 
	 */
	this._add_MS_1_IonCurrent_Histograms_Chart = function( params ) {
		var chartDataParams = params.chartDataParams;
		var $chartContainer = params.$chartContainer;

		var chartDataParam = chartDataParams.chartData;
		var chartTitle = chartDataParams.chartTitle;
		var chart_X_Axis_Label = chartDataParams.chart_X_Axis_Label;
		var tooltip = chartDataParams.tooltip;

		var chartBuckets = chartDataParam.chartBuckets;

		//  chart data for Google charts
		var chartData = [];

		var chartDataHeaderEntry = [ 'H Axis', "V Axis", { role: 'style' }, {role: "tooltip", 'p': {'html': true} }
		]; 
		chartData.push( chartDataHeaderEntry );

		var maxCount = 0;

		for ( var index = 0; index < chartBuckets.length; index++ ) {
			var bucket = chartBuckets[ index ];

			//  Setting barStyle based on barColor from server
//			var barColor = "rgb(" + bucket.barColorRed + "," + bucket.barColorGreen + "," + bucket.barColorBlue + ")";
//			var barStyle = 
//			"color: " + barColor +
//			"; stroke-color: " + barColor + 
//			"; stroke-width: 1; fill-color: " + barColor + ";";

			var barStyle = _OVERALL_GLOBALS.BAR_STYLE_CROSSLINK;

			var binStartString = bucket.binStart;
			var binEndString = bucket.binEnd;
			try {
				binStartString = bucket.binStart.toLocaleString();
				binEndString = bucket.binEnd.toLocaleString();
			} catch( e ) {
			}

			var tooltipText = "<div style='padding: 4px;'>Ion Current: " + bucket.intensitySummed.toPrecision( 5 ) +
			"<br>" + chart_X_Axis_Label + " approximately " + binStartString + " to " + binEndString + "</div>";

			var chartEntry = [ 
				bucket.binCenter,  
				bucket.intensitySummed, 
				//  Style of bar
				barStyle,
				//  Tool Tip
				tooltipText
				];
			chartData.push( chartEntry );
			if ( bucket.count > maxCount ) {
				maxCount = bucket.count;
			}
		}

		var barColors = [ _OVERALL_GLOBALS.BAR_COLOR_CROSSLINK ]; // must be an array

		var optionsFullsize = {
				//  Overridden for Specific elements like Chart Title and X and Y Axis labels
				fontSize: _MS_1_IonCurrent_Histograms_CHART_GLOBALS._CHART_DEFAULT_FONT_SIZE,  //  Default font size - using to set font size for tick marks.

				title: chartTitle, // Title above chart
				titleTextStyle: {
					color : _PROXL_DEFAULT_FONT_COLOR, //  Set default font color
//					color: <string>,    // any HTML string color ('red', '#cc00cc')
//					fontName: <string>, // i.e. 'Times New Roman'
					fontSize: _MS_1_IonCurrent_Histograms_CHART_GLOBALS._TITLE_FONT_SIZE, // 12, 18 whatever you want (don't specify px)
//					bold: <boolean>,    // true or false
//					italic: <boolean>   // true of false
				},
				//  X axis label below chart
				hAxis: { title: chart_X_Axis_Label, titleTextStyle: { color: 'black', fontSize: _MS_1_IonCurrent_Histograms_CHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
				,gridlines: {  
					color: 'none'  //  No vertical grid lines on the horzontal axis
				}
				},  
				//  Y axis label left of chart
				vAxis: { title: 'Ion Current', titleTextStyle: { color: 'black', fontSize: _MS_1_IonCurrent_Histograms_CHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
				,baseline: 1     // always start at zero
//				,ticks: vAxisTicks
				,scaleType: 'log'
					,format: 'scientific'
//						,maxValue : maxCount
				},
				legend: { position: 'none' }, //  position: 'none':  Don't show legend of bar colors in upper right corner
//				width : 500, 
//				height : 300,   // width and height of chart, otherwise controlled by enclosing div
				bar: { groupWidth: '100%' },  // set bar width large to eliminate space between bars
				colors: barColors,
				tooltip: {isHtml: true}
//				,chartArea : { left : 140, top: 60, 
//				width: objectThis.RETENTION_TIME_COUNT_CHART_WIDTH - 200 ,  //  was 720 as measured in Chrome
//				height : objectThis.RETENTION_TIME_COUNT_CHART_HEIGHT - 120 }  //  was 530 as measured in Chrome
		};        
		// create the chart
		var data = google.visualization.arrayToDataTable( chartData );
		var chartFullsize = new google.visualization.ColumnChart( $chartContainer[0] );

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

var qcPageChartScanFileStatistics_MS1_IonCurrent_vs_RT_MZ = new QCPageChartScanFileStatistics_MS1_IonCurrent_vs_RT_MZ();

 export { qcPageChartScanFileStatistics_MS1_IonCurrent_vs_RT_MZ }
 