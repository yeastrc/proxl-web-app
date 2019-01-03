
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
		
		//  Remove Auto Refresh
		
		//  Auto Refresh for certain status values

//		AUTO_REFRESH_DELAY : 60 * 1000  //  In Milliseconds  Check every 60 seconds
//		,
		
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

	try {

		$("#upload_data_expand_show_data").click(function(eventObject) {
			try {
				var clickThis = this;
				$( this ).hide();
				$("#upload_data_collapse_hide_data").show();
				$("#upload_data_main_collapsable_jq").show();

				$("#upload_data_top_level_container").data( objectThis.CONSTANTS.DATA_ATTR_FOR_UPLOAD_DATA_SHOWING, true );

				objectThis.showUploadDataClicked( clickThis, eventObject );

				return false;

			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});

		$("#upload_data_collapse_hide_data").click(function(eventObject) {
			try {
//				var clickThis = this;
				$( this ).hide();
				$("#upload_data_expand_show_data").show();
				$("#upload_data_main_collapsable_jq").hide();

				$("#upload_data_top_level_container").data( objectThis.CONSTANTS.DATA_ATTR_FOR_UPLOAD_DATA_SHOWING, false );

				return false;

			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});


		$("#upload_data_refresh_data").click(function(eventObject) {

			try {

				var clickThis = this;

				objectThis.refreshDataClicked( clickThis, eventObject );

				return false;

			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});



		$(".proxl_xml_file_upload_complete_successfully_overlay_cancel_parts_jq").click(function(eventObject) {
			try {
				var clickThis = this;

				objectThis.cancelClicked( clickThis, eventObject );

				return false;

			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});


		$("#proxl_xml_file_upload_complete_successfully_refresh_page_button").click(function(eventObject) {
			try {
				var clickThis = this;

				objectThis.refreshPageClicked( clickThis, eventObject );

				return false;

			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});

		this.statusIdQueued = $("#proxl_xml_file_upload_complete_successfully_status_id_queued").val();
		this.statusIdRequeued = $("#proxl_xml_file_upload_complete_successfully_status_id_re_queued").val();
		this.statusIdStarted = $("#proxl_xml_file_upload_complete_successfully_status_id_started").val();
		this.statusIdComplete = $("#proxl_xml_file_upload_complete_successfully_status_id_complete").val();
		this.statusIdFailed = $("#proxl_xml_file_upload_complete_successfully_status_id_failed").val();


		$("#upload_data_pending_items_show_link").click(function(eventObject) {
			try {
				$( this ).hide();
				$("#upload_data_pending_items_container").show();
				$("#upload_data_pending_items_hide_link").show();

				return false;

			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});

		$("#upload_data_pending_items_hide_link").click(function(eventObject) {
			try {
				$( this ).hide();
				$("#upload_data_pending_items_container").hide();
				$("#upload_data_pending_items_show_link").show();

				return false;

			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});


		$("#upload_data_history_items_show_link").click(function(eventObject) {
			try {
				$( this ).hide();
				$("#upload_data_history_items_container").show();
				$("#upload_data_history_items_hide_link").show();

				$("#upload_data_history_items_block").data( objectThis.CONSTANTS.DATA_ATTR_FOR_UPLOAD_DATA_SHOWING, true );

				return false;

			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});

		$("#upload_data_history_items_hide_link").click(function(eventObject) {
			try {
				$( this ).hide();
				$("#upload_data_history_items_container").hide();
				$("#upload_data_history_items_show_link").show();

				$("#upload_data_history_items_block").data( objectThis.CONSTANTS.DATA_ATTR_FOR_UPLOAD_DATA_SHOWING, false );

				return false;

			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});

		$("#upload_data_pending_items_show_all_details_link").click(function(eventObject) {
			try {
				objectThis.showAllItemDetails({ clickedThis : this } );
				eventObject.preventDefault();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		
		$("#upload_data_history_items_show_all_details_link").click(function(eventObject) {
			try {
				objectThis.showAllItemDetails({ clickedThis : this } );
				eventObject.preventDefault();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		

//		this.populatePendingCount();  do initial population in the Struts Action instead 


//		TODO  Turned off


		//  Do initial status change check to get initial status values

//		this.checkForStatusChange();

	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
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
	
	var _URL = contextPathJSVar + "/services/file_import_proxl_xml_scans/trackingDataList";

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

			try {

				objectThis.populateDataBlockAndPendingCountProcessResponse( { responseData : data, requestData : requestData } );
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
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
			throw Error( "handlebarsSource_proxl_xml_import_item_template === undefined" );
		}
		if ( handlebarsSource_proxl_xml_import_item_template === null ) {
			throw Error( "handlebarsSource_proxl_xml_import_item_template === null" );
		}
		this.handlebarsTemplate_proxl_xml_import_item_template = Handlebars.compile( handlebarsSource_proxl_xml_import_item_template );
	}

	if ( this.handlebarsTemplate_proxl_xml_import_item_separator_template === undefined ) {
		var handlebarsSource_proxl_xml_import_item_separator_template = $( "#proxl_xml_import_item_separator_template" ).html();
		if ( handlebarsSource_proxl_xml_import_item_separator_template === undefined ) {
			throw Error( "handlebarsSource_proxl_xml_import_item_separator_template === undefined" );
		}
		if ( handlebarsSource_proxl_xml_import_item_separator_template === null ) {
			throw Error( "handlebarsSource_proxl_xml_import_item_separator_template === null" );
		}
		this.handlebarsTemplate_proxl_xml_import_item_separator_template = Handlebars.compile( handlebarsSource_proxl_xml_import_item_separator_template );
	}

	if ( this.handlebarsTemplate_proxl_xml_import_item_row_details_template === undefined ) {
		var handlebarsSource_proxl_xml_import_item_row_details_template = $( "#proxl_xml_import_item_row_details_template" ).html();
		if ( handlebarsSource_proxl_xml_import_item_row_details_template === undefined ) {
			throw Error( "handlebarsSource_proxl_xml_import_item_row_details_template === undefined" );
		}
		if ( handlebarsSource_proxl_xml_import_item_row_details_template === null ) {
			throw Error( "handlebarsSource_proxl_xml_import_item_row_details_template === null" );
		}
		this.handlebarsTemplate_proxl_xml_import_item_row_details_template = Handlebars.compile( handlebarsSource_proxl_xml_import_item_row_details_template );
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
		
		$("#upload_data_pending_items_outer_container").hide();
		$("#upload_data_pending_items_no_pending_text").show();
		
	} else {
	
		//  Have Pending to display
		
		$("#upload_data_pending_items_block").show();

		$("#upload_data_pending_items_outer_container").show();
		$("#upload_data_pending_items_no_pending_text").hide();

		var $upload_data_pending_items_table = $("#upload_data_pending_items_table");


		var $filename_status_cell_jq_ALL = $upload_data_pending_items_table.find(".filename_status_cell_jq");

		$filename_status_cell_jq_ALL.qtip('destroy', true); // Immediately destroy all tooltips belonging to the selected elements

		$upload_data_pending_items_table.empty();
		
		//  Add data to the page

		for ( var dataIndex = 0; dataIndex < pendingItemsList.length ; dataIndex++ ) {

			var dataItem = pendingItemsList[ dataIndex ];

			this.populateDataBlockPendingOrHistoryItem( { dataItem : dataItem, $containerTable : $upload_data_pending_items_table } );
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
		
		//  Add data to the page

		for ( var dataIndex = 0; dataIndex < historyItemsList.length ; dataIndex++ ) {
			var dataItem = historyItemsList[ dataIndex ];
			this.populateDataBlockPendingOrHistoryItem( { dataItem : dataItem, $containerTable : $upload_data_history_items_table } );
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
	
	//  Remove auto-refresh since now that closes any detail items the user had showing
//	if ( uploadDataShowing &&
//			pendingItemsList && pendingItemsList.length > 0 ) {
//		
//		//  Auto Refresh after delay
//
//		this.autoRefreshTimerId = setTimeout( function() {
//			objectThis.autoRefreshTimerId = undefined;
//			objectThis.populateDataBlockAndPendingCount( { fromAutoRefresh : true } );
//		}, this.CONSTANTS.AUTO_REFRESH_DELAY );
//	}
};


ProxlXMLFileImportStatusDisplay.prototype.populateDataBlockPendingOrHistoryItem  = function( params ) {
	var objectThis = this;
	var dataItem = params.dataItem;
	var $containerTable = params.$containerTable;

	var context = dataItem;

	var html = this.handlebarsTemplate_proxl_xml_import_item_template( context );

	var $upload_data_item = $(html).appendTo( $containerTable );

	var numCols = $upload_data_item.children("td").length;

	addToolTips( $upload_data_item );

	//  Add details row

	context.numCols = numCols;
	context.numColsMinusTwo = numCols - 2;
	
	var detailsHtml = this.handlebarsTemplate_proxl_xml_import_item_row_details_template( context );

	var $upload_data_details_item = $(detailsHtml).appendTo( $containerTable );
	
	var $proxl_xml_import_item_expand_collapse_row_clickable_jq_All = $upload_data_item.find(".proxl_xml_import_item_expand_collapse_row_clickable_jq");

	$proxl_xml_import_item_expand_collapse_row_clickable_jq_All.click(function(eventObject) {
		try {
			objectThis.processClickStatusRow( { clickedThis : this } );
			
			eventObject.preventDefault();

		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});


	addToolTips( $upload_data_details_item );

	//  Add divider row
	
	var html = this.handlebarsTemplate_proxl_xml_import_item_separator_template( context );

	var $upload_divider_item = $(html).appendTo( $containerTable );

};


//////////

ProxlXMLFileImportStatusDisplay.prototype.showAllItemDetails  = function( params ) {
	var objectThis = this;
	var clickedThis = params.clickedThis;
	var $clickedThis = $( clickedThis );

	var container_id = $clickedThis.attr("data-container_id");
	
	var $container = $( "#" + container_id );
	
	var $proxl_xml_import_item_row_jq_All = $container.find(".proxl_xml_import_item_row_jq");
	$proxl_xml_import_item_row_jq_All.each( function() {
		var $row = $( this );
		objectThis.showHideStatusDetailsRow({ alwaysShowRow : true, $proxl_xml_import_item_row_jq : $row } );
	} );
};

//////////

ProxlXMLFileImportStatusDisplay.prototype.processClickStatusRow  = function( params ) {
	var clickedThis = params.clickedThis;

	var $clickedThis = $( clickedThis );
	var $proxl_xml_import_item_row_jq = $clickedThis.closest(".proxl_xml_import_item_row_jq");

	this.showHideStatusDetailsRow({ $proxl_xml_import_item_row_jq : $proxl_xml_import_item_row_jq } );
};


//////////

ProxlXMLFileImportStatusDisplay.prototype.showHideStatusDetailsRow  = function( params ) {
	var $proxl_xml_import_item_row_jq = params.$proxl_xml_import_item_row_jq;
	var alwaysShowRow = params.alwaysShowRow;
	
	var $proxl_xml_import_item_expand_row_icon_jq = $proxl_xml_import_item_row_jq.find(".proxl_xml_import_item_expand_row_icon_jq");
	var $proxl_xml_import_item_collapse_row_icon_jq = $proxl_xml_import_item_row_jq.find(".proxl_xml_import_item_collapse_row_icon_jq");
	
	var $proxl_xml_import_item_row_jq_NextRow = $proxl_xml_import_item_row_jq.next();
	
	if ( alwaysShowRow || $proxl_xml_import_item_expand_row_icon_jq.is(":visible") ) {
		
		$proxl_xml_import_item_row_jq_NextRow.show();
		$proxl_xml_import_item_expand_row_icon_jq.hide();
		$proxl_xml_import_item_collapse_row_icon_jq.show();
		
	} else {
		
		$proxl_xml_import_item_row_jq_NextRow.hide();
		$proxl_xml_import_item_collapse_row_icon_jq.hide();
		$proxl_xml_import_item_expand_row_icon_jq.show();
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



///////////////

//  Instance of object on page

var proxlXMLFileImportStatusDisplay = new ProxlXMLFileImportStatusDisplay();

window.proxlXMLFileImportStatusDisplay = proxlXMLFileImportStatusDisplay;

export { proxlXMLFileImportStatusDisplay }
