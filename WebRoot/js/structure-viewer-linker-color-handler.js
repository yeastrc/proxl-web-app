

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
		}
		
};

