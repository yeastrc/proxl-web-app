"use strict";

/**
 * The color manager hands out colors to be used for protein bars and links
 * in the image viewer.
 * 
 * @constructor
 * @author Michael Riffle <mriffle@uw.edu>
 * 
 * !!! The following global variables from "crosslink-image-viewer.js" are used in this file:
 * 
 * _indexManager
 * _imageProteinBarDataManager
 * _linkExclusionDataManager
 * 
 */
var ColorManager = function() {	
	
	this.theme = new ColorManager.PrettyTheme();
	
};

/**
 * Get the hex code to use for coloring the scale bars and sequence coverage
 * associated with a given UID
 * 
 * @param {string} uid The UID of the protein bar
 * @returns {object} { hex:hex code, opacity:#}
 * 
 */
ColorManager.prototype.getColorForUIDAnnotation = function( uid ) {
	try {
		return this.theme.getColorForUIDAnnotation( uid );
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
}

/**
 * Get the hex code to use for coloring the protein bar with the given uid. Will
 * call a method with the same name in set theme.
 * 
 * @param {string} uid The UID of the protein bar
 * @returns {object} { hex:hex code, opacity:#}
 * 
 */
ColorManager.prototype.getColorForUID = function( uid ) {
	try {
		return this.theme.getColorForUID( uid );
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
}

/**
 * Get the hex code to use for coloring the protein bar with the given uid. Will
 * call a method with the same name in set theme.
 * 
 * @param {object} region A region in the form of { uid:uid, start:start, end:end }
 * @returns {object} { hex:hex code, opacity:#}
 * 
 */
ColorManager.prototype.getColorForRegion = function( region ) {
	try {
		return this.theme.getColorForRegion( region );
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
}

/**
 * Convenience method for getting a color based on index number instead
 * of UID. Looks up the UID for that index and calls getColorForUID
 * 
 * @param {number} index The index (starting at 0) of the protein bar
 * @returns {object} { hex:hex code, opacity:#}
 * 
 */
ColorManager.prototype.getColorForIndex = function( index ) {
	var uid = _indexManager.getUIDForIndex( index );
	return this.getColorForUID( uid );
}

/**
 * Get the hex code to use for coloring a link. Will
 * call a method with the same name in set theme.
 * 
 * @param {object} link The link we're coloring. Should have the form:
 * 				{
 * 					type:"crosslink" (or looplink, or monolink)
 * 					uid1:"uid" - uid of originating link
 * 					position1:# - position of originating link
 * 					uid2:"uid" - uid of of destination
 * 					position2:# - position of destination
 * 				}
 * 
 * @returns @returns {object} { hex:hex code, opacity:#}
 * 
 */
ColorManager.prototype.getColorForLink = function( link ) {
	try {
		return this.theme.getColorForLink( link );
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
};

/**
 * Get the color object ( { hex: hexcode, opacity: opacity } ) to be used for coloring
 * the supplied combination of search ids.
 * 
 * @param {array} searches The search list
 * @returns {object} { hex:hex code, opacity:#}
 * 
 */
ColorManager.prototype.getColorForSearches = function( searches ) {
	try {
		return this.theme.getColorForSearches( searches );
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
};

/*

 * Theme Definitions
 * 
 * Each theme should define a class that implements the following methods:
 * 
 * getColorForUID( uid )
 * 		@param uid The UID from the index manager corresponding to a protein bar
 * 		@return hex code for the color to use for a given protein bar
 * 
 * getColorForLink( link )
 * 		@param link The link we want to color. link is an object with the following parameters:
 * 				{
 * 					type:"crosslink" (or looplink, or monolink)
 * 					uid1:"uid" - uid of originating link
 * 					position1:# - position of originating link
 * 					uid2:"uid" - uid of of destination
 * 					position2:# - position of destination
 * 				}
 * 				
 * 				If type is monolink, only uid1 and position1 have values
 * 				If type is looplink, only uid1, position1, and position2 have values
 * 				if type is crosslink, all parameters must have values
 * 
 * 		@returns {object} { hex:hex code, opacity:#}
 * 
 *  getColorForSearches( searches )
 *       @param searches A list of search IDs
 *
*/

/*
 * Pretty Theme - uses a "pretty" coloring algorithm that automagically determines the colors to use
 * based on how many items there are in the index.
 */

/**
 * Construct a PrettyTheme object
 * @constructor
 * @author Michael Riffle <mriffle@uw.edu> 
 */
ColorManager.PrettyTheme = function() { };

ColorManager.PrettyTheme.CONSTANTS = {
		_NON_HIGHLIGHTED_COLOR: "#868686",			// the color of non-highlighted bars and lines
		
		// colors to use when coloring by search
		_SEARCH_COLORS: {
			"1" : "#995d5d",	// redish
			"2" : "#5d6499",	// blueish
			"3" : "#b5af00",	// yellowish
			
			"12" : "#cd83c7",	// purpleish
			"13" : "#35bec0",	// orangeish
			"23" : "#5d9960",	// greenish
			
			"123" : "#3a3a3a",	// grey
		},
};

/**
 * Get the hex code to use for coloring a link.
 * 
 * @param {object} link The link we're coloring. Should have the form:
 * 				{
 * 					type:"crosslink" (or looplink, or monolink)
 * 					uid1:"uid" - uid of originating link
 * 					position1:# - position of originating link
 * 					uid2:"uid" - uid of of destination
 * 					position2:# - position of destination
 * 				}
 * 
 * @returns {object} { hex:hex code, opacity:#}
 * 
 */
ColorManager.PrettyTheme.prototype.getColorForLink = function( link ) {	
	
	// Ensure we have the data we need and that it has expected values
	ColorManager.validateLink( link );
	
	// if no highlighting is going on, just return the normal color
	if( !_imageProteinBarDataManager.isAnyProteinBarsHighlighted() ) {	
		return this.getColorForHighlightedLink( link );		
	}
	
	//  Check exclusions
	if( link.type === "crosslink" ) {
		if ( _linkExclusionDataManager.isLinkExcludedForProteinUIDProteinPosition( {
				proteinUID_1 : link.uid1, proteinPosition_1 : link.position1,
				proteinUID_2 : link.uid2, proteinPosition_2 : link.position2 } ) ) {
			return this.getColorForNonHighlightedItem();
		}		
	}
	if( link.type === "looplink" ) {
		if ( _linkExclusionDataManager.isLinkExcludedForProteinUIDProteinPosition( {
			proteinUID_1 : link.uid1, proteinPosition_1 : link.position1,
			proteinUID_2 : link.uid1, proteinPosition_2 : link.position2 } ) ) {
			return this.getColorForNonHighlightedItem();
		}		
	}

	
	var barEntry1 = _imageProteinBarDataManager.getItemByUID( link.uid1 );
	var barEntry2 = undefined;			//undefined if monolink or looplink
	if( link.type === "crosslink" ) {
		barEntry2 = _imageProteinBarDataManager.getItemByUID( link.uid2 );		
	}
	
	// whether only one region is highlighted, or if multiple, monolinks
	// will be highlighted if the linked position is in a highlighted
	// region
	if( link.type === "monolink" ) {
		if( barEntry1.isAllOfProteinBarHighlighted() || barEntry1._isProteinBarHighlightedAtSinglePosition( link.position1 ) ) {
			return this.getColorForHighlightedLink( link )
		} else {
			return this.getColorForNonHighlightedItem();
		}
	}
	
	// if exactly one region is highlighted, any link that has either end
	// on that region should be highlighted
	if( ColorManager.getAllRegions().length === 1 ) {
		
		if( link.type === "looplink" ) {
			if( barEntry1.isAllOfProteinBarHighlighted() ||
				barEntry1._isProteinBarHighlightedAtSinglePosition( link.position1 ) ||
				barEntry1._isProteinBarHighlightedAtSinglePosition( link.position2 ) ) {
				return this.getColorForHighlightedLink( link )
			} else {
				return this.getColorForNonHighlightedItem();
			}
		}
		
		if( link.type === "crosslink" ) {
			
			// self crosslink
			if( link.uid1 === link.uid2 ) {
				if( barEntry1.isAllOfProteinBarHighlighted() ||
					barEntry1._isProteinBarHighlightedAtSinglePosition( link.position1 ) ||
					barEntry1._isProteinBarHighlightedAtSinglePosition( link.position2 ) ) {
						return this.getColorForHighlightedLink( link )
					} else {
						return this.getColorForNonHighlightedItem();
					}				
			}
			
			// cross link between two different protein bars
			
			// barEntry1 is the highlighted one
			if( barEntry1.isAllOfProteinBarHighlighted() ||
				barEntry1._isProteinBarHighlightedAtSinglePosition( link.position1 ) ) {
				
				return this.getColorForHighlightedLink( link )
				
			// barEntry2 is the highlighted one
			} else if( barEntry2.isAllOfProteinBarHighlighted() ||
					   barEntry2._isProteinBarHighlightedAtSinglePosition( link.position2 ) ) {
				
				return this.getColorForHighlightedLink( link )
			}
			
			// neither are highlighted
			else {
				return this.getColorForNonHighlightedItem();
			}
		}
	}
	
	// more than one region is highlighted. only highlight if both ends of a link
	// is contained in a highlighted region	
	
	// handle looplinks and intra-protein cross-links
	if( link.type === "looplink" || link.uid1 === link.uid2 ) {
		if( barEntry1.isAllOfProteinBarHighlighted() ||
			( barEntry1._isProteinBarHighlightedAtSinglePosition( link.position1 ) &&
			  barEntry1._isProteinBarHighlightedAtSinglePosition( link.position2 ) ) ) {
			return this.getColorForHighlightedLink( link )
		} else {
			return this.getColorForNonHighlightedItem();
		}
	}
	
	// handle inter-protein cross-links
	if( link.type === "crosslink" ) {

		if( ( barEntry1.isAllOfProteinBarHighlighted() || barEntry1._isProteinBarHighlightedAtSinglePosition( link.position1 ) ) && 
		    ( barEntry2.isAllOfProteinBarHighlighted() || barEntry2._isProteinBarHighlightedAtSinglePosition( link.position2 ) ) ) {
			
			return this.getColorForHighlightedLink( link );
		} else {		
			return this.getColorForNonHighlightedItem();
		}
	}
};

/**
 * Get the hex code to use for coloring the scale bar and sequence coverage
 * associated with the given uid
 * 
 * @param {string} uid The UID of the protein bar
 * @returns {object} { hex:hex code, opacity:#}
 * 
 */
ColorManager.PrettyTheme.prototype.getColorForUIDAnnotation = function( uid ) {
	
	var barData = _imageProteinBarDataManager.getItemByUID( uid );
	
	if( _colorLinesBy !== SELECT_ELEMENT_COLOR_BY_REGION ) {
		
		if( !_imageProteinBarDataManager.isAnyProteinBarsHighlighted() || 
			barData.isAllOfProteinBarHighlighted() ||
			barData.isAnyOfProteinBarHighlighted() ) {
			
			return this.getColorForHighlightedUID( uid );
		}
		
		return this.getColorForNonHighlightedItem();
	}
	
	return this.getColorForUID( uid );
}

/**
 * Get the hex code to use for coloring the protein bar with the given uid.
 * 
 * @param {string} uid The UID of the protein bar
 * @returns {object} { hex:hex code, opacity:#}
 * 
 */
ColorManager.PrettyTheme.prototype.getColorForUID = function( uid ) {
	
	if( _imageProteinBarDataManager.isAnyProteinBarsHighlighted() ) {
		
		var barData = _imageProteinBarDataManager.getItemByUID( uid );
		
		if( !barData ) {
			throw Error( "Could not find protein bar data for uid: " + uid );
		}
		
		if( barData.isAllOfProteinBarHighlighted() ) {
			
			// if we're coloring by region, treat this protein bar as a region
			if( _colorLinesBy === SELECT_ELEMENT_COLOR_BY_REGION ) {
				
				var region = { };
				
				region.uid = uid;
				region.start = 1;
				region.end = _proteinLengths.getProteinLength( _indexManager.getProteinIdForUID( uid ) );
				
				return this.getColorForRegion( region );				
				
			} else {
				return this.getColorForHighlightedUID( uid );	// there is highlighting going on and this protein bar is highlighted
			}
		} else {
			return this.getColorForNonHighlightedItem();	// there is highlighting going on and this protein bar is NOT highlighted
		}
	} else {
		return this.getColorForHighlightedUID( uid );		// no highlighting going on, so everything is highlighted
	}
};

/**
 * Get the color to use for a link if it's highlighted, or if there are
 * no highlighted bars
 * 
 * @param {object} link The link we're highlighting
 * @returns {object} The color to use for the link
 * 
 */
ColorManager.PrettyTheme.prototype.getColorForHighlightedLink = function( link ) {
	
	var color;
	
	// color by search
	if( _colorLinesBy === SELECT_ELEMENT_COLOR_BY_SEARCH ) {

		color =  this.getColorForLinkUsingSearch( link );
		
	} else if( _colorLinesBy === SELECT_ELEMENT_COLOR_BY_REGION ) {

		color =  this.getColorForLinkUsingRegions( link );
	} else {
		color =  this.getColorForLinkUsingUID( link );
	}
	
	// if shading by counts, set the transparency of this color appropriately	
	color.opacity = ColorManager.getOpacityForHighlightedLink( link );
	
	return color;
};

/**
 * Get the color to use for a link based on its UID
 * 
 * @param {object} link The link we're highlighting
 * @returns {object} The color to use for the link
 * 
 */
ColorManager.PrettyTheme.prototype.getColorForLinkUsingUID = function ( link ) {
		
	if( link.type === "monolink" ||
		link.type === "looplink" ||
		link.uid1 === link.uid2 ||
		ColorManager.isLinkedPositionHighlighted( link.uid1, link.position1 ) ) {
		
		return this.getColorForHighlightedUID( link.uid1 );

	} else if( ColorManager.isLinkedPositionHighlighted( link.uid2, link.position2 ) ) {
		
		return this.getColorForHighlightedUID( link.uid2 );
	} else {		
		throw Error( "Was told to highlight link by protein (uid), but neither end of it is highlighted?" );
	}
};


/**
 * Get the color to use for a link based on the searches in which it is found
 * 
 * @param {object} link The link we're highlighting
 * @returns {object} The color to use for the link
 * 
 */
ColorManager.PrettyTheme.prototype.getColorForLinkUsingSearch = function ( link ) {
	
	var searches;
	
	if( link.type === "crosslink" ) { searches = findSearchesForCrosslink( link.protein1, link.protein2, link.position1, link.position2 ); }
	else if( link.type === "looplink" ) { searches = findSearchesForLooplink( link.protein1, link.position1, link.position2 ); }
	else if( link.type === "monolink" ) { searches = findSearchesForMonolink( link.protein1, link.position1 ); }
	
	if( !searches || searches.length < 1 ) {
		throw Error( "Unable to find searches for link: " + link );
	}
	
	return this.getColorForSearches( searches );
};


/**
 * Get the color to use for a link based on the regions it which it is found
 * 
 * @param {object} link The link we're highlighting
 * @returns {object} The color to use for the link
 * 
 */
ColorManager.PrettyTheme.prototype.getColorForLinkUsingRegions = function ( link ) {
	
	if( link.type === "monolink" || ColorManager.isLinkedPositionHighlighted( link.uid1, link.position1 ) ) {
		
		var region1 = ColorManager.getRegionForUIDAndPosition( link.uid1, link.position1 );
		
		if( !region1 ) {
			
			console.log( link );
			console.log( ColorManager.getAllRegions() );
			
			throw Error( "Could not find region while coloring by region for: uid: " + link.uid1 + ", position:" + link.position1 );
		}
		
		return this.getColorForRegion( region1 );		
		
	} else if( ColorManager.isLinkedPositionHighlighted( link.uid2, link.position2 ) ) {
		
		var region2 = ColorManager.getRegionForUIDAndPosition( link.uid2, link.position2 );

		if( !region2 ) {
			throw Error( "Could not find region while coloring by region for: uid: " + link.uid2 + ", position:" + link.position2 );
		}
		
		return this.getColorForRegion( region2 );	
		
	} else {
		
		throw Error( "Was told to highlight link by region, but neither end of it is highlighted?" );
		
	}
};




/**
 * Get the color to use for the given list of searches
 * 
 * @param {array} An array of search IDs
 * @returns {object} The color to use.
 */
ColorManager.PrettyTheme.prototype.getColorForSearches = function( searches ) {
	
	var colorIndex = "";
	
	for ( var i = 0; i < _searches.length; i++ ) {
		for ( var k = 0; k < searches.length; k++ ) {
			if ( _searches[i]['id'] === searches[ k ] ) {
				colorIndex += ( i + 1 );
				break;
			}
		}
	}
		
	return { hex:ColorManager.PrettyTheme.CONSTANTS._SEARCH_COLORS[ colorIndex ], opacity:1 };
};


/**
 * Get the color to use for a UID bar if it's highlighted, or if there are
 * no highlighted bars.
 * 
 * @param {string} uid The UID of the bar to test
 * @returns {object} The color object to use.
 */
ColorManager.PrettyTheme.prototype.getColorForHighlightedUID = function( uid ) {
		
	var index = _indexManager.getIndexForUID( uid );
		
	if( index < 0 ) {
		throw Error( "Could not find uid: " + uid + " in index manager." );
	}
	
	var number = _indexManager.getProteinList().length;
	
	var hueDivider = 360 / number;
	var saturation = 0.39;
	var brightness = 0.60;

	var hue =  (360 - (hueDivider * index ) - 1) / 360;
		
	var color = Snap.hsb2rgb( hue, saturation, brightness );
		
	return {
		hex:color.hex,
		opacity:1
	};
	
}

/**
 * Get the hex code to use for coloring a supplied highlighted region
 * 
 * @param {object} region A region in the form of { uid:uid, start:start, end:end }
 * @returns {object} { hex:hex code, opacity:#}
 * 
 */
ColorManager.PrettyTheme.prototype.getColorForRegion = function( region ) {
		
	if( _colorLinesBy !== SELECT_ELEMENT_COLOR_BY_REGION ) {
		return this.getColorForHighlightedUID( region.uid );
	}
	
	var index = ColorManager.getIndexOfRegion( region );
		
	if( index < 0 ) {
		
		console.log( ColorManager.getAllRegions() );
		console.log( region );
		
		throw Error( "Could not find index for region." );
	}
	
	var number = ColorManager.getAllRegions().length;
	
	var hueDivider = 360 / number;
	var saturation = 0.39;
	var brightness = 0.60;

	var hue =  (360 - (hueDivider * index ) - 1) / 360;
		
	var color = Snap.hsb2rgb( hue, saturation, brightness );
		
	return {
		hex:color.hex,
		opacity:1
	};
	
};


/**
 * Get the color to use for a non-highlighted item
 */
ColorManager.PrettyTheme.prototype.getColorForNonHighlightedItem = function() {
	return {
		hex:ColorManager.PrettyTheme.CONSTANTS._NON_HIGHLIGHTED_COLOR,
		opacity:0.25
	};
};




/********************
 * Static helper functions
 ******************************/

/**
 * Find the region corresponding to the given uid and position.
 * 
 * @param {string} uid The UID to use
 * @param {number} position The position to use
 * @returns {object} The region ( { uid:uid,start:start,end:end } ) found. null if nothing found
 */
ColorManager.getRegionForUIDAndPosition = function( uid, position ) {
	
	// if nothing is explicitly highlighted, everything is highlighted
	if( !_imageProteinBarDataManager.isAnyProteinBarsHighlighted() ) {
		
		var region = { };
		
		region.uid = uid;
		region.start = 1;
		region.end = _proteinLengths.getProteinLength( _indexManager.getProteinIdForUID( uid ) );
			
		return region;
	}
	
	var barData = _imageProteinBarDataManager.getItemByUID( uid );	
	
	if( !barData ) {
		throw Error( "Unable to find protein bar data for uid: " + uid );
	}
		
	if( barData.isAllOfProteinBarHighlighted() ) {
			
		var region = { };
			
		region.uid = uid;
		region.start = 1;
		region.end = _proteinLengths.getProteinLength( _indexManager.getProteinIdForUID( uid ) );
			
		return region;
	}
		
	var barDataRegions = barData.getProteinBarHighlightedRegionsArray();
		
	for( var b = 0; b < barDataRegions.length; b++ ) {
			
		var bdRegion = barDataRegions[ b ];

		if( position >= bdRegion.s && position <= bdRegion.e ) {
				
			var region = { };
				
			region.uid = uid;
			region.start = bdRegion.s;
			region.end = bdRegion.e;
				
			return region;				
		}
			
	}
	
	return null;	
}


/**
 * Determine whether the supplied position in the supplied uid is currently highlighted
 * 
 * @param {string} uid The UID to check
 * @param {number} The position in that UID (starting at 1)
 * @returns {boolean} True if that position in that UID is highlighted, false if not
 */
ColorManager.isLinkedPositionHighlighted = function( uid, position ) {

	// if nothing is explicitly highlighted, everything is highlighted
	if( !_imageProteinBarDataManager.isAnyProteinBarsHighlighted() ) {	
		return true;
	}
	
	var barData = _imageProteinBarDataManager.getItemByUID( uid );	
	
	if( !barData ) {
		throw Error( "Unable to find protein bar data for uid: " + uid );
	}
		
	if( barData.isAllOfProteinBarHighlighted() ) {
		return true;
	}
	
	if( !barData.isAnyOfProteinBarHighlighted() ) {
		return false;
	}
	
	if( barData._isProteinBarHighlightedAtSinglePosition( position ) ) {
		return true;
	}
	
	return false;	
	
}

/**
 * Get all highlighted regions currently defined 
 * 
 * @returns A list of objects with the form of { uid:uid, start:start, end:end }
 */
ColorManager.getAllRegions = function() {

	var entries = _indexManager.getProteinArray();
	var regions = [ ];
	
	for( var i = 0; i < entries.length; i++ ) {
		
		var uid = entries [ i ].uid;
		var pid = entries [ i ].pid;
		
		// if nothing is explicitly highlighted, everything is highlighted
		if( !_imageProteinBarDataManager.isAnyProteinBarsHighlighted() ) {	
			var region = { };
			
			region.uid = uid;
			region.start = 1;
			region.end = _proteinLengths.getProteinLength( pid );
			
			if( !region.end ) {
				throw Error( "Unable to find protein length for protein: " + pid );
			}
			
			regions.push( region );	

		} else {
		
			var barData = _imageProteinBarDataManager.getItemByUID( uid );		
			
			if( !barData ) {
				throw Error( "Unable to find protein bar data for uid: " + uid );
			}
	
			// nothing to do in this case
			if( !barData.isAnyOfProteinBarHighlighted() ) {
				continue;
			}
			
			if( barData.isAllOfProteinBarHighlighted() ) {
				
				var region = { };
				
				region.uid = uid;
				region.start = 1;
				region.end = _proteinLengths.getProteinLength( pid );
				
				if( !region.end ) {
					throw Error( "Unable to find protein length for protein: " + pid );
				}
				
				regions.push( region );			
	
			} else {
				
				var barDataRegions = barData.getProteinBarHighlightedRegionsArray();
								
				// iterate over the regions, add each one to the regions array
				for( var b = 0; b < barDataRegions.length; b++ ) {
					
					var bdRegion = barDataRegions[ b ];
					
					var region = { };
					
					region.uid = uid;
					region.start = bdRegion.s;
					region.end = bdRegion.e;
					
					regions.push( region );				
				}
				
			}
		}
		
	}
	
	
	return regions;	
};

/**
 * Get the index of the supplied region, -1 if no such region exists
 * 
 * @param {object} region The region to test
 * @returns {number} The index of the supplied region, -1 if not found
 */
ColorManager.getIndexOfRegion = function( region ) {
	
	var regions = ColorManager.getAllRegions();
	
	for( var i = 0; i < regions.length; i++ ) {
		
		if( region.uid === regions[ i ].uid &&
			region.start === regions[ i ].start &&
			region.end === regions[ i ].end ) {
			
			return i;
		}
		
	}
	
	return -1;	
};

/**
 * Get the opacity to use for drawing this link.
 * 
 * @param {Object} link - The link for which we need the opacity
 * @param {string} link.type - The type of the link (crosslink, looplink, or monolink)
 * @param {string} link.uid1 - The uid (protein bar) from which the link is originating
 * @param {number} link.position1 - The position in the protein (starting at 1) of the originating link
 * @param {string} link.uid2 - The uid (protein bar) to which the link is terminating
 * @param {number} link.position2 - The position in the protein (starting at 1) of the destination link
 * @returns {number} The opacity (0.0 to 1.0) to use for the link.
 * 
 */
ColorManager.getOpacityForHighlightedLink = function( link ) {

	if ( $( "input#shade-by-counts" ).is( ':checked' ) ) {

		if( !link ) {
			throw Error( "Supposed to set opacity based on PSM count, but got no link. Setting opacity to 1." );
		}

		var numPsms = 0;

		try {
			if( link.type == 'crosslink' ) {

				numPsms = _linkPSMCounts[ 'crosslink' ][ link.protein1 ][ link.protein2 ][ link.position1 ][ link.position2 ];				

			} else if( link.type == 'looplink' ) {

				try {
					numPsms = _linkPSMCounts[ 'looplink' ][ link.protein1 ][ link.protein1 ][ link.position1 ][ link.position2 ];
				} catch( err ) { }
				
				if( !numPsms ) {
					try {
						numPsms = _linkPSMCounts[ 'looplink' ][ link.protein1 ][ link.protein1 ][ link.position2 ][ link.position1 ];
					} catch( err ) { }
				}

			} else if( link.type == 'monolink' ) {

				numPsms = _linkPSMCounts[ 'monolink' ][ link.protein1 ][ link.position1 ];	

			} else {

				throw Error( "Supposed to set opacity based on PSM count, but link has no valid type. Setting to 1. link: " );
			
			}
		} catch( err ) {

			throw Error( "Got error getting PSM count: " + err.message );				
		}


		if( !numPsms ) {

			throw Error( "Supposed to set opacity based on PSM count, but got no PSMs for link. " );			
		}

		var min = 0.1;
		var max = 1.0;
		var countForMaxOpacity = 10;

		if( numPsms > countForMaxOpacity ) { numPsms = countForMaxOpacity; }

		var opacity = max - ( ( max - min ) * ( ( countForMaxOpacity - numPsms ) / countForMaxOpacity ) );
		if( opacity < min || opacity > max ) {
			throw( "Invalid opacity: " + opacity  + " (numPsms: " + numPsms + ")" );
		}

		return opacity;

	} else {

		return 1.0;
	}
	
}


/**
 * Very anally ensure the link sent has all expected values and that they're valid
 */
ColorManager.validateLink = function( link ) {
		
	if( !link.hasOwnProperty( "type" ) ) {
		throw Error( "type does not exist." );
	} else {
		if( link.type !== "monolink" && link.type !== "looplink" && link.type !== "crosslink" ) {
			throw Error( "type must be monolink, looplink, or crosslink" );
		}
	}
	
	if( !link.hasOwnProperty( "uid1" ) ) {
		throw Error( "uid1 does not exist." );
	} else {
		if( !_indexManager.containsUID( link.uid1 ) ) {
			throw Error( "invalid uid1 sent: " + link.uid1 );
		}
		
		if( !_imageProteinBarDataManager.getItemByUID( link.uid1 ) ) {
			throw Error( "no protein bar data for uid1: " + link.uid1 );
		}
	}
	
	if( !link.hasOwnProperty( "position1" ) ) {
		throw Error( "position1 does not exist." );
	} else {
		if( typeof link.position1 !== "number" ) {
			throw Error( "position1 must be a number" );
		} else {
			var uidForPosition1 = link.uid1;
			var proteinIdForPosition1 = _indexManager.getProteinIdForUID( uidForPosition1 );
			var proteinLengthForPosition1 = _proteinLengths.getProteinLength( proteinIdForPosition1 );
			if( link.position1 < 1 || link.position1 > proteinLengthForPosition1 ) {
				throw Error( "position 1 is not in the valid range for uid (1-" + 
						proteinLengthForPosition1 + "), uid: " + uidForPosition1 );
			}
		}
	}
	
	if( link.type === "crosslink" || link.type ==="looplink" ) {
		
		if( link.type === "crosslink" && !link.hasOwnProperty( "uid2" ) ) {
			throw Error( "uid2 does not exist." );
		} else if( link.type === "crosslink" ) {
			if( !_indexManager.containsUID( link.uid2 ) ) {
				throw Error( "invalid uid2 sent: " + link.uid2 );
			}
			if( !_imageProteinBarDataManager.getItemByUID( link.uid2 ) ) {
				throw Error( "no protein bar data for uid2: " + link.uid2 );
			}
		}
		
		if( !link.hasOwnProperty( "position2" ) ) {
			throw Error( "position2 does not exist." );
		} else {
			if( typeof link.position2 !== "number" ) {
				throw Error( "position2 must be a number" );
			} else {
				var uidForPosition2 = link.uid2; //  uid for position 2 for crosslink
				if ( link.type ==="looplink" && uidForPosition2 === undefined ) {
					uidForPosition2 = link.uid1; //  uid for position 2 for looplink
				}
				var proteinIdForPosition2 = _indexManager.getProteinIdForUID( uidForPosition2 );
				var proteinLengthForPosition2 = _proteinLengths.getProteinLength( proteinIdForPosition2 );
				if( link.position2 < 1 || link.position2 > proteinLengthForPosition2 ) {
					throw Error( "position 2 is not in the valid range for uid (1-" + 
							proteinLengthForPosition2 + "), uid: " + uidForPosition2 );
				}
			}
		}

	}
}


