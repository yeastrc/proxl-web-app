
//  viewPeptidesRelatedToPSMsByScanId.js

//Process and load data into the file  viewPeptidesRelatedToPSMsByScanId.jsp

//JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

// Class contructor

var ViewPeptidesRelatedToPSMsByScanId = function() {

	var _handlebarsTemplate_peptides_related_to_psm_block_template = null;
	var _handlebarsTemplate_peptides_related_to_psm_row_entry_template = null;
	var _handlebarsTemplate_peptides_related_to_psm_child_row_template = null;
	
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
	this.init = function( ) {
		var objectThis = this;
		// /////////////////////////////////////////////////////////////////////////
		// / Attach Overlay Click handlers
		var $view_data_related_to_psm_data_overlay_X_for_exit_overlay = $(".view-data-related-to-psm-data-overlay-X-for-exit-overlay");
		$view_data_related_to_psm_data_overlay_X_for_exit_overlay.click( function( eventObject ) {
			try {
				objectThis.closeOverlay();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		} );
		var $view_data_related_to_psm_data_modal_dialog_overlay_background = $("#view_data_related_to_psm_data_modal_dialog_overlay_background");
		$view_data_related_to_psm_data_modal_dialog_overlay_background.click( function( eventObject ) {
			try {
				objectThis.closeOverlay();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		} );
	};
	
	// /////////
	this.closeOverlay = function(  ) {
		var $view_data_related_to_psm_data_modal_dialog_overlay_background = $("#view_data_related_to_psm_data_modal_dialog_overlay_background");
		$view_data_related_to_psm_data_modal_dialog_overlay_background.hide();
		var $view_data_related_to_psm_data_overlay_div = $("#view_data_related_to_psm_data_overlay_div");
		$view_data_related_to_psm_data_overlay_div.hide();
	};
	
	// /////////
	//   Called by "onclick" on HTML element
	this.openOverlayForPeptidesRelatedToPSMsByScanId = function( params ) {
		try {
			var clickedElement = params.clickedElement;
			var $clickedElement = $( clickedElement );
			var $view_data_related_to_psm_data_modal_dialog_overlay_background = $("#view_data_related_to_psm_data_modal_dialog_overlay_background");
			$view_data_related_to_psm_data_modal_dialog_overlay_background.show();
			var $view_data_related_to_psm_data_overlay_div = $("#view_data_related_to_psm_data_overlay_div");
			$view_data_related_to_psm_data_overlay_div.show();
			// Close any open Lorikeet Overlay
			closeLorikeetOverlay();
			// Adjust the overlay positon to be within the viewport
			var scrollTopWindow = $(window).scrollTop();
			if ( scrollTopWindow > 0 ) {
				// User has scrolled down
				var overlayTop = scrollTopWindow + 10;
				$view_data_related_to_psm_data_overlay_div.css( { top: overlayTop + "px" } );
			} else {
				$view_data_related_to_psm_data_overlay_div.css( { top: "10px" } );
			}
			this.loadAndInsertPeptides( { $clickedElement : $clickedElement } );
			return false;  // does not stop bubbling of click event
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
	
	// /////////
	this.loadAndInsertPeptides = function( params ) {
		var objectThis = this;
		var $clickedElement = params.$clickedElement;
		var search_id = $clickedElement.attr( "search_id" );
		var psm_id = $clickedElement.attr( "psm_id" );
		var scan_id = $clickedElement.attr( "scan_id" );
		// Convert all attributes to empty string if null or undefined
		if ( ! search_id ) {
			search_id = "";
		}
		if ( ! psm_id ) {
			psm_id = "";
		}
		if ( ! scan_id ) {
			scan_id = "";
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
		var psmPeptideCutoffsForSearchId = _psmPeptideCutoffsRootObject.searches[ search_id ];
		if ( psmPeptideCutoffsForSearchId === undefined || psmPeptideCutoffsForSearchId === null ) {
			psmPeptideCutoffsForSearchId = {};
//			throw "Getting data.  Unable to get cutoff data for search id: " + search_id;
		}
		var psmPeptideCutoffsForSearchId_JSONString = JSON.stringify( psmPeptideCutoffsForSearchId );
		
		var psmPeptideAnnTypeDisplayPerSearchId_JSONString = null;
		if ( _psmPeptideAnnTypeIdDisplay ) {
			var psmPeptideAnnTypeIdDisplayForSearchId = _psmPeptideAnnTypeIdDisplay.searches[ search_id ];
			if ( psmPeptideAnnTypeIdDisplayForSearchId === undefined || psmPeptideAnnTypeIdDisplayForSearchId === null ) {
//				psmPeptideAnnTypeIdDisplayForSearchId = {};
				throw Error( "Getting data.  Unable to get ann type display data for search id: " + search_id );
			}
			psmPeptideAnnTypeDisplayPerSearchId_JSONString = JSON.stringify( psmPeptideAnnTypeIdDisplayForSearchId );
		}
		
		var ajaxRequestData = {
				search_id : search_id,
				psm_id : psm_id,
				scan_id : scan_id,
				psmPeptideCutoffsForSearchId : psmPeptideCutoffsForSearchId_JSONString,
				peptideAnnTypeDisplayPerSearch : psmPeptideAnnTypeDisplayPerSearchId_JSONString
		};
		$.ajax({
			url : contextPathJSVar + "/services/reportedPeptidesRelatedToPSMService/get",
			// traditional: true, // Force traditional serialization of the data sent
			// // One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
			// // So searchIds array is passed as "searchIds=<value>" which is what Jersey expects
			data : ajaxRequestData,  // The data sent as params on the URL
			dataType : "json",
			success : function( ajaxResponseData ) {
				try {
					var responseParams = {
							ajaxResponseData : ajaxResponseData, 
							ajaxRequestData : ajaxRequestData,
							$clickedElement : $clickedElement
					};
					objectThis.loadAndInsertPeptidesResponse( responseParams );
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
	
	// /////////
	this.loadAndInsertPeptidesResponse = function( params ) {
		var ajaxResponseData = params.ajaxResponseData;
		var ajaxRequestData = params.ajaxRequestData;
		var $clickedElement = params.$clickedElement;
		var peptideAnnotationDisplayNameDescriptionList = ajaxResponseData.peptideAnnotationDisplayNameDescriptionList;
		var psmAnnotationDisplayNameDescriptionList = ajaxResponseData.psmAnnotationDisplayNameDescriptionList;
		var reportedPeptides = ajaxResponseData.webReportedPeptideWebserviceWrapperList;
		var initial_reported_peptide_id = $clickedElement.attr( "initial_reported_peptide_id" );
		initial_reported_peptide_id = parseInt( initial_reported_peptide_id, 10 );
		if ( isNaN( initial_reported_peptide_id ) ) {
			initial_reported_peptide_id = null;
		}
		var $reported_peptide_data_container = $("#view_data_related_to_psm_data_overlay_data_container");
		if ( $reported_peptide_data_container.length === 0 ) {
			throw "unable to find HTML element with id 'view_data_related_to_psm_data_overlay_data_container'";
		}
		$reported_peptide_data_container.empty();
		if ( _handlebarsTemplate_peptides_related_to_psm_block_template === null ) {
			var handlebarsSource_peptides_related_to_psm_block_template = $( "#peptides_related_to_psm_block_template" ).html();
			if ( handlebarsSource_peptides_related_to_psm_block_template === undefined ) {
				throw "handlebarsSource_peptides_related_to_psm_block_template === undefined";
			}
			if ( handlebarsSource_peptides_related_to_psm_block_template === null ) {
				throw "handlebarsSource_peptides_related_to_psm_block_template === null";
			}
			_handlebarsTemplate_peptides_related_to_psm_block_template = Handlebars.compile( handlebarsSource_peptides_related_to_psm_block_template );
		}
		if ( _handlebarsTemplate_peptides_related_to_psm_row_entry_template === null ) {
			var handlebarsSource_peptides_related_to_psm_row_entry_template = $( "#peptides_related_to_psm_row_entry_template" ).html();
			if ( handlebarsSource_peptides_related_to_psm_row_entry_template === undefined ) {
				throw "handlebarsSource_peptides_related_to_psm_row_entry_template === undefined";
			}
			if ( handlebarsSource_peptides_related_to_psm_row_entry_template === null ) {
				throw "handlebarsSource_peptides_related_to_psm_row_entry_template === null";
			}
			_handlebarsTemplate_peptides_related_to_psm_row_entry_template = Handlebars.compile( handlebarsSource_peptides_related_to_psm_row_entry_template );
		}
		if ( _handlebarsTemplate_peptides_related_to_psm_child_row_template === null ) {
			var handlebarsSource_peptides_related_to_psm_child_row_template = $( "#peptides_related_to_psm_child_row_template" ).html();
			if ( handlebarsSource_peptides_related_to_psm_child_row_template === undefined ) {
				throw "handlebarsSource_peptides_related_to_psm_child_row_template === undefined";
			}
			if ( handlebarsSource_peptides_related_to_psm_child_row_template === null ) {
				throw "handlebarsSource_peptides_related_to_psm_child_row_template === null";
			}
			_handlebarsTemplate_peptides_related_to_psm_child_row_template = Handlebars.compile( handlebarsSource_peptides_related_to_psm_child_row_template );
		}
		// create context for header row
		var context = { 
				peptideAnnotationDisplayNameDescriptionList : peptideAnnotationDisplayNameDescriptionList,
				psmAnnotationDisplayNameDescriptionList : psmAnnotationDisplayNameDescriptionList
		};
		var html = _handlebarsTemplate_peptides_related_to_psm_block_template(context);
		var $peptides_related_to_psm_block_template = $(html).appendTo($reported_peptide_data_container);
		var peptides_related_to_psm_table_jq_ClassName = "peptides_related_to_psm_table_jq";
		var $peptides_related_to_psm_table_jq = $peptides_related_to_psm_block_template.find("." + peptides_related_to_psm_table_jq_ClassName );
// var $peptides_related_to_psm_table_jq =
// $reported_peptide_data_container.find(".peptides_related_to_psm_table_jq");
		if ( $peptides_related_to_psm_table_jq.length === 0 ) {
			throw "unable to find HTML element with class '" + peptides_related_to_psm_table_jq_ClassName + "'";
		}
		// Add reported_peptide data to the page
		for ( var reportedPeptideIndex = 0; reportedPeptideIndex < reportedPeptides.length ; reportedPeptideIndex++ ) {
			var reportedPeptide = reportedPeptides[ reportedPeptideIndex ];
			// wrap data in an object to allow adding more fields
			var context = { 
					data : reportedPeptide, 
					initial_scan_id : ajaxRequestData.scan_id,
					searchId : ajaxRequestData.search_id
			};
			if ( initial_reported_peptide_id !== undefined
					&& initial_reported_peptide_id !== null 
					&& reportedPeptide.reportedPeptide_Id === initial_reported_peptide_id ) {
				context.scanIdMatchesInitialScanId = true;
			}
			var html = _handlebarsTemplate_peptides_related_to_psm_row_entry_template(context);
			var $reported_peptide_entry = 
				$(html).appendTo($peptides_related_to_psm_table_jq);
			// Get the number of columns of the inserted row so can set the
			// "colspan=" in the next row
			// that holds the child data
			var $reported_peptide_entry__columns = $reported_peptide_entry.find("td");
			var reported_peptide_entry__numColumns = $reported_peptide_entry__columns.length;
			// colSpan is used as the value for "colspan=" in the <td>
			var childRowHTML_Context = { colSpan : reported_peptide_entry__numColumns };
			var childRowHTML = _handlebarsTemplate_peptides_related_to_psm_child_row_template( childRowHTML_Context );
			// Add next row for child data
			$( childRowHTML ).appendTo($peptides_related_to_psm_table_jq);
		}
		this.overlayWidthResizer( { $clickedElement : $clickedElement });
	};
	
	// ////////////////////
	this.overlayWidthResizer = function( params ) {
// var $clickedElement = params.$clickedElement;
		var view_data_related_to_psm_data_overlay_div__Width = 0;
// var $top_data_table_jq = $clickedElement.closest(".top_data_table_jq");
//		
// if ( $top_data_table_jq.length > 0 ) {
//		
// var top_data_table__Width = $top_data_table_jq.outerWidth( true /* [
// includeMargin ] */ );
//
// view_data_related_to_psm_data_overlay_div__Width = top_data_table__Width;
//
// }
		var $peptides_related_to_psm_table = $("#peptides_related_to_psm_table");
		var peptides_related_to_psm_table_width = $peptides_related_to_psm_table.outerWidth( true /* [ includeMargin ] */ );
		if ( view_data_related_to_psm_data_overlay_div__Width < peptides_related_to_psm_table_width ) {
			view_data_related_to_psm_data_overlay_div__Width = peptides_related_to_psm_table_width;
		}
// Adjust width of link info overlay to be 40 pixels wider than the link info
// table
		view_data_related_to_psm_data_overlay_div__Width += 40;
		$("#view_data_related_to_psm_data_overlay_div").css( {"width": view_data_related_to_psm_data_overlay_div__Width } );
	};
};

// Static Singleton Instance of Class
var viewPeptidesRelatedToPSMsByScanId = new ViewPeptidesRelatedToPSMsByScanId();

// /////////////////////////////////////////////
$(document).ready(function()  { 
	try {
		viewPeptidesRelatedToPSMsByScanId.init();
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
});
