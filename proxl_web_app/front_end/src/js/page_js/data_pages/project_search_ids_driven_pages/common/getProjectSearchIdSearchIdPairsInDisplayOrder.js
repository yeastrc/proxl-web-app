/**
 * getProjectSearchIdSearchIdPairsInDisplayOrder.js
 * 
 * Get Project Search Id / Search Id Pairs from JSON on the page
 * 
 */

//  JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

let _dataRetrievedFromPage = false;

let _projectSearchIdSearchIdPairsInDisplayOrder_FromPage = undefined;

let _projectSearchIdsInDisplayOrder_FromPage = undefined;

/**
 * 
 */
const getProjectSearchIdSearchIdPairsInDisplayOrder = function() {

    _getDataFromPage_IfNeeded();

    return _projectSearchIdSearchIdPairsInDisplayOrder_FromPage.projectSearchIdSearchIdPairList;
}


/**
 * 
 */
const getProjectSearchIdsInDisplayOrder = function() {

    _getDataFromPage_IfNeeded();

    return _projectSearchIdsInDisplayOrder_FromPage;
}


/**
 * 
 */
const _getDataFromPage_IfNeeded = function() {

    if ( _dataRetrievedFromPage ) {

        return;
    }

    const domElement = document.getElementById("project_search_id_search_id_pairs_display_order_list_json");
    if ( ! domElement ) {
        throw Error("No DOM element with id 'project_search_id_search_id_pairs_display_order_list_json'");
    }

    const json = domElement.textContent;
    if ( ! json ) {
        throw Error("DOM element with id 'project_search_id_search_id_pairs_display_order_list_json' is empty");
    }

    try {
        _projectSearchIdSearchIdPairsInDisplayOrder_FromPage = JSON.parse( json )
    } catch ( e ) {
        console.log( "Failed to parse JSON in DOM element with id 'project_search_id_search_id_pairs_display_order_list_json'.  json: " + json );
        console.log( e );
        throw e;
    }

    _projectSearchIdsInDisplayOrder_FromPage = [];
    for ( const entry of _projectSearchIdSearchIdPairsInDisplayOrder_FromPage.projectSearchIdSearchIdPairList ) {
        const projectSearchId = entry.projectSearchId;
        _projectSearchIdsInDisplayOrder_FromPage.push( projectSearchId );
    }
}

export { getProjectSearchIdSearchIdPairsInDisplayOrder, getProjectSearchIdsInDisplayOrder }
