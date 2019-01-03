
//  inviteUserGetNewUserInfo.js

//   /js/user_account/inviteUserGetNewUserInfo.js

//  Javascript for the user account management page   account.jsp

// JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

var PAGE_CONSTANTS = {
		ERROR_MESSAGE_VERTICAL_MOVEMENT : 50 // number of pixels for moving error message when showing it. 
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

	hideAllErrorMessages();
	var $inviteCode = $("#code");
	if ($inviteCode.length === 0) {
		throw Error( "Unable to find input field for id 'code' " );
	}
	var inviteCode = $inviteCode.val();
	if ( inviteCode === undefined || inviteCode === null ) {
		var $element = $("#error_message_system_error");
		showErrorMsg( $element );
		throw Error( 'inviteCode === undefined || inviteCode === null ' );
	}
	if ( inviteCode === "" ) {
		var $element = $("#error_message_system_error");
		showErrorMsg( $element );
		throw Error( 'inviteCode === "" ' );
	}
	//  Terms of service accepted Key
	var tosAcceptedKey = "";
	var $terms_of_service_id_string = $("#terms_of_service_id_string");
	if ( $terms_of_service_id_string.length > 0 ) {
		tosAcceptedKey = $terms_of_service_id_string.val();
	}
	
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
			passwordConfirm === "" 	) {
		var $element = $("#error_message_all_fields_required");
		showErrorMsg( $element );
		return null;  //  !!!  EARLY EXIT
	} 
	if ( password !== passwordConfirm ) {
		var $element = $("#error_message_password_confirm_password_not_match");
		showErrorMsg( $element );
		return null;  //  !!!  EARLY EXIT
	} 
	
	var formPageData = {
			inviteCode : inviteCode,
			tos_key : tosAcceptedKey,
			firstName : firstName,
			lastName :  lastName,
			organization :  organization,
			email :  email,
			username :  username,
			password :  password
	};

	return formPageData;
};

//////////////////////
window.createAccount = function() {	

	var requestData = createAccountGetFormDataAndValidate();
	
	var _URL = contextPathJSVar + "/services/user/createAccountFromInvite";
//	var request =
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
//			alert(errMsg);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			var $element = $("#error_message_system_error");
			showErrorMsg( $element );
//			handleAJAXError(jqXHR, textStatus, errorThrown);
//			alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
//			textStatus: " + textStatus );
		}
	});
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
		return;
	} 
	$("#list_projects_form").submit();
};

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
	initPage();
});
