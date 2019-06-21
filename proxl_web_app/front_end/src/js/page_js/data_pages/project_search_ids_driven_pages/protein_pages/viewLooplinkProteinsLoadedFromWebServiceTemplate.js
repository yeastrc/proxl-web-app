
//   viewLooplinkProteinsLoadedFromWebServiceTemplate.js

//   Process and load data into the file viewLooplinkProteinsLoadedFromWebServiceTemplateFragment.jsp

//////////////////////////////////
// JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

//  Import Handlebars templates

const _protein_page_template = 
require("../../../../../../handlebars_templates_precompiled/protein_page/protein_page_template-bundle.js");

const _merged_pages_shared_template = 
require("../../../../../../handlebars_templates_precompiled/merged_pages_shared/merged_pages_shared_template-bundle.js");


import { getProjectSearchIdSearchIdPairsInDisplayOrder, getProjectSearchIdsInDisplayOrder } from 'page_js/data_pages/project_search_ids_driven_pages/common/getProjectSearchIdSearchIdPairsInDisplayOrder.js';

import { computeMergedSearchColorIndex_OneBased } from 'page_js/data_pages/project_search_ids_driven_pages/common/computeMergedSearchColorIndex.js';


//  For showing Data for links (Drilldown) (Called by HTML onclick):
import { viewLooplinkReportedPeptidesLoadedFromWebServiceTemplate } from 'page_js/data_pages/project_search_ids_driven_pages/protein_pages/viewLooplinkReportedPeptidesLoadedFromWebServiceTemplate.js';


//   Class contructor
var ViewLooplinkProteinsLoadedFromWebServiceTemplate = function() {

	if ( ! _protein_page_template.protein_Looplink_data_per_search_block_template ) {
		throw Error("Missing: _protein_page_template.protein_Looplink_data_per_search_block_template")
	}
	if ( ! _protein_page_template.protein_Looplink_data_per_search_data_row_template ) {
		throw Error("Missing: _protein_page_template.protein_Looplink_data_per_search_data_row_template")
	}
	if ( ! _protein_page_template.protein_Looplink_data_per_search_child_row_template ) {
		throw Error("Missing: _protein_page_template.protein_Looplink_data_per_search_block_template")
	}
	
	if ( ! _merged_pages_shared_template.mergedPages_data_per_search_between_searches_html ) {
		throw Error("Missing: _merged_pages_shared_template.mergedPages_data_per_search_between_searches_html")
	}

	var _DATA_LOADED_DATA_KEY = "dataLoaded";

	var _psmPeptideCutoffsRootObject = null;
	var _excludeLinksWith_Root = null;
	
	//////////////
	this.setPsmPeptideCriteria = function( psmPeptideCutoffsRootObject ) {
		_psmPeptideCutoffsRootObject = psmPeptideCutoffsRootObject;
	};

	//////////////
	this.setExcludeLinksWith_Root = function( excludeLinksWith_Root ) {
		_excludeLinksWith_Root = excludeLinksWith_Root;
	};
	
	//////////////
	this.showHideLooplinkProteins = function( params ) {
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
				this.loadAndInsertLooplinkProteinsIfNeeded( { $topTRelement : $itemToToggle, $clickedElement : $clickedElement } );
			}
			return false;  // does not stop bubbling of click event
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
	
	////////////////////////
	this.loadAndInsertLooplinkProteinsIfNeeded = function( params ) {
		var objectThis = this;
		var $topTRelement = params.$topTRelement;
		var $clickedElement = params.$clickedElement;
		var dataLoaded = $topTRelement.data( _DATA_LOADED_DATA_KEY );
		if ( dataLoaded ) {
			return;  //  EARLY EXIT  since data already loaded. 
		}
		var project_search_idsCommaDelim = $clickedElement.attr( "data-project_search_ids" );
		var protein_id = $clickedElement.attr( "data-protein_id" );
		var protein_position_1 = $clickedElement.attr( "data-protein_position_1" );
		var protein_position_2 = $clickedElement.attr( "data-protein_position_2" );
		//  Convert all attributes to empty string if null or undefined
		if ( ! protein_id ) {
			protein_id = "";
		}
		if ( ! protein_position_1 ) {
			protein_position_1 = "";
		}
		if ( ! protein_position_2 ) {
			protein_position_2 = "";
		}
		if ( project_search_idsCommaDelim === undefined || project_search_idsCommaDelim === null || project_search_idsCommaDelim === "" ) {
			throw Error( "attribute 'data-project_search_ids' is missing or empty" );
		}
		//  Convert project_search_ids comma delim to array
		var project_search_ids = [];
		var project_search_idsCommaDelimSplit = project_search_idsCommaDelim.split(",");
		for ( var search_idsCommaDelimSplitCounter = 0; search_idsCommaDelimSplitCounter < project_search_idsCommaDelimSplit.length; search_idsCommaDelimSplitCounter++ ) {
			var project_search_idsCommaDelimSplitEntry = project_search_idsCommaDelimSplit[ search_idsCommaDelimSplitCounter ];
			if ( project_search_idsCommaDelimSplitEntry === "" ) {
				continue;
			}
			project_search_ids.push( project_search_idsCommaDelimSplitEntry );
		}
		if ( project_search_ids.length === 0 ) {
			throw Error( "No values found in attribute 'data-project_search_ids'." );
		}
		//   Copy the cutoffs for the search ids found on the element
		var psmPeptideCriteriaSearches = _psmPeptideCutoffsRootObject.searches;
		var cutoffsPerProjectSearchIds = {};
		for ( var projectSearchIdIndex = 0; projectSearchIdIndex < project_search_ids.length; projectSearchIdIndex++ ) {
			var projectSearchIdForLookup = project_search_ids[ projectSearchIdIndex ];
			var cutoffForSearchId = psmPeptideCriteriaSearches[ projectSearchIdForLookup ];
			if ( cutoffForSearchId === undefined || cutoffForSearchId === null ) {
				throw Error( "No Cutoff data found for projectSearchIdForLookup: " + projectSearchIdForLookup );
			}
			cutoffsPerProjectSearchIds[ projectSearchIdForLookup ] = cutoffForSearchId;
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
				protein_id : protein_id,
				protein_position_1 : protein_position_1,
				protein_position_2 : protein_position_2,
		};
		$.ajax({
			url : "services/data/getLooplinkProteinsPerSearchIdsProteinIdsPositions",
			traditional: true,  //  Force traditional serialization of the data sent
								//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
								//   So searchIds array is passed as "searchIds=<value>" which is what Jersey expects
			data : ajaxRequestData,  // The data sent as params on the URL
			dataType : "json",
			success : function( ajaxResponseData ) {
				try {
					var responseParams = {
							ajaxResponseData : ajaxResponseData, 
							ajaxRequestData : ajaxRequestData,
							$topTRelement : $topTRelement
					};
					objectThis.loadAndInsertLooplinkProteinsResponse( responseParams );
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
	
	this.loadAndInsertLooplinkProteinsResponse = function({ ajaxResponseData, ajaxRequestData, $topTRelement }) {
		
		const proteinsPerProjectSearchIdList = ajaxResponseData.proteinsPerProjectSearchIdList;

		const $data_container = $topTRelement.find(".child_data_container_jq");
		if ( $data_container.length === 0 ) {
			throw Error( "unable to find HTML element with class 'child_data_container_jq'" );
		}
		$data_container.empty();
		
		//  Create "Display List" in order that searches are displayed in search details

		const proteinsPerProjectSearchIdList_DisplayOrder = [];
		{
			//  External Function:
			// const projectSearchIdSearchIdPairsInDisplayOrder_FromPage = getProjectSearchIdSearchIdPairsInDisplayOrder();
			const projectSearchIdsInDisplayOrder_FromPage = getProjectSearchIdsInDisplayOrder();

			for ( let index = 0; index < projectSearchIdsInDisplayOrder_FromPage.length; index++ ) {
				const projectSearchId_DisplayOrder = projectSearchIdsInDisplayOrder_FromPage[ index ];

				// let foundEntryFor_projectSearchId_DisplayOrder = false;
				for ( const reportedPeptidesPerProjectSearchId_Entry of proteinsPerProjectSearchIdList ) {
					if ( reportedPeptidesPerProjectSearchId_Entry.projectSearchId === projectSearchId_DisplayOrder ) {
						//  Found entry in proteinsPerProjectSearchIdList for projectSearchId_DisplayOrder so save to result list
						const displayItem = { item : reportedPeptidesPerProjectSearchId_Entry, index };
						proteinsPerProjectSearchIdList_DisplayOrder.push( displayItem );
						// foundEntryFor_projectSearchId_DisplayOrder = true;
						break;
					}
				}
			}

			if ( proteinsPerProjectSearchIdList.length !== proteinsPerProjectSearchIdList_DisplayOrder.length ) {
				throw Error("ERROR: proteinsPerProjectSearchIdList.length !== proteinsPerProjectSearchIdList_DisplayOrder.length ");
			}
		}

		//  Process Per Search List
		for ( let dataArrayIndex = 0; dataArrayIndex < proteinsPerProjectSearchIdList_DisplayOrder.length; dataArrayIndex++ ) {

			//  If after the first search, insert the separator
			if ( dataArrayIndex > 0 ) {
				const html = _merged_pages_shared_template.mergedPages_data_per_search_between_searches_html();
				$( html ).appendTo( $data_container );
			}

			const displayEntry = proteinsPerProjectSearchIdList_DisplayOrder[ dataArrayIndex ];

			const colorIndex_OneBased = computeMergedSearchColorIndex_OneBased({ searchIndex_ZeroBased : displayEntry.index });

			const proteinsPerProjectSearchEntry = displayEntry.item;

			const proteinsForSearchWithAnnotationNameDescList = proteinsPerProjectSearchEntry;

			const peptideAnnotationDisplayNameDescriptionList =  proteinsForSearchWithAnnotationNameDescList.peptideAnnotationDisplayNameDescriptionList;
			const psmAnnotationDisplayNameDescriptionList =  proteinsForSearchWithAnnotationNameDescList.psmAnnotationDisplayNameDescriptionList;

			const proteinsForSearch =  proteinsForSearchWithAnnotationNameDescList.proteins;

			//  create context for header row
			const context = { 
					peptideAnnotationDisplayNameDescriptionList : peptideAnnotationDisplayNameDescriptionList,
					psmAnnotationDisplayNameDescriptionList : psmAnnotationDisplayNameDescriptionList
			};
			
			const protein_Looplink_data_per_search_block_template_HTML = _protein_page_template.protein_Looplink_data_per_search_block_template(context);
			const $looplink_protein_block_template = $(protein_Looplink_data_per_search_block_template_HTML).appendTo($data_container);
			const looplink_protein_table_jq_ClassName = "looplink_protein_table_jq";
			const $looplink_protein_table_jq = $looplink_protein_block_template.find("." + looplink_protein_table_jq_ClassName );
			//			const $looplink_protein_table_jq = $looplink_protein_data_container.find(".looplink_protein_table_jq");
			if ( $looplink_protein_table_jq.length === 0 ) {
				throw Error( "unable to find HTML element with class '" + looplink_protein_table_jq_ClassName + "'" );
			}
			//  Add protein data to the page
			for ( let proteinIndex = 0; proteinIndex < proteinsForSearch.length ; proteinIndex++ ) {
				const proteinData = proteinsForSearch[ proteinIndex ];
				const context = { 
					colorIndex_OneBased,
						data : proteinData,
						protein_id : ajaxRequestData.protein_id,
						protein_position_1 : ajaxRequestData.protein_position_1,
						protein_position_2 : ajaxRequestData.protein_position_2
				};
				const html = _protein_page_template.protein_Looplink_data_per_search_data_row_template(context);
				const $looplink_protein_entry = $(html).appendTo($looplink_protein_table_jq);
				//  Get the number of columns of the inserted row so can set the "colspan=" in the next row
				//       that holds the child data
				const $looplink_protein_entry__columns = $looplink_protein_entry.find("td");
				const looplink_protein_entry__numColumns = $looplink_protein_entry__columns.length;
				//  colSpan is used as the value for "colspan=" in the <td>
				const childRowHTML_Context = { colSpan : looplink_protein_entry__numColumns };
				const childRowHTML = _protein_page_template.protein_Looplink_data_per_search_child_row_template( childRowHTML_Context );
				//  Add next row for child data
				$( childRowHTML ).appendTo($looplink_protein_table_jq);
			}
		}  // END:  For Each Project Search Id
	};
};

//  Static Singleton Instance of Class
const viewLooplinkProteinsLoadedFromWebServiceTemplate = new ViewLooplinkProteinsLoadedFromWebServiceTemplate();

window.viewLooplinkProteinsLoadedFromWebServiceTemplate = viewLooplinkProteinsLoadedFromWebServiceTemplate;

export { viewLooplinkProteinsLoadedFromWebServiceTemplate }

