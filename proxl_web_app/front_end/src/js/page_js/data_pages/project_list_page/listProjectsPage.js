
//   listProjectsPage.js


//  Javascript for the listProjectsPage.jsp page

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";

//  require full Handlebars since compiling templates
const Handlebars = require('handlebars');

import { header_mainVariable } from 'page_js/header_section_js_all_pages_main_pages/header_section_main_pages/header_main.js';


//var PAGE_CONSTANTS = {
//		
//		ERROR_MESSAGE_VERTICAL_MOVEMENT : 50 // number of pixels for moving error message when showing it. 
//		
//};


/////////////////

var getProjectList = function() {

	var _URL = "services/project/listForCurrentUser";
	
	var requestData = {};

	// var request =
	$.ajax({
		type : "GET",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(data) {

			try {

				getProjectListResponse(requestData, data);
				
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
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

};

var getProjectListResponse = function(requestData, responseData) {
	
	var projectList = responseData.projectList;

	var $project_list = $("#project_list");
	
	var $project_root_container_jq_List = $project_list.find(".project_root_container_jq");
	
	$project_root_container_jq_List.remove();
	
	var $project_separator_row_jq_List = $project_list.find(".project_separator_row_jq");
	
	$project_separator_row_jq_List.remove();
	

	if (projectList && projectList.length > 0) {
		
		var source = $("#project_template tbody").html();

		if ( source === undefined ) {
			throw Error( ' $("#project_template tbody").html() === undefined' );
		}
		if ( source === null ) {
			throw Error( ' $("#project_template tbody").html() === null' );
		}
		
		var template = Handlebars.compile(source);
		
		
		for (var index = 0; index < projectList.length; index++) {

			var responseDataItem = projectList[index];

			var context = responseDataItem;

			var html = template(context);

			var $project_entry = $(html).appendTo($project_list);
			
//			$project_entry.data("project_context", context);


			if ( responseDataItem.canDelete ) {

				var $delete_project_link_jq = $project_entry.find(".delete_project_link_jq");

				if ($delete_project_link_jq.length === 0) {

					throw Error( "Unable to find '.delete_project_link_jq'" );
				}

				$delete_project_link_jq.click(function(eventObject) {
	
					try {
						var clickThis = this;

						markProjectForDeletion( clickThis, eventObject );
					} catch( e ) {
						reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
						throw e;
					}
				});
			}

		}
		
		addToolTips();

	} else {

//		var noDataMsg = $("#project_entry_no_data_template_div").html();
//
//		$project_list.html(noDataMsg);
	}
};



/////////////////////////

var markProjectForDeletion = function(clickThis, eventObject) {
	
	openConfirmMarkProjectForDeletionOverlay( clickThis, eventObject );
};


///////////

var openConfirmMarkProjectForDeletionOverlay = function(clickThis, eventObject) {

	var $clickThis = $(clickThis);
	
	//  the project_id is on the delete link
	var project_id = $clickThis.attr("project_id");

//	get root div for this project
	var $project_root_container_jq = $clickThis.closest(".project_root_container_jq");

//	copy the project title to the overlay

	var $project_title_jq = $project_root_container_jq.find(".project_title_jq");

	var project_title = $project_title_jq.text();

	var $mark_project_for_deletion_overlay_project_title = $("#mark_project_for_deletion_overlay_project_title");
	$mark_project_for_deletion_overlay_project_title.text( project_title );



	var $mark_project_for_deletion_confirm_button = $("#mark_project_for_deletion_confirm_button");
	$mark_project_for_deletion_confirm_button.data("project_id", project_id);

//	Position dialog over clicked delete icon

//	get position of div containing the dialog that is inline in the page
	var $mark_project_for_deletion_overlay_containing_outermost_div_inline_div = $("#mark_project_for_deletion_overlay_containing_outermost_div_inline_div");

	var offset__containing_outermost_div_inline_div = $mark_project_for_deletion_overlay_containing_outermost_div_inline_div.offset();
	var offsetTop__containing_outermost_div_inline_div = offset__containing_outermost_div_inline_div.top;

	var offset__ClickedDeleteIcon = $clickThis.offset();
	var offsetTop__ClickedDeleteIcon = offset__ClickedDeleteIcon.top;

	var offsetDifference = offsetTop__ClickedDeleteIcon - offsetTop__containing_outermost_div_inline_div;

//	adjust vertical position of dialog 

	var $mark_project_for_deletion_overlay_container = $("#mark_project_for_deletion_overlay_container");

	var height__mark_project_for_deletion_overlay_container = $mark_project_for_deletion_overlay_container.outerHeight( true /* [includeMargin ] */ );

	var positionAdjust = offsetDifference - ( height__mark_project_for_deletion_overlay_container / 2 );

	$mark_project_for_deletion_overlay_container.css( "top", positionAdjust );


	var $mark_project_for_deletion_overlay_background = $("#mark_project_for_deletion_overlay_background"); 
	$mark_project_for_deletion_overlay_background.show();
	$mark_project_for_deletion_overlay_container.show();
};

///////////

var closeConfirmMarkProjectForDeletionOverlay = function(clickThis, eventObject) {

	var $mark_project_for_deletion_confirm_button = $("#mark_project_for_deletion_confirm_button");
	$mark_project_for_deletion_confirm_button.data("project_id", null);

	$(".mark_project_for_deletion_overlay_show_hide_parts_jq").hide();
};


/////////////////

//   put click handler for this on #mark_project_for_deletion_confirm_button

var markProjectForDeletionConfirmed = function(clickThis, eventObject) {


	var $clickThis = $(clickThis);

	var project_id = $clickThis.data("project_id");

	if ( project_id === undefined || project_id === null ) {

		throw Error( " project_id === undefined || project_id === null " );
	}

	if ( project_id === "" ) {

		throw Error( ' project_id === "" ' );
	}

	
	

	if ( project_id === undefined || project_id === null || project_id === "" ) {

		alert("System error");
		
		var $element = $("#error_message_system_error");

		showErrorMsg( $element );
		
		throw Error( 'Page Error:  project_id === undefined || project_id === null || project_id === ""' );

		return;  //  !!!  EARLY EXIT
	}
	
	
	var requestData = {
			projectId : project_id
	};
	

	var _URL = "services/project/markForDeletion";

//	var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(data) {

			try {
				
				markProjectForDeletionComplete(requestData, data);
				
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		},
        failure: function(errMsg) {
        	handleAJAXFailure( errMsg );
        },
		error : function(jqXHR, textStatus, errorThrown) {


//			var $element = $("#error_message_system_error");
//
//			showErrorMsg( $element );

			handleAJAXError(jqXHR, textStatus, errorThrown);

//			alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
//			textStatus: " + textStatus );
		}
	});
};


////////

var markProjectForDeletionComplete = function(requestData, responseData) {

	if ( ! responseData.status ) {

		alert("System Error");

		var $element = $("#error_message_system_error");

		showErrorMsg( $element );

		return;
	} 

	closeConfirmMarkProjectForDeletionOverlay();

	getProjectList();


};


////////////////////////////

var addProject = function(clickThis, eventObject) {

	
	
	var $new_project_title = $("#new_project_title");

	if ($new_project_title.length === 0) {

		throw Error( "Unable to find input field for id 'new_project_title' " );
	}

	var $new_project_abstract = $("#new_project_abstract");

	if ($new_project_abstract.length === 0) {

		throw Error( "Unable to find input field for id 'new_project_abstract' " );
	}



	var new_project_title = $new_project_title.val();

	var new_project_abstract = $new_project_abstract.val();
	

	if ( new_project_title === "" ) {
		
//		alert("Title required");
		
		var $element = $("#error_message_project_title_required");
		
		showErrorMsg( $element );
		
		return;  //  !!!  EARLY EXIT

//	} else if ( new_project_abstract === "" ) {
//
////		alert("Abstract required");
//
//		var $element = $("#error_message_project_abstract_required");
//		
//		showErrorMsg( $element );
//			
//		return;  //  !!!  EARLY EXIT
	}

	var requestData = {
			projectTitle : new_project_title,
			projectAbstract : new_project_abstract
	};

	var _URL = "services/project/create";

	// var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(data) {

			try {
				addProjectComplete(requestData, data);
				
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		},
        failure: function(errMsg) {
        	handleAJAXFailure( errMsg );
        },
		error : function(jqXHR, textStatus, errorThrown) {

			
//			var $element = $("#error_message_system_error");
//			
//			showErrorMsg( $element );
			
			handleAJAXError(jqXHR, textStatus, errorThrown);

			// alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
			// textStatus: " + textStatus );
		}
	});
};

//////////

var addProjectComplete = function(requestData, responseData) {

	if ( ! responseData.status ) {
		
		alert("System Error");
			
		var $element = $("#error_message_system_error");
			
		showErrorMsg( $element );
		
		return;
	} 
	
	closeAndClearAddProject();
	
	getProjectList();
	

};


///////////

function closeAndClearAddProject() {
	
	

	$("#new_project_expanded").hide();
	$("#new_project_collapsed").show(); 
	
	$("#new_project_expand_link" ).show();
	$("#new_project_cancel_link" ).hide();

	
	$("#new_project_title").val("");
	$("#new_project_abstract").val("");
	
	
};

//		
//		/////////////////
//		
//		function showErrorMsg( $element ) {
//			
//			$element.css( { top: PAGE_CONSTANTS.ERROR_MESSAGE_VERTICAL_MOVEMENT } );
//			
//			$element.show();
//			
//			$element.animate( { top: 0 }, { duration: 500 } );
//		
//		};
//		




//////////////////

function initPage() {
	
	
	getProjectList();
	
	
	var $new_project_expand_jq  = $(".new_project_expand_jq");

	$new_project_expand_jq.click( function(eventObject) {
		try {
			$("#new_project_expanded").show();
			$("#new_project_collapsed").hide(); 
			$("#new_project_expand_link" ).hide();
			$("#new_project_cancel_link" ).show();

			$("#new_project_title").focus();
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});
	
	var $new_project_cancel_jq  = $(".new_project_cancel_jq");

	$new_project_cancel_jq.click( function(eventObject) {
		try {
			closeAndClearAddProject();
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});
	
	var $add_project_button = $("#add_project_button");
	
	$add_project_button.click( function(eventObject) {

		try {
			var clickThis = this;

			addProject( clickThis, eventObject );
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});


	$("#mark_project_for_deletion_confirm_button").click(function(eventObject) {
		try {
			var clickThis = this;

			markProjectForDeletionConfirmed( clickThis, eventObject );

			return false;
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});


	$(".mark_project_for_deletion_overlay_cancel_parts_jq").click(function(eventObject) {

		try {
			var clickThis = this;

			closeConfirmMarkProjectForDeletionOverlay( clickThis, eventObject );

			return false;
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});


};
	
	
///////////////

$(document).ready(function() {

	try {
		initPage();
		
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}

});
