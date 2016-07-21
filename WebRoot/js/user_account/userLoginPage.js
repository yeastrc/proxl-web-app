
//   userLoginPage.js


//  Javascript for the user login page login.jsp

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";


var PAGE_CONSTANTS = {
		
		USER_LOGIN_PAGE_URL_FRAGMENT :  "user_loginPage",
		
		ERROR_MESSAGE_VERTICAL_MOVEMENT : 50 // number of pixels for moving error message when showing it. 
		
};


///////////////////

var loginPersonFormSubmit = function() {
	
	loginPerson();
};

////////////////////////////

var loginPerson = function(clickThis, eventObject) {

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
			password : password
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

//		private boolean invalidUserOrPassword = false;
//		private boolean disabledUser = false;

		if ( responseData.invalidUserOrPassword ) {
			
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


//function showErrorMsg( $element ) {
//	
//	$element.css( { top: PAGE_CONSTANTS.ERROR_MESSAGE_VERTICAL_MOVEMENT } );
//	
//	$element.show();
//	
//	$element.animate( { top: 0 }, { duration: 500 } );
//
//};
//
//
//
///////////////
//
//function hideAllErrorMessages() {
//
//	var $error_message_container_jq = $(".error_message_container_jq");
//
//	$error_message_container_jq.hide();
//}



//////////////////

function initLogin() {
	
	
	$(document).click( function(eventObject) {
	
		hideAllErrorMessages();
	});
	
	$("#username").focus();

//	var $login_person_button  = $("#login_person_button");
//
//	$login_person_button.click( function(eventObject) {
//
//		var clickThis = this;
//		loginPerson(clickThis, eventObject);	
//		
//		return false;  // stop click bubble up.
//	});
	

};
	
	
///////////////

$(document).ready(function() {

	initLogin();

});
