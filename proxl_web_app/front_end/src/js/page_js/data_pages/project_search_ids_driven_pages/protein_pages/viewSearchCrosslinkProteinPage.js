
//  viewSearchCrosslinkProteinPage.js

//  Javascript for the viewSearchCrosslinkCrosslinkProtein.jsp page

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";

//Import header_main.js and children to ensure on the page
import { header_mainVariable } from 'page_js/header_section_js_all_pages_main_pages/header_section_main_pages/header_main.js';

//  Import to make available on the page
import { defaultPageView } from 'page_js/data_pages/project_search_ids_driven_pages/common/defaultPageView.js';


import { getProjectSearchIdSearchIdPairsInDisplayOrder, getProjectSearchIdsInDisplayOrder } from 'page_js/data_pages/project_search_ids_driven_pages/common/getProjectSearchIdSearchIdPairsInDisplayOrder.js';

import { DataPages_LoggedInUser_CommonObjectsFactory } from 'page_js/data_pages/data_pages_common/dataPages_LoggedInUser_CommonObjectsFactory.js';


import { onDocumentReady, viewSearchProteinPageCommonCrosslinkLooplinkCoverage } from 'page_js/data_pages/project_search_ids_driven_pages/protein__protein_coverage__shared/viewProteinPageCommonCrosslinkLooplinkCoverageSearchMerged.js';

//  For showing Data for links (Drilldown) (Called by HTML onclick):
import { viewCrosslinkReportedPeptidesLoadedFromWebServiceTemplate } from 'page_js/data_pages/project_search_ids_driven_pages/protein_pages/viewCrosslinkReportedPeptidesLoadedFromWebServiceTemplate.js';


import { createSpinner, destroySpinner, incrementSpinner, decrementSpinner }  from 'page_js/header_section_js_all_pages_main_pages/header_section_every_page/spinner.js';


import { Protein_crosslink_PageData_SingleSearch_FromWebservice } from './protein_crosslink_SingleSearch_PageData_FromWebservice.js';

//   Code in protein_crosslink_SingleSearch_PageData_FromWebservice.js handles loading and displaying of main data table, and entry options in top filter part of page



$(document).ready(function() { 
	onDocumentReady();
}); // end $(document).ready(function()


//  Constructor

class ViewSearchCrosslinkProteinPageCode {

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

		var objectThis = this;

        createSpinner();				// create spinner

		this._initialDisplayOfAboveTableSection();

		//  External Function:
		const projectSearchIdsInDisplayOrder_FromPage = getProjectSearchIdsInDisplayOrder();
		const projectSearchId = projectSearchIdsInDisplayOrder_FromPage[ 0 ];

		const proteinQueryJSONRoot = viewSearchProteinPageCommonCrosslinkLooplinkCoverage.getQueryJSONObject();

		const callback = this._callbackAfterDataLoad_DOM_AboveMainTableUpdated.bind( this );

		this._protein_crosslink_PageData_SingleSearch_FromWebservice = new Protein_crosslink_PageData_SingleSearch_FromWebservice();
		
		this._protein_crosslink_PageData_SingleSearch_FromWebservice.initialize({ 
			projectSearchId, proteinQueryJSONRoot, callbackAfterDataLoad_DOM_AboveMainTableUpdated : callback, destroySpinner });
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
        const $selector_search_details_searches_list_root_row = $tbody.children(".selector_search_details_searches_list_root_row");
        $selector_search_details_searches_list_root_row.show();
        $search_details_and_main_filter_criteria_main_page_root.show();
    }

	/**
	 * called after main table populated and before displayed
	 */
	_finalDisplayOfAboveTableSection() {
        
        const $search_details_and_main_filter_criteria_main_page_root = $("#search_details_and_main_filter_criteria_main_page_root");

        //  is currently a <table> so:
        //    get all rows and show them

        const $tbody = $search_details_and_main_filter_criteria_main_page_root.children("tbody");
        const $allTR = $tbody.children("tr");
        $allTR.show();

        const $main_items_below_filter_criteria_table_above_main_display_table = $("#main_items_below_filter_criteria_table_above_main_display_table");
        $main_items_below_filter_criteria_table_above_main_display_table.show();
    }

};

//  Instance of class

const viewSearchCrosslinkProteinPageCode = new ViewSearchCrosslinkProteinPageCode();

window.viewSearchCrosslinkProteinPageCode = viewSearchCrosslinkProteinPageCode;


$(document).ready(function() { 
	viewSearchCrosslinkProteinPageCode.initialize();
}); // end $(document).ready(function()

