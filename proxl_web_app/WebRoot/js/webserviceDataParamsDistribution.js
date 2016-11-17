
//  webserviceDataParamsDistribution.js


//  Javascript:  Common code for passing parameters needed for webservice calls to get data

//  Currently for passing the cutoffs for PSM and Peptide, and Annotation Types to display



//  JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

//  Instance variable:  webserviceDataParamsDistributionCommonCode

//  Constructor
var WebserviceDataParamsDistributionCommonCode = function() {

	this.paramsForDistribution = function( params ) {

		var psmPeptideCutoffsRootObject = params.cutoffs;
		var annTypeIdDisplay = params.annTypeIdDisplay;

		if ( window.annotationDataDisplayProcessingCommonCode ) {
			if ( annTypeIdDisplay !== undefined ) {
				window.annotationDataDisplayProcessingCommonCode.putAnnTypeIdDisplayOnThePage( { annTypeIdDisplay : annTypeIdDisplay } );
			}
		}

		if ( window.viewPsmsLoadedFromWebServiceTemplate ) {
			if ( psmPeptideCutoffsRootObject !== undefined ) {
				window.viewPsmsLoadedFromWebServiceTemplate.setPsmPeptideCriteria( psmPeptideCutoffsRootObject );
			}
			if ( annTypeIdDisplay !== undefined ) {
				window.viewPsmsLoadedFromWebServiceTemplate.setPsmPeptideAnnTypeIdDisplay( annTypeIdDisplay );
			}
		}

		if ( window.viewPeptidesRelatedToPSMsByScanId ) {
			if ( psmPeptideCutoffsRootObject !== undefined ) {
				window.viewPeptidesRelatedToPSMsByScanId.setPsmPeptideCriteria( psmPeptideCutoffsRootObject );
			}
			if ( annTypeIdDisplay !== undefined ) {
				window.viewPeptidesRelatedToPSMsByScanId.setPsmPeptideAnnTypeIdDisplay( annTypeIdDisplay );
			}
		}

		//  Peptides for Protein JS
		if ( window.viewCrosslinkReportedPeptidesLoadedFromWebServiceTemplate ) {
			if ( psmPeptideCutoffsRootObject !== undefined ) {
				window.viewCrosslinkReportedPeptidesLoadedFromWebServiceTemplate.setPsmPeptideCriteria( psmPeptideCutoffsRootObject );
			}
			if ( annTypeIdDisplay !== undefined ) {
				window.viewCrosslinkReportedPeptidesLoadedFromWebServiceTemplate.setPsmPeptideAnnTypeIdDisplay( annTypeIdDisplay );
			}
		}
		if ( window.viewLooplinkReportedPeptidesLoadedFromWebServiceTemplate ) {
			if ( psmPeptideCutoffsRootObject !== undefined ) {
				window.viewLooplinkReportedPeptidesLoadedFromWebServiceTemplate.setPsmPeptideCriteria( psmPeptideCutoffsRootObject );
			}
			if ( annTypeIdDisplay !== undefined ) {
				window.viewLooplinkReportedPeptidesLoadedFromWebServiceTemplate.setPsmPeptideAnnTypeIdDisplay( annTypeIdDisplay );
			}
		}
		if ( window.viewMonolinkReportedPeptidesLoadedFromWebServiceTemplate ) {
			if ( psmPeptideCutoffsRootObject !== undefined ) {
				window.viewMonolinkReportedPeptidesLoadedFromWebServiceTemplate.setPsmPeptideCriteria( psmPeptideCutoffsRootObject );
			}
			if ( annTypeIdDisplay !== undefined ) {
				window.viewMonolinkReportedPeptidesLoadedFromWebServiceTemplate.setPsmPeptideAnnTypeIdDisplay( annTypeIdDisplay );
			}
		}

		//  Merged JS
		if ( window.viewMergedPeptidePerSearchDataFromWebServiceTemplate ) {
			if ( psmPeptideCutoffsRootObject !== undefined ) {
				window.viewMergedPeptidePerSearchDataFromWebServiceTemplate.setPsmPeptideCriteria( psmPeptideCutoffsRootObject );
			}
			if ( annTypeIdDisplay !== undefined ) {
				window.viewMergedPeptidePerSearchDataFromWebServiceTemplate.setPsmPeptideAnnTypeIdDisplay( annTypeIdDisplay );
			}
		}
		if ( window.viewCrosslinkProteinsLoadedFromWebServiceTemplate ) {
			if ( psmPeptideCutoffsRootObject !== undefined ) {
				window.viewCrosslinkProteinsLoadedFromWebServiceTemplate.setPsmPeptideCriteria( psmPeptideCutoffsRootObject );
			}
			if ( annTypeIdDisplay !== undefined && window.viewCrosslinkProteinsLoadedFromWebServiceTemplate.setPsmPeptideAnnTypeIdDisplay ) {
				//  Not currently exist:  window.viewCrosslinkProteinsLoadedFromWebServiceTemplate.setPsmPeptideAnnTypeIdDisplay
				window.viewCrosslinkProteinsLoadedFromWebServiceTemplate.setPsmPeptideAnnTypeIdDisplay( annTypeIdDisplay );
			}
		}
		if ( window.viewLooplinkProteinsLoadedFromWebServiceTemplate ) {
			if ( psmPeptideCutoffsRootObject !== undefined ) {
				window.viewLooplinkProteinsLoadedFromWebServiceTemplate.setPsmPeptideCriteria( psmPeptideCutoffsRootObject );
			}
			if ( annTypeIdDisplay !== undefined && window.viewLooplinkProteinsLoadedFromWebServiceTemplate.setPsmPeptideAnnTypeIdDisplay ) {
				//  Not currently exist: window.viewLooplinkProteinsLoadedFromWebServiceTemplate.setPsmPeptideAnnTypeIdDisplay
				window.viewLooplinkProteinsLoadedFromWebServiceTemplate.setPsmPeptideAnnTypeIdDisplay( annTypeIdDisplay );
			}
		}

	};
};


//  Instance of class

var webserviceDataParamsDistributionCommonCode = new WebserviceDataParamsDistributionCommonCode();
