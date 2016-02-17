
//  viewSearchLooplinkProteinPage.js

//  Javascript for the viewSearchLooplinkLooplinkProtein.jsp page

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";


//  Constructor

var ViewSearchLooplinkProteinPageCode = function() {

	//  function called after all HTML above main table is generated

	this.createPartsAboveMainTable = function() {

		var params = {
				listOfObjectsToPassPsmPeptideCutoffsRootTo : [ 
				                                               viewLooplinkReportedPeptidesLoadedFromWebServiceTemplate,
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

var viewSearchLooplinkProteinPageCode = new ViewSearchLooplinkProteinPageCode();


