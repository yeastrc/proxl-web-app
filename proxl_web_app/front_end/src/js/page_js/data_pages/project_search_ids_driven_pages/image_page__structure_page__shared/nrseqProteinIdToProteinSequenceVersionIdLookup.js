"use strict";

//   nrseqProteinIdToProteinSequenceVersionIdLookup.js

//  Perform the AJAX call to get protein sequence id values for the provided nrseq protein ids

function getProteinSequenceVersionIdsForNrseqProteinIds( params ) {
	
	try {

		var nrseqProteinIds = params.nrseqProteinIds;
		var callback = params.callback;


		console.log( "Getting nrseq protein ids to protein sequence ids: " );

		incrementSpinner();				// create spinner

		var url = "services/nrseqDataMapping/getProteinSequenceVersionIdsForNrseqProteinIds";


		var ajaxRequestData = {
				nrseqProteinId : nrseqProteinIds
		};

		$.ajax({
			type: "POST",  //  POST so no limit on number of protein ids
			url: url,
			dataType: "json",
			data: ajaxRequestData,  //  The data sent as params on the URL

			traditional: true,  //  Force traditional serialization of the data sent
			//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
			//   So proteinIdsToGetSequence array is passed as "proteinIdsToGetSequence=<value>" which is what Jersey expects

			success: function(data)	{

				try {

					var proteinIdsMapping = data;  


					console.log( "Received nrseq protein ids to protein sequence ids mapping" );

					decrementSpinner();

					callback( { proteinIdsMapping : proteinIdsMapping, calledAJAX : true } );
					
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}


			},
			failure: function(errMsg) {
				decrementSpinner();
				handleAJAXFailure( errMsg );
			},
			error: function(jqXHR, textStatus, errorThrown) {	
				decrementSpinner();
				handleAJAXError( jqXHR, textStatus, errorThrown );
			}
		});	


		return { calledAJAX : true };

		
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}

}

export { getProteinSequenceVersionIdsForNrseqProteinIds }
