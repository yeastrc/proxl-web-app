
"use strict";

/**
 * The index manager keeps track of which proteins are visible in the diagram and
 * in what order they should be displayed (in contexts where order matters).
 * 
 * It also holds an association of a unique identifier string with each protein added to the
 * image, such that multiple instances of the same protein will have unique identifers.
 * Information associated with displayed proteins, such as bar offsets, colors, and so
 * on should be associated with this unique identifier.
 */
var indexManager = function() {	
	this.initialize();
};

/**
 * Perform any initialization
 */
indexManager.prototype.initialize  = function(  ) {

	/*
	 * Holds the protein array. This is an array of objects where
	 * each object is:
	 * 		{
	 * 			pid : 		proxl sequence id of protein at that index,
	 * 			uid  : 		unique id to serve for identifying this instance
	 * 						of this protein (since it can appear more than once)
	 * 						
	 * 						This unique id will not change if the protein's index
	 * 						changes. Things such as protein bar position information,
	 * 						custom colors, and the like should be associated with this
	 * 						unique id.
	 * 		}
	 */
	this.parr = [ ];

};

/**
 * Get all the protein index data. Comes as an array of objects, as defined
 * above.
 */
indexManager.prototype.getProteinArray = function() {
	return this.parr;
};

/**
 * Set all the protein index data.
 * 
 * Used for populating from URL Hash
 */
indexManager.prototype.setProteinArray = function( parrParam ) {
	this.parr = parrParam;
};

/**
 * Using the supplied proteins list as the list of valid proteins, remove
 * all entries from the index manager that contain proteins not in this list.
 * 
 * @param validProteinList The list of valid proteins
 * @return A list of the removed entries
 */
indexManager.prototype.removeInvalidProteins = function( validProteinList ) {
	
	var removedEntries = [ ];
	var UIDsToRemove = [ ];
	
	for( var i = 0; i < this.parr.length; i++ ) {
		var element = this.parr[ i ];
		
		if( !this.listContainsElement( validProteinList, element.pid ) ) {			
			UIDsToRemove.push( element.uid );
		}
	}
	
	for( var i = 0; i < UIDsToRemove.length; i++ ) {

		var removedElement = this.removeEntryByUID( UIDsToRemove[ i ] );
		
		console.log( "Removed invalid element from index manager: " );
		console.log( removedElement );
		console.log( "\n" );
		
		removedEntries.push( removedElement );
	}	
	
	return removedEntries;
};

/**
 * Test if the given list contains the given element
 * 
 * @param list The list that may or may not contain the element
 * @param element The element that may or may not be contained in the list
 * @return true if in the list, false if not
 */
indexManager.prototype.listContainsElement = function( list, element ) {	
	var l = list.length;
	
    for (var i = 0; i < l; i++) {
        if( list[ i ] === element ) {
            return true;
        }
    }
    return false;	
};

/**
 * Get a new unique id. This is guaranteed to be unique in the current
 * list of proteins in the index. It may, however, have the same id as
 * a previously-deleted entry.
 */
indexManager.prototype.getNewUniqueId = function() {
	
	var i = 1;
	var uid = "p" + i;
	
	while( this.findIndexPosition( uid ) !== -1 ) {
		i++;
		uid = "p" + i;
	}
	
	return uid;
};

/**
 * Get the protein ID for the supplied unique ID.
 * 
 * @param uniqueId The unique id to search
 * @return The protein ID found for the unique ID, null if not found
 */
indexManager.prototype.getProteinIdForUID = function( uniqueId) {
	
	for( var i = 0; i < this.parr.length; i++ ) {
		if( this.parr[ i ].uid === uniqueId ) {
			return this.parr[ i ].pid;
		}
	}
	
	return null;
};

/**
 * Get the index (starting at 0) for the given UID Returns -1 if not found
 * 
 * @param uniqueId the UID to check
 * @return the index (starting at 0) for the given UID Returns -1 if not found
 */
indexManager.prototype.getIndexForUID = function( uniqueId ) {
	
	for( var i = 0; i < this.parr.length; i++ ) {
		if( this.parr[ i ].uid === uniqueId ) {
			return i;
		}
	}
	
	return -1;	
};

/**
 * Get the UID of the given index
 * 
 * @param index the index to check
 * @return the uid of the index
 */
indexManager.prototype.getUIDForIndex = function( index ) {
	
	return this.parr[ index ].uid;	
};

/**
 * Check whether the given UID is defined in this index manager
 * 
 * @param uniqueId the UID to check
 * @return true if that UID is in this index manager, false if not
 */
indexManager.prototype.containsUID = function( uniqueId ) {
	
	for( var i = 0; i < this.parr.length; i++ ) {
		if( this.parr[ i ].uid === uniqueId ) {
			return true;
		}
	}
	
	return false;	
};

/**
 * Add the given proteinId to the index manager.
 *
 * @param proteinId The protein id.
 * @return The unique id created for this protein
 *
 */
indexManager.prototype.addProteinId = function( proteinId ) {
	
	var pid = Number( proteinId );
	if( !pid ) {
		throw Error( "Got a non number for a proteinId. Got: " + proteinId );
	}
	
	
	var data = {
		pid : pid,
		uid  : this.getNewUniqueId()
	};
	
	this.parr.push( data );
	
	return data.uid;	
};

/**
 * Finds the index position (starting at 0) for the given unique id
 *
 * @param uniqueId The unique id to remove
 * @return a value >= 0 if found, -1 if not
 */
indexManager.prototype.findIndexPosition = function( uniqueId ) {
	
	var index = -1;
	
	for( var i = 0; i < this.parr.length; i++ ) {
		if( this.parr[ i ].uid === uniqueId ) {
			index = i;
			break;
		}
	}
	
	return index;
};

/**
 * Removes the entry with the given unique id from the index manager
 *
 * @param uniqueId The unique id to remove
 * @return the entry that was removed, null otherwise
 *
 */
indexManager.prototype.removeEntryByUID = function( uniqueId ) {
		
	var indexToRemove = this.findIndexPosition( uniqueId );

	if( indexToRemove !== -1 ) {
		var entry = this.parr[ indexToRemove ];
		
		this.parr.splice( indexToRemove, 1 );

		return entry;
	}
	
	return null;
};

/**
 * Removes the entry at the given position
 *
 * @param index The position at which we want to remove an entry
 * @return the entry that was removed, null otherwise
 *
 */
indexManager.prototype.removeEntryByIndex = function( index ) {
		
	if( index >= 0 && index < this.parr.length ) {
		var entry = this.parr[ index ];
		this.parr.splice( index, 1 );
		
		return entry;
	}
	
	return null;
};


/**
 * Moves the entry with the given unique id to the requested index position
 *
 * @param uniqueId The unique id to move
 * @param indexPosition The index position (starting at 0) to which to move the entry
 * 
 * @return true if something was moved, false otherwise
 *
 */
indexManager.prototype.moveEntryToIndexPosition = function( uniqueId, indexPosition ) {
	
	var entry = this.removeEntryByUID( uniqueId );
	
	if( entry !== null ) {
		this.parr.splice( indexPosition, 0, entry );
		return true;
	}
	
	return false;
};

/**
 * Get a list of the currently-displayed proteins, in the order of the index. Can
 * contain the same protein multiple times.
 */
indexManager.prototype.getProteinList = function() {
	var prots = [ ];
	
	for( var i = 0; i < this.parr.length; i++ ) {
		prots.push( this.parr[ i ].pid );
	}
	
	return prots;
};

