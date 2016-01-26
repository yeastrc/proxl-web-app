

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
		
		lengthColors : {
			short : "#00B428",			// greenish
			medium : "#E3E602",			// yellowish
			long : "#D33333"			// reddish
		},
		
		defaultOpacity : 0.9,			// default opacity to use for links		
};

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
	
	console.log( "ERROR, GOT INVALID COLOR FORMAT:" );
	console.log( colorFormat );
	
	return;
};

/**
 * Get the color to use for the given link, based on length
 * @param link
 */
LinkColorHandler.prototype.getLinkColorByLength = function( link ) {

	var length = parseInt( link.length );
	
	var constraints = this.getDistanceConstraints();
	if( !constraints ) {
		console.log("ERROR, GOT NOT CONSTRAINTS IN getLinkColorByLength" );
		console.log( link );
		console.log( colorFormat );
	}
	
	if( length <= constraints.shortDistance ) {
		return this._CONSTANTS.lengthColors.short;
	}
	
	if( length <= constraints.longDistance ) {
		return this._CONSTANTS.lengthColors.medium;
	}
	
	return this._CONSTANTS.lengthColors.long;
}

/**
 * Get the color to use for the given link, based on link type
 * @param link
 */
LinkColorHandler.prototype.getLinkColorByType = function( link ) {
	
	// color by the type (e.g. cross-link, loop-link, and mono-link)
	if( link.type === 'crosslink' ) { return this._CONSTANTS.typeColors.crosslink; }
	if( link.type === 'looplink' ) { return this._CONSTANTS.typeColors.looplink; }
	if( link.type === 'monolink' ) { return this._CONSTANTS.typeColors.monolink; }		
	
	console.log( "ERROR: link.type is not recognized." );
	return this._CONSTANTS.typeColors.unknown;
}


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
}

/**
 * Get the color to use for the list of searches passed in
 * @param searches
 */
LinkColorHandler.prototype.getColorForSearches = function( searches ) {
	var colorIndex = "";
	
	for ( var i = 0; i < _searches.length; i++ ) {
		for ( var k = 0; k < searches.length; k++ ) {
			if ( _searches[i]['id'] === searches[ k ] ) {
				colorIndex += ( i + 1 );
				break;
			}
		}
	}
	
	if( _searches.length === 2 ) {
		return this._CONSTANTS.searchColors_2searches[ colorIndex ];
	}
	
	return this._CONSTANTS.searchColors_3searches[ colorIndex ];
}


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
}

LinkColorHandler.prototype.setUserDistanceConstraints = function(short, long) {
	this.userDistanceConstraint = {
		shortDistance : short,
		longDistance : long
	};
}

LinkColorHandler.prototype.getUserDistanceConstraints = function() {
	if( !( 'userDistanceConstraint' in this ) ) { return undefined; }
	return this.userDistanceConstraint;
}

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