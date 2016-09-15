
//  viewProteinCoverageReport.js

//  Javascript for the viewProteinCoverageReport.jsp page

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";


//  Constructor

var ViewProteinCoverageReportPageCode = function() {

	//  function called after all HTML above main table is generated

	this.createPartsAboveMainTable = function() {

		var params = {
				listOfObjectsToPassPsmPeptideCutoffsRootTo : [ ] };

		viewSearchProteinPageCommonCrosslinkLooplinkCoverage.createPartsAboveMainTableSearchProteinPageCommon( params );
	};
	
		
	///////////////////////
	
	this.updatePageForFormParams = function() {
	
		
		viewSearchProteinPageCommonCrosslinkLooplinkCoverage.updatePageForFormParams();
	};
		
		
};

//  Instance of class

var viewProteinCoverageReportPageCode = new ViewProteinCoverageReportPageCode();


