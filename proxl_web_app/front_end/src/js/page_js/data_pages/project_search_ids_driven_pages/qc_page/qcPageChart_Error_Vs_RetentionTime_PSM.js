/**
 * qcPageChart_Error_Vs_RetentionTime_PSM.js
 * 
 * Javascript for the viewQC.jsp page - Chart Error Vs Retention Time - in PSM Error section
 * 
 * page variable qcPageChart_Error_Vs_RetentionTime_PSM
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

import { webserviceCallStandardPost } from 'page_js/webservice_call_common/webserviceCallStandardPost.js';

import { qc_pages_Single_Merged_Common } from './qc_pages_Single_Merged_Common.js';

import { qcChartDownloadHelp } from './qcChart_Download_Help_HTMLBlock.js';

/**
 * Constructor 
 */
var QCPageChart_Error_Vs_RetentionTime_PSM = function() {

	//  Download data URL
	var _downloadStrutsAction = "downloadQC_Psm_PPM_Error_VS_RT_ChartData.do";


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
	this.initActual = function(params) {
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

		} catch (e) {
			reportWebErrorToServer.reportErrorObjectToServer({
				errorException : e
			});
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

		//		var $PSM_PPM_Error_CountsBlock = $("#PSM_PPM_Error_CountsBlock");
		//		$PSM_PPM_Error_CountsBlock.empty();

		//  Abort any active AJAX calls
		if (_activeAjax) {
			var objKeys = Object.keys(_activeAjax);
			objKeys.forEach(function(element, index, array) {
				var selectedLinkType = element;
				if (_activeAjax[selectedLinkType]) {
					_activeAjax[selectedLinkType].abort();
					_activeAjax[selectedLinkType] = null;
				}
			}, this /* passed to function as this */ );
		}
		_activeAjax = null;
	};


	/**
	 * If not currently loaded, load
	 */
	this.loadChartIfNeeded = function() {

		if (_chart_isLoaded === _IS_LOADED_NO) {
			this.load_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlot();
		}
	};

	var _activeAjax = null;

	/**
	 * Load the data for  PPM Error for PSMs Scatter Plot
	 */
	this.load_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlot = function() {
		var objectThis = this;

		_chart_isLoaded = _IS_LOADED_LOADING;

		var $PSM_PPM_Error_Vs_RetentionTime_CountsBlock = $("#PSM_PPM_Error_Vs_RetentionTime_CountsBlock");
		$PSM_PPM_Error_Vs_RetentionTime_CountsBlock.empty();

		var hash_json_Contents = _get_hash_json_Contents();

		var selectedLinkTypes = hash_json_Contents.linkTypes;

		if (!_anySearchesHaveScanDataYes) {

			// Show cells for selected link types
			for (var index = 0; index < selectedLinkTypes.length; index++) {
				var selectedLinkType = selectedLinkTypes[index];
				//  Add empty chart with Loading message

				var $chart_outer_container_jq = this._addChartOuterTemplate({
					$chart_group_container_table_jq : $PSM_PPM_Error_Vs_RetentionTime_CountsBlock
				});
				this._placeEmptyDummyChartForMessage({
					$chart_outer_container_jq : $chart_outer_container_jq,
					//					linkType : selectedLinkType, 
					messageWhole : _DUMMY_CHART_STATUS_WHOLE_TEXT_SCANS_NOT_UPLOADED
				});
			}

			_chart_isLoaded = _IS_LOADED_YES;

			//  Exit since no data to display

			return; //  EARLY EXIT
		}

		//  Make a copy of hash_json_Contents    (  true for deep, target object, source object, <source object 2>, ... )
		var hash_json_Contents_COPY = $.extend(true /*deep*/ , {}, hash_json_Contents);

		// Add cells for selected link types
		selectedLinkTypes.forEach(function(currentArrayValue, index, array) {
			var selectedLinkType = currentArrayValue;

			//  Add empty chart with Loading message
			var $chart_outer_container_jq = this._addChartOuterTemplate({
				linkType : selectedLinkType,
				$chart_group_container_table_jq : $PSM_PPM_Error_Vs_RetentionTime_CountsBlock
			});

			//  Add empty chart with Loading message
			this._placeEmptyDummyChartForMessage({
				$chart_outer_container_jq : $chart_outer_container_jq,
				//				linkType : selectedLinkType, 
				messagePrefix : _DUMMY_CHART_STATUS_TEXT_PREFIX_LOADING,
				messageSuffix : _DUMMY_CHART_STATUS_TEXT_SUFFIX_LOADING
			});

			//  For each selected link type, 
			//    set it as selected link type in hash_json_Contents_COPY,
			//      and create a chart for that

			hash_json_Contents_COPY.linkTypes = [ selectedLinkType ];

			this.load_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlotForLinkType({
				selectedLinkType : selectedLinkType,
				hash_json_Contents_COPY : hash_json_Contents_COPY,
				$chart_outer_container_jq : $chart_outer_container_jq
			});

		}, this /* passed to function as this */ );

	};

	var _activeAjax = null;

	/**
	 * Load the data for  PPM Error Vs Retention Time for PSMs Scatter Plot Specific Link Type
	 */
	this.load_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlotForLinkType = function(params) {
		var objectThis = this;

		var selectedLinkType = params.selectedLinkType;
		var hash_json_Contents_COPY = params.hash_json_Contents_COPY;
		var $chart_outer_container_jq = params.$chart_outer_container_jq;

		if (_activeAjax &&
			_activeAjax[selectedLinkType]) {
			_activeAjax[selectedLinkType].abort();
			_activeAjax[selectedLinkType] = null;
		}
		if (!_activeAjax) {
			_activeAjax = {};
		}

		const ajaxRequestData = { projectSearchIds : _project_search_ids, qcPageQueryJSONRoot : hash_json_Contents_COPY };

		const url = "services/qc/dataPage/ppmErrorVsRetentionTime";

		const webserviceCallStandardPostResult = webserviceCallStandardPost({ dataToSend : ajaxRequestData, url }); //  External Function

		const promise_webserviceCallStandardPost = webserviceCallStandardPostResult.promise; 
		_activeAjax[selectedLinkType] = webserviceCallStandardPostResult.api;

		promise_webserviceCallStandardPost.catch( ( ) => { 
			if (_activeAjax) {
				_activeAjax[selectedLinkType] = null;
			}
		 } );

		promise_webserviceCallStandardPost.then( ({ responseData }) => {
			try {
				if (_activeAjax) {
					_activeAjax[selectedLinkType] = null;
				}
				var responseParams = {
					ajaxResponseData : responseData,
					selectedLinkType : selectedLinkType,
					$chart_outer_container_jq : $chart_outer_container_jq
				};
				objectThis.load_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlotResponse(responseParams);
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
	};

	/**
	 * Process AJAX Response
	 */
	this.load_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlotResponse = function(params) {
		var objectThis = this;

		var ajaxResponseData = params.ajaxResponseData;
		var selectedLinkType = params.selectedLinkType;
		var $chart_outer_container_jq = params.$chart_outer_container_jq;

		var dataForChartPerLinkTypeList = ajaxResponseData.dataForChartPerLinkTypeList;

		if (dataForChartPerLinkTypeList.length !== 1) {
			throw Error("dataForChartPerLinkTypeList.length !== 1, is = " + dataForChartPerLinkTypeList.length);
		}

		var entryForLinkType = dataForChartPerLinkTypeList[0];

		var linkType = entryForLinkType.linkType;
		var retentionTimeBuckets = entryForLinkType.retentionTimeBuckets;

		$chart_outer_container_jq.empty();

		if (retentionTimeBuckets === null || retentionTimeBuckets.length === 0) {
			//  No data for this link type

			//  Add empty chart with No Data message
			this._placeEmptyDummyChartForMessage({
				$chart_outer_container_jq : $chart_outer_container_jq,
				//				linkType : selectedLinkType, 
				messagePrefix : _DUMMY_CHART_STATUS_TEXT_PREFIX_NO_DATA,
				messageSuffix : _DUMMY_CHART_STATUS_TEXT_SUFFIX_NO_DATA
			});

			return; //  EARLY RETURN
		}

		var $chart_container_jq = this._addChartInnerTemplate({
			$chart_outer_container_jq : $chart_outer_container_jq
		});

		var colorAndbarColor = this.getColorAndBarColorFromLinkType(linkType);

		this._add_PPM_Error_Vs_RetentionTime_For_PSMs_Histogram_Chart({
			entryForLinkType : entryForLinkType,
			colorAndbarColor : colorAndbarColor,
			$chartContainer : $chart_container_jq
		});

		//  Download Data Setup

		var hash_json_Contents = _get_hash_json_Contents();
		//  Set link types to chart link type
		hash_json_Contents.linkTypes = [ linkType ];

		var downloadDataCallback = function(params) {
			//			var clickedThis = params.clickedThis;

			//  Download the data for params
			const dataToSend = { projectSearchIds : _project_search_ids, qcPageQueryJSONRoot : hash_json_Contents };
			qc_pages_Single_Merged_Common.submitDownloadForParams( { downloadStrutsAction : _downloadStrutsAction, dataToSend } );
		};

		//  Get Help tooltip HTML
		var elementId = "psm_error_block_help_tooltip_error_vs_retention_time_" + linkType
		var $psm_error_block_help_tooltip_error_vs_retention_time_LinkType = $("#" + elementId);
		if ($psm_error_block_help_tooltip_error_vs_retention_time_LinkType.length === 0) {
			throw Error("No element found with id '" + elementId + "' ");
		}
		var helpTooltipHTML = $psm_error_block_help_tooltip_error_vs_retention_time_LinkType.html();

		qcChartDownloadHelp.add_DownloadClickHandlers_HelpTooltip({
			$chart_outer_container_for_download_jq : $chart_outer_container_jq,
			downloadDataCallback : downloadDataCallback,
			helpTooltipHTML : helpTooltipHTML,
			helpTooltip_Wide : true
		});

		// Add tooltips for download links
		addToolTips($chart_outer_container_jq);

		//  TODO  FIX THIS

	//		_chart_isLoaded = _IS_LOADED_YES;
	};

	//  Overridden for Specific elements like Chart Title and X and Y Axis labels
	var _PPM_Error_Vs_RetentionTime_For_PSMs_CHART_GLOBALS = {
		_CHART_DEFAULT_FONT_SIZE : 12, //  Default font size - using to set font size for tick marks.
		_TITLE_FONT_SIZE : 15, // In PX
		_AXIS_LABEL_FONT_SIZE : 14, // In PX
		_TICK_MARK_TEXT_FONT_SIZE : 14, // In PX
	}

	/**
	 * 
	 */
	this._add_PPM_Error_Vs_RetentionTime_For_PSMs_Histogram_Chart = function(params) {
		var entryForLinkType = params.entryForLinkType;
		var colorAndbarColor = params.colorAndbarColor;
		var $chartContainer = params.$chartContainer;

		var linkType = entryForLinkType.linkType;

		var retentionTimeBuckets = entryForLinkType.retentionTimeBuckets;

		//  All PPM Error Bin Start values, sorted smallest to largest
		var ppmErrorBinStartDistinctSorted = entryForLinkType.ppmErrorBinStartDistinctSorted;

		var numScans = entryForLinkType.numScans;

		var ppmErrorBinMin = entryForLinkType.ppmErrorBinMin;
		var ppmErrorBinMax = entryForLinkType.ppmErrorBinMax;
		var ppmErrorPossibleMax = entryForLinkType.ppmErrorPossibleMax;
		var retentionTimeBinMin = entryForLinkType.retentionTimeBinMin;
		var retentionTimeBinMax = entryForLinkType.retentionTimeBinMax;
		var retentionTimePossibleMax = entryForLinkType.retentionTimePossibleMax;

		//  chart data for Google charts
		var chartData = [];

		var OPACITY_DEFAULT = "0.5";

		//  opacity: null  Use Default

		// Ranges:  1-3, 4-10, 11-17, 18+

		//  Only last element can have and must have "min" property 
		var SERIES_SETTINGS = [
			{
				max : 3,
				color : _PROXL_COLOR_SITE_BLUE,
				pointSize : 4,
				opacity : null
			}
			, {
				max : 10,
				color : _PROXL_COLOR_SITE_GREEN,
				pointSize : 5,
				opacity : null
			}
			, {
				max : 17,
				color : '#ddbf17',
				pointSize : 6,
				opacity : null
			}
			, {
				min : 18,
				color : _PROXL_COLOR_SITE_RED,
				pointSize : 7,
				opacity : null
			}
		];

		var addHeaderEntry = function(label) {
			chartDataHeaderEntry.push({
				label : label,
				type : 'number'
			});
			chartDataHeaderEntry.push({
				type : 'string',
				role : 'style'
			});
		//			, {type:'string', role: "tooltip", 'p': {'html': true} }
		}

		var chartDataHeaderEntry = [ 'Retention Time' ];

		var prevMax = 0;
		for (var seriesSettingsIndex = 0; seriesSettingsIndex < SERIES_SETTINGS.length; seriesSettingsIndex++) {
			var entry = SERIES_SETTINGS[seriesSettingsIndex];
			if (entry.max !== undefined && entry.max !== null) {
				addHeaderEntry((prevMax + 1) + "-" + entry.max);
			} else {
				// No max so must contain min
				if (entry.min === undefined || entry.min === null) {
					throw Error("SERIES_SETTINGS element not contain min or max property");
				}
				addHeaderEntry(entry.min + "+");
			}
			prevMax = entry.max;
		}

		chartData.push(chartDataHeaderEntry);

		var dataPointCount = 0;

		var maxCount = 0;

		var totalCount = 0;

		var maxRetenionTimeError = null;
		var minRetenionTimeError = null;

		var maxPPMError = null;
		var minPPMError = null;

		for (var retentionTimeBucketsIndex = 0; retentionTimeBucketsIndex < retentionTimeBuckets.length; retentionTimeBucketsIndex++) {
			var retentionTimeBucket = retentionTimeBuckets[retentionTimeBucketsIndex];
			var chartBuckets = retentionTimeBucket.chartBuckets;

			for (var bucketIndex = 0; bucketIndex < chartBuckets.length; bucketIndex++) {
				var bucket = chartBuckets[bucketIndex];

				var retentionTimeStart = bucket.retentionTimeStart;
				var retentionTimeEnd = bucket.retentionTimeEnd;
				var ppmErrorStart = bucket.ppmErrorStart;
				var ppmErrorEnd = bucket.ppmErrorEnd;
				var count = bucket.count;

				dataPointCount++;

				var retentionTimeCenter = retentionTimeStart + ((retentionTimeEnd - retentionTimeStart) / 2);
				var ppmErrorCenter = ppmErrorStart + ((ppmErrorEnd - ppmErrorStart) / 2);

				//				var tooltipText = "<div style='padding: 4px;'>Count: " + bucket.count +
				//				"<br>PPM Error approximately " + 
				//				bucket.binStart.toPrecision( _PPM_ERROR_DISPLAY_MAX_SIGNIFICANT_DIGITS ) + 
				//				" to " + bucket.binEnd.toPrecision( _PPM_ERROR_DISPLAY_MAX_SIGNIFICANT_DIGITS ) +
				//				"</div>";

				var entryAnnotationText = bucket.count;

				var chartEntrySpecificSeries = [
					ppmErrorCenter // Y axis
					, '' // style will be set below
				];

				var fillStyleOptionEnd = ";";
				var fillColorStyleOption = 'fill-color: ';

				var fillOpacityStyleOption = 'fill-opacity: ';

				//  Style of point
				//				colorAndbarColor.barColor,
				//  Style appears to be ignored
				//				' color : orange; ' +
				//				'opacity : 0; '+
				//				'stroke-width : 0; ' 
				//				+
				//				'stroke-color : red; ' +
				//				'stroke-opacity : 1; ' +
				//				'fill-color : red; ' +
				//				'fill-opacity : .2'
				//				,
				//				//  Tool Tip
				//				tooltipText
				////				,
				////				entryAnnotationText

				var chartEntry = [ retentionTimeCenter ]; // X axis

				//  Copy chartEntrySpecificSeries to chartEntry
				var copy_chartEntrySpecificSeriesTo_chartEntry = function() {
					for (var copy_chartEntrySpecificSeriesTo_chartEntry_index = 0; copy_chartEntrySpecificSeriesTo_chartEntry_index < chartEntrySpecificSeries.length; copy_chartEntrySpecificSeriesTo_chartEntry_index++) {
						var chartEntrySpecificSeriesEntry = chartEntrySpecificSeries[copy_chartEntrySpecificSeriesTo_chartEntry_index];
						chartEntry.push(chartEntrySpecificSeriesEntry);
					}
				}

				//  Fill in for other series
				var addNullEntriesToChartEntryForSeriesCount = function(seriesCount) {
					var counterMax = seriesCount * chartEntrySpecificSeries.length;
					for (var counter = 0; counter < counterMax; counter++) {
						chartEntry.push(null);
					}
				}

				for (var seriesSettingsIndex = 0; seriesSettingsIndex < SERIES_SETTINGS.length; seriesSettingsIndex++) {
					var entry = SERIES_SETTINGS[seriesSettingsIndex];
					if (entry.max !== undefined && entry.max !== null) {
						if (count <= entry.max) {
							addNullEntriesToChartEntryForSeriesCount(seriesSettingsIndex);
							var pointColor = entry.color;
							var opacityString = null;
							if (entry.opacity !== undefined && entry.opacity !== null) {
								//								opacityString = ;
								throw Error("opacityString not being set");
							} else {
								opacityString = OPACITY_DEFAULT;
							}

							//  Add Style string
							chartEntrySpecificSeries[1] = fillColorStyleOption + pointColor + fillStyleOptionEnd +
								fillOpacityStyleOption + opacityString + fillStyleOptionEnd;

							copy_chartEntrySpecificSeriesTo_chartEntry(chartEntry, chartEntrySpecificSeries);
							addNullEntriesToChartEntryForSeriesCount(SERIES_SETTINGS.length - (seriesSettingsIndex + 1));
							break;
						}
					} else {
						// No max so must contain min
						if (entry.min === undefined || entry.min === null) {
							throw Error("SERIES_SETTINGS element not contain min or max property");
						}
						if (count >= entry.min) {
							addNullEntriesToChartEntryForSeriesCount(seriesSettingsIndex);
							var pointColor = entry.color;
							var opacityString = null;
							if (entry.opacity !== undefined && entry.opacity !== null) {
								opacityString = opacityString;
							} else {
								opacityString = OPACITY_DEFAULT;
							}

							//  Add Style string
							chartEntrySpecificSeries[1] = fillColorStyleOption + pointColor + fillStyleOptionEnd +
								fillOpacityStyleOption + opacityString + fillStyleOptionEnd;

							copy_chartEntrySpecificSeriesTo_chartEntry(chartEntry, chartEntrySpecificSeries);
							break;
						}
					}
				}

				chartData.push(chartEntry);

				//  Update Max Min and Total
				totalCount += bucket.count;
				if (bucket.count > maxCount) {
					maxCount = bucket.count;
				}
				if (maxPPMError === null || ppmErrorEnd > maxPPMError) {
					maxPPMError = ppmErrorEnd;
				}
				if (minPPMError === null || ppmErrorStart < minPPMError) {
					minPPMError = ppmErrorStart;
				}
				if (maxRetenionTimeError === null || retentionTimeEnd > maxRetenionTimeError) {
					maxRetenionTimeError = retentionTimeEnd;
				}
				if (minRetenionTimeError === null || retentionTimeStart < minRetenionTimeError) {
					minRetenionTimeError = retentionTimeStart;
				}
			}
		}

		var vAxisTicks = this._get_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlot_Chart_Vertical_TickMarks({
			maxValue : maxPPMError,
			minValue : minPPMError,
			dataPointCount : dataPointCount
		});

		var hAxisTicks = this._get_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlot_Chart_Horizontal_TickMarks({
			maxValue : maxRetenionTimeError,
			minValue : minRetenionTimeError
		});

		//  Build Series Config and Color Arrays
		var seriesConfig = [];
		var barColors = []; // must be an array

		for (var seriesSettingsIndex = 0; seriesSettingsIndex < SERIES_SETTINGS.length; seriesSettingsIndex++) {
			var entry = SERIES_SETTINGS[seriesSettingsIndex];
			var seriesConfigEntry = {
				color : entry.color,
				pointSize : entry.pointSize
			};
			seriesConfig.push(seriesConfigEntry);
			barColors.push(entry.color);
		}

		var chartTitle = 'PPM Error vs/ Retention Time (' + linkType + ")";
		var optionsFullsize = {
			//  Overridden for Specific elements like Chart Title and X and Y Axis labels
			fontSize : _PPM_Error_Vs_RetentionTime_For_PSMs_CHART_GLOBALS._CHART_DEFAULT_FONT_SIZE, //  Default font size - using to set font size for tick marks.
			series : seriesConfig,
			//				pointSize: 20,
			title : chartTitle, // Title above chart
			titleTextStyle : {
				color : _PROXL_DEFAULT_FONT_COLOR, //  Set default font color
				//					color: <string>,    // any HTML string color ('red', '#cc00cc')
				//					fontName: <string>, // i.e. 'Times New Roman'
				fontSize : _PPM_Error_Vs_RetentionTime_For_PSMs_CHART_GLOBALS._TITLE_FONT_SIZE, // 12, 18 whatever you want (don't specify px)
			//					bold: <boolean>,    // true or false
			//					italic: <boolean>   // true of false
			},
			//  X axis label below chart
			hAxis : {
				title : 'Retention Time (minutes)',
				titleTextStyle : {
					color : 'black',
					fontSize : _PPM_Error_Vs_RetentionTime_For_PSMs_CHART_GLOBALS._AXIS_LABEL_FONT_SIZE
				},
				gridlines : {
					color : 'none' //  No vertical grid lines on the horzontal axis
				}, //				,baselineColor: 'none' // Hide 'Zero' line //				,baselineColor: '#CDCDCD' // Make 'Zero' line really light
				ticks : hAxisTicks, //				,ticks: [-100,0,100]
				maxValue : maxRetenionTimeError,
				minValue : minRetenionTimeError
			},
			//  Y axis label left of chart
			vAxis : {
				title : 'PPM Error',
				titleTextStyle : {
					color : 'black',
					fontSize : _PPM_Error_Vs_RetentionTime_For_PSMs_CHART_GLOBALS._AXIS_LABEL_FONT_SIZE
				}, //				,baseline: 0     // always start at zero
				ticks : vAxisTicks,
				maxValue : maxPPMError,
				minValue : minPPMError
			},
			//				legend: { position: 'none' }, //  position: 'none':  Don't show legend of bar colors in upper right corner
			//  Size picked up from containing HTML element
			//				width : 500, 
			//				height : 300,   // width and height of chart, otherwise controlled by enclosing div
			//				bar: { groupWidth: '100%' },  // set bar width large to eliminate space between bars
			colors : barColors,
			tooltip : {
				isHtml : true
			}
		};
		// create the chart
		var data = google.visualization.arrayToDataTable(chartData);

		var chartFullsize = new google.visualization.ScatterChart($chartContainer[0]);

		//  Register for chart errors
		var errorDrawingChart = function(err) {
			//  Properties of err object
			//			id [Required] - The ID of the DOM element containing the chart, or an error message displayed instead of the chart if it cannot be rendered.
			//			message [Required] - A short message string describing the error.
			//			detailedMessage [Optional] - A detailed explanation of the error.
			//			options [Optional]- An object containing custom parameters appropriate to this error and chart type.

			//  This thrown string is displayed on the chart on the page as well as logged to browser console and logged to the server 
			throw Error("Chart Error: " + err.message + " :: detailed error msg: " + err.detailedMessage);
		}
		google.visualization.events.addListener(chartFullsize, 'error', errorDrawingChart);

		chartFullsize.draw(data, optionsFullsize);

		//  Using Material chart.  added to viewQC.jsp:  <c:set var="googleChartPackagesLoadAdditions">,"scatter"</c:set>
		//		var chartFullsize = new google.charts.Scatter( $chartContainer[0] );
		//		chartFullsize.draw( data, google.charts.Scatter.convertOptions( optionsFullsize ) );

	};

	/**
	 * for vertical PPM Error axis
	 */
	this._get_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlot_Chart_Vertical_TickMarks = function(params) {
		var maxValue = params.maxValue;
		var minValue = params.minValue;
		var dataPointCount = params.dataPointCount; // number of data points in plot

		//  Compute max and min tick marks, to next higher and lower multiple of 1
		var maxValueCeil = Math.ceil(maxValue);
		var minValueFloor = Math.floor(minValue);
		var maxCeilMinusMinFloor = maxValueCeil - minValueFloor;

		var tickMarkIncrementFromZero = maxCeilMinusMinFloor / 5;

		var tickMarks = [];

		if (minValue < 0 && maxValue > 0) {

			if ((maxValue - minValue) >= 10) {
				var tickMarkValueForTickMarkCounter = function(tickCounter) {
					var tickMark = tickCounter * tickMarkIncrementFromZero;
					return tickMark;
				};

				//  Add tick marks for < 0 
				for (var tickCounter = 1; (-( tickMarkValueForTickMarkCounter(tickCounter) )) > minValueFloor; tickCounter++) {
					var tickMark = (-( tickMarkValueForTickMarkCounter(tickCounter) ));
					tickMarks.push(tickMark);
				}
				tickMarks.push(0);
				//  Add tick marks for > 0 
				for (var tickCounter = 1; ( ( tickMarkValueForTickMarkCounter(tickCounter) ) ) < maxValueCeil; tickCounter++) {
					var tickMark = ( ( tickMarkValueForTickMarkCounter(tickCounter) ) );
					tickMarks.push(tickMark);
				}
			} else {
				var minValueFloorPt5 = (Math.floor((minValue * 2)) / 2);
				var maxValueCeilPt5 = (Math.ceil((maxValue * 2)) / 2);
				var maxValueCeilPt5MinusminValueFloorPt5 = maxValueCeilPt5 - minValueFloorPt5;
				var tickMarkIncrementForPt5 = maxValueCeilPt5MinusminValueFloorPt5 / 5;

				var tickMarkValueForTickMarkCounterLessThanTenGE1 = function(tickCounter) {
					var tickMark = (tickCounter * tickMarkIncrementForPt5);
					return tickMark;
				};

				//  Add tick marks for < 0 
				for (var tickCounter = 1; (-( tickMarkValueForTickMarkCounterLessThanTenGE1(tickCounter) )) > minValue; tickCounter++) {
					var tickMark = (-( tickMarkValueForTickMarkCounterLessThanTenGE1(tickCounter) ));
					tickMarks.push(tickMark);
				}
				tickMarks.push(0);
				//  Add tick marks for > 0 
				for (var tickCounter = 1; ( ( tickMarkValueForTickMarkCounterLessThanTenGE1(tickCounter) ) ) < maxValue; tickCounter++) {
					var tickMark = ( ( tickMarkValueForTickMarkCounterLessThanTenGE1(tickCounter) ) );
					tickMarks.push(tickMark);
				}
			}
		} else {

			if (dataPointCount === 1) {
				tickMarks.push(minValue);
				tickMarks.push((minValue + (maxValue - minValue) / 2));
				tickMarks.push(maxValue);

			} else if ((maxValue - minValue) >= 10) {
				var tickMarkValueForTickMarkCounterNoZero = function(tickCounter) {
					return minValueFloor + (tickCounter * tickMarkIncrementFromZero);
				};
				for (var tickCounter = 1; ( ( tickMarkValueForTickMarkCounterNoZero(tickCounter) ) ) < maxValue; tickCounter++) {
					var tickMark = ( ( tickMarkValueForTickMarkCounterNoZero(tickCounter) ) );
					tickMarks.push(tickMark);
				}
			} else if ((maxValue - minValue) >= 1) {
				var minValueFloorPt5 = (Math.floor((minValue * 2)) / 2);
				var maxValueCeilPt5 = (Math.ceil((maxValue * 2)) / 2);
				var maxValueCeilPt5MinusminValueFloorPt5 = maxValueCeilPt5 - minValueFloorPt5;
				var tickMarkIncrementForPt5 = maxValueCeilPt5MinusminValueFloorPt5 / 5;

				var tickMarkValueForTickMarkCounterLessThanTenGE1 = function(tickCounter) {
					return minValueFloorPt5 + (tickCounter * tickMarkIncrementForPt5);
				};
				for (var tickCounter = 1; ( ( tickMarkValueForTickMarkCounterLessThanTenGE1(tickCounter) ) ) < maxValue; tickCounter++) {
					var tickMark = ( ( tickMarkValueForTickMarkCounterLessThanTenGE1(tickCounter) ) );
					tickMarks.push(tickMark);
				}
			} else {
				if ((maxValue - minValue) > .2) {
					var minValueFloorPt1 = (Math.floor((minValue * 10)) / 10);
					var maxValueCeilPt1 = (Math.ceil((maxValue * 10)) / 10);
					var maxValueCeilPt1MinusminValueFloorPt1 = maxValueCeilPt1 - minValueFloorPt1;
					var tickMarkIncrementForPt1 = maxValueCeilPt1MinusminValueFloorPt1 / 5;

					var tickMarkValueForTickMarkCounterLessThanOneGtPt2 = function(tickCounter) {
						var tickMarkVal = minValueFloorPt1 + (tickCounter * tickMarkIncrementForPt1);
						return tickMarkVal;
					};
					for (var tickCounter = 1; ( ( tickMarkValueForTickMarkCounterLessThanOneGtPt2(tickCounter) ) ) < maxValue; tickCounter++) {
						var tickMark = ( ( tickMarkValueForTickMarkCounterLessThanOneGtPt2(tickCounter) ) );
						tickMarks.push(tickMark);
					}
				} else {
					tickMarks.push(minValue);
					tickMarks.push((minValue + (maxValue - minValue) / 2));
					tickMarks.push(maxValue);
				}
			}
		}


		return tickMarks;
	};

	/**
	 * for horizontal Retention Time axis
	 */
	this._get_PPM_Error_Vs_RetentionTime_For_PSMs_ScatterPlot_Chart_Horizontal_TickMarks = function(params) {
		var maxValue = params.maxValue;
		var minValue = params.minValue;

		var SCALE_FACTOR = 10;

		//  Compute max and min tick marks, to next higher and lower multiple of SCALE_FACTOR
		var maxTickMark = Math.ceil(maxValue / SCALE_FACTOR) * SCALE_FACTOR;
		var minTickMark = Math.floor(minValue / SCALE_FACTOR) * SCALE_FACTOR;

		var maxMinusMin_TickMark = maxTickMark - minTickMark;

		//  Create tick marks at min, 25%, 50%, 75%, max
		var tickMarks = [
			minTickMark,
			minTickMark + (maxMinusMin_TickMark * .25),
			minTickMark + (maxMinusMin_TickMark * .5),
			minTickMark + (maxMinusMin_TickMark * .75),
			maxTickMark
		];
		return tickMarks;
	};


};

/**
 * page variable 
 */

var qcPageChart_Error_Vs_RetentionTime_PSM = new QCPageChart_Error_Vs_RetentionTime_PSM();

export { qcPageChart_Error_Vs_RetentionTime_PSM }
