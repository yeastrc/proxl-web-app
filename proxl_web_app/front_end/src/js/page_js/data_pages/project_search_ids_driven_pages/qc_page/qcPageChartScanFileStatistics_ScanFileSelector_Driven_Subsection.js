/**
 * qcPageChartScanFileStatistics_ScanFileSelector_Driven_Subsection.js
 * 
 * Javascript for the viewQC.jsp page - Chart Scan File Statistics
 * 
 *   Subsection (currently the only part) Of Scan File Statistics
 *     that are driven by the choice of Scan File
 * 
 */

//JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";


//   Variables Stored on 'window' 

const reportWebErrorToServer = window.reportWebErrorToServer;
const addToolTips = window.addToolTips;

const _PROXL_DEFAULT_FONT_COLOR = window._PROXL_DEFAULT_FONT_COLOR;


import { webserviceCallStandardPost } from 'page_js/webservice_call_common/webserviceCallStandardPost.js';

import { qc_pages_Single_Merged_Common } from './qc_pages_Single_Merged_Common.js';

import { qcChartDownloadHelp } from './qcChart_Download_Help_HTMLBlock.js';

import { qcPageChartScanFileStatistics_ScanFileSelector } from './qcPageChartScanFileStatistics_ScanFileSelector';

import { qcPageChartScanFileStatistics_OverallStatistics } from './qcPageChartScanFileStatistics_OverallStatistics';
import { qcPageChartScanFileStatistics_MS1_IonCurrent_vs_RT_MZ } from './qcPageChartScanFileStatistics_MS1_IonCurrent_vs_RT_MZ';
import { qcPageChartScanFileStatistics_MS_Each_PerScan_IonCurrent_IonInjectionTime } from './qcPageChartScanFileStatistics_MS_Each_PerScan_IonCurrent_IonInjectionTime';
import { qcPageChartScanFileStatistics_MS2_Count_Per_MS1_Scan } from './qcPageChartScanFileStatistics_MS2_Count_Per_MS1_Scan';

import { qcPageChartScanFileStatistics_MS1_PerScan_BoxPlot_IonCurrent_IonInjectionTime } from './qcPageChartScanFileStatistics_MS1_PerScan_BoxPlot_IonCurrent_IonInjectionTime';
import { qcPageChartScanFileStatistics_MS1_Binned_IonCurrent_MZ_vs_RT } from './qcPageChartScanFileStatistics_MS1_Binned_IonCurrent_MZ_vs_RT';


var _IS_LOADED_YES = "YES";
var _IS_LOADED_NO = "NO";
var _IS_LOADED_LOADING = "LOADING";


/**
 * Constructor 
 */
var QCPageChartScanFileStatistics_ScanFileSelector_Driven_Subsection = function() {
	
	//  Charts and Scan Summary Objects for Subsection that is Scan File Selector Driven
	//     !!!  Excludes qcPageChartScanFileStatistics_ScanFileSelector  !!!
	var _chartObjectsFor_SubSection = [
		
		qcPageChartScanFileStatistics_OverallStatistics,
		qcPageChartScanFileStatistics_MS1_IonCurrent_vs_RT_MZ,
		qcPageChartScanFileStatistics_MS1_Binned_IonCurrent_MZ_vs_RT,

		//  Currently Alex Only Page at URL 'qc_Alex.do':

		//     The following code will detect when the DOM elements are not on the page and skip generating those charts rather than throw Error.

		qcPageChartScanFileStatistics_MS_Each_PerScan_IonCurrent_IonInjectionTime,
		qcPageChartScanFileStatistics_MS2_Count_Per_MS1_Scan,
		qcPageChartScanFileStatistics_MS1_PerScan_BoxPlot_IonCurrent_IonInjectionTime
	];

	///////////
	
	//   Variables for this chart
	
	var _chart_isLoaded = _IS_LOADED_NO;

	/**
	 * Init page Actual - Called from qcPageMain.initActual
	 */
	this.initActual = function( params ) {
		try {
			qcPageChartScanFileStatistics_MS1_PerScan_BoxPlot_IonCurrent_IonInjectionTime.set_qcPageChartScanFileStatistics_ScanFileSelector( qcPageChartScanFileStatistics_ScanFileSelector );
			qcPageChartScanFileStatistics_MS2_Count_Per_MS1_Scan.set_qcPageChartScanFileStatistics_ScanFileSelector( qcPageChartScanFileStatistics_ScanFileSelector );

			for ( const chartObjectFor_SubSection of _chartObjectsFor_SubSection ) {
				chartObjectFor_SubSection.initActual( params );
			}

			qcPageChartScanFileStatistics_ScanFileSelector.setChartsToUpdateOnScanFilePopulateOrChange( _chartObjectsFor_SubSection );

			qcPageChartScanFileStatistics_ScanFileSelector.initActual( params );

		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};

	///////////////////////

	//     Scan File Selector

	/**
	 * Clear data for Scan File Selector
	 */
	this.clearChart = function() {

		_chart_isLoaded = _IS_LOADED_NO;

		qcPageChartScanFileStatistics_ScanFileSelector.clearScanFileSelector();

		for ( const chartObjectFor_SubSection of _chartObjectsFor_SubSection ) {
			chartObjectFor_SubSection.clearChart();
		}
	};

	/**
	 * If not loaded, call ...
	 */
	this.loadChartIfNeeded = function() {
		var objectThis = this;
		
		if ( _chart_isLoaded === _IS_LOADED_NO ) {

			qcPageChartScanFileStatistics_ScanFileSelector.loadScanFileSelectorIfNeeded();

			// Load Scan File List which will trigger display of data for first scan file

		}
	};


};

/**
 * Instance of class
 */

var qcPageChartScanFileStatistics_ScanFileSelector_Driven_Subsection = new QCPageChartScanFileStatistics_ScanFileSelector_Driven_Subsection();

 export { qcPageChartScanFileStatistics_ScanFileSelector_Driven_Subsection }
 