/**
 * copyObject_DeepCopy.js
 * 
 * Javascript:  Utility to make deep copy of object
 * 
 * 
 * 
 */

//////////////////////////////////
//JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

///////////////////////////////////////////

/**
 * Make deep copy of object parameter
 */
var copyObject_DeepCopy_Proxl = function( objectToCopy ) {
	
	if ( objectToCopy === undefined || objectToCopy === null ) {
		throw Error("copyObject_DeepCopy_Proxl: invalid parameter, is undefined or null");
	}
	return jQuery.extend( true /* [deep ] */, {}, objectToCopy );
};

export { copyObject_DeepCopy_Proxl }

