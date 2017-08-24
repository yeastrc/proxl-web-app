/**
 * qcPageChartIonCurrentStatistics.js
 * 
 * Javascript for the viewQC.jsp page - Chart Ion Current Statistics
 * 
 * page variable qcPageChartIonCurrentStatistics
 * 
 * All Chart data for Ion Current section
 * 
 * 		!!!!  Currently only works for single search.  
 * 
 * 		The page is designed to work with multiple merged searches 
 * 		but the code and SQL need to be reviewed to determine that the results returned are what the user expects,
 * 		especially for reported peptide level results. 
 * 
 * This code has been updated to cancel existing active AJAX calls when "Update from Database" button is clicked.
 *   This is done so that previous AJAX responses don't overlay new AJAX responses.
 */

//JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";


/**
 * Constructor 
 */
var QCPageChartIonCurrentStatistics = function() {


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
	var _getScanFilesForProjectSearchId = undefined;
	
	
	///////////
	
	//   Variables for this chart
	
	var _chart_isLoaded = _IS_LOADED_NO;

	//  Saved webservice response data
	var _scanOverallStatistics = null;
	
	/**
	 * Init page Actual - Called from qcPageMain.initActual
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
			var $scan_file_no_files_block = $("#scan_file_no_files_block");
			$scan_file_no_files_block.show();
			var $scan_file_files_loading_block = $("#scan_file_files_loading_block");
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

		var $scan_file_files_loading_block = $("#scan_file_files_loading_block");
		$scan_file_files_loading_block.hide();

		var $scan_level_display_scan_file_selector_block = $("#scan_level_display_scan_file_selector_block");
		$scan_level_display_scan_file_selector_block.show();

		var $scan_file_selected_file_statistics_display_block = $("#scan_file_selected_file_statistics_display_block");
		$scan_file_selected_file_statistics_display_block.show();

		var scanfileIdToLoadDataFor = scanFiles[ 0 ].id;
		
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
		this.loadDataForScanFileId( { scanFileId: scanFileId } );
	};

	///////////////////////

	//   Load Scan data for Scan File Id

	/**
	 * Load the data for  Scan File Id
	 */
	this.loadDataForScanFileId = function( params ) {
		var scanFileId = params.scanFileId;
		this.loadScanOverallStatistics( { scanFileId : scanFileId } );
		this.load_MS_1_IonCurrent_Histograms( { scanFileId : scanFileId } );
		this.load_MS_1_IonCurrent_Heatmap( { scanFileId : scanFileId } );
	};

	///////////////////////

	//     Scan Overall Statistics

	/**
	 * Clear data for Scan Overall Statistics
	 */
	this.clearScanOverallStastics = function() {

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
	this.loadScanOverallStatistics = function( params ) {
		var objectThis = this;
		var scanFileId = params.scanFileId;

		this.clearScanOverallStastics();

		var _URL = contextPathJSVar + "/services/qc/dataPage/getScanOverallStatistics";
		var requestData = {
				project_search_id : _project_search_ids,
				scan_file_id : scanFileId
		};
		if ( _activeAjax ) {
			_activeAjax.abort();
			_activeAjax = null;
		}

		_activeAjax =
			$.ajax({
				type : "GET",
				url : _URL,
				data : requestData,
				dataType : "json",
				traditional: true,  //  Force traditional serialization of the data sent
				//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
				//   So _project_search_ids array is passed as "project_search_id=<value>" which is what Jersey expects
				success : function(data) {
					try {
						_activeAjax = null;
						objectThis.loadScanOverallStatisticsProcessResponse(requestData, data, params );
					} catch( e ) {
						reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
						throw e;
					}
				},
				failure: function(errMsg) {
					_activeAjax = null;
					handleAJAXFailure( errMsg );
				},
				error : function(jqXHR, textStatus, errorThrown) {
					_activeAjax = null;
					handleAJAXError(jqXHR, textStatus, errorThrown);
				}
			});
	};

	/**
	 * Load the data for  Scan Overall Statistics Process AJAX response
	 */
	this.loadScanOverallStatisticsProcessResponse = function( requestData, responseData, originalParams ) {
		var scanFileId = originalParams.scanFileId;

		_scanOverallStatistics = responseData;

		if ( ! responseData.haveData ) {
			//  No data found
			var $scan_file_overall_statistics_loading_block = $("#scan_file_overall_statistics_loading_block");
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

		var $scan_file_overall_statistics_loading_block = $("#scan_file_overall_statistics_loading_block");
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

		var hash_json_field_Contents_JSONString = JSON.stringify( _get_hash_json_Contents() );

		var _URL = contextPathJSVar + "/services/qc/dataPage/ms2Counts";
		var requestData = {
				project_search_id : _project_search_ids, //  Only supported if length === 1
				scan_file_id : scanFileId,
				filterCriteria : hash_json_field_Contents_JSONString
		};

		if ( _MS2CountsActiveAjax ) {
			_MS2CountsActiveAjax.abort();
			_MS2CountsActiveAjax = null;
		}
		_MS2CountsActiveAjax =
			$.ajax({
				type : "GET",
				url : _URL,
				data : requestData,
				dataType : "json",
				traditional: true,  //  Force traditional serialization of the data sent
				//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
				//   So _project_search_ids array is passed as "project_search_id=<value>" which is what Jersey expects
				success : function(data) {
					try {
						_MS2CountsActiveAjax = null;
						objectThis.loadScanOverallStatisticsMS2CountsProcessResponse(requestData, data);
					} catch( e ) {
						reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
						throw e;
					}
				},
				failure: function(errMsg) {
					_MS2CountsActiveAjax = null;
					handleAJAXFailure( errMsg );
				},
				error : function(jqXHR, textStatus, errorThrown) {
					_MS2CountsActiveAjax = null;
					handleAJAXError(jqXHR, textStatus, errorThrown);
				}
			});

	};

	/**
	 * Load the data for  Scan Overall Statistics MS Counts Process AJAX response
	 */
	this.loadScanOverallStatisticsMS2CountsProcessResponse = function( requestData, responseData ) {

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
				var z = e;
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


	//////////////////////////////////////////////////////////////////

	//     MS1 Ion Current Histograms

	/**
	 * Clear data for  MS1 Ion Current Histograms
	 */
	this.clear_MS_1_IonCurrent_Histograms = function() {

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
	this.load_MS_1_IonCurrent_Histograms = function( params ) {
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
				project_search_id : _project_search_ids,
				scan_file_id : scanFileId
		};

		if ( _load_MS_1_IonCurrent_HistogramsActiveAjax ) {
			_load_MS_1_IonCurrent_HistogramsActiveAjax.abort();
			_load_MS_1_IonCurrent_HistogramsActiveAjax = null;
		}
		//  Set to returned jQuery XMLHttpRequest (jqXHR) object
		_load_MS_1_IonCurrent_HistogramsActiveAjax =
			$.ajax({
				url : contextPathJSVar + "/services/qc/dataPage/getScan_MS_1_IonCurrent_Histograms",
				traditional: true,  //  Force traditional serialization of the data sent
				//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
				//   So project_search_ids array is passed as "project_search_ids=<value>" which is what Jersey expects
				data : ajaxRequestData,  // The data sent as params on the URL
				dataType : "json",
				success : function( ajaxResponseData ) {
					try {
						_load_MS_1_IonCurrent_HistogramsActiveAjax = null;
						var responseParams = {
								ajaxResponseData : ajaxResponseData, 
								ajaxRequestData : ajaxRequestData
						};
						objectThis.load_MS_1_IonCurrent_HistogramsResponse( responseParams );
					} catch( e ) {
						reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
						throw e;
					}
				},
				failure: function(errMsg) {
					_load_MS_1_IonCurrent_HistogramsActiveAjax = null;
					handleAJAXFailure( errMsg );
				},
				error : function(jqXHR, textStatus, errorThrown) {
					_load_MS_1_IonCurrent_HistogramsActiveAjax = null;
					if ( objectThis._passAJAXErrorTo_handleAJAXError(jqXHR, textStatus, errorThrown) ) {
						handleAJAXError(jqXHR, textStatus, errorThrown);
					}
				}
			});
	};

	/**
	 * Process AJAX Response
	 */
	this.load_MS_1_IonCurrent_HistogramsResponse = function( params ) {
		var ajaxResponseData = params.ajaxResponseData;
		var ajaxRequestData = params.ajaxRequestData;

		var scan_MS_1_IonCurrent_HistogramsResult = ajaxResponseData.scan_MS_1_IonCurrent_HistogramsResult;

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

		var retentionTimeTooltip = 
			""; 

		var retentionTimeParams = { 
				chartData: dataForRetentionTimeChart,
				chartTitle : "MS1 Ion Current vs/ Retention Time",
				chart_X_Axis_Label : "Retention Time (s)",
				tooltip: retentionTimeTooltip,
				$chart_outer_container_jq : $MS_1_IonCurrent_RetentionTime_Histogram_Container
		};

		this._process_MS_1_IonCurrent_Histograms_Data_OneChartType( retentionTimeParams );

		var mOverZ_Tooltip = 
			""; 

		var mOverZ_Params = { 
				chartData: dataFor_M_Over_Z_Chart,
				chartTitle : "MS1 Ion Current vs/ M/Z",
				chart_X_Axis_Label : "M/Z",
				tooltip: mOverZ_Tooltip,
				$chart_outer_container_jq : $MS_1_IonCurrent_M_Over_Z_Histogram_Container
		};

		this._process_MS_1_IonCurrent_Histograms_Data_OneChartType( mOverZ_Params );
	};

	/**
	 * 
	 */
	this._process_MS_1_IonCurrent_Histograms_Data_OneChartType = function( params ) {
		var chartData = params.chartData;
		var $chart_outer_container_jq = params.$chart_outer_container_jq;

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

		chartDownload.addDownloadClickHandlers( { $chart_outer_container_for_download_jq :  $chart_outer_container_jq } );
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

	//////////////////////////////////////////////////////////////////

	//     MS1 Ion Current Heatmap

	/**
	 * Clear data for  MS1 Ion Current Heatmap
	 */
	this.clear_MS_1_IonCurrent_Heatmap = function() {

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
	this.load_MS_1_IonCurrent_Heatmap = function( params ) {
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

			//  Exit since no data to display

			return;  //  EARLY EXIT
		}

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

		var ajaxRequestData = {
				project_search_id : _project_search_ids,
				scan_file_id : scanFileId
		};

		if ( _load_MS_1_IonCurrent_HeatmapActiveAjax ) {
			_load_MS_1_IonCurrent_HeatmapActiveAjax.abort();
			_load_MS_1_IonCurrent_HeatmapActiveAjax = null;
		}
		//  Set to returned jQuery XMLHttpRequest (jqXHR) object
		_load_MS_1_IonCurrent_HeatmapActiveAjax =
			$.ajax({
				url : contextPathJSVar + "/services/qc/dataPage/getScan_MS_1_IonCurrent_HeatmapHasData",
				traditional: true,  //  Force traditional serialization of the data sent
				//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
				//   So project_search_ids array is passed as "project_search_ids=<value>" which is what Jersey expects
				data : ajaxRequestData,  // The data sent as params on the URL
				dataType : "json",
				success : function( ajaxResponseData ) {
					try {
						_load_MS_1_IonCurrent_HeatmapActiveAjax = null;
						var responseParams = {
								ajaxResponseData : ajaxResponseData, 
								ajaxRequestData : ajaxRequestData,
								scanFileId : scanFileId
						};
						objectThis.load_MS_1_IonCurrent_HeatmapResponse( responseParams );
					} catch( e ) {
						reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
						throw e;
					}
				},
				failure: function(errMsg) {
					_load_MS_1_IonCurrent_HeatmapActiveAjax = null;
					handleAJAXFailure( errMsg );
				},
				error : function(jqXHR, textStatus, errorThrown) {
					_load_MS_1_IonCurrent_HeatmapActiveAjax = null;
					if ( objectThis._passAJAXErrorTo_handleAJAXError(jqXHR, textStatus, errorThrown) ) {
						handleAJAXError(jqXHR, textStatus, errorThrown);
					}
				}
			});
	};

	/**
	 * Process AJAX Response
	 */
	this.load_MS_1_IonCurrent_HeatmapResponse = function( params ) {
		var objectThis = this;

		var ajaxResponseData = params.ajaxResponseData;
		var ajaxRequestData = params.ajaxRequestData;
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


};

/**
 * page variable 
 */

var qcPageChartIonCurrentStatistics = new QCPageChartIonCurrentStatistics();
