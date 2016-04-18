
//   manageConfigurationPage.js



//  Javascript for the project admin section of the page manageUsersPage.jsp

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";




/////////////////

var getListConfiguration = function() {

	var requestData = {

	};

	var _URL = contextPathJSVar + "/services/config/list";

//	var request =
	$.ajax({
		type : "GET",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(data) {

			getListConfigurationResponse(requestData, data);
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

var getListConfigurationResponse = function(requestData, responseData) {

	var $invited_people = $("#invited_people_current_users");

	$invited_people.empty();

	if (responseData && responseData.length > 0) {

		var access_level_id_administrator_String = $("#access_level_id_administrator").val();
		var access_level_id_user_String = $("#access_level_id_user").val();

		if ( access_level_id_administrator_String === undefined || 
				access_level_id_administrator_String === null || 
				access_level_id_administrator_String === "" ) {

			throw "No value for hidden field with id 'access_level_id_administrator'";
		}

	}
};






function initAdmin() {

	var $logged_in_user_id = $("#logged_in_user_id");


	if ( $logged_in_user_id.length === 0 ) {

		throw "Unable to find hidden field '#logged_in_user_id'";
	}

	var logged_in_user_id = $("#logged_in_user_id").val();

	if (logged_in_user_id === undefined || logged_in_user_id === null
			|| logged_in_user_id.length === 0) {

		throw "No value in hidden field '#logged_in_user_id' ";
	}

	try {
		adminGlobals.logged_in_user_id = parseInt(logged_in_user_id, 10);
	} catch (ex) {

		throw "failed to parse logged_in_user_id: " + logged_in_user_id;
	}

	if ( isNaN( adminGlobals.logged_in_user_id ) ) {

		throw "failed to parse logged_in_user_id (parse to NaN): " + logged_in_user_id;
	}


	$("#invite_user_button").click(function(eventObject) {

		var clickThis = this;

		invitePerson( clickThis );

		return false;
	});

	$(".invite_user_expand_link_jq").click(function(eventObject) {

		$("#invite_user_collapsed").hide();
		$("#invite_user_expanded").show();

		return false;
	});


	$(".invite_user_cancel_button_jq").click(function(eventObject) {

		clearInviteUserField();

		$("#invite_user_collapsed").show();
		$("#invite_user_expanded").hide();

		return false;
	});



	$("#revoke_invite_to_project_confirm_button").click(function(eventObject) {

		var clickThis = this;

		revokePersonInviteConfirmed( clickThis, eventObject );

		return false;
	});

	$(".revoke_invite_to_project_overlay_show_hide_parts_jq").click(function(eventObject) {

		var clickThis = this;

		closeRevokePersonInviteOverlay( clickThis, eventObject );

		return false;
	});


	updateInvitedPeopleCurrentUsersLists();

};


///////////////

$(document).ready(function() {

	initAdmin();

});
