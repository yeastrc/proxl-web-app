/**
 * qcPageChart_PSM_Count_Vs_Score_PSM.js
 * 
 * Javascript for the viewQC.jsp page - Chart PSM Count Vs Score - in PSM Level section
 * 
 * page variable qcPageChart_PSM_Count_Vs_Score_PSM
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
var QCPageChart_PSM_Count_Vs_Score_PSM = function() {



	//	CONSTANTS - PSM Count Vs Score Chart

	var PSM_COUNT_VS_SCORE_CHART_BIN_START_OR_END_TO_FIXED_VALUE = 4;
	var PSM_COUNT_VS_SCORE_CHART_PERCENTAGE_OF_MAX_TO_FIXED_VALUE = 1;
	var PSM_COUNT_VS_SCORE_CHART_PERCENTAGE_OF_MAX_TO_FIXED_VALUE_OUTPUT_FILE = 4;
	var PSM_COUNT_VS_SCORE_CHART_COMPARISON_DIRECTION_STRING_ABOVE_ASCII = ">=";
	var PSM_COUNT_VS_SCORE_CHART_COMPARISON_DIRECTION_STRING_BELOW_ASCII = "<=";
	var PSM_COUNT_VS_SCORE_CHART_COMPARISON_DIRECTION_STRING_ABOVE = "\u2265"; // ">=" as a single character
	var PSM_COUNT_VS_SCORE_CHART_COMPARISON_DIRECTION_STRING_BELOW = "\u2264"; // "<=" as a single character
	var RELOAD_PSM_COUNT_VS_SCORE_CHART_TIMER_DELAY = 400;  // in Milliseconds
	
	var PSM_COUNT_VS_SCORE_CHART_Y_AXIS_CHOICE_PERCENTAGE = "PERCENTAGE";
	var PSM_COUNT_VS_SCORE_CHART_Y_AXIS_CHOICE_RAW_COUNTS = "RAW_COUNTS";

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
	
	var _helpTooltipHTML = undefined;
	
	
	/**
	 * Used by PSM Count Vs Scores Chart Code
	 */
	this.globals_PSMCountVsScoresChart = {
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
			var $psm_level_block_help_tooltip_psm_counts_vs_score = $("#psm_level_block_help_tooltip_psm_counts_vs_score");
			if ( $psm_level_block_help_tooltip_psm_counts_vs_score.length === 0 ) {
				throw Error( "No element found with id 'psm_level_block_help_tooltip_psm_counts_vs_score' " );
			}
			_helpTooltipHTML = $psm_level_block_help_tooltip_psm_counts_vs_score.html();

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

		$("#qc_plot_psm_count_vs_score_link").click(function(eventObject) {
			try {
				var clickThis = this;
				objectThis.psmCountVsScoreQCPlotClickHandler( clickThis, eventObject );
				return false;
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});	 	
		$(".psm_count_vs_score_qc_plot_overlay_close_parts_jq").click(function(eventObject) {
			try {
				var clickThis = this;
				if ( objectThis.reloadPSMCountVsScoreChartTimerId ) {
					clearTimeout( objectThis.reloadPSMCountVsScoreChartTimerId );
					objectThis.reloadPSMCountVsScoreChartTimerId = null;
				}
				objectThis.closePSMCountVsScoreQCPlotOverlay( clickThis, eventObject );
				return false;
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		$("#psm_count_vs_score_qc_plot_score_type_id").change(function(eventObject) {
			try {
				objectThis.psmCountVsScoreQCPlot_scoreTypeChanged( );
				return false;
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		$(".psm_count_vs_score_qc_plot_on_change_jq").change(function(eventObject) {
			try {
				objectThis.psmCountVsScoreQCPlot_createChartFromPageParams( );
				return false;
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		$(".psm_count_vs_score_qc_plot_on_change_jq").keyup(function(eventObject) {
			try {
				if ( objectThis.reloadPSMCountVsScoreChartTimerId ) {
					clearTimeout( objectThis.reloadPSMCountVsScoreChartTimerId );
					objectThis.reloadPSMCountVsScoreChartTimerId = null;
				}
				objectThis.reloadPSMCountVsScoreChartTimerId = setTimeout( function() {
					try {
						objectThis.psmCountVsScoreQCPlot_createChartFromPageParams( );
					} catch( e ) {
						reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
						throw e;
					}
				}, RELOAD_PSM_COUNT_VS_SCORE_CHART_TIMER_DELAY );
				return false;
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		$("#psm_count_vs_score_qc_plot_max_reset_button").click(function(eventObject) {
			try {
				objectThis.psmCountVsScoreQCPlot_resetMaxXMaxY();
				objectThis.psmCountVsScoreQCPlot_createChartFromPageParams( );
				return false;
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});

		// For PSM Count Vs Score, add Download Click handlers for overlay
		var $psm_count_vs_score_qc_plot_chartDiv_Container = $("#psm_count_vs_score_qc_plot_chartDiv_Container");

		//  Download Data Setup
		var downloadDataCallback = function( params ) {
//			var clickedThis = params.clickedThis;

			//  Download the data for params
			objectThis.psmCountVsScoreQCPlot_downloadData( { thumbnailChart : false } );
		};
		
		qcChartDownloadHelp.add_DownloadClickHandlers_HelpTooltip( { 
			$chart_outer_container_for_download_jq :  $psm_count_vs_score_qc_plot_chartDiv_Container, 
			downloadDataCallback : downloadDataCallback,
			helpTooltipHTML : _helpTooltipHTML 
		} );
		
	};


	///////////////////////////////////////////

	///////////////////////////////////////////

	///////   PSM Count Vs Score Chart


	/**
	 * Clear data for 
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
			this.createPSMCountVsScoreChartThumbnail();
//		}
	};

//	var _activeAjax = null;

	///
	this.createPSMCountVsScoreChartThumbnail = function() {
		
		// TODO  Hard code taking first project search id
		var projectSearchId = _project_search_ids[ 0 ];	

		//  TODO  Need this for thumbnail
		
		var $psm_count_vs_score_outer_container_div = $("#psm_count_vs_score_outer_container_div");
		$psm_count_vs_score_outer_container_div.empty();
		
		this._placeEmptyDummyChartForMessage( { 
			$chart_outer_container_jq : $psm_count_vs_score_outer_container_div, 
			//				linkType : selectedLinkType, 
			messagePrefix:  _DUMMY_CHART_STATUS_TEXT_PREFIX_LOADING,
			messageSuffix:  _DUMMY_CHART_STATUS_TEXT_SUFFIX_LOADING
		} );
		
		this.psmCountVsScoreQCPlot_createChartFromPageParams( { thumbnailChart : true } );
	};

/////////////////
	this.psmCountVsScoreQCPlotClickHandler = function(clickThis, eventObject) {
		var objectThis = this;
		objectThis.openPSMCountVsScoreQCPlotOverlay(clickThis, eventObject);
		return;
	};

///////////
	this.closePSMCountVsScoreQCPlotOverlay = function(clickThis, eventObject) {
		$(".psm_count_vs_score_qc_plot_overlay_show_hide_parts_jq").hide();
		var $psm_count_vs_score_qc_plot_chartDiv = $("#psm_count_vs_score_qc_plot_chartDiv");
		$psm_count_vs_score_qc_plot_chartDiv.empty();
	};

///////////
	this.openPSMCountVsScoreQCPlotOverlay = function(clickThis, eventObject) {
		var objectThis = this;
		var $clickThis = $(clickThis);
		//	get root div for this search
		var $search_root_jq = $clickThis.closest(".search_root_jq");
		var projectSearchId = $search_root_jq.attr("data-project_search_id");	

		// Position dialog over clicked link
		//  get position of div containing the dialog that is inline in the page
		var $psm_count_vs_score_qc_plot_overlay_containing_outermost_div_inline_div = $("#psm_count_vs_score_qc_plot_overlay_containing_outermost_div_inline_div");
		var offset__containing_outermost_div_inline_div = $psm_count_vs_score_qc_plot_overlay_containing_outermost_div_inline_div.offset();
		var offsetTop__containing_outermost_div_inline_div = offset__containing_outermost_div_inline_div.top;
		var $psm_count_vs_score_qc_plot_overlay_container = $("#psm_count_vs_score_qc_plot_overlay_container");
		var scrollTopWindow = $(window).scrollTop();
		var positionAdjust = scrollTopWindow - offsetTop__containing_outermost_div_inline_div + 10;
		$psm_count_vs_score_qc_plot_overlay_container.css( "top", positionAdjust );
		var $psm_count_vs_score_qc_plot_overlay_container = $("#psm_count_vs_score_qc_plot_overlay_container");
		var $psm_count_vs_score_qc_plot_overlay_background = $("#psm_count_vs_score_qc_plot_overlay_background"); 
		$psm_count_vs_score_qc_plot_overlay_background.show();
		$psm_count_vs_score_qc_plot_overlay_container.show();
		////////////////

		this.globals_PSMCountVsScoresChart.currentSearchData = undefined;   ///  Reset "currentSearchData" for new search

		var hash_json_Contents = _get_hash_json_Contents();
		
		//		initialize all "Link Type" checkboxes to checked or not based on main page ( in hash JSON object hash_json_Contents )
		//  First set all to unchecked
		var $psm_count_vs_score_qc_plot_link_type_include_jq = $(".psm_count_vs_score_qc_plot_link_type_include_jq");
		$psm_count_vs_score_qc_plot_link_type_include_jq.each( function(   ) {
			$( this ).prop('checked',false);
		} );
		//  Set to checked for entries in hash_json_Contents.linkTypes
		if ( hash_json_Contents.linkTypes ) {
			hash_json_Contents.linkTypes.forEach( function ( currentArrayValue, index, array ) {
				var entry = currentArrayValue;
				var htmlId = "psm_count_vs_score_qc_plot_link_type_checkbox_" + entry;
				var $checkbox = $( "#" + htmlId );
				$checkbox.prop('checked',true);
			}, this /* passed to function as this */ );
		}
		
		
		//  Empty Include Protein Name option Select before get list of protein names
		var $psm_count_vs_score_qc_plot_protein_seq_id_include_select = $("#psm_count_vs_score_qc_plot_protein_seq_id_include_select");
		$psm_count_vs_score_qc_plot_protein_seq_id_include_select.empty();
		$psm_count_vs_score_qc_plot_protein_seq_id_include_select.val("");

		//  Empty Exclude Protein Name option Select before get list of protein names
		var $psm_count_vs_score_qc_plot_protein_seq_id_exclude_select = $("#psm_count_vs_score_qc_plot_protein_seq_id_exclude_select");
		$psm_count_vs_score_qc_plot_protein_seq_id_exclude_select.empty();
		$psm_count_vs_score_qc_plot_protein_seq_id_exclude_select.val("");

		var callback_GetScanFilesForProjectSearchId = function( params ) {
			objectThis.getScanFilesForProjectSearchId_ProcessResponse( params );
		};
		
		_getScanFilesForProjectSearchId( { 
			htmlElementScanFileTR : "#psm_count_vs_score_qc_plot_overlay_scan_file_selector_row",
			addAllOption : true,
			callback: callback_GetScanFilesForProjectSearchId } );

		this.psmCountVsScoreQCPlot_getProteinSeqIdsProteinAnnNamesForProjectSearchId( { 
			projectSearchId: projectSearchId
		} );

	};

	this.getScanFilesForProjectSearchId_ProcessResponse = function( params ) {
//		var objectThis = this;
		var scanFiles = params.scanFiles;
		var selectorUpdated = params.selectorUpdated;
		if ( ! selectorUpdated ) {
			
		}
		
		var $psm_count_vs_score_qc_plot_overlay_scan_file_selector_row = $("#psm_count_vs_score_qc_plot_overlay_scan_file_selector_row");
		if ( ! scanFiles ) {
			// No scan files
			$psm_count_vs_score_qc_plot_overlay_scan_file_selector_row.hide();
		} else {
			$psm_count_vs_score_qc_plot_overlay_scan_file_selector_row.show();
		}
		
		this.psmCountVsScoreQCPlot_getPSMFilterableAnnTypesForProjectSearchId( );
	};

//	/  
	this.psmCountVsScoreQCPlot_getPSMFilterableAnnTypesForProjectSearchId = function( params ) {
		var objectThis = this;

		// TODO  Hard code taking first project search id
		var projectSearchId = _project_search_ids[ 0 ];	
		
		var _URL = contextPathJSVar + "/services/annotationTypes/getAnnotationTypesPsmFilterableForProjectSearchId";
		var requestData = {
				projectSearchId : projectSearchId
		};
//		var request =
		$.ajax({
			type : "GET",
			url : _URL,
			data : requestData,
			dataType : "json",
			success : function(data) {
				objectThis.psmCountVsScoreQCPlot_getPSMFilterableAnnTypesForProjectSearchIdResponse(requestData, data, params);
			},
			failure: function(errMsg) {
				handleAJAXFailure( errMsg );
			},
			error : function(jqXHR, textStatus, errorThrown) {
				handleAJAXError(jqXHR, textStatus, errorThrown);
			}
		});
	};

//	/
	this.psmCountVsScoreQCPlot_getPSMFilterableAnnTypesForProjectSearchIdResponse = function(requestData, responseData, originalParams) {
		var annTypesSearchProgramsPerSearch = responseData.annotationTypeList;
		if (  annTypesSearchProgramsPerSearch.length === 0 ) {
			throw "annTypesSearchProgramsPerSearch.length === 0";
		}
		if ( ! this.globals_PSMCountVsScoresChart.currentSearchData ) {
			this.globals_PSMCountVsScoresChart.currentSearchData = {};
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
		this.globals_PSMCountVsScoresChart.currentSearchData.annotationTypeDataArray = annTypes;
		this.globals_PSMCountVsScoresChart.currentSearchData.annotationTypeDataById = annTypesById;
		var annTypes = this.globals_PSMCountVsScoresChart.currentSearchData.annotationTypeDataArray;
		var $psm_count_vs_score_qc_plot_score_type_id = $("#psm_count_vs_score_qc_plot_score_type_id");
		$psm_count_vs_score_qc_plot_score_type_id.empty();
		var optionsHTMLarray = [];
		for ( var annTypesIndex = 0; annTypesIndex < annTypes.length; annTypesIndex++ ) {
			var annType = annTypes[ annTypesIndex ];
			var html = "<option value='" + annType.id + "'>" + annType.name +
			" (" + annType.searchProgramName + ")" +
			"</option>";
			optionsHTMLarray.push( html );
		}
		var optionsHTML = optionsHTMLarray.join("");
		$psm_count_vs_score_qc_plot_score_type_id.append( optionsHTML );
		
		//  Set specific annotation type record as "selected"
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
			$psm_count_vs_score_qc_plot_score_type_id.val( annTypeIdToUse );
		}
		
		this.psmCountVsScoreQCPlot_createChartFromPageParams();
	};


//	/  
	this.psmCountVsScoreQCPlot_getProteinSeqIdsProteinAnnNamesForProjectSearchId = function( params ) {
		var objectThis = this;
//		if ( ! qcChartsInitialized ) {
//			throw "qcChartsInitialized is false"; 
//		}

		// TODO  Hard code taking first project search id
		var projectSearchId = _project_search_ids[ 0 ];	
		
		var _URL = contextPathJSVar + "/services/proteinNames/getProteinNameListForProjectSearchId";
		var requestData = {
				projectSearchId : projectSearchId
		};
//		var request =
		$.ajax({
			type : "GET",
			url : _URL,
			data : requestData,
			dataType : "json",
			success : function(data) {
				objectThis.psmCountVsScoreQCPlot_getProteinSeqIdsProteinAnnNamesForProjectSearchIdResponse(requestData, data, params);
			},
			failure: function(errMsg) {
				handleAJAXFailure( errMsg );
			},
			error : function(jqXHR, textStatus, errorThrown) {
				handleAJAXError(jqXHR, textStatus, errorThrown);
			}
		});
	};

//	/
	this.psmCountVsScoreQCPlot_getProteinSeqIdsProteinAnnNamesForProjectSearchIdResponse = function(requestData, responseData, originalParams) {

		var proteinSequenceIdProteinAnnotationNameList = responseData.proteinSequenceIdProteinAnnotationNameList;

		//	Empty Include Protein Name option Select before get list of protein names
		var $psm_count_vs_score_qc_plot_protein_seq_id_include_select = $("#psm_count_vs_score_qc_plot_protein_seq_id_include_select");
		$psm_count_vs_score_qc_plot_protein_seq_id_include_select.empty();

		//	Empty Exclude Protein Name option Select before get list of protein names
		var $psm_count_vs_score_qc_plot_protein_seq_id_exclude_select = $("#psm_count_vs_score_qc_plot_protein_seq_id_exclude_select");
		$psm_count_vs_score_qc_plot_protein_seq_id_exclude_select.empty();

		var optionsHTMLarray = [];
		for ( var dataIndex = 0; dataIndex < proteinSequenceIdProteinAnnotationNameList.length; dataIndex++ ) {
			var proteinSequenceIdProteinAnnotationName = proteinSequenceIdProteinAnnotationNameList[ dataIndex ];
			var html = "<option value='" + proteinSequenceIdProteinAnnotationName.proteinSequenceId + 
			"'>" + proteinSequenceIdProteinAnnotationName.annotationName +
			"</option>";
			optionsHTMLarray.push( html );
		}
		var optionsHTML = optionsHTMLarray.join("");
		$psm_count_vs_score_qc_plot_protein_seq_id_include_select.append( optionsHTML );
		$psm_count_vs_score_qc_plot_protein_seq_id_exclude_select.append( optionsHTML );

		$psm_count_vs_score_qc_plot_protein_seq_id_include_select.val("");
		$psm_count_vs_score_qc_plot_protein_seq_id_exclude_select.val("");
	};

/////////////
	this.psmCountVsScoreQCPlot_scoreTypeChanged = function( ) {
//		var objectThis = this;
		var annTypes = this.globals_PSMCountVsScoresChart.currentSearchData.annotationTypeDataById;
		var $psm_count_vs_score_qc_plot_score_type_id = $("#psm_count_vs_score_qc_plot_score_type_id");
		var selectedAnnTypeId = $psm_count_vs_score_qc_plot_score_type_id.val( );
		var annTypeForSelectId = annTypes[ selectedAnnTypeId ];
		if ( annTypeForSelectId === undefined || annTypeForSelectId === null ) {
			throw "annType not found for id: " + selectedAnnTypeId;
		}
		var $psm_count_vs_score_qc_plot_max_x = $("#psm_count_vs_score_qc_plot_max_x");
		var $psm_count_vs_score_qc_plot_max_y = $("#psm_count_vs_score_qc_plot_max_y");
		$psm_count_vs_score_qc_plot_max_x.val("");
		$psm_count_vs_score_qc_plot_max_y.val("");
		this.psmCountVsScoreQCPlot_createChartFromPageParams( );
	};

//	/  
	this.psmCountVsScoreQCPlot_createChartFromPageParams = function( params ) {
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

		if ( ! this.globals_PSMCountVsScoresChart.currentSearchData ) {
			this.globals_PSMCountVsScoresChart.currentSearchData = {};
		}
		
		var hash_json_Contents = _get_hash_json_Contents();

		//  Set for either thumbnail or overlay
		var annotationTypeId = undefined;
		var selectedAnnotationTypeText = undefined;
		var selectedLinkTypes = undefined;

		if ( thumbnailChart ) {
			
			selectedLinkTypes = hash_json_Contents.linkTypes;
			
			//  TODO  Properly handle no link types selected on main page
			
//			var selectedLinkTypes = this.psmCountVsScoreQCPlot_getLinkTypesChecked();
//			if ( selectedLinkTypes.length === 0 ) {
//				$(".psm_count_vs_score_qc_plot_no_data_jq").show();
//				$(".psm_count_vs_score_qc_plot_param_not_a_number_jq").hide();
//				$(".psm_count_vs_score_qc_plot_have_data_jq").hide();
//				return;  //  EARLY EXIT		
//			}
			
		} else {

			if ( this.reloadPSMCountVsScoreChartTimerId ) {
				clearTimeout( this.reloadPSMCountVsScoreChartTimerId );
				this.reloadPSMCountVsScoreChartTimerId = null;
			}
			var $psm_count_vs_score_qc_plot_max_x = $("#psm_count_vs_score_qc_plot_max_x");
			var $psm_count_vs_score_qc_plot_max_y = $("#psm_count_vs_score_qc_plot_max_y");
			/////////////
			//  if Y Axis choice has changed, clear Y Axis Max input field
			if ( ( $("#psm_count_vs_score_qc_plot_y_axis_as_percentage").prop("checked")
					&& this.globals_PSMCountVsScoresChart.prevYAxisChoice !== PSM_COUNT_VS_SCORE_CHART_Y_AXIS_CHOICE_PERCENTAGE )
					|| ( $("#psm_count_vs_score_qc_plot_y_axis_as_raw_counts").prop("checked")
							&& this.globals_PSMCountVsScoresChart.prevYAxisChoice !== PSM_COUNT_VS_SCORE_CHART_Y_AXIS_CHOICE_RAW_COUNTS ) ) {
				$psm_count_vs_score_qc_plot_max_y.val( "" );
			}
			//  Set this.globals_PSMCountVsScoresChart.prevYAxisChoice per current selected Y Axis choice
			if ( $("#psm_count_vs_score_qc_plot_y_axis_as_percentage").prop("checked") ) {
				this.globals_PSMCountVsScoresChart.prevYAxisChoice = PSM_COUNT_VS_SCORE_CHART_Y_AXIS_CHOICE_PERCENTAGE;
			}
			if ( $("#psm_count_vs_score_qc_plot_y_axis_as_raw_counts").prop("checked") ) {
				this.globals_PSMCountVsScoresChart.prevYAxisChoice = PSM_COUNT_VS_SCORE_CHART_Y_AXIS_CHOICE_RAW_COUNTS;
			}
			/////////////
			var userInputMaxX = $psm_count_vs_score_qc_plot_max_x.val();
			var userInputMaxY = $psm_count_vs_score_qc_plot_max_y.val();
			if ( userInputMaxX !== "" ) {
				// only test for valid Max X value if not empty string
				if ( !  /^[+-]?((\d+(\.\d*)?)|(\.\d+))$/.test( userInputMaxX ) ) {
					//  Max X value is not a valid decimal number
					$(".psm_count_vs_score_qc_plot_param_not_a_number_jq").show();
					$(".psm_count_vs_score_qc_plot_no_data_jq").hide();
					$(".psm_count_vs_score_qc_plot_have_data_jq").hide();
					$psm_count_vs_score_qc_plot_max_x.focus();
					return;  //  EARLY EXIT
				}
//				if ( userInputMaxXNum < 0 ) {

//				$psm_count_vs_score_qc_plot_max_x.val( "0" );

//				userInputMaxX = "0";
//				}
			}	
			if ( userInputMaxY !== "" ) {
				// only test for valid Max Y value if not empty string
				if ( !  /^[+-]?((\d+(\.\d*)?)|(\.\d+))$/.test( userInputMaxY ) ) {
					//  Max Y value is not a valid decimal number
					$(".psm_count_vs_score_qc_plot_param_not_a_number_jq").show();
					$(".psm_count_vs_score_qc_plot_no_data_jq").hide();
					$(".psm_count_vs_score_qc_plot_have_data_jq").hide();
					$psm_count_vs_score_qc_plot_max_y.focus();
					return;  //  EARLY EXIT
				}
				if ( $("#psm_count_vs_score_qc_plot_y_axis_as_percentage").prop("checked") ) {
					var userInputMaxYNum = parseFloat( userInputMaxY ); 
					if ( userInputMaxYNum < 0 ) {
						$psm_count_vs_score_qc_plot_max_y.val( "0" );
						userInputMaxY = "0";
					}
					if ( userInputMaxYNum > 100 ) {
						$psm_count_vs_score_qc_plot_max_y.val( "100" );
						userInputMaxY = "100";
					}
				}
			}	

			var $psm_count_vs_score_qc_plot_score_type_id = $("#psm_count_vs_score_qc_plot_score_type_id");
			annotationTypeId = $psm_count_vs_score_qc_plot_score_type_id.val();
			selectedAnnotationTypeText = $("#psm_count_vs_score_qc_plot_score_type_id option:selected").text();
			selectedLinkTypes = this.psmCountVsScoreQCPlot_getLinkTypesChecked();
			if ( selectedLinkTypes.length === 0 ) {
				$(".psm_count_vs_score_qc_plot_no_data_jq").show();
				$(".psm_count_vs_score_qc_plot_param_not_a_number_jq").hide();
				$(".psm_count_vs_score_qc_plot_have_data_jq").hide();
				return;  //  EARLY EXIT		
			}
		}

		var $psm_count_vs_score_qc_plot_scan_file_id = $("#psm_count_vs_score_qc_plot_scan_file_id");
		var scanFileId = $psm_count_vs_score_qc_plot_scan_file_id.val();
		var scanFileName = $( "#psm_count_vs_score_qc_plot_scan_file_id option:selected" ).text();

		var createChartParams =  {  
				projectSearchId : projectSearchId,
				scanFileId : scanFileId,
				scanFileName : scanFileName,
				annotationTypeId : annotationTypeId,
				selectedAnnotationTypeText : selectedAnnotationTypeText,
				selectedLinkTypes : selectedLinkTypes,
				userInputMaxX : userInputMaxX,
				userInputMaxY : userInputMaxY, 
				thumbnailChart : thumbnailChart
		};

		if ( ! thumbnailChart ) {

			//  Process User select of proteins to include

			var $psm_count_vs_score_qc_plot_protein_seq_id_include_select = $("#psm_count_vs_score_qc_plot_protein_seq_id_include_select");
			var psm_count_vs_score_qc_plot_protein_seq_id_include_select = $psm_count_vs_score_qc_plot_protein_seq_id_include_select.val();

			if ( psm_count_vs_score_qc_plot_protein_seq_id_include_select !== null ) {
				createChartParams.includeProteinSequenceIds = psm_count_vs_score_qc_plot_protein_seq_id_include_select;
			}

			//  Process User select of proteins to exclude

			var $psm_count_vs_score_qc_plot_protein_seq_id_exclude_select = $("#psm_count_vs_score_qc_plot_protein_seq_id_exclude_select");
			var psm_count_vs_score_qc_plot_protein_seq_id_exclude_select = $psm_count_vs_score_qc_plot_protein_seq_id_exclude_select.val();

			if ( psm_count_vs_score_qc_plot_protein_seq_id_exclude_select !== null ) {
				createChartParams.excludeProteinSequenceIds = psm_count_vs_score_qc_plot_protein_seq_id_exclude_select;
			}
		}

		this.psmCountVsScoreQCPlot_createChart( createChartParams );
	};


	this.psmCountVsScoreQCPlot_getLinkTypesChecked = function(  ) {
		var selectedLinkTypes = [];
		var $psm_count_vs_score_qc_plot_link_type_include_jq = $(".psm_count_vs_score_qc_plot_link_type_include_jq");
		$psm_count_vs_score_qc_plot_link_type_include_jq.each( function(   ) {
			var $thisCheckbox = $( this );
			if ( $thisCheckbox.prop('checked') ) {
				var checkboxValue = $thisCheckbox.attr("value");
				selectedLinkTypes.push( checkboxValue );
			}
		} );
		return selectedLinkTypes;
	};


//	/  
	this.psmCountVsScoreQCPlot_createChart = function( params ) {
		var objectThis = this;
		var projectSearchId = params.projectSearchId;
		var scanFileId = params.scanFileId;
		var scanFileName = params.scanFileName;
		var selectedLinkTypes = params.selectedLinkTypes;
		var includeProteinSequenceIds = params.includeProteinSequenceIds;
		var excludeProteinSequenceIds = params.excludeProteinSequenceIds;
		var annotationTypeId = params.annotationTypeId;
		var userInputMaxXString = params.userInputMaxX;
		var thumbnailChart = params.thumbnailChart;
		
		var $psm_count_vs_score_qc_plot_chartDiv = $("#psm_count_vs_score_qc_plot_chartDiv");
		$psm_count_vs_score_qc_plot_chartDiv.empty();
		var _URL = contextPathJSVar + "/services/qcplot/getPsmCountsVsScore";
		var requestData = {
				selectedLinkTypes : selectedLinkTypes,
				iP : includeProteinSequenceIds,
				eP : excludeProteinSequenceIds,
				projectSearchId : projectSearchId,
				annotationTypeId : annotationTypeId
		};
		if ( userInputMaxXString !== "" && userInputMaxXString !== undefined ) {
			var psmScoreCutoff = userInputMaxXString;
			requestData.psmScoreCutoff = psmScoreCutoff;
		}

		if ( scanFileId === "" || scanFileId === undefined || scanFileId === null ) {
		} else {
			//  Add scan file id
			requestData.scanFileId = [ scanFileId ];
		}
		
		// var request =
		$.ajax({
			type : "GET",
			url : _URL,
			data : requestData,
			traditional: true,  //  Force traditional serialization of the data sent
			//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
			//   So scansFor array is passed as "scansFor=<value>" which is what Jersey expects
			dataType : "json",
			success : function(data) {
				objectThis.psmCountVsScoreQCPlot_createChartResponse(requestData, data, params);
			},
			failure: function(errMsg) {
				handleAJAXFailure( errMsg );
			},
			error : function(jqXHR, textStatus, errorThrown) {
				handleAJAXError(jqXHR, textStatus, errorThrown);
				// alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
				// textStatus: " + textStatus );
			}
		});
	};


	this.psmCountVsScoreQCPlot_createChartResponse = function(requestData, responseData, originalParams) {
		var objectThis = this;
		
		var scanFileId = originalParams.scanFileId;
		var scanFileName = originalParams.scanFileName;

		var selectedAnnotationTypeText = originalParams.selectedAnnotationTypeText;
		var userInputMaxXString = originalParams.userInputMaxX;
		var userInputMaxYString = originalParams.userInputMaxY;
		var thumbnailChart = originalParams.thumbnailChart;

		var chartDataParam = responseData;

		if ( thumbnailChart ) {
			this.globals_PSMCountVsScoresChart.chartDataFromServer_Thumbnail = chartDataParam;
		} else {
			this.globals_PSMCountVsScoresChart.chartDataFromServer_FullSize_Overlay = chartDataParam;
		}
		
		if ( ! selectedAnnotationTypeText ) {
			selectedAnnotationTypeText = chartDataParam.annotationTypeName + " (" + chartDataParam.searchProgramName + ")";
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

		var $chartContainer = undefined;
		if ( thumbnailChart ) {
			var $psm_count_vs_score_outer_container_div = $("#psm_count_vs_score_outer_container_div");
			$psm_count_vs_score_outer_container_div.empty();
			var $chart_container_jq = this._addChartInnerTemplate( { $chart_outer_container_jq : $psm_count_vs_score_outer_container_div } );
			
			//  Download Data Setup
			var downloadDataCallback = function( params ) {
//				var clickedThis = params.clickedThis;

				//  Download the data for params
				objectThis.psmCountVsScoreQCPlot_downloadData( { thumbnailChart : thumbnailChart, selectedAnnotationTypeText : selectedAnnotationTypeText } );
			};
			
			qcChartDownloadHelp.add_DownloadClickHandlers_HelpTooltip( { 
				$chart_outer_container_for_download_jq :  $psm_count_vs_score_outer_container_div, 
				downloadDataCallback : downloadDataCallback,
				helpTooltipHTML : _helpTooltipHTML 
			} );
			
			// Make so clicking on the thumbnail chart opens the overlay
			$chart_container_jq.click(function(eventObject) {
				try {
					var clickThis = this;
					objectThis.psmCountVsScoreQCPlotClickHandler( clickThis, eventObject );
					eventObject.preventDefault();
					eventObject.stopPropagation();
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			});	 
			
			$chartContainer = $chart_container_jq;
		} else {
			$chartContainer = $("#psm_count_vs_score_qc_plot_chartDiv");
			$chartContainer.empty();
		}
		
		var dataArraySize = chartDataParam.dataArraySize;
		var crosslinkChartData = chartDataParam.crosslinkData;
		var looplinkChartData = chartDataParam.looplinkData;
		var unlinkedChartData = chartDataParam.unlinkedData;
		var alllinkChartData = chartDataParam.alllinkData;
		if ( ( crosslinkChartData && crosslinkChartData.chartBuckets.length > 0 )
				|| ( looplinkChartData && looplinkChartData.chartBuckets.length > 0 )
				|| ( unlinkedChartData && unlinkedChartData.chartBuckets.length > 0 )
				|| ( alllinkChartData && alllinkChartData.chartBuckets.length > 0 ) ) {
			$(".psm_count_vs_score_qc_plot_no_data_jq").hide();
			$(".psm_count_vs_score_qc_plot_param_not_a_number_jq").hide();
			$(".psm_count_vs_score_qc_plot_have_data_jq").show();
		} else {
			$(".psm_count_vs_score_qc_plot_no_data_jq").show();
			$(".psm_count_vs_score_qc_plot_have_data_jq").hide();
			$(".psm_count_vs_score_qc_plot_param_not_a_number_jq").hide();
			return;  //  EARLY EXIT
		}

		if ( ! thumbnailChart ) {
			var $psm_count_vs_score_qc_plot_overlay_container = $("#psm_count_vs_score_qc_plot_overlay_container");
			var $psm_count_vs_score_qc_plot_overlay_background = $("#psm_count_vs_score_qc_plot_overlay_background"); 
			$psm_count_vs_score_qc_plot_overlay_background.show();
			$psm_count_vs_score_qc_plot_overlay_container.show();
		}

//		<xs:simpleType name="filter_direction_type">
//		The direction a filterable annotation type is sorted in.  
//		If set to "below", attributes with lower values are considered more significant (such as in the case of p-values). 
//		If set to "above", attributes with higher values are considered more significant (such as in the case of XCorr).
		var comparisonDirectionString = "???";
		if ( chartDataParam.sortDirectionAbove ) {
			comparisonDirectionString = PSM_COUNT_VS_SCORE_CHART_COMPARISON_DIRECTION_STRING_ABOVE;
		} else if ( chartDataParam.sortDirectionBelow ) {
			comparisonDirectionString = PSM_COUNT_VS_SCORE_CHART_COMPARISON_DIRECTION_STRING_BELOW;
		} else {
			throw "sortDirectionBelow or sortDirectionAbove must be true";
		}
		var displayAsPercentage = false;
		if ( $("#psm_count_vs_score_qc_plot_y_axis_as_percentage").prop("checked") ) {
			displayAsPercentage = true;
		}
		//  chart data for Google charts
		var chartData = [];

		var lineColors = [];

		var chartDataHeaderEntry = [ selectedAnnotationTypeText ];
		if ( alllinkChartData ) {
			chartDataHeaderEntry.push( "all" );
			chartDataHeaderEntry.push( {role: "tooltip", 'p': {'html': true} }  ); 
			lineColors.push( _OVERALL_GLOBALS.BAR_COLOR_ALL_COMBINED );	//	combined: #a5a5a5 (gray)
//			updateMaxPSMCountForType( alllinkChartData );
		}
		if ( crosslinkChartData ) {
			chartDataHeaderEntry.push( "crosslink" );
			chartDataHeaderEntry.push( {role: "tooltip", 'p': {'html': true} }  ); 
			lineColors.push( _OVERALL_GLOBALS.BAR_COLOR_CROSSLINK );	//	red: #A55353
//			updateMaxPSMCountForType( crosslinkChartData );
		}
		if ( looplinkChartData ) {
			chartDataHeaderEntry.push( "looplink" );
			chartDataHeaderEntry.push( {role: "tooltip", 'p': {'html': true} }  ); 
			lineColors.push( _OVERALL_GLOBALS.BAR_COLOR_LOOPLINK );	//	green: #53a553
//			updateMaxPSMCountForType( looplinkChartData );
		}
		if ( unlinkedChartData ) {
			chartDataHeaderEntry.push( "unlinked" );
			chartDataHeaderEntry.push( {role: "tooltip", 'p': {'html': true} }  ); 
			lineColors.push( _OVERALL_GLOBALS.BAR_COLOR_UNLINKED );	//	blue: #5353a5
//			updateMaxPSMCountForType( unlinkedChartData );
		}
		chartData.push( chartDataHeaderEntry );
		var processBucketForType = function( params ) {
			var dataForType = params.dataForType;
			var linkTypeLabel = params.linkTypeLabel;
			if ( dataForType ) {
				var bucket = dataForType.chartBuckets[ index ];
				if ( chartDataEntry.length === 0 ) {
					// Add position to chartDataEntry array if chartDataEntry is empty
					if ( chartDataParam.sortDirectionAbove ) {
						chartDataEntry.push( bucket.binStart );	
					} else if ( chartDataParam.sortDirectionBelow ) {
						chartDataEntry.push( bucket.binEnd );	
					}
				}
				var chartDataValue = bucket.totalCount;
				if ( displayAsPercentage ) {
					chartDataValue = chartDataValue / dataForType.totalCountForType * 100;
				}
				chartDataEntry.push( chartDataValue );
				//  For "above" display the left edge of the bucket, otherwise the right edge
				var bucketStartOrEndForDisplayNumber; 
				if ( chartDataParam.sortDirectionAbove ) {
					bucketStartOrEndForDisplayNumber = bucket.binStart;
				} else {
					bucketStartOrEndForDisplayNumber = bucket.binEnd;
				}			
				var bucketStartOrEndForDisplayNumberRounded = bucketStartOrEndForDisplayNumber.toFixed( PSM_COUNT_VS_SCORE_CHART_BIN_START_OR_END_TO_FIXED_VALUE );
				var rawCount = bucket.totalCount;
				try {
					rawCount = rawCount.toLocaleString();
				} catch (e) {
				}
				var tooltip = "<div style='margin: 10px;'>Type: " + linkTypeLabel +
				"<br>Raw count: " + rawCount;
				if ( displayAsPercentage ) {
					var chartDataValueRounded = chartDataValue.toFixed( PSM_COUNT_VS_SCORE_CHART_PERCENTAGE_OF_MAX_TO_FIXED_VALUE );
					tooltip += "<br>Percent of Max: " + chartDataValueRounded; 
				}
				tooltip += "<br>" + selectedAnnotationTypeText + " " + comparisonDirectionString + " " + bucketStartOrEndForDisplayNumberRounded + "</div>";
				chartDataEntry.push( tooltip );
			}
		};
		for ( var index = 0; index < dataArraySize; index++ ) {
			var chartDataEntry = [];
			processBucketForType( { dataForType: alllinkChartData, linkTypeLabel: "all" } );
			processBucketForType( { dataForType: crosslinkChartData, linkTypeLabel: "crosslink" }  );
			processBucketForType( { dataForType: looplinkChartData, linkTypeLabel: "looplink" }  );
			processBucketForType( { dataForType: unlinkedChartData, linkTypeLabel: "unlinked" }  );
			if ( chartDataEntry && chartDataEntry.length > 0 ) {
				chartData.push( chartDataEntry );
			}
		}
		var chartTitle = "Cumulative PSM Count vs " + selectedAnnotationTypeText;
		var yAxisLabel = "# PSM " + comparisonDirectionString + " " + selectedAnnotationTypeText;
		if ( displayAsPercentage ) {
			yAxisLabel = "# PSM " + comparisonDirectionString + " " + selectedAnnotationTypeText + " (% of max)";
		}
		
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
		
		var optionsFullsize = {
				title: chartTitle, // Title above chart
				//  X axis label below chart
				hAxis: { title: selectedAnnotationTypeText, titleTextStyle: {color: 'black'}
//					,ticks: [ 0.0, 0.2, 0.4, 0.6, 0.8, 1.0 ], format:'#.##'
//					,minValue: -.05
//					,maxValue : maxDataX
				},  
				//  Y axis label left of chart
				vAxis: { title: yAxisLabel, titleTextStyle: {color: 'black'}
					,baseline: 0                    // always start at zero
//					,ticks: vAxisTicks, format:'#,###'
//					,maxValue : 10
				},
//				legend: { position: 'none' }, //  position: 'none':  Don't show legend of bar colors in upper right corner
				legend: legendConfig,

				colors: lineColors,  //  Color of lines
				tooltip: {isHtml: true},
				chartArea : chartChartArea
		};        
		if ( userInputMaxY ) {
			//  Data points with Y axis > userInputMaxY will not be shown
			optionsFullsize.vAxis.viewWindow = { max : userInputMaxY };
		}
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

		chartFullsize.draw( data, optionsFullsize );
		
//		google.visualization.events.addListener( chartFullsize, 'select', function() {
//			var tableSelection = chartFullsize.getSelection();
//			var tableSelection0 = tableSelection[ 0 ];
//			var column = tableSelection0.column;
//			var row = tableSelection0.row;
//			var chartDataForRow = chartData[ row ];
//			var z = 0;
//		});
		
	};

///////////
	this.psmCountVsScoreQCPlot_resetMaxXMaxY = function() {
//		var objectThis = this;
		var $psm_count_vs_score_qc_plot_max_x = $("#psm_count_vs_score_qc_plot_max_x");
		var $psm_count_vs_score_qc_plot_max_y = $("#psm_count_vs_score_qc_plot_max_y");
		$psm_count_vs_score_qc_plot_max_x.val( "" );
		$psm_count_vs_score_qc_plot_max_y.val( "" );
	};


	this.psmCountVsScoreQCPlot_downloadData = function( params ) {
		var objectThis = this;
		var thumbnailChart = false;
		var selectedAnnotationTypeText = undefined
		if ( params ) {
			if ( params.thumbnailChart ) {
				thumbnailChart = true;
			}
			if ( params.selectedAnnotationTypeText ) {
				selectedAnnotationTypeText = params.selectedAnnotationTypeText;
			}
		}
		try {
			
			var chartDataFromServer = undefined;
			//		Count Label
			var displayAsPercentage = false;
			var countLabel = "Raw count";

			if ( thumbnailChart ) {
			
				if ( this.globals_PSMCountVsScoresChart === undefined || this.globals_PSMCountVsScoresChart.chartDataFromServer_Thumbnail === undefined ) {
					//  No stored chart data from server so exit
					return;  //  EARLY EXIT
				}
				
				chartDataFromServer = this.globals_PSMCountVsScoresChart.chartDataFromServer_Thumbnail;
			
			} else {
				if ( this.globals_PSMCountVsScoresChart === undefined || this.globals_PSMCountVsScoresChart.chartDataFromServer_FullSize_Overlay === undefined ) {
					//  No stored chart data from server so exit
					return;  //  EARLY EXIT
				}
				
				chartDataFromServer = this.globals_PSMCountVsScoresChart.chartDataFromServer_FullSize_Overlay;

				if ( $("#psm_count_vs_score_qc_plot_y_axis_as_percentage").prop("checked") ) {
					displayAsPercentage = true;
				}
				if ( displayAsPercentage ) {
					countLabel = "Percent of Max"; 
				}

				//  Selected Annotation Text
				if ( ! selectedAnnotationTypeText ) {
					selectedAnnotationTypeText = $("#psm_count_vs_score_qc_plot_score_type_id option:selected").text();
				}

			}
			
			var dataArraySize = chartDataFromServer.dataArraySize;
			var crosslinkChartData = chartDataFromServer.crosslinkData;
			var looplinkChartData = chartDataFromServer.looplinkData;
			var unlinkedChartData = chartDataFromServer.unlinkedData;
			var alllinkChartData = chartDataFromServer.alllinkData;

			//  Annotation direction
			var comparisonDirectionString = "???";
			if ( chartDataFromServer.sortDirectionAbove ) {
				comparisonDirectionString = PSM_COUNT_VS_SCORE_CHART_COMPARISON_DIRECTION_STRING_ABOVE_ASCII;
			} else if ( chartDataFromServer.sortDirectionBelow ) {
				comparisonDirectionString = PSM_COUNT_VS_SCORE_CHART_COMPARISON_DIRECTION_STRING_BELOW_ASCII;
			} else {
				throw "sortDirectionBelow or sortDirectionAbove must be true";
			}


			var headerLine = "type\t" + countLabel + "\t" + selectedAnnotationTypeText + " " + comparisonDirectionString ;

			var outputStringArray = new Array();

			outputStringArray.push( headerLine );

			var processForType = function( params ) {

				var dataForType = params.dataForType;
				var linkTypeLabel = params.linkTypeLabel;

				for ( var index = 0; index < dataArraySize; index++ ) {

					var bucket = dataForType.chartBuckets[ index ];

					var outputType = linkTypeLabel;

					var outputCount = undefined;
					var outputAnnTypeValue = undefined;

					var outputCount = bucket.totalCount;
					if ( displayAsPercentage ) {
						outputCount = ( outputCount / dataForType.totalCountForType * 100 ).toFixed( PSM_COUNT_VS_SCORE_CHART_PERCENTAGE_OF_MAX_TO_FIXED_VALUE_OUTPUT_FILE );
					}
					//  For "above" display the left edge of the bucket, otherwise the right edge
					var bucketStartOrEndForDisplayNumber; 
					if ( chartDataFromServer.sortDirectionAbove ) {
						bucketStartOrEndForDisplayNumber = bucket.binStart;
					} else {
						bucketStartOrEndForDisplayNumber = bucket.binEnd;
					}			
					var outputAnnTypeValue = bucketStartOrEndForDisplayNumber.toFixed( PSM_COUNT_VS_SCORE_CHART_BIN_START_OR_END_TO_FIXED_VALUE );

					var outputLine = outputType + "\t" + outputCount + "\t" + outputAnnTypeValue ;

					outputStringArray.push( outputLine );
				}
			};


			if ( crosslinkChartData ) {
				processForType( { dataForType: crosslinkChartData, linkTypeLabel: "crosslink" }  );
			}
			if ( looplinkChartData ) {
				processForType( { dataForType: looplinkChartData, linkTypeLabel: "looplink" }  );
			}
			if ( unlinkedChartData ) {
				processForType( { dataForType: unlinkedChartData, linkTypeLabel: "unlinked" }  );
			}
			if ( alllinkChartData ) {
				processForType( { dataForType: alllinkChartData, linkTypeLabel: "all" } );
			}

			var content = outputStringArray.join("\n");

			var filename = "psmCountData.txt"
				var mimetype = "text/plain";

			downloadStringAsFile( filename, mimetype, content )

		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}

	};
	
};

/**
 * page variable 
 */

var qcPageChart_PSM_Count_Vs_Score_PSM = new QCPageChart_PSM_Count_Vs_Score_PSM();
