
"use strict";

import * as d3 from "d3";
import {SVGDownloadUtils} from "../common/svgDownloadUtils.js";
import {LinkablePositionUtils} from "./linkable-positions-utils";

export class DensityPlot {

    static loadAndShowDensityPlot( { linkablePositionData, divToUpdateSelector, visibleProteinsMap, onlyShortest, alignments, structure, renderedLinks} ) {

        if( !visibleProteinsMap ) { return; }

        const renderedDistanceArray = LinkablePositionUtils.getRenderedDistanceArray( renderedLinks );

        const pdbDistanceArray = LinkablePositionUtils.getDistanceArrayFromLinkablePositions({
            data : linkablePositionData,
            visibleProteinsMap,
            onlyShortest,
            alignments,
            structure
        });

        DensityPlot.staticShowDensityPlot( { divToUpdateSelector, pdbDistanceArray, renderedDistanceArray } );

    }



    static staticShowDensityPlot( { divToUpdateSelector, pdbDistanceArray, renderedDistanceArray } ) {

        $( divToUpdateSelector ).empty();

        const maxDistance = DensityPlot.getMaximumDistance( pdbDistanceArray, renderedDistanceArray );
        console.log( "max distance: " + maxDistance );

        const margin = {top: 15, right: 10, bottom: 30, left: 50},
            width = 500 - margin.left - margin.right,
            height = 400 - margin.top - margin.bottom;


        const svg = d3.select( divToUpdateSelector )
            .append("svg")
            .attr("width", width + margin.left + margin.right)
            .attr("height", height + margin.top + margin.bottom + 10)
            .attr("id", "density-plot-svg" )
            .append("g")
            .attr("transform",
                "translate(" + margin.left + "," + margin.top + ")");

        // add the x Axis
        const x = d3.scaleLinear()
            .domain([0, maxDistance + 30 ])
            .range([0, width]);

        // Compute kernel density estimation
        const kde = DensityPlot.kernelDensityEstimator( DensityPlot.kernelEpanechnikov(7), x.ticks(40) );

        const pdbDensity =  kde( pdbDistanceArray );
        const renderedLinkDensity =  kde( renderedDistanceArray );

        const maximumHeight = DensityPlot.getMaximumHeight( pdbDensity, renderedLinkDensity );

        svg.append("g")
            .attr("transform", "translate(0," + height + ")")
            .call(d3.axisBottom(x));

        // Add the text label for the x axis
        svg.append("text")
            .attr("transform", "translate(" + (width / 2) + " ," + (height + margin.bottom) + ")")
            .style("text-anchor", "middle")
            .attr("y", 0 )
            .attr("font-size", 10 )
            .text("Length (Ang.)");

        // add the y Axis
        const y = d3.scaleLinear()
            .range([height, 0])
            .domain([0, maximumHeight ]);

        svg.append("g")
            .call(d3.axisLeft(y));

        // Add the text label for the Y axis
        svg.append("text")
            .attr("transform", "rotate(-90)")
            .attr("y", 0 - margin.left - 2)
            .attr("x",0 - (height / 2))
            .attr("dy", "1em")
            .attr("font-size", 10 )
            .style("text-anchor", "middle")
            .text("Probability density");

        // Plot the area
        svg.append("path")
            .attr("class", "mypath")
            .datum( pdbDensity )
            .attr("fill", "#ffffff")
            .attr("opacity", "1")
            .attr("fill-opacity", 0 )
            .attr("stroke", "#000")
            .attr("stroke-width", 2)
            .attr("stroke-linejoin", "round")
            .attr("d",  d3.line()
                .curve(d3.curveBasis)
                .x(function(d) { return x(d[0]); })
                .y(function(d) { return y(d[1]); })
            );

        // Plot the area
        svg.append("path")
            .attr("class", "mypath")
            .datum( renderedLinkDensity )
            .attr("fill", "#ffffff")
            .attr("opacity", "1")
            .attr("fill-opacity", 0 )
            .attr("stroke", "#A55353")
            .attr("stroke-width", 2)
            .attr("stroke-linejoin", "round")
            .attr("d",  d3.line()
                .curve(d3.curveBasis)
                .x(function(d) { return x(d[0]); })
                .y(function(d) { return y(d[1]); })
            );

        // add a legend
        let legend = svg.append("g")
            .attr('class', 'legend');

        legend.append("text")
            .text("Possible UDRs" )
            .attr("font-size", 12 )
            .attr("fill", "#000000")
            .attr("opacity", "0.9")
            .attr("x", 290 )
            .attr("y", 10 );

        legend.append("text")
            .text("Observed UDRs" )
            .attr("font-size", 12 )
            .attr("fill", "#A55353")
            .attr("x", 290 )
            .attr("y", 30 );

        DensityPlot.addDensityPlotDownloadOption();

    }


    static addDensityPlotDownloadOption() {

        const $parentSpan = $( "#svg-download" );
        $parentSpan.empty();

        let html = "<a id=\"download_as_link\"\n" +
            "data-tooltip=\"Download current image as file.\" style=\"font-size:10pt;white-space:nowrap;\" \n" +
            "href=\"javascript:\" class=\"tool_tip_attached_jq download-svg\">[Download Image]</a>\n" +

            "<span id=\"svg-download-options\" style=\"font-size:10pt;\">\n" +
            "Choose file format:\n" +
            "<a data-tooltip=\"Download as a JPEG image file.\" id=\"svg-download-jpeg\" class=\"svg-download-option tool_tip_attached_jq\" href=\"javascript:\" style=\"margin-top:5px;\">JPEG</a>\n" +
            "<a data-tooltip=\"Download as PDF file suitable for use in Adobe Illustrator or printing.\" id=\"svg-download-pdf\" class=\"svg-download-option tool_tip_attached_jq\" href=\"javascript:\">PDF</a>\n" +
            "<a data-tooltip=\"Download as PNG image file.\" id=\"svg-download-png\" class=\"svg-download-option tool_tip_attached_jq\" href=\"javascript:\">PNG</a>\n" +
            "<a data-tooltip=\"Download as scalable vector graphics file suitable for use in Inkscape or other compatible software.\" id=\"svg-download-svg\" class=\"svg-download-option tool_tip_attached_jq\" href=\"javascript:\">SVG</a>\n" +
            "</span>\n";

        const $html = $( html );

        $parentSpan.append( $html );

        $html.find( "#svg-download-jpeg" ).click( function() { SVGDownloadUtils.downloadSvgAsImageType( $("#density-plot-svg")[0], 'jpeg' ); });
        $html.find( "#svg-download-png" ).click( function() { SVGDownloadUtils.downloadSvgAsImageType( $("#density-plot-svg")[0], 'png' ); });
        $html.find( "#svg-download-pdf" ).click( function() { SVGDownloadUtils.downloadSvgAsImageType( $("#density-plot-svg")[0], 'pdf' ); });
        $html.find( "#svg-download-svg" ).click( function() { SVGDownloadUtils.downloadSvgAsImageType( $("#density-plot-svg")[0], 'svg' ); });

    }

    static getMaximumHeight( pdbDensity, renderedDensity ) {

        let maxPdbHeight = DensityPlot.getMaximumHeightForDensity( pdbDensity );
        let maxRenderedHeight = DensityPlot.getMaximumHeightForDensity( renderedDensity );

        console.log( "max rendered height: " + maxRenderedHeight );

        if( maxPdbHeight === undefined && maxRenderedHeight === undefined ) {
            return undefined;
        }

        if( maxPdbHeight === undefined ) {
            return maxRenderedHeight;
        }

        if( maxRenderedHeight === undefined ) {
            return maxPdbHeight;
        }

        return maxPdbHeight > maxRenderedHeight ? maxPdbHeight : maxRenderedHeight;
    }

    static getMaximumHeightForDensity( density ) {

        if( density === undefined ) { return undefined; }

        let maxHeight = 0;

        for (let i = 0; i < density.length; i++) {
            const height = density[ i ][ 1 ];
            if( height > maxHeight ) { maxHeight = height; }
        }

        return maxHeight;
    }

    static getMaximumDistance( pdbDistanceArray, renderedDistanceArray ) {

        let maxPdbDistance = undefined;

        if( pdbDistanceArray && pdbDistanceArray.length > 0 ) {
            maxPdbDistance = pdbDistanceArray.reduce(function(a, b) {
                return Math.max(a, b);
            });
        }

        let maxRenderedDistance = undefined;

        if( renderedDistanceArray && renderedDistanceArray.length > 0 ) {
            maxRenderedDistance = renderedDistanceArray.reduce(function(a, b) {
                return Math.max(a, b);
            });
        }

        if( maxPdbDistance === undefined && maxRenderedDistance === undefined ) {
            return undefined;
        }

        if( maxPdbDistance === undefined ) {
            return maxRenderedDistance;
        }

        if( maxRenderedDistance === undefined ) {
            return maxPdbDistance;
        }

        return maxPdbDistance > maxRenderedDistance ? maxPdbDistance : maxRenderedDistance;
    }


    // Function to compute density
    // taken from https://www.d3-graph-gallery.com/graph/density_basic.html
    static kernelDensityEstimator(kernel, X) {
        return function(V) {
            return X.map(function(x) {
                return [x, d3.mean(V, function(v) { return kernel(x - v); })];
            });
        };
    }
    static kernelEpanechnikov(k) {
        return function(v) {
            return Math.abs(v /= k) <= 1 ? 0.75 * (1 - v * v) / k : 0;
        };
    }

}