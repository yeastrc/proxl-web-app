/**
 * crosslink-image-viewer-link-exclusion-manager.js
 * 
 * Javascript for the viewMergedImage.jsp page
 * 
 * This file holds javascript for link exclusion data.  
 * 
 * Link exclusion means dimming and disabling links that have excluded.
 * 
 * The method of selecting links to exclude is to choose a pair of ( selected protein and/or selected protein region )
 */


// JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

////////////////////////////
//   All references to proteinId or protein_id are actually referencing the protein sequence id
////////////////////////////

/////     Link Exclusion Data Manager

/**
 * Constructor
 * 
 * @constructor
 */
var LinkExclusionDataManager = function( params ) {
	var indexManager = params.indexManager;
	var imageProteinBarDataManager = params.imageProteinBarDataManager;
	this.indexManager = indexManager;
	this.imageProteinBarDataManager = imageProteinBarDataManager;

	this.exclusionDataLinksTwoEndPoints = []; //  For Crosslinks and Looplinks.  Not used for Monolinks if they are ever added
	
};


/**
 * Function that returns a new object using Constructor LinkExclusionDataManager()
 */
var LinkExclusionDataManagerContructor = function( params ) { 
	
	var linkExclusionDataManager =  new LinkExclusionDataManager( params );
	return linkExclusionDataManager;
};

/**
 * 
 * 
 */
LinkExclusionDataManager.prototype.getExclusionList = function() {

	if ( ! this.exclusionDataLinksTwoEndPoints ) {
		return [];
	}
	return this.exclusionDataLinksTwoEndPoints;
};


/**
 * Sort exclusions on protein then region order
 * 
 */
LinkExclusionDataManager.prototype.sortExclusions = function() {

	if ( this.exclusionDataLinksTwoEndPoints ) {
		
		//  First generate an array of the proteins and regions in order of displayed proteins and regions
		
		var proteinUIDregionUID_orderArray = [];
		
		var proteinArray = this.indexManager.getProteinArray();
		
		for ( var proteinArrayIndex = 0; proteinArrayIndex < proteinArray.length; proteinArrayIndex++ ) {
			var proteinArrayEntry = proteinArray[ proteinArrayIndex ];
			var imageProteinBarDataItem = this.imageProteinBarDataManager.getItemByUID( proteinArrayEntry.uid );
			if( !imageProteinBarDataItem ) {
				throw Error( "ERROR: Have no entry in protein bar data manager for uid:" + proteinArrayEntry.uid );
			}
			//  Entry for protein UID with no region
			proteinUIDregionUID_orderArray.push( { proteinUID : proteinArrayEntry.uid } );

			//  Add regions
			var regionsArray = imageProteinBarDataItem.getProteinBarHighlightedRegionsArray();
			
			if ( regionsArray && regionsArray.length > 0 ) {
				for ( var regionsArrayIndex = 0; regionsArrayIndex < regionsArray.length; regionsArrayIndex++ ) {
					var regionEntry = regionsArray[ regionsArrayIndex ];
					var regionUniqueId = regionEntry.getRegionUniqueId();
					//  Entry for protein UID with region
					proteinUIDregionUID_orderArray.push( { proteinUID : proteinArrayEntry.uid, regionUID : regionUniqueId } );
				}
			}
		}
		
		var getIndexForProteinUIDRegionUID = function( proteinUID, regionUID ) {
			for ( var proteinUIDregionUID_orderArrayIndex = 0; proteinUIDregionUID_orderArrayIndex < proteinUIDregionUID_orderArray.length; proteinUIDregionUID_orderArrayIndex++ ) {
				var proteinUIDregionUID_orderArrayItem = proteinUIDregionUID_orderArray[ proteinUIDregionUID_orderArrayIndex ];
				if ( proteinUIDregionUID_orderArrayItem.proteinUID === proteinUID && proteinUIDregionUID_orderArrayItem.regionUID === regionUID ) {
					return proteinUIDregionUID_orderArrayIndex;
				}
			}
			throw Error( "in getIndexForProteinUIDRegionUID(...), index not found for proteinUID: " + proteinUID + ", regionUID: " + regionUID );
		}
		
		//  Sort the array
		this.exclusionDataLinksTwoEndPoints.sort(function( item_1, item_2 ) {
			var item_1_part_1_Index = getIndexForProteinUIDRegionUID( item_1.getProteinUID_1(), item_1.getRegionUID_1() );
			var item_2_part_1_Index = getIndexForProteinUIDRegionUID( item_2.getProteinUID_1(), item_2.getRegionUID_1() );
			if ( item_1_part_1_Index != item_2_part_1_Index ) {
				return item_1_part_1_Index - item_2_part_1_Index;
			}
			var item_1_part_2_Index = getIndexForProteinUIDRegionUID( item_1.getProteinUID_2(), item_1.getRegionUID_2() );
			var item_2_part_2_Index = getIndexForProteinUIDRegionUID( item_2.getProteinUID_2(), item_2.getRegionUID_2() );
			return item_1_part_2_Index - item_2_part_2_Index;
		})
	}
};


/**
 * Remove exclusions that reference protein or region UID that no longer exist
 * 
 */
LinkExclusionDataManager.prototype.removeOrphanRecords = function() {

	if ( this.exclusionDataLinksTwoEndPoints ) {
		var new_exclusionDataLinksTwoEndPoints = [];
		for ( var index = 0; index < this.exclusionDataLinksTwoEndPoints.length; index++ ) {
			var entry = this.exclusionDataLinksTwoEndPoints[ index ];
			if ( this._IsProteinUID_RegionUID_Orphan( { proteinUID : entry.getProteinUID_1(), regionUID : entry.getRegionUID_1() } )
					|| this._IsProteinUID_RegionUID_Orphan( { proteinUID : entry.getProteinUID_2(), regionUID : entry.getRegionUID_2() } ) ) {
				
			} else {
				new_exclusionDataLinksTwoEndPoints.push( entry );
			}
		}
		this.exclusionDataLinksTwoEndPoints = new_exclusionDataLinksTwoEndPoints;
	}

	this.sortExclusions();
};

/**
 * Is this specific ProteinUID_RegionUID Orphan
 * 
 */
LinkExclusionDataManager.prototype._IsProteinUID_RegionUID_Orphan = function( params ) {
	var proteinUID = params.proteinUID;
	var regionUID = params.regionUID;
	
	var proteinObj = null;
	try {
		proteinObj = this.imageProteinBarDataManager.getItemByUID( proteinUID );
	} catch( e ) {
		return true; // since getItemByUID throws exception when proteinUID not found 
	}
	if ( ! proteinObj ) {
		return true;
	}
	if ( regionUID === undefined ) {
		return false;
	}
	if ( proteinObj.getRegionFromRegionUID( { regionUID : regionUID } ) === null ) {
		return true;
	}
	return false;
}



/**
 * Add Link Exclusion
 * 
 * @param {object} params 
 * { proteinUID_1:The UID of the protein bar 1, regionUID_1:The UID of region 1
 *   proteinUID_2:The UID of the protein bar 2, regionUID_2:The UID of region 2
 * }
 * 
 */
LinkExclusionDataManager.prototype.addExclusionEntry = function( params ) {
	var proteinUID_1 = params.proteinUID_1;
	var regionUID_1 = params.regionUID_1;
	var proteinUID_2 = params.proteinUID_2;
	var regionUID_2 = params.regionUID_2;
	
	var newEntry = LinkExclusionDataEntry.constructEmptyLinkExclusionDataEntry();
	
	newEntry.setProteinUID_1( proteinUID_1 );
	newEntry.setRegionUID_1( regionUID_1 );
	newEntry.setProteinUID_2( proteinUID_2 );
	newEntry.setRegionUID_2( regionUID_2 );
	
	//  if already in exclusion array, don't add it again
	if ( this.exclusionDataLinksTwoEndPoints ) {
		for ( var index = 0; index < this.exclusionDataLinksTwoEndPoints.length; index++ ) {
			var entry = this.exclusionDataLinksTwoEndPoints[ index ];
			if ( entry.equals( newEntry ) ) {
				return;
			}
		}
	}
	
	this.exclusionDataLinksTwoEndPoints.push( newEntry );
	
	this.sortExclusions();
	
	var z = 0;
};


/**
 * Remove Link Exclusion
 * 
 * @param {object} params 
 * { proteinUID_1:The UID of the protein bar 1, regionUID_1:The UID of region 1
 *   proteinUID_2:The UID of the protein bar 2, regionUID_2:The UID of region 2
 * }
 * 
 */
LinkExclusionDataManager.prototype.removeExclusionEntry = function( params ) {
	var proteinUID_1 = params.proteinUID_1;
	var regionUID_1 = params.regionUID_1;
	var proteinUID_2 = params.proteinUID_2;
	var regionUID_2 = params.regionUID_2;
	
	var removeEntry = LinkExclusionDataEntry.constructEmptyLinkExclusionDataEntry();
	
	removeEntry.setProteinUID_1( proteinUID_1 );
	removeEntry.setRegionUID_1( regionUID_1 );
	removeEntry.setProteinUID_2( proteinUID_2 );
	removeEntry.setRegionUID_2( regionUID_2 );

	if ( this.exclusionDataLinksTwoEndPoints ) {
		for ( var index = 0; index < this.exclusionDataLinksTwoEndPoints.length; index++ ) {
			var entry = this.exclusionDataLinksTwoEndPoints[ index ];
			if ( entry.equals( removeEntry ) ) {
				//  Found entry to remove so remove from the array
				this.exclusionDataLinksTwoEndPoints.splice( index, 1 ); // Remove the entry at index
				return; // EARLY EXIT
			}
		}
		//  Get here if not found the entry in the array in this object
		//  Failed to find the object to remove it
	}
};

/**
 * Is Link Excluded
 * 
 * Link is excluded if it connects a pair of ( selected protein and/or selected protein region ) that are marked as excluded.
 * 
 * @param {object} params 
 * { proteinUID_1:The UID of the protein bar 1, proteinPosition_1:position in the protein bar 1,
 *   proteinUID_2:The UID of the protein bar 2, proteinPosition_2:position in the protein bar 2
 * }
 * @returns {boolean} true if link is excluded
 */
LinkExclusionDataManager.prototype.isLinkExcludedForProteinUIDProteinPosition = function( params ) {
	var proteinUID_1 = params.proteinUID_1;
	var proteinPosition_1 = params.proteinPosition_1;
	var proteinUID_2 = params.proteinUID_2;
	var proteinPosition_2 = params.proteinPosition_2;
	
	//  Search for parameters in exclusions
	if ( ( ! this.exclusionDataLinksTwoEndPoints ) || this.exclusionDataLinksTwoEndPoints.length === 0 ) {
		//  No Exclusions
		return false;  //  EARLY RETURN    Would never return true
	}

	var proteinBar_1 = this.imageProteinBarDataManager.getItemByUID( proteinUID_1 ); // getItemByUID throws for UID not found
	var proteinBar_2 = this.imageProteinBarDataManager.getItemByUID( proteinUID_2 ); // getItemByUID throws for UID not found
	
	var regionObject_1 = proteinBar_1.getProteinBarHighlightedRegionObjectAtSinglePosition( proteinPosition_1 ); // Set to undefined if region not found
	var regionObject_2 = proteinBar_2.getProteinBarHighlightedRegionObjectAtSinglePosition( proteinPosition_2 ); // Set to undefined if region not found

	var regionUID_1 = undefined;
	var regionUID_2 = undefined;
	if ( regionObject_1 ) {
		regionUID_1 = regionObject_1.getRegionUniqueId();
	}
	if ( regionObject_2 ) {
		regionUID_2 = regionObject_2.getRegionUniqueId();
	}

	//  Check for exclusion match for each permutation of:
	//      region 1 and 2 
	//		region 1 (2 is undefined)
	//		region 2 (1 is undefined)
	//		no regions (1 and 2 are undefined)
	
	//  region 1 and 2 
	var proteinRegionCheckParams = {
			proteinUID_1 : proteinUID_1,
			proteinUID_2 : proteinUID_2,
			regionUID_1 : regionUID_1,
			regionUID_2 : regionUID_2
	};
	if ( this._isLinkExcludedForProteinUIDRegionUID( proteinRegionCheckParams ) ) {
		return true;
	}

	//		region 1 (2 is undefined)
	proteinRegionCheckParams.regionUID_2 = undefined;
	if ( this._isLinkExcludedForProteinUIDRegionUID( proteinRegionCheckParams ) ) {
		return true;
	}

	//		region 2 (1 is undefined)
	proteinRegionCheckParams.regionUID_1 = undefined;
	proteinRegionCheckParams.regionUID_2 = regionUID_2;
	if ( this._isLinkExcludedForProteinUIDRegionUID( proteinRegionCheckParams ) ) {
		return true;
	}

	//		no regions (1 and 2 are undefined)
	proteinRegionCheckParams.regionUID_1 = undefined;
	proteinRegionCheckParams.regionUID_2 = undefined;
	if ( this._isLinkExcludedForProteinUIDRegionUID( proteinRegionCheckParams ) ) {
		return true;
	}

	return false;
};

/**
 * Check for exclusion using proteinUID_1, proteinUID_2, regionUID_1, regionUID_2
 */
LinkExclusionDataManager.prototype._isLinkExcludedForProteinUIDRegionUID = function( params ) {
	for ( var index = 0; index < this.exclusionDataLinksTwoEndPoints.length; index++ ) {
		var exclusionEntry = this.exclusionDataLinksTwoEndPoints[ index ];

		if ( ( exclusionEntry.getProteinUID_1() === params.proteinUID_1 && exclusionEntry.getRegionUID_1() === params.regionUID_1 
				&&  exclusionEntry.getProteinUID_2() === params.proteinUID_2 && exclusionEntry.getRegionUID_2() === params.regionUID_2 )
				|| ( exclusionEntry.getProteinUID_1() === params.proteinUID_2 && exclusionEntry.getRegionUID_1() === params.regionUID_2 
						&&  exclusionEntry.getProteinUID_2() === params.proteinUID_1 && exclusionEntry.getRegionUID_2() === params.regionUID_1 ) ) {
			
			return true;
		}
	}
	return false;
};


////  URL Hash Specific Processing

/**
 * Get data for Hash
 */
LinkExclusionDataManager.prototype.getDataForHash = function( ) {

	var linkExclusionDataManagerResult = {};
	
	if ( this.exclusionDataLinksTwoEndPoints ) {
		var linkExclusionForHashEntries = [];
		for ( var index = 0; index < this.exclusionDataLinksTwoEndPoints.length; index++ ) {
			var exclusionEntry = this.exclusionDataLinksTwoEndPoints[ index ];
			var linkExclusionForHashEntry = exclusionEntry.getHashDataObject();
			linkExclusionForHashEntries.push( linkExclusionForHashEntry );
		}
		linkExclusionDataManagerResult.a = linkExclusionForHashEntries;
	}

	return linkExclusionDataManagerResult;
};

/**
 * Update Internal LinkExclusionDataManager data with data in the Page Hash
 */
LinkExclusionDataManager.prototype.replaceInternalDataWithDataInHash = function( linkExclusionDataManagerData ) {

//	this.clearItems();
	
	this.exclusionDataLinksTwoEndPoints = [];
	
	if ( linkExclusionDataManagerData ) {
		var linkExclusionHashEntries = linkExclusionDataManagerData.a;
		if ( linkExclusionHashEntries ) {
			for ( var index = 0; index < linkExclusionHashEntries.length; index++ ) {
				var exclusionHashEntry = linkExclusionHashEntries[ index ];
				var exclusionEntry = LinkExclusionDataEntry.constructLinkExclusionDataEntryFromHashDataObject( exclusionHashEntry ); 
				this.exclusionDataLinksTwoEndPoints.push( exclusionEntry );
			}
		}
	}
};

/////////////////////////////////////////////////

/////   Class:  Link Exclusion Data Entry - Single entry

var LinkExclusionDataEntry = function() {

	/*
	 * Holds a single Link Exclusion Entry.
	 * 
	 * 2 protein unique ids and optional region unique ids
	 * 
	 * proteinUID_1 : 		Protein 1 Unique Id
	 * regionUID_1 : 		Region 1 Unique Id (optional) - associated with Protein 1
	 * 
	 * proteinUID_2 : 		Protein 2 Unique Id
	 * regionUID_2 : 		Region 2 Unique Id (optional) - associated with Protein 2
	 * 						
	 */
	this.proteinUID_1 = undefined;
	this.regionUID_1 = undefined;
	
	this.proteinUID_2 = undefined;
	this.regionUID_2 = undefined;
};


/**
 * Add Link Exclusion
 * 
 * @param {object} otherObject Class LinkExclusionDataEntry
 * @return true if equals, otherwise false
 */
LinkExclusionDataEntry.prototype.equals = function( otherObject ) {
	if ( this.proteinUID_1 === otherObject.proteinUID_1
			&& this.regionUID_1 === otherObject.regionUID_1
			&& this.proteinUID_2 === otherObject.proteinUID_2
			&& this.regionUID_2 === otherObject.regionUID_2 ) {
		return true;
	}
	return false
};

LinkExclusionDataEntry.prototype.getProteinUID_1 = function() {
	return this.proteinUID_1;
};
LinkExclusionDataEntry.prototype.setProteinUID_1 = function( proteinUID_1 ) {
	this.proteinUID_1 = proteinUID_1;
};
LinkExclusionDataEntry.prototype.getRegionUID_1 = function() {
	return this.regionUID_1;
};
LinkExclusionDataEntry.prototype.setRegionUID_1 = function( regionUID_1 ) {
	this.regionUID_1 = regionUID_1;
};

LinkExclusionDataEntry.prototype.getProteinUID_2 = function() {
	return this.proteinUID_2;
};
LinkExclusionDataEntry.prototype.setProteinUID_2 = function( proteinUID_2 ) {
	this.proteinUID_2 = proteinUID_2;
};
LinkExclusionDataEntry.prototype.getRegionUID_2 = function() {
	return this.regionUID_2;
};
LinkExclusionDataEntry.prototype.setRegionUID_2 = function( regionUID_2 ) {
	this.regionUID_2 = regionUID_2;
};

/**
 * Get the data object to put on the Hash
 */
LinkExclusionDataEntry.prototype.getHashDataObject = function() {
	var hashDataObject = {};
	hashDataObject.a = this.proteinUID_1;
	hashDataObject.b = this.regionUID_1;
	hashDataObject.c = this.proteinUID_2;
	hashDataObject.d = this.regionUID_2;
	return hashDataObject;
};

/**
 * Construct object of type ImageProteinBarData from hashDataObject.  The parameter hashDataObject comes from the Hash
 */
LinkExclusionDataEntry.constructLinkExclusionDataEntryFromHashDataObject = function( hashDataObject ) {
	var item = new LinkExclusionDataEntry();
	if ( hashDataObject ) {
		item.proteinUID_1 = hashDataObject.a ;
		item.regionUID_1 = hashDataObject.b;
		item.proteinUID_2 = hashDataObject.c;
		item.regionUID_2 = hashDataObject.d;
	}
	return item;
};


/**
 * Construct empty object of type LinkExclusionDataEntry
 */
LinkExclusionDataEntry.constructEmptyLinkExclusionDataEntry = function(  ) {
//	Create an instance from the constructor
	var linkExclusionDataEntry = new LinkExclusionDataEntry();
	return linkExclusionDataEntry;
};

export { LinkExclusionDataManagerContructor }
