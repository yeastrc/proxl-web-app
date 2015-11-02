
//   viewCrosslinkProteinsLoadedFromWebServiceTemplate.js




//   Process and load data into the file viewCrosslinkProteinsLoadedFromWebServiceTemplateFragment.jsp


//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";


//   Class contructor

var ViewCrosslinkProteinsLoadedFromWebServiceTemplate = function() {

	var _DATA_LOADED_DATA_KEY = "dataLoaded";
	
	var _handlebarsTemplate_crosslink_protein_block_template = null;
	var _handlebarsTemplate_crosslink_protein_data_row_entry_template = null;
	var _handlebarsTemplate_crosslink_protein_child_row_entry_template = null;
	
	
	this.showHideCrosslinkProteins = function( params ) {
		
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

			this.loadAndInsertCrosslinkProteinsIfNeeded( { $topTRelement : $itemToToggle, $clickedElement : $clickedElement } );
		}

		return false;  // does not stop bubbling of click event
	};
	
		
	
	////////////////////////
	
	this.loadAndInsertCrosslinkProteinsIfNeeded = function( params ) {

		var objectThis = this;
		
		var $topTRelement = params.$topTRelement;
		var $clickedElement = params.$clickedElement;
		

		var dataLoaded = $topTRelement.data( _DATA_LOADED_DATA_KEY );
		
		if ( dataLoaded ) {
			
			return;  //  EARLY EXIT  since data already loaded. 
		}
		

		
		var search_idsCommaDelim = $clickedElement.attr( "search_ids" );
		var project_id = $clickedElement.attr( "project_id" );
		var peptide_q_value_cutoff = $clickedElement.attr( "peptide_q_value_cutoff" );
		var psm_q_value_cutoff = $clickedElement.attr( "psm_q_value_cutoff" );
		var protein_1_id = $clickedElement.attr( "protein_1_id" );
		var protein_2_id = $clickedElement.attr( "protein_2_id" );
		var protein_1_position = $clickedElement.attr( "protein_1_position" );
		var protein_2_position = $clickedElement.attr( "protein_2_position" );

		
		//  Convert all attributes to empty string if null or undefined

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
		if ( ! protein_1_id ) {
			protein_1_id = "";
		}
		if ( ! protein_2_id ) {
			protein_2_id = "";
		}
		if ( ! protein_1_position ) {
			protein_1_position = "";
		}
		if ( ! protein_2_position ) {
			protein_2_position = "";
		}
		
		
		if ( search_idsCommaDelim === undefined || search_idsCommaDelim === null || search_idsCommaDelim === "" ) {
			
			throw "attribute 'search_ids' is missing or empty";
		}
		
		//  Convert search ids comma delim to array
		
		var search_ids = [];
		
		var search_idsCommaDelimSplit = search_idsCommaDelim.split(",");
		
		for ( var search_idsCommaDelimSplitCounter = 0; search_idsCommaDelimSplitCounter < search_idsCommaDelimSplit.length; search_idsCommaDelimSplitCounter++ ) {
			
			var search_idsCommaDelimSplitEntry = search_idsCommaDelimSplit[ search_idsCommaDelimSplitCounter ];
			
			if ( search_idsCommaDelimSplitEntry === "" ) {
				continue;
			}
			
			search_ids.push( search_idsCommaDelimSplitEntry );
		}
		
		if ( search_ids.length === 0 ) {
			
			throw "No values found in attribute 'search_ids'.";
		}
		
		
		
		
		var ajaxRequestData = {

				search_ids : search_ids,
				project_id : project_id,
				peptide_q_value_cutoff : peptide_q_value_cutoff,
				psm_q_value_cutoff : psm_q_value_cutoff,
				protein_1_id : protein_1_id,
				protein_2_id : protein_2_id,
				protein_1_position : protein_1_position,
				protein_2_position : protein_2_position,
		};
		
		
		$.ajax({
			url : contextPathJSVar + "/services/data/getCrosslinkProteins",

			traditional: true,  //  Force traditional serialization of the data sent
								//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
								//   So searchIds array is passed as "searchIds=<value>" which is what Jersey expects
			
			data : ajaxRequestData,  // The data sent as params on the URL
			dataType : "json",

			success : function( ajaxResponseData ) {
				
				var responseParams = {
						ajaxResponseData : ajaxResponseData, 
						ajaxRequestData : ajaxRequestData,
						$topTRelement : $topTRelement
				};

				objectThis.loadAndInsertCrosslinkProteinsResponse( responseParams );
				

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
	
	
	this.loadAndInsertCrosslinkProteinsResponse = function( params ) {
		
		var ajaxResponseData = params.ajaxResponseData;
		
		var ajaxRequestData = params.ajaxRequestData;
		

		var crosslink_proteins = ajaxResponseData;
		
		
		
		var $topTRelement = params.$topTRelement;
		
		var $crosslink_protein_data_container = $topTRelement.find(".child_data_container_jq");
		
		if ( $crosslink_protein_data_container.length === 0 ) {
			
			throw "unable to find HTML element with class 'child_data_container_jq'";
		}

		$crosslink_protein_data_container.empty();
		
		if ( _handlebarsTemplate_crosslink_protein_block_template === null ) {
			
			var handlebarsSource_crosslink_protein_block_template = $( "#crosslink_protein_block_template" ).html();

			if ( handlebarsSource_crosslink_protein_block_template === undefined ) {
				throw "handlebarsSource_crosslink_protein_block_template === undefined";
			}
			if ( handlebarsSource_crosslink_protein_block_template === null ) {
				throw "handlebarsSource_crosslink_protein_block_template === null";
			}
			
			_handlebarsTemplate_crosslink_protein_block_template = Handlebars.compile( handlebarsSource_crosslink_protein_block_template );
		}
	
		if ( _handlebarsTemplate_crosslink_protein_data_row_entry_template === null ) {

			var handlebarsSource_crosslink_protein_data_row_entry_template = $( "#crosslink_protein_data_row_entry_template" ).html();

			if ( handlebarsSource_crosslink_protein_data_row_entry_template === undefined ) {
				throw "handlebarsSource_crosslink_protein_data_row_entry_template === undefined";
			}
			if ( handlebarsSource_crosslink_protein_data_row_entry_template === null ) {
				throw "handlebarsSource_crosslink_protein_data_row_entry_template === null";
			}
			
			_handlebarsTemplate_crosslink_protein_data_row_entry_template = Handlebars.compile( handlebarsSource_crosslink_protein_data_row_entry_template );
		}
		
		
		if ( _handlebarsTemplate_crosslink_protein_child_row_entry_template === null ) {

			var handlebarsSource_crosslink_protein_child_row_entry_template = $( "#crosslink_protein_child_row_entry_template" ).html();

			if ( handlebarsSource_crosslink_protein_child_row_entry_template === undefined ) {
				throw "handlebarsSource_crosslink_protein_child_row_entry_template === undefined";
			}
			if ( handlebarsSource_crosslink_protein_child_row_entry_template === null ) {
				throw "handlebarsSource_crosslink_protein_child_row_entry_template === null";
			}
			
			_handlebarsTemplate_crosslink_protein_child_row_entry_template = Handlebars.compile( handlebarsSource_crosslink_protein_child_row_entry_template );
		}

		

		//  Search for bestPeptideQValue being set in any row

		var bestPeptideQValueSetAnyRows = false;
		
		for ( var crosslink_proteinIndex = 0; crosslink_proteinIndex < crosslink_proteins.length ; crosslink_proteinIndex++ ) {
			
			var crosslink_protein = crosslink_proteins[ crosslink_proteinIndex ];
			
			if ( crosslink_protein.searchProteinCrosslink.bestPeptideQValue !== undefined && crosslink_protein.searchProteinCrosslink.bestPeptideQValue !== null ) {
				
				bestPeptideQValueSetAnyRows = true;
				break;
			}
		}
		

		//  create context for header row
		var context = { bestPeptideQValueSetAnyRows : bestPeptideQValueSetAnyRows };

		var html = _handlebarsTemplate_crosslink_protein_block_template(context);

		var $crosslink_protein_block_template = $(html).appendTo($crosslink_protein_data_container);
		

		var crosslink_protein_table_jq_ClassName = "crosslink_protein_table_jq";
		
		var $crosslink_protein_table_jq = $crosslink_protein_block_template.find("." + crosslink_protein_table_jq_ClassName );
	
	//			var $crosslink_protein_table_jq = $crosslink_protein_data_container.find(".crosslink_protein_table_jq");
		
		if ( $crosslink_protein_table_jq.length === 0 ) {
			
			throw "unable to find HTML element with class '" + crosslink_protein_table_jq_ClassName + "'";
		}
		
		
		//  Add crosslink_protein data to the page
	
		for ( var crosslink_proteinIndex = 0; crosslink_proteinIndex < crosslink_proteins.length ; crosslink_proteinIndex++ ) {
	
			var crosslink_protein = crosslink_proteins[ crosslink_proteinIndex ];
			
			//  wrap data in an object to allow adding more fields
			var context = { data : crosslink_protein, bestPeptideQValueSetAnyRows : bestPeptideQValueSetAnyRows };
	
			var html = _handlebarsTemplate_crosslink_protein_data_row_entry_template(context);
	
			var $crosslink_protein_entry = 
				$(html).appendTo($crosslink_protein_table_jq);
			
			
			//  Get the number of columns of the inserted row so can set the "colspan=" in the next row
			//       that holds the child data
			
			var $crosslink_protein_entry__columns = $crosslink_protein_entry.find("td");
			
			var crosslink_protein_entry__numColumns = $crosslink_protein_entry__columns.length;
			
			//  colSpan is used as the value for "colspan=" in the <td>
			var childRowHTML_Context = { colSpan : crosslink_protein_entry__numColumns };
			
			var childRowHTML = _handlebarsTemplate_crosslink_protein_child_row_entry_template( childRowHTML_Context );
			
			//  Add next row for child data
			$( childRowHTML ).appendTo($crosslink_protein_table_jq);
		}
		
		var $openLorkeetLinks = $(".view_spectrum_open_spectrum_link_jq");
		
		addOpenLorikeetViewerClickHandlers( $openLorkeetLinks );
	
		//  Does not seem to work so not run it
//		if ( crosslink_proteins.length > 0 ) {
//			
//			try {
//				$crosslink_protein_block_template.tablesorter(); // gets exception if there are no data rows
//			} catch (e) {
//				
//				var z = 0;
//			}
//		}

		
	};
	
};



//Static Singleton Instance of Class

var viewCrosslinkProteinsLoadedFromWebServiceTemplate = new ViewCrosslinkProteinsLoadedFromWebServiceTemplate();
