
//  crosslink-image-viewer-per-protein-bar-data.js

//  Javascript for the viewMergedImage.jsp page


//   This file holds javascript for per protein bar data.   

//   This is a work in progress so it doesn't hold all the data yet 


//   This file also handles protein bar highlighting/selecting and de-selecting



//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";


//   All references to proteinId or protein_id are actually referencing the protein sequence id


////////////////////////////

/////     Protein Bar Data Manager


//   Constructor

var ImageProteinBarDataManager = function() {
		
	this.barData = { };

};

var ImageProteinBarDataManagerContructor = function() { 
	
	return new ImageProteinBarDataManager();
};
		

ImageProteinBarDataManager.prototype.addEntry = function( uid, entry ) {
	


		if ( entry ) {

			entry.setContainingImageProteinBarDataManager( { containingImageProteinBarDataManager : this } );

			entry.uid = uid;
			entry.pid = _indexManager.getProteinIdForUID( uid );
			
			this.barData[ uid ] = entry;

		} else {

			var newEntry = ImageProteinBarData.constructEmptyImageProteinBarData();
			
			newEntry.setContainingImageProteinBarDataManager( { containingImageProteinBarDataManager : this } );

			newEntry.uid = uid;
			newEntry.pid = _indexManager.getProteinIdForUID( uid );

			this.barData[ uid ] = newEntry;
		}
		
};


//  Get all items

ImageProteinBarDataManager.prototype.getAllItems = function( ) {

	return this.barData;
};


ImageProteinBarDataManager.prototype.getItemByUID = function( uid ) {
	
	var item = this.barData[ uid ];
	
	if ( ! item ) {
		throw "entry not found in barData for uid: " + uid;
	}
	
	return item;
};

ImageProteinBarDataManager.prototype.getItemByIndex = function( index ) {
	
	var entry = _indexManager.getProteinArray()[ index ];

	if ( ! entry ) {
		throw "entry not found in barData for index: " + index;
	}
	
	return this.getItemByUID( entry.uid );
};


ImageProteinBarDataManager.prototype.removeItemByUID = function( uid ) {
	delete this.barData[ uid ];
};



ImageProteinBarDataManager.prototype.clearItems = function() {
	
	this.barData = { };
};



////

////    URL Hash Specific Processing

//  Get objects for Hash

ImageProteinBarDataManager.prototype.getObjectsForHash = function( ) {
	
	var imageProteinBarDataResult = { };
	
	
	var keys = Object.keys( this.getAllItems() );
	
	for( var i = 0; i < keys.length; i++ ) {
		var key = keys[ i ];
	    
		var imageProteinBarDataEntry = this.barData[ key ];
		var imageProteinBarDataForHash = imageProteinBarDataEntry.getHashDataObject();
		
		imageProteinBarDataResult[ key ] = imageProteinBarDataForHash;
	}
	
	
	return imageProteinBarDataResult;
};
	
////

//  Update Internal ProteinBarData objects with objects in the Page Hash

ImageProteinBarDataManager.prototype.replaceInternalObjectsWithObjectsInHash = function( proteinBarData ) {
	
	this.clearItems();
	
	if ( proteinBarData ) {
		
		var keys = Object.keys( proteinBarData );
		
		for( var i = 0; i < keys.length; i++ ) {
			var key = keys[ i ];
			
			var proteinBarDataHashItem = proteinBarData[ key ];
		
			var proteinBarDataItem = ImageProteinBarData.constructImageProteinBarDataFromHashDataObject( proteinBarDataHashItem ); 
			proteinBarDataItem.setContainingImageProteinBarDataManager( { containingImageProteinBarDataManager : this } );

			this.addEntry( key, proteinBarDataItem );
			
		}
		
	}
};

////////

///   Add on functions for specific parts


//  Proteins Reversed

ImageProteinBarDataManager.prototype.clearAllProteinBarsReversed = function(  ) {
	
	var keys = Object.keys( this.getAllItems() );
	
	for( var i = 0; i < keys.length; i++ ) {
		var key = keys[ i ];
	    
		var imageProteinBarDataEntry = this.barData[ key ];

		imageProteinBarDataEntry.setProteinReversed( { proteinReversed : undefined } );
	}
};

//  Protein Offsets

ImageProteinBarDataManager.prototype.clearAllProteinBarsOffsets = function(  ) {

	var keys = Object.keys( this.getAllItems() );
	
	for( var i = 0; i < keys.length; i++ ) {
		var key = keys[ i ];
	    
		var imageProteinBarDataEntry = this.barData[ key ];

		imageProteinBarDataEntry.setProteinOffset( { proteinOffset : 0 } );
	}
};


ImageProteinBarDataManager.prototype.addToAllProteinOffsets = function( params ) {

	var offsetChange = params.offsetChange;
	
	var keys = Object.keys( this.getAllItems() );
	
	for( var i = 0; i < keys.length; i++ ) {
		var key = keys[ i ];
	    
		var imageProteinBarDataEntry = this.barData[ key ];

		imageProteinBarDataEntry.addToProteinOffset( { offsetChange : offsetChange } );
	}
};




//   Highlighted Protein bars

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

///

ImageProteinBarDataManager.prototype.clearAllProteinBarsHighlighted = function(  ) {
	
	var keys = Object.keys( this.getAllItems() );
	
	for( var i = 0; i < keys.length; i++ ) {
		var key = keys[ i ];
	    
		var imageProteinBarDataEntry = this.barData[ key ];

		imageProteinBarDataEntry.clearProteinBarHighlighted();
	}
};

///////////

//   For Conversion from NRSEQ Protein Id to Protein Sequence Id


//  return true if any imageProteinBarDataListEntry contains 

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
////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////


//   Data for a Single Protein Bar 


//        Constructor for an object for the data for a single protein bar displayed

var ImageProteinBarData = function(  ) {

	this.containingImageProteinBarDataManager = undefined;
		
	this.proteinBarHighlightedRegions = undefined;
	
	this.proteinBarHighlightedAll = undefined;
	
	
};

//  Clear properties that no longer apply after protein id change

ImageProteinBarData.prototype.clearPropertiesOnProteinIdChange = function() {
	
	this.proteinBarHighlightedRegions = undefined;
	
	this.proteinBarHighlightedAll = undefined;
		
	this.proteinReversed = undefined;
	
	this.proteinOffset = 0;
};


//  containing ImageProteinBarDataManager

ImageProteinBarData.prototype.getContainingImageProteinBarDataManager = function( ) {

	return this.containingImageProteinBarDataManager;
};
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

//  Add value to current this.proteinOffset

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


//  Is any of the protein bar highlighted

ImageProteinBarData.prototype.isAnyOfProteinBarHighlighted = function( ) {

	if ( this.proteinBarHighlightedAll ) {
		
		return true;
	}
	
	if ( this.proteinBarHighlightedRegions && this.proteinBarHighlightedRegions.length > 0 ) {

		return true;
	}

	return false;
};


//  Is all of the protein bar highlighted

ImageProteinBarData.prototype.isAllOfProteinBarHighlighted = function( ) {

	if ( this.proteinBarHighlightedAll ) {
		
		return true;
	}

	return false;
};


//   Selected Region count.   Return 1 if all of protein bar highlighted

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

//  If there are 2 positions passed in, rules are followed to determine if the link between them should be highlighted.

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



//  Internal.    Is the protein bar highlighted for this single position

ImageProteinBarData.prototype._isProteinBarHighlightedAtSinglePosition = function( position ) {

	var regionIndexContainingPosition = this.indexOfProteinBarHighlightedRegionAtSinglePosition( position );
	
	if ( regionIndexContainingPosition !== -1 ) {
		
		return true;
	}

	return false;
};


//  Index of the protein bar highlighted region for this single position

//   Index means the region number starting at zero for the number of regions on the protein bar

ImageProteinBarData.prototype.indexOfProteinBarHighlightedRegionAtSinglePosition = function( position ) {
	
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


///////////////////////////

//  Is protein bar highlighted anywhere between two positions.  

//  Is the protein bar highlighted anywhere between the two positions, inclusive of the positions

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



///////////////////////////


//  Index of the First protein bar highlighted region between these 2 positions, inclusive of the positions

//  Index means the region number starting at zero for the number of regions on the protein bar

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



//  Get highlighted values as array.  Only use for drawing rectangles to display selected regions

ImageProteinBarData.prototype.getProteinBarHighlightedRegionsArray = function() {

	return this.proteinBarHighlightedRegions;
};


//  Replace existing highlighted values

ImageProteinBarData.prototype.setProteinBarHighlightedAll = function( ) {

	this.proteinBarHighlightedRegions = undefined;
	
	this.proteinBarHighlightedAll = true;
	
	//  If this is the only bar, unhighlight it.
	
	var numItems = Object.keys( this.containingImageProteinBarDataManager.getAllItems() ).length;
	
	if ( ( ! numItems ) || numItems === 0 || numItems === 1 ) {
		this.proteinBarHighlightedAll = undefined;
	}
};


ImageProteinBarData.prototype.getProteinId = function() {
	return this.pid;
};

ImageProteinBarData.prototype.getProteinLength = function() {
	return _proteinLengths[ this.getProteinId() ];
};


//  Replace existing highlighted values

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
	
	var regionToSet = { s : start, e : end };
	
	var proteinBarHighlightedRegionEntry = regionToSet;

	this.proteinBarHighlightedRegions = [ proteinBarHighlightedRegionEntry ];
	
	this.proteinBarHighlightedAll = undefined;
	
	this._if_WholeBarHighlighted_SwitchTo_proteinBarHighlightedAll();
};


//  Add to existing highlighted values.   No optimization yet to set this.proteinBarHighlightedAll if whole sequence is selected

ImageProteinBarData.prototype.addProteinBarHighlightedRegion = function( params ) {

	var start = params.start;
	var end = params.end;
	

	if ( start > this.getProteinLength() ) {
		
		//  start is not within the protein bar so don't add
		
		return;  //   EARLY EXIT
	}
	
	if ( start < 1 ) {
		
		start = 1;
	}
	
	if ( end > this.getProteinLength() ) {
		
		end = this.getProteinLength();
	}
	
	var regionToAdd = { s : start, e : end };
	
//	if ( this.proteinBarHighlightedAll ) {
//		
//		//  Exit since whole Protein Bar already highlighted
//		
//		return;  //  EARLY EXIT
//	}
	
	if ( ( ! this.proteinBarHighlightedRegions ) || this.proteinBarHighlightedRegions.length === 0 ) {
		
		//  No regions set so set this and exit
		
		this.setProteinBarHighlightedRegion( params );
		
		return;  // EARLY EXIT
	}

	
	//  Add to existing highlighted array, address overlapping regions
	
	//  Keep highlighted array in sorted order

	//  First add new region into the array in sorted order, ascending start, descending end
	
	var newRegionAdded = false;
	
	var new_proteinBarHighlightedRegions = [];  //  Done by building a new highlighted array.
	
	for ( var regionsIndex = 0; regionsIndex < this.proteinBarHighlightedRegions.length; regionsIndex++ ) {
		
		var region = this.proteinBarHighlightedRegions[ regionsIndex ];
		
		if ( newRegionAdded ) {
			
			//  New region added already so now just copy the existing regions
		
			new_proteinBarHighlightedRegions.push( region );
		
		} else if ( regionToAdd.s < region.s ) {
			
			new_proteinBarHighlightedRegions.push( regionToAdd );
			new_proteinBarHighlightedRegions.push( region );
			
			newRegionAdded = true;
		
		} else if ( regionToAdd.s === region.s ) {
			
			if ( regionToAdd.e >= region.e ) {

				new_proteinBarHighlightedRegions.push( regionToAdd );
				new_proteinBarHighlightedRegions.push( region );
				
				newRegionAdded = true;
				
			} else {

				new_proteinBarHighlightedRegions.push( region );
				new_proteinBarHighlightedRegions.push( regionToAdd );
				
				newRegionAdded = true;
			}
			
		} else {
			
			new_proteinBarHighlightedRegions.push( region );
		}
	}
	
	if ( ! newRegionAdded ) {
		
		//  New Region not added yet since it sorts after the existing regions. Add it here
		
		new_proteinBarHighlightedRegions.push( regionToAdd );
	}

	this.proteinBarHighlightedRegions = new_proteinBarHighlightedRegions;
	
	//  Second Combine overlapping regions

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
				
				}  // else drop region since fully contained within lastAddedRegion
			}
		}
	}

	this.proteinBarHighlightedRegions = new_proteinBarHighlightedRegions;
	
	this._if_WholeBarHighlighted_SwitchTo_proteinBarHighlightedAll();
	
};



//  If whole bar is selected by region and switch to setting the proteinBarHighlightedAll

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


//  Remove from existing highlighted values

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


// Clear existing highlighted values

ImageProteinBarData.prototype.clearProteinBarHighlighted = function( ) {
	
	this.proteinBarHighlightedAll = undefined;
	
	this.proteinBarHighlightedRegions = undefined;
};


//////////

//  Get the data object to put on the Hash

ImageProteinBarData.prototype.getHashDataObject = function() {
	
	var hashDataObject = {};
		
	hashDataObject.pHlhAll = this.proteinBarHighlightedAll;
	hashDataObject.pHlghRgns = this.proteinBarHighlightedRegions;
	hashDataObject.pRvrs = this.proteinReversed;
	hashDataObject.pOffst = this.proteinOffset;
	
	return hashDataObject;
};



///  Construct object of type ImageProteinBarData from hashDataObject.  The parameter hashDataObject comes from the Hash

ImageProteinBarData.constructImageProteinBarDataFromHashDataObject = function( hashDataObject ) {

//	Create an instance from the constructor

	var imageProteinBarData = new ImageProteinBarData();

	if ( hashDataObject ) {

//		Copy data from hashDataObject

		imageProteinBarData.proteinBarHighlightedAll = hashDataObject.pHlhAll;
		imageProteinBarData.proteinBarHighlightedRegions = hashDataObject.pHlghRgns;

		imageProteinBarData.proteinReversed = hashDataObject.pRvrs;
		imageProteinBarData.proteinOffset  = hashDataObject.pOffst;
		
		//  Backwards compatible:
		
		if ( hashDataObject.proteinId ) {
			
			imageProteinBarData.nrseqProteinId = hashDataObject.proteinId; // Store off for conversion to protein sequence id
		}
		
		if ( hashDataObject.proteinBarHighlightedAll ) {

			imageProteinBarData.proteinBarHighlightedAll = hashDataObject.proteinBarHighlightedAll;
		}
		if ( hashDataObject.proteinBarHighlightedAll ) {

			imageProteinBarData.proteinBarHighlightedRegions = hashDataObject.proteinBarHighlightedRegions;
		}

		if ( hashDataObject.proteinReversed ) {

			imageProteinBarData.proteinReversed = hashDataObject.proteinReversed;
		}
		if ( hashDataObject.proteinOffset !== undefined ) {

			imageProteinBarData.proteinOffset  = hashDataObject.proteinOffset;
		}
		
		
		
		if ( imageProteinBarData.proteinOffset === undefined ) {
			
			imageProteinBarData.proteinOffset = 0;
		}
	}

	return imageProteinBarData;
};



//////////

///  Construct empty object of type ImageProteinBarData 

ImageProteinBarData.constructEmptyImageProteinBarData = function(  ) {

	//	Create an instance from the constructor

	var imageProteinBarData = new ImageProteinBarData();
	
	imageProteinBarData.proteinOffset = 0;

	return imageProteinBarData;
};




