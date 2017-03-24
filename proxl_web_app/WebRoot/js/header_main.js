
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

	
	$("#header_projects_link,#header_projects_list_container").mouseenter( function( eventObject  ) {
		
		if ( headerMainGlobals.divCloseTimerId !== null ) {
			
			// Cancel existing timer
			clearTimeout( headerMainGlobals.divCloseTimerId );
		}

		var $window = $( window );
		var viewportHeight = $window.height();
		
		var $header_projects_list_container = $("#header_projects_list_container");
		var $header_projects_list = $("#header_projects_list");

		$header_projects_list_container.show();
		
		var listMaximumHeight = $header_projects_list.height() + 5; // + 40;

		var LIST_MINIMUM_HEIGHT = 150;

		//   Set list height to viewport * .8 (80%) or at minimum height
		var header_projects_listHeight = viewportHeight * .8;
		if ( header_projects_listHeight < LIST_MINIMUM_HEIGHT ) {
			header_projects_listHeight = LIST_MINIMUM_HEIGHT ;
		}
		if ( header_projects_listHeight > listMaximumHeight ) {
			header_projects_listHeight = listMaximumHeight;
		}
		
		//  Apply height to list container
		$header_projects_list_container.css( { height : header_projects_listHeight + "px" } ); 
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
			$("#header_projects_list_container").hide();	
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
