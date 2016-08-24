
"use strict";

///////////////////////////////////////////

//  proxlXMLFileImport.js

//  used on viewProject page

///////////////




$(document).ready( function() {

	proxlXMLFileImport.initOnDocumentReady();

});

//  Deprecated and removed in jQuery 3.0
//  $(window).unload( function( eventObject ) {
	
	
//$(window).on( "unload", function( eventObject ) {
//
//	//  Seemed to work but now doesn't work to clean up the temp dir
//	
//	proxlXMLFileImport.pageUnload( eventObject );
//	
//} );

window.addEventListener("beforeunload", function (event) {
	
	proxlXMLFileImport.pageUnload( event );
});


//////////////////////////////

///    Container class for Single File Data Item

//Constructor

var ProxlXMLFileImportFileData = function( params ) {
	this.file = params.file;
	this.fileType = params.fileType;
	this.fileTypeString = params.fileTypeString;
	this.fileIndex = params.fileIndex;
	this.isProxlXMLFile = params.isProxlXMLFile;
	
	this.uploadedToServer = undefined;
	
	
	this.xmlHttpRequest = undefined;
};

ProxlXMLFileImportFileData.prototype.getFile = function() {
	return this.file;
};

ProxlXMLFileImportFileData.prototype.getFileTypeString = function() {
	return this.fileTypeString;
};
ProxlXMLFileImportFileData.prototype.getFileType = function() {
	return this.fileType;
};

ProxlXMLFileImportFileData.prototype.getFileIndex = function() {
	return this.fileIndex;
};

ProxlXMLFileImportFileData.prototype.isIsProxlXMLFile  = function() {
	return this.isProxlXMLFile;
};

ProxlXMLFileImportFileData.prototype.isUploadedToServer  = function() {
	return this.uploadedToServer;
};

ProxlXMLFileImportFileData.prototype.setUploadedToServer  = function( uploadedToServer ) {
	this.uploadedToServer = uploadedToServer;
};

ProxlXMLFileImportFileData.prototype.getXMLHttpRequest = function() {
	return this.xmlHttpRequest;
};
ProxlXMLFileImportFileData.prototype.setXMLHttpRequest = function( xmlHttpRequest ) {
	this.xmlHttpRequest = xmlHttpRequest;
};

ProxlXMLFileImportFileData.factory = function( params ) {
	
	var constructorParam = {
			file : params.file,
			fileType : params.fileType,
			fileTypeString : params.fileTypeString,
			fileIndex : params.fileIndex,
			isProxlXMLFile : params.isProxlXMLFile
	};
	
	if ( constructorParam.fileType === undefined ) {
		
		if ( constructorParam.fileTypeString === undefined 
				|| constructorParam.fileTypeString === null
				|| constructorParam.fileTypeString === "" ) {
			
			throw "fileTypeString is undefined, null or empty";
		}
		
		constructorParam.fileType = parseInt( constructorParam.fileTypeString, 10 );
		
		if ( isNaN( constructorParam.fileType ) ) {
			
			throw "fileTypeString failed to parse: " + fileTypeString;
		}
	}
	
	var proxlXMLFileImportFileData = new ProxlXMLFileImportFileData( constructorParam );
		
	return proxlXMLFileImportFileData;
};
	
	
////////////////////////////////////////
////////////////////////////////////////

///    Main ProxlXMLFileImport Class

//  Constructor

var ProxlXMLFileImport = function() {	

	this.uploadingScanFiles = undefined;
	
	this.maxProxlXMLFileUploadSize = undefined;
	this.maxProxlXMLFileUploadSizeFormatted = undefined;
	this.maxScanFileUploadSize = undefined;
	this.maxScanFileUploadSizeFormatted = undefined;

	this.prevFileIndex = undefined;
	this.fileStorage = undefined;

	this.xmlHttpRequest = undefined;
	this.uploadKey = undefined;

	this.initialize();
	
};



ProxlXMLFileImport.prototype.CONSTANTS = { 

		UPLOAD_FILE_FORM_NAME : "uploadFile"  //  Keep in sync with server side
};



ProxlXMLFileImport.prototype.initialize  = function(  ) {


};

ProxlXMLFileImport.prototype.getNextFileIndex  = function(  ) {

	if ( ! this.prevFileIndex ) {
		this.prevFileIndex = 0;
	}
	
	this.prevFileIndex++;
	return this.prevFileIndex;
};

////////////  File Data

ProxlXMLFileImport.prototype.isFileDataEmpty  = function( ) {

	if ( ! this.fileStorage ) {
		
		return true;
	}
	
	if ( this.fileStorage.length === 0 ) {
		
		return true;
	}
	return false;
};

ProxlXMLFileImport.prototype.getAllFileData  = function( ) {

	if ( ! this.fileStorage ) {
		
		return [];
	}
	
	return this.fileStorage;
};

ProxlXMLFileImport.prototype.getFileData  = function( params ) {

	var fileIndex = params.fileIndex;

	if ( ! this.fileStorage ) {
		
		throw "no files in fileStorage";
	}
	
	var fileData = this.fileStorage[ fileIndex ];
	
	return fileData;
};

ProxlXMLFileImport.prototype.addFileData  = function( params ) {
	
	var fileData = params.fileData;
	var fileIndex = params.fileIndex;
	
	if ( ! this.fileStorage ) {
		
		this.fileStorage = [];
	}
	
	this.fileStorage[ fileIndex ] = fileData;
	
};




ProxlXMLFileImport.prototype.removeFileData  = function( params ) {

	var fileIndex = params.fileIndex;

	if ( ! this.fileStorage ) {
		
		return;
	}
	
	if ( ! this.fileStorage ) {
		
		throw "no files in fileStorage";
	}

	if ( ! this.fileStorage[ fileIndex ] ) {
		
		throw "no file in fileStorage for index: " + fileIndex;
	}
	
	delete this.fileStorage[ fileIndex ];
};


ProxlXMLFileImport.prototype.clearFileData  = function( ) {

	
	this.prevFileIndex = undefined;

	if ( ! this.fileStorage ) {
		
		return;
	}

	delete this.fileStorage;
};


ProxlXMLFileImport.prototype.getFileIndexAsIntFromDOM  = function( params ) {

	var $domElement = params.$domElement;


	var fileIndexString = $domElement.attr("data-file_index");

	if ( fileIndexString === undefined ) {

		throw 'undefined:  $domElement.attr("data-file_index");';
	}
	if ( fileIndexString === "" ) {

		throw 'empty string:  $domElement.attr("data-file_index");';
	}

	var fileIndex = parseInt( fileIndexString, 10 );

	if ( isNaN( fileIndex ) ) {

		throw 'Fail parse to int:  $domElement.attr("data-file_index") : fileIndexString: ' + fileIndexString;
	}

	return fileIndex;
};






/////////////////////////////////


ProxlXMLFileImport.prototype.initOnDocumentReady  = function(  ) {

	var objectThis = this;
	
	
	
	//  Get uploading scan files
	
	
	this.uploadingScanFiles = false;

	var $proxl_xml_file_upload_overlay_upload_scan_files = $("#proxl_xml_file_upload_overlay_upload_scan_files");

	if ( $proxl_xml_file_upload_overlay_upload_scan_files.length > 0 ) {
		
		var proxl_xml_file_upload_overlay_upload_scan_files_text = $proxl_xml_file_upload_overlay_upload_scan_files.val();
		
		if ( proxl_xml_file_upload_overlay_upload_scan_files_text !== "" ) {
			
			//  Only populated when true
			
			this.uploadingScanFiles = true;
		}
	}
	
	
	

	//  Get max Proxl XML upload size

	var $proxl_xml_file_max_file_upload_size = $("#proxl_xml_file_max_file_upload_size");

	if ( $proxl_xml_file_max_file_upload_size.length === 0 ) {
		
		throw "#proxl_xml_file_max_file_upload_size input field missing";
	}
	
	var proxl_xml_file_max_file_upload_size_val = $proxl_xml_file_max_file_upload_size.val();

	this.maxProxlXMLFileUploadSize = parseInt( proxl_xml_file_max_file_upload_size_val, 10 );

	if ( isNaN( this.maxProxlXMLFileUploadSize ) ) {

		throw "Unable to parse #proxl_xml_file_max_file_upload_size: " + proxl_xml_file_max_file_upload_size_val;
	}

	var $proxl_xml_file_max_file_upload_size_formatted = $("#proxl_xml_file_max_file_upload_size_formatted");
	
	if ( $proxl_xml_file_max_file_upload_size_formatted.length === 0 ) {
		
		throw "#proxl_xml_file_max_file_upload_size_formatted input field missing";
	}

	this.maxProxlXMLFileUploadSizeFormatted = $proxl_xml_file_max_file_upload_size_formatted.val();

	if ( this.maxProxlXMLFileUploadSizeFormatted === undefined || this.maxProxlXMLFileUploadSizeFormatted === "" ) {
		
		throw "#proxl_xml_file_max_file_upload_size_formatted input field empty";
	}
	
	if ( this.uploadingScanFiles ) {  

		//  Get max Scan upload size

		var $proxl_import_scan_file_max_file_upload_size = $("#proxl_import_scan_file_max_file_upload_size");

		if ( $proxl_import_scan_file_max_file_upload_size.length === 0 ) {

			throw "#proxl_import_scan_file_max_file_upload_size input field missing";
		}

		var proxl_import_scan_file_max_file_upload_size_val = $proxl_import_scan_file_max_file_upload_size.val();

		this.maxScanFileUploadSize = parseInt( proxl_import_scan_file_max_file_upload_size_val, 10 );

		if ( isNaN( this.maxScanFileUploadSize ) ) {

			throw "Unable to parse #proxl_import_scan_file_max_file_upload_size: " + proxl_import_scan_file_max_file_upload_size_val;
		}

		var $proxl_import_scan_file_max_file_upload_size_formatted = $("#proxl_import_scan_file_max_file_upload_size_formatted");

		if ( $proxl_import_scan_file_max_file_upload_size_formatted.length === 0 ) {

			throw "#proxl_import_scan_file_max_file_upload_size_formatted input field missing";
		}

		this.maxScanFileUploadSizeFormatted = $proxl_import_scan_file_max_file_upload_size_formatted.val();

		if ( this.maxProxlXMLFileUploadSizeFormatted === undefined || this.maxProxlXMLFileUploadSizeFormatted === "" ) {

			throw "#proxl_import_scan_file_max_file_upload_size_formatted input field empty";
		}

	}	
	
	
	
	
	////////////////////

	$(".open_proxl_file_upload_overlay_jq").click(function(eventObject) {

		var clickThis = this;

		objectThis.openOverlay( clickThis, eventObject );

		eventObject.preventDefault();
		
		return false;
	});	 			


	$(".proxl_xml_file_upload_overlay_close_parts_jq").click(function(eventObject) {

		var clickThis = this;

		objectThis.closeClicked( clickThis, eventObject );

		eventObject.preventDefault();
		
		return false;
	});
	
	
	$("#import_proxl_xml_choose_proxl_xml_file_button").click(function(eventObject) {

//		var clickThis = this;

		$("#import_proxl_xml_proxl_xml_file_field").click(); // "click" the file input field

		eventObject.preventDefault();
		
		return false;
	});

	$("#import_proxl_xml_proxl_xml_file_field").change(  function(eventObject) {

		var changeThis = this;

		objectThis.proxlXMLFileDialogChanged( changeThis, eventObject );
	});

	//  Remove The Proxl XML file, aborting the upload if in progress

	$("#import_proxl_xml_remove_proxl_xml_file_button").click(function( eventObject ) {

		var clickThis = this;

		objectThis.removeProxlXMLFile( { clickThis : clickThis, doAbortIfNeeded : true, eventObject : eventObject } );
		
		eventObject.preventDefault();
		
		return false;
	});
	
	

	
	$("#import_proxl_xml_choose_scan_file_button").click(function(eventObject) {

//		var clickThis = this;

		$("#import_proxl_xml_scan_file_field").click(); // "click" the file input field

		eventObject.preventDefault();
		
		return false;
	});

	$("#import_proxl_xml_scan_file_field").change(  function(eventObject) {

		var changeThis = this;

		objectThis.scanFileDialogChanged( changeThis, eventObject );
	});


	$("#import_proxl_xml_file_close_button").click(function(eventObject) {

		var clickThis = this;

		objectThis.closeClicked( clickThis, eventObject );

		eventObject.preventDefault();
		
		return false;
	});

	

	$("#import_proxl_xml_file_submit_button").click(function(eventObject) {

		var clickThis = this;

		objectThis.submitClicked( clickThis, eventObject );
		
		eventObject.preventDefault();

		return false;
	});

	$("#import_proxl_xml_file_submit_import_success_submit_another_import_button").click(function(eventObject) {

//		var clickThis = this;

		objectThis.openOverlay(); // Will call resetOverlay

		eventObject.preventDefault();
		
		return false;
	});


	
	////////////////////
	
	//   Upload error message overlay

	$(".import_proxl_xml_file_upload_error_overlay_cancel_parts_jq").click(function(eventObject) {

		$(".import_proxl_xml_file_upload_error_overlay_show_hide_parts_jq").hide();

		eventObject.preventDefault();
		
		return false;
	});

	////////////////////
	
	//   File Choose error message overlay

	$(".import_proxl_xml_choose_file_error_overlay_show_hide_parts_jq").click(function(eventObject) {

		$(".import_proxl_xml_choose_file_error_overlay_show_hide_parts_jq").hide();

		eventObject.preventDefault();
		
		return false;
	});

	////////////////////
	
	//   Confirm Abandon Upload overlay

	$(".import_proxl_xml_file_confirm_abandon_upload_overlay_cancel_parts_jq").click(function(eventObject) {

		$(".import_proxl_xml_file_confirm_abandon_upload_overlay_show_hide_parts_jq").hide();

		eventObject.preventDefault();
		
		return false;
	});
	
	$("#import_proxl_xml_file_confirm_abandon_upload_confirm_button").click(function(eventObject) {

		var clickThis = this;
		
		$(".import_proxl_xml_file_confirm_abandon_upload_overlay_show_hide_parts_jq").hide();
		
		objectThis.closeOverlay( clickThis, eventObject );
		
		eventObject.preventDefault();
		
		return false;
	});
	
	
	
};


//  Called on Page unload

ProxlXMLFileImport.prototype.pageUnload  = function( eventObject ) {
	
	//  Seemed to work but now doesn't work to clean up the temp dir
	
	//  Call to remove partial upload if one is set up or in progress
	
	this.closeOverlay( { eventObject : eventObject } );
};


//User clicked the "Close" button or the close "X"

ProxlXMLFileImport.prototype.closeClicked  = function( clickThis, eventObject ) {


	if ( ! this.isFileDataEmpty() ) {
		
		//  Prompt to confirm

		$(".import_proxl_xml_file_confirm_abandon_upload_overlay_show_hide_parts_jq").show();

		return;
	}

	this.closeOverlay( { clickThis : clickThis, eventObject : eventObject } );

};


ProxlXMLFileImport.prototype.closeOverlay  = function( params ) {

	//  Not always provided:  clickThis, eventObject
	
//	var clickThis = params.clickThis;
//	var eventObject = params.eventObject;
	
	
	var allFileData = this.getAllFileData();
	
	var fileDataKeys = Object.keys( allFileData );
	
	for( var index = 0; index < fileDataKeys.length; index++ ) {
		
		var fileDataKey = fileDataKeys[ index ];
		
		var proxlXMLFileImportFileData = allFileData[ fileDataKey ];
		

		this.abortXMLHttpRequestSend( { proxlXMLFileImportFileData : proxlXMLFileImportFileData } );

	}
	
	
	//  TODO  Add code to clear current request and temp subdir on server
	
	//  TODO  Add code to clear variables related to current request
	
	
	
	$(".proxl_xml_file_upload_overlay_show_hide_parts_jq").hide();
	
	this.resetOverlay();
	
	if ( this.uploadKey ) {
		
		this.updateServerAbandonedUploadKey();
	}
};



ProxlXMLFileImport.prototype.resetOverlayProxlXMLSection  = function(  ) {

	var fileIndex = this.getNextFileIndex();

	$("#import_proxl_xml_chosen_proxl_xml_file_block").attr( "data-file_index", fileIndex );

	$("#import_proxl_xml_proxl_xml_file_field").val("");

	$("#import_proxl_xml_choose_proxl_xml_file_block").show();

	$("#import_proxl_xml_chosen_proxl_xml_file_block").hide();
	
	$("#import_proxl_xml_chosen_proxl_xml_file_name").text( "" );
	
	$("#import_proxl_xml_choose_scan_file_block").hide();
};

ProxlXMLFileImport.prototype.resetOverlay  = function(  ) {

	this.clearFileData();
	
	this.resetOverlayProxlXMLSection();
	
	$("#import_proxl_xml_file_search_name").val("");
	
	$("#import_proxl_xml_scan_file_field").val("");

	$("#import_proxl_xml_scan_files_block").empty();

	this.disableSubmitUploadButton();
	
	$("#import_proxl_xml_file_file_sent_confirmation_block").hide();
	$("#import_proxl_xml_file_error_message_block").hide();
	
	$("#import_proxl_xml_file_cancel_failed_message_block").hide();
	$("#import_proxl_xml_file_cancel_message_block").hide();

	
};







//  User chose a file in the Proxl XML file dialog, or it is empty

ProxlXMLFileImport.prototype.proxlXMLFileDialogChanged  = function( changeThis, eventObject ) {


//	var objectThis = this;
	
	
	this.disableSubmitUploadButton();
	
	
	var $fileElement = $( changeThis );  //  $("#import_proxl_xml_proxl_xml_file_field");

	var fileElement = $fileElement[0];
	
	var file = undefined;
	var filename = undefined;
	var fileSize = undefined;
	
	try {

		//  length is zero if no file selected
		if( fileElement.files.length === 0){

//			alert("File is required");
			
			$fileElement.val("");

			return;
		}

		file = fileElement.files[ 0 ];  // get file, will only be one file since not multi-select <file> element
		
		
		$fileElement.val(""); // clear the input field after get the selected file

		filename = file.name;
		
		fileSize = file.size;
		

	} catch (e) { 

		alert( "Javascript File API not supported.  Use a newer browser" );

		return;
	}
	
	

	var maxProxlXMLFileUploadSize = this.maxProxlXMLFileUploadSize;

	if ( fileSize > maxProxlXMLFileUploadSize ) {

		var $import_proxl_xml_file_choose_file_error_message = $("#import_proxl_xml_file_choose_file_error_message");

		$import_proxl_xml_file_choose_file_error_message.empty();
		
		var errorMessage = $("#import_proxl_xml_file_choose_file_error_message_file_too_large").html();

		var $errorMessage = $( errorMessage );

		var $chosen_file_jq = $errorMessage.find(".chosen_file_jq");
		$chosen_file_jq.text( filename );
		
		var $file_limit_jq = $errorMessage.find(".file_limit_jq");
		$file_limit_jq.text( this.maxProxlXMLFileUploadSizeFormatted );

		$import_proxl_xml_file_choose_file_error_message.append( $errorMessage );

		$(".import_proxl_xml_choose_file_error_overlay_show_hide_parts_jq").show();



		return;  //  EARY EXIT

	}
	

	


	var fileTypeString = $fileElement.attr("data-file_type");
	
	if ( fileTypeString === undefined || fileTypeString === null || fileTypeString === "" ) {
		
		throw "fileType is not a value";
	}
	
	var $import_proxl_xml_chosen_proxl_xml_file_block = $("#import_proxl_xml_chosen_proxl_xml_file_block");
	
	var fileIndex = this.getFileIndexAsIntFromDOM( { $domElement : $import_proxl_xml_chosen_proxl_xml_file_block } );

	
	
	var proxlXMLFileImportFileData = ProxlXMLFileImportFileData.factory(
			{ isProxlXMLFile : true, file : file, fileTypeString : fileTypeString,  fileIndex : fileIndex });

	this.addFileData( { fileData : proxlXMLFileImportFileData,  fileIndex : fileIndex } );
	
	
	$("#import_proxl_xml_proxl_xml_file_field").val("");
	
	
	
	

	$("#import_proxl_xml_choose_proxl_xml_file_block").hide();
	
	$("#import_proxl_xml_chosen_proxl_xml_file_block").show();
	
	
	$("#import_proxl_xml_chosen_proxl_xml_file_name").text( filename );
	
	
	//  Start the upload of the selected file

	this.uploadFile( { isProxlXMLFile : true, $containingBlock : $import_proxl_xml_chosen_proxl_xml_file_block } );
	
};



//  User is removing their choice of Proxl XML file

//   TODO  If uploaded, remove from server

ProxlXMLFileImport.prototype.removeProxlXMLFile  = function( params ) {

	var clickThis = params.clickThis; 
	var $import_proxl_xml_file_scan_file_entry_block_jq = params.$import_proxl_xml_file_scan_file_entry_block_jq;
//	var eventObject = params.eventObject;
	var doAbortIfNeeded = params.doAbortIfNeeded;

	this.disableSubmitUploadButton();
	
	$("#import_proxl_xml_choose_scan_file_block").hide(); // Hide since need to upload proxl xml file first

	if ( ! $import_proxl_xml_file_scan_file_entry_block_jq ) {

		if ( ! clickThis ) {
			throw "Either $import_proxl_xml_file_scan_file_entry_block_jq or clickThis must be populated";
		}
		var $clickThis = $( clickThis );

		$import_proxl_xml_file_scan_file_entry_block_jq = $clickThis.closest(".import_proxl_xml_file_scan_file_entry_block_jq");
	}

	var fileIndex = this.getFileIndexAsIntFromDOM( { $domElement : $import_proxl_xml_file_scan_file_entry_block_jq } );

	if ( doAbortIfNeeded ) {
		//	Does abort of send if in progress
		this.abortXMLHttpRequestSend( { fileIndex : fileIndex } );
	}

	this.removeFileData( { fileIndex : fileIndex } );

	this.resetOverlayProxlXMLSection();
};




//  Remove The Scan file, aborting the upload if in progress

ProxlXMLFileImport.prototype.removeScanFile  = function( params ) {

	var clickThis = params.clickThis; 
	var $import_proxl_xml_file_scan_file_entry_block_jq = params.$import_proxl_xml_file_scan_file_entry_block_jq;
//	var eventObject = params.eventObject;
	var doAbortIfNeeded = params.doAbortIfNeeded;
	
	if ( ! $import_proxl_xml_file_scan_file_entry_block_jq ) {

		if ( ! clickThis ) {
			throw "Either $import_proxl_xml_file_scan_file_entry_block_jq or clickThis must be populated";
		}
		var $clickThis = $( clickThis );

		$import_proxl_xml_file_scan_file_entry_block_jq = $clickThis.closest(".import_proxl_xml_file_scan_file_entry_block_jq");
	}

	var fileIndex = this.getFileIndexAsIntFromDOM( { $domElement : $import_proxl_xml_file_scan_file_entry_block_jq } );

	if ( doAbortIfNeeded ) {
		//	Does abort of send if in progress
		this.abortXMLHttpRequestSend( { fileIndex : fileIndex } );
	}

	this.removeFileData( { fileIndex : fileIndex } );

	$import_proxl_xml_file_scan_file_entry_block_jq.remove();

	this.enableDisableSubmitUploadButtonAndAddScanFileLinkConditional();
	
};


///////////////////////////////////////////////

//  Abort the send of the XMLHttpRequest

//		Must do before remove file data

ProxlXMLFileImport.prototype.abortXMLHttpRequestSend  = function( params ) {

	var fileIndex = params.fileIndex;
	var proxlXMLFileImportFileData = params.proxlXMLFileImportFileData; 

	if ( ! fileIndex && ! proxlXMLFileImportFileData ) {

		throw "fileIndex or proxlXMLFileImportFileData is required"; 
	}

	if ( ! proxlXMLFileImportFileData ) {

		proxlXMLFileImportFileData = this.getFileData( { fileIndex : fileIndex } );
	}

	if ( proxlXMLFileImportFileData ) {

		var xmlHttpRequest = proxlXMLFileImportFileData.getXMLHttpRequest();

		if ( xmlHttpRequest ) {

			//  cancel the XMLHttpRequest

			try {

				xmlHttpRequest.abort();

			} catch (e) { 

			}
		}
	}
};






//  User chose a file in the Scan file dialog, or it is empty

ProxlXMLFileImport.prototype.scanFileDialogChanged  = function( changeThis, eventObject ) {
	
	if ( ! this.uploadingScanFiles ) {
		
		throw "scanFileDialogChanged(...): Scan files not allowed, should not enter this";
	}

	var objectThis = this;

	var $fileElement = $( changeThis );  //  $("#import_proxl_xml_proxl_xml_file_field");

	var fileElement = $fileElement[0];
	
	var file = undefined;
	var filename = undefined;
	var fileSize = undefined;
	
	try {

		//  length is zero if no file selected
		if( fileElement.files.length === 0){

//			alert("File is required");
			
			$fileElement.val("");

			return;
		}

		file = fileElement.files[ 0 ];  // get file, will only be one file since not multi-select <file> element
		
		
		$fileElement.val(""); // clear the input field after get the selected file

		filename = file.name;
		
		fileSize = file.size;
		

	} catch (e) { 

		alert( "Javascript File API not supported.  Use a newer browser" );

		return;
	}
	
	
	
	
	
	//  Did the user already select this filename

	var allFileData = this.getAllFileData();
		
	var fileDataKeys = Object.keys( allFileData );
	
	for( var index = 0; index < fileDataKeys.length; index++ ) {
		
		var fileDataKey = fileDataKeys[ index ];
		
		var fileData = allFileData[ fileDataKey ];
		
		if ( fileData.isUploadedToServer() ) {
			
			//  Only add the files that have been uploaded to the server

			var fileObj = fileData.getFile();
			var filenameInFileData = fileObj.name;
			
			if ( filenameInFileData === filename ) {
				
				//  Same filename already uploaded
				
				var $import_proxl_xml_file_choose_file_error_message = $("#import_proxl_xml_file_choose_file_error_message");

				$import_proxl_xml_file_choose_file_error_message.empty();
				
				var errorMessage = $("#import_proxl_xml_file_choose_file_error_message_filename_already_chosen").html();

				var $errorMessage = $( errorMessage );

				var $chosen_file_jq = $errorMessage.find(".chosen_file_jq");

				$chosen_file_jq.text( filename );

				$import_proxl_xml_file_choose_file_error_message.append( $errorMessage );

				$(".import_proxl_xml_choose_file_error_overlay_show_hide_parts_jq").show();



				return;  //  EARY EXIT
			}
		}
	}
	
	



	var fileType = $fileElement.attr("data-file_type");
	
	if ( fileType === undefined || fileType === null || fileType === "" ) {
		
		throw "fileType is not a value";
	}
	
	var maxScanFileUploadSize = this.maxScanFileUploadSize;

	if ( fileSize > maxScanFileUploadSize ) {

		var $import_proxl_xml_file_choose_file_error_message = $("#import_proxl_xml_file_choose_file_error_message");

		$import_proxl_xml_file_choose_file_error_message.empty();
		
		var errorMessage = $("#import_proxl_xml_file_choose_file_error_message_file_too_large").html();

		var $errorMessage = $( errorMessage );

		var $chosen_file_jq = $errorMessage.find(".chosen_file_jq");
		$chosen_file_jq.text( filename );
		
		var $file_limit_jq = $errorMessage.find(".file_limit_jq");
		$file_limit_jq.text( this.maxScanFileUploadSizeFormatted );

		$import_proxl_xml_file_choose_file_error_message.append( $errorMessage );

		$(".import_proxl_xml_choose_file_error_overlay_show_hide_parts_jq").show();



		return;  //  EARY EXIT

	}
	


	this.disableSubmitUploadButton();
	
	$("#import_proxl_xml_choose_scan_file_block").hide();
	
	$fileElement.val(""); // clear the input field after get the selected file(s)
	
	
	
	var fileIndex = this.getNextFileIndex();

	
	if ( ! this.scanFileHandlebarsTemplate ) {
	
		//  Get Handlebars template for creating entry on page for scan file

		
		var $import_proxl_xml_file_scan_file_entry_template = $("#import_proxl_xml_file_scan_file_entry_template");
		
		if ( $import_proxl_xml_file_scan_file_entry_template.length === 0 ) {
			
			throw "id 'import_proxl_xml_file_scan_file_entry_template not on page'";
		}

		var source = $("#import_proxl_xml_file_scan_file_entry_template").html();

		if ( source === undefined ) {
			throw '$("#import_proxl_xml_file_scan_file_entry_template").html() === undefined';
		}
		if ( source === null ) {
			throw '$("#import_proxl_xml_file_scan_file_entry_template").html() === null';
		}
		
		this.scanFileHandlebarsTemplate = Handlebars.compile(source);
	}
	
	var context = { fileIndex : fileIndex, fileType : fileType, fileName : filename };

	var html = this.scanFileHandlebarsTemplate(context);

	var $import_proxl_xml_scan_files_block = $("#import_proxl_xml_scan_files_block");
	
	var $scanFileEntry = $(html).appendTo( $import_proxl_xml_scan_files_block );
	
	var $scan_file_remove_button_jq = $scanFileEntry.find(".scan_file_remove_button_jq");
	
	//  Remove The Scan file, aborting the upload if in progress

	$scan_file_remove_button_jq.click(function( eventObject ) {

		var clickThis = this;

		objectThis.removeScanFile( { clickThis : clickThis, doAbortIfNeeded : true, eventObject : eventObject } );

		return false;
	});
	

	var proxlXMLFileImportFileData = ProxlXMLFileImportFileData.factory(
			{ isProxlXMLFile : false, file : file, fileTypeString : fileType,  fileIndex : fileIndex });

	this.addFileData( { fileData : proxlXMLFileImportFileData,  fileIndex : fileIndex } );
	
	
	//  Start the upload of the selected file

	this.uploadFile( { isProxlXMLFile : false, $containingBlock : $scanFileEntry } );

};


//////////////////////////////////////

ProxlXMLFileImport.prototype.openOverlay  = function( clickThis, eventObject ) {

	var objectThis = this;
	
//	var $clickThis = $(clickThis);
	

	var $project_id = $("#project_id");
	var projectId = $project_id.val();

	var _URL = contextPathJSVar + "/services/proxl_xml_file_import/uploadInit";

	var requestData = { project_id : projectId };

	// var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(data) {

			objectThis.openOverlayProcessServerResponse( { requestData : requestData, responseData : data } );
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



ProxlXMLFileImport.prototype.openOverlayProcessServerResponse  = function( params ) {

//	var requestData = params.requestData; 
	var responseData = params.responseData;
	
	var statusSuccess = responseData.statusSuccess;
	var projectLocked = responseData.projectLocked;

	//
	
	var uploadKey = responseData.uploadKey;


	if ( ! statusSuccess ) {
		
		if ( projectLocked ) {

			//  Project is now locked so reload page so not display option to upload files for import
			
			//  reload current URL
			
			window.location.reload(true);
		}
		
		//  Probably shouldn't get here
		
		throw "statusSuccess is false";  ///  TODO  Need to display error
	}
	
	//  Save upload key
	this.uploadKey = uploadKey;
	
	this.resetOverlay();
	
	var $overlay_background = $("#proxl_xml_file_upload_modal_dialog_overlay_background"); 
	var $overlay_container = $("#proxl_xml_file_upload_overlay_container_div");
	
	//  Position Overlay Vertically

	var $window = $( window );
	
	var windowScrollTop = $window.scrollTop();

	var overlayNewTop = windowScrollTop + 30;

	//  Apply position to overlay
	
	$overlay_container.css( { top : overlayNewTop + "px" } );
	
	
	$overlay_background.show();
	$overlay_container.show();
};



ProxlXMLFileImport.prototype.uploadFile  = function ( params ) {

	var objectThis = this;

	var isProxlXMLFile = params.isProxlXMLFile;
	var $containingBlock = params.$containingBlock;



	var formData;

	var filename;
	
	var proxlXMLFileImportFileData;
	
	var fileIndex = this.getFileIndexAsIntFromDOM( { $domElement : $containingBlock } );

	
	var fileTypeString = $containingBlock.attr("data-file_type");
	
	if ( fileTypeString === undefined ) {
		
		throw 'undefined:  $containingBlock.attr("data-file_type");';
	}
	if ( fileTypeString === "" ) {

		throw 'empty string:  $containingBlock.attr("data-file_type");';
	}
	
	var fileType = parseInt( fileTypeString, 10 );
	
	if ( isNaN( fileType ) ) {
		
		throw 'Fail parse to int:  $containingBlock.attr("data-file_type");';
	}
	
	var $project_id = $("#project_id");
	var projectId = $project_id.val();
	
	var uploadKey = this.uploadKey;
	
	if ( ! uploadKey ) {
		
		//  TODO  show error to user?
		
		throw "upload key cannot be not set";
	}
	
	try {

		//  fileIndex is an int
		

		proxlXMLFileImportFileData = this.getFileData( { fileIndex : fileIndex } );
		
		if ( fileType !== proxlXMLFileImportFileData.getFileType() ) {
			
			throw "file type for saved fileData does not match file type in DOM element";
		}
		
		var file = proxlXMLFileImportFileData.getFile();

		filename = file.name;

		formData = new FormData();
		formData.append( this.CONSTANTS.UPLOAD_FILE_FORM_NAME, file, file.name );

//		formData.append( 'fastaDescription', fastaDescription );  // sent as part of the multipart form



	} catch (e) { 

		alert( "Javascript File API not supported.  Please use a newer browser" );

		return;
	}


	this.progressBarClear( { $containingBlock : $containingBlock } );

	var xmlHttpRequest = new XMLHttpRequest();
	
	proxlXMLFileImportFileData.setXMLHttpRequest( xmlHttpRequest );


	xmlHttpRequest.onload = function() {
		
		var currentXHRinOnLoad = xmlHttpRequest;
		
		proxlXMLFileImportFileData.setXMLHttpRequest( undefined ); ///  clear reference to XMLHttpRequest


		$("#import_proxl_xml_file_close_button").show();
		$("#import_proxl_xml_file_cancel_button").hide();
		

		var xhrStatus = currentXHRinOnLoad.status;

		
		var xhrResponse = currentXHRinOnLoad.response;
		
		var xhrResponseText = currentXHRinOnLoad.responseText;
		
		if (xhrStatus === 200) {

			var resp = null;

			try {
				resp = JSON.parse(xhrResponse);

			} catch (e) {
//				resp = {
//						statusSuccess: false,
//						data: 'Unknown error occurred: [' + xhrResponseText + ']'
//				};

				var errorMessage = "File Uploaded but failed to get information from server response.";

				objectThis.failedFileUpload( 
						{ isProxlXMLFile : isProxlXMLFile,
							errorMessage :  errorMessage,
							filename : filename,
							$containingBlock : $containingBlock } );
			}
				

			if ( resp !== null ) {
				
				if ( resp.statusSuccess ) {
					
					objectThis.successfulFileUpload( 
							{
								isProxlXMLFile : isProxlXMLFile,
								fileUploadResponse : resp,
								proxlXMLFileImportFileData : proxlXMLFileImportFileData,
								$containingBlock : $containingBlock } );
					
				} else {

					var errorMessage = "File NOT Uploaded, service returned failed status";

					objectThis.failedFileUpload( 
							{ isProxlXMLFile : isProxlXMLFile,
								errorMessage :  errorMessage,
								filename : filename,
								$containingBlock : $containingBlock } );
				}
			}


		} else if (xhrStatus === 400) {

			var resp = null;

			try {
				resp = JSON.parse(xhrResponse);


			} catch (e) {

				var errorMessage = 'Unknown error occurred: [' + xhrResponseText + ']';

				objectThis.failedFileUpload( 
						{ isProxlXMLFile : isProxlXMLFile,
							errorMessage :  errorMessage,
							filename : filename,
							$containingBlock : $containingBlock } );
			}
			
			if ( resp !== null ) {

				if ( resp.fileSizeLimitExceeded ) {
					
					var errorMessage = "File NOT Uploaded, file too large.  Max file size in bytes: " + resp.maxSizeFormatted;

					objectThis.failedFileUpload( 
							{ isProxlXMLFile : isProxlXMLFile,
								errorMessage :  errorMessage,
								filename : filename,
								$containingBlock : $containingBlock } );

				} else if ( resp.ProjectLocked ) {
					
					var errorMessage = "The project is locked so no imports are allowed.  Please reload the web page.";

					objectThis.failedFileUpload( 
							{ isProxlXMLFile : isProxlXMLFile,
								errorMessage :  errorMessage,
								filename : filename,
								$containingBlock : $containingBlock } );
					
				} else if ( resp.filenameInFormNotMatchFilenameInQueryString ) {

					var errorMessage = "System Error";

					objectThis.failedFileUpload( 
							{ isProxlXMLFile : isProxlXMLFile,
								errorMessage :  errorMessage,
								filename : filename,
								$containingBlock : $containingBlock } );
					
				} else if ( resp.noUploadedFile ) {

					var errorMessage = "System Error";

					objectThis.failedFileUpload( 
							{ isProxlXMLFile : isProxlXMLFile,
								errorMessage :  errorMessage,
								filename : filename,
								$containingBlock : $containingBlock } );
					
				} else if ( resp.proxlXMLFileFailsInitialParse ) {

					var errorMessage = "The server failed to parse the Proxl XML file.  Please confirm that it is a valid Proxl XML file.";

					objectThis.failedFileUpload( 
							{ isProxlXMLFile : isProxlXMLFile,
								errorMessage :  errorMessage,
								filename : filename,
								$containingBlock : $containingBlock } );
					
				} else if ( resp.proxlXMLFilerootXMLNodeIncorrect ) {

					var errorMessage = "The server failed to parse the Proxl XML file.  Please confirm that it is a valid Proxl XML file.";

					objectThis.failedFileUpload( 
							{ isProxlXMLFile : isProxlXMLFile,
								errorMessage :  errorMessage,
								filename : filename,
								$containingBlock : $containingBlock } );
					

				} else if ( resp.scanFileNotAllowed ) {

					var errorMessage = "Scan files are no longer allowed.  Please refresh the page.";

					objectThis.failedFileUpload( 
							{ isProxlXMLFile : isProxlXMLFile,
								errorMessage :  errorMessage,
								filename : filename,
								$containingBlock : $containingBlock } );
					
				} else {

					var errorMessage = "File NOT Uploaded, input data error, status 400";

					objectThis.failedFileUpload( 
							{ isProxlXMLFile : isProxlXMLFile,
								errorMessage :  errorMessage,
								filename : filename,
								$containingBlock : $containingBlock } );

				}
			}

			objectThis.progressBarClear( { $containingBlock : $containingBlock } );


		} else if (xhrStatus === 401 || xhrStatus === 403) {

			//  No Session or not Authorized

			var handledResponse = handleRawAJAXError( currentXHRinOnLoad );
			
			if ( handledResponse ) {
				
				return;
			}

			objectThis.progressBarClear( { $containingBlock : $containingBlock } );

			if (xhrStatus === 401 ) {

				var errorMessage = "File NOT Uploaded, server error, status 401";

				objectThis.failedFileUpload( 
						{ isProxlXMLFile : isProxlXMLFile,
							errorMessage :  errorMessage,
							filename : filename,
							$containingBlock : $containingBlock } );

			} else {
				
				var errorMessage = "File NOT Uploaded, server error, status 403";

				objectThis.failedFileUpload( 
						{ isProxlXMLFile : isProxlXMLFile,
							errorMessage :  errorMessage,
							filename : filename,
							$containingBlock : $containingBlock } );
			}

			

		} else if (xhrStatus === 500) {


//			var resp = null;
//
//			try {
//				resp = JSON.parse(xhrResponse);
//			} catch (e){
//				resp = {
//						statusSuccess: false,
//						data: 'Unknown error occurred: [' + xhrResponseText + ']'
//				};
//			}
			var errorMessage = "File NOT Uploaded, server error, status 500";

			objectThis.failedFileUpload( 
					{ isProxlXMLFile : isProxlXMLFile,
						errorMessage :  errorMessage,
						filename : filename,
						$containingBlock : $containingBlock } );

		} else if (xhrStatus === 404) {


//			var resp = null;
//
//			try {
//				resp = JSON.parse(xhrResponse);
//			} catch (e){
//				resp = {
//						statusSuccess: false,
//						data: 'Unknown error occurred: [' + xhrResponseText + ']'
//				};
//			}
			
			var errorMessage = "File NOT Uploaded, Service not found on server. status 404";

			objectThis.failedFileUpload( 
					{ isProxlXMLFile : isProxlXMLFile,
						errorMessage :  errorMessage,
						filename : filename,
						$containingBlock : $containingBlock } );

		} else {
			
			var errorMessage = "File upload failed. xhrStatus: " + xhrStatus;

			objectThis.failedFileUpload( 
					{ isProxlXMLFile : isProxlXMLFile,
						errorMessage :  errorMessage,
						filename : filename,
						$containingBlock : $containingBlock } );

		}
	};

	xmlHttpRequest.upload.addEventListener('error', function(event){

		
		proxlXMLFileImportFileData.setXMLHttpRequest( undefined ); ///  clear reference to XMLHttpRequest

//		var currentXHRinOnLoad = xmlHttpRequest;

		var errorMessage = "File NOT Uploaded.  Error connecting to server";

		objectThis.failedFileUpload( 
				{ isProxlXMLFile : isProxlXMLFile,
					errorMessage :  errorMessage,
					filename : filename,
					$containingBlock : $containingBlock } );
		
	}, false);


	xmlHttpRequest.upload.addEventListener('abort', function(event){

//		var currentXHRinOnLoad = xmlHttpRequest;

		//  This is called when the "Abort" is called on the xmlHttpRequest object

		proxlXMLFileImportFileData.setXMLHttpRequest( undefined ); ///  clear reference to XMLHttpRequest

//		alert("Upload aborted");
		
		
	}, false);


	xmlHttpRequest.upload.addEventListener('progress', function(event){

		var progressPercent = Math.ceil( ( event.loaded / event.total) * 100 );

		objectThis.progressBarUpdate( { progressPercent : progressPercent, $containingBlock : params.$containingBlock }  );

	}, false);

	//  parameters added to the query string are available when the request is first received at the server.


	var filenameURIEncoded = encodeURIComponent( filename );

	var postURL = contextPathJSVar + "/uploadFileForImport?"
	+ "upload_key=" +  uploadKey
	+ "&project_id=" + projectId
	+ "&file_index=" + fileIndex
	+ "&file_type=" + fileType
	+ "&filename=" + filenameURIEncoded;



	xmlHttpRequest.open('POST', postURL);
	xmlHttpRequest.send(formData);

};



ProxlXMLFileImport.prototype.successfulFileUpload = function( params ) {

//	var fileUploadResponse = params.fileUploadResponse;
	
	var isProxlXMLFile = params.isProxlXMLFile;
	var proxlXMLFileImportFileData = params.proxlXMLFileImportFileData;
	
	var $containingBlock = params.$containingBlock;

	//	var eventObject = params.eventObject;
	
	var $progress_bar_container_jq = $containingBlock.find(".progress_bar_container_jq");
	
	$progress_bar_container_jq.hide();
	
	var $upload_complete_msg_jq = $containingBlock.find(".upload_complete_msg_jq");

	$upload_complete_msg_jq.show();

	proxlXMLFileImportFileData.setUploadedToServer( true );


	if ( isProxlXMLFile ) {
		
		
	}
	
	$("#import_proxl_xml_choose_scan_file_block").show();
	
//	this.enableSubmitUploadButton();
	
	this.enableDisableSubmitUploadButtonAndAddScanFileLinkConditional();
};

/////

ProxlXMLFileImport.prototype.failedFileUpload = function( params ) {

	var isProxlXMLFile = params.isProxlXMLFile;
	var errorMessage = params.errorMessage;
	
	var $containingBlock = params.$containingBlock;
	
	if ( isProxlXMLFile ) {
		
		this.removeProxlXMLFile( { $import_proxl_xml_file_scan_file_entry_block_jq : $containingBlock } );

	} else {
		
		this.removeScanFile( { $import_proxl_xml_file_scan_file_entry_block_jq : $containingBlock } );
	}
	
	var filename = params.filename;


	$("#import_proxl_xml_file_error_message_filename").text( filename );
	
	$("#import_proxl_xml_file_file_error_message").text( errorMessage );
	
	$(".import_proxl_xml_file_upload_error_overlay_show_hide_parts_jq").show();

};


ProxlXMLFileImport.prototype.progressBarClear = function( params ) {

	var $containingBlock = params.$containingBlock;
	
	var $upload_complete_msg_jq = $containingBlock.find(".upload_complete_msg_jq");

	$upload_complete_msg_jq.hide();

	var $progress_bar_container_jq = $containingBlock.find(".progress_bar_container_jq");
	
	$progress_bar_container_jq.show();
	
	this.progressBarUpdate( { progressPercent : 0, $containingBlock : $containingBlock } );
};


ProxlXMLFileImport.prototype.progressBarUpdate = function( params ) { // progressPercent as integer 0 to 100

	var $containingBlock = params.$containingBlock;

	var progressPercent = params.progressPercent;

	var $progress_bar_jq = $containingBlock.find(".progress_bar_jq");

	var progressBar = $progress_bar_jq[0];

	var progressPercentText = progressPercent + '%';
	
	progressBar.style.width = progressPercentText;
	
	var $progress_bar_text_jq = $containingBlock.find(".progress_bar_text_jq");
	
	$progress_bar_text_jq.text( progressPercentText );
};




//  User clicked "submit"

ProxlXMLFileImport.prototype.submitClicked  = function( clickThis, eventObject ) {
	
	var objectThis = this;
	
//	var $clickThis = $(clickThis);
	

	var $project_id = $("#project_id");
	var projectId = $project_id.val();
	
	var $import_proxl_xml_file_search_name = $("#import_proxl_xml_file_search_name");
	
	var searchName = $import_proxl_xml_file_search_name.val();
	
	var uploadKey = this.uploadKey;
	
	if ( ! uploadKey ) {
		
		throw "uploadKey must be set";
	}
	
	
	var foundProxlXMLFile = false;
	
	var allFileData = this.getAllFileData();
	
	var fileItems = [];
	
	var fileDataKeys = Object.keys( allFileData );
	
	for( var index = 0; index < fileDataKeys.length; index++ ) {
		
		var fileDataKey = fileDataKeys[ index ];
		
		var fileData = allFileData[ fileDataKey ];
		
		if ( fileData.isUploadedToServer() ) {
			
			//  Only add the files that have been uploaded to the server

			var fileObj = fileData.getFile();
			var filename = fileObj.name;

			var fileType = fileData.getFileType(); 
			var isProxlXMLFile = fileData.isIsProxlXMLFile();
			
			if ( isProxlXMLFile ) {
				if ( foundProxlXMLFile ) {
					
					throw "Found more than one Proxl XML file in submit: second filename: " + filename;
				}
				foundProxlXMLFile = true;
			}
			
			//  All properties put in fileItem must be accepted by the web service

			var fileItem = {
					isProxlXMLFile : isProxlXMLFile,
					uploadedFilename : filename,
					fileType : fileType,
					fileIndex : fileData.getFileIndex()
			};

			fileItems.push( fileItem );
		}
	}
	
	if ( ! foundProxlXMLFile ) {
		
		throw "No Proxl XML file in submit";
	}
	

	var _URL = contextPathJSVar + "/services/proxl_xml_file_import/uploadSubmit";

	//  All properties put in requestObj must be accepted by the web service
	
	var requestObj = { 
			projectId : projectId, 
			uploadKey : uploadKey,
			searchName : searchName,
			fileItems : fileItems };
	
	var requestData = JSON.stringify( requestObj );


	// var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : requestData,
		contentType: "application/json; charset=utf-8",
		dataType : "json",  //  data type returned
		success : function(data) {

			objectThis.submitClickedProcessServerResponse( { requestObj : requestObj, responseData : data } );
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



ProxlXMLFileImport.prototype.submitClickedProcessServerResponse  = function( params ) {

	var requestObj = params.requestObj; 
	var responseData = params.responseData;
	
	var statusSuccess = responseData.statusSuccess;
	var projectLocked = responseData.projectLocked;
	var scanFileNotAllowed = responseData.scanFileNotAllowed;
	//
	
	if ( ! statusSuccess ) {
		
		if ( projectLocked ) {

			//  Project is now locked so reload page so not display option to upload files for import
			
			//  reload current URL
			
			window.location.reload(true);
			
			return;
		}
		
		if ( scanFileNotAllowed ) {

			//  Scan files are no longer allowed.  reload the page to reflect that.

			//  reload current URL
			
			window.location.reload(true);
			
			return;
		}
		
		//  Probably shouldn't get here

		window.location.reload(true);
		
		return;
		
		throw "statusSuccess is false";  ///  TODO  Need to display error
	}
	
	this.uploadKey = undefined;
	
	this.clearFileData();
	
	proxlXMLFileImportStatusDisplay.populateDataBlockAndPendingCount();
	
	this.closeOverlay();
};



ProxlXMLFileImport.prototype.enableDisableSubmitUploadButtonAndAddScanFileLinkConditional = function () {

	//  Enable Submit Upload button and show Add Scan Files Link
	//  if Proxl XML file is uploaded and all scan files are uploaded.

	var proxlXMLfileUploaded = false;
	var allScanFilesUploadedOrNoScanFiles = true;
	
	var allFileData = this.getAllFileData();
	
	var fileDataKeys = Object.keys( allFileData );
	
	for( var index = 0; index < fileDataKeys.length; index++ ) {
		
		var fileDataKey = fileDataKeys[ index ];
		
		var proxlXMLFileImportFileData = allFileData[ fileDataKey ];
		
		if ( proxlXMLFileImportFileData.isIsProxlXMLFile() ) {
			
			if ( proxlXMLFileImportFileData.isUploadedToServer() ) {
				
				proxlXMLfileUploaded = true;
			}
			
		} else {
			
			if ( proxlXMLFileImportFileData.isUploadedToServer() ) {
				
			} else {
				
				allScanFilesUploadedOrNoScanFiles = false;
			}
			
		}
	}
	
	if ( proxlXMLfileUploaded && allScanFilesUploadedOrNoScanFiles ) {
		
		this.enableSubmitUploadButton();
		
		$("#import_proxl_xml_choose_scan_file_block").show();
	
	} else {
		
		this.disableSubmitUploadButton();
		
		$("#import_proxl_xml_choose_scan_file_block").hide();
	}
	
};


ProxlXMLFileImport.prototype.enableSubmitUploadButton  = function () {

	$("#import_proxl_xml_file_submit_button").prop( "disabled", false );
	$("#import_proxl_xml_file_submit_button_disabled_overlay").hide();
};

ProxlXMLFileImport.prototype.disableSubmitUploadButton  = function () {

	$("#import_proxl_xml_file_submit_button_disabled_overlay").show();
	$("#import_proxl_xml_file_submit_button").prop( "disabled", true );
};


////////////////////

//  User abandoned upload so inform server so it can be removed

ProxlXMLFileImport.prototype.updateServerAbandonedUploadKey  = function() {

//	var objectThis = this;


	if ( ! this.uploadKey ) {

		return;
	}
	
	var uploadKey = this.uploadKey;

	var $project_id = $("#project_id");
	var projectId = $project_id.val();

	var _URL = contextPathJSVar + "/services/proxl_xml_file_import/removeAbandonedUploadKey";

//	All properties put in requestObj must be accepted by the web service

	var requestObj = { projectId : projectId, uploadKey : uploadKey };

	var requestData = JSON.stringify( requestObj );


//	var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : requestData,
		contentType: "application/json; charset=utf-8",
		dataType : "json",  //  data type returned
		success : function(data) {

			
		},
		failure: function(errMsg) {
//			handleAJAXFailure( errMsg );
		},
		error : function(jqXHR, textStatus, errorThrown) {

//			handleAJAXError(jqXHR, textStatus, errorThrown);

			// alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
			// textStatus: " + textStatus );
		}
	});
};






///////////////

//  Instance of object on page

var proxlXMLFileImport = new ProxlXMLFileImport();
