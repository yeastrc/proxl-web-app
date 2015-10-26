
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
			}
			
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

		var clickThis = this;

		objectThis.scanRetentionTimeQCPlotClickHandler( clickThis, eventObject );
		
		return false;
	});	 	
	
	$(".scan_retention_time_qc_plot_overlay_close_parts_jq").click(function(eventObject) {

		var clickThis = this;
		
		if ( objectThis.reloadRetentionTimeCountChartTimerId ) {
			
			clearTimeout( objectThis.reloadRetentionTimeCountChartTimerId );
			
			objectThis.reloadRetentionTimeCountChartTimerId = null;
		}

		objectThis.closeScanRetentionTimeQCPlotOverlay( clickThis, eventObject );
		
		return false;
	});

	
	$(".scan_retention_time_qc_plot_on_change_jq").change(function(eventObject) {
		
		objectThis.createRetentionTimeCountChartFromPageParams( );
		
		return false;
	});
	

	$(".scan_retention_time_qc_plot_on_change_jq").keyup(function(eventObject) {
		
		if ( objectThis.reloadRetentionTimeCountChartTimerId ) {
			
			clearTimeout( objectThis.reloadRetentionTimeCountChartTimerId );
			
			objectThis.reloadRetentionTimeCountChartTimerId = null;
		}

		
		objectThis.reloadRetentionTimeCountChartTimerId = setTimeout( function() {
				
			objectThis.createRetentionTimeCountChartFromPageParams( );
				
		}, objectThis.RELOAD_RETENTION_COUNT_CHART_TIMER_DELAY );
		
		return false;
	});
	
		
	
	$("#scan_retention_time_qc_plot_max_reset_button").click(function(eventObject) {

//		var clickThis = this;
		
		objectThis.resetRetentionTimeCountChartMaxXMaxY();

		objectThis.createRetentionTimeCountChartFromPageParams( );
		
		return false;
	});
	
	
	$( "#scan_retention_time_qc_plot_download_svg" ).click( function() {
		var d = new Date().toISOString().slice(0, 19).replace(/-/g, "");
		var $svg_image_inner_container_div__svg_merged_image_svg_jq = $( "#scan_retention_time_qc_plot_chartDiv svg " );
		var svgContents = $svg_image_inner_container_div__svg_merged_image_svg_jq.html();
		var fullSVG_String = "<svg id=\"svg\">" + svgContents +"</svg>";
		var svgBase64Ecoded = Base64.encode( fullSVG_String );
		var hrefString = "data:application/svg+xml;base64," + svgBase64Ecoded;
		var downloadFilename = "scan_retention_time_" + d + ".svg";
		$(this).attr("href", hrefString ).attr("download", downloadFilename );
	});
	
	
};



/////////////////////////////////////////


//   Scan Retention Time QC Plot


//var scanRetentionTimeQCPlot




/////////////////

QCChartRetentionTime.prototype.scanRetentionTimeQCPlotClickHandler = function(clickThis, eventObject) {

	var objectThis = this;
	
	objectThis.openScanRetentionTimeQCPlotOverlay(clickThis, eventObject);

	return;

};

///////////

QCChartRetentionTime.prototype.openScanRetentionTimeQCPlotOverlay = function(clickThis, eventObject) {

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
	
	var $scan_retention_time_qc_plot_scan_file_id = $("#scan_retention_time_qc_plot_scan_file_id");

	var $PEPTIDE_Q_VALUE_CUTOFF_DEFAULT = $("#PEPTIDE_Q_VALUE_CUTOFF_DEFAULT");
	
	var PEPTIDE_Q_VALUE_CUTOFF_DEFAULT = $PEPTIDE_Q_VALUE_CUTOFF_DEFAULT.val();
	
	var $scan_retention_time_qc_plot_psmQValueCutoff = $("#scan_retention_time_qc_plot_psmQValueCutoff");
	
	$scan_retention_time_qc_plot_psmQValueCutoff.val( PEPTIDE_Q_VALUE_CUTOFF_DEFAULT );
	
	
	//  initialize all "Scans for" checkboxes to checked

	var $scan_retention_time_qc_plot_scans_include_jq = $(".scan_retention_time_qc_plot_scans_include_jq");

	$scan_retention_time_qc_plot_scans_include_jq.each( function(   ) {

		$( this ).prop('checked',true);

	} );

		
	
	objectThis.getScanFileIdsForSearchId( { 
		searchId: searchId, 
		$scanFileIdSelect : $scan_retention_time_qc_plot_scan_file_id,
		callback: objectThis.createRetentionTimeCountChartFromPageParams,
		callbackThis : objectThis } );
};


//////////	/

QCChartRetentionTime.prototype.closeScanRetentionTimeQCPlotOverlay = function(clickThis, eventObject) {

	$(".scan_retention_time_qc_plot_overlay_show_hide_parts_jq").hide();
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


			objectThis.getScanFileIdsForSearchIdResponse(requestData, data, params);
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

	var $scanFileIdSelect = originalParams.$scanFileIdSelect;
	var callback = originalParams.callback;
	

	if ( scanFiles.length === 0 ) {
		
		throw "scanFileIds.length === 0";
	}
	
	$scanFileIdSelect.empty();
	
	var optionsHTMLarray = [];
	
	for ( var scanFilesIndex = 0; scanFilesIndex < scanFiles.length; scanFilesIndex++ ) {
		
		var scanFile = scanFiles[ scanFilesIndex ];
		
		var html = "<option value='" + scanFile.id + "'>" + scanFile.filename + "</option>";
			
		optionsHTMLarray.push( html );
	}
	
	var optionsHTML = optionsHTMLarray.join("");
	
	$scanFileIdSelect.append( optionsHTML );
	
	
	if ( callback ) {
		
		var callbackThis = originalParams.callbackThis;
		
		callback.call( callbackThis );  // set the "this" to callbackThis
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

	
	
	var $scan_retention_time_qc_plot_current_search_id = $("#scan_retention_time_qc_plot_current_search_id");
	
	var searchId = $scan_retention_time_qc_plot_current_search_id.val( );

	var $scan_retention_time_qc_plot_scan_file_id = $("#scan_retention_time_qc_plot_scan_file_id");
	
	var scanFileId = $scan_retention_time_qc_plot_scan_file_id.val();
	
	var scanFileName = $( "#scan_retention_time_qc_plot_scan_file_id option:selected" ).text();

	var $scan_retention_time_qc_plot_psmQValueCutoff = $("#scan_retention_time_qc_plot_psmQValueCutoff");
	
	var psmQValueCutoff = $scan_retention_time_qc_plot_psmQValueCutoff.val();
	
	
	if ( isNaN( parseFloat( psmQValueCutoff ) ) ) {
		
//		alert( "psm cutoff not a number" );
		
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

	
	
	var scansForSelectedLinkTypes = [];

	var $scan_retention_time_qc_plot_scans_include_jq = $(".scan_retention_time_qc_plot_scans_include_jq");

	$scan_retention_time_qc_plot_scans_include_jq.each( function(   ) {

		var $thisCheckbox = $( this );
		
		if ( $thisCheckbox.prop('checked') ) {
			
			var checkboxValue = $thisCheckbox.attr("value");
		
			scansForSelectedLinkTypes.push( checkboxValue );
		}
	} );

	
	objectThis.createRetentionTimeCountChart( { psmQValueCutoff : psmQValueCutoff, 
		searchId : searchId,
		scanFileId : scanFileId, scanFileName : scanFileName,
		scansForSelectedLinkTypes : scansForSelectedLinkTypes,
		userInputMaxX : userInputMaxX,
		userInputMaxY : userInputMaxY } );
};



//////////

///  

QCChartRetentionTime.prototype.createRetentionTimeCountChart = function( params ) {

	var objectThis = this;

	
	
	var searchId = params.searchId;
	var scanFileId = params.scanFileId;
	var psmQValueCutoff = params.psmQValueCutoff;
	var scansForSelectedLinkTypes = params.scansForSelectedLinkTypes;
	var userInputMaxXString = params.userInputMaxX;
	
	if ( ! qcChartsInitialized ) {
		
		throw "qcChartsInitialized is false"; 
	}
	
	
	
	var $scan_retention_time_qc_plot_chartDiv = $("#scan_retention_time_qc_plot_chartDiv");
	
	$scan_retention_time_qc_plot_chartDiv.empty();


	var _URL = contextPathJSVar + "/services/qcplot/getScanRetentionTime";
	
	var requestData = {
			scansForSelectedLinkTypes : scansForSelectedLinkTypes,
			searchId : searchId,
			scanFileId : scanFileId,
			psmQValueCutoff : psmQValueCutoff
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

			objectThis.createRetentionTimeCountChartResponse(requestData, data, params);
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

