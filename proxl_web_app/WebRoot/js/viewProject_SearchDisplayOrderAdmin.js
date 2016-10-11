
//    viewProject_SearchDisplayOrderAdmin.js



//  Javascript for the project admin of search display order of the page viewProject.jsp

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";


///////////////

$(document).ready(function() {

	searchReorder.init();
});


/////////////////

//  Constructor

var SearchReorder = function() {

	this.init = function( ) {
		
		var objectThis = this;

		$("#re_order_search_button").click(function(eventObject) {

			objectThis.startSearchReorder();
		});
		
		
	};
	
	
	this.startSearchReorder = function() {
		
		$("#explore_data_main_data_block").hide();
		
		$("#re_order_searches_data_block").show();
		
		this.loadData();

		$("#re_order_searches_main_data_block").show();  //  TEMP
	};

	this.doneSearchReorder = function() {

		$("#re_order_searches_data_block").hide();
		$("#re_order_searches_re_loading_page_message").show();

		window.location.reload(true);
	};
	
	this.loadData = function() {

		var objectThis = this;

		var requestData = { project_id : adminGlobals.project_id };

		$.ajax({
			url : contextPathJSVar + "/services/project/getSearchesReorderData",
			
			data : requestData, // The data sent as params on the URL
			dataType : "json",

			success : function(data) {
				objectThis.loadDataProcessResponse( { requestData : requestData, responseData : data } );
			},
	        failure: function(errMsg) {
	        	handleAJAXFailure( errMsg );
	        },
			error : function(jqXHR, textStatus, errorThrown) {
				handleAJAXError(jqXHR, textStatus, errorThrown);
			}
		});		
		
	};

	this.loadDataProcessResponse = function( params ) {

		var objectThis = this;
		
//		var requestData = params.requestData;
		var responseData = params.responseData;
		
		if ( ! responseData.status ) {

			window.location.reload(true);
		}

		var searchDataList = responseData.searchDataList;

		if ( searchDataList.length === 0 ) {

			window.location.reload(true);
		}
		
		$("#re_order_searches_loading_message").hide();
		$("#re_order_searches_main_data_block").show();

		var source = $("#re_order_searches_single_search_template").html();

		if ( source === undefined ) {
			throw '$("#re_order_searches_single_search_template").html() === undefined';
		}
		if ( source === null ) {
			throw '$("#re_order_searches_single_search_template").html() === null';
		}
		
		var template = Handlebars.compile(source);

		var $re_order_searches_search_entries_block = $("#re_order_searches_search_entries_block");
		
		for ( var searchDataListIndex = 0; searchDataListIndex < searchDataList.length; searchDataListIndex++ ) {
			
			var searchDataEntry = searchDataList[ searchDataListIndex ];

			var html = template(searchDataEntry);

			$( html ).appendTo( $re_order_searches_search_entries_block );
		}
		
	    $( "#re_order_searches_search_entries_block" ).sortable( {
	    		update : function() {
	    			objectThis.changeSearchesOrderInDB();
	    		}
	    } );
	    
	    $( "#re_order_searches_search_entries_block" ).disableSelection();		
	};
	
	this.changeSearchesOrderInDB = function() {
		
		var searchesInOrder = [];
		
		var $search_display_order_item_jq = $( "#re_order_searches_search_entries_block" ).find(".search_display_order_item_jq");
		
		$search_display_order_item_jq.each( function() {
			
			var $this = $( this );
			var search_id = $this.attr("data-search_id");
			
			searchesInOrder.push( search_id );
		});
		

		var requestData = {
				searchesInOrder: searchesInOrder
		};
		
		var requestDataJSON = JSON.stringify( requestData );

		var _URL = contextPathJSVar + "/services/project/setSearchesOrder";

		// var request =
		$.ajax({
			type : "POST",
			url : _URL,
		    data: requestDataJSON,
		    contentType: "application/json; charset=utf-8",
		    dataType: "json",
			success : function(data) {

				if ( ! data.status ) {
					
					window.location.reload(true);
				}
			},
	        failure: function(errMsg) {
	        	handleAJAXFailure( errMsg );
	        },
			error : function(jqXHR, textStatus, errorThrown) {

				handleAJAXError(jqXHR, textStatus, errorThrown);

				// alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
				// textStatus: " + textStatus );
			}
		});
		
	};
	
};


//  Instance of class

var searchReorder = new SearchReorder();

