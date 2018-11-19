
//  viewMergedCrosslinkProteinPage.js

//  Javascript for the viewMergedCrosslinkProtein.jsp page

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";

//  Constructor

var ViewMergedCrosslinkProteinPageCode = function() {

	//  function called after all HTML above main table is generated

	this.createPartsAboveMainTable = function() {

		setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else

			try {

				var params = {
						listOfObjectsToPassPsmPeptideCutoffsRootTo : [ 
						                                              viewCrosslinkProteinsLoadedFromWebServiceTemplate,
						                                              viewCrosslinkReportedPeptidesLoadedFromWebServiceTemplate,
						                                              viewPsmsLoadedFromWebServiceTemplate,
						                                              viewPeptidesRelatedToPSMsByScanId
						                                              ]
				};

				viewSearchProteinPageCommonCrosslinkLooplinkCoverage.createPartsAboveMainTableSearchProteinPageCommon( params );

				setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else
					if ( window.mergedPeptideProteinSearchesListVennDiagramSection ) {
						window.mergedPeptideProteinSearchesListVennDiagramSection.init();
					}
				},10);
				
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		},10);
		

		setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else

			//  If this function exists, call it to create the Venn diagram on the page

			if ( window.createMergedSearchesLinkCountsVennDiagram_PageFunction ) {

				try {

					window.createMergedSearchesLinkCountsVennDiagram_PageFunction();

				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
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

var viewMergedCrosslinkProteinPageCode = new ViewMergedCrosslinkProteinPageCode();



		
