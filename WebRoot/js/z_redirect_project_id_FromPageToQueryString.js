
//  z_redirect_project_id_FromPageToQueryString.js


//  Javascript  to take in the project_id in the hidden field on the page and create a new URL with the project_id in the query string 
//    and redirect the browser to that page
//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";

function createNewURL() {

	var windowHash = window.location.hash;

	if ( windowHash === "" || windowHash === "#" ) {

		return null;
	}
	
	var queryString = window.location.search; // start with existing query string
	
	  
	var project_idElement = document.getElementById( "project_id" );

	var project_id = project_idElement.value;
	
	var queryStringProjectId = "&project_id=" + project_id;
	
	var newURLlocal = window.location.pathname + queryString + queryStringProjectId + windowHash;

	return newURLlocal;
}

var newURL =  createNewURL();

if ( newURL != null ) {
	

	window.location.href = newURL;
	
} else {
	
	window.location.href = contextPathJSVar + "/invalidRequestData.do";
}