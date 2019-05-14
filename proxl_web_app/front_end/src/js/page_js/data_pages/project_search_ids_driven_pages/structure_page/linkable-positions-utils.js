"use strict";


import {StructureAlignmentUtils} from "./structure-alignment-utils";
import {StructureUtils} from "./stucture-utils";

export class LinkablePositionUtils {

    static getRenderedDistanceArray( renderedLinks, linkExclusionHandler ) {

        let distanceArray = [ ];
        let UDRsCounted = { };

        if( renderedLinks.crosslinks ) {
            for (let i = 0; i < renderedLinks['crosslinks'].length; i++) {

                const link = renderedLinks['crosslinks'][i]['link'];

                if( !linkExclusionHandler || !linkExclusionHandler.isLinkExcluded( link ) ) {
                    LinkablePositionUtils.addCrosslinkToCountedUDRs({UDRsCounted, link})

                    distanceArray.push(parseFloat(renderedLinks['crosslinks'][i]['link']['length']));
                }
            }
        }

        if( renderedLinks.looplinks ) {
            for (let i = 0; i < renderedLinks['looplinks'].length; i++) {

                const link = renderedLinks['looplinks'][i]['link'];

                if( !LinkablePositionUtils.isUDRinRenderedUDRs( {
                    renderedUDRs: UDRsCounted,
                    protein1: link.protein1,
                    protein2: link.protein1,
                    position1: link.position1,
                    position2: link.position2 } )) {
                    if( !linkExclusionHandler || !linkExclusionHandler.isLinkExcluded( link ) ) {

                        console.log( 'added looplink:' );
                        console.log( link );

                        distanceArray.push(parseFloat(renderedLinks['looplinks'][i]['link']['length']));
                    }
                }
            }
        }

        return distanceArray;
    }

    static getRenderedUDRs({ renderedLinks, linkExclusionHandler }) {

        let renderedUDRs = { };

        if( renderedLinks.crosslinks ) {
            for (let i = 0; i < renderedLinks['crosslinks'].length; i++) {

                const link = renderedLinks['crosslinks'][i]['link'];

                if( !linkExclusionHandler || !linkExclusionHandler.isLinkExcluded( link ) ) {
                    LinkablePositionUtils.addCrosslinkToCountedUDRs({UDRsCounted: renderedUDRs, link})
                }
            }
        }

        if( renderedLinks.looplinks ) {
            for (let i = 0; i < renderedLinks['looplinks'].length; i++) {

                const link = renderedLinks['looplinks'][i]['link'];

                if( !linkExclusionHandler || !linkExclusionHandler.isLinkExcluded( link ) ) {
                    LinkablePositionUtils.addLooplinkToCountedUDRs({UDRsCounted: renderedUDRs, link})
                }
            }
        }

        return renderedUDRs;
    }

    static addCrosslinkToCountedUDRs({UDRsCounted,link}) {
        const protein1 = link['protein1'];
        const protein2 = link['protein2'];
        const position1 = link['position1'];
        const position2 = link['position2'];

        // add this to the list of UDRs we've counted so we can calculated total unique UDRs among cross- and loop-links
        if( !( protein1 in UDRsCounted ) ) { UDRsCounted[ protein1 ] = { }; }
        if( !( protein2 in UDRsCounted[ protein1 ] ) ) { UDRsCounted[ protein1 ][ protein2 ] = { }; }
        if( !( position1 in UDRsCounted[ protein1 ][ protein2 ] ) ) { UDRsCounted[ protein1 ][ protein2 ][ position1 ] = { }; };
        if( !( position2 in UDRsCounted[ protein1 ][ protein2 ][ position1 ] ) ) { UDRsCounted[ protein1 ][ protein2 ][ position1 ][ position2 ] = { }; };
    }

    static addLooplinkToCountedUDRs({UDRsCounted,link}) {
        const protein1 = link['protein1'];
        const protein2 = link['protein1'];
        const position1 = link['position1'];
        const position2 = link['position2'];

        // add this to the list of UDRs we've counted so we can calculated total unique UDRs among cross- and loop-links
        if( !( protein1 in UDRsCounted ) ) { UDRsCounted[ protein1 ] = { }; }
        if( !( protein2 in UDRsCounted[ protein1 ] ) ) { UDRsCounted[ protein1 ][ protein2 ] = { }; }
        if( !( position1 in UDRsCounted[ protein1 ][ protein2 ] ) ) { UDRsCounted[ protein1 ][ protein2 ][ position1 ] = { }; };
        if( !( position2 in UDRsCounted[ protein1 ][ protein2 ][ position1 ] ) ) { UDRsCounted[ protein1 ][ protein2 ][ position1 ][ position2 ] = { }; };
    }


    static isUDRinRenderedUDRs({ renderedUDRs, protein1, protein2, position1, position2 } ) {

        // console.log( renderedUDRs );
        // console.log( protein1 );
        // console.log( protein2 );
        // console.log( position1 );
        // console.log( position2 );
        // console.log( "=============" );
        // console.log( renderedUDRs[ protein1 ] );
        // console.log( renderedUDRs[ protein1 ][ protein2 ] );
        // console.log( renderedUDRs[ protein1 ][ protein2 ][ position1 ] );
        // console.log( renderedUDRs[ protein1 ][ protein2 ][ position1 ][ position2 ] );
        // console.log( "=============" );
        
        if( protein1 in renderedUDRs ) {

            if( protein2 in renderedUDRs[ protein1 ] &&
            position1 in renderedUDRs[ protein1 ][ protein2 ] &&
            position2 in renderedUDRs[ protein1 ][ protein2 ][ position1 ] ) {
                return true;
            }

            // test swapping positions if it's within the same protein
            if( protein1 === protein2 ) {
                if( protein2 in renderedUDRs[ protein1 ] &&
                    position2 in renderedUDRs[ protein1 ][ protein2 ] &&
                    position1 in renderedUDRs[ protein1 ][ protein2 ][ position2 ] ) {
                    return true;
                }
            }


        } else if( protein1 !== protein2 && protein2 in renderedUDRs ) {

            if( protein1 in renderedUDRs[ protein2 ] &&
                position2 in renderedUDRs[ protein2 ][ protein1 ] &&
                position1 in renderedUDRs[ protein2 ][ protein1 ][ position2 ] ) {
                return true;
            }

        }

        return false;
    }

    static getDistanceArrayFromLinkablePositions( {
                                                      data,
                                                      visibleProteinsMap,
                                                      onlyShortest,
                                                      alignments,
                                                      structure,
                                                      linkExclusionHandler,
                                                      renderedLinks
                                                    }) {

        const pdbDistanceArray = [ ];

        for( let i = 0; i < data.length; i++ ) {

            // all the actually-rendered UDRs. Do not include the distance for a non-rendered UDR
            const renderedUDRs = LinkablePositionUtils.getRenderedUDRs({ renderedLinks, linkExclusionHandler } );

            const protein1 =  parseInt(data[ i ][ 'protein1' ]);
            const protein2 =  parseInt(data[ i ][ 'protein2' ]);

            const position1 = parseInt(data[ i ][ 'position1' ]);
            const position2 = parseInt(data[ i ][ 'position2' ]);

            // don't included excluded things
            if( ( linkExclusionHandler.isCrosslinkExcluded( protein1, position1, protein2, position2 ) ||
                linkExclusionHandler.isLooplinkExcluded( protein1, position1, position2 ) ) &&
                !LinkablePositionUtils.isUDRinRenderedUDRs( { renderedUDRs, protein1, protein2, position1, position2 } ) ) {
                continue;
            }


            const chains1 = visibleProteinsMap[ protein1 ];
            const chains2 = visibleProteinsMap[ protein2 ];

            let shortestDistance = -1;

            if( !chains1 || chains1 == undefined || chains1.length < 1 ) {
                console.log( "ERROR: Got no chains for protein: " + protein1 );
                return;
            }

            if( !chains2 || chains2 == undefined || chains2.length < 1 ) {
                console.log( "ERROR: Got no chains for protein: " + protein2 );
                return;
            }

            for( let j = 0; j < chains1.length; j++ ) {
                const chain1 = chains1[ j ];

                const coordsArray1 = StructureAlignmentUtils.findCACoords( protein1, position1, [ chain1 ], alignments, structure );
                if( coordsArray1 == undefined || coordsArray1.length < 1 ) { continue; }

                for( let k = 0; k < chains2.length; k++ ) {
                    const chain2 = chains2[ k ];

                    if( chain1 == chain2 && protein1 == protein2 && position1 == position2 ) { continue; }

                    const coordsArray2 = StructureAlignmentUtils.findCACoords( protein2, position2, [ chain2 ], alignments, structure );
                    if( coordsArray1 == undefined || coordsArray2.length < 1 ) { continue; }

                    const distance = StructureUtils.calculateDistance( coordsArray1[ 0 ], coordsArray2[ 0 ] );

                    if( !onlyShortest ) {

                        pdbDistanceArray.push( distance );

                    } else {

                        if( shortestDistance === -1 || shortestDistance > distance ) {
                            shortestDistance = distance;
                        }

                    }
                }
            }

            if( onlyShortest ) {
                if( shortestDistance != -1 ) {
                    pdbDistanceArray.push(shortestDistance);
                }
            }
        }

        return pdbDistanceArray;
    }

}