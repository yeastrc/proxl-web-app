
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
		
		//  Java class ExcludeLinksWith_JSONRoot
		var excludeLinksWith_Root = undefined
		if ( params.minPSMs !== undefined ||
				params.filterNonUniquePeptides !== undefined || 
				params.filterOnlyOnePeptide !== undefined ||
				params.removeNonUniquePSMs !== undefined ) {

			// Populate this since at least one input is not undefined
			excludeLinksWith_Root = {
					minPSMs : params.minPSMs,
					filterNonUniquePeptides : params.filterNonUniquePeptides,
					filterOnlyOnePeptide : params.filterOnlyOnePeptide,
					removeNonUniquePSMs : params.removeNonUniquePSMs
			};
		}

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
			if ( excludeLinksWith_Root !== undefined ) {
				if ( window.viewPsmsLoadedFromWebServiceTemplate.setExcludeLinksWith_Root ) {
					window.viewPsmsLoadedFromWebServiceTemplate.setExcludeLinksWith_Root( excludeLinksWith_Root );
				}
			}
		}

		if ( window.viewPeptidesRelatedToPSMsByScanId ) {
			if ( psmPeptideCutoffsRootObject !== undefined ) {
				window.viewPeptidesRelatedToPSMsByScanId.setPsmPeptideCriteria( psmPeptideCutoffsRootObject );
			}
			if ( annTypeIdDisplay !== undefined ) {
				window.viewPeptidesRelatedToPSMsByScanId.setPsmPeptideAnnTypeIdDisplay( annTypeIdDisplay );
			}
			if ( excludeLinksWith_Root !== undefined ) {
				if ( window.viewPeptidesRelatedToPSMsByScanId.setExcludeLinksWith_Root ) {
					window.viewPeptidesRelatedToPSMsByScanId.setExcludeLinksWith_Root( excludeLinksWith_Root );
				}
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
			if ( excludeLinksWith_Root !== undefined ) {
				if ( window.viewCrosslinkReportedPeptidesLoadedFromWebServiceTemplate.setExcludeLinksWith_Root ) {
					window.viewCrosslinkReportedPeptidesLoadedFromWebServiceTemplate.setExcludeLinksWith_Root( excludeLinksWith_Root );
				}
			}
		}
		if ( window.viewLooplinkReportedPeptidesLoadedFromWebServiceTemplate ) {
			if ( psmPeptideCutoffsRootObject !== undefined ) {
				window.viewLooplinkReportedPeptidesLoadedFromWebServiceTemplate.setPsmPeptideCriteria( psmPeptideCutoffsRootObject );
			}
			if ( annTypeIdDisplay !== undefined ) {
				window.viewLooplinkReportedPeptidesLoadedFromWebServiceTemplate.setPsmPeptideAnnTypeIdDisplay( annTypeIdDisplay );
			}
			if ( excludeLinksWith_Root !== undefined ) {
				if ( window.viewLooplinkReportedPeptidesLoadedFromWebServiceTemplate.setExcludeLinksWith_Root ) {
					window.viewLooplinkReportedPeptidesLoadedFromWebServiceTemplate.setExcludeLinksWith_Root( excludeLinksWith_Root );
				}
			}
		}
		if ( window.viewMonolinkReportedPeptidesLoadedFromWebServiceTemplate ) {
			if ( psmPeptideCutoffsRootObject !== undefined ) {
				window.viewMonolinkReportedPeptidesLoadedFromWebServiceTemplate.setPsmPeptideCriteria( psmPeptideCutoffsRootObject );
			}
			if ( annTypeIdDisplay !== undefined ) {
				window.viewMonolinkReportedPeptidesLoadedFromWebServiceTemplate.setPsmPeptideAnnTypeIdDisplay( annTypeIdDisplay );
			}
			if ( excludeLinksWith_Root !== undefined ) {
				if ( window.viewMonolinkReportedPeptidesLoadedFromWebServiceTemplate.setExcludeLinksWith_Root ) {
					window.viewMonolinkReportedPeptidesLoadedFromWebServiceTemplate.setExcludeLinksWith_Root( excludeLinksWith_Root );
				}
			}
		}
		if ( window.viewReportedPeptidesForProteinAllLoadedFromWebServiceTemplate ) {
			if ( psmPeptideCutoffsRootObject !== undefined ) {
				window.viewReportedPeptidesForProteinAllLoadedFromWebServiceTemplate.setPsmPeptideCriteria( psmPeptideCutoffsRootObject );
			}
			if ( annTypeIdDisplay !== undefined ) {
				window.viewReportedPeptidesForProteinAllLoadedFromWebServiceTemplate.setPsmPeptideAnnTypeIdDisplay( annTypeIdDisplay );
			}
			if ( excludeLinksWith_Root !== undefined ) {
				if ( window.viewReportedPeptidesForProteinAllLoadedFromWebServiceTemplate.setExcludeLinksWith_Root ) {
					window.viewReportedPeptidesForProteinAllLoadedFromWebServiceTemplate.setExcludeLinksWith_Root( excludeLinksWith_Root );
				}
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
			if ( excludeLinksWith_Root !== undefined ) {
				if ( window.viewMergedPeptidePerSearchDataFromWebServiceTemplate.setExcludeLinksWith_Root ) {
					window.viewMergedPeptidePerSearchDataFromWebServiceTemplate.setExcludeLinksWith_Root( excludeLinksWith_Root );
				}
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
			if ( excludeLinksWith_Root !== undefined ) {
				if ( window.viewCrosslinkProteinsLoadedFromWebServiceTemplate.setExcludeLinksWith_Root ) {
					window.viewCrosslinkProteinsLoadedFromWebServiceTemplate.setExcludeLinksWith_Root( excludeLinksWith_Root );
				}
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
			if ( window.viewLooplinkProteinsLoadedFromWebServiceTemplate.setExcludeLinksWith_Root ) {
				window.viewLooplinkProteinsLoadedFromWebServiceTemplate.setExcludeLinksWith_Root( excludeLinksWith_Root );
			}
		}
		if ( window.viewProteinSingleForMergedProteinAllPageLoadedFromWebServiceTemplate ) {
			if ( psmPeptideCutoffsRootObject !== undefined ) {
				window.viewProteinSingleForMergedProteinAllPageLoadedFromWebServiceTemplate.setPsmPeptideCriteria( psmPeptideCutoffsRootObject );
			}
			if ( annTypeIdDisplay !== undefined && window.viewProteinSingleForMergedProteinAllPageLoadedFromWebServiceTemplate.setPsmPeptideAnnTypeIdDisplay ) {
				//  Not currently exist: window.viewLooplinkProteinsLoadedFromWebServiceTemplate.setPsmPeptideAnnTypeIdDisplay
				window.viewProteinSingleForMergedProteinAllPageLoadedFromWebServiceTemplate.setPsmPeptideAnnTypeIdDisplay( annTypeIdDisplay );
			}
			if ( excludeLinksWith_Root !== undefined ) {
				if ( window.viewProteinSingleForMergedProteinAllPageLoadedFromWebServiceTemplate.setExcludeLinksWith_Root ) {
					window.viewProteinSingleForMergedProteinAllPageLoadedFromWebServiceTemplate.setExcludeLinksWith_Root( excludeLinksWith_Root );
				}
			}
		}
	

	};
};


//  Instance of class

var webserviceDataParamsDistributionCommonCode = new WebserviceDataParamsDistributionCommonCode();

window.webserviceDataParamsDistributionCommonCode = webserviceDataParamsDistributionCommonCode;

export { webserviceDataParamsDistributionCommonCode }
