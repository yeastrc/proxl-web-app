
//   viewLooplinkProteinsLoadedFromWebServiceTemplate.js




//   Process and load data into the file viewLooplinkProteinsLoadedFromWebServiceTemplateFragment.jsp


//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";


//   Class contructor

var ViewLooplinkProteinsLoadedFromWebServiceTemplate = function() {

	var _DATA_LOADED_DATA_KEY = "dataLoaded";
	
	var _handlebarsTemplate_looplink_protein_block_template = null;
	var _handlebarsTemplate_looplink_protein_data_row_entry_template = null;
	var _handlebarsTemplate_looplink_protein_child_row_entry_template = null;
	
	
	this.showHideLooplinkProteins = function( params ) {
		
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

			this.loadAndInsertLooplinkProteinsIfNeeded( { $topTRelement : $itemToToggle, $clickedElement : $clickedElement } );
		}

		return false;  // does not stop bubbling of click event
	};
	
		
	
	////////////////////////
	
	this.loadAndInsertLooplinkProteinsIfNeeded = function( params ) {

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
		var protein_id = $clickedElement.attr( "protein_id" );
		var protein_position_1 = $clickedElement.attr( "protein_position_1" );
		var protein_position_2 = $clickedElement.attr( "protein_position_2" );

		
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
		if ( ! protein_id ) {
			protein_id = "";
		}
		if ( ! protein_position_1 ) {
			protein_position_1 = "";
		}
		if ( ! protein_position_2 ) {
			protein_position_2 = "";
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
				protein_id : protein_id,
				protein_position_1 : protein_position_1,
				protein_position_2 : protein_position_2,
		};
		
		
		$.ajax({
			url : contextPathJSVar + "/services/data/getLooplinkProteins",

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

				objectThis.loadAndInsertLooplinkProteinsResponse( responseParams );
				

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
	
	
	this.loadAndInsertLooplinkProteinsResponse = function( params ) {
		
		var ajaxResponseData = params.ajaxResponseData;
		
		var ajaxRequestData = params.ajaxRequestData;
		

		var looplink_proteins = ajaxResponseData;
		
		
		
		var $topTRelement = params.$topTRelement;
		
		var $looplink_protein_data_container = $topTRelement.find(".child_data_container_jq");
		
		if ( $looplink_protein_data_container.length === 0 ) {
			
			throw "unable to find HTML element with class 'child_data_container_jq'";
		}

		$looplink_protein_data_container.empty();
		
		if ( _handlebarsTemplate_looplink_protein_block_template === null ) {
			
			var handlebarsSource_looplink_protein_block_template = $( "#looplink_protein_block_template" ).html();

			if ( handlebarsSource_looplink_protein_block_template === undefined ) {
				throw "handlebarsSource_looplink_protein_block_template === undefined";
			}
			if ( handlebarsSource_looplink_protein_block_template === null ) {
				throw "handlebarsSource_looplink_protein_block_template === null";
			}
			
			_handlebarsTemplate_looplink_protein_block_template = Handlebars.compile( handlebarsSource_looplink_protein_block_template );
		}
	
		if ( _handlebarsTemplate_looplink_protein_data_row_entry_template === null ) {

			var handlebarsSource_looplink_protein_data_row_entry_template = $( "#looplink_protein_data_row_entry_template" ).html();

			if ( handlebarsSource_looplink_protein_data_row_entry_template === undefined ) {
				throw "handlebarsSource_looplink_protein_data_row_entry_template === undefined";
			}
			if ( handlebarsSource_looplink_protein_data_row_entry_template === null ) {
				throw "handlebarsSource_looplink_protein_data_row_entry_template === null";
			}
			
			_handlebarsTemplate_looplink_protein_data_row_entry_template = Handlebars.compile( handlebarsSource_looplink_protein_data_row_entry_template );
		}
		
		
		if ( _handlebarsTemplate_looplink_protein_child_row_entry_template === null ) {

			var handlebarsSource_looplink_protein_child_row_entry_template = $( "#looplink_protein_child_row_entry_template" ).html();

			if ( handlebarsSource_looplink_protein_child_row_entry_template === undefined ) {
				throw "handlebarsSource_looplink_protein_child_row_entry_template === undefined";
			}
			if ( handlebarsSource_looplink_protein_child_row_entry_template === null ) {
				throw "handlebarsSource_looplink_protein_child_row_entry_template === null";
			}
			
			_handlebarsTemplate_looplink_protein_child_row_entry_template = Handlebars.compile( handlebarsSource_looplink_protein_child_row_entry_template );
		}

		

		//  Search for bestPeptideQValue being set in any row

		var bestPeptideQValueSetAnyRows = false;
		
		for ( var looplink_proteinIndex = 0; looplink_proteinIndex < looplink_proteins.length ; looplink_proteinIndex++ ) {
			
			var looplink_protein = looplink_proteins[ looplink_proteinIndex ];
			
			if ( looplink_protein.searchProteinLooplink.bestPeptideQValue !== undefined && looplink_protein.searchProteinLooplink.bestPeptideQValue !== null ) {
				
				bestPeptideQValueSetAnyRows = true;
				break;
			}
		}
		

		//  create context for header row
		var context = { bestPeptideQValueSetAnyRows : bestPeptideQValueSetAnyRows };

		var html = _handlebarsTemplate_looplink_protein_block_template(context);

		var $looplink_protein_block_template = $(html).appendTo($looplink_protein_data_container);
		

		var looplink_protein_table_jq_ClassName = "looplink_protein_table_jq";
		
		var $looplink_protein_table_jq = $looplink_protein_block_template.find("." + looplink_protein_table_jq_ClassName );
	
	//			var $looplink_protein_table_jq = $looplink_protein_data_container.find(".looplink_protein_table_jq");
		
		if ( $looplink_protein_table_jq.length === 0 ) {
			
			throw "unable to find HTML element with class '" + looplink_protein_table_jq_ClassName + "'";
		}
		
		
		//  Add looplink_protein data to the page
	
		for ( var looplink_proteinIndex = 0; looplink_proteinIndex < looplink_proteins.length ; looplink_proteinIndex++ ) {
	
			var looplink_protein = looplink_proteins[ looplink_proteinIndex ];
			
			//  wrap data in an object to allow adding more fields
			var context = { data : looplink_protein, bestPeptideQValueSetAnyRows : bestPeptideQValueSetAnyRows };
	
			var html = _handlebarsTemplate_looplink_protein_data_row_entry_template(context);
	
			var $looplink_protein_entry = 
				$(html).appendTo($looplink_protein_table_jq);
			
			
			//  Get the number of columns of the inserted row so can set the "colspan=" in the next row
			//       that holds the child data
			
			var $looplink_protein_entry__columns = $looplink_protein_entry.find("td");
			
			var looplink_protein_entry__numColumns = $looplink_protein_entry__columns.length;
			
			//  colSpan is used as the value for "colspan=" in the <td>
			var childRowHTML_Context = { colSpan : looplink_protein_entry__numColumns };
			
			var childRowHTML = _handlebarsTemplate_looplink_protein_child_row_entry_template( childRowHTML_Context );
			
			//  Add next row for child data
			$( childRowHTML ).appendTo($looplink_protein_table_jq);
		}
		
		var $openLorkeetLinks = $(".view_spectrum_open_spectrum_link_jq");
		
		addOpenLorikeetViewerClickHandlers( $openLorkeetLinks );
	
		//  Does not seem to work so not run it
//		if ( looplink_proteins.length > 0 ) {
//			
//			try {
//				$looplink_protein_block_template.tablesorter(); // gets exception if there are no data rows
//			} catch (e) {
//				
//				var z = 0;
//			}
//		}

		
	};
	
};



//Static Singleton Instance of Class

var viewLooplinkProteinsLoadedFromWebServiceTemplate = new ViewLooplinkProteinsLoadedFromWebServiceTemplate();
