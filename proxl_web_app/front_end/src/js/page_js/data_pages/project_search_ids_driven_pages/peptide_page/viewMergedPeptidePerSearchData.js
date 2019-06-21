
//  viewMergedPeptidePerSearchData.js

//  Retrieve and display the Per Search data which is the Reported peptide for the search

//////////////////////////////////
// JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

//  Import Handlebars templates

const _peptide_page_template = 
require("../../../../../../handlebars_templates_precompiled/peptide_page/peptide_page_template-bundle.js");

const _merged_pages_shared_template = 
require("../../../../../../handlebars_templates_precompiled/merged_pages_shared/merged_pages_shared_template-bundle.js");


import { getProjectSearchIdSearchIdPairsInDisplayOrder, getProjectSearchIdsInDisplayOrder } from 'page_js/data_pages/project_search_ids_driven_pages/common/getProjectSearchIdSearchIdPairsInDisplayOrder.js';

import { computeMergedSearchColorIndex_OneBased } from 'page_js/data_pages/project_search_ids_driven_pages/common/computeMergedSearchColorIndex.js';


//  For showing Data for links (Drilldown) (Called by HTML onclick):
import { viewPsmsLoadedFromWebServiceTemplate } from 'page_js/data_pages/project_search_ids_driven_pages/common/viewPsmsLoadedFromWebServiceTemplate.js';


//   Class contructor

var ViewMergedPeptidePerSearchDataFromWebServiceTemplate = function() {

	if ( ! _peptide_page_template.peptidePage_peptide_data_per_search_block_template ) {
		throw Error("Missing: _peptide_page_template.peptidePage_peptide_data_per_search_block_template")
	}
	if ( ! _peptide_page_template.peptidePage_peptide_data_per_search_data_row_template ) {
		throw Error("Missing: _peptide_page_template.peptidePage_peptide_data_per_search_data_row_template")
	}
	if ( ! _peptide_page_template.peptidePage_peptide_data_per_search_child_row_template ) {
		throw Error("Missing: _peptide_page_template.peptidePage_peptide_data_per_search_child_row_template")
	}

	if ( ! _merged_pages_shared_template.mergedPages_data_per_search_between_searches_html ) {
		throw Error("Missing: _merged_pages_shared_template.mergedPages_data_per_search_between_searches_html")
	}

	var _DATA_LOADED_DATA_KEY = "dataLoaded";

	var _excludeLinksWith_Root = null;
	var _psmPeptideAnnTypeIdDisplay = null;
	var _psmPeptideCutoffsRootObject = null;

	//////////////
	this.setPsmPeptideCriteria = function( psmPeptideCutoffsRootObject ) {
		_psmPeptideCutoffsRootObject = psmPeptideCutoffsRootObject;
	};
	
	//////////////
	this.setPsmPeptideAnnTypeIdDisplay = function( psmPeptideAnnTypeIdDisplay ) {
		_psmPeptideAnnTypeIdDisplay = psmPeptideAnnTypeIdDisplay;
	};

	//////////////
	this.setExcludeLinksWith_Root = function( excludeLinksWith_Root ) {
		_excludeLinksWith_Root = excludeLinksWith_Root;
	};
	
	//////////////
	//   Called by "onclick" on HTML element
	this.showHideReportedPeptidesPerSearch = function( params ) {
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
				this.loadAndInsertReportedPeptidesPerSearchIfNeeded( { $topTRelement : $itemToToggle, $clickedElement : $clickedElement } );
			}
			return false;  // does not stop bubbling of click event
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
	
	////////////////////////
	this.loadAndInsertReportedPeptidesPerSearchIfNeeded = function( params ) {
		var objectThis = this;
		var $topTRelement = params.$topTRelement;
		var $clickedElement = params.$clickedElement;
		var dataLoaded = $topTRelement.data( _DATA_LOADED_DATA_KEY );
		if ( dataLoaded ) {
			return;  //  EARLY EXIT  since data already loaded. 
		}
		var unified_reported_peptide_id = $clickedElement.attr( "data-unified_reported_peptide_id" );
		var project_search_ids = [];
		var $project_search_id_input_field_jq_List = $(".project_search_id_input_field_jq");
		if ( $project_search_id_input_field_jq_List.length === 0 ) {
			throw "input fields with class 'project_search_id_input_field_jq' containing project search ids is missing from the page";
		}
		$project_search_id_input_field_jq_List.each( function( index, element ) {
			var project_search_id = $( this ).val();
			//  Convert all attributes to empty string if null or undefined
			if ( ! project_search_id ) {
				project_search_id = "";
			}
			project_search_ids.push( project_search_id );
		} );
		//  Convert all attributes to empty string if null or undefined
		if ( ! unified_reported_peptide_id ) {
			unified_reported_peptide_id = "";
		}
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
		var psmPeptideCutoffsForProjectSearchIds_JSONString = JSON.stringify( _psmPeptideCutoffsRootObject );
		var annTypeDisplay_JSONString = null;
		if ( _psmPeptideAnnTypeIdDisplay ) {
			annTypeDisplay_JSONString = JSON.stringify( _psmPeptideAnnTypeIdDisplay );
		}

		var excludeLinksWith_Root_JSONString = undefined;
		if ( _excludeLinksWith_Root ) {
			excludeLinksWith_Root_JSONString = JSON.stringify( _excludeLinksWith_Root );
		}
		
		var ajaxRequestData = {
				project_search_ids : project_search_ids,
				unified_reported_peptide_id : unified_reported_peptide_id,
				psmPeptideCutoffsForProjectSearchIds : psmPeptideCutoffsForProjectSearchIds_JSONString,
				annTypeDisplay : annTypeDisplay_JSONString,
				excludeLinksWith_Root : excludeLinksWith_Root_JSONString
		};
		$.ajax({
			url : "services/data/getReportedPeptidesForUnifiedPeptId",
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
					objectThis.loadAndInsertReportedPeptidesPerSearchResponse( responseParams );
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
	
	this.loadAndInsertReportedPeptidesPerSearchResponse = function({ ajaxResponseData, $topTRelement }) {

		const reportedPeptidesPerSearchResponse = ajaxResponseData;

		//  Convert all attributes to empty string if null or undefined
		const $data_container = $topTRelement.find(".child_data_container_jq");
		if ( $data_container.length === 0 ) {
			throw "unable to find HTML element with class 'child_data_container_jq'";
		}

		$data_container.empty();
		
		////////////////////////////
		///////   Process Per Project Search Id:
		const reportedPeptidesPerProjectSearchIdList =  reportedPeptidesPerSearchResponse.reportedPeptidesPerProjectSearchIdList;

		//  Create "Display List" in order that searches are displayed in search details

		const reportedPeptidesPerProjectSearchIdList_DisplayOrder = [];
		{
			//  External Function:
			// const projectSearchIdSearchIdPairsInDisplayOrder_FromPage = getProjectSearchIdSearchIdPairsInDisplayOrder();
			const projectSearchIdsInDisplayOrder_FromPage = getProjectSearchIdsInDisplayOrder();

			for ( let index = 0; index < projectSearchIdsInDisplayOrder_FromPage.length; index++ ) {
				const projectSearchId_DisplayOrder = projectSearchIdsInDisplayOrder_FromPage[ index ];

				// let foundEntryFor_projectSearchId_DisplayOrder = false;
				for ( const reportedPeptidesPerProjectSearchId_Entry of reportedPeptidesPerProjectSearchIdList ) {
					if ( reportedPeptidesPerProjectSearchId_Entry.projectSearchId === projectSearchId_DisplayOrder ) {
						//  Found entry in reportedPeptidesPerProjectSearchIdList for projectSearchId_DisplayOrder so save to result list
						const displayItem = { item : reportedPeptidesPerProjectSearchId_Entry, index };
						reportedPeptidesPerProjectSearchIdList_DisplayOrder.push( displayItem );
						// foundEntryFor_projectSearchId_DisplayOrder = true;
						break;
					}
				}
			}

			if ( reportedPeptidesPerProjectSearchIdList.length !== reportedPeptidesPerProjectSearchIdList_DisplayOrder.length ) {
				throw Error("ERROR: reportedPeptidesPerProjectSearchIdList.length !== reportedPeptidesPerProjectSearchIdList_DisplayOrder.length ");
			}
		}

		//  Process Per Search List
		for ( let dataArrayIndex = 0; dataArrayIndex < reportedPeptidesPerProjectSearchIdList_DisplayOrder.length; dataArrayIndex++ ) {

			//  If after the first search, insert the separator
			if ( dataArrayIndex > 0 ) {
				const html = _merged_pages_shared_template.mergedPages_data_per_search_between_searches_html();
//				const $peptide_data_per_search_between_searches_html = 
				$( html ).appendTo( $data_container );
			}
			const displayEntry = reportedPeptidesPerProjectSearchIdList_DisplayOrder[ dataArrayIndex ];

			const colorIndex_OneBased = computeMergedSearchColorIndex_OneBased({ searchIndex_ZeroBased : displayEntry.index });

			const reportedPeptidesPerSearchEntry = displayEntry.item;
			// const projectSearchId = reportedPeptidesPerSearchEntry.projectSearchId;
			const reportedPeptidesForSearch =  reportedPeptidesPerSearchEntry.reportedPepides;

			//  Add header row for single search
			//  create context for header row
			const context = { 
					peptideAnnotationDisplayNameDescriptionList : reportedPeptidesPerSearchEntry.peptideAnnotationDisplayNameDescriptionList,
					psmAnnotationDisplayNameDescriptionList : reportedPeptidesPerSearchEntry.psmAnnotationDisplayNameDescriptionList
			};
			const peptidePage_peptide_data_per_search_block_template_HTML = _peptide_page_template.peptidePage_peptide_data_per_search_block_template(context);
			const $peptide_data_per_search_block_template = $( peptidePage_peptide_data_per_search_block_template_HTML ).appendTo($data_container);
			const peptide_table_jq_ClassName = "peptide_table_jq";
			const $peptide_table_jq = $peptide_data_per_search_block_template.find("." + peptide_table_jq_ClassName );
			if ( $peptide_table_jq.length === 0 ) {
				throw "unable to find HTML element with class '" + peptide_table_jq_ClassName + "'";
			}

			//  Add peptide data to the page for a single search
			for ( let peptideIndex = 0; peptideIndex < reportedPeptidesForSearch.length ; peptideIndex++ ) {
				const peptide = reportedPeptidesForSearch[ peptideIndex ];
				//  wrap data in an object to allow adding more fields
				const context = { data : peptide, colorIndex_OneBased };
				const peptidePage_peptide_data_per_search_data_row_template_HTML = _peptide_page_template.peptidePage_peptide_data_per_search_data_row_template(context);
				const $peptide_entry = 
					$( peptidePage_peptide_data_per_search_data_row_template_HTML ).appendTo($peptide_table_jq);
				//  Get the number of columns of the inserted row so can set the "colspan=" in the next row
				//       that holds the child data
				const $peptide_entry__columns = $peptide_entry.find("td");
				const peptide_entry__numColumns = $peptide_entry__columns.length;
				//  colSpan is used as the value for "colspan=" in the <td>
				const childRowHTML_Context = { colSpan : peptide_entry__numColumns };
				const childRowHTML = _peptide_page_template.peptidePage_peptide_data_per_search_child_row_template( childRowHTML_Context );
				//  Add next row for child data
				$( childRowHTML ).appendTo($peptide_table_jq);
			}
		}  // END:  For Each Project Search Id
	};
};

export { ViewMergedPeptidePerSearchDataFromWebServiceTemplate }
