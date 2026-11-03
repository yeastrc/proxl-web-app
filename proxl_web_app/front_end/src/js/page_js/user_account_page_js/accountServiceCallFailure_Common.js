//   accountServiceCallFailure_Common.js

//  The call from the server to the account service (User Mgmt) failed.
//  The server returns HTTP status 500 with one of the response texts below.
//  The messages shown to the user are kept here.

//  The response texts must match WebServiceErrorMessageConstants.java

const RESPONSE_STATUS_CODE = 500;

const RESPONSE_TEXT__CHANGE_NOT_SAVED = "change_not_saved";
const RESPONSE_TEXT__CHANGE_OUTCOME_UNKNOWN = "change_outcome_unknown";
const RESPONSE_TEXT__ACCOUNT_SERVICE_UNAVAILABLE = "account_service_unavailable";

const MESSAGE__CHANGE_NOT_SAVED = "Your change could not be saved; nothing was changed.  Please try again.";
const MESSAGE__CHANGE_OUTCOME_UNKNOWN = "We couldn't confirm whether your change was saved.  Please check before trying again.";
const MESSAGE__CHANGE_OUTCOME_UNKNOWN__ADDITION__CREATE_ACCOUNT_SELF = "If you can't sign in with the new account, please try again.";
const MESSAGE__CHANGE_OUTCOME_UNKNOWN__ADDITION__CREATE_ACCOUNT_ADMIN = "If the new account doesn't appear in the list, please try again.";
const MESSAGE__ACCOUNT_SERVICE_UNAVAILABLE = "The account service is temporarily unavailable.  Please try again in a moment.";

/**
 * Values for param createAccount
 */
export const ACCOUNT_SERVICE_CALL_FAILURE__CREATE_ACCOUNT__SELF = "SELF";    //  Self sign-up and sign-up from invite
export const ACCOUNT_SERVICE_CALL_FAILURE__CREATE_ACCOUNT__ADMIN = "ADMIN";  //  Admin creates the account

/**
 * @param jqXHR - from the jQuery AJAX 'error' callback
 * @param createAccount - undefined when not a create account, else one of the ACCOUNT_SERVICE_CALL_FAILURE__CREATE_ACCOUNT__ values
 * @returns The message to show, as text.  null if the response is not for a failed call to the account service.
 */
export const accountServiceCallFailure_GetMessage = function({ jqXHR, createAccount }) {

	if ( ( ! jqXHR ) || jqXHR.status !== RESPONSE_STATUS_CODE ) {
		return null;
	}
	const responseText = jqXHR.responseText;

	if ( responseText === RESPONSE_TEXT__CHANGE_NOT_SAVED ) {
		return MESSAGE__CHANGE_NOT_SAVED;
	}
	if ( responseText === RESPONSE_TEXT__CHANGE_OUTCOME_UNKNOWN ) {
		if ( createAccount === ACCOUNT_SERVICE_CALL_FAILURE__CREATE_ACCOUNT__SELF ) {
			return MESSAGE__CHANGE_OUTCOME_UNKNOWN + "  " + MESSAGE__CHANGE_OUTCOME_UNKNOWN__ADDITION__CREATE_ACCOUNT_SELF;
		}
		if ( createAccount === ACCOUNT_SERVICE_CALL_FAILURE__CREATE_ACCOUNT__ADMIN ) {
			return MESSAGE__CHANGE_OUTCOME_UNKNOWN + "  " + MESSAGE__CHANGE_OUTCOME_UNKNOWN__ADDITION__CREATE_ACCOUNT_ADMIN;
		}
		return MESSAGE__CHANGE_OUTCOME_UNKNOWN;
	}
	if ( responseText === RESPONSE_TEXT__ACCOUNT_SERVICE_UNAVAILABLE ) {
		return MESSAGE__ACCOUNT_SERVICE_UNAVAILABLE;
	}
	return null;
}

/**
 * Show the message in the page's element with id 'error_message_account_service', if the response is for a failed call to the account service.
 *
 * @param jqXHR - from the jQuery AJAX 'error' callback
 * @param createAccount - undefined when not a create account, else one of the ACCOUNT_SERVICE_CALL_FAILURE__CREATE_ACCOUNT__ values
 * @param showErrorMessage_Function - the page's function to show an error message element
 * @returns true if the message was shown
 */
export const accountServiceCallFailure_ShowErrorMessage_IfFailure = function({ jqXHR, createAccount, showErrorMessage_Function }) {

	const message = accountServiceCallFailure_GetMessage({ jqXHR, createAccount });
	if ( ! message ) {
		return false;
	}

	const $element = $("#error_message_account_service");
	if ( $element.length === 0 ) {
		throw Error( "Unable to find element for id 'error_message_account_service' " );
	}
	$element.find(".account_service_message_jq").text( message );

	showErrorMessage_Function( $element );

	return true;
}
