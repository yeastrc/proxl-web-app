
//  passwordTooLong_Common.js

//  For pages that SET a password (create account, change password, reset password).  NOT for sign in.

//  The maximum password length is in UTF-8 bytes and comes from the server
//    in the hidden input with id 'password_max_length_utf8_bytes'.

//  The message shown to the user does NOT mention bytes.  It tells the user how many characters to remove from the end.

"use strict";

/**
 * @param showErrorMessage_Function - the page's function for showing an error message element: function( $element )
 * @returns true if password is too long (and the error message is shown), false otherwise
 */
export const passwordTooLong_CheckBeforeSubmit_ShowErrorMessage = function({ password, showErrorMessage_Function }) {

	const maxLength_UTF8_Bytes = _getMaxLength_UTF8_Bytes();

	const removeCount = _getCharactersToRemoveFromEnd_Count({ password, maxLength_UTF8_Bytes });
	if ( removeCount === 0 ) {
		return false;
	}

	_showErrorMessage({ removeWhatText : _getRemoveTheLastNCharactersText({ removeCount }), showErrorMessage_Function });

	return true;
}

/**
 * Call when the server returned passwordTooLong true
 * 
 * @param showErrorMessage_Function - the page's function for showing an error message element: function( $element )
 */
export const passwordTooLong_ServerReturnedPasswordTooLong_ShowErrorMessage = function({ password, showErrorMessage_Function }) {

	const maxLength_UTF8_Bytes = _getMaxLength_UTF8_Bytes();

	const removeCount = _getCharactersToRemoveFromEnd_Count({ password, maxLength_UTF8_Bytes });
	if ( removeCount === 0 ) {
		//  Page and server disagree.  Should not happen.
		_showErrorMessage({ removeWhatText : "5 characters", showErrorMessage_Function });
		return;
	}

	_showErrorMessage({ removeWhatText : _getRemoveTheLastNCharactersText({ removeCount }), showErrorMessage_Function });
}

////////////////////

const _getMaxLength_UTF8_Bytes = function() {

	const $password_max_length_utf8_bytes = $("#password_max_length_utf8_bytes");
	if ( $password_max_length_utf8_bytes.length === 0 ) {
		throw Error( "Unable to find input field for id 'password_max_length_utf8_bytes' " );
	}
	const maxLength_UTF8_Bytes_String = $password_max_length_utf8_bytes.val();
	const maxLength_UTF8_Bytes = Number.parseInt( maxLength_UTF8_Bytes_String, 10 );
	if ( Number.isNaN( maxLength_UTF8_Bytes ) || maxLength_UTF8_Bytes <= 0 ) {
		throw Error( "Value in input field for id 'password_max_length_utf8_bytes' is not a positive integer: '" + maxLength_UTF8_Bytes_String + "'" );
	}
	return maxLength_UTF8_Bytes;
}

/**
 * Number of whole characters (code points, so an emoji is 1) to remove from the end of password
 * so the UTF-8 byte length is at or under maxLength_UTF8_Bytes.
 *
 * @returns 0 if password is not too long
 */
const _getCharactersToRemoveFromEnd_Count = function({ password, maxLength_UTF8_Bytes }) {

	const textEncoder = new TextEncoder();

	let length_UTF8_Bytes = textEncoder.encode( password ).length;
	if ( length_UTF8_Bytes <= maxLength_UTF8_Bytes ) {
		return 0;
	}

	const passwordCodePoints = Array.from( password );  //  Splits on code points, not UTF-16 units

	let removeCount = 0;
	for ( let index = passwordCodePoints.length - 1; index >= 0 && length_UTF8_Bytes > maxLength_UTF8_Bytes; index-- ) {
		length_UTF8_Bytes -= textEncoder.encode( passwordCodePoints[ index ] ).length;
		removeCount++;
	}
	return removeCount;
}

const _getRemoveTheLastNCharactersText = function({ removeCount }) {

	if ( removeCount === 1 ) {
		return "the last 1 character";
	}
	return "the last " + removeCount + " characters";
}

const _showErrorMessage = function({ removeWhatText, showErrorMessage_Function }) {

	const $element = $("#error_message_password_too_long");
	if ( $element.length === 0 ) {
		throw Error( "Unable to find element for id 'error_message_password_too_long' " );
	}
	$element.find(".password_too_long_remove_what_jq").text( removeWhatText );

	showErrorMessage_Function( $element );
}
