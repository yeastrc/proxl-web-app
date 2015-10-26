
//   createTooltipForProteinNames.js

//  /js/createTooltipForProteinNames.js

//  Javascript for adding tooltip with PDR info to the protein names
 
//		<script type="text/javascript" src="${ contextPath }/js/createTooltipForProteinNames.js"></script>

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";


////////////////////////////



$(document).ready(function() { 
 
	  setTimeout( function() { // put in setTimeout so it doesn't run on the main page init thread
		
		  createTooltipForProteinNames();
	  },10);
	
	
});


var createTooltipForProteinNames = function() {
	
    setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else
  	
    	
    	
    	$('.proteinName').each(function() {
    		
    		var $elementToAddToolTipTo =  $(this);
    		

    		var htmlIdString = $elementToAddToolTipTo.attr( "id" );

    		var proteinIdString = htmlIdString.replace( "protein-id-", "" );
    		
    		
    		
    		addSingleTooltipForProteinNameOnTopRight( {$elementToAddToolTipTo: $elementToAddToolTipTo, proteinIdString: proteinIdString } );
        });


    },50);



};


var __proteinDataForToolTipFormattedHTMLCache = {};


//   Add the tool tip to display on the top right of the element

var addSingleTooltipForProteinNameOnTopRight = function( params ) {
	

	params.tipPosition = {
            my: 'bottom left',
            at: 'top right',
            viewport: $(window)
         };
	
	addSingleTooltipForProteinName( params );
};


/////////////   This does not work.  When the text is updated after the AJAX call, the position switches to bottom right.

//Add the tool tip to display on the bottom middle of the element

//var addSingleTooltipForProteinNameOnBottomMiddle = function( params ) {
//
//
//params.tipPosition = {
//       my: 'top center',
//       at: 'bottom center',
//       viewport: $(window)
//    };
//
//	addSingleTooltipForProteinName( params );
//};



var addSingleTooltipForProteinName = function( params ) {
	
	var  $elementToAddToolTipTo = params.$elementToAddToolTipTo;
	
	var tipPosition = params.tipPosition;
	
	if ( tipPosition === undefined || tipPosition === null ) {
		
		//  Default to position tool tip on top right corner of element
		
		tipPosition = {
	            my: 'bottom left',
	            at: 'top right',
	            viewport: $(window)
	         };
		
	}
	
	var proteinIdString = params.proteinIdString;


	$elementToAddToolTipTo.qtip({
        	  
              content: {
            	  
                  text: function(event, api) {
                	  
                	  var proteinDataFormattedHTM = __proteinDataForToolTipFormattedHTMLCache[ proteinIdString ];
                	  
                	  if ( proteinDataFormattedHTM !== undefined ) {
                		  
                		  return proteinDataFormattedHTM;  //   EARLY Function RETURN
                	  
                	  } else {

                		  var url = "http://www.yeastrc.org/pdr/services/proteinListingService/jsonp/" + proteinIdString + "?jsonpCallback=?";

                		  $.ajax({
                			  url: url,
                			  dataType: 'jsonp'
                		  })
                		  .done(function(data) {

                			  var output = "<div style=\"margin-bottom:10px;\" class=\"isTooltip\">Source: " + data.source + "</div>" 
                			  + "<div style=\"margin-bottom:10px;\" class=\"isTooltip\">Name: " + data.name + "</div>";

                			  if ( data.description !== undefined && data.description !== null ) {
                				  output += "<div style=\"margin-bottom:10px;\" class=\"isTooltip\">Description: " + data.description + "</div>";
                			  }

                			  __proteinDataForToolTipFormattedHTMLCache[ proteinIdString ] = output;

                			  api.set('content.text', output);
                		  });

                		  return 'Loading from PDR...'; // Set some initial text
                	  }
                  }
              },
              position: tipPosition
          });
	
};


