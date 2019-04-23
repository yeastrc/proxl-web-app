
/*
 * Takes a filename, mimetype, and string content and initiates a file download
 * of the content from the current page, without leaving the page.
 */

import { saveAs } from 'file-saver';

var downloadStringAsFile = function( filename, mimetype, content ) {

	try {

		console.log( "downloadStringAsFile_OnlyJS called." );

		var blob = new Blob([content], {type: "text/plain;charset=ISO-8859-1"});

		saveAs(blob, filename);


	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
};


export { downloadStringAsFile }
