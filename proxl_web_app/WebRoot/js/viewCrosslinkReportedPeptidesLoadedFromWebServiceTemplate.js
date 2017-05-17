
//   viewCrosslinkReportedPeptidesLoadedFromWebServiceTemplate.js

//   Process and load data into the file viewCrosslinkReportedPeptidesLoadedFromWebServiceTemplateFragment.jsp

//////////////////////////////////
// JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

//   Class contructor

var ViewCrosslinkReportedPeptidesLoadedFromWebServiceTemplate = function() {

	var _DATA_LOADED_DATA_KEY = "dataLoaded";
	var _handlebarsTemplate_crosslink_peptide_block_template = null;
	var _handlebarsTemplate_crosslink_peptide_data_row_entry_template = null;
	var _handlebarsTemplate_crosslink_peptide_child_row_entry_template = null;

	var _psmPeptideAnnTypeIdDisplay = null;
	var _psmPeptideCutoffsRootObject = null;
	
	//   Currently expect _psmPeptideCriteria = 
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
//           The key to:
//				searches - searchId
//				peptideCutoffValues and psmCutoffValues - annotation type id
//			peptideCutoffValues.id and psmCutoffValues.id - annotation type id
	
	//////////////
	this.setPsmPeptideCriteria = function( psmPeptideCutoffsRootObject ) {
		_psmPeptideCutoffsRootObject = psmPeptideCutoffsRootObject;
	};

	//////////////
	this.setPsmPeptideAnnTypeIdDisplay = function( psmPeptideAnnTypeIdDisplay ) {
		_psmPeptideAnnTypeIdDisplay = psmPeptideAnnTypeIdDisplay;
	};

	// ////////////
	this.showHideCrosslinkReportedPeptides = function( params ) {
		try {
			var clickedElement = params.clickedElement;
			var $clickedElement = $( clickedElement );
			var $itemToToggle = $clickedElement.next();
			if( $itemToToggle.is(":visible" ) ) {
				$itemToToggle.hide(); 
				$clickedElement.find(".toggle_visibility_expansion_span_jq").show();
				$clickedElement.find(".toggle_visibility_contraction_span_jq").hide();
			} else { 
				$itemToToggle.show();
				$clickedElement.find(".toggle_visibility_expansion_span_jq").hide();
				$clickedElement.find(".toggle_visibility_contraction_span_jq").show();
				this.loadAndInsertCrosslinkReportedPeptidesIfNeeded( { $topTRelement : $itemToToggle, $clickedElement : $clickedElement } );
			}
			return false;  // does not stop bubbling of click event
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
	

	// ////////////
	this.reloadData = function( params ) {
		try {
			var $htmlElement = params.$htmlElement;
			var $itemToToggle = $htmlElement.next();

			this.loadAndInsertCrosslinkReportedPeptidesIfNeeded( { 
				$topTRelement : $itemToToggle, 
				$clickedElement : $htmlElement,
				reloadData : true } );

		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
	
	
	////////////////////////
	this.loadAndInsertCrosslinkReportedPeptidesIfNeeded = function( params ) {

		var objectThis = this;
		var $topTRelement = params.$topTRelement;
		var $clickedElement = params.$clickedElement;
		var reloadData = params.reloadData;
		
		if ( ! reloadData ) {
			var dataLoaded = $topTRelement.data( _DATA_LOADED_DATA_KEY );
			if ( dataLoaded ) {
				return;  //  EARLY EXIT  since data already loaded. 
			}
		}
		
		var project_search_id = $clickedElement.attr( "data-project_search_id" );
		var protein_1_id = $clickedElement.attr( "data-protein_1_id" );
		var protein_2_id = $clickedElement.attr( "data-protein_2_id" );
		var protein_1_position = $clickedElement.attr( "data-protein_1_position" );
		var protein_2_position = $clickedElement.attr( "data-protein_2_position" );
		
		//  Convert all attributes to empty string if null or undefined
		if ( ! project_search_id ) {
			project_search_id = "";
		}
		if ( ! protein_1_id ) {
			protein_1_id = "";
		}
		if ( ! protein_2_id ) {
			protein_2_id = "";
		}
		if ( ! protein_1_position ) {
			protein_1_position = "";
		}
		if ( ! protein_2_position ) {
			protein_2_position = "";
		}
		//   Currently expect _psmPeptideCriteria = 
//						searches: Object
//							128: Object			
//								peptideCutoffValues: Object
//									238: Object
//										id: 238
//										value: "0.01"
//								psmCutoffValues: Object
//									384: Object
//										id: 384
//										value: "0.01"
//								searchId: 128
//	           The key to:
//					searches - searchId
//					peptideCutoffValues and psmCutoffValues - annotation type id
//				peptideCutoffValues.id and psmCutoffValues.id - annotation type id
		if ( _psmPeptideCutoffsRootObject === null || _psmPeptideCutoffsRootObject === undefined ) {
			throw Error( "_psmPeptideCutoffsRootObject not initialized" );
		} 
		var psmPeptideCutoffsForProjectSearchId = _psmPeptideCutoffsRootObject.searches[ project_search_id ];
		if ( psmPeptideCutoffsForProjectSearchId === undefined || psmPeptideCutoffsForProjectSearchId === null ) {
			psmPeptideCutoffsForProjectSearchId = {};
//			throw Error( "Getting data.  Unable to get cutoff data for project_search_id: " + project_search_id );
		}
		var psmPeptideCutoffsForProjectSearchId_JSONString = JSON.stringify( psmPeptideCutoffsForProjectSearchId );
		
		var psmPeptideAnnTypeDisplayPerProjectSearchId_JSONString = null;
		if ( _psmPeptideAnnTypeIdDisplay ) {
			var psmPeptideAnnTypeIdDisplayForProjectSearchId = _psmPeptideAnnTypeIdDisplay.searches[ project_search_id ];
			if ( psmPeptideAnnTypeIdDisplayForProjectSearchId === undefined || psmPeptideAnnTypeIdDisplayForProjectSearchId === null ) {
//				psmPeptideAnnTypeIdDisplayForSearchId = {};
				throw Error( "Getting data.  Unable to get ann type display data for project_search_id: " + project_search_id );
			}
			psmPeptideAnnTypeDisplayPerProjectSearchId_JSONString = JSON.stringify( psmPeptideAnnTypeIdDisplayForProjectSearchId );
		}

		var ajaxRequestData = {
				project_search_id : project_search_id,
				protein_1_id : protein_1_id,
				protein_2_id : protein_2_id,
				protein_1_position : protein_1_position,
				protein_2_position : protein_2_position,
				psmPeptideCutoffsForProjectSearchId : psmPeptideCutoffsForProjectSearchId_JSONString,
				peptideAnnTypeDisplayPerSearch : psmPeptideAnnTypeDisplayPerProjectSearchId_JSONString
		};
		$.ajax({
			url : contextPathJSVar + "/services/data/getCrosslinkReportedPeptides",
//			traditional: true,  //  Force traditional serialization of the data sent
//								//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
//								//   So searchIds array is passed as "searchIds=<value>" which is what Jersey expects
			data : ajaxRequestData,  // The data sent as params on the URL
			dataType : "json",
			success : function( ajaxResponseData ) {
				try {
					var responseParams = {
							ajaxResponseData : ajaxResponseData, 
							ajaxRequestData : ajaxRequestData,
							$topTRelement : $topTRelement,
							$clickedElement : $clickedElement
					};
					objectThis.loadAndInsertCrosslinkReportedPeptidesResponse( responseParams );
					$topTRelement.data( _DATA_LOADED_DATA_KEY, true );
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
			}
		});
	};
	
	///////////////////
	this.loadAndInsertCrosslinkReportedPeptidesResponse = function( params ) {
		var ajaxResponseData = params.ajaxResponseData;
		var ajaxRequestData = params.ajaxRequestData;
		var $clickedElement = params.$clickedElement;
		var show_children_if_one_row = $clickedElement.attr( "show_children_if_one_row" );
		var peptideAnnotationDisplayNameDescriptionList = ajaxResponseData.peptideAnnotationDisplayNameDescriptionList;
		var psmAnnotationDisplayNameDescriptionList = ajaxResponseData.psmAnnotationDisplayNameDescriptionList;
		var crosslink_peptides = ajaxResponseData.searchPeptideCrosslinkList;
		var $topTRelement = params.$topTRelement;
		var $crosslink_peptide_data_container = $topTRelement.find(".child_data_container_jq");
		if ( $crosslink_peptide_data_container.length === 0 ) {
			throw Error( "unable to find HTML element with class 'child_data_container_jq'" );
		}
		$crosslink_peptide_data_container.empty();
		if ( _handlebarsTemplate_crosslink_peptide_block_template === null ) {
			var handlebarsSource_crosslink_peptide_block_template = $( "#crosslink_peptide_block_template" ).html();
			if ( handlebarsSource_crosslink_peptide_block_template === undefined ) {
				throw Error( "handlebarsSource_crosslink_peptide_block_template === undefined" );
			}
			if ( handlebarsSource_crosslink_peptide_block_template === null ) {
				throw Error( "handlebarsSource_crosslink_peptide_block_template === null" );
			}
			_handlebarsTemplate_crosslink_peptide_block_template = Handlebars.compile( handlebarsSource_crosslink_peptide_block_template );
		}
		if ( _handlebarsTemplate_crosslink_peptide_data_row_entry_template === null ) {
			var handlebarsSource_crosslink_peptide_data_row_entry_template = $( "#crosslink_peptide_data_row_entry_template" ).html();
			if ( handlebarsSource_crosslink_peptide_data_row_entry_template === undefined ) {
				throw Error( "handlebarsSource_crosslink_peptide_data_row_entry_template === undefined" );
			}
			if ( handlebarsSource_crosslink_peptide_data_row_entry_template === null ) {
				throw Error( "handlebarsSource_crosslink_peptide_data_row_entry_template === null" );
			}
			_handlebarsTemplate_crosslink_peptide_data_row_entry_template = Handlebars.compile( handlebarsSource_crosslink_peptide_data_row_entry_template );
		}
		if ( _handlebarsTemplate_crosslink_peptide_child_row_entry_template === null ) {
			var handlebarsSource_crosslink_peptide_child_row_entry_template = $( "#crosslink_peptide_child_row_entry_template" ).html();
			if ( handlebarsSource_crosslink_peptide_child_row_entry_template === undefined ) {
				throw Error( "handlebarsSource_crosslink_peptide_child_row_entry_template === undefined" );
			}
			if ( handlebarsSource_crosslink_peptide_child_row_entry_template === null ) {
				throw Error( "handlebarsSource_crosslink_peptide_child_row_entry_template === null" );
			}
			_handlebarsTemplate_crosslink_peptide_child_row_entry_template = Handlebars.compile( handlebarsSource_crosslink_peptide_child_row_entry_template );
		}
		//  Search for NumberUniquePSMs being set in any row
		var showNumberNonUniquePSMs = false;
		for ( var crosslink_peptideIndex = 0; crosslink_peptideIndex < crosslink_peptides.length ; crosslink_peptideIndex++ ) {
			var crosslink_peptide = crosslink_peptides[ crosslink_peptideIndex ];
			if ( crosslink_peptide.numNonUniquePsms !== undefined && crosslink_peptide.numNonUniquePsms !== null ) {
				showNumberNonUniquePSMs = true;
				break;
			}
		}
		//  create context for header row
		var context = { 
				showNumberNonUniquePSMs : showNumberNonUniquePSMs,
				peptideAnnotationDisplayNameDescriptionList : peptideAnnotationDisplayNameDescriptionList,
				psmAnnotationDisplayNameDescriptionList : psmAnnotationDisplayNameDescriptionList
		};
		var html = _handlebarsTemplate_crosslink_peptide_block_template(context);
		var $crosslink_peptide_block_template = $(html).appendTo($crosslink_peptide_data_container);
		var crosslink_peptide_table_jq_ClassName = "crosslink_peptide_table_jq";
		var $crosslink_peptide_table_jq = $crosslink_peptide_block_template.find("." + crosslink_peptide_table_jq_ClassName );
		if ( $crosslink_peptide_table_jq.length === 0 ) {
			throw Error( "unable to find HTML element with class '" + crosslink_peptide_table_jq_ClassName + "'" );
		}
		//  Add crosslink_peptide data to the page
		for ( var crosslink_peptideIndex = 0; crosslink_peptideIndex < crosslink_peptides.length ; crosslink_peptideIndex++ ) {
			var crosslink_peptide = crosslink_peptides[ crosslink_peptideIndex ];
			//  wrap data in an object to allow adding more fields
			var context = { 
					showNumberNonUniquePSMs : showNumberNonUniquePSMs,
					data : crosslink_peptide, 
					projectSearchId : ajaxRequestData.project_search_id
					};
			var html = _handlebarsTemplate_crosslink_peptide_data_row_entry_template(context);
			var $crosslink_peptide_entry = 
				$(html).appendTo($crosslink_peptide_table_jq);
			//  Get the number of columns of the inserted row so can set the "colspan=" in the next row
			//       that holds the child data
			var $crosslink_peptide_entry__columns = $crosslink_peptide_entry.find("td");
			var crosslink_peptide_entry__numColumns = $crosslink_peptide_entry__columns.length;
			//  colSpan is used as the value for "colspan=" in the <td>
			var childRowHTML_Context = { colSpan : crosslink_peptide_entry__numColumns };
			var childRowHTML = _handlebarsTemplate_crosslink_peptide_child_row_entry_template( childRowHTML_Context );
			//  Add next row for child data
			$( childRowHTML ).appendTo($crosslink_peptide_table_jq);
			//  If only one record, click on it to show it's children
			if ( show_children_if_one_row === "true" && crosslink_peptides.length === 1 ) {
				$crosslink_peptide_entry.click();
			}
		}
		//  If the function window.linkInfoOverlayWidthResizer() exists, call it to resize the overlay
		if ( window.linkInfoOverlayWidthResizer ) {
			window.linkInfoOverlayWidthResizer();
		}
	};
};

//  Static Singleton Instance of Class
var viewCrosslinkReportedPeptidesLoadedFromWebServiceTemplate = new ViewCrosslinkReportedPeptidesLoadedFromWebServiceTemplate();
