
//   viewProteinSingleForMergedProteinAllPageLoadedFromWebServiceTemplate.js

//   Process and load data into the file all_protein_block_template.jsp

//////////////////////////////////
// JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

//  For showing Data for links (Drilldown) (Called by HTML onclick):
import { viewReportedPeptidesForProteinAllLoadedFromWebServiceTemplate } from 'page_js/data_pages/project_search_ids_driven_pages/protein_pages/viewReportedPeptidesForProteinAllLoadedFromWebServiceTemplate.js';


//   Class contructor
var ViewProteinSingleForMergedProteinAllPageLoadedFromWebServiceTemplate = function() {
	var _DATA_LOADED_DATA_KEY = "dataLoaded";
	var _handlebarsTemplate_all_protein_block_template = null;
	var _handlebarsTemplate_all_protein_data_row_entry_template = null;
	var _handlebarsTemplate_all_protein_child_row_entry_template = null;
	var _data_per_search_between_searches_html = null;
	var _psmPeptideCutoffsRootObject = null;
	var _excludeLinksWith_Root =  null;
	var _chosenLinkTypes = undefined;

	//////////////
	this.setPsmPeptideCriteria = function( psmPeptideCutoffsRootObject ) {
		_psmPeptideCutoffsRootObject = psmPeptideCutoffsRootObject;
	};

	//////////////
	this.setExcludeLinksWith_Root = function( excludeLinksWith_Root ) {
		_excludeLinksWith_Root = excludeLinksWith_Root;
	};
	
	//////////////
	this.setChosenLinkTypes = function( chosenLinkTypes ) {
		_chosenLinkTypes = chosenLinkTypes;
	};
	
	//////////////
	this.showHideProteinAlls = function( params ) {
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
				this.loadAndInsertProteinsIfNeeded( { $topTRelement : $itemToToggle, $clickedElement : $clickedElement } );
			}
			return false;  // does not stop bubbling of click event
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
	
	////////////////////////
	this.loadAndInsertProteinsIfNeeded = function( params ) {
		var objectThis = this;
		var $topTRelement = params.$topTRelement;
		var $clickedElement = params.$clickedElement;
		var dataLoaded = $topTRelement.data( _DATA_LOADED_DATA_KEY );
		if ( dataLoaded ) {
			return;  //  EARLY EXIT  since data already loaded. 
		}
		var project_search_idsCommaDelim = $clickedElement.attr( "data-project_search_ids" );
		var protein_id = $clickedElement.attr( "data-protein_id" );
		//  Convert all attributes to empty string if null or undefined
		if ( ! protein_id ) {
			protein_id = "";
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
				protein_id : protein_id
		};
		if ( _chosenLinkTypes !== null && _chosenLinkTypes !== undefined ) {
			ajaxRequestData.link_type = _chosenLinkTypes;
		}

		$.ajax({
			url : "services/data/getProteinsAllPerSearchIdsProteinIds",
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
					objectThis.loadAndInsertProteinsResponse( responseParams );
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
	
	this.loadAndInsertProteinsResponse = function( params ) {
		var ajaxResponseData = params.ajaxResponseData;
		var ajaxRequestData = params.ajaxRequestData;
		var proteinsPerProjectSearchIdMap = ajaxResponseData.proteinsPerProjectSearchIdMap;
		var $topTRelement = params.$topTRelement;
		var $data_container = $topTRelement.find(".child_data_container_jq");
		if ( $data_container.length === 0 ) {
			throw Error( "unable to find HTML element with class 'child_data_container_jq'" );
		}
		var $data_containerHTML = $data_container.html();
		$data_container.empty();
		if ( _handlebarsTemplate_all_protein_block_template === null ) {
			var handlebarsSource_all_protein_block_template = $( "#all_protein_block_template" ).html();
			if ( handlebarsSource_all_protein_block_template === undefined ) {
				throw Error( "handlebarsSource_all_protein_block_template === undefined" );
			}
			if ( handlebarsSource_all_protein_block_template === null ) {
				throw Error( "handlebarsSource_all_protein_block_template === null" );
			}
			_handlebarsTemplate_all_protein_block_template = Handlebars.compile( handlebarsSource_all_protein_block_template );
		}
		if ( _handlebarsTemplate_all_protein_data_row_entry_template === null ) {
			var handlebarsSource_all_protein_data_row_entry_template = $( "#all_protein_data_row_entry_template" ).html();
			if ( handlebarsSource_all_protein_data_row_entry_template === undefined ) {
				throw Error( "handlebarsSource_all_protein_data_row_entry_template === undefined" );
			}
			if ( handlebarsSource_all_protein_data_row_entry_template === null ) {
				throw Error( "handlebarsSource_all_protein_data_row_entry_template === null" );
			}
			_handlebarsTemplate_all_protein_data_row_entry_template = Handlebars.compile( handlebarsSource_all_protein_data_row_entry_template );
		}
		if ( _handlebarsTemplate_all_protein_child_row_entry_template === null ) {
			var handlebarsSource_all_protein_child_row_entry_template = $( "#all_protein_child_row_entry_template" ).html();
			if ( handlebarsSource_all_protein_child_row_entry_template === undefined ) {
				throw Error( "handlebarsSource_all_protein_child_row_entry_template === undefined" );
			}
			if ( handlebarsSource_all_protein_child_row_entry_template === null ) {
				throw Error( "handlebarsSource_all_protein_child_row_entry_template === null" );
			}
			_handlebarsTemplate_all_protein_child_row_entry_template = Handlebars.compile( handlebarsSource_all_protein_child_row_entry_template );
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
			var html = _handlebarsTemplate_all_protein_block_template(context);
			var $all_protein_block_template = $(html).appendTo($data_container);
			if ( _data_per_search_between_searches_html === null ) {
				_data_per_search_between_searches_html = $all_protein_block_template.find( ".data_per_search_between_searches_html_jq" ).html();
				if ( _data_per_search_between_searches_html === undefined ) {
					throw Error( "data_per_search_between_searches_html_jq === undefined" );
				}
				if ( _data_per_search_between_searches_html === null ) {
					throw Error( "data_per_search_between_searches_html_jq === null" );
				}
			}
			var all_protein_table_jq_ClassName = "all_protein_table_jq";
			var $all_protein_table_jq = $all_protein_block_template.find("." + all_protein_table_jq_ClassName );
			//			var $all_protein_table_jq = $all_protein_data_container.find(".all_protein_table_jq");
			if ( $all_protein_table_jq.length === 0 ) {
				throw Error( "unable to find HTML element with class '" + all_protein_table_jq_ClassName + "'" );
			}
			//  Add protein data to the page
			for ( var proteinIndex = 0; proteinIndex < proteinsForSearch.length ; proteinIndex++ ) {
				var proteinData = proteinsForSearch[ proteinIndex ];
				var context = { 
						data : proteinData,
						protein_id : ajaxRequestData.protein_id
				};
				var html = _handlebarsTemplate_all_protein_data_row_entry_template(context);
				var $all_protein_entry = $( html ).appendTo( $all_protein_table_jq );
				//  Get the number of columns of the inserted row so can set the "colspan=" in the next row
				//       that holds the child data
				var $all_protein_entry__columns = $all_protein_entry.find("td");
				var all_protein_entry__numColumns = $all_protein_entry__columns.length;
				//  colSpan is used as the value for "colspan=" in the <td>
				var childRowHTML_Context = { colSpan : all_protein_entry__numColumns };
				var childRowHTML = _handlebarsTemplate_all_protein_child_row_entry_template( childRowHTML_Context );
				//  Add next row for child data
				$( childRowHTML ).appendTo($all_protein_table_jq);
			}
		}  // END:  For Each Project Search Id
	};
};

//Static Singleton Instance of Class
var viewProteinSingleForMergedProteinAllPageLoadedFromWebServiceTemplate = new ViewProteinSingleForMergedProteinAllPageLoadedFromWebServiceTemplate();

window.viewProteinSingleForMergedProteinAllPageLoadedFromWebServiceTemplate = viewProteinSingleForMergedProteinAllPageLoadedFromWebServiceTemplate;

export { viewProteinSingleForMergedProteinAllPageLoadedFromWebServiceTemplate }
