/**
 * qcPageSectionSummaryStatistics.js
 * 
 * Javascript for the viewQC.jsp page - Section Summary Statistics
 * 
 * page variable qcPageSectionSummaryStatistics
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


const reportWebErrorToServer = window.reportWebErrorToServer;



import { qcPageChartSummaryStatistics } from './qcPageChartSummaryStatistics.js';


/**
 * Constructor 
 */
var QCPageSectionSummaryStatistics = function() {

	//  objects for the charts on the page for this section
	var _pageChartObjectsForSection = [
		qcPageChartSummaryStatistics
	];

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

	//  Copy references to qcPageMain functions to here
	this._passAJAXErrorTo_handleAJAXError = undefined;
	this._addChartOuterTemplate = undefined;
	this._addChartInnerTemplate = undefined;
	this._placeEmptyDummyChartForMessage = undefined;
	this.getColorAndBarColorFromLinkType = undefined;

	var _get_hash_json_Contents = undefined; // function on qcPageMain

	
	///////////
	
	//   Variables for this section

	var _sectionDisplayed = false;

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

			
			this.addClickAndOnChangeHandlers();


			_pageChartObjectsForSection.forEach( function( pageChartObjectForSection, index, array ) {
				pageChartObjectForSection.initActual( params );
			}, this );

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

		var $summary_expand_link = $("#summary_expand_link");
		$summary_expand_link.click( function( event ) { 
			try {
				objectThis._section_expand_link_Clicked( { clickedThis : this } ); 
				event.preventDefault();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});

		var $summary_collapse_link = $("#summary_collapse_link");
		$summary_collapse_link.click( function( event ) { 
			try {
				objectThis._section_collapse_link_Clicked( { clickedThis : this } ); 
				event.preventDefault();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
	};

	/**
	 * 
	 */
	this._section_expand_link_Clicked = function() {
		this.show_Section();
	};

	/**
	 *  
	 */
	this._section_collapse_link_Clicked = function() {
		this.hide_Section();
	};

	/**
	 *  Special function for calling from qcPageMain.js on page init to display this section automatically on page display
	 */
	this.show_Section_From_qcPageMain = function() {
		if ( ! _pageChartObjectsForSection ) {
			//  _pageChartObjectsForSection not yet populated so exit
			return;
		}
		this.show_Section();
	};
	
	/**
	 *  
	 */
	this.show_Section = function() {

		_sectionDisplayed = true;

		var $summary_display_block = $("#summary_display_block");
		$summary_display_block.show();
		var $summary_expand_link = $("#summary_expand_link");
		$summary_expand_link.hide();
		var $summary_collapse_link = $("#summary_collapse_link");
		$summary_collapse_link.show();

		this.loadSectionIfNeeded();
	};

	/**
	 *  
	 */
	this.hide_Section = function() {

		var $summary_display_block = $("#summary_display_block");
		$summary_display_block.hide();
		var $summary_expand_link = $("#summary_expand_link");
		$summary_expand_link.show();
		var $summary_collapse_link = $("#summary_collapse_link");
		$summary_collapse_link.hide();

		_sectionDisplayed = false;

		_pageChartObjectsForSection.forEach( function( pageChartObjectForSection, index, array ) {
			pageChartObjectForSection.sectionHidden();
		}, this );
	};

	/**
	 * Load the data for the section, if this section is show
	 */
	this.loadSectionIfNeededIfShown = function() {
		if ( _sectionDisplayed ) {
			this.loadSectionIfNeeded();
		}
	};
	
	/**
	 * Load the data for the section
	 */
	this.loadSectionIfNeeded = function() {
		_pageChartObjectsForSection.forEach( function( pageChartObjectForSection, index, array ) {
			pageChartObjectForSection.loadChartIfNeeded();
		}, this );
	};

	/**
	 * Clear the data for the section
	 */
	this.clearSection = function() {
		_pageChartObjectsForSection.forEach( function( pageChartObjectForSection, index, array ) {
			pageChartObjectForSection.clearChart();
		}, this );
	};
};

/**
 * page variable 
 */

var qcPageSectionSummaryStatistics = new QCPageSectionSummaryStatistics();

export { qcPageSectionSummaryStatistics }
