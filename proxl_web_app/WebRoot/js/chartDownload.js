/**
 * chartDownload.js
 * 
 * Javascript for the chartDownloadHTMLBlock.jsp page fragment
 * 
 * page variable chartDownload
 * 
 * !!!!   Page Requirements:
 * 
 * The element containing the include of chartDownloadHTMLBlock.jsp 
 *   has to have class "chart_outer_container_for_download_jq".
 * 
 * The 
 * 
 */

//  JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

///////
$(document).ready(function() { 
	chartDownload.init();

} ); // end $(document).ready(function() 


/**
 * Constructor 
 */
var ChartDownload = function() {

	/**
	 * Init page on load 
	 */
	this.init = function() {
		var objectThis = this;
	};

	/**
	 * params: { $chart_outer_container_for_download_jq : element with ".chart_outer_container_for_download_jq"
	 * 
	 * If the element $chart_outer_container_for_download_jq was dynamically added, need to run to add tool tips on download links: 
	 *			addToolTips( $chartOuterContainer );
	 */
	this.addDownloadClickHandlers = function( params ) {
		var objectThis = this;
		var $chart_outer_container_for_download_jq = params.$chart_outer_container_for_download_jq;
		var $chart_download_link_jq_All = $chart_outer_container_for_download_jq.find(".chart_download_link_jq");
		$chart_download_link_jq_All.click( function( event ) { 
			objectThis._downloadChart( { clickedThis : this } ); 
			event.preventDefault();
		});
	};

	/**
	 * 
	 */
	this._downloadChart = function( params ) {
		try {
			var clickedThis = params.clickedThis;

			var $clickedThis = $( clickedThis );
			var download_type = $clickedThis.attr("data-download_type");
			var $chart_outer_container_for_download_jq = $clickedThis.closest(".chart_outer_container_for_download_jq");

			var getSVGContentsAsStringResult = this._getSVGContentsAsString( $chart_outer_container_for_download_jq );
			
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

			var getSVGContentsAsStringResult = getSVGContentsAsString( $chart_outer_container_for_download_jq );
			var svgString = getSVGContentsAsStringResult.fullSVG_String;
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
		
	};
	

	/**
	 * 
	 */
	this._getSVGContentsAsString = function ( $chart_outer_container_for_download_jq ) {
		try {
			var $svgRoot = $chart_outer_container_for_download_jq.find("svg");
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

var chartDownload = new ChartDownload();
