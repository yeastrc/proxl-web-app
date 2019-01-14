/**
 * qcMergedMergedPageChart_PPM_Error_PSM.js
 * 
 * Javascript for the viewQCMerged.jsp page - Chart PPM Error - in PSM Error section
 * 
 * page variable qcMergedPageChart_PPM_Error_PSM
 * 
 * Merged QC Page
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
var QCMergedPageChart_PPM_Error_PSM = function() {
	
	//  Download data URL
	var _downloadStrutsAction = "downloadQC_Psm_PPM_Error_ChartData.do";

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
	var _searchIdsObject_Key_projectSearchId = undefined;

	var _colorsPerSearch = undefined;
	
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
			_searchIdsObject_Key_projectSearchId = params.searchIdsObject_Key_projectSearchId;

			_colorsPerSearch = params.colorsPerSearch;
			
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
	 * Clear data 
	 */
	this.clearChart = function() {

		_chart_isLoaded = _IS_LOADED_NO;

		var $PSM_PPM_Error_CountsBlock = $("#PSM_PPM_Error_CountsBlock");
		$PSM_PPM_Error_CountsBlock.empty();

		//  Abort any active AJAX calls
		if ( _activeAjax ) {
			var objKeys = Object.keys( _activeAjax ); 
			objKeys.forEach( function( element, index, array ) {
				var selectedLinkType = element;
				if ( _activeAjax[ selectedLinkType ] ) {
					_activeAjax[ selectedLinkType ].abort();
					_activeAjax[ selectedLinkType ] = null;
				}
			}, this /* passed to function as this */ );
		}
		_activeAjax = null;
	};


	/**
	 * If not currently loaded, load
	 */
	this.loadChartIfNeeded = function() {

		if ( _chart_isLoaded === _IS_LOADED_NO ) {
			this.load_PPM_Error_For_PSMs_Histogram();
		}
	};

	var _load_PPM_Error_PSMCountActiveAjax = null;

	/**
	 * Load the data for  PPM Error for PSMs Histogram
	 */
	this.load_PPM_Error_For_PSMs_Histogram = function() {
		var objectThis = this;

		_chart_isLoaded = _IS_LOADED_LOADING;

		var $PSM_PPM_Error_CountsBlock = $("#PSM_PPM_Error_CountsBlock");
		$PSM_PPM_Error_CountsBlock.empty();

		var hash_json_Contents = _get_hash_json_Contents();
		
		var selectedLinkTypes = hash_json_Contents.linkTypes;

		if ( ! _anySearchesHaveScanDataYes ) {

			// Show cells for selected link types
			for ( var index = 0; index < selectedLinkTypes.length; index++ ) {
				var selectedLinkType = selectedLinkTypes[ index ];
				//  Add empty chart with Loading message

				var $chart_outer_container_jq =
					this._addChartOuterTemplate( { $chart_group_container_table_jq : $PSM_PPM_Error_CountsBlock } );
				this._placeEmptyDummyChartForMessage( { 
					$chart_outer_container_jq : $chart_outer_container_jq, 
//					linkType : selectedLinkType, 
					messageWhole:  _DUMMY_CHART_STATUS_WHOLE_TEXT_SCANS_NOT_UPLOADED
				} );
			}

			_chart_isLoaded = _IS_LOADED_YES;

			//  Exit since no data to display

			return;  //  EARLY EXIT
		}		

		//  Make a copy of hash_json_Contents    (  true for deep, target object, source object, <source object 2>, ... )
		var hash_json_Contents_COPY = $.extend( true /*deep*/,  {}, hash_json_Contents );

		// Add cells for selected link types
		selectedLinkTypes.forEach( function ( currentArrayValue, indexForLinkType, array ) {
			var selectedLinkType = currentArrayValue;

			//  Add empty chart with Loading message
			var $chart_outer_container_jq =
				this._addChartOuterTemplate( { linkType : selectedLinkType, $chart_group_container_table_jq : $PSM_PPM_Error_CountsBlock } );

			//  Add empty chart with Loading message
			this._placeEmptyDummyChartForMessage( { 
				$chart_outer_container_jq : $chart_outer_container_jq, 
				//				linkType : selectedLinkType, 
				messagePrefix:  _DUMMY_CHART_STATUS_TEXT_PREFIX_LOADING,
				messageSuffix:  _DUMMY_CHART_STATUS_TEXT_SUFFIX_LOADING
			} );

			//  For each selected link type, 
			//    set it as selected link type in hash_json_Contents_COPY,
			//      and create a chart for that

			hash_json_Contents_COPY.linkTypes = [ selectedLinkType ];

			this.load_PPM_Error_For_PSMs_HistogramForLinkType( {  
				selectedLinkType : selectedLinkType,
				indexForLinkType : indexForLinkType,
				hash_json_Contents_COPY : hash_json_Contents_COPY,
				$chart_outer_container_jq : $chart_outer_container_jq
			});

		}, this /* passed to function as this */ );

	};

	var _activeAjax = null;

	/**
	 * Load the data for  PPM Error for PSMs Histogram Specific Link Type
	 */
	this.load_PPM_Error_For_PSMs_HistogramForLinkType = function( params ) {
		var objectThis = this;

		var selectedLinkType = params.selectedLinkType;
		var indexForLinkType = params.indexForLinkType;
		var hash_json_Contents_COPY = params.hash_json_Contents_COPY;
		var $chart_outer_container_jq = params.$chart_outer_container_jq; 


		//  POST body
		var hash_json_field_Contents_JSONString = JSON.stringify( hash_json_Contents_COPY );

		//  Put Project Search Ids on the Query String of the URL
		var projectSearchIdsForQueryStringArray = [];
		_project_search_ids.forEach(function( projectSearchId, index, array) {
			projectSearchIdsForQueryStringArray.push( "project_search_id=" + projectSearchId );
		}, this )
		
		var projectSearchIdsForQueryString = projectSearchIdsForQueryStringArray.join( "&" );
		
		var url = "services/qc/dataPage/ppmError_Merged?" + projectSearchIdsForQueryString;

		if ( _activeAjax && 
				_activeAjax[ selectedLinkType ] ) {
			_activeAjax[ selectedLinkType ].abort();
			_activeAjax[ selectedLinkType ] = null;
		}
		if ( ! _activeAjax ) {
			_activeAjax = {};
		}
		//  Set to returned jQuery XMLHttpRequest (jqXHR) object
		_activeAjax[ selectedLinkType ] = 
			$.ajax({
				type : "POST",
				url : url,
				traditional: true,  //  Force traditional serialization of the data sent
				//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
				//   So project_search_ids array is passed as "project_search_ids=<value>" which is what Jersey expects
				data : hash_json_field_Contents_JSONString,  // The data sent as the POST body
				contentType: "application/json; charset=utf-8",
				dataType : "json",
				success : function( ajaxResponseData ) {
					try {
						if ( _activeAjax ) {
							_activeAjax[ selectedLinkType ] = null;
						}
						var responseParams = {
								ajaxResponseData : ajaxResponseData, 
								selectedLinkType : selectedLinkType,
								indexForLinkType : indexForLinkType,
								$chart_outer_container_jq : $chart_outer_container_jq
						};
						objectThis.load_PPM_Error_For_PSMs_HistogramResponse( responseParams );
//						$topTRelement.data( _DATA_LOADED_DATA_KEY, true );
					} catch( e ) {
						reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
						throw e;
					}
				},
				failure: function(errMsg) {
					if ( _activeAjax ) {
						_activeAjax[ selectedLinkType ] = null;
					}
					handleAJAXFailure( errMsg );
				},
				error : function(jqXHR, textStatus, errorThrown) {
					if ( _activeAjax ) {
						_activeAjax[ selectedLinkType ] = null;
					}
					if ( objectThis._passAJAXErrorTo_handleAJAXError(jqXHR, textStatus, errorThrown) ) {
						handleAJAXError(jqXHR, textStatus, errorThrown);
					}
				}
			});
		// indent due to assignment
	};

	/**
	 * Process AJAX Response
	 */
	this.load_PPM_Error_For_PSMs_HistogramResponse = function( params ) {
		var objectThis = this;

		var ajaxResponseData = params.ajaxResponseData;
		var selectedLinkType = params.selectedLinkType;
		var indexForLinkType = params.indexForLinkType;
		var $chart_outer_container_jq = params.$chart_outer_container_jq;

		var dataForChartPerLinkTypeList = ajaxResponseData.dataForChartPerLinkTypeList;

		if ( dataForChartPerLinkTypeList.length !== 1 ) {
			throw Error( "dataForChartPerLinkTypeList.length !== 1, is = " + dataForChartPerLinkTypeList.length );
		}

		var entryForLinkType = dataForChartPerLinkTypeList[ 0 ];

		var linkType = entryForLinkType.linkType;
		var chartBuckets = entryForLinkType.chartBuckets;

		$chart_outer_container_jq.empty();

		var dataFound = entryForLinkType.dataFound;

		if ( ! dataFound ) {
			//  No data for this link type

			//  Add empty chart with No Data message
			this._placeEmptyDummyChartForMessage( { 
				$chart_outer_container_jq : $chart_outer_container_jq, 
				//				linkType : selectedLinkType, 
				messagePrefix:  _DUMMY_CHART_STATUS_TEXT_PREFIX_NO_DATA,
				messageSuffix:  _DUMMY_CHART_STATUS_TEXT_SUFFIX_NO_DATA
			} );

		} else {

			var $chart_container_jq = this._addChartInnerTemplate( { $chart_outer_container_jq : $chart_outer_container_jq } );

			var colorAndbarColor = this.getColorAndBarColorFromLinkType( linkType );

			this._add_PPM_Error_For_PSMs_Chart( { entryForLinkType: entryForLinkType, colorAndbarColor: colorAndbarColor, chartIndex : indexForLinkType, $chartContainer : $chart_container_jq } );

			//  Download Data Setup
			
			var hash_json_Contents = _get_hash_json_Contents();
			//  Set link types to chart link type
			hash_json_Contents.linkTypes = [ linkType ];

			var downloadSummaryDataCallback = function( params ) {
//				var clickedThis = params.clickedThis;
				
				qcChartDownloadHelp._downloadBoxplotChartSummaryData( { filenamePartChartName : "qc-psm-ppm-error-summary-" , entryForLinkType : entryForLinkType, _project_search_ids : _project_search_ids } );
			};
			
			var downloadDataCallback = function( params ) {
//				var clickedThis = params.clickedThis;

				//  Download the data for params
				qc_pages_Single_Merged_Common.submitDownloadForParams( { downloadStrutsAction : _downloadStrutsAction, project_search_ids : _project_search_ids, hash_json_Contents : hash_json_Contents } );
			};

			//  Get Help tooltip HTML
			var elementId = "psm_error_block_help_tooltip_ppm_error_" + linkType
			var $psm_error_block_help_tooltip_ppm_error_LinkType = $("#" + elementId );
			if ( $psm_error_block_help_tooltip_ppm_error_LinkType.length === 0 ) {
				throw Error( "No element found with id '" + elementId + "' " );
			}
			var helpTooltipHTML = $psm_error_block_help_tooltip_ppm_error_LinkType.html();

			qcChartDownloadHelp.add_DownloadClickHandlers_HelpTooltip( { 
				$chart_outer_container_for_download_jq :  $chart_outer_container_jq, 
				downloadDataCallback : downloadDataCallback,
				downloadSummaryDataCallback : downloadSummaryDataCallback,
				helpTooltipHTML : helpTooltipHTML, 
				helpTooltip_Wide : true 
			} );
			
			// Add tooltips for download links
			addToolTips( $chart_outer_container_jq );

		}

		//  TODO  FIX THIS

//		_chart_isLoaded = _IS_LOADED_YES;
	};

	//  Overridden for Specific elements like Chart Title and X and Y Axis labels
	var _PPM_Error_For_PSMs_CHART_GLOBALS = {
			_CHART_DEFAULT_FONT_SIZE : 12,  //  Default font size - using to set font size for tick marks.
			_TITLE_FONT_SIZE : 15, // In PX
			_AXIS_LABEL_FONT_SIZE : 14, // In PX
			_TICK_MARK_TEXT_FONT_SIZE : 14, // In PX
	}

	/**
	 * 
	 */
	this._add_PPM_Error_For_PSMs_Chart = function( params ) {
		var entryForLinkType = params.entryForLinkType;
		var colorAndbarColor = params.colorAndbarColor;
		var chartIndex = params.chartIndex;  //  zero based
		var $chartContainer = params.$chartContainer;

		var linkType = entryForLinkType.linkType;
		var dataForChartPerSearchIdMap_KeyProjectSearchId = entryForLinkType.dataForChartPerSearchIdMap_KeyProjectSearchId;
		
		//  chart data for Google charts
		var chartData = [];

		var chartDataHeaderEntry = [ 
			'SearchId', 
			
			//  Putting style here doesn't work since that styles the lines which are hidden
			'Max',
			'Min',
			'First Quartile',
			'Median',
			'Third Quartile',

			//  Putting style here doesn't work since that styles the intervals
			
			{id:'max', type:'number', role:'interval'},
			{id:'min', type:'number', role:'interval'},
			
			{id:'firstQuartile', type:'number', role:'interval'},
			{id:'median', type:'number', role:'interval'},
			{id:'thirdQuartile', type:'number', role:'interval'},
			
			{role: "tooltip", 'p': {'html': true} }, // tooltip for top of top box
			
			{type:'string', role: 'style' } // Color for all of interval parts/entries for current X axis entry
			
			];
		
		chartData.push( chartDataHeaderEntry );
		
		var _CHART_SIGNIFICANT_DIGITS = 5;

		var searchCountPlusOne = _project_search_ids.length + 1;
		
		var wholeChartTooltipData = [];

		
		var maxCount = 0;

		var totalCount = 0;


		_project_search_ids.forEach( function ( _project_search_ids_ArrayValue, indexForProjectSearchId, array ) {
			
			var dataForChartPerSearchIdEntry = dataForChartPerSearchIdMap_KeyProjectSearchId[ _project_search_ids_ArrayValue ];
			
			var searchId = dataForChartPerSearchIdEntry.searchId;

			var colorForSearchEntry = _colorsPerSearch[ indexForProjectSearchId ];

			
			var chartIntervalMaxString = dataForChartPerSearchIdEntry.chartIntervalMax.toPrecision( _CHART_SIGNIFICANT_DIGITS );
			var thirdQuartileString = dataForChartPerSearchIdEntry.thirdQuartile.toPrecision( _CHART_SIGNIFICANT_DIGITS );
			var medianString = dataForChartPerSearchIdEntry.median.toPrecision( _CHART_SIGNIFICANT_DIGITS );
			var firstQuartileString = dataForChartPerSearchIdEntry.firstQuartile.toPrecision( _CHART_SIGNIFICANT_DIGITS );
			var chartIntervalMinString = dataForChartPerSearchIdEntry.chartIntervalMin.toPrecision( _CHART_SIGNIFICANT_DIGITS );
			

			var mainBoxPlotTooltip =
					"Search Number: " + searchId + "\n\n" +
					"Max: " + chartIntervalMaxString + "\n" +
					"Third Quartile: " + thirdQuartileString + "\n" +
					"Median: " + medianString + "\n" +
					"First Quartile: " + firstQuartileString + "\n" +
					"Min: " + chartIntervalMinString
					;

			var wholeChartTooltipEntry = {
					searchId : searchId,
					max : chartIntervalMaxString,
					thirdQuartile : thirdQuartileString,
					median : medianString,
					firstQuartile : firstQuartileString,
					min : chartIntervalMinString,
					searchColor : colorForSearchEntry
			};
			
			wholeChartTooltipData.push( wholeChartTooltipEntry );
			
			var chartEntry = [ 
				{ v: searchId.toString(), f: searchId.toString() }, //  X axis label as well as for tooltip
				//  First list for charting for tool tips
				{ v: dataForChartPerSearchIdEntry.chartIntervalMax , f: '\nMax Value: ' + chartIntervalMaxString },
				{ v: dataForChartPerSearchIdEntry.chartIntervalMin , f: '\nMin Value: ' + chartIntervalMinString },
				{ v: dataForChartPerSearchIdEntry.firstQuartile , f: '\nFirst Quartile Value: ' + firstQuartileString },
				{ v: dataForChartPerSearchIdEntry.median , f: '\nMedian Value: ' + medianString },
				{ v: dataForChartPerSearchIdEntry.thirdQuartile , f: '\nThird Quartile Value: ' + thirdQuartileString },
				
				//  Next list for Box Chart
				dataForChartPerSearchIdEntry.chartIntervalMax,
				dataForChartPerSearchIdEntry.chartIntervalMin,
				dataForChartPerSearchIdEntry.firstQuartile,
				dataForChartPerSearchIdEntry.median,
				dataForChartPerSearchIdEntry.thirdQuartile,

				mainBoxPlotTooltip, // tooltip for top of top box
				
				'color: ' + colorForSearchEntry + ';'  // style required to make visible :  color: blue; opacity: 1;
			
				];
			
			chartData.push( chartEntry );

		}, this /* passed to function as this */ );
		

		var chartTitle = 'Distribution of Precursor error (' + linkType + ")";
		
		
		var $boxplot_chart_whole_chart_tooltip_template = $("#boxplot_chart_whole_chart_tooltip_template");
		if ( $boxplot_chart_whole_chart_tooltip_template.length === 0 ) {
			throw Error( "HTML element not found for id 'boxplot_chart_whole_chart_tooltip_template'" );
		}
		var handlebarsSource_boxplot_chart_whole_chart_tooltip_template = $boxplot_chart_whole_chart_tooltip_template.html();
		
		var _handlebarsTemplate_boxplot_chart_whole_chart_tooltip_template = Handlebars.compile( handlebarsSource_boxplot_chart_whole_chart_tooltip_template );

		var dataForTable = { perSearchDataList : wholeChartTooltipData, chartTitle : chartTitle, searchCountPlusOne : searchCountPlusOne };
		
		var htmlForTooltip = _handlebarsTemplate_boxplot_chart_whole_chart_tooltip_template( dataForTable );
		
		var qtipWholeChartPosition = {
				viewport: $(window)
		}

		if ( chartIndex == 0 ) { // zero based
			//  Left chart, attach to left edge of chart, tooltip grows to right
			qtipWholeChartPosition.my = 'bottom left';
			qtipWholeChartPosition.at = 'top left';

		} else if ( chartIndex == 1 ) {
			//  Center chart, attach to center of chart
			qtipWholeChartPosition.my = 'bottom center';
			qtipWholeChartPosition.at = 'top center';

		} else {
			//  Right chart, attach to right edge of chart, tooltip grows to left
			qtipWholeChartPosition.my = 'bottom right';
			qtipWholeChartPosition.at = 'top right';
		}

		// Add tooltip to whole chart
		$chartContainer.qtip( {
			content: {
				text: htmlForTooltip
			},
			style: {
				classes: 'qc-chart-boxplot-whole-chart-tooltip' // add this class to the tool tip
			},
			position: qtipWholeChartPosition,
			hide: {  //  Allow user to mouse into tooltip within 100 milliseconds
		          fixed: true,
		          delay: 100
		     }
		});	
		
		var barColors = [ colorAndbarColor.color ]; // must be an array

		var optionsFullsize = {
				//  Overridden for Specific elements like Chart Title and X and Y Axis labels
				fontSize: _PPM_Error_For_PSMs_CHART_GLOBALS._CHART_DEFAULT_FONT_SIZE,  //  Default font size - using to set font size for tick marks.

				title: chartTitle, // Title above chart
				titleTextStyle: {
					color : _PROXL_DEFAULT_FONT_COLOR, //  Set default font color
//					color: <string>,    // any HTML string color ('red', '#cc00cc')
//					fontName: <string>, // i.e. 'Times New Roman'
					fontSize: _PPM_Error_For_PSMs_CHART_GLOBALS._TITLE_FONT_SIZE, // 12, 18 whatever you want (don't specify px)
//					bold: <boolean>,    // true or false
//					italic: <boolean>   // true of false
				},
				legend: {position: 'none'},
				hAxis: {
					title: 'Search Number'
						, titleTextStyle: { color: 'black', fontSize: _PPM_Error_For_PSMs_CHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
//					gridlines: {color: '#fff'}
				},
				vAxis: 
				{ 	title: 'PPM Error'
					, titleTextStyle: { color: 'black', fontSize: _PPM_Error_For_PSMs_CHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
				},
				
				lineWidth: 0,  //  Hide lines
				
				interpolateNulls: true,   //  Supposed to continue a line when there is no value for x-axis point.  Doesn't appear to work when using for outliers
				
//				series: [{'color': '#D3362D'}],
				//  Series overrides colors when there are enough entries to cover the interval entries
//				series: [{'color': '#00FF00'},{'color': '#00FF00'},{'color': '#00FF00'},{'color': '#00FF00'},{'color': '#00FF00'},{'color': '#00FF00'},{'color': '#00FF00'},{'color': '#00FF00'},{'color': '#00FF00'}],
//				colors: [ '#0000FF' ],
				
				intervals: {
					barWidth: 1,
					boxWidth: 1,
					lineWidth: 2,
					style: 'boxes'
				},
				interval: {
					max: {
						style: 'bars',
						barWidth: 0.75, // length of horizontal bars, as a fraction of total width ( '1' for same width as boxes )
						fillOpacity: 1,
//						,
//						color: '#777'  //  Removed since overridden on a per search basis
					},
					min: {
						style: 'bars',
						barWidth: 0.75, // length of horizontal bars, as a fraction of total width ( '1' for same width as boxes )
						fillOpacity: 1
//						,
//						color: '#777'  //  Removed since overridden on a per search basis
					}
				}
		};        
		// create the chart
		var data = google.visualization.arrayToDataTable( chartData );
		var chartFullsize = new google.visualization.LineChart( $chartContainer[0] );

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

var qcMergedPageChart_PPM_Error_PSM = new QCMergedPageChart_PPM_Error_PSM();

export { qcMergedPageChart_PPM_Error_PSM }
