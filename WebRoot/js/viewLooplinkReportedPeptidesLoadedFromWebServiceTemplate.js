
//   viewLooplinkReportedPeptidesLoadedFromWebServiceTemplate.js




//   Process and load data into the file viewLooplinkReportedPeptidesLoadedFromWebServiceTemplateFragment.jsp


//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";


//   Class contructor

var ViewLooplinkReportedPeptidesLoadedFromWebServiceTemplate = function() {

	var _DATA_LOADED_DATA_KEY = "dataLoaded";
	
	var _handlebarsTemplate_looplink_peptide_block_template = null;
	var _handlebarsTemplate_looplink_peptide_data_row_entry_template = null;
	var _handlebarsTemplate_looplink_peptide_child_row_entry_template = null;
	
	
	this.showHideLooplinkReportedPeptides = function( params ) {
		
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

			this.loadAndInsertLooplinkReportedPeptidesIfNeeded( { $topTRelement : $itemToToggle, $clickedElement : $clickedElement } );
		}

		return false;  // does not stop bubbling of click event
	};
	
		
	
	////////////////////////
	
	this.loadAndInsertLooplinkReportedPeptidesIfNeeded = function( params ) {

		var objectThis = this;
		
		var $topTRelement = params.$topTRelement;
		var $clickedElement = params.$clickedElement;
		

		var dataLoaded = $topTRelement.data( _DATA_LOADED_DATA_KEY );
		
		if ( dataLoaded ) {
			
			return;  //  EARLY EXIT  since data already loaded. 
		}
		

		
		var reported_peptide_id = $clickedElement.attr( "reported_peptide_id" );
		var search_id = $clickedElement.attr( "search_id" );
		var project_id = $clickedElement.attr( "project_id" );
		var peptide_q_value_cutoff = $clickedElement.attr( "peptide_q_value_cutoff" );
		var psm_q_value_cutoff = $clickedElement.attr( "psm_q_value_cutoff" );
		var protein_id = $clickedElement.attr( "protein_id" );
		var protein_position_1 = $clickedElement.attr( "protein_position_1" );
		var protein_position_2 = $clickedElement.attr( "protein_position_2" );

		
		//  Convert all attributes to empty string if null or undefined
		if ( ! reported_peptide_id ) {
			reported_peptide_id = "";
		}
		if ( ! search_id ) {
			search_id = "";
		}
		if ( ! project_id ) {
			project_id = "";
		}
		if ( ! peptide_q_value_cutoff ) {
			peptide_q_value_cutoff = "";
		}

		//
		if ( ! psm_q_value_cutoff ) {
			psm_q_value_cutoff = "";
		}
		if ( ! protein_id ) {
			protein_id = "";
		}
		if ( ! protein_position_1 ) {
			protein_position_1 = "";
		}
		if ( ! protein_position_2 ) {
			protein_position_2 = "";
		}
		
		
		var ajaxRequestData = {

				reported_peptide_id : reported_peptide_id,
				search_id : search_id,
				project_id : project_id,
				peptide_q_value_cutoff : peptide_q_value_cutoff,
				psm_q_value_cutoff : psm_q_value_cutoff,
				protein_id : protein_id,
				protein_position_1 : protein_position_1,
				protein_position_2 : protein_position_2,
		};
		
		
		$.ajax({
			url : contextPathJSVar + "/services/data/getLooplinkReportedPeptides",

//			traditional: true,  //  Force traditional serialization of the data sent
//								//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
//								//   So searchIds array is passed as "searchIds=<value>" which is what Jersey expects
			
			data : ajaxRequestData,  // The data sent as params on the URL
			dataType : "json",

			success : function( ajaxResponseData ) {
				
				var responseParams = {
						ajaxResponseData : ajaxResponseData, 
						ajaxRequestData : ajaxRequestData,
						$topTRelement : $topTRelement
				};

				objectThis.loadAndInsertLooplinkReportedPeptidesResponse( responseParams );
				

				$topTRelement.data( _DATA_LOADED_DATA_KEY, true );
				
			},
	        failure: function(errMsg) {
	        	handleAJAXFailure( errMsg );
	        },

			error : function(jqXHR, textStatus, errorThrown) {

				handleAJAXError(jqXHR, textStatus, errorThrown);

			}
		});
		
		
		
	};
	
	
	this.loadAndInsertLooplinkReportedPeptidesResponse = function( params ) {
		
		var ajaxResponseData = params.ajaxResponseData;
		
		var ajaxRequestData = params.ajaxRequestData;

		var $topTRelement = params.$topTRelement;
		

		var looplink_peptides = ajaxResponseData;
		
		
		var $looplink_peptide_data_container = $topTRelement.find(".child_data_container_jq");
		
		if ( $looplink_peptide_data_container.length === 0 ) {
			
			throw "unable to find HTML element with class 'child_data_container_jq'";
		}

		$looplink_peptide_data_container.empty();
		
		if ( _handlebarsTemplate_looplink_peptide_block_template === null ) {
			
			var handlebarsSource_looplink_peptide_block_template = $( "#looplink_peptide_block_template" ).html();
			
			if ( handlebarsSource_looplink_peptide_block_template === undefined ) {
				throw "handlebarsSource_looplink_peptide_block_template === undefined";
			}
			if ( handlebarsSource_looplink_peptide_block_template === null ) {
				throw "handlebarsSource_looplink_peptide_block_template === null";
			}
			
			_handlebarsTemplate_looplink_peptide_block_template = Handlebars.compile( handlebarsSource_looplink_peptide_block_template );
		}
	
		if ( _handlebarsTemplate_looplink_peptide_data_row_entry_template === null ) {

			var handlebarsSource_looplink_peptide_data_row_entry_template = $( "#looplink_peptide_data_row_entry_template" ).html();
			
			if ( handlebarsSource_looplink_peptide_data_row_entry_template === undefined ) {
				throw "handlebarsSource_looplink_peptide_data_row_entry_template === undefined";
			}
			if ( handlebarsSource_looplink_peptide_data_row_entry_template === null ) {
				throw "handlebarsSource_looplink_peptide_data_row_entry_template === null";
			}
			
			_handlebarsTemplate_looplink_peptide_data_row_entry_template = Handlebars.compile( handlebarsSource_looplink_peptide_data_row_entry_template );
		}
		
		
		if ( _handlebarsTemplate_looplink_peptide_child_row_entry_template === null ) {

			if ( _handlebarsTemplate_looplink_peptide_child_row_entry_template === null ) {

				var handlebarsSource_looplink_peptide_child_row_entry_template = $( "#looplink_peptide_child_row_entry_template" ).html();

				if ( handlebarsSource_looplink_peptide_child_row_entry_template === undefined ) {
					throw "handlebarsSource_looplink_peptide_child_row_entry_template === undefined";
				}
				if ( handlebarsSource_looplink_peptide_child_row_entry_template === null ) {
					throw "handlebarsSource_looplink_peptide_child_row_entry_template === null";
				}
				
				_handlebarsTemplate_looplink_peptide_child_row_entry_template = Handlebars.compile( handlebarsSource_looplink_peptide_child_row_entry_template );
			}
		}

		


		//  Search for qvalue being set in any row

		var qvalueSetAnyRows = false;

		for ( var looplink_peptideIndex = 0; looplink_peptideIndex < looplink_peptides.length ; looplink_peptideIndex++ ) {
	
			var looplink_peptide = looplink_peptides[ looplink_peptideIndex ];
			
			if ( looplink_peptide.qvalue !== undefined && looplink_peptide.qvalue !== null ) {
				
				qvalueSetAnyRows = true;
				break;
			}
		}
		

		//  create context for header row
		var context = { qvalueSetAnyRows : qvalueSetAnyRows };

		var html = _handlebarsTemplate_looplink_peptide_block_template(context);

		var $looplink_peptide_block_template = $(html).appendTo($looplink_peptide_data_container);
		
		var looplink_peptide_table_jq_ClassName = "looplink_peptide_table_jq";
		
		var $looplink_peptide_table_jq = $looplink_peptide_block_template.find("." + looplink_peptide_table_jq_ClassName );
	
		if ( $looplink_peptide_table_jq.length === 0 ) {
			
			throw "unable to find HTML element with class '" + looplink_peptide_table_jq_ClassName + "'";
		}
		
	
		var percolatorPsmFound = false;
		
		//  Add looplink_peptide data to the page
	
		for ( var looplink_peptideIndex = 0; looplink_peptideIndex < looplink_peptides.length ; looplink_peptideIndex++ ) {
	
			var looplink_peptide = looplink_peptides[ looplink_peptideIndex ];
			
			//  wrap data in an object to allow adding more fields
			var context = { data : looplink_peptide, searchId : ajaxRequestData.search_id, qvalueSetAnyRows : qvalueSetAnyRows };
	
			var html = _handlebarsTemplate_looplink_peptide_data_row_entry_template(context);
	
			var $looplink_peptide_entry = 
				$(html).appendTo($looplink_peptide_table_jq);
			

			//  Get the number of columns of the inserted row so can set the "colspan=" in the next row
			//       that holds the child data
			
			var $looplink_peptide_entry__columns = $looplink_peptide_entry.find("td");
			
			var looplink_peptide_entry__numColumns = $looplink_peptide_entry__columns.length;
			
			//  colSpan is used as the value for "colspan=" in the <td>
			var childRowHTML_Context = { colSpan : looplink_peptide_entry__numColumns };
			
			var childRowHTML = _handlebarsTemplate_looplink_peptide_child_row_entry_template( childRowHTML_Context );
			
			//  Add next row for child data
			$( childRowHTML ).appendTo($looplink_peptide_table_jq);			
		}
		
		if ( ! percolatorPsmFound ) {
			
			//  Remove percolatorPsm columns aloop the table since no data found
			var $percolatorPsm_columns_jq = $looplink_peptide_table_jq.find(".percolatorPsm_columns_jq");
			$percolatorPsm_columns_jq.remove();
		}
		
		var $openLorkeetLinks = $(".view_spectrum_open_spectrum_link_jq");
		
		addOpenLorikeetViewerClickHandlers( $openLorkeetLinks );
	
		//  Does not seem to work so not run it
//		if ( looplink_peptides.length > 0 ) {
//			
//			try {
//				$looplink_peptide_block_template.tablesorter(); // gets exception if there are no data rows
//			} catch (e) {
//				
//				var z = 0;
//			}
//		}

		
	};
	
};



//Static Singleton Instance of Class

var viewLooplinkReportedPeptidesLoadedFromWebServiceTemplate = new ViewLooplinkReportedPeptidesLoadedFromWebServiceTemplate();
