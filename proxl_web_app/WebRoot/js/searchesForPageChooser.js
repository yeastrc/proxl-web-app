/**
 * searchesForPageChooser.js
 * 
 * Javascript for the choosing which searches to show
 * 
 * Using webservice /project/getSearchDataList   
 * 
 *  Required on all pages (Must be on page before this JS is included):
 * 	<input type="hidden" id="project_id" value="{ project id }"> 
 *  <input type="hidden" class=" project_search_id_jq " value="{ project search id }">
 */

//  JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

// Instance object on page:  searchesForPageChooser



/**
 * Constructor
 */
var SearchesForPageChooser = function( params ) {
	this._singleSearchTemplate_HandlebarsTemplate = undefined;
	this.minimumNumberOfSearches = 1;
};


SearchesForPageChooser.prototype.CONSTANTS = {
		QUERY_STRING_PROJECT_SEARCH_ID : "projectSearchId",
		USER_CHOSE_SEARCH_CSS_CLASS : "searches-for-page-chooser-overlay-user-selected-search"
};

/**
 * 
 */
SearchesForPageChooser.prototype.init = function( params ) {
	var objectThis = this;
	try {
		//  project_id on the page
		var $project_id = $("#project_id");
		if ( $project_id.length === 0 ) {
			throw Error( 'Change/Add Searches not shown since missing from page: <input type="hidden" id="project_id" value="{ project id }">' );
			//  EARLY EXIT since missing needed data on page
		}
		var project_id = $project_id.val();
		if ( project_id === undefined || project_id === "" ) {
			throw Error( 'Change/Add Searches not shown since value from page: <input type="hidden" id="project_id" value="{ project id }"> is  project_id === undefined || project_id === ""' );
			//  EARLY EXIT since missing needed data on page
		}
		
		//  at least one project_search_id on the page
		var $project_search_id_jq = $(".project_search_id_jq");
		if ( $project_search_id_jq.length === 0 ) {
			throw Error( 'Change/Add Searches not shown since missing from page: <input type="hidden" class=" project_search_id_jq " value="{ project search id }">' );
			//  EARLY EXIT since missing needed data on page
		}
		var first_project_search_id = $project_search_id_jq.val()
		if ( first_project_search_id === undefined || first_project_search_id === "" ) {
			throw Error( 'Change/Add Searches not shown since first value from page: <input type="hidden" class=" project_search_id_jq " value="{ project search id }"> is  first_project_search_id === undefined || first_project_search_id === ""' );
			//  EARLY EXIT since missing needed data on page
		}
		

		var $searches_for_page_chooser_open_overlay = $("#searches_for_page_chooser_open_overlay");
		$searches_for_page_chooser_open_overlay.click( function( eventObject ) {
			try {
				objectThis.openOverlay();
				eventObject.preventDefault();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		} );
		var $searches_for_page_chooser_modal_dialog_overlay_hide_parts_jq = $(".searches_for_page_chooser_modal_dialog_overlay_hide_parts_jq");
		$searches_for_page_chooser_modal_dialog_overlay_hide_parts_jq.click( function( eventObject ) {
			try {
				objectThis.closeOverlay();
				eventObject.preventDefault();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		} );
		var $searches_for_page_chooser_overlay_change_button = $("#searches_for_page_chooser_overlay_change_button");
		$searches_for_page_chooser_overlay_change_button.click( function( eventObject ) {
			try {
				objectThis.changeButtonClicked();
				eventObject.preventDefault();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		} );
		var $searches_for_page_chooser_overlay_change_button_disabled_cover_div = $("#searches_for_page_chooser_overlay_change_button_disabled_cover_div");
		var tooltipText = $searches_for_page_chooser_overlay_change_button_disabled_cover_div.attr("data-tooltip_get_in_init");			
		tooltipText = tooltipText.replace("#", this.minimumNumberOfSearches );
		$searches_for_page_chooser_overlay_change_button_disabled_cover_div.attr("data-tooltip", tooltipText);	
		addSingleGenericProxlToolTip( $searches_for_page_chooser_overlay_change_button_disabled_cover_div );

		//  Compile Handlebars Templates
		var singleSearchTemplate_handlebarsSource = $( "#searches_for_page_chooser_overlay_search_entry_template" ).text();
		if ( singleSearchTemplate_handlebarsSource === undefined ) {
			throw Error( "singleSearchTemplate_handlebarsSource === undefined" );
		}
		if ( singleSearchTemplate_handlebarsSource === null ) {
			throw Error( "singleSearchTemplate_handlebarsSource === null" );
		}
		this._singleSearchTemplate_HandlebarsTemplate = Handlebars.compile( singleSearchTemplate_handlebarsSource );
		
		//  Plain text template using "#" for project search id 
		var $searches_for_page_chooser_overlay_project_search_id_input_template = $("#searches_for_page_chooser_overlay_project_search_id_input_template");
		if ( $searches_for_page_chooser_overlay_project_search_id_input_template.length === 0 ) {
			throw Error( "value with id 'searches_for_page_chooser_overlay_project_search_id_input_template' not on page" );
		}
		this._project_search_id_input_template = $searches_for_page_chooser_overlay_project_search_id_input_template.text();
		
		//  URL/URI paths for image and structure pages
		
		var $searches_for_page_chooser_modal_dialog_overlay_image_uri_path = $("#searches_for_page_chooser_modal_dialog_overlay_image_uri_path");
		if ( $searches_for_page_chooser_modal_dialog_overlay_image_uri_path.length === 0 ) {
			throw Error( "value with id 'searches_for_page_chooser_modal_dialog_overlay_image_uri_path' not on page" );
		}
		var $searches_for_page_chooser_modal_dialog_overlay_structure_uri_path = $("#searches_for_page_chooser_modal_dialog_overlay_structure_uri_path");
		if ( $searches_for_page_chooser_modal_dialog_overlay_structure_uri_path.length === 0 ) {
			throw Error( "value with id 'searches_for_page_chooser_modal_dialog_overlay_structure_uri_path' not on page" );
		}
		this._image_uri_path = $searches_for_page_chooser_modal_dialog_overlay_image_uri_path.text();
		this._structure_uri_path = $searches_for_page_chooser_modal_dialog_overlay_structure_uri_path.text();
		
		//  Everything on the page so show the button
		$("#searches_for_page_chooser_button_container").show();
		
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		console.log( e );  
		// Don't rethrow exception since want page initialization to continue so rest of page is usable
	}

};

/**
 * 
 */
SearchesForPageChooser.prototype.openOverlay = function( ) {
	this.loadSearchData();
};

/**
 * 
 */
SearchesForPageChooser.prototype.closeOverlay = function( ) {
	$(".searches_for_page_chooser_modal_dialog_overlay_display_parts_jq").hide()
	
};

/**
 * 
 */
SearchesForPageChooser.prototype.loadSearchData = function( params ) {
	var objectThis = this;
	var $project_id = $("#project_id");
	if ( $project_id.length === 0 ) {
		throw Error( 'Missing from page: <input type="hidden" id="project_id" value="{ project id }">' );
	}
	var project_id = $project_id.val()

	var requestData = { project_id : project_id };
	$.ajax({
		url : contextPathJSVar + "/services/project/getSearchDataList",
		data : requestData, // The data sent as params on the URL
		dataType : "json",
		success : function(data) {
			try {
				objectThis.loadSearchDataProcessResponse( { requestData : requestData, responseData : data } );
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		},
        failure: function(errMsg) {
        	handleAJAXFailure( errMsg );
        },
		error : function(jqXHR, textStatus, errorThrown) {
			handleAJAXError(jqXHR, textStatus, errorThrown);
		}
	});		
};



/**
 * 
 */
SearchesForPageChooser.prototype.loadSearchDataProcessResponse = function( params ) {
	var objectThis = this;
	var requestData = params.requestData;
	var responseData = params.responseData;
	
	var status = responseData.status;
	
	if ( ! status ) {
		console.log( "status false" );
		return;
	}
	
	var searchesToDisplay = responseData.searchesList;

	var projectSearchIdsForCurrentPage = this.getProjectSearchIdsForCurrentPage();
	var projectSearchIdsForCurrentPageAsObject = projectSearchIdsForCurrentPage.asObject;

	//  Process the Search data and add it to the page
	var $searches_for_page_chooser_list_container = $("#searches_for_page_chooser_list_container");
	$searches_for_page_chooser_list_container.empty();
	
	var searchesOnCurrentPage = [];
	var searchesNOTOnCurrentPage = [];
	
	//  First separate out the searches currently on the page into a separate array which will be displayed at the top of the list of searches
	for ( var searchDataListIndex = 0; searchDataListIndex < searchesToDisplay.length; searchDataListIndex++ ) {
		var searchDataEntry = searchesToDisplay[ searchDataListIndex ];
		if ( projectSearchIdsForCurrentPageAsObject[ searchDataEntry.id ] ) {
			searchesOnCurrentPage.push( searchDataEntry );
		} else {
			searchesNOTOnCurrentPage.push( searchDataEntry );
		}
	}
	
	this.addSearchArrayToPage( { 
		searchesToAddToPage: searchesOnCurrentPage, 
		$searches_for_page_chooser_list_container : $searches_for_page_chooser_list_container,
		addCssClassSelected: true } );
	this.addSearchArrayToPage( { 
		searchesToAddToPage: searchesNOTOnCurrentPage, 
		$searches_for_page_chooser_list_container : $searches_for_page_chooser_list_container,
		addCssClassSelected: false } );

	$(".searches_for_page_chooser_modal_dialog_overlay_display_parts_jq").show()
	
	var OVERLAY_MINIMUM_HEIGTH = 150;
	
	var $window = $( window );
	var windowScrollTop = $window.scrollTop();
	var viewportHeight = $window.height();

	var $searches_for_page_chooser_list_outer_container = $("#searches_for_page_chooser_list_outer_container");
	var $searches_for_page_chooser_overlay_div = $("#searches_for_page_chooser_overlay_div");

	var listMaximumHeight = $searches_for_page_chooser_list_container.height() + 5;
	
	//   Set overlay height to viewport - 40px or at minimum height
	var overlayHeight = viewportHeight - 40;
	var current_select_searches_overlay_div_Heigth = $searches_for_page_chooser_overlay_div.height();
	var overlayHeightDiff = overlayHeight - current_select_searches_overlay_div_Heigth;
	var current_searches_list_container_Height = $searches_for_page_chooser_list_container.height();
	var new_current_searches_list_container_Height = current_searches_list_container_Height + overlayHeightDiff;
	if ( new_current_searches_list_container_Height < OVERLAY_MINIMUM_HEIGTH ) {
		new_current_searches_list_container_Height = OVERLAY_MINIMUM_HEIGTH ;
	}
	if ( new_current_searches_list_container_Height > listMaximumHeight ) {
		new_current_searches_list_container_Height = listMaximumHeight ;
	}
	
	//  Position Overlay Vertically
	var overlayNewTop = windowScrollTop + 10;
	//  Apply position to overlay
	$searches_for_page_chooser_overlay_div.css( { top : overlayNewTop + "px" } );
	$searches_for_page_chooser_list_outer_container.css( { height : new_current_searches_list_container_Height + "px" } ); 

	//  Does not always work, if the mouse moves right after the click
//	console.log("Before: Scroll to first selected search" );
//	//  Scroll to first selected search 
//	var $search_select_jq_All = $("#searches_for_page_chooser_list_container .search_select_jq");
//	$search_select_jq_All.each(function() {
//		var $this = $( this );
//		if ( $this.hasClass( objectThis.CONSTANTS.USER_CHOSE_SEARCH_CSS_CLASS ) ) {
//			try {
//				var search_select_jq_PositionTop = $this.position().top;
//				var projectSearchId = $this.attr("data-project_search_id");
//				console.log("search_select_jq_PositionTop: " + search_select_jq_PositionTop
//						+ ", data-project_search_id: " + projectSearchId );
//				$searches_for_page_chooser_list_container.scrollTop( search_select_jq_PositionTop - 20 );
//			} catch ( e ) {
//			}
//			return false; // EXIT EARLY .each()
//		}
//	});

};



/**
 * 
 */
SearchesForPageChooser.prototype.addSearchArrayToPage = function( params ) {
	var objectThis = this;
	var searchesToAddToPage = params.searchesToAddToPage;
	var $searches_for_page_chooser_list_container = params.$searches_for_page_chooser_list_container;
	var addCssClassSelected = params.addCssClassSelected;
	for ( var searchDataListIndex = 0; searchDataListIndex < searchesToAddToPage.length; searchDataListIndex++ ) {
		var searchDataEntry = searchesToAddToPage[ searchDataListIndex ];
		var html = objectThis._singleSearchTemplate_HandlebarsTemplate(searchDataEntry);
		var $addedItem = 
			$( html ).appendTo( $searches_for_page_chooser_list_container );
		if ( addCssClassSelected ) {
			$addedItem.addClass( objectThis.CONSTANTS.USER_CHOSE_SEARCH_CSS_CLASS );
		}
		$addedItem.click( function( eventObject ) {
			try {
				objectThis.searchClicked( { clickedThis : this } );
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		} );
	}
};

/**
 * 
 */
SearchesForPageChooser.prototype.searchClicked = function( params ) {
	var clickedThis = params.clickedThis;
	var $clickedThis = $( clickedThis );
	//  toggle CSS class this.CONSTANTS.USER_CHOSE_SEARCH_CSS_CLASS
	$clickedThis.toggleClass( this.CONSTANTS.USER_CHOSE_SEARCH_CSS_CLASS );
	this.enableDisableChangeButton();
};

/**
 * Enable/Disable Change Button, requiring at least this.minimumNumberOfSearches # searches selected
 */
SearchesForPageChooser.prototype.enableDisableChangeButton = function() {
	var objectThis = this;
	var $search_select_jq_All = $("#searches_for_page_chooser_list_container .search_select_jq");
	var chosenSearchCount = 0;
	$search_select_jq_All.each(function() {
		var $this = $( this );
		if ( $this.hasClass( objectThis.CONSTANTS.USER_CHOSE_SEARCH_CSS_CLASS ) ) {
			chosenSearchCount++;
		}
	});
	if ( chosenSearchCount >= this.minimumNumberOfSearches ) {
		$("#searches_for_page_chooser_overlay_change_button").prop("disabled", false);
		$("#searches_for_page_chooser_overlay_change_button_disabled_cover_div").hide();
	} else {
		$("#searches_for_page_chooser_overlay_change_button").prop("disabled", true);
		$("#searches_for_page_chooser_overlay_change_button_disabled_cover_div").show();
	}
};

/**
 * Change Button clicked
 */
SearchesForPageChooser.prototype.changeButtonClicked = function() {
	var objectThis = this;

	var projectSearchIdsForCurrentPage = this.getProjectSearchIdsForCurrentPage();
	var projectSearchIdsForCurrentPageAsArray = projectSearchIdsForCurrentPage.asArray;

	var $search_select_jq_All = $("#searches_for_page_chooser_list_container .search_select_jq");
	var chosenProjectSearchIds = [];
	$search_select_jq_All.each(function() {
		var $this = $( this );
		if ( $this.hasClass( objectThis.CONSTANTS.USER_CHOSE_SEARCH_CSS_CLASS ) ) {
			var chosenProjectSearchId = $this.attr("data-project_search_id");
			chosenProjectSearchIds.push( chosenProjectSearchId );
		}
	});
	if ( chosenProjectSearchIds.length === 0 ) {
		throw Error( "chosenProjectSearchIds.length === 0" );
	}
	chosenProjectSearchIds.sort();
	//  If chose exact same searches as current page, do nothing and exit 
	if ( projectSearchIdsForCurrentPageAsArray.length === chosenProjectSearchIds.length	) {
		// Length same so confirm all values the same
		var allValuesMatch = true;
		for ( var index = 0; index < projectSearchIdsForCurrentPageAsArray.length; index++ ) {
			if ( projectSearchIdsForCurrentPageAsArray[ index ] != chosenProjectSearchIds[ index ] ) {
				allValuesMatch = false;
			}
		}
		if ( allValuesMatch ) {
			//  Chose exact same searches as current page, do nothing and exit
			console.log( "No changes to chosen project search ids so just exit without changing URL" );
			this.closeOverlay();
			return;
		}
	}
	
	var urlPathname = window.location.pathname;
	
	if ( urlPathname.indexOf( this._image_uri_path ) != -1
			|| urlPathname.indexOf( this._structure_uri_path ) != -1 ) {
		
		this.changePageUrlForImageOrStructurePage( { chosenProjectSearchIds : chosenProjectSearchIds } );
	}

	this.changePageUrlFor_NOT_ImageOrStructurePage( { chosenProjectSearchIds : chosenProjectSearchIds } );
	
};


/**
 * For NOT Image or Structure page
 * Update form on page with new project search id(s) and submit it
 */
SearchesForPageChooser.prototype.changePageUrlFor_NOT_ImageOrStructurePage = function( params ) {

	var chosenProjectSearchIds = params.chosenProjectSearchIds;

	if ( chosenProjectSearchIds.length === 1 ) {
		//  1 project search id chosen
		var $form_get_for_updated_parameters_single_search = $("#form_get_for_updated_parameters_single_search");

		var $project_search_id_in_update_form_jq_All = $form_get_for_updated_parameters_single_search.find(".project_search_id_in_update_form_jq");
		$project_search_id_in_update_form_jq_All.remove(); // Removes all existing input fields

		var queryStringProjectSearchIds = [];
		for ( var index = 0; index < chosenProjectSearchIds.length; index++ ) {
			var projectSearchId = chosenProjectSearchIds[ index ];
			var inputFieldForProjectSearchIdHTML = this._project_search_id_input_template.replace( "#", projectSearchId );
			var $addedItem = 
				$( inputFieldForProjectSearchIdHTML ).prependTo( $form_get_for_updated_parameters_single_search );
		}

		$form_get_for_updated_parameters_single_search.submit();
		return;
	} 
	
	//  More than one project search id chosen
	
	var $form_get_for_updated_parameters_multiple_searches = $("#form_get_for_updated_parameters_multiple_searches");
	
	var $project_search_id_in_update_form_jq_All = $form_get_for_updated_parameters_multiple_searches.find(".project_search_id_in_update_form_jq");
	$project_search_id_in_update_form_jq_All.remove(); // Removes all existing input fields

	for ( var index = 0; index < chosenProjectSearchIds.length; index++ ) {
		var projectSearchId = chosenProjectSearchIds[ index ];
		var inputFieldForProjectSearchIdHTML = this._project_search_id_input_template.replace( "#", projectSearchId );
		var $addedItem = 
			$( inputFieldForProjectSearchIdHTML ).prependTo( $form_get_for_updated_parameters_multiple_searches );
	}

	$form_get_for_updated_parameters_multiple_searches.submit();
	var z = 0;
};

/**
 * For Image or Structure page
 * Create new URL with new project search id(s) and change browser URL to it
 */
SearchesForPageChooser.prototype.changePageUrlForImageOrStructurePage = function( params ) {
	
	var chosenProjectSearchIds = params.chosenProjectSearchIds;
	
	// Build new URL
	var href = window.location.href;
	var pathname = window.location.pathname;
//	pathname	:	"/proxl/image.do"
	
	var urlHash = window.location.hash;
	
	var queryStringProjectSearchIds = [];
	for ( var index = 0; index < chosenProjectSearchIds.length; index++ ) {
		var projectSearchId = chosenProjectSearchIds[ index ];
		var singleProjectSearchIdAssignment = 
			this.CONSTANTS.QUERY_STRING_PROJECT_SEARCH_ID 
			+ "="
			+ projectSearchId;
		queryStringProjectSearchIds.push( singleProjectSearchIdAssignment );
	}
	var queryString = queryStringProjectSearchIds.join( "&" );
	
	var newURL = pathname + "?" + queryString + urlHash;
	
	window.location.href = newURL;
	
	var z = 0;
};

/**
 * Get project_search_id_jq from page as object where ids are properties and value is true
 */
SearchesForPageChooser.prototype.getProjectSearchIdsForCurrentPage = function() {
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


/**
 * 
 */
var searchesForPageChooser = new SearchesForPageChooser();


//$(document).ready(function()  { 
	try {
		searchesForPageChooser.init();
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
//});
