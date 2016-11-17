
//   mergedSearchesVennDiagramCreator.js

//  Dependent on the variable "searchesVennDiagramData" on the page.

//////////////////////////////////
// JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

///////////////////////////////////////////
//  This is a copy of colors in CSS classes altered to compensate for fill-opacity: 0.3 in the venn diagram
//  CSS class names start with "merged-search-search-background-color-" and are in the file global.css

var vennDiagramColors = [ 
				"#FF9C9C",
				"#9CFF9C",
				"#9C9CFF"
				];

///////////////////////////////////////////
//  This function is called on the page to create the Venn diagram
function createMergedSearchesLinkCountsVennDiagram(  ) {
	try {
		if ( typeof Modernizr === 'undefined' || ! Modernizr.svg ) {
			console.log( "SVG not supported." );
			return;
		}
		var $venn_diagram_data_JSON = $("#venn_diagram_data_JSON");
		if ( $venn_diagram_data_JSON.length === 0 ) {
			throw Error( 'element with id "venn_diagram_data_JSON" not found ' );
		}
		var venn_diagram_data_JSONString = $venn_diagram_data_JSON.text();
		if ( venn_diagram_data_JSONString === undefined ) {
			throw Error( 'element with id "venn_diagram_data_JSON" returned .text() undefined  ' );
		}
		if ( venn_diagram_data_JSONString === null ) {
			throw Error( 'element with id "venn_diagram_data_JSON" returned .text() null ' );
		}
		if ( venn_diagram_data_JSONString === "" ) {
			throw Error( 'element with id "venn_diagram_data_JSON" returned .text() empty string "" ' );
		}
		var venn_diagram_data = null;
		try {
			venn_diagram_data = JSON.parse( venn_diagram_data_JSONString );
		} catch( e ) {
			throw Error( 'element with id "venn_diagram_data_JSON" does not contain valid JSON, failed to parse as JSON ' );
		}
		
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
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
}		
