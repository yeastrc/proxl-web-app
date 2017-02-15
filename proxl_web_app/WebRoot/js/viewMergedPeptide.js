
//viewMergedPeptide.js

//  Javascript for the viewMergedPeptide.jsp page

//  JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

$(document).ready(function() { 
	setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else
		$("#crosslink-table").tablesorter(); // gets exception if there are no data rows
	},10);
} ); // end $(document).ready(function()

//  Constructor

var ViewMergedPeptidePageCode = function() {
	
	var _query_json_field_Contents = null;
	
	///////
	//  function called after all HTML above main table is generated, called from inline script on page
	this.createPartsAboveMainTable = function() {
		var objectThis = this;
		setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else
			try {
				objectThis.get_query_json_field_ContentsFromHiddenField();
				window.createVennDiagramIfNeeded();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		},10);
	};
	
	///////
	this.get_query_json_field_ContentsFromHiddenField = function() {
		var $query_json_field =  $("#query_json_field_outside_form");
		if ( $query_json_field.length === 0 ) {
			throw Error( "No HTML field with id 'query_json_field'" );
		}
		var query_json_field_String = $query_json_field.val();
		try {
			_query_json_field_Contents = JSON.parse( query_json_field_String );
		} catch( e ) {
			throw Error( "Failed to parse JSON from HTML field with id 'query_json_field'.  JSON String: " + query_json_field_String );
		}
//		_query_json_field_Contents: Object
//		cutoffs: Object
//		searches: Object
//		128: Object			
//		peptideCutoffValues: Object
//		238: Object
//		id: 238
//		value: "0.01"
//		psmCutoffValues: Object
//		384: Object
//		id: 384
//		value: "0.01"
//		searchId: 128
//		linkTypes: null
//		mods: null
		
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
					//  if linkTypeFieldValue found in linkTypes array, set it to checked
					for ( var linkTypesIndex = 0; linkTypesIndex < linkTypes.length; linkTypesIndex++ ) {
						var linkTypesEntry = linkTypes[ linkTypesIndex ];
						if ( linkTypesEntry === linkTypeFieldValue ) {
							$item.prop('checked', true);
						}
					}
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
					//  if linkTypeFieldValue found in dynamicModMasses array, set it to checked
					for ( var dynamicModMassesIndex = 0; dynamicModMassesIndex < dynamicModMasses.length; dynamicModMassesIndex++ ) {
						var dynamicModMassesEntry = dynamicModMasses[ dynamicModMassesIndex ];
						if ( dynamicModMassesEntry === linkTypeFieldValue ) {
							$item.prop('checked', true);
						}
					}
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
	
	///////
	this.put_query_json_field_ContentsToHiddenField = function() {
		try {
			var $query_json_field = $("#query_json_field");
			if ( $query_json_field.length === 0 ) {
				throw Error( "No HTML field with id 'query_json_field'" );
			}

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
			var output_query_json_field_Contents = { 
					cutoffs : outputCutoffs, 
					annTypeIdDisplay : annotationTypeDisplayByProjectSearchId,
					linkTypes : outputLinkTypes, 
					mods : outputDynamicModMasses 
			};
			try {
				var output_query_json_field_String = JSON.stringify( output_query_json_field_Contents );
				$query_json_field.val( output_query_json_field_String );
			} catch( e ) {
				throw Error( "Failed to stringify JSON to HTML field with id 'query_json_field'." );
			}
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
	
	///////
	this.updatePageForFormParams = function() {
		try {	
			var put_query_json_field_ContentsToHiddenFieldResult =
				this.put_query_json_field_ContentsToHiddenField();
			if ( put_query_json_field_ContentsToHiddenFieldResult 
					&& put_query_json_field_ContentsToHiddenFieldResult.output_FieldDataFailedValidation ) {
				//  Only submit if there were no errors in the input data
				return;
			}
			$('#form_get_for_updated_parameters').submit();
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
};

//  Instance of class
var viewMergedPeptidePageCode = new ViewMergedPeptidePageCode();

//  Copy to standard page level JS Code Object
var standardFullPageCode = viewMergedPeptidePageCode;
