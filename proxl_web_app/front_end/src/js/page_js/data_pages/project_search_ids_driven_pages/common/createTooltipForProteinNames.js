
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
		try {
			createTooltipForProteinNames();
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
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
		try {
			$('.proteinName').each(function() {
				var $elementToAddToolTipTo =  $(this);
				var htmlIdString = $elementToAddToolTipTo.attr( "id" );
				var proteinIdString = htmlIdString.replace( "protein-id-", "" );
				addSingleTooltipForProteinNameOnTopRight( {$elementToAddToolTipTo: $elementToAddToolTipTo, proteinIdString: proteinIdString } );
			});
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
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
//params.tipPosition = {
//       my: 'top center',
//       at: 'bottom center',
//       viewport: $(window)
//    };
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
                						proteinSequenceVersionId : proteinIdString
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
                			  .done( function( data ) {
                				  try {
                					  //  first, show any "local" descriptions for this protein
                					  var descriptionCount = 0;
                					  if( data.annotations && data.annotations.length > 0 ) {
                						  for( var i = 0; i < data.annotations.length; i++ ) {
                							  var annotation = data.annotations[ i ];
                							  if( annotation.description != null && annotation.description.length > 0 ) {
                								  descriptionCount++;
                							  }         							  
                						  }
                					  }
                					  var mainProteinDataFormattedHTML =  "";
                					  if( descriptionCount === 0 ) {
                						  mainProteinDataFormattedHTML += "<div style=\"margin-bottom:10px;\" class=\"isTooltip\"><span class='is-tooltip-label'>No description uploaded to Proxl.</span></div>";
                					  } else {
                						  mainProteinDataFormattedHTML += "<div style=\"margin-bottom:10px;\" class=\"isTooltip\">";
                    					  if( data.annotations && data.annotations.length > 0 ) {
                    						  mainProteinDataFormattedHTML += "<div style=\"margin-bottom:10px;\" class=\"isTooltip\">";
                    						  mainProteinDataFormattedHTML += "<span class='is-tooltip-label'>Description(s) uploaded to ProXL:</span> ";
                    						  mainProteinDataFormattedHTML += "</div>";
                    						  for( var i = 0; i < data.annotations.length; i++ ) {
                    							  var annotation = data.annotations[ i ];
                        						  mainProteinDataFormattedHTML += "<div style=\"margin-bottom:10px;margin-left:10px;\" class=\"isTooltip\">";
                        						  mainProteinDataFormattedHTML += "<span>" + annotation.description + "</span> ";
                        						  mainProteinDataFormattedHTML += "</div>";
                    						  }
                    					  }
                						  mainProteinDataFormattedHTML += "</div>";
                					  }
                					  if ( data.dataFound ) {
                						  mainProteinDataFormattedHTML += 
                							  "<div style=\"margin-bottom:10px;\" class=\"isTooltip\">"
                							  + "<span class='is-tooltip-label'>From YRC PDR:</span> " 
                							  + "</div>"
                							  + "<div style=\"margin-bottom:5px;margin-left:10px;\" class=\"isTooltip\"><span>Source:</span> " + data.source + "</div>" 
                							  + "<div style=\"margin-bottom:5px;margin-left:10px;\" class=\"isTooltip\"><span>Name:</span> " + data.name + "</div>";
                						  if ( data.description !== undefined && data.description !== null ) {
                							  mainProteinDataFormattedHTML += "<div style=\"margin-bottom:5px;margin-left:10px;\" class=\"isTooltip\"><span>Description:</span> " + data.description + "</div>";
                						  } else {
                							  mainProteinDataFormattedHTML += "<div style=\"margin-bottom:5px;margin-left:10px;\" class=\"isTooltip\"><span>Description:</span> Not found.</div>"
                						  }
                					  } else {
                						  mainProteinDataFormattedHTML += "<div style=\"margin-bottom:10px;\" class=\"isTooltip\"><span class='is-tooltip-label'>No data found in YRC PDR.</span></div>";
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
                					} catch( e ) {
                						reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
                						throw e;
                					}
                			  } );
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
	  + "<span class='is-tooltip-label'>Full name:</span> " 
	  + displayedProteinName 
	  + "</div>";
	return html;
};
