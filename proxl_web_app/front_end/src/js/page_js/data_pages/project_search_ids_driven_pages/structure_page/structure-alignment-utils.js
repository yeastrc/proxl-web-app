"use strict";

import {StructureUtils} from "./stucture-utils";

export class StructureAlignmentUtils {

    static getAlignmentByChainAndProtein(chainId, proteinId, alignments) {

        if (!alignments[chainId]) {
            return undefined;
        }

        for (let j = 0; j < alignments[chainId].length; j++) {

            const alignment = alignments[chainId][j];

            if (proteinId == alignment.proteinSequenceVersionId) {
                return alignments[chainId][j];
            }

        }

        return undefined;
    };

    static getAlignmentById( alignmentId, alignments ) {

        const chains = Object.keys( alignments );
        for( let i = 0; i < chains.length; i++ ) {
            const chain = chains[ i ];

            if( !alignments[ chain ] ) { return undefined; }

            for( let j = 0; j < alignments[ chain ].length; j++ ) {

                if( alignmentId == alignments[ chain ][ j ][ 'id' ] ) {
                    return alignments[ chain ][ j ];
                }

            }
        }

        return undefined;
    };

    static findCACoords(proteinId, position, chains, alignments, structure) {

        let coords = [];

        for (let i = 0; i < chains.length; i++) {

            const pdbResidue = StructureAlignmentUtils.findPDBResidueFromAlignment(proteinId, position, chains[i], alignments );
            if (!pdbResidue) {
                continue;
            }

            const chain = structure.chainByName(chains[i]);

            const residues = chain.residues();
            const residue = residues[pdbResidue - 1];

            if (residue) {
                const atom = residue.atom('CA');
                if (atom) {
                    coords.push(atom.pos());
                }
            } else {
                console.log("WARNING: Did not find residue at position " + pdbResidue + " in chain " + chains[i] + " in PDB.");
                console.log(residue);
                console.log(pdbResidue);
                console.log(residues[pdbResidue - 1]);
                console.log(residues);
            }

        }

        return coords;
    };

    static findCAAtoms( proteinId, position, chains, alignments, structure ) {

        let atoms = [];

        for( let i = 0; i < chains.length; i++ ) {

            const pdbResidue = StructureAlignmentUtils.findPDBResidueFromAlignment( proteinId, position, chains[ i ], alignments );
            if( !pdbResidue ) { continue; }

            const chain = structure.chainByName( chains[ i ] );

            const residues = chain.residues();
            const residue = residues[ pdbResidue - 1];

            if( residue ) {
                const atom = residue.atom( 'CA' );
                if( atom ) {
                    atoms.push( atom );
                }
            } else {
                console.log( "WARNING: Did not find residue at position " + pdbResidue + " in chain " + chains[ i ] + " in PDB." );
                console.log( residue );
                console.log( pdbResidue );
                console.log( residues[ pdbResidue - 1 ] );
                console.log( residues );
            }

        }

        return atoms;
    };

    static findPDBResidueFromAlignment(proteinId, position, chain, alignments) {

        const alignment = StructureAlignmentUtils.getAlignmentByChainAndProtein(chain, proteinId, alignments);

        let expPosition = 0;
        let pdbPosition = 0;
        for (let i = 0; i < alignment.alignedExperimentalSequence.length; i++) {

            if (alignment.alignedExperimentalSequence[i] != '-') {
                expPosition++;
            }
            if (alignment.alignedPDBSequence[i] != '-') {
                pdbPosition++;
            }

            if (expPosition == position) {
                if (alignment.alignedPDBSequence[position - 1] == '-') {
                    //console.log( "Found no PDB position for position " + position + " in protein " + proteinId );
                    return undefined;
                } else {
                    //console.log( "Found PDB position " + pdbPosition + " for position " + position + " in protein " + proteinId );
                    return pdbPosition;
                }
            }
        }

        console.log("MAJOR WARNING: DID NOT FIND POSITION " + position + " FOR PROTEIN " + proteinId + " IN CHAIN " + chain);
        return undefined;
    };

    static getShortestDistanceForProteinPair( proteinPairObject, alignments, structure ) {

        const protein1 =  parseInt( proteinPairObject[ 'protein1' ] );
        const protein2 =  parseInt( proteinPairObject[ 'protein2' ] );

        const position1 = parseInt( proteinPairObject[ 'position1' ] );
        const position2 = parseInt( proteinPairObject[ 'position2' ] );

        const visibleProteinsMap = getVisibleProteins();
        const chains1 = visibleProteinsMap[ protein1 ];
        const chains2 = visibleProteinsMap[ protein2 ];

        let shortestLink = 0;

        if( !chains1 || chains1 == undefined || chains1.length < 1 ) {
            console.log( "ERROR: Got no chains for protein: " + protein1 );
            return -1;
        }

        if( !chains2 || chains2 == undefined || chains2.length < 1 ) {
            console.log( "ERROR: Got no chains for protein: " + protein2 );
            return -1;
        }

        for( var j = 0; j < chains1.length; j++ ) {
            var chain1 = chains1[ j ];

            var coordsArray1 = StructureAlignmentUtils.findCACoords( protein1, position1, [ chain1 ], alignments, structure );
            if( coordsArray1 == undefined || coordsArray1.length < 1 ) { continue; }

            for( var k = 0; k < chains2.length; k++ ) {
                var chain2 = chains2[ k ];

                if( chain1 == chain2 && protein1 == protein2 && position1 == position2 ) { continue; }

                var coordsArray2 = StructureAlignmentUtils.findCACoords( protein2, position2, [ chain2 ], alignments, structure );
                if( coordsArray1 == undefined || coordsArray2.length < 1 ) { continue; }

                var distance = StructureUtils.calculateDistance( coordsArray1[ 0 ], coordsArray2[ 0 ] );

                if( !shortestLink || shortestLink[ 'distance' ] > distance ) {

                    shortestLink = {
                        'chain1' : chain1,
                        'chain2' : chain2,
                        'protein1' : protein1,
                        'protein2' : protein2,
                        'position1' : position1,
                        'position2' : position2,
                        'distance' : distance
                    };
                }

            }
        }

        if( shortestLink ) {

            return shortestLink.distance;
        }

        return -1;
    };

    /**
     * For the given alignment and PDB position, find the position in the experimental protein that corresponds to it
     */
    static findExpPositionForPDBPosition( alignment, position ) {

        let expPosition = 0;
        let pdbPosition = 0;

        for( let i = 0; i < alignment.alignedPDBSequence.length; i++ ) {

            if( alignment.alignedExperimentalSequence[ i ] != '-' ) { expPosition++; }
            if( alignment.alignedPDBSequence[ i ] != '-' ) { pdbPosition++; }

            if( pdbPosition == position ) {
                if( alignment.alignedExperimentalSequence[ i ] == '-' ) {
                    //console.log( "Found no Nrseq position for position " + position + " in chain " + alignment.chainId );
                    return undefined;
                }
                else {
                    //console.log( "Found Experimental position " + expPosition + " for position " + position + " in chain " + alignment.chainId );
                    return expPosition;
                }
            }
        }

        console.log( "MAJOR WARNING: DID NOT FIND POSITION " + position + " FOR PROTEIN " + alignment.proteinSequenceVersionId + " IN CHAIN " + alignment.chainId );
        console.log( alignment );
        return null;
    };

    /**
     * Get all proteinSequenceVersionId:position pairs that correspond to the supplied pdbPosition in the supplied alignments
     *
     * This is for the "experimental" protein sequence from the experiment
     */
    static getExpproteinSequenceVersionIdPositionPairs( alignments, pdbPosition ) {

        let expproteinSequenceVersionIdPositionPairs = [];

        for( let i = 0; i < alignments.length; i++ ) {
            const expProteinPosition = StructureAlignmentUtils.findExpPositionForPDBPosition( alignments[ i ], pdbPosition );

            if( expProteinPosition ) {
                const expproteinSequenceVersionIdPositionPair = {
                    proteinSequenceVersionId : alignments[ i ].proteinSequenceVersionId,
                    position : expProteinPosition
                };

                expproteinSequenceVersionIdPositionPairs.push( expproteinSequenceVersionIdPositionPair );
            }
        }

        return expproteinSequenceVersionIdPositionPairs;
    };

}