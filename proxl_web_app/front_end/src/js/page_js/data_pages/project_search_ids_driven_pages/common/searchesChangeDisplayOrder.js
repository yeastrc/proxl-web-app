/**
 * searchesChangeDisplayOrder.js
 * 
 * Javascript for the choosing what order searches are shown
 * 
 * Instance object on page:  searchesChangeDisplayOrder
 */

//JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";


/**
 * Constructor
 */
var SearchesChangeDisplayOrder = function( params ) {
	this._folderTemplate_HandlebarsTemplate = undefined;
	this._singleSearchTemplate_HandlebarsTemplate = undefined;
	this.minimumNumberOfSearches = 1;

	this.CONSTANTS = {
			
	};

	/**
	 * 
	 */
	this.init = function( params ) {
		var objectThis = this;
		try {

			var $searches_change_display_order_open_overlay = $("#searches_change_display_order_open_overlay");
			$searches_change_display_order_open_overlay.click( function( eventObject ) {
				try {
					objectThis.openOverlay();
					eventObject.preventDefault();
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			} );
			var $searches_change_display_order_modal_dialog_overlay_hide_parts_jq = $(".searches_change_display_order_modal_dialog_overlay_hide_parts_jq");
			$searches_change_display_order_modal_dialog_overlay_hide_parts_jq.click( function( eventObject ) {
				try {
					objectThis.closeOverlay();
					eventObject.preventDefault();
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			} );
			var $searches_change_display_order_overlay_change_button = $("#searches_change_display_order_overlay_change_button");
			$searches_change_display_order_overlay_change_button.click( function( eventObject ) {
				try {
					objectThis.changeButtonClicked();
					eventObject.preventDefault();
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			} );
			
			//  Compile Handlebars Templates
			var singleSearchTemplate_handlebarsSource = $( "#searches_change_display_order_overlay_search_entry_template" ).text();
			if ( singleSearchTemplate_handlebarsSource === undefined ) {
				throw Error( "singleSearchTemplate_handlebarsSource === undefined" );
			}
			if ( singleSearchTemplate_handlebarsSource === null ) {
				throw Error( "singleSearchTemplate_handlebarsSource === null" );
			}
			this._singleSearchTemplate_HandlebarsTemplate = Handlebars.compile( singleSearchTemplate_handlebarsSource );

			//  Plain text template using "#" for project search id 
			var $searches_change_display_order_overlay_project_search_id_input_template = $("#searches_change_display_order_overlay_project_search_id_input_template");
			if ( $searches_change_display_order_overlay_project_search_id_input_template.length === 0 ) {
				throw Error( "value with id 'searches_change_display_order_overlay_project_search_id_input_template' not on page" );
			}
			this._project_search_id_input_template = $searches_change_display_order_overlay_project_search_id_input_template.text();

			//  Everything on the page so show the button
			var $searches_change_display_order_button_container = $("#searches_change_display_order_button_container")
			$searches_change_display_order_button_container.show();

		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			console.log( e );  
			// Don't rethrow exception since want page initialization to continue so rest of page is usable
		}

	};

	/**
	 * 
	 */
	this.closeOverlay = function( ) {
		$(".searches_change_display_order_modal_dialog_overlay_display_parts_jq").hide()

	};
	
	/**
	 * 
	 */
	this.openOverlay = function( ) {

		this.addSearchesToPage();

		$(".searches_change_display_order_modal_dialog_overlay_display_parts_jq").show()

		var $window = $( window );
		var windowScrollTop = $window.scrollTop();

		//  Position Overlay Vertically
		var overlayNewTop = windowScrollTop + 10;
		//  Apply position to overlay
		var $searches_change_display_order_overlay_div = $("#searches_change_display_order_overlay_div");
		$searches_change_display_order_overlay_div.css( { top : overlayNewTop + "px" } );

		var $searches_change_display_order_list_outer_container = $("#searches_change_display_order_list_outer_container")
		$searches_change_display_order_list_outer_container.css( { height : "0px" } );

		// Get overlay height when search list is empty
		this.select_searches_overlay_div_HeightWithSearchListZeroHeight = $searches_change_display_order_overlay_div.height();

		//  forceAdjustHeight : true - for when first loading searches and folders
		this.setOverlayHeight( { forceAdjustHeight : true } );
	};



	/**
	 *  
	 */
	this.addSearchesToPage = function( params ) {
		var objectThis = this;
		//  Process the Search data and add it to the page
		var $searches_change_display_order_list_container = $("#searches_change_display_order_list_container");
		$searches_change_display_order_list_container.empty();
		
		var $searches_details_list_container = $("#searches_details_list_container")
		
		var $search_list_item_jq_ALL = $searches_details_list_container.find(".search_list_item_jq");

		$search_list_item_jq_ALL.each( function() {
			var $this = $( this );
			var projectSearchId = $this.attr("data-project_search_id");
			var $search_details_name_search_id_string_jq = $this.find(".search_details_name_search_id_string_jq");
			var searchNameAndSearchId = $search_details_name_search_id_string_jq.text();
			
			var templateContext = {
					projectSearchId : projectSearchId,
					searchNameAndSearchId : searchNameAndSearchId
			};
			
			var html = objectThis._singleSearchTemplate_HandlebarsTemplate( templateContext );
			var $addedItem = $( html ).appendTo( $searches_change_display_order_list_container );
		} );

		$searches_change_display_order_list_container.sortable( {
//	    		update : function() {
//	    			objectThis.changeFoldersOrderInDB();
//	    		}
	    } );
		$searches_change_display_order_list_container.disableSelection();		
		
	};



	/**
	 * 
	 */
	this.setOverlayHeight = function( params ) {
		var forceAdjustHeight = undefined;
		if ( params ) {
			forceAdjustHeight = params.forceAdjustHeight;
		}

		var OVERLAY_MINIMUM_HEIGHT = 150;
//		var OVERLAY_MINIMUM_HEIGHT = 50;
		
		var OVERLAY_MAXIMUM_WIDTH = 1200;

		var $window = $( window );
		var viewportHeight = $window.height();
		var viewportWidth = $window.width();

		var $searches_change_display_order_list_outer_container = $("#searches_change_display_order_list_outer_container");
		var $searches_change_display_order_list_container = $("#searches_change_display_order_list_container");
		var $searches_change_display_order_overlay_div = $("#searches_change_display_order_overlay_div");

		var current_searches_list_container_Height = $searches_change_display_order_list_container.height();
		var listMaximumHeight = current_searches_list_container_Height + 5;

		// overlay height when search list is empty
		//  this.select_searches_overlay_div_HeightWithEmptySearchList

//		Set overlay height to viewport - 40px or at minimum height
		var overlayHeight = viewportHeight - 40;
		var overlayHeightDiff = overlayHeight - this.select_searches_overlay_div_HeightWithSearchListZeroHeight;

		var new_current_searches_list_container_Height = overlayHeightDiff;

		if ( new_current_searches_list_container_Height < OVERLAY_MINIMUM_HEIGHT ) {
			new_current_searches_list_container_Height = OVERLAY_MINIMUM_HEIGHT ;
		}
		if ( new_current_searches_list_container_Height > listMaximumHeight ) {
			new_current_searches_list_container_Height = listMaximumHeight ;
		}
		
		var current_searches_change_display_order_list_outer_container = $searches_change_display_order_list_outer_container.height();

		if ( forceAdjustHeight || new_current_searches_list_container_Height > current_searches_change_display_order_list_outer_container ) {
			$searches_change_display_order_list_outer_container.css( { 
				height : new_current_searches_list_container_Height + "px" } );
		}

		//  Set width
		var newContainerWidth = viewportWidth - 40;
		if ( newContainerWidth > OVERLAY_MAXIMUM_WIDTH ) {
			newContainerWidth = OVERLAY_MAXIMUM_WIDTH
		}
		$searches_change_display_order_overlay_div.css( { width: newContainerWidth + "px", } );
	};


	/**
	 * Change Button clicked
	 */
	this.changeButtonClicked = function() {
		var objectThis = this;

		var $searches_change_display_order_list_container= $("#searches_change_display_order_list_container");
		
		var projectSearchIdsInNewOrder = [];
		
		var $search_display_order_item_jqAll = $searches_change_display_order_list_container.find(".search_display_order_item_jq")
		$search_display_order_item_jqAll.each( function( ){
			var $this = $( this );
			var projectSearchId = $this.attr("data-project_search_id");
			projectSearchIdsInNewOrder.push( projectSearchId );
		} ) ;
		

		//   Special case for Image Page
		if ( window.imageViewerPageObject ) {
			//   imageViewerPageObject object on window object so call this instead of the code below
			imageViewerPageObject.changeProjectSearchIdOrderInURL( { projectSearchIdsInNewOrder : projectSearchIdsInNewOrder } );
			return;  //  EARLY EXIT
		}

		//   Special case for Structure Page
		if ( window.structureViewerPageObject ) {
			//   imageViewerPageObject object on window object so call this instead of the code below
			structureViewerPageObject.changeProjectSearchIdOrderInURL( { projectSearchIdsInNewOrder : projectSearchIdsInNewOrder } );
			return;  //  EARLY EXIT
		}

		//   Special case for Merged QC Page
		
		if ( window.qcMergedPageMain ) {
			
			//   qcMergedPageMain object on window object so call this instead of the code below
			
			qcMergedPageMain.changeProjectSearchIdOrderInURL( { projectSearchIdsInNewOrder : projectSearchIdsInNewOrder } );
			
			return;  //  EARLY EXIT
		}
		
		
		this.updatePageToNewProjectSearchIdsOrder( projectSearchIdsInNewOrder );
	};
	

	/**
	 * 'update': This function is called when the user stopped sorting and the DOM position has changed.
	 * (User released the item they were dragging and items are now in "after sort" order)
	 */
	this.updatePageToNewProjectSearchIdsOrder = function( projectSearchIdsInNewOrder ) {


		//  Update order of search ids and submit form

		//  Plain text template using "#" for project search id 
		var $searches_for_page_chooser_overlay_project_search_id_input_template = $("#searches_for_page_chooser_overlay_project_search_id_input_template");
		if ( $searches_for_page_chooser_overlay_project_search_id_input_template.length === 0 ) {
			throw Error( "value with id 'searches_for_page_chooser_overlay_project_search_id_input_template' not on page" );
		}
//		this._project_search_id_input_template = $searches_for_page_chooser_overlay_project_search_id_input_template.text();
		var project_search_id_input_template = $searches_for_page_chooser_overlay_project_search_id_input_template.text();


		var $query_param_do_not_sort_yes_input_template = $("#query_param_do_not_sort_yes_input_template");
		if ( $query_param_do_not_sort_yes_input_template.length === 0 ) {
			throw Error( "value with id 'query_param_do_not_sort_yes_input_template' not on page" );
		}
		var query_param_do_not_sort_yes_input_template = $query_param_do_not_sort_yes_input_template.text();


		var $form_get_for_updated_parameters_multiple_searches = $("#form_get_for_updated_parameters_multiple_searches");

		var $query_param_do_not_sort_in_update_form_jq = $form_get_for_updated_parameters_multiple_searches.find(".query_param_do_not_sort_in_update_form_jq");
		$query_param_do_not_sort_in_update_form_jq.remove(); // Removes existing do not search input field

		//  Add new do not search input field
		var $addedItem = 
			$( query_param_do_not_sort_yes_input_template ).prependTo( $form_get_for_updated_parameters_multiple_searches );


		var $project_search_id_in_update_form_jq_All = $form_get_for_updated_parameters_multiple_searches.find(".project_search_id_in_update_form_jq");
		$project_search_id_in_update_form_jq_All.remove(); // Removes all existing projectSearchId input fields

		//  Add in reverse since 'prependTo' since want to have query JSON at end of URL
		for ( var index = projectSearchIdsInNewOrder.length - 1; index >= 0; index-- ) {
			var projectSearchId = projectSearchIdsInNewOrder[ index ];
			var inputFieldForProjectSearchIdHTML = project_search_id_input_template.replace( "#", projectSearchId );
			var $addedItem = 
				$( inputFieldForProjectSearchIdHTML ).prependTo( $form_get_for_updated_parameters_multiple_searches );
		}

		$form_get_for_updated_parameters_multiple_searches.submit();

	};

	/**
	 * Get project_search_id_jq from page as object where ids are properties and value is true
	 */
	this.getProjectSearchIdsForCurrentPage = function() {
		var $project_search_id_jq_All = $(".project_search_id_jq");
		if ( $project_search_id_jq_All.length === 0 ) {
			throw Error( "No elements with class 'project_search_id_jq' on page" );
		}
		var projectSearchIdsForCurrentPageAsArray = [];
		var projectSearchIdsForCurrentPageAsObject = {};
		$project_search_id_jq_All.each(function() {
			var $this = $( this );
			var projectSearchIdForCurrentPage = $this.val();
			projectSearchIdsForCurrentPageAsArray.push( projectSearchIdForCurrentPage );
			projectSearchIdsForCurrentPageAsObject[ projectSearchIdForCurrentPage ] = true;
		});
		projectSearchIdsForCurrentPageAsArray.sort();
		var projectSearchIdsForCurrentPage = { 
				asArray : projectSearchIdsForCurrentPageAsArray, 
				asObject : projectSearchIdsForCurrentPageAsObject
		};
		return projectSearchIdsForCurrentPage;
	};
};

/**
 * 
 */
var searchesChangeDisplayOrder = new SearchesChangeDisplayOrder();

window.searchesChangeDisplayOrder = searchesChangeDisplayOrder;

export { searchesChangeDisplayOrder }
