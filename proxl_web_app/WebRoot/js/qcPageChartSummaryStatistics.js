/**
 * qcPageChartSummaryStatistics.js
 * 
 * Javascript for the viewQC.jsp page - Chart Summary Statistics
 * 
 * page variable qcPageChartSummaryStatistics
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


/**
 * Constructor 
 */
var QCPageChartSummaryStatistics = function() {


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

	/////////   Summary Statistics


	/**
	 * Clear data for Summary_Statistics_Counts
	 */
	this.clearChart = function() {

		_chart_isLoaded = _IS_LOADED_NO;

		//  PSM Summary Summary 
		var $Summary_Statistics_CountsBlock = $("#Summary_Statistics_CountsBlock");
		var $Summary_Statistics_CountsBlock_chart_container_jq = $("#Summary_Statistics_CountsBlock .chart_container_jq"); 
		$Summary_Statistics_CountsBlock_chart_container_jq.empty();

		if ( _activeAjax ) {
			_activeAjax.abort();
			_activeAjax = null;
		}
	};


	/**
	 * If not currently loaded, call loadSummaryStatisticsCount()
	 */
	this.loadChartIfNeeded = function() {

		if ( _chart_isLoaded === _IS_LOADED_NO ) {
			this.loadSummaryStatisticsCount();
		}
	};

	/**
	 * Keep this in sync with the number of charts actually created in this.loadSummaryStatisticsCount(...)
	 */
	var _SUMMARY_STATISTICS_CHART_COUNT = 3;

	var _activeAjax = null;

	/**
	 * Load the data for SummaryStatisticsCount
	 */
	this.loadSummaryStatisticsCount = function() {
		var objectThis = this;

		_chart_isLoaded = _IS_LOADED_LOADING;

		// Block for "Summary Statistics"
		var $Summary_Statistics_CountsBlock = $("#Summary_Statistics_CountsBlock");
		if ( $Summary_Statistics_CountsBlock.length === 0 ) {
			throw Error( "unable to find HTML element with id 'Summary_Statistics_CountsBlock'" );
		}
		$Summary_Statistics_CountsBlock.empty();

		for ( var counter = 0; counter < _SUMMARY_STATISTICS_CHART_COUNT; counter++ ) {
			//  Add empty chart with Loading message
			var $chart_outer_container_jq = this._addChartOuterTemplate( { $chart_group_container_table_jq : $Summary_Statistics_CountsBlock } );
			this._placeEmptyDummyChartForMessage( { 
				$chart_outer_container_jq : $chart_outer_container_jq, 
				//				linkType : selectedLinkType, 
				messagePrefix:  _DUMMY_CHART_STATUS_TEXT_PREFIX_LOADING,
				messageSuffix:  _DUMMY_CHART_STATUS_TEXT_SUFFIX_LOADING
			} );
		}

		var hash_json_field_Contents_JSONString = JSON.stringify( _get_hash_json_Contents() );
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
				url : contextPathJSVar + "/services/qc/dataPage/summaryStatistics",
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
						};
						objectThis.loadSummaryStatisticsCountProcessResponse( responseParams );
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
	 * Load the data for Summary_Statistics_Counts
	 */
	this.loadSummaryStatisticsCountProcessResponse = function( params ) {
		var ajaxResponseData = params.ajaxResponseData;
		var ajaxRequestData = params.ajaxRequestData;

		var qc_SummaryCountsResults = ajaxResponseData.qc_SummaryCountsResults;
		var resultsPerLinkTypeList = qc_SummaryCountsResults.resultsPerLinkTypeList;
		var uniqueProteinSequenceIdCountAllLinkTypes = qc_SummaryCountsResults.uniqueProteinSequenceIdCountAllLinkTypes;

		// Block for "Summary Statistics"
		var $Summary_Statistics_CountsBlock = $("#Summary_Statistics_CountsBlock");
		if ( $Summary_Statistics_CountsBlock.length === 0 ) {
			throw Error( "unable to find HTML element with id 'Summary_Statistics_CountsBlock'" );
		}
		$Summary_Statistics_CountsBlock.empty();

		//  Build data for charts
		var psmCountPerType = [];
		var reportedPeptideCountPerType = [];
		var proteinSequenceIdCountPerType = [];

		var foundDataForAtLeastOneLinkType = false;

		resultsPerLinkTypeList.forEach( function ( currentArrayValue, index, array ) {
			var entry = currentArrayValue;

			if ( entry.psmCount === 0 ) {
				//  No data for this entry so skip it
				return;
			}

			foundDataForAtLeastOneLinkType = true;

			var psmCountSingleType = { 
					linkType : entry.linkType,
					count : entry.psmCount
			};

			var reportedPeptideCountSingleType = { 
					linkType : entry.linkType,
					count : entry.uniqueReportedPeptideCount
			};

			var proteinSequenceIdCountSingleType = { 
					linkType : entry.linkType,
					count : entry.uniqueProteinSequenceIdCount
			};

			psmCountPerType.push( psmCountSingleType );
			reportedPeptideCountPerType.push( reportedPeptideCountSingleType );
			proteinSequenceIdCountPerType.push( proteinSequenceIdCountSingleType );

		}, this /* passed to function as this */ );

		if ( ! foundDataForAtLeastOneLinkType ) {

			for ( var counter = 0; counter < _SUMMARY_STATISTICS_CHART_COUNT; counter++ ) {

				var $chart_outer_container_jq =
					this._addChartOuterTemplate( { $chart_group_container_table_jq : $Summary_Statistics_CountsBlock } );

				//  Add empty chart with No Data message
				this._placeEmptyDummyChartForMessage( { 
					$chart_outer_container_jq : $chart_outer_container_jq, 
//					linkType : selectedLinkType, 
					messagePrefix:  _DUMMY_CHART_STATUS_TEXT_PREFIX_NO_DATA,
					messageSuffix:  _DUMMY_CHART_STATUS_TEXT_SUFFIX_NO_DATA
				} );
			}

			//  Exit since no data found

			return;  //  EARLY EXIT 
		}


		//  Keep _SUMMARY_STATISTICS_CHART_COUNT in sync with the actual number of charts created here

		var $chart_outer_container_jq =
			this._addChartOuterTemplate( { $chart_group_container_table_jq : $Summary_Statistics_CountsBlock } );

		var $chart_container_jq = this._addChartInnerTemplate( { $chart_outer_container_jq : $chart_outer_container_jq } );

		this._addSummaryChart( { 
			chartTitle : 'PSM Count',
			dataWithOneElementPerType: psmCountPerType, 
			$chartContainer : $chart_container_jq } );


		var $chart_outer_container_jq =
			this._addChartOuterTemplate( { $chart_group_container_table_jq : $Summary_Statistics_CountsBlock } );

		var $chart_container_jq = this._addChartInnerTemplate( { $chart_outer_container_jq : $chart_outer_container_jq } );

		this._addSummaryChart( { 
			chartTitle : 'Peptide Count',
			dataWithOneElementPerType: reportedPeptideCountPerType, 
			$chartContainer : $chart_container_jq } );


		var $chart_outer_container_jq =
			this._addChartOuterTemplate( { $chart_group_container_table_jq : $Summary_Statistics_CountsBlock } );

		var $chart_container_jq = this._addChartInnerTemplate( { $chart_outer_container_jq : $chart_outer_container_jq } );

		this._addSummaryChart( { 
			chartTitle : 'Protein Count',
			dataWithOneElementPerType: proteinSequenceIdCountPerType, 
			combinedCount : uniqueProteinSequenceIdCountAllLinkTypes,
			$chartContainer : $chart_container_jq } );

		chartDownload.addDownloadClickHandlers( { $chart_outer_container_for_download_jq :  $Summary_Statistics_CountsBlock } );
		// Add tooltips for download links
		addToolTips( $Summary_Statistics_CountsBlock );
	};

	/**
	 * Add Summary Chart
	 */
	this._addSummaryChart = function( params ) {
		var chartTitleParam = params.chartTitle;
		var dataWithOneElementPerType = params.dataWithOneElementPerType;
		var combinedCountParam = params.combinedCount;
		var $chartContainer = params.$chartContainer;

//		var combinedCount = 0;
//
//		if ( combinedCountParam !== undefined ) {
//			combinedCount = combinedCountParam;
//
//		} else {
//
//			dataWithOneElementPerType.forEach( function ( currentArrayValue, index, array ) {
//				var entry = currentArrayValue;
//				combinedCount += entry.count;
//			}, this /* passed to function as this */ );
//		}

		var $chart_container_jqHTMLElement = $chartContainer[ 0 ];

		if ( combinedCountParam !== undefined ) {

			//  Only for Protein
			
			var totalForCombined = { 
					linkType : _link_type_combined_LOWER_CASE_constant, 
					count : combinedCountParam };

			dataWithOneElementPerType.push( totalForCombined );
		}

		var chartTitle = chartTitleParam;

//		chart data for Google charts
		var chartData = [];

		var barColors = [  ]; // must be an array

		var chartDataHeaderEntry = [ 'Link Type', "Count", 
			{ role: 'style' },  // Style of the bar 
			{role: "tooltip", 'p': {'html': true} }
			, {type: 'string', role: 'annotation'}
			]; 
		chartData.push( chartDataHeaderEntry );

		var maxYvalue = 0;

		dataWithOneElementPerType.forEach( function ( currentArrayValue, index, array ) {
			var totalForLinkType = currentArrayValue;

			var linkType = totalForLinkType.linkType;
			var colorAndbarColor = this.getColorAndBarColorFromLinkType( linkType );

			var chartY = totalForLinkType.count;

			if ( chartY === undefined ) {
				chartY = 0;
			}

			var countString = totalForLinkType.count;
			try {
				countString = totalForLinkType.count.toLocaleString();
			} catch( e ) {
			}

			var tooltipText = "<div  style='padding: 4px;'>Count: " + countString + "</div>";
			var entryAnnotationText = countString;

			var chartEntry = [ 
				linkType,
				chartY, 
				//  Style of bar
				colorAndbarColor.barColor,
				//  Tool Tip
				tooltipText
				,entryAnnotationText
				];
			chartData.push( chartEntry );
			if ( chartY > maxYvalue ) {
				maxYvalue = chartY;
			}

			barColors.push( colorAndbarColor.color );

		}, this /* passed to function as this */ );

//		var vAxisTicks = this._get___________TickMarks( { maxValue : maxYvalue } );

		var optionsFullsize = {
				//  Overridden for Specific elements like Chart Title and X and Y Axis labels
				fontSize: _CHART_GLOBALS._CHART_DEFAULT_FONT_SIZE,  //  Default font size - using to set font size for tick marks.

				title: chartTitle, // Title above chart
				titleTextStyle: {
					color : _PROXL_DEFAULT_FONT_COLOR, //  Set default font color
//					color: <string>,    // any HTML string color ('red', '#cc00cc')
//					fontName: <string>, // i.e. 'Times New Roman'
					fontSize: _CHART_GLOBALS._TITLE_FONT_SIZE, // 12, 18 whatever you want (don't specify px)
//					bold: <boolean>,    // true or false
//					italic: <boolean>   // true of false
				},
				//  X axis label below chart
				hAxis: { title: 'Link Type', titleTextStyle: { color: 'black', fontSize: _CHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
				},  
				//  Y axis label left of chart
				vAxis: { title: 'Count', titleTextStyle: { color: 'black', fontSize: _CHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
				,baseline: 0     // always start at zero
//				,ticks: vAxisTicks
//				,maxValue : XXXXX
				},
				legend: { position: 'none' }, //  position: 'none':  Don't show legend of bar colors in upper right corner
//				width : 500, 
//				height : 300,   // width and height of chart, otherwise controlled by enclosing div
//				colors: barColors,  //  Assigned to each bar
				tooltip: {isHtml: true}
//				,chartArea : { left : 140, top: 60, 
//				width: objectThis.RETENTION_TIME_COUNT_CHART_WIDTH - 200 ,  //  was 720 as measured in Chrome
//				height : objectThis.RETENTION_TIME_COUNT_CHART_HEIGHT - 120 }  //  was 530 as measured in Chrome
		};        
//		create the chart
		var data = google.visualization.arrayToDataTable( chartData );
		var chartFullsize = new google.visualization.ColumnChart( $chart_container_jqHTMLElement );

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

	};



};

/**
 * page variable 
 */

var qcPageChartSummaryStatistics = new QCPageChartSummaryStatistics();
