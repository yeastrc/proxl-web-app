"use strict";


import {LinkablePositionUtils} from "./linkable-positions-utils.js";
import {StatsUtils} from "../../../../utils/stats";

export class PValueUtils {

    static updatePValueDisplay({linkablePositionData, visibleProteinsMap,
                                   onlyShortest, alignments, structure, renderedLinks }) {

        if( !visibleProteinsMap ) { return; }

        const legendUpdateSelector = "#p-value-legend";
        const currentCutoffDivSelector = "#distance-cutoff-report-field";
        const divToUpdateSelector = "#p-value-display";

        const cutoff = PValueUtils.getCutoff( currentCutoffDivSelector );
        if( cutoff === undefined ) {
            return;
        }

        const renderedDistanceArray = LinkablePositionUtils.getRenderedDistanceArray( renderedLinks );

        const pdbDistanceArray = LinkablePositionUtils.getDistanceArrayFromLinkablePositions({
            data : linkablePositionData,
            visibleProteinsMap,
            onlyShortest,
            alignments,
            structure
        });

        const T = pdbDistanceArray.length;
        const A = PValueUtils.getDistanceCountUnderCutoff( pdbDistanceArray, cutoff );
        const B = renderedDistanceArray.length;
        const I = PValueUtils.getDistanceCountUnderCutoff( renderedDistanceArray, cutoff );

        const pValue = StatsUtils.getHypergeometricPValue({
           setASize: A,
           setBSize: B,
           ixnSize: I,
           universeSize: T
        });

        $( divToUpdateSelector ).html( pValue.toPrecision( 4 ) );
        PValueUtils.updateLegend( { legendUpdateSelector, T, A, B, I, cutoff } );

    }

    static updateLegend( { legendUpdateSelector, T, A, B, I, cutoff } ) {

        const $legendObject = $( legendUpdateSelector );

        if( $legendObject ) {

            let txt = "Random prob. of " + I + " or more UDRs <= " + cutoff +
                "&Aring;, given " + B + " observed UDRs and " + A + "/" + T +
                " UDRs in PDB <= " + cutoff + "&Aring;";

            $legendObject.empty();
            $legendObject.html( txt );

        }

    }

    static getDistanceCountUnderCutoff( distanceArray, cutoff ) {

        let count = 0;

        for( const distance of distanceArray ) {
            if( distance <= cutoff ) {
                count++;
            }
        }

        return count;
    }





    static getCutoff( currentCutoffDivSelector ) {

        const $distanceCutoffReportField = $( currentCutoffDivSelector );
        if( $distanceCutoffReportField ) {

            const cutoff = $distanceCutoffReportField.val();
            if( isNaN(cutoff) ) {
                return undefined;
            }

            return parseInt( cutoff );
        }

        return undefined;
    }

}
