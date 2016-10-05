"use strict";

///////////////////////////////////////////

var circlePlotViewer = function() {	
	this.initialize();
};


circlePlotViewer.prototype.initialize  = function(  ) {

};

circlePlotViewer.prototype.CONSTANTS = { 

		_DEFAULT_DIAMETER : 800,
		
		_GAP_BETWEEN_BARS : 2,			// gap between proteins bars in degrees
		_HEIGHT_OF_PROTEIN_BARS : 40,	// height of protein bars in pixels
		_GAP_BETWEEN_BARS_AND_LINKS : 2, // gap in pixels between protein bars and start of drawn links
		_MINIMUM_CROSSLINK_LOOP_HEIGHT: 60, // minimum height of crosslink loops in pixels
		_MONOLINK_HEIGHT:15,			// height of monolinks in pixels
		
		_LINKABLE_POSITION_COLOR:"#ffffff",	// color to use for linkable positions
		_LINKABLE_POSITION_OPACITY:0.5,	// opacity to use for linkable positions
		
		_SCALE_BAR_HEIGHT:10,		// height of scale bar in pixels
		_SCALE_BAR_FONT_HEIGHT:12,	// height of scale bar font in pixles
		_GAP_BETWEEN_SCALE_BAR_AND_TEXT:5,	// gap (in pixels) between scale bar and scale bar label
		_GAP_BETWEEN_SCALE_BAR_AND_PROTEIN_BAR:5,	// gap (in pixels) between scale bar and protein bar
		
		_LEGEND_WIDTH:200,			// width to allocate for the legend when coloring by search
		_LEGEND_GAP:10,				// space between legend and drawing
		_LEGEND_HEADER_HEIGHT:16,	// height of the font of the legend header
		_LEGEND_FONT_HEIGHT:14,		// height of the font of the legend text
		_LEGEND_INDENT:5,			// indentation for legend items under header
		_LEGEND_RADIUS:7,			// pixels of radius of legend indicator (circle)
		
		// colors to use when coloring by search
		_SEARCH_COLORS: {
			"1" : "#995d5d",	// redish
			"2" : "#5d6499",	// blueish
			"3" : "#b5af00",	// yellowish
			
			"12" : "#cd83c7",	// purpleish
			"13" : "#35bec0",	// orangeish
			"23" : "#5d9960",	// greenish
			
			"123" : "#3a3a3a",	// grey
		},
		
		_FEATURE_ANNOTATION_HEIGHT:15,	// height of the feature annotation bar
		_DISOPRED_COLOR: "#3a3a3a",			// color to use for disopred annotations
		_PSIPRED_ALPHA_COLOR: "#5d9960",	// color to use for alpha helices in psipred annotations
		_PSIPRED_BETA_COLOR: "#5d6499",		// color to use for beta sheets in psipred annotations
			
			
			
		_PROTEIN_BAR_OVERLAY_RECTANGLE_LABEL_CLASS: "protein-bar-class",	// css class for protein bars
		
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
	
	_GLOBAL_SNAP_SVG_OBJECT = svgRootSnapSVGObject;
	
	this.setViewerDimensions( svgRootSnapSVGObject );
	
	this.drawProteinBars( svgRootSnapSVGObject, false );
	
	
	if( this.isFeatureAnnotationShown() ) {
		this.drawFeatureAnnotationData( svgRootSnapSVGObject );
	}
		
	if ( $( "input#show-scalebar" ).is( ':checked' ) ) {
		this.drawScaleBars( svgRootSnapSVGObject );
	}
	
	if ( $( "input#show-linkable-positions" ).is( ':checked' ) ) {
		this.drawLinkablePositions( svgRootSnapSVGObject );
	}
	
	if ( $( "input#show-self-crosslinks" ).is( ':checked' ) ) {
		this.drawSelfCrosslinks( svgRootSnapSVGObject );
	}

	if ( $( "input#show-crosslinks" ).is( ':checked' ) ) {
		this.drawCrosslinks( svgRootSnapSVGObject );
	}
	
	if ( $( "input#show-looplinks" ).is( ':checked' ) ) {
		this.drawLooplinks( svgRootSnapSVGObject );
	}
	
	if ( $( "input#show-monolinks" ).is( ':checked' ) ) {
		this.drawMonolinks( svgRootSnapSVGObject );
	}
	
	if ( $( "input#show-tryptic-cleavage-positions" ).is( ':checked' ) ) {
		this.drawTrypticPositions( svgRootSnapSVGObject );
	}
	
	
	// draw the transparent protein bars over the protein bars to capture
	// mouse events such as mouseover and click
	this.drawProteinBars( svgRootSnapSVGObject, true );

	
	
	if( this.isColorBySearch() ) {
		this.drawLegend( svgRootSnapSVGObject );
	}
	
};

/**
 * Draw the selected annotation data on the image
 * @param svgRootSnapSVGObject
 */
circlePlotViewer.prototype.drawFeatureAnnotationData = function( svgRootSnapSVGObject ) {
	var selectedProteins = getAllSelectedProteins();
	var annoType = $("#annotation_type").val();	
	var radii = this.getFeatureAnnotationRadii();		// the radii to use for drawing the feature annotation curved bars
	
	for( var i = 0; i < selectedProteins.length; i++ ) {
		
		var segments;
		if( annoType === SELECT_ELEMENT_ANNOTATION_TYPE_DISOPRED3 ) {
		
			segments = getDisorderedRegionsDisopred_3( selectedProteins[ i ] );

		} else if( annoType === SELECT_ELEMENT_ANNOTATION_TYPE_PSIPRED3 ) {
			
			segments = getSecondaryStructureRegions( selectedProteins[ i ] );
			
		} else if( annoType === SELECT_ELEMENT_ANNOTATION_TYPE_SEQUENCE_COVERAGE ) {
			
			segments = _ranges[ selectedProteins[ i ] ];
			
		} else {
			console.log( "Error, unknown feature annotation type selected." );
			return;
		}
		
		if( segments == undefined ) {
			console.log( "Error, got undefined for segments." );
			
			console.log( "protein: " + selectedProteins[ i ] );
			console.log( "annoType: " + annoType );
			
			return;
		}
		
		// draw the segments
		for( var s = 0; s < segments.length; s++ ) {
			
			var segment = segments[ s ];
			var toolTipText = '';
			
			// get the color to use for this segment
			var color;
			if( annoType === SELECT_ELEMENT_ANNOTATION_TYPE_DISOPRED3 ) {
				
				color = this.CONSTANTS._DISOPRED_COLOR;
				
				toolTipText = 'Disordered Region: start: ' + segment.startPosition + ', end: ' + segment.endPosition;

			} else if( annoType === SELECT_ELEMENT_ANNOTATION_TYPE_PSIPRED3 ) {
				
				if ( segment.type === BETA_SHEET ) {
					color = this.CONSTANTS._PSIPRED_BETA_COLOR;
					
					toolTipText = '&#946; sheet: start: ' + segment.startPosition + ', end: ' + segment.endPosition;
					
				} else {
					color = this.CONSTANTS._PSIPRED_ALPHA_COLOR;
					
					toolTipText = '&#945; helix: start: ' + segment.startPosition + ', end: ' + segment.endPosition;

				}
								
			} else if( annoType === SELECT_ELEMENT_ANNOTATION_TYPE_SEQUENCE_COVERAGE ) {
				
				color = this.getColorForIndex( i );
				
				toolTipText = 'Sequence coverage segment: start: ' + segment.start + ', end: ' + segment.end;
				
			}
			
			// draw this segment
			var pathText;
			if( annoType === SELECT_ELEMENT_ANNOTATION_TYPE_SEQUENCE_COVERAGE ) {
				pathText = this.getPathForSegment( i, segment.start, segment.end );			
			} else {
				pathText = this.getPathForSegment( i, segment.startPosition, segment.endPosition )
			}
			
			var p = svgRootSnapSVGObject.path( pathText );
			p.attr({
				fill:color,
				"fill-opacity":"0.8"
			});
			
			// add tooltip to this segment
			
			var toolTipParams = {
					content: {
						text: toolTipText
					},
					position: {
						target: 'mouse'
							,
							adjust: { x: 5, y: 5 } // Offset it slightly from under the mouse
					}
			};
			
			var pSVGNativeObject = p.node;
			var $pSVGNativeObject= $(pSVGNativeObject);
			$pSVGNativeObject.qtip( toolTipParams );
			
		}
		
	}
	
	
};

/**
 * Get the string to use for the path for a feature annotation segment
 * 
 * @param proteinIndex
 * @param segment
 */
circlePlotViewer.prototype.getPathForSegment = function( proteinIndex, startPosition, endPosition ) {
	
	var startAngle = this.getAngleForProteinPosition( proteinIndex, startPosition );
	var endAngle = this.getAngleForProteinPosition( proteinIndex, endPosition );
	
	var radii = this.getFeatureAnnotationRadii();
	var center = this.getCenterCoords();
	
    var outerStart = this.polarToCartesian(center.x, center.y, radii.outer, endAngle);
    var outerEnd = this.polarToCartesian(center.x, center.y, radii.outer, startAngle);

    var innerStart = this.polarToCartesian(center.x, center.y, radii.inner, endAngle);
    var innerEnd = this.polarToCartesian(center.x, center.y, radii.inner, startAngle);
    
    var arcSweep = endAngle - startAngle <= 180 ? "0" : "1";
    
    var d = [
             "M", innerEnd.x, innerEnd.y, 
             "A", radii.inner, radii.inner, 0, arcSweep, 1, innerStart.x, innerStart.y,
             "L", outerStart.x, outerStart.y,
             "A", radii.outer, radii.outer, 0, arcSweep, 0, outerEnd.x, outerEnd.y,
             "L", innerEnd.x, innerEnd.y
         ].join(" ");
	
	return d;
};



/**
 * The radii to use for the feature annotation bars
 */
circlePlotViewer.prototype.getFeatureAnnotationRadii = function() {
	
	var innerRadius, outerRadius;
	
	if( this.isScaleBarSelected() ) {
		outerRadius =  this.radius;
		
		// subtract total height of scale bar plus gap between layers
		outerRadius -= this.CONSTANTS._SCALE_BAR_HEIGHT;
		outerRadius -= this.CONSTANTS._SCALE_BAR_FONT_HEIGHT;
		outerRadius -= this.CONSTANTS._GAP_BETWEEN_SCALE_BAR_AND_PROTEIN_BAR;
		outerRadius -= this.CONSTANTS._GAP_BETWEEN_SCALE_BAR_AND_TEXT;
		
		innerRadius = outerRadius - this.CONSTANTS._FEATURE_ANNOTATION_HEIGHT;
	} else {
		outerRadius =  this.radius;
		innerRadius = outerRadius - this.CONSTANTS._FEATURE_ANNOTATION_HEIGHT;
	}
	
	return {
		inner:innerRadius,
		outer:outerRadius
	};
	
};

/**
 * Draw the legend when coloring by search
 * 
 * @param svgRootSnapSVGObject
 */
circlePlotViewer.prototype.drawLegend = function( svgRootSnapSVGObject ) {

	var g = svgRootSnapSVGObject.g();
	
	var p = svgRootSnapSVGObject.text( this.radius * 2 + this.CONSTANTS._LEGEND_GAP, this.CONSTANTS._LEGEND_HEADER_HEIGHT, "Line Legend" );
	p.attr({
		"font-size":this.CONSTANTS._LEGEND_HEADER_HEIGHT + "px",
		"font-weight":"bold",
		"fill-opacity":"0.7"
	});
	g.add( p );
	
	// get all possible combination of searches as arrays
	var searchArrays = [ ];
	
	for( var i = 0; i < _searches.length; i++ ) {
		searchArrays.push( [ _searches[ i ].id ] );
	}
	
	for( var i = 0; i < _searches.length; i++ ) {
		for( var j = 0; j < _searches.length; j++ ) {
			if( i >= j ) { continue; }
			
			searchArrays.push( [ _searches[ i ].id, _searches[ j ].id ] );
		}
	}
	
	if( _searches.length > 2 ) {
		searchArrays.push( [ _searches[ 0 ].id, _searches[ 1 ].id, _searches[ 2 ].id ] );
	}


	// the left edge of the legend area
	var x = this.radius * 2 + this.CONSTANTS._LEGEND_GAP + this.CONSTANTS._LEGEND_INDENT;

	// draw each legend item
	for( var i = 0; i < searchArrays.length; i++ ) {
		
		var color = this.getColorForSearches( searchArrays[ i ] );
		
		var y = this.CONSTANTS._LEGEND_HEADER_HEIGHT + this.CONSTANTS._LEGEND_GAP;
		y += i * ( this.CONSTANTS._LEGEND_FONT_HEIGHT + this.CONSTANTS._LEGEND_GAP );
		y += this.CONSTANTS._LEGEND_FONT_HEIGHT;

		// center of the circle to use as the legend item graphic
		var circleY = y - this.CONSTANTS._LEGEND_RADIUS;
		var circleX = x + this.CONSTANTS._LEGEND_RADIUS;
		
		p = svgRootSnapSVGObject.circle( circleX, circleY, this.CONSTANTS._LEGEND_RADIUS );
		p.attr( {
			"stroke":"none",
			"fill":color,
		})
		g.add( p );
		

		// the left edge of the associated text
		var textX = x + (this.CONSTANTS._LEGEND_RADIUS * 2 ) + this.CONSTANTS._LEGEND_INDENT;
		
		
		var text = searchArrays[ i ].join( ", " );
		if( searchArrays[ i ].length < 2 ) {
			text = "Search " + text;
		} else {
			text = "Searches " + text;
		}		
		
		p = svgRootSnapSVGObject.text( textX, y - 2, text );
		p.attr( {
			"font-size": this.CONSTANTS._LEGEND_FONT_HEIGHT + "px",
			fill:color,
			"fill-opacity":"0.9",
			"font-weight":"600",
		});
		
		g.add( p );
		
	};
	
	
	
	
};

/**
 * Draw scale bar outside of each protein bar
 * 
 * @param svgRootSnapSVGObject
 */
circlePlotViewer.prototype.drawScaleBars = function( svgRootSnapSVGObject ) {
	
	var center = this.getCenterCoords();
	var selectedProteins = getAllSelectedProteins();
	var degreesPerResidue = this.getDegreesPerResidue();
	
	// determine frequency and factor of tic marks
	var testFactors = [ 1, 2, 4, 10, 20, 40, 60, 80, 100, 200, 400, 600, 1000, 2000, 5000, 10000 ];
	
	var factor;				// number of residues per tick mark
	var degreesPerTick;		// number of degrees between tick marks
	
	for( var i = 0; i < testFactors.length; i++ ) {
		degreesPerTick = testFactors[ i ] * degreesPerResidue;
		
		if( degreesPerTick >= 5 ) {
			factor = testFactors[ i ];
			break;
		}
	}
	
	// loop over all proteins, draw their scale bars
	for( var i = 0; i < selectedProteins.length; i++ ) {

		var proteinId = selectedProteins[ i ];
		
		// group to hold the scale bar
		var group = svgRootSnapSVGObject.g();

		
		var startAngle = this.getAngleForProteinPosition( i, 1 );
		var endAngle = this.getAngleForProteinPosition( i, _proteinLengths[ proteinId ] );
		
		
		
		
		// draw base line of scale bar
		var radius = this.radius;
		radius -= this.CONSTANTS._SCALE_BAR_FONT_HEIGHT;
		radius -= this.CONSTANTS._GAP_BETWEEN_SCALE_BAR_AND_TEXT;
		radius -= this.CONSTANTS._SCALE_BAR_HEIGHT / 2;
		
		var startPoint = this.polarToCartesian( center.x, center.y, radius , startAngle)
		var endPoint = this.polarToCartesian( center.x, center.y, radius , endAngle)
		
		
	    var arcSweep = endAngle - startAngle <= 180 ? "0" : "1";
	    
	    var d = [
	             "M", endPoint.x, endPoint.y, 
	             "A", radius, radius, 0, arcSweep, 0, startPoint.x, startPoint.y
	         ].join(" ");
		
		var path = svgRootSnapSVGObject.path( d );
		path.attr({
			stroke:this.getColorForIndex( i ),
			fill:"none"
		});
		
		group.add( path );
		
		
		
		// draw start tick
		var radius1 = this.radius;
		radius1 -= this.CONSTANTS._SCALE_BAR_FONT_HEIGHT;
		radius1 -= this.CONSTANTS._GAP_BETWEEN_SCALE_BAR_AND_TEXT;
		
		var radius2 = radius1 - this.CONSTANTS._SCALE_BAR_HEIGHT;
		
		startPoint = this.polarToCartesian( center.x, center.y, radius1 , startAngle)
		endPoint = this.polarToCartesian( center.x, center.y, radius2 , startAngle)
		
		d = [
             "M", startPoint.x, startPoint.y, 
             "L", endPoint.x, endPoint.y
         ].join(" ");
		
		path = svgRootSnapSVGObject.path( d );
		path.attr({
			stroke:this.getColorForIndex( i ),
			fill:"none"
		});
		group.add( path );
		
		
		
		// draw end tick
		startPoint = this.polarToCartesian( center.x, center.y, radius1 , endAngle)
		endPoint = this.polarToCartesian( center.x, center.y, radius2 , endAngle)
		
		d = [
             "M", startPoint.x, startPoint.y, 
             "L", endPoint.x, endPoint.y
         ].join(" ");
		
		path = svgRootSnapSVGObject.path( d );
		path.attr({
			stroke:this.getColorForIndex( i ),
			fill:"none"
		});
		group.add( path );
		
		

		// draw interval ticks
		for( var k = factor; k < _proteinLengths[ proteinId ]; k += factor ) {
			
			var angle = startAngle + ( k * degreesPerResidue ) - degreesPerResidue;
			
			startPoint = this.polarToCartesian( center.x, center.y, radius1 , angle)
			endPoint = this.polarToCartesian( center.x, center.y, radius2 , angle)
			
			d = [
	             "M", startPoint.x, startPoint.y, 
	             "L", endPoint.x, endPoint.y
	         ].join(" ");
			
			path = svgRootSnapSVGObject.path( d );
			path.attr({
				stroke:this.getColorForIndex( i ),
				fill:"none"
			});
			group.add( path );
			
			
		}
		
		// draw labels, start with 2nd tick and label every other tick
		// draw interval ticks
		
		radius = this.radius;
		radius -= this.CONSTANTS._SCALE_BAR_FONT_HEIGHT;
		
		for( var k = factor; k < _proteinLengths[ proteinId ]; k += factor * 2 ) {
			
			// create the path on which we are drawing our label
			
			var angle = startAngle + ( k * degreesPerResidue ) - degreesPerResidue;
			
			startPoint = this.polarToCartesian( center.x, center.y, radius , angle - degreesPerResidue * factor );
			endPoint = this.polarToCartesian( center.x, center.y, radius , angle + degreesPerResidue * factor  );
			
			d = [
	             "M", startPoint.x, startPoint.y, 
	             "A", radius, radius, 0, 0, 1, endPoint.x, endPoint.y
	         ].join(" ");
					
			var text = svgRootSnapSVGObject.text( 0, 0, k + "" );
			
			text.attr( {
				"textpath" : d,
				"font-size": this.CONSTANTS._SCALE_BAR_FONT_HEIGHT + "px",
				stroke:this.getColorForIndex( i ),
				fill:"none",
				"stroke-opacity":"0.9",
				"text-anchor":"middle",
			});
			
			text.textPath.attr( {
				"startOffset":"50%",
			});
			
			group.add( text );

		}
		
		
	}//end looping over proteins
};



/**
 * Draw all tryptic positions in all proteins
 * 
 * @param svgRootSnapSVGObject
 */
circlePlotViewer.prototype.drawTrypticPositions = function( svgRootSnapSVGObject ) {
	var selectedProteins = getAllSelectedProteins();
	var radius = this.getProteinBarRadii();
	var center = this.getCenterCoords();
	
	for( var i = 0; i < selectedProteins.length; i++ ) {
		var proteinId = selectedProteins[ i ];
		
		var positions = _proteinSequenceTrypsinCutPoints[ proteinId ];
		if( positions != undefined ) {
			
			for( var p = 0; p < positions.length; p++ ) {
				var angle = this.getAngleForProteinPosition( i, positions[ p ] );
				var start = this.polarToCartesian( center.x, center.y, radius.inner, angle );
				var end   = this.polarToCartesian( center.x, center.y, radius.outer, angle );
				
				var pathString = "M" + start.x + "," + start.y;
				pathString +=    "L" + end.x + "," + end.y;
				
				var path = svgRootSnapSVGObject.path( pathString );
				
				path.attr( {
					stroke:this.CONSTANTS._LINKABLE_POSITION_COLOR,
					"stroke-opacity":this.CONSTANTS._LINKABLE_POSITION_OPACITY,
					"stroke-dasharray":"4,2"
				})
			}
			
		}
	}
};

/**
 * Draw all linkable positions in all proteins
 * 
 * @param svgRootSnapSVGObject
 */
circlePlotViewer.prototype.drawLinkablePositions = function( svgRootSnapSVGObject ) {
	var selectedProteins = getAllSelectedProteins();
	var radius = this.getProteinBarRadii();
	var center = this.getCenterCoords();
	
	for( var i = 0; i < selectedProteins.length; i++ ) {
		var proteinId = selectedProteins[ i ];
		
		var positions = _linkablePositions[ proteinId ];
		if( positions != undefined ) {
			
			for( var p = 0; p < positions.length; p++ ) {
				var angle = this.getAngleForProteinPosition( i, positions[ p ] );
				var start = this.polarToCartesian( center.x, center.y, radius.inner, angle );
				var end   = this.polarToCartesian( center.x, center.y, radius.outer, angle );
				
				var pathString = "M" + start.x + "," + start.y;
				pathString +=    "L" + end.x + "," + end.y;
				
				var path = svgRootSnapSVGObject.path( pathString );
				
				path.attr( {
					stroke:this.CONSTANTS._LINKABLE_POSITION_COLOR,
					"stroke-opacity":this.CONSTANTS._LINKABLE_POSITION_OPACITY
				})
			}
			
		}
	}
};

/**
 * Draw all monolinks
 * @param svgRootSnapSVGObject
 */
circlePlotViewer.prototype.drawMonolinks = function( svgRootSnapSVGObject ) {
	var selectedProteins = getAllSelectedProteins();
	
	for( var i = 0; i < selectedProteins.length; i++ ) {
		var proteinId = selectedProteins[ i ];
		
		if ( _proteinMonolinkPositions[ proteinId ] == undefined ) { 
			continue; //  skip processing this selected protein 
		}

		var positions = Object.keys( _proteinMonolinkPositions[ selectedProteins[ i ] ] );
		for( var k = 0; k < positions.length; k++ ) {
			var position = positions[ k ];
			
			var link = { };
			link.type = "monolink";
			link.protein1 = proteinId;
			link.position1 = position;
			
			
			this.drawMonolink( i, link, svgRootSnapSVGObject );
		}
	
	}
};

/**
 * Draw a monolink
 * @param index
 * @param link
 * @param svgRootSnapSVGObject
 */
circlePlotViewer.prototype.drawMonolink = function( index, link, svgRootSnapSVGObject ) {
	
	var angle = this.getAngleForProteinPosition( index, link.position1 );
	var center = this.getCenterCoords();
	
	var radius1 = this.getProteinBarRadii().inner - this.CONSTANTS._GAP_BETWEEN_BARS_AND_LINKS;
	var radius2 = radius1 - this.CONSTANTS._MONOLINK_HEIGHT;
	
	var point1 = this.polarToCartesian( center.x, center.y, radius1, angle );
	var point2 = this.polarToCartesian( center.x, center.y, radius2, angle );
	
	var pathString = "M" + point1.x + "," + point1.y;
	pathString += "L" + point2.x + "," + point2.y;
	
	var path = svgRootSnapSVGObject.path( pathString );
	
	var opacity = getOpacityForIndexAndLink( index, link );
	
    var lsearches = _proteinMonolinkPositions[ link.protein1 ][ link.position1 ];
	
    path.attr( {
		stroke:this.getColorForIndex( index ),
		"stroke-dasharray":"4,2",
		fill:"none",
		"stroke-opacity":opacity,
		'from_protein_id':link.protein1,
		'fromp': _proteinNames[ link.protein1 ],
		'frompp': link.position1,
		'searches': lsearches,
		'linktype' : 'monolink'
	});
    
    
    
    // add a tooltip to this drawn link
    
    var text = "";
    
	text = 'Monolink: ' + _proteinNames[ link.protein1 ] + " (" + link.position1 + ")<br>Searches: " + lsearches;
    
	var pathSVGObject = path.node;
	var $pathSVGObject = $( pathSVGObject );		// jquery variable
	
	
	$pathSVGObject.qtip({
		content:  { text: text },
	    position: { target: 'mouse', adjust: { x: 5, y: 5 } }
	});
    
    
	// add mouseover effects
	path.mouseover( function() { this.attr({ strokeWidth: 3 }); });
	path.mouseout( function() { this.attr({ strokeWidth: 1 }); });
	
	// add click event
	$pathSVGObject.click( function(  ) { processClickOnLink( this ); });
};


/**
 * Draw all looplinks
 * @param svgRootSnapSVGObject
 */
circlePlotViewer.prototype.drawLooplinks = function( svgRootSnapSVGObject ) {
	var selectedProteins = getAllSelectedProteins();
	
	for( var i = 0; i < selectedProteins.length; i++ ) {
		var proteinId = selectedProteins[ i ];
		
		var drawnLinks = { };
		
		if ( _proteinLooplinkPositions[ proteinId ] == undefined ) { continue; }
		if ( _proteinLooplinkPositions[ proteinId ][ proteinId ] == undefined ) { continue; }
		
		var fromPositions = Object.keys( _proteinLooplinkPositions[ proteinId ][ proteinId ] );
		
		for( var fromPositionIndex = 0; fromPositionIndex < fromPositions.length; fromPositionIndex++ ) {
			var fromPosition = parseInt( fromPositions[ fromPositionIndex ] );
			
			var toPositions = Object.keys( _proteinLooplinkPositions[ proteinId ][ proteinId ][ fromPosition ] );
			
			for( var toPositionIndex = 0; toPositionIndex < toPositions.length; toPositionIndex++ ) {
				var toPosition = parseInt( toPositions [ toPositionIndex ] );
				
				// prevent drawing the same links twice
				var key;
				if( toPosition >= fromPosition ) {
					key = toPosition + "-" + fromPosition;
				} else {
					key = fromPosition + "-" + toPosition;
				}
				
				if( key in drawnLinks ) { continue; }
				else { drawnLinks[ key ] = 1; }
				
				
				var link = { };
				link.type = "looplink",
				link.protein1 = proteinId;
				link.position1 = fromPosition;
				link.position2 = toPosition;
				
				var looplink = this.drawCrosslink( i, fromPosition, i, toPosition, link, svgRootSnapSVGObject );
				looplink.attr( { "stroke-dasharray":"4,2" });
			}
			
		}		
	}
};


/**
 * Draw all inter-protein crosslinks
 * @param svgRootSnapSVGObject
 */
circlePlotViewer.prototype.drawCrosslinks = function( svgRootSnapSVGObject ) {
	var selectedProteins = getAllSelectedProteins();
	
	for( var i = 0; i < selectedProteins.length; i++ ) {
		var proteinId1 = selectedProteins[ i ];
		
		if ( _proteinLinkPositions[ proteinId1 ] == undefined ) { continue; }
		
		for( var j = 0; j < selectedProteins.length; j++ ) {
			if( j <= i ) { continue; }
			
			var proteinId2 = selectedProteins[ j ]
			
			if ( _proteinLinkPositions[ proteinId1 ][ proteinId2 ] == undefined ) { continue; }
			
			var fromPositions = Object.keys( _proteinLinkPositions[ proteinId1 ][ proteinId2 ] );
			
			for( var fromPositionIndex = 0; fromPositionIndex < fromPositions.length; fromPositionIndex++ ) {
				var fromPosition = parseInt( fromPositions[ fromPositionIndex ] );
				
				var toPositions = Object.keys( _proteinLinkPositions[ proteinId1 ][ proteinId2 ][ fromPosition ] );
				
				for( var toPositionIndex = 0; toPositionIndex < toPositions.length; toPositionIndex++ ) {
					var toPosition = parseInt( toPositions [ toPositionIndex ] );

					var link = { };
					link.type = "crosslink";
					link.protein1 = proteinId1;
					link.position1 = fromPosition;
					link.protein2 = proteinId2;
					link.position2 = toPosition;
					
					this.drawCrosslink( i, fromPosition, j, toPosition, link, svgRootSnapSVGObject );				
				}
				
			}
			
			
		}
	}
	
};


/**
 * Draw all the self-crosslinks
 * 
 * @param svgRootSnapSVGObject
 */
circlePlotViewer.prototype.drawSelfCrosslinks = function( svgRootSnapSVGObject ) {
	var selectedProteins = getAllSelectedProteins();
		
	for( var i = 0; i < selectedProteins.length; i++ ) {
		var proteinId = selectedProteins[ i ];
				
		var drawnLinks = { };
		
		if ( _proteinLinkPositions[ proteinId ] == undefined ) { continue; }
		if ( _proteinLinkPositions[ proteinId ][ proteinId ] == undefined ) { continue; }
		
		
		
		var fromPositions = Object.keys( _proteinLinkPositions[ proteinId ][ proteinId ] );		
		
		for( var fromPositionIndex = 0; fromPositionIndex < fromPositions.length; fromPositionIndex++ ) {
			var fromPosition = parseInt( fromPositions[ fromPositionIndex ] );
						
			var toPositions = Object.keys( _proteinLinkPositions[ proteinId ][ proteinId ][ fromPosition ] );
						
			for( var toPositionIndex = 0; toPositionIndex < toPositions.length; toPositionIndex++ ) {
				var toPosition = parseInt( toPositions [ toPositionIndex ] );
								
				// prevent drawing the same links twice
				var key;
				if( toPosition >= fromPosition ) {
					key = toPosition + "-" + fromPosition;
				} else {
					key = fromPosition + "-" + toPosition;
				}
				
				if( key in drawnLinks ) { continue; }
				else { drawnLinks[ key ] = 1; }
				
				var link = { };
				link.type = "crosslink",
				link.protein1 = proteinId;
				link.protein2 = proteinId;
				link.position1 = fromPosition;
				link.position2 = toPosition;
				
				this.drawCrosslink( i, fromPosition, i, toPosition, link, svgRootSnapSVGObject );				
			}
			
		}		
	}
};


/**
 * Draw a cross link on the SVG object
 * @param fromIndex
 * @param fromPosition
 * @param toIndex
 * @param toPosition
 * @param svgRootSnapSVGObject
 */
circlePlotViewer.prototype.drawCrosslink = function( fromIndex, fromPosition, toIndex, toPosition, link, svgRootSnapSVGObject ) {
	
	var center = this.getCenterCoords();
	
	var fromCoords = this.getCrosslinkTerminus( fromIndex, fromPosition );
	var toCoords = this.getCrosslinkTerminus( toIndex, toPosition );

	var fromDegrees = this.getAngleForProteinPosition( fromIndex, fromPosition );
	var toDegrees = this.getAngleForProteinPosition( toIndex, toPosition );
			
	var diffAngle = Math.abs( fromDegrees - toDegrees );
	if( diffAngle > 180 ) { diffAngle = 360 - diffAngle; }
	
	var midAngle;
	if( Math.abs( fromDegrees - toDegrees ) <= 180 ) {
		midAngle = ( fromDegrees + toDegrees ) / 2;
	} else {
		midAngle = ( fromDegrees + toDegrees ) / 2;
		if( midAngle < 180 ) { midAngle += 180; }
		else { midAngle -= 180; }
	}
	
	var ratio = diffAngle / 180;
	
	var radius = this.getProteinBarRadii().inner - this.CONSTANTS._GAP_BETWEEN_BARS_AND_LINKS - this.CONSTANTS._MINIMUM_CROSSLINK_LOOP_HEIGHT;
	radius -= radius * ratio;
		
	var loopPeakCoordinates = this.polarToCartesian( center.x, center.y, radius, midAngle );
	
	var opacity = getOpacityForIndexAndLink( fromIndex, link );
	
    var pathString = "M" + fromCoords.x + "," + fromCoords.y;
    pathString += "Q" + loopPeakCoordinates.x + "," + loopPeakCoordinates.y;
    pathString += " " + toCoords.x + "," + toCoords.y;
        
    var path = svgRootSnapSVGObject.path( pathString );
    path.attr( {
		stroke:this.getColorForLink( fromIndex, link ),
		fill:"none",
		"stroke-opacity":opacity,
	});
    
    // add a tooltip to this drawn link
    
    var text = "";
    
    if( link.type === "looplink" ) {	
    	
		var lsearches = _proteinLooplinkPositions[ link.protein1 ][ link.protein1 ][ link.position1 ][ link.position2 ];
		text = 'Looplink: ' + _proteinNames[ link.protein1 ] + " (" + link.position1 + "," + link.position2 + ")<br>Searches: " + lsearches;
		
	    path.attr( {
			'from_protein_id': link.protein1,
			'fromp': _proteinNames[ link.protein1 ],
			'frompp': link.position1,
			'topp': link.position2,
			'searches': lsearches,
			'linktype' : 'looplink'
		});
		
    } else {
		var lsearches = _proteinLinkPositions[ link.protein1 ][ link.protein2 ][ link.position1 ][ link.position2 ];
		text = 'Crosslink: ' + _proteinNames[ link.protein1 ] + " (" + link.position1 + ") - " + _proteinNames[ link.protein2 ] + " (" + link.position2 + ")<br>Searches: " + lsearches;
		
	    path.attr( {
			'from_protein_id':link.protein1,
			'to_protein_id':link.protein2,
			'fromp': _proteinNames[ link.protein1 ],
			'top': _proteinNames[ link.protein2 ],
			'frompp': link.position1,
			'topp': link.position2,
			'searches': lsearches,
			'linktype' : 'crosslink'
		});
	    
    }
    
	var pathSVGObject = path.node;
	var $pathSVGObject = $( pathSVGObject );		// jquery variable
	
	
	$pathSVGObject.qtip({
		content:  { text: text },
	    position: { target: 'mouse', adjust: { x: 5, y: 5 } }
	});
    
    
	// add mouseover effects
	path.mouseover( function() { this.attr({ strokeWidth: 3 }); });
	path.mouseout( function() { this.attr({ strokeWidth: 1 }); });
	
	// add click effects
	$pathSVGObject.click( function(  ) { processClickOnLink( this ); });

	
    return path;
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
	
	var center = this.getCenterCoords();
	
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
			
			return this.polarToCartesian( center.x,
										  center.y,
										  this.getProteinBarRadii().inner - this.CONSTANTS._GAP_BETWEEN_BARS_AND_LINKS,
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
 * Find the degrees of rotation to the given protein's position
 * @param proteinIndex
 * @param position
 * @returns {Number}
 */
circlePlotViewer.prototype.getAngleForProteinPosition = function( proteinIndex, position ) {
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
			
			return degrees;

		} else {
			var totalLengthProportion = _proteinLengths[ selectedProteins[ i ] ] / totalProteinLength;
			var rotationDegrees = workingDegrees * totalLengthProportion;
			currentStartDegrees += this.CONSTANTS._GAP_BETWEEN_BARS + rotationDegrees;
		}
		
	}
};



/**
 * Draw all the protein bars, and associated items (such as labels, linkable positions, and scale bars)
 * @param svgRootSnapSVGObject
 */
circlePlotViewer.prototype.drawProteinBars = function( svgRootSnapSVGObject, isTransparent ) {
	
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
		
		this.drawProteinBar( svgRootSnapSVGObject, isTransparent, selectedProteins[ i ], i, currentStartDegrees, rotationDegrees );
		
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
circlePlotViewer.prototype.drawProteinBar = function( svgRootSnapSVGObject, isTransparent, proteinId, index, startDegrees, rotationDegrees ) {
	
	var center = this.getCenterCoords();
	
	var path = this.getCurvedBarPath( 
									  startDegrees,
									  startDegrees + rotationDegrees
									 );
	
	var svgPath = svgRootSnapSVGObject.path( path );
	
	if( isTransparent ) {
		
		svgPath.attr( {
			"fill-opacity":"0",
			"protein_id":proteinId,
		});
		
		this.addMouseOverHandlerToProteinBar( svgRootSnapSVGObject, svgPath, proteinId, index );
		
	} else {
		svgPath.attr( {
			fill:this.getColorForIndex( index ),
		});
	}

	if( !isTransparent ){
		this.drawProteinBarLabel( svgRootSnapSVGObject, proteinId, index, startDegrees, rotationDegrees );
	}
	
};

/**
 * Draw the label for a given protein bar
 * @param svgRootSnapSVGObject
 * @param svgPath
 * @param proteinId
 */
circlePlotViewer.prototype.drawProteinBarLabel = function( svgRootSnapSVGObject, proteinId, index, startDegrees, rotationDegrees ) {
	
	var center = this.getCenterCoords();
	
	var path = this.getCurvedPathForLabel( center.x, 
									  center.y,
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
};

/**
 * Add mouseover handler (causing a tooltip) to the protein bar
 * 
 * @param svgPath
 * @param proteinId
 */
circlePlotViewer.prototype.addMouseOverHandlerToProteinBar = function( svgRootSnapSVGObject, svgPath, proteinId, index ) {
	
	svgPath.addClass( _PROTEIN_BAR_OVERLAY_RECTANGLE_LABEL_CLASS );
	
	var proteinBarPlainSVGObject = svgPath.node;
	var $proteinBarPlainSVGObject = $( proteinBarPlainSVGObject );		// jquery variable
	var $svgDrawing = $proteinBarPlainSVGObject.parent();				// jquery object for parent svg drawing
	
	// coords for the location of the svg drawing so we can convert page
	// coordiantes into coordinates within the drawing
	var topLevelSVGCoords = {
			x:$svgDrawing.offset().left,
			y:$svgDrawing.offset().top
	};
	
	// a this we can use within the function defined below that refers to this object
	var _THIS = this;
	

	// the function called on mouse over
	var toolTipFunction = function( eventObject, qtipAPI /* qtip "api" variable/property */ ) {
		
		var eventpageX = eventObject.pageX;
		var eventpageY = eventObject.pageY;
		
		var svgCoords = { };
		svgCoords.x = eventpageX - topLevelSVGCoords.x;
		svgCoords.y = eventpageY - topLevelSVGCoords.y;
		
		
		var proteinPositionOneBased = _THIS.getProteinIndexPosition( index, svgCoords );
		var proteinPositionZeroBased = proteinPositionOneBased - 1;
		
		var proteinName = getProteinName( proteinId );
		var proteinSequence = _proteinSequences[ proteinId ];
		var proteinSequenceLength  = proteinSequence.length;
		
		var sequenceAtPosition = proteinSequence.charAt( ( proteinPositionZeroBased ) );  

		//  Default sequences left and right to "" since may be at either edge.
		var sequenceAtPositionLeft1 = "";
		var sequenceAtPositionLeft2 = "";
		var sequenceAtPositionLeft3 = "";

		var sequenceAtPositionRight1 = "";
		var sequenceAtPositionRight2 = "";
		var sequenceAtPositionRight3 = "";
		
		if ( proteinPositionZeroBased > 0 ) { sequenceAtPositionLeft1 = proteinSequence.charAt( ( proteinPositionZeroBased - 1 ) ); }
		if ( proteinPositionZeroBased > 1 ) { sequenceAtPositionLeft2 = proteinSequence.charAt( ( proteinPositionZeroBased - 2 ) ); }
		if ( proteinPositionZeroBased > 2 ) { sequenceAtPositionLeft3 = proteinSequence.charAt( ( proteinPositionZeroBased - 3 ) ); }
		
		if ( proteinPositionZeroBased < proteinSequenceLength - 1 ) { sequenceAtPositionRight1 = proteinSequence.charAt( ( proteinPositionZeroBased + 1 ) ); }
		if ( proteinPositionZeroBased < proteinSequenceLength - 2 ) { sequenceAtPositionRight2 = proteinSequence.charAt( ( proteinPositionZeroBased + 2 ) ); }
		if ( proteinPositionZeroBased < proteinSequenceLength - 3 ) { sequenceAtPositionRight3 = proteinSequence.charAt( ( proteinPositionZeroBased + 3 ) ); }
		

		// whether or not show linkable positions is selected
		var linkablePositionsSelected = $( "input#show-linkable-positions" ).is( ':checked' );
		
		var proteinNameDisplay = " display:none; ";

		if ( linkablePositionsSelected ) {
			proteinNameDisplay = " ";	
		}

	
		var cutPointBetweenCenterAndFirstLeft = false;
		var cutPointBetweenFirstLeftAndSecondLeft = false;
		var cutPointBetweenSecondLeftAndThirdLeft = false;
		
		var cutPointBetweenCenterAndFirstRight = false;
		var cutPointBetweenFirstRightAndSecondRight = false;
		var cutPointBetweenSecondRightAndThirdRight = false;
		
		var proteinSequenceTrypsinCutPoints = _proteinSequenceTrypsinCutPoints[ proteinId ];

		for ( var proteinSequenceTrypsinCutPointsIndex = 0; proteinSequenceTrypsinCutPointsIndex < proteinSequenceTrypsinCutPoints.length; proteinSequenceTrypsinCutPointsIndex++ ) {
			
			//  trypsinCutPoint is "1" based 
			
			var trypsinCutPoint = proteinSequenceTrypsinCutPoints[ proteinSequenceTrypsinCutPointsIndex ];

			if ( trypsinCutPoint > proteinPositionOneBased - 1 && trypsinCutPoint < proteinPositionOneBased ) {
				cutPointBetweenCenterAndFirstLeft = true;
			}
			if ( trypsinCutPoint > proteinPositionOneBased - 2 && trypsinCutPoint < proteinPositionOneBased - 1 ) {
				cutPointBetweenFirstLeftAndSecondLeft = true;
			}
			if ( trypsinCutPoint > proteinPositionOneBased - 3 && trypsinCutPoint < proteinPositionOneBased - 2 ) {
				cutPointBetweenSecondLeftAndThirdLeft = true;
			}

			if ( trypsinCutPoint > proteinPositionOneBased && trypsinCutPoint < proteinPositionOneBased + 1 ) {
				cutPointBetweenCenterAndFirstRight = true;
			}
			if ( trypsinCutPoint > proteinPositionOneBased + 1 && trypsinCutPoint < proteinPositionOneBased + 2 ) {
				cutPointBetweenFirstRightAndSecondRight = true;
			}
			if ( trypsinCutPoint > proteinPositionOneBased + 2 && trypsinCutPoint < proteinPositionOneBased + 3 ) {
				cutPointBetweenSecondRightAndThirdRight = true;
			}
		
		
		}
		
		
		
		//  name of css class in global.css.  Using a css class here since this is tool tip SVG, not main SVG
		
		var textClassForLinkablePosition = "proxl-primary-color-bold-svg-text";

		var textClassAtPosition = "";

		var textClassAtPositionLeft1 = "";
		var textClassAtPositionLeft2 = "";
		var textClassAtPositionLeft3 = "";

		var textClassAtPositionRight1 = "";
		var textClassAtPositionRight2 = "";
		var textClassAtPositionRight3 = "";
		
		
		
		var linkablePositionsForProtein = _linkablePositions[ proteinId ];
		
		
		for ( var linkablePositionsForProteinIndex = 0; linkablePositionsForProteinIndex < linkablePositionsForProtein.length; linkablePositionsForProteinIndex++ ) {
		
			//  linkablePositionsForProtein is "1" based 
			
			var linkablePosition = linkablePositionsForProtein[ linkablePositionsForProteinIndex ];

			if ( linkablePosition === proteinPositionOneBased ) {
				textClassAtPosition = textClassForLinkablePosition;
			}

			if ( linkablePosition === proteinPositionOneBased - 1 ) {
				textClassAtPositionLeft1 = textClassForLinkablePosition;
			}
			if ( linkablePosition === proteinPositionOneBased - 2 ) {
				textClassAtPositionLeft2 = textClassForLinkablePosition;
			}
			if ( linkablePosition === proteinPositionOneBased - 3 ) {
				textClassAtPositionLeft3 = textClassForLinkablePosition;
			}

			if ( linkablePosition === proteinPositionOneBased + 1 ) {
				textClassAtPositionRight1 = textClassForLinkablePosition;
			}
			if ( linkablePosition === proteinPositionOneBased + 2 ) {
				textClassAtPositionRight2 = textClassForLinkablePosition;
			}
			if ( linkablePosition === proteinPositionOneBased + 3 ) {
				textClassAtPositionRight3 = textClassForLinkablePosition;
			}
		
		
		}
		
		///////////
		
		//   New tool tip contents:
			
		var tooltipDataObject = {
				
				sequencePosition: proteinPositionOneBased,
				
				proteinName: proteinName,
				proteinNameDisplay: proteinNameDisplay,
				
				sequenceAtPosition: sequenceAtPosition,
				sequenceAtPositionLeft1: sequenceAtPositionLeft1,
				sequenceAtPositionLeft2: sequenceAtPositionLeft2,
				sequenceAtPositionLeft3: sequenceAtPositionLeft3,
				sequenceAtPositionRight1: sequenceAtPositionRight1,
				sequenceAtPositionRight2: sequenceAtPositionRight2,
				sequenceAtPositionRight3: sequenceAtPositionRight3,
				
				cutPointBetweenCenterAndFirstLeft: cutPointBetweenCenterAndFirstLeft,
				cutPointBetweenFirstLeftAndSecondLeft: cutPointBetweenFirstLeftAndSecondLeft,
				cutPointBetweenSecondLeftAndThirdLeft: cutPointBetweenSecondLeftAndThirdLeft,

				cutPointBetweenCenterAndFirstRight: cutPointBetweenCenterAndFirstRight,
				cutPointBetweenFirstRightAndSecondRight: cutPointBetweenFirstRightAndSecondRight,
				cutPointBetweenSecondRightAndThirdRight: cutPointBetweenSecondRightAndThirdRight,
				
				textClassAtPosition: textClassAtPosition,
				
				textClassAtPositionLeft1: textClassAtPositionLeft1,
				textClassAtPositionLeft2: textClassAtPositionLeft2,
				textClassAtPositionLeft3: textClassAtPositionLeft3,
				
				textClassAtPositionRight1: textClassAtPositionRight1,
				textClassAtPositionRight2: textClassAtPositionRight2,
				textClassAtPositionRight3: textClassAtPositionRight3
		

		};
		
		//  Use Handlebars libary to convert the template into HTML, performing substitutions using tooltipDataObject
		var tooltipContentsHTML = _proteinBarToolTip_template_HandlebarsTemplate( tooltipDataObject );


		
		//  Update tool tip contents
		qtipAPI.set('content.text', tooltipContentsHTML );
		
		// draw the indicator of this protein position on the SVG document
		_THIS.drawProteinPositionIndicator( svgRootSnapSVGObject, svgPath, index, proteinPositionOneBased );
	};
		

		
	//  Add the qtip tool tip to the protein bar overlay rectangle
		
	$proteinBarPlainSVGObject.qtip({
		content: {
			text: toolTipFunction
		},
		position: {
			my: 'top center', //  center the tool tip under the mouse and place the call out arrow in the center top of the tool tip box
			target: 'mouse',
			adjust: { x: 0, y: 10 } // Offset it from under the mouse
		}
	});
		
		
	// Grab the first element in the tooltips array and access its qTip API
	var qtipAPI = $proteinBarPlainSVGObject.qtip('api');
		
	
	
	svgPath.mousemove( function( eventObject ) {
		toolTipFunction( eventObject, qtipAPI );
	} );
	
	svgPath.mouseout( function( eventObject ) {
		_THIS.removeProteinPositionIndicator();
	} );
	
};

circlePlotViewer.prototype.removeProteinPositionIndicator = function() {
	if( !this.proteinPositionIndicator ) { return; }
	this.proteinPositionIndicator.svgPath.remove();
	delete this.proteinPositionIndicator;
};


/**
 * Draw the visual indicator of a specific position on a protein bar when it is moused over
 */
circlePlotViewer.prototype.drawProteinPositionIndicator = function ( svgRootSnapSVGObject, parentPath, index, position ) {

	// if we're moused over the same position, do nothing
	if( this.proteinPositionIndicator &&
		this.proteinPositionIndicator.index === index &&
		this.proteinPositionIndicator.position == position ) {
		return;
	} else {
		this.removeProteinPositionIndicator();
	}
	
	var path = this.getPathForProteinSegment( index, position, position );
	var svgPath = svgRootSnapSVGObject.path( path );
	
	svgPath.attr( {
		fill:"cyan",
		opacity:"0.45"
	});
	
	parentPath.before( svgPath );

	this.proteinPositionIndicator = {
		svgPath:svgPath,
		index:index,
		position:position
	};
	
	
	return svgPath;
};

/**
 * Get the SVG path string for the given protein index and start and end positions (starting at 1)
 */
circlePlotViewer.prototype.getPathForProteinSegment = function( index, start, end ) {
	
	var minDegrees = 0.2;
	
	// getAngleForProteinPosition = function( proteinIndex, position )
	var degreesPerResidue = this.getDegreesPerResidue();
	
	var startDegrees = this.getAngleForProteinPosition( index, start ) - ( degreesPerResidue / 2 );
	var endDegrees = this.getAngleForProteinPosition( index, end ) + ( degreesPerResidue / 2 );
	
	// ensure there is a minimum separation between start and end so that an indicator is visible
	var diff = endDegrees - startDegrees;
	if( diff < minDegrees ) {

		startDegrees = this.getAngleForProteinPosition( index, start ) - ( minDegrees / 2 );
		endDegrees = this.getAngleForProteinPosition( index, end ) + ( minDegrees / 2 );
		
	}
	
	var path = this.getCurvedBarPath( startDegrees, endDegrees );
	
	return path;	
};



circlePlotViewer.prototype.inializeSVGObject = function() {

	var svgRootSnapSVGObject;  // the Snap SVG object for the SVG

	
	//  First remove and add the "<svg>" element from the template
	
	var $svg_image_template_div = $("#svg_image_template_div");
	var svg_image_template_div__html = $svg_image_template_div.html();
	var $svg_image_inner_container_div = $("#svg_image_inner_container_div");
	
	//  select the <svg> element	
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
	} else {
		$svg_image_inner_container_div.css( { height : this.getDiameter() + 4 + "px" } );
	}
	
	return svgRootSnapSVGObject;
	
};



circlePlotViewer.prototype.setViewerDimensions = function(svgRootSnapSVGObject) {
	
	this.centerX = this.getDiameter() / 2;
	this.centerY = this.centerX;
	this.radius = this.centerX;
	
	var width = this.getDiameter();
	var height = this.getDiameter();
	
	if( this.isColorBySearch() ) {
		width += this.CONSTANTS._LEGEND_WIDTH + this.CONSTANTS._LEGEND_GAP;
	}
	
	svgRootSnapSVGObject.attr({  width: width,
								 height: height
							});

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

/**
 * Find and return the number of degrees (starting with 0 degrees being straight up)
 * around a circle centered at centerX and centerY we have to go before drawing a ray
 * that intersected the supplied coordinates.
 * 
 * @param centerCoords {x : x coord, y: y coord}
 * @param testCoords {x : x coord, y: y coord}
 */
circlePlotViewer.prototype.cartesianToPolar = function( centerCoords, testCoords ) {
	
	var deltaX = testCoords.x - centerCoords.x;
	var deltaY = centerCoords.y - testCoords.y;
	
	if( deltaX === 0 && deltaY === 0 ) {
		console.log( "Warning, called cartesianToPolar, but point was the center of the circle. Returning undefined." );
		return;
	}
	
	
	var degrees;
	if( deltaX == 0 ) {
		degrees = 90;
	} else {
		degrees = Math.atan( Math.abs( deltaY ) / Math.abs( deltaX ) );
		degrees = degrees * (180 / Math.PI);	// convert from radians to degrees
	}
	
	// convert to degrees rotation to the right, where 0 degrees is straight up
	if( deltaX >= 0 && deltaY >= 0 ) {
		degrees = 90 - degrees;
	} else if( deltaX >= 0 && deltaY < 0 ) {
		degrees = 90 + degrees;
	} else if( deltaX < 0 && deltaY < 0 ) {
		degrees = 270 - degrees;
	} else {
		degrees = 270 + degrees;
	}
	
	return degrees;
	
};

/**
 * Find the amino acid position (starting at 1) represented by the coordinates in testCoords
 * for the given protein index in the image. Note, there is no guarantee that the returned
 * position does not exceed the length of the protein at the given index.
 * 
 * @param proteinIndex
 * @param testCoords {x : x coord, y: y coord}
 */
circlePlotViewer.prototype.getProteinIndexPosition = function( proteinIndex, testCoords ) {
	
	var startingAngle = this.getAngleForProteinPosition( proteinIndex, 1 );
	var testingAngle = this.cartesianToPolar( this.getCenterCoords(), testCoords );
	var degreesPerResidue = this.getDegreesPerResidue();
	
	startingAngle -= degreesPerResidue / 2;
	
	var angleDiff = testingAngle - startingAngle;
	
	var position = Math.floor( 1 + angleDiff / degreesPerResidue );
	
	return position;
}

circlePlotViewer.prototype.getCurvedPathForLabel = function(centerX, centerY, startAngle, endAngle){
	
	var radii = this.getProteinBarRadii();
	
	var outerRadius = radii.outer;
	var innerRadius = radii.inner;
	
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
};


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
circlePlotViewer.prototype.getCurvedBarPath = function( startAngle, endAngle){
	
	var radii = this.getProteinBarRadii();
	
	var center = this.getCenterCoords();
	
	var outerRadius = radii.outer;
	var innerRadius = radii.inner;
	
    var outerStart = this.polarToCartesian( center.x, center.y, outerRadius, endAngle );
    var outerEnd = this.polarToCartesian( center.x, center.y, outerRadius, startAngle );

    var innerStart = this.polarToCartesian( center.x, center.y, innerRadius, endAngle );
    var innerEnd = this.polarToCartesian( center.x, center.y, innerRadius, startAngle );
    
    var arcSweep = endAngle - startAngle <= 180 ? "0" : "1";
    
    var d = [
             "M", innerEnd.x, innerEnd.y, 
             "A", innerRadius, innerRadius, 0, arcSweep, 1, innerStart.x, innerStart.y,
             "L", outerStart.x, outerStart.y,
             "A", outerRadius, outerRadius, 0, arcSweep, 0, outerEnd.x, outerEnd.y,
             "L", innerEnd.x, innerEnd.y
         ].join(" ");
    
    return d + "Z";       
};

/**
 * Get the radii to use for protein bars
 * 
 * @returns { outer: outer radius, inner: inner radius
 */
circlePlotViewer.prototype.getProteinBarRadii = function() {
	
	var radii = { };
	
	radii.outer = this.radius;
	
	if( this.isFeatureAnnotationShown() ) {
		radii.outer = this.getFeatureAnnotationRadii()[ 'inner' ];
		radii.outer -= this.CONSTANTS._GAP_BETWEEN_SCALE_BAR_AND_PROTEIN_BAR

	} else if( this.isScaleBarSelected() ) {
		radii.outer -= ( this.CONSTANTS._SCALE_BAR_HEIGHT + 
						 this.CONSTANTS._SCALE_BAR_FONT_HEIGHT +
						 this.CONSTANTS._GAP_BETWEEN_SCALE_BAR_AND_TEXT +
						 this.CONSTANTS._GAP_BETWEEN_SCALE_BAR_AND_PROTEIN_BAR );
	}
	
	radii.inner = radii.outer - this.CONSTANTS._HEIGHT_OF_PROTEIN_BARS;
	
	return radii;
};

/**
 * Get the center of the circle
 * @returns { x: x coord of center, y: y coord of center }
 */
circlePlotViewer.prototype.getCenterCoords = function() {
	
	var center = { };
	
	center.x = this.centerX;
	center.y = this.centerY;
	
	return center;
};

/**
 * Get the color to use for the given link and originating index
 * @param index
 * @param link
 */
circlePlotViewer.prototype.getColorForLink = function( index, link ) {
	
	var searches;
	
	// are we coloring by search?
	if( this.isColorBySearch() ) {
		if( link.type === "crosslink" ) { searches = findSearchesForCrosslink( link.protein1, link.protein2, link.position1, link.position2 ); }
		else if( link.type === "looplink" ) { searches = findSearchesForLooplink( link.protein1, link.position1, link.position2 ); }
		else if( link.type === "monolink" ) { searches = findSearchesForMonolink( link.protein1, link.position1 ); }
		else {
			console.log( "ERROR in getColorForLink, can't type type of link to find searches:" );
			console.log( link );
			return "#000000";
		}
				
		return this.getColorForSearches( searches );
	}

	// color by "originating" protein is default
	return this.getColorForIndex( index );
	
};

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
		
	var color = Snap.hsb2rgb( hue, saturation, brightness );
	return color.hex;
};

/**
 * Get the color to use for an array of searches when coloring by search
 * @param searches
 * @returns
 */
circlePlotViewer.prototype.getColorForSearches = function( searches ) {

	var colorIndex = "";
	
	for ( var i = 0; i < _searches.length; i++ ) {
		for ( var k = 0; k < searches.length; k++ ) {
			if ( _searches[i]['id'] === searches[ k ] ) {
				colorIndex += ( i + 1 );
				break;
			}
		}
	}
	
	return this.CONSTANTS._SEARCH_COLORS[ colorIndex ];
	
};

/**
 * Get distance between two Cartesian points
 * @param fromCoords
 * @param toCoords
 * @returns
 */
circlePlotViewer.prototype.getDistanceBetweenPoints = function( fromCoords, toCoords ) {
	return Math.sqrt( Math.pow( fromCoords.x - toCoords.x, 2) + Math.pow( fromCoords.x - toCoords.x, 2 ) );
};

/**
 * Maximum possible drawn crosslink distance
 */
circlePlotViewer.prototype.getMaxCrosslinkDistance = function( ) {
	reutrn ( this.getProteinBarRadii().inner - this.CONSTANTS._GAP_BETWEEN_BARS_AND_LINKS ) * 2;
};

/**
 * Whether or not the scale bars are enabled
 * @returns {Boolean}
 */
circlePlotViewer.prototype.isScaleBarSelected  = function() {
	if ( $( "input#show-scalebar" ).is( ':checked' ) ) {
		return true;
	}
	
	return false;
};

/**
 * Whether or not links should be colored by search
 * @returns {Boolean}
 */
circlePlotViewer.prototype.isColorBySearch  = function() {
	var colorBy = $("#color_by").val();
	
	if( colorBy === "search" ) {
		return true;
	}
	
	return false;
};

/**
 * Whether or not feature annotations are being shown
 * @returns {Boolean}
 */
circlePlotViewer.prototype.isFeatureAnnotationShown  = function() {
	
	if( $("#annotation_type").val() ) {
		return true;
	}
	
	return false;
};


/**
 * Get the number of degrees that represents a single residue in the graphic
 * @returns {Number}
 */
circlePlotViewer.prototype.getDegreesPerResidue = function() {
	var selectedProteins = getAllSelectedProteins();
	
	var totalGapDegrees = selectedProteins.length * this.CONSTANTS._GAP_BETWEEN_BARS;
	var workingDegrees = 360 - totalGapDegrees;
	
	var totalProteinLength = 0;
	for( var i = 0; i < selectedProteins.length; i++ ) {
		totalProteinLength += _proteinLengths[ selectedProteins[ i ] ];
	}
	
	// number of degrees per protein residue
	return workingDegrees / totalProteinLength;
};

/**
 * Get the diameter to use for the circle plot. Will use the default diameter set
 * in CONSTANTS, unless automatic sizing is disabled--it will then use the value
 * set in the slider
 */
circlePlotViewer.prototype.getDiameter = function() {
	
	if ( $( "input#automatic-sizing" ).is( ':checked' ) ) {
		return this.CONSTANTS._DEFAULT_DIAMETER;
	} else {
		
		if( this.getUserDiameter() ) {
			return this.getUserDiameter();
		} else {
			return this.CONSTANTS._DEFAULT_DIAMETER;
		}
	}
	
};

/**
 * Get the value set by the user to use for the diameter
 */
circlePlotViewer.prototype.getUserDiameter = function() {
	return this.userDiameter;
};

/**
 * Set the value set by the user to use for the diameter
 */
circlePlotViewer.prototype.setUserDiameter = function( d ) {
	return this.userDiameter = d;
};

