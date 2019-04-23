"use strict";

export class StructureUtils {

    /**
     * Calculate distance between 2 3D coordinates
     */
    static calculateDistance( coords1, coords2 ) {

        const xpart = Math.pow( coords1[ 0 ] - coords2[ 0 ], 2 );
        const ypart = Math.pow( coords1[ 1 ] - coords2[ 1 ], 2 );
        const zpart = Math.pow( coords1[ 2 ] - coords2[ 2 ], 2 );

        return Math.sqrt( xpart + ypart + zpart );

    };

}