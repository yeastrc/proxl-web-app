/**
 * qcPageChartScanFileStatistics_OverallStatistics.js
 * 
 * Javascript for the viewQC.jsp page - Scan File Statistics
 * 
 * Under Scan File Selector Driven Subsection
 * 
 * Updated whenever the Scan File Selector changes
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
var QCPageChartScanFileStatistics_OverallStatistics = function() {

	//  From QCPageMain
	var _OVERALL_GLOBALS;

	var _project_search_ids = undefined;

	var _anySearchesHaveScanDataYes = undefined;

	var _IS_LOADED_YES = "YES";
	var _IS_LOADED_NO = "NO";
	var _IS_LOADED_LOADING = "LOADING";
	

	//  Saved webservice response data
	var _scanOverallStatistics = null;
	

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
	
	/**
	 * Init page Actual - Called from qcPageMain.initActual
	 */
	this.initActual = function( params ) {
		try {
			var objectThis = this;

			_OVERALL_GLOBALS = params.OVERALL_GLOBALS;

			_project_search_ids = params.project_search_ids;

			_anySearchesHaveScanDataYes = params.anySearchesHaveScanDataYes;

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
			
			//  Get Help tooltip HTML
			
			var $scan_level_block_help_tooltip_overall_statistics_section = $("#scan_level_block_help_tooltip_overall_statistics_section");
			if ( $scan_level_block_help_tooltip_overall_statistics_section.length === 0 ) {
				throw Error( "No element found with id 'scan_level_block_help_tooltip_overall_statistics_section' " );
			}

			var overallStatistics_helpTooltipHTML = $scan_level_block_help_tooltip_overall_statistics_section.html();
			
			var $scan_file_overall_statistics_help_block = $("#scan_file_overall_statistics_help_block");

			//  Use for adding Help
			qcChartDownloadHelp.add_DownloadClickHandlers_HelpTooltip( { $chart_outer_container_for_download_jq :  $scan_file_overall_statistics_help_block, helpTooltipHTML : overallStatistics_helpTooltipHTML, helpTooltip_Wide : true } );

		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};


	/**
	 * Add Click and onChange handlers 
	 */
	this.addClickAndOnChangeHandlers = function() {
	
	};

	/**
	 * Clear data for Scan Overall Statistics
	 */
	this.clearChart = function() {

		var $scan_file_overall_statistics_loading_block = $("#scan_file_overall_statistics_loading_block");
		$scan_file_overall_statistics_loading_block.show();
		var $scan_file_overall_statistics_no_data_block = $("#scan_file_overall_statistics_no_data_block");
		$scan_file_overall_statistics_no_data_block.hide();
		var $scan_file_overall_statistics_block = $("#scan_file_overall_statistics_block");
		$scan_file_overall_statistics_block.hide();

		if ( _activeAjax ) {
			_activeAjax.abort();
			_activeAjax = null;
		}
	};

	var _activeAjax = null;

	/**
	 * Load the data for  Scan Overall Statistics
	 */
	this.loadDataForScanFileId = function( params ) {
		var objectThis = this;
		var scanFileId = params.scanFileId;

		this.clearChart();

		var requestData = {
			projectSearchIds : _project_search_ids,
			scanFileId : scanFileId
		};
		if ( _activeAjax ) {
			_activeAjax.abort();
			_activeAjax = null;
		}

		const url = "services/qc/dataPage/getScanOverallStatistics";

		const webserviceCallStandardPostResult = webserviceCallStandardPost({ dataToSend : requestData, url }); //  External Function

		const promise_webserviceCallStandardPost = webserviceCallStandardPostResult.promise; 
		_activeAjax = webserviceCallStandardPostResult.api;

		promise_webserviceCallStandardPost.catch( ( ) => { _activeAjax = null; } );

		promise_webserviceCallStandardPost.then( ({ responseData }) => {
			try {
				_activeAjax = null;
				objectThis.loadScanOverallStatisticsProcessResponse( responseData, params );
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
	};

	/**
	 * Load the data for  Scan Overall Statistics Process AJAX response
	 */
	this.loadScanOverallStatisticsProcessResponse = function( responseData, originalParams ) {
		var scanFileId = originalParams.scanFileId;

		_scanOverallStatistics = responseData;

		if ( ! responseData.haveData ) {
			//  No data found
			const $scan_file_overall_statistics_loading_block = $("#scan_file_overall_statistics_loading_block");
			$scan_file_overall_statistics_loading_block.hide();
			var $scan_file_overall_statistics_no_data_block = $("#scan_file_overall_statistics_no_data_block");
			$scan_file_overall_statistics_no_data_block.show();
			return;  //  EARLY EXIT
		}
		var totalIonCurrent = responseData.ms_1_ScanIntensitiesSummed + responseData.ms_2_ScanIntensitiesSummed;

		var totalIonCurrentString = totalIonCurrent;
		var ms_1_ScanIntensitiesSummedString = responseData.ms_1_ScanIntensitiesSummed;
		var ms_2_ScanIntensitiesSummedString = responseData.ms_2_ScanIntensitiesSummed;
		try {
			totalIonCurrentString = totalIonCurrent.toExponential( 3 );
			ms_1_ScanIntensitiesSummedString = responseData.ms_1_ScanIntensitiesSummed.toExponential( 3 );
			ms_2_ScanIntensitiesSummedString = responseData.ms_2_ScanIntensitiesSummed.toExponential( 3 );
		} catch( e ) {
			try {
				totalIonCurrentString = totalIonCurrent.toExponential();
				ms_1_ScanIntensitiesSummedString = responseData.ms_1_ScanIntensitiesSummed.toExponential();
				ms_2_ScanIntensitiesSummedString = responseData.ms_2_ScanIntensitiesSummed.toExponential();
			} catch( e2 ) {
			}
		}

		var ms_1_ScanCountString = responseData.ms_1_ScanCount;
		var ms_2_ScanCountString = responseData.ms_2_ScanCount;
		try {
			ms_1_ScanCountString = responseData.ms_1_ScanCount.toLocaleString();
			ms_2_ScanCountString = responseData.ms_2_ScanCount.toLocaleString();
		} catch( e ) {
		}

		var $scan_file_overall_statistics_total_ion_current = $("#scan_file_overall_statistics_total_ion_current");
		$scan_file_overall_statistics_total_ion_current.text( totalIonCurrentString )

		var $scan_file_overall_statistics_total_ms1_ion_current = $("#scan_file_overall_statistics_total_ms1_ion_current");
		$scan_file_overall_statistics_total_ms1_ion_current.text( ms_1_ScanIntensitiesSummedString )
		var $scan_file_overall_statistics_total_ms2_ion_current = $("#scan_file_overall_statistics_total_ms2_ion_current");
		$scan_file_overall_statistics_total_ms2_ion_current.text( ms_2_ScanIntensitiesSummedString )

		var $scan_file_overall_statistics_number_ms1_scans = $("#scan_file_overall_statistics_number_ms1_scans");
		$scan_file_overall_statistics_number_ms1_scans.text( ms_1_ScanCountString )
		var $scan_file_overall_statistics_number_ms2_scans = $("#scan_file_overall_statistics_number_ms2_scans");
		$scan_file_overall_statistics_number_ms2_scans.text( ms_2_ScanCountString )

		const $scan_file_overall_statistics_loading_block = $("#scan_file_overall_statistics_loading_block");
		$scan_file_overall_statistics_loading_block.hide();
		var $scan_file_overall_statistics_block = $("#scan_file_overall_statistics_block");
		$scan_file_overall_statistics_block.show();

		this.loadScanOverallStatisticsMS2Counts( { scanFileId : scanFileId } );
	};
	
	//////////////////////////////////////////
	
	/////   Second AJAX call to load MS2 Counts

	var _MS2CountsActiveAjax = null;

	/**
	 * Load the data for  Scan Overall Statistics - MS2 Counts
	 */
	this.loadScanOverallStatisticsMS2Counts = function( params ) {
		var objectThis = this;
		var scanFileId = params.scanFileId;

		if ( _MS2CountsActiveAjax ) {
			_MS2CountsActiveAjax.abort();
			_MS2CountsActiveAjax = null;
		}

		const hash_json_Contents = _get_hash_json_Contents();

		const ajaxRequestData = { projectSearchIds : _project_search_ids, qcPageQueryJSONRoot : hash_json_Contents, scanFileId };

		const url = "services/qc/dataPage/ms2Counts";

		const webserviceCallStandardPostResult = webserviceCallStandardPost({ dataToSend : ajaxRequestData, url }); //  External Function

		const promise_webserviceCallStandardPost = webserviceCallStandardPostResult.promise; 
		_MS2CountsActiveAjax = webserviceCallStandardPostResult.api;

		promise_webserviceCallStandardPost.catch( ( ) => { _MS2CountsActiveAjax = null } );

		promise_webserviceCallStandardPost.then( ({ responseData }) => {
			try {
				_MS2CountsActiveAjax = null;
				objectThis.loadScanOverallStatisticsMS2CountsProcessResponse( responseData );
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
	};

	/**
	 * Load the data for  Scan Overall Statistics MS Counts Process AJAX response
	 */
	this.loadScanOverallStatisticsMS2CountsProcessResponse = function( responseData ) {

		var crosslinkCount = responseData.crosslinkCount;
		var looplinkCount = responseData.looplinkCount;
		var unlinkedCount = responseData.unlinkedCount; // includes dimers

		var ms_2_ScanCount = _scanOverallStatistics.ms_2_ScanCount;

		var totalMS2CountForFilter = crosslinkCount + looplinkCount + unlinkedCount;

		var crosslinkCountString = crosslinkCount;
		var looplinkCountString = looplinkCount;
		var unlinkedCountString = unlinkedCount;
		var totalMS2CountForFilterString = totalMS2CountForFilter;
		try {
			crosslinkCountString = crosslinkCount.toLocaleString();
			looplinkCountString = looplinkCount.toLocaleString();
			unlinkedCountString = unlinkedCount.toLocaleString();
			totalMS2CountForFilterString = totalMS2CountForFilter.toLocaleString();
		} catch( e ) {
			var z = e;
		}

		var _FRACTION_DECIMAL_PLACES = 1;

		var formatFraction = function( fraction ) {
			var fractionPercent = fraction * 100;
			return " (" + fractionPercent.toFixed( _FRACTION_DECIMAL_PLACES ) + "%)"
		}

		var crosslinkCountFractionDisplay = "";
		var looplinkCountFractionDisplay = "";
		var unlinkedCountFractionDisplay = "";
		var totalMS2CountForFilterFractionDisplay = "";

		if ( ms_2_ScanCount !== 0 ) {
			try {
				var crosslinkCountFraction = crosslinkCount / ms_2_ScanCount;
				var looplinkCountFraction = looplinkCount / ms_2_ScanCount;
				var unlinkedCountFraction = unlinkedCount / ms_2_ScanCount;
				var totalMS2CountForFilterFraction = totalMS2CountForFilter / ms_2_ScanCount;

				crosslinkCountFractionDisplay = formatFraction( crosslinkCountFraction );
				looplinkCountFractionDisplay = formatFraction( looplinkCountFraction );
				unlinkedCountFractionDisplay = formatFraction( unlinkedCountFraction );
				totalMS2CountForFilterFractionDisplay = formatFraction( totalMS2CountForFilterFraction );
			} catch( e ) {
				var znothing = e;
			}
		}			

		var totalMS2CountForFilterDisplay = totalMS2CountForFilterString + totalMS2CountForFilterFractionDisplay;
		var crosslinkCountDisplay = crosslinkCountString + crosslinkCountFractionDisplay;
		var looplinkCountDisplay = looplinkCountString + looplinkCountFractionDisplay;
		var unlinkedCountDisplay = unlinkedCountString + unlinkedCountFractionDisplay;

		var $scan_file_overall_statistics_number_ms2_scans_all_types = $("#scan_file_overall_statistics_number_ms2_scans_all_types");
		$scan_file_overall_statistics_number_ms2_scans_all_types.text( totalMS2CountForFilterDisplay );

		var $scan_file_overall_statistics_number_ms2_scans_crosslink = $("#scan_file_overall_statistics_number_ms2_scans_crosslink");
		$scan_file_overall_statistics_number_ms2_scans_crosslink.text( crosslinkCountDisplay );
		var $scan_file_overall_statistics_number_ms2_scans_looplink = $("#scan_file_overall_statistics_number_ms2_scans_looplink");
		$scan_file_overall_statistics_number_ms2_scans_looplink.text( looplinkCountDisplay );
		var $scan_file_overall_statistics_number_ms2_scans_unlinked = $("#scan_file_overall_statistics_number_ms2_scans_unlinked");
		$scan_file_overall_statistics_number_ms2_scans_unlinked.text( unlinkedCountDisplay );
	};


};

/**
 * class instance
 */

var qcPageChartScanFileStatistics_OverallStatistics = new QCPageChartScanFileStatistics_OverallStatistics();

 export { qcPageChartScanFileStatistics_OverallStatistics }
 