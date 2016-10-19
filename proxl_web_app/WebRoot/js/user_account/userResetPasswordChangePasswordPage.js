
//   userResetPasswordChangePasswordPage.js


//  Javascript for the user reset email page resetPasswordChangePassword.jsp

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";


var PAGE_CONSTANTS = {
		
		ERROR_MESSAGE_VERTICAL_MOVEMENT : 50 // number of pixels for moving error message when showing it. 
		
};


///////////////////

var resetPasswordChangePasswordFormSubmit = function() {

	try {
		resetPasswordChangePassword();
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
};


////////////////////////////

var resetPasswordChangePassword = function(clickThis, eventObject) {

	hideAllErrorMessages();
	

	var $resetPasswordTrackingCode = $("#resetPasswordTrackingCode");

	if ($resetPasswordTrackingCode.length === 0) {
		
		var $element = $("#error_message_system_error");
		
		showMsg( $element );
		
		throw Error( "Unable to find input field for id 'resetPasswordTrackingCode' " );
	}

	var $password_change_field = $("#password_change_field");

	if ($password_change_field.length === 0) {
		
		
		var $element = $("#error_message_system_error");
		
		showMsg( $element );

		throw Error( "Unable to find input field for id 'password_change_field' " );
	}

	var $password_confirm_field = $("#password_confirm_field");

	if ($password_confirm_field.length === 0) {
		
		var $element = $("#error_message_system_error");
		
		showMsg( $element );
		
		throw Error( "Unable to find input field for id 'password_confirm_field' " );
	}


	

	var resetPasswordTrackingCode = $resetPasswordTrackingCode.val();

	var password_change_field = $password_change_field.val();

	var password_confirm_field = $password_confirm_field.val();
	

	if ( password_change_field === "" && password_confirm_field === "" ) {
			
		var $element = $("#error_message_all_fields_required");
		
		showMsg( $element );
			
		return;  //  !!!  EARLY EXIT
	}
	

	if ( password_change_field !== password_confirm_field ) {
			
		var $element = $("#error_message_password_confirm_password_not_match");
		
		showMsg( $element );
			
		return;  //  !!!  EARLY EXIT
	}

	var requestData = {
			password : password_change_field,
			resetPasswordTrackingCode : resetPasswordTrackingCode
	};

	var _URL = contextPathJSVar + "/services/user/resetPasswordUpdatePassword";

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

		if ( responseData.errorMessage ) {
			
			var $error_message_from_server_text = $("#error_message_from_server_text");
			
			$error_message_from_server_text.text( responseData.errorMessage );
			
			var $element = $( "#error_message_from_server" );
			
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

function initResetPasswordChangePassword() {
	
	
	$(document).click( function(eventObject) {
	
		hideAllErrorMessages();
	});

//	var $change_password_button  = $("#change_password_button");
//
//	$change_password_button.click( function(eventObject) {
//
//		var clickThis = this;
//		resetPasswordChangePassword(clickThis, eventObject);	
//		
//		return false;  // stop click bubble up.
//	});
	

};
	
	
///////////////

$(document).ready(function() {

	try {
		initResetPasswordChangePassword();
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}

});
