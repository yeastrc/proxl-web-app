
"use strict";

///////////////////////////////////////////


//   proxlXMLFileImportUserUpdates.js

//  On View Project page, user manages Proxl XML Import entries 



$(document).ready(function() {

//	proxlXMLFileImportUserUpdates.initOnDocumentReady();
});



//Constructor

var ProxlXMLFileImportUserUpdates = function() {	
	
	try {
		
		this.initialize();

	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
	
};


ProxlXMLFileImportUserUpdates.prototype.initialize  = function(  ) {


};


ProxlXMLFileImportUserUpdates.prototype.initOnDocumentReady  = function(  ) {

	
};



ProxlXMLFileImportUserUpdates.prototype.addClickHandlers  = function(  ) {

	var objectThis = this;
	


	$(".proxl_xml_import_item_cancel_queued_jq").click(function(eventObject) {

		try {

			var clickThis = this;

			objectThis.cancelQueuedItemClicked( { cancelQueued : true, clickThis : clickThis, eventObject : eventObject } );
			eventObject.preventDefault();
			
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});


	$(".cancel_queued_item_yes_button_jq").click(function(eventObject) {

		try {

			var clickThis = this;

			objectThis.cancelQueuedItemConfirmedClicked(  { cancelQueued : true, clickThis : clickThis, eventObject : eventObject } );
			eventObject.preventDefault();
			
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});



	$(".proxl_xml_import_item_cancel_re_queued_jq").click(function(eventObject) {

		try {

			var clickThis = this;

			objectThis.cancelQueuedItemClicked( { cancelRequeued : true, clickThis : clickThis, eventObject : eventObject } );

			return false;
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});


	$(".cancel_re_queued_item_yes_button_jq").click(function(eventObject) {

		try {

			var clickThis = this;

			objectThis.cancelQueuedItemConfirmedClicked(  { cancelQueued : true, clickThis : clickThis, eventObject : eventObject } );

			return false;
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});





	$(".proxl_xml_import_item_remove_failed_jq").click(function(eventObject) {

		try {

			var clickThis = this;

			objectThis.removeFailedItemClicked( clickThis, eventObject );

			return false;
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});


	$(".remove_failed_item_yes_button_jq").click(function(eventObject) {

		try {

			var clickThis = this;

			objectThis.removeFailedItemConfirmedClicked( clickThis, eventObject );

			return false;
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});


	$(".proxl_xml_import_item_remove_completed_jq").click(function(eventObject) {

		try {

			var clickThis = this;

			objectThis.removeCompletedItemClicked( clickThis, eventObject );

			return false;
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});

	$(".remove_completed_item_yes_button_jq").click(function(eventObject) {

		try {

			var clickThis = this;

			objectThis.removeCompletedItemConfirmedClicked( clickThis, eventObject );

			return false;
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});


	$(".import_proxl_xml_file_confirm_remove_upload_overlay_cancel_parts_jq").click(function(eventObject) {

		try {

//			var clickThis = this;

			$(".import_proxl_xml_file_confirm_remove_upload_overlay_show_hide_parts_jq").hide();

			return false;
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});

};


ProxlXMLFileImportUserUpdates.prototype.CONSTANTS = { 

};



//  User clicked the "Cancel Queued" link 

ProxlXMLFileImportUserUpdates.prototype.cancelQueuedItemClicked  = function( params ) {

	var cancelQueued = params.cancelQueued;
//	var cancelRequeued = params.cancelRequeued;

	var clickThis = params.clickThis;
//	var eventObject = params.eventObject;
		

	var $clickThis = $( clickThis );
	

	var $proxl_xml_import_item_row_jq = $clickThis.closest(".proxl_xml_import_item_row_jq");
	
	var tracking_id =  $proxl_xml_import_item_row_jq.attr("data-tracking_id");

	var status_id =  $proxl_xml_import_item_row_jq.attr("data-status_id");
	
	var filename = $proxl_xml_import_item_row_jq.attr("data-filename");

	var $import_proxl_xml_file_confirm_remove_upload_overlay_container = $("#import_proxl_xml_file_confirm_remove_upload_overlay_container");

	var $filename_jq = $import_proxl_xml_file_confirm_remove_upload_overlay_container.find(".filename_jq");
	
	$filename_jq.text( filename );
	
	var $any_item_jq = $import_proxl_xml_file_confirm_remove_upload_overlay_container.find(".any_item_jq");
	
	$any_item_jq.hide();
	
	if ( cancelQueued ) {

		var $cancel_queued_item_jq = $import_proxl_xml_file_confirm_remove_upload_overlay_container.find(".cancel_queued_item_jq");

		$cancel_queued_item_jq.show();

		var $cancel_queued_item_yes_button_jq = $import_proxl_xml_file_confirm_remove_upload_overlay_container.find(".cancel_queued_item_yes_button_jq");

		$cancel_queued_item_yes_button_jq.show();

		$cancel_queued_item_yes_button_jq.data( { tracking_id : tracking_id, status_id : status_id } );
		
		
	} else {

		var $cancel_re_queued_item_jq = $import_proxl_xml_file_confirm_remove_upload_overlay_container.find(".cancel_re_queued_item_jq");

		$cancel_re_queued_item_jq.show();

		var $cancel_re_queued_item_yes_button_jq = $import_proxl_xml_file_confirm_remove_upload_overlay_container.find(".cancel_re_queued_item_yes_button_jq");

		$cancel_re_queued_item_yes_button_jq.show();

		$cancel_re_queued_item_yes_button_jq.data( { tracking_id : tracking_id, status_id : status_id } );
		
	}
	
	$(".import_proxl_xml_file_confirm_remove_upload_overlay_show_hide_parts_jq").show();
};


//  User clicked the "Cancel Queued" link 

ProxlXMLFileImportUserUpdates.prototype.cancelQueuedItemConfirmedClicked  = function( params ) {

	var objectThis = this;
	
	var clickThis = params.clickThis;
	
	var $clickThis = $( clickThis );
	

	var tracking_id = $clickThis.data("tracking_id");
	
	var status_id = $clickThis.data("status_id");
	
	var requestData = { tracking_id : tracking_id, status_id : status_id };


	var _URL = contextPathJSVar + "/services/file_import_proxl_xml_scans/cancelQueuedImport";

	$.ajax({
		type: "POST",
		url: _URL,
		data: requestData,
		dataType: "json",
		success: function( responseData )	{

			try {

				var responseParams = {
						responseData : responseData,
						clickThis : clickThis,
						tracking_id : tracking_id
				};

				objectThis.cancelQueuedItemClickedProcessAjaxResponse( responseParams );

			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		},
		failure: function(errMsg) {
			handleAJAXFailure( errMsg );
		},
		error: function(jqXHR, textStatus, errorThrown) {	

			handleAJAXError( jqXHR, textStatus, errorThrown );
		}
	});

};

ProxlXMLFileImportUserUpdates.prototype.cancelQueuedItemClickedProcessAjaxResponse  = function( responseParams ) {

//	var responseData = responseParams.responseData;
//	var clickThis = responseParams.clickThis;
//	var tracking_id = responseParams.tracking_id;

//	if ( responseData.success ) {
//
//		alert("Successful cancel" );
//	}
//	
//	if ( responseData.statusNotQueued ) {
//
//		alert("Unable to cancel since status is no longer queued" );
//	}
//	
//	
//	if ( responseData.statusNotRequeued ) {
//
//		alert("Unable to cancel since status is no longer requeued" );
//	}	
		
	$(".import_proxl_xml_file_confirm_remove_upload_overlay_show_hide_parts_jq").hide();

	proxlXMLFileImportStatusDisplay.populateDataBlockAndPendingCount();
	
};

///////////////////////////////
///////////////////////////////


//  User clicked the "Remove Failed" link 

ProxlXMLFileImportUserUpdates.prototype.removeFailedItemClicked  = function( clickThis, eventObject ) {

//	var objectThis = this;

	var $clickThis = $( clickThis );

	var $proxl_xml_import_item_row_jq = $clickThis.closest(".proxl_xml_import_item_row_jq");

	var tracking_id =  $proxl_xml_import_item_row_jq.attr("data-tracking_id");

	var filename = $proxl_xml_import_item_row_jq.attr("data-filename");

	var $import_proxl_xml_file_confirm_remove_upload_overlay_container = $("#import_proxl_xml_file_confirm_remove_upload_overlay_container");

	var $filename_jq = $import_proxl_xml_file_confirm_remove_upload_overlay_container.find(".filename_jq");
	
	$filename_jq.text( filename );
	
	var $any_item_jq = $import_proxl_xml_file_confirm_remove_upload_overlay_container.find(".any_item_jq");
	
	$any_item_jq.hide();

	var $remove_failed_item_jq = $import_proxl_xml_file_confirm_remove_upload_overlay_container.find(".remove_failed_item_jq");

	$remove_failed_item_jq.show();

	var $remove_failed_item_yes_button_jq = $import_proxl_xml_file_confirm_remove_upload_overlay_container.find(".remove_failed_item_yes_button_jq");

	$remove_failed_item_yes_button_jq.show();

	$remove_failed_item_yes_button_jq.data( { tracking_id : tracking_id } );

	$(".import_proxl_xml_file_confirm_remove_upload_overlay_show_hide_parts_jq").show();
	
	
};


//User clicked the "Remove Failed" link 

ProxlXMLFileImportUserUpdates.prototype.removeFailedItemConfirmedClicked  = function( clickThis, eventObject ) {

	var objectThis = this;

	var $clickThis = $( clickThis );
	
	var tracking_id =  $clickThis.data("tracking_id");
	
	var requestData = { tracking_id : tracking_id };


	var _URL = contextPathJSVar + "/services/file_import_proxl_xml_scans/removeFailedImport";

	$.ajax({
		type: "POST",
		url: _URL,
		data: requestData,
		dataType: "json",
		success: function( responseData )	{

			try {

				var responseParams = {
						responseData : responseData,
						clickThis : clickThis,
						tracking_id : tracking_id
				};

				objectThis.removeFailedItemClickedProcessAjaxResponse( responseParams );

			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		},
		failure: function(errMsg) {
			handleAJAXFailure( errMsg );
		},
		error: function(jqXHR, textStatus, errorThrown) {	

			handleAJAXError( jqXHR, textStatus, errorThrown );
		}
	});

};

ProxlXMLFileImportUserUpdates.prototype.removeFailedItemClickedProcessAjaxResponse  = function( responseParams ) {

//	var responseData = responseParams.responseData;
//	var clickThis = responseParams.clickThis;
//	var tracking_id = responseParams.tracking_id;

//	if ( responseData.success ) {
//
//		alert("Successful removal" );
//	}
//
//	if ( responseData.statusNotFailed ) {
//
//		alert("Unable to remove since status is no longer failed" );
//	}

	$(".import_proxl_xml_file_confirm_remove_upload_overlay_show_hide_parts_jq").hide();


	proxlXMLFileImportStatusDisplay.populateDataBlockAndPendingCount();
};



///////////////////////////////
///////////////////////////////


//  User clicked the "Remove Completed" link 

ProxlXMLFileImportUserUpdates.prototype.removeCompletedItemClicked  = function( clickThis, eventObject ) {

//	var objectThis = this;

	var $clickThis = $( clickThis );

	var $proxl_xml_import_item_row_jq = $clickThis.closest(".proxl_xml_import_item_row_jq");

	var tracking_id =  $proxl_xml_import_item_row_jq.attr("data-tracking_id");

	var filename = $proxl_xml_import_item_row_jq.attr("data-filename");

	var $import_proxl_xml_file_confirm_remove_upload_overlay_container = $("#import_proxl_xml_file_confirm_remove_upload_overlay_container");

	var $filename_jq = $import_proxl_xml_file_confirm_remove_upload_overlay_container.find(".filename_jq");
	
	$filename_jq.text( filename );
	
	var $any_item_jq = $import_proxl_xml_file_confirm_remove_upload_overlay_container.find(".any_item_jq");

	$any_item_jq.hide();

	var $remove_completed_item_jq = $import_proxl_xml_file_confirm_remove_upload_overlay_container.find(".remove_completed_item_jq");

	$remove_completed_item_jq.show();

	var $remove_completed_item_yes_button_jq = $import_proxl_xml_file_confirm_remove_upload_overlay_container.find(".remove_completed_item_yes_button_jq");

	$remove_completed_item_yes_button_jq.show();

	$remove_completed_item_yes_button_jq.data( { tracking_id : tracking_id } );

	$(".import_proxl_xml_file_confirm_remove_upload_overlay_show_hide_parts_jq").show();


};


//  User clicked the "Remove Completed" link 

ProxlXMLFileImportUserUpdates.prototype.removeCompletedItemConfirmedClicked  = function( clickThis, eventObject ) {

	var objectThis = this;

	var $clickThis = $( clickThis );

	var tracking_id =  $clickThis.data("tracking_id");

	var requestData = { tracking_id : tracking_id };


	var _URL = contextPathJSVar + "/services/file_import_proxl_xml_scans/removeCompletedImport";

	$.ajax({
		type: "POST",
		url: _URL,
		data: requestData,
		dataType: "json",
		success: function( responseData )	{

			try {

				var responseParams = {
						responseData : responseData,
						clickThis : clickThis,
						tracking_id : tracking_id
				};

				objectThis.removeFailedItemClickedProcessAjaxResponse( responseParams );
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		},
		failure: function(errMsg) {
			handleAJAXFailure( errMsg );
		},
		error: function(jqXHR, textStatus, errorThrown) {	

			handleAJAXError( jqXHR, textStatus, errorThrown );
		}
	});

};

ProxlXMLFileImportUserUpdates.prototype.removeFailedItemClickedProcessAjaxResponse  = function( responseParams ) {

//	var responseData = responseParams.responseData;
//	var clickThis = responseParams.clickThis;
//	var tracking_id = responseParams.tracking_id;

//	if ( responseData.success ) {

//	alert("Successful removal" );
//	}

//	if ( responseData.statusNotFailed ) {

//	alert("Unable to remove since status is no longer failed" );
//	}

	$(".import_proxl_xml_file_confirm_remove_upload_overlay_show_hide_parts_jq").hide();

	proxlXMLFileImportStatusDisplay.populateDataBlockAndPendingCount();
};

///////////////

//Instance of object on page

var proxlXMLFileImportUserUpdates = new ProxlXMLFileImportUserUpdates();


