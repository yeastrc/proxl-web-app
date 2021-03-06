
//	viewProteinPageCommonCrosslinkLooplinkCoverageSearchMerged.js

//   Single Search  and Merged 
//  Javascript for use on the pages:
//   Single search
//		viewSearchCrosslinkCrosslinkProtein.jsp 
//		viewSearchLooplinkLooplinkProtein.jsp
//		viewProteinCoverageReport.jsp
//	Merged Searches
//		viewMergedCrosslinkProtein.jsp
//		viewMergedLooplinkProtein.jsp
//		viewMergedProteinCoverageReport.jsp

//JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

//  Import to make available on the page
import { searchesChangeDisplayOrder } from 'page_js/data_pages/project_search_ids_driven_pages/common/searchesChangeDisplayOrder.js';
import { searchesForPageChooser } from 'page_js/data_pages/project_search_ids_driven_pages/common/searchesForPageChooser.js';
import { sharePageURLShortener  } from 'page_js/data_pages/project_search_ids_driven_pages/common/sharePageURLShortener.js';
import { addSingleTooltipForProteinName } from 'page_js/data_pages/project_search_ids_driven_pages/common/createTooltipForProteinNames.js';

import { DataPages_LoggedInUser_CommonObjectsFactory } from 'page_js/data_pages/data_pages_common/dataPages_LoggedInUser_CommonObjectsFactory.js';




import { minimumPSM_Count_Filter } from 'page_js/data_pages/project_search_ids_driven_pages/common/minimumPSM_Count_Filter.js';
import { annotationDataDisplayProcessingCommonCode } from 'page_js/data_pages/project_search_ids_driven_pages/common/psmPeptideAnnDisplayDataCommon.js';
import { cutoffProcessingCommonCode } from 'page_js/data_pages/project_search_ids_driven_pages/common/psmPeptideCutoffsCommon.js';

import { webserviceDataParamsDistributionCommonCode } from 'page_js/data_pages/project_search_ids_driven_pages/common/webserviceDataParamsDistribution.js';



var onDocumentReady = function() {

	// setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else
	// 	$("#main_page_data_table").tablesorter(); // gets exception if there are no data rows
	// },10);


}

/////////////////////////
//  Constructor

var ViewSearchProteinPageCommonCrosslinkLooplinkCoverage = function() {


	const dataPages_LoggedInUser_CommonObjectsFactory = new DataPages_LoggedInUser_CommonObjectsFactory();

	const saveView_dataPages = dataPages_LoggedInUser_CommonObjectsFactory.instantiate_SaveView_dataPages();


	var _query_json_field_Contents = null;
	var _query_json_field_String = null;
	
	///////////////
	//  function called after all HTML above main table is generated
	this.createPartsAboveMainTableSearchProteinPageCommon = function( params ) {
		var objectThis = this;
		// setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else
			try {
				objectThis.updateUserInputFieldsWithDataIn_query_json_field_ContentsInHiddenField( params );
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		// },10);
		// setTimeout( () => { // put in setTimeout so if it fails it doesn't kill anything else
			try {
				this.initialize_saveView_dataPages();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		// },11);
	};
	
	//////////////
	this.initialize_saveView_dataPages = function() {

		saveView_dataPages.initialize({ /* projectSearchIds, container_DOM_Element, enableSetDefault */ });
	}


	this.getQueryJSONString = function() {
		return _query_json_field_String;
	};


	this.getQueryJSONObject = function() {

		if ( _query_json_field_Contents ) {
			return _query_json_field_Contents;
		}

		var $query_json_field =  $("#query_json_field_outside_form");
		if ( $query_json_field.length === 0 ) {
			throw Error( "No HTML field with id 'query_json_field'" );
		}
		
		_query_json_field_String = $query_json_field.val();
		try {
			_query_json_field_Contents = JSON.parse( _query_json_field_String );
		} catch( e ) {
			throw Error( "Failed to parse JSON from HTML field with id 'query_json_field'.  JSON String: " + _query_json_field_String );
		}
		return _query_json_field_Contents;
	};
	
	/////////////////
	this.updateUserInputFieldsWithDataIn_query_json_field_ContentsInHiddenField = function( params) {
		
		this.getQueryJSONObject(); //  Populate createPartsAboveMainTableSearchProteinPageCommon if not populated
		
		if ( window.cutoffProcessingCommonCode ) {
			cutoffProcessingCommonCode.putCutoffsOnThePage( { cutoffs : _query_json_field_Contents.cutoffs } );
		} else {
			//  webserviceDataParamsDistributionCommonCode not on page yet so delay the call
			$(document).ready(function() { 
				setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else
					try {
						cutoffProcessingCommonCode.putCutoffsOnThePage( { cutoffs : _query_json_field_Contents.cutoffs } );
					} catch( e ) {
						reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
						throw e;
					}
				},10);
			}); // end $(document).ready(function()
		}
		
		//  Pass cutoffs and ann type display to all JS that call web services to get data (IE PSMs)
		if ( window.webserviceDataParamsDistributionCommonCode ) {
			webserviceDataParamsDistributionCommonCode.paramsForDistribution( _query_json_field_Contents  );
		} else {
			//  webserviceDataParamsDistributionCommonCode not on page yet so delay the call
			$(document).ready(function() { 
				setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else
					try {
						webserviceDataParamsDistributionCommonCode.paramsForDistribution( _query_json_field_Contents  );
					} catch( e ) {
						reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
						throw e;
					}
				},10);
			}); // end $(document).ready(function()
		}

		//  Process chosen link types
		var linkTypes = _query_json_field_Contents.linkTypes;
		
		//  Pass chosen link types to viewReportedPeptidesForProteinAllLoadedFromWebServiceTemplate if on the page
		if ( window.viewReportedPeptidesForProteinAllLoadedFromWebServiceTemplate ) {
			viewReportedPeptidesForProteinAllLoadedFromWebServiceTemplate.setChosenLinkTypes( linkTypes );
		}
		
		if ( window.viewProteinSingleForMergedProteinAllPageLoadedFromWebServiceTemplate ) {
			viewProteinSingleForMergedProteinAllPageLoadedFromWebServiceTemplate.setChosenLinkTypes( linkTypes );
		}
		
		//  Mark check boxes for chosen link types
		if ( linkTypes !== undefined && linkTypes !== null ) {
			//  linkTypes not null so process it, empty array means nothing chosen
			if ( linkTypes.length > 0 ) {
				var $link_type_jq = $(".link_type_jq");
				$link_type_jq.each( function( index, element ) {
					var $item = $( this );
					var linkTypeFieldValue = $item.val();
					//  if linkTypeFieldValue found in linkTypes array, set it to checked, else set it to not checked
					var linkFieldCheckedPropertyValue = false;
					for ( var linkTypesIndex = 0; linkTypesIndex < linkTypes.length; linkTypesIndex++ ) {
						var linkTypesEntry = linkTypes[ linkTypesIndex ];
						if ( linkTypesEntry === linkTypeFieldValue ) {
							linkFieldCheckedPropertyValue = true;
						}
					}
					$item.prop('checked', linkFieldCheckedPropertyValue);
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
		
		//  Set Minimum PSM count on page
		if ( window.minimumPSM_Count_Filter ) {
			minimumPSM_Count_Filter.saveMinPSMsFilter( { minPSMs : _query_json_field_Contents.minPSMs } );
		}
		
		//  Mark check boxes for chosen links to exclude:  "no unique peptides", "only one PSM", "only one peptide", "remove non-unique PSMs"
		if ( _query_json_field_Contents.filterNonUniquePeptides ) {
			$("#filterNonUniquePeptides").prop('checked', true);
		} else {
			$("#filterNonUniquePeptides").prop('checked', false);
		}
		
		if ( _query_json_field_Contents.filterOnlyOnePeptide ) {
			$("#filterOnlyOnePeptide").prop('checked', true);
		} else {
			$("#filterOnlyOnePeptide").prop('checked', false);
		}
		if ( _query_json_field_Contents.removeNonUniquePSMs ) {
			$("#removeNonUniquePSMs").prop('checked', true);
		} else {
			$("#removeNonUniquePSMs").prop('checked', false);
		}
		
		//  Mark check boxes for chosen taxonomy to exclude
		var excludeTaxonomy = _query_json_field_Contents.excludeTaxonomy;
		if ( excludeTaxonomy !== undefined && excludeTaxonomy !== null ) {
			//  excludeTaxonomy not null so process it, empty array means nothing chosen
			if ( excludeTaxonomy.length > 0 ) {
				var $excludeTaxonomy_jq = $(".excludeTaxonomy_jq");
				$excludeTaxonomy_jq.each( function( index, element ) {
					var $item = $( this );
					var excludeTaxonomyFieldValue = $item.val();
					//  if excludeTaxonomyFieldValue found in excludeTaxonomy array, set it to checked, else set it to not checked
					var checkedPropertyValue = false;
					for ( var excludeTaxonomyIndex = 0; excludeTaxonomyIndex < excludeTaxonomy.length; excludeTaxonomyIndex++ ) {
						var excludeTaxonomyEntry = excludeTaxonomy[ excludeTaxonomyIndex ];
						var excludeTaxonomyEntryString = excludeTaxonomyEntry.toString();
						//  Compare as string since that is what will be retrieved from HTML element
						if ( excludeTaxonomyEntryString === excludeTaxonomyFieldValue ) {
							checkedPropertyValue = true;
						}
					}
					$item.prop('checked', checkedPropertyValue);
				});
			}
		}
		
		//  Mark Multi <select> for chosen Proteins to exclude
		//   (_query_json_field_Contents.excludeproteinSequenceVersionIds populated in Struts Action since may have convert from old NRSEQ Protein Id)
		var excludeProtein = _query_json_field_Contents.excludeproteinSequenceVersionIds;
		$("#excludeProtein").val( excludeProtein );
	};
	
	/////////////
	//  Read the user input parameters on the page and update the JSON in the hidden field to send to the server
	this.put_query_json_field_ContentsToHiddenField = function() {
		

		var $query_json_field = $("#query_json_field");
		var $query_json_field_jq = $(".query_json_field_jq"); // Used on Protein Coverage page
		if ( $query_json_field.length === 0 && $query_json_field_jq.length === 0 ) {
			throw Error( "No HTML field with id 'query_json_field' or class 'query_json_field_jq'" );
		}
//		var inputCutoffs = _query_json_field_Contents.cutoffs;
		var getCutoffsFromThePageResult = cutoffProcessingCommonCode.getCutoffsFromThePage(  {  } );
		var getCutoffsFromThePageResult_FieldDataFailedValidation = getCutoffsFromThePageResult.getCutoffsFromThePageResult_FieldDataFailedValidation;
		if ( getCutoffsFromThePageResult_FieldDataFailedValidation ) {
			//  Cutoffs failed validation and error message was displayed
			//  EARLY EXIT from function
			return { output_FieldDataFailedValidation : getCutoffsFromThePageResult_FieldDataFailedValidation };
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

		//  Get Minimum PSM count 
		var minPSMs = minimumPSM_Count_Filter.getMinPSMsFilter();
		
		//  Read check boxes for chosen links to exclude:  "no unique peptides", "only one peptide", "remove non-unique PSMs"
		var filterNonUniquePeptides = false;
		var filterOnlyOnePeptide = false;
		var removeNonUniquePSMs = false;
		if ( $("#filterNonUniquePeptides").prop('checked') === true ) {
			filterNonUniquePeptides = true;
		}
		if ( $("#filterOnlyOnePeptide").prop('checked') === true ) {
			filterOnlyOnePeptide = true;
		}
		if ( $("#removeNonUniquePSMs").prop('checked') === true ) {
			removeNonUniquePSMs = true;
		}
		//  Create array from check boxes for chosen Taxonomy to exclude
		var outputExcludeTaxonomy= [];
		var $excludeTaxonomy_jq = $(".excludeTaxonomy_jq");
		$excludeTaxonomy_jq.each( function( index, element ) {
			var $item = $( this );
			if ( $item.prop('checked') === true ) {
				var excludeTaxonomyFieldValue = $item.val();
				var excludeTaxonomyFieldValueInt = parseInt( excludeTaxonomyFieldValue, 10 );
				if ( isNaN( excludeTaxonomyFieldValueInt ) ) {
					throw Error( "excludeTaxonomy cannot be parsed to int.  value: " + excludeTaxonomyFieldValue );
				}
				outputExcludeTaxonomy.push( excludeTaxonomyFieldValueInt );
			}
		});
		//  Create array of values from Multi <select> for chosen Proteins to exclude
		var outputExcludeProteinsAsStrings = $("#excludeProtein").val( );
		var getEncodedExcludeProteinsResult = this.getEncodedExcludeProteins( outputExcludeProteinsAsStrings );
		var output_query_json_field_Contents = { 
				cutoffs : outputCutoffs, 
				annTypeIdDisplay : annotationTypeDisplayByProjectSearchId,
				linkTypes : outputLinkTypes, 
				minPSMs : minPSMs,
				filterNonUniquePeptides : filterNonUniquePeptides,
				filterOnlyOnePeptide : filterOnlyOnePeptide,
				removeNonUniquePSMs : removeNonUniquePSMs,
				excludeTaxonomy : outputExcludeTaxonomy,  
//				excludeproteinSequenceVersionIds : outputExcludeProteinsAsInts
				exclproteinSequenceVersionIdsEncoded : getEncodedExcludeProteinsResult.encodedExcludeProteins,
				exclproteinSequenceVersionIdsEncodedRadix : getEncodedExcludeProteinsResult.exclProtEncRadix,
				exclproteinSequenceVersionIdsEncodedSeparator : getEncodedExcludeProteinsResult.exclProtEncSeparator
		};
		//  Create the JSON of the page parameters and store it on the page
		try {
			var output_query_json_field_String = JSON.stringify( output_query_json_field_Contents );
			$query_json_field.val( output_query_json_field_String );
			$query_json_field_jq.val( output_query_json_field_String );
		} catch( e ) {
			throw Error( "Failed to stringify JSON to HTML field with id 'query_json_field' or HTML field with class 'query_json_field_jq'." );
		}
	};
	
//	///////////////////////
	this.getEncodedExcludeProteins = function( outputExcludeProteinsAsStrings ) {
		var TO_STRING_RADIX = 32;
		var SEPARATOR = "Z";
		var encodedExcludeProteins = "";
		var outputExcludeProteinsAsInts = null;
		if ( outputExcludeProteinsAsStrings !== undefined && outputExcludeProteinsAsStrings != null ) {
			outputExcludeProteinsAsInts = [];
			for ( var outputExcludeProteinsAsStringsIndex = 0; outputExcludeProteinsAsStringsIndex < outputExcludeProteinsAsStrings.length; outputExcludeProteinsAsStringsIndex++ ) {
				var outputExcludeProteinString = outputExcludeProteinsAsStrings[ outputExcludeProteinsAsStringsIndex ];
				var outputExcludeProteinInt = parseInt( outputExcludeProteinString, 10 );
				if ( isNaN( outputExcludeProteinInt ) ) {
					throw Error( "outputExcludeProtein cannot be parsed to int.  value: " + outputExcludeProteinString );
				}
				outputExcludeProteinsAsInts.push( outputExcludeProteinInt );
			}
		}
		if ( outputExcludeProteinsAsInts && outputExcludeProteinsAsInts.length > 0 ) {
			if ( outputExcludeProteinsAsInts.length === 1 ) {
				encodedExcludeProteins = outputExcludeProteinsAsInts[ 0 ].toString( TO_STRING_RADIX );
			} else {
				//  sort numerically
				outputExcludeProteinsAsInts.sort(function(a, b) {
					return a - b;
				});
				var firstValue = outputExcludeProteinsAsInts[ 0 ].toString( TO_STRING_RADIX ); 
				var outputAsOffsets = [ firstValue ];
				//  start at second entry in array
				for ( var index = 1; index < outputExcludeProteinsAsInts.length; index++ ) {
					var offsetFromPrevValueInt = outputExcludeProteinsAsInts[ index ] - outputExcludeProteinsAsInts[ index - 1 ];
					var offsetFromPrevValue = ( offsetFromPrevValueInt ).toString( TO_STRING_RADIX );
					outputAsOffsets.push( offsetFromPrevValue );
				}
				encodedExcludeProteins = outputAsOffsets.join( SEPARATOR );  // SEPARATOR separator
			}
		}
		return {
			outputExcludeProteinsAsInts : outputExcludeProteinsAsInts,
			encodedExcludeProteins : encodedExcludeProteins, 
			exclProtEncRadix : TO_STRING_RADIX, 
			exclProtEncSeparator : SEPARATOR };
	};

	///////////////////////
	//   Called from Crosslink and Looplink code
	this.updatePageForFormParams = function() {
		var put_query_json_field_ContentsToHiddenFieldResult =
			this.put_query_json_field_ContentsToHiddenField();
		if ( put_query_json_field_ContentsToHiddenFieldResult 
				&& put_query_json_field_ContentsToHiddenFieldResult.output_FieldDataFailedValidation ) {
			//  Only submit if there were no errors in the input data
			return;
		}

		var form_get_for_updated_parameters__id_to_use = "form_get_for_updated_parameters_multiple_searches";
		
		var $form_get_for_updated_parameters__id_to_use = $("#form_get_for_updated_parameters__id_to_use");
		if ( $form_get_for_updated_parameters__id_to_use.length > 0 ) {
			form_get_for_updated_parameters__id_to_use = $form_get_for_updated_parameters__id_to_use.text();
		}
		var formToSubmitSelector = "#" + form_get_for_updated_parameters__id_to_use;
		var $formToSubmit = $( formToSubmitSelector )
		
		$formToSubmit.submit();
		
	};
};

//  Instance of class
var viewSearchProteinPageCommonCrosslinkLooplinkCoverage = new ViewSearchProteinPageCommonCrosslinkLooplinkCoverage();

window.viewSearchProteinPageCommonCrosslinkLooplinkCoverage = viewSearchProteinPageCommonCrosslinkLooplinkCoverage;

//  Copy to standard page level JS Code Object
window.standardFullPageCode = window.viewSearchProteinPageCommonCrosslinkLooplinkCoverage;

export { onDocumentReady, viewSearchProteinPageCommonCrosslinkLooplinkCoverage }
