
//   userResetPasswordPage.js


//  Javascript for the user reset email page resetPassword.jsp

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";


var PAGE_CONSTANTS = {
		
		ERROR_MESSAGE_VERTICAL_MOVEMENT : 50 // number of pixels for moving error message when showing it. 
		
};



///////////////////

var resetPasswordFormSubmit = function() {

	try {
		resetPassword();
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
};


////////////////////////////

var resetPassword = function(clickThis, eventObject) {

	hideAllErrorMessages();
	

	var $username = $("#username");

	if ($username.length === 0) {

		throw Error( "Unable to find input field for id 'username' " );
	}

	var $email = $("#email");

	if ($email.length === 0) {

		throw Error( "Unable to find input field for id 'email' " );
	}



	var username = $username.val();

	var email = $email.val();
	

	if ( username === "" && email === "" ) {
			
		var $element = $("#error_message_username_or_email_required");
		
		showMsg( $element );
			
		return;  //  !!!  EARLY EXIT
	}
	

	if ( username !== "" && email !== "" ) {
			
		var $element = $("#error_message_username_and_email_both_populated");
		
		showMsg( $element );
			
		return;  //  !!!  EARLY EXIT
	}

	var requestData = {
			username : username,
			email : email
	};

	var _URL = contextPathJSVar + "/services/user/resetPasswordGenEmail";

	// var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(data) {

			try {
				resetPasswordComplete(requestData, data);
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		},
		failure : function(errMsg) {
			var $element = $("#error_message_system_error");
			
			showMsg( $element );
			
//			alert(errMsg);
			
		},
		error : function(jqXHR, textStatus, errorThrown) {

			
			var $element = $("#error_message_system_error");
			
			showMsg( $element );
			
//			handleAJAXError(jqXHR, textStatus, errorThrown);

			// alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
			// textStatus: " + textStatus );
		}
	});
};

//////////

var resetPasswordComplete = function(requestData, responseData) {

	if ( ! responseData.status ) {

//		private boolean invalidUserOrPassword = false;
//		private boolean disabledUser = false;

		if ( responseData.invalidUsernameOrEmail ) {
			
			var id = null;
			
			if ( requestData.username !== ""  ) {
				
				id = "error_message_username_invalid";
			} else {
				
				id = "error_message_email_invalid";
			}
			
			var $element = $( "#" + id );
			
			
			
			
			showMsg( $element );
			

		} else if ( responseData.disabledUser ) {
				
			var $element = $("#error_message_user_disabled");
			
			showMsg( $element );
				
		} else {
			
			var $element = $("#error_message_system_error");
			
			showMsg( $element );
		}
		
		return;
		
	} 
	
	var $element = $("#success_message_system_success");
	
	showMsg( $element );


//	alert("Person login complete: ");
//
//	var $requestedURL = $("#requestedURL");
//
//	if ($requestedURL.length === 0) {
//
//		throw Error( "Unable to find input field for id 'requestedURL' " );
//	}
//
//
//	var requestedURL = $requestedURL.val();
//	
//	if ( requestedURL !== "" ) {
//		
//		document.location.href = requestedURL;
//		
//		return;
//	}
//
//	
//	var $defaultURL = $("#defaultURL");
//
//	if ($defaultURL.length === 0) {
//
//		throw Error( "Unable to find input field for id 'defaultURL' " );
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
//	throw Error( "requestedURL and defaultURL are both empty" );

};


function showMsg( $element ) {
	
	$element.css( { top: PAGE_CONSTANTS.ERROR_MESSAGE_VERTICAL_MOVEMENT } );
	
	$element.show();
	
	$element.animate( { top: 0 }, { duration: 500 } );

};


/////////////

function hideAllErrorMessages() {

	var $error_message_container_jq = $(".error_message_container_jq");

	$error_message_container_jq.hide();
}



//////////////////

function initResetPassword() {
	
	
	$(document).click( function(eventObject) {
	
		hideAllErrorMessages();
	});

//	var $reset_password_button  = $("#reset_password_button");
//
//	$reset_password_button.click( function(eventObject) {
//
//		var clickThis = this;
//		resetPassword(clickThis, eventObject);	
//		
//		return false;  // stop click bubble up.
//	});
	

};
	
	
///////////////

$(document).ready(function() {

	try {
		initResetPassword();
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}

});
