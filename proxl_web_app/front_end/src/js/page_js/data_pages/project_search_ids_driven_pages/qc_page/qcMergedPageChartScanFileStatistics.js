/**
 * qcMergedPageChartScanFileStatistics.js
 * 
 * Javascript for the viewQCMerged.jsp page - Chart Ion Current Statistics
 * 
 * page variable qcMergedPageChartScanFileStatistics
 * 
 * Merged QC Page
 * 
 * This code has been updated to cancel existing active AJAX calls when "Update from Database" button is clicked.
 *   This is done so that previous AJAX responses don't overlay new AJAX responses.
 */

//JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

//  require full Handlebars since compiling templates
const Handlebars = require('handlebars');


import { webserviceCallStandardPost } from 'page_js/webservice_call_common/webserviceCallStandardPost.js';

import { qcChartDownloadHelp } from './qcChart_Download_Help_HTMLBlock.js';


/**
 * Constructor 
 */
var QCMergedPageChartScanFileStatistics = function() {


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
	var _searchIdsObject_Key_projectSearchId = undefined;
	
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
	 * Init page Actual - Called from qcPageMain.initActual
	 */
	this.initActual = function( params ) {
		try {
			var objectThis = this;

			_OVERALL_GLOBALS = params.OVERALL_GLOBALS;

			_project_search_ids = params.project_search_ids;
			_searchIdsObject_Key_projectSearchId = params.searchIdsObject_Key_projectSearchId;
			
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
	 * Add Click and onChange handlers   - called from this.initActual(...)
	 */
	this.addClickAndOnChangeHandlers = function() {
		var objectThis = this;

	};

	/**
	 * Called when the selection criteria at the top of the page has changed and user has clicked "Update from Database".
	 *   The current chart(s) and it's data are no longer applicable and can be deleted and removed.
	 * 
	 * Clear data 
	 */
	this.clearChart = function() {

		_loadChartIfNeeded_PreviouslyCalled = false;
		_chartsData = undefined;;
		_chartsDisplayed = false;

		var $scan_file_files_loading_block = $("#scan_file_files_loading_block");
		$scan_file_files_loading_block.show();
		
		var $scan_file_no_files_block = $("#scan_file_no_files_block");
		$scan_file_no_files_block.hide();

		var $scan_file_selected_file_statistics_display_block = $("#scan_file_selected_file_statistics_display_block");
		$scan_file_selected_file_statistics_display_block.hide();
		
		var $scan_file_overall_statistics_block = $("#scan_file_overall_statistics_block");
		$scan_file_overall_statistics_block.empty();

		if ( _activeAjax ) {
			_activeAjax.abort();
			_activeAjax = null;
		}
	};

	/**
	 * Called when the section is hidden
	 * 
	 * Clear data for Summary_Statistics_Counts
	 */
	this.sectionHidden = function() {

		_sectionVisible = false;
	}

	let _sectionVisible = false; //  When true, the section is visible

	let _loadChartIfNeeded_PreviouslyCalled = false; // Also false if this.clearChart() called

	//  Single variable since single AJAX call
	let _chartsData = undefined;

	//  Single variable since all created at once
	let _chartsDisplayed = false; // Also false if this.clearChart() called


	/**
	 * This Method should be called 'createChartIfNeeded'.  Also when called, the section is shown.
	 * 
	 * Called when the section on the page that this chart is in is opened (or on page load for section open on page load)
	 * 
	 */
	this.loadChartIfNeeded = function() {

		_sectionVisible = true;

		if ( ! _chartsDisplayed ) {
			if ( ! _chartsData ) {
				if ( ! _loadChartIfNeeded_PreviouslyCalled ) {
					// loadChartIfNeeded not previously called so start at the top

					_loadChartIfNeeded_PreviouslyCalled = true;

					const promise_getData_FromServer = this.getData_FromServer();

					promise_getData_FromServer.catch( () => {} );
					promise_getData_FromServer.then( ({ responseData }) => {
						_chartsData = responseData;
						if ( ! _sectionVisible ) {
							//  Section no longer visible so skip creating the charts
							return; // EARLY EXIT
						}
						this.createCharts();
					});
				} else {
					//  Loading is already in progress so exit
					return; // EARLY RETURN
				}
			} else {
				//  Have charts data but not displayed so display the chart data
				this.createCharts();
			}
		} 
	};


	var _activeAjax = null;

	/**
	 * Load the data from the server
	 */
	this.getData_FromServer = function() {
		var objectThis = this;

		if ( _activeAjax ) {
			_activeAjax.abort();
			_activeAjax = null;
		}
		
		const hash_json_Contents = _get_hash_json_Contents();

		return new Promise( (resolve, reject) => {
			try {
				const ajaxRequestData = { projectSearchIds : _project_search_ids, qcPageQueryJSONRoot : hash_json_Contents };

				const url = "services/qc/dataPage/getScanStatistics_Merged";

				const webserviceCallStandardPostResult = webserviceCallStandardPost({ dataToSend : ajaxRequestData, url }); //  External Function

				const promise_webserviceCallStandardPost = webserviceCallStandardPostResult.promise; 
				_activeAjax = webserviceCallStandardPostResult.api;

				promise_webserviceCallStandardPost.catch( ( ) => {
					_activeAjax = null;
					reject();
			   } );

			   promise_webserviceCallStandardPost.then( ({ responseData }) => {
				   try {
					   _activeAjax = null;
					   resolve({ responseData });
			   
				   } catch( e ) {
					   reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					   throw e;
				   }
			   });
		   } catch( e ) {
			   reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			   throw e;
		   }
	   });
	};

	/**
	 * Create the charts
	 */
	this.createCharts = function() {

		var results = _chartsData.results;
		
		var $scan_file_files_loading_block = $("#scan_file_files_loading_block");
		$scan_file_files_loading_block.hide();
		
		
		if ( ! results.haveData ) {
			//  No data found
			
			var $scan_file_no_files_block = $("#scan_file_no_files_block");
			$scan_file_no_files_block.show();
			
			return;  //  EARLY EXIT
		}

		var $scan_file_selected_file_statistics_display_block = $("#scan_file_selected_file_statistics_display_block");
		$scan_file_selected_file_statistics_display_block.show();
		
		var dataPerSearchMap_KeyProjectSearchId = results.dataPerSearchMap_KeyProjectSearchId;
		
		var searchCountPlusOne = _project_search_ids.length + 1;
				
		//  Build array of objects for rendering with Handlebars template
		
		var NO_DATA_STRING = "No Data";
		
		var displayPerSearchList = [];

		_project_search_ids.forEach( function ( _project_search_ids_ArrayValue, index, array ) {
			
			var dataPerSearch = dataPerSearchMap_KeyProjectSearchId[ _project_search_ids_ArrayValue ];
			
			//  Process data for each searchId
			
			var ms_2_ScanCount = 0;
			var totalIonCurrentString = NO_DATA_STRING;
			var ms_1_ScanIntensitiesSummedString = NO_DATA_STRING;
			var ms_2_ScanIntensitiesSummedString = NO_DATA_STRING;

			var ms_1_ScanCountString = NO_DATA_STRING;
			var ms_2_ScanCountString = NO_DATA_STRING;
				
			if ( dataPerSearch.haveSscanOverallData ) {

				ms_2_ScanCount = dataPerSearch.ms_2_ScanCount;

				var totalIonCurrent = dataPerSearch.ms_1_ScanIntensitiesSummed + dataPerSearch.ms_2_ScanIntensitiesSummed;

				totalIonCurrentString = totalIonCurrent;
				ms_1_ScanIntensitiesSummedString = dataPerSearch.ms_1_ScanIntensitiesSummed;
				ms_2_ScanIntensitiesSummedString = dataPerSearch.ms_2_ScanIntensitiesSummed;
				try {
					totalIonCurrentString = totalIonCurrent.toExponential( 3 );
					ms_1_ScanIntensitiesSummedString = dataPerSearch.ms_1_ScanIntensitiesSummed.toExponential( 3 );
					ms_2_ScanIntensitiesSummedString = dataPerSearch.ms_2_ScanIntensitiesSummed.toExponential( 3 );
				} catch( e ) {
					try {
						totalIonCurrentString = totalIonCurrent.toExponential();
						ms_1_ScanIntensitiesSummedString = dataPerSearch.ms_1_ScanIntensitiesSummed.toExponential();
						ms_2_ScanIntensitiesSummedString = dataPerSearch.ms_2_ScanIntensitiesSummed.toExponential();
					} catch( e2 ) {
					}
				}

				ms_1_ScanCountString = dataPerSearch.ms_1_ScanCount;
				ms_2_ScanCountString = dataPerSearch.ms_2_ScanCount;
				try {
					ms_1_ScanCountString = dataPerSearch.ms_1_ScanCount.toLocaleString();
					ms_2_ScanCountString = dataPerSearch.ms_2_ScanCount.toLocaleString();
				} catch( e ) {
				}
				
			}
			
			// "MS2 scans with a PSM meeting cutoffs" section
			
			var combinedLinkTypes_MS_2_scansMeetsCutoffs = dataPerSearch.crosslinkCount + dataPerSearch.looplinkCount + dataPerSearch.unlinkedCount;

			var crosslinkCountString = dataPerSearch.crosslinkCount;
			var looplinkCountString = dataPerSearch.looplinkCount;
			var unlinkedCountString = dataPerSearch.unlinkedCount;
			var totalMS2CountForFilterString = combinedLinkTypes_MS_2_scansMeetsCutoffs;
			try {
				crosslinkCountString = dataPerSearch.crosslinkCount.toLocaleString();
				looplinkCountString = dataPerSearch.looplinkCount.toLocaleString();
				unlinkedCountString = dataPerSearch.unlinkedCount.toLocaleString();
				totalMS2CountForFilterString = combinedLinkTypes_MS_2_scansMeetsCutoffs.toLocaleString();
			} catch( e ) {
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
					var crosslinkCountFraction = dataPerSearch.crosslinkCount / ms_2_ScanCount;
					var looplinkCountFraction = dataPerSearch.looplinkCount / ms_2_ScanCount;
					var unlinkedCountFraction = dataPerSearch.unlinkedCount / ms_2_ScanCount;
					var totalMS2CountForFilterFraction = combinedLinkTypes_MS_2_scansMeetsCutoffs / ms_2_ScanCount;

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

			var displayForSearch = {
					
					searchId : dataPerSearch.searchId,
					totalIonCurrent : totalIonCurrentString,
					total_MS_1_IonCurrent : ms_1_ScanIntensitiesSummedString,
					total_MS_2_IonCurrent : ms_2_ScanIntensitiesSummedString,
					number_MS_1_scans : ms_1_ScanCountString,
					number_MS_2_scans : ms_2_ScanCountString,
					
					crosslink_MS_2_scansMeetsCutoffsDisplay : crosslinkCountDisplay,
					looplink_MS_2_scansMeetsCutoffsDisplay : looplinkCountDisplay,
					unlinked_MS_2_scansMeetsCutoffsDisplay : unlinkedCountDisplay,
					combinedLinkTypes_MS_2_scansMeetsCutoffsDisplay : totalMS2CountForFilterDisplay
					
			};
			
			displayPerSearchList.push( displayForSearch );

			
		}, this )
		
		var $scan_file_overall_statistics_template = $("#scan_file_overall_statistics_template");
		if ( $scan_file_overall_statistics_template.length === 0 ) {
			throw Error( "HTML element not found for id 'scan_file_overall_statistics_template'" );
		}
		var handlebarsSource_scan_file_overall_statistics_template = $scan_file_overall_statistics_template.html();
		
		var _handlebarsTemplate_scan_file_overall_statistics_template = Handlebars.compile( handlebarsSource_scan_file_overall_statistics_template );

		var $scan_file_overall_statistics_block = $("#scan_file_overall_statistics_block");
		
		var dataForTable = { perSearchDataList : displayPerSearchList, searchCountPlusOne : searchCountPlusOne };
		
		$scan_file_overall_statistics_block.empty();
		
		var html = _handlebarsTemplate_scan_file_overall_statistics_template( dataForTable );
//		var $psm_block_template = 
		$( html ).appendTo( $scan_file_overall_statistics_block ); 

		
		
		var $scan_file_overall_statistics_loading_block = $("#scan_file_overall_statistics_loading_block");
		$scan_file_overall_statistics_loading_block.hide();
		
		$scan_file_overall_statistics_block.show();


		_chartsDisplayed = true;
	};
	
};

/**
 * page variable 
 */

var qcMergedPageChartScanFileStatistics = new QCMergedPageChartScanFileStatistics();

export { qcMergedPageChartScanFileStatistics }
