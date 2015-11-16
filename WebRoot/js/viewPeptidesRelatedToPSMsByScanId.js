
//  viewPeptidesRelatedToPSMsByScanId.js




//Process and load data into the file  viewPeptidesRelatedToPSMsByScanId.jsp




//JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";


// Class contructor

var ViewPeptidesRelatedToPSMsByScanId = function() {

	var _handlebarsTemplate_peptides_related_to_psm_block_template = null;
	var _handlebarsTemplate_peptides_related_to_psm_row_entry_template = null;
	var _handlebarsTemplate_peptides_related_to_psm_child_row_template = null;
	
	
	// ////////////
	
	this.init = function( ) {

		var objectThis = this;
		
		// /////////////////////////////////////////////////////////////////////////

		// / Attach Overlay Click handlers


		var $view_data_related_to_psm_data_overlay_X_for_exit_overlay = $(".view-data-related-to-psm-data-overlay-X-for-exit-overlay");
		
		$view_data_related_to_psm_data_overlay_X_for_exit_overlay.click( function( eventObject ) {

			objectThis.closeOverlay();
		} );

		var $view_data_related_to_psm_data_modal_dialog_overlay_background = $("#view_data_related_to_psm_data_modal_dialog_overlay_background");
		
		$view_data_related_to_psm_data_modal_dialog_overlay_background.click( function( eventObject ) {

			objectThis.closeOverlay();
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
	
	this.openOverlayForPeptidesRelatedToPSMsByScanId = function( params ) {

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
	};






	// /////////

	this.loadAndInsertPeptides = function( params ) {

		var objectThis = this;

		var $clickedElement = params.$clickedElement;


		var project_id = $clickedElement.attr( "project_id" );
		var search_id = $clickedElement.attr( "search_id" );
		var psm_id = $clickedElement.attr( "psm_id" );
		var scan_id = $clickedElement.attr( "scan_id" );
		var peptide_q_value_cutoff = $clickedElement.attr( "peptide_q_value_cutoff" );
		var psm_q_value_cutoff = $clickedElement.attr( "psm_q_value_cutoff" );
		
		// Convert all attributes to empty string if null or undefined
		if ( ! search_id ) {
			search_id = "";
		}
		if ( ! project_id ) {
			project_id = "";
		}
		if ( ! psm_id ) {
			psm_id = "";
		}
		if ( ! scan_id ) {
			scan_id = "";
		}
		if ( ! peptide_q_value_cutoff ) {
			peptide_q_value_cutoff = "";
		}
		if ( ! psm_q_value_cutoff ) {
			psm_q_value_cutoff = "";
		}



		var ajaxRequestData = {

				search_id : search_id,
				project_id : project_id,
				psm_id : psm_id,
				scan_id : scan_id,
				peptide_q_value_cutoff : peptide_q_value_cutoff,
				psm_q_value_cutoff : psm_q_value_cutoff
		};


		$.ajax({
			url : contextPathJSVar + "/services/reportedPeptidesRelatedToPSMService/get",

// traditional: true, // Force traditional serialization of the data sent
// // One thing this means is that arrays are sent as the object property
// instead of object property followed by "[]".
// // So searchIds array is passed as "searchIds=<value>" which is what Jersey
// expects

			data : ajaxRequestData,  // The data sent as params on the URL
			dataType : "json",

			success : function( ajaxResponseData ) {

				var responseParams = {
						ajaxResponseData : ajaxResponseData, 
						ajaxRequestData : ajaxRequestData,
						$clickedElement : $clickedElement
				};

				objectThis.loadAndInsertPeptidesResponse( responseParams );
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


		var reportedPeptides = ajaxResponseData;

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



		// Search for qvalue being set in any row

		var qvalueSetAnyRows = false;

		for ( var reportedPeptideIndex = 0; reportedPeptideIndex < reportedPeptides.length ; reportedPeptideIndex++ ) {

			var reportedPeptide = reportedPeptides[ reportedPeptideIndex ];

			if ( reportedPeptide.qvalue !== undefined && reportedPeptide.qvalue !== null ) {

				qvalueSetAnyRows = true;
				break;
			}
		}


		// create context for header row
		var context = { qvalueSetAnyRows : qvalueSetAnyRows };

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
					searchId : ajaxRequestData.search_id, 
					peptide_q_value_cutoff : ajaxRequestData.peptide_q_value_cutoff, 
					psm_q_value_cutoff : ajaxRequestData.psm_q_value_cutoff, 
					qvalueSetAnyRows : qvalueSetAnyRows 
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

		var view_data_related_to_psm_data_overlay_div__Width = view_data_related_to_psm_data_overlay_div__Width + 40;

		$("#view_data_related_to_psm_data_overlay_div").css( {"width": view_data_related_to_psm_data_overlay_div__Width } );
	};


};



// Static Singleton Instance of Class

var viewPeptidesRelatedToPSMsByScanId = new ViewPeptidesRelatedToPSMsByScanId();


// /////////////////////////////////////////////

$(document).ready(function()  { 

	viewPeptidesRelatedToPSMsByScanId.init();

});
