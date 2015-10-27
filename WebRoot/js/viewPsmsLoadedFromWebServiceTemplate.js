
//   viewPsmsLoadedFromWebServiceTemplate.js




//   Process and load data into the file viewPsmsLoadedFromWebServiceTemplateFragment.jsp


//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";


//   Class contructor

var ViewPsmsLoadedFromWebServiceTemplate = function() {

	var _DATA_LOADED_DATA_KEY = "dataLoaded";
	
	var _psm_block_template = null;
	var _handlebarsTemplate_psm_entry_template = null;
	
	
	this.showHidePsms = function( params ) {
		
		var clickedElement = params.clickedElement;
		
		var $clickedElement = $( clickedElement );
		
		var $itemToToggle = $clickedElement.next();



		if( $itemToToggle.is(":visible" ) ) {

			$itemToToggle.hide(); 

			$clickedElement.find(".toggle_visibility_expansion_span_jq").show();
			$clickedElement.find(".toggle_visibility_contraction_span_jq").hide();
		} else { 
			$itemToToggle.show();

			$clickedElement.find(".toggle_visibility_expansion_span_jq").hide();
			$clickedElement.find(".toggle_visibility_contraction_span_jq").show();

			this.loadAndInsertPsmsIfNeeded( { $topTRelement : $itemToToggle, $clickedElement : $clickedElement } );
		}

		return false;  // does not stop bubbling of click event
	};
	
		
	
	////////////////////////
	
	this.loadAndInsertPsmsIfNeeded = function( params ) {

		var objectThis = this;
		
		var $topTRelement = params.$topTRelement;
		var $clickedElement = params.$clickedElement;
		

		var dataLoaded = $topTRelement.data( _DATA_LOADED_DATA_KEY );
		
		if ( dataLoaded ) {
			
			return;  //  EARLY EXIT  since data already loaded. 
		}
		

		
		var reported_peptide_id = $clickedElement.attr( "reported_peptide_id" );
		var search_id = $clickedElement.attr( "search_id" );
		var project_id = $clickedElement.attr( "project_id" );
		var psm_q_value_cutoff = $clickedElement.attr( "psm_q_value_cutoff" );

		
		//  Convert all attributes to empty string if null or undefined
		if ( ! reported_peptide_id ) {
			reported_peptide_id = "";
		}
		if ( ! search_id ) {
			search_id = "";
		}
		if ( ! project_id ) {
			project_id = "";
		}
		if ( ! psm_q_value_cutoff ) {
			psm_q_value_cutoff = "";
		}
				
		
		var ajaxRequestData = {

				reported_peptide_id : reported_peptide_id,
				search_id : search_id,
				project_id : project_id,
				psm_q_value_cutoff : psm_q_value_cutoff
		};
		
		
		$.ajax({
			url : contextPathJSVar + "/services/data/getPsms",

//			traditional: true,  //  Force traditional serialization of the data sent
//								//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
//								//   So searchIds array is passed as "searchIds=<value>" which is what Jersey expects
			
			data : ajaxRequestData,  // The data sent as params on the URL
			dataType : "json",

			success : function( ajaxResponseData ) {

				objectThis.loadAndInsertPsmsResponse( { ajaxResponseData : ajaxResponseData, $topTRelement : $topTRelement } );
				

				$topTRelement.data( _DATA_LOADED_DATA_KEY, true );
				
			},
	        failure: function(errMsg) {
	        	handleAJAXFailure( errMsg );
	        },

			error : function(jqXHR, textStatus, errorThrown) {

				handleAJAXError(jqXHR, textStatus, errorThrown);

			}
		});
		
		
		
	};
	
	
	this.loadAndInsertPsmsResponse = function( params ) {
		
		var ajaxResponseData = params.ajaxResponseData;
		
	//	var ajaxRequestData = params.ajaxRequestData;

		var $topTRelement = params.$topTRelement;
		
		var $psm_data_container = $topTRelement.find(".child_data_container_jq");
		
		if ( $psm_data_container.length === 0 ) {
			
			throw "unable to find HTML element with class 'child_data_container_jq'";
		}

		$psm_data_container.empty();
		
		if ( _psm_block_template === null ) {
			
			//  Not a handlebars template so just use the html
			_psm_block_template = $( "#psm_block_template" ).html();
		}
	
		if ( _handlebarsTemplate_psm_entry_template === null ) {

			var handlebarsSource_psm_entry_template = $( "#psm_entry_template tbody" ).html(); //  " tbody" since it is a table

			if ( handlebarsSource_psm_entry_template === undefined ) {
				throw "handlebarsSource_psm_entry_template === undefined";
			}
			if ( handlebarsSource_psm_entry_template === null ) {
				throw "handlebarsSource_psm_entry_template === null";
			}
			
			_handlebarsTemplate_psm_entry_template = Handlebars.compile( handlebarsSource_psm_entry_template );
		}
		
		
		
	
		var $psm_block_template = $(_psm_block_template).appendTo( $psm_data_container );  ///////////////////////////
		
		var $psm_table_jq = $psm_block_template.find(".psm_table_jq");
	
	//			var $psm_table_jq = $psm_data_container.find(".psm_table_jq");
		
		if ( $psm_table_jq.length === 0 ) {
			
			throw "unable to find HTML element with class 'psm_table_jq'";
		}
		
		var psms = ajaxResponseData;
	
		var percolatorPsmFound = false;
		
		//  Add psm data to the page
	
		for ( var psmIndex = 0; psmIndex < psms.length ; psmIndex++ ) {
	
			var psm = psms[ psmIndex ];
	
			if (  psm.chargeSet ) {
				
				psm.chargeDisplay = psm.charge;
			}
			
			if ( psm.psmDTO.percolatorPsm ) {
				
				percolatorPsmFound = true;
			}
			
			var context = psm;
	
			var html = _handlebarsTemplate_psm_entry_template(context);
	
	//		var $psm_entry = 
			$(html).appendTo($psm_table_jq);
		}
		
		if ( ! percolatorPsmFound ) {
			
			//  Remove percolatorPsm columns across the table since no data found
			var $percolatorPsm_columns_jq = $psm_table_jq.find(".percolatorPsm_columns_jq");
			$percolatorPsm_columns_jq.remove();
		}
		
		var $openLorkeetLinks = $(".view_spectrum_open_spectrum_link_jq");
		
		addOpenLorikeetViewerClickHandlers( $openLorkeetLinks );
	
		//  Does not seem to work so not run it
//		if ( psms.length > 0 ) {
//			
//			try {
//				$psm_block_template.tablesorter(); // gets exception if there are no data rows
//			} catch (e) {
//				
//				var z = 0;
//			}
//		}

		
		if ( window.linkInfoOverlayWidthResizer ) {
			
			window.linkInfoOverlayWidthResizer();
		}
		
	};
	
};



//Static Singleton Instance of Class

var viewPsmsLoadedFromWebServiceTemplate = new ViewPsmsLoadedFromWebServiceTemplate();