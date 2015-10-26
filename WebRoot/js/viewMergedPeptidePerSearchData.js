
//  viewMergedPeptidePerSearchData.js

//  Retrieve and display the Per Search data which is the Reported peptide for the search



//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";


//   Class contructor

var ViewMergedPeptidePerSearchDataFromWebServiceTemplate = function() {


	var _DATA_LOADED_DATA_KEY = "dataLoaded";
	
	var _handlebarsTemplate_peptide_data_per_search_block_template = null;
	var _handlebarsTemplate_peptide_data_per_search_data_row_template = null;
	var _handlebarsTemplate_peptide_data_per_search_child_row_template = null;
	
	
	this.showHideCrosslinkReportedPeptides = function( params ) {
		
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

			this.loadAndInsertCrosslinkReportedPeptidesIfNeeded( { $topTRelement : $itemToToggle, $clickedElement : $clickedElement } );
		}

		return false;  // does not stop bubbling of click event
	};
	
		
	
	////////////////////////
	
	this.loadAndInsertCrosslinkReportedPeptidesIfNeeded = function( params ) {

		var objectThis = this;
		
		var $topTRelement = params.$topTRelement;
		var $clickedElement = params.$clickedElement;
		

		var dataLoaded = $topTRelement.data( _DATA_LOADED_DATA_KEY );
		
		if ( dataLoaded ) {
			
			return;  //  EARLY EXIT  since data already loaded. 
		}
		

		
		var project_id = $( "#project_id_for_js" ).val();
		var peptide_q_value_cutoff = $( "#peptide_q_value_cutoff_for_js" ).val();
		var psm_q_value_cutoff = $( "#psm_q_value_cutoff_for_js" ).val();

		var unified_reported_peptide_id = $clickedElement.attr( "data-unified_reported_peptide_id" );


		var search_ids = [];
		
		
		var $search_id_input_field_jq_List = $(".search_id_input_field_jq");
		
		if ( $search_id_input_field_jq_List.length === 0 ) {
			
			throw "input fields with class 'search_id_input_field_jq' containing search ids is missing from the page";
		}
		
		$search_id_input_field_jq_List.each( function( index, element ) {
			
			var search_id = $( this ).val();
			
			//  Convert all attributes to empty string if null or undefined
			if ( ! search_id ) {
				search_id = "";
			}
			
			search_ids.push( search_id );
			
		} );
		
		
		
		
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
		if ( ! unified_reported_peptide_id ) {
			unified_reported_peptide_id = "";
		}
		
		
		
		
		var ajaxRequestData = {

				search_ids : search_ids,
				project_id : project_id,
				peptide_q_value_cutoff : peptide_q_value_cutoff,
				psm_q_value_cutoff : psm_q_value_cutoff,
				unified_reported_peptide_id : unified_reported_peptide_id,
		};
		
		
		$.ajax({
			url : contextPathJSVar + "/services/data/getReportedPeptidesForUnifiedPeptId",

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

				objectThis.loadAndInsertCrosslinkReportedPeptidesResponse( responseParams );
				

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
	
	
	this.loadAndInsertCrosslinkReportedPeptidesResponse = function( params ) {
		
		var ajaxResponseData = params.ajaxResponseData;
		
		var ajaxRequestData = params.ajaxRequestData;
		

		var reported_peptides = ajaxResponseData;
		
		var project_id = $( "#project_id_for_js" ).val();

		//  Convert all attributes to empty string if null or undefined
		if ( ! project_id ) {
			project_id = "";
		}

		var psm_q_value_cutoff = $( "#psm_q_value_cutoff_for_js" ).val();

		//
		if ( ! psm_q_value_cutoff ) {
			psm_q_value_cutoff = "";
		}

		
		
		var $topTRelement = params.$topTRelement;
		
		var $data_container = $topTRelement.find(".child_data_container_jq");
		
		if ( $data_container.length === 0 ) {
			
			throw "unable to find HTML element with class 'child_data_container_jq'";
		}

		$data_container.empty();
		
		if ( _handlebarsTemplate_peptide_data_per_search_block_template === null ) {
			
			var handlebarsSource_peptide_data_per_search_block_template = $( "#peptide_data_per_search_block_template" ).html();

			if ( handlebarsSource_peptide_data_per_search_block_template === undefined ) {
				throw "handlebarsSource_peptide_data_per_search_block_template === undefined";
			}
			if ( handlebarsSource_peptide_data_per_search_block_template === null ) {
				throw "handlebarsSource_peptide_data_per_search_block_template === null";
			}
			
			_handlebarsTemplate_peptide_data_per_search_block_template = Handlebars.compile( handlebarsSource_peptide_data_per_search_block_template );
		}
	
		if ( _handlebarsTemplate_peptide_data_per_search_data_row_template === null ) {

			var handlebarsSource_peptide_data_per_search_data_row_template = $( "#peptide_data_per_search_data_row_template" ).html();

			if ( handlebarsSource_peptide_data_per_search_data_row_template === undefined ) {
				throw "handlebarsSource_peptide_data_per_search_data_row_template === undefined";
			}
			if ( handlebarsSource_peptide_data_per_search_data_row_template === null ) {
				throw "handlebarsSource_peptide_data_per_search_data_row_template === null";
			}
			
			_handlebarsTemplate_peptide_data_per_search_data_row_template = Handlebars.compile( handlebarsSource_peptide_data_per_search_data_row_template );
		}
		
		
		if ( _handlebarsTemplate_peptide_data_per_search_child_row_template === null ) {

			var handlebarsSource_peptide_data_per_search_child_row_template = $( "#peptide_data_per_search_child_row_template" ).html();

			if ( handlebarsSource_peptide_data_per_search_child_row_template === undefined ) {
				throw "handlebarsSource_peptide_data_per_search_child_row_template === undefined";
			}
			if ( handlebarsSource_peptide_data_per_search_child_row_template === null ) {
				throw "handlebarsSource_peptide_data_per_search_child_row_template === null";
			}
			
			_handlebarsTemplate_peptide_data_per_search_child_row_template = Handlebars.compile( handlebarsSource_peptide_data_per_search_child_row_template );
		}

		

		//  Search for qvalue being set in any row

		var anyLinksHavePeptideQValue = false;
		var anyLinksHavePeptidePEPValue = false;
		var anyLinksHavePeptideSVMValue = false;
		
		
		for ( var peptideIndex = 0; peptideIndex < reported_peptides.length ; peptideIndex++ ) {
			
			var peptide = reported_peptides[ peptideIndex ];
			
			if ( peptide.peptideQValue !== undefined && peptide.peptideQValue !== null ) {
				
				anyLinksHavePeptideQValue = true;
				break;
			}
		}

		for ( var peptideIndex = 0; peptideIndex < reported_peptides.length ; peptideIndex++ ) {
			
			var peptide = reported_peptides[ peptideIndex ];
		
			if ( peptide.peptidePEP !== undefined && peptide.peptidePEP !== null ) {
				
				anyLinksHavePeptidePEPValue = true;
				break;
			}
		}
		
		for ( var peptideIndex = 0; peptideIndex < reported_peptides.length ; peptideIndex++ ) {
			
			var peptide = reported_peptides[ peptideIndex ];
			
			if ( peptide.peptideSVMScore !== undefined && peptide.peptideSVMScore !== null ) {
				
				anyLinksHavePeptideSVMValue = true;
				break;
			}
		}
		
		var nameWidthPercent = 40;  // percentage width of table for Search Name
		var reportedPeptideWidthPercent = 50;  // percentage width of table for Reported Peptide Sequence String
			
		//  Reduce name and rep peptide width for q value, pep and svm if they are displayed.
		//     those fields are hard coded at 10% table width
		
		if ( anyLinksHavePeptideQValue ) {
			
			nameWidthPercent -= 4;
			reportedPeptideWidthPercent -= 3;
		}

		if ( anyLinksHavePeptidePEPValue ) {
			
			nameWidthPercent -= 4;
			reportedPeptideWidthPercent -= 3;
		}

		if ( anyLinksHavePeptideSVMValue ) {
			
			nameWidthPercent -= 4;
			reportedPeptideWidthPercent -= 3;
		}

		//  create context for header row
		var context = { 
				pageFormatting : {
					nameWidthPercent : nameWidthPercent,
					reportedPeptideWidthPercent : reportedPeptideWidthPercent
				},
				anyLinksHavePeptideQValue : anyLinksHavePeptideQValue, 
				anyLinksHavePeptidePEPValue : anyLinksHavePeptidePEPValue,
				anyLinksHavePeptideSVMValue : anyLinksHavePeptideSVMValue
		};
		

		var html = _handlebarsTemplate_peptide_data_per_search_block_template(context);

		var $peptide_data_per_search_block_template = $(html).appendTo($data_container);
		

		var peptide_table_jq_ClassName = "peptide_table_jq";
		
		var $peptide_table_jq = $peptide_data_per_search_block_template.find("." + peptide_table_jq_ClassName );
	
		if ( $peptide_table_jq.length === 0 ) {
			
			throw "unable to find HTML element with class '" + peptide_table_jq_ClassName + "'";
		}
		

		
		//  Add peptide data to the page
	
		for ( var peptideIndex = 0; peptideIndex < reported_peptides.length ; peptideIndex++ ) {
	
			var peptide = reported_peptides[ peptideIndex ];
			
			//  wrap data in an object to allow adding more fields
			var context = { data : peptide, 
					anyLinksHavePeptideQValue : anyLinksHavePeptideQValue, 
					anyLinksHavePeptidePEPValue : anyLinksHavePeptidePEPValue,
					anyLinksHavePeptideSVMValue : anyLinksHavePeptideSVMValue,
					psm_q_value_cutoff : psm_q_value_cutoff, project_id : project_id 
			};
	
			var html = _handlebarsTemplate_peptide_data_per_search_data_row_template(context);
	
			var $peptide_entry = 
				$(html).appendTo($peptide_table_jq);
			
			
			//  Get the number of columns of the inserted row so can set the "colspan=" in the next row
			//       that holds the child data
			
			var $peptide_entry__columns = $peptide_entry.find("td");
			
			var peptide_entry__numColumns = $peptide_entry__columns.length;
			
			//  colSpan is used as the value for "colspan=" in the <td>
			var childRowHTML_Context = { colSpan : peptide_entry__numColumns };
			
			var childRowHTML = _handlebarsTemplate_peptide_data_per_search_child_row_template( childRowHTML_Context );
			
			//  Add next row for child data
			$( childRowHTML ).appendTo($peptide_table_jq);
		}
		

		
	};
	

};


//Static Singleton Instance of Class

var viewMergedPeptidePerSearchDataFromWebServiceTemplate = new ViewMergedPeptidePerSearchDataFromWebServiceTemplate();
