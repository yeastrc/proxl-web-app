

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";



///////////////   CONSTANTS  ////////////////////////


//  Default exclude link type "No Links"

var EXCLUDE_LINK_TYPE_DEFAULT = [ 0 ];



var PROTEIN_SELECTOR_HANDLEBARS_STRING = "\t<option value=\"{{ proteinId }}\">{{ proteinName }}</option>\n";

var PROTEIN_BAR_DATA_ARRAY_INDEX_ATTR_NAME = "data-protein_bars_data_array_index";




//  Matches the "value" for the <select id="annotation_type">

var SELECT_ELEMENT_ANNOTATION_TYPE_SEQUENCE_COVERAGE = "sequence_coverage";
//var SELECT_ELEMENT_ANNOTATION_TYPE_DISOPRED2 = "disopred2";
var SELECT_ELEMENT_ANNOTATION_TYPE_DISOPRED3 = "disopred3";
var SELECT_ELEMENT_ANNOTATION_TYPE_PSIPRED3 = "psired3";



//Matches the "value" for the <select id="color_by">

var SELECT_ELEMENT_COLOR_BY_REGION = "region";
var SELECT_ELEMENT_COLOR_BY_SEARCH = "search";

//////////////////////////

//  Visible Constants

var _PROTEIN_TERMINUS_LABEL_C = "C";
var _PROTEIN_TERMINUS_LABEL_N = "N";

//  height and width for when initially attach to <svg> element 

var _DEFAULT_VIEWPORT_HEIGHT = 1;
var _DEFAULT_VIEWPORT_WIDTH = 1;


////  JSON Hash property names and values

var PROTEIN_NAMES_POSITION_HASH_JSON_PROPERTY_NAME = "protein_names_position";

var PROTEIN_NAMES_POSITION_LEFT = "left";



///   CSS Class name constants for jQuery search

var _PROTEIN_BAR_OVERLAY_RECTANGLE_LABEL_CLASS = "protein_bar_overlay_rectangle_jq";

var _PROTEIN_BAR_MAIN_RECTANGLE_LABEL_CLASS = "protein_bar_main_rectangle_jq";

var _PROTEIN_BAR_PROTEIN_NAME_LABEL_CLASS = "protein_bar_protein_name_jq";

var _PROTEIN_BAR_GROUP_ON_TOP_OF_MAIN_RECTANGLE_LABEL_CLASS = "protein_bar_group_on_top_of_main_rectangle_jq";



///   CSS Class name constants for processing Protein Selection Overlay

var _PROTEIN_OVERLAY_PROTEIN_SELECTED = "select-protein-overlay-user-selected-protein";


////////////////////////////////////////////////////////////////////////

//  Mapping to/from JSON put on Hash in URL


//  Hash properties.  

//  The property names are referenced in this code
//  The property values are used as the property names in the JSON stored on the Hash

//  !!  Important !!  	Two properties cannot have the same value.
//	A value cannot be re-used for a new property 

var HASH_OBJECT_PROPERTIES = {

		excludeTaxonomy : "a",
		excludeType : "b",
		cutoffs : "c",

		filterNonUniquePeptides : "d",
		filterOnlyOnePSM : "e",
		filterOnlyOnePeptide : "f",

		"show-self-crosslinks" : "g",
		"show-crosslinks" : "h",
		"show-looplinks" : "i",
		"show-monolinks" : "k",
		"show-protein-termini" : "l",
		"show-linkable-positions" : "m",
		"show-tryptic-cleavage-positions" : "n",
		"show-scalebar" : "o",

		"shade-by-counts" : "p",
		
		"color-by-search" : "q",  //  Not used, only for backwards compatibility
		
		"automatic-sizing" : "r",
		"protein_names_position" /* PROTEIN_NAMES_POSITION_HASH_JSON_PROPERTY_NAME */ : "s",
		"annotation_type" : "t",
		"vertical-bar-spacing" : "u",
		"horizontal-bar-scaling" : "v",
		"protein_bar_data" : "w",

		"selected-proteins" : "x",
		
		"color_by" : "y"
};



var hashObjectManager = {

		hashObject : {},

		resetHashObject : function() {

			this.hashObject = {};

		},

		setOnHashObject : function( property, value ) {

			if ( property === undefined || property === null ) {
				
				throw "property not found in 'HASH_OBJECT_PROPERTIES' ";
			}

			this.hashObject[ property ] = value;
		},
		
		getFromHashObject : function( property ) {

			if ( property === undefined || property === null ) {
				
				throw "property not found in 'HASH_OBJECT_PROPERTIES' ";
			}
			
			return this.hashObject[ property ];

		},
		
		getHashObject : function() {
			
			return this.hashObject;
		},

		setHashObject : function( hashObject ) {
			
			this.hashObject = hashObject;
		},
		
		

};




/////////////////////////////////////////////////////////////////////////

///////////////    GLOBAL VARIABLES  ////////////////////////////////////



//   Objects of classes that have their own code

var _imageProteinBarDataManager = ImageProteinBarDataManagerContructor();

var _proteinBarRegionSelectionsOverlayCode = ProteinBarRegionSelectionsOverlayCodeContructor( { imageProteinBarDataManager : _imageProteinBarDataManager });

var _circlePlotViewer = new circlePlotViewer();



//   General

var _testThatCanGetSVGasString = true;





//  From Page


var _searchIds = {};



//  Loaded data:

var _proteins;
var _proteinSequences = {};
var _proteinSequenceTrypsinCutPoints = {};

var _proteinTaxonomyIds = {};
var _proteinLengths;
var _proteinNames;
var _proteinLinkPositions;
var _proteinLooplinkPositions;
var _proteinMonolinkPositions;

var _linkPSMCounts = { };

var _searches;

var _taxonomies;
var _linkablePositions;
var _coverages;
var _ranges;


//  From JSON (probably round trips from the input fields to the JSON in the Hash in the URL to these variables)

var _psmPeptideCutoffsRootObjectStorage = {
		
		_psmPeptideCutoffsRootObject : null,
		
		setPsmPeptideCutoffsRootObject : function( psmPeptideCutoffsRootObject ) {
			
			this._psmPeptideCutoffsRootObject = psmPeptideCutoffsRootObject;
			
			viewLooplinkReportedPeptidesLoadedFromWebServiceTemplate.setPsmPeptideCriteria( psmPeptideCutoffsRootObject );
			viewCrosslinkReportedPeptidesLoadedFromWebServiceTemplate.setPsmPeptideCriteria( psmPeptideCutoffsRootObject );
			viewMonolinkReportedPeptidesLoadedFromWebServiceTemplate.setPsmPeptideCriteria( psmPeptideCutoffsRootObject );
			
			viewPsmsLoadedFromWebServiceTemplate.setPsmPeptideCriteria( psmPeptideCutoffsRootObject );
		},

		getPsmPeptideCutoffsRootObject : function( ) {

			return this._psmPeptideCutoffsRootObject;
		}
};


var _excludeTaxonomy;
var _excludeType;
var _filterNonUniquePeptides;
var _filterOnlyOnePSM;
var _filterOnlyOnePeptide;


var _colorLinesBy = undefined;


//  working data (does round trip to the JSON in the Hash in the URL)

var _userScaleFactor;

var _horizontalScalingPercent = 100;

//  Working data, does not go to JSON  to Hash

var _proteinBarToolTip_template_HandlebarsTemplate = null;  ///  Compiled Handlebars template for the protein bar tool tip


//////////////////////////////////////////////////////////////////////////////

////////////////     CODE   ///////////////////////////////////


// get values for variables from the hash part of the URL as JSON
function getJsonFromHash() {
	
	var jsonFromHash = null;

	var windowHash = window.location.hash;
	
	if ( windowHash === "" || windowHash === "#" ) {
		
		//  No Hash value so set defaults and return
		
		jsonFromHash = { 
				cutoffs : getCutoffDefaultsFromPage(),
				
				'excludeType' : EXCLUDE_LINK_TYPE_DEFAULT
		};
		
		return jsonFromHash;
	}
	
	var windowHashContentsMinusHashChar = windowHash.slice( 1 );


	// Try first:  the hash contained Compressed URI-encoded JSON, try decoding using decodeURIComponent( windowHashContentsMinusHashChar )

	try {
		//  LZString.decompressFromEncodedURIComponent(...) returns null if unable to decompress
		var windowHashContentsMinusHashCharDecompressedDecodeURIComponent = LZString.decompressFromEncodedURIComponent( windowHashContentsMinusHashChar );

		if ( windowHashContentsMinusHashCharDecompressedDecodeURIComponent !== null 
				&& windowHashContentsMinusHashCharDecompressedDecodeURIComponent !== undefined ) {

			jsonFromHash = JSON.parse( windowHashContentsMinusHashCharDecompressedDecodeURIComponent );
		} else {
			
			jsonFromHash = undefined;
		}
	} catch( e ) {
		
		jsonFromHash = undefined;
	}
	
	if ( jsonFromHash === null || jsonFromHash === undefined ) {

		try {

			// if this works, the hash contains native (non encoded) JSON
			jsonFromHash = JSON.parse( windowHashContentsMinusHashChar );
		} catch( e ) {

			jsonFromHash = undefined;
		}
	}
	

	if ( jsonFromHash === null || jsonFromHash === undefined ) {

		try {
			// if we got here, the hash contained URI-encoded JSON, try decoding using decodeURI( windowHashContentsMinusHashChar )

			var windowHashContentsMinusHashCharDecodeURI = decodeURI( windowHashContentsMinusHashChar );

			jsonFromHash = JSON.parse( windowHashContentsMinusHashCharDecodeURI );
		} catch( e ) {

			jsonFromHash = undefined;
		}
	}

	if ( jsonFromHash === null || jsonFromHash === undefined ) {

		try {
			// if we got here, the hash contained URI-encoded JSON, try decoding using decodeURIComponent( windowHashContentsMinusHashChar )

			var windowHashContentsMinusHashCharDecodeURIComponent = decodeURIComponent( windowHashContentsMinusHashChar );

			jsonFromHash = JSON.parse( windowHashContentsMinusHashCharDecodeURIComponent );
		} catch( e ) {

			jsonFromHash = undefined;
		}
	}

	if ( jsonFromHash === null || jsonFromHash === undefined ) {

		throw "Failed to parse window hash string as JSON and decodeURI and then parse as JSON.  windowHashContentsMinusHashChar: " 
		+ windowHashContentsMinusHashChar;
	}
	
	//   Transform json on hash to expected object for rest of the code
	
	var json = jsonFromHash;
	
	
	var hashPropertiesConstantsKeys = Object.keys( HASH_OBJECT_PROPERTIES );

	for ( var hashPropertiesConstantsKeysIndex = 0; hashPropertiesConstantsKeysIndex < hashPropertiesConstantsKeys.length; hashPropertiesConstantsKeysIndex++ ) {

		var jsonPropertyKey = hashPropertiesConstantsKeys[ hashPropertiesConstantsKeysIndex ];

		var hashPropertyKey = HASH_OBJECT_PROPERTIES[ jsonPropertyKey ];
		
		var valueForHashPropertyKey = jsonFromHash[ hashPropertyKey ];
		
		if ( valueForHashPropertyKey !== undefined ) {

			json[ jsonPropertyKey ] = valueForHashPropertyKey;
		}
	}
	
	//  Since show-lysines was renamed to show-linkable-positions
		
	if ( json[ 'show-lysines' ] ) {
	
		json[ 'show-linkable-positions' ] = true;
	}
	
	if ( json.cutoffs === undefined || json.cutoffs === null ) {

		//  Set cutoff defaults if not in JSON
		
		json.cutoffs = getCutoffDefaultsFromPage();
	}
	
	//  Set default for exclude link type
	
	if ( json[ 'excludeType' ] === undefined ) {
		
		json[ 'excludeType' ] = EXCLUDE_LINK_TYPE_DEFAULT;
	}

	
	return json;
}


////

function getCutoffDefaultsFromPage() {
	

	
	var $cutoffValuesRootLevelCutoffDefaults = $("#cutoffValuesRootLevelCutoffDefaults");
	
	var cutoffValuesRootLevelCutoffDefaultsString = $cutoffValuesRootLevelCutoffDefaults.val();
	
	try {
		var cutoffValuesRootLevelCutoffDefaults = JSON.parse( cutoffValuesRootLevelCutoffDefaultsString );
	} catch( e2 ) {
		
		throw "Failed to parse cutoffValuesRootLevelCutoffDefaults string as JSON.  " +
				"Error Message: " + e2.message +
				".  cutoffValuesRootLevelCutoffDefaultsString: |" +
				cutoffValuesRootLevelCutoffDefaultsString +
				"|";
	}

	return cutoffValuesRootLevelCutoffDefaults;
}




function getValuesFromForm() {

	
	///// Called before this function is called
	
//	hashObjectManager.resetHashObject();


	
	var getCutoffsFromThePageResult = cutoffProcessingCommonCode.getCutoffsFromThePage(  {  } );
	
	var getCutoffsFromThePageResult_FieldDataFailedValidation = getCutoffsFromThePageResult.getCutoffsFromThePageResult_FieldDataFailedValidation;
	
	if ( getCutoffsFromThePageResult_FieldDataFailedValidation ) {
		
		//  Cutoffs failed validation and error message was displayed
		
		//  EARLY EXIT from function
		
//		return { output_FieldDataFailedValidation : getCutoffsFromThePageResult_FieldDataFailedValidation };
		
		throw "Cutoffs are invalid so stop processing";
	}
	
	var outputCutoffs = getCutoffsFromThePageResult.cutoffsBySearchId;
	

	
	hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES.cutoffs, outputCutoffs );


	if ( $( "input#filterNonUniquePeptides" ).is( ':checked' ) ) {
		hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES.filterNonUniquePeptides, true );
	} else {
		hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES.filterNonUniquePeptides, false );
	}

	if ( $( "input#filterOnlyOnePSM" ).is( ':checked' ) ) {
		hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES.filterOnlyOnePSM, true );
	} else {
		hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES.filterOnlyOnePSM, false );
	}

	if ( $( "input#filterOnlyOnePeptide" ).is( ':checked' ) ) {
		hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES.filterOnlyOnePeptide, true );
	} else {
		hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES.filterOnlyOnePeptide, false );
	}

	var xTax = new Array();
	var taxKeys = Object.keys( _taxonomies );

	for ( var taxKeysIndex = 0; taxKeysIndex < taxKeys.length; taxKeysIndex++ ) {

		var id = taxKeys[ taxKeysIndex ];

		if ( $( "input#exclude-taxonomy-" + id ).is( ':checked' ) ) {
			xTax.push( parseInt( id ) );
		}
	}

	hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES.excludeTaxonomy, xTax );


	var xType = new Array();
	for ( var excludeTypeIndex = 0; excludeTypeIndex < 5; excludeTypeIndex++ ) {			
		if ( $( "input#exclude-type-" + excludeTypeIndex ).is( ':checked' ) ) {
			xType.push( excludeTypeIndex );
		}
	}
	
	hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES.excludeType, xType );
}






//build a query string based on selections by user
function updateURLHash( useSearchForm ) {

	hashObjectManager.resetHashObject();

	if ( ! useSearchForm ) {

//		build hash string from previous search, they've just updated the drawing

//		add taxonomy exclusions
		hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES.excludeTaxonomy, _excludeTaxonomy );

//		add type exclusions
		hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES.excludeType, _excludeType );

//		add psm/peptide cutoffs
		hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES.cutoffs, _psmPeptideCutoffsRootObjectStorage.getPsmPeptideCutoffsRootObject() );

//		add filter out non unique peptides
		hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES.filterNonUniquePeptides, _filterNonUniquePeptides );

//		add filter out non unique peptides
		hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES.filterOnlyOnePSM, _filterOnlyOnePSM );

//		add filter out non unique peptides
		hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES.filterOnlyOnePeptide, _filterOnlyOnePeptide );

	} else {

//		build hash string from values in form, they've requested a data refresh

		getValuesFromForm();
	}


//	load in settings from viewer section
	if ( $( "input#show-self-crosslinks" ).is( ':checked' ) ) {
		hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES["show-self-crosslinks"], true );
	} else {
		hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES["show-self-crosslinks"], false );
	}

	if ( $( "input#show-crosslinks" ).is( ':checked' ) ) {
		hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES["show-crosslinks"], true );
	} else {
		hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES["show-crosslinks"], false );
	}

	if ( $( "input#show-looplinks" ).is( ':checked' ) ) {
		hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES["show-looplinks"], true );
	} else {
		hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES["show-looplinks"], false );
	}

	if ( $( "input#show-monolinks" ).is( ':checked' ) ) {
		hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES["show-monolinks"], true );
	} else {
		hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES["show-monolinks"], false );
	}
	

	if ( $( "input#show-protein-termini" ).is( ':checked' ) ) {
		hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES["show-protein-termini"], true );
	} else {
		hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES["show-protein-termini"], false );
	}
	

	if ( $( "input#show-linkable-positions" ).is( ':checked' ) ) {
		hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES["show-linkable-positions"], true );
	} else {
		hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES["show-linkable-positions"], false );
	}
	
	if ( $( "input#show-tryptic-cleavage-positions" ).is( ':checked' ) ) {
		hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES["show-tryptic-cleavage-positions"], true );
	} else {
		hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES["show-tryptic-cleavage-positions"], false );
	}	

	if ( $( "input#show-scalebar" ).is( ':checked' ) ) {
		hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES["show-scalebar"], true );
	} else {
		hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES["show-scalebar"], false );
	}

	if ( $( "input#shade-by-counts" ).is( ':checked' ) ) {
		hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES["shade-by-counts"], true );
	}

	_colorLinesBy = $("#color_by").val();
	
	if ( _colorLinesBy !== "" ) {

		hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES["color_by"], _colorLinesBy );
	}


	if ( !isSizingAutomatic() ) {
		hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES["automatic-sizing"], false );
	}

	if ( $( "input#protein_names_position_left" ).is( ':checked' ) ) {
		hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES["protein_names_position"], PROTEIN_NAMES_POSITION_LEFT );
	}
	
	
	hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES["annotation_type"], $("#annotation_type").val() );
	
	//  Add in protein bar data
	hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES["protein_bar_data"], _imageProteinBarDataManager.getArrayOfObjectsForHash() );
	
	
	//  Now covered in the 'protein_bar_data'
//	add in the selected proteins
//	hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES["selected-proteins"], getAllSelectedProteins() );

	
	
	var hashObject = hashObjectManager.getHashObject();
	
	var newHash = JSON.stringify( hashObject );

//	var newHashLength = newHash.length;
	
	var newHashEncodedToEncodedURIComponent = LZString.compressToEncodedURIComponent( newHash );
	
//	var newHashEncodedToEncodedURIComponentLength = newHashEncodedToEncodedURIComponent.length;
	
	try {

		window.location.hash = newHashEncodedToEncodedURIComponent;
		
	} catch ( e ) {

		//  TODO  Need to handle this error.  
		
		//     The user really shouldn't continue since the settings are not being stored in the Hash
		
		console.log( "Update window.location.hash Failed: e: " + e );
	}

}






function buildQueryStringFromHash() {
	
	
	var queryString = "?";
	var items = new Array();
	
	
	var json = getJsonFromHash();
	
	//  searchIds from the page
	for ( var i = 0; i < _searchIds.length; i++ ) {
		items.push( "searchIds=" + _searchIds[ i ] );
	}
	

	
	if ( json.excludeTaxonomy != undefined && json.excludeTaxonomy.length > 0 ) {
		for ( var i = 0; i < json.excludeTaxonomy.length; i++ ) {
			items.push( "excludeTaxonomy=" + json.excludeTaxonomy[ i ] );
		}
	}
	
	if ( json.excludeType != undefined && json.excludeType.length > 0 ) {
		for ( var i = 0; i < json.excludeType.length; i++ ) {
			items.push( "excludeType=" + json.excludeType[ i ] );
		}
	}
	
	
	
	///   Serialize cutoffs to JSON
	
	var cutoffs = json.cutoffs;
	
	var psmPeptideCutoffsForSearchIds_JSONString = JSON.stringify( cutoffs );

	var psmPeptideCutoffsForSearchIds_JSONStringEncoded = encodeURIComponent( psmPeptideCutoffsForSearchIds_JSONString );

	items.push( "psmPeptideCutoffsForSearchIds=" + psmPeptideCutoffsForSearchIds_JSONStringEncoded );


	if ( json.filterNonUniquePeptides != undefined && json.filterNonUniquePeptides ) {
		items.push( "filterNonUniquePeptides=on" );
	}
	if ( json.filterOnlyOnePSM != undefined && json.filterOnlyOnePSM ) {
		items.push( "filterOnlyOnePSM=on" );
	}
	if ( json.filterOnlyOnePeptide != undefined && json.filterOnlyOnePeptide ) {
		items.push( "filterOnlyOnePeptide=on" );
	}
	
	queryString += items.join( "&" );
	
	return queryString;
}

/////////////////////

//   Called from button "Update From Database" on the page

///   Refresh the data on the page

function refreshData() {
	
	updateURLHash( true /* useSearchForm */ );
	
	defaultPageView.searchFormUpdateButtonPressed_ForDefaultPageView();
	


	_coverages = undefined;
	_ranges = undefined;
	
//	proteinAnnotationStore.reset(); // don't call since not affected by search criteria on page
	
	_proteinMonolinkPositions = undefined;
	_proteinLooplinkPositions = undefined;
	_proteinLinkPositions = undefined;
	_linkPSMCounts = { };

	loadDataFromService();
}

///////////////////

// Load protein sequence coverage data for a specific protein
function loadSequenceCoverageDataForProtein( protein, doDraw ) {
	
	console.log( "Loading sequence coverage data for protein: " + protein );
	
		if ( _ranges == undefined || _ranges[ protein ] == undefined ) {
			
			incrementSpinner();				// create spinner
			
			var url = contextPathJSVar + "/services/sequenceCoverage/getDataForProtein";
			url += buildQueryStringFromHash();
			url += "&proteinId=" + protein;
			
			 $.ajax({
			        type: "GET",
			        url: url,
			        dataType: "json",
			        success: function(data)	{
			        
			        	if ( _ranges == undefined ) {
			        		_coverages = data.coverages;
			        		_ranges = data.ranges;
			        	} else {
			        		_coverages[ protein ] = data[ 'coverages' ][ protein ];
			        		_ranges[ protein ] = data[ 'ranges' ][ protein ];
			        	}
			        	
			        	decrementSpinner();
			        	loadDataAndDraw( doDraw );
			        },
			        failure: function(errMsg) {
						decrementSpinner();
			        	handleAJAXFailure( errMsg );
			        },
			        error: function(jqXHR, textStatus, errorThrown) {	
							decrementSpinner();
							handleAJAXError( jqXHR, textStatus, errorThrown );
//							alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ", textStatus: " + textStatus );
					}
			  });	
		} else {
			loadDataAndDraw( doDraw );
		}

}


//Load protein sequence data for a list of proteins
function loadProteinSequenceDataForProtein( proteinIdsToGetSequence, doDraw ) {

	console.log( "Loading protein sequence data for proteins: " + proteinIdsToGetSequence );

	incrementSpinner();				// create spinner
	
	var url = contextPathJSVar + "/services/proteinSequence/getDataForProtein";

	var project_id = $("#project_id").val();
	
	if ( project_id === undefined || project_id === null 
			|| project_id === "" ) {
		
		throw '$("#project_id").val() returned no value';
	}
	

	var ajaxRequestData = {
			project_id : project_id,
			proteinIdsToGetSequence: proteinIdsToGetSequence
	};
	
	$.ajax({
	        type: "GET",
	        url: url,
			dataType: "json",
			data: ajaxRequestData,  //  The data sent as params on the URL
	        
			traditional: true,  //  Force traditional serialization of the data sent
			//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
			//   So proteinIdsToGetSequence array is passed as "proteinIdsToGetSequence=<value>" which is what Jersey expects

	        success: function(data)	{
	        
	        	var returnedProteinIdsAndSequences = data;  //  The property names are the protein ids and the property values are the sequences
	        	
	        	// copy the returned sequences into the global object
	        	
	    		var returnedProteinIdsAndSequences_Keys = Object.keys( returnedProteinIdsAndSequences );
	    		
	    		for ( var keysIndex = 0; keysIndex < returnedProteinIdsAndSequences_Keys.length; keysIndex++ ) {
	    			
	    			var proteinId = returnedProteinIdsAndSequences_Keys[ keysIndex ];
	    			
	    			_proteinSequences[ proteinId ] = returnedProteinIdsAndSequences[ proteinId ];
	    		}
	    		
	        	
	        	decrementSpinner();
	        	loadDataAndDraw( doDraw );
	        },
	        failure: function(errMsg) {
				decrementSpinner();
	        	handleAJAXFailure( errMsg );
	        },
	        error: function(jqXHR, textStatus, errorThrown) {	
					decrementSpinner();
					handleAJAXError( jqXHR, textStatus, errorThrown );
//							alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ", textStatus: " + textStatus );
			}
	  });	

}



//  Load mapping of protein id to  taxonomy id data for a list of proteins

function loadProteinTaxonomyIdDataForProtein( proteinIds, doDraw ) {

	console.log( "Loading protein taxonomy id data for proteins: " + proteinIds );

	incrementSpinner();				// create spinner
	
	var url = contextPathJSVar + "/services/proteinTaxonomyId/getDataForProtein";


	var project_id = $("#project_id").val();
	
	if ( project_id === undefined || project_id === null 
			|| project_id === "" ) {
		
		throw '$("#project_id").val() returned no value';
	}
	

	var ajaxRequestData = {
			project_id : project_id,
			proteinIds: proteinIds
	};

	
	$.ajax({
	        type: "GET",
	        url: url,
			dataType: "json",
			data: ajaxRequestData,  //  The data sent as params on the URL
	        
			traditional: true,  //  Force traditional serialization of the data sent
			//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
			//   So proteinIdsToGetSequence array is passed as "proteinIdsToGetSequence=<value>" which is what Jersey expects

	        success: function(data)	{
	        
	        	var returnedProteinIdsAndTaxonomyIds = data;  //  The property names are the protein ids and the property values are the taxonomy ids
	        	
        	
	        	// copy the returned taxonomy ids into the global object
	        	
	    		var returnedProteinIdsAndTaxonomyIds_Keys = Object.keys( returnedProteinIdsAndTaxonomyIds );
	    		
	    		for ( var keysIndex = 0; keysIndex < returnedProteinIdsAndTaxonomyIds_Keys.length; keysIndex++ ) {
	    			
	    			var proteinId = returnedProteinIdsAndTaxonomyIds_Keys[ keysIndex ];
	    			
	    			_proteinTaxonomyIds[ proteinId ] = returnedProteinIdsAndTaxonomyIds[ proteinId ];
	    		}
	    		
	        	
	        	decrementSpinner();
	        	loadDataAndDraw( doDraw );
	        },
	        failure: function(errMsg) {
				decrementSpinner();
	        	handleAJAXFailure( errMsg );
	        },
	        error: function(jqXHR, textStatus, errorThrown) {	
					decrementSpinner();
					handleAJAXError( jqXHR, textStatus, errorThrown );
//							alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ", textStatus: " + textStatus );
			}
	  });	

}




//Toggle the visibility of monolink data on the viewer
function loadMonolinkData( doDraw ) {
	
	console.log( "Loading monolink data." );
	
	if ( $( "input#show-monolinks" ).is( ':checked' ) ) {
		if ( _proteinMonolinkPositions != undefined ) {
			reportLinkDataLoadComplete( 'monolinks', doDraw );
		} else {
			
			incrementSpinner();				// create spinner
			
			var url = contextPathJSVar + "/services/imageViewer/getMonolinkData";
			url += buildQueryStringFromHash();
			
			// var request = 
			$.ajax({
			        type: "GET",
			        url: url,
			        dataType: "json",
			        success: function(data)	{
			        
			        	_proteinMonolinkPositions = data.proteinMonoLinkPositions;

			        	decrementSpinner();
			        	
			        	reportLinkDataLoadComplete( 'monolinks', doDraw );

			        },
			        failure: function(errMsg) {
						decrementSpinner();
			        	handleAJAXFailure( errMsg );
			        },
			        error: function(jqXHR, textStatus, errorThrown) {	
			        	decrementSpinner();
							handleAJAXError( jqXHR, textStatus, errorThrown );							
					}
			  });
			
		}
	} else {
		reportLinkDataLoadComplete( 'monolinks', doDraw );
	}
}


function loadMonolinkPSMCounts( doDraw ) {
	
	console.log( "Loading monolink PSM counts." );
	
	if ( $( "input#show-monolinks" ).is( ':checked' ) &&  $( "input#shade-by-counts" ).is( ':checked' ) ) {
		if ( _linkPSMCounts.monolink != undefined ) {
			reportLinkDataLoadComplete( 'monolinkPSMs', doDraw );
		} else {
			
			incrementSpinner();				// create spinner
			
			var url = contextPathJSVar + "/services/imageViewer/getMonolinkPSMCounts";
			url += buildQueryStringFromHash();
			
			// var request = 
			$.ajax({
			        type: "GET",
			        url: url,
			        dataType: "json",
			        success: function(data)	{
			        
			        	_linkPSMCounts.monolink = data.monolinkPSMCounts;
			        	
			        	decrementSpinner();
			        	
						reportLinkDataLoadComplete( 'monolinkPSMs', doDraw );

			        },
			        failure: function(errMsg) {
						decrementSpinner();
			        	handleAJAXFailure( errMsg );
			        },
			        error: function(jqXHR, textStatus, errorThrown) {	
							decrementSpinner();
							handleAJAXError( jqXHR, textStatus, errorThrown );							
					}
			  });
			
		}
	} else {
		reportLinkDataLoadComplete( 'monolinkPSMs', doDraw );
	}
}




//Toggle the visibility of looplink data on the viewer
function loadLooplinkData( doDraw ) {
	
	console.log( "Loading looplink data." );
	
	if ( $( "input#show-looplinks" ).is( ':checked' ) ) {
		if ( _proteinLooplinkPositions != undefined ) {
			reportLinkDataLoadComplete( 'looplinks', doDraw );
		} else {
			
			incrementSpinner();				// create spinner
			
			var url = contextPathJSVar + "/services/imageViewer/getLooplinkData";
			url += buildQueryStringFromHash();
			
			// var request = 
			$.ajax({
			        type: "GET",
			        url: url,
			        dataType: "json",
			        success: function(data)	{
			        
			        	_proteinLooplinkPositions = data.proteinLoopLinkPositions;
			        	
			        	decrementSpinner();
			        	
			        	reportLinkDataLoadComplete( 'looplinks', doDraw );

			        },
			        failure: function(errMsg) {
						decrementSpinner();
			        	handleAJAXFailure( errMsg );
			        },
					error: function(jqXHR, textStatus, errorThrown) {	
							decrementSpinner();
							handleAJAXError( jqXHR, textStatus, errorThrown );
					}
			  });
			
		}
	} else {
		reportLinkDataLoadComplete( 'looplinks', doDraw );
	}
}

function loadLooplinkPSMCounts( doDraw ) {
	
	console.log( "Loading looplink PSM counts." );
	
	if ( $( "input#show-looplinks" ).is( ':checked' ) &&  $( "input#shade-by-counts" ).is( ':checked' ) ) {
		if ( _linkPSMCounts.looplink != undefined ) {
			reportLinkDataLoadComplete( 'looplinkPSMs', doDraw );
		} else {
			
			incrementSpinner();				// create spinner
			
			var url = contextPathJSVar + "/services/imageViewer/getLooplinkPSMCounts";
			url += buildQueryStringFromHash();
			
			// var request = 
			$.ajax({
			        type: "GET",
			        url: url,
			        dataType: "json",
			        success: function(data)	{
			        
			        	_linkPSMCounts.looplink = data.looplinkPSMCounts;
			        	
			        	decrementSpinner();
			        	
						reportLinkDataLoadComplete( 'looplinkPSMs', doDraw );

			        },
			        failure: function(errMsg) {
						decrementSpinner();
			        	handleAJAXFailure( errMsg );
			        },
			        error: function(jqXHR, textStatus, errorThrown) {	
							decrementSpinner();
							handleAJAXError( jqXHR, textStatus, errorThrown );							
					}
			  });
			
		}
	} else {
		reportLinkDataLoadComplete( 'looplinkPSMs', doDraw );
	}
}



//Toggle the visibility of crosslink data on the viewer
function loadCrosslinkData( doDraw ) {
	
	console.log( "Loading crosslink data." );
	
	if ( $( "input#show-crosslinks" ).is( ':checked' ) ||  $( "input#show-self-crosslinks" ).is( ':checked' ) ) {
		if ( _proteinLinkPositions != undefined ) {
			reportLinkDataLoadComplete( 'crosslinks', doDraw );
		} else {
			
			incrementSpinner();				// create spinner
			
			var url = contextPathJSVar + "/services/imageViewer/getCrosslinkData";
			url += buildQueryStringFromHash();
			
			 $.ajax({
			        type: "GET",
			        url: url,
			        dataType: "json",
			        success: function(data)	{
			        
			        	_proteinLinkPositions = data.proteinLinkPositions;
			        	
			        	decrementSpinner();
			        	
			        	reportLinkDataLoadComplete( 'crosslinks', doDraw );
			        	
			        },
			        failure: function(errMsg) {
						decrementSpinner();
			        	handleAJAXFailure( errMsg );
			        },
					error: function(jqXHR, textStatus, errorThrown) {	
							decrementSpinner();
							handleAJAXError( jqXHR, textStatus, errorThrown );
//							alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ", textStatus: " + textStatus );
					}
			  });
			
		}
	} else {
		reportLinkDataLoadComplete( 'crosslinks', doDraw );
	}
}

function loadCrosslinkPSMCounts( doDraw ) {
	
	console.log( "Loading crosslink PSM counts." );
	
	if ( $( "input#show-crosslinks" ).is( ':checked' ) &&  $( "input#shade-by-counts" ).is( ':checked' ) ) {
		if ( _linkPSMCounts.crosslink != undefined ) {
			reportLinkDataLoadComplete( 'crosslinkPSMs', doDraw );
		} else {
			
			incrementSpinner();				// create spinner
			
			var url = contextPathJSVar + "/services/imageViewer/getCrosslinkPSMCounts";
			url += buildQueryStringFromHash();
			
			// var request = 
			$.ajax({
			        type: "GET",
			        url: url,
			        dataType: "json",
			        success: function(data)	{
			        
			        	_linkPSMCounts.crosslink = data.crosslinkPSMCounts;
			        	
			        	decrementSpinner();
			        	
						reportLinkDataLoadComplete( 'crosslinkPSMs', doDraw );

			        },
			        failure: function(errMsg) {
						decrementSpinner();
			        	handleAJAXFailure( errMsg );
			        },
			        error: function(jqXHR, textStatus, errorThrown) {	
							decrementSpinner();
							handleAJAXError( jqXHR, textStatus, errorThrown );							
					}
			  });
			
		}
	} else {
		reportLinkDataLoadComplete( 'crosslinkPSMs', doDraw );
	}
}


var _dataLoadManager = { };

var loadRequiredLinkData = function( doDraw ) {
	
	if( !( 'currentLoad' in _dataLoadManager ) ) { _dataLoadManager.currentLoad = { }; }
	if( !( 'currentLoadCount' in _dataLoadManager ) ) { _dataLoadManager.currentLoadCount = 0; }
	
	if ( $( "input#show-crosslinks" ).is( ':checked' ) || $( "input#show-self-crosslinks" ).is( ':checked' ) ) {
		
		if( _proteinLinkPositions == undefined && !_dataLoadManager.currentLoad[ 'crosslinks' ] ) {
						
			_dataLoadManager.currentLoad[ 'crosslinks' ] = 1;
			_dataLoadManager.currentLoadCount = _dataLoadManager.currentLoadCount + 1;
				
			loadCrosslinkData( doDraw );
		}
		
		if ( !_linkPSMCounts.crosslink && $( "input#shade-by-counts" ).is( ':checked' ) ) {
			_dataLoadManager.currentLoad[ 'crosslinkPSMs' ] = 1;
			_dataLoadManager.currentLoadCount = _dataLoadManager.currentLoadCount + 1;
				
			loadCrosslinkPSMCounts( doDraw );
		}
		
	}
	
	if ( $( "input#show-looplinks" ).is( ':checked' ) ) {

		if( _proteinLooplinkPositions == undefined && !_dataLoadManager.currentLoad[ 'looplinks' ] ) {
			
			_dataLoadManager.currentLoad[ 'looplinks' ] = 1;
			_dataLoadManager.currentLoadCount = _dataLoadManager.currentLoadCount + 1;
				
			loadLooplinkData( doDraw );
		}
		
		if ( !_linkPSMCounts.looplink && $( "input#shade-by-counts" ).is( ':checked' ) ) {
			_dataLoadManager.currentLoad[ 'looplinkPSMs' ] = 1;
			_dataLoadManager.currentLoadCount = _dataLoadManager.currentLoadCount + 1;
				
			loadLooplinkPSMCounts( doDraw );
		}
		
	}
	
	if ( $( "input#show-monolinks" ).is( ':checked' ) ) {
		
		if( _proteinMonolinkPositions == undefined && !_dataLoadManager.currentLoad[ 'monolinks' ] ) {
			
			_dataLoadManager.currentLoad[ 'monolinks' ] = 1;
			_dataLoadManager.currentLoadCount = _dataLoadManager.currentLoadCount + 1;
				
			loadMonolinkData( doDraw );
		}
		
		if ( !_linkPSMCounts.monolink && $( "input#shade-by-counts" ).is( ':checked' ) ) {
			_dataLoadManager.currentLoad[ 'monolinkPSMs' ] = 1;
			_dataLoadManager.currentLoadCount = _dataLoadManager.currentLoadCount + 1;
				
			loadMonolinkPSMCounts( doDraw );
		}
	}

	// if we get here, there was nothing to load--just draw
	if( _dataLoadManager.currentLoadCount == 0 && doDraw ) {
		loadDataAndDraw( doDraw, true );
	}
	
};


var reportLinkDataLoadComplete = function ( loadStringId, doDraw ) {
	
	// should never happen, if it does, don't redraw
	if( !( 'currentLoad' in _dataLoadManager ) ) {
		console.log( "ERROR: reportLinkDataLoadComplete() called with no 'currentLoad' property set in _dataLoadManager. _dataLoadManager:" );
		console.log( _dataLoadManager );
		return;
	}
	
	// should never happen, if it does, don't redraw
	if( !( 'currentLoadCount' in _dataLoadManager ) ) {
		console.log( "ERROR: reportLinkDataLoadComplete() called with no 'currentLoadCount' property set in _dataLoadManager. _dataLoadManager:" );
		console.log( _dataLoadManager );
		return;
	}
	
	// should also never happen, if it does don't redraw
	if( !( loadStringId in _dataLoadManager.currentLoad ) ) {
		console.log( "ERROR: reportLinkDataLoadComplete() called with no '" + loadStringId + "' property set in _dataLoadManager.currentLoad. _dataLoadManager:" );
		console.log( _dataLoadManager );
		return;
	}
	
	_dataLoadManager.currentLoad[ loadStringId ] = 0;
	_dataLoadManager.currentLoadCount = _dataLoadManager.currentLoadCount - 1;
	
	if( _dataLoadManager.currentLoadCount < 0 ) {
		console.log( "WARNING: _dataLoadManager.currentLoadCount is less than 0? Setting to 0 and continuing. _dataLoadManager:" );
		console.log( _dataLoadManager );
		
		_dataLoadManager.currentLoadCount = 0;
	}

	console.log( _dataLoadManager );
	
	if( _dataLoadManager.currentLoadCount === 0 && doDraw ) {
		loadDataAndDraw( doDraw, true );
	}
};


//ensure the necessary data are collected before viewer is drawn
function loadDataAndDraw( doDraw, loadComplete ) {
	
	if( !loadComplete ) { return loadRequiredLinkData( doDraw ); }

	
	
	// only load sequences for visible proteins

	var selectedProteins = getAllSelectedProteins();
	
	var proteinIdsToGetSequence = [];
	
	for ( var i = 0; i < selectedProteins.length; i++ ) {
		var proteinId = selectedProteins[ i ];
		if ( _proteinSequences[ proteinId ] == undefined ) {
			
			proteinIdsToGetSequence.push( proteinId );
		}
	}

	if ( proteinIdsToGetSequence.length > 0 ) {
		
		return loadProteinSequenceDataForProtein( proteinIdsToGetSequence, doDraw );
	}
	
	
	
	//   Add computing of Trypsin cut points
	
	for ( var i = 0; i < selectedProteins.length; i++ ) {
		var proteinId = selectedProteins[ i ];
		if ( _proteinSequenceTrypsinCutPoints[ proteinId ] == undefined ) {
			
			var proteinSequence = _proteinSequences[ proteinId ];
			var cutPointsForProteinSequence = computeCutPoints( proteinSequence );
			_proteinSequenceTrypsinCutPoints[ proteinId ] = cutPointsForProteinSequence;
		}
	}
	
	
	
	
	// only load taxonomy ids for visible proteins
	
	var proteinIdsToGetTaxonomyIds = [];
	
	for ( var i = 0; i < selectedProteins.length; i++ ) {
		var proteinId = selectedProteins[ i ];
		if ( _proteinTaxonomyIds[ proteinId ] == undefined ) {
			
			proteinIdsToGetTaxonomyIds.push( proteinId );
		}
	}

	if ( proteinIdsToGetTaxonomyIds.length > 0 ) {
		
		return loadProteinTaxonomyIdDataForProtein( proteinIdsToGetTaxonomyIds, doDraw );
	}
	
	
	
	
	
	// only load annotation data ( other than Sequence Coverage ) for visible proteins
	
	var selectedProteins = getAllSelectedProteins();

	var annotationType = $("#annotation_type").val();
	
	var annotationType_DisplayText = $("#annotation_type option:selected").text();
	
	
	if ( annotationType === SELECT_ELEMENT_ANNOTATION_TYPE_SEQUENCE_COVERAGE ) {
		
		for ( var i = 0; i < selectedProteins.length; i++ ) {
			var prot = selectedProteins[ i ];
			if ( _ranges == undefined || _ranges[ prot ] == undefined ) {
				return loadSequenceCoverageDataForProtein( prot, doDraw );
			}
		}
	
	} else if ( annotationType !== undefined && annotationType !== "" ) {
		
		// load Selected Protein Annotation for visible proteins
		
		
		////////////////////
		

		incrementSpinner();				// create spinner
		


//		If all annotations are loaded just from the submit call (all are in the database ready to load)


		var annotationLoadingAllCompleteOnSubmit = function( params ) {

			var doDraw = params.doDraw;


			decrementSpinner();


			loadDataAndDraw( doDraw );
		};




//		If at least One annotation loaded from the submit call or Get Call is FAILED


		var annotationLoadingAtLeastOneFailed = function( initialLoadAnnotationsParams, errorParams ) {

//			var doDraw = initialLoadAnnotationsParams.doDraw;
			
			var proteinIdsThatFailed = errorParams.proteinIdsThatFailed;
			
			var annotationType_DisplayText_local = initialLoadAnnotationsParams.annotationType_DisplayText;
		


			decrementSpinner();

			$("#pgm_failed_annotation_data_overlay_annotation_type_header_text").text( annotationType_DisplayText_local );

			$("#pgm_failed_annotation_data_overlay_annotation_type_text").text( annotationType_DisplayText_local );
			

			var proteinNamesThatFailedString = "";
			
			if ( proteinIdsThatFailed.length > 0 ) {
				

				var proteinNamesThatFailedArray = [];

				for ( var proteinIdsThatFailedIndex = 0; proteinIdsThatFailedIndex < proteinIdsThatFailed.length; proteinIdsThatFailedIndex++ ) {

					var proteinIdThatFailed = proteinIdsThatFailed[ proteinIdsThatFailedIndex ];
					var proteinNameThatFailed = _proteinNames[ proteinIdThatFailed ];
					proteinNamesThatFailedArray.push( proteinNameThatFailed );
				}

				proteinNamesThatFailedArray.sort();

				proteinNamesThatFailedString = proteinNamesThatFailedArray[ 0 ]; // init to first protein name

				for ( var proteinNamesThatFailedArrayIndex = 1; proteinNamesThatFailedArrayIndex < proteinNamesThatFailedArray.length; proteinNamesThatFailedArrayIndex++ ) {

					var proteinName = proteinNamesThatFailedArray[ proteinNamesThatFailedArrayIndex ];

					proteinNamesThatFailedString += ", " + proteinName;
				}
			}
			
			$("#pgm_failed_annotation_data_overlay_failed_protein_names_text").text( proteinNamesThatFailedString );
			
			$("#pgm_failed_annotation_data_modal_dialog_overlay_background").show();

			$("#pgm_failed_annotation_data_overlay_div").show();


			
		};
		




//		If all annotations are loaded just from the submit call (all are in the database ready to load)


		var annotationLoading_NOT_AllCompleteOnSubmit = function( params ) {

//			var doDraw = params.doDraw;


//			decrementSpinner();


//			alert("Processing of one or more proteins for annotation type is required so there will be a delay in the availablity of viewing the data."
//			+ "  Please try again later.  A message will pop up when the data for the currently shown proteins is available.");

//			$("#annotation_type").val("");
//			updateURLHash( false /* useSearchForm */ );

//			loadDataAndDraw( doDraw );
			
			
			var annotationType_DisplayText_local = params.annotationType_DisplayText;
			
			$("#run_pgm_annotation_data_overlay_annotation_type_text").text( annotationType_DisplayText_local );


			$("#run_pgm_annotation_data_modal_dialog_overlay_background").show();

			$("#run_pgm_annotation_data_overlay_div").show();


		};




		// If all annotations are loaded from get calls that are delayed


		var annotationLoadingAllCompleteOnGet = function( initialLoadAnnotationsParams ) {

			var doDraw = initialLoadAnnotationsParams.doDraw;


			decrementSpinner();

			$("#run_pgm_annotation_data_modal_dialog_overlay_background").hide();

			$("#run_pgm_annotation_data_overlay_div").hide();


//			alert("All annotation data is computed and ready for display");


			loadDataAndDraw( doDraw );
		};



		var functionCallbacks = { 
				
				annotationLoadingAllCompleteOnSubmit : annotationLoadingAllCompleteOnSubmit,
				annotationLoadingAtLeastOneFailedOnSubmit : annotationLoadingAtLeastOneFailed,
				annotationLoading_NOT_AllCompleteOnSubmit : annotationLoading_NOT_AllCompleteOnSubmit,
				
				annotationLoadingAllCompleteOnGet : annotationLoadingAllCompleteOnGet,
				annotationLoadingAtLeastOneFailedOnGet : annotationLoadingAtLeastOneFailed
		};
		
		
		var proteinAnnotationStore_loadProteinAnnotationData_request = {
			
			annotationType : annotationType, 
			selectedProteins : selectedProteins,
			
			globalMainData : {
				
				proteinSequences : _proteinSequences,
				proteinTaxonomyIds : _proteinTaxonomyIds,
				proteinNames : _proteinNames,
				proteinLengths : _proteinLengths
			},

			functionCallbacks : functionCallbacks,
			
			annotationType_DisplayText : annotationType_DisplayText,
			
			doDraw : doDraw
		};
		

		
		var proteinAnnotationStore_loadProteinAnnotationData_response = null;
		
		if ( annotationType === SELECT_ELEMENT_ANNOTATION_TYPE_DISOPRED3 ) {
			
			proteinAnnotationStore_loadProteinAnnotationData_response =
				proteinAnnotationStore.loadProteinAnnotationData__Disopred_3_Data( proteinAnnotationStore_loadProteinAnnotationData_request );

			
		} else if ( annotationType === SELECT_ELEMENT_ANNOTATION_TYPE_PSIPRED3 ) {
				
			proteinAnnotationStore_loadProteinAnnotationData_response =
				proteinAnnotationStore.loadProteinAnnotationData__Psipred_3_Data( proteinAnnotationStore_loadProteinAnnotationData_request );
				
		} else {
			
			throw "unknown annotationType: " + annotationType;
		}
		
		
		
		if ( proteinAnnotationStore_loadProteinAnnotationData_response.waitingAllSubmitResponse ) {
			
			return;
		}
		
		if ( ! proteinAnnotationStore_loadProteinAnnotationData_response.allDataLoaded ) {

			$("#annotation_type").val("");
			updateURLHash( false /* useSearchForm */ );


			//  return;  //  do return if put up blocking modal dialog with cancel
		} else {
			
			decrementSpinner();

		}
	}
	
	
	if ( doDraw ) {
		drawSvg();
	}
}


////////////

function loadDataFromService() {
	
	console.log( "Loading protein data." );
	
	incrementSpinner();				// create spinner
	
	var url = contextPathJSVar + "/services/imageViewer/getProteinData";
	url += buildQueryStringFromHash();
	
	 $.ajax({
	        type: "GET",
	        url: url,
	        dataType: "json",
	        success: function(data)	{
	        
	        	// handle searches
	        	_searches = data.searches;
	        	
	        	// handle proteins
	        	_proteins = data.proteins;
	        	
	        	// handle protein lengths
	        	_proteinLengths = data.proteinLengths;
	        	
	        	// handle protein names
	        	_proteinNames = data.proteinNames;
	        	
	        	// positions of all linkable-positions
	        	_linkablePositions = data.linkablePositions;
	        	
	        	// handle other search parameters

	        	_psmPeptideCutoffsRootObjectStorage.setPsmPeptideCutoffsRootObject( data.cutoffs );
	        	
	        	_excludeTaxonomy = data.excludeTaxonomy;
	        	_excludeType = data.excludeType;
	        	_filterNonUniquePeptides = data.filterNonUniquePeptides;
	        	_filterOnlyOnePSM = data.filterOnlyOnePSM;
	        	_filterOnlyOnePeptide = data.filterOnlyOnePeptide;
	        	_taxonomies = data.taxonomies;
	        	
	        	//  Populate these from the Hash
	        	
	        	populateFromHash_imageProteinBarDataManager();
	        	
	        	
	        	populateNavigation();
	        	
	        	//  Populate from local variables
	        	populateSearchForm();
	        	
	        	

	        	populateSelectProteinSelect();
	        	
	        	initializeViewer();
	        	
	        	updateURLHash( false /* useSearchForm */ );
	        	
	        	decrementSpinner();
	        	
	        	

	        	//  populate _imageProteinBarDataManager from the data loaded from server

	        	populateFromDataLoaded_imageProteinBarDataManager();
	        	
	        	
	        	
	        	//  Find and show max protein length
	        	
//	        	var maxProteinLength = 0;
//	        	var maxProteinLength_proteinId = 0;
//	        	
//	        	var proteinLengthsKeys = Object.keys( _proteinLengths );
//
//	        	for ( var i = 0; i < proteinLengthsKeys.length; i++ ) {
//	        		
//	        		var id = proteinLengthsKeys[ i ];
//	        		var proteinLength = _proteinLengths[ id ];
//	        		
//	        		if ( proteinLength > maxProteinLength ) {
//	        			
//	        			maxProteinLength = proteinLength;
//	        			maxProteinLength_proteinId = id;
//	        		}
//	        	}
//	        	
//	        	var proteinNameOfMaxLengthProtein = _proteinNames[ maxProteinLength_proteinId ];
//	        	
//	        	alert( "Longest protein:  id: " + maxProteinLength_proteinId + ", length: " + maxProteinLength 
//	        			+ ", name: " + proteinNameOfMaxLengthProtein );
	        	
	        	/////////
	        	
	        	
	        	loadDataAndDraw( true /* doDraw */ );
	        	
	        	
	        },
	        failure: function(errMsg) {
				decrementSpinner();
	        	handleAJAXFailure( errMsg );
	        },
			error: function(jqXHR, textStatus, errorThrown) {
				handleAJAXError( jqXHR, textStatus, errorThrown );
//					alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ", textStatus: " + textStatus );
			}
	  });
}


//  populate _imageProteinBarDataManager from the hash

function populateFromHash_imageProteinBarDataManager(){
	
	var json = getJsonFromHash();

	var proteinBarData = json['protein_bar_data'];
	
	if ( proteinBarData !== undefined ) {
		
		_imageProteinBarDataManager.replaceInternalObjectsWithObjectsInHash( { 
			proteinBarData : proteinBarData,
			currentProteinIdsArray : _proteins
		} );
	
	} else {

		//  Backwards compatibility Support
		
		
		
		//   IMPORTANT

		//  If items[ 'protein_bar_data' ] is undefined, it needs to be built from old/previous values 
		//                to be backwards compatible
		
		//  Build from other sources
		
		
		
		var selectedProteinIds = json[ 'selected-proteins' ];

		if ( selectedProteinIds !== undefined && selectedProteinIds !== null ) {

			for ( var selectedProteinIdsIndex = 0; selectedProteinIdsIndex < selectedProteinIds.length; selectedProteinIdsIndex++ ) {

				var selectedProteinId = selectedProteinIds[ selectedProteinIdsIndex ];
				
				//  _proteinLengths may not be populated at this point
				
				var selectedProteinLength = undefined;
				
				if ( _proteinLengths ) {
					
					selectedProteinLength = _proteinLengths[ selectedProteinId ];
				}
				
				var newEntry = ImageProteinBarData.constructEmptyImageProteinBarData();
				
				newEntry.setProteinId( { proteinId : selectedProteinId, proteinLength : selectedProteinLength } );
				
				_imageProteinBarDataManager.addEntry( { arrayIndexInt : selectedProteinIdsIndex, entry : newEntry } );
			}
			

			var highlightedProteins = json['highlighted-proteins'];  //   Array of the indexes of the highlighted proteins
			
			if ( highlightedProteins !== undefined && highlightedProteins !== null ) {

				for ( var highlightedProteinsIndex = 0; highlightedProteinsIndex < highlightedProteins.length; highlightedProteinsIndex++ ) {

					var highlightedProteinProteinBarPositionIndex = highlightedProteins[ highlightedProteinsIndex ];

					try {
						
						//  Try/Catch since the index may not be valid
						
						var entry = _imageProteinBarDataManager.getItemByIndex( { arrayIndexInt : highlightedProteinProteinBarPositionIndex } );
						
						entry.setProteinBarHighlightedAll();
						
					} catch ( e ) {
						
						
					}
				}
			}			
		}
	}
	
	//  More conversion from old data
	
	//  Object. Property keys are protein ids.  The reversed proteins have a value of true

	var proteinsReversedObject = json['proteins-reversed'];  

	if ( proteinsReversedObject !== undefined && proteinsReversedObject !== null ) {

		var imageProteinBarDataItems = _imageProteinBarDataManager.getAllItems();
		
		var proteinsReversedKeys = Object.keys( proteinsReversedObject );
		
		for ( var proteinsReversedKeysIndex = 0; proteinsReversedKeysIndex < proteinsReversedKeys.length; proteinsReversedKeysIndex++ ) {
		
			var reversedProtein_Key = proteinsReversedKeys[ proteinsReversedKeysIndex ];
			
			var reversedProtein_Value = proteinsReversedObject[ reversedProtein_Key ];
			
			if ( reversedProtein_Value ) {

				//  value is true so this protein id is reversed

				var reversedProtein_ProteinId = reversedProtein_Key;

				//  Find all entries in _imageProteinBarDataManager with this protein id and set the protein reversed property to true

				for ( var imageProteinBarDataItemsIndex = 0; imageProteinBarDataItemsIndex < imageProteinBarDataItems.length; imageProteinBarDataItemsIndex ++ ) {

					var imageProteinBarDataEntry = imageProteinBarDataItems[ imageProteinBarDataItemsIndex ];

					var imageProteinBarDataEntryProteinId = imageProteinBarDataEntry.getProteinId();

					if ( imageProteinBarDataEntryProteinId === reversedProtein_ProteinId ) {

						imageProteinBarDataEntry.setProteinReversed( { proteinReversed : true } );
					}
				}
			}
		}
	}
	
	

	//  Object. Property keys are protein ids.  Value are from the left edge

	var proteinsOffsetsObject = json['protein-offsets'];  

	if ( proteinsOffsetsObject !== undefined && proteinsOffsetsObject !== null ) {

		var imageProteinBarDataItems = _imageProteinBarDataManager.getAllItems();
		
		var proteinsOffsetsKeys = Object.keys( proteinsOffsetsObject );
		
		for ( var proteinsOffsetsKeysIndex = 0; proteinsOffsetsKeysIndex < proteinsOffsetsKeys.length; proteinsOffsetsKeysIndex++ ) {
		
			var offsetProtein_Key = proteinsOffsetsKeys[ proteinsOffsetsKeysIndex ];
			
			var offsetProtein_Value = proteinsOffsetsObject[ offsetProtein_Key ];
			
			if ( offsetProtein_Value !== undefined && offsetProtein_Value !== null ) {

				//  value is a number so use it

				var offsetProtein_ProteinId = offsetProtein_Key;

				//  Find all entries in _imageProteinBarDataManager with this protein id and set the protein offset property to the value

				for ( var imageProteinBarDataItemsIndex = 0; imageProteinBarDataItemsIndex < imageProteinBarDataItems.length; imageProteinBarDataItemsIndex ++ ) {

					var imageProteinBarDataEntry = imageProteinBarDataItems[ imageProteinBarDataItemsIndex ];

					var imageProteinBarDataEntryProteinId = imageProteinBarDataEntry.getProteinId();

					if ( imageProteinBarDataEntryProteinId === offsetProtein_ProteinId ) {

						imageProteinBarDataEntry.setProteinOffset( { proteinOffset : offsetProtein_Value } );
					}
				}
			}
		}
	}
	
	
}



//  populate _imageProteinBarDataManager from the data loaded from server

function populateFromDataLoaded_imageProteinBarDataManager(){

	_imageProteinBarDataManager.updateProteinLengths( { proteinLengths : _proteinLengths } );
}


function populateSearchForm() {
	
	cutoffProcessingCommonCode.putCutoffsOnThePage(  { cutoffs : _psmPeptideCutoffsRootObjectStorage.getPsmPeptideCutoffsRootObject() } );

	
	$( "input#filterNonUniquePeptides" ).prop('checked', _filterNonUniquePeptides);
	$( "input#filterOnlyOnePSM" ).prop('checked', _filterOnlyOnePSM);
	$( "input#filterOnlyOnePeptide" ).prop('checked', _filterOnlyOnePeptide);
	
	var html = "";
	var taxKeys = Object.keys( _taxonomies );
	
	for ( var i = 0; i < taxKeys.length; i++ ) {
		var id = taxKeys[ i ];
		var name = _taxonomies[ id ];
		
		html += "<label><span style=\"white-space:nowrap;\" ><input type=\"checkbox\" name=\"excludeTaxonomy\" id=\"exclude-taxonomy-" + id + "\" value=\"" + id + "\">";
		html += "<span style=\"font-style:italic;\">" + name + "</span></span></label> ";		
	}
	
	var $taxonomy_checkboxes = $( "div#taxonomy-checkboxes" );
	
	$taxonomy_checkboxes.empty();
	$taxonomy_checkboxes.html( html );
	
	$taxonomy_checkboxes.find("input").change(function() {
		
		defaultPageView.searchFormChanged_ForDefaultPageView();;
	});
	
	
	if ( _excludeTaxonomy != undefined && _excludeTaxonomy.length > 0 ) {
		for ( var i = 0; i < _excludeTaxonomy.length; i++ ) {
			$( "input#exclude-taxonomy-" + _excludeTaxonomy[ i ] ).prop( 'checked', true );
		}
	}

	if ( _excludeType != undefined && _excludeType.length > 0 ) {
		for ( var i = 0; i < _excludeType.length; i++ ) {
			$( "input#exclude-type-" + _excludeType[ i ] ).prop( 'checked', true );
		}
	}
	
}

function populateViewerCheckBoxes() {
	
	
	var json = getJsonFromHash();
	
	$( "input#show-self-crosslinks" ).prop('checked', json[ 'show-self-crosslinks' ] );
	$( "input#show-looplinks" ).prop('checked', json[ 'show-looplinks' ] );
	$( "input#show-crosslinks" ).prop('checked', json[ 'show-crosslinks' ] );
	$( "input#show-monolinks" ).prop('checked', json[ 'show-monolinks' ] );

	$( "input#show-linkable-positions" ).prop('checked', json[ 'show-linkable-positions' ] );
	$( "input#show-tryptic-cleavage-positions" ).prop('checked', json[ 'show-tryptic-cleavage-positions' ] );
	
	
	$( "input#show-protein-termini" ).prop('checked', json[ 'show-protein-termini' ] );
	
	$( "input#show-scalebar" ).prop('checked', json[ 'show-scalebar' ] );

	_colorLinesBy = json[ 'color_by' ];

	// Backwards compatable color-by-search
	
	if ( json[ 'color-by-search' ] ) {
		
		_colorLinesBy = SELECT_ELEMENT_COLOR_BY_SEARCH;
	}
		
	if ( _colorLinesBy != undefined ) {
		$("#color_by").val( _colorLinesBy );
	}
	
	if ( json[ 'annotation_type' ] != undefined ) {
		$("#annotation_type").val( json[ 'annotation_type' ] );
	}
	
	if( 'shade-by-counts' in json ) {
		$( "input#shade-by-counts" ).prop('checked', json[ 'shade-by-counts' ] );
	}
	
	// handle the scale factor visibility items
	if ( json[ 'automatic-sizing' ] != undefined && json[ 'automatic-sizing' ] === false ) {
		$( "input#automatic-sizing" ).prop('checked', false );
	}
	
	
	handleScaleFactorVisibility( true /* supressRedraw */ );
	
}



function showSelectedProteins() {

	
	// add the appropriate selected proteins
	
	var $selected_proteins_container = $("#selected_proteins_container");
	
	var $protein_select_text_container_jq = $selected_proteins_container.find(".protein_select_text_container_jq");
	
	$protein_select_text_container_jq.qtip('destroy', true); // Immediately destroy all tooltips belonging to the selected elements

	var $tool_tip_attached_jq_Children = $selected_proteins_container.find(".tool_tip_attached_jq");

	$tool_tip_attached_jq_Children.qtip('destroy', true); // Immediately destroy all tooltips belonging to the selected elements

	
	$selected_proteins_container.empty();
	
	
	
	var selectedProteinIds = getAllSelectedProteins();
	
	if ( !selectedProteinIds || selectedProteinIds.length < 1 || _proteins.length < 1 ) {

		$("#no_proteins_add_protein_outer_container").show();
		$("#svg_image_outer_container_div").hide();
		
		return;

	} else {
		
		$("#svg_image_outer_container_div").show();
		$("#no_proteins_add_protein_outer_container").hide();
	}
	
	

	var $selected_protein_entry_template = $("#selected_protein_entry_template");
	

	
	var selectedProteinEntry_template_handlebarsSource = $selected_protein_entry_template.text();

	if ( selectedProteinEntry_template_handlebarsSource === undefined ) {
		throw "selectedProteinEntry_template_handlebarsSource === undefined";
	}
	if ( selectedProteinEntry_template_handlebarsSource === null ) {
		throw "selectedProteinEntry_template_handlebarsSource === null";
	}

	
	if ( selectedProteinIds ) {

		var selectedProteinEntry_HandlebarsTemplate = Handlebars.compile( selectedProteinEntry_template_handlebarsSource );

		for ( var i = 0; i < selectedProteinIds.length; i++ ) {

			var proteinId = selectedProteinIds[ i ];
			
			var proteinName = getProteinName( proteinId );

			var selectedProteinEntryValue = { positionIndex : i, proteinId : proteinId , proteinName : proteinName };
			
			var selectedProteinHTML = selectedProteinEntry_HandlebarsTemplate( selectedProteinEntryValue );

			var $newEntry = 
				$( selectedProteinHTML ).appendTo( $selected_proteins_container );
			
			var $protein_select_text_container_jq_Items =  $newEntry.find(".protein_select_text_container_jq");
			
			$protein_select_text_container_jq_Items.each( function() {
				
				var $protein_select_text_container_jq = $( this );
				
				//  Add specific tool tip for the protein name
				
				addSingleTooltipForProteinName( { 
					$elementToAddToolTipTo : $protein_select_text_container_jq, 
					proteinIdString : proteinId,
					proteinDisplayName : proteinName
				} );
			} );
		}
		
	}

	//  Select Protein item and sort handle block of each Selected protein

	var $protein_select_protein_item_and_sort_handle_block_jq_Items = $selected_proteins_container.find(".protein_select_protein_item_and_sort_handle_block_jq");

	$protein_select_protein_item_and_sort_handle_block_jq_Items.click( function( eventObject ) {
		
		openSelectProteinSelect( { clickedThis : this } );
	} );
	
	


	//  Select Delete icon of each Selected protein

	var $protein_delete_icon_jq_Items = $selected_proteins_container.find(".protein_delete_icon_jq");

	//  Add click handler for removing the protein
	
	$protein_delete_icon_jq_Items.click( function( eventObject ) {
		
		processClickOnRemoveSelectedProtein( { clickedThis : this } );
	} );
	
	// Add General tool tips to everything in the container.  (including on Delete icon)
	
	addToolTips( $selected_proteins_container );
	
	
	$selected_proteins_container.sortable( {

		//  On sort start, call this function
        start : processSortStartSelectedProteins,
		
		//  This event is triggered when the user stopped sorting and the DOM position has changed.
		//        (User released the item they were dragging and items are now in "after sort" order)
        update : processSortUpdateSelectedProteins		
	} );
}

function processSortStartSelectedProteins( event, ui ) {
		
	var $item = ui.item;
	
	//  ui.item is  <div class="outer-float  protein_select_outer_block_jq" style="" >
	
	var $sort_handle_jq = $item.find(".sort_handle_jq");
	
	$sort_handle_jq.qtip('toggle', false); // Immediately hide all tooltips belonging to the selected elements
	
	var $protein_select_text_container_jq = $item.find(".protein_select_text_container_jq");
	
	$protein_select_text_container_jq.qtip('toggle', false); // Immediately hide all tooltips belonging to the selected elements
}

function processSortUpdateSelectedProteins( event, ui ) {
	
	console.log("processSortUpdateSelectedProteins");
	
	//   Update the Selected Proteins with their current index.
	
	var $selected_proteins_container = $("#selected_proteins_container");
	
	var $protein_select_outer_block_jq_Items = $selected_proteins_container.find(".protein_select_outer_block_jq");
	
	var elementPrevPositions = [];
	
	$protein_select_outer_block_jq_Items.each( function( index, element ) {
	
		var $this = $( this );
		
		var element_data_position_index = $this.attr("data-position_index");
		
		var element_data_position_index_Int = parseInt( element_data_position_index, 10 );
		
		if ( isNaN( element_data_position_index_Int ) ) {
			
			throw "element_data_position_index is not an integer: " + element_data_position_index;
		}
		
		var elementPrevPosition = { prevPosition : element_data_position_index_Int };
		
		elementPrevPositions.push( elementPrevPosition );
	} );
	
	_imageProteinBarDataManager.updateItemOrder( { elementPrevPositions : elementPrevPositions } );
	
	updateURLHash( false /* useSearchForm */ );
	
	showSelectedProteins();
	
	drawSvg();
}



function processClickOnRemoveSelectedProtein( params ) {
	
	var clickedThis = params.clickedThis;
	
	var $clickedThis = $( clickedThis );
	
	$clickedThis.qtip('destroy', true); // Immediately destroy all tooltips belonging to the selected elements
	
//	var protein_id = $clickedThis.attr("data-protein_id");
	
	var positionIndex = $clickedThis.attr("data-position_index");

	var positionIndexInt = parseInt( positionIndex, 10 );
	
	if ( isNaN( positionIndexInt ) ) {
		
		throw "positionIndex is not a number.  is: " + positionIndex;
	}
	
	_imageProteinBarDataManager.removeItemByIndex( { arrayIndexInt : positionIndexInt } );
	
	updateURLHash( false /* useSearchForm */ );
	
	showSelectedProteins();
	
	drawSvg();
}


function getNavigationJSON_Not_for_Image_Or_Structure() {

	var json = getJsonFromHash();
	

	///   Serialize cutoffs to JSON
	
	var cutoffs = json.cutoffs;
	
	//  Layout of baseJSONObject  matches Java class A_QueryBase_JSONRoot
	
	var baseJSONObject = { cutoffs : cutoffs };
	
	
	//  Add to baseJSONObject
	
	if ( json.filterNonUniquePeptides !== undefined ) {
		baseJSONObject.filterNonUniquePeptides = json.filterNonUniquePeptides;
	}
	if ( json.filterOnlyOnePSM !== undefined ) {
		baseJSONObject.filterOnlyOnePSM = json.filterOnlyOnePSM;
	}
	if ( json.filterOnlyOnePeptide !== undefined ) {
		baseJSONObject.filterOnlyOnePeptide = json.filterOnlyOnePeptide;
	}
	
	
	if ( json.excludeTaxonomy !== undefined ) {
		baseJSONObject.excludeTaxonomy = json.excludeTaxonomy;
	}

	return baseJSONObject;
}

//////////////////


function populateNavigation() {
	
	var queryString = "?";
	var items = new Array();
	
	
	if ( _searches.length > 1 ) {
		for ( var i = 0; i < _searchIds.length; i++ ) {
			items.push( "searchIds=" + _searchIds[ i ] );
		}
	} else {
		items.push( "searchId=" + _searchIds[ 0 ] );		
	}

	
	var baseJSONObject = getNavigationJSON_Not_for_Image_Or_Structure();
	
	
	
	var psmPeptideCutoffsForSearchIds_JSONString = JSON.stringify( baseJSONObject );
	
	
	var psmPeptideCutoffsForSearchIds_JSONStringEncodedURIComponent = encodeURIComponent( psmPeptideCutoffsForSearchIds_JSONString ); 

	//  Parameter name matches standard form parameter name for JSON
	
	items.push( "queryJSON=" + psmPeptideCutoffsForSearchIds_JSONStringEncodedURIComponent );

	
	queryString += items.join( "&" );

	var html = "";

	if ( _searches.length > 1 ) {
		html += " <span class=\"tool_tip_attached_jq\" data-tooltip=\"View peptides\" style=\"white-space:nowrap;\" >[<a href=\"" + contextPathJSVar + "/mergedPeptide.do" + queryString + "\">Peptide View</a>]</span>";
		html += " <span class=\"tool_tip_attached_jq\" data-tooltip=\"View proteins\" style=\"white-space:nowrap;\" >[<a href=\"" + contextPathJSVar + "/mergedCrosslinkProtein.do" + queryString + "\">Protein View</a>]</span>";
		html += " <span class=\"tool_tip_attached_jq\" data-tooltip=\"View protein coverage report\" style=\"white-space:nowrap;\" >[<a href=\"" + contextPathJSVar + "/mergedProteinCoverageReport.do" + queryString + "\">Coverage Report</a>]</span>";
	} else {
		

		//  Add Peptide Link
		
		html += " [<a class=\"tool_tip_attached_jq\" data-tooltip=\"View peptides\" href='" + contextPathJSVar + "/";
        
		var viewSearchPeptideDefaultPageUrl = $("#viewSearchPeptideDefaultPageUrl").val();
		
		if ( viewSearchPeptideDefaultPageUrl === undefined || viewSearchPeptideDefaultPageUrl === "" ) {
			      
			html += "peptide.do" + queryString;

		} else {
			
			html += viewSearchPeptideDefaultPageUrl;
			
		}
		html += "'>Peptide View</a>]";
				

		//  Add Protein View Link
		
		html += " [<a class=\"tool_tip_attached_jq\" data-tooltip=\"View proteins\" href='" + contextPathJSVar + "/";
        
		var viewSearchCrosslinkProteinDefaultPageUrl = $("#viewSearchCrosslinkProteinDefaultPageUrl").val();
		
		if ( viewSearchCrosslinkProteinDefaultPageUrl === undefined || viewSearchCrosslinkProteinDefaultPageUrl === "" ) {
			      
			html += "crosslinkProtein.do" + queryString;

		} else {
			
			html += viewSearchCrosslinkProteinDefaultPageUrl;
			
		}
		html += "'>Protein View</a>]";
				

		//  Add Coverage Report Link
		
		html += " [<a class=\"tool_tip_attached_jq\" data-tooltip=\"View protein coverage report\" href='" + contextPathJSVar + "/";
        
		var viewProteinCoverageReportDefaultPageUrl = $("#viewProteinCoverageReportDefaultPageUrl").val();
		
		if ( viewProteinCoverageReportDefaultPageUrl === undefined || viewProteinCoverageReportDefaultPageUrl === "" ) {
			      
			html += "proteinCoverageReport.do" + queryString;

		} else {
			
			html += viewProteinCoverageReportDefaultPageUrl;
			
		}
		html += "'>Coverage Report</a>]";
		
	}


	var $navigation_links_except_structure = $("#navigation_links_except_structure"); 
	
	
	$navigation_links_except_structure.empty();
	$navigation_links_except_structure.html( html );
	addToolTips( $navigation_links_except_structure );
	
	
	var $structure_viewer_link_span = $("#structure_viewer_link_span");
	
	if ( $structure_viewer_link_span.length > 0 ) {


		var structureNavHTML = "<span class=\"tool_tip_attached_jq\" data-tooltip=\"View data on 3D structures\" " +
			"style=\"white-space:nowrap;\" >[<a href=\"" + contextPathJSVar + "/";
			
			
		var viewMergedStructureDefaultPageUrl = $("#viewMergedStructureDefaultPageUrl").val();
		
		
		if ( viewMergedStructureDefaultPageUrl === undefined || viewMergedStructureDefaultPageUrl === "" ) {
			      
			var structureQueryString = "?";

			for ( var j = 0; j < _searchIds.length; j++ ) {

				if ( j > 0 ) {

					structureQueryString += "&";
				}

				structureQueryString += "searchIds=" + _searchIds[ j ];
			}

			var structureJSON = { };

//			add taxonomy exclusions
			structureJSON[ 'excludeTaxonomy' ] = _excludeTaxonomy;

//			add type exclusions
			structureJSON[ 'excludeType' ] = _excludeType;

			//  Add Filter cutoffs
			structureJSON[ 'cutoffs' ] = _psmPeptideCutoffsRootObjectStorage.getPsmPeptideCutoffsRootObject();


//			add filter out non unique peptides
			structureJSON[ 'filterNonUniquePeptides' ] = _filterNonUniquePeptides;

			structureJSON[ 'filterOnlyOnePSM' ] = _filterOnlyOnePSM;
			structureJSON[ 'filterOnlyOnePeptide' ] = _filterOnlyOnePeptide;

			var structureJSONString = encodeURIComponent( JSON.stringify( structureJSON ) );

			
			structureNavHTML += "structure.do" + structureQueryString + "#" + structureJSONString;


		} else {
			
			structureNavHTML += viewMergedStructureDefaultPageUrl;
			
		}
		
		structureNavHTML += "\">Structure View</a>]</span>";

		$structure_viewer_link_span.empty();
		$structure_viewer_link_span.html( structureNavHTML );
		addToolTips( $structure_viewer_link_span );

	}
	
}


//  Reset Proteins highlighting, flipping, positioning, and horizontal scaling and redraw

function resetProteins() {
	
	// reset protein highlighting
	_imageProteinBarDataManager.clearAllProteinBarsHighlighted();
	
	// reset protein flipping
	_imageProteinBarDataManager.clearAllProteinBarsReversed();
	
	// reset protein horizontal positioning
	_imageProteinBarDataManager.clearAllProteinBarsOffsets();
	
	// reset protein horizontal scaling
	var $automatic_sizing = $( "input#automatic-sizing" );
	
	if ( ! $automatic_sizing.prop( "checked" ) ) {
		
		$automatic_sizing.click();
	}
	
	updateURLHash( false /* useSearchForm */ );
	drawSvg();
}


// reset all protein offsets and redraw
function resetProteinOffsets() {
	
	_imageProteinBarDataManager.clearAllProteinBarsOffsets();
	
	updateURLHash( false /* useSearchForm */ );
	drawSvg();
}


// get the visual offset for a specific protein
function getProteinOffset( params ) {

	var proteinBarIndex = params.proteinBarIndex;
	
	var entry = _imageProteinBarDataManager.getItemByIndex( { arrayIndexInt : proteinBarIndex } );
	
	var proteinOffset = entry.getProteinOffset();

	return proteinOffset;
}



//reset all proteinsReversed and redraw
function resetProteinsReversed() {

	_imageProteinBarDataManager.clearAllProteinBarsReversed();

	updateURLHash( false /* useSearchForm */ );
	drawSvg();
}

//get if a specific protein should be displayed reversed
function isProteinReversed( params ) {

//	var proteinId = params.proteinId;
	var proteinBarIndex = params.proteinBarIndex;
	
	var entry = _imageProteinBarDataManager.getItemByIndex( { arrayIndexInt : proteinBarIndex } );
	
	var proteinReversed = entry.getProteinReversed();

	return proteinReversed;
}



function removeHighlightedProteinRegion( params ) {
	

	var proteinBarIndex = params.proteinBarIndex;
	var proteinId = params.proteinId;
	var mousePositionX = params.mousePositionX; 
	var rectangleOffsetLeft = params.rectangleOffsetLeft;

	var mouseOffsetFromRectangleLeft = mousePositionX - rectangleOffsetLeft;
	
	var multiplier = getMultiplier();
	
	var sequencePositionOneBased = Math.round( ( mouseOffsetFromRectangleLeft / multiplier ) - 0.5 ) + 1;

	if ( isProteinReversed( { proteinId : proteinId, proteinBarIndex : proteinBarIndex } ) ) {

		var proteinSequence = _proteinSequences[ proteinId ];
		var proteinSequenceLength  = proteinSequence.length;

		sequencePositionOneBased = proteinSequenceLength - sequencePositionOneBased + 1;
	}

	var imageProteinBarDataEntry = _imageProteinBarDataManager.getItemByIndex( { arrayIndexInt : proteinBarIndex } );
	
	imageProteinBarDataEntry.removeProteinBarHighlightedRegion( { position : sequencePositionOneBased } );
	
	drawSvg();
}



// reset/clear highlighted proteins
function clearHighlightedProteins() {
	
//	_highlightedProteins = [ ];
	
	_imageProteinBarDataManager.clearAllProteinBarsHighlighted();
}

// whether or not the supplied selected protein index is currently highlighted
function isHighlightedProtein( i ) {

	var imageProteinBarData =  _imageProteinBarDataManager.getItemByIndex( { arrayIndexInt : i } );

	if ( imageProteinBarData.isAllOfProteinBarHighlighted()  ) {
		
		return true;
	}
	
	return false;
}

// returns a list of searches for the given link
function findSearchesForMonolink( protein, position ) {
	
	return _proteinMonolinkPositions[ protein ][ position ];
}

//returns a list of searches for the given link
function findSearchesForLooplink( protein, position1, position2 ) {
	
	return _proteinLooplinkPositions[ protein ][protein][ position1 ][ position2 ];
}

//returns a list of searches for the given link
function findSearchesForCrosslink( protein1, protein2, position1, position2 ) {	
	
	return _proteinLinkPositions[ protein1 ][ protein2 ][ position1 ][ position2 ];
}





/////////////////////////////////////////////////////////

///  Actually draw the image



function drawSvg() {
	_circlePlotViewer.draw();
}

/////////////////////////////////////////


//   Protein Select


function populateSelectProteinSelect( ) {
	

	var $protein_list_container = $("#protein_list_container");
	
	$protein_list_container.empty();
	
	var $select_protein_overlay_protein_entry_template = $("#select_protein_overlay_protein_entry_template");
	

	
	var proteinEntry_template_handlebarsSource = $select_protein_overlay_protein_entry_template.text();

	if ( proteinEntry_template_handlebarsSource === undefined ) {
		throw "proteinEntry_template_handlebarsSource === undefined";
	}
	if ( proteinEntry_template_handlebarsSource === null ) {
		throw "proteinEntry_template_handlebarsSource === null";
	}
	
	var proteinEntry_HandlebarsTemplate = Handlebars.compile( proteinEntry_template_handlebarsSource );

	for ( var i = 0; i < _proteins.length; i++ ) {
		
		var proteinId = _proteins[ i ];
		
		var proteinName = getProteinName( proteinId );

		var proteinLinkValue = { proteinId : proteinId, proteinName : proteinName };
		
		//  Use Handlebars libary to convert the template into HTML, performing substitutions using proteinLinkValue
		
		var proteinLinkHTML = proteinEntry_HandlebarsTemplate( proteinLinkValue );
		
		var $newEntry = 
			$( proteinLinkHTML ).appendTo( $protein_list_container );
		
		//  Add click handler for changing the protein
		
		$newEntry.click( function( eventObject ) {
			
			processClickOnSelectProtein( { clickedThis : this } );
		} );
		
		//  Add Protein Name Tool Tip
		
		$newEntry.each( function() {
			
			var $protein_select_jq = $( this );
			
			addSingleTooltipForProteinName( { 
				$elementToAddToolTipTo : $protein_select_jq, 
				proteinIdString : proteinId,
				proteinDisplayName : proteinName,
				tooltipDelay : 500  //  milliseconds
			} );
		} );
	}

}



function closeAddProteinSelect( params ) {
	
	var $select_protein_modal_dialog_overlay_background = $("#select_protein_modal_dialog_overlay_background");

	$select_protein_modal_dialog_overlay_background.hide();

	var $select_protein_overlay_div = $("#select_protein_overlay_div");

	$select_protein_overlay_div.hide();
	
}



function openSelectProteinSelect( params ) {
	
	if ( ! params ) {
		
		throw "params is empty";
	}
	
	var clickedThis = params.clickedThis;
	
	var addProteinsClicked = params.addProteinsClicked;
	
	if ( ! clickedThis ) {
		
		throw "clickedThis is empty";
	}
	
	if ( addProteinsClicked === undefined ) {
		
		addProteinsClicked = false;
	}

	var $clickedThis = $( clickedThis );

	$clickedThis.qtip('toggle', false); // Immediately hide all tooltips belonging to the selected elements
	
	var $tool_tip_attached_jq_Parent = $clickedThis.closest(".tool_tip_attached_jq");

	$tool_tip_attached_jq_Parent.qtip('toggle', false); // Immediately hide all tooltips belonging to the selected elements


	var $tool_tip_attached_jq_Children = $clickedThis.find(".tool_tip_attached_jq");

	$tool_tip_attached_jq_Children.qtip('toggle', false); // Immediately hide all tooltips belonging to the selected elements

	var $protein_select_text_container_jq = $clickedThis.find(".protein_select_text_container_jq");

	$protein_select_text_container_jq.qtip('toggle', false); // Immediately hide all tooltips belonging to the selected elements
	
	var positionIndexSelectedProtein = $clickedThis.attr("data-position_index");
	var proteinIdSelectedProtein = $clickedThis.attr("data-protein_id");
	
	
	if ( positionIndexSelectedProtein === undefined ) {
		positionIndexSelectedProtein = null;
	}

	if ( proteinIdSelectedProtein === undefined ) {
		proteinIdSelectedProtein = null;
	}
	
	
	
	if ( addProteinsClicked ) {
		
		//  "Add Protein" button clicked
		
		positionIndexSelectedProtein = null;
		proteinIdSelectedProtein = null;
		
		$("#select_protein_add_proteins_overlay_header_text").show();
		$("#select_protein_change_protein_overlay_header_text").hide();

		$("#select_protein_add_proteins_overlay_body_text").show();
		$("#select_protein_change_protein_overlay_body_text").hide();
		
		$("#select_protein_add_proteins_overlay_add_button_container").show();

	} else {
		
		//  An existing protein is clicked
		
		$("#select_protein_change_protein_overlay_header_text").show();
		$("#select_protein_add_proteins_overlay_header_text").hide();
		
		$("#select_protein_change_protein_overlay_body_text").show();
		$("#select_protein_add_proteins_overlay_body_text").hide();
	
		$("#select_protein_add_proteins_overlay_add_button_container").hide();
		
	}
	
	

	$("#select_protein_modal_dialog_overlay_background").show();

	var $select_protein_overlay_div = $("#select_protein_overlay_div");
	
	$select_protein_overlay_div.show();
	
	
	var $protein_list_container = $("#protein_list_container");
	
	//  Save off values from clicked Selected Protein.  Will be null for Add Protein
	
	$protein_list_container.data( { 
		positionIndex: positionIndexSelectedProtein, 
		proteinIdSelectedProtein : proteinIdSelectedProtein,
		addProteinsClicked : addProteinsClicked } );
	
	//  Highlight the currently selected protein
	
	var $protein_select_jq_Items = $protein_list_container.find(".protein_select_jq");
	

	//  Clean up to remove all highlighted protein names
	
	$protein_select_jq_Items.removeClass("single-protein-select-item-highlight");
	
	$protein_select_jq_Items.removeClass( _PROTEIN_OVERLAY_PROTEIN_SELECTED );
	
	
	if ( proteinIdSelectedProtein !== undefined && proteinIdSelectedProtein !== null ) {
		
		$protein_select_jq_Items.each( function( index, element ) {
			
			var $protein_select_jq = $( this );
			
			var element_protein_id =  $protein_select_jq.attr("data-protein_id");
			
			if ( element_protein_id === proteinIdSelectedProtein ) {
				
				$protein_select_jq.addClass("single-protein-select-item-highlight");
				
				//  Scroll to highlighted protein
				
				try {

					var protein_select_jq_PositionTop = $protein_select_jq.position().top;

					$protein_list_container.scrollTop( protein_select_jq_PositionTop - 20 );
				
				} catch ( e ) {
					
					
				}
				
//				return false;  //  Exit .each(...) processing
			}
		} );
		
	} else {
		
		//  Position at top for add protein
		
		$protein_list_container.scrollTop( 0 );
	}
	
	
	markInvalidProteinsInProteinSelector( { positionIndexSelectedProtein : positionIndexSelectedProtein } );

	
	//  Position and Set Size of overlay

	var OVERLAY_STANDARD_WIDTH = 400;
	var OVERLAY_MINIMUM_WIDTH = 300;
	
	var OVERLAY_MINIMUM_HEIGTH = 150;
	
	var OVERLAY_MINIMUM_LEFT_BUFFER = 5;
	var OVERLAY_MINIMUM_RIGHT_BUFFER = 5;
	
	
	var $window = $( window );
	
	var windowScrollTop = $window.scrollTop();
	
	var windowScrollLeft = $window.scrollLeft();

	var viewportHeight = $window.height();
	var viewportWidth = $window.width();
	
	
	var leftEdgeOfPageinViewport = windowScrollLeft;
	var rightEdgeOfPageinViewport = windowScrollLeft + viewportWidth;
	
	//  Click Item position
	
	var clickedItemOffset = $clickedThis.offset();
	var clickedItemOffsetLeft = clickedItemOffset.left;
//	var clickedItemOffsetTop = clickedItemOffset.top;
	
	var overlayNewOffsetLeft = clickedItemOffsetLeft - 10;
	
	
	
	
	//  Position Overlay horizontally and set width
	
	var overlayNewWidth = OVERLAY_STANDARD_WIDTH;
	
	if ( overlayNewOffsetLeft < leftEdgeOfPageinViewport + OVERLAY_MINIMUM_LEFT_BUFFER ) {
		
		overlayNewOffsetLeft = leftEdgeOfPageinViewport + OVERLAY_MINIMUM_LEFT_BUFFER;
	}
	
	if ( ( overlayNewOffsetLeft + overlayNewWidth + OVERLAY_MINIMUM_RIGHT_BUFFER ) > ( rightEdgeOfPageinViewport ) ) {
		
		// overlay at overlayNewOffsetLeft and overlayNewWidth will overflow right edge
		
		if ( overlayNewWidth + leftEdgeOfPageinViewport + OVERLAY_MINIMUM_LEFT_BUFFER + OVERLAY_MINIMUM_RIGHT_BUFFER < rightEdgeOfPageinViewport ) {

			//  Can move overlay left
			
			overlayNewOffsetLeft = 
				rightEdgeOfPageinViewport - ( overlayNewWidth + OVERLAY_MINIMUM_LEFT_BUFFER + OVERLAY_MINIMUM_RIGHT_BUFFER );
			
		} else {
			
			//  Must move overlay left and shrink it
			
			overlayNewOffsetLeft = leftEdgeOfPageinViewport + OVERLAY_MINIMUM_LEFT_BUFFER;
			
			overlayNewWidth = rightEdgeOfPageinViewport - ( leftEdgeOfPageinViewport + OVERLAY_MINIMUM_LEFT_BUFFER + OVERLAY_MINIMUM_RIGHT_BUFFER );
			
			if ( overlayNewWidth < OVERLAY_MINIMUM_WIDTH ) {
				
				overlayNewWidth = OVERLAY_MINIMUM_WIDTH;
			}
			
		}
	}
	
	//   Set overlay height to viewport - 40px or at minimum height

	var overlayHeight = viewportHeight - 40;
	
	var current_select_protein_overlay_div_Heigth = $select_protein_overlay_div.height();

	var overlayHeightDiff = overlayHeight - current_select_protein_overlay_div_Heigth;
	
	var current_protein_list_container_Height = $protein_list_container.height();
	
	var new_current_protein_list_container_Height = current_protein_list_container_Height + overlayHeightDiff;
	
	if ( new_current_protein_list_container_Height < OVERLAY_MINIMUM_HEIGTH ) {
		
		new_current_protein_list_container_Height = OVERLAY_MINIMUM_HEIGTH ;
	}
	
	
	//  Position Overlay Vertically

	var overlayNewTop = windowScrollTop + 10;

	//  Apply position to overlay
	
	$select_protein_overlay_div.css( { 
		left : overlayNewOffsetLeft + "px", 
		top : overlayNewTop + "px",
		width : overlayNewWidth + "px" } );
	
	$protein_list_container.css( { height : new_current_protein_list_container_Height + "px" } ); 
}



function processClickOnSelectProtein( params ) {
	
	var clickedThis = params.clickedThis; //  HTML <div> object for clicked protein name
	
	var $clickedThis = $( clickedThis );
	

	var $protein_list_container = $("#protein_list_container");
	
	var protein_list_container_Data = $protein_list_container.data();
	
	var positionIndex = protein_list_container_Data.positionIndex;
	var proteinIdSelectedProtein = protein_list_container_Data.proteinIdSelectedProtein;
	
	var addProteinsClicked = protein_list_container_Data.addProteinsClicked;
	
	
	var selectedProteinId = $clickedThis.attr("data-protein_id");
	
	if ( selectedProteinId === proteinIdSelectedProtein ) {
		
		//  No protein id change so just close the overlay
		
		closeAddProteinSelect();
		
		return;
	}
	
	if ( addProteinsClicked ) {
		
		//  Adding proteins so mark this protein as selected, unless it is selected and then mark as unselected
		
		if ( $clickedThis.hasClass( _PROTEIN_OVERLAY_PROTEIN_SELECTED ) ) {
			
			 $clickedThis.removeClass( _PROTEIN_OVERLAY_PROTEIN_SELECTED );
		} else {
			
			 $clickedThis.addClass( _PROTEIN_OVERLAY_PROTEIN_SELECTED );
		}
		
		markInvalidProteinsInProteinSelector( { positionIndexSelectedProtein : positionIndex } );
		
	} else {

		//  Update an existing entry using the selected protein id

		var positionIndexInt = parseInt( positionIndex, 10 );

		if ( isNaN( positionIndexInt ) ) {

			throw "positionIndex is not an integer,  positionIndex: '" + positionIndex + "'";
		}

		var imageProteinBarDataItem = _imageProteinBarDataManager.getItemByIndex( { arrayIndexInt : positionIndexInt } );
		
		imageProteinBarDataItem.setProteinId( { proteinId : selectedProteinId } );
		

		closeAddProteinSelect();
		
		updateURLHash( false /* useSearchForm */ );
		
		showSelectedProteins();
		
		loadDataAndDraw( true /* doDraw */ );
	}
	
}


function processClickOnAddProteins( ) {
	

	//  Add one or more new protein bar entries
	
	var $selectedEntries = $("#protein_list_container ." + _PROTEIN_OVERLAY_PROTEIN_SELECTED );
	
	$selectedEntries.each( function( index, element ) {
	
		var $this = $( this );
		
		var selectedProteinId = $this.attr("data-protein_id");
		
		var newImageProteinBarData = ImageProteinBarData.constructEmptyImageProteinBarData();
		
		newImageProteinBarData.setProteinId( { proteinId : selectedProteinId } );
		
		var proteinLength = _proteinLengths[ selectedProteinId ];
		
		newImageProteinBarData.setProteinLength( { proteinLength : proteinLength } );
		
		_imageProteinBarDataManager.addEntry( { entry : newImageProteinBarData } );
		
	} );
	
	closeAddProteinSelect();
	
	updateURLHash( false /* useSearchForm */ );
	
	showSelectedProteins();
	
	loadDataAndDraw( true /* doDraw */ );
	
}




// get an array of all the currently-selected proteins, can contain same
// protein multiple times
function getAllSelectedProteins() {

	var imageProteinBarDataItems = _imageProteinBarDataManager.getAllItems();
	

	var proteinsLocalVar = [];
	
	for ( var imageProteinBarDataItemsIndex = 0; imageProteinBarDataItemsIndex < imageProteinBarDataItems.length; imageProteinBarDataItemsIndex ++ ) {

		var imageProteinBarDataEntry = imageProteinBarDataItems[ imageProteinBarDataItemsIndex ];

		var imageProteinBarDataEntryProteinId = imageProteinBarDataEntry.getProteinId();

		proteinsLocalVar.push( imageProteinBarDataEntryProteinId );
	}
	
	return proteinsLocalVar;
	
}




//get an array of all the currently-selected proteins, can contain same
//protein multiple times
function getSelectedProteinsExcludingPositionIndex( params ) {
	
	var positionIndex = params.positionIndex;

	var imageProteinBarDataItems = _imageProteinBarDataManager.getAllItems();
	

	var proteinsLocalVar = [];
	
	for ( var imageProteinBarDataItemsIndex = 0; imageProteinBarDataItemsIndex < imageProteinBarDataItems.length; imageProteinBarDataItemsIndex ++ ) {

		if ( imageProteinBarDataItemsIndex === positionIndex ) {
			
			//   Skip provided positionIndex
			
			continue;  //  EARLY continue
		}
		
		var imageProteinBarDataEntry = imageProteinBarDataItems[ imageProteinBarDataItemsIndex ];

		var imageProteinBarDataEntryProteinId = imageProteinBarDataEntry.getProteinId();

		proteinsLocalVar.push( imageProteinBarDataEntryProteinId );
	}
	
	return proteinsLocalVar;
	
}


function markInvalidProteinsInProteinSelector( params ) {
	
	var positionIndexSelectedProtein = params.positionIndexSelectedProtein;

	//  validProteins contains array of protein ids that the selected protein ids link to, 
	//    excluding the protein id at position positionIndexSelectedProtein ( null or undefined for add )
	
	var validProteins = getValidProteins( { positionIndexSelectedProtein : positionIndexSelectedProtein } );

	var $protein_list_container = $("#protein_list_container");
	
	var $protein_select_jq_Items = $protein_list_container.find(".protein_select_jq");
	
	$protein_select_jq_Items.removeClass("select-protein-overlay-dimmed-protein-select-text");
	
	$protein_select_jq_Items.each( function() {
		
		var $protein_select_jq = $( this );
		
		var protein_select_jq_protein_id = $protein_select_jq.attr("data-protein_id");

		if ( $( "input#show-crosslinks" ).is( ':checked' ) 
				&& $.inArray( protein_select_jq_protein_id, validProteins ) == -1 ) {
			
			$(this).addClass("select-protein-overlay-dimmed-protein-select-text");
		}
	});
}


// get an array of "valid" proteins--proteins with crosslinks with
// any of the currently-selected proteins
function getValidProteins( params ) {
	
	var positionIndexSelectedProtein = params.positionIndexSelectedProtein;
	
	///  Producing a list of protein ids that the other selected protein ids link to
	
	if ( _proteinLinkPositions == undefined ) {
		
		//  If _proteinLinkPositions == undefined, return all protein ids
		
		return _proteins;
	}
	
	var selectedProteins = null;
	
	if ( positionIndexSelectedProtein !== undefined && positionIndexSelectedProtein !== null ) {

		//  Get selected proteins, excluding current selector value

		selectedProteins = getSelectedProteinsExcludingPositionIndex( { positionIndex : positionIndexSelectedProtein } );
			
	} else {
		
		//  Get all proteins since no current selector
		selectedProteins = getAllSelectedProteins();
	}
	
	//  Add in proteins already selected in the protein selection overlay
		
	var $selectedEntries = $("#protein_list_container ." + _PROTEIN_OVERLAY_PROTEIN_SELECTED );

	$selectedEntries.each( function( index, element ) {

		var $this = $( this );

		var selectedProteinId = $this.attr("data-protein_id");

		if ( $.inArray( selectedProteinId, selectedProteins ) == -1 ) {
		
			selectedProteins.push( selectedProteinId );
		}
	
	} );	
	
	var vProteins = new Array();

	if ( selectedProteins.length == 0 ) { 
		
		// If there no selected proteins other than current select, return all possible crosslink proteins
		
		return Object.keys( _proteinLinkPositions ); 
	}

	for ( var i = 0; i < selectedProteins.length; i++ ) {

		//  for each selected protein from other than current selector
		
		var selectedProteinId = selectedProteins[ i ];
		
		if ( _proteinLinkPositions[ selectedProteinId ] == undefined ) {
			
			//  No linkable protein positions so continue to next selected protein id
			
			continue; 
		}
		
		//  Get an array of the protein ids that the selected protein id links to
		
		var partners = Object.keys( _proteinLinkPositions[ selectedProteinId ]  );

		for ( var k = 0; k < partners.length; k++ ) {
			if ( $.inArray( partners[ k ], vProteins ) == -1 ) {
				
				//  The linked to protein id is not already in the output array so add it 
				
				vProteins.push( partners[ k ] );
			}
		}
	}

	return vProteins;
}

/////////////////////////////

function downloadSvg( params ) {
	
	var clickedThis = params.clickedThis;
	
	var $clickedThis = $( clickedThis );
	
	var getSVGContentsAsStringResult = getSVGContentsAsString();
	
	if ( getSVGContentsAsStringResult.noPageElement ) {
		
		return;
	}

	if ( getSVGContentsAsStringResult.error ) {
		
		//  TODO Display ERROR Message
		
		//  Cannot get SVG contents as string so disable "Download SVG" link

		$( "#download_svg_link" ).hide();
		$( "#download_svg_not_supported" ).show();
		
		return;
	}
	 
	var fullSVG_String = getSVGContentsAsStringResult.fullSVG_String;
		
	var svgBase64Ecoded = Base64.encode( fullSVG_String );
	var hrefString = "data:application/svg+xml;base64," + svgBase64Ecoded;

	var dateForFilename = new Date().toISOString().slice(0, 19).replace(/-/g, "");

	var downloadFilename = "crosslinks-" + dateForFilename + ".svg";

	$clickedThis.attr("href", hrefString ).attr("download", downloadFilename );

	
	//  OLD
	
//	var uriContent = "data:application/svg+xml," + encodeURIComponent( "<svg id=\"svg\">" + $( "svg#svg" ).html() +"</svg>");
//	var myWindow = window.open(uriContent, "crosslink.svg");
//	myWindow.focus();
}


function getSVGContentsAsString() {

	try {

		var $svg_image_inner_container_div__svg_merged_image_svg_jq = $( "#svg_image_inner_container_div svg.merged_image_svg_jq " );

		if ( $svg_image_inner_container_div__svg_merged_image_svg_jq.length === 0 ) {

			//  No SVG element found

			return { noPageElement : true };   //  EARLY EXIT
		}

		// In Edge, svgElement.innerHTML === undefined    This causes .html() to fail with exception
		
		//  svgContents contains the inner contents of the <svg> element
		
		var svgContents = $svg_image_inner_container_div__svg_merged_image_svg_jq.html();
		
		var fullSVG_String = "<svg id=\"svg\">" + svgContents +"</svg>";
		
		return { fullSVG_String : fullSVG_String};

	} catch( e ) {
		
		//  Not all browsers have svgElement.innerHTML which .html() tries to use, causing an exception
		
		return { error : true };
	}
}


///////////////////////////

var viewerInitialized = false;

function initializeViewer()  {

	showSelectedProteins();
	
	if ( viewerInitialized ) { 
		return; 
	}
	
	viewerInitialized = true;
	
	populateViewerCheckBoxes();
	
	$( "input#show-crosslinks" ).change( function() {
		updateURLHash( false /* useSearchForm */ );
		loadDataAndDraw( true /* doDraw */ );
	});
	
	$( "input#show-self-crosslinks" ).change( function() {
		updateURLHash( false /* useSearchForm */ );
		loadDataAndDraw( true /* doDraw */ );
	});

	$( "input#show-looplinks" ).change( function() {
		updateURLHash( false /* useSearchForm */ );
		loadDataAndDraw( true /* doDraw */ );
	});

	$( "input#show-monolinks" ).change( function() {
		updateURLHash( false /* useSearchForm */ );
		loadDataAndDraw( true /* doDraw */ );
	});

	$( "input#show-linkable-positions" ).change( function() {
		updateURLHash( false /* useSearchForm */ );
		drawSvg();
	});

	$( "input#show-tryptic-cleavage-positions" ).change( function() {
		updateURLHash( false /* useSearchForm */ );
		drawSvg();
	});
	
	$( "input#show-scalebar" ).change( function() {
		updateURLHash( false /* useSearchForm */ );
		drawSvg();
	});
	

	$( "#color_by" ).change( function() {
		updateURLHash( false /* useSearchForm */ );
		drawSvg();
	});
	

	$( "input#shade-by-counts" ).change( function() {
		updateURLHash( false /* useSearchForm */ );
		loadDataAndDraw( true /* doDraw */ );
	});

	
	$( "#annotation_type" ).change( function() {
		updateURLHash( false /* useSearchForm */ );
		loadDataAndDraw( true /* doDraw */ );
	});
	
	
	$( "input#automatic-sizing" ).change( function() {
		
		var horizontal_scaling_slider_Value = $("#horizontal_scaling_slider_div").slider( "value" );
		
		_userScaleFactor = _computedStandardMultiplier * ( horizontal_scaling_slider_Value / 100 );

		handleScaleFactorVisibility();
		updateURLHash( false /* useSearchForm */ );
	});
	
	$( "input#scale-factor-button" ).click( function() {
		submitUserScaleFactor();
	});
	
	
	$( "input#protein_names_position_left" ).change( function() {
		updateURLHash( false /* useSearchForm */ );
		drawSvg();
	});
	
	$( "input#show-protein-termini" ).change( function() {
		updateURLHash( false /* useSearchForm */ );
		drawSvg();
	});

	$( "#download_svg_link" ).click( function() {
		
		downloadSvg( { clickedThis : this } );
	});
	
	
	$("#run_pgm_annotation_data_overlay_cancel_button").click( function() { 

		
		proteinAnnotationStore.cancelCheckForComplete();

		decrementSpinner();

		$("#run_pgm_annotation_data_modal_dialog_overlay_background").hide();
		
		$("#run_pgm_annotation_data_overlay_div").hide();
		
		$("#annotation_type").val("");
		updateURLHash( false /* useSearchForm */ );


		loadDataAndDraw( true /* doDraw */ );
	});
	
	
	
	
	$("#pgm_failed_annotation_data_overlay_annotation_type_button").click( function() { 

		$("#pgm_failed_annotation_data_modal_dialog_overlay_background").hide();

		$("#pgm_failed_annotation_data_overlay_div").hide();


//		$("#annotation_type").val("");
//		updateURLHash( false /* useSearchForm */ );

		loadDataAndDraw( true /* doDraw */ );

	});


};


function isSizingAutomatic() {
	if ( $( "input#automatic-sizing" ).is( ':checked' ) ) {
		return true;
	}
	
	return false;
}

function handleScaleFactorVisibility( supressRedraw ) {
	
	if ( isSizingAutomatic() ) {
		$( "div#user-image-sizing-div" ).hide();
	} else {
		
//		if ( _userScaleFactor == undefined ) {
//			setUserScaleFactorAndRedraw( ( getMultiplier( true /* ignoreUserScale */ ) ).toFixed( 1 ) );
//		}
		
		
		
		$( "div#user-image-sizing-div" ).show();
	}
	
	if ( supressRedraw == undefined || supressRedraw === false ) {
		drawSvg();
	}
}

//handle redrawing on resize
var delta = 200;
var _timerId;
$(window).resize(function() {

	
	if ( _timerId ) {
		//  Existing timeout so clear it
		clearTimeout( _timerId );
	}
	
	//  create new timeout
	_timerId = setTimeout(function() {
			
		//  clear the timerId
		_timerId = null;
		
		drawSvg();
			
	}, delta);
	
});





function getProteinName( proteinId ) {
	if ( _proteinNames[ proteinId ] === undefined ) {
		return "name not found";
	}

	return _proteinNames[ proteinId ];
}


//////////////////////////////////////////////////

//  Initialize the page and load the data

function initPage() {


	console.log( "Initializing the page." );
	
	proteinAnnotationStore.init();
	
	
	//  Set up nag overlay for text for Merged Image page
	
	$("#nag_update_button_other_pages").hide();
	$("#nag_update_button_merged_image_page").show();
	
	

	if ( Modernizr && ! Modernizr.svg ) {  //  Modernizr.svg is false if SVG not supported

		console.log( "SVG not supported." );
		

		throw "SVG not supported";
	}

	
	_searchIds = [];
	
	var $search_id_jq = $(".search_id_jq");
	
	if ( $search_id_jq.length === 0 ) {
		
		throw "Must be at least one search id in hidden field with class 'search_id_jq'";
	}
	
	$search_id_jq.each( function( index, element ) {
		
		var $search_id_jq_single = $( this );
		
		var searchIdString = $search_id_jq_single.val();
		
		var searchId = parseInt( searchIdString, 10 );
		
		if ( isNaN( searchId ) ) {
			
			throw "Search Id is not a number: " + searchIdString;
		}
		
		
		_searchIds.push( searchId );
	});
	

	var json = getJsonFromHash();

	if ( json === null ) {

		$("#invalid_url_no_data_after_hash_div").show();


		throw "Invalid URL, no data after the hash '#'";
	}

	
	if ( _searchIds && _searchIds.length > 1 ) {
		
		$("#merged_label_text_header").show();  //  Update text at top to show this is for "merged" since more than one search
	}

	attachViewLinkInfoOverlayClickHandlers();
	
	
	var proteinBarToolTip_template_handlebarsSource = $( "#protein_bar_tool_tip_template" ).text();

	if ( proteinBarToolTip_template_handlebarsSource === undefined ) {
		throw "proteinBarToolTip_template_handlebarsSource === undefined";
	}
	if ( proteinBarToolTip_template_handlebarsSource === null ) {
		throw "proteinBarToolTip_template_handlebarsSource === null";
	}
	
	_proteinBarToolTip_template_HandlebarsTemplate = Handlebars.compile( proteinBarToolTip_template_handlebarsSource );

	
	$( "input#filterNonUniquePeptides" ).change(function() {
		
		defaultPageView.searchFormChanged_ForDefaultPageView();
	});
	$( "input#filterOnlyOnePSM" ).change(function() {
		
		defaultPageView.searchFormChanged_ForDefaultPageView();
	});
	$( "input#filterOnlyOnePeptide" ).change(function() {
		
		defaultPageView.searchFormChanged_ForDefaultPageView();
	});		

	$("#exclude_protein_types_block").find("input").change(function() {
		
		defaultPageView.searchFormChanged_ForDefaultPageView();
	});
	
	//  select_protein_modal_dialog_overlay_background
	
	$("#select_protein_overlay_X_for_exit_overlay").click( function( eventObject ) {
		closeAddProteinSelect();
	} );
	
	
	$("#select_protein_add_proteins_overlay_add_button").click( function( eventObject ) {
		processClickOnAddProteins();
	} );
	
	
	
	_proteinBarRegionSelectionsOverlayCode.init();
	

	loadDataFromService();
};



$(document).ready(function()  { 
	initPage();
});

///////////

//   Object for passing to other objects

var imageViewerPageObject = {

		getQueryJSONString : function() {

//			var queryJSON = getNavigationJSON_Not_for_Image_Or_Structure();
			
			var queryJSON = getJsonFromHash();

			var queryJSONString = JSON.stringify( queryJSON );

			return queryJSONString;
		}
};




