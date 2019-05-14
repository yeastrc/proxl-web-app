
//  viewPsmPerPeptideLoadedFromWebServiceTemplate.js

//  Process and load data into the file viewPsmPerPeptideLoadedFromWebServiceTemplateFragment.jsp


//JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

//  require full Handlebars since compiling templates
const Handlebars = require('handlebars');


//Class contructor

var ViewPsmPerPeptideLoadedFromWebServiceTemplate = function() {

	var _DATA_LOADED_DATA_KEY = "dataLoaded";
	var _handlebarsTemplate_psm_per_peptide_block_template = null;
	var _handlebarsTemplate_psm_per_peptide_data_row_entry_template = null;
	var _psm_per_peptide_row_entry_no_annotation_data_no_scan_data_row_HTML = null;

	//  Queue of params for this._addChartsParams() before Google Chart API is loaded 
	var _addChartsParamsQueue = [];
	
	var _googleChartAPIloaded = false;

	var _excludeLinksWith_Root =  null;
	var _psmPeptideAnnTypeIdDisplay = null;
	var _psmPeptideCutoffsRootObject = null;

	this.googleChartAPIloaded = function() {
		_googleChartAPIloaded = true;
		this._addChartsFromParamsQueue();
	};

	//////////////
	this.setPsmPeptideCriteria = function( psmPeptideCutoffsRootObject ) {
		_psmPeptideCutoffsRootObject = psmPeptideCutoffsRootObject;
	};

	//////////////
	this.setPsmPeptideAnnTypeIdDisplay = function( psmPeptideAnnTypeIdDisplay ) {
		_psmPeptideAnnTypeIdDisplay = psmPeptideAnnTypeIdDisplay;
	};
	
	//////////////
	this.setExcludeLinksWith_Root = function( excludeLinksWith_Root ) {
		_excludeLinksWith_Root = excludeLinksWith_Root;
	};

	//////////
	//   Called by HTML Element onclick 
	this.showHidePsmPerPeptide = function( params ) {
		try {
			var clickedElement = params.clickedElement;
			var $clickedElement = $( clickedElement );
			var $itemToToggle = $clickedElement.next();
			if( $itemToToggle.is(":visible" ) ) {
				$itemToToggle.hide(); 
				$clickedElement.find(".toggle_visibility_expansion_span_jq").show();
				$clickedElement.find(".toggle_visibility_contraction_span_jq").hide();
			} else { 
				$itemToToggle.show();
				$clickedElement.find(".toggle_visibility_expansion_span_jq").hide();
				$clickedElement.find(".toggle_visibility_contraction_span_jq").show();
				this.loadAndInsertPsmsIfNeeded( { $topTRelement : $itemToToggle, $clickedElement : $clickedElement } );
			}
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
		return false;  // does not stop bubbling of click event
	};

	////////////////////////
	this.loadAndInsertPsmsIfNeeded = function( params ) {
		var objectThis = this;
		var $topTRelement = params.$topTRelement;
		var $clickedElement = params.$clickedElement;
		var dataLoaded = $topTRelement.data( _DATA_LOADED_DATA_KEY );
		if ( dataLoaded ) {
			return;  //  EARLY EXIT  since data already loaded. 
		}
		var psm_id = $clickedElement.attr( "data-psm_id" );
		var project_search_id = $clickedElement.attr( "data-project_search_id" );
		var ajaxRequestData = {
				psm_id : psm_id,
				project_search_id : project_search_id
		};
		$.ajax({
			url : "services/data/getPsmPerPeptide",
//			traditional: true,  //  Force traditional serialization of the data sent
//			//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
//			//   So searchIds array is passed as "searchIds=<value>" which is what Jersey expects
			data : ajaxRequestData,  // The data sent as params on the URL
			dataType : "json",
			success : function( ajaxResponseData ) {
				try {
					objectThis.loadAndInsertPsmsResponse( 
							{ ajaxResponseData : ajaxResponseData, 
								$topTRelement : $topTRelement, 
								ajaxRequestData : ajaxRequestData
							} );
					$topTRelement.data( _DATA_LOADED_DATA_KEY, true );
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			},
			failure: function(errMsg) {
				handleAJAXFailure( errMsg );
			},
			error : function(jqXHR, textStatus, errorThrown) {
				handleAJAXError(jqXHR, textStatus, errorThrown);
			}
		});
	};

	////////////
	this.loadAndInsertPsmsResponse = function( params ) {
		
		var ajaxRequestData = params.ajaxRequestData;
		var ajaxResponseData = params.ajaxResponseData;
		
		var annotationLabels = ajaxResponseData.annotationLabels;
		var peptideRows = ajaxResponseData.peptideRows;
		var searchHasScanData = ajaxResponseData.searchHasScanData;
		
		//	var ajaxRequestData = params.ajaxRequestData;
		var $topTRelement = params.$topTRelement;
		var $psm_per_peptide_data_container = $topTRelement.find(".child_data_container_jq");
		if ( $psm_per_peptide_data_container.length === 0 ) {
			throw Error( "unable to find HTML element with class 'child_data_container_jq'" );
		}
		$psm_per_peptide_data_container.empty();
		if ( _handlebarsTemplate_psm_per_peptide_block_template === null ) {
			var handlebarsSource_psm_per_peptide_block_template = $( "#psm_per_peptide_block_template" ).html();
			if ( handlebarsSource_psm_per_peptide_block_template === undefined ) {
				throw Error( "handlebarsSource_psm_per_peptide_block_template === undefined" );
			}
			if ( handlebarsSource_psm_per_peptide_block_template === null ) {
				throw Error( "handlebarsSource_psm_per_peptide_block_template === null" );
			}
			_handlebarsTemplate_psm_per_peptide_block_template = Handlebars.compile( handlebarsSource_psm_per_peptide_block_template );
		}
		if ( _handlebarsTemplate_psm_per_peptide_data_row_entry_template === null ) {
			var handlebarsSource_psm_per_peptide_row_entry_template = $( "#psm_per_peptide_data_row_entry_template" ).html();
			if ( handlebarsSource_psm_per_peptide_row_entry_template === undefined ) {
				throw Error( "handlebarsSource_psm_per_peptide_row_entry_template === undefined" );
			}
			if ( handlebarsSource_psm_per_peptide_row_entry_template === null ) {
				throw Error( "handlebarsSource_psm_per_peptide_row_entry_template === null" );
			}
			_handlebarsTemplate_psm_per_peptide_data_row_entry_template = Handlebars.compile( handlebarsSource_psm_per_peptide_row_entry_template );
		}
		if ( _psm_per_peptide_row_entry_no_annotation_data_no_scan_data_row_HTML === null ) {
			_psm_per_peptide_row_entry_no_annotation_data_no_scan_data_row_HTML = $("#psm_data_row_entry_no_annotation_data_no_scan_data_row").html();
		}
		
		var peptideLinkPosition_1_AnyRows = false;
		var peptideLinkPosition_2_AnyRows = false;
		var scanDataAnyRows = false;
		var scanNumberAnyRows = false;
		var scanFilenameAnyRows = false;
		
		peptideRows.forEach(function( peptideRow, index, array) {
			if ( peptideRow.peptideLinkPosition_1 !== undefined && 
					peptideRow.peptideLinkPosition_1 !== null && 
					peptideRow.peptideLinkPosition_1 !== -1 ) {
				peptideLinkPosition_1_AnyRows = true;
			}
			if ( peptideRow.peptideLinkPosition_2 !== undefined && 
					peptideRow.peptideLinkPosition_2 !== null &&
					peptideRow.peptideLinkPosition_2 !== -1 ) {
				peptideLinkPosition_2_AnyRows = true;
			}
			if (  peptideRow.showViewSpectrumLink ) {
				scanDataAnyRows = true;
			}
			if (  peptideRow.scanNumber ) {
				scanNumberAnyRows = true;
			}
			if (  peptideRow.scanFilename ) {
				scanFilenameAnyRows = true;
			}
		}, this );

		var showViewSpectrumLinkColumn = false;
		
		if ( searchHasScanData && scanDataAnyRows ) {
			showViewSpectrumLinkColumn = true;
		}
		
		//  Context for creating column headings HTML
		var context = {
				annotationLabels : annotationLabels,
				peptideLinkPosition_1_AnyRows : peptideLinkPosition_1_AnyRows,
				peptideLinkPosition_2_AnyRows : peptideLinkPosition_2_AnyRows,
				showViewSpectrumLinkColumn : showViewSpectrumLinkColumn,
				scanDataAnyRows : scanDataAnyRows,
				scanNumberAnyRows : scanNumberAnyRows,
				scanFilenameAnyRows : scanFilenameAnyRows,
				project_search_id : ajaxRequestData.project_search_id
		};

		var html = _handlebarsTemplate_psm_per_peptide_block_template( context );
		var $psm_per_peptide_block_template = $( html ).appendTo( $psm_per_peptide_data_container ); 
		var $psm_per_peptide_table_jq = $psm_per_peptide_block_template.find(".psm_per_peptide_table_jq");

		if ( $psm_per_peptide_table_jq.length === 0 ) {
			throw Error( "unable to find HTML element with class 'psm_per_peptide_table_jq'" );
		}

		peptideRows.forEach(function( peptideRow, index, array) {
			// Build Mods
			var mods = [];
			peptideRow.mods.forEach(function( modEntry, index, array ) {
				mods.push( modEntry.position + "(" + modEntry.mass + ")" );
			}, this );
			
			var modsString = mods.join(",");
			
			//  Context for creating data row HTML
			var context = { 
					peptideRow : peptideRow,
					mods : modsString,
					peptideLinkPosition_1_AnyRows : peptideLinkPosition_1_AnyRows,
					peptideLinkPosition_2_AnyRows : peptideLinkPosition_2_AnyRows,
					showViewSpectrumLinkColumn : showViewSpectrumLinkColumn,
					scanDataAnyRows : scanDataAnyRows,
					scanNumberAnyRows : scanNumberAnyRows,
					scanFilenameAnyRows : scanFilenameAnyRows
			};
			var html = _handlebarsTemplate_psm_per_peptide_data_row_entry_template(context);

			//		var $psm_per_peptide_entry = 
			$(html).appendTo($psm_per_peptide_table_jq);
		}, this );

		//  Add tablesorter to the populated table of psm data
		setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else
			$psm_per_peptide_table_jq.tablesorter(); // gets exception if there are no data rows
		},10);

		addToolTips( $psm_per_peptide_block_template );

		if ( window.linkInfoOverlayWidthResizer ) {
			window.linkInfoOverlayWidthResizer();
		}
	};
	
	
	this._addChartsFromParamsQueue = function() {
		
		if ( _addChartsParamsQueue && _addChartsParamsQueue.length > 0  ) {
			//  process the entries
			for ( var index = 0; index < _addChartsParamsQueue.length; index++ ) {
				var paramsEntry = _addChartsParamsQueue[ index ];
				this._addCharts( paramsEntry )
			}
			//  Clear the queue
			_addChartsParamsQueue = [];
		}
		
	}
	

	///
	this._addCharts = function( params ) {
		var objectThis = this;
		var psmWebDisplayList = params.psmWebDisplayList;
		var $psm_data_container = params.$psm_data_container;
		var $psm_qc_charts_container_jq = $psm_data_container.find(".psm_qc_charts_container_jq");
		if ( $psm_qc_charts_container_jq.length === 0 ) {
			throw Error( "unable to find HTML element with class 'psm_qc_charts_container_jq'" );
		}

		var hasChargeDataAllRows = true;
		var hasRetentionTimeDataAllRows = true;

		for ( var psmIndex = 0; psmIndex < psmWebDisplayList.length ; psmIndex++ ) {
			var psm = psmWebDisplayList[ psmIndex ];
			if ( psm.retentionTime === undefined || psm.retentionTime === null ) {
				hasRetentionTimeDataAllRows = false;
			}
			if ( psm.charge === undefined || psm.charge === null ) {
				hasChargeDataAllRows = false;
			}
		}

		if ( hasChargeDataAllRows ) {
			this._addChargeChart( { psmWebDisplayList : psmWebDisplayList, $psm_qc_charts_container_jq : $psm_qc_charts_container_jq } )
		}
		if ( hasRetentionTimeDataAllRows ) {
			this._addRetentionTimeChart( { psmWebDisplayList : psmWebDisplayList, $psm_qc_charts_container_jq : $psm_qc_charts_container_jq } )
		}
		
		var $chart_download_link_jq_All = $psm_qc_charts_container_jq.find(".chart_download_link_jq");
		$chart_download_link_jq_All.click( function( event ) { 
			objectThis._downloadChart( { clickedThis : this } ); 
			event.preventDefault();
		});

		if ( window.linkInfoOverlayWidthResizer ) {
			window.linkInfoOverlayWidthResizer();
		}
	};
	

	//  Overridden for Specific elements like Chart Title and X and Y Axis labels
	var _CHART_DEFAULT_FONT_SIZE = 12;  //  Default font size - using to set font size for tick marks.

	var _TITLE_FONT_SIZE = 15; // In PX
	var _AXIS_LABEL_FONT_SIZE = 14; // In PX
	var _TICK_MARK_TEXT_FONT_SIZE = 14; // In PX

	///
	this._addChargeChart = function( params ) {
		var psmWebDisplayList = params.psmWebDisplayList;
		var $psm_qc_charts_container_jq = params.$psm_qc_charts_container_jq;
		
		var $psm_qc_charge_chart_outer_container_jq = $psm_qc_charts_container_jq.find(".psm_qc_charge_chart_outer_container_jq");
		if ( $psm_qc_charge_chart_outer_container_jq.length === 0 ) {
			throw Error( "unable to find HTML element with class 'psm_qc_charge_chart_outer_container_jq'" );
		}
		var $psm_qc_charge_chart_container_jq = $psm_qc_charge_chart_outer_container_jq.find(".psm_qc_charge_chart_container_jq");
		if ( $psm_qc_charge_chart_container_jq.length === 0 ) {
			throw Error( "unable to find HTML element with class 'psm_qc_charge_chart_container_jq'" );
		}
		
		$psm_qc_charge_chart_outer_container_jq.show();

		//  Process charge and get count per value  
		var countsPerCharge = {};
		for ( var psmIndex = 0; psmIndex < psmWebDisplayList.length ; psmIndex++ ) {
			var psm = psmWebDisplayList[ psmIndex ];
			var charge = psm.charge;
			if ( countsPerCharge[ charge ] === undefined ) {
				countsPerCharge[ charge ] = 1;
			} else {
				countsPerCharge[ charge ]++;
			}
		}
		//  Extract counts to array, including object key
		var countsPerChargeArray = [];
		var maxChargeCount = 0;
		var countsPerChargeKeys = Object.keys( countsPerCharge );
		for ( var index = 0; index < countsPerChargeKeys.length; index++ ) {
			var countsPerChargeKeysEntry = countsPerChargeKeys[ index ];
			var countsPerChargeEntry = countsPerCharge[ countsPerChargeKeysEntry ];
			var countsPerChargeWithKey = { charge: countsPerChargeKeysEntry, count: countsPerChargeEntry };
			countsPerChargeArray.push( countsPerChargeWithKey );
			if ( countsPerChargeEntry > maxChargeCount ) {
				maxChargeCount = countsPerChargeEntry;
			}
		}
		//  Sort in charge order
		countsPerChargeArray.sort( function( a, b ) {
			return a.charge - b.charge;
		})

		//  chart data for Google charts
		var chartData = [];

		var chartDataHeaderEntry = [ 'AAAAAAAAA', "MMM", {role: "tooltip", 'p': {'html': true} } ]; 
		chartData.push( chartDataHeaderEntry );

		for ( var index = 0; index < countsPerChargeArray.length; index++ ) {
			var countsPerChargeArrayEntry = countsPerChargeArray[ index ];
			var chartEntry = [ 
				"+" + countsPerChargeArrayEntry.charge, 
				countsPerChargeArrayEntry.count, 
				//  Tool Tip
				"Charge: +" + countsPerChargeArrayEntry.charge + ", Count: " + countsPerChargeArrayEntry.count ];
			chartData.push( chartEntry );
		}
		
		var vAxisTicks = this._getChargeCountTickMarks( { maxValue : maxChargeCount } );

		var chartTitle = 'Counts Per Charge';
		var optionsFullsize = {
			//  Overridden for Specific elements like Chart Title and X and Y Axis labels
							fontSize: _CHART_DEFAULT_FONT_SIZE,  //  Default font size - using to set font size for tick marks.
							
				title: chartTitle, // Title above chart
			    titleTextStyle: {
//			        color: <string>,    // any HTML string color ('red', '#cc00cc')
//			        fontName: <string>, // i.e. 'Times New Roman'
			        fontSize: _TITLE_FONT_SIZE, // 12, 18 whatever you want (don't specify px)
//			        bold: <boolean>,    // true or false
//			        italic: <boolean>   // true of false
			    },
				//  X axis label below chart
				hAxis: { title: 'Charge', titleTextStyle: { color: 'black', fontSize: _AXIS_LABEL_FONT_SIZE }
				},  
				//  Y axis label left of chart
				vAxis: { title: 'Count', titleTextStyle: { color: 'black', fontSize: _AXIS_LABEL_FONT_SIZE }
					,baseline: 0     // always start at zero
					,ticks: vAxisTicks
					,maxValue : maxChargeCount
				},
				legend: { position: 'none' }, //  position: 'none':  Don't show legend of bar colors in upper right corner
//				width : 300, 
//				height : 300,   // width and height of chart, otherwise controlled by enclosing div
				colors: ['#A55353'],  //  Color of bars, Proxl shades of red
				tooltip: {isHtml: true}
//				,chartArea : { left : 140, top: 60, 
//				width: objectThis.RETENTION_TIME_COUNT_CHART_WIDTH - 200 ,  //  was 720 as measured in Chrome
//				height : objectThis.RETENTION_TIME_COUNT_CHART_HEIGHT - 120 }  //  was 530 as measured in Chrome
		};        
		// create the chart
		var data = google.visualization.arrayToDataTable( chartData );
		var chartFullsize = new google.visualization.ColumnChart( $psm_qc_charge_chart_container_jq[0] );
		chartFullsize.draw(data, optionsFullsize);
	};

	///////////
	this._getChargeCountTickMarks = function( params ) {
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
	
//	/
	this._addRetentionTimeChart = function( params ) {
		var psmWebDisplayList = params.psmWebDisplayList;
		var $psm_qc_charts_container_jq = params.$psm_qc_charts_container_jq;

		var $psm_qc_retention_time_chart_outer_container_jq = $psm_qc_charts_container_jq.find(".psm_qc_retention_time_chart_outer_container_jq");
		if ( $psm_qc_retention_time_chart_outer_container_jq.length === 0 ) {
			throw Error( "unable to find HTML element with class 'psm_qc_retention_time_chart_outer_container_jq'" );
		}
		var $psm_qc_retention_time_chart_container_jq = $psm_qc_retention_time_chart_outer_container_jq.find(".psm_qc_retention_time_chart_container_jq");
		if ( $psm_qc_retention_time_chart_container_jq.length === 0 ) {
			throw Error( "unable to find HTML element with class 'psm_qc_retention_time_chart_container_jq'" );
		}
		
		$psm_qc_retention_time_chart_outer_container_jq.show();
		
		//  Get max and min retention time
		var maxRetentionTime = null;
		var minRetentionTime = null;
		for ( var index = 0; index < psmWebDisplayList.length; index++ ) {
			var psmWebDisplayEntry = psmWebDisplayList[ index ];
			var entryRetentionTime = psmWebDisplayEntry.retentionTime;
			if ( maxRetentionTime === null ) {
				maxRetentionTime = entryRetentionTime;
				minRetentionTime = entryRetentionTime;
			} else {
				if ( entryRetentionTime > maxRetentionTime ) {
					maxRetentionTime = entryRetentionTime;
				}
				if ( entryRetentionTime < minRetentionTime ) {
					minRetentionTime = entryRetentionTime;
				}
			}
		}
		var maxMinusMinRetentionTime = maxRetentionTime - minRetentionTime;

		//  	Put retention times in buckets
		//  Determine # buckets and bucket size
		var numberBuckets = Math.floor( Math.sqrt( psmWebDisplayList.length ) );
		if ( numberBuckets < 6 ) {
			numberBuckets = 6;
		}
		if ( numberBuckets > psmWebDisplayList.length ) {
			numberBuckets = psmWebDisplayList.length;  
		}
		var bucketSize = maxMinusMinRetentionTime / numberBuckets;

		// initialize buckets to zero
		var buckets = [];
		for ( var index = 0; index < numberBuckets; index++ ) {
			buckets.push( 0 );
		}
		//  increment bucket counts for PSM retention time values
		for ( var index = 0; index < psmWebDisplayList.length; index++ ) {
			var psmWebDisplayEntry = psmWebDisplayList[ index ];
			var entryRetentionTime = psmWebDisplayEntry.retentionTime;
			var retentionTimeOffset = entryRetentionTime - minRetentionTime;
			var bucketIndex = 0;
			if ( retentionTimeOffset > 0 ) {
				var retentionTimeOffsetFraction = retentionTimeOffset /  maxMinusMinRetentionTime;
				bucketIndex = Math.floor( retentionTimeOffsetFraction * numberBuckets );
			}
			if ( bucketIndex < 0 ) {
				bucketIndex = 0;
			} else if ( bucketIndex >= numberBuckets ) {
				bucketIndex = numberBuckets - 1; //  required for maxRetetionTime entry since numberBuckets is then 1
			}
			if ( buckets[ bucketIndex ] === undefined ) {
				throw Error( "array buckets not initialized for index: " + bucketIndex );
			}
			buckets[ bucketIndex ]++;
		}
		
		var maxBucketCount = 0;
		
		var bucketSizeMinutes = bucketSize / 60;
		var bucketSizeMinutesHalf = bucketSizeMinutes / 2;

		//  chart data for Google charts
		var chartData = [];
		//  output columns specification
		//  With Tooltip
		chartData.push( ["retention time",
			"count",{role: "tooltip",  'p': {'html': true} }, 
//			, { role: 'style' } 
			] );
		for ( var index = 0; index < buckets.length; index++ ) {
			var bucketCount = buckets[ index ];
			if ( bucketCount > maxBucketCount ) {
				maxBucketCount = bucketCount;
			}
			//  With Tooltip
			var approxBucketStart = ( minRetentionTime + ( index * bucketSize ) ) / 60;
			var approxBucketEnd = ( minRetentionTime + ( ( index + 1 ) * bucketSize ) ) / 60;
			var approxBucketCenter = ( approxBucketStart + ( bucketSizeMinutesHalf ) );
			var approxBucketStartMinutesString = ( approxBucketStart).toFixed( 2 );
			var approxBucketEndMinutesString = ( approxBucketEnd ).toFixed( 2 );
			
			var tooltip = "<div style='margin: 10px;'>PSM count: " + bucketCount + 
			"<br>retention time approximately " + approxBucketStartMinutesString + 
			" to " + approxBucketEndMinutesString + 
			"</div>";
			
			var chartXaxisValue = approxBucketCenter;
			
			if ( maxMinusMinRetentionTime === 0 ) {
				//  Only 1 value for retention time
				
				//  Convert to string so Google Chart will display
				chartXaxisValue = approxBucketStart.toFixed( 2 );
				
				//  Change tooltip
				tooltip = "<div style='margin: 10px;'>PSM count: " + bucketCount + 
				"<br>retention time approximately " + approxBucketStartMinutesString + 
				"</div>";
			}
			
			chartData.push( [ chartXaxisValue, bucketCount, tooltip 
//				,  'stroke-width: 2;stroke-color: blue; '
				] );
		}

		var minDataX = minRetentionTime / 60;
		var maxDataX = maxRetentionTime / 60;
		
		var maxDataY = maxBucketCount;

		//  control the tick marks horizontal and vertical
		var hAxisTicks = this._getRetentionTimeTickMarks( { minValue : minDataX, maxValue : maxDataX } );
		
		var vAxisTicks = this._getRetentionTimeCountTickMarks( { maxValue : maxDataY } );
		
		var chartTitle = 'Count vs/ Retention Time';
		var optionsFullsize = {
				//  Overridden for Specific elements like Chart Title and X and Y Axis labels
				fontSize: _CHART_DEFAULT_FONT_SIZE,  //  Default font size - using to set font size for tick marks.
				
				title: chartTitle, // Title above chart
			    titleTextStyle: {
//			        color: <string>,    // any HTML string color ('red', '#cc00cc')
//			        fontName: <string>, // i.e. 'Times New Roman'
			        fontSize: _TITLE_FONT_SIZE, // 12, 18 whatever you want (don't specify px)
//			        bold: <boolean>,    // true or false
//			        italic: <boolean>   // true of false
			    },
			    //  X axis label below chart
				hAxis: { title: 'Retention Time (minutes)', titleTextStyle: { color: 'black', fontSize: _AXIS_LABEL_FONT_SIZE }
					,ticks: hAxisTicks
					, format:'#,###.##'
					,maxValue : maxDataX
					,gridlines: {  
		                color: 'none'  //  No vertical grid lines on the horzontal axis
		            }
				},  
				//  Y axis label left of chart
				vAxis: { title: 'Count', titleTextStyle: { color: 'black', fontSize: _AXIS_LABEL_FONT_SIZE }
					,baseline: 0                    // always start at zero
					,ticks: vAxisTicks
					, format:'#,###'
					,maxValue : maxDataY
				},
				legend: { position: 'none' }, //  position: 'none':  Don't show legend of bar colors in upper right corner
//				width : objectThis.RETENTION_TIME_COUNT_CHART_WIDTH, 
//				height : objectThis.RETENTION_TIME_COUNT_CHART_HEIGHT,   // width and height of chart, otherwise controlled by enclosing div
//				bar: { groupWidth: 5 },  // set bar width large to eliminate space between bars
				bar: { groupWidth: '100%' },  // set bar width large to eliminate space between bars
//				colors: ['red','blue'],  //  Color of bars
				colors: ['#A55353'],  //  Color of bars, Proxl shades of red, total counts is second
				tooltip: {isHtml: true}
//				,
//				isStacked: true
//				,chartArea : { left : 140, top: 60, 
//				width: objectThis.RETENTION_TIME_COUNT_CHART_WIDTH - 200 ,  //  was 720 as measured in Chrome
//				height : objectThis.RETENTION_TIME_COUNT_CHART_HEIGHT - 120 }  //  was 530 as measured in Chrome
		};        
		// create the chart
		var data = google.visualization.arrayToDataTable( chartData );
		var chartFullsize = new google.visualization.ColumnChart( $psm_qc_retention_time_chart_container_jq[0] );
		chartFullsize.draw(data, optionsFullsize);
//		google.visualization.events.addListener(chartFullsize, 'select', function(event) {
//
//			var tableSelection = chartFullsize.getSelection();
//			var tableSelection0 = tableSelection[ 0 ];
//			var column = tableSelection0.column;
//			var row = tableSelection0.row;
//			var chartDataForRow = chartData[ row ];
//			  
//			  var z = 0;
//		});
	};


	///////////
	this._getRetentionTimeTickMarks = function( params ) {
		var minValue = params.minValue; 
		var maxValue = params.maxValue;
		var maxValueMinusMinValue = maxValue - minValue;
		var tickMarks = [ 
			minValue,
			( maxValueMinusMinValue * 0.25 ) + minValue,
			( maxValueMinusMinValue * 0.5 ) + minValue,
			( maxValueMinusMinValue * 0.75 ) + minValue,
			maxValue ];
		return tickMarks;
	};
	

	///////////
	this._getRetentionTimeCountTickMarks = function( params ) {
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
	

	/**
	 * 
	 */
	this._downloadChart = function( params ) {
		try {
			var clickedThis = params.clickedThis;

			var $clickedThis = $( clickedThis );
			var download_type = $clickedThis.attr("data-download_type");
			var $psm_qc_either_chart_outer_container_jq = $clickedThis.closest(".psm_qc_either_chart_outer_container_jq");
			var chart_type = $psm_qc_either_chart_outer_container_jq.attr("data-chart_type");

			var getSVGContentsAsStringResult = this._getSVGContentsAsString( $psm_qc_either_chart_outer_container_jq );
			
			if ( getSVGContentsAsStringResult.errorException ) {
				throw errorException;
			}
			
			var fullSVG_String = getSVGContentsAsStringResult.fullSVG_String;
			
			var form = document.createElement( "form" );
			$( form ).hide();
			form.setAttribute( "method", "post" );
			form.setAttribute( "action", "convertAndDownloadSVG.do" );

			var svgStringField = document.createElement( "input" );
			svgStringField.setAttribute("name", "svgString");
			svgStringField.setAttribute("value", fullSVG_String );
			var fileTypeField = document.createElement( "input" );
			fileTypeField.setAttribute("name", "fileType");
			fileTypeField.setAttribute("value", download_type);
			form.appendChild( svgStringField );
			form.appendChild( fileTypeField );
			document.body.appendChild(form);    // Not entirely sure if this is necessary			
			form.submit();
			document.body.removeChild( form );

			var getSVGContentsAsStringResult = getSVGContentsAsString( $psm_qc_either_chart_outer_container_jq );
			var svgString = getSVGContentsAsStringResult.fullSVG_String;
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
		
	};
	

	/**
	 * 
	 */
	this._getSVGContentsAsString = function ( $psm_qc_either_chart_outer_container_jq ) {
		try {
			var $psm_qc_either_chart_container_jq = $psm_qc_either_chart_outer_container_jq.find(".psm_qc_either_chart_container_jq");
			if ( $psm_qc_either_chart_container_jq.length === 0 ) {
				// No element found with class psm_qc_either_chart_container_jq
				return { noPageElement : true };
			}
			var $svgRoot = $psm_qc_either_chart_container_jq.find("svg");
			if ( $svgRoot.length === 0 ) {
				// No <svg> element found
				return { noPageElement : true };
			}

			var svgContents = $svgRoot.html();
			var fullSVG_String = "<?xml version=\"1.0\" standalone=\"no\"?><!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">";
			fullSVG_String += "<svg id=\"svg\" ";
			fullSVG_String += "width=\"" + $svgRoot.attr( "width" ) + "\" ";
			fullSVG_String += "height=\"" + $svgRoot.attr( "height" ) + "\" ";
			fullSVG_String += "xmlns=\"http://www.w3.org/2000/svg\">" + svgContents + "</svg>";
			// fix the URL that google charts is putting into the SVG. Breaks parsing.
			fullSVG_String = fullSVG_String.replace( /url\(.+\#_ABSTRACT_RENDERER_ID_(\d+)\)/g, "url(#_ABSTRACT_RENDERER_ID_$1)" );	

			return { fullSVG_String : fullSVG_String};
		} catch( e ) {
			//  Not all browsers have svgElement.innerHTML which .html() tries to use, causing an exception
			return { errorException : e };
		}
	};
};

//	Static Singleton Instance of Class
var viewPsmPerPeptideLoadedFromWebServiceTemplate = new ViewPsmPerPeptideLoadedFromWebServiceTemplate();

window.viewPsmPerPeptideLoadedFromWebServiceTemplate = viewPsmPerPeptideLoadedFromWebServiceTemplate;

export { viewPsmPerPeptideLoadedFromWebServiceTemplate }
