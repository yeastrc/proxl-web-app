/**
 * minimumPSM_Count_Filter.js
 * 
 * Javascript for minimumPSM_Count_Filter.jsp page fragment 
 * 
 * Filter section of Peptide, Merged Peptide, Protein, Merged Protein, Coverage, Image, and Structure Pages
 * 
 * page variable: minimumPSM_Count_Filter
 * 
 * Filter on minimum number of PSMs
 */

//////////////////////////////////
// JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

///////////////////////////////////////////


var MinimumPSM_Count_Filter = function() {
	var initializeCalled = false;
	
	var minPSMs = undefined;
	var minimum_psm_count_default_value = undefined;
	
	//  initialize the page (Add element listeners like onClick, ...)
	
	this.initialize = function(  ) {
		try {
			var objectThis = this;
			var $minimum_psm_count_default_value = $("#minimum_psm_count_default_value");
			var minimum_psm_count_default_valueString = $minimum_psm_count_default_value.text();
			minimum_psm_count_default_value = window.parseInt( minimum_psm_count_default_valueString, 10 );
			if ( window.isNaN( minimum_psm_count_default_value ) ) {
				throw Error( "Value in element with id 'minimum_psm_count_default_value' cannot be parsed to a integer.  Value: " + minimum_psm_count_default_valueString );
			}
			initializeCalled = true;
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};

	this.getDefault = function() {
		try {
			if ( ! initializeCalled ) {
				this.initialize();
			}
			return minimum_psm_count_default_value;
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
	
	//  params is normally { minPSMs : _query_json_field_Contents.minPSMs }
	this.saveMinPSMsFilter = function( params ) {
		try {
			if ( ! initializeCalled ) {
				this.initialize();
			}
			if ( params.minPSMs === null || params.minPSMs === undefined ) {
				 minPSMs = minimum_psm_count_default_value;
			} else {
				 minPSMs = params.minPSMs;
			}
			this.updatePageWithCurrentValue();
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};

	/**
	 * Return current minimum PSMs
	 */
	this.getMinPSMsFilter = function(  ) {
		if ( ! initializeCalled ) {
			this.initialize();
		}
		return minPSMs;
	};
	
	////////////
	
	//  Overlay management

	/**
	 * 
	 */
	this.cancel = function() {
		
		this.hideOverlay();
	};
	
	/**
	 * Hide overlay
	 */
	this.hideOverlay = function() {
		
		var $minimum_psm_count_modal_dialog_overlay_background = $("#minimum_psm_count_modal_dialog_overlay_background");
		$minimum_psm_count_modal_dialog_overlay_background.hide();
		var $minimum_psm_count_modal_dialog_overlay_div = $("#minimum_psm_count_modal_dialog_overlay_div");
		$minimum_psm_count_modal_dialog_overlay_div.hide();
	};

	/**
	 * Open overlay
	 */
	this.openOverlay = function() {
		
		var $minimum_psm_count_user_input = $("#minimum_psm_count_user_input");
		$minimum_psm_count_user_input.val( minPSMs );
		var $minimum_psm_count_modal_dialog_overlay_background = $("#minimum_psm_count_modal_dialog_overlay_background");
		$minimum_psm_count_modal_dialog_overlay_background.show();
		var $minimum_psm_count_modal_dialog_overlay_div = $("#minimum_psm_count_modal_dialog_overlay_div");
		$minimum_psm_count_modal_dialog_overlay_div.show();
	};
	
	/**
	 * 
	 */
	this.saveUserValues = function() {
		var $minimum_psm_count_user_input = $("#minimum_psm_count_user_input");
		var minimum_psm_count_user_input = $minimum_psm_count_user_input.val();
		
		//  TODO  Validate input
		
		if ( window.defaultPageView ) {
			window.defaultPageView.searchFormChanged_ForDefaultPageView(); 
		}
		
		minPSMs = parseInt( minimum_psm_count_user_input, 10 );
		if ( window.isNaN( minPSMs ) ) {
			minPSMs = 1;  // Not valid integer so default to 1
		} 
		if ( minPSMs < 1 ) {
			minPSMs = 1; // Minimum value is 1
		}
		this.updatePageWithCurrentValue();
		this.hideOverlay();
	};
	
	/**
	 * 
	 */
	this.reset = function() {
		
		var $minimum_psm_count_user_input = $("#minimum_psm_count_user_input");
		$minimum_psm_count_user_input.val( minimum_psm_count_default_value );
	};
	
	
	/**
	 * 
	 */
	this.updatePageWithCurrentValue = function( ) {
		
		var $minimum_psm_count_current_value_display_when_value_default = $("#minimum_psm_count_current_value_display_when_value_default");
		var $minimum_psm_count_current_value_display = $("#minimum_psm_count_current_value_display");
		
		if ( minPSMs === 1 ) {
			//  Display "Showing All" message 
			$minimum_psm_count_current_value_display.hide();
			$minimum_psm_count_current_value_display_when_value_default.show();
		} else {
			//  Display minimum PSM count
			$minimum_psm_count_current_value_display_when_value_default.hide();
			$minimum_psm_count_current_value_display.show();
			$minimum_psm_count_current_value_display.text( minPSMs );
		}
	};
};

//Instance of class
var minimumPSM_Count_Filter = new MinimumPSM_Count_Filter();

window.minimumPSM_Count_Filter = minimumPSM_Count_Filter;

export { minimumPSM_Count_Filter }

