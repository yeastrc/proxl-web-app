	
//  Generic tool tip

	$(document).ready(function() {
		
		try {

			addToolTips();
			
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}

	});
	
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