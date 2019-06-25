/**
 * qcMergedPageMain.js
 * 
 * Javascript for the viewQCMerged.jsp page
 * 
 * page variable qcMergedPageMain
 * 
 * Merged QC Page  
 * 
 */

//JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

//Import header_main.js and children to ensure on the page
import { header_mainVariable } from 'page_js/header_section_js_all_pages_main_pages/header_section_main_pages/header_main.js';


import { loadGoogleChart_CoreChart }  from 'page_js/data_pages/data_pages_common/googleChartLoaderForThisWebapp.js';

import { copyObject_DeepCopy_Proxl } from 'page_js/common_js_includes_all_pages/copyObject_DeepCopy.js';


//  Import to make available on the page
import { searchesChangeDisplayOrder } from 'page_js/data_pages/project_search_ids_driven_pages/common/searchesChangeDisplayOrder.js';
import { searchesForPageChooser } from 'page_js/data_pages/project_search_ids_driven_pages/common/searchesForPageChooser.js';
import { sharePageURLShortener  } from 'page_js/data_pages/project_search_ids_driven_pages/common/sharePageURLShortener.js';


import { DataPages_LoggedInUser_CommonObjectsFactory } from 'page_js/data_pages/data_pages_common/dataPages_LoggedInUser_CommonObjectsFactory.js';


import { webserviceDataParamsDistributionCommonCode } from 'page_js/data_pages/project_search_ids_driven_pages/common/webserviceDataParamsDistribution.js';
import { annotationDataDisplayProcessingCommonCode } from 'page_js/data_pages/project_search_ids_driven_pages/common/psmPeptideAnnDisplayDataCommon.js';
import { cutoffProcessingCommonCode } from 'page_js/data_pages/project_search_ids_driven_pages/common/psmPeptideCutoffsCommon.js';


import { qc_pages_Single_Merged_Common } from './qc_pages_Single_Merged_Common.js';

import { qcMergedPageSection_Peptide_Level_Statistics } from './qcMergedPageSection_Peptide_Level_Statistics.js';
import { qcMergedPageSection_PSM_Error_Estimates } from './qcMergedPageSection_PSM_Error_Estimates.js';
import { qcMergedPageSection_PSM_Level_Statistics } from './qcMergedPageSection_PSM_Level_Statistics.js';
import { qcMergedPageSectionDigestionStatistics } from './qcMergedPageSectionDigestionStatistics.js';
import { qcMergedPageSectionModificationStatistics } from './qcMergedPageSectionModificationStatistics.js';
import { qcMergedPageSectionScanFileStatistics } from './qcMergedPageSectionScanFileStatistics.js';
import { qcMergedPageSectionSummaryStatistics } from './qcMergedPageSectionSummaryStatistics.js';

/**
 * Constructor 
 */
var QCMergedPageMain = function() {

	//  objects for the sections on the page
	var _pageSectionObjects = [
		qcMergedPageSectionSummaryStatistics,
		qcMergedPageSectionDigestionStatistics,
		qcMergedPageSectionScanFileStatistics,
		qcMergedPageSection_PSM_Level_Statistics,
		qcMergedPageSection_PSM_Error_Estimates,
		qcMergedPageSectionModificationStatistics,
		qcMergedPageSection_Peptide_Level_Statistics
		];
	
	var _OVERALL_GLOBALS = { 
			//  Color of bars:  from head_section_include_every_page.jsp
			BAR_COLOR_CROSSLINK : _PROXL_COLOR_LINK_TYPE_CROSSLINK, // '#A55353', // Crosslink: Proxl shades of red
			BAR_COLOR_LOOPLINK : _PROXL_COLOR_LINK_TYPE_LOOPLINK, // '#53a553',  // Looplink: green: #53a553
			BAR_COLOR_UNLINKED : _PROXL_COLOR_LINK_TYPE_UNLINKED, // '#5353a5'   //	Unlinked: blue: #5353a5
			BAR_COLOR_ALL_COMBINED : _PROXL_COLOR_LINK_TYPE_ALL_COMBINED,  //  All Combined  Grey  #A5A5A5
			BAR_COLOR_RED : _PROXL_COLOR_SITE_RED,
			BAR_COLOR_PINK : _PROXL_COLOR_SITE_PINK
	};

	_OVERALL_GLOBALS.BAR_STYLE_CROSSLINK = 
		"color: " + _OVERALL_GLOBALS.BAR_COLOR_CROSSLINK +
		"; stroke-color: " + _OVERALL_GLOBALS.BAR_COLOR_CROSSLINK + 
		"; stroke-width: 1; fill-color: " + _OVERALL_GLOBALS.BAR_COLOR_CROSSLINK + ";";

	_OVERALL_GLOBALS.BAR_STYLE_LOOPLINK = 
		"color: " + _OVERALL_GLOBALS.BAR_COLOR_LOOPLINK +
		"; stroke-color: " + _OVERALL_GLOBALS.BAR_COLOR_LOOPLINK + 
		"; stroke-width: 1; fill-color: " + _OVERALL_GLOBALS.BAR_COLOR_LOOPLINK + ";";

	_OVERALL_GLOBALS.BAR_STYLE_UNLINKED = 
		"color: " + _OVERALL_GLOBALS.BAR_COLOR_UNLINKED +
		"; stroke-color: " + _OVERALL_GLOBALS.BAR_COLOR_UNLINKED + 
		"; stroke-width: 1; fill-color: " + _OVERALL_GLOBALS.BAR_COLOR_UNLINKED + ";";

	_OVERALL_GLOBALS.BAR_STYLE_ALL_COMBINED = 
		"color: " + _OVERALL_GLOBALS.BAR_COLOR_ALL_COMBINED +
		"; stroke-color: " + _OVERALL_GLOBALS.BAR_COLOR_ALL_COMBINED + 
		"; stroke-width: 1; fill-color: " + _OVERALL_GLOBALS.BAR_COLOR_ALL_COMBINED + ";";


	_OVERALL_GLOBALS.BAR_STYLE_RED = 
		"color: " + _OVERALL_GLOBALS.BAR_COLOR_RED +
		"; stroke-color: " + _OVERALL_GLOBALS.BAR_COLOR_RED + 
		"; stroke-width: 1; fill-color: " + _OVERALL_GLOBALS.BAR_COLOR_RED + ";";

	_OVERALL_GLOBALS.BAR_STYLE_PINK = 
		"color: " + _OVERALL_GLOBALS.BAR_COLOR_PINK +
		"; stroke-color: " + _OVERALL_GLOBALS.BAR_COLOR_PINK + 
		"; stroke-width: 1; fill-color: " + _OVERALL_GLOBALS.BAR_COLOR_PINK + ";";


	const dataPages_LoggedInUser_CommonObjectsFactory = new DataPages_LoggedInUser_CommonObjectsFactory();

	const saveView_dataPages = dataPages_LoggedInUser_CommonObjectsFactory.instantiate_SaveView_dataPages();


	//   General
	
	var _project_search_ids = null;
	
	var _searchIdsObject_Key_projectSearchId = null;
	
	var _userOrderedProjectSearchIds = null;


	let _defaultsCutoffsAndOthers = null;  // Passed from server code via DOM element JSOn

	
	var _colorsPerSearch = null;


	//////////////////////

	///  Has URL contents

	var _hash_json_Contents = null;
	//   Added properties:
	//		includeProteinSeqVIdsDecodedArray

	//  Properties added to _hash_json_Contents are placed here to strip them out when create JSON to put back on URL
	const _hash_json_Contents_PROPERTY_ADDITIONS = [ 'includeProteinSeqVIdsDecodedArray' ];

	//  Properties On Hash in _hash_json_Contents are only for URL.  They are not passed to webservices
	const _hash_json_Contents_PROPERTY_URL_ONLY = [ 'inclProtSeqVIds', 'inclProtSeqVIdsAllSelected' ];

	////////////////////
	
	var _anySearchesHaveScanDataYes = false;

	//  Contains {{link_type}} to replace with link type.  Contains {{link_type}}_chart_outer_container_jq chart_outer_container_jq
	var _common_chart_outer_entry_templateHTML = null;

	var _common_chart_inner_entry_templateHTML = null;

	var _dummy_chart_entry_for_message_templateHTML = null;


	var _link_type_crosslink_constant = null;
	var _link_type_looplink_constant = null;
	var _link_type_unlinked_constant = null;
	var _link_type_default_selected = null;

	var _link_type_crosslink_LOWER_CASE_constant = null;
	var _link_type_looplink_LOWER_CASE_constant = null;
	var _link_type_unlinked_LOWER_CASE_constant = null;

	var _link_type_combined_LOWER_CASE_constant = "combined";

	//   These will have the link type added in between prefix and suffix, adding a space after link type.
	//       There is no space at start of suffix to support no link type
	var _DUMMY_CHART_STATUS_TEXT_PREFIX_LOADING = "Loading ";
	var _DUMMY_CHART_STATUS_TEXT_SUFFIX_LOADING = "Data";
	var _DUMMY_CHART_STATUS_TEXT_PREFIX_NO_DATA = "No ";
	var _DUMMY_CHART_STATUS_TEXT_SUFFIX_NO_DATA = "Data Found";
	var _DUMMY_CHART_STATUS_TEXT_PREFIX_ERROR_LOADING = "Error Loading ";
	var _DUMMY_CHART_STATUS_TEXT_SUFFIX_ERROR_LOADING = "Data";

	var _DUMMY_CHART_STATUS_WHOLE_TEXT_SCANS_NOT_UPLOADED = "Scans Not Uploaded";


	var _IS_LOADED_YES = "YES";
	var _IS_LOADED_NO = "NO";
	var _IS_LOADED_LOADING = "LOADING";
	
	/**
	 * used by other classes to get _hash_json_Contents, returns a copy for safety
	 */
	this._get_hash_json_Contents = function() {

		//  Make a copy of hash_json_Contents    (  true for deep, target object, source object, <source object 2>, ... )
		var hash_json_Contents_COPY = $.extend( true /*deep*/,  {}, _hash_json_Contents );
		
		//  Delete encoded data, for URL only
		for ( const propertyNameToRemove of _hash_json_Contents_PROPERTY_URL_ONLY ) {
			delete hash_json_Contents_COPY[ propertyNameToRemove ];
		}
		
		return hash_json_Contents_COPY;
	};
	
	/**
	 * Should this AJAX Error be passed to handleAJAXError()?
	 * Check textStatus in AJAX error for 'abort'.  If abort, return false
	 */
	this._passAJAXErrorTo_handleAJAXError = function( jqXHR, textStatus, errorThrown ) {
		if ( textStatus === 'abort' ) {
			return false;
		}
		return true;
	};


	/**
	 * Init page.  Called after Google Charts have loaded.  Called in header_main.jsp 
	 */
	this.init = function() {

		//  Load Google Charts API and then call this.initActual();

		const loadGoogleChart_CoreChartResult = loadGoogleChart_CoreChart();

		if ( ! loadGoogleChart_CoreChartResult.isLoaded ) {
			loadGoogleChart_CoreChartResult.loadingPromise.then( (value) => { // On Fulfilled
				try {
					this.initActual();
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			}).catch(function(reason) {
				try {
					console.log( "loadGoogleChart_CoreChartResult.loadingPromise.catch(reason) called" );
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			});
		} else {
			this.initActual();
		}
	};
	
	/**
	 * Init page Actual 
	 */
	this.initActual = function() {
		try {
			var objectThis = this;

			this.populateConstantsFromPage();

			this.getDefaultsFromPage();  //  Get the defaults out of the DOM placed by the Server Side Code and store in Javascript variables 

			this.updatePageFiltersFromURLHash();

			//  Get Project Search Ids from Page

			_project_search_ids = [];
			var $project_search_id_jq_List = $(".project_search_id_jq");
			if ( $project_search_id_jq_List.length === 0 ) {
				throw "input fields with class 'project_search_id_jq' containing project search ids is missing from the page";
			}
			$project_search_id_jq_List.each( function( index, element ) {
				var project_search_id = $( this ).val();
				//  Convert all attributes to empty string if null or undefined
				if ( ! project_search_id ) {
					project_search_id = "";
				}
				_project_search_ids.push( project_search_id );
			} );
			
			//  Get Project Search Id / Search Id Pairs from Page

			_searchIdsObject_Key_projectSearchId = {};
			var $project_search_id__search_id_pair_jq_List = $(".project_search_id__search_id_pair_jq");
			if ( $project_search_id__search_id_pair_jq_List.length === 0 ) {
				throw "input fields with class 'project_search_id__search_id_pair_jq' containing project search ids is missing from the page";
			}
			$project_search_id__search_id_pair_jq_List.each( function( index, element ) {
				var fieldContents = $( this ).val();
				if ( ! fieldContents || fieldContents === "" ) {
					throw Error( "element with class 'project_search_id__search_id_pair_jq' is empty or .val() returns null or undefined" );
				}
				var fieldContentsSplit = fieldContents.split( ":" );
				if ( fieldContentsSplit.length !== 2 ) {
					throw Error( "element with class 'project_search_id__search_id_pair_jq' does not contain ':'" );
				}
				var projectSearchId = fieldContentsSplit[ 0 ];
				var searchId = fieldContentsSplit[ 1 ];
				_searchIdsObject_Key_projectSearchId[ projectSearchId ] = searchId;
			} );
			
			//  Did user order project search ids
			
			var $userOrderedProjectSearchIds = $("#userOrderedProjectSearchIds");
			if ( $userOrderedProjectSearchIds.length > 0 ) {
				_userOrderedProjectSearchIds = $userOrderedProjectSearchIds.val();
			}
			
			this.populateNavigation();

			this.populateColorsPerSearchArray();
			
			this.setColorsPerSearchOnSearchDetailsSection();


			var $anySearchesHaveScanDataYes = $("#anySearchesHaveScanDataYes");
			if ( $anySearchesHaveScanDataYes.length > 0 ) {
				_anySearchesHaveScanDataYes = true;
			}

			//  Contains {{link_type}} to replace with link type
			var $common_chart_outer_entry_template = $("#common_chart_outer_entry_template");
			if ( $common_chart_outer_entry_template.length === 0 ) {
				throw Error("No element with id 'common_chart_outer_entry_template'");
			}
			_common_chart_outer_entry_templateHTML = $common_chart_outer_entry_template.html();

			var $common_chart_inner_entry_template = $("#common_chart_inner_entry_template");
			if ( $common_chart_inner_entry_template.length === 0 ) {
				throw Error("No element with id 'common_chart_inner_entry_template'");
			}
			_common_chart_inner_entry_templateHTML = $common_chart_inner_entry_template.html();

			var $dummy_chart_entry_for_message_template = $("#dummy_chart_entry_for_message_template");
			if ( $dummy_chart_entry_for_message_template.length === 0 ) {
				throw Error("No element with id 'dummy_chart_entry_for_message_template'");
			}
			_dummy_chart_entry_for_message_templateHTML = $dummy_chart_entry_for_message_template.html();

			
			
			var getScanFilesForProjectSearchId = function( params ) {
				objectThis.getScanFilesForProjectSearchId( params );
			};
			
			var sectionInitActualParams = {

				OVERALL_GLOBALS : _OVERALL_GLOBALS,

				project_search_ids : _project_search_ids,
				
				searchIdsObject_Key_projectSearchId : _searchIdsObject_Key_projectSearchId,
				
				colorsPerSearch : _colorsPerSearch,

				anySearchesHaveScanDataYes : _anySearchesHaveScanDataYes,

				//  Contains {{link_type}} to replace with link type.  Contains {{link_type}}_chart_outer_container_jq chart_outer_container_jq
				common_chart_outer_entry_templateHTML : _common_chart_outer_entry_templateHTML,

				common_chart_inner_entry_templateHTML : _common_chart_inner_entry_templateHTML,

				dummy_chart_entry_for_message_templateHTML : _dummy_chart_entry_for_message_templateHTML,


				link_type_crosslink_constant : _link_type_crosslink_constant,
				link_type_looplink_constant : _link_type_looplink_constant,
				link_type_unlinked_constant : _link_type_unlinked_constant,
				link_type_default_selected : _link_type_default_selected,

				link_type_crosslink_LOWER_CASE_constant : _link_type_crosslink_LOWER_CASE_constant,
				link_type_looplink_LOWER_CASE_constant : _link_type_looplink_LOWER_CASE_constant,
				link_type_unlinked_LOWER_CASE_constant : _link_type_unlinked_LOWER_CASE_constant,

				link_type_combined_LOWER_CASE_constant : _link_type_combined_LOWER_CASE_constant,

				//   These will have the link type added in between prefix and suffix, adding a space after link type.
				//       There is no space at start of suffix to support no link type
				DUMMY_CHART_STATUS_TEXT_PREFIX_LOADING : _DUMMY_CHART_STATUS_TEXT_PREFIX_LOADING,
				DUMMY_CHART_STATUS_TEXT_SUFFIX_LOADING : _DUMMY_CHART_STATUS_TEXT_SUFFIX_LOADING,
				DUMMY_CHART_STATUS_TEXT_PREFIX_NO_DATA : _DUMMY_CHART_STATUS_TEXT_PREFIX_NO_DATA,
				DUMMY_CHART_STATUS_TEXT_SUFFIX_NO_DATA : _DUMMY_CHART_STATUS_TEXT_SUFFIX_NO_DATA,
				DUMMY_CHART_STATUS_TEXT_PREFIX_ERROR_LOADING : _DUMMY_CHART_STATUS_TEXT_PREFIX_ERROR_LOADING,
				DUMMY_CHART_STATUS_TEXT_SUFFIX_ERROR_LOADING : _DUMMY_CHART_STATUS_TEXT_SUFFIX_ERROR_LOADING,

				DUMMY_CHART_STATUS_WHOLE_TEXT_SCANS_NOT_UPLOADED : _DUMMY_CHART_STATUS_WHOLE_TEXT_SCANS_NOT_UPLOADED,

				// functions
				_passAJAXErrorTo_handleAJAXError : this._passAJAXErrorTo_handleAJAXError,
				_addChartOuterTemplate : this._addChartOuterTemplate,
				_addChartInnerTemplate : this._addChartInnerTemplate,
				_placeEmptyDummyChartForMessage : this._placeEmptyDummyChartForMessage,
				getColorAndBarColorFromLinkType : this.getColorAndBarColorFromLinkType,
				get_hash_json_Contents : this._get_hash_json_Contents,
				getScanFilesForProjectSearchId : getScanFilesForProjectSearchId
			};
			
			_pageSectionObjects.forEach( function( pageSectionObject, index, array ) {
				pageSectionObject.initActual( sectionInitActualParams );
			}, this );

			this.addClickAndOnChangeHandlers();


			//  Default display on page load

			//  TODO  TEMP comment out

			qcMergedPageSectionSummaryStatistics.show_Section_From_qcMergedPageMain();

			//  TODO  TEMP Add

//			qcMergedPageSectionDigestionStatistics.show_Section();
			
//			qcMergedPageSectionScanFileStatistics.show_Section();

//			qcMergedPageSection_PSM_Level_Statistics.show_Section();

//			qcMergedPageSection_PSM_Error_Estimates.show_Section();

//			qcMergedPageSectionModificationStatistics.show_Section();

//			qcMergedPageSection_Peptide_Level_Statistics.show_Section();


			saveView_dataPages.initialize();

		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}

	};
	
	/**
	 * Populate _colorsPerSearch with colors for each search 
	 */
	this.populateColorsPerSearchArray = function() {
		
		var number = _project_search_ids.length;
		
		_colorsPerSearch = [];
		
		var hueDivider = 360 / number;
		var saturation = 0.39;
		var brightness = 0.60;

		for ( var index = 0; index < number; index++ ) {

			var hue =  (360 - (hueDivider * index ) - 1) / 360;

			var color = Snap.hsb2rgb( hue, saturation, brightness );
			
			_colorsPerSearch.push( color.hex );
		}
	};

	/**
	 * Set background color on each element with class 'search_color_block_jq'
	 */
	this.setColorsPerSearchOnSearchDetailsSection = function() {
		
		var $search_color_block_jq = $(".search_color_block_jq");
		$search_color_block_jq.each( function( index, element ) {
			var $htmlElement = $( this );
			var color = _colorsPerSearch[ index ];
			$htmlElement.css( "background-color", color );
		});
	};


	/**
	 * Add Click and onChange handlers 
	 */
	this.addClickAndOnChangeHandlers = function() {
		var objectThis = this;

		var $update_from_database_button = $("#update_from_database_button");
		$update_from_database_button.click( function( event ) { 
			try {
				objectThis._update_from_database_button_Clicked( { clickedThis : this } ); 
				event.preventDefault();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});

		var $qc_page_expand_all_button = $("#qc_page_expand_all_button");
		$qc_page_expand_all_button.click( function( event ) { 
			try {
				objectThis._expand_all_button_Clicked( { clickedThis : this } ); 
				event.preventDefault();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
	};

	/**
	 * "Expand All" Button Clicked
	 */
	this._expand_all_button_Clicked = function( params ) {
		var clickedThis = params.clickedThis;

		_pageSectionObjects.forEach( function( pageSectionObject, index, array ) {
			pageSectionObject.show_Section();
		}, this );
		
	}; 
	
	
	
	var _scanFiles_ForProjectSearchId_Cached = null;
	
	/**
	 * Calls params.callback with object containing resulting scan file entries array, or null if no scan files
	 * 
	 * Call to callback:
	 *    callback( { scanFiles : null, selectorUpdated : false } );
	 *    callback( { scanFiles : scanFiles, selectorUpdated : true } );
	 *    
	 */
	this.getScanFilesForProjectSearchId = function( params ) {
		var objectThis = this;
		var htmlElementSelector = params.htmlElementSelector;
		var addAllOption = params.addAllOption;
		var callback = params.callback;
		if ( ! callback ) {
			throw Error("No htmlElementSelector param passed to getScanFilesForProjectSearchId(...)" );
		}
		if ( ! callback ) {
			throw Error("No callback param passed to getScanFilesForProjectSearchId(...)" );
		}
		
		if ( ! _anySearchesHaveScanDataYes ) {
			callback( {  scanFiles : null, selectorUpdated : false } );
			return;
		}
		if ( _scanFiles_ForProjectSearchId_Cached ) {
			objectThis.getScanFilesForProjectSearchId_ProcessResponse( { originalParams : params } );
			return;
		}

		// TODO  Hard code taking first project search id
		var projectSearchId = _project_search_ids[ 0 ];	
				
		var _URL = "services/utils/getScanFilesForProjectSearchId";
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
				try {
					_scanFiles_ForProjectSearchId_Cached = data;
					objectThis.getScanFilesForProjectSearchId_ProcessResponse( { originalParams : params } );
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

	this.getScanFilesForProjectSearchId_ProcessResponse = function( params ) {
//		var objectThis = this;
		var originalParams = params.originalParams;
		var htmlElementScanFileTR = originalParams.htmlElementScanFileTR;
		var addAllOption = originalParams.addAllOption;
		var callback = originalParams.callback;
		
		var scanFiles = _scanFiles_ForProjectSearchId_Cached;
		
		if ( scanFiles.length === 0 ) {
			callback( {  scanFiles : null, selectorUpdated : false } );
		}
				
		var $htmlElementScanFileTR = $( htmlElementScanFileTR );
		var $scan_file_selector_jq = $htmlElementScanFileTR.find(".scan_file_selector_jq");
		var $single_scan_file_display_jq = $htmlElementScanFileTR.find(".single_scan_file_display_jq");
		
		$scan_file_selector_jq.empty();
		var optionsHTMLarray = [];
		if ( addAllOption && scanFiles.length > 1 ) {
			//  Only add if more than one scan file
			optionsHTMLarray.push( "<option value=''>All</option>" );
		}
		for ( var scanFilesIndex = 0; scanFilesIndex < scanFiles.length; scanFilesIndex++ ) {
			var scanFile = scanFiles[ scanFilesIndex ];
			var html = "<option value='" + scanFile.id + "'>" + scanFile.filename + "</option>";
			optionsHTMLarray.push( html );
		}
		var optionsHTML = optionsHTMLarray.join("");
		$scan_file_selector_jq.append( optionsHTML );
		
		if ( scanFiles.length === 1 ) {
			//  Only one scan file
			scanFile = scanFiles[ 0 ];
			$scan_file_selector_jq.hide();
			$single_scan_file_display_jq.show();
			$single_scan_file_display_jq.text( scanFile.filename );
		} else {
			$scan_file_selector_jq.show();
			$single_scan_file_display_jq.hide();
		}

		callback( { scanFiles : _scanFiles_ForProjectSearchId_Cached, selectorUpdated : true } );
	};
	

	/**
	 * 
	 */
	this._update_from_database_button_Clicked = function( params ) {
		this.updatePageForFilterCriteria();
	};

	//////////////////

	/**
	 *  
	 */
	this.updatePageForFilterCriteria = function() {

		this.updatePageFromFiltersToURLHashJSVarsAndPageData();
		this.populateNavigation();
		
		saveView_dataPages.searchFormUpdateButtonPressed_ForSaveView();
		
		this.clearAllDisplayedDataAndCharts();
		this.loadDataForDisplayedDataAndCharts();
	};

	/**
	 *  
	 */
	this.clearAllDisplayedDataAndCharts = function() {

		_pageSectionObjects.forEach( function( pageSectionObject, index, array ) {
			pageSectionObject.clearSection();
		}, this );
		
	};

	/**
	 *  
	 */
	this.loadDataForDisplayedDataAndCharts = function() {

		_pageSectionObjects.forEach( function( pageSectionObject, index, array ) {
			pageSectionObject.loadSectionIfNeededIfShown();
		}, this );
		
	};


	///////////////////////////////////////////////////



	/**
	 * Read strings from page from Java constants and store in Javascript variables used as constants 
	 */
	this.populateConstantsFromPage = function() {
		var $link_type_crosslink_constant = $("#link_type_crosslink_constant");
		_link_type_crosslink_constant = $link_type_crosslink_constant.text();
		var $link_type_looplink_constant = $("#link_type_looplink_constant");
		_link_type_looplink_constant = $link_type_looplink_constant.text();
		var $link_type_unlinked_constant = $("#link_type_unlinked_constant");
		_link_type_unlinked_constant = $link_type_unlinked_constant.text();

		_link_type_crosslink_LOWER_CASE_constant = _link_type_crosslink_constant.toLowerCase();
		_link_type_looplink_LOWER_CASE_constant = _link_type_looplink_constant.toLowerCase();
		_link_type_unlinked_LOWER_CASE_constant = _link_type_unlinked_constant.toLowerCase();

		_link_type_default_selected = [ _link_type_crosslink_constant, _link_type_looplink_constant, _link_type_unlinked_constant ]
	};

	/**
	 * Get the defaults out of the DOM placed by the Server Side Code and store in Javascript variables 
	 */
	this.getDefaultsFromPage = function() {

		const $default_values_cutoffs_others = $("#default_values_cutoffs_others");
		const default_values_cutoffs_othersString = $default_values_cutoffs_others.val();

		try {
			const default_values_cutoffs_others = JSON.parse( default_values_cutoffs_othersString );

			//  Copy only the properties that are set in the server side code
			_defaultsCutoffsAndOthers = { cutoffs: default_values_cutoffs_others.cutoffs, minPSMs : default_values_cutoffs_others.minPSMs };

		} catch( e2 ) {
			throw Error( "Failed to parse default_values_cutoffs_othersString as JSON.  " +
					"Error Message: " + e2.message +
					".  default_values_cutoffs_othersString: |" +
					default_values_cutoffs_othersString +
					"|." );
		}
	}

	/**
	 * 
	 */
	this.getCutoffDefaults = function() {

		const default_values_cutoffs = _defaultsCutoffsAndOthers.cutoffs;

		return default_values_cutoffs;
	}

	///////////////////////////////////////////////////////////

	/**
	 * return bar color for link type
	 */
	this.getColorAndBarColorFromLinkType = function( linkType ) {
		if ( linkType === _link_type_crosslink_LOWER_CASE_constant ) {
			return { color : _OVERALL_GLOBALS.BAR_COLOR_CROSSLINK, barColor : _OVERALL_GLOBALS.BAR_STYLE_CROSSLINK };
		} else if ( linkType === _link_type_looplink_LOWER_CASE_constant ) {
			return { color : _OVERALL_GLOBALS.BAR_COLOR_LOOPLINK, barColor : _OVERALL_GLOBALS.BAR_STYLE_LOOPLINK };
		} else if ( linkType === _link_type_unlinked_LOWER_CASE_constant ) {
			return { color : _OVERALL_GLOBALS.BAR_COLOR_UNLINKED, barColor : _OVERALL_GLOBALS.BAR_STYLE_UNLINKED };

		} else if ( linkType === _link_type_combined_LOWER_CASE_constant ) {
			return { color : _OVERALL_GLOBALS.BAR_COLOR_ALL_COMBINED, barColor : _OVERALL_GLOBALS.BAR_STYLE_ALL_COMBINED };
		} else {
			throw Error( "getColorAndBarColorFromLinkType(...), unknown link type: " + linkType );
		}
	};


	/**
	 *  
	 */
	this.updatePageFiltersFromURLHash = function() {

		_hash_json_Contents = this.getJsonFromHash();

		cutoffProcessingCommonCode.putCutoffsOnThePage(  { cutoffs : _hash_json_Contents.cutoffs } );

		//  Pass cutoffs and ann type display to all JS that call web services to get data (IE PSMs)
		webserviceDataParamsDistributionCommonCode.paramsForDistribution( _hash_json_Contents  )

		//  Mark check boxes for link types
		var linkTypes = _hash_json_Contents.linkTypes;

		this.markCheckBoxesForLinkTypes( linkTypes );

		//  Mark check boxes for chosen dynamic mod masses
		var dynamicModMasses = _hash_json_Contents.mods;
		if ( dynamicModMasses !== undefined && dynamicModMasses !== null && dynamicModMasses.length > 0  ) {
			//  dynamicModMasses not null so process it, empty array means nothing chosen
			if ( dynamicModMasses.length > 0 ) {
				var $mod_mass_filter_jq = $(".mod_mass_filter_jq");
				$mod_mass_filter_jq.each( function( index, element ) {
					var $item = $( this );
					var linkTypeFieldValue = $item.val();
					//  if linkTypeFieldValue found in dynamicModMasses array, set it to checked, else set it to not checked
					var checkedPropertyValue = false;
					for ( var dynamicModMassesIndex = 0; dynamicModMassesIndex < dynamicModMasses.length; dynamicModMassesIndex++ ) {
						var dynamicModMassesEntry = dynamicModMasses[ dynamicModMassesIndex ];
						if ( dynamicModMassesEntry === linkTypeFieldValue ) {
							checkedPropertyValue = true;
							break;
						}
					}
					$item.prop('checked', checkedPropertyValue);
				});
			}
		} else {
			//  dynamicModMasses null means all are chosen, since don't know which one was wanted
			var $mod_mass_filter_jq = $(".mod_mass_filter_jq");
			$mod_mass_filter_jq.each( function( index, element ) {
				var $item = $( this );
				$item.prop('checked', true);
			});
		}
				
		//  Mark Multi <select> for chosen Proteins to include
		if ( _hash_json_Contents.includeProteinSeqVIdsDecodedArray ) {
			const $includeProtein = $("#includeProtein");
			$includeProtein.val( _hash_json_Contents.includeProteinSeqVIdsDecodedArray );
		}
		if ( _hash_json_Contents.inclProtSeqVIdsAllSelected ) {
			//  All selected so build array of all option values and assign it
			const $includeProtein = $("#includeProtein");
						//  Set all <option> values in selector to selected
			const $optionAll = $includeProtein.find("option");
			$optionAll.each( ( index, element ) => {
				element.selected = true;
			});
		}
	};

	/**
	 * 
	 */
	this.markCheckBoxesForLinkTypes = function( linkTypes ) {
		if ( linkTypes !== undefined && linkTypes !== null ) {
			//  linkTypes not null so process it, empty array means nothing chosen
			if ( linkTypes.length > 0 ) {
				var $link_type_jq = $(".link_type_jq");
				$link_type_jq.each( function( index, element ) {
					var $item = $( this );
					var linkTypeFieldValue = $item.val();
					//  if linkTypeFieldValue found in linkTypes array, set it to checked, else set it to not checked
					var checkedPropertyValue = false;
					for ( var linkTypesIndex = 0; linkTypesIndex < linkTypes.length; linkTypesIndex++ ) {
						var linkTypesEntry = linkTypes[ linkTypesIndex ];
						if ( linkTypesEntry === linkTypeFieldValue ) {
							checkedPropertyValue = true;
							break;
						}
					}
					$item.prop('checked', checkedPropertyValue);
				});
			}
		} else {
			//  linkTypes null means all are chosen, since don't know which one was wanted
			var $link_type_jq = $(".link_type_jq");
			$link_type_jq.each( function( index, element ) {
				var $item = $( this );
				$item.prop('checked', true);
			});
		}
	};











	/**
	 * 
	 */
	this.updatePageFromFiltersToURLHashJSVarsAndPageData = function() {
		try {
			var dataFromFiltersResult =
				this.getDataFromFilters();
			if ( dataFromFiltersResult 
					&& dataFromFiltersResult.output_FieldDataFailedValidation ) {
				//  Only update if there were no errors in the input data
				return;
			}

			this.updateURLHashWithJSONObject( dataFromFiltersResult );

			this.decode_inclProtSeqVIds_Update_json_param( dataFromFiltersResult );

			//  Update filter data held in JS variable
			_hash_json_Contents = dataFromFiltersResult;

		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};

	/**
	 * 
	 */
	this.updateURLHashWithJSONObject = function( jsonObject ) {

		const jsonObject_Copy = copyObject_DeepCopy_Proxl( jsonObject );  // External function

		for ( const propertyNameToRemove of _hash_json_Contents_PROPERTY_ADDITIONS ) {
			delete jsonObject_Copy[ propertyNameToRemove ];
		}

		var newHash = JSON.stringify( jsonObject );	
		var newHashEncodedToEncodedURIComponent = LZString.compressToEncodedURIComponent( newHash );
		try {
			window.location.hash = newHashEncodedToEncodedURIComponent;
		} catch ( e ) {
			//  TODO  Need to handle this error.  
			//     The user really shouldn't continue since the settings are not being stored in the Hash
			console.log( "Update window.location.hash Failed: e: " + e );
		}
	}


	/**
	 * 
	 */
	this.getDataFromFilters = function() {

		var getCutoffsFromThePageResult = cutoffProcessingCommonCode.getCutoffsFromThePage(  {  } );
		var getCutoffsFromThePageResult_FieldDataFailedValidation = getCutoffsFromThePageResult.getCutoffsFromThePageResult_FieldDataFailedValidation;
		if ( getCutoffsFromThePageResult_FieldDataFailedValidation ) {
			//  Cutoffs failed validation and error message was displayed
			return { output_FieldDataFailedValidation : getCutoffsFromThePageResult_FieldDataFailedValidation };  //  EARLY EXIT from function
		}
		var outputCutoffs = getCutoffsFromThePageResult.cutoffsByProjectSearchId;
		//  Output the selected Annotation data for display
		var getAnnotationTypeDisplayFromThePageResult = annotationDataDisplayProcessingCommonCode.getAnnotationTypeDisplayFromThePage( {} );
		var annotationTypeDisplayByProjectSearchId = getAnnotationTypeDisplayFromThePageResult.annTypeIdDisplayByProjectSearchId;
		//  Create array from check boxes for chosen link types
		var outputLinkTypes = [];
		var $link_type_jq = $(".link_type_jq");
		$link_type_jq.each( function( index, element ) {
			var $item = $( this );
			if ( $item.prop('checked') === true ) {
				var linkTypeFieldValue = $item.val();
				outputLinkTypes.push( linkTypeFieldValue );
			}
		});
		//  If no link types are selected, change crosslink to selected
		if ( outputLinkTypes.length === 0 ) {
			outputLinkTypes = _link_type_default_selected;
			//  Update page with defaults
			this.markCheckBoxesForLinkTypes( outputLinkTypes );
		}

		//  Create array from check boxes for chosen dynamic mod masses
		var outputDynamicModMasses = [];
		var allDynamicModMassesChosen = true;
		var $mod_mass_filter_jq = $(".mod_mass_filter_jq");
		$mod_mass_filter_jq.each( function( index, element ) {
			var $item = $( this );
			if ( $item.prop('checked') === true ) {
				var fieldValue = $item.val();
				outputDynamicModMasses.push( fieldValue );
			} else {
				allDynamicModMassesChosen = false;
			}
		});
		if ( allDynamicModMassesChosen ) {
			outputDynamicModMasses = null;  //  set to null when all chosen
		}
		var outputFilterCContents = { 
				cutoffs : outputCutoffs, 
				annTypeIdDisplay : annotationTypeDisplayByProjectSearchId,
				linkTypes : outputLinkTypes, 
				mods : outputDynamicModMasses 
		};

		const areAllOrNoneIncludeProteinsSelected_Result = qc_pages_Single_Merged_Common.areAllOrNoneIncludeProteinsSelected();

		if ( areAllOrNoneIncludeProteinsSelected_Result.allSelected ) {
			//  Set property in output object
			outputFilterCContents.inclProtSeqVIdsAllSelected = true;

		} else if ( ( ! areAllOrNoneIncludeProteinsSelected_Result.noneSelected ) ) {
			var outputIncludeProteinsAsStrings = $("#includeProtein").val( );
			var getEncodedIncludeProteinsResponse = qc_pages_Single_Merged_Common.getEncodedIncludeProteins( outputIncludeProteinsAsStrings );
			//  Set property in output object
			outputFilterCContents.inclProtSeqVIds = getEncodedIncludeProteinsResponse.encodedIncludeProteins;
		}

		return outputFilterCContents;
	};


	/**
	 * get values for variables from the hash part of the URL as JSON
	 */
	this.getRawJsonFromHash = function() {
		var jsonFromHash = null;
		var windowHash = window.location.hash;
		if ( windowHash === "" || windowHash === "#" ) {
			//  No Hash value so set defaults and return
			jsonFromHash = { 
					cutoffs : this.getCutoffDefaults(),
					linkTypes : _link_type_default_selected
			};
			return jsonFromHash;
		}
		var windowHashContentsMinusHashChar = windowHash.slice( 1 );
		// Try first:  the hash contained Compressed URI-encoded JSON, try decoding using decodeURIComponent( windowHashContentsMinusHashChar )
		try {
			//  LZString.decompressFromEncodedURIComponent(...) returns null if unable to decompress
			var windowHashContentsMinusHashCharDecompressedDecodeURIComponent = LZString.decompressFromEncodedURIComponent( windowHashContentsMinusHashChar );
			if ( windowHashContentsMinusHashCharDecompressedDecodeURIComponent !== null 
					&& windowHashContentsMinusHashCharDecompressedDecodeURIComponent !== undefined ) {
				jsonFromHash = JSON.parse( windowHashContentsMinusHashCharDecompressedDecodeURIComponent );
			} else {
				jsonFromHash = undefined;
			}
		} catch( e ) {
			jsonFromHash = undefined;
		}
		if ( jsonFromHash === null || jsonFromHash === undefined ) {
			try {
				// if this works, the hash contains native (non encoded) JSON
				jsonFromHash = JSON.parse( windowHashContentsMinusHashChar );
			} catch( e ) {
				jsonFromHash = undefined;
			}
		}
		if ( jsonFromHash === null || jsonFromHash === undefined ) {
			try {
				// if we got here, the hash contained URI-encoded JSON, try decoding using decodeURI( windowHashContentsMinusHashChar )
				var windowHashContentsMinusHashCharDecodeURI = decodeURI( windowHashContentsMinusHashChar );
				jsonFromHash = JSON.parse( windowHashContentsMinusHashCharDecodeURI );
			} catch( e ) {
				jsonFromHash = undefined;
			}
		}
		if ( jsonFromHash === null || jsonFromHash === undefined ) {
			try {
				// if we got here, the hash contained URI-encoded JSON, try decoding using decodeURIComponent( windowHashContentsMinusHashChar )
				var windowHashContentsMinusHashCharDecodeURIComponent = decodeURIComponent( windowHashContentsMinusHashChar );
				jsonFromHash = JSON.parse( windowHashContentsMinusHashCharDecodeURIComponent );
			} catch( e ) {
				jsonFromHash = undefined;
			}
		}
		if ( jsonFromHash === null || jsonFromHash === undefined ) {
			throw Error( "Failed to parse window hash string as JSON and decodeURI and then parse as JSON.  windowHashContentsMinusHashChar: " 
					+ windowHashContentsMinusHashChar );
		}
		//   Transform json on hash to expected object for rest of the code
		var json = jsonFromHash;
		return json;
	}

	/**
	 * get values for variables from the hash part of the URL as JSON
	 */
	this.getJsonFromHash = function() {
		var json = this.getRawJsonFromHash();
		if ( json.cutoffs === undefined || json.cutoffs === null ) {
			//  Set cutoff defaults if not in JSON
			json.cutoffs = getCutoffDefaults();
		}

		//  START: Special update to allow projectSearchId values to be added or removed from URL
		//  Update cutoffs to add defaults for search ids in defaults but not in cutoffs
		//  Update cutoffs to remove search ids not in defaults but in cutoffs
		var cutoffs_Searches = json.cutoffs.searches;
		var cutoffDefaultsFromPage = this.getCutoffDefaults();
		var cutoffDefaultsFromPage_Searches = cutoffDefaultsFromPage.searches;
		//  Update cutoffs_Searches with values from cutoffDefaultsFromPage
		//      for any searches in cutoffDefaultsFromPage but not in cutoffs_Searches
		var cutoffDefaultsFromPageSrchIdArry = Object.keys( cutoffDefaultsFromPage_Searches );
		for ( var index = 0; index < cutoffDefaultsFromPageSrchIdArry.length; index++ ) {
			var cutoffDefaultsFromPageSrchId = cutoffDefaultsFromPageSrchIdArry[ index ];
			var cutoffs_SearchesEntryForDefProcessing = cutoffs_Searches[ cutoffDefaultsFromPageSrchId ];
			if ( cutoffs_SearchesEntryForDefProcessing === undefined || cutoffs_SearchesEntryForDefProcessing === null ) {
				// Not in cutoff values so copy from default
				var cutoffDefaultValues_ForSearch = cutoffDefaultsFromPage_Searches[ cutoffDefaultsFromPageSrchId ];
				// 											(  true for deep, target object, source object, <source object 2>, ... )
				var cloneOfDefaultValuesForSearch = jQuery.extend( true /* [deep ] */, {}, cutoffDefaultValues_ForSearch );
				cutoffs_Searches[ cutoffDefaultsFromPageSrchId ] = cloneOfDefaultValuesForSearch;
			}
		}
		//  Remove cutoffs in cutoffs_Searches for searches not in cutoffDefaultsFromPage
		var cutoffs_SearchesSrchIdArry = Object.keys( cutoffs_Searches );
		for ( var index = 0; index < cutoffs_SearchesSrchIdArry.length; index++ ) {
			var cutoffs_SearchesSrchId = cutoffs_SearchesSrchIdArry[ index ];
			var cutoffDefaultsFromPageForSrchId = cutoffDefaultsFromPage_Searches[ cutoffs_SearchesSrchId ];
			if ( cutoffDefaultsFromPageForSrchId === undefined || cutoffDefaultsFromPageForSrchId === null ) {
				// Not in default values so remove from input
				delete cutoffs_Searches[ cutoffs_SearchesSrchId ];
			}
		}
		//  END: Special update to allow projectSearchId values to be added or removed from URL
		
		//  If linkTypes property not in hash, set to default
		if ( ! json.linkTypes ) {
			json.linkTypes = _link_type_default_selected;
		}
		
		this.decode_inclProtSeqVIds_Update_json_param( json );

		return json;
	}

	this.decode_inclProtSeqVIds_Update_json_param = function( json ) {

		//  Decode json.inclProtSeqVIds and place result into json.includeProteinSeqVIdsDecodedArray

		if ( json.inclProtSeqVIds ) {
			const includeProteinSeqVIdsSet = qc_pages_Single_Merged_Common.getDecodedIncludeProteins( json.inclProtSeqVIds );
			if ( includeProteinSeqVIdsSet ) {
				const includeProteinSeqVIdsDecodedArray = Array.from( includeProteinSeqVIdsSet );
				json.includeProteinSeqVIdsDecodedArray = includeProteinSeqVIdsDecodedArray;
			}
		}
	}

	/////////////////////////////////////////////////
	/////////////////////////////////////////////////

	//    Place Empty Dummy Chart for Message
	

	/**
	 * Overridden for Specific elements like Chart Title and X and Y Axis labels
	 */
	var _DUMMY_CHART_GLOBALS = {
			_CHART_DEFAULT_FONT_SIZE : 12,  //  Default font size - using to set font size for tick marks.
			_TITLE_FONT_SIZE : 15, // In PX
			_AXIS_LABEL_FONT_SIZE : 14, // In PX
			_TICK_MARK_TEXT_FONT_SIZE : 14 // In PX
	};


	/**
	 * 
	 */
	this._placeEmptyDummyChartForMessage = function( params ) {
		var $chart_outer_container_jq = params.$chart_outer_container_jq;
		var linkType = params.linkType;  //  May be all caps or lower case
		var messageWhole = params.messageWhole;  //  if set, it is the whole message
		var messagePrefix = params.messagePrefix;
		var messageSuffix = params.messageSuffix;

		var $addedDummyChartEntryForMessageTemplate = $( _dummy_chart_entry_for_message_templateHTML ).appendTo( $chart_outer_container_jq );

		var $message_text_containing_div_jq = $addedDummyChartEntryForMessageTemplate.find(".message_text_containing_div_jq");
		if ( $message_text_containing_div_jq.length === 0 ) {
			throw Error( "element with class 'message_text_containing_div_jq' not found" );
		}
		var $message_text_jq = $addedDummyChartEntryForMessageTemplate.find(".message_text_jq");
		if ( $message_text_jq.length === 0 ) {
			throw Error( "element with class 'message_text_jq' not found" );
		}
		var $dummy_chart_container_jq = $addedDummyChartEntryForMessageTemplate.find(".dummy_chart_container_jq");
		if ( $dummy_chart_container_jq.length === 0 ) {
			throw Error( "element with class 'dummy_chart_container_jq' not found" );
		}
		var dummy_chart_container_jqHTMLElement = $dummy_chart_container_jq[0];
		if ( dummy_chart_container_jqHTMLElement === null || dummy_chart_container_jqHTMLElement === undefined ) {
			throw Error( "element with class 'dummy_chart_container_jq' not found" );
		}

		var displayMessage = undefined;

		if ( messageWhole ) {
			displayMessage = messageWhole;
		} else {
			//  A space is required after each link type since the messageSuffix does NOT start with a space
			var linkTypeDisplay = null;
			if ( linkType === null || linkType === undefined ) {
				linkTypeDisplay = "";
			} else if ( linkType === _link_type_crosslink_constant || linkType === _link_type_crosslink_LOWER_CASE_constant ) {
				linkTypeDisplay = "Crosslink ";
			} else if ( linkType === _link_type_looplink_constant || linkType === _link_type_looplink_LOWER_CASE_constant ) {
				linkTypeDisplay = "Looplink ";
			} else if ( linkType === _link_type_unlinked_constant || linkType === _link_type_unlinked_LOWER_CASE_constant ) {
				linkTypeDisplay = "Unlinked ";
			} else {
				linkTypeDisplay = "Unknown Link Type ";
			}
			displayMessage = messagePrefix + linkTypeDisplay + messageSuffix;
		}

		$message_text_jq.text( displayMessage );

		//  Position display message in center of $chartContainer

		var addedDummyChartEntryForMessageTemplateWidth = $addedDummyChartEntryForMessageTemplate.width();
		var addedDummyChartEntryForMessageTemplateHeight = $addedDummyChartEntryForMessageTemplate.height();

		var message_text_containing_div_jqWidth = $message_text_containing_div_jq.width();
		var message_text_containing_div_jqHeight = $message_text_containing_div_jq.height();

		var message_text_containing_div_jqLeft = ( addedDummyChartEntryForMessageTemplateWidth / 2 ) - ( message_text_containing_div_jqWidth / 2 );
		var message_text_containing_div_jqTop = ( addedDummyChartEntryForMessageTemplateHeight / 2 ) - ( message_text_containing_div_jqHeight / 2 );

		$message_text_containing_div_jq.css( { top: message_text_containing_div_jqTop, left: message_text_containing_div_jqLeft } );

		//  add dummy chart

		var optionsFullsize = {
				//  Overridden for Specific elements like Chart Title and X and Y Axis labels
				fontSize: _DUMMY_CHART_GLOBALS._CHART_DEFAULT_FONT_SIZE,  //  Default font size - using to set font size for tick marks.
				pointSize: 0,
				legend: 'none',
//				title: chartTitle, // Title above chart
				titleTextStyle: {
					color : _PROXL_DEFAULT_FONT_COLOR, //  Set default font color
//					color: <string>,    // any HTML string color ('red', '#cc00cc')
//					fontName: <string>, // i.e. 'Times New Roman'
					fontSize: _DUMMY_CHART_GLOBALS._TITLE_FONT_SIZE // 12, 18 whatever you want (don't specify px)
//					bold: <boolean>,    // true or false
//					italic: <boolean>   // true of false
				},
				//  X axis label below chart
				hAxis: 
				{ 	title: 'X Axis'
					, titleTextStyle: { color: 'black', fontSize: _DUMMY_CHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
				,gridlines: {  
					color: 'none'  //  No vertical grid lines on the horzontal axis
				}
//				,baseline: 0     // always start at zero
				,baselineColor: 'none' // Hide 'Zero' line
//					,baselineColor: '#CDCDCD' // Make 'Zero' line really light
					,ticks: [ 0, 1 ]
//				,maxValue : max_M_over_Z_Error
//				,minValue : min_M_over_Z_Error
				},  
				//  Y axis label left of chart
				vAxis: 
				{ 	title: 'Y Axis'
					, titleTextStyle: { color: 'black', fontSize: _DUMMY_CHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
//				,baseline: 0     // always start at zero
				,baselineColor: 'none' // Hide 'Zero' line
					,ticks: [ 0, 1 ]
//				,ticks: vAxisTicks
//				,maxValue : maxCount
//				,maxValue : maxPPMError
//				,minValue : minPPMError

				},
//				legend: { position: 'none' }, //  position: 'none':  Don't show legend of bar colors in upper right corner
				//  Size picked up from containing HTML element
//				width : 500, 
//				height : 300,   // width and height of chart, otherwise controlled by enclosing div
//				bar: { groupWidth: '100%' },  // set bar width large to eliminate space between bars
//				colors: barColors,
				tooltip: {isHtml: true}
		};        

		var chartData = [ ["X axis", "Y axis"], [ 1,1 ] ];

		// create the chart
		var data = google.visualization.arrayToDataTable( chartData );

		var chartFullsize = new google.visualization.ScatterChart( dummy_chart_container_jqHTMLElement );
		chartFullsize.draw( data, optionsFullsize );
	};

	/**
	 * 
	 */
	this._addChartOuterTemplateForLinkTypes = function( params ) {
		var objectThis = this;
		var $chart_group_container_table_jq = params.$chart_group_container_table_jq;
		var linkTypes = params.linkTypes;

		linkTypes.forEach( function ( currentArrayValue, index, array ) {
			var linkType = currentArrayValue;
			this._addChartOuterTemplate( { $chart_group_container_table_jq : $chart_group_container_table_jq, linkType : linkType } );
		}, this /* passed to function as this */ );
	};

	/**
	 * linkType optional
	 */
	this._addChartOuterTemplate = function( params ) {
		var $chart_group_container_table_jq = params.$chart_group_container_table_jq;
		var linkType = params.linkType;  

		if ( linkType === undefined || linkType === null ) {
			linkType = "";
		}

		var html = _common_chart_outer_entry_templateHTML.replace( "{{link_type}}", linkType );

		var $tdElement = $( html ).appendTo( $chart_group_container_table_jq );
		var $chart_outer_container_jq = $tdElement.find(".chart_outer_container_jq");

		return $chart_outer_container_jq;
	};

	/**
	 * 
	 */
	this._addChartInnerTemplate = function( params ) {
		var $chart_outer_container_jq = params.$chart_outer_container_jq;

		var $divElement = $( _common_chart_inner_entry_templateHTML ).appendTo( $chart_outer_container_jq );
		var $chart_container_jq = $divElement.find(".chart_container_jq");

		return $chart_container_jq;
	};
	
	/////////////////////////////////////////////////
	/////////////////////////////////////////////////


	/**
	 * 
	 */
	this.populateNavigation = function() {
		var queryString = "?";
		var items = new Array();
		for ( var i = 0; i < _project_search_ids.length; i++ ) {
			items.push( "projectSearchId=" + _project_search_ids[ i ] );
		}
		if ( _userOrderedProjectSearchIds ) {
			items.push( "ds=" + _userOrderedProjectSearchIds );
		}
		
		var baseJSONObject = this.getNavigationJSON_Not_for_Image_Or_Structure();
		
		var psmPeptideCutoffsForProjectSearchIds_JSONString = JSON.stringify( baseJSONObject );
		var psmPeptideCutoffsForProjectSearchIds_JSONStringEncodedURIComponent = encodeURIComponent( psmPeptideCutoffsForProjectSearchIds_JSONString ); 
		//  Parameter name matches standard form parameter name for JSON
		items.push( "queryJSON=" + psmPeptideCutoffsForProjectSearchIds_JSONStringEncodedURIComponent );
		queryString += items.join( "&" );
		var html = "";
		if ( _project_search_ids.length > 1 ) {
			html += " <span class=\"tool_tip_attached_jq\" data-tooltip=\"View peptides\" style=\"white-space:nowrap;\" >[<a href=\"mergedPeptide.do" + queryString + "\">Peptide View</a>]</span>";
			html += " <span class=\"tool_tip_attached_jq\" data-tooltip=\"View proteins\" style=\"white-space:nowrap;\" >[<a href=\"mergedCrosslinkProtein.do" + queryString + "\">Protein View</a>]</span>";
			html += " <span class=\"tool_tip_attached_jq\" data-tooltip=\"View protein coverage report\" style=\"white-space:nowrap;\" >[<a href=\"mergedProteinCoverageReport.do" + queryString + "\">Coverage Report</a>]</span>";
		} else {
			//  Add Peptide Link
			html += " [<a class=\"tool_tip_attached_jq\" data-tooltip=\"View peptides\" href='";
			var viewSearchPeptideDefaultPageUrl = $("#viewSearchPeptideDefaultPageUrl").val();
			if ( viewSearchPeptideDefaultPageUrl === undefined || viewSearchPeptideDefaultPageUrl === "" ) {
				html += "peptide.do" + queryString;
			} else {
				html += viewSearchPeptideDefaultPageUrl;
			}
			html += "'>Peptide View</a>]";
			//  Add Protein View Link
			html += " [<a class=\"tool_tip_attached_jq\" data-tooltip=\"View proteins\" href='";
			var viewSearchCrosslinkProteinDefaultPageUrl = $("#viewSearchCrosslinkProteinDefaultPageUrl").val();
			if ( viewSearchCrosslinkProteinDefaultPageUrl === undefined || viewSearchCrosslinkProteinDefaultPageUrl === "" ) {
				html += "crosslinkProtein.do" + queryString;
			} else {
				html += viewSearchCrosslinkProteinDefaultPageUrl;
			}
			html += "'>Protein View</a>]";
			//  Add Coverage Report Link
			html += " [<a class=\"tool_tip_attached_jq\" data-tooltip=\"View protein coverage report\" href='";
			var viewProteinCoverageReportDefaultPageUrl = $("#viewProteinCoverageReportDefaultPageUrl").val();
			if ( viewProteinCoverageReportDefaultPageUrl === undefined || viewProteinCoverageReportDefaultPageUrl === "" ) {
				html += "proteinCoverageReport.do" + queryString;
			} else {
				html += viewProteinCoverageReportDefaultPageUrl;
			}
			html += "'>Coverage Report</a>]";
			
		}

		var imageNavHTML = " <span class=\"tool_tip_attached_jq\" data-tooltip=\"Graphical view of links between proteins\" " +
		"style=\"white-space:nowrap;\" >[<a href=\"";
		var viewMergedImageDefaultPageUrl = $("#viewMergedImageDefaultPageUrl").val();
		if ( viewMergedImageDefaultPageUrl === undefined || viewMergedImageDefaultPageUrl === "" ) {
			var imageQueryString = "?";
			for ( var j = 0; j < _project_search_ids.length; j++ ) {
				if ( j > 0 ) {
					imageQueryString += "&";
				}
				imageQueryString += "projectSearchId=" + _project_search_ids[ j ];
			}
			if ( _userOrderedProjectSearchIds ) {
				imageQueryString +=  "&ds=" + _userOrderedProjectSearchIds;
			}
			
			var imageJSON = { };
			//  Add Filter cutoffs
			imageJSON[ 'cutoffs' ] = baseJSONObject.cutoffs;
			//  Add Ann Type Display
			var annTypeIdDisplay = baseJSONObject.annTypeIdDisplay;
			imageJSON[ 'annTypeIdDisplay' ] = annTypeIdDisplay;
//			add filter out non unique peptides
			var imageJSONString = encodeURIComponent( JSON.stringify( imageJSON ) );
			imageNavHTML += "image.do" + imageQueryString + "#" + imageJSONString;
		} else {
			imageNavHTML += viewMergedImageDefaultPageUrl;
		}
		imageNavHTML += "\">Image View</a>]</span>";
		
		html += imageNavHTML;
		
		var $navigation_links_except_structure = $("#navigation_links_except_structure"); 
		$navigation_links_except_structure.empty();
		$navigation_links_except_structure.html( html );
		addToolTips( $navigation_links_except_structure );
		
		//  Process Structure Link separately since may not be shown
		var $structure_viewer_link_span = $("#structure_viewer_link_span");
		if ( $structure_viewer_link_span.length > 0 ) {
			var structureNavHTML = "<span class=\"tool_tip_attached_jq\" data-tooltip=\"View data on 3D structures\" " +
			"style=\"white-space:nowrap;\" >[<a href=\"";
			var viewMergedStructureDefaultPageUrl = $("#viewMergedStructureDefaultPageUrl").val();
			if ( viewMergedStructureDefaultPageUrl === undefined || viewMergedStructureDefaultPageUrl === "" ) {
				var structureQueryString = "?";
				for ( var j = 0; j < _project_search_ids.length; j++ ) {
					if ( j > 0 ) {
						structureQueryString += "&";
					}
					structureQueryString += "projectSearchId=" + _project_search_ids[ j ];
				}
				if ( _userOrderedProjectSearchIds ) {
					structureQueryString +=  "&ds=" + _userOrderedProjectSearchIds;
				}

				var structureJSON = { };
				//  Add Filter cutoffs
				structureJSON[ 'cutoffs' ] = baseJSONObject.cutoffs;
				//  Add Ann Type Display
				var annTypeIdDisplay = baseJSONObject.annTypeIdDisplay;
				structureJSON[ 'annTypeIdDisplay' ] = annTypeIdDisplay;
//				add filter out non unique peptides
				var structureJSONString = encodeURIComponent( JSON.stringify( structureJSON ) );
				structureNavHTML += "structure.do" + structureQueryString + "#" + structureJSONString;
			} else {
				structureNavHTML += viewMergedStructureDefaultPageUrl;
			}
			structureNavHTML += "\">Structure View</a>]</span>";
			$structure_viewer_link_span.empty();
			$structure_viewer_link_span.html( structureNavHTML );
			addToolTips( $structure_viewer_link_span );
		}
	};


	/**
	 * 
	 */
	this.getNavigationJSON_Not_for_Image_Or_Structure = function() {
		var json = this.getJsonFromHash();
		///   Serialize cutoffs to JSON
		var cutoffs = json.cutoffs;
		var annTypeIdDisplay = json.annTypeIdDisplay;
		//  Layout of baseJSONObject  matches Java class A_QueryBase_JSONRoot
		var baseJSONObject = { cutoffs : cutoffs, annTypeIdDisplay : annTypeIdDisplay };
		return baseJSONObject;
	}


	/**
	 * Called from mergedPeptideProteinSearchesListVennDiagramSection.js to change order of project search ids in the URL
	 * 
	 * 
	 */
	this.changeProjectSearchIdOrderInURL = function( params ) {
		var projectSearchIdsInNewOrder = params.projectSearchIdsInNewOrder;
		
		var newProjectSearchIdParamsArray = [];
		
		projectSearchIdsInNewOrder.forEach(function( element, idex, array ) {
			var newProjectSearchIdParam = "projectSearchId=" + element;
			newProjectSearchIdParamsArray.push( newProjectSearchIdParam )
		}, this );
		
		var newProjectSearchIdParamsString = newProjectSearchIdParamsArray.join( "&" );
		
		//  qc.do?projectSearchId=
		
		var windowHref = window.location.href;
		
		var windowHash = window.location.hash;
		
		var strutsActionIndex = windowHref.indexOf( "qc.do?projectSearchId" );
		
		var windowHrefBeforeStrutsAction = windowHref.substring( 0, strutsActionIndex );
		
		var newWindowHref = windowHrefBeforeStrutsAction + "qc.do?" + newProjectSearchIdParamsString + "&ds=y" + windowHash;
		
		window.location.href = newWindowHref;
	};


	this.getQueryJSONString = function() {
//			var queryJSON = getNavigationJSON_Not_for_Image_Or_Structure();
		var queryJSON = this.getJsonFromHash();
		var queryJSONString = JSON.stringify( queryJSON );
		return queryJSONString;
	};

};

const qcMergedPageMain = new QCMergedPageMain();

qcMergedPageMain.init();

//  Copy to standard page level JS Code Object
//  Not currently supported  var standardFullPageCode = qcMergedPageMain;
