
//  image_structure_click_element_common.js

/////    Click handling common code for Image and Structure pages

//////////////////////////////////
// JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

//  Import Handlebars templates

const _image_structure_pages_shared_template = 
require("../../../../../../handlebars_templates_precompiled/image_structure_pages_shared/image_structure_pages_shared_template-bundle.js");

const _merged_pages_shared_template = 
require("../../../../../../handlebars_templates_precompiled/merged_pages_shared/merged_pages_shared_template-bundle.js");


import { getProjectSearchIdSearchIdPairsInDisplayOrder, getProjectSearchIdsInDisplayOrder } from 'page_js/data_pages/project_search_ids_driven_pages/common/getProjectSearchIdSearchIdPairsInDisplayOrder.js';

import { computeMergedSearchColorIndex_OneBased } from 'page_js/data_pages/project_search_ids_driven_pages/common/computeMergedSearchColorIndex.js';


//  For showing Data for links (Drilldown) (Called by HTML onclick):
import { viewLooplinkProteinsLoadedFromWebServiceTemplate } from 'page_js/data_pages/project_search_ids_driven_pages/protein_pages/viewLooplinkProteinsLoadedFromWebServiceTemplate.js';

import { viewCrosslinkProteinsLoadedFromWebServiceTemplate } from 'page_js/data_pages/project_search_ids_driven_pages/protein_pages/viewCrosslinkProteinsLoadedFromWebServiceTemplate.js';
import { viewCrosslinkReportedPeptidesLoadedFromWebServiceTemplate } from 'page_js/data_pages/project_search_ids_driven_pages/protein_pages/viewCrosslinkReportedPeptidesLoadedFromWebServiceTemplate.js';
import { viewMonolinkReportedPeptidesLoadedFromWebServiceTemplate } from 'page_js/data_pages/project_search_ids_driven_pages/protein_pages/viewMonolinkReportedPeptidesLoadedFromWebServiceTemplate.js';

import { closeLorikeetOverlay } from 'page_js/data_pages/project_search_ids_driven_pages/common/lorikeetPageProcessing.js';


//////////
//   Globals
var _data_per_search_between_searches_html = null;



var _image_structure_click_element_common__ValidateInitialState = function() {


	if ( ! _image_structure_pages_shared_template.image_structure_pages_shared_data_per_search_block_template ) {
		throw Error("Missing: _image_structure_pages_shared_template.image_structure_pages_shared_data_per_search_block_template")
	}
	if ( ! _image_structure_pages_shared_template.image_structure_pages_shared_data_per_search_data_row_template ) {
		throw Error("Missing: _image_structure_pages_shared_template.image_structure_pages_shared_data_per_search_data_row_template")
	}
	if ( ! _image_structure_pages_shared_template.image_structure_pages_shared_data_per_search_child_row_template ) {
		throw Error("Missing: _image_structure_pages_shared_template.image_structure_pages_shared_data_per_search_block_template")
	}

	
	if ( ! _merged_pages_shared_template.mergedPages_data_per_search_between_searches_html ) {
		throw Error("Missing: _merged_pages_shared_template.mergedPages_data_per_search_between_searches_html")
	}
}



//////////////   Process LOOP link
/////////////////////
function getLooplinkDataCommon( params ) {
	try {
		var psmPeptideCutoffsRootObject = params.psmPeptideCutoffsRootObject;
		var removeNonUniquePSMs = params.removeNonUniquePSMs;
		var requestContext = params.context;
		
		var project_search_ids = requestContext.searchesArray;
		//  Extract psm peptide cutoffs for project search ids provided
		var psmPeptideCutoffsRootObjectForQuery = { searches : {} };
		var searches = psmPeptideCutoffsRootObject.searches;
		var querySearches = psmPeptideCutoffsRootObjectForQuery.searches;
		for ( var project_search_idsIndex = 0; project_search_idsIndex < project_search_ids.length; project_search_idsIndex++ ) {
			var projectSearchId = project_search_ids[ project_search_idsIndex ];
			var projectSearchIdString = projectSearchId.toString();
			var cutoffForProjectSearchId = searches[ projectSearchIdString ];
			querySearches[ projectSearchIdString ] = cutoffForProjectSearchId;
		} 
		///   Serialize cutoffs to JSON
		var psmPeptideCutoffsRootObjectForQueryObject_JSONString = JSON.stringify( psmPeptideCutoffsRootObjectForQuery );

		var excludeLinksWith_Root_JSONString = undefined;
		if ( removeNonUniquePSMs ) {
			var excludeLinksWith_Root = { removeNonUniquePSMs : removeNonUniquePSMs };
			excludeLinksWith_Root_JSONString = JSON.stringify( excludeLinksWith_Root );
		}
		
		var ajaxRequestData = {
				psmPeptideCutoffsForProjectSearchIds : psmPeptideCutoffsRootObjectForQueryObject_JSONString,
				excludeLinksWith_Root : excludeLinksWith_Root_JSONString,
				project_search_ids: project_search_ids,
				protein_id : requestContext.from_protein_id,
				protein_position_1 : requestContext.protein_position_1,
				protein_position_2 : requestContext.protein_position_2
		};
		var _URL = "services/data/getLooplinkProteinsPerSearchIdsProteinIdsPositions";
		$.ajax({
			type: "GET",
			url: _URL,
			dataType: "json",
			data: ajaxRequestData,  //  The data sent as params on the URL
			traditional: true,  //  Force traditional serialization of the data sent
			//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
			//   So project_search_ids array is passed as "project_search_ids=<value>" which is what Jersey expects
			success: function(data)	{
				try {
					decrementSpinner();
					getLooplinkDataCommonProcessResponse( { ajaxResponseData: data, ajaxRequestData: ajaxRequestData, requestContext : requestContext });
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			},
			failure: function(errMsg) {
				handleAJAXFailure( errMsg );
			},
			error: function(jqXHR, textStatus, errorThrown) {	
				handleAJAXError( jqXHR, textStatus, errorThrown );
			}
		});
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
}

///////////////////////////
function getLooplinkDataCommonProcessResponse({ ajaxResponseData, requestContext }) {
	
	const proteinsPerProjectSearchIdList = ajaxResponseData.proteinsPerProjectSearchIdList;
	
	openLinkInfoOverlayBackground();
	$("#loop_link_general_data").show();
	$("#cross_link_general_data").hide();
	$("#mono_link_general_data").hide();

	if ( requestContext.fcnToCallOnDisplay ) {
		requestContext.fcnToCallOnDisplay();
	}

	const $from_protein_id = $("#loop_link_from_protein_id");
	$from_protein_id.text( requestContext.from_protein_id );
	const $from_protein_name = $("#loop_link_from_protein_name");
	$from_protein_name.text( requestContext.from_protein_name );
	const $from_protein_position = $("#loop_link_from_protein_position");
	const $to_protein_position = $("#loop_link_to_protein_position");
	$from_protein_position.text( requestContext.protein_position_1 );
	$to_protein_position.text( requestContext.protein_position_2 );
	
	////////////////////////////
	const $data_container = $("#link_data_table_place_holder");
	$data_container.empty();

	//  Create "Display List" in order that searches are displayed in search details

	const proteinsPerProjectSearchIdList_DisplayOrder = [];
	{
		//  External Function:
		// const projectSearchIdSearchIdPairsInDisplayOrder_FromPage = getProjectSearchIdSearchIdPairsInDisplayOrder();
		const projectSearchIdsInDisplayOrder_FromPage = getProjectSearchIdsInDisplayOrder();

		for ( let index = 0; index < projectSearchIdsInDisplayOrder_FromPage.length; index++ ) {
			const projectSearchId_DisplayOrder = projectSearchIdsInDisplayOrder_FromPage[ index ];

			for ( const reportedPeptidesPerProjectSearchId_Entry of proteinsPerProjectSearchIdList ) {
				if ( reportedPeptidesPerProjectSearchId_Entry.projectSearchId === projectSearchId_DisplayOrder ) {
					//  Found entry in proteinsPerProjectSearchIdList for projectSearchId_DisplayOrder so save to result list
					const displayItem = { item : reportedPeptidesPerProjectSearchId_Entry, index };
					proteinsPerProjectSearchIdList_DisplayOrder.push( displayItem );
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

	// for ( let projectSearchIdIndex = 0; projectSearchIdIndex < projectSearchIdArray.length; projectSearchIdIndex++ ) {

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
		const image_structure_pages_shared_data_per_search_block_template_HTML = _image_structure_pages_shared_template.image_structure_pages_shared_data_per_search_block_template(context);
		const $protein_data_per_search_block = $(image_structure_pages_shared_data_per_search_block_template_HTML).appendTo($data_container);
		const link_info_table_jq_ClassName = "link_info_table_jq";
		const $link_info_table_jq = $protein_data_per_search_block.find("." + link_info_table_jq_ClassName );
		//			const $link_info_table_jq = $crosslink_protein_data_container.find(".link_info_table_jq");
		if ( $link_info_table_jq.length === 0 ) {
			throw Error( "unable to find HTML element with class '" + link_info_table_jq_ClassName + "'" );
		}
		//  Add protein data to the page
		for ( let proteinIndex = 0; proteinIndex < proteinsForSearch.length ; proteinIndex++ ) {
			const proteinData = proteinsForSearch[ proteinIndex ];
			const dataRowContext = { 
				colorIndex_OneBased,
					data : proteinData, 
					projectSearchId : proteinsPerProjectSearchEntry.projectSearchId,
					from_protein_id : requestContext.from_protein_id,
					from_protein_position : requestContext.protein_position_1,
					to_protein_position : requestContext.protein_position_2,
					isLooplink : true 
			};
			const html = _image_structure_pages_shared_template.image_structure_pages_shared_data_per_search_data_row_template( dataRowContext );
			const $search_entry_data_row = $( html ).appendTo( $link_info_table_jq );
			const $search_entry_data_row__columns = $search_entry_data_row.find("td");
			const search_entry_data_row__columns = $search_entry_data_row__columns.length;
			const contextChildRow = { 
					colSpan : search_entry_data_row__columns
			};
			const htmlChildRow = _image_structure_pages_shared_template.image_structure_pages_shared_data_per_search_child_row_template( contextChildRow );
//			const $search_entry_child_row = 
			$( htmlChildRow ).appendTo( $link_info_table_jq );
//			If only one record, click on it to show it's children
			if ( proteinsPerProjectSearchIdList_DisplayOrder.length === 1 ) {
				$search_entry_data_row.click();
			}
		}  
		addToolTips( $protein_data_per_search_block );
	} // END:  For Each Project Search Id
	linkInfoOverlayWidthResizer();
}

/////////////////////////////////
//////////////Process CROSS link
/////////////////////
function getCrosslinkDataCommon( params ) {
	var psmPeptideCutoffsRootObject = params.psmPeptideCutoffsRootObject;
	var removeNonUniquePSMs = params.removeNonUniquePSMs;
	var requestContext = params.context;
	
	var project_search_ids = requestContext.searchesArray;
	//  Extract psm peptide cutoffs for project search ids provided
	var psmPeptideCutoffsRootObjectForQuery = { searches : {} };
	var searches = psmPeptideCutoffsRootObject.searches;
	var querySearches = psmPeptideCutoffsRootObjectForQuery.searches;
	for ( var project_search_idsIndex = 0; project_search_idsIndex < project_search_ids.length; project_search_idsIndex++ ) {
		var projectSearchId = project_search_ids[ project_search_idsIndex ];
		var projectSearchIdString = projectSearchId.toString();
		var cutoffForProjectSearchId = searches[ projectSearchIdString ];
		querySearches[ projectSearchIdString ] = cutoffForProjectSearchId;
	} 
	///   Serialize cutoffs to JSON
	var psmPeptideCutoffsRootObjectForQueryObject_JSONString = JSON.stringify( psmPeptideCutoffsRootObjectForQuery );
	
	var excludeLinksWith_Root_JSONString = undefined;
	if ( removeNonUniquePSMs ) {
		var excludeLinksWith_Root = { removeNonUniquePSMs : removeNonUniquePSMs };
		excludeLinksWith_Root_JSONString = JSON.stringify( excludeLinksWith_Root );
	}
	
	var ajaxRequestData = {
			psmPeptideCutoffsForProjectSearchIds : psmPeptideCutoffsRootObjectForQueryObject_JSONString,
			excludeLinksWith_Root : excludeLinksWith_Root_JSONString,
			project_search_ids: project_search_ids,
			protein_1_id: requestContext.protein1.protein_id_int,
			protein_2_id: requestContext.protein2.protein_id_int,
			protein_1_position: requestContext.protein1.protein_position,
			protein_2_position: requestContext.protein2.protein_position
	};
	var _URL = "services/data/getCrosslinkProteinsPerSearchIdsProteinIdsPositions";
	$.ajax({
		type: "GET",
		url: _URL,
		dataType: "json",
		data: ajaxRequestData,  //  The data sent as params on the URL
		traditional: true,  //  Force traditional serialization of the data sent
		//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
		//   So project_search_ids array is passed as "project_search_ids=<value>" which is what Jersey expects
		success: function(data)	{
			try {
				decrementSpinner();
				var getCrosslinkDataCommonProcessResponseParams = 
				{ 
						ajaxResponseData: data, 
						ajaxRequestData: ajaxRequestData,
						requestContext: requestContext
				};
				getCrosslinkDataCommonProcessResponse( getCrosslinkDataCommonProcessResponseParams );
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		},
		failure: function(errMsg) {
			handleAJAXFailure( errMsg );
		},
		error: function(jqXHR, textStatus, errorThrown) {	
			handleAJAXError( jqXHR, textStatus, errorThrown );
		}
	});
}

function getCrosslinkDataCommonProcessResponse({ ajaxResponseData, requestContext }) {

	const proteinsPerProjectSearchIdList = ajaxResponseData.proteinsPerProjectSearchIdList;
	
	openLinkInfoOverlayBackground();
	$("#cross_link_general_data").show();
	$("#loop_link_general_data").hide();
	$("#mono_link_general_data").hide();

	if ( requestContext.fcnToCallOnDisplay ) {
		requestContext.fcnToCallOnDisplay();
	}

	const $from_protein_id = $("#cross_link_from_protein_id");
	const $to_protein_id = $("#cross_link_to_protein_id");
	$from_protein_id.text( requestContext.protein1.protein_id_int );
	$to_protein_id.text( requestContext.protein2.protein_id_int );
	const $cross_link_protein_name_1 = $("#cross_link_protein_name_1");
	const $cross_link_protein_name_2 = $("#cross_link_protein_name_2");
	$cross_link_protein_name_1.text( requestContext.protein1.protein_name );
	$cross_link_protein_name_2.text( requestContext.protein2.protein_name );
	const $cross_link_protein_position_1 = $("#cross_link_protein_position_1");
	const $cross_link_protein_position_2 = $("#cross_link_protein_position_2");
	$cross_link_protein_position_1.text( requestContext.protein1.protein_position );
	$cross_link_protein_position_2.text( requestContext.protein2.protein_position );

	////////////////////////////
	const $data_container = $("#link_data_table_place_holder");
	$data_container.empty();

	//  Create "Display List" in order that searches are displayed in search details

	const proteinsPerProjectSearchIdList_DisplayOrder = [];
	{
		//  External Function:
		// const projectSearchIdSearchIdPairsInDisplayOrder_FromPage = getProjectSearchIdSearchIdPairsInDisplayOrder();
		const projectSearchIdsInDisplayOrder_FromPage = getProjectSearchIdsInDisplayOrder();

		for ( let index = 0; index < projectSearchIdsInDisplayOrder_FromPage.length; index++ ) {
			const projectSearchId_DisplayOrder = projectSearchIdsInDisplayOrder_FromPage[ index ];

			for ( const reportedPeptidesPerProjectSearchId_Entry of proteinsPerProjectSearchIdList ) {
				if ( reportedPeptidesPerProjectSearchId_Entry.projectSearchId === projectSearchId_DisplayOrder ) {
					//  Found entry in proteinsPerProjectSearchIdList for projectSearchId_DisplayOrder so save to result list
					const displayItem = { item : reportedPeptidesPerProjectSearchId_Entry, index };
					proteinsPerProjectSearchIdList_DisplayOrder.push( displayItem );
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

	// for ( let projectSearchIdIndex = 0; projectSearchIdIndex < projectSearchIdArray.length; projectSearchIdIndex++ ) {

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
		const image_structure_pages_shared_data_per_search_block_template_HTML = _image_structure_pages_shared_template.image_structure_pages_shared_data_per_search_block_template(context);
		const $protein_data_per_search_block = $(image_structure_pages_shared_data_per_search_block_template_HTML).appendTo($data_container);
		const link_info_table_jq_ClassName = "link_info_table_jq";
		const $link_info_table_jq = $protein_data_per_search_block.find("." + link_info_table_jq_ClassName );
		//			const $link_info_table_jq = $crosslink_protein_data_container.find(".link_info_table_jq");
		if ( $link_info_table_jq.length === 0 ) {
			throw Error( "unable to find HTML element with class '" + link_info_table_jq_ClassName + "'" );
		}
		//  Add protein data to the page
		for ( let proteinIndex = 0; proteinIndex < proteinsForSearch.length ; proteinIndex++ ) {
			const proteinData = proteinsForSearch[ proteinIndex ];
			const dataRowContext = { 
				colorIndex_OneBased,
					data : proteinData, 
					projectSearchId : proteinsPerProjectSearchEntry.projectSearchId,
					from_protein_id : requestContext.protein1.protein_id_int,
					to_protein_id : requestContext.protein2.protein_id_int,
					from_protein_position : requestContext.protein1.protein_position,
					to_protein_position : requestContext.protein2.protein_position,
					isCrosslink : true 
			};
			const html = _image_structure_pages_shared_template.image_structure_pages_shared_data_per_search_data_row_template( dataRowContext );
			const $search_entry_data_row = $(html).appendTo( $link_info_table_jq );
			const $search_entry_data_row__columns = $search_entry_data_row.find("td");
			const search_entry_data_row__columns = $search_entry_data_row__columns.length;
			const contextChildRow = { 
					colSpan : search_entry_data_row__columns
			};
			const htmlChildRow = _image_structure_pages_shared_template.image_structure_pages_shared_data_per_search_child_row_template( contextChildRow );
//			const $search_entry_child_row = 
			$( htmlChildRow ).appendTo( $link_info_table_jq );
			//  If only one record, click on it to show it's children
			if ( proteinsPerProjectSearchIdList_DisplayOrder.length === 1 ) {
				$search_entry_data_row.click();
			}
		}  
		addToolTips( $protein_data_per_search_block );
	} // END:  For Each Search Id
	linkInfoOverlayWidthResizer();
}

//////////////  Process Mono link

/////////////////////
function getMonolinkDataCommon( params ) {
	var psmPeptideCutoffsRootObject = params.psmPeptideCutoffsRootObject;
	var removeNonUniquePSMs = params.removeNonUniquePSMs;
	var requestContext = params.context;
	
	var project_search_ids = requestContext.searchesArray;
	//  Extract psm peptide cutoffs for project search ids provided
	var psmPeptideCutoffsRootObjectForQuery = { searches : {} };
	var searches = psmPeptideCutoffsRootObject.searches;
	var querySearches = psmPeptideCutoffsRootObjectForQuery.searches;
	for ( var project_search_idsIndex = 0; project_search_idsIndex < project_search_ids.length; project_search_idsIndex++ ) {
		var projectSearchId = project_search_ids[ project_search_idsIndex ];
		var projectSearchIdString = projectSearchId.toString();
		var cutoffForProjectSearchId = searches[ projectSearchIdString ];
		querySearches[ projectSearchIdString ] = cutoffForProjectSearchId;
	} 
	///   Serialize cutoffs to JSON
	var psmPeptideCutoffsRootObjectForQueryObject_JSONString = JSON.stringify( psmPeptideCutoffsRootObjectForQuery );
	
	var excludeLinksWith_Root_JSONString = undefined;
	if ( removeNonUniquePSMs ) {
		var excludeLinksWith_Root = { removeNonUniquePSMs : removeNonUniquePSMs };
		excludeLinksWith_Root_JSONString = JSON.stringify( excludeLinksWith_Root );
	}
	
	var ajaxRequestData = {
			psmPeptideCutoffsForProjectSearchIds : psmPeptideCutoffsRootObjectForQueryObject_JSONString,
			excludeLinksWith_Root : excludeLinksWith_Root_JSONString,
			project_search_ids: project_search_ids,
			protein_id : requestContext.from_protein_id,
			protein_position : requestContext.protein_position
	};
	var _URL = "services/data/getMonolinkProteinsPerSearchIdsProteinIdsPositions";
	$.ajax({
		type: "GET",
		url: _URL,
		dataType: "json",
		data: ajaxRequestData,  //  The data sent as params on the URL
		traditional: true,  //  Force traditional serialization of the data sent
		//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
		//   So project_search_ids array is passed as "project_search_ids=<value>" which is what Jersey expects
		success: function(data)	{
			try {
				decrementSpinner();
				getMonolinkDataCommonProcessResponse( { ajaxResponseData: data, ajaxRequestData: ajaxRequestData, requestContext : requestContext });
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		},
		failure: function(errMsg) {
			handleAJAXFailure( errMsg );
		},
		error: function(jqXHR, textStatus, errorThrown) {	
			handleAJAXError( jqXHR, textStatus, errorThrown );
		}
	});
}

/////////////////////////
function getMonolinkDataCommonProcessResponse({ ajaxResponseData, requestContext }) {

	const proteinsPerProjectSearchIdList = ajaxResponseData.proteinsPerProjectSearchIdList;
	
	openLinkInfoOverlayBackground();
	$("#mono_link_general_data").show();
	$("#loop_link_general_data").hide();
	$("#cross_link_general_data").hide();

	if ( requestContext.fcnToCallOnDisplay ) {
		requestContext.fcnToCallOnDisplay();
	}

	const $from_protein_id = $("#mono_link_from_protein_id");
	$from_protein_id.text( requestContext.from_protein_id );
	const $from_protein_name = $("#mono_link_from_protein_name");
	$from_protein_name.text( requestContext.from_protein_name );
	const $protein_position = $("#mono_link_protein_position");
	$protein_position.text( requestContext.protein_position );

	////////////////////////////
	const $data_container = $("#link_data_table_place_holder");
	$data_container.empty();

	//  Create "Display List" in order that searches are displayed in search details

	const proteinsPerProjectSearchIdList_DisplayOrder = [];
	{
		//  External Function:
		// const projectSearchIdSearchIdPairsInDisplayOrder_FromPage = getProjectSearchIdSearchIdPairsInDisplayOrder();
		const projectSearchIdsInDisplayOrder_FromPage = getProjectSearchIdsInDisplayOrder();

		for ( let index = 0; index < projectSearchIdsInDisplayOrder_FromPage.length; index++ ) {
			const projectSearchId_DisplayOrder = projectSearchIdsInDisplayOrder_FromPage[ index ];

			for ( const reportedPeptidesPerProjectSearchId_Entry of proteinsPerProjectSearchIdList ) {
				if ( reportedPeptidesPerProjectSearchId_Entry.projectSearchId === projectSearchId_DisplayOrder ) {
					//  Found entry in proteinsPerProjectSearchIdList for projectSearchId_DisplayOrder so save to result list
					const displayItem = { item : reportedPeptidesPerProjectSearchId_Entry, index };
					proteinsPerProjectSearchIdList_DisplayOrder.push( displayItem );
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

	// for ( let projectSearchIdIndex = 0; projectSearchIdIndex < projectSearchIdArray.length; projectSearchIdIndex++ ) {

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
		const image_structure_pages_shared_data_per_search_block_template_HTML = _image_structure_pages_shared_template.image_structure_pages_shared_data_per_search_block_template(context);
		const $protein_data_per_search_block = $(image_structure_pages_shared_data_per_search_block_template_HTML).appendTo($data_container);
		const link_info_table_jq_ClassName = "link_info_table_jq";
		const $link_info_table_jq = $protein_data_per_search_block.find("." + link_info_table_jq_ClassName );
		//			const $link_info_table_jq = $crosslink_protein_data_container.find(".link_info_table_jq");
		if ( $link_info_table_jq.length === 0 ) {
			throw Error( "unable to find HTML element with class '" + link_info_table_jq_ClassName + "'" );
		}
		//  Add protein data to the page
		for ( let proteinIndex = 0; proteinIndex < proteinsForSearch.length ; proteinIndex++ ) {
			const proteinData = proteinsForSearch[ proteinIndex ];
			const dataRowContext = { 
				colorIndex_OneBased,
					data : proteinData, 
					projectSearchId : proteinsPerProjectSearchEntry.projectSearchId,
					from_protein_id : requestContext.from_protein_id,
					from_protein_position : requestContext.protein_position,
					isMonolink : true 
			};
			const html = _image_structure_pages_shared_template.image_structure_pages_shared_data_per_search_data_row_template( dataRowContext );
			const $search_entry_data_row = $( html ).appendTo( $link_info_table_jq );
			const $search_entry_data_row__columns = $search_entry_data_row.find("td");
			const search_entry_data_row__columns = $search_entry_data_row__columns.length;
			const contextChildRow = { 
					colSpan : search_entry_data_row__columns
			};
			const htmlChildRow = _image_structure_pages_shared_template.image_structure_pages_shared_data_per_search_child_row_template( contextChildRow );
//			const $search_entry_child_row = 
			$( htmlChildRow ).appendTo( $link_info_table_jq );
			//  If only one record, click on it to show it's children
			if ( proteinsPerProjectSearchIdList_DisplayOrder.length === 1 ) {
				$search_entry_data_row.click();
			}
		}  
		addToolTips( $protein_data_per_search_block );
	} // END:  For Each Search Id
	linkInfoOverlayWidthResizer();
}

///////////////////////////////////////////////////////////////////////////
///  Overlay general processing
var openLinkInfoOverlayBackground = function (  ) {
//	Close any open Lorikeet Overlay
	closeLorikeetOverlay();
	var $view_link_info_modal_dialog_overlay_background = $("#view_link_info_modal_dialog_overlay_background");
	var $view_link_info_overlay_div = $("#view_link_info_overlay_div");
//	Adjust the overlay positon to be within the viewport
	var scrollTopWindow = $(window).scrollTop();
	if ( scrollTopWindow > 0 ) {
//		User has scrolled down
		var overlayTop = scrollTopWindow + 10;
		$view_link_info_overlay_div.css( { top: overlayTop + "px" } );
	} else {
		$view_link_info_overlay_div.css( { top: "10px" } );
	}
	$view_link_info_modal_dialog_overlay_background.show();
	$view_link_info_overlay_div.show();
};

/////////////////////
///  Overlay general processing
var closeViewLinkInfoOverlay = function (  ) {
	$("#view_link_info_modal_dialog_overlay_background").hide();
	$(".view-link-info-overlay-div").hide();
};

///////////////////////////////////////////////////////////////////////////
///  Attach Overlay Click handlers
var attachViewLinkInfoOverlayClickHandlers = function (  ) {
	$(".view-link-info-overlay-X-for-exit-overlay").click( function( eventObject ) {
		try {
			closeViewLinkInfoOverlay();
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	} );
	$("#view_link_info_modal_dialog_overlay_background").click( function( eventObject ) {
		try {
			closeViewLinkInfoOverlay();
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	} );
	$(".error-message-ok-button").click( function( eventObject ) {
		try {
			closeViewLinkInfoOverlay();
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	} );

	_image_structure_click_element_common__ValidateInitialState();

};

///////////////////////////////////////////////////////////////////////////
///  Resize overlay
var linkInfoOverlayWidthResizer = function() {
	var $link_info_table__tbody_jq_Entries = $(".link_info_table__tbody_jq");
	var max_link_info_table_width = 0;
	$link_info_table__tbody_jq_Entries.each( function( index, element ) {
		var $link_info_table__tbody_jq_Entry = $( this ); 
		var link_info_table_width = $link_info_table__tbody_jq_Entry.outerWidth( true /* [ includeMargin ] */ );
		if ( max_link_info_table_width < link_info_table_width ) {
			max_link_info_table_width = link_info_table_width;
		}
	});
	//  Adjust width of link info overlay to be 40 pixels wider than the link info table
	var view_link_info_overlay_div = max_link_info_table_width + 40;
	$("#view_link_info_overlay_div").css( {"width": view_link_info_overlay_div } );
};


//  Attach to window since code not in front end build still calls this function
window.linkInfoOverlayWidthResizer = linkInfoOverlayWidthResizer;

export { getLooplinkDataCommon, getCrosslinkDataCommon, getMonolinkDataCommon, attachViewLinkInfoOverlayClickHandlers }
