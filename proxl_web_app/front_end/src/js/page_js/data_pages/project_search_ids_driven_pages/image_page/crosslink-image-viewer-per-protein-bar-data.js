/**
 * crosslink-image-viewer-per-protein-bar-data.js
 * 
 * Javascript for the viewMergedImage.jsp page
 * 
 * This file holds javascript for per protein bar data.  
 * 
 * This file also handles protein bar highlighting/selecting and de-selecting
 *  
 * !!! The following global variables from "crosslink-image-viewer.js" are used in this file:
 * 
 * 	  window.imagePagePrimaryRootCodeObject.getVariable__v_indexManager()
 *    window.imagePagePrimaryRootCodeObject.getVariable__v_proteinLengths()
 */

// JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

////////////////////////////
//   All references to proteinId or protein_id are actually referencing the protein sequence id
////////////////////////////

/////     Protein Bar Data Manager

/**
 * Constructor
 */
var ImageProteinBarDataManager = function() {
	this.barData = { };
	this.maxRegionIdNumber = 0; //  Current max region id that was allocated, init to zero

	this.UNIQUE_ID_PREFIX = "z";
	this.UNIQUE_ID_TO_STRING_RADIX = 32;  // max 36
};

/**
 * Function that returns a new object using Constructor ImageProteinBarDataManager()
 */
var ImageProteinBarDataManagerContructor = function() { 
	
	var imageProteinBarDataManager =  new ImageProteinBarDataManager();
	ProteinBarHighlightedRegion.imageProteinBarDataManager = imageProteinBarDataManager;
	return imageProteinBarDataManager;
};

/**
 * Get a new unique id. This is guaranteed to be unique.
 */
ImageProteinBarDataManager.prototype.getNextRegionUniqueId = function() {
	if ( this.maxRegionIdNumber === undefined ) {
		throw Error( "this.maxRegionIdNumber === undefined" );
	}
	if ( this.maxRegionIdNumber === null ) {
		throw Error( "this.maxRegionIdNumber === null" );
	}
	this.maxRegionIdNumber++;
	
	var maxRegionIdNumberLocal = this.maxRegionIdNumber;
	//  make regionUniqueId from maxRegionIdNumberLocal using this.UNIQUE_ID_TO_STRING_RADIX as radix
	var regionUniqueId = this.UNIQUE_ID_PREFIX + maxRegionIdNumberLocal.toString( this.UNIQUE_ID_TO_STRING_RADIX );
	
	//  TODO  Pass new this.maxRegionIdNumber to server for tracking
	
	return regionUniqueId;
};

/**
 * 
 */
ImageProteinBarDataManager.prototype.addEntry = function( uid, entry ) {
	
		if ( entry ) {
			entry.setContainingImageProteinBarDataManager( { containingImageProteinBarDataManager : this } );
			entry.uid = uid;
			entry.pid = window.imagePagePrimaryRootCodeObject.getVariable__v_indexManager().getProteinIdForUID( uid );
			this.barData[ uid ] = entry;
		} else {
			var newEntry = ImageProteinBarData.constructEmptyImageProteinBarData();
			newEntry.setContainingImageProteinBarDataManager( { containingImageProteinBarDataManager : this } );
			newEntry.uid = uid;
			newEntry.pid = window.imagePagePrimaryRootCodeObject.getVariable__v_indexManager().getProteinIdForUID( uid );
			this.barData[ uid ] = newEntry;
		}
};

/**
 * Get all items
 */
ImageProteinBarDataManager.prototype.getAllItems = function( ) {
	return this.barData;
};

/**
 * 
 */
ImageProteinBarDataManager.prototype.getItemByUID = function( uid ) {
	var item = this.barData[ uid ];
	if ( ! item ) {
		throw Error( "entry not found in barData for uid: " + uid );
	}
	return item;
};

/**
 * Remove all invalid UIDs--that is, UIDs not found in the window.imagePagePrimaryRootCodeObject.getVariable__v_indexManager()
 */
ImageProteinBarDataManager.prototype.removeInvalidEntries = function() {
		
	var keys = Object.keys( this.getAllItems() );
	var kl = keys.length;	
	var UIDsToRemove = [ ];
	
	for( var i = 0; i < kl; i++ ) {
		if( !(window.imagePagePrimaryRootCodeObject.getVariable__v_indexManager().containsUID( keys[ i ] )) ) {
			UIDsToRemove.push( keys[ i ] );
		}
	}
	for( var i = 0; i < UIDsToRemove.length; i++ ) {
		console.log( "Removing invalid entry from protein bar manager: " + UIDsToRemove[ i ] );
		this.removeItemByUID( UIDsToRemove[ i ] );
	}
};

/**
 * 
 */
ImageProteinBarDataManager.prototype.getItemByIndex = function( index ) {
	
	var entry = window.imagePagePrimaryRootCodeObject.getVariable__v_indexManager().getProteinArray()[ index ];
	if ( ! entry ) {
		throw Error( "entry not found in barData for index: " + index );
	}
	return this.getItemByUID( entry.uid );
};

/**
 * 
 */
ImageProteinBarDataManager.prototype.removeItemByUID = function( uid ) {
	delete this.barData[ uid ];
};

/**
 * 
 */
ImageProteinBarDataManager.prototype.clearItems = function() {
	
	this.barData = { };
};

////    URL Hash Specific Processing

/**
 * Get data for Hash
 */
ImageProteinBarDataManager.prototype.getDataForHash = function( ) {
	
	var imageProteinBarDataManagerDataResult = {};

	imageProteinBarDataManagerDataResult.a = this.maxRegionIdNumber;

	var imageProteinBarDataResult = { };
	var keys = Object.keys( this.getAllItems() );
	for( var i = 0; i < keys.length; i++ ) {
		var key = keys[ i ];
		var imageProteinBarDataEntry = this.barData[ key ];
		var imageProteinBarDataForHash = imageProteinBarDataEntry.getHashDataObject();
		imageProteinBarDataResult[ key ] = imageProteinBarDataForHash;
	}
	imageProteinBarDataManagerDataResult.b = imageProteinBarDataResult;
	
	return imageProteinBarDataManagerDataResult;
};

/**
 * Update Internal ImageProteinBarDataManager data with data in the Page Hash
 */
ImageProteinBarDataManager.prototype.replaceInternalDataWithDataInHash = function( imageProteinBarDataManagerData ) {

	this.clearItems();
	if ( imageProteinBarDataManagerData ) {
		var maxRegionIdNumber = imageProteinBarDataManagerData.a;
		var proteinBarDataObjects = imageProteinBarDataManagerData.b;
		if ( maxRegionIdNumber !== undefined && proteinBarDataObjects !== undefined ) {
			//  Current version so process
			this.maxRegionIdNumber = maxRegionIdNumber;
			if ( proteinBarDataObjects ) {
				var keys = Object.keys( proteinBarDataObjects );
				for( var i = 0; i < keys.length; i++ ) {
					var key = keys[ i ];
					var proteinBarDataHashItem = proteinBarDataObjects[ key ];
					var proteinBarDataItem = ImageProteinBarData.constructImageProteinBarDataFromHashDataObject( proteinBarDataHashItem ); 
					proteinBarDataItem.setContainingImageProteinBarDataManager( { containingImageProteinBarDataManager : this } );
					this.addEntry( key, proteinBarDataItem );
				}
			}
		} else {
			// Previous version (proteinBarDataObjects is at the root level of imageProteinBarDataManagerData
			if ( proteinBarDataObjects ) {
				var keys = Object.keys( proteinBarDataObjects );
				for( var i = 0; i < keys.length; i++ ) {
					var key = keys[ i ];
					var proteinBarDataHashItem = proteinBarDataObjects[ key ];
					var proteinBarDataItem = ImageProteinBarData.constructImageProteinBarDataFromHashDataObject( proteinBarDataHashItem ); 
					proteinBarDataItem.setContainingImageProteinBarDataManager( { containingImageProteinBarDataManager : this } );
					this.addEntry( key, proteinBarDataItem );
				}
			}
		}
	}
};

///   Add on functions for specific parts

/**
 * Proteins Reversed
 */
ImageProteinBarDataManager.prototype.clearAllProteinBarsReversed = function(  ) {
	var keys = Object.keys( this.getAllItems() );
	for( var i = 0; i < keys.length; i++ ) {
		var key = keys[ i ];
		var imageProteinBarDataEntry = this.barData[ key ];
		imageProteinBarDataEntry.setProteinReversed( { proteinReversed : undefined } );
	}
};

/**
 * Protein Offsets
 */
ImageProteinBarDataManager.prototype.clearAllProteinBarsOffsets = function(  ) {
	var keys = Object.keys( this.getAllItems() );
	for( var i = 0; i < keys.length; i++ ) {
		var key = keys[ i ];
		var imageProteinBarDataEntry = this.barData[ key ];
		imageProteinBarDataEntry.setProteinOffset( { proteinOffset : 0 } );
	}
};

/**
 * 
 */
ImageProteinBarDataManager.prototype.addToAllProteinOffsets = function( params ) {
	var offsetChange = params.offsetChange;
	var keys = Object.keys( this.getAllItems() );
	for( var i = 0; i < keys.length; i++ ) {
		var key = keys[ i ];
		var imageProteinBarDataEntry = this.barData[ key ];
		imageProteinBarDataEntry.addToProteinOffset( { offsetChange : offsetChange } );
	}
};

//    Highlighted Protein bars

/**
 * Is Any Protein bars Highlighted
 */
ImageProteinBarDataManager.prototype.isAnyProteinBarsHighlighted = function(  ) {
	var keys = Object.keys( this.getAllItems() );
	for( var i = 0; i < keys.length; i++ ) {
		var key = keys[ i ];
		var imageProteinBarDataEntry = this.barData[ key ];
		if ( imageProteinBarDataEntry.isAnyOfProteinBarHighlighted() ) {
			return true;
		}
	}
	return false;
};

/**
 * 
 */
ImageProteinBarDataManager.prototype.exactly_One_ProteinBar_Highlighted = function(  ) {
	var proteinBarHighlightCount = 0;
	var keys = Object.keys( this.getAllItems() );
	for( var i = 0; i < keys.length; i++ ) {
		var key = keys[ i ];
		var imageProteinBarDataEntry = this.barData[ key ];
		if ( imageProteinBarDataEntry.isAnyOfProteinBarHighlighted() ) {
			proteinBarHighlightCount++;
		}
	}
	if ( proteinBarHighlightCount === 1 ) {
		return true;
	}
	return false;
};

/**
 * 
 */
ImageProteinBarDataManager.prototype.moreThan_One_ProteinBar_Highlighted = function(  ) {
	var proteinBarHighlightCount = 0;
	var keys = Object.keys( this.getAllItems() );
	for( var i = 0; i < keys.length; i++ ) {
		var key = keys[ i ];
		var imageProteinBarDataEntry = this.barData[ key ];
		if ( imageProteinBarDataEntry.isAnyOfProteinBarHighlighted() ) {
			proteinBarHighlightCount++;
		}
	}
	if ( proteinBarHighlightCount > 1 ) {
		return true;
	}
	return false;
};

/**
 * 
 */
ImageProteinBarDataManager.prototype.clearAllProteinBarsHighlighted = function(  ) {
	var keys = Object.keys( this.getAllItems() );
	for( var i = 0; i < keys.length; i++ ) {
		var key = keys[ i ];
		var imageProteinBarDataEntry = this.barData[ key ];
		imageProteinBarDataEntry.clearProteinBarHighlighted();
	}
};

//   For Conversion from NRSEQ Protein Id to Protein Sequence Id

/** 
 * return true if any imageProteinBarDataListEntry contains 
 */
ImageProteinBarDataManager.prototype.contains_OLD_NrseqProteinId = function(  ) {
	var keys = Object.keys( this.getAllItems() );
	for( var i = 0; i < keys.length; i++ ) {
		var key = keys[ i ];
		var imageProteinBarDataEntry = this.barData[ key ];
		if ( imageProteinBarDataEntry.getOLD_NrseqProteinId() ) {
			return true;
		}
	}
	return false;
};

////////////////////////////////////////////////////////////////////////////////////

//   Data for a Single Protein Bar 

/**
 * Constructor for an object for the data for a single protein bar displayed
 */
var ImageProteinBarData = function(  ) {
	this.containingImageProteinBarDataManager = undefined;
	this.proteinBarHighlightedRegions = undefined;
	this.proteinBarHighlightedAll = undefined;
	
	this.uid = undefined;
	this.pid = undefined;

};

/**
 * Clear properties that no longer apply after protein id change
 */
ImageProteinBarData.prototype.clearPropertiesOnProteinIdChange = function() {
	this.proteinBarHighlightedRegions = undefined;
	this.proteinBarHighlightedAll = undefined;
	this.proteinReversed = undefined;
	this.proteinOffset = 0;
};

ImageProteinBarData.prototype.getProteinBarUniqueId = function() {
	return this.uid;
};

ImageProteinBarData.prototype.getProteinBarProtienId = function() {
	return this.pid;
};

/**
 * get containing ImageProteinBarDataManager
 */
ImageProteinBarData.prototype.getContainingImageProteinBarDataManager = function( ) {
	return this.containingImageProteinBarDataManager;
};
/**
 * set containing ImageProteinBarDataManager
 */
ImageProteinBarData.prototype.setContainingImageProteinBarDataManager = function( params ) {
	this.containingImageProteinBarDataManager = params.containingImageProteinBarDataManager;
};

//////////////////////
//    OLD NRSEQ Protein Id

ImageProteinBarData.prototype.getOLD_NrseqProteinId = function( ) {
	return this.nrseqProteinId;
};
ImageProteinBarData.prototype.setOLD_NrseqProteinId = function( params ) {
	var nrseqProteinId = params.nrseqProteinId;
	this.nrseqProteinId = nrseqProteinId;
};

////////////////
//  Protein Direction Reversed

ImageProteinBarData.prototype.getProteinReversed = function( ) {
	return this.proteinReversed;
};
ImageProteinBarData.prototype.setProteinReversed = function( params ) {
	var proteinReversed = params.proteinReversed;
	this.proteinReversed = proteinReversed;
};

////////////////
//  Protein Offset from left edge

ImageProteinBarData.prototype.getProteinOffset = function( ) {
	return this.proteinOffset;
};
ImageProteinBarData.prototype.setProteinOffset = function( params ) {
	var proteinOffset = params.proteinOffset;
	this.proteinOffset = proteinOffset;
};

/**
 * Add value to current this.proteinOffset
 */
ImageProteinBarData.prototype.addToProteinOffset = function( params ) {
	var offsetChange = params.offsetChange;
	if ( this.proteinOffset != undefined && this.proteinOffset != null ) {
		this.proteinOffset += offsetChange;
	} else {
		this.proteinOffset = offsetChange;
	}
};

//////////

//  protein bar highlighted

/**
 * Is any of the protein bar highlighted
 */
ImageProteinBarData.prototype.isAnyOfProteinBarHighlighted = function( ) {
	if ( this.proteinBarHighlightedAll ) {
		return true;
	}
	if ( this.proteinBarHighlightedRegions && this.proteinBarHighlightedRegions.length > 0 ) {
		return true;
	}
	return false;
};

/**
 * Is all of the protein bar highlighted
 */
ImageProteinBarData.prototype.isAllOfProteinBarHighlighted = function( ) {
	if ( this.proteinBarHighlightedAll ) {
		return true;
	}
	return false;
};

/**
 * Selected Region count.   Return 1 if all of protein bar highlighted
 */
ImageProteinBarData.prototype.getProteinBarHighlightedRegionCount = function( ) {
	if ( this.proteinBarHighlightedAll ) {
		return 1;
	}
	if ( this.proteinBarHighlightedRegions && this.proteinBarHighlightedRegions.length > 0 ) {
		return this.proteinBarHighlightedRegions.length;
	}
	return 0;
};

///////////////////////////
//  Is protein bar highlighted at position.  

/**
 * If there are 2 positions passed in, rules are followed to determine if the link between them should be highlighted.
 */
ImageProteinBarData.prototype.isProteinBarHighlightedAtPosition = function( params ) {
	var position_1 = params.position_1;
	var position_2 = params.position_2;  // optional
	if ( this.proteinBarHighlightedAll ) {
		//  This protein bar is All highlighted so return true
		return true;
	}
	if ( this.proteinBarHighlightedRegions === undefined
			|| this.proteinBarHighlightedRegions === null
			|| this.proteinBarHighlightedRegions.length === 0 ) {
		//  There are no highlighted regions so return false
		return false;
	}
	if ( position_2 === undefined || position_2 === null ) {
		//  Only one position so return result of is that position in a selected region
		return this._isProteinBarHighlightedAtSinglePosition( position_1 );
	}
	/////////
	//  2 positions so evaluate both
	
	//  Only get here if this bar is not fully highlighted 
	//  and there is at least one highlighted region in this bar
	if ( this.containingImageProteinBarDataManager.moreThan_One_ProteinBar_Highlighted() 
			|| this.proteinBarHighlightedRegions.length > 1 ) {
		//  Either another protein bar is fully or partially highlighted
		//  or this protein bar has more than one highlighted regions

		//  Both positions must be within highlighted/selected regions
		if ( this._isProteinBarHighlightedAtSinglePosition( position_1 ) 
				&& this._isProteinBarHighlightedAtSinglePosition( position_2 ) ) {
			return true;
		}
		return false;
	}
	//  NOT More than one one highlighted bar or region across all the protein bars.  
	//  Either positions must be within highlighted/selected regions
	if ( this._isProteinBarHighlightedAtSinglePosition( position_1 ) 
			|| this._isProteinBarHighlightedAtSinglePosition( position_2 ) ) {
		return true;
	}
	return false;
};

/**
 * Internal.    Is the protein bar highlighted for this single position
 */
ImageProteinBarData.prototype._isProteinBarHighlightedAtSinglePosition = function( position ) {
	var regionIndexContainingPosition = this.indexOfProteinBarHighlightedRegionAtSinglePosition( position );
	if ( regionIndexContainingPosition !== -1 ) {
		return true;
	}
	return false;
};

/**
 * Index of the protein bar highlighted region for this single position
 * 
 * Index means the region number starting at zero for the number of regions on the protein bar
 */
ImageProteinBarData.prototype.indexOfProteinBarHighlightedRegionAtSinglePosition = function( position ) {
	if ( this.proteinBarHighlightedRegions === undefined
			|| this.proteinBarHighlightedRegions === null
			|| this.proteinBarHighlightedRegions.length === 0 ) {
		//  There are no highlighted regions so return false
		return -1;
	}
	for ( var index = 0; index < this.proteinBarHighlightedRegions.length; index++ ) {
		var proteinBarHighlightedRegionsEntry = this.proteinBarHighlightedRegions[ index ];
		if ( proteinBarHighlightedRegionsEntry.s <= position 
				&& proteinBarHighlightedRegionsEntry.e >= position ) {
			return index;
		}
		;
	}
	return -1;
};

/**
 * Is protein bar highlighted anywhere between two positions.
 * 
 * Is the protein bar highlighted anywhere between the two positions, inclusive of the positions
 */
ImageProteinBarData.prototype.isProteinBarHighlightedAnywhereBetweenPositions = function( params ) {
	var position_1 = params.position_1;
	var position_2 = params.position_2;
	var regionIndex = 
		this.indexOfProteinBarHighlightedRegionAnywhereBetweenPositionsInclusive( { position_1 : position_1, position_2 : position_2 } );
	if ( regionIndex === -1 ) {
		return false;
	}
	return true;
};

/**
 * Index of the First protein bar highlighted region between these 2 positions, inclusive of the positions
 * 
 * Index means the region number starting at zero for the number of regions on the protein bar
 */
ImageProteinBarData.prototype.indexOfProteinBarHighlightedRegionAnywhereBetweenPositionsInclusive = function( params ) {
	var position_1 = params.position_1;
	var position_2 = params.position_2;
	if ( this.proteinBarHighlightedAll ) {
//		This protein bar is All highlighted so return 0
		return 0;
	}
	if ( this.proteinBarHighlightedRegions === undefined
			|| this.proteinBarHighlightedRegions === null
			|| this.proteinBarHighlightedRegions.length === 0 ) {
//		There are no highlighted regions so return -1
		return -1;
	}
//	Only get here if this bar is not fully highlighted 
//	and there is at least one highlighted region in this bar
	for ( var index = 0; index < this.proteinBarHighlightedRegions.length; index++ ) {
		var proteinBarHighlightedRegionsEntry = this.proteinBarHighlightedRegions[ index ];
		if ( ( proteinBarHighlightedRegionsEntry.s <= position_1 
				&& proteinBarHighlightedRegionsEntry.e >= position_1 ) //  The select block contains position_1
				|| ( proteinBarHighlightedRegionsEntry.s <= position_2 
						&& proteinBarHighlightedRegionsEntry.e >= position_2 ) //  The select block contains position_2
						|| ( proteinBarHighlightedRegionsEntry.s >= position_1
								&& proteinBarHighlightedRegionsEntry.e <= position_2 ) //  The select block is within position_1 and position_2
		)
		{
//			Either position_1 or position_2 is within a selected region
//			or a selected region is within position_1 and position_2 so return true
			return index;
		}
		;
	}
	return -1;
};

/**
 * Get highlighted values as array.  Only use for drawing rectangles to display selected regions
 */
ImageProteinBarData.prototype.getProteinBarHighlightedRegionsArray = function() {
	return this.proteinBarHighlightedRegions;
};

/**
 * Replace existing highlighted values
 */
ImageProteinBarData.prototype.setProteinBarHighlightedAll = function( ) {
	this.proteinBarHighlightedRegions = undefined;
	this.proteinBarHighlightedAll = true;
	//  If this is the only bar, unhighlight it.
	var numItems = Object.keys( this.containingImageProteinBarDataManager.getAllItems() ).length;
	if ( ( ! numItems ) || numItems === 0 || numItems === 1 ) {
		this.proteinBarHighlightedAll = undefined;
	}
};



/**
 * Get Region Object of the protein bar highlighted region for this single position
 * 
 * @param {number} position in the protein bar
 * @returns {object} Class ProteinBarHighlightedRegion, or undefined if no region at the position
 */
ImageProteinBarData.prototype.getProteinBarHighlightedRegionObjectAtSinglePosition = function( position ) {
	if ( this.proteinBarHighlightedRegions === undefined
			|| this.proteinBarHighlightedRegions === null
			|| this.proteinBarHighlightedRegions.length === 0 ) {
		//  There are no highlighted regions so return undefined
		return undefined;
	}
	for ( var index = 0; index < this.proteinBarHighlightedRegions.length; index++ ) {
		var proteinBarHighlightedRegionsEntry = this.proteinBarHighlightedRegions[ index ];
		if ( proteinBarHighlightedRegionsEntry.getStart() <= position 
				&& proteinBarHighlightedRegionsEntry.getEnd() >= position ) {
			return proteinBarHighlightedRegionsEntry;
		}
	}
	return undefined;
};

/**
 * Get Region Object of the protein bar highlighted region for this region unique id
 * 
 * @param {string} regionUniqueId
 * @returns {object} Class ProteinBarHighlightedRegion, or undefined if no region with that regionUniqueId
 */
ImageProteinBarData.prototype.getProteinBarHighlightedRegionObjectForRegionUniqueId = function( regionUniqueId ) {
	if ( this.proteinBarHighlightedRegions === undefined
			|| this.proteinBarHighlightedRegions === null
			|| this.proteinBarHighlightedRegions.length === 0 ) {
		//  There are no highlighted regions so return undefined
		return undefined;
	}
	for ( var index = 0; index < this.proteinBarHighlightedRegions.length; index++ ) {
		var proteinBarHighlightedRegionsEntry = this.proteinBarHighlightedRegions[ index ];
		if ( proteinBarHighlightedRegionsEntry.getRegionUniqueId() === regionUniqueId ) {
			return proteinBarHighlightedRegionsEntry;
		}
	}
	return undefined;
};



/**
 * 
 */
ImageProteinBarData.prototype.getProteinId = function() {
	return this.pid;
};

/**
 * 
 */
ImageProteinBarData.prototype.getProteinLength = function() {
	return window.imagePagePrimaryRootCodeObject.getVariable__v_proteinLengths().getProteinLength( this.getProteinId() );
};

/**
 * Replace existing highlighted values
 */
ImageProteinBarData.prototype.setProteinBarHighlightedRegion = function( params ) {
	var start = params.start;
	var end = params.end;
	if ( start > this.getProteinLength() ) {
		//  start is not within the protein bar so don't add
		return;  //   EARLY EXIT
	}
	if ( end < 1 ) {
		//  end is not within the protein bar so don't add
		return;  //   EARLY EXIT
	}
	if ( start < 1 ) {
		start = 1;
	}
	if ( end > this.getProteinLength() ) {
		end = this.getProteinLength();
	}
	
	var regionToSet = ProteinBarHighlightedRegion.constructEmptyProteinBarHighlightedRegion();
	regionToSet.setStart( start );  // property 's'
	regionToSet.setEnd( end );  // property 'e'
	regionToSet.setRegionUniqueId( ProteinBarHighlightedRegion.imageProteinBarDataManager.getNextRegionUniqueId() );

	var proteinBarHighlightedRegionEntry = regionToSet;
	this.proteinBarHighlightedRegions = [ proteinBarHighlightedRegionEntry ];
	this.proteinBarHighlightedAll = undefined;
	this._if_WholeBarHighlighted_SwitchTo_proteinBarHighlightedAll();
};

/**
 * Add to existing highlighted values.
 * 
 * Calls this._if_WholeBarHighlighted_SwitchTo_proteinBarHighlightedAll() at end of this function
 */
ImageProteinBarData.prototype.addProteinBarHighlightedRegion = function( params ) {
	var start = params.start;
	var end = params.end;
	var proteinLength = this.getProteinLength();
	if ( start > proteinLength ) {
		//  start is not within the protein bar so don't add
		console.log("addProteinBarHighlightedRegion(...): region start > proteinLength so ignoring protein region. "
				+ "region start: " + start 
				+ ", protein length: " + proteinLength );
		return;  //   EARLY EXIT
	}

//	if ( this.proteinBarHighlightedAll ) {
//		//  Exit since whole Protein Bar already highlighted
//		return;  //  EARLY EXIT
//	}
	if ( ( ! this.proteinBarHighlightedRegions ) || this.proteinBarHighlightedRegions.length === 0 ) {
		//  No regions set so set this and exit
		this.setProteinBarHighlightedRegion( params );
		return;  // EARLY EXIT
	}
	
	if ( start < 1 ) {
		start = 1;
	}
	if ( end > this.getProteinLength() ) {
		end = this.getProteinLength();
	}
	var regionToAdd = ProteinBarHighlightedRegion.constructEmptyProteinBarHighlightedRegion();
	regionToAdd.setStart( start );  // property 's'
	regionToAdd.setEnd( end );  // property 'e'
	
	//  Add to existing highlighted array, address overlapping regions
	//  Keep highlighted array in sorted order

	if ( ! this.proteinBarHighlightedRegions ) {
		this.proteinBarHighlightedRegions = [];
	}
	this.proteinBarHighlightedRegions.push( regionToAdd );

	//  Sort to keep highlighted array in sorted order
	this.proteinBarHighlightedRegions.sort(function( item1, item2 ) {
		if ( item1.getStart() !== item2.getStart() ) {
			return item1.getStart() - item2.getStart();
		}
		return item1.getEnd() - item2.getEnd();
	})
	
	// Combine overlapping regions.  If combine, assign new region unique id
	
	var regionCombined = null;
	var new_proteinBarHighlightedRegions = [];
	var lastAddedRegion = null;
	for ( var regionsIndex = 0; regionsIndex < this.proteinBarHighlightedRegions.length; regionsIndex++ ) {
		var region = this.proteinBarHighlightedRegions[ regionsIndex ];
		if ( lastAddedRegion === null ) {
			new_proteinBarHighlightedRegions.push( region );
			lastAddedRegion = region;
		} else { 
			if ( lastAddedRegion.e < region.s ) {
				//  No region overlap
				new_proteinBarHighlightedRegions.push( region );
				lastAddedRegion = region;
			} else {
				//  Region overlap so copy region.end to lastAddedRegion.end if needed
				if ( lastAddedRegion.e < region.e ) {
					//  region.end is after lastAddedRegion.end so extend lastAddedRegion and drop region
					lastAddedRegion.e = region.e;
					regionCombined = lastAddedRegion;
				}  // else drop region since fully contained within lastAddedRegion
			}
		}
	}
	
	if ( regionCombined ) {
		//  Change region unique id since the region start or end changed
		regionCombined.setRegionUniqueId( ProteinBarHighlightedRegion.imageProteinBarDataManager.getNextRegionUniqueId() );
	} else {
		// New region was not combined with any other so set it's regionUniqueId
		regionToAdd.setRegionUniqueId( ProteinBarHighlightedRegion.imageProteinBarDataManager.getNextRegionUniqueId() );
	}

	this.proteinBarHighlightedRegions = new_proteinBarHighlightedRegions;
	
	this._if_WholeBarHighlighted_SwitchTo_proteinBarHighlightedAll();
};

/**
 * If whole bar is selected by region and switch to setting the proteinBarHighlightedAll
 */
ImageProteinBarData.prototype._if_WholeBarHighlighted_SwitchTo_proteinBarHighlightedAll = function( ) {
	//   Check if whole bar is selected by region and switch to setting the proteinBarHighlightedAll
	if ( this.proteinBarHighlightedRegions && this.proteinBarHighlightedRegions.length === 1 ) {
		var proteinBarHighlightedRegionEntry = this.proteinBarHighlightedRegions[ 0 ];
		if ( proteinBarHighlightedRegionEntry.s <= 1 
				&& proteinBarHighlightedRegionEntry.e >= ( this.getProteinLength() ) ) {
			//  Only 1 region that selects the whole protein bar
			this.setProteinBarHighlightedAll();
		}
	}
};

/**
 * Get region from regionUID
 * 
 * return null if not found
 */
ImageProteinBarData.prototype.getRegionFromRegionUID = function( params ) {
	var regionUID = params.regionUID;
	if ( ! this.proteinBarHighlightedRegions ) {
		return null;
	}
	//  Find region containing this position and return it;
	for ( var regionsIndex = 0; regionsIndex < this.proteinBarHighlightedRegions.length ; regionsIndex++ ) {
		var region = this.proteinBarHighlightedRegions[ regionsIndex ];
		if ( region.getRegionUniqueId() === regionUID ) {
			return region;
		}
	}
	return null;
};

/**
 * Remove from existing highlighted values
 */
ImageProteinBarData.prototype.removeProteinBarHighlightedRegion = function( params ) {
	var position = params.position;
	if ( this.proteinBarHighlightedAll ) {
		this.proteinBarHighlightedAll = undefined;
		this.proteinBarHighlightedRegions = undefined;
		return;   // EARLY EXIT
	}
	if ( this.proteinBarHighlightedRegions === undefined || this.proteinBarHighlightedRegions === null ) {
		return;   // EARLY EXIT
	}
	//  Find region containing this position and remove it;
	for ( var regionsIndex = 0; regionsIndex < this.proteinBarHighlightedRegions.length ; regionsIndex++ ) {
		var region = this.proteinBarHighlightedRegions[ regionsIndex ];
		if ( region.s <= position && region.e >= position ) {
			if ( this.proteinBarHighlightedRegions.length === 1 ) {
				//  Only one region so clear the regions array to remove it
				this.proteinBarHighlightedRegions = undefined;
				break;
			} else {
				//  Remove the region from the array
//				var removedElement = 
				this.proteinBarHighlightedRegions.splice( regionsIndex, 1 );
				break;
			}
		}
	}
};

/**
 * Clear existing highlighted values
 */
ImageProteinBarData.prototype.clearProteinBarHighlighted = function( ) {
	this.proteinBarHighlightedAll = undefined;
	this.proteinBarHighlightedRegions = undefined;
};


/**
 * Clear existing highlighted values

 */
ImageProteinBarData.prototype.syncProteinBarHighlightedRegioToProvidedList = function( params ) {
	var syncRegionList = params.syncRegionList;
	if ( this.proteinBarHighlightedRegions ) {
		//  First remove regions in this.proteinBarHighlightedRegions that are not in syncRegionList by matching regionUniqueId
		var newListRemovedMissingRegionsproteinBarHighlightedRegions = [];
		for ( var existingEntriesIndex = 0; existingEntriesIndex < this.proteinBarHighlightedRegions.length; existingEntriesIndex++ ) {
			var existingEntry = this.proteinBarHighlightedRegions[ existingEntriesIndex ];
			for ( var syncEntriesIndex = 0; syncEntriesIndex < syncRegionList.length; syncEntriesIndex++ ) {
				var syncRegion = syncRegionList[ syncEntriesIndex ];
				if ( existingEntry.getRegionUniqueId() === syncRegion.getRegionUniqueId()  ) {
					newListRemovedMissingRegionsproteinBarHighlightedRegions.push( existingEntry );
					break;
				}
			}
		}
		this.proteinBarHighlightedRegions = newListRemovedMissingRegionsproteinBarHighlightedRegions;
	}
	// Add the regions in syncRegionList that don't have a region unique id
	
	for ( var syncEntriesIndex = 0; syncEntriesIndex < syncRegionList.length; syncEntriesIndex++ ) {
		var syncRegion = syncRegionList[ syncEntriesIndex ];
		if ( syncRegion.getRegionUniqueId() === undefined ) {
			var region = { start : syncRegion.getStart() , end : syncRegion.getEnd() };
			this.addProteinBarHighlightedRegion( region );
		}
	}
};


//////////////

/**
 * Get the data object to put on the Hash
 */
ImageProteinBarData.prototype.getHashDataObject = function() {
	
	var regionsForHashObject = this.getRegionsForHashObject();
	
	var hashDataObject = {};
	hashDataObject.a = regionsForHashObject;
	hashDataObject.pHlhAll = this.proteinBarHighlightedAll;
	hashDataObject.pRvrs = this.proteinReversed;
	hashDataObject.pOffst = this.proteinOffset;
	return hashDataObject;
};

/**
 * Get the Regions data array to put on the Hash
 */
ImageProteinBarData.prototype.getRegionsForHashObject = function() {
	if ( ! this.proteinBarHighlightedRegions ) {
		return undefined;
	}
	var regionsForHashObject = [];
	for ( var index = 0; index < this.proteinBarHighlightedRegions.length; index++ ) {
		var region = this.proteinBarHighlightedRegions[ index ];
		var regionForHashObject = region.getHashDataObject();
		regionsForHashObject.push( regionForHashObject );
	}
	return regionsForHashObject;
};

/**
 * Construct object of type ImageProteinBarData from hashDataObject.  The parameter hashDataObject comes from the Hash
 */
ImageProteinBarData.constructImageProteinBarDataFromHashDataObject = function( hashDataObject ) {
//	Create an instance from the constructor
	var imageProteinBarData = new ImageProteinBarData();
	if ( hashDataObject ) {
//		Copy data from hashDataObject
		if ( hashDataObject.a ) {
			imageProteinBarData.proteinBarHighlightedRegions = this.getRegionsFromHashObject( hashDataObject.a );
		}
		imageProteinBarData.proteinBarHighlightedAll = hashDataObject.pHlhAll;
		imageProteinBarData.proteinReversed = hashDataObject.pRvrs;
		imageProteinBarData.proteinOffset  = hashDataObject.pOffst;
		//  Backwards compatible:
		if ( hashDataObject.proteinId ) {
			imageProteinBarData.nrseqProteinId = hashDataObject.proteinId; // Store off for conversion to protein sequence id
		}
		if ( hashDataObject.proteinBarHighlightedAll ) {
			imageProteinBarData.proteinBarHighlightedAll = hashDataObject.proteinBarHighlightedAll;
		}
		if ( hashDataObject.proteinBarHighlightedRegions ) {
			imageProteinBarData.proteinBarHighlightedRegions = hashDataObject.proteinBarHighlightedRegions;
		}
		if ( hashDataObject.proteinReversed ) {
			imageProteinBarData.proteinReversed = hashDataObject.proteinReversed;
		}
		if ( hashDataObject.proteinOffset !== undefined ) {
			imageProteinBarData.proteinOffset  = hashDataObject.proteinOffset;
		}
		if ( hashDataObject.pHlghRgns ) {
			imageProteinBarData.proteinBarHighlightedRegions = this.getRegionsFromHashObject( hashDataObject.pHlghRgns );
		}
		//  END: Backwards compatible
		
		if ( imageProteinBarData.proteinOffset === undefined ) {
			imageProteinBarData.proteinOffset = 0;
		}
	}
	return imageProteinBarData;
};

/**
 * Construct regions from Hash object 
 */
ImageProteinBarData.getRegionsFromHashObject = function( hashDataObject ) {
	var regionsFromHashObject = [];
	for ( var index = 0; index < hashDataObject.length; index++ ) {
		var hashDataObjectRegionItem = hashDataObject[ index ];
		var regionFromHashObject = ProteinBarHighlightedRegion.constructProteinBarHighlightedRegionFromHashDataObject( hashDataObjectRegionItem );
		regionsFromHashObject.push( regionFromHashObject );
	}
	return regionsFromHashObject;
};

/**
 * Construct empty object of type ImageProteinBarData
 */
ImageProteinBarData.constructEmptyImageProteinBarData = function(  ) {
	//	Create an instance from the constructor
	var imageProteinBarData = new ImageProteinBarData();
	imageProteinBarData.proteinOffset = 0;
	return imageProteinBarData;
};

/////////////////////////////////////////////////

///    Protein Bar Highlighted Region Class

var ProteinBarHighlightedRegion = function() {

	/*
	 * Holds a single selected user region.
	 * 
	 * properties   's' and 'e' are used throughout so kept as is
	 * s : 		region start.
	 * e : 		region end.
	 * regionUniqueId  : 	unique id for region to serve for identifying this instance
	 * 						
	 */
	this.s = undefined; //  region start
	this.e = undefined; //  region end
	this.regionUniqueId = undefined; 
};

ProteinBarHighlightedRegion.prototype.getStart = function() {
	return this.s;
};
ProteinBarHighlightedRegion.prototype.setStart = function( regionsStart ) {
	this.s = regionsStart;
};
ProteinBarHighlightedRegion.prototype.getEnd = function() {
	return this.e;
};
ProteinBarHighlightedRegion.prototype.setEnd = function( regionEndParam ) {
	this.e = regionEndParam;
};
ProteinBarHighlightedRegion.prototype.getRegionUniqueId = function() {
	return this.regionUniqueId;
};
ProteinBarHighlightedRegion.prototype.setRegionUniqueId = function( regionUniqueIdParam ) {
	this.regionUniqueId = regionUniqueIdParam;
};



/**
 * Get the data object to put on the Hash
 */
ProteinBarHighlightedRegion.prototype.getHashDataObject = function() {
	var hashDataObject = {};
	hashDataObject.s = this.s;
	hashDataObject.e = this.e;
	hashDataObject.a = this.regionUniqueId;
	return hashDataObject;
};

/**
 * Construct object of type ImageProteinBarData from hashDataObject.  The parameter hashDataObject comes from the Hash
 */
ProteinBarHighlightedRegion.constructProteinBarHighlightedRegionFromHashDataObject = function( hashDataObject ) {
	var item = new ProteinBarHighlightedRegion();
	if ( hashDataObject ) {
		item.s = hashDataObject.s ;
		item.e = hashDataObject.e;
		item.regionUniqueId = hashDataObject.a;
		if ( ! item.regionUniqueId ) { //  assign regionUniqueId if not assigned
			item.regionUniqueId = ProteinBarHighlightedRegion.imageProteinBarDataManager.getNextRegionUniqueId();
		}
	}
	return item;
};


/**
 * Construct empty object of type ProteinBarHighlightedRegion
 */
ProteinBarHighlightedRegion.constructEmptyProteinBarHighlightedRegion = function(  ) {
	//	Create an instance from the constructor
	var proteinBarHighlightedRegion = new ProteinBarHighlightedRegion();
	return proteinBarHighlightedRegion;
};

export { ImageProteinBarDataManagerContructor, ImageProteinBarData, ProteinBarHighlightedRegion }
