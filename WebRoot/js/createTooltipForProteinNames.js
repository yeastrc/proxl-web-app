
//   createTooltipForProteinNames.js

//  /js/createTooltipForProteinNames.js

//  Javascript for adding tooltip with PDR info to the protein names

//   Search for this JS filename for companion Java class that must be called for every Action for pages that
//   this JS file is included on.
 
//		<script type="text/javascript" src="${ contextPath }/js/createTooltipForProteinNames.js"></script>

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";


////////////////////////////


var _protein_listing_webservice_base_url_set = null;

var _searchIds_protein_listing = null;


$(document).ready(function() { 

	setTimeout( function() { // put in setTimeout so it doesn't run on the main page init thread

		createTooltipForProteinNames();
	},10);

	
});


var createTooltipForProteinNames = function() {
	
	_protein_listing_webservice_base_url_set = $("#protein_listing_webservice_base_url_set").val();
	
	if ( _protein_listing_webservice_base_url_set === undefined 
			|| _protein_listing_webservice_base_url_set === null
			|| _protein_listing_webservice_base_url_set === "" ) {
		
		return;  //  EARLY exit since no URL configured for protein listings
	}

	_searchIds_protein_listing = [];
	
	$(".search_id_input_field_protein_listing_tooltip_jq").each( function() {
		
		var $this = $( this );
		
		var searchId = $this.val();
		
		_searchIds_protein_listing.push( searchId );
	});
	

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
	

	_protein_listing_webservice_base_url_set = $("#protein_listing_webservice_base_url_set").val();
	
	if ( _protein_listing_webservice_base_url_set === undefined 
			|| _protein_listing_webservice_base_url_set === null
			|| _protein_listing_webservice_base_url_set === "" ) {
		
		return;  //  EARLY exit since no URL configured for protein listings
	}
	
	if ( _searchIds_protein_listing === null ) {

		_searchIds_protein_listing = [];

		$(".search_id_input_field_protein_listing_tooltip_jq").each( function() {
			
			var $this = $( this );
			
			var searchId = $this.val();
			
			_searchIds_protein_listing.push( searchId );
		});
	}
	
	
	var  $elementToAddToolTipTo = params.$elementToAddToolTipTo;
	
	var proteinDisplayName = params.proteinDisplayName;
	
	var tooltipDelay = params.tooltipDelay;    //  milliseconds
	
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
                	  
                	  var mainProteinDataFormattedHTML = __proteinDataForToolTipFormattedHTMLCache[ proteinIdString ];
                	  
                	  if ( mainProteinDataFormattedHTML !== undefined ) {
                		  
        				  if ( proteinDisplayName ) {
        					  
        					  //  Prepend Protein Display name
        					  
        					  var HTML_Addition_DisplayedProteinName =
        						  _addSingleTooltipForProteinName_HTML_Addition_DisplayedProteinName( { displayedProteinName : proteinDisplayName } );
        							
        					  mainProteinDataFormattedHTML = 
        						  HTML_Addition_DisplayedProteinName + mainProteinDataFormattedHTML;
        				  }
        				  
                		  return mainProteinDataFormattedHTML;  //   EARLY Function RETURN
                	  
                	  } else {

                		  if ( _protein_listing_webservice_base_url_set !== undefined 
                				  && _protein_listing_webservice_base_url_set !== null
                				  && _protein_listing_webservice_base_url_set !== "" ) {

                				var requestData = {

                						searchId : _searchIds_protein_listing,
                						proteinSequenceId : proteinIdString
                				};

                			  var _URL = contextPathJSVar + "/services/proteinNameForTooltip/getData";

                			  $.ajax({
                					type : "GET",
                					url : _URL,
                					data : requestData,
                					dataType : "json",

                					traditional: true  //  Force traditional serialization of the data sent
                					//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
                					//   So proteinIdsToGetSequence array is passed as "proteinIdsToGetSequence=<value>" which is what Jersey expects

                			  })
                			  .done(function(data) {


                				  var mainProteinDataFormattedHTML = "<div style=\"margin-bottom:10px;\" class=\"isTooltip\">" 
                					  + "<span class='is-tooltip-label'>Source:</span> " + data.source + "</div>" 
                					  + "<div style=\"margin-bottom:10px;\" class=\"isTooltip\">" 
                					  + "<span class='is-tooltip-label'>Name:</span> " + data.name + "</div>";

                				  if ( data.description !== undefined && data.description !== null ) {
                					  output += "<div style=\"margin-bottom:10px;\" class=\"isTooltip\">" 
                					  		+ "<span class='is-tooltip-label'>Description:</span> " + data.description + "</div>";
                				  }

                				  __proteinDataForToolTipFormattedHTMLCache[ proteinIdString ] = mainProteinDataFormattedHTML;
                				  

                				  var output = mainProteinDataFormattedHTML;

                				  if ( proteinDisplayName ) {

                					  var HTML_Addition_DisplayedProteinName =
                						  _addSingleTooltipForProteinName_HTML_Addition_DisplayedProteinName( { displayedProteinName : proteinDisplayName } );
                							
                					  output = 
                						  HTML_Addition_DisplayedProteinName + mainProteinDataFormattedHTML;
                					  
                				  }
                				  

                				  api.set('content.text', output);
                			  });

                			  return 'Loading listing information...'; // Set some initial text
                		  
                		  } else {
                		  
                			  return 'No listing information available';  //  Shouldn't get here
                		  }
                	  }
                  }
              },
              position: tipPosition,
              show: {
            	  delay: tooltipDelay  //  milliseconds
              }
                      
          });
	
};


// tool tip HTML Addition to add the displayed protein name 

var _addSingleTooltipForProteinName_HTML_Addition_DisplayedProteinName = function ( params ) {
	
	var displayedProteinName = params.displayedProteinName;
	
	var html = "<div style=\"margin-bottom:10px;\" class=\"isTooltip\">"
	  + "<span class='is-tooltip-label'>Displayed name:</span> " 
	  + displayedProteinName 
	  + "</div>"
	  + "<div style=\"margin-bottom:10px;\" class=\"isTooltip\">"
	  + "<span class='is-tooltip-label'>From YRC PDR:</span> " 
	  + "</div>";
	
	return html;
};



