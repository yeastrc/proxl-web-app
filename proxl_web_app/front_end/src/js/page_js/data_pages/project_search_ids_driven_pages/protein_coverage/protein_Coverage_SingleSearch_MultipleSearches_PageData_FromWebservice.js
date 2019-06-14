/**
 * protein_Coverage_SingleSearch_MultipleSearches_PageData_FromWebservice.js
 * 
 * Javascript for Protein Coverage Single Search AND Multiple Searches page to get the main table from webservice and display using Handlebars template
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

import { computeMergedSearchColorIndex_OneBased } from 'page_js/data_pages/project_search_ids_driven_pages/common/computeMergedSearchColorIndex.js';

import { createTooltipForProteinNames } from 'page_js/data_pages/project_search_ids_driven_pages/common/createTooltipForProteinNames.js';

/**
 * 
 */
export class Protein_Coverage_SingleSearch_MultipleSearches_PageData_FromWebservice {

	/**
	 * 
	 */
	constructor() {

		this._initializeCalled = false;

		this._blockContentsIntialized = false;  //  Are the contents of the block initialized (initialized on first open)
		
		if (!_protein_page_template.protein_Coverage_Entry) {
			throw Error("Nothing in _protein_page_template.protein_Coverage_Entry");
		}
        this._protein_Coverage_Entry_template = _protein_page_template.protein_Coverage_Entry;

		if ( ! _protein_pages_coverage_report_shared_template.exclude_protein_select_option ) {
			throw Error("Nothing in _protein_page_template.protein_AllProteins_MultipleSearches_PageData_MainTable_child_table_container_row");
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
	initialize({ projectSearchIds, proteinQueryJSONRoot, callbackAfterDataLoad_DOM_AboveMainTableUpdated, destroySpinner }) {
		
        const objectThis = this;
        
        const promise_getData_OnServer = this._getData_OnServer({ projectSearchIds, proteinQueryJSONRoot });

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
    _getData_OnServer( { projectSearchIds, proteinQueryJSONRoot } ) {
    
		const objectThis = this;
		
        return new Promise(function(resolve, reject) {
		  try {
            let requestObj = {
                projectSearchIds : projectSearchIds,
                proteinQueryJSONRoot : proteinQueryJSONRoot
            };

			const url = "services/proteinPage-Coverage-SingleSearch-MultipleSearches-MainDisplay";

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
			const $numProteins_1 = $("#numProteins_1");
			$numProteins_1.text( viewSearchProteinsPageDataRoot.numProteins );

			const $numProteins_2 = $("#numProteins_2");
			$numProteins_2.text( viewSearchProteinsPageDataRoot.numProteins );
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

        const $main_page_data_table = $("#main_page_data_table");
        if ( $main_page_data_table.length === 0 ) {
            throw Error("No DOM element with id 'main_page_data_table'");
        }
        const $main_page_data_table_tbody = $main_page_data_table.children("tbody");
        if ( $main_page_data_table_tbody.length === 0 ) {
            throw Error("No DOM element '<tbody>' under DOM element with id 'main_page_data_table'");
        }
        $main_page_data_table_tbody.empty();

        if ( responseData.proteinCoverageDataList ) {

            //  Add each Data Row and Child Container Row to <table> DOM element

            for ( const entry of responseData.proteinCoverageDataList ) {
                const html_Entry = this._protein_Coverage_Entry_template({ coverage : entry });
                const $dataRow = $( html_Entry );
                $dataRow.appendTo( $main_page_data_table_tbody );
            }
        }

        setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else
            $("#main_page_data_table").tablesorter(); // gets exception if there are no data rows
        },10);

    }
}
