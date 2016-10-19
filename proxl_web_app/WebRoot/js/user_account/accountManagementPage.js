
//   accountManagementPage.js


//   /js/user_account/accountManagementPage.js


//  Javascript for the user account management page   account.jsp

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";


////////////////////////////

var updateFirstName = function(clickThis, eventObject) {


	var $firstName = $("#first-name-change-field");

	if ($firstName.length === 0) {

		throw Error( "Unable to find input field for id 'first-name-change-field' " );
	}

	var firstName = $firstName.val();


	if ( firstName === "" ) {
		
		var $element = $("#error_message_field_empty");
		
		showErrorMsg( $element );
		
		return;  //  !!!  EARLY EXIT
	} 

	var requestData = {
			firstName : firstName
	};

	var _URL = contextPathJSVar + "/services/user/changeFirstName";

	// var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(data) {

			try {
				updateFirstNameComplete( { requestData: requestData, responseData: data, clickThis: clickThis } );
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

			// alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
			// textStatus: " + textStatus );
		}
	});
};

//////////

var updateFirstNameComplete = function( params ) {

	 var requestData = params.requestData;
	 var responseData = params.responseData;
	 var clickThis = params.clickThis; 
	
	
	if ( ! responseData.status ) {

		var $element = $("#error_message_system_error");

		showErrorMsg( $element );
		
		return;
		
	} 

	var $clickThis = $(clickThis);
	
	
	var $value_container_jq = $clickThis.closest(".value_container_jq");

	var $current_value_container_jq = $value_container_jq.find(".current_value_container_jq");
	
	var $current_value_span_jq = $value_container_jq.find(".current_value_span_jq");
	
	var $edit_value_container_jq = $value_container_jq.find(".edit_value_container_jq");

	$current_value_span_jq.text( requestData.firstName );
	
	$("#header-user-first-name").text( requestData.firstName );
	
	
	$edit_value_container_jq.hide();
	$current_value_container_jq.show();
};



////////////////////////////

var updateLastName = function(clickThis, eventObject) {


	var $lastName = $("#last-name-change-field");

	if ($lastName.length === 0) {

		throw Error( "Unable to find input field for id 'last-name-change-field' " );
	}

	var lastName = $lastName.val();


	if ( lastName === "" ) {

		var $element = $("#error_message_field_empty");

		showErrorMsg( $element );

		return;  //  !!!  EARLY EXIT

	} 
	

	var requestData = {
			lastName : lastName
	};

	var _URL = contextPathJSVar + "/services/user/changeLastName";

//	var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(data) {

			try {
				updateLastNameComplete( { requestData: requestData, responseData: data, clickThis: clickThis } );
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



var updateLastNameComplete = function( params ) {

	var requestData = params.requestData;
	var responseData = params.responseData;
	var clickThis = params.clickThis; 


	if ( ! responseData.status ) {

		var $element = $("#error_message_system_error");

		showErrorMsg( $element );
		
		return;

	} 

	var $clickThis = $(clickThis);


	var $value_container_jq = $clickThis.closest(".value_container_jq");

	var $current_value_container_jq = $value_container_jq.find(".current_value_container_jq");

	var $current_value_span_jq = $value_container_jq.find(".current_value_span_jq");

	var $edit_value_container_jq = $value_container_jq.find(".edit_value_container_jq");

	$current_value_span_jq.text( requestData.lastName );

	$("#header-user-last-name").text( requestData.lastName );


	$edit_value_container_jq.hide();
	$current_value_container_jq.show();
};


////////////////////


var updateOrganization = function(clickThis, eventObject) {


	var $organization = $("#organization-change-field");

	if ($organization.length === 0) {

		throw Error( "Unable to find input field for id 'organization-change-field' " );
	}

	var organization = $organization.val();


	if ( organization === "" ) {

		var $element = $("#error_message_field_empty");

		showErrorMsg( $element );

		return;  //  !!!  EARLY EXIT

	} 

	var requestData = {
			organization : organization
	};

	var _URL = contextPathJSVar + "/services/user/changeOrganization";

//	var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(data) {

			try {
				updateOrganizationComplete( { requestData: requestData, responseData: data, clickThis: clickThis } );
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



var updateOrganizationComplete = function( params ) {

	var requestData = params.requestData;
	var responseData = params.responseData;
	var clickThis = params.clickThis; 


	if ( ! responseData.status ) {
		var $element = $("#error_message_system_error");

		showErrorMsg( $element );

		return;

	} 

	var $clickThis = $(clickThis);


	var $value_container_jq = $clickThis.closest(".value_container_jq");

	var $current_value_container_jq = $value_container_jq.find(".current_value_container_jq");

	var $current_value_span_jq = $value_container_jq.find(".current_value_span_jq");

	var $edit_value_container_jq = $value_container_jq.find(".edit_value_container_jq");

	$current_value_span_jq.text( requestData.organization );


	$edit_value_container_jq.hide();
	$current_value_container_jq.show();
};


////////////////////


var updateEmail = function(clickThis, eventObject) {


	var $email = $("#email-change-field");

	if ($email.length === 0) {

		throw Error( "Unable to find input field for id 'email-change-field' " );
	}

	var email = $email.val();


	if ( email === "" ) {

		var $element = $("#error_message_field_empty");

		showErrorMsg( $element );

		return;  //  !!!  EARLY EXIT

	} 

	var requestData = {
			email : email
	};

	var _URL = contextPathJSVar + "/services/user/changeEmail";

//	var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(data) {

			try {
				updateEmailComplete( { requestData: requestData, responseData: data, clickThis: clickThis } );
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



var updateEmailComplete = function( params ) {

	var requestData = params.requestData;
	var responseData = params.responseData;
	var clickThis = params.clickThis; 


	if ( ! responseData.status ) {

		if ( responseData.valueAlreadyExists ) {

			var $element = $("#error_message_email_already_exists");

			showErrorMsg( $element );
			
//			alert("error_message_email_already_exists");

		} else {

			var $element = $("#error_message_system_error");

			showErrorMsg( $element );
		}


		return;

	} 

	var $clickThis = $(clickThis);


	var $value_container_jq = $clickThis.closest(".value_container_jq");

	var $current_value_container_jq = $value_container_jq.find(".current_value_container_jq");

	var $current_value_span_jq = $value_container_jq.find(".current_value_span_jq");

	var $edit_value_container_jq = $value_container_jq.find(".edit_value_container_jq");

	$current_value_span_jq.text( requestData.email );


	$edit_value_container_jq.hide();
	$current_value_container_jq.show();
};


////////////////


var updateUsername = function(clickThis, eventObject) {


	var $username = $("#username-change-field");

	if ($username.length === 0) {

		throw Error( "Unable to find input field for id 'username-change-field' " );
	}

	var username = $username.val();


	if ( username === "" ) {

		var $element = $("#error_message_field_empty");

		showErrorMsg( $element );

		return;  //  !!!  EARLY EXIT

	} 

	var requestData = {
			username : username
	};

	var _URL = contextPathJSVar + "/services/user/changeUsername";

//	var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(data) {

			try {
				updateUsernameComplete( { requestData: requestData, responseData: data, clickThis: clickThis } );
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

/////////////

var updateUsernameComplete = function( params ) {

	var requestData = params.requestData;
	var responseData = params.responseData;
	var clickThis = params.clickThis; 


	if ( ! responseData.status ) {

		if ( responseData.valueAlreadyExists ) {

			var $element = $("#error_message_username_already_exists");

			showErrorMsg( $element );
			
//			alert("error_message_username_already_exists");

		} else {

			var $element = $("#error_message_system_error");

			showErrorMsg( $element );
		}

		return;

	} 

	var $clickThis = $(clickThis);


	var $value_container_jq = $clickThis.closest(".value_container_jq");

	var $current_value_container_jq = $value_container_jq.find(".current_value_container_jq");

	var $current_value_span_jq = $value_container_jq.find(".current_value_span_jq");

	var $edit_value_container_jq = $value_container_jq.find(".edit_value_container_jq");

	$current_value_span_jq.text( requestData.username );

	
	$("#header-user-user-name").text( requestData.username );
	


	$edit_value_container_jq.hide();
	$current_value_container_jq.show();
};


////////////////


var updatePassword = function(clickThis, eventObject) {


	var $password = $("#password-change-field");

	if ($password.length === 0) {

		throw Error( "Unable to find input field for id 'password-change-field' " );
	}

	var $passwordConfirm = $("#password-confirm-field");

	if ($passwordConfirm.length === 0) {

		throw Error( "Unable to find input field for id 'password-confirm-field' " );
	}

	var password = $password.val();
	var passwordConfirm = $passwordConfirm.val();


	if ( password === "" ) {

		var $element = $("#error_message_field_empty");

		showErrorMsg( $element );

		return;  //  !!!  EARLY EXIT

	} 
	

	if ( passwordConfirm === "" ) {

		var $element = $("#error_message_field_empty");

		showErrorMsg( $element );

		return;  //  !!!  EARLY EXIT
	} 

	if ( password !== passwordConfirm ) {

		var $element = $("#error_message_password_confirm_not_match");

		showErrorMsg( $element );

		return;  //  !!!  EARLY EXIT
	} 
	

	var requestData = {
			password : password
	};

	var _URL = contextPathJSVar + "/services/user/changePassword";

//	var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(data) {

			try {
				updatePasswordComplete( { requestData: requestData, responseData: data, clickThis: clickThis } );
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



var updatePasswordComplete = function( params ) {

	var requestData = params.requestData;
	var responseData = params.responseData;
	var clickThis = params.clickThis; 


	if ( ! responseData.status ) {

		var $element = $("#error_message_system_error");

		showErrorMsg( $element );

		return;

	} 

	var $clickThis = $(clickThis);


	var $value_container_jq = $clickThis.closest(".value_container_jq");

	var $current_value_container_jq = $value_container_jq.find(".current_value_container_jq");

	var $current_value_span_jq = $value_container_jq.find(".current_value_span_jq");

	var $edit_value_container_jq = $value_container_jq.find(".edit_value_container_jq");

	$current_value_span_jq.text( requestData.password );


	$edit_value_container_jq.hide();
	$current_value_container_jq.show();
	
	

	var $password = $("#password-change-field");

	if ($password.length === 0) {

		throw Error( "Unable to find input field for id 'password-change-field' " );
	}

	var $passwordConfirm = $("#password-confirm-field");

	if ($passwordConfirm.length === 0) {

		throw Error( "Unable to find input field for id 'password-confirm-field' " );
	}
	
	///  Clear the entry fields

	$password.val("");
	$passwordConfirm.val("");
};



///////////////

var editValue = function( clickThis ) {
	
	
	var $clickThis = $(clickThis);
	
	
	var $value_container_jq = $clickThis.closest(".value_container_jq");

	var $current_value_container_jq = $value_container_jq.find(".current_value_container_jq");
	
	var $current_value_span_jq = $value_container_jq.find(".current_value_span_jq");
	
	var $edit_value_container_jq = $value_container_jq.find(".edit_value_container_jq");
	
	var $edit_value_input_field_jq = $value_container_jq.find(".edit_value_input_field_jq");
	
	if ( $current_value_span_jq.length !== 0 ) {

		var currentValue = $current_value_span_jq.text();
	
		$edit_value_input_field_jq.val( currentValue );
	}
	
	$current_value_container_jq.hide();
	
	$edit_value_container_jq.show();
};

//////////////////

var closeEditValue = function( clickThis ) {

	var $clickThis = $(clickThis);

	var $value_container_jq = $clickThis.closest(".value_container_jq");

	var $current_value_container_jq = $value_container_jq.find(".current_value_container_jq");

	var $edit_value_container_jq = $value_container_jq.find(".edit_value_container_jq");

	$edit_value_container_jq.hide();
	$current_value_container_jq.show();

};

//////////////////

function initPage() {
	
	
//	$(document).click( function(eventObject) {
//	
//		var $error_message_container_jq = $(".error_message_container_jq");
//
//		$error_message_container_jq.hide();
//	});

	// set up so clicking the edit icon switches to the text to change

	var $edit_value_jq = $(".edit_value_jq");

	$edit_value_jq.click( function(eventObject) {

		try {
			var clickThis = this;

			hideAllErrorMessages();

			editValue( clickThis );

			return false;  // stop click bubble up.
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});

	var $cancel_button_jq = $(".cancel_button_jq");

	$cancel_button_jq.click( function(eventObject) {

		try {
			var clickThis = this;

			hideAllErrorMessages();

			closeEditValue( clickThis );

			return false;  // stop click bubble up.
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});


	var $submit_first_name_change_button = $("#submit-first-name-change-button");

	$submit_first_name_change_button.click( function(eventObject) {

		try {
			var clickThis = this;

			hideAllErrorMessages();

			updateFirstName(clickThis, eventObject);	

			return false;  // stop click bubble up.
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});


	var $submit_last_name_change_button = $("#submit-last-name-change-button");

	$submit_last_name_change_button.click( function(eventObject) {

		try {
			var clickThis = this;

			hideAllErrorMessages();

			updateLastName(clickThis, eventObject);	

			return false;  // stop click bubble up.
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});



	var $submit_organization_change_button = $("#submit-organization-change-button");

	$submit_organization_change_button.click( function(eventObject) {

		try {
			var clickThis = this;

			hideAllErrorMessages();

			updateOrganization(clickThis, eventObject);	

			return false;  // stop click bubble up.
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});

	var $submit_email_change_button = $("#submit-email-change-button");

	$submit_email_change_button.click( function(eventObject) {

		try {
			var clickThis = this;

			hideAllErrorMessages();

			updateEmail(clickThis, eventObject);	

			return false;  // stop click bubble up.
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});


	var $submit_username_change_button = $("#submit-username-change-button");

	$submit_username_change_button.click( function(eventObject) {

		try {
			var clickThis = this;

			hideAllErrorMessages();

			updateUsername(clickThis, eventObject);	

			return false;  // stop click bubble up.
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});


	var $submit_password_change_button = $("#submit-password-change-button");

	$submit_password_change_button.click( function(eventObject) {

		try {
			var clickThis = this;

			hideAllErrorMessages();

			updatePassword(clickThis, eventObject);	

			return false;  // stop click bubble up.
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});

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

	try {
		initPage();
		
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
});
