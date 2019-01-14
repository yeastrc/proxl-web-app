

//  z_redirect_pre_generic_image_structure_ToGenericURL.js

//   Convert the hash code value to generic


////////s SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS



//  Javascript  to take in the pre-generic URL and convert the psm and peptide cutoffs to the new format

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";

function createNewURL() {
	
	var redirectURLElement = document.getElementById( "redirectURL" );
	
	var redirectURLValue = redirectURLElement.value;
	
	
	var cutoffValuesRootLevelJSONRootElement = document.getElementById( "cutoffValuesRootLevelJSONRootString" );
	
	var cutoffValuesRootLevelJSONRootStringValue = cutoffValuesRootLevelJSONRootElement.value;

	var cutoffValuesRootLevelJSONRootValue = null;

	try {

		cutoffValuesRootLevelJSONRootValue = JSON.parse( cutoffValuesRootLevelJSONRootStringValue );
	} catch( e ) {

		// 
		throw "cutoffValuesRootLevelJSONRootStringValue failed to parse" ;
	}

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


	var psmQValueCutoff = hashData.psmQValueCutoff;
	var peptideQValueCutoff = hashData.peptideQValueCutoff;
	
	if ( psmQValueCutoff === undefined || psmQValueCutoff === null
			|| peptideQValueCutoff === undefined || peptideQValueCutoff === null ) {
		
		
		
	} else {
		
		delete hashData.psmQValueCutoff;
		delete hashData.peptideQValueCutoff;
		
		var cutoffs = cutoffValuesRootLevelJSONRootValue;
		
		hashData.cutoffs = cutoffs;
		
		
		var cutoffSearches = cutoffs.searches;

		var cutoffSearchesKeys = Object.keys( cutoffSearches );

		for ( var cutoffSearchesKeysIndex = 0; cutoffSearchesKeysIndex < cutoffSearchesKeys.length; cutoffSearchesKeysIndex++ ) {

			var propertyNameCutoffs = cutoffSearchesKeys[ cutoffSearchesKeysIndex ];

			var cutoffsForSearch = cutoffSearches[ propertyNameCutoffs ];
			
			var cutoffsForPsm = cutoffsForSearch.psmCutoffValues;

			var cutoffsForPsmKeys = Object.keys( cutoffsForPsm );

			for ( var cutoffsForPsmKeysIndex = 0; cutoffsForPsmKeysIndex < cutoffsForPsmKeys.length; cutoffsForPsmKeysIndex++ ) {

				var propertyNamePsm = cutoffsForPsmKeys[ cutoffsForPsmKeysIndex ];

				var cutoffItemForPsm = cutoffsForPsm[ propertyNamePsm ];
					
				cutoffItemForPsm.value = psmQValueCutoff;
			}
			

			var cutoffsForPeptide = cutoffsForSearch.peptideCutoffValues;

			var cutoffsForPeptideKeys = Object.keys( cutoffsForPeptide );

			for ( var cutoffsForPeptideKeysIndex = 0; cutoffsForPeptideKeysIndex < cutoffsForPeptideKeys.length; cutoffsForPeptideKeysIndex++ ) {

				var propertyNamePeptide = cutoffsForPeptideKeys[ cutoffsForPeptideKeysIndex ];

				var cutoffItemForPeptide = cutoffsForPeptide[ propertyNamePeptide ];
					
				cutoffItemForPeptide.value = peptideQValueCutoff;
			}
		}
		
		
		
	}
	
	

//	cutoffValuesRootLevelJSONRootValue
//	"{"searches":{"14":{"searchId":14,"psmCutoffValues":{"214":{"id":214,"value":null}},"peptideCutoffValues":{"209":{"id":209,"value":null}}}}}"
	
	
	
	
	var URLHash = encodeURI( JSON.stringify( hashData ) );
	
	
	
	var newURLlocal = redirectURLValue + "#" + URLHash;

	return newURLlocal;
}

var newURL =  createNewURL();

if ( newURL != null ) {
	

	window.location.href = newURL;
	
} else {
	
	window.location.href = "invalidRequestData.do";
}