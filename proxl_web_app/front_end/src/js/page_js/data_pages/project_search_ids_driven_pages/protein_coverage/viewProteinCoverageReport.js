
//  viewProteinCoverageReport.js

//  Javascript for the viewProteinCoverageReport.jsp page

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";

//Import header_main.js and children to ensure on the page
import { header_mainVariable } from 'page_js/header_section_js_all_pages_main_pages/header_section_main_pages/header_main.js';

//  Import to make available on the page
import { defaultPageView } from 'page_js/data_pages/project_search_ids_driven_pages/common/defaultPageView.js';


import { onDocumentReady, viewSearchProteinPageCommonCrosslinkLooplinkCoverage } from 'page_js/data_pages/project_search_ids_driven_pages/protein__protein_coverage__shared/viewProteinPageCommonCrosslinkLooplinkCoverageSearchMerged.js';



$(document).ready(function() { 
	onDocumentReady();
}); // end $(document).ready(function()



//  Constructor

var ViewProteinCoverageReportPageCode = function() {

	//  function called after all HTML above main table is generated, called from inline script on page

	this.createPartsAboveMainTable = function() {

		try {

			var params = {
					listOfObjectsToPassPsmPeptideCutoffsRootTo : [ ] };

			viewSearchProteinPageCommonCrosslinkLooplinkCoverage.createPartsAboveMainTableSearchProteinPageCommon( params );

		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}		
	};
	
		
	///////////////////////
	
	//   Called by "onclick" on HTML element

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

window.viewProteinCoverageReportPageCode = new ViewProteinCoverageReportPageCode();


