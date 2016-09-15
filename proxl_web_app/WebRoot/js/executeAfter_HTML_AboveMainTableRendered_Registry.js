
//  executeAfter_HTML_AboveMainTableRendered_Registry.js

//    Create a registry of what to execute after the HTMl above the main table has been rendered.

//    Then when the HTML above the main table has been rendered, call everything in the registry.


//    IMPORTANT   This must be included in the HTML before anything that registers with it 

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";


//Constructor

var ExecuteAfter_HTML_AboveMainTableRendered_Registry = function () {

	this.executeRegistry = [];
};



ExecuteAfter_HTML_AboveMainTableRendered_Registry.prototype.addExecuteAfter_HTML_AboveMainTableRendered_ = function ( itemToCall_createPartsAboveMainTable_on ) {

	this.executeRegistry.push( itemToCall_createPartsAboveMainTable_on );
};


ExecuteAfter_HTML_AboveMainTableRendered_Registry.prototype.createPartsAboveMainTable = function ( params ) {

	$.each( this.executeRegistry, function( index, executeRegistryItem ) {

		executeRegistryItem.createPartsAboveMainTable( params );
	});
};

//  Object of the type

var executeAfter_HTML_AboveMainTableRendered_Registry = new ExecuteAfter_HTML_AboveMainTableRendered_Registry();

