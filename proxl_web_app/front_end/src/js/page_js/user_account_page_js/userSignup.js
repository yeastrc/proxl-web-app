
//  userSignup.js

//   /js/user_account/userSignup.js

//Javascript for the user account management page   account.jsp
//JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

var PAGE_CONSTANTS = {
		ERROR_MESSAGE_VERTICAL_MOVEMENT : 50 // number of pixels for moving error message when showing it. 
};

var globalVars = {
		usernameConfirmedNotInDB : false,
		emailConfirmedNotInDB : false
};

///////////////////
window.createAccountFormSubmit = function() {
	try {
		var requestData = createAccountGetFormDataAndValidate();
		if ( requestData === null ) {  //  Error in form data so exit
			return;  //  EARLY EXIT
		}
		if ( requestData.tos_key !== "" ) {  //  Have Terms of service key so user has to accept terms of service
			$("#terms_of_service_modal_dialog_overlay_background").show();
			$("#terms_of_service_overlay_div").show();
			return;  //  EARLY EXIT
		}
		
		//  Form data valid and no terms of service so create account
		createAccount();
		
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
};


///////////////////
window.createAccountGetFormDataAndValidate = function() {

	try {
		hideAllErrorMessages();
		var $firstName = $("#firstName");
		if ($firstName.length === 0) {
			throw Error( "Unable to find input field for id 'firstName' " );
		}
		var firstName = $firstName.val();
		var $lastName = $("#lastName");
		if ($lastName.length === 0) {
			throw Error( "Unable to find input field for id 'lastName' " );
		}
		var lastName = $lastName.val();
		var $organization = $("#organization");
		if ($organization.length === 0) {
			throw Error( "Unable to find input field for id 'organization' " );
		}
		var organization = $organization.val();
		//
		var $email = $("#email");
		if ($email.length === 0) {
			throw Error( "Unable to find input field for id 'email' " );
		}
		var email = $email.val();
		var $username = $("#username");
		if ($username.length === 0) {
			throw Error( "Unable to find input field for id 'username' " );
		}
		var username = $username.val();
		var $password = $("#password");
		if ($password.length === 0) {
			throw Error( "Unable to find input field for id 'password' " );
		}
		var password = $password.val();
		var $passwordConfirm = $("#passwordConfirm");
		if ($passwordConfirm.length === 0) {
			throw Error( "Unable to find input field for id 'passwordConfirm' " );
		}
		var passwordConfirm = $passwordConfirm.val();
		if ( firstName === "" ||
				lastName === "" ||
				organization === "" ||
				email === "" ||
				username === "" ||
				password === "" ||
				passwordConfirm === "" ) {
			var $element = $("#error_message_all_fields_required");
			showErrorMsg( $element );
			return null;  //  !!!  EARLY EXIT
		} 
		if ( password !== passwordConfirm ) {
			var $element = $("#error_message_password_confirm_password_not_match");
			showErrorMsg( $element );
			return null;  //  !!!  EARLY EXIT
		} 
		var recaptchaValue = "";
		var $proxl_google_recaptcha_container_div = $("#proxl_google_recaptcha_container_div");
		if ( $proxl_google_recaptcha_container_div.length > 0 ) {
			//  Google Recaptcha included so get the user's response
			recaptchaValue = grecaptcha.getResponse();
			if ( recaptchaValue === "" || recaptchaValue === null || recaptchaValue === undefined ) {
				var $element = $("#error_message_recaptcha_required");
				showErrorMsg( $element );
				return null;  //  !!!  EARLY EXIT
			}
		}
		//  Terms of service accepted Key
		var tosAcceptedKey = "";
		var $terms_of_service_id_string = $("#terms_of_service_id_string");
		if ( $terms_of_service_id_string.length > 0 ) {
			tosAcceptedKey = $terms_of_service_id_string.val();
		}
		
		var formPageData = {
				firstName : firstName,
				lastName :  lastName,
				organization :  organization,
				email :  email,
				username :  username,
				password :  password,
				recaptchaValue : recaptchaValue,
				tos_key : tosAcceptedKey
		};
	
		return formPageData;
		
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
};




//////////////////////
window.createAccount = function() {
	try {

		var requestData = createAccountGetFormDataAndValidate();
		
		var _URL = contextPathJSVar + "/services/user/createAccountNoInvite";
//		var request =
		$.ajax({
			type : "POST",
			url : _URL,
			data : requestData,
			dataType : "json",
			success : function(data) {
				try {
					createAccountComplete( { requestData: requestData, responseData: data } );
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			},
			failure : function(errMsg) {
				var $element = $("#error_message_system_error");
				showErrorMsg( $element );
//				alert(errMsg);
			},
			error : function(jqXHR, textStatus, errorThrown) {
				var $element = $("#error_message_system_error");
				showErrorMsg( $element );
//				handleAJAXError(jqXHR, textStatus, errorThrown);
//				alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
//				textStatus: " + textStatus );
			}
		});
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
};

window.createAccountComplete = function( params ) {
	var requestData = params.requestData;
	var responseData = params.responseData;
	
	$("#terms_of_service_modal_dialog_overlay_background").hide();
	$("#terms_of_service_overlay_div").hide();
	
	if ( ! responseData.status ) {
		if ( responseData.duplicateUsername && responseData.duplicateEmail ) {
			var $element = $("#error_message_username_email_taken");
			showErrorMsg( $element );
			var $emailInput = $("#email");
			$emailInput.focus();
		} else if ( responseData.duplicateUsername ) {
			var $element = $("#error_message_username_taken");
			showErrorMsg( $element );
			var $usernameInput = $("#username");
			$usernameInput.focus();
		} else if ( responseData.duplicateEmail ) {
			var $element = $("#error_message_email_taken");
			showErrorMsg( $element );
			var $emailInput = $("#email");
			$emailInput.focus();
		} else if ( responseData.errorMessage ) {
			$("#error_message_from_server_text").text( responseData.errorMessage );
			var $element = $("#error_message_from_server");
			showErrorMsg( $element );
		} else {
			var $element = $("#error_message_system_error");
			showErrorMsg( $element );
		}
		if ( responseData.userTestValidated ) {
			//  Reset captcha widget so user is requested to verify captcha again
			//   The user's previous captcha response cannot be used again
			grecaptcha.reset(); 
		}
		return;
	} 
	
	$("#list_projects_form").submit();
};


//	
//	
//	////////////
//	
//	
//	window.showErrorMsg = function( $element ) {
//	
//		$element.css( { top: PAGE_CONSTANTS.ERROR_MESSAGE_VERTICAL_MOVEMENT } );
//	
//		$element.show();
//	
//		$element.animate( { top: 0 }, { duration: 500 } );
//	
//	};
//	
//	
//	
//	/////////////
//	
//	window.hideAllErrorMessages = function() {
//	
//		var $error_message_container_jq = $(".error_message_container_jq");
//	
//		$error_message_container_jq.hide();
//	}
//	

/////////////////
window.initPage = function() {

	$(document).click( function(eventObject) {
		try {
			hideAllErrorMessages();
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});
	
	$("#terms_of_service_acceptance_yes_button").click( function(eventObject) {
//		var clickThis = this;
		try {
			createAccount();
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
		initPage();
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
});
