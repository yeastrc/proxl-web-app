
//   userLoginPage.js


//  Javascript for the user login page login.jsp

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";


var PAGE_CONSTANTS = {
		
		USER_LOGIN_PAGE_URL_FRAGMENT :  "user_loginPage",
		
		ERROR_MESSAGE_VERTICAL_MOVEMENT : 50 // number of pixels for moving error message when showing it. 
		
};

var pageGlobals = {
		
	tosKey : ""	
		
};

///////////////////

var loginPersonFormSubmit = function() {
	
	loginPerson();
};

////////////////////////////

var loginPerson = function( params ) {
	
	var tosKeyToServer = "";
	
	if ( params ) {
		
		if ( params.tosKey ) {
			
			tosKeyToServer = params.tosKey;
		}
	}
	
	
	hideAllErrorMessages();
	
	
	

	var $username = $("#username");

	if ($username.length === 0) {

		throw "Unable to find input field for id 'username' ";
	}

	var $password = $("#password");

	if ($password.length === 0) {

		throw "Unable to find input field for id 'password' ";
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
	

	var requestData = {
			username : username,
			password : password,
			return_tos : "true",
			tos_key : tosKeyToServer
	};

	var _URL = contextPathJSVar + "/services/user/login";

	// var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(data) {

			loginComplete(requestData, data);
		},
		failure : function(errMsg) {
			var $element = $("#error_message_system_error");
			
			showErrorMsg( $element );
			
//			alert(errMsg);
			
		},
		error : function(jqXHR, textStatus, errorThrown) {

			
			var $element = $("#error_message_system_error");
			
			showErrorMsg( $element );
			
//			handleAJAXError(jqXHR, textStatus, errorThrown);

			// alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
			// textStatus: " + textStatus );
		}
	});
};

//////////

var loginComplete = function(requestData, responseData) {

	if ( ! responseData.status ) {

		//  User not logged in if status not true
		
		if ( responseData.termsOfServiceAcceptanceRequired ) {

			$("#terms_of_service_modal_dialog_overlay_background").show();
			$("#terms_of_service_overlay_div").show();
			
			pageGlobals.tosKey = responseData.termsOfServiceKey;
			
			$("#terms_of_service_acceptance_required_text").html( responseData.termsOfServiceText );
			
		} else if ( responseData.invalidUserOrPassword ) {
			
			var $element = $("#error_message_username_or_password_invalid");
			
			showErrorMsg( $element );
			

		} else if ( responseData.disabledUser ) {
				
			var $element = $("#error_message_user_disabled");
			
			showErrorMsg( $element );
				
		} else {
			
			var $element = $("#error_message_system_error");
			
			showErrorMsg( $element );
		}
		
		return;
		
	} 

//	alert("Person login complete: ");
	
	var $useDefaultURL = $("#useDefaultURL");

	if ($useDefaultURL.length === 0) {

		throw "Unable to find input field for id 'useDefaultURL' ";
	}
	
	var useDefaultURL = $useDefaultURL.val();
	

	var $defaultURL = $("#defaultURL");

	if ($defaultURL.length === 0) {

		throw "Unable to find input field for id 'defaultURL' ";
	}


	var defaultURL = $defaultURL.val();
	
	
	if ( useDefaultURL ) {
		

		
		if (  defaultURL === undefined ||  defaultURL === null || defaultURL === "" ) {
			
			throw "input field for id 'defaultURL' is empty";
		}
			
		window.location.href = defaultURL;

		return;
		
		
	} else {

		var $requestedURL = $("#requestedURL");

		if ($requestedURL.length === 0) {

			throw "Unable to find input field for id 'requestedURL' ";
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
	
	//  WAS
	
//	var $defaultURL = $("#defaultURL");
//
//	if ($defaultURL.length === 0) {
//
//		throw "Unable to find input field for id 'defaultURL' ";
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
//	throw "requestedURL and defaultURL are both empty";

};



//////////////////

function initLoginPage() {
	
	
	$(document).click( function(eventObject) {
	
		hideAllErrorMessages();
	});
	
	$("#username").focus();

	
	$("#terms_of_service_acceptance_yes_button").click( function(eventObject) {

//		var clickThis = this;

		loginPerson( { tosKey : pageGlobals. tosKey } );
			
		return false;  // stop click bubble up.
	});
	
	$("#terms_of_service_acceptance_no_button").click( function(eventObject) {

//		var clickThis = this;

		$("#terms_of_service_modal_dialog_overlay_background").hide();
		$("#terms_of_service_overlay_div").hide();
			
		return false;  // stop click bubble up.
	});

};
	
	
///////////////

$(document).ready(function() {

	initLoginPage();

});
