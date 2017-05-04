/**
 * mergedPeptideProteinSearchesListVennDiagramSection.js
 * 
 * Javascript for the mergedPeptideProteinSearchesListVennDiagramSection.jsp include block
 * 
 * Page variable mergedPeptideProteinSearchesListVennDiagramSection
 */

/**
 * Constructor
 */
var MergedPeptideProteinSearchesListVennDiagramSection = function() {
	
	/**
	 * init
	 */
	this.init = function() {
		var objectThis = this;

		setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else
			try {
				//  Make searches in search details and next to Venn Diagram draggable
				var $searches_details_list_container = $(".searches_sort_list_container_jq");
				if ( $searches_details_list_container.length === 0 ) {
					throw Error( "No HTML element found with id 'searches_details_list_container'" );
				}
				$searches_details_list_container.sortable( {
					// Sort handle
					handle: ".search_sort_handle_jq",
					//  'start':  On sort start, call this function
					start : function( event, ui ) {
						objectThis.processSortStartSearchesInSearchDetails( event, ui )
					},
					//  'update': This event is triggered when the user stopped sorting and the DOM position has changed.
					//        (User released the item they were dragging and items are now in "after sort" order)
					update : function( event, ui ) {
						objectThis.processSortUpdateSearchesInSearchDetails( event, ui )
					},
					// Added to ensure click event doesn't fire in Firefox
					helper:'clone'
				} );
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		},10);
	}

	/**
	 * 'start':  On sort start, call this function
	 */
	this.processSortStartSearchesInSearchDetails = function( event, ui ) {
		var $item = ui.item;
		//  ui.item is  <table class="table-no-border-no-cell-spacing-no-cell-padding  searches_details_list_item_jq  
		var $search_sort_handle_jq = $item.find(".search_sort_handle_jq");
		$search_sort_handle_jq.qtip('toggle', false); // Immediately hide all tooltips belonging to the selected elements
//		var $searches_details_list_container = $("#searches_details_list_container");
//		$searches_details_list_container.qtip('toggle', false); // Immediately hide all tooltips belonging to the selected elements
	};

	//  'update': This event is triggered when the user stopped sorting and the DOM position has changed.
	//        (User released the item they were dragging and items are now in "after sort" order)

	/**
	 * 'update': This function is called when the user stopped sorting and the DOM position has changed.
	 * (User released the item they were dragging and items are now in "after sort" order)
	 */
	this.processSortUpdateSearchesInSearchDetails = function( event, ui ) {
		var $item = ui.item;
//		var project_search_id = $item.attr( "data-project_search_id" );	// the project_search_id of the search instance we moved

		var projectSearchIdsInNewOrder = [];

		var $searches_sort_list_container_jq = $item.closest(".searches_sort_list_container_jq");
		var $searches_sort_list_item_jq_All = $searches_sort_list_container_jq.find(".searches_sort_list_item_jq");
		if ( $searches_sort_list_item_jq_All.length === 0 ) {
			throw Error( "No HTML elements found with class 'searches_sort_list_item_jq' under element with class 'searches_sort_list_container_jq'" );
		}
		$searches_sort_list_item_jq_All.each( function( index, element ) {
			var searches_sort_list_item = this;
			var $searches_sort_list_item = $( searches_sort_list_item );
//			var search_order_indexString = $searches_sort_list_item.attr("data-search_order_index");
//			var search_order_indexInt = Number( search_order_indexString );
//			if ( isNaN( search_order_indexInt ) ) {
//			throw Error( "On HTML element with class 'searches_sort_list_item_jq' the attribute 'data-search_order_index' is not a number" );
//			}
			var project_search_id = $searches_sort_list_item.attr("data-project_search_id");
			projectSearchIdsInNewOrder.push( project_search_id );
		});

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
		var z = 0;

//		// Move the moved protein to the correct index position in the index manager
//		var $selected_proteins_container = $("#selected_proteins_container");
//		var $protein_select_outer_block_jq_Items = $selected_proteins_container.find(".protein_select_outer_block_jq");
//		$protein_select_outer_block_jq_Items.each( function( index, element ) {
//		var $this = $( this );
//		var tuid = $this.attr("data-uid");
//		if( tuid === uid ) {
//		// index is now the position to which we moved the protein
//		_indexManager.moveEntryToIndexPosition( uid, index );
//		}
//		} );
//		updateURLHash( false /* useSearchForm */ );
//		//showSelectedProteins();
//		drawSvg();
	};
};

var mergedPeptideProteinSearchesListVennDiagramSection = new MergedPeptideProteinSearchesListVennDiagramSection();
		