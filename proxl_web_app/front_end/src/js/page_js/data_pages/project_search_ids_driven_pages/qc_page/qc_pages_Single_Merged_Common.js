
/*
 * qc_pages_Single_Merged_Common.js
 * 
 * Common code for QC pages for Single Search and Merged Searches
 * 
 * viewQC.jsp and viewQCMerged.jsp
 * 
 * page variable qc_pages_Single_Merged_Common
 * 
 */

const INCLUDE_PROTEINS_CONVERSION_TO_FROM_STRING_CONSTANTS__TO_STRING_RADIX = 32;
const INCLUDE_PROTEINS_CONVERSION_TO_FROM_STRING_CONSTANTS__SEPARATOR = "Z"

var QC_Pages_Single_Merged_Common = function() {
	

	/*
	 * @return boolean - true if all or no include proteins are selected
	 */
	this.areAllOrNoneIncludeProteinsSelected = function( ) {

		const $includeProtein = $("#includeProtein"); // <select> DOM element
		
		const selectedValuesArray = $includeProtein.val();

		if ( ( ! selectedValuesArray ) || selectedValuesArray.length === 0 ) {
			return { noneSelected : true }; // No proteins are selected
		}

		let areAllOrNoneIncludeProteinsSelected = true;

		const selectedValuesSet = new Set( selectedValuesArray );

		//  Check that all <option> values in selector are in selectedValues
		const $optionAll = $includeProtein.find("option");
		$optionAll.each( ( index, element ) => {
			const $element = $( element );
			const optionValue = $element.attr("value");
			if ( ! selectedValuesSet.has( optionValue ) ) {
				areAllOrNoneIncludeProteinsSelected = false;
				return false; // exit loop
			}
		});

		return { allSelected : areAllOrNoneIncludeProteinsSelected };
	}

	/*
	 * @param outputIncludeProteinsAsStrings - Array of protein ids as strings
	 * @return String - encoded protein ids
	 */
	this.getEncodedIncludeProteins = function( outputIncludeProteinsAsStrings ) {

		const TO_STRING_RADIX = INCLUDE_PROTEINS_CONVERSION_TO_FROM_STRING_CONSTANTS__TO_STRING_RADIX;
		const SEPARATOR = INCLUDE_PROTEINS_CONVERSION_TO_FROM_STRING_CONSTANTS__SEPARATOR;
		
		var encodedIncludeProteins = "";
		var outputIncludeProteinsAsInts = null;
		if ( outputIncludeProteinsAsStrings !== undefined && outputIncludeProteinsAsStrings != null ) {
			outputIncludeProteinsAsInts = [];
			for ( var outputIncludeProteinsAsStringsIndex = 0; outputIncludeProteinsAsStringsIndex < outputIncludeProteinsAsStrings.length; outputIncludeProteinsAsStringsIndex++ ) {
				var outputIncludeProteinString = outputIncludeProteinsAsStrings[ outputIncludeProteinsAsStringsIndex ];
				var outputIncludeProteinInt = parseInt( outputIncludeProteinString, 10 );
				if ( isNaN( outputIncludeProteinInt ) ) {
					throw Error( "outputIncludeProtein cannot be parsed to int.  value: " + outputIncludeProteinString );
				}
				outputIncludeProteinsAsInts.push( outputIncludeProteinInt );
			}
		}
		if ( outputIncludeProteinsAsInts && outputIncludeProteinsAsInts.length > 0 ) {
			if ( outputIncludeProteinsAsInts.length === 1 ) {
				encodedIncludeProteins = outputIncludeProteinsAsInts[ 0 ].toString( TO_STRING_RADIX );
			} else {
				//  sort numerically
				outputIncludeProteinsAsInts.sort(function(a, b) {
					return a - b;
				});
				var firstValue = outputIncludeProteinsAsInts[ 0 ].toString( TO_STRING_RADIX ); 
				var outputAsOffsets = [ firstValue ];
				//  start at second entry in array
				for ( var index = 1; index < outputIncludeProteinsAsInts.length; index++ ) {
					var offsetFromPrevValueInt = outputIncludeProteinsAsInts[ index ] - outputIncludeProteinsAsInts[ index - 1 ];
					var offsetFromPrevValue = ( offsetFromPrevValueInt ).toString( TO_STRING_RADIX );
					outputAsOffsets.push( offsetFromPrevValue );
				}
				encodedIncludeProteins = outputAsOffsets.join( SEPARATOR );  // SEPARATOR separator
			}
		}
		return {
			outputIncludeProteinsAsInts : outputIncludeProteinsAsInts,
			encodedIncludeProteins : encodedIncludeProteins };
	};
	
	/*
	 * Reverse of this.getEncodedIncludeProteins(...)

	 * @param includeProteinsEncodedInSingleString - encoded protein ids
	 * @return - Set<protein ids as numbers> or null
	 * 
	 */
	this.getDecodedIncludeProteins = function( includeProteinsEncodedInSingleString ) {

		const TO_STRING_RADIX = INCLUDE_PROTEINS_CONVERSION_TO_FROM_STRING_CONSTANTS__TO_STRING_RADIX;
		const SEPARATOR = INCLUDE_PROTEINS_CONVERSION_TO_FROM_STRING_CONSTANTS__SEPARATOR;

		if ( ! includeProteinsEncodedInSingleString || includeProteinsEncodedInSingleString === "" ) {
			return null;
		}

		const includeproteinSequenceVersionIdsSet = new Set();
		
		const includeProteinsEncodedInSingleString_Split = includeProteinsEncodedInSingleString.split( SEPARATOR );
		let proteinSequenceVersionIdsEncodedEntryActualPrev = 0;
		for ( const includeProteinEncoded_SingleEntry of includeProteinsEncodedInSingleString_Split ) {
			const includeProteinEncoded_SingleEntryInt = Number.parseInt( includeProteinEncoded_SingleEntry, TO_STRING_RADIX );
			if ( Number.isNaN( includeProteinEncoded_SingleEntryInt ) ) {
				throw Error("getEncodedIncludeProteins(...): Failed to parse includeProteinEncoded_SingleEntry: " + includeProteinEncoded_SingleEntry );
			}
			//  The actual protein sequence id is the encoded id + the prev encoded id.
			const proteinSequenceVersionIdsEncodedEntryActual = includeProteinEncoded_SingleEntryInt + proteinSequenceVersionIdsEncodedEntryActualPrev;
			includeproteinSequenceVersionIdsSet.add( proteinSequenceVersionIdsEncodedEntryActual );
			proteinSequenceVersionIdsEncodedEntryActualPrev = proteinSequenceVersionIdsEncodedEntryActual;
		}
		return includeproteinSequenceVersionIdsSet;
	};

	/////////////////////////////////////////////

	//   Prep for webservice and download calls


	/*
	 * Creating JS object for webservice and download calls, when using full hash_json_Contents and project search ids
	 * 
	 */
	this.createJSObjectForWebserviceRequestAndDownload_From_psids_hash_json_Contents = function({ project_search_ids, hash_json_Contents }) {


	}

	/////////////////////////////////////////////

	//   download calls
	
	/*
	 * Takes a filename, mimetype, and string content and initiates a file download
	 * of the content from the current page, without leaving the page. 
	 * 
	 * It is assumed jquery is loaded.
	 * 
	 */
	this.submitDownloadForParams = function({ downloadStrutsAction, dataToSend }) {

		const form = document.createElement( "form" );

		try {

			$( form ).hide();

			form.setAttribute( "method", "post" );
			form.setAttribute( "action", downloadStrutsAction );
			form.setAttribute( "target", "_blank" );

			if ( dataToSend ) {
				const dataToSend_JSONString = JSON.stringify( dataToSend );

				const requestJSONStringField = document.createElement( "textarea" );
				requestJSONStringField.setAttribute("name", "requestJSONString");

				$( requestJSONStringField ).text( dataToSend_JSONString );

				form.appendChild( requestJSONStringField );
			}

			document.body.appendChild(form);    // Not entirely sure if this is necessary			

			form.submit();

		} finally {

			document.body.removeChild( form );
		}
	};
	
};

//  instance of object
const qc_pages_Single_Merged_Common = new QC_Pages_Single_Merged_Common();

export { qc_pages_Single_Merged_Common }

