
//  viewMergedLooplinkProteinPage.js

//  Javascript for the viewMergedLooplinkProtein.jsp page

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";


//Import header_main.js and children to ensure on the page
import { header_mainVariable } from 'page_js/common_all_pages/header_section_main_pages/header_main.js';


import { createMergedSearchesLinkCountsVennDiagram } from 'page_js/data_pages/project_search_ids_driven_pages/merged_pages_common/mergedSearchesVennDiagramCreator.js';


import { onDocumentReady, viewSearchProteinPageCommonCrosslinkLooplinkCoverage } from 'page_js/data_pages/project_search_ids_driven_pages/protein__protein_coverage__shared/viewProteinPageCommonCrosslinkLooplinkCoverageSearchMerged.js';


//  For showing Data for links (Drilldown) (Called by HTML onclick):
import { viewLooplinkProteinsLoadedFromWebServiceTemplate } from 'page_js/data_pages/project_search_ids_driven_pages/protein_pages/viewLooplinkProteinsLoadedFromWebServiceTemplate.js';
import { viewLooplinkReportedPeptidesLoadedFromWebServiceTemplate } from 'page_js/data_pages/project_search_ids_driven_pages/protein_pages/viewLooplinkReportedPeptidesLoadedFromWebServiceTemplate.js';


$(document).ready(function() { 
	onDocumentReady();
}); // end $(document).ready(function()


//  Constructor

var ViewMergedLooplinkProteinPageCode = function() {


	//  function called after all HTML above main table is generated

	this.createPartsAboveMainTable = function() {

		setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else

			try {

				var params = {
						listOfObjectsToPassPsmPeptideCutoffsRootTo : [ 
						                                              viewLooplinkReportedPeptidesLoadedFromWebServiceTemplate,
						                                              viewLooplinkProteinsLoadedFromWebServiceTemplate,
						                                              viewPsmsLoadedFromWebServiceTemplate,
						                                              viewPeptidesRelatedToPSMsByScanId
						                                              ]
				};

				viewSearchProteinPageCommonCrosslinkLooplinkCoverage.createPartsAboveMainTableSearchProteinPageCommon( params );

			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
			
		},10);
		

		setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else
			
			//  call to create the Venn diagram on the page, if there is venn data on the page
			try {
				createMergedSearchesLinkCountsVennDiagram.createMergedSearchesLinkCountsVennDiagram( );

			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		},10);
	};
	

	///////////////////////
	
	this.updatePageForFormParams = function() {

		try {		

			viewSearchProteinPageCommonCrosslinkLooplinkCoverage.updatePageForFormParams();
			
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
		
	
	
		
	
};

//  Instance of class

window.viewMergedLooplinkProteinPageCode = new ViewMergedLooplinkProteinPageCode();
