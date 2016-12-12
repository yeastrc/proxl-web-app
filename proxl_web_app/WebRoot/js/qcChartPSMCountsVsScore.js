
//          qcChartPSMCountsVsScore.js

//  Quality Control chart

//  Javascript for the viewProject.jsp page

//////////////////////////////////
// JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

//  CONSTANTS

var PSM_COUNT_VS_SCORE_CHART_BIN_START_OR_END_TO_FIXED_VALUE = 4;
var PSM_COUNT_VS_SCORE_CHART_PERCENTAGE_OF_MAX_TO_FIXED_VALUE = 1;
var PSM_COUNT_VS_SCORE_CHART_PERCENTAGE_OF_MAX_TO_FIXED_VALUE_OUTPUT_FILE = 4;
var PSM_COUNT_VS_SCORE_CHART_COMPARISON_DIRECTION_STRING_ABOVE_ASCII = ">=";
var PSM_COUNT_VS_SCORE_CHART_COMPARISON_DIRECTION_STRING_BELOW_ASCII = "<=";
var PSM_COUNT_VS_SCORE_CHART_COMPARISON_DIRECTION_STRING_ABOVE = "\u2265"; // ">=" as a single character
var PSM_COUNT_VS_SCORE_CHART_COMPARISON_DIRECTION_STRING_BELOW = "\u2264"; // "<=" as a single character
var PSM_COUNT_VS_SCORE_CHART_WIDTH = 920;
var PSM_COUNT_VS_SCORE_CHART_HEIGHT = 650;
var RELOAD_PSM_COUNT_VS_SCORE_CHART_TIMER_DELAY = 400;  // in Milliseconds


var qcChartPSMCountVsScoreInitialized = false;

///   Called when Google charts is initialized
function initQCChartPSMCountVsScore() {
	qcChartPSMCountVsScoreInitialized = true;
};

///   Called from viewProjectPage.js  initPage()  when page is ready
function initQCPlotPSMCountVsScoreClickHandlers() {	
	qcChartPSMCountVsScores.init();
}

//  Constructor for QC chart for PSM Q Values
var QCChartPSMCountVsScores = function(  ) {
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
var qcChartPSMCountVsScores = new QCChartPSMCountVsScores();

//  CONSTANTS
QCChartPSMCountVsScores.prototype.PSM_COUNT_VS_SCORE_CHART_WIDTH = PSM_COUNT_VS_SCORE_CHART_WIDTH;
QCChartPSMCountVsScores.prototype.PSM_COUNT_VS_SCORE_CHART_HEIGHT = PSM_COUNT_VS_SCORE_CHART_HEIGHT;
QCChartPSMCountVsScores.prototype.RELOAD_PSM_COUNT_VS_SCORE_CHART_TIMER_DELAY = RELOAD_PSM_COUNT_VS_SCORE_CHART_TIMER_DELAY;  // in Milliseconds
QCChartPSMCountVsScores.prototype.Y_AXIS_CHOICE_PERCENTAGE = "PERCENTAGE";
QCChartPSMCountVsScores.prototype.Y_AXIS_CHOICE_RAW_COUNTS = "RAW_COUNTS";

//////////
QCChartPSMCountVsScores.prototype.init = function() {
	var objectThis = this;
	$(".qc_plot_psm_count_vs_score_link_jq").click(function(eventObject) {
		try {
			var clickThis = this;
			objectThis.psmCountVsScoreQCPlotClickHandler( clickThis, eventObject );
			return false;
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});	 	
	$(".psm_count_vs_score_qc_plot_overlay_close_parts_jq").click(function(eventObject) {
		try {
			var clickThis = this;
			if ( objectThis.reloadPSMCountVsScoreChartTimerId ) {
				clearTimeout( objectThis.reloadPSMCountVsScoreChartTimerId );
				objectThis.reloadPSMCountVsScoreChartTimerId = null;
			}
			objectThis.closePSMCountVsScoreQCPlotOverlay( clickThis, eventObject );
			return false;
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});
	$("#psm_count_vs_score_qc_plot_score_type_id").change(function(eventObject) {
		try {
			objectThis.scoreTypeChanged( );
			return false;
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});
	$(".psm_count_vs_score_qc_plot_on_change_jq").change(function(eventObject) {
		try {
			objectThis.createChartFromPageParams( );
			return false;
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});
	$(".psm_count_vs_score_qc_plot_on_change_jq").keyup(function(eventObject) {
		try {
			if ( objectThis.reloadPSMCountVsScoreChartTimerId ) {
				clearTimeout( objectThis.reloadPSMCountVsScoreChartTimerId );
				objectThis.reloadPSMCountVsScoreChartTimerId = null;
			}
			objectThis.reloadPSMCountVsScoreChartTimerId = setTimeout( function() {
				try {
					objectThis.createChartFromPageParams( );
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
				}, objectThis.RELOAD_PSM_COUNT_VS_SCORE_CHART_TIMER_DELAY );
			return false;
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
	$("#psm_count_vs_score_qc_plot_max_reset_button").click(function(eventObject) {
		try {
			objectThis.resetMaxXMaxY();
			objectThis.createChartFromPageParams( );
			return false;
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
		});

	$( "#psm_count-svg-download-jpeg" ).click( function() { objectThis.downloadSvg( 'jpeg' ); });
	$( "#psm_count-svg-download-png" ).click( function() { objectThis.downloadSvg( 'png' ); });
	$( "#psm_count-svg-download-pdf" ).click( function() { objectThis.downloadSvg( 'pdf' ); });
	$( "#psm_count-svg-download-svg" ).click( function() { objectThis.downloadSvg( 'svg' ); });
	
	$( "#psm_count-svg-download-data" ).click( function() { objectThis.downloadData( ); });
	
};


QCChartPSMCountVsScores.prototype.downloadData = function( ) {
	
	var objectThis = this;
	
	try {
		if ( this.globals === undefined || this.globals.chartDataFromServer === undefined ) {
			//  No stored chart data from server so exit
			return;  //  EARLY EXIT
		}
		
		var dataArraySize = this.globals.chartDataFromServer.dataArraySize;
		var crosslinkChartData = this.globals.chartDataFromServer.crosslinkData;
		var looplinkChartData = this.globals.chartDataFromServer.looplinkData;
		var unlinkedChartData = this.globals.chartDataFromServer.unlinkedData;
		var alllinkChartData = this.globals.chartDataFromServer.alllinkData;

		//		Count Label
		var displayAsPercentage = false;
		if ( $("#psm_count_vs_score_qc_plot_y_axis_as_percentage").prop("checked") ) {
			displayAsPercentage = true;
		}
		var countLabel = "Raw count";
		if ( displayAsPercentage ) {
			countLabel = "Percent of Max"; 
		}

		//  Selected Annotation Text
		var selectedAnnotationTypeText = $("#psm_count_vs_score_qc_plot_score_type_id option:selected").text();

		//  Annotation direction
		var comparisonDirectionString = "???";
		if ( this.globals.chartDataFromServer.sortDirectionAbove ) {
			comparisonDirectionString = PSM_COUNT_VS_SCORE_CHART_COMPARISON_DIRECTION_STRING_ABOVE_ASCII;
		} else if ( objectThis.globals.chartDataFromServer.sortDirectionBelow ) {
			comparisonDirectionString = PSM_COUNT_VS_SCORE_CHART_COMPARISON_DIRECTION_STRING_BELOW_ASCII;
		} else {
			throw "sortDirectionBelow or sortDirectionAbove must be true";
		}

		
		var headerLine = "type\t" + countLabel + "\t" + selectedAnnotationTypeText + " " + comparisonDirectionString ;

		var outputStringArray = new Array();
		
		outputStringArray.push( headerLine );
		
		var processForType = function( params ) {
			
			var dataForType = params.dataForType;
			var linkTypeLabel = params.linkTypeLabel;
			
			for ( var index = 0; index < dataArraySize; index++ ) {
			
				var bucket = dataForType.chartBuckets[ index ];

				var outputType = linkTypeLabel;
				
				var outputCount = undefined;
				var outputAnnTypeValue = undefined;

				var outputCount = bucket.totalCount;
				if ( displayAsPercentage ) {
					outputCount = ( outputCount / dataForType.totalCountForType * 100 ).toFixed( PSM_COUNT_VS_SCORE_CHART_PERCENTAGE_OF_MAX_TO_FIXED_VALUE_OUTPUT_FILE );
				}
				//  For "above" display the left edge of the bucket, otherwise the right edge
				var bucketStartOrEndForDisplayNumber; 
				if ( objectThis.globals.chartDataFromServer.sortDirectionAbove ) {
					bucketStartOrEndForDisplayNumber = bucket.binStart;
				} else {
					bucketStartOrEndForDisplayNumber = bucket.binEnd;
				}			
				var outputAnnTypeValue = bucketStartOrEndForDisplayNumber.toFixed( PSM_COUNT_VS_SCORE_CHART_BIN_START_OR_END_TO_FIXED_VALUE );

				var outputLine = outputType + "\t" + outputCount + "\t" + outputAnnTypeValue ;
				
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
		if ( alllinkChartData ) {
			processForType( { dataForType: alllinkChartData, linkTypeLabel: "all" } );
		}
		
		var content = outputStringArray.join("\n");
		
		var filename = "psmCountData.txt"
		var mimetype = "text/plain";
		
		downloadStringAsFile( filename, mimetype, content )
		
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
	
};


QCChartPSMCountVsScores.prototype.downloadSvg = function( type ) {
	
	try {
		
		var form = document.createElement( "form" );
		
		$( form ).hide();
		
	    form.setAttribute( "method", "post" );
	    form.setAttribute( "action", contextPathJSVar + "/convertAndDownloadSVG.do" );

		var $svg_image_inner_container_div__svg_merged_image_svg_jq = $( "#psm_count_vs_score_qc_plot_chartDiv svg " );
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
QCChartPSMCountVsScores.prototype.psmCountVsScoreQCPlotClickHandler = function(clickThis, eventObject) {
	var objectThis = this;
	objectThis.openPSMCountVsScoreQCPlotOverlay(clickThis, eventObject);
	return;
};

///////////
QCChartPSMCountVsScores.prototype.closePSMCountVsScoreQCPlotOverlay = function(clickThis, eventObject) {
	$(".psm_count_vs_score_qc_plot_overlay_show_hide_parts_jq").hide();
};

///////////
QCChartPSMCountVsScores.prototype.openPSMCountVsScoreQCPlotOverlay = function(clickThis, eventObject) {
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
	var $psm_count_vs_score_qc_plot_current_search_name_and_id = $("#psm_count_vs_score_qc_plot_current_search_name_and_id");
	$psm_count_vs_score_qc_plot_current_search_name_and_id.val( searchNameAndNumberInParens );
	
	// Position dialog over clicked link
	//  get position of div containing the dialog that is inline in the page
	var $psm_count_vs_score_qc_plot_overlay_containing_outermost_div_inline_div = $("#psm_count_vs_score_qc_plot_overlay_containing_outermost_div_inline_div");
	var offset__containing_outermost_div_inline_div = $psm_count_vs_score_qc_plot_overlay_containing_outermost_div_inline_div.offset();
	var offsetTop__containing_outermost_div_inline_div = offset__containing_outermost_div_inline_div.top;
	var $psm_count_vs_score_qc_plot_overlay_container = $("#psm_count_vs_score_qc_plot_overlay_container");
	var scrollTopWindow = $(window).scrollTop();
	var positionAdjust = scrollTopWindow - offsetTop__containing_outermost_div_inline_div + 10;
	$psm_count_vs_score_qc_plot_overlay_container.css( "top", positionAdjust );
	var $psm_count_vs_score_qc_plot_overlay_container = $("#psm_count_vs_score_qc_plot_overlay_container");
	var $psm_count_vs_score_qc_plot_overlay_background = $("#psm_count_vs_score_qc_plot_overlay_background"); 
	$psm_count_vs_score_qc_plot_overlay_background.show();
	$psm_count_vs_score_qc_plot_overlay_container.show();
	////////////////
	var $psm_count_vs_score_qc_plot_current_search_id = $("#psm_count_vs_score_qc_plot_current_search_id");
	var prevSearchId = $psm_count_vs_score_qc_plot_current_search_id.val( );
	if ( prevSearchId === searchId ) {
		////  Same Search Id as when last opened so just show it
		var $psm_count_vs_score_qc_plot_overlay_background = $("#psm_count_vs_score_qc_plot_overlay_background"); 
		$psm_count_vs_score_qc_plot_overlay_background.show();
		$psm_count_vs_score_qc_plot_overlay_container.show();
		return;  //  EARLY EXIT
	}

	this.globals.currentSearchData = undefined;   ///  Reset "currentSearchData" for new search
	this.populateCutoffsOnImportMessage( { $search_root_jq: $search_root_jq } );
	$psm_count_vs_score_qc_plot_current_search_id.val( searchId );
	
	//  Empty Include Protein Name option Select before get list of protein names
	var $psm_count_vs_score_qc_plot_protein_seq_id_include_select = $("#psm_count_vs_score_qc_plot_protein_seq_id_include_select");
	$psm_count_vs_score_qc_plot_protein_seq_id_include_select.empty();
	$psm_count_vs_score_qc_plot_protein_seq_id_include_select.val("");
	
	//  Empty Exclude Protein Name option Select before get list of protein names
	var $psm_count_vs_score_qc_plot_protein_seq_id_exclude_select = $("#psm_count_vs_score_qc_plot_protein_seq_id_exclude_select");
	$psm_count_vs_score_qc_plot_protein_seq_id_exclude_select.empty();
	$psm_count_vs_score_qc_plot_protein_seq_id_exclude_select.val("");

	this.getPSMFilterableAnnTypesForSearchId( { 
		searchId: searchId
	} );
	this.getProteinSeqIdsProteinAnnNamesForSearchId( { 
		searchId: searchId
	} );
};

//////////
QCChartPSMCountVsScores.prototype.populateCutoffsOnImportMessage = function( params ) {
	var $search_root_jq = params.$search_root_jq;
	var $qc_plots_links_filtered_on_import_message_jq = $search_root_jq.find(".qc_plots_links_filtered_on_import_message_jq");
	var qc_plots_links_filtered_on_import_message_jqHTML = $qc_plots_links_filtered_on_import_message_jq.html();
	var $psm_count_vs_score_qc_plot_score_cutoffs_on_import_row = $("#psm_count_vs_score_qc_plot_score_cutoffs_on_import_row");
	var $psm_count_vs_score_qc_plot_score_cutoffs_on_import_anns_values = $("#psm_count_vs_score_qc_plot_score_cutoffs_on_import_anns_values");
	if ( qc_plots_links_filtered_on_import_message_jqHTML === "" ) {
		$psm_count_vs_score_qc_plot_score_cutoffs_on_import_row.hide();
		return;
	}
	$psm_count_vs_score_qc_plot_score_cutoffs_on_import_row.show();
	$psm_count_vs_score_qc_plot_score_cutoffs_on_import_anns_values.html( qc_plots_links_filtered_on_import_message_jqHTML );
};

///  
QCChartPSMCountVsScores.prototype.getPSMFilterableAnnTypesForSearchId = function( params ) {
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
QCChartPSMCountVsScores.prototype.getPSMFilterableAnnTypesForSearchIdResponse = function(requestData, responseData, originalParams) {
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
	var $psm_count_vs_score_qc_plot_score_type_id = $("#psm_count_vs_score_qc_plot_score_type_id");
	$psm_count_vs_score_qc_plot_score_type_id.empty();
	var optionsHTMLarray = [];
	for ( var annTypesIndex = 0; annTypesIndex < annTypes.length; annTypesIndex++ ) {
		var annType = annTypes[ annTypesIndex ];
		var html = "<option value='" + annType.id + "'>" + annType.name +
			" (" + annType.searchProgramName + ")" +
			"</option>";
		optionsHTMLarray.push( html );
	}
	var optionsHTML = optionsHTMLarray.join("");
	$psm_count_vs_score_qc_plot_score_type_id.append( optionsHTML );
	//  If an annotation type record has sort id of 1, then assign that annotation type id to the selector 
	for ( var annTypesIndex = 0; annTypesIndex < annTypes.length; annTypesIndex++ ) {
		var annType = annTypes [ annTypesIndex ];
		if ( annType.annotationTypeFilterableDTO && annType.annotationTypeFilterableDTO.sortOrder === 1 ) {
			$psm_count_vs_score_qc_plot_score_type_id.val( annType.id );
		}
	}
	this.createChartFromPageParams( { 
		searchId: originalParams.searchId
	} );
};


///  
QCChartPSMCountVsScores.prototype.getProteinSeqIdsProteinAnnNamesForSearchId = function( params ) {
	var objectThis = this;
	var searchId = params.searchId;
	if ( ! qcChartsInitialized ) {
		throw "qcChartsInitialized is false"; 
	}
	var _URL = contextPathJSVar + "/services/proteinNames/getProteinNameListForSearchId";
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
			objectThis.getProteinSeqIdsProteinAnnNamesForSearchIdResponse(requestData, data, params);
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
QCChartPSMCountVsScores.prototype.getProteinSeqIdsProteinAnnNamesForSearchIdResponse = function(requestData, responseData, originalParams) {
	
	var proteinSequenceIdProteinAnnotationNameList = responseData.proteinSequenceIdProteinAnnotationNameList;
	
	//	Empty Include Protein Name option Select before get list of protein names
	var $psm_count_vs_score_qc_plot_protein_seq_id_include_select = $("#psm_count_vs_score_qc_plot_protein_seq_id_include_select");
	$psm_count_vs_score_qc_plot_protein_seq_id_include_select.empty();
	
	//	Empty Exclude Protein Name option Select before get list of protein names
	var $psm_count_vs_score_qc_plot_protein_seq_id_exclude_select = $("#psm_count_vs_score_qc_plot_protein_seq_id_exclude_select");
	$psm_count_vs_score_qc_plot_protein_seq_id_exclude_select.empty();
	
	var optionsHTMLarray = [];
	for ( var dataIndex = 0; dataIndex < proteinSequenceIdProteinAnnotationNameList.length; dataIndex++ ) {
		var proteinSequenceIdProteinAnnotationName = proteinSequenceIdProteinAnnotationNameList[ dataIndex ];
		var html = "<option value='" + proteinSequenceIdProteinAnnotationName.proteinSequenceId + 
			"'>" + proteinSequenceIdProteinAnnotationName.annotationName +
			"</option>";
		optionsHTMLarray.push( html );
	}
	var optionsHTML = optionsHTMLarray.join("");
	$psm_count_vs_score_qc_plot_protein_seq_id_include_select.append( optionsHTML );
	$psm_count_vs_score_qc_plot_protein_seq_id_exclude_select.append( optionsHTML );
	
	$psm_count_vs_score_qc_plot_protein_seq_id_include_select.val("");
	$psm_count_vs_score_qc_plot_protein_seq_id_exclude_select.val("");
};

/////////////
QCChartPSMCountVsScores.prototype.scoreTypeChanged = function( ) {
//	var objectThis = this;
	var annTypes = this.globals.currentSearchData.annotationTypeDataById;
	var $psm_count_vs_score_qc_plot_score_type_id = $("#psm_count_vs_score_qc_plot_score_type_id");
	var selectedAnnTypeId = $psm_count_vs_score_qc_plot_score_type_id.val( );
	var annTypeForSelectId = annTypes[ selectedAnnTypeId ];
	if ( annTypeForSelectId === undefined || annTypeForSelectId === null ) {
		throw "annType not found for id: " + selectedAnnTypeId;
	}
	var $psm_count_vs_score_qc_plot_max_x = $("#psm_count_vs_score_qc_plot_max_x");
	var $psm_count_vs_score_qc_plot_max_y = $("#psm_count_vs_score_qc_plot_max_y");
	$psm_count_vs_score_qc_plot_max_x.val("");
	$psm_count_vs_score_qc_plot_max_y.val("");
	this.createChartFromPageParams( );
};

//////////
///  
///  
QCChartPSMCountVsScores.prototype.createChartFromPageParams = function( ) {
	var objectThis = this;
	if ( objectThis.reloadPSMCountVsScoreChartTimerId ) {
		clearTimeout( objectThis.reloadPSMCountVsScoreChartTimerId );
		objectThis.reloadPSMCountVsScoreChartTimerId = null;
	}
	if ( ! this.globals.currentSearchData ) {
		this.globals.currentSearchData = {};
	}
	var $psm_count_vs_score_qc_plot_max_x = $("#psm_count_vs_score_qc_plot_max_x");
	var $psm_count_vs_score_qc_plot_max_y = $("#psm_count_vs_score_qc_plot_max_y");
	if ( ! this.globals.currentSearchData ) {
		this.globals.currentSearchData = {};
	}
	/////////////
	//  if Y Axis choice has changed, clear Y Axis Max input field
	if ( ( $("#psm_count_vs_score_qc_plot_y_axis_as_percentage").prop("checked")
			&& this.globals.prevYAxisChoice !== this.Y_AXIS_CHOICE_PERCENTAGE )
			|| ( $("#psm_count_vs_score_qc_plot_y_axis_as_raw_counts").prop("checked")
					&& this.globals.prevYAxisChoice !== this.Y_AXIS_CHOICE_RAW_COUNTS ) ) {
			$psm_count_vs_score_qc_plot_max_y.val( "" );
	}
	//  Set this.globals.prevYAxisChoice per current selected Y Axis choice
	if ( $("#psm_count_vs_score_qc_plot_y_axis_as_percentage").prop("checked") ) {
		this.globals.prevYAxisChoice = this.Y_AXIS_CHOICE_PERCENTAGE;
	}
	if ( $("#psm_count_vs_score_qc_plot_y_axis_as_raw_counts").prop("checked") ) {
		this.globals.prevYAxisChoice = this.Y_AXIS_CHOICE_RAW_COUNTS;
	}
	/////////////
	var userInputMaxX = $psm_count_vs_score_qc_plot_max_x.val();
	var userInputMaxY = $psm_count_vs_score_qc_plot_max_y.val();
	if ( userInputMaxX !== "" ) {
		// only test for valid Max X value if not empty string
		if ( !  /^[+-]?((\d+(\.\d*)?)|(\.\d+))$/.test( userInputMaxX ) ) {
			//  Max X value is not a valid decimal number
			$(".psm_count_vs_score_qc_plot_param_not_a_number_jq").show();
			$(".psm_count_vs_score_qc_plot_no_data_jq").hide();
			$(".psm_count_vs_score_qc_plot_have_data_jq").hide();
			$psm_count_vs_score_qc_plot_max_x.focus();
			return;  //  EARLY EXIT
		}
//		if ( userInputMaxXNum < 0 ) {
//			
//			$psm_count_vs_score_qc_plot_max_x.val( "0" );
//			
//			userInputMaxX = "0";
//		}
	}	
	if ( userInputMaxY !== "" ) {
		// only test for valid Max Y value if not empty string
		if ( !  /^[+-]?((\d+(\.\d*)?)|(\.\d+))$/.test( userInputMaxY ) ) {
			//  Max Y value is not a valid decimal number
			$(".psm_count_vs_score_qc_plot_param_not_a_number_jq").show();
			$(".psm_count_vs_score_qc_plot_no_data_jq").hide();
			$(".psm_count_vs_score_qc_plot_have_data_jq").hide();
			$psm_count_vs_score_qc_plot_max_y.focus();
			return;  //  EARLY EXIT
		}
		if ( $("#psm_count_vs_score_qc_plot_y_axis_as_percentage").prop("checked") ) {
			var userInputMaxYNum = parseFloat( userInputMaxY ); 
			if ( userInputMaxYNum < 0 ) {
				$psm_count_vs_score_qc_plot_max_y.val( "0" );
				userInputMaxY = "0";
			}
			if ( userInputMaxYNum > 100 ) {
				$psm_count_vs_score_qc_plot_max_y.val( "100" );
				userInputMaxY = "100";
			}
		}
	}	
	var $psm_count_vs_score_qc_plot_current_search_id = $("#psm_count_vs_score_qc_plot_current_search_id");
	var searchId = $psm_count_vs_score_qc_plot_current_search_id.val( );
	var $psm_count_vs_score_qc_plot_score_type_id = $("#psm_count_vs_score_qc_plot_score_type_id");
	var annotationTypeId = $psm_count_vs_score_qc_plot_score_type_id.val();
	var selectedAnnotationTypeText = $("#psm_count_vs_score_qc_plot_score_type_id option:selected").text();
	var selectedLinkTypes = this._getLinkTypesChecked();
	if ( selectedLinkTypes.length === 0 ) {
		$(".psm_count_vs_score_qc_plot_no_data_jq").show();
		$(".psm_count_vs_score_qc_plot_param_not_a_number_jq").hide();
		$(".psm_count_vs_score_qc_plot_have_data_jq").hide();
		return;  //  EARLY EXIT		
	}
	
	var createChartParams =  {  
			searchId : searchId,
			annotationTypeId : annotationTypeId,
			selectedAnnotationTypeText : selectedAnnotationTypeText,
			selectedLinkTypes : selectedLinkTypes,
			userInputMaxX : userInputMaxX,
			userInputMaxY : userInputMaxY };
	
	 //  Process User select of proteins to include
	 
	var $psm_count_vs_score_qc_plot_protein_seq_id_include_select = $("#psm_count_vs_score_qc_plot_protein_seq_id_include_select");
	var psm_count_vs_score_qc_plot_protein_seq_id_include_select = $psm_count_vs_score_qc_plot_protein_seq_id_include_select.val();
	
	if ( psm_count_vs_score_qc_plot_protein_seq_id_include_select !== null ) {
		createChartParams.includeProteinSequenceIds = psm_count_vs_score_qc_plot_protein_seq_id_include_select;
	}

	 //  Process User select of proteins to exclude
	
	var $psm_count_vs_score_qc_plot_protein_seq_id_exclude_select = $("#psm_count_vs_score_qc_plot_protein_seq_id_exclude_select");
	var psm_count_vs_score_qc_plot_protein_seq_id_exclude_select = $psm_count_vs_score_qc_plot_protein_seq_id_exclude_select.val();
	
	if ( psm_count_vs_score_qc_plot_protein_seq_id_exclude_select !== null ) {
		createChartParams.excludeProteinSequenceIds = psm_count_vs_score_qc_plot_protein_seq_id_exclude_select;
	}

	
	objectThis.createChart( createChartParams );
};

//////////
QCChartPSMCountVsScores.prototype._getLinkTypesChecked = function(  ) {
	var selectedLinkTypes = [];
	var $psm_count_vs_score_qc_plot_link_type_include_jq = $(".psm_count_vs_score_qc_plot_link_type_include_jq");
	$psm_count_vs_score_qc_plot_link_type_include_jq.each( function(   ) {
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
QCChartPSMCountVsScores.prototype.createChart = function( params ) {
	var objectThis = this;
	var searchId = params.searchId;
	var selectedLinkTypes = params.selectedLinkTypes;
	var includeProteinSequenceIds = params.includeProteinSequenceIds;
	var excludeProteinSequenceIds = params.excludeProteinSequenceIds;
	var annotationTypeId = params.annotationTypeId;
	var userInputMaxXString = params.userInputMaxX;
	if ( ! qcChartPSMCountVsScoreInitialized ) {
		throw "qcChartPSMCountVsScoreInitialized is false"; 
	}
	var $psm_count_vs_score_qc_plot_chartDiv = $("#psm_count_vs_score_qc_plot_chartDiv");
	$psm_count_vs_score_qc_plot_chartDiv.empty();
	var _URL = contextPathJSVar + "/services/qcplot/getPsmCountsVsScore";
	var requestData = {
			selectedLinkTypes : selectedLinkTypes,
			iP : includeProteinSequenceIds,
			eP : excludeProteinSequenceIds,
			searchId : searchId,
			annotationTypeId : annotationTypeId
	};
	if ( userInputMaxXString !== "" ) {
		var psmScoreCutoff = userInputMaxXString;
		requestData.psmScoreCutoff = psmScoreCutoff;
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
QCChartPSMCountVsScores.prototype.createChartResponse = function(requestData, responseData, originalParams) {
	var objectThis = this;
	var $psm_count_vs_score_qc_plot_chartDiv = $("#psm_count_vs_score_qc_plot_chartDiv");
	var selectedAnnotationTypeText = originalParams.selectedAnnotationTypeText;
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
	
	var dataArraySize = chartDataParam.dataArraySize;
	var crosslinkChartData = chartDataParam.crosslinkData;
	var looplinkChartData = chartDataParam.looplinkData;
	var unlinkedChartData = chartDataParam.unlinkedData;
	var alllinkChartData = chartDataParam.alllinkData;
	if ( ( crosslinkChartData && crosslinkChartData.chartBuckets.length > 0 )
			|| ( looplinkChartData && looplinkChartData.chartBuckets.length > 0 )
			|| ( unlinkedChartData && unlinkedChartData.chartBuckets.length > 0 )
			|| ( alllinkChartData && alllinkChartData.chartBuckets.length > 0 ) ) {
		$(".psm_count_vs_score_qc_plot_no_data_jq").hide();
		$(".psm_count_vs_score_qc_plot_param_not_a_number_jq").hide();
		$(".psm_count_vs_score_qc_plot_have_data_jq").show();
	} else {
		$(".psm_count_vs_score_qc_plot_no_data_jq").show();
		$(".psm_count_vs_score_qc_plot_have_data_jq").hide();
		$(".psm_count_vs_score_qc_plot_param_not_a_number_jq").hide();
		return;  //  EARLY EXIT
	}
//	<xs:simpleType name="filter_direction_type">
//	The direction a filterable annotation type is sorted in.  
//	If set to "below", attributes with lower values are considered more significant (such as in the case of p-values). 
//	If set to "above", attributes with higher values are considered more significant (such as in the case of XCorr).
	var comparisonDirectionString = "???";
	if ( chartDataParam.sortDirectionAbove ) {
		comparisonDirectionString = PSM_COUNT_VS_SCORE_CHART_COMPARISON_DIRECTION_STRING_ABOVE;
	} else if ( chartDataParam.sortDirectionBelow ) {
		comparisonDirectionString = PSM_COUNT_VS_SCORE_CHART_COMPARISON_DIRECTION_STRING_BELOW;
	} else {
		throw "sortDirectionBelow or sortDirectionAbove must be true";
	}
	var displayAsPercentage = false;
	if ( $("#psm_count_vs_score_qc_plot_y_axis_as_percentage").prop("checked") ) {
		displayAsPercentage = true;
	}
	//  chart data for Google charts
	var chartData = [];
	//  output columns specification
//	chartData.push( ["preMZ","count"] );
//	//  With Tooltip
//	chartData.push( ["q_value",
//	                 "total_count",{role: "tooltip",  'p': {'html': true} }
////	                 , { role: 'style' } 
//	                 ] );
	var lineColors = [];
//	var maxPSMCount = 0;
//	
//	var updateMaxPSMCountForType = function( chartDataForType ) {
//		
//		if ( maxPSMCount < chartDataForType.totalCountForType ) {
//			
//			maxPSMCount = chartDataForType.totalCountForType;
//		}
//	};
//	red: #A55353
//	green: #53a553
//	blue: #5353a5
//	combined: #a5a5a5 (gray)
	var chartDataHeaderEntry = [ selectedAnnotationTypeText ];
	if ( alllinkChartData ) {
		chartDataHeaderEntry.push( "all" );
		chartDataHeaderEntry.push( {role: "tooltip", 'p': {'html': true} }  ); 
		lineColors.push( '#a5a5a5' );	//	combined: #a5a5a5 (gray)
//		updateMaxPSMCountForType( alllinkChartData );
	}
	if ( crosslinkChartData ) {
		chartDataHeaderEntry.push( "crosslink" );
		chartDataHeaderEntry.push( {role: "tooltip", 'p': {'html': true} }  ); 
		lineColors.push( '#A55353' );	//	red: #A55353
//		updateMaxPSMCountForType( crosslinkChartData );
	}
	if ( looplinkChartData ) {
		chartDataHeaderEntry.push( "looplink" );
		chartDataHeaderEntry.push( {role: "tooltip", 'p': {'html': true} }  ); 
		lineColors.push( '#53a553' );	//	green: #53a553
//		updateMaxPSMCountForType( looplinkChartData );
	}
	if ( unlinkedChartData ) {
		chartDataHeaderEntry.push( "unlinked" );
		chartDataHeaderEntry.push( {role: "tooltip", 'p': {'html': true} }  ); 
		lineColors.push( '#5353a5' );	//	blue: #5353a5
//		updateMaxPSMCountForType( unlinkedChartData );
	}
	chartData.push( chartDataHeaderEntry );
	var processBucketForType = function( params ) {
		var dataForType = params.dataForType;
		var linkTypeLabel = params.linkTypeLabel;
		if ( dataForType ) {
			var bucket = dataForType.chartBuckets[ index ];
			if ( chartDataEntry.length === 0 ) {
				// Add position to chartDataEntry array if chartDataEntry is empty
				if ( chartDataParam.sortDirectionAbove ) {
					chartDataEntry.push( bucket.binStart );	
				} else if ( chartDataParam.sortDirectionBelow ) {
					chartDataEntry.push( bucket.binEnd );	
				}
			}
			var chartDataValue = bucket.totalCount;
			if ( displayAsPercentage ) {
				chartDataValue = chartDataValue / dataForType.totalCountForType * 100;
			}
			chartDataEntry.push( chartDataValue );
			//  For "above" display the left edge of the bucket, otherwise the right edge
			var bucketStartOrEndForDisplayNumber; 
			if ( chartDataParam.sortDirectionAbove ) {
				bucketStartOrEndForDisplayNumber = bucket.binStart;
			} else {
				bucketStartOrEndForDisplayNumber = bucket.binEnd;
			}			
			var bucketStartOrEndForDisplayNumberRounded = bucketStartOrEndForDisplayNumber.toFixed( PSM_COUNT_VS_SCORE_CHART_BIN_START_OR_END_TO_FIXED_VALUE );
			var rawCount = bucket.totalCount;
			try {
				rawCount = rawCount.toLocaleString();
			} catch (e) {
			}
			var tooltip = "<div style='margin: 10px;'>Type: " + linkTypeLabel +
							"<br>Raw count: " + rawCount;
			if ( displayAsPercentage ) {
				var chartDataValueRounded = chartDataValue.toFixed( PSM_COUNT_VS_SCORE_CHART_PERCENTAGE_OF_MAX_TO_FIXED_VALUE );
				tooltip += "<br>Percent of Max: " + chartDataValueRounded; 
			}
			tooltip += "<br>" + selectedAnnotationTypeText + " " + comparisonDirectionString + " " + bucketStartOrEndForDisplayNumberRounded + "</div>";
			chartDataEntry.push( tooltip );
		}
	};
	for ( var index = 0; index < dataArraySize; index++ ) {
		var chartDataEntry = [];
		processBucketForType( { dataForType: alllinkChartData, linkTypeLabel: "all" } );
		processBucketForType( { dataForType: crosslinkChartData, linkTypeLabel: "crosslink" }  );
		processBucketForType( { dataForType: looplinkChartData, linkTypeLabel: "looplink" }  );
		processBucketForType( { dataForType: unlinkedChartData, linkTypeLabel: "unlinked" }  );
		if ( chartDataEntry && chartDataEntry.length > 0 ) {
			chartData.push( chartDataEntry );
		}
	}
	var $psm_count_vs_score_qc_plot_current_search_name_and_id = $("#psm_count_vs_score_qc_plot_current_search_name_and_id");
	var searchNameAndNumberInParens = 
		$psm_count_vs_score_qc_plot_current_search_name_and_id.val( );
	var chartTitle = "Cumulative PSM Count vs " + selectedAnnotationTypeText + "\n" + searchNameAndNumberInParens;
	var yAxisLabel = "# PSM " + comparisonDirectionString + " " + selectedAnnotationTypeText;
	if ( displayAsPercentage ) {
		yAxisLabel = "# PSM " + comparisonDirectionString + " " + selectedAnnotationTypeText + " (% of max)";
	}
	var optionsFullsize = {
			title: chartTitle, // Title above chart
			//  X axis label below chart
			hAxis: { title: selectedAnnotationTypeText, titleTextStyle: {color: 'black'}
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
			width : objectThis.PSM_COUNT_VS_SCORE_CHART_WIDTH, 
			height : objectThis.PSM_COUNT_VS_SCORE_CHART_HEIGHT,   // width and height of chart, otherwise controlled by enclosing div
//			bar: { groupWidth: 5 },  // set bar width large to eliminate space between bars
//			bar: { groupWidth: '100%' },  // set bar width large to eliminate space between bars
//			colors: ['red','blue'],  //  Color of bars
			colors: lineColors,  //  Color of lines
//			red: #A55353
//			green: #53a553
//			blue: #5353a5
//			combined: #a5a5a5 (gray)
			tooltip: {isHtml: true},
//			isStacked: true
			chartArea : { left : 140, top: 60, 
				width: objectThis.PSM_COUNT_VS_SCORE_CHART_WIDTH - 200 ,  //  was 720 as measured in Chrome
				height : objectThis.PSM_COUNT_VS_SCORE_CHART_HEIGHT - 120 }  //  was 530 as measured in Chrome
	};        
	if ( userInputMaxY ) {
		//  Data points with Y axis > userInputMaxY will not be shown
		optionsFullsize.vAxis.viewWindow = { max : userInputMaxY };
	}
	var $psm_count_vs_score_qc_plot_overlay_container = $("#psm_count_vs_score_qc_plot_overlay_container");
	var $psm_count_vs_score_qc_plot_overlay_background = $("#psm_count_vs_score_qc_plot_overlay_background"); 
	$psm_count_vs_score_qc_plot_overlay_background.show();
	$psm_count_vs_score_qc_plot_overlay_container.show();
	// create the chart
	var data = google.visualization.arrayToDataTable( chartData );
	var chartFullsize = new google.visualization.LineChart( $psm_count_vs_score_qc_plot_chartDiv[0] );
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
QCChartPSMCountVsScores.prototype.resetMaxXMaxY = function() {
//	var objectThis = this;
	var $psm_count_vs_score_qc_plot_max_x = $("#psm_count_vs_score_qc_plot_max_x");
	var $psm_count_vs_score_qc_plot_max_y = $("#psm_count_vs_score_qc_plot_max_y");
	$psm_count_vs_score_qc_plot_max_x.val( "" );
	$psm_count_vs_score_qc_plot_max_y.val( "" );
};
