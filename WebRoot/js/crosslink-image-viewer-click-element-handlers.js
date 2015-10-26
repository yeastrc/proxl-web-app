
//  crosslink-image-viewer-click-element-handlers.js



///////////////////////////////////////////////////////////////////////////

////////////    Click handlers for the links ( Lines that show the links )



var linkInfoOverlayWidthResizer = function() {
	
	var $link_info_table = $("#link_info_table__tbody");

	var link_info_table_width = $link_info_table.outerWidth( true /* [ includeMargin ] */ );

	//  Adjust width of link info overlay to be 40 pixels wider than the link info table
	
	var view_link_info_overlay_div = link_info_table_width + 40;

	$("#view_link_info_overlay_div").css( {"width": view_link_info_overlay_div } );
};


//////////////   Process LOOP link


/////////////////////

function getLooplinkDataForSpecificLinkInGraph( params ) {

	var clickThis = params.clickThis;
	var psmQValueCutoff = params.psmQValueCutoff;
	var peptideQValueCutoff = params.peptideQValueCutoff;


	var $clickThis = $(clickThis);
	
//	var $link_info_table = $("#link_info_table__tbody");
//	
//	$link_info_table.empty();
	
	
	createSpinner();				// create spinner

//	var fromP = $clickThis.attr( "fromp" );
	var fromPp = $clickThis.attr( "frompp" );
	var toPp = $clickThis.attr( "topp" );
	var lsearches = $clickThis.attr( "searches" );

	var from_protein_id = $clickThis.attr( "from_protein_id" );
//	var to_protein_id = $clickThis.attr( "to_protein_id" );

	var searchesArray = lsearches.split(",");

	var _URL = contextPathJSVar + "/services/imageViewer/getLooplinkDataForSpecificLinkInGraph";

//	_URL += buildQueryStringFromHash();

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


			destroySpinner();

			getLooplinkDataForSpecificLinkInGraphProcessResponse( { ajaxResponseData: data, ajaxRequestData: ajaxRequestData, clickThis: clickThis });

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

function getLooplinkDataForSpecificLinkInGraphProcessResponse( params ) {
	
	var ajaxResponseData = params.ajaxResponseData;
	
//	var ajaxRequestData = params.ajaxRequestData;

	var clickThis = params.clickThis;

	var $clickThis = $(clickThis);
	
	
	openLinkInfoOverlayBackground();
	
	var fromP = $clickThis.attr( "fromp" );
	var fromPp = $clickThis.attr( "frompp" );
	var toPp = $clickThis.attr( "topp" );
	var lsearches = $clickThis.attr( "searches" );

	
	$("#loop_link_general_data").show();
	$("#cross_link_general_data").hide();
	$("#mono_link_general_data").hide();

	var from_protein_id = $clickThis.attr( "from_protein_id" );

	$from_protein_id = $("#loop_link_from_protein_id");

	$from_protein_id.text( from_protein_id );

	var $from_protein_name = $("#loop_link_from_protein_name");

	$from_protein_name.text( fromP );

	$from_protein_position = $("#loop_link_from_protein_position");
	$to_protein_position = $("#loop_link_to_protein_position");

	$from_protein_position.text( fromPp );
	$to_protein_position.text( toPp );

	$searches = $("#searches");

	$searches.text( lsearches );
	
	var $link_info_table = $("#link_info_table__tbody");
	
	$link_info_table.empty();
	
	var $loop_link_protein_info = $("#loop_link_protein_info");
	
	var loop_link_protein_info = $loop_link_protein_info.text();
	
	
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


//searchProteinLooplink: Object
//	bestPSMQValue: 0.001051
//	bestPeptideQValue: 0.004214
//	numPeptides: 3
//	numPsms: 8
//	numUniquePeptides: 3
//	numUniquePsms: 0
//	peptideCutoff: 0.01
//	peptides: Array[3]
//		0: Object
//		bestPsmQValue: 0.001051
//		numPsms: 4
//		pValue: 0
//		pep: 0.009062
//		reportedPeptide: Object
//			c: "-"
//				id: 1012223
//				n: "-"
//				sequence: "NSSEEGTAEK[155.09]SKKLR(12,13)-LOOP"
//		peptide: Object
//			id: 228484
//			sequence: "NSSEEGTAEKSKKLR"
//		peptidePosition1: 12
//		peptidePosition2: 13
//		
//		peptideProteinPositions: Array[1]
//			0: Object
//			position1: 24
//			position2: 25
//			protein: Object - Same or similar to "protein: Object" below
//				description: "No description in FASTA."
//					name: "Fbxl3-human"
//					nrProtein: Object
//					search: Object
//			
//		peptideQValueCutoff: 0.01
//		proteinPositionsString: "Fbxl3-human(24,25)"
//		psmQValueCutoff: 0.01
//
//		psms: Array[4]
//			0: Object
//			calcMass: 0
//			charge: 2
//			chargeSet: true
//			id: 382114
//			pep: 0.0009191
//			reportedPeptideId: 1012223
//			searchId: 308
//			psmId: "T-28418.pf.2015-01-08-Q_2013_1016_RJ_08-relD500odb-percolatorIn.txt"
//			qValue: 0.001051
//			scanId: 635969
//			svmScore: 0.733
//			type: 2
//
//		qvalue: 0.004214
//		search: Object
//		svmScore: 0.733
//
//	search: Object
//		comments: Array[0]
//		fastaFilename: "RJAZ10-comet-plus.fasta"
//		filename: "/data/search_space/CrossLinks/Crosslinks_Importer/ImportPercolatorKojakAndMzMLFiles_Searchespace/../Sample_FIles_Mike_Example_Dir__Modified_to_point_to_local_copy_of_files__2015_01_22/percout.xml"
//		formattedLoadTime: "2015-02-06 16:27:35"
//		id: 308
//		load_time: Object
//		name: "Search: 308"
//		projectId: 1
//		sha1sum: "586f185fc905d6d34f928292f8d221d95a415126"
//	
//	protein: Object
//		description: "No description in FASTA."
//		name: "Fbxl3-human"
//		nrProtein: Object
//			nrseqId: 23980491
//			sequence: "GSMKRGGRDSDRNSSEEGTAEKSKKLRTTNEHSQTCDWGNLLQDIILQVFKYLPLLDRAHASQVCRNWNQVFHMPDLWRCFEFELNQPATSYLKATHPELIKQIIKRHSNHLQYVSFKVDSSKESAEAACDILSQLVNCSLKTLGLISTARPSFMDLPKSHFISALTVVFVNSKSLSSLKIDDTPVDDPSLKVLVANNSDTLKLLKMSSCPHVSPAGILCVADQCHGLRELALNYHLLSDELLLALSSEKHVRLEHLRIDVVSENPGQTHFHTIQKSSWDAFIRHSPKVNLVMYFFLYEEEFDPFFRYEIPATHLYFGRSVSKDVLGRVGMTCPRLVELVVCANGLRPLDEELIRIAERCKNLSAIGLGECEVSCSAFVEFVKMCGGRLSQLSIMEEVLIPDQKYSLEQIHWEVSKHLGRVWFPDMMPTW"
//			taxonomyId: 9606
//	
//	proteinPosition1: 24
//	proteinPosition2: 25
//	psmCutoff: 0.01







/////////////////////////////////

//////////////   Process CROSS link




/////////////////////

function getCrosslinkDataForSpecificLinkInGraph( params ) {

	var clickThis = params.clickThis;
	var psmQValueCutoff = params.psmQValueCutoff;
	var peptideQValueCutoff = params.peptideQValueCutoff;

	var $clickThis = $(clickThis);


	createSpinner();				// create spinner

	var from_protein_name = $clickThis.attr( "fromp" );
	var from_protein_position = $clickThis.attr( "frompp" );
	var to_protein_name = $clickThis.attr( "top" );
	var to_protein_position = $clickThis.attr( "topp" );

	var from_protein_id = $clickThis.attr( "from_protein_id" );
	var to_protein_id = $clickThis.attr( "to_protein_id" );

	var searchesCommaDelim = $clickThis.attr( "searches" ); //  Searches, comma delimited

	
	
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

	var searchesArray = searchesCommaDelim.split(",");
	
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


			destroySpinner();
			
			var getCrosslinkDataForSpecificLinkInGraphProcessResponseParams = 
				{ 
					ajaxResponseData: data, 
					ajaxRequestData: ajaxRequestData,
					context: context,
					clickThis: clickThis
				};

			getCrosslinkDataForSpecificLinkInGraphProcessResponse( getCrosslinkDataForSpecificLinkInGraphProcessResponseParams );

		},
        failure: function(errMsg) {
        	handleAJAXFailure( errMsg );
        },
        error: function(jqXHR, textStatus, errorThrown) {	

			handleAJAXError( jqXHR, textStatus, errorThrown );
		}
	});


}



function getCrosslinkDataForSpecificLinkInGraphProcessResponse( params ) {

	var ajaxResponseData = params.ajaxResponseData;

//	var ajaxRequestData = params.ajaxRequestData;

//	var clickThis = params.clickThis;
	
	var responseContext = params.context;

//	var $clickThis = $(clickThis);


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

function getMonolinkDataForSpecificLinkInGraph( params ) {

	var clickThis = params.clickThis;
	var psmQValueCutoff = params.psmQValueCutoff;
	var peptideQValueCutoff = params.peptideQValueCutoff;

	var $clickThis = $(clickThis);

//	var $link_info_table = $("#link_info_table__tbody");

//	$link_info_table.empty();


	createSpinner();				// create spinner

//	var fromP = $clickThis.attr( "fromp" );
	var fromPp = $clickThis.attr( "frompp" );
//	var toPp = $clickThis.attr( "topp" );
	var lsearches = $clickThis.attr( "searches" );

	var from_protein_id = $clickThis.attr( "from_protein_id" );
//	var to_protein_id = $clickThis.attr( "to_protein_id" );

	var searchesArray = lsearches.split(",");

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


			destroySpinner();

			getMonolinkDataForSpecificLinkInGraphProcessResponse( { ajaxResponseData: data, ajaxRequestData: ajaxRequestData, clickThis: clickThis });

		},
        failure: function(errMsg) {
        	handleAJAXFailure( errMsg );
        },
        error: function(jqXHR, textStatus, errorThrown) {	

			handleAJAXError( jqXHR, textStatus, errorThrown );
		}
	});


}



function getMonolinkDataForSpecificLinkInGraphProcessResponse( params ) {

	var ajaxResponseData = params.ajaxResponseData;

//	var ajaxRequestData = params.ajaxRequestData;

	var clickThis = params.clickThis;

	var $clickThis = $(clickThis);


	openLinkInfoOverlayBackground();

	var fromP = $clickThis.attr( "fromp" );
	var fromPp = $clickThis.attr( "frompp" );
//	var toPp = $clickThis.attr( "topp" );
	var lsearches = $clickThis.attr( "searches" );


	$("#mono_link_general_data").show();
	$("#loop_link_general_data").hide();
	$("#cross_link_general_data").hide();


	var from_protein_id = $clickThis.attr( "from_protein_id" );

	$from_protein_id = $("#mono_link_from_protein_id");

	$from_protein_id.text( from_protein_id );

	var $from_protein_name = $("#mono_link_from_protein_name");

	$from_protein_name.text( fromP );

	$protein_position = $("#mono_link_protein_position");

	$protein_position.text( fromPp );

	$searches = $("#searches");

	$searches.text( lsearches );

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

//	var docHeight = $(document).height();

//	$("#lorikeet-modal-dialog-overlay-background").css({ height: docHeight });
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

