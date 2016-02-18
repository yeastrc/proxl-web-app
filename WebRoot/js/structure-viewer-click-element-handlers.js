
//  crosslink-image-viewer-click-element-handlers.js



///////////////////////////////////////////////////////////////////////////

////////////    Click handlers for the links ( Lines that show the links )



//////////////   Process LOOP link


/////////////////////

function getLooplinkDataForSpecificLinkInGraph( params, link ) {

	var psmPeptideCutoffsRootObject = params.psmPeptideCutoffsRootObject;

	incrementSpinner();				// create spinner

	var requestContext = {
			
			searchesArray : link.searchIds,
			
			from_protein_id : link.protein1,
			from_protein_name : _proteinNames[ link.protein1 ],
			protein_position_1 : link.position1,
			protein_position_2 : link.position2			
	};
	
	var fcnToCallOnDisplay = function() {

		$length = $("#looplink_length");
		$length.text( Math.round( link.length * 10 ) / 10 );
	};
	
	
	var getLooplinkDataCommonParams = {
			
			context : requestContext,
			psmPeptideCutoffsRootObject : psmPeptideCutoffsRootObject,
			
			fcnToCallOnDisplay : fcnToCallOnDisplay
	};
	
	getLooplinkDataCommon( getLooplinkDataCommonParams );
	
}

/////////////////////////////////

//////////////   Process CROSS link




/////////////////////

function getCrosslinkDataForSpecificLinkInGraph( params, link ) {

	var psmPeptideCutoffsRootObject = params.psmPeptideCutoffsRootObject;
	
	incrementSpinner();				// create spinner

	var from_protein_name = _proteinNames[ link.protein1 ];
	var from_protein_position = link.position1;
	var to_protein_name = _proteinNames[ link.protein2 ];
	var to_protein_position = link.position2;

	var from_protein_id = link.protein1;
	var to_protein_id = link.protein2;

	var searchesArray = link.searchIds;
	
	var searchesCommaDelim = searchesArray.join( "," );
	
	
	var from_protein_id_int = parseInt( from_protein_id, 10 );
	if ( isNaN( from_protein_id_int ) ) {
		throw "from_protein_id is not an Integer, is: " + from_protein_id;
	}
	
	var to_protein_id_int = parseInt( to_protein_id, 10 );
	if ( isNaN( to_protein_id_int ) ) {
		throw "to_protein_id is not an Integer, is: " + to_protein_id;
	}
	
	var from_protein_position_int = parseInt( from_protein_position, 10 );
	if ( isNaN( from_protein_position_int ) ) {
		throw "from_protein_position is not an Integer, is: " + from_protein_position;
	}
	
	var to_protein_position_int = parseInt( to_protein_position, 10 );
	if ( isNaN( to_protein_position_int ) ) {
		throw "to_protein_position is not an Integer, is: " + to_protein_position;
	}
	
	var context = {

			protein1: {

				protein_id_int: from_protein_id_int,
				protein_id: from_protein_id,

				protein_position_int: from_protein_position_int,
				protein_position : from_protein_position,
				
				protein_name : from_protein_name
			},
			protein2: {

				protein_id_int: to_protein_id_int,
				protein_id: to_protein_id,

				protein_position_int : to_protein_position_int,
				protein_position : to_protein_position,

				protein_name : to_protein_name
			},

			searchesCommaDelim: searchesCommaDelim,
			searchesArray: searchesArray
	};
	
	
	if ( ( context.protein1.protein_id_int > context.protein2.protein_id_int )
			|| ( context.protein1.protein_id_int === context.protein2.protein_id_int 
					&& context.protein1.protein_position_int > context.protein2.protein_position_int ) ){
		
		//  Switch the proteins so proteinId1 < proteinId2
		//     and if proteinId1 == proteinId2, then protein_position1 < protein_position2
		
		var tempprotein = context.protein1;
		context.protein1 = context.protein2;
		context.protein2 = tempprotein;
	}
	

	var fcnToCallOnDisplay = function() {

		$length = $("#crosslink_length");
		$length.text( Math.round( link.length * 10 ) / 10 );
	};

	var getCrosslinkDataCommonParams = {
			
			psmPeptideCutoffsRootObject : psmPeptideCutoffsRootObject,
			context : context,
			fcnToCallOnDisplay : fcnToCallOnDisplay
	};

	getCrosslinkDataCommon( getCrosslinkDataCommonParams );
}



//////////////Process MONO link


/////////////////////

function getMonolinkDataForSpecificLinkInGraph( params, link ) {

	console.log( params );
	
	var psmPeptideCutoffsRootObject = params.psmPeptideCutoffsRootObject;
	
	incrementSpinner();				// create spinner

	var requestContext = {
			
			searchesArray : link.searchIds,
			
			from_protein_name : _proteinNames[ link.protein1 ],
			from_protein_id : link.protein1,
			protein_position : link.position1
	};
	
	var getMonolinkDataCommonParams = {
			
			context : requestContext,
			psmPeptideCutoffsRootObject : psmPeptideCutoffsRootObject
	};
	
	getMonolinkDataCommon( getMonolinkDataCommonParams );
}


