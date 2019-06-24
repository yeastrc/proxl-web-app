/**
 * snapSVG_Library_Loader.js
 * 
 * Javascript for Loading the Snap SVG API Library (JS file included in this webapp)
 * 
 * Uses jQuery $.ajax to load Javascript
 * 
 */

//JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

///////////////////////////////////////////

/**
 * module imports
 */

import { reportWebErrorToServer } from 'page_js/header_section_js_all_pages_main_pages/header_section_every_page/reportWebErrorToServer.js';


/**
 * Local Constants
 */

const LOADER_URL ="js/libs/snap.svg-min.js";

const LOADER_LOADED_NO = "LOADED_NO";
const LOADER_LOADING_IN_PROGRESS = "LOADING_IN_PROGRESS";
const LOADER_LOADED_YES = "LOADED_YES";

/**
 * Local Variables
 */

/**
 * Local Variables - Chart Loader Loading
 */
let libraryLoader_Loaded = LOADER_LOADED_NO;
let libraryLoader_DeferredToResolveOnLoad = [];

/**
 * Local Variables - Core Charts Loading
 */

let library_Loaded = LOADER_LOADED_NO;
let library_DeferredToResolveOnLoad = [];

/**
 * Local Class - Kind of a Hack but it works
 */
class Deferred_Local_LibraryLoader {
	constructor() {
		this.promise = new Promise((resolve, reject)=> {
			try {
				this.reject = reject;
				this.resolve = resolve;
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
	}
	
	containedPromise() {
		return this.promise;
	}
	resolvePromise() {
		this.resolve();
	}
	rejectPromise() {
		this.reject();
	}
};



/**
 * Local Functions
 */

/**
 * 
 */
let _loadSnapSVGLoader = function() {	
	
	if ( libraryLoader_Loaded === LOADER_LOADED_YES ) {
		return Promise.resolve();
	}

	if ( libraryLoader_Loaded === LOADER_LOADING_IN_PROGRESS ) {

		let deferred = new Deferred_Local_LibraryLoader();
		libraryLoader_DeferredToResolveOnLoad.push( deferred );
//		console.log("Adding to snapSVG_LibraryLoader_DeferredToResolveOnLoad");
		return { loadingPromise: deferred.containedPromise() };
	}
	
	libraryLoader_Loaded = LOADER_LOADING_IN_PROGRESS;
	
	return new Promise( function( resolve, reject ) {
		try {
			jQuery.ajax({
				url: LOADER_URL,
				dataType: "script",
				cache: true
			}).done( function( data, textStatus, jqXHR ) {
				try {
					libraryLoader_Loaded = LOADER_LOADED_YES;

					let snapSVG_LibraryLoader_DeferredToResolveOnLoad_Local = libraryLoader_DeferredToResolveOnLoad;
					
					libraryLoader_DeferredToResolveOnLoad = []; // reset
					
					if ( snapSVG_LibraryLoader_DeferredToResolveOnLoad_Local.length > 0 ) {
						for ( const snapSVG_LibraryLoader_DeferredToResolveOnLoadItem of snapSVG_LibraryLoader_DeferredToResolveOnLoad_Local ) {
	//						console.log("Processsing entry in snapSVG_LibraryLoader_DeferredToResolveOnLoad_Local, index: " + i );
							snapSVG_LibraryLoader_DeferredToResolveOnLoadItem.resolvePromise();
						}
					}
					
					resolve();
					
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			}).fail( function( jqXHR, textStatus, errorThrown ) {
				handleRawAJAXError( jqXHR, textStatus, errorThrown );
			});
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	} );
};

	
/**
 * Public functions
 */

/**
 * 
 */
let loadSnapSVG_Library = function() {	

	if ( library_Loaded === LOADER_LOADED_YES ) {
		return { isLoaded: true };
	}
	
	if ( library_Loaded === LOADER_LOADING_IN_PROGRESS ) {
				
		let deferred = new Deferred_Local_LibraryLoader();
		library_DeferredToResolveOnLoad.push( deferred );
//		console.log("Adding to snapSVG_LibraryCoreCharts_DeferredToResolveOnLoad");
		return { loadingPromise: deferred.containedPromise() };
	}
	
	library_Loaded = LOADER_LOADING_IN_PROGRESS;

	return { loadingPromise : new Promise( function( resolve, reject ) {
	  	try {
			let loadSnapSVG_Loader_Promise = _loadSnapSVGLoader()
			loadSnapSVG_Loader_Promise.then(function(value) { // On Fulfilled
				try {
					library_Loaded = LOADER_LOADED_YES;

					let snapSVG_LibraryCoreCharts_DeferredToResolveOnLoad_Local = library_DeferredToResolveOnLoad;
					
					library_DeferredToResolveOnLoad = []; // reset
					
					if ( snapSVG_LibraryCoreCharts_DeferredToResolveOnLoad_Local.length > 0 ) {
						snapSVG_LibraryCoreCharts_DeferredToResolveOnLoad_Local.forEach(function( snapSVG_LibraryCoreCharts_DeferredToResolveOnLoadItem, i, array) {
	//						console.log("Processsing entry in snapSVG_LibraryCoreCharts_DeferredToResolveOnLoad_Local, index: " + i );
							snapSVG_LibraryCoreCharts_DeferredToResolveOnLoadItem.resolvePromise();
						}, this)
					}
					
					resolve();
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			}).catch(function(reason) {
				try {
					throw Error( "ERROR: Loading Snap SVG JS Library from Local webapp" );
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			});
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	} ) };
	
};


export { loadSnapSVG_Library }
