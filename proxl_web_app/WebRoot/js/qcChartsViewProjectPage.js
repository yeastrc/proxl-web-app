
//          qcChartsViewProjectPage.js


//  Quality Control charts

//  Javascript for the viewProject.jsp page

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";



//CONSTANTS


var RETENTION_TIME_COUNT_CHART_WIDTH = 920;
var RETENTION_TIME_COUNT_CHART_HEIGHT = 650;

var RELOAD_RETENTION_COUNT_CHART_TIMER_DELAY = 400;  // in Milliseconds





var qcChartsInitialized = false;




///   Called when Google charts is initialized

function initQCCharts() {
	
	qcChartsInitialized = true;
	
	
	
};




///   Called from viewProjectPage.js  initPage()  when page is ready


function initQCPlotsClickHandlers() {	
	
	qcChartRetentionTime.init();
	
	
	
	
}



//  Constructor for QC chart for Scan Retention Time

var QCChartRetentionTime = function(  ) {
	
	
	this.globals = {
			
			currentSearchData : 
				{ 
				
				prevScanFileId : undefined
				,
				maxValuesInDB : {
					
					maxX : undefined,
					maxY : undefined
				}
				,
				annotationTypeDataById : undefined,
				annotationTypeDataArray : undefined
			}
//			,
//			
//			allSearchesData :
//				{
//					"<searchId>" : {
//						
//						annotationTypes : [ ]
//					}
//				
//				}
			
	};
	

	
};


///////////

//  Create an instance from the constructor

var qcChartRetentionTime = new QCChartRetentionTime();




//CONSTANTS


QCChartRetentionTime.prototype.RETENTION_TIME_COUNT_CHART_WIDTH = RETENTION_TIME_COUNT_CHART_WIDTH;
QCChartRetentionTime.prototype.RETENTION_TIME_COUNT_CHART_HEIGHT = RETENTION_TIME_COUNT_CHART_HEIGHT;

QCChartRetentionTime.prototype.RELOAD_RETENTION_COUNT_CHART_TIMER_DELAY = RELOAD_RETENTION_COUNT_CHART_TIMER_DELAY;  // in Milliseconds


//////////

QCChartRetentionTime.prototype.init = function() {

	var objectThis = this;

	

	$(".qc_plot_scan_retention_time_link_jq").click(function(eventObject) {

		try {
			var clickThis = this;

			objectThis.scanRetentionTimeQCPlotClickHandler( clickThis, eventObject );

			return false;
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});	 	

	$(".scan_retention_time_qc_plot_overlay_close_parts_jq").click(function(eventObject) {

		try {
			var clickThis = this;

			if ( objectThis.reloadRetentionTimeCountChartTimerId ) {

				clearTimeout( objectThis.reloadRetentionTimeCountChartTimerId );

				objectThis.reloadRetentionTimeCountChartTimerId = null;
			}

			objectThis.closeScanRetentionTimeQCPlotOverlay( clickThis, eventObject );

			return false;
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});

	$("#scan_retention_time_qc_plot_score_type_id").change(function(eventObject) {

		try {
			objectThis.scoreTypeChanged( );

			return false;
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});


	$(".scan_retention_time_qc_plot_on_change_jq").change(function(eventObject) {

		try {
			objectThis.createRetentionTimeCountChartFromPageParams( );

			return false;
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});


	$(".scan_retention_time_qc_plot_on_change_jq").keyup(function(eventObject) {

		try {
			if ( objectThis.reloadRetentionTimeCountChartTimerId ) {

				clearTimeout( objectThis.reloadRetentionTimeCountChartTimerId );

				objectThis.reloadRetentionTimeCountChartTimerId = null;
			}


			objectThis.reloadRetentionTimeCountChartTimerId = setTimeout( function() {
				try {

					objectThis.createRetentionTimeCountChartFromPageParams( );
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}

			}, objectThis.RELOAD_RETENTION_COUNT_CHART_TIMER_DELAY );

			return false;
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});



	$("#scan_retention_time_qc_plot_max_reset_button").click(function(eventObject) {

		try {
//			var clickThis = this;

			objectThis.resetRetentionTimeCountChartMaxXMaxY();

			objectThis.createRetentionTimeCountChartFromPageParams( );

			return false;
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});


	$( "#rt-svg-download-jpeg" ).click( function() { objectThis.downloadSvg( 'jpeg' ); });
	$( "#rt-svg-download-png" ).click( function() { objectThis.downloadSvg( 'png' ); });
	$( "#rt-svg-download-pdf" ).click( function() { objectThis.downloadSvg( 'pdf' ); });
	$( "#rt-svg-download-svg" ).click( function() { objectThis.downloadSvg( 'svg' ); });
	
};



/////////////////////////////////////////


//   Scan Retention Time QC Plot


//var scanRetentionTimeQCPlot


QCChartRetentionTime.prototype.downloadSvg = function( type ) {
	
	try {
		
		var form = document.createElement( "form" );
		
		$( form ).hide();
		
	    form.setAttribute( "method", "post" );
	    form.setAttribute( "action", contextPathJSVar + "/convertAndDownloadSVG.do" );

		var $svg_image_inner_container_div__svg_merged_image_svg_jq = $( "#scan_retention_time_qc_plot_chartDiv svg " );
		var svgContents = $svg_image_inner_container_div__svg_merged_image_svg_jq.html();
		
		
		
		var fullSVG_String = "<?xml version=\"1.0\" standalone=\"no\"?><!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">";
		fullSVG_String += "<svg id=\"svg\" ";
		
		fullSVG_String += "width=\"" + $svg_image_inner_container_div__svg_merged_image_svg_jq.attr( "width" ) + "\" ";
		fullSVG_String += "height=\"" + $svg_image_inner_container_div__svg_merged_image_svg_jq.attr( "height" ) + "\" ";
		
		fullSVG_String += "xmlns=\"http://www.w3.org/2000/svg\">" + svgContents + "</svg>";
	    
		// fix the URL that google charts is putting into the SVG. Breaks parsing.
		fullSVG_String = fullSVG_String.replace( /url\(.+\#_ABSTRACT_RENDERER_ID_(\d+)\)/g, "url(#_ABSTRACT_RENDERER_ID_$1)" );		
		
	    var svgStringField = document.createElement( "input" );
	    svgStringField.setAttribute("name", "svgString");
	    svgStringField.setAttribute("value", fullSVG_String );
	    
	    var fileTypeField = document.createElement( "input" );
	    fileTypeField.setAttribute("name", "fileType");
	    fileTypeField.setAttribute("value", type);

	    form.appendChild( svgStringField );
	    form.appendChild( fileTypeField );
	    
	    document.body.appendChild(form);    // Not entirely sure if this is necessary			

	    form.submit();
		
	    document.body.removeChild( form );
		
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
	
};


/////////////////

QCChartRetentionTime.prototype.scanRetentionTimeQCPlotClickHandler = function(clickThis, eventObject) {

	var objectThis = this;
	
	objectThis.openScanRetentionTimeQCPlotOverlay(clickThis, eventObject);

	return;

};

///////////

QCChartRetentionTime.prototype.closeScanRetentionTimeQCPlotOverlay = function(clickThis, eventObject) {

	$(".scan_retention_time_qc_plot_overlay_show_hide_parts_jq").hide();
};



///////////

QCChartRetentionTime.prototype.openScanRetentionTimeQCPlotOverlay = function(clickThis, eventObject) {

	var objectThis = this;

	
	var $clickThis = $(clickThis);

	//	get root div for this search
	var $search_root_jq = $clickThis.closest(".search_root_jq");

	var searchId = $search_root_jq.attr("searchId");	
	
	
	var $scan_retention_time_qc_plot_overlay_container = $("#scan_retention_time_qc_plot_overlay_container");

	var $scan_retention_time_qc_plot_overlay_background = $("#scan_retention_time_qc_plot_overlay_background"); 
	$scan_retention_time_qc_plot_overlay_background.show();
	$scan_retention_time_qc_plot_overlay_container.show();

	

	// copy the search name to the overlay

//	var $search_name_display_jq = $search_root_jq.find(".search_name_display_jq");

//	var search_name_display_jq = $search_name_display_jq.text();

//	var $delete_search_overlay_search_name = $("#delete_search_overlay_search_name");
//	$delete_search_overlay_search_name.text( search_name_display_jq );



//	var $delete_search_confirm_button = $("#delete_search_confirm_button");
//	$delete_search_confirm_button.data("searchId", searchId);
	
	// Position dialog over clicked link
	
	//  get position of div containing the dialog that is inline in the page
	var $scan_retention_time_qc_plot_overlay_containing_outermost_div_inline_div = $("#scan_retention_time_qc_plot_overlay_containing_outermost_div_inline_div");
	
	var offset__containing_outermost_div_inline_div = $scan_retention_time_qc_plot_overlay_containing_outermost_div_inline_div.offset();
	var offsetTop__containing_outermost_div_inline_div = offset__containing_outermost_div_inline_div.top;
	

	var $scan_retention_time_qc_plot_overlay_container = $("#scan_retention_time_qc_plot_overlay_container");
	
	var scrollTopWindow = $(window).scrollTop();
	
	var positionAdjust = scrollTopWindow - offsetTop__containing_outermost_div_inline_div + 10;

	$scan_retention_time_qc_plot_overlay_container.css( "top", positionAdjust );


	
	
	
	var $scan_retention_time_qc_plot_current_search_id = $("#scan_retention_time_qc_plot_current_search_id");
	
	var prevSearchId = $scan_retention_time_qc_plot_current_search_id.val( );
	
	if ( prevSearchId === searchId ) {
		
		////  Same Search Id as when last opened so just show it
		
		var $scan_retention_time_qc_plot_overlay_background = $("#scan_retention_time_qc_plot_overlay_background"); 
		$scan_retention_time_qc_plot_overlay_background.show();
		$scan_retention_time_qc_plot_overlay_container.show();
		
		return;  //  EARLY EXIT
	}
	
	
	this.globals.currentSearchData = undefined;   ///  Reset "currentSearchData" for new search
	
	
	
	
	$scan_retention_time_qc_plot_current_search_id.val( searchId );
	
	
	//  initialize all "Scans for" checkboxes to checked

	var $scan_retention_time_qc_plot_scans_include_jq = $(".scan_retention_time_qc_plot_scans_include_jq");

	$scan_retention_time_qc_plot_scans_include_jq.each( function(   ) {

		$( this ).prop('checked',true);

	} );
	
	this.populateCutoffsOnImportMessage( { $search_root_jq: $search_root_jq } );


	var afterGetScanFileIdsForSearchId = function() {
		
		objectThis.getPSMFilterableAnnTypesForSearchId( { searchId : searchId } );
	};
	
	
	this.getScanFileIdsForSearchId( { 
		searchId: searchId, 
		callback: afterGetScanFileIdsForSearchId } );
	
};


//////////

///  

QCChartRetentionTime.prototype.populateCutoffsOnImportMessage = function( params ) {

	var $search_root_jq = params.$search_root_jq;
	
	var $qc_plots_links_filtered_on_import_message_jq = $search_root_jq.find(".qc_plots_links_filtered_on_import_message_jq");
	
	var qc_plots_links_filtered_on_import_message_jqHTML = $qc_plots_links_filtered_on_import_message_jq.html();
	
	var $scan_retention_time_qc_plot_score_cutoffs_on_import_row = $("#scan_retention_time_qc_plot_score_cutoffs_on_import_row");
	var $scan_retention_time_qc_plot_score_cutoffs_on_import_anns_values = $("#scan_retention_time_qc_plot_score_cutoffs_on_import_anns_values");
	
	if ( qc_plots_links_filtered_on_import_message_jqHTML === "" ) {
		
		$scan_retention_time_qc_plot_score_cutoffs_on_import_row.hide();
		return;
	}
	
	$scan_retention_time_qc_plot_score_cutoffs_on_import_row.show();
	$scan_retention_time_qc_plot_score_cutoffs_on_import_anns_values.html( qc_plots_links_filtered_on_import_message_jqHTML );
	
};


//////////

///  

QCChartRetentionTime.prototype.getPSMFilterableAnnTypesForSearchId = function( params ) {

	var objectThis = this;


	var searchId = params.searchId;
	
	


	if ( ! qcChartsInitialized ) {

		throw "qcChartsInitialized is false"; 
	}


	var _URL = contextPathJSVar + "/services/annotationTypes/getAnnotationTypesPsmFilterableForSearchId";

	var requestData = {
			searchId : searchId
	};

//	var request =
	$.ajax({
		type : "GET",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(data) {

			try {
				objectThis.getPSMFilterableAnnTypesForSearchIdResponse(requestData, data, params);
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

///

QCChartRetentionTime.prototype.getPSMFilterableAnnTypesForSearchIdResponse = function(requestData, responseData, originalParams) {
	
	var annTypesSearchProgramsPerSearch = responseData.annotationTypeList;

	if (  annTypesSearchProgramsPerSearch.length === 0 ) {
		
		throw "annTypesSearchProgramsPerSearch.length === 0";
	}
	
	
	
	if ( ! this.globals.currentSearchData ) {
		
		this.globals.currentSearchData = {};
	}
	
	var annTypes = [];
	
	var annTypesById = {};
	
	for ( var annTypesSearchProgramsPerSearchIndex = 0; annTypesSearchProgramsPerSearchIndex < annTypesSearchProgramsPerSearch.length; annTypesSearchProgramsPerSearchIndex++ ) {
		
		var annTypeSearchProgramPerSearch = annTypesSearchProgramsPerSearch [ annTypesSearchProgramsPerSearchIndex ];
		
		var annType = annTypeSearchProgramPerSearch.annotationTypeDTO;
		
		var searchProgramPerSearch = annTypeSearchProgramPerSearch.searchProgramsPerSearchDTO;
		
		annType.searchProgramName = searchProgramPerSearch.displayName;
		
		annTypes.push( annType );
		
		var annTypeId = annType.id.toString();  
		
		annTypesById[ annTypeId ] = annType;
	}
	
	this.globals.currentSearchData.annotationTypeDataArray = annTypes;
	
	this.globals.currentSearchData.annotationTypeDataById = annTypesById;
	
	
	this.getPSMFilterableMaxMinValuesAnnTypes( { searchId : originalParams.searchId } );
};
	




///  

QCChartRetentionTime.prototype.getPSMFilterableMaxMinValuesAnnTypes = function( params ) {

	var objectThis = this;

	var searchId = params.searchId;

	var annTypes = this.globals.currentSearchData.annotationTypeDataArray;



	if ( ! qcChartsInitialized ) {

		throw "qcChartsInitialized is false"; 
	}
	
	
	var annTypeIds = [];
	
	for ( var annTypesIndex = 0; annTypesIndex < annTypes.length; annTypesIndex++ ) {
		
		var annType = annTypes[ annTypesIndex ];
		var annTypeId = annType.id;
		
		annTypeIds.push( annTypeId ); 
	}


	var _URL = contextPathJSVar + "/services/annotationTypes/getMinMaxValuesForPsmFilterableAnnTypeIdsSearchId";

	var requestData = {
			search_id : searchId,
			ann_type_id : annTypeIds
	};

//	var request =
	$.ajax({
		type : "GET",
		url : _URL,
		data : requestData,

		traditional: true,  //  Force traditional serialization of the data sent
		//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
		//   So proteinIdsToGetSequence array is passed as "proteinIdsToGetSequence=<value>" which is what Jersey expects
		
		dataType : "json",
		success : function(data) {
			try {
				objectThis.getPSMFilterableMaxMinValuesAnnTypesResponse(requestData, data, params);
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

///

QCChartRetentionTime.prototype.getPSMFilterableMaxMinValuesAnnTypesResponse = function(requestData, responseData, originalParams) {

	var minMaxValuesPerAnnType = responseData.minMaxValuesPerAnnType;

	if ( ! this.globals.currentSearchData ) {

		this.globals.currentSearchData = {};
	}

	this.globals.currentSearchData.minMaxValuesPerAnnType = minMaxValuesPerAnnType;
	
	this.populateAnnTypesSelect( { searchId : originalParams.searchId } );
};


///

QCChartRetentionTime.prototype.populateAnnTypesSelect = function( params ) {

	var annTypes = this.globals.currentSearchData.annotationTypeDataArray;
	
	var $scan_retention_time_qc_plot_score_type_id = $("#scan_retention_time_qc_plot_score_type_id");

	$scan_retention_time_qc_plot_score_type_id.empty();
	
	var optionsHTMLarray = [];
	
	for ( var annTypesIndex = 0; annTypesIndex < annTypes.length; annTypesIndex++ ) {
		
		var annType = annTypes[ annTypesIndex ];
		
		var html = "<option value='" + annType.id + "'>" + annType.name +
			" (" + annType.searchProgramName + ")" +
			"</option>";
			
		optionsHTMLarray.push( html );
	}
	
	var optionsHTML = optionsHTMLarray.join("");
	
	$scan_retention_time_qc_plot_score_type_id.append( optionsHTML );
	
	//  If an annotation type record has sort id of 1, then assign that annotation type id to the selector 
	
	for ( var annTypesIndex = 0; annTypesIndex < annTypes.length; annTypesIndex++ ) {
		
		var annType = annTypes [ annTypesIndex ];
		
		if ( annType.annotationTypeFilterableDTO && annType.annotationTypeFilterableDTO.sortOrder === 1 ) {
			
			$scan_retention_time_qc_plot_score_type_id.val( annType.id );
		}
	}
	
	//  Trigger change on annotation type selector
	
	$scan_retention_time_qc_plot_score_type_id.change();
};

//////////

///  

QCChartRetentionTime.prototype.getScanFileIdsForSearchId = function( params ) {


	var objectThis = this;

	
	
	var searchId = params.searchId;

	


	if ( ! qcChartsInitialized ) {

		throw "qcChartsInitialized is false"; 
	}


	var _URL = contextPathJSVar + "/services/utils/getScanFilesForSearchId";

	var requestData = {
			searchId : searchId
	};

//	var request =
	$.ajax({
		type : "GET",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(data) {
			try {
				objectThis.getScanFileIdsForSearchIdResponse(requestData, data, params);
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

			// alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
			// textStatus: " + textStatus );
		}
	});

};

///

QCChartRetentionTime.prototype.getScanFileIdsForSearchIdResponse = function(requestData, responseData, originalParams) {


//	var objectThis = this;

	
	
	var scanFiles = responseData;
	

//	var searchId = originalParams.searchId;

	var $scan_retention_time_qc_plot_scan_file_id = $("#scan_retention_time_qc_plot_scan_file_id");
	
	var callback = originalParams.callback;
	

	if ( scanFiles.length === 0 ) {
		
		throw "scanFileIds.length === 0";
	}
	
	$scan_retention_time_qc_plot_scan_file_id.empty();
	
	var optionsHTMLarray = [];
	
	for ( var scanFilesIndex = 0; scanFilesIndex < scanFiles.length; scanFilesIndex++ ) {
		
		var scanFile = scanFiles[ scanFilesIndex ];
		
		var html = "<option value='" + scanFile.id + "'>" + scanFile.filename + "</option>";
			
		optionsHTMLarray.push( html );
	}
	
	var optionsHTML = optionsHTMLarray.join("");
	
	$scan_retention_time_qc_plot_scan_file_id.append( optionsHTML );
	
	
	if ( callback ) {
		
		callback();
	}
	
};

/////////////


QCChartRetentionTime.prototype.scoreTypeChanged = function( ) {


//	var objectThis = this;
	

	var annTypes = this.globals.currentSearchData.annotationTypeDataById;
	
	var minMaxValuesPerAnnType = this.globals.currentSearchData.minMaxValuesPerAnnType;
		

	var $scan_retention_time_qc_plot_score_type_id = $("#scan_retention_time_qc_plot_score_type_id");

	var selectedAnnTypeId = $scan_retention_time_qc_plot_score_type_id.val( );
	
	var annTypeForSelectId = annTypes[ selectedAnnTypeId ];
	
	if ( annTypeForSelectId === undefined || annTypeForSelectId === null ) {
		
		throw "annType not found for id: " + selectedAnnTypeId;
	}
	
	var minMaxValuesForSelectId = minMaxValuesPerAnnType[ selectedAnnTypeId ];

	if ( minMaxValuesForSelectId === undefined || minMaxValuesForSelectId === null ) {
		
		throw "min max values not found for id: " + selectedAnnTypeId;
	}
	
	var $scan_retention_time_qc_plot_min_value_for_ann_type_id = $("#scan_retention_time_qc_plot_min_value_for_ann_type_id");
	var $scan_retention_time_qc_plot_max_value_for_ann_type_id = $("#scan_retention_time_qc_plot_max_value_for_ann_type_id");

	$scan_retention_time_qc_plot_min_value_for_ann_type_id.text( minMaxValuesForSelectId.minValue );
	$scan_retention_time_qc_plot_max_value_for_ann_type_id.text( minMaxValuesForSelectId.maxValue );
	

	var $scan_retention_time_qc_plot_psm_score_cutoff = $("#scan_retention_time_qc_plot_psm_score_cutoff");


	var selectedAnnotationTypeFilterableDTO = annTypeForSelectId.annotationTypeFilterableDTO;

	var defaultFilterValueString = selectedAnnotationTypeFilterableDTO.defaultFilterValueString;

	if ( defaultFilterValueString !== undefined && defaultFilterValueString !== null && defaultFilterValueString !== "" ) {

		var newPsmScoreCutoff = selectedAnnotationTypeFilterableDTO.defaultFilterValueString;

		$scan_retention_time_qc_plot_psm_score_cutoff.val( newPsmScoreCutoff );
		
		this.createRetentionTimeCountChartFromPageParams( );

	} else {
		
		//  No default filter value so clear the input field and remove the chart
		
		$scan_retention_time_qc_plot_psm_score_cutoff.val( "" );
		
		//  Remove chart
		
		this.removeRetentionTimeCountChart();
	}
};


//////////

///  

QCChartRetentionTime.prototype.createRetentionTimeCountChartFromPageParams = function( ) {


	var objectThis = this;

	
	
	if ( objectThis.reloadRetentionTimeCountChartTimerId ) {
		
		clearTimeout( objectThis.reloadRetentionTimeCountChartTimerId );
		
		objectThis.reloadRetentionTimeCountChartTimerId = null;
	}


	$(".scan_retention_time_qc_plot_filter_psms_by_param_not_a_number_jq").hide();

	$(".scan_retention_time_qc_plot_param_not_a_number_jq").hide();
	
	$(".scan_retention_time_qc_plot_have_data_jq").hide();
	
	
	var $scan_retention_time_qc_plot_current_search_id = $("#scan_retention_time_qc_plot_current_search_id");
	
	var searchId = $scan_retention_time_qc_plot_current_search_id.val( );

	var $scan_retention_time_qc_plot_scan_file_id = $("#scan_retention_time_qc_plot_scan_file_id");
	
	var scanFileId = $scan_retention_time_qc_plot_scan_file_id.val();
	
	var scanFileName = $( "#scan_retention_time_qc_plot_scan_file_id option:selected" ).text();
	
	var $scan_retention_time_qc_plot_score_type_id = $("#scan_retention_time_qc_plot_score_type_id");
	
	var annotationTypeId = $scan_retention_time_qc_plot_score_type_id.val();
	

	var $scan_retention_time_qc_plot_psm_score_cutoff = $("#scan_retention_time_qc_plot_psm_score_cutoff");
	
	var psmScoreCutoff = $scan_retention_time_qc_plot_psm_score_cutoff.val();
	

	if ( ! /^[+-]?((\d+(\.\d*)?)|(\.\d+))$/.test( psmScoreCutoff ) ) {

		//  psmScoreCutoff value is not a valid decimal number

		this.removeRetentionTimeCountChart();

		$(".scan_retention_time_qc_plot_filter_psms_by_param_not_a_number_jq").show();

		$scan_retention_time_qc_plot_psm_score_cutoff.focus();
		
		return;  //  EARLY EXIT
		
	}
	

	
	var $scan_retention_time_qc_plot_max_x = $("#scan_retention_time_qc_plot_max_x");
	
	var $scan_retention_time_qc_plot_max_y = $("#scan_retention_time_qc_plot_max_y");
	
	
	if ( ! this.globals.currentSearchData ) {
		
		this.globals.currentSearchData = {};
	}
	
	if ( scanFileId != this.globals.currentSearchData.prevScanFileId ) {
		
		//  Different scan file so clear these values
		
		this.globals.currentSearchData.maxValuesInDB = undefined;  // clear values
		
		$scan_retention_time_qc_plot_max_x.val("");  // clear values
		$scan_retention_time_qc_plot_max_y.val("");  // clear values
	}
	
	this.globals.currentSearchData.prevScanFileId = scanFileId;
	
	

	var userInputMaxX = $scan_retention_time_qc_plot_max_x.val();
	var userInputMaxY = $scan_retention_time_qc_plot_max_y.val();

	if ( userInputMaxX !== "" ) {

		// only test for valid Max X value if not empty string

		if ( ! /^[+-]?((\d+(\.\d*)?)|(\.\d+))$/.test( userInputMaxX ) ) {
			
			//  Max X value is not a valid decimal number

			this.removeRetentionTimeCountChart();

			$(".scan_retention_time_qc_plot_param_not_a_number_jq").show();
			
			$scan_retention_time_qc_plot_max_x.focus();
			
			return;  //  EARLY EXIT
		}
	}
	
	if ( userInputMaxY !== "" ) {

		// only test for valid Max Y value if not empty string

		if ( ! /^[+-]?((\d+(\.\d*)?)|(\.\d+))$/.test( userInputMaxY ) ) {
			
			//  Max X value is not a valid decimal number
			

			this.removeRetentionTimeCountChart();

			$(".scan_retention_time_qc_plot_param_not_a_number_jq").show();
			
			$scan_retention_time_qc_plot_max_y.focus();
			
			return;  //  EARLY EXIT
		}
	}
	
	
	
	var scansForSelectedLinkTypes = [];

	var $scan_retention_time_qc_plot_scans_include_jq = $(".scan_retention_time_qc_plot_scans_include_jq");

	$scan_retention_time_qc_plot_scans_include_jq.each( function(   ) {

		var $thisCheckbox = $( this );
		
		if ( $thisCheckbox.prop('checked') ) {
			
			var checkboxValue = $thisCheckbox.attr("value");
		
			scansForSelectedLinkTypes.push( checkboxValue );
		}
	} );

	
	objectThis.createRetentionTimeCountChart( { 
		psmScoreCutoff : psmScoreCutoff,
		annotationTypeId : annotationTypeId,
		searchId : searchId,
		scanFileId : scanFileId, 
		scanFileName : scanFileName,
		scansForSelectedLinkTypes : scansForSelectedLinkTypes,
		userInputMaxX : userInputMaxX,
		userInputMaxY : userInputMaxY } );
};



//////////

///  

QCChartRetentionTime.prototype.removeRetentionTimeCountChart = function(  ) {

	var $scan_retention_time_qc_plot_chartDiv = $("#scan_retention_time_qc_plot_chartDiv");
	
	$scan_retention_time_qc_plot_chartDiv.empty();
};

//////////

///  

QCChartRetentionTime.prototype.createRetentionTimeCountChart = function( params ) {

	var objectThis = this;

	
	
	var searchId = params.searchId;
	var scanFileId = params.scanFileId;
	var annotationTypeId = params.annotationTypeId;
	var psmScoreCutoff = params.psmScoreCutoff;
	var scansForSelectedLinkTypes = params.scansForSelectedLinkTypes;
	var userInputMaxXString = params.userInputMaxX;
	
	if ( ! qcChartsInitialized ) {
		
		throw "qcChartsInitialized is false"; 
	}
	
	
	this.removeRetentionTimeCountChart();
	

	var _URL = contextPathJSVar + "/services/qcplot/getScanRetentionTime";
	
	
	
	var requestData = {
			scansForSelectedLinkTypes : scansForSelectedLinkTypes,
			searchId : searchId,
			scanFileId : scanFileId,
			annotationTypeId : annotationTypeId,
			psmScoreCutoff : psmScoreCutoff
	};
	
	if ( userInputMaxXString !== "" ) {
	
		requestData.retentionTimeInMinutesCutoff = userInputMaxXString;
	}

	
	

	// var request =
	$.ajax({
		type : "GET",
		url : _URL,
		data : requestData,

		traditional: true,  //  Force traditional serialization of the data sent
		//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
		//   So scansFor array is passed as "scansFor=<value>" which is what Jersey expects
		
		dataType : "json",
		success : function(data) {
			try {
				objectThis.createRetentionTimeCountChartResponse(requestData, data, params);
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

			// alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
			// textStatus: " + textStatus );
		}
	});
	
};

//////////

QCChartRetentionTime.prototype.createRetentionTimeCountChartResponse = function(requestData, responseData, originalParams) {

	var objectThis = this;

	
	
	var $scan_retention_time_qc_plot_chartDiv = $("#scan_retention_time_qc_plot_chartDiv");
	
	var scanFileName = originalParams.scanFileName;
	
	var userInputMaxXString = originalParams.userInputMaxX;
	var userInputMaxYString = originalParams.userInputMaxY;
	
	
	var userInputMaxX = undefined;
	
	if ( userInputMaxXString !== "" ) {

		// only test for valid Max X value if not empty string

		if ( /^[+-]?((\d+(\.\d*)?)|(\.\d+))$/.test( userInputMaxXString ) ) {
			
			//  Max X value is a valid decimal number

			userInputMaxX = parseFloat( userInputMaxXString );

			if ( isNaN( userInputMaxX ) ) {

				userInputMaxX = undefined;
			}
		}
	}
	
	var userInputMaxY = undefined;
	
	if ( userInputMaxYString !== "" ) {

		// only test for valid Max Y value if not empty string

		if ( /^[+-]?((\d+(\.\d*)?)|(\.\d+))$/.test( userInputMaxYString ) ) {
			
			//  Max X value is a valid decimal number

			userInputMaxY = parseFloat( userInputMaxYString );

			if ( isNaN( userInputMaxY ) ) {

				userInputMaxY = undefined;
			}
		}
	}
	

	var maxDataX = 0;
	var maxDataY = 0;
	

	var chartBuckets = responseData.chartBuckets;
	

	
	if ( chartBuckets.length === 0
			|| ( chartBuckets[ 0 ].binCenter === 0 ) ) {
		
		$(".scan_retention_time_qc_plot_no_data_jq").show();
		
		$(".scan_retention_time_qc_plot_have_data_jq").hide();
		
		return;  //  EARLY EXIT
	} else {
		
		$(".scan_retention_time_qc_plot_no_data_jq").hide();

		$(".scan_retention_time_qc_plot_have_data_jq").show();
	}
	
	
	

	//  chart data for Google charts
	var chartData = [];

	//  output columns specification
//	chartData.push( ["preMZ","count"] );

	//  With Tooltip
	chartData.push( ["retention time",
	                 "sub_count",{role: "tooltip",  'p': {'html': true} }, 
//	                 { role: 'style' } ,
	                 "total_count",{role: "tooltip",  'p': {'html': true} }
//	                 , { role: 'style' } 
	                 ] );

	for ( var index = 0; index < chartBuckets.length; index++ ) {

		var bucket = chartBuckets[ index ];
		

//		chartData.push( [bucket.binMiddle, bucket.totalCount  ] );
		
		
		//  not needed since send userInputMaxX to the server
//		if ( userInputMaxX && bucket.binEnd > userInputMaxX ) {
//			
//			//  Skip entries beyond userInputMaxX
//			
//			continue;   //  EARLY continue
//		}
		
		
		var tooltipTotalCount = bucket.totalCount; 

		var tooltipCountForPsmsThatMeetCriteria = bucket.countForPsmsThatMeetCriteria; 
		
		if ( userInputMaxY && bucket.totalCount > userInputMaxY ) {
			
			bucket.totalCount = userInputMaxY;
		}
		
		if ( userInputMaxY && bucket.countForPsmsThatMeetCriteria > userInputMaxY ) {
			
			bucket.countForPsmsThatMeetCriteria = userInputMaxY;
		}
		
		
		
		if ( bucket.binEnd > maxDataX ) {
			
			maxDataX = bucket.binEnd;
		}
		
		if ( bucket.totalCount > maxDataY ) {
			
			maxDataY = bucket.totalCount;
		}
		

		//  With Tooltip
		
		var totalScanCountBarHeight = bucket.totalCount - bucket.countForPsmsThatMeetCriteria;
		
		var tooltip = "<div style='margin: 10px;'>total scan count: " + tooltipTotalCount + 
		"<br>retention time approximately " + bucket.binStart + " to " + bucket.binEnd + "</div>";
		
		

		var countForPsmsThatMeetCriteriaTooltip = "<div style='margin: 10px;'>scan count Psms That Meet Criteria: " + tooltipCountForPsmsThatMeetCriteria + 
		"<br>retention time approximately " + bucket.binStart + " to " + bucket.binEnd + "</div>";

		chartData.push( [bucket.binCenter, 
		                 bucket.countForPsmsThatMeetCriteria, countForPsmsThatMeetCriteriaTooltip, 
//		                 'stroke-width: 2;stroke-color: red; ', 
		                 totalScanCountBarHeight, tooltip 
//		                 ,  'stroke-width: 2;stroke-color: blue; '
		                 ] );
	}
	
	
	
	var $scan_retention_time_qc_plot_max_x = $("#scan_retention_time_qc_plot_max_x");
	
	var $scan_retention_time_qc_plot_max_y = $("#scan_retention_time_qc_plot_max_y");
	
	if ( ! this.globals.currentSearchData ) {
		
		this.globals.currentSearchData = {};
	}
	
	if ( ! this.globals.currentSearchData.maxValuesInDB ) {
		
		this.globals.currentSearchData.maxValuesInDB = {};
	}
	
	if ( ! this.globals.currentSearchData.maxValuesInDB.maxX ) {
		
		this.globals.currentSearchData.maxValuesInDB.maxX = maxDataX;
		this.globals.currentSearchData.maxValuesInDB.maxY = maxDataY;
	}

	
	
	var userInputMaxXStringCurrentRead = $scan_retention_time_qc_plot_max_x.val();
	var userInputMaxYStringCurrentRead = $scan_retention_time_qc_plot_max_y.val();

	
	if ( userInputMaxXStringCurrentRead === "" ) {
		
		$scan_retention_time_qc_plot_max_x.val( this.globals.currentSearchData.maxValuesInDB.maxX );
	}
	
	if ( userInputMaxYStringCurrentRead === "" ) {
		
		$scan_retention_time_qc_plot_max_y.val( this.globals.currentSearchData.maxValuesInDB.maxY );
	}

	//  control the tick marks horizontal and vertical
	
	var hAxisTicks = objectThis.getRetentionTimeTickMarks( maxDataX );
	
	var vAxisTicks = objectThis.getRetentionTimeTickMarks( maxDataY );
	
	
	var chartTitle = 'Count vs/ Retention Time - ' + scanFileName;
	
	
	var optionsFullsize = {

			title: chartTitle, // Title above chart

			//  X axis label below chart
			hAxis: { title: 'Retention Time (minutes)', titleTextStyle: {color: 'black'}
						,ticks: hAxisTicks, format:'#,###'
						,maxValue : maxDataX
			},  
			//  Y axis label left of chart
			vAxis: { title: 'Count', titleTextStyle: {color: 'black'}
						,baseline: 0                    // always start at zero
						,ticks: vAxisTicks, format:'#,###'
						,maxValue : maxDataY

			},
			legend: { position: 'none' }, //  position: 'none':  Don't show legend of bar colors in upper right corner
			
			width : objectThis.RETENTION_TIME_COUNT_CHART_WIDTH, 
			height : objectThis.RETENTION_TIME_COUNT_CHART_HEIGHT,   // width and height of chart, otherwise controlled by enclosing div
			
//			bar: { groupWidth: 5 },  // set bar width large to eliminate space between bars
			bar: { groupWidth: '100%' },  // set bar width large to eliminate space between bars

//			colors: ['red','blue'],  //  Color of bars
			
			colors: ['#A55353','#FFF0F0'],  //  Color of bars, Proxl shades of red, total counts is second
			
			
			tooltip: {isHtml: true},
			isStacked: true

			,chartArea : { left : 140, top: 60, 
				width: objectThis.RETENTION_TIME_COUNT_CHART_WIDTH - 200 ,  //  was 720 as measured in Chrome
				height : objectThis.RETENTION_TIME_COUNT_CHART_HEIGHT - 120 }  //  was 530 as measured in Chrome
			
	};        
	
	
	// create the chart

	var data = google.visualization.arrayToDataTable( chartData );
	
	
	var chartFullsize = new google.visualization.ColumnChart( $scan_retention_time_qc_plot_chartDiv[0] );
	chartFullsize.draw(data, optionsFullsize);
	
	google.visualization.events.addListener(chartFullsize, 'select', function() {
		  
//		var tableSelection = chartFullsize.getSelection();
//		
//		var tableSelection0 = tableSelection[ 0 ];
//		
//		var column = tableSelection0.column;
//		var row = tableSelection0.row;
//		
//		var chartDataForRow = chartData[ row ];
//		  
//		  var z = 0;
	});
	
	var $scan_retention_time_qc_plot_overlay_container = $("#scan_retention_time_qc_plot_overlay_container");

	var $scan_retention_time_qc_plot_overlay_background = $("#scan_retention_time_qc_plot_overlay_background"); 
	$scan_retention_time_qc_plot_overlay_background.show();
	$scan_retention_time_qc_plot_overlay_container.show();

};




///////////

QCChartRetentionTime.prototype.getRetentionTimeTickMarks = function( maxValue ) {

//	var objectThis = this;

	var maxValueRoundDown = maxValue;
	
	maxValueRoundDown = Math.round( maxValue / 200 ) * 200;
	
	if ( maxValue < 1000 ) {
			
			maxValueRoundDown = Math.round( maxValue / 20 ) * 20;
	}
	
	
	if ( maxValue < 100 ) {
			
			maxValueRoundDown = Math.round( maxValue / 2 ) * 2;
	}

	var tickMarks = [ maxValueRoundDown  * 0.25, maxValueRoundDown  * 0.5, maxValueRoundDown * 0.75, maxValue ];

//	var tickMarks = [ maxValueRoundDown  * 0.2, maxValueRoundDown  * 0.4, maxValueRoundDown * 0.6, maxValueRoundDown * 0.8, maxValue ];
	
	return tickMarks;
	
};


///////////

QCChartRetentionTime.prototype.resetRetentionTimeCountChartMaxXMaxY = function() {

//	var objectThis = this;

	
	
	var $scan_retention_time_qc_plot_max_x = $("#scan_retention_time_qc_plot_max_x");
	
	var $scan_retention_time_qc_plot_max_y = $("#scan_retention_time_qc_plot_max_y");
	
	var $scan_retention_time_qc_plot_max_x_initial_value = $("#scan_retention_time_qc_plot_max_x_initial_value");
	var $scan_retention_time_qc_plot_max_y_initial_value = $("#scan_retention_time_qc_plot_max_y_initial_value");
	
	var scan_retention_time_qc_plot_max_x_initial_value = $scan_retention_time_qc_plot_max_x_initial_value.val();
	var scan_retention_time_qc_plot_max_y_initial_value = $scan_retention_time_qc_plot_max_y_initial_value.val();

	$scan_retention_time_qc_plot_max_x.val( scan_retention_time_qc_plot_max_x_initial_value );
	$scan_retention_time_qc_plot_max_y.val( scan_retention_time_qc_plot_max_y_initial_value );

};
