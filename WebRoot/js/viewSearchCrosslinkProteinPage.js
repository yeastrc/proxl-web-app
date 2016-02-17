
//  viewSearchCrosslinkProteinPage.js

//  Javascript for the viewSearchCrosslinkCrosslinkProtein.jsp page

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";

//  Constructor

var ViewSearchCrosslinkProteinPageCode = function() {

	//  function called after all HTML above main table is generated

	this.createPartsAboveMainTable = function() {
		
		var params = {
				listOfObjectsToPassPsmPeptideCutoffsRootTo : [ 
				                                               viewCrosslinkReportedPeptidesLoadedFromWebServiceTemplate,
				                                               viewPsmsLoadedFromWebServiceTemplate,
				                                               viewPeptidesRelatedToPSMsByScanId
				                                               ]
		};

		viewSearchProteinPageCommonCrosslinkLooplinkCoverage.createPartsAboveMainTableSearchProteinPageCommon( params );
	};
	
		
	///////////////////////
	
	this.updatePageForFormParams = function() {
	
		
		viewSearchProteinPageCommonCrosslinkLooplinkCoverage.updatePageForFormParams();
	};
		
	
		
};

//  Instance of class

var viewSearchCrosslinkProteinPageCode = new ViewSearchCrosslinkProteinPageCode();


