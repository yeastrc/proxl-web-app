
"use strict";

///////////////////////////////////////////

//   proxlXMLFileImportStatusDisplay.js

//  On View Project page, manage display and update of Proxl XML Import status


//  If the status changes in the DB, prompt the user to refresh the page with an overlay



$(document).ready(function() {
	
	proxlXMLFileImportStatusDisplay.initOnDocumentReady();
});


//Constructor

var ProxlXMLFileImportStatusDisplay = function() {	
	
	this.initialize();
};




ProxlXMLFileImportStatusDisplay.prototype.CONSTANTS = { 

//		CHECK_FOR_CHANGE_STATUS_DELAY : 60 * 1000  //  Check every 60 seconds

//		CHECK_FOR_CHANGE_STATUS_DELAY : 1 * 1000  // TODO  TEMP Check every 1 seconds
//		,
		
//		UPDATE_PENDING_COUNT_DELAY : 60 * 1000  //  Check every 60 seconds
//		,
		
		//  Auto Refresh for certain status values

		AUTO_REFRESH_DELAY : 60 * 1000  //  In Milliseconds  Check every 60 seconds
		,
		
//		AUTO_REFRESH_DELAY : 3 * 1000  //  In Milliseconds  Check every 3 seconds
//		,

		
		DATA_ATTR_FOR_UPLOAD_DATA_LOADED : "UPLOAD_DATA_LOADED",
		
		DATA_ATTR_FOR_UPLOAD_DATA_SHOWING : "UPLOAD_DATA_SHOWING"

};


ProxlXMLFileImportStatusDisplay.prototype.initialize  = function(  ) {

	this.statusIdQueued = undefined;
	this.statusIdRequeued = undefined;
	this.statusIdStarted = undefined;
	this.statusIdComplete = undefined;
	this.statusIdFailed = undefined;
	
	this.prevCompleteSuccessTrackingIdList = undefined;
	this.autoRefreshTimerId = undefined;

};


ProxlXMLFileImportStatusDisplay.prototype.initOnDocumentReady  = function(  ) {

	var objectThis = this;
	
	$("#upload_data_expand_show_data").click(function(eventObject) {

		var clickThis = this;
		
		$( this ).hide();
		$("#upload_data_collapse_hide_data").show();
		$("#upload_data_main_collapsable_jq").show();
		
		$("#upload_data_top_level_container").data( objectThis.CONSTANTS.DATA_ATTR_FOR_UPLOAD_DATA_SHOWING, true );

		objectThis.showUploadDataClicked( clickThis, eventObject );

		return false;
	});

	$("#upload_data_collapse_hide_data").click(function(eventObject) {

//		var clickThis = this;

		$( this ).hide();
		$("#upload_data_expand_show_data").show();
		$("#upload_data_main_collapsable_jq").hide();
		
		$("#upload_data_top_level_container").data( objectThis.CONSTANTS.DATA_ATTR_FOR_UPLOAD_DATA_SHOWING, false );

		return false;
	});
	

	$("#upload_data_refresh_data").click(function(eventObject) {

		var clickThis = this;
		
		objectThis.refreshDataClicked( clickThis, eventObject );

		return false;
	});
	
	

	$(".proxl_xml_file_upload_complete_successfully_overlay_cancel_parts_jq").click(function(eventObject) {

		var clickThis = this;

		objectThis.cancelClicked( clickThis, eventObject );

		return false;
	});


	$("#proxl_xml_file_upload_complete_successfully_refresh_page_button").click(function(eventObject) {

		var clickThis = this;

		objectThis.refreshPageClicked( clickThis, eventObject );

		return false;
	});
	
	this.statusIdQueued = $("#proxl_xml_file_upload_complete_successfully_status_id_queued").val();
	this.statusIdRequeued = $("#proxl_xml_file_upload_complete_successfully_status_id_re_queued").val();
	this.statusIdStarted = $("#proxl_xml_file_upload_complete_successfully_status_id_started").val();
	this.statusIdComplete = $("#proxl_xml_file_upload_complete_successfully_status_id_complete").val();
	this.statusIdFailed = $("#proxl_xml_file_upload_complete_successfully_status_id_failed").val();
									

	$("#upload_data_pending_items_show_link").click(function(eventObject) {

		$( this ).hide();
		$("#upload_data_pending_items_container").show();
		$("#upload_data_pending_items_hide_link").show();

		return false;
	});

	$("#upload_data_pending_items_hide_link").click(function(eventObject) {

		$( this ).hide();
		$("#upload_data_pending_items_container").hide();
		$("#upload_data_pending_items_show_link").show();

		return false;
	});
	

	$("#upload_data_history_items_show_link").click(function(eventObject) {

		$( this ).hide();
		$("#upload_data_history_items_container").show();
		$("#upload_data_history_items_hide_link").show();

		$("#upload_data_history_items_block").data( objectThis.CONSTANTS.DATA_ATTR_FOR_UPLOAD_DATA_SHOWING, true );
		
		return false;
	});

	$("#upload_data_history_items_hide_link").click(function(eventObject) {

		$( this ).hide();
		$("#upload_data_history_items_container").hide();
		$("#upload_data_history_items_show_link").show();

		$("#upload_data_history_items_block").data( objectThis.CONSTANTS.DATA_ATTR_FOR_UPLOAD_DATA_SHOWING, false );
		
		return false;
	});
	
	

//	this.populatePendingCount();  do initial population in the Struts Action instead 
	

//  TODO  Turned off
	
	
	//  Do initial status change check to get initial status values
	
//	this.checkForStatusChange();


};


ProxlXMLFileImportStatusDisplay.prototype.showUploadDataClicked = function( clickThis, eventObject ) {
	
	var $upload_data_pending_and_history_items_block = $("#upload_data_pending_and_history_items_block");
	
	
	var dataLoaded = $upload_data_pending_and_history_items_block.attr( this.CONSTANTS.DATA_ATTR_FOR_UPLOAD_DATA_LOADED );
	
	if ( dataLoaded ) {
		
		return;   // data already loaded
	}
	
	this.populateDataBlockAndPendingCount();
};



ProxlXMLFileImportStatusDisplay.prototype.refreshDataClicked = function( clickThis, eventObject ) {

	this.populateDataBlockAndPendingCount();
};


//  Populate the data block

ProxlXMLFileImportStatusDisplay.prototype.populateDataBlockAndPendingCount = function( params ) {

	var objectThis = this;
	
	var fromAutoRefresh = undefined;
	
	if ( params ) {

		fromAutoRefresh = params.fromAutoRefresh;
	}
	

	var $project_id = $("#project_id");
	var projectId = $project_id.val();
	
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

			objectThis.populateDataBlockAndPendingCountProcessResponse( { responseData : data, requestData : requestData } );
			
		},
		failure: function(errMsg) {
			
			if ( ! fromAutoRefresh ) {

				handleAJAXFailure( errMsg );
			}
		},
		error : function(jqXHR, textStatus, errorThrown) {

			if ( ! fromAutoRefresh ) {

				handleAJAXError(jqXHR, textStatus, errorThrown);
			}

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
	

	if ( this.handlebarsTemplate_proxl_xml_import_item_tooltip_template === undefined ) {

		var handlebarsSource_proxl_xml_import_item_tooltip_template = $( "#proxl_xml_import_item_tooltip_template" ).html();

		if ( handlebarsSource_proxl_xml_import_item_tooltip_template === undefined ) {
			throw "handlebarsSource_proxl_xml_import_item_tooltip_template === undefined";
		}
		if ( handlebarsSource_proxl_xml_import_item_tooltip_template === null ) {
			throw "handlebarsSource_proxl_xml_import_item_tooltip_template === null";
		}

		this.handlebarsTemplate_proxl_xml_import_item_tooltip_template = Handlebars.compile( handlebarsSource_proxl_xml_import_item_tooltip_template );
	}
	
	
	
};

ProxlXMLFileImportStatusDisplay.prototype.populateDataBlockAndPendingCountProcessResponse  = function( params ) {

	var objectThis = this;
	
//	var requestData = params.requestData;
	var responseData = params.responseData;
	
	var pendingCount = responseData.pendingCount;
	
	var pendingItemsList = responseData.pendingItemsList;
	var historyItemsList = responseData.historyItemsList;
	
	var completeSuccessTrackingIdList = responseData.completeSuccessTrackingIdList;
	
	if ( pendingCount > 0 ) {
		
		
	} else {
		
		
	}
	
	$("#upload_data_pending_number").text( pendingCount );
	$("#upload_data_pending_block").show();
	
	

	
	var submit_process_date_time_jqMaxWidth = 0;
	
	var $submit_process_date_time_jq_Pending = null;
	var $submit_process_date_time_jq_History = null;
	
	
	
	///  Process pendingItemsList
	
	if ( pendingItemsList.length === 0 ) {
		
		//  No Pending to display
		
//		$("#upload_data_pending_items_block").hide();
		

		$("#upload_data_pending_items_block").show();
		
		$("#upload_data_pending_items_table").hide();
		$("#upload_data_pending_items_no_pending_text").show();
		
		
		
	} else {
	
		//  Have Pending to display

		$("#upload_data_pending_items_block").show();

		$("#upload_data_pending_items_table").show();
		$("#upload_data_pending_items_no_pending_text").hide();

		var $upload_data_pending_items_table = $("#upload_data_pending_items_table");


		var $filename_status_cell_jq_ALL = $upload_data_pending_items_table.find(".filename_status_cell_jq");

		$filename_status_cell_jq_ALL.qtip('destroy', true); // Immediately destroy all tooltips belonging to the selected elements


		$upload_data_pending_items_table.empty();
		
		var $upload_data_item = null;

		//  Add data to the page

		for ( var dataIndex = 0; dataIndex < pendingItemsList.length ; dataIndex++ ) {

			var dataItem = pendingItemsList[ dataIndex ];

			var context = dataItem;

			var html = this.handlebarsTemplate_proxl_xml_import_item_template( context );

			$upload_data_item = $(html).appendTo( $upload_data_pending_items_table );

			addToolTips( $upload_data_item );


			var tooltipHtml = this.handlebarsTemplate_proxl_xml_import_item_tooltip_template( context );

			var $filename_status_cell_jq = $upload_data_item.find(".filename_status_cell_jq");

			$filename_status_cell_jq.qtip( {
				content: {
					text: tooltipHtml
				},
				style: {
					classes: 'upload-search-tooltip' // add this class to the tool tip
				},
				position: {
					my: 'bottom right',
					at: 'top left',
					viewport: $(window)
				}
			});	
		}

		$submit_process_date_time_jq_Pending = $upload_data_pending_items_table.find(".submit_process_date_time_jq");
		
		var submit_process_date_time_jqWidth = $submit_process_date_time_jq_Pending.width();
		

		if ( submit_process_date_time_jqMaxWidth < submit_process_date_time_jqWidth ) {
			
			submit_process_date_time_jqMaxWidth = submit_process_date_time_jqWidth;
		}
	}
	
	//  End processing pendingItemsList
	
	/////////////////

	///  Process historyItemsList
	
	if ( historyItemsList.length === 0 ) {
		
		//  No History to display
		
		$("#upload_data_history_items_block").hide();
		
		
	} else {
	
		//  Have History to display

		$("#upload_data_history_items_block").show();
		$("#upload_data_history_items_container").show();

		$("#upload_data_history_items_hide_link").show();
		$("#upload_data_history_items_show_link").hide();

		var $upload_data_history_items_table = $("#upload_data_history_items_table");

		var $filename_status_cell_jq_ALL = $upload_data_history_items_table.find(".filename_status_cell_jq");

		$filename_status_cell_jq_ALL.qtip('destroy', true); // Immediately destroy all tooltips belonging to the selected elements

		$upload_data_history_items_table.empty();
		
		var $upload_data_item = null;

		//  Add data to the page

		for ( var dataIndex = 0; dataIndex < historyItemsList.length ; dataIndex++ ) {

			var dataItem = historyItemsList[ dataIndex ];

			var context = dataItem;

			var html = this.handlebarsTemplate_proxl_xml_import_item_template( context );

			$upload_data_item = $(html).appendTo( $upload_data_history_items_table );

			addToolTips( $upload_data_item );


			var tooltipHtml = this.handlebarsTemplate_proxl_xml_import_item_tooltip_template( context );

			var $filename_status_cell_jq = $upload_data_item.find(".filename_status_cell_jq");

			$filename_status_cell_jq.qtip( {
				content: {
					text: tooltipHtml
				},
				style: {
					classes: 'upload-search-tooltip' // add this class to the tool tip
				},
				position: {
					my: 'bottom right',
					at: 'top left',
					viewport: $(window)
				}
			});	
		}
		
		$submit_process_date_time_jq_History = $upload_data_history_items_table.find(".submit_process_date_time_jq");
		
		var submit_process_date_time_jqWidth = $submit_process_date_time_jq_History.width();
		
		if ( submit_process_date_time_jqMaxWidth < submit_process_date_time_jqWidth ) {
			
			submit_process_date_time_jqMaxWidth = submit_process_date_time_jqWidth;
		}
		
	}
	
	//  End processing historyItemsList
	
	submit_process_date_time_jqMaxWidth += 1;
	
	//  Submitted/Processed column set column to max width
	
	if ( $submit_process_date_time_jq_Pending ) {
		
		$submit_process_date_time_jq_Pending.css( "min-width" , submit_process_date_time_jqMaxWidth + "px" );
		$submit_process_date_time_jq_Pending.css( "max-width" , submit_process_date_time_jqMaxWidth + "px" );
	};
	
	if ( $submit_process_date_time_jq_History ) {
		
		$submit_process_date_time_jq_History.css( "min-width" , submit_process_date_time_jqMaxWidth + "px" );
		$submit_process_date_time_jq_History.css( "max-width" , submit_process_date_time_jqMaxWidth + "px" );
	};

	
	if ( pendingItemsList.length !== 0 ) {
		
		//  Hide History data since have pending data

		var dataShowing = $("#upload_data_history_items_block").data( objectThis.CONSTANTS.DATA_ATTR_FOR_UPLOAD_DATA_SHOWING );
		
		if ( ! dataShowing ) {
		
			$("#upload_data_history_items_container").hide();

			$("#upload_data_history_items_hide_link").hide();
			$("#upload_data_history_items_show_link").show();
		}
	}

	
	

	if ( window.proxlXMLFileImportUserUpdates ) {
		
		try {
	
			window.proxlXMLFileImportUserUpdates.addClickHandlers();
		} catch (e) {
			
			
		}
	}
	
	this.eval_completeSuccessTrackingIdList_forAdditions( completeSuccessTrackingIdList );
	
	
	//  auto refresh for certain statuses
	
	
	if ( this.autoRefreshTimerId ) {
		
		//  clear existing timer since refreshed data now

		try {
			clearTimeout( this.autoRefreshTimerId );
		} catch (e) {
		}
	}
	
	var $upload_data_pending_and_history_items_block = $("#upload_data_pending_and_history_items_block");
	
	$upload_data_pending_and_history_items_block.attr( this.CONSTANTS.DATA_ATTR_FOR_UPLOAD_DATA_LOADED, true );
	

	var uploadDataShowing = $("#upload_data_top_level_container").data( this.CONSTANTS.DATA_ATTR_FOR_UPLOAD_DATA_SHOWING );


	
	if ( uploadDataShowing &&
			pendingItemsList && pendingItemsList.length > 0 ) {
		
		//  Auto Refresh after delay

		this.autoRefreshTimerId = setTimeout( function() {

			objectThis.autoRefreshTimerId = undefined;

			objectThis.populateDataBlockAndPendingCount( { fromAutoRefresh : true } );

		}, this.CONSTANTS.AUTO_REFRESH_DELAY );
	}
};

//////////

ProxlXMLFileImportStatusDisplay.prototype.eval_completeSuccessTrackingIdList_forAdditions  = function( completeSuccessTrackingIdList ) {
	

	//  Evaluate for auto refresh and alert user to successfully completed imports
	
	//     Show overlay if new entries have been added to the list
	
	if ( this.prevCompleteSuccessTrackingIdList ) {

			// compare the array contents, item by item
		
		var completeSuccessTrackingIdItemIndex = 0;
		var prevCompleteSuccessTrackingIdItemIndex = 0;
		
		var foundNewTrackingId = false;

		while ( true ) {

			var completeSuccessTrackingIdItem = completeSuccessTrackingIdList[ completeSuccessTrackingIdItemIndex ];
			var prevCompleteSuccessTrackingIdItem = this.prevCompleteSuccessTrackingIdList[ prevCompleteSuccessTrackingIdItemIndex ];

			if ( completeSuccessTrackingIdItem === prevCompleteSuccessTrackingIdItem ) {
				
				//  values equal so advance both indexes
				
				completeSuccessTrackingIdItemIndex++;
				prevCompleteSuccessTrackingIdItemIndex++;
				
			} else {

				if ( completeSuccessTrackingIdItem < prevCompleteSuccessTrackingIdItem ) {
					
					//  Found new entry in completeSuccessTrackingIdItem
					
					foundNewTrackingId = true;
					
					break;  //  EARLY EXIT FROM LOOP  
				}
				
				// completeSuccessTrackingIdItem > prevCompleteSuccessTrackingIdItem
				//   so advance prev 
				
				prevCompleteSuccessTrackingIdItemIndex++;

			}
			
			if ( prevCompleteSuccessTrackingIdItemIndex >= this.prevCompleteSuccessTrackingIdList.length ) {
				
				//  Came to end of prev list

				if ( completeSuccessTrackingIdItemIndex < completeSuccessTrackingIdList.length ) {
					
					//  Not at end of current list so has at least one new entry
					
					foundNewTrackingId = true;
					
					break;  //  EARLY EXIT FROM LOOP  
				}
					
				//   At end of both lists so they match
				
				break;  //  EARLY EXIT FROM LOOP  
			}
		}
		
		if ( foundNewTrackingId ) {
			

			//  Found new complete successful tracking id so prompt user to refresh page

			this.openUploadedCompletedSuccessfullyOverlay();
		}
	}
	
	//  save current as prev
	this.prevCompleteSuccessTrackingIdList = completeSuccessTrackingIdList;
	
};


ProxlXMLFileImportStatusDisplay.prototype.openUploadedCompletedSuccessfullyOverlay  = function( ) {
	

	$(".proxl_xml_file_upload_complete_successfully_overlay_show_hide_parts_jq").show();
	
};


//  User clicked the "Cancel" button or clicked on the background

ProxlXMLFileImportStatusDisplay.prototype.cancelClicked  = function( clickThis, eventObject ) {

	$(".proxl_xml_file_upload_complete_successfully_overlay_show_hide_parts_jq").hide();

};

//  User clicked the "Refresh" button 

ProxlXMLFileImportStatusDisplay.prototype.refreshPageClicked  = function( clickThis, eventObject ) {

	//  reload current URL
	
	window.location.reload(true);
};




//   Possibly not used

//ProxlXMLFileImportStatusDisplay.prototype.checkForStatusChange  = function( ) {
//
//	var objectThis = this;
//	
//	
//	var $project_id = $("#project_id");
//	var projectId = $project_id.val();
//	
//
//	
//	//  Check if status has changed
//	
//
//	var _URL = contextPathJSVar + "/services/proxl_xml_file_import/trackingIdStatusIdList";
//
//	var requestData = { project_id : projectId };
//
//	// var request =
//	$.ajax({
//		type : "GET",
//		url : _URL,
//		data : requestData,
//		
//		traditional: true,  //  Force traditional serialization of the data sent
//		//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
//		//   So searchIds array is passed as "searchIds=<value>" which is what Jersey expects
//
//		dataType : "json",
//		success : function(data) {
//
//			objectThis.checkForStatusChangeProcessResponse( { responseData : data, requestData : requestData } );
//			
//			//  check again after the delay
//			
//			setTimeout( function() {
//					objectThis.checkForStatusChange();
//				}, objectThis.CONSTANTS.CHECK_FOR_CHANGE_STATUS_DELAY );
//			
//		},
//		failure: function(errMsg) {
//			
//			// silently let fail
//			
////			handleAJAXFailure( errMsg );
//		},
//		error : function(jqXHR, textStatus, errorThrown) {
//
//			// silently let fail
//			
////			handleAJAXError(jqXHR, textStatus, errorThrown);
//
//			// alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
//			// textStatus: " + textStatus );
//		}
//	});
//};
//
//
//ProxlXMLFileImportStatusDisplay.prototype.checkForStatusChangeProcessResponse  = function( params ) {
//
////	var requestData = params.requestData;
//	var responseDataArray = params.responseData;
//	
//	//  Sort response on trackingId
//	
//	responseDataArray.sort( function(a, b) {
//		  return a.trackingId - b.trackingId;
//		} );
//	
//	
//	if ( this.prevStatusDataArray === undefined ) {
//		
//		//  initial data retrieved
//		
//		this.prevStatusDataArray = responseDataArray;
//		
//		return;  //  EARLY EXIT
//	}
//	
//	// Compare prev data to response
//	
//	if ( this.prevStatusDataArray.length !== responseDataArray.length ) {
//		
//		this.openOverlay();
//		
//		this.prevStatusDataArray = responseDataArray;
//		
//		return;  //  EARLY EXIT
//	}
//	
//	// compare the array contents, item by item
//	
//	for ( var index = 0; index < responseDataArray.length; index++ ) {
//		
//		var responseDataItem = responseDataArray[ index ];
//		var prevStatusDataItem = this.prevStatusDataArray[ index ];
//		
//		if ( responseDataItem.trackingId !== prevStatusDataItem.trackingId
//				|| responseDataItem.statusId !== prevStatusDataItem.statusId ) {
//			
//			//  Found difference so prompt user to refresh page
//
//			this.openOverlay();
//			
//			this.prevStatusDataArray = responseDataArray;
//			
//			return;  //  EARLY EXIT
//		}
//	}
//	
//
//	this.prevStatusDataArray = responseDataArray;
//};




//  Possibly not used

//  Populate pending count         
//
//ProxlXMLFileImportStatusDisplay.prototype.populatePendingCount = function( ) {
//
////	Anything that isn't failed or finished
//
//	var objectThis = this;
//
//
//	var $project_id = $("#project_id");
//	var projectId = $project_id.val();
//
//
//
////	Check if status has changed
//
//
//	var _URL = contextPathJSVar + "/services/proxl_xml_file_import/pendingCount";
//
//	var requestData = { project_id : projectId };
//
////	var request =
//	$.ajax({
//		type : "GET",
//		url : _URL,
//		data : requestData,
//
//		traditional: true,  //  Force traditional serialization of the data sent
//		//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
//		//   So searchIds array is passed as "searchIds=<value>" which is what Jersey expects
//
//		dataType : "json",
//		success : function(data) {
//
//			objectThis.populatePendingCountProcessResponse( { responseData : data, requestData : requestData } );
//
//			//  check again after the delay
//
////			setTimeout( function() {
////			objectThis.populatePendingCount();
////			}, objectThis.CONSTANTS.UPDATE_PENDING_COUNT_DELAY );
//
//		},
//		failure: function(errMsg) {
//
//			// silently let fail
//
////			handleAJAXFailure( errMsg );
//		},
//		error : function(jqXHR, textStatus, errorThrown) {
//
//			// silently let fail
//
////			handleAJAXError(jqXHR, textStatus, errorThrown);
//
//			// alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
//			// textStatus: " + textStatus );
//		}
//	});
//};
//
//ProxlXMLFileImportStatusDisplay.prototype.populatePendingCountProcessResponse  = function( params ) {
//
////	var requestData = params.requestData;
//	var responseData = params.responseData;
//
//	var pendingCount = responseData.pendingCount;
//
//	if ( pendingCount > 0 ) {
//
//
//	} else {
//
//
//	}
//
//	$("#upload_data_pending_number").text( pendingCount );
//	$("#upload_data_pending_block").show();
//
//};



///////////////

//  Instance of object on page

var proxlXMLFileImportStatusDisplay = new ProxlXMLFileImportStatusDisplay();
