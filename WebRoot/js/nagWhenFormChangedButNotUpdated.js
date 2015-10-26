
//  nagWhenFormChangedButNotUpdated.js

//  Javascript for nagging the user when they change the form and don't click the "update" button

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";


////////   Disable the nag since found to be annoying

var _DISABLE_NAG = true;



var _SEARCH_FORM_CHANGED_NAG_TIMEOUT = 20000; // milliseconds




var _NAG__formDataForLoadedData = null;



var _searchFormChangedNagTimer = null;




//$(document).ready(function()  { 
//
////	initNagUser();
//
//});

/////////////

function initNagUser() {

	attachNagUserOverlayClickHandlers();

	saveCurrentSearchFormValues_ForNag();
}




function searchFormUpdateButtonPressed() {


	console.log( "searchFormUpdateButtonPressed()" );


	if ( _searchFormChangedNagTimer ) {
		//  Existing timeout so clear it
		clearTimeout( _searchFormChangedNagTimer );
		//  clear the timerId
		_searchFormChangedNagTimer = null;
	}
	
}


function searchFormChanged() {
	
	
	searchFormChanged_ForNag();
}

function searchFormChanged_ForNag() {

	if ( _DISABLE_NAG ) {

		return;  //  EARLY RETURN    Disable the Nag processing
	}
	

	if ( _NAG__formDataForLoadedData === null ) {
		
		return; //  not initialized yet
	}
	
//	Compare current form values to saved form values.
//	If they are different, start the timer for the nag overlay.
//	If they are the same, clear the timer for the nag overlay.

	

	var currentFormDataObject = getValuesFromForm();

	if ( ! compareFormDataObject( currentFormDataObject, _NAG__formDataForLoadedData ) ) {
		
//		values are different

		console.log( "searchFormChanged(): form values are changed from saved" );


		if ( ! _searchFormChangedNagTimer ) {
			
			//  create new timeout
			_searchFormChangedNagTimer = setTimeout(function() {

				//  clear the timerId
				_searchFormChangedNagTimer = null;
				
				openNagUserOverlay();

//				alert("Form changed but update button not clicked");

			}, _SEARCH_FORM_CHANGED_NAG_TIMEOUT );
		}
		
	} else {

//		values are same

		console.log( "searchFormChanged(): form values are same as saved" );


		if ( _searchFormChangedNagTimer ) {
			//  Existing timeout so clear it
			clearTimeout( _searchFormChangedNagTimer );
			//  clear the timerId
			_searchFormChangedNagTimer = null;
		}

	}


	var z = 0;

}



/////////////////////////////////////

//////////   !!!!!!   For now, this assumes there are no sub-objects

//  Returns true if same

function compareFormDataObject( currentFormDataObject, formDataObjectForLoadedData ) {


	var formDataKeys = Object.keys( formDataObjectForLoadedData );

	for ( var formDataKeysIndex = 0; formDataKeysIndex < formDataKeys.length; formDataKeysIndex++ ) {

		var formDataKey = formDataKeys[ formDataKeysIndex ];

		var formDataItemForLoadedData = formDataObjectForLoadedData[ formDataKey ];
		var formDataItemForCurrent = currentFormDataObject[ formDataKey ];

		if ( Array.isArray( formDataItemForLoadedData ) ) {

			if ( ! Array.isArray( formDataItemForCurrent ) ) {
				
				return false;
			}
			
			var arrayCompare = compareFormDataArray( formDataItemForCurrent, formDataItemForLoadedData );
			
			if ( ! arrayCompare ) {
				
				return false;
			}

		} else {

//			compare the values

			if ( formDataItemForLoadedData !== formDataItemForCurrent ) {

				return false;  //  EARLY EXIT
			}
		}


	}

	return true;

}


/////////////////////

//Returns true if same

function compareFormDataArray( currentFormDataArray, formDataArrayForLoadedData ) {

	if ( currentFormDataArray.length !== formDataArrayForLoadedData.length ) {

		return false;
	}

	for ( var currentFormDataArrayIndex = 0; currentFormDataArrayIndex < currentFormDataArray.length; currentFormDataArrayIndex++ ) {

		var formDataItemForLoadedData = formDataArrayForLoadedData[ currentFormDataArrayIndex ];
		var formDataItemForCurrent = currentFormDataArray[ currentFormDataArrayIndex ];

		if ( Array.isArray( formDataItemForLoadedData ) ) {
			
			if ( ! Array.isArray( formDataItemForCurrent ) ) {
				
				return false;
			}

			var currentFormDataArraySubArray = currentFormDataArray[ currentFormDataArrayIndex ];
			var formDataArrayForLoadedDataSubArray = formDataArrayForLoadedData[ currentFormDataArrayIndex ];
			
			var arrayCompare = compareFormDataArray( currentFormDataArraySubArray, formDataArrayForLoadedDataSubArray );
			
			if ( ! arrayCompare ) {
				
				return false;
			}

		} else {

//			compare the values

			if ( formDataItemForLoadedData !== formDataItemForCurrent ) {

				return false;  //  EARLY EXIT
			}
		}


	}

	return true;

}






function saveCurrentSearchFormValues_ForNag() {

	_NAG__formDataForLoadedData = getValuesFromForm();

}




///////////////////////////////////////////////////////////////////////////


///  Overlay general processing

var openNagUserOverlay = function (  ) {

	$("#nag-user-modal-dialog-overlay-background").show();

	//  position: fixed; height: 100% covers the whole document
//	var docHeight = $(document).height();
//
//	$("#nag-user-modal-dialog-overlay-background").css({ height: docHeight });
	
	$(".nag-user-overlay-div").show();
	
	//  scroll the window to the top left
	
	var $window = $(window);
	$window.scrollTop( 0 );
	$window.scrollLeft( 0 );
};

///  Overlay general processing

var closeNagUserOverlay = function (  ) {

	$("#nag-user-modal-dialog-overlay-background").hide();
	$(".nag-user-overlay-div").hide();

	$("#nag-user-view-spectra-overlay").empty();
};

///////////////////////////////////////////////////////////////////////////

///  Attach Overlay Click handlers


var attachNagUserOverlayClickHandlers = function (  ) {

	var $view_spectra_overlay_X_for_exit_overlay = $(".nag-user-overlay-X-for-exit-overlay");
	
	$view_spectra_overlay_X_for_exit_overlay.click( function( eventObject ) {

		closeNagUserOverlay();
	} );

	$("#nag-user-modal-dialog-overlay-background").click( function( eventObject ) {

		closeNagUserOverlay();
	} );

//	$(".view-spectra-overlay-div").click( function( eventObject ) {
//
//		closeNagUserOverlay();
//	} );

//	$(".error-message-ok-button").click( function( eventObject ) {
//
//		closeNagUserOverlay();
//	} );

	
	
	
//	$("#view-spectra-overlay-div").click( function( eventObject ) {
//
//		return false;
//	} );

};


