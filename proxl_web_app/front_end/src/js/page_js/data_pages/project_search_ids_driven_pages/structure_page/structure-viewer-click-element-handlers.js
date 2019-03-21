/**
 * structure-viewer-click-element-handlers.js
 * 
 * Javascript for the viewMergedStructure.jsp page
 *  
 * !!! The following variables passed in from "structure-viewer-page.js" are used in this file:
 * 
 *    structurePagePrimaryRootCodeObject
 */


import { getLooplinkDataCommon, getCrosslinkDataCommon, getMonolinkDataCommon, attachViewLinkInfoOverlayClickHandlers } from 'page_js/data_pages/project_search_ids_driven_pages/image_page__structure_page__shared/image_structure_click_element_common.js';


var structurePagePrimaryRootCodeObject = undefined; // passed in from "structure-viewer-page.js"


///////////////////////////////////////////////////////////////////////////

////////////    Click handlers for the links ( Lines that show the links )



//////////////   Process LOOP link


/////////////////////

function getLooplinkDataForSpecificLinkInGraph( params, link ) {

	var psmPeptideCutoffsRootObject = params.psmPeptideCutoffsRootObject;
	var removeNonUniquePSMs = params.removeNonUniquePSMs;

	incrementSpinner();				// create spinner

	var context = {
			
			searchesArray : link.searchIds,
			
			from_protein_id : link.protein1,
			from_protein_name : structurePagePrimaryRootCodeObject.getVariable__v_proteinNames()[ link.protein1 ],
			protein_position_1 : link.position1,
			protein_position_2 : link.position2,
			
			fcnToCallOnDisplay : function() {

				var $length = $("#looplink_length");
				$length.text( Math.round( link.length * 10 ) / 10 );
			}
	};
	
	
	var getLooplinkDataCommonParams = {
			
			context : context,
			psmPeptideCutoffsRootObject : psmPeptideCutoffsRootObject,
			removeNonUniquePSMs : removeNonUniquePSMs
	};
	
	getLooplinkDataCommon( getLooplinkDataCommonParams );
	
}

/////////////////////////////////

//////////////   Process CROSS link




/////////////////////

function getCrosslinkDataForSpecificLinkInGraph( params, link ) {

	var psmPeptideCutoffsRootObject = params.psmPeptideCutoffsRootObject;
	var removeNonUniquePSMs = params.removeNonUniquePSMs;
	
	incrementSpinner();				// create spinner

	var from_protein_name = structurePagePrimaryRootCodeObject.getVariable__v_proteinNames()[ link.protein1 ];
	var from_protein_position = link.position1;
	var to_protein_name = structurePagePrimaryRootCodeObject.getVariable__v_proteinNames()[ link.protein2 ];
	var to_protein_position = link.position2;

	var from_protein_id = link.protein1;
	var to_protein_id = link.protein2;

	var searchesArray = link.searchIds;
	
	var searchesCommaDelim = searchesArray.join( "," );
	
	
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
			searchesArray: searchesArray,
			

			fcnToCallOnDisplay : function() {

				var $length = $("#crosslink_length");
				$length.text( Math.round( link.length * 10 ) / 10 );
			}
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
			removeNonUniquePSMs : removeNonUniquePSMs,
			context : context
	};

	getCrosslinkDataCommon( getCrosslinkDataCommonParams );
}



//////////////Process MONO link


/////////////////////

function getMonolinkDataForSpecificLinkInGraph( params, link ) {

	console.log( params );
	
	var psmPeptideCutoffsRootObject = params.psmPeptideCutoffsRootObject;
	var removeNonUniquePSMs = params.removeNonUniquePSMs;
	
	incrementSpinner();				// create spinner

	var context = {
			
			searchesArray : link.searchIds,
			
			from_protein_name : structurePagePrimaryRootCodeObject.getVariable__v_proteinNames()[ link.protein1 ],
			from_protein_id : link.protein1,
			protein_position : link.position1
	};
	
	var getMonolinkDataCommonParams = {
			
			context : context,
			psmPeptideCutoffsRootObject : psmPeptideCutoffsRootObject,
			removeNonUniquePSMs : removeNonUniquePSMs
	};
	
	getMonolinkDataCommon( getMonolinkDataCommonParams );
}


/**
 * Called from "structure-viewer-page.js" to populate local copy of structurePagePrimaryRootCodeObject
 */
var structure_viewer_click_element_handlers_pass_structurePagePrimaryRootCodeObject = function( structurePagePrimaryRootCodeObject_Param ) {
	structurePagePrimaryRootCodeObject = structurePagePrimaryRootCodeObject_Param;
}


export { getLooplinkDataForSpecificLinkInGraph, getCrosslinkDataForSpecificLinkInGraph, getMonolinkDataForSpecificLinkInGraph, structure_viewer_click_element_handlers_pass_structurePagePrimaryRootCodeObject }
