/**
 * viewQC.js
 * 
 * Javascript for the viewQC.jsp page
 * 
 * page variable viewQCPageCode
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

//  JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

/**
 * 
 */
$(document).ready(function() {
	
	//  Delay init until Google Charts have loaded which is done in header_main.jsp
	
	//     viewQCPageCode.init(); called in header_main.jsp after Google charts are initialized
	
//	try {
//	viewQCPageCode.init();
//	} catch( e ) {
//		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
//		throw e;
//	}


} ); // end $(document).ready(function() 


/**
 * Constructor 
 */
var ViewQCPageCode = function() {

	var _OVERALL_GLOBALS = { 
			 //  Color of bars:  from head_section_include_every_page.jsp
			BAR_COLOR_CROSSLINK : _PROXL_COLOR_LINK_TYPE_CROSSLINK, // '#A55353', // Crosslink: Proxl shades of red
			BAR_COLOR_LOOPLINK : _PROXL_COLOR_LINK_TYPE_LOOPLINK, // '#53a553',  // Looplink: green: #53a553
			BAR_COLOR_UNLINKED : _PROXL_COLOR_LINK_TYPE_UNLINKED, // '#5353a5'   //	Unlinked: blue: #5353a5
			BAR_COLOR_ALL_COMBINED : _PROXL_COLOR_LINK_TYPE_ALL_COMBINED   //  All Combined  Grey  #A5A5A5
	};
	
	_OVERALL_GLOBALS.BAR_STYLE_CROSSLINK = 
		"color: " + _OVERALL_GLOBALS.BAR_COLOR_CROSSLINK +
		"; stroke-color: " + _OVERALL_GLOBALS.BAR_COLOR_CROSSLINK + 
		"; stroke-width: 1; fill-color: " + _OVERALL_GLOBALS.BAR_COLOR_CROSSLINK + "";
	
	_OVERALL_GLOBALS.BAR_STYLE_LOOPLINK = 
		"color: " + _OVERALL_GLOBALS.BAR_COLOR_LOOPLINK +
		"; stroke-color: " + _OVERALL_GLOBALS.BAR_COLOR_LOOPLINK + 
		"; stroke-width: 1; fill-color: " + _OVERALL_GLOBALS.BAR_COLOR_LOOPLINK + "";
	
	_OVERALL_GLOBALS.BAR_STYLE_UNLINKED = 
		"color: " + _OVERALL_GLOBALS.BAR_COLOR_UNLINKED +
		"; stroke-color: " + _OVERALL_GLOBALS.BAR_COLOR_UNLINKED + 
		"; stroke-width: 1; fill-color: " + _OVERALL_GLOBALS.BAR_COLOR_UNLINKED + "";

	_OVERALL_GLOBALS.BAR_STYLE_ALL_COMBINED = 
		"color: " + _OVERALL_GLOBALS.BAR_COLOR_ALL_COMBINED +
		"; stroke-color: " + _OVERALL_GLOBALS.BAR_COLOR_ALL_COMBINED + 
		"; stroke-width: 1; fill-color: " + _OVERALL_GLOBALS.BAR_COLOR_ALL_COMBINED + "";

	var _project_search_ids = null;
	

	var _hash_json_Contents = null;
	
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

	//  Block of "isLoaded" variables.  
	//        These all need to be set to NO in ... when the user clicks "Update from Database" button
	var _summary_Statistics_isLoaded = _IS_LOADED_NO;
	
	var _digestion_Statistics_isLoaded = _IS_LOADED_NO;
	
	var _chargeCount_Statistics_isLoaded = _IS_LOADED_NO;
	var _M_Over_Z_For_PSMs_Statistics_isLoaded = _IS_LOADED_NO;
	
	var _PPM_Error_For_PSMs_Statistics_isLoaded = _IS_LOADED_NO;
	var _PPM_Error_Vs_RetentionTime_For_PSMs_ErrorEstimates_isLoaded = _IS_LOADED_NO;
	var _PPM_Error_Vs_M_over_Z_For_PSMs_ErrorEstimates_isLoaded = _IS_LOADED_NO;
	
	var _peptideLengthsHistogram_isLoaded = _IS_LOADED_NO;
	
	//  Block of "sectionDisplayed" variables.  Lists which sections are currently displayed
	var _summary_Statistics_sectionDisplayed = false;
	var _digestion_Statistics_sectionDisplayed = false;
	var _psm_Statistics_sectionDisplayed = false;
	var _psm_Error_Estimates_sectionDisplayed = false;
	var _peptide_Statistics_sectionDisplayed = false;
	

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
		var objectThis = this;
		setTimeout(function() {
			objectThis.initActual();
		}, 100 );
		
	};
	
	/**
	 * Init page Actual 
	 */
	this.initActual = function() {
		try {
			var objectThis = this;

			this.populateConstantsFromPage();

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


			this.addClickAndOnChangeHandlers();


			//  Default display on page load

			//  TODO  TEMP comment out
			
			this.showSummaryStatistics();

			//  TODO  TEMP Add
			
//			this.showDigestionStatistics();

//			this.showPSMLevelstatistics();

//			this.showPSMErrorEstimates();
			
//			this.showPeptideLevelstatistics();

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

		var $digestion_expand_link = $("#digestion_expand_link");
		$digestion_expand_link.click( function( event ) { 
			try {
				objectThis._digestion_expand_link_Clicked( { clickedThis : this } ); 
				event.preventDefault();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});

		var $digestion_collapse_link = $("#digestion_collapse_link");
		$digestion_collapse_link.click( function( event ) { 
			try {
				objectThis._digestion_collapse_link_Clicked( { clickedThis : this } ); 
				event.preventDefault();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});

		var $psm_level_expand_link = $("#psm_level_expand_link");
		$psm_level_expand_link.click( function( event ) { 
			try {
				objectThis._psm_level_expand_link_Clicked( { clickedThis : this } ); 
				event.preventDefault();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});

		var $psm_level_collapse_link = $("#psm_level_collapse_link");
		$psm_level_collapse_link.click( function( event ) { 
			try {
				objectThis._psm_level_collapse_link_Clicked( { clickedThis : this } ); 
				event.preventDefault();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});

		var $psm_error_estimates_expand_link = $("#psm_error_estimates_expand_link");
		$psm_error_estimates_expand_link.click( function( event ) { 
			try {
				objectThis._psm_error_estimates_expand_link_Clicked( { clickedThis : this } ); 
				event.preventDefault();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});

		var $psm_error_estimates_collapse_link = $("#psm_error_estimates_collapse_link");
		$psm_error_estimates_collapse_link.click( function( event ) { 
			try {
				objectThis._psm_error_estimates_collapse_link_Clicked( { clickedThis : this } ); 
				event.preventDefault();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});

		var $peptide_level_expand_link = $("#peptide_level_expand_link");
		$peptide_level_expand_link.click( function( event ) { 
			try {
				objectThis._peptide_level_expand_link_Clicked( { clickedThis : this } ); 
				event.preventDefault();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});

		var $peptide_level_collapse_link = $("#peptide_level_collapse_link");
		$peptide_level_collapse_link.click( function( event ) { 
			try {
				objectThis._peptide_level_collapse_link_Clicked( { clickedThis : this } ); 
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
	this._update_from_database_button_Clicked = function( params ) {
		this.updatePageForFilterCriteria();
	};

	/**
	 * 
	 */
	this._summary_expand_link_Clicked = function( params ) {
		this.showSummaryStatistics();
	};
	
	/**
	 *  
	 */
	this._summary_collapse_link_Clicked = function( params ) {
		this.hideSummaryStatistics();
	};
	
	/**
	 *  
	 */
	this.showSummaryStatistics = function( params ) {
		
		_summary_Statistics_sectionDisplayed = true;
		
		var $summary_display_block = $("#summary_display_block");
		$summary_display_block.show();
		var $summary_expand_link = $("#summary_expand_link");
		$summary_expand_link.hide();
		var $summary_collapse_link = $("#summary_collapse_link");
		$summary_collapse_link.show();
		
		this.loadSummaryStatisticsIfNeeded();
	};
	
	/**
	 *  
	 */
	this.hideSummaryStatistics = function( params ) {
		
		var $summary_display_block = $("#summary_display_block");
		$summary_display_block.hide();
		var $summary_expand_link = $("#summary_expand_link");
		$summary_expand_link.show();
		var $summary_collapse_link = $("#summary_collapse_link");
		$summary_collapse_link.hide();
		
		_summary_Statistics_sectionDisplayed = false;
	};
	
	/**
	 * 
	 */
	this._digestion_expand_link_Clicked = function( params ) {
		this.showDigestionStatistics();
	};
	
	/**
	 *  
	 */
	this._digestion_collapse_link_Clicked = function( params ) {
		this.hideDigestionStatistics();
	};
	
	/**
	 *  
	 */
	this.showDigestionStatistics = function( params ) {
		
		_digestion_Statistics_sectionDisplayed = true;
		
		var $digestion_display_block = $("#digestion_display_block");
		$digestion_display_block.show();
		var $digestion_expand_link = $("#digestion_expand_link");
		$digestion_expand_link.hide();
		var $digestion_collapse_link = $("#digestion_collapse_link");
		$digestion_collapse_link.show();
		
		this.loadDigestionStatisticsIfNeeded();
	};
	
	/**
	 *  
	 */
	this.hideDigestionStatistics = function( params ) {
		
		var $digestion_display_block = $("#digestion_display_block");
		$digestion_display_block.hide();
		var $digestion_expand_link = $("#digestion_expand_link");
		$digestion_expand_link.show();
		var $digestion_collapse_link = $("#digestion_collapse_link");
		$digestion_collapse_link.hide();
		
		_digestion_Statistics_sectionDisplayed = false;
	};
	

	/**
	 * 
	 */
	this._psm_level_expand_link_Clicked = function( params ) {
		this.showPSMLevelstatistics();
	};
	
	/**
	 *  
	 */
	this._psm_level_collapse_link_Clicked = function( params ) {
		this.hidePSMLevelStatistics();
	};

	/**
	 *  
	 */
	this.showPSMLevelstatistics = function( params ) {

		_psm_Statistics_sectionDisplayed = true;

		var $psm_level_display_block = $("#psm_level_display_block");
		$psm_level_display_block.show();
		var $psm_level_expand_link = $("#psm_level_expand_link");
		$psm_level_expand_link.hide();
		var $psm_level_collapse_link = $("#psm_level_collapse_link");
		$psm_level_collapse_link.show();
		
		this.load_PSM_Level_StatisticsIfNeeded();
	};
	
	/**
	 *  
	 */
	this.hidePSMLevelStatistics = function( params ) {
		
		var $psm_level_display_block = $("#psm_level_display_block");
		$psm_level_display_block.hide();
		var $psm_level_expand_link = $("#psm_level_expand_link");
		$psm_level_expand_link.show();
		var $psm_level_collapse_link = $("#psm_level_collapse_link");
		$psm_level_collapse_link.hide();
		
		_psm_Statistics_sectionDisplayed = false;
	};

	/**
	 * 
	 */
	this._psm_error_estimates_expand_link_Clicked = function( params ) {
		this.showPSMErrorEstimates();
	};
	
	/**
	 *  
	 */
	this._psm_error_estimates_collapse_link_Clicked = function( params ) {
		this.hidePSMErrorEstimates();
	};

	/**
	 *  
	 */
	this.showPSMErrorEstimates = function( params ) {

		_psm_Error_Estimates_sectionDisplayed = true;

		var $psm_error_estimates_display_block = $("#psm_error_estimates_display_block");
		$psm_error_estimates_display_block.show();
		var $psm_error_estimates_expand_link = $("#psm_error_estimates_expand_link");
		$psm_error_estimates_expand_link.hide();
		var $psm_error_estimates_collapse_link = $("#psm_error_estimates_collapse_link");
		$psm_error_estimates_collapse_link.show();
		
		this.load_PSM_ErrorEstimatesIfNeeded();
	};
	
	/**
	 *  
	 */
	this.hidePSMErrorEstimates = function( params ) {
		
		var $psm_error_estimates_display_block = $("#psm_error_estimates_display_block");
		$psm_error_estimates_display_block.hide();
		var $psm_error_estimates_expand_link = $("#psm_error_estimates_expand_link");
		$psm_error_estimates_expand_link.show();
		var $psm_error_estimates_collapse_link = $("#psm_error_estimates_collapse_link");
		$psm_error_estimates_collapse_link.hide();
		
		_psm_Error_Estimates_sectionDisplayed = false;
	};

	/**
	 * 
	 */
	this._peptide_level_expand_link_Clicked = function( params ) {
		this.showPeptideLevelstatistics();
	};
	
	/**
	 *  
	 */
	this._peptide_level_collapse_link_Clicked = function( params ) {
		this.hidePeptideLevelStatistics();
	};
	

	/**
	 *  
	 */
	this.showPeptideLevelstatistics = function( params ) {

		_peptide_Statistics_sectionDisplayed = true;

		var $peptide_level_display_block = $("#peptide_level_display_block");
		$peptide_level_display_block.show();
		var $peptide_level_expand_link = $("#peptide_level_expand_link");
		$peptide_level_expand_link.hide();
		var $peptide_level_collapse_link = $("#peptide_level_collapse_link");
		$peptide_level_collapse_link.show();
		
		this.load_Peptide_Level_StatisticsIfNeeded();
	};
	
	/**
	 *  
	 */
	this.hidePeptideLevelStatistics = function( params ) {
		
		var $peptide_level_display_block = $("#peptide_level_display_block");
		$peptide_level_display_block.hide();
		var $peptide_level_expand_link = $("#peptide_level_expand_link");
		$peptide_level_expand_link.show();
		var $peptide_level_collapse_link = $("#peptide_level_collapse_link");
		$peptide_level_collapse_link.hide();
		
		_peptide_Statistics_sectionDisplayed = false;
	};
	
	//////////////////
	
	/**
	 *  
	 */
	this.updatePageForFilterCriteria = function() {
		
		this.updatePageFromFiltersToURLHashJSVarsAndPageData();

		this.clearAllDisplayedDataAndCharts();
		
		this.loadDataForDisplayedDataAndCharts();
		
	};
	
	/**
	 *  
	 */
	this.clearAllDisplayedDataAndCharts = function() {
		
		this.clearSummaryStatistics();
		this.clearDigestionStatistics();
		this.clearPSM_Level_Statistics();
		this.clearPSM_ErrorEstimates();
		this.clear_Peptide_Level_Statistics();
	};
	

	/**
	 *  
	 */
	this.loadDataForDisplayedDataAndCharts = function() {
		if ( _summary_Statistics_sectionDisplayed ) {
			this.loadSummaryStatisticsIfNeeded();
		}
		if ( _digestion_Statistics_sectionDisplayed ) {
			this.loadDigestionStatisticsIfNeeded();
		}
		if ( _psm_Statistics_sectionDisplayed ) {
			this.load_PSM_Level_StatisticsIfNeeded();
		}
		if ( _psm_Error_Estimates_sectionDisplayed ) {
			this.load_PSM_ErrorEstimatesIfNeeded();
		}
		if ( _peptide_Statistics_sectionDisplayed ) {
			this.load_Peptide_Level_StatisticsIfNeeded();
		}
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
			//  Update filter data held in JS variable
			_hash_json_Contents = dataFromFiltersResult;
			
			this.updateURLHashWithJSONObject( dataFromFiltersResult );

		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
	
	/**
	 * 
	 */
	this.updateURLHashWithJSONObject = function( jsonObject ) {
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
					cutoffs : this.getCutoffDefaultsFromPage(),
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
			json.cutoffs = getCutoffDefaultsFromPage();
		}
		//  START: Special update to allow projectSearchId values to be added or removed from URL
		//  Update cutoffs to add defaults for search ids in defaults but not in cutoffs
		//  Update cutoffs to remove search ids not in defaults but in cutoffs
		var cutoffs_Searches = json.cutoffs.searches;
		var cutoffDefaultsFromPage = this.getCutoffDefaultsFromPage();
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
		return json;
	}

	/**
	 * 
	 */
	this.getCutoffDefaultsFromPage = function() {
		var $cutoffValuesRootLevelCutoffDefaults = $("#cutoffValuesRootLevelCutoffDefaults");
		var cutoffValuesRootLevelCutoffDefaultsString = $cutoffValuesRootLevelCutoffDefaults.val();
		try {
			var cutoffValuesRootLevelCutoffDefaults = JSON.parse( cutoffValuesRootLevelCutoffDefaultsString );
		} catch( e2 ) {
			throw Error( "Failed to parse cutoffValuesRootLevelCutoffDefaults string as JSON.  " +
					"Error Message: " + e2.message +
					".  cutoffValuesRootLevelCutoffDefaultsString: |" +
					cutoffValuesRootLevelCutoffDefaultsString +
			"|" );
		}
		return cutoffValuesRootLevelCutoffDefaults;
	};
	
	///////////////////////////////////////////
	
	///////////////////////////////////////////
	
	/////////   Summary Statistics

	/**
	 * Load the data for the Summary Statistics section
	 */
	this.loadSummaryStatisticsIfNeeded = function() {
		
		this.loadSummaryStatisticsCountIfNeeded();
	};
	
	/**
	 * Clear the data for the Summary Statistics section
	 */
	this.clearSummaryStatistics = function() {
		
		this.clearSummaryStatisticsCount();
	};
	
	/**
	 * Clear data for Summary_Statistics_Counts
	 */
	this.clearSummaryStatisticsCount = function() {
		
		_summary_Statistics_isLoaded = _IS_LOADED_NO;

		//  PSM Summary Summary 
		var $Summary_Statistics_CountsBlock = $("#Summary_Statistics_CountsBlock");
		var $Summary_Statistics_CountsBlock_chart_container_jq = $("#Summary_Statistics_CountsBlock .chart_container_jq"); 
		$Summary_Statistics_CountsBlock_chart_container_jq.empty();
		
		if ( _loadSummaryStatisticsCountActiveAjax ) {
			_loadSummaryStatisticsCountActiveAjax.abort();
			_loadSummaryStatisticsCountActiveAjax = null;
		}
	};
	

	/**
	 * If not currently loaded, call loadSummaryStatisticsCount()
	 */
	this.loadSummaryStatisticsCountIfNeeded = function() {
		
		if ( _summary_Statistics_isLoaded === _IS_LOADED_NO ) {
			this.loadSummaryStatisticsCount();
		}
	};
	
	/**
	 * Keep this in sync with the number of charts actually created in this.loadSummaryStatisticsCount(...)
	 */
	var _SUMMARY_STATISTICS_CHART_COUNT = 3;

	var _loadSummaryStatisticsCountActiveAjax = null;
	
	/**
	 * Load the data for SummaryStatisticsCount
	 */
	this.loadSummaryStatisticsCount = function() {
		var objectThis = this;
		
		_summary_Statistics_isLoaded = _IS_LOADED_LOADING;
		
		// Block for "Summary Statistics"
		var $Summary_Statistics_CountsBlock = $("#Summary_Statistics_CountsBlock");
		if ( $Summary_Statistics_CountsBlock.length === 0 ) {
			throw Error( "unable to find HTML element with id 'Summary_Statistics_CountsBlock'" );
		}
		$Summary_Statistics_CountsBlock.empty();

		for ( var counter = 0; counter < _SUMMARY_STATISTICS_CHART_COUNT; counter++ ) {
			//  Add empty chart with Loading message
			var $chart_outer_container_jq = this._addChartOuterTemplate( { $chart_group_container_table_jq : $Summary_Statistics_CountsBlock } );
			this._placeEmptyDummyChartForMessage( { 
				$chart_outer_container_jq : $chart_outer_container_jq, 
				//				linkType : selectedLinkType, 
				messagePrefix:  _DUMMY_CHART_STATUS_TEXT_PREFIX_LOADING,
				messageSuffix:  _DUMMY_CHART_STATUS_TEXT_SUFFIX_LOADING
			} );
		}
				
		var hash_json_field_Contents_JSONString = JSON.stringify( _hash_json_Contents );
		var ajaxRequestData = {
				project_search_id : _project_search_ids,
				filterCriteria : hash_json_field_Contents_JSONString
		};
		
		if ( _loadSummaryStatisticsCountActiveAjax ) {
			_loadSummaryStatisticsCountActiveAjax.abort();
			_loadSummaryStatisticsCountActiveAjax = null;
		}
		
		//  Set to returned jQuery XMLHttpRequest (jqXHR) object
		_loadSummaryStatisticsCountActiveAjax =
			$.ajax({
				url : contextPathJSVar + "/services/qc/dataPage/summaryStatistics",
				traditional: true,  //  Force traditional serialization of the data sent
				//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
				//   So project_search_ids array is passed as "project_search_ids=<value>" which is what Jersey expects
				data : ajaxRequestData,  // The data sent as params on the URL
				dataType : "json",
				success : function( ajaxResponseData ) {
					try {
						_loadSummaryStatisticsCountActiveAjax = null;
						var responseParams = {
								ajaxResponseData : ajaxResponseData, 
								ajaxRequestData : ajaxRequestData
						};
						objectThis.loadSummaryStatisticsCountProcessResponse( responseParams );
					} catch( e ) {
						reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
						throw e;
					}
				},
				failure: function(errMsg) {
					_loadSummaryStatisticsCountActiveAjax = null;
					handleAJAXFailure( errMsg );
				},
				error : function(jqXHR, textStatus, errorThrown) {
					_loadSummaryStatisticsCountActiveAjax = null;
					if ( objectThis._passAJAXErrorTo_handleAJAXError(jqXHR, textStatus, errorThrown) ) {
						handleAJAXError(jqXHR, textStatus, errorThrown);
					}
				}
			});
	
	};
	

	/**
	 * Load the data for Summary_Statistics_Counts
	 */
	this.loadSummaryStatisticsCountProcessResponse = function( params ) {
		var ajaxResponseData = params.ajaxResponseData;
		var ajaxRequestData = params.ajaxRequestData;
		
		var qc_SummaryCountsResults = ajaxResponseData.qc_SummaryCountsResults;
		var resultsPerLinkTypeList = qc_SummaryCountsResults.resultsPerLinkTypeList;
		
		// Block for "Summary Statistics"
		var $Summary_Statistics_CountsBlock = $("#Summary_Statistics_CountsBlock");
		if ( $Summary_Statistics_CountsBlock.length === 0 ) {
			throw Error( "unable to find HTML element with id 'Summary_Statistics_CountsBlock'" );
		}
		$Summary_Statistics_CountsBlock.empty();

		//  Build data for charts
		var psmCountPerType = [];
		var reportedPeptideCountPerType = [];
		var proteinSequenceIdCountPerType = [];
		
		var foundDataForAtLeastOneLinkType = false;
		
		resultsPerLinkTypeList.forEach( function ( currentArrayValue, index, array ) {
			var entry = currentArrayValue;
			
			if ( entry.psmCount === 0 ) {
				//  No data for this entry so skip it
				return;
			}
			
			foundDataForAtLeastOneLinkType = true;
			
			var psmCountSingleType = { 
					linkType : entry.linkType,
					count : entry.psmCount
			};

			var reportedPeptideCountSingleType = { 
					linkType : entry.linkType,
					count : entry.uniqueReportedPeptideCount
			};

			var proteinSequenceIdCountSingleType = { 
					linkType : entry.linkType,
					count : entry.uniqueProteinSequenceIdCount
			};
			
			psmCountPerType.push( psmCountSingleType );
			reportedPeptideCountPerType.push( reportedPeptideCountSingleType );
			proteinSequenceIdCountPerType.push( proteinSequenceIdCountSingleType );
			
		}, this /* passed to function as this */ );
		
		if ( ! foundDataForAtLeastOneLinkType ) {
			
			for ( var counter = 0; counter < _SUMMARY_STATISTICS_CHART_COUNT; counter++ ) {

				var $chart_outer_container_jq =
					this._addChartOuterTemplate( { $chart_group_container_table_jq : $Summary_Statistics_CountsBlock } );

				//  Add empty chart with No Data message
				this._placeEmptyDummyChartForMessage( { 
					$chart_outer_container_jq : $chart_outer_container_jq, 
//					linkType : selectedLinkType, 
					messagePrefix:  _DUMMY_CHART_STATUS_TEXT_PREFIX_NO_DATA,
					messageSuffix:  _DUMMY_CHART_STATUS_TEXT_SUFFIX_NO_DATA
				} );
			}
			
			//  Exit since no data found
			
			return;  //  EARLY EXIT 
		}
		
		
		//  Keep _SUMMARY_STATISTICS_CHART_COUNT in sync with the actual number of charts created here

		var $chart_outer_container_jq =
			this._addChartOuterTemplate( { $chart_group_container_table_jq : $Summary_Statistics_CountsBlock } );
		
		var $chart_container_jq = this._addChartInnerTemplate( { $chart_outer_container_jq : $chart_outer_container_jq } );
		
		this._addSummaryChart( { 
			chartTitle : 'PSM Count',
			dataWithOneElementPerType: psmCountPerType, 
			$chartContainer : $chart_container_jq } );

		
		var $chart_outer_container_jq =
			this._addChartOuterTemplate( { $chart_group_container_table_jq : $Summary_Statistics_CountsBlock } );
		
		var $chart_container_jq = this._addChartInnerTemplate( { $chart_outer_container_jq : $chart_outer_container_jq } );
		
		this._addSummaryChart( { 
			chartTitle : 'Peptide Count',
			dataWithOneElementPerType: reportedPeptideCountPerType, 
			$chartContainer : $chart_container_jq } );

		
		var $chart_outer_container_jq =
			this._addChartOuterTemplate( { $chart_group_container_table_jq : $Summary_Statistics_CountsBlock } );
		
		var $chart_container_jq = this._addChartInnerTemplate( { $chart_outer_container_jq : $chart_outer_container_jq } );
		
		this._addSummaryChart( { 
			chartTitle : 'Protein Count',
			dataWithOneElementPerType: proteinSequenceIdCountPerType, 
			$chartContainer : $chart_container_jq } );
		
		chartDownload.addDownloadClickHandlers( { $chart_outer_container_for_download_jq :  $Summary_Statistics_CountsBlock } );
		// Add tooltips for download links
		addToolTips( $Summary_Statistics_CountsBlock );
	};
	
	/**
	 * Add Summary Chart
	 */
	this._addSummaryChart = function( params ) {
		var chartTitleParam = params.chartTitle;
		var dataWithOneElementPerType = params.dataWithOneElementPerType;
		var $chartContainer = params.$chartContainer;
		
		var combinedCount = 0;
		
		dataWithOneElementPerType.forEach( function ( currentArrayValue, index, array ) {
			var entry = currentArrayValue;
			combinedCount += entry.count;
		}, this /* passed to function as this */ );

		var $chart_container_jqHTMLElement = $chartContainer[ 0 ];

		var totalForCombined = { 
				linkType : _link_type_combined_LOWER_CASE_constant, 
				count : combinedCount };
		
		dataWithOneElementPerType.push( totalForCombined );

		var chartTitle = chartTitleParam;

//		chart data for Google charts
		var chartData = [];

		var barColors = [  ]; // must be an array

		var chartDataHeaderEntry = [ 'Link Type', "Count", 
			{ role: 'style' },  // Style of the bar 
			{role: "tooltip", 'p': {'html': true} }
			, {type: 'string', role: 'annotation'}
			]; 
		chartData.push( chartDataHeaderEntry );

		var maxYvalue = 0;

		dataWithOneElementPerType.forEach( function ( currentArrayValue, index, array ) {
			var totalForLinkType = currentArrayValue;

			var linkType = totalForLinkType.linkType;
			var colorAndbarColor = this.getColorAndBarColorFromLinkType( linkType );

			var chartY = totalForLinkType.count;

			if ( chartY === undefined ) {
				chartY = 0;
			}

			var tooltipText = "<div  style='padding: 4px;'>Count: " + totalForLinkType.count + "</div>";
			var entryAnnotationText = chartY;

			var chartEntry = [ 
				linkType,
				chartY, 
				//  Style of bar
				colorAndbarColor.barColor,
				//  Tool Tip
				tooltipText
				,entryAnnotationText
				];
			chartData.push( chartEntry );
			if ( chartY > maxYvalue ) {
				maxYvalue = chartY;
			}

			barColors.push( colorAndbarColor.color );

		}, this /* passed to function as this */ );

//		var vAxisTicks = this._get___________TickMarks( { maxValue : maxYvalue } );

		var optionsFullsize = {
				//  Overridden for Specific elements like Chart Title and X and Y Axis labels
				fontSize: _MISSED_CLEAVAGE_CHART_GLOBALS._CHART_DEFAULT_FONT_SIZE,  //  Default font size - using to set font size for tick marks.

				title: chartTitle, // Title above chart
				titleTextStyle: {
			    	color : _PROXL_DEFAULT_FONT_COLOR, //  Set default font color
//					color: <string>,    // any HTML string color ('red', '#cc00cc')
//					fontName: <string>, // i.e. 'Times New Roman'
					fontSize: _MISSED_CLEAVAGE_CHART_GLOBALS._TITLE_FONT_SIZE, // 12, 18 whatever you want (don't specify px)
//					bold: <boolean>,    // true or false
//					italic: <boolean>   // true of false
				},
				//  X axis label below chart
				hAxis: { title: 'Link Type', titleTextStyle: { color: 'black', fontSize: _MISSED_CLEAVAGE_CHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
				},  
				//  Y axis label left of chart
				vAxis: { title: 'Count', titleTextStyle: { color: 'black', fontSize: _MISSED_CLEAVAGE_CHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
				,baseline: 0     // always start at zero
//				,ticks: vAxisTicks
//				,maxValue : maxChargeCount
				},
				legend: { position: 'none' }, //  position: 'none':  Don't show legend of bar colors in upper right corner
//				width : 500, 
//				height : 300,   // width and height of chart, otherwise controlled by enclosing div
//				colors: barColors,  //  Assigned to each bar
				tooltip: {isHtml: true}
//				,chartArea : { left : 140, top: 60, 
//				width: objectThis.RETENTION_TIME_COUNT_CHART_WIDTH - 200 ,  //  was 720 as measured in Chrome
//				height : objectThis.RETENTION_TIME_COUNT_CHART_HEIGHT - 120 }  //  was 530 as measured in Chrome
		};        
//		create the chart
		var data = google.visualization.arrayToDataTable( chartData );
		var chartFullsize = new google.visualization.ColumnChart( $chart_container_jqHTMLElement );
		chartFullsize.draw(data, optionsFullsize);

	};
	
	
	
	///////////////////////////////////////////
	
	/////////    Digestion Statistics

	/**
	 * Load the data for the Digestion Statistics section
	 */
	this.loadDigestionStatisticsIfNeeded = function() {
		
		this.loadMissingCleavageReportedPeptidesCountIfNeeded();
	};
	
	/**
	 * Clear the data for the Digestion Statistics section
	 */
	this.clearDigestionStatistics = function() {
		
		this.clearMissingCleavageReportedPeptidesCount();
	};
	
	/**
	 * Clear data for MissingCleavageReportedPeptidesCount
	 */
	this.clearMissingCleavageReportedPeptidesCount = function() {
		
		_digestion_Statistics_isLoaded = _IS_LOADED_NO;

		var $missingCleavageReportedPeptidesCountBlock = $("#missingCleavageReportedPeptidesCountBlock");
		$missingCleavageReportedPeptidesCountBlock.empty();
		
		if ( _loadMissingCleavageReportedPeptidesCountActiveAjax ) {
			_loadMissingCleavageReportedPeptidesCountActiveAjax.abort();
			_loadMissingCleavageReportedPeptidesCountActiveAjax = null;
		}
	};
	

	/**
	 * If not currently loaded, call loadMissingCleavageReportedPeptidesCount()
	 */
	this.loadMissingCleavageReportedPeptidesCountIfNeeded = function() {
		
		if ( _digestion_Statistics_isLoaded === _IS_LOADED_NO ) {
			this.loadMissingCleavageReportedPeptidesCount();
		}
	};

	var _loadMissingCleavageReportedPeptidesCountActiveAjax = null;
	
	/**
	 * Load the data for MissingCleavageReportedPeptidesCount
	 */
	this.loadMissingCleavageReportedPeptidesCount = function() {
		var objectThis = this;
		
		_digestion_Statistics_isLoaded = _IS_LOADED_LOADING;
		
		// Add 1 dummy chart for place holder
		var $missingCleavageReportedPeptidesCountBlock = $("#missingCleavageReportedPeptidesCountBlock");
		if ( $missingCleavageReportedPeptidesCountBlock.length === 0 ) {
			throw Error( "unable to find HTML element with id 'missingCleavageReportedPeptidesCountBlock'" );
		}
		$missingCleavageReportedPeptidesCountBlock.empty();
		
		var $chart_outer_container_jq =
			this._addChartOuterTemplate( { $chart_group_container_table_jq : $missingCleavageReportedPeptidesCountBlock } );
		
		//  Add empty chart with Loading message
		this._placeEmptyDummyChartForMessage( { 
			$chart_outer_container_jq : $chart_outer_container_jq, 
//			linkType : selectedLinkType, 
			messagePrefix:  _DUMMY_CHART_STATUS_TEXT_PREFIX_LOADING,
			messageSuffix:  _DUMMY_CHART_STATUS_TEXT_SUFFIX_LOADING
		} );
				
		var hash_json_field_Contents_JSONString = JSON.stringify( _hash_json_Contents );
		var ajaxRequestData = {
				project_search_id : _project_search_ids,
				filterCriteria : hash_json_field_Contents_JSONString
		};
		
		if ( _loadMissingCleavageReportedPeptidesCountActiveAjax ) {
			_loadMissingCleavageReportedPeptidesCountActiveAjax.abort();
			_loadMissingCleavageReportedPeptidesCountActiveAjax = null;
		}
		
		//  Set to returned jQuery XMLHttpRequest (jqXHR) object
		_loadMissingCleavageReportedPeptidesCountActiveAjax =
			$.ajax({
				url : contextPathJSVar + "/services/qc/dataPage/missingCleavages",
				traditional: true,  //  Force traditional serialization of the data sent
				//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
				//   So project_search_ids array is passed as "project_search_ids=<value>" which is what Jersey expects
				data : ajaxRequestData,  // The data sent as params on the URL
				dataType : "json",
				success : function( ajaxResponseData ) {
					try {
						_loadMissingCleavageReportedPeptidesCountActiveAjax = null;
						var responseParams = {
								ajaxResponseData : ajaxResponseData, 
								ajaxRequestData : ajaxRequestData
//								,
//								topTRelement : topTRelement
						};
						objectThis.loadMissingCleavageReportedPeptidesCountProcessResponse( responseParams );
//						$topTRelement.data( _DATA_LOADED_DATA_KEY, true );
					} catch( e ) {
						reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
						throw e;
					}
				},
				failure: function(errMsg) {
					_loadMissingCleavageReportedPeptidesCountActiveAjax = null;
					handleAJAXFailure( errMsg );
				},
				error : function(jqXHR, textStatus, errorThrown) {
					_loadMissingCleavageReportedPeptidesCountActiveAjax = null;
					if ( objectThis._passAJAXErrorTo_handleAJAXError(jqXHR, textStatus, errorThrown) ) {
						handleAJAXError(jqXHR, textStatus, errorThrown);
					}
				}
			});
	
	};
	
	
	/**
	 * Load the data for MissingCleavageReportedPeptidesCount
	 */
	this.loadMissingCleavageReportedPeptidesCountProcessResponse = function( params ) {
		var ajaxResponseData = params.ajaxResponseData;
		var ajaxRequestData = params.ajaxRequestData;
		
		var missingCleavageReportedPeptidesCountForLinkTypeList = ajaxResponseData.missingCleavageReportedPeptidesCountForLinkTypeList;
		
		var $missingCleavageReportedPeptidesCountBlock = $("#missingCleavageReportedPeptidesCountBlock");
		if ( $missingCleavageReportedPeptidesCountBlock.length === 0 ) {
			throw Error( "unable to find HTML element with id 'missingCleavageReportedPeptidesCountBlock'" );
		}

		$missingCleavageReportedPeptidesCountBlock.empty();

		//  Build data for charts
		var peptidesWithMissedCleavagePerType = [];
		var missedCleavagesPerType = [];
		var missedCleavagePSMCountPerType = [];
		
		var foundDataForAtLeastOneLinkType = false;
		
		for ( var index = 0; index < missingCleavageReportedPeptidesCountForLinkTypeList.length; index++ ) {
			var entry = missingCleavageReportedPeptidesCountForLinkTypeList[ index ];
			
			if ( entry.totalReportedPeptideCount === 0 || entry.totalPSMCount === 0 ) {
				//  No data for this entry so skip it
				continue;
			}
			
			foundDataForAtLeastOneLinkType = true;
			
			var peptidesWithMissedCleavageTooltip = 
				entry.missedCleavageReportedPeptideCount + 
				" Peptides w/ Missed Cleavage / " + 
				entry.totalReportedPeptideCount + " Total Peptides"; 

			var peptidesWithMissedCleavageSingleType = { 
					linkType : entry.linkType,
					count : entry.missedCleavageReportedPeptideCount,
					totalCount : entry.totalReportedPeptideCount,
					tooltip: peptidesWithMissedCleavageTooltip 
			};

			var missedCleavageTooltip = 
				entry.missedCleavageCount + 
				" Missed Cleavages / " + 
				entry.totalReportedPeptideCount + " Total Peptides"; 

			var missedCleavageSingleType = { 
					linkType : entry.linkType,
					count : entry.missedCleavageCount,
					totalCount : entry.totalReportedPeptideCount,
					tooltip: missedCleavageTooltip
			};

			var missedCleavagePSMCountTooltip = 
				entry.missedCleavagePSMCount + 
				" Missed Cleavage PSM Count / " + 
				entry.totalPSMCount + " Total PSMs"; 
			
			var missedCleavagePSMCountSingleType = { 
					linkType : entry.linkType,
					count : entry.missedCleavagePSMCount,
					totalCount : entry.totalPSMCount,
					tooltip: missedCleavagePSMCountTooltip
			};
			
			peptidesWithMissedCleavagePerType.push( peptidesWithMissedCleavageSingleType );
			missedCleavagesPerType.push( missedCleavageSingleType );
			missedCleavagePSMCountPerType.push( missedCleavagePSMCountSingleType );
		}
		
		if ( ! foundDataForAtLeastOneLinkType ) {

			var $chart_outer_container_jq =
				this._addChartOuterTemplate( { $chart_group_container_table_jq : $missingCleavageReportedPeptidesCountBlock } );
			
			//  Add empty chart with No Data message
			this._placeEmptyDummyChartForMessage( { 
				$chart_outer_container_jq : $chart_outer_container_jq, 
//				linkType : selectedLinkType, 
				messagePrefix:  _DUMMY_CHART_STATUS_TEXT_PREFIX_NO_DATA,
				messageSuffix:  _DUMMY_CHART_STATUS_TEXT_SUFFIX_NO_DATA
			} );
			
			//  Exit since no data found
			
			return;  //  EARLY EXIT 
		}
		

		var $chart_outer_container_jq =
			this._addChartOuterTemplate( { $chart_group_container_table_jq : $missingCleavageReportedPeptidesCountBlock } );
		
		var $chart_container_jq = this._addChartInnerTemplate( { $chart_outer_container_jq : $chart_outer_container_jq } );
		
		this._addMissedCleavageChart( { 
			chartTitle : 'Fraction Peptides w/ Missed Cleavages',
			dataWithOneElementPerType: peptidesWithMissedCleavagePerType, 
			$chartContainer : $chart_container_jq } );

		
		var $chart_outer_container_jq =
			this._addChartOuterTemplate( { $chart_group_container_table_jq : $missingCleavageReportedPeptidesCountBlock } );
		
		var $chart_container_jq = this._addChartInnerTemplate( { $chart_outer_container_jq : $chart_outer_container_jq } );
		
		this._addMissedCleavageChart( { 
			chartTitle : 'Missed Cleavages Per Peptide',
			dataWithOneElementPerType: missedCleavagesPerType, 
			$chartContainer : $chart_container_jq } );

		
		var $chart_outer_container_jq =
			this._addChartOuterTemplate( { $chart_group_container_table_jq : $missingCleavageReportedPeptidesCountBlock } );
		
		var $chart_container_jq = this._addChartInnerTemplate( { $chart_outer_container_jq : $chart_outer_container_jq } );
		
		this._addMissedCleavageChart( { 
			chartTitle : 'Fraction PSMs w/ Missed Cleavages',
			dataWithOneElementPerType: missedCleavagePSMCountPerType, 
			$chartContainer : $chart_container_jq } );
		
		chartDownload.addDownloadClickHandlers( { $chart_outer_container_for_download_jq :  $missingCleavageReportedPeptidesCountBlock } );

	};
	
	/**
	 * Overridden for Specific elements like Chart Title and X and Y Axis labels
	 */
	var _MISSED_CLEAVAGE_CHART_GLOBALS = {
			_CHART_DEFAULT_FONT_SIZE : 12,  //  Default font size - using to set font size for tick marks.
			_TITLE_FONT_SIZE : 15, // In PX
			_AXIS_LABEL_FONT_SIZE : 14, // In PX
			_TICK_MARK_TEXT_FONT_SIZE : 14 // In PX
	}

	/**
	 * 
	 */
	this._addMissedCleavageChart = function( params ) {

		var dataWithOneElementPerType = params.dataWithOneElementPerType;
		var chartTitle = params.chartTitle;
		var $chartContainer = params.$chartContainer;

		//  chart data for Google charts
		var chartData = [];
		
		var barColors = [  ]; // must be an array

		var chartDataHeaderEntry = [ 'Link Type', "Percentage", 
			{ role: 'style' },  // Style of the bar 
			{role: "tooltip", 'p': {'html': true} }
			, {type: 'string', role: 'annotation'}
		]; 
		chartData.push( chartDataHeaderEntry );

		var maxYvalue = 0;

		for ( var index = 0; index < dataWithOneElementPerType.length; index++ ) {
			var dataForOneLinkType = dataWithOneElementPerType[ index ];
			
			var linkType = dataForOneLinkType.linkType;
			var colorAndbarColor = this.getColorAndBarColorFromLinkType( linkType );
			
			var chartY = dataForOneLinkType.count / dataForOneLinkType.totalCount;
			
			if ( chartY === undefined ) {
				chartY = 0;
			}
			
			var tooltipText = "<div  style='padding: 4px;'>" + dataForOneLinkType.tooltip + "</div>";
			var entryAnnotationText = chartY.toFixed( 2 );;
			
			var chartEntry = [ 
				linkType,
				chartY, 
				//  Style of bar
				colorAndbarColor.barColor,
				//  Tool Tip
				tooltipText
				,entryAnnotationText
				 ];
			chartData.push( chartEntry );
			if ( chartY > maxYvalue ) {
				maxYvalue = chartY;
			}
			
			barColors.push( colorAndbarColor.color );

		}
		
//		var vAxisTicks = this._get___________TickMarks( { maxValue : maxYvalue } );

		var optionsFullsize = {
			//  Overridden for Specific elements like Chart Title and X and Y Axis labels
				fontSize: _MISSED_CLEAVAGE_CHART_GLOBALS._CHART_DEFAULT_FONT_SIZE,  //  Default font size - using to set font size for tick marks.
							
				title: chartTitle, // Title above chart
			    titleTextStyle: {
			    	color : _PROXL_DEFAULT_FONT_COLOR, //  Set default font color
//			        color: <string>,    // any HTML string color ('red', '#cc00cc')
//			        fontName: <string>, // i.e. 'Times New Roman'
			        fontSize: _MISSED_CLEAVAGE_CHART_GLOBALS._TITLE_FONT_SIZE, // 12, 18 whatever you want (don't specify px)
//			        bold: <boolean>,    // true or false
//			        italic: <boolean>   // true of false
			    },
				//  X axis label below chart
				hAxis: { title: 'Link Type', titleTextStyle: { color: 'black', fontSize: _MISSED_CLEAVAGE_CHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
				},  
				//  Y axis label left of chart
				vAxis: { title: 'Fraction', titleTextStyle: { color: 'black', fontSize: _MISSED_CLEAVAGE_CHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
					,baseline: 0     // always start at zero
//					,ticks: vAxisTicks
//					,maxValue : maxChargeCount
				},
				legend: { position: 'none' }, //  position: 'none':  Don't show legend of bar colors in upper right corner
//				width : 500, 
//				height : 300,   // width and height of chart, otherwise controlled by enclosing div
//				colors: barColors,  //  Assigned to each bar
				tooltip: {isHtml: true}
//				,chartArea : { left : 140, top: 60, 
//				width: objectThis.RETENTION_TIME_COUNT_CHART_WIDTH - 200 ,  //  was 720 as measured in Chrome
//				height : objectThis.RETENTION_TIME_COUNT_CHART_HEIGHT - 120 }  //  was 530 as measured in Chrome
		};        
		// create the chart
		var data = google.visualization.arrayToDataTable( chartData );
		var chartFullsize = new google.visualization.ColumnChart( $chartContainer[0] );
		chartFullsize.draw(data, optionsFullsize);
		
		//  Temp code to find <rect> that are the actual data columns
		//     Changing them to green to allow show that they are only the data columns and not other <rect> in the <svg>
		
//		var $rectanglesInChart_All = $chartContainer.find("rect");
//		
//		$rectanglesInChart_All.each( function() {
//			var $rectangleInChart = $( this );
//			var rectangleFillColor = $rectangleInChart.attr("fill");
//			if ( rectangleFillColor !== undefined ) {
//				if ( rectangleFillColor.toLowerCase() === barColor.toLowerCase() ) {
//					$rectangleInChart.attr("fill","green");
//					var z = 0;
//				}
//			}
//		});
		
	};

	/**
	 * 
	 */
	this._getChargeCountTickMarks = function( params ) {
		var maxValue = params.maxValue;
		if ( maxValue < 5 ) {
			var tickMarks = [ 0 ];
			for ( var counter = 1; counter <= maxValue; counter++ ) {
				tickMarks.push( counter );
			}
			return tickMarks;
		}
		return undefined; //  Use defaults
	};

	///////////////////////////////////////////////////////
	
	///////    PSM Statistics  ////////////////////
	

	/**
	 * Clear the data for the PSM section
	 */
	this.clearPSM_Level_Statistics = function() {
		
		this.clearChargeCount();
		this.clear_M_Over_Z_For_PSMs_Histogram();
	};
	
	
	/**
	 * Load the data for PSM section
	 */
	this.load_PSM_Level_StatisticsIfNeeded = function() {
		this.loadChargeCountIfNeeded();
		this.load_M_Over_Z_For_PSMs_HistogramIfNeeded();
	};
	
	//////////////////////////////
	
	////  PSM  Charge Count and PSM Summary (Summary created from ChargeCount Data)

	/**
	 * Clear data for ChargeCount and Summary (Summary created from ChargeCount Data)
	 */
	this.clearChargeCount = function() {
		
		_chargeCount_Statistics_isLoaded = _IS_LOADED_NO;

		var $PSMChargeStatesCountsBlock = $("#PSMChargeStatesCountsBlock");
		$PSMChargeStatesCountsBlock.empty();
		
		if ( _loadloadChargeCountActiveAjax ) {
			_loadloadChargeCountActiveAjax.abort();
			_loadloadChargeCountActiveAjax = null;
		}
	};
	
	/**
	 * If not loaded, call this.loadChargeCount();
	 */
	this.loadChargeCountIfNeeded = function() {
		if ( _chargeCount_Statistics_isLoaded === _IS_LOADED_NO ) {
			this.loadChargeCount();
		}
	};
	
	var _loadloadChargeCountActiveAjax = null;
	
	/**
	 * Load the data for ChargeCount  and Summary (Summary created from ChargeCount Data)
	 */
	this.loadChargeCount = function() {
		var objectThis = this;
		
		_chargeCount_Statistics_isLoaded = _IS_LOADED_LOADING;
		
		var $PSMChargeStatesCountsBlock = $("#PSMChargeStatesCountsBlock");
		$PSMChargeStatesCountsBlock.empty();

		var selectedLinkTypes = _hash_json_Contents.linkTypes;
		
		// Show cells for selected link types
		selectedLinkTypes.forEach( function ( currentArrayValue, index, array ) {
			var selectedLinkType = currentArrayValue;

			//  Add empty chart with Loading message
			var $chart_outer_container_jq =
				this._addChartOuterTemplate( { linkType : selectedLinkType, $chart_group_container_table_jq : $PSMChargeStatesCountsBlock } );

			//  Add empty chart with Loading message
			this._placeEmptyDummyChartForMessage( { 
				$chart_outer_container_jq : $chart_outer_container_jq, 
			//				linkType : selectedLinkType, 
				messagePrefix:  _DUMMY_CHART_STATUS_TEXT_PREFIX_LOADING,
				messageSuffix:  _DUMMY_CHART_STATUS_TEXT_SUFFIX_LOADING
			} );
			
		}, this /* passed to function as this */ );
		
		var hash_json_field_Contents_JSONString = JSON.stringify( _hash_json_Contents );
		var ajaxRequestData = {
				project_search_id : _project_search_ids,
				filterCriteria : hash_json_field_Contents_JSONString
		};
		
		if ( _loadloadChargeCountActiveAjax ) {
			_loadloadChargeCountActiveAjax.abort();
			_loadloadChargeCountActiveAjax = null;
		}
		//  Set to returned jQuery XMLHttpRequest (jqXHR) object
		_loadloadChargeCountActiveAjax =
			$.ajax({
				url : contextPathJSVar + "/services/qc/dataPage/chargeCounts",
				traditional: true,  //  Force traditional serialization of the data sent
				//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
				//   So project_search_ids array is passed as "project_search_ids=<value>" which is what Jersey expects
				data : ajaxRequestData,  // The data sent as params on the URL
				dataType : "json",
				success : function( ajaxResponseData ) {
					try {
						_loadloadChargeCountActiveAjax = null;
						var responseParams = {
								ajaxResponseData : ajaxResponseData, 
								ajaxRequestData : ajaxRequestData
//								,
//								topTRelement : topTRelement
						};
						objectThis.loadChargeCountResponse( responseParams );
//						$topTRelement.data( _DATA_LOADED_DATA_KEY, true );
					} catch( e ) {
						reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
						throw e;
					}
				},
				failure: function(errMsg) {
					_loadloadChargeCountActiveAjax = null;
					handleAJAXFailure( errMsg );
				},
				error : function(jqXHR, textStatus, errorThrown) {
					_loadloadChargeCountActiveAjax = null;
					if ( objectThis._passAJAXErrorTo_handleAJAXError(jqXHR, textStatus, errorThrown) ) {
						handleAJAXError(jqXHR, textStatus, errorThrown);
					}
				}
			});
	};
	

	
	/**
	 * Load the data for Charge Counts  and Summary (Summary created from ChargeCount Data)
	 */
	this.loadChargeCountResponse = function( params ) {
		var ajaxResponseData = params.ajaxResponseData;
		var ajaxRequestData = params.ajaxRequestData;
		
		var chargeStateCountsResults = ajaxResponseData.chargeStateCountsResults;
		var resultsPerLinkTypeList = chargeStateCountsResults.resultsPerLinkTypeList;

		var $PSMChargeStatesCountsBlock = $("#PSMChargeStatesCountsBlock");
		if ( $PSMChargeStatesCountsBlock.length === 0 ) {
			throw Error( "unable to find HTML element with id 'PSMChargeStatesCountsBlock'" );
		}

		$PSMChargeStatesCountsBlock.empty();
		
		resultsPerLinkTypeList.forEach( function ( currentArrayValue, index, array ) {
			var entryForLinkType = currentArrayValue;
			var linkType = entryForLinkType.linkType;
			var resultsPerChargeValueList = entryForLinkType.resultsPerChargeValueList;
			
			if ( resultsPerChargeValueList === null || resultsPerChargeValueList === undefined ||
					resultsPerChargeValueList.length === 0 ) {

				//  Add empty chart with No Data message
				var $chart_outer_container_jq = this._addChartOuterTemplate( { $chart_group_container_table_jq : $PSMChargeStatesCountsBlock } );
				this._placeEmptyDummyChartForMessage( { 
					$chart_outer_container_jq : $chart_outer_container_jq, 
					linkType : linkType, 
					messagePrefix:  _DUMMY_CHART_STATUS_TEXT_PREFIX_NO_DATA,
					messageSuffix:  _DUMMY_CHART_STATUS_TEXT_SUFFIX_NO_DATA
				} );
				
			} else {
				var $chart_outer_container_jq = this._addChartOuterTemplate( { $chart_group_container_table_jq : $PSMChargeStatesCountsBlock } );
				var $chart_container_jq = this._addChartInnerTemplate( { $chart_outer_container_jq : $chart_outer_container_jq } );
				
				var linkType = entryForLinkType.linkType;
				var colorAndbarColor = this.getColorAndBarColorFromLinkType( linkType );
				
				this._addSinglePSMChargeChart( { entryForLinkType: entryForLinkType, colorAndbarColor : colorAndbarColor, $chartContainer : $chart_container_jq } );
				
				chartDownload.addDownloadClickHandlers( { $chart_outer_container_for_download_jq :  $chart_outer_container_jq } );
				// Add tooltips for download links
				addToolTips( $chart_outer_container_jq );
			}
		}, this /* passed to function as this */ );
		
		_chargeCount_Statistics_isLoaded = _IS_LOADED_YES;
		
	};
	
	/**
	 * Overridden for Specific elements like Chart Title and X and Y Axis labels
	 */
	var _CHARGE_CHART_GLOBALS = {
			_CHART_DEFAULT_FONT_SIZE : 12,  //  Default font size - using to set font size for tick marks.
			_TITLE_FONT_SIZE : 15, // In PX
			_AXIS_LABEL_FONT_SIZE : 14, // In PX
			_TICK_MARK_TEXT_FONT_SIZE : 14 // In PX
	};
	
	/**
	 * Add single Charge Chart
	 */
	this._addSinglePSMChargeChart = function( params ) {
		var entryForLinkType = params.entryForLinkType;
		var colorAndbarColor = params.colorAndbarColor;
		var $chartContainer = params.$chartContainer;

		var linkType = entryForLinkType.linkType;
		var resultsPerChargeValueList = entryForLinkType.resultsPerChargeValueList;

		//  chart data for Google charts
		var chartData = [];

		var chartDataHeaderEntry = [ 'AAAAAAAAA', "MMM", {role: "tooltip", 'p': {'html': true} }, {type: 'string', role: 'annotation'} ]; 
		chartData.push( chartDataHeaderEntry );

		var maxChargeCount = 0;

		for ( var indexForChargeValue = 0; indexForChargeValue < resultsPerChargeValueList.length; indexForChargeValue++ ) {
			var entryForChargeValue = resultsPerChargeValueList[ indexForChargeValue ];
			
			var tooltipText = "<div  style='padding: 4px;'>Charge: +" + entryForChargeValue.chargeValue + 
				"<br>Count: " + entryForChargeValue.chargeCount + "</div>";
//			var entryAnnotationText = "+" + entryForChargeValue.chargeValue + ":" + entryForChargeValue.chargeCount;
			var entryAnnotationText = entryForChargeValue.chargeCount;
			
			var chartEntry = [ 
				"+" + entryForChargeValue.chargeValue, 
				entryForChargeValue.chargeCount, 
				//  Tool Tip
				tooltipText,
				entryAnnotationText
				 ];
			chartData.push( chartEntry );
			if ( entryForChargeValue.chargeCount > maxChargeCount ) {
				maxChargeCount = entryForChargeValue.chargeCount;
			}

		}
		
		var vAxisTicks = this._getChargeCountTickMarks( { maxValue : maxChargeCount } );
		
		var barColors = [ colorAndbarColor.color ]; // must be an array

		var chartTitle = 'Number PSMs with Charge (' + linkType + ")";
		var optionsFullsize = {
			//  Overridden for Specific elements like Chart Title and X and Y Axis labels
							fontSize: _CHARGE_CHART_GLOBALS._CHART_DEFAULT_FONT_SIZE,  //  Default font size - using to set font size for tick marks.
							
				title: chartTitle, // Title above chart
			    titleTextStyle: {
			    	color : _PROXL_DEFAULT_FONT_COLOR, //  Set default font color
//			        color: <string>,    // any HTML string color ('red', '#cc00cc')
//			        fontName: <string>, // i.e. 'Times New Roman'
			        fontSize: _CHARGE_CHART_GLOBALS._TITLE_FONT_SIZE, // 12, 18 whatever you want (don't specify px)
//			        bold: <boolean>,    // true or false
//			        italic: <boolean>   // true of false
			    },
				//  X axis label below chart
				hAxis: { title: 'Charge', titleTextStyle: { color: 'black', fontSize: _CHARGE_CHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
				},  
				//  Y axis label left of chart
				vAxis: { title: 'Count', titleTextStyle: { color: 'black', fontSize: _CHARGE_CHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
					,baseline: 0     // always start at zero
					,ticks: vAxisTicks
					,maxValue : maxChargeCount
				},
				legend: { position: 'none' }, //  position: 'none':  Don't show legend of bar colors in upper right corner
//				width : 500, 
//				height : 300,   // width and height of chart, otherwise controlled by enclosing div
				colors: barColors,
				tooltip: {isHtml: true}
//				,chartArea : { left : 140, top: 60, 
//				width: objectThis.RETENTION_TIME_COUNT_CHART_WIDTH - 200 ,  //  was 720 as measured in Chrome
//				height : objectThis.RETENTION_TIME_COUNT_CHART_HEIGHT - 120 }  //  was 530 as measured in Chrome
		};        
		// create the chart
		var data = google.visualization.arrayToDataTable( chartData );
		var chartFullsize = new google.visualization.ColumnChart( $chartContainer[0] );
		chartFullsize.draw(data, optionsFullsize);
		
		//  Temp code to find <rect> that are the actual data columns
		//     Changing them to green to allow show that they are only the data columns and not other <rect> in the <svg>
		
//		var $rectanglesInChart_All = $chartContainer.find("rect");
//		
//		$rectanglesInChart_All.each( function() {
//			var $rectangleInChart = $( this );
//			var rectangleFillColor = $rectangleInChart.attr("fill");
//			if ( rectangleFillColor !== undefined ) {
//				if ( rectangleFillColor.toLowerCase() === barColor.toLowerCase() ) {
//					$rectangleInChart.attr("fill","green");
//					var z = 0;
//				}
//			}
//		});
		
	};

	/**
	 * 
	 */
	this._getChargeCountTickMarks = function( params ) {
		var maxValue = params.maxValue;
		if ( maxValue < 5 ) {
			var tickMarks = [ 0 ];
			for ( var counter = 1; counter <= maxValue; counter++ ) {
				tickMarks.push( counter );
			}
			return tickMarks;
		}
		return undefined; //  Use defaults
	};
	
	//////////////////////////////////////////////////////////////////

	//     M/Z for PSMs Histogram
	

	/**
	 * Clear data for  M/Z for PSMs Histogram
	 */
	this.clear_M_Over_Z_For_PSMs_Histogram = function() {
		
		_M_Over_Z_For_PSMs_Statistics_isLoaded = _IS_LOADED_NO;

		var $PSM_M_Over_Z_CountsBlock = $("#PSM_M_Over_Z_CountsBlock");
		if ( $PSM_M_Over_Z_CountsBlock.length === 0 ) {
			throw Error( "unable to find HTML element with id 'PSM_M_Over_Z_CountsBlock'" );
		}
		$PSM_M_Over_Z_CountsBlock.empty();
		
		if ( _load_M_Over_Z_For_PSMs_HistogramActiveAjax ) {
			_load_M_Over_Z_For_PSMs_HistogramActiveAjax.abort();
			_load_M_Over_Z_For_PSMs_HistogramActiveAjax = null;
		}
	};

	/**
	 * If not loaded, call this.load_M_Over_Z_For_PSMs_Histogram()
	 */
	this.load_M_Over_Z_For_PSMs_HistogramIfNeeded = function() {
		if ( _M_Over_Z_For_PSMs_Statistics_isLoaded === _IS_LOADED_NO ) {
			this.load_M_Over_Z_For_PSMs_Histogram();
		}
	};
	
	var _load_M_Over_Z_For_PSMs_HistogramActiveAjax = null;
	
	/**
	 * Load the data for  M/Z for PSMs Histogram
	 */
	this.load_M_Over_Z_For_PSMs_Histogram = function() {
		var objectThis = this;

		_M_Over_Z_For_PSMs_Statistics_isLoaded = _IS_LOADED_LOADING;

		var $PSM_M_Over_Z_CountsBlock = $("#PSM_M_Over_Z_CountsBlock");
		if ( $PSM_M_Over_Z_CountsBlock.length === 0 ) {
			throw Error( "unable to find HTML element with id 'PSM_M_Over_Z_CountsBlock'" );
		}
		$PSM_M_Over_Z_CountsBlock.empty();

		var selectedLinkTypes = _hash_json_Contents.linkTypes;
		
		if ( ! _anySearchesHaveScanDataYes ) {

			// Show cells for selected link types
			selectedLinkTypes.forEach( function ( currentArrayValue, index, array ) {
				var selectedLinkType = currentArrayValue;

				//  Add empty chart with Loading message
				var $chart_outer_container_jq =
					this._addChartOuterTemplate( { $chart_group_container_table_jq : $PSM_M_Over_Z_CountsBlock } );
				this._placeEmptyDummyChartForMessage( { 
					$chart_outer_container_jq : $chart_outer_container_jq, 
//					linkType : selectedLinkType, 
					messageWhole:  _DUMMY_CHART_STATUS_WHOLE_TEXT_SCANS_NOT_UPLOADED
				} );
			}, this /* passed to function as this */ );

			_M_Over_Z_For_PSMs_Statistics_isLoaded = _IS_LOADED_YES;
			
			//  Exit since no data to display
			
			return;  //  EARLY EXIT
		}

		// Add cells for selected link types
		selectedLinkTypes.forEach( function ( currentArrayValue, index, array ) {
			var selectedLinkType = currentArrayValue;

			//  Add empty chart with Loading message
			var $chart_outer_container_jq =
				this._addChartOuterTemplate( { linkType : selectedLinkType, $chart_group_container_table_jq : $PSM_M_Over_Z_CountsBlock } );

			//  Add empty chart with Loading message
			this._placeEmptyDummyChartForMessage( { 
				$chart_outer_container_jq : $chart_outer_container_jq, 
			//				linkType : selectedLinkType, 
				messagePrefix:  _DUMMY_CHART_STATUS_TEXT_PREFIX_LOADING,
				messageSuffix:  _DUMMY_CHART_STATUS_TEXT_SUFFIX_LOADING
			} );
			
		}, this /* passed to function as this */ );
		

		var hash_json_field_Contents_JSONString = JSON.stringify( _hash_json_Contents );
		var ajaxRequestData = {
				project_search_id : _project_search_ids,
				filterCriteria : hash_json_field_Contents_JSONString
		};
		if ( _load_M_Over_Z_For_PSMs_HistogramActiveAjax ) {
			_load_M_Over_Z_For_PSMs_HistogramActiveAjax.abort();
			_load_M_Over_Z_For_PSMs_HistogramActiveAjax = null;
		}
		//  Set to returned jQuery XMLHttpRequest (jqXHR) object
		_load_M_Over_Z_For_PSMs_HistogramActiveAjax =
			$.ajax({
				url : contextPathJSVar + "/services/qc/dataPage/mzForPSMsHistogramCounts",
				traditional: true,  //  Force traditional serialization of the data sent
				//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
				//   So project_search_ids array is passed as "project_search_ids=<value>" which is what Jersey expects
				data : ajaxRequestData,  // The data sent as params on the URL
				dataType : "json",
				success : function( ajaxResponseData ) {
					try {
						_load_M_Over_Z_For_PSMs_HistogramActiveAjax = null;
						var responseParams = {
								ajaxResponseData : ajaxResponseData, 
								ajaxRequestData : ajaxRequestData
//								,
//								topTRelement : topTRelement
						};
						objectThis.load_M_Over_Z_For_PSMs_HistogramResponse( responseParams );
//						$topTRelement.data( _DATA_LOADED_DATA_KEY, true );
					} catch( e ) {
						reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
						throw e;
					}
				},
				failure: function(errMsg) {
					_load_M_Over_Z_For_PSMs_HistogramActiveAjax = null;
					handleAJAXFailure( errMsg );
				},
				error : function(jqXHR, textStatus, errorThrown) {
					_load_M_Over_Z_For_PSMs_HistogramActiveAjax = null;
					if ( objectThis._passAJAXErrorTo_handleAJAXError(jqXHR, textStatus, errorThrown) ) {
						handleAJAXError(jqXHR, textStatus, errorThrown);
					}
				}
			});
	};

	/**
	 * Load the data for Charge Counts
	 */
	this.load_M_Over_Z_For_PSMs_HistogramResponse = function( params ) {
		var ajaxResponseData = params.ajaxResponseData;
		var ajaxRequestData = params.ajaxRequestData;
		
		var preMZ_Histogram_For_PSMPeptideCutoffsResults = ajaxResponseData.preMZ_Histogram_For_PSMPeptideCutoffsResults;
		var dataForChartPerLinkTypeList = preMZ_Histogram_For_PSMPeptideCutoffsResults.dataForChartPerLinkTypeList;

		var $PSM_M_Over_Z_CountsBlock = $("#PSM_M_Over_Z_CountsBlock");
		if ( $PSM_M_Over_Z_CountsBlock.length === 0 ) {
			throw Error( "unable to find HTML element with id 'PSM_M_Over_Z_CountsBlock'" );
		}

		$PSM_M_Over_Z_CountsBlock.empty();

		dataForChartPerLinkTypeList.forEach( function ( currentArrayValue, indexForLinkType, array ) {
			var entryForLinkType = currentArrayValue;
			var linkType = entryForLinkType.linkType;
			var chartBuckets = entryForLinkType.chartBuckets;
			
			if ( chartBuckets === undefined ||chartBuckets === null || chartBuckets.length === 0 ) {
				//  No data for this link type

				//  Add empty chart with No Data message
				var $chart_outer_container_jq =
					this._addChartOuterTemplate( { $chart_group_container_table_jq : $PSM_M_Over_Z_CountsBlock } );
				this._placeEmptyDummyChartForMessage( { 
					$chart_outer_container_jq : $chart_outer_container_jq, 
					linkType : linkType, 
					messagePrefix:  _DUMMY_CHART_STATUS_TEXT_PREFIX_NO_DATA,
					messageSuffix:  _DUMMY_CHART_STATUS_TEXT_SUFFIX_NO_DATA
				} );
				
				return;  //  EARLY exit for this array element
			}
			var $chart_outer_container_jq =
				this._addChartOuterTemplate( { $chart_group_container_table_jq : $PSM_M_Over_Z_CountsBlock } );
			var $chart_container_jq = this._addChartInnerTemplate( { $chart_outer_container_jq : $chart_outer_container_jq } );

			var colorAndbarColor = this.getColorAndBarColorFromLinkType( linkType );

			this._add_M_Over_Z_For_PSMs_Histogram_Chart( { entryForLinkType: entryForLinkType, colorAndbarColor: colorAndbarColor, $chartContainer : $chart_container_jq } );

			chartDownload.addDownloadClickHandlers( { $chart_outer_container_for_download_jq :  $chart_outer_container_jq } );
			// Add tooltips for download links
			addToolTips( $chart_outer_container_jq );
		}, this /* passed to function as this */ );
		
		_M_Over_Z_For_PSMs_Statistics_isLoaded = _IS_LOADED_YES;
	};
	
	//  Overridden for Specific elements like Chart Title and X and Y Axis labels
	var _M_Over_Z_For_PSMs_CHART_GLOBALS = {
			_CHART_DEFAULT_FONT_SIZE : 12,  //  Default font size - using to set font size for tick marks.
			_TITLE_FONT_SIZE : 15, // In PX
			_AXIS_LABEL_FONT_SIZE : 14, // In PX
			_TICK_MARK_TEXT_FONT_SIZE : 14, // In PX
	}

	/**
	 * 
	 */
	this._add_M_Over_Z_For_PSMs_Histogram_Chart = function( params ) {
		var entryForLinkType = params.entryForLinkType;
		var colorAndbarColor = params.colorAndbarColor;
		var $chartContainer = params.$chartContainer;
		
		var linkType = entryForLinkType.linkType;
		var chartBuckets = entryForLinkType.chartBuckets;

		//  chart data for Google charts
		var chartData = [];

		var chartDataHeaderEntry = [ 'preMZ', "Count", { role: 'style' }, {role: "tooltip", 'p': {'html': true} }
//			, {type: 'string', role: 'annotation'}
			]; 
		chartData.push( chartDataHeaderEntry );
		
		var maxCount = 0;

		for ( var index = 0; index < chartBuckets.length; index++ ) {
			var bucket = chartBuckets[ index ];
			
			var tooltipText = "<div style='padding: 4px;'>Count: " + bucket.count +
			"<br>m/z approximately " + bucket.binStart + " to " + bucket.binEnd + "</div>";
			
			var entryAnnotationText = bucket.count;
			
			var chartEntry = [ 
				bucket.binCenter,  
				bucket.count, 
				//  Style of bar
				colorAndbarColor.barColor,
				//  Tool Tip
				tooltipText
//				,
//				entryAnnotationText
				 ];
			chartData.push( chartEntry );
			if ( bucket.count > maxCount ) {
				maxCount = bucket.count;
			}

		}
		
		var vAxisTicks = this._get_M_Over_Z_For_PSMs_Histogram_ChartTickMarks( { maxValue : maxCount } );
		
		var barColors = [ colorAndbarColor.color ]; // must be an array

		//  Chart title and layout also in viewQC_MZ_Data_NoDataAvailable.jsp for empty chart when no M/Z data
		
		var chartTitle = 'PSM Count vs/ m/z (' + linkType + ")";
		var optionsFullsize = {
			//  Overridden for Specific elements like Chart Title and X and Y Axis labels
				fontSize: _M_Over_Z_For_PSMs_CHART_GLOBALS._CHART_DEFAULT_FONT_SIZE,  //  Default font size - using to set font size for tick marks.
							
				title: chartTitle, // Title above chart
			    titleTextStyle: {
			    	color : _PROXL_DEFAULT_FONT_COLOR, //  Set default font color
//			        color: <string>,    // any HTML string color ('red', '#cc00cc')
//			        fontName: <string>, // i.e. 'Times New Roman'
			        fontSize: _M_Over_Z_For_PSMs_CHART_GLOBALS._TITLE_FONT_SIZE, // 12, 18 whatever you want (don't specify px)
//			        bold: <boolean>,    // true or false
//			        italic: <boolean>   // true of false
			    },
				//  X axis label below chart
				hAxis: { title: 'M/Z', titleTextStyle: { color: 'black', fontSize: _M_Over_Z_For_PSMs_CHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
					,gridlines: {  
		                color: 'none'  //  No vertical grid lines on the horzontal axis
		            }
				},  
				//  Y axis label left of chart
				vAxis: { title: 'Count', titleTextStyle: { color: 'black', fontSize: _M_Over_Z_For_PSMs_CHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
//					,baseline: 0     // always start at zero
					,ticks: vAxisTicks
					,maxValue : maxCount
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
		chartFullsize.draw(data, optionsFullsize);
		
		//  Temp code to find <rect> that are the actual data columns
		//     Changing them to green to allow show that they are only the data columns and not other <rect> in the <svg>
		
//		var $rectanglesInChart_All = $chartContainer.find("rect");
//		
//		$rectanglesInChart_All.each( function() {
//			var $rectangleInChart = $( this );
//			var rectangleFillColor = $rectangleInChart.attr("fill");
//			if ( rectangleFillColor !== undefined ) {
//				if ( rectangleFillColor.toLowerCase() === _OVERALL_GLOBALS.BAR_COLOR_CROSSLINK.toLowerCase() ) {
//					$rectangleInChart.attr("fill","green");
//					var z = 0;
//				}
//			}
//		});
		
	};

	/**
	 * 
	 */
	this._get_M_Over_Z_For_PSMs_Histogram_ChartTickMarks = function( params ) {
		var maxValue = params.maxValue;
		if ( maxValue < 5 ) {
			var tickMarks = [ 0 ];
			for ( var counter = 1; counter <= maxValue; counter++ ) {
				tickMarks.push( counter );
			}
			return tickMarks;
		}
		return undefined; //  Use defaults
	};
	

	///////////////////////////////////////////////////////
	
	///////    PSM Error Estimates  ////////////////////
	

	/**
	 * Clear the data for the PSM section
	 */
	this.clearPSM_ErrorEstimates = function() {
		
		this.clear_PPM_Error_For_PSMs_Histogram();
		this.clear_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlot();
		this.clear_PPM_Error_Vs_M_over_Z_For_PSMs_ScatterPlot();
	};
	
	
	/**
	 * Load the data for PSM section
	 */
	this.load_PSM_ErrorEstimatesIfNeeded = function() {
		this.load_PPM_Error_For_PSMs_HistogramIfNeeded();
		this.load_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlotIfNeeded();
		this.load_PPM_Error_Vs_M_over_Z_For_PSMs_ScatterPlotIfNeeded();
	};
	

	//////////////////////////////////////////////////////////////////

	//    PPM Error for PSMs Histogram
	

	/**
	 * Clear data for  PPM Error for PSMs Histogram
	 */
	this.clear_PPM_Error_For_PSMs_Histogram = function() {
		
		_PPM_Error_For_PSMs_Statistics_isLoaded = _IS_LOADED_NO;

		var $PSM_PPM_Error_CountsBlock = $("#PSM_PPM_Error_CountsBlock");
		$PSM_PPM_Error_CountsBlock.empty();
		
		//  Abort any active AJAX calls
		if ( _load_PPM_Error_For_PSMs_HistogramForLinkTypeActiveAjax ) {
			var objKeys = Object.keys( _load_PPM_Error_For_PSMs_HistogramForLinkTypeActiveAjax ); 
			objKeys.forEach( function( element, index, array ) {
				var selectedLinkType = element;
				if ( _load_PPM_Error_For_PSMs_HistogramForLinkTypeActiveAjax[ selectedLinkType ] ) {
					_load_PPM_Error_For_PSMs_HistogramForLinkTypeActiveAjax[ selectedLinkType ].abort();
					_load_PPM_Error_For_PSMs_HistogramForLinkTypeActiveAjax[ selectedLinkType ] = null;
				}
			}, this /* passed to function as this */ );
		}
		_load_PPM_Error_For_PSMs_HistogramForLinkTypeActiveAjax = null;
	};

	/**
	 * If not loaded, call this.load_PPM_Error_For_PSMs_Histogram()
	 */
	this.load_PPM_Error_For_PSMs_HistogramIfNeeded = function() {
		if ( _PPM_Error_For_PSMs_Statistics_isLoaded === _IS_LOADED_NO ) {
			this.load_PPM_Error_For_PSMs_Histogram();
		}
	};
	
	/**
	 * Load the data for  PPM Error for PSMs Histogram
	 */
	this.load_PPM_Error_For_PSMs_Histogram = function() {
		var objectThis = this;

		_PPM_Error_For_PSMs_Statistics_isLoaded = _IS_LOADED_LOADING;

		var $PSM_PPM_Error_CountsBlock = $("#PSM_PPM_Error_CountsBlock");
		$PSM_PPM_Error_CountsBlock.empty();

		var selectedLinkTypes = _hash_json_Contents.linkTypes;
		
		if ( ! _anySearchesHaveScanDataYes ) {

			// Show cells for selected link types
			for ( var index = 0; index < selectedLinkTypes.length; index++ ) {
				var selectedLinkType = selectedLinkTypes[ index ];
				//  Add empty chart with Loading message

				var $chart_outer_container_jq =
					this._addChartOuterTemplate( { $chart_group_container_table_jq : $PSM_PPM_Error_CountsBlock } );
				this._placeEmptyDummyChartForMessage( { 
					$chart_outer_container_jq : $chart_outer_container_jq, 
//					linkType : selectedLinkType, 
					messageWhole:  _DUMMY_CHART_STATUS_WHOLE_TEXT_SCANS_NOT_UPLOADED
				} );
			}

			_PPM_Error_For_PSMs_Statistics_isLoaded = _IS_LOADED_YES;
			
			//  Exit since no data to display
			
			return;  //  EARLY EXIT
		}		

		//  Make a copy of _hash_json_Contents    (  true for deep, target object, source object, <source object 2>, ... )
		var hash_json_Contents_COPY = $.extend( true /*deep*/,  {}, _hash_json_Contents );
		
		// Add cells for selected link types
		selectedLinkTypes.forEach( function ( currentArrayValue, index, array ) {
			var selectedLinkType = currentArrayValue;

			//  Add empty chart with Loading message
			var $chart_outer_container_jq =
				this._addChartOuterTemplate( { linkType : selectedLinkType, $chart_group_container_table_jq : $PSM_PPM_Error_CountsBlock } );

			//  Add empty chart with Loading message
			this._placeEmptyDummyChartForMessage( { 
				$chart_outer_container_jq : $chart_outer_container_jq, 
			//				linkType : selectedLinkType, 
				messagePrefix:  _DUMMY_CHART_STATUS_TEXT_PREFIX_LOADING,
				messageSuffix:  _DUMMY_CHART_STATUS_TEXT_SUFFIX_LOADING
			} );
			
			//  For each selected link type, 
			//    set it as selected link type in hash_json_Contents_COPY,
			//      and create a chart for that
			
			hash_json_Contents_COPY.linkTypes = [ selectedLinkType ];
			
			this.load_PPM_Error_For_PSMs_HistogramForLinkType( {  
					selectedLinkType : selectedLinkType,
					hash_json_Contents_COPY : hash_json_Contents_COPY,
					$chart_outer_container_jq : $chart_outer_container_jq
			});

		}, this /* passed to function as this */ );
		
	};
	
	var _load_PPM_Error_For_PSMs_HistogramForLinkTypeActiveAjax = null;

	/**
	 * Load the data for  PPM Error for PSMs Histogram Specific Link Type
	 */
	this.load_PPM_Error_For_PSMs_HistogramForLinkType = function( params ) {
		var objectThis = this;

		var selectedLinkType = params.selectedLinkType;
		var hash_json_Contents_COPY = params.hash_json_Contents_COPY;
		var $chart_outer_container_jq = params.$chart_outer_container_jq; 
			
		var hash_json_field_Contents_JSONString = JSON.stringify( hash_json_Contents_COPY );
		var ajaxRequestData = {
				project_search_id : _project_search_ids,
				filterCriteria : hash_json_field_Contents_JSONString
		};
		if ( _load_PPM_Error_For_PSMs_HistogramForLinkTypeActiveAjax && 
				_load_PPM_Error_For_PSMs_HistogramForLinkTypeActiveAjax[ selectedLinkType ] ) {
			_load_PPM_Error_For_PSMs_HistogramForLinkTypeActiveAjax[ selectedLinkType ].abort();
			_load_PPM_Error_For_PSMs_HistogramForLinkTypeActiveAjax[ selectedLinkType ] = null;
		}
		if ( ! _load_PPM_Error_For_PSMs_HistogramForLinkTypeActiveAjax ) {
			_load_PPM_Error_For_PSMs_HistogramForLinkTypeActiveAjax = {};
		}
		//  Set to returned jQuery XMLHttpRequest (jqXHR) object
		_load_PPM_Error_For_PSMs_HistogramForLinkTypeActiveAjax[ selectedLinkType ] = 
			$.ajax({
//				cache : false,
				url : contextPathJSVar + "/services/qc/dataPage/ppmError",
				traditional: true,  //  Force traditional serialization of the data sent
				//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
				//   So project_search_ids array is passed as "project_search_ids=<value>" which is what Jersey expects
				data : ajaxRequestData,  // The data sent as params on the URL
				dataType : "json",
				success : function( ajaxResponseData ) {
					try {
						if ( _load_PPM_Error_For_PSMs_HistogramForLinkTypeActiveAjax ) {
							_load_PPM_Error_For_PSMs_HistogramForLinkTypeActiveAjax[ selectedLinkType ] = null;
						}
						var responseParams = {
								ajaxResponseData : ajaxResponseData, 
								ajaxRequestData : ajaxRequestData,
								selectedLinkType : selectedLinkType,
								$chart_outer_container_jq : $chart_outer_container_jq
						};
						objectThis.load_PPM_Error_For_PSMs_HistogramResponse( responseParams );
//						$topTRelement.data( _DATA_LOADED_DATA_KEY, true );
					} catch( e ) {
						reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
						throw e;
					}
				},
				failure: function(errMsg) {
					if ( _load_PPM_Error_For_PSMs_HistogramForLinkTypeActiveAjax ) {
						_load_PPM_Error_For_PSMs_HistogramForLinkTypeActiveAjax[ selectedLinkType ] = null;
					}
					handleAJAXFailure( errMsg );
				},
				error : function(jqXHR, textStatus, errorThrown) {
					if ( _load_PPM_Error_For_PSMs_HistogramForLinkTypeActiveAjax ) {
						_load_PPM_Error_For_PSMs_HistogramForLinkTypeActiveAjax[ selectedLinkType ] = null;
					}
					if ( objectThis._passAJAXErrorTo_handleAJAXError(jqXHR, textStatus, errorThrown) ) {
						handleAJAXError(jqXHR, textStatus, errorThrown);
					}
				}
			});
		// indent due to assignment
	};

	/**
	 * Process AJAX Response
	 */
	this.load_PPM_Error_For_PSMs_HistogramResponse = function( params ) {
		var objectThis = this;

		var ajaxResponseData = params.ajaxResponseData;
		var ajaxRequestData = params.ajaxRequestData;
		var selectedLinkType = params.selectedLinkType;
		var $chart_outer_container_jq = params.$chart_outer_container_jq;
		
		var ppmErrorHistogramResult = ajaxResponseData.ppmErrorHistogramResult;
		var dataForChartPerLinkTypeList = ppmErrorHistogramResult.dataForChartPerLinkTypeList;
		
		if ( dataForChartPerLinkTypeList.length !== 1 ) {
			throw Error( "dataForChartPerLinkTypeList.length !== 1, is = " + dataForChartPerLinkTypeList.length );
		}
		
		var entryForLinkType = dataForChartPerLinkTypeList[ 0 ];
		
		var linkType = entryForLinkType.linkType;
		var chartBuckets = entryForLinkType.chartBuckets;
		
		$chart_outer_container_jq.empty();

		if ( chartBuckets === null || chartBuckets.length === 0 ) {
			//  No data for this link type

			//  Add empty chart with No Data message
			this._placeEmptyDummyChartForMessage( { 
				$chart_outer_container_jq : $chart_outer_container_jq, 
			//				linkType : selectedLinkType, 
				messagePrefix:  _DUMMY_CHART_STATUS_TEXT_PREFIX_NO_DATA,
				messageSuffix:  _DUMMY_CHART_STATUS_TEXT_SUFFIX_NO_DATA
			} );
			
		} else {

			var $chart_container_jq = this._addChartInnerTemplate( { $chart_outer_container_jq : $chart_outer_container_jq } );

			var colorAndbarColor = this.getColorAndBarColorFromLinkType( linkType );
			
			this._add_PPM_Error_For_PSMs_Histogram_Chart( { entryForLinkType: entryForLinkType, colorAndbarColor: colorAndbarColor, $chartContainer : $chart_container_jq } );

			chartDownload.addDownloadClickHandlers( { $chart_outer_container_for_download_jq :  $chart_outer_container_jq } );
			// Add tooltips for download links
			addToolTips( $chart_outer_container_jq );

		}
	
		//  TODO  FIX THIS
		
//		_PPM_Error_For_PSMs_Statistics_isLoaded = _IS_LOADED_YES;
	};
	
	//  Overridden for Specific elements like Chart Title and X and Y Axis labels
	var _PPM_Error_For_PSMs_CHART_GLOBALS = {
			_CHART_DEFAULT_FONT_SIZE : 12,  //  Default font size - using to set font size for tick marks.
			_TITLE_FONT_SIZE : 15, // In PX
			_AXIS_LABEL_FONT_SIZE : 14, // In PX
			_TICK_MARK_TEXT_FONT_SIZE : 14, // In PX
	}

	/**
	 * 
	 */
	this._add_PPM_Error_For_PSMs_Histogram_Chart = function( params ) {
		var entryForLinkType = params.entryForLinkType;
		var colorAndbarColor = params.colorAndbarColor;
		var $chartContainer = params.$chartContainer;
		
		var linkType = entryForLinkType.linkType;
		var chartBuckets = entryForLinkType.chartBuckets;

		//  chart data for Google charts
		var chartData = [];

		var chartDataHeaderEntry = [ 'PPM Error', "Count", { role: 'style' }, {role: "tooltip", 'p': {'html': true} }
//			, {type: 'string', role: 'annotation'}
			]; 
		chartData.push( chartDataHeaderEntry );
		
		var _PPM_ERROR_DISPLAY_MAX_SIGNIFICANT_DIGITS = 5;
		
		var maxCount = 0;
		
		var totalCount = 0;
		
		var maxPPMError = null;
		var minPPMError = null;

		for ( var index = 0; index < chartBuckets.length; index++ ) {
			var bucket = chartBuckets[ index ];
			
			var tooltipText = "<div style='padding: 4px;'>Count: " + bucket.count +
			"<br>PPM Error approximately " + 
			bucket.binStart.toPrecision( _PPM_ERROR_DISPLAY_MAX_SIGNIFICANT_DIGITS ) + 
			" to " + bucket.binEnd.toPrecision( _PPM_ERROR_DISPLAY_MAX_SIGNIFICANT_DIGITS ) +
			"</div>";
			
			var entryAnnotationText = bucket.count;
			
			var chartEntry = [ 
				bucket.binCenter,  
				bucket.count, 
				//  Style of bar
				colorAndbarColor.barColor,
				//  Tool Tip
				tooltipText
//				,
//				entryAnnotationText
				 ];
			chartData.push( chartEntry );
			
			totalCount += bucket.count;
			if ( bucket.count > maxCount ) {
				maxCount = bucket.count;
			}
			if ( index === 0 ) {
				var maxPPMError = bucket.binEnd;
				var minPPMError = bucket.binStart;
			} else {
				if ( maxPPMError > bucket.binEnd ) {
					maxPPMError = bucket.binEnd;
				}
				if ( minPPMError > bucket.binStart ) {
					minPPMError = bucket.binStart;
				}
			}
		}
		
		var vAxisTicks = this._get_PPM_Error_For_PSMs_Histogram_ChartTickMarks( { maxValue : maxCount } );
		
		var barColors = [ colorAndbarColor.color ]; // must be an array
		
		var chartTitle = 'PSM Count vs/ PPM Error (' + linkType + ")";
		var optionsFullsize = {
			//  Overridden for Specific elements like Chart Title and X and Y Axis labels
				fontSize: _PPM_Error_For_PSMs_CHART_GLOBALS._CHART_DEFAULT_FONT_SIZE,  //  Default font size - using to set font size for tick marks.
							
				title: chartTitle, // Title above chart
			    titleTextStyle: {
			    	color : _PROXL_DEFAULT_FONT_COLOR, //  Set default font color
//			        color: <string>,    // any HTML string color ('red', '#cc00cc')
//			        fontName: <string>, // i.e. 'Times New Roman'
			        fontSize: _PPM_Error_For_PSMs_CHART_GLOBALS._TITLE_FONT_SIZE, // 12, 18 whatever you want (don't specify px)
//			        bold: <boolean>,    // true or false
//			        italic: <boolean>   // true of false
			    },
				//  X axis label below chart
				hAxis: { title: 'PPM Error', titleTextStyle: { color: 'black', fontSize: _PPM_Error_For_PSMs_CHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
					,gridlines: {  
		                color: 'none'  //  No vertical grid lines on the horzontal axis
		            }
//					,baselineColor: 'none' // Hide 'Zero' line
//					,baselineColor: '#CDCDCD' // Make 'Zero' line really light
//					,ticks: hAxisTicks
//					,ticks: [-100,0,100]
					,maxValue : maxPPMError
					,minValue : minPPMError
				},  
				//  Y axis label left of chart
				vAxis: { title: 'Count', titleTextStyle: { color: 'black', fontSize: _PPM_Error_For_PSMs_CHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
//					,baseline: 0     // always start at zero
					,ticks: vAxisTicks
					,maxValue : maxCount
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
		chartFullsize.draw(data, optionsFullsize);
		
		//  Temp code to find <rect> that are the actual data columns
		//     Changing them to green to allow show that they are only the data columns and not other <rect> in the <svg>
		
//		var $rectanglesInChart_All = $chartContainer.find("rect");
//		
//		$rectanglesInChart_All.each( function() {
//			var $rectangleInChart = $( this );
//			var rectangleFillColor = $rectangleInChart.attr("fill");
//			if ( rectangleFillColor !== undefined ) {
//				if ( rectangleFillColor.toLowerCase() === _OVERALL_GLOBALS.BAR_COLOR_CROSSLINK.toLowerCase() ) {
//					$rectangleInChart.attr("fill","green");
//					var z = 0;
//				}
//			}
//		});
		
	};

	/**
	 * 
	 */
	this._get_PPM_Error_For_PSMs_Histogram_ChartTickMarks = function( params ) {
		var maxValue = params.maxValue;
		if ( maxValue < 5 ) {
			var tickMarks = [ 0 ];
			for ( var counter = 1; counter <= maxValue; counter++ ) {
				tickMarks.push( counter );
			}
			return tickMarks;
		}
		return undefined; //  Use defaults
	};
	
	


	//////////////////////////////////////////////////////////////////

	//    PPM Error Vs Retention Time for PSMs Scatter Plot
	

	/**
	 * Clear data for  PPM Error for PSMs Scatter Plot
	 */
	this.clear_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlot = function() {
		
		_PPM_Error_Vs_RetentionTime_For_PSMs_ErrorEstimates_isLoaded = _IS_LOADED_NO;

		//  Abort any active AJAX calls
		if ( _load_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlotForLinkTypeActiveAjax ) {
			var objKeys = Object.keys( _load_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlotForLinkTypeActiveAjax ); 
			objKeys.forEach(function( element, index, array ) {
				var selectedLinkType = element;
				if ( _load_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlotForLinkTypeActiveAjax[ selectedLinkType ] ) {
					_load_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlotForLinkTypeActiveAjax[ selectedLinkType ].abort();
					_load_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlotForLinkTypeActiveAjax[ selectedLinkType ] = null;
				}
			}, this /* passed to function as this */ );
		}
		_load_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlotForLinkTypeActiveAjax = null;

		
	};

	/**
	 * If not loaded, call load_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlot()
	 */
	this.load_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlotIfNeeded = function() {
		if ( _PPM_Error_Vs_RetentionTime_For_PSMs_ErrorEstimates_isLoaded === _IS_LOADED_NO ) {
			this.load_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlot();
		}
	};
	
	/**
	 * Load the data for  PPM Error for PSMs Scatter Plot
	 */
	this.load_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlot = function() {
		var objectThis = this;

		_PPM_Error_Vs_RetentionTime_For_PSMs_ErrorEstimates_isLoaded = _IS_LOADED_LOADING;
		
		var $PSM_PPM_Error_Vs_RetentionTime_CountsBlock = $("#PSM_PPM_Error_Vs_RetentionTime_CountsBlock");
		$PSM_PPM_Error_Vs_RetentionTime_CountsBlock.empty();
		

		var selectedLinkTypes = _hash_json_Contents.linkTypes;
		
		if ( ! _anySearchesHaveScanDataYes ) {

			// Show cells for selected link types
			for ( var index = 0; index < selectedLinkTypes.length; index++ ) {
				var selectedLinkType = selectedLinkTypes[ index ];
				//  Add empty chart with Loading message

				var $chart_outer_container_jq =
					this._addChartOuterTemplate( { $chart_group_container_table_jq : $PSM_PPM_Error_Vs_RetentionTime_CountsBlock } );
				this._placeEmptyDummyChartForMessage( { 
					$chart_outer_container_jq : $chart_outer_container_jq, 
//					linkType : selectedLinkType, 
					messageWhole:  _DUMMY_CHART_STATUS_WHOLE_TEXT_SCANS_NOT_UPLOADED
				} );
			}

			_PPM_Error_For_PSMs_Statistics_isLoaded = _IS_LOADED_YES;
			
			//  Exit since no data to display
			
			return;  //  EARLY EXIT
		}		

		//  Make a copy of _hash_json_Contents    (  true for deep, target object, source object, <source object 2>, ... )
		var hash_json_Contents_COPY = $.extend( true /*deep*/,  {}, _hash_json_Contents );
		
		// Add cells for selected link types
		selectedLinkTypes.forEach( function ( currentArrayValue, index, array ) {
			var selectedLinkType = currentArrayValue;

			//  Add empty chart with Loading message
			var $chart_outer_container_jq =
				this._addChartOuterTemplate( { linkType : selectedLinkType, $chart_group_container_table_jq : $PSM_PPM_Error_Vs_RetentionTime_CountsBlock } );

			//  Add empty chart with Loading message
			this._placeEmptyDummyChartForMessage( { 
				$chart_outer_container_jq : $chart_outer_container_jq, 
			//				linkType : selectedLinkType, 
				messagePrefix:  _DUMMY_CHART_STATUS_TEXT_PREFIX_LOADING,
				messageSuffix:  _DUMMY_CHART_STATUS_TEXT_SUFFIX_LOADING
			} );
			
			//  For each selected link type, 
			//    set it as selected link type in hash_json_Contents_COPY,
			//      and create a chart for that
			
			hash_json_Contents_COPY.linkTypes = [ selectedLinkType ];
			
			this.load_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlotForLinkType( {  
					selectedLinkType : selectedLinkType,
					hash_json_Contents_COPY : hash_json_Contents_COPY,
					$chart_outer_container_jq : $chart_outer_container_jq
			});

		}, this /* passed to function as this */ );
		
	};
	
	var _load_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlotForLinkTypeActiveAjax = null;

	/**
	 * Load the data for  PPM Error Vs Retention Time for PSMs Scatter Plot Specific Link Type
	 */
	this.load_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlotForLinkType = function( params ) {
		var objectThis = this;

		var selectedLinkType = params.selectedLinkType;
		var hash_json_Contents_COPY = params.hash_json_Contents_COPY;
		var $chart_outer_container_jq = params.$chart_outer_container_jq; 
			
		var hash_json_field_Contents_JSONString = JSON.stringify( hash_json_Contents_COPY );
		var ajaxRequestData = {
				project_search_id : _project_search_ids,
				filterCriteria : hash_json_field_Contents_JSONString
		};

		if ( _load_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlotForLinkTypeActiveAjax && 
				_load_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlotForLinkTypeActiveAjax[ selectedLinkType ] ) {
			_load_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlotForLinkTypeActiveAjax[ selectedLinkType ].abort();
			_load_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlotForLinkTypeActiveAjax[ selectedLinkType ] = null;
		}
		if ( ! _load_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlotForLinkTypeActiveAjax ) {
			_load_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlotForLinkTypeActiveAjax = {};
		}
		//  Set to returned jQuery XMLHttpRequest (jqXHR) object
		_load_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlotForLinkTypeActiveAjax[ selectedLinkType ] = 
			$.ajax({
//				cache : false,
				url : contextPathJSVar + "/services/qc/dataPage/ppmErrorVsRetentionTime", // ppmErrorVsRetentionTime
				traditional: true,  //  Force traditional serialization of the data sent
				//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
				//   So project_search_ids array is passed as "project_search_ids=<value>" which is what Jersey expects
				data : ajaxRequestData,  // The data sent as params on the URL
				dataType : "json",
				success : function( ajaxResponseData ) {
					try {
						if ( _load_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlotForLinkTypeActiveAjax ) {
							_load_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlotForLinkTypeActiveAjax[ selectedLinkType ] = null;
						}
						var responseParams = {
								ajaxResponseData : ajaxResponseData, 
								ajaxRequestData : ajaxRequestData,
								selectedLinkType : selectedLinkType,
								$chart_outer_container_jq : $chart_outer_container_jq
						};
						objectThis.load_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlotResponse( responseParams );
					} catch( e ) {
						reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
						throw e;
					}
				},
				failure: function(errMsg) {
					if ( _load_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlotForLinkTypeActiveAjax ) {
						_load_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlotForLinkTypeActiveAjax[ selectedLinkType ] = null;
					}
					handleAJAXFailure( errMsg );
				},
				error : function(jqXHR, textStatus, errorThrown) {
					if ( _load_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlotForLinkTypeActiveAjax ) {
						_load_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlotForLinkTypeActiveAjax[ selectedLinkType ] = null;
					}
					if ( objectThis._passAJAXErrorTo_handleAJAXError(jqXHR, textStatus, errorThrown) ) {
						handleAJAXError(jqXHR, textStatus, errorThrown);
					}
				}
			});
	};

	/**
	 * Process AJAX Response
	 */
	this.load_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlotResponse = function( params ) {
		var objectThis = this;

		var ajaxResponseData = params.ajaxResponseData;
		var ajaxRequestData = params.ajaxRequestData;
		var selectedLinkType = params.selectedLinkType;
		var $chart_outer_container_jq = params.$chart_outer_container_jq;
		
		var ppmErrorVsRTScatterPlotResult = ajaxResponseData.ppmErrorVsRTScatterPlotResult;
		var dataForChartPerLinkTypeList = ppmErrorVsRTScatterPlotResult.dataForChartPerLinkTypeList;
		
		if ( dataForChartPerLinkTypeList.length !== 1 ) {
			throw Error( "dataForChartPerLinkTypeList.length !== 1, is = " + dataForChartPerLinkTypeList.length );
		}
		
		var entryForLinkType = dataForChartPerLinkTypeList[ 0 ];
		
		var linkType = entryForLinkType.linkType;
		var retentionTimeBuckets = entryForLinkType.retentionTimeBuckets;

		$chart_outer_container_jq.empty();

		if ( retentionTimeBuckets === null || retentionTimeBuckets.length === 0 ) {
			//  No data for this link type

			//  Add empty chart with No Data message
			this._placeEmptyDummyChartForMessage( { 
				$chart_outer_container_jq : $chart_outer_container_jq, 
			//				linkType : selectedLinkType, 
				messagePrefix:  _DUMMY_CHART_STATUS_TEXT_PREFIX_NO_DATA,
				messageSuffix:  _DUMMY_CHART_STATUS_TEXT_SUFFIX_NO_DATA
			} );
			
			return;  //  EARLY RETURN
		}

		var $chart_container_jq = this._addChartInnerTemplate( { $chart_outer_container_jq : $chart_outer_container_jq } );

		var colorAndbarColor = this.getColorAndBarColorFromLinkType( linkType );

		this._add_PPM_Error_Vs_RetentionTime_For_PSMs_Histogram_Chart( { entryForLinkType: entryForLinkType, colorAndbarColor: colorAndbarColor, $chartContainer : $chart_container_jq } );

		chartDownload.addDownloadClickHandlers( { $chart_outer_container_for_download_jq :  $chart_outer_container_jq } );
		// Add tooltips for download links
		addToolTips( $chart_outer_container_jq );
	
		//  TODO  FIX THIS
		
//		_PPM_Error_Vs_RetentionTime_Vs_RetentionTime_For_PSMs_ErrorEstimates_isLoaded = _IS_LOADED_YES;
	};
	
	//  Overridden for Specific elements like Chart Title and X and Y Axis labels
	var _PPM_Error_Vs_RetentionTime_For_PSMs_CHART_GLOBALS = {
			_CHART_DEFAULT_FONT_SIZE : 12,  //  Default font size - using to set font size for tick marks.
			_TITLE_FONT_SIZE : 15, // In PX
			_AXIS_LABEL_FONT_SIZE : 14, // In PX
			_TICK_MARK_TEXT_FONT_SIZE : 14, // In PX
	}

	/**
	 * 
	 */
	this._add_PPM_Error_Vs_RetentionTime_For_PSMs_Histogram_Chart = function( params ) {
		var entryForLinkType = params.entryForLinkType;
		var colorAndbarColor = params.colorAndbarColor;
		var $chartContainer = params.$chartContainer;
		
		var linkType = entryForLinkType.linkType;

		var retentionTimeBuckets = entryForLinkType.retentionTimeBuckets;

		//  All PPM Error Bin Start values, sorted smallest to largest
		var ppmErrorBinStartDistinctSorted = entryForLinkType.ppmErrorBinStartDistinctSorted;
		
		var numScans = entryForLinkType.numScans;
		
		var ppmErrorBinMin = entryForLinkType.ppmErrorBinMin;
		var ppmErrorBinMax = entryForLinkType.ppmErrorBinMax;
		var ppmErrorPossibleMax = entryForLinkType.ppmErrorPossibleMax;
		var retentionTimeBinMin = entryForLinkType.retentionTimeBinMin;
		var retentionTimeBinMax = entryForLinkType.retentionTimeBinMax;
		var retentionTimePossibleMax = entryForLinkType.retentionTimePossibleMax;

		//  chart data for Google charts
		var chartData = [];

		var OPACITY_DEFAULT = "0.5";
		
		//  opacity: null  Use Default

		// Ranges:  1-3, 4-10, 11-17, 18+
		
		//  Only last element can have and must have "min" property 
		var SERIES_SETTINGS = [
			{ max: 3, color: _PROXL_COLOR_SITE_BLUE, pointSize : 4, opacity: null }
			,{ max: 10, color: _PROXL_COLOR_SITE_GREEN, pointSize : 5, opacity: null }
			,{ max: 17, color: '#ddbf17', pointSize : 6, opacity: null }
			,{ min: 18, color: _PROXL_COLOR_SITE_RED, pointSize : 7, opacity: null }
		];
		
		
		
		var addHeaderEntry = function( label ) {
			chartDataHeaderEntry.push( {label: label, type: 'number'} );
			chartDataHeaderEntry.push( {type:'string', role: 'style' } );
//			, {type:'string', role: "tooltip", 'p': {'html': true} }
		}

		var chartDataHeaderEntry = [ 'Retention Time' ];

		var prevMax = 0;
		for ( var seriesSettingsIndex = 0; seriesSettingsIndex < SERIES_SETTINGS.length; seriesSettingsIndex++ ) {
			var entry = SERIES_SETTINGS[ seriesSettingsIndex ];
			if ( entry.max !== undefined && entry.max !== null ) {
				addHeaderEntry( ( prevMax + 1 ) + "-" + entry.max );
			} else {
				// No max so must contain min
				if ( entry.min === undefined || entry.min === null ) {
					throw Error( "SERIES_SETTINGS element not contain min or max property" );
				}
				addHeaderEntry( entry.min + "+" );
			}
			prevMax = entry.max;
		}
		
		chartData.push( chartDataHeaderEntry );
		
		var dataPointCount = 0;

		var maxCount = 0;
		
		var totalCount = 0;
		
		var maxRetenionTimeError = null;
		var minRetenionTimeError = null;

		var maxPPMError = null;
		var minPPMError = null;

		for ( var retentionTimeBucketsIndex = 0; retentionTimeBucketsIndex < retentionTimeBuckets.length; retentionTimeBucketsIndex++ ) {
			var retentionTimeBucket = retentionTimeBuckets[ retentionTimeBucketsIndex ];
			var chartBuckets = retentionTimeBucket.chartBuckets;

			for ( var bucketIndex = 0; bucketIndex < chartBuckets.length; bucketIndex++ ) {
				var bucket = chartBuckets[ bucketIndex ];

				var retentionTimeStart = bucket.retentionTimeStart;
				var retentionTimeEnd = bucket.retentionTimeEnd;
				var ppmErrorStart = bucket.ppmErrorStart;
				var ppmErrorEnd = bucket.ppmErrorEnd;
				var count = bucket.count;
				
				dataPointCount++;
				
				var retentionTimeCenter = retentionTimeStart + ( ( retentionTimeEnd - retentionTimeStart ) / 2 );
				var ppmErrorCenter = ppmErrorStart + ( ( ppmErrorEnd - ppmErrorStart ) / 2 );
				
//				var tooltipText = "<div style='padding: 4px;'>Count: " + bucket.count +
//				"<br>PPM Error approximately " + 
//				bucket.binStart.toPrecision( _PPM_ERROR_DISPLAY_MAX_SIGNIFICANT_DIGITS ) + 
//				" to " + bucket.binEnd.toPrecision( _PPM_ERROR_DISPLAY_MAX_SIGNIFICANT_DIGITS ) +
//				"</div>";

				var entryAnnotationText = bucket.count;
				
				var chartEntrySpecificSeries = [
					ppmErrorCenter 		// Y axis
					, 'fill-opacity : ??'  // style
				];
				
				var fillOpacityStyleOption = 'fill-opacity : ';

				//  Style of point
//				colorAndbarColor.barColor,
				//  Style appears to be ignored
//				' color : orange; ' +
//				'opacity : 0; '+
//				'stroke-width : 0; ' 
//				+
//				'stroke-color : red; ' +
//				'stroke-opacity : 1; ' +
//				'fill-color : red; ' +
//				'fill-opacity : .2'
//				,
//				//  Tool Tip
//				tooltipText
////				,
////				entryAnnotationText

				var chartEntry = [ retentionTimeCenter ];  	// X axis

				//  Copy chartEntrySpecificSeries to chartEntry
				var copy_chartEntrySpecificSeriesTo_chartEntry = function( ) {
					for ( var copy_chartEntrySpecificSeriesTo_chartEntry_index = 0; copy_chartEntrySpecificSeriesTo_chartEntry_index < chartEntrySpecificSeries.length; copy_chartEntrySpecificSeriesTo_chartEntry_index++ ) {
						var chartEntrySpecificSeriesEntry = chartEntrySpecificSeries[ copy_chartEntrySpecificSeriesTo_chartEntry_index ];
						chartEntry.push( chartEntrySpecificSeriesEntry );
					}
				}

				//  Fill in for other series
				var addNullEntriesToChartEntryForSeriesCount = function( seriesCount ) {
					var counterMax = seriesCount * chartEntrySpecificSeries.length;
					for ( var counter = 0; counter < counterMax ; counter++ ) {
						chartEntry.push( null );
					}
				}

				for ( var seriesSettingsIndex = 0; seriesSettingsIndex < SERIES_SETTINGS.length; seriesSettingsIndex++ ) {
					var entry = SERIES_SETTINGS[ seriesSettingsIndex ];
					if ( entry.max !== undefined && entry.max !== null ) {
						if ( count <= entry.max ) {
							addNullEntriesToChartEntryForSeriesCount( seriesSettingsIndex );
							var opacityString = null;
							if ( entry.opacity !== undefined && entry.opacity !== null ) {
								opacityString = opacityString;
							} else {
								opacityString = OPACITY_DEFAULT;
							}
							chartEntrySpecificSeries[ 1 ] = fillOpacityStyleOption + opacityString;
							copy_chartEntrySpecificSeriesTo_chartEntry( chartEntry, chartEntrySpecificSeries );
							addNullEntriesToChartEntryForSeriesCount( SERIES_SETTINGS.length - ( seriesSettingsIndex + 1 ) );
							break;
						}
					} else {
						// No max so must contain min
						if ( entry.min === undefined || entry.min === null ) {
							throw Error( "SERIES_SETTINGS element not contain min or max property" );
						}
						if ( count >= entry.min ) {
							addNullEntriesToChartEntryForSeriesCount( seriesSettingsIndex );
							var opacityString = null;
							if ( entry.opacity !== undefined && entry.opacity !== null ) {
								opacityString = opacityString;
							} else {
								opacityString = OPACITY_DEFAULT;
							}
							chartEntrySpecificSeries[ 1 ] = fillOpacityStyleOption + opacityString;
							copy_chartEntrySpecificSeriesTo_chartEntry( chartEntry, chartEntrySpecificSeries );
							break;
						}		
					}
				}
				
				chartData.push( chartEntry );

				//  Update Max Min and Total
				totalCount += bucket.count;
				if ( bucket.count > maxCount ) {
					maxCount = bucket.count;
				}
				if ( maxPPMError === null || ppmErrorEnd > maxPPMError ) {
					maxPPMError = ppmErrorEnd;
				}
				if ( minPPMError === null || ppmErrorStart < minPPMError ) {
					minPPMError = ppmErrorStart;
				}
				if ( maxRetenionTimeError === null || retentionTimeEnd > maxRetenionTimeError ) {
					maxRetenionTimeError = retentionTimeEnd;
				}
				if ( minRetenionTimeError === null || retentionTimeStart < minRetenionTimeError ) {
					minRetenionTimeError = retentionTimeStart;
				}
			}
		}

		var vAxisTicks = 
			this._get_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlot_Chart_Vertical_TickMarks( { 
				maxValue : maxPPMError, minValue : minPPMError, dataPointCount : dataPointCount } );

		var hAxisTicks = 
			this._get_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlot_Chart_Horizontal_TickMarks( { maxValue : maxRetenionTimeError, minValue : minRetenionTimeError } );
		
		//  Build Series Config and Color Arrays
		var seriesConfig = [];
		var barColors = []; // must be an array
		
		for ( var seriesSettingsIndex = 0; seriesSettingsIndex < SERIES_SETTINGS.length; seriesSettingsIndex++ ) {
			var entry = SERIES_SETTINGS[ seriesSettingsIndex ];
			var seriesConfigEntry = { color: entry.color, pointSize: entry.pointSize };
			seriesConfig.push( 	seriesConfigEntry );
			barColors.push( entry.color );
		}
		
		var chartTitle = 'PPM Error vs/ Retention Time (' + linkType + ")";
		var optionsFullsize = {
			//  Overridden for Specific elements like Chart Title and X and Y Axis labels
				fontSize: _PPM_Error_Vs_RetentionTime_For_PSMs_CHART_GLOBALS._CHART_DEFAULT_FONT_SIZE,  //  Default font size - using to set font size for tick marks.
				series: seriesConfig,	
//				pointSize: 20,
				title: chartTitle, // Title above chart
			    titleTextStyle: {
			    	color : _PROXL_DEFAULT_FONT_COLOR, //  Set default font color
//			        color: <string>,    // any HTML string color ('red', '#cc00cc')
//			        fontName: <string>, // i.e. 'Times New Roman'
			        fontSize: _PPM_Error_Vs_RetentionTime_For_PSMs_CHART_GLOBALS._TITLE_FONT_SIZE, // 12, 18 whatever you want (don't specify px)
//			        bold: <boolean>,    // true or false
//			        italic: <boolean>   // true of false
			    },
				//  X axis label below chart
				hAxis: { title: 'Retention Time', titleTextStyle: { color: 'black', fontSize: _PPM_Error_Vs_RetentionTime_For_PSMs_CHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
					,gridlines: {  
		                color: 'none'  //  No vertical grid lines on the horzontal axis
		            }
//					,baselineColor: 'none' // Hide 'Zero' line
//					,baselineColor: '#CDCDCD' // Make 'Zero' line really light
					,ticks: hAxisTicks
//					,ticks: [-100,0,100]
					,maxValue : maxRetenionTimeError
					,minValue : minRetenionTimeError
				},  
				//  Y axis label left of chart
				vAxis: { title: 'PPM Error', titleTextStyle: { color: 'black', fontSize: _PPM_Error_Vs_RetentionTime_For_PSMs_CHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
//					,baseline: 0     // always start at zero
					,ticks: vAxisTicks
					,maxValue : maxPPMError
					,minValue : minPPMError
				},
//				legend: { position: 'none' }, //  position: 'none':  Don't show legend of bar colors in upper right corner
				//  Size picked up from containing HTML element
//				width : 500, 
//				height : 300,   // width and height of chart, otherwise controlled by enclosing div
//				bar: { groupWidth: '100%' },  // set bar width large to eliminate space between bars
				colors: barColors,
				tooltip: {isHtml: true}
		};        
		// create the chart
		var data = google.visualization.arrayToDataTable( chartData );
		
		var chartFullsize = new google.visualization.ScatterChart( $chartContainer[0] );
		chartFullsize.draw( data, optionsFullsize );
		
		//  Using Material chart.  added to viewQC.jsp:  <c:set var="googleChartPackagesLoadAdditions">,"scatter"</c:set>
//		var chartFullsize = new google.charts.Scatter( $chartContainer[0] );
//		chartFullsize.draw( data, google.charts.Scatter.convertOptions( optionsFullsize ) );
		
	};

	/**
	 * for vertical PPM Error axis
	 */
	this._get_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlot_Chart_Vertical_TickMarks = function( params ) {
		var maxValue = params.maxValue;
		var minValue = params.minValue;
		var dataPointCount = params.dataPointCount; // number of data points in plot
		
		//  Compute max and min tick marks, to next higher and lower multiple of 1
		var maxValueCeil = Math.ceil( maxValue );
		var minValueFloor = Math.floor( minValue );
		var maxCeilMinusMinFloor = maxValueCeil - minValueFloor;
		
		var tickMarkIncrementFromZero = maxCeilMinusMinFloor / 5;
		
		var tickMarks = [];
		
		if ( minValue < 0 && maxValue > 0 ) {
			
			if ( ( maxValue - minValue ) >= 10 ) {
				var tickMarkValueForTickMarkCounter = function( tickCounter ) {
					var tickMark = tickCounter * tickMarkIncrementFromZero;
					return tickMark; 
				};

				//  Add tick marks for < 0 
				for ( var tickCounter = 1; ( - ( tickMarkValueForTickMarkCounter( tickCounter ) ) ) > minValueFloor; tickCounter++ ) {
					var tickMark = ( - ( tickMarkValueForTickMarkCounter( tickCounter ) ) );
					tickMarks.push( tickMark );
				}
				tickMarks.push( 0 );
				//  Add tick marks for > 0 
				for ( var tickCounter = 1; ( ( tickMarkValueForTickMarkCounter( tickCounter ) ) ) < maxValueCeil; tickCounter++ ) {
					var tickMark = ( ( tickMarkValueForTickMarkCounter( tickCounter ) ) );
					tickMarks.push( tickMark );
				}
			} else {
				var minValueFloorPt5 = ( Math.floor( ( minValue * 2 ) ) / 2 );
				var maxValueCeilPt5 = ( Math.ceil( ( maxValue * 2 ) ) / 2 );
				var maxValueCeilPt5MinusminValueFloorPt5 = maxValueCeilPt5 - minValueFloorPt5;
				var tickMarkIncrementForPt5 = maxValueCeilPt5MinusminValueFloorPt5 / 5;

				var tickMarkValueForTickMarkCounterLessThanTenGE1 = function( tickCounter ) {
					var tickMark = ( tickCounter * tickMarkIncrementForPt5 );
					return tickMark;
				};

				//  Add tick marks for < 0 
				for ( var tickCounter = 1; ( - ( tickMarkValueForTickMarkCounterLessThanTenGE1( tickCounter ) ) ) > minValue; tickCounter++ ) {
					var tickMark = ( - ( tickMarkValueForTickMarkCounterLessThanTenGE1( tickCounter ) ) );
					tickMarks.push( tickMark );
				}
				tickMarks.push( 0 );
				//  Add tick marks for > 0 
				for ( var tickCounter = 1; ( ( tickMarkValueForTickMarkCounterLessThanTenGE1( tickCounter ) ) ) < maxValue; tickCounter++ ) {
					var tickMark = ( ( tickMarkValueForTickMarkCounterLessThanTenGE1( tickCounter ) ) );
					tickMarks.push( tickMark );
				}
			}
		} else {

			if ( dataPointCount === 1 ) {
				tickMarks.push( minValue );
				tickMarks.push( ( minValue + ( maxValue - minValue ) / 2 ) );
				tickMarks.push( maxValue );

			} else if ( ( maxValue - minValue ) >= 10 ) {
				var tickMarkValueForTickMarkCounterNoZero = function( tickCounter ) {
					return minValueFloor + ( tickCounter * tickMarkIncrementFromZero );
				};
				for ( var tickCounter = 1; ( ( tickMarkValueForTickMarkCounterNoZero( tickCounter ) ) ) < maxValue; tickCounter++ ) {
					var tickMark = ( ( tickMarkValueForTickMarkCounterNoZero( tickCounter ) ) );
					tickMarks.push( tickMark );
				}
			} else if ( ( maxValue - minValue ) >= 1 ) {
				var minValueFloorPt5 = ( Math.floor( ( minValue * 2 ) ) / 2 );
				var maxValueCeilPt5 = ( Math.ceil( ( maxValue * 2 ) ) / 2 );
				var maxValueCeilPt5MinusminValueFloorPt5 = maxValueCeilPt5 - minValueFloorPt5;
				var tickMarkIncrementForPt5 = maxValueCeilPt5MinusminValueFloorPt5 / 5;

				var tickMarkValueForTickMarkCounterLessThanTenGE1 = function( tickCounter ) {
					return minValueFloorPt5 + ( tickCounter * tickMarkIncrementForPt5 );
				};
				for ( var tickCounter = 1; ( ( tickMarkValueForTickMarkCounterLessThanTenGE1( tickCounter ) ) ) < maxValue; tickCounter++ ) {
					var tickMark = ( ( tickMarkValueForTickMarkCounterLessThanTenGE1( tickCounter ) ) );
					tickMarks.push( tickMark );
				}
			} else {
				if ( ( maxValue - minValue ) > .2 ) {
					var minValueFloorPt1 = ( Math.floor( ( minValue * 10 ) ) / 10 );
					var maxValueCeilPt1 = ( Math.ceil( ( maxValue * 10 ) ) / 10 );
					var maxValueCeilPt1MinusminValueFloorPt1 = maxValueCeilPt1 - minValueFloorPt1;
					var tickMarkIncrementForPt1 = maxValueCeilPt1MinusminValueFloorPt1 / 5;

					var tickMarkValueForTickMarkCounterLessThanOneGtPt2 = function( tickCounter ) {
						var tickMarkVal = minValueFloorPt1 + ( tickCounter * tickMarkIncrementForPt1 );
						return tickMarkVal;
					};
					for ( var tickCounter = 1; ( ( tickMarkValueForTickMarkCounterLessThanOneGtPt2( tickCounter ) ) ) < maxValue; tickCounter++ ) {
						var tickMark = ( ( tickMarkValueForTickMarkCounterLessThanOneGtPt2( tickCounter ) ) );
						tickMarks.push( tickMark );
					}
				} else {
					tickMarks.push( minValue );
					tickMarks.push( ( minValue + ( maxValue - minValue ) / 2 ) );
					tickMarks.push( maxValue );
				}
			}
		}
		
		
		return tickMarks;
	};
	
	/**
	 * for horizontal Retention Time axis
	 */
	this._get_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlot_Chart_Horizontal_TickMarks = function( params ) {
		var maxValue = params.maxValue;
		var minValue = params.minValue;
		
		var SCALE_FACTOR = 10;
		
		//  Compute max and min tick marks, to next higher and lower multiple of SCALE_FACTOR
		var maxTickMark = Math.ceil( maxValue / SCALE_FACTOR ) * SCALE_FACTOR;
		var minTickMark = Math.floor( minValue / SCALE_FACTOR ) * SCALE_FACTOR;
		
		var maxMinusMin_TickMark =  maxTickMark - minTickMark;
		
		//  Create tick marks at min, 25%, 50%, 75%, max
		var tickMarks = [ 
			minTickMark, 
			minTickMark + ( maxMinusMin_TickMark * .25 ),
			minTickMark + ( maxMinusMin_TickMark * .5 ),
			minTickMark + ( maxMinusMin_TickMark * .75 ),
			maxTickMark
			];
		return tickMarks;
	};
		
	//////////////////////////////////////////////////////////////////

	//    PPM Error Vs M/Z for PSMs Scatter Plot
	

	/**
	 * Clear data for  PPM Error for PSMs Scatter Plot
	 */
	this.clear_PPM_Error_Vs_M_over_Z_For_PSMs_ScatterPlot = function() {
		
		_PPM_Error_Vs_M_over_Z_For_PSMs_ErrorEstimates_isLoaded = _IS_LOADED_NO;

		var $PSM_PPM_Error_Vs_M_over_Z_CountsBlock = $("#PSM_PPM_Error_Vs_M_over_Z_CountsBlock");
		if ( $PSM_PPM_Error_Vs_M_over_Z_CountsBlock.length === 0 ) {
			throw Error( "unable to find HTML element with id 'PSM_PPM_Error_Vs_M_over_Z_CountsBlock'" );
		}
		$PSM_PPM_Error_Vs_M_over_Z_CountsBlock.empty();
		
		//  Abort any active AJAX calls
		if ( _load_PPM_Error_Vs_M_over_Z_For_PSMs_ScatterPlotForLinkTypeActiveAjax ) {
			var objKeys = Object.keys( _load_PPM_Error_Vs_M_over_Z_For_PSMs_ScatterPlotForLinkTypeActiveAjax ); 
			objKeys.forEach(function( element, index, array ) {
				var selectedLinkType = element;
				if ( _load_PPM_Error_Vs_M_over_Z_For_PSMs_ScatterPlotForLinkTypeActiveAjax[ selectedLinkType ] ) {
					_load_PPM_Error_Vs_M_over_Z_For_PSMs_ScatterPlotForLinkTypeActiveAjax[ selectedLinkType ].abort();
					_load_PPM_Error_Vs_M_over_Z_For_PSMs_ScatterPlotForLinkTypeActiveAjax[ selectedLinkType ] = null;
				}
			}, this )
		}
		_load_PPM_Error_Vs_M_over_Z_For_PSMs_ScatterPlotForLinkTypeActiveAjax = null;
	};

	/**
	 * If not loaded, call load_PPM_Error_Vs_M_over_Z_For_PSMs_ScatterPlot()
	 */
	this.load_PPM_Error_Vs_M_over_Z_For_PSMs_ScatterPlotIfNeeded = function() {
		
		if ( _PPM_Error_Vs_M_over_Z_For_PSMs_ErrorEstimates_isLoaded === _IS_LOADED_NO ) {
			this.load_PPM_Error_Vs_M_over_Z_For_PSMs_ScatterPlot();
		}
	};
	
	/**
	 * Load the data for  PPM Error for PSMs Scatter Plot
	 */
	this.load_PPM_Error_Vs_M_over_Z_For_PSMs_ScatterPlot = function() {
		var objectThis = this;

		_PPM_Error_Vs_M_over_Z_For_PSMs_ErrorEstimates_isLoaded = _IS_LOADED_LOADING;

		var $PSM_PPM_Error_Vs_M_over_Z_CountsBlock = $("#PSM_PPM_Error_Vs_M_over_Z_CountsBlock");
		if ( $PSM_PPM_Error_Vs_M_over_Z_CountsBlock.length === 0 ) {
			throw Error( "unable to find HTML element with id 'PSM_PPM_Error_Vs_M_over_Z_CountsBlock'" );
		}
		$PSM_PPM_Error_Vs_M_over_Z_CountsBlock.empty();

		var selectedLinkTypes = _hash_json_Contents.linkTypes;
		
		if ( ! _anySearchesHaveScanDataYes ) {

			// Show cells for selected link types
			for ( var index = 0; index < selectedLinkTypes.length; index++ ) {
				var selectedLinkType = selectedLinkTypes[ index ];
				//  Add empty chart with Loading message

				var $chart_outer_container_jq =
					this._addChartOuterTemplate( { $chart_group_container_table_jq : $PSM_PPM_Error_Vs_M_over_Z_CountsBlock } );
				this._placeEmptyDummyChartForMessage( { 
					$chart_outer_container_jq : $chart_outer_container_jq, 
//					linkType : selectedLinkType, 
					messageWhole:  _DUMMY_CHART_STATUS_WHOLE_TEXT_SCANS_NOT_UPLOADED
				} );
			}

			_PPM_Error_For_PSMs_Statistics_isLoaded = _IS_LOADED_YES;
			
			//  Exit since no data to display
			
			return;  //  EARLY EXIT
		}		

		//  Make a copy of _hash_json_Contents    (  true for deep, target object, source object, <source object 2>, ... )
		var hash_json_Contents_COPY = $.extend( true /*deep*/,  {}, _hash_json_Contents );
		
		// Add cells for selected link types
		selectedLinkTypes.forEach( function ( currentArrayValue, index, array ) {
			var selectedLinkType = currentArrayValue;

			//  Add empty chart with Loading message
			var $chart_outer_container_jq =
				this._addChartOuterTemplate( { linkType : selectedLinkType, $chart_group_container_table_jq : $PSM_PPM_Error_Vs_M_over_Z_CountsBlock } );

			//  Add empty chart with Loading message
			this._placeEmptyDummyChartForMessage( { 
				$chart_outer_container_jq : $chart_outer_container_jq, 
			//				linkType : selectedLinkType, 
				messagePrefix:  _DUMMY_CHART_STATUS_TEXT_PREFIX_LOADING,
				messageSuffix:  _DUMMY_CHART_STATUS_TEXT_SUFFIX_LOADING
			} );
			
			//  For each selected link type, 
			//    set it as selected link type in hash_json_Contents_COPY,
			//      and create a chart for that
			
			hash_json_Contents_COPY.linkTypes = [ selectedLinkType ];
			
			this.load_PPM_Error_Vs_M_over_Z_For_PSMs_ScatterPlotForLinkType( {  
					selectedLinkType : selectedLinkType,
					hash_json_Contents_COPY : hash_json_Contents_COPY,
					$chart_outer_container_jq : $chart_outer_container_jq
			});

		}, this /* passed to function as this */ );
		
	};
	
	var _load_PPM_Error_Vs_M_over_Z_For_PSMs_ScatterPlotForLinkTypeActiveAjax = null;

	/**
	 * Load the data for  PPM Error Vs M/Z for PSMs Scatter Plot Specific Link Type
	 */
	this.load_PPM_Error_Vs_M_over_Z_For_PSMs_ScatterPlotForLinkType = function( params ) {
		var objectThis = this;

		var selectedLinkType = params.selectedLinkType;
		var hash_json_Contents_COPY = params.hash_json_Contents_COPY;
		var $chart_outer_container_jq = params.$chart_outer_container_jq; 
			
		var hash_json_field_Contents_JSONString = JSON.stringify( hash_json_Contents_COPY );
		var ajaxRequestData = {
				project_search_id : _project_search_ids,
				filterCriteria : hash_json_field_Contents_JSONString
		};
		if ( _load_PPM_Error_Vs_M_over_Z_For_PSMs_ScatterPlotForLinkTypeActiveAjax && 
				_load_PPM_Error_Vs_M_over_Z_For_PSMs_ScatterPlotForLinkTypeActiveAjax[ selectedLinkType ] ) {
			_load_PPM_Error_Vs_M_over_Z_For_PSMs_ScatterPlotForLinkTypeActiveAjax[ selectedLinkType ].abort();
			_load_PPM_Error_Vs_M_over_Z_For_PSMs_ScatterPlotForLinkTypeActiveAjax[ selectedLinkType ] = null;
		}
		if ( ! _load_PPM_Error_Vs_M_over_Z_For_PSMs_ScatterPlotForLinkTypeActiveAjax ) {
			_load_PPM_Error_Vs_M_over_Z_For_PSMs_ScatterPlotForLinkTypeActiveAjax = {};
		}
		//  Set to returned jQuery XMLHttpRequest (jqXHR) object
		_load_PPM_Error_Vs_M_over_Z_For_PSMs_ScatterPlotForLinkTypeActiveAjax[ selectedLinkType ] = 
			$.ajax({
//				cache : false,
				url : contextPathJSVar + "/services/qc/dataPage/ppmErrorVsM_over_Z", // ppmErrorVsM_over_Z
				traditional: true,  //  Force traditional serialization of the data sent
				//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
				//   So project_search_ids array is passed as "project_search_ids=<value>" which is what Jersey expects
				data : ajaxRequestData,  // The data sent as params on the URL
				dataType : "json",
				success : function( ajaxResponseData ) {
					try {
						if ( _load_PPM_Error_Vs_M_over_Z_For_PSMs_ScatterPlotForLinkTypeActiveAjax ) {
							_load_PPM_Error_Vs_M_over_Z_For_PSMs_ScatterPlotForLinkTypeActiveAjax[ selectedLinkType ] = null;
						}
						var responseParams = {
								ajaxResponseData : ajaxResponseData, 
								ajaxRequestData : ajaxRequestData,
								selectedLinkType : selectedLinkType,
								$chart_outer_container_jq : $chart_outer_container_jq
						};
						objectThis.load_PPM_Error_Vs_M_over_Z_For_PSMs_ScatterPlotResponse( responseParams );
					} catch( e ) {
						reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
						throw e;
					}
				},
				failure: function(errMsg) {
					if ( _load_PPM_Error_Vs_M_over_Z_For_PSMs_ScatterPlotForLinkTypeActiveAjax ) {
						_load_PPM_Error_Vs_M_over_Z_For_PSMs_ScatterPlotForLinkTypeActiveAjax[ selectedLinkType ] = null;
					}
					handleAJAXFailure( errMsg );
				},
				error : function(jqXHR, textStatus, errorThrown) {
					if ( _load_PPM_Error_Vs_M_over_Z_For_PSMs_ScatterPlotForLinkTypeActiveAjax ) {
						_load_PPM_Error_Vs_M_over_Z_For_PSMs_ScatterPlotForLinkTypeActiveAjax[ selectedLinkType ] = null;
					}
					if ( objectThis._passAJAXErrorTo_handleAJAXError(jqXHR, textStatus, errorThrown) ) {
						handleAJAXError(jqXHR, textStatus, errorThrown);
					}
				}
			});
	};

	/**
	 * Process AJAX Response
	 */
	this.load_PPM_Error_Vs_M_over_Z_For_PSMs_ScatterPlotResponse = function( params ) {
		var objectThis = this;

		var ajaxResponseData = params.ajaxResponseData;
		var ajaxRequestData = params.ajaxRequestData;
		var selectedLinkType = params.selectedLinkType;
		var $chart_outer_container_jq = params.$chart_outer_container_jq;
		
		var ppmErrorVsM_over_ZScatterPlotResult = ajaxResponseData.ppmErrorVsM_over_ZScatterPlotResult;
		var dataForChartPerLinkTypeList = ppmErrorVsM_over_ZScatterPlotResult.dataForChartPerLinkTypeList;
		
		if ( dataForChartPerLinkTypeList.length !== 1 ) {
			throw Error( "dataForChartPerLinkTypeList.length !== 1, is = " + dataForChartPerLinkTypeList.length );
		}
		
		var $PSM_PPM_Error_Vs_M_over_Z_CountsBlock = $("#PSM_PPM_Error_Vs_M_over_Z_CountsBlock");
		if ( $PSM_PPM_Error_Vs_M_over_Z_CountsBlock.length === 0 ) {
			throw Error( "unable to find HTML element with id 'PSM_PPM_Error_Vs_M_over_Z_CountsBlock'" );
		}

		var entryForLinkType = dataForChartPerLinkTypeList[ 0 ];
		
		var linkType = entryForLinkType.linkType;
		var m_over_ZBuckets = entryForLinkType.m_over_ZBuckets;


		$chart_outer_container_jq.empty();

		if ( m_over_ZBuckets === null || m_over_ZBuckets.length === 0 ) {
			//  No data for this link type

			//  Add empty chart with No Data message
			this._placeEmptyDummyChartForMessage( { 
				$chart_outer_container_jq : $chart_outer_container_jq, 
			//				linkType : selectedLinkType, 
				messagePrefix:  _DUMMY_CHART_STATUS_TEXT_PREFIX_NO_DATA,
				messageSuffix:  _DUMMY_CHART_STATUS_TEXT_SUFFIX_NO_DATA
			} );
			
			return;  //  EARLY RETURN
		}

		var $chart_container_jq = this._addChartInnerTemplate( { $chart_outer_container_jq : $chart_outer_container_jq } );

		var colorAndbarColor = this.getColorAndBarColorFromLinkType( linkType );

		this._add_PPM_Error_Vs_M_over_Z_For_PSMs_Histogram_Chart( { entryForLinkType: entryForLinkType, colorAndbarColor: colorAndbarColor, $chartContainer : $chart_container_jq } );

		chartDownload.addDownloadClickHandlers( { $chart_outer_container_for_download_jq :  $chart_outer_container_jq } );
		// Add tooltips for download links
		addToolTips( $chart_outer_container_jq );
	
		//  TODO  FIX THIS
		
//		_PPM_Error_Vs_M_over_Z_Vs_M_over_Z_For_PSMs_ErrorEstimates_isLoaded = _IS_LOADED_YES;
	};
	
	//  Overridden for Specific elements like Chart Title and X and Y Axis labels
	var _PPM_Error_Vs_M_over_Z_For_PSMs_CHART_GLOBALS = {
			_CHART_DEFAULT_FONT_SIZE : 12,  //  Default font size - using to set font size for tick marks.
			_TITLE_FONT_SIZE : 15, // In PX
			_AXIS_LABEL_FONT_SIZE : 14, // In PX
			_TICK_MARK_TEXT_FONT_SIZE : 14, // In PX
	}

	/**
	 * 
	 */
	this._add_PPM_Error_Vs_M_over_Z_For_PSMs_Histogram_Chart = function( params ) {
		var entryForLinkType = params.entryForLinkType;
		var colorAndbarColor = params.colorAndbarColor;
		var $chartContainer = params.$chartContainer;
		
		var linkType = entryForLinkType.linkType;

		var m_over_ZBuckets = entryForLinkType.m_over_ZBuckets;

		//  All PPM Error Bin Start values, sorted smallest to largest
		var ppmErrorBinStartDistinctSorted = entryForLinkType.ppmErrorBinStartDistinctSorted;
		
		var numScans = entryForLinkType.numScans;
		
		var ppmErrorBinMin = entryForLinkType.ppmErrorBinMin;
		var ppmErrorBinMax = entryForLinkType.ppmErrorBinMax;
		var ppmErrorPossibleMax = entryForLinkType.ppmErrorPossibleMax;
		var m_over_ZBinMin = entryForLinkType.m_over_ZBinMin;
		var m_over_ZBinMax = entryForLinkType.m_over_ZBinMax;
		var m_over_ZPossibleMax = entryForLinkType.m_over_ZPossibleMax;

		//  chart data for Google charts
		var chartData = [];
		
		var OPACITY_DEFAULT = "0.5";
		
		//  opacity: null  Use Default

		// Ranges:  1-3, 4-10, 11-17, 18+
		
		//  Only last element can have and must have "min" property 
		var SERIES_SETTINGS = [
			{ max: 3, color: _PROXL_COLOR_SITE_BLUE, pointSize : 4, opacity: null }
			,{ max: 10, color: _PROXL_COLOR_SITE_GREEN, pointSize : 5, opacity: null }
			,{ max: 17, color: '#ddbf17', pointSize : 6, opacity: null }
			,{ min: 18, color: _PROXL_COLOR_SITE_RED, pointSize : 7, opacity: null }
		];
		
		
		
		var addHeaderEntry = function( label ) {
			chartDataHeaderEntry.push( {label: label, type: 'number'} );
			chartDataHeaderEntry.push( {type:'string', role: 'style' } );
//			, {type:'string', role: "tooltip", 'p': {'html': true} }
		}

		var chartDataHeaderEntry = [ 'M/Z' ];

		var prevMax = 0;
		for ( var seriesSettingsIndex = 0; seriesSettingsIndex < SERIES_SETTINGS.length; seriesSettingsIndex++ ) {
			var entry = SERIES_SETTINGS[ seriesSettingsIndex ];
			if ( entry.max !== undefined && entry.max !== null ) {
				addHeaderEntry( ( prevMax + 1 ) + "-" + entry.max );
			} else {
				// No max so must contain min
				if ( entry.min === undefined || entry.min === null ) {
					throw Error( "SERIES_SETTINGS element not contain min or max property" );
				}
				addHeaderEntry( entry.min + "+" );
			}
			prevMax = entry.max;
		}
		
		chartData.push( chartDataHeaderEntry );

		var maxCount = 0;
		
		var totalCount = 0;

		var max_M_over_Z_Error = null;
		var min_M_over_Z_Error = null;

		var maxPPMError = null;
		var minPPMError = null;

		for ( var m_over_ZBucketsIndex = 0; m_over_ZBucketsIndex < m_over_ZBuckets.length; m_over_ZBucketsIndex++ ) {
			var m_over_ZBucket = m_over_ZBuckets[ m_over_ZBucketsIndex ];
			var chartBuckets = m_over_ZBucket.chartBuckets;

			for ( var bucketIndex = 0; bucketIndex < chartBuckets.length; bucketIndex++ ) {
				var bucket = chartBuckets[ bucketIndex ];

				var m_over_ZStart = bucket.m_over_ZStart;
				var m_over_ZEnd = bucket.m_over_ZEnd;
				var ppmErrorStart = bucket.ppmErrorStart;
				var ppmErrorEnd = bucket.ppmErrorEnd;
				var count = bucket.count;
				
				var m_over_ZCenter = m_over_ZStart + ( ( m_over_ZEnd - m_over_ZStart ) / 2 );
				var ppmErrorCenter = ppmErrorStart + ( ( ppmErrorEnd - ppmErrorStart ) / 2 );
				
//				var tooltipText = "<div style='padding: 4px;'>Count: " + bucket.count +
//				"<br>PPM Error approximately " + 
//				bucket.binStart.toPrecision( _PPM_ERROR_DISPLAY_MAX_SIGNIFICANT_DIGITS ) + 
//				" to " + bucket.binEnd.toPrecision( _PPM_ERROR_DISPLAY_MAX_SIGNIFICANT_DIGITS ) +
//				"</div>";

				var entryAnnotationText = bucket.count;
				
				var chartEntrySpecificSeries = [
					ppmErrorCenter 		// Y axis
					, 'fill-opacity : ??'  // style
				];
				
				var fillOpacityStyleOption = 'fill-opacity : ';

				//  Style of point
//				colorAndbarColor.barColor,
				//  Style appears to be ignored
//				' color : orange; ' +
//				'opacity : 0; '+
//				'stroke-width : 0; ' 
//				+
//				'stroke-color : red; ' +
//				'stroke-opacity : 1; ' +
//				'fill-color : red; ' +
//				'fill-opacity : .2'
//				,
//				//  Tool Tip
//				tooltipText
////				,
////				entryAnnotationText

				var chartEntry = [ m_over_ZCenter ];  	// X axis

				//  Copy chartEntrySpecificSeries to chartEntry
				var copy_chartEntrySpecificSeriesTo_chartEntry = function( ) {
					for ( var copy_chartEntrySpecificSeriesTo_chartEntry_index = 0; copy_chartEntrySpecificSeriesTo_chartEntry_index < chartEntrySpecificSeries.length; copy_chartEntrySpecificSeriesTo_chartEntry_index++ ) {
						var chartEntrySpecificSeriesEntry = chartEntrySpecificSeries[ copy_chartEntrySpecificSeriesTo_chartEntry_index ];
						chartEntry.push( chartEntrySpecificSeriesEntry );
					}
				}

				//  Fill in for other series
				var addNullEntriesToChartEntryForSeriesCount = function( seriesCount ) {
					var counterMax = seriesCount * chartEntrySpecificSeries.length;
					for ( var counter = 0; counter < counterMax ; counter++ ) {
						chartEntry.push( null );
					}
				}

				for ( var seriesSettingsIndex = 0; seriesSettingsIndex < SERIES_SETTINGS.length; seriesSettingsIndex++ ) {
					var entry = SERIES_SETTINGS[ seriesSettingsIndex ];
					if ( entry.max !== undefined && entry.max !== null ) {
						if ( count <= entry.max ) {
							addNullEntriesToChartEntryForSeriesCount( seriesSettingsIndex );
							var opacityString = null;
							if ( entry.opacity !== undefined && entry.opacity !== null ) {
								opacityString = opacityString;
							} else {
								opacityString = OPACITY_DEFAULT;
							}
							chartEntrySpecificSeries[ 1 ] = fillOpacityStyleOption + opacityString;
							copy_chartEntrySpecificSeriesTo_chartEntry( chartEntry, chartEntrySpecificSeries );
							addNullEntriesToChartEntryForSeriesCount( SERIES_SETTINGS.length - ( seriesSettingsIndex + 1 ) );
							break;
						}
					} else {
						// No max so must contain min
						if ( entry.min === undefined || entry.min === null ) {
							throw Error( "SERIES_SETTINGS element not contain min or max property" );
						}
						if ( count >= entry.min ) {
							addNullEntriesToChartEntryForSeriesCount( seriesSettingsIndex );
							var opacityString = null;
							if ( entry.opacity !== undefined && entry.opacity !== null ) {
								opacityString = opacityString;
							} else {
								opacityString = OPACITY_DEFAULT;
							}
							chartEntrySpecificSeries[ 1 ] = fillOpacityStyleOption + opacityString;
							copy_chartEntrySpecificSeriesTo_chartEntry( chartEntry, chartEntrySpecificSeries );
							break;
						}		
					}
				}
				
				chartData.push( chartEntry );

				//  Update Max Min and Total
				totalCount += bucket.count;
				if ( bucket.count > maxCount ) {
					maxCount = bucket.count;
				}
				if ( maxPPMError === null || ppmErrorEnd > maxPPMError ) {
					maxPPMError = ppmErrorEnd;
				}
				if ( minPPMError === null || ppmErrorStart < minPPMError ) {
					minPPMError = ppmErrorStart;
				}
				if ( max_M_over_Z_Error === null || m_over_ZEnd > max_M_over_Z_Error ) {
					max_M_over_Z_Error = m_over_ZEnd;
				}
				if ( min_M_over_Z_Error === null || m_over_ZStart < min_M_over_Z_Error ) {
					min_M_over_Z_Error = m_over_ZStart;
				}
			}
		}
		
		var vAxisTicks = 
			this._get_PPM_Error_Vs_M_over_Z_For_PSMs_ScatterPlot_Chart_Vertical_TickMarks( { maxValue : maxPPMError, minValue : minPPMError } );
		var hAxisTicks = 
			this._get_PPM_Error_Vs_M_over_Z_For_PSMs_ScatterPlot_Chart_Horizontal_TickMarks( { maxValue : max_M_over_Z_Error, minValue : min_M_over_Z_Error } );
		
		//  Build Series Config and Color Arrays
		var seriesConfig = [];
		var barColors = []; // must be an array
		
		for ( var seriesSettingsIndex = 0; seriesSettingsIndex < SERIES_SETTINGS.length; seriesSettingsIndex++ ) {
			var entry = SERIES_SETTINGS[ seriesSettingsIndex ];
			var seriesConfigEntry = { color: entry.color, pointSize: entry.pointSize };
			seriesConfig.push( 	seriesConfigEntry );
			barColors.push( entry.color );
		}

		var chartTitle = 'PPM Error vs/ M/Z (' + linkType + ")";
		var optionsFullsize = {
			//  Overridden for Specific elements like Chart Title and X and Y Axis labels
				fontSize: _PPM_Error_Vs_M_over_Z_For_PSMs_CHART_GLOBALS._CHART_DEFAULT_FONT_SIZE,  //  Default font size - using to set font size for tick marks.
				series: seriesConfig,
//				pointSize: 20,
				title: chartTitle, // Title above chart
			    titleTextStyle: {
			    	color : _PROXL_DEFAULT_FONT_COLOR, //  Set default font color
//			        color: <string>,    // any HTML string color ('red', '#cc00cc')
//			        fontName: <string>, // i.e. 'Times New Roman'
			        fontSize: _PPM_Error_Vs_M_over_Z_For_PSMs_CHART_GLOBALS._TITLE_FONT_SIZE // 12, 18 whatever you want (don't specify px)
//			        bold: <boolean>,    // true or false
//			        italic: <boolean>   // true of false
			    },
				//  X axis label below chart
				hAxis: 
				{ 	title: 'M/Z'
					, titleTextStyle: { color: 'black', fontSize: _PPM_Error_Vs_M_over_Z_For_PSMs_CHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
					,gridlines: {  
		                color: 'none'  //  No vertical grid lines on the horzontal axis
		            }
//					,baseline: 0     // always start at zero
//					,baselineColor: 'none' // Hide 'Zero' line
//					,baselineColor: '#CDCDCD' // Make 'Zero' line really light
					,ticks: hAxisTicks
//					,ticks: [-100,0,100]
					,maxValue : max_M_over_Z_Error
					,minValue : min_M_over_Z_Error
				},  
				//  Y axis label left of chart
				vAxis: 
				{ 	title: 'PPM Error'
					, titleTextStyle: { color: 'black', fontSize: _PPM_Error_Vs_M_over_Z_For_PSMs_CHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
//					,baseline: 0     // always start at zero
					,ticks: vAxisTicks
//					,maxValue : maxCount
				,maxValue : maxPPMError
				,minValue : minPPMError

				},
//				legend: { position: 'none' }, //  position: 'none':  Don't show legend of bar colors in upper right corner
				//  Size picked up from containing HTML element
//				width : 500, 
//				height : 300,   // width and height of chart, otherwise controlled by enclosing div
//				bar: { groupWidth: '100%' },  // set bar width large to eliminate space between bars
				colors: barColors,
				tooltip: {isHtml: true}
		};        
		// create the chart
		var data = google.visualization.arrayToDataTable( chartData );
		
		var chartFullsize = new google.visualization.ScatterChart( $chartContainer[0] );
		chartFullsize.draw( data, optionsFullsize );
		
		//  Using Material chart.  added to viewQC.jsp:  <c:set var="googleChartPackagesLoadAdditions">,"scatter"</c:set>
//		var chartFullsize = new google.charts.Scatter( $chartContainer[0] );
//		chartFullsize.draw( data, google.charts.Scatter.convertOptions( optionsFullsize ) );
		
	};


	/**
	 * for vertical PPM Error axis
	 */
	this._get_PPM_Error_Vs_M_over_Z_For_PSMs_ScatterPlot_Chart_Vertical_TickMarks = function( params ) {
		var maxValue = params.maxValue;
		var minValue = params.minValue;
		var dataPointCount = params.dataPointCount; // number of data points in plot
		
		//  Compute max and min tick marks, to next higher and lower multiple of 1
		var maxValueCeil = Math.ceil( maxValue );
		var minValueFloor = Math.floor( minValue );
		var maxCeilMinusMinFloor = maxValueCeil - minValueFloor;
		
		var tickMarkIncrementFromZero = maxCeilMinusMinFloor / 5;
		
		var tickMarks = [];
		
		if ( minValue < 0 && maxValue > 0 ) {
			
			if ( ( maxValue - minValue ) >= 10 ) {
				var tickMarkValueForTickMarkCounter = function( tickCounter ) {
					var tickMark = tickCounter * tickMarkIncrementFromZero;
					return tickMark; 
				};

				//  Add tick marks for < 0 
				for ( var tickCounter = 1; ( - ( tickMarkValueForTickMarkCounter( tickCounter ) ) ) > minValueFloor; tickCounter++ ) {
					var tickMark = ( - ( tickMarkValueForTickMarkCounter( tickCounter ) ) );
					tickMarks.push( tickMark );
				}
				tickMarks.push( 0 );
				//  Add tick marks for > 0 
				for ( var tickCounter = 1; ( ( tickMarkValueForTickMarkCounter( tickCounter ) ) ) < maxValueCeil; tickCounter++ ) {
					var tickMark = ( ( tickMarkValueForTickMarkCounter( tickCounter ) ) );
					tickMarks.push( tickMark );
				}
			} else {
				var minValueFloorPt5 = ( Math.floor( ( minValue * 2 ) ) / 2 );
				var maxValueCeilPt5 = ( Math.ceil( ( maxValue * 2 ) ) / 2 );
				var maxValueCeilPt5MinusminValueFloorPt5 = maxValueCeilPt5 - minValueFloorPt5;
				var tickMarkIncrementForPt5 = maxValueCeilPt5MinusminValueFloorPt5 / 5;

				var tickMarkValueForTickMarkCounterLessThanTenGE1 = function( tickCounter ) {
					var tickMark = ( tickCounter * tickMarkIncrementForPt5 );
					return tickMark;
				};

				//  Add tick marks for < 0 
				for ( var tickCounter = 1; ( - ( tickMarkValueForTickMarkCounterLessThanTenGE1( tickCounter ) ) ) > minValue; tickCounter++ ) {
					var tickMark = ( - ( tickMarkValueForTickMarkCounterLessThanTenGE1( tickCounter ) ) );
					tickMarks.push( tickMark );
				}
				tickMarks.push( 0 );
				//  Add tick marks for > 0 
				for ( var tickCounter = 1; ( ( tickMarkValueForTickMarkCounterLessThanTenGE1( tickCounter ) ) ) < maxValue; tickCounter++ ) {
					var tickMark = ( ( tickMarkValueForTickMarkCounterLessThanTenGE1( tickCounter ) ) );
					tickMarks.push( tickMark );
				}
			}
		} else {

			if ( dataPointCount === 1 ) {
				tickMarks.push( minValue );
				tickMarks.push( ( minValue + ( maxValue - minValue ) / 2 ) );
				tickMarks.push( maxValue );

			} else if ( ( maxValue - minValue ) >= 10 ) {
				var tickMarkValueForTickMarkCounterNoZero = function( tickCounter ) {
					return minValueFloor + ( tickCounter * tickMarkIncrementFromZero );
				};
				for ( var tickCounter = 1; ( ( tickMarkValueForTickMarkCounterNoZero( tickCounter ) ) ) < maxValue; tickCounter++ ) {
					var tickMark = ( ( tickMarkValueForTickMarkCounterNoZero( tickCounter ) ) );
					tickMarks.push( tickMark );
				}
			} else if ( ( maxValue - minValue ) >= 1 ) {
				var minValueFloorPt5 = ( Math.floor( ( minValue * 2 ) ) / 2 );
				var maxValueCeilPt5 = ( Math.ceil( ( maxValue * 2 ) ) / 2 );
				var maxValueCeilPt5MinusminValueFloorPt5 = maxValueCeilPt5 - minValueFloorPt5;
				var tickMarkIncrementForPt5 = maxValueCeilPt5MinusminValueFloorPt5 / 5;

				var tickMarkValueForTickMarkCounterLessThanTenGE1 = function( tickCounter ) {
					return minValueFloorPt5 + ( tickCounter * tickMarkIncrementForPt5 );
				};
				for ( var tickCounter = 1; ( ( tickMarkValueForTickMarkCounterLessThanTenGE1( tickCounter ) ) ) < maxValue; tickCounter++ ) {
					var tickMark = ( ( tickMarkValueForTickMarkCounterLessThanTenGE1( tickCounter ) ) );
					tickMarks.push( tickMark );
				}
			} else {
				if ( ( maxValue - minValue ) > .2 ) {
					var minValueFloorPt1 = ( Math.floor( ( minValue * 10 ) ) / 10 );
					var maxValueCeilPt1 = ( Math.ceil( ( maxValue * 10 ) ) / 10 );
					var maxValueCeilPt1MinusminValueFloorPt1 = maxValueCeilPt1 - minValueFloorPt1;
					var tickMarkIncrementForPt1 = maxValueCeilPt1MinusminValueFloorPt1 / 5;

					var tickMarkValueForTickMarkCounterLessThanOneGtPt2 = function( tickCounter ) {
						var tickMarkVal = minValueFloorPt1 + ( tickCounter * tickMarkIncrementForPt1 );
						return tickMarkVal;
					};
					for ( var tickCounter = 1; ( ( tickMarkValueForTickMarkCounterLessThanOneGtPt2( tickCounter ) ) ) < maxValue; tickCounter++ ) {
						var tickMark = ( ( tickMarkValueForTickMarkCounterLessThanOneGtPt2( tickCounter ) ) );
						tickMarks.push( tickMark );
					}
				} else {
					tickMarks.push( minValue );
					tickMarks.push( ( minValue + ( maxValue - minValue ) / 2 ) );
					tickMarks.push( maxValue );
				}
			}
		}
		
		
		return tickMarks;
	};
	
	/**
	 * for horizontal M/Z axis
	 */
	this._get_PPM_Error_Vs_M_over_Z_For_PSMs_ScatterPlot_Chart_Horizontal_TickMarks = function( params ) {
		var maxValue = params.maxValue;
		var minValue = params.minValue;
		
		var SCALE_FACTOR = 100;
		
		//  Compute max and min tick marks, to next higher and lower multiple of 100
		var maxTickMark = Math.ceil( maxValue / SCALE_FACTOR ) * SCALE_FACTOR;
		var minTickMark = Math.floor( minValue / SCALE_FACTOR ) * SCALE_FACTOR;
		
		var maxMinusMin_TickMark =  maxTickMark - minTickMark;
		
		//  Create tick marks at min, 25%, 50%, 75%, max
		var tickMarks = [ 
			minTickMark, 
			minTickMark + ( maxMinusMin_TickMark * .25 ),
			minTickMark + ( maxMinusMin_TickMark * .5 ),
			minTickMark + ( maxMinusMin_TickMark * .75 ),
			maxTickMark
			];
		return tickMarks;
	};
		
	
	//////////////////////////////////////////////////////////////////////

	//     Peptide Statistics

	/**
	 * Clear the data for Peptide 
	 */
	this.clear_Peptide_Level_Statistics = function() {
	
		this.clearPeptideLengthsHistogram();
	};
	
	/**
	 * Load the data for Peptide 
	 */
	this.load_Peptide_Level_StatisticsIfNeeded = function() {
	
		this.loadPeptideLengthsHistogramIfNeeded();
	};

	//////////////////////////////////////////////////////////////////////

	//     Peptide Lengths Histogram



	/**
	 * Clear data for Peptide Lengths Histogram
	 */
	this.clearPeptideLengthsHistogram = function() {
		
		_peptideLengthsHistogram_isLoaded = _IS_LOADED_NO;

		var $PeptideLengthsCountsBlock = $("#PeptideLengthsCountsBlock");
		if ( $PeptideLengthsCountsBlock.length === 0 ) {
			throw Error( "unable to find HTML element with id 'PeptideLengthsCountsBlock'" );
		}
		$PeptideLengthsCountsBlock.empty();
	};

	/**
	 * If not loaded, call loadPeptideLengthsHistogram()
	 */
	this.loadPeptideLengthsHistogramIfNeeded = function() {
		if ( _peptideLengthsHistogram_isLoaded === _IS_LOADED_NO ) {
			this.loadPeptideLengthsHistogram();
		}
	};
	
	var _loadPeptideLengthsHistogramActiveAjax = null;
	
	/**
	 * Load the data for  Peptide Lengths Histogram
	 */
	this.loadPeptideLengthsHistogram = function() {
		var objectThis = this;
		
		_peptideLengthsHistogram_isLoaded = _IS_LOADED_LOADING;
		
		var $PeptideLengthsCountsBlock = $("#PeptideLengthsCountsBlock");
		if ( $PeptideLengthsCountsBlock.length === 0 ) {
			throw Error( "unable to find HTML element with id 'PeptideLengthsCountsBlock'" );
		}
		$PeptideLengthsCountsBlock.empty();
		
		var selectedLinkTypes = _hash_json_Contents.linkTypes;
		
		//  Make a copy of _hash_json_Contents    (  true for deep, target object, source object, <source object 2>, ... )
		var hash_json_Contents_COPY = $.extend( true /*deep*/,  {}, _hash_json_Contents );
		
		// Add cells for selected link types
		selectedLinkTypes.forEach( function ( currentArrayValue, index, array ) {
			var selectedLinkType = currentArrayValue;

			//  Add empty chart with Loading message
			var $chart_outer_container_jq =
				this._addChartOuterTemplate( { linkType : selectedLinkType, $chart_group_container_table_jq : $PeptideLengthsCountsBlock } );
			this._placeEmptyDummyChartForMessage( { 
				$chart_outer_container_jq : $chart_outer_container_jq, 
			//				linkType : selectedLinkType, 
				messagePrefix:  _DUMMY_CHART_STATUS_TEXT_PREFIX_LOADING,
				messageSuffix:  _DUMMY_CHART_STATUS_TEXT_SUFFIX_LOADING
			} );
			
		}, this /* passed to function as this */ );
		
		
		var hash_json_field_Contents_JSONString = JSON.stringify( _hash_json_Contents );
		var ajaxRequestData = {
				project_search_id : _project_search_ids,
				filterCriteria : hash_json_field_Contents_JSONString
		};
		if ( _loadPeptideLengthsHistogramActiveAjax ) {
			_loadPeptideLengthsHistogramActiveAjax.abort();
			_loadPeptideLengthsHistogramActiveAjax = null;
		}
		//  Set to returned jQuery XMLHttpRequest (jqXHR) object
		_loadPeptideLengthsHistogramActiveAjax =
			$.ajax({
				url : contextPathJSVar + "/services/qc/dataPage/peptideLengthsHistogram",
				traditional: true,  //  Force traditional serialization of the data sent
				//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
				//   So project_search_ids array is passed as "project_search_ids=<value>" which is what Jersey expects
				data : ajaxRequestData,  // The data sent as params on the URL
				dataType : "json",
				success : function( ajaxResponseData ) {
					_loadPeptideLengthsHistogramActiveAjax = null;
					try {
						var responseParams = {
								ajaxResponseData : ajaxResponseData, 
								ajaxRequestData : ajaxRequestData
//								,
//								topTRelement : topTRelement
						};
						objectThis.loadPeptideLengthsHistogramResponse( responseParams );
//						$topTRelement.data( _DATA_LOADED_DATA_KEY, true );
					} catch( e ) {
						reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
						throw e;
					}
				},
				failure: function(errMsg) {
					_loadPeptideLengthsHistogramActiveAjax = null;
					handleAJAXFailure( errMsg );
				},
				error : function(jqXHR, textStatus, errorThrown) {
					_loadPeptideLengthsHistogramActiveAjax = null;
					if ( objectThis._passAJAXErrorTo_handleAJAXError(jqXHR, textStatus, errorThrown) ) {
						handleAJAXError(jqXHR, textStatus, errorThrown);
					}
				}
			});
	};
	
	/**
	 * Process the AJAX response for Peptide Lengths Counts
	 */
	this.loadPeptideLengthsHistogramResponse = function( params ) {
		var ajaxResponseData = params.ajaxResponseData;
		var ajaxRequestData = params.ajaxRequestData;
		
		var peptideLength_Histogram_For_PSMPeptideCutoffsResults = ajaxResponseData.peptideLength_Histogram_For_PSMPeptideCutoffsResults;
		var dataForChartPerLinkTypeList = peptideLength_Histogram_For_PSMPeptideCutoffsResults.dataForChartPerLinkTypeList;
		
		var $PeptideLengthsCountsBlock = $("#PeptideLengthsCountsBlock");
		if ( $PeptideLengthsCountsBlock.length === 0 ) {
			throw Error( "unable to find HTML element with id 'PeptideLengthsCountsBlock'" );
		}
		$PeptideLengthsCountsBlock.empty();

		for ( var indexForLinkType = 0; indexForLinkType < dataForChartPerLinkTypeList.length; indexForLinkType++ ) {
			var entryForLinkType = dataForChartPerLinkTypeList[ indexForLinkType ];
			var linkType = entryForLinkType.linkType;
			var chartBuckets = entryForLinkType.chartBuckets;

			if ( chartBuckets === null || chartBuckets.length === 0 ) {
				//  No data for this link type

				var $chart_outer_container_jq =
					this._addChartOuterTemplate( { $chart_group_container_table_jq : $PeptideLengthsCountsBlock } );
				//  Add empty chart with No Data message
				this._placeEmptyDummyChartForMessage( { 
					$chart_outer_container_jq : $chart_outer_container_jq, 
				//				linkType : selectedLinkType, 
					messagePrefix:  _DUMMY_CHART_STATUS_TEXT_PREFIX_NO_DATA,
					messageSuffix:  _DUMMY_CHART_STATUS_TEXT_SUFFIX_NO_DATA
				} );
				
				continue;  //  EARLY CONTINUE
			}

			var $chart_outer_container_jq =
				this._addChartOuterTemplate( { $chart_group_container_table_jq : $PeptideLengthsCountsBlock } );
			var $chart_container_jq = this._addChartInnerTemplate( { $chart_outer_container_jq : $chart_outer_container_jq } );

			var colorAndbarColor = this.getColorAndBarColorFromLinkType( linkType );

			this._addPeptideLengthsHistogram_Chart( { entryForLinkType: entryForLinkType, colorAndbarColor : colorAndbarColor, $chartContainer : $chart_container_jq } );

			chartDownload.addDownloadClickHandlers( { $chart_outer_container_for_download_jq :  $chart_outer_container_jq } );
			// Add tooltips for download links
			addToolTips( $chart_outer_container_jq );
		}
		
		_peptideLengthsHistogram_isLoaded = _IS_LOADED_YES;
	};
	
	/**
	 * Overridden for Specific elements like Chart Title and X and Y Axis labels
	 */
	var PeptideLengthsCHART_GLOBALS = {
			_CHART_DEFAULT_FONT_SIZE : 12,  //  Default font size - using to set font size for tick marks.
			_TITLE_FONT_SIZE : 15, // In PX
			_AXIS_LABEL_FONT_SIZE : 14, // In PX
			_TICK_MARK_TEXT_FONT_SIZE : 14 // In PX
	}

	/**
	 * 
	 */
	this._addPeptideLengthsHistogram_Chart = function( params ) {
		var entryForLinkType = params.entryForLinkType;
		var colorAndbarColor = params.colorAndbarColor;
		var $chartContainer = params.$chartContainer;
		
		var linkType = entryForLinkType.linkType;
		var chartBuckets = entryForLinkType.chartBuckets;

		//  chart data for Google charts
		var chartData = [];

		var chartDataHeaderEntry = [ 'peptideLength', "Count", { role: 'style' }, {role: "tooltip", 'p': {'html': true} }
//			, {type: 'string', role: 'annotation'}
			]; 
		chartData.push( chartDataHeaderEntry );
		
		var minpeptideLength = null;
		var maxpeptideLength = null;
		
		var maxCount = 0;

		for ( var index = 0; index < chartBuckets.length; index++ ) {
			var bucket = chartBuckets[ index ];
			
			var tooltipText = "<div style='padding: 4px;'>Count: " + bucket.count +
			"<br>peptide length: " + bucket.binStart + " to " + bucket.binEnd + "</div>";
			
			var entryAnnotationText = bucket.count;
			
			var chartEntry = [ 
				bucket.binCenter,  
				bucket.count, 
				//  Style of the bar
				colorAndbarColor.barColor,
				//  Tool Tip
				tooltipText
//				,
//				entryAnnotationText
				 ];
			chartData.push( chartEntry );
			if ( bucket.count > maxCount ) {
				maxCount = bucket.count;
			}
		}
		
		var vAxisTicks = this._getPeptideLengthsHistogram_ChartTickMarks( { maxValue : maxCount } );
		
		var barColors = [ colorAndbarColor.color ]; // must be an array

		var chartTitle = 'Peptide Count vs/ Length (' + linkType + ")";
		var optionsFullsize = {
			//  Overridden for Specific elements like Chart Title and X and Y Axis labels
				fontSize: PeptideLengthsCHART_GLOBALS._CHART_DEFAULT_FONT_SIZE,  //  Default font size - using to set font size for tick marks.
							
				title: chartTitle, // Title above chart
			    titleTextStyle: {
			    	color : _PROXL_DEFAULT_FONT_COLOR, //  Set default font color
//			        color: <string>,    // any HTML string color ('red', '#cc00cc')
//			        fontName: <string>, // i.e. 'Times New Roman'
			        fontSize: PeptideLengthsCHART_GLOBALS._TITLE_FONT_SIZE, // 12, 18 whatever you want (don't specify px)
//			        bold: <boolean>,    // true or false
//			        italic: <boolean>   // true of false
			    },
				//  X axis label below chart
				hAxis: { title: 'Peptide Length', titleTextStyle: { color: 'black', fontSize: PeptideLengthsCHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
			    	,minValue : entryForLinkType.peptideLengthMin
			    	,maxValue : entryForLinkType.peptideLengthMax
					,gridlines: {  
		                color: 'none'  //  No vertical grid lines on the horzontal axis
		            }
				},  
				//  Y axis label left of chart
				vAxis: { title: 'Count', titleTextStyle: { color: 'black', fontSize: PeptideLengthsCHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
//					,baseline: 0     // always start at zero
					,ticks: vAxisTicks
					,maxValue : maxCount
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
		
		chartFullsize.draw(data, optionsFullsize);
		
		//  Temp code to find <rect> that are the actual data columns
		//     Changing them to green to allow show that they are only the data columns and not other <rect> in the <svg>
		
//		var $rectanglesInChart_All = $chartContainer.find("rect");
//		
//		$rectanglesInChart_All.each( function() {
//			var $rectangleInChart = $( this );
//			var rectangleFillColor = $rectangleInChart.attr("fill");
//			if ( rectangleFillColor !== undefined ) {
//				if ( rectangleFillColor.toLowerCase() === _OVERALL_GLOBALS.BAR_COLOR_CROSSLINK.toLowerCase() ) {
//					$rectangleInChart.attr("fill","green");
//					var z = 0;
//				}
//			}
//		});
		
	};

	/**
	 * 
	 */
	this._getPeptideLengthsHistogram_ChartTickMarks = function( params ) {
		var maxValue = params.maxValue;
		if ( maxValue < 5 ) {
			var tickMarks = [ 0 ];
			for ( var counter = 1; counter <= maxValue; counter++ ) {
				tickMarks.push( counter );
			}
			return tickMarks;
		}
		return undefined; //  Use defaults
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
				fontSize: _PPM_Error_Vs_M_over_Z_For_PSMs_CHART_GLOBALS._CHART_DEFAULT_FONT_SIZE,  //  Default font size - using to set font size for tick marks.
				pointSize: 0,
				legend: 'none',
//				title: chartTitle, // Title above chart
			    titleTextStyle: {
			    	color : _PROXL_DEFAULT_FONT_COLOR, //  Set default font color
//			        color: <string>,    // any HTML string color ('red', '#cc00cc')
//			        fontName: <string>, // i.e. 'Times New Roman'
			        fontSize: _PPM_Error_Vs_M_over_Z_For_PSMs_CHART_GLOBALS._TITLE_FONT_SIZE // 12, 18 whatever you want (don't specify px)
//			        bold: <boolean>,    // true or false
//			        italic: <boolean>   // true of false
			    },
				//  X axis label below chart
				hAxis: 
				{ 	title: 'X Axis'
					, titleTextStyle: { color: 'black', fontSize: _PPM_Error_Vs_M_over_Z_For_PSMs_CHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
					,gridlines: {  
		                color: 'none'  //  No vertical grid lines on the horzontal axis
		            }
//					,baseline: 0     // always start at zero
					,baselineColor: 'none' // Hide 'Zero' line
//					,baselineColor: '#CDCDCD' // Make 'Zero' line really light
					,ticks: [ 0, 1 ]
//					,maxValue : max_M_over_Z_Error
//					,minValue : min_M_over_Z_Error
				},  
				//  Y axis label left of chart
				vAxis: 
				{ 	title: 'Y Axis'
					, titleTextStyle: { color: 'black', fontSize: _PPM_Error_Vs_M_over_Z_For_PSMs_CHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
//					,baseline: 0     // always start at zero
					,baselineColor: 'none' // Hide 'Zero' line
					,ticks: [ 0, 1 ]
//					,ticks: vAxisTicks
//					,maxValue : maxCount
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
	
};



var viewQCPageCode = new ViewQCPageCode();

//  Copy to standard page level JS Code Object
//  Not currently supported  var standardFullPageCode = viewQCPageCode;
