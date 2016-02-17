"use strict";


var _MAX_PDB_FILESIZE_IN_MB = 200; 
var _MAX_PDB_FILESIZE = _MAX_PDB_FILESIZE_IN_MB * 1000 * 1000;



// initialize the pdb upload overlay
$(document).ready(function()  { 
	attachPDBUploadOverlayClickHandlers();
	attachPDBFileUploadHandlers();
});

// opens the overlay
var openPDBUploadOverlay = function (  ) {

	$("#pdb-upload-modal-dialog-overlay-background").show();
	$(".pdb-upload-overlay-div").show();
	
	//  scroll the window to the top left
	var $window = $(window);
	$window.scrollTop( 0 );
	$window.scrollLeft( 0 );
};

// close the overlay
var closePDBUploadOverlay = function (  ) {

	$("#pdb-upload-modal-dialog-overlay-background").hide();
	$(".pdb-upload-overlay-div").hide();

};

// attach handlers for the upload overlay, namely ensure clicking "X" closes the overlay.
var attachPDBUploadOverlayClickHandlers = function (  ) {
	var $pdb_upload_overlay_X_for_exit_overlay = $(".pdb-upload-overlay-X-for-exit-overlay");
	
	$pdb_upload_overlay_X_for_exit_overlay.click( function( eventObject ) {
		closePDBUploadOverlay();
	} );
	
};





function attachPDBFileUploadHandlers() {

	var $uploadButton  = $("#pdb-upload-button");
	var $fileField  = $("#pdb-file-field");

	// make upload button clickable!
	$uploadButton.click( function(eventObject) {
		uploadPDBFile();		
	});

	// enable upload button if they select a file
	$fileField.change( function( eventObject ) {
		$uploadButton.prop("disabled", false );
	});
	
	// upload button defaults to disabled
	$uploadButton.prop("disabled", true );

};


function uploadPDBFile() {
	
	// get our file
	var file = document.getElementById("pdb-file-field").files[ 0 ];
	var desc = $("#pdb-file-description").val();
//	var visibility = $("#pdb-file-visibility").val();
	
	var projectId = $("#project_id" ).val();
	
	if ( projectId === undefined || projectId === null || projectId === "" ) {
		
		throw "Input field with id 'project_id' not found";
	}
	
	

	if ( file.size > _MAX_PDB_FILESIZE ) {
		
		alert( "File to upload is too large, it exceeds " + _MAX_PDB_FILESIZE_IN_MB + "MB." );

		return;
	}

	
	
	var formData = new FormData();
	formData.append( 'file', file, file.name );
	formData.append( 'description', desc );
	formData.append( 'projectId', projectId );
//	formData.append( 'visibility', visibility );
	
	var xhr = new XMLHttpRequest();
	xhr.open('POST', contextPathJSVar + '/uploadPDBFileService.do', true);

    xhr.onload = function() {
        if (xhr.status === 200) {

        	var xhrResponse = xhr.response;


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
        			
        			throw 'Unknown error occurred: [' + xhr.responseText + ']';
        		}

        	} catch (e) {
        		
        		alert("File Upload failed. Failed to get information from server response.");
        		
        		throw 'Unknown error occurred: [' + xhr.responseText + ']';
        	}




        	closePDBUploadOverlay();
        	
        	var pdbFileId = getSelectedPDBFile().id;
        	loadPDBFiles( pdbFileId );
        	
          } else {

        	  
        	  handleAJAXError( xhr );
        	  
        	  
          }
    };
    
    xhr.send(formData);

}
