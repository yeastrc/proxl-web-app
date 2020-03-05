/**
 * qcPageChartScanFileStatistics_ScanFileSelector.js
 * 
 * Javascript for the viewQC.jsp page - Single Search - Chart Scan File Statistics - Scan File Selector
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
var QCPageChartScanFileStatistics_ScanFileSelector = function() {
	
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
	

	let _chartsToUpdateOnScanFilePopulateOrChange = undefined;  // Array of chart objects to update
	
	///////////
	
	//   Variables for this chart
	
	var _chart_isLoaded = _IS_LOADED_NO;
	
	var _currentScanFileId = undefined;

	this.setChartsToUpdateOnScanFilePopulateOrChange = function( chartsToUpdateOnScanFilePopulateOrChange ) {

		_chartsToUpdateOnScanFilePopulateOrChange = chartsToUpdateOnScanFilePopulateOrChange;
	}
	
	/**
	 * Init page Actual - Called from qcPageMain.initActual
	 */
	this.initActual = function( params ) {
		try {
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


		$("#scan_level_display_scan_file_selector").change(function(eventObject) {
			try {
				objectThis.scanFileSelectorChange( { changedThis : this } );
				eventObject.preventDefault();
				eventObject.stopPropagation();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		
		var $MS_1_IonCurrent_Heatmap_image_download_data_link = $("#MS_1_IonCurrent_Heatmap_image_download_data_link")
		$MS_1_IonCurrent_Heatmap_image_download_data_link.click(function(eventObject) {
			try {
				objectThis.downloadMS1_Heatmap_Data( { clickedThis : this } );
				eventObject.preventDefault();
				eventObject.stopPropagation();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		
	};



	///////////////////////

	//     Scan File Selector

	/**
	 * Clear data for Scan File Selector
	 */
	this.clearScanFileSelector = function() {

		_chart_isLoaded = _IS_LOADED_NO;

		var $scan_file_files_loading_block = $("#scan_file_files_loading_block");
		$scan_file_files_loading_block.show();
		
		var $scan_level_display_scan_file_selector_block = $("#scan_level_display_scan_file_selector_block");
		$scan_level_display_scan_file_selector_block.hide();
		var $scan_level_display_scan_file_selector = $("#scan_level_display_scan_file_selector");
		$scan_level_display_scan_file_selector.hide();
		$scan_level_display_scan_file_selector.empty();
		var $scan_level_display_single_file_filename = $("#scan_level_display_single_file_filename")
		$scan_level_display_single_file_filename.hide();

		var $scan_file_selected_file_statistics_display_block = $("#scan_file_selected_file_statistics_display_block");
		$scan_file_selected_file_statistics_display_block.hide();
	};

	/**
	 * If not loaded, call ...
	 */
	this.loadScanFileSelectorIfNeeded = function() {
		var objectThis = this;
		
		if ( _chart_isLoaded === _IS_LOADED_NO ) {

			var callback_GetScanFilesForProjectSearchId = function( params ) {
				objectThis.getScanFilesForProjectSearchId_ProcessResponse( params );
			};
			
			_getScanFilesForProjectSearchId( { 
				htmlElementScanFileTR : "#scan_level_display_scan_file_selector_block",
				addAllOption : false,
				callback: callback_GetScanFilesForProjectSearchId } );
		}
	};

	/**
	 * Process callback response from _getScanFilesForProjectSearchId
	 */
	this.getScanFilesForProjectSearchId_ProcessResponse = function( params ) {
//		var objectThis = this;
		var scanFiles = params.scanFiles;
		var selectorUpdated = params.selectorUpdated;
		if ( ! selectorUpdated ) {
			
		}
		
		if ( ! scanFiles || scanFiles.length === 0 ) {
			//  No Scan files so show message and exit
			const $scan_file_no_files_block = $("#scan_file_no_files_block");
			$scan_file_no_files_block.show();
			const $scan_file_files_loading_block = $("#scan_file_files_loading_block");
			$scan_file_files_loading_block.hide();

			return;  //  EARLY EXIT
		}
//		
//		//  For viewing single Scan File
//
//		var scanfileIdToLoadDataFor = null;
//
//		if ( scanFiles.length === 1 ) {
//			var $scan_file_single_file_block = $("#scan_file_single_file_block");
//			$scan_file_single_file_block.show();
//			var $scan_file_single_file_filename = $("#scan_file_single_file_filename");
//			var scanFile = scanFiles[ 0 ];
//			$scan_file_single_file_filename.text( scanFile.filename );
//			$scan_file_single_file_filename.data("id", scanFile.id );
//			scanfileIdToLoadDataFor = scanFile.id;
//		} else {
//			var $scan_file_selector = $("#scan_file_selector");
//			$scan_file_selector.empty();
//			var optionsHTMLarray = [];
//			for ( var scanFilesIndex = 0; scanFilesIndex < scanFiles.length; scanFilesIndex++ ) {
//				var scanFile = scanFiles[ scanFilesIndex ];
//				var html = "<option value='" + scanFile.id + "'>" + scanFile.filename + "</option>";
//				optionsHTMLarray.push( html );
//				if ( scanFilesIndex === 0 ) {
//					scanfileIdToLoadDataFor = scanFile.id;
//				}
//			}
//			var optionsHTML = optionsHTMLarray.join("");
//			$scan_file_selector.append( optionsHTML );
//
//			var $scan_file_files_loading_block = $("#scan_file_files_loading_block");
//			var $scan_file_selector_block = $("#scan_file_selector_block");
//			$scan_file_files_loading_block.hide();
//			$scan_file_selector_block.show();
//		}

		const $scan_file_files_loading_block = $("#scan_file_files_loading_block");
		$scan_file_files_loading_block.hide();

		var $scan_level_display_scan_file_selector_block = $("#scan_level_display_scan_file_selector_block");
		$scan_level_display_scan_file_selector_block.show();

		var $scan_file_selected_file_statistics_display_block = $("#scan_file_selected_file_statistics_display_block");
		$scan_file_selected_file_statistics_display_block.show();

		var scanfileIdToLoadDataFor = scanFiles[ 0 ].id;

		this.scanfileId_CurrentSelectedValue = scanfileIdToLoadDataFor;
		
		this.loadDataForScanFileId( { scanFileId: scanfileIdToLoadDataFor } );
	};

	/**
	 * Process Change of Selector of Scan File Id
	 */
	this.scanFileSelectorChange = function( params ) {
		var objectThis = this;
		var changedThis = params.changedThis;
		var $changedThis = $( changedThis );
		
		this.clearScanOverallStastics()
		this.clear_MS_1_IonCurrent_Histograms()
		this.clear_MS_1_IonCurrent_Heatmap()

		var scanFileId = $changedThis.val();

		this.scanfileId_CurrentSelectedValue = scanFileId;
		
		this.loadDataForScanFileId( { scanFileId: scanFileId } );
	};

	/**
	 * Get value for Scan File Id
	 */
	this.scanFileId_GetCurrentSelectedValue = function() {

		return this.scanfileId_CurrentSelectedValue;
	};

	///////////////////////

	//   Load Scan data for Scan File Id

	/**
	 * Load the data for  Scan File Id
	 */
	this.loadDataForScanFileId = function( params ) {
		var scanFileId = params.scanFileId;

		_currentScanFileId = scanFileId;
		
		for (  const chartToUpdateOnScanFilePopulateOrChange of _chartsToUpdateOnScanFilePopulateOrChange ) {

			chartToUpdateOnScanFilePopulateOrChange.loadDataForScanFileId({ scanFileId });
		}
	};

};

/**
 * Instance of class 
 */

var qcPageChartScanFileStatistics_ScanFileSelector = new QCPageChartScanFileStatistics_ScanFileSelector();

 export { qcPageChartScanFileStatistics_ScanFileSelector }
 