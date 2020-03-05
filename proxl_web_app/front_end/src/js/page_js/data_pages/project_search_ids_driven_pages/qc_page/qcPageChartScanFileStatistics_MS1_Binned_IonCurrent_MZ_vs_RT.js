/**
 * qcPageChartScanFileStatistics_MS1_Binned_IonCurrent_MZ_vs_RT.js
 * 
 * Javascript for the viewQC.jsp page - Chart Scan File Statistics
 * 
 *  Chart:  MS1 Binned Ion Current: m/z vs/ Retention Time   - Image generated on Server Side
 * 
 * This code has been updated to cancel existing active AJAX calls when "Update from Database" button is clicked.
 *   This is done so that previous AJAX responses don't overlay new AJAX responses.
 */

//JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";


//   Variables Stored on 'window' 

const reportWebErrorToServer = window.reportWebErrorToServer;

import { webserviceCallStandardPost } from 'page_js/webservice_call_common/webserviceCallStandardPost.js';

import { qc_pages_Single_Merged_Common } from './qc_pages_Single_Merged_Common.js';

import { qcChartDownloadHelp } from './qcChart_Download_Help_HTMLBlock.js';


/**
 * Constructor 
 */
var QCPageChartScanFileStatistics_MS1_Binned_IonCurrent_MZ_vs_RT = function() {

	//  Download data URL
	var _download_MS1_VS_RetentionTime_VS_M_Over_Z_StrutsAction = "download_MS1_VS_RetentionTime_VS_M_Over_Z_ChartData.do";

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
	// var _OVERALL_GLOBALS;

	var _project_search_ids = undefined;

	var _anySearchesHaveScanDataYes = undefined;

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
	
	// var _chart_isLoaded = _IS_LOADED_NO;
	
	var _currentScanFileId = undefined;

	/**
	 * Init page Actual - Called from qcPageMain.initActual
	 */
	this.initActual = function( params ) {
		try {
			var objectThis = this;

			// _OVERALL_GLOBALS = params.OVERALL_GLOBALS;

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
			
			var $scan_level_block_help_tooltip_ms1_binned_ion_current_chart = $("#scan_level_block_help_tooltip_ms1_binned_ion_current_chart");
			if ( $scan_level_block_help_tooltip_ms1_binned_ion_current_chart.length === 0 ) {
				throw Error( "No element found with id 'scan_level_block_help_tooltip_ms1_binned_ion_current_chart' " );
			}
			
			var ms1BinnedIonCurrent_helpTooltipHTML = $scan_level_block_help_tooltip_ms1_binned_ion_current_chart.html();
			
			var $scan_file_overall_statistics_help_block = $("#scan_file_overall_statistics_help_block");

			//  Use for adding Help

			var $MS_1_IonCurrent_Heatmap_image_outer_container = $("#MS_1_IonCurrent_Heatmap_image_outer_container");

			qcChartDownloadHelp.add_DownloadClickHandlers_HelpTooltip( { $chart_outer_container_for_download_jq :  $MS_1_IonCurrent_Heatmap_image_outer_container, helpTooltipHTML : ms1BinnedIonCurrent_helpTooltipHTML, helpTooltip_Wide : false } );
			
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


	//////////////////////////////////////////////////////////////////

	//     MS1 Ion Current Heatmap

	/**
	 * Clear data for  MS1 Ion Current Heatmap
	 */
	this.clearChart = function() {

		//  Empty where image will be placed
		var $MS_1_IonCurrent_Heatmap_image_container = $("#MS_1_IonCurrent_Heatmap_image_container");
		if ( $MS_1_IonCurrent_Heatmap_image_container.length === 0 ) {
			throw Error( "unable to find HTML element with id 'MS_1_IonCurrent_Heatmap_image_container'" );
		}
		$MS_1_IonCurrent_Heatmap_image_container.empty();

		//  Show loading message
		var $MS_1_IonCurrent_Heatmap_Loading = $("#MS_1_IonCurrent_Heatmap_Loading");
		if ( $MS_1_IonCurrent_Heatmap_Loading.length === 0 ) {
			throw Error( "unable to find HTML element with id 'MS_1_IonCurrent_Heatmap_Loading'" );
		}
		$MS_1_IonCurrent_Heatmap_Loading.show();

		//  Hide No Data message
		var $MS_1_IonCurrent_Heatmap_No_Data = $("#MS_1_IonCurrent_Heatmap_No_Data");
		if ( $MS_1_IonCurrent_Heatmap_No_Data.length === 0 ) {
			throw Error( "unable to find HTML element with id 'MS_1_IonCurrent_Heatmap_No_Data'" );
		}
		$MS_1_IonCurrent_Heatmap_No_Data.hide();

		if ( _load_MS_1_IonCurrent_HeatmapActiveAjax ) {
			_load_MS_1_IonCurrent_HeatmapActiveAjax.abort();
			_load_MS_1_IonCurrent_HeatmapActiveAjax = null;
		}
	};

	var _load_MS_1_IonCurrent_HeatmapActiveAjax = null;

	/**
	 * Load the data for  MS1 Ion Current Heatmap
	 */
	this.loadDataForScanFileId = function( params ) {
		var scanFileId = params.scanFileId;

		var objectThis = this;

		//  The containers for the Heatmap

		//  Empty where image will be placed
		var $MS_1_IonCurrent_Heatmap_image_container = $("#MS_1_IonCurrent_Heatmap_image_container");
		if ( $MS_1_IonCurrent_Heatmap_image_container.length === 0 ) {
			throw Error( "unable to find HTML element with id 'MS_1_IonCurrent_Heatmap_image_container'" );
		}
		$MS_1_IonCurrent_Heatmap_image_container.empty();

		if ( ! _anySearchesHaveScanDataYes ) {
			//  No Searches have data so show this:

			//  Hide loading message
			const $MS_1_IonCurrent_Heatmap_Loading = $("#MS_1_IonCurrent_Heatmap_Loading");
			if ( $MS_1_IonCurrent_Heatmap_Loading.length === 0 ) {
				throw Error( "unable to find HTML element with id 'MS_1_IonCurrent_Heatmap_Loading'" );
			}
			$MS_1_IonCurrent_Heatmap_Loading.hide();

			//  Show No Data message
			const $MS_1_IonCurrent_Heatmap_No_Data = $("#MS_1_IonCurrent_Heatmap_No_Data");
			if ( $MS_1_IonCurrent_Heatmap_No_Data.length === 0 ) {
				throw Error( "unable to find HTML element with id 'MS_1_IonCurrent_Heatmap_No_Data'" );
			}
			$MS_1_IonCurrent_Heatmap_No_Data.show();

			//  Exit since no data to display

			return;  //  EARLY EXIT
		}

		//  Show loading message
		const $MS_1_IonCurrent_Heatmap_Loading = $("#MS_1_IonCurrent_Heatmap_Loading");
		if ( $MS_1_IonCurrent_Heatmap_Loading.length === 0 ) {
			throw Error( "unable to find HTML element with id 'MS_1_IonCurrent_Heatmap_Loading'" );
		}
		$MS_1_IonCurrent_Heatmap_Loading.show();

		//  Hide No Data message
		const $MS_1_IonCurrent_Heatmap_No_Data = $("#MS_1_IonCurrent_Heatmap_No_Data");
		if ( $MS_1_IonCurrent_Heatmap_No_Data.length === 0 ) {
			throw Error( "unable to find HTML element with id 'MS_1_IonCurrent_Heatmap_No_Data'" );
		}
		$MS_1_IonCurrent_Heatmap_No_Data.hide();

		var ajaxRequestData = {
			projectSearchIds : _project_search_ids,
			scanFileId : scanFileId
		};

		if ( _load_MS_1_IonCurrent_HeatmapActiveAjax ) {
			_load_MS_1_IonCurrent_HeatmapActiveAjax.abort();
			_load_MS_1_IonCurrent_HeatmapActiveAjax = null;
		}

		const url = "services/qc/dataPage/getScan_MS_1_IonCurrent_HeatmapHasData";

		const webserviceCallStandardPostResult = webserviceCallStandardPost({ dataToSend : ajaxRequestData, url }); //  External Function

		const promise_webserviceCallStandardPost = webserviceCallStandardPostResult.promise; 
		_load_MS_1_IonCurrent_HeatmapActiveAjax = webserviceCallStandardPostResult.api;

		promise_webserviceCallStandardPost.catch( ( ) => { _load_MS_1_IonCurrent_HeatmapActiveAjax = null; } );

		promise_webserviceCallStandardPost.then( ({ responseData }) => {
			try {
				_load_MS_1_IonCurrent_HeatmapActiveAjax = null;
				var responseParams = {
					ajaxResponseData : responseData, 
					scanFileId : scanFileId
				};
				objectThis.load_MS_1_IonCurrent_HeatmapResponse( responseParams );
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
	};

	/**
	 * Process AJAX Response
	 */
	this.load_MS_1_IonCurrent_HeatmapResponse = function( params ) {
		var objectThis = this;

		var ajaxResponseData = params.ajaxResponseData;
		var scanFileId = params.scanFileId;

		var hasData = ajaxResponseData.hasData;

		if ( ! hasData ) {
			// 

			//  Hide loading message
			var $MS_1_IonCurrent_Heatmap_Loading = $("#MS_1_IonCurrent_Heatmap_Loading");
			if ( $MS_1_IonCurrent_Heatmap_Loading.length === 0 ) {
				throw Error( "unable to find HTML element with id 'MS_1_IonCurrent_Heatmap_Loading'" );
			}
			$MS_1_IonCurrent_Heatmap_Loading.hide();

			//  Show No Data message
			var $MS_1_IonCurrent_Heatmap_No_Data = $("#MS_1_IonCurrent_Heatmap_No_Data");
			if ( $MS_1_IonCurrent_Heatmap_No_Data.length === 0 ) {
				throw Error( "unable to find HTML element with id 'MS_1_IonCurrent_Heatmap_No_Data'" );
			}
			$MS_1_IonCurrent_Heatmap_No_Data.show();

			return; // EARLY EXIT  
		}

		//  Empty where image will be placed
		var $MS_1_IonCurrent_Heatmap_image_container = $("#MS_1_IonCurrent_Heatmap_image_container");
		if ( $MS_1_IonCurrent_Heatmap_image_container.length === 0 ) {
			throw Error( "unable to find HTML element with id 'MS_1_IonCurrent_Heatmap_image_container'" );
		}
		$MS_1_IonCurrent_Heatmap_image_container.empty();

		var $MS_1_IonCurrent_Heatmap_full_size_link_template = $("#MS_1_IonCurrent_Heatmap_full_size_link_template");
		if ( $MS_1_IonCurrent_Heatmap_full_size_link_template.length === 0 ) {
			throw Error( "unable to find HTML element with id 'MS_1_IonCurrent_Heatmap_full_size_link_template'" );
		}
		var fullSizeLinkTemplate = $MS_1_IonCurrent_Heatmap_full_size_link_template.text();

		var $MS_1_IonCurrent_Heatmap_fullsize_href_prefix = $("#MS_1_IonCurrent_Heatmap_fullsize_href_prefix");
		if ( $MS_1_IonCurrent_Heatmap_fullsize_href_prefix.length === 0 ) {
			throw Error( "unable to find HTML element with id 'MS_1_IonCurrent_Heatmap_fullsize_href_prefix'" );
		}
		var imageFullSizeHrefPrefix = $MS_1_IonCurrent_Heatmap_fullsize_href_prefix.text();

		var $MS_1_IonCurrent_Heatmap_href_prefix = $("#MS_1_IonCurrent_Heatmap_href_prefix");
		if ( $MS_1_IonCurrent_Heatmap_href_prefix.length === 0 ) {
			throw Error( "unable to find HTML element with id 'MS_1_IonCurrent_Heatmap_href_prefix'" );
		}
		var imageHrefPrefix = $MS_1_IonCurrent_Heatmap_href_prefix.text();

		var imageFullSizeHref = imageFullSizeHrefPrefix;
		var imageHref = imageHrefPrefix;

		_project_search_ids.forEach( function ( currentArrayValue, index, array ) {
			var project_search_id = currentArrayValue;
			var project_search_id_ForLink = "&project_search_id=" + project_search_id;

			imageFullSizeHref += project_search_id_ForLink;
			imageHref += project_search_id_ForLink;
		}, this /* passed to function as this */ );

		var scanFileId_ForLink = "&scan_file_id=" + scanFileId;

		imageFullSizeHref += scanFileId_ForLink;
		imageHref += scanFileId_ForLink;

		var $fullSizeLink = $( fullSizeLinkTemplate ).appendTo( $MS_1_IonCurrent_Heatmap_image_container );

		$fullSizeLink.attr("href", imageFullSizeHref );

		var img = new Image();
		var $img = $( img );
		$fullSizeLink.append( $img );

		//  Add the 'load' before setting 'src' to ensure the 'load' callback is always called, even for cached images

		var callBackOnImageLoadSuccess = function( event ) {
			objectThis.load_MS_1_IonCurrent_HeatmapOnImageLoad();
		};
		var callBackOnImageLoadFail = function( event ) {
			//  Add code to handle fail, maybe display message to user
			var z = 0;
		};

		img.addEventListener('load', callBackOnImageLoadSuccess, false /* useCapture */);
		img.addEventListener('error', callBackOnImageLoadFail, false /* useCapture */);

		img.src = imageHref;
	};

	/**
	 * On Image Load
	 */
	this.load_MS_1_IonCurrent_HeatmapOnImageLoad = function() {

		//  Hide loading message
		var $MS_1_IonCurrent_Heatmap_Loading = $("#MS_1_IonCurrent_Heatmap_Loading");
		if ( $MS_1_IonCurrent_Heatmap_Loading.length === 0 ) {
			throw Error( "unable to find HTML element with id 'MS_1_IonCurrent_Heatmap_Loading'" );
		}
		$MS_1_IonCurrent_Heatmap_Loading.hide();
	};
	
	/**
	 * User clicks download MS1 Heatmap data
	 */
	this.downloadMS1_Heatmap_Data = function() {
	
		// var urlQueryParamsArray = [];
		
		if ( _project_search_ids.length !== 1 ) {
			throw Error( "project_search_ids length must be 1.  Download code server side only supports 1 project_search_id" );
		}
		
		// urlQueryParamsArray.push( "project_search_id=" + _project_search_ids[ 0 ] );

//		project_search_ids.forEach(function( projectSearchId, i, array) {
//			urlQueryParamsArray.push( "project_search_id=" + projectSearchId );
//		}, this );
		
		// urlQueryParamsArray.push( "scan_file_id=" + _currentScanFileId );

		// var urlQueryParams = urlQueryParamsArray.join( "&" );

		// var downloadURL = _download_MS1_VS_RetentionTime_VS_M_Over_Z_StrutsAction + "?" + urlQueryParams;

		// qc_pages_Single_Merged_Common.submitDownloadURL( { downloadURL : downloadURL } );
		
		const dataToSend = { projectSearchId : _project_search_ids[ 0 ], scanFileId : _currentScanFileId };
		qc_pages_Single_Merged_Common.submitDownloadForParams( { downloadStrutsAction : _download_MS1_VS_RetentionTime_VS_M_Over_Z_StrutsAction, dataToSend } );
		
		
	};

};

/**
 * Class Instance
 */

var qcPageChartScanFileStatistics_MS1_Binned_IonCurrent_MZ_vs_RT = new QCPageChartScanFileStatistics_MS1_Binned_IonCurrent_MZ_vs_RT();

 export { qcPageChartScanFileStatistics_MS1_Binned_IonCurrent_MZ_vs_RT }
 