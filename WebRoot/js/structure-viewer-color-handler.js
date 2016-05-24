

"use strict";

var LinkColorHandler = function() { };

/**
 * Constants for link colors
 */
LinkColorHandler.prototype._CONSTANTS = {
		
		// default distances associated with distance-based coloring (in Angstroms)
		linkers : {
			dss : {
				shortDistance : 25,
				longDistance : 35
			},
			bs3 : {
				shortDistance : 25,
				longDistance : 35
			},
			dsg : {
				shortDistance : 20,
				longDistance : 30
			},
			bs2 : {
				shortDistance : 20,
				longDistance : 25
			},
			edc : {
				shortDistance : 10,
				longDistance : 20
			},
			generic : {
				shortDistance : 25,
				longDistance : 35
			},

		},
		
		searchColors_3searches : {
			1:		"#FF0000",			// red, for items belonging only to first search
			2:		"#0000FF",			// blue, for items belonging only to second search
			3:		"#dcd900",			// mustard yellow, for items belonging only to third search
			12:		"#8a51ff",			// purple, for items belonging to first and second search
			13:		"#FF6600",			// orange, for items belonging to first and third search
			23:		"#006600",			// green, for items belonging to second and third search
			123:	"#000000",			// black, for items belonging to all three searches
		},
		
		searchColors_2searches : {
			1:		"#FF0000",			// red, for items only in first search
			2:		"#0000FF",			// blue for items only in second search
			12:		"#00FF00",			// green for items in both searches
		},
		
		// colors to use for the type of link
		typeColors : {
			crosslink : "#FF0000",
			looplink  : "#0000FF",
			monolink  : "#A243E4",
			unknown   : "#000000",
		},

		typeColorsProperties : { //  Keep these values the same as the property names under 'typeColors'
			
			CROSSLINK : "crosslink",
			LOOPLINK : "looplink",
			MONOLINK : "monolink",
			UNKNOWN : "unknown"
		},
		
		lengthColors : {
			short : "#00c224",			// greenish
			medium : "#e3c903",			// yellowish
			long : "#8f8f8f"			// grey
		},
		
		lengthColorProperties : { //  Keep these values the same as the property names under 'lengthColors'
			
			SHORT : "short",
			MEDIUM : "medium",
			LONG : "long"
		},
		
		defaultOpacity : 0.9,			// default opacity to use for links		
};


////////////////

//  User Color by Type


LinkColorHandler.prototype.clearUserColorByType = function() {
	this.userColorByType = undefined;
};

LinkColorHandler.prototype.setUserColorByTypeSingleColor = function( params ) {
	
	var linkTypeLabel = params.linkTypeLabel;
	var linkColor = params.linkColor;
	
			
	if ( linkTypeLabel !== this._CONSTANTS.typeColorsProperties.CROSSLINK
			&& linkTypeLabel !== this._CONSTANTS.typeColorsProperties.LOOPLINK
			&& linkTypeLabel !== this._CONSTANTS.typeColorsProperties.MONOLINK
			&& linkTypeLabel !== this._CONSTANTS.typeColorsProperties.UNKNOWN ) {
		
		throw "setUserColorByTypeSingleColor(..): value for 'linkTypeLabel' invalid: " + linkTypeLabel;
	}
	
	if ( ! this.userColorByType ) {
		
		this.userColorByType = {};
	}
	
	this.userColorByType[ linkTypeLabel ] = linkColor;
};


LinkColorHandler.prototype.setUserColorByType = function( userColorByType ) {

	this.userColorByType = userColorByType;
};

LinkColorHandler.prototype.getUserColorByType = function() {

	return this.userColorByType;
};


LinkColorHandler.prototype.getColorByLinkTypeLabel = function( linkTypeLabel ) {

	if ( linkTypeLabel !== this._CONSTANTS.typeColorsProperties.CROSSLINK
			&& linkTypeLabel !== this._CONSTANTS.typeColorsProperties.LOOPLINK
			&& linkTypeLabel !== this._CONSTANTS.typeColorsProperties.MONOLINK
			&& linkTypeLabel !== this._CONSTANTS.typeColorsProperties.UNKNOWN ) {

		throw "getColorByLinkTypeLabel(..): value for 'linkTypeLabel' invalid: " + linkTypeLabel;
	}
	
	if ( this.userColorByType && this.userColorByType[ linkTypeLabel ] ) {
		return this.userColorByType[ linkTypeLabel ];
	}
	
	return this._CONSTANTS.typeColors[ linkTypeLabel ];
};


/////////////

//  User Color by Length

LinkColorHandler.prototype.clearUserColorByLength = function() {
	this.userColorByLength = undefined;
};

LinkColorHandler.prototype.setUserColorByLengthSingleColor = function( params ) {
	
	var linkLengthLabel = params.linkLengthLabel;
	var linkColor = params.linkColor;
	
	if ( linkLengthLabel !== this._CONSTANTS.lengthColorProperties.SHORT
			&& linkLengthLabel !== this._CONSTANTS.lengthColorProperties.MEDIUM
			&& linkLengthLabel !== this._CONSTANTS.lengthColorProperties.LONG ) {
		
		throw "setUserColorByLengthSingleColor(..): value for 'linkLengthLabel' invalid: " + linkLengthLabel;
	}
	
	if ( ! this.userColorByLength ) {
		
		this.userColorByLength = {};
	}
	
	this.userColorByLength[ linkLengthLabel ] = linkColor;
};


LinkColorHandler.prototype.setUserColorByLength = function( userColorByLength ) {

	this.userColorByLength = userColorByLength;
};

LinkColorHandler.prototype.getUserColorByLength = function() {

	return this.userColorByLength;
};


LinkColorHandler.prototype.getColorByLinkLengthLabel = function( linkLengthLabel ) {

	if ( linkLengthLabel !== this._CONSTANTS.lengthColorProperties.SHORT
			&& linkLengthLabel !== this._CONSTANTS.lengthColorProperties.MEDIUM
			&& linkLengthLabel !== this._CONSTANTS.lengthColorProperties.LONG ) {

		throw "getColorByLinkLengthLabel(..): value for 'linkLengthLabel' invalid: " + linkLengthLabel;
	}
	
	if ( this.userColorByLength && this.userColorByLength[ linkLengthLabel ] ) {
		return this.userColorByLength[ linkLengthLabel ];
	}
	
	return this._CONSTANTS.lengthColors[ linkLengthLabel ];
};


/////////////

//  User Color by Search

LinkColorHandler.prototype.clearUserColorBySearch = function() {
	this.userColorBySearch = undefined;
};

LinkColorHandler.prototype.setUserColorBySearchSingleColor = function( params ) {
	
	var searchIdsArray = params.searchIdsArray;
	var linkColor = params.linkColor;

	if ( ! this.userColorBySearch ) {

		this.userColorBySearch = {};
	}
	
	var colorPropertyObjectKey = this.getColorPropertyNameLabelForSearches( searchIdsArray );

	this.userColorBySearch[ colorPropertyObjectKey ] = linkColor;
};


LinkColorHandler.prototype.setUserColorBySearch = function( userColorBySearch ) {

	this.userColorBySearch = userColorBySearch;
};

LinkColorHandler.prototype.getUserColorBySearch = function() {

	return this.userColorBySearch;
};


LinkColorHandler.prototype.getColorByColorPropertyObjectKey = function( colorPropertyObjectKey ) {

	if ( this.userColorBySearch && this.userColorBySearch[ colorPropertyObjectKey ] ) {
		return this.userColorBySearch[ colorPropertyObjectKey ];
	}

	//  No User defined color so return default color
	
	if( _searches.length === 2 ) {
		
		//  different set of colors for 2 searches being merged 
		
		return this._CONSTANTS.searchColors_2searches[ colorPropertyObjectKey ];
	}
	
	return this._CONSTANTS.searchColors_3searches[ colorPropertyObjectKey ];
};


///////////////////////

/**
 * Get the color for the specified link.
 * @param link The link for which we want a color
 * @param colorFormat The format for the color returned. Possibilities are:
 * 		'hex' : The color as a string hexadecimal (e.g., "#FF0000" for red)
 * 		'rgb' : The color as integers assigned to each channel (e.g., { r:255, g:0, b:0 } for red )
 * 		'pvrgba' : The color as an array of decimals [r, g, b, opacity] expected by the pv viewer: ( [ 1.0, 0.0, 0.0, 0.75 ] for red at 75% opacity )
 */
LinkColorHandler.prototype.getLinkColor = function( link, colorFormat ) {
	if( !link ) {
		console.log( "ERROR, got no linker in getLinkColor" );
		return;
	}
	
	if( !colorFormat ) {
		console.log( "ERROR, got no colorFormat for getLinkColor" );
		return;
	}
	
	var mode = getLinkColorMode();
	var color = '';
	
	if( mode === 'length' ) {
		color =  this.getLinkColorByLength( link );
	}
	
	if( mode === 'type' ) {
		color =  this.getLinkColorByType( link );
	}
	
	if( mode === 'search' ) {
		color =  this.getLinkColorBySearches( link );
	}

	if( !color || color === '' ) {
		console.log( 'ERROR, COULD NOT GET COLOR FOR LINK:' );
		console.log( link );
		console.log( colorFormat );
	}
	
	if( colorFormat === 'hex' ) {
		return color;
	}
	
	if( colorFormat === 'rgb' ) {
		return this.hexToRgb( color );
	}
	
	if( colorFormat === 'pvrgba' ) {
		return this.hexToRgbaDecimalArray( color, this.getOpacity( link ) );
	}
	
	if( colorFormat === 'name' ) {
		return this.hexToName( color );
	}
	
	console.log( "ERROR, GOT INVALID COLOR FORMAT:" );
	console.log( colorFormat );
	
	return;
};

/**
 * Get the color to use for the given link, based on length
 * @param link
 */
LinkColorHandler.prototype.getLinkColorByLength = function( link ) {

	var length = link.length;
	
	var constraints = this.getDistanceConstraints();
	if( !constraints ) {
		console.log("ERROR, GOT NOT CONSTRAINTS IN getLinkColorByLength" );
		console.log( link );
		console.log( colorFormat );
	}

	if( length <= constraints.shortDistance ) {
		return this.getColorByLinkLengthLabel( this._CONSTANTS.lengthColorProperties.SHORT );
	}
	
	if( length <= constraints.longDistance ) {
		return this.getColorByLinkLengthLabel( this._CONSTANTS.lengthColorProperties.MEDIUM );
	}
	
	return this.getColorByLinkLengthLabel( this._CONSTANTS.lengthColorProperties.LONG );
};

/**
 * Get the color to use for the given link, based on link type
 * @param link
 */
LinkColorHandler.prototype.getLinkColorByType = function( link ) {
	
	// color by the type (e.g. cross-link, loop-link, and mono-link)
	if( link.type === 'crosslink' ) { 
		return this.getColorByLinkTypeLabel( _linkColorHandler._CONSTANTS.typeColorsProperties.CROSSLINK ); 
	}
	if( link.type === 'looplink' ) { 
		return this.getColorByLinkTypeLabel( _linkColorHandler._CONSTANTS.typeColorsProperties.LOOPLINK ); 
	}
	if( link.type === 'monolink' ) { 
		return this.getColorByLinkTypeLabel( _linkColorHandler._CONSTANTS.typeColorsProperties.MONOLINK ); 
	}		
	
	console.log( "ERROR: link.type is not recognized." );
	return this._CONSTANTS.typeColors.unknown;
};


/**
 * Get the color to use for the given link, based on which search(es) the link is in
 * @param link
 */
LinkColorHandler.prototype.getLinkColorBySearches = function( link ) {

	var searches;		// searches in which this link is found
	
	if( link.type === 'crosslink' ) { searches = findSearchesForCrosslink( link.protein1, link.protein2, link.position1, link.position2 ); }
	else if( link.type === 'looplink' ) { searches = findSearchesForLooplink( link.protein1, link.position1, link.position2 ); }
	else if( link.type === 'monolink' ) { searches = findSearchesForMonolink( link.protein1, link.position1 ); }	
	else {
		console.log( "ERROR: link.type is not recognized." );
		return;
	}
	
	if( !searches || searches.length < 1 ) {
		console.log( "ERROR: got no searches for link:" );
		console.log (link );
		return;
	}
	
	return this.getColorForSearches( searches );
};

/**
 * Get the color to use for the array of searches passed in
 * @param searches
 */
LinkColorHandler.prototype.getColorForSearches = function( searches ) {
	
	//  colorPropertyLookup is a string.  
	//  It is a concatenation of the positions of the search id positions ( One based )
	//  Which is used to retrieve colors based on properties that match that set of positions 
	
	//  The variable _searches  is defined and populated in the Javascript file "structure-viewer-page.js"
	
	var colorPropertyObjectKey = this.getColorPropertyNameLabelForSearches( searches );
	
	return this.getColorByColorPropertyObjectKey( colorPropertyObjectKey );
	
//	if( _searches.length === 2 ) {
//		
//		//  different set of colors for 2 searches being merged 
//		
//		return this._CONSTANTS.searchColors_2searches[ colorPropertyObjectKey ];
//	}
//	
//	return this._CONSTANTS.searchColors_3searches[ colorPropertyObjectKey ];
};



/**
 * Get the color property name/value to use for the array of searches passed in
 * @param searches
 */
LinkColorHandler.prototype.getColorPropertyNameLabelForSearches = function( searches ) {
	
	//  colorPropertyLookup is a string.  
	//  It is a concatenation of the positions of the search id positions ( One based )
	//  Which is used to retrieve colors based on properties that match that set of positions 
	
	//  The variable _searches  is defined and populated in the Javascript file "structure-viewer-page.js"
	
	var colorPropertyLookup = "";
	
	for ( var i = 0; i < _searches.length; i++ ) {
		
		//  Loop through all searches being merged
		
		for ( var k = 0; k < searches.length; k++ ) {
			
			//  Loop through the search ids getting a color for
			
			if ( _searches[i]['id'] === searches[ k ] ) {
				
				//  If the search id from "all searches being merged" 
				//    matches the search id getting the color for,
				//    String Append the position in the "all searches being merged" array
				//    to the property lookup
				
				colorPropertyLookup += ( i + 1 );  //  " + 1" to make it One based instead of zero based
				break;
			}
		}
	}
	
	return colorPropertyLookup;
};




/**
 * Return the supplied hex and opacity as an array of decimal rgba values as expected by the pv structure viewer
 * @param hex The hex code of the color (e.g., #FF0000 for red)
 * @param opacity The opacity (from 0 - 1)
 * @returns [ r (0-1), g (0-1), b (0-1), opacity ]
 */
LinkColorHandler.prototype.hexToRgbaDecimalArray = function( hex, opacity ) {
	var rgb = this.hexToRgb( hex );
	return rgb ? [ (rgb.r / 255), (rgb.g / 255 ),  (rgb.b / 255 ), opacity ] : null;	
};


/**
 * Convert hex to rgb
 * @returns { r: redValue, g: greenValue, b: blueValue } (all values 0-255)
 */
LinkColorHandler.prototype.hexToRgb = function( hex ) {
    // Expand shorthand form (e.g. "03F") to full form (e.g. "0033FF")
    var shorthandRegex = /^#?([a-f\d])([a-f\d])([a-f\d])$/i;
    hex = hex.replace(shorthandRegex, function(m, r, g, b) {
        return r + r + g + g + b + b;
    });

    var result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
    return result ? {
        r: parseInt(result[1], 16),
        g: parseInt(result[2], 16),
        b: parseInt(result[3], 16)
    } : null;
};

/**
 * Get the opacity to use for the given link's color
 * @param link The link we're testing
 * @param forReport true if for the distance report table, false if not
 */
LinkColorHandler.prototype.getOpacity = function( link ) {
	
	var shadeByCounts = false;
	if ( $( "input#shade-by-counts" ).is( ':checked' ) ) { shadeByCounts = true; }
	
	if( shadeByCounts && !('psmCount' in link)) {
		console.log( "ERROR: Was told to shade by counts, but link has no PSM count Using default opacity. Link:" );
		console.log( link );
	}
	
	var opacity = this._CONSTANTS.defaultOpacity;
	
	if( shadeByCounts && 'psmCount' in link ) {
		var psmCount = link[ 'psmCount' ];
		var min = 0.5;
		var max = opacity;
		var countForMaxOpacity = 10;
		
		if( psmCount > countForMaxOpacity ) { psmCount = countForMaxOpacity; }
		
		opacity = max - ( ( max - min ) * ( ( countForMaxOpacity - psmCount ) / countForMaxOpacity ) );
		if( opacity < min || opacity > max ) {
			console.log( "WARNING: Invalid opacity: " + opacity  + " (psmCount: " + psmCount + ")" );
		}
		
	}
	
	return opacity;
};

LinkColorHandler.prototype.removeUserDistanceConstraints = function() {
	this.userDistanceConstraint = undefined;
};

LinkColorHandler.prototype.setUserDistanceConstraints = function(short, long) {
	this.userDistanceConstraint = {
		shortDistance : short,
		longDistance : long
	};
};

LinkColorHandler.prototype.getUserDistanceConstraints = function() {
	if( !( 'userDistanceConstraint' in this ) ) { return undefined; }
	return this.userDistanceConstraint;
};

/**
 * Get the distance restraints to use for coloring, given the linkers used in the current
 * search(es).
 */
LinkColorHandler.prototype.getDistanceConstraints = function() {

	// if the user has set custom constraints, use them
	if( this.getUserDistanceConstraints() ) {
		return this.getUserDistanceConstraints();
	}
	
	var linkers = getLinkerStringsAsArray();
	
	if( !linkers || linkers.length < 1 ) {
		return this._CONSTANTS.linkers.generic;
	}
	
	if( linkers.length == 1 ) {
		if( linkers[ 0 ] in this._CONSTANTS.linkers ) {
			return this._CONSTANTS.linkers[ linkers[ 0 ] ];
		} else {
			this._CONSTANTS.linkers.generic;
		}
	}

	// more than one linker was used. color based on the "longer" of the two, as
	// determined by the "longDistance" property.
	var longLinker;
	for( var i = 0; i < linkers.length; i++ ) {
		if( linkers[ i ] in this._CONSTANTS.linkers ) {
			var tLinker = this._CONSTANTS.linkers[ linkers[ i ] ];
			
			if( !longLinker || longLinker.longDistance < tLinker.longDistance ) {
				longLinker = tLinker;
			}
		}
	}
	
	if( !longLinker ) { longLinker = this._CONSTANTS.linkers.generic; }
	
	return longLinker;
};