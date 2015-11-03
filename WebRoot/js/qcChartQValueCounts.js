
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
				
				prevScanFileId : undefined
				,
				maxValuesInDB : {
					
					maxX : undefined,
					maxY : undefined
				}
			}
			
	};
	
};


///////////

//  Create an instance from the constructor

var qcChartQValueCounts = new QCChartQValueCounts();




//CONSTANTS


QCChartQValueCounts.prototype.Q_VALUE__COUNT_CHART_WIDTH = Q_VALUE__COUNT_CHART_WIDTH;
QCChartQValueCounts.prototype.Q_VALUE__COUNT_CHART_HEIGHT = Q_VALUE__COUNT_CHART_HEIGHT;

QCChartQValueCounts.prototype.RELOAD_Q_VALUE_COUNT_CHART_TIMER_DELAY = RELOAD_Q_VALUE_COUNT_CHART_TIMER_DELAY;  // in Milliseconds


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

//	var $search_name_display_jq = $search_root_jq.find(".search_name_display_jq");

//	var search_name_display_jq = $search_name_display_jq.text();

//	var $delete_search_overlay_search_name = $("#delete_search_overlay_search_name");
//	$delete_search_overlay_search_name.text( search_name_display_jq );



//	var $delete_search_confirm_button = $("#delete_search_confirm_button");
//	$delete_search_confirm_button.data("searchId", searchId);
	
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
	
	//  initialize all "Scans for" checkboxes to checked

	var $psm_q_value_count_qc_plot_link_type_include_jq = $(".psm_q_value_count_qc_plot_link_type_include_jq");

	$psm_q_value_count_qc_plot_link_type_include_jq.each( function(   ) {

		$( this ).prop('checked',true);

	} );

		
	
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
	


	var $psm_q_value_count_qc_plot_current_search_id = $("#psm_q_value_count_qc_plot_current_search_id");
	
	var searchId = $psm_q_value_count_qc_plot_current_search_id.val( );

	
	var selectedLinkTypes = this._getLinkTypesChecked();

	if ( selectedLinkTypes.length === 0 ) {
		

		$(".psm_q_value_count_qc_plot_no_data_jq").show();
		
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
		selectedLinkTypes : selectedLinkTypes } );
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
	
	var chartBuckets = responseData.chartBuckets;
	

	
	if ( chartBuckets.length === 0
			|| ( chartBuckets[ 0 ].binCenter === 0 ) ) {
		
		$(".psm_q_value_count_qc_plot_no_data_jq").show();
		
		$(".psm_q_value_count_qc_plot_have_data_jq").hide();
		
		return;  //  EARLY EXIT
		
	} else {
		
		$(".psm_q_value_count_qc_plot_no_data_jq").hide();

		$(".psm_q_value_count_qc_plot_have_data_jq").show();
	}
	
	
	

	//  chart data for Google charts
	var chartData = [];

	//  output columns specification
//	chartData.push( ["preMZ","count"] );

	//  With Tooltip
	chartData.push( ["q_value",
	                 "total_count",{role: "tooltip",  'p': {'html': true} }
//	                 , { role: 'style' } 
	                 ] );
	
	
	var tooltipZero = "<div style='margin: 10px;'>Total PSMs: " + responseData.qvalueZeroCount + 
	"<br>Q-value <= 0</div>";


	var qvalueZeroXPosition = 0;
	
	chartData.push( [qvalueZeroXPosition, 
	                 responseData.qvalueZeroCount, tooltipZero 
//	                 ,  'stroke-width: 2;stroke-color: blue; '
	                 ] );	
	

	var bucketTotalCount = 0;
	
//	private int qvalueZeroCount;
//	private int qvalueOneCount;
	
	for ( var index = 0; index < chartBuckets.length; index++ ) {

		var bucket = chartBuckets[ index ];
		

//		chartData.push( [bucket.binMiddle, bucket.totalCount  ] );
		
		
		bucketTotalCount = bucket.totalCount; 


		//  With Tooltip
		
		var binEndRounded = bucket.binEnd.toFixed( 2 );
		
		var tooltip = "<div style='margin: 10px;'>Total PSMs: " + bucketTotalCount + 
		"<br>Q-value <= " + binEndRounded + "</div>";

		chartData.push( [bucket.binCenter, 
		                 bucketTotalCount, tooltip 
//		                 ,  'stroke-width: 2;stroke-color: blue; '
		                 ] );
	}

	var tooltipOne = "<div style='margin: 10px;'>Total PSMs: " + bucketTotalCount + 
	"<br>Q-value <= 1</div>";

	var qvalueOneXPosition = 1;
	
	chartData.push( [qvalueOneXPosition, 
	                 bucketTotalCount, tooltipOne 
//	                 ,  'stroke-width: 2;stroke-color: blue; '
	                 ] );	
	
	
	var chartTitle = 'PSM Count vs Q-value';
	
	
	var optionsFullsize = {

			title: chartTitle, // Title above chart

			//  X axis label below chart
			hAxis: { title: 'Q-value', titleTextStyle: {color: 'black'}
						,ticks: [ 0.0, 0.2, 0.4, 0.6, 0.8, 1.0 ], format:'#.##'
//							,minValue: -.05
//						,maxValue : maxDataX
			},  
			//  Y axis label left of chart
			vAxis: { title: 'Count', titleTextStyle: {color: 'black'}
						,baseline: 0                    // always start at zero
//						,ticks: vAxisTicks, format:'#,###'
//						,maxValue : maxDataY

			},
			legend: { position: 'none' }, //  position: 'none':  Don't show legend of bar colors in upper right corner
			
			width : objectThis.Q_VALUE__COUNT_CHART_WIDTH, 
			height : objectThis.Q_VALUE__COUNT_CHART_HEIGHT,   // width and height of chart, otherwise controlled by enclosing div
			
//			bar: { groupWidth: 5 },  // set bar width large to eliminate space between bars
//			bar: { groupWidth: '100%' },  // set bar width large to eliminate space between bars

//			colors: ['red','blue'],  //  Color of bars
			
			colors: ['#A55353','#FFF0F0'],  //  Color of bars, Proxl shades of red, total counts is second
			
			
			tooltip: {isHtml: true},
//			isStacked: true

			chartArea : { left : 140, top: 60, 
				width: objectThis.Q_VALUE__COUNT_CHART_WIDTH - 200 ,  //  was 720 as measured in Chrome
				height : objectThis.Q_VALUE__COUNT_CHART_HEIGHT - 120 }  //  was 530 as measured in Chrome
			
	};        
	
	
	// create the chart

	var data = google.visualization.arrayToDataTable( chartData );
	
	var chartFullsize = new google.visualization.LineChart( $psm_q_value_count_qc_plot_chartDiv[0] );
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
	
	var $psm_q_value_count_qc_plot_overlay_container = $("#psm_q_value_count_qc_plot_overlay_container");

	var $psm_q_value_count_qc_plot_overlay_background = $("#psm_q_value_count_qc_plot_overlay_background"); 
	$psm_q_value_count_qc_plot_overlay_background.show();
	$psm_q_value_count_qc_plot_overlay_container.show();

};


