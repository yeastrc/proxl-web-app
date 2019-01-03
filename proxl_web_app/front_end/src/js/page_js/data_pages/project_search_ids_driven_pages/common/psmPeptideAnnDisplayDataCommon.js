
//   psmPeptideAnnDisplayDataCommon.js

//  Javascript:  Common code for processing the Annotation data Display section on the page

//////////////////////////////////
// JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

//  Instance variable:  annotationDataDisplayProcessingCommonCode

//  Constructor
var AnnotationDataDisplayProcessingCommonCode = function() {
	
	var initializeCalled = false;
	
	//  example:  {"searches":{"212":{"psm":[2075,2076,2077],"peptide":[2071,2072,2073]}}}
	var _annTypeIdDisplayDefaultValues = undefined;  // Default values from Search annotation type data in DB
	var _annTypeIdDisplayInitialValues = undefined;  //  Values when Page loads
	var _annTypeIdDisplayWorkingValues = undefined;  //  Values as user changes the overlay
	var _annTypeIdDisplaySavedValues   = undefined;    //  Values when user clicks "Save"
//	var _handlebarsTemplate_annotation_data_display_single_value_display_template = null;
	var _handlebarsTemplate_annotation_data_display_sort_block_single_value_display_template = null;
	//  initialize the page (Add element listeners like onClick, ...)
	
	this.initialize = function(  ) {
		try {
			var objectThis = this;
			if ( initializeCalled ) {  //  initialize() already called
				return;  // EARLY EXIT
			}
			//  Get default display ann type ids
			var $annotation_displays_defaults_json_jq = $("#annotation_displays_defaults_json_jq");
			if ( $annotation_displays_defaults_json_jq.length === 0 ) {
				throw Error( "Failed to find element with id: 'annotation_displays_defaults_json_jq'" );
			}
			var annotation_displays_defaults_json = $annotation_displays_defaults_json_jq.text();
			if ( annotation_displays_defaults_json === undefined ||
					annotation_displays_defaults_json === null || 
					annotation_displays_defaults_json === "" ) {
				throw Error( "Element with id: 'annotation_displays_defaults_json_jq' does not contain a value" );
			}
			_annTypeIdDisplayDefaultValues = JSON.parse( annotation_displays_defaults_json );
			//  If On Coverage page, no initialization needed.
			var $annotation_mgmt_coverage_page_script_tag = $("#annotation_mgmt_coverage_page_script_tag");
			if ( $annotation_mgmt_coverage_page_script_tag.length > 0 ) {
				// #annotation_mgmt_coverage_page_script_tag present so on coverage page
				initializeCalled = true;
				return;  // EARLY EXIT
			}
			// Add .sortable to Annotation Display Overlay Data Block
			var $annotation_displays_container_jq = $(".annotation_displays_container_jq");
			if ( $annotation_displays_container_jq.length === 0 ) {
				throw Error( "Failed to find elements with class: 'annotation_displays_container_jq'" );
			}
			var $annotation_display_order_items_container_jq = $annotation_displays_container_jq.find(".annotation_display_order_items_container_jq");
			if ( $annotation_display_order_items_container_jq.length === 0 ) {
				throw Error( "Failed to find elements with class: 'annotation_display_order_items_container_jq'" );
			}
			$annotation_display_order_items_container_jq.sortable( {
				//  On sort start, call this function
		        start : function( event, ui ) { 
		        	objectThis.processSortStartAnnotationDisplayItems( event, ui ); 
		        },
				//  This event is triggered when the user stopped sorting and the DOM position has changed.
				//        (User released the item they were dragging and items are now in "after sort" order)
		        update : function( event, ui ) { 
		        	objectThis.processSortUpdateAnnotationDisplayItems( event, ui ); 
		        }	
			} );
			initializeCalled = true;
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
	
	////////   Called when user starts dragging an element to change the order
	this.processSortStartAnnotationDisplayItems = function ( event, ui ) {
		var $item = ui.item;
		//  ui.item is  <div class="outer-float  protein_select_outer_block_jq" style="" >
		var $sort_handle_jq = $item.find(".sort_handle_jq");
		$sort_handle_jq.qtip('toggle', false); // Immediately hide all tooltips belonging to the selected elements
		var $protein_select_text_container_jq = $item.find(".protein_select_text_container_jq");
		$protein_select_text_container_jq.qtip('toggle', false); // Immediately hide all tooltips belonging to the selected elements
	};
	
	////////   Called when user stops dragging an element to change the order
	//  Update the order of the annotation type ids in memory to match the page
	this.processSortUpdateAnnotationDisplayItems = function ( event, ui ) {
//		console.log("processSortUpdateAnnotationDisplayItems");
		var $item = ui.item;
		var $overlayContainerBlock = $item.closest(".annotation_displays_container_jq");
		var projectSearchId = $overlayContainerBlock.attr("data-project_search_id");
		if ( projectSearchId === undefined || projectSearchId === null || projectSearchId === "" ) {
			throw Error( "attr 'data-project_search_id' missing or empty string" );
		}
		var psmPeptideTypeString = $overlayContainerBlock.attr("data-psm_peptide_type");
		if ( psmPeptideTypeString === undefined || psmPeptideTypeString === null || psmPeptideTypeString === "" ) {
			throw Error( "attr 'data-psm_peptide_type' missing or empty string" );
		}
		var selectedAnnTypesForSearches = _annTypeIdDisplayWorkingValues.searches;
		if ( selectedAnnTypesForSearches === undefined ) {
			throw Error( "selectedAnnTypesForSearches not found for searches: " );
		}
		var selectedAnnTypesForProjectSearchId = selectedAnnTypesForSearches[ projectSearchId ];
		if ( selectedAnnTypesForProjectSearchId === undefined ) {
			throw Error( "selectedAnnTypesForProjectSearchId not found for projectSearchId: " + projectSearchId );
		}
		//  reset to empty array and will populate next while processing sorted DOM elements
		selectedAnnTypesForProjectSearchId[ psmPeptideTypeString ] = [];
		var selectedDataEntries = selectedAnnTypesForProjectSearchId[ psmPeptideTypeString ];
		if ( selectedDataEntries === undefined ) {
			throw Error( "selectedDataEntries not found for psmPeptideTypeString: " + psmPeptideTypeString );
		}
		var $annotation_data_single_sort_entry_jq_All = $overlayContainerBlock.find(".annotation_data_single_sort_entry_jq");
		if ( $annotation_data_single_sort_entry_jq_All.length === 0 ) {
			throw Error( "No elements with class 'annotation_data_single_sort_entry_jq' found under $overlayContainerBlock_Each" );
		}
		//  Process the annotation sort data entries, building new array of annotation type ids:
		$annotation_data_single_sort_entry_jq_All.each( function( index, element ) {
			var $annotation_data_single_sort_entry_jq_Each = $( this );
			var annotationTypeIdParam = $annotation_data_single_sort_entry_jq_Each.attr("data-annotation_type_id");
			if ( annotationTypeIdParam === undefined || annotationTypeIdParam === null || annotationTypeIdParam === "" ) {
				throw Error( "attr 'data-annotation_type_id' missing or empty string" );
			}
			var annotationTypeIdInt = parseInt( annotationTypeIdParam, 10 );
			if ( isNaN( annotationTypeIdInt ) ) {
				throw Error( "Unable to parse annotationTypeIdParam: " + annotationTypeIdParam );
			}
			selectedDataEntries.push( annotationTypeIdInt );
		});		
	};
	
	//  function called to put annotation display data on the page, update the selected items with the values in the object
	//  params is normally { annTypeIdDisplay : _query_json_field_Contents.annTypeIdDisplay }
	this.putAnnTypeIdDisplayOnThePage = function( params ) {
		try {
			var objectThis = this;
			var annTypeIdDisplay = params.annTypeIdDisplay;
			this.initialize();
			if ( annTypeIdDisplay === undefined || annTypeIdDisplay === null ) {
				annTypeIdDisplay = jQuery.extend( true /* [deep ] */, {}, _annTypeIdDisplayDefaultValues );
			}
			_annTypeIdDisplayInitialValues = annTypeIdDisplay;
			
			//  START: Special update to allow projectSearchId values to be added or removed from URL
			
			//  Update _annTypeIdDisplayInitialValues with values from _annTypeIdDisplayDefaultValues
			//      for any searches in _annTypeIdDisplayDefaultValues but not in _annTypeIdDisplayInitialValues
			
			var annTypeIdDisplayInitialValues_Searches = _annTypeIdDisplayInitialValues.searches;
			var annTypeIdDisplayDefaultValues_Searches = _annTypeIdDisplayDefaultValues.searches;
			var annTypeIdDisplayDefaultValues_Searches_KeysArray = Object.keys( annTypeIdDisplayDefaultValues_Searches );
			for ( var index = 0; index < annTypeIdDisplayDefaultValues_Searches_KeysArray.length; index++ ) {
				var searchIdInDefaultValues = annTypeIdDisplayDefaultValues_Searches_KeysArray[ index ];
				var initialValuesForSearch = annTypeIdDisplayInitialValues_Searches[ searchIdInDefaultValues ];
				if ( initialValuesForSearch === undefined || initialValuesForSearch === null ) {
					// Not in Initial values so copy from default
					var annTypeIdDisplayDefaultValues_ForSearch = annTypeIdDisplayDefaultValues_Searches[ searchIdInDefaultValues ];
					var cloneOfDefaultValuesForSearch = jQuery.extend( true /* [deep ] */, {}, annTypeIdDisplayDefaultValues_ForSearch );
					annTypeIdDisplayInitialValues_Searches[ searchIdInDefaultValues ] = cloneOfDefaultValuesForSearch;
				}
			}
			//  Remove _annTypeIdDisplayInitialValues entries for searches not in _annTypeIdDisplayDefaultValues
			var projectSearchIdIdArry = Object.keys( annTypeIdDisplayInitialValues_Searches );
			for ( var index = 0; index < projectSearchIdIdArry.length; index++ ) {
				var projectSearchId = projectSearchIdIdArry[ index ];
				var annTypeIdDisplayDefaultValues_ForSrchId = annTypeIdDisplayDefaultValues_Searches[ projectSearchId ];
				if ( annTypeIdDisplayDefaultValues_ForSrchId === undefined || annTypeIdDisplayDefaultValues_ForSrchId === null ) {
					// Not in default values so remove from input
					delete annTypeIdDisplayInitialValues_Searches[ projectSearchId ];
				}
			}
			
			//  END: Special update to allow projectSearchId values to be added or removed from URL
			
			
			//  Values when user clicks "Save"
			//  Deep copy of _annTypeIdDisplayInitialValues to _annTypeIdDisplaySavedValues
			_annTypeIdDisplaySavedValues = jQuery.extend( true /* [deep ] */, {}, _annTypeIdDisplayInitialValues );
			//  Values as user changes the overlay
			//  Deep copy of _annTypeIdDisplayInitialValues to _annTypeIdDisplayWorkingValues
			_annTypeIdDisplayWorkingValues = jQuery.extend( true /* [deep ] */, {}, _annTypeIdDisplayInitialValues );
			var annDisplayItemsSearches = annTypeIdDisplay.searches;
			if ( annDisplayItemsSearches === undefined ) {
				throw Error( "no property 'searches' on annTypeIdDisplay" );
			}
			var $annotation_displays_modal_dialog_overlay_div_jq = $("#annotation_displays_modal_dialog_overlay_div_jq");
			var $per_search_container_jq_All = $annotation_displays_modal_dialog_overlay_div_jq.find(".per_search_container_jq");
			if ( $per_search_container_jq_All.length === 0 ) {
				throw Error( "No element with class 'per_search_container_jq' found" );
			}
			if ( $per_search_container_jq_All.length > 1 ) {
				var $per_search_container_jqFirst = $per_search_container_jq_All.first();
				$per_search_container_jqFirst.show(); // show the first one
			}
			//  Process all searches
			var processedProjectSearchIds = {};
			$per_search_container_jq_All.each( function() {
				var $per_search_container_jq_Each = $( this );
				var project_search_id = $per_search_container_jq_Each.attr("data-project_search_id");
				if ( project_search_id === undefined || project_search_id === null || project_search_id === "" ) {
					throw Error( "attr 'data-project_search_id' missing or empty string" );
				}
				var annDisplayItemsForSearch = annDisplayItemsSearches[ project_search_id ];
				if ( annDisplayItemsForSearch === undefined || annDisplayItemsForSearch === null ) {
					throw Error( "No data found in input params for project_search_id: " + project_search_id );
				}
				//   process all blocks
				var $overlayContainerBlock_All = $per_search_container_jq_Each.find(".annotation_displays_container_jq");
				if ( $overlayContainerBlock_All.length === 0 ) {
					throw Error( "No element with class 'annotation_displays_container_jq' found" );
				}
				// Process each $overlayContainerBlock (psm and peptide)
				$overlayContainerBlock_All.each( function() {
					var $overlayContainerBlock_Each = $( this );
					var $annotation_display_select_items_container_jq =  $overlayContainerBlock_Each.find(".annotation_display_select_items_container_jq");
					if ( $annotation_display_select_items_container_jq.length === 0 ) {
						throw Error( "No element with class 'annotation_display_select_items_container_jq' found" );
					}
					var psmAnnDisplayItemsForSearch = annDisplayItemsForSearch.psm;
					if ( psmAnnDisplayItemsForSearch === undefined || psmAnnDisplayItemsForSearch === null ) {
						throw Error( "No data found in input params for project_search_id: " + project_search_id + " for property 'psm'" );
					}
					//  Update overlay display for data in input params
					objectThis._updateSelectionListChooseItemListFromSelectedAndOrder( { $overlayContainerBlock : $overlayContainerBlock_Each } );
					//  Update main display with selected ann types
//					objectThis._updateAnnDataDisplayMainDisplay( { $overlayContainerBlock : $overlayContainerBlock_Each } );
					processedProjectSearchIds[ project_search_id ] = true;
				}); // all blocks
			}); //  all searches
			//  Validate that processed all search ids in input params
			var annDisplayItemsProjectSearchIdArray = Object.keys( annDisplayItemsSearches );
			for ( var annDisplayItemsProjectSearchIdIndex = 0; annDisplayItemsProjectSearchIdIndex < annDisplayItemsProjectSearchIdArray.length; annDisplayItemsProjectSearchIdIndex++ ) {
				var annDisplayItemsProjectSearchId = annDisplayItemsProjectSearchIdArray[ annDisplayItemsProjectSearchIdIndex ];
				if ( ! processedProjectSearchIds[ annDisplayItemsProjectSearchId ] ) {
					throw Error( "Project_Search_Id in input params '" + annDisplayItemsProjectSearchId + "' not found on the page." );
				}
			}
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
	
	//  function called to get annotation type ids for display from the page, from the user input fields
	//  Returns object of format
//	{
//		annTypeIdDisplayByProjectSearchId : { 
//			searches : 
//			{
//				"<ProjectSearchId>" :
//					"psm" : [ <annotationTypeId>, <annotationTypeId> ]
//			}
//		},
//	}
	
	// 
	this.getAnnotationTypeDisplayFromThePage = function( params ) {
		try {
			var resultObj = { 
					annTypeIdDisplayByProjectSearchId : _annTypeIdDisplaySavedValues
			};
			return resultObj;
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
	
	//   
	this.closeAnnDisplayDataOverlay = function( params ) {
		try {
//			var clickedThis = params.clickedThis;
//			var $clickedThis = $( clickedThis );
			var $annotation_displays_modal_dialog_overlay_background_jq = $("#annotation_displays_modal_dialog_overlay_background_jq");
			if ( $annotation_displays_modal_dialog_overlay_background_jq.length === 0 ) {
				throw Error( "Failed to find with id: #annotation_displays_modal_dialog_overlay_background_jq" );
			}
			var $annotation_displays_modal_dialog_overlay_div_jq = $("#annotation_displays_modal_dialog_overlay_div_jq");
			if ( $annotation_displays_modal_dialog_overlay_div_jq.length === 0 ) {
				throw Error( "Failed to find with id: #annotation_displays_modal_dialog_overlay_div_jq" );
			}
			$annotation_displays_modal_dialog_overlay_background_jq.hide();
			$annotation_displays_modal_dialog_overlay_div_jq.hide();
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
	
	//   Edit icon, label, or value clicked
	this.openAnnDisplayDataOverlay = function( params ) {
		try {
//			var clickedThis = params.clickedThis;
//			var $clickedThis = $( clickedThis );
			var $annotation_displays_modal_dialog_overlay_background_jq = $("#annotation_displays_modal_dialog_overlay_background_jq");
			if ( $annotation_displays_modal_dialog_overlay_background_jq.length === 0 ) {
				throw Error( "Failed to find with id: #annotation_displays_modal_dialog_overlay_background_jq" );
			}
			var $annotation_displays_modal_dialog_overlay_div_jq = $("#annotation_displays_modal_dialog_overlay_div_jq");
			if ( $annotation_displays_modal_dialog_overlay_div_jq.length === 0 ) {
				throw Error( "Failed to find with id: #annotation_displays_modal_dialog_overlay_div_jq" );
			}
			$annotation_displays_modal_dialog_overlay_background_jq.show();
			$annotation_displays_modal_dialog_overlay_div_jq.show();
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
	
	//////
	this.dataEntryClicked = function( params ) {
		try {
			var clickedThis = params.clickedThis; //  HTML <div> object for clicked protein name
			var $clickedThis = $( clickedThis );
			var $overlayContainerBlock = $clickedThis.closest(".annotation_displays_container_jq");
			var $annotation_data_single_entry_jq = $clickedThis;
			this.addToSelectedAnnotationTypeList( {
				$overlayContainerBlock : $overlayContainerBlock,
				$annotation_data_single_entry_jq : $annotation_data_single_entry_jq,
				addToSelectedArray : true
			} );
			$annotation_data_single_entry_jq.hide();
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
	
	//////
	this.searchSelectorChanged = function( params ) {
		try {
			var selectThis = params.selectThis; //  <select>
			var $selectThis = $( selectThis );
			var projectSearchId = $selectThis.val();
			// First hide all searches
			var $annotation_displays_modal_dialog_overlay_body = $("#annotation_displays_modal_dialog_overlay_body");
			var $per_search_container_jq_All =  $annotation_displays_modal_dialog_overlay_body.find(".per_search_container_jq");
			$per_search_container_jq_All.hide();
			// Then show the selected search
			var elementId = "annotation_displays_modal_dialog_overlay_div_project_search_id_" + projectSearchId;
			var $selectedSearchIdElement = $( "#" + elementId );
			$selectedSearchIdElement.show();
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
	
	//////
	this.setToDefaultValues = function( params ) {
		try {
			var objectThis = this;
//			var clickedThis = params.clickedThis; //  HTML <div> object for clicked protein name
//			var $clickedThis = $( clickedThis );
			//  Deep copy of _annTypeIdDisplayDefaultValues to _annTypeIdDisplayWorkingValues
			_annTypeIdDisplayWorkingValues = jQuery.extend( true /* [deep ] */, {}, _annTypeIdDisplayDefaultValues );
			//   process all blocks
			var $overlayContainerBlock_All = $(".annotation_displays_container_jq");
			if ( $overlayContainerBlock_All.length === 0 ) {
				throw Error( "No element with class 'annotation_displays_container_jq' found" );
			}
			// Process each $overlayContainerBlock in case there are multiples such as for page initialization
			$overlayContainerBlock_All.each( function() {
				var $overlayContainerBlock_Each = $( this );
				objectThis._updateSelectionListChooseItemListFromSelectedAndOrder( { $overlayContainerBlock : $overlayContainerBlock_Each } );
			});
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
	
	this.saveUserValues = function( params ) {
		try {
			var clickedThis = params.clickedThis; 
//			var $clickedThis = $( clickedThis );
			//  Values when user clicks "Save"
			//  TODO   Consider checking that any values changed and if not, close overlay and don't reload page
			//  Deep copy of _annTypeIdDisplayInitialValues to _annTypeIdDisplaySavedValues
			_annTypeIdDisplaySavedValues = jQuery.extend( true /* [deep ] */, {}, _annTypeIdDisplayWorkingValues );
			//  Update main display with selected ann types
//			this._updateAnnDataDisplayMainDisplay( { $overlayContainerBlock : $overlayContainerBlock } );
			//  Hide the overlay
			this.closeAnnDisplayDataOverlay({ clickedThis : clickedThis });
			//  Reload page for new values.  'standardFullPageCode' is a copy of the main JS object for the page
			if ( window.standardFullPageCode && window.standardFullPageCode.updatePageForFormParams ) {
				standardFullPageCode.updatePageForFormParams();
			}
			//  For Image and Structure pages, update main stored values
			if ( window.webserviceDataParamsDistributionCommonCode ) {
				webserviceDataParamsDistributionCommonCode.paramsForDistribution( { 
					annTypeIdDisplay : _annTypeIdDisplaySavedValues
				} );
			}
			if ( window.updateURLHash ) {
				updateURLHash( false );
			}
			if ( window.populateNavigation ) {
				populateNavigation();
			}
			this.reloadDisplayedDataOnImageAndStructurePages();
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
	
	//////
	this.reloadDisplayedDataOnImageAndStructurePages = function( params ) {
		//  For Image and Structure pages, update displayed data
		var $link_data_table_place_holder = $("#link_data_table_place_holder");
		if ( $link_data_table_place_holder.length === 0 ) {
			//  Not on page so must not be Image or Structure page
			return;  //  EARLY EXIT
		}
		var $link_info_table__tbody_jq = $link_data_table_place_holder.find(".link_info_table__tbody_jq");
		if ( $link_info_table__tbody_jq.length === 0 ) {
			//  Not on page so must not be Image or Structure page
			return;  //  EARLY EXIT
		}
		var $main_data_row_jq_All = $link_info_table__tbody_jq.find(".main_data_row_jq");
		if ( $main_data_row_jq_All.length === 0 ) {
			//  Not on page so must not be Image or Structure page
			return;  //  EARLY EXIT
		}
		$main_data_row_jq_All.each( function( index, element ) {
			var $main_data_row_jq_Each = $( this );
			//  Retrieve the string containing the object in Window scope, if it exists, call the reloadData function on it
			var children_mgmt_objectString = $main_data_row_jq_Each.attr("data-children_mgmt_object");
			var children_mgmt_object = window[ children_mgmt_objectString ];
			if ( children_mgmt_object ) {
				children_mgmt_object.reloadData( { $htmlElement : $main_data_row_jq_Each } );
			}
		});
	};
	
	//////
	this.cancel_RestoreUserValues = function( params ) {
		//  Restore sort order and which items were selected
		try {
			var objectThis = this;
			var clickedThis = params.clickedThis;
//			var $clickedThis = $( clickedThis );
			//	Restore last Saved Values 
			//  Deep copy of _annTypeIdDisplayInitialValues to _annTypeIdDisplayWorkingValues
			_annTypeIdDisplayWorkingValues = jQuery.extend( true /* [deep ] */, {}, _annTypeIdDisplaySavedValues );
			//   process all blocks
			var $overlayContainerBlock_All = $(".annotation_displays_container_jq");
			if ( $overlayContainerBlock_All.length === 0 ) {
				throw Error( "No element with class 'annotation_displays_container_jq' found" );
			}
			// Process each $overlayContainerBlock in case there are multiples such as for page initialization
			$overlayContainerBlock_All.each( function() {
				var $overlayContainerBlock_Each = $( this );
				//  Update overlay display for prev values
				objectThis._updateSelectionListChooseItemListFromSelectedAndOrder( { $overlayContainerBlock : $overlayContainerBlock_Each } );
			});
			//  Hide the overlay
			this.closeAnnDisplayDataOverlay({ clickedThis : clickedThis });
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
	
	//////////
	//  Selection List functions
	this.clearSelectedAnnotationTypeList = function( params ) {
		var $overlayContainerBlock = params.$overlayContainerBlock;
		var $annotation_display_order_items_container_jq = $overlayContainerBlock.find(".annotation_display_order_items_container_jq");
		$annotation_display_order_items_container_jq.empty();
	};
	
	this.removeFromSelectedAnnotationTypeList = function( params ) {
		try {
			var clickedThis = params.clickedThis;
			var $clickedThis = $( clickedThis );
			//  Gen enclosing <tr> with data value
			var $annotation_data_single_sort_entry_jq = $clickedThis.closest(".annotation_data_single_sort_entry_jq");
			var $overlayContainerBlock = $clickedThis.closest(".annotation_displays_container_jq");
			var annotationTypeIdParam = $annotation_data_single_sort_entry_jq.attr("data-annotation_type_id");
			if ( annotationTypeIdParam === undefined || annotationTypeIdParam === null || annotationTypeIdParam === "" ) {
				throw Error( "attr 'data-annotation_type_id' missing or empty string" );
			}
			var annotationTypeIdInt = parseInt( annotationTypeIdParam, 10 );
			if ( isNaN( annotationTypeIdInt ) ) {
				throw Error( "Unable to parse annotationTypeIdParam: " + annotationTypeIdParam );
			}
			var projectSearchId = $overlayContainerBlock.attr("data-project_search_id");
			if ( projectSearchId === undefined || projectSearchId === null || projectSearchId === "" ) {
				throw Error( "attr 'data-project_search_id' missing or empty string" );
			}
			var psmPeptideTypeString = $overlayContainerBlock.attr("data-psm_peptide_type");
			if ( psmPeptideTypeString === undefined || psmPeptideTypeString === null || psmPeptideTypeString === "" ) {
				throw Error( "attr 'data-psm_peptide_type' missing or empty string" );
			}
			var selectedAnnTypesForSearches = _annTypeIdDisplayWorkingValues.searches;
			if ( selectedAnnTypesForSearches === undefined ) {
				throw Error( "selectedAnnTypesForSearches not found for searches: " );
			}
			var selectedAnnTypesForProjectSearchId = selectedAnnTypesForSearches[ projectSearchId ];
			if ( selectedAnnTypesForProjectSearchId === undefined ) {
				throw Error( "selectedAnnTypesForProjectSearchId not found for projectSearchId: " + projectSearchId );
			}
			var selectedDataEntries = selectedAnnTypesForProjectSearchId[ psmPeptideTypeString ];
			if ( selectedDataEntries === undefined ) {
				throw Error( "selectedDataEntries not found for psmPeptideTypeString: " + psmPeptideTypeString );
			}
			var $annotation_data_single_entry_jq_All = $overlayContainerBlock.find(".annotation_data_single_entry_jq");
			if ( $annotation_data_single_entry_jq_All.length === 0 ) {
				throw Error( "No elements with class 'annotation_data_single_entry_jq' found under $overlayContainerBlock_Each" );
			}
			//  Process the annotation data entries:
			$annotation_data_single_entry_jq_All.each( function( index, element ) {
				var $annotation_data_single_entry_jq_Each = $( this );
				var annotationTypeIdString = $annotation_data_single_entry_jq_Each.attr("data-annotation_type_id");
				//  If annotationTypeIdString === annotationTypeIdParam, show
				if ( annotationTypeIdString === annotationTypeIdParam ) {
					$annotation_data_single_entry_jq_Each.show();
				}
			});
			$clickedThis.qtip('toggle', false); // Immediately hide all tooltips belonging to the selected elements
			$annotation_data_single_sort_entry_jq.qtip('toggle', false); // Immediately hide all tooltips belonging to the selected elements
			$annotation_data_single_sort_entry_jq.remove();
			//  Remove from array of selected entries in memory
			var indexOfElement = selectedDataEntries.indexOf( annotationTypeIdInt );
			if ( indexOfElement > -1 ) {
				selectedDataEntries.splice( indexOfElement, 1 );
			}
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
	
	this.addToSelectedAnnotationTypeList = function( params ) {
		var $overlayContainerBlock = params.$overlayContainerBlock;
		var $annotation_data_single_entry_jq = params.$annotation_data_single_entry_jq;
		var addToSelectedArray = params.addToSelectedArray;
		var annotationTypeId = $annotation_data_single_entry_jq.attr("data-annotation_type_id");
		var annotationName = $annotation_data_single_entry_jq.attr("data-annotation_name");
		var annotationDescription = $annotation_data_single_entry_jq.attr("data-annotation_description");
		if ( annotationTypeId === undefined || annotationTypeId === null || annotationTypeId === "" ) {
			throw Error( "attr 'data-annotation_type_id' missing or empty string" );
		}
		var annotationTypeIdInt = parseInt( annotationTypeId, 10 );
		if ( isNaN( annotationTypeIdInt ) ) {
			throw Error( "Unable to parse annotationTypeIdString: " + annotationTypeIdString );
		}
		var projecSearchId = $overlayContainerBlock.attr("data-project_search_id");
		if ( projecSearchId === undefined || projecSearchId === null || projecSearchId === "" ) {
			throw Error( "attr 'data-project_search_id' missing or empty string" );
		}
		var psmPeptideTypeString = $overlayContainerBlock.attr("data-psm_peptide_type");
		if ( psmPeptideTypeString === undefined || psmPeptideTypeString === null || psmPeptideTypeString === "" ) {
			throw Error( "attr 'data-psm_peptide_type' missing or empty string" );
		}
		var selectedAnnTypesForSearches = _annTypeIdDisplayWorkingValues.searches;
		if ( selectedAnnTypesForSearches === undefined ) {
			throw Error( "selectedAnnTypesForSearches not found for searches: " );
		}
		var selectedAnnTypesForProjectSearchId = selectedAnnTypesForSearches[ projecSearchId ];
		if ( selectedAnnTypesForProjectSearchId === undefined ) {
			throw Error( "selectedAnnTypesForProjectSearchId not found for projecSearchId: " + projecSearchId );
		}
		var selectedDataEntries = selectedAnnTypesForProjectSearchId[ psmPeptideTypeString ];
		if ( selectedDataEntries === undefined ) {
			throw Error( "selectedDataEntries not found for psmPeptideTypeString: " + psmPeptideTypeString );
		}
		if ( _handlebarsTemplate_annotation_data_display_sort_block_single_value_display_template === null ) {
			var handlebarsSource_annotation_data_display_single_value_display_template = $( "#annotation_data_display_sort_block_single_value_display_template" ).html();
			if ( handlebarsSource_annotation_data_display_single_value_display_template === undefined ) {
				throw Error( "handlebarsSource_annotation_data_display_single_value_display_template === undefined" );
			}
			if ( handlebarsSource_annotation_data_display_single_value_display_template === null ) {
				throw Error( "handlebarsSource_annotation_data_display_single_value_display_template === null" );
			}
			_handlebarsTemplate_annotation_data_display_sort_block_single_value_display_template = Handlebars.compile( handlebarsSource_annotation_data_display_single_value_display_template );
		}
		var $annotation_display_order_items_container_jq = $overlayContainerBlock.find(".annotation_display_order_items_container_jq");
		var context = { 
				data : { 
					annotationTypeId : annotationTypeId, 
					name : annotationName,
					description : annotationDescription } 
		};
		var html = _handlebarsTemplate_annotation_data_display_sort_block_single_value_display_template( context );
		var $annotation_data_display_sort_block_single_value_display_template = 
			$( html ).appendTo( $annotation_display_order_items_container_jq ); 
		addToolTips( $annotation_data_display_sort_block_single_value_display_template );
		if ( addToSelectedArray ) {
			// Skip for initial population
			selectedDataEntries.push( annotationTypeIdInt );
		}
	};
	
	//////////
	//  Use selected and sort order in .data() on display block element to update the selection list and choose item list 
	//  For set default and cancel
	this._updateSelectionListChooseItemListFromSelectedAndOrder = function( params ) {
		try {
			var objectThis = this;
			var $overlayContainerBlock = params.$overlayContainerBlock;
			var projecSearchId = $overlayContainerBlock.attr("data-project_search_id");
			if ( projecSearchId === undefined || projecSearchId === null || projecSearchId === "" ) {
				throw Error( "attr 'data-project_search_id' missing or empty string" );
			}
			var psmPeptideTypeString = $overlayContainerBlock.attr("data-psm_peptide_type");
			if ( psmPeptideTypeString === undefined || psmPeptideTypeString === null || psmPeptideTypeString === "" ) {
				throw Error( "attr 'data-psm_peptide_type' missing or empty string" );
			}
			var selectedAnnTypesForSearches = _annTypeIdDisplayWorkingValues.searches;
			if ( selectedAnnTypesForSearches === undefined ) {
				throw Error( "selectedAnnTypesForSearches not found for searches: " );
			}
			var selectedAnnTypesForProjectSearchId = selectedAnnTypesForSearches[ projecSearchId ];
			if ( selectedAnnTypesForProjectSearchId === undefined ) {
				throw Error( "selectedAnnTypesForProjectSearchId not found for projecSearchId: " + projecSearchId );
			}
			var selectedDataEntries = selectedAnnTypesForProjectSearchId[ psmPeptideTypeString ];
			if ( selectedDataEntries === undefined ) {
				throw Error( "selectedDataEntries not found for psmPeptideTypeString: " + psmPeptideTypeString );
			}
			var $annotation_display_select_items_container_jq =  $overlayContainerBlock.find(".annotation_display_select_items_container_jq");
			if ( $annotation_display_select_items_container_jq.length === 0 ) {
				throw Error( "No element with class 'annotation_display_select_items_container_jq' found" );
			}
			var $annotation_data_single_entry_jq_All = $overlayContainerBlock.find(".annotation_data_single_entry_jq");
			if ( $annotation_data_single_entry_jq_All.length === 0 ) {
				if ( true ) {
					//  No elements allowed for peptides
					return;
				}
				throw Error( "No elements with class 'annotation_data_single_entry_jq' found under $overlayContainerBlock_Each" );
			}
			//  Process the annotation data entries:
			//    sort them in the order from selectedDataEntries
			var displayItemsDomElements = [];
			$annotation_data_single_entry_jq_All.each( function( index, element ) {
				var $annotation_data_single_entry_jq_Each = $( this );
				var annotationTypeIdString = $annotation_data_single_entry_jq_Each.attr("data-annotation_type_id");
				var annotationTypeIdInt = parseInt( annotationTypeIdString, 10 );
				if ( isNaN( annotationTypeIdInt ) ) {
					throw Error( "Unable to parse annotationTypeIdString: " + annotationTypeIdString );
				}
				//  If DOM entry ann type is in selectedDataEntries, save along with index in selectedDataEntries
				var foundInSelectedDataEntries = false;
				for ( var selectedDataEntriesIndex = 0; selectedDataEntriesIndex < selectedDataEntries.length; selectedDataEntriesIndex++ ) {
					var selectedDataEntriesItem = selectedDataEntries[ selectedDataEntriesIndex ];
					if ( selectedDataEntriesItem === annotationTypeIdInt ) {
						foundInSelectedDataEntries = true;
						break;
					}
				}
				if ( foundInSelectedDataEntries ) {
					var annDataEntry = {
							$annotation_data_single_entry_jq_Each : $annotation_data_single_entry_jq_Each,
							displayOrderInt : selectedDataEntriesIndex
					};
					displayItemsDomElements.push( annDataEntry );
					$annotation_data_single_entry_jq_Each.hide(); // hide in choose to display list
				} else {
					$annotation_data_single_entry_jq_Each.show(); // show in choose to display list
				}
			});
			//  Sort the elements on sort order
			displayItemsDomElements.sort( function(o1, o2) {  // o1 and o2 are array elements to compare
				return o1.displayOrderInt - o2.displayOrderInt;
			});
			objectThis.clearSelectedAnnotationTypeList( { $overlayContainerBlock : $overlayContainerBlock } );
			//  Add data for sorted elements into the Sort Order block 
			for ( var displayItemsDomElementsIndex = 0; displayItemsDomElementsIndex < displayItemsDomElements.length; displayItemsDomElementsIndex++ ) {
				var annDataEntry = displayItemsDomElements[ displayItemsDomElementsIndex ];
				var $annotation_data_single_entry_jq_Each = annDataEntry.$annotation_data_single_entry_jq_Each;
				objectThis.addToSelectedAnnotationTypeList({ 
					$overlayContainerBlock : $overlayContainerBlock,
					$annotation_data_single_entry_jq : $annotation_data_single_entry_jq_Each } );
			}
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
	
	//  update main "Display Data" section 
//	this._updateAnnDataDisplayMainDisplay = function( params ) {
//		try {
//			var $overlayContainerBlock = params.$overlayContainerBlock;
////			var selectedDataEntries = $overlayContainerBlock.data( __DATA__ITEM_SELECTED_SORT_ORDER );
//			var associated_ann_data_display_block_id = $overlayContainerBlock.attr("data-associated_ann_data_display_block_id");
//			if ( associated_ann_data_display_block_id === undefined 
//					|| associated_ann_data_display_block_id === null
//					|| associated_ann_data_display_block_id === "" ) {
//				throw Error( "Attribute 'data-associated_ann_data_display_block_id' not set on element with class 'annotation_data_single_entry_jq' " );
//			}
//			var $associated_ann_data_display_block_id = $("#" + associated_ann_data_display_block_id );
//			if ( $associated_ann_data_display_block_id.length === 0 ) {
//				throw Error( "HTML element with id: '" + associated_ann_data_display_block_id +
//				"' not found.  id came from attribute 'data-associated_ann_data_display_block_id'" );
//			}
//			if ( _handlebarsTemplate_annotation_data_display_single_value_display_template === null ) {
//				var handlebarsSource_annotation_data_display_single_value_display_template = $( "#annotation_data_display_single_value_display_template" ).html();
//				if ( handlebarsSource_annotation_data_display_single_value_display_template === undefined ) {
//					throw Error( "handlebarsSource_annotation_data_display_single_value_display_template === undefined" );
//				}
//				if ( handlebarsSource_annotation_data_display_single_value_display_template === null ) {
//					throw Error( "handlebarsSource_annotation_data_display_single_value_display_template === null" );
//				}
//				_handlebarsTemplate_annotation_data_display_single_value_display_template = Handlebars.compile( handlebarsSource_annotation_data_display_single_value_display_template );
//			}
//			var $associated_ann_data_display_block_id = $("#" + associated_ann_data_display_block_id );
//			$associated_ann_data_display_block_id.empty();
//			var $annotation_display_order_items_container_jq = $overlayContainerBlock.find(".annotation_display_order_items_container_jq");
//			if ( $annotation_display_order_items_container_jq.length === 0 ) {
//				throw Error( "Failed to find elements with class: 'annotation_display_order_items_container_jq'" );
//			}
//			var $annotation_data_single_sort_entry_jq_All = $annotation_display_order_items_container_jq.find(".annotation_data_single_sort_entry_jq");
//			//  Empty if no entries chosen by user
////			if ( $annotation_data_single_sort_entry_jq_All.length === 0 ) {
////				throw Error( "Failed to find elements with class: 'annotation_data_single_sort_entry_jq'" );
////			}
//			/////   Take the selected items in $annotation_display_order_items_container_jq and update the main filter value(s) display on the main page (not in the overlay) 
//			$annotation_data_single_sort_entry_jq_All.each( function( index, element ) {
//				//   The processing of input fields is stopped if an input value is not a valid decimal
//				var $annotation_data_single_sort_entry_jq_Each = $( this );
//				var context = { 
//						data : { 
//							name : $annotation_data_single_sort_entry_jq_Each.attr("data-ann_name"),
//							description : $annotation_data_single_sort_entry_jq_Each.attr("data-ann_description")
//						}
//				};
//				var html = _handlebarsTemplate_annotation_data_display_single_value_display_template( context );
////				var $annotation_data_display_single_value_display_template = 
//				$( html ).appendTo( $associated_ann_data_display_block_id ); 
//				//  Append this to create a space between spans of filter values so that wrapping between filter values can occur
//				$( "<span style='font-size: 1px;'> </span>" ).appendTo( $associated_ann_data_display_block_id ); 
//			});
//			addToolTips( $associated_ann_data_display_block_id );
//		} catch( e ) {
//			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
//			throw e;
//		}
//	};
	
};

//   Instance of class
var annotationDataDisplayProcessingCommonCode = new AnnotationDataDisplayProcessingCommonCode();

window.annotationDataDisplayProcessingCommonCode = annotationDataDisplayProcessingCommonCode;

export { annotationDataDisplayProcessingCommonCode }
