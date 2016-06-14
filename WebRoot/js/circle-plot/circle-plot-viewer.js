"use strict";

///////////////////////////////////////////

var circlePlotViewer = function() {	
	this.initialize();
};


circlePlotViewer.prototype.initialize  = function(  ) {
	
};

circlePlotViewer.prototype.CONSTANTS = { 

		_DEFAULT_VIEWPORT_HEIGHT : 700,
		_DEFAULT_VIEWPORT_WIDTH : 700,
		
		_GAP_BETWEEN_BARS : 2,			// gap between proteins bars in degrees
		_HEIGHT_OF_PROTEIN_BARS : 40,	// height of protein bars in pixels
		_GAP_BETWEEN_BARS_AND_LINKS : 2, // gap in pixels between protein bars and start of drawn links
};

/**
 * Draw the diagram
 */
circlePlotViewer.prototype.draw  = function(  ) {

	// our SVG object onto which we're drawing
	var svgRootSnapSVGObject = this.inializeSVGObject();
	
	// if this object is undefined or empty, we couldn't draw so do nothing
	if( !svgRootSnapSVGObject ) {
		return;
	}
	
	this.setViewerDimensions( svgRootSnapSVGObject );
	
	console.log( this.centerX );
	
	this.drawProteinBars( svgRootSnapSVGObject );

	//var bigCircle = svgRootSnapSVGObject.path( this.getCurvedBarPath( 400, 400, 400, 40, 0, 178 ) );
	
	
	
	
};

/**
 * Given an index among selectedProteins and a position in that protein, find the X,Y coordinate for where to
 * terminate (start or end) a crosslink for that protein and position
 * 
 * @param proteinIndex
 * @param position
 * @returns
 */
circlePlotViewer.prototype.getCrosslinkTerminus = function( proteinIndex, position ) {
	var selectedProteins = getAllSelectedProteins();
	
	var totalGapDegrees = selectedProteins.length * this.CONSTANTS._GAP_BETWEEN_BARS;
	var workingDegrees = 360 - totalGapDegrees;
	
	var totalProteinLength = 0;
	for( var i = 0; i < selectedProteins.length; i++ ) {
		totalProteinLength += _proteinLengths[ selectedProteins[ i ] ];
	}
	
	// number of degrees per protein residue
	var degreesPerResidue = workingDegrees / totalProteinLength;
	
	var currentStartDegrees = 0;
	for( var i = 0; i < selectedProteins.length; i++ ) {
		
		
		if( i == proteinIndex ) {

			var degrees = currentStartDegrees + (position * degreesPerResidue - (0.5 * degreesPerResidue ) );
			
			return this.polarToCartesian( this.centerX,
										  this.centerY,
										  this.radius - this.CONSTANTS._HEIGHT_OF_PROTEIN_BARS - _GAP_BETWEEN_BARS_AND_LINKS,
										  degrees
										 );
			 

		} else {
			var totalLengthProportion = _proteinLengths[ selectedProteins[ i ] ] / totalProteinLength;
			var rotationDegrees = workingDegrees * totalLengthProportion;
			currentStartDegrees += this.CONSTANTS._GAP_BETWEEN_BARS + rotationDegrees;
		}
		
	}
	
};

/**
 * Draw all the self-crosslinks
 * 
 * @param svgRootSnapSVGObject
 */
circlePlotViewer.prototype.drawSelfCrosslinks = function( svgRootSnapSVGObject ) {
	
};

/**
 * Draw all the protein bars, and associated items (such as labels, linkable positions, and scale bars)
 * @param svgRootSnapSVGObject
 */
circlePlotViewer.prototype.drawProteinBars = function( svgRootSnapSVGObject ) {
	
	var selectedProteins = getAllSelectedProteins();
	var totalGapDegrees = selectedProteins.length * this.CONSTANTS._GAP_BETWEEN_BARS;
	var workingDegrees = 360 - totalGapDegrees;
	
	var totalProteinLength = 0;
	for( var i = 0; i < selectedProteins.length; i++ ) {
		totalProteinLength += _proteinLengths[ selectedProteins[ i ] ];
	}
	
	var currentStartDegrees = 0;
	for( var i = 0; i < selectedProteins.length; i++ ) {
		var totalLengthProportion = _proteinLengths[ selectedProteins[ i ] ] / totalProteinLength;
		var rotationDegrees = workingDegrees * totalLengthProportion;
		
		this.drawProteinBar( svgRootSnapSVGObject, selectedProteins[ i ], i, currentStartDegrees, rotationDegrees );
		
		currentStartDegrees += this.CONSTANTS._GAP_BETWEEN_BARS + rotationDegrees;
		
	}
	
};



/**
 * Draw the bar for a given protein
 * @param svgRootSnapSVGObject
 * @param proteinId
 * @param startDegrees
 * @param rotationDegrees
 */
circlePlotViewer.prototype.drawProteinBar = function( svgRootSnapSVGObject, proteinId, index, startDegrees, rotationDegrees ) {
	
	var path = this.getCurvedBarPath( this.centerX, 
									  this.centerY,
									  this.radius,
									  this.CONSTANTS._HEIGHT_OF_PROTEIN_BARS,
									  startDegrees,
									  startDegrees + rotationDegrees
									 );
	
	console.log( path );
	var svgPath = svgRootSnapSVGObject.path( path );
	
	svgPath.attr( {
		fill:this.getColorForIndex( index ),
	})
	
	//this.drawProteinBarLabel( svgRootSnapSVGObject, svgPath, proteinId );
	this.drawProteinBarLabel( svgRootSnapSVGObject, proteinId, index, startDegrees, rotationDegrees );
}

/**
 * Draw the label for a given protein bar
 * @param svgRootSnapSVGObject
 * @param svgPath
 * @param proteinId
 */
circlePlotViewer.prototype.drawProteinBarLabel = function( svgRootSnapSVGObject, proteinId, index, startDegrees, rotationDegrees ) {
	
	var path = this.getCurvedPathForLabel( this.centerX, 
									  this.centerY,
									  this.radius,
									  this.CONSTANTS._HEIGHT_OF_PROTEIN_BARS,
									  startDegrees,
									  startDegrees + rotationDegrees
									 );
	
	var svgPath = svgRootSnapSVGObject.path( path );
	
	svgPath.attr( {
		fill:"none",
		stroke:"none",
	})
	
	var proteinLabelTextSnapSVGObject = svgRootSnapSVGObject.text( 0, 0, _proteinNames[ proteinId ] );
	
	proteinLabelTextSnapSVGObject.attr({
		fill:"#ffffff",
		"font-size":"14px",
		textpath: svgPath,
		dy:"-15px",
		dx:"5px",
	});
}

/*
circlePlotViewer.prototype.drawProteinBarLabel = function( svgRootSnapSVGObject, svgPath, proteinId ) {
	
	var proteinLabelTextSnapSVGObject = svgRootSnapSVGObject.text( 0, 0, _proteinNames[ proteinId ] );
	
	proteinLabelTextSnapSVGObject.attr({
		fill:"#ffffff",
		"font-size":"14px",
		textpath: svgPath,
		dy:"-15px",
		dx:"5px",
	});
	
};
*/

circlePlotViewer.prototype.inializeSVGObject = function() {

	var svgRootSnapSVGObject;  // the Snap SVG object for the SVG

	
	//  First remove and add the "<svg>" element from the template
	
	var $svg_image_template_div = $("#svg_image_template_div");
	var svg_image_template_div__html = $svg_image_template_div.html();
	var $svg_image_inner_container_div = $("#svg_image_inner_container_div");
	
	//  select the <svg> element
	var $merged_image_svg_jq__BeforeEmpty = $svg_image_inner_container_div.find("svg.merged_image_svg_jq");
	
	$svg_image_inner_container_div.empty();  //  remove the <svg> element.  jQuery will also properly remove all the handlers attached to those elements
	$svg_image_inner_container_div.html( svg_image_template_div__html );  // insert new <svg> as copied from the template
	
	//  select the <svg> element
	var $merged_image_svg_jq = $svg_image_inner_container_div.find("svg.merged_image_svg_jq");
	
	//  get the <svg> HTML element to pass to Snap
	var merged_image_svg_element = $merged_image_svg_jq[0];
	
	var selectedProteins = getAllSelectedProteins();
	
	if ( selectedProteins === undefined || selectedProteins.length < 1 ) { 
		
		//  No proteins so remove CSS height attribute
		$svg_image_inner_container_div.css( { height : '' } );	

		return;  //  EARLY EXIT of function if no proteins to display 
	}
		
	svgRootSnapSVGObject = Snap( merged_image_svg_element );  // pass in HTML element
		
	if ( _proteins.length < 1 ) {
		
		//  No Proteins Data loaded
		var newHeightContainingDivEmpty = 4;
		$svg_image_inner_container_div.css( { height : newHeightContainingDivEmpty } );
		return;
	}
	
	return svgRootSnapSVGObject;
	
};



circlePlotViewer.prototype.setViewerDimensions = function(svgRootSnapSVGObject) {
		
	svgRootSnapSVGObject.attr( { width: this.CONSTANTS._DEFAULT_VIEWPORT_WIDTH } );
	svgRootSnapSVGObject.attr( { height: this.CONSTANTS._DEFAULT_VIEWPORT_HEIGHT } );
	
	this.centerX = this.CONSTANTS._DEFAULT_VIEWPORT_WIDTH / 2;
	this.centerY = this.centerX;
	this.radius = this.centerX;

};

/**
 * Find the coordinates of the point corresponding to rotating the given number of degrees
 * (0 being straight up) along a circle of the given radius and center. This method was borrowed
 * from StackOverflow at:
 * http://stackoverflow.com/questions/5736398/how-to-calculate-the-svg-path-for-an-arc-of-a-circle
 * 
 * @param centerX
 * @param centerY
 * @param radius
 * @param angleInDegrees
 * @returns {___anonymous3091_3208}
 */
circlePlotViewer.prototype.polarToCartesian = function(centerX, centerY, radius, angleInDegrees) {
	
	var angleInRadians = (angleInDegrees-90) * Math.PI / 180.0;

	  return {
	    x: centerX + (radius * Math.cos(angleInRadians)),
	    y: centerY + (radius * Math.sin(angleInRadians))
	  };
};


circlePlotViewer.prototype.getCurvedPathForLabel = function(centerX, centerY, outerRadius, barHeight, startAngle, endAngle){
	
	var innerRadius = outerRadius - barHeight;
	
    var outerStart = this.polarToCartesian(centerX, centerY, outerRadius, endAngle);
    var outerEnd = this.polarToCartesian(centerX, centerY, outerRadius, startAngle);

    var innerStart = this.polarToCartesian(centerX, centerY, innerRadius, endAngle);
    var innerEnd = this.polarToCartesian(centerX, centerY, innerRadius, startAngle);
    
    var arcSweep = endAngle - startAngle <= 180 ? "0" : "1";
    
    var d = [
             "M", innerEnd.x, innerEnd.y, 
             "A", innerRadius, innerRadius, 0, arcSweep, 1, innerStart.x, innerStart.y
         ].join(" ");
    
    return d + "Z";       
}


/**
 * Get the string defining the SVG path for a curved protein bar
 * 
 * @param centerX The center of the diagram
 * @param centerY The center of the diagram
 * @param outerRadius The outer radius of the protein bar
 * @param barHeight The height of the protein bar
 * @param startAngle The start angle in degress (0 is straight up)
 * @param endAngle The end angle in degrees
 * @returns {String}
 */
circlePlotViewer.prototype.getCurvedBarPath = function(centerX, centerY, outerRadius, barHeight, startAngle, endAngle){
	
	var innerRadius = outerRadius - barHeight;
	
    var outerStart = this.polarToCartesian(centerX, centerY, outerRadius, endAngle);
    var outerEnd = this.polarToCartesian(centerX, centerY, outerRadius, startAngle);

    var innerStart = this.polarToCartesian(centerX, centerY, innerRadius, endAngle);
    var innerEnd = this.polarToCartesian(centerX, centerY, innerRadius, startAngle);
    
    var arcSweep = endAngle - startAngle <= 180 ? "0" : "1";
    
    var d = [
             "M", innerEnd.x, innerEnd.y, 
             "A", innerRadius, innerRadius, 0, arcSweep, 1, innerStart.x, innerStart.y,
             "L", outerStart.x, outerStart.y,
             "A", outerRadius, outerRadius, 0, arcSweep, 0, outerEnd.x, outerEnd.y,
             "L", innerEnd.x, innerEnd.y
         ].join(" ");
    
    /*
    var d = [
        "M", outerStart.x, outerStart.y, 
        "A", outerRadius, outerRadius, 0, arcSweep, 0, outerEnd.x, outerEnd.y,
        "L", innerEnd.x, innerEnd.y,
        "A", innerRadius, innerRadius, 0, arcSweep, 1, innerStart.x, innerStart.y,
        "L", outerStart.x, outerStart.y
    ].join(" ");
	*/
    
    return d + "Z";       
}

/**
 * Get the color for the given index, given the total number of proteins
 * @param index
 * @returns
 */
circlePlotViewer.prototype.getColorForIndex = function( index ) {
	var number = getAllSelectedProteins().length;
		
	var hueDivider = 360 / number;
	var saturation = 0.39;
	var brightness = 0.60;

	var hue =  (360 - (hueDivider * index ) - 1) / 360;
	
	console.log( [hue, saturation, brightness ] );
	
	var color = Snap.hsb2rgb( hue, saturation, brightness );
	console.log( color );
	return color.hex;
}


