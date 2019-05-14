
//   viewReportedPeptidesForProteinAllLoadedFromWebServiceTemplate.js

//   Process and load data into the file viewReportedPeptidesForProteinAllLoadedFromWebServiceTemplateFragment.jsp

//   Used on page viewSearchProteinAll.jsp

//////////////////////////////////
// JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

//  require full Handlebars since compiling templates
const Handlebars = require('handlebars');



//  For showing Data for links (Drilldown) (Called by HTML onclick):
import { viewPsmsLoadedFromWebServiceTemplate } from 'page_js/data_pages/project_search_ids_driven_pages/common/viewPsmsLoadedFromWebServiceTemplate.js';


//   Class contructor

var ViewReportedPeptidesForProteinAllLoadedFromWebServiceTemplate = function() {

	var _DATA_LOADED_DATA_KEY = "dataLoaded";
	var _handlebarsTemplate_peptide_protein_all_block_template = null;
	var _handlebarsTemplate_peptide_protein_all_data_row_entry_template = null;
	var _handlebarsTemplate_peptide_protein_all_child_row_entry_template = null;

	var _psmPeptideAnnTypeIdDisplay = null;
	var _psmPeptideCutoffsRootObject = null;
	var _excludeLinksWith_Root =  null;
	var _chosenLinkTypes = undefined;
	
	//////////////
	this.setPsmPeptideCriteria = function( psmPeptideCutoffsRootObject ) {
		_psmPeptideCutoffsRootObject = psmPeptideCutoffsRootObject;
	};

	//////////////
	this.setPsmPeptideAnnTypeIdDisplay = function( psmPeptideAnnTypeIdDisplay ) {
		_psmPeptideAnnTypeIdDisplay = psmPeptideAnnTypeIdDisplay;
	};

	//////////////
	this.setExcludeLinksWith_Root = function( excludeLinksWith_Root ) {
		_excludeLinksWith_Root = excludeLinksWith_Root;
	};
	
	//////////////
	this.setChosenLinkTypes = function( chosenLinkTypes ) {
		_chosenLinkTypes = chosenLinkTypes;
	};
	
	// ////////////
	this.showHideReportedPeptides = function( params ) {
		try {
			var clickedElement = params.clickedElement;
			var $clickedElement = $( clickedElement );
			var $itemToToggle = $clickedElement.next();
			if( $itemToToggle.is(":visible" ) ) {
				$itemToToggle.hide(); 
				$clickedElement.find(".toggle_visibility_expansion_span_jq").show();
				$clickedElement.find(".toggle_visibility_contraction_span_jq").hide();
			} else { 
				$itemToToggle.show();
				$clickedElement.find(".toggle_visibility_expansion_span_jq").hide();
				$clickedElement.find(".toggle_visibility_contraction_span_jq").show();
				this.loadAndInsertReportedPeptidesIfNeeded( { $topTRelement : $itemToToggle, $clickedElement : $clickedElement } );
			}
			return false;  // does not stop bubbling of click event
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
	

	// ////////////
	this.reloadData = function( params ) {
		try {
			var $htmlElement = params.$htmlElement;
			var $itemToToggle = $htmlElement.next();

			this.loadAndInsertReportedPeptidesIfNeeded( { 
				$topTRelement : $itemToToggle, 
				$clickedElement : $htmlElement,
				reloadData : true } );

		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
	
	
	////////////////////////
	this.loadAndInsertReportedPeptidesIfNeeded = function( params ) {

		var objectThis = this;
		var $topTRelement = params.$topTRelement;
		var $clickedElement = params.$clickedElement;
		var reloadData = params.reloadData;
		
		if ( ! reloadData ) {
			var dataLoaded = $topTRelement.data( _DATA_LOADED_DATA_KEY );
			if ( dataLoaded ) {
				return;  //  EARLY EXIT  since data already loaded. 
			}
		}
		
		var project_search_id = $clickedElement.attr( "data-project_search_id" );
		var protein_id = $clickedElement.attr( "data-protein_id" );
		
		//  Convert all attributes to empty string if null or undefined
		if ( ! project_search_id ) {
			project_search_id = "";
		}
		if ( ! protein_id ) {
			protein_id = "";
		}
		if ( _psmPeptideCutoffsRootObject === null || _psmPeptideCutoffsRootObject === undefined ) {
			throw Error( "_psmPeptideCutoffsRootObject not initialized" );
		} 
		var psmPeptideCutoffsForProjectSearchId = _psmPeptideCutoffsRootObject.searches[ project_search_id ];
		if ( psmPeptideCutoffsForProjectSearchId === undefined || psmPeptideCutoffsForProjectSearchId === null ) {
			psmPeptideCutoffsForProjectSearchId = {};
//			throw Error( "Getting data.  Unable to get cutoff data for project_search_id: " + project_search_id );
		}
		var psmPeptideCutoffsForProjectSearchId_JSONString = JSON.stringify( psmPeptideCutoffsForProjectSearchId );
		
		var psmPeptideAnnTypeDisplayPerProjectSearchId_JSONString = null;
		if ( _psmPeptideAnnTypeIdDisplay ) {
			var psmPeptideAnnTypeIdDisplayForProjectSearchId = _psmPeptideAnnTypeIdDisplay.searches[ project_search_id ];
			if ( psmPeptideAnnTypeIdDisplayForProjectSearchId === undefined || psmPeptideAnnTypeIdDisplayForProjectSearchId === null ) {
//				psmPeptideAnnTypeIdDisplayForSearchId = {};
				throw Error( "Getting data.  Unable to get ann type display data for project_search_id: " + project_search_id );
			}
			psmPeptideAnnTypeDisplayPerProjectSearchId_JSONString = JSON.stringify( psmPeptideAnnTypeIdDisplayForProjectSearchId );
		}

		var excludeLinksWith_Root_JSONString = undefined;
		if ( _excludeLinksWith_Root ) {
			excludeLinksWith_Root_JSONString = JSON.stringify( _excludeLinksWith_Root );
		}
		
		var ajaxRequestData = {
				project_search_id : project_search_id,
				protein_id : protein_id,
				psmPeptideCutoffsForProjectSearchId : psmPeptideCutoffsForProjectSearchId_JSONString,
				peptideAnnTypeDisplayPerSearch : psmPeptideAnnTypeDisplayPerProjectSearchId_JSONString,
				excludeLinksWith_Root : excludeLinksWith_Root_JSONString
		};
		if ( _chosenLinkTypes !== null && _chosenLinkTypes !== undefined ) {
			ajaxRequestData.link_type = _chosenLinkTypes;
		}
		$.ajax({
			url : "services/data/getReportedPeptidesAllProteinsPage",
			traditional: true,  //  Force traditional serialization of the data sent
								//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
								//   So searchIds array is passed as "searchIds=<value>" which is what Jersey expects
			data : ajaxRequestData,  // The data sent as params on the URL
			dataType : "json",
			success : function( ajaxResponseData ) {
				try {
					var responseParams = {
							ajaxResponseData : ajaxResponseData, 
							ajaxRequestData : ajaxRequestData,
							$topTRelement : $topTRelement,
							$clickedElement : $clickedElement
					};
					objectThis.loadAndInsertReportedPeptidesResponse( responseParams );
					$topTRelement.data( _DATA_LOADED_DATA_KEY, true );
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			},
	        failure: function(errMsg) {
	        	handleAJAXFailure( errMsg );
	        },
			error : function(jqXHR, textStatus, errorThrown) {
				handleAJAXError(jqXHR, textStatus, errorThrown);
			}
		});
	};
	
	///////////////////
	this.loadAndInsertReportedPeptidesResponse = function( params ) {
		var ajaxResponseData = params.ajaxResponseData;
		var ajaxRequestData = params.ajaxRequestData;
		var $clickedElement = params.$clickedElement;
		var show_children_if_one_row = $clickedElement.attr( "show_children_if_one_row" );
		var peptideAnnotationDisplayNameDescriptionList = ajaxResponseData.peptideAnnotationDisplayNameDescriptionList;
		var psmAnnotationDisplayNameDescriptionList = ajaxResponseData.psmAnnotationDisplayNameDescriptionList;
		var peptide_protein_alls = ajaxResponseData.searchPeptideNoLinkInfoList;
		var $topTRelement = params.$topTRelement;
		var $peptide_protein_all_data_container = $topTRelement.find(".child_data_container_jq");
		if ( $peptide_protein_all_data_container.length === 0 ) {
			throw Error( "unable to find HTML element with class 'child_data_container_jq'" );
		}
		$peptide_protein_all_data_container.empty();
		if ( _handlebarsTemplate_peptide_protein_all_block_template === null ) {
			var handlebarsSource_peptide_protein_all_block_template = $( "#peptide_protein_all_block_template" ).html();
			if ( handlebarsSource_peptide_protein_all_block_template === undefined ) {
				throw Error( "handlebarsSource_peptide_protein_all_block_template === undefined" );
			}
			if ( handlebarsSource_peptide_protein_all_block_template === null ) {
				throw Error( "handlebarsSource_peptide_protein_all_block_template === null" );
			}
			_handlebarsTemplate_peptide_protein_all_block_template = Handlebars.compile( handlebarsSource_peptide_protein_all_block_template );
		}
		if ( _handlebarsTemplate_peptide_protein_all_data_row_entry_template === null ) {
			var handlebarsSource_peptide_protein_all_data_row_entry_template = $( "#peptide_protein_all_data_row_entry_template" ).html();
			if ( handlebarsSource_peptide_protein_all_data_row_entry_template === undefined ) {
				throw Error( "handlebarsSource_peptide_protein_all_data_row_entry_template === undefined" );
			}
			if ( handlebarsSource_peptide_protein_all_data_row_entry_template === null ) {
				throw Error( "handlebarsSource_peptide_protein_all_data_row_entry_template === null" );
			}
			_handlebarsTemplate_peptide_protein_all_data_row_entry_template = Handlebars.compile( handlebarsSource_peptide_protein_all_data_row_entry_template );
		}
		if ( _handlebarsTemplate_peptide_protein_all_child_row_entry_template === null ) {
			var handlebarsSource_peptide_protein_all_child_row_entry_template = $( "#peptide_protein_all_child_row_entry_template" ).html();
			if ( handlebarsSource_peptide_protein_all_child_row_entry_template === undefined ) {
				throw Error( "handlebarsSource_peptide_protein_all_child_row_entry_template === undefined" );
			}
			if ( handlebarsSource_peptide_protein_all_child_row_entry_template === null ) {
				throw Error( "handlebarsSource_peptide_protein_all_child_row_entry_template === null" );
			}
			_handlebarsTemplate_peptide_protein_all_child_row_entry_template = Handlebars.compile( handlebarsSource_peptide_protein_all_child_row_entry_template );
		}
		//  Search for NumberNonUniquePSMs being set in any row
		var showNumberNonUniquePSMs = false;
		for ( var peptide_protein_allIndex = 0; peptide_protein_allIndex < peptide_protein_alls.length ; peptide_protein_allIndex++ ) {
			var peptide_protein_all = peptide_protein_alls[ peptide_protein_allIndex ];
			if ( peptide_protein_all.numNonUniquePsms !== undefined && peptide_protein_all.numNonUniquePsms !== null ) {
				showNumberNonUniquePSMs = true;
				break;
			}
		}
		//  create context for header row
		var context = { 
				showNumberNonUniquePSMs : showNumberNonUniquePSMs,
				peptideAnnotationDisplayNameDescriptionList : peptideAnnotationDisplayNameDescriptionList,
				psmAnnotationDisplayNameDescriptionList : psmAnnotationDisplayNameDescriptionList
		};
		var html = _handlebarsTemplate_peptide_protein_all_block_template(context);
		var $peptide_protein_all_block_template = $(html).appendTo($peptide_protein_all_data_container);
		var peptide_protein_all_table_jq_ClassName = "peptide_protein_all_table_jq";
		var $peptide_protein_all_table_jq = $peptide_protein_all_block_template.find("." + peptide_protein_all_table_jq_ClassName );
		if ( $peptide_protein_all_table_jq.length === 0 ) {
			throw Error( "unable to find HTML element with class '" + peptide_protein_all_table_jq_ClassName + "'" );
		}
		//  Add peptide_protein_all data to the page
		for ( var peptide_protein_allIndex = 0; peptide_protein_allIndex < peptide_protein_alls.length ; peptide_protein_allIndex++ ) {
			var peptide_protein_all = peptide_protein_alls[ peptide_protein_allIndex ];
			//  wrap data in an object to allow adding more fields
			var context = { 
					showNumberNonUniquePSMs : showNumberNonUniquePSMs,
					data : peptide_protein_all, 
					projectSearchId : ajaxRequestData.project_search_id
					};
			var html = _handlebarsTemplate_peptide_protein_all_data_row_entry_template(context);
			var $peptide_protein_all_entry = 
				$(html).appendTo($peptide_protein_all_table_jq);
			//  Get the number of columns of the inserted row so can set the "colspan=" in the next row
			//       that holds the child data
			var $peptide_protein_all_entry__columns = $peptide_protein_all_entry.find("td");
			var peptide_protein_all_entry__numColumns = $peptide_protein_all_entry__columns.length;
			//  colSpan is used as the value for "colspan=" in the <td>
			var childRowHTML_Context = { colSpan : peptide_protein_all_entry__numColumns };
			var childRowHTML = _handlebarsTemplate_peptide_protein_all_child_row_entry_template( childRowHTML_Context );
			//  Add next row for child data
			$( childRowHTML ).appendTo($peptide_protein_all_table_jq);
			//  If only one record, click on it to show it's children
			if ( show_children_if_one_row === "true" && peptide_protein_alls.length === 1 ) {
				$peptide_protein_all_entry.click();
			}
		}
		//  If the function window.linkInfoOverlayWidthResizer() exists, call it to resize the overlay
		if ( window.linkInfoOverlayWidthResizer ) {
			window.linkInfoOverlayWidthResizer();
		}
	};
};

//  Static Singleton Instance of Class
var viewReportedPeptidesForProteinAllLoadedFromWebServiceTemplate = new ViewReportedPeptidesForProteinAllLoadedFromWebServiceTemplate();

window.viewReportedPeptidesForProteinAllLoadedFromWebServiceTemplate = viewReportedPeptidesForProteinAllLoadedFromWebServiceTemplate;

export { viewReportedPeptidesForProteinAllLoadedFromWebServiceTemplate }
