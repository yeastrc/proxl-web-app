
//   viewLooplinkReportedPeptidesLoadedFromWebServiceTemplate.js

//   Process and load data into the file viewLooplinkReportedPeptidesLoadedFromWebServiceTemplateFragment.jsp

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

//  require full Handlebars since compiling templates
const Handlebars = require('handlebars');


//  For showing Data for links (Drilldown) (Called by HTML onclick):
import { viewPsmsLoadedFromWebServiceTemplate } from 'page_js/data_pages/project_search_ids_driven_pages/common/viewPsmsLoadedFromWebServiceTemplate.js';

//   Class contructor

var ViewLooplinkReportedPeptidesLoadedFromWebServiceTemplate = function() {

	var _DATA_LOADED_DATA_KEY = "dataLoaded";
	var _handlebarsTemplate_looplink_peptide_block_template = null;
	var _handlebarsTemplate_looplink_peptide_data_row_entry_template = null;
	var _handlebarsTemplate_looplink_peptide_child_row_entry_template = null;
	
	var _excludeLinksWith_Root =  null;
	var _psmPeptideAnnTypeIdDisplay = null;
	var _psmPeptideCutoffsRootObject = null;

	//////////////
	this.setPsmPeptideCriteria = function( psmPeptideCutoffsRootObject ) {
		_psmPeptideCutoffsRootObject = psmPeptideCutoffsRootObject;
	};

	//////////////
	this.setPsmPeptideAnnTypeIdDisplay = function( psmPeptideAnnTypeIdDisplay ) {
		_psmPeptideAnnTypeIdDisplay = psmPeptideAnnTypeIdDisplay;
	};
	
	//////////////
	this.setExcludeLinksWith_Root = function( excludeLinksWith_Root ) {
		_excludeLinksWith_Root = excludeLinksWith_Root;
	};
	
	// ////////////
	this.showHideLooplinkReportedPeptides = function( params ) {
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
				this.loadAndInsertLooplinkReportedPeptidesIfNeeded( { $topTRelement : $itemToToggle, $clickedElement : $clickedElement } );
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

			this.loadAndInsertLooplinkReportedPeptidesIfNeeded( { 
				$topTRelement : $itemToToggle, 
				$clickedElement : $htmlElement,
				reloadData : true } );

		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
	
	
	
	////////////////////////
	this.loadAndInsertLooplinkReportedPeptidesIfNeeded = function( params ) {
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
		var protein_id = $clickedElement.attr( "data-protein_id" );
		var protein_position_1 = $clickedElement.attr( "data-protein_position_1" );
		var protein_position_2 = $clickedElement.attr( "data-protein_position_2" );
		//  Convert all attributes to empty string if null or undefined
		if ( ! project_search_id ) {
			project_search_id = "";
		}
		if ( ! protein_id ) {
			protein_id = "";
		}
		if ( ! protein_position_1 ) {
			protein_position_1 = "";
		}
		if ( ! protein_position_2 ) {
			protein_position_2 = "";
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

		var excludeLinksWith_Root_JSONString = undefined;
		if ( _excludeLinksWith_Root ) {
			excludeLinksWith_Root_JSONString = JSON.stringify( _excludeLinksWith_Root );
		}

		var ajaxRequestData = {
				project_search_id : project_search_id,
				protein_id : protein_id,
				protein_position_1 : protein_position_1,
				protein_position_2 : protein_position_2,
				psmPeptideCutoffsForProjectSearchId : psmPeptideCutoffsForProjectSearchId_JSONString,
				peptideAnnTypeDisplayPerSearch : psmPeptideAnnTypeDisplayPerProjectSearchId_JSONString,
				excludeLinksWith_Root : excludeLinksWith_Root_JSONString
		};
		$.ajax({
			url : "services/data/getLooplinkReportedPeptides",
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
					objectThis.loadAndInsertLooplinkReportedPeptidesResponse( responseParams );
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
	
	this.loadAndInsertLooplinkReportedPeptidesResponse = function( params ) {
		var ajaxResponseData = params.ajaxResponseData;
		var ajaxRequestData = params.ajaxRequestData;
		var $topTRelement = params.$topTRelement;
		var $clickedElement = params.$clickedElement;
		var show_children_if_one_row = $clickedElement.attr( "show_children_if_one_row" );
		var peptideAnnotationDisplayNameDescriptionList = ajaxResponseData.peptideAnnotationDisplayNameDescriptionList;
		var psmAnnotationDisplayNameDescriptionList = ajaxResponseData.psmAnnotationDisplayNameDescriptionList;
		var looplink_peptides = ajaxResponseData.searchPeptideLooplinkList;
		var $looplink_peptide_data_container = $topTRelement.find(".child_data_container_jq");
		if ( $looplink_peptide_data_container.length === 0 ) {
			throw Error( "unable to find HTML element with class 'child_data_container_jq'" );
		}
		$looplink_peptide_data_container.empty();
		if ( _handlebarsTemplate_looplink_peptide_block_template === null ) {
			var handlebarsSource_looplink_peptide_block_template = $( "#looplink_peptide_block_template" ).html();
			if ( handlebarsSource_looplink_peptide_block_template === undefined ) {
				throw Error( "handlebarsSource_looplink_peptide_block_template === undefined" );
			}
			if ( handlebarsSource_looplink_peptide_block_template === null ) {
				throw Error( "handlebarsSource_looplink_peptide_block_template === null" );
			}
			_handlebarsTemplate_looplink_peptide_block_template = Handlebars.compile( handlebarsSource_looplink_peptide_block_template );
		}
		if ( _handlebarsTemplate_looplink_peptide_data_row_entry_template === null ) {
			var handlebarsSource_looplink_peptide_data_row_entry_template = $( "#looplink_peptide_data_row_entry_template" ).html();
			if ( handlebarsSource_looplink_peptide_data_row_entry_template === undefined ) {
				throw Error( "handlebarsSource_looplink_peptide_data_row_entry_template === undefined" );
			}
			if ( handlebarsSource_looplink_peptide_data_row_entry_template === null ) {
				throw Error( "handlebarsSource_looplink_peptide_data_row_entry_template === null" );
			}
			_handlebarsTemplate_looplink_peptide_data_row_entry_template = Handlebars.compile( handlebarsSource_looplink_peptide_data_row_entry_template );
		}
		if ( _handlebarsTemplate_looplink_peptide_child_row_entry_template === null ) {
			if ( _handlebarsTemplate_looplink_peptide_child_row_entry_template === null ) {
				var handlebarsSource_looplink_peptide_child_row_entry_template = $( "#looplink_peptide_child_row_entry_template" ).html();
				if ( handlebarsSource_looplink_peptide_child_row_entry_template === undefined ) {
					throw Error( "handlebarsSource_looplink_peptide_child_row_entry_template === undefined" );
				}
				if ( handlebarsSource_looplink_peptide_child_row_entry_template === null ) {
					throw Error( "handlebarsSource_looplink_peptide_child_row_entry_template === null" );
				}
				_handlebarsTemplate_looplink_peptide_child_row_entry_template = Handlebars.compile( handlebarsSource_looplink_peptide_child_row_entry_template );
			}
		}
		//  Search for NumberUniquePSMs being set in any row
		var showNumberNonUniquePSMs = false;
		for ( var looplink_peptideIndex = 0; looplink_peptideIndex < looplink_peptides.length ; looplink_peptideIndex++ ) {
			var looplink_peptide = looplink_peptides[ looplink_peptideIndex ];
			if ( looplink_peptide.numNonUniquePsms !== undefined && looplink_peptide.numNonUniquePsms !== null ) {
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
		var html = _handlebarsTemplate_looplink_peptide_block_template(context);
		var $looplink_peptide_block_template = $(html).appendTo($looplink_peptide_data_container);
		var looplink_peptide_table_jq_ClassName = "looplink_peptide_table_jq";
		var $looplink_peptide_table_jq = $looplink_peptide_block_template.find("." + looplink_peptide_table_jq_ClassName );
		if ( $looplink_peptide_table_jq.length === 0 ) {
			throw Error( "unable to find HTML element with class '" + looplink_peptide_table_jq_ClassName + "'" );
		}
		//  Add looplink_peptide data to the page
		for ( var looplink_peptideIndex = 0; looplink_peptideIndex < looplink_peptides.length ; looplink_peptideIndex++ ) {
			var looplink_peptide = looplink_peptides[ looplink_peptideIndex ];
			//  wrap data in an object to allow adding more fields
			var context = { 
					showNumberNonUniquePSMs : showNumberNonUniquePSMs,
					data : looplink_peptide, 
					projectSearchId : ajaxRequestData.project_search_id
					};
			var html = _handlebarsTemplate_looplink_peptide_data_row_entry_template(context);
			var $looplink_peptide_entry = 
				$(html).appendTo($looplink_peptide_table_jq);
			//  Get the number of columns of the inserted row so can set the "colspan=" in the next row
			//       that holds the child data
			var $looplink_peptide_entry__columns = $looplink_peptide_entry.find("td");
			var looplink_peptide_entry__numColumns = $looplink_peptide_entry__columns.length;
			//  colSpan is used as the value for "colspan=" in the <td>
			var childRowHTML_Context = { colSpan : looplink_peptide_entry__numColumns };
			var childRowHTML = _handlebarsTemplate_looplink_peptide_child_row_entry_template( childRowHTML_Context );
			//  Add next row for child data
			$( childRowHTML ).appendTo($looplink_peptide_table_jq);	
			//  If only one record, click on it to show it's children
			if ( show_children_if_one_row === "true" && looplink_peptides.length === 1 ) {
				$looplink_peptide_entry.click();
			}
		}

		setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else
			$looplink_peptide_table_jq.tablesorter(); // gets exception if there are no data rows
		},10);
		
		//  If the function window.linkInfoOverlayWidthResizer() exists, call it to resize the overlay
		if ( window.linkInfoOverlayWidthResizer ) {
			window.linkInfoOverlayWidthResizer();
		}
	};
};

//  Static Singleton Instance of Class
var viewLooplinkReportedPeptidesLoadedFromWebServiceTemplate = new ViewLooplinkReportedPeptidesLoadedFromWebServiceTemplate();

window.viewLooplinkReportedPeptidesLoadedFromWebServiceTemplate = viewLooplinkReportedPeptidesLoadedFromWebServiceTemplate;

export { viewLooplinkReportedPeptidesLoadedFromWebServiceTemplate }
