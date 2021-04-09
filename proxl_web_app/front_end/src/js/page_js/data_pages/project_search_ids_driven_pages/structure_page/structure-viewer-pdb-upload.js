"use strict";

/**
 * Javascript for the viewMergedStructure.jsp page
 *  
 * !!! The following variables passed in from "structure-viewer-page.js" are used in this file:
 * 
 *    structurePagePrimaryRootCodeObject (copied to local variable structurePagePrimaryRootCodeObject_LocalCopy)
 */



var structurePagePrimaryRootCodeObject_LocalCopy = undefined; // passed in from "structure-viewer-page.js"



var _MAX_PDB_FILESIZE_IN_MB = 200; 
var _MAX_PDB_FILESIZE = _MAX_PDB_FILESIZE_IN_MB * 1000 * 1000;



// initialize the pdb upload overlay - Moved to main JS file
// $(document).ready(function()  { 
	
// 	try {
	
// 		attachPDBUploadOverlayClickHandlers();
// 		attachPDBFileUploadHandlers();
		
// 	} catch( e ) {
// 		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
// 		throw e;
// 	}
// });

// opens the overlay
window.openPDBUploadOverlay = function (  ) {

	try {

		$("#pdb-upload-modal-dialog-overlay-background").show();
		$(".pdb-upload-overlay-div").show();

		//  scroll the window to the top left
		var $window = $(window);
		$window.scrollTop( 0 );
		$window.scrollLeft( 0 );

	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
};

// close the overlay
window.closePDBUploadOverlay = function (  ) {

	try {

		$("#pdb-upload-modal-dialog-overlay-background").hide();
		$(".pdb-upload-overlay-div").hide();

	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}

};

// attach handlers for the upload overlay, namely ensure clicking "X" closes the overlay.
var attachPDBUploadOverlayClickHandlers = function (  ) {
	var $pdb_upload_overlay_X_for_exit_overlay = $(".pdb-upload-overlay-X-for-exit-overlay");
	
	$pdb_upload_overlay_X_for_exit_overlay.click( function( eventObject ) {

		try {
		
			closePDBUploadOverlay();
			
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	} );
	
};





function attachPDBFileUploadHandlers() {

	var $uploadButton  = $("#pdb-upload-button");
	var $fileField  = $("#pdb-file-field");

	// make upload button clickable!
	$uploadButton.click( function(eventObject) {

		try {
		
			uploadPDBFile();		
			
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});

	// enable upload button if they select a file
	$fileField.change( function( eventObject ) {
		
		// get file to upload
		var fileToUpload = document.getElementById("pdb-file-field").files[ 0 ];
		
		if ( fileToUpload ) {
			
			//  Have file to upload
			$uploadButton.prop("disabled", false );
		} else {
			// No file to upload
			$uploadButton.prop("disabled", true );
		}
		
	});
	
	// upload button defaults to disabled
	$uploadButton.prop("disabled", true );

};


window.uploadPDBFile = function() {
	
	// get file to upload
	var fileToUpload = document.getElementById("pdb-file-field").files[ 0 ];
	
	if ( ! fileToUpload ) {
		
		return;  //  NO File Selected
	}

	var pdbUserDescription = $("#pdb-file-description").val();

	var projectId = $("#project_id" ).val();
	
	if ( projectId === undefined || projectId === null || projectId === "" ) {
		
		throw "Input field with id 'project_id' not found";
	}
	
	

	if ( fileToUpload.size > _MAX_PDB_FILESIZE ) {
		
		alert( "File to upload is too large, it exceeds " + _MAX_PDB_FILESIZE_IN_MB + "MB." );

		return;
	}

	
	var xmlHttpRequest = new XMLHttpRequest();

    xmlHttpRequest.onload = function() {

    	try {

    		if (xmlHttpRequest.status === 200) {

    			var xhrResponse = xmlHttpRequest.response;


    			var resp = null;

    			try {
    				resp = JSON.parse(xhrResponse);

    				if ( resp.statusSuccess ) {


    				} else if ( resp.parsePDBFailed ) {

    					alert( "Unable to parse the uploaded PDB file. Please upload a different file." );
    					return;

    				} else if ( resp.noChains ) {

    					alert( "Can not find chains in that file. Please upload a different file." );
    					return;

    				} else {

    					alert("File Upload failed. Failed to determine error reason.");

    					throw 'Unknown error occurred: [' + xmlHttpRequest.responseText + ']';
    				}

    			} catch (e) {

    				alert("File Upload failed. Failed to get information from server response.");

    				throw 'Unknown error occurred: [' + xmlHttpRequest.responseText + ']';
    			}




    			closePDBUploadOverlay();

    			var pdbFileId = structurePagePrimaryRootCodeObject_LocalCopy.call__getSelectedPDBFile().id;
    			structurePagePrimaryRootCodeObject_LocalCopy.call__loadPDBFiles( pdbFileId );

    		} else {


    			handleAJAXError( xmlHttpRequest );


    		}
    		
    	} catch( e ) {
    		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
    		throw e;
    	}
	};
	
	xmlHttpRequest.open('POST', 'uploadPDBFileService.do', true);

	xmlHttpRequest.setRequestHeader( "Content-Type", "application/octet-stream" );

	const filename_of_fileToUpload = fileToUpload.name;
		
	//  Send values in Request Header

	xmlHttpRequest.setRequestHeader( "X-Proxl-description", pdbUserDescription );
	xmlHttpRequest.setRequestHeader( "X-Proxl-project_id", projectId );
	xmlHttpRequest.setRequestHeader( "X-Proxl-filename", filename_of_fileToUpload );

    xmlHttpRequest.send( fileToUpload );

}

/**
 * Called from "structure-viewer-page.js" to populate local copy of structurePagePrimaryRootCodeObject
 */
var PdbUpload_pass_structurePagePrimaryRootCodeObject = function( structurePagePrimaryRootCodeObject_LocalCopy_Param ) {
	structurePagePrimaryRootCodeObject_LocalCopy = structurePagePrimaryRootCodeObject_LocalCopy_Param;
}


export { attachPDBUploadOverlayClickHandlers, attachPDBFileUploadHandlers, PdbUpload_pass_structurePagePrimaryRootCodeObject }
