/**
 * qcPageChart_Peptide_Length_Vs_PSM_Count.js
 * 
 * Javascript for the viewQC.jsp page - Chart Peptide Lengths Vs PSM Count
 * 
 * page variable qcPageChart_Peptide_Length_Vs_PSM_Count
 * 
 * 		!!!!  Currently only works for single search.  
 * 
 * 		The page is designed to work with multiple merged searches 
 * 		but the code and SQL need to be reviewed to determine that the results returned are what the user expects,
 * 		especially for reported peptide level results. 
 * 
 * This code has been updated to cancel existing active AJAX calls when "Update from Database" button is clicked.
 *   This is done so that previous AJAX responses don't overlay new AJAX responses.
 */

//JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";


const reportWebErrorToServer = window.reportWebErrorToServer;
const addToolTips = window.addToolTips;
const _PROXL_DEFAULT_FONT_COLOR = window._PROXL_DEFAULT_FONT_COLOR;



import { webserviceCallStandardPost } from 'page_js/webservice_call_common/webserviceCallStandardPost.js';

import { qc_pages_Single_Merged_Common } from './qc_pages_Single_Merged_Common.js';

import { qcChartDownloadHelp } from './qcChart_Download_Help_HTMLBlock.js';


/**
 * Constructor 
 */
var QCPageChart_Peptide_Length_Vs_PSM_Count = function() {
	
	//  Download data URL
	var _downloadStrutsAction = "downloadQC_PeptideLengthVsPSMCountHistogramSingleSearchChartData.do";

	/**
	 * Overridden for Specific elements like Chart Title and X and Y Axis labels
	 */
	var _CHART_GLOBALS = {
			_CHART_DEFAULT_FONT_SIZE : 12,  //  Default font size - using to set font size for tick marks.
			_TITLE_FONT_SIZE : 15, // In PX
			_AXIS_LABEL_FONT_SIZE : 14, // In PX
			_TICK_MARK_TEXT_FONT_SIZE : 14 // In PX

			, _ENTRY_ANNOTATION_TEXT_SIGNIFICANT_DIGITS : 2
	}
	
	//  From QCPageMain
	var _OVERALL_GLOBALS;

	var _project_search_ids = undefined;

	var _anySearchesHaveScanDataYes = undefined;

	//  Contains {{link_type}} to replace with link type.  Contains {{link_type}}_chart_outer_container_jq chart_outer_container_jq
	var _common_chart_outer_entry_templateHTML = undefined;

	var _common_chart_inner_entry_templateHTML = undefined;

	var _dummy_chart_entry_for_message_templateHTML = undefined;


	var _link_type_crosslink_constant = undefined;
	var _link_type_looplink_constant = undefined;
	var _link_type_unlinked_constant = undefined;
	var _link_type_default_selected = undefined;

	var _link_type_crosslink_LOWER_CASE_constant = undefined;
	var _link_type_looplink_LOWER_CASE_constant = undefined;
	var _link_type_unlinked_LOWER_CASE_constant = undefined;

	var _link_type_combined_LOWER_CASE_constant = undefined;

	//   These will have the link type added in between prefix and suffix, adding a space after link type.
	//       There is no space at start of suffix to support no link type
	var _DUMMY_CHART_STATUS_TEXT_PREFIX_LOADING = undefined;
	var _DUMMY_CHART_STATUS_TEXT_SUFFIX_LOADING = undefined;
	var _DUMMY_CHART_STATUS_TEXT_PREFIX_NO_DATA = undefined;
	var _DUMMY_CHART_STATUS_TEXT_SUFFIX_NO_DATA = undefined;
	var _DUMMY_CHART_STATUS_TEXT_PREFIX_ERROR_LOADING = undefined;
	var _DUMMY_CHART_STATUS_TEXT_SUFFIX_ERROR_LOADING = undefined;

	var _DUMMY_CHART_STATUS_WHOLE_TEXT_SCANS_NOT_UPLOADED = undefined;

	var _IS_LOADED_YES = "YES";
	var _IS_LOADED_NO = "NO";
	var _IS_LOADED_LOADING = "LOADING";
	

	//  passed in functions

	//  Copy references to qcPageMain functions to here
	this._passAJAXErrorTo_handleAJAXError = undefined;
	this._addChartOuterTemplate = undefined;
	this._addChartInnerTemplate = undefined;
	this._placeEmptyDummyChartForMessage = undefined;
	this.getColorAndBarColorFromLinkType = undefined;
	
	var _get_hash_json_Contents = undefined; // function on qcPageMain
	
	
	///////////
	
	//   Variables for this chart
	
	var _chart_isLoaded = _IS_LOADED_NO;

	/**
	 * Init page Actual - Called from qcPageMain.initActual
	 */
	this.initActual = function( params ) {
		try {
			var objectThis = this;

			_OVERALL_GLOBALS = params.OVERALL_GLOBALS;

			_project_search_ids = params.project_search_ids;

			_anySearchesHaveScanDataYes = params.anySearchesHaveScanDataYes;

			//  Contains {{link_type}} to replace with link type.  Contains {{link_type}}_chart_outer_container_jq chart_outer_container_jq
			_common_chart_outer_entry_templateHTML = params.common_chart_outer_entry_templateHTML;
			_common_chart_inner_entry_templateHTML = params.common_chart_inner_entry_templateHTML;
			_dummy_chart_entry_for_message_templateHTML = params.dummy_chart_entry_for_message_templateHTML;

			_link_type_crosslink_constant = params.link_type_crosslink_constant;
			_link_type_looplink_constant = params.link_type_looplink_constant;
			_link_type_unlinked_constant = params.link_type_unlinked_constant;
			_link_type_default_selected = params.link_type_default_selected;

			_link_type_crosslink_LOWER_CASE_constant = params.link_type_crosslink_LOWER_CASE_constant;
			_link_type_looplink_LOWER_CASE_constant = params.link_type_looplink_LOWER_CASE_constant;
			_link_type_unlinked_LOWER_CASE_constant = params.link_type_unlinked_LOWER_CASE_constant;

			_link_type_combined_LOWER_CASE_constant = params.link_type_combined_LOWER_CASE_constant;

			//   These will have the link type added in between prefix and suffix, adding a space after link type.
			//       There is no space at start of suffix to support no link type
			_DUMMY_CHART_STATUS_TEXT_PREFIX_LOADING = params.DUMMY_CHART_STATUS_TEXT_PREFIX_LOADING;
			_DUMMY_CHART_STATUS_TEXT_SUFFIX_LOADING = params.DUMMY_CHART_STATUS_TEXT_SUFFIX_LOADING;
			_DUMMY_CHART_STATUS_TEXT_PREFIX_NO_DATA = params.DUMMY_CHART_STATUS_TEXT_PREFIX_NO_DATA;
			_DUMMY_CHART_STATUS_TEXT_SUFFIX_NO_DATA = params.DUMMY_CHART_STATUS_TEXT_SUFFIX_NO_DATA;
			_DUMMY_CHART_STATUS_TEXT_PREFIX_ERROR_LOADING = params.DUMMY_CHART_STATUS_TEXT_PREFIX_ERROR_LOADING;
			_DUMMY_CHART_STATUS_TEXT_SUFFIX_ERROR_LOADING = params.DUMMY_CHART_STATUS_TEXT_SUFFIX_ERROR_LOADING;

			_DUMMY_CHART_STATUS_WHOLE_TEXT_SCANS_NOT_UPLOADED = params.DUMMY_CHART_STATUS_WHOLE_TEXT_SCANS_NOT_UPLOADED;
			
			//  Copy references to qcPageMain functions to here
			this._passAJAXErrorTo_handleAJAXError = params._passAJAXErrorTo_handleAJAXError;
			this._addChartOuterTemplate = params._addChartOuterTemplate;
			this._addChartInnerTemplate = params._addChartInnerTemplate;
			this._placeEmptyDummyChartForMessage = params._placeEmptyDummyChartForMessage;
			this.getColorAndBarColorFromLinkType = params.getColorAndBarColorFromLinkType

			//  Do not store what is returned from function _get_hash_json_Contents since it can change
			_get_hash_json_Contents = params.get_hash_json_Contents; // function

			this.addClickAndOnChangeHandlers();

		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}

	};



	/**
	 * Add Click and onChange handlers 
	 */
	this.addClickAndOnChangeHandlers = function() {
		var objectThis = this;

	};


	///////////////////////////////////////////

	///////////////////////////////////////////

	/////////   _Peptide_Level_ Statistics


	//////////////////////////////////////////////////////////////////////

	//     Peptide Lengths Histogram


	/**
	 * Clear data for _Peptide_Level__Statistics_Counts
	 */
	this.clearChart = function() {

		_chart_isLoaded = _IS_LOADED_NO;

		var $PeptideLengthVsPSMCountBlock = $("#PeptideLengthVsPSMCountBlock");
		if ( $PeptideLengthVsPSMCountBlock.length === 0 ) {
			throw Error( "unable to find HTML element with id 'PeptideLengthVsPSMCountBlock'" );
		}
		$PeptideLengthVsPSMCountBlock.empty();
	};


	/**
	 * If not currently loaded, load
	 */
	this.loadChartIfNeeded = function() {

		if ( _chart_isLoaded === _IS_LOADED_NO ) {
			this.loadPeptideLengthsHistogram();
		}
	};

	var _activeAjax = null;

	/**
	 * Load the data for  Peptide Lengths Histogram
	 */
	this.loadPeptideLengthsHistogram = function() {
		var objectThis = this;

		_chart_isLoaded = _IS_LOADED_LOADING;

		var $PeptideLengthVsPSMCountBlock = $("#PeptideLengthVsPSMCountBlock");
		if ( $PeptideLengthVsPSMCountBlock.length === 0 ) {
			throw Error( "unable to find HTML element with id 'PeptideLengthVsPSMCountBlock'" );
		}
		$PeptideLengthVsPSMCountBlock.empty();
		
		var hash_json_Contents = _get_hash_json_Contents();

		var selectedLinkTypes = hash_json_Contents.linkTypes;

		//  Make a copy of hash_json_Contents    (  true for deep, target object, source object, <source object 2>, ... )
		var hash_json_Contents_COPY = $.extend( true /*deep*/,  {}, hash_json_Contents );

		// Add cells for selected link types
		selectedLinkTypes.forEach( function ( currentArrayValue, index, array ) {
			var selectedLinkType = currentArrayValue;

			//  Add empty chart with Loading message
			var $chart_outer_container_jq =
				this._addChartOuterTemplate( { linkType : selectedLinkType, $chart_group_container_table_jq : $PeptideLengthVsPSMCountBlock } );
			this._placeEmptyDummyChartForMessage( { 
				$chart_outer_container_jq : $chart_outer_container_jq, 
				//				linkType : selectedLinkType, 
				messagePrefix:  _DUMMY_CHART_STATUS_TEXT_PREFIX_LOADING,
				messageSuffix:  _DUMMY_CHART_STATUS_TEXT_SUFFIX_LOADING
			} );

		}, this /* passed to function as this */ );


		if ( _activeAjax ) {
			_activeAjax.abort();
			_activeAjax = null;
		}

		const ajaxRequestData = { projectSearchIds : _project_search_ids, qcPageQueryJSONRoot : hash_json_Contents };

		const url = "services/qc/dataPage/peptideLengthVsPSMCount";

		const webserviceCallStandardPostResult = webserviceCallStandardPost({ dataToSend : ajaxRequestData, url }); //  External Function

		const promise_webserviceCallStandardPost = webserviceCallStandardPostResult.promise; 
		_activeAjax = webserviceCallStandardPostResult.api;

		promise_webserviceCallStandardPost.catch( ( ) => { _activeAjax = null; } );

		promise_webserviceCallStandardPost.then( ({ responseData }) => {
			try {
				_activeAjax = null;
				var responseParams = { ajaxResponseData : responseData };
				objectThis.loadPeptideLengthsHistogramResponse( responseParams );
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
	};

	/**
	 * Process the AJAX response for Peptide Lengths Counts
	 */
	this.loadPeptideLengthsHistogramResponse = function( params ) {
		var ajaxResponseData = params.ajaxResponseData;

		var peptideLengthVsPSMCount_For_PSMPeptideCutoffsResults = ajaxResponseData.peptideLengthVsPSMCount_For_PSMPeptideCutoffsResults;
		var dataForChartPerLinkTypeList = peptideLengthVsPSMCount_For_PSMPeptideCutoffsResults.dataForChartPerLinkTypeList;

		var $PeptideLengthVsPSMCountBlock = $("#PeptideLengthVsPSMCountBlock");
		if ( $PeptideLengthVsPSMCountBlock.length === 0 ) {
			throw Error( "unable to find HTML element with id 'PeptideLengthVsPSMCountBlock'" );
		}
		$PeptideLengthVsPSMCountBlock.empty();

		dataForChartPerLinkTypeList.forEach( function ( currentArrayValue, indexForLinkType, array ) {

			var entryForLinkType = currentArrayValue;
			var linkType = entryForLinkType.linkType;
			var chartBuckets = entryForLinkType.chartBuckets;

			if ( chartBuckets === null || chartBuckets.length === 0 ) {
				//  No data for this link type

				const $chart_outer_container_jq =
					this._addChartOuterTemplate( { $chart_group_container_table_jq : $PeptideLengthVsPSMCountBlock } );
				//  Add empty chart with No Data message
				this._placeEmptyDummyChartForMessage( { 
					$chart_outer_container_jq : $chart_outer_container_jq, 
					//				linkType : selectedLinkType, 
					messagePrefix:  _DUMMY_CHART_STATUS_TEXT_PREFIX_NO_DATA,
					messageSuffix:  _DUMMY_CHART_STATUS_TEXT_SUFFIX_NO_DATA
				} );

				return;  //  EARLY exit for this array element
			}

			const $chart_outer_container_jq =
				this._addChartOuterTemplate( { $chart_group_container_table_jq : $PeptideLengthVsPSMCountBlock } );
			var $chart_container_jq = this._addChartInnerTemplate( { $chart_outer_container_jq : $chart_outer_container_jq } );

			var colorAndbarColor = this.getColorAndBarColorFromLinkType( linkType );

			this._addPeptideLengthsHistogram_Chart( { entryForLinkType: entryForLinkType, colorAndbarColor : colorAndbarColor, $chartContainer : $chart_container_jq } );
			
			//  Download Data
			
			var hash_json_Contents = _get_hash_json_Contents();
			//  Set link types to chart link type
			hash_json_Contents.linkTypes = [ linkType ];
							
			var downloadDataCallback = function( params ) {
//				var clickedThis = params.clickedThis;
				//  Download the data for params
				const dataToSend = { projectSearchIds : _project_search_ids, qcPageQueryJSONRoot : hash_json_Contents };
				qc_pages_Single_Merged_Common.submitDownloadForParams( { downloadStrutsAction : _downloadStrutsAction, dataToSend } );
			};
			
			//  Get Help tooltip HTML
			var elementId = "psm_level_block_help_tooltip_peptide_length_vs_psm_count_tooltip_" + linkType
			var $psm_level_block_help_tooltip_peptide_length_vs_psm_count_tooltip_LinkType = $("#" + elementId );
			if ( $psm_level_block_help_tooltip_peptide_length_vs_psm_count_tooltip_LinkType.length === 0 ) {
				throw Error( "No element found with id '" + elementId + "' " );
			}
			var helpTooltipHTML = $psm_level_block_help_tooltip_peptide_length_vs_psm_count_tooltip_LinkType.html();

			qcChartDownloadHelp.add_DownloadClickHandlers_HelpTooltip( { 
				$chart_outer_container_for_download_jq :  $chart_outer_container_jq, 
				downloadDataCallback : downloadDataCallback,
				helpTooltipHTML : helpTooltipHTML, 
				helpTooltip_Wide : true 
			} );
			
			// Add tooltips for download links
			addToolTips( $chart_outer_container_jq );

		}, this /* passed to function as this */ );

		_chart_isLoaded = _IS_LOADED_YES;
	};

	/**
	 * Overridden for Specific elements like Chart Title and X and Y Axis labels
	 */
	var PeptideLengthsCHART_GLOBALS = {
			_CHART_DEFAULT_FONT_SIZE : 12,  //  Default font size - using to set font size for tick marks.
			_TITLE_FONT_SIZE : 15, // In PX
			_AXIS_LABEL_FONT_SIZE : 14, // In PX
			_TICK_MARK_TEXT_FONT_SIZE : 14 // In PX
	}

	/**
	 * 
	 */
	this._addPeptideLengthsHistogram_Chart = function( params ) {
		var entryForLinkType = params.entryForLinkType;
		var colorAndbarColor = params.colorAndbarColor;
		var $chartContainer = params.$chartContainer;

		var linkType = entryForLinkType.linkType;
		var chartBuckets = entryForLinkType.chartBuckets;

		//  chart data for Google charts
		var chartData = [];

		var chartDataHeaderEntry = [ 'peptideLength', "Count", { role: 'style' }, {role: "tooltip", 'p': {'html': true} }
//		, {type: 'string', role: 'annotation'}
		]; 
		chartData.push( chartDataHeaderEntry );

		var minpeptideLength = null;
		var maxpeptideLength = null;

		var maxCount = 0;

		for ( var index = 0; index < chartBuckets.length; index++ ) {
			var bucket = chartBuckets[ index ];
			
			var peptideLength = bucket.peptideLength;
			var psmCount = bucket.psmCount;

			var countString = psmCount;
			try {
				countString = psmCount.toLocaleString();
			} catch( e ) {
			}

			var tooltipText = null;
			tooltipText = "<div style='padding: 4px;'>Count: " + countString + "<br>peptide length: " + peptideLength + "</div>";
			var entryAnnotationText = psmCount;

			var chartEntry = [ 
				peptideLength,  
				psmCount, 
				//  Style of the bar
				colorAndbarColor.barColor,
				//  Tool Tip
				tooltipText
//				,
//				entryAnnotationText
				];
			chartData.push( chartEntry );
			if ( psmCount > maxCount ) {
				maxCount = psmCount;
			}
		}

		var vAxisTicks = this._getPeptideLengthsHistogram_ChartTickMarks( { maxValue : maxCount } );

		var barColors = [ colorAndbarColor.color ]; // must be an array

		var chartTitle = 'PSM Count vs/ Peptide Length (' + linkType + ")";
		var optionsFullsize = {
				//  Overridden for Specific elements like Chart Title and X and Y Axis labels
				fontSize: PeptideLengthsCHART_GLOBALS._CHART_DEFAULT_FONT_SIZE,  //  Default font size - using to set font size for tick marks.

				title: chartTitle, // Title above chart
				titleTextStyle: {
					color : _PROXL_DEFAULT_FONT_COLOR, //  Set default font color
//					color: <string>,    // any HTML string color ('red', '#cc00cc')
//					fontName: <string>, // i.e. 'Times New Roman'
					fontSize: PeptideLengthsCHART_GLOBALS._TITLE_FONT_SIZE, // 12, 18 whatever you want (don't specify px)
//					bold: <boolean>,    // true or false
//					italic: <boolean>   // true of false
				},
				//  X axis label below chart
				hAxis: { title: 'Peptide Length', titleTextStyle: { color: 'black', fontSize: PeptideLengthsCHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
				,minValue : entryForLinkType.peptideLengthMin
				,maxValue : entryForLinkType.peptideLengthMax
				,gridlines: {  
					color: 'none'  //  No vertical grid lines on the horzontal axis
				}
				},  
				//  Y axis label left of chart
				vAxis: { title: 'PSM Count', titleTextStyle: { color: 'black', fontSize: PeptideLengthsCHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
//				,baseline: 0     // always start at zero
				,ticks: vAxisTicks
				,maxValue : maxCount
				},
				legend: { position: 'none' }, //  position: 'none':  Don't show legend of bar colors in upper right corner
//				width : 500, 
//				height : 300,   // width and height of chart, otherwise controlled by enclosing div
				bar: { groupWidth: '100%' },  // set bar width large to eliminate space between bars
				colors: barColors,
				tooltip: {isHtml: true}
//				,chartArea : { left : 140, top: 60, 
//				width: objectThis.RETENTION_TIME_COUNT_CHART_WIDTH - 200 ,  //  was 720 as measured in Chrome
//				height : objectThis.RETENTION_TIME_COUNT_CHART_HEIGHT - 120 }  //  was 530 as measured in Chrome
		};        
		// create the chart
		var data = google.visualization.arrayToDataTable( chartData );

		var chartFullsize = new google.visualization.ColumnChart( $chartContainer[0] );

		//  Register for chart errors
		var errorDrawingChart = function( err ) {
			//  Properties of err object
//			id [Required] - The ID of the DOM element containing the chart, or an error message displayed instead of the chart if it cannot be rendered.
//			message [Required] - A short message string describing the error.
//			detailedMessage [Optional] - A detailed explanation of the error.
//			options [Optional]- An object containing custom parameters appropriate to this error and chart type.
			
			//  This thrown string is displayed on the chart on the page as well as logged to browser console and logged to the server 
			throw Error("Chart Error: " + err.message + " :: detailed error msg: " + err.detailedMessage ); 
		}
		google.visualization.events.addListener(chartFullsize, 'error', errorDrawingChart);

		chartFullsize.draw(data, optionsFullsize);
		
		//  Temp code to find <rect> that are the actual data columns
		//     Changing them to green to allow show that they are only the data columns and not other <rect> in the <svg>

//		var $rectanglesInChart_All = $chartContainer.find("rect");

//		$rectanglesInChart_All.each( function() {
//		var $rectangleInChart = $( this );
//		var rectangleFillColor = $rectangleInChart.attr("fill");
//		if ( rectangleFillColor !== undefined ) {
//		if ( rectangleFillColor.toLowerCase() === _OVERALL_GLOBALS.BAR_COLOR_CROSSLINK.toLowerCase() ) {
//		$rectangleInChart.attr("fill","green");
//		var z = 0;
//		}
//		}
//		});

	};

	/**
	 * 
	 */
	this._getPeptideLengthsHistogram_ChartTickMarks = function( params ) {
		var maxValue = params.maxValue;
		if ( maxValue < 5 ) {
			var tickMarks = [ 0 ];
			for ( var counter = 1; counter <= maxValue; counter++ ) {
				tickMarks.push( counter );
			}
			return tickMarks;
		}
		return undefined; //  Use defaults
	};



};

/**
 * page variable 
 */

var qcPageChart_Peptide_Length_Vs_PSM_Count = new QCPageChart_Peptide_Length_Vs_PSM_Count();

export { qcPageChart_Peptide_Length_Vs_PSM_Count }
