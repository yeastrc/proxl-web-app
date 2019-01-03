
//   viewCrosslinkProteinsLoadedFromWebServiceTemplate.js

//   Process and load data into the file crosslink_protein_block_template.jsp

//////////////////////////////////
// JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";


//  For showing Data for links (Drilldown) (Called by HTML onclick):
import { viewCrosslinkReportedPeptidesLoadedFromWebServiceTemplate } from 'page_js/data_pages/project_search_ids_driven_pages/protein_pages/viewCrosslinkReportedPeptidesLoadedFromWebServiceTemplate.js';


//   Class contructor
var ViewCrosslinkProteinsLoadedFromWebServiceTemplate = function() {
	var _DATA_LOADED_DATA_KEY = "dataLoaded";
	var _handlebarsTemplate_crosslink_protein_block_template = null;
	var _handlebarsTemplate_crosslink_protein_data_row_entry_template = null;
	var _handlebarsTemplate_crosslink_protein_child_row_entry_template = null;
	var _data_per_search_between_searches_html = null;

	var _psmPeptideCutoffsRootObject = null;
	var _excludeLinksWith_Root =  null;
	
	//////////////
	this.setPsmPeptideCriteria = function( psmPeptideCutoffsRootObject ) {
		_psmPeptideCutoffsRootObject = psmPeptideCutoffsRootObject;
	};

	//////////////
	this.setExcludeLinksWith_Root = function( excludeLinksWith_Root ) {
		_excludeLinksWith_Root = excludeLinksWith_Root;
	};
	
	//////////////
	this.showHideCrosslinkProteins = function( params ) {
		try {
			var clickedElement = params.clickedElement;
			var $clickedElement = $( clickedElement );
			var $itemToToggle = $clickedElement.next();
			if( $itemToToggle.is(":visible" ) ) {
				$itemToToggle.hide(); 
				$clickedElement.find(".toggle_visibility_expansion_span_jq").show();
				$clickedElement.find(".toggle_visibility_contraction_span_jq").hide();
			} else { 
				$itemToToggle.show();
				$clickedElement.find(".toggle_visibility_expansion_span_jq").hide();
				$clickedElement.find(".toggle_visibility_contraction_span_jq").show();
				this.loadAndInsertCrosslinkProteinsIfNeeded( { $topTRelement : $itemToToggle, $clickedElement : $clickedElement } );
			}
			return false;  // does not stop bubbling of click event
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
	
	////////////////////////
	this.loadAndInsertCrosslinkProteinsIfNeeded = function( params ) {
		var objectThis = this;
		var $topTRelement = params.$topTRelement;
		var $clickedElement = params.$clickedElement;
		var dataLoaded = $topTRelement.data( _DATA_LOADED_DATA_KEY );
		if ( dataLoaded ) {
			return;  //  EARLY EXIT  since data already loaded. 
		}
		var project_search_idsCommaDelim = $clickedElement.attr( "data-project_search_ids" );
		var protein_1_id = $clickedElement.attr( "data-protein_1_id" );
		var protein_2_id = $clickedElement.attr( "data-protein_2_id" );
		var protein_1_position = $clickedElement.attr( "data-protein_1_position" );
		var protein_2_position = $clickedElement.attr( "data-protein_2_position" );
		//  Convert all attributes to empty string if null or undefined
		if ( ! protein_1_id ) {
			protein_1_id = "";
		}
		if ( ! protein_2_id ) {
			protein_2_id = "";
		}
		if ( ! protein_1_position ) {
			protein_1_position = "";
		}
		if ( ! protein_2_position ) {
			protein_2_position = "";
		}
		if ( project_search_idsCommaDelim === undefined || project_search_idsCommaDelim === null || project_search_idsCommaDelim === "" ) {
			throw Error( "attribute 'data-project_search_ids' is missing or empty" );
		}
		//  Convert project search ids comma delim to array
		var project_search_ids = [];
		var project_search_idsCommaDelimSplit = project_search_idsCommaDelim.split(",");
		for ( var search_idsCommaDelimSplitCounter = 0; search_idsCommaDelimSplitCounter < project_search_idsCommaDelimSplit.length; search_idsCommaDelimSplitCounter++ ) {
			var search_idsCommaDelimSplitEntry = project_search_idsCommaDelimSplit[ search_idsCommaDelimSplitCounter ];
			if ( search_idsCommaDelimSplitEntry === "" ) {
				continue;
			}
			project_search_ids.push( search_idsCommaDelimSplitEntry );
		}
		if ( project_search_ids.length === 0 ) {
			throw Error( "No values found in attribute 'data-project_search_ids'." );
		}
		//   Currently expect _psmPeptideCutoffsRootObject = 
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
		//   Copy the cutoffs for the search ids found on the element
		var psmPeptideCriteriaSearches = _psmPeptideCutoffsRootObject.searches;
		var cutoffsPerProjectSearchIds = {};
		for ( var projectSearchIdIndex = 0; projectSearchIdIndex < project_search_ids.length; projectSearchIdIndex++ ) {
			var projectSearchIdForLookup = project_search_ids[ projectSearchIdIndex ];
			var cutoffForProjectSearchId = psmPeptideCriteriaSearches[ projectSearchIdForLookup ];
			if ( cutoffForProjectSearchId === undefined || cutoffForProjectSearchId === null ) {
				throw Error( "No Cutoff data found for project search id: " + projectSearchIdForLookup );
			}
			cutoffsPerProjectSearchIds[ projectSearchIdForLookup ] = cutoffForProjectSearchId;
		}
		var cutoffsForWebservice = { searches: cutoffsPerProjectSearchIds };
		var psmPeptideCutoffsForProjectSearchIds_JSONString = JSON.stringify( cutoffsForWebservice );

		var excludeLinksWith_Root_JSONString = undefined;
		if ( _excludeLinksWith_Root ) {
			excludeLinksWith_Root_JSONString = JSON.stringify( _excludeLinksWith_Root );
		}
		
		var ajaxRequestData = {
				project_search_ids : project_search_ids,
				psmPeptideCutoffsForProjectSearchIds : psmPeptideCutoffsForProjectSearchIds_JSONString,
				excludeLinksWith_Root : excludeLinksWith_Root_JSONString,
				protein_1_id : protein_1_id,
				protein_2_id : protein_2_id,
				protein_1_position : protein_1_position,
				protein_2_position : protein_2_position
		};
		$.ajax({
			url : contextPathJSVar + "/services/data/getCrosslinkProteinsPerSearchIdsProteinIdsPositions",
			traditional: true,  //  Force traditional serialization of the data sent
								//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
								//   So project_search_ids array is passed as "project_search_ids=<value>" which is what Jersey expects
			data : ajaxRequestData,  // The data sent as params on the URL
			dataType : "json",
			success : function( ajaxResponseData ) {
				try {
					var responseParams = {
							ajaxResponseData : ajaxResponseData, 
							ajaxRequestData : ajaxRequestData,
							$topTRelement : $topTRelement
					};
					objectThis.loadAndInsertCrosslinkProteinsResponse( responseParams );
					$topTRelement.data( _DATA_LOADED_DATA_KEY, true );
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
	
	this.loadAndInsertCrosslinkProteinsResponse = function( params ) {
		var ajaxResponseData = params.ajaxResponseData;
		var ajaxRequestData = params.ajaxRequestData;
		var proteinsPerProjectSearchIdMap = ajaxResponseData.proteinsPerProjectSearchIdMap;
		var $topTRelement = params.$topTRelement;
		var $data_container = $topTRelement.find(".child_data_container_jq");
		if ( $data_container.length === 0 ) {
			throw Error( "unable to find HTML element with class 'child_data_container_jq'" );
		}
		$data_container.empty();
		if ( _handlebarsTemplate_crosslink_protein_block_template === null ) {
			var handlebarsSource_crosslink_protein_block_template = $( "#crosslink_protein_block_template" ).html();
			if ( handlebarsSource_crosslink_protein_block_template === undefined ) {
				throw Error( "handlebarsSource_crosslink_protein_block_template === undefined" );
			}
			if ( handlebarsSource_crosslink_protein_block_template === null ) {
				throw Error( "handlebarsSource_crosslink_protein_block_template === null" );
			}
			_handlebarsTemplate_crosslink_protein_block_template = Handlebars.compile( handlebarsSource_crosslink_protein_block_template );
		}
		if ( _handlebarsTemplate_crosslink_protein_data_row_entry_template === null ) {
			var handlebarsSource_crosslink_protein_data_row_entry_template = $( "#crosslink_protein_data_row_entry_template" ).html();
			if ( handlebarsSource_crosslink_protein_data_row_entry_template === undefined ) {
				throw Error( "handlebarsSource_crosslink_protein_data_row_entry_template === undefined" );
			}
			if ( handlebarsSource_crosslink_protein_data_row_entry_template === null ) {
				throw Error( "handlebarsSource_crosslink_protein_data_row_entry_template === null" );
			}
			_handlebarsTemplate_crosslink_protein_data_row_entry_template = Handlebars.compile( handlebarsSource_crosslink_protein_data_row_entry_template );
		}
		if ( _handlebarsTemplate_crosslink_protein_child_row_entry_template === null ) {
			var handlebarsSource_crosslink_protein_child_row_entry_template = $( "#crosslink_protein_child_row_entry_template" ).html();
			if ( handlebarsSource_crosslink_protein_child_row_entry_template === undefined ) {
				throw Error( "handlebarsSource_crosslink_protein_child_row_entry_template === undefined" );
			}
			if ( handlebarsSource_crosslink_protein_child_row_entry_template === null ) {
				throw Error( "handlebarsSource_crosslink_protein_child_row_entry_template === null" );
			}
			_handlebarsTemplate_crosslink_protein_child_row_entry_template = Handlebars.compile( handlebarsSource_crosslink_protein_child_row_entry_template );
		}
		
		////////////////////////////
		///////   Process Per Project Search Id:
		var projectSearchIdArray = Object.keys( proteinsPerProjectSearchIdMap );
		//  Sort the Project Search Ids in ascending order
		projectSearchIdArray.sort(function compareNumbers(a, b) {
			  return a - b;
		});
		for ( var projectSearchIdIndex = 0; projectSearchIdIndex < projectSearchIdArray.length; projectSearchIdIndex++ ) {
			//  If after the first search, insert the separator
			if ( projectSearchIdIndex > 0 ) {
				var $data_per_search_between_searches_htmlEntry =
					$( _data_per_search_between_searches_html ).appendTo($data_container);
				$data_per_search_between_searches_htmlEntry.show();
			}
			var projectSearchId = projectSearchIdArray[ projectSearchIdIndex ];
			var proteinsForSearchWithAnnotationNameDescList = proteinsPerProjectSearchIdMap[ projectSearchId ];
			var peptideAnnotationDisplayNameDescriptionList =  proteinsForSearchWithAnnotationNameDescList.peptideAnnotationDisplayNameDescriptionList;
			var psmAnnotationDisplayNameDescriptionList =  proteinsForSearchWithAnnotationNameDescList.psmAnnotationDisplayNameDescriptionList;
			var proteinsForSearch =  proteinsForSearchWithAnnotationNameDescList.proteins;
			//  create context for header row
			var context = { 
					peptideAnnotationDisplayNameDescriptionList : peptideAnnotationDisplayNameDescriptionList,
					psmAnnotationDisplayNameDescriptionList : psmAnnotationDisplayNameDescriptionList
			};
			var html = _handlebarsTemplate_crosslink_protein_block_template(context);
			var $crosslink_protein_block_template = $(html).appendTo($data_container);
			if ( _data_per_search_between_searches_html === null ) {
				_data_per_search_between_searches_html = $crosslink_protein_block_template.find( ".data_per_search_between_searches_html_jq" ).html();
				if ( _data_per_search_between_searches_html === undefined ) {
					throw Error( "data_per_search_between_searches_html_jq === undefined" );
				}
				if ( _data_per_search_between_searches_html === null ) {
					throw Error( "data_per_search_between_searches_html_jq === null" );
				}
			}
			var crosslink_protein_table_jq_ClassName = "crosslink_protein_table_jq";
			var $crosslink_protein_table_jq = $crosslink_protein_block_template.find("." + crosslink_protein_table_jq_ClassName );
			//			var $crosslink_protein_table_jq = $crosslink_protein_data_container.find(".crosslink_protein_table_jq");
			if ( $crosslink_protein_table_jq.length === 0 ) {
				throw Error( "unable to find HTML element with class '" + crosslink_protein_table_jq_ClassName + "'" );
			}
			//  Add protein data to the page
			for ( var proteinIndex = 0; proteinIndex < proteinsForSearch.length ; proteinIndex++ ) {
				var proteinData = proteinsForSearch[ proteinIndex ];
				var context = { 
						data : proteinData,
						protein_1_id : ajaxRequestData.protein_1_id,
						protein_2_id : ajaxRequestData.protein_2_id,
						protein_1_position : ajaxRequestData.protein_1_position,
						protein_2_position : ajaxRequestData.protein_2_position
				};
				var html = _handlebarsTemplate_crosslink_protein_data_row_entry_template(context);
				var $crosslink_protein_entry = $( html ).appendTo( $crosslink_protein_table_jq );
				//  Get the number of columns of the inserted row so can set the "colspan=" in the next row
				//       that holds the child data
				var $crosslink_protein_entry__columns = $crosslink_protein_entry.find("td");
				var crosslink_protein_entry__numColumns = $crosslink_protein_entry__columns.length;
				//  colSpan is used as the value for "colspan=" in the <td>
				var childRowHTML_Context = { colSpan : crosslink_protein_entry__numColumns };
				var childRowHTML = _handlebarsTemplate_crosslink_protein_child_row_entry_template( childRowHTML_Context );
				//  Add next row for child data
				$( childRowHTML ).appendTo($crosslink_protein_table_jq);
			}
		}  // END:  For Each Project Search Id
	};
};

//Static Singleton Instance of Class
var viewCrosslinkProteinsLoadedFromWebServiceTemplate = new ViewCrosslinkProteinsLoadedFromWebServiceTemplate();

window.viewCrosslinkProteinsLoadedFromWebServiceTemplate = viewCrosslinkProteinsLoadedFromWebServiceTemplate;

export { viewCrosslinkProteinsLoadedFromWebServiceTemplate }
