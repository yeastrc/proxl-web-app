
//  crosslink-image-viewer-region-selections.js

//  Javascript for the viewMergedImage.jsp page


//  This file holds javascript for managing the "Protein Bar Region Selections" Overlay.   



//  JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";


//  The following global variables from "crosslink-image-viewer.js" are used in this file:

//  _proteinNames



//Constructor

var ProteinBarRegionSelectionsOverlayCode = function( params ) {

	var imageProteinBarDataManager = params.imageProteinBarDataManager;
	
	this.imageProteinBarDataManager = imageProteinBarDataManager;

};

var ProteinBarRegionSelectionsOverlayCodeContructor = function( params ) { 

	return new ProteinBarRegionSelectionsOverlayCode( params );
};


ProteinBarRegionSelectionsOverlayCode.prototype.init = function( ) {

	var objectThis = this;

	$("#view_protein_bar_highlighting_overlay_X_for_exit_overlay").click( function( eventObject ) {

		try {
			objectThis.closeOverlay();
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	} );
	

	$("#view_protein_bar_highlighting_overlay_protein_bars_cancel_button").click( function( eventObject ) {
		try {
			objectThis.closeOverlay();
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	} );
	

	$("#view_protein_bar_highlighting_overlay_protein_bars_reset_button").click( function( eventObject ) {

		try {
			objectThis._populate();
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	} );
	

	$("#view_protein_bar_highlighting_overlay_protein_bars_clear_all_button").click( function( eventObject ) {

		try {
			objectThis._populate( { clearAll : true } );
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	} );
	
	

	$("#view_protein_bar_highlighting_overlay_protein_bars_save_button").click( function( eventObject ) {

		try {	
			objectThis.save();
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	} );

	
	//  Compile Handlebars Templates

	var singleBarTemplate_handlebarsSource = $( "#view_protein_bar_highlighting_overlay_single_bar_template" ).text();

	if ( singleBarTemplate_handlebarsSource === undefined ) {
		throw Error( "singleBarTemplate_handlebarsSource === undefined" );
	}
	if ( singleBarTemplate_handlebarsSource === null ) {
		throw Error( "singleBarTemplate_handlebarsSource === null" );
	}

	this._singleBarTemplate_HandlebarsTemplate = Handlebars.compile( singleBarTemplate_handlebarsSource );


	
	var singleBarRegionTemplate_handlebarsSource = $( "#view_protein_bar_highlighting_overlay_bar_region_template" ).text();

	if ( singleBarRegionTemplate_handlebarsSource === undefined ) {
		throw Error( "singleBarRegionTemplate_handlebarsSource === undefined" );
	}
	if ( singleBarRegionTemplate_handlebarsSource === null ) {
		throw Error( "singleBarRegionTemplate_handlebarsSource === null" );
	}
	
	this._singleBarRegionTemplate_HandlebarsTemplate = Handlebars.compile( singleBarRegionTemplate_handlebarsSource );


};

///

ProteinBarRegionSelectionsOverlayCode.prototype.closeOverlay = function( ) {

	var $view_protein_bar_highlighting_modal_dialog_overlay_background = $("#view_protein_bar_highlighting_modal_dialog_overlay_background");

	$view_protein_bar_highlighting_modal_dialog_overlay_background.hide();

	var $view_protein_bar_highlighting_overlay_div = $("#view_protein_bar_highlighting_overlay_div");

	$view_protein_bar_highlighting_overlay_div.hide();
};


//  Open

ProteinBarRegionSelectionsOverlayCode.prototype.openOverlay = function() {

	var $view_protein_bar_highlighting_modal_dialog_overlay_background = $("#view_protein_bar_highlighting_modal_dialog_overlay_background");

	$view_protein_bar_highlighting_modal_dialog_overlay_background.show();

	var $view_protein_bar_highlighting_overlay_div = $("#view_protein_bar_highlighting_overlay_div");

	$view_protein_bar_highlighting_overlay_div.show();

	this._populate();
};

//  Populate

ProteinBarRegionSelectionsOverlayCode.prototype._populate = function( params ) {
	
	var objectThis = this;
	
	var clearAll = false;
	
	if ( params ) {
		
		clearAll = params.clearAll;
	}
	

	var $view_protein_bar_highlighting_overlay_protein_bars_data_div = $("#view_protein_bar_highlighting_overlay_protein_bars_data_div");
	$view_protein_bar_highlighting_overlay_protein_bars_data_div.empty();

	//  Special case of no protein bars selected
	
	var noProteinBarsSelected = true;
	
	var isAnyProteinBarsHighlighted = this.imageProteinBarDataManager.isAnyProteinBarsHighlighted();
	
	if ( isAnyProteinBarsHighlighted ) {
		
		noProteinBarsSelected = false;
	}
	
	
	for( var i = 0; i < _indexManager.getProteinArray().length; i++ ) {
		
		var uid = _indexManager.getProteinArray()[ i ][ 'uid' ];

		var imageProteinBarDataItem = this.imageProteinBarDataManager.getAllItems()[ uid ];
		if( !imageProteinBarDataItem ) {
			console.log( "WARNING: Have no entry in protein bar data manager for uid:" + uid );
			console.log( "Adding empty entry." );
			
			this.imageProteinBarDataManager.addEntry( uid );			
		}
		
		
		var proteinId = imageProteinBarDataItem.getProteinId();

		var proteinName = _proteinNames[ proteinId ];

		var singleBarContext = {
				uid : uid,
				proteinId : proteinId,
				proteinName : proteinName,
				proteinLength : imageProteinBarDataItem.getProteinLength()
		};

		var singleBarHtml = this._singleBarTemplate_HandlebarsTemplate( singleBarContext );

		var $singleBarHtml = $( singleBarHtml ).appendTo( $view_protein_bar_highlighting_overlay_protein_bars_data_div );

		//  Hide protein divider after last entry
		
		if ( i === ( _indexManager.getProteinArray().length - 1 ) ) {

			var $protein_divider_jq = $singleBarHtml.find(".protein_divider_jq");
			
			$protein_divider_jq.hide();
		}

		var $add_region_jq =  $singleBarHtml.find(".add_region_jq");
		
		$add_region_jq.click( function( eventObject ) {

			try {
				objectThis.addRegion( { clickedThis : this } );
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		} );
		
		

		var proteinBarHighlightedRegionsArray = imageProteinBarDataItem.getProteinBarHighlightedRegionsArray();


		var $whole_protein_bar_selected_checkbox_jq = $singleBarHtml.find(".whole_protein_bar_selected_checkbox_jq");

		$whole_protein_bar_selected_checkbox_jq.click( function( eventObject ) {

			try {
				objectThis.clickSelectWholeProteinBarProcessor( { clickedThis : this } );
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		} );

		if ( ( noProteinBarsSelected || clearAll ) || ( ( imageProteinBarDataItem.isAllOfProteinBarHighlighted() ) ) ) {

			$whole_protein_bar_selected_checkbox_jq.prop( "checked", true );

			objectThis.clickSelectWholeProteinBarProcessor( { $this : $whole_protein_bar_selected_checkbox_jq } );
		}

		if ( ( ! clearAll ) && (proteinBarHighlightedRegionsArray && proteinBarHighlightedRegionsArray.length > 0 ) ) {

			var $regions_items_block_jq = $singleBarHtml.find(".regions_items_block_jq");

			for ( var proteinBarHighlightedRegionsArrayIndex = 0; proteinBarHighlightedRegionsArrayIndex < proteinBarHighlightedRegionsArray.length; proteinBarHighlightedRegionsArrayIndex++ ) {

				var proteinBarHighlightedRegionsEntry = proteinBarHighlightedRegionsArray[ proteinBarHighlightedRegionsArrayIndex ];

				var singleBarRegionContext = {

						start : proteinBarHighlightedRegionsEntry.s,
						end : proteinBarHighlightedRegionsEntry.e

				};

				this._appendProteinBarHighlightedOverlayRegion( { $regions_items_block_jq : $regions_items_block_jq, singleBarRegionContext : singleBarRegionContext } );
			}

		}


	}

};

ProteinBarRegionSelectionsOverlayCode.prototype._appendProteinBarHighlightedOverlayRegion = function( params ) {

	var $regions_items_block_jq = params.$regions_items_block_jq;
	var singleBarRegionContext = params.singleBarRegionContext;
	

	var singleBarRegionHtml = this._singleBarRegionTemplate_HandlebarsTemplate( singleBarRegionContext );

	var $singleBarRegionHtml = $( singleBarRegionHtml ).appendTo( $regions_items_block_jq );

	var $view_protein_bar_highlighting_region_remove_button_jq = $singleBarRegionHtml.find(".view_protein_bar_highlighting_region_remove_button_jq");

	$view_protein_bar_highlighting_region_remove_button_jq.click( function( eventObject ) {

		try {
			var $clickedThis = $( this );

			var $bar_region_jq = $clickedThis.closest(".bar_region_jq");

			$bar_region_jq.remove();
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	} );

};


ProteinBarRegionSelectionsOverlayCode.prototype.addRegion = function( params ) {

	var clickedThis = params.clickedThis;
	var $clickedThis = $( clickedThis );



	var singleBarRegionTemplate_handlebarsSource = $( "#view_protein_bar_highlighting_overlay_bar_region_template" ).text();

	if ( singleBarRegionTemplate_handlebarsSource === undefined ) {
		throw Error( "singleBarRegionTemplate_handlebarsSource === undefined" );
	}
	if ( singleBarRegionTemplate_handlebarsSource === null ) {
		throw Error( "singleBarRegionTemplate_handlebarsSource === null" );
	}

	var $regions_block_jq = $clickedThis.closest(".regions_block_jq");
	var $regions_items_block_jq =  $regions_block_jq.find(".regions_items_block_jq");

	this._appendProteinBarHighlightedOverlayRegion( { $regions_items_block_jq : $regions_items_block_jq, singleBarRegionContext : {} } );



};


ProteinBarRegionSelectionsOverlayCode.prototype.clickSelectWholeProteinBarProcessor = function( params ) {

	var clickedThis = params.clickedThis;
	var $clickedThis = params.$this;

	if ( ! $clickedThis ) {

		$clickedThis = $( clickedThis );
	}

	var $single_protein_bar_block_jq = $clickedThis.closest(".single_protein_bar_block_jq");

	var $regions_block_jq = $single_protein_bar_block_jq.find(".regions_block_jq");

	if ( $clickedThis.prop( "checked" ) ) {

		$regions_block_jq.hide();
	} else {

		$regions_block_jq.show();
	}
};


ProteinBarRegionSelectionsOverlayCode.prototype.save = function( params ) {

	var objectThis = this;
	
	
	var dataValidationError = false;
	
	
	var selectionDataUpdates = [];
	
	
	var allProteinBarsSelectWholeProteinBar = true;
	
	
	var $view_protein_bar_highlighting_overlay_protein_bars_data_div = $("#view_protein_bar_highlighting_overlay_protein_bars_data_div");
	
	//  Get all of single_protein_bar_block_jq
	
	var $all__single_protein_bar_block_jq = $view_protein_bar_highlighting_overlay_protein_bars_data_div.find(".single_protein_bar_block_jq");
	
	
	$all__single_protein_bar_block_jq.each( function( index ) { 
		
		var selectionDataSingleBarUpdate = {};
		
		selectionDataUpdates.push( selectionDataSingleBarUpdate );
		
		var $single_protein_bar_block_jq = $( this );
		
		var uid = $single_protein_bar_block_jq.attr( "data-uid" );
		
		if( !uid ) {
			throw Error( "Got no uid." );
		}

		var proteinIdString = $single_protein_bar_block_jq.attr( "data-protein_id");

		var proteinLengthString = $single_protein_bar_block_jq.attr( "data-protein_length");
		var proteinLength = parseInt( proteinLengthString, 10 );
		
		var imageProteinBarDataItem =
			objectThis.imageProteinBarDataManager.getItemByUID( uid );
		
		if ( proteinIdString !== imageProteinBarDataItem.getProteinId() ) {
			
			throw Error( "protein id mismatch.  proteinIdString: '" + proteinIdString + "'.  data item protein id: '" 
					+ imageProteinBarDataItem.getProteinId() + "'." );
		}
		
		selectionDataSingleBarUpdate.imageProteinBarDataItem = imageProteinBarDataItem;
		
		var $whole_protein_bar_selected_checkbox_jq = $single_protein_bar_block_jq.find(".whole_protein_bar_selected_checkbox_jq");
		
		if ( $whole_protein_bar_selected_checkbox_jq.prop( "checked" ) ) {
			
			selectionDataSingleBarUpdate.setProteinBarHighlightedAll = true;
			
		} else {
			
			allProteinBarsSelectWholeProteinBar = false;
			
			var selectionRegions = [];
			
			selectionDataSingleBarUpdate.selectionRegions = selectionRegions;
			
			var $all__bar_region_jq = $single_protein_bar_block_jq.find(".bar_region_jq");

			$all__bar_region_jq.each( function( index ) { 
				
				var $bar_region_jq = $( this );

				var regionStart = undefined;
				var regionEnd = undefined;
								
				var regionStartResult = objectThis._getRegionStartEnd( { selectorClassName : "start_jq", $bar_region_jq : $bar_region_jq } );

//				var $regionStartInputField = regionStartResult.$inputField;

				if ( regionStartResult.invalidString ) {
					
					dataValidationError = true;
					
					return false;  //  EARLY EXIT .each()
				} 

				if ( regionStartResult.emptyString ) {
					regionStart = 1;
				} else {
					regionStart = regionStartResult.valueInt;
				}
				
				

				var regionEndResult = objectThis._getRegionStartEnd( { selectorClassName : "end_jq", $bar_region_jq : $bar_region_jq } );

				var $regionEndInputField = regionEndResult.$inputField;

				if ( regionEndResult.invalidString ) {
					
					dataValidationError = true;
					
					return false;  //  EARLY EXIT .each()
				} 

				if ( regionEndResult.emptyString ) {
					regionEnd = proteinLength;
				} else {
					regionEnd = regionEndResult.valueInt;
				}
				
				if (  regionEnd < regionStart ) {
					
					$regionEndInputField.focus();
					
					var $element = $bar_region_jq.find(".end_before_start_error_msg_jq");
					
					showErrorMsg( $element );
					
					
					dataValidationError = true;

					return false;  //  EARLY EXIT .each()
				}
				
				selectionRegions.push( { regionStart : regionStart , regionEnd : regionEnd } );
				
			});
			
		}

		if ( dataValidationError ) {
			
			return false;  //  EXIT .each()
		}
	});
	
	if ( dataValidationError ) {
		
		return;  // EARLY EXIT
	}
	
	//  Validation has passed 
	
	if ( allProteinBarsSelectWholeProteinBar ) {
		
		//  Special case of all protein bars whole protein bar selected.
		
		//  This is interpreted as no protein bar is selected.
		
		objectThis.imageProteinBarDataManager.clearAllProteinBarsHighlighted();
		
	} else {
	
		//  update the Selection Regions

		for ( var selectionDataUpdatesIndex = 0; selectionDataUpdatesIndex < selectionDataUpdates.length; selectionDataUpdatesIndex++ ) {

			var selectionDataSingleBarUpdate = selectionDataUpdates[ selectionDataUpdatesIndex ];

			var imageProteinBarDataItem = selectionDataSingleBarUpdate.imageProteinBarDataItem;

			if ( selectionDataSingleBarUpdate.setProteinBarHighlightedAll ) {

				imageProteinBarDataItem.setProteinBarHighlightedAll();

			} else {

				imageProteinBarDataItem.clearProteinBarHighlighted();

				var selectionRegions = selectionDataSingleBarUpdate.selectionRegions;



				for ( var selectionRegionsIndex = 0; selectionRegionsIndex < selectionRegions.length; selectionRegionsIndex++ ) {

					var selectionRegionEntry = selectionRegions[ selectionRegionsIndex ];

					var addRegionParams = { start : selectionRegionEntry.regionStart  , end: selectionRegionEntry.regionEnd };

					imageProteinBarDataItem.addProteinBarHighlightedRegion( addRegionParams );

				}
			}
		}
	}
	
	//  Update the Hash and redraw
	
	updateURLHash( false /* useSearchForm */ );	  					// save the new selection to the URL hash
	drawSvg();

	this.closeOverlay();
};



ProteinBarRegionSelectionsOverlayCode.prototype._getRegionStartEnd = function( params ) {
	
	var $bar_region_jq = params.$bar_region_jq;
	
	var selectorClassName = params.selectorClassName;
	
	
	var $inputField = $bar_region_jq.find("." + selectorClassName );
	
	var regionStartEndString = $inputField.val();

	if ( regionStartEndString === "" ) {
		
		return { emptyString : true, $inputField : $inputField };  //  EARLY EXIT
	}

	var integerRegex = /^\d+$/;

	if ( ! integerRegex.test( regionStartEndString ) ) {

		$inputField.focus();

		var $element = $bar_region_jq.find(".invalid_number_error_msg_jq");
		
		showErrorMsg( $element );
		
		
		return { invalidString : true };  //  EARLY EXIT
	}

	var regionStartEndInt = parseInt( regionStartEndString, 10 );

	return { valueInt : regionStartEndInt, $inputField : $inputField };
};
