
//    viewProject_OrganizeSearchesAndFoldersAdmin.js

//  Javascript for the project admin of organzing searches in folders 
//             and setting display of both on the page viewProject.jsp
//////////////////////////////////
// JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

///////////////
$(document).ready(function() {
	try {
		organizeSearches.init();
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
});

/////////////////
//  Constructor
var OrganizeSearches = function() {
	
	var _selectedFolderId = null;
	var _selectedFolderSearchesNotInAnyFolder = false;
	
	var _CONSTANTS = {
			MESSAGE_HIDE_DELAY : 2000,  // in Milliseconds
			MESSAGE_FADEOUT_OPTIONS : { duration : 800 },  // duration in Milliseconds 
			MESSAGE_SET_TIMEOUT_DATA_KEY : "MESSAGE_SET_TIMEOUT_DATA_KEY"
	};

	
	this.init = function( ) {
		var objectThis = this;
		$("#organize_searches_button").click(function(eventObject) {
			try {
				var $clickThis = $( this );
				var qtipApi = $clickThis.qtip('api');
				if ( qtipApi ) {
					qtipApi.hide(true);
				}
				objectThis.startSearchesOrganize();
				eventObject.stopPropagation();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		$("#organize_searches_done_organizing_button").click(function(eventObject) {
			try {
				objectThis.doneSearchOrganizing();
				eventObject.stopPropagation();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		//  "New Folder" Button
		$("#organize_searches_new_folder_button").click(function(eventObject) {
			try {
				var $organize_searches_folder_new_folder_block = $("#organize_searches_folder_new_folder_block");
				$organize_searches_folder_new_folder_block.hide();
				var $organize_searches_folder_add_new_folder_block = $("#organize_searches_folder_add_new_folder_block");
				$organize_searches_folder_add_new_folder_block.show();
				eventObject.stopPropagation();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		$("#organize_searches_add_new_folder_button").click(function(eventObject) {
			try {
				objectThis.addNewFolder();
				eventObject.stopPropagation();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		$(".delete_folder_overlay_cancel_parts_jq").click(function(eventObject) {
			var clickThis = this;
			try {
				objectThis.closeConfirmDeleteFolderOverlay( clickThis, eventObject );
				eventObject.stopPropagation();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
			return false;
		});
		$(".folder_delete_button_jq").click(function(eventObject) {
			try {
				var clickThis = this;
				objectThis.deleteFolderClickHandler(clickThis, eventObject);
				eventObject.stopPropagation();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		$("#delete_folder_confirm_button").click(function(eventObject) {
			try {
				var clickThis = this;
				objectThis.deleteFolderConfirmed(clickThis, eventObject);
				eventObject.stopPropagation();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		
		$(".rename_folder_overlay_cancel_parts_jq").click(function(eventObject) {
			var clickThis = this;
			try {
				objectThis.closeConfirmRenameFolderOverlay( clickThis, eventObject );
				eventObject.stopPropagation();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
			return false;
		});
		$(".folder_rename_button_jq").click(function(eventObject) {
			try {
				var clickThis = this;
				objectThis.renameFolderClickHandler(clickThis, eventObject);
				eventObject.stopPropagation();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		$("#rename_folder_save_button").click(function(eventObject) {
			try {
				var clickThis = this;
				objectThis.renameFolderSaveNewName(clickThis, eventObject);
				eventObject.stopPropagation();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		//  Make whole "Folder List" draggable vertically, handle is on "Folder List" div
		var $folderDragHandle = $("#organize_searches_folder_total_block_drag_handle");
		var folderDragHandleElement = $folderDragHandle[ 0 ]; 
		var $organize_searches_folder_total_block = $("#organize_searches_folder_total_block");
		$organize_searches_folder_total_block.draggable({
			axis: "y",
			handle: folderDragHandleElement,
			containment: "parent"
			
		});
		var handle = $organize_searches_folder_total_block.draggable( "option", "handle" );
		var z = 0;
		  
	};
	
	this.startSearchesOrganize = function() {
		var objectThis = this;
		
		//  Remove other blocks from project page
		$("#view_searches_project_admin_div").remove();
		$("#public_access_data_top_level_container").remove();
		$("#upload_data_top_level_container").remove();
		$("#explore_data_main_data_block").remove();
		
		$("#organize_searches_data_block").show();
		
		var callback = function( params ) {
			objectThis.putFoldersAndSearchesOnPage( params );
		}
		this.loadData( { callback : callback } );
	};

	this.doneSearchOrganizing = function() {
		try {
			$("#organize_searches_data_block").hide();
			$("#organize_searches_re_loading_page_message").show();
			window.location.reload(true);
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
	
	this.loadData = function( params ) {
		var objectThis = this;
		var callback = params.callback;
		var requestData = { project_id : adminGlobals.project_id };
		$.ajax({
			url : "services/project/organizeSearchesGetData",
			data : requestData, // The data sent as params on the URL
			dataType : "json",
			success : function(data) {
				try {
					callback( { requestData : requestData, responseData : data } );
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
	
	this.putFoldersAndSearchesOnPage = function( params ) {
		var objectThis = this;
//		var requestData = params.requestData;
		var responseData = params.responseData;
		if ( ! responseData.status ) {
			window.location.reload(true);
			return;
		}
		if ( responseData.noSearchesFound ) {
			window.location.reload(true);
			return;
		}
		var folderDataList = responseData.folderDataList;
		var searchesNotInFoldersList = responseData.searchesNotInFoldersList;
		
		$("#organize_searches_loading_message").hide();
		$("#organize_searches_main_data_block").show();

		//  Get Folder Handlebars template and parse it
		var organize_searches_single_folder_template_html = $("#organize_searches_single_folder_template").html();
		if ( organize_searches_single_folder_template_html === undefined ) {
			throw Error( '$("#organize_searches_single_folder_template").html() === undefined' );
		}
		if ( organize_searches_single_folder_template_html === null ) {
			throw Error( '$("#organize_searches_single_folder_template").html() === null' );
		}
		var organize_searches_single_folder_template = Handlebars.compile(organize_searches_single_folder_template_html);
		

		//  Get Folder Tooltip Handlebars template and parse it
		var organize_searches_single_folder_tooltip_template_html = $("#organize_searches_single_folder_tooltip_template").html();
		if ( organize_searches_single_folder_tooltip_template_html === undefined ) {
			throw Error( '$("#organize_searches_single_folder_tooltip_template").html() === undefined' );
		}
		if ( organize_searches_single_folder_tooltip_template_html === null ) {
			throw Error( '$("#organize_searches_single_folder_tooltip_template").html() === null' );
		}
		var organize_searches_single_folder_tooltip_template = Handlebars.compile(organize_searches_single_folder_tooltip_template_html);
		
		var firstSearchesToDisplay = searchesNotInFoldersList;
		var folderIndex = null;

		if ( ( ! firstSearchesToDisplay ) || firstSearchesToDisplay.length === 0 ) {
			//  Find a folder containing searches
			firstSearchesToDisplay = null;
			for ( var folderDataListIndex = 0; folderDataListIndex < folderDataList.length; folderDataListIndex++ ) {
				var folderDataEntry = folderDataList[ folderDataListIndex ];
				var searchesForFolder = folderDataEntry.searchesForFolder;
				if ( searchesForFolder && searchesForFolder.length > 0 ) {
					firstSearchesToDisplay = searchesForFolder;
					folderIndex = folderDataListIndex;
					_selectedFolderId = folderDataEntry.id;
					_selectedFolderSearchesNotInAnyFolder = false;
					break;
				}
			}
			if ( firstSearchesToDisplay === null ) {
				//  No searches found for any folder so highlight searches not in any folders
				firstSearchesToDisplay = searchesNotInFoldersList;
				folderIndex = null;
				_selectedFolderId = null;
				_selectedFolderSearchesNotInAnyFolder = true;
			}
		}
		

		//  Process the Folder data and add it to the page
		var $organize_searches_folder_entries_block = $("#organize_searches_folder_entries_block");
		$organize_searches_folder_entries_block.empty();
		for ( var folderDataListIndex = 0; folderDataListIndex < folderDataList.length; folderDataListIndex++ ) {
			var folderDataEntry = folderDataList[ folderDataListIndex ];
			var html = organize_searches_single_folder_template(folderDataEntry);
			var $addedItem = $( html ).appendTo( $organize_searches_folder_entries_block );
			if ( folderIndex !== null && folderIndex === folderDataListIndex ) {
				//  Highlight this folder since the searches for it will be displayed initially 
				var $folder_display_order_inner_item_jq = $addedItem.find(".folder_display_order_inner_item_jq");
				$folder_display_order_inner_item_jq.addClass("selected-item");
			}
			
			var $folder_display_name_jq = $addedItem.find(".folder_display_name_jq");
			
			var tooltipHTML = organize_searches_single_folder_tooltip_template( folderDataEntry );
			$folder_display_name_jq.qtip( {
		        content: {
		            text: tooltipHTML
		        },
				position: {
					target: 'mouse',
					adjust: { x: 5, y: 5 }, // Offset it slightly from under the mouse
		            viewport: $(window)
		         }
		    });				
		}
		
		//   Add Folder events and handling 

	    $( "#organize_searches_folder_entries_block" ).sortable( {
	    		update : function() {
	    			objectThis.changeFoldersOrderInDB();
	    		}
	    } );
	    $( "#organize_searches_folder_entries_block" ).disableSelection();		
		
	    
	    //  Add Click handlers to the folders on the folder name
		var $folder_display_order_inner_item_jq = $organize_searches_folder_entries_block.find(".folder_display_order_inner_item_jq");
		$folder_display_order_inner_item_jq.click(function(eventObject) {
			try {
				//  User has selected this folder by clicking on it
				objectThis.processClick_OnFolder_Or_SearchesNotInAnyFolder( { clickedThis : this, eventObject : eventObject } );
				eventObject.stopPropagation();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		//  Add click handler to delete icon each folder
		var $folder_delete_button_jq_InBlock = $organize_searches_folder_entries_block.find(".folder_delete_button_jq");
		$folder_delete_button_jq_InBlock.click(function(eventObject) {
			try {
				var clickThis = this;
				objectThis.deleteFolderClickHandler(clickThis, eventObject);
				eventObject.stopPropagation();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		//  Add click handler to rename icon each folder
		var $folder_rename_button_jq_InBlock = $organize_searches_folder_entries_block.find(".folder_rename_button_jq");
		$folder_rename_button_jq_InBlock.click(function(eventObject) {
			try {
				var clickThis = this;
				objectThis.renameFolderClickHandler(clickThis, eventObject);
				eventObject.stopPropagation();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});


		//  "Searches not in any folder"
		
		var $organize_searches_folder_searches_not_in_any_folder_inner_item = $("#organize_searches_folder_searches_not_in_any_folder_inner_item");
		if ( folderIndex === null ) {
			//  Highlight "Searches not in any folder" since the searches for it are displayed 
			$organize_searches_folder_searches_not_in_any_folder_inner_item.addClass("selected-item");
		}

	    //  Add Click handler to the "Searches not in any folder"
		$organize_searches_folder_searches_not_in_any_folder_inner_item.click(function(eventObject) {
			try {
				//  User has selected this by clicking on it
				objectThis.processClick_OnFolder_Or_SearchesNotInAnyFolder( { clickedThis : this, eventObject : eventObject } );
				eventObject.stopPropagation();
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		});
		
		//  Add tooltips
		var $organize_searches_folder_total_block = $("#organize_searches_folder_total_block");
		addToolTips( $organize_searches_folder_total_block );
		
		this.addSearchesToPage( { searchesToDisplay : firstSearchesToDisplay } );
	};
	
	this.addSearchesToPage = function( params ) {
		var objectThis = this;
		
		var searchesToDisplay = params.searchesToDisplay;
		
		//  Get Search Handlebars template and parse it
		var organize_searches_single_search_template_html = $("#organize_searches_single_search_template").html();
		if ( organize_searches_single_search_template_html === undefined ) {
			throw Error( '$("#organize_searches_single_search_template").html() === undefined' );
		}
		if ( organize_searches_single_search_template_html === null ) {
			throw Error( '$("#organize_searches_single_search_template").html() === null' );
		}
		var organize_searches_single_search_template = Handlebars.compile(organize_searches_single_search_template_html);

		//  Get Search Tooltip Handlebars template and parse it
		var organize_searches_single_search_tooltip_template_html = $("#organize_searches_single_search_tooltip_template").html();
		if ( organize_searches_single_search_tooltip_template_html === undefined ) {
			throw Error( '$("#organize_searches_single_search_tooltip_template").html() === undefined' );
		}
		if ( organize_searches_single_search_tooltip_template_html === null ) {
			throw Error( '$("#organize_searches_single_search_tooltip_template").html() === null' );
		}
		var organize_searches_single_search_tooltip_template = Handlebars.compile(organize_searches_single_search_tooltip_template_html);
		
		// Reposition the Folder list at the top of the draggable space
		var $organize_searches_folder_total_block = $("#organize_searches_folder_total_block");
		$organize_searches_folder_total_block.css("top", "0px;")
		
		//  Process the Search data and add it to the page
		var $organize_searches_search_entries_block = $("#organize_searches_search_entries_block");
		$organize_searches_search_entries_block.empty();
		
		for ( var searchDataListIndex = 0; searchDataListIndex < searchesToDisplay.length; searchDataListIndex++ ) {
			var searchDataEntry = searchesToDisplay[ searchDataListIndex ];
			var html = organize_searches_single_search_template(searchDataEntry);
			var $addedItem = 
				$( html ).appendTo( $organize_searches_search_entries_block );
			 
			var tooltipHTML = organize_searches_single_search_tooltip_template( searchDataEntry )
			
			var $search_display_name_jq = $addedItem.find(".search_display_name_jq");
			
			$search_display_name_jq.qtip( {
		        content: {
		            text: tooltipHTML
		        },
				position: {
					target: 'mouse',
					adjust: { x: 5, y: 5 }, // Offset it slightly from under the mouse
		            viewport: $(window)
		         }
		    });	
		}

		//   Add Search events and handling	    

		$organize_searches_search_entries_block.sortable( {
	    	
	    	//  properties of "ui", except dragging doesn't work properly when referenced in "start"
//			", ui.offset.top: " + ui.offset.top +
//			", ui.offset.left: " + ui.offset.left +
//			", ui.position.top: " + ui.position.top +
//			", ui.position.left: " + ui.position.left +
//			", ui.originalPosition.top: " + ui.originalPosition.top +
//			", ui.originalPosition.left: " + ui.originalPosition.left;
	    	
	    		update :  function( event, ui ) {// update fires when dragging stops and the order has changed
	    			var $item = ui.item;
	    			objectThis.changeSearchesOrderInDB();
	    		},
	    		start : function( event, ui ) {// start fires when dragging stops
	    			var $item = ui.item;
	    		},
	    		sort : function( event, ui ) {// sort fires while dragging 
	    			//  Adding a hack to the "sort" to detect if the item was dragged over a folder
	    			
	    			var $item = ui.item;
	    			
	    			objectThis.removeHighlightFromAllFolders();
	    			
	    			var callbackOnInFolder = function( params ) {
	    				var $folder_display_order_item_jq_MouseIsOver = params.$folder_display_order_item_jq_MouseIsOver;
	    				//  Add highlight to folder mouse is over
	    				var $folder_display_order_inner_item_jq = 
	    					$folder_display_order_item_jq_MouseIsOver.find(".folder_display_order_inner_item_jq");
	    				$folder_display_order_inner_item_jq.addClass("highlighted-item");
	    			}

	    			objectThis.callCallbackForItemInFolder( { 
	    				ui : ui, 
	    				$item : $item, 
	    				callbackOnInFolder : callbackOnInFolder } );

	    		},


	    		stop : function( event, ui ) {// beforeStop fires when dragging stops, after update if update is called
	    			
	    			//  Adding a hack to the "stop" to detect if the item was dragged over a folder
	    			
	    			var $searchItem = ui.item;
	    			
	    			objectThis.removeHighlightFromAllFolders();
	    			
    				// Grab the first element in the $searchItem array and access its qTip API
    				var qtipApi = $searchItem.qtip('api');
    				if ( qtipApi ) {
    					qtipApi.hide(true);
    				}

	    			var $tool_tip_attached_jq_All = $searchItem.find(".tool_tip_attached_jq");

	    			$tool_tip_attached_jq_All.each( function( index ) {
	    				var $tool_tip_attached_jq_One = $( this );
	    				// Grab the first element in the $tool_tip_attached_jq_One array and access its qTip API
	    				var qtipApi = $tool_tip_attached_jq_One.qtip('api');
	    				if ( qtipApi ) {
	    					qtipApi.hide(true);
	    				}
	    			});
	    			
	    			
	    			//  Build callback function that is called 
	    			//  if the dragged search is over a folder
	    			var callbackOnInFolder = function( params ) {
	    				var $folder_display_order_item_jq_MouseIsOver = params.$folder_display_order_item_jq_MouseIsOver;

	    				var searches_not_in_any_folder = 
	    					objectThis.isFolder_searches_not_in_any_folder( { $folder_display_order_item_jq : $folder_display_order_item_jq_MouseIsOver } );
	    				
	    				var folder_id = undefined;
	    				var folderIdInt = undefined;
	    				
	    				if ( ! searches_not_in_any_folder ) {
	    					folder_id = $folder_display_order_item_jq_MouseIsOver.attr("data-folder_id");
	    					if ( folder_id === undefined ) {
	    						throw Error( '$folder_display_order_item_jq_MouseIsOver.attr("data-folder_id"); returned undefined ' );
	    					}
	    					folderIdInt = parseInt( folder_id, 10 );
	    					if ( isNaN( folderIdInt ) ) {
	    						throw Error( '$folder_display_order_item_jq_MouseIsOver.attr("data-folder_id"); returned string that is not an int:  ' + folder_id );
	    					}
	    				}
	    				
	    				if ( _selectedFolderSearchesNotInAnyFolder ) {
	    					if ( searches_not_in_any_folder ) {
	    						//  "Searches not in any folder" selected 
	    						//  and is where search is dropped so do nothing
	    						return;  // EARLY EXIT
	    					}
	    				} else {
	    					if ( _selectedFolderId === folderIdInt ) {
	    						// the folder selected 
	    						//  and is where search is dropped so do nothing
	    						return;  // EARLY EXIT
	    					}
	    				}
    					var draggedSearch__project_search_id = $searchItem.attr("data-project_search_id");
    					if ( draggedSearch__project_search_id === undefined ) {
    						throw Error( '$searchItem.attr("data-project_search_id"); returned undefined ' );
    					}
    					objectThis.moveSearchToFolder( {
    						folder_id : folder_id,
    						searches_not_in_any_folder : searches_not_in_any_folder,
    						project_search_id : draggedSearch__project_search_id,
    						$searchItem : $searchItem,
    						$folder_display_order_item_jq_MouseIsOver : $folder_display_order_item_jq_MouseIsOver
    					} );

	    			};
	    			objectThis.callCallbackForItemInFolder( { 
	    				ui : ui, 
	    				$item : $searchItem, 
	    				callbackOnInFolder : callbackOnInFolder } );
	    		},
	    		beforeStop : function( event, ui ) {// beforeStop fires when dragging stops
	    			var $item = ui.item;
	    			
	    		},
	    		change : function( event, ui ) { // change fires whenever sort order changes while dragging
	    			var $item = ui.item;
	    			
	    		}
	    		
//	    		items: "> li"
//	    		Type: Selector
//	    		Default: "> *"
//	    		Specifies which items inside the element should be sortable.
	    } );
	    $( "#organize_searches_search_entries_block" ).disableSelection();		
	    

		//  Add tooltips
		addToolTips( $organize_searches_search_entries_block );

	};
	
	this.moveSearchToFolder = function( params ) {
		try {
			var objectThis = this;
			var folder_id = params.folder_id;
			var searches_not_in_any_folder = params.searches_not_in_any_folder;
			var project_search_id = params.project_search_id;
			var $searchItem = params.$searchItem;
			var $folder_display_order_item_jq_MouseIsOver = params.$folder_display_order_item_jq_MouseIsOver;

			var requestData = {
					projectSearchId : project_search_id,
					folderId : folder_id,
					searches_not_in_any_folder : searches_not_in_any_folder
			};

			var _URL = "services/project/organizeSearchesSetSearchFolder";

//			var request =
			$.ajax({
				type : "POST",
				url : _URL,
				data : requestData,
				dataType : "json",
				success : function(responseData) {
					try {
						objectThis.moveSearchToFolderProcessResponse( {  
							responseData : responseData,
							requestData : requestData,
							$searchItem : $searchItem,
							$folder_display_order_item_jq_MouseIsOver : $folder_display_order_item_jq_MouseIsOver
						} );
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
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}

	};
	
	this.moveSearchToFolderProcessResponse = function( params ) {
		var responseData = params.responseData;
		var requestData = params.requestData;
		var $searchItem = params.$searchItem; 
		var $folder_display_order_item_jq_MouseIsOver = params.$folder_display_order_item_jq_MouseIsOver;

		var qtipApi_OnSearchItem = $searchItem.qtip('api');
		if ( qtipApi_OnSearchItem ) {
			qtipApi_OnSearchItem.destroy(true);
		}
		
		var $tool_tip_attached_jq_All = $searchItem.find(".tool_tip_attached_jq");

		$tool_tip_attached_jq_All.each( function( index ) {
			var $tool_tip_attached_jq_One = $( this );
			// Grab the first element in the $folder_root_jq_One array and access its qTip API
			var qtipApi = $tool_tip_attached_jq_One.qtip('api');
			if ( qtipApi ) {
				qtipApi.destroy(true);
			}
		});
		
		this.showMovedSearchToFolderMessage( { $searchItem : $searchItem, $folder_display_order_item_jq_MouseIsOver : $folder_display_order_item_jq_MouseIsOver } );
		
		$searchItem.remove();
	};
	

	this.showMovedSearchToFolderMessage = function( params ) {
		var objectThis = this;
//		var clickedThis = params.clickedThis;
		
		var $searchItem = params.$searchItem;
		var $folder_display_order_item_jq_MouseIsOver = params.$folder_display_order_item_jq_MouseIsOver;

//		var $clickedThis = $( clickedThis );
//		var $folder_root_jq = $clickedThis.closest(".folder_root_jq");
		
		var $folder_root_jq = $folder_display_order_item_jq_MouseIsOver;

//		if ( $current_url_saved_as_default_page_view_success.length > 0 ) {
			// Position dialog over clicked delete icon
			//  get position of clicked button
//			var offset__ClickedSaveAsDefaultButton = $clickedThis.offset();
//			var offsetTop__SaveAsDefaultButton = offset__ClickedSaveAsDefaultButton.top;
//			var offsetLeft__SaveAsDefaultButton = offset__ClickedSaveAsDefaultButton.left;
//			var width__SaveAsDefaultButton = $clickedThis.outerWidth( true /* [includeMargin ] */ );;
//			//  adjust vertical position of dialog 
//			var height__current_url_saved_as_default_page_view_success_div = $current_url_saved_as_default_page_view_success.outerHeight( true /* [includeMargin ] */ );
//			var positionAdjustTop = offsetTop__SaveAsDefaultButton - ( height__current_url_saved_as_default_page_view_success_div / 2 );
//			$current_url_saved_as_default_page_view_success.css( "top", positionAdjustTop );
//			var width__current_url_saved_as_default_page_view_success_div = $current_url_saved_as_default_page_view_success.outerWidth( true /* [includeMargin ] */ );
//			var positionAdjustLeft = offsetLeft__SaveAsDefaultButton + ( width__SaveAsDefaultButton / 2 ) 
//			- ( width__current_url_saved_as_default_page_view_success_div / 2 );
//			if ( positionAdjustLeft < 5 ) {
//				positionAdjustLeft = 5;
//			}
//			$current_url_saved_as_default_page_view_success.css( "left", positionAdjustLeft );
//			$current_url_saved_as_default_page_view_success.show();

		var $search_moved_to_folder_msg_jq = $folder_root_jq.find(".search_moved_to_folder_msg_jq");
		var $element = $search_moved_to_folder_msg_jq;
		var clearMsg = true;
		this.clearMovedSearchToFolderMessage( $element, false /* fadeErrorMsg */ );
		$element.show();
//		var animateNewTop = offsetTop__SaveAsDefaultButton - height__current_url_saved_as_default_page_view_success_div - 10;
//		$element.animate( { top: animateNewTop }, { duration: 750 } );
		$element.css("top","-10px");
		$element.animate( { top: -50 }, { duration: 750 } );
		if ( clearMsg ) {
			var timerId = setTimeout( function() {
				objectThis.clearMovedSearchToFolderMessage( $element, true /* fade */ );
			}, _CONSTANTS.MESSAGE_HIDE_DELAY );
			$element.data( _CONSTANTS.MESSAGE_SET_TIMEOUT_DATA_KEY, timerId );
		}

//		}
	};
	
	//	Called from "onclick" in moved Search Message
	this.hideMovedSearchToFolderMessage = function ( clickedThis ) {
		try {
			var $clickedThis = $( clickedThis );
			var $folder_root_jq = $clickedThis.closest(".folder_root_jq");
			var $search_moved_to_folder_msg_jq = $folder_root_jq.find(".search_moved_to_folder_msg_jq");

			this.clearMovedSearchToFolderMessage( $search_moved_to_folder_msg_jq, false /* fadeErrorMsg */ );
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
	
	this.clearMovedSearchToFolderMessage = function ( $element, fadeErrorMsg ) {
		$element.stop( true /* [clearQueue ] */ /*  [, jumpToEnd ] */ );
		var setTimeoutId = $element.data( _CONSTANTS.MESSAGE_SET_TIMEOUT_DATA_KEY );
		if ( setTimeoutId ) {
			clearTimeout( setTimeoutId );
		}
		$element.data( _CONSTANTS.MESSAGE_SET_TIMEOUT_DATA_KEY, null );
		if ( fadeErrorMsg ) {
			$element.fadeOut( 400 /* [duration ] */  /* [, complete ] */ );  //  OR ( options_object )  duration (default: 400)
		} else {
			$element.hide();
		}
	};

	
	
	this.isFolder_searches_not_in_any_folder = function( params ) {
		var $folder_display_order_item_jq = params.$folder_display_order_item_jq;
		var searches_not_in_any_folder_String = $folder_display_order_item_jq.attr("data-searches_not_in_any_folder");
		var searches_not_in_any_folder = false;
		if ( searches_not_in_any_folder_String === "true" ) {
			return true;
		}
		return false;
	};

	this.removeHighlightFromAllFolders = function() {
		//  Remove highlight from all
		var $folder_display_order_inner_item_jq_All = 
			$( "#organize_searches_folder_entries_block .folder_display_order_inner_item_jq");
		$folder_display_order_inner_item_jq_All.removeClass("highlighted-item");
		var $organize_searches_folder_searches_not_in_any_folder_inner_item =
			$("#organize_searches_folder_searches_not_in_any_folder_inner_item");
		$organize_searches_folder_searches_not_in_any_folder_inner_item.removeClass("highlighted-item");
	};
	

	this.callCallbackForItemInFolder = function( params ) {
		var objectThis = this;
		var ui = params.ui;
		var $item = params.$item;
		var callbackOnInFolder = params.callbackOnInFolder;
		
		var searchDragItemInfo = objectThis.isSearchBeingDraggedInsideFolderGetSearchDragItemInfo( 
				{ ui : ui, $searchDragItem : $item } );

		// Determine if the element being dragged top and left are inside "Searches not in any folder"
		var $organize_searches_folder_searches_not_in_any_folder_outer_item =
			$("#organize_searches_folder_searches_not_in_any_folder_outer_item");
		if ( objectThis.isSearchBeingDraggedInsideFolder( { searchDragItemInfo : searchDragItemInfo, $folderItem : $organize_searches_folder_searches_not_in_any_folder_outer_item } ) ) {
			//  Search is in folder so process
			callbackOnInFolder( { $folder_display_order_item_jq_MouseIsOver : $organize_searches_folder_searches_not_in_any_folder_outer_item } );
			return; // Early Exit
		}
		
		// Determine if the element being dragged top and left are inside any of the folder elements
		var $folder_display_order_item_jq_All = 
			$( "#organize_searches_folder_entries_block" ).find(".folder_display_order_item_jq");
		$folder_display_order_item_jq_All.each(  function( index ) { 
			var $folder_display_order_item_jq_Single = $( this );
			
			if ( objectThis.isSearchBeingDraggedInsideFolder( { searchDragItemInfo : searchDragItemInfo, $folderItem : $folder_display_order_item_jq_Single } ) ) {
				//  Search is in folder so process
				callbackOnInFolder( { $folder_display_order_item_jq_MouseIsOver : $folder_display_order_item_jq_Single } );
				return false; // Exit processing in .each()
			}
			
		});
	};
	
	this.isSearchBeingDraggedInsideFolderGetSearchDragItemInfo = function( params ) {
		var ui = params.ui;
		var $searchDragItem = params.$searchDragItem;
		var searchDragItemInfo = { 
				searchDragHeight : $searchDragItem.height(),
				searchDragLeft : ui.offset.left,
				searchDragTop : ui.offset.top };
		
		searchDragItemInfo.searchDragVerticalMiddle = 
			searchDragItemInfo.searchDragTop + ( searchDragItemInfo.searchDragHeight / 2 );

		return searchDragItemInfo;
	};
	
	this.isSearchBeingDraggedInsideFolder = function( params ) {
		var searchDragItemInfo = params.searchDragItemInfo;
		var $folderItem = params.$folderItem;

		var folderOffset = $folderItem.offset();
		var folderTop = folderOffset.top;
		var folderLeft = folderOffset.left;
		var folderWidth = $folderItem.width()
		var folderOuterWidth = $folderItem.outerWidth( false /* [includeMargin ] */ );
		var folderHeight = $folderItem.height();
		var folderOuterHeight = $folderItem.outerHeight( false /* [includeMargin ] */ );
		var folderRight = folderLeft + folderWidth;
		var folderBottom = folderTop + folderHeight;
		if ( searchDragItemInfo.searchDragLeft > folderLeft && searchDragItemInfo.searchDragLeft < folderRight
				&& searchDragItemInfo.searchDragVerticalMiddle > folderTop && searchDragItemInfo.searchDragVerticalMiddle < folderBottom ) {
			return true; //  Search being dragged is in folder
		}
		return false; //  Search being dragged is NOT in folder		
	};

	
	//  User has selected a folder or "Searches not in any folder" by clicking on it
	this.processClick_OnFolder_Or_SearchesNotInAnyFolder = function( params ) {
		var objectThis = this;
		var clickedThis = params.clickedThis;
		var eventObject = params.eventObject;
		var $clickedThis = $(clickedThis);
		
		// Change search list (and highlighting) to clicked folder
		
		//  Remove highlighting from all folders and "Searches not in any folder"
		var $organize_searches_folder_entries_block_Local = $("#organize_searches_folder_entries_block");
		var $folder_display_order_inner_item_jq_Local = $organize_searches_folder_entries_block_Local.find(".folder_display_order_inner_item_jq");
		// Can be empty if no folders
//		if ( $folder_display_order_inner_item_jq_Local.length === 0 ) {
//			throw Error( 'no elements with class "folder_display_order_inner_item_jq" found under "#organize_searches_folder_entries_block"' );
//		}
		$folder_display_order_inner_item_jq_Local.removeClass("selected-item");
		$("#organize_searches_folder_searches_not_in_any_folder_inner_item").removeClass("selected-item");
		
		//  Add highlighting to chosen folder
//		var $folder_display_order_inner_item_jq_This = $clickedThis.closest(".folder_display_order_inner_item_jq");
		var $folder_display_order_inner_item_jq_This = $clickedThis;
		if ( $folder_display_order_inner_item_jq_This.length === 0 ) {
			throw Error( 'no elements with class "folder_display_order_inner_item_jq" above clicked element' );
		}
		$folder_display_order_inner_item_jq_This.addClass("selected-item");
		
		var $folder_display_order_item_jq_This = $clickedThis.closest(".folder_display_order_item_jq");
		if ( $folder_display_order_item_jq_This.length === 0 ) {
			throw Error( 'no elements with class "folder_display_order_item_jq" above clicked element' );
		}
		
		var folder_id = null;

		var folderIs__searches_not_in_any_folder = 
			objectThis.isFolder_searches_not_in_any_folder( { $folder_display_order_item_jq : $folder_display_order_item_jq_This } );

		if ( ! folderIs__searches_not_in_any_folder ) {
			//  Get folder id
			var folder_id_String = $folder_display_order_item_jq_This.attr("data-folder_id");
			if ( folder_id_String === undefined ) {
				throw Error( ' $folder_display_order_item_jq_This.attr("data-folder_id") returned undefined' );
			}
			if ( folder_id_String && folder_id_String !== "" ) {
				folder_id = parseInt( folder_id_String, 10 );
				if ( isNaN( folder_id) ) {
					throw Error( ' $folder_display_order_item_jq_This.attr("data-folder_id") returned string that cannot be parsed: ' + folder_id_String );
				}
			}
			_selectedFolderId = folder_id;
			_selectedFolderSearchesNotInAnyFolder = false;
		} else {
			_selectedFolderId = null;
			_selectedFolderSearchesNotInAnyFolder = true;
		}
		
		var callback = function( params ) {paramsForCall
			var paramsForCall = { paramsFrom_loadData : params, selectedFolderData : { folderId: folder_id } }; 
			objectThis.processLoadDataResultsForSpecificFolder( paramsForCall );
		}
		this.loadData( { callback : callback } );

		var z = 0;
	};
	
	this.processLoadDataResultsForSpecificFolder = function( params ) {
		var paramsFrom_loadData = params.paramsFrom_loadData;
		var selectedFolderData = params.selectedFolderData;
		var folderId = selectedFolderData.folderId;

		var loadData_responseData = paramsFrom_loadData.responseData;
		if ( ! loadData_responseData.status ) {
			window.location.reload(true);
			return;
		}
		if ( loadData_responseData.noSearchesFound ) {
			window.location.reload(true);
			return;
		}
		var folderDataList = loadData_responseData.folderDataList;
		var searchesNotInFoldersList = loadData_responseData.searchesNotInFoldersList;
		
		var searchesToDisplay = null;
		
		if ( folderId === null ) {
			searchesToDisplay = searchesNotInFoldersList
		} else {
			//  Get searches for folder id
			for ( var folderDataListIndex = 0; folderDataListIndex < folderDataList.length; folderDataListIndex++ ) {
				var folderDataEntry = folderDataList[ folderDataListIndex ];
				if ( folderDataEntry.id === folderId ) {
					searchesToDisplay = folderDataEntry.searchesForFolder;
//					var folderIndex = folderDataListIndex;
					break;
				}
			}
			if ( searchesToDisplay === null ) {
				//  No searches found for folder id
				throw Error( 'No folder found for id: ' + folderId );
			}
		}
		
		//  Pass searches to put on page

		this.addSearchesToPage( { searchesToDisplay : searchesToDisplay } );

	};
	
	
	
	/////////////////////
	this.changeSearchesOrderInDB = function() {
		try {
			var searchesInOrder = [];
			var $search_display_order_item_jq = $( "#organize_searches_search_entries_block" ).find(".search_display_order_item_jq");
			$search_display_order_item_jq.each( function() {
				var $this = $( this );
				var project_search_id = $this.attr("data-project_search_id");
				if ( project_search_id === undefined ) {
					throw Error( ' $this.attr("data-project_search_id") returned undefined' );
				}
				searchesInOrder.push( project_search_id );
			});
			var requestData = {
					searchesInOrder: searchesInOrder
			};
			var requestDataJSON = JSON.stringify( requestData );
			var _URL = "services/project/organizeSearchesSetSearchesOrder";
			// var request =
			$.ajax({
				type : "POST",
				url : _URL,
				data: requestDataJSON,
				contentType: "application/json; charset=utf-8",
				dataType: "json",
				success : function(data) {
					try {
						if ( ! data.status ) {
							window.location.reload(true);
						}
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
					// alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
					// textStatus: " + textStatus );
				}
			});
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
	
	///   Save new order of folders
	/////////////////////
	this.changeFoldersOrderInDB = function() {
		try {
			var foldersInOrder = [];
			var folder_display_order_item_jq_All = $( "#organize_searches_folder_entries_block" ).find(".folder_display_order_item_jq");
			folder_display_order_item_jq_All.each( function() {
				var $folder_display_order_item_jq_Each = $( this );
				var folder_id = $folder_display_order_item_jq_Each.attr("data-folder_id");
				if ( folder_id === undefined ) {
					throw Error( ' $folder_display_order_item_jq_Each.attr("data-folder_id") returned undefined' );
				}
				foldersInOrder.push( folder_id );
			});
			var requestData = {
					foldersInOrder: foldersInOrder
			};
			var requestDataJSON = JSON.stringify( requestData );
			var _URL = "services/project/folder/SetFoldersOrder";
			// var request =
			$.ajax({
				type : "POST",
				url : _URL,
				data: requestDataJSON,
				contentType: "application/json; charset=utf-8",
				dataType: "json",
				success : function(data) {
					try {
						if ( ! data.status ) {
							window.location.reload(true);
						}
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
					// alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
					// textStatus: " + textStatus );
				}
			});
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
	
	

	/////////////////////
	this.addNewFolder = function() {

		// $("#organize_searches_add_new_folder_button").click(function(eventObject) {
		try {
			var objectThis = this;
			var $organize_searches_folder_new_folder_name = $("#organize_searches_folder_new_folder_name");
			var folderName = $organize_searches_folder_new_folder_name.val();
			if ( folderName === undefined ) {
				throw Error( ' $("#organize_searches_folder_new_folder_name").val() returned undefined' );
			}
			folderName = folderName.trim();
			if ( folderName === "" ) {
				return;  // EARLY EXIT
			}
			var requestData = {
					project_id : adminGlobals.project_id,
					folderName: folderName
			};
			var _URL = "services/project/folder/addFolder";
			// var request =
			$.ajax({
				type : "POST",
				url : _URL,
				data : requestData,
				dataType : "json",
				success : function(responseData) {
					try {
						objectThis.addNewFolderProcessResponse( {  
							responseData : responseData,
							requestData : requestData 
						} );
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
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
	
	this.addNewFolderProcessResponse = function( params ) {
		var responseData = params.responseData;
		var requestData = params.requestData;
		if ( ! responseData.status ) {
			window.location.reload(true);
			return;
		}
		var $organize_searches_folder_new_folder_name = $("#organize_searches_folder_new_folder_name");
		$organize_searches_folder_new_folder_name.val("");
		$organize_searches_folder_new_folder_name.focus();
		//  Reload all data
		this.startSearchesOrganize();
	};

	//  Delete Folder processing
	/////////////////
	this.deleteFolderClickHandler = function(clickThis, eventObject) {
		this.openConfirmDeleteFolderOverlay(clickThis, eventObject);
		return;
	};

	///////////
	this.openConfirmDeleteFolderOverlay = function(clickThis, eventObject) {
		var $clickThis = $(clickThis);
//		get root div for this folder
		var $folder_root_jq = $clickThis.closest(".folder_root_jq");
		var folderId = $folder_root_jq.attr("data-folder_id");	
		if ( folderId === undefined ) {
			throw Error( "Error: attribute 'data-folder_id' not found on element with class 'folder_root_jq'" );
		}
		var in_organize_overlay = $folder_root_jq.attr("data-in_organize_overlay");
//		copy the folder name to the overlay
		var $folder_name_jq = $folder_root_jq.find(".folder_name_jq");
		var folder_name_jq = $folder_name_jq.text();
		var $delete_folder_overlay_folder_name = $("#delete_folder_overlay_folder_name");
		$delete_folder_overlay_folder_name.text( folder_name_jq );
		var $delete_folder_confirm_button = $("#delete_folder_confirm_button");
		$delete_folder_confirm_button.data("folderId", folderId);
		$delete_folder_confirm_button.data("in_organize_overlay", in_organize_overlay);
//		Position dialog over clicked delete icon
//		get position of div containing the dialog that is inline in the page
		var $delete_folder_overlay_containing_outermost_div_inline_div = $("#delete_folder_overlay_containing_outermost_div_inline_div");
		var offset__containing_outermost_div_inline_div = $delete_folder_overlay_containing_outermost_div_inline_div.offset();
		var offsetTop__containing_outermost_div_inline_div = offset__containing_outermost_div_inline_div.top;
		var offset__ClickedDeleteIcon = $clickThis.offset();
		var offsetTop__ClickedDeleteIcon = offset__ClickedDeleteIcon.top;
		var offsetDifference = offsetTop__ClickedDeleteIcon - offsetTop__containing_outermost_div_inline_div;
//		adjust vertical position of dialog 
		var $delete_folder_overlay_container = $("#delete_folder_overlay_container");
		var height__delete_folder_overlay_container = $delete_folder_overlay_container.outerHeight( true /* [includeMargin ] */ );
		var positionAdjust = offsetDifference - ( height__delete_folder_overlay_container / 2 );
		$delete_folder_overlay_container.css( "top", positionAdjust );
		var $delete_folder_overlay_background = $("#delete_folder_overlay_background"); 
		$delete_folder_overlay_background.show();
		$delete_folder_overlay_container.show();
	};

	///////////
	this.closeConfirmDeleteFolderOverlay = function(clickThis, eventObject) {
		var $delete_folder_confirm_button = $("#delete_folder_confirm_button");
		$delete_folder_confirm_button.data("folderId", null);
		$delete_folder_confirm_button.data("in_organize_overlay", null);
		$(".delete_folder_overlay_show_hide_parts_jq").hide();
	};

	/////////////////
	//  put click handler for this on #delete_folder_confirm_button
	this.deleteFolderConfirmed = function(clickThis, eventObject) {
		var objectThis = this;
		var $clickThis = $(clickThis);
		var folderId = $clickThis.data("folderId");
		var in_organize_overlay = $clickThis.data("in_organize_overlay");
		if ( folderId === undefined || folderId === null ) {
			throw Error( " folderId === undefined || folderId === null " );
		}
		if ( folderId === "" ) {
			throw Error( ' folderId === "" ' );
		}
		var _URL = "services/project/folder/deleteFolder";
//		var request = 
		$.ajax({
			type: "POST",
			url: _URL,
			data: { 'folderId' : folderId },
			dataType: "json",
			success: function(data)	{
				try {
					if ( in_organize_overlay === "true" ) {
						objectThis.startSearchesOrganize()
						objectThis.closeConfirmDeleteFolderOverlay();
					} else {
						window.location.reload(true);
						objectThis.closeConfirmDeleteFolderOverlay();
					}
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			},
			failure: function(errMsg) {
				handleAJAXFailure( errMsg );
			},
			error: function(jqXHR, textStatus, errorThrown) {	
				handleAJAXError( jqXHR, textStatus, errorThrown );
//				alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ", textStatus: " + textStatus );
			}
		});
	};


	//  END   Delete Folder processing


	//  Rename Folder processing
	/////////////////
	this.renameFolderClickHandler = function(clickThis, eventObject) {
		this.openConfirmRenameFolderOverlay(clickThis, eventObject);
		return;
	};

	///////////
	this.openConfirmRenameFolderOverlay = function(clickThis, eventObject) {
		var $clickThis = $(clickThis);
//		get root div for this folder
		var $folder_root_jq = $clickThis.closest(".folder_root_jq");
		var folderId = $folder_root_jq.attr("data-folder_id");	
		if ( folderId === undefined ) {
			throw Error( "Error: attribute 'data-folder_id' not found on element with class 'folder_root_jq'" );
		}
		var in_organize_overlay = $folder_root_jq.attr("data-in_organize_overlay");
//		copy the folder name to the overlay
		var $folder_name_jq = $folder_root_jq.find(".folder_name_jq");
		var folder_name_jq = $folder_name_jq.text();
		var $rename_folder_overlay_folder_name = $("#rename_folder_overlay_folder_name");
		$rename_folder_overlay_folder_name.val( folder_name_jq );
		var $rename_folder_save_button = $("#rename_folder_save_button");
		$rename_folder_save_button.data("folderId", folderId);
		$rename_folder_save_button.data("in_organize_overlay", in_organize_overlay);
//		Position dialog over clicked rename icon
//		get position of div containing the dialog that is inline in the page
		var $rename_folder_overlay_containing_outermost_div_inline_div = $("#rename_folder_overlay_containing_outermost_div_inline_div");
		var offset__containing_outermost_div_inline_div = $rename_folder_overlay_containing_outermost_div_inline_div.offset();
		var offsetTop__containing_outermost_div_inline_div = offset__containing_outermost_div_inline_div.top;
		var offset__ClickedRenameIcon = $clickThis.offset();
		var offsetTop__ClickedRenameIcon = offset__ClickedRenameIcon.top;
		var offsetDifference = offsetTop__ClickedRenameIcon - offsetTop__containing_outermost_div_inline_div;
//		adjust vertical position of dialog 
		var $rename_folder_overlay_container = $("#rename_folder_overlay_container");
		var height__rename_folder_overlay_container = $rename_folder_overlay_container.outerHeight( true /* [includeMargin ] */ );
		var positionAdjust = offsetDifference - ( height__rename_folder_overlay_container / 2 );
		$rename_folder_overlay_container.css( "top", positionAdjust );
		var $rename_folder_overlay_background = $("#rename_folder_overlay_background"); 
		$rename_folder_overlay_background.show();
		$rename_folder_overlay_container.show();
	};

	///////////
	this.closeConfirmRenameFolderOverlay = function(clickThis, eventObject) {
		var $rename_folder_save_button = $("#rename_folder_save_button");
		$rename_folder_save_button.data("folderId", null);
		$rename_folder_save_button.data("in_organize_overlay", null);
		$(".rename_folder_overlay_show_hide_parts_jq").hide();
	};

	/////////////////
	//  put click handler for this on #rename_folder_save_button
	this.renameFolderSaveNewName = function(clickThis, eventObject) {
		var objectThis = this;
		var $clickThis = $(clickThis);
		var folderId = $clickThis.data("folderId");
		var in_organize_overlay = $clickThis.data("in_organize_overlay");
		if ( folderId === undefined || folderId === null ) {
			throw Error( " folderId === undefined || folderId === null " );
		}
		if ( folderId === "" ) {
			throw Error( ' folderId === "" ' );
		}
		var $rename_folder_overlay_folder_name = $("#rename_folder_overlay_folder_name");
		var folderName = $rename_folder_overlay_folder_name.val();
		if ( folderName === "" ) {
			return;  // EARLY EXIT
		}
		var _URL = "services/project/folder/renameFolder";
//		var request = 
		$.ajax({
			type: "POST",
			url: _URL,
			data: { 'folderId' : folderId, folderName : folderName },
			dataType: "json",
			success: function(data)	{
				try {
					if ( in_organize_overlay === "true" ) {
						objectThis.startSearchesOrganize()
						objectThis.closeConfirmRenameFolderOverlay();
					} else {
						window.location.reload(true);
						objectThis.closeConfirmRenameFolderOverlay();
					}
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			},
			failure: function(errMsg) {
				handleAJAXFailure( errMsg );
			},
			error: function(jqXHR, textStatus, errorThrown) {	
				handleAJAXError( jqXHR, textStatus, errorThrown );
//				alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ", textStatus: " + textStatus );
			}
		});
	};


	//  END   Rename Folder processing


	
};

//  Instance of class
var organizeSearches = new OrganizeSearches();

window.organizeSearches = organizeSearches;

export { organizeSearches }
