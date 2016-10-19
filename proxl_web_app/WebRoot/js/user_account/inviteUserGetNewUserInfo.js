
//  inviteUserGetNewUserInfo.js


//   /js/user_account/inviteUserGetNewUserInfo.js




//Javascript for the user account management page   account.jsp



//JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";


var PAGE_CONSTANTS = {

		ERROR_MESSAGE_VERTICAL_MOVEMENT : 50 // number of pixels for moving error message when showing it. 

};


///////////////////

var createAccountFormSubmit = function() {

	try {
		createAccount();
		
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
};


//////////////////////

var createAccount = function(clickThis, eventObject) {

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

		return;  //  !!!  EARLY EXIT

	} 
	
	if ( password !== passwordConfirm ) {

		var $element = $("#error_message_password_confirm_password_not_match");

		showErrorMsg( $element );

		return;  //  !!!  EARLY EXIT

	} 
	
	
	var requestData = {
			inviteCode : inviteCode,
			firstName : firstName,
			lastName :  lastName,
			organization :  organization,
			email :  email,
			username :  username,
			password :  password
	};

	var _URL = contextPathJSVar + "/services/user/createAccountFromInvite";

//	var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(data) {

			try {
				createAccountComplete( { requestData: requestData, responseData: data, clickThis: clickThis } );
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



var createAccountComplete = function( params ) {

	var requestData = params.requestData;
	var responseData = params.responseData;
	var clickThis = params.clickThis; 


	if ( ! responseData.status ) {

		if ( responseData.duplicateUsername && responseData.duplicateEmail ) {
			
			var $element = $("#error_message_username_email_taken");

			showErrorMsg( $element );

		} else if ( responseData.duplicateUsername ) {

			var $element = $("#error_message_username_taken");

			showErrorMsg( $element );
			
		} else if ( responseData.duplicateEmail ) {
			
			var $element = $("#error_message_email_taken");

			showErrorMsg( $element );
			
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

//	var $clickThis = $(clickThis);

	

//	var $element = $("#success_message_system_success");
//
//	showErrorMsg( $element );
	
	
	$("#list_projects_form").submit();

};

//	
//	
//	////////////
//	
//	
//	function showErrorMsg( $element ) {
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
//	function hideAllErrorMessages() {
//	
//		var $error_message_container_jq = $(".error_message_container_jq");
//	
//		$error_message_container_jq.hide();
//	}
//	


/////////////////

function initPage() {

//	var $create_account_button = $("#create_account_button");
//
//	$create_account_button.click( function(eventObject) {
//
//		var clickThis = this;
//		createAccount(clickThis, eventObject);	
//
//		return false;  // stop click bubble up.
//	});

	
	$(document).click( function(eventObject) {
	
		try {

			hideAllErrorMessages();
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});

};



///////////////

$(document).ready(function() {

	initPage();

});
