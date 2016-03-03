
//  crosslink-image-viewer.js

//  Javascript for the viewMergedImage.jsp page

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";


//  The actual svg image is drawn in the function drawSvg()



//   A plain protein is the protein id


//    Snap.svg  Info

//        every object created has a "node" property which is the raw SVG element - http://snapsvg.io/docs/#Element.node


//  Example Text entry from Mason   

//     The "dy" value of ".35em" makes it so the "x" and "y" refer to the upper left corner instead of the text baseline,
//     making it easier to line text up with shapes. 

// <text id="SvgjsText1582" style="font-size:12px;font-family:Helvetica, Arial, sans-serif" data-row-rowIdx="0" dy=".35em" x="18" y="9.5">
//	<tspan id="SvgjsTspan1583" dy=".35em" x="18" style="font-size:12px;font-family:Helvetica, Arial, sans-serif">Coiled-coil</tspan>
//  </text>



///////////////   CONSTANTS  ////////////////////////


//  Default exclude link type "No Links"

var EXCLUDE_LINK_TYPE_DEFAULT = [ 0 ];



//////  Code flow constants

//   These turn on and off certain features under consideration

//  Not currently used:
//	  This has been replaced with a tool tip in a floating div 
//  	This is for showing info embedded into the main SVG image.
// var _SHOW_SEQUENCE_POSITION_TOOLTIP_ON_PROTEIN_BARS = false;
// var _SHOW_SEQUENCE_POSITION_TOOLTIP_ON_PROTEIN_BARS = true;


//  Matches the "value" for the <select id="annotation_type">

var SELECT_ELEMENT_ANNOTATION_TYPE_SEQUENCE_COVERAGE = "sequence_coverage";
//var SELECT_ELEMENT_ANNOTATION_TYPE_DISOPRED2 = "disopred2";
var SELECT_ELEMENT_ANNOTATION_TYPE_DISOPRED3 = "disopred3";
var SELECT_ELEMENT_ANNOTATION_TYPE_PSIPRED3 = "psired3";



//////////////////////////

//  Visible Constants

var _PROTEIN_TERMINUS_LABEL_C = "C";
var _PROTEIN_TERMINUS_LABEL_N = "N";

 ////  Color constants


var _MOUSE_POSITION_ON_PROTEIN_BAR_COLOR = "yellow";

// get color for protein bar 
//  if no highlighted proteins or this protein bar index is highlighted, use this color
		
var _PROTEIN_BAR_COLOR_MAIN_AND_HIGHLIGHTED = "#000000";
	
//if there are highlighted proteins and this protein bar index is NOT highlighted, use this color
var _PROTEIN_BAR_COLOR_NOT_HIGHLIGHTED = "#606060";


var _NOT_HIGHLIGHTED_LINE_COLOR = "#868686";


var _LINE_COLORS = [
                  "#FF0000",	// red
                  "#006600",	// green
                  "#0000FF",	// blue
                  "#FF00FF",	// magenta
                  "#FF6600",	// orange
                  "#289897",	// dark cyan
                  "#8a51ff",	// purple
                  "#878906",	// mustard yellow
                  ];

// colors to use for search-based coloring, based on 
// RYB subractive color model

var _SEARCH_COLORS = {
					1:		"#FF0000",			// red, for items belonging only to first search
					2:		"#0000FF",			// blue, for items belonging only to second search
					3:		"#dcd900",			// mustard yellow, for items belonging only to third search
					12:		"#8a51ff",			// purple, for items belonging to first and second search
					13:		"#FF6600",			// orange, for items belonging to first and third search
					23:		"#006600",			// green, for items belonging to second and third search
					123:	"#000000",			// black, for items belonging to all three searches
};

var _SEARCH_COLORS_TWO_SEARCHES = {
					1:		"#FF0000",
					2:		"#0000FF",
					12:		"#00FF00",
};



var _MAX_NUMBER_SEARCHES_SUPPORTED_FOR_COLOR_BY_SEARCHES = 3;  // driven by the values in the variable _SEARCH_COLORS above



//  height and width for when initially attach to <svg> element 

var _DEFAULT_VIEWPORT_HEIGHT = 1;
var _DEFAULT_VIEWPORT_WIDTH = 1;


//////  Overall image outside edge  constants:

//  Left and Right constants

var _PADDING_OVERALL_IMAGE_LEFT_SIDE = 3;
var _PADDING_OVERALL_IMAGE_RIGHT_SIDE = 3;

var _BORDER_OVERALL_IMAGE_ON_ONE_SIDE = 0;
// var _BORDER_OVERALL_IMAGE_ON_ONE_SIDE = 1; //  Use when put border on <svg> element

// var _MAX_WIDTH_EXTRA_SUBTRACT_VALUE = -3;
// var _MAX_WIDTH_EXTRA_SUBTRACT_VALUE = 0;
// var _MAX_WIDTH_EXTRA_SUBTRACT_VALUE = 10;
 var _MAX_WIDTH_EXTRA_SUBTRACT_VALUE = 20; 

 
 //  Vertical constants
 
var _OFFSET_FROM_TOP = 25;  //  This number is to the top of the first protein bar.  Space is needed for the links above the bar. 

///////////////

/////  Positioning constants


var _PROTEIN_BAR_PROTEIN_NAME_GAP_WHEN_PROTEIN_NAME_TO_THE_LEFT = 5;

//  Position from protein bar for "C" and "N" Terminus Labels

var _PROTEIN_TERMINUS_BAR_HORIZONTAL_OFFSET = 3; 
var _PROTEIN_TERMINUS_BAR_VERTICAL_OFFSET = 15;

var _SINGLE_PROTEIN_BAR_OVERALL_HEIGHT_DEFAULT = 80;


var _SINGLE_PROTEIN_BAR_HEIGHT = 25;	

var _COLOR_BY_SEARCH_OUTER_WIDTH = 140;


var _MIN_SPACE_BETWEEN_TICK_MARKS = 50;  // minimum horizontal space in pixels
var _POTENTIAL_TICK_INCREMENTS = [ 1, 5, 10, 20, 50, 100, 200, 250, 500, 1000 ];



///  Function Parameter constants

var _MAKE_ARC_PATH_DIRECTION_UP = "up";
var _MAKE_ARC_PATH_DIRECTION_DOWN = "down";


var _TRANSLATE_SEQUENCE_COVERAGE_POSITION_TO_X_COORDINATES_SIDE_START = "start";
var _TRANSLATE_SEQUENCE_COVERAGE_POSITION_TO_X_COORDINATES_SIDE_END = "end";


////  JSON Hash property names and values

var PROTEIN_NAMES_POSITION_HASH_JSON_PROPERTY_NAME = "protein_names_position";

var PROTEIN_NAMES_POSITION_LEFT = "left";



///   CSS Class name constants for jQuery search

var _PROTEIN_BAR_OVERLAY_RECTANGLE_LABEL_CLASS = "protein_bar_overlay_rectangle_jq";

var _PROTEIN_BAR_PROTEIN_NAME_LABEL_CLASS = "protein_bar_protein_name_jq";



/////////////////////////////////////////////////////////////////////////

///////////////    GLOBAL VARIABLES  ////////////////////////////////////

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


//  working data (does round trip to the JSON in the Hash in the URL)

var _highlightedProteins = [];
var _userScaleFactor;
var _proteinOffsets = {};
var _proteinsReversed = {};

var _singleProteinBarOverallHeight = _SINGLE_PROTEIN_BAR_OVERALL_HEIGHT_DEFAULT;

var _horizontalScalingPercent = 100;

//  Working data, does not go to JSON

var _proteinBarsLeftEdge = _PADDING_OVERALL_IMAGE_LEFT_SIDE;

var _proteinBarToolTip_template_HandlebarsTemplate = null;  ///  Compiled Handlebars template for the protein bar tool tip




//////////////////////////////////////////////////////////////////////////////

////////////////     CODE   ///////////////////////////////////



function get_MAX_WIDTH() {
	
//	_MAX_WIDTH = $(window).width() - 100;
	
	
	//  Tried computing width relative to the div it is inserted in that has width:100%
	//    This is a more accurate available width since it takes into account the left and right margins
	
	var $svg_image_outer_container_div = $("#svg_image_outer_container_div");
	var svg_image_outer_container_div__width = $svg_image_outer_container_div.width();

//	_MAX_WIDTH = svg_image_outer_container_div__width - 100;
	
	var maxWidth = 
		svg_image_outer_container_div__width - /* _PADDING_OVERALL_IMAGE_LEFT_SIDE */ _proteinBarsLeftEdge - _PADDING_OVERALL_IMAGE_RIGHT_SIDE - 
		( _BORDER_OVERALL_IMAGE_ON_ONE_SIDE * 2 ) - _MAX_WIDTH_EXTRA_SUBTRACT_VALUE;  
	
	return maxWidth;
}



var _computedMultiplier = 0;
var _computedStandardMultiplier = 0;

/*
 * Get the multiplier, which is the translation of residue position to pixel position relative to the
 * graphical beginning of the protein. E.g., position 1 translates to protein offset + 0
 */
function getMultiplier( ignoreUserScale ) {

	if ( ignoreUserScale || isSizingAutomatic() ) {
		
		return _computedMultiplier;
		
//		var maxProteinLength = 0;
//		var selectedProteins = getAllSelectedProteins();
//		
//		for ( var i = 0; i < selectedProteins.length; i++ ) {
//			
//			if ( _proteinLengths[ selectedProteins[ i ] ] > maxProteinLength ) { 
//			
//				maxProteinLength = _proteinLengths[ selectedProteins[ i ] ]; 
//			}
//		}
//		
//		return get_MAX_WIDTH() / ( maxProteinLength + 1 );
	}
	
	return _userScaleFactor;
}



//	
//	// given the length and positions of visible proteins, return the width that should be
//	// used to draw the viewer
//	function getCurrentViewportWidth() {
//		
//		var length = getCurrentRightmostProteinEdge();
//		var multiplier = getMultiplier();
//	
//		var ensureSpaceLastTickMark = 49 * multiplier;	// ensure there is room to draw the last tick of the scale bar
//		length += ensureSpaceLastTickMark;
//		
//		length += 20;				// a little buffer space on the edge for labels
//		
//		return length;
//	}

// get the right-most edge (in pixels) of the currently selected proteins
function getCurrentRightmostProteinEdge( ) {
	
	var prots = getAllSelectedProteins();
	var multiplier = getMultiplier();
	var rightmostEdge = 0;
	
	for ( var i = 0; i < prots.length; i++ ) {
		
		var protein = prots[ i ];
		
		var proteinOffsetFromLeftEdge = getProteinOffset( protein ) + _proteinBarsLeftEdge;
		var lengthOfProteinSequence = _proteinLengths[ protein ];
		var widthOfProtein = lengthOfProteinSequence * multiplier; 
		var rightEdgeOfProtein = proteinOffsetFromLeftEdge + widthOfProtein;
		
		if ( rightEdgeOfProtein > rightmostEdge ) { 
			rightmostEdge = rightEdgeOfProtein; 
		}
	}
	
	return rightmostEdge;
}



// get values for variables from the hash part of the URL as JSON
function getJsonFromHash() {
	
	var json;

	var windowHash = window.location.hash;
	
	if ( windowHash === "" || windowHash === "#" ) {
		
		//  Set cutoff defaults if not in JSON
			
		json = { 
				cutoffs : getCutoffDefaultsFromPage(),
				
				'excludeType' : EXCLUDE_LINK_TYPE_DEFAULT
		};
		
		return json;
		
//		return null;
	}
	
	var windowHashContentsMinusHashChar = windowHash.slice( 1 );
	
	try {
		
		// if this works, the hash contains native (non encoded) JSON
		json = JSON.parse( windowHashContentsMinusHashChar );
	} catch( e ) {
		
		// if we got here, the hash contained URI-encoded JSON, try decoding using decodeURI( windowHashContentsMinusHashChar )
		
		var windowHashContentsMinusHashCharDecodeURI = decodeURI( windowHashContentsMinusHashChar );
		
		try {
			json = JSON.parse( windowHashContentsMinusHashCharDecodeURI );
		} catch( e2 ) {
			

			// if we got here, the hash contained URI-encoded JSON, try decoding using decodeURIComponent( windowHashContentsMinusHashChar )
			
			var windowHashContentsMinusHashCharDecodeURIComponent = decodeURIComponent( windowHashContentsMinusHashChar );

			try {
				json = JSON.parse( windowHashContentsMinusHashCharDecodeURIComponent );
			} catch( e3 ) {
				

				throw "Failed to parse window hash string as JSON and decodeURI and then parse as JSON.  windowHashContentsMinusHashChar: " 
				+ windowHashContentsMinusHashChar;
			}
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

	var items = { };

	
	var getCutoffsFromThePageResult = cutoffProcessingCommonCode.getCutoffsFromThePage(  {  } );
	
	var getCutoffsFromThePageResult_FieldDataFailedValidation = getCutoffsFromThePageResult.getCutoffsFromThePageResult_FieldDataFailedValidation;
	
	if ( getCutoffsFromThePageResult_FieldDataFailedValidation ) {
		
		//  Cutoffs failed validation and error message was displayed
		
		//  EARLY EXIT from function
		
//		return { output_FieldDataFailedValidation : getCutoffsFromThePageResult_FieldDataFailedValidation };
		
		throw "Cutoffs are invalid so stop processing";
	}
	
	var outputCutoffs = getCutoffsFromThePageResult.cutoffsBySearchId;
	

	
	
	items[ 'cutoffs' ] = outputCutoffs;
	
	

	if ( $( "input#filterNonUniquePeptides" ).is( ':checked' ) )
		items[ 'filterNonUniquePeptides' ] = true;
	else
		items[ 'filterNonUniquePeptides' ] = false;

	if ( $( "input#filterOnlyOnePSM" ).is( ':checked' ) )
		items[ 'filterOnlyOnePSM' ] = true;
	else
		items[ 'filterOnlyOnePSM' ] = false;

	if ( $( "input#filterOnlyOnePeptide" ).is( ':checked' ) )
		items[ 'filterOnlyOnePeptide' ] = true;
	else
		items[ 'filterOnlyOnePeptide' ] = false;


	var xTax = new Array();
	var taxKeys = Object.keys( _taxonomies );

	for ( var taxKeysIndex = 0; taxKeysIndex < taxKeys.length; taxKeysIndex++ ) {

		var id = taxKeys[ taxKeysIndex ];

		if ( $( "input#exclude-taxonomy-" + id ).is( ':checked' ) ) {
			xTax.push( parseInt( id ) );
		}
	}

	items[ 'excludeTaxonomy' ] = xTax;		

	var xType = new Array();
	for ( var excludeTypeIndex = 0; excludeTypeIndex < 5; excludeTypeIndex++ ) {			
		if ( $( "input#exclude-type-" + excludeTypeIndex ).is( ':checked' ) ) {
			xType.push( excludeTypeIndex );
		}
	}

	items[ 'excludeType' ] = xType;	

	return items;
}






//build a query string based on selections by user
function updateURLHash( useSearchForm ) {

	var items = { };


//	DO NOT put anything in "items" before this "if" statement.
//	The "else" of this "if" replaces the contents of "items"


	if ( ! useSearchForm ) {

//		build hash string from previous search, they've just updated the drawing

//		add taxonomy exclusions
		items[ 'excludeTaxonomy' ] = _excludeTaxonomy;

//		add type exclusions
		items[ 'excludeType' ] = _excludeType;

//		add psm/peptide cutoffs
		items[ 'cutoffs' ] = _psmPeptideCutoffsRootObjectStorage.getPsmPeptideCutoffsRootObject();

//		add filter out non unique peptides
		items[ 'filterNonUniquePeptides' ] = _filterNonUniquePeptides;

//		add filter out non unique peptides
		items[ 'filterOnlyOnePSM' ] = _filterOnlyOnePSM;

//		add filter out non unique peptides
		items[ 'filterOnlyOnePeptide' ] = _filterOnlyOnePeptide;

	} else {

//		build hash string from values in form, they've requested a data refresh

		var formValues = getValuesFromForm();

		if ( formValues === null ) {

			return null;  //  EARLY EXIT
		}

//		nothing in items yet so just copy

		items = formValues;

	}


//	load in settings from viewer section
	if ( $( "input#show-self-crosslinks" ).is( ':checked' ) ) {
		items[ 'show-self-crosslinks' ] = true;
	} else {
		items[ 'show-self-crosslinks' ] = false;
	}

	if ( $( "input#show-crosslinks" ).is( ':checked' ) ) {
		items[ 'show-crosslinks' ] = true;
	} else {
		items[ 'show-crosslinks' ] = false;
	}

	if ( $( "input#show-looplinks" ).is( ':checked' ) ) {
		items[ 'show-looplinks' ] = true;
	} else {
		items[ 'show-looplinks' ] = false;
	}

	if ( $( "input#show-monolinks" ).is( ':checked' ) ) {
		items[ 'show-monolinks' ] = true;
	} else {
		items[ 'show-monolinks' ] = false;
	}
	

	if ( $( "input#show-protein-termini" ).is( ':checked' ) ) {
		items[ 'show-protein-termini' ] = true;
	} else {
		items[ 'show-protein-termini' ] = false;
	}
	

	if ( $( "input#show-linkable-positions" ).is( ':checked' ) ) {
		items[ 'show-linkable-positions' ] = true;
	} else {
		items[ 'show-linkable-positions' ] = false;
	}
	
	if ( $( "input#show-tryptic-cleavage-positions" ).is( ':checked' ) ) {
		items[ 'show-tryptic-cleavage-positions' ] = true;
	} else {
		items[ 'show-tryptic-cleavage-positions' ] = false;
	}	

	if ( $( "input#show-scalebar" ).is( ':checked' ) ) {
		items[ 'show-scalebar' ] = true;
	} else {
		items[ 'show-scalebar' ] = false;
	}

//	if ( $( "input#show-coverage" ).is( ':checked' ) ) {
//		items[ 'show-coverage' ] = true;
//	} else {
//		items[ 'show-coverage' ] = false;
//	}

	if ( $( "input#shade-by-counts" ).is( ':checked' ) ) {
		items[ 'shade-by-counts' ] = true;
	}

	if ( $( "input#color-by-search" ).is( ':checked' ) ) {
		items[ 'color-by-search' ] = true;
	}

	if ( !isSizingAutomatic() ) {
		items[ 'automatic-sizing' ] = false;
	}

	if ( $( "input#protein_names_position_left" ).is( ':checked' ) ) {
		items[ PROTEIN_NAMES_POSITION_HASH_JSON_PROPERTY_NAME ] = PROTEIN_NAMES_POSITION_LEFT;
	}
	
	
	items[ 'annotation_type' ] = $("#annotation_type").val();


	items[ 'vertical-bar-spacing' ] = _singleProteinBarOverallHeight;
	
	items[ 'horizontal-bar-scaling' ] = _horizontalScalingPercent;
	


//	add in the selected proteins
	items[ 'selected-proteins' ] = getAllSelectedProteins();

//	add in protein offsets, only add the offsets for visible proteins to save space in URL bar
	if ( _proteinOffsets != undefined ) {

		var proteinOffsets2 = {};

		var sprots = getSelectedProteins();

		if ( sprots != undefined ) {

			for ( var sprotsIndex = 0; sprotsIndex < sprots.length; sprotsIndex++ ) {
				proteinOffsets2[ sprots[ sprotsIndex ] ] = getProteinOffset( sprots[ sprotsIndex ] );
			}
		}

		items[ 'protein-offsets' ] = proteinOffsets2;
	}
	

//	add in protein reverses, only add the reverses for visible proteins to save space in URL bar
	if ( _proteinsReversed != undefined ) {

		var proteinsReversed2 = {};

		sprots = getSelectedProteins();

		if ( sprots != undefined ) {

			for ( sprotsIndex = 0; sprotsIndex < sprots.length; sprotsIndex++ ) {
				proteinsReversed2[ sprots[ sprotsIndex ] ] = isProteinReversed( sprots[ sprotsIndex ] );
			}
		}

		items[ 'proteins-reversed' ] = proteinsReversed2;
	}

//	add in the currently highlighted protein
	if ( _highlightedProteins.length > 0 ) {
		items[ 'highlighted-proteins' ] = _highlightedProteins;
	}

	window.location.hash = encodeURIComponent( JSON.stringify( items ) );
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

	items.push( "psmPeptideCutoffsForSearchIds=" + psmPeptideCutoffsForSearchIds_JSONString );

	
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
			        	markInvalidProteins();
			        	
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

	var selectedProteins = getSelectedProteins();
	
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
	
	var selectedProteins = getSelectedProteins();

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
	        	
	        	populateProteinOffsets();
	        	populateProteinsReversed();
	        	populateHighlightedProtein();
	        	populateNavigation();
	        	populateSearchForm();
	        	initializeViewer();
	        	
	        	updateURLHash( false /* useSearchForm */ );
	        	
	        	decrementSpinner();
	        	
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

// populate the protein offsets from the hash
function populateProteinOffsets() {
	var json = getJsonFromHash();
	
	if ( json['protein-offsets'] != undefined ) {
		_proteinOffsets = json['protein-offsets'];
	}
}


//populate the proteins reversed from the hash
function populateProteinsReversed() {
	var json = getJsonFromHash();
	
	if ( json['proteins-reversed'] != undefined ) {
		_proteinsReversed = json['proteins-reversed'];
	}
}

function populateHighlightedProtein() {
	var json = getJsonFromHash();
	
	if ( json['highlighted-proteins'] != undefined ) {
		_highlightedProteins = json['highlighted-proteins'];
	}
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
//	$( "input#show-coverage" ).prop('checked', json[ 'show-coverage' ] );
	$( "input#color-by-search" ).prop('checked', json[ 'color-by-search' ] );
	
	if ( json[ PROTEIN_NAMES_POSITION_HASH_JSON_PROPERTY_NAME ] === PROTEIN_NAMES_POSITION_LEFT ) {
		
		$( "input#protein_names_position_left" ).prop('checked', 'checked' );
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
	
	if ( json[ 'userScaleFactor' ] != undefined ) {
		_userScaleFactor = json[ 'userScaleFactor' ];
	}
	
	
	if ( json[ 'vertical-bar-spacing' ] !== undefined ) {
		_singleProteinBarOverallHeight = json[ 'vertical-bar-spacing' ];
	}
	
	if ( json[ 'horizontal-bar-scaling' ] !== undefined ) {
		_horizontalScalingPercent = json[ 'horizontal-bar-scaling' ];
		
		$("#horizontal_scaling_value").text( _horizontalScalingPercent );

	}
	
	
	handleScaleFactorVisibility( true /* supressRedraw */ );
	
}
	
function refreshViewerProteinSelects() {	
	var json = getJsonFromHash();

	
	// add and populate the appropriate selected proteins
	$( "select.svg-protein-select" ).remove();
	var sprots = json[ 'selected-proteins' ];
	if ( !sprots || sprots.length < 1 || _proteins.length < 1 ) {
		addProteinSelect();
	} else if ( sprots ) {
		
		for ( var i = 0; i < sprots.length; i++ ) {
			addProteinSelect();
			
			if ( $( "select.svg-protein-select option[value=\"" + sprots[ i ] + "\"]" ).length > 0 ) {
				$( "select.svg-protein-select" ).last().val( sprots[ i ] );
			} else {
				$( "select.svg-protein-select" ).last().val( 0 );
			}
		}
		
	}
		
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


// reset all protein offsets and redraw
function resetProteinOffsets() {
	_proteinOffsets = {};
	updateURLHash( false /* useSearchForm */ );
	drawSvg();
}


// get the visual offset for a specific protein
function getProteinOffset( proteinId ) {
	
	if ( _proteinOffsets[ proteinId ] === undefined ) {
		_proteinOffsets[ proteinId ] = 0;
	}
	
	return _proteinOffsets[ proteinId ];
}



//reset all proteinsReversed and redraw
function resetProteinsReversed() {
	_proteinsReversed = {};
	updateURLHash( false /* useSearchForm */ );
	drawSvg();
}

//get if a specific protein should be displayed reversed
function isProteinReversed( proteinId ) {
	
	if ( _proteinsReversed[ proteinId ] === undefined ) {
		_proteinsReversed[ proteinId ] = false;
	}
	
	return _proteinsReversed[ proteinId ];
}


// responsible for adding the draggable events to a protein group ( the rectangle and the label (protein name) )
function addDragTo( g, protein ) {

	var startx = null; // updated on "start drag"
	
	var tooltipsDestroyed = false;
	
	var moveFunc = function (dx, dy, posx, posy) {
		
		//  "this" is the protein group ( the rectangle and the label (protein name) )

		this.transform("t" + dx );
		
		
		if ( ! tooltipsDestroyed ) {

			var bbox = this.getBBox();
			var amountElementMoved = bbox.x - startx;

			// only do this if they actually moved the protein bar group
			if ( Math.abs( amountElementMoved ) > 0 ) {

				tooltipsDestroyed = true;

				var $svg_image_inner_container_div = $("#svg_image_inner_container_div");

				//  select the <svg> element
				var $merged_image_svg_jq__BeforeEmpty = $svg_image_inner_container_div.find("svg.merged_image_svg_jq");

				var $proteinBarOverlayElements = $merged_image_svg_jq__BeforeEmpty.find( "." + _PROTEIN_BAR_OVERLAY_RECTANGLE_LABEL_CLASS );

				$proteinBarOverlayElements.qtip('destroy', true); // Immediately destroy all tooltips belonging to the selected elements

				var $proteinBarProteinNameElements = $merged_image_svg_jq__BeforeEmpty.find( "." + _PROTEIN_BAR_PROTEIN_NAME_LABEL_CLASS );

				$proteinBarProteinNameElements.qtip('destroy', true); // Immediately destroy all tooltips belonging to the selected elements
			}
		}
	};
	
	var startDragFunc = function() {
		
		//  "this" is the protein group ( the rectangle and the label (protein name) )
		
		var bbox = this.getBBox();
		startx = bbox.x;
		
		tooltipsDestroyed = false;
	};
	
	var stopDragFunc = function () {
		
		if ( startx === null ) {
			
			throw "start drag not called, startx === null";
		}
		
		//  "this" is the protein group ( the rectangle and the label (protein name) )

		var bbox = this.getBBox();
		
		var amountElementMoved = bbox.x - startx;
		
		// only do this if they actually moved the protein bar group, or moved bar and tool tips were destroyed
		if ( ( Math.abs( amountElementMoved ) > 0 ) || tooltipsDestroyed ) {
		
			var prevOffset = _proteinOffsets[ protein ];
			
			var newOffset = prevOffset + ( amountElementMoved ); // adjust offset for amount element moved;
			
			_proteinOffsets[ protein ] = newOffset;			  // save the new position of the protein		
			
			//  Adjust Protein Offsets So Left Most At Left Edge
//			adjustProteinOffsetsSoLeftMostAtLeftEdge();
			
			//  Adjust offsets if protein bar moved to left of most possible left position of protein bars 
			
			if ( _proteinOffsets[ protein ] < 0 ) {
				var selectedProteins = getAllSelectedProteins();
				var adder = ( _proteinOffsets[ protein ] ) * -1;
				for ( var i = 0; i < selectedProteins.length; i++ ) {
					_proteinOffsets[ selectedProteins[ i ] ] += adder;
				}
			}
			
			updateURLHash( false /* useSearchForm */ );	  					// save the new position to the URL hash
			drawSvg();
		}
		
		startx = null; //  reset.   updated on "start drag"
		
		tooltipsDestroyed = false;
	};
	
	//  actually add the draw to the SVG group - protein group ( the rectangle and the label (protein name) )
	g.drag( moveFunc, startDragFunc, stopDragFunc  );
	
	
}



// add click and double click handlers to a protein rectangle, such that it
// functions alongside the draggable event handler
function addClickDoubleClickTo( g, protein, i  ) {
	
	var clicks = 0;
	var startx = 0;
	
	var singleClickFunction = function( e ) {
		toggleHighlightedProtein( i, true /* shouldClearFirst */ );
		updateURLHash( false /* useSearchForm */ );
	};
	
	
	var doubleClickFunction = function( e ) {
		
		toggleReversedProtein( i, protein );
		updateURLHash( false /* useSearchForm */ );
		//console.log( "got double click on " + _proteinNames[ protein ] + "(" + i + ")" );
	};

	
	var shiftClickFunction = function( e ) {
		toggleHighlightedProtein( i, false /* shouldClearFirst */ );
		updateURLHash( false /* useSearchForm */ );
	};
	
	
	
	
	var mouseDownFunction = function( e ) {
		startx = e.clientX;
	};
	
	
	var mouseUpFunction = function( e ) {
		
		if ( Math.abs( e.clientX - startx ) < 1 ) {
			clicks++;
		} else {
			clicks = 0;
		}
		
		if ( clicks === 1 && Math.abs( e.clientX - startx ) < 1 ) {
			setTimeout( function() {
				if ( clicks === 1 ) {
					if ( e.shiftKey ) { shiftClickFunction.call( this, e ); }
					else { singleClickFunction.call( this, e ); }
				} else {
					doubleClickFunction.call( this, e );
				}
				clicks = 0;
			}, 300 );
		}
	};
	
	g.mousedown( mouseDownFunction );
	g.mouseup( mouseUpFunction );
}

// reset/clear highlighted proteins
function clearHighlightedProteins() {
	
	_highlightedProteins = [ ];
}

// whether or not the supplied selected protein index is currently highlighted
function isHighlightedProtein( i ) {
	
	return _highlightedProteins.indexOf( i ) != -1;
}

// highlight the given index among the selected proteins
function toggleHighlightedProtein( i, shouldClearFirst ) {
	
	if ( shouldClearFirst && _highlightedProteins.length > 1 ) {
		clearHighlightedProteins();
		_highlightedProteins.push( i );
	} else {	
		if ( isHighlightedProtein( i ) ) {
			_highlightedProteins.splice( _highlightedProteins.indexOf( i ), 1 ); //  Remove the element for this protein
		} else {
			if ( shouldClearFirst ) { 
				clearHighlightedProteins(); 
			}
			_highlightedProteins.push( i );
		}
	}
	
	drawSvg();
}


//Reverse the protein 
function toggleReversedProtein( i, protein ) {
	
	if ( ! _proteinsReversed[ protein ] ) {
		_proteinsReversed[ protein ] = true;
		
		$( "input#show-protein-termini" ).prop('checked', true );

	} else {	
		_proteinsReversed[ protein ] = false;
	}
	
	drawSvg();
}


// get color for protein bar 
function getProteinBarColor( i ) {
	
	if ( _highlightedProteins.length < 1 || isHighlightedProtein( i ) ) {
		
		//  if no highlighted proteins or this protein bar index is highlighted, use this color
		
		return _PROTEIN_BAR_COLOR_MAIN_AND_HIGHLIGHTED;
	}
	
	return _PROTEIN_BAR_COLOR_NOT_HIGHLIGHTED;
}

// get the color that should be used for the supplied index among the selected proteins
function getColor( i ) {
	
	if ( _highlightedProteins.length < 1 || isHighlightedProtein( i ) ) {
		return _LINE_COLORS [ i % _LINE_COLORS.length ];
	}
	
	return _NOT_HIGHLIGHTED_LINE_COLOR;
}

// get color for line that is connecting two proteins, based on those proteins indexes among selected proteins
function getCrosslinkLineColor( i, k ) {
	
	if ( _highlightedProteins.length < 1 ) { 
		return getColor( i ); 
	}
	
	if ( _highlightedProteins.length == 1 ) {
		if ( isHighlightedProtein( i ) ) { 
			return getColor( i ); 
		}
		return getColor( k );
	}

	if ( isHighlightedProtein( i ) && isHighlightedProtein( k ) ) { 
		return getColor( i ); 
	}

	return _NOT_HIGHLIGHTED_LINE_COLOR;
}

// get the opacity that should be used for links associated with a specific protein index
function getOpacity( i, link ) {
	
	if ( _highlightedProteins.length < 1 || isHighlightedProtein( i ) ) {
		
		if ( $( "input#shade-by-counts" ).is( ':checked' ) ) {
			
			if( !link ) {
				console.log( "ERROR: Supposed to set opacity based on PSM count, but got no link. Setting opacity to 1." );
				return 1.0;
			}
			
			var numPsms = 0;
			
			try {
				if( link.type == 'crosslink' ) {
				
					numPsms = _linkPSMCounts[ 'crosslink' ][ link.protein1 ][ link.protein2 ][ link.position1 ][ link.position2 ];				
					
				} else if( link.type == 'looplink' ) {
					
					numPsms = _linkPSMCounts[ 'looplink' ][ link.protein1 ][ link.protein1 ][ link.position1 ][ link.position2 ];	
					
				} else if( link.type == 'monolink' ) {
					
					numPsms = _linkPSMCounts[ 'monolink' ][ link.protein1 ][ link.position1 ];	
					
				} else {
					
					console.log( "ERROR: Supposed to set opacity based on PSM count, but link has no valid type. Setting to 1. link: " );
					console.log( link );
					
					return 1.0;				
				}
			} catch( err ) {
				
				console.log( "Got error getting PSM count: " + err.message );				
			}

			
			if( !numPsms ) {
				
				console.log( "ERROR: Supposed to set opacity based on PSM count, but got no PSMs for link. Returning 1.0" );
				console.log( "link:" );
				console.log( link );
				console.log( "_linkPSMCounts" );
				console.log( _linkPSMCounts );
				
				return 1.0;				
			}
			
			var min = 0.1;
			var max = 1.0;
			var countForMaxOpacity = 10;
			
			if( numPsms > countForMaxOpacity ) { numPsms = countForMaxOpacity; }
			
			var opacity = max - ( ( max - min ) * ( ( countForMaxOpacity - numPsms ) / countForMaxOpacity ) );
			if( opacity < min || opacity > max ) {
				console.log( "WARNING: Invalid opacity: " + opacity  + " (numPsms: " + numPsms + ")" );
			}
			
			return opacity;
			
		} else {
			
			return 1.0;
		}
		

	}
	
	return 0.25;
}

// get the opacity that should be used for lines connecting two crosslinked proteins, based on those proteins indexes among selected proteins
function getCrosslinkLineOpacity( i, k, link ) {

	if ( _highlightedProteins.length < 1 ) { return getOpacity( i, link ); }
	
	if ( _highlightedProteins.length == 1 ) {
		if ( isHighlightedProtein( i ) ) { return getOpacity( i, link ); }
		return getOpacity( k, link );
	}

	if ( isHighlightedProtein( i ) && isHighlightedProtein( k ) ) { return getOpacity( i, link ); }
	
	return 0.25;
}

// get the color that should be used for a link, based on that links presence in the supplied searches
// index i is supplied to support "highlighted" protein coloring
function getColorForSearches( i, searchList ) {
	
	var colorIndex = "";
	
	if ( _highlightedProteins.length < 1 || isHighlightedProtein( i ) || i == -1 ) {
		for ( var i = 0; i < _searches.length; i++ ) {
			for ( var k = 0; k < searchList.length; k++ ) {
				if ( _searches[i]['id'] === searchList[ k ] ) {
					colorIndex += ( i + 1 );
					break;
				}
			}
		}
		
		if ( _searches.length === 2 ) {
			
			var colorForSearches = _SEARCH_COLORS_TWO_SEARCHES[ colorIndex ];
			
//			if ( colorForSearches === undefined ) {
//				
//				throw "In getColorForSearches: color for searches is undefined for colorIndex: '" + colorIndex + "'  _SEARCH_COLORS_TWO_SEARCHES[ colorIndex ]";
//			}
			
			return colorForSearches;
		} else {
			var colorForSearches = _SEARCH_COLORS[ colorIndex ];
			
//			if ( colorForSearches === undefined ) {
//				
//				throw "In getColorForSearches: color for searches is undefined for colorIndex: '" + colorIndex + "'  _SEARCH_COLORS[ colorIndex ]";
//			}
			
			return colorForSearches;
		}
	}
	
	return _NOT_HIGHLIGHTED_LINE_COLOR;
}



// get the color to base used for a line connecting two proteins while coloring by search
// i and k are supplied to support "highlighted" protein coloring
function getCrosslinkLineColorForSearches( i, k, searchList ) {

	if ( _highlightedProteins.length < 1 ) { 
		return getColorForSearches( i, searchList ); 
	}
	
	if ( _highlightedProteins.length == 1 ) {
		if ( isHighlightedProtein( i ) ) { 
			return getColorForSearches( i, searchList ); 
		}
		return getColorForSearches( k, searchList );
	}

	if ( isHighlightedProtein( i ) && isHighlightedProtein( k ) ) { 
		return getColorForSearches( i, searchList ); 
	}

	return _NOT_HIGHLIGHTED_LINE_COLOR;
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


/////////////

function findMaxLengthInPixelsOfProteinName( svgRootSnapSVGObject, selectedProteins ) {
	

	//  Place each name on the page and measure the width

	var maxLengthInPixelsOfProteinName = 0;

	// draw rectangles
	for ( var i = 0; i < selectedProteins.length; i++ ) {

		var protein = selectedProteins[ i ];
	
		var proteinName = getProteinName( protein );
		

		var proteinNameSnapSVGObject = svgRootSnapSVGObject.text( 1, 10, proteinName );

		var proteinNameSnapSVGObjectBBox = proteinNameSnapSVGObject.getBBox();
		var proteinNameSnapSVGObjectWidth = proteinNameSnapSVGObjectBBox.width;

		//  Remove temporary text object inserted to get it's size
		
		proteinNameSnapSVGObject.remove();
		
		if ( proteinNameSnapSVGObjectWidth > maxLengthInPixelsOfProteinName ) {
			
			maxLengthInPixelsOfProteinName = proteinNameSnapSVGObjectWidth;
		}
		
	}
	
	return maxLengthInPixelsOfProteinName;
}




/////////////

function findMaxLengthInPixelsOfTerminusLabels( svgRootSnapSVGObject ) {


//	Place each name on the page and measure the width

	var maxLengthInPixelsOfTerminusLabel = 0;

	var terminusSnapSVGObjectWidth = findLengthInPixelsOfTerminusLabel( svgRootSnapSVGObject, "C" );

	if ( terminusSnapSVGObjectWidth > maxLengthInPixelsOfTerminusLabel ) {

		maxLengthInPixelsOfTerminusLabel = terminusSnapSVGObjectWidth;
	}

	var terminusSnapSVGObjectWidth = findLengthInPixelsOfTerminusLabel( svgRootSnapSVGObject, "N" );

	if ( terminusSnapSVGObjectWidth > maxLengthInPixelsOfTerminusLabel ) {

		maxLengthInPixelsOfTerminusLabel = terminusSnapSVGObjectWidth;
	}
	

	return maxLengthInPixelsOfTerminusLabel;
}


////////////////////////////

function findLengthInPixelsOfTerminusLabel( svgRootSnapSVGObject, terminusLabel ) {
	

	var terminusSnapSVGObject = svgRootSnapSVGObject.text( 1, 10, "C" );

	var terminusSnapSVGObjectBBox = terminusSnapSVGObject.getBBox();
	var terminusSnapSVGObjectWidth = terminusSnapSVGObjectBBox.width;

//	Remove temporary text object inserted to get it's size

	terminusSnapSVGObject.remove();
	
	return terminusSnapSVGObjectWidth;
}



////////////////////////////

function precomputeMultiplierAndOtherValuesForSVG( svgRootSnapSVGObject, selectedProteins ) {
	
	
	//  Get max protein length
	
	var maxProteinLength = 0;
	var selectedProteins = getAllSelectedProteins();
	
	for ( var i = 0; i < selectedProteins.length; i++ ) {
		
		var proteinLength = _proteinLengths[ selectedProteins[ i ] ];
		
		if ( proteinLength > maxProteinLength ) { 
		
			maxProteinLength = proteinLength; 
		}
	}
	
	//  Get width of tick mark label for max protein length.
	
	var maxProteinLengthText = maxProteinLength.toString();
	
	if ( maxProteinLengthText.charAt( 0 ) === "9" ) {
		
		maxProteinLengthText = "0" + maxProteinLengthText; // add another character since may roll over when add to get to the next tick point
	}
	
	
	var maxProteinLengthTextForLengthTest = maxProteinLengthText.replace( "1", "0" ); // replace "1" with "0" since in some fonts "1" may be narrower

	
	var tickMarkSnapSVGObjectMaxProteinLength = svgRootSnapSVGObject.text( 1, 10, maxProteinLengthTextForLengthTest );

	var tickMarkSnapSVGObjectMaxProteinLengthBBox = tickMarkSnapSVGObjectMaxProteinLength.getBBox();
	var tickMarkSnapSVGObjectMaxProteinLengthWidth = tickMarkSnapSVGObjectMaxProteinLengthBBox.width;

	var tickMarkSnapSVGObjectMaxProteinLengthWidthHalf = tickMarkSnapSVGObjectMaxProteinLengthWidth / 2;

	//  Remove temporary text object inserted to get it's size
	
	tickMarkSnapSVGObjectMaxProteinLength.remove();
	
	
	
	var proteinNamePositionLeftSelected = false;
	var showProteinTerminiSelected = false;
	
	if ( $( "input#protein_names_position_left" ).is( ':checked' ) ) {

		proteinNamePositionLeftSelected = true;
	}
	
	if ( $( "input#show-protein-termini" ).is( ':checked' ) ) {

		showProteinTerminiSelected = true;
	}
	
	if ( proteinNamePositionLeftSelected ) {
		
		var maxLengthInPixelsOfProteinName = findMaxLengthInPixelsOfProteinName( svgRootSnapSVGObject, selectedProteins );


		//  Make room for protein names on the left
		
		_proteinBarsLeftEdge = 
			Math.ceil( _PADDING_OVERALL_IMAGE_LEFT_SIDE + maxLengthInPixelsOfProteinName + _PROTEIN_BAR_PROTEIN_NAME_GAP_WHEN_PROTEIN_NAME_TO_THE_LEFT );

	} else if ( showProteinTerminiSelected ) {
		
		var maxLengthInPixelsOfTerminusLabel = findMaxLengthInPixelsOfTerminusLabels( svgRootSnapSVGObject );
		
		
		//  Make room for Terminus Label on the left
		
		_proteinBarsLeftEdge = 
			Math.ceil( _PADDING_OVERALL_IMAGE_LEFT_SIDE + maxLengthInPixelsOfTerminusLabel + _PROTEIN_TERMINUS_BAR_HORIZONTAL_OFFSET );

		
		
	} else {
		
		//  Align protein bars left edge at the left edge
		
		_proteinBarsLeftEdge = _PADDING_OVERALL_IMAGE_LEFT_SIDE;
	}
	
	
	//  Compute
	

	var maxWidth = get_MAX_WIDTH();
	

	if ( showProteinTerminiSelected ) {
		
		//  Shrink maxWidth to make room for the right Protein Termini since it extends past the protein bar 
		
		var maxLengthInPixelsOfTerminusLabel = findMaxLengthInPixelsOfTerminusLabels( svgRootSnapSVGObject );
		
		maxWidth = maxWidth - maxLengthInPixelsOfTerminusLabel - _PROTEIN_TERMINUS_BAR_HORIZONTAL_OFFSET;
	}
	

	//  Compute Multiplier
	
	var precomputeStandardResult = 
		precomputeMultiplier__CallOnlyFrom__precomputeMultiplierAndOtherValuesForSVG( maxWidth, maxProteinLength, tickMarkSnapSVGObjectMaxProteinLengthWidthHalf );

	
	_computedStandardMultiplier = precomputeStandardResult.computedMultiplier;
	
	_userScaleFactor = _computedStandardMultiplier * ( _horizontalScalingPercent / 100 );
	
	if ( ! isSizingAutomatic() && _userScaleFactor !== undefined ) {
		
		maxWidth = _userScaleFactor * maxProteinLength;
	}
	

	//  Compute Multiplier
	
	var precomputeResult = 
		precomputeMultiplier__CallOnlyFrom__precomputeMultiplierAndOtherValuesForSVG( maxWidth, maxProteinLength, tickMarkSnapSVGObjectMaxProteinLengthWidthHalf  );

	_computedMultiplier = precomputeResult.computedMultiplier;
	var scalebarIncrement = precomputeResult.scalebarIncrement;
	
	
//	var svgViewPortWidth = getCurrentViewportWidth();
	
	
	var functionResponse = {
			
//			svgViewPortWidth: svgViewPortWidth,
			scalebarIncrement: scalebarIncrement
	};
	
	
	return functionResponse;
}




/////  Compute Multiplier



function precomputeMultiplier__CallOnlyFrom__precomputeMultiplierAndOtherValuesForSVG( 
		
		maxWidth, maxProteinLength, tickMarkSnapSVGObjectMaxProteinLengthWidthHalf
) {
	

	

	//  Compute max horizonal width for tick marks
	
	var maxWidthForTicks = maxWidth - tickMarkSnapSVGObjectMaxProteinLengthWidthHalf - 2 ; // subtract to ensure tick label will fit 
	
	//  Get max number of tick marks, minimum 50 pixels 
	
	var maxNumberTickMarksUnrounded = maxWidthForTicks / _MIN_SPACE_BETWEEN_TICK_MARKS;
	
	var maxNumberTickMarks = Math.floor( maxNumberTickMarksUnrounded );
	
	//  Determine what tick mark increment and tick mark spacing to use
	

	var scalebarIncrement = 0;
	
	for ( var i = 0; i < _POTENTIAL_TICK_INCREMENTS.length; i++ ) {
		
		var maxSequenceLengthRepresented = _POTENTIAL_TICK_INCREMENTS[ i ] * maxNumberTickMarks;
		
		if ( maxSequenceLengthRepresented < maxProteinLength ) { 
			continue;  //  The max sequence length represented is too small so try the next larger increment
		}
		scalebarIncrement = _POTENTIAL_TICK_INCREMENTS[ i ];
		break;
	}
	
	if ( scalebarIncrement === 0 ) {

		scalebarIncrement = _POTENTIAL_TICK_INCREMENTS[ _POTENTIAL_TICK_INCREMENTS.length - 1 ]; // default to largest value
	}
	
	//  tick count excludes the first one on the left end
	var tickCount = Math.ceil( maxProteinLength / scalebarIncrement );
	
	var ticksMaxLabelValue = tickCount * scalebarIncrement;
	
	//  Compute Multiplier
	
	var computedMultiplier = maxWidthForTicks / ticksMaxLabelValue;
	
	
	
	return { computedMultiplier : computedMultiplier, scalebarIncrement : scalebarIncrement };
}



/////////////////////////////////////////////////////////

///  Actually draw the image



function drawSvg() {
	
	var svgRootSnapSVGObject;  // the Snap SVG object for the SVG


	
	//  First remove and add the "<svg>" element from the template
	
	var $svg_image_template_div = $("#svg_image_template_div");
	
	var svg_image_template_div__html = $svg_image_template_div.html();
	
	var $svg_image_inner_container_div = $("#svg_image_inner_container_div");
	
	//  select the <svg> element
	var $merged_image_svg_jq__BeforeEmpty = $svg_image_inner_container_div.find("svg.merged_image_svg_jq");

	var $proteinBarOverlayElements = $merged_image_svg_jq__BeforeEmpty.find( "." + _PROTEIN_BAR_OVERLAY_RECTANGLE_LABEL_CLASS );
	
	$proteinBarOverlayElements.qtip('destroy', true); // Immediately destroy all tooltips belonging to the selected elements

	var $proteinBarProteinNameElements = $merged_image_svg_jq__BeforeEmpty.find( "." + _PROTEIN_BAR_PROTEIN_NAME_LABEL_CLASS );
	
	$proteinBarProteinNameElements.qtip('destroy', true); // Immediately destroy all tooltips belonging to the selected elements
	
	
	$svg_image_inner_container_div.empty();  //  remove the <svg> element.  jQuery will also properly remove all the handlers attached to those elements
	
	$svg_image_inner_container_div.html( svg_image_template_div__html );  // insert new <svg> as copied from the template
	
	//  select the <svg> element
	var $merged_image_svg_jq = $svg_image_inner_container_div.find("svg.merged_image_svg_jq");
	
	//  get the <svg> HTML element to pass to Snap
	var merged_image_svg_element = $merged_image_svg_jq[0];

	

	
	var bottomOfLowestItemDrawn = 0;
	var rightMostEdgeOfAllElements = 0;
	
	var selectedProteins = getAllSelectedProteins();
	
//	var maxProteinLength = 0;

	if ( selectedProteins === undefined || selectedProteins.length < 1 ) { 
	
		return;  //  EARLY EXIT of function if no proteins to display 
	}
	

//	svgRootSnapSVGObject = Snap("#svg"); //  using CSS selector, no longer has that "id" 
	
	svgRootSnapSVGObject = Snap( merged_image_svg_element );  // pass in HTML element
	
	
	svgRootSnapSVGObject.attr( { width: _DEFAULT_VIEWPORT_WIDTH } );
	svgRootSnapSVGObject.attr( { height: _DEFAULT_VIEWPORT_HEIGHT } );
		
//	svgRootSnapSVGObject.clear();  //  switched to using jQuery .empty() on parent element since jQuery properly removes attached event handlers on the objects.

	if ( selectedProteins.length < 1 ) {
		return;
	}
	
	if ( _proteins.length < 1 ) {
		return;
	}

	
//	svgRootSnapSVGObject.attr( { width: getCurrentViewportWidth() } );

	
	
	var precomputeMultiplierAndOtherValuesForSVGResults = precomputeMultiplierAndOtherValuesForSVG( svgRootSnapSVGObject, selectedProteins );

	var scalebarIncrement = precomputeMultiplierAndOtherValuesForSVGResults.scalebarIncrement;
	
	
	//	draw sequence coverage, if requested
//	if ( $( "input#show-coverage" ).is( ':checked' ) && _ranges != undefined ) {
	

	var annotationType = $("#annotation_type").val();
	
	if ( annotationType === SELECT_ELEMENT_ANNOTATION_TYPE_SEQUENCE_COVERAGE ) {

		drawSequenceCoverage( selectedProteins, svgRootSnapSVGObject );
	}

	
	
	drawAnnotationData( selectedProteins, svgRootSnapSVGObject );
	
	
	
	
	
	// draw protein bar rectangles
	var addAllProteinBarGroupsResponse = addAllProteinBarGroups( selectedProteins, svgRootSnapSVGObject, $merged_image_svg_jq );

	var rightMostEdgeOfAllElementsAddAllProteinBarGroups = addAllProteinBarGroupsResponse.rightMostEdgeOfAllElementsThisFunction;
	if ( rightMostEdgeOfAllElementsAddAllProteinBarGroups > rightMostEdgeOfAllElements ) {
		rightMostEdgeOfAllElements = rightMostEdgeOfAllElementsAddAllProteinBarGroups;
	} 

	
	bottomOfLowestItemDrawn = _singleProteinBarOverallHeight * selectedProteins.length;
	

	// draw scale bar, if requested
	if ( $( "input#show-scalebar" ).is( ':checked' ) ) {
		
		var drawScaleBarResponse = drawScaleBar( selectedProteins, scalebarIncrement, svgRootSnapSVGObject );
		
		bottomOfLowestItemDrawn =  drawScaleBarResponse.bottomOfLowestItemDrawn;

		var rightMostEdgeOfAllElementsAddScaleBars = drawScaleBarResponse.rightMostEdgeOfAllElementsThisFunction;
		if ( rightMostEdgeOfAllElementsAddScaleBars > rightMostEdgeOfAllElements ) {
			rightMostEdgeOfAllElements = rightMostEdgeOfAllElementsAddScaleBars;
		} 
	}
	
	


	// draw interprotein crosslink lines, if requested
	if ( $( "input#show-crosslinks" ).is( ':checked' ) ) {
		
		drawInterProteinCrosslinkLines( selectedProteins, svgRootSnapSVGObject );
	}
	

	// draw self-crosslinks, if requested
	if ( $( "input#show-self-crosslinks" ).is( ':checked' ) ) {
		
		drawSelfProteinCrosslinkLines( selectedProteins, svgRootSnapSVGObject );
	}
	


	// draw looplinks, if requested
	if ( $( "input#show-looplinks" ).is( ':checked' ) ) {
		
		drawProteinLooplinkLines( selectedProteins, svgRootSnapSVGObject );
		

	}

	
	
	// draw monolinks, if requested
	if ( $( "input#show-monolinks" ).is( ':checked' ) ) {
		
		drawProteinMonolinkLines( selectedProteins, svgRootSnapSVGObject );
		

	}
	
	// draw the legend, if they're coloring by search
	if ( $( "input#color-by-search" ).is( ':checked' ) ) {
		
		var drawColorBySearchLegendResponse = drawColorBySearchLegend(  selectedProteins, bottomOfLowestItemDrawn, svgRootSnapSVGObject );

		var rightMostEdgeOfAllElementsDrawColorBySearchLegendResponse = drawColorBySearchLegendResponse.rightMostEdgeOfAllElementsThisFunction;
		if ( rightMostEdgeOfAllElementsDrawColorBySearchLegendResponse > rightMostEdgeOfAllElements ) {
			rightMostEdgeOfAllElements = rightMostEdgeOfAllElementsDrawColorBySearchLegendResponse;
		} 
		
		bottomOfLowestItemDrawn += 50;  // adjust bottomOfLowestItemDrawn for this item
	};
	
	
	//  Test to show where the value of bottomOfLowestItemDrawn falls
//	var rectangleAtBottom = svgRootSnapSVGObject.rect( 100, bottomOfLowestItemDrawn, 20, 20 );
//	rectangleAtBottom.attr( {
//		fill: "pink"
//	});

	var newHeight = bottomOfLowestItemDrawn + 5;  //  Add 60 to be conservative to not cut anything off 
	
	svgRootSnapSVGObject.attr( { height: newHeight } );
	
	var newWidth = rightMostEdgeOfAllElements + _PADDING_OVERALL_IMAGE_RIGHT_SIDE;
	
	svgRootSnapSVGObject.attr( { width: newWidth } );

	
	/*
	// for testing, show all protein positions
	for ( var i = 0; i < selectedProteins.length; i++ ) {

		console.log( "Length: " + _proteinLengths[ selectedProteins[ i ] ] );
		
		var fromY = _OFFSET_FROM_TOP + ( 80 * i ) + _SINGLE_PROTEIN_BAR_HEIGHT - 17;
		var toY = _OFFSET_FROM_TOP + ( 80 * i ) - 10;

		for ( var k = 1; k <= _proteinLengths[ selectedProteins[ i ] ]; k++ ) {
			var x = translatePositionToXCoordinate( selectedProteins[ i ], k );
				
			var line = svgRootSnapSVGObject.line( x, fromY, x, toY );
			line.attr({
				stroke:'red',
				strokeWidth:1,
				fill: "none"
			});
			
			//svgRootSnapSVGObject.text( x -5 , toY - 8, k );
		}
	}
	*/
	

}



////////////////////////////////////////////////////////////


function drawSequenceCoverage( selectedProteins, svgRootSnapSVGObject ) {

	for ( var proteinBarRowIndex = 0; proteinBarRowIndex < selectedProteins.length; proteinBarRowIndex++ ) {

		var protein = selectedProteins[ proteinBarRowIndex ];
		var blockColor = getColor( proteinBarRowIndex );

		var segments = _ranges[ protein ];

		if ( segments != undefined && segments.length > 0 ) {
			
			for ( var k = 0; k < segments.length; k++ ) {
			
				var segment = segments[ k ];
				var start = segment.start;
				var end = segment.end;

				var drawAnnotationRectangle_Params = {
						
						 protein : protein,
						 start : start,
						 end : end,
						 proteinBarRowIndex : proteinBarRowIndex,
						 blockColor : blockColor,
						 
						 svgRootSnapSVGObject : svgRootSnapSVGObject
				};
				
				var rectangleSnapSVGObject = drawAnnotationRectangle( drawAnnotationRectangle_Params );

				var toolTipText = 'Sequence Coverage: start: ' + start + ', end: ' + end;

				var toolTipParams = {
						content: {
							text: toolTipText
						},
						position: {
							target: 'mouse'
								,
								adjust: { x: 5, y: 5 } // Offset it slightly from under the mouse
						}
				};

				var rectangleSVGNativeObject = rectangleSnapSVGObject.node;
				var $rectangleSVGNativeObject = $(rectangleSVGNativeObject);
				$rectangleSVGNativeObject.qtip( toolTipParams );
			}
		}
	}

}



////////////////////////////////////////////////////////////


function drawAnnotationData( selectedProteins, svgRootSnapSVGObject ) {
	
	var annotationType = $("#annotation_type").val();
	
	if ( annotationType !== undefined && annotationType !== "" && annotationType !== SELECT_ELEMENT_ANNOTATION_TYPE_SEQUENCE_COVERAGE ) {

		if ( annotationType === SELECT_ELEMENT_ANNOTATION_TYPE_DISOPRED3 ) {
			
			drawDisopred_3_AnnotationData( selectedProteins, svgRootSnapSVGObject );
			
		} else if ( annotationType === SELECT_ELEMENT_ANNOTATION_TYPE_PSIPRED3 ) {
			
			drawPsipred_3_AnnotationData( selectedProteins, svgRootSnapSVGObject );
			
		} else {
			
			throw "unknown annotationType: " + annotationType;
		}
		

			
	}
	
	
}


function drawDisopred_3_AnnotationData( selectedProteins, svgRootSnapSVGObject ) {

	for ( var proteinBarRowIndex = 0; proteinBarRowIndex < selectedProteins.length; proteinBarRowIndex++ ) {

		var protein = selectedProteins[ proteinBarRowIndex ];

		var blockColor = "#000000";

		var segments = getDisorderedRegionsDisopred_3( protein );
		
		if ( segments === null ) {
			
			//  Disopred likely Failed so display failed 
			
			var proteinLength = _proteinLengths[ protein ];
			
			var drawAnnotationRectangle_Params = {

					protein : protein,
					start : 1,
					end : proteinLength,
					proteinBarRowIndex : proteinBarRowIndex,
					blockColor : "#FF0000",

					svgRootSnapSVGObject : svgRootSnapSVGObject
			};
			

			var rectangleSnapSVGObject = drawAnnotationRectangle( drawAnnotationRectangle_Params );


			var toolTipText = 'Disordered Failed to compute';

			var toolTipParams = {
					content: {
						text: toolTipText
					},
					position: {
						target: 'mouse'
							,
							adjust: { x: 5, y: 5 } // Offset it slightly from under the mouse
					}
			};

			var rectangleSVGNativeObject = rectangleSnapSVGObject.node;
			var $rectangleSVGNativeObject = $(rectangleSVGNativeObject);
			$rectangleSVGNativeObject.qtip( toolTipParams );
			
		} else {

			if ( segments != undefined && segments.length > 0 ) {

				for ( var k = 0; k < segments.length; k++ ) {

					var segment = segments[ k ];
					var start = segment.startPosition;
					var end = segment.endPosition;

					var drawAnnotationRectangle_Params = {

							protein : protein,
							start : start,
							end : end,
							proteinBarRowIndex : proteinBarRowIndex,
							blockColor : blockColor,

							svgRootSnapSVGObject : svgRootSnapSVGObject
					};

					var rectangleSnapSVGObject = drawAnnotationRectangle( drawAnnotationRectangle_Params );


					var toolTipText = 'Disordered Region: start: ' + start + ', end: ' + end;

					var toolTipParams = {
							content: {
								text: toolTipText
							},
							position: {
								target: 'mouse'
									,
									adjust: { x: 5, y: 5 } // Offset it slightly from under the mouse
							}
					};

					var rectangleSVGNativeObject = rectangleSnapSVGObject.node;
					var $rectangleSVGNativeObject = $(rectangleSVGNativeObject);
					$rectangleSVGNativeObject.qtip( toolTipParams );
				}
			}
		}
	}

}




var BETA_SHEET = "E";
var ALPHA_HELIX = "H";

var TOOL_TIP_ALPHA_HELIX = "&#945; helix";  //  First character is a Alpha character	

var TOOL_TIP_BETA_SHEET = "&#946; sheet";  //  First character is a Beta character	
	

function drawPsipred_3_AnnotationData( selectedProteins, svgRootSnapSVGObject ) {

	for ( var proteinBarRowIndex = 0; proteinBarRowIndex < selectedProteins.length; proteinBarRowIndex++ ) {

		var protein = selectedProteins[ proteinBarRowIndex ];
		
		var blockColor = null;
		
		var typeTooltipText = null;

		var segments = getSecondaryStructureRegions( protein );
		

		if ( segments === null ) {
			
			//  Secondary Structure likely Failed so display failed 
			
			var proteinLength = _proteinLengths[ protein ];
			
			var drawAnnotationRectangle_Params = {

					protein : protein,
					start : 1,
					end : proteinLength,
					proteinBarRowIndex : proteinBarRowIndex,
					blockColor : "#FF0000",

					svgRootSnapSVGObject : svgRootSnapSVGObject
			};
			

			var rectangleSnapSVGObject = drawAnnotationRectangle( drawAnnotationRectangle_Params );


			var toolTipText = 'Secondary Structure Failed to compute';

			var toolTipParams = {
					content: {
						text: toolTipText
					},
					position: {
						target: 'mouse'
							,
							adjust: { x: 5, y: 5 } // Offset it slightly from under the mouse
					}
			};

			var rectangleSVGNativeObject = rectangleSnapSVGObject.node;
			var $rectangleSVGNativeObject = $(rectangleSVGNativeObject);
			$rectangleSVGNativeObject.qtip( toolTipParams );
			
		} else {
			
			if ( segments != undefined && segments.length > 0 ) {

				for ( var k = 0; k < segments.length; k++ ) {

					var segment = segments[ k ];
					var start = segment.startPosition;
					var end = segment.endPosition;


					if ( segment.type === BETA_SHEET ) {

						blockColor = "#0000FF";// { red: 0, green: 0, blue: 255 };

						typeTooltipText = TOOL_TIP_BETA_SHEET;

					} else { //  type === ALPHA_HELIX )

						blockColor =  "#009600";// { red: 0, green: 150, blue: 0 };

						typeTooltipText = TOOL_TIP_ALPHA_HELIX;
					}

					var drawAnnotationRectangle_Params = {

							protein : protein,
							start : start,
							end : end,
							proteinBarRowIndex : proteinBarRowIndex,
							blockColor : blockColor,

							svgRootSnapSVGObject : svgRootSnapSVGObject
					};

					var rectangleSnapSVGObject = drawAnnotationRectangle( drawAnnotationRectangle_Params );



					var toolTipText = 'Secondary Structure: ' + typeTooltipText + '<br>start: ' + start + ', end: ' + end;

					var toolTipParams = {
							content: {
								text: toolTipText
							},
							position: {
								target: 'mouse'
									,
									adjust: { x: 5, y: 5 } // Offset it slightly from under the mouse
							}
					};

					var rectangleSVGNativeObject = rectangleSnapSVGObject.node;
					var $rectangleSVGNativeObject = $(rectangleSVGNativeObject);
					$rectangleSVGNativeObject.qtip( toolTipParams );				
				}
			}
		}
	}
}



////////////////////////////////////////////////////////////

///   Draws an Annotation Rectangle under the Protein Bar and returns the Snap SVG Object

function drawAnnotationRectangle( params ) {
	
	var protein = params.protein;
	var start = params.start;
	var end = params.end;
	var proteinBarRowIndex = params.proteinBarRowIndex;
	var blockColor = params.blockColor;
	
	var svgRootSnapSVGObject = params.svgRootSnapSVGObject;

	var sx = translateSequenceCoveragePositionToXCoordinate( protein, start, _TRANSLATE_SEQUENCE_COVERAGE_POSITION_TO_X_COORDINATES_SIDE_START );
	var ex = translateSequenceCoveragePositionToXCoordinate( protein, end, _TRANSLATE_SEQUENCE_COVERAGE_POSITION_TO_X_COORDINATES_SIDE_END );


	var rectX = sx;
	var rectY = _OFFSET_FROM_TOP + ( _singleProteinBarOverallHeight * proteinBarRowIndex /* i */ ) - 10;
	var rectWidth = Math.abs( ex - sx );
	var rectHeight = _SINGLE_PROTEIN_BAR_HEIGHT + 20;
	
	if ( isProteinReversed( protein ) ) {
		
		rectX = rectX - rectWidth;
	}
	
	var rectangleSnapSVGObject = svgRootSnapSVGObject.rect( rectX, rectY, rectWidth, rectHeight );

	rectangleSnapSVGObject.attr( { fill: blockColor, "fill-opacity": 0.15 } );
	
	return rectangleSnapSVGObject;
}



////////////////////////////////////////////////////////////

//Add All Protein Bar Groups ( all of: rectangle and protein name label and cover transparent rectangle )

function addAllProteinBarGroups( selectedProteins, svgRootSnapSVGObject, $merged_image_svg_jq ) {


	var rightMostEdgeOfAllElementsThisFunction = 0;
	
	
	var proteinNamePositionLeftSelected = false;
	var showProteinTerminiSelected = false;
	
	
	if ( $( "input#protein_names_position_left" ).is( ':checked' ) ) {

		proteinNamePositionLeftSelected = true;
	}
	
	if ( $( "input#show-protein-termini" ).is( ':checked' ) ) {

		showProteinTerminiSelected = true;
	}
	

	// draw protein bar groups
	for ( var i = 0; i < selectedProteins.length; i++ ) {

		var protein = selectedProteins[ i ];

		var addSingleProteinBarGroupResponse = addSingleProteinBarGroup( i, protein, proteinNamePositionLeftSelected, showProteinTerminiSelected, svgRootSnapSVGObject, $merged_image_svg_jq );

		var rightMostEdgeOfAllElementsThisAddedSingleProteinBarGroup = addSingleProteinBarGroupResponse.rightMostEdgeOfAllElementsThisFunction;

		if ( rightMostEdgeOfAllElementsThisAddedSingleProteinBarGroup > rightMostEdgeOfAllElementsThisFunction ) {

			rightMostEdgeOfAllElementsThisFunction = rightMostEdgeOfAllElementsThisAddedSingleProteinBarGroup;
		} 
	}



	var functionResponseObject = {

			rightMostEdgeOfAllElementsThisFunction : rightMostEdgeOfAllElementsThisFunction
	};

	return functionResponseObject;

}



//  Add a single Protein Bar Group ( rectangle and protein name label and cover transparent rectangle )

function addSingleProteinBarGroup( i, protein, proteinNamePositionLeftSelected, showProteinTerminiSelected, svgRootSnapSVGObject, $merged_image_svg_jq ) {


	var rightMostEdgeOfAllElementsThisFunction = 0;


	var groupSnapSVGForProteinBar = svgRootSnapSVGObject.g();  //  Create group for all elements for this protein bar

	var proteinOffset = getProteinOffset( protein );

	var proteinOffsetFromImageLeftEdge = proteinOffset + _proteinBarsLeftEdge;

	var rectangleStartX = proteinOffsetFromImageLeftEdge;
	var rectangleStartY = _OFFSET_FROM_TOP + ( _singleProteinBarOverallHeight * i );

	var rectanglePixelWidth = translateProteinWidthToPixelWidth( protein );
	var rectanglePixelHeight = _SINGLE_PROTEIN_BAR_HEIGHT;

	//	rectangle protein  !!!  Important, if add border to this rectangle with { stroke: "#bada55", strokeWidth: 5 }, need to add to overlay rectangle as well
	//                     !!!  Important, if change size of this rectangle, need to change overlay rectangle as well
	var proteinBarRectangleSnapSVGObject = svgRootSnapSVGObject.rect( rectangleStartX, rectangleStartY, rectanglePixelWidth, rectanglePixelHeight );

	var proteinBarColor = getProteinBarColor( i );

	proteinBarRectangleSnapSVGObject.attr({
		fill: proteinBarColor
	});


	var proteinLabelTextX = proteinOffsetFromImageLeftEdge + 5;
	var proteinLabelTextY = ( _OFFSET_FROM_TOP + _SINGLE_PROTEIN_BAR_HEIGHT - 8 ) + ( _singleProteinBarOverallHeight * i );

	
	if ( proteinNamePositionLeftSelected ) {

		//		Move the protein name to the left of the protein bar

		proteinLabelTextX = rectangleStartX - _PROTEIN_BAR_PROTEIN_NAME_GAP_WHEN_PROTEIN_NAME_TO_THE_LEFT;

	}
	
	
	var proteinName = getProteinName( protein );

	var proteinLabelTextSnapSVGObject = svgRootSnapSVGObject.text( proteinLabelTextX, proteinLabelTextY, proteinName );


	var proteinLabelAttributesToSet = {
			
			protein_id : protein,

			style: "cursor:default;"  // force standard mouse cursor (usually arrow) and suppress I beam mouse cursor since text not selectable due to drag

//				This attribute not supported to automatically update the "style"
//				cursor: "default;"  // force standard mouse cursor (usually arrow) and suppress I beam mouse cursor since text not selectable due to drag
	};

	if ( proteinNamePositionLeftSelected ) {
		
		proteinLabelAttributesToSet[ "text-anchor" ] = "end";   //  align text to the center of the horizontal position
		

	} else {

//		Set these attributes if the protein name text is on the bar
		proteinLabelAttributesToSet[ "fill" ] = 'white';
	}

	proteinLabelTextSnapSVGObject.attr( proteinLabelAttributesToSet );
	

	proteinLabelTextSnapSVGObject.addClass( _PROTEIN_BAR_PROTEIN_NAME_LABEL_CLASS );

	if ( proteinNamePositionLeftSelected ) {
		

		//  If positioned to the left, add a tool tip
		
		var proteinLabelTextPlainSVGObject = proteinLabelTextSnapSVGObject.node;
		var $proteinLabelTextPlainSVGObject = $( proteinLabelTextPlainSVGObject );
		
		var protein_id = protein;
		
		addSingleTooltipForProteinName( {$elementToAddToolTipTo: $proteinLabelTextPlainSVGObject, proteinIdString: protein_id } );
	}
	

	//	Put the elements in a group

	groupSnapSVGForProteinBar.add( proteinBarRectangleSnapSVGObject, proteinLabelTextSnapSVGObject );


	
	if ( showProteinTerminiSelected ) {
		
		//  TODO  arbitrary position for now
		
		//  TODO  Need to detect when to swap left and right
		
		var protein_Left_TerminusStartX = proteinOffsetFromImageLeftEdge - _PROTEIN_TERMINUS_BAR_HORIZONTAL_OFFSET;

		var protein_Left_TerminusStartY = _OFFSET_FROM_TOP + ( _singleProteinBarOverallHeight * i ) 
			+ _SINGLE_PROTEIN_BAR_HEIGHT + _PROTEIN_TERMINUS_BAR_VERTICAL_OFFSET;
		

		
		var leftTerminusLabel = _PROTEIN_TERMINUS_LABEL_N;
		var rightTerminusLabel = _PROTEIN_TERMINUS_LABEL_C;

		if ( isProteinReversed( protein ) ) {
			
			leftTerminusLabel = _PROTEIN_TERMINUS_LABEL_C;
			rightTerminusLabel = _PROTEIN_TERMINUS_LABEL_N;
		}
		
		var protein_Left_TerminusTextSnapSVGObject = svgRootSnapSVGObject.text( protein_Left_TerminusStartX, protein_Left_TerminusStartY, leftTerminusLabel );

		proteinLabelAttributesToSet = { "text-anchor" : "end" };   //  align text to the right of the horizontal position
		protein_Left_TerminusTextSnapSVGObject.attr( proteinLabelAttributesToSet ); 

		
		
		var protein_Right_TerminusStartX = proteinOffsetFromImageLeftEdge + rectanglePixelWidth + _PROTEIN_TERMINUS_BAR_HORIZONTAL_OFFSET;

		var protein_Right_TerminusStartY = _OFFSET_FROM_TOP + ( _singleProteinBarOverallHeight * i ) 
			+ _SINGLE_PROTEIN_BAR_HEIGHT + _PROTEIN_TERMINUS_BAR_VERTICAL_OFFSET;
		
		var protein_Right_TerminusTextSnapSVGObject = svgRootSnapSVGObject.text( protein_Right_TerminusStartX, protein_Right_TerminusStartY, rightTerminusLabel );

		proteinLabelAttributesToSet = { "text-anchor" : "start" };   //  align text to the left of the horizontal position
		protein_Right_TerminusTextSnapSVGObject.attr( proteinLabelAttributesToSet ); 
				

		groupSnapSVGForProteinBar.add( protein_Left_TerminusTextSnapSVGObject, protein_Right_TerminusTextSnapSVGObject );
		
		var protein_Right_TerminusTextSnapSVGObjectBBox = protein_Right_TerminusTextSnapSVGObject.getBBox();

		var protein_Right_TerminusTextRightSide = protein_Right_TerminusTextSnapSVGObjectBBox.x2;
		
		if ( protein_Right_TerminusTextRightSide > rightMostEdgeOfAllElementsThisFunction ) {
			
			rightMostEdgeOfAllElementsThisFunction = protein_Right_TerminusTextRightSide;
		}
	}
	

	
	var linkablePositionsSelected = false;
	

	// if requested, show linkable-positions
	if ( $( "input#show-linkable-positions" ).is( ':checked' ) ) {
		
		linkablePositionsSelected = true;
	}
	

	
	var trypticCleavagePositionsSelected = false;
	

	// if requested, show linkable-positions
	if ( $( "input#show-tryptic-cleavage-positions" ).is( ':checked' ) ) {
		
		trypticCleavagePositionsSelected = true;
	}
	
	
	//  Draw linkable-positions lines here so under clear overlay
	
	// if requested, show linkable-positions
	if ( linkablePositionsSelected ) {
		
//		for ( var i = 0; i < selectedProteins.length; i++ ) {

			var lineColor = "#FFFFFF";
			var fromY;
			
			if ( trypticCleavagePositionsSelected ) {
				// Also drawing tryptic positions so draw these on the top half of the bar
				fromY = _OFFSET_FROM_TOP + ( _singleProteinBarOverallHeight * i ) + ( _SINGLE_PROTEIN_BAR_HEIGHT / 2 );
			} else {
				fromY = _OFFSET_FROM_TOP + ( _singleProteinBarOverallHeight * i ) + _SINGLE_PROTEIN_BAR_HEIGHT;
			}
			var toY = _OFFSET_FROM_TOP + ( _singleProteinBarOverallHeight * i );

//			var protein = selectedProteins[ i ];

			var positions = _linkablePositions[ protein ];

			if ( positions !== undefined ) { 

				for ( var k = 0; k < positions.length; k++ ) {

					var x = translatePositionToXCoordinate( protein, positions[ k ] );

					var lineSnapSVGObject = svgRootSnapSVGObject.line( x, fromY, x, toY );
					lineSnapSVGObject.attr({
						stroke: lineColor,
						strokeWidth: 1,
						fill: "none"
					});
					
					groupSnapSVGForProteinBar.add( lineSnapSVGObject );
					
				}
			}
			
//		}
	}
	
	

	//  Draw show-tryptic-cleavage-positions lines here so under clear overlay
	
	// if requested, show show-tryptic-cleavage-positions
	if ( trypticCleavagePositionsSelected ) {
		
//		for ( var i = 0; i < selectedProteins.length; i++ ) {

			var lineColor = "#FFFF00";
			var fromY = _OFFSET_FROM_TOP + ( _singleProteinBarOverallHeight * i ) + _SINGLE_PROTEIN_BAR_HEIGHT;
			var toY;
			if ( linkablePositionsSelected ) {
				// Also drawing tryptic positions so draw these on the bottom half of the bar
				toY = _OFFSET_FROM_TOP + ( _singleProteinBarOverallHeight * i ) + ( _SINGLE_PROTEIN_BAR_HEIGHT / 2 );
			} else {
				toY = _OFFSET_FROM_TOP + ( _singleProteinBarOverallHeight * i );
			}

//			var protein = selectedProteins[ i ];

			var positions = _proteinSequenceTrypsinCutPoints[ protein ];

			if ( positions !== undefined ) { 

				for ( var k = 0; k < positions.length; k++ ) {

					var x = translatePositionToXCoordinate( protein, positions[ k ] );

					var lineSnapSVGObject = svgRootSnapSVGObject.line( x, fromY, x, toY );
					lineSnapSVGObject.attr({
						stroke: lineColor,
						strokeWidth: 1,
						fill: "none"
					});
					
					groupSnapSVGForProteinBar.add( lineSnapSVGObject );
					
				}
			}
			
//		}
	}
		

	////////////////////////////////
	
	//////  Add vertical line over bar that indicates mouse current position
	

	/////////////    Build the group that will be the mouse over info

	var fromY = _OFFSET_FROM_TOP + ( _singleProteinBarOverallHeight * i ) + _SINGLE_PROTEIN_BAR_HEIGHT;
	var toY = _OFFSET_FROM_TOP + ( _singleProteinBarOverallHeight * i );

	var mousePositionLineSnapSVGObject = svgRootSnapSVGObject.line( rectangleStartX, fromY, rectangleStartX, toY );
	mousePositionLineSnapSVGObject.attr({
		stroke: _MOUSE_POSITION_ON_PROTEIN_BAR_COLOR,
		strokeWidth: 0   // hide when create
	});

	//	Add mousePositionLineSnapSVGObject to the main group for this Protein Bar Block

	groupSnapSVGForProteinBar.add( mousePositionLineSnapSVGObject );


	/////////////    END:  Build the group that will be the mouse over info
	
	

	//	Create overlay transparent rectangle to pick up mouseover events
	
	//	!!!  Important, if add border to main rectangle with { stroke: "#bada55", strokeWidth: 5 }, need to add to this rectangle as well and make opacity "0"
	//  !!!  Important, if change size of this rectangle, need to change main rectangle (above) as well
	//	Overlay rectangle for protein.  
	var proteinBarOverlayRectangleSnapSVGObject = svgRootSnapSVGObject.rect( rectangleStartX, rectangleStartY, rectanglePixelWidth, rectanglePixelHeight );

	
	//	Put overlay transparent rectangle in the main group for this Protein Bar

	groupSnapSVGForProteinBar.add( proteinBarOverlayRectangleSnapSVGObject );
	
	
	
	//	make fill totally clear but still pick up mouse events. { fill: "none" } does not pick up mouse events
	proteinBarOverlayRectangleSnapSVGObject.attr( { "fill-opacity": "0" } );  

	proteinBarOverlayRectangleSnapSVGObject.attr( { "protein_id": protein, "protein_index" : i } );  

	proteinBarOverlayRectangleSnapSVGObject.addClass( _PROTEIN_BAR_OVERLAY_RECTANGLE_LABEL_CLASS );

	
	
	var proteinBarOverlayRectanglePlainSVGObject = proteinBarOverlayRectangleSnapSVGObject.node;
	
	var $proteinBarOverlayRectanglePlainSVGObject = $( proteinBarOverlayRectanglePlainSVGObject );
	
	
	
	///   function to create the tool tip contents and update the tool tip.  Called on mouseover and mousemove.
	
	var proteinBarOverlayRectangleToolTipTextFcn = function( eventObject, qtipAPI /* qtip "api" variable/property */ ) {
		
		// get mouse position, not using the "Y" axis value
		var eventpageX = eventObject.pageX;
//		var eventpageY = eventObject.pageY;

		var rectangleOffset = $proteinBarOverlayRectanglePlainSVGObject.offset(); // returns an object containing the properties top and left.

		var rectangleOffsetLeft = rectangleOffset.left;
		//		var rectangleOffsetTop = rectangleOffset.top; // not using the "Y" axis value


		var elementNewPositionX = eventpageX - rectangleOffsetLeft;  //  compute mouse position relative to protein bar rectangle



		var proteinSequence = _proteinSequences[ protein ];
		var proteinSequenceLength  = proteinSequence.length;
		
		
		//  convert elementNewPositionX to sequence position
		var multiplier = getMultiplier();

		var sequencePositionOneBased = Math.round( ( elementNewPositionX / multiplier ) - 0.5 ) + 1; //  Add 1 for display since protein positions start at "1"


		if ( isProteinReversed( protein ) ) {
			
			sequencePositionOneBased = proteinSequenceLength - sequencePositionOneBased + 1;
		}
		
		if ( sequencePositionOneBased > proteinSequenceLength ) {
			
			sequencePositionOneBased = proteinSequenceLength;
		}
		
		var sequencePositionZeroBased = sequencePositionOneBased - 1; //  subtract 1 for indexing since charAt starts at zero
		
		
		var sequenceAtPosition = proteinSequence.charAt( ( sequencePositionZeroBased ) );  
		
		//  Default sequences left and right to "" since may be at either edge.
		
		var sequenceAtPositionLeft1 = "";
		var sequenceAtPositionLeft2 = "";
		var sequenceAtPositionLeft3 = "";

		var sequenceAtPositionRight1 = "";
		var sequenceAtPositionRight2 = "";
		var sequenceAtPositionRight3 = "";
		
		if ( sequencePositionZeroBased > 0 ) {
			
			sequenceAtPositionLeft1 = proteinSequence.charAt( ( sequencePositionZeroBased - 1 ) );  
		}

		if ( sequencePositionZeroBased > 1 ) {
			
			sequenceAtPositionLeft2 = proteinSequence.charAt( ( sequencePositionZeroBased - 2 ) );  
		}
		if ( sequencePositionZeroBased > 2 ) {
			
			sequenceAtPositionLeft3 = proteinSequence.charAt( ( sequencePositionZeroBased - 3 ) );  
		}
		
		if ( sequencePositionZeroBased < proteinSequenceLength - 1 ) {
			
			sequenceAtPositionRight1 = proteinSequence.charAt( ( sequencePositionZeroBased + 1 ) );  
		}

		if ( sequencePositionZeroBased < proteinSequenceLength - 2 ) {
			
			sequenceAtPositionRight2 = proteinSequence.charAt( ( sequencePositionZeroBased + 2 ) );  
		}
		if ( sequencePositionZeroBased < proteinSequenceLength - 3 ) {
			
			sequenceAtPositionRight3 = proteinSequence.charAt( ( sequencePositionZeroBased + 3 ) );  
		}
		
		if ( isProteinReversed( protein ) ) {
			
			//  If protein reversed, reset everything and reverse it

			sequenceAtPositionLeft1 = "";
			sequenceAtPositionLeft2 = "";
			sequenceAtPositionLeft3 = "";

			sequenceAtPositionRight1 = "";
			sequenceAtPositionRight2 = "";
			sequenceAtPositionRight3 = "";
			
			if ( sequencePositionZeroBased > 0 ) {
				
				sequenceAtPositionRight1 = proteinSequence.charAt( ( sequencePositionZeroBased - 1 ) );  
			}

			if ( sequencePositionZeroBased > 1 ) {
				
				sequenceAtPositionRight2 = proteinSequence.charAt( ( sequencePositionZeroBased - 2 ) );  
			}
			if ( sequencePositionZeroBased > 2 ) {
				
				sequenceAtPositionRight3 = proteinSequence.charAt( ( sequencePositionZeroBased - 3 ) );  
			}
			
			if ( sequencePositionZeroBased < proteinSequenceLength - 1 ) {
				
				sequenceAtPositionLeft1 = proteinSequence.charAt( ( sequencePositionZeroBased + 1 ) );  
			}

			if ( sequencePositionZeroBased < proteinSequenceLength - 2 ) {
				
				sequenceAtPositionLeft2 = proteinSequence.charAt( ( sequencePositionZeroBased + 2 ) );  
			}
			if ( sequencePositionZeroBased < proteinSequenceLength - 3 ) {
				
				sequenceAtPositionLeft3 = proteinSequence.charAt( ( sequencePositionZeroBased + 3 ) );  
			}
			
			
		}
		
		var proteinNameDisplay = " display:none; ";
		

		if ( linkablePositionsSelected && ( ! proteinNamePositionLeftSelected ) ) {
			
			proteinNameDisplay = " ";	
		}

	
		var cutPointBetweenCenterAndFirstLeft = false;
		var cutPointBetweenFirstLeftAndSecondLeft = false;
		var cutPointBetweenSecondLeftAndThirdLeft = false;
		
		var cutPointBetweenCenterAndFirstRight = false;
		var cutPointBetweenFirstRightAndSecondRight = false;
		var cutPointBetweenSecondRightAndThirdRight = false;
		
		
		
		var proteinSequenceTrypsinCutPoints = _proteinSequenceTrypsinCutPoints[ protein ];
		
		
		for ( var proteinSequenceTrypsinCutPointsIndex = 0; proteinSequenceTrypsinCutPointsIndex < proteinSequenceTrypsinCutPoints.length; proteinSequenceTrypsinCutPointsIndex++ ) {
		
			//  trypsinCutPoint is "1" based 
			
			var trypsinCutPoint = proteinSequenceTrypsinCutPoints[ proteinSequenceTrypsinCutPointsIndex ];

			if ( trypsinCutPoint > sequencePositionOneBased - 1 && trypsinCutPoint < sequencePositionOneBased ) {
				cutPointBetweenCenterAndFirstLeft = true;
			}
			if ( trypsinCutPoint > sequencePositionOneBased - 2 && trypsinCutPoint < sequencePositionOneBased - 1 ) {
				cutPointBetweenFirstLeftAndSecondLeft = true;
			}
			if ( trypsinCutPoint > sequencePositionOneBased - 3 && trypsinCutPoint < sequencePositionOneBased - 2 ) {
				cutPointBetweenSecondLeftAndThirdLeft = true;
			}

			if ( trypsinCutPoint > sequencePositionOneBased && trypsinCutPoint < sequencePositionOneBased + 1 ) {
				cutPointBetweenCenterAndFirstRight = true;
			}
			if ( trypsinCutPoint > sequencePositionOneBased + 1 && trypsinCutPoint < sequencePositionOneBased + 2 ) {
				cutPointBetweenFirstRightAndSecondRight = true;
			}
			if ( trypsinCutPoint > sequencePositionOneBased + 2 && trypsinCutPoint < sequencePositionOneBased + 3 ) {
				cutPointBetweenSecondRightAndThirdRight = true;
			}
		
		
		}
		
		
		
		//  Hard coded testing at position 60
		
//		if ( sequencePositionOneBased === 60 ) {
//			
//			cutPointBetweenCenterAndFirstLeft = true;
//			cutPointBetweenFirstLeftAndSecondLeft = true;
//			cutPointBetweenSecondLeftAndThirdLeft = true;
//
//			cutPointBetweenCenterAndFirstRight = true;
//			cutPointBetweenFirstRightAndSecondRight = true;
//			cutPointBetweenSecondRightAndThirdRight = true;
//		}
	
		
		//  name of css class in global.css.  Using a css class here since this is tool tip SVG, not main SVG
		
		var textClassForLinkablePosition = "proxl-primary-color-bold-svg-text";

		var textClassAtPosition = "";

		var textClassAtPositionLeft1 = "";
		var textClassAtPositionLeft2 = "";
		var textClassAtPositionLeft3 = "";

		var textClassAtPositionRight1 = "";
		var textClassAtPositionRight2 = "";
		var textClassAtPositionRight3 = "";
		


		var linkablePositionsForProtein = _linkablePositions[ protein ];
		
		
		for ( var linkablePositionsForProteinIndex = 0; linkablePositionsForProteinIndex < linkablePositionsForProtein.length; linkablePositionsForProteinIndex++ ) {
		
			//  linkablePositionsForProtein is "1" based 
			
			var linkablePosition = linkablePositionsForProtein[ linkablePositionsForProteinIndex ];

			if ( linkablePosition === sequencePositionOneBased ) {
				textClassAtPosition = textClassForLinkablePosition;
			}

			if ( linkablePosition === sequencePositionOneBased - 1 ) {
				textClassAtPositionLeft1 = textClassForLinkablePosition;
			}
			if ( linkablePosition === sequencePositionOneBased - 2 ) {
				textClassAtPositionLeft2 = textClassForLinkablePosition;
			}
			if ( linkablePosition === sequencePositionOneBased - 3 ) {
				textClassAtPositionLeft3 = textClassForLinkablePosition;
			}

			if ( linkablePosition === sequencePositionOneBased + 1 ) {
				textClassAtPositionRight1 = textClassForLinkablePosition;
			}
			if ( linkablePosition === sequencePositionOneBased + 2 ) {
				textClassAtPositionRight2 = textClassForLinkablePosition;
			}
			if ( linkablePosition === sequencePositionOneBased + 3 ) {
				textClassAtPositionRight3 = textClassForLinkablePosition;
			}
		
		
		}		
		
	
		
		///////////
		
		//   New tool tip contents:
			
		var tooltipDataObject = {
				
				sequencePosition: sequencePositionOneBased,
				
				proteinName: proteinName,
				proteinNameDisplay: proteinNameDisplay,
				
				sequenceAtPosition: sequenceAtPosition,
				sequenceAtPositionLeft1: sequenceAtPositionLeft1,
				sequenceAtPositionLeft2: sequenceAtPositionLeft2,
				sequenceAtPositionLeft3: sequenceAtPositionLeft3,
				sequenceAtPositionRight1: sequenceAtPositionRight1,
				sequenceAtPositionRight2: sequenceAtPositionRight2,
				sequenceAtPositionRight3: sequenceAtPositionRight3,
				
				cutPointBetweenCenterAndFirstLeft: cutPointBetweenCenterAndFirstLeft,
				cutPointBetweenFirstLeftAndSecondLeft: cutPointBetweenFirstLeftAndSecondLeft,
				cutPointBetweenSecondLeftAndThirdLeft: cutPointBetweenSecondLeftAndThirdLeft,

				cutPointBetweenCenterAndFirstRight: cutPointBetweenCenterAndFirstRight,
				cutPointBetweenFirstRightAndSecondRight: cutPointBetweenFirstRightAndSecondRight,
				cutPointBetweenSecondRightAndThirdRight: cutPointBetweenSecondRightAndThirdRight,
				
				textClassAtPosition: textClassAtPosition,
				
				textClassAtPositionLeft1: textClassAtPositionLeft1,
				textClassAtPositionLeft2: textClassAtPositionLeft2,
				textClassAtPositionLeft3: textClassAtPositionLeft3,
				
				textClassAtPositionRight1: textClassAtPositionRight1,
				textClassAtPositionRight2: textClassAtPositionRight2,
				textClassAtPositionRight3: textClassAtPositionRight3
		

		};
			
		//  Use Handlebars libary to convert the template into HTML, performing substitutions using tooltipDataObject
			
		var tooltipContentsHTML = _proteinBarToolTip_template_HandlebarsTemplate( tooltipDataObject );


		
		//  Update tool tip contents
		
		qtipAPI.set('content.text', tooltipContentsHTML );

//		$("#TEMPHOLDER_TOOLTIP").html( tooltipContentsHTML );  // place the contents of the tool tip in a div for easier inspection and debugging

		//		Fortunately, jQuery normalizes the .pageX and .pageY properties so that they can be used in all browsers. These properties provide the X and Y coordinates of the mouse pointer relative to the top-left corner of the document, as illustrated in the example output above.

		//		var pageCoords = "( " + event.pageX + ", " + event.pageY + " )";
		//		var clientCoords = "( " + event.clientX + ", " + event.clientY + " )";
		
	};
	
	//  Add the qtip tool tip to the protein bar overlay rectangle
	
	$proteinBarOverlayRectanglePlainSVGObject.qtip({
		content: {
			text: proteinBarOverlayRectangleToolTipTextFcn
		},
		position: {
			my: 'top center', //  center the tool tip under the mouse and place the call out arrow in the center top of the tool tip box
			target: 'mouse'
				,
				adjust: { x: 0, y: 10 } // Offset it from under the mouse
		}
	});

	
	
	// Grab the first element in the tooltips array and access its qTip API
	var qtipAPI = $proteinBarOverlayRectanglePlainSVGObject.qtip('api');
	
	
	//  Add a mouse move to the protein bar overlay rectangle to update the contents of the qtip tool tip 
	$proteinBarOverlayRectanglePlainSVGObject.mousemove( function( eventObject ) {

		proteinBarOverlayRectangleToolTipTextFcn( eventObject, qtipAPI );
	} );

	
	
	
	///////////////////////////////////////////

	//   Add mouse handlers to overlay transparent rectangle for managing "mousePositionLineSnapSVGObject" position


	////////

	var updateMousePositionLineSnapSVGObjectPosition = function ( mousePositionRelativeToContainingRectangleX  ) {

		var elementNewPositionX = mousePositionRelativeToContainingRectangleX - 0.5;

		mousePositionLineSnapSVGObject.transform( "t" + elementNewPositionX );
	};

	$proteinBarOverlayRectanglePlainSVGObject.mouseover(  function( eventObject ) {

		var $rectangle = $( this ); 

		var rectangleOffset = $rectangle.offset(); // Get current positon of rectangle, returns an object containing the properties top and left.

		var rectangleOffsetLeft = rectangleOffset.left;
		//		var rectangleOffsetTop = rectangleOffset.top;

		var eventpageX = eventObject.pageX;
//		var eventpageY = eventObject.pageY;

		var elementNewPositionX = eventpageX - rectangleOffsetLeft;

		updateMousePositionLineSnapSVGObjectPosition( elementNewPositionX );

		//	make line visible.
		mousePositionLineSnapSVGObject.attr( { strokeWidth: 1	} );
	});

	$proteinBarOverlayRectanglePlainSVGObject.mousemove( function( eventObject ) {

		var eventpageX = eventObject.pageX;
//		var eventpageY = eventObject.pageY;

		var $rectangle = $( this );

		var rectangleOffset = $rectangle.offset(); // returns an object containing the properties top and left.

		var rectangleOffsetLeft = rectangleOffset.left;
		//		var rectangleOffsetTop = rectangleOffset.top;

		var elementNewPositionX = eventpageX - rectangleOffsetLeft;

		updateMousePositionLineSnapSVGObjectPosition( elementNewPositionX );

		//		Fortunately, jQuery normalizes the .pageX and .pageY properties so that they can be used in all browsers. These properties provide the X and Y coordinates of the mouse pointer relative to the top-left corner of the document, as illustrated in the example output above.

		//		var pageCoords = "( " + event.pageX + ", " + event.pageY + " )";
		//		var clientCoords = "( " + event.clientX + ", " + event.clientY + " )";
	} );



	$proteinBarOverlayRectanglePlainSVGObject.mouseout( function( eventObject ) {


//		var eventpageX = eventObject.pageX;
//		var eventpageY = eventObject.pageY;

		//	hide line.
		mousePositionLineSnapSVGObject.attr( { strokeWidth: 0 } );
			
	});
	
	
	
	
//	  This has been replaced with a tool tip in a floating div 
//	This is for showing info embedded into the main SVG image.
//	
//	if ( _SHOW_SEQUENCE_POSITION_TOOLTIP_ON_PROTEIN_BARS ) {
//
//		///////
//
//		//   Add mouse handlers to overlay transparent rectangle for managing "groupMouseOverInfoBlock" content and position
//
//
//		var proteinBarOverlayRectanglePlainSVGObject = proteinBarOverlayRectangleSnapSVGObject.node;
//
//		var $proteinBarOverlayRectanglePlainSVGObject = $( proteinBarOverlayRectanglePlainSVGObject );
//
//		////////
//
//		var updatePositionAndPositionText = function ( mousePositionRelativeToContainingRectangleX  ) {
//
//
//
//			var mouseOverAddElementPlainSVGObject = mouseOverAddElementSnapSVGObject.node;
//
//			var $mouseOverAddElementPlainSVGObject = $( mouseOverAddElementPlainSVGObject );
//
//			var $position_number_jq = $mouseOverAddElementPlainSVGObject.find(".position_number_jq");
//
//			//  convert elementNewPositionX to sequence position
//			var multiplier = getMultiplier();
//
//			var sequencePosition = Math.round( mousePositionRelativeToContainingRectangleX / multiplier ) + 1; //  Add 1 since starts at "1"
//
//			$position_number_jq.text( sequencePosition );
//
//			//////  Assume base location is left edge of Protein Bar Rectangle, see above where this text element is created
//
//			var groupMouseOverInfoBlockBBox = groupMouseOverInfoBlock.getBBox();
//
//			var groupMouseOverInfoBlockWidth = groupMouseOverInfoBlockBBox.width;
//
//			var groupMouseOverInfoBlockWidthHalf = ( groupMouseOverInfoBlockWidth / 2 ) + 3; // add 3 for fudge factor
//
//			var minElementPosition = groupMouseOverInfoBlockWidthHalf;
//
//			var maxElementPosition = rectanglePixelWidth - groupMouseOverInfoBlockWidthHalf;
//
//
//			var elementNewPositionX = mousePositionRelativeToContainingRectangleX;
//
//			if ( elementNewPositionX < minElementPosition ) {
//
//				elementNewPositionX = minElementPosition;
//
//			} else if ( elementNewPositionX > maxElementPosition ) {
//
//				elementNewPositionX = maxElementPosition;
//			}
//
//			groupMouseOverInfoBlock.transform( "t" + elementNewPositionX );
//
//
//
//		};
//
//
//
//		//  Not usable since it does not update the tool tip text after the tool tip is first generated
////		$proteinBarOverlayRectanglePlainSVGObject.qtip({
//
////		content: {
//
////		text: function(event, api) {
//
////		setTimeout( function() {
////		//  use the "api" object to update the tip later
////		api.set('content.text', "Later Updated Tip: pageX: " + event.pageX );
////		}, 5000);
//
////		return "Generic Tooltip: pageX: " + event.pageX;  
//
//
////		}
////		},
////		position: {
////		target: 'mouse'
////		,
////		adjust: { x: 5, y: 5 } // Offset it slightly from under the mouse
////		}
//////		position: {
//////		my: 'bottom left',
//////		at: 'top right',
//////		viewport: $(window)
//////		}
////		});
//
//
//		$proteinBarOverlayRectanglePlainSVGObject.mouseover(  function( eventObject ) {
//
//			var $rectangle = $( this ); 
//
//			//		var protein_id = $rectangle.attr("protein_id");
//			//
//			//		var proteinName = getProteinName( protein_id );
//
//			var rectangleOffset = $rectangle.offset(); // Get current positon of rectangle, returns an object containing the properties top and left.
//
//			var rectangleOffsetLeft = rectangleOffset.left;
//			//		var rectangleOffsetTop = rectangleOffset.top;
//
//
//			//		var overallSVGImageOffset = $merged_image_svg_jq.offset();
//			//
//			//		var overallSVGImageOffsetLeft = overallSVGImageOffset.left;
//			//		var overallSVGImageOffsetTop = overallSVGImageOffset.top;
//
//			var eventpageX = eventObject.pageX;
////			var eventpageY = eventObject.pageY;
//
//
//			var elementNewPositionX = eventpageX - rectangleOffsetLeft;
//
//			updatePositionAndPositionText( elementNewPositionX );
//
//			groupMouseOverInfoBlock.attr( { "display" : "" } );				//  show
//
//			//		$("#TEMPHOLDER").prepend("<div >Mouse EEEE Enter:  eventpageX: " + eventpageX + ", eventpageY: " + eventpageY + "</div>");
//
//		} );
//
//
//		$proteinBarOverlayRectanglePlainSVGObject.mousemove( function( eventObject ) {
//
//			var eventpageX = eventObject.pageX;
////			var eventpageY = eventObject.pageY;
//
//			//  $("#TEMPHOLDER").prepend("<div >Mouse MMMM  Move:  eventpageX: " + eventpageX + ", eventpageY: " + eventpageY + "</div>");
//
//			var $rectangle = $( this );
//
//			var rectangleOffset = $rectangle.offset(); // returns an object containing the properties top and left.
//
//			var rectangleOffsetLeft = rectangleOffset.left;
//			//		var rectangleOffsetTop = rectangleOffset.top;
//
//			var elementNewPositionX = eventpageX - rectangleOffsetLeft;
//
//			groupMouseOverInfoBlock.transform( "t" + elementNewPositionX );
//
//
//			updatePositionAndPositionText( elementNewPositionX );
//
//
//
//			//		Fortunately, jQuery normalizes the .pageX and .pageY properties so that they can be used in all browsers. These properties provide the X and Y coordinates of the mouse pointer relative to the top-left corner of the document, as illustrated in the example output above.
//
//			//		var pageCoords = "( " + event.pageX + ", " + event.pageY + " )";
//			//		var clientCoords = "( " + event.clientX + ", " + event.clientY + " )";
//		} );
//
//
//
//		$proteinBarOverlayRectanglePlainSVGObject.mouseout( function( eventObject ) {
//
//
////			var eventpageX = eventObject.pageX;
////			var eventpageY = eventObject.pageY;
//
//			groupMouseOverInfoBlock.attr("display","none");  // hide
//
//			//  $("#TEMPHOLDER").prepend("<div >Mouse OOOOO Out:  eventpageX: " + eventpageX + ", eventpageY: " + eventpageY + "</div>");
//
//		});
//	}

	//  END  Add mouse handlers to overlay transparent rectangle for managing "groupMouseOverInfoBlock" content and position

	///////
	
	///  add tool tip to group
	
	
	
	
	////////


	addClickDoubleClickTo( groupSnapSVGForProteinBar, protein, i );
	
	addDragTo( groupSnapSVGForProteinBar, protein );



	var rightEdgeOfRectangle = rectangleStartX + rectanglePixelWidth - 1;

	if ( rightEdgeOfRectangle > rightMostEdgeOfAllElementsThisFunction ) {

		rightMostEdgeOfAllElementsThisFunction = rightEdgeOfRectangle;
	} 


	var functionResponseObject = {

			rightMostEdgeOfAllElementsThisFunction : rightMostEdgeOfAllElementsThisFunction
	};

	return functionResponseObject;
}



///////////////////////////////////////////////////////////////////////

function drawScaleBar( selectedProteins, scalebarIncrement, svgRootSnapSVGObject ) {


	var rightMostEdgeOfAllElementsThisFunction = 0;
	
	
	var startx = translateScaleBarPositionToXCoordinate( 1 );
	var basey = _OFFSET_FROM_TOP + ( ( selectedProteins.length - 1 ) * _singleProteinBarOverallHeight + _SINGLE_PROTEIN_BAR_OVERALL_HEIGHT_DEFAULT );

	var bottomOfLowestItemDrawn = basey + 40;


	var g = svgRootSnapSVGObject.g();

	var rightMostProteinPixel = Math.floor( getCurrentRightmostProteinEdge() );
	var scaleBarProteinPosition = Math.floor( getScaleBarPositionForPixel( rightMostProteinPixel ) );	//the scale bar tick mark that would represent the right most pixel		
	scaleBarProteinPosition += scalebarIncrement - ( scaleBarProteinPosition % scalebarIncrement );					// set the scale bar position to the nearest multiple of 50 beyond the right-most protein edge
	var endx = translateScaleBarPositionToXCoordinate( scaleBarProteinPosition );		// get the x coord position of this tick mark, will be right edge of scale bar


	var scalebarLine = svgRootSnapSVGObject.line( startx, basey, endx, basey );
	scalebarLine.attr({
		stroke:'black',
		strokeWidth:2
	});

	g.add( scalebarLine );

	scalebarLine = svgRootSnapSVGObject.line( startx, basey - 8, startx, basey + 8 );
	scalebarLine.attr({
		stroke:'black',
		strokeWidth:2
	});
	var scalebarText = svgRootSnapSVGObject.text( startx - 4 , basey + 25, "1" );
	g.add( scalebarLine, scalebarText );


	for ( var scaleBarValue = scalebarIncrement; scaleBarValue <= scaleBarProteinPosition; scaleBarValue += scalebarIncrement ) {

		if ( scaleBarValue === 1 ) {

			continue; // already have a "1" tick mark at the left edge
		}

		var cx = translateScaleBarPositionToXCoordinate( scaleBarValue );

		scalebarLine = svgRootSnapSVGObject.line( cx, basey - 8, cx, basey + 8 );
		scalebarLine.attr({
			stroke:'black',
			strokeWidth:2
		});

		

		scalebarText = svgRootSnapSVGObject.text( cx, basey + 25, scaleBarValue );
		scalebarText.attr( { "text-anchor" : "middle" } );  //  align text to the center of the horizontal position 

		g.add( scalebarLine, scalebarText );	

		var scalebarTextBBox = scalebarText.getBBox();

		var scalebarTextRightSide = scalebarTextBBox.x2;


		if ( scalebarTextRightSide > rightMostEdgeOfAllElementsThisFunction ) {

			rightMostEdgeOfAllElementsThisFunction = scalebarTextRightSide;
		} 
	}

	var drawScaleBarResponse = { bottomOfLowestItemDrawn: bottomOfLowestItemDrawn, rightMostEdgeOfAllElementsThisFunction: rightMostEdgeOfAllElementsThisFunction };

	return drawScaleBarResponse;

}

///////////////////////////////////////////////////////////////////////

//draw interprotein crosslink line

function drawInterProteinCrosslinkLines( selectedProteins, svgRootSnapSVGObject ) {

	for ( var selectedProteinsIndex = 0; selectedProteinsIndex < selectedProteins.length; selectedProteinsIndex++ ) {

		var fromY = _OFFSET_FROM_TOP + ( _singleProteinBarOverallHeight * selectedProteinsIndex ) + _SINGLE_PROTEIN_BAR_HEIGHT;

		for ( var k = 0; k < selectedProteins.length; k++ ) {

			if ( k <= selectedProteinsIndex ) { continue; }
			if ( _proteinLinkPositions[ selectedProteins[ selectedProteinsIndex ] ] == undefined ) { continue; }
			if ( _proteinLinkPositions[ selectedProteins[ selectedProteinsIndex ] ][ selectedProteins[ k ] ] == undefined ) { continue; }

			var lineColor = getCrosslinkLineColor( selectedProteinsIndex, k );

			var toY = _OFFSET_FROM_TOP + ( _singleProteinBarOverallHeight * k );

			var fromKeys = Object.keys( _proteinLinkPositions[ selectedProteins[ selectedProteinsIndex ] ][ selectedProteins[ k ] ] );

			for ( var ii = 0; ii < fromKeys.length; ii++ ) {
				var x1 = translatePositionToXCoordinate( selectedProteins[ selectedProteinsIndex ], parseInt(fromKeys[ ii ] ) );
				var y1 = fromY;

				var tos = Object.keys( _proteinLinkPositions[ selectedProteins[ selectedProteinsIndex ] ][ selectedProteins[ k ] ][ fromKeys[ ii ] ] );
				for ( var kk = 0; kk < tos.length; kk++ ) {
					var x2 = translatePositionToXCoordinate( selectedProteins[ k ], parseInt( tos[ kk ] ) );
					var y2 = toY;


					var findSearchesForCrosslink__response = undefined;


					if ( $( "input#color-by-search" ).is( ':checked' ) ) {

						findSearchesForCrosslink__response = 
							findSearchesForCrosslink( 
									selectedProteins[ selectedProteinsIndex ], 
									selectedProteins[ k ], 
									fromKeys[ ii ], tos[ kk ] );

						lineColor = getCrosslinkLineColorForSearches( selectedProteinsIndex, k, findSearchesForCrosslink__response );
					};

					if ( lineColor === undefined ) {

						var searchesListPart = "";

						if ( findSearchesForCrosslink__response ) {

							searchesListPart = ", findSearchesForCrosslink__response: [" + findSearchesForCrosslink__response.join( ", " ) + "]";

						}

						throw "lineColor returned from getCrosslinkLineColorForSearches(...) is undefined for selectedProteinsIndex: " + selectedProteinsIndex + ", k: " + k + searchesListPart;
					}

					console.assert( lineColor != undefined );
					
					var fromP = _proteinNames[ selectedProteins[ selectedProteinsIndex ] ];
					var toP = _proteinNames[ selectedProteins[ k ] ];
					var fromPp = parseInt(fromKeys[ ii ] );
					var toPp = parseInt(tos[ kk ] );
					var lsearches = _proteinLinkPositions[ selectedProteins[ selectedProteinsIndex ] ][ selectedProteins[ k ] ][ fromKeys[ ii ] ][ tos[ kk ] ];

					var link = { };
					link.type = 'crosslink';
					link.protein1 = selectedProteins[ selectedProteinsIndex ];
					link.position1 = fromPp;
					link.protein2 = selectedProteins[ k ];
					link.position2 = toPp;
					link.searchIds = lsearches;
					
					
					var line = svgRootSnapSVGObject.line( x1, y1, x2, y2 );
					line.attr({
						stroke: lineColor,
						strokeWidth: 1,
						"stroke-opacity": getCrosslinkLineOpacity( selectedProteinsIndex, k, link ),
						'from_protein_id':selectedProteins[ selectedProteinsIndex ],
						'to_protein_id':selectedProteins[ k ],
						'fromp': fromP,
						'top': toP,
						'frompp': fromPp,
						'topp': toPp,
						'searches': lsearches,
						'linktype' : 'crosslink'
					});

					if ( lineColor !== _NOT_HIGHLIGHTED_LINE_COLOR ) {
						
						line.mouseover( function() { this.attr({ strokeWidth: 2 }); });
						line.mouseout( function() { this.attr({ strokeWidth: 1 }); });
						
//						add tooltips and click handlers to crosslinks
						
						var lineSVGNativeObject = line.node;
						
						var $lineSVGNativeObject = $(lineSVGNativeObject);

						//  add tool tip

						$lineSVGNativeObject.qtip({
							content: {
								text: 'Crosslink: ' + fromP + "(" + fromPp + ") - " + toP + "(" + toPp + ")<br>Searches: " + lsearches
							},
							position: {
								target: 'mouse'
									,
									adjust: { x: 5, y: 5 } // Offset it slightly from under the mouse
							}
						});


						//  Add click handler
						
						addClickHandler__InterProteinCrosslinkLines( $lineSVGNativeObject );

					}
				}
			}
		}
	}

}



function addClickHandler__InterProteinCrosslinkLines( $SVGNativeObject ) {
	
	//  Add click handler

	$SVGNativeObject.click( function(  ) {

		processClickOnCrossLink( this );

	});
}



///////////////////////////////////////////////////////////////////////

// draw self-crosslinks

function drawSelfProteinCrosslinkLines( selectedProteins, svgRootSnapSVGObject ) {

	for ( var i = 0; i < selectedProteins.length; i++ ) {

		if ( _proteinLinkPositions[ selectedProteins[ i ] ] == undefined ) { continue; }

		var lineColor = getColor( i );
		var fromY = _OFFSET_FROM_TOP + ( _singleProteinBarOverallHeight * i );
		var toY =   _OFFSET_FROM_TOP + ( _singleProteinBarOverallHeight * i );

		if ( _proteinLinkPositions[ selectedProteins[ i ] ][ selectedProteins[ i ] ] == undefined ) { continue; }

		var fromKeys = Object.keys( _proteinLinkPositions[ selectedProteins[ i ] ][ selectedProteins[ i ] ] );

		for ( var ii = 0; ii < fromKeys.length; ii++ ) {
			
			var x1 = translatePositionToXCoordinate( selectedProteins[ i ], parseInt(fromKeys[ ii ] ) );
			var y1 = fromY;

			var tos = Object.keys( _proteinLinkPositions[ selectedProteins[ i ] ][ selectedProteins[ i ] ][ fromKeys[ ii ] ] );
			
			for ( var kk = 0; kk < tos.length; kk++ ) {
				
				var x2 = translatePositionToXCoordinate( selectedProteins[ i ], parseInt( tos[ kk ] ) );
				var y2 = toY;

				if ( $( "input#color-by-search" ).is( ':checked' ) ) {
					lineColor = getColorForSearches( i, findSearchesForCrosslink( selectedProteins[ i ], selectedProteins[ i ], fromKeys[ ii ], tos[ kk ] ) );
				};

				var fromP = _proteinNames[ selectedProteins[ i ] ];
				var toP = _proteinNames[ selectedProteins[ i ] ];
				var fromPp = parseInt(fromKeys[ ii ] );
				var toPp = parseInt(tos[ kk ] );
				var lsearches = _proteinLinkPositions[ selectedProteins[ i ] ][ selectedProteins[ i ] ][ fromKeys[ ii ] ][ tos[ kk ] ];

				var link = { };
				link.type = 'crosslink';
				link.protein1 = selectedProteins[ i ];
				link.position1 = fromPp;
				link.protein2 = selectedProteins[ i ];
				link.position2 = toPp;
				link.searchIds = lsearches;
				
				var arc = svgRootSnapSVGObject.path( makeArcPath( _MAKE_ARC_PATH_DIRECTION_UP, x1, y1, x2, y2 ) );
				arc.attr({
					stroke:lineColor,
					strokeWidth:1,
					fill: "none",
					"stroke-opacity": getOpacity( i, link ),
					
					'from_protein_id':selectedProteins[ i ],
					'to_protein_id':selectedProteins[ i ],
					
					'fromp': fromP,
					'top': toP,
					'frompp': fromPp,
					'topp': toPp,
					'searches': lsearches,
					'linktype' : 'crosslink'
				});

				if ( lineColor !== _NOT_HIGHLIGHTED_LINE_COLOR ) {
					
					//  Add mouseover/mouseout to make line thicker when moused over
					
					arc.mouseover( function() { this.attr({ strokeWidth: 2 }); });
					arc.mouseout( function() { this.attr({ strokeWidth: 1 }); });
					

//					add tooltips and click handlers to crosslinks
					
					var arcSVGNativeObject = arc.node;
					
					var $arcSVGNativeObject = $(arcSVGNativeObject);

					//  add tool tip

					$arcSVGNativeObject.qtip({
						content: {
							text: 'Crosslink: ' + fromP + "(" + fromPp + ") - " + toP + "(" + toPp + ")<br>Searches: " + lsearches
						},
						position: {
							target: 'mouse'
								,
								adjust: { x: 5, y: 5 } // Offset it slightly from under the mouse
						}
					});

					//  Add click handler

					addClickHandler__SelfProteinCrosslinkLines( $arcSVGNativeObject );

				}
			}
		}
	}
}

function addClickHandler__SelfProteinCrosslinkLines( $SVGNativeObject ) {
	
	//  Add click handler

	$SVGNativeObject.click( function(  ) {

		processClickOnCrossLink( this );

	});
	
}

///////////////////////////////////////////////////////////////////////

// draw looplinks
function drawProteinLooplinkLines( selectedProteins, svgRootSnapSVGObject ) {

	for ( var i = 0; i < selectedProteins.length; i++ ) {

		var lineColor = getColor( i );
		var fromY = _OFFSET_FROM_TOP + ( _singleProteinBarOverallHeight * i ) + _SINGLE_PROTEIN_BAR_HEIGHT;
		var toY =   _OFFSET_FROM_TOP + ( _singleProteinBarOverallHeight * i ) + _SINGLE_PROTEIN_BAR_HEIGHT;

		if ( _proteinLooplinkPositions[ selectedProteins[ i ] ] == undefined ) { continue; }
		if ( _proteinLooplinkPositions[ selectedProteins[ i ] ][ selectedProteins[ i ] ] == undefined ) { continue; }

		var fromKeys = Object.keys( _proteinLooplinkPositions[ selectedProteins[ i ] ][ selectedProteins[ i ] ] );

		for ( var ii = 0; ii < fromKeys.length; ii++ ) {
			var x1 = translatePositionToXCoordinate( selectedProteins[ i ], parseInt(fromKeys[ ii ] ) );
			var y1 = fromY;

			var tos = Object.keys( _proteinLooplinkPositions[ selectedProteins[ i ] ][ selectedProteins[ i ] ][ fromKeys[ ii ] ] );

			for ( var kk = 0; kk < tos.length; kk++ ) {
				var x2 = translatePositionToXCoordinate( selectedProteins[ i ], parseInt( tos[ kk ] ) );
				var y2 = toY;

				if ( $( "input#color-by-search" ).is( ':checked' ) ) {
					lineColor = getColorForSearches( i, findSearchesForLooplink( selectedProteins[ i ], fromKeys[ ii ], tos[ kk ] ) );
				};

				var fromP = _proteinNames[ selectedProteins[ i ] ];
				var fromPp = parseInt(fromKeys[ ii ] );
				var toPp = parseInt(tos[ kk ] );
				var lsearches = _proteinLooplinkPositions[ selectedProteins[ i ] ][ selectedProteins[ i ] ][ fromKeys[ ii ] ][ tos[ kk ] ];

				var link = { };
				link.type = 'looplink';
				link.protein1 = selectedProteins[ i ];
				link.position1 = fromPp;
				link.position2 = toPp;
				link.searchIds = lsearches;
				
				var arc = svgRootSnapSVGObject.path( makeArcPath( _MAKE_ARC_PATH_DIRECTION_DOWN, x1, y1, x2, y2 ) );
				arc.attr({
					stroke:lineColor,
					strokeWidth:1,
					//"stroke-dasharray":"1,1",
					fill: "none",
					"stroke-opacity": getOpacity( i, link ),
					'from_protein_id':selectedProteins[ i ],
					'fromp': fromP,
					'frompp': fromPp,
					'topp': toPp,
					'searches': lsearches,
					'linktype' : 'looplink'
				});

				if ( lineColor !== _NOT_HIGHLIGHTED_LINE_COLOR ) {
					
					//  Add mouseover/mouseout to make line thicker when moused over
					
					arc.mouseover( function() { this.attr({ strokeWidth: 2 }); });
					arc.mouseout( function() { this.attr({ strokeWidth: 1 }); });
					

//					add tooltips and click handlers 
					
					var arcSVGNativeObject = arc.node;
					
					var $arcSVGNativeObject = $(arcSVGNativeObject);


					//  add tool tip

					$arcSVGNativeObject.qtip({
//						style: {
//						tip: false // suppress arrow from tool tip, corner of tool tip box then at mouse position
//						},
						content: {
							text: 'Looplink: ' + fromP + "(" + fromPp + "," + toPp + ")<br>Searches: " + lsearches
						}
					,
					position: {
						target: 'mouse' // Position at the mouse...
							,
							adjust: { x: 5, y: 5 } // Offset it slightly from under the mouse
					}
					});

					//  Add click handler
					
					addClickHandler__ProteinLooplinkLines( $arcSVGNativeObject );

				}
			}
		}
	}
}


function addClickHandler__ProteinLooplinkLines( $SVGNativeObject ) {
	
	//  Add click handler

	$SVGNativeObject.click( function(  ) {

		processClickOnLoopLink( this );

	});
}


///////////////////////////////////////////////////////////////////////

//draw monolinks		
function drawProteinMonolinkLines( selectedProteins, svgRootSnapSVGObject ) {

	for ( var i = 0; i < selectedProteins.length; i++ ) {

		var lineColor = getColor( i );

		var fromY = _OFFSET_FROM_TOP + ( _singleProteinBarOverallHeight * i ) + _SINGLE_PROTEIN_BAR_HEIGHT;
		var toY = fromY + 16;

		if ( _proteinMonolinkPositions[ selectedProteins[ i ] ] == undefined ) { 

			continue; //  skip processing this selected protein 
		}



		var positions = Object.keys( _proteinMonolinkPositions[ selectedProteins[ i ] ] );

		for ( var k = 0; k < positions.length; k++ ) {
			
			var x = translatePositionToXCoordinate( selectedProteins[ i ],  positions[ k ] );


			if ( $( "input#color-by-search" ).is( ':checked' ) ) {
				lineColor = getColorForSearches( i, findSearchesForMonolink( selectedProteins[ i ], positions[ k ] ) );
			};

			var fromP = _proteinNames[ selectedProteins[ i ] ];
			var fromPp = parseInt( positions[ k ] );
			var lsearches = _proteinMonolinkPositions[ selectedProteins[ i ] ][ positions[ k ] ];

			var link = { };
			link.type = 'monolink';
			link.protein1 = selectedProteins[ i ];
			link.position1 = fromPp;
			link.searchIds = lsearches;
			
			var line = svgRootSnapSVGObject.line( x, fromY, x, toY );
			line.attr({
				stroke:lineColor,
				strokeWidth:1,
				//"stroke-dasharray":"1,1",
				fill: "none",
				"stroke-opacity": getOpacity( i, link ),
				'from_protein_id':selectedProteins[ i ],
				'fromp': fromP,
				'frompp': fromPp,
				'searches': lsearches,
				'linktype' : 'monolink'
			});

			if ( lineColor !== _NOT_HIGHLIGHTED_LINE_COLOR ) {
				
				//  Add mouseover/mouseout to make line thicker when moused over

				line.mouseover( function() { this.attr({ strokeWidth: 2 }); });
				line.mouseout( function() { this.attr({ strokeWidth: 1 }); });
			}

			var circle = svgRootSnapSVGObject.circle( x, fromY + 18, 2 );
			circle.attr({
				stroke:lineColor,
				strokeWidth:1,
				"stroke-dasharray":"1,1",
				fill: lineColor,
				"fill-opacity":0.5,
				"stroke-opacity": getOpacity( i, link ),
				'from_protein_id':selectedProteins[ i ],
				'fromp': fromP,
				'frompp': fromPp,
				'searches': lsearches,
				'linktype' : 'monolink'
			});
			

			if ( lineColor !== _NOT_HIGHLIGHTED_LINE_COLOR ) {
				
//				add tooltips to mono links
				
				var toolTipText = 'Monolink: ' + fromP + "(" + fromPp + ")<br>Searches: " + lsearches;

				var toolTipParams = {
						content: {
							text: toolTipText
						},
						position: {
							target: 'mouse'
								,
								adjust: { x: 5, y: 5 } // Offset it slightly from under the mouse
						}
				};

				var lineSVGNativeObject = line.node;
				var $lineSVGNativeObject = $(lineSVGNativeObject);
				$lineSVGNativeObject.qtip( toolTipParams );

				var circleSVGNativeObject = circle.node;
				var $circleSVGNativeObject = $(circleSVGNativeObject);
				$circleSVGNativeObject.qtip( toolTipParams );


				//  Add click handler

				addClickHandler__ProteinMonolinkLines( $lineSVGNativeObject );
				addClickHandler__ProteinMonolinkLines( $circleSVGNativeObject );
			}
		}
	}
}


function addClickHandler__ProteinMonolinkLines( $SVGNativeObject ) {

//	Add click handler

	$SVGNativeObject.click( function(  ) {

		processClickOnMonoLink( this );

	});
}

///////////////////////////////////////////////

function drawColorBySearchLegend(  selectedProteins, bottomOfLowestItemDrawn, svgRootSnapSVGObject ) {
	
	
	var rightMostEdgeOfAllElementsThisFunction = 0;
	
	
	//  This legend, like the rest of 'Color by search' only supports up to 3 search
	
//	var basey = _OFFSET_FROM_TOP + 60 + ( selectedProteins.length * _singleProteinBarOverallHeight );
	
	//  Change to base basey of the bottom of previous drawn items.  This way it adjusts for when the scale bar is not drawn
	var basey = bottomOfLowestItemDrawn + 30;
	
	
	
	var startx = translateScaleBarPositionToXCoordinate( 1 );
	
	var searchBlocksLeftMargin = startx + 60;
	
	var g = svgRootSnapSVGObject.g();
	
	var legendText = svgRootSnapSVGObject.text( startx - 4 , basey, "Legend:" );
	g.add( legendText );
	
	var legendCounter = 0;
	
	var wrapLegendToNextLineAsNeeded = function() {
		
		var searchBlockRightEdge = searchBlocksLeftMargin + ( (legendCounter + 1 ) * _COLOR_BY_SEARCH_OUTER_WIDTH );
		
		if ( searchBlockRightEdge > get_MAX_WIDTH() ) {
			
			//  Move the rest of the search blocks to another row
			
			basey = bottomOfLowestItemDrawn + 10;
			
			bottomOfLowestItemDrawn = basey + 20;  // adjust bottomOfLowestItemDrawn for this item
			
			legendCounter = 0;  // reset to left edge
		}
	};
	
	
	
	
	for ( var i = 0; i < _searches.length; i++ ) {
		
		//  A legend part for up to 3 searches, each search alone
		
		if ( i > 2 ) { 
			break;  //  EARLY LOOP EXIT after processing the first 3
		}
		
		wrapLegendToNextLineAsNeeded();
					
		var legendRectangle = svgRootSnapSVGObject.rect( searchBlocksLeftMargin + (legendCounter * _COLOR_BY_SEARCH_OUTER_WIDTH ), basey - 15, 20, 20 );
		legendRectangle.attr( {
			fill: getColorForSearches( -1, [ _searches[ i ][ 'id' ], ] )
		});
		
		legendText = svgRootSnapSVGObject.text( searchBlocksLeftMargin + 23 + (legendCounter * _COLOR_BY_SEARCH_OUTER_WIDTH ) , basey, " = Search: " + _searches[ i ][ 'id' ] );			
		
		g.add( legendRectangle );
		g.add( legendText );
		
		legendCounter++;
		
		
		
		var legendTextBBox = legendText.getBBox();
		var legendTextRightSide = legendTextBBox.x2;
		
		if ( legendTextRightSide > rightMostEdgeOfAllElementsThisFunction ) {
			
			rightMostEdgeOfAllElementsThisFunction = legendTextRightSide;
		} 


	}
	
	if ( _searches.length > 1 ) {
		
		//  A legend part for all combinations of 2 searches
		
		for ( var i = 0; i < _searches.length; i++ ) {
			for ( var k = i + 1; k < _searches.length; k++ ) {
				
				wrapLegendToNextLineAsNeeded();

				var legendSearchArray = [ _searches[ i ][ 'id' ], _searches[ k ][ 'id' ] ];
				
				legendRectangle = svgRootSnapSVGObject.rect( searchBlocksLeftMargin + (legendCounter * _COLOR_BY_SEARCH_OUTER_WIDTH ), basey - 15, 20, 20 );
				legendRectangle.attr( {
					fill: getColorForSearches( -1, legendSearchArray )
				});
				
				legendText = svgRootSnapSVGObject.text( searchBlocksLeftMargin + 23 + (legendCounter * _COLOR_BY_SEARCH_OUTER_WIDTH ) , basey, " = Searches: " + legendSearchArray.join() );			
				
				g.add( legendRectangle );
				g.add( legendText );
				
				legendCounter++;
				
				
				
				var legendTextBBox = legendText.getBBox();
				var legendTextRightSide = legendTextBBox.x2;
				
				if ( legendTextRightSide > rightMostEdgeOfAllElementsThisFunction ) {
					
					rightMostEdgeOfAllElementsThisFunction = legendTextRightSide;
				} 


			}
		}
	}
	
	if ( _searches.length > 2 ) {
		
		//  A legend part for the color for all 3 searches combined 
		
		wrapLegendToNextLineAsNeeded();

		legendSearchArray = [ _searches[ 0 ][ 'id' ], _searches[ 1 ][ 'id' ], _searches[ 2 ][ 'id' ] ];
		
		legendRectangle = svgRootSnapSVGObject.rect( searchBlocksLeftMargin + (legendCounter * _COLOR_BY_SEARCH_OUTER_WIDTH ), basey - 15, 20, 20 );
		legendRectangle.attr( {
			fill: getColorForSearches( -1, legendSearchArray )
		});
		
		legendText = svgRootSnapSVGObject.text( searchBlocksLeftMargin + 23 + (legendCounter * _COLOR_BY_SEARCH_OUTER_WIDTH ) , basey, " = Searches: " + legendSearchArray.join() );			
		
		g.add( legendRectangle );
		g.add( legendText );
		
		legendCounter++;
		
		
		var legendTextBBox = legendText.getBBox();
		var legendTextRightSide = legendTextBBox.x2;
		
		if ( legendTextRightSide > rightMostEdgeOfAllElementsThisFunction ) {
			
			rightMostEdgeOfAllElementsThisFunction = legendTextRightSide;
		} 
	}

	var functionResponseObject = {

			rightMostEdgeOfAllElementsThisFunction : rightMostEdgeOfAllElementsThisFunction
	};

	return functionResponseObject;

}

/////////////////////////////////////////////////////////

/*
 * For the given position in the given protein, determine the x coordinate that the
 * sequence coverage bar should start or end. The variable named "side" must be
 * either "start" or "end", based on whether this query is resulting in the start
 * or end of the sequence coverage box.
 */
function translateSequenceCoveragePositionToXCoordinate( protein, position, side ) {
	
	var m = getMultiplier();
	
	
	if ( isProteinReversed( protein ) ) {
		
		//  Flip the "side"
		
		if ( side === _TRANSLATE_SEQUENCE_COVERAGE_POSITION_TO_X_COORDINATES_SIDE_START ) {
			
			side = _TRANSLATE_SEQUENCE_COVERAGE_POSITION_TO_X_COORDINATES_SIDE_END;
		} else {
			
			side === _TRANSLATE_SEQUENCE_COVERAGE_POSITION_TO_X_COORDINATES_SIDE_START;
		}
		
	}
	
	if ( side === _TRANSLATE_SEQUENCE_COVERAGE_POSITION_TO_X_COORDINATES_SIDE_START ) {
		return translatePositionToXCoordinate( protein, position ) - (m / 2 );
	} else if ( side === _TRANSLATE_SEQUENCE_COVERAGE_POSITION_TO_X_COORDINATES_SIDE_END ) {
		return translatePositionToXCoordinate( protein, position ) + (m / 2 );
	
	} else {
		
		throw "translateSequenceCoveragePositionToXCoordinate: unknown value for side: |" + side + "|"; 
	}
}

/*
 * For a given protein, calculate its width in pixels for the viewer
 */
function translateProteinWidthToPixelWidth( protein ) {
	
	var m = getMultiplier();
	var l = _proteinLengths[ protein ];
	
	return m * l;
}

/*
 * For a given protein and position, determine the x-axis pixel coordinate on the rendered viewer
 */
function translatePositionToXCoordinate( protein, position ) {	
	
	var m = getMultiplier();
	
	var offset = ( position * m  ) - ( m / 2 );
	
	if ( isProteinReversed( protein ) ) {
		
		var rectanglePixelWidth = translateProteinWidthToPixelWidth( protein );
		
		//  reverse to be offset from right edge of protein bar
		offset =  rectanglePixelWidth - offset; 
	}
	
	return getProteinOffset( protein ) + _proteinBarsLeftEdge + offset;
}

/*
 * Get the tick positions for the scale bar
 */
function translateScaleBarPositionToXCoordinate( position ) {	
	
	var m = getMultiplier();
	
	return _proteinBarsLeftEdge + ( position * m  ) - ( m / 2 );
}


/*
 * Given a horizontal (x coord) pixel value, determine what tick on the scale bar (in protein residue position)
 * would represent that position
 */
function getScaleBarPositionForPixel( x ) {
	
	var m = getMultiplier();
	
	x -= _proteinBarsLeftEdge;	
	return Math.round( x / m );
}


function addProteinSelect() {

	// the existing element after which we are inserting our select box
	
//	var count = $("select.svg-protein-select").length;
	
	var newId = "svg-protein-select" + ( parseInt( getMaxProteinSelectIdNumber() ) + 1 );

	var html = "<select id=\"" + newId + "\" class=\"svg-protein-select\">\n";

	html += "\t<option value=\"0\">Select a protein</option>\n";

	for ( var i = 0; i < _proteins.length; i++ ) {
		html+= "\t<option value=\"" + _proteins[ i ] + "\">" + getProteinName( _proteins[ i ] ) + "</option>\n";
	}


	var $newSelector = $( html ).insertBefore( $( "span#svg-protein-selector_location" ) );

//	$( "select#" + newId )
	
	$newSelector.change( function() {
		markInvalidProteinsExcludeSelect( $( this ) );
		updateURLHash( false /* useSearchForm */ );
		loadDataAndDraw( true /* doDraw */ );
	});

	markInvalidProteinsInSelect( $newSelector );

}

function markInvalidProteins() {

	$("select.svg-protein-select").each( function() {
		markInvalidProteinsInSelect( $( this ) );
	});
}


function markInvalidProteinsExcludeSelect( $select ) {

	$("select.svg-protein-select").each( function() {

		if ( $select.attr( "id" ) == $( this ).attr( "id" ) ) {
			return true;
		}

		markInvalidProteinsInSelect( $( this ) );			
	});
}

function markInvalidProteinsInSelect( $select ) {

	//if ( getSelectedProteins().length == 0 ) { return; }
	var validProteins = getValidProteins( $select );

	$select.children().each( function() {

		if ( $( this ).val() == 0 ) { return true; }

		if ( $( "input#show-crosslinks" ).is( ':checked' ) && $.inArray( $( this ).val(), validProteins ) == -1 ) {
			$(this).css('color', '#969696');
		} else {
			$(this).css('color', '#000000');
		}
	});


}


// remove the protein select box w/ the given id from the DOM
function removeProteinSelect( id ) {
	
	$("select#" + id ).remove();
}

function getMaxProteinSelectIdNumber() {

	if ( $("select.svg-protein-select").length == 0 ) { return 0; }		
	var $lastItem = $("select.svg-protein-select").last();

	var re = /^.+(\d+)$/;
	var m = re.exec( $lastItem.attr("id" ) );

	return m[ 1 ];		
}

// get an array of the currently-selected proteins
function getSelectedProteins( $select_to_exclude ) {
	
	var proteinsLocalVar = new Array();

	$("select.svg-protein-select").each( function() {

		if ( $select_to_exclude != undefined && $select_to_exclude.attr( "id" ) == $( this ).attr( "id" ) ) { return true; }

		var value = $( this ).val();			
		if ( value != 0 && $.inArray(value, proteinsLocalVar) == -1 ) {
			proteinsLocalVar.push( value );
		}

	});

	return proteinsLocalVar;
}

// get an array of all the currently-selected proteins, can contain same
// protein multiple times
function getAllSelectedProteins() {
	
	var proteinsLocalVar = new Array();

	$("select.svg-protein-select").each( function() {

		var value = $( this ).val();			
		if ( value != 0 ) { //  0 is the value when a protein has not been selected
			proteinsLocalVar.push( value );
		}

	});

	return proteinsLocalVar;
}



// get an array of "valid" proteins--proteins with crosslinks with
// any of the currently-selected proteins
function getValidProteins( $select ) {
	
	if ( _proteinLinkPositions == undefined ) {
		return _proteins;
	}
	
	var sProteins = getSelectedProteins( $select );
	var vProteins = new Array();

	if ( sProteins.length == 0 ) { return Object.keys( _proteinLinkPositions ); }

	for ( var i = 0; i < sProteins.length; i++ ) {
		if ( _proteinLinkPositions[ sProteins[ i ] ] == undefined ) { continue; }
		var partners = Object.keys( _proteinLinkPositions[ sProteins[ i ] ]  );

		for ( var k = 0; k < partners.length; k++ ) {
			if ( $.inArray( partners[ k ], vProteins ) == -1 ) {
				vProteins.push( partners[ k ] );
			}
		}
	}

	return vProteins;
}


function makeArcPath( direction, startx, starty, endx, endy ) {
	
	var height = 20;

	var x1 = startx + ( endx - startx ) / 4 ;
	var x2 = startx + (endx - startx  ) / 2 ;
	var x3 = startx + ( ( endx - startx  ) * 3 / 4 );

	var y1, y2, y3;

	if ( direction === _MAKE_ARC_PATH_DIRECTION_UP ) {
		
		y1 = starty - ( height * 3 / 4 );
		y2 = starty - height;
		y3 = y1;
		
	} else if ( direction === _MAKE_ARC_PATH_DIRECTION_DOWN ) {
		
		y1 = starty + ( height * 3 / 4 );
		y2 = starty + height;
		y3 = y1;
	
	} else {
		
		throw "direction passed to makeArcPath(...) is invalid.  direction: '" + direction + "'.";
	}

	var path = "M" + startx + " " + starty;

	path += "R" + x1 + " " + y1;
	path += " " + x2 + " " + y2;
	path += " " + x3 + " " + y3;
	path += " " + endx + " " + endy;				

	return path;
}

function downloadSvg() {
	
	var uriContent = "data:application/svg+xml," + encodeURIComponent( "<svg id=\"svg\">" + $( "svg#svg" ).html() +"</svg>");
	var myWindow = window.open(uriContent, "crosslink.svg");
	myWindow.focus();
}


///////////////////////////

var viewerInitialized = false;

function initializeViewer()  { 

	refreshViewerProteinSelects();
	
	if ( viewerInitialized ) { 
		return; 
	}
	
	viewerInitialized = true;
	
	populateViewerCheckBoxes();

	
	
	if ( _searches.length > _MAX_NUMBER_SEARCHES_SUPPORTED_FOR_COLOR_BY_SEARCHES ) {
		
		//  The number of searches is > number supported for "Color by search" 
		//    so switch to showing the disabled checkbox for "Color by search"
		
		$("#color_by_search_outer_container").hide();
		$("#color_by_search_disabled_outer_container").show();
	}


	
	$("#vertical_spacing_value").text( _singleProteinBarOverallHeight );
	
	$("#vertical_spacing_slider_div").slider({
	      value:_singleProteinBarOverallHeight,
	      min: 80,
	      max: 160,
//	      step: 50,
	      slide: function( event, ui ) {
	    	  $("#vertical_spacing_value").text( ui.value );
	      },
	      change: function( event, ui ) {
	  		  
	  		  _singleProteinBarOverallHeight = ui.value ;
	  		  
	  		  updateURLHash( false /* useSearchForm */ );

	    	  $("#vertical_spacing_value").text( _singleProteinBarOverallHeight );
	    	  drawSvg();
	      }

	    });
	

	
	$("#horizontal_scaling_slider_div").slider({
	      value: _horizontalScalingPercent,
	      min: 50,
	      max: 400,
	      step: 50,
	      slide: function( event, ui ) {
	    	  $("#horizontal_scaling_value").text( ui.value );
	      },
	      change: function( event, ui ) {
	    	  
	    	  _horizontalScalingPercent = ui.value;
	    	  
	  		  updateURLHash( false /* useSearchForm */ );

	  		  _userScaleFactor = _computedStandardMultiplier * ( _horizontalScalingPercent / 100 );
	    	  $("#horizontal_scaling_value").text( _horizontalScalingPercent );
	    	  drawSvg();
	      }
	    });
	
	$( "input#show-crosslinks" ).change( function() {
		updateURLHash( false /* useSearchForm */ );
		loadDataAndDraw( true /* doDraw */ );
		markInvalidProteins();
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
	
	$( "input#color-by-search" ).change( function() {
		updateURLHash( false /* useSearchForm */ );
		drawSvg();
	});

	$( "input#shade-by-counts" ).change( function() {
		updateURLHash( false /* useSearchForm */ );
		loadDataAndDraw( true /* doDraw */ );
	});
	
//	$( "input#show-coverage" ).change( function() {
//		updateURLHash( false /* useSearchForm */ );
//		loadDataAndDraw( true /* doDraw */ );
//	});
	
	
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

	$( "a.download-svg" ).click( function() {
		var d = new Date().toISOString().slice(0, 19).replace(/-/g, "");
		var $svg_image_inner_container_div__svg_merged_image_svg_jq = $( "#svg_image_inner_container_div svg.merged_image_svg_jq " );
		var svgContents = $svg_image_inner_container_div__svg_merged_image_svg_jq.html();
		var fullSVG_String = "<svg id=\"svg\">" + svgContents +"</svg>";
		var svgBase64Ecoded = Base64.encode( fullSVG_String );
		var hrefString = "data:application/svg+xml;base64," + svgBase64Ecoded;
		var downloadFilename = "crosslinks-" + d + ".svg";
		$(this).attr("href", hrefString ).attr("download", downloadFilename );
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

//function setUserScaleFactorAndRedraw( newMultiplier ) {
//	
//	if ( _userScaleFactor == undefined || _userScaleFactor != newMultiplier ) {
//		
//		_userScaleFactor = newMultiplier;
//		drawSvg();
//	}
//}
//
//function submitUserScaleFactor() {
//	
//	//  If the user entered scale factor is not a floating point number,
//	//    put the previous value stored in the variable into the input field
//	
//	var $input_scale_factor_input = $( "input#scale-factor-input" );
//	
//	var input_scale_factor_input = $input_scale_factor_input.val();
//	
//	if ( input_scale_factor_input == undefined || input_scale_factor_input === "" ) {
//		$input_scale_factor_input.val( _userScaleFactor );
//		return;
//	}
//	
//	if ( isNaN( input_scale_factor_input ) ) {
//		$input_scale_factor_input.val( _userScaleFactor );
//		return;
//	}
//	
//	var value = parseFloat( input_scale_factor_input );
//	if ( value <= 0 ) {
//		$input_scale_factor_input.val( _userScaleFactor );
//		return;
//	}
//	
//	//  The user has entered a number so save the value and redraw
//	
//	setUserScaleFactorAndRedraw( value );
//	updateURLHash( false /* useSearchForm */ );
//}


//// handle redrawing on resize
//var rtime;
//var timeout = false;
//var delta = 200;
//$(window).resize(function() {
//	rtime = new Date();
//	if (timeout === false) {
//		timeout = true;
//		setTimeout(resizeend, delta);
//	}
//});
//
//function resizeend() {
//	if (new Date() - rtime < delta) {
//		setTimeout(resizeend, delta);
//	} else {
//		timeout = false;
//		drawSvg();
//	}
//}
//



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




///////////////////////////////////////////////////////////////////////////

////////////    Click handlers for the links ( Lines that show the links )



//////////////   Process LOOP link


function processClickOnLoopLink( clickThis  ) {

	var params = {
			clickThis : clickThis,
			psmPeptideCutoffsRootObject : _psmPeptideCutoffsRootObjectStorage.getPsmPeptideCutoffsRootObject()
	};

	getLooplinkDataForSpecificLinkInGraph( params );
}



/////////////////////////////////

//////////////   Process CROSS link


function processClickOnCrossLink( clickThis  ) {

	var params = {
			clickThis : clickThis,
			psmPeptideCutoffsRootObject : _psmPeptideCutoffsRootObjectStorage.getPsmPeptideCutoffsRootObject()
	};

	getCrosslinkDataForSpecificLinkInGraph( params );
}



/////////////////////////////////

//////////////Process Mono link


function processClickOnMonoLink( clickThis  ) {

	var params = {
			clickThis : clickThis,
			psmPeptideCutoffsRootObject : _psmPeptideCutoffsRootObjectStorage.getPsmPeptideCutoffsRootObject()
	};

	getMonolinkDataForSpecificLinkInGraph( params );
}



//Initialize the page and load the data

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
	
	

	loadDataFromService();
};



$(document).ready(function()  { 
	initPage();
});

///////////

//   Object for passing to other objects

var imageViewerPageObject = {

		getQueryJSONString : function() {

			var queryJSON = getNavigationJSON_Not_for_Image_Or_Structure();

			var queryJSONString = JSON.stringify( queryJSON );

			return queryJSONString;
		}
};




