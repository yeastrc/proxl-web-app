
//mergedSearchesVennDiagramCreator.js

//Dependent on the variable "searchesVennDiagramData" on the page.


//JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";



/**
 * Constructor 
 */
var CreateMergedSearchesLinkCountsVennDiagram = function() {

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
	this.createMergedSearchesLinkCountsVennDiagram = function() {
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

			// 		 Add a border to the circles in the venn diagram
			//  Get the venn diagram
			var $searches_intersection_venn_diagram = $("#searches_intersection_venn_diagram");
			//  Get all the circles in the venn diagram
			var $circle_All = $searches_intersection_venn_diagram.find("circle");
			//  Apply a stroke color
			$circle_All.attr("stroke","#000000");
			//  Apply a stroke width
			$circle_All.attr("stroke-width","1");

			this.addDownloadClickHandlers()
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
	this.addDownloadClickHandlers = function( params ) {
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

			var getSVGContentsAsStringResult = this._getSVGContentsAsString( $searches_intersection_venn_diagram );
			
			if ( getSVGContentsAsStringResult.errorException ) {
				throw errorException;
			}
			
			var fullSVG_String = getSVGContentsAsStringResult.fullSVG_String;
			
			var form = document.createElement( "form" );
			$( form ).hide();
			form.setAttribute( "method", "post" );
			form.setAttribute( "action", contextPathJSVar + "/convertAndDownloadSVG.do" );

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
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
		
	};
	

	/**
	 * 
	 */
	this._getSVGContentsAsString = function ( $searches_intersection_venn_diagram ) {
		try {
			var $svgRoot = $searches_intersection_venn_diagram.find("svg");
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

var createMergedSearchesLinkCountsVennDiagram = new CreateMergedSearchesLinkCountsVennDiagram();
