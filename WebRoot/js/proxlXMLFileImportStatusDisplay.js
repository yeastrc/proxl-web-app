
"use strict";

///////////////////////////////////////////

//   proxlXMLFileImportStatusDisplay.js

//  On View Project page, manage display and update of Proxl XML Import status


//  If the status changes in the DB, prompt the user to refresh the page with an overlay


//  !!!!!!!!!   The setTimeout for recheck is currently commented out


$(document).ready(function() {
	
	proxlXMLFileImportStatusDisplay.initOnDocumentReady();
});


//Constructor

var ProxlXMLFileImportStatusDisplay = function() {	
	this.initialize();
	
};


ProxlXMLFileImportStatusDisplay.prototype.initialize  = function(  ) {


};


ProxlXMLFileImportStatusDisplay.prototype.initOnDocumentReady  = function(  ) {

	var objectThis = this;
	
	$("#upload_data_expand_show_data").click(function(eventObject) {

		var clickThis = this;
		
		$( this ).hide();
		$("#upload_data_collapse_hide_data").show();
		$("#upload_data_main_collapsable_jq").show();

		objectThis.showUploadDataClicked( clickThis, eventObject );

		return false;
	});

	$("#upload_data_collapse_hide_data").click(function(eventObject) {

		var clickThis = this;

		$( this ).hide();
		$("#upload_data_expand_show_data").show();
		$("#upload_data_main_collapsable_jq").hide();
		
		objectThis.hideUploadDataClicked( clickThis, eventObject );

		return false;
	});
	
	
//  TODO  Turned off
	
	

//	$(".proxl_xml_file_upload_status_changed_overlay_cancel_parts_jq").click(function(eventObject) {
//
//		var clickThis = this;
//
//		objectThis.cancelClicked( clickThis, eventObject );
//
//		return false;
//	});
//
//
//	$("#proxl_xml_file_upload_status_changed_refresh_page_button").click(function(eventObject) {
//
//		var clickThis = this;
//
//		objectThis.refreshPageClicked( clickThis, eventObject );
//
//		return false;
//	});
	
	

//	this.populatePendingCount();  do initial population in the Struts Action instead 
	

//  TODO  Turned off
	
	
	//  Do initial status change check to get initial status values
	
//	this.checkForStatusChange();


};


ProxlXMLFileImportStatusDisplay.prototype.CONSTANTS = { 

		CHECK_FOR_CHANGE_STATUS_DELAY : 60 * 1000  //  Check every 60 seconds

//		CHECK_FOR_CHANGE_STATUS_DELAY : 1 * 1000  // TODO  TEMP Check every 1 seconds

		,
		
		UPDATE_PENDING_COUNT_DELAY : 60 * 1000  //  Check every 60 seconds
		
		,
		
		DATA_ATTR_FOR_UPLOAD_DATA_LOADED : "UPLOAD_DATA_LOADED"
};


ProxlXMLFileImportStatusDisplay.prototype.showUploadDataClicked = function( clickThis, eventObject ) {
	
	var $upload_data_items_table = $("#upload_data_items_table");
	
	
	var dataLoaded = $upload_data_items_table.attr( this.CONSTANTS.DATA_ATTR_FOR_UPLOAD_DATA_LOADED );
	
	if ( dataLoaded ) {
		
		return;   // data already loaded
	}
	
	this.populateDataBlock();
};


//  Possibly not used

//  Populate pending count         

ProxlXMLFileImportStatusDisplay.prototype.populatePendingCount = function( ) {

//	Anything that isn't failed or finished

	var objectThis = this;
	
	
	var $project_id = $("#project_id");
	var projectId = $project_id.val();
	

	
	//  Check if status has changed
	

	var _URL = contextPathJSVar + "/services/proxl_xml_file_import/pendingCount";

	var requestData = { project_id : projectId };

	// var request =
	$.ajax({
		type : "GET",
		url : _URL,
		data : requestData,
		
		traditional: true,  //  Force traditional serialization of the data sent
		//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
		//   So searchIds array is passed as "searchIds=<value>" which is what Jersey expects

		dataType : "json",
		success : function(data) {

			objectThis.populatePendingCountProcessResponse( { responseData : data, requestData : requestData } );
			
			//  check again after the delay
			
//			setTimeout( function() {
//					objectThis.populatePendingCount();
//				}, objectThis.CONSTANTS.UPDATE_PENDING_COUNT_DELAY );
			
		},
		failure: function(errMsg) {
			
			// silently let fail
			
//			handleAJAXFailure( errMsg );
		},
		error : function(jqXHR, textStatus, errorThrown) {

			// silently let fail
			
//			handleAJAXError(jqXHR, textStatus, errorThrown);

			// alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
			// textStatus: " + textStatus );
		}
	});
};

ProxlXMLFileImportStatusDisplay.prototype.populatePendingCountProcessResponse  = function( params ) {

//	var requestData = params.requestData;
	var responseData = params.responseData;
	
	var pendingCount = responseData.pendingCount;
	
	if ( pendingCount > 0 ) {
		
		
	} else {
		
		
	}
	
	$("#upload_data_pending_number").text( pendingCount );
	$("#upload_data_pending_block").show();
	
};


//  Populate the data block

ProxlXMLFileImportStatusDisplay.prototype.populateDataBlock = function( ) {

//	Anything that isn't failed or finished

	var objectThis = this;
	
	
	var $project_id = $("#project_id");
	var projectId = $project_id.val();
	

	
	//  Check if status has changed
	

	var _URL = contextPathJSVar + "/services/proxl_xml_file_import/trackingDataList";

	var requestData = { project_id : projectId };

	// var request =
	$.ajax({
		type : "GET",
		url : _URL,
		data : requestData,
		
		traditional: true,  //  Force traditional serialization of the data sent
		//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
		//   So searchIds array is passed as "searchIds=<value>" which is what Jersey expects

		dataType : "json",
		success : function(data) {

			objectThis.populateDataBlockProcessResponse( { responseData : data, requestData : requestData } );
			
		},
		failure: function(errMsg) {
			
			handleAJAXFailure( errMsg );
		},
		error : function(jqXHR, textStatus, errorThrown) {

			handleAJAXError(jqXHR, textStatus, errorThrown);

			// alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
			// textStatus: " + textStatus );
		}
	});
	
	//  Compile needed Handlebars Templates
	
	if ( this.handlebarsTemplate_proxl_xml_import_item_template === undefined ) {

		var handlebarsSource_proxl_xml_import_item_template = $( "#proxl_xml_import_item_template" ).html();

		if ( handlebarsSource_proxl_xml_import_item_template === undefined ) {
			throw "handlebarsSource_proxl_xml_import_item_template === undefined";
		}
		if ( handlebarsSource_proxl_xml_import_item_template === null ) {
			throw "handlebarsSource_proxl_xml_import_item_template === null";
		}

		this.handlebarsTemplate_proxl_xml_import_item_template = Handlebars.compile( handlebarsSource_proxl_xml_import_item_template );
	}
	
};

ProxlXMLFileImportStatusDisplay.prototype.populateDataBlockProcessResponse  = function( params ) {

//	var requestData = params.requestData;
	var responseDataArray = params.responseData;
	
	var $upload_data_items_table = $("#upload_data_items_table");
	
	$upload_data_items_table.empty();

	//  Add data to the page

	for ( var dataIndex = 0; dataIndex < responseDataArray.length ; dataIndex++ ) {

		var dataItem = responseDataArray[ dataIndex ];

		var context = dataItem;
	
		var html = this.handlebarsTemplate_proxl_xml_import_item_template( context );
		
		var $upload_data_item = $(html).appendTo( $upload_data_items_table );
		
		addToolTips( $upload_data_item );
	}

	if ( window.proxlXMLFileImportUserUpdates ) {
		
		try {
	
			window.proxlXMLFileImportUserUpdates.addClickHandlers();
		} catch (e) {
			
			
		}
	}
};



ProxlXMLFileImportStatusDisplay.prototype.openOverlay  = function( ) {
	

	$("#proxl_xml_file_upload_status_changed_overlay_background").show();
	$("#proxl_xml_file_upload_status_changed_overlay_container").show();
	
};


//  User clicked the "Cancel" button or clicked on the background

ProxlXMLFileImportStatusDisplay.prototype.cancelClicked  = function( clickThis, eventObject ) {

	$("#proxl_xml_file_upload_status_changed_overlay_background").hide();
	$("#proxl_xml_file_upload_status_changed_overlay_container").hide();

};

//  User clicked the "Refresh" button 

ProxlXMLFileImportStatusDisplay.prototype.refreshPageClicked  = function( clickThis, eventObject ) {

	//  reload current URL
	
	window.location.reload(true);
};



ProxlXMLFileImportStatusDisplay.prototype.checkForStatusChange  = function( ) {

	var objectThis = this;
	
	
	var $project_id = $("#project_id");
	var projectId = $project_id.val();
	

	
	//  Check if status has changed
	

	var _URL = contextPathJSVar + "/services/proxl_xml_file_import/trackingIdStatusIdList";

	var requestData = { project_id : projectId };

	// var request =
	$.ajax({
		type : "GET",
		url : _URL,
		data : requestData,
		
		traditional: true,  //  Force traditional serialization of the data sent
		//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
		//   So searchIds array is passed as "searchIds=<value>" which is what Jersey expects

		dataType : "json",
		success : function(data) {

			objectThis.checkForStatusChangeProcessResponse( { responseData : data, requestData : requestData } );
			
			//  check again after the delay
			
			setTimeout( function() {
					objectThis.checkForStatusChange();
				}, objectThis.CONSTANTS.CHECK_FOR_CHANGE_STATUS_DELAY );
			
		},
		failure: function(errMsg) {
			
			// silently let fail
			
//			handleAJAXFailure( errMsg );
		},
		error : function(jqXHR, textStatus, errorThrown) {

			// silently let fail
			
//			handleAJAXError(jqXHR, textStatus, errorThrown);

			// alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
			// textStatus: " + textStatus );
		}
	});
};


ProxlXMLFileImportStatusDisplay.prototype.checkForStatusChangeProcessResponse  = function( params ) {

//	var requestData = params.requestData;
	var responseDataArray = params.responseData;
	
	//  Sort response on trackingId
	
	responseDataArray.sort( function(a, b) {
		  return a.trackingId - b.trackingId;
		} );
	
	
	if ( this.prevStatusDataArray === undefined ) {
		
		//  initial data retrieved
		
		this.prevStatusDataArray = responseDataArray;
		
		return;  //  EARLY EXIT
	}
	
	// Compare prev data to response
	
	if ( this.prevStatusDataArray.length !== responseDataArray.length ) {
		
		this.openOverlay();
		
		this.prevStatusDataArray = responseDataArray;
		
		return;  //  EARLY EXIT
	}
	
	// compare the array contents, item by item
	
	for ( var index = 0; index < responseDataArray.length; index++ ) {
		
		var responseDataItem = responseDataArray[ index ];
		var prevStatusDataItem = this.prevStatusDataArray[ index ];
		
		if ( responseDataItem.trackingId !== prevStatusDataItem.trackingId
				|| responseDataItem.statusId !== prevStatusDataItem.statusId ) {
			
			//  Found difference so prompt user to refresh page

			this.openOverlay();
			
			this.prevStatusDataArray = responseDataArray;
			
			return;  //  EARLY EXIT
		}
	}
	

	this.prevStatusDataArray = responseDataArray;
};



///////////////

//  Instance of object on page

var proxlXMLFileImportStatusDisplay = new ProxlXMLFileImportStatusDisplay();
