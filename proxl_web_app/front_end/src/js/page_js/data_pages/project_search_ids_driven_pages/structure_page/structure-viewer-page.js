"use strict";


//	!!!! Warning !!!!:  
//		the objects in _searches have property ['id'] which is the projectSearchId
//		search.id and _searches[i]['id']  reference the projectSearchId 
//		Some uses of searchId are actually projectSearchId

//   All references to proteinId or protein_id are actually referencing the protein sequence id

	//  Inside the Javascript Class Contructor that will hold all the Javascript code in this file:
	//   Some functions under the "Legacy/Existing"	code that were called from HTML 
	//      and were not called within this code were attached to this_OfOutermostObjectOfClass/this
	//      so they could be called directly from the HTML



//Import header_main.js and children to ensure on the page
import { header_mainVariable } from 'page_js/header_section_js_all_pages_main_pages/header_section_main_pages/header_main.js';


//  Import to make available on the page
import { searchesChangeDisplayOrder } from 'page_js/data_pages/project_search_ids_driven_pages/common/searchesChangeDisplayOrder.js';
import { searchesForPageChooser } from 'page_js/data_pages/project_search_ids_driven_pages/common/searchesForPageChooser.js';
import { sharePageURLShortener  } from 'page_js/data_pages/project_search_ids_driven_pages/common/sharePageURLShortener.js';
import { addSingleTooltipForProteinName } from 'page_js/data_pages/project_search_ids_driven_pages/common/createTooltipForProteinNames.js';

import { defaultPageView } from 'page_js/data_pages/project_search_ids_driven_pages/common/defaultPageView.js';


import { DataPages_LoggedInUser_CommonObjectsFactory } from 'page_js/data_pages/data_pages_common/dataPages_LoggedInUser_CommonObjectsFactory.js';


import { minimumPSM_Count_Filter } from 'page_js/data_pages/project_search_ids_driven_pages/common/minimumPSM_Count_Filter.js';
import { annotationDataDisplayProcessingCommonCode } from 'page_js/data_pages/project_search_ids_driven_pages/common/psmPeptideAnnDisplayDataCommon.js';
import { cutoffProcessingCommonCode } from 'page_js/data_pages/project_search_ids_driven_pages/common/psmPeptideCutoffsCommon.js';

import { webserviceDataParamsDistributionCommonCode } from 'page_js/data_pages/project_search_ids_driven_pages/common/webserviceDataParamsDistribution.js';


import { getLooplinkDataForSpecificLinkInGraph, getCrosslinkDataForSpecificLinkInGraph, getMonolinkDataForSpecificLinkInGraph, structure_viewer_click_element_handlers_pass_structurePagePrimaryRootCodeObject } from './structure-viewer-click-element-handlers.js';
import { LinkColorHandler, LinkColorHandler_pass_structurePagePrimaryRootCodeObject } from './structure-viewer-color-handler.js';
import { attachPDBMapProteinOverlayClickHandlers, attachPDBMapProteinOverlayClickHandlers_pass_structurePagePrimaryRootCodeObject } from './structure-viewer-map-protein.js';
import { attachPDBUploadOverlayClickHandlers, attachPDBFileUploadHandlers, PdbUpload_pass_structurePagePrimaryRootCodeObject } from './structure-viewer-pdb-upload.js';

import { getProteinSequenceVersionIdsForNrseqProteinIds } from 'page_js/data_pages/project_search_ids_driven_pages/image_page__structure_page__shared/nrseqProteinIdToProteinSequenceVersionIdLookup.js';
import { getLooplinkDataCommon, getCrosslinkDataCommon, getMonolinkDataCommon, attachViewLinkInfoOverlayClickHandlers } from 'page_js/data_pages/project_search_ids_driven_pages/image_page__structure_page__shared/image_structure_click_element_common.js';

import { downloadStringAsFile } from 'page_js/data_pages/project_search_ids_driven_pages/common/download-string-as-file.js';

import { LinkExclusionHandler } from './link-exclusion-handler.js';
import {BackboneColorManager} from "./backbone-color-manager.js";
import {StructureWebserviceMethods} from "./structure-webservice-methods.js";
import {StructureUtils} from "./stucture-utils.js";
import {StructureAlignmentUtils} from "./structure-alignment-utils.js";
import {DensityPlot} from "./density-plot.js";
import {LinkablePositionDataManager} from "./linkable-position-data-manager";
import {PValueUtils} from "./p-value-utils";

/////////////////////////////

//   Javascript Class Contructor that will hold all the Javascript code in this file 
// 	   to remove all the Javascript code in this file from the global scope.



//  Called from popout window

window.popinViewer = function() {
	window.structurePagePrimaryRootCodeObject.call__popinViewer();
}

window.drawStructureAfterResize = function() {
	window.structurePagePrimaryRootCodeObject.call__drawStructureAfterResize();
}




window.refreshData = function() {
	window.structurePagePrimaryRootCodeObject.call__refreshData();
}


window.popoutViewer = function() {
	window.structurePagePrimaryRootCodeObject.call__popoutViewer();
}

window.closePopout = function() {
	window.structurePagePrimaryRootCodeObject.call__closePopout();
}





window.showDistanceReportPanel = function() {
	window.structurePagePrimaryRootCodeObject.call__showDistanceReportPanel();
}
window.showChainMapPanel = function() {
	window.structurePagePrimaryRootCodeObject.call__showChainMapPanel();
}
window.userChangeDistanceConstraintsInterface = function() {
	window.structurePagePrimaryRootCodeObject.call__userChangeDistanceConstraintsInterface();
}
window.removeUserDistanceConstraints = function() {
	window.structurePagePrimaryRootCodeObject.call__removeUserDistanceConstraints();
}
window.clearUserColorByLength = function() {
	window.structurePagePrimaryRootCodeObject.call__clearUserColorByLength();
}
window.updateUserDistanceConstraints = function() {
	window.structurePagePrimaryRootCodeObject.call__updateUserDistanceConstraints();
}
window.drawLegend = function() {
	window.structurePagePrimaryRootCodeObject.call__drawLegend();
}
window.clearUserColorByLinkType = function() {
	window.structurePagePrimaryRootCodeObject.call__clearUserColorByLinkType();
}
window.clearUserColorBySearch = function() {
	window.structurePagePrimaryRootCodeObject.call__clearUserColorBySearch();
}

window.deleteAlignment = function( clickThis, alignmentId ) {
	window.structurePagePrimaryRootCodeObject.call__deleteAlignment( clickThis, alignmentId );
}

window.proteinClicked = function() {
	window.structurePagePrimaryRootCodeObject.call__proteinClicked();
}

/////////////////////////////

//   Javascript Class Contructor that will hold all the Javascript code in this file 
// 	   to remove all the Javascript code in this file from the global scope.

var StructurePagePrimaryRootCodeClass = function() {

	var this_OfOutermostObjectOfClass = this;

	//   Some functions under the "Legacy/Existing"	code that were called from HTML 
	//      and were not called within this code were attached to this_OfOutermostObjectOfClass/this
	//      so they could be called directly from the HTML

	////////////////////////////////

	//  functions attached to 'this' to call functions inside this constructor

	/**
	 * Called from popout window
	 */
	this_OfOutermostObjectOfClass.call__popinViewer = function() {
		popinViewer();
	}
	
	/**
	 * Called from popout window
	 */
	this_OfOutermostObjectOfClass.call__drawStructureAfterResize = function() {
		drawStructureAfterResize();
	}

	/**
	 * Initialize the page and load the data
	 */
	this_OfOutermostObjectOfClass.call__initPage = function() {
		return initPage();
	}

	/**
	 * Called from button "Update From Database" on the page
	 *   Refresh the data on the page
	 */
	this_OfOutermostObjectOfClass.call__refreshData = function() {
		refreshData();
	}

	//  These are all called from the JSP (and some also from HTML in this JS)
	
	this_OfOutermostObjectOfClass.call__popoutViewer = function() {
		popoutViewer();
	}
	this_OfOutermostObjectOfClass.call__closePopout = function() {
		closePopout();
	}
	
	
	
	this_OfOutermostObjectOfClass.call__showDistanceReportPanel = function() {
		showDistanceReportPanel();
	}
	this_OfOutermostObjectOfClass.call__showChainMapPanel = function() {
		showChainMapPanel();
	}
	this_OfOutermostObjectOfClass.call__userChangeDistanceConstraintsInterface = function() {
		userChangeDistanceConstraintsInterface();
	}
	this_OfOutermostObjectOfClass.call__removeUserDistanceConstraints = function() {
		removeUserDistanceConstraints();
	}
	this_OfOutermostObjectOfClass.call__clearUserColorByLength = function() {
		clearUserColorByLength();
	}
	this_OfOutermostObjectOfClass.call__updateUserDistanceConstraints = function() {
		updateUserDistanceConstraints();
	}
	this_OfOutermostObjectOfClass.call__drawLegend = function() {
		drawLegend();
	}
	this_OfOutermostObjectOfClass.call__clearUserColorByLinkType = function() {
		clearUserColorByLinkType();
	}
	this_OfOutermostObjectOfClass.call__clearUserColorBySearch = function() {
		clearUserColorBySearch();
	}

	this_OfOutermostObjectOfClass.call__deleteAlignment = function( clickThis, alignmentId ) {
		deleteAlignment( clickThis, alignmentId );
	}

	this_OfOutermostObjectOfClass.call__proteinClicked = function() {
		proteinClicked();
	}


	//  Called from JS in this file outside this class

	/**
	 * get values for variables from the hash part of the URL as JSON
	 */
	this_OfOutermostObjectOfClass.call__getJsonFromHash = function() {
		return getJsonFromHash();
	}

	////////

	//  Called from other JS files on this page

	/**
	 * 
	 */
	this_OfOutermostObjectOfClass.call__getLinkColorMode = function() {
		return getLinkColorMode();
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
	this_OfOutermostObjectOfClass.call__findSearchesForLooplink = function( protein, position1, position2 ) {
		return findSearchesForLooplink( protein, position1, position2 );
	}

	/**
	 * returns a list of searches for the given link
	 */
	this_OfOutermostObjectOfClass.call__findSearchesForMonolink = function( protein, position ) {
		return findSearchesForMonolink( protein, position );
	}

	/**
	 * 
	 */
	this_OfOutermostObjectOfClass.call__getLinkerStringsAsArray = function() {
		return getLinkerStringsAsArray();
	}

	/**
	 * 
	 */
	this_OfOutermostObjectOfClass.call__getAllAlignmentsForChain = function( chainId ) {
		return getAllAlignmentsForChain( chainId );
	}

	/**
	 * get the currently selected pdb file
	 */
	this_OfOutermostObjectOfClass.call__getSelectedPDBFile = function() {
		return getSelectedPDBFile();
	}

	/**
	 * 
	 */
	this_OfOutermostObjectOfClass.call__loadPDBFileAlignments = function( callback, doDraw ) {
		return loadPDBFileAlignments( callback, doDraw );
	}

	/**
	 * populate the PDB files
	 */
	this_OfOutermostObjectOfClass.call__loadPDBFiles = function( defaultId, doDraw ) {
		return loadPDBFiles( defaultId, doDraw );
	}
		
	////////////////////////////////

	//  functions attached to 'this' to return 'global' constants inside this constructor



	////////////////////////////////

	//  functions attached to 'this' to SET 'global' variables inside this constructor


	////////////////////////////////

	//  functions attached to 'this' to return 'global' variables inside this constructor


	this_OfOutermostObjectOfClass.getVariable__v_proteins = function() {
		return _proteins;
	}

	this_OfOutermostObjectOfClass.getVariable__v_proteinNames = function() {
		return _proteinNames;
	}

	this_OfOutermostObjectOfClass.getVariable__v_searches = function() {
		return _searches;
	}

	this_OfOutermostObjectOfClass.getVariable__v_linkColorHandler = function() {
		return _linkColorHandler;
	}

	this_OfOutermostObjectOfClass.getVariable__v_PDB_FILES = function() {
		return _PDB_FILES;
	}

	this_OfOutermostObjectOfClass.getVariable__v_listChains = function() {
		return listChains;
	}

	this_OfOutermostObjectOfClass.getVariable__v_STRUCTURE = function() {
		return _STRUCTURE;
	}

	////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////

	//   Start of Legacy code in this file



	///////////////   CONSTANTS  ////////////////////////

	//   Property keys in the JSON on the Hash of the URL

	//  Current property_keys

	var HASH_PROPERTY_VISIBLE_CHAINS_WITH_PROTEIN_SEQUENCE_IDS = "visible_chains_w_prot_seq_ids";


	//  Prev property_keys - no longer used, used for conversions

	var HASH_PROPERTY_PREV__VISIBLE_CHAINS = "visible-chains";




	//  Default exclude link type "No Links"

	var EXCLUDE_LINK_TYPE_DEFAULT = [ 0 ];

	//  Link Length constants

	var LINK_LENGTH_COLOR_BLOCK__DATA__SAVED_BACKGROUND_COLOR = "SAVED_BACKGROUND_COLOR";
	var LINK_LENGTH_COLOR_BLOCK__DATA__COLOR_BLOCK_LENGTH_LABEL = "COLOR_BLOCK_LENGTH_LABEL";

	var LINK_LENGTH_INTERNAL_STRING_SHORT = "SHORT";
	var LINK_LENGTH_INTERNAL_STRING_MEDIUM = "MEDIUM";
	var LINK_LENGTH_INTERNAL_STRING_LONG = "LONG";

	//  Link Type constants

	var LINK_TYPE_COLOR_BLOCK__DATA__SAVED_BACKGROUND_COLOR = "SAVED_BACKGROUND_COLOR";
	var LINK_TYPE_COLOR_BLOCK__DATA__COLOR_BLOCK_TYPE_LABEL = "COLOR_BLOCK_TYPE_LABEL";

	var LINK_TYPE_INTERNAL_STRING_CROSSLINK = "CROSSLINK";
	var LINK_TYPE_INTERNAL_STRING_LOOPLINK = "LOOPLINK";
	var LINK_TYPE_INTERNAL_STRING_MONOLINK = "MONOLINK";


	//  Color By Search constants

	var COLOR_BY_SEARCH_COLOR_BLOCK__DATA__SAVED_BACKGROUND_COLOR = "SAVED_BACKGROUND_COLOR";
	var COLOR_BY_SEARCH_COLOR_BLOCK__DATA__SEARCH_IDS = "SEARCH_IDS";


	///////////////   Global variables  ////////////////////////

	//  From Page


	var _projectSearchIds;
	var _projectSearchIdsUserOrdered = "";

	// object to handle all link color determination duties
	var _linkColorHandler = new LinkColorHandler();

	// object to handle link exclusions
	var _linkExclusionHandler = new LinkExclusionHandler();

	// object to handle link exclusions
	var _linkablePositionDataManager = new LinkablePositionDataManager();

	// object to handle chain colors
	var _backboneColorManager = new BackboneColorManager();

	const dataPages_LoggedInUser_CommonObjectsFactory = new DataPages_LoggedInUser_CommonObjectsFactory();
	const saveView_dataPages = dataPages_LoggedInUser_CommonObjectsFactory.instantiate_SaveView_dataPages();

	//Loaded data:

	var _proteins;
	var _proteinSequences = { };
	var _proteinNames;
	var _proteinLinkPositions;
	var _proteinLooplinkPositions;
	var _proteinMonolinkPositions;
	var _linkablePositions;

	// an object with the keys: 'crosslink', 'looplink', 'monolink' 
	var _linkPSMCounts = { };

	// an object with keys: 'crosslinks', 'looplinks', 'monolinks', each linking to an array of objects with keys: 'atom1', 'atom2' ('atom2' not present for monolinks)
	// used to generate pymol and chimera scripts
	var _renderedLinks = { };

	var _searches;

	var _projectSearchIdToSearchIdMapping;

	var _taxonomies;
	var _lysineLocations;
	var _coverages;
	var _ranges;


	//From JSON (probably round trips from the input fields to the JSON in the Hash in the URL to these variables)

	var _psmPeptideCutoffsRootObjectStorage = {
		
		_psmPeptideCutoffsRootObject : null,
		
		setPsmPeptideCutoffsRootObject : function( psmPeptideCutoffsRootObject ) {
			try {
				this._psmPeptideCutoffsRootObject = psmPeptideCutoffsRootObject;
				//  Distribute the updated value to the JS code that loads and displays Peptide and PSM data
				webserviceDataParamsDistributionCommonCode.paramsForDistribution( { cutoffs : psmPeptideCutoffsRootObject } );
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
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

	var _distanceReportData = { };


	/////////////////////////////////////////////////////////



	//   Convert OLD JSON if necessary

	//   Will immediately return after converting JSON or will call initPage() again after converting JSON

	//   Unable to return immediately if need to use webservice to get data from server.

	function convertOldJSONIfNecessaryReturnTrueIfExit() {

		var json = getJsonFromHash();

		if ( convertVisibleChainsToproteinSequenceVersionIds( json ) ) {
			
			return true;  // force main code to wait for async
		}


	}

	/////

	function convertVisibleChainsToproteinSequenceVersionIds( json ) {
		
		if ( ! ( HASH_PROPERTY_PREV__VISIBLE_CHAINS  in json ) ) {
			
			return false;
		}
		

		console.log( "Converting nrseq protein ids to protein sequence ids: " );

		
		var nrseqProteinIds = [];
		
		var visibleChains = json[ HASH_PROPERTY_PREV__VISIBLE_CHAINS ];

		var visibleChains_Keys = Object.keys( visibleChains );
		
		for ( var keysIndex = 0; keysIndex < visibleChains_Keys.length; keysIndex++ ) {
			
			var visibleChains_Key = visibleChains_Keys[ keysIndex ];
			
			var visibleChainNRSEQProteinIdsArray = visibleChains[ visibleChains_Key ];
			
			for ( var visibleChainNRSEQProteinIdsArrayIndex = 0; visibleChainNRSEQProteinIdsArrayIndex < visibleChainNRSEQProteinIdsArray.length; visibleChainNRSEQProteinIdsArrayIndex++ ) {
				
				var visibleChainNRSEQProteinIdEntry = visibleChainNRSEQProteinIdsArray[ visibleChainNRSEQProteinIdsArrayIndex ];
				
				nrseqProteinIds.push( visibleChainNRSEQProteinIdEntry );
			}
		}	
		
		var callback = function( params ) {
			
			convertVisibleChainsToproteinSequenceVersionIdsProcessReponse( params );
		};
		
		
		
		var params = {
				
				nrseqProteinIds : nrseqProteinIds,
				callback : callback
		};
		
		var response = getProteinSequenceVersionIdsForNrseqProteinIds( params );
		
		if ( response.calledAJAX ) {
		
			return true;
		}
		
		return false;
	}

	/////

	function convertVisibleChainsToproteinSequenceVersionIdsProcessReponse( responseParams ) {

		var proteinIdsMapping = responseParams.proteinIdsMapping;
		var calledAJAX = responseParams.calledAJAX;
		

		var json = getJsonFromHash();

		if ( ! ( HASH_PROPERTY_PREV__VISIBLE_CHAINS  in json ) ) {
			
			return false;
		}
		

		var visibleChains = json[ HASH_PROPERTY_PREV__VISIBLE_CHAINS ];

		var visibleChains_Keys = Object.keys( visibleChains );
		
		for ( var keysIndex = 0; keysIndex < visibleChains_Keys.length; keysIndex++ ) {
			
			var visibleChains_Key = visibleChains_Keys[ keysIndex ];
			
			var visibleChainNRSEQProteinIdsArray = visibleChains[ visibleChains_Key ];
			
			for ( var visibleChainNRSEQProteinIdsArrayIndex = 0; visibleChainNRSEQProteinIdsArrayIndex < visibleChainNRSEQProteinIdsArray.length; visibleChainNRSEQProteinIdsArrayIndex++ ) {
				
				var visibleChainNRSEQProteinIdEntry = visibleChainNRSEQProteinIdsArray[ visibleChainNRSEQProteinIdsArrayIndex ];
				
				var matchingproteinSequenceVersionId = proteinIdsMapping[ visibleChainNRSEQProteinIdEntry ];
				
				if ( matchingproteinSequenceVersionId === undefined || matchingproteinSequenceVersionId === null ) {
					
					throw Error( "Matching protein sequence id not found for nrseq protein id: " + visibleChainNRSEQProteinIdEntry );
				}
				
				visibleChainNRSEQProteinIdsArray[ visibleChainNRSEQProteinIdsArrayIndex ] = matchingproteinSequenceVersionId;
			}
		}	

		//  Copy to new property name and remove old property name.
		
		json[ HASH_PROPERTY_VISIBLE_CHAINS_WITH_PROTEIN_SEQUENCE_IDS ] = json[ HASH_PROPERTY_PREV__VISIBLE_CHAINS ];
		
		delete json[ HASH_PROPERTY_PREV__VISIBLE_CHAINS ]; //  Remove old property
		
		//  Update Hash with updated JSON
		
		updateURLHashWithJSONObject( json );
		

		if ( calledAJAX ) {

			//   Re call initPage() since on AJAX callback thread;

			initPage();
		}
	}



	////////////////////////////////////////////

	function populateProjectSearchIdToSearchIdMapping() {
		_projectSearchIdToSearchIdMapping = {};
		for ( var index = 0; index < _searches.length; index++ ) {
			var search = _searches[ index ];
			_projectSearchIdToSearchIdMapping[ search.id ] = search.searchId;
		}
	}

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


	////////////////////////////////////////



	//  get values for variables from the hash part of the URL as JSON

	function getJsonFromHash() {
		
		var jsonFromHash = null;

		var windowHash = window.location.hash;
		
		if ( windowHash === "" || windowHash === "#" ) {
			
			//  Set cutoff defaults if not in JSON
				
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

			// if we got here, the hash contained URI-encoded JSON, try decoding using decodeURI( windowHashContentsMinusHashChar )
			
			var windowHashContentsMinusHashCharDecodeURI = decodeURI( windowHashContentsMinusHashChar );
			
			try {
				jsonFromHash = JSON.parse( windowHashContentsMinusHashCharDecodeURI );
			} catch( e2 ) {

				jsonFromHash = undefined;
			}
		}

		if ( jsonFromHash === null || jsonFromHash === undefined ) {

			// if we got here, the hash contained URI-encoded JSON, try decoding using decodeURIComponent( windowHashContentsMinusHashChar )

			var windowHashContentsMinusHashCharDecodeURIComponent = decodeURIComponent( windowHashContentsMinusHashChar );

			try {
				jsonFromHash = JSON.parse( windowHashContentsMinusHashCharDecodeURIComponent );
			} catch( e3 ) {

				jsonFromHash = undefined;
			}
		}

		if ( jsonFromHash === null || jsonFromHash === undefined ) {

			throw Error( "Failed to parse window hash string as JSON and decodeURI and then parse as JSON.  windowHashContentsMinusHashChar: " 
			+ windowHashContentsMinusHashChar );
		}

		//   Transform json on hash to expected object for rest of the code
		
		var json = jsonFromHash;
		
		
		if ( json.cutoffs === undefined || json.cutoffs === null ) {

			//  Set cutoff defaults if not in JSON
			
			json.cutoffs = getCutoffDefaultsFromPage();
		}
		
		if ( json.filterOnlyOnePSM ) {
			json.minPSMs = 2;
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


	////

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




	function populateSearchForm() {
		
		cutoffProcessingCommonCode.putCutoffsOnThePage(  { cutoffs : _psmPeptideCutoffsRootObjectStorage.getPsmPeptideCutoffsRootObject() } );
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
			
			defaultPageView.searchFormChanged_ForDefaultPageView();
			if ( window.saveView_dataPages ) {
				window.saveView_dataPages.searchFormChanged_ForSaveView(); 
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

	function getValuesFromForm() {

		var items = { };


		var getCutoffsFromThePageResult = cutoffProcessingCommonCode.getCutoffsFromThePage(  {  } );
		
		var getCutoffsFromThePageResult_FieldDataFailedValidation = getCutoffsFromThePageResult.getCutoffsFromThePageResult_FieldDataFailedValidation;
		
		if ( getCutoffsFromThePageResult_FieldDataFailedValidation ) {
			
			//  Cutoffs failed validation and error message was displayed
			
			//  EARLY EXIT from function
			
	//		return { output_FieldDataFailedValidation : getCutoffsFromThePageResult_FieldDataFailedValidation };
			
			throw Error( "Cutoffs are invalid so stop processing" );
		}
		
		var outputCutoffs = getCutoffsFromThePageResult.cutoffsByProjectSearchId;
		
		items[ 'cutoffs' ] = outputCutoffs;
		
		items[ 'minPSMs' ] = minimumPSM_Count_Filter.getMinPSMsFilter();

		if ( $( "input#filterNonUniquePeptides" ).is( ':checked' ) )
			items[ 'filterNonUniquePeptides' ] = true;
		else
			items[ 'filterNonUniquePeptides' ] = false;

		if ( $( "input#filterOnlyOnePeptide" ).is( ':checked' ) )
			items[ 'filterOnlyOnePeptide' ] = true;
		else
			items[ 'filterOnlyOnePeptide' ] = false;
		
		if ( $( "input#removeNonUniquePSMs" ).is( ':checked' ) )
			items[ 'removeNonUniquePSMs' ] = true;
		else
			items[ 'removeNonUniquePSMs' ] = false;
		
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
	function updateURLHash( useSearchForm) {

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
			items[ 'minPSMs' ] = _minPSMs;

	//		add filter out non unique peptides
			items[ 'filterOnlyOnePeptide' ] = _filterOnlyOnePeptide;

	//		add filter out non unique peptides
			items[ 'removeNonUniquePSMs' ] = _removeNonUniquePSMs;

		} else {

	//		build hash string from values in form, they've requested a data refresh

			var formValues = getValuesFromForm();

			if ( formValues === null ) {

				return null;  //  EARLY EXIT
			}

	//		nothing in items yet so just copy

			items = formValues;

		}
		
		//  Output the selected Annotation data for display
		var getAnnotationTypeDisplayFromThePageResult = annotationDataDisplayProcessingCommonCode.getAnnotationTypeDisplayFromThePage( {} );
		var annTypeIdDisplay = getAnnotationTypeDisplayFromThePageResult.annTypeIdDisplayByProjectSearchId;

		items[ 'annTypeIdDisplay' ] = annTypeIdDisplay;

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
		if ( $( "input#show-linkable-positions" ).is( ':checked' ) ) {
			items[ 'show-linkable-positions' ] = true;
		} else {
			items[ 'show-linkable-positions' ] = false;
		}
		if ( $( "input#show-coverage" ).is( ':checked' ) ) {
			items[ 'show-coverage' ] = true;
		} else {
			items[ 'show-coverage' ] = false;
		}
		if ( $( "input#shade-by-counts" ).is( ':checked' ) ) {
			items[ 'shade-by-counts' ] = true;
		} else {
			items[ 'shade-by-counts' ] = false;
		}
		
		
		// add selected PDB file
		var pdbFile = getSelectedPDBFile();
		
		if ( ! isNaN( pdbFile.id ) ) {
			items[ 'pdb-file-id' ] = pdbFile.id;
		}
		
		// add the selected proteins/chains
		var visibleChains = getVisibleChains();
		
		if( visibleChains ) {
			items[ HASH_PROPERTY_VISIBLE_CHAINS_WITH_PROTEIN_SEQUENCE_IDS ] = visibleChains;
		}
		
		items[ 'render-mode' ] = getRenderMode();
		items[ 'link-color-mode' ] = getLinkColorMode();
		items[ 'show-unique-udrs' ] = getShowUniqueUDRs();
		
		var $distanceCutoffReportField = $( '#distance-cutoff-report-field' );
		if( $distanceCutoffReportField ) {
			var cutoff = $distanceCutoffReportField.val();
			if( !isNaN(cutoff) ) {
				items[ 'distance-report-cutoff' ] = cutoff;
			}
		}
		
		if( isDistanceReportVisible() ) {
			items[ 'distance-report-visible' ] = true;
		}
		
		// include user-defined coloring of distance constraints based on search
		items[ 'ucbs' ] = _linkColorHandler.getUserColorBySearch();
		
		// include user-defined coloring of distance constraints based on link type
		items[ 'ucbt' ] = _linkColorHandler.getUserColorByType();
		
		// include user-defined coloring of distance constraints based on length
		items[ 'ucbl' ] = _linkColorHandler.getUserColorByLength();
		
		// include user-defined distance constraints for coloring
		var userDistanceConstraints = _linkColorHandler.getUserDistanceConstraints();
		if( userDistanceConstraints ) {
			items[ 'udcs' ] = userDistanceConstraints.shortDistance;
			items[ 'udcl' ] = userDistanceConstraints.longDistance;
		}

		items[ 'le' ] = _linkExclusionHandler.getDataStructureForHash();
		items[ 'bc' ] = _backboneColorManager.getDataStructureForHash();

		updateURLHashWithJSONObject( items );
	}



	function updateURLHashWithJSONObject( jsonObject ) {
		

		var newHash = JSON.stringify( jsonObject );

	//	var newHashLength = newHash.length;
		
		var newHashEncodedToEncodedURIComponent = LZString.compressToEncodedURIComponent( newHash );
		

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
		
		if ( json.filterNonUniquePeptides != undefined && json.filterNonUniquePeptides ) {
			items.push( "filterNonUniquePeptides=on" );
		}
		if ( json.filterOnlyOnePeptide != undefined && json.filterOnlyOnePeptide ) {
			items.push( "filterOnlyOnePeptide=on" );
		}
		if ( json.removeNonUniquePSMs != undefined && json.removeNonUniquePSMs ) {
			items.push( "removeNonUniquePSMs=on" );
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
		
		queryString += items.join( "&" );
		
		return queryString;
	}

	/////////////////////

	//Called from button "Update From Database" on the page

	///   Refresh the data on the page

	function refreshData() {

		updateURLHash( true /* useSearchForm */ );
			
		defaultPageView.searchFormUpdateButtonPressed_ForDefaultPageView();
		saveView_dataPages.searchFormUpdateButtonPressed_ForSaveView(); 
		
		loadDataFromService();
	}


	///////////////////

	//Load protein sequence coverage data for a specific protein
	function loadSequenceCoverageDataForProtein( protein, loadRequest, callout ) {
		
		console.log( "Loading sequence coverage data for protein: " + protein );
				
			if ( _ranges == undefined || _ranges[ protein ] == undefined ) {
				
				incrementSpinner();				// create spinner
				
				var url = "services/sequenceCoverage/getDataForProtein";
				url += buildQueryStringFromHash();
				url += "&proteinSequenceVersionId=" + protein;
				
				$.ajax({
						type: "GET",
						url: url,
						dataType: "json",
						success: function(data)	{
						
							try {
							
								decrementSpinner();

								if ( _ranges == undefined ) {
									_coverages = data.coverages;
									_ranges = data.ranges;
								} else {
									_coverages[ protein ] = data[ 'coverages' ][ protein ];
									_ranges[ protein ] = data[ 'ranges' ][ protein ];
								}

								if( loadRequest ) { loadRequest.statusMap[ protein ] = 1; }
								if( callout ) { callout(); }
								
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
	}


	//Toggle the visibility of crosslink data on the viewer
	function loadCrosslinkData( doDraw ) {
		
		console.log( "Loading crosslink data." );
		//_proteinLinkPositions = _TEST_CROSSLINK_DATA.proteinLinkPositions;
		//console.log( _proteinLinkPositions );
		//return;
		
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

								if( doDraw ) {
									drawMeshesOnStructure( drawCrosslinks);
								}
								
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

	this_OfOutermostObjectOfClass.downloadPSMsForAllShownUDRLinks = function() {
		
		var requestObject = { };
		requestObject.projectSearchIds = _projectSearchIds;
		
		if( !_renderedLinks ) { return; }
		
		if( _renderedLinks['crosslinks'] && _renderedLinks['crosslinks'].length > 0 ) {
			
			requestObject.crosslinkUdrRequestList = [  ];
			
			for( var i = 0; i < _renderedLinks[ 'crosslinks' ].length; i++ ) {
				var link = _renderedLinks[ 'crosslinks' ][ i ][ 'link' ];

				if( _linkExclusionHandler.isLinkExcluded( link ) ) {
					continue;
				}
		
				var dataObj = { };
				dataObj.protId1 = link.protein1;
				dataObj.protId2 = link.protein2;
				dataObj.pos1 = link.position1;
				dataObj.pos2 = link.position2;
				
				requestObject.crosslinkUdrRequestList.push( dataObj );
			}
		}

		if( _renderedLinks['looplinks'] && _renderedLinks['looplinks'].length > 0 ) {
			
			requestObject.looplinkUdrRequestList = [  ];
			
			for( var i = 0; i < _renderedLinks[ 'looplinks' ].length; i++ ) {
				var link = _renderedLinks[ 'looplinks' ][ i ][ 'link' ];

				if( _linkExclusionHandler.isLinkExcluded( link ) ) {
					continue;
				}
		
				var dataObj = { };
				dataObj.protId1 = link.protein1;
				dataObj.pos1 = link.position1;
				dataObj.pos2 = link.position2;
				
				requestObject.looplinkUdrRequestList.push( dataObj );
			}
		}
		
		loadPSMUDRData( requestObject );
		
	}

	function loadPSMUDRData( requestObject ) {
		
		console.log( "Loading PSM UDR data." );
		
				incrementSpinner();				// create spinner
				
				var url = "services/imageViewer/getMultPsmUDRData";
				//url += buildQueryStringFromHash();
				
				var requestData = JSON.stringify( requestObject );
				
				$.ajax({
					type : "POST",
					url : url,
					data : requestData,
					contentType: "application/json; charset=utf-8",
					dataType : "json",
					success : function(data) {
							
							try {
						
								decrementSpinner();
								collateAndDownloadPSMUDRData( data );
								
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
	 * Assemble and download a report of the PDB mapping of the currently shown chains and proteins
	 */
	this_OfOutermostObjectOfClass.downloadProteinToPDBMap = function() {
		
		var reportText = "";
		
		reportText += "protein\tposition\tresidue\tPDB chain\tPDB chain position\tPDB residue\n";
		
		var visibleProteinsMap = getVisibleProteins();
			
		if( !visibleProteinsMap || visibleProteinsMap == undefined || visibleProteinsMap.length < 1 ) { return; }
		var visibleProteins = Object.keys( visibleProteinsMap );
		
		for( var i = 0; i < visibleProteins.length; i++ ) {
			var proteinId = visibleProteins[ i ];
			var chains = visibleProteinsMap[ proteinId ];
			
			for( var k = 0; k < chains.length; k++ ) {
				
				var chain = chains[ k ];

				var alignment = StructureAlignmentUtils.getAlignmentByChainAndProtein( chain, proteinId, _ALIGNMENTS );
				
				var expPosition = 0;
				var pdbPosition = 0;
				
				for( var h = 0; h < alignment.alignedExperimentalSequence.length; h++ ) {

					if( alignment.alignedExperimentalSequence[ h ] != '-' ) { expPosition++; }
					if( alignment.alignedPDBSequence[ h ] != '-' ) { pdbPosition++; }
					
					if( alignment.alignedExperimentalSequence[ h ] !== '-' ) {
						
						reportText += _proteinNames[ proteinId ] + "\t";
						reportText += expPosition + "\t";
						reportText += alignment.alignedExperimentalSequence[ h ] + "\t";
						
						var atoms = StructureAlignmentUtils.findCAAtoms( proteinId, expPosition, [ chain ], _ALIGNMENTS, _STRUCTURE );
						
						if( !atoms || atoms.length < 1 ) {
							reportText += "\t\t\n";
						} else {
						
							var atom = atoms[ 0 ];					
							var residue = atom.residue();
							var num = residue.num();		// pdb number for this residue in this chain
						
							var chain = residue.chain().name();
						
							reportText += chain + "\t";
							reportText += num + "\t";
							reportText += alignment.alignedPDBSequence[ h ] + "\n";
						}
					}
					
					
				}

			}
		}
		
		downloadStringAsFile( "protein-to-pdb-map.txt", "text/plain", reportText );
	}

	function collateAndDownloadPSMUDRData( data ) {
		
		var reportText = "";
		
		if( !data || !data.dataForSearches ) {
			throw Error( "Did not get data." );
		}

		for( var i = 0; i < _projectSearchIds.length; i++ ) {
			
			var projectSearchId = _projectSearchIds[ i ];
			var searchId = _projectSearchIdToSearchIdMapping[ projectSearchId ];
			if ( searchId === undefined ) {
				throw Error( "collateAndDownloadPSMUDRData( data ): Failed to find searchId mapping for projectSearchId: " + projectSearchId );
			}

			var searchData = data.dataForSearches[ searchId ];
			if( ! searchData ) {
				console.log( "WARNING: GOT NO PSM UDR DATA FOR SEARCH: " + searchId );
				continue;
			}
			
			// pad each searches section with some white space
			if( i != 0 ) {
				reportText += "\n\n";
			}
			
			reportText += "search id\tlink type\tprotein 1\tposition 1\tprotein 2\tposition 2\tdistance";
			
			for( var k = 0; k < searchData.psmValuesNames.length; k++ ) {
				reportText += "\t";
				reportText += searchData.psmValuesNames[ k ];
			}
			
			reportText += "\n";
			
			if( searchData.crosslinkUdrItemList && searchData.crosslinkUdrItemList.length > 0 ) {
				
				for( var k = 0; k < searchData.crosslinkUdrItemList.length; k++ ) {
					
					var udrItem = searchData.crosslinkUdrItemList[ k ];
					
					var protein1 = udrItem.protId1;
					var protein2 = udrItem.protId2;
					var position1 = udrItem.pos1;
					var position2 = udrItem.pos2;
					
					var link = getRenderedLink( "crosslink", protein1, position1, protein2, position2 );
					if( !link ) {
						reportWebErrorToServer.reportErrorObjectToServer( Error( "Did not get rendered link for UDR..." ));
						throw e;
					}
					
					var distance = link.length;
					
					for( var j = 0; j < udrItem.psmItemList.length; j++ ) {
						var psmItem = udrItem.psmItemList[ j ];
						
						reportText += searchId + "\t";
						reportText += "crosslink\t";
						reportText += _proteinNames[ protein1 ] + "\t";
						reportText += position1 + "\t";
						reportText += _proteinNames[ protein2 ] + "\t";
						reportText += position2 + "\t";
						reportText += distance + "\t";
						
						for( var m = 0; m < psmItem.psmValues.length; m++ ) {
							if( m != 0 ) { reportText += "\t"; }
							
							reportText += psmItem.psmValues[ m ];
						}
						
						reportText += "\n";										
					}
				}
			}
			
			
			if( searchData.looplinkUdrItemList && searchData.looplinkUdrItemList.length > 0 ) {
				
				for( var k = 0; k < searchData.looplinkUdrItemList.length; k++ ) {
					
					var udrItem = searchData.looplinkUdrItemList[ k ];
					
					var protein1 = udrItem.protId1;
					var position1 = udrItem.pos1;
					var position2 = udrItem.pos2;
					
					var link = getRenderedLink( "looplink", protein1, position1, protein1, position2 );
					if( !link ) {
						reportWebErrorToServer.reportErrorObjectToServer( Error( "Did not get rendered link for UDR..." ));
						throw e;
					}
					
					var distance = link.length;
					
					for( var j = 0; j < udrItem.psmItemList.length; j++ ) {
						var psmItem = udrItem.psmItemList[ j ];
						
						reportText += searchId + "\t";
						reportText += "looplink\t";
						reportText += _proteinNames[ protein1 ] + "\t";
						reportText += position1 + "\t";
						reportText += _proteinNames[ protein1 ] + "\t";
						reportText += position2 + "\t";
						reportText += distance + "\t";
						
						for( var m = 0; m < psmItem.psmValues.length; m++ ) {
							if( m != 0 ) { reportText += "\t"; }
							
							reportText += psmItem.psmValues[ m ];
						}
						
						reportText += "\n";										
					}
				}
			}
		}
		
		downloadStringAsFile( "psm-udr-report.txt", "text/plain", reportText );

	}


	function collateAndDownloadDetailedUDRReport( data ) {

		/*
		* Create the header of the report
		*/
		let reportText = "link type\tprotein 1\tposition 1\tprotein 2\tposition 2\tdistance\tnum PSMs\tnumPeptides\tunique peptides\t";
		
		for( var i = 0; i < data[ "bestPSMValuesNames" ].length; i++ ) {
			if( i != 0 ) { reportText += "\t"; }
			
			reportText += "best PSM " + data[ "bestPSMValuesNames" ][ i ];
		}

		if( data[ "bestPeptideValuesNames" ] ) {
			for( var i = 0; i < data[ "bestPeptideValuesNames" ].length; i++ ) {			
				reportText += "\tbest peptide " + data[ "bestPeptideValuesNames" ][ i ];
			}
		}
		
		reportText += "\n";
		
		/*
		* Create the report itself, by iterating over all UDRs and only printing those that are currently
		* visible on the structure.
		*/
		var udrLength = data[ 'udrItemList' ].length;
		console.log( udrLength );
		for( var i = 0; i < udrLength; i++ ) {
			var udrItem = data[ 'udrItemList' ][ i ];
					
			var link = getRenderedLink( udrItem.linkType, udrItem.proteinSeqId_1, udrItem.proteinPos_1, udrItem.proteinSeqId_2, udrItem.proteinPos_2 );

			// not a rendered link, skip it.
			if( !link ) { continue; }

			if( _linkExclusionHandler.isLinkExcluded( link ) ) {
				continue;
			}

			reportText += udrItem.linkType + "\t";
			reportText += _proteinNames[ link[ 'protein1' ]  ] + "\t";
			reportText += link[ 'position1' ] + "\t";
			
			if( udrItem.linkType === "crosslink" ) {
				reportText += _proteinNames[ link[ 'protein2' ]  ] + "\t";
			} else {
				reportText += "\t";
			}
			reportText += link[ 'position2' ] + "\t";
			reportText += link.length + "\t";
			reportText += udrItem.numPSMs + "\t";
			reportText += udrItem.numPeptides + "\t";
			reportText += udrItem.numUniquePeptides + "\t";

			for( var k = 0; k < udrItem[ 'bestPSMValues' ].length; k++ ) {
				if( k != 0 ) { reportText += "\t"; }

				reportText += udrItem[ 'bestPSMValues' ][ k ] + "\t";
			}
			
			if( udrItem[ 'bestPeptideValues' ] ) {
				for( var k = 0; k < udrItem[ 'bestPeptideValues' ].length; k++ ) {
					reportText += "\t" + udrItem[ 'bestPeptideValues' ][ k ];
				}
			}
			
			reportText += "\n";		
		}
		
		downloadStringAsFile( "detailed-udr-report.txt", "text/plain", reportText );

	}

	function getRenderedLink( type, protein1, position1, protein2, position2 ) {
			
		if( type === "crosslink" ) {
			if (!_renderedLinks[ 'crosslinks' ] || _renderedLinks[ 'crosslinks' ].length === 0 ) { return null; }
			
			for( var i = 0; i < _renderedLinks[ 'crosslinks' ].length; i++ ) {
				var link = _renderedLinks[ 'crosslinks' ][ i ][ 'link' ];

				if( link.protein1 == protein1 && link.protein2 == protein2 &&
					link.position1 == position1 && link.position2 == position2 ) {
					
					return link;
				}

				if( link.protein1 == protein2 && link.protein2 == protein1 &&
						link.position1 == position2 && link.position2 == position1 ) {
						
						return link;
				}
			
			}
		}
		
		
		if( type === "looplink" ) {
			if (!_renderedLinks[ 'looplinks' ] || _renderedLinks[ 'looplinks' ].length === 0 ) { return null; }
			
			for( var i = 0; i < _renderedLinks[ 'looplinks' ].length; i++ ) {
				var link = _renderedLinks[ 'looplinks' ][ i ][ 'link' ];

				if( link.protein1 == protein1 &&
					link.position1 == position1 && link.position2 == position2 ) {
					
					return link;
				}

			
				if( link.protein1 == protein1 &&
						link.position1 == position2 && link.position2 == position2 ) {
						
						return link;
				}
			}
		}
		
		// nothing was found
		return null;	
	}

	function loadDetailedUDRData( callback ) {
		
		console.log( "Loading detailed UDR data." );
		
				incrementSpinner();				// create spinner
				
				var url = "services/imageViewer/getUDRData";
				url += buildQueryStringFromHash();
				
				$.ajax({
						type: "GET",
						url: url,
						dataType: "json",
						success: function(data)	{
							
							try {
						
								_linkPSMCounts.crosslink = data.crosslinkPSMCounts;
								decrementSpinner();

								console.log( data );
								
								callback( data );
								
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
	 * Download a detailed UDR report
	 */
	this_OfOutermostObjectOfClass.downloadDetailedUDRReport = function () {
		loadDetailedUDRData( collateAndDownloadDetailedUDRReport );
	}


	function loadCrosslinkPSMCounts( doDraw ) {
		
		console.log( "Loading crosslink PSM counts." );
		
				incrementSpinner();				// create spinner
				
				var url = "services/imageViewer/getCrosslinkPSMCounts";
				url += buildQueryStringFromHash();
				
				$.ajax({
						type: "GET",
						url: url,
						dataType: "json",
						success: function(data)	{
							
							try {
						
								_linkPSMCounts.crosslink = data.crosslinkPSMCounts;
								decrementSpinner();

								if( doDraw ) {
									drawMeshesOnStructure( drawCrosslinks);
								}
								
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


	//Toggle the visibility of crosslink data on the viewer
	function loadLooplinkData( doDraw ) {
		
		console.log( "Loading looplink data." );
		
				incrementSpinner();				// create spinner
				
				var url = "services/imageViewer/getLooplinkData";
				url += buildQueryStringFromHash();
				
				$.ajax({
						type: "GET",
						url: url,
						dataType: "json",
						success: function(data)	{
							
							try {

								// handle protein monolink positions
								_proteinLooplinkPositions = data.proteinLoopLinkPositions;			        	
								decrementSpinner();

								if( doDraw ) {
									drawMeshesOnStructure( drawLooplinks);
								}
								
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

	function loadLooplinkPSMCounts( doDraw ) {
		
		console.log( "Loading looplink PSM counts." );
		
				incrementSpinner();				// create spinner
				
				var url = "services/imageViewer/getLooplinkPSMCounts";
				url += buildQueryStringFromHash();
				
				$.ajax({
						type: "GET",
						url: url,
						dataType: "json",
						success: function(data)	{
							
							try {
						
								_linkPSMCounts.looplink = data.looplinkPSMCounts;			        	
								decrementSpinner();

								if( doDraw ) {
									drawMeshesOnStructure( drawLooplinks);
								}
								
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



	//Toggle the visibility of crosslink data on the viewer
	function loadMonolinkData( doDraw ) {
		
		console.log( "Loading monolink data." );
		
				incrementSpinner();				// create spinner
				
				var url = "services/imageViewer/getMonolinkData";
				url += buildQueryStringFromHash();
				
				$.ajax({
						type: "GET",
						url: url,
						dataType: "json",
						success: function(data)	{
							
							try {
						
								// handle protein monolink positions
								_proteinMonolinkPositions = data.proteinMonoLinkPositions;			        	
								decrementSpinner();

								if( doDraw ) {
									drawMeshesOnStructure( drawMonolinks);
								}
								
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

	function loadMonolinkPSMCounts( doDraw ) {
		
		console.log( "Loading monolink PSM counts." );
		
				incrementSpinner();				// create spinner
				
				var url = "services/imageViewer/getMonolinkPSMCounts";
				url += buildQueryStringFromHash();
				
				$.ajax({
						type: "GET",
						url: url,
						dataType: "json",
						success: function(data)	{
							
							try {
						
								_linkPSMCounts.monolink = data.monolinkPSMCounts;			        	
								decrementSpinner();

								if( doDraw ) {
									drawMeshesOnStructure( drawMonolinks);
								}
								
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

	//Load protein sequence data for a list of proteins
	function loadProteinSequencesForProteins( proteinIdsToGetSequence, doDrawReport ) {

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
						}


						decrementSpinner();

						if( doDrawReport ) {
							redrawDistanceReport();
						}
						
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

						_linkablePositions = data.linkablePositions;

						//console.log( _linkablePositions );

						// clear all other data that depends on which peptides are loaded
						_proteinLinkPositions = undefined;
						_proteinMonolinkPositions = undefined;
						_proteinLooplinkPositions = undefined;
						_ranges = undefined;
						_coverages = undefined;
						_linkPSMCounts = { };
						_distanceReportData = { };


						populateNavigation();

						populateSearchForm();
						populatePDBFormArea();


	//	        		Cannot put this call here, it does not work right:    updateURLHash( false /* useSearchForm */ );

						decrementSpinner();
						
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
	//					alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ", textStatus: " + textStatus );
				}
		});
	}


	//returns a list of searches for the given link
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


	function getNavigationJSON_Not_for_Image_Or_Structure() {

		
		var json = getJsonFromHash();
		
		

		///   Serialize cutoffs to JSON
		
		var cutoffs = json.cutoffs;
		
		var annTypeIdDisplay = json[ 'annTypeIdDisplay' ];
		
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

	////////////////

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

		var $navigation_links_except_image = $("#navigation_links_except_image"); 
		
		$navigation_links_except_image.empty();
		$navigation_links_except_image.html( html );
		addToolTips( $navigation_links_except_image );
		
		//  Add Merged Image Link
		var imageNavHTML = "<span class=\"tool_tip_attached_jq\" data-tooltip=\"Graphical view of links between proteins\" " + 
			"style=\"white-space:nowrap;\" >[<a href=\"";

		var $viewMergedImageDefaultPageUrl = $("#viewMergedImageDefaultPageUrl");
		var viewMergedImageDefaultPageUrl = $viewMergedImageDefaultPageUrl.val();
		if ( viewMergedImageDefaultPageUrl === undefined || viewMergedImageDefaultPageUrl === "" ) {
			var imageQueryString = "?";
			for ( var j = 0; j < _projectSearchIds.length; j++ ) {
				if ( j > 0 ) {
					imageQueryString += "&";
				}
				imageQueryString += "projectSearchId=" + _projectSearchIds[ j ];
			}
			if ( _projectSearchIdsUserOrdered && _projectSearchIdsUserOrdered !== "" ) {
				imageQueryString += "&ds=" + _projectSearchIdsUserOrdered;
			}
			
			var imageJSON = { };
	//		add taxonomy exclusions
			imageJSON[ 'excludeTaxonomy' ] = _excludeTaxonomy;
	//		add type exclusions
			imageJSON[ 'excludeType' ] = _excludeType;
			//  Add Filter cutoffs
			imageJSON[ 'cutoffs' ] = _psmPeptideCutoffsRootObjectStorage.getPsmPeptideCutoffsRootObject();
			//  Add Ann Type Display
			var annTypeIdDisplay = baseJSONObject.annTypeIdDisplay;
			imageJSON[ 'annTypeIdDisplay' ] = annTypeIdDisplay;
	//		add filter out non unique peptides
			imageJSON[ 'filterNonUniquePeptides' ] = _filterNonUniquePeptides;
			imageJSON[ 'minPSMs' ] = _minPSMs;
			imageJSON[ 'filterOnlyOnePeptide' ] = _filterOnlyOnePeptide;
			imageJSON[ 'removeNonUniquePSMs' ] = _removeNonUniquePSMs;
			var imageJSONString = encodeURI( JSON.stringify( imageJSON ) );
			imageNavHTML += "image.do" 
			+ imageQueryString + "#" + imageJSONString;
		} else {
			imageNavHTML += viewMergedImageDefaultPageUrl;
		}
		
		imageNavHTML += "\">Image View</a>]</span>";

		var $image_viewer_link_span = $("#image_viewer_link_span");

		$image_viewer_link_span.empty();
		$image_viewer_link_span.html( imageNavHTML );
		addToolTips( $image_viewer_link_span );
	}

	//
	////ensure the necessary data are collected before viewer is drawn
	//function loadDataAndDraw( doDraw ) {
	//
	//	if ( ( $( "input#show-crosslinks" ).is( ':checked' ) || $( "input#show-self-crosslinks" ).is( ':checked' ) ) && _proteinLinkPositions == undefined ) {
	//		return loadCrosslinkData( doDraw );
	//	}
	//	
	//	if ( $( "input#show-looplinks" ).is( ':checked' ) && _proteinLooplinkPositions == undefined ) {
	//		return loadLooplinkData( doDraw );
	//	}
	//	
	//	if ( $( "input#show-monolinks" ).is( ':checked' ) && _proteinMonolinkPositions == undefined ) {
	//		return loadMonolinkData( doDraw );
	//	}
	//	
	//	// only load sequence coverage for visible proteins
	//	if ( $( "input#show-coverage" ).is( ':checked' ) ) {
	//		var selectedProteins = getSelectedProteins();
	//		for ( var i = 0; i < selectedProteins.length; i++ ) {
	//			var prot = selectedProteins[ i ];
	//			if ( _ranges == undefined || _ranges[ prot ] == undefined ) {
	//				return loadSequenceCoverageDataForProtein( prot, doDraw );
	//			}
	//		}
	//	}
	//	
	//	
	//	// only load sequences for visible proteins
	//
	//	var selectedProteins = getSelectedProteins();
	//	
	//	var proteinIdsToGetSequence = [];
	//	
	//	for ( var i = 0; i < selectedProteins.length; i++ ) {
	//		var proteinId = selectedProteins[ i ];
	//		if ( _proteinSequences == undefined || _proteinSequences[ proteinId ] == undefined ) {
	//			
	//			proteinIdsToGetSequence.push( proteinId );
	//		}
	//	}
	//
	//	if ( proteinIdsToGetSequence.length > 0 ) {
	//		
	//		return loadProteinSequenceDataForProtein( proteinIdsToGetSequence, doDraw );
	//	}
	//	
	//	if ( doDraw ) {
	//		drawSvg();
	//	}
	//}


	var populatePDBFormArea = function() {
		
		var json = getJsonFromHash();

		_linkExclusionHandler.populateDataFromJSON( json );
		_backboneColorManager.populateDataFromJSON( json );

		$( "input#show-looplinks" ).prop('checked', json[ 'show-looplinks' ] );
		
		if( 'show-crosslinks' in json ) {
			$( "input#show-crosslinks" ).prop('checked', json[ 'show-crosslinks' ] );
		}
		
		$( "input#show-monolinks" ).prop('checked', json[ 'show-monolinks' ] );
		$( "input#show-coverage" ).prop('checked', json[ 'show-coverage' ] );
		
		if( 'show-unique-udrs' in json ) {
			$( "input#show-unique-udrs" ).prop('checked', json[ 'show-unique-udrs' ] );
		}
		
		if( 'show-linkable-positions' in json ) {
			$( "input#show-linkable-positions" ).prop('checked', json[ 'show-linkable-positions' ] );
		}
		
		if( 'shade-by-counts' in json ) {
			$( "input#shade-by-counts" ).prop('checked', json[ 'shade-by-counts' ] );
		}
		
		var existingSelect = $("#pdb-file-select-menu");
		if( existingSelect.length ) {

			drawStructure();
			
		} else {
			
			var pdbFileId = json[ 'pdb-file-id' ];
					
			if( pdbFileId ) {
				loadPDBFiles( pdbFileId, true );
			} else {
				loadPDBFiles();
			}
			
		}
		
		
		if( 'render-mode' in json ) {
			$( "#select-render-mode" ).val( json[ 'render-mode' ] );
		}
		
		if( 'link-color-mode' in json ) {
			$( "#select-link-color-mode" ).val( json[ 'link-color-mode' ] );
		}
			
		if( 'udcs' in json && 'udcl' in json ) {		
			_linkColorHandler.setUserDistanceConstraints(parseInt( json.udcs), parseInt( json.udcl ) );		
		}

		_linkColorHandler.setUserColorBySearch( json[ 'ucbs' ] );
		
		_linkColorHandler.setUserColorByType( json[ 'ucbt' ] );
		
		_linkColorHandler.setUserColorByLength( json[ 'ucbl' ] );
		
		drawLegend();
		
	};

	var changeRenderMode = function() {
		
		drawStructure();
		
	};

	var changeLinkColorMode = function() {
		
		drawLegend();
		drawMeshesOnStructure();
		
	};

	var toggleShadeByCounts = function() {
		drawMeshesOnStructure();
		
	};
	// populate the PDB files
	function loadPDBFiles( defaultId, doDraw ) {
		
		incrementSpinner();
		
		var projectId = $("#project_id" ).val();
		
		var url = "services/pdb/listPDBFiles?projectId=" + projectId;
		
		$.ajax({
				type: "GET",
				url: url,
				dataType: "json",
				success: function(data)	{
					
					try {
				
						console.log( "PDB files:" );
						console.log( data );

						createPDBFileSelect( data, defaultId, doDraw );
						decrementSpinner();
						
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


	// create PDB file pull-down
	var _PDB_FILES;
	function createPDBFileSelect( pdbFiles, defaultId, doDraw ) {
		
		_PDB_FILES = { };
		
		// blow away any existing element
		var existingSelect = $("#pdb-file-select-menu");
		if( existingSelect ) {
			existingSelect.remove();
		}
		
		var html = "<select id=\"pdb-file-select-menu\" class=\"pdb-file-select-menu\">\n";

		html += "\t<option value=\"0\">Select a PDB File:</option>\n";

		for ( var i = 0; i < pdbFiles.length; i++ ) {
			
			_PDB_FILES[ pdbFiles[ i ][ 'dto' ][ 'id' ] ] = pdbFiles[ i ];
			
			if( pdbFiles[ i ][ 'dto' ][ 'description' ] ) {
				html+= "\t<option data-filename=\"" + pdbFiles[ i ][ 'dto' ][ 'name' ] + "\" value=\"" + pdbFiles[ i ][ 'dto' ][ 'id' ] + "\">" + pdbFiles[ i ][ 'dto' ][ 'description' ] + " (" + pdbFiles[ i ][ 'dto' ][ 'name' ] + ")</option>\n";
			} else {
				html+= "\t<option data-filename=\"" + pdbFiles[ i ][ 'dto' ][ 'name' ] + "\" value=\"" + pdbFiles[ i ][ 'dto' ][ 'id' ] + "\">" + pdbFiles[ i ][ 'dto' ][ 'name' ] + "</option>\n";
			}
		}

		html += "</select>\n";

		var $newSelector = $( html ).insertBefore( $( "span#pdb-file-selector-location" ) );

		if( typeof defaultId !== 'undefined' ) {
			$newSelector.val( defaultId );
		}
		
		$newSelector.change( function() {
			selectNewPDBFile();
		});
		
		if( doDraw ) {
			loadPDBFileContent();
		}

	}

	var selectNewPDBFile = function() {
		

		// selected PDB file
		var pdbFile = getSelectedPDBFile();
			
		if( isNaN( pdbFile.id ) || pdbFile.id == 0 ) {
			return;
		}
		
		$( "#chain-list-div" ).empty();
		updateURLHash( false /* useSearchForm */ );
		
		loadPDBFileContent();	
	};

	// get the currently selected pdb file
	function getSelectedPDBFile() {
		
		var pdbFile = { };
		pdbFile.id = parseInt( $("#pdb-file-select-menu").val() );
		pdbFile.name = $("#pdb-file-select-menu").find(":selected").text();
		pdbFile.filename = $("#pdb-file-select-menu").find(":selected").attr( "data-filename" );
		
		if ( isNaN( pdbFile.id ) ) {
			
			pdbFile.id = 0;
		}
		
		return pdbFile;
	}


	var _PDB_FILE_CONTENT;
	function loadPDBFileContent() {
			

		// selected PDB file
		var pdbFile = getSelectedPDBFile();
			
		if( isNaN( pdbFile.id ) || pdbFile.id == 0 ) {
			return;
		}

		incrementSpinner();
		
		$("#glmol-div").empty();

		
		var url = "services/pdb/getContentForPDBFile";
		url += "?pdbFileId=" + pdbFile.id;
		
		$.ajax({
				type: "GET",
				url: url,
				dataType: "json",
				success: function(data)	{
					
					try {

						_PDB_FILE_CONTENT = data[ 'content' ];
						_VIEWER = undefined;
						_STRUCTURE = undefined;

						initViewer();
						decrementSpinner();
						
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

	var _VIEWER;
	var _STRUCTURE;
	function initViewer() {

		_VIEWER = createViewer();
		_STRUCTURE = pv.io.pdb( _PDB_FILE_CONTENT );

		/*
		* If the structure has a chain with no name, change its name to _ (underscore)
		*/
		if( _STRUCTURE._chains && _STRUCTURE._chains.length == 1 ) {
			if( _STRUCTURE._chains[ 0 ]._name === " " ) {
				_STRUCTURE._chains[ 0 ]._name = "_";
			}
		}


		var proxlOb = { };
		proxlOb.viewerInitialLoad = 1;
		
		_VIEWER.proxlOb = proxlOb;
		
		loadPDBFileAlignments( listChains, true );
	}

	/**
	 * Create and return a pv viewer located in the correct spot (popup window or in page)
	 */
	var createViewer = function() {

		var viewer;
		console.log( "Called createViewer()" );
		
		if( _NEW_WINDOW ) {
			var $viewerDiv = $( _NEW_WINDOW.document.getElementById( "new-window-viewer-div" ) );
		
			$viewerDiv.empty();
			
			var options = {
					width: $(_NEW_WINDOW).width() - 20,
					height: $(_NEW_WINDOW).height() - 20,
					antialias: true,
					quality : 'high',
					fog: false
			};
			
			viewer = pv.Viewer(_NEW_WINDOW.document.getElementById('new-window-viewer-div'), options);
			
		} else {
			$("#glmol-div").empty();
			
			var options = {
					width: 500,
					height: 500,
					antialias: true,
					quality : 'high',
					fog: false
			};
				
			viewer = pv.Viewer(document.getElementById('glmol-div'), options);
		}
		
		
		viewer.on('click', function(picked, e) {  
			return viewerClicked( picked, e );
		});
		
		return viewer;
	};

	function isIE() { return ((navigator.appName == 'Microsoft Internet Explorer') || ((navigator.appName == 'Netscape') && (new RegExp("Trident/.*rv:([0-9]{1,}[\.0-9]{0,})").exec(navigator.userAgent) != null))); }

	var _NEW_WINDOW;
	function popoutViewer() {
		
		if( isIE() ) {
			alert( "This feature not supported by Internet Explorer." );
			return;
		}
		
		$("#glmol-div").empty();
		$("#popout-link-span").html( "<a href=\"javascript:\" onclick=\"window.structurePagePrimaryRootCodeObject.closePopout()\">[Popin Viewer]</a>" );
		
		_NEW_WINDOW = window.open("proxlExternalViewer.do", "proxlWindow", "width=800, height=800, resizable=yes" );

	}

	this_OfOutermostObjectOfClass.closePopout = function() {
		console.log( "called closePopout()" );
		_NEW_WINDOW.close();
	};

	var popinViewer = function() {
		
		console.log( "called popinViewer()" );
		
		$("#popout-link-span").html( "<a href=\"javascript:\" onclick=\"popoutViewer()\">[Popout Viewer]</a>" );
		_NEW_WINDOW = undefined;
		
		_VIEWER = createViewer();

		var proxlOb = { };
		proxlOb.viewerInitialLoad = 1;
		
		_VIEWER.proxlOb = proxlOb;
		
		drawStructure();
	};


	function drawStructureAfterResize() {
			_VIEWER = createViewer();
			
			var proxlOb = { };
			proxlOb.viewerInitialLoad = 1;
			
			_VIEWER.proxlOb = proxlOb;
			
			drawStructure();           
	}


	var _ALIGNMENTS;
	function loadPDBFileAlignments( callback, doDraw ) {
		

		// selected PDB file
		var pdbFile = getSelectedPDBFile();
			
		if( isNaN( pdbFile.id ) || pdbFile.id == 0 ) {
			return;
		}
		
		var url = "services/psa/getAlignmentsForPDBFile";
		url += "?pdbFileId=" + pdbFile.id;

		incrementSpinner();
		
		$.ajax({
				type: "GET",
				url: url,
				dataType: "json",
				success: function(data)	{
					
					try {
					
						_ALIGNMENTS = data;
						//console.log( _ALIGNMENTS );
						decrementSpinner();

						callback( doDraw );
						
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

	/*
	* Create and download a text script for drawing the currently-shown links in the Chimera viewer for the
	* currently shown PDB file.
	*/
	this_OfOutermostObjectOfClass.downloadChimeraScript = function() {
		
		console.log( "downloadChimeraScript called." );
		
		var customColorHash = { };
		
		var scriptText = "";
			
		// do monolinks
		var links = _renderedLinks.monolinks;
		if( $( "input#show-monolinks" ).is( ':checked' ) && links ) {
			
			
			
			for( var i = 0; i < links.length; i++ ) {
				
				var atom = links[ i ].atom1;
				
				var hexColor = _linkColorHandler.getLinkColor( links[ i ].link, 'hex' );
				var colorName;
				if( !( hexColor in customColorHash ) ) {
					colorName = getCustomColorName( customColorHash, hexColor );
					var rgbaColor = _linkColorHandler.hexToRgbaDecimalArray( hexColor, 1 );
					
					scriptText += "colordef " + colorName + " " + rgbaColor[ 0 ] + " " + rgbaColor[ 1 ] + " " + rgbaColor[ 2 ] + "\n";
				} else {
					colorName = getCustomColorName( customColorHash, hexColor );
				}
				
				if( !colorName ) {
					console.log( "ERROR: Could not get a color name for link: " );
					console.log( link );
				}
				
				scriptText += "shape sphere ";
				scriptText += "center :" + atom.residue().num() + "." + atom.residue().chain().name() + "@CA ";
				scriptText += "radius 1.5 color " + colorName + " modelName monolinks modelId 4\n";
				
			}
		}
		
		
		var distancesAdded = { };
		
		// do looplinks
		links = _renderedLinks.looplinks;
		if( $( "input#show-looplinks" ).is( ':checked' ) && links ) {
			
			
			
			for( var i = 0; i < links.length; i++ ) {

				// skip hidden links
				if( _linkExclusionHandler.isLinkExcluded( links[ i ][ 'link' ] ) ) {
					continue;
				}

				var atom1 = links[ i ].atom1;
				var atom2 = links[ i ].atom2;
				
				const distance = StructureUtils.calculateDistance( atom1.pos(), atom2.pos() );

				var hexColor = _linkColorHandler.getLinkColor( links[ i ].link, 'hex' );
				var colorName;
				if( !( hexColor in customColorHash ) ) {
					colorName = getCustomColorName( customColorHash, hexColor );
					var rgbaColor = _linkColorHandler.hexToRgbaDecimalArray( hexColor, 1 );
					
					scriptText += "colordef " + colorName + " " + rgbaColor[ 0 ] + " " + rgbaColor[ 1 ] + " " + rgbaColor[ 2 ] + "\n";
				} else {
					colorName = getCustomColorName( customColorHash, hexColor );
				}
				
				if( !colorName ) {
					console.log( "ERROR: Could not get a color name for link: " );
					console.log( link );
				}
				
				scriptText += "shape tube ";
				scriptText += ":" + atom1.residue().num() + "." + atom1.residue().chain().name() + "@CA";
				scriptText += ":" + atom2.residue().num() + "." + atom2.residue().chain().name() + "@CA ";
				scriptText += "radius .75 color " + colorName + " modelName looplinks modelId 3\n";
				
				// ensure a distance is only added once (ie, don't want same distance added for a looplink and crosslink on same atoms)
				var distanceId = atom1.residue().chain().name() + "-" + atom1.residue().num() + "-" + atom2.residue().chain().name() + "-" + atom2.residue().num();	
				
				if( !( distanceId in distancesAdded ) ) {
					scriptText += "distance ";
					scriptText += ":" + atom1.residue().num() + "." + atom1.residue().chain().name() + "@CA ";
					scriptText += ":" + atom2.residue().num() + "." + atom2.residue().chain().name() + "@CA\n";
					
					distancesAdded[ distanceId ] = 1;
				}
				
			}
		}
		
		
		// do crosslinks
		links = _renderedLinks.crosslinks;
		if( $( "input#show-crosslinks" ).is( ':checked' ) && links ) {
			
			
			
			for( var i = 0; i < links.length; i++ ) {

				// skip hidden links
				if( _linkExclusionHandler.isLinkExcluded( links[ i ][ 'link' ] ) ) {
					continue;
				}

				var atom1 = links[ i ].atom1;
				var atom2 = links[ i ].atom2;
				
				var distance = StructureUtils.calculateDistance( atom1.pos(), atom2.pos() );

				var hexColor = _linkColorHandler.getLinkColor( links[ i ].link, 'hex' );
				var colorName;
				if( !( hexColor in customColorHash ) ) {
					colorName = getCustomColorName( customColorHash, hexColor );
					var rgbaColor = _linkColorHandler.hexToRgbaDecimalArray( hexColor, 1 );
					
					scriptText += "colordef " + colorName + " " + rgbaColor[ 0 ] + " " + rgbaColor[ 1 ] + " " + rgbaColor[ 2 ] + "\n";
				} else {
					colorName = getCustomColorName( customColorHash, hexColor );
				}
				
				if( !colorName ) {
					console.log( "ERROR: Could not get a color name for link: " );
					console.log( link );
				}
				
				scriptText += "shape tube ";
				scriptText += ":" + atom1.residue().num() + "." + atom1.residue().chain().name() + "@CA";
				scriptText += ":" + atom2.residue().num() + "." + atom2.residue().chain().name() + "@CA ";
				scriptText += "radius .75 color " + colorName + " modelName crosslinks modelId 2\n";
				
				// ensure a distance is only added once (ie, don't want same distance added for a looplink and crosslink on same atoms)
				var distanceId = atom1.residue().chain().name() + "-" + atom1.residue().num() + "-" + atom2.residue().chain().name() + "-" + atom2.residue().num();	
				
				if( !( distanceId in distancesAdded ) ) {
					scriptText += "distance ";
					scriptText += ":" + atom1.residue().num() + "." + atom1.residue().chain().name() + "@CA ";
					scriptText += ":" + atom2.residue().num() + "." + atom2.residue().chain().name() + "@CA\n";
					
					distancesAdded[ distanceId ] = 1;
				}
				
			}
		}
		
		
		downloadStringAsFile( "chimera-script-" + getSelectedPDBFile().filename + ".txt", "text/plain", scriptText );
	};

	function endsWith(str, suffix) {
		return str.indexOf(suffix, str.length - suffix.length) !== -1;
	}

	/*
	* Used by the pymol and chimera scripts to get custom color names
	*/
	var getCustomColorName = function( customColorHash, color ) {
		if( !( color in customColorHash ) ) {
			var name = "CustomColor" + Object.keys( customColorHash ).length;
			customColorHash[ color ] = name;
		}
		
		return customColorHash[ color ];
	}

	/*
	* Create and download a text script for drawing the currently-shown links in the Pymol viewer for the
	* currently shown PDB file.
	*/
	this_OfOutermostObjectOfClass.downloadPymolScript = function() {
		
		var fullpdbName = getSelectedPDBFile().filename;
		var customColorHash = { };

		// strip off any extension
		var pdbName = fullpdbName.substr(0, fullpdbName.lastIndexOf('.')) || fullpdbName;

		
		var scriptText = "";
			
		// do monolinks
		var links = _renderedLinks.monolinks;
		if( $( "input#show-monolinks" ).is( ':checked' ) && links ) {
			
			
			
			for( var i = 0; i < links.length; i++ ) {
				
				var atom = links[ i ].atom1;
				
				var hexColor = _linkColorHandler.getLinkColor( links[ i ].link, 'hex' );
				var colorName;
				if( !( hexColor in customColorHash ) ) {
					colorName = getCustomColorName( customColorHash, hexColor );
					var rgbaColor = _linkColorHandler.hexToRgbaDecimalArray( hexColor, 1 );
					
					scriptText += "set_color " + colorName + ", [" + rgbaColor[ 0 ] + ", " + rgbaColor[ 1 ] + "," + rgbaColor[ 2 ] + "]\n";
				} else {
					colorName = getCustomColorName( customColorHash, hexColor );
				}
				
				if( !colorName ) {
					console.log( "ERROR: Could not get a color name for link: " );
					console.log( link );
				}
				
				scriptText += "color " + colorName + ", /" + pdbName + "//" + atom.residue().chain().name() + "/" + atom.residue().num() + "/ca\n";
				scriptText += "show sphere, /" + pdbName + "//" + atom.residue().chain().name() + "/" + atom.residue().num() + "/ca\n";
		
			}
		}
		
		var distancesAdded = { };
		
		// do looplinks
		links = _renderedLinks.looplinks;
		if( $( "input#show-looplinks" ).is( ':checked' ) && links ) {
			
			for( var i = 0; i < links.length; i++ ) {

				// skip hidden links
				if( _linkExclusionHandler.isLinkExcluded( links[ i ][ 'link' ] ) ) {
					continue;
				}

				var atom1 = links[ i ].atom1;
				var atom2 = links[ i ].atom2;
				
	//			var distance = calculateDistance( atom1.pos(), atom2.pos() );
				
				var hexColor = _linkColorHandler.getLinkColor( links[ i ].link, 'hex' );
				var colorName;
				if( !( hexColor in customColorHash ) ) {
					colorName = getCustomColorName( customColorHash, hexColor );
					var rgbaColor = _linkColorHandler.hexToRgbaDecimalArray( hexColor, 1 );
					
					scriptText += "set_color " + colorName + ", [" + rgbaColor[ 0 ] + ", " + rgbaColor[ 1 ] + "," + rgbaColor[ 2 ] + "]\n";
				} else {
					colorName = getCustomColorName( customColorHash, hexColor );
				}
				
				if( !colorName ) {
					console.log( "ERROR: Could not get a color name for link: " );
					console.log( link );
				}
				
				//  Get Experiment Protein Sequence Id and Position data for the atoms and process them
				
				var atom1_ExpproteinSequenceVersionIdPositionPairs = StructureAlignmentUtils.getExpproteinSequenceVersionIdPositionPairs( getVisibleAlignmentsForChain( atom1.residue().chain().name() ), atom1.residue().index() + 1 );
				var atom2_ExpproteinSequenceVersionIdPositionPairs = StructureAlignmentUtils.getExpproteinSequenceVersionIdPositionPairs( getVisibleAlignmentsForChain( atom2.residue().chain().name() ), atom2.residue().index() + 1 );
				
				if( !atom1_ExpproteinSequenceVersionIdPositionPairs || atom1_ExpproteinSequenceVersionIdPositionPairs.length != 1 ) {
					console.log( "WARNING: Got anomolous readings for first protein in link." );
				}
				
				if( !atom2_ExpproteinSequenceVersionIdPositionPairs || atom2_ExpproteinSequenceVersionIdPositionPairs.length != 1 ) {
					console.log( "WARNING: Got anomolous readings for second protein in link." );
				}
				
				var atom1_ExpproteinSequenceVersionIdPositionPair = atom1_ExpproteinSequenceVersionIdPositionPairs[ 0 ];
				var atom2_ExpproteinSequenceVersionIdPositionPair = atom2_ExpproteinSequenceVersionIdPositionPairs[ 0 ];
				
				// if protein names contain commas, only keep what's before the first comma
				var proteinName1 = _proteinNames[ atom1_ExpproteinSequenceVersionIdPositionPair.proteinSequenceVersionId ];
				proteinName1 = proteinName1.split(",")[ 0 ];
				
				var proteinName2 = _proteinNames[ atom2_ExpproteinSequenceVersionIdPositionPair.proteinSequenceVersionId ];
				proteinName2 = proteinName2.split(",")[ 0 ];
				
				var uniqueId = 
					proteinName1 + "_" 
					+ atom1_ExpproteinSequenceVersionIdPositionPair.position 
					+ "L" + proteinName2 + "_" 
					+ atom2_ExpproteinSequenceVersionIdPositionPair.position;

				// ensure a distance is only added once (ie, don't want same distance added for a looplink and crosslink on same atoms)
				var distanceId = atom1.residue().chain().name() + "-" + atom1.residue().num() + "-" + atom2.residue().chain().name() + "-" + atom2.residue().num();	
				
				if( !( distanceId in distancesAdded ) ) {
					scriptText += "distance " + uniqueId + ", ";
					distancesAdded[ distanceId ] = 1;
				}
				
				scriptText += "(/" + pdbName + "//" + atom1.residue().chain().name() + "/" + atom1.residue().num() + "/ca), ";
				scriptText += "(/" + pdbName + "//" + atom2.residue().chain().name() + "/" + atom2.residue().num() + "/ca)\n";
				
				scriptText += "color " + colorName +", " + uniqueId + "\n";			
			}
		}
		
		
		// do crosslinks
		links = _renderedLinks.crosslinks;
		if( $( "input#show-crosslinks" ).is( ':checked' ) && links ) {
			
			for( var i = 0; i < links.length; i++ ) {

				// skip hidden links
				if( _linkExclusionHandler.isLinkExcluded( links[ i ][ 'link' ] ) ) {
					continue;
				}

				var atom1 = links[ i ].atom1;
				var atom2 = links[ i ].atom2;
				
	//			var distance = calculateDistance( atom1.pos(), atom2.pos() );

				var hexColor = _linkColorHandler.getLinkColor( links[ i ].link, 'hex' );
				var colorName;
				if( !( hexColor in customColorHash ) ) {
					colorName = getCustomColorName( customColorHash, hexColor );
					var rgbaColor = _linkColorHandler.hexToRgbaDecimalArray( hexColor, 1 );
					
					scriptText += "set_color " + colorName + ", [" + rgbaColor[ 0 ] + ", " + rgbaColor[ 1 ] + "," + rgbaColor[ 2 ] + "]\n";
				} else {
					colorName = getCustomColorName( customColorHash, hexColor );
				}
				
				if( !colorName ) {
					console.log( "ERROR: Could not get a color name for link: " );
					console.log( link );
				}

				//  Get Experiment Protein Sequence Id and Position data for the atoms and process them
				
				var atom1_ExpproteinSequenceVersionIdPositionPairs = StructureAlignmentUtils.getExpproteinSequenceVersionIdPositionPairs( getVisibleAlignmentsForChain( atom1.residue().chain().name() ), atom1.residue().index() + 1 );
				var atom2_ExpproteinSequenceVersionIdPositionPairs = StructureAlignmentUtils.getExpproteinSequenceVersionIdPositionPairs( getVisibleAlignmentsForChain( atom2.residue().chain().name() ), atom2.residue().index() + 1 );
				
				
				if( !atom1_ExpproteinSequenceVersionIdPositionPairs || atom1_ExpproteinSequenceVersionIdPositionPairs.length != 1 ) {
					console.log( "WARNING: Got anomolous readings for first protein in link." );
				}
				
				if( !atom2_ExpproteinSequenceVersionIdPositionPairs || atom2_ExpproteinSequenceVersionIdPositionPairs.length != 1 ) {
					console.log( "WARNING: Got anomolous readings for second protein in link." );
				}
				

				var atom1_ExpproteinSequenceVersionIdPositionPair = atom1_ExpproteinSequenceVersionIdPositionPairs[ 0 ];
				var atom2_ExpproteinSequenceVersionIdPositionPair = atom2_ExpproteinSequenceVersionIdPositionPairs[ 0 ];
				
				
				// if protein names contain commas, only keep what's before the first comma
				var proteinName1 = _proteinNames[ atom1_ExpproteinSequenceVersionIdPositionPair.proteinSequenceVersionId ];
				proteinName1 = proteinName1.split(",")[ 0 ];
				
				var proteinName2 = _proteinNames[ atom2_ExpproteinSequenceVersionIdPositionPair.proteinSequenceVersionId ];
				proteinName2 = proteinName2.split(",")[ 0 ];
				
				var uniqueId = 
					proteinName1 + "_" 
					+ atom1_ExpproteinSequenceVersionIdPositionPair.position 
					+ "C" + proteinName2 
					+ "_" + atom2_ExpproteinSequenceVersionIdPositionPair.position;
				
				// ensure a distance is only added once (ie, don't want same distance added for a looplink and crosslink on same atoms)
				var distanceId = atom1.residue().chain().name() + "-" + atom1.residue().num() + "-" + atom2.residue().chain().name() + "-" + atom2.residue().num();	
				
				if( !( distanceId in distancesAdded ) ) {
					scriptText += "distance " + uniqueId + ", ";
					distancesAdded[ distanceId ] = 1;
				}

				scriptText += "(/" + pdbName + "//" + atom1.residue().chain().name() + "/" + atom1.residue().num() + "/ca), ";
				scriptText += "(/" + pdbName + "//" + atom2.residue().chain().name() + "/" + atom2.residue().num() + "/ca)\n";
				
				scriptText += "color " + colorName +", " + uniqueId + "\n";			
			}
		}
		
		scriptText += "set dash_width, 5\n";
		scriptText += "set dash_length, 2\n";
		scriptText += "set cartoon_transparency, 0\n";
		scriptText += "set label_font_id, 7\n";
		scriptText += "set label_color, white\n";
		scriptText += "set ray_opaque_background, off\n";
		
		downloadStringAsFile( "pymol-script-" + getSelectedPDBFile().filename + ".txt", "text/plain", scriptText );
	};

	var getLinkerStringsAsArray = function() {
		var linkers = [ ];
		
		for( var i = 0; i < _searches.length; i++ ) {
			for( var j = 0; j < _searches[ i ][ 'linkers' ].length; j++ ) {			
				linkers.push( _searches[ i ][ 'linkers' ][ j ][ 'abbr' ] );
			}
		}
		
		return linkers;
	};

	this_OfOutermostObjectOfClass.beginSkylinePeptideReport = function() {

		const queryPromise = getAllLinkersSupportedForSkylineReport();

		queryPromise.then( (queryResults) => {

			if( queryResults[0] === true ) {

				const lookupPromise = doCleavageLookupForSkyline();

				lookupPromise.then( (results) => {

					generateSkylinePeptideReport( results );

				});

			} else {
				alert("At least one linker on the page does not have a chemical formula or linkable positions defined. Cannot run report." );
			}
		});
	};

	var generateSkylinePeptideReport = function( results ) {

		let lines = [ ];
		let i = 0;
		for( const result of results ) {

			const distance = StructureAlignmentUtils.getShortestDistanceForProteinPair( result[ 'proteinPositionPair' ], _ALIGNMENTS, _STRUCTURE );

			if( distance !== -1 ) {
				lines[ i ] = [result[ 'reportLine' ], "\t", distance ].join("");
				i++;
			}
		}

		downloadStringAsFile( "peptides-for-all-mappable-udrs-skyline.txt", "text/plain", lines.join( "\n" ) );
	};

	/**
	 * Sends the request out for a lookup
	 */
	var getAllLinkersSupportedForSkylineReport = function( ) {

		return new Promise( (resolve, reject ) => {

			incrementSpinner();				// create spinner

			const url = "services/cleavageProductLookup/allLinkersAreSupported";

			const requestData = {
				projectSearchIds: _projectSearchIds,
			};

			const requestDataJSON = JSON.stringify( requestData );

			$.ajax({
				type: "POST",
				url: url,
				data: requestDataJSON,
				dataType: "json",
				contentType: "application/json; charset=utf-8",
				success: function (data) {
					decrementSpinner();
					resolve( data );
				},
				failure: function (errMsg) {
					decrementSpinner();
					handleAJAXFailure(errMsg);
					reject();
				},
				error: function (jqXHR, textStatus, errorThrown) {
					decrementSpinner();
					handleAJAXError(jqXHR, textStatus, errorThrown);
					reject();
				}
			});
		});
	};

	/**
	 * Sends the request out for a lookup
	 */
	var doCleavageLookupForSkyline = function( ) {

		return new Promise( (resolve, reject ) => {

			const visibleProteinsMap = getVisibleProteins();

			if( !visibleProteinsMap || visibleProteinsMap == undefined || visibleProteinsMap.length < 1 ) { return; }

			const visibleProteins = Object.keys( visibleProteinsMap );

			let proteinNameMap = { };
			for( const proteinId of Object.keys( visibleProteinsMap ) ) {
				proteinNameMap[ proteinId ] = _proteinNames[ proteinId ];
			}

			incrementSpinner();				// create spinner

			const url = "services/cleavageProductLookup/getCleavageProductsForSkyline";

			const requestData = {
				projectSearchIds: _projectSearchIds,
				proteinSequenceVersionIds: visibleProteins,
				proteinNameMap: proteinNameMap
			};

			const requestDataJSON = JSON.stringify( requestData );

			$.ajax({
				type: "POST",
				url: url,
				data: requestDataJSON,
				dataType: "json",
				contentType: "application/json; charset=utf-8",
				success: function (data) {
					decrementSpinner();
					resolve( data );
				},
				failure: function (errMsg) {
					decrementSpinner();
					handleAJAXFailure(errMsg);
					reject();
				},
				error: function (jqXHR, textStatus, errorThrown) {
					decrementSpinner();
					handleAJAXError(jqXHR, textStatus, errorThrown);
					reject();
				}
			});
		});
	};

	/**
	 * Answers the request for the lookup
	 */
	var generateAllUDRsReport = function( data, onlyShortest ) {
		
		let response = "chain 1\tchain 1 position\tprotein 1\tposition 1\tchain 2\tchain 2 position\tprotein 2\tposition 2\tdistance\n";

		var visibleProteinsMap = getVisibleProteins();
		
		for( var i = 0; i < data.length; i++ ) {
			
			var protein1 =  parseInt(data[ i ][ 'protein1' ]);
			var protein2 =  parseInt(data[ i ][ 'protein2' ]);
			
			var position1 = parseInt(data[ i ][ 'position1' ]);
			var position2 = parseInt(data[ i ][ 'position2' ]);
			

			var chains1 = visibleProteinsMap[ protein1 ];
			var chains2 = visibleProteinsMap[ protein2 ];
			
			var shortestLink = 0;

			if( !chains1 || chains1 == undefined || chains1.length < 1 ) {
				console.log( "ERROR: Got no chains for protein: " + protein1 );
				return;
			}
			
			if( !chains2 || chains2 == undefined || chains2.length < 1 ) {
				console.log( "ERROR: Got no chains for protein: " + protein2 );
				return;
			}
			

			for( let j = 0; j < chains1.length; j++ ) {
				const chain1 = chains1[ j ];

				const coordsArray1 = StructureAlignmentUtils.findCACoords( protein1, position1, [ chain1 ], _ALIGNMENTS, _STRUCTURE );
				if( coordsArray1 == undefined || coordsArray1.length < 1 ) { continue; }

				const atoms1 = StructureAlignmentUtils.findCAAtoms( protein1, position1, [ chain1 ], _ALIGNMENTS, _STRUCTURE );
				if( atoms1 === undefined || atoms1.length < 1 ) {
					const message = "Error getting atom for alpha carbon for protein " + protein1 + ", position " + position1 + ", chain " + chain1;
					console.log( message );
					alert( message );
					return;
				}

				if( atoms1.length > 1 ) {
					console.log( "WARNING, got more than one alpha carbon for " + protein1 + ", position " + position1 + ", chain " + chain1 );
				}
				const atom1 = atoms1[ 0 ];
				const pdbChainPosition1 = atom1.residue().num();

				for( let k = 0; k < chains2.length; k++ ) {
					const chain2 = chains2[ k ];
					
					if( chain1 == chain2 && protein1 == protein2 && position1 == position2 ) { continue; }

					const coordsArray2 = StructureAlignmentUtils.findCACoords( protein2, position2, [ chain2 ], _ALIGNMENTS, _STRUCTURE );
					if( coordsArray1 == undefined || coordsArray2.length < 1 ) { continue; }


					const atoms2 = StructureAlignmentUtils.findCAAtoms( protein2, position2, [ chain2 ], _ALIGNMENTS, _STRUCTURE );
					if( atoms2 === undefined || atoms2.length < 1 ) {
						const message = "Error getting atom for alpha carbon for protein " + protein2 + ", position " + position2 + ", chain " + chain2;
						console.log( message );
						alert( message );
						return;
					}

					if( atoms2.length > 1 ) {
						console.log( "WARNING, got more than one alpha carbon for " + protein2 + ", position " + position2 + ", chain " + chain2 );
					}
					const atom2 = atoms2[ 0 ];
					const pdbChainPosition2 = atom2.residue().num();

					const distance = StructureUtils.calculateDistance( coordsArray1[ 0 ], coordsArray2[ 0 ] );

					if( !onlyShortest ) {
					
						response += chain1 + "\t" + pdbChainPosition1 + "\t" + _proteinNames[ protein1 ] + "\t" + position1 + "\t";
						response += chain2 + "\t" + pdbChainPosition2 + "\t" + _proteinNames[ protein2 ] + "\t" + position2 + "\t";
						response += distance + "\n";	

					} else {
						
						if( !shortestLink || shortestLink[ 'distance' ] > distance ) {

							shortestLink = {
												'chain1' : chain1,
												'chainPosition1' : pdbChainPosition1,
												'chain2' : chain2,
												'chainPosition2' : pdbChainPosition2,
												'protein1' : protein1,
												'protein2' : protein2,
												'position1' : position1,
												'position2' : position2,
												'distance' : distance
										};
						}
						
					}
				}
			}
			
			if( onlyShortest && shortestLink ) {
				
				response += shortestLink.chain1 + "\t" + shortestLink.chainPosition1 + "\t" + _proteinNames[ shortestLink.protein1 ] + "\t" + shortestLink.position1 + "\t";
				response += shortestLink.chain2 + "\t" + shortestLink.chainPosition2 + "\t" + _proteinNames[ shortestLink.protein2 ] + "\t" + shortestLink.position2 + "\t";
				response += shortestLink.distance + "\n";
			}
		}
			
		downloadStringAsFile( "all-by-all-linkable-positions.txt", "text/plain", response );
	};


	this_OfOutermostObjectOfClass.downloadUDRsAsProteinPairs = function(onlyShortest) {

		const visibleProteinsMap = getVisibleProteins();

		if( !visibleProteinsMap || visibleProteinsMap == undefined || visibleProteinsMap.length < 1 ) { return; }

		const visibleProteins = Object.keys( visibleProteinsMap );

		const lookupPromise = StructureWebserviceMethods.doLinkablePositionsLookup({
			proteins : visibleProteins,
			projectSearchIds : _projectSearchIds,
			startCallout : incrementSpinner,
			endCallout : decrementSpinner
		});

		lookupPromise.then( (data) => {
			generateAllUDRsReport( data, onlyShortest);
		});

	};

	this_OfOutermostObjectOfClass.downloadShownUDRsForRosetta = function() {
		var response = "";
		
		if( _renderedLinks[ 'crosslinks' ] && _renderedLinks[ 'crosslinks' ].length > 0 ) {
			
			for( var i = 0; i < _renderedLinks[ 'crosslinks' ].length; i++ ) {

				if( _linkExclusionHandler.isLinkExcluded( _renderedLinks[ 'crosslinks' ][ i ][ 'link' ] ) ) {
					continue;
				}

				var atom1 =  _renderedLinks[ 'crosslinks' ][ i ][ 'atom1' ];	// cA atom
				var atom2 =  _renderedLinks[ 'crosslinks' ][ i ][ 'atom2' ];	// cA atom
			
				response += "AtomPair ";
				response += "CA " + atom1.residue().num() + atom1.residue().chain().name() + " ";
				response += "CA " + atom2.residue().num() + atom2.residue().chain().name() + " ";

				response += "HARMONIC NUM1 NUM2 NUM3\n";
			}
		}
		
		if( _renderedLinks[ 'looplinks' ] && _renderedLinks[ 'looplinks' ].length > 0 ) {
					
			for( var i = 0; i < _renderedLinks[ 'looplinks' ].length; i++ ) {

				if( _linkExclusionHandler.isLinkExcluded( _renderedLinks[ 'looplinks' ][ i ][ 'link' ] ) ) {
					continue;
				}

				var atom1 =  _renderedLinks[ 'looplinks' ][ i ][ 'atom1' ];	// cA atom
				var atom2 =  _renderedLinks[ 'looplinks' ][ i ][ 'atom2' ];	// cA atom
			
				response += "AtomPair ";
				response += "CA " + atom1.residue().num() + atom1.residue().chain().name() + " ";
				response += "CA " + atom2.residue().num() + atom2.residue().chain().name() + " ";

				response += "HARMONIC NUM1 NUM2 NUM3\n";
			}
			
		}
		
		if( response == "" ) { return; }
		
		downloadStringAsFile( "rosetta-constraints.txt", "text/plain", response );
	};


	this_OfOutermostObjectOfClass.downloadShownUDRLinks = function() {
		var response = "";
		
		if( _renderedLinks[ 'crosslinks' ] && _renderedLinks[ 'crosslinks' ].length > 0 ) {
			
			for( var i = 0; i < _renderedLinks[ 'crosslinks' ].length; i++ ) {
				var link = _renderedLinks[ 'crosslinks' ][ i ][ 'link' ];

				// skip hidden links
				if( _linkExclusionHandler.isCrosslinkExcluded( link.protein1, link.position1, link.protein2, link.position2 ) ) {
					continue;
				}


				var chain1 = _renderedLinks[ 'crosslinks' ][ i ][ 'atom1' ].residue().chain().name();
				var chain2 = _renderedLinks[ 'crosslinks' ][ i ][ 'atom2' ].residue().chain().name();

				var protein1 = _proteinNames[ link[ 'protein1' ] ];
				var protein2 = _proteinNames[ link[ 'protein2' ] ];
				
				var position1 = link[ 'position1' ];
				var position2 = link[ 'position2' ];
				
				var distance = link[ 'length' ];
				
				response += "crosslink\t" + chain1 + "\t" + protein1 + "\t" + position1 + "\t";
				response += chain2 + "\t" + protein2 + "\t" + position2 + "\t" + distance + "\n";
			}
		}
		
		if( _renderedLinks[ 'looplinks' ] && _renderedLinks[ 'looplinks' ].length > 0 ) {
					
			for( var i = 0; i < _renderedLinks[ 'looplinks' ].length; i++ ) {
				var link = _renderedLinks[ 'looplinks' ][ i ][ 'link' ];

				// skip hidden links
				if( _linkExclusionHandler.isLooplinkExcluded( link.protein1, link.position1, link.position2 ) ) {
					continue;
				}

				var chain1 = _renderedLinks[ 'looplinks' ][ i ][ 'atom1' ].residue().chain().name();
				var chain2 = _renderedLinks[ 'looplinks' ][ i ][ 'atom2' ].residue().chain().name();

				var protein1 = _proteinNames[ link[ 'protein1' ] ];
				
				var position1 = link[ 'position1' ];
				var position2 = link[ 'position2' ];
				
				var distance = link[ 'length' ];
				
				response += "looplink\t" + chain1 + "\t" + protein1 + "\t" + position1 + "\t";
				response += chain2 + "\t" + protein1 + "\t" + position2 + "\t" + distance + "\n";
			}
			
		}
		
		if( response == "" ) { return; }
		
		downloadStringAsFile( "all-shown-udrs.txt", "text/plain", response );
	};

	/**
	 * Get an array of currently-visible protein IDs
	 */
	var getVisibleProteinsOnStructure = function() {
		
		var visibleProteins = new Array();
		
		var visibleChainMap = getVisibleChains();
		if( !visibleChainMap ) { return visibleProteins; }
		
		var visibleChains = Object.keys( visibleChainMap );
		
		for( var i = 0; i < visibleChains.length; i++ ) {
			
			var visibleProteinsForChain = visibleChainMap[ visibleChains[ i ] ];
			if( !visibleProteinsForChain || visibleProteinsForChain.length < 1 ) { continue; }
			
			for( var j = 0; j < visibleProteinsForChain.length; j++ ) {
				
				visibleProteins.push( visibleProteinsForChain[ j ] );
			}
			
		}
		
		return visibleProteins;
	};

	/**
	 * Return true if the sequences for all currently-visible proteins are already loaded
	 * False if not
	 */
	var allVisibleSequencesAreLoaded = function() {
		
		if( !_proteinSequences ) { return false; }
		if( _proteinSequences.length < 1 ) { return false; }
		
		var visibleProteins = getVisibleProteinsOnStructure();
		
		for( var i = 0; i < visibleProteins.length; i++ ) {
			if( !_proteinSequences[ visibleProteins[ i ] ] ) { return false; }
		}
		
		return true;
	};

	/**
	 * Load all sequences for all visible proteins. 
	 */
	var loadSequencesForVisibleProteins = function( doRedrawReport ) {
		var visibleProteins = getVisibleProteinsOnStructure();
		var proteinIdsToGetSequence = { };

		for( var i = 0; i < visibleProteins.length; i++ ) {
			if ( _proteinSequences == undefined || !( visibleProteins[ i ] in _proteinSequences ) ) {
				proteinIdsToGetSequence[ visibleProteins[ i ] ] = 1;
			}
		}
		
		var proteinArray = Object.keys( proteinIdsToGetSequence );
		if ( proteinArray.length > 0 ) {
			return loadProteinSequencesForProteins( proteinArray, doRedrawReport );
		}
		
		if( doRedrawReport ) {
			redrawDistanceReport();
		}
	};

	/**
	 * Return true if the distance report is currently visible, false if not
	 */
	var isDistanceReportVisible = function() {
		var $distanceReportDiv = $( '#distance-report-div' );
		if( !$distanceReportDiv || $distanceReportDiv.length < 1 ) { return false; }
		
		return $distanceReportDiv.is(":visible"); 
	};

	/**
	 * Regenerate and redraw the distance report
	 */
	var redrawDistanceReport = function( ) {

		if( !isDistanceReportVisible() ) { return; }
		
		console.log( "Redrawing distance report." );
		
		if( !allVisibleSequencesAreLoaded() ) {
			return loadSequencesForVisibleProteins( true );
		}
		
		var UDRDataObject = calculateNumUDRs();					// total number of UDRs among selected proteins

		var $distanceReportDiv = $( '#distance-report-div' );
		$distanceReportDiv.empty();
		
		
		var html = "<div style=\"margin-top:10px;\">";
			
		html += "<div style=\"font-size:14pt;\">Total UDRs: " + UDRDataObject.totalUnique + " (" + UDRDataObject.totalUniqueMappable + " mappable)</div>";
		html += "<div style=\"font-size:12pt;margin-left:20px;\">Crosslink UDRs: " + UDRDataObject.crosslinkTotal + " (" + UDRDataObject.crosslinkTotalMappable + " mappable)</div>";
		html += "<div style=\"font-size:12pt;margin-left:20px;\">Looplink UDRs: " + UDRDataObject.looplinkTotal + " (" + UDRDataObject.looplinkTotalMappable + " mappable)</div>";
		
		html += "</div>\n";
		
		
		
		
		html += "<div style=\"margin-top:15px;\">";
		html += "<div style=\"font-size:14pt;\">Shown links <= <input id=\"distance-cutoff-report-field\" type=\"text\" style=\"width:2em;font-size:12pt;\" ";
		
		var distance = getDistanceReportCutoffFromHash();
		if( !distance ) { distance = 35; }
		
		html += "value=\"" + distance + "\">&Aring;: ";
		html += "<span id=\"total-links-meeting-cutoff-val\">0</span> / <span id=\"total-links-val\">0</span></div>\n";
		html += "<div id=\"distance-cutoff-report-text\"></div>\n";

		html += "<div style=\"font-size:14pt;margin-top:15px;\">P-value: <span style=\"font-size:12pt;\" id=\"p-value-display\"></span></div>";
		html += "<div style=\"font-size:8pt;\" id=\"p-value-legend\"></div>";

		html += "<div style=\"font-size:14pt;margin-top:15px;\">Distance Density Plot:</div>";
		html += "<div id=\"svg-download\"></div>";

		html += "<div id=\"distance-density-plot\" style=\"margin-top:-5px;\">\n";
		html += "</div>";

		html += "<div id=\"shown-crosslinks-text\"></div>\n";
		html += "<div id=\"shown-looplinks-text\"></div>\n";
		html += "</div>";


		html += "<div style=\"font-size:14pt;margin-top:15px;\">Download reports:</div>";
		
		html += "<div style=\"font-size:12pt;margin-left:20px;\"><a href=\"javascript:\" onclick=\"window.structurePagePrimaryRootCodeObject.downloadProteinToPDBMap()\">Protein Position to PDB Mapping</a></div>";

		if( _projectSearchIds.length === 1 ) {
			html += "<div style=\"font-size:12pt;margin-left:20px;\"><a href=\"javascript:\" onclick=\"window.structurePagePrimaryRootCodeObject.downloadDetailedUDRReport()\">All shown UDRs</a></div>";
		} else {
			html += "<div style=\"font-size:12pt;margin-left:20px;\"><a href=\"javascript:\" onclick=\"window.structurePagePrimaryRootCodeObject.downloadShownUDRLinks()\">All shown UDRs</a></div>";
		}
		
		html += "<div style=\"font-size:12pt;margin-left:20px;\"><a href=\"javascript:\" onclick=\"window.structurePagePrimaryRootCodeObject.downloadPSMsForAllShownUDRLinks()\">PSMs for all shown UDRs</a></div>";

		
		html += "<div style=\"font-size:12pt;margin-left:20px;\"><a href=\"javascript:\" onclick=\"window.structurePagePrimaryRootCodeObject.downloadUDRsAsProteinPairs(0)\">All possible UDRs (all possible points on structure)</a></div>";
		html += "<div style=\"font-size:12pt;margin-left:20px;\"><a href=\"javascript:\" onclick=\"window.structurePagePrimaryRootCodeObject.downloadUDRsAsProteinPairs(1)\">All possible UDRs (shortest-only)</a></div>";
		html += "<div style=\"font-size:12pt;margin-left:20px;\"><a href=\"javascript:\" onclick=\"window.structurePagePrimaryRootCodeObject.beginSkylinePeptideReport()\">All theoretical peptides for all possible UDRs (Skyline format)</a></div>";
		html += "<div style=\"font-size:12pt;margin-left:20px;\"><a href=\"javascript:\" onclick=\"window.structurePagePrimaryRootCodeObject.downloadShownUDRsForRosetta()\">Download Rosetta Constraints File</a></div>";
		
		$distanceReportDiv.html( html );
		
		$( '#distance-cutoff-report-field' ).on('input',function(e){
			updateURLHash( false );
			updateDistanceCutoffReport();
			updatePValue();
		});

		updateDistanceCutoffReport();
		updateShownLinks();
		updateDensityPlotAndPValue();

	};

	const updateDensityPlotAndPValue = function() {

		const densityPlotSelector = "#distance-density-plot";

		const onlyShortest = getShowUniqueUDRs();

		$(densityPlotSelector).empty();

		const visibleProteins = getVisibleProteins();
		const visibleProteinsList = Object.keys( visibleProteins );
		if( !visibleProteinsList || !visibleProteinsList.length ) { return; }

		const selectedPDBFile = getSelectedPDBFile();

		const linkablePositionDataPromise = _linkablePositionDataManager.getLinkablePositionData( {
			visibleProteinList: visibleProteinsList,
			pdbFileName: selectedPDBFile.filename,
			projectSearchIds: _projectSearchIds
		});

		linkablePositionDataPromise.then( (data) => {

			// update the density plot
			DensityPlot.loadAndShowDensityPlot({
				linkablePositionData : data,
				divToUpdateSelector : densityPlotSelector,
				visibleProteinsMap : visibleProteins,
				onlyShortest : onlyShortest,
				alignments : _ALIGNMENTS,
				structure : _STRUCTURE,
				renderedLinks : _renderedLinks
			});

			PValueUtils.updatePValueDisplay({
				linkablePositionData : data,
				visibleProteinsMap : visibleProteins,
				onlyShortest : onlyShortest,
				alignments : _ALIGNMENTS,
				structure : _STRUCTURE,
				renderedLinks : _renderedLinks
			});

		});
	};

	const updatePValue = function() {

		const onlyShortest = getShowUniqueUDRs();

		const visibleProteins = getVisibleProteins();
		const visibleProteinsList = Object.keys( visibleProteins );
		if( !visibleProteinsList || !visibleProteinsList.length ) { return; }

		const selectedPDBFile = getSelectedPDBFile();

		const linkablePositionDataPromise = _linkablePositionDataManager.getLinkablePositionData( {
			visibleProteinList: visibleProteinsList,
			pdbFileName: selectedPDBFile.filename,
			projectSearchIds: _projectSearchIds
		});

		linkablePositionDataPromise.then( (data) => {

			PValueUtils.updatePValueDisplay({
				linkablePositionData : data,
				visibleProteinsMap : visibleProteins,
				onlyShortest : onlyShortest,
				alignments : _ALIGNMENTS,
				structure : _STRUCTURE,
				renderedLinks : _renderedLinks
			});

		});
	};

	var updateShownLinks = function () {

		updateShownCrosslinks();
		updateShownLooplinks();

	};


	var updateShownCrosslinksHeader = function() {

		var exclusionCount = _linkExclusionHandler.getExcludedRenderedCrosslinkCount( _renderedLinks[ 'crosslinks' ] );
		var $headerDiv = $( 'div#shown-crosslinks-header' );

		var html = "Shown Crosslinks:"

		if( exclusionCount !== 0 ) {

			html += " <span style=\"font-size:12pt;\">(" + exclusionCount + " hidden by user)</span>";

		}

		$headerDiv.html( html );
	};

	var updateShownCrosslinks = function() {

		var $shownCrosslinksDiv = $( '#shown-crosslinks-text' );
		if( !$shownCrosslinksDiv || $shownCrosslinksDiv.length < 1 ) { return; }

		var html = "<div id=\"shown-crosslinks-header\" style=\"font-size:14pt;margin-top:15px;\">Shown Crosslinks:</div>";

		if( _renderedLinks[ 'crosslinks' ] && _renderedLinks[ 'crosslinks' ].length > 0 ) {

			html += "<table style=\"margin-left:10px;\">\n";
			html += "<tr><td style=\"width:20px;\">&nbsp;</td><td style=\"width:180px;font-weight:bold;\">Protein (Pos)</td><td style=\"width:180px;font-weight:bold;\">Protein (Pos)</td><td style=\"width:100px;font-weight:bold;\">Distance (&Aring;)</td></tr>";


			for( var i = 0; i < _renderedLinks[ 'crosslinks' ].length; i++ ) {
				var link = _renderedLinks[ 'crosslinks' ][ i ][ 'link' ];

				var color = _linkColorHandler.getLinkColor( link, 'rgb' );
				var rgbaString = "rgba(" + color.r + "," + color.g + "," + color.b + ",0.15)";

				var isLinkExcluded = _linkExclusionHandler.isCrosslinkExcluded( link.protein1, link.position1, link.protein2, link.position2 );

				html += "<tr class=\" reported_crosslink_jq tool_tip_attached_jq " + _linkExclusionHandler.getClassNameForLink( link ) + "\" data-crosslink-index=\"" + i + "\" "
					+ " data-tooltip=\"Click for Details\""
					+ " style=\"cursor: pointer; background-color:" + rgbaString + ";"
					+ "opacity:" + _linkExclusionHandler.getOpacityForLinkRow( link ) + "\">";

				html += "<td style=\"width:20px;background-color:white;\"><span class=\"" + _linkExclusionHandler.getClassNameForLink( link ) + "\" id=\"cross-link-show-toggle-index-" + i + "\">"
					+ _linkExclusionHandler.getHTMLForLinkToggleLink( link )
					+ "</span></td>";

				html += "<td style=\"width:180px;\">" + _proteinNames[ link.protein1 ] + " (" + link.position1 + ")</td>";
				html += "<td style=\"width:180px;\">" + _proteinNames[ link.protein2 ] + " (" + link.position2 + ")</td>";
				html += "<td style=\"width:100px;\">" + link.length.toFixed( 1 ) + "</td>";


				html += "</tr>";

			}
			html += "</table>\n";
		} else {
			html += "<div style=\"margin-left:20px;\">No crosslinks currently shown.</div>\n";
		}

		$shownCrosslinksDiv.html( html );
		updateShownCrosslinksHeader();

		var $reported_crosslink_jq = $( '.reported_crosslink_jq' );

		$reported_crosslink_jq.click( function( e ) {

			try {

				var params = {
					psmPeptideCutoffsRootObject : _psmPeptideCutoffsRootObjectStorage.getPsmPeptideCutoffsRootObject(),
					removeNonUniquePSMs : _removeNonUniquePSMs
				};

				var index = $(e.currentTarget ).attr( 'data-crosslink-index' );
				var link = _renderedLinks[ 'crosslinks' ][ index ];
				if( !link ) { return; }

				getCrosslinkDataForSpecificLinkInGraph( params, link.link );

			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});

		addToolTips( $shownCrosslinksDiv );

		// add click handlers for toggling visibility of links
		_linkExclusionHandler.addClickHandlerToCrosslinkToggles( _renderedLinks, drawCrosslinks_noReportRedraw, updateURLHash );

	}


	var updateShownLooplinksHeader = function() {

		var exclusionCount =  _linkExclusionHandler.getExcludedRenderedLooplinkCount( _renderedLinks[ 'looplinks' ] );
		var $headerDiv = $( 'div#shown-looplinks-header' );

		var html = "Shown Looplinks:"

		if( exclusionCount !== 0 ) {

			html += " <span style=\"font-size:12pt;\">(" + exclusionCount + " hidden by user)</span>";

		}

		$headerDiv.html( html );
	}

	var updateShownLooplinks = function() {

		var $shownLooplinksDiv = $( '#shown-looplinks-text' );
		if( !$shownLooplinksDiv || $shownLooplinksDiv.length < 1 ) { return; }


		var html = "<div id=\"shown-looplinks-header\" style=\"font-size:14pt;margin-top:15px;\">Shown Looplinks:</div>\n";

		if( _renderedLinks[ 'looplinks' ] && _renderedLinks[ 'looplinks' ].length > 0 ) {

			html += "<table style=\"margin-left:10px;\">\n";
			html += "<tr><td style=\"width:20px;\">&nbsp;</td><td style=\"width:180px;font-weight:bold;\">Protein (Pos, Pos)</td><td style=\"width:100px;font-weight:bold;\">Distance (&Aring;)</td></tr>";


			for( var i = 0; i < _renderedLinks[ 'looplinks' ].length; i++ ) {
				var link = _renderedLinks[ 'looplinks' ][ i ][ 'link' ];

				var color = _linkColorHandler.getLinkColor( link, 'rgb' );
				var rgbaString = "rgba(" + color.r + "," + color.g + "," + color.b + ",0.15)";

				html += "<tr class=\" reported_looplink_jq  tool_tip_attached_jq " + _linkExclusionHandler.getClassNameForLink( link )  + "\" data-looplink-index=\"" + i + "\" "
					+ " data-tooltip=\"Click for Details\""
					+ " style=\"cursor: pointer; background-color:" + rgbaString + ";"
					+ "opacity:" + _linkExclusionHandler.getOpacityForLinkRow( link ) + ";\">";

				html += "<td style=\"width:20px;background-color:white;\"><span class=\"" + _linkExclusionHandler.getClassNameForLink( link )  + "\" id=\"loop-link-show-toggle-index-" + i + "\">"
					+ _linkExclusionHandler.getHTMLForLinkToggleLink( link )
					+ "</span></td>";

				html += "<td style=\"width:180px;\">" + _proteinNames[ link.protein1 ] + " (" + link.position1 + ", " + link.position2 + ")</td>";
				html += "<td style=\"width:100px;\">" + link.length.toFixed( 1 ) + "</td>";
				html += "</tr>\n";

			}

			html += "</table>\n";
		} else {
			html += "<div style=\"margin-left:20px;\">No looplinks currently shown.</div>\n";
		}


		$shownLooplinksDiv.html( html );
		updateShownLooplinksHeader();

		$( '.reported_looplink_jq' ).click( function( e ) {

			try {

				var params = {
					psmPeptideCutoffsRootObject : _psmPeptideCutoffsRootObjectStorage.getPsmPeptideCutoffsRootObject(),
					removeNonUniquePSMs : _removeNonUniquePSMs
				};


				var index = $(e.currentTarget ).attr( 'data-looplink-index' );
				var link = _renderedLinks[ 'looplinks' ][ index ];
				if( !link ) { return; }

				getLooplinkDataForSpecificLinkInGraph( params, link.link );

			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});


		addToolTips( $shownLooplinksDiv );

		// add click handlers for toggling visibility of links
		_linkExclusionHandler.addClickHandlerToLooplinkToggles( _renderedLinks, drawLooplinks_noReportRedraw, updateURLHash );
	}


	var getDistanceReportCutoffFromHash = function() {
		var json = getJsonFromHash();
		if( !json ) { return 0; }
		
		if( !'distance-report-cutoff' in json ) { return 0; }
		
		return parseInt( json[ 'distance-report-cutoff' ] );
	};



	/**
	 * Update the distance summary report given the cutoff present on the page
	 */
	var updateDistanceCutoffReport = function() {
		
		var $distanceCutoffReportTextDiv = $( '#distance-cutoff-report-text' );
		if( !$distanceCutoffReportTextDiv ) { return; }
		
		var $distanceCutoffReportField = $( '#distance-cutoff-report-field' );
		if( !$distanceCutoffReportField ) { return; }
		
		var cutoff = $distanceCutoffReportField.val();
		if( isNaN(cutoff) ) { return; }
		
		var totalRenderedCrosslinks = 0;
		var totalRenderedLooplinks = 0;
		
		var totalRenderedCrosslinksMeetingCutoff = 0;
		var totalRenderedLooplinksMeetingCutoff = 0;
		
		var totalRenderedLinks = 0;
		var totalRenderedLinksMeetingCutoff = 0;
		
		var uniquePairs = { };
		

		if( _renderedLinks[ 'crosslinks' ] ) {

			for( var i = 0; i < _renderedLinks[ 'crosslinks' ].length; i++ ) {

				var link = _renderedLinks[ 'crosslinks' ][ i ][ 'link' ];

				// skip this link if it's hidden.
				if( _linkExclusionHandler.isCrosslinkExcluded( link.protein1, link.position1, link.protein2, link.position2 ) ) {
					continue;
				}

				var atom1 = _renderedLinks[ 'crosslinks' ][ i ][ 'atom1' ];
				var atom2 = _renderedLinks[ 'crosslinks' ][ i ][ 'atom2' ];
				
				var distanceId = atom1.residue().chain().name() + "-" + atom1.residue().num() + "-" + atom2.residue().chain().name() + "-" + atom2.residue().num();
				uniquePairs[ distanceId ] = StructureUtils.calculateDistance( atom1.pos(), atom2.pos() );

				totalRenderedCrosslinks++;

				if( StructureUtils.calculateDistance( atom1.pos(), atom2.pos() ) <= cutoff ) {
					totalRenderedCrosslinksMeetingCutoff++;
				}
			}
			
		}
		
		if( _renderedLinks[ 'looplinks' ] ) {

			for( var i = 0; i < _renderedLinks[ 'looplinks' ].length; i++ ) {

				var link = _renderedLinks[ 'looplinks' ][ i ][ 'link' ];

				// skip this link if it's hidden.
				if( _linkExclusionHandler.isLooplinkExcluded( link.protein1, link.position1, link.position2 ) ) {
					continue;
				}

				var atom1 = _renderedLinks[ 'looplinks' ][ i ][ 'atom1' ];
				var atom2 = _renderedLinks[ 'looplinks' ][ i ][ 'atom2' ];
				
				var distanceId = atom1.residue().chain().name() + "-" + atom1.residue().num() + "-" + atom2.residue().chain().name() + "-" + atom2.residue().num();
				uniquePairs[ distanceId ] = StructureUtils.calculateDistance( atom1.pos(), atom2.pos() );

				totalRenderedLooplinks++;

				if( StructureUtils.calculateDistance( atom1.pos(), atom2.pos() ) <= cutoff ) {
					totalRenderedLooplinksMeetingCutoff++;
				}
			}
			
		}
		
		var uniquePairIds = Object.keys( uniquePairs );
		if( uniquePairIds && uniquePairIds.length > 0 ) {
			totalRenderedLinks = uniquePairIds.length;
			
			for( var i = 0; i < uniquePairIds.length; i++ ) {
				var uniqueId = uniquePairIds[ i ];
				if( uniquePairs[ uniqueId ] <= cutoff ) {
					totalRenderedLinksMeetingCutoff++;
				}
			}
			
		}
		
		$( '#total-links-meeting-cutoff-val' ).html( totalRenderedLinksMeetingCutoff );
		$( '#total-links-val' ).html( totalRenderedLinks );
		
		
		var html = "<div style=\"font-size:12pt;margin-left:20px;\">Crosslinks: " + totalRenderedCrosslinksMeetingCutoff + " / " + totalRenderedCrosslinks + "</div>";
		html += "<div style=\"font-size:12pt;margin-left:20px;\">Looplinks: " + totalRenderedLooplinksMeetingCutoff + " / " + totalRenderedLooplinks + "</div>";

		$distanceCutoffReportTextDiv.empty();
		$distanceCutoffReportTextDiv.html( html );
	};

	// calculate the number of UDRs for currently selected proteins
	var calculateNumUDRs = function() {
		
		var UDRDataObject = { };						// what we're returning
		
		UDRDataObject.crosslinkTotal = 0;				// number of crosslink UDRs
		UDRDataObject.looplinkTotal = 0;				// number of looplinks UDRs
		UDRDataObject.totalUnique = 0;					// number of combined, unique UDRs
		
		UDRDataObject.crosslinkTotalMappable = 0;		// number of mappable crosslink UDRs
		UDRDataObject.looplinkTotalMappable = 0;		// number of looplinks UDRs
		UDRDataObject.totalUniqueMappable = 0;			// number of combined, unique UDRs
		
		var UDRsCounted = { };
			
		var proteinChainMap = getVisibleProteins();
		if( !proteinChainMap ) { return UDRDataObject; }
		
		var proteins = Object.keys( proteinChainMap );
		if( proteins.length == 0 ) { return UDRDataObject; }
		
		for( var i = 0; i < proteins.length; i++ ) {
			var protein1 = proteins[ i ];
			
			for( var j = 0; j < proteins.length; j++ ) {
				var protein2 = proteins[ j ];
				
				// ensure we only count a UDR once
				if( protein1 > protein2 ) { continue; }
				
				// loop over crosslinks
				if( _proteinLinkPositions && 
				protein1 in _proteinLinkPositions &&
				protein2 in _proteinLinkPositions[ protein1 ] ) {
					
					var froms = Object.keys( _proteinLinkPositions[ protein1 ][ protein2 ] );
					for( var fromsIndex = 0; fromsIndex < froms.length; fromsIndex++ ) {
						var from = froms[ fromsIndex ];
						
						var tos = Object.keys( _proteinLinkPositions[ protein1 ][ protein2 ][ from ] );
						for( var tosIndex = 0; tosIndex < tos.length; tosIndex++ ) {
							var to = tos[ tosIndex ];
							
							if( protein1 == protein2 ) {
								if( to > from ) { continue; }		// only consider UDRs once
								
								if( to == from ) {
									
									if( countCrosslinkToSelfAsUDR( protein1 ) ) {
										console.log( "Counting " + protein1 + " (" + to + ") to " + protein1 + " (" + to + ") -- protein appears more than once in structure." );								
									} else {
										console.log( "NOT counting " + protein1 + " (" + to + ") to " + protein1 + " (" + to + ") -- protein appears once in structure." );								
										continue;
									}
									
									
									//continue;		// do not currently count positions in a protein to the same position in that protein
								}

							}
							
							UDRDataObject.crosslinkTotal++;
							UDRDataObject.totalUnique++;
							
							if( proteinPositionIsMappable( protein1, from ) && proteinPositionIsMappable( protein2, to ) ) {
								UDRDataObject.crosslinkTotalMappable++;
								UDRDataObject.totalUniqueMappable++;
							}
							
							
							// add this to the list of UDRs we've counted so we can calculated total unique UDRs among cross- and loop-links
							if( !( protein1 in UDRsCounted ) ) { UDRsCounted[ protein1 ] = { }; }
							if( !( protein2 in UDRsCounted[ protein1 ] ) ) { UDRsCounted[ protein1 ][ protein2 ] = { }; }
							if( !( from in UDRsCounted[ protein1 ][ protein2 ] ) ) { UDRsCounted[ protein1 ][ protein2 ][ from ] = { }; };
							if( !( to in UDRsCounted[ protein1 ][ protein2 ][ from ] ) ) { UDRsCounted[ protein1 ][ protein2 ][ from ][ to ] = { }; };

						} // end looping over tos
						
					} // end looping over froms
					
				} //end looping over crosslink data

			} //end looping over protein2s

			
			// loop over looplinks
			if( _proteinLooplinkPositions && 
			protein1 in _proteinLooplinkPositions ) {
						
				var froms = Object.keys( _proteinLooplinkPositions[ protein1 ][ protein1 ] );
				for( var fromsIndex = 0; fromsIndex < froms.length; fromsIndex++ ) {
					var from = froms[ fromsIndex ];
							
					var tos = Object.keys( _proteinLooplinkPositions[ protein1 ][ protein1 ][ from ] );
					for( var tosIndex = 0; tosIndex < tos.length; tosIndex++ ) {
						var to = tos[ tosIndex ];
						
						if( to < from ) { continue; }		// only consider UDRs once
									
						if( to == from ) {										
							continue;		// do not  count looplink positions in a protein to the same position in that protein
						}

								
						UDRDataObject.looplinkTotal++;
						var mappable = false;
						
						if( proteinPositionIsMappable( protein1, from ) && proteinPositionIsMappable( protein1, to ) ) {
							mappable = true;
							UDRDataObject.looplinkTotalMappable++;
						}
								
						// if this wasn't already counted as a crosslink, add increment unique udr count
						if( protein1 in UDRsCounted &&
								protein1 in UDRsCounted[ protein1 ] &&
								from in UDRsCounted[ protein1 ][ protein1 ] &&
								to in UDRsCounted[ protein1 ][ protein1 ][ from ] ) {

						} else if( protein1 in UDRsCounted &&
								protein1 in UDRsCounted[ protein1 ] &&
								to in UDRsCounted[ protein1 ][ protein1 ] &&
								from in UDRsCounted[ protein1 ][ protein1 ][ to ] ) {
							
						} else {
							UDRDataObject.totalUnique++;
							
							if( mappable ) {
								UDRDataObject.totalUniqueMappable++;
							}
						}

					} // end looping over tos
							
				} // end looping over froms
						
			} //end looping over looplink data
			
			
		} //end looping over protein1s
		
		
		return UDRDataObject;	
	};

	/**
	 * Returns true if the given position in the given protein is visible given the
	 * currently visible protein alignments
	 */
	var proteinPositionIsMappable = function( protein, position ) {
		var visibleChains = getVisibleChainsForProtein( protein );

		for( var i = 0; i < visibleChains.length; i++ ) {
			if( StructureAlignmentUtils.findPDBResidueFromAlignment( protein, position, visibleChains[ i ], _ALIGNMENTS ) ) { return true; }
		}


		return false;
	};

	/**
	 * Get all chains, as an Array, where the suppied protein is currently visible
	 */
	var getVisibleChainsForProtein = function( protein ) {
		var visibleChainsForProtein = new Array();

		var visibleChainMap = getVisibleChains();
		if( !visibleChainMap ) { return visibleChainsForProtein; }
		
		var visibleChains = Object.keys( visibleChainMap );
		
		for( var i = 0; i < visibleChains.length; i++ ) {
			
			var visibleProteins = visibleChainMap[ visibleChains[ i ] ];
			if( !visibleProteins || visibleProteins.length < 1 ) { continue; }
			
			for( var j = 0; j < visibleProteins.length; j++ ) {
				
				if( protein == visibleProteins[ j ] ) {
					visibleChainsForProtein.push( visibleChains[ i ] );
					break;	// stop iterating over this chain's proteins
				}
			}
			
		}
		
		return visibleChainsForProtein;	
	};


	var showDistanceReportPanel = function( skipHashUpdate) {
		$('#pdb-info-nav-chain-choice').hide();
		$('#chain-list-div').hide();
		
		$('#pdb-info-nav-report-choice').show();
		$('#distance-report-div').show();
		
		if( !skipHashUpdate ) {	updateURLHash( false ); }
		redrawDistanceReport();
	};


	var showChainMapPanel = function() {
		$('#pdb-info-nav-chain-choice').show();
		$('#chain-list-div').show();
		
		$('#pdb-info-nav-report-choice').hide();
		
		//$('#distance-report-div').empty();		// commented this out to preserve the user-selected cutoff when toggling between report and chain list
		$('#distance-report-div').hide();
		
		updateURLHash( false );
	};


	/**
	 * Given a protein and position, and given the currently visible protein alignments,
	 * should we count a link from a protein and position to the same protein and position
	 * as a UDR? This is true if that protein is visible in multiple alignments, false
	 * if not
	 * 
	 * protein is the protein id
	 * 
	 */
	var countCrosslinkToSelfAsUDR = function( protein ) {
			
		var visibleChainMap = getVisibleChains();
		var visibleChains = Object.keys( visibleChainMap );
		
		var foundProtein = false;
			
		// if fewer than 2 chains are visible, we can stop trying
		if( !visibleChains || visibleChains.length < 2 ) { return false; }
			
		for( var i = 0; i < visibleChains.length; i++ ) {
			
			if( visibleChains[ i ] && visibleChains[ i ].length > 0 ) {
				
				var chain = visibleChains[ i ];
							
				for( var j = 0; j < visibleChainMap[ chain ].length; j++ ) {
									
					if( protein == visibleChainMap[ chain ][ j ] ) {
						if( foundProtein ) {
							return true;		// this protein is visible in more than 1 chain
						}
						
						foundProtein = true;
						break;	// break out of looping over proteins in this chain
					}
					
				}
				
			}
		}
		
		return false;
	};


	/**
	 * List the chains for the currently selected PDB file, and list the loaded alignments of proteins
	 * to those chains, and automatically check any of those proteins that should be checked
	 */
	var listChains = function( doDraw ) {

		console.log( "Calling listChains( " + doDraw + " )" );
		

		// selected PDB file
		var pdbFile = getSelectedPDBFile();
			
		if( isNaN( pdbFile.id ) || pdbFile.id == 0 ) {
			return;
		}
		
		var json = getJsonFromHash();
		
		var chains = _STRUCTURE.chains();
		var $chainsDiv = $( "#chain-list-div" );
		$chainsDiv.empty();

		var html = "<h2 style=\"display:inline;font-size:14pt;\">PDB File: " + pdbFile.name + "</h2>\n";
			
		if( _PDB_FILES[ pdbFile.id ][ 'canEdit' ] ) {
			html += " <span style=\"font-size:10pt;\"><a href=\"javascript:\" onclick=\"window.structurePagePrimaryRootCodeObject.confirmPDBFileDelete( this, " + pdbFile.id + ")\"><img id=\"delete-pdb-icon\" style=\"border-width:0px;margin-left:2px;max-width:15px;\" src=\"images/icon-delete-small.png\" /></a>\n";
		}
		
		html += " <span style=\"font-size:10pt;\"><a href=\"downloadPDBFile.do?id=" + pdbFile.id + "\" target=\"download_pdb_file\"><img id=\"download-pdb-icon\" style=\"border-width:0px;margin-left:2px;max-width:15px;\" src=\"images/icon-download-small.png\" /></a>\n";
		html += " <span style=\"font-size:10pt;\"><a href=\"javascript:\" onClick=\"window.structurePagePrimaryRootCodeObject.downloadChimeraScript()\" target=\"download_pdb_file\"><img id=\"chimera-icon\" style=\"border-width:0px;margin-left:2px;max-width:12px;\" src=\"images/chimera-logo.png\" /></a>\n";
		html += " <span style=\"font-size:10pt;\"><a href=\"javascript:\" onClick=\"window.structurePagePrimaryRootCodeObject.downloadPymolScript()\" target=\"download_pdb_file\"><img id=\"pymol-icon\" style=\"border-width:0px;margin-left:2px;max-width:12px;\" src=\"images/pymol-logo.png\" /></a>\n";

		html += "<script type=\"text/javascript\">\
					$(\"#chimera-icon\").qtip({ \
							content: {\
								text: \"Download Chimera script containing currently-shown links.\"\
							}\
						});\
					$(\"#pymol-icon\").qtip({ \
							content: {\
								text: \"Download Pymol script containing currently-shown links.\"\
							}\
						});\
					$(\"#download-pdb-icon\").qtip({ \
							content: {\
								text: \"Download PDB file.\"\
							}\
						});\
					$(\"#delete-pdb-icon\").qtip({ \
							content: {\
								text: \"Delete PDB file.\"\
							}\
						});\
					</script>";

		var $pdbTitleDiv = $( "#pdb-title-div" );
		$pdbTitleDiv.html( html );
			
		for( var i = 0; i < chains.length; i++ ) {
			
			var chainDisplayName = chains[ i ].name();
			if( chainDisplayName === "_" ) { chainDisplayName = "Default"; }

			html = "<div style=\"margin-top:10px;\" id=\"chain-" + chains[ i ].name() + "-div\">" +

				"<span style=\"" +
				"position:relative;" +
				"top:-2px;" +
				"margin-right:5px;" +
				"width:15px;" +
				"background-color:" + getRGBAForChain( chains[ i ].name() ) + ";" +
				"border-style:solid;" +
				"border-width:1px;" +
				"border-color:black;" +
				"\" class=\"chain-color-picker clickable\" data-chain-id=\"" + chains[ i ].name() + "\">&nbsp;&nbsp;&nbsp;</span>" +

				"<span style=\"font-size:14pt;\">Chain: " + chainDisplayName + "</span>\n";

			if( _PDB_FILES[ pdbFile.id ][ 'canEdit' ] ) {
				if( PDBChainIsProtein( chains[ i ].name() ) ) {
					html += "<a class=\"tool_tip_attached_jq\" data-tooltip=\"Map this chain's sequence to the sequence of a protein found in the search\" href=\"javascript:mapProtein( '" + chains[ i ].name() + "')\">[Map Protein]</a>";
				} else {
					html += "[Chain is not a peptide]";
				}
			}
			
			// list out the proteins we've aligned to this chain previously, which are also in this experiment
			html += "<div style=\"margin-left:20px;\">\n";
			
			if( _ALIGNMENTS[ chains[ i ].name() ] ) {
				
				for( var k = 0; k < _ALIGNMENTS[ chains[ i ].name() ].length; k++ ) {
					
					var alignment = _ALIGNMENTS[ chains[ i ].name() ][ k ];
					
					var proteinId = alignment.proteinSequenceVersionId;
					
					// limit the list to proteins in this experiment
					if( _proteinNames[ proteinId ] ) {
						html += "<span style=\"white-space:nowrap;margin-right:10px;\"><input ";
						
						if( HASH_PROPERTY_VISIBLE_CHAINS_WITH_PROTEIN_SEQUENCE_IDS in json ) {

							if ( k === 0 && Object.keys(json[HASH_PROPERTY_VISIBLE_CHAINS_WITH_PROTEIN_SEQUENCE_IDS]).length < 1) {
								html += "checked ";
							} else {

								var chainsElemName = chains[i].name();

								if (chainsElemName in json[HASH_PROPERTY_VISIBLE_CHAINS_WITH_PROTEIN_SEQUENCE_IDS]) {

									var prots = json[HASH_PROPERTY_VISIBLE_CHAINS_WITH_PROTEIN_SEQUENCE_IDS][chains[i].name()];

									if (prots.length > 0) {
										for (var protsIndex = 0; protsIndex < prots.length; protsIndex++) {
											if (prots[protsIndex] == proteinId) {
												html += "checked ";
											}
										}
									}
								}
							}
						}
						
						html += "data-chain=\"" + chains[ i ].name() + "\" onchange=\"proteinClicked()\" id=\"protein-checkbox-" + proteinId + "\" type=\"checkbox\" data-tooltip=\"Check to include the mapping of this protein to this chain of the PDB when rendering links\" class=\"tool_tip_attached_jq protein-checkbox\" value=\"" + proteinId + "\">" + _proteinNames[ proteinId ] + "</input>";

						if( _PDB_FILES[ pdbFile.id ][ 'canEdit' ] ) {
							html += "<img data-tooltip=\"Edit or view alignment of this protein's sequence from the FASTA file to the PDB chain sequence\" style=\"margin-left:2px;max-width:15px;\" src=\"images/icon-edit-small.png\" class='tool_tip_attached_jq clickable' onclick=\"window.structurePagePrimaryRootCodeObject.editAlignment('" + chains[ i ].name() + "'," + k + ")\" />";
						} else {
							html += "<img data-tooltip=\"View alignment of this protein's sequence from the FASTA file to the PDB chain sequence\" style=\"margin-left:2px;max-width:15px;\" src=\"images/icon-view-small.png\" class='tool_tip_attached_jq clickable' onclick=\"window.structurePagePrimaryRootCodeObject.editAlignment('" + chains[ i ].name() + "'," + k + ")\" />";
						}
						
						if( _PDB_FILES[ pdbFile.id ][ 'canEdit' ] ) {
							html += "<img data-tooltip=\"Remove the alignment of this protein from this chain of the PDB\" style=\"margin-left:2px;max-width:15px;\" src=\"images/icon-delete-small.png\" class='tool_tip_attached_jq clickable' onclick=\"window.structurePagePrimaryRootCodeObject.deleteAlignment( this, " + _ALIGNMENTS[ chains[ i ].name() ][ k ][ 'id' ] + ")\" />";
						}
						
						html += "</span>\n";
					}
				}
				
			}
			
			html += "</div>\n";
			html += "</div>";
			
			// add new HTML to DOM
			$chainsDiv.append( html );
		
			addToolTips( $chainsDiv );
			
			// add mouseover events for chains
			addChainMouseover( chains[ i ].name() );


		}

		// add color picker handlers
		addColorPickers( $chainsDiv );

		html = "<div class=\"clickable\" style=\"margin-top:15px\;color:#A55353;\">[Reset Colors]</div>";
		let $html = $(html);
		$html.click( function() {
			_backboneColorManager.resetColors();

			const rgbaArray = _backboneColorManager.getDefaultColor();
			$('.chain-color-picker').css('backgroundColor',
				"rgba(" + rgbaArray[ 0 ] + "," + rgbaArray[ 1 ] + "," + rgbaArray[ 2 ] + "," + rgbaArray[ 3 ] + ")");

			updateURLHash( false /* useSearchForm */ );
			drawStructure();
		});
		$chainsDiv.append( $html );


		if( doDraw ) {
			drawStructure();
		}
		
	};

	const addColorPickers = function( $chainsDiv ) {

		$chainsDiv.find( ".chain-color-picker" ).ColorPicker({
			color:"#ff0000",
			onSubmit: function(hsbColor, hexColor, rgbColor, htmlElement ) {

				// update block on page with selected color

				const $htmlElement = $( htmlElement );
				const chainName = $htmlElement.data( 'chain-id' );
				const rgbaArray = [ rgbColor.r, rgbColor.g, rgbColor.b, 1.0 ];

				$htmlElement.css('backgroundColor', '#' + hexColor); // set HTML block color on page
				//$htmlElement.data( 'backgroundHex', '#' + hexColor );
				$htmlElement.ColorPickerHide();

				_backboneColorManager.setChainColor( {chainName, color:rgbaArray } );

				updateURLHash( false /* useSearchForm */ );
				drawStructure();

			},
			onHide: function() {
				//  called when hidden
				// alert("On Hide");
			}
		});

	};

	const getRGBAForChain = function( chainName ) {
		const rgbaArray = _backboneColorManager.getChainColor( chainName );

		return "rgba(" + rgbaArray[ 0 ] + "," + rgbaArray[ 1 ] + "," + rgbaArray[ 2 ] + "," + rgbaArray[ 3 ] + ")";
	};

	var proteinClicked = function() {
		
		updateURLHash( false /* useSearchForm */ );
		
		if ( $( "input#show-coverage" ).is( ':checked' ) ) {
			drawStructure();
		} else {
			drawMeshesOnStructure();
		}
		
	};

	/**
	 * Handle drawing the custom mesh on structure, which includes everything 
	 * other than the structure itself. Works by assembling a data structure to
	 * pass into the actual function that draws the actual data on the structure.
	 * 
	 * @param callout If supplied, only this callout function will be called
	 *                If not supplied, all functions for drawing will be
	 *                called, if their respective checkboxes are checked.
	 * @returns
	 */
	var drawMeshesOnStructure = function( callout ) {

		_distanceReportData = { };
		
		// selected PDB file
		var pdbFile = getSelectedPDBFile();
			
		if( isNaN( pdbFile.id ) || pdbFile.id == 0 ) {
			return;
		}
		
		// ensure report is being shown if it should be shown -- should only really happen when page first loads and report should be shown
		var json = getJsonFromHash();
		if( !isDistanceReportVisible() && 'distance-report-visible' in json && json[ 'distance-report-visible' ] ) {
			showDistanceReportPanel( true );
		}
		
		
		// an associative array where key is the protein id and value is an array of chains that protein is visible in
		var proteins = getVisibleProteins();
		
		if( callout ) {
			callout( proteins );
		} else {
		
			if ( $( "input#show-crosslinks" ).is( ':checked' ) ) {
				drawCrosslinks( proteins );
			}
		
			if ( $( "input#show-monolinks" ).is( ':checked' ) ) {
				drawMonolinks( proteins );
			}
			
			if ( $( "input#show-looplinks" ).is( ':checked' ) ) {
				drawLooplinks( proteins );
			}

			if ( $( "input#show-linkable-positions" ).is( ':checked' ) ) {
				drawLinkableResidues( proteins );
			}
		}
	};

	var viewerClicked = function( picked, e ) {
		
		
		if( !picked ) { return; }
		if( !picked.target() ) { return; }
		if( !( 'type' in picked.target() ) ) { return; }
		var type = picked.target().type;
		
		var params = { 
				psmPeptideCutoffsRootObject : _psmPeptideCutoffsRootObjectStorage.getPsmPeptideCutoffsRootObject(),
				removeNonUniquePSMs : _removeNonUniquePSMs
		};
		
		if( type == 'monolink' ) {
			getMonolinkDataForSpecificLinkInGraph( params, picked.target() );
		} else if( type == 'looplink' ) {
			getLooplinkDataForSpecificLinkInGraph( params, picked.target() );
		} else if( type == 'crosslink' ) {
			getCrosslinkDataForSpecificLinkInGraph( params, picked.target() );
		} else {
			return;
		}
			
	};


	/**
	 * Get the proteins currently checked to be visible in the structure, coupled with which
	 * chains they are visible in.
	 * @returns Map of visible protein ids as the key and an array of chains as the value
	 */
	var getVisibleProteins = function() {
		
		// an associative array where key is the protein id and value is an array of chains that protein is visible in
		var proteins = { };
		
		$( ".protein-checkbox" ).each( function() {
			if( $( this ).prop( 'checked' ) ) {
				
				var pid = $( this ).val();
				var chain = $(this ).attr( 'data-chain' );
				
				if( !proteins[ pid ] ) {
					proteins[ pid ] = new Array();
				}
				
				proteins[ pid ].push( chain );
			}
		});
		
		return proteins;
	};

	/**
	 * Returns a map where the keys are chain IDs (e.g. "A") and the values are an array of
	 * protein IDs currently visible in that chain
	 * @returns
	 */
	var getVisibleChains = function() {
		var chains = { };
		
		$( ".protein-checkbox" ).each( function() {
			if( $( this ).prop( 'checked' ) ) {

				var chain = $(this ).attr( 'data-chain' );
				if( !chains[ chain ] ) {
					chains[ chain ] = [ ];
				}
				
				chains[ chain ].push( $( this ).val() );
			}
		});
		
		return chains;
	};

	var _CROSSLINKS_MESH;

	var drawCrosslinks_noReportRedraw = function( proteins ) {

		if( !proteins ) {
			proteins = getVisibleProteins();
		}

		drawCrosslinks_doRedraw( proteins );
		updateDistanceCutoffReport();
		updateShownCrosslinksHeader();

	}

	var drawCrosslinks = function( proteins ) {

		if( !proteins ) {
			proteins = getVisibleProteins();
		}

		drawCrosslinks_doRedraw( proteins );
		redrawDistanceReport();
	}

	var drawCrosslinks_doRedraw = function( proteins ) {
		
		if( !_proteinLinkPositions ) {
			loadCrosslinkData( true );
			return;
		}
		
		var shadeByCounts = false;
		if ( $( "input#shade-by-counts" ).is( ':checked' ) ) { shadeByCounts = true; }
		
		
		if( shadeByCounts && !( 'crosslink' in _linkPSMCounts ) ) {
			loadCrosslinkPSMCounts( true );
			return;
		}
		
		// blow this data away from report data object
		delete _distanceReportData[ 'shown-crosslinks' ];
		
		_renderedLinks.crosslinks = new Array();
		
		if( _CROSSLINKS_MESH ) {
			//_CROSSLINKS_MESH.hide();
			_VIEWER.hide( 'crosslinks' );
			_VIEWER.rm( 'crosslinks' );
		}
		_CROSSLINKS_MESH = _VIEWER.customMesh('crosslinks');
		
		var proteinIds = Object.keys( proteins );

		var distinctUDRs = { };		// if we're showing unique UDRs, keep track of the coordinates that yield the shortest distances for each distinct UDR
		var uniqueUDRs = getShowUniqueUDRs();
		
		for( var i = 0; i < proteinIds.length; i++ ) {
			
			var protein1 = proteinIds[ i ];
			if( !_proteinLinkPositions[ protein1 ] ) { continue; }
			
			
			for( var j = 0; j < proteinIds.length; j++ ) {
				
				var protein2 = proteinIds[ j ];
				
				// ensure we only do a combo of 2 proteins once
				if( protein2 < protein1 ) { continue; }
				if( !_proteinLinkPositions[ protein1 ][ protein2 ] ) { continue; }
				
				//console.log( "finding crosslinks to draw between " + protein1 + " and " + protein2 );
				
				var fromKeys = Object.keys( _proteinLinkPositions[ protein1 ][ protein2] );
				for( var ii = 0; ii < fromKeys.length; ii++ ) {
					var fromPosition = fromKeys[ ii ];
					
					// find and return the coords of the CA atoms present for this position in this protein in the visible chains
					var fromAtoms = StructureAlignmentUtils.findCAAtoms( protein1, fromPosition, proteins[ protein1 ], _ALIGNMENTS, _STRUCTURE );
					
					if( !fromAtoms || fromAtoms.length < 1 ) { continue; }
					
					var toKeys = Object.keys( _proteinLinkPositions[ protein1 ][ protein2 ][ fromPosition ] );
					
					
					for( var jj = 0; jj < toKeys.length; jj++ ) {
						var toPosition = toKeys[ jj ];


						// ensure we only consider a pair of positions once and we consider no zero-length crosslinks
						if( protein1 == protein2 ) {
							if( toPosition < fromPosition ) { continue; }	// ensure we only consider links within the same protein once
							
							// only consider links to the same position in the same protein if the protein appears multiple times in the structure
							if( toPosition == fromPosition ) {
							

								if( countCrosslinkToSelfAsUDR( protein1 ) ) {
									console.log( "Drawing " + protein1 + " (" + toPosition + ") to " + protein1 + " (" + toPosition + ") -- protein appears more than once in structure." );								
								} else {
									console.log( "NOT drawing " + protein1 + " (" + toPosition + ") to " + protein1 + " (" + toPosition + ") -- protein appears once in structure." );								
									continue;
								}

								
								//continue;		// never draw links from same position in protein to itself
								
							}
						}
						
						//console.log( "\tfrom " + fromPosition + " to " + toPosition );

						// find and return the coords of the CA atoms present for this position in this protein in the visible chains
						var toAtoms = StructureAlignmentUtils.findCAAtoms( protein2, toPosition, proteins[ protein2 ], _ALIGNMENTS, _STRUCTURE );
						
						if( !toAtoms || toAtoms.length < 1 ) { continue; }	

						for( var ci = 0; ci < fromAtoms.length; ci++ ) {

							var fromAtom = fromAtoms[ ci ];
							var fromCoord = fromAtom.pos();
							
							// find the shortest link from this atom
							var shortestPair = new Array();
							var shortestDistance = -1;
							
							for( var cj = 0; cj < toAtoms.length; cj++ ) {
								
								var toAtom = toAtoms[ cj ];
								var toCoord = toAtom.pos();
															
								var distance = StructureUtils.calculateDistance( fromCoord, toCoord );
								if( distance == 0 ) { continue; }
								
								if( shortestDistance == -1 || distance < shortestDistance ) {
									shortestDistance = distance;
									shortestPair = [ fromAtom, toAtom ];
								}
							}
							
							// draw the shortest link for this starting atom
							if( shortestDistance != -1 ) {

								var renderedLink = { };
								renderedLink.atom1 = shortestPair[ 0 ];
								renderedLink.atom2 = shortestPair[ 1 ];
								
								if( !uniqueUDRs ) {
									var link = { };
									link.type = 'crosslink';
									link.length = shortestDistance;
									link.protein1 = protein1;
									link.protein2 = protein2;
									link.position1 = fromPosition;
									link.position2 = toPosition;
									link.searchIds = _proteinLinkPositions[ protein1 ][ protein2 ][ fromPosition ][ toPosition ];
									
									if( shadeByCounts ) {
										link.psmCount = _linkPSMCounts[ 'crosslink' ][ protein1 ][ protein2 ][ fromPosition ][ toPosition ];
										
										if( !link.psmCount ) {
											console.log( "WARNING: Got 0 psms for link: " );
											console.log( link );
										}
									}

									// do not draw this link if it's being excluded
									if( !_linkExclusionHandler.isCrosslinkExcluded( protein1, fromPosition, protein2, toPosition ) ) {
										_CROSSLINKS_MESH.addTube( shortestPair[ 0 ].pos(), shortestPair[ 1 ].pos(), 0.6, { cap: true, color : _linkColorHandler.getLinkColor( link, 'pvrgba' ), userData: link });
									}

									renderedLink.link = link;
									
									_renderedLinks.crosslinks.push( renderedLink );
									
								} else {
									
									// add this to the unique udr map we're building
									if( !( protein1 in distinctUDRs ) ) { distinctUDRs[ protein1 ] = { }; }
									if( !( protein2 in distinctUDRs[ protein1 ] ) ) { distinctUDRs[ protein1 ][ protein2 ] = { }; }
									if( !( fromPosition in distinctUDRs[ protein1 ][ protein2 ] ) ) { distinctUDRs[ protein1 ][ protein2 ][ fromPosition ] = { }; }
									if( !( toPosition in distinctUDRs[ protein1 ][ protein2 ][ fromPosition ] ) ) { distinctUDRs[ protein1 ][ protein2 ][ fromPosition ][ toPosition ] = { }; }
									
									var UDR = distinctUDRs[ protein1 ][ protein2 ][ fromPosition ][ toPosition ];
									
									if( !( 'distance' in UDR ) || UDR[ 'distance' ] > shortestDistance ) {
										UDR[ 'shortestPair' ] = shortestPair;
										UDR[ 'distance' ] = shortestDistance;
										UDR[ 'renderedLink' ] = renderedLink;
										
										distinctUDRs[ protein1 ][ protein2 ][ fromPosition ][ toPosition ] = UDR;
									}								
								}
							}
						
						}
					
					}
				
				
				}
				
			}
			
		}
		
		// draw the unique UDRs if that's our choice
		if( uniqueUDRs ) {

			//console.log( "Unique crosslink UDRs: " );
			//console.log( distinctUDRs );
			
			var fromProteins = Object.keys( distinctUDRs );
			for( var fpi = 0; fpi < fromProteins.length; fpi++ ) {

				var fromProtein = fromProteins[ fpi ];
				var toProteins = Object.keys( distinctUDRs[ fromProtein ] );

				for( var tpi = 0; tpi < toProteins.length; tpi++ ) {
					
					var toProtein = toProteins[ tpi ];
					var fromPositions = Object.keys( distinctUDRs[ fromProtein ][ toProtein ] );
					
					for( var i = 0; i < fromPositions.length; i++ ) {
						
						var fromPosition = fromPositions[ i ];
						var toPositions = Object.keys( distinctUDRs[ fromProtein ][ toProtein ][ fromPosition ] );
						
						for( var j = 0; j < toPositions.length; j++ ) {
							
							var toPosition = toPositions[ j ];
							var UDR = distinctUDRs[ fromProtein ][ toProtein ][ fromPosition ][ toPosition ];
							
							var link = { };
							link.type = 'crosslink';
							link.length = UDR[ 'distance' ];
							link.protein1 = fromProtein;
							link.protein2 = toProtein;
							link.position1 = fromPosition;
							link.position2 = toPosition;
							link.searchIds = _proteinLinkPositions[ fromProtein ][ toProtein ][ fromPosition ][ toPosition ];
							
							if( shadeByCounts ) {
								link.psmCount = _linkPSMCounts[ 'crosslink' ][ fromProtein ][ toProtein ][ fromPosition ][ toPosition ];
								
								if( !link.psmCount ) {
									console.log( "WARNING: Got 0 psms for link: " );
									console.log( link );
								}
							}

							// do not draw this link if it's being excluded
							if( !_linkExclusionHandler.isCrosslinkExcluded( fromProtein, fromPosition, toProtein, toPosition ) ) {
								_CROSSLINKS_MESH.addTube( UDR[ 'shortestPair' ][ 0 ].pos(), UDR[ 'shortestPair' ][ 1 ].pos(), 0.6, { cap: true, color : _linkColorHandler.getLinkColor( link, 'pvrgba' ), userData: link });
							}

							UDR[ 'renderedLink' ].link = link;

							_renderedLinks.crosslinks.push( UDR[ 'renderedLink' ] );

						}
						
					}
					
				}
				
			}
		}
	};



	var _MONOLINKS_MESH;
	var drawMonolinks = function( proteins ) {
			
		if( !_proteinMonolinkPositions ) {
			loadMonolinkData( true );
			return;
		}

		var shadeByCounts = false;
		if ( $( "input#shade-by-counts" ).is( ':checked' ) ) { shadeByCounts = true; }
		
		
		if( shadeByCounts && !( 'monolink' in _linkPSMCounts ) ) {
			loadMonolinkPSMCounts( true );
			return;
		}

		_renderedLinks.monolinks = new Array();
		
		if( _MONOLINKS_MESH ) { _MONOLINKS_MESH.hide(); }
		_MONOLINKS_MESH = _VIEWER.customMesh('monolinks');
		
		var proteinIds = Object.keys( proteins );
		
		for( var i = 0; i < proteinIds.length; i++ ) {
			var proteinId = proteinIds[ i ];
			
			if( !_proteinMonolinkPositions[ proteinId ] ) { continue; }
			
			var monoLinkPositions = Object.keys( _proteinMonolinkPositions[ proteinId ] );
			for( var j = 0; j < monoLinkPositions.length; j++ ) {
				
				// find and return the CA atoms present for this position in this protein in the visible chains
				var atoms = StructureAlignmentUtils.findCAAtoms( proteinId, monoLinkPositions[ j ], proteins[ proteinId ], _ALIGNMENTS, _STRUCTURE );
				
				for( var k = 0; k < atoms.length; k++ ) {
					
					var link = { };
					link.type = 'monolink';
					link.length = 12;
					link.protein1 = proteinId;
					link.position1 = monoLinkPositions[ j ];
					link.searchIds = _proteinMonolinkPositions[ proteinId ][ monoLinkPositions[ j ] ];

					if( shadeByCounts ) {
						link.psmCount = _linkPSMCounts[ 'monolink' ][ proteinId ][ monoLinkPositions[ j ] ];
						
						if( !link.psmCount ) {
							console.log( "WARNING: Got 0 psms for link: " );
							console.log( link );
						}
					}
					
					var coord = atoms[ k ].pos();
					_MONOLINKS_MESH.addTube( coord, [ coord[ 0 ] + 3, coord[ 1 ] + 3, coord[ 2 ] + 3 ], 0.6, { color: _linkColorHandler.getLinkColor( link, 'pvrgba' ), userData: link });

					var renderedLink = { };
					renderedLink.atom1 = atoms[ k ];
					
					renderedLink.link = link;
					
					_renderedLinks.monolinks.push( renderedLink );
				}
				
			}
			
			
		}
	};


	var _LINKABLE_MESH;
	var drawLinkableResidues = function( proteins ) {
		
		if( _LINKABLE_MESH ) { _LINKABLE_MESH.hide(); }
		_LINKABLE_MESH = _VIEWER.customMesh('linkable-positions');
		
		var proteinIds = Object.keys( proteins );
		
		for( var i = 0; i < proteinIds.length; i++ ) {
			var proteinId = proteinIds[ i ];
			
			if( !_linkablePositions[ proteinId ] ) { continue; }
			
			for( var j = 0; j < _linkablePositions[ proteinId ].length; j++ ) {
				
				var coords = StructureAlignmentUtils.findCACoords( proteinId, _linkablePositions[ proteinId ][ j ], proteins[ proteinId ], _ALIGNMENTS, _STRUCTURE );
				for( var k = 0; k < coords.length; k++ ) {
					
					//console.log( "Drawing linkable position: " );
					//console.log( coords[ k ] );
					
					var userData = { };
					userData.proteinId = proteinId;
					userData.position = _linkablePositions[ proteinId ][ j ];
					
					_LINKABLE_MESH.addSphere( coords[ k ], 1, { color: '#000000', userData: userData } );
					
					
				}
				
			}
			
			
		}
	};



	var _LOOPLINKS_MESH;

	var drawLooplinks_noReportRedraw = function( proteins ) {

		if( !proteins ) {
			proteins = getVisibleProteins();
		}

		drawLooplinks_doRedraw( proteins );
		updateDistanceCutoffReport();
		updateShownLooplinksHeader();
	}

	var drawLooplinks = function( proteins ) {

		if( !proteins ) {
			proteins = getVisibleProteins();
		}

		drawLooplinks_doRedraw( proteins );
		redrawDistanceReport();
	}

	var drawLooplinks_doRedraw = function( proteins ) {
		
		if( !_proteinLooplinkPositions ) {
			loadLooplinkData( true );
			return;
		}
		
		var shadeByCounts = false;
		if ( $( "input#shade-by-counts" ).is( ':checked' ) ) { shadeByCounts = true; }
		
		
		if( shadeByCounts && !( 'looplink' in _linkPSMCounts ) ) {
			loadLooplinkPSMCounts( true );
			return;
		}
		
		// blow this data away from report data object
		delete _distanceReportData[ 'shown-looplinks' ];
		
		var distinctUDRs = { };		// if we're showing unique UDRs, keep track of the coordinates that yield the shortest distances for each distinct UDR
		var uniqueUDRs = getShowUniqueUDRs();
		
		_renderedLinks.looplinks = new Array();
		
		if( _LOOPLINKS_MESH ) {
			_VIEWER.hide( 'looplinks' );
			_VIEWER.rm( 'looplinks' );
		}
		_LOOPLINKS_MESH = _VIEWER.customMesh('looplinks');
		
		var proteinIds = Object.keys( proteins );
		
		for( var i = 0; i < proteinIds.length; i++ ) {
			var proteinId = proteinIds[ i ];
			
			if( !_proteinLooplinkPositions[ proteinId ] ) { continue; }
			if( !_proteinLooplinkPositions[ proteinId ][ proteinId ] ) {
				console.log( "MAJOR WARNING: Did not find _proteinLooplinkPositions[ proteniId ][ proteinId ] for proteinId: " + proteinId );
			}
					
			for( var chainIndex = 0; chainIndex < proteins[ proteinId ].length; chainIndex++ ) {
				var chainId = proteins[ proteinId ][ chainIndex ];
			
				
				var fromKeys = Object.keys( _proteinLooplinkPositions[ proteinId ][ proteinId ] );
				for( var fromIndex = 0; fromIndex < fromKeys.length; fromIndex++ ) {
					var fromPosition = fromKeys[ fromIndex ];
					var fromAtomsArray = StructureAlignmentUtils.findCAAtoms( proteinId, fromPosition, [ chainId ], _ALIGNMENTS, _STRUCTURE );

					if( !fromAtomsArray || fromAtomsArray.length < 1 ) { continue; }
					
					if( fromAtomsArray.length > 1 ) {
						console.log( "MAJOR WARNING: Got more than one CA atom in chain " + chainId + " for protein " + proteinId + " at position " + fromPosition );
					}
					
					var fromAtom = fromAtomsArray [ 0 ];
					var fromCoords = fromAtom.pos();
					
					var toKeys = Object.keys( _proteinLooplinkPositions[ proteinId ][ proteinId ][ fromPosition ] );
					for( var toIndex = 0; toIndex < toKeys.length; toIndex++ ) {
						var toPosition = toKeys[ toIndex ];
						
						if( toPosition <= fromPosition ) { continue; }		// ensure we're only looking at a given looplink once
						
						var toAtomsArray = StructureAlignmentUtils.findCAAtoms( proteinId, toPosition, [ chainId ], _ALIGNMENTS, _STRUCTURE );
						
						if( !toAtomsArray || toAtomsArray.length < 1 ) { continue; }
						
						if( toAtomsArray.length > 1 ) {
							console.log( "MAJOR WARNING: Got more than one CA atom in chain " + chainId + " for protein " + proteinId + " at position " + fromPosition );
						}
						
						var toAtom = toAtomsArray[ 0 ];
						var toCoords = toAtom.pos();

						var renderedLink = { };
						renderedLink.atom1 = fromAtom;
						renderedLink.atom2 = toAtom;
						
						if( !uniqueUDRs ) {
							
							var link = { };
							link.type = 'looplink';
							link.length = StructureUtils.calculateDistance( fromCoords, toCoords );
							link.protein1 = proteinId;
							link.position1 = fromPosition;
							link.position2 = toPosition;
							link.searchIds = _proteinLooplinkPositions[ proteinId ][ proteinId ][ fromPosition ][ toPosition ];
							
							if( shadeByCounts ) {
								link.psmCount = _linkPSMCounts[ 'looplink' ][ proteinId ][ proteinId ][ fromPosition ][ toPosition ];
								
								if( !link.psmCount ) {
									console.log( "WARNING: Got 0 psms for link: " );
									console.log( link );
								}
							}

							if( !_linkExclusionHandler.isLooplinkExcluded( proteinId, fromPosition, toPosition ) ) {
								_LOOPLINKS_MESH.addTube(fromCoords, toCoords, 0.6, {
									color: _linkColorHandler.getLinkColor(link, 'pvrgba'),
									userData: link
								});
							}

							renderedLink.link = link;
							
							_renderedLinks.looplinks.push( renderedLink );
							
						} else {

							// add this to the unique udr map we're building
							if( !( proteinId in distinctUDRs ) ) { distinctUDRs[ proteinId ] = { }; }
							if( !( fromPosition in distinctUDRs[ proteinId ] ) ) { distinctUDRs[ proteinId ][ fromPosition ] = { }; }
							if( !( toPosition in distinctUDRs[ proteinId ][ fromPosition ] ) ) { distinctUDRs[ proteinId ][ fromPosition ][ toPosition ] = { }; }
							
							var UDR = distinctUDRs[ proteinId ][ fromPosition ][ toPosition ];
							
							if( !( 'distance' in UDR ) || UDR[ 'distance' ] > StructureUtils.calculateDistance( fromCoords, toCoords ) ) {
								UDR[ 'shortestPair' ] = [ fromCoords, toCoords ];
								UDR[ 'distance' ] = StructureUtils.calculateDistance( fromCoords, toCoords );
								UDR[ 'renderedLink' ] = renderedLink;
								
								distinctUDRs[ proteinId ][ fromPosition ][ toPosition ] = UDR;
							}	
							
						}
					}
					
				}
				
			}

		}
		
		// draw the unique UDRs if that's our choice
		if( uniqueUDRs ) {

			//console.log( "Unique looplink UDRs: " );
			//console.log( distinctUDRs );
			
			var fromProteins = Object.keys( distinctUDRs );
			for( var fpi = 0; fpi < fromProteins.length; fpi++ ) {

				var fromProtein = fromProteins[ fpi ];
				var fromPositions = Object.keys( distinctUDRs[ fromProtein ] );
					
				for( var i = 0; i < fromPositions.length; i++ ) {
						
					var fromPosition = fromPositions[ i ];
					var toPositions = Object.keys( distinctUDRs[ fromProtein ][ fromPosition ] );
						
					for( var j = 0; j < toPositions.length; j++ ) {
							
						var toPosition = toPositions[ j ];
						var UDR = distinctUDRs[ fromProtein ][ fromPosition ][ toPosition ];
						
						var link = { };
						link.type = 'looplink';
						link.length = UDR[ 'distance' ];
						link.protein1 = fromProtein;
						link.position1 = fromPosition;
						link.position2 = toPosition;
						link.searchIds = _proteinLooplinkPositions[ fromProtein ][ fromProtein ][ fromPosition ][ toPosition ];

						if( shadeByCounts ) {
							link.psmCount = _linkPSMCounts[ 'looplink' ][ fromProtein ][ fromProtein ][ fromPosition ][ toPosition ];
							
							if( !link.psmCount ) {
								console.log( "WARNING: Got 0 psms for link: " );
								console.log( link );
							}
						}

						if( !_linkExclusionHandler.isLooplinkExcluded( fromProtein, fromPosition, toPosition ) ) {
							_LOOPLINKS_MESH.addTube(UDR['shortestPair'][0], UDR['shortestPair'][1], 0.6, {
								cap: true,
								color: _linkColorHandler.getLinkColor(link, 'pvrgba'),
								userData: link
							});
						}

						UDR[ 'renderedLink' ].link = link;
						
						_renderedLinks.looplinks.push( UDR[ 'renderedLink' ] );
							
					}
				}				
			}
		}
	};


	var toggleShowCrosslinks = function() {
		if( _CROSSLINKS_MESH ) {
			_CROSSLINKS_MESH.hide();
			_CROSSLINKS_MESH = _VIEWER.customMesh('crosslinks');
			_CROSSLINKS_MESH = undefined;
			
			_renderedLinks.crosslinks = new Array();
			delete _distanceReportData[ 'shown-crosslinks' ];
			redrawDistanceReport();
		} else {
			drawMeshesOnStructure( drawCrosslinks);
		}
	};

	var toggleShowMonolinks = function() {
		if( _MONOLINKS_MESH ) {
			_MONOLINKS_MESH.hide();
			_MONOLINKS_MESH = _VIEWER.customMesh('monolinks');
			_MONOLINKS_MESH = undefined;
			
			_renderedLinks.monolinks = new Array();
		} else {
			drawMeshesOnStructure( drawMonolinks );
		}
	};

	var toggleShowLooplinks = function() {
		if( _LOOPLINKS_MESH ) {
			_LOOPLINKS_MESH.hide();
			_LOOPLINKS_MESH = _VIEWER.customMesh('monolinks');
			_LOOPLINKS_MESH = undefined;
			
			_renderedLinks.looplinks = new Array();
			delete _distanceReportData[ 'shown-looplinks' ];
			redrawDistanceReport();
		} else {
			drawMeshesOnStructure( drawLooplinks );
		}
	};

	var toggleShowLinkablePositions = function() {
		if( _LINKABLE_MESH ) {
			_LINKABLE_MESH.hide();
			_LINKABLE_MESH = _VIEWER.customMesh('linkable-positions');
			_LINKABLE_MESH = undefined;
		} else {
			drawMeshesOnStructure( drawLinkableResidues );
		}
	};

	var toggleShowUniqueUDRs = function() {
		drawMeshesOnStructure();
	};

	var toggleShowCoverage = function() {
		drawStructure();
	};


	var getRenderMode = function() {	
		var renderMode = $( "#select-render-mode" ).val();
		if( !renderMode ) { return 'cartoon'; }
		
		return renderMode;	
	};

	var getLinkColorMode = function() {
		var linkColorMode = $( "#select-link-color-mode" ).val();
		if( !linkColorMode ) { return 'length'; }
		
		return linkColorMode;
	};

	var getShowUniqueUDRs = function() {
		return $( "#show-unique-udrs" ).is( ':checked' );
	};


	var _RESIDUE_COLOR_LIGHT = [ 220/255, 220/255, 220/255, 0.75 ];
	var _RESIDUE_COLOR_DARK = [ 120/255, 120/255, 120/255, 0.75 ];

	var drawStructure = function() {
		
		console.log( "Calling drawStructure()" );
		

		// selected PDB file
		var pdbFile = getSelectedPDBFile();
			
		if( isNaN( pdbFile.id ) || pdbFile.id == 0 ) {
			return;
		}
		
		if ( $( "input#show-coverage" ).is( ':checked' ) ) {
			loadandShowVisibleProteinCoverage();
		} else {
			_VIEWER.clear();

			const chains = _STRUCTURE.chains();
			for( let i = 0; i < chains.length; i++ ) {
				const chain = _STRUCTURE.select({cname : chains[i].name()});

				_VIEWER.renderAs( 'protein', chain, getRenderMode(), { color:color.uniform( getColorArrayForView( _backboneColorManager.getChainColor( chains[i].name() ) ) ) } );
			}


			if( _VIEWER.proxlOb.viewerInitialLoad ) {
				_VIEWER.autoZoom();
				_VIEWER.proxlOb.viewerInitialLoad = 0;
			}
			
			_VIEWER.centerOn(_STRUCTURE);

			drawMeshesOnStructure();
		}
		
	};

	const getColorArrayForView = function( rgbaArray ) {

		return [ rgbaArray[ 0 ] / 255, rgbaArray[ 1 ] / 255, rgbaArray[ 2 ] / 255, rgbaArray[ 3 ] ];
	};

	/**
	 * Draw the legend based on the current color mode
	 */
	var drawLegend = function() {
		
		var $legendDiv = $( '#legend-div' );
		
		var $legend_by_link_length = $("#legend_by_link_length");
		var $legend_by_link_type = $("#legend_by_link_type");
		var $legend_by_search = $("#legend_by_search");
		
		$legend_by_link_length.hide();
		$legend_by_link_type.hide();
		$legend_by_search.hide();
		
		$legendDiv.show();
		
		
		var mode = getLinkColorMode();
		
		
		if ( mode === 'type' ) {
			
			$legend_by_link_type.show();
			
			//  Set color of blocks on legend
			
			var $legend_by_link_type_crosslink_color_block = $("#legend_by_link_type_crosslink_color_block");
			var $legend_by_link_type_looplink_color_block = $("#legend_by_link_type_looplink_color_block");
			var $legend_by_link_type_monolink_color_block = $("#legend_by_link_type_monolink_color_block");
			
			var colorCrosslink = _linkColorHandler.getColorByLinkTypeLabel( _linkColorHandler._CONSTANTS.typeColorsProperties.CROSSLINK );
			var colorLooplink = _linkColorHandler.getColorByLinkTypeLabel( _linkColorHandler._CONSTANTS.typeColorsProperties.LOOPLINK );
			var colorMonolink = _linkColorHandler.getColorByLinkTypeLabel( _linkColorHandler._CONSTANTS.typeColorsProperties.MONOLINK );
			
			$legend_by_link_type_crosslink_color_block.css( { 'background-color' : colorCrosslink } );
			$legend_by_link_type_crosslink_color_block.data( LINK_TYPE_COLOR_BLOCK__DATA__SAVED_BACKGROUND_COLOR, colorCrosslink );
			$legend_by_link_type_crosslink_color_block.data( LINK_TYPE_COLOR_BLOCK__DATA__COLOR_BLOCK_TYPE_LABEL, LINK_TYPE_INTERNAL_STRING_CROSSLINK );
			
			$legend_by_link_type_looplink_color_block.css( { 'background-color' : colorLooplink } );
			$legend_by_link_type_looplink_color_block.data( LINK_TYPE_COLOR_BLOCK__DATA__SAVED_BACKGROUND_COLOR, colorLooplink );
			$legend_by_link_type_looplink_color_block.data( LINK_TYPE_COLOR_BLOCK__DATA__COLOR_BLOCK_TYPE_LABEL, LINK_TYPE_INTERNAL_STRING_LOOPLINK );
			
			$legend_by_link_type_monolink_color_block.css( { 'background-color' : colorMonolink } );
			$legend_by_link_type_monolink_color_block.data( LINK_TYPE_COLOR_BLOCK__DATA__SAVED_BACKGROUND_COLOR, colorMonolink );
			$legend_by_link_type_monolink_color_block.data( LINK_TYPE_COLOR_BLOCK__DATA__COLOR_BLOCK_TYPE_LABEL, LINK_TYPE_INTERNAL_STRING_MONOLINK );
			
		}
		
		else if( mode === 'search' ) {
			
			$("#legend_by_search  .legend_by_search_container_for_search_jq").hide();

			// color blocks for single search ids
			
			for ( var i = 0; i < _searches.length; i++ ) {
				
				var projectSearchId = _searches[ i ].id;
				
				var projectSearchIds = [ projectSearchId ];
				
				var searchIds = convertProjectSearchIdArrayToSearchIdArray( projectSearchIds );
				
				var searchId = searchIds[ 0 ];

				var colorForBlock = _linkColorHandler.getColorForSearches( projectSearchIds ); 
					
				var searchPositionNumber = ( i + 1 );
				
				var $legend_by_search_container_for_search_X = $( "#legend_by_search_container_for_search_" + searchPositionNumber );
				var $legend_by_search_color_block_for_search_X = $( "#legend_by_search_color_block_for_search_" + searchPositionNumber );
				var $legend_by_search_search_id_for_search_X_jq = $( ".legend_by_search_search_id_for_search_" + searchPositionNumber + "_jq");

				$legend_by_search_container_for_search_X.show();
				$legend_by_search_color_block_for_search_X.css({ "background-color" : colorForBlock } );
				$legend_by_search_search_id_for_search_X_jq.text( searchId );
				
				$legend_by_search_color_block_for_search_X.data( COLOR_BY_SEARCH_COLOR_BLOCK__DATA__SAVED_BACKGROUND_COLOR, colorForBlock );
				$legend_by_search_color_block_for_search_X.data( COLOR_BY_SEARCH_COLOR_BLOCK__DATA__SEARCH_IDS, projectSearchIds );
			}
			
			if ( _searches.length <= 3 ) {

				// color blocks for pairs of search ids

				//  Pairs so outer loop stops before _searches.length - 1
				for( var i = 0; i < _searches.length - 1; i++ ) {

					//  Start inner loop at outer loop index + 1
					for( var k = i + 1; k < _searches.length; k++ ) {

						var projectSearchId_A = _searches[ i ].id;
						var projectSearchId_B = _searches[ k ].id;

						var projectSearchIds = [ projectSearchId_A, projectSearchId_B ];

						var searchIds = convertProjectSearchIdArrayToSearchIdArray( projectSearchIds );

						var colorForBlock = _linkColorHandler.getColorForSearches( projectSearchIds ); 

						var searchPositionNumber_A = ( i + 1 );
						var searchPositionNumber_B = ( k + 1 );

						var $legend_by_search_container_for_search_X = 
							$( "#legend_by_search_container_for_search_" + searchPositionNumber_A + "_" + searchPositionNumber_B );
						var $legend_by_search_color_block_for_search_X = 
							$( "#legend_by_search_color_block_for_search_" + searchPositionNumber_A + "_" + searchPositionNumber_B );

						$legend_by_search_container_for_search_X.show();
						$legend_by_search_color_block_for_search_X.css({ "background-color" : colorForBlock } );
						$legend_by_search_color_block_for_search_X.data( COLOR_BY_SEARCH_COLOR_BLOCK__DATA__SAVED_BACKGROUND_COLOR, colorForBlock );
						$legend_by_search_color_block_for_search_X.data( COLOR_BY_SEARCH_COLOR_BLOCK__DATA__SEARCH_IDS, projectSearchIds );

					}
				}

				if( _searches.length === 3 ) {

					var projectSearchIds = [ _searches[ 0 ].id, _searches[ 1 ].id, _searches[ 2 ].id ];

					var searchIds = convertProjectSearchIdArrayToSearchIdArray( projectSearchIds );

					var colorForBlock = _linkColorHandler.getColorForSearches( projectSearchIds ); 

					var $legend_by_search_container_for_search_1_2_3 = $("#legend_by_search_container_for_search_1_2_3");
					var $legend_by_search_color_block_for_search_1_2_3 = $("#legend_by_search_color_block_for_search_1_2_3");

					$legend_by_search_container_for_search_1_2_3.show();
					$legend_by_search_color_block_for_search_1_2_3.css({ "background-color" : colorForBlock } );
					$legend_by_search_color_block_for_search_1_2_3.data( COLOR_BY_SEARCH_COLOR_BLOCK__DATA__SAVED_BACKGROUND_COLOR, colorForBlock );
					$legend_by_search_color_block_for_search_1_2_3.data( COLOR_BY_SEARCH_COLOR_BLOCK__DATA__SEARCH_IDS, projectSearchIds );
				}
			}

			$legend_by_search.show();
			
		}
		
		else {
		
			// Color by Length  Legend
			
			$legend_by_link_length.show();
			

			$("#legend_by_link_length_main").show();
			$("#legend_by_link_length_change_cutoffs").hide();
			
			//  Set text for cutoff values in legend
			
			var cutoff_shortDistance = _linkColorHandler.getDistanceConstraints().shortDistance;
			var cutoff_longDistance = _linkColorHandler.getDistanceConstraints().longDistance;
			
			var $legend_by_link_length_cutoff_1 = $("#legend_by_link_length_cutoff_1");
			var $legend_by_link_length_cutoff_2 = $("#legend_by_link_length_cutoff_2");
			var $legend_by_link_length_cutoff_3 = $("#legend_by_link_length_cutoff_3");
			
			$legend_by_link_length_cutoff_1.text( cutoff_shortDistance );
			$legend_by_link_length_cutoff_2.text( cutoff_longDistance );
			$legend_by_link_length_cutoff_3.text( cutoff_longDistance );
			
			//  Set color of blocks on legend
			
			var $legend_by_link_length_color_block_short = $("#legend_by_link_length_color_block_short");
			var $legend_by_link_length_color_block_medium = $("#legend_by_link_length_color_block_medium");
			var $legend_by_link_length_color_block_long = $("#legend_by_link_length_color_block_long");
			
			var colorShort = _linkColorHandler.getColorByLinkLengthLabel( _linkColorHandler._CONSTANTS.lengthColorProperties.SHORT );
			var colorMedium = _linkColorHandler.getColorByLinkLengthLabel( _linkColorHandler._CONSTANTS.lengthColorProperties.MEDIUM );
			var colorLong = _linkColorHandler.getColorByLinkLengthLabel( _linkColorHandler._CONSTANTS.lengthColorProperties.LONG );
			
			
			$legend_by_link_length_color_block_short.css( { 'background-color' : colorShort } );
			$legend_by_link_length_color_block_short.data( LINK_LENGTH_COLOR_BLOCK__DATA__SAVED_BACKGROUND_COLOR, colorShort );
			$legend_by_link_length_color_block_short.data( LINK_LENGTH_COLOR_BLOCK__DATA__COLOR_BLOCK_LENGTH_LABEL, LINK_LENGTH_INTERNAL_STRING_SHORT );
			
			$legend_by_link_length_color_block_medium.css( { 'background-color' : colorMedium } );
			$legend_by_link_length_color_block_medium.data( LINK_LENGTH_COLOR_BLOCK__DATA__SAVED_BACKGROUND_COLOR, colorMedium );
			$legend_by_link_length_color_block_medium.data( LINK_LENGTH_COLOR_BLOCK__DATA__COLOR_BLOCK_LENGTH_LABEL, LINK_LENGTH_INTERNAL_STRING_MEDIUM );
			
			$legend_by_link_length_color_block_long.css( { 'background-color' : colorLong } );
			$legend_by_link_length_color_block_long.data( LINK_LENGTH_COLOR_BLOCK__DATA__SAVED_BACKGROUND_COLOR, colorLong );
			$legend_by_link_length_color_block_long.data( LINK_LENGTH_COLOR_BLOCK__DATA__COLOR_BLOCK_LENGTH_LABEL, LINK_LENGTH_INTERNAL_STRING_LONG );

			////////////////////////
			
			//   Update color of blocks used in User change cutoffs block
			
			var colorShort = _linkColorHandler.getColorByLinkLengthLabel( _linkColorHandler._CONSTANTS.lengthColorProperties.SHORT );
			var colorMedium = _linkColorHandler.getColorByLinkLengthLabel( _linkColorHandler._CONSTANTS.lengthColorProperties.MEDIUM );
			var colorLong = _linkColorHandler.getColorByLinkLengthLabel( _linkColorHandler._CONSTANTS.lengthColorProperties.LONG );

			$("#userConstraintFormShortDistance_color_block").css( { 'background-color' : colorShort } );
			$("#userConstraintFormMediumDistance_color_block").css( { 'background-color' : colorMedium } );
			$("#userConstraintFormLongDistance_color_block").css( { 'background-color' : colorLong } );

		}
		
	};


	//////////////////

	//   User Color by Link Length


	var clearUserColorByLength = function() {
		
		_linkColorHandler.clearUserColorByLength();
		
		updateURLHash( false /* useSearchForm */ );
		
		drawStructure();
		redrawDistanceReport();
		drawLegend();
	};

	///

	function saveChangedColorLinkByLength() {
		
		//  process all the color pickers, saving their values as user defined
		
		var $color_picker_link_length_jq = $(".color_picker_link_length_jq");

		$color_picker_link_length_jq.each( function( index, element ) {
			
			var $htmlElement = $( this );

			var blockColor = $htmlElement.data( LINK_LENGTH_COLOR_BLOCK__DATA__SAVED_BACKGROUND_COLOR ); // get color in .data
		
			var colorBlockLengthLabel = $htmlElement.data( LINK_LENGTH_COLOR_BLOCK__DATA__COLOR_BLOCK_LENGTH_LABEL );
			var colorBlockLengthLabelForColorHandler = _linkColorHandler._CONSTANTS.lengthColorProperties.SHORT;

			if ( colorBlockLengthLabel === LINK_LENGTH_INTERNAL_STRING_MEDIUM ) {
				colorBlockLengthLabelForColorHandler = _linkColorHandler._CONSTANTS.lengthColorProperties.MEDIUM;
			}

			if ( colorBlockLengthLabel === LINK_LENGTH_INTERNAL_STRING_LONG ) {
				colorBlockLengthLabelForColorHandler = _linkColorHandler._CONSTANTS.lengthColorProperties.LONG;
			}

			//  Call function to update User defined colors in object
			
			_linkColorHandler.setUserColorByLengthSingleColor( { 
				linkLengthLabel : colorBlockLengthLabelForColorHandler,
				linkColor : blockColor
			} );
		} );
		
		////////////////////////
		
		//   Update color of blocks used in User change cutoffs block
		
		var colorShort = _linkColorHandler.getColorByLinkLengthLabel( _linkColorHandler._CONSTANTS.lengthColorProperties.SHORT );
		var colorMedium = _linkColorHandler.getColorByLinkLengthLabel( _linkColorHandler._CONSTANTS.lengthColorProperties.MEDIUM );
		var colorLong = _linkColorHandler.getColorByLinkLengthLabel( _linkColorHandler._CONSTANTS.lengthColorProperties.LONG );

		$("#userConstraintFormShortDistance_color_block").css( { 'background-color' : colorShort } );
		$("#userConstraintFormMediumDistance_color_block").css( { 'background-color' : colorMedium } );
		$("#userConstraintFormLongDistance_color_block").css( { 'background-color' : colorLong } );

		///////////////////////
		
		//   Update Hash and redraw
		
		updateURLHash( false /* useSearchForm */ );
		
		drawStructure();
		redrawDistanceReport();
		drawLegend();
	}




	//////////////////

	//  User Color by Link Type


	var clearUserColorByLinkType = function() {

		_linkColorHandler.clearUserColorByType();

		updateURLHash( false /* useSearchForm */ );

		drawStructure();
		redrawDistanceReport();
		drawLegend();
	};

	///

	function saveChangedColorLinkByType() {

	//	process all the color pickers, saving their values as user defined

		var $color_picker_link_type_jq = $(".color_picker_link_type_jq");

		$color_picker_link_type_jq.each( function( index, element ) {

			var $htmlElement = $( this );

			var blockColor = $htmlElement.data( LINK_TYPE_COLOR_BLOCK__DATA__SAVED_BACKGROUND_COLOR ); // get color in .data

			var colorBlockTypeLabel = $htmlElement.data( LINK_TYPE_COLOR_BLOCK__DATA__COLOR_BLOCK_TYPE_LABEL );
			var colorBlockTypeLabelForColorHandler = _linkColorHandler._CONSTANTS.typeColorsProperties.CROSSLINK;

			if ( colorBlockTypeLabel === LINK_TYPE_INTERNAL_STRING_LOOPLINK ) {
				colorBlockTypeLabelForColorHandler = _linkColorHandler._CONSTANTS.typeColorsProperties.LOOPLINK;
			}

			if ( colorBlockTypeLabel === LINK_TYPE_INTERNAL_STRING_MONOLINK ) {
				colorBlockTypeLabelForColorHandler = _linkColorHandler._CONSTANTS.typeColorsProperties.MONOLINK;
			}

	//		Call function to update User defined colors in object

			_linkColorHandler.setUserColorByTypeSingleColor( { 
				linkTypeLabel : colorBlockTypeLabelForColorHandler,
				linkColor : blockColor
			} );
		} );


		///////////////////////

		//	Update Hash and redraw

		updateURLHash( false /* useSearchForm */ );

		drawStructure();
		redrawDistanceReport();
		drawLegend();
	}





	//  User Color by Search


	var clearUserColorBySearch = function() {

		_linkColorHandler.clearUserColorBySearch();

		updateURLHash( false /* useSearchForm */ );

		drawStructure();
		redrawDistanceReport();
		drawLegend();
	};

	///

	function saveChangedColorLinkBySearch() {

	//	process all the color pickers, saving their values as user defined

		var $color_picker_link_type_jq = $(".color_picker_by_search_jq");

		$color_picker_link_type_jq.each( function( index, element ) {

			var $htmlElement = $( this );

			var blockColor = $htmlElement.data( COLOR_BY_SEARCH_COLOR_BLOCK__DATA__SAVED_BACKGROUND_COLOR ); // get color in .data

			var colorBlockSearchIdsArray = $htmlElement.data( COLOR_BY_SEARCH_COLOR_BLOCK__DATA__SEARCH_IDS );
			
			if ( colorBlockSearchIdsArray ) {

				//  Skip this element if there are no search ids in the data.
				
	//			Call function to update User defined colors in object

				_linkColorHandler.setUserColorBySearchSingleColor( { 
					searchIdsArray : colorBlockSearchIdsArray,
					linkColor : blockColor
				} );
			}
		} );


	///////////////////////

	//	Update Hash and redraw

		updateURLHash( false /* useSearchForm */ );

		drawStructure();
		redrawDistanceReport();
		drawLegend();
	}





	//////////////////

	var updateUserDistanceConstraints = function() {
		
		var shortDistance = parseInt( $( '#userConstraintFormShortDistance' ).val() );
		var longDistance = parseInt( $( '#userConstraintFormLongDistance' ).val() );
		
		var $errorSpan = $( '#userConstraintsErrorSpan' );
		
		if( shortDistance != $( '#userConstraintFormShortDistance' ).val() ) {
			$errorSpan.html( "Non integer entered for short distance." );
			return;
		}
		
		if( longDistance != $( '#userConstraintFormLongDistance' ).val() ) {
			$errorSpan.html( "Non integer entered for long distance." );
			return;
		}
		
		if( longDistance <= shortDistance ) {
			$errorSpan.html( "Long distance must be greater than short distance." );
			return;
		}
		
		if( shortDistance <= 0 ) {
			$errorSpan.html( "Short distance must be 1 or more." );
			return;
		}
		
		$errorSpan.empty();
		
		_linkColorHandler.setUserDistanceConstraints( shortDistance, longDistance );
		
		updateURLHash( false /* useSearchForm */ );
		
		drawStructure();
		redrawDistanceReport();
		drawLegend();
		
	};

	var removeUserDistanceConstraints = function() {
		
		_linkColorHandler.removeUserDistanceConstraints();
		
		updateURLHash( false /* useSearchForm */ );
		
		drawStructure();
		redrawDistanceReport();
		drawLegend();
	};

	var userChangeDistanceConstraintsInterface = function() {
		
		var shortDistance = _linkColorHandler.getDistanceConstraints().shortDistance;
		var longDistance = _linkColorHandler.getDistanceConstraints().longDistance;
		
		$("#userConstraintFormShortDistance").val( shortDistance );
		$("#userConstraintFormLongDistance").val( longDistance );
		
		$("#legend_by_link_length_main").hide();
		$("#legend_by_link_length_change_cutoffs").show();
		
	};

	var _currentLoadRequest;
	var loadandShowVisibleProteinCoverage = function() {
		
		console.log( "Calling loadandShowVisibleProteinCoverage()" );
		
		var requestOb = { };
		requestOb.id = Date.now();
		_currentLoadRequest = requestOb;
		
		var proteinMap = getVisibleProteins();
		if( !proteinMap ) { proteinMap = { }; }
		
		var proteins = Object.keys( proteinMap );
		//if( !proteins || proteins.length < 1 ) { return; }
		
		var statusMap = { };
		requestOb.statusMap = statusMap;
		
		var foundall = true;
		for( var i = 0; i < proteins.length; i++ ) {
			if( _coverages && _coverages[ proteins[ i ] ] ) {
				statusMap[ proteins[ i ] ] = 1;
			} else {
				statusMap[ proteins[ i ] ] = 0;
				foundall = false;
				loadSequenceCoverageDataForProtein( proteins[ i ], requestOb, function() { checkIfCoverageLoadComplete( requestOb ); } );
			}				
		}
		
		// if coverage data is already loaded for all selected proteins, continue on
		if( foundall == true ) {
			checkIfCoverageLoadComplete( requestOb );
		}
		
	};


	var checkIfCoverageLoadComplete = function( requestOb ) {

		// ignore this if it's coming from an old request
		if( requestOb.id != _currentLoadRequest.id ) { return; }
		
		// see if all proteins have coverage
		var proteins = Object.keys( requestOb.statusMap );
		for( var i = 0; i < proteins.length; i++ ) {
			if( !requestOb.statusMap[ proteins[ i ] ] ) { return; }		// do nothing if something still isn't done loading
		}
		
		incrementSpinner();
		
		// should only get here if it's all done, draw the structure
		_VIEWER.clear();
		
		_VIEWER.renderAs( 'protein', _STRUCTURE, getRenderMode(), { color:getSequenceCoverageColorOp() });

		if( _VIEWER.proxlOb.viewerInitialLoad ) {
			_VIEWER.autoZoom();
			_VIEWER.proxlOb.viewerInitialLoad = 0;
		}
		
		_VIEWER.centerOn(_STRUCTURE);
		
		drawMeshesOnStructure();
		
		decrementSpinner();
	};

	var isProteinPositionCovered = function( proteinId, position ) {
		
		if( !_ranges ) { return false; }
		if( !_ranges[ proteinId ] ) { return false; }
		
		for( var i = 0; i < _ranges[ proteinId ].length; i++ ) {
			var start = _ranges[ proteinId ][ i ].start;
			var end = _ranges[ proteinId ][ i ].end;
			
			if( position >= start && position <= end ) { return true; }
		}
		
		return false;
	};

	var _COVERED_RESIDUE_COLOR = [ 190/255, 255/255, 190/255, 1 ];
	var _UNCOVERED_RESIDUE_COLOR = [ 255/255, 190/255, 190/255, 1 ];
	var _INACTIVE_RESIDUE_COLOR = [ 190/255, 190/255, 190/255, 0.75 ];

	/**
	 * Returns a colorop that colors residues covered by sequence coverage differently
	 * than residues not covered.
	 * @returns {pv.color.ColorOp}
	 */
	function getSequenceCoverageColorOp() {
		
		return new pv.color.ColorOp( function(atom, out, index) {

			// get residue of this atom
			var residue = atom.residue();

			// this can only happen when rendering as lines or points, which attempts
			// to draw all atoms, even those that are not peptide residues
			if( !residue.isAminoacid() ) {

				// color as inactive
				out[index+0] = _INACTIVE_RESIDUE_COLOR[ 0 ]; out[index+1] = _INACTIVE_RESIDUE_COLOR[ 1 ];
				out[index+2] = _INACTIVE_RESIDUE_COLOR[ 2 ]; out[index+3] = _INACTIVE_RESIDUE_COLOR[ 3 ];

				return;
			}

			// get position of residue in the PDB
			var pdbPosition = residue.index() + 1;

			// get chain of this atom
			var chain = residue.chain();

			// get the alignments visible for this chain
			var alignments = getVisibleAlignmentsForChain( chain.name() );

			if( !alignments || alignments.length < 1 ) {
				//console.log( "Got no visible alignments for chain: " + chain.name() );

				// color as inactive
				out[index+0] = _INACTIVE_RESIDUE_COLOR[ 0 ]; out[index+1] = _INACTIVE_RESIDUE_COLOR[ 1 ];
				out[index+2] = _INACTIVE_RESIDUE_COLOR[ 2 ]; out[index+3] = _INACTIVE_RESIDUE_COLOR[ 3 ];

				return;
			}

			//console.log( "Got " + alignments.length + " visible alignments for chain: " + chain.name() );

			// get a map of proteinSequenceVersionId:position pairs that correspond to the supplied pdbPosition in the supplied alignments
			var expproteinSequenceVersionIdPositionPairs = StructureAlignmentUtils.getExpproteinSequenceVersionIdPositionPairs( alignments, pdbPosition );

			if( !expproteinSequenceVersionIdPositionPairs || expproteinSequenceVersionIdPositionPairs.length < 1 ) {

				console.log( atom );
				//console.log( "Got no nrseq proteins positions for chain " + chain.name() );

				// color as not covered
				out[index+0] = _UNCOVERED_RESIDUE_COLOR[ 0 ]; out[index+1] = _UNCOVERED_RESIDUE_COLOR[ 1 ];
				out[index+2] = _UNCOVERED_RESIDUE_COLOR[ 2 ]; out[index+3] = _UNCOVERED_RESIDUE_COLOR[ 3 ];

				return;
			}


			if( expproteinSequenceVersionIdPositionPairs.length > 1 ) {
				//console.log( "WARNING: Got more than 1 nrseq:position for chain " + chain.name() + " at " + pdbPosition + ". Only using first one." );
			}

			var proteinSequenceVersionId = expproteinSequenceVersionIdPositionPairs[ 0 ].proteinSequenceVersionId;
			var position = expproteinSequenceVersionIdPositionPairs[ 0 ].position;

			if( isProteinPositionCovered( proteinSequenceVersionId, position ) ) {

				//console.log( proteinSequenceVersionId + " at position " + position + " is a covered position." );

				// color as a covered position
				out[index+0] = _COVERED_RESIDUE_COLOR[ 0 ]; out[index+1] = _COVERED_RESIDUE_COLOR[ 1 ];
				out[index+2] = _COVERED_RESIDUE_COLOR[ 2 ]; out[index+3] = _COVERED_RESIDUE_COLOR[ 3 ];


			} else {

				//console.log( proteinSequenceVersionId + " at position " + position + " is not a covered position." );

				out[index+0] = _UNCOVERED_RESIDUE_COLOR[ 0 ]; out[index+1] = _UNCOVERED_RESIDUE_COLOR[ 1 ];
				out[index+2] = _UNCOVERED_RESIDUE_COLOR[ 2 ]; out[index+3] = _UNCOVERED_RESIDUE_COLOR[ 3 ];

			}


		});
	}


	function getVisibleAlignmentsForChain( chainId ) {
		
		var alignments = new Array();
			
		$( ".protein-checkbox" ).each( function() {
			if( $( this ).prop( 'checked' ) ) {
				
				var pid = $( this ).val();
				var chain = $(this ).attr( 'data-chain' );
				
				if( chainId != chain ) { return true; }
				
				const alignment = StructureAlignmentUtils.getAlignmentByChainAndProtein( chain, pid, _ALIGNMENTS );
				if( alignment ) {
					alignments.push( alignment );
				} else {
					console.log( "WARNING: Got no alignment for a checked protein in chain?" );
				}
				
			}
		});
		
		return alignments;
	}

	function getAllAlignmentsForChain( chainId ) {
		
		var alignments = new Array();
			
		$( ".protein-checkbox" ).each( function() {
				
			var pid = $( this ).val();
			var chain = $(this ).attr( 'data-chain' );
				
			if( chainId != chain ) { return true; }
				
			var alignment = StructureAlignmentUtils.getAlignmentByChainAndProtein( chain, pid, _ALIGNMENTS );
			if( alignment ) {
				alignments.push( alignment );
			}	

		});
		
		return alignments;
	}

	/**
	 * Returns true if the supplied chain is a protein sequence (at least one
	 * residue has an alpha carbon). False otherwise.
	 */
	var PDBChainIsProtein = function( chainName ) {
		
		if( !_STRUCTURE ) { return false; }
		if( !_STRUCTURE.chainByName( chainName ) ) { return false; }
		
		var residues = _STRUCTURE.chainByName( chainName ).residues();
		if( !residues ) { return false; }
		
		for( var i = 0; i < residues.length; i++ ) {
			if( residues[ i ].atom( 'CA' ) ) {
				return true;
			}
		}
		
		return false;
	};


	this_OfOutermostObjectOfClass.editAlignment = function ( chainId, index ) {
		showAlignment( _ALIGNMENTS[ chainId ][ index ], false );
	};


	/////////////////

	this_OfOutermostObjectOfClass.deleteAlignment = function ( clickThis, alignmentId ) {

		openConfirmDeleteAlignmentOverlay(clickThis, alignmentId );

		return;

	};

	///////////

	var openConfirmDeleteAlignmentOverlay = function(clickThis, alignmentId ) {

		var $clickThis = $(clickThis);


		var $delete_alignment_confirm_button = $("#delete_alignment_confirm_button");
		$delete_alignment_confirm_button.data("alignmentId", alignmentId);

	//	Position dialog over clicked delete icon

	//	get position of div containing the dialog that is inline in the page
		var $delete_alignment_overlay_containing_outermost_div_inline_div = $("#delete_alignment_overlay_containing_outermost_div_inline_div");

		var offset__containing_outermost_div_inline_div = $delete_alignment_overlay_containing_outermost_div_inline_div.offset();
		var offsetTop__containing_outermost_div_inline_div = offset__containing_outermost_div_inline_div.top;

		var offset__ClickedDeleteIcon = $clickThis.offset();
		var offsetTop__ClickedDeleteIcon = offset__ClickedDeleteIcon.top;

		var offsetDifference = offsetTop__ClickedDeleteIcon - offsetTop__containing_outermost_div_inline_div;

	//	adjust vertical position of dialog 

		var $delete_alignment_overlay_container = $("#delete_alignment_overlay_container");

		var height__delete_alignment_overlay_container = $delete_alignment_overlay_container.outerHeight( true /* [includeMargin ] */ );

		var positionAdjust = offsetDifference - ( height__delete_alignment_overlay_container / 2 );

	//	$delete_alignment_overlay_container.css( "top", offsetTop__ClickedDeleteIcon );
		$delete_alignment_overlay_container.css( "top", positionAdjust );


		var $delete_alignment_overlay_background = $("#delete_alignment_overlay_background"); 
		$delete_alignment_overlay_background.show();
		$delete_alignment_overlay_container.show();
	};

	///////////

	var closeConfirmDeleteAlignmentOverlay = function(clickThis, eventObject) {

		var $delete_alignment_confirm_button = $("#delete_alignment_confirm_button");
		$delete_alignment_confirm_button.data("alignmentId", null);

		$(".delete_alignment_overlay_show_hide_parts_jq").hide();
	};


	/////////////////

	//put click handler for this on #delete_alignment_confirm_button

	var deleteAlignmentConfirmed = function(clickThis, eventObject) {


		var $delete_alignment_confirm_button = $("#delete_alignment_confirm_button");

		var alignmentId = $delete_alignment_confirm_button.data("alignmentId");




		
	//	if( confirm( "Are you sure you want to unassociate this protein from this PDB chain?" ) ) {

		incrementSpinner();

		var url = "services/psa/deleteAlignment";

		var requestData = {
				alignmentId : alignmentId
		};

		$.ajax({
			type: "POST",
			url: url,
			data : requestData,
			dataType: "text",
			success: function(data)	{

				try {

					closeConfirmDeleteAlignmentOverlay( clickThis, eventObject );


					loadPDBFileAlignments( listChains );

					decrementSpinner();
					
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
				decrementSpinner();
			}
		});

	//	} else {

	//	return;

	//	}

	};




	/////////////////




	this_OfOutermostObjectOfClass.confirmPDBFileDelete = function( clickThis, pdbFileId ) {
		
		openConfirmDeletePDBFileOverlay(clickThis, pdbFileId );

		return;

	};

	///////////

	var openConfirmDeletePDBFileOverlay = function(clickThis, pdbFileId ) {

		var $clickThis = $(clickThis);


		var $delete_pdb_file_confirm_button = $("#delete_pdb_file_confirm_button");
		$delete_pdb_file_confirm_button.data("pdbFileId", pdbFileId);

	//	Position dialog over clicked delete icon

	//	get position of div containing the dialog that is inline in the page
		var $delete_pdb_file_overlay_containing_outermost_div_inline_div = $("#delete_pdb_file_overlay_containing_outermost_div_inline_div");

		var offset__containing_outermost_div_inline_div = $delete_pdb_file_overlay_containing_outermost_div_inline_div.offset();
		var offsetTop__containing_outermost_div_inline_div = offset__containing_outermost_div_inline_div.top;

		var offset__ClickedDeleteIcon = $clickThis.offset();
		var offsetTop__ClickedDeleteIcon = offset__ClickedDeleteIcon.top;

		var offsetDifference = offsetTop__ClickedDeleteIcon - offsetTop__containing_outermost_div_inline_div;

	//	adjust vertical position of dialog 

		var $delete_pdb_file_overlay_container = $("#delete_pdb_file_overlay_container");

		var height__delete_pdb_file_overlay_container = $delete_pdb_file_overlay_container.outerHeight( true /* [includeMargin ] */ );

		var positionAdjust = offsetDifference - ( height__delete_pdb_file_overlay_container / 2 );

	//	$delete_pdb_file_overlay_container.css( "top", offsetTop__ClickedDeleteIcon );
		$delete_pdb_file_overlay_container.css( "top", positionAdjust );


		var $delete_pdb_file_overlay_background = $("#delete_pdb_file_overlay_background"); 
		$delete_pdb_file_overlay_background.show();
		$delete_pdb_file_overlay_container.show();
	};

	///////////

	var closeConfirmDeletePDBFileOverlay = function(clickThis, eventObject) {

		var $delete_pdb_file_confirm_button = $("#delete_pdb_file_confirm_button");
		$delete_pdb_file_confirm_button.data("pdbFileId", null);

		$(".delete_pdb_file_overlay_show_hide_parts_jq").hide();
	};


	/////////////////

	//put click handler for this on #delete_pdb_file_confirm_button

	var deletePDBFileConfirmed = function(clickThis, eventObject) {


		var $delete_pdb_file_confirm_button = $("#delete_pdb_file_confirm_button");
		
		var pdbFileId = $delete_pdb_file_confirm_button.data("pdbFileId");


		
		
	//	if( confirm( "Are you sure you want to delete this PDB file from the database? All protein mapping will be lost and it will no longer be available to other users." ) ) {

			incrementSpinner();
			
			var url = "services/pdb/deletePDBFile";
			
			var requestData = {
					pdbFileId : pdbFileId
			};
			
			$.ajax({
					type: "POST",
					url: url,
					data : requestData,
					dataType: "json",
					success: function(data)	{
						
						try {

							closeConfirmDeletePDBFileOverlay( clickThis, eventObject );

							loadPDBFiles();
							$("#glmol-div").empty();
							_VIEWER = undefined;
							_STRUCTURE = undefined;
							$( "#chain-list-div" ).empty();

							decrementSpinner();
							
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
						decrementSpinner();
					}
			});
			
	//	} else {
	//		
	//		return;
	//		
	//	}
			
	};


	function addChainMouseover( chainId ) {
		$("#chain-" + chainId + "-div").mouseover( function() {
			//mouseoverChain( chainId );
		});
		
		$("#chain-" + chainId + "-div").mouseout( function() {
			//mouseoutChain( chainId );
		});
	};

	function mouseoverChain( chainId ) {
		
		_VIEWER.clear();
		
		var chain = undefined;
		
		var chains = _STRUCTURE.chains();
		for( var i = 0; i < chains.length; i++ ) {
			
			if( chains[i].name() === chainId ) {
				chain = _STRUCTURE.select({cname : chains[i].name()});
				_VIEWER.cartoon( 'protein', chain, { color:color.uniform( 'red' ) } );
			} else {
				chain = _STRUCTURE.select({cname : chains[i].name()});
				_VIEWER.cartoon( 'protein', chain, { color:color.uniform( '#fefefe' ) } );
			}
		}
		
		if ( chain === undefined ) {
			
			console.log("chain is undefined, it was never set");
		}
		
		_VIEWER.cartoon( 'protein', chain, { color:color.uniform( 'red' ) } );
		
		
	}
	function mouseoutChain( chainId ) {
		
	//	var chain = 
			_STRUCTURE.select({cname : chainId});

		_VIEWER.clear();
		_VIEWER.cartoon('protein', _STRUCTURE, { color : color.rainbow() });
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



	//  Initialize the page and load the data

	function initPage() {

		var json = getJsonFromHash();

		if ( json === null ) {

			$("#invalid_url_no_data_after_hash_div").show();
			throw Error( "Invalid URL, no data after the hash '#'" );
		}

		
		//  Will immediately return after converting JSON or will call initPage() again after converting JSON
		if ( convertOldJSONIfNecessaryReturnTrueIfExit() ) {
			
			//  convertOldJSONIfNecessaryReturnTrueIfExit needs to wait for async so exit
			
			return;
		}


		populateAnnotationDataDisplayProcessingCommonCodeFromHash();
		
		console.log( "Initializing the page." );
		
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
		
		
		if ( json.searches && json.searches.length > 1 ) {
			
			$("#merged_label_text_header").show();  //  Update text at top to show this is for "merged" since more than one search
		}

		//  Attach click handlers for confirm delete overlays

		$("#delete_pdb_file_confirm_button").click(function(eventObject) {

			try {

				var clickThis = this;

				deletePDBFileConfirmed( clickThis, eventObject );

				return false;

			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});

		$(".delete_pdb_file_overlay_show_hide_parts_jq").click(function(eventObject) {

			try {

				var clickThis = this;

				closeConfirmDeletePDBFileOverlay( clickThis, eventObject );

				return false;

			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});



		$("#delete_alignment_confirm_button").click(function(eventObject) {

			try {

				var clickThis = this;

				deleteAlignmentConfirmed( clickThis, eventObject );

				return false;

			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});

		$(".delete_alignment_overlay_show_hide_parts_jq").click(function(eventObject) {

			try {

				var clickThis = this;

				closeConfirmDeleteAlignmentOverlay( clickThis, eventObject );

				return false;

			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});



		attachViewLinkInfoOverlayClickHandlers();




		$( "input#filterNonUniquePeptides" ).change(function() {

			try {

				defaultPageView.searchFormChanged_ForDefaultPageView();

			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
			try {
				if ( window.saveView_dataPages ) {
					window.saveView_dataPages.searchFormChanged_ForSaveView(); 
				}
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
			try {
				if ( window.saveView_dataPages ) {
					window.saveView_dataPages.searchFormChanged_ForSaveView(); 
				}
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
			try {
				if ( window.saveView_dataPages ) {
					window.saveView_dataPages.searchFormChanged_ForSaveView(); 
				}
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
			try {
				if ( window.saveView_dataPages ) {
					window.saveView_dataPages.searchFormChanged_ForSaveView(); 
				}
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});


		$( "input#show-crosslinks" ).change( function() {

			try {

				updateURLHash( false /* useSearchForm */ );
				toggleShowCrosslinks();

			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		$( "input#show-looplinks" ).change( function() {

			try {

				updateURLHash( false /* useSearchForm */ );
				toggleShowLooplinks();

			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		$( "input#show-monolinks" ).change( function() {

			try {

				updateURLHash( false /* useSearchForm */ );
				toggleShowMonolinks();

			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		$( "input#show-linkable-positions" ).change( function() {

			try {

				updateURLHash( false /* useSearchForm */ );
				toggleShowLinkablePositions();

			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		$( "input#show-coverage" ).change( function() {

			try {

				updateURLHash( false /* useSearchForm */ );
				toggleShowCoverage();

			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});

		$( "#select-render-mode" ).change( function() {

			try {

				updateURLHash( false /* 
			useSearchForm */ );
				changeRenderMode();

			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});

		$( "#select-link-color-mode" ).change( function() {

			try {

				updateURLHash( false /* useSearchForm */ );
				changeLinkColorMode();

			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});

		$( "#show-unique-udrs" ).change( function() {

			try {

				updateURLHash( false /* useSearchForm */ );
				toggleShowUniqueUDRs();

			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});

		$( "#shade-by-counts" ).change( function() {

			try {

				updateURLHash( false /* useSearchForm */ );
				toggleShadeByCounts();

			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});


		///////
		
		//   Link Color by link length
		
		//  Add color picker to color block for link color 
		

		$('.color_picker_link_length_jq').ColorPicker({
			onSubmit: function(hsbColor, hexColor, rgbColor, htmlElement ) {
				
				// update block on page with selected color 
				
				var $htmlElement = $( htmlElement );
				
				$htmlElement.css('backgroundColor', '#' + hexColor); // set HTML block color on page
				
				$htmlElement.data( LINK_LENGTH_COLOR_BLOCK__DATA__SAVED_BACKGROUND_COLOR, '#' + hexColor ); // store color in .data

				$htmlElement.ColorPickerHide();
				

				saveChangedColorLinkByLength();
				
			},
			onBeforeShow: function () {
				var $this = $(this);
				var thisColor = $this.data( LINK_LENGTH_COLOR_BLOCK__DATA__SAVED_BACKGROUND_COLOR );
				$this.ColorPickerSetColor( thisColor );
			},
			onHide: function() {
				//  called when hidden
				// alert("On Hide");
			}
		});
			

		///////
		
		//   Link Color by link type
		
		//  Add color picker to color block for link color 
		

		$('.color_picker_link_type_jq').ColorPicker({
			onSubmit: function(hsbColor, hexColor, rgbColor, htmlElement ) {
				
				// update block on page with selected color 
				
				var $htmlElement = $( htmlElement );
				
				$htmlElement.css('backgroundColor', '#' + hexColor); // set HTML block color on page
				
				$htmlElement.data( LINK_TYPE_COLOR_BLOCK__DATA__SAVED_BACKGROUND_COLOR, '#' + hexColor ); // store color in .data

				$htmlElement.ColorPickerHide();
				

				saveChangedColorLinkByType();
				
			},
			onBeforeShow: function () {
				var $this = $(this);
				var thisColor = $this.data( LINK_TYPE_COLOR_BLOCK__DATA__SAVED_BACKGROUND_COLOR );
				$this.ColorPickerSetColor( thisColor );
			},
			onHide: function() {
				//  called when hidden
				// alert("On Hide");
			}
		});

		///////
		
		//   Link Color by Search
		
		//  Add color picker to color block for link color 
		

		$('.color_picker_by_search_jq').ColorPicker({
			onSubmit: function(hsbColor, hexColor, rgbColor, htmlElement ) {
				
				try {

					// update block on page with selected color 

					var $htmlElement = $( htmlElement );

					$htmlElement.css('backgroundColor', '#' + hexColor); // set HTML block color on page

					$htmlElement.data( COLOR_BY_SEARCH_COLOR_BLOCK__DATA__SAVED_BACKGROUND_COLOR, '#' + hexColor ); // store color in .data

					$htmlElement.ColorPickerHide();


					saveChangedColorLinkBySearch();

				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
				
			},
			onBeforeShow: function () {
				
				try {

					var $this = $(this);
					var thisColor = $this.data( COLOR_BY_SEARCH_COLOR_BLOCK__DATA__SAVED_BACKGROUND_COLOR );
					$this.ColorPickerSetColor( thisColor );
					
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			},
			onHide: function() {
				//  called when hidden
				// alert("On Hide");
			}
		});

		saveView_dataPages.initialize({ /* projectSearchIds, container_DOM_Element, enableSetDefault */ });

		loadDataFromService();
	};






	// TODO   This function is probably not called

	//This will only be called when there is one search

	function mergedImageSaveOrUpdateDefaultPageView__( clickedThis ) {
		
		if ( _searches === undefined || _searches === null ) {
			
			throw Error( "_searches is undefined or null which is invalid " );
		}
		
		if ( _searches.length === 0 ) {
			
			throw Error( "_searches.length === 0 which is invalid " );
		}
		
		if ( _searches.length > 1 ) {
			
			throw Error( "_searches.length > 1 which is invalid " );
		}
		
		var search = _searches[ 0 ];
		
		var searchId = search.id;

		//  queryJSON is what is used for DB queries
		var queryJSON = getNavigationJSON_Not_for_Image_Or_Structure();
		
		saveOrUpdateDefaultPageView( { clickedThis : clickedThis, searchId : searchId, queryJSON : queryJSON } );
	};

}

var structurePagePrimaryRootCodeObject = new StructurePagePrimaryRootCodeClass();


window.structurePagePrimaryRootCodeObject = structurePagePrimaryRootCodeObject;


//   Pass structurePagePrimaryRootCodeObject to other JS files that use it
structure_viewer_click_element_handlers_pass_structurePagePrimaryRootCodeObject( structurePagePrimaryRootCodeObject );
LinkColorHandler_pass_structurePagePrimaryRootCodeObject( structurePagePrimaryRootCodeObject );
attachPDBMapProteinOverlayClickHandlers_pass_structurePagePrimaryRootCodeObject( structurePagePrimaryRootCodeObject );
PdbUpload_pass_structurePagePrimaryRootCodeObject( structurePagePrimaryRootCodeObject );


///////////

//  Object for passing to other objects

window.structureViewerPageObject = {

		getQueryJSONString : function() {

//			var queryJSON = getNavigationJSON_Not_for_Image_Or_Structure();
			
			var queryJSON = window.structurePagePrimaryRootCodeObject.call__getJsonFromHash();

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
			
			//  structure.do?projectSearchId=
			
			var windowHref = window.location.href;
			
			var windowHash = window.location.hash;
			
			var strutsActionIndex = windowHref.indexOf( "structure.do?projectSearchId" );
			
			var windowHrefBeforeStrutsAction = windowHref.substring( 0, strutsActionIndex );
			
			var newWindowHref = windowHrefBeforeStrutsAction + "structure.do?" + newProjectSearchIdParamsString + "&ds=y" + windowHash;
			
			window.location.href = newWindowHref;
		}

};




$(document).ready(function()  { 
		
	try {

		attachPDBMapProteinOverlayClickHandlers();
		
		attachPDBUploadOverlayClickHandlers();
		attachPDBFileUploadHandlers();

		window.structurePagePrimaryRootCodeObject.call__initPage();
		
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
});

$(window).unload(function()  { 
	if( _NEW_WINDOW ) {
		_NEW_WINDOW.close();
	}
});





