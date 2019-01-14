/**
 * qcPageChart_M_Over_Z_Statistics_PSM.js
 * 
 * Javascript for the viewQC.jsp page - Chart M/Z Statistics - in PSM Level section
 * 
 * page variable qcPageChart_M_Over_Z_Statistics_PSM
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

import { qc_pages_Single_Merged_Common } from './qc_pages_Single_Merged_Common.js';

import { qcChartDownloadHelp } from './qcChart_Download_Help_HTMLBlock.js';

/**
 * Constructor 
 */
var QCPageChart_M_Over_Z_Statistics_PSM = function() {
	
	//  Download data URL
	var _downloadStrutsAction = "downloadQC_Psm_M_Over_Z_ChartData.do";

//	/**
//	 * Overridden for Specific elements like Chart Title and X and Y Axis labels
//	 */
//	var _CHART_GLOBALS = {
//			_CHART_DEFAULT_FONT_SIZE : 12,  //  Default font size - using to set font size for tick marks.
//			_TITLE_FONT_SIZE : 15, // In PX
//			_AXIS_LABEL_FONT_SIZE : 14, // In PX
//			_TICK_MARK_TEXT_FONT_SIZE : 14 // In PX
//
//			, _ENTRY_ANNOTATION_TEXT_SIGNIFICANT_DIGITS : 2
//	}
	
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
		var skipAddClickHandlers = params.skipAddClickHandlers;  // set to true when used by Merged chart
		
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

	/////////   


	/**
	 * Clear data for 
	 */
	this.clearChart = function() {

		_chart_isLoaded = _IS_LOADED_NO;

		var $PSM_M_Over_Z_CountsBlock = $("#PSM_M_Over_Z_CountsBlock");
		if ( $PSM_M_Over_Z_CountsBlock.length === 0 ) {
			throw Error( "unable to find HTML element with id 'PSM_M_Over_Z_CountsBlock'" );
		}
		$PSM_M_Over_Z_CountsBlock.empty();

		if ( _activeAjax ) {
			_activeAjax.abort();
			_activeAjax = null;
		}
	};


	/**
	 * If not currently loaded, load
	 */
	this.loadChartIfNeeded = function() {

		if ( _chart_isLoaded === _IS_LOADED_NO ) {
			this.load_M_Over_Z_For_PSMs_Histogram();
		}
	};

	var _activeAjax = null;

	/**
	 * Load the data for  M/Z for PSMs Histogram
	 */
	this.load_M_Over_Z_For_PSMs_Histogram = function() {
		var objectThis = this;

		_chart_isLoaded = _IS_LOADED_LOADING;

		var $PSM_M_Over_Z_CountsBlock = $("#PSM_M_Over_Z_CountsBlock");
		if ( $PSM_M_Over_Z_CountsBlock.length === 0 ) {
			throw Error( "unable to find HTML element with id 'PSM_M_Over_Z_CountsBlock'" );
		}
		$PSM_M_Over_Z_CountsBlock.empty();
		
		var hash_json_Contents = _get_hash_json_Contents();

		var selectedLinkTypes = hash_json_Contents.linkTypes;

		if ( ! _anySearchesHaveScanDataYes ) {

			// Show cells for selected link types
			selectedLinkTypes.forEach( function ( currentArrayValue, index, array ) {
				var selectedLinkType = currentArrayValue;

				//  Add empty chart with Loading message
				var $chart_outer_container_jq =
					this._addChartOuterTemplate( { $chart_group_container_table_jq : $PSM_M_Over_Z_CountsBlock } );
				this._placeEmptyDummyChartForMessage( { 
					$chart_outer_container_jq : $chart_outer_container_jq, 
//					linkType : selectedLinkType, 
					messageWhole:  _DUMMY_CHART_STATUS_WHOLE_TEXT_SCANS_NOT_UPLOADED
				} );
			}, this /* passed to function as this */ );

			_chart_isLoaded = _IS_LOADED_YES;

			//  Exit since no data to display

			return;  //  EARLY EXIT
		}

		// Add cells for selected link types
		selectedLinkTypes.forEach( function ( currentArrayValue, index, array ) {
			var selectedLinkType = currentArrayValue;

			//  Add empty chart with Loading message
			var $chart_outer_container_jq =
				this._addChartOuterTemplate( { linkType : selectedLinkType, $chart_group_container_table_jq : $PSM_M_Over_Z_CountsBlock } );

			//  Add empty chart with Loading message
			this._placeEmptyDummyChartForMessage( { 
				$chart_outer_container_jq : $chart_outer_container_jq, 
				//				linkType : selectedLinkType, 
				messagePrefix:  _DUMMY_CHART_STATUS_TEXT_PREFIX_LOADING,
				messageSuffix:  _DUMMY_CHART_STATUS_TEXT_SUFFIX_LOADING
			} );

		}, this /* passed to function as this */ );

		var hash_json_field_Contents_JSONString = JSON.stringify( hash_json_Contents );
		var ajaxRequestData = {
				project_search_id : _project_search_ids,
				filterCriteria : hash_json_field_Contents_JSONString
		};

		if ( _activeAjax ) {
			_activeAjax.abort();
			_activeAjax = null;
		}
		//  Set to returned jQuery XMLHttpRequest (jqXHR) object
		_activeAjax =
			$.ajax({
				url : "services/qc/dataPage/mzForPSMsHistogramCounts",
				traditional: true,  //  Force traditional serialization of the data sent
				//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
				//   So project_search_ids array is passed as "project_search_ids=<value>" which is what Jersey expects
				data : ajaxRequestData,  // The data sent as params on the URL
				dataType : "json",
				success : function( ajaxResponseData ) {
					try {
						_activeAjax = null;
						var responseParams = {
								ajaxResponseData : ajaxResponseData, 
								ajaxRequestData : ajaxRequestData
//								,
//								topTRelement : topTRelement
						};
						objectThis.load_M_Over_Z_For_PSMs_HistogramResponse( responseParams );
//						$topTRelement.data( _DATA_LOADED_DATA_KEY, true );
					} catch( e ) {
						reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
						throw e;
					}
				},
				failure: function(errMsg) {
					_activeAjax = null;
					handleAJAXFailure( errMsg );
				},
				error : function(jqXHR, textStatus, errorThrown) {
					_activeAjax = null;
					if ( objectThis._passAJAXErrorTo_handleAJAXError(jqXHR, textStatus, errorThrown) ) {
						handleAJAXError(jqXHR, textStatus, errorThrown);
					}
				}
			});
	};

	/**
	 * Process AJAX Response
	 */
	this.load_M_Over_Z_For_PSMs_HistogramResponse = function( params ) {
		var ajaxResponseData = params.ajaxResponseData;
		var ajaxRequestData = params.ajaxRequestData;

		var preMZ_Histogram_For_PSMPeptideCutoffsResults = ajaxResponseData.preMZ_Histogram_For_PSMPeptideCutoffsResults;
		var dataForChartPerLinkTypeList = preMZ_Histogram_For_PSMPeptideCutoffsResults.dataForChartPerLinkTypeList;

		var $PSM_M_Over_Z_CountsBlock = $("#PSM_M_Over_Z_CountsBlock");
		if ( $PSM_M_Over_Z_CountsBlock.length === 0 ) {
			throw Error( "unable to find HTML element with id 'PSM_M_Over_Z_CountsBlock'" );
		}

		$PSM_M_Over_Z_CountsBlock.empty();

		dataForChartPerLinkTypeList.forEach( function ( currentArrayValue, indexForLinkType, array ) {
			var entryForLinkType = currentArrayValue;
			var linkType = entryForLinkType.linkType;
			var chartBuckets = entryForLinkType.chartBuckets;

			if ( chartBuckets === undefined ||chartBuckets === null || chartBuckets.length === 0 ) {
				//  No data for this link type

				//  Add empty chart with No Data message
				var $chart_outer_container_jq =
					this._addChartOuterTemplate( { $chart_group_container_table_jq : $PSM_M_Over_Z_CountsBlock } );
				this._placeEmptyDummyChartForMessage( { 
					$chart_outer_container_jq : $chart_outer_container_jq, 
					linkType : linkType, 
					messagePrefix:  _DUMMY_CHART_STATUS_TEXT_PREFIX_NO_DATA,
					messageSuffix:  _DUMMY_CHART_STATUS_TEXT_SUFFIX_NO_DATA
				} );

				return;  //  EARLY exit for this array element
			}
			var $chart_outer_container_jq =
				this._addChartOuterTemplate( { $chart_group_container_table_jq : $PSM_M_Over_Z_CountsBlock } );
			var $chart_container_jq = this._addChartInnerTemplate( { $chart_outer_container_jq : $chart_outer_container_jq } );

			var colorAndbarColor = this.getColorAndBarColorFromLinkType( linkType );

			this._add_M_Over_Z_For_PSMs_Histogram_Chart( { entryForLinkType: entryForLinkType, colorAndbarColor: colorAndbarColor, $chartContainer : $chart_container_jq } );

			//  Download Data Setup
			
			var hash_json_Contents = _get_hash_json_Contents();
			//  Set link types to chart link type
			hash_json_Contents.linkTypes = [ linkType ];
							
			var downloadDataCallback = function( params ) {
//				var clickedThis = params.clickedThis;

				//  Download the data for params
				qc_pages_Single_Merged_Common.submitDownloadForParams( { downloadStrutsAction : _downloadStrutsAction, project_search_ids : _project_search_ids, hash_json_Contents : hash_json_Contents } );
			};
			
			//  Get Help tooltip HTML
			var elementId = "psm_level_block_help_tooltip_m_over_z_statistics_" + linkType
			var $psm_level_block_help_tooltip_m_over_z_statistics_LinkType = $("#" + elementId );
			if ( $psm_level_block_help_tooltip_m_over_z_statistics_LinkType.length === 0 ) {
				throw Error( "No element found with id '" + elementId + "' " );
			}
			var helpTooltipHTML = $psm_level_block_help_tooltip_m_over_z_statistics_LinkType.html();

			qcChartDownloadHelp.add_DownloadClickHandlers_HelpTooltip( { 
				$chart_outer_container_for_download_jq :  $chart_outer_container_jq, 
				downloadDataCallback : downloadDataCallback,
				helpTooltipHTML : helpTooltipHTML, 
				helpTooltip_Wide : false 
			} );
			
			// Add tooltips for download links
			addToolTips( $chart_outer_container_jq );
			
		}, this /* passed to function as this */ );

		_chart_isLoaded = _IS_LOADED_YES;
	};

	//  Overridden for Specific elements like Chart Title and X and Y Axis labels
	var _M_Over_Z_For_PSMs_CHART_GLOBALS = {
			_CHART_DEFAULT_FONT_SIZE : 12,  //  Default font size - using to set font size for tick marks.
			_TITLE_FONT_SIZE : 15, // In PX
			_AXIS_LABEL_FONT_SIZE : 14, // In PX
			_TICK_MARK_TEXT_FONT_SIZE : 14, // In PX
	}

	/**
	 * 
	 */
	this._add_M_Over_Z_For_PSMs_Histogram_Chart = function( params ) {
		var entryForLinkType = params.entryForLinkType;
		var colorAndbarColor = params.colorAndbarColor;
		var $chartContainer = params.$chartContainer;
		var enableInteractivityOption = params.enableInteractivityOption;
		var chartReadyCallback = params.chartReadyCallback;
		var chartWidthParam = params.chartWidth;
		var chartHeightParam = params.chartHeight;
		
		if ( ! chartWidthParam ) {
			chartWidthParam = undefined;
		}
		if ( ! chartHeightParam ) {
			chartHeightParam = undefined;
		}

		var linkType = entryForLinkType.linkType;
		var chartBuckets = entryForLinkType.chartBuckets;

		//  chart data for Google charts
		var chartData = [];

		var chartDataHeaderEntry = [ 'preMZ', "Count", { role: 'style' }, {role: "tooltip", 'p': {'html': true} }
//		, {type: 'string', role: 'annotation'}
		]; 
		chartData.push( chartDataHeaderEntry );

		var maxCount = 0;

		for ( var index = 0; index < chartBuckets.length; index++ ) {
			var bucket = chartBuckets[ index ];

			var countString = bucket.count;
			var binStartString = bucket.binStart;
			var binEndString = bucket.binEnd;
			try {
				countString = bucket.count.toLocaleString();
				binStartString = bucket.binStart.toLocaleString();
				binEndString = bucket.binEnd.toLocaleString();
			} catch( e ) {
			}

			var tooltipText = "<div style='padding: 4px;'>Count: " + countString +
			"<br>m/z approximately " + binStartString + " to " + binEndString + "</div>";

//			var entryAnnotationText = countString;

			var chartEntry = [ 
				bucket.binCenter,  
				bucket.count, 
				//  Style of bar
				colorAndbarColor.barColor,
				//  Tool Tip
				tooltipText
//				,
//				entryAnnotationText
				];
			chartData.push( chartEntry );
			if ( bucket.count > maxCount ) {
				maxCount = bucket.count;
			}

		}

		var vAxisTicks = this._get_M_Over_Z_For_PSMs_Histogram_ChartTickMarks( { maxValue : maxCount } );

		var barColors = [ colorAndbarColor.color ]; // must be an array

		//  Chart title and layout also in viewQC_MZ_Data_NoDataAvailable.jsp for empty chart when no M/Z data

		var chartTitle = 'PSM Count vs/ m/z (' + linkType + ")";
		var optionsFullsize = {
				//  Overridden for Specific elements like Chart Title and X and Y Axis labels
				fontSize: _M_Over_Z_For_PSMs_CHART_GLOBALS._CHART_DEFAULT_FONT_SIZE,  //  Default font size - using to set font size for tick marks.

				title: chartTitle, // Title above chart
				titleTextStyle: {
					color : _PROXL_DEFAULT_FONT_COLOR, //  Set default font color
//					color: <string>,    // any HTML string color ('red', '#cc00cc')
//					fontName: <string>, // i.e. 'Times New Roman'
					fontSize: _M_Over_Z_For_PSMs_CHART_GLOBALS._TITLE_FONT_SIZE, // 12, 18 whatever you want (don't specify px)
//					bold: <boolean>,    // true or false
//					italic: <boolean>   // true of false
				},
				//  X axis label below chart
				hAxis: { title: 'M/Z', titleTextStyle: { color: 'black', fontSize: _M_Over_Z_For_PSMs_CHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
				,gridlines: {  
					color: 'none'  //  No vertical grid lines on the horzontal axis
				}
				},  
				//  Y axis label left of chart
				vAxis: { title: 'Count', titleTextStyle: { color: 'black', fontSize: _M_Over_Z_For_PSMs_CHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
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
				,enableInteractivity : enableInteractivityOption,
				width : chartWidthParam,  // chartWidthParam may have a value or be undefined 
				height : chartHeightParam // chartHeightParam may have a value or be undefined
					
//				,chartArea : { left : 140, top: 60, 
//				width: objectThis.RETENTION_TIME_COUNT_CHART_WIDTH - 200 ,  //  was 720 as measured in Chrome
//				height : objectThis.RETENTION_TIME_COUNT_CHART_HEIGHT - 120 }  //  was 530 as measured in Chrome
		};      
		
		// create the chart
		var data = google.visualization.arrayToDataTable( chartData );
		var chartFullsize = new google.visualization.ColumnChart( $chartContainer[0] );

		if ( chartReadyCallback ) {

			var chartWidth = undefined;
			var chartHeight = undefined;
			
			if ( chartWidthParam ) {
				chartWidth = chartWidthParam;
			} else {
				chartWidth = $chartContainer.width();
			}
			if ( chartHeightParam ) {
				chartHeight = chartHeightParam;
			} else {
				chartHeight = $chartContainer.height();
			}

			var readyDrawingChart = function(  ) {
				setTimeout(function() {  // put in settime to clear the stack.
					try {
						chartReadyCallback( { chart : chartFullsize, chartWidth : chartWidth, chartHeight : chartHeight  } );
					} catch( e ) {
						reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
						throw e;
					}
				}, 10 );
			};
			google.visualization.events.addListener( chartFullsize, 'ready', readyDrawingChart );

		}


		//  Register for chart errors
		var errorDrawingChart = function( err ) {
			//  Properties of err object
//			id [Required] - The ID of the DOM element containing the chart, or an error message displayed instead of the chart if it cannot be rendered.
//			message [Required] - A short message string describing the error.
//			detailedMessage [Optional] - A detailed explanation of the error.
//			options [Optional]- An object containing custom parameters appropriate to this error and chart type.
			
			try {
				//  This thrown string is displayed on the chart on the page as well as logged to browser console and logged to the server 
				throw Error("Chart Error: " + err.message + " :: detailed error msg: " + err.detailedMessage );
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}

		}
		google.visualization.events.addListener(chartFullsize, 'error', errorDrawingChart);

		chartFullsize.draw(data, optionsFullsize);
		
	};

	/**
	 * 
	 */
	this._get_M_Over_Z_For_PSMs_Histogram_ChartTickMarks = function( params ) {
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

var qcPageChart_M_Over_Z_Statistics_PSM = new QCPageChart_M_Over_Z_Statistics_PSM();

export { qcPageChart_M_Over_Z_Statistics_PSM }
