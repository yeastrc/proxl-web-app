
//    header_main.js


//   For content in the header_main.jsp file



//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";

// /////////////////////////////////////////


var HEADER_MAIN_CONSTANTS = {
		
	DIV_CLOSE_TIMEOUT : 250  // milliseconds	
		
};



var headerMainGlobals = {

	divCloseTimerId : null
};




////////////////////////////

var initHeaderMain = function() {

	
	// Put on both items
	$("#header_projects_link,#header_projects_list").mouseenter( function( eventObject  ) {
		
		if ( headerMainGlobals.divCloseTimerId !== null ) {
			
			// Cancel existing timer
			clearTimeout( headerMainGlobals.divCloseTimerId );
		}

		var $header_projects_list = $("#header_projects_list");

		$header_projects_list.show();
		
		var LIST_MINIMUM_HEIGHT = 150;
		
		var $window = $( window );
		var viewportHeight = $window.height();

		//   Set list height to viewport * .8 (80%) or at minimum height
		var header_projects_listHeight = viewportHeight * .8;
		if ( header_projects_listHeight < LIST_MINIMUM_HEIGHT ) {
			header_projects_listHeight = LIST_MINIMUM_HEIGHT ;
		}
		//  Apply height to list container
		$header_projects_list.css( { height : header_projects_listHeight + "px" } ); 
		//  Reposition at top of list
		$header_projects_list.scrollTop( 0 );
		
		///////////
		
	} ).mouseleave( function( eventObject  ) {
		
		if ( headerMainGlobals.divCloseTimerId !== null ) {
			// Cancel existing timer
			clearTimeout( headerMainGlobals.divCloseTimerId );
		}
		// Create timer to close projects list div
		headerMainGlobals.divCloseTimerId = setTimeout( function() {
			$("#header_projects_list").hide();	
		}, HEADER_MAIN_CONSTANTS.DIV_CLOSE_TIMEOUT );
	} );
	

	///////////   Simulated Tool Tips

	$("#signin_header_link").mouseenter( function( eventObject  ) {
		
		$("#signin_header_tooltip").show();
		
	} ).mouseleave( function( eventObject  ) {
		
		$("#signin_header_tooltip").hide();
		
	} );
	
	
	$("#account_settings_header_link").mouseenter( function( eventObject  ) {
		
		$("#account_settings_header_tooltip").show();
		
	} ).mouseleave( function( eventObject  ) {
		
		$("#account_settings_header_tooltip").hide();
		
	} );
	
	$("#manage_users_header_link").mouseenter( function( eventObject  ) {
		
		$("#manage_users_tooltip_header").show();
		
	} ).mouseleave( function( eventObject  ) {
		
		$("#manage_users_tooltip_header").hide();
		
	} );
	
	$("#manage_proxl_settings_header_link").mouseenter( function( eventObject  ) {
		
		$("#manage_config_tooltip_header").show();
		
	} ).mouseleave( function( eventObject  ) {
		
		$("#manage_config_tooltip_header").hide();
		
	} );	
	
	$("#sign_out_header_link").mouseenter( function( eventObject  ) {
		
		$("#sign_out_header_tooltip").show();
		
	} ).mouseleave( function( eventObject  ) {
		
		$("#sign_out_header_tooltip").hide();
		
	} );
	

	$("#help_header_link").mouseenter( function( eventObject  ) {
		
		$("#help_header_tooltip").show();
		
	} ).mouseleave( function( eventObject  ) {
		
		$("#help_header_tooltip").hide();
		
	} );
	
};



///////////////

$(document).ready(function() {

	initHeaderMain();

});
