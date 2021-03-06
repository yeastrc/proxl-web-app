/**
 * qcMergedPageChart_Peptide_Lengths.js
 * 
 * Javascript for the viewQCMerged.jsp page - Chart Peptide Lengths Vs Peptide Count
 * 
 * page variable qcMergedPageChart_Peptide_Lengths
 * 
 * Merged QC Page
 * 
 * This code has been updated to cancel existing active AJAX calls when "Update from Database" button is clicked.
 *   This is done so that previous AJAX responses don't overlay new AJAX responses.
 */

//JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

//  require full Handlebars since compiling templates
const Handlebars = require('handlebars');


import { webserviceCallStandardPost } from 'page_js/webservice_call_common/webserviceCallStandardPost.js';

import { qc_pages_Single_Merged_Common } from './qc_pages_Single_Merged_Common.js';

import { qcChartDownloadHelp } from './qcChart_Download_Help_HTMLBlock.js';

/**
 * Constructor 
 */
var QCMergedPageChart_Peptide_Lengths = function() {

	//  Download data URL
	var _downloadStrutsAction = "downloadQC_PeptideLengthChartData.do";

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

		var $PeptideLengthsCountsBlock = $("#PeptideLengthsCountsBlock");
		if ( $PeptideLengthsCountsBlock.length === 0 ) {
			throw Error( "unable to find HTML element with id 'PeptideLengthsCountsBlock'" );
		}
		$PeptideLengthsCountsBlock.empty();
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

		var $PeptideLengthsCountsBlock = $("#PeptideLengthsCountsBlock");
		if ( $PeptideLengthsCountsBlock.length === 0 ) {
			throw Error( "unable to find HTML element with id 'PeptideLengthsCountsBlock'" );
		}
		$PeptideLengthsCountsBlock.empty();
		
		var hash_json_Contents = _get_hash_json_Contents();

		var selectedLinkTypes = hash_json_Contents.linkTypes;

		// Add cells for selected link types
		selectedLinkTypes.forEach( function ( currentArrayValue, index, array ) {
			var selectedLinkType = currentArrayValue;

			//  Add empty chart with Loading message
			var $chart_outer_container_jq =
				this._addChartOuterTemplate( { linkType : selectedLinkType, $chart_group_container_table_jq : $PeptideLengthsCountsBlock } );
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

				const url = "services/qc/dataPage/peptideLengthsHistogram_Merged";

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

		var results = _chartsData.results;
		var dataForChartPerLinkTypeList = results.dataForChartPerLinkTypeList;

		var $PeptideLengthsCountsBlock = $("#PeptideLengthsCountsBlock");
		if ( $PeptideLengthsCountsBlock.length === 0 ) {
			throw Error( "unable to find HTML element with id 'PeptideLengthsCountsBlock'" );
		}
		$PeptideLengthsCountsBlock.empty();

		dataForChartPerLinkTypeList.forEach( function ( currentArrayValue, indexForLinkType, array ) {
			var entryForLinkType = currentArrayValue;
			var linkType = entryForLinkType.linkType;
			var dataFound = entryForLinkType.dataFound;

			if ( ! dataFound ) {
				//  No data for this link type

				var $chart_outer_container_jq =
					this._addChartOuterTemplate( { $chart_group_container_table_jq : $PeptideLengthsCountsBlock } );
				//  Add empty chart with No Data message
				this._placeEmptyDummyChartForMessage( { 
					$chart_outer_container_jq : $chart_outer_container_jq, 
					//				linkType : selectedLinkType, 
					messagePrefix:  _DUMMY_CHART_STATUS_TEXT_PREFIX_NO_DATA,
					messageSuffix:  _DUMMY_CHART_STATUS_TEXT_SUFFIX_NO_DATA
				} );

				return;  //  EARLY exit for this array element
			}

			var $chart_outer_container_jq =
				this._addChartOuterTemplate( { $chart_group_container_table_jq : $PeptideLengthsCountsBlock } );
			var $chart_container_jq = this._addChartInnerTemplate( { $chart_outer_container_jq : $chart_outer_container_jq } );

			var colorAndbarColor = this.getColorAndBarColorFromLinkType( linkType );

			this._addPeptideLengthsHistogram_Chart( { entryForLinkType: entryForLinkType, colorAndbarColor : colorAndbarColor, chartIndex : indexForLinkType, $chartContainer : $chart_container_jq } );
			
			//  Download Data
			
			var hash_json_Contents = _get_hash_json_Contents();
			//  Set link types to chart link type
			hash_json_Contents.linkTypes = [ linkType ];

			var downloadSummaryDataCallback = function( params ) {
//				var clickedThis = params.clickedThis;
				
				qcChartDownloadHelp._downloadBoxplotChartSummaryData( { filenamePartChartName : "qc-qc-peptide-length-summary-" , entryForLinkType : entryForLinkType, _project_search_ids : _project_search_ids } );
			};
			
			var downloadDataCallback = function( params ) {
//				var clickedThis = params.clickedThis;
				//  Download the data for params
				const dataToSend = { projectSearchIds : _project_search_ids, qcPageQueryJSONRoot : hash_json_Contents };
				qc_pages_Single_Merged_Common.submitDownloadForParams( { downloadStrutsAction : _downloadStrutsAction, dataToSend } );
			};
			
			//  Get Help tooltip HTML
			var elementId = "peptide_level_block_help_tooltip_" + linkType
			var $peptide_level_block_help_tooltip_LinkType = $("#" + elementId );
			if ( $peptide_level_block_help_tooltip_LinkType.length === 0 ) {
				throw Error( "No element found with id '" + elementId + "' " );
			}
			var helpTooltipHTML = $peptide_level_block_help_tooltip_LinkType.html();
			
			qcChartDownloadHelp.add_DownloadClickHandlers_HelpTooltip( { 
				$chart_outer_container_for_download_jq :  $chart_outer_container_jq, 
				downloadDataCallback : downloadDataCallback,
				downloadSummaryDataCallback : downloadSummaryDataCallback,
				helpTooltipHTML : helpTooltipHTML,
				helpTooltip_Wide : true 
			} );
			
			// Add tooltips for download links
			addToolTips( $chart_outer_container_jq );
			
		}, this /* passed to function as this */ );

		_chartsDisplayed = true;
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
		var chartIndex = params.chartIndex;  //  zero based
		var $chartContainer = params.$chartContainer;

		var linkType = entryForLinkType.linkType;
		var dataForChartPerSearchIdMap_KeyProjectSearchId = entryForLinkType.dataForChartPerSearchIdMap_KeyProjectSearchId;

		//  Get max peptideLengths_outliers length
		
		var peptideLengths_outliers_Max_Length = undefined;

		var peptideLengths_outliers_Min_Length = undefined;
		

		_project_search_ids.forEach( function ( _project_search_ids_ArrayValue, index, array ) {
			
			var dataForChartPerSearchIdEntry = dataForChartPerSearchIdMap_KeyProjectSearchId[ _project_search_ids_ArrayValue ];

			if ( dataForChartPerSearchIdEntry.peptideLengths_outliers ) { // peptideLengths_outliers null if no outliers
				
				if ( peptideLengths_outliers_Max_Length === undefined 
						|| ( dataForChartPerSearchIdEntry.peptideLengths_outliers.length
								&& dataForChartPerSearchIdEntry.peptideLengths_outliers.length > peptideLengths_outliers_Max_Length ) ) {
					peptideLengths_outliers_Max_Length = dataForChartPerSearchIdEntry.peptideLengths_outliers.length; 
				}

				if ( peptideLengths_outliers_Min_Length === undefined 
						|| ( dataForChartPerSearchIdEntry.peptideLengths_outliers.length
								&& dataForChartPerSearchIdEntry.peptideLengths_outliers.length < peptideLengths_outliers_Min_Length ) ) {
					peptideLengths_outliers_Min_Length = dataForChartPerSearchIdEntry.peptideLengths_outliers.length; 
				}
			}
		}, this /* passed to function as this */ );

		if ( peptideLengths_outliers_Max_Length === undefined ) {
			// not set so set to 0
			peptideLengths_outliers_Max_Length = 0;
		}
		if ( peptideLengths_outliers_Min_Length === undefined ) {
			// not set so set to 0
			peptideLengths_outliers_Min_Length = 0;
		}

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
				
//		!!!!!!!!!!   Adding a variable number of outliers does not work when putting null for missing outliers for a given search id
		
		//   Add header entries for max number of outliers found across all link types
		for ( var counter = 0; counter < peptideLengths_outliers_Max_Length; counter++ ) {
			chartDataHeaderEntry.push( 'Outlier Point' );
			chartDataHeaderEntry.push( {type:'string', role: 'style' } );
		}
				
		chartData.push( chartDataHeaderEntry );
		
		var searchCountPlusOne = _project_search_ids.length + 1;
		
		var wholeChartTooltipData = [];

		_project_search_ids.forEach( function ( _project_search_ids_ArrayValue, indexForProjectSearchId, array ) {
			
			var dataForChartPerSearchIdEntry = dataForChartPerSearchIdMap_KeyProjectSearchId[ _project_search_ids_ArrayValue ];
			
			var searchId = dataForChartPerSearchIdEntry.searchId;

			var colorForSearchEntry = _colorsPerSearch[ indexForProjectSearchId ];

			
			var chartIntervalMaxString = dataForChartPerSearchIdEntry.chartIntervalMax;
			var thirdQuartileString = dataForChartPerSearchIdEntry.thirdQuartile;
			var medianString = dataForChartPerSearchIdEntry.median;
			var firstQuartileString = dataForChartPerSearchIdEntry.firstQuartile;
			var chartIntervalMinString = dataForChartPerSearchIdEntry.chartIntervalMin;
			

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
			
			if ( dataForChartPerSearchIdEntry.peptideLengths_outliers && dataForChartPerSearchIdEntry.peptideLengths_outliers.length > 0 ) {
				//  peptideLengths_outliers is not null and not empty array

				//  Place first so under the visible outlier point
				//  Add the last outlier point for each search to the max length of outlier.  Done so this X-axis entry has the same number of Y-axis entries as for Max Outliers X-axis entry
				var peptideLengths_outliers_lastEntry = dataForChartPerSearchIdEntry.peptideLengths_outliers[ dataForChartPerSearchIdEntry.peptideLengths_outliers.length - 1 ];
				for ( var counter = dataForChartPerSearchIdEntry.peptideLengths_outliers.length; counter < peptideLengths_outliers_Max_Length; counter++ ) {
					chartEntry.push( peptideLengths_outliers_lastEntry );
					chartEntry.push( 'point { visible: false; size: 0; }' ); // style as hidden, size zero since not an actual valid point 
				}
				
				//  Add each outlier
				dataForChartPerSearchIdEntry.peptideLengths_outliers.forEach( function ( currentArrayValue, indexForSearchId, array ) {
					var chartOutlierString = currentArrayValue;
					chartEntry.push( { v: currentArrayValue , f: '\nOutlier Value: ' + chartOutlierString } );
					chartEntry.push( 'point { visible: true; size: 2; color: ' + colorForSearchEntry + ' }' ); // style required to make visible :  color: blue; opacity: 1; 
				}, this /* passed to function as this */ );


			} else {
				//  No outliers so add invisible point at the chartIntervalMax position
				for ( var counter = 0; counter < peptideLengths_outliers_Max_Length; counter++ ) {
					chartEntry.push( dataForChartPerSearchIdEntry.chartIntervalMax );
					chartEntry.push( 'point { visible: false; size: 0; }' ); // style as hidden, size zero since not an actual valid point 
				}
			}
			
			chartData.push( chartEntry );

		}, this /* passed to function as this */ );

		
		var chartTitle = 'Distribution of peptide lengths (' + linkType + ")";

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
				legend: {position: 'none'},
				hAxis: {
					title: 'Search Number'
						, titleTextStyle: { color: 'black', fontSize: PeptideLengthsCHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
//					gridlines: {color: '#fff'}
				},
				vAxis: 
				{ 	title: 'Peptide Length'
					, titleTextStyle: { color: 'black', fontSize: PeptideLengthsCHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
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

var qcMergedPageChart_Peptide_Lengths = new QCMergedPageChart_Peptide_Lengths();

export { qcMergedPageChart_Peptide_Lengths }
