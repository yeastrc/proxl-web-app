
//  crosslink-image-viewer-click-element-handlers.js



///////////////////////////////////////////////////////////////////////////

////////////    Click handlers for the links ( Lines that show the links )




var linkInfoOverlayWidthResizer = function() {
	
	var $link_info_table = $("#link_info_table__tbody");

//	var link_info_table_innerWidth = $link_info_table.width( );
//
//	var link_info_table_outerWidth_NoMargin = $link_info_table.outerWidth( false /* [ includeMargin ] */ );

	var link_info_table_width = $link_info_table.outerWidth( true /* [ includeMargin ] */ );

	//  Adjust width of link info overlay to be 40 pixels wider than the link info table
	
	var view_link_info_overlay_div = link_info_table_width + 40;

	$("#view_link_info_overlay_div").css( {"width": view_link_info_overlay_div } );
};


//////////////   Process LOOP link


/////////////////////

function getLooplinkDataForSpecificLinkInGraph( params, link ) {

	var psmQValueCutoff = params.psmQValueCutoff;
	var peptideQValueCutoff = params.peptideQValueCutoff;
	
	incrementSpinner();				// create spinner

	var fromPp = link.position1;
	var toPp = link.position2;
	var from_protein_id = link.protein1;

	var searchesArray = link.searchIds;
	
	var _URL = contextPathJSVar + "/services/imageViewer/getLooplinkDataForSpecificLinkInGraph";

	var ajaxRequestData = {

			psmQValueCutoff : psmQValueCutoff,
			peptideQValueCutoff : peptideQValueCutoff,
			
			searchIds: searchesArray,
			proteinId: from_protein_id,
			proteinLinkPosition1: fromPp,
			proteinLinkPosition2: toPp
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

			getLooplinkDataForSpecificLinkInGraphProcessResponse( { ajaxResponseData: data, ajaxRequestData: ajaxRequestData }, link);

		},
        failure: function(errMsg) {
        	handleAJAXFailure( errMsg );
        },
		error: function(jqXHR, textStatus, errorThrown) {	

			handleAJAXError( jqXHR, textStatus, errorThrown );
		}
	});
	
	
}

////////////////////////

function getLooplinkDataForSpecificLinkInGraphProcessResponse( params, link ) {
	
	var ajaxResponseData = params.ajaxResponseData;
	
	openLinkInfoOverlayBackground();
	
	var fromP = _proteinNames[ link.protein1 ];
	var fromPp = link.position1;
	var toPp = link.position2;
	
	$("#loop_link_general_data").show();
	$("#cross_link_general_data").hide();
	$("#mono_link_general_data").hide();

	var from_protein_id = link.protein1;

	$from_protein_id = $("#loop_link_from_protein_id");

	$from_protein_id.text( from_protein_id );

	var $from_protein_name = $("#loop_link_from_protein_name");

	$from_protein_name.text( fromP );

	$from_protein_position = $("#loop_link_from_protein_position");
	$to_protein_position = $("#loop_link_to_protein_position");

	$from_protein_position.text( fromPp );
	$to_protein_position.text( toPp );

	$searches = $("#searches");

	$searches.text( "search text" );
	
	var $link_info_table = $("#link_info_table__tbody");
	
	$link_info_table.empty();
	
	var $loop_link_protein_info = $("#loop_link_protein_info");
	var loop_link_protein_info = $loop_link_protein_info.text();

	$length = $("#looplink_length");
	$length.text( Math.round( link.length * 10 ) / 10 );
	
	
	var handlebarsSource_search_entry_data_row_template = $( "#search_entry_data_row_template tbody" ).html(); //  " tbody" since it is a table

	if ( handlebarsSource_search_entry_data_row_template === undefined ) {
		throw "handlebarsSource_search_entry_data_row_template === undefined";
	}
	if ( handlebarsSource_search_entry_data_row_template === null ) {
		throw "handlebarsSource_search_entry_data_row_template === null";
	}
	
	var handlebarsTemplate_search_entry_data_row_template = Handlebars.compile( handlebarsSource_search_entry_data_row_template );
	
	
	var handlebarsSource_search_entry_child_row_template = $( "#search_entry_child_row_template tbody" ).html(); //  " tbody" since it is a table

	var handlebarsSource_looplink_peptide_block_template = $( "#looplink_peptide_block_template" ).html(); 

	
	var handlebarsSource_looplink_peptide_data_row_entry_template = $( "#looplink_peptide_data_row_entry_template tbody" ).html(); //  " tbody" since it is a table

	if ( handlebarsSource_looplink_peptide_data_row_entry_template === undefined ) {
		throw "handlebarsSource_looplink_peptide_data_row_entry_template === undefined";
	}
	if ( handlebarsSource_looplink_peptide_data_row_entry_template === null ) {
		throw "handlebarsSource_looplink_peptide_data_row_entry_template === null";
	}
	
	var handlebarsTemplate_looplink_peptide_data_row_entry_template = Handlebars.compile( handlebarsSource_looplink_peptide_data_row_entry_template );

	
	var handlebarsSource_looplink_peptide_child_row_entry_template = $( "#looplink_peptide_child_row_entry_template tbody" ).html(); //  " tbody" since it is a table

	
	var searchIdArray = Object.keys( ajaxResponseData );
	
	//  Sort the search ids in ascending order
	searchIdArray.sort(function compareNumbers(a, b) {
		  return a - b;
	});
	
	for ( var searchIdIndex = 0; searchIdIndex < searchIdArray.length; searchIdIndex++ ) {
		
		var searchId = searchIdArray[ searchIdIndex ];
		
		var searchProteinLooplink = ajaxResponseData[ searchId ];

		var context = searchProteinLooplink;
		

		var html = handlebarsTemplate_search_entry_data_row_template(context);

		var $search_entry_data_row = $(html).appendTo($link_info_table);
		
		var $search_entry_child_row = $( handlebarsSource_search_entry_child_row_template ).appendTo($link_info_table);
		
		if ( searchIdArray.length === 1 ) {
			
			var $toggle_visibility_expansion_span_jq = $search_entry_data_row.find(".toggle_visibility_expansion_span_jq");
			var $toggle_visibility_contraction_span_jq = $search_entry_data_row.find(".toggle_visibility_contraction_span_jq");

			$toggle_visibility_expansion_span_jq.hide();
			$toggle_visibility_contraction_span_jq.show();
			$search_entry_child_row.show();
		}
		
		var $peptide_data_container = $search_entry_child_row.find(".peptide_data_container");
		
		var $looplink_peptide_block_template = $(handlebarsSource_looplink_peptide_block_template).appendTo($peptide_data_container);

		//  This only works if $looplink_peptide_block_template has a single top level node in it
		var $peptide_table_jq =  $looplink_peptide_block_template.find(".peptide_table_jq");

		//  This accomplishes the same thing as the previous statement using a higher node in the HTML
//		var $peptide_table_jq =  $peptide_data_container.find(".peptide_table_jq");




		var peptides = searchProteinLooplink.peptides;

		for ( var peptideIndex = 0; peptideIndex < peptides.length ; peptideIndex++ ) {

			var peptide = peptides[ peptideIndex ];

			peptide.loop_link_protein_info = loop_link_protein_info;
			
			peptide.searchId = searchId;
			
//			var 
			context = peptide;
		
//			var    
			html = handlebarsTemplate_looplink_peptide_data_row_entry_template(context);

			var $peptide_entry_data_row = $( html ).appendTo($peptide_table_jq);
			
//			var $peptide_entry_child_row = 
			$( handlebarsSource_looplink_peptide_child_row_entry_template ).appendTo($peptide_table_jq);

			if ( peptides.length === 1 ) {
				
				//  There is only one peptide so show the PSMs for it.
				
				$peptide_entry_data_row.click();
			}
			

		}
	}
	
	var $openLorkeetLinks = $(".view_spectrum_open_spectrum_link_jq");
	
	addOpenLorikeetViewerClickHandlers( $openLorkeetLinks );

	linkInfoOverlayWidthResizer();
	
}




/////////////////////////////////

//////////////   Process CROSS link




/////////////////////

function getCrosslinkDataForSpecificLinkInGraph( params, link ) {

	var psmQValueCutoff = params.psmQValueCutoff;
	var peptideQValueCutoff = params.peptideQValueCutoff;


	incrementSpinner();				// create spinner

	var from_protein_name = _proteinNames[ link.protein1 ];
	var from_protein_position = link.position1;
	var to_protein_name = _proteinNames[ link.protein2 ];
	var to_protein_position = link.position2;

	var from_protein_id = link.protein1;
	var to_protein_id = link.protein2;

	var searchesArray = link.searchIds;
	
	var searchesCommaDelim = searchesArray.join( "," );
	
	
	var from_protein_id_int = parseInt( from_protein_id, 10 );
	if ( isNaN( from_protein_id_int ) ) {
		throw "from_protein_id is not an Integer, is: " + from_protein_id;
	}
	
	var to_protein_id_int = parseInt( to_protein_id, 10 );
	if ( isNaN( to_protein_id_int ) ) {
		throw "to_protein_id is not an Integer, is: " + to_protein_id;
	}
	
	var from_protein_position_int = parseInt( from_protein_position, 10 );
	if ( isNaN( from_protein_position_int ) ) {
		throw "from_protein_position is not an Integer, is: " + from_protein_position;
	}
	
	var to_protein_position_int = parseInt( to_protein_position, 10 );
	if ( isNaN( to_protein_position_int ) ) {
		throw "to_protein_position is not an Integer, is: " + to_protein_position;
	}
	
	var context = {

			protein1: {

				protein_id_int: from_protein_id_int,
				protein_id: from_protein_id,

				protein_position_int: from_protein_position_int,
				protein_position : from_protein_position,
				
				protein_name : from_protein_name
			},
			protein2: {

				protein_id_int: to_protein_id_int,
				protein_id: to_protein_id,

				protein_position_int : to_protein_position_int,
				protein_position : to_protein_position,

				protein_name : to_protein_name
			},

			searchesCommaDelim: searchesCommaDelim,
			searchesArray: searchesArray
	};
	
	
	if ( ( context.protein1.protein_id_int > context.protein2.protein_id_int )
			|| ( context.protein1.protein_id_int === context.protein2.protein_id_int 
					&& context.protein1.protein_position_int > context.protein2.protein_position_int ) ){
		
		//  Switch the proteins so proteinId1 < proteinId2
		//     and if proteinId1 == proteinId2, then protein_position1 < protein_position2
		
		var tempprotein = context.protein1;
		context.protein1 = context.protein2;
		context.protein2 = tempprotein;
	}
	

	var _URL = contextPathJSVar + "/services/imageViewer/getCrosslinkDataForSpecificLinkInGraph";

	var ajaxRequestData = {

			psmQValueCutoff : psmQValueCutoff,
			peptideQValueCutoff : peptideQValueCutoff,
			
			searchIds: context.searchesArray,
			proteinId1: context.protein1.protein_id_int,
			proteinId2: context.protein2.protein_id_int,
			proteinLinkPosition1: context.protein1.protein_position,
			proteinLinkPosition2: context.protein2.protein_position
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
			
			var getCrosslinkDataForSpecificLinkInGraphProcessResponseParams = 
				{ 
					ajaxResponseData: data, 
					ajaxRequestData: ajaxRequestData,
					context: context,
				};

			getCrosslinkDataForSpecificLinkInGraphProcessResponse( getCrosslinkDataForSpecificLinkInGraphProcessResponseParams, link );

		},
        failure: function(errMsg) {
        	handleAJAXFailure( errMsg );
        },
		error: function(jqXHR, textStatus, errorThrown) {	

			handleAJAXError( jqXHR, textStatus, errorThrown );
		}
	});


}



function getCrosslinkDataForSpecificLinkInGraphProcessResponse( params, link ) {

	var ajaxResponseData = params.ajaxResponseData;
	var responseContext = params.context;

	openLinkInfoOverlayBackground();
	
	$("#cross_link_general_data").show();
	$("#loop_link_general_data").hide();
	$("#mono_link_general_data").hide();



	$from_protein_id = $("#cross_link_from_protein_id");
	$to_protein_id = $("#cross_link_to_protein_id");

	$from_protein_id.text( responseContext.protein1.protein_id_int );
	$to_protein_id.text( responseContext.protein2.protein_id_int );

	var $cross_link_protein_name_1 = $("#cross_link_protein_name_1");
	var $cross_link_protein_name_2 = $("#cross_link_protein_name_2");

	$cross_link_protein_name_1.text( responseContext.protein1.protein_name );
	$cross_link_protein_name_2.text( responseContext.protein2.protein_name );

	$cross_link_protein_position_1 = $("#cross_link_protein_position_1");
	$cross_link_protein_position_2 = $("#cross_link_protein_position_2");

	$cross_link_protein_position_1.text( responseContext.protein1.protein_position );
	$cross_link_protein_position_2.text( responseContext.protein2.protein_position );

	$searches = $("#searches");
	$searches.text( responseContext.searchesCommaDelim );

	$length = $("#crosslink_length");
	$length.text( Math.round( link.length * 10 ) / 10 );
	
	var $link_info_table = $("#link_info_table__tbody");

	$link_info_table.empty();


	var handlebarsSource_search_entry_data_row_template = $( "#search_entry_data_row_template tbody" ).html(); //  " tbody" since it is a table

	if ( handlebarsSource_search_entry_data_row_template === undefined ) {
		throw "handlebarsSource_search_entry_data_row_template === undefined";
	}
	if ( handlebarsSource_search_entry_data_row_template === null ) {
		throw "handlebarsSource_search_entry_data_row_template === null";
	}
	
	var handlebarsTemplate_search_entry_data_row_template = Handlebars.compile( handlebarsSource_search_entry_data_row_template );
	
	
	var handlebarsSource_search_entry_child_row_template = $( "#search_entry_child_row_template tbody" ).html(); //  " tbody" since it is a table

	var handlebarsSource_crosslink_peptide_block_template = $( "#crosslink_peptide_block_template" ).html(); 

	
	var handlebarsSource_crosslink_peptide_data_row_entry_template = $( "#crosslink_peptide_data_row_entry_template tbody" ).html(); //  " tbody" since it is a table

	if ( handlebarsSource_crosslink_peptide_data_row_entry_template === undefined ) {
		throw "handlebarsSource_crosslink_peptide_data_row_entry_template === undefined";
	}
	if ( handlebarsSource_crosslink_peptide_data_row_entry_template === null ) {
		throw "handlebarsSource_crosslink_peptide_data_row_entry_template === null";
	}
	
	var handlebarsTemplate_crosslink_peptide_data_row_entry_template = Handlebars.compile( handlebarsSource_crosslink_peptide_data_row_entry_template );

	
	var handlebarsSource_crosslink_peptide_child_row_entry_template = $( "#crosslink_peptide_child_row_entry_template tbody" ).html(); //  " tbody" since it is a table

	var searchIdArray = Object.keys( ajaxResponseData );
	
	//  Sort the search ids in ascending order
	searchIdArray.sort(function compareNumbers(a, b) {
		  return a - b;
	});
	
	for ( var searchIdIndex = 0; searchIdIndex < searchIdArray.length; searchIdIndex++ ) {

		var searchId = searchIdArray[ searchIdIndex ];

		var searchProteinCrosslink = ajaxResponseData[ searchId ];

		searchProteinCrosslink.numPeptides = searchProteinCrosslink.numLinkedPeptides;
		searchProteinCrosslink.numUniquePeptides  = searchProteinCrosslink.numUniqueLinkedPeptides;
		
		var context = searchProteinCrosslink;

		
		var html = handlebarsTemplate_search_entry_data_row_template(context);



		var $search_entry_data_row = $(html).appendTo($link_info_table);
		
		var $search_entry_child_row = $( handlebarsSource_search_entry_child_row_template ).appendTo($link_info_table);
		
		if ( searchIdArray.length === 1 ) {
			
			var $toggle_visibility_expansion_span_jq = $search_entry_data_row.find(".toggle_visibility_expansion_span_jq");
			var $toggle_visibility_contraction_span_jq = $search_entry_data_row.find(".toggle_visibility_contraction_span_jq");

			$toggle_visibility_expansion_span_jq.hide();
			$toggle_visibility_contraction_span_jq.show();
			$search_entry_child_row.show();
		}
		
		var $peptide_data_container = $search_entry_child_row.find(".peptide_data_container");
		
		var $crosslink_peptide_block_template = $(handlebarsSource_crosslink_peptide_block_template).appendTo($peptide_data_container);

		
		var $peptide_table_jq =  $crosslink_peptide_block_template.find(".peptide_table_jq");

//		var $peptide_table_jq =  $peptide_data_container.find(".peptide_table_jq");



		var peptides = searchProteinCrosslink.peptides;

		for ( var peptideIndex = 0; peptideIndex < peptides.length ; peptideIndex++ ) {

			var peptide = peptides[ peptideIndex ];
			
			peptide.searchId = searchId;
			
//			var 
			context = peptide;
			
			if ( peptides.length === 1 ) {
				context.onlyOneEntry = true;
			}
			
			

//			var    
			html = handlebarsTemplate_crosslink_peptide_data_row_entry_template(context);

			var $peptide_entry_data_row = $( html ).appendTo($peptide_table_jq);
			
//			var $peptide_entry_child_row = 
			$( handlebarsSource_crosslink_peptide_child_row_entry_template ).appendTo($peptide_table_jq);

			if ( peptides.length === 1 ) {
				
				//  There is only one peptide so show the PSMs for it.
				
				$peptide_entry_data_row.click();
			}
		}

	}


	var $openLorkeetLinks = $(".view_spectrum_open_spectrum_link_jq");

	addOpenLorikeetViewerClickHandlers( $openLorkeetLinks );

	linkInfoOverlayWidthResizer();
}


//////////////Process MONO link


/////////////////////

function getMonolinkDataForSpecificLinkInGraph( params, link ) {

	console.log( params );
	
	var psmQValueCutoff = params.psmQValueCutoff;
	var peptideQValueCutoff = params.peptideQValueCutoff;

	incrementSpinner();				// create spinner

	var fromPp = link.position1;
	var from_protein_id = link.protein1;

	var searchesArray = link.searchIds;

	var _URL = contextPathJSVar + "/services/imageViewer/getMonolinkDataForSpecificLinkInGraph";

//	_URL += buildQueryStringFromHash();

	var ajaxRequestData = {

			psmQValueCutoff : psmQValueCutoff,
			peptideQValueCutoff : peptideQValueCutoff,
			searchIds: searchesArray,
			proteinId: from_protein_id,
			proteinLinkPosition: fromPp
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

			getMonolinkDataForSpecificLinkInGraphProcessResponse( { ajaxResponseData: data, ajaxRequestData: ajaxRequestData }, link);

		},
        failure: function(errMsg) {
        	handleAJAXFailure( errMsg );
        },
		error: function(jqXHR, textStatus, errorThrown) {	

			handleAJAXError( jqXHR, textStatus, errorThrown );
		}
	});


}



function getMonolinkDataForSpecificLinkInGraphProcessResponse( params, link ) {

	var ajaxResponseData = params.ajaxResponseData;


	openLinkInfoOverlayBackground();

	var fromP = _proteinNames[ link.protein1 ];
	var fromPp = link.position1;

	$("#mono_link_general_data").show();
	$("#loop_link_general_data").hide();
	$("#cross_link_general_data").hide();


	var from_protein_id = link.protein1;

	$from_protein_id = $("#mono_link_from_protein_id");

	$from_protein_id.text( from_protein_id );

	var $from_protein_name = $("#mono_link_from_protein_name");

	$from_protein_name.text( fromP );

	$protein_position = $("#mono_link_protein_position");

	$protein_position.text( fromPp );

	$searches = $("#searches");

	$searches.text( 'searches text' );

	var $link_info_table = $("#link_info_table__tbody");

	$link_info_table.empty();

	var $mono_link_protein_info = $("#mono_link_protein_info");

	var mono_link_protein_info = $mono_link_protein_info.text();

	

	var handlebarsSource_search_entry_data_row_template = $( "#search_entry_data_row_template tbody" ).html(); //  " tbody" since it is a table

	if ( handlebarsSource_search_entry_data_row_template === undefined ) {
		throw "handlebarsSource_search_entry_data_row_template === undefined";
	}
	if ( handlebarsSource_search_entry_data_row_template === null ) {
		throw "handlebarsSource_search_entry_data_row_template === null";
	}
	
	var handlebarsTemplate_search_entry_data_row_template = Handlebars.compile( handlebarsSource_search_entry_data_row_template );
	
	
	var handlebarsSource_search_entry_child_row_template = $( "#search_entry_child_row_template tbody" ).html(); //  " tbody" since it is a table

	var handlebarsSource_monolink_peptide_block_template = $( "#monolink_peptide_block_template" ).html(); 

	
	var handlebarsSource_monolink_peptide_data_row_entry_template = $( "#monolink_peptide_data_row_entry_template tbody" ).html(); //  " tbody" since it is a table

	if ( handlebarsSource_monolink_peptide_data_row_entry_template === undefined ) {
		throw "handlebarsSource_monolink_peptide_data_row_entry_template === undefined";
	}
	if ( handlebarsSource_monolink_peptide_data_row_entry_template === null ) {
		throw "handlebarsSource_monolink_peptide_data_row_entry_template === null";
	}
	
	var handlebarsTemplate_monolink_peptide_data_row_entry_template = Handlebars.compile( handlebarsSource_monolink_peptide_data_row_entry_template );

	
	var handlebarsSource_monolink_peptide_child_row_entry_template = $( "#monolink_peptide_child_row_entry_template tbody" ).html(); //  " tbody" since it is a table

	var searchIdArray = Object.keys( ajaxResponseData );

	//  Sort the search ids in ascending order
	searchIdArray.sort(function compareNumbers(a, b) {
		  return a - b;
	});
	
	for ( var searchIdIndex = 0; searchIdIndex < searchIdArray.length; searchIdIndex++ ) {

		var searchId = searchIdArray[ searchIdIndex ];

		var searchProteinMonolink = ajaxResponseData[ searchId ];

		var context = searchProteinMonolink;

		
		var html = handlebarsTemplate_search_entry_data_row_template(context);

		var $search_entry_data_row = $(html).appendTo($link_info_table);
		
		var $search_entry_child_row = $( handlebarsSource_search_entry_child_row_template ).appendTo($link_info_table);
		
		if ( searchIdArray.length === 1 ) {
			
			var $toggle_visibility_expansion_span_jq = $search_entry_data_row.find(".toggle_visibility_expansion_span_jq");
			var $toggle_visibility_contraction_span_jq = $search_entry_data_row.find(".toggle_visibility_contraction_span_jq");

			$toggle_visibility_expansion_span_jq.hide();
			$toggle_visibility_contraction_span_jq.show();
			$search_entry_child_row.show();
		}
		
		var $peptide_data_container = $search_entry_child_row.find(".peptide_data_container");
		
		var $monolink_peptide_block_template = 
			$(handlebarsSource_monolink_peptide_block_template).appendTo($peptide_data_container);


		var $peptide_table_jq =  $monolink_peptide_block_template.find(".peptide_table_jq");

//		var $peptide_table_jq =  $peptide_data_container.find(".peptide_table_jq");




		var peptides = searchProteinMonolink.peptides;

		for ( var peptideIndex = 0; peptideIndex < peptides.length ; peptideIndex++ ) {

			var peptide = peptides[ peptideIndex ];

			peptide.mono_link_protein_info = mono_link_protein_info;

			peptide.searchId = searchId;


//			var 
			context = peptide;


//			var    
			html = handlebarsTemplate_monolink_peptide_data_row_entry_template(context);

			var $peptide_entry_data_row = $( html ).appendTo($peptide_table_jq);
			
//			var $peptide_entry_child_row = 
			$( handlebarsSource_monolink_peptide_child_row_entry_template ).appendTo($peptide_table_jq);

			if ( peptides.length === 1 ) {
				
				//  There is only one peptide so show the PSMs for it.
				$peptide_entry_data_row.click();
			}
			
		}
	}

	var $openLorkeetLinks = $(".view_spectrum_open_spectrum_link_jq");

	addOpenLorikeetViewerClickHandlers( $openLorkeetLinks );

	linkInfoOverlayWidthResizer();
}



///////////////////////////////////////////////////////////////////////////


///  Overlay general processing

var openLinkInfoOverlayBackground = function (  ) {
	
	
	
	//  Close any open Lorikeet Overlay
	closeLorikeetOverlay();
	
	

	var $view_link_info_modal_dialog_overlay_background = $("#view_link_info_modal_dialog_overlay_background");
	var $view_link_info_overlay_div = $("#view_link_info_overlay_div");

	

	//  Adjust the overlay positon to be within the viewport

	var scrollTopWindow = $(window).scrollTop();

	if ( scrollTopWindow > 0 ) {

		//  User has scrolled down

		var overlayTop = scrollTopWindow + 10;

		$view_link_info_overlay_div.css( { top: overlayTop + "px" } );

	} else {

		$view_link_info_overlay_div.css( { top: "10px" } );
	}
	
	
	$view_link_info_modal_dialog_overlay_background.show();
	$view_link_info_overlay_div.show();

};

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

//	$(".view-spectra-overlay-div").click( function( eventObject ) {
//
//		closeViewLinkInfoOverlay();
//	} );

	$(".error-message-ok-button").click( function( eventObject ) {

		closeViewLinkInfoOverlay();
	} );

	
	
	
//	$("#view-spectra-overlay-div").click( function( eventObject ) {
//
//		return false;
//	} );

};

