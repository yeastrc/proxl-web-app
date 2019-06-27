
"use strict";

export class SVGDownloadUtils {

    /**
     * Download the given SVG string as an image
     *
     * @param svgString The <svg>...</svg> XML as a string
     * @param type The type, must be one of svg, jpeg, png, pdf
     */
    static downloadSvgAsImageType( svgNode, type ) {
        try {

            const svgString = SVGDownloadUtils.getSVGStringForSvgNode( svgNode );
            SVGDownloadUtils.convertAndDownloadSVG( svgString, type );

        } catch( e ) {
            reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
            throw e;
        }
    }

    /**
     * Convert and download the conversion of the supplied SVG to the supplied type
     * As of this writing, the type must be "pdf", "png", "jpeg", or "svg"
     */
    static convertAndDownloadSVG( svgString, typeString ) {
        var form = document.createElement( "form" );
        $( form ).hide();
        form.setAttribute( "method", "post" );
        form.setAttribute( "action", "convertAndDownloadSVG.do" );
        //form.setAttribute( "target", "_blank" );
        const svgStringField = document.createElement( "input" );
        svgStringField.setAttribute("name", "svgString");
        svgStringField.setAttribute("value", svgString);
        const fileTypeField = document.createElement( "input" );
        fileTypeField.setAttribute("name", "fileType");
        fileTypeField.setAttribute("value", typeString);
        form.appendChild( svgStringField );
        form.appendChild( fileTypeField );

		const browserURL = window.location.href;
		const browserURLField = document.createElement( "input" );
		browserURLField.setAttribute("name", "browserURL");
		browserURLField.setAttribute("value", browserURL);
		form.appendChild( browserURLField );

        document.body.appendChild(form);    // Not entirely sure if this is necessary

        form.submit();
        document.body.removeChild( form );
    };


    /**
     * Get string representation of a SVG element as valid XML. Mostly taken from
     *
     * @param svgNode
     * @returns {string}
     */
    static getSVGStringForSvgNode( svgNode ) {

        const svgContents = svgNode.innerHTML;
        let fullSVG_String = "<?xml version=\"1.0\" standalone=\"no\"?><!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">";
        fullSVG_String += "<svg id=\"svg\" ";
        fullSVG_String += "width=\"" + svgNode.getAttribute( "width" ) + "\" ";
        fullSVG_String += "height=\"" + svgNode.getAttribute( "height" ) + "\" ";
        fullSVG_String += "xmlns=\"http://www.w3.org/2000/svg\">" + svgContents + "</svg>";
        // fix the URL that google charts is putting into the SVG. Breaks parsing.
        fullSVG_String = fullSVG_String.replace( /url\(.+\#_ABSTRACT_RENDERER_ID_(\d+)\)/g, "url(#_ABSTRACT_RENDERER_ID_$1)" );

        return fullSVG_String;
    }


}