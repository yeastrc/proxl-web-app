
//  viewMergedProteinCoverageReport.js

//  Javascript for the viewMergedProteinCoverageReport.jsp page



//JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";


$(document).ready(function() 
		{ 


	setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else

		$("#crosslink-table").tablesorter(); // gets exception if there are no data rows
	},10);
		} 
); // end $(document).ready(function() 


//Constructor

var ViewProteinCoverageReportPageCode = function() {

	var _query_json_field_Contents = null;


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




	this.passCutoffsToPSMWebserviceJS = function( psmPeptideCutoffsRootObject ) {

		//  Not needed on this page

//		viewPsmsLoadedFromWebServiceTemplate.setPsmPeptideCriteria( psmPeptideCutoffsRootObject );

//		viewPeptidesRelatedToPSMsByScanId.setPsmPeptideCriteria( psmPeptideCutoffsRootObject );
	};


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

//		private boolean filterNonUniquePeptides;
//		private boolean filterOnlyOnePSM;
//		private boolean filterOnlyOnePeptide;

//		private int[] excludeTaxonomy;
//		private int[] excludeProtein;


		cutoffProcessingCommonCode.putCutoffsOnThePage( { cutoffs : _query_json_field_Contents.cutoffs } );


		this.passCutoffsToPSMWebserviceJS( _query_json_field_Contents.cutoffs );



		//  Mark check boxes for chosen links to exclude:  "no unique peptides", "only one PSM", "only one peptide"

		if ( _query_json_field_Contents.filterNonUniquePeptides ) {

			$("#filterNonUniquePeptides").prop('checked', true);
		}

		if ( _query_json_field_Contents.filterOnlyOnePSM ) {

			$("#filterOnlyOnePSM").prop('checked', true);
		}
		if ( _query_json_field_Contents.filterOnlyOnePeptide ) {

			$("#filterOnlyOnePeptide").prop('checked', true);
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

		var excludeProtein = _query_json_field_Contents.excludeProtein;

		$("#excludeProtein").val( excludeProtein );

	};



	/////////////

	//  Read the user input parameters on the page and update the JSON in the hidden field to send to the server

	this.put_query_json_field_ContentsToHiddenField = function() {

		try {

			var $query_json_field = $("#query_json_field");

			if ( $query_json_field.length === 0 ) {

				throw Error( "No HTML field with id 'query_json_field'" );
			}

//			var inputCutoffs = _query_json_field_Contents.cutoffs;


			var getCutoffsFromThePageResult = cutoffProcessingCommonCode.getCutoffsFromThePage(  {  } );

			var getCutoffsFromThePageResult_FieldDataFailedValidation = getCutoffsFromThePageResult.getCutoffsFromThePageResult_FieldDataFailedValidation;

			if ( getCutoffsFromThePageResult_FieldDataFailedValidation ) {

				//  Cutoffs failed validation and error message was displayed

				//  EARLY EXIT from function

				return { output_FieldDataFailedValidation : getCutoffsFromThePageResult_FieldDataFailedValidation };
			}

			var outputCutoffs = getCutoffsFromThePageResult.cutoffsByProjectSearchId;



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

						throw Error( "excludeTaxonomy cannot be parsed to int.  value: " + excludeTaxonomyFieldValue );
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

						throw Error( "outputExcludeProtein cannot be parsed to int.  value: " + outputExcludeProteinString );
					}

					outputExcludeProteinsAsInts.push( outputExcludeProteinInt );
				}
			}

			var output_query_json_field_Contents = { 

					cutoffs : outputCutoffs, 

					filterNonUniquePeptides : filterNonUniquePeptides,
					filterOnlyOnePSM : filterOnlyOnePSM,
					filterOnlyOnePeptide : filterOnlyOnePeptide,


					excludeTaxonomy : outputExcludeTaxonomy,  
					excludeProtein : outputExcludeProteinsAsInts
			};

			//  Create the JSON of the page parameters and store it on the page

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

			var $form_get_for_updated_parameters__id_to_use = $("#form_get_for_updated_parameters__id_to_use");
			var form_get_for_updated_parameters__id_to_use = $form_get_for_updated_parameters__id_to_use.text();
			var formToSubmitSelector = "#" + form_get_for_updated_parameters__id_to_use;
			var $formToSubmit = $( formToSubmitSelector )
			
			$formToSubmit.submit();


		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
	
};

//Instance of class

var viewProteinCoverageReportPageCode = new ViewProteinCoverageReportPageCode();



//  Copy to standard page level JS Code Object

var standardFullPageCode = viewProteinCoverageReportPageCode;



