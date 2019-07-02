
"use strict";

let PDFDocument = require('pdfkit').default;
let blobStream = require('blob-stream');

import { saveAs } from 'file-saver';
import { saveSvgAsPng } from 'save-svg-as-png'
import * as SVGtoPDF from 'svg-to-pdfkit';
import { downloadStringAsFile} from "./download-string-as-file";

// 3 lines here to make svgtopdf work
import fs from 'fs'
import Helvetica from '!!raw-loader!pdfkit/js/data/Helvetica.afm'
fs.writeFileSync('data/Helvetica.afm', Helvetica)

export class SVGDownloadUtils {

    /**
     * Download the given SVG string as an image
     *
     * @param svgString The <svg>...</svg> XML as a string
     * @param type The type, must be one of svg, png, pdf
     */
    static downloadSvgAsImageType( svgNode, type ) {
        console.log('called downloadSvgAsImageType');

        try {

            let filename = 'proxl-image.' + type;

            if( type === 'png' ) {

                let options = {
                    backgroundColor:'#ffffff',
                    encoderType:'image/'+type,
                    scale:3,            // 3x size
                    encoderOptions:9,   // quality
                }

                saveSvgAsPng(svgNode, filename, options);

            } else if( type === 'pdf' ) {

                let doc = new PDFDocument({compress: false});
                let stream = doc.pipe(blobStream());

                SVGtoPDF(doc, svgNode, 0, 0, {useCSS:true});
                doc.end();

                stream.on('finish', function() {
                    let blob = stream.toBlob('application/pdf');
                    saveAs(blob, filename);
                });

            } else if( type === 'svg' ) {
                downloadStringAsFile( filename, 'image/svg', SVGDownloadUtils.getSVGStringForSvgNode(svgNode) );
            }


        } catch( e ) {
            reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
            throw e;
        }
    }

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