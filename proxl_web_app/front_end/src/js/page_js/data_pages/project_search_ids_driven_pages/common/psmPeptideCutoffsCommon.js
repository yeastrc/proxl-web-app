
//   psmPeptideCutoffsCommon.js

//  Javascript:  Common code for processing the Cutoffs on the page

//////////////////////////////////
// JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

//_query_json_field_Contents: Object
//	cutoffs: Object
//		searches: Object
//			128: Object			
//				peptideCutoffValues: Object
//					238: Object
//						id: 238
//						value: "0.01"
//				psmCutoffValues: Object
//					384: Object
//						id: 384
//						value: "0.01"
//				searchId: 128
//	linkTypes: null
//	mods: null

//  Constructor
var CutoffProcessingCommonCode = function() {
	var initializeCalled = false;
	var _CUTOFF_VALUE__DATA__ = "cutoff_saved_value";
	var _FIELD_VALUE_HAS_ERROR__DATA__ = "cutoff_value_has_error";
	var _handlebarsTemplate_filter_single_value_display_template = null;
	//  initialize the page (Add element listeners like onClick, ...)
	
	this.initialize = function(  ) {
		try {
			var objectThis = this;
			if ( initializeCalled ) {
				return;
			}
			var $annotation_cutoff_input_field_jq = $(".annotation_cutoff_input_field_jq");
			$annotation_cutoff_input_field_jq.change(function(eventObject) {
				try {
					objectThis.inputFieldChanged( { fieldThis : this } );
					return false;
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			});
			$annotation_cutoff_input_field_jq.keyup(function(eventObject) {
				try {
					objectThis.inputFieldChanged( { fieldThis : this } );
					return false;
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			});
			initializeCalled = true;
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
	
	//  Clear error messages for what ever root element is passed in.
	//  May be for the entire overlay or just a single input field
	this.clearAllOverlayErrorMessages = function( params ) {
		try {
			var $root_element = params.$root_element;
			var objectThis = this;
			var $annotation_cutoff_input_field_jq = $root_element.find(".annotation_cutoff_input_field_jq");
			$annotation_cutoff_input_field_jq.each( function( index, element ) {
				var $fieldThis = $( this );
				objectThis.inputFieldSetHasNOError( { $fieldThis : $fieldThis } );
			} );
			var $annotation_cutoff_missing_or_exceeds_cutoff_on_import_message_jq = $root_element.find(".annotation_cutoff_missing_or_exceeds_cutoff_on_import_message_jq");
			$annotation_cutoff_missing_or_exceeds_cutoff_on_import_message_jq.hide();
			var $annotation_cutoff_not_number_message_jq = $root_element.find(".annotation_cutoff_not_number_message_jq");
			$annotation_cutoff_not_number_message_jq.hide();
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
	
	this.updateSaveButtonEnableDisable = function( params) {
		var $root_element = params.$root_element;
		if ( this.getAnyFieldsHaveError( { $root_element : $root_element } ) ) {
			var $save_user_cutoff_values_button_jq = $root_element.find(".save_user_cutoff_values_button_jq");
			$save_user_cutoff_values_button_jq.prop("disabled",true);
		} else {
			var $save_user_cutoff_values_button_jq = $root_element.find(".save_user_cutoff_values_button_jq");
			$save_user_cutoff_values_button_jq.prop("disabled",false);
		}
	};
	
	this.getAnyFieldsHaveError = function( params ) {
		var $root_element = params.$root_element;
		var objectThis = this;
		var $annotation_cutoff_input_field_jq = $root_element.find(".annotation_cutoff_input_field_jq");
		var foundAnyFieldError = false;
		$annotation_cutoff_input_field_jq.each( function( index, element ) {
			var $fieldThis = $( this );
			if ( objectThis.inputFieldGetHasError( { $fieldThis : $fieldThis } ) ) {
				foundAnyFieldError = true;
				return false;  //  EARLY EXIT of ".each" loop
				//  Stop the loop from within the callback function by returning false.
			}
		} );
		return foundAnyFieldError;
	};
	
	//  Does input field have error
	this.inputFieldGetHasError = function( params ) {
		var $fieldThis = params.$fieldThis;
		var hasError = $fieldThis.data( _FIELD_VALUE_HAS_ERROR__DATA__ );
		return hasError;
	};
	
	//  Mark input field as has error
	this.inputFieldSetHasError = function( params ) {
		var $fieldThis = params.$fieldThis;
		$fieldThis.data( _FIELD_VALUE_HAS_ERROR__DATA__, true );
	};
	
	//  Mark input field as has NO error
	this.inputFieldSetHasNOError = function( params ) {
		var $fieldThis = params.$fieldThis;
		$fieldThis.data( _FIELD_VALUE_HAS_ERROR__DATA__, false );
	};
	
	this.inputFieldChanged = function( params) {
		var fieldThis = params.fieldThis;
		this.validateSingleField( { fieldThis : fieldThis } );
		var $fieldThis = $( fieldThis );
		var $filter_cutoffs_modal_dialog_overlay_div_jq =  $fieldThis.closest(".filter_cutoffs_modal_dialog_overlay_div_jq");
		this.updateSaveButtonEnableDisable( { $root_element : $filter_cutoffs_modal_dialog_overlay_div_jq } );
	};
	
	this.validateSingleField = function( params) {
		var fieldThis = params.fieldThis;
		var $fieldThis = $( fieldThis );
		var $annotation_entry_root_tr_jq = $fieldThis.closest(".annotation_entry_root_tr_jq");
		var $annotation_cutoff_not_number_message_jq = $annotation_entry_root_tr_jq.find(".annotation_cutoff_not_number_message_jq");
		var $annotation_cutoff_missing_or_exceeds_cutoff_on_import_message_jq = $annotation_entry_root_tr_jq.find(".annotation_cutoff_missing_or_exceeds_cutoff_on_import_message_jq");
		this.clearAllOverlayErrorMessages( { $root_element : $annotation_entry_root_tr_jq } );
		var fieldValue = $fieldThis.val();
		if ( ! this.isFieldValueValidNumber( { fieldValue : fieldValue } ) ) {
			$annotation_cutoff_not_number_message_jq.show();
			this.inputFieldSetHasError( { $fieldThis : $fieldThis } );
			return;
		}
		var $annotation_cutoff_on_import_value_field_jq = $annotation_entry_root_tr_jq.find(".annotation_cutoff_on_import_value_field_jq");
		if ( $annotation_cutoff_on_import_value_field_jq.length === 0 ) {
			//  No action needed.
			return;
		}
		var annotation_cutoff_on_import_value = $annotation_cutoff_on_import_value_field_jq.val();
		if ( annotation_cutoff_on_import_value === "" ) {
			//  No action needed.
			return;
		}
		if ( fieldValue === "" ) {
			$annotation_cutoff_missing_or_exceeds_cutoff_on_import_message_jq.show();
			this.inputFieldSetHasError( { $fieldThis : $fieldThis } );
			return;
		}
		//  Get filter direction strings
		var $search_details_block_filter_direction_above_value = $("#search_details_block_filter_direction_above_value");
//		var $search_details_block_filter_direction_below_value = $("#search_details_block_filter_direction_below_value");
		var filter_direction_above_value = $search_details_block_filter_direction_above_value.text();
//		var filter_direction_below_value = $search_details_block_filter_direction_below_value.text();
		// Compare current value to cutoff on import and set to cutoff on import if required.
		var $annotation_filter_direction_field_jq = $annotation_entry_root_tr_jq.find(".annotation_filter_direction_field_jq");
		var annotation_filter_direction = $annotation_filter_direction_field_jq.val();
		var annotation_cutoff_on_importNumber = parseFloat( annotation_cutoff_on_import_value );
		if ( isNaN( annotation_cutoff_on_importNumber ) ) {
			this.inputFieldSetHasError( { $fieldThis : $fieldThis } );
			throw Error( "'annotation_cutoff_on_import_value_field_jq' is not a number: is: " + annotation_cutoff_on_import_value );
		}
		var fieldValueNumber = parseFloat( fieldValue );
		if ( isNaN( fieldValueNumber ) ) {
			this.inputFieldSetHasError( { $fieldThis : $fieldThis } );
			throw Error( "'annotation_cutoff_on_import_value_field_jq' is not a number: is: " + annotation_cutoff_on_import_value );
		}
		var valueExceedsCutoff = false;
		if ( annotation_filter_direction === filter_direction_above_value ) {
			if ( fieldValueNumber < annotation_cutoff_on_importNumber ) {
				valueExceedsCutoff = true;
			}
		} else {
			if ( fieldValueNumber > annotation_cutoff_on_importNumber ) {
				valueExceedsCutoff = true;
			}
		}
		if ( valueExceedsCutoff ) {
			$annotation_cutoff_missing_or_exceeds_cutoff_on_import_message_jq.show();
			this.inputFieldSetHasError( { $fieldThis : $fieldThis } );
		}
	};
	
	this.isFieldValueValidNumber = function( params ) {
		var fieldValue = params.fieldValue;
		if ( fieldValue === "" ) {
			return true;
		}
		// only test for valid cutoff value if not empty string
		if ( !  /^[+-]?((\d+(\.\d*)?)|(\.\d+))$/.test( fieldValue ) ) {
			//  cutoff value is not a valid decimal number
			return false; 
		}
		return true;
	};
	
	//  function called to put cutoffs on the page, update the input fields with the values in the object
	//  params is normally { cutoffs : _query_json_field_Contents.cutoffs }
	this.putCutoffsOnThePage = function( params ) {
		try {
			var objectThis = this;
			var cutoffs = params.cutoffs;
			this.initialize();
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
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
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
	//  Returns cutoffs by search id ("cutoffsByProjectSearchId") and in flat format ("cutoffsByAnnotationId") ( currently all web services expect the by search id format )
	//   "cutoffsByProjectSearchId" object is the old "outputCutoffs" standard value
	//  Returns object of format
//	{
//		cutoffsByProjectSearchId : { 
//			searches : 
//			{
//				"<projectSearchId>" : 
//				{	"searchId" : <projectSearchId>,
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
		try {
			var outputCutoffsByProjectSearchId = { searches : {} };
			var outputCutoffsByAnnotationId = [];
			var output_FieldDataFailedValidation = false;
			var getCutoffsPerPsmPeptideTypeParams;
			var _getCutoffsPerPsmPeptideTypeResult; 
			//  Get annotation data for PSM
			getCutoffsPerPsmPeptideTypeParams = {
					psmPeptideCutoffsObjKey : "psmCutoffValues",
					psmPeptideCutoffs_CSS_Class : "psm_annotation_cutoff_input_field_jq",
					outputCutoffsByProjectSearchId : outputCutoffsByProjectSearchId,
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
						outputCutoffsByProjectSearchId : outputCutoffsByProjectSearchId,
						outputCutoffsByAnnotationId : outputCutoffsByAnnotationId
				};
				_getCutoffsPerPsmPeptideTypeResult =
					_getCutoffsPerPsmPeptideType( getCutoffsPerPsmPeptideTypeParams );
				if ( _getCutoffsPerPsmPeptideTypeResult.output_FieldDataFailedValidation  ) {
					output_FieldDataFailedValidation = true;
				}
			}
			var resultObj = { 
					cutoffsByProjectSearchId : outputCutoffsByProjectSearchId ,
					cutoffsByAnnotationId : outputCutoffsByAnnotationId,
					getCutoffsFromThePageResult_FieldDataFailedValidation : output_FieldDataFailedValidation
			};
			if ( output_FieldDataFailedValidation ) {
				var $element = $("#error_message_cutoff_value_invalid");
				showErrorMsg( $element );
			}
			return resultObj;
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
	
	///////////////
	//   Internal function called by object function  this.getCutoffsFromThePage( ... )
	var _getCutoffsPerPsmPeptideType = function( params ) {
		var psmPeptideCutoffsObjKey = params.psmPeptideCutoffsObjKey;
		var psmPeptideCutoffs_CSS_Class = params.psmPeptideCutoffs_CSS_Class;
		var outputCutoffsByProjectSearchId = params.outputCutoffsByProjectSearchId.searches;
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
			var projectSearchIdString = $item.attr("data-project_search_id");
//			var cutoffValue = $item.val();
			//  Get value in data
			var cutoffValue = $item.data( _CUTOFF_VALUE__DATA__ );
			if ( cutoffValue === undefined || cutoffValue === null ) {
				cutoffValue = "";
			}
			var projectSearchId = parseInt( projectSearchIdString, 10 );
			if ( isNaN( projectSearchId ) ) {
				throw Error( "Unable to parse projectSearchIdString: " + projectSearchIdString );
			}
			///////////////////////////
			///   Add output cutoffs for search id here so always create it 
			//          even if only has empty arrays for psm and peptide cutoffs
			//  get by search base object and create if necessary
			var outputCutoffsForProjectSearchId = outputCutoffsByProjectSearchId[ projectSearchIdString ];
			if ( outputCutoffsForProjectSearchId === undefined ) {
				//  not in object so create and add it
				outputCutoffsForProjectSearchId = { 
						psmCutoffValues : {}, 
						peptideCutoffValues : {}, 
						projectSearchId : projectSearchId 
				};
				
				outputCutoffsByProjectSearchId[ projectSearchIdString ] = outputCutoffsForProjectSearchId;
			}
			///////////////////
			//  Check for empty string since empty string does not get sent to the server.
			if ( cutoffValue === "" ) {
				return;  // EARLY EXIT from processing this input field since is empty string
			}
			var annotationTypeId = parseInt( annotationTypeIdString, 10 );
			if ( isNaN( annotationTypeId ) ) {
				throw Error( "Unable to parse annotation Type Id: " + annotationTypeIdString );
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
			var outputCutoffsForPsmOrPeptide = outputCutoffsForProjectSearchId[ psmPeptideCutoffsObjKey ];
			if ( outputCutoffsForProjectSearchId === undefined ) {
				throw Error( "output object does not contain property with key: " + psmPeptideCutoffsObjKey );
			}
			outputCutoffsForPsmOrPeptide[ annotationTypeIdString ] = outputByAnnotationIdCutoffValueEntry;
		} );
		return { output_FieldDataFailedValidation : output_FieldDataFailedValidation };
	};
	
	//  "Save" button to save user entered values
	this.saveUserValues = function( params ) {
		try {
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
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
	
	//  "Cancel" button to restore user entered values
	this.cancel_RestoreUserValues = function( params ) {
		try {
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
			var $filter_cutoffs_modal_dialog_overlay_div_jq =  $cutoffs_overlay_container_jq.closest(".filter_cutoffs_modal_dialog_overlay_div_jq");
			this.clearAllOverlayErrorMessages( { $root_element : $filter_cutoffs_modal_dialog_overlay_div_jq } );
			this.closeCutoffOverlay( { $cutoffs_overlay_container_jq : $cutoffs_overlay_container_jq } );
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};	
	
	//  "Set to Defaults" button to restore default values
	this.setToDefaultValues = function( params ) {
		try {
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
//				$inputField.data( _CUTOFF_VALUE__DATA__, defaultValue );
			});
			this.clearAllOverlayErrorMessages( { $root_element : $cutoff_overlay_enclosing_block_jq } );
			this.updateSaveButtonEnableDisable( { $root_element : $cutoff_overlay_enclosing_block_jq } );
			//  DO NOT Close Overlay
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
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
			var cutoffValue = $inputField.val().trim();
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
				throw Error( "failed to find element with class 'cutoff_input_field_block_jq'" );
			}
			var $annotation_display_name_field_jq = $cutoff_input_field_block_jq.find(".annotation_display_name_field_jq");
			if ( $annotation_display_name_field_jq.length === 0 ) {
				throw Error( "failed to find element with class 'annotation_display_name_field_jq'" );
			}
			var annotation_display_name = $annotation_display_name_field_jq.val();
			var $annotation_description_field_jq = $cutoff_input_field_block_jq.find(".annotation_description_field_jq");
			if ( $annotation_description_field_jq.length === 0 ) {
				throw Error( "failed to find element with class 'annotation_description_field_jq'" );
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
			throw Error( "Attribute 'data-associated_cutoffs_display_block_id' not set on element with class 'cutoff_overlay_enclosing_block_jq' " );
		}
		if ( _handlebarsTemplate_filter_single_value_display_template === null ) {
			var handlebarsSource_filter_single_value_display_template = $( "#filter_single_value_display_template" ).html();
			if ( handlebarsSource_filter_single_value_display_template === undefined ) {
				throw Error( "handlebarsSource_filter_single_value_display_template === undefined" );
			}
			if ( handlebarsSource_filter_single_value_display_template === null ) {
				throw Error( "handlebarsSource_filter_single_value_display_template === null" );
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
		try {
			var clickedThis = params.clickedThis;
			var $clickedThis = $( clickedThis );
			//  Gen enclosing <tr> with data value
			var $cutoff_per_search_block_tr_jq = $clickedThis.closest(".cutoff_per_search_block_tr_jq");
			var associated_overlay_container_id = $cutoff_per_search_block_tr_jq.attr("data-associated_overlay_container_id");
			var $associated_overlay_container_id = $("#" + associated_overlay_container_id);
			if ( $associated_overlay_container_id.length === 0 ) {
				throw Error( "Failed to find associated_overlay_container_id with id: " + associated_overlay_container_id );
			}
			var $filter_cutoffs_modal_dialog_overlay_background_jq = $associated_overlay_container_id.find(".filter_cutoffs_modal_dialog_overlay_background_jq");
			if ( $filter_cutoffs_modal_dialog_overlay_background_jq.length === 0 ) {
				throw Error( "Failed to find associated_overlay_container_id with class: filter_cutoffs_modal_dialog_overlay_background_jq" );
			}
			var $filter_cutoffs_modal_dialog_overlay_div_jq = $associated_overlay_container_id.find(".filter_cutoffs_modal_dialog_overlay_div_jq");
			if ( $filter_cutoffs_modal_dialog_overlay_div_jq.length === 0 ) {
				throw Error( "Failed to find associated_overlay_container_id with class: filter_cutoffs_modal_dialog_overlay_div_jq" );
			}
			this.clearAllOverlayErrorMessages( { $root_element : $filter_cutoffs_modal_dialog_overlay_div_jq } );
			this.updateSaveButtonEnableDisable( { $root_element : $filter_cutoffs_modal_dialog_overlay_div_jq } );
			$filter_cutoffs_modal_dialog_overlay_background_jq.show();
			$filter_cutoffs_modal_dialog_overlay_div_jq.show();
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
	
	//   Close Overlay
	this.closeCutoffOverlay = function( params ) {
		try {
			var $cutoffs_overlay_container_jq = params.$cutoffs_overlay_container_jq;
			var $filter_cutoffs_modal_dialog_overlay_background_jq = $cutoffs_overlay_container_jq.find(".filter_cutoffs_modal_dialog_overlay_background_jq");
			if ( $filter_cutoffs_modal_dialog_overlay_background_jq.length === 0 ) {
				throw Error( "Failed to find associated_overlay_container_id with class: filter_cutoffs_modal_dialog_overlay_background_jq" );
			}
			var $filter_cutoffs_modal_dialog_overlay_div_jq = $cutoffs_overlay_container_jq.find(".filter_cutoffs_modal_dialog_overlay_div_jq");
			if ( $filter_cutoffs_modal_dialog_overlay_div_jq.length === 0 ) {
				throw Error( "Failed to find associated_overlay_container_id with class: filter_cutoffs_modal_dialog_overlay_div_jq" );
			}
			$filter_cutoffs_modal_dialog_overlay_background_jq.hide();
			$filter_cutoffs_modal_dialog_overlay_div_jq.hide();
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
};

//   Instance of class
var cutoffProcessingCommonCode = new CutoffProcessingCommonCode();

window.cutoffProcessingCommonCode = cutoffProcessingCommonCode;

export { cutoffProcessingCommonCode }
