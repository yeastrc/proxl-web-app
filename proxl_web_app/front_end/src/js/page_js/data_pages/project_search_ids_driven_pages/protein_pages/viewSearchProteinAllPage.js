
//  viewSearchProteinAllPage.js

//  Javascript for the viewSearchProteinAll.jsp page

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";

//  Import to make available on the page
import { defaultPageView } from 'page_js/data_pages/project_search_ids_driven_pages/common/defaultPageView.js';


import { onDocumentReady, viewSearchProteinPageCommonCrosslinkLooplinkCoverage } from 'page_js/data_pages/project_search_ids_driven_pages/protein__protein_coverage__shared/viewProteinPageCommonCrosslinkLooplinkCoverageSearchMerged.js';

//  For showing Data for links (Drilldown) (Called by HTML onclick):
import { viewReportedPeptidesForProteinAllLoadedFromWebServiceTemplate } from 'page_js/data_pages/project_search_ids_driven_pages/protein_pages/viewReportedPeptidesForProteinAllLoadedFromWebServiceTemplate.js';


$(document).ready(function() { 
	onDocumentReady();
}); // end $(document).ready(function()


//  Constructor

var ViewSearchProteinAllPageCode = function() {

	//  function called after all HTML above main table is generated, called from inline script on page
	this.createPartsAboveMainTable = function() {
		try {
			viewSearchProteinPageCommonCrosslinkLooplinkCoverage.createPartsAboveMainTableSearchProteinPageCommon( {} );
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

window.viewSearchProteinAllPageCode = new ViewSearchProteinAllPageCode();


