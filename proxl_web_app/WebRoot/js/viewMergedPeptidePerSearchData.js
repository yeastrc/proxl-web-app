
//  viewMergedPeptidePerSearchData.js

//  Retrieve and display the Per Search data which is the Reported peptide for the search



//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";


//   Class contructor

var ViewMergedPeptidePerSearchDataFromWebServiceTemplate = function() {


	var _DATA_LOADED_DATA_KEY = "dataLoaded";

	var _data_per_search_between_searches_html = null;

	var _handlebarsTemplate_peptide_data_per_search_block_template = null;
	var _handlebarsTemplate_peptide_data_per_search_data_row_template = null;
	var _handlebarsTemplate_peptide_data_per_search_child_row_template = null;
	
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
	
	
	
	//////////////

	//   Called by "onclick" on HTML element
	
	this.showHideReportedPeptidesPerSearch = function( params ) {

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

				this.loadAndInsertReportedPeptidesPerSearchIfNeeded( { $topTRelement : $itemToToggle, $clickedElement : $clickedElement } );
			}
			return false;  // does not stop bubbling of click event
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
	
	////////////////////////
	this.loadAndInsertReportedPeptidesPerSearchIfNeeded = function( params ) {

		var objectThis = this;
		
		var $topTRelement = params.$topTRelement;
		var $clickedElement = params.$clickedElement;
		
		var dataLoaded = $topTRelement.data( _DATA_LOADED_DATA_KEY );
		
		if ( dataLoaded ) {
			
			return;  //  EARLY EXIT  since data already loaded. 
		}
		

		
//		var project_id = $( "#project_id_for_js" ).val();

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

		if ( ! unified_reported_peptide_id ) {
			unified_reported_peptide_id = "";
		}
		


		//   Currently expect _psmPeptideCriteria = 
//						searches: Object
//							128: Object			
//								peptideCutoffValues: Object
//									238: Object
//										id: 238
//										value: "0.01"
//								psmCutoffValues: Object
//									384: Object
//										id: 384
//										value: "0.01"
//								searchId: 128
		
//	           The key to:
//					searches - searchId
//					peptideCutoffValues and psmCutoffValues - annotation type id
		
//				peptideCutoffValues.id and psmCutoffValues.id - annotation type id
		

		var psmPeptideCutoffsForSearchIds_JSONString = JSON.stringify( _psmPeptideCutoffsRootObject );

		
		var annTypeDisplay_JSONString = null;
		
		if ( _psmPeptideAnnTypeIdDisplay ) {
			annTypeDisplay_JSONString = JSON.stringify( _psmPeptideAnnTypeIdDisplay );
		}
				
		var ajaxRequestData = {
				search_ids : search_ids,
				unified_reported_peptide_id : unified_reported_peptide_id,
				psmPeptideCutoffsForSearchIds : psmPeptideCutoffsForSearchIds_JSONString,
				annTypeDisplay : annTypeDisplay_JSONString
		};
		
		$.ajax({
			url : contextPathJSVar + "/services/data/getReportedPeptidesForUnifiedPeptId",

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
							$topTRelement : $topTRelement
					};

					objectThis.loadAndInsertReportedPeptidesPerSearchResponse( responseParams );


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
	
	
	this.loadAndInsertReportedPeptidesPerSearchResponse = function( params ) {
		
		var ajaxResponseData = params.ajaxResponseData;
		
//		var ajaxRequestData = params.ajaxRequestData;
		

		var reportedPeptidesPerSearchResponse = ajaxResponseData;
		
		
		
//		var project_id = $( "#project_id_for_js" ).val();

		//  Convert all attributes to empty string if null or undefined

		
		
		var $topTRelement = params.$topTRelement;
		
		var $data_container = $topTRelement.find(".child_data_container_jq");
		
		if ( $data_container.length === 0 ) {
			
			throw "unable to find HTML element with class 'child_data_container_jq'";
		}

		$data_container.empty();
		
		
		
		if ( _data_per_search_between_searches_html === null ) {
			
			_data_per_search_between_searches_html = $( "#data_per_search_between_searches_html" ).html();
			
			if ( _data_per_search_between_searches_html === undefined ) {
				throw "_data_per_search_between_searches_html === undefined";
			}
			if ( _data_per_search_between_searches_html === null ) {
				throw "_data_per_search_between_searches_html === null";
			}

		}
		
		
		
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

		////////////////////////////
		
		
		///////   Process Per Search Id:
		
		

		var reportedPeptidesPerSearch =  reportedPeptidesPerSearchResponse.reportedPeptidesPerSearchIdMap;

		var searchIdArray = Object.keys( reportedPeptidesPerSearch );
		
		//  Sort the search ids in ascending order
		searchIdArray.sort(function compareNumbers(a, b) {
			  return a - b;
		});
		
		
		
		for ( var searchIdIndex = 0; searchIdIndex < searchIdArray.length; searchIdIndex++ ) {
			
			
			//  If after the first search, insert the separator
			
			if ( searchIdIndex > 0 ) {
				
//				var $peptide_data_per_search_between_searches_html = 
				$( _data_per_search_between_searches_html ).appendTo($data_container);
			}
			
			
			
			var searchId = searchIdArray[ searchIdIndex ];
			
			var reportedPeptidesPerSearchEntry = reportedPeptidesPerSearch[ searchId ];

			var reportedPeptidesForSearch =  reportedPeptidesPerSearchEntry.reportedPepides;
		
		
			
			//  create context for header row
			var context = { 
					
					peptideAnnotationDisplayNameDescriptionList : reportedPeptidesPerSearchEntry.peptideAnnotationDisplayNameDescriptionList,
					psmAnnotationDisplayNameDescriptionList : reportedPeptidesPerSearchEntry.psmAnnotationDisplayNameDescriptionList
			};

		

			var html = _handlebarsTemplate_peptide_data_per_search_block_template(context);

			var $peptide_data_per_search_block_template = $(html).appendTo($data_container);


			var peptide_table_jq_ClassName = "peptide_table_jq";

			var $peptide_table_jq = $peptide_data_per_search_block_template.find("." + peptide_table_jq_ClassName );

			if ( $peptide_table_jq.length === 0 ) {

				throw "unable to find HTML element with class '" + peptide_table_jq_ClassName + "'";
			}


			
			

			//  Add peptide data to the page

			for ( var peptideIndex = 0; peptideIndex < reportedPeptidesForSearch.length ; peptideIndex++ ) {

				var peptide = reportedPeptidesForSearch[ peptideIndex ];

				//  wrap data in an object to allow adding more fields
				var context = { data : peptide
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


		}  // END:  For Each Search Id
	};


};


//Static Singleton Instance of Class

var viewMergedPeptidePerSearchDataFromWebServiceTemplate = new ViewMergedPeptidePerSearchDataFromWebServiceTemplate();
