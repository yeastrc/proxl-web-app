/**
 * genericToolTip.js
 * 
 * Javascript for Generic tool tip  
 * 
 * 
 */	


import { reportWebErrorToServer } from 'page_js/header_section_js_all_pages_main_pages/header_section_every_page/reportWebErrorToServer';

if ( ! window.genericToolTip_js_Initialized ) {

	$(document).ready(function() {
		
		try {

			addToolTips();
			
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}

	});
	
	window.genericToolTip_js_Initialized = true;
	
}

//  Just define these functions again and attach to window

	
var addToolTips = function ( $element ) {

		var $elements;
		
		if( $element != undefined ) {
			$elements = $element.find( ".tool_tip_attached_jq" );
			//console.log($element);
			//console.log($elements);
		} else {
			$elements = $(".tool_tip_attached_jq" );
		}
		
		$elements.each( function(  ) {
			
			var $this = $( this );
	
			//console.log( "Adding handler to:" );
			//console.log( $this );
		
			addSingleGenericProxlToolTip( $this );

		});
	};
	

var addSingleGenericProxlToolTip = function ( $element ) {
		
		var tooltipText = $element.attr("data-tooltip");			
		
		$element.qtip( {
	        content: {
	            text: tooltipText
	        },
			position: {
				target: 'mouse',
				adjust: { x: 5, y: 5 }, // Offset it slightly from under the mouse
	            viewport: $(window)
	         }
	    });		
		
	};
	
window.addToolTips = addToolTips;
window.addSingleGenericProxlToolTip = addSingleGenericProxlToolTip;

export { addToolTips, addSingleGenericProxlToolTip }
