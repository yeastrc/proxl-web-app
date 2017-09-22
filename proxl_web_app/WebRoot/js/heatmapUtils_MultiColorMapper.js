/**
 * heatmapUtils_MultiColorMapper.js
 * 
 * Javascript version of code in Java class MultiColorMapper in heatmapUtils
 * 
 * page variable heatmapUtils_MultiColorMapper
 * 
 */

//JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";


/**
 * Factory 
 * { double dataMin, double dataMax, Color[] colors }
 * 
 * Example call:
 * 
 *  var heatmapUtils_MultiColorMapper = heatmapUtils_MultiColorMapperFactory(  { dataMin : 1, dataMax : 5, colors : [ { red: 81, green: 46, blue: 155 },  { red: 255, green: 246, blue: 11 },  { red: 255, green: 0, blue: 0 } ] } );
 */
var heatmapUtils_MultiColorMapperFactory = function( params ) {
	var _dataMin = params.dataMin;
	var _dataMax = params.dataMax;
	var _colorsParam = params.colors;
	
	if ( _colorsParam === undefined || _colorsParam === null ) {
		throw Error("colors param is not provided");
	}
	if ( ! Array.isArray( _colorsParam ) ) {
		throw Error("colors is not an array");
	}
	if ( _colorsParam.length === 0 ) {
		throw Error("colors array is zero length");
	}
	
	var _colors = [];
		
	//  validate all entries in _colorsParam contains object with properties red, green, and blue
	_colorsParam.forEach( function( color /* element */, i, array) {

		//  Possible code for convert from Hex but has no error checking
//	    hex = hex.replace('#','');
//	    r = parseInt(hex.substring(0,2), 16);
//	    g = parseInt(hex.substring(2,4), 16);
//	    b = parseInt(hex.substring(4,6), 16);
		
		if ( ! ( color.red !== undefined && color.green !== undefined && color.blue !== undefined ) ) {
			throw Error( "element in colors param does not have 'red', 'green' or 'blue' property." );
		}
		
		_colors.push( color );
		
	}, this );
	
	

	return new HeatmapUtils_MultiColorMapper( params );
};


/**
 * Constructor 
 * { double dataMin, double dataMax, Color[] colors }
 */
var HeatmapUtils_MultiColorMapper = function( params ) {
	var _dataMin = params.dataMin;
	var _dataMax = params.dataMax;
	var _colors = params.colors;
	
	var _colorsArrayLength = _colors.length;
	var _dataRange = _dataMax - _dataMin;
	var _step = _dataRange / ( _colorsArrayLength - 1 );


	/**
	 * Get color for value
	 * @param: value
	 */
	this.getColor = function( value ) {
		
		if( value < _dataMin ) { 
			value = _dataMin; 
		} else if( value > _dataMax ) { 
			value = _dataMax; 
		}
		
		var index = Math.floor( (value - _dataMin) / _step );		
		if( index > _colorsArrayLength - 2) { index = _colorsArrayLength - 2; }
		
		var rangeMin = _dataMin + _step * index;
		var rangeMax = _dataMin + _step * (index + 1);
		
		var factor = 1.0 - ( rangeMax - value ) / ( rangeMax - rangeMin );

		var red = _getColorValue ( index, 'red' /* colorsProperty */, factor );
		var green = _getColorValue (index, 'green' /* colorsProperty */, factor );
		var blue = _getColorValue (index, 'blue' /* colorsProperty */, factor );
			
		var outputColor = { red : red, green : green, blue : blue };

		return outputColor;
	}


	/**
	 * Get color part for params (red, green, or blue)
	 */
	var _getColorValue = function( index, colorsProperty, factor ) {
		var start = _colors[ index ][ colorsProperty ];
		var end = _colors[ index + 1 ][ colorsProperty ];
		
		var dj = ( start - end ) * factor;
		
		var color = start - Math.floor( dj );
		
		if ( color < 0 ) {
			color = 0;
		} else if ( color > 255 ) {
			color = 255;
		}
		
		return color;
	};
	
	
};

	