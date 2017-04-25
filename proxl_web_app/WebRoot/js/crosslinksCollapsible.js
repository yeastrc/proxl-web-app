//
//  crosslinksCollapsible.js
// 
//       Manages collapse and show for <div class="collapsable_container_jq" > and it's children
//

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";



///////////////

$(document).ready(function() {

	addCollapsableEventHandlers();  // Run with no parameter
});

var addCollapsableEventHandlers = function( $searchRoot ) {
	
	try {

		var $collapsable_collapse_link_jq = null;
		var $collapsable_expand_link_jq = null;
		
		if ( $searchRoot ) {
			
			$collapsable_collapse_link_jq = $searchRoot.find(".collapsable_collapse_link_jq");
			$collapsable_expand_link_jq = $searchRoot.find(".collapsable_expand_link_jq")
		} else {
			$collapsable_collapse_link_jq = $(".collapsable_collapse_link_jq");
			$collapsable_expand_link_jq = $(".collapsable_expand_link_jq")
		}
		
		$collapsable_collapse_link_jq.click( function(eventObject) {

			try {

				var $this = $( this );
				var $collapsable_container_jq = $this.closest(".collapsable_container_jq");

				var $collapsable_jq = $collapsable_container_jq.children(".collapsable_jq");

				var $collapsable_expand_link_jq = $collapsable_container_jq.children(".collapsable_expand_link_jq");

				if ( $collapsable_expand_link_jq.length === 0 ) {

					var $collapsable_link_container_jq = $collapsable_container_jq.children(".collapsable_link_container_jq");
					$collapsable_expand_link_jq = $collapsable_link_container_jq.children(".collapsable_expand_link_jq");

				}

				$this.hide();
				$collapsable_jq.hide();
				$collapsable_expand_link_jq.show();

				if ( $collapsable_expand_link_jq.length === 0 ) {

					throw Error( "Unable to find class=\"collapsable_jq\" to hide" );
				}

				if ( $collapsable_expand_link_jq.length === 0 ) {

					throw Error( "Unable to find expand link" );
				}
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});

		$collapsable_expand_link_jq.click( function(eventObject) {

			try {

				var $this = $( this );
				var $collapsable_container_jq = $this.closest(".collapsable_container_jq");

				var $collapsable_jq = $collapsable_container_jq.children(".collapsable_jq");

				var $collapsable_collapse_link_jq = $collapsable_container_jq.children(".collapsable_collapse_link_jq");

				if ( $collapsable_collapse_link_jq.length === 0 ) {

					var $collapsable_link_container_jq = $collapsable_container_jq.children(".collapsable_link_container_jq");
					$collapsable_collapse_link_jq = $collapsable_link_container_jq.children(".collapsable_collapse_link_jq");

				}

				$this.hide();
				$collapsable_jq.show();
				$collapsable_collapse_link_jq.show();

				if ( $collapsable_collapse_link_jq.length === 0 ) {

					throw Error( "Unable to find class=\"collapsable_jq\" to show" );
				}

				if ( $collapsable_collapse_link_jq.length === 0 ) {

					throw Error( "Unable to find collapse link" );
				}
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}

		});
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}

};
