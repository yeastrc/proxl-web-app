
//  viewProteinCoverageReport.js

//  Javascript for the viewProteinCoverageReport.jsp page

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";


//Import header_main.js and children to ensure on the page
import { header_mainVariable } from 'page_js/header_section_js_all_pages_main_pages/header_section_main_pages/header_main.js';


import { createSpinner, destroySpinner, incrementSpinner, decrementSpinner }  from 'page_js/header_section_js_all_pages_main_pages/header_section_every_page/spinner.js';

import { getProjectSearchIdSearchIdPairsInDisplayOrder, getProjectSearchIdsInDisplayOrder } from 'page_js/data_pages/project_search_ids_driven_pages/common/getProjectSearchIdSearchIdPairsInDisplayOrder.js';

import { DataPages_LoggedInUser_CommonObjectsFactory } from 'page_js/data_pages/data_pages_common/dataPages_LoggedInUser_CommonObjectsFactory.js';

import { onDocumentReady, viewSearchProteinPageCommonCrosslinkLooplinkCoverage } from 'page_js/data_pages/project_search_ids_driven_pages/protein__protein_coverage__shared/viewProteinPageCommonCrosslinkLooplinkCoverageSearchMerged.js';

import { Protein_Coverage_SingleSearch_MultipleSearches_PageData_FromWebservice } from './protein_Coverage_SingleSearch_MultipleSearches_PageData_FromWebservice.js';

//   Code in viewProteinCoverageReport.js handles loading and displaying of main data table, and entry options in top filter part of page


$(document).ready(function() { 
	onDocumentReady();
}); // end $(document).ready(function()


//  Constructor

class ViewProteinCoverageReport {

	/**
	 * 
	 */
	constructor() {
		const dataPages_LoggedInUser_CommonObjectsFactory = new DataPages_LoggedInUser_CommonObjectsFactory();

		this._saveView_dataPages = dataPages_LoggedInUser_CommonObjectsFactory.instantiate_SaveView_dataPages();
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

		this._protein_Coverage_SingleSearch_MultipleSearches_PageData_FromWebservice = new Protein_Coverage_SingleSearch_MultipleSearches_PageData_FromWebservice();
		
		this._protein_Coverage_SingleSearch_MultipleSearches_PageData_FromWebservice.initialize({ 
			projectSearchIds : projectSearchIdsInDisplayOrder_FromPage, proteinQueryJSONRoot, callbackAfterDataLoad_DOM_AboveMainTableUpdated : callback, destroySpinner });
	};

	/**
	 * Callback after Data is loaded
	 */ 
	_callbackAfterDataLoad_DOM_AboveMainTableUpdated() {
		try {
			viewSearchProteinPageCommonCrosslinkLooplinkCoverage.createPartsAboveMainTableSearchProteinPageCommon( {} );
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
		try {
			this._finalDisplayOfAboveTableSection();
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
	_finalDisplayOfAboveTableSection() {
        
        const $search_details_and_main_filter_criteria_main_page_root = $("#search_details_and_main_filter_criteria_main_page_root");

		const $cutoff_per_search_block_tr_jq_All = $search_details_and_main_filter_criteria_main_page_root.find(".cutoff_per_search_block_tr_jq");
		$cutoff_per_search_block_tr_jq_All.show();

        //  is currently a <table> so:
        //    get all rows and show them

        const $tbody = $search_details_and_main_filter_criteria_main_page_root.children("tbody");
        const $allTR = $tbody.children("tr");
        $allTR.show();

        //  Show <div> containing rest after filters table
        const $main_rest_after_filters_table_container = $("#main_rest_after_filters_table_container");
        if ( $main_rest_after_filters_table_container.length === 0 ) {
            throw Error("No DOM element with id 'main_rest_after_filters_table_container'");
        }
        $main_rest_after_filters_table_container.show();
    }
};

//  Instance of class

const viewProteinCoverageReportPageCode = new ViewProteinCoverageReport();

window.viewProteinCoverageReportPageCode = viewProteinCoverageReportPageCode;


$(document).ready(function() { 
	window.viewProteinCoverageReportPageCode.initialize();
}); // end $(document).ready(function()

		
