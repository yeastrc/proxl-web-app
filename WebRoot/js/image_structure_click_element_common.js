
//  image_structure_click_element_common.js


/////    Click handling common code for Image and Structure pages


//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";




//////////

//   Globals




var _data_per_search_between_searches_html = null;




//////////////   Process LOOP link


/////////////////////

function getLooplinkDataCommon( params ) {
	

	var psmPeptideCutoffsRootObject = params.psmPeptideCutoffsRootObject;
	

	var requestContext = params.context;
	


	///   Serialize cutoffs to JSON
	
	var psmPeptideCutoffsRootObject_JSONString = JSON.stringify( psmPeptideCutoffsRootObject );

	var ajaxRequestData = {

			search_ids : requestContext.searchesArray,
			psmPeptideCutoffsForSearchIds : psmPeptideCutoffsRootObject_JSONString,
			
			protein_id : requestContext.from_protein_id,
			protein_position_1 : requestContext.protein_position_1,
			protein_position_2 : requestContext.protein_position_2
	};
	


	var _URL = contextPathJSVar + "/services/data/getLooplinkProteinsPerSearchIdsProteinIdsPositions";

	$.ajax({
		type: "GET",
		url: _URL,
		dataType: "json",
		data: ajaxRequestData,  //  The data sent as params on the URL

		traditional: true,  //  Force traditional serialization of the data sent
							//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
							//   So searchIds array is passed as "searchIds=<value>" which is what Jersey expects
		
		success: function(data)	{


			decrementSpinner();

			getLooplinkDataCommonProcessResponse( { ajaxResponseData: data, ajaxRequestData: ajaxRequestData, requestContext : requestContext });

		},
        failure: function(errMsg) {
        	handleAJAXFailure( errMsg );
        },
        error: function(jqXHR, textStatus, errorThrown) {	

			handleAJAXError( jqXHR, textStatus, errorThrown );
		}
	});
	
	
	
}


///////////////////////////

function getLooplinkDataCommonProcessResponse( params ) {

	var ajaxResponseData = params.ajaxResponseData;

	var requestContext = params.requestContext;



	var proteinsPerSearchIdMap = ajaxResponseData.proteinsPerSearchIdMap;


	openLinkInfoOverlayBackground();



	$("#loop_link_general_data").show();
	$("#cross_link_general_data").hide();
	$("#mono_link_general_data").hide();
	


	if ( requestContext.fcnToCallOnDisplay ) {
		
		requestContext.fcnToCallOnDisplay();
	}
	

	var $from_protein_id = $("#loop_link_from_protein_id");

	$from_protein_id.text( requestContext.from_protein_id );

	var $from_protein_name = $("#loop_link_from_protein_name");

	$from_protein_name.text( requestContext.from_protein_name );

	var $from_protein_position = $("#loop_link_from_protein_position");
	var $to_protein_position = $("#loop_link_to_protein_position");

	$from_protein_position.text( requestContext.protein_position_1 );
	$to_protein_position.text( requestContext.protein_position_2 );


//	var $loop_link_protein_info = $("#loop_link_protein_info");

//	var loop_link_protein_info = $loop_link_protein_info.text();



	var handlebarsSource_protein_data_per_search_block_template = $( "#protein_data_per_search_block_template" ).html(); 

	if ( handlebarsSource_protein_data_per_search_block_template === undefined ) {
		throw "handlebarsSource_protein_data_per_search_block_template === undefined";
	}
	if ( handlebarsSource_protein_data_per_search_block_template === null ) {
		throw "handlebarsSource_protein_data_per_search_block_template === null";
	}

	var handlebarsTemplate_protein_data_per_search_block_template = Handlebars.compile( handlebarsSource_protein_data_per_search_block_template );


	var handlebarsSource_protein_data_per_search_data_row_entry_template = $( "#protein_data_per_search_data_row_entry_template" ).html(); 

	if ( handlebarsSource_protein_data_per_search_data_row_entry_template === undefined ) {
		throw "handlebarsSource_protein_data_per_search_data_row_entry_template === undefined";
	}
	if ( handlebarsSource_protein_data_per_search_data_row_entry_template === null ) {
		throw "handlebarsSource_protein_data_per_search_data_row_entry_template === null";
	}

	var handlebarsTemplate_protein_data_per_search_data_row_entry_template = Handlebars.compile( handlebarsSource_protein_data_per_search_data_row_entry_template );


	var handlebarsSource_protein_data_per_search_child_row_entry_template = $( "#protein_data_per_search_child_row_entry_template" ).html(); 

	if ( handlebarsSource_protein_data_per_search_child_row_entry_template === undefined ) {
		throw "handlebarsSource_protein_data_per_search_child_row_entry_template === undefined";
	}
	if ( handlebarsSource_protein_data_per_search_child_row_entry_template === null ) {
		throw "handlebarsSource_protein_data_per_search_child_row_entry_template === null";
	}

	var handlebarsTemplate_protein_data_per_search_child_row_entry_template = Handlebars.compile( handlebarsSource_protein_data_per_search_child_row_entry_template );





	var $link_data_table_place_holder = $("#link_data_table_place_holder");

	$link_data_table_place_holder.empty();




///////   Process Per Search Id:

	var searchIdArray = Object.keys( proteinsPerSearchIdMap );

//	Sort the search ids in ascending order
	searchIdArray.sort(function compareNumbers(a, b) {
		return a - b;
	});

	for ( var searchIdIndex = 0; searchIdIndex < searchIdArray.length; searchIdIndex++ ) {


//		If after the first search, insert the separator

		if ( searchIdIndex > 0 ) {

			var $data_per_search_between_searches_htmlEntry =
				$( _data_per_search_between_searches_html ).appendTo( $link_data_table_place_holder );

			$data_per_search_between_searches_htmlEntry.show();
		}



		var searchId = searchIdArray[ searchIdIndex ];

		var proteinsForSearchWithAnnotationNameDescList = proteinsPerSearchIdMap[ searchId ];


		var peptideAnnotationDisplayNameDescriptionList =  proteinsForSearchWithAnnotationNameDescList.peptideAnnotationDisplayNameDescriptionList;
		var psmAnnotationDisplayNameDescriptionList =  proteinsForSearchWithAnnotationNameDescList.psmAnnotationDisplayNameDescriptionList;


		var proteinsForSearch =  proteinsForSearchWithAnnotationNameDescList.proteins;




//		create context for header row
		var dataBlockContext = { 

				peptideAnnotationDisplayNameDescriptionList : peptideAnnotationDisplayNameDescriptionList,
				psmAnnotationDisplayNameDescriptionList : psmAnnotationDisplayNameDescriptionList
		};


		var protein_data_per_search_block = handlebarsTemplate_protein_data_per_search_block_template( dataBlockContext );


		var $protein_data_per_search_block = $( protein_data_per_search_block ).appendTo( $link_data_table_place_holder );


		if ( _data_per_search_between_searches_html === null ) {

			_data_per_search_between_searches_html = $protein_data_per_search_block.find( ".data_per_search_between_searches_html_jq" ).html();

			if ( _data_per_search_between_searches_html === undefined ) {
				throw "data_per_search_between_searches_html_jq === undefined";
			}
			if ( _data_per_search_between_searches_html === null ) {
				throw "data_per_search_between_searches_html_jq === null";
			}

		}





		var link_info_table_jq_ClassName = "link_info_table_jq";

		var $link_info_table_jq = $protein_data_per_search_block.find("." + link_info_table_jq_ClassName );

//		var $link_info_table_jq = $crosslink_protein_data_container.find(".link_info_table_jq");

		if ( $link_info_table_jq.length === 0 ) {

			throw "unable to find HTML element with class '" + link_info_table_jq_ClassName + "'";
		}



//		Add protein data to the page

		for ( var proteinIndex = 0; proteinIndex < proteinsForSearch.length ; proteinIndex++ ) {


			var proteinData = proteinsForSearch[ proteinIndex ];

			var dataRowContext = { 
					data : proteinData, 
					searchId : searchId,
					from_protein_id : requestContext.from_protein_id,
					from_protein_position : requestContext.protein_position_1,
					to_protein_position : requestContext.protein_position_2,

					isLooplink : true 
			};


			var html = handlebarsTemplate_protein_data_per_search_data_row_entry_template( dataRowContext );

			var $search_entry_data_row = $( html ).appendTo( $link_info_table_jq );


			var $search_entry_data_row__columns = $search_entry_data_row.find("td");

			var search_entry_data_row__columns = $search_entry_data_row__columns.length;



			var contextChildRow = { 
					colSpan : search_entry_data_row__columns
			};


			var htmlChildRow = handlebarsTemplate_protein_data_per_search_child_row_entry_template( contextChildRow );

//			var $search_entry_child_row = 
			$( htmlChildRow ).appendTo( $link_info_table_jq );


//			If only one record, click on it to show it's children

			if ( searchIdArray.length === 1 ) {

				$search_entry_data_row.click();
			}

		}  // END:  For Each Search Id

	}


	linkInfoOverlayWidthResizer();

}




/////////////////////////////////

//////////////Process CROSS link




/////////////////////

function getCrosslinkDataCommon( params ) {

	var psmPeptideCutoffsRootObject = params.psmPeptideCutoffsRootObject;

	var requestContext = params.context;

	///   Serialize cutoffs to JSON

	var psmPeptideCutoffsRootObject_JSONString = JSON.stringify( psmPeptideCutoffsRootObject );


	var _URL = contextPathJSVar + "/services/data/getCrosslinkProteinsPerSearchIdsProteinIdsPositions";

	var ajaxRequestData = {

			psmPeptideCutoffsForSearchIds : psmPeptideCutoffsRootObject_JSONString,

			search_ids: requestContext.searchesArray,
			protein_1_id: requestContext.protein1.protein_id_int,
			protein_2_id: requestContext.protein2.protein_id_int,
			protein_1_position: requestContext.protein1.protein_position,
			protein_2_position: requestContext.protein2.protein_position
	};



	$.ajax({
		type: "GET",
		url: _URL,
		dataType: "json",
		data: ajaxRequestData,  //  The data sent as params on the URL

		traditional: true,  //  Force traditional serialization of the data sent
		//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
		//   So searchIds array is passed as "searchIds=<value>" which is what Jersey expects

		success: function(data)	{


			decrementSpinner();

			var getCrosslinkDataCommonProcessResponseParams = 
			{ 
					ajaxResponseData: data, 
					ajaxRequestData: ajaxRequestData,
					requestContext: requestContext
			};

			getCrosslinkDataCommonProcessResponse( getCrosslinkDataCommonProcessResponseParams );

		},
		failure: function(errMsg) {
			handleAJAXFailure( errMsg );
		},
		error: function(jqXHR, textStatus, errorThrown) {	

			handleAJAXError( jqXHR, textStatus, errorThrown );
		}
	});

}



function getCrosslinkDataCommonProcessResponse( params ) {

	var ajaxResponseData = params.ajaxResponseData;

//	var ajaxRequestData = params.ajaxRequestData;

	var requestContext = params.requestContext;


	var proteinsPerSearchIdMap = ajaxResponseData.proteinsPerSearchIdMap;



	openLinkInfoOverlayBackground();




	$("#cross_link_general_data").show();
	$("#loop_link_general_data").hide();
	$("#mono_link_general_data").hide();



	if ( requestContext.fcnToCallOnDisplay ) {
		
		requestContext.fcnToCallOnDisplay();
	}
	
	

	var $from_protein_id = $("#cross_link_from_protein_id");
	var $to_protein_id = $("#cross_link_to_protein_id");

	$from_protein_id.text( requestContext.protein1.protein_id_int );
	$to_protein_id.text( requestContext.protein2.protein_id_int );

	var $cross_link_protein_name_1 = $("#cross_link_protein_name_1");
	var $cross_link_protein_name_2 = $("#cross_link_protein_name_2");

	$cross_link_protein_name_1.text( requestContext.protein1.protein_name );
	$cross_link_protein_name_2.text( requestContext.protein2.protein_name );

	var $cross_link_protein_position_1 = $("#cross_link_protein_position_1");
	var $cross_link_protein_position_2 = $("#cross_link_protein_position_2");

	$cross_link_protein_position_1.text( requestContext.protein1.protein_position );
	$cross_link_protein_position_2.text( requestContext.protein2.protein_position );


	var handlebarsSource_protein_data_per_search_block_template = $( "#protein_data_per_search_block_template" ).html(); 

	if ( handlebarsSource_protein_data_per_search_block_template === undefined ) {
		throw "handlebarsSource_protein_data_per_search_block_template === undefined";
	}
	if ( handlebarsSource_protein_data_per_search_block_template === null ) {
		throw "handlebarsSource_protein_data_per_search_block_template === null";
	}

	var handlebarsTemplate_protein_data_per_search_block_template = Handlebars.compile( handlebarsSource_protein_data_per_search_block_template );


	var handlebarsSource_protein_data_per_search_data_row_entry_template = $( "#protein_data_per_search_data_row_entry_template" ).html(); 

	if ( handlebarsSource_protein_data_per_search_data_row_entry_template === undefined ) {
		throw "handlebarsSource_protein_data_per_search_data_row_entry_template === undefined";
	}
	if ( handlebarsSource_protein_data_per_search_data_row_entry_template === null ) {
		throw "handlebarsSource_protein_data_per_search_data_row_entry_template === null";
	}

	var handlebarsTemplate_protein_data_per_search_data_row_entry_template = Handlebars.compile( handlebarsSource_protein_data_per_search_data_row_entry_template );


	var handlebarsSource_protein_data_per_search_child_row_entry_template = $( "#protein_data_per_search_child_row_entry_template" ).html(); 

	if ( handlebarsSource_protein_data_per_search_child_row_entry_template === undefined ) {
		throw "handlebarsSource_protein_data_per_search_child_row_entry_template === undefined";
	}
	if ( handlebarsSource_protein_data_per_search_child_row_entry_template === null ) {
		throw "handlebarsSource_protein_data_per_search_child_row_entry_template === null";
	}

	var handlebarsTemplate_protein_data_per_search_child_row_entry_template = Handlebars.compile( handlebarsSource_protein_data_per_search_child_row_entry_template );



	////////////////////////////


	var $link_data_table_place_holder = $("#link_data_table_place_holder");

	$link_data_table_place_holder.empty();

	////////////////////////////


	///////   Process Per Search Id:

	var searchIdArray = Object.keys( proteinsPerSearchIdMap );

	//  Sort the search ids in ascending order
	searchIdArray.sort(function compareNumbers(a, b) {
		return a - b;
	});

	for ( var searchIdIndex = 0; searchIdIndex < searchIdArray.length; searchIdIndex++ ) {


		//  If after the first search, insert the separator

		if ( searchIdIndex > 0 ) {

			var $data_per_search_between_searches_htmlEntry =
				$( _data_per_search_between_searches_html ).appendTo( $link_data_table_place_holder );

			$data_per_search_between_searches_htmlEntry.show();
		}



		var searchId = searchIdArray[ searchIdIndex ];

		var proteinsForSearchWithAnnotationNameDescList = proteinsPerSearchIdMap[ searchId ];


		var peptideAnnotationDisplayNameDescriptionList =  proteinsForSearchWithAnnotationNameDescList.peptideAnnotationDisplayNameDescriptionList;
		var psmAnnotationDisplayNameDescriptionList =  proteinsForSearchWithAnnotationNameDescList.psmAnnotationDisplayNameDescriptionList;


		var proteinsForSearch =  proteinsForSearchWithAnnotationNameDescList.proteins;




		//  create context for header row
		var dataBlockContext = { 

				peptideAnnotationDisplayNameDescriptionList : peptideAnnotationDisplayNameDescriptionList,
				psmAnnotationDisplayNameDescriptionList : psmAnnotationDisplayNameDescriptionList
		};


		var protein_data_per_search_block = handlebarsTemplate_protein_data_per_search_block_template( dataBlockContext );


		var $protein_data_per_search_block = $( protein_data_per_search_block ).appendTo( $link_data_table_place_holder );


		if ( _data_per_search_between_searches_html === null ) {

			_data_per_search_between_searches_html = $protein_data_per_search_block.find( ".data_per_search_between_searches_html_jq" ).html();

			if ( _data_per_search_between_searches_html === undefined ) {
				throw "data_per_search_between_searches_html_jq === undefined";
			}
			if ( _data_per_search_between_searches_html === null ) {
				throw "data_per_search_between_searches_html_jq === null";
			}

		}


		////////////////////////////


		var link_info_table_jq_ClassName = "link_info_table_jq";

		var $link_info_table_jq = $protein_data_per_search_block.find("." + link_info_table_jq_ClassName );

		//			var $link_info_table_jq = $crosslink_protein_data_container.find(".link_info_table_jq");

		if ( $link_info_table_jq.length === 0 ) {

			throw "unable to find HTML element with class '" + link_info_table_jq_ClassName + "'";
		}



		//  Add protein data to the page

		for ( var proteinIndex = 0; proteinIndex < proteinsForSearch.length ; proteinIndex++ ) {


			var proteinData = proteinsForSearch[ proteinIndex ];

			var dataRowContext = { 
					data : proteinData, 
					searchId : searchId,
					from_protein_id : requestContext.protein1.protein_id_int,
					to_protein_id : requestContext.protein2.protein_id_int,
					from_protein_position : requestContext.protein1.protein_position,
					to_protein_position : requestContext.protein2.protein_position,
					isCrosslink : true 
			};


			var html = handlebarsTemplate_protein_data_per_search_data_row_entry_template( dataRowContext );

			var $search_entry_data_row = $(html).appendTo( $link_info_table_jq );


			var $search_entry_data_row__columns = $search_entry_data_row.find("td");

			var search_entry_data_row__columns = $search_entry_data_row__columns.length;



			var contextChildRow = { 
					colSpan : search_entry_data_row__columns
			};


			var htmlChildRow = handlebarsTemplate_protein_data_per_search_child_row_entry_template( contextChildRow );

//			var $search_entry_child_row = 
			$( htmlChildRow ).appendTo( $link_info_table_jq );


			//  If only one record, click on it to show it's children

			if ( searchIdArray.length === 1 ) {

				$search_entry_data_row.click();
			}


		}  // END:  For Each Search Id

	}

	linkInfoOverlayWidthResizer();

}




//////////////Process Mono link


/////////////////////

function getMonolinkDataCommon( params ) {


	var psmPeptideCutoffsRootObject = params.psmPeptideCutoffsRootObject;


	var requestContext = params.context;



//	/   Serialize cutoffs to JSON

	var psmPeptideCutoffsRootObject_JSONString = JSON.stringify( psmPeptideCutoffsRootObject );


	var ajaxRequestData = {

			search_ids : requestContext.searchesArray,
			psmPeptideCutoffsForSearchIds : psmPeptideCutoffsRootObject_JSONString,

			protein_id : requestContext.from_protein_id,
			protein_position : requestContext.protein_position
	};


	var _URL = contextPathJSVar + "/services/data/getMonolinkProteinsPerSearchIdsProteinIdsPositions";

	$.ajax({
		type: "GET",
		url: _URL,
		dataType: "json",
		data: ajaxRequestData,  //  The data sent as params on the URL

		traditional: true,  //  Force traditional serialization of the data sent
		//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
		//   So searchIds array is passed as "searchIds=<value>" which is what Jersey expects

		success: function(data)	{


			decrementSpinner();

			getMonolinkDataCommonProcessResponse( { ajaxResponseData: data, ajaxRequestData: ajaxRequestData, requestContext : requestContext });

		},
		failure: function(errMsg) {
			handleAJAXFailure( errMsg );
		},
		error: function(jqXHR, textStatus, errorThrown) {	

			handleAJAXError( jqXHR, textStatus, errorThrown );
		}
	});



}



/////////////////////////

function getMonolinkDataCommonProcessResponse( params ) {

	var ajaxResponseData = params.ajaxResponseData;

	var requestContext = params.requestContext;
	


	var proteinsPerSearchIdMap = ajaxResponseData.proteinsPerSearchIdMap;


	openLinkInfoOverlayBackground();


	$("#mono_link_general_data").show();
	$("#loop_link_general_data").hide();
	$("#cross_link_general_data").hide();



	if ( requestContext.fcnToCallOnDisplay ) {
		
		requestContext.fcnToCallOnDisplay();
	}
	

	var $from_protein_id = $("#mono_link_from_protein_id");

	$from_protein_id.text( requestContext.from_protein_id );

	var $from_protein_name = $("#mono_link_from_protein_name");

	$from_protein_name.text( requestContext.from_protein_name );

	var $protein_position = $("#mono_link_protein_position");

	$protein_position.text( requestContext.protein_position );

	var $searches = $("#searches");

	$searches.text( requestContext.searchIdsCommaDelimString );


//	var $mono_link_protein_info = $("#mono_link_protein_info");
//
//	var mono_link_protein_info = $mono_link_protein_info.text();



	var handlebarsSource_protein_data_per_search_block_template = $( "#protein_data_per_search_block_template" ).html(); 

	if ( handlebarsSource_protein_data_per_search_block_template === undefined ) {
		throw "handlebarsSource_protein_data_per_search_block_template === undefined";
	}
	if ( handlebarsSource_protein_data_per_search_block_template === null ) {
		throw "handlebarsSource_protein_data_per_search_block_template === null";
	}
	
	var handlebarsTemplate_protein_data_per_search_block_template = Handlebars.compile( handlebarsSource_protein_data_per_search_block_template );

	
	var handlebarsSource_protein_data_per_search_data_row_entry_template = $( "#protein_data_per_search_data_row_entry_template" ).html(); 

	if ( handlebarsSource_protein_data_per_search_data_row_entry_template === undefined ) {
		throw "handlebarsSource_protein_data_per_search_data_row_entry_template === undefined";
	}
	if ( handlebarsSource_protein_data_per_search_data_row_entry_template === null ) {
		throw "handlebarsSource_protein_data_per_search_data_row_entry_template === null";
	}
	
	var handlebarsTemplate_protein_data_per_search_data_row_entry_template = Handlebars.compile( handlebarsSource_protein_data_per_search_data_row_entry_template );
	
	
	var handlebarsSource_protein_data_per_search_child_row_entry_template = $( "#protein_data_per_search_child_row_entry_template" ).html(); 

	if ( handlebarsSource_protein_data_per_search_child_row_entry_template === undefined ) {
		throw "handlebarsSource_protein_data_per_search_child_row_entry_template === undefined";
	}
	if ( handlebarsSource_protein_data_per_search_child_row_entry_template === null ) {
		throw "handlebarsSource_protein_data_per_search_child_row_entry_template === null";
	}
	
	var handlebarsTemplate_protein_data_per_search_child_row_entry_template = Handlebars.compile( handlebarsSource_protein_data_per_search_child_row_entry_template );


	////////////////////////////
	

	var $link_data_table_place_holder = $("#link_data_table_place_holder");

	$link_data_table_place_holder.empty();

	////////////////////////////
	
	
	///////   Process Per Search Id:
	
	var searchIdArray = Object.keys( proteinsPerSearchIdMap );
	
	//  Sort the search ids in ascending order
	searchIdArray.sort(function compareNumbers(a, b) {
		  return a - b;
	});

	for ( var searchIdIndex = 0; searchIdIndex < searchIdArray.length; searchIdIndex++ ) {
		
		
		//  If after the first search, insert the separator
		
		if ( searchIdIndex > 0 ) {
			
			var $data_per_search_between_searches_htmlEntry =
				$( _data_per_search_between_searches_html ).appendTo( $link_data_table_place_holder );
			
			$data_per_search_between_searches_htmlEntry.show();
		}
		
		
		
		var searchId = searchIdArray[ searchIdIndex ];
		
		var proteinsForSearchWithAnnotationNameDescList = proteinsPerSearchIdMap[ searchId ];

		
		var peptideAnnotationDisplayNameDescriptionList =  proteinsForSearchWithAnnotationNameDescList.peptideAnnotationDisplayNameDescriptionList;
		var psmAnnotationDisplayNameDescriptionList =  proteinsForSearchWithAnnotationNameDescList.psmAnnotationDisplayNameDescriptionList;
		

		var proteinsForSearch =  proteinsForSearchWithAnnotationNameDescList.proteins;
	
	

		
		//  create context for header row
		var dataBlockContext = { 
				
				peptideAnnotationDisplayNameDescriptionList : peptideAnnotationDisplayNameDescriptionList,
				psmAnnotationDisplayNameDescriptionList : psmAnnotationDisplayNameDescriptionList
		};


		var protein_data_per_search_block = handlebarsTemplate_protein_data_per_search_block_template( dataBlockContext );


		var $protein_data_per_search_block = $( protein_data_per_search_block ).appendTo( $link_data_table_place_holder );


		if ( _data_per_search_between_searches_html === null ) {
			
			_data_per_search_between_searches_html = $protein_data_per_search_block.find( ".data_per_search_between_searches_html_jq" ).html();
			
			if ( _data_per_search_between_searches_html === undefined ) {
				throw "data_per_search_between_searches_html_jq === undefined";
			}
			if ( _data_per_search_between_searches_html === null ) {
				throw "data_per_search_between_searches_html_jq === null";
			}

		}
		
		
		////////////////////////////
		
		
		var link_info_table_jq_ClassName = "link_info_table_jq";

		var $link_info_table_jq = $protein_data_per_search_block.find("." + link_info_table_jq_ClassName );

		//			var $link_info_table_jq = $crosslink_protein_data_container.find(".link_info_table_jq");

		if ( $link_info_table_jq.length === 0 ) {

			throw "unable to find HTML element with class '" + link_info_table_jq_ClassName + "'";
		}



		//  Add protein data to the page

		for ( var proteinIndex = 0; proteinIndex < proteinsForSearch.length ; proteinIndex++ ) {

		
			var proteinData = proteinsForSearch[ proteinIndex ];

			var dataRowContext = { 
					data : proteinData, 
					searchId : searchId,
					from_protein_id : requestContext.from_protein_id,
					from_protein_position : requestContext.protein_position,

					isMonolink : true 
			};


			var html = handlebarsTemplate_protein_data_per_search_data_row_entry_template( dataRowContext );

			var $search_entry_data_row = $( html ).appendTo( $link_info_table_jq );

			var $search_entry_data_row__columns = $search_entry_data_row.find("td");

			var search_entry_data_row__columns = $search_entry_data_row__columns.length;



			var contextChildRow = { 
					colSpan : search_entry_data_row__columns
			};


			var htmlChildRow = handlebarsTemplate_protein_data_per_search_child_row_entry_template( contextChildRow );

//			var $search_entry_child_row = 
			$( htmlChildRow ).appendTo( $link_info_table_jq );


			//  If only one record, click on it to show it's children

			if ( searchIdArray.length === 1 ) {

				$search_entry_data_row.click();
			}

		}  // END:  For Each Search Id
		
	}


	linkInfoOverlayWidthResizer();
}







///////////////////////////////////////////////////////////////////////////


///  Overlay general processing

var openLinkInfoOverlayBackground = function (  ) {


//	Close any open Lorikeet Overlay
	closeLorikeetOverlay();

	var $view_link_info_modal_dialog_overlay_background = $("#view_link_info_modal_dialog_overlay_background");
	var $view_link_info_overlay_div = $("#view_link_info_overlay_div");


//	Adjust the overlay positon to be within the viewport

	var scrollTopWindow = $(window).scrollTop();

	if ( scrollTopWindow > 0 ) {

//		User has scrolled down

		var overlayTop = scrollTopWindow + 10;

		$view_link_info_overlay_div.css( { top: overlayTop + "px" } );

	} else {

		$view_link_info_overlay_div.css( { top: "10px" } );
	}

	$view_link_info_modal_dialog_overlay_background.show();
	$view_link_info_overlay_div.show();

};

/////////////////////

///  Overlay general processing

var closeViewLinkInfoOverlay = function (  ) {

	$("#view_link_info_modal_dialog_overlay_background").hide();
	$(".view-link-info-overlay-div").hide();

};


///////////////////////////////////////////////////////////////////////////

///  Attach Overlay Click handlers


var attachViewLinkInfoOverlayClickHandlers = function (  ) {

	$(".view-link-info-overlay-X-for-exit-overlay").click( function( eventObject ) {

		closeViewLinkInfoOverlay();
	} );

	$("#view_link_info_modal_dialog_overlay_background").click( function( eventObject ) {

		closeViewLinkInfoOverlay();
	} );

	$(".error-message-ok-button").click( function( eventObject ) {

		closeViewLinkInfoOverlay();
	} );


};




///////////////////////////////////////////////////////////////////////////

///  Resize overlay


var linkInfoOverlayWidthResizer = function() {
	
	var $link_info_table = $("#link_info_table__tbody");

	var link_info_table_width = $link_info_table.outerWidth( true /* [ includeMargin ] */ );

	//  Adjust width of link info overlay to be 40 pixels wider than the link info table
	
	var view_link_info_overlay_div = link_info_table_width + 40;

	$("#view_link_info_overlay_div").css( {"width": view_link_info_overlay_div } );
};

