
//   manageUsersPage.js

//  Javascript for the project admin section of the page manageUsersPage.jsp

//////////////////////////////////
// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";

// /////////////////////////////////////////
///////////////////////////////////////////


import { webserviceCallStandardPost } from 'page_js/webservice_call_common/webserviceCallStandardPost.js';


var adminGlobals = {
	logged_in_user_id : null
};

/////////////////
window.updateInvitedPeopleCurrentUsersLists = function() {
	try {
		getInvitedPeople();
//		getCurrentUserAccess();
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
};
/////////////////
window.getInvitedPeople = function() {
	var requestData = {
	};
	var _URL = "services/user/listInvitedPeople";
//	var request =
	$.ajax({
		type : "GET",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(data) {
			try {
				getInvitedPeopleResponse(requestData, data);
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
//			alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
//			textStatus: " + textStatus );
		}
	});
};
///////
window.getInvitedPeopleResponse = function(requestData, responseData) {
	var $invited_people = $("#invited_people_current_users");
	$invited_people.empty();
	if (responseData && responseData.length > 0) {
		var access_level_id_administrator_String = $("#access_level_id_administrator").val();
		var access_level_id_user_String = $("#access_level_id_user").val();
		if ( access_level_id_administrator_String === undefined || 
				access_level_id_administrator_String === null || 
				access_level_id_administrator_String === "" ) {
			throw Error( "No value for hidden field with id 'access_level_id_administrator'" );
		}
		if ( access_level_id_user_String === undefined || 
				access_level_id_user_String === null ) {
			throw Error( "Hidden field with id 'access_level_id_user' value is undefined or null or the field is not found" );
		}
		var access_level_id_administrator = parseInt( access_level_id_administrator_String, 10 ); 
		if ( isNaN( access_level_id_administrator ) ) {
			throw Error( "value in hidden field with id 'access_level_id_administrator' is not a number, it is: " + access_level_id_administrator_String );
		}
		var access_level_id_user = null;  //  default to null if field === ""
		if ( access_level_id_user_String !== "" ) {
			access_level_id_user = parseInt( access_level_id_user_String, 10 ); 
			if ( isNaN( access_level_id_user ) ) {
				throw Error( "value in hidden field with id 'access_level_id_user' is not a number, it is: " + access_level_id_user_String );
			}
		}
		var access_level_id_project_owner_String = $("#access-level-id-project-owner").val();
		var access_level_id_project_researcher_String = $("#access-level-id-project-researcher").val();
		if ( access_level_id_project_owner_String === undefined || 
				access_level_id_project_owner_String === null || 
				access_level_id_project_owner_String === "" ) {
			throw Error( "No value for hidden field with id 'access-level-id-project-owner'" );
		}
		if ( access_level_id_project_researcher_String === undefined || 
				access_level_id_project_researcher_String === null || 
				access_level_id_project_researcher_String === "" ) {
			throw Error( "No value for hidden field with id 'access-level-id-project-researcher'" );
		}
		var access_level_id_project_owner = parseInt( access_level_id_project_owner_String, 10 ); 
		var access_level_id_project_researcher = parseInt( access_level_id_project_researcher_String, 10 ); 
		if ( isNaN( access_level_id_project_owner ) ) {
			throw Error( "value in hidden field with id 'access-level-id-project-owner' is not a number, it is: " + access_level_id_project_owner_String );
		}
		if ( isNaN( access_level_id_project_researcher ) ) {
			throw Error( "value in hidden field with id 'access-level-id-project-researcher' is not a number, it is: " + access_level_id_project_researcher_String );
		}
//		version for '#invited_person_entry_template' is a div:
//		var source = $("#invited_person_entry_template").html();
//		verson for '#invited_person_entry_template' is a table:
		var $invited_person_entry_template = $("#invited_person_entry_template tbody");
		var source = $invited_person_entry_template.html();
		if ( source === undefined ) {
			throw Error( "$invited_person_entry_template.html() === undefined" );
		}
		if ( source === null ) {
			throw Error( "$invited_person_entry_template.html() === null" );
		}
		var template = Handlebars.compile(source);
		for (var index = 0; index < responseData.length; index++) {
			var responseDataItem = responseData[index];
			var context = responseDataItem;
			var html = template(context);
			var $invited_person_entry = $(html).appendTo($invited_people);
			$invited_person_entry.data("context", context);
			var attachClickHandlerToRemoveButton = true;
//			No test for "Logged in User is administrator or better" since only admins have access to this page
			if ( responseDataItem.invitedUserAccessLevel === access_level_id_administrator ) {
				var $access_level_administrator_jq = $invited_person_entry.find(".access_level_administrator_jq");
				$access_level_administrator_jq.show();
				var $invited_person_entry_access_level_update_button_jq = $invited_person_entry.find(".invited_person_entry_access_level_update_button_jq");
				$invited_person_entry_access_level_update_button_jq.show();
				var $invited_person_entry_access_level_update_button_jq = $access_level_administrator_jq.find(".invited_person_entry_access_level_update_button_jq");
//				if ( adminGlobals.logged_in_user_access_level_owner_or_better ) {
					//  Logged in User is administrator or better
				$invited_person_entry_access_level_update_button_jq.click(function(eventObject) {
					try {
						var clickThis = this;
						updateInvitedPersonAccessLevel( { clickThis: clickThis, newAccessLevel: access_level_id_user } );
						return false;
					} catch( e ) {
						reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
						throw e;
					}
				});
//				} else {
//
//					//  Logged in user is not administrator level
//
//					//     hide buttons for "revoke invite" and "change user access level"
//
//					$invited_person_entry_access_level_update_button_jq.hide();
//
//					attachClickHandlerToRemoveButton = false;
//
//				}
			} else if ( responseDataItem.invitedUserAccessLevel === access_level_id_project_owner ) {
//				var $access_level_owner_jq = $invited_person_entry.find(".access_level_owner_jq");
//				$access_level_owner_jq.show();
				var $access_level_user_jq = $invited_person_entry.find(".access_level_user_jq");
				$access_level_user_jq.show();
				if ( responseDataItem.projectId !== undefined && responseDataItem.projectId !== null ) {
					var $invited_to_project_text_jq = $invited_person_entry.find(".invited_to_project_text_jq");
					$invited_to_project_text_jq.show();
				} else {
					var $invited_to_project_text__no_project_id_jq = $invited_person_entry.find(".invited_to_project_text__no_project_id_jq");
					$invited_to_project_text__no_project_id_jq.show();
				}
			} else if ( responseDataItem.invitedUserAccessLevel === access_level_id_project_researcher ) {
				//  User in list is "Researcher" access level
//				var $access_level_researcher_jq = $invited_person_entry.find(".access_level_researcher_jq");
//				$access_level_researcher_jq.show();
				var $access_level_user_jq = $invited_person_entry.find(".access_level_user_jq");
				$access_level_user_jq.show();
				if ( responseDataItem.projectId !== undefined && responseDataItem.projectId !== null ) {
					var $invited_to_project_text_jq = $invited_person_entry.find(".invited_to_project_text_jq");
					$invited_to_project_text_jq.show();
				} else {
					var $invited_to_project_text__no_project_id_jq = $invited_person_entry.find(".invited_to_project_text__no_project_id_jq");
					$invited_to_project_text__no_project_id_jq.show();
				}
			} else { // if ( responseDataItem.invitedUserAccessLevel === access_level_id_user ) {
//				User in list is "User" access level
				var $access_level_user_jq = $invited_person_entry.find(".access_level_user_jq");
				$access_level_user_jq.show();
				var $invited_person_entry_access_level_update_button_jq = $invited_person_entry.find(".invited_person_entry_access_level_update_button_jq");
				$invited_person_entry_access_level_update_button_jq.show();
//				if ( adminGlobals.logged_in_user_access_level_owner_or_better ) {
					//  Logged in User is administrator or better
				$invited_person_entry_access_level_update_button_jq.click(function(eventObject) {
					try {
						var clickThis = this;
						updateInvitedPersonAccessLevel( { clickThis: clickThis, newAccessLevel: access_level_id_administrator } );
						return false;
					} catch( e ) {
						reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
						throw e;
					}
				});
//				} else {
//
//					//  Logged in user is not administrator level, 
//
//					//     hide buttons for "revoke invite" and "change user access level"
//
//					$invited_person_entry_access_level_update_button_jq.hide();
//
//					attachClickHandlerToRemoveButton = false;
//
//				}
			}
			var $invited_person_entry_access_level_remove_button_jq = $invited_person_entry.find(".invited_person_entry_access_level_remove_button_jq");
			if ( attachClickHandlerToRemoveButton ) {
				$invited_person_entry_access_level_remove_button_jq.click(function(eventObject) {
					try {
						var clickThis = this;
						revokePersonInvite(clickThis);
						return false;
					} catch( e ) {
						reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
						throw e;
					}
				});
			} else {
				$invited_person_entry_access_level_remove_button_jq.css({visibility:"hidden"});
			}
		}
	} else {
//		var noDataMsg = $("#invited_person_entry_no_data_template_div").html();
//		$invited_persons.html(noDataMsg);
	}
	getCurrentUserAccess();
};
/////////////////
window.updateInvitedPersonAccessLevel = function(params) {
	var clickThis = params.clickThis;
	var newAccessLevel = params.newAccessLevel;
	var $clickThis = $(clickThis);
//	get root div for this invited person entry
	var $invited_person_entry_root_div_jq = $clickThis.closest(".invited_person_entry_root_div_jq");
//	var $invited_person_entry_access_level_entry_field_jq = $invited_person_entry_root_div_jq
//	.find(".current_user_entry_access_level_entry_field_jq");
//	var invited_person_entry_access_level_entry = $invited_person_entry_access_level_entry_field_jq
//	.val();

	var invited_person_entry_user_id = $invited_person_entry_root_div_jq.attr("inviteId");

	var ajaxRequestData = {
			inviteId : invited_person_entry_user_id,
			personAccessLevel : newAccessLevel
	};

	const url = "services/user/updateInviteAccessLevel";

	const webserviceCallStandardPostResult = webserviceCallStandardPost({ dataToSend : ajaxRequestData, url }); //  External Function

	const promise_webserviceCallStandardPost = webserviceCallStandardPostResult.promise; 
	//  activeAjax = webserviceCallStandardPostResult.api;

	promise_webserviceCallStandardPost.catch( ( ) => { } );

	promise_webserviceCallStandardPost.then( ({ responseData }) => {
		try {
			updateInvitedPersonAccessLevelResponse({
				data : responseData,
				clickThis : clickThis
			});
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});
};

window.updateInvitedPersonAccessLevelResponse = function(params) {
	var data = params.data;
	if (data.status) {
//		alert("User access to project updated");
	} else {
		alert("Error Updating invited person access to project");
	}
	updateInvitedPeopleCurrentUsersLists();
};
/////////////////
window.revokePersonInvite = function(clickThis, eventObject) {
	openRevokePersonInviteOverlay(clickThis, eventObject);
	return;
};
window.openRevokePersonInviteOverlay = function(clickThis, eventObject) {
	var $clickThis = $(clickThis);
//	get root div for this current user entry
	var $invited_person_entry_root_div_jq = $clickThis.closest(".invited_person_entry_root_div_jq");
	var inviteId = $invited_person_entry_root_div_jq.attr("inviteId");	
//	copy the email address to the overlay
	var $invited_person_entry_email_address_jq = $invited_person_entry_root_div_jq.find(".invited_person_entry_email_address_jq");
	var invited_person_entry_email_address_jq = $invited_person_entry_email_address_jq.text();
	var $revoke_invite_to_project_overlay_email = $("#revoke_invite_to_project_overlay_email");
	$revoke_invite_to_project_overlay_email.text( invited_person_entry_email_address_jq );
	var $revoke_invite_to_project_confirm_button = $("#revoke_invite_to_project_confirm_button");
	$revoke_invite_to_project_confirm_button.data("inviteId", inviteId);
	$("#revoke_invite_to_project_overlay_background").show();
	$("#revoke_invite_to_project_overlay_container").show();
};
///////////
window.closeRevokePersonInviteOverlay = function(clickThis, eventObject) {
	var $revoke_invite_to_project_confirm_button = $("#revoke_invite_to_project_confirm_button");
	$revoke_invite_to_project_confirm_button.data("userId", null);
	$(".revoke_invite_to_project_overlay_show_hide_parts_jq").hide();
};
/////////////////
//put click handler for this on #revoke_invite_to_project_confirm_button
window.revokePersonInviteConfirmed = function(clickThis, eventObject) {
///////////////////
//
//window.revokePersonInvite = function(clickThis) {
//
//	if (!confirm('Are you sure you want to revoke this invite?')) {
//
//		return false;
//	}
	var $clickThis = $(clickThis);
//	get root div for this current user entry
//	var $invited_person_entry_root_div_jq = $clickThis.closest(".invited_person_entry_root_div_jq");
//
//	var inviteId = $invited_person_entry_root_div_jq.attr("inviteId");
	var inviteId = $clickThis.data("inviteId");
	var _URL = "services/user/revokeInvite";
	var ajaxParams = {
			inviteId : inviteId
	};
//	var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : ajaxParams,
		dataType : "json",
		success : function(data) {
			revokePersonInviteResponse({
				data : data,
				clickThis : clickThis
			});
		},
        failure: function(errMsg) {
        	handleAJAXFailure( errMsg );
        },
        error : function(jqXHR, textStatus, errorThrown) {
			handleAJAXError(jqXHR, textStatus, errorThrown);
//			alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
//			textStatus: " + textStatus );
		}
	});
};
window.revokePersonInviteResponse = function(params) {
	var data = params.data;
	if (data.status) {
		closeRevokePersonInviteOverlay();
//		alert("User access to project removed");
		getInvitedPeople();
	} else {
		alert("Error removing person invite");
	}
};
////////////////////
window.getCurrentUserAccess = function() {
	var requestData = {
	};
	var _URL = "services/user/listAll";
	// var request =
	$.ajax({
		type : "GET",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(data) {
			getCurrentUserAccessResponse(requestData, data);
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
////////
window.getCurrentUserAccessResponse = function(requestData, responseData) {
	if ( responseData === undefined || responseData === null || responseData.status === false ) {
		alert("Get user data failed.");
		return;
	}
	var $current_account = $("#current_account");
	$current_account.empty();
	var $invited_people_current_users = $("#invited_people_current_users");
//	$invited_people_current_users.empty();
	var users = responseData.users;
	var currentUser = responseData.currentUser;
	if ( currentUser === undefined || currentUser === null ) {
	} else {
		var source = $("#current_account_template").html();
		if  ( source !== undefined && source !== null && source !== "" ) {
			var template = Handlebars.compile(source);
			if ( template === undefined ) {
				throw Error( '$("#current_account_template").html() === undefined' );
			}
			if ( template === null ) {
				throw Error( '$("#current_account_template").html() === null' );
			}
			var context = currentUser;
			var html = template(context);
//			var $current_user_entry = 
			$(html).appendTo($current_account);
		}
	}
	if ( users === undefined || users === null || users.length === 0 ) {
		$("#no_users").show();
	} else {
		var access_level_id_administrator_String = $("#access_level_id_administrator").val();
		var access_level_id_user_String = $("#access_level_id_user").val();
		if ( access_level_id_administrator_String === undefined || 
				access_level_id_administrator_String === null || 
				access_level_id_administrator_String === "" ) {
			throw Error( "No value for hidden field with id 'access_level_id_administrator'" );
		}
		if ( access_level_id_user_String === undefined || 
				access_level_id_user_String === null ) {
			throw Error( "Hidden field with id 'access_level_id_user' value is undefined or null or the field is not found" );
		}
		var access_level_id_administrator = parseInt( access_level_id_administrator_String, 10 ); 
		if ( isNaN( access_level_id_administrator ) ) {
			throw Error( "value in hidden field with id 'access_level_id_administrator' is not a number, it is: " + access_level_id_administrator_String );
		}
		var access_level_id_user = null;  //  default to null if field === ""
		if ( access_level_id_user_String !== "" ) {
			access_level_id_user = parseInt( access_level_id_user_String, 10 ); 
			if ( isNaN( access_level_id_user ) ) {
				throw Error( "value in hidden field with id 'access_level_id_user' is not a number, it is: " + access_level_id_user_String );
			}
		}
		var source = $("#user_entry_template tbody").html();
		if ( source === undefined ) {
			throw Error( '$("#user_entry_template tbody").html() === undefined' );
		}
		if ( source === null ) {
			throw Error( '$("#user_entry_template tbody").html() === null' );
		}
		var template = Handlebars.compile(source);
		for (var index = 0; index < users.length; index++) {
			var userDataItem = users[index];
			var context = userDataItem;
			var html = template(context);
			var $user_entry = $(html).appendTo($invited_people_current_users);
			$user_entry.data("context", context);
			var userAccessLevel = userDataItem.authUser.userAccessLevel;
			var userEnabledAppSpecific =  userDataItem.authUser.enabledAppSpecific;
			var userEnabledUserMgmtGlobalLevel =  userDataItem.authUser.enabledUserMgmtGlobalLevel;
//			var $active_user_entry_access_level_update_button_jq  
//				= $active_user_entry.find(".current_user_entry_access_level_update_button_jq");
//
//			$active_user_entry_access_level_update_button_jq.click( function(eventObject) {
//
//				var clickThis = this;
//				updateUserAccessLevel( clickThis );
//			});
			//  If record being processed is the currently logged in user
			if ( adminGlobals.logged_in_user_id === userDataItem.authUser.id ) {
				// Show the user's level
				if ( userAccessLevel === access_level_id_administrator ) {
					var $access_level_administrator_jq = $user_entry.find(".access_level_administrator_jq");
					$access_level_administrator_jq.show();
				} else {
					var $access_level_administrator_jq = $user_entry.find(".access_level_user_jq");
					$access_level_administrator_jq.show();					
				}		
				//  Hide the button to change the user's level
				$user_entry_access_level_update_button_jq = $user_entry.find(".user_entry_access_level_update_button_jq");
				$user_entry_access_level_update_button_jq.css({visibility:"hidden"});
			} else {
				//  User being processed is not the currently logged on user
				if ( userEnabledAppSpecific && userEnabledUserMgmtGlobalLevel ) {
					//  User is enabled
					var $user_disable_button_jq = $user_entry.find(".user_disable_button_jq");
					$user_disable_button_jq.show();
					$user_disable_button_jq.click( function(eventObject) {
						try {
							var clickThis = this;
							disableUser( clickThis );
						} catch( e ) {
							reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
							throw e;
						}
					});
					var $user_entry_access_level_update_button_jq = $user_entry.find(".user_entry_access_level_update_button_jq");
					if ( userAccessLevel === access_level_id_administrator ) {
						$user_entry_access_level_update_button_jq.click(function(eventObject) {
							try {
								var clickThis = this;
								//  access_level_id_user  requires special handling if it is null
								updateUserAccessLevel( { clickThis: clickThis, newAccessLevel: access_level_id_user } );
								return false;
							} catch( e ) {
								reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
								throw e;
							}
						});					
						var $access_level_administrator_jq = $user_entry.find(".access_level_administrator_jq");
						$access_level_administrator_jq.show();
					} else {
						$user_entry_access_level_update_button_jq.click(function(eventObject) {
							try {
								var clickThis = this;
								updateUserAccessLevel( { clickThis: clickThis, newAccessLevel: access_level_id_administrator } );
								return false;
							} catch( e ) {
								reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
								throw e;
							}
						});					
						var $access_level_administrator_jq = $user_entry.find(".access_level_user_jq");
						$access_level_administrator_jq.show();					
					}
					
				} else if ( ! userEnabledUserMgmtGlobalLevel ) {
					//  User is disabled Globally
					var $user_disabled_global_jq = $user_entry.find(".user_disabled_global_jq");
					$user_disabled_global_jq.show();
//					var $user_enable_button_jq = $user_entry.find(".user_enable_button_jq");
//					$user_enable_button_jq.show();
//					$user_enable_button_jq.click( function(eventObject) {
//						try {
//							var clickThis = this;
//							enableUser( clickThis );
//						} catch( e ) {
//							reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
//							throw e;
//						}
//					});
					var $name_of_user_jq = $user_entry.find(".name_of_user_jq");
					$name_of_user_jq.addClass( "name-of-user-disabled-user" );
				} else {
					//  User is disabled for this app
					var $user_disabled_jq = $user_entry.find(".user_disabled_jq");
					$user_disabled_jq.show();
					var $user_enable_button_jq = $user_entry.find(".user_enable_button_jq");
					$user_enable_button_jq.show();
					$user_enable_button_jq.click( function(eventObject) {
						try {
							var clickThis = this;
							enableUser( clickThis );
						} catch( e ) {
							reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
							throw e;
						}
					});
					var $name_of_user_jq = $user_entry.find(".name_of_user_jq");
					$name_of_user_jq.addClass( "name-of-user-disabled-user" );
				}
			}			
		}
	}
};
/////////////////
window.updateUserAccessLevel = function(params) {
	var clickThis = params.clickThis;
	var newAccessLevel = params.newAccessLevel;
	var $clickThis = $(clickThis);
	// get root div for this current user entry
	var $current_user_entry_root_div_jq = $clickThis
			.closest(".current_user_entry_root_div_jq");
	var current_user_entry_user_id = $current_user_entry_root_div_jq
			.attr("userId");
	var _URL = "services/user/updateGlobalAccess";
	var ajaxParams = {
		personId : current_user_entry_user_id,
		personAccessLevel : newAccessLevel
	};
	// var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : ajaxParams,
		dataType : "json",
		success : function(data) {
			updateUserAccessLevelResponse({
				data : data,
				clickThis : clickThis
			});
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
//////
window.updateUserAccessLevelResponse = function(params) {
	var data = params.data;
	if (data.status) {
//		alert("User access updated");
		updateInvitedPeopleCurrentUsersLists();
	} else {
		alert("Error Updating user access");
	}
};
/////////////////
window.disableUser = function(clickThis) {
//	var $clickThis = $(clickThis);
	enableDisableUser( { enabled: false, clickThis: clickThis });
};
/////////////////
window.enableUser = function(clickThis) {
//var $clickThis = $(clickThis);
	enableDisableUser( { enabled: true, clickThis: clickThis });
};
/////////////////
window.enableDisableUser = function(params) {
	var enabled = params.enabled;
	var clickThis = params.clickThis;
	var $clickThis = $(clickThis);
//	get root div for this current user entry
	var $current_user_entry_root_div_jq = $clickThis.closest(".current_user_entry_root_div_jq");
	var current_user_entry_user_id = $current_user_entry_root_div_jq.attr("userId");
	var _URL = "services/user/updateEnabledFlag";
	var ajaxParams = {
			personId : current_user_entry_user_id,
			personEnabledFlag : enabled
	};
//	var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : ajaxParams,
		dataType : "json",
		success : function(data) {
			enableDisableUserResponse({
				data : data,
				clickThis : clickThis
			});
		},
        failure: function(errMsg) {
        	handleAJAXFailure( errMsg );
        },
        error : function(jqXHR, textStatus, errorThrown) {
			handleAJAXError(jqXHR, textStatus, errorThrown);
//			alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
//			textStatus: " + textStatus );
		}
	});
};
window.enableDisableUserResponse = function(params) {
	var data = params.data;
	if (data.status) {
//		alert("User updated");
		updateInvitedPeopleCurrentUsersLists();
	} else {
		alert("Error Updating user");
	}
};

////////////////////////////
window.invitePerson = function(clickThis, eventObject) {
	var $invite_user_email = $("#invite_user_email");
	if ($invite_user_email.length === 0) {
		throw Error( "Unable to find input field for id 'invite_user_email' " );
	}
	var $invite_person_access_level_entry_field = $("#invite_person_access_level_entry_field");
	if ($invite_person_access_level_entry_field.length === 0) {
	}
	var invitedPersonAccessLevel = $invite_person_access_level_entry_field.val();
	var invitedPersonEmail = $invite_user_email.val();
	if ( invitedPersonEmail === undefined || invitedPersonEmail === null ) {
		throw Error( "ERROR: invitedPersonEmail === undefined || invitedPersonEmail === null" );
	}
	if (invitedPersonEmail.length === 0) {
		var $element = $("#error_message_field_empty");
		showErrorMsg( $element );
		return;  //  !!!  EARLY EXIT
	}
	var requestData = {
		invitedPersonEmail : invitedPersonEmail,
		invitedPersonAccessLevel : invitedPersonAccessLevel
	};
	var _URL = "services/user/invite";
	// var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(data) {
			inviteComplete(requestData, data);
		},
		failure : function(errMsg) {
			var $element = $("#error_message_system_error");
			showErrorMsg( $element );
		},
		error : function(jqXHR, textStatus, errorThrown) {
			handleAJAXError(jqXHR, textStatus, errorThrown);
			// alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
			// textStatus: " + textStatus );
		}
	});
};
//////////
window.inviteComplete = function(requestData, responseData) {
	if ( ! responseData.status ) {
		if ( responseData.emailAddressDuplicateError ) {
			var $element = $("#error_message_email_already_exists");
			showErrorMsg( $element );
		} else {
			var $element = $("#error_message_system_error");
			showErrorMsg( $element );
		}
		return;
	} 
//	alert("Person invited: email: " + requestData.invitedPersonEmail);
	clearInviteUserField();
	$("#invite_user_collapsed").show();
	$("#invite_user_expanded").hide();
	updateInvitedPeopleCurrentUsersLists();
};
window.clearInviteUserField = function( ) {
	var $invite_user_email = $("#invite_user_email");
	$invite_user_email.val("");
};

/////////////

//  Create User


///////////////////
window.createAccountGetFormDataAndValidate = function() {

	try {
		hideAllErrorMessages();
		var $create_person_access_level_entry_field = $("#create_person_access_level_entry_field");
		if ($create_person_access_level_entry_field.length === 0) {
			throw Error( "Unable to find input field for id 'create_person_access_level_entry_field' " );
		}
		var accessLevel = $create_person_access_level_entry_field.val();
		
		var $firstName = $("#firstName");
		if ($firstName.length === 0) {
			throw Error( "Unable to find input field for id 'firstName' " );
		}
		var firstName = $firstName.val();
		var $lastName = $("#lastName");
		if ($lastName.length === 0) {
			throw Error( "Unable to find input field for id 'lastName' " );
		}
		var lastName = $lastName.val();
		var $organization = $("#organization");
		if ($organization.length === 0) {
			throw Error( "Unable to find input field for id 'organization' " );
		}
		var organization = $organization.val();

		var $email = $("#email");
		if ($email.length === 0) {
			throw Error( "Unable to find input field for id 'email' " );
		}
		var email = $email.val();
		var $username = $("#username");
		if ($username.length === 0) {
			throw Error( "Unable to find input field for id 'username' " );
		}
		var username = $username.val();
		var $password = $("#password");
		if ($password.length === 0) {
			throw Error( "Unable to find input field for id 'password' " );
		}
		var password = $password.val();
		var $passwordConfirm = $("#passwordConfirm");
		if ($passwordConfirm.length === 0) {
			throw Error( "Unable to find input field for id 'passwordConfirm' " );
		}
		var passwordConfirm = $passwordConfirm.val();
		if ( firstName === "" ||
				lastName === "" ||
				organization === "" ||
				email === "" ||
				username === "" ||
				password === "" ||
				passwordConfirm === "" ) {
			var $element = $("#error_message_all_fields_required");
			showErrorMsg( $element );
			return null;  //  !!!  EARLY EXIT
		} 
		if ( password !== passwordConfirm ) {
			var $element = $("#error_message_password_confirm_password_not_match");
			showErrorMsg( $element );
			return null;  //  !!!  EARLY EXIT
		} 

		var formPageData = {
				accessLevel : accessLevel,
				firstName : firstName,
				lastName :  lastName,
				organization :  organization,
				email :  email,
				username :  username,
				password :  password
		};

		return formPageData;

	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
};



window.createAccountFormSubmit = function() {

	var requestData = createAccountGetFormDataAndValidate();
	if ( requestData === null ) {  //  Error in form data so exit
		return;  //  EARLY EXIT
	}

	var _URL = "services/user/createAccountUsingAdminUserAccount";
//	var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(data) {
			createAccountComplete( { requestData: requestData, responseData: data } );
		},
		failure : function(errMsg) {
			var $element = $("#error_message_system_error");
			showErrorMsg( $element );
		},
		error : function(jqXHR, textStatus, errorThrown) {
			handleAJAXError(jqXHR, textStatus, errorThrown);
//			alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
//			textStatus: " + textStatus );
		}
	});
};

window.createAccountComplete = function( params ) {
	var requestData = params.requestData;
	var responseData = params.responseData;
	
	if ( ! responseData.status ) {
		if ( responseData.duplicateUsername && responseData.duplicateEmail ) {
			var $element = $("#error_message_username_email_taken");
			showErrorMsg( $element );
			var $emailInput = $("#email");
			$emailInput.focus();
		} else if ( responseData.duplicateUsername ) {
			var $element = $("#error_message_username_taken");
			showErrorMsg( $element );
			var $usernameInput = $("#username");
			$usernameInput.focus();
		} else if ( responseData.duplicateEmail ) {
			var $element = $("#error_message_email_taken");
			showErrorMsg( $element );
			var $emailInput = $("#email");
			$emailInput.focus();
		} else if ( responseData.errorMessage ) {
			$("#error_message_from_server_text").text( responseData.errorMessage );
			var $element = $("#error_message_from_server");
			showErrorMsg( $element );
		} else {
			var $element = $("#error_message_system_error");
			showErrorMsg( $element );
		}
		return;
	} 
	
	updateInvitedPeopleCurrentUsersLists();
};



//////////////////
window.initAdmin = function() {
	var $logged_in_user_id = $("#logged_in_user_id");
	if ( $logged_in_user_id.length === 0 ) {
		throw Error( "Unable to find hidden field '#logged_in_user_id'" );
	}
	var logged_in_user_id = $("#logged_in_user_id").val();
	if (logged_in_user_id === undefined || logged_in_user_id === null
			|| logged_in_user_id.length === 0) {
		throw Error( "No value in hidden field '#logged_in_user_id' " );
	}
	try {
		adminGlobals.logged_in_user_id = parseInt(logged_in_user_id, 10);
	} catch (ex) {
		throw Error( "failed to parse logged_in_user_id: " + logged_in_user_id );
	}
	if ( isNaN( adminGlobals.logged_in_user_id ) ) {
		throw Error( "failed to parse logged_in_user_id (parse to NaN): " + logged_in_user_id );
	}
	$("#invite_user_button").click(function(eventObject) {
		try {
			var clickThis = this;
			invitePerson( clickThis );
			return false;
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});
	$(".invite_user_expand_link_jq").click(function(eventObject) {
		try {
			$("#invite_user_collapsed").hide();
			$("#invite_user_expanded").show();
			return false;
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});
	$(".invite_user_cancel_button_jq").click(function(eventObject) {
		try {
			clearInviteUserField();
			$("#invite_user_collapsed").show();
			$("#invite_user_expanded").hide();
			return false;
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});
	$("#revoke_invite_to_project_confirm_button").click(function(eventObject) {
		try {
			var clickThis = this;
			revokePersonInviteConfirmed( clickThis, eventObject );
			return false;
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});
	$(".revoke_invite_to_project_overlay_show_hide_parts_jq").click(function(eventObject) {
		try {
			var clickThis = this;
			closeRevokePersonInviteOverlay( clickThis, eventObject );
			return false;
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});
	
	$(".create_user_expand_link_jq").click(function(eventObject) {
		try {
			$("#create_user_collapsed").hide();
			$("#create_user_expanded").show();
			return false;
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});
	$(".create_user_cancel_button_jq").click(function(eventObject) {
		try {
			clearInviteUserField();
			$("#create_user_collapsed").show();
			$("#create_user_expanded").hide();
			return false;
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});
	updateInvitedPeopleCurrentUsersLists();
};

///////////////
$(document).ready(function() {
	try {
		initAdmin();
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
});
