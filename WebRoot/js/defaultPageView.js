"use strict";



var SAVE_DEFAULT_PAGE_VIEW_MESSAGE_CONSTANTS = {
	
	MESSAGE_HIDE_DELAY : 2000,  // in Milliseconds
	MESSAGE_FADEOUT_OPTIONS : { duration : 800 },  // duration in Milliseconds 
	
	MESSAGE_SET_TIMEOUT_DATA_KEY : "SAVE_DEFAULT_PAGE_VIEW_MESSAGE_TIMEOUT_DATA_KEY"
	
};



var _DefaultPageView__formDataForLoadedData = null;



/////////////

function initDefaultPageView() {

	saveCurrentSearchFormValues_ForDefaultPageView();
}



function saveCurrentSearchFormValues_ForDefaultPageView() {

	_DefaultPageView__formDataForLoadedData = getValuesFromForm();

}



function searchFormChanged_ForDefaultPageView() {


	if ( _DefaultPageView__formDataForLoadedData === null ) {
		
		return; //  not initialized yet
	}

	var currentFormDataObject = getValuesFromForm();
	
	if ( currentFormDataObject === undefined || currentFormDataObject === null ) {
		
		
		return;  // EXIT EARLY
	}

	if ( ! compareFormDataObject_ForDefaultPageView( currentFormDataObject, _DefaultPageView__formDataForLoadedData ) ) {
		
//		values are different

		console.log( "searchFormChanged_ForDefaultPageView(): form values are changed from saved" );

		$("#mergedImageSaveOrUpdateDefaultPageView").prop("disabled",true);

	} else {

//		values are same

		console.log( "searchFormChanged_ForDefaultPageView(): form values are same as saved" );

		$("#mergedImageSaveOrUpdateDefaultPageView").prop("disabled",false);


	}


	var z = 0;

}



function searchFormUpdateButtonPressed_ForDefaultPageView() {


	console.log( "searchFormUpdateButtonPressed()" );

	$("#mergedImageSaveOrUpdateDefaultPageView").prop("disabled",false);

	
}





var saveOrUpdateDefaultPageView = function( params ) {
	
	var clickedThis = this;
	
	if ( ! params ) {
		
		console.log( "params cannot be empty" );
		throw "saveOrUpdateDefaultPageView(params): params cannot be empty"; 
	}
	
	
	var searchId = params.searchId;
	
	if ( ! searchId ) {
		
		console.log( "searchId cannot be empty" );
		throw "saveOrUpdateDefaultPageView(params): searchId cannot be empty"; 
	}
	
	
	var pageName = null;
	var pageUrl = null;

	var successCallback = null;
	
	if ( params ) {
		
		pageName = params.pageName;
		pageUrl = params.pageUrl;
		successCallback = params.successCallback;
		
		if ( params.clickedThis ) {
			
			clickedThis = params.clickedThis;
		}
	}
	
	
	if ( ! pageUrl ) {
		
		pageUrl = getDefaultPageViewURL();  //  pageUrl is not provided so get it.
	}
	
    var requestData = { searchId: searchId, pageName : pageName, pageUrl: pageUrl };

	
	if ( pageName ) {

		requestData.pageName = requestData;  //  pageName is provided so pass it to server.
	}
	
	
	var _URL = contextPathJSVar + "/services/defaultPageView/saveOrUpdateDefaultPageView";

	$.ajax({
	        type: "POST",
	        url: _URL,
	        data: requestData,
	        dataType: "json",
	        success: function( responseData )	{
	        	
	        	var saveOrUpdateDefaultPageViewProcessAjaxResponseParams = {
	        			
	        			responseData : responseData,
	        			
	        			pageUrl : pageUrl,
	        			pageName : pageName,
	        			clickedThis : clickedThis,
	        			successCallback : successCallback
	        			
	        			
	        	};

	        	saveOrUpdateDefaultPageViewProcessAjaxResponse( saveOrUpdateDefaultPageViewProcessAjaxResponseParams );

											
			},
	        failure: function(errMsg) {
	        	handleAJAXFailure( errMsg );
	        },
	        error: function(jqXHR, textStatus, errorThrown) {	
	        	
				handleAJAXError( jqXHR, textStatus, errorThrown );
			}
	  });
	
};



//////////

var saveOrUpdateDefaultPageViewProcessAjaxResponse = function( params ) {
	
	
	var clickedThis = params.clickedThis;
	var successCallback = params.successCallback;
	
	var $clickedThis = $( clickedThis );
	
	
	var $current_url_saved_as_default_page_view_success = $("#current_url_saved_as_default_page_view_success");
	
//	var $current_url_saved_as_default_page_view_success_inner_div = $("#current_url_saved_as_default_page_view_success_inner_div");
	
	
//	if ( $current_url_saved_as_default_page_view_success.length > 0
//			&& $current_url_saved_as_default_page_view_success_inner_div.length > 0 ) {
		
	if ( $current_url_saved_as_default_page_view_success.length > 0 ) {
			

		// Position dialog over clicked delete icon
		
		
		//  get position of clicked button
		
		var offset__ClickedSaveAsDefaultButton = $clickedThis.offset();
		var offsetTop__SaveAsDefaultButton = offset__ClickedSaveAsDefaultButton.top;
		var offsetLeft__SaveAsDefaultButton = offset__ClickedSaveAsDefaultButton.left;
		var width__SaveAsDefaultButton = $clickedThis.outerWidth( true /* [includeMargin ] */ );;
		
		//  adjust vertical position of dialog 
		
		
		var height__current_url_saved_as_default_page_view_success_div = $current_url_saved_as_default_page_view_success.outerHeight( true /* [includeMargin ] */ );
		
		var positionAdjustTop = offsetTop__SaveAsDefaultButton - ( height__current_url_saved_as_default_page_view_success_div / 2 );
		
		$current_url_saved_as_default_page_view_success.css( "top", positionAdjustTop );

		var width__current_url_saved_as_default_page_view_success_div = $current_url_saved_as_default_page_view_success.outerWidth( true /* [includeMargin ] */ );

		var positionAdjustLeft = offsetLeft__SaveAsDefaultButton + ( width__SaveAsDefaultButton / 2 ) 
				- ( width__current_url_saved_as_default_page_view_success_div / 2 );
		
		if ( positionAdjustLeft < 5 ) {
			
			positionAdjustLeft = 5;
		}
		
		$current_url_saved_as_default_page_view_success.css( "left", positionAdjustLeft );
		
//		$current_url_saved_as_default_page_view_success.show();
		
		
		var $element = $current_url_saved_as_default_page_view_success;
		

		var clearMsg = true;
		
		if ( clearMsg === undefined ) { //  If no value passed, default to true;

			clearMsg = true;
		}

		clearSaveOrUpdateDefaultPageViewMsg( $element );

		$element.show();

		var animateNewTop = offsetTop__SaveAsDefaultButton - height__current_url_saved_as_default_page_view_success_div - 10;

		$element.animate( { top: animateNewTop }, { duration: 750 } );

		if ( clearMsg ) {

			var timerId = setTimeout( function() {

				clearSaveOrUpdateDefaultPageViewMsg( $element, true /* fade */ );

			}, SAVE_DEFAULT_PAGE_VIEW_MESSAGE_CONSTANTS.MESSAGE_HIDE_DELAY );

			$element.data( SAVE_DEFAULT_PAGE_VIEW_MESSAGE_CONSTANTS.MESSAGE_SET_TIMEOUT_DATA_KEY, timerId );
		}
		
	}
	
	
	if ( successCallback ) {
		successCallback();
	}
	
};



////////////

function showSaveOrUpdateDefaultPageViewMsg( $element ) {

	var clearMsg = true;
	
	if ( clearMsg === undefined ) { //  If no value passed, default to true;

		clearMsg = true;
	}

	clearSaveOrUpdateDefaultPageViewMsg( $element );

	$element.show();

	var height = $element.outerHeight( true /* includeMargin */ );

	var animateMovement = -height;

	$element.animate( { top: animateMovement }, { duration: 500 } );

	if ( clearMsg ) {

		var timerId = setTimeout( function() {

			clearSaveOrUpdateDefaultPageViewMsg( $element, true /* fade */ );

		}, SAVE_DEFAULT_PAGE_VIEW_MESSAGE_CONSTANTS.MESSAGE_HIDE_DELAY );

		$element.data( SAVE_DEFAULT_PAGE_VIEW_MESSAGE_CONSTANTS.MESSAGE_SET_TIMEOUT_DATA_KEY, timerId );
	}
};


function hideSaveOrUpdateDefaultPageViewMsg( clickedThis ) {
	
//	var $clickedThis = $( clickedThis );
	
	var $current_url_saved_as_default_page_view_success = $("#current_url_saved_as_default_page_view_success");
	
	clearSaveOrUpdateDefaultPageViewMsg( $current_url_saved_as_default_page_view_success, false /* fadeErrorMsg */ );
}



function clearSaveOrUpdateDefaultPageViewMsg( $element, fadeErrorMsg ) {

	$element.stop( true /* [clearQueue ] */ /*  [, jumpToEnd ] */ );

	var setTimeoutId = $element.data( SAVE_DEFAULT_PAGE_VIEW_MESSAGE_CONSTANTS.MESSAGE_SET_TIMEOUT_DATA_KEY );

	if ( setTimeoutId ) {

		clearTimeout( setTimeoutId );
	}

	$element.data( SAVE_DEFAULT_PAGE_VIEW_MESSAGE_CONSTANTS.MESSAGE_SET_TIMEOUT_DATA_KEY, null );



	if ( fadeErrorMsg ) {

		$element.fadeOut( 400 /* [duration ] */  /* [, complete ] */ );  //  OR ( options_object )  duration (default: 400)

	} else {

		$element.hide();
	}

};

//////////

var getDefaultPageViewURL = function(  ) {

	var href = window.location.href;
	
	return href;
};





/////////////////////////////////////

//////////!!!!!!   For now, this assumes there are no sub-objects

//  Returns true if same, false if not

function compareFormDataObject_ForDefaultPageView( currentFormDataObject, formDataObjectForLoadedData ) {

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


