/**
 * crosslink-image-viewer.js
 * 
 * Javascript for the viewMergedImage.jsp page
 * 
 *  !!!! Warning !!!!:  
 *		the objects in _searches have property ['id'] which is the projectSearchId
 *		search.id and _searches[i]['id']  reference the projectSearchId 
 *		Some uses of searchId are actually projectSearchId
 *
 *   All references to proteinId are actually referencing the protein sequence id
 *
 *   A plain "protein" variable is the protein sequence id
 *   
 *   The actual svg image is drawn in the function drawSvg()
 *   
 *   WARNING:  There are global variables scattered throughout the file
 */

//    Snap.svg  Info
//        every object created has a "node" property which is the raw SVG element - http://snapsvg.io/docs/#Element.node

//  Example Text entry from Mason   
//     The "dy" value of ".35em" makes it so the "x" and "y" refer to the upper left corner instead of the text baseline,
//     making it easier to line text up with shapes. 
// <text id="SvgjsText1582" style="font-size:12px;font-family:Helvetica, Arial, sans-serif" data-row-rowIdx="0" dy=".35em" x="18" y="9.5">
//	<tspan id="SvgjsTspan1583" dy=".35em" x="18" style="font-size:12px;font-family:Helvetica, Arial, sans-serif">Coiled-coil</tspan>
//  </text>

//////////////////////////////////
// JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";


//  Import to make available on the page


//Import header_main.js and children to ensure on the page
import { header_mainVariable } from 'page_js/header_section_js_all_pages_main_pages/header_section_main_pages/header_main.js';


import { searchesChangeDisplayOrder } from 'page_js/data_pages/project_search_ids_driven_pages/common/searchesChangeDisplayOrder.js';
import { searchesForPageChooser } from 'page_js/data_pages/project_search_ids_driven_pages/common/searchesForPageChooser.js';
import { sharePageURLShortener  } from 'page_js/data_pages/project_search_ids_driven_pages/common/sharePageURLShortener.js';


import { defaultPageView } from 'page_js/data_pages/project_search_ids_driven_pages/common/defaultPageView.js';

import { addSingleTooltipForProteinName } from 'page_js/data_pages/project_search_ids_driven_pages/common/createTooltipForProteinNames.js';

import { minimumPSM_Count_Filter } from 'page_js/data_pages/project_search_ids_driven_pages/common/minimumPSM_Count_Filter.js';
import { annotationDataDisplayProcessingCommonCode } from 'page_js/data_pages/project_search_ids_driven_pages/common/psmPeptideAnnDisplayDataCommon.js';
import { cutoffProcessingCommonCode } from 'page_js/data_pages/project_search_ids_driven_pages/common/psmPeptideCutoffsCommon.js';

import { webserviceDataParamsDistributionCommonCode } from 'page_js/data_pages/project_search_ids_driven_pages/common/webserviceDataParamsDistribution.js';

import { computeCutPoints } from 'page_js/data_pages/project_search_ids_driven_pages/image_page/trypsinCutPointsForSequence.js';

import { ImageProteinBarDataManagerContructor, ImageProteinBarData, ImageProteinBarDataManager_pass_imagePagePrimaryRootCodeObject } from './crosslink-image-viewer-per-protein-bar-data.js';

import { indexManager } from './crosslink-image-viewer-index-manager.js';
import { customRegionManager, customRegionManager_pass_imagePagePrimaryRootCodeObject } from './crosslink-image-viewer-custom-region-manager.js';

import { proteinAnnotationStore, getDisorderedRegionsDisopred_2, getDisorderedRegionsDisopred_3, getSecondaryStructureRegions, proteinAnnotationStore_pass_imagePagePrimaryRootCodeObject } from './crosslink-image-viewer-protein-annotation.js';

import { LinkExclusionDataManagerContructor } from './crosslink-image-viewer-link-exclusion-manager.js';

import { ProteinBarRegionSelectionsOverlayCodeContructor, proteinBarRegionSelectionsOverlayCode_pass_imagePagePrimaryRootCodeObject } from './crosslink-image-viewer-region-selections.js';
import { LinkExclusionSelectionsOverlayCodeContructor, LinkExclusionSelectionsOverlayCode_pass_imagePagePrimaryRootCodeObject } from './crosslink-image-viewer-link-exclusion-selections.js';

import { ColorManager, ColorManager_pass_imagePagePrimaryRootCodeObject } from './crosslink-image-viewer-color-manager.js';
import { circlePlotViewer, circlePlotViewer_pass_imagePagePrimaryRootCodeObject } from './circle-plot-viewer.js';

import { LegacyJSONUpdater, LegacyJSONUpdater_pass_imagePagePrimaryRootCodeObject } from './crosslink-image-viewer-legacy-handler.js';

import { getLooplinkDataForSpecificLinkInGraph, getCrosslinkDataForSpecificLinkInGraph, getMonolinkDataForSpecificLinkInGraph } from './crosslink-image-viewer-click-element-handlers.js';

import { getLooplinkDataCommon, getCrosslinkDataCommon, getMonolinkDataCommon, attachViewLinkInfoOverlayClickHandlers } from 'page_js/data_pages/project_search_ids_driven_pages/image_page__structure_page__shared/image_structure_click_element_common.js';

/////////////////////////////

//   Javascript Class Contructor that will hold all the Javascript code in this file 
// 	   to remove all the Javascript code in this file from the global scope.

var ImagePagePrimaryRootCodeClass = function() {

	var this_OfOutermostObjectOfClass = this;

	////////////////////////////////


	//  functions attached to 'this' to call functions on Variables inside this constructor

	this_OfOutermostObjectOfClass.call__proteinBarRegionSelectionsOverlayCode_openOverlay = function() {
		_proteinBarRegionSelectionsOverlayCode.openOverlay();
	}

	this_OfOutermostObjectOfClass.call__linkExclusionSelectionsOverlayCode_openOverlay = function() {
		_linkExclusionSelectionsOverlayCode.openOverlay();
	}

	this_OfOutermostObjectOfClass.call__customRegionManager_showManagerOverlay = function() {
		_customRegionManager.showManagerOverlay()
	}

	//  functions attached to 'this' to call functions inside this constructor

	/**
	 * Opens the protein selection overlay, and populates it with appropriate data.
	 */
	this_OfOutermostObjectOfClass.call__openSelectProteinSelect = function( params ) {
		openSelectProteinSelect( params );
	}

	/**
	 * Initialize the page and load the data
	 */
	this_OfOutermostObjectOfClass.call__initPage = function() {
		initPage();
	}

	/**
	 * Called from button "Update From Database" on the page
	 *   Refresh the data on the page
	 */
	this_OfOutermostObjectOfClass.call__refreshData = function() {
		refreshData();
	}

	/**
	 * Reset Proteins highlighting, flipping, positioning, and horizontal scaling and redraw
	 */
	this_OfOutermostObjectOfClass.call__resetProteins = function() {
		resetProteins();
	}


	/**
	 * get values for variables from the hash part of the URL as JSON
	 */
	this_OfOutermostObjectOfClass.call__getJsonFromHash = function() {
	
		return getJsonFromHash();
	}

	/**
	 * get values for variables from the hash part of the URL as JSON
	 */
	this_OfOutermostObjectOfClass.call__getRawJsonFromHash = function() {
	
		return getRawJsonFromHash();
	}

	/////////////////////////////////////
	/**
	 * Get the JSON version number from the raw JSON hash
	 */
	this_OfOutermostObjectOfClass.call__getJSONVersionNumber = function() {
		return getJSONVersionNumber();
	}

	/**
	 * build a query string based on selections by user
	 */
	this_OfOutermostObjectOfClass.call__updateURLHash = function( useSearchForm ) {
		return updateURLHash( useSearchForm );
	}

	/**
	 * 
	 */
	this_OfOutermostObjectOfClass.call__updateURLHashWithJSONObject = function( jsonObject ) {
		return updateURLHashWithJSONObject( jsonObject );
	}

	/**
	 * Take a property name for HASH_OBJECT_PROPERTIES and return it's value.  Throw an exception if the property name is not found
	 */
	this_OfOutermostObjectOfClass.call__hashObjectPropertyValueFromPropertyNameLookup = function( hashObjectPropertyLabel ) {
		return hashObjectPropertyValueFromPropertyNameLookup( hashObjectPropertyLabel )
	}

	/**
	 * ensure the necessary data are collected before viewer is drawn
	 */
	this_OfOutermostObjectOfClass.call__loadDataAndDraw = function( doDraw, loadComplete ) {
		return loadDataAndDraw( doDraw, loadComplete );
	}

	/////////////////////////////////////////////////////////
	/**
	 * Actually draw the image
	 */
	this_OfOutermostObjectOfClass.call__drawSvg = function() {
		return drawSvg();
	}

	/**
	 * returns a list of searches for the given link
	 */
	this_OfOutermostObjectOfClass.call__findSearchesForCrosslink = function( protein1, protein2, position1, position2 ) {
		return findSearchesForCrosslink( protein1, protein2, position1, position2 );
	}

	/**
	 * returns a list of searches for the given link
	 */
	this_OfOutermostObjectOfClass.call__findSearchesForMonolink = function( protein, position ) {
		return findSearchesForMonolink( protein, position );
	}

	/**
	 * returns a list of searches for the given link
	 */
	this_OfOutermostObjectOfClass.call__findSearchesForLooplink = function( protein, position1, position2 ) {
		return findSearchesForLooplink( protein, position1, position2 );
	}

	/**
	 * 
	 */
	this_OfOutermostObjectOfClass.call__convertProjectSearchIdArrayToSearchIdArray = function( projectSearchIdArray ) {
		return convertProjectSearchIdArrayToSearchIdArray( projectSearchIdArray );
	}

	/**
	 * 
	 */
	this_OfOutermostObjectOfClass.call__processClickOnLink = function( clickThis ) {
		return processClickOnLink( clickThis );
	}

	/**
	 * highlight the given index among the selected proteins
	 */
	this_OfOutermostObjectOfClass.call__toggleHighlightedProtein = function( i, shouldClearFirst ) {
		return toggleHighlightedProtein( i, shouldClearFirst );
	}

	/**
	 * 
	 */
	this_OfOutermostObjectOfClass.call__getProteinName = function( proteinId ) {
		return getProteinName( proteinId );
	}

	////////////////////////////////

	//  functions attached to 'this' to return 'global' constants inside this constructor

	this_OfOutermostObjectOfClass.getVariable__v_SEARCH_COLORS = function() {
		return _SEARCH_COLORS;
	}

	this_OfOutermostObjectOfClass.getVariable__v_SELECT_ELEMENT_COLOR_BY_REGION = function() {
		return SELECT_ELEMENT_COLOR_BY_REGION;
	}

	this_OfOutermostObjectOfClass.getVariable__v_JSON_VERSION_NUMBER = function() {
		return _JSON_VERSION_NUMBER;
	}

	this_OfOutermostObjectOfClass.getVariable__v_HASH_OBJECT_PROPERTIES = function() {
		return HASH_OBJECT_PROPERTIES;
	}

	this_OfOutermostObjectOfClass.getVariable__v_BETA_SHEET = function() {
		return BETA_SHEET;
	}

	this_OfOutermostObjectOfClass.getVariable__v_ALPHA_HELIX = function() {
		return ALPHA_HELIX;
	}

	this_OfOutermostObjectOfClass.getVariable__v_PROTEIN_BAR_OVERLAY_RECTANGLE_LABEL_CLASS = function() {
		return _PROTEIN_BAR_OVERLAY_RECTANGLE_LABEL_CLASS;
	}

	this_OfOutermostObjectOfClass.getVariable__v_SELECT_ELEMENT_ANNOTATION_TYPE_DISOPRED3 = function() {
		return SELECT_ELEMENT_ANNOTATION_TYPE_DISOPRED3;
	}

	this_OfOutermostObjectOfClass.getVariable__v_SELECT_ELEMENT_ANNOTATION_TYPE_PSIPRED3 = function() {
		return SELECT_ELEMENT_ANNOTATION_TYPE_PSIPRED3;
	}

	this_OfOutermostObjectOfClass.getVariable__v_SELECT_ELEMENT_ANNOTATION_TYPE_SEQUENCE_COVERAGE = function() {
		return SELECT_ELEMENT_ANNOTATION_TYPE_SEQUENCE_COVERAGE;
	}

	this_OfOutermostObjectOfClass.getVariable__v_SELECT_ELEMENT_ANNOTATION_TYPE_CUSTOM = function() {
		return SELECT_ELEMENT_ANNOTATION_TYPE_CUSTOM;
	}

	this_OfOutermostObjectOfClass.getVariable__v_SELECT_ELEMENT_COLOR_BY_SEARCH = function() {
		return SELECT_ELEMENT_COLOR_BY_SEARCH;
	}
	

	////////////////////////////////

	//  functions attached to 'this' to SET 'global' variables inside this constructor

	this_OfOutermostObjectOfClass.set_Variable__v_GLOBAL_SNAP_SVG_OBJECT = function( GLOBAL_SNAP_SVG_OBJECT ) {
		_GLOBAL_SNAP_SVG_OBJECT = GLOBAL_SNAP_SVG_OBJECT;
	}

	////////////////////////////////

	//  functions attached to 'this' to return 'global' variables inside this constructor

	this_OfOutermostObjectOfClass.getVariable__v_GLOBAL_SNAP_SVG_OBJECT = function() {
		return _GLOBAL_SNAP_SVG_OBJECT;
	}
	
	this_OfOutermostObjectOfClass.getVariable__v_indexManager = function() {
		return _indexManager;
	}

	this_OfOutermostObjectOfClass.getVariable__v_imageProteinBarDataManager = function() {
		return _imageProteinBarDataManager;
	}

	this_OfOutermostObjectOfClass.getVariable__v_linkExclusionDataManager = function() {
		return _linkExclusionDataManager;
	}

	this_OfOutermostObjectOfClass.getVariable__v_searches = function() {
		return _searches;
	}

	this_OfOutermostObjectOfClass.getVariable__v_proteinLengths = function() {
		return _proteinLengths;
	}

	this_OfOutermostObjectOfClass.getVariable__v_linkPSMCounts = function() {
		return _linkPSMCounts;
	}

	this_OfOutermostObjectOfClass.getVariable__v_proteins = function() {
		return _proteins;
	}

	this_OfOutermostObjectOfClass.getVariable__v_proteinNames = function() {
		return _proteinNames;
	}

	this_OfOutermostObjectOfClass.getVariable__v_ranges = function() {
		return _ranges;
	}

	this_OfOutermostObjectOfClass.getVariable__v_customRegionManager = function() {
		return _customRegionManager;
	}

	this_OfOutermostObjectOfClass.getVariable__v_proteinSequenceTrypsinCutPoints = function() {
		return _proteinSequenceTrypsinCutPoints;
	}

	this_OfOutermostObjectOfClass.getVariable__v_linkablePositions = function() {
		return _linkablePositions;
	}

	this_OfOutermostObjectOfClass.getVariable__v_proteinMonolinkPositions = function() {
		return _proteinMonolinkPositions;
	}

	this_OfOutermostObjectOfClass.getVariable__v_proteinLooplinkPositions = function() {
		return _proteinLooplinkPositions;
	}

	this_OfOutermostObjectOfClass.getVariable__v_proteinLinkPositions = function() {
		return _proteinLinkPositions;
	}

	this_OfOutermostObjectOfClass.getVariable__v_proteinSequences = function() {
		return _proteinSequences;
	}

	this_OfOutermostObjectOfClass.getVariable__v_proteinBarToolTip_template_HandlebarsTemplate = function() {
		return _proteinBarToolTip_template_HandlebarsTemplate;
	}

	this_OfOutermostObjectOfClass.getVariable__v_colorManager = function() {
		return _colorManager;
	}

	this_OfOutermostObjectOfClass.getVariable__v_colorLinesBy = function() {
		return _colorLinesBy;
	}

	
	
	////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////

	//   Start of Legacy code in this file


	///////////////   CONSTANTS  ////////////////////////
	/*
	* The version number to pass along in the JSON for check for compatibility
	*/
	var _JSON_VERSION_NUMBER = 2;
	/**
	 * Default exclude link type "No Links"
	 */
	var EXCLUDE_LINK_TYPE_DEFAULT = [ 0 ];

	/**
	 *  Matches the "value" for the <select id="annotation_type">
	 */
	var SELECT_ELEMENT_ANNOTATION_TYPE_SEQUENCE_COVERAGE = "sequence_coverage";
	//var SELECT_ELEMENT_ANNOTATION_TYPE_DISOPRED2 = "disopred2";
	var SELECT_ELEMENT_ANNOTATION_TYPE_DISOPRED3 = "disopred3";
	var SELECT_ELEMENT_ANNOTATION_TYPE_PSIPRED3 = "psired3";
	var SELECT_ELEMENT_ANNOTATION_TYPE_CUSTOM = "custom";

	/**
	 * Matches the "value" for the <select id="color_by">
	 */
	var SELECT_ELEMENT_COLOR_BY_REGION = "region";
	var SELECT_ELEMENT_COLOR_BY_SEARCH = "search";

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
					"#878906",	
					// mustard yellow
					];
	//  If the number of searches supported by search colors changes, change the conditional in the JSP
	//    for the option:   <option value="search">search</option>
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

	//  height and width for when initially attach to <svg> element 
	var _DEFAULT_VIEWPORT_HEIGHT = 1;
	var _DEFAULT_VIEWPORT_WIDTH = 1;

	//////  Overall image outside edge  constants:
	//  Left and Right constants
	var _PADDING_OVERALL_IMAGE_LEFT_SIDE = 3;
	var _PADDING_OVERALL_IMAGE_RIGHT_SIDE = 3;
	var _BORDER_OVERALL_IMAGE_ON_ONE_SIDE = 0;
	var _MAX_WIDTH_EXTRA_SUBTRACT_VALUE = 20;
	
	// the snap svg object
	var _GLOBAL_SNAP_SVG_OBJECT;
	
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
	var _PROTEIN_BAR_MAIN_RECTANGLE_LABEL_CLASS = "protein_bar_main_rectangle_jq";
	var _PROTEIN_BAR_PROTEIN_NAME_LABEL_CLASS = "protein_bar_protein_name_jq";
	var _PROTEIN_BAR_GROUP_ON_TOP_OF_MAIN_RECTANGLE_LABEL_CLASS = "protein_bar_group_on_top_of_main_rectangle_jq";

	///   CSS Class name constants for processing Protein Selection Overlay
	var _PROTEIN_OVERLAY_PROTEIN_SELECTED = "select-protein-overlay-user-selected-protein";

	////////////////////////////////////////////////////////////////////////
	/**
	 *  Mapping to/from JSON put on Hash in URL
	 *  Hash properties.  
	 *  The property names are referenced in this code
	 *  The property values are used as the property names in the JSON stored on the Hash
	 *  !!  Important !!  	Two properties cannot have the same value.
	 *	A value cannot be re-used for a new property 
	*/
	var HASH_OBJECT_PROPERTIES = {
			excludeTaxonomy : "a",
			excludeType : "b",
			cutoffs : "c",
			filterNonUniquePeptides : "d",
			filterOnlyOnePSM : "e",   //  only for backwards compatibility, used by LegacyJSONUpdater
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
			"color_by" : "y",
			"annTypeIdDisplay" : "z",
			"link_exclusion_data_current" : "A",
			"removeNonUniquePSMs" : "B",
			"minPSMs" : "C",
			
			//  For future additions, please continue the single character approach instead of using mnemonic or acronym
			//    1)  It will keep the overall JSON string shorter
			//    2)  It will be easier to see what is available to use
			
			//  For future additions, please do not use "-" in the property name
			
			"view-as-circle-plot" : "cir",
			"user_circle_diameter" : "ucd",
			"index-manager-data" : "imd",
			"version-number" : "vn"
	};

	/**
	 * Take a property name for HASH_OBJECT_PROPERTIES and return it's value.  Throw an exception if the property name is not found
	 */
	var hashObjectPropertyValueFromPropertyNameLookup = function( hashObjectPropertyLabel ) {
		var propertyValue = HASH_OBJECT_PROPERTIES[ hashObjectPropertyLabel ];
		if ( propertyValue === undefined || propertyValue === null ) {
			throw Error( "property name/label not found in 'HASH_OBJECT_PROPERTIES' : " + hashObjectPropertyLabel );
		}
		return propertyValue;
	};

	/**
	 * 
	 */
	var hashObjectManager = {
			hashObject : {},
			resetHashObject : function() {
				this.hashObject = {};
			},
			setOnHashObject : function( property, value ) {
				if ( property === undefined || property === null ) {
					throw Error( "property not found in 'HASH_OBJECT_PROPERTIES' " );
				}
				this.hashObject[ property ] = value;
			},
			getFromHashObject : function( property ) {
				if ( property === undefined || property === null ) {
					throw Error( "property not found in 'HASH_OBJECT_PROPERTIES' " );
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

	//   Objects of classes that have their own code, somewhat ordered by the order of the global variable dependencies

	var _imageProteinBarDataManager = ImageProteinBarDataManagerContructor();
	var _indexManager = new indexManager();
	var _customRegionManager = new customRegionManager();

	var _linkExclusionDataManager = LinkExclusionDataManagerContructor( { 
		imageProteinBarDataManager : _imageProteinBarDataManager,
		indexManager : _indexManager 
	} );

	var _proteinBarRegionSelectionsOverlayCode = ProteinBarRegionSelectionsOverlayCodeContructor( { 
		imageProteinBarDataManager : _imageProteinBarDataManager,
		indexManager : _indexManager 
	} );

	var _linkExclusionSelectionsOverlayCode = LinkExclusionSelectionsOverlayCodeContructor( {
		linkExclusionDataManager : _linkExclusionDataManager,
		imageProteinBarDataManager : _imageProteinBarDataManager,
		indexManager : _indexManager 
	} );

	var _colorManager = new ColorManager();
	var _circlePlotViewer = new circlePlotViewer();


	//   General

	/**
	 *  set to true when viewer is initialized
	 */
	var viewerInitialized = false;

	var _testThatCanGetSVGasString = true;

	var _computedMultiplier = 0;
	var _computedStandardMultiplier = 0;

	/**
	 * 
	 */
	var _dataLoadManager = { };


	//  From Page
	var _projectSearchIds;
	var _projectSearchIdsUserOrdered = "";

	//  Loaded data:
	
	var _allLinkersSupportedForLinkablePositions = undefined; // true or false returned from webservice
	
	var _proteins;
	var _proteinSequences = {};
	var _proteinSequenceTrypsinCutPoints = {};
	var _proteinTaxonomyIds = {};

	var _proteinLengths = {
			_proteinLengthsInternal : {},
			getProteinLength : function( proteinSequenceVersionIdString ) {
				var proteinLength = this._proteinLengthsInternal[ proteinSequenceVersionIdString ];
				if ( proteinLength === undefined ) {
					throw Error( "proteinLength not found in _proteinLengths._proteinLengthsInternal for proteinSequenceVersionIdString: " + proteinSequenceVersionIdString );
				}
				return proteinLength;
			},
			setProteinLength : function( proteinSequenceVersionIdString, proteinLength ) {
				this._proteinLengthsInternal[ proteinSequenceVersionIdString ] = proteinLength;
			}
	};

	var _proteinNames;
	var _proteinLinkPositions;
	var _proteinLooplinkPositions;
	var _proteinMonolinkPositions;
	var _linkPSMCounts = { };
	var _searches;
	var _projectSearchIdToSearchIdMapping;
	var _taxonomies;
	var _linkablePositions;
	var _coverages;
	var _ranges;

	//  From JSON (probably round trips from the input fields to the JSON in the Hash in the URL to these variables)
	var _psmPeptideCutoffsRootObjectStorage = {
			_psmPeptideCutoffsRootObject : null,
			setPsmPeptideCutoffsRootObject : function( psmPeptideCutoffsRootObject ) {
				this._psmPeptideCutoffsRootObject = psmPeptideCutoffsRootObject;
				//  Distribute the updated value to the JS code that loads and displays Peptide and PSM data
				webserviceDataParamsDistributionCommonCode.paramsForDistribution( { psmPeptideCutoffsRootObject : psmPeptideCutoffsRootObject } );
			},
			getPsmPeptideCutoffsRootObject : function( ) {
				return this._psmPeptideCutoffsRootObject;
			}
	};

	var _excludeTaxonomy;
	var _excludeType;
	var _filterNonUniquePeptides;
	var _minPSMs;
	var _filterOnlyOnePeptide;
	var _removeNonUniquePSMs;
	var _colorLinesBy = undefined;

	//  working data (does round trip to the JSON in the Hash in the URL)
	var _userScaleFactor;
	var _singleProteinBarOverallHeight = _SINGLE_PROTEIN_BAR_OVERALL_HEIGHT_DEFAULT;
	var _horizontalScalingPercent = 100;

	//  Working data, does not go to JSON  to Hash
	var _proteinBarsLeftEdge = _PADDING_OVERALL_IMAGE_LEFT_SIDE;
	var _proteinBarToolTip_template_HandlebarsTemplate = null;  ///  Compiled Handlebars template for the protein bar tool tip

	////////////////////////////////////////////

	//////////////   MAIN CODE

	/**
	 * 
	 */
	function populateProjectSearchIdToSearchIdMapping() {
		_projectSearchIdToSearchIdMapping = {};
		for ( var index = 0; index < _searches.length; index++ ) {
			var search = _searches[ index ];
			_projectSearchIdToSearchIdMapping[ search.id ] = search.searchId;
		}
	}

	/**
	 * 
	 */
	function convertProjectSearchIdArrayToSearchIdArray( projectSearchIdArray ) {
		if ( ! Array.isArray( projectSearchIdArray ) ) {
			throw Error( "projectSearchIdArray is not an array" );
		}
		var searchIdArray = [];
		for ( var index = 0; index < projectSearchIdArray.length; index++ ) {
			var projectSearchId = projectSearchIdArray[ index ];
			var searchId = _projectSearchIdToSearchIdMapping[ projectSearchId ];
			if ( searchId === undefined ) {
				throw Error( "Failed to find searchId mapping for projectSearchId: " + projectSearchId );
			}
			searchIdArray.push( searchId );
		}
		return searchIdArray;
	}

	/**
	 * 
	 */
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

	/*
	* Get the multiplier, which is the translation of residue position to pixel position relative to the
	* graphical beginning of the protein. E.g., position 1 translates to protein offset + 0
	*/
	function getMultiplier( ignoreUserScale ) {
		if ( ignoreUserScale || isSizingAutomatic() ) {
			return _computedMultiplier;
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

	/**
	 * get the right-most edge (in pixels) of the currently selected proteins
	 */
	function getCurrentRightmostProteinEdge( ) {
		var prots = _indexManager.getProteinList();
		var multiplier = getMultiplier();
		var rightmostEdge = 0;
		for ( var i = 0; i < prots.length; i++ ) {
			var protein = prots[ i ];
			var proteinOffsetFromLeftEdge = getProteinOffset( { proteinBarIndex : i } ) + _proteinBarsLeftEdge;
			var lengthOfProteinSequence = _proteinLengths.getProteinLength( protein );
			var widthOfProtein = lengthOfProteinSequence * multiplier; 
			var rightEdgeOfProtein = proteinOffsetFromLeftEdge + widthOfProtein;
			if ( rightEdgeOfProtein > rightmostEdge ) { 
				rightmostEdge = rightEdgeOfProtein; 
			}
		}
		return rightmostEdge;
	}

	/**
	 * get values for variables from the hash part of the URL as JSON
	 */
	function getRawJsonFromHash() {
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
			throw Error( "Failed to parse window hash string as JSON and decodeURI and then parse as JSON.  windowHashContentsMinusHashChar: " 
					+ windowHashContentsMinusHashChar );
		}
		//   Transform json on hash to expected object for rest of the code
		var json = jsonFromHash;
		return json;
	}

	/**
	 * get values for variables from the hash part of the URL as JSON
	 */
	function getJsonFromHash() {
		var json = getRawJsonFromHash();
		var hashPropertiesConstantsKeys = Object.keys( HASH_OBJECT_PROPERTIES );
		for ( var hashPropertiesConstantsKeysIndex = 0; hashPropertiesConstantsKeysIndex < hashPropertiesConstantsKeys.length; hashPropertiesConstantsKeysIndex++ ) {
			var jsonPropertyKey = hashPropertiesConstantsKeys[ hashPropertiesConstantsKeysIndex ];
			var hashPropertyKey = HASH_OBJECT_PROPERTIES[ jsonPropertyKey ];
			var valueForHashPropertyKey = json[ hashPropertyKey ];
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
		//  START: Special update to allow projectSearchId values to be added or removed from URL
		//  Update cutoffs to add defaults for search ids in defaults but not in cutoffs
		//  Update cutoffs to remove search ids not in defaults but in cutoffs
		var cutoffs_Searches = json.cutoffs.searches;
		var cutoffDefaultsFromPage = getCutoffDefaultsFromPage();
		var cutoffDefaultsFromPage_Searches = cutoffDefaultsFromPage.searches;
		//  Update cutoffs_Searches with values from cutoffDefaultsFromPage
		//      for any searches in cutoffDefaultsFromPage but not in cutoffs_Searches
		var cutoffDefaultsFromPageSrchIdArry = Object.keys( cutoffDefaultsFromPage_Searches );
		for ( var index = 0; index < cutoffDefaultsFromPageSrchIdArry.length; index++ ) {
			var cutoffDefaultsFromPageSrchId = cutoffDefaultsFromPageSrchIdArry[ index ];
			var cutoffs_SearchesEntryForDefProcessing = cutoffs_Searches[ cutoffDefaultsFromPageSrchId ];
			if ( cutoffs_SearchesEntryForDefProcessing === undefined || cutoffs_SearchesEntryForDefProcessing === null ) {
				// Not in cutoff values so copy from default
				var cutoffDefaultValues_ForSearch = cutoffDefaultsFromPage_Searches[ cutoffDefaultsFromPageSrchId ];
				var cloneOfDefaultValuesForSearch = jQuery.extend( true /* [deep ] */, {}, cutoffDefaultValues_ForSearch );
				cutoffs_Searches[ cutoffDefaultsFromPageSrchId ] = cloneOfDefaultValuesForSearch;
			}
		}
		//  Remove cutoffs in cutoffs_Searches for searches not in cutoffDefaultsFromPage
		var cutoffs_SearchesSrchIdArry = Object.keys( cutoffs_Searches );
		for ( var index = 0; index < cutoffs_SearchesSrchIdArry.length; index++ ) {
			var cutoffs_SearchesSrchId = cutoffs_SearchesSrchIdArry[ index ];
			var cutoffDefaultsFromPageForSrchId = cutoffDefaultsFromPage_Searches[ cutoffs_SearchesSrchId ];
			if ( cutoffDefaultsFromPageForSrchId === undefined || cutoffDefaultsFromPageForSrchId === null ) {
				// Not in default values so remove from input
				delete cutoffs_Searches[ cutoffs_SearchesSrchId ];
			}
		}
		//  END: Special update to allow projectSearchId values to be added or removed from URL
		//  Set default for exclude link type
		if ( json[ 'excludeType' ] === undefined ) {
			json[ 'excludeType' ] = EXCLUDE_LINK_TYPE_DEFAULT;
		}
		return json;
	}

	/**
	 * 
	 */
	function getCutoffDefaultsFromPage() {
		var $cutoffValuesRootLevelCutoffDefaults = $("#cutoffValuesRootLevelCutoffDefaults");
		var cutoffValuesRootLevelCutoffDefaultsString = $cutoffValuesRootLevelCutoffDefaults.val();
		try {
			var cutoffValuesRootLevelCutoffDefaults = JSON.parse( cutoffValuesRootLevelCutoffDefaultsString );
		} catch( e2 ) {
			throw Error( "Failed to parse cutoffValuesRootLevelCutoffDefaults string as JSON.  " +
					"Error Message: " + e2.message +
					".  cutoffValuesRootLevelCutoffDefaultsString: |" +
					cutoffValuesRootLevelCutoffDefaultsString +
					"|" );
		}
		return cutoffValuesRootLevelCutoffDefaults;
	}

	/**
	 * 
	 */
	function getValuesFromForm() {
		///// Called before this function is called
	//	hashObjectManager.resetHashObject();
		var getCutoffsFromThePageResult = cutoffProcessingCommonCode.getCutoffsFromThePage(  {  } );
		var getCutoffsFromThePageResult_FieldDataFailedValidation = getCutoffsFromThePageResult.getCutoffsFromThePageResult_FieldDataFailedValidation;
		if ( getCutoffsFromThePageResult_FieldDataFailedValidation ) {
			//  Cutoffs failed validation and error message was displayed
			//  EARLY EXIT from function
	//		return { output_FieldDataFailedValidation : getCutoffsFromThePageResult_FieldDataFailedValidation };
			throw Error( "Cutoffs are invalid so stop processing" );
		}
		var outputCutoffs = getCutoffsFromThePageResult.cutoffsByProjectSearchId;
		hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES.cutoffs, outputCutoffs );
		hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES.minPSMs, minimumPSM_Count_Filter.getMinPSMsFilter() );
		
		if ( $( "input#filterNonUniquePeptides" ).is( ':checked' ) ) {
			hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES.filterNonUniquePeptides, true );
		} else {
			hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES.filterNonUniquePeptides, false );
		}
		if ( $( "input#filterOnlyOnePeptide" ).is( ':checked' ) ) {
			hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES.filterOnlyOnePeptide, true );
		} else {
			hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES.filterOnlyOnePeptide, false );
		}
		if ( $( "input#removeNonUniquePSMs" ).is( ':checked' ) ) {
			hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES.removeNonUniquePSMs, true );
		} else {
			hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES.removeNonUniquePSMs, false );
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

	/**
	 * build a query string based on selections by user
	 */
	function updateURLHash( useSearchForm ) {
		hashObjectManager.resetHashObject();
		// add in the version number
		hashObjectManager.setOnHashObject(  HASH_OBJECT_PROPERTIES[ "version-number" ], _JSON_VERSION_NUMBER );	
		if ( ! useSearchForm ) {
	//		build hash string from previous search, they've just updated the drawing
	//		add taxonomy exclusions
			hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES.excludeTaxonomy, _excludeTaxonomy );
	//		add type exclusions
			hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES.excludeType, _excludeType );
	//		add psm/peptide cutoffs
			hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES.cutoffs, _psmPeptideCutoffsRootObjectStorage.getPsmPeptideCutoffsRootObject() );
	//		add filter on minimum number of PSMs
			hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES.minPSMs, _minPSMs );
	//		add filter out non unique peptides
			hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES.filterNonUniquePeptides, _filterNonUniquePeptides );
	//		add filter out only one Peptide
			hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES.filterOnlyOnePeptide, _filterOnlyOnePeptide );
	//		add filter out non unique PSMs
			hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES.removeNonUniquePSMs, _removeNonUniquePSMs );
			
		} else {
	//		build hash string from values in form, they've requested a data refresh
			getValuesFromForm();
		}
		//  Output the selected Annotation data for display
		var getAnnotationTypeDisplayFromThePageResult = annotationDataDisplayProcessingCommonCode.getAnnotationTypeDisplayFromThePage( {} );
		var annTypeIdDisplay = getAnnotationTypeDisplayFromThePageResult.annTypeIdDisplayByProjectSearchId;
		hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES.annTypeIdDisplay, annTypeIdDisplay );
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
		if ( $( "input#view-as-circle-plot" ).is( ':checked' ) ) {
			hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES["view-as-circle-plot"], true );
		} else {
			hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES["view-as-circle-plot"], false );
		}
		// save the index manager data
		hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES["index-manager-data"], _indexManager.getDataForHash() );
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
		if( _circlePlotViewer.getDiameter() ) {
			hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES["user_circle_diameter"], _circlePlotViewer.getDiameter() );
		}
		hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES["annotation_type"], $("#annotation_type").val() );
		hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES["vertical-bar-spacing"], _singleProteinBarOverallHeight );
		hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES["horizontal-bar-scaling"], _horizontalScalingPercent );
		//  Add in protein bar data
		hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES["protein_bar_data"], _imageProteinBarDataManager.getDataForHash() );
		//  Add in link exclusion data
		hashObjectManager.setOnHashObject( HASH_OBJECT_PROPERTIES["link_exclusion_data_current"], _linkExclusionDataManager.getDataForHash() );
		
		var hashObject = hashObjectManager.getHashObject();
		updateURLHashWithJSONObject( hashObject );
	}

	/**
	 * 
	 */
	function updateURLHashWithJSONObject( jsonObject ) {
		var newHash = JSON.stringify( jsonObject );	
		var newHashEncodedToEncodedURIComponent = LZString.compressToEncodedURIComponent( newHash );
		try {
			window.location.hash = newHashEncodedToEncodedURIComponent;
		} catch ( e ) {
			//  TODO  Need to handle this error.  
			//     The user really shouldn't continue since the settings are not being stored in the Hash
			console.log( "Update window.location.hash Failed: e: " + e );
		}
	}

	/**
	 * 
	 */
	function buildQueryStringFromHash() {
		var queryString = "?";
		var items = new Array();
		var json = getJsonFromHash();
		//  _projectSearchIds from the page
		for ( var i = 0; i < _projectSearchIds.length; i++ ) {
			items.push( "projectSearchId=" + _projectSearchIds[ i ] );
		}
		if ( _projectSearchIdsUserOrdered && _projectSearchIdsUserOrdered !== "" ) {
			items.push( "ds=" + _projectSearchIdsUserOrdered );
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
		var psmPeptideCutoffsForProjectSearchIds_JSONString = JSON.stringify( cutoffs );
		var psmPeptideCutoffsForProjectSearchIds_JSONStringEncoded = encodeURIComponent( psmPeptideCutoffsForProjectSearchIds_JSONString );
		items.push( "psmPeptideCutoffsForProjectSearchIds=" + psmPeptideCutoffsForProjectSearchIds_JSONStringEncoded );
		
		var minPSMs = json.minPSMs;
		if ( minPSMs === undefined || minPSMs === null ) {
			minPSMs = minimumPSM_Count_Filter.getDefault();
		}
		items.push( "minPSMs=" + minPSMs );
		
		if ( json.filterNonUniquePeptides != undefined && json.filterNonUniquePeptides ) {
			items.push( "filterNonUniquePeptides=on" );
		}
		if ( json.filterOnlyOnePeptide != undefined && json.filterOnlyOnePeptide ) {
			items.push( "filterOnlyOnePeptide=on" );
		}
		if ( json.removeNonUniquePSMs != undefined && json.removeNonUniquePSMs ) {
			items.push( "removeNonUniquePSMs=on" );
		}
		queryString += items.join( "&" );
		return queryString;
	}

	/**
	 * Called from button "Update From Database" on the page
	 *   Refresh the data on the page
	 */
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

	/**
	 * Load protein sequence coverage data for a specific protein
	 */
	function loadSequenceCoverageDataForProtein( selProteinsForSeqCov, doDraw ) {
		incrementSpinner();				// create spinner
		var url = "services/sequenceCoverage/getDataForProtein";
		url += buildQueryStringFromHash();
		var urlAdditionproteinSequenceVersionIds = "";
		for ( var index = 0; index < selProteinsForSeqCov.length; index++ ) {
			var protein = selProteinsForSeqCov[ index ];
			urlAdditionproteinSequenceVersionIds += "&proteinSequenceVersionId=" + protein;
		}
		url += urlAdditionproteinSequenceVersionIds;
		console.log( "Loading sequence coverage data for selProteinsForSeqCov: " + urlAdditionproteinSequenceVersionIds );
		$.ajax({
			type: "GET",
			url: url,
			dataType: "json",
			success: function(data)	{
				try {
					if ( _ranges == undefined ) {
						_coverages = data.coverages;
						_ranges = data.ranges;
					} else {
						for ( var index = 0; index < selProteinsForSeqCov.length; index++ ) {
							var protein = selProteinsForSeqCov[ index ];
							_coverages[ protein ] = data[ 'coverages' ][ protein ];
							_ranges[ protein ] = data[ 'ranges' ][ protein ];
						}
					}
					decrementSpinner();
					loadDataAndDraw( doDraw );
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
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

	/**
	 * Load protein sequence data for a list of proteins
	 */
	function loadProteinSequenceDataForProtein( proteinIdsToGetSequence, doDraw ) {
		console.log( "Loading protein sequence data for proteins: " + proteinIdsToGetSequence );
		incrementSpinner();				// create spinner
		var url = "services/proteinSequence/getDataForProtein";
		var project_id = $("#project_id").val();
		if ( project_id === undefined || project_id === null 
				|| project_id === "" ) {
			throw Error( '$("#project_id").val() returned no value' );
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
					try {
						var returnedProteinIdsAndSequences = data;  //  The property names are the protein ids and the property values are the sequences
						// copy the returned sequences into the global object
						var returnedProteinIdsAndSequences_Keys = Object.keys( returnedProteinIdsAndSequences );
						for ( var keysIndex = 0; keysIndex < returnedProteinIdsAndSequences_Keys.length; keysIndex++ ) {
							var proteinId = returnedProteinIdsAndSequences_Keys[ keysIndex ];
							_proteinSequences[ proteinId ] = returnedProteinIdsAndSequences[ proteinId ];
							_proteinLengths.setProteinLength( proteinId, returnedProteinIdsAndSequences[ proteinId ].length )
						}
						decrementSpinner();
						loadDataAndDraw( doDraw );
					} catch( e ) {
						reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
						throw e;
					}
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

	/**
	 * Load mapping of protein id to  taxonomy id data for a list of proteins
	 */
	function loadProteinTaxonomyIdDataForProtein( proteinIds, doDraw ) {
		console.log( "Loading protein taxonomy id data for proteins: " + proteinIds );
		incrementSpinner();				// create spinner
		var url = "services/proteinTaxonomyId/getDataForProtein";
		var ajaxRequestData = {
				projectSearchIds : _projectSearchIds,
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
					try {
						var returnedProteinIdsAndTaxonomyIds = data;  //  The property names are the protein ids and the property values are the taxonomy ids
						// copy the returned taxonomy ids into the global object
						var returnedProteinIdsAndTaxonomyIds_Keys = Object.keys( returnedProteinIdsAndTaxonomyIds );
						if ( returnedProteinIdsAndTaxonomyIds_Keys.length === 0 ) {
							throw Error( "No taxonomy ids returned. proteinIds: " + proteinIds );
						}
						for ( var keysIndex = 0; keysIndex < returnedProteinIdsAndTaxonomyIds_Keys.length; keysIndex++ ) {
							var proteinId = returnedProteinIdsAndTaxonomyIds_Keys[ keysIndex ];
							_proteinTaxonomyIds[ proteinId ] = returnedProteinIdsAndTaxonomyIds[ proteinId ];
						}
						decrementSpinner();
						loadDataAndDraw( doDraw );
					} catch( e ) {
						reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
						throw e;
					}
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

	/**
	 * Toggle the visibility of monolink data on the viewer
	 */
	function loadMonolinkData( doDraw ) {
		console.log( "Loading monolink data." );
		if ( $( "input#show-monolinks" ).is( ':checked' ) ) {
			if ( _proteinMonolinkPositions != undefined ) {
				reportLinkDataLoadComplete( 'monolinks', doDraw );
			} else {
				incrementSpinner();				// create spinner
				var url = "services/imageViewer/getMonolinkData";
				url += buildQueryStringFromHash();
				// var request = 
				$.ajax({
						type: "GET",
						url: url,
						dataType: "json",
						success: function(data)	{
							try {
								_proteinMonolinkPositions = data.proteinMonoLinkPositions;
								decrementSpinner();
								reportLinkDataLoadComplete( 'monolinks', doDraw );
							} catch( e ) {
								reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
								throw e;
							}
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

	/**
	 * 
	 */
	function loadMonolinkPSMCounts( doDraw ) {
		console.log( "Loading monolink PSM counts." );
		if ( $( "input#show-monolinks" ).is( ':checked' ) &&  $( "input#shade-by-counts" ).is( ':checked' ) ) {
			if ( _linkPSMCounts.monolink != undefined ) {
				reportLinkDataLoadComplete( 'monolinkPSMs', doDraw );
			} else {
				incrementSpinner();				// create spinner
				var url = "services/imageViewer/getMonolinkPSMCounts";
				url += buildQueryStringFromHash();
				// var request = 
				$.ajax({
						type: "GET",
						url: url,
						dataType: "json",
						success: function(data)	{
							try {
								_linkPSMCounts.monolink = data.monolinkPSMCounts;
								decrementSpinner();
								reportLinkDataLoadComplete( 'monolinkPSMs', doDraw );
							} catch( e ) {
								reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
								throw e;
							}
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

	/**
	 * Toggle the visibility of looplink data on the viewer
	 */
	function loadLooplinkData( doDraw ) {
		console.log( "Loading looplink data." );
		if ( $( "input#show-looplinks" ).is( ':checked' ) ) {
			if ( _proteinLooplinkPositions != undefined ) {
				reportLinkDataLoadComplete( 'looplinks', doDraw );
			} else {
				incrementSpinner();				// create spinner
				var url = "services/imageViewer/getLooplinkData";
				url += buildQueryStringFromHash();
				// var request = 
				$.ajax({
						type: "GET",
						url: url,
						dataType: "json",
						success: function(data)	{
							try {
								_proteinLooplinkPositions = data.proteinLoopLinkPositions;
								decrementSpinner();
								reportLinkDataLoadComplete( 'looplinks', doDraw );
							} catch( e ) {
								reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
								throw e;
							}
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

	/**
	 * 
	 */
	function loadLooplinkPSMCounts( doDraw ) {
		console.log( "Loading looplink PSM counts." );
		if ( $( "input#show-looplinks" ).is( ':checked' ) &&  $( "input#shade-by-counts" ).is( ':checked' ) ) {
			if ( _linkPSMCounts.looplink != undefined ) {
				reportLinkDataLoadComplete( 'looplinkPSMs', doDraw );
			} else {
				incrementSpinner();				// create spinner
				var url = "services/imageViewer/getLooplinkPSMCounts";
				url += buildQueryStringFromHash();
				// var request = 
				$.ajax({
						type: "GET",
						url: url,
						dataType: "json",
						success: function(data)	{
							try {
								_linkPSMCounts.looplink = data.looplinkPSMCounts;
								decrementSpinner();
								reportLinkDataLoadComplete( 'looplinkPSMs', doDraw );
							} catch( e ) {
								reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
								throw e;
							}
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

	/**
	 * Toggle the visibility of crosslink data on the viewer
	 */
	function loadCrosslinkData( doDraw ) {
		console.log( "Loading crosslink data." );
		if ( $( "input#show-crosslinks" ).is( ':checked' ) ||  $( "input#show-self-crosslinks" ).is( ':checked' ) ) {
			if ( _proteinLinkPositions != undefined ) {
				reportLinkDataLoadComplete( 'crosslinks', doDraw );
			} else {
				incrementSpinner();				// create spinner
				var url = "services/imageViewer/getCrosslinkData";
				url += buildQueryStringFromHash();
				$.ajax({
						type: "GET",
						url: url,
						dataType: "json",
						success: function(data)	{
							try {
								_proteinLinkPositions = data.proteinLinkPositions;
								decrementSpinner();
								reportLinkDataLoadComplete( 'crosslinks', doDraw );
							} catch( e ) {
								reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
								throw e;
							}
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
			reportLinkDataLoadComplete( 'crosslinks', doDraw );
		}
	}

	/**
	 * 
	 */
	function loadCrosslinkPSMCounts( doDraw ) {
		console.log( "Loading crosslink PSM counts." );
		if ( ( $( "input#show-self-crosslinks" ).is( ':checked' ) || $( "input#show-crosslinks" ).is( ':checked' ) ) &&  $( "input#shade-by-counts" ).is( ':checked' ) ) {
			if ( _linkPSMCounts.crosslink != undefined ) {
				reportLinkDataLoadComplete( 'crosslinkPSMs', doDraw );
			} else {
				incrementSpinner();				// create spinner
				var url = "services/imageViewer/getCrosslinkPSMCounts";
				url += buildQueryStringFromHash();
				// var request = 
				$.ajax({
						type: "GET",
						url: url,
						dataType: "json",
						success: function(data)	{
							try {
								_linkPSMCounts.crosslink = data.crosslinkPSMCounts;
								decrementSpinner();
								reportLinkDataLoadComplete( 'crosslinkPSMs', doDraw );
							} catch( e ) {
								reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
								throw e;
							}
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


	/**
	 * 
	 */
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

	/**
	 * 
	 */
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

	/**
	 * ensure the necessary data are collected before viewer is drawn
	 */
	function loadDataAndDraw( doDraw, loadComplete ) {
		if( !loadComplete ) { return loadRequiredLinkData( doDraw ); }
		// only load sequences for visible proteins
		var selectedProteins = _indexManager.getProteinList()
		
		
		if ( selectedProteins && selectedProteins.length > 0 ) {
			$("#manage_regios_exclusions_links_block").show();
			$("#manage_regios_exclusions_links_disabled_block").hide();
		} else {
			$("#manage_regios_exclusions_links_block").hide();
			$("#manage_regios_exclusions_links_disabled_block").show();
		}
		
		
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
			if ( _proteinTaxonomyIds[ proteinId ] === undefined ) {
				proteinIdsToGetTaxonomyIds.push( proteinId );
			}
		}
		if ( proteinIdsToGetTaxonomyIds.length > 0 ) {
			return loadProteinTaxonomyIdDataForProtein( proteinIdsToGetTaxonomyIds, doDraw );
		}
		// only load annotation data ( other than Sequence Coverage ) for visible proteins
		var selectedProteins = _indexManager.getProteinList();
		var annotationType = $("#annotation_type").val();
		var annotationType_DisplayText = $("#annotation_type option:selected").text();
		if ( annotationType === SELECT_ELEMENT_ANNOTATION_TYPE_SEQUENCE_COVERAGE ) {
			//  Call loadSequenceCoverageDataForProtein with proteins that need sequence coverage
			//     loaded from server
			var selProtsFrSeqCov = [];
			for ( var i = 0; i < selectedProteins.length; i++ ) {
				var selectedProtein = selectedProteins[ i ];
				if ( _ranges == undefined || _ranges[ selectedProtein ] == undefined ) {
					var foundSelectedProteinInSelProtsFrSeqCov = false;
					//  If not already in selProtsFrSeqCov, add it
					for ( var spfscIndex = 0; spfscIndex < selProtsFrSeqCov.length; spfscIndex++ ) {
						var selProtFrSeqCov = selProtsFrSeqCov[ spfscIndex ];
						if ( selProtFrSeqCov === selectedProtein ) {
							foundSelectedProteinInSelProtsFrSeqCov = true;
						}
					}
					if ( ! foundSelectedProteinInSelProtsFrSeqCov ) {
						selProtsFrSeqCov.push( selectedProtein );
					} 
				}
			}
			if ( selProtsFrSeqCov.length > 0 ) {
				return loadSequenceCoverageDataForProtein( selProtsFrSeqCov, doDraw );
			}
		} else if ( annotationType === SELECT_ELEMENT_ANNOTATION_TYPE_CUSTOM ) {

			var proteinsToLoad = [];
			for ( var i = 0; i < selectedProteins.length; i++ ) {
				var selectedProtein = selectedProteins[ i ];
				if ( _customRegionManager._customRegionAnnotationData[ selectedProtein ] == undefined ) {
					proteinsToLoad.push( selectedProtein );
				}
			}
			if ( proteinsToLoad.length > 0 ) {
				return _customRegionManager.getCustomRegionDataForProteinsViaAjaxForViewerDisplay( proteinsToLoad, doDraw );
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
					proteinNames : _proteinNames
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
				throw Error( "unknown annotationType: " + annotationType );
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

	/**
	 * 
	 */
	function loadDataFromService() {
		console.log( "Loading protein data." );
		incrementSpinner();				// create spinner
		var url = "services/imageViewer/getProteinData";
		url += buildQueryStringFromHash();
		$.ajax({
				type: "GET",
				url: url,
				dataType: "json",
				success: function(data)	{
					try {
						// handle searches
						_searches = data.searches;
						populateProjectSearchIdToSearchIdMapping();
						// handle proteins
						_proteins = data.proteins;
						// handle protein names
						_proteinNames = data.proteinNames;
						// positions of all linkable-positions
						_linkablePositions = data.linkablePositions;
						// handle other search parameters
						_psmPeptideCutoffsRootObjectStorage.setPsmPeptideCutoffsRootObject( data.cutoffs );
						_excludeTaxonomy = data.excludeTaxonomy;
						_excludeType = data.excludeType;
						_filterNonUniquePeptides = data.filterNonUniquePeptides;
						_minPSMs = data.minPSMs;
						_filterOnlyOnePeptide = data.filterOnlyOnePeptide;
						_removeNonUniquePSMs = data.removeNonUniquePSMs;
						
						//  Distribute the updated value to the JS code that loads and displays Peptide and PSM data
						webserviceDataParamsDistributionCommonCode.paramsForDistribution( { 
							filterNonUniquePeptides : _filterNonUniquePeptides,
							minPSMs : _minPSMs,
							filterOnlyOnePeptide : _filterOnlyOnePeptide,
							removeNonUniquePSMs : _removeNonUniquePSMs
						} );
						
						_taxonomies = data.taxonomies;
						// remove any now-invalid proteins from the index manager
						if( _indexManager ) {
							_indexManager.removeInvalidProteins( _proteins );
						}
						//  Populate these from the Hash	        		
						populateFromHash_imageProteinBarDataManager();
						//  Populate these from the Hash	        		
						populateFromHash_linkExclusionDataManager();

						// remove any now-valid entries from the protein bar data manager
						_imageProteinBarDataManager.removeInvalidEntries();
						populateNavigation();
						//  Populate from local variables
						populateSearchForm();
						populateSelectProteinSelect();
						initializeViewer();

						if ( _allLinkersSupportedForLinkablePositions === undefined ) {
							
							// Update the first time only
							_allLinkersSupportedForLinkablePositions = data.allLinkersSupportedForLinkablePositions;
							
							if ( _allLinkersSupportedForLinkablePositions === undefined ) {
								//  Not returned from webservice so default to true
								_allLinkersSupportedForLinkablePositions = true;
							}
						
							updatePageAndHashIfNeededFor_allLinkersSupportedForLinkablePositions();
						}
						
						updateURLHash( false /* useSearchForm */ );
						decrementSpinner();
						loadDataAndDraw( true /* doDraw */ );
					} catch( e ) {
						reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
						throw e;
					}
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
	
	/**
	 * 
	 */
	function updatePageAndHashIfNeededFor_allLinkersSupportedForLinkablePositions() {
		
		if ( _allLinkersSupportedForLinkablePositions ) {
			//  Yes all linkers support Linkable positions so:
			//     Hide disabled option
			//     Show option for selecting it
			$("#show-linkable-positions-disabled-container").hide();
			$("#show-linkable-positions-container").show();
			
		} else {
			//  Not all linkers support Linkable positions so:
			//    Hide option for selecting it and clear that checkbox
			//    Show disabled option
			$("#show-linkable-positions-container").hide();
			$("#show-linkable-positions-disabled-container").show();

			$( "input#show-linkable-positions" ).prop('checked', false );
		}
	}

	/**
	 * populate _imageProteinBarDataManager from the hash
	 */
	function populateFromHash_imageProteinBarDataManager(){
		var json = getJsonFromHash();
			
		var proteinBarData = json['protein_bar_data'];
		if ( proteinBarData !== undefined ) {
			_imageProteinBarDataManager.replaceInternalDataWithDataInHash( proteinBarData );
		}
	}

	/**
	 * populate _linkExclusionDataManager from the hash
	 */
	function populateFromHash_linkExclusionDataManager(){
		var json = getJsonFromHash();
		
		var linkExclusionData = json['link_exclusion_data_current'];
		if ( linkExclusionData !== undefined ) {
			_linkExclusionDataManager.replaceInternalDataWithDataInHash( linkExclusionData );
		}
	}


	/**
	 * Populates the index manager data from the hash.
	 */
	function populateIndexManagerFromHash() {
		var json = getJsonFromHash();	
		
		var data = json[ 'index-manager-data' ];
		if ( data ) {
			_indexManager.replaceInternalDataWithDataInHash( data );
			return;
		}
	}

	/**
	 * 
	 */
	function populateSearchForm() {
		var psmPeptideCutoffsRootObject = _psmPeptideCutoffsRootObjectStorage.getPsmPeptideCutoffsRootObject();
		webserviceDataParamsDistributionCommonCode.paramsForDistribution( { cutoffs : psmPeptideCutoffsRootObject } );
		cutoffProcessingCommonCode.putCutoffsOnThePage(  { cutoffs : psmPeptideCutoffsRootObject } );
		minimumPSM_Count_Filter.saveMinPSMsFilter( { minPSMs : _minPSMs } );
		$( "input#filterNonUniquePeptides" ).prop('checked', _filterNonUniquePeptides);
		$( "input#filterOnlyOnePeptide" ).prop('checked', _filterOnlyOnePeptide);
		$( "input#removeNonUniquePSMs" ).prop('checked', _removeNonUniquePSMs);
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
			try {
				defaultPageView.searchFormChanged_ForDefaultPageView();;
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
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

	/**
	 * 
	 */
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
		$( "input#view-as-circle-plot" ).prop('checked', json[ 'view-as-circle-plot' ] );
		_colorLinesBy = json[ 'color_by' ];
		// Backwards compatable color-by-search
		if ( json[ 'color-by-search' ] ) {
			_colorLinesBy = SELECT_ELEMENT_COLOR_BY_SEARCH;
		}
		if ( _colorLinesBy != undefined ) {
			$("#color_by").val( _colorLinesBy );
		}
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
		if( json[ 'user_circle_diameter' ] ) {
			_circlePlotViewer.setUserDiameter( json[ 'user_circle_diameter' ] );
		}
		updateContextOptions();
	}

	/**
	 * 
	 */
	function showSelectedProteins() {
		// add the appropriate selected proteins
		var $selected_proteins_container = $("#selected_proteins_container");
		var $protein_select_text_container_jq = $selected_proteins_container.find(".protein_select_text_container_jq");
		$protein_select_text_container_jq.qtip('destroy', true); // Immediately destroy all tooltips belonging to the selected elements
		var $tool_tip_attached_jq_Children = $selected_proteins_container.find(".tool_tip_attached_jq");
		$tool_tip_attached_jq_Children.qtip('destroy', true); // Immediately destroy all tooltips belonging to the selected elements
		$selected_proteins_container.empty();
		var selectedProteinIds = _indexManager.getProteinArray();
		if ( !selectedProteinIds || selectedProteinIds.length < 1 || _proteins.length < 1 ) {
			$("#no_proteins_add_protein_outer_container").show();
			$("#svg_image_outer_container_div").hide();
			$("#manage_regios_exclusions_links_block").hide();
			$("#manage_regios_exclusions_links_disabled_block").show();
			return;
		} else {
			$("#svg_image_outer_container_div").show();
			$("#no_proteins_add_protein_outer_container").hide();
			$("#manage_regios_exclusions_links_block").show();
			$("#manage_regios_exclusions_links_disabled_block").hide();
		}
		var $selected_protein_entry_template = $("#selected_protein_entry_template");
		var selectedProteinEntry_template_handlebarsSource = $selected_protein_entry_template.text();
		if ( selectedProteinEntry_template_handlebarsSource === undefined ) {
			throw Error( "selectedProteinEntry_template_handlebarsSource === undefined" );
		}
		if ( selectedProteinEntry_template_handlebarsSource === null ) {
			throw Error( "selectedProteinEntry_template_handlebarsSource === null" );
		}
		if ( selectedProteinIds ) {
			var selectedProteinEntry_HandlebarsTemplate = Handlebars.compile( selectedProteinEntry_template_handlebarsSource );
			for ( var i = 0; i < selectedProteinIds.length; i++ ) {
				var proteinId = selectedProteinIds[ i ].pid;
				var proteinName = getProteinName( proteinId );
				var selectedProteinEntryValue = { uid : selectedProteinIds[ i ].uid, proteinId : proteinId , proteinName : proteinName };
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
			try {
				openSelectProteinSelect( { clickedThis : this } );
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		} );
		//  Select Delete icon of each Selected protein
		var $protein_delete_icon_jq_Items = $selected_proteins_container.find(".protein_delete_icon_jq");
		//  Add click handler for removing the protein
		$protein_delete_icon_jq_Items.click( function( eventObject ) {
			try {
				processClickOnRemoveSelectedProtein( { clickedThis : this } );
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		} );
		// Add General tool tips to everything in the container.  (including on Delete icon)
		addToolTips( $selected_proteins_container );
		$selected_proteins_container.sortable( {
			//  On sort start, call this function
			start : processSortStartSelectedProteins,
			//  This event is triggered when the user stopped sorting and the DOM position has changed.
			//        (User released the item they were dragging and items are now in "after sort" order)
			update : processSortUpdateSelectedProteins	,
			// Added to ensure click event doesn't fire in Firefox
			helper:'clone'
		} );
	}

	/**
	 * 
	 */
	function processSortStartSelectedProteins( event, ui ) {
		var $item = ui.item;
		//  ui.item is  <div class="outer-float  protein_select_outer_block_jq" style="" >
		var $sort_handle_jq = $item.find(".sort_handle_jq");
		$sort_handle_jq.qtip('toggle', false); // Immediately hide all tooltips belonging to the selected elements
		var $protein_select_text_container_jq = $item.find(".protein_select_text_container_jq");
		$protein_select_text_container_jq.qtip('toggle', false); // Immediately hide all tooltips belonging to the selected elements
	}

	/**
	 * 
	 */
	function processSortUpdateSelectedProteins( event, ui ) {
		var $item = ui.item;
		var uid = $item.attr( "data-uid" );	// the uid of the protein instance we moved
		// Move the moved protein to the correct index position in the index manager
		var $selected_proteins_container = $("#selected_proteins_container");
		var $protein_select_outer_block_jq_Items = $selected_proteins_container.find(".protein_select_outer_block_jq");
		$protein_select_outer_block_jq_Items.each( function( index, element ) {
			var $this = $( this );
			var tuid = $this.attr("data-uid");
			if( tuid === uid ) {
				// index is now the position to which we moved the protein
				_indexManager.moveEntryToIndexPosition( uid, index );
			}
		} );
		updateURLHash( false /* useSearchForm */ );
		//showSelectedProteins();
		drawSvg();
	}

	/**
	 * 
	 */
	function processClickOnRemoveSelectedProtein( params ) {
		try {
			var clickedThis = params.clickedThis;
			var $clickedThis = $( clickedThis );
			$clickedThis.qtip('destroy', true); // Immediately destroy all tooltips belonging to the selected elements
			var uid = $clickedThis.attr("data-uid");
			_imageProteinBarDataManager.removeItemByUID( uid );	
			_indexManager.removeEntryByUID( uid );
			updateURLHash( false /* useSearchForm */ );
			showSelectedProteins();
			drawSvg();
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	}

	/**
	 * 
	 */
	function getNavigationJSON_Not_for_Image_Or_Structure() {
		var json = getJsonFromHash();
		///   Serialize cutoffs to JSON
		var cutoffs = json.cutoffs;
		var annTypeIdDisplay = json.annTypeIdDisplay;
		//  Layout of baseJSONObject  matches Java class A_QueryBase_JSONRoot
		var baseJSONObject = { cutoffs : cutoffs, annTypeIdDisplay : annTypeIdDisplay };
		//  Add to baseJSONObject
		if ( json.filterNonUniquePeptides !== undefined ) {
			baseJSONObject.filterNonUniquePeptides = json.filterNonUniquePeptides;
		}
		if ( json.minPSMs !== undefined ) {
			baseJSONObject.minPSMs = json.minPSMs;
		}
		if ( json.filterOnlyOnePeptide !== undefined ) {
			baseJSONObject.filterOnlyOnePeptide = json.filterOnlyOnePeptide;
		}
		if ( json.removeNonUniquePSMs !== undefined ) {
			baseJSONObject.removeNonUniquePSMs = json.removeNonUniquePSMs;
		}
		if ( json.excludeTaxonomy !== undefined ) {
			baseJSONObject.excludeTaxonomy = json.excludeTaxonomy;
		}
		return baseJSONObject;
	}

	/**
	 * 
	 */
	function populateNavigation() {
		var queryString = "?";
		var items = new Array();
		for ( var i = 0; i < _projectSearchIds.length; i++ ) {
			items.push( "projectSearchId=" + _projectSearchIds[ i ] );
		}
		if ( _projectSearchIdsUserOrdered && _projectSearchIdsUserOrdered !== "" ) {
			items.push( "ds=" + _projectSearchIdsUserOrdered );
		}
		var baseJSONObject = getNavigationJSON_Not_for_Image_Or_Structure();
		var psmPeptideCutoffsForProjectSearchIds_JSONString = JSON.stringify( baseJSONObject );
		var psmPeptideCutoffsForProjectSearchIds_JSONStringEncodedURIComponent = encodeURIComponent( psmPeptideCutoffsForProjectSearchIds_JSONString ); 
		//  Parameter name matches standard form parameter name for JSON
		items.push( "queryJSON=" + psmPeptideCutoffsForProjectSearchIds_JSONStringEncodedURIComponent );
		queryString += items.join( "&" );
		var html = "";
		
		//  Add QC Link
		var qc_page_link_text = $("#qc_page_link_text").html();
		var qc_page_link_tooltip = $("#qc_page_link_tooltip").html();
		
		var qcNavHTML = "<span class=\"tool_tip_attached_jq\" data-tooltip=\"" + qc_page_link_tooltip + "\" " +
		"style=\"white-space:nowrap;\" >[<a href=\"";

		var qcQueryString = "?";
		for ( var j = 0; j < _projectSearchIds.length; j++ ) {
			if ( j > 0 ) {
				qcQueryString += "&";
			}
			qcQueryString += "projectSearchId=" + _projectSearchIds[ j ];
		}
		if ( _projectSearchIdsUserOrdered && _projectSearchIdsUserOrdered !== "" ) {
			qcQueryString += "&ds=" + _projectSearchIdsUserOrdered;
		}
		
		var qcJSON = { };
		//  Add Filter cutoffs
		qcJSON[ 'cutoffs' ] = _psmPeptideCutoffsRootObjectStorage.getPsmPeptideCutoffsRootObject();
		//  Add Ann Type Display
		var annTypeIdDisplay = baseJSONObject.annTypeIdDisplay;
		qcJSON[ 'annTypeIdDisplay' ] = annTypeIdDisplay;
		var qcJSONString = encodeURIComponent( JSON.stringify( qcJSON ) );
		qcNavHTML += "qc.do" + qcQueryString + "#" + qcJSONString;

		qcNavHTML += "\">" + qc_page_link_text + "</a>]</span>";
		html += qcNavHTML;
		
		if ( _searches.length > 1 ) {
			html += " <span class=\"tool_tip_attached_jq\" data-tooltip=\"View peptides\" style=\"white-space:nowrap;\" >[<a href=\"mergedPeptide.do" + queryString + "\">Peptide View</a>]</span>";
			html += " <span class=\"tool_tip_attached_jq\" data-tooltip=\"View proteins\" style=\"white-space:nowrap;\" >[<a href=\"mergedCrosslinkProtein.do" + queryString + "\">Protein View</a>]</span>";
			html += " <span class=\"tool_tip_attached_jq\" data-tooltip=\"View protein coverage report\" style=\"white-space:nowrap;\" >[<a href=\"mergedProteinCoverageReport.do" + queryString + "\">Coverage Report</a>]</span>";
		} else {
			
			//  Add Peptide Link
			html += " [<a class=\"tool_tip_attached_jq\" data-tooltip=\"View peptides\" href='";
			var viewSearchPeptideDefaultPageUrl = $("#viewSearchPeptideDefaultPageUrl").val();
			if ( viewSearchPeptideDefaultPageUrl === undefined || viewSearchPeptideDefaultPageUrl === "" ) {
				html += "peptide.do" + queryString;
			} else {
				html += viewSearchPeptideDefaultPageUrl;
			}
			html += "'>Peptide View</a>]";
			//  Add Protein View Link
			html += " [<a class=\"tool_tip_attached_jq\" data-tooltip=\"View proteins\" href='";
			var viewSearchCrosslinkProteinDefaultPageUrl = $("#viewSearchCrosslinkProteinDefaultPageUrl").val();
			if ( viewSearchCrosslinkProteinDefaultPageUrl === undefined || viewSearchCrosslinkProteinDefaultPageUrl === "" ) {
				html += "crosslinkProtein.do" + queryString;
			} else {
				html += viewSearchCrosslinkProteinDefaultPageUrl;
			}
			html += "'>Protein View</a>]";
			//  Add Coverage Report Link
			html += " [<a class=\"tool_tip_attached_jq\" data-tooltip=\"View protein coverage report\" href='";
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
				"style=\"white-space:nowrap;\" >[<a href=\"";
			var viewMergedStructureDefaultPageUrl = $("#viewMergedStructureDefaultPageUrl").val();
			if ( viewMergedStructureDefaultPageUrl === undefined || viewMergedStructureDefaultPageUrl === "" ) {
				var structureQueryString = "?";
				for ( var j = 0; j < _projectSearchIds.length; j++ ) {
					if ( j > 0 ) {
						structureQueryString += "&";
					}
					structureQueryString += "projectSearchId=" + _projectSearchIds[ j ];
				}
				if ( _projectSearchIdsUserOrdered && _projectSearchIdsUserOrdered !== "" ) {
					structureQueryString += "&ds=" + _projectSearchIdsUserOrdered;
				}
				var structureJSON = { };
	//			add taxonomy exclusions
				structureJSON[ 'excludeTaxonomy' ] = _excludeTaxonomy;
	//			add type exclusions
				structureJSON[ 'excludeType' ] = _excludeType;
				//  Add Filter cutoffs
				structureJSON[ 'cutoffs' ] = _psmPeptideCutoffsRootObjectStorage.getPsmPeptideCutoffsRootObject();
				//  Add Ann Type Display
				var annTypeIdDisplay = baseJSONObject.annTypeIdDisplay;
				structureJSON[ 'annTypeIdDisplay' ] = annTypeIdDisplay;
	//			add filter out non unique peptides
				structureJSON[ 'filterNonUniquePeptides' ] = _filterNonUniquePeptides;
				structureJSON[ 'minPSMs' ] = _minPSMs;
				structureJSON[ 'filterOnlyOnePeptide' ] = _filterOnlyOnePeptide;
				structureJSON[ 'removeNonUniquePSMs' ] = _removeNonUniquePSMs;
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

	/**
	 * Reset Proteins highlighting, flipping, positioning, and horizontal scaling and redraw
	 */
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

	/**
	 * reset all protein offsets and redraw
	 */
	function resetProteinOffsets() {
		_imageProteinBarDataManager.clearAllProteinBarsOffsets();
		updateURLHash( false /* useSearchForm */ );
		drawSvg();
	}

	/**
	 * get the visual offset for a specific protein
	 */
	function getProteinOffset( params ) {
		var proteinBarIndex = params.proteinBarIndex;
		var entry = _imageProteinBarDataManager.getItemByIndex( proteinBarIndex );
		var proteinOffset = entry.getProteinOffset();
		return proteinOffset;
	}

	/**
	 * get if a specific protein should be displayed reversed
	 */
	function isProteinReversed( params ) {
	//	var proteinId = params.proteinId;
		var proteinBarIndex = params.proteinBarIndex;
		var entry = _imageProteinBarDataManager.getItemByIndex( proteinBarIndex );
		var proteinReversed = entry.getProteinReversed();
		return proteinReversed;
	}

	/**
	 * responsible for adding and handling the draggable events to a protein group ( the rectangle and the label (protein name) )
	 * 
	 * This function serves as a closure to contain all the variables shared 
	 * between the functions "startDragFunc", "moveFunc", and "stopDragFunc"
	 * The detection of the 'control' key (or 'command' key) being down changes the behavior of what is done during the drag.
	 * 
	 * There are numerous functions defined inside this function
	 */
	function addDragTo( g, protein, proteinBarIndex, svgRootSnapSVGObject ) {
		
		var ctrlKey_or_metaKey_Down = false;
		
		/**
		 * Function called when Dragging is Started
		 */
		var startDragFunc = function( clickX, ClickY, mouseEvent ) {
			ctrlKey_or_metaKey_Down = mouseEvent.ctrlKey || mouseEvent.metaKey;
			//  "this" is the Snap object holding the <g> for the protein group ( the rectangle and the label (protein name) )
			if ( ctrlKey_or_metaKey_Down ) {
				startSelectProteinBarPartMouseHandler( this, clickX, ClickY, mouseEvent );
			} else {
				startDragProteinBarMouseHandler( this, clickX, ClickY, mouseEvent );
			}
		};

		/**
		 * Function called while Dragging is In Progress, as the mouse is moved while dragging
		 */
		var moveFunc = function (dx, dy, posx, posy) {
			//  "this" is the Snap object holding the <g> for the protein group ( the rectangle and the label (protein name) )
			if ( ctrlKey_or_metaKey_Down ) {
				mouseMoveSelectProteinBarPart_Handler( this, dx, dy, posx, posy );
			} else {
				mouseMoveDragProteinBarHandler( this, dx, dy, posx, posy );
			}
		};
	
		/**
		 * Function called when Dragging is Stopped/Ended
		 */
		var stopDragFunc = function (mouseEvent) {
			//  "this" is the Snap object holding the <g> for the protein group ( the rectangle and the label (protein name) )
			if ( ctrlKey_or_metaKey_Down ) {
				endSelectSelectProteinBarPart_Handler( this, mouseEvent );
			} else {
				endDragProteinBarMouseHandler( this, mouseEvent );
			}
			//  Do Reset action for both drag options
			resetSelectSelectProteinBarPart_Handler();
			resetDragProteinBarMouseHandler();
		};
		
		//  actually add the drag listener to the SVG group - protein group ( the rectangle and the label (protein name) )
		g.drag( moveFunc, startDragFunc, stopDragFunc  );
		////////////////////
		////   For Dragging the protein bar to change it's horizontal position
		var dragStarted = false;
		var startX_ProteinBarPosition = null; // updated on "start drag"
		var tooltipsDestroyed = false;
		var startDragProteinBarMouseHandler = function ( draggedGroupSnapObject, clickX, ClickY, mouseEvent ) {
			//  draggedGroupSnapObject is the Snap object holding the <g> for the protein group ( the rectangle and the label (protein name) )
			var bbox = draggedGroupSnapObject.getBBox();
			startX_ProteinBarPosition = bbox.x;
			tooltipsDestroyed = false;
			dragStarted = true;
		};
		
		/**
		 * 
		 */
		var mouseMoveDragProteinBarHandler = function ( draggedGroupSnapObject, dx, dy, posx, posy) {
			if ( ! dragStarted ) {
				console.log( "In Drag Move but Drag not started, 'startDragFunc(...) not called" );
				return;   //  EARLY EXIT
			}
			//  draggedGroupSnapObject is the Snap object holding the <g> for the protein group ( the rectangle and the label (protein name) )
			draggedGroupSnapObject.transform("t" + dx );
			if ( ! tooltipsDestroyed ) {
				var bbox = draggedGroupSnapObject.getBBox();
				var amountElementMoved = bbox.x - startX_ProteinBarPosition;
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
		
		/**
		 * 
		 */
		var endDragProteinBarMouseHandler = function ( draggedGroupSnapObject, mouseEvent ) {
			if ( ! dragStarted ) {
				console.log( "In Drag End but Drag not started, 'startDragFunc(...) not called" );
				return;   //  EARLY EXIT
			}
			dragStarted = false;
			if ( startX_ProteinBarPosition === null ) {
				console.log( "start drag not called, startX_ProteinBarPosition === null" );
				return;   //  EARLY EXIT
			}
			//  draggedGroupSnapObject is the Snap object holding the <g> for the protein group ( the rectangle and the label (protein name) )
			var bbox = draggedGroupSnapObject.getBBox();
			var amountElementMoved = bbox.x - startX_ProteinBarPosition;
			// only do this if they actually moved the protein bar group, or moved bar and tool tips were destroyed
			if ( ( Math.abs( amountElementMoved ) > 0 ) || tooltipsDestroyed ) {
				var entry = _imageProteinBarDataManager.getItemByIndex( proteinBarIndex );
				var prevOffset = entry.getProteinOffset();
				var newOffset = prevOffset + ( amountElementMoved ); // adjust offset for amount element moved;
				entry.setProteinOffset( { proteinOffset : newOffset } );    // save the new position of the protein	
				//  Adjust offsets if protein bar moved to left of most possible left position of protein bars 
				if ( newOffset < 0 ) {
					var offsetChange = ( newOffset ) * -1;
					_imageProteinBarDataManager.addToAllProteinOffsets( { offsetChange : offsetChange } );
				}
				updateURLHash( false /* useSearchForm */ );	  					// save the new position to the URL hash
				drawSvg();
			}
		};
		
		/**
		 * 
		 */
		var resetDragProteinBarMouseHandler = function() {
			startX_ProteinBarPosition = null; //  reset.   updated on "start drag"
			tooltipsDestroyed = false;
		};
		
		////////////////////
		////   For Selecting part of the Protein Bar by holding down the ctrl key while dragging the mouse
		var selectProteinBarPart = false;
		var firstDrag = false;
		
		/**
		 * 
		 */
		var startSelectProteinBarPartMouseHandler = function ( draggedGroupSnapObject, clickX, ClickY, mouseEvent ) {
			selectProteinBarPart = true;
			if ( mouseEvent.shiftKey ) {
				shiftKeyDown = true;
			} else {
				shiftKeyDown = false;
			}
			var proteinBarOverlayRectangleSnapObject = draggedGroupSnapObject.select( "." + _PROTEIN_BAR_OVERLAY_RECTANGLE_LABEL_CLASS );
			var proteinBarOverlayRectangleSnapObject_BBox = proteinBarOverlayRectangleSnapObject.getBBox();
			proteinBarOverlayRectangleSnapObject_BBox_height = proteinBarOverlayRectangleSnapObject_BBox.height;
			var proteinBarOverlayRectangleSnapObject_BBox_Width = proteinBarOverlayRectangleSnapObject_BBox.width;
			proteinBarOverlayRectangleSnapObject_BBox_X = proteinBarOverlayRectangleSnapObject_BBox.x;
			proteinBarOverlayRectangleSnapObject_BBox_Y = proteinBarOverlayRectangleSnapObject_BBox.y;
			var $proteinBarOverlayRectanglePlainSVGObject = $( proteinBarOverlayRectangleSnapObject.node );
			var rectangleOffset = $proteinBarOverlayRectanglePlainSVGObject.offset(); // returns an object containing the properties top and left.
			var rectangleOffsetLeft = rectangleOffset.left;
			//		var rectangleOffsetTop = rectangleOffset.top; // not using the "Y" axis value
			var mouseOffsetFromRectangleLeft = clickX - rectangleOffsetLeft;
			startX_NewRectanglePosition = proteinBarOverlayRectangleSnapObject_BBox_X + mouseOffsetFromRectangleLeft;
			newRectangleMaxWidthToRight = 
				proteinBarOverlayRectangleSnapObject_BBox_X + proteinBarOverlayRectangleSnapObject_BBox_Width - startX_NewRectanglePosition + 3; 
			firstDrag = true;
		};
		
		//  The detection of the 'shift' key being down changes the behavior of what is done during the drag.
		var shiftKeyDown = false;
		var proteinBarOverlayRectangleSnapObject_BBox_X = null;
		var proteinBarOverlayRectangleSnapObject_BBox_Y = null;
		var proteinBarOverlayRectangleSnapObject_BBox_height = null;
		var newProteinSelectorRectangleSnapSVGObject = null;
		var startX_NewRectanglePosition = null;
		var endX_NewRectanglePosition = null;
		var newRectangleMaxWidthToRight = null;
		
		/**
		 * 
		 */
		var mouseMoveSelectProteinBarPart_Handler = function ( draggedGroupSnapObject, dx, dy, posx, posy) {
			//  draggedGroupSnapObject is the Snap object holding the <g> for the protein group ( the rectangle and the label (protein name) )
			if ( ! selectProteinBarPart ) {
				throw Error( "In select Protein Bar Part Mouse Move but Select Protein Bar Part not started, 'startDragFunc(...) not called" );
			}
			if ( firstDrag ) {
				var proteinBarMainRectangleSnapObject = draggedGroupSnapObject.select( "." + _PROTEIN_BAR_MAIN_RECTANGLE_LABEL_CLASS );
				proteinBarMainRectangleSnapObject.attr({
					fill: _NOT_HIGHLIGHTED_LINE_COLOR
				});
				var groupOnMainProteinBarRectSnapObject = draggedGroupSnapObject.select( "." + _PROTEIN_BAR_GROUP_ON_TOP_OF_MAIN_RECTANGLE_LABEL_CLASS );
				newProteinSelectorRectangleSnapSVGObject = 
					svgRootSnapSVGObject.rect( startX_NewRectanglePosition, proteinBarOverlayRectangleSnapObject_BBox_Y, 1, proteinBarOverlayRectangleSnapObject_BBox_height );
				newProteinSelectorRectangleSnapSVGObject.attr( { fill: "red", "fill-opacity": 1 } );
				groupOnMainProteinBarRectSnapObject.add( newProteinSelectorRectangleSnapSVGObject );
				firstDrag = false;
			}
			if ( dx >= 0 ) {
				//  Mouse dragged to right of start so just adjust rectangle width
				var newWidth = dx;
				if ( newWidth > newRectangleMaxWidthToRight ) {
					newWidth = newRectangleMaxWidthToRight;
				}
				newProteinSelectorRectangleSnapSVGObject.attr( { width: newWidth } );
				endX_NewRectanglePosition = startX_NewRectanglePosition + newWidth;
			} else {
				//  Mouse dragged to left of start so move the left edge of rectangle and adjust rectangle width
				var dragOffsetMadePositive = - dx;
				var newRectX = startX_NewRectanglePosition - dragOffsetMadePositive;
				if ( newRectX < proteinBarOverlayRectangleSnapObject_BBox_X ) {
					newRectX = proteinBarOverlayRectangleSnapObject_BBox_X;
					dragOffsetMadePositive = startX_NewRectanglePosition - newRectX;
				}
				newProteinSelectorRectangleSnapSVGObject.attr( { x: newRectX , width: dragOffsetMadePositive } );
				endX_NewRectanglePosition = newRectX;
			}
		};

		/**
		 * 
		 */
		var endSelectSelectProteinBarPart_Handler = function ( draggedGroupSnapObject, mouseEvent ) {
			if ( ! selectProteinBarPart ) {
				throw Error( "In select Protein Bar Part End but Select Protein Bar Part not started, 'startDragFunc(...) not called" );
			}
			if ( endX_NewRectanglePosition === undefined || endX_NewRectanglePosition === null ) {
				return;
			}
			//  Convert positions to protein positions and store in object
			var proteinSequence = _proteinSequences[ protein ];
			var proteinSequenceLength  = proteinSequence.length;
			//  convert elementNewPositionX to sequence position
			var multiplier = getMultiplier();
			if ( startX_NewRectanglePosition > endX_NewRectanglePosition ) {
				//  Swap so start is always before end
				var tempX = endX_NewRectanglePosition;
				endX_NewRectanglePosition = startX_NewRectanglePosition;
				startX_NewRectanglePosition = tempX;
			}
			var startX_OffsetFromRectangleLeft = startX_NewRectanglePosition - proteinBarOverlayRectangleSnapObject_BBox_X;
			var endX_OffsetFromRectangleLeft = endX_NewRectanglePosition - proteinBarOverlayRectangleSnapObject_BBox_X;
			var startX_SequencePositionOneBased = Math.round( ( startX_OffsetFromRectangleLeft / multiplier ) - 0.5 ) + 1;
			var endX_SequencePositionOneBased = Math.round( ( endX_OffsetFromRectangleLeft / multiplier ) - 0.5 ) + 1; 
			if ( isProteinReversed( { proteinId : protein, proteinBarIndex : proteinBarIndex } ) ) {
				startX_SequencePositionOneBased = proteinSequenceLength - startX_SequencePositionOneBased + 1;
				endX_SequencePositionOneBased = proteinSequenceLength - endX_SequencePositionOneBased + 1;
			}
			if ( startX_SequencePositionOneBased > endX_SequencePositionOneBased ) {
				//  Swap so start is always before end
				var tempX = endX_SequencePositionOneBased;
				endX_SequencePositionOneBased = startX_SequencePositionOneBased;
				startX_SequencePositionOneBased = tempX;
			}
			if ( startX_SequencePositionOneBased > proteinSequenceLength ) {
				startX_SequencePositionOneBased = proteinSequenceLength;
			}
			if ( endX_SequencePositionOneBased > proteinSequenceLength ) {
				endX_SequencePositionOneBased = proteinSequenceLength;
			}
			var selectionRegion = { start: startX_SequencePositionOneBased, end: endX_SequencePositionOneBased };
			var imageProteinBarDataEntry = 
				_imageProteinBarDataManager.getItemByIndex( proteinBarIndex );
			if ( shiftKeyDown ) {
				imageProteinBarDataEntry.addProteinBarHighlightedRegion( selectionRegion );
			} else {
				_imageProteinBarDataManager.clearAllProteinBarsHighlighted();
				imageProteinBarDataEntry.setProteinBarHighlightedRegion( selectionRegion );
			}
	//		console.log( "Selection region:  Start: " + startX_SequencePositionOneBased + ", End: " + endX_SequencePositionOneBased );
			updateURLHash( false /* useSearchForm */ );	  					// save the new selection to the URL hash
			drawSvg();
		};
		
		/**
		 * 
		 */
		var resetSelectSelectProteinBarPart_Handler = function () {
			selectProteinBarPart = false;
			startX_NewRectanglePosition = null;
			endX_NewRectanglePosition = null;
		};
	}

	/**
	 * 
	 */
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
		var imageProteinBarDataEntry = _imageProteinBarDataManager.getItemByIndex(  proteinBarIndex );
		imageProteinBarDataEntry.removeProteinBarHighlightedRegion( { position : sequencePositionOneBased } );
		drawSvg();
	}

	/**
	 * add click and double click handlers to a protein rectangle, such that it
	 * functions alongside the draggable event handler
	 */
	function addClickDoubleClickTo( g, protein, i  ) {
		var clicks = 0;
		var startx = 0;
		var proteinBarOverlayRectangleSnapObject = g.select( "." + _PROTEIN_BAR_OVERLAY_RECTANGLE_LABEL_CLASS );
		var $proteinBarOverlayRectanglePlainSVGObject = $( proteinBarOverlayRectangleSnapObject.node );
		var rectangleOffset = $proteinBarOverlayRectanglePlainSVGObject.offset(); // returns an object containing the properties top and left.
		var rectangleOffsetLeft = rectangleOffset.left;
		//		var rectangleOffsetTop = rectangleOffset.top; // not using the "Y" axis value
		
		/**
		 * 
		 */
		var singleClickFunction = function( e ) {
			toggleHighlightedProtein( i, true /* shouldClearFirst */ );
			updateURLHash( false /* useSearchForm */ );
		};
		
		/**
		 * 
		 */
		var doubleClickFunction = function( e ) {
			toggleReversedProtein( i, protein );
			updateURLHash( false /* useSearchForm */ );
			//console.log( "got double click on " + _proteinNames[ protein ] + "(" + i + ")" );
		};
		
		/**
		 * 
		 */
		var shiftClickFunction = function( e ) {
			toggleHighlightedProtein( i, false /* shouldClearFirst */ );
			updateURLHash( false /* useSearchForm */ );
		};
		
		/**
		 * 
		 */
		var controlClickFunction = function( e ) {
			removeHighlightedProteinRegion( { proteinBarIndex : i, proteinId : protein, mousePositionX : e.clientX, rectangleOffsetLeft : rectangleOffsetLeft } );
	//		toggleHighlightedProtein( i, false /* shouldClearFirst */ );
			updateURLHash( false /* useSearchForm */ );
		};
		
		/**
		 * 
		 */
		var mouseDownFunction = function( e ) {
			startx = e.clientX;
		};
		
		/**
		 * 
		 */
		var mouseUpFunction = function( e ) {
			if ( Math.abs( e.clientX - startx ) < 1 ) {
				clicks++;
			} else {
				clicks = 0;
			}
			if ( clicks === 1 && Math.abs( e.clientX - startx ) < 1 ) {
				setTimeout( function() {
					if ( clicks === 1 ) {
						if ( e.shiftKey ) { 
							shiftClickFunction.call( this, e );
						} else if ( e.ctrlKey || e.metaKey ) { 
							controlClickFunction.call( this, e ); 
						} else { 
							singleClickFunction.call( this, e ); 
						}
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

	/**
	 * reset/clear highlighted proteins
	 */
	function clearHighlightedProteins() {
		_imageProteinBarDataManager.clearAllProteinBarsHighlighted();
	}

	/**
	 * whether or not the supplied selected protein index is currently highlighted
	 */
	function isHighlightedProtein( i ) {
		var imageProteinBarData =  _imageProteinBarDataManager.getItemByIndex( i );
		if ( imageProteinBarData.isAllOfProteinBarHighlighted()  ) {
			return true;
		}
		return false;
	}

	/**
	 * highlight the given index among the selected proteins
	 */
	function toggleHighlightedProtein( i, shouldClearFirst ) {
		if ( shouldClearFirst && _imageProteinBarDataManager.moreThan_One_ProteinBar_Highlighted() ) {
			clearHighlightedProteins();
			var imageProteinBarDataItem = _imageProteinBarDataManager.getItemByIndex( i );
			imageProteinBarDataItem.setProteinBarHighlightedAll();
		} else {
			if ( isHighlightedProtein( i ) ) {
				var imageProteinBarDataItem = _imageProteinBarDataManager.getItemByIndex( i );
				imageProteinBarDataItem.clearProteinBarHighlighted();
			} else {
				if ( shouldClearFirst ) { 
					clearHighlightedProteins(); 
				}
				var imageProteinBarDataItem = _imageProteinBarDataManager.getItemByIndex( i );
				imageProteinBarDataItem.setProteinBarHighlightedAll();
			}
		}
		drawSvg();
	}

	/**
	 * Reverse the protein
	 */
	function toggleReversedProtein( i, protein ) {
		var entry = _imageProteinBarDataManager.getItemByIndex( i );
		var proteinReversed = entry.getProteinReversed();
		if ( ! proteinReversed ) {
			entry.setProteinReversed( { proteinReversed : true });
			$( "input#show-protein-termini" ).prop('checked', true );
		} else {	
			entry.setProteinReversed( { proteinReversed : false });
		}
		drawSvg();
	}

	/**
	 * 
	 */
	function annotationColorOverrideForUnselected( params ) {
		var proteinBarPositionIndex = params.proteinBarPositionIndex;
		var annotationStartPosition = params.annotationStartPosition;
		var annotationEndPosition = params.annotationEndPosition;
		var imageProteinBarDataEntry = _imageProteinBarDataManager.getItemByIndex(  proteinBarPositionIndex );
		if ( imageProteinBarDataEntry.isProteinBarHighlightedAtPosition( { position_1 : annotationStartPosition } )
				|| imageProteinBarDataEntry.isProteinBarHighlightedAtPosition( { position_1 : annotationEndPosition } ) ) {
			//  Start or End position is in a highlighted region
			return false;
		}
		return _NOT_HIGHLIGHTED_LINE_COLOR;
	}

	/**
	 * 
	 */
	function getColorForIndex( i ) {
		return _LINE_COLORS [ i % _LINE_COLORS.length ];
	}

	/**
	 * 
	 */
	function getColorForProteinBarRowIndexPosition( params ) {
		var proteinBarRowIndex = params.proteinBarRowIndex;
		var singlePosition = params.singlePosition;
		//  Used for: Links within same protein bar (Self Crosslink or Looplink)
		var position_1 = params.position_1;
		var position_2 = params.position_2;
		
		if ( _colorLinesBy === SELECT_ELEMENT_COLOR_BY_REGION && ( _imageProteinBarDataManager.isAnyProteinBarsHighlighted() ) ) {
			
			if ( singlePosition !== undefined ) {
				var lineColorIndex = 
					getColorIndexForProteinBarRowIndexPosition__SinglePosition( { proteinBarRowIndex : proteinBarRowIndex, singlePosition : singlePosition } );
				if ( lineColorIndex !== -1 ) {
					
					return getColorForIndex( lineColorIndex );
				}
				throw Error( "getColorForProteinBarRowIndexPosition: singlePosition: lineColorIndex === -1" );
	//			return _NOT_HIGHLIGHTED_LINE_COLOR;
			}
			if ( position_1 !== undefined && position_2 !== undefined ) {
				var lineColorIndexPosition_1 = 
					getColorIndexForProteinBarRowIndexPosition__SinglePosition( { proteinBarRowIndex : proteinBarRowIndex, singlePosition : position_1 } );
				if ( lineColorIndexPosition_1 !== -1 ) {
					
					return getColorForIndex( lineColorIndexPosition_1 );
				}
				var lineColorIndexPosition_2 = 
					getColorIndexForProteinBarRowIndexPosition__SinglePosition( { proteinBarRowIndex : proteinBarRowIndex, singlePosition : position_2 } );
				if ( lineColorIndexPosition_2 !== -1 ) {
					
					return getColorForIndex( lineColorIndexPosition_2 );
				}
				throw Error( "getColorForProteinBarRowIndexPosition: singlePosition: lineColorIndex === -1" );
	//			return _NOT_HIGHLIGHTED_LINE_COLOR;
			}
		} 
		return getColorForIndex( proteinBarRowIndex );
	}

	/**
	 * getColorIndexForProteinBarRowIndexPosition__SinglePosition
	 * 
	 * Must return an index or -1 to indicate no index
	 */
	function getColorIndexForProteinBarRowIndexPosition__SinglePosition( params ) {
		var proteinBarRowIndex = params.proteinBarRowIndex;
		var singlePosition = params.singlePosition;
		//  Loop through protein bars selected regions incrementing a counter to get an index into the colors array
		var proteinBarsSelectedRegionsCounter = 0;
		//  Loop through the protein bars up to index proteinBarRowIndex, counting regions to get the color for that region
		for ( var imageProteinBarDataEntryArrayIndex = 0; imageProteinBarDataEntryArrayIndex <= proteinBarRowIndex; imageProteinBarDataEntryArrayIndex++ ) {
			var imageProteinBarDataEntry = _imageProteinBarDataManager.getItemByIndex( imageProteinBarDataEntryArrayIndex );
			if ( proteinBarRowIndex === imageProteinBarDataEntryArrayIndex ) {
				//  position is in this index so find this position in a region and exit.
				if ( imageProteinBarDataEntry.isAllOfProteinBarHighlighted() ) {
					//  Whole protein bar highlighted so a single region
					
					return proteinBarsSelectedRegionsCounter;
				}
				var regionIndexContainingPosition = 
					imageProteinBarDataEntry.indexOfProteinBarHighlightedRegionAtSinglePosition( singlePosition );
				if ( regionIndexContainingPosition === -1 ) {
					
					return -1;
				}
				proteinBarsSelectedRegionsCounter += ( regionIndexContainingPosition );
				return proteinBarsSelectedRegionsCounter;
			}
			if ( imageProteinBarDataEntry.isAllOfProteinBarHighlighted() ) {
				//  Whole protein bar highlighted so a single region
				proteinBarsSelectedRegionsCounter++;
			} else {
				var proteinBarHighlightedRegionCount = 
					imageProteinBarDataEntry.getProteinBarHighlightedRegionCount();
				proteinBarsSelectedRegionsCounter += proteinBarHighlightedRegionCount;
			}
		}
		throw Error( "ERROR: in getColorForProteinBarRowIndexPosition__SinglePosition(...): at bottom of function without returning a value. " +
				" proteinBarRowIndex: " + proteinBarRowIndex + ", singlePosition: " + singlePosition );
	}

	/**
	 * 
	 */
	function getColorForProteinBarRowIndexBlockPositions( params ) {
		var proteinBarRowIndex = params.proteinBarRowIndex;
		var block_position_1 = params.block_position_1;
		var block_position_2 = params.block_position_2;
		
		if ( _colorLinesBy === SELECT_ELEMENT_COLOR_BY_REGION && ( _imageProteinBarDataManager.isAnyProteinBarsHighlighted() ) ) {
			
			if ( block_position_1 !== undefined && block_position_2 !== undefined ) {
				//  Loop through protein bars selected regions incrementing a counter to get an index into the colors array
				var foundProteinBarsSelectedRegion = false;
				var proteinBarsSelectedRegionsCounter = 0;
				//  Loop through the protein bars up to index proteinBarRowIndex, counting regions to get the color for that region
				for ( var imageProteinBarDataEntryArrayIndex = 0; imageProteinBarDataEntryArrayIndex <= proteinBarRowIndex; imageProteinBarDataEntryArrayIndex++ ) {
					var imageProteinBarDataEntry = _imageProteinBarDataManager.getItemByIndex( imageProteinBarDataEntryArrayIndex );
					if ( proteinBarRowIndex === imageProteinBarDataEntryArrayIndex ) {
						//  block positions are in this index so find this position in a region and exit.
						if ( imageProteinBarDataEntry.isAllOfProteinBarHighlighted() ) {
							//  Whole protein bar highlighted so a single region
							foundProteinBarsSelectedRegion = true;
							break;  // EARLY EXIT LOOP
						}
						var regionIndexContainingPosition = 
							imageProteinBarDataEntry.indexOfProteinBarHighlightedRegionAnywhereBetweenPositionsInclusive( {
								position_1 : block_position_1,
								position_2 : block_position_2
							} );
						if ( regionIndexContainingPosition === -1 ) {
							proteinBarsSelectedRegionsCounter = -1;
							break;  // EARLY EXIT LOOP
						}
						proteinBarsSelectedRegionsCounter += ( regionIndexContainingPosition );
						foundProteinBarsSelectedRegion = true;
						break;
					}
					if ( imageProteinBarDataEntry.isAllOfProteinBarHighlighted() ) {
						//  Whole protein bar highlighted so a single region
						proteinBarsSelectedRegionsCounter++;
					} else {
						var proteinBarHighlightedRegionCount = 
							imageProteinBarDataEntry.getProteinBarHighlightedRegionCount();
						proteinBarsSelectedRegionsCounter += proteinBarHighlightedRegionCount;
					}
				}
				if ( ! foundProteinBarsSelectedRegion ) {
					throw Error( "ERROR: in getColorForProteinBarRowIndexBlockPositions(...): ! foundProteinBarsSelectedRegion " );
				}
				
				return getColorForIndex( proteinBarsSelectedRegionsCounter );
			}
		} 
		return getColorForIndex( proteinBarRowIndex );
	}

	/**
	 * get the color that should be used for a link, based on that links presence in the supplied searches
	 * index i is supplied to support "highlighted" protein coloring
	 */
	function getColorForSearchesForIndexAndSearchList( i, searchList ) {
		var colorIndex = "";
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
	//				throw Error( "In getColorForSearches: color for searches is undefined for colorIndex: '" + colorIndex + "'  _SEARCH_COLORS_TWO_SEARCHES[ colorIndex ]" );
	//			}
			
			return colorForSearches;
			
		} else {
			var colorForSearches = _SEARCH_COLORS[ colorIndex ];
	//			if ( colorForSearches === undefined ) {
	//				throw Error( "In getColorForSearches: color for searches is undefined for colorIndex: '" + colorIndex + "'  _SEARCH_COLORS[ colorIndex ]" );
	//			}
			
			return colorForSearches;
		}
	}

	/**
	 *   Used for getting color for the legend
	 * get the color that should be used for a link, based on that links presence in the supplied searches
	 * index i is supplied to support "highlighted" protein coloring
	 */
	function getColorForSearchesForLegend( searchList ) {
		var i = -1;  // hard coded for Legend
		return getColorForSearchesForIndexAndSearchList( i, searchList );
		
		//  Never called.  Consider removing   TODO
		return _NOT_HIGHLIGHTED_LINE_COLOR;
	}

	/**
	 * get color for line that is connecting one or 1 points on the same protein, based on that protein index among selected proteins
	 */
	function getLineColorSingleProteinBar( params ) {
		var searchList = params.searchList;  // Optional, populated for "Color By Search"
		var proteinIndex = params.proteinIndex;
		var fromProteinPosition = params.fromProteinPosition;  
		var toProteinPosition  = params.toProteinPosition;   //  Optional
		
		if ( toProteinPosition ) {
			var proteinUID = _indexManager.getUIDForIndex( proteinIndex );

			if ( _linkExclusionDataManager.isLinkExcludedForProteinUIDProteinPosition( {
				proteinUID_1 : proteinUID, proteinPosition_1 : fromProteinPosition,
				proteinUID_2 : proteinUID, proteinPosition_2 : toProteinPosition } ) ) {
				return _NOT_HIGHLIGHTED_LINE_COLOR;
			}		
		}

		if ( ( ! _imageProteinBarDataManager.isAnyProteinBarsHighlighted() ) ) { 
			if ( searchList ) {
				return getColorForSearchesForIndexAndSearchList( proteinIndex, searchList );
			}
			return getColorForProteinBarRowIndexPosition( { proteinBarRowIndex : proteinIndex } ); 
		}
		var imageProteinBarDataProtein =  _imageProteinBarDataManager.getItemByIndex( proteinIndex );
		if ( toProteinPosition === undefined || toProteinPosition === null ) {
			//  Only From Position for Monolink
			if ( imageProteinBarDataProtein.isProteinBarHighlightedAtPosition( { position_1 : fromProteinPosition } )  ) {
				if ( searchList ) {
					return getColorForSearchesForIndexAndSearchList( proteinIndex, searchList );
				}
				return  getColorForProteinBarRowIndexPosition( { proteinBarRowIndex : proteinIndex, singlePosition : fromProteinPosition } );
			}
		} else {
			//  Self Crosslink or Looplink
			if ( imageProteinBarDataProtein.isProteinBarHighlightedAtPosition( { position_1 : fromProteinPosition, position_2 : toProteinPosition } )  ) {
				if ( searchList ) {
					return getColorForSearchesForIndexAndSearchList( proteinIndex, searchList );
				}
				return getColorForProteinBarRowIndexPosition( { proteinBarRowIndex : proteinIndex, position_1 : fromProteinPosition, position_2 : toProteinPosition } );
			}
		}
		return _NOT_HIGHLIGHTED_LINE_COLOR;
	}

	/**
	 * get color for line that is connecting two proteins, based on those proteins indexes among selected proteins
	 */
	function getCrosslinkLineColor( params ) {
		var searchList = params.searchList;    // Optional, populated for "Color By Search"
		var fromProteinIndex = params.fromProteinIndex;
		var fromProteinPosition = params.fromProteinPosition;  
		var toProteinIndex = params.toProteinIndex;
		var toProteinPosition  = params.toProteinPosition;
		
		var fromProteinUID = _indexManager.getUIDForIndex( fromProteinIndex );
		var toProteinUID = _indexManager.getUIDForIndex( toProteinIndex );
		
		if ( _linkExclusionDataManager.isLinkExcludedForProteinUIDProteinPosition( {
			proteinUID_1 : fromProteinUID, proteinPosition_1 : fromProteinPosition,
			proteinUID_2 : toProteinUID, proteinPosition_2 : toProteinPosition } ) ) {
			return _NOT_HIGHLIGHTED_LINE_COLOR;
		}		

		
		if ( ( ! _imageProteinBarDataManager.isAnyProteinBarsHighlighted() ) ) { 
			if ( searchList ) {
				return getColorForSearchesForIndexAndSearchList( fromProteinIndex, searchList );
			}
			return getColorForProteinBarRowIndexPosition( { proteinBarRowIndex : fromProteinIndex } );
		}
		var imageProteinBarDataFromProtein =  _imageProteinBarDataManager.getItemByIndex( fromProteinIndex );
		var imageProteinBarDataToProtein =  _imageProteinBarDataManager.getItemByIndex( toProteinIndex );
		if ( _imageProteinBarDataManager.exactly_One_ProteinBar_Highlighted() ) {
			if ( imageProteinBarDataFromProtein.isProteinBarHighlightedAtPosition( { position_1 : fromProteinPosition } )  ) {
				if ( searchList ) {
					return getColorForSearchesForIndexAndSearchList( fromProteinIndex, searchList );
				}
				return getColorForProteinBarRowIndexPosition( { proteinBarRowIndex : fromProteinIndex, singlePosition : fromProteinPosition } ); 
			} else if ( imageProteinBarDataToProtein.isProteinBarHighlightedAtPosition( { position_1 : toProteinPosition } )  ) {
				if ( searchList ) {
					return getColorForSearchesForIndexAndSearchList( toProteinIndex, searchList );
				}
				return getColorForProteinBarRowIndexPosition( { proteinBarRowIndex : toProteinIndex, singlePosition : toProteinPosition } ); 
			}
			return _NOT_HIGHLIGHTED_LINE_COLOR;
		}
		if ( imageProteinBarDataFromProtein.isProteinBarHighlightedAtPosition( { position_1 : fromProteinPosition } )
			&& imageProteinBarDataToProtein.isProteinBarHighlightedAtPosition( { position_1 : toProteinPosition } )  ) {
			if ( searchList ) {
				return getColorForSearchesForIndexAndSearchList( fromProteinIndex, searchList );
			}
			return getColorForProteinBarRowIndexPosition( { proteinBarRowIndex : fromProteinIndex, singlePosition : fromProteinPosition } ); 
		}
		return _NOT_HIGHLIGHTED_LINE_COLOR;
	}

	/**
	 * get the opacity that should be used for links associated with a specific protein index
	 */
	function getOpacityForIndexAndLink( i, link ) {
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
					try {
						numPsms = _linkPSMCounts[ 'looplink' ][ link.protein1 ][ link.protein1 ][ link.position1 ][ link.position2 ];
					} catch( err ) { }
					if( !numPsms ) {
						try {
							numPsms = _linkPSMCounts[ 'looplink' ][ link.protein1 ][ link.protein1 ][ link.position2 ][ link.position1 ];
						} catch( err ) { }
					}
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

	/**
	 * get opacity for line that is connecting one or 1 points on the same protein, based on that protein index among selected proteins
	 */
	function getLineOpacitySingleProteinBar( params ) {
		var link = params.link;
		var proteinIndex = params.proteinIndex;
		var fromProteinPosition = params.fromProteinPosition;  
		var toProteinPosition  = params.toProteinPosition;   //  Optional
		
		var proteinUID = _indexManager.getUIDForIndex( proteinIndex );

		var isLinkExcludedForProteinUIDProteinPositionParams = {
				proteinUID_1 : proteinUID, proteinPosition_1 : fromProteinPosition,
				proteinUID_2 : proteinUID, proteinPosition_2 : toProteinPosition
		};
		if ( toProteinPosition === undefined ) {
			isLinkExcludedForProteinUIDProteinPositionParams.proteinPosition_2 = fromProteinPosition;
		}
		if ( _linkExclusionDataManager.isLinkExcludedForProteinUIDProteinPosition( isLinkExcludedForProteinUIDProteinPositionParams ) ) {
			return 0.25;
		}		
		
		if ( ( ! _imageProteinBarDataManager.isAnyProteinBarsHighlighted() ) ) { 
			return getOpacityForIndexAndLink( proteinIndex, link );
		}
		var imageProteinBarDataProtein =  _imageProteinBarDataManager.getItemByIndex( proteinIndex );
		if ( toProteinPosition === undefined || toProteinPosition === null ) {
			if ( imageProteinBarDataProtein.isProteinBarHighlightedAtPosition( { position_1 : fromProteinPosition } )  ) {
				return getOpacityForIndexAndLink( proteinIndex, link );
			}
		} else {
			if ( imageProteinBarDataProtein.isProteinBarHighlightedAtPosition( { position_1 : fromProteinPosition, position_2 : toProteinPosition } )  ) {
				return getOpacityForIndexAndLink( proteinIndex, link );
			}
		}
		return 0.25;
	}

	/**
	 * //  get opacity for line that is connecting one or 1 points on the same protein, based on that protein index among selected proteins
	 */
	function getCrosslinkLineOpacity( params ) {
		var link = params.link;
		var fromProteinIndex = params.fromProteinIndex;
		var fromProteinPosition = params.fromProteinPosition;  
		var toProteinIndex = params.toProteinIndex;
		var toProteinPosition  = params.toProteinPosition;

		var fromPproteinUID = _indexManager.getUIDForIndex( fromProteinIndex );
		var toPproteinUID = _indexManager.getUIDForIndex( toProteinIndex );

		var isLinkExcludedForProteinUIDProteinPositionParams = {
				proteinUID_1 : fromPproteinUID, proteinPosition_1 : fromProteinPosition,
				proteinUID_2 : toPproteinUID, proteinPosition_2 : toProteinPosition
		};
		if ( _linkExclusionDataManager.isLinkExcludedForProteinUIDProteinPosition( isLinkExcludedForProteinUIDProteinPositionParams ) ) {
			return 0.25;
		}		
		
		
		if ( ( ! _imageProteinBarDataManager.isAnyProteinBarsHighlighted() ) ) { 
			return getOpacityForIndexAndLink( fromProteinIndex, link );
		}
		var imageProteinBarDataFromProtein =  _imageProteinBarDataManager.getItemByIndex( fromProteinIndex );
		var imageProteinBarDataToProtein =  _imageProteinBarDataManager.getItemByIndex( toProteinIndex );
		if ( _imageProteinBarDataManager.exactly_One_ProteinBar_Highlighted() ) {
			if ( imageProteinBarDataFromProtein.isProteinBarHighlightedAtPosition( { position_1 : fromProteinPosition } )  ) {
				return getOpacityForIndexAndLink( fromProteinIndex, link ); 
			} else if ( imageProteinBarDataToProtein.isProteinBarHighlightedAtPosition( { position_1 : toProteinPosition } )  ) {
				return getOpacityForIndexAndLink( toProteinIndex, link ); 
			}
			return 0.25;
		}
		if ( imageProteinBarDataFromProtein.isProteinBarHighlightedAtPosition( { position_1 : fromProteinPosition } )
			&& imageProteinBarDataToProtein.isProteinBarHighlightedAtPosition( { position_1 : toProteinPosition } )  ) {
			return getOpacityForIndexAndLink( fromProteinIndex, link ); 
		}
		return 0.25;
	}

	/**
	 * returns a list of searches for the given link
	 */
	function findSearchesForMonolink( protein, position ) {
		return _proteinMonolinkPositions[ protein ][ position ];
	}

	/**
	 * returns a list of searches for the given link
	 */
	function findSearchesForLooplink( protein, position1, position2 ) {
		var searches;
		try {
			searches = _proteinLooplinkPositions[ protein ][protein][ position1 ][ position2 ];
		} catch( err ) { }
		if( !searches ) {
			try {
				searches = _proteinLooplinkPositions[ protein ][protein][ position2 ][ position1 ];
			} catch( err ) { }
		}
		if( !searches ) {
			console.log( "WARNING: could not find any searches for looplink. protein: " + protein + " position1 " + position1 + " position2: " + position2 );
		}
		return searches;
	}

	/**
	 * returns a list of searches for the given link
	 */
	function findSearchesForCrosslink( protein1, protein2, position1, position2 ) {	
		return _proteinLinkPositions[ protein1 ][ protein2 ][ position1 ][ position2 ];
	}

	/**
	 * 
	 */
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

	/**
	 * 
	 */
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

	/**
	 * 
	 */
	function findLengthInPixelsOfTerminusLabel( svgRootSnapSVGObject, terminusLabel ) {
		var terminusSnapSVGObject = svgRootSnapSVGObject.text( 1, 10, "C" );
		var terminusSnapSVGObjectBBox = terminusSnapSVGObject.getBBox();
		var terminusSnapSVGObjectWidth = terminusSnapSVGObjectBBox.width;
	//	Remove temporary text object inserted to get it's size
		terminusSnapSVGObject.remove();
		return terminusSnapSVGObjectWidth;
	}

	/**
	 * 
	 */
	function precomputeMultiplierAndOtherValuesForSVG( svgRootSnapSVGObject, selectedProteins ) {
		//  Get max protein length
		var maxProteinLength = 0;
		var selectedProteins = _indexManager.getProteinList();
		for ( var i = 0; i < selectedProteins.length; i++ ) {
			var proteinSequenceVersionId = selectedProteins[ i ];
			var proteinLength = _proteinLengths.getProteinLength( proteinSequenceVersionId );
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

	/**
	 *  Compute Multiplier
	 */
	function precomputeMultiplier__CallOnlyFrom__precomputeMultiplierAndOtherValuesForSVG( 
			maxWidth, maxProteinLength, tickMarkSnapSVGObjectMaxProteinLengthWidthHalf ) {
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
	/**
	 * Actually draw the image
	 */
	function drawSvg() {
		if(  $( "input#view-as-circle-plot" ).is( ':checked' )  ) {
			_circlePlotViewer.draw();
			return;
		}
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
		var selectedProteins = _indexManager.getProteinList();
	//	var maxProteinLength = 0;
		if ( selectedProteins === undefined || selectedProteins.length < 1 ) { 
			//  No proteins so remove CSS height attribute
			$svg_image_inner_container_div.css( { height : '' } );
			return;  //  EARLY EXIT of function if no proteins to display 
		}
	//	svgRootSnapSVGObject = Snap("#svg"); //  now using CSS selector, no longer has that "id" 
		var svgRootSnapSVGObject = Snap( merged_image_svg_element );  // pass in HTML element
		_GLOBAL_SNAP_SVG_OBJECT = svgRootSnapSVGObject;
		svgRootSnapSVGObject.attr( { width: _DEFAULT_VIEWPORT_WIDTH } );
		svgRootSnapSVGObject.attr( { height: _DEFAULT_VIEWPORT_HEIGHT } );
	//	svgRootSnapSVGObject.clear();  //  switched to using jQuery .empty() on parent element since jQuery properly removes attached event handlers on the objects.
		if ( _proteins.length < 1 ) {
			//  No Proteins Data loaded
			var newHeightContainingDivEmpty = 4;
			$svg_image_inner_container_div.css( { height : newHeightContainingDivEmpty } );
			return;
		}
	//	svgRootSnapSVGObject.attr( { width: getCurrentViewportWidth() } );
		var precomputeMultiplierAndOtherValuesForSVGResults = precomputeMultiplierAndOtherValuesForSVG( svgRootSnapSVGObject, selectedProteins );
		var scalebarIncrement = precomputeMultiplierAndOtherValuesForSVGResults.scalebarIncrement;
		//	draw sequence coverage, if requested
		var annotationType = $("#annotation_type").val();
		if ( annotationType === SELECT_ELEMENT_ANNOTATION_TYPE_SEQUENCE_COVERAGE ) {
			drawSequenceCoverage( selectedProteins, svgRootSnapSVGObject );
		}
		else if ( annotationType === SELECT_ELEMENT_ANNOTATION_TYPE_CUSTOM ) {
			drawCustomRegions( selectedProteins, svgRootSnapSVGObject );
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
		if ( _colorLinesBy === SELECT_ELEMENT_COLOR_BY_SEARCH ) {
			var drawColorBySearchLegendResponse = drawColorBySearchLegend(  selectedProteins, bottomOfLowestItemDrawn, svgRootSnapSVGObject );
			//  drawColorBySearchLegendResponse === null if nothing drawn
			if ( drawColorBySearchLegendResponse ) {
				var rightMostEdgeOfAllElementsDrawColorBySearchLegendResponse = drawColorBySearchLegendResponse.rightMostEdgeOfAllElementsThisFunction;
				if ( rightMostEdgeOfAllElementsDrawColorBySearchLegendResponse > rightMostEdgeOfAllElements ) {
					rightMostEdgeOfAllElements = rightMostEdgeOfAllElementsDrawColorBySearchLegendResponse;
				} 
				bottomOfLowestItemDrawn = drawColorBySearchLegendResponse.bottomOfLowestItemDrawn;  // adjust bottomOfLowestItemDrawn for this item
			}
		};
		var newHeight = bottomOfLowestItemDrawn + 5;  //  Add 60 to be conservative to not cut anything off 
		svgRootSnapSVGObject.attr( { height: newHeight } );
		var newWidth = rightMostEdgeOfAllElements + _PADDING_OVERALL_IMAGE_RIGHT_SIDE;
		svgRootSnapSVGObject.attr( { width: newWidth } );
		var newHeightContainingDiv = newHeight + 4;
		$svg_image_inner_container_div.css( { height : newHeightContainingDiv } );
	}

	/**
	 * 
	 */
	function drawCustomRegions( selectedProteins, svgRootSnapSVGObject ) {

		console.log( "drawCustomRegions() called" );

		var isAnyProteinBarsHighlighted = _imageProteinBarDataManager.isAnyProteinBarsHighlighted();
		for ( var proteinBarRowIndex = 0; proteinBarRowIndex < selectedProteins.length; proteinBarRowIndex++ ) {
			var protein = selectedProteins[ proteinBarRowIndex ];
			var segments = _customRegionManager._customRegionAnnotationData[ protein ];

			if ( segments != undefined && segments.length > 0 ) {
				for ( var k = 0; k < segments.length; k++ ) {
					var segment = segments[ k ];

					var start = segment.startPosition;
					var end = segment.endPosition;
					var blockColor = undefined;

					if ( isAnyProteinBarsHighlighted ) {
						var imageProteinBarDataEntry = _imageProteinBarDataManager.getItemByIndex( proteinBarRowIndex );
						var isProteinBarHighlightedAnywhereBetweenPositionsParams = {
								position_1 : start,
								position_2 : end
						};
						if ( ! imageProteinBarDataEntry.isProteinBarHighlightedAnywhereBetweenPositions( isProteinBarHighlightedAnywhereBetweenPositionsParams ) ) {
							//  The sequence coverage block is outside of any selected regions so set the block color for Unhighlighted
							blockColor = _NOT_HIGHLIGHTED_LINE_COLOR;
						}
					}

					if ( blockColor === undefined ) {
						blockColor = segment.annotationColor;
					}
					var drawAnnotationRectangle_Params = {
							protein : protein,
							start : start,
							end : end,
							proteinBarRowIndex : proteinBarRowIndex,
							blockColor : blockColor,
							svgRootSnapSVGObject : svgRootSnapSVGObject,
							fillOpacity : 0.5
					};

					var rectangleSnapSVGObject = drawAnnotationRectangle( drawAnnotationRectangle_Params );
					var toolTipText = segment.annotationText;
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

	/**
	 * 
	 */
	function drawSequenceCoverage( selectedProteins, svgRootSnapSVGObject ) {
		var colorBySearch = false;
		if ( _colorLinesBy === SELECT_ELEMENT_COLOR_BY_SEARCH ) {
			colorBySearch = true;
		}
		var isAnyProteinBarsHighlighted = _imageProteinBarDataManager.isAnyProteinBarsHighlighted();
		for ( var proteinBarRowIndex = 0; proteinBarRowIndex < selectedProteins.length; proteinBarRowIndex++ ) {
			var protein = selectedProteins[ proteinBarRowIndex ];
			var segments = _ranges[ protein ];
			if ( segments != undefined && segments.length > 0 ) {
				for ( var k = 0; k < segments.length; k++ ) {
					var segment = segments[ k ];
					var start = segment.start;
					var end = segment.end;
					var blockColor = undefined;
					if ( isAnyProteinBarsHighlighted ) {
						var imageProteinBarDataEntry = _imageProteinBarDataManager.getItemByIndex( proteinBarRowIndex );
						var isProteinBarHighlightedAnywhereBetweenPositionsParams = {
								position_1 : start,
								position_2 : end
						};
						if ( ! imageProteinBarDataEntry.isProteinBarHighlightedAnywhereBetweenPositions( isProteinBarHighlightedAnywhereBetweenPositionsParams ) ) {
							//  The sequence coverage block is outside of any selected regions so set the block color for Unhighlighted
							blockColor = _NOT_HIGHLIGHTED_LINE_COLOR;
						}
					}
					if ( blockColor === undefined ) {
						if ( colorBySearch ) {
							blockColor = getColorForIndex( proteinBarRowIndex );
						}
					}
					if ( blockColor === undefined ) {
						blockColor = getColorForProteinBarRowIndexBlockPositions( { proteinBarRowIndex : proteinBarRowIndex, block_position_1 : start, block_position_2 : end } );
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

	/**
	 * 
	 */
	function drawAnnotationData( selectedProteins, svgRootSnapSVGObject ) {
		var annotationType = $("#annotation_type").val();
		if ( annotationType !== undefined && annotationType !== "" && annotationType !== SELECT_ELEMENT_ANNOTATION_TYPE_SEQUENCE_COVERAGE && annotationType !== SELECT_ELEMENT_ANNOTATION_TYPE_CUSTOM ) {
			if ( annotationType === SELECT_ELEMENT_ANNOTATION_TYPE_DISOPRED3 ) {
				drawDisopred_3_AnnotationData( selectedProteins, svgRootSnapSVGObject );
			} else if ( annotationType === SELECT_ELEMENT_ANNOTATION_TYPE_PSIPRED3 ) {
				drawPsipred_3_AnnotationData( selectedProteins, svgRootSnapSVGObject );
			} else {
				throw Error( "unknown annotationType: " + annotationType );
			}
		}
	}

	/**
	 * 
	 */
	function drawDisopred_3_AnnotationData( selectedProteins, svgRootSnapSVGObject ) {
		var isAnyProteinBarsHighlighted = _imageProteinBarDataManager.isAnyProteinBarsHighlighted();
		for ( var proteinBarRowIndex = 0; proteinBarRowIndex < selectedProteins.length; proteinBarRowIndex++ ) {
			var protein = selectedProteins[ proteinBarRowIndex ];
			var segments = getDisorderedRegionsDisopred_3( protein );
			if ( segments === null ) {
				//  Disopred likely Failed so display failed 
				var proteinLength = _proteinLengths.getProteinLength( protein );
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
						var blockColor = "#000000";
						if ( isAnyProteinBarsHighlighted ) {
							var imageProteinBarDataEntry = _imageProteinBarDataManager.getItemByIndex( proteinBarRowIndex );
							var isProteinBarHighlightedAnywhereBetweenPositionsParams = {
									position_1 : start,
									position_2 : end
							};
							if ( ! imageProteinBarDataEntry.isProteinBarHighlightedAnywhereBetweenPositions( isProteinBarHighlightedAnywhereBetweenPositionsParams ) ) {
								//  The annotation block is outside of any selected regions so set the block color for Unhighlighted
								blockColor = _NOT_HIGHLIGHTED_LINE_COLOR;
							}
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

	/**
	 * 
	 */
	function drawPsipred_3_AnnotationData( selectedProteins, svgRootSnapSVGObject ) {
		var isAnyProteinBarsHighlighted = _imageProteinBarDataManager.isAnyProteinBarsHighlighted();
		for ( var proteinBarRowIndex = 0; proteinBarRowIndex < selectedProteins.length; proteinBarRowIndex++ ) {
			var protein = selectedProteins[ proteinBarRowIndex ];
			var blockColor = null;
			var typeTooltipText = null;
			var segments = getSecondaryStructureRegions( protein );
			if ( segments === null ) {
				//  Secondary Structure likely Failed so display failed 
				var proteinLength = _proteinLengths.getProteinLength( protein );
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
						if ( isAnyProteinBarsHighlighted ) {
							var imageProteinBarDataEntry = _imageProteinBarDataManager.getItemByIndex( proteinBarRowIndex );
							var isProteinBarHighlightedAnywhereBetweenPositionsParams = {
									position_1 : start,
									position_2 : end
							};
							if ( ! imageProteinBarDataEntry.isProteinBarHighlightedAnywhereBetweenPositions( isProteinBarHighlightedAnywhereBetweenPositionsParams ) ) {
								//  The annotation block is outside of any selected regions so set the block color for Unhighlighted
								blockColor = _NOT_HIGHLIGHTED_LINE_COLOR;
							}
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

	/**
	 * Draws an Annotation Rectangle under the Protein Bar and returns the Snap SVG Object
	 */
	function drawAnnotationRectangle( params ) {
		var protein = params.protein;
		var start = params.start;
		var end = params.end;
		var proteinBarRowIndex = params.proteinBarRowIndex;
		var blockColor = params.blockColor;
		var svgRootSnapSVGObject = params.svgRootSnapSVGObject;
		var sx = translateAnnotationRectanglePositionToXCoordinate( protein, proteinBarRowIndex, start, _TRANSLATE_SEQUENCE_COVERAGE_POSITION_TO_X_COORDINATES_SIDE_START );
		var ex = translateAnnotationRectanglePositionToXCoordinate( protein, proteinBarRowIndex, end, _TRANSLATE_SEQUENCE_COVERAGE_POSITION_TO_X_COORDINATES_SIDE_END );
		var rectX = sx;
		var rectY = _OFFSET_FROM_TOP + ( _singleProteinBarOverallHeight * proteinBarRowIndex /* i */ ) - 10;
		var rectWidth = Math.abs( ex - sx );
		var rectHeight = _SINGLE_PROTEIN_BAR_HEIGHT + 20;
		if ( isProteinReversed( { proteinId : protein, proteinBarIndex : proteinBarRowIndex } ) ) {
			rectX = rectX - rectWidth;
		}
		var rectangleSnapSVGObject = svgRootSnapSVGObject.rect( rectX, rectY, rectWidth, rectHeight );

		if( params.fillOpacity ) {
			rectangleSnapSVGObject.attr( { fill: blockColor, "fill-opacity": params.fillOpacity } );
		} else {
			rectangleSnapSVGObject.attr( { fill: blockColor, "fill-opacity": 0.15 } );
		}

		return rectangleSnapSVGObject;
	}

	/**
	 * Add All Protein Bar Groups ( all of: rectangle and protein name label and cover transparent rectangle )
	 */
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

	/**
	 * Add a single Protein Bar Group ( rectangle and protein name label and cover transparent rectangle )
	 */
	function addSingleProteinBarGroup( i, protein, proteinNamePositionLeftSelected, showProteinTerminiSelected, svgRootSnapSVGObject, $merged_image_svg_jq ) {
		
		var rightMostEdgeOfAllElementsThisFunction = 0;
		var imageProteinBarDataEntry = _imageProteinBarDataManager.getItemByIndex( i );
		var groupSnapSVGForProteinBar = svgRootSnapSVGObject.g();  //  Create group for all elements for this protein bar
		var proteinOffset = getProteinOffset( { proteinBarIndex : i } );
		var proteinOffsetFromImageLeftEdge = proteinOffset + _proteinBarsLeftEdge;
		var rectangleStartX = proteinOffsetFromImageLeftEdge;
		var rectangleStartY = _OFFSET_FROM_TOP + ( _singleProteinBarOverallHeight * i );
		var rectanglePixelWidth = translateProteinWidthToPixelWidth( protein );
		var rectanglePixelHeight = _SINGLE_PROTEIN_BAR_HEIGHT;
		//	rectangle protein  !!!  Important, if add border to this rectangle with { stroke: "#bada55", strokeWidth: 5 }, need to add to overlay rectangle as well
		//                     !!!  Important, if change size of this rectangle, need to change overlay rectangle as well
		var proteinBarRectangleSnapSVGObject = svgRootSnapSVGObject.rect( rectangleStartX, rectangleStartY, rectanglePixelWidth, rectanglePixelHeight );
		// get color for protein bar 
		var proteinBarColor = _PROTEIN_BAR_COLOR_NOT_HIGHLIGHTED;
		if ( ( ! _imageProteinBarDataManager.isAnyProteinBarsHighlighted() ) 
				|| imageProteinBarDataEntry.isAllOfProteinBarHighlighted() ) {
			//  if no highlighted proteins or this protein bar index is highlighted, use this color
			proteinBarColor = _PROTEIN_BAR_COLOR_MAIN_AND_HIGHLIGHTED;
		}
		proteinBarRectangleSnapSVGObject.attr({
			fill: proteinBarColor
		});
		
		proteinBarRectangleSnapSVGObject.addClass( _PROTEIN_BAR_MAIN_RECTANGLE_LABEL_CLASS );
		groupSnapSVGForProteinBar.add( proteinBarRectangleSnapSVGObject );
		//   Add Group SVG object here so can add more objects at this layer in the SVG later
		var groupSnapSVGForDataItemsAboveMainProteinBar = svgRootSnapSVGObject.g();  
		groupSnapSVGForDataItemsAboveMainProteinBar.attr( { "protein_id": protein, "protein_index" : i } );  
		groupSnapSVGForDataItemsAboveMainProteinBar.addClass( _PROTEIN_BAR_GROUP_ON_TOP_OF_MAIN_RECTANGLE_LABEL_CLASS );
		groupSnapSVGForProteinBar.add( groupSnapSVGForDataItemsAboveMainProteinBar );
		//  Add in existing protein selection regions
		var proteinBarHighlightedRegions = imageProteinBarDataEntry.getProteinBarHighlightedRegionsArray();
		if ( proteinBarHighlightedRegions && proteinBarHighlightedRegions.length > 0 ) {
			for ( var index = 0; index < proteinBarHighlightedRegions.length; index++ ) {
				var proteinRegion = proteinBarHighlightedRegions[ index ];
				var regionStartSequencePosition = proteinRegion.s - 0.5;
				var regionEndSequencePosition = proteinRegion.e + 0.5;
				var regionRectStartX = translatePositionToXCoordinate( { position : regionStartSequencePosition, proteinId : protein, proteinBarIndex : i } );
				var regionRectEndX = translatePositionToXCoordinate( { position : regionEndSequencePosition, proteinId : protein, proteinBarIndex : i } );
				if ( regionRectStartX > regionRectEndX ) {
					//  flip if needed so start is before end
					var regionRectTemp = regionRectStartX;
					regionRectStartX = regionRectEndX;
					regionRectEndX = regionRectTemp;
				}
				var regionRectWidth = regionRectEndX - regionRectStartX;
				var selectRegionRectangleSnapSVGObject = 
					svgRootSnapSVGObject.rect( regionRectStartX, rectangleStartY, regionRectWidth, rectanglePixelHeight );
				selectRegionRectangleSnapSVGObject.attr({
					fill: _PROTEIN_BAR_COLOR_MAIN_AND_HIGHLIGHTED
				});
				groupSnapSVGForDataItemsAboveMainProteinBar.add( selectRegionRectangleSnapSVGObject );
			}
		}
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
		groupSnapSVGForProteinBar.add( proteinLabelTextSnapSVGObject );
		if ( showProteinTerminiSelected ) {
			//   arbitrary position for now
			var protein_Left_TerminusStartX = proteinOffsetFromImageLeftEdge - _PROTEIN_TERMINUS_BAR_HORIZONTAL_OFFSET;
			var protein_Left_TerminusStartY = _OFFSET_FROM_TOP + ( _singleProteinBarOverallHeight * i ) 
				+ _SINGLE_PROTEIN_BAR_HEIGHT + _PROTEIN_TERMINUS_BAR_VERTICAL_OFFSET;
			var leftTerminusLabel = _PROTEIN_TERMINUS_LABEL_N;
			var rightTerminusLabel = _PROTEIN_TERMINUS_LABEL_C;
			if ( isProteinReversed( { proteinId : protein, proteinBarIndex : i } ) ) {
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
						var x = translatePositionToXCoordinate( { position : positions[ k ], proteinId : protein, proteinBarIndex : i } );
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
						var x = translatePositionToXCoordinate( { position : positions[ k ], proteinId : protein, proteinBarIndex : i } );
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
			//  eventObject  is a jQuery object.
			//  Another option for getting the offset left for the protein bar overlay rectangle.
			//     This appears to produce the correct result but not fully researched or tested.
	//		var eventTarget = eventObject.target;
	//		var $eventTarget = $( eventTarget );
	//		var eventTargetOffset = $eventTarget.offset();
	//		var eventTargetOffsetLeft = eventTargetOffset.left;
			
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
			if ( isProteinReversed( { proteinId : protein, proteinBarIndex : i } ) ) {
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
			if ( isProteinReversed( { proteinId : protein, proteinBarIndex : i } ) ) {
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

			var $rectangle = $( this ); 
		});
		//  END  Add mouse handlers to overlay transparent rectangle for managing "groupMouseOverInfoBlock" content and position
		///////
		addClickDoubleClickTo( groupSnapSVGForProteinBar, protein, i );
		addDragTo( groupSnapSVGForProteinBar, protein, i, svgRootSnapSVGObject );
		var rightEdgeOfRectangle = rectangleStartX + rectanglePixelWidth - 1;
		if ( rightEdgeOfRectangle > rightMostEdgeOfAllElementsThisFunction ) {
			rightMostEdgeOfAllElementsThisFunction = rightEdgeOfRectangle;
		} 
		var functionResponseObject = {
				rightMostEdgeOfAllElementsThisFunction : rightMostEdgeOfAllElementsThisFunction
		};
		return functionResponseObject;
	}

	/**
	 * 
	 */
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

	/**
	 * draw interprotein crosslink line
	 */
	function drawInterProteinCrosslinkLines( selectedProteins, svgRootSnapSVGObject ) {
		var colorBySearch = false;
		if ( _colorLinesBy === SELECT_ELEMENT_COLOR_BY_SEARCH ) {
			colorBySearch = true;
		}
		for ( var fromSelectedProteinsIndex = 0; fromSelectedProteinsIndex < selectedProteins.length; fromSelectedProteinsIndex++ ) {
			var fromProteinId = selectedProteins[ fromSelectedProteinsIndex ];
			var fromY = _OFFSET_FROM_TOP + ( _singleProteinBarOverallHeight * fromSelectedProteinsIndex ) + _SINGLE_PROTEIN_BAR_HEIGHT;
			for ( var toSelectedProteinsIndex = 0; toSelectedProteinsIndex < selectedProteins.length; toSelectedProteinsIndex++ ) {
				var toProteinId = selectedProteins[ toSelectedProteinsIndex ];
				if ( toSelectedProteinsIndex <= fromSelectedProteinsIndex ) { continue; }
				if ( _proteinLinkPositions[ selectedProteins[ fromSelectedProteinsIndex ] ] == undefined ) { continue; }
				if ( _proteinLinkPositions[ selectedProteins[ fromSelectedProteinsIndex ] ][ toProteinId ] == undefined ) { continue; }
				var toY = _OFFSET_FROM_TOP + ( _singleProteinBarOverallHeight * toSelectedProteinsIndex );
				var fromProteinPositionObject = _proteinLinkPositions[ fromProteinId ][ toProteinId ];
				var fromProteinPositionKeys = Object.keys( fromProteinPositionObject );
				for ( var ii = 0; ii < fromProteinPositionKeys.length; ii++ ) {
					var fromProteinPosition = fromProteinPositionKeys[ ii ];
					var fromProteinPositionInt =  parseInt( fromProteinPosition );
					var x1 = translatePositionToXCoordinate( { position : fromProteinPositionInt, proteinId : fromProteinId, proteinBarIndex : fromSelectedProteinsIndex } );
					var y1 = fromY;
					var toProteinPositionObject = _proteinLinkPositions[ fromProteinId ][ toProteinId ][ fromProteinPosition ];
					var toProteinPositionKeys = Object.keys( toProteinPositionObject );
					for ( var kk = 0; kk < toProteinPositionKeys.length; kk++ ) {
						var toProteinPosition = toProteinPositionKeys[ kk ];
						var toProteinPositionInt =  parseInt( toProteinPosition );
						var x2 = translatePositionToXCoordinate( { position : toProteinPositionInt, proteinId : toProteinId, proteinBarIndex : toSelectedProteinsIndex } );
						var y2 = toY;
						var findSearchesForCrosslink__response = undefined;
						var fromP = _proteinNames[ selectedProteins[ fromSelectedProteinsIndex ] ];
						var toP = _proteinNames[ toProteinId ];
						var fromPp = fromProteinPositionInt;
						var toPp = toProteinPositionInt;
						var lsearches = _proteinLinkPositions[ fromProteinId ][ toProteinId ][ fromProteinPosition ][ toProteinPosition ];
						// for display to user
						var searchIdArray = convertProjectSearchIdArrayToSearchIdArray( lsearches );
						var getCrosslinkLineColorParams = { 
								fromProteinIndex : fromSelectedProteinsIndex,
								fromProteinPosition : fromPp, 
								toProteinIndex : toSelectedProteinsIndex,
								toProteinPosition : toPp 
						};
						if ( colorBySearch ) {
							findSearchesForCrosslink__response = 
								findSearchesForCrosslink( 
										selectedProteins[ fromSelectedProteinsIndex ], 
										selectedProteins[ toSelectedProteinsIndex ], 
										fromProteinPosition, toProteinPosition );
							getCrosslinkLineColorParams.searchList = findSearchesForCrosslink__response;
						}
						var lineColor = getCrosslinkLineColor( getCrosslinkLineColorParams );
						if ( lineColor === undefined ) {
							var searchesListPart = "";
							if ( findSearchesForCrosslink__response ) {
								searchesListPart = ", findSearchesForCrosslink__response: [" + findSearchesForCrosslink__response.join( ", " ) + "]";
							}
							throw Error( "lineColor returned from getCrosslinkLineColorForSearches(...) is undefined for selectedProteinsIndex: " + fromSelectedProteinsIndex + ", k: " + toSelectedProteinsIndex + searchesListPart );
						}
						console.assert( lineColor != undefined );
						var link = { 
								type : 'crosslink',
								protein1 : selectedProteins[ fromSelectedProteinsIndex ],
								position1 : fromPp,
								protein2 : selectedProteins[ toSelectedProteinsIndex ],
								position2 : toPp,
								searchIds : lsearches
						};
						var getCrosslinkLineOpacityParams = {
								link : link,
								fromProteinIndex : fromSelectedProteinsIndex,
								fromProteinPosition : fromPp,  
								toProteinIndex : toSelectedProteinsIndex,
								toProteinPosition  : toPp
						};
						var line = svgRootSnapSVGObject.line( x1, y1, x2, y2 );
						line.attr({
							stroke: lineColor,
							strokeWidth: 1,
							"stroke-opacity": getCrosslinkLineOpacity( getCrosslinkLineOpacityParams ),
							'from_protein_id':selectedProteins[ fromSelectedProteinsIndex ],
							'to_protein_id':selectedProteins[ toSelectedProteinsIndex ],
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
									text: 'Crosslink: ' + fromP + "(" + fromPp + ") - " + toP + "(" + toPp + ")<br>Searches: " + searchIdArray
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

	/**
	 * 
	 */
	function addClickHandler__InterProteinCrosslinkLines( $SVGNativeObject ) {
		//  Add click handler
		$SVGNativeObject.click( function(  ) {
			try {
				processClickOnCrossLink( this );
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
	}

	/**
	 * draw self-crosslinks
	 */
	function drawSelfProteinCrosslinkLines( selectedProteins, svgRootSnapSVGObject ) {
		var colorBySearch = false;
		if ( _colorLinesBy === SELECT_ELEMENT_COLOR_BY_SEARCH ) {
			colorBySearch = true;
		}
		for ( var i = 0; i < selectedProteins.length; i++ ) {
			var proteinBarProteinId = selectedProteins[ i ];
			if ( _proteinLinkPositions[ proteinBarProteinId ] == undefined ) { continue; }
			var fromY = _OFFSET_FROM_TOP + ( _singleProteinBarOverallHeight * i );
			var toY =   _OFFSET_FROM_TOP + ( _singleProteinBarOverallHeight * i );
			if ( _proteinLinkPositions[ proteinBarProteinId ][ proteinBarProteinId ] == undefined ) { continue; }
			var fromProteinPositionKeys = Object.keys( _proteinLinkPositions[ proteinBarProteinId ][ proteinBarProteinId ] );
			for ( var ii = 0; ii < fromProteinPositionKeys.length; ii++ ) {
				var fromProteinPosition = fromProteinPositionKeys[ ii ];
				var fromProteinPositionInt =  parseInt( fromProteinPosition );
				var x1 = translatePositionToXCoordinate( { position : fromProteinPositionInt, proteinId : proteinBarProteinId, proteinBarIndex : i } );
				var y1 = fromY;
				var toProteinPositionKeys = Object.keys( _proteinLinkPositions[ proteinBarProteinId ][ proteinBarProteinId ][ fromProteinPosition ] );
				for ( var kk = 0; kk < toProteinPositionKeys.length; kk++ ) {
					var toProteinPosition = toProteinPositionKeys[ kk ];
					var toProteinPositionInt =  parseInt( toProteinPosition );
					if ( fromProteinPositionInt > toProteinPositionInt ) {
						//  Drawing line from right to left.  
						//  Likely line already drawn for same positions from left to right.
						//  Check for line already drawn between From and To positions
						var toPositionsUsingToPositionAsFromPosition = _proteinLinkPositions[ proteinBarProteinId ][ proteinBarProteinId ][ toProteinPosition ];
						if ( toPositionsUsingToPositionAsFromPosition !== undefined ) {
							//  toPosition found as a from position 
							//  so now check if the fromPosition is a to position in that sub-array
							if ( toPositionsUsingToPositionAsFromPosition[ fromProteinPosition ] !== undefined ) {
								//  fromPosition is a to position in that sub-array
								//  so a line has already been drawn from left to right
								//  for these positions
								//  Skip drawing this line
								continue;  //  EARLY CONTINUE
							}
						}
					}
					var x2 = translatePositionToXCoordinate( { position : toProteinPositionInt, proteinId : proteinBarProteinId, proteinBarIndex : i } );
					var y2 = toY;
					var fromP = _proteinNames[ proteinBarProteinId ];
					var toP = _proteinNames[ proteinBarProteinId ];
					var fromPp = fromProteinPositionInt;
					var toPp = toProteinPositionInt;
					var lsearches = _proteinLinkPositions[ proteinBarProteinId ][ proteinBarProteinId ][ fromProteinPosition ][ toProteinPosition ];
					// for display to user
					var searchIdArray = convertProjectSearchIdArrayToSearchIdArray( lsearches );
					var link = { 
							type : 'crosslink',
							protein1 : proteinBarProteinId,
							position1 : fromPp,
							protein2 : proteinBarProteinId,
							position2 : toPp,
							searchIds : lsearches
					};
					var getLineColorSingleProteinBarParams = { 
							proteinIndex : i, 
							fromProteinPosition : fromProteinPositionInt, 
							toProteinPosition : toProteinPositionInt
					};
					if ( colorBySearch ) {
						var searchList = findSearchesForCrosslink( proteinBarProteinId, proteinBarProteinId, fromProteinPosition, toProteinPosition );
						getLineColorSingleProteinBarParams.searchList = searchList;
					};
					var lineColor = getLineColorSingleProteinBar( getLineColorSingleProteinBarParams ) ;
					var lineOpacity = 
						getLineOpacitySingleProteinBar( { 
							link : link,
							proteinIndex : i,
							fromProteinPosition : fromProteinPositionInt,  
							toProteinPosition : toProteinPositionInt
						} );
					var arc = svgRootSnapSVGObject.path( makeArcPath( _MAKE_ARC_PATH_DIRECTION_UP, x1, y1, x2, y2 ) );
					arc.attr({
						stroke:lineColor,
						strokeWidth:1,
						fill: "none",
						"stroke-opacity": lineOpacity,
						'from_protein_id':proteinBarProteinId,
						'to_protein_id':proteinBarProteinId,
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
								text: 'Crosslink: ' + fromP + "(" + fromPp + ") - " + toP + "(" + toPp + ")<br>Searches: " + searchIdArray
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

	/**
	 * 
	 */
	function addClickHandler__SelfProteinCrosslinkLines( $SVGNativeObject ) {
		//  Add click handler
		$SVGNativeObject.click( function(  ) {
			try {
				processClickOnCrossLink( this );
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
	}

	/**
	 * draw looplinks
	 */
	function drawProteinLooplinkLines( selectedProteins, svgRootSnapSVGObject ) {
		var colorBySearch = false;
		if ( _colorLinesBy === SELECT_ELEMENT_COLOR_BY_SEARCH ) {
			colorBySearch = true;
		}
		for ( var i = 0; i < selectedProteins.length; i++ ) {
			var proteinBarProteinId = selectedProteins[ i ];
			var fromY = _OFFSET_FROM_TOP + ( _singleProteinBarOverallHeight * i ) + _SINGLE_PROTEIN_BAR_HEIGHT;
			var toY =   _OFFSET_FROM_TOP + ( _singleProteinBarOverallHeight * i ) + _SINGLE_PROTEIN_BAR_HEIGHT;
			if ( _proteinLooplinkPositions[ proteinBarProteinId ] == undefined ) { continue; }
			if ( _proteinLooplinkPositions[ proteinBarProteinId ][ proteinBarProteinId ] == undefined ) { continue; }
			var fromProteinPositionKeys = Object.keys( _proteinLooplinkPositions[ proteinBarProteinId ][ proteinBarProteinId ] );
			for ( var ii = 0; ii < fromProteinPositionKeys.length; ii++ ) {
				var fromProteinPosition = fromProteinPositionKeys[ ii ];
				var fromProteinPositionInt =  parseInt( fromProteinPosition );
				var x1 = translatePositionToXCoordinate( { position : fromProteinPositionInt, proteinId : proteinBarProteinId, proteinBarIndex : i } );
				var y1 = fromY;
				var toProteinPositionKeys = Object.keys( _proteinLooplinkPositions[ proteinBarProteinId ][ proteinBarProteinId ][ fromProteinPosition ] );
				for ( var kk = 0; kk < toProteinPositionKeys.length; kk++ ) {
					var toProteinPosition = toProteinPositionKeys[ kk ];
					var toProteinPositionInt =  parseInt( toProteinPosition );
					var x2 = translatePositionToXCoordinate( { position : toProteinPositionInt, proteinId : proteinBarProteinId, proteinBarIndex : i } );
					var y2 = toY;
					var fromP = _proteinNames[ proteinBarProteinId ];
					var fromPp = fromProteinPositionInt;
					var toPp = toProteinPositionInt;
					var lsearches = _proteinLooplinkPositions[ proteinBarProteinId ][ proteinBarProteinId ][ fromProteinPosition ][ toProteinPosition ];
					// for display to user
					var searchIdArray = convertProjectSearchIdArrayToSearchIdArray( lsearches );
					var link = {
							type : 'looplink',
							protein1 : selectedProteins[ i ],
							position1 : fromPp,
							position2 : toPp,
							searchIds : lsearches
					};
					var getLineColorSingleProteinBarParams = {
							proteinIndex : i,
							fromProteinPosition : fromProteinPositionInt,  
							toProteinPosition : toProteinPositionInt
					};
					if ( colorBySearch ) {
						var searchList = findSearchesForLooplink( proteinBarProteinId, fromProteinPosition, toProteinPosition );
						getLineColorSingleProteinBarParams.searchList = searchList;
					};
					var lineColor = getLineColorSingleProteinBar( getLineColorSingleProteinBarParams ) ;
					var lineOpacity = 
						getLineOpacitySingleProteinBar( { 
							link : link,
							proteinIndex : i,
							fromProteinPosition : fromProteinPositionInt,  
							toProteinPosition : toProteinPositionInt
						} );
					var arc = svgRootSnapSVGObject.path( makeArcPath( _MAKE_ARC_PATH_DIRECTION_DOWN, x1, y1, x2, y2 ) );
					arc.attr({
						stroke:lineColor,
						strokeWidth:1,
						//"stroke-dasharray":"1,1",
						fill: "none",
						"stroke-opacity": lineOpacity,
						'from_protein_id': proteinBarProteinId,
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
								text: 'Looplink: ' + fromP + "(" + fromPp + "," + toPp + ")<br>Searches: " + searchIdArray
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

	/**
	 * 
	 */
	function addClickHandler__ProteinLooplinkLines( $SVGNativeObject ) {
		//  Add click handler
		$SVGNativeObject.click( function(  ) {
			try {
				processClickOnLoopLink( this );
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
	}

	/**
	 * draw monolinks
	 */
	function drawProteinMonolinkLines( selectedProteins, svgRootSnapSVGObject ) {
		var colorBySearch = false;
		if ( _colorLinesBy === SELECT_ELEMENT_COLOR_BY_SEARCH ) {
			colorBySearch = true;
		}
		for ( var i = 0; i < selectedProteins.length; i++ ) {
			var proteinBarProteinId = selectedProteins[ i ];
			var fromY = _OFFSET_FROM_TOP + ( _singleProteinBarOverallHeight * i ) + _SINGLE_PROTEIN_BAR_HEIGHT;
			var toY = fromY + 16;
			if ( _proteinMonolinkPositions[ selectedProteins[ i ] ] == undefined ) { 
				continue; //  skip processing this selected protein 
			}
			var positions = Object.keys( _proteinMonolinkPositions[ selectedProteins[ i ] ] );
			for ( var k = 0; k < positions.length; k++ ) {
				var proteinPosition = positions[ k ];
				var proteinPositionInt =  parseInt( proteinPosition );
				var x = translatePositionToXCoordinate( { position : proteinPosition, proteinId : proteinBarProteinId, proteinBarIndex : i } );
				var fromP = _proteinNames[ proteinBarProteinId ];
				var fromPp = proteinPositionInt;
				var lsearches = _proteinMonolinkPositions[ proteinBarProteinId ][ proteinPosition ];
				var searchIdArray = convertProjectSearchIdArrayToSearchIdArray( lsearches );
				var link = {
						type : 'monolink',
						protein1 : proteinBarProteinId,
						position1 : fromPp,
						searchIds : lsearches
				};
				var getLineColorSingleProteinBarParams = {
						proteinIndex : i,
						fromProteinPosition : proteinPositionInt  
				};
				if ( colorBySearch ) {
					var searchList = findSearchesForMonolink( proteinBarProteinId, proteinPosition ) ;
					getLineColorSingleProteinBarParams.searchList = searchList;
				};
				var lineColor = getLineColorSingleProteinBar( getLineColorSingleProteinBarParams ) ;
				var lineOpacity = 
					getLineOpacitySingleProteinBar( { 
						link : link,
						proteinIndex : i,
						fromProteinPosition : proteinPositionInt
					} );
				var line = svgRootSnapSVGObject.line( x, fromY, x, toY );
				line.attr({
					stroke:lineColor,
					strokeWidth:1,
					//"stroke-dasharray":"1,1",
					fill: "none",
					"stroke-opacity": lineOpacity,
					'from_protein_id':proteinBarProteinId,
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
					"stroke-opacity": lineOpacity,
					'from_protein_id':proteinBarProteinId,
					'fromp': fromP,
					'frompp': fromPp,
					'searches': lsearches,
					'linktype' : 'monolink'
				});
				if ( lineColor !== _NOT_HIGHLIGHTED_LINE_COLOR ) {
	//				add tooltips to mono links
					var toolTipText = 'Monolink: ' + fromP + "(" + fromPp + ")<br>Searches: " + searchIdArray;
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

	/**
	 * 
	 */
	function addClickHandler__ProteinMonolinkLines( $SVGNativeObject ) {
	//	Add click handler
		$SVGNativeObject.click( function(  ) {
			try {
				processClickOnMonoLink( this );
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
	}

	///////////////////////////////////////////////
	var _COLOR_BY_SEARCH_LEGEND_TEXT_LEFT_FROM_STARTX_PX = -4;
	var _COLOR_BY_SEARCH_LEGEND_TEXT_RIGHT_SIDE_SPACING_PX = 10;
	var _COLOR_BY_SEARCH_MIN_SPACING_PX = 10;
	var _COLOR_BY_SEARCH_LEGEND_FIRST_ROW_HEIGHT = 40;
	var _COLOR_BY_SEARCH_LEGEND_FIRST_ROW_VERTICAL_OFFSET = 30;
	var _COLOR_BY_SEARCH_LEGEND_ADDNL_ROW_HEIGHT = 30;
	var _COLOR_BY_SEARCH_LEGEND_ADDNL_ROW_VERTICAL_OFFSET = 20;
	var _COLOR_BY_SEARCH_LEGEND_COLOR_BLOCK_WIDTH = 20;
	var _COLOR_BY_SEARCH_LEGEND_COLOR_BLOCK_HEIGHT = 20;

	/**
	 * 
	 */
	function drawColorBySearchLegend(  selectedProteins, bottomOfLowestItemDrawn, svgRootSnapSVGObject ) {
		//  This legend, like the rest of 'Color by search' only supports up to 3 search
		if ( _searches.length > 3 ) {
			return null;
		}
		var rightMostEdgeOfAllElementsThisFunction = 0;
		var maxSvgImageWidth = get_MAX_WIDTH();
		//  Base basey of the bottom of previous drawn items.  This way it adjusts for when the scale bar is not drawn
		var basey = bottomOfLowestItemDrawn + _COLOR_BY_SEARCH_LEGEND_FIRST_ROW_VERTICAL_OFFSET;
		bottomOfLowestItemDrawn += _COLOR_BY_SEARCH_LEGEND_FIRST_ROW_HEIGHT;  // adjust bottomOfLowestItemDrawn for this item
		var startx = translateScaleBarPositionToXCoordinate( 1 );
		var legendLabelLeftEdge = startx + _COLOR_BY_SEARCH_LEGEND_TEXT_LEFT_FROM_STARTX_PX;
		var groupSnapSVGForSearchLegend = svgRootSnapSVGObject.g();
		var legendLabelSnapSVGObject = svgRootSnapSVGObject.text( legendLabelLeftEdge , basey, "Legend:" );
		groupSnapSVGForSearchLegend.add( legendLabelSnapSVGObject );
		var legendLabelSnapSVGObjectBBox = legendLabelSnapSVGObject.getBBox();
		var legendLabelSnapSVGObjectWidth = legendLabelSnapSVGObjectBBox.width;
		var searchItemsLeftMargin = legendLabelLeftEdge + legendLabelSnapSVGObjectWidth + _COLOR_BY_SEARCH_LEGEND_TEXT_RIGHT_SIDE_SPACING_PX;
		var searchItemsNextPosition = searchItemsLeftMargin;
		var maxWidthOfLegendItem = 0;
		var updateMaxItemWidthAndDelete = function( groupSnapSVGForSearchLegendGroup ) {
			var groupSnapSVGForSearchLegendGroupBBox = groupSnapSVGForSearchLegendGroup.getBBox();
			var groupSnapSVGForSearchLegendGroupWidth = groupSnapSVGForSearchLegendGroupBBox.width;
			//  Remove temporary text object inserted to get it's size
			groupSnapSVGForSearchLegendGroup.remove();
			if ( maxWidthOfLegendItem < groupSnapSVGForSearchLegendGroupWidth ) {
				maxWidthOfLegendItem = groupSnapSVGForSearchLegendGroupWidth;
			}
		};
		var advanceToNextItemPosition = function( params ) {
			var forceNextLine = false;
			if ( params && params.forceNextLine ) {
				forceNextLine = true; //  Added for adding item with 3 search Ids and need to force to next line
			}
			searchItemsNextPosition += maxWidthOfLegendItem;
			var searchItemRightEdge = searchItemsNextPosition + maxWidthOfLegendItem;
			if ( ( searchItemRightEdge > maxSvgImageWidth ) || forceNextLine ) {
				//  Move the rest of the search blocks to another row
				basey = bottomOfLowestItemDrawn + _COLOR_BY_SEARCH_LEGEND_ADDNL_ROW_VERTICAL_OFFSET;
				bottomOfLowestItemDrawn += _COLOR_BY_SEARCH_LEGEND_ADDNL_ROW_HEIGHT;  // adjust bottomOfLowestItemDrawn for this item
				//  Start at left edge on next row
				searchItemsNextPosition = searchItemsLeftMargin;
			}
		};
		var addLegendItem = function( searchesArray ) {
			var searchLabel = "Search";
			if ( searchesArray.length > 1 ) {
				searchLabel = "Searches";
			}
			var searchIdArray = convertProjectSearchIdArrayToSearchIdArray( searchesArray );
			var searchIdsAsCommaDelimText = searchIdArray.join();
			var groupSnapSVGForSearchLegendGroup = svgRootSnapSVGObject.g();
			var legendPerItemRectangle_Y_Position = basey - 15;
			var legendRectangle = svgRootSnapSVGObject.rect( 
					searchItemsNextPosition, legendPerItemRectangle_Y_Position, 
					_COLOR_BY_SEARCH_LEGEND_COLOR_BLOCK_WIDTH, _COLOR_BY_SEARCH_LEGEND_COLOR_BLOCK_HEIGHT );
			legendRectangle.attr( {
				fill: getColorForSearchesForLegend( searchesArray )
			});
			var legendPerItemText_X_Position = searchItemsNextPosition + 23;
			var legendPerItemText_Y_Position = basey;
			var legendText = svgRootSnapSVGObject.text( 
					legendPerItemText_X_Position, legendPerItemText_Y_Position, " " + searchLabel + ": " + searchIdsAsCommaDelimText );
			groupSnapSVGForSearchLegendGroup.add( legendRectangle );
			groupSnapSVGForSearchLegendGroup.add( legendText );
			return groupSnapSVGForSearchLegendGroup;
		};
		var addItemsForOneAndTwoSearchIds = function( params ) {
			var onlyMeasureWidth = params.onlyMeasureWidth;
			for ( var i = 0; i < _searches.length; i++ ) {
				var search = _searches[ i ];
				if ( search === undefined ) {
					throw Error( "search not found in _searches for index: " + i );
				}
				var searchId = search[ 'id' ];
				if ( searchId === undefined ) {
					throw Error( "searchId not found in _searches for index: " + i );
				}
				if ( ! onlyMeasureWidth && i !== 0 ) {
					//  Skip for first entry
					advanceToNextItemPosition();
				}
				var groupSnapSVGForSearchLegendGroup = addLegendItem( [ searchId ] );
				if ( onlyMeasureWidth ) {
					updateMaxItemWidthAndDelete( groupSnapSVGForSearchLegendGroup );
				} else {
					groupSnapSVGForSearchLegend.add( groupSnapSVGForSearchLegendGroup );
				}
			}
			if ( _searches.length > 1 ) {
				//  A legend part for all combinations of 2 searches
				for ( var i = 0; i < _searches.length; i++ ) {
					var search_1 = _searches[ i ];
					if ( search_1 === undefined ) {
						throw Error( "search not found in _searches for index: " + i );
					}
					var searchId_1 = search_1[ 'id' ];
					if ( searchId_1 === undefined ) {
						throw Error( "searchId not found in _searches for index: " + i );
					}
					for ( var k = i + 1; k < _searches.length; k++ ) {
						var search_2 = _searches[ k ];
						if ( search_2 === undefined ) {
							throw Error( "search not found in _searches for index: " + k );
						}
						var searchId_2 = search_2[ 'id' ];
						if ( searchId_2 === undefined ) {
							throw Error( "searchId not found in _searches for index: " + k );
						}
						if ( ! onlyMeasureWidth ) {
							advanceToNextItemPosition();
						}
						var groupSnapSVGForSearchLegendGroup = addLegendItem( [ searchId_1, searchId_2 ] );
						if ( onlyMeasureWidth ) {
							updateMaxItemWidthAndDelete( groupSnapSVGForSearchLegendGroup );
						} else {
							groupSnapSVGForSearchLegend.add( groupSnapSVGForSearchLegendGroup );
							var groupSnapSVGForSearchLegendGroupBBox = groupSnapSVGForSearchLegendGroup.getBBox();
							var groupSnapSVGForSearchLegendGroupRightSide = groupSnapSVGForSearchLegendGroupBBox.x2;
							if ( groupSnapSVGForSearchLegendGroupRightSide > rightMostEdgeOfAllElementsThisFunction ) {
								rightMostEdgeOfAllElementsThisFunction = groupSnapSVGForSearchLegendGroupRightSide;
							} 
						}
					}
				}
			}
		};
		//  add, measure for max, delete
		addItemsForOneAndTwoSearchIds( { onlyMeasureWidth : true } );
		//  Add so there is space between items
		maxWidthOfLegendItem += _COLOR_BY_SEARCH_MIN_SPACING_PX;
		//  add
		addItemsForOneAndTwoSearchIds( { onlyMeasureWidth : false } );
		//  
		if ( _searches.length > 2 ) {
			//  A legend part for the color for all 3 searches combined 
			advanceToNextItemPosition();
			var legendItemSearchArray = [ _searches[ 0 ][ 'id' ], _searches[ 1 ][ 'id' ], _searches[ 2 ][ 'id' ] ];
			var groupSnapSVGForSearchLegendGroup = addLegendItem( legendItemSearchArray );
			var groupSnapSVGForSearchLegendGroupBBox = groupSnapSVGForSearchLegendGroup.getBBox();
			var groupSnapSVGForSearchLegendGroupRightSide = groupSnapSVGForSearchLegendGroupBBox.x2;
			if ( groupSnapSVGForSearchLegendGroupRightSide > maxSvgImageWidth ) {
				//  Item for 3 searches extends past max width so delete it and add it on the next line
				groupSnapSVGForSearchLegendGroup.remove();
				advanceToNextItemPosition( { forceNextLine : true } );
				var groupSnapSVGForSearchLegendGroup = addLegendItem( legendItemSearchArray );
			}
			groupSnapSVGForSearchLegend.add( groupSnapSVGForSearchLegendGroup );
			//  recompute bbox in case changed
			groupSnapSVGForSearchLegendGroupBBox = groupSnapSVGForSearchLegendGroup.getBBox();
			groupSnapSVGForSearchLegendGroupRightSide = groupSnapSVGForSearchLegendGroupBBox.x2;
			if ( rightMostEdgeOfAllElementsThisFunction < groupSnapSVGForSearchLegendGroupRightSide ) {
				rightMostEdgeOfAllElementsThisFunction = groupSnapSVGForSearchLegendGroupRightSide;
			} 
		}
		var functionResponseObject = {
				rightMostEdgeOfAllElementsThisFunction : rightMostEdgeOfAllElementsThisFunction,
				bottomOfLowestItemDrawn : bottomOfLowestItemDrawn
		};
		return functionResponseObject;
	}

	/////////////////////////////////////////////////////////
	/*
	* For the given position in the given protein, determine the x coordinate that the
	* annotation bar should start or end. The variable named "side" must be
	* either "start" or "end", based on whether this query is resulting in the start
	* or end of the sequence coverage box.
	*/
	function translateAnnotationRectanglePositionToXCoordinate( protein, proteinBarRowIndex, position, side ) {
		var m = getMultiplier();
		if ( isProteinReversed( { proteinId : protein, proteinBarIndex : proteinBarRowIndex } ) ) {
			//  Flip the "side"
			if ( side === _TRANSLATE_SEQUENCE_COVERAGE_POSITION_TO_X_COORDINATES_SIDE_START ) {
				side = _TRANSLATE_SEQUENCE_COVERAGE_POSITION_TO_X_COORDINATES_SIDE_END;
			} else {
				side === _TRANSLATE_SEQUENCE_COVERAGE_POSITION_TO_X_COORDINATES_SIDE_START;
			}
		}
		if ( side === _TRANSLATE_SEQUENCE_COVERAGE_POSITION_TO_X_COORDINATES_SIDE_START ) {
			return translatePositionToXCoordinate( { position : position, proteinId : protein, proteinBarIndex : proteinBarRowIndex } ) - (m / 2 );
		} else if ( side === _TRANSLATE_SEQUENCE_COVERAGE_POSITION_TO_X_COORDINATES_SIDE_END ) {
			return translatePositionToXCoordinate(  { position : position, proteinId : protein, proteinBarIndex : proteinBarRowIndex } ) + (m / 2 );
		} else {
			throw Error( "translateAnnotationRectanglePositionToXCoordinate: unknown value for side: |" + side + "|" ); 
		}
	}

	/*
	* For a given protein, calculate its width in pixels for the viewer
	*/
	function translateProteinWidthToPixelWidth( protein ) {
		var m = getMultiplier();
		var l = _proteinLengths.getProteinLength( protein );
		return m * l;
	}

	/*
	* For a given protein and position, determine the x-axis pixel coordinate on the rendered viewer
	*/
	function translatePositionToXCoordinate( params ) {	
		var position = params.position;
		var proteinId = params.proteinId;
		var proteinBarIndex = params.proteinBarIndex;
		var m = getMultiplier();
		var offset = ( position * m  ) - ( m / 2 );
		if ( isProteinReversed( { proteinId : proteinId, proteinBarIndex : proteinBarIndex } ) ) {
			var rectanglePixelWidth = translateProteinWidthToPixelWidth( proteinId );
			//  reverse to be offset from right edge of protein bar
			offset =  rectanglePixelWidth - offset; 
		}
		return getProteinOffset( { proteinBarIndex : proteinBarIndex } ) + _proteinBarsLeftEdge + offset;
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

	/**
	 * Protein Select
	 */
	function populateSelectProteinSelect( ) {
		var $protein_list_container = $("#protein_list_container");
		$protein_list_container.empty();
		var $select_protein_overlay_protein_entry_template = $("#select_protein_overlay_protein_entry_template");
		var proteinEntry_template_handlebarsSource = $select_protein_overlay_protein_entry_template.text();
		if ( proteinEntry_template_handlebarsSource === undefined ) {
			throw Error( "proteinEntry_template_handlebarsSource === undefined" );
		}
		if ( proteinEntry_template_handlebarsSource === null ) {
			throw Error( "proteinEntry_template_handlebarsSource === null" );
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
				try {
					processClickOnSelectProtein( { clickedThis : this } );
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
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

	/**
	 * 
	 */
	function closeAddProteinSelect( params ) {
		var $select_protein_modal_dialog_overlay_background = $("#select_protein_modal_dialog_overlay_background");
		$select_protein_modal_dialog_overlay_background.hide();
		var $select_protein_overlay_div = $("#select_protein_overlay_div");
		$select_protein_overlay_div.hide();
	}

	/**
	 * Opens the protein selection overlay, and populates it with appropriate data.
	 */
	function openSelectProteinSelect( params ) {
		if ( ! params ) {
			throw Error( "params is empty" );
		}
		var clickedUID, clickedProteinId;
		var clickedThis = params.clickedThis;
		var addProteinsClicked = params.addProteinsClicked;
		if ( ! clickedThis ) {
			throw Error( "clickedThis is empty" );
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
		clickedUID = $clickedThis.attr("data-uid");
		if ( !clickedUID ) {
			clickedUID = null;
		}
		if ( addProteinsClicked ) {
			//  "Add Protein" button clicked
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
		var $protein_list_outer_container = $("#protein_list_outer_container");
		var $protein_list_container = $("#protein_list_container");
		//  Save off values from clicked Selected Protein.  Will be null for Add Protein
		$protein_list_container.data( { 
			clickedUID : clickedUID,
			addProteinsClicked : addProteinsClicked } );
		//  Highlight the currently selected protein
		var $protein_select_jq_Items = $protein_list_container.find(".protein_select_jq");
		//  Clean up to remove all highlighted protein names
		$protein_select_jq_Items.removeClass("single-protein-select-item-highlight");
		$protein_select_jq_Items.removeClass( _PROTEIN_OVERLAY_PROTEIN_SELECTED );
		if( clickedUID ) {
			clickedProteinId = _indexManager.getProteinIdForUID( clickedUID );
		}
		if ( clickedProteinId !== undefined && clickedProteinId !== null ) {
			$protein_select_jq_Items.each( function( index, element ) {
				var $protein_select_jq = $( this );
				var element_protein_id =  $protein_select_jq.attr("data-protein_id");
				if ( element_protein_id == clickedProteinId ) {
					$protein_select_jq.addClass("single-protein-select-item-highlight");
					//  Scroll to highlighted protein
					try {
						var protein_select_jq_PositionTop = $protein_select_jq.position().top;
						$protein_list_outer_container.scrollTop( protein_select_jq_PositionTop - 20 );
					} catch ( e ) {
					}
	//				return false;  //  Exit .each(...) processing
				}
			} );
		} else {
			//  Position at top for add protein
			$protein_list_outer_container.scrollTop( 0 );
		}
		markInvalidProteinsInProteinSelector( clickedUID );
		//  Position and Set Size of overlay
		var OVERLAY_STANDARD_WIDTH = 400;
		var OVERLAY_MINIMUM_WIDTH = 300;
		var OVERLAY_MINIMUM_HEIGHT = 150;
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

		var listMaximumHeight = $protein_list_container.height() + 5;
		
		//  Set scrollable div height to zero
		$protein_list_outer_container.css( { height : "0px" } );
		
		//   Set overlay height to viewport - 40px or at minimum height
		var overlayHeight = viewportHeight - 40;
		var current_select_protein_overlay_div_Height = $select_protein_overlay_div.outerHeight();
		var overlayHeightDiff = overlayHeight - current_select_protein_overlay_div_Height;

		var protein_list_container_HeightSetToListMaxHeight = false;
		
		var current_protein_list_container_Height = $protein_list_container.height();
		var new_protein_list_container_Height = overlayHeightDiff;
		if ( new_protein_list_container_Height < OVERLAY_MINIMUM_HEIGHT ) {
			new_protein_list_container_Height = OVERLAY_MINIMUM_HEIGHT ;
		}
		if ( new_protein_list_container_Height > listMaximumHeight ) {
			new_protein_list_container_Height = listMaximumHeight ;
			protein_list_container_HeightSetToListMaxHeight = true;
		}

		$protein_list_outer_container.css( { height : new_protein_list_container_Height + "px" } );
		
		//  Position Overlay Vertically
		var overlayNewTop = windowScrollTop + 10;
		if ( protein_list_container_HeightSetToListMaxHeight ) {
			//  overlay height less than full viewport so position top as close to proteins, keeping full overlay within viewport
			var select_protein_overlay_divHeight = $select_protein_overlay_div.outerHeight();
			var selected_proteins_outer_containerTop = $("#selected_proteins_outer_container").position().top;
			var containerTopInViewport = selected_proteins_outer_containerTop - windowScrollTop - 10;
			overlayNewTop = containerTopInViewport;
			if ( overlayNewTop + select_protein_overlay_divHeight > viewportHeight - 40 ) {
				overlayNewTop = windowScrollTop + viewportHeight - select_protein_overlay_divHeight - 20;
			}
			
		}

		//  Apply position to overlay
		$select_protein_overlay_div.css( { 
			left : overlayNewOffsetLeft + "px", 
			top : overlayNewTop + "px",
			width : overlayNewWidth + "px" } );
	}

	/**
	 * Called when a protein listed in the protein selection overlay is clicked on
	 */
	function processClickOnSelectProtein( params ) {
		var clickedThis = params.clickedThis; //  HTML <div> object for clicked protein name
		var $clickedThis = $( clickedThis );
		var $protein_list_container = $("#protein_list_container");
		var protein_list_container_Data = $protein_list_container.data();
		var clickedUID = protein_list_container_Data.clickedUID;
		var clickedProteinId = null;
		if( clickedUID ) {
			clickedProteinId = _indexManager.getProteinIdForUID( clickedUID );
		}
		var addProteinsClicked = protein_list_container_Data.addProteinsClicked;	
		var selectedProteinId = Number( $clickedThis.attr("data-protein_id") );
		if ( selectedProteinId == clickedProteinId ) {
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
			markInvalidProteinsInProteinSelector();
		} else {
			//  Update an existing entry using the selected protein id
			var position = _indexManager.findIndexPosition( clickedUID );
			if( position === -1 ) {
				throw Error( "Could not find position for UID: " + clickedUID );
			}
			_indexManager.removeEntryByUID( clickedUID );
			var newUID = _indexManager.addProteinId( selectedProteinId );
			_indexManager.moveEntryToIndexPosition( newUID, position );
			_imageProteinBarDataManager.removeItemByUID( clickedUID );
			_imageProteinBarDataManager.addEntry( newUID )
			closeAddProteinSelect();
			updateURLHash( false /* useSearchForm */ );
			showSelectedProteins();
			loadDataAndDraw( true /* doDraw */ );
		}
	}

	/**
	 * Called when the Add button of the protein selection overlay is clicked on.
	 */
	function processClickOnAddProteins( ) {
		//  Add one or more new protein bar entries
		var $selectedEntries = $("#protein_list_container ." + _PROTEIN_OVERLAY_PROTEIN_SELECTED );
		$selectedEntries.each( function( index, element ) {
			var $this = $( this );
			var selectedProteinId = $this.attr("data-protein_id");
			var newImageProteinBarData = ImageProteinBarData.constructEmptyImageProteinBarData();
			// add this protein to the index manager
			var uid = _indexManager.addProteinId( selectedProteinId );
			// add an entry for protein bar information
			_imageProteinBarDataManager.addEntry( uid, newImageProteinBarData );
		} );
		closeAddProteinSelect();
		updateURLHash( false /* useSearchForm */ );
		showSelectedProteins();
		loadDataAndDraw( true /* doDraw */ );
	}

	/**
	 * "Grays out" proteins listed in the protein selection overlay if the respective
	 * protein is not cross-linked to any currently-shown protein (minus the one that
	 * was clicked on to open the window) or any of the other selected proteins in the
	 * selection overlay.
	 */
	function markInvalidProteinsInProteinSelector( uid ) {
		//  validProteins contains array of protein ids that the selected protein ids link to, 
		//    excluding the protein id at position positionIndexSelectedProtein ( null or undefined for add )
		var validProteins = getValidProteins( uid );
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
	};

	/**
	 * get an array of "valid" proteins--proteins with crosslinks with
	 * any of the currently-selected proteins
	 */
	function getValidProteins( uid ) {
		///  Producing a list of protein ids that the other selected protein ids link to
		if ( _proteinLinkPositions == undefined ) {
			//  If _proteinLinkPositions == undefined, return all protein ids
			return _proteins;
		}
		var pid = null;						// protein ID for the given uid
		var selectedProteins = null;
		if( uid ) {
			pid = _indexManager.getProteinIdForUID( uid );
		}
		selectedProteins = _indexManager.getProteinList();
		// remove our protein id from this list
		if( pid !== undefined && pid !== null ) {
			selectedProteins = selectedProteins.filter( function( e ) { return e !== pid; } );
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

	/**
	 * 
	 */
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
			throw Error( "direction passed to makeArcPath(...) is invalid.  direction: '" + direction + "'." );
		}
		var path = "M" + startx + " " + starty;
		path += "R" + x1 + " " + y1;
		path += " " + x2 + " " + y2;
		path += " " + x3 + " " + y3;
		path += " " + endx + " " + endy;				
		return path;
	}

	/**
	 * 
	 */
	function downloadSvg( type ) {
		try {
			var getSVGContentsAsStringResult = getSVGContentsAsString();
			var svgString = getSVGContentsAsStringResult.fullSVG_String;
			convertAndDownloadSVG( svgString, type );
			updateURLHash( false /* useSearchForm */ );
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	}

	/**
	 * 
	 */
	function getSVGContentsAsString() {
		try {
			if ( !_GLOBAL_SNAP_SVG_OBJECT ) {
				//  No SVG element found
				return { noPageElement : true };   //  EARLY EXIT
			}
			var fullSVG_String = "<?xml version=\"1.0\" standalone=\"no\"?>\n";
			fullSVG_String += "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">";
			fullSVG_String += _GLOBAL_SNAP_SVG_OBJECT.toString();
			return { fullSVG_String : fullSVG_String};
		} catch( e ) {
			//  Not all browsers have svgElement.innerHTML which .html() tries to use, causing an exception
			return { error : true };
		}
	}

	/**
	 * 
	 */
	function initializeViewer()  {
		showSelectedProteins();
		if ( viewerInitialized ) { 
			return; 
		}
		viewerInitialized = true;
		populateViewerCheckBoxes();
		// set up the slider for custom diameter
		$("#circle_diameter_value").text( _circlePlotViewer.getDiameter() );
		$("#circle_diameter_slider_div").slider({
			value:_circlePlotViewer.getDiameter(),
			min: 500,
			max: 2000,
			step: 100,
			slide: function( event, ui ) {
				$("#circle_diameter_value").text( ui.value );
			},
			change: function( event, ui ) {
				_circlePlotViewer.setUserDiameter( ui.value );
				updateURLHash( false /* useSearchForm */ );
				$("#circle_diameter_value").text( _circlePlotViewer.getDiameter() );
				drawSvg();
			}
			});
		$("#vertical_spacing_value").text( _singleProteinBarOverallHeight );
		$("#vertical_spacing_slider_div").slider({
			value:_singleProteinBarOverallHeight,
			min: 80,
			max: 320,
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
			try {
				updateURLHash( false /* useSearchForm */ );
				loadDataAndDraw( true /* doDraw */ );
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		$( "input#show-self-crosslinks" ).change( function() {
			try {
				updateURLHash( false /* useSearchForm */ );
				loadDataAndDraw( true /* doDraw */ );
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		$( "input#show-looplinks" ).change( function() {
			try {
				updateURLHash( false /* useSearchForm */ );
				loadDataAndDraw( true /* doDraw */ );
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		$( "input#show-monolinks" ).change( function() {
			try {
				updateURLHash( false /* useSearchForm */ );
				loadDataAndDraw( true /* doDraw */ );
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		$( "input#show-linkable-positions" ).change( function() {
			try {
				updateURLHash( false /* useSearchForm */ );
				drawSvg();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		$( "input#show-tryptic-cleavage-positions" ).change( function() {
			try {
				updateURLHash( false /* useSearchForm */ );
				drawSvg();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		$( "input#show-scalebar" ).change( function() {
			try {
				updateURLHash( false /* useSearchForm */ );
				drawSvg();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		$( "input#view-as-circle-plot" ).change( function() {
			try {
				toggleCircleBarView();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		$( "#color_by" ).change( function() {
			try {
				updateURLHash( false /* useSearchForm */ );
				drawSvg();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		$( "input#shade-by-counts" ).change( function() {
			try {
				updateURLHash( false /* useSearchForm */ );
				loadDataAndDraw( true /* doDraw */ );
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		$( "#annotation_type" ).change( function() {
			try {
				updateURLHash( false /* useSearchForm */ );
				loadDataAndDraw( true /* doDraw */ );
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		$( "input#automatic-sizing" ).change( function() {
			try {
				var horizontal_scaling_slider_Value = $("#horizontal_scaling_slider_div").slider( "value" );
				_userScaleFactor = _computedStandardMultiplier * ( horizontal_scaling_slider_Value / 100 );
				handleScaleFactorVisibility();
				updateURLHash( false /* useSearchForm */ );
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		$( "input#scale-factor-button" ).click( function() {
			try {
				submitUserScaleFactor();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		$( "input#protein_names_position_left" ).change( function() {
			try {
				updateURLHash( false /* useSearchForm */ );
				drawSvg();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		$( "input#show-protein-termini" ).change( function() {
			try {
				updateURLHash( false /* useSearchForm */ );
				drawSvg();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		$( "#svg-download-jpeg" ).click( function() { downloadSvg( 'jpeg' ); });
		$( "#svg-download-png" ).click( function() { downloadSvg( 'png' ); });
		$( "#svg-download-pdf" ).click( function() { downloadSvg( 'pdf' ); });
		$( "#svg-download-svg" ).click( function() { downloadSvg( 'svg' ); });
		$("#run_pgm_annotation_data_overlay_cancel_button").click( function() { 
			try {
				proteinAnnotationStore.cancelCheckForComplete();
				decrementSpinner();
				$("#run_pgm_annotation_data_modal_dialog_overlay_background").hide();
				$("#run_pgm_annotation_data_overlay_div").hide();
				$("#annotation_type").val("");
				updateURLHash( false /* useSearchForm */ );
				loadDataAndDraw( true /* doDraw */ );
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		$("#pgm_failed_annotation_data_overlay_annotation_type_button").click( function() { 
			try {
				$("#pgm_failed_annotation_data_modal_dialog_overlay_background").hide();
				$("#pgm_failed_annotation_data_overlay_div").hide();
				loadDataAndDraw( true /* doDraw */ );
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
	};

	/**
	 * update the contextual options (e.g. show bar plot-only options when viewing bar plot)
	 */
	function updateContextOptions() {
		if(  $( "input#view-as-circle-plot" ).is( ':checked' )  ) {
			// turn on circle-plot-only options
			$( ".circle-only" ).show();
			// hide bar options
			$( ".bar-only" ).hide();
		} else {
			// turn on bar-only options
			$( ".bar-only" ).show();
			// hide circle-only options
			$( ".circle-only" ).hide();
		}
	}
	// function called when toggling between circle and bar plot
	function toggleCircleBarView() {	
		updateContextOptions();	
		updateURLHash( false /* useSearchForm */ );
		loadDataAndDraw( true /* doDraw */ );
	}
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

	///////////////////////////////////////////////////////////////////////////
	////////////    Click handlers for the links ( Lines that show the links )
	/**
	 * 
	 */
	function processClickOnLink( clickThis ) {
		var $clickThis = $(clickThis);
		if( $clickThis.attr( 'linktype' ) === "monolink" ) { return processClickOnMonoLink( clickThis ); }
		if( $clickThis.attr( 'linktype' ) === "looplink" ) { return processClickOnLoopLink( clickThis ); }
		if( $clickThis.attr( 'linktype' ) === "crosslink" ) { return processClickOnCrossLink( clickThis ); }
		console.log( "Clicked on a link of unknown type:" );
		console.log( clickThis );
	}

	/**
	 * Process Click on LOOP link
	 */
	function processClickOnLoopLink( clickThis  ) {
		var params = {
				clickThis : clickThis,
				psmPeptideCutoffsRootObject : _psmPeptideCutoffsRootObjectStorage.getPsmPeptideCutoffsRootObject(),
				removeNonUniquePSMs : _removeNonUniquePSMs
		};
		getLooplinkDataForSpecificLinkInGraph( params );
	}

	/**
	 * Process Click on CROSS link
	 */
	function processClickOnCrossLink( clickThis  ) {
		var params = {
				clickThis : clickThis,
				psmPeptideCutoffsRootObject : _psmPeptideCutoffsRootObjectStorage.getPsmPeptideCutoffsRootObject(),
				removeNonUniquePSMs : _removeNonUniquePSMs
		};
		getCrosslinkDataForSpecificLinkInGraph( params );
	}

	/**
	 * Process Click on Mono link
	 */
	function processClickOnMonoLink( clickThis  ) {
		var params = {
				clickThis : clickThis,
				psmPeptideCutoffsRootObject : _psmPeptideCutoffsRootObjectStorage.getPsmPeptideCutoffsRootObject(),
				removeNonUniquePSMs : _removeNonUniquePSMs
		};
		getMonolinkDataForSpecificLinkInGraph( params );
	}

	/////////////////////////////////////
	/**
	 * Get the JSON version number from the raw JSON hash
	 */
	function getJSONVersionNumber() {
		var json = getRawJsonFromHash();
		var version = json.vn;
		if( version ) {
			return version;
		}
		return 0;
	}

	/**
	 * Populates the annotationDataDisplayProcessingCommonCode data from the hash.
	 */
	function populateAnnotationDataDisplayProcessingCommonCodeFromHash() {
		var json = getJsonFromHash();	
		var data = json[ 'annTypeIdDisplay' ];
		if( data ) {
			annotationDataDisplayProcessingCommonCode.putAnnTypeIdDisplayOnThePage( { annTypeIdDisplay : data } );
		} else {
			annotationDataDisplayProcessingCommonCode.putAnnTypeIdDisplayOnThePage( { annTypeIdDisplay : undefined } );
		}	
		//  Get from annotationDataDisplayProcessingCommonCode to reflect defaults if no data
		var annTypeIdDisplay = annotationDataDisplayProcessingCommonCode.getAnnotationTypeDisplayFromThePage({});
		var annTypeIdDisplayByProjectSearchId = annTypeIdDisplay.annTypeIdDisplayByProjectSearchId;
		webserviceDataParamsDistributionCommonCode.paramsForDistribution( { 
			annTypeIdDisplay : annTypeIdDisplayByProjectSearchId
		} );
	}

	//////////////////////////////////////////////////

	/**
	 * Initialize the page and load the data
	 */
	function initPage() {
		console.log( "Initializing the page." );
		// convert old JSON
		console.log( "\tPerforming necessary legacy JSON updates." );
		var _legacyJSONUpdater = new LegacyJSONUpdater();
		if( _legacyJSONUpdater.convertLegacyJSON() ) {
			return;
		}
		console.log( "\tPopulating index manager." );
		populateIndexManagerFromHash();
		populateAnnotationDataDisplayProcessingCommonCodeFromHash();
		proteinAnnotationStore.init();

		if ( Modernizr && ! Modernizr.svg ) {  //  Modernizr.svg is false if SVG not supported
			console.log( "SVG not supported." );
			throw Error( "SVG not supported" );
		}
		_projectSearchIds = [];
		var $project_search_id_jq = $(".project_search_id_jq");
		if ( $project_search_id_jq.length === 0 ) {
			throw Error( "Must be at least one search id in hidden field with class 'project_search_id_jq'" );
		}
		$project_search_id_jq.each( function( index, element ) {
			var $project_search_id_jq_single = $( this );
			var projectSearchIdString = $project_search_id_jq_single.val();
			var projectSearchId = parseInt( projectSearchIdString, 10 );
			if ( isNaN( projectSearchId ) ) {
				throw Error( "Project Search Id is not a number: " + projectSearchIdString );
			}
			_projectSearchIds.push( projectSearchId );
		});
		
		
		_projectSearchIdsUserOrdered = "";
		var $project_search_ids_user_ordered = $("#project_search_ids_user_ordered");
		if ( $project_search_id_jq.length === 0 ) {
			throw Error( "Must be hidden field with id 'project_search_ids_user_ordered'" );
		}
		var project_search_ids_user_orderedString = $project_search_ids_user_ordered.val();
		if ( project_search_ids_user_orderedString.length > 0 ) {
			_projectSearchIdsUserOrdered = project_search_ids_user_orderedString;
		}
		
		var json = getJsonFromHash();
		if ( json === null ) {
			$("#invalid_url_no_data_after_hash_div").show();
			throw Error( "Invalid URL, no data after the hash '#'" );
		}
		if ( _projectSearchIds && _projectSearchIds.length > 1 ) {
			$("#merged_label_text_header").show();  //  Update text at top to show this is for "merged" since more than one search
		}
		attachViewLinkInfoOverlayClickHandlers();
		var proteinBarToolTip_template_handlebarsSource = $( "#protein_bar_tool_tip_template" ).text();
		if ( proteinBarToolTip_template_handlebarsSource === undefined ) {
			throw Error( "proteinBarToolTip_template_handlebarsSource === undefined" );
		}
		if ( proteinBarToolTip_template_handlebarsSource === null ) {
			throw Error( "proteinBarToolTip_template_handlebarsSource === null" );
		}
		_proteinBarToolTip_template_HandlebarsTemplate = Handlebars.compile( proteinBarToolTip_template_handlebarsSource );
		$( "input#filterNonUniquePeptides" ).change(function() {
			try {
				defaultPageView.searchFormChanged_ForDefaultPageView();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		$( "input#filterOnlyOnePeptide" ).change(function() {
			try {
				defaultPageView.searchFormChanged_ForDefaultPageView();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});	
		$( "input#removeNonUniquePSMs" ).change(function() {
			try {
				defaultPageView.searchFormChanged_ForDefaultPageView();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});	
		$("#exclude_protein_types_block").find("input").change(function() {
			try {
				defaultPageView.searchFormChanged_ForDefaultPageView();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		//  select_protein_modal_dialog_overlay_background
		$("#select_protein_overlay_X_for_exit_overlay").click( function( eventObject ) {
			try {
				closeAddProteinSelect();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		} );
		$("#select_protein_add_proteins_overlay_add_button").click( function( eventObject ) {
			try {
				processClickOnAddProteins();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		} );

		$("#custom_region_manager_modal_dialog_overlay_background").click( function( eventObject ) {
			try {
				_customRegionManager.closeManagerOverlay();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		} );
		$("#custom_region_manager_overlay_X_for_exit_overlay").click( function( eventObject ) {
			try {
				_customRegionManager.closeManagerOverlay();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		} );


		_proteinBarRegionSelectionsOverlayCode.init();
		_linkExclusionSelectionsOverlayCode.init();
		loadDataFromService();
	};


	/**
	 * Convert and download the conversion of the supplied SVG to the supplied type
	 * As of this writing, the type must be "pdf", "png", "jpeg", or "svg"
	 */
	var convertAndDownloadSVG = function( svgString, typeString ) {	
		console.log( "convertAndDownloadSVG called." );
		var form = document.createElement( "form" );
		$( form ).hide();
		form.setAttribute( "method", "post" );
		form.setAttribute( "action", "convertAndDownloadSVG.do" );
		//form.setAttribute( "target", "_blank" );
		var svgStringField = document.createElement( "input" );
		svgStringField.setAttribute("name", "svgString");
		svgStringField.setAttribute("value", svgString);
		var fileTypeField = document.createElement( "input" );
		fileTypeField.setAttribute("name", "fileType");
		fileTypeField.setAttribute("value", typeString);
		form.appendChild( svgStringField );
		form.appendChild( fileTypeField );
		document.body.appendChild(form);    // Not entirely sure if this is necessary			
		form.submit();
		document.body.removeChild( form );
	};

}

var imagePagePrimaryRootCodeObject = new ImagePagePrimaryRootCodeClass();

//  Attach to window
window.imagePagePrimaryRootCodeObject_OnWindow = imagePagePrimaryRootCodeObject;

//  Pass imagePagePrimaryRootCodeObject to other JS files that use it
proteinBarRegionSelectionsOverlayCode_pass_imagePagePrimaryRootCodeObject( imagePagePrimaryRootCodeObject );
proteinAnnotationStore_pass_imagePagePrimaryRootCodeObject( imagePagePrimaryRootCodeObject );
ImageProteinBarDataManager_pass_imagePagePrimaryRootCodeObject( imagePagePrimaryRootCodeObject );
LinkExclusionSelectionsOverlayCode_pass_imagePagePrimaryRootCodeObject( imagePagePrimaryRootCodeObject );
LegacyJSONUpdater_pass_imagePagePrimaryRootCodeObject( imagePagePrimaryRootCodeObject );
customRegionManager_pass_imagePagePrimaryRootCodeObject( imagePagePrimaryRootCodeObject );
ColorManager_pass_imagePagePrimaryRootCodeObject( imagePagePrimaryRootCodeObject );
circlePlotViewer_pass_imagePagePrimaryRootCodeObject( imagePagePrimaryRootCodeObject );

///////////
/**
 * Object for passing to other objects
 */
window.imageViewerPageObject = {
	getQueryJSONString : function() {
//			var queryJSON = getNavigationJSON_Not_for_Image_Or_Structure();
		var queryJSON = window.imagePagePrimaryRootCodeObject.call__getJsonFromHash();
		var queryJSONString = JSON.stringify( queryJSON );
		return queryJSONString;
	},
	

	/**
	 * Called from searchesChangeDisplayOrder.js to change order of project search ids in the URL
	 * 
	 * 
	 */
	changeProjectSearchIdOrderInURL : function( params ) {
		var projectSearchIdsInNewOrder = params.projectSearchIdsInNewOrder;
		
		var newProjectSearchIdParamsArray = [];
		
		projectSearchIdsInNewOrder.forEach(function( element, idex, array ) {
			var newProjectSearchIdParam = "projectSearchId=" + element;
			newProjectSearchIdParamsArray.push( newProjectSearchIdParam )
		}, this );
		
		var newProjectSearchIdParamsString = newProjectSearchIdParamsArray.join( "&" );
		
		//  image.do?projectSearchId=
		
		var windowHref = window.location.href;
		
		var windowHash = window.location.hash;
		
		var strutsActionIndex = windowHref.indexOf( "image.do?projectSearchId" );
		
		var windowHrefBeforeStrutsAction = windowHref.substring( 0, strutsActionIndex );
		
		var newWindowHref = windowHrefBeforeStrutsAction + "image.do?" + newProjectSearchIdParamsString + "&ds=y" + windowHash;
		
		window.location.href = newWindowHref;
	}

};


$(document).ready(function()  { 
	try {
		imagePagePrimaryRootCodeObject.call__initPage();
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
});
