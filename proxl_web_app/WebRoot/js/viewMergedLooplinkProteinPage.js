
//  viewMergedLooplinkProteinPage.js

//  Javascript for the viewMergedLooplinkProtein.jsp page

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";


//  Constructor

var ViewMergedLooplinkProteinPageCode = function() {


	//  function called after all HTML above main table is generated

	this.createPartsAboveMainTable = function() {

		setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else

			var params = {
					listOfObjectsToPassPsmPeptideCutoffsRootTo : [ 
					                                              viewLooplinkReportedPeptidesLoadedFromWebServiceTemplate,
					                                              viewLooplinkProteinsLoadedFromWebServiceTemplate,
					                                              viewPsmsLoadedFromWebServiceTemplate,
					                                              viewPeptidesRelatedToPSMsByScanId
					                                              ]
			};

			viewSearchProteinPageCommonCrosslinkLooplinkCoverage.createPartsAboveMainTableSearchProteinPageCommon( params );

		},10);
		

		setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else

			//  If this function exists, call it to create the Venn diagram on the page

			if ( window.createMergedSearchesLinkCountsVennDiagram_PageFunction ) {

				window.createMergedSearchesLinkCountsVennDiagram_PageFunction();
			}
		},10);
	};
	

	///////////////////////
	
	this.updatePageForFormParams = function() {
	
		
		viewSearchProteinPageCommonCrosslinkLooplinkCoverage.updatePageForFormParams();
	};
		
	
	
		
	
};

//  Instance of class

var viewMergedLooplinkProteinPageCode = new ViewMergedLooplinkProteinPageCode();

