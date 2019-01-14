/**
 * genericToolTip.js
 * 
 * Javascript for Generic tool tip  
 * 
 * 
 */	


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

	
	window.addToolTips = function ( $element ) {

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
	

	window.addSingleGenericProxlToolTip = function ( $element ) {
		
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

export { addToolTips, addSingleGenericProxlToolTip }
