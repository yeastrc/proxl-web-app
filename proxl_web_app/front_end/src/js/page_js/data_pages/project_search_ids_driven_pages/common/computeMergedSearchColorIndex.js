/**
 * computeMergedSearchColorIndex.js
 * 
 * Compute the Merged Color Index into the CSS class names starting with 'merged-search-search-background-color-'
 * 
 * This code must match the code in mergedSearch_SearchIndexToSearchColorCSSClassName.jsp
 * 
 */

//  JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

const computeMergedSearchColorIndex_OneBased = function({ searchIndex_ZeroBased }) {

    if ( searchIndex_ZeroBased === undefined ) {
        throw Error("No Value for parameter searchIndex_ZeroBased");
    }
    const mergedSearchColorIndex_OneBased = ( searchIndex_ZeroBased % 9 ) + 1;
    return mergedSearchColorIndex_OneBased;
}

export { computeMergedSearchColorIndex_OneBased }
