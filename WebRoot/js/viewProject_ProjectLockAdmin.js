
//    viewProject_ProjectLockAdmin.js

//  Javascript for the project admin of project locked flag of the page viewProject.jsp

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";



/////////////////

var unlockProject = function(clickThis) {

	var requestData = {
			projectId : getProjectIdForProjectLockUnlockChange()
	};

	var _URL = contextPathJSVar + "/services/project/projectLockedAdmin/unlock";

//	var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(responseData) {

			$("#project_just_unlocked_message").show();

			reloadPageForProjectLockUnlockChange( clickThis );
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




/////////////////

var lockProject = function(clickThis) {

	var requestData = {
			projectId : getProjectIdForProjectLockUnlockChange()
	};

	var _URL = contextPathJSVar + "/services/project/projectLockedAdmin/lock";

//	var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(responseData) {

			$("#project_just_locked_message").show();

			reloadPageForProjectLockUnlockChange( clickThis );
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

var getProjectIdForProjectLockUnlockChange = function() {
	

	var $project_id = $("#project_id");

	if ($project_id.length === 0) {

		throw "Unable to find input field for id 'project_id' ";
	}

	var project_id = $project_id.val();
	

	if ( project_id === undefined || project_id === null || project_id === "" ) {

		throw "No value in input field for id 'project_id' ";
	}

	return project_id;
};


/////////////

var reloadPageForProjectLockUnlockChange = function(clickThis) {

	setTimeout( function() { 
		
		window.location.reload(true);

	}, 1000 ); //  time in milliseconds
};

