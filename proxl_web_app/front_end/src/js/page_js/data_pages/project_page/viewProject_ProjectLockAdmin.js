
//    viewProject_ProjectLockAdmin.js

//  Javascript for the project admin of project locked flag of the page viewProject.jsp

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";



/////////////////

var unlockProject = function(clickThis) {

	try {

		var requestData = {
				projectId : getProjectIdForProjectLockUnlockChange()
		};

		var _URL = contextPathJSVar + "/services/project/projectLockedAdmin/unlock";

//		var request =
		$.ajax({
			type : "POST",
			url : _URL,
			data : requestData,
			dataType : "json",
			success : function(responseData) {

				try {

					$("#project_just_unlocked_message").show();

					reloadPageForProjectLockUnlockChange( clickThis );

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

//				alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
//				textStatus: " + textStatus );
			}
		});

	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}

};




/////////////////

var lockProject = function(clickThis) {

	try {

		var requestData = {
				projectId : getProjectIdForProjectLockUnlockChange()
		};

		var _URL = contextPathJSVar + "/services/project/projectLockedAdmin/lock";

//		var request =
		$.ajax({
			type : "POST",
			url : _URL,
			data : requestData,
			dataType : "json",
			success : function(responseData) {

				try {

					$("#project_just_locked_message").show();

					reloadPageForProjectLockUnlockChange( clickThis );

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

//				alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
//				textStatus: " + textStatus );
			}

		});

	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}

};

var getProjectIdForProjectLockUnlockChange = function() {
	

	var $project_id = $("#project_id");

	if ($project_id.length === 0) {

		throw Error( "Unable to find input field for id 'project_id' " );
	}

	var project_id = $project_id.val();
	

	if ( project_id === undefined || project_id === null || project_id === "" ) {

		throw Error( "No value in input field for id 'project_id' " );
	}

	return project_id;
};


/////////////

var reloadPageForProjectLockUnlockChange = function(clickThis) {

	try {

		setTimeout( function() { 

			window.location.reload(true);

		}, 1000 ); //  time in milliseconds

	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
};

