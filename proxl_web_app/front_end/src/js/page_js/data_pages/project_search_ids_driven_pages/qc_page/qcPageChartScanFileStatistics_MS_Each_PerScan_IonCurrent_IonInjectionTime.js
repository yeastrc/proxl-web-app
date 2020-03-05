/**
 * qcPageChartScanFileStatistics_MS_Each_PerScan_IonCurrent_IonInjectionTime.js
 * 
 * Javascript for the viewQC.jsp page - Chart Scan File Statistics
 * 
 * Charts for:
 * 
 *     	Per Scan Level: Scan Total Ion Current and Scan Ion Injection Time Both Plotted on Y Axis
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
var QCPageChartScanFileStatistics_MS_Each_PerScan_IonCurrent_IonInjectionTime = function() {

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
	};

	
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

	this._isChartContainerOnPage = function() {

		const $Scan_Statistics_Per_Scan_Ion_Injection_Time_and_Total_Ion_Current_Per_Scan__PerScanLevel_NotBinned_Charts_Block = $("#Scan_Statistics_Per_Scan_Ion_Injection_Time_and_Total_Ion_Current_Per_Scan__PerScanLevel_NotBinned_Charts_Block");
		if ( $Scan_Statistics_Per_Scan_Ion_Injection_Time_and_Total_Ion_Current_Per_Scan__PerScanLevel_NotBinned_Charts_Block.length === 0 ) {

			console.log("No DOM element for chart container so not creating chart for QCPageChartScanFileStatistics_MS_Each_PerScan_IonCurrent_IonInjectionTime.  DOM id 'Scan_Statistics_Per_Scan_Ion_Injection_Time_and_Total_Ion_Current_Per_Scan__PerScanLevel_NotBinned_Charts_Block'")

			return false;  // EARLY RETURN
		}

		return true;
	}
	
	/**
	 * Init page Actual - Called from qcPageMain.initActual
	 */
	this.initActual = function( params ) {
		try {
			var objectThis = this;

			if ( ! this._isChartContainerOnPage() ) {

				return;    // EARLY RETURN
			}

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

		const $Scan_Statistics_Per_Scan_Ion_Injection_Time_and_Total_Ion_Current_Per_Scan__PerScanLevel_Charts_Block_Create_NotBinned_Charts_Link = $("#Scan_Statistics_Per_Scan_Ion_Injection_Time_and_Total_Ion_Current_Per_Scan__PerScanLevel_Charts_Block_Create_NotBinned_Charts_Link");
		$Scan_Statistics_Per_Scan_Ion_Injection_Time_and_Total_Ion_Current_Per_Scan__PerScanLevel_Charts_Block_Create_NotBinned_Charts_Link.click( ( event ) => { 
			try {
				objectThis._add_NotBinnedData_Charts();
				event.preventDefault();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
	};

	///////////////////////////////////////////

	/**
	 * Clear Chart
	 */
	this.clearChart = function() {

		_chart_isLoaded = _IS_LOADED_NO;

		if ( ! this._isChartContainerOnPage() ) {

			return;    // EARLY RETURN
		}

		var $Scan_Statistics_Per_Scan_Ion_Injection_Time_and_Total_Ion_Current_Per_Scan__PerScanLevel_Charts_Block = $("#Scan_Statistics_Per_Scan_Ion_Injection_Time_and_Total_Ion_Current_Per_Scan__PerScanLevel_Charts_Block");
		$Scan_Statistics_Per_Scan_Ion_Injection_Time_and_Total_Ion_Current_Per_Scan__PerScanLevel_Charts_Block.empty();

		const $Scan_Statistics_Per_Scan_Ion_Injection_Time_and_Total_Ion_Current_Per_Scan__PerScanLevel_NotBinned_Charts_Block = $("#Scan_Statistics_Per_Scan_Ion_Injection_Time_and_Total_Ion_Current_Per_Scan__PerScanLevel_NotBinned_Charts_Block");
		$Scan_Statistics_Per_Scan_Ion_Injection_Time_and_Total_Ion_Current_Per_Scan__PerScanLevel_NotBinned_Charts_Block.empty();

		const $Scan_Statistics_Per_Scan_Ion_Injection_Time_and_Total_Ion_Current_Per_Scan__PerScanLevel_Charts_Block_Create_NotBinned_Charts_Link_Container = $("#Scan_Statistics_Per_Scan_Ion_Injection_Time_and_Total_Ion_Current_Per_Scan__PerScanLevel_Charts_Block_Create_NotBinned_Charts_Link_Container");
		$Scan_Statistics_Per_Scan_Ion_Injection_Time_and_Total_Ion_Current_Per_Scan__PerScanLevel_Charts_Block_Create_NotBinned_Charts_Link_Container.hide();

		if ( _activeAjax ) {
			_activeAjax.abort();
			_activeAjax = null;
		}
	};


	var _activeAjax = null;

	/**
	 * Load the data for  MS1 Ion Current Histograms
	 */
	this.loadDataForScanFileId = function( params ) {

		if ( ! this._isChartContainerOnPage() ) {

			return;    // EARLY RETURN
		}
		
		var scanFileId = params.scanFileId;

		_chart_isLoaded = _IS_LOADED_LOADING;

		var $Scan_Statistics_Per_Scan_Ion_Injection_Time_and_Total_Ion_Current_Per_Scan__PerScanLevel_Charts_Block = $("#Scan_Statistics_Per_Scan_Ion_Injection_Time_and_Total_Ion_Current_Per_Scan__PerScanLevel_Charts_Block");
		$Scan_Statistics_Per_Scan_Ion_Injection_Time_and_Total_Ion_Current_Per_Scan__PerScanLevel_Charts_Block.empty();
		
		// const hash_json_Contents = _get_hash_json_Contents();

		// var selectedLinkTypes = hash_json_Contents.linkTypes;

		// Show cells for selected link types
		// selectedLinkTypes.forEach( function ( currentArrayValue, index, array ) {
		// 	var selectedLinkType = currentArrayValue;

			//  Add empty chart with Loading message
			var $chart_outer_container_jq =
				this._addChartOuterTemplate( { /* linkType : selectedLinkType, */ $chart_group_container_table_jq : $Scan_Statistics_Per_Scan_Ion_Injection_Time_and_Total_Ion_Current_Per_Scan__PerScanLevel_Charts_Block } );

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

		var $Scan_Statistics_Per_Scan_Ion_Injection_Time_and_Total_Ion_Current_Per_Scan__PerScanLevel_Charts_Block = $("#Scan_Statistics_Per_Scan_Ion_Injection_Time_and_Total_Ion_Current_Per_Scan__PerScanLevel_Charts_Block");
		if ( $Scan_Statistics_Per_Scan_Ion_Injection_Time_and_Total_Ion_Current_Per_Scan__PerScanLevel_Charts_Block.length === 0 ) {
			throw Error( "unable to find HTML element with id 'Scan_Statistics_Per_Scan_Ion_Injection_Time_and_Total_Ion_Current_Per_Scan__PerScanLevel_Charts_Block'" );
		}
		$Scan_Statistics_Per_Scan_Ion_Injection_Time_and_Total_Ion_Current_Per_Scan__PerScanLevel_Charts_Block.empty();


		for ( const scan of scansFromServer ) {

			if ( scan.ionInjectionTime_MilliSeconds === null || scan.ionInjectionTime_MilliSeconds === undefined ) {

				//  This scan does not have ionInjectionTime_MilliSeconds populated so cannot show this chart

				// const $chart_outer_container_jq =
				// 	this._addChartOuterTemplate( { $chart_group_container_table_jq : $Scan_Statistics_Per_Scan_Ion_Injection_Time_and_Total_Ion_Current_Per_Scan__PerScanLevel_Charts_Block } );
				// //  Add empty chart with No Data message
				// this._placeEmptyDummyChartForMessage( { 
				// 	$chart_outer_container_jq : $chart_outer_container_jq, 
				// 	//				linkType : selectedLinkType, 
				// 	messagePrefix:  _DUMMY_CHART_STATUS_TEXT_PREFIX_NO_DATA,
				// 	messageSuffix:  _DUMMY_CHART_STATUS_TEXT_SUFFIX_NO_DATA
				// } );

				const $Scan_Statistics_Per_Scan_Ion_Injection_Time_and_Total_Ion_Current_Per_Scan__PerScanLevel_Charts_DataNotAvailable = $("#Scan_Statistics_Per_Scan_Ion_Injection_Time_and_Total_Ion_Current_Per_Scan__PerScanLevel_Charts_DataNotAvailable");
				$Scan_Statistics_Per_Scan_Ion_Injection_Time_and_Total_Ion_Current_Per_Scan__PerScanLevel_Charts_DataNotAvailable.show();

				return;  //  EARLY RETURN
			}
		}

		const $Scan_Statistics_Per_Scan_Ion_Injection_Time_and_Total_Ion_Current_Per_Scan__PerScanLevel_Charts_DataNotAvailable = $("#Scan_Statistics_Per_Scan_Ion_Injection_Time_and_Total_Ion_Current_Per_Scan__PerScanLevel_Charts_DataNotAvailable");
		$Scan_Statistics_Per_Scan_Ion_Injection_Time_and_Total_Ion_Current_Per_Scan__PerScanLevel_Charts_DataNotAvailable.hide();

		// Separate Charts per Scan Level so Accumulate scans per scan level

		const scansPerScanLevel = new Map();  //  Map<ScanLevel, [scan] >

		for ( const scan of scansFromServer ) {

			const scanLevel = scan.level;
			let scansPerScanLevelEntry = scansPerScanLevel.get( scanLevel );
			if ( ! scansPerScanLevelEntry ) {
				scansPerScanLevelEntry = [];
				scansPerScanLevel.set( scanLevel, scansPerScanLevelEntry );
			}
			scansPerScanLevelEntry.push(  scan );
		}

		const scanLevels = Array.from( scansPerScanLevel.keys() );

		scanLevels.sort( (a,b) => {
			if ( a < b ) {
				return -1;
			}
			if ( a > b ) {
				return 1;
			}
			return 0;
		})

		//  Save Converted Server Response
		this.scansPerScanLevel_Saved = scansPerScanLevel;

		for ( const scanLevel of scanLevels ) {

			const scansForScanLevel = scansPerScanLevel.get( scanLevel );

			const $chart_outer_container_jq = this._addChartOuterTemplate( { $chart_group_container_table_jq : $Scan_Statistics_Per_Scan_Ion_Injection_Time_and_Total_Ion_Current_Per_Scan__PerScanLevel_Charts_Block } );
			var $chart_container_jq = this._addChartInnerTemplate( { $chart_outer_container_jq : $chart_outer_container_jq } );

			this._addSingleChart( { scanLevel, scansForScanLevel, $chartContainer : $chart_container_jq } );

			//  Download Data
			
			// var hash_json_Contents = _get_hash_json_Contents();
			// //  Set link types to chart link type
			// hash_json_Contents.linkTypes = [ linkType ];
							
			var downloadDataCallback = function( params ) {
//					var clickedThis = params.clickedThis;

				window.alert("Download Data NOT supported YET")

				//  Download the data for params
				// const dataToSend = { projectSearchIds : _project_search_ids, qcPageQueryJSONRoot : hash_json_Contents };
				// qc_pages_Single_Merged_Common.submitDownloadForParams( { downloadStrutsAction : _downloadStrutsAction, dataToSend } );

				////  Prev Commented out: qc_pages_Single_Merged_Common.submitDownloadForParams( { downloadStrutsAction : _downloadStrutsAction, project_search_ids : _project_search_ids, hash_json_Contents : hash_json_Contents } );
			};
			
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
			addToolTips( $chart_outer_container_jq );
			
		}

		const $Scan_Statistics_Per_Scan_Ion_Injection_Time_and_Total_Ion_Current_Per_Scan__PerScanLevel_Charts_Block_Create_NotBinned_Charts_Link_Container = $("#Scan_Statistics_Per_Scan_Ion_Injection_Time_and_Total_Ion_Current_Per_Scan__PerScanLevel_Charts_Block_Create_NotBinned_Charts_Link_Container");
		$Scan_Statistics_Per_Scan_Ion_Injection_Time_and_Total_Ion_Current_Per_Scan__PerScanLevel_Charts_Block_Create_NotBinned_Charts_Link_Container.show();

		_chart_isLoaded = _IS_LOADED_YES;

	};

	////////////////////////

	//  Single Chart Main Page

	/**
	 * Add single Chart
	 */
	this._addSingleChart = function( { scanLevel, scansForScanLevel, $chartContainer } ) {

		//  Chart Area Width

		//  Measured Chart Width for 'Standard' size QC charts
		const chartArea_Width = 313;

		const binCount = 104;  //  chartArea_Width / 3 = 104.3

		//  Uncomment when use below
		// const chartArea_Width = scansForScanLevel.length;


		let retentionTimeInSeconds_Min = undefined;
		let retentionTimeInSeconds_Max = undefined;

		for ( const scan of scansForScanLevel ) {

			const retentionTimeInSeconds = scan.retentionTime_Seconds;

			if ( retentionTimeInSeconds_Min === undefined ) {
				retentionTimeInSeconds_Min = retentionTimeInSeconds;
				retentionTimeInSeconds_Max = retentionTimeInSeconds;
			} else {
				if ( retentionTimeInSeconds_Min > retentionTimeInSeconds ) {
					retentionTimeInSeconds_Min = retentionTimeInSeconds;
				}
				if ( retentionTimeInSeconds_Max < retentionTimeInSeconds ) {
					retentionTimeInSeconds_Max = retentionTimeInSeconds;
				}
			}
		}

		const _SECONDS_IN_1_MINUTE = 60;

		const retentionTimeInMinutes_Min_Floor = Math.floor( retentionTimeInSeconds_Min / _SECONDS_IN_1_MINUTE );
		const retentionTimeInMinutes_Max_Ceil = Math.ceil( retentionTimeInSeconds_Max / _SECONDS_IN_1_MINUTE );

		const retentionTimeInMinutes_Max_Ceil_MINUS_retentionTimeInMinutes_Min_Floor = retentionTimeInMinutes_Max_Ceil - retentionTimeInMinutes_Min_Floor;

		//  Bin the data on Retention Time Per Minute

		const binSize = ( retentionTimeInMinutes_Max_Ceil - retentionTimeInMinutes_Min_Floor ) / binCount;
		const binSizeHalf = binSize / 2;

		const scansInArrayPerBin_Per_RetentionTimeMinute = [];

		for ( const scan of scansForScanLevel ) {

			const retentionTimeInSeconds = scan.retentionTime_Seconds;
			const retentionTimeInMinutes = retentionTimeInSeconds / _SECONDS_IN_1_MINUTE;
			const retentionTimeInMinutes_Floor = Math.floor( retentionTimeInMinutes );

			const binRatio = ( retentionTimeInMinutes_Floor - retentionTimeInMinutes_Min_Floor ) / retentionTimeInMinutes_Max_Ceil_MINUS_retentionTimeInMinutes_Min_Floor;
			const binIndex = Math.floor( binRatio * binCount );

			if ( binIndex > 111 ) {
				var z = 0;
			}

			if ( ! scansInArrayPerBin_Per_RetentionTimeMinute[ binIndex ] ) {
				//  No existing Array so add it
				scansInArrayPerBin_Per_RetentionTimeMinute[ binIndex ] = [ scan ];
			} else {
				//  Yes existing Array so add to it
				scansInArrayPerBin_Per_RetentionTimeMinute[ binIndex ].push( scan );
			}
		}

		const chartBucketArray = [];

		//  Process Binned Data to make Charts
		const binnedDataArrayLength = scansInArrayPerBin_Per_RetentionTimeMinute.length;

		for ( let binIndex = 0; binIndex < binnedDataArrayLength; binIndex++ ) {

			const scansForBin = scansInArrayPerBin_Per_RetentionTimeMinute[ binIndex ];

			if ( ! scansForBin ) {
				//  No Scans for this bin

				continue;  //  EARLY CONTINUE
			}

			const chartBucket = {};

			const binStartDecimal = ( ( binIndex * binSize ) ) + retentionTimeInMinutes_Min_Floor;

			if ( binIndex == 0 && binStartDecimal < 0.1 ) {
				chartBucket.binStart = 0;
			} else { 
				chartBucket.binStart = Math.round( binStartDecimal );
			}
			chartBucket.binEnd = Math.round( ( ( binIndex + 1 ) * binSize ) + retentionTimeInMinutes_Min_Floor );
			chartBucket.binCenter = binStartDecimal + binSizeHalf; //  Bucket positioned on binCenter

			//  Y Values for chartBucket:  Average of scan.totalIonCurrent_ForScan, scan.ionInjectionTime_MilliSeconds
			{  
				let totalIonCurrent_Summed = 0;
				let ionInjectionTime_MilliSeconds_Summed = 0;

				for ( const scan of scansForBin ) {
					// if ( scan.ionInjectionTime_MilliSeconds > 50 ) {
					// 	var z = 0;
					// }
					totalIonCurrent_Summed += scan.totalIonCurrent_ForScan;
					ionInjectionTime_MilliSeconds_Summed += scan.ionInjectionTime_MilliSeconds;
				}
				chartBucket.totalIonCurrent_Average = totalIonCurrent_Summed / scansForBin.length;
				chartBucket.ionInjectionTime_MilliSeconds_Average = ionInjectionTime_MilliSeconds_Summed / scansForBin.length;
				// if ( chartBucket.ionInjectionTime_MilliSeconds_Average > 50 ) {
				// 	var z = 0;
				// }
			}
			chartBucketArray.push( chartBucket );
		}

		//   Change to Bin on Retention Time and Use Retention Time on X-axis

		//      First keep existing Chart for display in overlay with 1 pixel per scan and/or very wide but X-axis is Retention Time

		const chartDataRows = [];

		let counter = 0;

		for ( const bucket of chartBucketArray ) {

			counter++;

			// var tooltipText_IonInjectionTime = ( 
			// 	"scanNumber: " + scanNumber 
			// 	+ ", scan_ionInjectionTime_MilliSeconds: " + scan_ionInjectionTime_MilliSeconds
			// );
			// var tooltipText_TotalIonCurrent = ( 
			// 	"scanNumber: " + scanNumber 
			// 	+ ", scan_totalIonCurrent_ForScan: " + scan_totalIonCurrent_ForScan 
			// );

			var binStartString = bucket.binStart;
			var binEndString = bucket.binEnd;
			try {
				binStartString = bucket.binStart.toLocaleString();
				binEndString = bucket.binEnd.toLocaleString();
			} catch( e ) {
			}
			const tooltipText_IonInjectionTime = ( 
				"<div  style='padding: 4px;'>Retention Time (Minutes) approximately: " + binStartString + " to " + binEndString
				+ "<br>Average scan_ionInjectionTime_MilliSeconds: " + bucket.ionInjectionTime_MilliSeconds_Average 
				+ "</div>"
			);
			const tooltipText_TotalIonCurrent = ( 
				"<div  style='padding: 4px;'>Retention Time (Minutes) approximately: " + binStartString + " to " + binEndString
				+ "<br>Average scan_totalIonCurrent_ForScan: " + bucket.totalIonCurrent_Average 
				+ "</div>"
			);

//			var entryAnnotationText = "+" + entryForChargeValue.chargeValue + ":" + entryForChargeValue.chargeCount;
			// const entryAnnotationText = "?entryAnnotationText??";

			var chartEntry = [ 
				bucket.binCenter, 
				
				bucket.ionInjectionTime_MilliSeconds_Average, 
				//  Tool Tip
				tooltipText_IonInjectionTime,

				bucket.totalIonCurrent_Average,
				//  Tool Tip
				tooltipText_TotalIonCurrent,

				// entryAnnotationText
				];
			chartDataRows.push( chartEntry );
			// if ( entryForChargeValue.chargeCount > maxChargeCount ) {
			// 	maxChargeCount = entryForChargeValue.chargeCount;
			// }

		}

		
		//  chart data for Google charts

		var chartDataTable = new google.visualization.DataTable();
		chartDataTable.addColumn('number', 'Retention Time (Minutes)');

		chartDataTable.addColumn('number', "Ion Injection Time");  //  Text is label for Legend
		// A column for custom tooltip content
		chartDataTable.addColumn({'type': 'string', 'role': 'tooltip', 'p': {'html': true}})

		chartDataTable.addColumn('number', "Total Ion Current");  //  Text is label for Legend
		// A column for custom tooltip content
		chartDataTable.addColumn({'type': 'string', 'role': 'tooltip', 'p': {'html': true}})

		chartDataTable.addRows( chartDataRows );

		// var vAxisTicks = this._getChargeCountTickMarks( { maxValue : maxChargeCount } );

		// var barColors = [ colorAndbarColor.color ]; // must be an array

		var chartTitle = "Scans MS" + scanLevel;
		var optionsFullsize = {
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
				//  X axis label below chart
				hAxis: { 
					title: 'Retention Time (minutes)', 
					titleTextStyle: { color: 'black', fontSize: _CHART_GLOBALS._AXIS_LABEL_FONT_SIZE },
					// Does NOT appear to Hide Axis Tick Mark Text:   axisFontSize : 0, // Hide Axis Tick Mark Text
					// textPosition: 'none' // Hide Axis Tick Mark Text - Works to Hide
				},  
				// Gives each series an axis that matches the vAxes number below.
				series: {
					0: {targetAxisIndex: 0},
					1: {targetAxisIndex: 1}
				  },
				  vAxes: {
					// Adds titles to each axis.
					0: {
						title: 'Ion Injection Time', 
						titleTextStyle: { color: 'black', fontSize: _CHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
						,baseline: 0     // always start at zero
						// ,ticks: vAxisTicks
						// ,maxValue : maxChargeCount
					}, 
					1: {
						title: 'Total Ion Current (TIC)', 
						titleTextStyle: { color: 'black', fontSize: _CHART_GLOBALS._AXIS_LABEL_FONT_SIZE } 
						,baseline: 0     // always start at zero
						,format : 'scientific'  //  displays numbers in scientific notation (e.g., 8e6)
						// ,ticks: vAxisTicks
						// ,maxValue : maxChargeCount
					}
				  },
				//  Y axis label left of chart
				// vAxis: { title: 'Count', titleTextStyle: { color: 'black', fontSize: _CHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
				// ,baseline: 0     // always start at zero
				// ,ticks: vAxisTicks
				// ,maxValue : maxChargeCount
				// },
				// legend: { position: 'none' }, //  position: 'none':  Don't show legend of bar colors in upper right corner
//				width : 500, 
//				height : 300,   // width and height of chart, otherwise controlled by enclosing div
				// colors: barColors,
				tooltip: {isHtml: true}


				// ,chartArea : { 
					
					//  Force chart to be 1 pixel per scan.  Need to fix chart container DOM elements to show full chart width
					// width : chartArea_Width

					//  From Prev Chart:

					// left : 140, 
					// top: 60, 
					// width: objectThis.RETENTION_TIME_COUNT_CHART_WIDTH - 200 ,  //  was 720 as measured in Chrome
					// height : objectThis.RETENTION_TIME_COUNT_CHART_HEIGHT - 120 //  was 530 as measured in Chrome
				// }

				//  Force chart to be 1 pixel per scan.  Need to fix chart container DOM elements to show full chart width
				// , width : chartArea_Width + 200
		};        

		//  Register for chart errors
		var errorDrawingChart = function( err ) {
			//  Properties of err object
//			id [Required] - The ID of the DOM element containing the chart, or an error message displayed instead of the chart if it cannot be rendered.
//			message [Required] - A short message string describing the error.
//			detailedMessage [Optional] - A detailed explanation of the error.
//			options [Optional]- An object containing custom parameters appropriate to this error and chart type.
			
			//  This thrown string is displayed on the chart on the page as well as logged to browser console and logged to the server 
			throw Error( "Chart Error: " + err.message + " :: detailed error msg: " + err.detailedMessage ); 
		}

		var chartFullsize = new google.visualization.LineChart( $chartContainer[0] );

		google.visualization.events.addListener( chartFullsize, 'error', errorDrawingChart );
		
		chartFullsize.draw( chartDataTable, optionsFullsize );

		//  Temp code to find <rect> that are the actual data columns
		//     Changing them to green to allow show that they are only the data columns and not other <rect> in the <svg>

//		var $rectanglesInChart_All = $chartContainer.find("rect");

//		$rectanglesInChart_All.each( function() {
//		var $rectangleInChart = $( this );
//		var rectangleFillColor = $rectangleInChart.attr("fill");
//		if ( rectangleFillColor !== undefined ) {
//		if ( rectangleFillColor.toLowerCase() === barColor.toLowerCase() ) {
//		$rectangleInChart.attr("fill","green");
//		var z = 0;
//		}
//		}
//		});

	};

	/**
	 * 
	 */
	// this._getChargeCountTickMarks = function( params ) {
	// 	var maxValue = params.maxValue;
	// 	if ( maxValue < 5 ) {
	// 		var tickMarks = [ 0 ];
	// 		for ( var counter = 1; counter <= maxValue; counter++ ) {
	// 			tickMarks.push( counter );
	// 		}
	// 		return tickMarks;
	// 	}
	// 	return undefined; //  Use defaults
	// };



	////////////////////////

	/**
	 * 
	 */
	this._add_NotBinnedData_Charts = function() {

		//  Saved Converted Server Response
		const scansPerScanLevel = this.scansPerScanLevel_Saved;

		const scanLevels = Array.from( scansPerScanLevel.keys() );

		scanLevels.sort( (a,b) => {
			if ( a < b ) {
				return -1;
			}
			if ( a > b ) {
				return 1;
			}
			return 0;
		});

		const $Scan_Statistics_Per_Scan_Ion_Injection_Time_and_Total_Ion_Current_Per_Scan__PerScanLevel_NotBinned_Charts_Block = $("#Scan_Statistics_Per_Scan_Ion_Injection_Time_and_Total_Ion_Current_Per_Scan__PerScanLevel_NotBinned_Charts_Block");

		let max_scansForScanLevel_Length = 0;


		for ( const scanLevel of scanLevels ) {

			const scansForScanLevel = scansPerScanLevel.get( scanLevel );
			const scansForScanLevel_Length = scansForScanLevel.length;

			if ( max_scansForScanLevel_Length < scansForScanLevel_Length ) {
				max_scansForScanLevel_Length = scansForScanLevel_Length;
			}
		}

		for ( const scanLevel of scanLevels ) {

			const scansForScanLevel = scansPerScanLevel.get( scanLevel );

			const $div = $("<div></div>");

			$Scan_Statistics_Per_Scan_Ion_Injection_Time_and_Total_Ion_Current_Per_Scan__PerScanLevel_NotBinned_Charts_Block.append( $div );

			this._addSingleChart_NotBinnedData( { scanLevel, scansForScanLevel, max_scansForScanLevel_Length, $chartContainer : $div } );


		}
	}

	//  Single Chart Overlay: display in overlay with 1 pixel per scan and/or very wide but X-axis is Retention Time

	/**
	 * Add single Chart
	 */
	this._addSingleChart_NotBinnedData = function( { scanLevel, scansForScanLevel, max_scansForScanLevel_Length, $chartContainer } ) {

		//  Chart Area Width

		//  Uncomment when use below
		const chartArea_Width = max_scansForScanLevel_Length;


		//   Change to Bin on Retention Time and Use Retention Time on X-axis

		//      First keep existing Chart for display in overlay with 1 pixel per scan and/or very wide but X-axis is Retention Time

		const chartDataRows = [];

		// let counter = 0;

		for ( const scan of scansForScanLevel ) {

			// counter++;

			// const counterCopy = counter;

			const scanNumber = scan.scanNumber;
			const retentionTime_Seconds = scan.retentionTime_Seconds;
			const retentionTime_Minutes = retentionTime_Seconds / 60;
			const scan_totalIonCurrent_ForScan = scan.totalIonCurrent_ForScan;
			const scan_ionInjectionTime_MilliSeconds = scan.ionInjectionTime_MilliSeconds;

			let scanNumberString = scanNumber;
			try {
				scanNumberString = scanNumber.toLocaleString();
			} catch( e ) {
			}

			let retentionTime_MinutesString = retentionTime_Minutes;
			try {
				retentionTime_MinutesString = retentionTime_Minutes.toFixed(3)
			} catch( e ) {
			}

			// var tooltipText_IonInjectionTime = ( 
			// 	"scanNumber: " + scanNumber 
			// 	+ ", scan_ionInjectionTime_MilliSeconds: " + scan_ionInjectionTime_MilliSeconds
			// );
			// var tooltipText_TotalIonCurrent = ( 
			// 	"scanNumber: " + scanNumber 
			// 	+ ", scan_totalIonCurrent_ForScan: " + scan_totalIonCurrent_ForScan 
			// );

			const tooltipText_IonInjectionTime = ( 
				"<div  style='padding: 4px;'>scanNumber: " + scanNumber 
				+ "<br>retentionTime_Minutes: " + retentionTime_MinutesString 
				+ "<br>scan_ionInjectionTime_MilliSeconds: " + scan_ionInjectionTime_MilliSeconds 
				+ "</div>"
			);
			const tooltipText_TotalIonCurrent = ( 
				"<div  style='padding: 4px;'>scanNumber: " + scanNumber 
				+ "<br>retentionTime_Minutes: " + retentionTime_MinutesString 
				+ "<br>scan_totalIonCurrent_ForScan: " + scan_totalIonCurrent_ForScan 
				+ "</div>"
			);

//			var entryAnnotationText = "+" + entryForChargeValue.chargeValue + ":" + entryForChargeValue.chargeCount;
			// const entryAnnotationText = "?entryAnnotationText??";

			var chartEntry = [ 
				retentionTime_Minutes, 
				
				scan_ionInjectionTime_MilliSeconds, 
				//  Tool Tip
				tooltipText_IonInjectionTime,

				scan_totalIonCurrent_ForScan,
				//  Tool Tip
				tooltipText_TotalIonCurrent,

				// entryAnnotationText
				];
			chartDataRows.push( chartEntry );
			// if ( entryForChargeValue.chargeCount > maxChargeCount ) {
			// 	maxChargeCount = entryForChargeValue.chargeCount;
			// }

		}

		
		//  chart data for Google charts

		var chartDataTable = new google.visualization.DataTable();
		chartDataTable.addColumn('number', 'Retention Time');

		chartDataTable.addColumn('number', "Ion Injection Time");
		// A column for custom tooltip content
		chartDataTable.addColumn({'type': 'string', 'role': 'tooltip', 'p': {'html': true}})

		chartDataTable.addColumn('number', "Total Ion Current");
		// A column for custom tooltip content
		chartDataTable.addColumn({'type': 'string', 'role': 'tooltip', 'p': {'html': true}})

		chartDataTable.addRows( chartDataRows );

		// var vAxisTicks = this._getChargeCountTickMarks( { maxValue : maxChargeCount } );

		// var barColors = [ colorAndbarColor.color ]; // must be an array

		var chartTitle = "Scans MS" + scanLevel;
		var optionsFullsize = {
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
				//  X axis label below chart
				hAxis: { 
					title: 'Retention Time (minutes)', 
					titleTextStyle: { color: 'black', fontSize: _CHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
					// Does NOT appear to Hide Axis Tick Mark Text:   axisFontSize : 0, // Hide Axis Tick Mark Text
					// ,textPosition: 'none' // Hide Axis Tick Mark Text - Works to Hide
					,baseline: 0     // always start at zero
				},  
				// Gives each series an axis that matches the vAxes number below.
				series: {
					0: {targetAxisIndex: 0},
					1: {targetAxisIndex: 1}
				  },
				  vAxes: {
					// Adds titles to each axis.
					0: {
						title: 'Ion Injection Time', 
						titleTextStyle: { color: 'black', fontSize: _CHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
						,baseline: 0     // always start at zero
						// ,ticks: vAxisTicks
						// ,maxValue : maxChargeCount
					}, 
					1: {
						title: 'Total Ion Current (TIC)', 
						titleTextStyle: { color: 'black', fontSize: _CHART_GLOBALS._AXIS_LABEL_FONT_SIZE } 
						,baseline: 0     // always start at zero
						,format : 'scientific'  //  displays numbers in scientific notation (e.g., 8e6)
						// ,ticks: vAxisTicks
						// ,maxValue : maxChargeCount
					}
				  },
				//  Y axis label left of chart
				// vAxis: { title: 'Count', titleTextStyle: { color: 'black', fontSize: _CHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
				// ,baseline: 0     // always start at zero
				// ,ticks: vAxisTicks
				// ,maxValue : maxChargeCount
				// },
				// legend: { position: 'none' }, //  position: 'none':  Don't show legend of bar colors in upper right corner
//				width : 500, 
//				height : 300,   // width and height of chart, otherwise controlled by enclosing div
				// colors: barColors,
				tooltip: {isHtml: true}


				,chartArea : { 
					
					//  Force chart to be based on number of scans in some rough way
					width : chartArea_Width

					//  From Prev Chart:

					// left : 140, 
					// top: 60, 
					// width: objectThis.RETENTION_TIME_COUNT_CHART_WIDTH - 200 ,  //  was 720 as measured in Chrome
					// height : objectThis.RETENTION_TIME_COUNT_CHART_HEIGHT - 120 //  was 530 as measured in Chrome
				}

				//  based on chartArea_Width
				, width : chartArea_Width + 200
		};        

		//  Register for chart errors
		var errorDrawingChart = function( err ) {
			//  Properties of err object
//			id [Required] - The ID of the DOM element containing the chart, or an error message displayed instead of the chart if it cannot be rendered.
//			message [Required] - A short message string describing the error.
//			detailedMessage [Optional] - A detailed explanation of the error.
//			options [Optional]- An object containing custom parameters appropriate to this error and chart type.
			
			//  This thrown string is displayed on the chart on the page as well as logged to browser console and logged to the server 
			throw Error( "Chart Error: " + err.message + " :: detailed error msg: " + err.detailedMessage ); 
		}

		var chartFullsize = new google.visualization.LineChart( $chartContainer[0] );

		google.visualization.events.addListener( chartFullsize, 'error', errorDrawingChart );
		
		chartFullsize.draw( chartDataTable, optionsFullsize );

		//  Temp code to find <rect> that are the actual data columns
		//     Changing them to green to allow show that they are only the data columns and not other <rect> in the <svg>

//		var $rectanglesInChart_All = $chartContainer.find("rect");

//		$rectanglesInChart_All.each( function() {
//		var $rectangleInChart = $( this );
//		var rectangleFillColor = $rectangleInChart.attr("fill");
//		if ( rectangleFillColor !== undefined ) {
//		if ( rectangleFillColor.toLowerCase() === barColor.toLowerCase() ) {
//		$rectangleInChart.attr("fill","green");
//		var z = 0;
//		}
//		}
//		});

	};

};

/**
 * Class Instance
 */

var qcPageChartScanFileStatistics_MS_Each_PerScan_IonCurrent_IonInjectionTime = new QCPageChartScanFileStatistics_MS_Each_PerScan_IonCurrent_IonInjectionTime();

 export { qcPageChartScanFileStatistics_MS_Each_PerScan_IonCurrent_IonInjectionTime }
 