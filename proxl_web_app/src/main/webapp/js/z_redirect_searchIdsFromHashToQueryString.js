
//  z_redirect_searchIdsFromHashToQueryString.js


//  Javascript  to take in the old searchIds in the hash and create a new URL with the searchIds in the query string 
//    and redirect the browser to that page
//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";

function createNewURL() {

	var windowHash = window.location.hash;

	if ( windowHash === "" || windowHash === "#" ) {

		return null;
	}
	
	var hashData = null;

	try {

		// if this works, the hash contains native (non encoded) JSON
		hashData = JSON.parse( window.location.hash.slice( 1 ) );
	} catch( e ) {

		// if we got here, the hash contained URI-encoded JSON
		hashData = JSON.parse( decodeURI( window.location.hash.slice( 1 ) ) );
	}


	var resultObject = {};
	
	var searchIds = null;
	

	var hashDataKeys = Object.keys( hashData );

	for ( var hashDataKeysIndex = 0; hashDataKeysIndex < hashDataKeys.length; hashDataKeysIndex++ ) {

		var propertyName = hashDataKeys[ hashDataKeysIndex ];

		if ( propertyName === "searches" ) {
			
			searchIds = hashData[ propertyName ];
			
		} else {
			
			resultObject[ propertyName ] = hashData[ propertyName ];
		}
		
	}
	
	if ( searchIds === undefined || searchIds === null ) {
		
		return null;
	}

	
	var URLHash = encodeURI( JSON.stringify( resultObject ) );

	var queryString = window.location.search; // start with existing query string
	
	//  Add search ids from JSON into query string
	
	for ( var i = 0; i < searchIds.length; i++ ) {
		queryString += "&searchIds=" + searchIds[ i ];
	}
	
	var newURLlocal = window.location.pathname + queryString + "#" + URLHash;

	return newURLlocal;
}

var newURL =  createNewURL();

if ( newURL != null ) {
	

	window.location.href = newURL;
	
} else {
	
	window.location.href = "invalidRequestData.do";
}