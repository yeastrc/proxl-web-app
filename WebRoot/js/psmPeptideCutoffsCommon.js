
//   psmPeptideCutoffsCommon.js


//  Javascript:  Common code for processing the Cutoffs on the page

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";



//Constructor

var CutoffProcessingCommonCode = function() {

	
	var _CUTOFF_VALUE__DATA__ = "cutoff_saved_value";
	
	var _handlebarsTemplate_filter_single_value_display_template = null;
	
			
//			_query_json_field_Contents: Object
//				cutoffs: Object
//					searches: Object
//						128: Object			
//							peptideCutoffValues: Object
//								238: Object
//									id: 238
//									value: "0.01"
//							psmCutoffValues: Object
//								384: Object
//									id: 384
//									value: "0.01"
//							searchId: 128
//				linkTypes: null
//				mods: null
			
			

	//  function called to put cutoffs on the page, update the input fields with the values in the object
	
	//  params is normally { cutoffs : _query_json_field_Contents.cutoffs }

	this.putCutoffsOnThePage = function( params ) {
		
		var objectThis = this;
		

		var cutoffs = params.cutoffs;
		
		var inputCutoffSearches = cutoffs.searches;

		var inputCutoffSearchIdArray = Object.keys( inputCutoffSearches );
		
		for ( var cutoffSearchIdIndex = 0; cutoffSearchIdIndex < inputCutoffSearchIdArray.length; cutoffSearchIdIndex++ ) {

			var inputCutoffSearchId = inputCutoffSearchIdArray[ cutoffSearchIdIndex ];

			var inputCutoffSearchEntry = inputCutoffSearches[ inputCutoffSearchId ];


			//  put PSM cutoff values

			var inputCutoffValuesPsm = inputCutoffSearchEntry.psmCutoffValues;
			
			_putCutoffsOnThePagePerPsmPeptideType( { inputCutoffValues : inputCutoffValuesPsm } );
			

			//  put Peptide cutoff values

			var inputCutoffValuesPeptide = inputCutoffSearchEntry.peptideCutoffValues;
			
			_putCutoffsOnThePagePerPsmPeptideType( { inputCutoffValues : inputCutoffValuesPeptide } );
		}
		
		

		var $cutoff_overlay_enclosing_block_jq__All = $(".cutoff_overlay_enclosing_block_jq");

		$cutoff_overlay_enclosing_block_jq__All.each( function( index, element ) {
			
			var $cutoff_overlay_enclosing_block_jq = $( this ); 
		
			objectThis._storeFieldValuesAndUpdateCutoffDisplay( { $cutoff_overlay_enclosing_block_jq : $cutoff_overlay_enclosing_block_jq } );
		});


	};
	
	///////////////
	
	//   Internal function called by object function  this.putCutoffsOnThePage()
	
	
	var _putCutoffsOnThePagePerPsmPeptideType = function( params ) {
		
		var inputCutoffValues = params.inputCutoffValues;

		var inputCutoffAnnotationIdArray = Object.keys( inputCutoffValues );

		for ( var cutoffAnnotationIdIndex = 0; cutoffAnnotationIdIndex < inputCutoffAnnotationIdArray.length; cutoffAnnotationIdIndex++ ) {
			
			var inputCutoffAnnotationId = inputCutoffAnnotationIdArray[ cutoffAnnotationIdIndex ];
			
			var cutoffAnnotationData = inputCutoffValues[ inputCutoffAnnotationId ];
			
			var cutoffAnnotationDataIdFromObj = cutoffAnnotationData.id;
			
			var inputCutoffValueEntryValue = cutoffAnnotationData.value;

			
			var inputFieldId = "annotation_cutoff_input_field_ann_id_" + cutoffAnnotationDataIdFromObj;
			
			var $inputField = $("#" + inputFieldId);

			$inputField.val( inputCutoffValueEntryValue );
			
			var $annotation_entry_root_tr_jq =  $inputField.closest(".annotation_entry_root_tr_jq");
			
//			$annotation_entry_root_tr_jq.show();
			

			var linkId = "add_annotation_cutoff_link_ann_id_" + cutoffAnnotationDataIdFromObj;
			
			var $link = $("#" + linkId );
					
			var $linkEnclosure = $link.closest(".add_annotation_link_enclosing_div_jq");
			
//			$linkEnclosure.hide();
		}
	};
	
	
	
	
	
	
	
	
	

	//  function called to get cutoffs from the page, from the user input fields
	
	//  Returns cutoffs by search id ("cutoffsBySearchId") and in flat format ("cutoffsByAnnotationId") ( currently all web services expect the by search id format )
	
	//   "cutoffsBySearchId" object is the old "outputCutoffs" standard value
	
	//  Returns object of format
	
//	{
//		cutoffsBySearchId : { 
//			searches : 
//			{
//				"<searchId>" : 
//				{	"searchId" : <searchId>,
//					"psmCutoffValues" :
//						{"<annotationId>" :
//							{ "id" : <annotationId>, "value" : "0.01001" } 
//						},
//					"peptideCutoffValues" :
//						{"<annotationId>":
//						{"id":<annotationId>,"value":"0.01001"} 
//				}
//			}
//		},
//		cutoffsByAnnotationId :
//			[
//			 	{ "id" : <annotationId>, "value" : "0.01001" }, ...
//			]
//		
//	}
	
	//  WAS  params is normally { cutoffs : _query_json_field_Contents.cutoffs }

	this.getCutoffsFromThePage = function( params ) {


		var outputCutoffsBySearchId = { searches : {} };

		var outputCutoffsByAnnotationId = [];
		
		var output_FieldDataFailedValidation = false;

		var getCutoffsPerPsmPeptideTypeParams;
		
		var _getCutoffsPerPsmPeptideTypeResult; 

		
		//  Get annotation data for PSM
		
		getCutoffsPerPsmPeptideTypeParams = {

				psmPeptideCutoffsObjKey : "psmCutoffValues",
				psmPeptideCutoffs_CSS_Class : "psm_annotation_cutoff_input_field_jq",

				outputCutoffsBySearchId : outputCutoffsBySearchId,
				outputCutoffsByAnnotationId : outputCutoffsByAnnotationId
		};
		
		_getCutoffsPerPsmPeptideTypeResult = 
			_getCutoffsPerPsmPeptideType( getCutoffsPerPsmPeptideTypeParams );
		
		if ( _getCutoffsPerPsmPeptideTypeResult.output_FieldDataFailedValidation  ) {
		
			output_FieldDataFailedValidation = true;
		}
			
		
		if ( ! output_FieldDataFailedValidation ) {

			//  Get annotation data for Peptide

			getCutoffsPerPsmPeptideTypeParams = {

					psmPeptideCutoffsObjKey : "peptideCutoffValues",
					psmPeptideCutoffs_CSS_Class : "peptide_annotation_cutoff_input_field_jq",

					outputCutoffsBySearchId : outputCutoffsBySearchId,
					outputCutoffsByAnnotationId : outputCutoffsByAnnotationId


			};

			_getCutoffsPerPsmPeptideTypeResult =
				_getCutoffsPerPsmPeptideType( getCutoffsPerPsmPeptideTypeParams );


			if ( _getCutoffsPerPsmPeptideTypeResult.output_FieldDataFailedValidation  ) {

				output_FieldDataFailedValidation = true;
			}
			
		}
		
		var resultObj = { 
				
				cutoffsBySearchId : outputCutoffsBySearchId ,
				cutoffsByAnnotationId : outputCutoffsByAnnotationId,
				
				getCutoffsFromThePageResult_FieldDataFailedValidation : output_FieldDataFailedValidation
		};

		
		if ( output_FieldDataFailedValidation ) {

			var $element = $("#error_message_cutoff_value_invalid");
		
			showErrorMsg( $element );
		}
		
		
				
		return resultObj;
	};
	
	
	
	///////////////
	
	//   Internal function called by object function  this.getCutoffsFromThePage( ... )
	
		
	var _getCutoffsPerPsmPeptideType = function( params ) {
		
		var psmPeptideCutoffsObjKey = params.psmPeptideCutoffsObjKey;

		var psmPeptideCutoffs_CSS_Class = params.psmPeptideCutoffs_CSS_Class;
		
		var outputCutoffsBySearchId = params.outputCutoffsBySearchId.searches;
		
		var outputCutoffsByAnnotationId = params.outputCutoffsByAnnotationId;
		
		
		var output_FieldDataFailedValidation = false;
		

		var $annotation_cutoff_input_field_jq_for_psm_or_peptide_type = $( "." + psmPeptideCutoffs_CSS_Class );

		$annotation_cutoff_input_field_jq_for_psm_or_peptide_type.each( function( index, element ) {
			
			//   The processing of input fields is stopped if an input value is not a valid decimal

			var $item = $( this );

//			if ( ! $item.is(':visible')  ) {
//				
//				return;  // EARLY EXIT from processing this input field since hidden
//			}

			var annotationTypeIdString = $item.attr("data-type_id");
			var searchIdString = $item.attr("data-search_id");
			
//			var cutoffValue = $item.val();
			
			//  Get value in data
			var cutoffValue = $item.data( _CUTOFF_VALUE__DATA__ );
			
			if ( cutoffValue === undefined || cutoffValue === null ) {
				
				cutoffValue = "";
			}

			
			var searchId = parseInt( searchIdString, 10 );

			if ( isNaN( searchId ) ) {

				throw "Unable to parse search Id: " + searchIdString;
			}



			///////////////////////////
			
			///   Add output cutoffs for search id here so always create it 
			//          even if only has empty arrays for psm and peptide cutoffs

			//  get by search base object and create if necessary
			
			var outputCutoffsForSearchId = outputCutoffsBySearchId[ searchIdString ];
			
			if ( outputCutoffsForSearchId === undefined ) {
				
				//  not in object so create and add it
				
				outputCutoffsForSearchId = { psmCutoffValues : {}, peptideCutoffValues : {}, searchId : searchId };
				
				outputCutoffsBySearchId[ searchIdString ] = outputCutoffsForSearchId;
			}
			
			
			///////////////////
			
			
			
			//  Check for empty string since empty string does not get sent to the server.

			if ( cutoffValue === "" ) {
				
				return;  // EARLY EXIT from processing this input field since is empty string
			}

			var annotationTypeId = parseInt( annotationTypeIdString, 10 );

			if ( isNaN( annotationTypeId ) ) {

				throw "Unable to parse annotation Type Id: " + annotationTypeIdString;
			}
			
			if ( cutoffValue !== "" ) {
				
				// only test for valid cutoff value if not empty string

				if ( !  /^[+-]?((\d+(\.\d*)?)|(\.\d+))$/.test( cutoffValue ) ) {
					
					//  cutoff value is not a valid decimal number

					if ( ! output_FieldDataFailedValidation ) {

						//  Put focus on first error

						$item.focus();
					}


					output_FieldDataFailedValidation = true;


					//  For now, exit after find first error.  Can change to continue if flag errors visually on the page

					return false;  //  EARLY EXIT of ".each" loop
					//  Stop the loop from within the callback function by returning false.

				}
			}
			
			
			var outputByAnnotationIdCutoffValueEntry = { id: annotationTypeId, value : cutoffValue };
			outputCutoffsByAnnotationId.push( outputByAnnotationIdCutoffValueEntry );
			
			
			
			var outputCutoffsForPsmOrPeptide = outputCutoffsForSearchId[ psmPeptideCutoffsObjKey ];
			

			if ( outputCutoffsForSearchId === undefined ) {
				
				throw "output object does not contain property with key: " + psmPeptideCutoffsObjKey;
			}
			
			outputCutoffsForPsmOrPeptide[ annotationTypeIdString ] = outputByAnnotationIdCutoffValueEntry;
			
			
		} );
		
		
		return { output_FieldDataFailedValidation : output_FieldDataFailedValidation };
		
	};
	
	
	//  "Save" button to save user entered values
	
	this.saveUserValues = function( params ) {

		var clickedThis = params.clickedThis;
		
		var $clickedThis = $( clickedThis );

		var $cutoff_overlay_enclosing_block_jq = $clickedThis.closest(".cutoff_overlay_enclosing_block_jq");
		
		var $annotation_cutoff_input_field_jq = $cutoff_overlay_enclosing_block_jq.find(".annotation_cutoff_input_field_jq");
		
		var output_FieldDataFailedValidation = false;
		
		/////  Validate the field data

		$annotation_cutoff_input_field_jq.each( function( index, element ) {

			//   The processing of input fields is stopped if an input value is not a valid decimal

			var $inputField = $( this );
			

			var cutoffValue = $inputField.val().trim();
			
			
			//  Check for empty string since empty string does not get sent to the server.

			if ( cutoffValue === "" ) {

				//  Store value in data
				$inputField.data( _CUTOFF_VALUE__DATA__, cutoffValue );
				
				return;  // EARLY EXIT from processing this input field since is empty string
			}
			
			if ( cutoffValue !== "" ) {
				
				// only test for valid cutoff value if not empty string

				if ( !  /^[+-]?((\d+(\.\d*)?)|(\.\d+))$/.test( cutoffValue ) ) {
					
					//  cutoff value is not a valid decimal number

					//  Put focus on first error

					$inputField.focus();


					output_FieldDataFailedValidation = true;


					//  For now, exit after find first error.  Can change to continue if flag errors visually on the page

					return false;  //  EARLY EXIT of ".each" loop
					//  Stop the loop from within the callback function by returning false.

				}
			}
		});
		


		var $cutoffs_overlay_container_jq = $clickedThis.closest(".cutoffs_overlay_container_jq");
		

		if ( output_FieldDataFailedValidation ) {
			
			//   Validation failed so exit

			if ( output_FieldDataFailedValidation ) {

				var $element = $cutoffs_overlay_container_jq.find(".error_message_cutoff_value_invalid_jq");
			
				showErrorMsg( $element );
			}
			
			
			return;  //  EARLY EXIT
		}
		
		this._storeFieldValuesAndUpdateCutoffDisplay( 
				{ 
					$cutoff_overlay_enclosing_block_jq : $cutoff_overlay_enclosing_block_jq,
					saveUserValuesRequested : true 
				} );
		

		this.closeCutoffOverlay( { $cutoffs_overlay_container_jq : $cutoffs_overlay_container_jq } );
		
	};
	
	
	
	//  "Cancel" button to restore user entered values
	
	this.cancel_RestoreUserValues = function( params ) {

		var clickedThis = params.clickedThis;
		
		var $clickedThis = $( clickedThis );

		var $cutoffs_overlay_container_jq = $clickedThis.closest(".cutoffs_overlay_container_jq");
		
		var $annotation_cutoff_input_field_jq = $cutoffs_overlay_container_jq.find(".annotation_cutoff_input_field_jq");
		

		$annotation_cutoff_input_field_jq.each( function( index, element ) {

			var $inputField = $( this );

			//  get value in data
			var cutoffValue = $inputField.data( _CUTOFF_VALUE__DATA__ );
			
			if ( cutoffValue === undefined || cutoffValue === null ) {
				
				cutoffValue = "";
			}

			$inputField.val( cutoffValue );
			
		});
			

		this.closeCutoffOverlay( { $cutoffs_overlay_container_jq : $cutoffs_overlay_container_jq } );
		

	};	
	

	
	//  "Set to Defaults" button to restore default values
	
	this.setToDefaultValues = function( params ) {

		var clickedThis = params.clickedThis;
		
		var $clickedThis = $( clickedThis );

		var $cutoff_overlay_enclosing_block_jq = $clickedThis.closest(".cutoff_overlay_enclosing_block_jq");
		
		var $annotation_cutoff_input_field_jq = $cutoff_overlay_enclosing_block_jq.find(".annotation_cutoff_input_field_jq");
		

		$annotation_cutoff_input_field_jq.each( function( index, element ) {

			var $inputField = $( this );
			
			var $cutoff_input_field_block_jq = $inputField.closest(".cutoff_input_field_block_jq");
			
			var $annotation_cutoff_default_value_field_jq = $cutoff_input_field_block_jq.find(".annotation_cutoff_default_value_field_jq");
			
			var defaultValue = $annotation_cutoff_default_value_field_jq.val();

			$inputField.val( defaultValue );

			//  DO NOT DO THIS:
			//  Store value in data
//			$inputField.data( _CUTOFF_VALUE__DATA__, defaultValue );
			
		});
			

		//  DO NOT Close Overlay
	};
	
	

	
	///////////////////

	//  Read input fields and store in .data and update associated cutoff display area
	
	this._storeFieldValuesAndUpdateCutoffDisplay = function( params ) {
	
		
		var $cutoff_overlay_enclosing_block_jq = params.$cutoff_overlay_enclosing_block_jq;
		
		var saveUserValuesRequested = params.saveUserValuesRequested;
		

		///    Get data from the input fields.  Store it in .data(...) and in an array
		
		
		
		var $annotation_cutoff_input_field_jq = $cutoff_overlay_enclosing_block_jq.find(".annotation_cutoff_input_field_jq");
		
		
		var anyValuesChanged = false;
		
		var cutoffDataEntries = [];
		

		$annotation_cutoff_input_field_jq.each( function( index, element ) {


			var $inputField = $( this );
			

			var cutoffValue = $inputField.val();
			
			
			
			//  Compare new value to prev value
			
			//  Get prev value in data
			var prevCutoffValue = $inputField.data( _CUTOFF_VALUE__DATA__ );
						
			if ( cutoffValue !== prevCutoffValue ) {

				anyValuesChanged = true;
			}
			
			
			
			//  Store value in data
			$inputField.data( _CUTOFF_VALUE__DATA__, cutoffValue );
			
			
			//  Check for empty string since empty string get displayed on filter list

			if ( cutoffValue === "" ) {

				return;  // EARLY EXIT from processing this input field since is empty string
			}

			var $cutoff_input_field_block_jq = $inputField.closest(".cutoff_input_field_block_jq");

			if ( $cutoff_input_field_block_jq.length === 0 ) {
				
				throw "failed to find element with class 'cutoff_input_field_block_jq'";
			}
			
			var $annotation_display_name_field_jq = $cutoff_input_field_block_jq.find(".annotation_display_name_field_jq");
			
			if ( $annotation_display_name_field_jq.length === 0 ) {
				
				throw "failed to find element with class 'annotation_display_name_field_jq'";
			}
			
			var annotation_display_name = $annotation_display_name_field_jq.val();

			var $annotation_description_field_jq = $cutoff_input_field_block_jq.find(".annotation_description_field_jq");

			if ( $annotation_description_field_jq.length === 0 ) {
				
				throw "failed to find element with class 'annotation_description_field_jq'";
			}
			
			var annotation_description = $annotation_description_field_jq.val();

			var cutoffData = { 
					display_name : annotation_display_name, 
					description : annotation_description, 
					value : cutoffValue };
			
			cutoffDataEntries.push( cutoffData );

		});
		
		
		var associated_cutoffs_display_block_id = $cutoff_overlay_enclosing_block_jq.attr("data-associated_cutoffs_display_block_id");
		
		if ( associated_cutoffs_display_block_id === undefined 
				|| associated_cutoffs_display_block_id === null
				|| associated_cutoffs_display_block_id === "" ) {
			
			throw "Attribute 'data-associated_cutoffs_display_block_id' not set on element with class 'cutoff_overlay_enclosing_block_jq' ";
		}
		

		if ( _handlebarsTemplate_filter_single_value_display_template === null ) {
			
			var handlebarsSource_filter_single_value_display_template = $( "#filter_single_value_display_template" ).html();

			if ( handlebarsSource_filter_single_value_display_template === undefined ) {
				throw "handlebarsSource_filter_single_value_display_template === undefined";
			}
			if ( handlebarsSource_filter_single_value_display_template === null ) {
				throw "handlebarsSource_filter_single_value_display_template === null";
			}
			
			_handlebarsTemplate_filter_single_value_display_template = Handlebars.compile( handlebarsSource_filter_single_value_display_template );
		}
		

		var $associated_cutoffs_display_block = $("#" + associated_cutoffs_display_block_id );
		
		$associated_cutoffs_display_block.empty();
		
		
		/////   Take the field data in the array and update the main filter value(s) display on the main page (not in the overlay) 
		
		for ( var cutoffDataIndex = 0; cutoffDataIndex < cutoffDataEntries.length ; cutoffDataIndex++ ) {

			var cutoffData = cutoffDataEntries[ cutoffDataIndex ];

			var context = { data : cutoffData };

			var html = _handlebarsTemplate_filter_single_value_display_template( context );


//			var $filter_single_value_display_template = 
				$( html ).appendTo( $associated_cutoffs_display_block ); 
			
				//  Append this to create a space between spans of filter values so that wrapping between filter values can occur
			$( "<span style='font-size: 1px;'> </span>" ).appendTo( $associated_cutoffs_display_block ); 
		}
		
		addToolTips( $associated_cutoffs_display_block );
		
		if ( anyValuesChanged && saveUserValuesRequested ) {
			
			if ( window.defaultPageView ) {
			
				defaultPageView.searchFormChanged_ForDefaultPageView();
			}
		}
		
		
	};	
	

	//   Edit icon, label, or value clicked

	this.openCutoffOverlay = function( params ) {

		var clickedThis = params.clickedThis;

		var $clickedThis = $( clickedThis );
		
		//  Gen enclosing <tr> with data value
		var $cutoff_per_search_block_tr_jq = $clickedThis.closest(".cutoff_per_search_block_tr_jq");
		
		var associated_overlay_container_id = $cutoff_per_search_block_tr_jq.attr("data-associated_overlay_container_id");
		
		var $associated_overlay_container_id = $("#" + associated_overlay_container_id);
		
		if ( $associated_overlay_container_id.length === 0 ) {
			
			throw "Failed to find associated_overlay_container_id with id: " + associated_overlay_container_id;
		}
		
		var $filter_cutoffs_modal_dialog_overlay_background_jq = $associated_overlay_container_id.find(".filter_cutoffs_modal_dialog_overlay_background_jq");
		

		if ( $filter_cutoffs_modal_dialog_overlay_background_jq.length === 0 ) {
			
			throw "Failed to find associated_overlay_container_id with class: filter_cutoffs_modal_dialog_overlay_background_jq";
		}
		
		var $filter_cutoffs_modal_dialog_overlay_div_jq = $associated_overlay_container_id.find(".filter_cutoffs_modal_dialog_overlay_div_jq");

		if ( $filter_cutoffs_modal_dialog_overlay_div_jq.length === 0 ) {
			
			throw "Failed to find associated_overlay_container_id with class: filter_cutoffs_modal_dialog_overlay_div_jq";
		}
		
		
		$filter_cutoffs_modal_dialog_overlay_background_jq.show();
		
		$filter_cutoffs_modal_dialog_overlay_div_jq.show();
		
	};
	
	


	//   Close Overlay

	this.closeCutoffOverlay = function( params ) {
	
		var $cutoffs_overlay_container_jq = params.$cutoffs_overlay_container_jq;
		

		var $filter_cutoffs_modal_dialog_overlay_background_jq = $cutoffs_overlay_container_jq.find(".filter_cutoffs_modal_dialog_overlay_background_jq");
		

		if ( $filter_cutoffs_modal_dialog_overlay_background_jq.length === 0 ) {
			
			throw "Failed to find associated_overlay_container_id with class: filter_cutoffs_modal_dialog_overlay_background_jq";
		}
		
		var $filter_cutoffs_modal_dialog_overlay_div_jq = $cutoffs_overlay_container_jq.find(".filter_cutoffs_modal_dialog_overlay_div_jq");

		if ( $filter_cutoffs_modal_dialog_overlay_div_jq.length === 0 ) {
			
			throw "Failed to find associated_overlay_container_id with class: filter_cutoffs_modal_dialog_overlay_div_jq";
		}
		
		
		$filter_cutoffs_modal_dialog_overlay_background_jq.hide();
		
		$filter_cutoffs_modal_dialog_overlay_div_jq.hide();
		
		
	};
	
};


//   Instance of class

var cutoffProcessingCommonCode = new CutoffProcessingCommonCode();
