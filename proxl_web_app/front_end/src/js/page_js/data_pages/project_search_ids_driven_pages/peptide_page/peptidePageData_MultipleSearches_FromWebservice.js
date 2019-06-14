/**
 * peptidePageData_MultipleSearches_FromWebservice.js
 * 
 * Javascript for Peptide Multiple Searches page to get the main table from webservice and display using Handlebars template
 * 
 */

/**
 * JavaScript directive:   all variables have to be declared with "var", maybe other things
 */
"use strict";

//  Import Handlebars templates

const _peptide_page_template = 
require("../../../../../../handlebars_templates_precompiled/peptide_page/peptide_page_template-bundle.js");

const _merged_pages_shared_template = 
require("../../../../../../handlebars_templates_precompiled/merged_pages_shared/merged_pages_shared_template-bundle.js");

//  module import 

import { reportWebErrorToServer } from 'page_js/header_section_js_all_pages_main_pages/header_section_every_page/reportWebErrorToServer.js';

import { webserviceCallStandardPost } from 'page_js/webservice_call_common/webserviceCallStandardPost.js';

import { addToolTips } from 'page_js/header_section_js_all_pages_main_pages/header_section_every_page/genericToolTip.js';

import { computeMergedSearchColorIndex_OneBased } from 'page_js/data_pages/project_search_ids_driven_pages/common/computeMergedSearchColorIndex.js';

import { createTooltipForProteinNames } from 'page_js/data_pages/project_search_ids_driven_pages/common/createTooltipForProteinNames.js';

import { createSpinner, destroySpinner, incrementSpinner, decrementSpinner }  from 'page_js/header_section_js_all_pages_main_pages/header_section_every_page/spinner.js';


import { MergedPages_ShowSearchesAndVennDiagram_AboveMainTable } from 'page_js/data_pages/project_search_ids_driven_pages/common_merged_pages/mergedPages_ShowSearchesAndVennDiagram_AboveMainTable.js';

/**
 * 
 */
export class PeptidePageData_MultipleSearches_FromWebservice {

	/**
	 * 
	 */
	constructor() {

		this._initializeCalled = false;

		this._blockContentsIntialized = false;  //  Are the contents of the block initialized (initialized on first open)
		
		if (!_peptide_page_template.peptidePage_MultipleSearches_MainTable_table) {
			throw Error("Nothing in _peptide_page_template.peptidePage_MultipleSearches_MainTable_table");
		}
        this._peptidePage_MultipleSearches_MainTable_table_template = _peptide_page_template.peptidePage_MultipleSearches_MainTable_table;

		if (!_peptide_page_template.peptidePage_MultipleSearches_MainTable_header_row) {
			throw Error("Nothing in _peptide_page_template.peptidePage_MultipleSearches_MainTable_header_row");
		}
        this._peptidePage_MultipleSearches_MainTable_header_row_template = _peptide_page_template.peptidePage_MultipleSearches_MainTable_header_row;

		if (!_peptide_page_template.peptidePage_MultipleSearches_MainTable_MainDataRow) {
			throw Error("Nothing in _peptide_page_template.peptidePage_MultipleSearches_MainTable_MainDataRow");
		}
        this._peptidePage_MultipleSearches_MainTable_MainDataRow_template = _peptide_page_template.peptidePage_MultipleSearches_MainTable_MainDataRow;

		if (!_peptide_page_template.peptidePage_MultipleSearches_MainTable_child_table_container_row) {
			throw Error("Nothing in _peptide_page_template.peptidePage_MultipleSearches_MainTable_child_table_container_row");
		}
        this._peptidePage_MultipleSearches_MainTable_child_table_container_row_template = _peptide_page_template.peptidePage_MultipleSearches_MainTable_child_table_container_row;

		if (!_merged_pages_shared_template.mergedPages_MultipleSearches_MainTable_header_search_id_tooltip) {
			throw Error("Nothing in _merged_pages_shared_template.mergedPages_MultipleSearches_MainTable_header_search_id_tooltip");
		}
        this._mergedPages_MultipleSearches_MainTable_header_search_id_tooltip_template = _merged_pages_shared_template.mergedPages_MultipleSearches_MainTable_header_search_id_tooltip;
        

        this._mergedPages_ShowSearchesAndVennDiagram_AboveMainTable = new MergedPages_ShowSearchesAndVennDiagram_AboveMainTable();
    }

	/**
	 * 
	 */
	initialize({ projectSearchIds, mergedPeptideQueryJSONRoot }) {
		
        const objectThis = this;

        this._mergedPeptideQueryJSONRoot = mergedPeptideQueryJSONRoot;
        
        this._initialDisplayOfAboveTableSection();

        createSpinner();				// create spinner

        const promise_getData_OnServer = this._getData_OnServer({ projectSearchIds, mergedPeptideQueryJSONRoot });

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
		
		const $cutoff_per_search_block_tr_jq_All = $search_details_and_main_filter_criteria_main_page_root.find(".cutoff_per_search_block_tr_jq");
		$cutoff_per_search_block_tr_jq_All.hide();

        const $selector_search_details_searches_list_root_row = $tbody.children(".selector_search_details_searches_list_root_row");
        $selector_search_details_searches_list_root_row.show();
        $search_details_and_main_filter_criteria_main_page_root.show();
    }

	/**
	 * called after main table populated and before displayed
	 */
	_finalDisplayOfAboveTableSection({ viewSearchPeptidesPageDataRoot }) {

        this._mergedPages_ShowSearchesAndVennDiagram_AboveMainTable.initialize({ 
            searchCountList : viewSearchPeptidesPageDataRoot.searchCountList, 
            vennDiagramDataToJSON : viewSearchPeptidesPageDataRoot.vennDiagramDataToJSON });
        
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
    }

	/**
	 * 
	 */  
    _getData_OnServer( { projectSearchIds, mergedPeptideQueryJSONRoot } ) {
    
		const objectThis = this;
		
        return new Promise(function(resolve, reject) {
		  try {
            let requestObj = {
                projectSearchIds : projectSearchIds,
                mergedPeptideQueryJSONRoot : mergedPeptideQueryJSONRoot
            };

			const url = "services/peptidePage-MultipleSearches-MainDisplay";

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

        if ( viewSearchPeptidesPageDataRoot.anyReportedPeptideEntriesWereCombined ) {
            const $anyReportedPeptideEntriesWereCombined = $("#anyReportedPeptideEntriesWereCombined");
            if ( $anyReportedPeptideEntriesWereCombined.length === 0 ) {
                throw Error("NO DOM element with id 'anyReportedPeptideEntriesWereCombined'");
            }
            $anyReportedPeptideEntriesWereCombined.show();
        }

        //  Create <table> DOM element
        const html_TableRoot = this._peptidePage_MultipleSearches_MainTable_table_template();
        
        const $peptideTableRoot = $( html_TableRoot );

        let headerRowColumnCount = 0; //  Set after add header row

        {
            //  Add Header Row

            //     First add CSS Color Index to per search Annotation Type objects
            {
                let index = 0;
                for ( const peptidePsmAnnotationNameDescListsForASearch of viewSearchPeptidesPageDataRoot.peptidePsmAnnotationNameDescListsForEachSearch ) {

                    const colorIndex_OneBased = computeMergedSearchColorIndex_OneBased({ searchIndex_ZeroBased : index });
                    peptidePsmAnnotationNameDescListsForASearch.colorIndex_OneBased = colorIndex_OneBased;
                    index++;
                }
            }

            const $peptideTable_thead = $peptideTableRoot.children("thead");
            if ( $peptideTable_thead.length === 0 ) {
                throw Error("No DOM element <thead> under $peptideTableRoot");
            }

            const html_HeaderRow = this._peptidePage_MultipleSearches_MainTable_header_row_template({ viewSearchPeptidesPageDataRoot });
            const $headerRow = $( html_HeaderRow );
            $headerRow.appendTo( $peptideTable_thead );

            const $headerRowEntries = $headerRow.find("th");
            headerRowColumnCount = $headerRowEntries.length;
            if ( headerRowColumnCount === 0 ) {
                throw Error("No '<th>' DOM elements found under header row $headerRow");
            }

            //  Add tooltips on each search id in header row
            for ( const searchesEntry of viewSearchPeptidesPageDataRoot.searchesList ) {

                const html = this._mergedPages_MultipleSearches_MainTable_header_search_id_tooltip_template( searchesEntry );
                  
                const selector = "#search_header_" + searchesEntry.projectSearchId;
                const $selector = $headerRow.find( selector );
                $selector.qtip( {
                    content: {
                        text: html
                    },
                    position: {
                        my: 'bottom left',
                        at: 'top center',
                        viewport: $(window)
                    }
                });								
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
                    //     First add CSS Color Index to per search Search Ids
                    {
                        const searchContainsPeptide_SubList_JS_Generated = [];
                        let index = 0;
                        for ( const searchContainsPeptide_SubListEntry of peptideEntry.searchContainsPeptide_SubList ) {
                            //  searchContainsPeptide_SubListEntry is boolean
                            const searchContainsPeptide_SubList_JS_Generated_Entry = { containsSearch : searchContainsPeptide_SubListEntry }
                            if ( searchContainsPeptide_SubListEntry ) {
                                const colorIndex_OneBased = computeMergedSearchColorIndex_OneBased({ searchIndex_ZeroBased : index });
                                searchContainsPeptide_SubList_JS_Generated_Entry.colorIndex_OneBased = colorIndex_OneBased;
                            }
                            searchContainsPeptide_SubList_JS_Generated.push( searchContainsPeptide_SubList_JS_Generated_Entry );
                            index++;
                        }
                        peptideEntry.searchContainsPeptide_SubList_JS_Generated = searchContainsPeptide_SubList_JS_Generated;
                    }
                    
                    //     First add CSS Color Index to per search Annotation Value objects
                    {
                        let index = 0;
                        for ( const peptidePsmAnnotationValueListsForEachSearch of peptideEntry.peptidePsmAnnotationValueListsForEachSearch ) {

                            const colorIndex_OneBased = computeMergedSearchColorIndex_OneBased({ searchIndex_ZeroBased : index });
                            peptidePsmAnnotationValueListsForEachSearch.colorIndex_OneBased = colorIndex_OneBased;
                            index++;
                        }
                    }

                    const html_PeptideEntry = this._peptidePage_MultipleSearches_MainTable_MainDataRow_template({ peptideEntry, viewSearchPeptidesPageDataRoot });
                    const $dataRow = $( html_PeptideEntry );
                    $dataRow.appendTo( $peptideTable_tbody );
                }
                {
                    //  Set tableColumnCount to headerRowColumnCount, assuming all data rows will have same number of columns as the header row
                    const html_ChildTableContainer = this._peptidePage_MultipleSearches_MainTable_child_table_container_row_template({ tableColumnCount : headerRowColumnCount });
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


        this._finalDisplayOfAboveTableSection({ viewSearchPeptidesPageDataRoot });

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

