
//  crosslink-image-viewer-click-element-handlers.js

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";




//   All references to proteinId are actually referencing the protein sequence id


///////////////////////////////////////////////////////////////////////////

////////////    Click handlers for the links ( Lines that show the links )


//////////////   Process LOOP link


/////////////////////

function getLooplinkDataForSpecificLinkInGraph( params ) {

	var clickThis = params.clickThis;

	var psmPeptideCutoffsRootObject = params.psmPeptideCutoffsRootObject;

	var $clickThis = $(clickThis);
	
	
	incrementSpinner();				// create spinner

	var from_protein_name = $clickThis.attr( "fromp" );

	var protein_position_1 = $clickThis.attr( "frompp" );
	var protein_position_2 = $clickThis.attr( "topp" );
	var searchIdsCommaDelimString = $clickThis.attr( "searches" );

	var from_protein_id = $clickThis.attr( "from_protein_id" );

	var searchesArray = searchIdsCommaDelimString.split(",");

	var context = {
			
			searchesArray : searchesArray,
			
			from_protein_id : from_protein_id,
			from_protein_name : from_protein_name,
			protein_position_1 : protein_position_1,
			protein_position_2 : protein_position_2			
			
	};
	
	var getLooplinkDataCommonParams = {
			
			context : context,
			psmPeptideCutoffsRootObject : psmPeptideCutoffsRootObject
	};
	
	getLooplinkDataCommon( getLooplinkDataCommonParams );
}

/////////////////////////////////

//////////////   Process CROSS link




/////////////////////

function getCrosslinkDataForSpecificLinkInGraph( params ) {

	var clickThis = params.clickThis;

	var psmPeptideCutoffsRootObject = params.psmPeptideCutoffsRootObject;
	
	var $clickThis = $(clickThis);


	incrementSpinner();				// create spinner

	var from_protein_name = $clickThis.attr( "fromp" );
	var from_protein_position = $clickThis.attr( "frompp" );
	var to_protein_name = $clickThis.attr( "top" );
	var to_protein_position = $clickThis.attr( "topp" );

	var from_protein_id = $clickThis.attr( "from_protein_id" );
	var to_protein_id = $clickThis.attr( "to_protein_id" );

	var searchesCommaDelim = $clickThis.attr( "searches" ); //  Searches, comma delimited

	
	
	var from_protein_id_int = parseInt( from_protein_id, 10 );
	if ( isNaN( from_protein_id_int ) ) {
		throw Error( "from_protein_id is not an Integer, is: " + from_protein_id );
	}
	
	var to_protein_id_int = parseInt( to_protein_id, 10 );
	if ( isNaN( to_protein_id_int ) ) {
		throw Error( "to_protein_id is not an Integer, is: " + to_protein_id );
	}
	
	var from_protein_position_int = parseInt( from_protein_position, 10 );
	if ( isNaN( from_protein_position_int ) ) {
		throw Error( "from_protein_position is not an Integer, is: " + from_protein_position );
	}
	
	var to_protein_position_int = parseInt( to_protein_position, 10 );
	if ( isNaN( to_protein_position_int ) ) {
		throw Error( "to_protein_position is not an Integer, is: " + to_protein_position );
	}

	var searchesArray = searchesCommaDelim.split(",");
	
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

	
	var getCrosslinkDataCommonParams = {
			
			psmPeptideCutoffsRootObject : psmPeptideCutoffsRootObject,
			
			context : context
			
	};

	getCrosslinkDataCommon( getCrosslinkDataCommonParams );
}



////////////////////////////////////////////


//////////////  Process MONO link


/////////////////////

function getMonolinkDataForSpecificLinkInGraph( params ) {

	var clickThis = params.clickThis;

	var psmPeptideCutoffsRootObject = params.psmPeptideCutoffsRootObject;
	
	var $clickThis = $(clickThis);


	incrementSpinner();				// create spinner


	var from_protein_name = $clickThis.attr( "fromp" );

	var protein_position = $clickThis.attr( "frompp" );
	var searchIdsCommaDelimString = $clickThis.attr( "searches" );

	var from_protein_id = $clickThis.attr( "from_protein_id" );

	var searchesArray = searchIdsCommaDelimString.split(",");

	var context = {
			
			searchesArray : searchesArray,
			
			from_protein_name : from_protein_name,
			from_protein_id : from_protein_id,
			protein_position : protein_position
	};
	
	var getMonolinkDataCommonParams = {
			
			context : context,
			psmPeptideCutoffsRootObject : psmPeptideCutoffsRootObject
	};
	
	getMonolinkDataCommon( getMonolinkDataCommonParams );
	
	
}

