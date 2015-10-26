
/*
 * Takes a filename, mimetype, and string content and initiates a file download
 * of the content from the current page, without leaving the page. 
 * 
 * It is assumed jquery is loaded.
 * 
 */
var downloadStringAsFile = function( filename, mimetype, content ) {
	
	var form = document.createElement( "form" );
	
	$( form ).hide();
	
    form.setAttribute( "method", "post" );
    form.setAttribute( "action", contextPathJSVar + "/downloadStringAsFile.do" );
    form.setAttribute( "target", "_blank" );

    var filenameField = document.createElement( "input" );
    filenameField.setAttribute("name", "filename");
    filenameField.setAttribute("value", filename);

    var mimetypeField = document.createElement( "input" );
    mimetypeField.setAttribute("name", "mimetype");
    mimetypeField.setAttribute("value", mimetype);

    var contentField = document.createElement( "textarea" );
    contentField.setAttribute("name", "content");
    
    $( contentField ).text( content );

    form.appendChild( filenameField );
    form.appendChild( mimetypeField );
    form.appendChild( contentField );
    
    document.body.appendChild(form);    // Not entirely sure if this is necessary			

    form.submit();
	
    document.body.removeChild( form );
};
