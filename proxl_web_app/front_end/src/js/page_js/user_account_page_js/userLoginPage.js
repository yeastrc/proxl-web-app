
//   userLoginPage.js

//  Javascript for the user login page login.jsp

//////////////////////////////////
// JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

import { createSpinner, destroySpinner, incrementSpinner, decrementSpinner }  from 'page_js/header_section_js_all_pages_main_pages/header_section_every_page/spinner.js';


var PAGE_CONSTANTS = {
		USER_LOGIN_PAGE_URL_FRAGMENT :  "user_loginPage",
		ERROR_MESSAGE_VERTICAL_MOVEMENT : 50 // number of pixels for moving error message when showing it. 
};

var pageGlobals = {
	tosKey : ""	
};

///////////////////

//  true while a sign-in request is in progress:  No second submit until the response arrives
var signInRequestInProgress = false;

/**
 * Allow a sign-in submit again
 */
var signInRequestDone = function() {
	signInRequestInProgress = false;
	$("#login_person_button").prop( "disabled", false );
};

///////////////////
window.loginPersonFormSubmit = function() {
	loginPerson();
};

////////////////////////////
window.loginPerson = function( params ) {
	if ( signInRequestInProgress ) {
		//  A sign-in request is in progress:  Ignore this submit (button click or Enter)
		return;  //  !!!  EARLY EXIT
	}
	var tosKeyToServer = "";
	if ( params ) {
		if ( params.tosKey ) {
			tosKeyToServer = params.tosKey;
		}
	}
	hideAllErrorMessages();
	var $username = $("#username");
	if ($username.length === 0) {
		throw Error("Unable to find input field for id 'username' ");
	}
	var $password = $("#password");
	if ($password.length === 0) {
		throw Error("Unable to find input field for id 'password' ");
	}
	var $inviteTrackingCode = $("#inviteTrackingCode");
	if ($inviteTrackingCode.length === 0) {
		throw Error("Unable to find input field for id 'inviteTrackingCode' ");
	}

	var username = $username.val();
	var password = $password.val();
	if ( username === "" ) {
		var $element = $("#error_message_username_required");
		showErrorMsg( $element );
		return;  //  !!!  EARLY EXIT
	} else if ( password === "" ) {
		var $element = $("#error_message_password_required");
		showErrorMsg( $element );
		return;  //  !!!  EARLY EXIT
	}
	// inviteTrackingCode Only when came to Sign in page from Clicking "Sign in" on Invite landing page
	var inviteTrackingCode = $inviteTrackingCode.val();


	createSpinner();				// create spinner - External Function

	
	var requestData = {
			username : username,
			password : password,
			return_tos : "true",
			tos_key : tosKeyToServer,
			inviteTrackingCode : inviteTrackingCode
	};
	var _URL = "services/user/login";

	signInRequestInProgress = true;
	$("#login_person_button").prop( "disabled", true );

	// var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(data) {
			try {
				loginComplete(requestData, data);
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		},
		failure : function(errMsg) {
			signInRequestDone();
			var $element = $("#error_message_system_error");
			showErrorMsg( $element );
//			alert(errMsg);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			signInRequestDone();
			var $element = $("#error_message_system_error");
			showErrorMsg( $element );
//			handleAJAXError(jqXHR, textStatus, errorThrown);
			// alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
			// textStatus: " + textStatus );
		}
	});
};

//////////
window.loginComplete = function(requestData, responseData) {
	if ( ! responseData.status ) {
		//  User not logged in if status not true

		signInRequestDone();

		if ( responseData.signInTemporarilyUnavailable ) {

			destroySpinner();  // Destroy Spinner - External Function

			//  Refused by a sign-in limit.  Show when to try again:  seconds below 60, otherwise minutes (rounded up)
			var seconds = responseData.signInTemporarilyUnavailable_Seconds;
			var number = seconds;
			var unit = ( seconds === 1 ) ? "second" : "seconds";
			if ( seconds >= 60 ) {
				number = Math.ceil( seconds / 60 );
				unit = ( number === 1 ) ? "minute" : "minutes";
			}
			var $element = $("#error_message_signin_temporarily_unavailable");
			$element.find(".signin_temporarily_unavailable_number_jq").text( number );
			$element.find(".signin_temporarily_unavailable_unit_jq").text( unit );
			showErrorMsg( $element );
		} else if ( responseData.termsOfServiceAcceptanceRequired ) {

			destroySpinner();  // Destroy Spinner - External Function
				
			$("#terms_of_service_modal_dialog_overlay_background").show();
			$("#terms_of_service_overlay_div").show();
			pageGlobals.tosKey = responseData.termsOfServiceKey;
			$("#terms_of_service_acceptance_required_text").html( responseData.termsOfServiceText );
		} else if ( responseData.invalidUserOrPassword ) {

			destroySpinner();  // Destroy Spinner - External Function
				
			var $element = $("#error_message_username_or_password_invalid");
			showErrorMsg( $element );
		} else if ( responseData.disabledUser ) {

			destroySpinner();  // Destroy Spinner - External Function
				
			var $element = $("#error_message_user_disabled");
			showErrorMsg( $element );
		} else if ( responseData.noProxlAccount ) {

			destroySpinner();  // Destroy Spinner - External Function
				
			var $element = $("#error_message_no_proxl_account");
			showErrorMsg( $element );
		} else if ( responseData. invalidInviteTrackingCode ) {
			//  Invalid Invite tracking code so redirect to requestedURL to process tracking code and show error msg
			var $requestedURL = $("#requestedURL");
			if ($requestedURL.length === 0) {

				destroySpinner();  // Destroy Spinner - External Function
				
				throw Error("Unable to find input field for id 'requestedURL' ");
			}
			var requestedURL = $requestedURL.val();
			if ( requestedURL !== "" ) {
				window.location.href = requestedURL;
				return;
			}
		} else {

			destroySpinner();  // Destroy Spinner - External Function
				
			var $element = $("#error_message_system_error");
			showErrorMsg( $element );
		}

		destroySpinner();  // Destroy Spinner - External Function
				
		return;
	} 
//	alert("Person login complete: ");
	var $useDefaultURL = $("#useDefaultURL");
	if ($useDefaultURL.length === 0) {

		destroySpinner();  // Destroy Spinner - External Function
				
		throw Error("Unable to find input field for id 'useDefaultURL' ");
	}
	var useDefaultURL = $useDefaultURL.val();
	var $defaultURL = $("#defaultURL");
	if ($defaultURL.length === 0) {

		destroySpinner();  // Destroy Spinner - External Function
				
		throw Error("Unable to find input field for id 'defaultURL' ");
	}
	var defaultURL = $defaultURL.val();
	if ( useDefaultURL ) {
		if (  defaultURL === undefined ||  defaultURL === null || defaultURL === "" ) {

			destroySpinner();  // Destroy Spinner - External Function
				
			throw Error("input field for id 'defaultURL' is empty");
		}
		window.location.href = defaultURL;
		return;
	} else {
		var $requestedURL = $("#requestedURL");
		if ($requestedURL.length === 0) {

			destroySpinner();  // Destroy Spinner - External Function
				
			throw Error("Unable to find input field for id 'requestedURL' ");
		}
		var requestedURL = $requestedURL.val();
		if ( requestedURL !== "" ) {
			window.location.href = requestedURL;
			return;
		}
		var currentURL = document.URL;
		var currentURLindexOfLoginPageURLFragment = currentURL.indexOf( PAGE_CONSTANTS.USER_LOGIN_PAGE_URL_FRAGMENT );
		if ( currentURLindexOfLoginPageURLFragment != -1 ) {
			//  current URL is the login URL so have to change the URL so send user to default URL
			window.location.href = defaultURL;
			return;
		}
		//  Were forwarded to the login page with some other URL on the browser address bar so just reload the page
		//  reload current URL
		window.location.reload(true);
		//  WAS
//		window.location.href = currentURL; 
		return;
	}
	
	destroySpinner();  // Destroy Spinner - External Function
				
	//  WAS
//	var $defaultURL = $("#defaultURL");
//
//	if ($defaultURL.length === 0) {
//
//		throw Error("Unable to find input field for id 'defaultURL' ");
//	}
//
//
//	var defaultURL = $defaultURL.val();
//	
//	if ( defaultURL !== "" ) {
//		
//		document.location.href = defaultURL;
//		
//		return;
//	}
//
//	throw Error("requestedURL and defaultURL are both empty");
};


//////////////////
window.initLoginPage = function() {
	$(document).click( function(eventObject) {
		try {
			hideAllErrorMessages();
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});
	$("#username").focus();
	$("#terms_of_service_acceptance_yes_button").click( function(eventObject) {
//		var clickThis = this;
		try {
			loginPerson( { tosKey : pageGlobals. tosKey } );
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
		return false;  // stop click bubble up.
	});
	$("#terms_of_service_acceptance_no_button").click( function(eventObject) {
//		var clickThis = this;
		try {
			$("#terms_of_service_modal_dialog_overlay_background").hide();
			$("#terms_of_service_overlay_div").hide();
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
		return false;  // stop click bubble up.
	});
};

///////////////
$(document).ready(function() {
	try {
		initLoginPage();
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
});
