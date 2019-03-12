var SPINNER_OPTIONS = {
		  lines: 13, // The number of lines to draw
		  length: 20, // The length of each line
		  width: 10, // The line thickness
		  radius: 30, // The radius of the inner circle
		  corners: 1, // Corner roundness (0..1)
		  rotate: 0, // The rotation offset
		  direction: 1, // 1: clockwise, -1: counterclockwise
		  color: '#000', // #rgb or #rrggbb or array of colors
		  speed: 1, // Rounds per second
		  trail: 60, // Afterglow percentage
		  shadow: false, // Whether to render a shadow
		  hwaccel: false, // Whether to use hardware acceleration
		  className: 'spinner', // The CSS class to assign to the spinner
		  zIndex: 2e9, // The z-index (defaults to 2000000000)
		  top: 'auto', // Top position relative to parent in px
		  left: 'auto' // Left position relative to parent in px
		};


var loadingSpinner;

var createSpinner = function () {

	var $spinnerContainer = $( "div#coverage-map-loading-spinner-block" );

	$spinnerContainer.show();

	var $spinnerDiv = $("div#coverage-map-loading-spinner");
	var spinnerDiv = $spinnerDiv.get( 0 );

	loadingSpinner = new Spinner( SPINNER_OPTIONS ).spin( spinnerDiv );

	return loadingSpinner;
};

window.createSpinner = createSpinner;


window.destroySpinner = function() {
	
	if ( loadingSpinner ) {
		loadingSpinner.stop();
	}
	
	loadingSpinner = undefined;

	var $spinnerContainer = $("div#coverage-map-loading-spinner-block");

	$spinnerContainer.hide();

};

window.incrementSpinner = function() {
	if( !loadingSpinner || !loadingSpinner.numSpinners ) {
		createSpinner();
		loadingSpinner.numSpinners = 1;
	} else {
		loadingSpinner.numSpinners += 1;
	}	
};

window.decrementSpinner = function() {
	if ( ! loadingSpinner ) { return; }
	var numSpinners = loadingSpinner.numSpinners;
	if( !numSpinners ) { return; }
	
	numSpinners--;
	
	if( !numSpinners ) {
		destroySpinner();
	}
	if( numSpinners ) {
		loadingSpinner.numSpinners = numSpinners;
	}
	
};

export { createSpinner } // export something for import to get onto page
