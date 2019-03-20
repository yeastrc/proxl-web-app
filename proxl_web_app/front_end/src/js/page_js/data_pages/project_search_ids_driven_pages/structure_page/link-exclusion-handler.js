"use strict";

var LinkExclusionHandler = function() {
    this.init();
};

LinkExclusionHandler.prototype.init = function( ) {
    this.crosslinkExclusions = { };
    this.looplinkExclusions = { };
};

LinkExclusionHandler.prototype.getExcludedCrosslinkCount = function( ) {

    let counter = 0;

    for( const protein1Id of Object.keys( this.crosslinkExclusions ) ) {

        for( const protein2Id of Object.keys( this.crosslinkExclusions[ protein1Id ] ) ) {

            for (const position1 of Object.keys( this.crosslinkExclusions[protein1Id][protein2Id]) ) {

                counter += Object.keys(this.crosslinkExclusions[protein1Id][protein2Id][position1]).length;
            }
        }
    }

    return counter;
};

LinkExclusionHandler.prototype.getExcludedRenderedCrosslinkCount = function( renderedCrosslinks ) {

    let counter = 0;

    if( !renderedCrosslinks ) { return 0; }

    for( const renderedLink of renderedCrosslinks ) {

        const link = renderedLink.link;
        if( this.isLinkExcluded( link ) ) {
            counter++;
        }
    }

    return counter;
};

LinkExclusionHandler.prototype.getExcludedLooplinkCount = function( ) {

    let counter = 0;

    for( const protein1Id of Object.keys( this.looplinkExclusions ) ) {

        for (const position1 of Object.keys( this.looplinkExclusions[protein1Id] )) {

            counter += Object.keys(this.looplinkExclusions[protein1Id][position1]).length;
        }
    }

    return counter;
};

LinkExclusionHandler.prototype.getExcludedRenderedLooplinkCount = function( renderedLooplinks ) {

    let counter = 0;

    if( !renderedLooplinks ) { return 0; }

    for( const renderedLink of renderedLooplinks ) {

        const link = renderedLink.link;
        if( this.isLinkExcluded( link ) ) {
            counter++;
        }
    }

    return counter;
};

LinkExclusionHandler.prototype.toggleCrosslinkExclusion = function(proteinId1, proteinPosition1, proteinId2, proteinPosition2) {

    if( this.isCrosslinkExcluded( proteinId1, proteinPosition1, proteinId2, proteinPosition2 ) ) {
        this.removeCrosslinkExclusion( proteinId1, proteinPosition1, proteinId2, proteinPosition2 );
    } else {
        this.addCrosslinkExclusion( proteinId1, proteinPosition1, proteinId2, proteinPosition2 );
    }

};

LinkExclusionHandler.prototype.toggleLooplinkExclusion = function(proteinId, proteinPosition1, proteinPosition2) {

    if( this.isLooplinkExcluded( proteinId, proteinPosition1, proteinPosition2 ) ) {
        this.removeLooplinkExclusion( proteinId, proteinPosition1, proteinPosition2 );
    } else {
        this.addLooplinkExclusion( proteinId, proteinPosition1, proteinPosition2 );
    }

};


LinkExclusionHandler.prototype.addCrosslinkExclusion = function(proteinId1, proteinPosition1, proteinId2, proteinPosition2) {

    const sortedLinkInfo = this.getSortedCrosslinkInfo( proteinId1, proteinPosition1, proteinId2, proteinPosition2 );
    proteinId1 = sortedLinkInfo.proteinId1;
    proteinId2 = sortedLinkInfo.proteinId2;
    proteinPosition1 = sortedLinkInfo.proteinPosition1;
    proteinPosition2 = sortedLinkInfo.proteinPosition2;

    if( this.crosslinkExclusions[ proteinId1 ] == undefined ) {
        this.crosslinkExclusions[ proteinId1 ] = { };
    }

    if( this.crosslinkExclusions[ proteinId1 ][ proteinId2 ] == undefined ) {
        this.crosslinkExclusions[ proteinId1 ][ proteinId2 ] = { };
    }

    if( this.crosslinkExclusions[ proteinId1 ][ proteinId2 ][ proteinPosition1 ] == undefined ) {
        this.crosslinkExclusions[ proteinId1 ][ proteinId2 ][ proteinPosition1 ] = { };
    }

    // add it so we can quickly query it
    this.crosslinkExclusions[ proteinId1 ][ proteinId2 ][ proteinPosition1 ][ proteinPosition2 ] = 1;

};

LinkExclusionHandler.prototype.addLooplinkExclusion = function(proteinId, proteinPosition1, proteinPosition2) {

    const sortedLinkInfo = this.getSortedLooplinkInfo( proteinId, proteinPosition1, proteinPosition2 );

    proteinId = sortedLinkInfo.proteinId;
    proteinPosition1 = sortedLinkInfo.proteinPosition1;
    proteinPosition2 = sortedLinkInfo.proteinPosition2;

    if( this.looplinkExclusions[ proteinId ] == undefined ) {
        this.looplinkExclusions[ proteinId ] = { };
    }

    if( this.looplinkExclusions[ proteinId ][ proteinPosition1 ] == undefined ) {
        this.looplinkExclusions[ proteinId ][ proteinPosition1 ] = { };
    }

    // add it so we can quickly query it
    this.looplinkExclusions[ proteinId ][ proteinPosition1 ][ proteinPosition2 ] = 1;

};


LinkExclusionHandler.prototype.removeCrosslinkExclusion = function(proteinId1, proteinPosition1, proteinId2, proteinPosition2) {

    const sortedLinkInfo = this.getSortedCrosslinkInfo( proteinId1, proteinPosition1, proteinId2, proteinPosition2 );
    proteinId1 = sortedLinkInfo.proteinId1;
    proteinId2 = sortedLinkInfo.proteinId2;
    proteinPosition1 = sortedLinkInfo.proteinPosition1;
    proteinPosition2 = sortedLinkInfo.proteinPosition2;

    if( this.crosslinkExclusions[ proteinId1 ] == undefined ) {
        return;
    }

    if( this.crosslinkExclusions[ proteinId1 ][ proteinId2 ] == undefined ) {
        return;
    }

    if( this.crosslinkExclusions[ proteinId1 ][ proteinId2 ][ proteinPosition1 ] == undefined ) {
        return;
    }

    if( this.crosslinkExclusions[ proteinId1 ][ proteinId2 ][ proteinPosition1 ][ proteinPosition2 ] ) {
        delete this.crosslinkExclusions[proteinId1][proteinId2][proteinPosition1][proteinPosition2];

        // clean up the data structure

        if( Object.keys( this.crosslinkExclusions[proteinId1][proteinId2][proteinPosition1] ).length < 1 ) {
            delete this.crosslinkExclusions[proteinId1][proteinId2][proteinPosition1];
        }

        if( Object.keys( this.crosslinkExclusions[proteinId1][proteinId2] ).length < 1 ) {
            delete this.crosslinkExclusions[proteinId1][proteinId2];
        }

        if( Object.keys( this.crosslinkExclusions[proteinId1] ).length < 1 ) {
            delete this.crosslinkExclusions[proteinId1];
        }
    }

};

LinkExclusionHandler.prototype.removeLooplinkExclusion = function(proteinId, proteinPosition1, proteinPosition2) {

    const sortedLinkInfo = this.getSortedLooplinkInfo( proteinId, proteinPosition1, proteinPosition2 );
    proteinId = sortedLinkInfo.proteinId;
    proteinPosition1 = sortedLinkInfo.proteinPosition1;
    proteinPosition2 = sortedLinkInfo.proteinPosition2;

    if( this.looplinkExclusions[ proteinId ] == undefined ) {
        return;
    }

    if( this.looplinkExclusions[ proteinId ][ proteinPosition1 ] == undefined ) {
        return;
    }

    if( this.looplinkExclusions[ proteinId ][ proteinPosition1 ][ proteinPosition2 ] ) {
        delete this.looplinkExclusions[proteinId][proteinPosition1][proteinPosition2];

        // clean up the data structure

        if( Object.keys( this.looplinkExclusions[proteinId][proteinPosition1] ).length < 1 ) {
            delete this.looplinkExclusions[proteinId][proteinPosition1];
        }

        if( Object.keys( this.looplinkExclusions[proteinId] ).length < 1 ) {
            delete this.looplinkExclusions[proteinId];
        }
    }

};

LinkExclusionHandler.prototype.isLinkExcluded = function(link) {

    if( link.type === 'crosslink' ) {
        return this.isCrosslinkExcluded( link.protein1, link.position1, link.protein2, link.position2 );
    }

    if( link.type === 'looplink' ) {
        return this.isLooplinkExcluded( link.protein1, link.position1, link.position2 );
    }

    console.log( "warn: called isLinkExcluded but is not a crosslink or looplink." );
    return false;
};


LinkExclusionHandler.prototype.isCrosslinkExcluded = function(proteinId1, proteinPosition1, proteinId2, proteinPosition2) {

    const sortedLinkInfo = this.getSortedCrosslinkInfo( proteinId1, proteinPosition1, proteinId2, proteinPosition2 );
    proteinId1 = sortedLinkInfo.proteinId1;
    proteinId2 = sortedLinkInfo.proteinId2;
    proteinPosition1 = sortedLinkInfo.proteinPosition1;
    proteinPosition2 = sortedLinkInfo.proteinPosition2;

    if( this.crosslinkExclusions[ proteinId1 ] == undefined ) {
         return false;
    }

    if( this.crosslinkExclusions[ proteinId1 ][ proteinId2 ] == undefined ) {
        return false;
    }

    if( this.crosslinkExclusions[ proteinId1 ][ proteinId2 ][ proteinPosition1 ] == undefined ) {
        return false;
    }

    if( this.crosslinkExclusions[ proteinId1 ][ proteinId2 ][ proteinPosition1 ][ proteinPosition2 ] ) {
        return true;
    }

    return false;
};

LinkExclusionHandler.prototype.isLooplinkExcluded = function(proteinId, proteinPosition1, proteinPosition2) {

    const sortedLinkInfo = this.getSortedLooplinkInfo( proteinId, proteinPosition1, proteinPosition2 );
    proteinId = sortedLinkInfo.proteinId;
    proteinPosition1 = sortedLinkInfo.proteinPosition1;
    proteinPosition2 = sortedLinkInfo.proteinPosition2;

    if( this.looplinkExclusions[ proteinId ] == undefined ) {
        return false;
    }

    if( this.looplinkExclusions[ proteinId ][ proteinPosition1 ] == undefined ) {
        return false;
    }

    if( this.looplinkExclusions[ proteinId ][ proteinPosition1 ][ proteinPosition2 ] ) {
        return true;
    }

    return false;
};

LinkExclusionHandler.prototype.getSortedCrosslinkInfo = function(proteinId1, proteinPosition1, proteinId2, proteinPosition2) {

    let retObject = { };

    proteinId1 = parseInt( proteinId1 );
    proteinPosition1 = parseInt( proteinPosition1 );
    proteinId2 = parseInt( proteinId2 );
    proteinPosition2 = parseInt( proteinPosition2 );

    if( proteinId1 > proteinId2 ) {

        retObject.proteinId1 = proteinId2;
        retObject.proteinId2 = proteinId1;
        retObject.proteinPosition1 = proteinPosition2;
        retObject.proteinPosition2 = proteinPosition1;

    } else if( proteinId1 < proteinId2 ) {

        retObject.proteinId1 = proteinId1;
        retObject.proteinId2 = proteinId2;
        retObject.proteinPosition1 = proteinPosition1;
        retObject.proteinPosition2 = proteinPosition2;

    } else {

        retObject.proteinId1 = proteinId1;
        retObject.proteinId2 = proteinId2;

        if( proteinPosition1 > proteinPosition2 ) {
            retObject.proteinPosition1 = proteinPosition2;
            retObject.proteinPosition2 = proteinPosition1;
        } else {
            retObject.proteinPosition1 = proteinPosition1;
            retObject.proteinPosition2 = proteinPosition2;
        }
    }

    return retObject;
};

LinkExclusionHandler.prototype.getSortedLooplinkInfo = function(proteinId, proteinPosition1, proteinPosition2) {

    let retObject = { };

    proteinId = parseInt( proteinId );
    proteinPosition1 = parseInt( proteinPosition1 );
    proteinPosition2 = parseInt( proteinPosition2 );

    retObject.proteinId = proteinId;

    if( proteinPosition1 > proteinPosition2 ) {
        retObject.proteinPosition1 = proteinPosition2;
        retObject.proteinPosition2 = proteinPosition1;
    } else {
        retObject.proteinPosition1 = proteinPosition1;
        retObject.proteinPosition2 = proteinPosition2;
    }

    return retObject;
};


/************  Items related to the user interface ****************/

LinkExclusionHandler.prototype.addClickHandlerToCrosslinkToggles = function( _renderedLinks, drawStructureFunction, updateHashFunction ) {

    if (_renderedLinks['crosslinks'] && _renderedLinks['crosslinks'].length > 0) {

        for (let i = 0; i < _renderedLinks['crosslinks'].length; i++) {
            const link = _renderedLinks['crosslinks'][i]['link'];

            this.addClickHandlerToCrosslinkToggle(link, i, drawStructureFunction, updateHashFunction);
        }
    }
};

LinkExclusionHandler.prototype.addClickHandlerToLooplinkToggles = function( _renderedLinks, drawStructureFunction, updateHashFunction ) {

    if (_renderedLinks['looplinks'] && _renderedLinks['looplinks'].length > 0) {

        for (let i = 0; i < _renderedLinks['looplinks'].length; i++) {
            const link = _renderedLinks['looplinks'][i]['link'];

            this.addClickHandlerToLooplinkToggle(link, i, drawStructureFunction, updateHashFunction);
        }
    }
};


LinkExclusionHandler.prototype.addClickHandlerToCrosslinkToggle = function(link, index, drawStructureFunction, updateHashFunction ) {

    const $clickedElement = $('span#cross-link-show-toggle-index-' + index);
    const objectThis = this;

    $clickedElement.click( function( e ) {
        e.preventDefault();
        objectThis.processClickedLinkVisibility( link, updateHashFunction );
        drawStructureFunction();

        return false;
    });
};

LinkExclusionHandler.prototype.addClickHandlerToLooplinkToggle = function(link, index, drawStructureFunction, updateHashFunction ) {

    const $clickedElement = $('span#loop-link-show-toggle-index-' + index);
    const objectThis = this;

    $clickedElement.click( function( e ) {
        e.preventDefault();
        objectThis.processClickedLinkVisibility( link, updateHashFunction );
        drawStructureFunction();

        return false;
    });
};


LinkExclusionHandler.prototype.processClickedLinkVisibility = function( link, updateHashFunction ) {

    if( link.type === 'crosslink' ) {
        this.toggleCrosslinkExclusion(link.protein1, link.position1, link.protein2, link.position2);
    } else if( link.type === 'looplink' ) {
        this.toggleLooplinkExclusion(link.protein1, link.position1, link.position2);
    }

    const className = this.getClassNameForLink( link );
    $('tr.' + className).fadeTo( 30, this.getOpacityForLinkRow( link ) );
    $('span.' + className).html( this.getHTMLForLinkToggleLink( link ) );

    updateHashFunction();

};

LinkExclusionHandler.prototype.getHTMLForLinkToggleLink = function( link ) {

    const htmlForHiddenLink = '<img style=\"max-width:15px;\" src=\"images/icon-eye-hidden.png\" />';
    const htmlForShownLink = '<img style=\"max-width:15px;\" src=\"images/icon-eye-visible.png\" />';

    if( link.type === 'crosslink' && this.isCrosslinkExcluded( link.protein1, link.position1, link.protein2, link.position2 ) ) {
        return htmlForHiddenLink;
    } else if( link.type === 'looplink' && this.isLooplinkExcluded( link.protein1, link.position1, link.position2 ) ) {
        return htmlForHiddenLink;
    }

    return htmlForShownLink;
};

LinkExclusionHandler.prototype.getOpacityForLinkRow = function( link ) {

    const opacityForHiddenLink = 0.5;
    const opacityForShownLink = 1;

    if( link.type === 'crosslink' && this.isCrosslinkExcluded( link.protein1, link.position1, link.protein2, link.position2 ) ) {
        return opacityForHiddenLink;
    } else if( link.type === 'looplink' && this.isLooplinkExcluded( link.protein1, link.position1, link.position2 ) ) {
        return opacityForHiddenLink;
    }

    return opacityForShownLink;
};


LinkExclusionHandler.prototype.getClassNameForLink = function( link ) {

    if( link.type === 'crosslink' ) {
        return this.getClassNameForCrosslink( link );
    }

    if( link.type === 'looplink' ) {
        return this.getClassNameForLooplink( link );
    }

    return '';

};

LinkExclusionHandler.prototype.getClassNameForCrosslink = function( link ) {
    return 'c' + link.protein1 + '-' + link.position1 + '-' + link.protein2 + '-' + link.position2;
};

LinkExclusionHandler.prototype.getClassNameForLooplink = function( link ) {
    return 'l' + link.protein1 + '-' + link.position1 + '-' + link.position2;
};



/************  Items related to encoding and decoding the hash in the URL ****************/


LinkExclusionHandler.prototype.getDataStructureForHash = function( ) {

    let dataStructure = { }

    if( this.getExcludedCrosslinkCount() != 0 ) {
        dataStructure.c = this.getCrosslinkDataStructureForHash();
    }

    if( this.getExcludedLooplinkCount() != 0 ) {
        dataStructure.l = this.getLooplinkDataStructureForHash();
    }

    return dataStructure;
};


LinkExclusionHandler.prototype.getCrosslinkDataStructureForHash = function( ) {

    let dataStructure = { };

    for( const protein1Id of Object.keys( this.crosslinkExclusions ) ) {

        const shortProtein1Id = parseInt( protein1Id).toString( 36 );
        dataStructure[ shortProtein1Id ] = { };

        for( const protein2Id of Object.keys( this.crosslinkExclusions[ protein1Id ] ) ) {


            const diff = parseInt( protein1Id ) - parseInt( protein2Id );
            const shortDiff = diff.toString( 36 );

            dataStructure[ shortProtein1Id ][ shortDiff ]= { };

            for (const position1 of Object.keys( this.crosslinkExclusions[protein1Id][protein2Id]) ) {

                const shortPosition1 = parseInt( position1).toString( 36 );

                dataStructure[ shortProtein1Id ][ shortDiff ][ shortPosition1 ] =
                    this.getMinifiedIntArray( Object.keys( this.crosslinkExclusions[protein1Id][protein2Id][position1] ).map( x=> parseInt(x) ) );
            }
        }
    }

    return dataStructure;
};

LinkExclusionHandler.prototype.getLooplinkDataStructureForHash = function( ) {

    let dataStructure = { };

    for( const protein1Id of Object.keys( this.looplinkExclusions ) ) {

        const shortProtein1Id = parseInt( protein1Id).toString( 36 );
        dataStructure[ shortProtein1Id ] = { };

        for (const position1 of Object.keys( this.looplinkExclusions[protein1Id]) ) {

            const shortPosition1 = parseInt( position1).toString( 36 );

            dataStructure[ shortProtein1Id ][ shortPosition1 ] =
                this.getMinifiedIntArray( Object.keys( this.looplinkExclusions[protein1Id][position1] ).map( x=> parseInt(x) ) );
        }
    }

    return dataStructure;
};


LinkExclusionHandler.prototype.populateDataFromJSON = function( jsonData ) {

    this.init();

    if( !jsonData.le ) { return; }

    if( jsonData.le.c ) {
        this.populateCrosslinkDataFromJSON( jsonData );
    }

    if( jsonData.le.l ) {
        this.populateLooplinkDataFromJSON( jsonData );
    }

};

LinkExclusionHandler.prototype.populateCrosslinkDataFromJSON = function( jsonData ) {

    const crosslinkData = jsonData.le.c;

    for( const shortProtein1Id of Object.keys( crosslinkData ) ) {

        const protein1Id = parseInt( shortProtein1Id, 36 );

        for( const shortDiff of Object.keys( crosslinkData[ shortProtein1Id ] ) ) {

            const diff = parseInt( shortDiff, 36 );
            const protein2Id = protein1Id - diff;

            for( const shortPosition1 of Object.keys( crosslinkData[ shortProtein1Id ][ shortDiff ] ) ) {

                const position1 = parseInt( shortPosition1, 36 );

                for( const position2 of this.getExpandedIntArray( crosslinkData[ shortProtein1Id ][ shortDiff ][ shortPosition1 ] ) ) {

                    this.addCrosslinkExclusion( protein1Id, position1, protein2Id, position2 );
                }
            }
        }
    }

};

LinkExclusionHandler.prototype.populateLooplinkDataFromJSON = function( jsonData ) {

    const looplinkData = jsonData.le.l;

    for( const shortProtein1Id of Object.keys( looplinkData ) ) {

        const protein1Id = parseInt( shortProtein1Id, 36 );

        for( const shortPosition1 of Object.keys( looplinkData[ shortProtein1Id ] ) ) {

            const position1 = parseInt( shortPosition1, 36 );

            for( const position2 of this.getExpandedIntArray( looplinkData[ shortProtein1Id ][ shortPosition1 ] ) ) {

                this.addLooplinkExclusion( protein1Id, position1, position2 );
            }
        }
    }
};

LinkExclusionHandler.prototype.getMinifiedIntArray = function( intArray ) {

    intArray = intArray.sort((a, b) => a - b);

    let lastNumber = 0;
    let newArray = [];

    for( const value of intArray ) {
        newArray.push( value - lastNumber );
        lastNumber = value;
    }


    return newArray;

};

LinkExclusionHandler.prototype.getExpandedIntArray = function( minifiedIntArray ) {

    let lastNumber = 0;
    let newArray = [];

    for( const value of minifiedIntArray ) {

        newArray.push( value + lastNumber );
        lastNumber = value + lastNumber;
    }

    return newArray;
};


export { LinkExclusionHandler };
