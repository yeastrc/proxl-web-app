
//  viewMergedProteinAllPage.js

//  Javascript for the viewMergedProteinAll.jsp page

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";


//Import header_main.js and children to ensure on the page
import { header_mainVariable } from 'page_js/header_section_js_all_pages_main_pages/header_section_main_pages/header_main.js';


import { createSpinner, destroySpinner, incrementSpinner, decrementSpinner }  from 'page_js/header_section_js_all_pages_main_pages/header_section_every_page/spinner.js';

import { getProjectSearchIdSearchIdPairsInDisplayOrder, getProjectSearchIdsInDisplayOrder } from 'page_js/data_pages/project_search_ids_driven_pages/common/getProjectSearchIdSearchIdPairsInDisplayOrder.js';

import { DataPages_LoggedInUser_CommonObjectsFactory } from 'page_js/data_pages/data_pages_common/dataPages_LoggedInUser_CommonObjectsFactory.js';

import { onDocumentReady, viewSearchProteinPageCommonCrosslinkLooplinkCoverage } from 'page_js/data_pages/project_search_ids_driven_pages/protein__protein_coverage__shared/viewProteinPageCommonCrosslinkLooplinkCoverageSearchMerged.js';

// For drill down 
import { viewProteinSingleForMergedProteinAllPageLoadedFromWebServiceTemplate } from 'page_js/data_pages/project_search_ids_driven_pages/protein_pages/viewProteinSingleForMergedProteinAllPageLoadedFromWebServiceTemplate.js';

import { Protein_AllProteins_PageData_MultipleSearches_FromWebservice } from './protein_AllProteins_MultipleSearches_PageData_FromWebservice.js';

import { MergedPages_ShowSearchesAndVennDiagram_AboveMainTable } from 'page_js/data_pages/project_search_ids_driven_pages/common_merged_pages/mergedPages_ShowSearchesAndVennDiagram_AboveMainTable.js';

//   Code in protein_ProteinAll_MultipleSearches_PageData_FromWebservice.js handles loading and displaying of main data table, and entry options in top filter part of page


$(document).ready(function() { 
	onDocumentReady();
}); // end $(document).ready(function()


//  Constructor

class ViewMergedProteinAllPageCode {

	/**
	 * 
	 */
	constructor() {
		const dataPages_LoggedInUser_CommonObjectsFactory = new DataPages_LoggedInUser_CommonObjectsFactory();

		this._saveView_dataPages = dataPages_LoggedInUser_CommonObjectsFactory.instantiate_SaveView_dataPages();

        this._mergedPages_ShowSearchesAndVennDiagram_AboveMainTable = new MergedPages_ShowSearchesAndVennDiagram_AboveMainTable();
	}

	//////////////
	//  function called from $(document).ready(function() { ... }
	initialize() {

		this._initialDisplayOfAboveTableSection();

        createSpinner();				// create spinner

		//  External Function:
		const projectSearchIdsInDisplayOrder_FromPage = getProjectSearchIdsInDisplayOrder();

		const proteinQueryJSONRoot = viewSearchProteinPageCommonCrosslinkLooplinkCoverage.getQueryJSONObject();

		const callback = this._callbackAfterDataLoad_DOM_AboveMainTableUpdated.bind( this );

		this._protein_AllProteins_PageData_MultipleSearches_FromWebservice = new Protein_AllProteins_PageData_MultipleSearches_FromWebservice();
		
		this._protein_AllProteins_PageData_MultipleSearches_FromWebservice.initialize({ 
			projectSearchIds : projectSearchIdsInDisplayOrder_FromPage, proteinQueryJSONRoot, callbackAfterDataLoad_DOM_AboveMainTableUpdated : callback, destroySpinner });
	};

	/**
	 * Callback after Data is loaded
	 */ 
	_callbackAfterDataLoad_DOM_AboveMainTableUpdated({ viewSearchProteinsPageDataRoot }) {
		try {
			viewSearchProteinPageCommonCrosslinkLooplinkCoverage.createPartsAboveMainTableSearchProteinPageCommon( {} );
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
		try {
			this._finalDisplayOfAboveTableSection({ viewSearchProteinsPageDataRoot });
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
		try {
			this.initialize_saveView_dataPages();
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	}
	
	//////////////
	initialize_saveView_dataPages() {

		this._saveView_dataPages.initialize({ /* projectSearchIds, container_DOM_Element, enableSetDefault */ });
	}

	///////////////////////
	//   Called by "onclick" on HTML element
	updatePageForFormParams() {
		try {
			viewSearchProteinPageCommonCrosslinkLooplinkCoverage.updatePageForFormParams();
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};


	/**
	 * 
	 */
	_initialDisplayOfAboveTableSection() {
        
        const $search_details_and_main_filter_criteria_main_page_root = $("#search_details_and_main_filter_criteria_main_page_root");

        //  is currently a <table> so:
        //    get all rows and hide them
        //    then get row for .selector_search_details_searches_list_root_row and show
        //    show table element

        const $tbody = $search_details_and_main_filter_criteria_main_page_root.children("tbody");
        const $allTR = $tbody.children("tr");
		$allTR.hide();
		
		const $cutoff_per_search_block_tr_jq_All = $search_details_and_main_filter_criteria_main_page_root.find(".cutoff_per_search_block_tr_jq");
		$cutoff_per_search_block_tr_jq_All.hide();

        const $selector_search_details_searches_list_root_row = $tbody.children(".selector_search_details_searches_list_root_row");
        $selector_search_details_searches_list_root_row.show();
        $search_details_and_main_filter_criteria_main_page_root.show();
    }

	/**
	 * called after main table populated and before displayed
	 */
	_finalDisplayOfAboveTableSection({ viewSearchProteinsPageDataRoot }) {
        
        const $search_details_and_main_filter_criteria_main_page_root = $("#search_details_and_main_filter_criteria_main_page_root");

		const $cutoff_per_search_block_tr_jq_All = $search_details_and_main_filter_criteria_main_page_root.find(".cutoff_per_search_block_tr_jq");
		$cutoff_per_search_block_tr_jq_All.show();

        //  is currently a <table> so:
        //    get all rows and show them

        const $tbody = $search_details_and_main_filter_criteria_main_page_root.children("tbody");
        const $allTR = $tbody.children("tr");
        $allTR.show();

        const $main_items_below_filter_criteria_table_above_main_display_table = $("#main_items_below_filter_criteria_table_above_main_display_table");
		$main_items_below_filter_criteria_table_above_main_display_table.show();
		

        this._mergedPages_ShowSearchesAndVennDiagram_AboveMainTable.initialize({ 
            searchCountList : viewSearchProteinsPageDataRoot.searchCountList, 
            vennDiagramDataToJSON : viewSearchProteinsPageDataRoot.vennDiagramDataToJSON });
        
    }
	
};

//  Instance of class

const viewMergedProteinAllPageCode = new ViewMergedProteinAllPageCode();

window.viewMergedProteinAllPageCode = viewMergedProteinAllPageCode;


$(document).ready(function() { 
	window.viewMergedProteinAllPageCode.initialize();
}); // end $(document).ready(function()

		
