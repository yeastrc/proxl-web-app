
//  viewProject_SearchMaint.js


//  Javascript for the viewProject.jsp page for maintenance of a single search

//    Only loaded for logged in users

//  JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";


$(document).ready(function()  { 
	try {
		initPageSearchMaint();
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
});

/////////////
function initPageSearchMaint() {
	//  tool tips for files attached to searches
	$(".search_file_link_for_tooltip_jq").each(function() { // Grab search file links
		var $search_file_link_tooltip_jq = $(this).children(".search_file_link_tooltip_jq");
		if ( $search_file_link_tooltip_jq.length > 0 ) {
			var tipText = $search_file_link_tooltip_jq.text();
			$(this).qtip({ 
				content: {
					text: tipText
				}
			});
		}
	});
	//   Attach Delete Search click handlers
	$(".delete_search_link_jq").click(function(eventObject) {
		var clickThis = this;
		try {
			deleteSearchClickHandler( clickThis, eventObject );
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
		return false;
	});	 			
	$("#delete_search_confirm_button").click(function(eventObject) {
		var clickThis = this;
		try {
			deleteSearchConfirmed( clickThis, eventObject );
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
		return false;
	});
	$(".delete_search_overlay_show_hide_parts_jq").click(function(eventObject) {
		var clickThis = this;
		try {
			closeConfirmDeleteSearchOverlay( clickThis, eventObject );
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
		return false;
	});
	///////  Attach Delete Search Weblink Click handlers
	$(".delete_search_webLink_link_jq").click(function(eventObject) {
		var clickThis = this;
		try {
			deleteSearchWebLinkClickHandler( clickThis, eventObject );
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
		return false;
	});	 			
	$("#delete_search_web_link_confirm_button").click(function(eventObject) {
		var clickThis = this;
		try {
			deleteSearchWebLinkConfirmed( clickThis, eventObject );
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
		return false;
	});
	$(".delete_search_web_link_overlay_show_hide_parts_jq").click(function(eventObject) {
		var clickThis = this;
		try {
			closeConfirmDeleteSearchWebLinkOverlay( clickThis, eventObject );
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
		return false;
	});
	///////  Attach Delete Search Comment Click handlers
	$("#delete_search_comment_confirm_button").click(function(eventObject) {
		var clickThis = this;
		try {
			deleteSearchCommentConfirmed( clickThis, eventObject );
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
		return false;
	});
	$(".delete_search_comment_overlay_show_hide_parts_jq").click(function(eventObject) {
		var clickThis = this;
		try {
			closeConfirmDeleteSearchCommentOverlay( clickThis, eventObject );
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
		return false;
	});
	//  Initialize the buttons from the current values of the check boxes.
	//     The check boxes may be checked from using the back button.
	updateButtonsBasedOnCheckedSearches ( );
}


///////////////
function validateURL(textval) {
	var urlregex = new RegExp(
	"^(http|https|ftp)\://([a-zA-Z0-9\.\-]+(\:[a-zA-Z0-9\.&amp;%\$\-]+)*@)*((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|([a-zA-Z0-9\-]+\.)*[a-zA-Z0-9\-]+\.(com|edu|gov|int|mil|net|org|biz|arpa|info|name|pro|aero|coop|museum|[a-zA-Z]{2}))(\:[0-9]+)*(/($|[a-zA-Z0-9\.\,\?\'\\\+&amp;%\$#\=~_\-]+))*$");
	return urlregex.test(textval);
}


window.addWebLink = function( searchId ) {
	var _URL = "services/searchWebLinks/add";
	var $linkUrlInputField = $( "input#web-links-url-input-" + searchId );
	var linkUrl = $linkUrlInputField.val();
	if( linkUrl === undefined || linkUrl === "" ) { return; }
	var linkLabel = $( "input#web-links-label-input-" + searchId ).val();
	if( linkLabel === undefined || linkLabel === "" ) { 
		linkLabel = linkUrl;
	}
	if ( validateURL( linkUrl )  ) {
	} else {
//		alert("url not valid");
		var $element = $("#error_message_web_link_url_invalid_" + searchId );
//		var linkUrlInputFieldTop = $linkUrlInputField.offset().top;

//		$element.css("top", linkUrlInputFieldTop );
		showErrorMsg( $element );
		return;  //  !!!  EARLY EXIT
	}
//	var request = 
	$.ajax({
		type: "POST",
		url: _URL,
		data: { 'searchId' : searchId, 'linkUrl' : linkUrl, linkLabel: linkLabel },
		dataType: "json",
		success: function(data)	{
			try {
				// add new web link to DOM
				var id = data.id;
				var source = $("#web_link_template").html();
				if ( source === undefined ) {
					throw Error( '$("#web_link_template").html() === undefined' );
				}
				if ( source === null ) {
					throw Error( '$("#web_link_template").html() === null' );
				}
				var template = Handlebars.compile(source);
				var context = data;
				var html = template(context);
				var web_link_root_container_div_jq = $( html ).insertBefore( "#add-web-links-link-span-" + searchId );
				addToolTips( web_link_root_container_div_jq );
//				attachProjectNoteMaintOnClick( web_link_root_container_div_jq );
//				$("#add_note_field").val("");
				$("#web-links-delete-" + id).click(function(eventObject) {
					var clickThis = this;
					try {
						deleteSearchWebLinkClickHandler( clickThis, eventObject );
					} catch( e ) {
						reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
						throw e;
					}
					return false;
				});	 	
				$( "div#web-links-" + id ).show( 200 );
				$( "#add-web-links-form-span-" + searchId ).hide();
				$( "#add-web-links-link-span-" + searchId ).show();
				$( "input#web-links-input-" + searchId ).val( "" );
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
//			alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ", textStatus: " + textStatus );
		}
	});
}


//Called by "onclick" on HTML element
window.cancelWebLink = function( id ) {
	try {
		$( "#add-web-links-form-span-" + id ).hide();
		$( "#add-web-links-link-span-" + id ).show();
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
}

//Called by "onclick" on HTML element
window.showAddWebLink = function( id ) {
	try {
		$( "input#web-links-url-input-" + id ).val("");
		$( "input#web-links-label-input-" + id ).val( "" );
		$( "#add-web-links-form-span-" + id ).show();
		$( "#add-web-links-link-span-" + id ).hide();
		$( "#web-links-url-input-" + id ).focus();
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
}


//Files for Search Maint

//Called by "onclick" on HTML element
window.saveSearchFilename = function( clickThis ) {
	try {
		var _URL = "services/search_file/updateDisplayFilename";
		var $clickThis = $( clickThis );
		var $display_search_filename_outer_container_jq = $clickThis.closest(".display_search_filename_outer_container_jq");
		var search_file_id = $display_search_filename_outer_container_jq.attr("search_file_id");
//		var search_id = $display_search_filename_outer_container_jq.attr("search_id");
		var $edit_search_filename_input_field_jq = $display_search_filename_outer_container_jq.find(".edit_search_filename_input_field_jq");
		var edit_search_filename_input_value = $edit_search_filename_input_field_jq.val(); 
		if ( edit_search_filename_input_value === "" ) {
			return;
		}
		var requestData = { 
				'searchFileId' : search_file_id, 
				displayFilename: edit_search_filename_input_value };
//		var request = 
		$.ajax({
			type: "POST",
			url: _URL,
			data: requestData,
			dataType: "json",
			success: function(data)	{
				try {
					var $search_filename_jq = $display_search_filename_outer_container_jq.find(".search_filename_jq");
					$search_filename_jq.text( edit_search_filename_input_value );
					var $display_search_filename_container_jq = $display_search_filename_outer_container_jq.find(".display_search_filename_container_jq");
					var $edit_search_filename_container_jq = $display_search_filename_outer_container_jq.find(".edit_search_filename_container_jq");
					$edit_search_filename_container_jq.hide();
					$display_search_filename_container_jq.show();
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
//				alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ", textStatus: " + textStatus );
			}
		});
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
}


//Called by "onclick" on HTML element
window.cancelSearchFilenameEdit = function( clickThis ) {
	try {
		var $clickThis = $( clickThis );
		var $display_search_filename_outer_container_jq = $clickThis.closest(".display_search_filename_outer_container_jq");
		var $display_search_filename_container_jq = $display_search_filename_outer_container_jq.find(".display_search_filename_container_jq");
		var $edit_search_filename_container_jq = $display_search_filename_outer_container_jq.find(".edit_search_filename_container_jq");
		$edit_search_filename_container_jq.hide();
		$display_search_filename_container_jq.show();
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
}


//Called by "onclick" on HTML element
window.showSearchFilenameForm = function( clickThis ) {
	try {
		var $clickThis = $( clickThis );
		var $display_search_filename_outer_container_jq = $clickThis.closest(".display_search_filename_outer_container_jq");
		var $display_search_filename_container_jq = $display_search_filename_outer_container_jq.find(".display_search_filename_container_jq");
		var $edit_search_filename_container_jq = $display_search_filename_outer_container_jq.find(".edit_search_filename_container_jq");
		$edit_search_filename_container_jq.show();
		$display_search_filename_container_jq.hide();
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
}

///////////////////////////////
////////////Search Comment
///////////////
//Called by "onclick" on HTML element
window.showSearchCommentEditForm = function( clickThis ) {
	try {
		var $clickThis = $( clickThis );
		var $search_comment_root_jq = $clickThis.closest(".search_comment_root_jq");
		var $search_comment_display_jq = $search_comment_root_jq.find(".search_comment_display_jq");
		var $search_comment_edit_jq = $search_comment_root_jq.find(".search_comment_edit_jq");
		var $search_comment_string_jq = $search_comment_root_jq.find(".search_comment_string_jq");
		var $search_comment_input_field_jq = $search_comment_root_jq.find(".search_comment_input_field_jq");
		var search_comment_value = $search_comment_string_jq.text();
		$search_comment_input_field_jq.val( search_comment_value );
		$search_comment_edit_jq.show();
		$search_comment_display_jq.hide();
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
}

///////////////
//Called by "onclick" on HTML element
window.cancelSearchCommentEditForm = function( clickThis ) {
	try {
		var $clickThis = $( clickThis );
		var $search_comment_root_jq = $clickThis.closest(".search_comment_root_jq");
		var $search_comment_display_jq = $search_comment_root_jq.find(".search_comment_display_jq");
		var $search_comment_edit_jq = $search_comment_root_jq.find(".search_comment_edit_jq");
		$search_comment_edit_jq.hide();
		$search_comment_display_jq.show();
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
}			

///////////////

//Called by "onclick" on HTML element
window.updateSearchComment = function( clickThis ) {
	try {
		var _URL = "services/searchComment/updateText";
		var $clickThis = $( clickThis );
		var $search_comment_root_jq = $clickThis.closest(".search_comment_root_jq");
		var searchCommentId = $search_comment_root_jq.attr("searchCommentId");	
		var $search_comment_input_field_jq = $search_comment_root_jq.find(".search_comment_input_field_jq");
		var search_comment_value = $search_comment_input_field_jq.val();
//		var request = 
		$.ajax({
			type: "POST",
			url: _URL,
			data: { 'id' : searchCommentId, 'comment' : search_comment_value },
			dataType: "json",
			success: function(data)	{
				try {
					// update comment 
					var $search_comment_string_jq = $search_comment_root_jq.find(".search_comment_string_jq");
					var $search_comment_date_jq = $search_comment_root_jq.find(".search_comment_date_jq");
					$search_comment_string_jq.text( data.comment );
					$search_comment_date_jq.text( data.dateTimeString );
					var $search_comment_display_jq = $search_comment_root_jq.find(".search_comment_display_jq");
					var $search_comment_edit_jq = $search_comment_root_jq.find(".search_comment_edit_jq");
					$search_comment_edit_jq.hide();
					$search_comment_display_jq.show();
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
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
}


//Called by "onclick" on HTML element
window.addComment = function( searchId ) {
	try {
		var _URL = "services/searchComment/add";
		var comment = $( "input#comment-input-" + searchId ).val();
		if( comment == undefined || comment == "" ) { return; }
//		var request = 
		$.ajax({
			type: "POST",
			url: _URL,
			data: { 'searchId' : searchId, 'comment' : comment },
			dataType: "json",
			success: function(data)	{
				try {
					// add new comment to DOM
					var id = data[ 'id' ];
					var source = $("#search_comment_template").html();
					if ( source === undefined ) {
						throw Error( '$("#search_comment_template").html() === undefined' );
					}
					if ( source === null ) {
						throw Error( '$("#search_comment_template").html() === null' );
					}
					var template = Handlebars.compile(source);
					var context = data;
					var html = template(context);
					var $inserted = $( html ).insertBefore( "span#add-comment-link-span-" + searchId );
					addToolTips( $inserted );
					$( "div#comment-" + id ).show( 200 );
					$( "span#add-comment-form-span-" + searchId ).hide();
					$( "span#add-comment-link-span-" + searchId ).show();
					$( "input#comment-input-" + searchId ).val( "" );
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
//				alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ", textStatus: " + textStatus );
			}
		});
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
}


//Called by "onclick" on HTML element
window.cancelComment = function( id ) {
	try {
		$( "span#add-comment-form-span-" + id ).hide();
		$( "span#add-comment-link-span-" + id ).show();
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
}

//Called by "onclick" on HTML element
window.showAddComment = function( id ) {
	try {
		$( "span#add-comment-form-span-" + id ).show();
		$( "span#add-comment-link-span-" + id ).hide();
		$( "#comment-input-" + id ).focus();
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
}

//Delete Search processing
/////////////////
window.deleteSearchClickHandler = function(clickThis, eventObject) {
	openConfirmDeleteSearchOverlay(clickThis, eventObject);
	return;
};

///////////
var openConfirmDeleteSearchOverlay = function(clickThis, eventObject) {
	var $clickThis = $(clickThis);
//	get root div for this search
	var $search_root_jq = $clickThis.closest(".search_root_jq");
	var projectSearchId = $search_root_jq.attr("data-project_search_id");	
	if ( projectSearchId === undefined ) {
		throw Error( "Error: attribute 'data-project_search_id' not found on element with class 'search_root_jq'" );
	}
//	copy the search name to the overlay
	var $search_name_display_jq = $search_root_jq.find(".search_name_display_jq");
	var search_name_display_jq = $search_name_display_jq.text();
	var $delete_search_overlay_search_name = $("#delete_search_overlay_search_name");
	$delete_search_overlay_search_name.text( search_name_display_jq );
	var $delete_search_confirm_button = $("#delete_search_confirm_button");
	$delete_search_confirm_button.data("projectSearchId", projectSearchId);
//	Position dialog over clicked delete icon
//	get position of div containing the dialog that is inline in the page
	var $delete_search_overlay_containing_outermost_div_inline_div = $("#delete_search_overlay_containing_outermost_div_inline_div");
	var offset__containing_outermost_div_inline_div = $delete_search_overlay_containing_outermost_div_inline_div.offset();
	var offsetTop__containing_outermost_div_inline_div = offset__containing_outermost_div_inline_div.top;
	var offset__ClickedDeleteIcon = $clickThis.offset();
	var offsetTop__ClickedDeleteIcon = offset__ClickedDeleteIcon.top;
	var offsetDifference = offsetTop__ClickedDeleteIcon - offsetTop__containing_outermost_div_inline_div;
//	adjust vertical position of dialog 
	var $delete_search_overlay_container = $("#delete_search_overlay_container");
	var height__delete_search_overlay_container = $delete_search_overlay_container.outerHeight( true /* [includeMargin ] */ );
	var positionAdjust = offsetDifference - ( height__delete_search_overlay_container / 2 );
	$delete_search_overlay_container.css( "top", positionAdjust );
	var $delete_search_overlay_background = $("#delete_search_overlay_background"); 
	$delete_search_overlay_background.show();
	$delete_search_overlay_container.show();
};

///////////
window.closeConfirmDeleteSearchOverlay = function(clickThis, eventObject) {
	var $delete_search_confirm_button = $("#delete_search_confirm_button");
	$delete_search_confirm_button.data("projectSearchId", null);
	$(".delete_search_overlay_show_hide_parts_jq").hide();
};

/////////////////
//  user confirms search delete
window.deleteSearchConfirmed = function(clickThis, eventObject) {
	var $clickThis = $(clickThis);
	var projectSearchId = $clickThis.data("projectSearchId");
	if ( projectSearchId === undefined || projectSearchId === null ) {
		throw Error( " projectSearchId === undefined || projectSearchId === null " );
	}
	if ( projectSearchId === "" ) {
		throw Error( ' projectSearchId === "" ' );
	}
	var requestData = {	projectSearchId : projectSearchId };
	var _URL = "services/search/delete";
//	var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(responseData) {
			try {
				deleteSearchConfirmedResponse( { responseData : responseData, projectSearchId: projectSearchId } );
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		},
		failure: function(errMsg) {
			handleAJAXFailure( errMsg );
			closeConfirmDeleteSearchOverlay();
		},
		error : function(jqXHR, textStatus, errorThrown) {
			handleAJAXError(jqXHR, textStatus, errorThrown);
			closeConfirmDeleteSearchOverlay();
//			alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
//			textStatus: " + textStatus );
		}
	});
};
///
var deleteSearchConfirmedResponse = function( params ) {
	var projectSearchId = params.projectSearchId;
	// assume successful if get here
	//  Remove search from page
	var single_search_entry__project_search_id__HTML_ElementID = "single_search_entry__project_search_id_" + projectSearchId;
	var $single_search_entry__project_search_id_ID = $( "#" + single_search_entry__project_search_id__HTML_ElementID );
	if ( $single_search_entry__project_search_id_ID.length === 0 ) {
		console.log("No HTML element found for id: " + single_search_entry__project_search_id__HTML_ElementID );
		//  Maybe reload page here
	} else {
		$single_search_entry__project_search_id_ID.remove();
	}
	closeConfirmDeleteSearchOverlay();
};

//  END   Delete Search processing


//  Delete Search Comment processing

/////////////////
window.deleteSearchCommentClickHandler = function(clickThis) {
	openConfirmDeleteSearchCommentOverlay(clickThis);
	return;
};

///////////
var openConfirmDeleteSearchCommentOverlay = function(clickThis) {
	var $clickThis = $(clickThis);
//	get root div for this search
	var $search_root_jq = $clickThis.closest(".search_comment_root_jq");
	var searchCommentId = $search_root_jq.attr("searchCommentId");	
	var $delete_search_comment_confirm_button = $("#delete_search_comment_confirm_button");
	$delete_search_comment_confirm_button.data("searchCommentId", searchCommentId);
//	Position dialog over clicked delete icon
//	get position of div containing the dialog that is inline in the page
	var $delete_search_comment_overlay_containing_outermost_div_inline_div = $("#delete_search_comment_overlay_containing_outermost_div_inline_div");
	var offset__containing_outermost_div_inline_div = $delete_search_comment_overlay_containing_outermost_div_inline_div.offset();
	var offsetTop__containing_outermost_div_inline_div = offset__containing_outermost_div_inline_div.top;
	var offset__ClickedDeleteIcon = $clickThis.offset();
	var offsetTop__ClickedDeleteIcon = offset__ClickedDeleteIcon.top;
	var offsetDifference = offsetTop__ClickedDeleteIcon - offsetTop__containing_outermost_div_inline_div;
//	adjust vertical position of dialog 
	var $delete_search_comment_overlay_container = $("#delete_search_comment_overlay_container");
	var height__delete_search_comment_overlay_container = $delete_search_comment_overlay_container.outerHeight( true /* [includeMargin ] */ );
	var positionAdjust = offsetDifference - ( height__delete_search_comment_overlay_container / 2 );
	$delete_search_comment_overlay_container.css( "top", positionAdjust );
	var $delete_search_comment_overlay_background = $("#delete_search_comment_overlay_background"); 
	$delete_search_comment_overlay_background.show();
	$delete_search_comment_overlay_container.show();
};

///////////
window.closeConfirmDeleteSearchCommentOverlay = function(clickThis, eventObject) {
	var $delete_search_comment_confirm_button = $("#delete_search_comment_confirm_button");
	$delete_search_comment_confirm_button.data("searchCommentId", null);
	$(".delete_search_comment_overlay_show_hide_parts_jq").hide();
};

/////////////////
//put click handler for this on #delete_search_comment_confirm_button
window.deleteSearchCommentConfirmed = function(clickThis, eventObject) {
	var $clickThis = $(clickThis);
	var searchCommentId = $clickThis.data("searchCommentId");
	if ( searchCommentId === undefined || searchCommentId === null ) {
		throw Error( " searchCommentId === undefined || searchCommentId === null " );
	}
	if ( searchCommentId === "" ) {
		throw Error( ' searchCommentId === "" ' );
	}
	var _URL = "services/searchComment/delete";
//	var request = 
	$.ajax({
		type: "POST",
		url: _URL,
		data: { 'id' : searchCommentId },
		dataType: "json",
		success: function(data)	{
			try {
				$( "div#comment-" + searchCommentId ).hide(200, function() { $( "div#comment-" + searchCommentId ).remove(); });
				closeConfirmDeleteSearchCommentOverlay();
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
//			alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ", textStatus: " + textStatus );
		}
	});
};

//END   Delete Search Comment processing


//Delete Search Web Link processing

/////////////////
window.deleteSearchWebLinkClickHandler = function(clickThis, eventObject) {
	openConfirmDeleteSearchWebLinkOverlay(clickThis, eventObject);
	return;
};

///////////
var openConfirmDeleteSearchWebLinkOverlay = function(clickThis, eventObject) {
	var $clickThis = $(clickThis);
//	get root div for this search
	var $search_root_jq = $clickThis.closest(".search_web_link_root_jq");
	var searchwebLinkId = $search_root_jq.attr("searchwebLinkId");	
	var $delete_search_web_link_confirm_button = $("#delete_search_web_link_confirm_button");
	$delete_search_web_link_confirm_button.data("searchwebLinkId", searchwebLinkId);
//	Position dialog over clicked delete icon
//	get position of div containing the dialog that is inline in the page
	var $delete_search_web_link_overlay_containing_outermost_div_inline_div = $("#delete_search_web_link_overlay_containing_outermost_div_inline_div");
	var offset__containing_outermost_div_inline_div = $delete_search_web_link_overlay_containing_outermost_div_inline_div.offset();
	var offsetTop__containing_outermost_div_inline_div = offset__containing_outermost_div_inline_div.top;
	var offset__ClickedDeleteIcon = $clickThis.offset();
	var offsetTop__ClickedDeleteIcon = offset__ClickedDeleteIcon.top;
	var offsetDifference = offsetTop__ClickedDeleteIcon - offsetTop__containing_outermost_div_inline_div;
//	adjust vertical position of dialog 
	var $delete_search_web_link_overlay_container = $("#delete_search_web_link_overlay_container");
	var height__delete_search_web_link_overlay_container = $delete_search_web_link_overlay_container.outerHeight( true /* [includeMargin ] */ );
	var positionAdjust = offsetDifference - ( height__delete_search_web_link_overlay_container / 2 );
	$delete_search_web_link_overlay_container.css( "top", positionAdjust );
	var $delete_search_web_link_overlay_background = $("#delete_search_web_link_overlay_background"); 
	$delete_search_web_link_overlay_background.show();
	$delete_search_web_link_overlay_container.show();
};

///////////
window.closeConfirmDeleteSearchWebLinkOverlay = function(clickThis, eventObject) {
	var $delete_search_web_link_confirm_button = $("#delete_search_web_link_confirm_button");
	$delete_search_web_link_confirm_button.data("searchwebLinkId", null);
	$(".delete_search_web_link_overlay_show_hide_parts_jq").hide();
};

/////////////////
//put click handler for this on #delete_search_web_link_confirm_button
window.deleteSearchWebLinkConfirmed = function(clickThis, eventObject) {
	var $clickThis = $(clickThis);
	var searchwebLinkId = $clickThis.data("searchwebLinkId");
	if ( searchwebLinkId === undefined || searchwebLinkId === null ) {
		throw Error( " searchwebLinkId === undefined || searchwebLinkId === null " );
	}
	if ( searchwebLinkId === "" ) {
		throw Error( ' searchwebLinkId === "" ' );
	}
	var _URL = "services/searchWebLinks/delete";
//	var request = 
	$.ajax({
		type: "POST",
		url: _URL,
		data: { 'id' : searchwebLinkId },
		dataType: "json",
		success: function(data)	{
			try {
				$( "div#web-links-" + searchwebLinkId ).hide(200, function() { $( "div#web-links-" + searchwebLinkId ).remove(); });
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
//			alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ", textStatus: " + textStatus );
		}
	});
	closeConfirmDeleteSearchWebLinkOverlay();
};

//END   Delete Search Web Link processing



//Called by "onclick" on HTML element
window.showSearchNameForm = function( id ) {
	try {
		$( "span#search-name-normal-" + id ).hide();
		$( "span#search-name-edit-" + id ).show();	
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
}

window.cancelNameEdit = function( id ) {
	try {
		$( "span#search-name-edit-" + id ).hide();
		$( "span#search-name-normal-" + id ).show();
		$( "input#search-name-value-" + id ).val( $( "span#search-name-display-" + id ).html() );
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
}


//Called by "onclick" on HTML element
window.saveName = function( projectSearchId ) {
	try {
		var _URL = "services/searchName/save";
		var name = $( "input#search-name-value-" + projectSearchId ).val();
		if( name == undefined || name == "" ) { return; }
//		var request = 
		$.ajax({
			type: "POST",
			url: _URL,
			data: { 'projectSearchId' : projectSearchId, 'name' : name },
			dataType: "json",
			success: function(data)	{
				try {
					$( "span#search-name-display-" + projectSearchId ).html( name );
					$( "span#search-name-edit-" + projectSearchId ).hide();
					$( "span#search-name-normal-" + projectSearchId ).show();
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
//				alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ", textStatus: " + textStatus );
			}
		});
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
}

////////////////

var viewProject_SearchMaint = null;

export { viewProject_SearchMaint }
