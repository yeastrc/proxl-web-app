/**
 * mergedPages_ShowSearchesAndVennDiagram_AboveMainTable.js
 * 
 * Javascript for Merged Pages (Peptides and Proteins)
 * 
 * Displays Search Ids and Search counts and sometimes Venn Diagram above the main Data Table
 * 
 */

/**
 * JavaScript directive:   all variables have to be declared with "var", maybe other things
 */
"use strict";

//  Import Handlebars templates

const _merged_pages_shared_template = 
require("../../../../../../handlebars_templates_precompiled/merged_pages_shared/merged_pages_shared_template-bundle.js");

//  module import 

import { reportWebErrorToServer } from 'page_js/header_section_js_all_pages_main_pages/header_section_every_page/reportWebErrorToServer.js';

import { webserviceCallStandardPost } from 'page_js/webservice_call_common/webserviceCallStandardPost.js';

import { addToolTips } from 'page_js/header_section_js_all_pages_main_pages/header_section_every_page/genericToolTip.js';

import { computeMergedSearchColorIndex_OneBased } from 'page_js/data_pages/project_search_ids_driven_pages/common/computeMergedSearchColorIndex.js';

import { createMergedSearchesLinkCountsVennDiagram } from 'page_js/data_pages/project_search_ids_driven_pages/common_merged_pages/mergedSearchesVennDiagramCreator.js';

/**
 * 
 */
export class MergedPages_ShowSearchesAndVennDiagram_AboveMainTable {

	/**
	 * 
	 */
	constructor() {

		this._initializeCalled = false;

		this._blockContentsIntialized = false;  //  Are the contents of the block initialized (initialized on first open)
		
		if (!_merged_pages_shared_template.mergedPages_SearchesList_WithVennDiagram_AboveMainDataTable) {
			throw Error("Nothing in _merged_pages_shared_template.mergedPages_SearchesList_WithVennDiagram_AboveMainDataTable");
		}
        this._mergedPages_SearchesList_WithVennDiagram_AboveMainDataTable_template = _merged_pages_shared_template.mergedPages_SearchesList_WithVennDiagram_AboveMainDataTable;

		if (!_merged_pages_shared_template.mergedPages_SearchesList_NO_VennDiagram_AboveMainDataTable) {
			throw Error("Nothing in _merged_pages_shared_template.mergedPages_SearchesList_NO_VennDiagram_AboveMainDataTable");
		}
        this._mergedPages_SearchesList_NO_VennDiagram_AboveMainDataTable_template = _merged_pages_shared_template.mergedPages_SearchesList_NO_VennDiagram_AboveMainDataTable;

        
    }

	/**
	 * 
	 */    
    initialize({ searchCountList, vennDiagramDataToJSON }) {

        const $merged_page_searches_list_venn_diagram_holder = $("#merged_page_searches_list_venn_diagram_holder");
        if ( $merged_page_searches_list_venn_diagram_holder.length === 0 ) {
            throw Error("No DOM element with id 'merged_page_searches_list_venn_diagram_holder'");
        }
        $merged_page_searches_list_venn_diagram_holder.empty();

        if ( vennDiagramDataToJSON ) {
            //  Have Venn Diagram data so use the Template that includes the Venn Diagram
            this._create_WITH_VennDiagram({ searchCountList, vennDiagramDataToJSON, $merged_page_searches_list_venn_diagram_holder })

            return; //  EARLY RETURN
        }

        this._create_NO_VennDiagram({ searchCountList, $merged_page_searches_list_venn_diagram_holder });
    }

    /**
	 * 
	 */   
    _create_WITH_VennDiagram({ searchCountList, vennDiagramDataToJSON, $merged_page_searches_list_venn_diagram_holder }) {

        //  Add Search Counts to DOM

        this._addCSS_ColorIndexTo_searchCountList({ searchCountList });

        const html = this._mergedPages_SearchesList_WithVennDiagram_AboveMainDataTable_template({ searchCounts : searchCountList });

        $merged_page_searches_list_venn_diagram_holder.append( html );
        
        //  Add Venn Diagram to DOM
        try {
            createMergedSearchesLinkCountsVennDiagram.createMergedSearchesLinkCountsVennDiagram({ vennDiagramDataToJSON });

        } catch( e ) {
            reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
            throw e;
        }
    }

    /**
	 * 
	 */   
    _create_NO_VennDiagram({ searchCountList, $merged_page_searches_list_venn_diagram_holder }) {

        //  Add Search Counts to DOM

        this._addCSS_ColorIndexTo_searchCountList({ searchCountList });

        const html = this._mergedPages_SearchesList_NO_VennDiagram_AboveMainDataTable_template({ searchCounts : searchCountList });

        $merged_page_searches_list_venn_diagram_holder.append( html );
    }

    /**
	 * 
	 */  
    _addCSS_ColorIndexTo_searchCountList({ searchCountList }) {

        //     Add CSS Color Index to searchCountList
        {
            let index = 0;
            for ( const searchCount of searchCountList ) {

                const colorIndex_OneBased = computeMergedSearchColorIndex_OneBased({ searchIndex_ZeroBased : index });
                searchCount.colorIndex_OneBased = colorIndex_OneBased;
                index++;
            }
        }
    }

}

