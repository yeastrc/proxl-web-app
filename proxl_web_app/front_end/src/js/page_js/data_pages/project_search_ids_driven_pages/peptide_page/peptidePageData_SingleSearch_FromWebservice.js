/**
 * peptidePageData_SingleSearch_FromWebservice.js
 * 
 * Javascript for Peptide Single Search page to get the main table from webservice and display using Handlebars template
 * 
 */

/**
 * JavaScript directive:   all variables have to be declared with "var", maybe other things
 */
"use strict";

//  Import Handlebars templates

const _peptide_page_template = 
require("../../../../../../handlebars_templates_precompiled/peptide_page/peptide_page_template-bundle.js");

//  module import 

import { reportWebErrorToServer } from 'page_js/header_section_js_all_pages_main_pages/header_section_every_page/reportWebErrorToServer.js';

import { webserviceCallStandardPost } from 'page_js/webservice_call_common/webserviceCallStandardPost.js';

import { addToolTips } from 'page_js/header_section_js_all_pages_main_pages/header_section_every_page/genericToolTip.js';

import { createTooltipForProteinNames } from 'page_js/data_pages/project_search_ids_driven_pages/common/createTooltipForProteinNames.js';

import { createSpinner, destroySpinner, incrementSpinner, decrementSpinner }  from 'page_js/header_section_js_all_pages_main_pages/header_section_every_page/spinner.js';


/**
 * 
 */
export class PeptidePageData_SingleSearch_FromWebservice {

	/**
	 * 
	 */
	constructor() {

		this._initializeCalled = false;

		this._blockContentsIntialized = false;  //  Are the contents of the block initialized (initialized on first open)
		
		if (!_peptide_page_template.peptidePage_SingleSearch_MainTable_table) {
			throw Error("Nothing in _peptide_page_template.peptidePage_SingleSearch_MainTable_table");
		}
        this._peptidePage_SingleSearch_MainTable_table_template = _peptide_page_template.peptidePage_SingleSearch_MainTable_table;

		if (!_peptide_page_template.peptidePage_SingleSearch_MainTable_header_row) {
			throw Error("Nothing in _peptide_page_template.peptidePage_SingleSearch_MainTable_header_row");
		}
        this._peptidePage_SingleSearch_MainTable_header_row_template = _peptide_page_template.peptidePage_SingleSearch_MainTable_header_row;

		if (!_peptide_page_template.peptidePage_SingleSearch_MainTable_MainDataRow) {
			throw Error("Nothing in _peptide_page_template.peptidePage_SingleSearch_MainTable_MainDataRow");
		}
        this._peptidePage_SingleSearch_MainTable_MainDataRow_template = _peptide_page_template.peptidePage_SingleSearch_MainTable_MainDataRow;

		if (!_peptide_page_template.peptidePage_SingleSearch_MainTable_child_table_container_row) {
			throw Error("Nothing in _peptide_page_template.peptidePage_SingleSearch_MainTable_child_table_container_row");
		}
        this._peptidePage_SingleSearch_MainTable_child_table_container_row_template = _peptide_page_template.peptidePage_SingleSearch_MainTable_child_table_container_row;
    }

	/**
	 * 
	 */
	initialize({ projectSearchId, peptideQueryJSONRoot }) {
		
        const objectThis = this;
        
        this._initialDisplayOfAboveTableSection();

        createSpinner();				// create spinner

        const promise_getData_OnServer = this._getData_OnServer({ projectSearchId, peptideQueryJSONRoot });

        promise_getData_OnServer.catch( ( reason ) => {} );
     
        promise_getData_OnServer.then( ({ responseData }) => {

            this._renderData({ responseData });
        });
    }

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

	/**
	 * 
	 */  
    _getData_OnServer( { projectSearchId, peptideQueryJSONRoot } ) {
    
		const objectThis = this;
		
        return new Promise(function(resolve, reject) {
		  try {
            let requestObj = {
                projectSearchIds : [ projectSearchId ],
                peptideQueryJSONRoot : peptideQueryJSONRoot
            };

			const url = "services/peptidePage-SingleSearch-MainDisplay";

			const webserviceCallStandardPostResult = webserviceCallStandardPost({ dataToSend : requestObj, url }) ;

			const promise_webserviceCallStandardPost = webserviceCallStandardPostResult.promise;

			promise_webserviceCallStandardPost.catch( () => { reject() }  );

			promise_webserviceCallStandardPost.then( ({ responseData }) => {
				try {
					resolve({ responseData });
				} catch (e) {
					reportWebErrorToServer.reportErrorObjectToServer({ errorException: e });
					throw e;
				}
			});
		  } catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		  }
        });
    };

	/**
	 * 
	 */ 
    _renderData({ responseData }) {

        // return;

        const viewSearchPeptidesPageDataRoot = responseData;

        const $peptide_count_display = $("#peptide_count_display");
        $peptide_count_display.text( viewSearchPeptidesPageDataRoot.peptideListSize );

        //  Create <table> DOM element
        const html_TableRoot = this._peptidePage_SingleSearch_MainTable_table_template();
        
        const $peptideTableRoot = $( html_TableRoot );

        let headerRowColumnCount = 0; //  Set after add header row

        {
            //  Add Header Row

            const $peptideTable_thead = $peptideTableRoot.children("thead");
            if ( $peptideTable_thead.length === 0 ) {
                throw Error("No DOM element <thead> under $peptideTableRoot");
            }

            const html_HeaderRow = this._peptidePage_SingleSearch_MainTable_header_row_template({ viewSearchPeptidesPageDataRoot });
            const $headerRow = $( html_HeaderRow );
            $headerRow.appendTo( $peptideTable_thead );

            const $headerRowEntries = $headerRow.find("th");
            headerRowColumnCount = $headerRowEntries.length;
            if ( headerRowColumnCount === 0 ) {
                throw Error("No '<th>' DOM elements found under header row $headerRow");
            }
        }

        if ( viewSearchPeptidesPageDataRoot.peptideList ) {

            //  Add each Data Row and Child Container Row to <table> DOM element

            const $peptideTable_tbody = $peptideTableRoot.children("tbody");
            if ( $peptideTable_tbody.length === 0 ) {
                throw Error("No DOM element <tbody> under $peptideTableRoot");
            }

            for ( const peptideEntry of viewSearchPeptidesPageDataRoot.peptideList ) {

                {
                    const html_PeptideEntry = this._peptidePage_SingleSearch_MainTable_MainDataRow_template({ peptideEntry, viewSearchPeptidesPageDataRoot });
                    const $dataRow = $( html_PeptideEntry );
                    $dataRow.appendTo( $peptideTable_tbody );
                }
                {
                    //  Set tableColumnCount to headerRowColumnCount, assuming all data rows will have same number of columns as the header row
                    const html_ChildTableContainer = this._peptidePage_SingleSearch_MainTable_child_table_container_row_template({ tableColumnCount : headerRowColumnCount });
                    const $childContainerTableRow = $( html_ChildTableContainer );
                    $childContainerTableRow.appendTo( $peptideTable_tbody );
                }
            }

        }

        
        addToolTips( $peptideTableRoot ); // external function
        createTooltipForProteinNames({ $findElementsRoot : $peptideTableRoot }); // external function

        
        setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else
            $("#peptide_table_js_populate").tablesorter(); // gets exception if there are no data rows
        },10);


        this._finalDisplayOfAboveTableSection();

        //  Container <div> on page

        const $peptides_from_webservice_container = $("#peptides_from_webservice_container");
        if ( $peptides_from_webservice_container.length === 0 ) {
            throw Error("No DOM element with id 'peptides_from_webservice_container'");
        }
        $peptides_from_webservice_container.empty(); //  is empty but do this anyway

        //  Add <table> DOM element and it's children to the container <div> on the page

        $peptideTableRoot.appendTo( $peptides_from_webservice_container );
        $peptides_from_webservice_container.show();

        destroySpinner();
    }
}

