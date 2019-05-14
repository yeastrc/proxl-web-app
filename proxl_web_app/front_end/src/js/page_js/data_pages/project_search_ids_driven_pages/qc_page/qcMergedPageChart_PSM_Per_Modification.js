/**
 * qcMergedPageChart_PSM_Per_Modification.js
 * 
 * Javascript for the viewQCMerged.jsp page - PSM  Count Per Modification type for search
 * 
 * page variable qcMergedPageChart_PSM_Per_Modification
 * 
 * Merged QC Page
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
var QCMergedPageChart_PSM_Per_Modification = function() {

	//  Download data URL
	var _downloadStrutsAction = "downloadQC_PsmModificationChartData.do";

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
	 * Add Click and onChange handlers   - called from this.initActual(...)
	 */
	this.addClickAndOnChangeHandlers = function() {
		var objectThis = this;

	};

	/**
	 * Called when the selection criteria at the top of the page has changed and user has clicked "Update from Database".
	 *   The current chart(s) and it's data are no longer applicable and can be deleted and removed.
	 * 
	 * Clear data 
	 */
	this.clearChart = function() {

		_loadChartIfNeeded_PreviouslyCalled = false;
		_chartsData = undefined;;
		_chartsDisplayed = false;

		var $PSM_Per_Modification_Counts_Block = $("#PSM_Per_Modification_Counts_Block");
		$PSM_Per_Modification_Counts_Block.empty();

		if ( _activeAjax ) {
			_activeAjax.abort();
			_activeAjax = null;
		}
	};

	/**
	 * Called when the section is hidden
	 * 
	 * Clear data for Summary_Statistics_Counts
	 */
	this.sectionHidden = function() {

		_sectionVisible = false;
	}

	let _sectionVisible = false; //  When true, the section is visible

	let _loadChartIfNeeded_PreviouslyCalled = false; // Also false if this.clearChart() called

	//  Single variable since single AJAX call
	let _chartsData = undefined;

	//  Single variable since all created at once
	let _chartsDisplayed = false; // Also false if this.clearChart() called


	/**
	 * This Method should be called 'createChartIfNeeded'.  Also when called, the section is shown.
	 * 
	 * Called when the section on the page that this chart is in is opened (or on page load for section open on page load)
	 * 
	 */
	this.loadChartIfNeeded = function() {

		_sectionVisible = true;

		if ( ! _chartsDisplayed ) {
			if ( ! _chartsData ) {
				if ( ! _loadChartIfNeeded_PreviouslyCalled ) {
					// loadChartIfNeeded not previously called so start at the top

					_loadChartIfNeeded_PreviouslyCalled = true;

					const promise_getData_FromServer = this.getData_FromServer();

					promise_getData_FromServer.catch( () => {} );
					promise_getData_FromServer.then( ({ responseData }) => {
						_chartsData = responseData;
						if ( ! _sectionVisible ) {
							//  Section no longer visible so skip creating the charts
							return; // EARLY EXIT
						}
						this.createCharts();
					});
				} else {
					//  Loading is already in progress so exit
					return; // EARLY RETURN
				}
			} else {
				//  Have charts data but not displayed so display the chart data
				this.createCharts();
			}
		} 
	};

	var _activeAjax = null;

	/**
	 * Load the data from the server
	 */
	this.getData_FromServer = function() {
		var objectThis = this;

		var $PSM_Per_Modification_Counts_Block = $("#PSM_Per_Modification_Counts_Block");
		$PSM_Per_Modification_Counts_Block.empty();
		
		var hash_json_Contents = _get_hash_json_Contents();

		var selectedLinkTypes = hash_json_Contents.linkTypes;

		// Show cells for selected link types
		selectedLinkTypes.forEach( function ( currentArrayValue, index, array ) {
			var selectedLinkType = currentArrayValue;

			//  Add empty chart with Loading message
			var $chart_outer_container_jq =
				this._addChartOuterTemplate( { linkType : selectedLinkType, $chart_group_container_table_jq : $PSM_Per_Modification_Counts_Block } );

			//  Add empty chart with Loading message
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

		return new Promise( (resolve, reject) => {
			try {
				const ajaxRequestData = { projectSearchIds : _project_search_ids, qcPageQueryJSONRoot : hash_json_Contents };

				const url = "services/qc/dataPage/psmCountsPerModification_Merged";

				const webserviceCallStandardPostResult = webserviceCallStandardPost({ dataToSend : ajaxRequestData, url }); //  External Function

				const promise_webserviceCallStandardPost = webserviceCallStandardPostResult.promise; 
				_activeAjax = webserviceCallStandardPostResult.api;

				promise_webserviceCallStandardPost.catch( ( ) => {
					_activeAjax = null;
					reject();
			   } );

			   promise_webserviceCallStandardPost.then( ({ responseData }) => {
				   try {
					   _activeAjax = null;
					   resolve({ responseData });
			   
				   } catch( e ) {
					   reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					   throw e;
				   }
			   });
		   } catch( e ) {
			   reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			   throw e;
		   }
	   });
	};

	/**
	 * Create the charts
	 */
	this.createCharts = function() {

		var qc_PSM_CountsPerModification_Merged_Results = _chartsData.qc_PSM_CountsPerModification_Merged_Results;
		var resultsPerLinkTypeList = qc_PSM_CountsPerModification_Merged_Results.resultsPerLinkTypeList;
		var searchIds = qc_PSM_CountsPerModification_Merged_Results.searchIds;

		var $PSM_Per_Modification_Counts_Block = $("#PSM_Per_Modification_Counts_Block");
		if ( $PSM_Per_Modification_Counts_Block.length === 0 ) {
			throw Error( "unable to find HTML element with id 'PSM_Per_Modification_Counts_Block'" );
		}

		$PSM_Per_Modification_Counts_Block.empty();

		resultsPerLinkTypeList.forEach( function ( currentArrayValue, index, array ) {
			var entryForLinkType = currentArrayValue;
			var linkType = entryForLinkType.linkType;
			var foundDataForLinkType = entryForLinkType.foundDataForLinkType;
			var resultsPerModMassList = entryForLinkType.resultsPerModMassList;

			if ( ! foundDataForLinkType ) {

				//  Add empty chart with No Data message
				var $chart_outer_container_jq = this._addChartOuterTemplate( { $chart_group_container_table_jq : $PSM_Per_Modification_Counts_Block } );
				this._placeEmptyDummyChartForMessage( { 
					$chart_outer_container_jq : $chart_outer_container_jq, 
					linkType : linkType, 
					messagePrefix:  _DUMMY_CHART_STATUS_TEXT_PREFIX_NO_DATA,
					messageSuffix:  _DUMMY_CHART_STATUS_TEXT_SUFFIX_NO_DATA
				} );

			} else {
				var $chart_outer_container_jq = this._addChartOuterTemplate( { $chart_group_container_table_jq : $PSM_Per_Modification_Counts_Block } );
				var $chart_container_jq = this._addChartInnerTemplate( { $chart_outer_container_jq : $chart_outer_container_jq } );

				var linkType = entryForLinkType.linkType;
				var colorAndbarColor = this.getColorAndBarColorFromLinkType( linkType );

				this._addSingle_PSM_Count_Per_Modification_Chart( { entryForLinkType: entryForLinkType, searchIds : searchIds, colorAndbarColor : colorAndbarColor, $chartContainer : $chart_container_jq } );

				//  Download Data
				
				var hash_json_Contents = _get_hash_json_Contents();
				//  Set link types to chart link type
				hash_json_Contents.linkTypes = [ linkType ];
								
				var downloadDataCallback = function( params ) {
//					var clickedThis = params.clickedThis;
					//  Download the data for params
					const dataToSend = { projectSearchIds : _project_search_ids, qcPageQueryJSONRoot : hash_json_Contents };
					qc_pages_Single_Merged_Common.submitDownloadForParams( { downloadStrutsAction : _downloadStrutsAction, dataToSend } );
				};
				
				//  Get Help tooltip HTML
				var elementId = "modification_stats_block_help_tooltip_" + linkType
				var $modification_stats_block_help_tooltip_LinkType = $("#" + elementId );
				if ( $modification_stats_block_help_tooltip_LinkType.length === 0 ) {
					throw Error( "No element found with id '" + elementId + "' " );
				}
				var helpTooltipHTML = $modification_stats_block_help_tooltip_LinkType.html();

				qcChartDownloadHelp.add_DownloadClickHandlers_HelpTooltip( { 
					$chart_outer_container_for_download_jq :  $chart_outer_container_jq,
					downloadDataCallback : downloadDataCallback,
					helpTooltipHTML : helpTooltipHTML, 
					helpTooltip_Wide : true
				} );
				
				// Add tooltips for download links
				addToolTips( $chart_outer_container_jq );
			}
		}, this /* passed to function as this */ );

		_chartsDisplayed = true;

	};

	/**
	 * Overridden for Specific elements like Chart Title and X and Y Axis labels
	 */
	var _PSM_COUNT_PER_MODIFICATION_CHART_GLOBALS = {
			_CHART_DEFAULT_FONT_SIZE : 12,  //  Default font size - using to set font size for tick marks.
			_TITLE_FONT_SIZE : 15, // In PX
			_AXIS_LABEL_FONT_SIZE : 14, // In PX
			_TICK_MARK_TEXT_FONT_SIZE : 14 // In PX

			, _ENTRY_ANNOTATION_TEXT_SIGNIFICANT_DIGITS : 3
	};

	/**
	 * Add single PSM_Count_Per_Modification Chart
	 */
	this._addSingle_PSM_Count_Per_Modification_Chart = function( params ) {
		var entryForLinkType = params.entryForLinkType;
		var searchIds = params.searchIds;
//		var colorAndbarColor = params.colorAndbarColor;
		var $chartContainer = params.$chartContainer;


		var linkType = entryForLinkType.linkType;
		var resultsPerModMassList = entryForLinkType.resultsPerModMassList;

		var fractionMax = 0;

		//  chart data for Google charts
		var chartData = [];

		var chartDataHeaderEntry = [ 'Modification' ];
		
		searchIds.forEach( function ( currentArrayValue, index, array ) {
			chartDataHeaderEntry.push( currentArrayValue.toString() );
			chartDataHeaderEntry.push( { role: 'style' } );  // Style of the bar 
			chartDataHeaderEntry.push( {role: "tooltip", 'p': {'html': true} } );
//			chartDataHeaderEntry.push(  {type: 'string', role: 'annotation'} );
		}, this /* passed to function as this */ );
			
		chartData.push( chartDataHeaderEntry );

		

		var maxYvalue = 0;

		resultsPerModMassList.forEach( function ( resultsPerModMassArrayValue, index, array ) {
			
			var dataForOneModMass = resultsPerModMassArrayValue;

			var countPerSearchIdMap_KeyProjectSearchId = dataForOneModMass.countPerSearchIdMap_KeyProjectSearchId;
			
			var modMassLabel = dataForOneModMass.modMassLabel;

			if ( dataForOneModMass.noModMass ) {
				modMassLabel = "None";
			}
			
			var chartEntry = [ modMassLabel ];


			_project_search_ids.forEach( function ( _project_search_ids_ArrayValue, index, array ) {
				
				var chartCountPerSearchIdEntry = countPerSearchIdMap_KeyProjectSearchId[ _project_search_ids_ArrayValue ];
				
//				var colorAndbarColor = this.getColorAndBarColorFromLinkType( linkType );

				var count = chartCountPerSearchIdEntry.count;
				var totalCount= chartCountPerSearchIdEntry.totalCount;

				var fraction = count / totalCount;
				if ( totalCount == 0 ) {
					fraction = 0;
				}

				var countString = count;
				try {
					countString = count.toLocaleString();
				} catch( e ) {
				}

				var totalCountString = totalCount;
				try {
					totalCountString = totalCount.toLocaleString();
				} catch( e ) {
				}

				var fractionDisplay = fraction.toPrecision( _PSM_COUNT_PER_MODIFICATION_CHART_GLOBALS._ENTRY_ANNOTATION_TEXT_SIGNIFICANT_DIGITS )

				var tooltipText = "<div  style='padding: 4px;'>" +
				"<div style='margin-bottom: 3px;'>Search id: " + chartCountPerSearchIdEntry.searchId.toString() + "</div>" +
				"<div style='margin-bottom: 3px;'>Fraction: " + fractionDisplay + "</div>" +
				countString + 
				" PSM Count for Modification / " + 
				totalCountString + " Total PSMs" + "</div>";

//				var colorAndbarColor = this.getColorAndBarColorFromLinkType( linkType );

				var chartY = fraction;

//				var entryAnnotationText = chartCountPerSearchIdEntry.fractionDisplay;

				chartEntry.push( chartY );
				chartEntry.push( _colorsPerSearch[ index ] /* colorAndbarColor.barColor */ );
				chartEntry.push( tooltipText );

				if ( chartY > maxYvalue ) {
					maxYvalue = chartY;
				}

//				barColors.push( colorAndbarColor.color );
				
			}, this /* passed to function as this */ );

			chartData.push( chartEntry );
			
		}, this /* passed to function as this */ );

		var chartOptionsVAxisMaxValue = undefined;

		if ( maxYvalue === 0 ) {
			//  If only value for v axis for bars is zero, the scale bars are from -1 to 1 which is wrong 
			//  so set chartOptionsVAxisMaxValue = 1.
			chartOptionsVAxisMaxValue = 1;
		}
		

//		var vAxisTicks = this._get_PSM_Count_Per_Modification_TickMarks( { maxValue : fractionMax } );

//		var barColors = [ colorAndbarColor.color ]; // must be an array

		var chartTitle = 'Fraction PSMs with Modification (' + linkType + ")";
		var optionsFullsize = {
				//  Overridden for Specific elements like Chart Title and X and Y Axis labels
				fontSize: _PSM_COUNT_PER_MODIFICATION_CHART_GLOBALS._CHART_DEFAULT_FONT_SIZE,  //  Default font size - using to set font size for tick marks.

				title: chartTitle, // Title above chart
				titleTextStyle: {
					color : _PROXL_DEFAULT_FONT_COLOR, //  Set default font color
//					color: <string>,    // any HTML string color ('red', '#cc00cc')
//					fontName: <string>, // i.e. 'Times New Roman'
					fontSize: _PSM_COUNT_PER_MODIFICATION_CHART_GLOBALS._TITLE_FONT_SIZE, // 12, 18 whatever you want (don't specify px)
//					bold: <boolean>,    // true or false
//					italic: <boolean>   // true of false
				},
				//  X axis label below chart
				hAxis: { title: 'Modification', titleTextStyle: { color: 'black', fontSize: _PSM_COUNT_PER_MODIFICATION_CHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
				},  
				//  Y axis label left of chart
				vAxis: { title: 'Fraction', titleTextStyle: { color: 'black', fontSize: _PSM_COUNT_PER_MODIFICATION_CHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
				,baseline: 0     // always start at zero
//				,ticks: vAxisTicks
				,maxValue : chartOptionsVAxisMaxValue
				},
//				legend: { position: 'none' }, //  position: 'none':  Don't show legend of bar colors in upper right corner
//				width : 500, 
//				height : 300,   // width and height of chart, otherwise controlled by enclosing div
				colors: _colorsPerSearch, // barColors,  //  Assigned to each bar
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

	};

	/**
	 * 
	 */
//	this._get_PSM_Count_Per_Modification_TickMarks = function( params ) {
//	var maxValue = params.maxValue;
//	if ( maxValue < 5 ) {
//	var tickMarks = [ 0 ];
//	for ( var counter = 1; counter <= maxValue; counter++ ) {
//	tickMarks.push( counter );
//	}
//	return tickMarks;
//	}
//	return undefined; //  Use defaults
//	};



};

/**
 * page variable 
 */

var qcMergedPageChart_PSM_Per_Modification = new QCMergedPageChart_PSM_Per_Modification();

export { qcMergedPageChart_PSM_Per_Modification }
