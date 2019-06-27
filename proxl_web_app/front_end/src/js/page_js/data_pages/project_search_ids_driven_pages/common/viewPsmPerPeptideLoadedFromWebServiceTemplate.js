
//  viewPsmPerPeptideLoadedFromWebServiceTemplate.js

//  Process and load data into the file viewPsmPerPeptideLoadedFromWebServiceTemplateFragment.jsp


//JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

//  require full Handlebars since compiling templates
const Handlebars = require('handlebars');


//Class contructor

var ViewPsmPerPeptideLoadedFromWebServiceTemplate = function() {

	var _DATA_LOADED_DATA_KEY = "dataLoaded";
	var _handlebarsTemplate_psm_per_peptide_block_template = null;
	var _handlebarsTemplate_psm_per_peptide_data_row_entry_template = null;
	var _psm_per_peptide_row_entry_no_annotation_data_no_scan_data_row_HTML = null;

	var _excludeLinksWith_Root =  null;
	var _psmPeptideAnnTypeIdDisplay = null;
	var _psmPeptideCutoffsRootObject = null;

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

	//////////
	//   Called by HTML Element onclick 
	this.showHidePsmPerPeptide = function( params ) {
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
				this.loadAndInsertPsmsIfNeeded( { $topTRelement : $itemToToggle, $clickedElement : $clickedElement } );
			}
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
		return false;  // does not stop bubbling of click event
	};

	////////////////////////
	this.loadAndInsertPsmsIfNeeded = function( params ) {
		var objectThis = this;
		var $topTRelement = params.$topTRelement;
		var $clickedElement = params.$clickedElement;
		var dataLoaded = $topTRelement.data( _DATA_LOADED_DATA_KEY );
		if ( dataLoaded ) {
			return;  //  EARLY EXIT  since data already loaded. 
		}
		var psm_id = $clickedElement.attr( "data-psm_id" );
		var project_search_id = $clickedElement.attr( "data-project_search_id" );
		var ajaxRequestData = {
				psm_id : psm_id,
				project_search_id : project_search_id
		};
		$.ajax({
			url : "services/data/getPsmPerPeptide",
//			traditional: true,  //  Force traditional serialization of the data sent
//			//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
//			//   So searchIds array is passed as "searchIds=<value>" which is what Jersey expects
			data : ajaxRequestData,  // The data sent as params on the URL
			dataType : "json",
			success : function( ajaxResponseData ) {
				try {
					objectThis.loadAndInsertPsmsResponse( 
							{ ajaxResponseData : ajaxResponseData, 
								$topTRelement : $topTRelement, 
								ajaxRequestData : ajaxRequestData
							} );
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

	////////////
	this.loadAndInsertPsmsResponse = function( params ) {
		
		var ajaxRequestData = params.ajaxRequestData;
		var ajaxResponseData = params.ajaxResponseData;
		
		var annotationLabels = ajaxResponseData.annotationLabels;
		var peptideRows = ajaxResponseData.peptideRows;
		var searchHasScanData = ajaxResponseData.searchHasScanData;
		
		//	var ajaxRequestData = params.ajaxRequestData;
		var $topTRelement = params.$topTRelement;
		var $psm_per_peptide_data_container = $topTRelement.find(".child_data_container_jq");
		if ( $psm_per_peptide_data_container.length === 0 ) {
			throw Error( "unable to find HTML element with class 'child_data_container_jq'" );
		}
		$psm_per_peptide_data_container.empty();
		if ( _handlebarsTemplate_psm_per_peptide_block_template === null ) {
			var handlebarsSource_psm_per_peptide_block_template = $( "#psm_per_peptide_block_template" ).html();
			if ( handlebarsSource_psm_per_peptide_block_template === undefined ) {
				throw Error( "handlebarsSource_psm_per_peptide_block_template === undefined" );
			}
			if ( handlebarsSource_psm_per_peptide_block_template === null ) {
				throw Error( "handlebarsSource_psm_per_peptide_block_template === null" );
			}
			_handlebarsTemplate_psm_per_peptide_block_template = Handlebars.compile( handlebarsSource_psm_per_peptide_block_template );
		}
		if ( _handlebarsTemplate_psm_per_peptide_data_row_entry_template === null ) {
			var handlebarsSource_psm_per_peptide_row_entry_template = $( "#psm_per_peptide_data_row_entry_template" ).html();
			if ( handlebarsSource_psm_per_peptide_row_entry_template === undefined ) {
				throw Error( "handlebarsSource_psm_per_peptide_row_entry_template === undefined" );
			}
			if ( handlebarsSource_psm_per_peptide_row_entry_template === null ) {
				throw Error( "handlebarsSource_psm_per_peptide_row_entry_template === null" );
			}
			_handlebarsTemplate_psm_per_peptide_data_row_entry_template = Handlebars.compile( handlebarsSource_psm_per_peptide_row_entry_template );
		}
		if ( _psm_per_peptide_row_entry_no_annotation_data_no_scan_data_row_HTML === null ) {
			_psm_per_peptide_row_entry_no_annotation_data_no_scan_data_row_HTML = $("#psm_data_row_entry_no_annotation_data_no_scan_data_row").html();
		}
		
		var peptideLinkPosition_1_AnyRows = false;
		var peptideLinkPosition_2_AnyRows = false;
		var scanDataAnyRows = false;
		var scanNumberAnyRows = false;
		var scanFilenameAnyRows = false;
		
		peptideRows.forEach(function( peptideRow, index, array) {
			if ( peptideRow.peptideLinkPosition_1 !== undefined && 
					peptideRow.peptideLinkPosition_1 !== null && 
					peptideRow.peptideLinkPosition_1 !== -1 ) {
				peptideLinkPosition_1_AnyRows = true;
			}
			if ( peptideRow.peptideLinkPosition_2 !== undefined && 
					peptideRow.peptideLinkPosition_2 !== null &&
					peptideRow.peptideLinkPosition_2 !== -1 ) {
				peptideLinkPosition_2_AnyRows = true;
			}
			if (  peptideRow.showViewSpectrumLink ) {
				scanDataAnyRows = true;
			}
			if (  peptideRow.scanNumber ) {
				scanNumberAnyRows = true;
			}
			if (  peptideRow.scanFilename ) {
				scanFilenameAnyRows = true;
			}
		}, this );

		var showViewSpectrumLinkColumn = false;
		
		if ( searchHasScanData && scanDataAnyRows ) {
			showViewSpectrumLinkColumn = true;
		}
		
		//  Context for creating column headings HTML
		var context = {
				annotationLabels : annotationLabels,
				peptideLinkPosition_1_AnyRows : peptideLinkPosition_1_AnyRows,
				peptideLinkPosition_2_AnyRows : peptideLinkPosition_2_AnyRows,
				showViewSpectrumLinkColumn : showViewSpectrumLinkColumn,
				scanDataAnyRows : scanDataAnyRows,
				scanNumberAnyRows : scanNumberAnyRows,
				scanFilenameAnyRows : scanFilenameAnyRows,
				project_search_id : ajaxRequestData.project_search_id
		};

		var html = _handlebarsTemplate_psm_per_peptide_block_template( context );
		var $psm_per_peptide_block_template = $( html ).appendTo( $psm_per_peptide_data_container ); 
		var $psm_per_peptide_table_jq = $psm_per_peptide_block_template.find(".psm_per_peptide_table_jq");

		if ( $psm_per_peptide_table_jq.length === 0 ) {
			throw Error( "unable to find HTML element with class 'psm_per_peptide_table_jq'" );
		}

		peptideRows.forEach(function( peptideRow, index, array) {
			// Build Mods
			var mods = [];
			peptideRow.mods.forEach(function( modEntry, index, array ) {
				mods.push( modEntry.position + "(" + modEntry.mass + ")" );
			}, this );
			
			var modsString = mods.join(",");
			
			//  Context for creating data row HTML
			var context = { 
					peptideRow : peptideRow,
					mods : modsString,
					peptideLinkPosition_1_AnyRows : peptideLinkPosition_1_AnyRows,
					peptideLinkPosition_2_AnyRows : peptideLinkPosition_2_AnyRows,
					showViewSpectrumLinkColumn : showViewSpectrumLinkColumn,
					scanDataAnyRows : scanDataAnyRows,
					scanNumberAnyRows : scanNumberAnyRows,
					scanFilenameAnyRows : scanFilenameAnyRows
			};
			var html = _handlebarsTemplate_psm_per_peptide_data_row_entry_template(context);

			//		var $psm_per_peptide_entry = 
			$(html).appendTo($psm_per_peptide_table_jq);
		}, this );

		//  Add tablesorter to the populated table of psm data
		setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else
			$psm_per_peptide_table_jq.tablesorter(); // gets exception if there are no data rows
		},10);

		addToolTips( $psm_per_peptide_block_template );

		if ( window.linkInfoOverlayWidthResizer ) {
			window.linkInfoOverlayWidthResizer();
		}
	};
};

//	Static Singleton Instance of Class
var viewPsmPerPeptideLoadedFromWebServiceTemplate = new ViewPsmPerPeptideLoadedFromWebServiceTemplate();

window.viewPsmPerPeptideLoadedFromWebServiceTemplate = viewPsmPerPeptideLoadedFromWebServiceTemplate;

export { viewPsmPerPeptideLoadedFromWebServiceTemplate }
