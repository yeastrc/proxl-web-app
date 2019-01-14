
//    viewProjectPage.js

//  Javascript for the viewProject.jsp page

//////////////////////////////////
// JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";


var _project_id = null;

$(document).ready(function()  { 
	try {
		initViewProjectPage();
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
});

/////////////
function initViewProjectPage() {
	var project_id = $("#project_id").val();
	if ( project_id === undefined || project_id === null 
			|| project_id === "" ) {
		throw Error( '$("#project_id").val() returned no value' );
	} else {
		_project_id = project_id;
	}
	//  tool tips for files attached to searches
	$(".search_file_link_for_tooltip_jq").each(function() { // Grab search file links
		var $search_file_link_tooltip_jq = $(this).children(".search_file_link_tooltip_jq");
		if ( $search_file_link_tooltip_jq.length > 0 ) {
			var tipText = $search_file_link_tooltip_jq.text();
			$(this).qtip({ 
				content: {
					text: tipText
				}
			});
		}
	});

	/////////////////////////
	setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else
		try {
			if ( window.initQCPlotPSMScoreVsScoreClickHandlers ) {
				initQCPlotPSMScoreVsScoreClickHandlers();
			}
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	},10);
	//  Initialize the buttons from the current values of the check boxes.
	//     The check boxes may be checked from using the back button.
	updateButtonsBasedOnCheckedSearches ( );
}

///////////// attach to window. since used in other JS files
window.searchesToMerge = new Array();


//////////
//   Called by "onclick" on HTML element
window.checkSearchCheckboxes = function( projectSearchId) {
	try {
		if( $( "input#search-checkbox-" + projectSearchId ).is( ":checked" ) ) {
			if( searchesToMerge.indexOf( projectSearchId ) == -1 ) { searchesToMerge.push( projectSearchId ); }
		} else {
			var index = searchesToMerge.indexOf( projectSearchId );
			if( index != -1 ) {
				searchesToMerge.splice( index, 1 );
			}
		}
		updateButtonsBasedOnCheckedSearches();
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
}

//////////
window.updateButtonsBasedOnCheckedSearches = function ( ) {
	var count = 0;
	$( ".search-checkbox" ).each( function() {
		if( $( this ).is( ":checked" ) ) {
			count++;
		}
	});				
	if( count < 2 ) { 
		disableButtons(); 
	} else { 
		enableButtons(); 
	}
	//  The following function is only on the page for admin so an exception will occur for non admin
	try {
		updateCopySearchesButtonFromSearchCheckboxes( count );
	}
	catch(err) {
	}					
}

//////////
window.disableButtons = function() {
	$( ".merge-button" ).attr("disabled", "disabled"); 
	//  show covering div
	$(".merge_button_disabled_cover_div_jq").show();
}
window.enableButtons = function() {
	$( ".merge-button" ).removeAttr("disabled"); 
	//  hide covering div
	$(".merge_button_disabled_cover_div_jq").hide();
}
//   Called by "onclick" on HTML element
window.viewMergedPeptides = function() {
	try {
		$( "form#viewMergedDataForm" ).attr("action", "mergedPeptide.do");
		$( "form#viewMergedDataForm" ).submit();
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
}

//   Called by "onclick" on HTML element
window.viewMergedProteins = function() {
	try {
		$( "form#viewMergedDataForm" ).attr("action", "mergedCrosslinkProtein.do");
		$( "form#viewMergedDataForm" ).submit();
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
}

//   Called by "onclick" on HTML element
window.viewMergedImage = function() {
	$( "form#viewMergedDataForm" ).attr("action", "image.do");
	$( "form#viewMergedDataForm" ).submit();
}

//   Called by "onclick" on HTML element
window.viewMergedStructure = function() {
	try {
		$( "form#viewMergedDataForm" ).attr("action", "structure.do");
		$( "form#viewMergedDataForm" ).submit();
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
}

//Called by "onclick" on HTML element
window.viewMergedQCPage = function() {
try {
	$( "form#viewMergedDataForm" ).attr("action", "qc.do");
	$( "form#viewMergedDataForm" ).submit();
} catch( e ) {
	reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
	throw e;
}
}

//   Called by "onclick" on HTML element
window.showSearchDetails = function( id ) {
	try {
		if( $( "table#search-details-" + id ).is( ":visible" ) ) {
			$( "table#search-details-" + id ).hide();
//			$( "a#search-details-link-" + id ).html( "[+]" );
			$( "a#search-details-link-" + id ).html( '<img src="images/icon-expand-small.png">' );
		} else {
			$( "table#search-details-" + id ).show();
//			$( "a#search-details-link-" + id ).html( "[-]" );
			$( "a#search-details-link-" + id ).html( '<img src="images/icon-collapse-small.png">' );
		}
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
}


export { initViewProjectPage }
