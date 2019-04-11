/**
 * Handle holding state and serialization/deserialization for URL
 */

"use strict";

// rgba arrays for pre-defined colors
const _COLOR_DEFAULT_BACKBONE = [ 190, 190, 190, 0.75 ];

export class BackboneColorManager {

    constructor() {
        this._colorsForChains = { };
    }

    /**
     * Set the rgba array to use for the given chainName
     *
     * @param chainName
     * @param color
     */
    setChainColor({ chainName, color }) {

        this._colorsForChains[ chainName ] = color;
    }

    /**
     * Get the rgba array to use for the given chain name
     *
     * @param chainName
     * @returns {number[]|*}
     */
    getChainColor( chainName ) {


        if( !(chainName in this._colorsForChains ) ) {
            return _COLOR_DEFAULT_BACKBONE;
        }

        return this._colorsForChains[ chainName ];
    }


    resetColors() {

        this._colorsForChains = { };

    }

    getDefaultColor() {
        return _COLOR_DEFAULT_BACKBONE;
    }


    /************* methods for serializing/deserializing json for URL hash *************/

    getDataStructureForHash() {

        let dataStructure = [ ];

        for( const chainName of Object.keys( this._colorsForChains ) ) {

            dataStructure.push( chainName );
            dataStructure = dataStructure.concat( this._colorsForChains[ chainName ] );

        }

        return dataStructure;
    }

    populateDataFromJSON( jsonData ) {

        const dataArray = jsonData[ 'bc' ];

        if( !dataArray ) { return; }

        for (let i = 0; i < dataArray.length; i+=5) {

            const chainName = dataArray[ i ];
            const color = [ dataArray[ i + 1],
                dataArray[ i + 2 ],
                dataArray[ i + 3 ],
                dataArray[ i + 4 ] ];

            this.setChainColor( { chainName, color } );
        }
    }
}