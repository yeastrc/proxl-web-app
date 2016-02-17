
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


$(document).ready(function() 
		{ 


	setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else

		$("#main_page_data_table").tablesorter(); // gets exception if there are no data rows
	},10);
		} 
); // end $(document).ready(function() 


/////////////////////////

//Constructor

var ViewSearchProteinPageCommonCrosslinkLooplinkCoverage = function() {




	///////////////

	//  function called after all HTML above main table is generated

	this.createPartsAboveMainTableSearchProteinPageCommon = function( params ) {

//		var listOfObjectsToPassPsmPeptideCutoffsRootTo = params.listOfObjectsToPassPsmPeptideCutoffsRootTo;
		
		//  listOfObjectsToPassPsmPeptideCutoffsRootTo: an array of functions to call 
		//                                                the function setPsmPeptideCriteria on 
		//												  passing the parameter: psmPeptideCutoffsRootObject
		
		var objectThis = this;

		setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else

			objectThis.updateUserInputFieldsWithDataIn_query_json_field_ContentsInHiddenField( params );

//			createImageViewerLink();

//			createStructureViewerLink();


		},10);



		setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else

//			initNagUser();
		},10);

		setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else

//			initDefaultPageView() ;
		},10);


	};


	/////////////////

	this.updateUserInputFieldsWithDataIn_query_json_field_ContentsInHiddenField = function( params) {

		var listOfObjectsToPassPsmPeptideCutoffsRootTo = params.listOfObjectsToPassPsmPeptideCutoffsRootTo;
		
		//  listOfObjectsToPassPsmPeptideCutoffsRootTo: an array of functions to call 
		//                                                the function setPsmPeptideCriteria on 
		//												  passing the parameter: psmPeptideCutoffsRootObject
		
		var query_json_field_Contents = null;


		var $query_json_field =  $("#query_json_field_outside_form");

		if ( $query_json_field.length === 0 ) {

			throw "No HTML field with id 'query_json_field'";
		}

		var query_json_field_String = $query_json_field.val();

		try {
			query_json_field_Contents = JSON.parse( query_json_field_String );

		} catch( e ) {

			throw "Failed to parse JSON from HTML field with id 'query_json_field'.  JSON String: " + query_json_field_String;

		}

//		private boolean filterNonUniquePeptides;
//		private boolean filterOnlyOnePSM;
//		private boolean filterOnlyOnePeptide;

//		private int[] excludeTaxonomy;
//		private int[] excludeProtein;


		cutoffProcessingCommonCode.putCutoffsOnThePage( { cutoffs : query_json_field_Contents.cutoffs } );


		this.passCutoffsToGetDataFromWebservicesJS( 
				{ psmPeptideCutoffsRootObject : query_json_field_Contents.cutoffs,
					listOfObjectsToPassPsmPeptideCutoffsRootTo : listOfObjectsToPassPsmPeptideCutoffsRootTo
				} );



		//  Mark check boxes for chosen links to exclude:  "no unique peptides", "only one PSM", "only one peptide"

		if ( query_json_field_Contents.filterNonUniquePeptides ) {

			$("#filterNonUniquePeptides").prop('checked', true);
		}

		if ( query_json_field_Contents.filterOnlyOnePSM ) {

			$("#filterOnlyOnePSM").prop('checked', true);
		}
		if ( query_json_field_Contents.filterOnlyOnePeptide ) {

			$("#filterOnlyOnePeptide").prop('checked', true);
		}


		//  Mark check boxes for chosen taxonomy to exclude

		var excludeTaxonomy = query_json_field_Contents.excludeTaxonomy;

		if ( excludeTaxonomy !== undefined && excludeTaxonomy !== null ) {

			//  excludeTaxonomy not null so process it, empty array means nothing chosen

			if ( excludeTaxonomy.length > 0 ) {

				var $excludeTaxonomy_jq = $(".excludeTaxonomy_jq");

				$excludeTaxonomy_jq.each( function( index, element ) {

					var $item = $( this );

					var excludeTaxonomyFieldValue = $item.val();

					//  if excludeTaxonomyFieldValue found in excludeTaxonomy array, set it to checked

					for ( var excludeTaxonomyIndex = 0; excludeTaxonomyIndex < excludeTaxonomy.length; excludeTaxonomyIndex++ ) {

						var excludeTaxonomyEntry = excludeTaxonomy[ excludeTaxonomyIndex ];

						var excludeTaxonomyEntryString = excludeTaxonomyEntry.toString();

						//  Compare as string since that is what will be retrieved from HTML element

						if ( excludeTaxonomyEntryString === excludeTaxonomyFieldValue ) {

							$item.prop('checked', true);
						}
					}
				});
			}

		}


		//  Mark Multi <select> for chosen Proteins to exclude

		var excludeProtein = query_json_field_Contents.excludeProtein;

		$("#excludeProtein").val( excludeProtein );

	};



	////////////////

	this.passCutoffsToGetDataFromWebservicesJS = function( params ) {
		
		var psmPeptideCutoffsRootObject = params.psmPeptideCutoffsRootObject;

		var listOfObjectsToPassPsmPeptideCutoffsRootTo = params.listOfObjectsToPassPsmPeptideCutoffsRootTo;

		if ( listOfObjectsToPassPsmPeptideCutoffsRootTo ) {
			
			for ( var index = 0; index < listOfObjectsToPassPsmPeptideCutoffsRootTo.length; index++ ) {
				
				var objectsToPassPsmPeptideCutoffsRootTo = listOfObjectsToPassPsmPeptideCutoffsRootTo[ index ];
				
				objectsToPassPsmPeptideCutoffsRootTo.setPsmPeptideCriteria( psmPeptideCutoffsRootObject );
			}
			
		}
	};
	

	/////////////

	//  Read the user input parameters on the page and update the JSON in the hidden field to send to the server

	this.put_query_json_field_ContentsToHiddenField = function() {

		var $query_json_field = $("#query_json_field");

		if ( $query_json_field.length === 0 ) {

			throw "No HTML field with id 'query_json_field'";
		}

//		var inputCutoffs = _query_json_field_Contents.cutoffs;


		var getCutoffsFromThePageResult = cutoffProcessingCommonCode.getCutoffsFromThePage(  {  } );

		var getCutoffsFromThePageResult_FieldDataFailedValidation = getCutoffsFromThePageResult.getCutoffsFromThePageResult_FieldDataFailedValidation;

		if ( getCutoffsFromThePageResult_FieldDataFailedValidation ) {

			//  Cutoffs failed validation and error message was displayed

			//  EARLY EXIT from function

			return { output_FieldDataFailedValidation : getCutoffsFromThePageResult_FieldDataFailedValidation };
		}

		var outputCutoffs = getCutoffsFromThePageResult.cutoffsBySearchId;




		//  Mark check boxes for chosen links to exclude:  "no unique peptides", "only one PSM", "only one peptide"

		var filterNonUniquePeptides = false;
		var filterOnlyOnePSM = false;
		var filterOnlyOnePeptide = false;


		if ( $("#filterNonUniquePeptides").prop('checked') === true ) {

			filterNonUniquePeptides = true;
		}
		if ( $("#filterOnlyOnePSM").prop('checked') === true ) {

			filterOnlyOnePSM = true;
		}
		if ( $("#filterOnlyOnePeptide").prop('checked') === true ) {

			filterOnlyOnePeptide = true;
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

					throw "excludeTaxonomy cannot be parsed to int.  value: " + excludeTaxonomyFieldValue;
				}

				outputExcludeTaxonomy.push( excludeTaxonomyFieldValueInt );
			}
		});

		//  Create array of values from Multi <select> for chosen Proteins to exclude

		var outputExcludeProteinsAsStrings = $("#excludeProtein").val( );

		var outputExcludeProteinsAsInts = null;

		if ( outputExcludeProteinsAsStrings !== undefined && outputExcludeProteinsAsStrings != null ) {

			outputExcludeProteinsAsInts = [];

			for ( var outputExcludeProteinsAsStringsIndex = 0; outputExcludeProteinsAsStringsIndex < outputExcludeProteinsAsStrings.length; outputExcludeProteinsAsStringsIndex++ ) {

				var outputExcludeProteinString = outputExcludeProteinsAsStrings[ outputExcludeProteinsAsStringsIndex ];

				var outputExcludeProteinInt = parseInt( outputExcludeProteinString, 10 );

				if ( isNaN( outputExcludeProteinInt ) ) {

					throw "outputExcludeProtein cannot be parsed to int.  value: " + outputExcludeProteinString;
				}

				outputExcludeProteinsAsInts.push( outputExcludeProteinInt );
			}
		}
		

		//  Not currently used.  An experiment
		
//		var getEncodedExcludeProteinsResult = this.getEncodedExcludeProteins( outputExcludeProteinsAsInts );

		var output_query_json_field_Contents = { 

				cutoffs : outputCutoffs, 

				filterNonUniquePeptides : filterNonUniquePeptides,
				filterOnlyOnePSM : filterOnlyOnePSM,
				filterOnlyOnePeptide : filterOnlyOnePeptide,


				excludeTaxonomy : outputExcludeTaxonomy,  
				excludeProtein : outputExcludeProteinsAsInts
				

				//  Not currently used.  An experiment
//				,
//				
//				exclProtEnc : getEncodedExcludeProteinsResult.encodedExcludeProteins,
//				exclProtEncRadix : getEncodedExcludeProteinsResult.exclProtEncRadix
		};

		//  Create the JSON of the page parameters and store it on the page

		try {
			var output_query_json_field_String = JSON.stringify( output_query_json_field_Contents );

			$query_json_field.val( output_query_json_field_String );

		} catch( e ) {

			throw "Failed to stringify JSON to HTML field with id 'query_json_field'.";

		}
		
	};

	//  Not currently used.  An experiment
	
//	///////////////////////
//
//	this.getEncodedExcludeProteins = function( outputExcludeProteinsAsInts ) {
//
//		
//		var TO_STRING_RADIX = 32;
//		
//		//  TODO  TEMP
//		
//		var encodedExcludeProteins = "";
//
//		if ( outputExcludeProteinsAsInts && outputExcludeProteinsAsInts.length > 0 ) {
//			
//			if ( outputExcludeProteinsAsInts.length === 1 ) {
//			
//				encodedExcludeProteins = outputExcludeProteinsAsInts[ 0 ].toString( TO_STRING_RADIX );
//		
//			} else {
//
//				//  sort numerically
//
//				outputExcludeProteinsAsInts.sort(function(a, b) {
//					return a - b;
//				});
//
//				var firstValue = outputExcludeProteinsAsInts[ 0 ].toString( TO_STRING_RADIX ); 
//				
//				var outputAsOffsets = [ firstValue ];
//
//				//  start at second entry in array
//				
//				for ( var index = 1; index < outputExcludeProteinsAsInts.length; index++ ) {
//					
//					var offsetFromPrevValue = ( outputExcludeProteinsAsInts[ index ] - outputExcludeProteinsAsInts[ index - 1 ] ).toString( TO_STRING_RADIX );
//					
//					outputAsOffsets.push( offsetFromPrevValue );
//				}
//
//				encodedExcludeProteins = outputAsOffsets.join( ',' );  // comma separate
//			}
//		}
//		
//		return { encodedExcludeProteins : encodedExcludeProteins, exclProtEncRadix : TO_STRING_RADIX };
//	};


	///////////////////////

//	this.encodeExcludeProteinAndPlaceInFormField = function( outputExcludeProteinsAsInts ) {
//	
//		ss ss
//		
//		exclude_protein_ids_encoded
//	
//	
//	};

	///////////////////////

	this.updatePageForFormParams = function() {


		var put_query_json_field_ContentsToHiddenFieldResult =
			this.put_query_json_field_ContentsToHiddenField();

		if ( put_query_json_field_ContentsToHiddenFieldResult 
				&& put_query_json_field_ContentsToHiddenFieldResult.output_FieldDataFailedValidation ) {

			//  Only submit if there were no errors in the input data

			return;
		}

		$('#form_get_for_updated_parameters').submit();

	};

};

//Instance of class

var viewSearchProteinPageCommonCrosslinkLooplinkCoverage = new ViewSearchProteinPageCommonCrosslinkLooplinkCoverage();




