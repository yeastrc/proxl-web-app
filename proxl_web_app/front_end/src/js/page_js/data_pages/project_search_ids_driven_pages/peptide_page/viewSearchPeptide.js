
//  viewSearchPeptide.js

//  Javascript for the viewSearchPeptide.jsp page

//  JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";



//Import header_main.js and children to ensure on the page
import { header_mainVariable } from 'page_js/common_all_pages/header_section_main_pages/header_main.js';


//  For showing Data for links (Drilldown) (Called by HTML onclick):
import { viewPsmsLoadedFromWebServiceTemplate } from 'page_js/data_pages/project_search_ids_driven_pages/common/viewPsmsLoadedFromWebServiceTemplate.js';


//  Import to make available on the page
import { searchesChangeDisplayOrder } from 'page_js/data_pages/project_search_ids_driven_pages/common/searchesChangeDisplayOrder.js';
import { searchesForPageChooser } from 'page_js/data_pages/project_search_ids_driven_pages/common/searchesForPageChooser.js';
import { sharePageURLShortener  } from 'page_js/data_pages/project_search_ids_driven_pages/common/sharePageURLShortener.js';
import { addSingleTooltipForProteinName } from 'page_js/data_pages/project_search_ids_driven_pages/common/createTooltipForProteinNames.js';
import { defaultPageView } from 'page_js/data_pages/project_search_ids_driven_pages/common/defaultPageView.js';



import { minimumPSM_Count_Filter } from 'page_js/data_pages/project_search_ids_driven_pages/common/minimumPSM_Count_Filter.js';
import { annotationDataDisplayProcessingCommonCode } from 'page_js/data_pages/project_search_ids_driven_pages/common/psmPeptideAnnDisplayDataCommon.js';
import { cutoffProcessingCommonCode } from 'page_js/data_pages/project_search_ids_driven_pages/common/psmPeptideCutoffsCommon.js';

import { webserviceDataParamsDistributionCommonCode } from 'page_js/data_pages/project_search_ids_driven_pages/common/webserviceDataParamsDistribution.js';



///////
$(document).ready(function() { 
	setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else
		$("#crosslink-table").tablesorter(); // gets exception if there are no data rows
	},10);
} ); // end $(document).ready(function() 

//  Constructor
var ViewSearchPeptidePageCode = function() {
	
	var _query_json_field_Contents = null;
	var _query_json_field_String = null;

	//////////////
	//  function called after all HTML above main table is generated, called from inline script on page
	this.createPartsAboveMainTable = function() {
		var objectThis = this;
		setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else
			try {
				objectThis.get_query_json_field_ContentsFromHiddenField();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		},10);
	};

	//////////////
	this.getQueryJSONString = function() {
		return _query_json_field_String;
	};

	//////////////
	this.get_query_json_field_ContentsFromHiddenField = function() {
		var query_json_field_outside_form_id = "query_json_field_outside_form";
		var $query_json_field =  $( "#" + query_json_field_outside_form_id );
		if ( $query_json_field.length === 0 ) {
			throw Error( "No HTML field with id '" + query_json_field_outside_form_id + "'" );
		}
		_query_json_field_String = $query_json_field.val();
		try {
			_query_json_field_Contents = JSON.parse( _query_json_field_String );
		} catch( e ) {
			throw Error( "Failed to parse JSON from HTML field with id 'query_json_field'.  JSON String: " + _query_json_field_String );
		}
		
		cutoffProcessingCommonCode.putCutoffsOnThePage(  { cutoffs : _query_json_field_Contents.cutoffs } );
		
		//  Pass cutoffs and ann type display to all JS that call web services to get data (IE PSMs)
		webserviceDataParamsDistributionCommonCode.paramsForDistribution( _query_json_field_Contents  )

		//  Mark check boxes for chosen link types
		var linkTypes = _query_json_field_Contents.linkTypes;
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
		//  Mark check boxes for chosen dynamic mod masses
		var dynamicModMasses = _query_json_field_Contents.mods;
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
		
		//  Set Minimum PSM count on page
		if ( window.minimumPSM_Count_Filter ) {
			minimumPSM_Count_Filter.saveMinPSMsFilter( { minPSMs : _query_json_field_Contents.minPSMs } );
		}

		//  Mark check box for chosen links to exclude: "remove non-unique PSMs"
		if ( _query_json_field_Contents.removeNonUniquePSMs ) {
			$("#removeNonUniquePSMs").prop('checked', true);
		} else {
			$("#removeNonUniquePSMs").prop('checked', false);
		}

		//  Mark check box for chosen links to exclude: "remove Intra Protein Links"
		if ( _query_json_field_Contents.removeIntraProteinLinks ) {
			$("#removeIntraProteinLinks").prop('checked', true);
		} else {
			$("#removeIntraProteinLinks").prop('checked', false);
		}
		
		

	};
	
	/////////////
	this.put_query_json_field_ContentsToHiddenField = function() {
		var $query_json_field = $("#query_json_field");
		if ( $query_json_field.length === 0 ) {
			throw Error( "No HTML field with id 'query_json_field'" );
		}
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
//				} else {
//				allLinkTypesChosen = false;
			}
		});
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
		
		//  Get Minimum PSM count 
		var minPSMs = minimumPSM_Count_Filter.getMinPSMsFilter();
		
		//  Read check boxes for chosen links to exclude:  "remove non-unique PSMs"
		var removeNonUniquePSMs = false;
		if ( $("#removeNonUniquePSMs").prop('checked') === true ) {
			removeNonUniquePSMs = true;
		}

		//  Read check boxes for chosen links to exclude:  "remove Intra Protein Links"
		var removeIntraProteinLinks = false;
		if ( $("#removeIntraProteinLinks").prop('checked') === true ) {
			removeIntraProteinLinks = true;
		}

		var output_query_json_field_Contents = { 
				cutoffs : outputCutoffs, 
				annTypeIdDisplay : annotationTypeDisplayByProjectSearchId,
				linkTypes : outputLinkTypes, 
				mods : outputDynamicModMasses,
				minPSMs : minPSMs,
				removeNonUniquePSMs : removeNonUniquePSMs,
				removeIntraProteinLinks : removeIntraProteinLinks
		};
		try {
			var output_query_json_field_String = JSON.stringify( output_query_json_field_Contents );
			$query_json_field.val( output_query_json_field_String );
		} catch( e ) {
			throw Error( "Failed to stringify JSON to HTML field with id 'query_json_field'." );
		}
	};
	
	///////////////////////
	//   Called by "onclick" on HTML element
	this.updatePageForFormParams = function() {
		try {
			var put_query_json_field_ContentsToHiddenFieldResult =
				this.put_query_json_field_ContentsToHiddenField();
			if ( put_query_json_field_ContentsToHiddenFieldResult 
					&& put_query_json_field_ContentsToHiddenFieldResult.output_FieldDataFailedValidation ) {
				//  Only submit if there were no errors in the input data
				return;
			}
			$('#form_get_for_updated_parameters_single_search').submit();
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
};

// Instance of class
window.viewSearchPeptidePageCode = new ViewSearchPeptidePageCode();

// Copy to standard page level JS Code Object
window.standardFullPageCode = window.viewSearchPeptidePageCode;
