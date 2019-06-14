/**
 * protein_looplink_SingleSearch_PageData_FromWebservice.js
 * 
 * Javascript for Looplink Protein Single Search page to get the main table from webservice and display using Handlebars template
 * 
 */

/**
 * JavaScript directive:   all variables have to be declared with "var", maybe other things
 */
"use strict";

//  Import Handlebars templates

const _protein_page_template = 
require("../../../../../../handlebars_templates_precompiled/protein_page/protein_page_template-bundle.js");

const _protein_pages_coverage_report_shared_template = 
require("../../../../../../handlebars_templates_precompiled/protein_pages_coverage_report_shared/protein_pages_coverage_report_shared_template-bundle.js");

const _protein_pages_coverage_report_image_structure_pages_shared_template = 
require("../../../../../../handlebars_templates_precompiled/protein_pages_coverage_report_image_structure_pages_shared/protein_pages_coverage_report_image_structure_pages_shared_template-bundle.js");

//  module import 

import { reportWebErrorToServer } from 'page_js/header_section_js_all_pages_main_pages/header_section_every_page/reportWebErrorToServer.js';

import { webserviceCallStandardPost } from 'page_js/webservice_call_common/webserviceCallStandardPost.js';

import { addToolTips } from 'page_js/header_section_js_all_pages_main_pages/header_section_every_page/genericToolTip.js';

import { createTooltipForProteinNames } from 'page_js/data_pages/project_search_ids_driven_pages/common/createTooltipForProteinNames.js';

/**
 * 
 */
export class Protein_looplink_PageData_SingleSearch_FromWebservice {

	/**
	 * 
	 */
	constructor() {

		this._initializeCalled = false;

		this._blockContentsIntialized = false;  //  Are the contents of the block initialized (initialized on first open)
		
		if (!_protein_page_template.protein_Looplink_SingleSearch_PageData_MainTable_table) {
			throw Error("Nothing in _protein_page_template.protein_Looplink_SingleSearch_PageData_MainTable_table");
		}
        this._protein_Looplink_SingleSearch_PageData_MainTable_table_template = _protein_page_template.protein_Looplink_SingleSearch_PageData_MainTable_table;

		if (!_protein_page_template.protein_Looplink_SingleSearch_PageData_MainTable_header_row) {
			throw Error("Nothing in _protein_page_template.protein_Looplink_SingleSearch_PageData_MainTable_header_row");
		}
        this._protein_Looplink_SingleSearch_PageData_MainTable_header_row_template = _protein_page_template.protein_Looplink_SingleSearch_PageData_MainTable_header_row;

		if (!_protein_page_template.protein_Looplink_SingleSearch_PageData_MainTable_MainDataRow) {
			throw Error("Nothing in _protein_page_template.protein_Looplink_SingleSearch_PageData_MainTable_MainDataRow");
		}
        this._protein_Looplink_SingleSearch_PageData_MainTable_MainDataRow_template = _protein_page_template.protein_Looplink_SingleSearch_PageData_MainTable_MainDataRow;

		if (!_protein_page_template.protein_Looplink_SingleSearch_PageData_MainTable_child_table_container_row) {
			throw Error("Nothing in _protein_page_template.protein_Looplink_SingleSearch_PageData_MainTable_child_table_container_row");
		}
		this._protein_Looplink_SingleSearch_PageData_MainTable_child_table_container_row_template = _protein_page_template.protein_Looplink_SingleSearch_PageData_MainTable_child_table_container_row;
		
		if ( ! _protein_pages_coverage_report_shared_template.exclude_protein_select_option ) {
			throw Error("Nothing in _protein_page_template.protein_Looplink_SingleSearch_PageData_MainTable_child_table_container_row");
		}
		this._exclude_protein_select_option_Template = _protein_pages_coverage_report_shared_template.exclude_protein_select_option;

		if ( ! _protein_pages_coverage_report_image_structure_pages_shared_template.exclude_taxonomy_checkbox_entry ) {
			throw Error("Nothing in _protein_pages_coverage_report_image_structure_pages_shared_template.exclude_taxonomy_checkbox_entry");
		}
		this._exclude_taxonomy_checkbox_entry_Template = _protein_pages_coverage_report_image_structure_pages_shared_template.exclude_taxonomy_checkbox_entry;
    }

	/**
	 * 
	 */
	initialize({ projectSearchId, proteinQueryJSONRoot, callbackAfterDataLoad_DOM_AboveMainTableUpdated, destroySpinner }) {
		
        const objectThis = this;
        
        const promise_getData_OnServer = this._getData_OnServer({ projectSearchId, proteinQueryJSONRoot });

        promise_getData_OnServer.catch( ( reason ) => {} );
     
        promise_getData_OnServer.then( ({ responseData }) => {

			this._updateDOMAboveMainTable({ responseData });

			callbackAfterDataLoad_DOM_AboveMainTableUpdated();

			this._renderMainTable({ responseData });
			
			destroySpinner();
		});
		
		return promise_getData_OnServer;
    }

	/**
	 * 
	 */  
    _getData_OnServer( { projectSearchId, proteinQueryJSONRoot } ) {
    
		const objectThis = this;
		
        return new Promise(function(resolve, reject) {
		  try {
            let requestObj = {
                projectSearchIds : [ projectSearchId ],
                proteinQueryJSONRoot : proteinQueryJSONRoot
            };

			const url = "services/proteinPage-Looplink-SingleSearch-MainDisplay";

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
    _updateDOMAboveMainTable({ responseData }) {

        const viewSearchProteinsPageDataRoot = responseData;

		{
			const $numLooplinks = $("#numLooplinks");
			$numLooplinks.text( viewSearchProteinsPageDataRoot.numLooplinks );

			const $numCrosslinks = $("#numCrosslinks");
			$numCrosslinks.text( viewSearchProteinsPageDataRoot.numCrosslinks );

			const $numLinks = $("#numLinks");
			$numLinks.text( viewSearchProteinsPageDataRoot.numLinks );

			const $numDistinctLinks = $("#numDistinctLinks");
			$numDistinctLinks.text( viewSearchProteinsPageDataRoot.numDistinctLinks );
		}
		{	//  Add Exclude Protein Entries
			//  <select>
			const $excludeProtein = $("#excludeProtein");
			$excludeProtein.empty();  // should be empty
			if ( viewSearchProteinsPageDataRoot.proteinExcludeEntries ) {
				for ( const proteinExcludeEntry of viewSearchProteinsPageDataRoot.proteinExcludeEntries ) {
					const html = this._exclude_protein_select_option_Template( proteinExcludeEntry );
					$excludeProtein.append( html );
				}
			}
		}
		{	//  Add Exclude Organism/Taxonomy Entries
			const $excludeTaxonomies = $("#excludeTaxonomies");
			$excludeTaxonomies.empty();  // should be empty
			if ( viewSearchProteinsPageDataRoot.taxonomies ) {
				//  taxonomies is an object with the keys being the taxonomyId and the value the taxonomyName
				const taxonomies = viewSearchProteinsPageDataRoot.taxonomies;
				const taxonomiesArray = [];
				for ( const taxonomyId of Object.keys( taxonomies ) ) {
					const taxonomyName = taxonomies[ taxonomyId ];
					const taxonomyEntry = { taxonomyId, taxonomyName };
					taxonomiesArray.push( taxonomyEntry );
				}
				//  Sort on taxonomyName then taxonomyId
				taxonomiesArray.sort( (a, b) => {
					if ( a.taxonomyName < b.taxonomyName ) {
						return -1;
					} else if ( a.taxonomyName > b.taxonomyName ) {
						return 1;
					}
					if ( a.taxonomyId < b.taxonomyId ) {
						return -1;
					} else if ( a.taxonomyId > b.taxonomyId ) {
						return 1;
					}
					return 0;
				})
				for ( const taxonomyEntry of taxonomiesArray ) {
					const html = this._exclude_taxonomy_checkbox_entry_Template( taxonomyEntry );
					$excludeTaxonomies.append( html );
				}
			}
		}
	}

	/**
	 * 
	 */ 
    _renderMainTable({ responseData }) {

        const viewSearchProteinsPageDataRoot = responseData;

        //  Create <table> DOM element
        const html_TableRoot = this._protein_Looplink_SingleSearch_PageData_MainTable_table_template();
        
        const $proteinTableRoot = $( html_TableRoot );

        let headerRowColumnCount = 0; //  Set after add header row

        {
            //  Add Header Row

            const $proteinTable_thead = $proteinTableRoot.children("thead");
            if ( $proteinTable_thead.length === 0 ) {
                throw Error("No DOM element <thead> under $proteinTableRoot");
            }

            const html_HeaderRow = this._protein_Looplink_SingleSearch_PageData_MainTable_header_row_template({ viewSearchProteinsPageDataRoot });
            const $headerRow = $( html_HeaderRow );
            $headerRow.appendTo( $proteinTable_thead );

            const $headerRowEntries = $headerRow.find("th");
            headerRowColumnCount = $headerRowEntries.length;
            if ( headerRowColumnCount === 0 ) {
                throw Error("No '<th>' DOM elements found under header row $headerRow");
            }
        }

        if ( viewSearchProteinsPageDataRoot.looplinks ) {

            //  Add each Data Row and Child Container Row to <table> DOM element

            const $proteinTable_tbody = $proteinTableRoot.children("tbody");
            if ( $proteinTable_tbody.length === 0 ) {
                throw Error("No DOM element <tbody> under $proteinTableRoot");
            }

            for ( const proteinEntry of viewSearchProteinsPageDataRoot.looplinks ) {
                {
                    const html_PeptideEntry = this._protein_Looplink_SingleSearch_PageData_MainTable_MainDataRow_template({ proteinEntry, viewSearchProteinsPageDataRoot });
                    const $dataRow = $( html_PeptideEntry );
                    $dataRow.appendTo( $proteinTable_tbody );
                }
                {
                    //  Set tableColumnCount to headerRowColumnCount, assuming all data rows will have same number of columns as the header row
                    const html_ChildTableContainer = this._protein_Looplink_SingleSearch_PageData_MainTable_child_table_container_row_template({ tableColumnCount : headerRowColumnCount });
                    const $childContainerTableRow = $( html_ChildTableContainer );
                    $childContainerTableRow.appendTo( $proteinTable_tbody );
                }
            }

        }

        
        addToolTips( $proteinTableRoot ); // external function
        createTooltipForProteinNames({ $findElementsRoot : $proteinTableRoot }); // external function

        
        setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else
            $("#protein_table_js_populate").tablesorter(); // gets exception if there are no data rows
        },10);


        //  Container <div> on page

        const $proteins_from_webservice_container = $("#proteins_from_webservice_container");
        if ( $proteins_from_webservice_container.length === 0 ) {
            throw Error("No DOM element with id 'proteins_from_webservice_container'");
        }
        $proteins_from_webservice_container.empty(); //  is empty but do this anyway

        //  Add <table> DOM element and it's children to the container <div> on the page

        $proteinTableRoot.appendTo( $proteins_from_webservice_container );
        $proteins_from_webservice_container.show();
    }
}
