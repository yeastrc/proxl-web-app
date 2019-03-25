/**
 * crosslink-image-viewer-link-exclusion-selections.js
 * 
 * Javascript for the viewMergedImage.jsp page
 * 
 * This file holds javascript for selecting link exclusion data via a overlay on the page.  
 * 
 * Link exclusion means dimming and disabling links that have excluded.
 * 
 * The method of selecting links to exclude is to choose a pair of ( selected protein and/or selected protein region )
 *  
 * !!! The following variables passed in from "crosslink-image-viewer.js" are used in this file:
 * 
 *    imagePagePrimaryRootCodeObject (copied to local variable imagePagePrimaryRootCodeObject_LocalCopy)
 */


// JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

////////////////////////////
//   All references to proteinId or protein_id are actually referencing the protein sequence id
////////////////////////////

/////     Link Exclusion Data Overlay code

import { ProteinBarHighlightedRegion } from './crosslink-image-viewer-per-protein-bar-data.js';


var imagePagePrimaryRootCodeObject_LocalCopy = undefined; // passed in from "crosslink-image-viewer.js"



/**
 * Constructor
 */
var LinkExclusionSelectionsOverlayCode = function( params ) {
	var linkExclusionDataManager = params.linkExclusionDataManager;
	var indexManager = params.indexManager;
	var imageProteinBarDataManager = params.imageProteinBarDataManager;
	
	this.linkExclusionDataManager = linkExclusionDataManager;
	this.indexManager = indexManager;
	this.imageProteinBarDataManager = imageProteinBarDataManager;
	
	this._singleExistingExclusion_HandlebarsTemplate = null;
	this._singleExclusionChoiceTemplate_HandlebarsTemplate = null;
	this._exclusionChoicePreShowTemplate_HandlebarsTemplate = null;
	
	this.dataChanged = false;
};

/**
 * 
 */
var LinkExclusionSelectionsOverlayCodeContructor = function( params ) { 
	return new LinkExclusionSelectionsOverlayCode( params );
};


/**
 * 
 */
LinkExclusionSelectionsOverlayCode.prototype.init = function( ) {
	var objectThis = this;
	$("#view_link_exclusions_overlay_X_for_exit_overlay").click( function( eventObject ) {
		try {
			objectThis.closeOverlay();
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	} );
	$("#view_link_exclusions_overlay_add_exclusion_button").click( function( eventObject ) {
		try {
			objectThis.addExclusion( { clickedThis : this } );
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	} );
	$("#view_link_exclusions_overlay_protein_bars_close_button").click( function( eventObject ) {
		try {
			var $clickedThis = $( this );
			// $clickedThis qTip API
			var qtipAPI = $clickedThis.qtip('api');
			if ( qtipAPI ) {
				qtipAPI.hide(true);
			}

			objectThis.closeOverlay();
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	} );

	//  Compile Handlebars Templates
	var singleExistingExclusion_handlebarsSource = $( "#view_link_exclusions_overlay_single_existing_exclusion_template" ).text();
	if ( singleExistingExclusion_handlebarsSource === undefined || singleExistingExclusion_handlebarsSource === "" ) {
		throw Error( 'element "#view_link_exclusions_overlay_single_existing_exclusion_template" not found' );
	}
	this._singleExistingExclusion_HandlebarsTemplate = Handlebars.compile( singleExistingExclusion_handlebarsSource );
	
	var singleExclusionChoiceTemplate_handlebarsSource = $( "#view_link_exclusions_overlay_single_exclusion_choice_template" ).text();
	if ( singleExclusionChoiceTemplate_handlebarsSource === undefined || singleExistingExclusion_handlebarsSource === "" ) {
		throw Error( 'element "#view_link_exclusions_overlay_single_exclusion_choice_template" not found' );
	}
	this._singleExclusionChoiceTemplate_HandlebarsTemplate = Handlebars.compile( singleExclusionChoiceTemplate_handlebarsSource );
	
	
	var exclusionChoicePreShowTemplate_handlebarsSource = $( "#view_link_exclusions_overlay_exclusion_choice_pre_show_template" ).text();
	if ( exclusionChoicePreShowTemplate_handlebarsSource === undefined || exclusionChoicePreShowTemplate_handlebarsSource === "" ) {
		throw Error( 'element "#view_link_exclusions_overlay_exclusion_choice_pre_show_template" not found' );
	}
	this._exclusionChoicePreShowTemplate_HandlebarsTemplate = Handlebars.compile( exclusionChoicePreShowTemplate_handlebarsSource );
	
};

/**
 * 
 */
LinkExclusionSelectionsOverlayCode.prototype.closeOverlay = function( ) {

	var $view_link_exclusions_modal_dialog_overlay_background = $("#view_link_exclusions_modal_dialog_overlay_background");
	$view_link_exclusions_modal_dialog_overlay_background.hide();
	var $view_link_exclusions_overlay_div = $("#view_link_exclusions_overlay_div");
	$view_link_exclusions_overlay_div.hide();

	this._clearExcludeChoicesBothLists( );
	
	if ( this.dataChanged ) {

		imagePagePrimaryRootCodeObject_LocalCopy.call__updateURLHash( false /* useSearchForm */ );
		imagePagePrimaryRootCodeObject_LocalCopy.call__loadDataAndDraw( true /* doDraw */ );
	}
};

/**
 * Open
 */
LinkExclusionSelectionsOverlayCode.prototype.openOverlay = function() {

	this.dataChanged = false;
	
	//  Clean up linkExclusionDataManager
	this.linkExclusionDataManager.removeOrphanRecords();
	
	var $view_link_exclusions_modal_dialog_overlay_background = $("#view_link_exclusions_modal_dialog_overlay_background");
	$view_link_exclusions_modal_dialog_overlay_background.show();
	var $view_link_exclusions_overlay_div = $("#view_link_exclusions_overlay_div");
	$view_link_exclusions_overlay_div.show();
	this._populate();
};

/**
 * Populate
 */
LinkExclusionSelectionsOverlayCode.prototype._populate = function( params ) {
	
	this._populateExcludedItems();
	this._populateExcludeChoiceItems();
	
	
};



/**
 * Populate Excluded Items
 */
LinkExclusionSelectionsOverlayCode.prototype._populateExcludedItems = function() {
	var objectThis = this;
	
	var $view_link_exclusions_overlay_excluded_items_data_div = $("#view_link_exclusions_overlay_excluded_items_data_div");
	if ( $view_link_exclusions_overlay_excluded_items_data_div.length === 0 ) {
		throw Error( 'ERROR: No element for "#view_link_exclusions_overlay_excluded_items_data_div"' );
	}
	$view_link_exclusions_overlay_excluded_items_data_div.empty();
	
	var exclusionDataLinksTwoEndPoints = this.linkExclusionDataManager.getExclusionList();

	if ( exclusionDataLinksTwoEndPoints.length === 0 ) {
		$("#view_link_exclusions_overlay_no_exclusions_text").show();
		$("#view_link_exclusions_overlay_excluded_items_outer_block").hide();
	} else {
		$("#view_link_exclusions_overlay_no_exclusions_text").hide();
		$("#view_link_exclusions_overlay_excluded_items_outer_block").show();
		
		for( var i = 0; i < exclusionDataLinksTwoEndPoints.length; i++ ) {
			var exclusionEntry = exclusionDataLinksTwoEndPoints[ i ];

			var displayInfo_1 = this._getProteinAndRegionInfo( { proteinUID : exclusionEntry.getProteinUID_1(), regionUID : exclusionEntry.getRegionUID_1() } );
			var displayInfo_2 = this._getProteinAndRegionInfo( { proteinUID : exclusionEntry.getProteinUID_2(), regionUID : exclusionEntry.getRegionUID_2() } );

			//  singleExclusionChoiceContext is for Handlebars template to put info on the overlay on the page
			var singleExclusionEntryContext = {
					proteinUID_1 : exclusionEntry.getProteinUID_1(),
					proteinUID_2 : exclusionEntry.getProteinUID_2(),
					regionUID_1 : exclusionEntry.getRegionUID_1(),
					regionUID_2 : exclusionEntry.getRegionUID_2(),
					proteinName_1 : displayInfo_1.proteinName,
					proteinName_2 : displayInfo_2.proteinName,
					regionRange_1 : displayInfo_1.regionRange,
					regionRange_2 : displayInfo_2.regionRange
			};
			var singleExclusionEntryFullHTMLHtml = this._singleExistingExclusion_HandlebarsTemplate( singleExclusionEntryContext );

			var $singleExclusionEntryFullHTMLHtml =
				$( singleExclusionEntryFullHTMLHtml ).appendTo( $view_link_exclusions_overlay_excluded_items_data_div );

			var $view_link_exclusions_overlay_exclusion_remove_button_jq =
				$singleExclusionEntryFullHTMLHtml.find(".view_link_exclusions_overlay_exclusion_remove_button_jq");
			$view_link_exclusions_overlay_exclusion_remove_button_jq.click( function( eventObject ) {
				try {
					objectThis.removeExclusionClicked( { clickedThis : this } );
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			} );
		}
	}
};


/**
 * Populate Excluded Items
 */
LinkExclusionSelectionsOverlayCode.prototype.removeExclusionClicked = function( params ) {
	var clickedThis = params.clickedThis;
	var $clickedThis = $( clickedThis );
	var $exclusion_entry_jq = $clickedThis.closest(".exclusion_entry_jq");
	
	this.dataChanged = true;
	
	var proteinUID_1 = $exclusion_entry_jq.attr("data-protein_uid_1");
	var regionUID_1 = $exclusion_entry_jq.attr("data-region_uid_1");
	var proteinUID_2 = $exclusion_entry_jq.attr("data-protein_uid_2");
	var regionUID_2 = $exclusion_entry_jq.attr("data-region_uid_2");
	
	if ( regionUID_1 === "" || regionUID_1 === null ) {
		regionUID_1 = undefined;
	}
	if ( regionUID_2 === "" || regionUID_2 === null ) {
		regionUID_2 = undefined;
	}

	this.linkExclusionDataManager.removeExclusionEntry( {
		proteinUID_1 : proteinUID_1,
		regionUID_1 : regionUID_1,
		proteinUID_2 : proteinUID_2,
		regionUID_2 : regionUID_2 
	});
	
	this._populateExcludedItems();
};

/**
 * Populate Excluded Items
 */
LinkExclusionSelectionsOverlayCode.prototype._getProteinAndRegionInfo = function( params ) {
	var proteinUID = params.proteinUID;
	var regionUID = params.regionUID;

	var imageProteinBarDataItem = this.imageProteinBarDataManager.getItemByUID( proteinUID );
	if( !imageProteinBarDataItem ) {
		throw Error( "ERROR: Have no entry in protein bar data manager for uid:" + proteinUID );
	}
	var proteinId = imageProteinBarDataItem.getProteinId();
	var proteinLength = imageProteinBarDataItem.getProteinLength();
	var proteinName = imagePagePrimaryRootCodeObject_LocalCopy.getVariable__v_proteinNames()[ proteinId ];
	var regionRange = undefined;
	if ( regionUID ) {
		var regionEntry = imageProteinBarDataItem.getRegionFromRegionUID( { regionUID : regionUID } );
		if ( ! regionEntry ) {
			throw Error( "ERROR: Have no entry in imageProteinBarDataItem for RegionUID:" + regionUID );
		}
		regionRange = "(" + regionEntry.getStart() + "-" + regionEntry.getEnd() + ")";
	}
	var result = {
			proteinName : proteinName,
			regionRange : regionRange,
			proteinId : proteinId
	};
	return result;
}

/**
 * Populate Exclude Choice Items
 */
LinkExclusionSelectionsOverlayCode.prototype._populateExcludeChoiceItems = function() {
	var objectThis = this;
	
	var $view_link_exclusions_overlay_exclude_choices_1 = $("#view_link_exclusions_overlay_exclude_choices_1");
	if ( $view_link_exclusions_overlay_exclude_choices_1.length === 0 ) {
		throw Error( 'ERROR: No element for "#view_link_exclusions_overlay_exclude_choices_1"' );
	}
	var $view_link_exclusions_overlay_exclude_choices_2 = $("#view_link_exclusions_overlay_exclude_choices_2");
	if ( $view_link_exclusions_overlay_exclude_choices_2.length === 0 ) {
		throw Error( 'ERROR: No element for "#view_link_exclusions_overlay_exclude_choices_2"' );
	}
	$view_link_exclusions_overlay_exclude_choices_1.empty();
	$view_link_exclusions_overlay_exclude_choices_2.empty();
	
	//  Special case of no protein bars selected
//	var noProteinBarsSelected = true;
//	var isAnyProteinBarsHighlighted = this.imageProteinBarDataManager.isAnyProteinBarsHighlighted();
//	if ( isAnyProteinBarsHighlighted ) {
//		noProteinBarsSelected = false;
//	}
	
	var proteinsChosenForDisplayArray = this.indexManager.getProteinArray();
	var proteinsChosenForDisplayArrayLength = proteinsChosenForDisplayArray.length;
	var proteinsChosenForDisplayArrayLastItemIndex = proteinsChosenForDisplayArray.length - 1;
	
	var last_1_$singleExclusionChoiceFullHTMLHtml = null;
	var last_2_$singleExclusionChoiceFullHTMLHtml = null;
	
	for( var i = 0; i < proteinsChosenForDisplayArrayLength; i++ ) {
		var uid = proteinsChosenForDisplayArray[ i ][ 'uid' ];
		var imageProteinBarDataItem = this.imageProteinBarDataManager.getItemByUID( uid );
		if( !imageProteinBarDataItem ) {
			throw Error( "ERROR: Have no entry in protein bar data manager for uid:" + uid );
		}
		var proteinId = imageProteinBarDataItem.getProteinId();
		var proteinLength = imageProteinBarDataItem.getProteinLength();
		var proteinName = imagePagePrimaryRootCodeObject_LocalCopy.getVariable__v_proteinNames()[ proteinId ];
		
		//  First add Protein without Regions
		//  singleExclusionChoiceContext is for Handlebars template to put info on the overlay on the page
		var singleExclusionChoiceContext = {
				proteinUID : uid,
				proteinName : proteinName,
				proteinLength : proteinLength
		};
		var singleExclusionChoiceFullHTMLHtml = this._singleExclusionChoiceTemplate_HandlebarsTemplate( singleExclusionChoiceContext );

		//  Add to both lists
		last_1_$singleExclusionChoiceFullHTMLHtml = 
			this._addSingleExclusionChoice( { singleExclusionChoiceFullHTMLHtml : singleExclusionChoiceFullHTMLHtml,
				$view_link_exclusions_overlay_exclude_choices : $view_link_exclusions_overlay_exclude_choices_1 } );
		last_2_$singleExclusionChoiceFullHTMLHtml = 
			this._addSingleExclusionChoice( { singleExclusionChoiceFullHTMLHtml : singleExclusionChoiceFullHTMLHtml,
				$view_link_exclusions_overlay_exclude_choices : $view_link_exclusions_overlay_exclude_choices_2 } );
	

		//  Second add Protein's Regions
		
		var regionsArray = imageProteinBarDataItem.getProteinBarHighlightedRegionsArray();
		
		if ( regionsArray && regionsArray.length > 0 ) {
			for ( var regionsArrayIndex = 0; regionsArrayIndex < regionsArray.length; regionsArrayIndex++ ) {
				var regionEntry = regionsArray[ regionsArrayIndex ];
				var regionUniqueId = regionEntry.getRegionUniqueId();
				var regionStart = regionEntry.getStart();
				var regionEnd = regionEntry.getEnd();
				var regionRange = "(" + regionStart + "-" + regionEnd + ")";
				
				//  singleExclusionChoiceContext is for Handlebars template to put info on the overlay on the page
				var singleExclusionChoiceContext = {
						proteinUID : uid,
						proteinName : proteinName,
						regionUID : regionUniqueId,
						regionRange : regionRange
				};
				var singleExclusionChoiceFullHTMLHtml = this._singleExclusionChoiceTemplate_HandlebarsTemplate( singleExclusionChoiceContext );

				//  Add to both user selection lists
				last_1_$singleExclusionChoiceFullHTMLHtml = 
					this._addSingleExclusionChoice( { singleExclusionChoiceFullHTMLHtml : singleExclusionChoiceFullHTMLHtml,
						$view_link_exclusions_overlay_exclude_choices : $view_link_exclusions_overlay_exclude_choices_1 } );
				last_2_$singleExclusionChoiceFullHTMLHtml = 
					this._addSingleExclusionChoice( { singleExclusionChoiceFullHTMLHtml : singleExclusionChoiceFullHTMLHtml,
						$view_link_exclusions_overlay_exclude_choices : $view_link_exclusions_overlay_exclude_choices_2 } );
			}
		}
	}
	//  Hide protein divider after last entry
//	if ( i === ( proteinsChosenForDisplayArrayLastItemIndex ) ) {
//		var $protein_divider_jq = $singleBarHtml.find(".protein_divider_jq");
//		$protein_divider_jq.hide();
//	}

};

/**
 * return $singleExclusionChoiceFullHTMLHtml
 */
LinkExclusionSelectionsOverlayCode.prototype._addSingleExclusionChoice = function( params ) {
	var objectThis = this;
	var singleExclusionChoiceFullHTMLHtml = params.singleExclusionChoiceFullHTMLHtml;
	var $view_link_exclusions_overlay_exclude_choices = params.$view_link_exclusions_overlay_exclude_choices;

	var $singleExclusionChoiceFullHTMLHtml = $( singleExclusionChoiceFullHTMLHtml ).appendTo( $view_link_exclusions_overlay_exclude_choices );
	$singleExclusionChoiceFullHTMLHtml.click( function( eventObject ) {
		try {
			objectThis._exclusionChoiceClicked( { clickedThis : this } );
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	} );
	
	return $singleExclusionChoiceFullHTMLHtml;
};

/**
 * Process click on an exclusion list entry
 */
LinkExclusionSelectionsOverlayCode.prototype._exclusionChoiceClicked = function( params ) {
	var clickedThis = params.clickedThis;
	var $clickedThis = $( clickedThis );
	var $exclude_choices_block_jq = $clickedThis.closest(".exclude_choices_block_jq");
	var $exclusion_choice_entry_jq_All = $exclude_choices_block_jq.find(".exclusion_choice_entry_jq");
	$exclusion_choice_entry_jq_All.removeClass("selected-choice");
	$clickedThis.addClass("selected-choice");
	
	this._excludeButtonUpdateEnabled();
	this._updateExclusionChoicePreShow();
};

/**
 * Enable Disable Exclude button
 */
LinkExclusionSelectionsOverlayCode.prototype._excludeButtonUpdateEnabled = function() {
	var $view_link_exclusions_overlay_exclude_choices_1 = $("#view_link_exclusions_overlay_exclude_choices_1");
	if ( $view_link_exclusions_overlay_exclude_choices_1.length === 0 ) {
		throw Error( 'ERROR: No element for "#view_link_exclusions_overlay_exclude_choices_1"' );
	}
	var $view_link_exclusions_overlay_exclude_choices_2 = $("#view_link_exclusions_overlay_exclude_choices_2");
	if ( $view_link_exclusions_overlay_exclude_choices_2.length === 0 ) {
		throw Error( 'ERROR: No element for "#view_link_exclusions_overlay_exclude_choices_2"' );
	}

	if ( this._excludeChoiceListHasSelected( { $view_link_exclusions_overlay_exclude_choices : $view_link_exclusions_overlay_exclude_choices_1 } )
			&& this._excludeChoiceListHasSelected( { $view_link_exclusions_overlay_exclude_choices : $view_link_exclusions_overlay_exclude_choices_2 } ) ) {
		// Found selected in both lists so enable the button
		$("#view_link_exclusions_overlay_add_exclusion_button").prop( "disabled", false );
		$("#view_link_exclusions_overlay_add_exclusion_button_disabled_cover_div").hide();
	} else {
		//  Disable button
		$("#view_link_exclusions_overlay_add_exclusion_button").prop( "disabled", true );
		$("#view_link_exclusions_overlay_add_exclusion_button_disabled_cover_div").show();
	}
};

/**
 * Does exclusion choice list have a selected item?
 */
LinkExclusionSelectionsOverlayCode.prototype._excludeChoiceListHasSelected = function( params ) {
	var foundSelectedChoice = false;
	var $view_link_exclusions_overlay_exclude_choices = params.$view_link_exclusions_overlay_exclude_choices;
	var $exclusion_choice_entry_jq_All = $view_link_exclusions_overlay_exclude_choices.find(".exclusion_choice_entry_jq");
	$exclusion_choice_entry_jq_All.each(  function( index, element ) {
		var $entry = $( this );
		if ( $entry.hasClass("selected-choice") ) {
			foundSelectedChoice = true;
			return false;   //  EARLY EXIT .each()
		}
	});
	return foundSelectedChoice;
};

/**
 * Update "Choice Pre Show" Block 
 * 
 * Show a matched pair of choices of proteins and optional regions the the "Choice Pre Show" Block
 */
LinkExclusionSelectionsOverlayCode.prototype._updateExclusionChoicePreShow = function() {

	var $view_link_exclusions_overlay_new_excluded_item_pre_show = $("#view_link_exclusions_overlay_new_excluded_item_pre_show");
	if ( $view_link_exclusions_overlay_new_excluded_item_pre_show.length === 0 ) {
		throw Error( 'ERROR: No element for "#view_link_exclusions_overlay_new_excluded_item_pre_show"' );
	}

	var $view_link_exclusions_overlay_exclude_choices_1 = $("#view_link_exclusions_overlay_exclude_choices_1");
	if ( $view_link_exclusions_overlay_exclude_choices_1.length === 0 ) {
		throw Error( 'ERROR: No element for "#view_link_exclusions_overlay_exclude_choices_1"' );
	}
	var $view_link_exclusions_overlay_exclude_choices_2 = $("#view_link_exclusions_overlay_exclude_choices_2");
	if ( $view_link_exclusions_overlay_exclude_choices_2.length === 0 ) {
		throw Error( 'ERROR: No element for "#view_link_exclusions_overlay_exclude_choices_2"' );
	}
	var selectedChoiceData_1 = this._getExcludeChoice( { $view_link_exclusions_overlay_exclude_choices : $view_link_exclusions_overlay_exclude_choices_1 } );
	var selectedChoiceData_2 = this._getExcludeChoice( { $view_link_exclusions_overlay_exclude_choices : $view_link_exclusions_overlay_exclude_choices_2 } );

	var newExclusionEntry = {};

	if ( selectedChoiceData_1 ) {
		var displayInfo_1 = this._getProteinAndRegionInfo( { proteinUID : selectedChoiceData_1.proteinUID, regionUID : selectedChoiceData_1.regionUID } );
		newExclusionEntry.proteinName_1 = displayInfo_1.proteinName;
		newExclusionEntry.regionRange_1 = displayInfo_1.regionRange;
	}
	if ( selectedChoiceData_2 ) {
		var displayInfo_2 = this._getProteinAndRegionInfo( { proteinUID : selectedChoiceData_2.proteinUID, regionUID : selectedChoiceData_2.regionUID } );
		newExclusionEntry.proteinName_2 = displayInfo_2.proteinName;
		newExclusionEntry.regionRange_2 = displayInfo_2.regionRange;
	}
	
	var choicePreShowHTML = this._exclusionChoicePreShowTemplate_HandlebarsTemplate( newExclusionEntry );
	$view_link_exclusions_overlay_new_excluded_item_pre_show.html( choicePreShowHTML );
};

/**
 * Clear exclusion choices both list
 */
LinkExclusionSelectionsOverlayCode.prototype._clearExcludeChoicesBothLists = function( params ) {

	var $view_link_exclusions_overlay_exclude_choices_1 = $("#view_link_exclusions_overlay_exclude_choices_1");
	if ( $view_link_exclusions_overlay_exclude_choices_1.length === 0 ) {
		throw Error( 'ERROR: No element for "#view_link_exclusions_overlay_exclude_choices_1"' );
	}
	var $view_link_exclusions_overlay_exclude_choices_2 = $("#view_link_exclusions_overlay_exclude_choices_2");
	if ( $view_link_exclusions_overlay_exclude_choices_2.length === 0 ) {
		throw Error( 'ERROR: No element for "#view_link_exclusions_overlay_exclude_choices_2"' );
	}
	
	this._clearExcludeChoice( { $view_link_exclusions_overlay_exclude_choices : $view_link_exclusions_overlay_exclude_choices_1 } );
	this._clearExcludeChoice( { $view_link_exclusions_overlay_exclude_choices : $view_link_exclusions_overlay_exclude_choices_2 } );
	
	//  Disable button
	$("#view_link_exclusions_overlay_add_exclusion_button").prop( "disabled", true );
	$("#view_link_exclusions_overlay_add_exclusion_button_disabled_cover_div_jq").show();
	
	$("#view_link_exclusions_overlay_new_excluded_item_pre_show").html("");
};

/**
 * Clear exclusion choice
 */
LinkExclusionSelectionsOverlayCode.prototype._clearExcludeChoice = function( params ) {
	var foundSelectedChoice = false;
	var $view_link_exclusions_overlay_exclude_choices = params.$view_link_exclusions_overlay_exclude_choices;
	var $exclusion_choice_entry_jq_All = $view_link_exclusions_overlay_exclude_choices.find(".exclusion_choice_entry_jq");
	$exclusion_choice_entry_jq_All.removeClass("selected-choice");
};

/**
 * Add the exclusion
 */
LinkExclusionSelectionsOverlayCode.prototype.addExclusion = function( params ) {
	var clickedThis = params.clickedThis;
	
	this.dataChanged = true;
	
	var $clickedThis = $( clickedThis );
	// $clickedThis qTip API
	var qtipAPI = $clickedThis.qtip('api');
	if ( qtipAPI ) {
		qtipAPI.hide(true);
	}

	var $view_link_exclusions_overlay_exclude_choices_1 = $("#view_link_exclusions_overlay_exclude_choices_1");
	if ( $view_link_exclusions_overlay_exclude_choices_1.length === 0 ) {
		throw Error( 'ERROR: No element for "#view_link_exclusions_overlay_exclude_choices_1"' );
	}
	var $view_link_exclusions_overlay_exclude_choices_2 = $("#view_link_exclusions_overlay_exclude_choices_2");
	if ( $view_link_exclusions_overlay_exclude_choices_2.length === 0 ) {
		throw Error( 'ERROR: No element for "#view_link_exclusions_overlay_exclude_choices_2"' );
	}

	var selectedChoiceData_1 = this._getExcludeChoice( { $view_link_exclusions_overlay_exclude_choices : $view_link_exclusions_overlay_exclude_choices_1 } );
	var selectedChoiceData_2 = this._getExcludeChoice( { $view_link_exclusions_overlay_exclude_choices : $view_link_exclusions_overlay_exclude_choices_2 } );

	if ( selectedChoiceData_1.exclusionChoiceIndex > selectedChoiceData_2.exclusionChoiceIndex ) {
		var selectedChoiceData_temp = selectedChoiceData_1;
		selectedChoiceData_1 = selectedChoiceData_2;
		selectedChoiceData_2 = selectedChoiceData_temp;
	}
	
	var newExclusionEntry = {
			proteinUID_1 : selectedChoiceData_1.proteinUID,
			regionUID_1 : selectedChoiceData_1.regionUID,
			proteinUID_2 : selectedChoiceData_2.proteinUID,
			regionUID_2 : selectedChoiceData_2.regionUID
	};
	
	this.linkExclusionDataManager.addExclusionEntry( newExclusionEntry );
	
	//  Clear PreShow
	$("#view_link_exclusions_overlay_new_excluded_item_pre_show").html("");
	
	this._populateExcludedItems();
	
	this._clearExcludeChoicesBothLists( );
	
	imagePagePrimaryRootCodeObject_LocalCopy.call__updateURLHash( false /* useSearchForm */ );

};


/**
 * Get selected exclusion choice from choice list
 * return null if no choice selected
 * returns first selected choice, although there should be only one
 */
LinkExclusionSelectionsOverlayCode.prototype._getExcludeChoice = function( params ) {
	var selectedChoiceData = null;
	var $view_link_exclusions_overlay_exclude_choices = params.$view_link_exclusions_overlay_exclude_choices;
	var $exclusion_choice_entry_jq_All = $view_link_exclusions_overlay_exclude_choices.find(".exclusion_choice_entry_jq");
	var exclusionChoiceIndex = 0;
	$exclusion_choice_entry_jq_All.each(  function( index, element ) {
		var $entry = $( this );
		if ( $entry.hasClass("selected-choice") ) {
			var proteinUID = $entry.attr("data-protein_uid");
			var regionUID = $entry.attr("data-region_uid");
			selectedChoiceData = {
					proteinUID : proteinUID,
					regionUID : regionUID,
					exclusionChoiceIndex : exclusionChoiceIndex
			};
			if ( selectedChoiceData.regionUID === "" ) {
				selectedChoiceData.regionUID = undefined;
			}
			return false;   //  EARLY EXIT .each()
		}
		exclusionChoiceIndex++;
	});
	return selectedChoiceData;
};



/**
 * Called from "crosslink-image-viewer.js" to populate local copy of imagePagePrimaryRootCodeObject_LocalCopy
 */
var LinkExclusionSelectionsOverlayCode_pass_imagePagePrimaryRootCodeObject = function( imagePagePrimaryRootCodeObject_LocalCopy_Param ) {
	imagePagePrimaryRootCodeObject_LocalCopy = imagePagePrimaryRootCodeObject_LocalCopy_Param;
}


export { LinkExclusionSelectionsOverlayCodeContructor, LinkExclusionSelectionsOverlayCode_pass_imagePagePrimaryRootCodeObject }


