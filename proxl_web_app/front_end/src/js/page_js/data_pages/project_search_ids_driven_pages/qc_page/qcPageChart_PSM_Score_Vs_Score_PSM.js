/**
 * qcPageChart_PSM_Score_Vs_Score_PSM.js
 * 
 * Javascript for the viewQC.jsp page - Chart PSM Score Vs Score - in PSM Level section
 * 
 * page variable qcPageChart_PSM_Score_Vs_Score_PSM
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

import { qcChartDownloadHelp } from './qcChart_Download_Help_HTMLBlock.js';

import { downloadStringAsFile } from 'page_js/data_pages/project_search_ids_driven_pages/common/download-string-as-file.js';

import { webserviceCallStandardPost } from 'page_js/webservice_call_common/webserviceCallStandardPost.js';

/**
 * Constructor 
 */
var QCPageChart_PSM_Score_Vs_Score_PSM = function() {

	const _SELECT_ANNOTATION_CHOICE_OPTION_ENTRY_HTML_HANDLEBARS_TEMPLATE = '<option value="{{ id }}">{{ name }} ({{ searchProgramName }})</option>';

	var _CHART_LEGEND_LABEL_CROSSLINK = "crosslink";
	var _CHART_LEGEND_LABEL_LOOPLINK = "looplink";
	var _CHART_LEGEND_LABEL_UNLINKED = "unlinked";

	//	CONSTANTS - PSM Score Vs Score Chart

	var PSM_SCORE_VS_SCORE_CHART_BIN_START_OR_END_TO_FIXED_VALUE = 4;
	var PSM_SCORE_VS_SCORE_CHART_PERCENTAGE_OF_MAX_TO_FIXED_VALUE = 1;
	var PSM_SCORE_VS_SCORE_CHART_PERCENTAGE_OF_MAX_TO_FIXED_VALUE_OUTPUT_FILE = 4;
	var PSM_SCORE_VS_SCORE_CHART_COMPARISON_DIRECTION_STRING_ABOVE_ASCII = ">=";
	var PSM_SCORE_VS_SCORE_CHART_COMPARISON_DIRECTION_STRING_BELOW_ASCII = "<=";
	var PSM_SCORE_VS_SCORE_CHART_COMPARISON_DIRECTION_STRING_ABOVE = "\u2265"; // ">=" as a single character
	var PSM_SCORE_VS_SCORE_CHART_COMPARISON_DIRECTION_STRING_BELOW = "\u2264"; // "<=" as a single character
	var RELOAD_PSM_SCORE_VS_SCORE_CHART_TIMER_DELAY = 400;  // in Milliseconds
	
	//  Chart Area Used to consolidating chart entries
	
	//  As currently measured on the page:
	var THUMBNAIL_CHART_AREA_AXIS_X = 300;
	var THUMBNAIL_CHART_AREA_AXIS_Y = 200;
	
	var FULL_SIZE_CHART_AREA_AXIS_X = 860;
	var FULL_SIZE_CHART_AREA_AXIS_Y = 560;

	var FULL_SIZE_CHART_AREA_AXIS_X_use = FULL_SIZE_CHART_AREA_AXIS_X;
	var FULL_SIZE_CHART_AREA_AXIS_Y_use = FULL_SIZE_CHART_AREA_AXIS_Y;
	
//	var FULL_SIZE_CHART_AREA_AXIS_X_use = FULL_SIZE_CHART_AREA_AXIS_X * 2;
//	var FULL_SIZE_CHART_AREA_AXIS_Y_use = FULL_SIZE_CHART_AREA_AXIS_Y * 2;

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

	/**
	 * Used by PSM Score Vs Scores Chart Code
	 */
	this.globals_PSMScoreVsScoresChart = {
			currentSearchData : 
				{ 
				maxValuesInDB : {
					maxX : undefined,
					maxY : undefined
				}
			},
			prevYAxisChoice : undefined,
			chartDataFromServer_Thumbnail : undefined,
			chartDataFromServer_FullSize_Overlay : undefined
	};
	
	// <%-- Values sent to server --%>
	var _psm_score_vs_score_qc_plot_choice_value__retention_time;
	var _psm_score_vs_score_qc_plot_choice_value__charge;
	var _psm_score_vs_score_qc_plot_choice_value__pre_mz;

	// <%-- Labels put on select for user --%>
	var _psm_score_vs_score_qc_plot_choice_label__retention_time;
	var _psm_score_vs_score_qc_plot_choice_label__charge;
	var _psm_score_vs_score_qc_plot_choice_label__pre_mz;

	
	var _helpTooltipHTML = undefined;
	
	
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
			
			//  Get Help tooltip HTML
			var $psm_level_block_help_tooltip_psm_score_vs_score = $("#psm_level_block_help_tooltip_psm_score_vs_score");
			if ( $psm_level_block_help_tooltip_psm_score_vs_score.length === 0 ) {
				throw Error( "No element found with id 'psm_level_block_help_tooltip_psm_score_vs_score' " );
			}
			_helpTooltipHTML = $psm_level_block_help_tooltip_psm_score_vs_score.html();

			this.addClickAndOnChangeHandlers();

			this.getSelectValuesFromPage();

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

		$("#qc_plot_psm_score_vs_score_link").click(function(eventObject) {
			try {
				var clickThis = this;
				objectThis.psmScoreVsScoreQCPlotClickHandler( clickThis, eventObject );
				eventObject.preventDefault();
				eventObject.stopPropagation();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});	 	
		$(".psm_score_vs_score_qc_plot_overlay_close_parts_jq").click(function(eventObject) {
			try {
				var clickThis = this;
				if ( objectThis.reloadPSMScoreVsScoreChartTimerId ) {
					clearTimeout( objectThis.reloadPSMScoreVsScoreChartTimerId );
					objectThis.reloadPSMScoreVsScoreChartTimerId = null;
				}
				objectThis.closePSMScoreVsScoreQCPlotOverlay( clickThis, eventObject );
				eventObject.preventDefault();
				eventObject.stopPropagation();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		$("#psm_score_vs_score_qc_plot_score_type_id_1").change(function(eventObject) {
			try {
				objectThis.psmScoreVsScoreQCPlot_scoreTypeChanged( );
				return false;
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		$("#psm_score_vs_score_qc_plot_score_type_id_2").change(function(eventObject) {
			try {
				objectThis.psmScoreVsScoreQCPlot_scoreTypeChanged( );
				eventObject.preventDefault();
				eventObject.stopPropagation();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		$(".psm_score_vs_score_qc_plot_on_change_jq").change(function(eventObject) {
			try {
				objectThis.psmScoreVsScoreQCPlot_createChartFromPageParams( );
				eventObject.preventDefault();
				eventObject.stopPropagation();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		$(".psm_score_vs_score_qc_plot_on_change_jq").keyup(function(eventObject) {
			try {
				if ( objectThis.reloadPSMScoreVsScoreChartTimerId ) {
					clearTimeout( objectThis.reloadPSMScoreVsScoreChartTimerId );
					objectThis.reloadPSMScoreVsScoreChartTimerId = null;
				}
				objectThis.reloadPSMScoreVsScoreChartTimerId = setTimeout( function() {
					try {
						objectThis.psmScoreVsScoreQCPlot_createChartFromPageParams( );
					} catch( e ) {
						reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
						throw e;
					}
					}, RELOAD_PSM_SCORE_VS_SCORE_CHART_TIMER_DELAY );
				eventObject.preventDefault();
				eventObject.stopPropagation();
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			});
		$("#psm_score_vs_score_qc_plot_max_reset_button").click(function(eventObject) {
			try {
				objectThis.psmScoreVsScoreQCPlot_resetMaxXMaxY();
				objectThis.psmScoreVsScoreQCPlot_createChartFromPageParams( );
				eventObject.preventDefault();
				eventObject.stopPropagation();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
			});

		// For PSM Score Vs Score, add Download Click handlers for overlay
		var $psm_score_vs_score_qc_plot_chartDiv_Container = $("#psm_score_vs_score_qc_plot_chartDiv_Container");

		//  Download Data Setup
		var downloadDataCallback = function( params ) {
//			var clickedThis = params.clickedThis;

			//  Download the data for params
			objectThis.psmScoreVsScoreQCPlot_downloadData( { thumbnailChart : false } );
		};
		
		qcChartDownloadHelp.add_DownloadClickHandlers_HelpTooltip( { 
			$chart_outer_container_for_download_jq :  $psm_score_vs_score_qc_plot_chartDiv_Container, 
			downloadDataCallback : downloadDataCallback,
			helpTooltipHTML : _helpTooltipHTML 
		} );
		
	};


	/**
	 * Get values to use in <select> of value to plot from page
	 */
	this.getSelectValuesFromPage = function() {
		
		// <%-- Values sent to server --%>
		var $psm_score_vs_score_qc_plot_choice_value__retention_time = $("#psm_score_vs_score_qc_plot_choice_value__retention_time");
		var $psm_score_vs_score_qc_plot_choice_value__charge = $("#psm_score_vs_score_qc_plot_choice_value__charge");
		var $psm_score_vs_score_qc_plot_choice_value__pre_mz = $("#psm_score_vs_score_qc_plot_choice_value__pre_mz");

		// <%-- Labels put on select for user --%>
		var $psm_score_vs_score_qc_plot_choice_label__retention_time = $("#psm_score_vs_score_qc_plot_choice_label__retention_time");
		var $psm_score_vs_score_qc_plot_choice_label__charge = $("#psm_score_vs_score_qc_plot_choice_label__charge");
		var $psm_score_vs_score_qc_plot_choice_label__pre_mz = $("#psm_score_vs_score_qc_plot_choice_label__pre_mz");

		
		
		// <%-- Values sent to server --%>
		if ( $psm_score_vs_score_qc_plot_choice_value__retention_time.length === 0 ) {
			throw Error( "Failed to find HTML element with id 'psm_score_vs_score_qc_plot_choice_value__retention_time' " );
		}
		if ( $psm_score_vs_score_qc_plot_choice_value__charge.length === 0 ) {
			throw Error( "Failed to find HTML element with id 'psm_score_vs_score_qc_plot_choice_value__charge' " );
		}
		if ( $psm_score_vs_score_qc_plot_choice_value__pre_mz.length === 0 ) {
			throw Error( "Failed to find HTML element with id 'psm_score_vs_score_qc_plot_choice_value__pre_mz' " );
		}

		// <%-- Labels put on select for user --%>
		if ( $psm_score_vs_score_qc_plot_choice_label__retention_time.length === 0 ) {
			throw Error( "Failed to find HTML element with id 'psm_score_vs_score_qc_plot_choice_label__retention_time' " );
		}
		if ( $psm_score_vs_score_qc_plot_choice_label__charge.length === 0 ) {
			throw Error( "Failed to find HTML element with id 'psm_score_vs_score_qc_plot_choice_label__charge' " );
		}
		if ( $psm_score_vs_score_qc_plot_choice_label__pre_mz.length === 0 ) {
			throw Error( "Failed to find HTML element with id 'psm_score_vs_score_qc_plot_choice_label__pre_mz' " );
		}

		// <%-- Values sent to server --%>
		_psm_score_vs_score_qc_plot_choice_value__retention_time = $psm_score_vs_score_qc_plot_choice_value__retention_time.text();
		_psm_score_vs_score_qc_plot_choice_value__charge = $psm_score_vs_score_qc_plot_choice_value__charge.text();
		_psm_score_vs_score_qc_plot_choice_value__pre_mz = $psm_score_vs_score_qc_plot_choice_value__pre_mz.text();

		// <%-- Labels put on select for user --%>
		_psm_score_vs_score_qc_plot_choice_label__retention_time = $psm_score_vs_score_qc_plot_choice_label__retention_time.text();
		_psm_score_vs_score_qc_plot_choice_label__charge = $psm_score_vs_score_qc_plot_choice_label__charge.text();
		_psm_score_vs_score_qc_plot_choice_label__pre_mz = $psm_score_vs_score_qc_plot_choice_label__pre_mz.text();
		
	};

	///////////////////////////////////////////

	///////////////////////////////////////////

	///////   PSM Score Vs Score Chart


	/**
	 * Clear data  
	 */
	this.clearChart = function() {

//		_chart_isLoaded = _IS_LOADED_NO;

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

//		if ( _chart_isLoaded === _IS_LOADED_NO ) {
			this.createPSMScoreVsScoreChartThumbnail();
//		}
	};

//	var _activeAjax = null;

	/**
	 * 
	 */
	this.createPSMScoreVsScoreChartThumbnail = function() {
		
		// TODO  Hard code taking first project search id
		var projectSearchId = _project_search_ids[ 0 ];	

		//  TODO  Need this for thumbnail
		
		var $psm_score_vs_score_outer_container_div = $("#psm_score_vs_score_outer_container_div");
		$psm_score_vs_score_outer_container_div.empty();
		
		this._placeEmptyDummyChartForMessage( { 
			$chart_outer_container_jq : $psm_score_vs_score_outer_container_div, 
			//				linkType : selectedLinkType, 
			messagePrefix:  _DUMMY_CHART_STATUS_TEXT_PREFIX_LOADING,
			messageSuffix:  _DUMMY_CHART_STATUS_TEXT_SUFFIX_LOADING
		} );
		
		this.psmScoreVsScoreQCPlot_createChartFromPageParams( { thumbnailChart : true } );
	};

	/**
	 * 
	 */
	this.psmScoreVsScoreQCPlotClickHandler = function(clickThis, eventObject) {
		var objectThis = this;
		objectThis.openPSMScoreVsScoreQCPlotOverlay(clickThis, eventObject);
		return;
	};

	/**
	 * 
	 */
	this.closePSMScoreVsScoreQCPlotOverlay = function(clickThis, eventObject) {
		$(".psm_score_vs_score_qc_plot_overlay_show_hide_parts_jq").hide();
		var $psm_score_vs_score_qc_plot_chartDiv = $("#psm_score_vs_score_qc_plot_chartDiv");
		$psm_score_vs_score_qc_plot_chartDiv.empty();
	};

	/**
	 * 
	 */
	this.openPSMScoreVsScoreQCPlotOverlay = function(clickThis, eventObject) {
		var objectThis = this;

		// TODO  Hard code taking first project search id
		var projectSearchId = _project_search_ids[ 0 ];	
		
//		Position dialog over clicked link
//		get position of div containing the dialog that is inline in the page
		var $psm_score_vs_score_qc_plot_overlay_containing_outermost_div_inline_div = $("#psm_score_vs_score_qc_plot_overlay_containing_outermost_div_inline_div");
		var offset__containing_outermost_div_inline_div = $psm_score_vs_score_qc_plot_overlay_containing_outermost_div_inline_div.offset();
		var offsetTop__containing_outermost_div_inline_div = offset__containing_outermost_div_inline_div.top;
		var $psm_score_vs_score_qc_plot_overlay_container = $("#psm_score_vs_score_qc_plot_overlay_container");
		var scrollTopWindow = $(window).scrollTop();
		var positionAdjust = scrollTopWindow - offsetTop__containing_outermost_div_inline_div + 10;
		$psm_score_vs_score_qc_plot_overlay_container.css( "top", positionAdjust );
		
		var $psm_score_vs_score_qc_plot_overlay_container = $("#psm_score_vs_score_qc_plot_overlay_container");
		var $psm_score_vs_score_qc_plot_overlay_background = $("#psm_score_vs_score_qc_plot_overlay_background"); 
		$psm_score_vs_score_qc_plot_overlay_background.show();
		$psm_score_vs_score_qc_plot_overlay_container.show();

		this.globals_PSMScoreVsScoresChart.currentSearchData = undefined;   ///  Reset "currentSearchData" for new search
		
		var hash_json_Contents = _get_hash_json_Contents();
		
		//		initialize all "Link Type" checkboxes to checked or not based on main page ( in hash JSON object hash_json_Contents )
		//  First set all to unchecked
		var $psm_score_vs_score_qc_plot_link_type_include_jq = $(".psm_score_vs_score_qc_plot_link_type_include_jq");
		$psm_score_vs_score_qc_plot_link_type_include_jq.each( function(   ) {
			$( this ).prop('checked',false);
		} );
		//  Set to checked for entries in hash_json_Contents.linkTypes
		if ( hash_json_Contents.linkTypes ) {
			hash_json_Contents.linkTypes.forEach( function ( currentArrayValue, index, array ) {
				var entry = currentArrayValue;
				var htmlId = "psm_score_vs_score_qc_plot_link_type_checkbox_" + entry;
				var $checkbox = $( "#" + htmlId );
				$checkbox.prop('checked',true);
			}, this /* passed to function as this */ );
		}

		var callback_GetScanFilesForProjectSearchId = function( params ) {
			objectThis.getScanFilesForProjectSearchId_ProcessResponse( params );
		};
		
		_getScanFilesForProjectSearchId( { 
			htmlElementScanFileTR : "#psm_score_vs_score_qc_plot_overlay_scan_file_selector_row",
			addAllOption : true,
			callback: callback_GetScanFilesForProjectSearchId } );
		
	};


	/**
	 * 
	 */
	this.psmScoreVsScoreQCPlot_resetMaxXMaxY = function() {
//		var objectThis = this;
		var $psm_score_vs_score_qc_plot_max_x = $("#psm_score_vs_score_qc_plot_max_x");
		var $psm_score_vs_score_qc_plot_max_y = $("#psm_score_vs_score_qc_plot_max_y");
		$psm_score_vs_score_qc_plot_max_x.val( "" );
		$psm_score_vs_score_qc_plot_max_y.val( "" );
	};

	/**
	 * 
	 */
	this.getScanFilesForProjectSearchId_ProcessResponse = function( params ) {
//		var objectThis = this;
		var scanFiles = params.scanFiles;
		var selectorUpdated = params.selectorUpdated;
		if ( ! selectorUpdated ) {
			
		}

		var $psm_score_vs_score_qc_plot_overlay_scan_file_selector_row = $("#psm_score_vs_score_qc_plot_overlay_scan_file_selector_row");
		if ( ! scanFiles ) {
			//  No scan files
			$psm_score_vs_score_qc_plot_overlay_scan_file_selector_row.hide();
		} else {
			$psm_score_vs_score_qc_plot_overlay_scan_file_selector_row.show();
		}
		
		this.psmScoreVsScoreQCPlot_getPSMFilterableAnnTypesForProjectSearchId( );
	};


	/**
	 * 
	 */
	this.psmScoreVsScoreQCPlot_getPSMFilterableAnnTypesForProjectSearchId = function( params ) {
		var objectThis = this;

		if ( _project_search_ids.length !== 1 ) {
			throw Error("Unsupported: _project_search_ids.length !== 1.  length: " + _project_search_ids.length );
		}

		// TODO  Hard code taking first project search id
		var projectSearchId = _project_search_ids[ 0 ];	
		
		const ajaxRequestData = { projectSearchId : projectSearchId };

		const url = "services/annotationTypes/getAnnotationTypesPsmFilterableForProjectSearchId";

		const webserviceCallStandardPostResult = webserviceCallStandardPost({ dataToSend : ajaxRequestData, url }); //  External Function

		const promise_webserviceCallStandardPost = webserviceCallStandardPostResult.promise; 
		// _activeAjax = webserviceCallStandardPostResult.api;

		promise_webserviceCallStandardPost.catch( ( ) => { } );

		promise_webserviceCallStandardPost.then( ({ responseData }) => {
			try {
				objectThis.psmScoreVsScoreQCPlot_getPSMFilterableAnnTypesForProjectSearchIdResponse( responseData );
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
	};

	/**
	 * 
	 */
	this.psmScoreVsScoreQCPlot_getPSMFilterableAnnTypesForProjectSearchIdResponse = function( responseData ) {
		var annTypesSearchProgramsPerSearch = responseData.annotationTypeList;
		if (  annTypesSearchProgramsPerSearch.length === 0 ) {
			throw "annTypesSearchProgramsPerSearch.length === 0";
		}
		if ( ! this.globals_PSMScoreVsScoresChart.currentSearchData ) {
			this.globals_PSMScoreVsScoresChart.currentSearchData = {};
		}
		var annTypes = [];
		var annTypesById = {};
		for ( var annTypesSearchProgramsPerSearchIndex = 0; annTypesSearchProgramsPerSearchIndex < annTypesSearchProgramsPerSearch.length; annTypesSearchProgramsPerSearchIndex++ ) {
			var annTypeSearchProgramPerSearch = annTypesSearchProgramsPerSearch [ annTypesSearchProgramsPerSearchIndex ];
			var annType = annTypeSearchProgramPerSearch.annotationTypeDTO;
			var searchProgramPerSearch = annTypeSearchProgramPerSearch.searchProgramsPerSearchDTO;
			annType.searchProgramName = searchProgramPerSearch.displayName;
			annTypes.push( annType );
			var annTypeId = annType.id.toString();  
			annTypesById[ annTypeId ] = annType;
		}
		this.globals_PSMScoreVsScoresChart.currentSearchData.annotationTypeDataArray = annTypes;
		this.globals_PSMScoreVsScoresChart.currentSearchData.annotationTypeDataById = annTypesById;
		var annTypes = this.globals_PSMScoreVsScoresChart.currentSearchData.annotationTypeDataArray;
		var $psm_score_vs_score_qc_plot_score_type_id_1 = $("#psm_score_vs_score_qc_plot_score_type_id_1");
		$psm_score_vs_score_qc_plot_score_type_id_1.empty();
		var $psm_score_vs_score_qc_plot_score_type_id_2 = $("#psm_score_vs_score_qc_plot_score_type_id_2");
		$psm_score_vs_score_qc_plot_score_type_id_2.empty();

		var optionEntryTemplateCompiled = Handlebars.compile( _SELECT_ANNOTATION_CHOICE_OPTION_ENTRY_HTML_HANDLEBARS_TEMPLATE );

		var optionsHTMLarray = [];
		for ( var annTypesIndex = 0; annTypesIndex < annTypes.length; annTypesIndex++ ) {
			var annType = annTypes[ annTypesIndex ];
			var html = optionEntryTemplateCompiled( annType );
			optionsHTMLarray.push( html );
		}
		//  Add options for RT, Charge, preMZ
		if ( _anySearchesHaveScanDataYes ) { // Only when have scans
			var html = "<option value='" + _psm_score_vs_score_qc_plot_choice_value__retention_time + "'>" + 
			_psm_score_vs_score_qc_plot_choice_label__retention_time +
			"</option>";
			optionsHTMLarray.push( html );
		}
		var html = "<option value='" + _psm_score_vs_score_qc_plot_choice_value__charge + "'>" + 
		_psm_score_vs_score_qc_plot_choice_label__charge +
		"</option>";
		optionsHTMLarray.push( html );
		if ( _anySearchesHaveScanDataYes ) { // Only when have scans
			var html = "<option value='" + _psm_score_vs_score_qc_plot_choice_value__pre_mz + "'>" + 
			_psm_score_vs_score_qc_plot_choice_label__pre_mz +
			"</option>";
			optionsHTMLarray.push( html );
		}
		
		var optionsHTML = optionsHTMLarray.join("");
		$psm_score_vs_score_qc_plot_score_type_id_1.append( optionsHTML );
		$psm_score_vs_score_qc_plot_score_type_id_2.append( optionsHTML );
		

		//  For ann_type_id_1, set specific annotation type record as "selected"
		//  If any annotation type record has sort id, use annotation type record with smallest sort id
		//  Otherwise, leave at first annotation type record as they are sorted alphabetically
		var annTypeIdToUse = undefined;
		var annTypeIdToUse_SortOrder = undefined;
		for ( var annTypesIndex = 0; annTypesIndex < annTypes.length; annTypesIndex++ ) {
			var annType = annTypes [ annTypesIndex ];
			if ( annType.annotationTypeFilterableDTO && 
					annType.annotationTypeFilterableDTO.sortOrder !== undefined &&
					annType.annotationTypeFilterableDTO.sortOrder !== null ) {
				if ( annTypeIdToUse === undefined ) {
					annTypeIdToUse = annType.id
					annTypeIdToUse_SortOrder = annType.annotationTypeFilterableDTO.sortOrder;
				} else {
					if ( annType.annotationTypeFilterableDTO.sortOrder < annTypeIdToUse_SortOrder ) {
						annTypeIdToUse = annType.id;
						annTypeIdToUse_SortOrder = annType.annotationTypeFilterableDTO.sortOrder
					}
				}
			}
		}
		if ( annTypeIdToUse !== undefined ) {
			$psm_score_vs_score_qc_plot_score_type_id_1.val( annTypeIdToUse );
		}

		//  For ann_type_id_2, leave at first annotation type record as they are sorted alphabetically ignore case
		
		this.psmScoreVsScoreQCPlot_createChartFromPageParams();
	};



	/**
	 * 
	 */
	this.psmScoreVsScoreQCPlot_scoreTypeChanged = function( ) {
//		var objectThis = this;
		var annTypes = this.globals_PSMScoreVsScoresChart.currentSearchData.annotationTypeDataById;
//		var $psm_score_vs_score_qc_plot_score_type_id_1 = $("#psm_score_vs_score_qc_plot_score_type_id_1");
//		var selectedAnnTypeId = $psm_score_vs_score_qc_plot_score_type_id_1.val( );
//		var annTypeForSelectId = annTypes[ selectedAnnTypeId ];
//		if ( annTypeForSelectId === undefined || annTypeForSelectId === null ) {
//			throw "annType not found for id: " + selectedAnnTypeId;
//		}
		var $psm_score_vs_score_qc_plot_max_x = $("#psm_score_vs_score_qc_plot_max_x");
		var $psm_score_vs_score_qc_plot_max_y = $("#psm_score_vs_score_qc_plot_max_y");
		$psm_score_vs_score_qc_plot_max_x.val("");
		$psm_score_vs_score_qc_plot_max_y.val("");
		this.psmScoreVsScoreQCPlot_createChartFromPageParams( );
	};

	/**
	 * 
	 */
	this.psmScoreVsScoreQCPlot_createChartFromPageParams = function( params ) {
		var objectThis = this;
		var thumbnailChart = undefined;
		if ( params ) {
			thumbnailChart = params.thumbnailChart;
		}

		//  Hack to remove tooltip from Thumbnail chart
		var $google_visualization_tooltip = $(".google-visualization-tooltip");
		$google_visualization_tooltip.remove();  //  remove element and children

		// TODO  Hard code taking first project search id
		var projectSearchId = _project_search_ids[ 0 ];
		
		var hash_json_Contents = _get_hash_json_Contents();

		//  Set for either thumbnail or overlay
		var selectedAnnotationTypeText = undefined;
		var selectedLinkTypes = undefined;

		if ( thumbnailChart ) {

			selectedLinkTypes = hash_json_Contents.linkTypes;

			//  TODO  Properly handle no link types selected on main page

		} else {

			if ( objectThis.reloadPSMScoreVsScoreChartTimerId ) {
				clearTimeout( objectThis.reloadPSMScoreVsScoreChartTimerId );
				objectThis.reloadPSMScoreVsScoreChartTimerId = null;
			}
			if ( ! this.globals_PSMScoreVsScoresChart.currentSearchData ) {
				this.globals_PSMScoreVsScoresChart.currentSearchData = {};
			}
			var $psm_score_vs_score_qc_plot_max_x = $("#psm_score_vs_score_qc_plot_max_x");
			var $psm_score_vs_score_qc_plot_max_y = $("#psm_score_vs_score_qc_plot_max_y");
			if ( ! this.globals_PSMScoreVsScoresChart.currentSearchData ) {
				this.globals_PSMScoreVsScoresChart.currentSearchData = {};
			}

			//			if Y Axis choice has changed, clear Y Axis Max input field
			if ( ( $("#psm_score_vs_score_qc_plot_y_axis_as_percentage").prop("checked")
					&& this.globals_PSMScoreVsScoresChart.prevYAxisChoice !== this.Y_AXIS_CHOICE_PERCENTAGE )
					|| ( $("#psm_score_vs_score_qc_plot_y_axis_as_raw_counts").prop("checked")
							&& this.globals_PSMScoreVsScoresChart.prevYAxisChoice !== this.Y_AXIS_CHOICE_RAW_COUNTS ) ) {
				$psm_score_vs_score_qc_plot_max_y.val( "" );
			}
			//	Set this.globals_PSMScoreVsScoresChart.prevYAxisChoice per current selected Y Axis choice
			if ( $("#psm_score_vs_score_qc_plot_y_axis_as_percentage").prop("checked") ) {
				this.globals_PSMScoreVsScoresChart.prevYAxisChoice = this.Y_AXIS_CHOICE_PERCENTAGE;
			}
			if ( $("#psm_score_vs_score_qc_plot_y_axis_as_raw_counts").prop("checked") ) {
				this.globals_PSMScoreVsScoresChart.prevYAxisChoice = this.Y_AXIS_CHOICE_RAW_COUNTS;
			}

			var userInputMaxX = $psm_score_vs_score_qc_plot_max_x.val();
			var userInputMaxY = $psm_score_vs_score_qc_plot_max_y.val();
			if ( userInputMaxX !== "" ) {
				// only test for valid Max X value if not empty string
				if ( !  /^[+-]?((\d+(\.\d*)?)|(\.\d+))$/.test( userInputMaxX ) ) {
					// Max X value is not a valid decimal number
					$(".psm_score_vs_score_qc_plot_param_not_a_number_jq").show();
					$(".psm_score_vs_score_qc_plot_no_data_jq").hide();
					$(".psm_score_vs_score_qc_plot_have_data_jq").hide();
					$psm_score_vs_score_qc_plot_max_x.focus();
					return;  //  EARLY EXIT
				}
//				if ( userInputMaxXNum < 0 ) {
//					$psm_score_vs_score_qc_plot_max_x.val( "0" );
//					userInputMaxX = "0";
//				}
			}	
			if ( userInputMaxY !== "" ) {
				// only test for valid Max Y value if not empty string
				if ( !  /^[+-]?((\d+(\.\d*)?)|(\.\d+))$/.test( userInputMaxY ) ) {
					// Max Y value is not a valid decimal number
					$(".psm_score_vs_score_qc_plot_param_not_a_number_jq").show();
					$(".psm_score_vs_score_qc_plot_no_data_jq").hide();
					$(".psm_score_vs_score_qc_plot_have_data_jq").hide();
					$psm_score_vs_score_qc_plot_max_y.focus();
					return;  //  EARLY EXIT
				}
				if ( $("#psm_score_vs_score_qc_plot_y_axis_as_percentage").prop("checked") ) {
					var userInputMaxYNum = parseFloat( userInputMaxY ); 
					if ( userInputMaxYNum < 0 ) {
						$psm_score_vs_score_qc_plot_max_y.val( "0" );
						userInputMaxY = "0";
					}
					if ( userInputMaxYNum > 100 ) {
						$psm_score_vs_score_qc_plot_max_y.val( "100" );
						userInputMaxY = "100";
					}
				}
			}	
			var $psm_score_vs_score_qc_plot_score_type_id_1 = $("#psm_score_vs_score_qc_plot_score_type_id_1");
			var annotationTypeId_1 = $psm_score_vs_score_qc_plot_score_type_id_1.val();
			var selectedAnnotationType_1_Text = $("#psm_score_vs_score_qc_plot_score_type_id_1 option:selected").text();
			var $psm_score_vs_score_qc_plot_score_type_id_2 = $("#psm_score_vs_score_qc_plot_score_type_id_2");
			var annotationTypeId_2 = $psm_score_vs_score_qc_plot_score_type_id_2.val();
			var selectedAnnotationType_2_Text = $("#psm_score_vs_score_qc_plot_score_type_id_2 option:selected").text();

			var selectedLinkTypes = this.psmScoreVsScoreQCPlot_getLinkTypesChecked();
			if ( selectedLinkTypes.length === 0 ) {
				$(".psm_score_vs_score_qc_plot_no_data_jq").show();
				$(".psm_score_vs_score_qc_plot_param_not_a_number_jq").hide();
				$(".psm_score_vs_score_qc_plot_have_data_jq").hide();
				return;  //  EARLY EXIT		
			}
		}

		var $psm_score_vs_score_qc_plot_scan_file_id = $("#psm_score_vs_score_qc_plot_scan_file_id");
		var scanFileId = $psm_score_vs_score_qc_plot_scan_file_id.val();
		var scanFileName = $( "#psm_score_vs_score_qc_plot_scan_file_id option:selected" ).text();

		var createChartParams =  {  
				projectSearchId : projectSearchId,
				scanFileId : scanFileId,
				scanFileName : scanFileName,
				annotationTypeId_1 : annotationTypeId_1,
				selectedAnnotationType_1_Text : selectedAnnotationType_1_Text,
				annotationTypeId_2 : annotationTypeId_2,
				selectedAnnotationType_2_Text : selectedAnnotationType_2_Text,
				selectedLinkTypes : selectedLinkTypes,
				userInputMaxX : userInputMaxX,
				userInputMaxY : userInputMaxY,
				thumbnailChart : thumbnailChart };

		objectThis.psmScoreVsScoreQCPlot_createChart( createChartParams );
	};

	/**
	 * 
	 */
	this.psmScoreVsScoreQCPlot_getLinkTypesChecked = function(  ) {
		var selectedLinkTypes = [];
		var $psm_score_vs_score_qc_plot_link_type_include_jq = $(".psm_score_vs_score_qc_plot_link_type_include_jq");
		$psm_score_vs_score_qc_plot_link_type_include_jq.each( function(   ) {
			var $thisCheckbox = $( this );
			if ( $thisCheckbox.prop('checked') ) {
				var checkboxValue = $thisCheckbox.attr("value");
				selectedLinkTypes.push( checkboxValue );
			}
		} );
		return selectedLinkTypes;
	};

	/**
	 * 
	 */
	this.psmScoreVsScoreQCPlot_createChart = function( params ) {
		var objectThis = this;
		var projectSearchId = params.projectSearchId;
		var scanFileId = params.scanFileId;
		var scanFileName = params.scanFileName;
		var selectedLinkTypes = params.selectedLinkTypes;
		var annotationTypeId_1 = params.annotationTypeId_1;
		var annotationTypeId_2 = params.annotationTypeId_2;
		var userInputMaxXString = params.userInputMaxX;
		var userInputMaxYString = params.userInputMaxY;
		var thumbnailChart = params.thumbnailChart;

		var $psm_score_vs_score_qc_plot_chartDiv = $("#psm_score_vs_score_qc_plot_chartDiv");
		$psm_score_vs_score_qc_plot_chartDiv.empty();
		var requestData = {
				selectedLinkTypes : selectedLinkTypes,
				projectSearchId : projectSearchId,
				scoreType_1 : annotationTypeId_1,
				scoreType_2 : annotationTypeId_2
		};
		if ( userInputMaxXString !== "" ) {
			var psmScoreCutoff_1 = userInputMaxXString;
			requestData.psmScoreCutoff_1 = psmScoreCutoff_1;
		}
		if ( userInputMaxYString !== "" ) {
			var psmScoreCutoff_2 = userInputMaxYString;
			requestData.psmScoreCutoff_2 = psmScoreCutoff_2;
		}
		if ( scanFileId === "" || scanFileId === undefined || scanFileId === null ) {
		} else {
			//  Add scan file id
			requestData.scanFileId = scanFileId;
		}
		
		const url = "services/qcplot/getPsmScoreVsScore";

		const webserviceCallStandardPostResult = webserviceCallStandardPost({ dataToSend : requestData, url }); //  External Function

		const promise_webserviceCallStandardPost = webserviceCallStandardPostResult.promise; 

		promise_webserviceCallStandardPost.catch( ( ) => { } );

		promise_webserviceCallStandardPost.then( ({ responseData }) => {
			try {
				objectThis.psmScoreVsScoreQCPlot_createChartResponse(requestData, responseData, params);
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});

	};

	/**
	 * 
	 */
	this.psmScoreVsScoreQCPlot_createChartResponse = function(requestData, responseData, originalParams) {
		var objectThis = this;

		var chartDataParam = responseData.results;
		
		var scanFileId = originalParams.scanFileId;
		var scanFileName = originalParams.scanFileName;

		var selectedAnnotationType_1_Text = originalParams.selectedAnnotationType_1_Text;
		var selectedAnnotationType_2_Text = originalParams.selectedAnnotationType_2_Text;
		var userInputMaxXString = originalParams.userInputMaxX;
		var userInputMaxYString = originalParams.userInputMaxY;
		var thumbnailChart = originalParams.thumbnailChart;
		

		if ( thumbnailChart ) {
			this.globals_PSMScoreVsScoresChart.chartDataFromServer_Thumbnail = chartDataParam;

			selectedAnnotationType_1_Text = chartDataParam.annotationTypeName_1 + " (" + chartDataParam.searchProgramName_1 + ")";
			selectedAnnotationType_2_Text = chartDataParam.annotationTypeName_2 + " (" + chartDataParam.searchProgramName_2 + ")";
				
		} else {
			this.globals_PSMScoreVsScoresChart.chartDataFromServer_FullSize_Overlay = chartDataParam;
		}
		
		var userInputMaxX = undefined;
		if ( userInputMaxXString !== "" ) {
			userInputMaxX = parseFloat( userInputMaxXString );
			if ( isNaN( userInputMaxX ) ) {
				userInputMaxX = undefined;
			}
		}
		var userInputMaxY = undefined;
		if ( userInputMaxYString !== "" ) {
			userInputMaxY = parseFloat( userInputMaxYString );
			if ( isNaN( userInputMaxY ) ) {
				userInputMaxY = undefined;
			}
		}
		var $chartContainerDiv = undefined;
		if ( thumbnailChart ) {
			var $psm_score_vs_score_outer_container_div = $("#psm_score_vs_score_outer_container_div");
			$psm_score_vs_score_outer_container_div.empty();
			var $chart_container_jq = this._addChartInnerTemplate( { $chart_outer_container_jq : $psm_score_vs_score_outer_container_div } );

			//  Download Data Setup
			var downloadDataCallback = function( params ) {
//				var clickedThis = params.clickedThis;

				//  Download the data for params
				objectThis.psmScoreVsScoreQCPlot_downloadData( { 
					thumbnailChart : thumbnailChart, 
					selectedAnnotationType_1_Text : selectedAnnotationType_1_Text,
					selectedAnnotationType_2_Text : selectedAnnotationType_2_Text 
				} );
			};
			
			qcChartDownloadHelp.add_DownloadClickHandlers_HelpTooltip( { 
				$chart_outer_container_for_download_jq :  $psm_score_vs_score_outer_container_div, 
				downloadDataCallback : downloadDataCallback,
				helpTooltipHTML : _helpTooltipHTML 
			} );
			
			// Make so clicking on the thumbnail chart opens the overlay
			$chart_container_jq.click(function(eventObject) {
				try {
					var clickThis = this;
					objectThis.psmScoreVsScoreQCPlotClickHandler( clickThis, eventObject );
					eventObject.preventDefault();
					eventObject.stopPropagation();
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			});	 
			
			//  Remove download
//			var $svg_download_outer_block_jq = $psm_score_vs_score_outer_container_div.find(".svg_download_outer_block_jq");
//			$svg_download_outer_block_jq.remove();
			
			$chartContainerDiv = $chart_container_jq;
		} else {
			$chartContainerDiv = $("#psm_score_vs_score_qc_plot_chartDiv");
			$chartContainerDiv.empty();
		}
		
		var crosslinkChartData = chartDataParam.crosslinkChartData;
		var looplinkChartData = chartDataParam.looplinkChartData;
		var unlinkedChartData = chartDataParam.unlinkedChartData;

		var _COLOR_CROSSLINK = _OVERALL_GLOBALS.BAR_COLOR_CROSSLINK;
		var _COLOR_LOOPLINK = _OVERALL_GLOBALS.BAR_COLOR_LOOPLINK;
		var _COLOR_UNLINKED = _OVERALL_GLOBALS.BAR_COLOR_UNLINKED;
		
//		var haveCrosslinkData = false;
//		var haveLooplinkData = false;
//		var haveUnlinkedData = false;

		
		var chartDataPerLinkTypeAsArray = []; //  In the order they will be added to the chart, unlinked, looplink, crosslink

		if ( unlinkedChartData && unlinkedChartData.length > 0 ) {
			var chartDataPerLinkTypeEntry = { chartData : unlinkedChartData, chartLegendLabel : _CHART_LEGEND_LABEL_UNLINKED, color : _COLOR_UNLINKED };
			chartDataPerLinkTypeAsArray.push( chartDataPerLinkTypeEntry );
		}
		if ( looplinkChartData && looplinkChartData.length > 0 ) {
			var chartDataPerLinkTypeEntry = { chartData : looplinkChartData, chartLegendLabel : _CHART_LEGEND_LABEL_LOOPLINK, color : _COLOR_LOOPLINK };
			chartDataPerLinkTypeAsArray.push( chartDataPerLinkTypeEntry );
		}
		if ( crosslinkChartData && crosslinkChartData.length > 0 ) {
			var chartDataPerLinkTypeEntry = { chartData : crosslinkChartData, chartLegendLabel : _CHART_LEGEND_LABEL_CROSSLINK, color : _COLOR_CROSSLINK };
			chartDataPerLinkTypeAsArray.push( chartDataPerLinkTypeEntry );
		}
		

		if ( thumbnailChart ) {
			
		} else {

			var $psm_score_vs_score_qc_plot_overlay_container = $("#psm_score_vs_score_qc_plot_overlay_container");
			var $psm_score_vs_score_qc_plot_overlay_background = $("#psm_score_vs_score_qc_plot_overlay_background"); 
			$psm_score_vs_score_qc_plot_overlay_background.show();
			$psm_score_vs_score_qc_plot_overlay_container.show();
			
			if ( chartDataPerLinkTypeAsArray.length > 0 ) {

				$(".psm_score_vs_score_qc_plot_no_data_jq").hide();
				$(".psm_score_vs_score_qc_plot_param_not_a_number_jq").hide();
				$(".psm_score_vs_score_qc_plot_have_data_jq").show();
			} else {
				$(".psm_score_vs_score_qc_plot_no_data_jq").show();
				$(".psm_score_vs_score_qc_plot_have_data_jq").hide();
				$(".psm_score_vs_score_qc_plot_param_not_a_number_jq").hide();
				return;  //  EARLY EXIT
			}


			//  Hack to remove tooltip from chart
			var $google_visualization_tooltip = $(".google-visualization-tooltip");
			$google_visualization_tooltip.remove();  //  remove element and children

			//  Hide tooltips on link and image
			var $qc_plot_psm_score_vs_score_link = $("#qc_plot_psm_score_vs_score_link");
			$qc_plot_psm_score_vs_score_link.qtip('toggle', false); // Immediately hide all tooltips belonging to the selected elements
			var $psm_score_vs_score_thumbnail_chart_image = $("#psm_score_vs_score_thumbnail_chart_image");
			$psm_score_vs_score_thumbnail_chart_image.qtip('toggle', false); // Immediately hide all tooltips belonging to the selected elements
			
		}
		
		var chartDataPerLinkTypeAsArray_ForChart = chartDataPerLinkTypeAsArray;

		if ( thumbnailChart ) {
			//  Combine Data Items to reduce number of entries in chart
			//      Output elements also have new properties 'combinedCount' and optional 'opacity'
			chartDataPerLinkTypeAsArray_ForChart = this.combineDataItems( { 
				chartDataPerLinkTypeAsArray : chartDataPerLinkTypeAsArray, 
				chartAreaAxis_X : THUMBNAIL_CHART_AREA_AXIS_X , 
				chartAreaAxis_Y : THUMBNAIL_CHART_AREA_AXIS_Y } );
		} else {
			//  Combine Data Items to reduce number of entries in chart
			//      Output elements also have new properties 'combinedCount' and optional 'opacity'
			chartDataPerLinkTypeAsArray_ForChart = this.combineDataItems( { 
				chartDataPerLinkTypeAsArray : chartDataPerLinkTypeAsArray, 
				chartAreaAxis_X : FULL_SIZE_CHART_AREA_AXIS_X_use , 
				chartAreaAxis_Y : FULL_SIZE_CHART_AREA_AXIS_Y_use } );
		}
		
//		<xs:simpleType name="filter_direction_type">
//		The direction a filterable annotation type is sorted in.  
//		If set to "below", attributes with lower values are considered more significant (such as in the case of p-values). 
//		If set to "above", attributes with higher values are considered more significant (such as in the case of XCorr).

//		chart data for Google charts
		var chartData = [];

		var pointColors = [];
		
//		output columns specification

		var chartDataHeaderEntry = [ "PSM_X" ];
		
		chartDataPerLinkTypeAsArray.forEach( function( chartDataPerLinkType_Element, index, array ) {
			chartDataHeaderEntry.push( chartDataPerLinkType_Element.chartLegendLabel );
			chartDataHeaderEntry.push( {type:'string', role: 'style' } );
			pointColors.push( chartDataPerLinkType_Element.color );
		}, this );
		
		chartData.push( chartDataHeaderEntry );

		//  Add chart data to chartData

		var chartDataPerLinkTypeAsArray_ForChartLength = chartDataPerLinkTypeAsArray_ForChart.length;
		
		//  Process each selected link type data
		chartDataPerLinkTypeAsArray_ForChart.forEach( function( chartDataPerLinkType_Element, chartDataPerLinkType_Index, chartDataPerLinkType_Array ) {

			var dataItems = chartDataPerLinkType_Element.chartData;
			
			//  Process data items for link type
			dataItems.forEach( function( dataItems_Element, dataItems_Index, dataItems_Array ) {
				
				var chartDataItem = dataItems_Element;
				var combinedCount = chartDataItem.combinedCount;
				var opacity = chartDataItem.opacity;
				var entryStyle = "";
				if ( opacity ) { //  Change opacity, need to add color
					entryStyle = "fill-opacity: " + opacity + "; fill-color: " + chartDataPerLinkType_Element.color;
				}
				var chartEntry = [ chartDataItem.score_1 ];
				
				// Add nulls to chartEntry for series before link type being processed
				for ( var fillNullCount = 0; fillNullCount < chartDataPerLinkType_Index; fillNullCount++ ) {
					chartEntry.push( null ); //  null for data
					chartEntry.push( null ); //  null for style
				}
				chartEntry.push( chartDataItem.score_2 );
				chartEntry.push( entryStyle ); //  Style

				// Add nulls to chartEntry for series after link type being processed
				for ( var fillNullCount = chartDataPerLinkType_Index + 1; fillNullCount < chartDataPerLinkTypeAsArray_ForChartLength; fillNullCount++ ) {
					chartEntry.push( null ); //  null for data
					chartEntry.push( null ); //  null for style
				}

				chartData.push( chartEntry );
			}, this );

		}, this );
		

		var chartTitle = "PSM " + selectedAnnotationType_1_Text + " vs " + selectedAnnotationType_2_Text;

		var xAxisLabel = "PSM Score: " + selectedAnnotationType_1_Text;
		var yAxisLabel = "PSM Score: " + selectedAnnotationType_2_Text;

		var legendConfig = { position: 'top', alignment: 'end' };
		var chartChartArea = undefined;

		if ( ! thumbnailChart ) {
			
			chartTitle += " - " + scanFileName;
			
			legendConfig.textStyle = { fontSize : 14 };
			
			chartChartArea = {  
					left: 80,
					right: 20, // Since legend on top
					top: 40,
					bottom: 50 };
		}
		
//		var enableInteractivityConfig = undefined;
//		if ( thumbnailChart ) {
//			// Turn off interactivity for thumbnail, since going to delete it anyway and replace with PNG
//			enableInteractivityConfig = false;
//		}
		
		var optionsFullsize = {
//				enableInteractivity : enableInteractivityConfig,
				title: chartTitle, // Title above chart
//				X axis label below chart
				hAxis: { title: selectedAnnotationType_1_Text, titleTextStyle: {color: 'black'}
//		,ticks: [ 0.0, 0.2, 0.4, 0.6, 0.8, 1.0 ], format:'#.##'
//		,minValue: -.05
//		,maxValue : maxDataX
				},  
//				Y axis label left of chart
				vAxis: { title: yAxisLabel, titleTextStyle: {color: 'black'}
				,baseline: 0                    // always start at zero
//				,ticks: vAxisTicks, format:'#,###'
//				,maxValue : 10
				},
//				legend: { position: 'none' }, //  position: 'none':  Don't show legend of bar colors in upper right corner
				legend: legendConfig,
//				legend.alignment	
//				Alignment of the legend. Can be one of the following:

//				'start' - Aligned to the start of the area allocated for the legend.
//				'center' - Centered in the area allocated for the legend.
//				'end' - Aligned to the end of the area allocated for the legend.
//				Start, center, and end are relative to the style -- vertical or horizontal -- of the legend. 
//				For example, in a 'right' legend, 'start' and 'end' are at the top and bottom, respectively; 
//				for a 'top' legend, 'start' and 'end' would be at the left and right of the area, 
//				respectively.

//				The default value depends on the legend's position. For 'bottom' legends, the default is 'center'; other legends default to 'start'.

//				Type: string
//				Default: automatic
//				legend.position	
//				Position of the legend. Can be one of the following:

//				'bottom' - Below the chart.
//				'left' - To the left of the chart, provided the left axis has no series associated with it. 
//				So if you want the legend on the left, use the option targetAxisIndex: 1.
//				'in' - Inside the chart, by the top left corner.
//				'none' - No legend is displayed.
//				'right' - To the right of the chart. Incompatible with the vAxes option.
//				'top' - Above the chart.
//				Type: string
//				Default: 'right'
//				legend.textStyle	
//				An object that specifies the legend text style. The object has this format:

//				{ color: <string>,
//				fontName: <string>,
//				fontSize: <number>,
//				bold: <boolean>,
//				italic: <boolean> }
//				width : XXX, 
//				height : XXX,   // width and height of chart, otherwise controlled by enclosing div
//				bar: { groupWidth: 5 },  // set bar width large to eliminate space between bars
//				bar: { groupWidth: '100%' },  // set bar width large to eliminate space between bars
//				colors: ['red','blue'],  //  Color of bars
				colors: pointColors,  //  Color of points
				dataOpacity: .6,  // Opacity of points
//				red: #A55353
//				green: #53a553
//				blue: #5353a5
//				combined: #a5a5a5 (gray)
				tooltip: {isHtml: true},
//				isStacked: true
				chartArea : chartChartArea 
		};        
		if ( userInputMaxY ) {
//			Data points with Y axis > userInputMaxY will not be shown
			optionsFullsize.vAxis.viewWindow = { max : userInputMaxY };
		}

//		console.log( "Before Create Chart: " +  Date.now()  );
		
//		create the chart
		var data = google.visualization.arrayToDataTable( chartData );
		
		var thumbnailContainerDiv = undefined;
		
		var divToCreateChartIn = undefined;
		
		if ( thumbnailChart ) {
			divToCreateChartIn = $chartContainerDiv[0];
			
			//  Does not help
			//  Create a new container div and child div to put chart in
//			thumbnailContainerDiv = document.createElement( 'div' );
//			divToCreateChartIn = document.createElement( 'div' );
//			thumbnailContainerDiv.appendChild( divToCreateChartIn );
//			var divToCreateChartInRemoved = thumbnailContainerDiv.removeChild( divToCreateChartIn );
			
		} else {
			divToCreateChartIn = $chartContainerDiv[0];
		}
		
		
		var chartFullsize = new google.visualization.ScatterChart( divToCreateChartIn );
		 
//		if ( thumbnailChart ) {
//
//			// Wait for the chart to finish drawing before calling the getImageURI() method.
//			google.visualization.events.addListener( chartFullsize, 'ready', function () {
//				
//				//  For Thumbnail, get chart as PNG image and replace chart with that PNG image
//				var $psm_score_vs_score_image_uri_template = $("#psm_score_vs_score_image_uri_template");
//				var psm_score_vs_score_image_uri_template = $psm_score_vs_score_image_uri_template.html();
//				var imageURI = chartFullsize.getImageURI();
//				var chartImgTag = psm_score_vs_score_image_uri_template.replace( "{{image_uri}}", imageURI );
//				$chartContainerDiv.empty();
//				$chartContainerDiv.append( chartImgTag );
//				// Add tooltips for download links
//				addToolTips( $chartContainerDiv );
//			});
//		}

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
		
		chartFullsize.draw( data, optionsFullsize );
		
		
//		console.log( "After Create Chart: " +  Date.now()  );
		
//		google.visualization.events.addListener( chartFullsize, 'select', function() {
////			var tableSelection = chartFullsize.getSelection();
//
////			var tableSelection0 = tableSelection[ 0 ];
//
////			var column = tableSelection0.column;
////			var row = tableSelection0.row;
//
////			var chartDataForRow = chartData[ row ];
//
////			var z = 0;
//		});
	};
	
	
	
	//  Combine Data Items to reduce number of entries in chart
	//      Output elements also have new properties 'combinedCount' and optional 'opacity'
	
	/**
	 *   Processing to reduce number of elements inserted into chart
	 *   
	 *   Output elements also have new property 'combinedCount'
	 */
	this.combineDataItems = function( params ) {
		
		var chartDataPerLinkTypeAsArray = params.chartDataPerLinkTypeAsArray; //  score_1 is X axis, score_2 is Y axis
		var chartAreaAxis_X = params.chartAreaAxis_X;
		var chartAreaAxis_Y = params.chartAreaAxis_Y;
		
		//  Bin items by pixel,  Return items where X and Y are the center of the pixel

		// First get Min and Max
		
		var score_1_Min = null;
		var score_1_Max = null;
		var score_2_Min = null;
		var score_2_Max = null;

		chartDataPerLinkTypeAsArray.forEach( function( chartDataPerLinkType_Element, chartDataPerLinkType_Index, chartDataPerLinkType_Array ) {

			var dataItems = chartDataPerLinkType_Element.chartData;

			dataItems.forEach( function( element, index, array ) {
				var score_1 = element.score_1;
				var score_2 = element.score_2;
				if ( score_1_Min === null ) {
					score_1_Min = score_1;
					score_1_Max = score_1;
					score_2_Min = score_2;
					score_2_Max = score_2;
				} else {
					if ( score_1 < score_1_Min ) {
						score_1_Min = score_1;
					}
					if ( score_1 > score_1_Max ) {
						score_1_Max = score_1;
					}
					if ( score_2 < score_2_Min ) {
						score_2_Min = score_2;
					}
					if ( score_2 > score_2_Max ) {
						score_2_Max = score_2;
					}
				}
			}, this );
		}, this );
		
		var score_1_Max_Eq_score_1_Min = false;
		if ( score_1_Max === score_1_Min ) {
			score_1_Max_Eq_score_1_Min = true;
		};
		var score_2_Max_Eq_score_2_Min = false;
		if ( score_2_Max === score_2_Min ) {
			score_2_Max_Eq_score_2_Min = true;
		};
		
		var score_1_binSize = ( score_1_Max - score_1_Min ) / chartAreaAxis_X;
		var score_2_binSize = ( score_2_Max - score_2_Min ) / chartAreaAxis_Y;
		
		var score_1_binSizeHalf = score_1_binSize / 2;
		var score_2_binSizeHalf = score_2_binSize / 2;
		
		//  Bin the data.   Main array is score_1, sub arrays are score_2
		
		//  result []
		
		var result = [];

		chartDataPerLinkTypeAsArray.forEach( function( chartDataPerLinkType_Element, chartDataPerLinkType_Index, chartDataPerLinkType_Array ) {

			var resultItem = {}
			result.push( resultItem );
			//  Copy properties other than 'chartData' to resultItem
			Object.keys( chartDataPerLinkType_Element ).forEach( function( chartDataPerLinkType_ElementKey, index, array ) {
				if ( chartDataPerLinkType_ElementKey !== 'chartData' ) {
					resultItem[ chartDataPerLinkType_ElementKey ] = chartDataPerLinkType_Element[ chartDataPerLinkType_ElementKey ];
				}
			}, this );
			
			
			var dataItems = chartDataPerLinkType_Element.chartData;

			var binnedData = [];

			dataItems.forEach(function( element, index, array ) {
				var score_1 = element.score_1;
				var score_2 = element.score_2;
				
				var score_1_Bin = 1;
				var score_1_BinFloor = 1;
				var score_1_BinCenter = 1;
				if ( ! score_1_Max_Eq_score_1_Min ) { // Only compute if not eq
					score_1_Bin = ( score_1 - score_1_Min ) / score_1_binSize;
					score_1_BinFloor = Math.floor( score_1_Bin );
					if ( score_1_BinFloor >= chartAreaAxis_X ) {
						score_1_BinFloor = chartAreaAxis_X - 1;
					}
					score_1_BinCenter = ( score_1_BinFloor * score_1_binSize ) + score_1_Min + score_1_binSizeHalf;
				}
				
				var score_2_Bin = 1;
				var score_2_BinFloor = 1;
				var score_2_BinCenter = 1;
				if ( ! score_2_Max_Eq_score_2_Min ) { // Only compute if not eq
					score_2_Bin = ( score_2 - score_2_Min ) / score_2_binSize;
					score_2_BinFloor = Math.floor( score_2_Bin );
					if ( score_2_BinFloor >= chartAreaAxis_X ) {
						score_2_BinFloor = chartAreaAxis_X - 1;
					}
					score_2_BinCenter = ( score_2_BinFloor * score_2_binSize ) + score_2_Min + score_2_binSizeHalf;
				}

				var binnedData_FromScore_1_BinFloor = binnedData[ score_1_BinFloor ];
				if ( ! binnedData_FromScore_1_BinFloor ) {
					binnedData_FromScore_1_BinFloor = [];
					binnedData[ score_1_BinFloor ] = binnedData_FromScore_1_BinFloor;
				}
				var binnedData_FromScore_2_BinFloor = binnedData_FromScore_1_BinFloor[ score_2_BinFloor ];
				if ( ! binnedData_FromScore_2_BinFloor ) {
					binnedData_FromScore_2_BinFloor = { 
							score_1: score_1_BinCenter , 
							score_2: score_2_BinCenter, 
							combinedCount: 1,
							score_1_list : [ score_1 ],
							score_2_list : [ score_2 ]
					};
					binnedData_FromScore_1_BinFloor[ score_2_BinFloor ] = binnedData_FromScore_2_BinFloor;
				} else {
					binnedData_FromScore_2_BinFloor.combinedCount++;
					binnedData_FromScore_2_BinFloor.score_1_list.push( score_1 );
					binnedData_FromScore_2_BinFloor.score_2_list.push( score_2 );
				}

			}, this );

			var resultForLinkType = [];
			
			binnedData.forEach( function( binnedDataElement, index, array ) {
				binnedDataElement.forEach( function( binnedDataElementElement, index, array ) {
					//  Set opactiy based on count
					var combinedCount = binnedDataElementElement.combinedCount;
					var opacity = undefined;
					if ( combinedCount > 3 ) {
						opacity = "1";
					} else if ( combinedCount > 1 ) {
						opacity = "0.9"; // Default opacity is currently 0.6 in Google charts
					}
					if ( opacity ) {
						binnedDataElementElement.opacity = opacity;
					}
					
					//  If the score min eq score max, use first element from score list
					
					if ( score_1_Max_Eq_score_1_Min ) { 
						binnedDataElementElement.score_1 = binnedDataElementElement.score_1_list[ 0 ];
					} else {
						// if not eq, get from function eval binned score and score list
						// If all scores are same value, use saved score instead of binned score
						binnedDataElementElement.score_1 = this._getBinnedElement_Score_1_or_2( binnedDataElementElement.score_1, binnedDataElementElement.score_1_list );
					}
					if ( ! score_2_Max_Eq_score_2_Min ) { // if not eq, get from function eval binned score and score list
						binnedDataElementElement.score_2 = binnedDataElementElement.score_2_list[ 0 ];
					} else {
						// If all scores are same value, use saved score instead of binned score
						binnedDataElementElement.score_2 = this._getBinnedElement_Score_1_or_2( binnedDataElementElement.score_2, binnedDataElementElement.score_2_list );
					}
					
					//  Add to results
					resultForLinkType.push( binnedDataElementElement )
				}, this );		
			}, this );
			
			resultItem.chartData = resultForLinkType;
			
		}, this );
		
		//  Output to result array
		
		
		return result;

	};
	
	/**
	 * If all scores are same value, use saved score instead of binned score
	 */
	this._getBinnedElement_Score_1_or_2 = function( binned_Score, scoreList ) {
		var firstScore = scoreList[ 0 ];
		for ( var index = 1; index < scoreList.length; index++ ) {
			if ( scoreList[ index ] != firstScore ) {
				//  Entries in score list not all same so return binned_Score
				return binned_Score;  // EARLY EXIT
			}
		}
		return firstScore;  // Got here so all scoreList entries must be same
	};

	//////////////////
	
	/**
	 * 
	 */
	this.psmScoreVsScoreQCPlot_downloadData = function( params ) {

		var objectThis = this;
		
		var thumbnailChart = false;
		var selectedAnnotationType_1_Text = undefined
		var selectedAnnotationType_2_Text = undefined

		if ( params ) {
			if ( params.thumbnailChart ) {
				thumbnailChart = true;
			}
			if ( params.selectedAnnotationType_1_Text ) {
				selectedAnnotationType_1_Text = params.selectedAnnotationType_1_Text;
			}
			if ( params.selectedAnnotationType_2_Text ) {
				selectedAnnotationType_2_Text = params.selectedAnnotationType_2_Text;
			}
		}

		try {
			var chartDataFromServer = undefined;

			if ( thumbnailChart ) {
			
				if ( this.globals_PSMScoreVsScoresChart === undefined || this.globals_PSMScoreVsScoresChart.chartDataFromServer_Thumbnail === undefined ) {
//					No stored chart data from server so exit
					return;  //  EARLY EXIT
				}
			
				chartDataFromServer = this.globals_PSMScoreVsScoresChart.chartDataFromServer_Thumbnail;
				
			} else {
				if ( this.globals_PSMScoreVsScoresChart === undefined || this.globals_PSMScoreVsScoresChart.chartDataFromServer_FullSize_Overlay === undefined ) {
//					No stored chart data from server so exit
					return;  //  EARLY EXIT
				}
				
				chartDataFromServer = this.globals_PSMScoreVsScoresChart.chartDataFromServer_FullSize_Overlay;
				
//				Selected Annotation Text
				selectedAnnotationType_1_Text = $("#psm_score_vs_score_qc_plot_score_type_id_1 option:selected").text();
				selectedAnnotationType_2_Text = $("#psm_score_vs_score_qc_plot_score_type_id_2 option:selected").text();

			}

			// TODO  Hard code taking first project search id
			var projectSearchId = _project_search_ids[ 0 ];	

			var crosslinkChartData = chartDataFromServer.crosslinkChartData;
			var looplinkChartData = chartDataFromServer.looplinkChartData;
			var unlinkedChartData = chartDataFromServer.unlinkedChartData;

			var headerLine = "search id\tpsm id\ttype\t" + selectedAnnotationType_1_Text + "\t" + selectedAnnotationType_2_Text;

			var outputStringArray = new Array();

			outputStringArray.push( headerLine );

			if ( crosslinkChartData ) {
				this._downloadData_ProcessForType( { 
					dataForType: crosslinkChartData, 
					linkTypeLabel: "crosslink", 
					projectSearchId : projectSearchId, 
					outputStringArray : outputStringArray 
				}  );
			}
			if ( looplinkChartData ) {
				this._downloadData_ProcessForType( { 
					dataForType: looplinkChartData, 
					linkTypeLabel: "looplink", 
					projectSearchId : projectSearchId, 
					outputStringArray : outputStringArray 
				}  );
			}
			if ( unlinkedChartData ) {
				this._downloadData_ProcessForType( { 
					dataForType: unlinkedChartData, 
					linkTypeLabel: "unlinkedChartData", 
					projectSearchId : projectSearchId, 
					outputStringArray : outputStringArray 
				}  );
			}

			var content = outputStringArray.join("\n");

			var filename = "proxlPsmScoreScoreData.txt"
				var mimetype = "text/plain";

			downloadStringAsFile( filename, mimetype, content )

		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}

	};

	/**
	 * 
	 */
	this._downloadData_ProcessForType = function( params ) {

		var dataForType = params.dataForType;
		var linkTypeLabel = params.linkTypeLabel;
		var projectSearchId= params.projectSearchId;
		var outputStringArray = params.outputStringArray;

		for ( var index = 0; index < dataForType.length; index++ ) {

			var dataForTypeEntry = dataForType[ index ];

			var outputType = linkTypeLabel;

			var outputScore_1_Value = dataForTypeEntry.score_1.toFixed( PSM_SCORE_VS_SCORE_CHART_BIN_START_OR_END_TO_FIXED_VALUE );
			var outputScore_2_Value = dataForTypeEntry.score_2.toFixed( PSM_SCORE_VS_SCORE_CHART_BIN_START_OR_END_TO_FIXED_VALUE );

			var outputLine = 
				projectSearchId + 
				"\t" + dataForTypeEntry.psmId + 
				"\t" + outputType + 
				"\t" + outputScore_1_Value + 
				"\t" + outputScore_2_Value ;

			outputStringArray.push( outputLine );
		}
	};
	
};

/**
 * page variable 
 */

var qcPageChart_PSM_Score_Vs_Score_PSM = new QCPageChart_PSM_Score_Vs_Score_PSM();

export { qcPageChart_PSM_Score_Vs_Score_PSM }
