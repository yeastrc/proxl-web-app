
//   viewPsmsLoadedFromWebServiceTemplate.js

//   Process and load data into the file viewPsmsLoadedFromWebServiceTemplateFragment.jsp

//////////////////////////////////
// JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

//   Class contructor

var ViewPsmsLoadedFromWebServiceTemplate = function() {

	var _DATA_LOADED_DATA_KEY = "dataLoaded";
	var _handlebarsTemplate_psm_block_template = null;
	var _handlebarsTemplate_psm_data_row_entry_template = null;
	var _psm_data_row_entry_no_annotation_data_no_scan_data_row_HTML = null;
	
	var _psmPeptideAnnTypeIdDisplay = null;
	var _psmPeptideCutoffsRootObject = null;
	//   Currently expect _psmPeptideCriteria = 
//					searches: Object
//						128: Object			
//							peptideCutoffValues: Object
//								238: Object
//									id: 238
//									value: "0.01"
//							psmCutoffValues: Object
//								384: Object
//									id: 384
//									value: "0.01"
//							searchId: 128
//           The key to:
//				searches - searchId
//				peptideCutoffValues and psmCutoffValues - annotation type id
//			peptideCutoffValues.id and psmCutoffValues.id - annotation type id
	
	//////////////
	this.setPsmPeptideCriteria = function( psmPeptideCutoffsRootObject ) {
		_psmPeptideCutoffsRootObject = psmPeptideCutoffsRootObject;
	};

	//////////////
	this.setPsmPeptideAnnTypeIdDisplay = function( psmPeptideAnnTypeIdDisplay ) {
		_psmPeptideAnnTypeIdDisplay = psmPeptideAnnTypeIdDisplay;
	};
	
	//////////
	//   Called by HTML Element onclick 
	this.showHidePsms = function( params ) {
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
		var initial_scan_id = $clickedElement.attr( "data-initial_scan_id" );
		var reported_peptide_id = $clickedElement.attr( "data-reported_peptide_id" );
		var project_search_id = $clickedElement.attr( "data-project_search_id" );
		var skip_associated_peptides_link = $clickedElement.attr( "data-skip_associated_peptides_link" );
		var show_associated_peptides_link_true = true; // default to true
		if ( skip_associated_peptides_link === "true" ) {
			show_associated_peptides_link_true = false;
		}
		//  Convert all attributes to empty string if null or undefined
		if ( ! reported_peptide_id ) {
			reported_peptide_id = "";
		}
		if ( ! project_search_id ) {
			project_search_id = "";
		}
		//   Currently expect _psmPeptideCriteria = 
//	           The key to:
//					searches - searchId
//					peptideCutoffValues and psmCutoffValues - annotation type id
//				peptideCutoffValues.id and psmCutoffValues.id - annotation type id
		var psmPeptideCutoffsForProjectSearchId = _psmPeptideCutoffsRootObject.searches[ project_search_id ];
		if ( psmPeptideCutoffsForProjectSearchId === undefined || psmPeptideCutoffsForProjectSearchId === null ) {
			psmPeptideCutoffsForProjectSearchId = {};
//			throw Error( "Getting data.  Unable to get cutoff data for project_search_id: " + project_search_id );
		}
		var psmPeptideCutoffsForProjectSearchId_JSONString = JSON.stringify( psmPeptideCutoffsForProjectSearchId );
		var psmAnnTypeDisplayIncludeExclude_JSONString = null;
		if ( _psmPeptideAnnTypeIdDisplay ) {
			var psmPeptideAnnTypeIdDisplayForSearchId = _psmPeptideAnnTypeIdDisplay.searches[ project_search_id ];
			if ( psmPeptideAnnTypeIdDisplayForSearchId === undefined || psmPeptideAnnTypeIdDisplayForSearchId === null ) {
//				psmPeptideAnnTypeIdDisplayForSearchId = {};
				throw Error( "Getting data.  Unable to get ann type display data for project_search_id: " + project_search_id );
			}
			var psmAnnTypeIdDisplayForSearchId = psmPeptideAnnTypeIdDisplayForSearchId.psm;
			if ( psmAnnTypeIdDisplayForSearchId === undefined || psmAnnTypeIdDisplayForSearchId === null ) {
				throw Error( "Getting data.  Unable to get ann type display data for project_search_id: " + project_search_id + " and .psm" );
			}
			var psmAnnTypeDisplayIncludeExclude = { inclAnnTypeId : psmAnnTypeIdDisplayForSearchId };
			psmAnnTypeDisplayIncludeExclude_JSONString = JSON.stringify( psmAnnTypeDisplayIncludeExclude );
		}
		var ajaxRequestData = {
				reported_peptide_id : reported_peptide_id,
				project_search_id : project_search_id,
				psmPeptideCutoffsForProjectSearchId : psmPeptideCutoffsForProjectSearchId_JSONString,
				psmAnnTypeDisplayIncludeExclude : psmAnnTypeDisplayIncludeExclude_JSONString
		};
		$.ajax({
			url : contextPathJSVar + "/services/data/getPsms",
//			traditional: true,  //  Force traditional serialization of the data sent
//								//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
//								//   So searchIds array is passed as "searchIds=<value>" which is what Jersey expects
			data : ajaxRequestData,  // The data sent as params on the URL
			dataType : "json",
			success : function( ajaxResponseData ) {
				try {
					objectThis.loadAndInsertPsmsResponse( 
							{ ajaxResponseData : ajaxResponseData, 
								$topTRelement : $topTRelement, 
								ajaxRequestData : ajaxRequestData,
								otherData : 
								{ show_associated_peptides_link_true : show_associated_peptides_link_true,
									initial_scan_id : initial_scan_id }
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
		var show_associated_peptides_link_true = params.otherData.show_associated_peptides_link_true;
		var initial_scan_id_String = params.otherData.initial_scan_id;
		var annotationDisplayNameDescriptionList = ajaxResponseData.annotationDisplayNameDescriptionList;
		var psms = ajaxResponseData.psmWebDisplayList;
		var initial_scan_id = parseInt( initial_scan_id_String, 10 );
		if ( isNaN( initial_scan_id ) ) {
			initial_scan_id = null;
		}
	//	var ajaxRequestData = params.ajaxRequestData;
		var $topTRelement = params.$topTRelement;
		var $psm_data_container = $topTRelement.find(".child_data_container_jq");
		if ( $psm_data_container.length === 0 ) {
			throw Error( "unable to find HTML element with class 'child_data_container_jq'" );
		}
		$psm_data_container.empty();
		if ( _handlebarsTemplate_psm_block_template === null ) {
			var handlebarsSource_psm_block_template = $( "#psm_block_template" ).html();
			if ( handlebarsSource_psm_block_template === undefined ) {
				throw Error( "handlebarsSource_psm_block_template === undefined" );
			}
			if ( handlebarsSource_psm_block_template === null ) {
				throw Error( "handlebarsSource_psm_block_template === null" );
			}
			_handlebarsTemplate_psm_block_template = Handlebars.compile( handlebarsSource_psm_block_template );
		}
		if ( _handlebarsTemplate_psm_data_row_entry_template === null ) {
			var handlebarsSource_psm_data_row_entry_template = $( "#psm_data_row_entry_template" ).html();
			if ( handlebarsSource_psm_data_row_entry_template === undefined ) {
				throw Error( "handlebarsSource_psm_data_row_entry_template === undefined" );
			}
			if ( handlebarsSource_psm_data_row_entry_template === null ) {
				throw Error( "handlebarsSource_psm_data_row_entry_template === null" );
			}
			_handlebarsTemplate_psm_data_row_entry_template = Handlebars.compile( handlebarsSource_psm_data_row_entry_template );
		}
		if ( _psm_data_row_entry_no_annotation_data_no_scan_data_row_HTML === null ) {
			_psm_data_row_entry_no_annotation_data_no_scan_data_row_HTML = $("#psm_data_row_entry_no_annotation_data_no_scan_data_row").html();
		}
		var scanDataAnyRows = false;
		var scanNumberAnyRows = false;
		var scanFilenameAnyRows = false;
		var chargeDataAnyRows = false;
		for ( var psmIndex = 0; psmIndex < psms.length ; psmIndex++ ) {
			var psm = psms[ psmIndex ];
			if (  psm.psmDTO.scanId ) {
				scanDataAnyRows = true;
			}
			if (  psm.scanNumber ) {
				scanNumberAnyRows = true;
			}
			if (  psm.scanFilename ) {
				scanFilenameAnyRows = true;
			}
			if (  psm.charge ) {
				chargeDataAnyRows = true;
			}
		}
		//  Context for creating column headings HTML
		var context = {
				annotationDisplayNameDescriptionList : annotationDisplayNameDescriptionList,
				scanDataAnyRows : scanDataAnyRows,
				scanNumberAnyRows : scanNumberAnyRows,
				scanFilenameAnyRows : scanFilenameAnyRows,
				chargeDataAnyRows : chargeDataAnyRows
		};
		context.scanDataAnyRows = scanDataAnyRows;
		context.scanNumberAnyRows = scanNumberAnyRows;
		context.scanFilenameAnyRows = scanFilenameAnyRows;
		context.chargeDataAnyRows = chargeDataAnyRows;
		var html = _handlebarsTemplate_psm_block_template(context);
		var $psm_block_template = $( html ).appendTo( $psm_data_container ); 
		var $psm_table_jq = $psm_block_template.find(".psm_table_jq");
	//			var $psm_table_jq = $psm_data_container.find(".psm_table_jq");
		if ( $psm_table_jq.length === 0 ) {
			throw Error( "unable to find HTML element with class 'psm_table_jq'" );
		}
		//  Add psm data to the page
		for ( var psmIndex = 0; psmIndex < psms.length ; psmIndex++ ) {
			var html = null;
			if ( ! scanDataAnyRows && ! chargeDataAnyRows 
					&& ! scanNumberAnyRows && ! scanFilenameAnyRows 
					&& annotationDisplayNameDescriptionList.length === 0 ) {
				//  Nothing to display so show contents of this which for now is <tr><td>PSM</td></tr>
				html = _psm_data_row_entry_no_annotation_data_no_scan_data_row_HTML;
			} else {
				var psm = psms[ psmIndex ];
				if (  psm.chargeSet ) {
					psm.chargeDisplay = psm.charge;
				}
				//  Context for creating data row HTML
				var context = { psm : psm,
						scanDataAnyRows : scanDataAnyRows,
						scanNumberAnyRows : scanNumberAnyRows,
						scanFilenameAnyRows : scanFilenameAnyRows,
						chargeDataAnyRows : chargeDataAnyRows
				};
				//  psm.psmCountForOtherAssocScanId is count of psms with same scan id, excluding current psm
				if ( psm.psmCountForOtherAssocScanId < 1 ) {
					context.uniquePSM = true;
				}
				if ( psm.psmDTO.scanId !== undefined
						&& psm.psmDTO.scanId !== null
						&& initial_scan_id !== null 
						&& psm.psmDTO.scanId === initial_scan_id ) {
					context.scanIdMatchesInitialScanId = true;
				}
				context.project_id = ajaxRequestData.project_id;
				context.project_search_id = ajaxRequestData.project_search_id;
				context.reported_peptide_id = ajaxRequestData.reported_peptide_id;
				context.show_associated_peptides_link_true = show_associated_peptides_link_true;
				html = _handlebarsTemplate_psm_data_row_entry_template(context);
			}
	//		var $psm_entry = 
			$(html).appendTo($psm_table_jq);
		}
		
		//  Add tablesorter to the populated table of psm data
		setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else
			$psm_table_jq.tablesorter(); // gets exception if there are no data rows
		},10);
		
		var $openLorkeetLinks = $psm_table_jq.find(".view_spectrum_open_spectrum_link_jq");
		addOpenLorikeetViewerClickHandlers( $openLorkeetLinks );
		addToolTips( $psm_block_template );
		
		//  Does not seem to work so not run it
//		if ( psms.length > 0 ) {
//			
//			try {
//				$psm_block_template.tablesorter(); // gets exception if there are no data rows
//			} catch (e) {
//				
//				var z = 0;
//			}
//		}
		if ( window.linkInfoOverlayWidthResizer ) {
			window.linkInfoOverlayWidthResizer();
		}
	};
};

//  Static Singleton Instance of Class
var viewPsmsLoadedFromWebServiceTemplate = new ViewPsmsLoadedFromWebServiceTemplate();