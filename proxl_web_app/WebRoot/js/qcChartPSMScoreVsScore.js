
//          qcChartPSMScoreVsScore.js

//  Quality Control chart  PSM Score vs Score

//  Javascript for the viewProject.jsp page

//////////////////////////////////
// JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

//  CONSTANTS

var PSM_SCORE_VS_SCORE_CHART_BIN_START_OR_END_TO_FIXED_VALUE = 4;
var PSM_SCORE_VS_SCORE_CHART_PERCENTAGE_OF_MAX_TO_FIXED_VALUE = 1;
var PSM_SCORE_VS_SCORE_CHART_PERCENTAGE_OF_MAX_TO_FIXED_VALUE_OUTPUT_FILE = 4;
var PSM_SCORE_VS_SCORE_CHART_COMPARISON_DIRECTION_STRING_ABOVE_ASCII = ">=";
var PSM_SCORE_VS_SCORE_CHART_COMPARISON_DIRECTION_STRING_BELOW_ASCII = "<=";
var PSM_SCORE_VS_SCORE_CHART_COMPARISON_DIRECTION_STRING_ABOVE = "\u2265"; // ">=" as a single character
var PSM_SCORE_VS_SCORE_CHART_COMPARISON_DIRECTION_STRING_BELOW = "\u2264"; // "<=" as a single character
var PSM_SCORE_VS_SCORE_CHART_WIDTH = 920;
var PSM_SCORE_VS_SCORE_CHART_HEIGHT = 650;
var RELOAD_PSM_SCORE_VS_SCORE_CHART_TIMER_DELAY = 400;  // in Milliseconds


var qcChartPSMScoreVsScoreInitialized = false;

///   Called when Google charts is initialized
function initQCChartPSMScoreVsScore() {
	qcChartPSMScoreVsScoreInitialized = true;
};

///   Called from viewProjectPage.js  initPage()  when page is ready
function initQCPlotPSMScoreVsScoreClickHandlers() {	
	qcChartPSMScoreVsScores.init();
}

//  Constructor for QC chart for PSM Q Values
var QCChartPSMScoreVsScores = function(  ) {
	this.globals = {
			currentSearchData : 
				{ 
				maxValuesInDB : {
					maxX : undefined,
					maxY : undefined
				}
			},
			prevYAxisChoice : undefined,
			chartDataFromServer : undefined
	};
};

///////////
//  Create an instance from the constructor
var qcChartPSMScoreVsScores = new QCChartPSMScoreVsScores();

//  CONSTANTS
QCChartPSMScoreVsScores.prototype.PSM_SCORE_VS_SCORE_CHART_WIDTH = PSM_SCORE_VS_SCORE_CHART_WIDTH;
QCChartPSMScoreVsScores.prototype.PSM_SCORE_VS_SCORE_CHART_HEIGHT = PSM_SCORE_VS_SCORE_CHART_HEIGHT;
QCChartPSMScoreVsScores.prototype.RELOAD_PSM_SCORE_VS_SCORE_CHART_TIMER_DELAY = RELOAD_PSM_SCORE_VS_SCORE_CHART_TIMER_DELAY;  // in Milliseconds

//////////
QCChartPSMScoreVsScores.prototype.init = function() {
	var objectThis = this;
	$(".qc_plot_psm_score_vs_score_link_jq").click(function(eventObject) {
		try {
			var clickThis = this;
			objectThis.psmScoreVsScoreQCPlotClickHandler( clickThis, eventObject );
			return false;
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});	 	
	$(".psm_score_vs_score_qc_plot_overlay_close_parts_jq").click(function(eventObject) {
		try {
			var clickThis = this;
			if ( objectThis.reloadPSMScoreVsScoreChartTimerId ) {
				clearTimeout( objectThis.reloadPSMScoreVsScoreChartTimerId );
				objectThis.reloadPSMScoreVsScoreChartTimerId = null;
			}
			objectThis.closePSMScoreVsScoreQCPlotOverlay( clickThis, eventObject );
			return false;
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});
	$("#psm_score_vs_score_qc_plot_score_type_id_1").change(function(eventObject) {
		try {
			objectThis.scoreTypeChanged( );
			return false;
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});
	$("#psm_score_vs_score_qc_plot_score_type_id_2").change(function(eventObject) {
		try {
			objectThis.scoreTypeChanged( );
			return false;
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});
	$(".psm_score_vs_score_qc_plot_on_change_jq").change(function(eventObject) {
		try {
			objectThis.createChartFromPageParams( );
			return false;
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});
	$(".psm_score_vs_score_qc_plot_on_change_jq").keyup(function(eventObject) {
		try {
			if ( objectThis.reloadPSMScoreVsScoreChartTimerId ) {
				clearTimeout( objectThis.reloadPSMScoreVsScoreChartTimerId );
				objectThis.reloadPSMScoreVsScoreChartTimerId = null;
			}
			objectThis.reloadPSMScoreVsScoreChartTimerId = setTimeout( function() {
				try {
					objectThis.createChartFromPageParams( );
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
				}, objectThis.RELOAD_PSM_SCORE_VS_SCORE_CHART_TIMER_DELAY );
			return false;
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
	$("#psm_score_vs_score_qc_plot_max_reset_button").click(function(eventObject) {
		try {
			objectThis.resetMaxXMaxY();
			objectThis.createChartFromPageParams( );
			return false;
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
		});

	$( "#psm_score-svg-download-jpeg" ).click( function() { objectThis.downloadSvg( 'jpeg' ); });
	$( "#psm_score-svg-download-png" ).click( function() { objectThis.downloadSvg( 'png' ); });
	$( "#psm_score-svg-download-pdf" ).click( function() { objectThis.downloadSvg( 'pdf' ); });
	$( "#psm_score-svg-download-svg" ).click( function() { objectThis.downloadSvg( 'svg' ); });
	
	$( "#psm_score-svg-download-data" ).click( function() { objectThis.downloadData( ); });
	
};


QCChartPSMScoreVsScores.prototype.downloadData = function( ) {
	
	var objectThis = this;
	
	try {
		if ( this.globals === undefined || this.globals.chartDataFromServer === undefined ) {
			//  No stored chart data from server so exit
			return;  //  EARLY EXIT
		}
		
		var crosslinkChartData = this.globals.chartDataFromServer.crosslinkChartData;
		var looplinkChartData = this.globals.chartDataFromServer.looplinkChartData;
		var unlinkedChartData = this.globals.chartDataFromServer.unlinkedChartData;

		var $psm_score_vs_score_qc_plot_current_search_id = $("#psm_score_vs_score_qc_plot_current_search_id");
		var searchId = $psm_score_vs_score_qc_plot_current_search_id.val( );
		
		//  Selected Annotation Text
		var selectedAnnotationTypeText_1 = $("#psm_score_vs_score_qc_plot_score_type_id_1 option:selected").text();
		var selectedAnnotationTypeText_2 = $("#psm_score_vs_score_qc_plot_score_type_id_2 option:selected").text();
		
		var headerLine = "search id\tpsm id\ttype\t" + selectedAnnotationTypeText_1 + "\t" + selectedAnnotationTypeText_2;

		var outputStringArray = new Array();
		
		outputStringArray.push( headerLine );
		
		var processForType = function( params ) {
			
			var dataForType = params.dataForType;
			var linkTypeLabel = params.linkTypeLabel;
			
			for ( var index = 0; index < dataForType.length; index++ ) {
			
				var dataForTypeEntry = dataForType[ index ];

				var outputType = linkTypeLabel;

				var outputScore_1_Value = dataForTypeEntry.score_1.toFixed( PSM_SCORE_VS_SCORE_CHART_BIN_START_OR_END_TO_FIXED_VALUE );
				var outputScore_2_Value = dataForTypeEntry.score_2.toFixed( PSM_SCORE_VS_SCORE_CHART_BIN_START_OR_END_TO_FIXED_VALUE );
				
				var outputLine = 
					searchId + 
					"\t" + dataForTypeEntry.psmId + 
					"\t" + outputType + 
					"\t" + outputScore_1_Value + 
					"\t" + outputScore_2_Value ;
				
				outputStringArray.push( outputLine );
			}
		};
		

		if ( crosslinkChartData ) {
			processForType( { dataForType: crosslinkChartData, linkTypeLabel: "crosslink" }  );
		}
		if ( looplinkChartData ) {
			processForType( { dataForType: looplinkChartData, linkTypeLabel: "looplink" }  );
		}
		if ( unlinkedChartData ) {
			processForType( { dataForType: unlinkedChartData, linkTypeLabel: "unlinked" }  );
		}
		
		var content = outputStringArray.join("\n");
		
		var filename = "proxlPsmScoreScoreData.txt"
		var mimetype = "text/plain";
		
		downloadStringAsFile( filename, mimetype, content )
		
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
	
};


QCChartPSMScoreVsScores.prototype.downloadSvg = function( type ) {
	
	try {
		
		var form = document.createElement( "form" );
		
		$( form ).hide();
		
	    form.setAttribute( "method", "post" );
	    form.setAttribute( "action", contextPathJSVar + "/convertAndDownloadSVG.do" );

		var $svg_image_inner_container_div__svg_merged_image_svg_jq = $( "#psm_score_vs_score_qc_plot_chartDiv svg " );
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
	    
	    // Make type "hidden" to not be limited by default maxlength in browser
	    svgStringField.setAttribute("type", "hidden");   

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
QCChartPSMScoreVsScores.prototype.psmScoreVsScoreQCPlotClickHandler = function(clickThis, eventObject) {
	var objectThis = this;
	objectThis.openPSMScoreVsScoreQCPlotOverlay(clickThis, eventObject);
	return;
};

///////////
QCChartPSMScoreVsScores.prototype.closePSMScoreVsScoreQCPlotOverlay = function(clickThis, eventObject) {
	$(".psm_score_vs_score_qc_plot_overlay_show_hide_parts_jq").hide();
};

///////////
QCChartPSMScoreVsScores.prototype.openPSMScoreVsScoreQCPlotOverlay = function(clickThis, eventObject) {
//	var objectThis = this;
	var $clickThis = $(clickThis);
	//	get root div for this search
	var $search_root_jq = $clickThis.closest(".search_root_jq");
	var searchId = $search_root_jq.attr("searchId");	
	// copy the search name to the overlay
	var $search_name_display_jq = $search_root_jq.find(".search_name_display_jq");
	var search_name_display_jq = $search_name_display_jq.text();
	var $search_number_in_parens_display_jq = $search_root_jq.find(".search_number_in_parens_display_jq");
	var search_number_in_parens_display_jq = $search_number_in_parens_display_jq.text();
	var searchNameAndNumberInParens = search_name_display_jq + " " + search_number_in_parens_display_jq;
	var $psm_score_vs_score_qc_plot_current_search_name_and_id = $("#psm_score_vs_score_qc_plot_current_search_name_and_id");
	$psm_score_vs_score_qc_plot_current_search_name_and_id.val( searchNameAndNumberInParens );
	
	// Position dialog over clicked link
	//  get position of div containing the dialog that is inline in the page
	var $psm_score_vs_score_qc_plot_overlay_containing_outermost_div_inline_div = $("#psm_score_vs_score_qc_plot_overlay_containing_outermost_div_inline_div");
	var offset__containing_outermost_div_inline_div = $psm_score_vs_score_qc_plot_overlay_containing_outermost_div_inline_div.offset();
	var offsetTop__containing_outermost_div_inline_div = offset__containing_outermost_div_inline_div.top;
	var $psm_score_vs_score_qc_plot_overlay_container = $("#psm_score_vs_score_qc_plot_overlay_container");
	var scrollTopWindow = $(window).scrollTop();
	var positionAdjust = scrollTopWindow - offsetTop__containing_outermost_div_inline_div + 10;
	$psm_score_vs_score_qc_plot_overlay_container.css( "top", positionAdjust );
	var $psm_score_vs_score_qc_plot_overlay_container = $("#psm_score_vs_score_qc_plot_overlay_container");
	var $psm_score_vs_score_qc_plot_overlay_background = $("#psm_score_vs_score_qc_plot_overlay_background"); 
	$psm_score_vs_score_qc_plot_overlay_background.show();
	$psm_score_vs_score_qc_plot_overlay_container.show();
	////////////////
	var $psm_score_vs_score_qc_plot_current_search_id = $("#psm_score_vs_score_qc_plot_current_search_id");
	var prevSearchId = $psm_score_vs_score_qc_plot_current_search_id.val( );
	if ( prevSearchId === searchId ) {
		////  Same Search Id as when last opened so just show it
		var $psm_score_vs_score_qc_plot_overlay_background = $("#psm_score_vs_score_qc_plot_overlay_background"); 
		$psm_score_vs_score_qc_plot_overlay_background.show();
		$psm_score_vs_score_qc_plot_overlay_container.show();
		return;  //  EARLY EXIT
	}

	this.globals.currentSearchData = undefined;   ///  Reset "currentSearchData" for new search
	this.populateCutoffsOnImportMessage( { $search_root_jq: $search_root_jq } );
	$psm_score_vs_score_qc_plot_current_search_id.val( searchId );
	
	this.getPSMFilterableAnnTypesForSearchId( { 
		searchId: searchId
	} );
};

//////////
QCChartPSMScoreVsScores.prototype.populateCutoffsOnImportMessage = function( params ) {
	var $search_root_jq = params.$search_root_jq;
	var $qc_plots_links_filtered_on_import_message_jq = $search_root_jq.find(".qc_plots_links_filtered_on_import_message_jq");
	var qc_plots_links_filtered_on_import_message_jqHTML = $qc_plots_links_filtered_on_import_message_jq.html();
	var $psm_score_vs_score_qc_plot_score_cutoffs_on_import_row = $("#psm_score_vs_score_qc_plot_score_cutoffs_on_import_row");
	var $psm_score_vs_score_qc_plot_score_cutoffs_on_import_anns_values = $("#psm_score_vs_score_qc_plot_score_cutoffs_on_import_anns_values");
	if ( qc_plots_links_filtered_on_import_message_jqHTML === "" ) {
		$psm_score_vs_score_qc_plot_score_cutoffs_on_import_row.hide();
		return;
	}
	$psm_score_vs_score_qc_plot_score_cutoffs_on_import_row.show();
	$psm_score_vs_score_qc_plot_score_cutoffs_on_import_anns_values.html( qc_plots_links_filtered_on_import_message_jqHTML );
};

///  
QCChartPSMScoreVsScores.prototype.getPSMFilterableAnnTypesForSearchId = function( params ) {
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
			objectThis.getPSMFilterableAnnTypesForSearchIdResponse(requestData, data, params);
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
QCChartPSMScoreVsScores.prototype.getPSMFilterableAnnTypesForSearchIdResponse = function(requestData, responseData, originalParams) {
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
	var annTypes = this.globals.currentSearchData.annotationTypeDataArray;
	var $psm_score_vs_score_qc_plot_score_type_id_1 = $("#psm_score_vs_score_qc_plot_score_type_id_1");
	$psm_score_vs_score_qc_plot_score_type_id_1.empty();
	var $psm_score_vs_score_qc_plot_score_type_id_2 = $("#psm_score_vs_score_qc_plot_score_type_id_2");
	$psm_score_vs_score_qc_plot_score_type_id_2.empty();
	var optionsHTMLarray = [];
	for ( var annTypesIndex = 0; annTypesIndex < annTypes.length; annTypesIndex++ ) {
		var annType = annTypes[ annTypesIndex ];
		var html = "<option value='" + annType.id + "'>" + annType.name +
			" (" + annType.searchProgramName + ")" +
			"</option>";
		optionsHTMLarray.push( html );
	}
	var optionsHTML = optionsHTMLarray.join("");
	$psm_score_vs_score_qc_plot_score_type_id_1.append( optionsHTML );
	$psm_score_vs_score_qc_plot_score_type_id_2.append( optionsHTML );
	//  If an annotation type record has sort id of 1, then assign that annotation type id to the selector 
	for ( var annTypesIndex = 0; annTypesIndex < annTypes.length; annTypesIndex++ ) {
		var annType = annTypes [ annTypesIndex ];
		if ( annType.annotationTypeFilterableDTO && annType.annotationTypeFilterableDTO.sortOrder === 1 ) {
			$psm_score_vs_score_qc_plot_score_type_id_1.val( annType.id );
//			$psm_score_vs_score_qc_plot_score_type_id_2.val( annType.id );
		}
	}
	this.createChartFromPageParams( { 
		searchId: originalParams.searchId
	} );
};



/////////////
QCChartPSMScoreVsScores.prototype.scoreTypeChanged = function( ) {
//	var objectThis = this;
	var annTypes = this.globals.currentSearchData.annotationTypeDataById;
	var $psm_score_vs_score_qc_plot_score_type_id_1 = $("#psm_score_vs_score_qc_plot_score_type_id_1");
	var selectedAnnTypeId = $psm_score_vs_score_qc_plot_score_type_id_1.val( );
	var annTypeForSelectId = annTypes[ selectedAnnTypeId ];
	if ( annTypeForSelectId === undefined || annTypeForSelectId === null ) {
		throw "annType not found for id: " + selectedAnnTypeId;
	}
	var $psm_score_vs_score_qc_plot_max_x = $("#psm_score_vs_score_qc_plot_max_x");
	var $psm_score_vs_score_qc_plot_max_y = $("#psm_score_vs_score_qc_plot_max_y");
	$psm_score_vs_score_qc_plot_max_x.val("");
	$psm_score_vs_score_qc_plot_max_y.val("");
	this.createChartFromPageParams( );
};

//////////
///  
///  
QCChartPSMScoreVsScores.prototype.createChartFromPageParams = function( ) {
	var objectThis = this;
	if ( objectThis.reloadPSMScoreVsScoreChartTimerId ) {
		clearTimeout( objectThis.reloadPSMScoreVsScoreChartTimerId );
		objectThis.reloadPSMScoreVsScoreChartTimerId = null;
	}
	if ( ! this.globals.currentSearchData ) {
		this.globals.currentSearchData = {};
	}
	var $psm_score_vs_score_qc_plot_max_x = $("#psm_score_vs_score_qc_plot_max_x");
	var $psm_score_vs_score_qc_plot_max_y = $("#psm_score_vs_score_qc_plot_max_y");
	if ( ! this.globals.currentSearchData ) {
		this.globals.currentSearchData = {};
	}
	/////////////
	//  if Y Axis choice has changed, clear Y Axis Max input field
	if ( ( $("#psm_score_vs_score_qc_plot_y_axis_as_percentage").prop("checked")
			&& this.globals.prevYAxisChoice !== this.Y_AXIS_CHOICE_PERCENTAGE )
			|| ( $("#psm_score_vs_score_qc_plot_y_axis_as_raw_counts").prop("checked")
					&& this.globals.prevYAxisChoice !== this.Y_AXIS_CHOICE_RAW_COUNTS ) ) {
			$psm_score_vs_score_qc_plot_max_y.val( "" );
	}
	//  Set this.globals.prevYAxisChoice per current selected Y Axis choice
	if ( $("#psm_score_vs_score_qc_plot_y_axis_as_percentage").prop("checked") ) {
		this.globals.prevYAxisChoice = this.Y_AXIS_CHOICE_PERCENTAGE;
	}
	if ( $("#psm_score_vs_score_qc_plot_y_axis_as_raw_counts").prop("checked") ) {
		this.globals.prevYAxisChoice = this.Y_AXIS_CHOICE_RAW_COUNTS;
	}
	/////////////
	var userInputMaxX = $psm_score_vs_score_qc_plot_max_x.val();
	var userInputMaxY = $psm_score_vs_score_qc_plot_max_y.val();
	if ( userInputMaxX !== "" ) {
		// only test for valid Max X value if not empty string
		if ( !  /^[+-]?((\d+(\.\d*)?)|(\.\d+))$/.test( userInputMaxX ) ) {
			//  Max X value is not a valid decimal number
			$(".psm_score_vs_score_qc_plot_param_not_a_number_jq").show();
			$(".psm_score_vs_score_qc_plot_no_data_jq").hide();
			$(".psm_score_vs_score_qc_plot_have_data_jq").hide();
			$psm_score_vs_score_qc_plot_max_x.focus();
			return;  //  EARLY EXIT
		}
//		if ( userInputMaxXNum < 0 ) {
//			
//			$psm_score_vs_score_qc_plot_max_x.val( "0" );
//			
//			userInputMaxX = "0";
//		}
	}	
	if ( userInputMaxY !== "" ) {
		// only test for valid Max Y value if not empty string
		if ( !  /^[+-]?((\d+(\.\d*)?)|(\.\d+))$/.test( userInputMaxY ) ) {
			//  Max Y value is not a valid decimal number
			$(".psm_score_vs_score_qc_plot_param_not_a_number_jq").show();
			$(".psm_score_vs_score_qc_plot_no_data_jq").hide();
			$(".psm_score_vs_score_qc_plot_have_data_jq").hide();
			$psm_score_vs_score_qc_plot_max_y.focus();
			return;  //  EARLY EXIT
		}
		if ( $("#psm_score_vs_score_qc_plot_y_axis_as_percentage").prop("checked") ) {
			var userInputMaxYNum = parseFloat( userInputMaxY ); 
			if ( userInputMaxYNum < 0 ) {
				$psm_score_vs_score_qc_plot_max_y.val( "0" );
				userInputMaxY = "0";
			}
			if ( userInputMaxYNum > 100 ) {
				$psm_score_vs_score_qc_plot_max_y.val( "100" );
				userInputMaxY = "100";
			}
		}
	}	
	var $psm_score_vs_score_qc_plot_current_search_id = $("#psm_score_vs_score_qc_plot_current_search_id");
	var searchId = $psm_score_vs_score_qc_plot_current_search_id.val( );
	var $psm_score_vs_score_qc_plot_score_type_id_1 = $("#psm_score_vs_score_qc_plot_score_type_id_1");
	var annotationTypeId_1 = $psm_score_vs_score_qc_plot_score_type_id_1.val();
	var selectedAnnotationType_1_Text = $("#psm_score_vs_score_qc_plot_score_type_id_1 option:selected").text();
	var $psm_score_vs_score_qc_plot_score_type_id_2 = $("#psm_score_vs_score_qc_plot_score_type_id_2");
	var annotationTypeId_2 = $psm_score_vs_score_qc_plot_score_type_id_2.val();
	var selectedAnnotationType_2_Text = $("#psm_score_vs_score_qc_plot_score_type_id_2 option:selected").text();

	var selectedLinkTypes = this._getLinkTypesChecked();
	if ( selectedLinkTypes.length === 0 ) {
		$(".psm_score_vs_score_qc_plot_no_data_jq").show();
		$(".psm_score_vs_score_qc_plot_param_not_a_number_jq").hide();
		$(".psm_score_vs_score_qc_plot_have_data_jq").hide();
		return;  //  EARLY EXIT		
	}
	
	var createChartParams =  {  
			searchId : searchId,
			annotationTypeId_1 : annotationTypeId_1,
			selectedAnnotationType_1_Text : selectedAnnotationType_1_Text,
			annotationTypeId_2 : annotationTypeId_2,
			selectedAnnotationType_2_Text : selectedAnnotationType_2_Text,
			selectedLinkTypes : selectedLinkTypes,
			userInputMaxX : userInputMaxX,
			userInputMaxY : userInputMaxY };
	
	objectThis.createChart( createChartParams );
};

//////////
QCChartPSMScoreVsScores.prototype._getLinkTypesChecked = function(  ) {
	var selectedLinkTypes = [];
	var $psm_score_vs_score_qc_plot_link_type_include_jq = $(".psm_score_vs_score_qc_plot_link_type_include_jq");
	$psm_score_vs_score_qc_plot_link_type_include_jq.each( function(   ) {
		var $thisCheckbox = $( this );
		if ( $thisCheckbox.prop('checked') ) {
			var checkboxValue = $thisCheckbox.attr("value");
			selectedLinkTypes.push( checkboxValue );
		}
	} );
	return selectedLinkTypes;
};

//////////
///  
QCChartPSMScoreVsScores.prototype.createChart = function( params ) {
	var objectThis = this;
	var searchId = params.searchId;
	var selectedLinkTypes = params.selectedLinkTypes;
	var annotationTypeId_1 = params.annotationTypeId_1;
	var annotationTypeId_2 = params.annotationTypeId_2;
	var userInputMaxXString = params.userInputMaxX;
	var userInputMaxYString = params.userInputMaxY;
	
	if ( ! qcChartPSMScoreVsScoreInitialized ) {
		throw "qcChartPSMScoreVsScoreInitialized is false"; 
	}
	var $psm_score_vs_score_qc_plot_chartDiv = $("#psm_score_vs_score_qc_plot_chartDiv");
	$psm_score_vs_score_qc_plot_chartDiv.empty();
	var _URL = contextPathJSVar + "/services/qcplot/getPsmScoreVsScore";
	var requestData = {
			selectedLinkTypes : selectedLinkTypes,
			searchId : searchId,
			annotationTypeId_1 : annotationTypeId_1,
			annotationTypeId_2 : annotationTypeId_2
	};
	if ( userInputMaxXString !== "" ) {
		var psmScoreCutoff_1 = userInputMaxXString;
		requestData.psmScoreCutoff_1 = psmScoreCutoff_1;
	}
	if ( userInputMaxYString !== "" ) {
		var psmScoreCutoff_2 = userInputMaxYString;
		requestData.psmScoreCutoff_2 = psmScoreCutoff_2;
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
			objectThis.createChartResponse(requestData, data, params);
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
QCChartPSMScoreVsScores.prototype.createChartResponse = function(requestData, responseData, originalParams) {
	var objectThis = this;
	var $psm_score_vs_score_qc_plot_chartDiv = $("#psm_score_vs_score_qc_plot_chartDiv");
	var selectedAnnotationType_1_Text = originalParams.selectedAnnotationType_1_Text;
	var selectedAnnotationType_2_Text = originalParams.selectedAnnotationType_2_Text;
	var userInputMaxXString = originalParams.userInputMaxX;
	var userInputMaxYString = originalParams.userInputMaxY;
	var userInputMaxX = undefined;
	if ( userInputMaxXString !== "" ) {
		userInputMaxX = parseFloat( userInputMaxXString );
		if ( isNaN( userInputMaxX ) ) {
			userInputMaxX = undefined;
		}
	}
	var userInputMaxY = undefined;
	if ( userInputMaxYString !== "" ) {
		userInputMaxY = parseFloat( userInputMaxYString );
		if ( isNaN( userInputMaxY ) ) {
			userInputMaxY = undefined;
		}
	}
	var chartDataParam = responseData;
	
	this.globals.chartDataFromServer = chartDataParam;
	
	var crosslinkChartData = chartDataParam.crosslinkChartData;
	var looplinkChartData = chartDataParam.looplinkChartData;
	var unlinkedChartData = chartDataParam.unlinkedChartData;
	
	if ( ( crosslinkChartData && crosslinkChartData.length > 0 )
			|| ( looplinkChartData && looplinkChartData.length > 0 )
			|| ( unlinkedChartData && unlinkedChartData.length > 0 ) ) {

		$(".psm_score_vs_score_qc_plot_no_data_jq").hide();
		$(".psm_score_vs_score_qc_plot_param_not_a_number_jq").hide();
		$(".psm_score_vs_score_qc_plot_have_data_jq").show();
	} else {
		$(".psm_score_vs_score_qc_plot_no_data_jq").show();
		$(".psm_score_vs_score_qc_plot_have_data_jq").hide();
		$(".psm_score_vs_score_qc_plot_param_not_a_number_jq").hide();
		return;  //  EARLY EXIT
	}

	var haveCrosslinkData = false;
	var haveLooplinkData = false;
	var haveUnlinkedData = false;
	
	if ( crosslinkChartData && crosslinkChartData.length > 0 ) {
		haveCrosslinkData = true;
	}
	if ( looplinkChartData && looplinkChartData.length > 0 ) {
		haveLooplinkData = true;
	}
	if ( unlinkedChartData && unlinkedChartData.length > 0 ) {
		haveUnlinkedData = true;
	}
	
//	<xs:simpleType name="filter_direction_type">
//	The direction a filterable annotation type is sorted in.  
//	If set to "below", attributes with lower values are considered more significant (such as in the case of p-values). 
//	If set to "above", attributes with higher values are considered more significant (such as in the case of XCorr).

	//  chart data for Google charts
	var chartData = [];
	
	var pointColors = [];
	
	//  output columns specification
	
	var chartDataHeaderEntry = [ "PSM_X" ];
	if ( unlinkedChartData) {
		chartDataHeaderEntry.push( "unlinked" );
//		chartDataHeaderEntry.push( [ "PSM_Y", "unlinked", {role: "tooltip", 'p': {'html': true} } ] );
		pointColors.push( '#c69200' );	//	
	}
	if ( looplinkChartData) {
		chartDataHeaderEntry.push( "looplink" );
//		chartDataHeaderEntry.push( [ "PSM_Y", "looplink", {role: "tooltip", 'p': {'html': true} } ] );
		pointColors.push( '#006d34' );	//	
	}
	if ( haveCrosslinkData) {
		chartDataHeaderEntry.push( "crosslink" );
//		chartDataHeaderEntry.push( [ "PSM_Y", "crosslink", {role: "tooltip", 'p': {'html': true} } ] );
		pointColors.push( '#991999' );	//	
	}
	//  loop, unlinked, cross 
//	#006d34  #c69200  #991999 

	//  	red: #cc6666 , green: #8f9b70 , blue: #8099e1 
	
	chartData.push( chartDataHeaderEntry );

//	chartDataHeaderEntry.push( "crosslink" );
//	chartDataHeaderEntry.push( {role: "tooltip", 'p': {'html': true} }  ); 
	
	
	if ( haveUnlinkedData ) {
		for ( var index = 0; index < unlinkedChartData.length; index++ ) {
			var chartDataItem = unlinkedChartData[ index ];
			var chartEntry = [ chartDataItem.score_1, chartDataItem.score_2 ];
			if ( haveLooplinkData ) {
				chartEntry.push( null );
			}
			if ( haveCrosslinkData ) {
				chartEntry.push( null );
			}
			chartData.push( chartEntry );
		}
	}

	if ( haveLooplinkData ) {
		for ( var index = 0; index < looplinkChartData.length; index++ ) {
			var chartDataItem = looplinkChartData[ index ];
			var chartEntry = [ chartDataItem.score_1 ];
			if ( haveUnlinkedData ) {
				chartEntry.push( null );
			}
			chartEntry.push( chartDataItem.score_2 );
			if ( haveCrosslinkData ) {
				chartEntry.push( null );
			}
			chartData.push( chartEntry );
		}
	}
	if ( haveCrosslinkData ) {
		for ( var index = 0; index < crosslinkChartData.length; index++ ) {
			var chartDataItem = crosslinkChartData[ index ];
			var chartEntry = [ chartDataItem.score_1 ];
			if ( haveUnlinkedData ) {
				chartEntry.push( null );
			}
			if ( haveLooplinkData ) {
				chartEntry.push( null );
			}
			chartEntry.push( chartDataItem.score_2 );
			chartData.push( chartEntry );
		}
	}
	
	var $psm_score_vs_score_qc_plot_current_search_name_and_id = $("#psm_score_vs_score_qc_plot_current_search_name_and_id");
	var searchNameAndNumberInParens = 
		$psm_score_vs_score_qc_plot_current_search_name_and_id.val( );
	var chartTitle = "PSM " + selectedAnnotationType_1_Text + " vs " + selectedAnnotationType_2_Text + "\n" + searchNameAndNumberInParens;

	var xAxisLabel = "PSM Score: " + selectedAnnotationType_1_Text;
	var yAxisLabel = "PSM Score: " + selectedAnnotationType_2_Text;

	var optionsFullsize = {
			title: chartTitle, // Title above chart
			//  X axis label below chart
			hAxis: { title: selectedAnnotationType_1_Text, titleTextStyle: {color: 'black'}
//						,ticks: [ 0.0, 0.2, 0.4, 0.6, 0.8, 1.0 ], format:'#.##'
//							,minValue: -.05
//						,maxValue : maxDataX
			},  
			//  Y axis label left of chart
			vAxis: { title: yAxisLabel, titleTextStyle: {color: 'black'}
						,baseline: 0                    // always start at zero
//						,ticks: vAxisTicks, format:'#,###'
//						,maxValue : 10
			},
//			legend: { position: 'none' }, //  position: 'none':  Don't show legend of bar colors in upper right corner
			legend: { position: 'top', alignment: 'end', textStyle: { fontSize : 14 } },
//			legend.alignment	
//				Alignment of the legend. Can be one of the following:
//	
//				'start' - Aligned to the start of the area allocated for the legend.
//				'center' - Centered in the area allocated for the legend.
//				'end' - Aligned to the end of the area allocated for the legend.
//				Start, center, and end are relative to the style -- vertical or horizontal -- of the legend. 
//				For example, in a 'right' legend, 'start' and 'end' are at the top and bottom, respectively; 
//					for a 'top' legend, 'start' and 'end' would be at the left and right of the area, 
//					respectively.
//	
//				The default value depends on the legend's position. For 'bottom' legends, the default is 'center'; other legends default to 'start'.
//	
//				Type: string
//				Default: automatic
//			legend.position	
//				Position of the legend. Can be one of the following:
//	
//				'bottom' - Below the chart.
//				'left' - To the left of the chart, provided the left axis has no series associated with it. 
//							So if you want the legend on the left, use the option targetAxisIndex: 1.
//				'in' - Inside the chart, by the top left corner.
//				'none' - No legend is displayed.
//				'right' - To the right of the chart. Incompatible with the vAxes option.
//				'top' - Above the chart.
//				Type: string
//				Default: 'right'
//			legend.textStyle	
//			An object that specifies the legend text style. The object has this format:
//
//			{ color: <string>,
//			  fontName: <string>,
//			  fontSize: <number>,
//			  bold: <boolean>,
//			  italic: <boolean> }
			width : objectThis.PSM_SCORE_VS_SCORE_CHART_WIDTH, 
			height : objectThis.PSM_SCORE_VS_SCORE_CHART_HEIGHT,   // width and height of chart, otherwise controlled by enclosing div
//			bar: { groupWidth: 5 },  // set bar width large to eliminate space between bars
//			bar: { groupWidth: '100%' },  // set bar width large to eliminate space between bars
//			colors: ['red','blue'],  //  Color of bars
			colors: pointColors,  //  Color of points
			dataOpacity: .6,  // Opacity of points
//			red: #A55353
//			green: #53a553
//			blue: #5353a5
//			combined: #a5a5a5 (gray)
			tooltip: {isHtml: true},
//			isStacked: true
			chartArea : { left : 140, top: 60, 
				width: objectThis.PSM_SCORE_VS_SCORE_CHART_WIDTH - 200 ,  //  was 720 as measured in Chrome
				height : objectThis.PSM_SCORE_VS_SCORE_CHART_HEIGHT - 120 }  //  was 530 as measured in Chrome
	};        
	if ( userInputMaxY ) {
		//  Data points with Y axis > userInputMaxY will not be shown
		optionsFullsize.vAxis.viewWindow = { max : userInputMaxY };
	}
	var $psm_score_vs_score_qc_plot_overlay_container = $("#psm_score_vs_score_qc_plot_overlay_container");
	var $psm_score_vs_score_qc_plot_overlay_background = $("#psm_score_vs_score_qc_plot_overlay_background"); 
	$psm_score_vs_score_qc_plot_overlay_background.show();
	$psm_score_vs_score_qc_plot_overlay_container.show();
	// create the chart
	var data = google.visualization.arrayToDataTable( chartData );
	var chartFullsize = new google.visualization.ScatterChart( $psm_score_vs_score_qc_plot_chartDiv[0] );
	chartFullsize.draw( data, optionsFullsize );
	google.visualization.events.addListener( chartFullsize, 'select', function() {
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
};

///////////
QCChartPSMScoreVsScores.prototype.resetMaxXMaxY = function() {
//	var objectThis = this;
	var $psm_score_vs_score_qc_plot_max_x = $("#psm_score_vs_score_qc_plot_max_x");
	var $psm_score_vs_score_qc_plot_max_y = $("#psm_score_vs_score_qc_plot_max_y");
	$psm_score_vs_score_qc_plot_max_x.val( "" );
	$psm_score_vs_score_qc_plot_max_y.val( "" );
};
