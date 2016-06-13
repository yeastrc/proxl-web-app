
//  crosslink-image-viewer-per-protein-bar-data.js

//  Javascript for the viewMergedImage.jsp page


//   This file holds javascript for per protein bar data.   

//   This is a work in progress so it doesn't hold all the data yet 


//   This file also handles protein bar highlighting/selecting and de-selecting



//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";



////////////////////////////

/////     Protein Bar Data Manager


//   Constructor

var ImageProteinBarDataManager = function() {
		
	this.imageProteinBarDataList = [];
};

var ImageProteinBarDataManagerContructor = function() { 
	
	return new ImageProteinBarDataManager();
};
		

ImageProteinBarDataManager.prototype.addEntry = function( params ) {

	var arrayIndexInt 	= undefined;
	var entry 			= undefined;

	if ( params ) {

		arrayIndexInt 	= params.arrayIndexInt; // Optional, add next if not set
		entry 			= params.entry;  		//  Optional
	}
	
	if ( this.imageProteinBarDataList[ arrayIndexInt ] ) {
		
		//  Already exists
		
		throw "Entry in imageProteinBarDataList already exists for arrayIndexInt: " + arrayIndexInt;
	}
	
	if ( arrayIndexInt !== undefined && arrayIndexInt !== null ) {

		if ( entry ) {

			this.imageProteinBarDataList[ arrayIndexInt ] = entry;
			
			entry.setContainingImageProteinBarDataManager( { containingImageProteinBarDataManager : this } );

		} else {

			var newEntry = ImageProteinBarData.constructEmptyImageProteinBarData();
			
			newEntry.setContainingImageProteinBarDataManager( { containingImageProteinBarDataManager : this } );
			
			this.imageProteinBarDataList[ arrayIndexInt ] = newEntry;
		} 
		
	} else {

		if ( entry ) {

			entry.setContainingImageProteinBarDataManager( { containingImageProteinBarDataManager : this } );
			
			this.imageProteinBarDataList.push( entry );

		} else {

			var newEntry = ImageProteinBarData.constructEmptyImageProteinBarData();

			newEntry.setContainingImageProteinBarDataManager( { containingImageProteinBarDataManager : this } );

			this.imageProteinBarDataList.push( newEntry );
		}
	}
};


//  Get all items

ImageProteinBarDataManager.prototype.getAllItems = function( ) {

	return this.imageProteinBarDataList;
},


ImageProteinBarDataManager.prototype.getItemByIndex = function( params ) {

	var arrayIndexInt = params.arrayIndexInt;
	
	var item = this.imageProteinBarDataList[ arrayIndexInt ];
	
	if ( ! item ) {
		
		throw "entry not found in imageProteinBarDataList for index: " + arrayIndexInt;
	}
	
	return item;
},

ImageProteinBarDataManager.prototype.removeItemByIndex = function( params ) {
	
	var arrayIndexInt = params.arrayIndexInt;
	
//	var removedItem = 
	this.imageProteinBarDataList.splice( arrayIndexInt, 1 );
};

ImageProteinBarDataManager.prototype.updateItemOrder = function( params ) {
	
	var elementPrevPositions = params.elementPrevPositions;
	
	var newImageProteinBarDataList = [];
	
	
	for ( var newPosition = 0; newPosition < elementPrevPositions.length; newPosition++ ) {
		
		var elementPrevPositionObj = elementPrevPositions[ newPosition ];
		
		var elementPrevPositionInt = elementPrevPositionObj.prevPosition;
		
		var elementAtPrevPosition = this.imageProteinBarDataList[ elementPrevPositionInt ];
		
		newImageProteinBarDataList.push( elementAtPrevPosition );
	}
	
	this.imageProteinBarDataList = newImageProteinBarDataList;
};


ImageProteinBarDataManager.prototype.clearItems = function( params ) {
	
	this.imageProteinBarDataList = [];
};



////

////    URL Hash Specific Processing

//  Get objects for Hash

ImageProteinBarDataManager.prototype.getArrayOfObjectsForHash = function( ) {
	
	var imageProteinBarDataListResult = [];
	
	for ( var index = 0; index < this.imageProteinBarDataList.length; index++ ) {
		
		var imageProteinBarDataEntry = this.imageProteinBarDataList[ index ];
		
		var imageProteinBarDataForHash = imageProteinBarDataEntry.getHashDataObject();

		imageProteinBarDataListResult.push( imageProteinBarDataForHash );
	}
	
	return imageProteinBarDataListResult;
};
	

ImageProteinBarDataManager.prototype.replaceInternalObjectsWithObjectsInHash = function( params ) {
	
	var proteinBarData = params.proteinBarData;
	var currentProteinIdsArray = params.currentProteinIdsArray;
	
	//  Copy current protein ids into an object/map for easier searching
	
	var currentProteinIdsObject = {};
	
	if ( currentProteinIdsArray ) {

		for ( var currentProteinIdsArrayIndex = 0; currentProteinIdsArrayIndex < currentProteinIdsArray.length; currentProteinIdsArrayIndex++ ) {
			
			var currentProteinIdsArrayEntry = currentProteinIdsArray[ currentProteinIdsArrayIndex ];
			
			var currentProteinIdsArrayEntryString = currentProteinIdsArrayEntry.toString();
			
			currentProteinIdsObject[ currentProteinIdsArrayEntryString ] = true;
		}
	}
	
	this.imageProteinBarDataList = [];
	
	if ( proteinBarData ) {
		
		for ( var index = 0; index < proteinBarData.length; index++ ) {
			
			var proteinBarDataHashItem = proteinBarData[ index ];
			
			var proteinBarDataItem = 
				ImageProteinBarData.constructImageProteinBarDataFromHashDataObject( proteinBarDataHashItem ); 

			//  If protein id in proteinBarDataItem is not in currentProteinIdsArray
			//   Drop the proteinBarDataItem
			
			if ( ! currentProteinIdsObject[ proteinBarDataItem.getProteinId() ] ) {
				
				continue;  // EARLY continue
			}
				

			
			proteinBarDataItem.setContainingImageProteinBarDataManager( { containingImageProteinBarDataManager : this } );

			this.imageProteinBarDataList.push( proteinBarDataItem );
		}
	}
};
	
////////

///  Update objects for data loaded from server

//  Update Protein Lengths

ImageProteinBarDataManager.prototype.updateProteinLengths = function( params ) {
	
	var proteinLengths = params.proteinLengths;
	
	for ( var index = 0; index < this.imageProteinBarDataList.length; index++ ) {
		
		var imageProteinBarDataEntry = this.imageProteinBarDataList[ index ];
		
		var proteinLengthForProteinId = proteinLengths[ imageProteinBarDataEntry.getProteinId() ];
		
		if ( proteinLengthForProteinId ) {

			imageProteinBarDataEntry.setProteinLength( { proteinLength : proteinLengthForProteinId } );
		}
	}
};



////////

///   Add on functions for specific parts


//  Proteins Reversed

ImageProteinBarDataManager.prototype.clearAllProteinBarsReversed = function(  ) {
	
	for ( var index = 0; index < this.imageProteinBarDataList.length; index++ ) {

		var imageProteinBarDataListEntry = this.imageProteinBarDataList[ index ];

		imageProteinBarDataListEntry.setProteinReversed( { proteinReversed : undefined } );
	};
};

//  Protein Offsets

ImageProteinBarDataManager.prototype.clearAllProteinBarsOffsets = function(  ) {

	for ( var index = 0; index < this.imageProteinBarDataList.length; index++ ) {

		var imageProteinBarDataListEntry = this.imageProteinBarDataList[ index ];

		imageProteinBarDataListEntry.setProteinOffset( { proteinOffset : 0 } );
	};
};


ImageProteinBarDataManager.prototype.addToAllProteinOffsets = function( params ) {

	var offsetChange = params.offsetChange;
	
	for ( var index = 0; index < this.imageProteinBarDataList.length; index++ ) {

		var imageProteinBarDataListEntry = this.imageProteinBarDataList[ index ];

		imageProteinBarDataListEntry.addToProteinOffset( { offsetChange : offsetChange } );
	};
};




//   Highlighted Protein bars

ImageProteinBarDataManager.prototype.isAnyProteinBarsHighlighted = function(  ) {
	

	for ( var index = 0; index < this.imageProteinBarDataList.length; index++ ) {

		var imageProteinBarDataListEntry = this.imageProteinBarDataList[ index ];

		if ( imageProteinBarDataListEntry.isAnyOfProteinBarHighlighted() ) {

			return true;
		}
		;
	}

	return false;
};


ImageProteinBarDataManager.prototype.exactly_One_ProteinBar_Highlighted = function(  ) {

	var proteinBarHighlightCount = 0;

	for ( var index = 0; index < this.imageProteinBarDataList.length; index++ ) {

		var imageProteinBarDataListEntry = this.imageProteinBarDataList[ index ];

		if ( imageProteinBarDataListEntry.isAnyOfProteinBarHighlighted() ) {

			proteinBarHighlightCount++;
		}
		;
	}
	
	if ( proteinBarHighlightCount === 1 ) {
		
		return true;
	}

	return false;
};


ImageProteinBarDataManager.prototype.moreThan_One_ProteinBar_Highlighted = function(  ) {

	var proteinBarHighlightCount = 0;

	for ( var index = 0; index < this.imageProteinBarDataList.length; index++ ) {

		var imageProteinBarDataListEntry = this.imageProteinBarDataList[ index ];

		if ( imageProteinBarDataListEntry.isAnyOfProteinBarHighlighted() ) {

			proteinBarHighlightCount++;
		}
		;
	}
	
	if ( proteinBarHighlightCount > 1 ) {
		
		return true;
	}

	return false;
};

ImageProteinBarDataManager.prototype.clearAllProteinBarsHighlighted = function(  ) {
	
	for ( var index = 0; index < this.imageProteinBarDataList.length; index++ ) {

		var imageProteinBarDataListEntry = this.imageProteinBarDataList[ index ];

		imageProteinBarDataListEntry.clearProteinBarHighlighted();
	}
};


		
		


////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////


//   Data for a Single Protein Bar 


//        Constructor for an object for the data for a single protein bar displayed

var ImageProteinBarData = function(  ) {

	this.containingImageProteinBarDataManager = undefined;
	
	
	this.proteinId = undefined;
	
	this.proteinBarHighlightedRegions = undefined;
	
	this.proteinBarHighlightedAll = undefined;
	
	
};

//  Clear properties that no longer apply after protein id change

ImageProteinBarData.prototype.clearPropertiesOnProteinIdChange = function() {
	
	this.proteinBarHighlightedRegions = undefined;
	
	this.proteinBarHighlightedAll = undefined;
	
	this.proteinLength = undefined;
	
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


//  Protein Id

ImageProteinBarData.prototype.getProteinId = function( ) {
	
	return this.proteinId;
};
ImageProteinBarData.prototype.setProteinId = function( params ) {
	
	var proteinId = params.proteinId;
	var proteinLength = params.proteinLength;
	
	if ( this.proteinId !== proteinId ) {
		
		this.clearPropertiesOnProteinIdChange();
	}
	
	this.proteinId = proteinId;
	this.proteinLength = proteinLength;
};

////////////////

//  Protein Length

ImageProteinBarData.prototype.getProteinLength = function( ) {

	return this.proteinLength;
};
ImageProteinBarData.prototype.setProteinLength = function( params ) {
	
	var proteinLength = params.proteinLength;
	
	this.proteinLength = proteinLength;
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
				
		if ( proteinBarHighlightedRegionsEntry.start <= position 
				&& proteinBarHighlightedRegionsEntry.end >= position ) {
			
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

		if ( ( proteinBarHighlightedRegionsEntry.start <= position_1 
				&& proteinBarHighlightedRegionsEntry.end >= position_1 ) //  The select block contains position_1
				|| ( proteinBarHighlightedRegionsEntry.start <= position_2 
						&& proteinBarHighlightedRegionsEntry.end >= position_2 ) //  The select block contains position_2
						|| ( proteinBarHighlightedRegionsEntry.start >= position_1
								&& proteinBarHighlightedRegionsEntry.end <= position_2 ) //  The select block is within position_1 and position_2
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
	
	var imageProteinBarDataItems = this.containingImageProteinBarDataManager.getAllItems();
	
	if ( ( ! imageProteinBarDataItems ) 
			|| imageProteinBarDataItems.length === 0 
			|| imageProteinBarDataItems.length === 1 ) {
		
		this.proteinBarHighlightedAll = undefined;
	}
};

//  Replace existing highlighted values

ImageProteinBarData.prototype.setProteinBarHighlightedRegion = function( params ) {
	

	var start = params.start;
	var end = params.end;
	

	if ( start > this.proteinLength ) {
		
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
	
	if ( end > this.proteinLength ) {
		
		end = this.proteinLength;
	}
	
	var regionToSet = { start : start, end : end };
	
	var proteinBarHighlightedRegionEntry = regionToSet;

	this.proteinBarHighlightedRegions = [ proteinBarHighlightedRegionEntry ];
	
	this.proteinBarHighlightedAll = undefined;
	
	this._if_WholeBarHighlighted_SwitchTo_proteinBarHighlightedAll();
};


//  Add to existing highlighted values.   No optimization yet to set this.proteinBarHighlightedAll if whole sequence is selected

ImageProteinBarData.prototype.addProteinBarHighlightedRegion = function( params ) {

	var start = params.start;
	var end = params.end;
	

	if ( start > this.proteinLength ) {
		
		//  start is not within the protein bar so don't add
		
		return;  //   EARLY EXIT
	}
	
	if ( start < 1 ) {
		
		start = 1;
	}
	
	if ( end > this.proteinLength ) {
		
		end = this.proteinLength;
	}
	
	var regionToAdd = { start : start, end : end };
	
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
		
		} else if ( regionToAdd.start < region.start ) {
			
			new_proteinBarHighlightedRegions.push( regionToAdd );
			new_proteinBarHighlightedRegions.push( region );
			
			newRegionAdded = true;
		
		} else if ( regionToAdd.start === region.start ) {
			
			if ( regionToAdd.end >= region.end ) {

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
			
			if ( lastAddedRegion.end < region.start ) {

				//  No region overlap

				new_proteinBarHighlightedRegions.push( region );

				lastAddedRegion = region;
				
			} else {
				
				//  Region overlap so copy region.end to lastAddedRegion.end if needed
			
				if ( lastAddedRegion.end < region.end ) {
			
					//  region.end is after lastAddedRegion.end so extend lastAddedRegion and drop region
					
					lastAddedRegion.end = region.end;
				
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
		
		if ( proteinBarHighlightedRegionEntry.start <= 1 
				&& proteinBarHighlightedRegionEntry.end >= ( this.proteinLength ) ) {
			
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
		
		if ( region.start <= position && region.end >= position ) {
			
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
	
	hashDataObject.proteinId = this.proteinId;
	
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

		imageProteinBarData.proteinId = hashDataObject.proteinId;

		imageProteinBarData.proteinBarHighlightedAll = hashDataObject.pHlhAll;
		imageProteinBarData.proteinBarHighlightedRegions = hashDataObject.pHlghRgns;

		imageProteinBarData.proteinReversed = hashDataObject.pRvrs;
		imageProteinBarData.proteinOffset  = hashDataObject.pOffst;
		
		//  Backwards compatible:
		
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




