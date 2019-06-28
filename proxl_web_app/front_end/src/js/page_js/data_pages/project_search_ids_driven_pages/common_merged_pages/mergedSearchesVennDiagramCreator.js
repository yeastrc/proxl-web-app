
//  mergedSearchesVennDiagramCreator.js

//JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

import { venn } from 'libs/venn.js';
import {SVGDownloadUtils} from "../common/svgDownloadUtils";

/**
 * Constructor 
 */
var CreateMergedSearchesLinkCountsVennDiagram = function() {

	let retryCountOnMissing_d3_Object = 0;

	///////////////////////////////////////////
	//	This is a copy of colors in CSS classes altered to compensate for fill-opacity: 0.3 in the venn diagram
	//	CSS class names start with "merged-search-search-background-color-" and are in the file global.css

	var vennDiagramColors = [ 
		"#FF9C9C",
		"#9CFF9C",
		"#9C9CFF"
		];

	///////////////////////////////////////////
	//	This function is called on the page to create the Venn diagram
	this.createMergedSearchesLinkCountsVennDiagram = function({ vennDiagramDataToJSON }) {

		if ( ! window.d3 ) {
			retryCountOnMissing_d3_Object++;
			//  d3 object not on page yet so try again in 1 second
			if ( retryCountOnMissing_d3_Object > 1 ) {
				throw Error("window.d3 not exist");
			}
			window.setTimeout( () => {
				try {
					this.createMergedSearchesLinkCountsVennDiagram({ vennDiagramDataToJSON });
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			}, 1000);
		}
		try {
			var venn_diagram_data = vennDiagramDataToJSON;

			var width = 300;
			var height = 150;
			var getVennData = function() {
				var sets = venn_diagram_data.sets;
				var areas = venn_diagram_data.areas;
				return venn.venn(sets, areas);
			};
			var parameters = { 
					colorsFcn : function( index ) {
						return vennDiagramColors[ index ];
					} 
			};
			var diagram = venn.drawD3Diagram( d3.select("#searches_intersection_venn_diagram"), getVennData(), width, height, parameters );

			// 		 Add a border to the circles in the venn diagram
			//  Get the venn diagram
			var $searches_intersection_venn_diagram = $("#searches_intersection_venn_diagram");
			//  Get all the circles in the venn diagram
			var $circle_All = $searches_intersection_venn_diagram.find("circle");
			//  Apply a stroke color
			$circle_All.attr("stroke","#000000");
			//  Apply a stroke width
			$circle_All.attr("stroke-width","1");

			this._addDownloadClickHandlers();

			var $searches_intersection_venn_diagram_outer_container = $("#searches_intersection_venn_diagram_outer_container");
			$searches_intersection_venn_diagram_outer_container.show();

		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	}		
	

	/**
	 * params: { $chart_outer_container_for_download_jq : element with ".chart_outer_container_for_download_jq"
	 * 
	 * If the element $chart_outer_container_for_download_jq was dynamically added, need to run to add tool tips on download links: 
	 *			addToolTips( $chartOuterContainer );
	 */
	this._addDownloadClickHandlers = function( params ) {
		var objectThis = this;
		var $searches_intersection_venn_diagram_outer_container = $("#searches_intersection_venn_diagram_outer_container");
		var $venn_diagram_download_link_jq_All = $searches_intersection_venn_diagram_outer_container.find(".venn_diagram_download_link_jq");
		$venn_diagram_download_link_jq_All.click( function( event ) { 
			objectThis._downloadVennDiagram( { clickedThis : this } ); 
			event.preventDefault();
			event.stopPropagation();
		});
		//  Comment out since no elements with class 'svg_download_outer_block_jq'
//		var $svg_download_outer_block_jq = $venn_diagram_download_link_jq_All.find(".svg_download_outer_block_jq");
//		$svg_download_outer_block_jq.click( function( event ) {  
//			event.preventDefault();
//			event.stopPropagation();
//		});
		
	};

	/**
	 * 
	 */
	this._downloadVennDiagram = function( params ) {
		try {
			var clickedThis = params.clickedThis;

			var $clickedThis = $( clickedThis );
			var download_type = $clickedThis.attr("data-download_type");
			var $searches_intersection_venn_diagram = $("#searches_intersection_venn_diagram");
			var $svgRoot = $searches_intersection_venn_diagram.find("svg");

			SVGDownloadUtils.downloadSvgAsImageType($svgRoot[0], download_type);

		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};



};

var createMergedSearchesLinkCountsVennDiagram = new CreateMergedSearchesLinkCountsVennDiagram();

// window.createMergedSearchesLinkCountsVennDiagram = createMergedSearchesLinkCountsVennDiagram;

export { createMergedSearchesLinkCountsVennDiagram }

