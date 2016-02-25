
//          qcChartPSMCountsVsScore.js


//  Quality Control chart

//  Javascript for the viewProject.jsp page

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";



//CONSTANTS


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
			
			prevYAxisChoice : undefined
			
	};
	
};


///////////

//  Create an instance from the constructor

var qcChartPSMCountVsScores = new QCChartPSMCountVsScores();




//CONSTANTS


QCChartPSMCountVsScores.prototype.PSM_COUNT_VS_SCORE_CHART_WIDTH = PSM_COUNT_VS_SCORE_CHART_WIDTH;
QCChartPSMCountVsScores.prototype.PSM_COUNT_VS_SCORE_CHART_HEIGHT = PSM_COUNT_VS_SCORE_CHART_HEIGHT;

QCChartPSMCountVsScores.prototype.RELOAD_PSM_COUNT_VS_SCORE_CHART_TIMER_DELAY = RELOAD_PSM_COUNT_VS_SCORE_CHART_TIMER_DELAY;  // in Milliseconds


QCChartPSMCountVsScores.prototype.Y_AXIS_CHOICE_PERCENTAGE = "PERCENTAGE";

QCChartPSMCountVsScores.prototype.Y_AXIS_CHOICE_RAW_COUNTS = "RAW_COUNTS";




//////////

QCChartPSMCountVsScores.prototype.init = function() {

	var objectThis = this;

	

	$(".qc_plot_psm_count_vs_score_link_jq").click(function(eventObject) {

		var clickThis = this;

		objectThis.psmCountVsScoreQCPlotClickHandler( clickThis, eventObject );
		
		return false;
	});	 	
	
	$(".psm_count_vs_score_qc_plot_overlay_close_parts_jq").click(function(eventObject) {

		var clickThis = this;
		
		if ( objectThis.reloadPSMCountVsScoreChartTimerId ) {
			
			clearTimeout( objectThis.reloadPSMCountVsScoreChartTimerId );
			
			objectThis.reloadPSMCountVsScoreChartTimerId = null;
		}

		objectThis.closePSMCountVsScoreQCPlotOverlay( clickThis, eventObject );
		
		return false;
	});
	
	$("#psm_count_vs_score_qc_plot_score_type_id").change(function(eventObject) {
		
		objectThis.scoreTypeChanged( );
		
		return false;
	});
	


	
	$(".psm_count_vs_score_qc_plot_on_change_jq").change(function(eventObject) {
		
		objectThis.createChartFromPageParams( );
		
		return false;
	});
	

	$(".psm_count_vs_score_qc_plot_on_change_jq").keyup(function(eventObject) {
		
		if ( objectThis.reloadPSMCountVsScoreChartTimerId ) {
			
			clearTimeout( objectThis.reloadPSMCountVsScoreChartTimerId );
			
			objectThis.reloadPSMCountVsScoreChartTimerId = null;
		}

		
		objectThis.reloadPSMCountVsScoreChartTimerId = setTimeout( function() {
				
			objectThis.createChartFromPageParams( );
				
		}, objectThis.RELOAD_PSM_COUNT_VS_SCORE_CHART_TIMER_DELAY );
		
		return false;
	});
	

	$("#psm_count_vs_score_qc_plot_max_reset_button").click(function(eventObject) {

		
		objectThis.resetMaxXMaxY();

		objectThis.createChartFromPageParams( );
		
		return false;
	});
	
		
	$( "#psm_count_vs_score_qc_plot_download_svg" ).click( function() {
		var d = new Date().toISOString().slice(0, 19).replace(/-/g, "");
		var $svg_image_inner_container_div__svg_merged_image_svg_jq = $( "#psm_count_vs_score_qc_plot_chartDiv svg " );
		var svgContents = $svg_image_inner_container_div__svg_merged_image_svg_jq.html();
		var fullSVG_String = "<svg id=\"svg\">" + svgContents +"</svg>";
		var svgBase64Ecoded = Base64.encode( fullSVG_String );
		var hrefString = "data:application/svg+xml;base64," + svgBase64Ecoded;
		var downloadFilename = "psm_count_vs_scores_" + d + ".svg";
		$(this).attr("href", hrefString ).attr("download", downloadFilename );
	});
	
	
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

	
	//  TODO  TEMP

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
	
	
	
	
	$psm_count_vs_score_qc_plot_current_search_id.val( searchId );
	

	this.getPSMFilterableAnnTypesForSearchId( { 
		searchId: searchId
	} );
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

	var annTypes = responseData.annotationTypeDTOList;

	if (  annTypes.length === 0 ) {

		throw "annTypes.length === 0";
	}



	if ( ! this.globals.currentSearchData ) {

		this.globals.currentSearchData = {};
	}

	var annTypesById = {};

	for ( var annTypesIndex = 0; annTypesIndex < annTypes.length; annTypesIndex++ ) {

		var annType = annTypes [ annTypesIndex ];

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
		
		var html = "<option value='" + annType.id + "'>" + annType.name + "</option>";
			
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

		var userInputMaxXNum = parseFloat( userInputMaxX ); 

		if ( isNaN( userInputMaxXNum ) ) {

			$(".psm_count_vs_score_qc_plot_param_not_a_number_jq").show();

			$(".psm_count_vs_score_qc_plot_no_data_jq").hide();
			$(".psm_count_vs_score_qc_plot_have_data_jq").hide();
			
			return;  //  EARLY EXIT
		}
		
		if ( userInputMaxXNum < 0 ) {
			
			$psm_count_vs_score_qc_plot_max_x.val( "0" );
			
			userInputMaxX = "0";
		}

	}	

	if ( userInputMaxY !== "" ) {

		var userInputMaxYNum = parseFloat( userInputMaxY ); 
		
		if ( isNaN( userInputMaxYNum ) ) {

			$(".psm_count_vs_score_qc_plot_param_not_a_number_jq").show();

			$(".psm_count_vs_score_qc_plot_no_data_jq").hide();
			$(".psm_count_vs_score_qc_plot_have_data_jq").hide();
			
			return;  //  EARLY EXIT
		}
		
		if ( userInputMaxYNum < 0 ) {
			
			$psm_count_vs_score_qc_plot_max_y.val( "0" );
			
			userInputMaxY = "0";
		}

		
		if ( $("#psm_count_vs_score_qc_plot_y_axis_as_percentage").prop("checked") ) {
			
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
		
		
//		var $psm_count_vs_score_qc_plot_link_type_include_jq = $(".psm_count_vs_score_qc_plot_link_type_include_jq");
//		
//		$psm_count_vs_score_qc_plot_link_type_include_jq.prop( "checked", true);
//
//		selectedLinkTypes = this._getLinkTypesChecked();
	}

	
	objectThis.createChart( {  
		searchId : searchId,
		annotationTypeId : annotationTypeId,
		selectedAnnotationTypeText : selectedAnnotationTypeText,
		selectedLinkTypes : selectedLinkTypes,
		userInputMaxX : userInputMaxX,
		userInputMaxY : userInputMaxY } );
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
	
	
	var chartDataHeaderEntry = [ "q_value" ];
	
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


	var _BIN_END_TO_FIXED_VALUE = 2;
	
	var _PERCENTAGE_OF_MAX_TO_FIXED_VALUE = 1;
	
	var processBucketForType = function( params ) {

		var dataForType = params.dataForType;
		var linkTypeLabel = params.linkTypeLabel;

		if ( dataForType ) {
			
			var bucket = dataForType.chartBuckets[ index ];
			
			if ( chartDataEntry.length === 0 ) {
				
				// Add position to chartDataEntry array if chartDataEntry is empty
				
				chartDataEntry.push( bucket.binEnd );
			}
			
			var chartDataValue = bucket.totalCount;
			
			if ( displayAsPercentage ) {
				
				chartDataValue = chartDataValue / dataForType.totalCountForType * 100;
			}

			chartDataEntry.push( chartDataValue );
			
			var binEndRounded = bucket.binEnd.toFixed( _BIN_END_TO_FIXED_VALUE );

			var rawCount = bucket.totalCount;
			
			try {
				
				rawCount = rawCount.toLocaleString();
			} catch (e) {

			}


			var tooltip = "<div style='margin: 10px;'>Type: " + linkTypeLabel +
							"<br>Raw count: " + rawCount;
			
			if ( displayAsPercentage ) {
				
				var chartDataValueRounded = chartDataValue.toFixed( _PERCENTAGE_OF_MAX_TO_FIXED_VALUE );
			
				tooltip += "<br>Percent of Max: " + chartDataValueRounded; 
			}
			
			tooltip += "<br>" + selectedAnnotationTypeText + " <= " + binEndRounded + "</div>";
			
			chartDataEntry.push( tooltip );
		}
	};

	
	for ( var index = 0; index < dataArraySize; index++ ) {
		
		var chartDataEntry = [];

		
		processBucketForType( { dataForType: alllinkChartData, linkTypeLabel: "all" } );
		
		processBucketForType( { dataForType: crosslinkChartData, linkTypeLabel: "crosslink" }  );
		processBucketForType( { dataForType: looplinkChartData, linkTypeLabel: "loooplink" }  );
		processBucketForType( { dataForType: unlinkedChartData, linkTypeLabel: "unlinked" }  );
		
		
		if ( chartDataEntry && chartDataEntry.length > 0 ) {
			
			chartData.push( chartDataEntry );
		}
		
	}

	var $psm_count_vs_score_qc_plot_current_search_name_and_id = $("#psm_count_vs_score_qc_plot_current_search_name_and_id");
	
	var searchNameAndNumberInParens = 
		$psm_count_vs_score_qc_plot_current_search_name_and_id.val( );
	

	
	var chartTitle = "Cumulative PSM Count vs " + selectedAnnotationTypeText + "\n" + searchNameAndNumberInParens;
	
	
	var yAxisLabel = "Cumulative PSM Count";
	

	if ( displayAsPercentage ) {
		
		yAxisLabel = "Cumulative PSM Count (% of max)";
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
