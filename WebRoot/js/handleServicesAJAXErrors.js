

//   handleServicesAJAXErrors.js





var AJAX_RESPONSE_NO_SESSION_TEXT = "no_session";
var AJAX_RESPONSE_NO_SESSION_STATUS_CODE = 401;

var AJAX_RESPONSE_NOT_AUTHORIZED_TEXT = "not_authorized";
var AJAX_RESPONSE_NOT_AUTHORIZED_STATUS_CODE = 403;

var AJAX_RESPONSE_INVALID_SEARCH_LIST_ACROSS_PROJECTS_TEXT = "invalid_search_list_across_projects_text";
var AJAX_RESPONSE_INVALID_SEARCH_LIST_ACROSS_PROJECTS_STATUS_CODE = 403;

var INVALID_SEARCH_LIST_NOT_IN_DB_TEXT = "invalid_search_list_not_in_db_text";
var INVALID_SEARCH_LIST_NOT_IN_DB_STATUS_CODE = 400;

var AJAX_RESPONSE_INVALID_PARAMETER_TEXT = "invalid_parameter";
var AJAX_RESPONSE_INVALID_PARAMETER_STATUS_CODE = 400;



/////////////////////

//Handle when AJAX call gets failure

function handleAJAXFailure( errMsg ) {

	showAjaxErrorMsgFromMsg( { errorMsg : "Connecting to server failed: " + errorMsg } );
}

/////////////////////

//  Handle when AJAX call gets error

function handleAJAXError( jqXHR, textStatus, errorThrown ) {

	
	var jqXHR_statusCode = jqXHR.status;
	var jqXHR_responseText = jqXHR.responseText;
	

	
	if ( jqXHR_statusCode === AJAX_RESPONSE_NO_SESSION_STATUS_CODE &&
			jqXHR_responseText === AJAX_RESPONSE_NO_SESSION_TEXT ) { 
		

		//  reload current URL
		
		window.location.reload(true);
		
		return;
		

//					$("#ajax_error_no_session_saved_url").val( document.URL );
//							
//					var $msg = $("#ajax_error_no_session_msg");
//					
//					if ( $msg.length === 0 ) {
//						
//						alert("User session has expired or does not exist.");
//						
//					} else {
//						
//						$(".overlay_show_hide_parts_jq").hide();
//						
//						$msg.show();
//						
//						window.scroll(0, 0);
//					}
		
		
		
	} else if ( jqXHR_statusCode === AJAX_RESPONSE_NOT_AUTHORIZED_STATUS_CODE &&
				jqXHR_responseText === AJAX_RESPONSE_NOT_AUTHORIZED_TEXT ) { 


		//  reload current URL
		
		window.location.reload(true);
		
		return;
		
		
//					var $msg = $("#ajax_error_not_authorized_msg");
//					
//					if ( $msg.length === 0 ) {
//						
//						alert("User is not authorized for this request.");
//						
//					} else {
//						
//						$(".overlay_show_hide_parts_jq").hide();
//						
//						$msg.show();
//						
//						window.scroll(0, 0);
//					}
		
		
	} else if ( jqXHR_statusCode === AJAX_RESPONSE_INVALID_SEARCH_LIST_ACROSS_PROJECTS_STATUS_CODE &&
				jqXHR_responseText === AJAX_RESPONSE_INVALID_SEARCH_LIST_ACROSS_PROJECTS_TEXT ) { 
		 
		showAjaxErrorMsg( { 
			 errorPageElementId : "ajax_error_invalid_search_list_across_projects_text_msg",
			 errorMsg : "Invalid Search list, it crosses projects" } );
		 

	} else if ( jqXHR_statusCode === INVALID_SEARCH_LIST_NOT_IN_DB_STATUS_CODE &&
				jqXHR_responseText === INVALID_SEARCH_LIST_NOT_IN_DB_TEXT ) { 
		
		 showAjaxErrorMsg( { 
			 errorPageElementId : "ajax_error_invalid_search_list_not_in_db_text_msg",
			 errorMsg : "Invalid Search list, at least one search not found in database.  Please start over at the project or the project list." } );
		 
	} else if ( jqXHR_statusCode === AJAX_RESPONSE_INVALID_PARAMETER_STATUS_CODE &&
			jqXHR_responseText === AJAX_RESPONSE_INVALID_PARAMETER_TEXT ) { 

		showAjaxErrorMsg( { errorMsg : "Invalid parameter passed to server" } );

		
		//  jqXHR_responseText is '' or '' to provide a cause

	} else if ( jqXHR_statusCode === 401 ) {
		
		showAjaxErrorMsg( { errorMsg : "401 received, responseText: " + jqXHR_responseText  } );
							
	} else if ( jqXHR_statusCode === 403 ) {
		
		showAjaxErrorMsg( { errorMsg : "403 received, responseText: " + jqXHR_responseText } );
			
	} else if ( jqXHR_statusCode === 404 ) {
		
		showAjaxErrorMsg( { errorMsg : "404 received, service not found on server, textStatus: " + textStatus  } );
			

	} else if ( jqXHR_statusCode === 500 ) {
		
		showAjaxErrorMsg( { errorMsg : "Internal Server error, status code 500, textStatus: " + textStatus  } );
		
	} else {
		
		showAjaxErrorMsg( { errorMsg : "exception: " + errorThrown + ", jqXHR: " + jqXHR + ", textStatus: " + textStatus } );
	}
	
	
	
};


function showAjaxErrorMsg( params ) {
	
	var errorPageElementId = params.errorPageElementId;
	var errorMsg = params.errorMsg;
	
	if ( errorPageElementId  ) {

		var $msg = $("#" + errorPageElementId );
		
		if ( $msg.length === 0 ) {
			
			showAjaxErrorMsgFromMsg( { errorMsg : errorMsg } );
			
		} else {
			
			$(".overlay_show_hide_parts_jq").hide();
			
			$msg.show();
			
			window.scroll(0, 0);  // scroll to top left, assuming message is in that corner
		}
		
	} else {
		
		showAjaxErrorMsgFromMsg( { errorMsg : errorMsg } );
		
		
	}
	
	
}
	

function showAjaxErrorMsgFromMsg( params ) {
	
	var errorMsg = params.errorMsg;
	
	if ( ! errorMsg || errorMsg === "" )  {
		
		throw "No value passed in params.errorMsg to function showAjaxErrorMsgFromMsg( params )";
	}
	
//	alert( errorMsg );
	
	var html = '<div style="position: absolute; background-color: white; z-index: 10000; top:40px; left:40px; width:500px; padding: 10px; border-width: 5px; border-color: red; border-style: solid;" >'
	
	+ '<h1 style="color: red;">Error accessing server</h1>'
	
	+ '<h3>Please reload the page and try again.</h3>'
	+ '<h3>If this error continues to occur, please contact the person at the bottom of the page.</h3>'
	
	+ '<br><br>'
	
	+ 'Error Message:<br>'
	
	+ errorMsg
	
	+ '<br><br>'
	
	+ '</div>';
		
		
	$("body").append( html );
	
	window.scroll(0, 0);  // scroll to top left, assuming message is in that corner

}
	
			
			