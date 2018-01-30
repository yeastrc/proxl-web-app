/**
 * crosslink-image-viewer-legacy-handler.js
 * 
 * Encapsulate code used to update legacy JSON to current standards
 * 
 * This code uses a number of variables and functions from crosslink-image-viewer.js and possibly other JS files
 */

var LegacyJSONUpdater = function() {

};

/**
 * Convert the JSON hash to the current spec for the JSON hash
 * and support backwards compatibility with previously-saved URLs
 * 
 * @return true if an async web services call has been made and initPage() should not proceed until finished
 */
LegacyJSONUpdater.prototype.convertLegacyJSON = function() {

	var json = getRawJsonFromHash();
	var version = getJSONVersionNumber();

	var objectThis = this;
	
	// nothing to do, we're current
	if( version && version >= _JSON_VERSION_NUMBER ) {
		return false;
	}
	
	/*
	 * Convert JSON to version 1
	 */
		
	var proteinBarData = json[ HASH_OBJECT_PROPERTIES["protein_bar_data"] ];
	
	// check for the pre-URL-shortened version
	if( !proteinBarData ) {
		proteinBarData = json['protein_bar_data'];
	}
	
	if( !version || version <= 1 ) {
	
		json[ HASH_OBJECT_PROPERTIES["version-number"] ] /* json.vn */ = 1;
		updateURLHashWithJSONObject( json );
		
		if( json[ "selected-proteins" ] ) {
			/*
			 * Convert pre-protein bar data. We know it uses the old nrseq ids and
			 * protein seq ids must be looked up via ajax calls
			 */
			
			var cbFunction = function( params ) {
				objectThis.convertSelectedProteinsJSON( params );
			};
			
			var params = {
					nrseqProteinIds : json[ "selected-proteins" ],
					callback : cbFunction
			};
			
			getProteinSequenceVersionIdsForNrseqProteinIds( params );
			
			return true;
			
		} else if( proteinBarData ) {
			
			/*
			 * convert protein bar data manager data
			 */
			
			if( this.proteinBarDataContainsNrSeqIds( proteinBarData ) ) {
				
				// contains nrseq ids, must convert nrseq ids to protein seq ids
				
				var nrseqArray = this.getNrseqProteinArrayFromProteinBarData( proteinBarData );
				
				var cbFunction = function( params ) {
					objectThis.convertProteinBarDataCallback( params );
				};
				
				var params = {
						nrseqProteinIds : nrseqArray,
						callback : cbFunction
				};
				
				getProteinSequenceVersionIdsForNrseqProteinIds( params );
				
				return true;

			} else {
			
				// contains new protein seq ids, no need to convert nrseq ids

				this.convertProteinBarDataToIndexManager( json, proteinBarData, false );
			}
		}
	} //end conversion to version 1
	
	/*
	// convert pre-protein data bar manager and pre-protein sequence id usage
	if(	convertOldJSONIfNecessaryReturnTrueIfExit() ){ 
		return true;
	}
	*/
		
	return false;
};

/**
 * Callback function, called when needing to do nrseq id to protein sequence id conversion
 * lookups via ajax before converting old-style protein bar data to use the new index
 * manager system.
 * 
 * @param params The params sent from the ajax call
 */
LegacyJSONUpdater.prototype.convertProteinBarDataCallback = function( params ) {
	
	var json = getRawJsonFromHash();
	var proteinIdsMapping = params[ "proteinIdsMapping" ];
	
	var proteinBarData = json[ HASH_OBJECT_PROPERTIES["protein_bar_data"] ];
	
	// check for the pre-URL-shortened version
	if( !proteinBarData ) {
		proteinBarData = json['protein_bar_data'];
	}
	
	for( var i = 0; i < proteinBarData.length; i++ ) {
		
		var entry = proteinBarData[ i ];
		var nrseqId = entry[ "proteinId" ];
		var pid = proteinIdsMapping[ nrseqId ];
		
		entry[ "protSeqId" ] = pid;
		delete entry[ "proteinId" ];
	}
	
	this.convertProteinBarDataToIndexManager( json, proteinBarData, true );
}




/**
 * Given the proteinBarData which contains nrseq ids, return an array of those nrseq ids
 * 
 * @param proteinBarData the protein bar data object containing nrseq ids
 * @return An array of nrseq protein ids
 */
LegacyJSONUpdater.prototype.getNrseqProteinArrayFromProteinBarData = function( proteinBarData ) {
	
	var nrseqIds = [ ];
	
	for( var i = 0; i < proteinBarData.length; i++ ) {
		var entry = proteinBarData[ i ];
		
		if( entry.proteinId ) {
			nrseqIds.push( entry.proteinId );
		}
		
	}	
	
	return nrseqIds;
};

/**
 * Convert data in the pre version 1 protein bar data manager json to version 1. The ajaxResults
 * variable will only be defined if a lookup of protein sequence ids was necessary.
 * 
 * @param json The object being serialized to json that will contain the modified data
 * @param proteinBarData The object containing the old-style protein data bar data
 * @param ajaxResults The results of the ajax call to do the nrseq -> protein sequence id lookup (if it was necessary)
 */
LegacyJSONUpdater.prototype.convertProteinBarDataToIndexManager = function( json, proteinBarData, ajaxResults ) {
		
	var UID_PREFIX = "a"; //  Prefix for UID for converted from prev Protein Bar Manager
	
	var imdata = [ ];		// the Index Manager data
	var pbdata = { };       //  Protein Bar Manager Data
	
	
	for( var i = 0; i < proteinBarData.length; i++ ) {
		
		var item = proteinBarData[ i ];
		var pid = Number( item[ "protSeqId" ] );
		if( !pid ) {
			throw Error( "Got a non number for protSeqId. Got: " + item[ "protSeqId" ] );
		}		
		
		var uid = UID_PREFIX + i;  //  uid: Unique ID assigned in conversion before Index Manager existed
		
		imdata.push( {
			pid:pid,
			uid:uid
		});
		
		// add to new protein bar data manager
	
		delete item.protSeqId;
		pbdata[ uid ] = item;
	}

	
	var jsonKey_indexManagerData = hashObjectPropertyValueFromPropertyNameLookup( "index-manager-data" );
	json[ jsonKey_indexManagerData ] = {a:0, b:imdata};

	var jsonKey_protein_bar_data_current = hashObjectPropertyValueFromPropertyNameLookup( "protein_bar_data" );
	json[ jsonKey_protein_bar_data_current ] = {a:0, b:pbdata};

	updateURLHashWithJSONObject( json );
		
	// if ajaxResults is true, then we need to re-call initPage()
	if( ajaxResults ) {
		initPage();
	}
};

/**
 * Return true if the protein bar data json contains nrseq ids
 */
LegacyJSONUpdater.prototype.proteinBarDataContainsNrSeqIds = function( ob ) {
	
	if( ob.length < 1 ) { return false; }
	
	var item = ob[ 0 ];
	if( item[ "proteinId" ] ) {
		return true;
	}
	
	if( item[ "protSeqId" ] ) {
		return false;
	}
	
	console.log( item );
	throw Error( "Got aberrant entry for protein bar data object." );
	};



/**
 * Convert the JSON hash to version 1 from the legacy
 * JSON hash that contained the "selected-proteins" variable.
 * This function is called as a callback after the new protein
 * sequence ids are looked up. Consequently the variable "this"
 * is not this object.
 * 
 * @param params Contains an object "proteinIdsMapping" that contains
 * properties that are nrseq protein ids and values that are new protein
 * sequence ids.
 */
LegacyJSONUpdater.prototype.convertSelectedProteinsJSON = function( params ) {
	
	var json = getRawJsonFromHash();
	var proteinIdsMapping = params[ "proteinIdsMapping" ];
	
	if( !proteinIdsMapping || Object.keys( proteinIdsMapping ).length < 1 ) {
		throw Error( "Got no protein sequence id mapping." );
	}
	
	// use this instead of the keys of the proteinIdsMapping to preserve order
	var nrseqIds = json[ 'selected-proteins' ];

	for( var i = 0; i < nrseqIds.length; i++ ) {
		var nrseqId = nrseqIds[ i ];
		var pid = proteinIdsMapping[ nrseqId ];
		var uid = _indexManager.addProteinId( pid );
		// populate the protein bar data manager
		this.convertProteinHashDataPre_protein_bar_data( json, uid, nrseqId );
	}
	
	var jsonKey_indexManagerData = hashObjectPropertyValueFromPropertyNameLookup( "index-manager-data" );
	var jsonKey_protein_bar_data_current = hashObjectPropertyValueFromPropertyNameLookup( "protein_bar_data" );

	json[ jsonKey_indexManagerData ] = _indexManager.getDataForHash();
	json[ jsonKey_protein_bar_data_current ] = _imageProteinBarDataManager.getDataForHash();
	
	delete json[ 'selected-proteins' ];
	delete json[ 'protein-offsets' ];
	delete json[ 'protein-selections' ];
	delete json[ 'proteins-reversed' ];
	delete json[ 'highlighted-proteins' ];
	
	updateURLHashWithJSONObject( json );
	
	initPage();
};


/**
 * Convert old-style (pre version 1) protein bar data to use protein bar data manager (as of version 1)
 * 
 * @param json The object w/ the data from the json we're updating
 * @param uid The unique id from the index manager
 * @param The old nrseq id that corresponds to this uid
 */
LegacyJSONUpdater.prototype.convertProteinHashDataPre_protein_bar_data = function( json, uid, nrseqId ) {

	//  Backwards compatibility Support	
		
	var newEntry = ImageProteinBarData.constructEmptyImageProteinBarData();
	_imageProteinBarDataManager.addEntry( uid, newEntry );

	var highlightedProteins = json['highlighted-proteins'];  //   Array of the indexes of the highlighted proteins

	if ( highlightedProteins ) {
		for ( var highlightedProteinsIndex = 0; highlightedProteinsIndex < highlightedProteins.length; highlightedProteinsIndex++ ) {
			var highlightedProteinProteinBarPositionIndex = highlightedProteins[ highlightedProteinsIndex ];
			try {
				//  Try/Catch since the index may not be valid
				var entry = _imageProteinBarDataManager.getItemByIndex( highlightedProteinProteinBarPositionIndex );
				entry.setProteinBarHighlightedAll();
			} catch ( e ) {
			}
		}
	}			

	//	Object. Property keys are nrseq protein ids.  The reversed proteins have a value of true

	var proteinsReversedObject = json['proteins-reversed'];  

	if ( proteinsReversedObject !== undefined && proteinsReversedObject !== null ) {
		var proteinsReversedKeys = Object.keys( proteinsReversedObject );
		for ( var proteinsReversedKeysIndex = 0; proteinsReversedKeysIndex < proteinsReversedKeys.length; proteinsReversedKeysIndex++ ) {
			var reversedProtein_Key = proteinsReversedKeys[ proteinsReversedKeysIndex ];
			var reversedProtein_Value = proteinsReversedObject[ reversedProtein_Key ];
			if ( reversedProtein_Value ) {
				//  value is true so this protein id is reversed
				var reversedProtein_ProteinId = reversedProtein_Key;
				if( reversedProtein_ProteinId == nrseqId ) {
					var imageProteinBarDataEntry = _imageProteinBarDataManager.getItemByUID( uid );
					imageProteinBarDataEntry.setProteinReversed( { proteinReversed : true } );
				}
			}
		}
	}

//	Object. Property keys are nrseq protein ids.  Value are from the left edge

	var proteinsOffsetsObject = json['protein-offsets'];  

	if ( proteinsOffsetsObject !== undefined && proteinsOffsetsObject !== null ) {
		var proteinsOffsetsKeys = Object.keys( proteinsOffsetsObject );
		for ( var proteinsOffsetsKeysIndex = 0; proteinsOffsetsKeysIndex < proteinsOffsetsKeys.length; proteinsOffsetsKeysIndex++ ) {
			var offsetProtein_Key = proteinsOffsetsKeys[ proteinsOffsetsKeysIndex ];
			var offsetProtein_Value = proteinsOffsetsObject[ offsetProtein_Key ];

			if ( offsetProtein_Value !== undefined && offsetProtein_Value !== null ) {
				//  value is a number so use it
				var offsetProtein_ProteinId = offsetProtein_Key;
				if( offsetProtein_ProteinId == nrseqId ) {
					var imageProteinBarDataEntry = _imageProteinBarDataManager.getItemByUID( uid );
					imageProteinBarDataEntry.setProteinOffset( { proteinOffset : offsetProtein_Value } );
				}
			}
		}
	}
};