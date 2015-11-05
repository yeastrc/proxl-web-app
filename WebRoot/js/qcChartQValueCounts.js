
//          qcChartQValueCounts.js


//  Quality Control chart

//  Javascript for the viewProject.jsp page

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";



//CONSTANTS


var Q_VALUE__COUNT_CHART_WIDTH = 920;
var Q_VALUE__COUNT_CHART_HEIGHT = 650;

var RELOAD_Q_VALUE_COUNT_CHART_TIMER_DELAY = 400;  // in Milliseconds





var qcChartQValueCountInitialized = false;




///   Called when Google charts is initialized

function initQCChartQValueCount() {
	
	qcChartQValueCountInitialized = true;
	
	
	
};




///   Called from viewProjectPage.js  initPage()  when page is ready


function initQCPlotQValueCountClickHandlers() {	
	
	qcChartQValueCounts.init();
	
	
	
	
}



//  Constructor for QC chart for PSM Q Values

var QCChartQValueCounts = function(  ) {
	
	
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

var qcChartQValueCounts = new QCChartQValueCounts();




//CONSTANTS


QCChartQValueCounts.prototype.Q_VALUE__COUNT_CHART_WIDTH = Q_VALUE__COUNT_CHART_WIDTH;
QCChartQValueCounts.prototype.Q_VALUE__COUNT_CHART_HEIGHT = Q_VALUE__COUNT_CHART_HEIGHT;

QCChartQValueCounts.prototype.RELOAD_Q_VALUE_COUNT_CHART_TIMER_DELAY = RELOAD_Q_VALUE_COUNT_CHART_TIMER_DELAY;  // in Milliseconds


QCChartQValueCounts.prototype.Y_AXIS_CHOICE_PERCENTAGE = "PERCENTAGE";

QCChartQValueCounts.prototype.Y_AXIS_CHOICE_RAW_COUNTS = "RAW_COUNTS";




//////////

QCChartQValueCounts.prototype.init = function() {

	var objectThis = this;

	

	$(".qc_plot_psm_q_value_count_link_jq").click(function(eventObject) {

		var clickThis = this;

		objectThis.qvalueCountQCPlotClickHandler( clickThis, eventObject );
		
		return false;
	});	 	
	
	$(".psm_q_value_count_qc_plot_overlay_close_parts_jq").click(function(eventObject) {

		var clickThis = this;
		
		if ( objectThis.reloadQvalueCountChartTimerId ) {
			
			clearTimeout( objectThis.reloadQvalueCountChartTimerId );
			
			objectThis.reloadQvalueCountChartTimerId = null;
		}

		objectThis.closeQvalueCountQCPlotOverlay( clickThis, eventObject );
		
		return false;
	});

	
	$(".psm_q_value_count_qc_plot_on_change_jq").change(function(eventObject) {
		
		objectThis.createChartFromPageParams( );
		
		return false;
	});
	

	$(".psm_q_value_count_qc_plot_on_change_jq").keyup(function(eventObject) {
		
		if ( objectThis.reloadQvalueCountChartTimerId ) {
			
			clearTimeout( objectThis.reloadQvalueCountChartTimerId );
			
			objectThis.reloadQvalueCountChartTimerId = null;
		}

		
		objectThis.reloadQvalueCountChartTimerId = setTimeout( function() {
				
			objectThis.createChartFromPageParams( );
				
		}, objectThis.RELOAD_Q_VALUE_COUNT_CHART_TIMER_DELAY );
		
		return false;
	});
	

	$("#psm_q_value_count_qc_plot_max_reset_button").click(function(eventObject) {

		
		objectThis.resetMaxXMaxY();

		objectThis.createChartFromPageParams( );
		
		return false;
	});
	
		
	$( "#psm_q_value_count_qc_plot_download_svg" ).click( function() {
		var d = new Date().toISOString().slice(0, 19).replace(/-/g, "");
		var $svg_image_inner_container_div__svg_merged_image_svg_jq = $( "#psm_q_value_count_qc_plot_chartDiv svg " );
		var svgContents = $svg_image_inner_container_div__svg_merged_image_svg_jq.html();
		var fullSVG_String = "<svg id=\"svg\">" + svgContents +"</svg>";
		var svgBase64Ecoded = Base64.encode( fullSVG_String );
		var hrefString = "data:application/svg+xml;base64," + svgBase64Ecoded;
		var downloadFilename = "psm_q_value_counts_" + d + ".svg";
		$(this).attr("href", hrefString ).attr("download", downloadFilename );
	});
	
	
};




/////////////////

QCChartQValueCounts.prototype.qvalueCountQCPlotClickHandler = function(clickThis, eventObject) {

	var objectThis = this;
	
	objectThis.openQvalueCountQCPlotOverlay(clickThis, eventObject);

	return;

};

///////////

QCChartQValueCounts.prototype.openQvalueCountQCPlotOverlay = function(clickThis, eventObject) {

	var objectThis = this;

	
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
	
	var $psm_q_value_count_qc_plot_current_search_name_and_id = $("#psm_q_value_count_qc_plot_current_search_name_and_id");
	
	$psm_q_value_count_qc_plot_current_search_name_and_id.val( searchNameAndNumberInParens );
	
	
	// Position dialog over clicked link
	
	//  get position of div containing the dialog that is inline in the page
	var $psm_q_value_count_qc_plot_overlay_containing_outermost_div_inline_div = $("#psm_q_value_count_qc_plot_overlay_containing_outermost_div_inline_div");
	
	var offset__containing_outermost_div_inline_div = $psm_q_value_count_qc_plot_overlay_containing_outermost_div_inline_div.offset();
	var offsetTop__containing_outermost_div_inline_div = offset__containing_outermost_div_inline_div.top;
	

	var $psm_q_value_count_qc_plot_overlay_container = $("#psm_q_value_count_qc_plot_overlay_container");
	
	var scrollTopWindow = $(window).scrollTop();
	
	var positionAdjust = scrollTopWindow - offsetTop__containing_outermost_div_inline_div + 10;

	$psm_q_value_count_qc_plot_overlay_container.css( "top", positionAdjust );


	
	
	
	var $psm_q_value_count_qc_plot_current_search_id = $("#psm_q_value_count_qc_plot_current_search_id");
	
	var prevSearchId = $psm_q_value_count_qc_plot_current_search_id.val( );
	
	if ( prevSearchId === searchId ) {
		
		////  Same Search Id as when last opened so just show it
		
		var $psm_q_value_count_qc_plot_overlay_background = $("#psm_q_value_count_qc_plot_overlay_background"); 
		$psm_q_value_count_qc_plot_overlay_background.show();
		$psm_q_value_count_qc_plot_overlay_container.show();
		
		return;  //  EARLY EXIT
	}
	
	
	this.globals.currentSearchData = undefined;   ///  Reset "currentSearchData" for new search
	
	
	
	
	$psm_q_value_count_qc_plot_current_search_id.val( searchId );
	
	
	objectThis.createChartFromPageParams( { 
		searchId: searchId
	} );
};


//////////	/

QCChartQValueCounts.prototype.closeQvalueCountQCPlotOverlay = function(clickThis, eventObject) {

	$(".psm_q_value_count_qc_plot_overlay_show_hide_parts_jq").hide();
};
			




//////////

///  
///  

QCChartQValueCounts.prototype.createChartFromPageParams = function( ) {


	var objectThis = this;

	
	
	if ( objectThis.reloadQvalueCountChartTimerId ) {
		
		clearTimeout( objectThis.reloadQvalueCountChartTimerId );
		
		objectThis.reloadQvalueCountChartTimerId = null;
	}

	
	if ( ! this.globals.currentSearchData ) {
		
		this.globals.currentSearchData = {};
	}
	
	
	
	

	
	var $psm_q_value_count_qc_plot_max_x = $("#psm_q_value_count_qc_plot_max_x");
	
	var $psm_q_value_count_qc_plot_max_y = $("#psm_q_value_count_qc_plot_max_y");
	
	
	if ( ! this.globals.currentSearchData ) {
		
		this.globals.currentSearchData = {};
	}
	
	/////////////
	
	//  if Y Axis choice has changed, clear Y Axis Max input field
	
	if ( ( $("#psm_q_value_count_qc_plot_y_axis_as_percentage").prop("checked")
			&& this.globals.prevYAxisChoice !== this.Y_AXIS_CHOICE_PERCENTAGE )
			|| ( $("#psm_q_value_count_qc_plot_y_axis_as_raw_counts").prop("checked")
					&& this.globals.prevYAxisChoice !== this.Y_AXIS_CHOICE_RAW_COUNTS ) ) {
			
			$psm_q_value_count_qc_plot_max_y.val( "" );
	}
	
	
	//  Set this.globals.prevYAxisChoice per current selected Y Axis choice

	if ( $("#psm_q_value_count_qc_plot_y_axis_as_percentage").prop("checked") ) {
		
		this.globals.prevYAxisChoice = this.Y_AXIS_CHOICE_PERCENTAGE;
	}

	if ( $("#psm_q_value_count_qc_plot_y_axis_as_raw_counts").prop("checked") ) {
		
		this.globals.prevYAxisChoice = this.Y_AXIS_CHOICE_RAW_COUNTS;
	}
	
	
	/////////////

	var userInputMaxX = $psm_q_value_count_qc_plot_max_x.val();
	var userInputMaxY = $psm_q_value_count_qc_plot_max_y.val();


	if ( userInputMaxX !== "" ) {

		var userInputMaxXNum = parseFloat( userInputMaxX ); 

		if ( isNaN( userInputMaxXNum ) ) {

			$(".psm_q_value_count_qc_plot_param_not_a_number_jq").show();

			$(".psm_q_value_count_qc_plot_no_data_jq").hide();
			$(".psm_q_value_count_qc_plot_have_data_jq").hide();
			
			return;  //  EARLY EXIT
		}
		
		if ( userInputMaxXNum < 0 ) {
			
			$psm_q_value_count_qc_plot_max_x.val( "0" );
			
			userInputMaxX = "0";
		}

	}	

	if ( userInputMaxY !== "" ) {

		var userInputMaxYNum = parseFloat( userInputMaxY ); 
		
		if ( isNaN( userInputMaxYNum ) ) {

			$(".psm_q_value_count_qc_plot_param_not_a_number_jq").show();

			$(".psm_q_value_count_qc_plot_no_data_jq").hide();
			$(".psm_q_value_count_qc_plot_have_data_jq").hide();
			
			return;  //  EARLY EXIT
		}
		
		if ( userInputMaxYNum < 0 ) {
			
			$psm_q_value_count_qc_plot_max_y.val( "0" );
			
			userInputMaxY = "0";
		}

		
		if ( $("#psm_q_value_count_qc_plot_y_axis_as_percentage").prop("checked") ) {
			
			if ( userInputMaxYNum > 100 ) {
				
				$psm_q_value_count_qc_plot_max_y.val( "100" );
				
				userInputMaxY = "100";
			}
		}
	}	

	
	
	
	




	var $psm_q_value_count_qc_plot_current_search_id = $("#psm_q_value_count_qc_plot_current_search_id");
	
	var searchId = $psm_q_value_count_qc_plot_current_search_id.val( );

	
	var selectedLinkTypes = this._getLinkTypesChecked();

	if ( selectedLinkTypes.length === 0 ) {
		

		$(".psm_q_value_count_qc_plot_no_data_jq").show();
		
		$(".psm_q_value_count_qc_plot_param_not_a_number_jq").hide();
		
		$(".psm_q_value_count_qc_plot_have_data_jq").hide();
		
		return;  //  EARLY EXIT		
		
		
//		var $psm_q_value_count_qc_plot_link_type_include_jq = $(".psm_q_value_count_qc_plot_link_type_include_jq");
//		
//		$psm_q_value_count_qc_plot_link_type_include_jq.prop( "checked", true);
//
//		selectedLinkTypes = this._getLinkTypesChecked();
	}

	
	objectThis.createChart( {  
		searchId : searchId,
		selectedLinkTypes : selectedLinkTypes,
		userInputMaxX : userInputMaxX,
		userInputMaxY : userInputMaxY } );
};


//////////

QCChartQValueCounts.prototype._getLinkTypesChecked = function(  ) {
	
	var selectedLinkTypes = [];

	var $psm_q_value_count_qc_plot_link_type_include_jq = $(".psm_q_value_count_qc_plot_link_type_include_jq");

	$psm_q_value_count_qc_plot_link_type_include_jq.each( function(   ) {

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

QCChartQValueCounts.prototype.createChart = function( params ) {

	var objectThis = this;

	
	
	var searchId = params.searchId;
	var selectedLinkTypes = params.selectedLinkTypes;

	var userInputMaxXString = params.userInputMaxX;

	if ( ! qcChartQValueCountInitialized ) {
		
		throw "qcChartQValueCountInitialized is false"; 
	}
	
	
	
	var $psm_q_value_count_qc_plot_chartDiv = $("#psm_q_value_count_qc_plot_chartDiv");
	
	$psm_q_value_count_qc_plot_chartDiv.empty();


	var _URL = contextPathJSVar + "/services/qcplot/getPsmCountPerQValue";
	
	var requestData = {
			selectedLinkTypes : selectedLinkTypes,
			searchId : searchId
	};
	

	if ( userInputMaxXString !== "" ) {
		
		var psmQValueCutoff = userInputMaxXString;
	
		requestData.psmQValueCutoff = psmQValueCutoff;
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

QCChartQValueCounts.prototype.createChartResponse = function(requestData, responseData, originalParams) {

	var objectThis = this;

	
	
	var $psm_q_value_count_qc_plot_chartDiv = $("#psm_q_value_count_qc_plot_chartDiv");
	

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

		$(".psm_q_value_count_qc_plot_no_data_jq").hide();
		
		$(".psm_q_value_count_qc_plot_param_not_a_number_jq").hide();
		
		$(".psm_q_value_count_qc_plot_have_data_jq").show();

	} else {
		
		$(".psm_q_value_count_qc_plot_no_data_jq").show();
		
		$(".psm_q_value_count_qc_plot_have_data_jq").hide();
		
		$(".psm_q_value_count_qc_plot_param_not_a_number_jq").hide();
		
		return;  //  EARLY EXIT
		
	}
	
	
	var displayAsPercentage = false;
	
	if ( $("#psm_q_value_count_qc_plot_y_axis_as_percentage").prop("checked") ) {
		
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
			
			tooltip += "<br>Q-value <= " + binEndRounded + "</div>";
			
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

	var $psm_q_value_count_qc_plot_current_search_name_and_id = $("#psm_q_value_count_qc_plot_current_search_name_and_id");
	
	var searchNameAndNumberInParens = 
		$psm_q_value_count_qc_plot_current_search_name_and_id.val( );
	

	
	var chartTitle = 'Cumulative PSM Count vs Q-value\n' + searchNameAndNumberInParens;
	
	
	var yAxisLabel = "Cumulative PSM Count";
	

	if ( displayAsPercentage ) {
		
		yAxisLabel = "Cumulative PSM Count (% of max)";
	}
	
	var optionsFullsize = {

			title: chartTitle, // Title above chart

			//  X axis label below chart
			hAxis: { title: 'Q-value', titleTextStyle: {color: 'black'}
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
			
			width : objectThis.Q_VALUE__COUNT_CHART_WIDTH, 
			height : objectThis.Q_VALUE__COUNT_CHART_HEIGHT,   // width and height of chart, otherwise controlled by enclosing div
			
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
				width: objectThis.Q_VALUE__COUNT_CHART_WIDTH - 200 ,  //  was 720 as measured in Chrome
				height : objectThis.Q_VALUE__COUNT_CHART_HEIGHT - 120 }  //  was 530 as measured in Chrome
			
	};        
	
	if ( userInputMaxY ) {
		
		//  Data points with Y axis > userInputMaxY will not be shown
		optionsFullsize.vAxis.viewWindow = { max : userInputMaxY };
	}
	
	var $psm_q_value_count_qc_plot_overlay_container = $("#psm_q_value_count_qc_plot_overlay_container");

	var $psm_q_value_count_qc_plot_overlay_background = $("#psm_q_value_count_qc_plot_overlay_background"); 
	$psm_q_value_count_qc_plot_overlay_background.show();
	$psm_q_value_count_qc_plot_overlay_container.show();

	
	// create the chart

	var data = google.visualization.arrayToDataTable( chartData );
	
	var chartFullsize = new google.visualization.LineChart( $psm_q_value_count_qc_plot_chartDiv[0] );
	
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

QCChartQValueCounts.prototype.resetMaxXMaxY = function() {

//	var objectThis = this;



	var $psm_q_value_count_qc_plot_max_x = $("#psm_q_value_count_qc_plot_max_x");

	var $psm_q_value_count_qc_plot_max_y = $("#psm_q_value_count_qc_plot_max_y");

	$psm_q_value_count_qc_plot_max_x.val( "" );
	$psm_q_value_count_qc_plot_max_y.val( "" );

};
