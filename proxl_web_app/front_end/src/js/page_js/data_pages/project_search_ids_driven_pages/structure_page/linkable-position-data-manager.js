"use strict";


import {StructureWebserviceMethods} from "./structure-webservice-methods.js";

export class LinkablePositionDataManager {

    constructor() {
        this._pdbFileName = undefined;
        this._visibleProteinList = undefined;
        this._linkablePositionData = undefined;
    }

    /**
     * Return a promise that resolves when the data are available
     *
     * @param visibleProteins
     * @param pdbFileName
     */
    getLinkablePositionData( { visibleProteinList, pdbFileName, projectSearchIds } ) {

        const objectThis = this;

        if( this._pdbFileName === pdbFileName && visibleProteinsMatch( { visibleProteinList } ) ) {

            // used cache data
            return new Promise(function(resolve, reject) {
                resolve( objectThis._linkablePositionData );
            });

        } else {

            this._pdbFileName = undefined;
            this._visibleProteinList = undefined;
            this._linkablePositionData = undefined;

            return new Promise( function(resolve,reject) {

                let dataLoadPromise =  StructureWebserviceMethods.doLinkablePositionsLookup({
                    proteins :  visibleProteinList,
                    projectSearchIds : projectSearchIds,
                });

                dataLoadPromise.then( (data)=>{

                    objectThis._pdbFileName = pdbFileName;
                    objectThis._visibleProteinList = visibleProteinList;
                    objectThis._linkablePositionData = data;

                    resolve( data );
                });

            });

        }

        /**
         * Returns true if the two arrays each contain the same values, order doesn't matter
         * @param visibleProteins
         * @returns {boolean}
         */
        function visibleProteinsMatch( { visibleProteinList } ) {
            if( !objectThis._visibleProteinList ) { return false; }

            if( objectThis._visibleProteinList.length !== visibleProteinList.length ) { return false; }

            for( let visibleProtein of visibleProteinList ) {
                if( !objectThis._visibleProteinList.includes( visibleProtein ) ) {
                    return false;
                }
            }

            return true;
        }
    }




}