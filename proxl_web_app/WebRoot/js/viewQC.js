/**
 * viewQC.js
 * 
 * Javascript for the viewQC.jsp page
 * 
 * page variable viewQCPageCode
 * 
 */

//  JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

///////
$(document).ready(function() { 
	viewQCPageCode.init();

} ); // end $(document).ready(function() 


/**
 * Constructor 
 */
var ViewQCPageCode = function() {

	var _hash_json_Contents = null;

	var _link_type_crosslink_constant = null;
	var _link_type_looplink_constant = null;
	var _link_type_unlinked_constant = null;
	var _link_type_default_selected = null;
	
	/**
	 * Init page on load 
	 */
	this.init = function() {
		var objectThis = this;
		
		this.populateConstantsFromPage();
		
		this.updatePageFiltersFromURLHash();
		
		//  TODO  TEMP since don't have default for Type Filter working
//		this.updatePageFromFiltersToURLHashJSVarsAndPageData();
		
		//  TODO  TEMP since don't have load on expand section working
		this.loadDigestionStatistics();
		
	};
	
	/**
	 * Read strings from page from Java constants and store in Javascript variables used as constants 
	 */
	this.populateConstantsFromPage = function() {
		var $link_type_crosslink_constant = $("#link_type_crosslink_constant");
		_link_type_crosslink_constant = $link_type_crosslink_constant.text();
		var $link_type_looplink_constant = $("#link_type_looplink_constant");
		_link_type_looplink_constant = $link_type_looplink_constant.text();
		var $link_type_unlinked_constant = $("#link_type_unlinked_constant");
		_link_type_unlinked_constant = $link_type_unlinked_constant.text();
		
		_link_type_default_selected = [ _link_type_crosslink_constant ] //  , _link_type_looplink_constant, _link_type_unlinked_constant ]
	};

	/**
	 * "Update from Database" clicked.
	 * Update JS variables with filter values from page 
	 */
	this.refreshData = function() {
		this.updatePageFromFiltersToURLHashJSVarsAndPageData();
	};
	
	/**
	 *  
	 */
	this.updatePageFiltersFromURLHash = function() {

		_hash_json_Contents = this.getJsonFromHash();
		
		cutoffProcessingCommonCode.putCutoffsOnThePage(  { cutoffs : _hash_json_Contents.cutoffs } );
		
		//  Pass cutoffs and ann type display to all JS that call web services to get data (IE PSMs)
		webserviceDataParamsDistributionCommonCode.paramsForDistribution( _hash_json_Contents  )

		//  Mark check boxes for link types
		var linkTypes = _hash_json_Contents.linkTypes;
		
		this.markCheckBoxesForLinkTypes( linkTypes );

		//  Mark check boxes for chosen dynamic mod masses
		var dynamicModMasses = _hash_json_Contents.mods;
		if ( dynamicModMasses !== undefined && dynamicModMasses !== null && dynamicModMasses.length > 0  ) {
			//  dynamicModMasses not null so process it, empty array means nothing chosen
			if ( dynamicModMasses.length > 0 ) {
				var $mod_mass_filter_jq = $(".mod_mass_filter_jq");
				$mod_mass_filter_jq.each( function( index, element ) {
					var $item = $( this );
					var linkTypeFieldValue = $item.val();
					//  if linkTypeFieldValue found in dynamicModMasses array, set it to checked, else set it to not checked
					var checkedPropertyValue = false;
					for ( var dynamicModMassesIndex = 0; dynamicModMassesIndex < dynamicModMasses.length; dynamicModMassesIndex++ ) {
						var dynamicModMassesEntry = dynamicModMasses[ dynamicModMassesIndex ];
						if ( dynamicModMassesEntry === linkTypeFieldValue ) {
							checkedPropertyValue = true;
							break;
						}
					}
					$item.prop('checked', checkedPropertyValue);
				});
			}
		} else {
			//  dynamicModMasses null means all are chosen, since don't know which one was wanted
			var $mod_mass_filter_jq = $(".mod_mass_filter_jq");
			$mod_mass_filter_jq.each( function( index, element ) {
				var $item = $( this );
				$item.prop('checked', true);
			});
		}
	};
	
	this.markCheckBoxesForLinkTypes = function( linkTypes ) {
		if ( linkTypes !== undefined && linkTypes !== null ) {
			//  linkTypes not null so process it, empty array means nothing chosen
			if ( linkTypes.length > 0 ) {
				var $link_type_jq = $(".link_type_jq");
				$link_type_jq.each( function( index, element ) {
					var $item = $( this );
					var linkTypeFieldValue = $item.val();
					//  if linkTypeFieldValue found in linkTypes array, set it to checked, else set it to not checked
					var checkedPropertyValue = false;
					for ( var linkTypesIndex = 0; linkTypesIndex < linkTypes.length; linkTypesIndex++ ) {
						var linkTypesEntry = linkTypes[ linkTypesIndex ];
						if ( linkTypesEntry === linkTypeFieldValue ) {
							checkedPropertyValue = true;
							break;
						}
					}
					$item.prop('checked', checkedPropertyValue);
				});
			}
		} else {
			//  linkTypes null means all are chosen, since don't know which one was wanted
			var $link_type_jq = $(".link_type_jq");
			$link_type_jq.each( function( index, element ) {
				var $item = $( this );
				$item.prop('checked', true);
			});
		}
	};

	///////////////////////
	//   Called by "onclick" on HTML element
	this.updatePageFromFiltersToURLHashJSVarsAndPageData = function() {
		try {
			var dataFromFiltersResult =
				this.getDataFromFilters();
			if ( dataFromFiltersResult 
					&& dataFromFiltersResult.output_FieldDataFailedValidation ) {
				//  Only update if there were no errors in the input data
				return;
			}
			//  Update filter data held in JS variable
			_hash_json_Contents = dataFromFiltersResult;
			
			this.updateURLHashWithJSONObject( dataFromFiltersResult );

			//  TODO Update the page

			this.loadDigestionStatistics();
			
			
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
	
	/**
	 * 
	 */
	this.updateURLHashWithJSONObject = function( jsonObject ) {
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


	/////////////
	this.getDataFromFilters = function() {
		
		var getCutoffsFromThePageResult = cutoffProcessingCommonCode.getCutoffsFromThePage(  {  } );
		var getCutoffsFromThePageResult_FieldDataFailedValidation = getCutoffsFromThePageResult.getCutoffsFromThePageResult_FieldDataFailedValidation;
		if ( getCutoffsFromThePageResult_FieldDataFailedValidation ) {
			//  Cutoffs failed validation and error message was displayed
			return { output_FieldDataFailedValidation : getCutoffsFromThePageResult_FieldDataFailedValidation };  //  EARLY EXIT from function
		}
		var outputCutoffs = getCutoffsFromThePageResult.cutoffsByProjectSearchId;
		//  Output the selected Annotation data for display
		var getAnnotationTypeDisplayFromThePageResult = annotationDataDisplayProcessingCommonCode.getAnnotationTypeDisplayFromThePage( {} );
		var annotationTypeDisplayByProjectSearchId = getAnnotationTypeDisplayFromThePageResult.annTypeIdDisplayByProjectSearchId;
		//  Create array from check boxes for chosen link types
		var outputLinkTypes = [];
		var $link_type_jq = $(".link_type_jq");
		$link_type_jq.each( function( index, element ) {
			var $item = $( this );
			if ( $item.prop('checked') === true ) {
				var linkTypeFieldValue = $item.val();
				outputLinkTypes.push( linkTypeFieldValue );
//				} else {
//				allLinkTypesChosen = false;
			}
		});
		//  If no link types are selected, change crosslink to selected
		if ( outputLinkTypes.length === 0 ) {
			outputLinkTypes = _link_type_default_selected;
			//  Update page with defaults
			this.markCheckBoxesForLinkTypes( outputLinkTypes );
		}
		
		//  Create array from check boxes for chosen dynamic mod masses
		var outputDynamicModMasses = [];
		var allDynamicModMassesChosen = true;
		var $mod_mass_filter_jq = $(".mod_mass_filter_jq");
		$mod_mass_filter_jq.each( function( index, element ) {
			var $item = $( this );
			if ( $item.prop('checked') === true ) {
				var fieldValue = $item.val();
				outputDynamicModMasses.push( fieldValue );
			} else {
				allDynamicModMassesChosen = false;
			}
		});
		if ( allDynamicModMassesChosen ) {
			outputDynamicModMasses = null;  //  set to null when all chosen
		}
		var outputFilterCContents = { 
				cutoffs : outputCutoffs, 
				annTypeIdDisplay : annotationTypeDisplayByProjectSearchId,
				linkTypes : outputLinkTypes, 
				mods : outputDynamicModMasses 
		};
		
		return outputFilterCContents;
	};
	

	/**
	 * get values for variables from the hash part of the URL as JSON
	 */
	this.getRawJsonFromHash = function() {
		var jsonFromHash = null;
		var windowHash = window.location.hash;
		if ( windowHash === "" || windowHash === "#" ) {
			//  No Hash value so set defaults and return
			jsonFromHash = { 
					cutoffs : this.getCutoffDefaultsFromPage(),
					linkTypes : _link_type_default_selected
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
	this.getJsonFromHash = function() {
		var json = this.getRawJsonFromHash();
		if ( json.cutoffs === undefined || json.cutoffs === null ) {
			//  Set cutoff defaults if not in JSON
			json.cutoffs = getCutoffDefaultsFromPage();
		}
		//  START: Special update to allow projectSearchId values to be added or removed from URL
		//  Update cutoffs to add defaults for search ids in defaults but not in cutoffs
		//  Update cutoffs to remove search ids not in defaults but in cutoffs
		var cutoffs_Searches = json.cutoffs.searches;
		var cutoffDefaultsFromPage = this.getCutoffDefaultsFromPage();
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
		return json;
	}

	/**
	 * 
	 */
	this.getCutoffDefaultsFromPage = function() {
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
	};

	/**
	 * Load the data for the Digestion Statistics section
	 */
	this.loadDigestionStatistics = function() {
		
		this.loadMissingCleavageReportedPeptidesCount();
	};
	


	/**
	 * Load the data for MissingCleavageReportedPeptidesCount
	 */
	this.loadMissingCleavageReportedPeptidesCount = function() {
		var objectThis = this;
		
		var $missingCleavageReportedPeptidesCountLoadingBlock = $("#missingCleavageReportedPeptidesCountLoadingBlock");
		var $missingCleavageReportedPeptidesCountBlock = $("#missingCleavageReportedPeptidesCountBlock");
		$missingCleavageReportedPeptidesCountLoadingBlock.show();
		$missingCleavageReportedPeptidesCountBlock.hide();
		
		var project_search_ids = [];
		var $project_search_id_jq_List = $(".project_search_id_jq");
		if ( $project_search_id_jq_List.length === 0 ) {
			throw "input fields with class 'project_search_id_jq' containing project search ids is missing from the page";
		}
		$project_search_id_jq_List.each( function( index, element ) {
			var project_search_id = $( this ).val();
			//  Convert all attributes to empty string if null or undefined
			if ( ! project_search_id ) {
				project_search_id = "";
			}
			project_search_ids.push( project_search_id );
		} );
		//   Currently expect _psmPeptideCriteria = 
//						searches: Object
//							128: Object			
//								peptideCutoffValues: Object
//									238: Object
//										id: 238
//										value: "0.01"
//								psmCutoffValues: Object
//									384: Object
//										id: 384
//										value: "0.01"
//								searchId: 128
//	           The key to:
//					searches - searchId
//					peptideCutoffValues and psmCutoffValues - annotation type id
//				peptideCutoffValues.id and psmCutoffValues.id - annotation type id
		
		var hash_json_field_Contents_JSONString = JSON.stringify( _hash_json_Contents );
//		var annTypeDisplay_JSONString = null;
//		if ( _psmPeptideAnnTypeIdDisplay ) {
//			annTypeDisplay_JSONString = JSON.stringify( _psmPeptideAnnTypeIdDisplay );
//		}
		var ajaxRequestData = {
				project_search_id : project_search_ids,
				filterCriteria : hash_json_field_Contents_JSONString
//				,
//				annTypeDisplay : annTypeDisplay_JSONString
		};
		$.ajax({
			url : contextPathJSVar + "/services/qc/dataPage/missing",
			traditional: true,  //  Force traditional serialization of the data sent
								//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
								//   So project_search_ids array is passed as "project_search_ids=<value>" which is what Jersey expects
			data : ajaxRequestData,  // The data sent as params on the URL
			dataType : "json",
			success : function( ajaxResponseData ) {
				try {
					var responseParams = {
							ajaxResponseData : ajaxResponseData, 
							ajaxRequestData : ajaxRequestData
//							,
//							topTRelement : topTRelement
					};
					objectThis.loadMissingCleavageReportedPeptidesCountProcessResponse( responseParams );
//					$topTRelement.data( _DATA_LOADED_DATA_KEY, true );
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			},
	        failure: function(errMsg) {
	        	handleAJAXFailure( errMsg );
	        },
			error : function(jqXHR, textStatus, errorThrown) {
				handleAJAXError(jqXHR, textStatus, errorThrown);
			}
		});
		
		
	};
	
	/**
	 * Load the data for MissingCleavageReportedPeptidesCount
	 */
	this.loadMissingCleavageReportedPeptidesCountProcessResponse = function( params ) {
		var ajaxResponseData = params.ajaxResponseData;
		var ajaxRequestData = params.ajaxRequestData;
		
		var missingCleavageReportedPeptidesCountForLinkTypeList = ajaxResponseData.missingCleavageReportedPeptidesCountForLinkTypeList;
		
		var $missingCleavageReportedPeptidesCountLoadingBlock = $("#missingCleavageReportedPeptidesCountLoadingBlock");
		var $missingCleavageReportedPeptidesCountBlock = $("#missingCleavageReportedPeptidesCountBlock");
		$missingCleavageReportedPeptidesCountLoadingBlock.hide();
		$missingCleavageReportedPeptidesCountBlock.show();

		var $missingCleavageReportedPeptidesCountTable_tbody = $("#missingCleavageReportedPeptidesCountTable tbody");
		$missingCleavageReportedPeptidesCountTable_tbody.empty();
		
		for ( var index = 0; index < missingCleavageReportedPeptidesCountForLinkTypeList.length; index++ ) {
			var entry = missingCleavageReportedPeptidesCountForLinkTypeList[ index ];
			
			var numberInit = (0).toFixed( 2 );
			
			//  TODO  Maybe init to something else besides zero
			var missedCleavageReportedPeptideCount_Div_totalReportedPeptideCount_Display = numberInit;
			var missedCleavageCount_Div_totalReportedPeptideCount_Display = numberInit;
			var missedCleavagePSMCount_Div_totalPSMCount_Display = numberInit;
			
			if ( entry.totalReportedPeptideCount !== 0 ) {
				missedCleavageReportedPeptideCount_Div_totalReportedPeptideCount_Display =
					( entry.missedCleavageReportedPeptideCount / entry.totalReportedPeptideCount ).toFixed( 2 );
			}
			if ( entry.totalReportedPeptideCount !== 0 ) {
				missedCleavageCount_Div_totalReportedPeptideCount_Display =
					( entry.missedCleavageCount / entry.totalReportedPeptideCount ).toFixed( 2 );
			}
			if ( entry.totalPSMCount !== 0 ) {
				missedCleavagePSMCount_Div_totalPSMCount_Display =
					( entry.missedCleavagePSMCount / entry.totalPSMCount ).toFixed( 2 );
			}
			
			var html = "<tr><td>" + entry.linkType + 
			'</td><td  class=" count-display ">' + 
			entry.missedCleavageReportedPeptideCount +
			" (" + 
			missedCleavageReportedPeptideCount_Div_totalReportedPeptideCount_Display +
			")" +
			'</td><td  class=" count-display ">' + 
			entry.missedCleavageCount + 
			" (" + 
			missedCleavageCount_Div_totalReportedPeptideCount_Display +
			")" +
			'</td><td  class=" count-display ">' + 
			entry.totalReportedPeptideCount + 
			'</td><td  class=" count-display ">' + 
			entry.missedCleavagePSMCount + 
			" (" + 
			missedCleavagePSMCount_Div_totalPSMCount_Display +
			")" +
			"</td></tr>";
			$( html ).appendTo( $missingCleavageReportedPeptidesCountTable_tbody );
		}
		
	};
	
};

var viewQCPageCode = new ViewQCPageCode();

//  Copy to standard page level JS Code Object
//  Not currently supported  var standardFullPageCode = viewQCPageCode;
