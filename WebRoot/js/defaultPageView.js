"use strict";




//Constructor

var DefaultPageView = function() {


	var SAVE_DEFAULT_PAGE_VIEW_MESSAGE_CONSTANTS = {

			MESSAGE_HIDE_DELAY : 2000,  // in Milliseconds
			MESSAGE_FADEOUT_OPTIONS : { duration : 800 },  // duration in Milliseconds 

			MESSAGE_SET_TIMEOUT_DATA_KEY : "SAVE_DEFAULT_PAGE_VIEW_MESSAGE_TIMEOUT_DATA_KEY"

	};



/////////////

//	this.initDefaultPageView = function () {
//
//	};


	//  Called from "onchange" and from CutoffProcessingCommonCode code when cutoffs change
	
	this.searchFormChanged_ForDefaultPageView = function () {

		$("#setDefaultPageViewButton").prop("disabled",true);
		$("#setDefaultPageViewButtonDisabledOverlay").show();
	};


	//  Called from Image and Structure when the Update button has been pressed
	
	this.searchFormUpdateButtonPressed_ForDefaultPageView = function () {


		console.log( "searchFormUpdateButtonPressed_ForDefaultPageView()" );

		$("#setDefaultPageViewButton").prop("disabled",false);
		$("#setDefaultPageViewButtonDisabledOverlay").hide();
	};



	//  Called from 'Save As Default' button

	this.saveOrUpdateDefaultPageView = function( params ) {

		var objectThis = this;
		
		if ( ! params ) {

			console.log( "params cannot be empty" );
			throw "saveOrUpdateDefaultPageView(params): params cannot be empty"; 
		}

		var clickedThis = params.clickedThis;

		if ( ! clickedThis ) {

			console.log( "clickedThis cannot be empty" );
			throw "saveOrUpdateDefaultPageView(params): clickedThis cannot be empty"; 
		}

		var $clickedThis = $( clickedThis );


		var page_JS_Object = params.page_JS_Object;

		if ( ! page_JS_Object ) {

			console.log( "page_JS_Object cannot be empty" );
			throw "saveOrUpdateDefaultPageView(params): page_JS_Object cannot be empty"; 
		}


		if ( ! page_JS_Object.getQueryJSONString ) {

			console.log( "page_JS_Object.getQueryJSONString must exist and must be a function" );
			throw "saveOrUpdateDefaultPageView(params): page_JS_Object.getQueryJSONString must exist and must be a function"; 
		}

		var searchId = params.searchId;

		if ( ! searchId ) {

			searchId = $clickedThis.attr("data-search_id");
		}

		if ( ! searchId ) {

			console.log( "searchId cannot be empty" );
			throw "saveOrUpdateDefaultPageView(params): searchId must be a parameter or 'data-search_id' must be an attribute on the button "; 
		}
		
		
		var pageQueryJSONString = null;

		try {
			
			pageQueryJSONString = page_JS_Object.getQueryJSONString();
			
		} catch( e ) {
			console.log( "calling page_JS_Object.getQueryJSON() throw an exception" );
			throw "saveOrUpdateDefaultPageView(params): calling page_JS_Object.getQueryJSON() throw an exception"; 
		}
		

		var successCallback = params.successCallback;


		var pageUrl = this.getDefaultPageViewURL(); 

		var requestData = { searchId: searchId, pageUrl: pageUrl, pageQueryJSON : pageQueryJSONString };


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
						clickedThis : clickedThis,
						successCallback : successCallback


				};

				objectThis.saveOrUpdateDefaultPageViewProcessAjaxResponse( saveOrUpdateDefaultPageViewProcessAjaxResponseParams );

			},
			failure: function(errMsg) {
				handleAJAXFailure( errMsg );
			},
			error: function(jqXHR, textStatus, errorThrown) {	

				handleAJAXError( jqXHR, textStatus, errorThrown );
			}
		});

	};





	this.saveOrUpdateDefaultPageViewProcessAjaxResponse = function( params ) {

		var objectThis = this;

		var clickedThis = params.clickedThis;
		var successCallback = params.successCallback;

		var $clickedThis = $( clickedThis );


		var $current_url_saved_as_default_page_view_success = $("#current_url_saved_as_default_page_view_success");

//		var $current_url_saved_as_default_page_view_success_inner_div = $("#current_url_saved_as_default_page_view_success_inner_div");


//		if ( $current_url_saved_as_default_page_view_success.length > 0
//		&& $current_url_saved_as_default_page_view_success_inner_div.length > 0 ) {

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

//			$current_url_saved_as_default_page_view_success.show();


			var $element = $current_url_saved_as_default_page_view_success;


			var clearMsg = true;

			if ( clearMsg === undefined ) { //  If no value passed, default to true;

				clearMsg = true;
			}

			this.clearSaveOrUpdateDefaultPageViewMsg( $element );

			$element.show();

			var animateNewTop = offsetTop__SaveAsDefaultButton - height__current_url_saved_as_default_page_view_success_div - 10;

			$element.animate( { top: animateNewTop }, { duration: 750 } );

			if ( clearMsg ) {

				var timerId = setTimeout( function() {

					objectThis.clearSaveOrUpdateDefaultPageViewMsg( $element, true /* fade */ );

				}, SAVE_DEFAULT_PAGE_VIEW_MESSAGE_CONSTANTS.MESSAGE_HIDE_DELAY );

				$element.data( SAVE_DEFAULT_PAGE_VIEW_MESSAGE_CONSTANTS.MESSAGE_SET_TIMEOUT_DATA_KEY, timerId );
			}

		}


		if ( successCallback ) {
			successCallback();
		}

	};




	//	Called from "onclick" in defaultPageViewFragment.jsp
	this.hideSaveOrUpdateDefaultPageViewMsg = function ( clickedThis ) {

		//		var $clickedThis = $( clickedThis );

		var $current_url_saved_as_default_page_view_success = $("#current_url_saved_as_default_page_view_success");

		this.clearSaveOrUpdateDefaultPageViewMsg( $current_url_saved_as_default_page_view_success, false /* fadeErrorMsg */ );
	};



	this.clearSaveOrUpdateDefaultPageViewMsg = function ( $element, fadeErrorMsg ) {

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



	this.getDefaultPageViewURL = function(  ) {

		var href = window.location.href;

		return href;
	};


};

//  Object from class

var defaultPageView = new DefaultPageView(); 


