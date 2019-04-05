/**
 * webserviceCallStandardPost.js
 * 
 * Make webservice call to server.
 * 
 * Makes Standard POST call to webservice
 * 
 * Search rest of code for use of ".ajax" for places where this is not used and jQuery $.ajax is still being used.
 * 
 * Search rest of code for XMLHttpRequest for AJAX using browser native.
 * 
 * 
 */

//////////////////////////////////
// JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

import { reportWebErrorToServer } from 'page_js/header_section_js_all_pages_main_pages/header_section_every_page/reportWebErrorToServer.js';
import { handleAJAXError, handleAJAXFailure } from 'page_js/header_section_js_all_pages_main_pages/header_section_every_page/handleServicesAJAXErrors.js';

import { WebserviceCallStandardPost_RejectObject_Class } from './webserviceCallStandardPost_RejectObject_Class.js';

const _AJAX_POST_JSON_CONTENT_TYPE = "application/json; charset=utf-8";

/**
 * Make webservice call
 * 
 * @param dataToSend Object that will be serialized to JSON and sent to server
 * @param url - without trailing '/' or getWebserviceSyncTrackingCode() 
 * @param doNotHandleErrorResponse - Do not process non-200 response code or AJAX Parse failures, Caller is responsible.
 * 
 * @return { promise, api }. On promise, call resolve({ responseData }) or reject({ rejectReasonObject }).  On api, call abort()
 * 
 * content type of post is assumed _AJAX_POST_JSON_CONTENT_TYPE
 * 
 */
var webserviceCallStandardPost = function ({ dataToSend, url, doNotHandleErrorResponse }) {

    let request = undefined;
    let abortCalled = false;

    const webserviceCallFunction = function( resolve, reject ) {
        
        const _URL = url;

        const requestData = JSON.stringify( dataToSend );

        request =
        $.ajax({
            type : "POST",
            url : _URL,
            data : requestData,
            contentType: _AJAX_POST_JSON_CONTENT_TYPE,
            dataType : "json",
            success : function( responseData ) {

                request = undefined;

                try {
                    resolve({ responseData });
                    
                } catch( e ) {
                    reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
                    
                    throw e;
                }
            },
            failure: function(errMsg) {

                request = undefined;

                if ( ! doNotHandleErrorResponse ) {
                    handleAJAXFailure( errMsg );  //  Sometimes throws exception so rest of processing won't always happen
                }

                const rejectReasonObject = new WebserviceCallStandardPost_RejectObject_Class();

                //  Need to set properties on object rejectReasonObject

                reject({ rejectReasonObject });
            },
            error : function(jqXHR, textStatus, errorThrown) {

                request = undefined;

                if ( ! abortCalled ) {
                    //  Abort not called so report error
                    
                    if ( ! doNotHandleErrorResponse ) {
                        window.handleAJAXError(jqXHR, textStatus, errorThrown);  //  Sometimes throws exception so rest of processing won't always happen
                    }

                    const rejectReasonObject = new WebserviceCallStandardPost_RejectObject_Class();

                    //  Need to set properties on object rejectReasonObject

                    reject({ rejectReasonObject });
                }

                // alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
                // textStatus: " + textStatus );
            }
        });
    }

    const api = {
        abort : function() {
            if ( request ) {
                abortCalled = true;
                request.abort();
                request = undefined;
            }
        }
    }

    const promise = new Promise( webserviceCallFunction );

    return { promise, api };
};

export { webserviceCallStandardPost }

