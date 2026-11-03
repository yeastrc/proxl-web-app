/*
* Original author: Daniel Jaschob <djaschob .at. uw.edu>
*
* Copyright 2026 University of Washington - Seattle, WA
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.yeastrc.xlink.www.user_mgmt_webapp_access;

import org.yeastrc.user_mgmt_central.user_mgmt_central__embed_code.UserMgmtConnectorCallFailure_IF;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

/**
 * A failed call to User Mgmt through the connector (User Mgmt not embedded in this webapp):
 * Find it in an exception and map it to the response sent to the browser.
 *
 * The mapping to what the user sees is kept here.
 *
 * Sign in:  See LoginService:  Returns "Sign-in is temporarily unavailable" for any connector call failure.
 *
 * Embedded User Mgmt (no connector):  find(...) returns null, the caller handles the exception as before.
 */
public class UserMgmt_ConnectorCallFailure_Util {

	/**
	 * Limit on the number of causes followed
	 */
	private static final int CAUSE_CHAIN_MAX_DEPTH = 20;

	/**
	 * private constructor
	 */
	private UserMgmt_ConnectorCallFailure_Util() {}

	/**
	 * @param throwable
	 * @return The connector call failure in throwable or its causes, null if none
	 */
	public static UserMgmtConnectorCallFailure_IF find( Throwable throwable ) {

		Throwable current = throwable;
		for ( int depth = 0; current != null && depth < CAUSE_CHAIN_MAX_DEPTH; depth++ ) {
			if ( current instanceof UserMgmtConnectorCallFailure_IF ) {
				return (UserMgmtConnectorCallFailure_IF) current;
			}
			Throwable cause = current.getCause();
			if ( cause == current ) {
				break;
			}
			current = cause;
		}
		return null;
	}

	/**
	 * For the log.  Contains no submitted values.
	 *
	 * @param callFailure
	 * @return
	 */
	public static String logText( UserMgmtConnectorCallFailure_IF callFailure ) {

		return "Call to the account service (User Mgmt) failed.  failure reason: " + callFailure.getFailureReason()
				+ ", call group: " + callFailure.getCallGroupName()
				+ ", HTTP status code: " + callFailure.getHttpStatusCode()
				+ ", outcome unknown: " + callFailure.isOutcomeUnknown();
	}

	/**
	 * A change (create account, change account data or password, reset password) failed
	 *
	 * @param callFailure
	 * @return The exception to throw from the web service
	 */
	public static WebApplicationException webApplicationException_ForChange( UserMgmtConnectorCallFailure_IF callFailure ) {

		String responseText = WebServiceErrorMessageConstants.USER_MGMT_CHANGE_NOT_SAVED_TEXT;
		if ( callFailure.isOutcomeUnknown() ) {
			responseText = WebServiceErrorMessageConstants.USER_MGMT_CHANGE_OUTCOME_UNKNOWN_TEXT;
		}
		return new WebApplicationException(
				Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
				.entity( responseText ) // This string will be passed to the client
				.build()
				);
	}

	/**
	 * Reading user data failed
	 *
	 * @param callFailure
	 * @return The exception to throw from the web service
	 */
	public static WebApplicationException webApplicationException_ForReadOnly( UserMgmtConnectorCallFailure_IF callFailure ) {

		return new WebApplicationException(
				Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
				.entity( WebServiceErrorMessageConstants.USER_MGMT_ACCOUNT_SERVICE_UNAVAILABLE_TEXT ) // This string will be passed to the client
				.build()
				);
	}
}
