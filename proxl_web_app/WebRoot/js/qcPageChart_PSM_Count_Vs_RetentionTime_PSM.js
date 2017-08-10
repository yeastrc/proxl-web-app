/**
 * qcPageChart_PSM_Count_Vs_RetentionTime_PSM.js
 * 
 * Javascript for the viewQC.jsp page - Chart PSM Count Vs Retention Time - in PSM Level section
 * 
 * page variable qcPageChart_PSM_Count_Vs_RetentionTime_PSM
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
var QCPageChart_PSM_Count_Vs_RetentionTime_PSM = function() {


	//  CONSTANTS - Retention Time Code
	var RELOAD_RETENTION_COUNT_CHART_TIMER_DELAY = 400;  // in Milliseconds
	
//	/**
//	 * Overridden for Specific elements like Chart Title and X and Y Axis labels
//	 */
//	var _CHART_GLOBALS = {
//			_CHART_DEFAULT_FONT_SIZE : 12,  //  Default font size - using to set font size for tick marks.
//			_TITLE_FONT_SIZE : 15, // In PX
//			_AXIS_LABEL_FONT_SIZE : 14, // In PX
//			_TICK_MARK_TEXT_FONT_SIZE : 14 // In PX
//
//			, _ENTRY_ANNOTATION_TEXT_SIGNIFICANT_DIGITS : 2
//	}
	
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

	var _scanFileIds = null;

	/**
	 * Used by Retention Time Chart Code
	 */
	this.globals_RentionTimeChart = {
			currentSearchData : 
			{ 
				prevScanFileId : undefined
				,
				maxValuesInDB : {
					maxX : undefined,
					maxY : undefined
				},
				annotationTypeDataById : undefined,
				annotationTypeDataArray : undefined
			}
	};
	
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


		////////////////////
		//  Retention Time:

		// Make so clicking on the thumbnail chart opens the overlay
		$("#retention_time_outer_container_div").click(function(eventObject) {
			try {
				var clickThis = this;
				objectThis.scanRetentionTimeQCPlotClickHandler( clickThis, eventObject );
				eventObject.preventDefault();
				eventObject.stopPropagation();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});	 
		
		//  Link below thumbnail chart for opening overlay
		$("#qc_plot_scan_retention_time_link").click(function(eventObject) {
			try {
				var clickThis = this;
				objectThis.scanRetentionTimeQCPlotClickHandler( clickThis, eventObject );
				eventObject.preventDefault();
				eventObject.stopPropagation();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});	 	
		$(".scan_retention_time_qc_plot_overlay_close_parts_jq").click(function(eventObject) {
			try {
				var clickThis = this;
				if ( objectThis.reloadRetentionTimeCountChartTimerId ) {
					clearTimeout( objectThis.reloadRetentionTimeCountChartTimerId );
					objectThis.reloadRetentionTimeCountChartTimerId = null;
				}
				objectThis.closeScanRetentionTimeQCPlotOverlay( clickThis, eventObject );
				eventObject.preventDefault();
				eventObject.stopPropagation();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		$(".scan_retention_time_qc_plot_on_change_jq").change(function(eventObject) {
			try {
				objectThis.createRetentionTimeCountChartFromPageParams( );
				eventObject.preventDefault();
				eventObject.stopPropagation();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		$(".scan_retention_time_qc_plot_on_change_jq").keyup(function(eventObject) {
			try {
				if ( objectThis.reloadRetentionTimeCountChartTimerId ) {
					clearTimeout( objectThis.reloadRetentionTimeCountChartTimerId );
					objectThis.reloadRetentionTimeCountChartTimerId = null;
				}
				objectThis.reloadRetentionTimeCountChartTimerId = setTimeout( function() {
					try {
						objectThis.createRetentionTimeCountChartFromPageParams( );
					} catch( e ) {
						reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
						throw e;
					}
				}, RELOAD_RETENTION_COUNT_CHART_TIMER_DELAY );
				eventObject.preventDefault();
				eventObject.stopPropagation();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		$("#scan_retention_time_qc_plot_max_reset_button").click(function(eventObject) {
			try {
//				var clickThis = this;
				objectThis.resetRetentionTimeCountChartMaxXMaxY();
				objectThis.createRetentionTimeCountChartFromPageParams( );
				eventObject.preventDefault();
				eventObject.stopPropagation();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});

		// For Retention Time, add Download Click handlers for overlay
		var $scan_retention_time_qc_plot_chartDiv_Container = $("#scan_retention_time_qc_plot_chartDiv_Container");
		chartDownload.addDownloadClickHandlers( { $chart_outer_container_for_download_jq :  $scan_retention_time_qc_plot_chartDiv_Container } );
		
	};


	///////////////////////////////////////////

	///////////////////////////////////////////

	/////////   


	/**
	 * Clear data for 
	 */
	this.clearChart = function() {

		_chart_isLoaded = _IS_LOADED_NO;

		//  Need updating before can uncomment !!!
		
//		var $PSM_PPM_Error_CountsBlock = $("#PSM_PPM_Error_CountsBlock");
//		$PSM_PPM_Error_CountsBlock.empty();

		//  Abort any active AJAX calls
//		if ( _activeAjax ) {
//			var objKeys = Object.keys( _activeAjax ); 
//			objKeys.forEach(function( element, index, array ) {
//				var selectedLinkType = element;
//				if ( _activeAjax[ selectedLinkType ] ) {
//					_activeAjax[ selectedLinkType ].abort();
//					_activeAjax[ selectedLinkType ] = null;
//				}
//			}, this /* passed to function as this */ );
//		}
//		_activeAjax = null;
	};


	/**
	 * If not currently loaded, load
	 */
	this.loadChartIfNeeded = function() {

		if ( _chart_isLoaded === _IS_LOADED_NO ) {
			this.createRetentionTimeCountChartThumbnail();
		}
	};

//	var _activeAjax = null;

	///
	this.createRetentionTimeCountChartThumbnail = function() {

		// TODO  Hard code taking first project search id
		var projectSearchId = _project_search_ids[ 0 ];	

		//  TODO  Need this for thumbnail
		
		var $retention_time_outer_container_div = $("#retention_time_outer_container_div");
		$retention_time_outer_container_div.empty();
		
		this._placeEmptyDummyChartForMessage( { 
			$chart_outer_container_jq : $retention_time_outer_container_div, 
			//				linkType : selectedLinkType, 
			messagePrefix:  _DUMMY_CHART_STATUS_TEXT_PREFIX_LOADING,
			messageSuffix:  _DUMMY_CHART_STATUS_TEXT_SUFFIX_LOADING
		} );
		
		var hash_json_Contents = _get_hash_json_Contents();
		
		this.createRetentionTimeCountChart( {
			projectSearchId : projectSearchId,
			scanFileId : undefined, 
			scanFileName : "All",
			scansForSelectedLinkTypes : hash_json_Contents.linkTypes,
			userInputMaxXString : undefined,
			userInputMaxY : undefined,
			thumbnailChart : true,
			$retention_time_outer_container_div : $retention_time_outer_container_div } );
	};
	
/////////////////
	this.scanRetentionTimeQCPlotClickHandler = function(clickThis, eventObject) {
		var objectThis = this;
		objectThis.openScanRetentionTimeQCPlotOverlay(clickThis, eventObject);
		return;
	};

///////////
	this.closeScanRetentionTimeQCPlotOverlay = function(clickThis, eventObject) {
		$(".scan_retention_time_qc_plot_overlay_show_hide_parts_jq").hide();
		var $scan_retention_time_qc_plot_chartDiv = $("#scan_retention_time_qc_plot_chartDiv");
		$scan_retention_time_qc_plot_chartDiv.empty();
	};

///////////
	this.openScanRetentionTimeQCPlotOverlay = function(clickThis, eventObject) {
		var objectThis = this;
		var $clickThis = $(clickThis);
		
		// TODO  Hard code taking first project search id
		var projectSearchId = _project_search_ids[ 0 ];	
		
		var $scan_retention_time_qc_plot_overlay_container = $("#scan_retention_time_qc_plot_overlay_container");
		var $scan_retention_time_qc_plot_overlay_background = $("#scan_retention_time_qc_plot_overlay_background"); 
		$scan_retention_time_qc_plot_overlay_background.show();
		$scan_retention_time_qc_plot_overlay_container.show();
		
//		Position dialog over clicked link
//		get position of div containing the dialog that is inline in the page
		var $scan_retention_time_qc_plot_overlay_containing_outermost_div_inline_div = $("#scan_retention_time_qc_plot_overlay_containing_outermost_div_inline_div");
		var offset__containing_outermost_div_inline_div = $scan_retention_time_qc_plot_overlay_containing_outermost_div_inline_div.offset();
		var offsetTop__containing_outermost_div_inline_div = offset__containing_outermost_div_inline_div.top;
		var $scan_retention_time_qc_plot_overlay_container = $("#scan_retention_time_qc_plot_overlay_container");
		var scrollTopWindow = $(window).scrollTop();
		var positionAdjust = scrollTopWindow - offsetTop__containing_outermost_div_inline_div + 10;
		$scan_retention_time_qc_plot_overlay_container.css( "top", positionAdjust );

		this.globals_RentionTimeChart.currentSearchData = undefined;   ///  Reset "currentSearchData" for new search

		//		initialize all "Link Type" checkboxes to checked or not based on main page ( in hash JSON object _hash_json_Contents )
		//  First set all to unchecked
		var $scan_retention_time_qc_plot_scans_include_jq = $(".scan_retention_time_qc_plot_scans_include_jq");
		$scan_retention_time_qc_plot_scans_include_jq.each( function(   ) {
			$( this ).prop('checked',false);
		} );
		//  Set to checked for entries in _hash_json_Contents.linkTypes

		var hash_json_Contents = _get_hash_json_Contents();
		
		if ( hash_json_Contents.linkTypes ) {
			hash_json_Contents.linkTypes.forEach( function ( currentArrayValue, index, array ) {
				var entry = currentArrayValue;
				var htmlId = "scan_retention_time_qc_plot_link_type_checkbox_" + entry;
				var $checkbox = $( "#" + htmlId );
				$checkbox.prop('checked',true);
			}, this /* passed to function as this */ );
		}
		
		var callback_GetScanFilesForProjectSearchId = function( params ) {
			objectThis.getScanFilesForProjectSearchId_ProcessResponse( params );
		};
		
		_getScanFilesForProjectSearchId( { 
			htmlElementSelector : "#scan_retention_time_qc_plot_scan_file_id",
			addAllOption : true,
			callback: callback_GetScanFilesForProjectSearchId } );
	};
	
//	/
	this.getScanFilesForProjectSearchId_ProcessResponse = function( params ) {
//		var objectThis = this;
		var scanFiles = params.scanFiles;
		var selectorUpdated = params.selectorUpdated;
		if ( ! selectorUpdated ) {
			
		}
		_scanFileIds = [];
		scanFiles.forEach(function( element, index, array ) {
			var scanFile = element;
			_scanFileIds.push( scanFile.id );
		}, this )

		this.createRetentionTimeCountChartFromPageParams( );
	};

	this.removeRetentionTimeCountChart = function(  ) {
		var $scan_retention_time_qc_plot_chartDiv = $("#scan_retention_time_qc_plot_chartDiv");
		$scan_retention_time_qc_plot_chartDiv.empty();
	};

//	/  
	this.createRetentionTimeCountChartFromPageParams = function( ) {
		var objectThis = this;
		if ( objectThis.reloadRetentionTimeCountChartTimerId ) {
			clearTimeout( objectThis.reloadRetentionTimeCountChartTimerId );
			objectThis.reloadRetentionTimeCountChartTimerId = null;
		}

		//  Hack to remove tooltip from Thumbnail chart
		var $google_visualization_tooltip = $(".google-visualization-tooltip");
		$google_visualization_tooltip.remove();  //  remove element and children

		// TODO  Hard code taking first project search id
		var projectSearchId = _project_search_ids[ 0 ];	
		
		$(".scan_retention_time_qc_plot_filter_psms_by_param_not_a_number_jq").hide();
		$(".scan_retention_time_qc_plot_param_not_a_number_jq").hide();
		
//		$(".scan_retention_time_qc_plot_have_data_jq").hide();
		
		var $scan_retention_time_qc_plot_scan_file_id = $("#scan_retention_time_qc_plot_scan_file_id");
		var scanFileId = $scan_retention_time_qc_plot_scan_file_id.val();
		var scanFileName = $( "#scan_retention_time_qc_plot_scan_file_id option:selected" ).text();

		var $scan_retention_time_qc_plot_max_x = $("#scan_retention_time_qc_plot_max_x");
		var $scan_retention_time_qc_plot_max_y = $("#scan_retention_time_qc_plot_max_y");
		if ( ! this.globals_RentionTimeChart.currentSearchData ) {
			this.globals_RentionTimeChart.currentSearchData = {};
		}
		if ( scanFileId != this.globals_RentionTimeChart.currentSearchData.prevScanFileId ) {
//			Different scan file so clear these values
			this.globals_RentionTimeChart.currentSearchData.maxValuesInDB = undefined;  // clear values
			$scan_retention_time_qc_plot_max_x.val("");  // clear values
			$scan_retention_time_qc_plot_max_y.val("");  // clear values
		}
		this.globals_RentionTimeChart.currentSearchData.prevScanFileId = scanFileId;
		var userInputMaxXString = $scan_retention_time_qc_plot_max_x.val();
		var userInputMaxY = $scan_retention_time_qc_plot_max_y.val();
		if ( userInputMaxXString !== "" ) {
//			only test for valid Max X value if not empty string
			if ( ! /^[+-]?((\d+(\.\d*)?)|(\.\d+))$/.test( userInputMaxXString ) ) {
//				Max X value is not a valid decimal number
				this.removeRetentionTimeCountChart();
				$(".scan_retention_time_qc_plot_param_not_a_number_jq").show();
				$scan_retention_time_qc_plot_max_x.focus();
				return;  //  EARLY EXIT
			}
		}
		if ( userInputMaxY !== "" ) {
//			only test for valid Max Y value if not empty string
			if ( ! /^[+-]?((\d+(\.\d*)?)|(\.\d+))$/.test( userInputMaxY ) ) {
//				Max X value is not a valid decimal number
				this.removeRetentionTimeCountChart();
				$(".scan_retention_time_qc_plot_param_not_a_number_jq").show();
				$scan_retention_time_qc_plot_max_y.focus();
				return;  //  EARLY EXIT
			}
		}
		var scansForSelectedLinkTypes = [];
		var $scan_retention_time_qc_plot_scans_include_jq = $(".scan_retention_time_qc_plot_scans_include_jq");
		$scan_retention_time_qc_plot_scans_include_jq.each( function(   ) {
			var $thisCheckbox = $( this );
			if ( $thisCheckbox.prop('checked') ) {
				var checkboxValue = $thisCheckbox.attr("value");
				scansForSelectedLinkTypes.push( checkboxValue );
			}
		} );
		
		this.removeRetentionTimeCountChart();
		var $scan_retention_time_qc_plot_chartDiv = $("#scan_retention_time_qc_plot_chartDiv");

		this.createRetentionTimeCountChart( { 
			projectSearchId : projectSearchId,
			scanFileId : scanFileId, 
			scanFileName : scanFileName,
			scansForSelectedLinkTypes : scansForSelectedLinkTypes,
			userInputMaxXString : userInputMaxXString,
			userInputMaxY : userInputMaxY,
			$scan_retention_time_qc_plot_chartDiv : $scan_retention_time_qc_plot_chartDiv } );
		
	};

//	/  
	this.createRetentionTimeCountChart = function( params ) {
		var objectThis = this;
		var projectSearchId = params.projectSearchId;
		var scanFileId = params.scanFileId;
		var scansForSelectedLinkTypes = params.scansForSelectedLinkTypes;
		var userInputMaxXString = params.userInputMaxXString;
		var thumbnailChart = params.thumbnailChart;
		
		var hash_json_Contents = _get_hash_json_Contents();
		
//		if ( ! qcRetentionTimeChartInitialized ) {
//			throw "qcRetentionTimeChartInitialized is false"; 
//		}
		var _URL = contextPathJSVar + "/services/qcplot/getScanRetentionTime";

		var hash_json_field_Contents_JSONString = JSON.stringify( hash_json_Contents );
		var requestData = {
				scansForSelectedLinkTypes : scansForSelectedLinkTypes,
				projectSearchId : projectSearchId,
				filterCriteria : hash_json_field_Contents_JSONString
		};
		if ( userInputMaxXString !== "" ) {
			requestData.retentionTimeInMinutesCutoff = userInputMaxXString;
		}
		//  Add scan file id or all scan file ids
		if ( scanFileId === "" || scanFileId === undefined || scanFileId === null ) {
			//  'All' selected Add all scan file ids
			if ( ! thumbnailChart && ! _scanFileIds ) {
				throw Error("_scanFileIds empty or null" );
			}
			if ( _scanFileIds !== null && _scanFileIds !== undefined ) {
				requestData.scanFileId = _scanFileIds;
			}
			requestData.scanFileAll = "Y";
		} else {
			requestData.scanFileId = [ scanFileId ];
		}
		
//		var request =
		$.ajax({
			type : "GET",
			url : _URL,
			data : requestData,
			traditional: true,  //  Force traditional serialization of the data sent
//			One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
//			So scansFor array is passed as "scansFor=<value>" which is what Jersey expects
			dataType : "json",
			success : function(data) {
				try {
					objectThis.createRetentionTimeCountChartResponse(requestData, data, params);
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			},
			failure: function(errMsg) {
				handleAJAXFailure( errMsg );
			},
			error : function(jqXHR, textStatus, errorThrown) {
				handleAJAXError(jqXHR, textStatus, errorThrown);
//				alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
//				textStatus: " + textStatus );
			}
		});
	};


	this.createRetentionTimeCountChartResponse = function(requestData, responseData, originalParams) {
		var objectThis = this;
		var scanFileName = originalParams.scanFileName;
		var userInputMaxXString = originalParams.userInputMaxX;
		var userInputMaxYString = originalParams.userInputMaxY;
		var thumbnailChart = originalParams.thumbnailChart;
		//  div for overlay
		var $scan_retention_time_qc_plot_chartDiv = originalParams.$scan_retention_time_qc_plot_chartDiv;
		//  outer div for thumbnail
		var $retention_time_outer_container_div = originalParams.$retention_time_outer_container_div;
		
		if ( $scan_retention_time_qc_plot_chartDiv ) {
			$scan_retention_time_qc_plot_chartDiv.empty();
		} else {
			$retention_time_outer_container_div.empty();
			var $chart_container_jq = this._addChartInnerTemplate( { $chart_outer_container_jq : $retention_time_outer_container_div } );
			chartDownload.addDownloadClickHandlers( { $chart_outer_container_for_download_jq :  $retention_time_outer_container_div } );
			$scan_retention_time_qc_plot_chartDiv = $chart_container_jq;
		}

		var scanFileIdList = responseData.scanFileIdList; //  Scan File Ids used for query, either passed in or retrieved from DB for project search id
		var chartBuckets = responseData.chartBuckets;
		
		var $qc_plot_scan_retention_time_link = $("#qc_plot_scan_retention_time_link");
		$qc_plot_scan_retention_time_link.show();
		
		if ( thumbnailChart ) {
			var $svg_download_outer_block_jq = $retention_time_outer_container_div.find(".svg_download_outer_block_jq");
			$svg_download_outer_block_jq.show();
		}
		
		if ( scanFileIdList.length === 0 ) {

			if ( thumbnailChart ) {
				$qc_plot_scan_retention_time_link.hide();
				var $svg_download_outer_block_jq = $retention_time_outer_container_div.find(".svg_download_outer_block_jq");
				$svg_download_outer_block_jq.hide();

				//  Add empty chart with No Scans message
				this._placeEmptyDummyChartForMessage( { 
					$chart_outer_container_jq : $scan_retention_time_qc_plot_chartDiv, 
//					linkType : selectedLinkType, 
					messageWhole:  _DUMMY_CHART_STATUS_WHOLE_TEXT_SCANS_NOT_UPLOADED
				} );				
			} else {
				$(".scan_retention_time_qc_plot_no_data_jq").show();
				$(".scan_retention_time_qc_plot_have_data_jq").hide();
			}
			return;  //  EARLY EXIT
		}

		var userInputMaxX = undefined;
		if ( userInputMaxXString !== "" ) {
//			only test for valid Max X value if not empty string
			if ( /^[+-]?((\d+(\.\d*)?)|(\.\d+))$/.test( userInputMaxXString ) ) {
//				Max X value is a valid decimal number
				userInputMaxX = parseFloat( userInputMaxXString );
				if ( isNaN( userInputMaxX ) ) {
					userInputMaxX = undefined;
				}
			}
		}
		var userInputMaxY = undefined;
		if ( userInputMaxYString !== "" ) {
//			only test for valid Max Y value if not empty string
			if ( /^[+-]?((\d+(\.\d*)?)|(\.\d+))$/.test( userInputMaxYString ) ) {
//				Max X value is a valid decimal number
				userInputMaxY = parseFloat( userInputMaxYString );
				if ( isNaN( userInputMaxY ) ) {
					userInputMaxY = undefined;
				}
			}
		}
		var maxDataX = 0;
		var maxDataY = 0;
		
		var scanFileIdList = responseData.scanFileIdList; //  Scan File Ids used for query, either passed in or retrieved from DB for project search id
		var chartBuckets = responseData.chartBuckets;
		
		if ( chartBuckets.length === 0
				|| ( chartBuckets[ 0 ].binCenter === 0 ) ) {

			if ( thumbnailChart ) {
				$qc_plot_scan_retention_time_link.hide();
				var $svg_download_outer_block_jq = $retention_time_outer_container_div.find(".svg_download_outer_block_jq");
				$svg_download_outer_block_jq.hide();

				//  Add empty chart with No Data message
				this._placeEmptyDummyChartForMessage( { 
					$chart_outer_container_jq : $scan_retention_time_qc_plot_chartDiv, 
//					linkType : selectedLinkType, 
					messagePrefix:  _DUMMY_CHART_STATUS_TEXT_PREFIX_NO_DATA,
					messageSuffix:  _DUMMY_CHART_STATUS_TEXT_SUFFIX_NO_DATA
				} );
				
			} else {
				$(".scan_retention_time_qc_plot_no_data_jq").show();
				$(".scan_retention_time_qc_plot_have_data_jq").hide();
			}
			return;  //  EARLY EXIT
		} else {
			if ( thumbnailChart ) {

			} else {
				$(".scan_retention_time_qc_plot_no_data_jq").hide();
				$(".scan_retention_time_qc_plot_have_data_jq").show();
			}
		}
		if ( ! thumbnailChart ) {
			var $scan_retention_time_qc_plot_overlay_container = $("#scan_retention_time_qc_plot_overlay_container");
			var $scan_retention_time_qc_plot_overlay_background = $("#scan_retention_time_qc_plot_overlay_background"); 
			$scan_retention_time_qc_plot_overlay_background.show();
			$scan_retention_time_qc_plot_overlay_container.show();
		}
		
		//  Full size in overlay:
		var legendAll = "All MS2 scans";
		var legendFiltered = "Filtered PSMs";
		if ( thumbnailChart ) {
			//  Thumbnail 
			legendAll = "All";
			legendFiltered = "Filtered";
		}
		
//		chart data for Google charts
		var chartData = [];
//		output columns specification
//		chartData.push( ["preMZ","count"] );
//		With Tooltip
		chartData.push( ["retention time",
			legendFiltered,{role: "tooltip",  'p': {'html': true} }, 
			{ role: 'style' } ,
			legendAll,{role: "tooltip",  'p': {'html': true} }
			, { role: 'style' } 
			] );
		for ( var index = 0; index < chartBuckets.length; index++ ) {
			var bucket = chartBuckets[ index ];
//			not needed since send userInputMaxX to the server
//			if ( userInputMaxX && bucket.binEnd > userInputMaxX ) {

////			Skip entries beyond userInputMaxX

//			continue;   //  EARLY continue
//			}
			if ( userInputMaxY && bucket.totalCount > userInputMaxY ) {
				bucket.totalCount = userInputMaxY;
			}
			if ( userInputMaxY && bucket.countForPsmsThatMeetCriteria > userInputMaxY ) {
				bucket.countForPsmsThatMeetCriteria = userInputMaxY;
			}
			if ( bucket.binEnd > maxDataX ) {
				maxDataX = bucket.binEnd;
			}
			if ( bucket.totalCount > maxDataY ) {
				maxDataY = bucket.totalCount;
			}
			//  numbers as strings locale for tooltip
			var tooltipTotalCountString = bucket.totalCount; 
			try {
				tooltipTotalCountString = tooltipTotalCountString.toLocaleString();
			} catch( e ) {
			}
			var tooltipCountForPsmsThatMeetCriteriaString = bucket.countForPsmsThatMeetCriteria; 
			try {
				tooltipCountForPsmsThatMeetCriteriaString = tooltipCountForPsmsThatMeetCriteriaString.toLocaleString();
			} catch( e ) {
			}
			var bucketStartString = bucket.binStart;
			try {
				bucketStartString = bucketStartString.toLocaleString();
			} catch( e ) {
			}
			var bucketEndString = bucket.binEnd;
			try {
				bucketEndString = bucketEndString.toLocaleString();
			} catch( e ) {
			}
//			With Tooltip
			var totalScanCountBarHeight = bucket.totalCount - bucket.countForPsmsThatMeetCriteria;
			var tooltip = "<div style='margin: 10px;'>total scan count: " + tooltipTotalCountString + 
			"<br>retention time approximately " + bucket.binStart + " to " + bucket.binEnd + "</div>";
			var countForPsmsThatMeetCriteriaTooltip = "<div style='margin: 10px;'>scan count Psms That Meet Criteria: " + tooltipCountForPsmsThatMeetCriteriaString + 
			"<br>retention time approximately " + bucket.binStart + " to " + bucket.binEnd + "</div>";
			chartData.push( [bucket.binCenter, 
				bucket.countForPsmsThatMeetCriteria, 
				countForPsmsThatMeetCriteriaTooltip, 
				_OVERALL_GLOBALS.BAR_STYLE_RED, 
				totalScanCountBarHeight, 
				tooltip 
				,_OVERALL_GLOBALS.BAR_STYLE_PINK
				] );
		}
		var $scan_retention_time_qc_plot_max_x = $("#scan_retention_time_qc_plot_max_x");
		var $scan_retention_time_qc_plot_max_y = $("#scan_retention_time_qc_plot_max_y");
		if ( ! this.globals_RentionTimeChart.currentSearchData ) {
			this.globals_RentionTimeChart.currentSearchData = {};
		}
		if ( ! this.globals_RentionTimeChart.currentSearchData.maxValuesInDB ) {
			this.globals_RentionTimeChart.currentSearchData.maxValuesInDB = {};
		}
		if ( ! this.globals_RentionTimeChart.currentSearchData.maxValuesInDB.maxX ) {
			this.globals_RentionTimeChart.currentSearchData.maxValuesInDB.maxX = maxDataX;
			this.globals_RentionTimeChart.currentSearchData.maxValuesInDB.maxY = maxDataY;
		}
		var userInputMaxXStringCurrentRead = $scan_retention_time_qc_plot_max_x.val();
		var userInputMaxYStringCurrentRead = $scan_retention_time_qc_plot_max_y.val();
		if ( userInputMaxXStringCurrentRead === "" ) {
			$scan_retention_time_qc_plot_max_x.val( this.globals_RentionTimeChart.currentSearchData.maxValuesInDB.maxX );
		}
		if ( userInputMaxYStringCurrentRead === "" ) {
			$scan_retention_time_qc_plot_max_y.val( this.globals_RentionTimeChart.currentSearchData.maxValuesInDB.maxY );
		}
		
//		control the tick marks horizontal and vertical
		var hAxisTicks = objectThis.getRetentionTimeTickMarks( maxDataX );
//		var vAxisTicks = objectThis.getRetentionTimeTickMarks( maxDataY );
		
		var chartTitle = 'PSM Count vs/ Retention Time';
		if ( ! thumbnailChart ) {
			if ( scanFileName ) {
				chartTitle += ' - ' + scanFileName;
			} else {
				try {
					if ( scanFileIdList && scanFileIdList.length > 0 ) {
						chartTitle += ' - All';
					}
				} catch( e ) {
				}
			}
		}

		//  Build Series Config and Color Arrays
		var seriesConfig = [
			{ color: _OVERALL_GLOBALS.BAR_COLOR_RED },
			{ color: _OVERALL_GLOBALS.BAR_COLOR_PINK }
		];
		
		var legendParam = undefined;
		if ( ! thumbnailChart ) {
//			legendParam = { position: 'none' };
			legendParam = { position: 'right', alignment: 'start' }; // default
		} else {
			legendParam = { position: 'right', alignment: 'start' }; // default
//			legendParam = { position: 'top', alignment: 'end' };
		}

		//  Set chart area and chart size if full size
//		var chartWidth = undefined;
//		var chartHeight = undefined;
		var chartChartArea = undefined;
		if ( ! thumbnailChart ) {
			chartChartArea = {  
					left: 80,
					right: 120, // for no wrap: right: 165,  nice wrap: right: 120,
					top: 40,
					bottom: 50 };
		}
		
		var optionsFullsize = {
				title: chartTitle, // Title above chart
				series: seriesConfig,
//				X axis label below chart
				hAxis: { title: 'Retention Time (minutes)', titleTextStyle: {color: 'black'}
					,ticks: hAxisTicks, format:'#,###'
					,maxValue : maxDataX
				},  
//				Y axis label left of chart
				vAxis: { title: 'Count', titleTextStyle: {color: 'black'}
					,baseline: 0                    // always start at zero
					,viewWindow: { min: 0 }
//				,ticks: vAxisTicks, format:'#,###'
//				,maxValue : maxDataY
				},
				legend: legendParam, // { position: 'none' }, //  position: 'none':  Don't show legend of bar colors in upper right corner
//				width : chartWidth, 
//				height : chartHeight,   // width and height of chart, otherwise controlled by enclosing div
//				bar: { groupWidth: 5 },  // set bar width large to eliminate space between bars
				bar: { groupWidth: '100%' },  // set bar width large to eliminate space between bars
//				colors: ['red','blue'],  //  Color of bars
				colors: [ _OVERALL_GLOBALS.BAR_COLOR_RED, _OVERALL_GLOBALS.BAR_COLOR_PINK ],  //  Color of bars, Proxl shades of red, total counts is second
				tooltip: {isHtml: true},
				isStacked: true
				,chartArea : chartChartArea
		};        
//		create the chart
		var data = google.visualization.arrayToDataTable( chartData );
		var chartFullsize = new google.visualization.ColumnChart( $scan_retention_time_qc_plot_chartDiv[0] );
		chartFullsize.draw(data, optionsFullsize);
		
		//  listener for when a specific element was clicked on the chart, like a bar of data
//		google.visualization.events.addListener(chartFullsize, 'select', function() {

//			var tableSelection = chartFullsize.getSelection();
//			var tableSelection0 = tableSelection[ 0 ];
//			var column = tableSelection0.column;
//			var row = tableSelection0.row;
//			var chartDataForRow = chartData[ row ];

//			var z = 0;
//		});
		
	};

///////////
	this.getRetentionTimeTickMarks = function( maxValue ) {
//		var objectThis = this;
		var maxValueRoundDown = maxValue;
		maxValueRoundDown = Math.round( maxValue / 200 ) * 200;
		if ( maxValue < 1000 ) {
			maxValueRoundDown = Math.round( maxValue / 20 ) * 20;
		}
		if ( maxValue < 100 ) {
			maxValueRoundDown = Math.round( maxValue / 2 ) * 2;
		}
		var tickMarks = [ maxValueRoundDown  * 0.25, maxValueRoundDown  * 0.5, maxValueRoundDown * 0.75 ];
//		var tickMarks = [ maxValueRoundDown  * 0.25, maxValueRoundDown  * 0.5, maxValueRoundDown * 0.75, maxValue ];
//		var tickMarks = [ maxValueRoundDown  * 0.2, maxValueRoundDown  * 0.4, maxValueRoundDown * 0.6, maxValueRoundDown * 0.8, maxValue ];
		return tickMarks;
	};

///////////
	this.resetRetentionTimeCountChartMaxXMaxY = function() {
//		var objectThis = this;
		var $scan_retention_time_qc_plot_max_x = $("#scan_retention_time_qc_plot_max_x");
		var $scan_retention_time_qc_plot_max_y = $("#scan_retention_time_qc_plot_max_y");
		var $scan_retention_time_qc_plot_max_x_initial_value = $("#scan_retention_time_qc_plot_max_x_initial_value");
		var $scan_retention_time_qc_plot_max_y_initial_value = $("#scan_retention_time_qc_plot_max_y_initial_value");
		var scan_retention_time_qc_plot_max_x_initial_value = $scan_retention_time_qc_plot_max_x_initial_value.val();
		var scan_retention_time_qc_plot_max_y_initial_value = $scan_retention_time_qc_plot_max_y_initial_value.val();
		$scan_retention_time_qc_plot_max_x.val( scan_retention_time_qc_plot_max_x_initial_value );
		$scan_retention_time_qc_plot_max_y.val( scan_retention_time_qc_plot_max_y_initial_value );
	};
	
};

/**
 * page variable 
 */

var qcPageChart_PSM_Count_Vs_RetentionTime_PSM = new QCPageChart_PSM_Count_Vs_RetentionTime_PSM();
