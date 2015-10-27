
	
//    viewProjectPage.js


var _project_id = null;


	 		$(document).ready(function()  { 
				
	 			
	 			initPage();
					
			});
	 		
	 		/////////////
	 		
	 		function initPage() {
	 			
	 			
	 			project_id = $("#project_id").val();
	 			
	 			if ( project_id === undefined || project_id === null 
	 					|| project_id === "" ) {
	 				
	 				throw '$("#project_id").val() returned no value';
	 			
	 			} else {
	 				
	 				_project_id = project_id;
	 			}
	 			
	 			
	 			
	 			//  tool tips for files attached to searches
	 			
	 			
	 			$(".search_file_link_for_tooltip_jq").each(function() { // Grab search file links
	 				
	 				var $search_file_link_tooltip_jq = $(this).children(".search_file_link_tooltip_jq");
	 				
	 				if ( $search_file_link_tooltip_jq.length > 0 ) {

	 					var tipText = $search_file_link_tooltip_jq.text();

	 					$(this).qtip({ 
	 						content: {
	 							text: tipText
	 						}
	 					});
	 				}
	 			});
	 			
	 			
	 			
	 			//   Delete Search click handlers
	 			

	 			$(".delete_search_link_jq").click(function(eventObject) {

	 				var clickThis = this;

	 				deleteSearchClickHandler( clickThis, eventObject );
	 				
	 				return false;
	 			});	 			
	 			
	 			
	 			$("#delete_search_confirm_button").click(function(eventObject) {

	 				var clickThis = this;

	 				deleteSearchConfirmed( clickThis, eventObject );

	 				return false;
	 			});
	 			

	 			$(".delete_search_overlay_show_hide_parts_jq").click(function(eventObject) {

	 				var clickThis = this;

	 				closeConfirmDeleteSearchOverlay( clickThis, eventObject );
	 				
	 				return false;
	 			});
	 			

	 			
	 			///////  Delete Search Weblink Click handlers
	 			
	 			

	 			$(".delete_search_webLink_link_jq").click(function(eventObject) {

	 				var clickThis = this;

	 				deleteSearchWebLinkClickHandler( clickThis, eventObject );
	 				
	 				return false;
	 			});	 			

	 			
	 			$("#delete_search_web_link_confirm_button").click(function(eventObject) {

	 				var clickThis = this;

	 				deleteSearchWebLinkConfirmed( clickThis, eventObject );

	 				return false;
	 			});
	 			

	 			$(".delete_search_web_link_overlay_show_hide_parts_jq").click(function(eventObject) {

	 				var clickThis = this;

	 				closeConfirmDeleteSearchWebLinkOverlay( clickThis, eventObject );
	 				
	 				return false;
	 			});
	 			
	 			
	 			
	 			
	 			///////  Delete Search Comment Click handlers
	 			
	 			
	 			
	 			$("#delete_search_comment_confirm_button").click(function(eventObject) {

	 				var clickThis = this;

	 				deleteSearchCommentConfirmed( clickThis, eventObject );

	 				return false;
	 			});
	 			

	 			$(".delete_search_comment_overlay_show_hide_parts_jq").click(function(eventObject) {

	 				var clickThis = this;

	 				closeConfirmDeleteSearchCommentOverlay( clickThis, eventObject );
	 				
	 				return false;
	 			});
	 			
	 			
	 			
	 			/////////////////////////
	 			
	 			
	 			
	 			initQCPlotsClickHandlers();
	 			
	 			
	 			
	 			//  Initialize the buttons from the current values of the check boxes.
	 			//     The check boxes may be checked from using the back button.
	 			updateButtonsBasedOnCheckedSearches ( );
					
	 		}
	 		
	 		
	 		/////////////
			
			var searchesToMerge = new Array();
			
			
			

			///////////////
			
			
			function validateURL(textval) {
			      var urlregex = new RegExp(
			            "^(http|https|ftp)\://([a-zA-Z0-9\.\-]+(\:[a-zA-Z0-9\.&amp;%\$\-]+)*@)*((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|([a-zA-Z0-9\-]+\.)*[a-zA-Z0-9\-]+\.(com|edu|gov|int|mil|net|org|biz|arpa|info|name|pro|aero|coop|museum|[a-zA-Z]{2}))(\:[0-9]+)*(/($|[a-zA-Z0-9\.\,\?\'\\\+&amp;%\$#\=~_\-]+))*$");
			      return urlregex.test(textval);
			}
			
			
			//////////

			function addWebLink( searchId ) {
			
				var _URL = contextPathJSVar + "/services/searchWebLinks/add";
				
				var $linkUrlInputField = $( "input#web-links-url-input-" + searchId );
				
				var linkUrl = $linkUrlInputField.val();
				if( linkUrl === undefined || linkUrl === "" ) { return; }

				var linkLabel = $( "input#web-links-label-input-" + searchId ).val();
				
				if( linkLabel === undefined || linkLabel === "" ) { 
					
					linkLabel = linkUrl;
				}
				
				
				
				
				if ( validateURL( linkUrl )  ) {
					
				} else {
					
//					alert("url not valid");
					
					var $element = $("#error_message_web_link_url_invalid_" + searchId );
					
//					var linkUrlInputFieldTop = $linkUrlInputField.offset().top;
//					
//					$element.css("top", linkUrlInputFieldTop );
					
					showErrorMsg( $element );
						
					return;  //  !!!  EARLY EXIT
				}
				
				
				
//				var request = 
				$.ajax({
				        type: "POST",
				        url: _URL,
				        data: { 'searchId' : searchId, 'linkUrl' : linkUrl, linkLabel: linkLabel },
				        dataType: "json",
				        success: function(data)	{
					        // add new web link to DOM
				        	
				        	var id = data.id;

				    		var source = $("#web_link_template").html();

				    		if ( source === undefined ) {
				    			throw '$("#web_link_template").html() === undefined';
				    		}
				    		if ( source === null ) {
				    			throw '$("#web_link_template").html() === null';
				    		}
				    		
				    		var template = Handlebars.compile(source);

				    		var context = data;
				    		
				    		var html = template(context);

				    		var web_link_root_container_div_jq = $( html ).insertBefore( "#add-web-links-link-span-" + searchId );
							addToolTips( web_link_root_container_div_jq );

				    		
//				    		attachProjectNoteMaintOnClick( web_link_root_container_div_jq );

//				    		$("#add_note_field").val("");

				    		
							
							$("#web-links-delete-" + id).click(function(eventObject) {

				 				var clickThis = this;

				 				deleteSearchWebLinkClickHandler( clickThis, eventObject );
				 				
				 				return false;
				 			});	 	
							
							$( "div#web-links-" + id ).show( 200 );
							
							$( "#add-web-links-form-span-" + searchId ).hide();
							$( "#add-web-links-link-span-" + searchId ).show();
							 $( "input#web-links-input-" + searchId ).val( "" );
							
														
						},
				        failure: function(errMsg) {
				        	handleAJAXFailure( errMsg );
				        },
				        error: function(jqXHR, textStatus, errorThrown) {	
				        	
							handleAJAXError( jqXHR, textStatus, errorThrown );

//								alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ", textStatus: " + textStatus );
						}
				  });
			}

			
			//////////

			function cancelWebLink( id ) {
				$( "#add-web-links-form-span-" + id ).hide();
				$( "#add-web-links-link-span-" + id ).show();
			}
			
			function showAddWebLink( id ) {
				
				$( "input#web-links-url-input-" + id ).val("");
				$( "input#web-links-label-input-" + id ).val( "" );

				$( "#add-web-links-form-span-" + id ).show();
				$( "#add-web-links-link-span-" + id ).hide();
				$( "#web-links-url-input-" + id ).focus();
			}

			
			////////////////////
			
			//  Files for Search Maint
			

			//////////

			function saveSearchFilename( clickThis ) {
			
				var _URL = contextPathJSVar + "/services/search_file/updateDisplayFilename";
				
				
				var $clickThis = $( clickThis );
				
				var $display_search_filename_outer_container_jq = $clickThis.closest(".display_search_filename_outer_container_jq");
				
				var search_file_id = $display_search_filename_outer_container_jq.attr("search_file_id");
//				var search_id = $display_search_filename_outer_container_jq.attr("search_id");
				
				var $edit_search_filename_input_field_jq = $display_search_filename_outer_container_jq.find(".edit_search_filename_input_field_jq");
				
				var edit_search_filename_input_value = $edit_search_filename_input_field_jq.val(); 

				if ( edit_search_filename_input_value === "" ) {
					
					return;
				}
				
				var requestData = { 'project_id' : project_id, 
						'searchFileId' : search_file_id, 
						displayFilename: edit_search_filename_input_value };
						
				
//				var request = 
				$.ajax({
				        type: "POST",
				        url: _URL,
				        data: requestData,
				        dataType: "json",
				        success: function(data)	{
					        
				        	var $search_filename_jq = $display_search_filename_outer_container_jq.find(".search_filename_jq");
				        	$search_filename_jq.text( edit_search_filename_input_value );
				        	
							var $display_search_filename_container_jq = $display_search_filename_outer_container_jq.find(".display_search_filename_container_jq");
							var $edit_search_filename_container_jq = $display_search_filename_outer_container_jq.find(".edit_search_filename_container_jq");
							$edit_search_filename_container_jq.hide();
							$display_search_filename_container_jq.show();
							
						},
				        failure: function(errMsg) {
				        	handleAJAXFailure( errMsg );
				        },
				        error: function(jqXHR, textStatus, errorThrown) {	
				        	
							handleAJAXError( jqXHR, textStatus, errorThrown );

//								alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ", textStatus: " + textStatus );
						}
				  });
			}

			
			//////////

			function cancelSearchFilenameEdit( clickThis ) {
				
				var $clickThis = $( clickThis );
				
				var $display_search_filename_outer_container_jq = $clickThis.closest(".display_search_filename_outer_container_jq");
				
				var $display_search_filename_container_jq = $display_search_filename_outer_container_jq.find(".display_search_filename_container_jq");
				var $edit_search_filename_container_jq = $display_search_filename_outer_container_jq.find(".edit_search_filename_container_jq");
				$edit_search_filename_container_jq.hide();
				$display_search_filename_container_jq.show();
			}
			
			function showSearchFilenameForm( clickThis ) {

				var $clickThis = $( clickThis );
				
				var $display_search_filename_outer_container_jq = $clickThis.closest(".display_search_filename_outer_container_jq");
				
				var $display_search_filename_container_jq = $display_search_filename_outer_container_jq.find(".display_search_filename_container_jq");
				var $edit_search_filename_container_jq = $display_search_filename_outer_container_jq.find(".edit_search_filename_container_jq");
				$edit_search_filename_container_jq.show();
				$display_search_filename_container_jq.hide();
			}

			
			
			///////////////////////////////
			
			
			////////////  Search Comment
			
			
			
			

			///////////////
						
			function showSearchCommentEditForm( clickThis ) {

				var $clickThis = $( clickThis );
				
				var $search_comment_root_jq = $clickThis.closest(".search_comment_root_jq");
				
				var $search_comment_display_jq = $search_comment_root_jq.find(".search_comment_display_jq");
				var $search_comment_edit_jq = $search_comment_root_jq.find(".search_comment_edit_jq");
				
				var $search_comment_string_jq = $search_comment_root_jq.find(".search_comment_string_jq");
				var $search_comment_input_field_jq = $search_comment_root_jq.find(".search_comment_input_field_jq");
				
				var search_comment_value = $search_comment_string_jq.text();
				$search_comment_input_field_jq.val( search_comment_value );
				
				
				$search_comment_edit_jq.show();
				$search_comment_display_jq.hide();
			}
			


			///////////////
						
			function cancelSearchCommentEditForm( clickThis ) {

				var $clickThis = $( clickThis );
				
				var $search_comment_root_jq = $clickThis.closest(".search_comment_root_jq");
				
				var $search_comment_display_jq = $search_comment_root_jq.find(".search_comment_display_jq");
				var $search_comment_edit_jq = $search_comment_root_jq.find(".search_comment_edit_jq");
				
				
				$search_comment_edit_jq.hide();
				$search_comment_display_jq.show();
			}			

			///////////////
			
			
			
			//////////

			function updateSearchComment( clickThis ) {
			
				var _URL = contextPathJSVar + "/services/searchComment/updateText";

				var $clickThis = $( clickThis );
				
				var $search_comment_root_jq = $clickThis.closest(".search_comment_root_jq");
				
				var searchCommentId = $search_comment_root_jq.attr("searchCommentId");	

				var $search_comment_input_field_jq = $search_comment_root_jq.find(".search_comment_input_field_jq");
				
				var search_comment_value = $search_comment_input_field_jq.val();
				
				
				
//				var request = 
				$.ajax({
				        type: "POST",
				        url: _URL,
				        data: { 'id' : searchCommentId, 'comment' : search_comment_value },
				        dataType: "json",
				        success: function(data)	{
				        	
					        // update comment 
							
							var $search_comment_string_jq = $search_comment_root_jq.find(".search_comment_string_jq");
							var $search_comment_date_jq = $search_comment_root_jq.find(".search_comment_date_jq");

							$search_comment_string_jq.text( data.comment );
							$search_comment_date_jq.text( data.dateTimeString );
							
							var $search_comment_display_jq = $search_comment_root_jq.find(".search_comment_display_jq");
							var $search_comment_edit_jq = $search_comment_root_jq.find(".search_comment_edit_jq");
							
							$search_comment_edit_jq.hide();
							$search_comment_display_jq.show();
							
														
						},
				        failure: function(errMsg) {
				        	handleAJAXFailure( errMsg );
				        },
				        error: function(jqXHR, textStatus, errorThrown) {	
				        	
							handleAJAXError( jqXHR, textStatus, errorThrown );
						}
				  });
			}

						
			
			
			//////////

			function addComment( searchId ) {
			
				var _URL = contextPathJSVar + "/services/searchComment/add";
				
				var comment = $( "input#comment-input-" + searchId ).val();
				if( comment == undefined || comment == "" ) { return; }
				
				
				
//				var request = 
				$.ajax({
				        type: "POST",
				        url: _URL,
				        data: { 'searchId' : searchId, 'comment' : comment },
				        dataType: "json",
				        success: function(data)	{
					        // add new comment to DOM
							
							var id = data[ 'id' ];
		

				    		var source = $("#search_comment_template").html();

				    		if ( source === undefined ) {
				    			throw '$("#search_comment_template").html() === undefined';
				    		}
				    		if ( source === null ) {
				    			throw '$("#search_comment_template").html() === null';
				    		}
				    		
				    		var template = Handlebars.compile(source);

				    		var context = data;
				    		
				    		var html = template(context);

//				    		var comment_root_container_div_jq = 
				    		$inserted = $( html ).insertBefore( "span#add-comment-link-span-" + searchId );
							addToolTips( $inserted );
				    		
							$( "div#comment-" + id ).show( 200 );
							
							$( "span#add-comment-form-span-" + searchId ).hide();
							$( "span#add-comment-link-span-" + searchId ).show();
							
							$( "input#comment-input-" + searchId ).val( "" );
							
														
						},
				        failure: function(errMsg) {
				        	handleAJAXFailure( errMsg );
				        },
				        error: function(jqXHR, textStatus, errorThrown) {	
				        	
							handleAJAXError( jqXHR, textStatus, errorThrown );

//								alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ", textStatus: " + textStatus );
						}
				  });
			}

			
			//////////

			function cancelComment( id ) {
				$( "span#add-comment-form-span-" + id ).hide();
				$( "span#add-comment-link-span-" + id ).show();
			}
			
			function showAddComment( id ) {
				$( "span#add-comment-form-span-" + id ).show();
				$( "span#add-comment-link-span-" + id ).hide();
				$( "#comment-input-" + id ).focus();
			}


			
			//   Delete Search processing
			

			/////////////////

			var deleteSearchClickHandler = function(clickThis, eventObject) {

				openConfirmDeleteSearchOverlay(clickThis, eventObject);

				return;

			};

			///////////
			
			var openConfirmDeleteSearchOverlay = function(clickThis, eventObject) {

				var $clickThis = $(clickThis);

				//	get root div for this search
				var $search_root_jq = $clickThis.closest(".search_root_jq");

				var searchId = $search_root_jq.attr("searchId");	

				// copy the search name to the overlay

				var $search_name_display_jq = $search_root_jq.find(".search_name_display_jq");

				var search_name_display_jq = $search_name_display_jq.text();

				var $delete_search_overlay_search_name = $("#delete_search_overlay_search_name");
				$delete_search_overlay_search_name.text( search_name_display_jq );



				var $delete_search_confirm_button = $("#delete_search_confirm_button");
				$delete_search_confirm_button.data("searchId", searchId);
				
				// Position dialog over clicked delete icon
				
				//  get position of div containing the dialog that is inline in the page
				var $delete_search_overlay_containing_outermost_div_inline_div = $("#delete_search_overlay_containing_outermost_div_inline_div");
				
				var offset__containing_outermost_div_inline_div = $delete_search_overlay_containing_outermost_div_inline_div.offset();
				var offsetTop__containing_outermost_div_inline_div = offset__containing_outermost_div_inline_div.top;
				
				var offset__ClickedDeleteIcon = $clickThis.offset();
				var offsetTop__ClickedDeleteIcon = offset__ClickedDeleteIcon.top;
				
				var offsetDifference = offsetTop__ClickedDeleteIcon - offsetTop__containing_outermost_div_inline_div;
				
				//  adjust vertical position of dialog 
				
				var $delete_search_overlay_container = $("#delete_search_overlay_container");
				
				var height__delete_search_overlay_container = $delete_search_overlay_container.outerHeight( true /* [includeMargin ] */ );
				
				var positionAdjust = offsetDifference - ( height__delete_search_overlay_container / 2 );
				
				$delete_search_overlay_container.css( "top", positionAdjust );

				
				var $delete_search_overlay_background = $("#delete_search_overlay_background"); 
				$delete_search_overlay_background.show();
				$delete_search_overlay_container.show();
			};

			//////////	/

			var closeConfirmDeleteSearchOverlay = function(clickThis, eventObject) {

				var $delete_search_confirm_button = $("#delete_search_confirm_button");
				$delete_search_confirm_button.data("searchId", null);

				$(".delete_search_overlay_show_hide_parts_jq").hide();
			};


			/////////////////

			//	put click handler for this on #delete_search_confirm_button

			var deleteSearchConfirmed = function(clickThis, eventObject) {
				

				var $clickThis = $(clickThis);

				var searchId = $clickThis.data("searchId");
				
				if ( searchId === undefined || searchId === null ) {
					
					throw " searchId === undefined || searchId === null ";
				}

				if ( searchId === "" ) {
					
					throw ' searchId === "" ';
				}

				document.location.href= contextPathJSVar + "/deleteSearch.do?searchId=" + searchId;
								
				
				closeConfirmDeleteSearchOverlay();
			};
			
			

			//   END   Delete Search processing
			
			
			
			

			
			//   Delete Search Comment processing
			

			/////////////////

			var deleteSearchCommentClickHandler = function(clickThis) {

				openConfirmDeleteSearchCommentOverlay(clickThis);

				return;

			};

			///////////
			
			var openConfirmDeleteSearchCommentOverlay = function(clickThis) {

				var $clickThis = $(clickThis);

				//	get root div for this search
				var $search_root_jq = $clickThis.closest(".search_comment_root_jq");

				var searchCommentId = $search_root_jq.attr("searchCommentId");	



				var $delete_search_comment_confirm_button = $("#delete_search_comment_confirm_button");
				$delete_search_comment_confirm_button.data("searchCommentId", searchCommentId);
				
				// Position dialog over clicked delete icon
				
				//  get position of div containing the dialog that is inline in the page
				var $delete_search_comment_overlay_containing_outermost_div_inline_div = $("#delete_search_comment_overlay_containing_outermost_div_inline_div");
				
				var offset__containing_outermost_div_inline_div = $delete_search_comment_overlay_containing_outermost_div_inline_div.offset();
				var offsetTop__containing_outermost_div_inline_div = offset__containing_outermost_div_inline_div.top;
				
				var offset__ClickedDeleteIcon = $clickThis.offset();
				var offsetTop__ClickedDeleteIcon = offset__ClickedDeleteIcon.top;
				
				var offsetDifference = offsetTop__ClickedDeleteIcon - offsetTop__containing_outermost_div_inline_div;
				
				//  adjust vertical position of dialog 
				
				var $delete_search_comment_overlay_container = $("#delete_search_comment_overlay_container");
				
				var height__delete_search_comment_overlay_container = $delete_search_comment_overlay_container.outerHeight( true /* [includeMargin ] */ );
				
				var positionAdjust = offsetDifference - ( height__delete_search_comment_overlay_container / 2 );
				
				$delete_search_comment_overlay_container.css( "top", positionAdjust );

				
				var $delete_search_comment_overlay_background = $("#delete_search_comment_overlay_background"); 
				$delete_search_comment_overlay_background.show();
				$delete_search_comment_overlay_container.show();
			};

			//////////	/

			var closeConfirmDeleteSearchCommentOverlay = function(clickThis, eventObject) {

				var $delete_search_comment_confirm_button = $("#delete_search_comment_confirm_button");
				$delete_search_comment_confirm_button.data("searchCommentId", null);

				$(".delete_search_comment_overlay_show_hide_parts_jq").hide();
			};


			/////////////////

			//	put click handler for this on #delete_search_comment_confirm_button

			var deleteSearchCommentConfirmed = function(clickThis, eventObject) {
				

				var $clickThis = $(clickThis);

				var searchCommentId = $clickThis.data("searchCommentId");
				
				if ( searchCommentId === undefined || searchCommentId === null ) {
					
					throw " searchCommentId === undefined || searchCommentId === null ";
				}

				if ( searchCommentId === "" ) {
					
					throw ' searchCommentId === "" ';
				}

				var _URL = contextPathJSVar + "/services/searchComment/delete";

//				var request = 
				$.ajax({
				        type: "POST",
				        url: _URL,
				        data: { 'id' : searchCommentId },
				        dataType: "json",
				        success: function(data)	{
					        $( "div#comment-" + searchCommentId ).hide(200, function() { $( "div#comment-" + searchCommentId ).remove(); });
						},
				        failure: function(errMsg) {
				        	handleAJAXFailure( errMsg );
				        },
						error: function(jqXHR, textStatus, errorThrown) {	
						
							handleAJAXError( jqXHR, textStatus, errorThrown );

//								alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ", textStatus: " + textStatus );
						}
				  });
				
				closeConfirmDeleteSearchCommentOverlay();
			};
			
			

			//   END   Delete Search Comment processing
			
			
			
			

			
			//   Delete Search Web Link processing
			

			/////////////////

			var deleteSearchWebLinkClickHandler = function(clickThis, eventObject) {

				openConfirmDeleteSearchWebLinkOverlay(clickThis, eventObject);

				return;

			};

			///////////
			
			var openConfirmDeleteSearchWebLinkOverlay = function(clickThis, eventObject) {

				var $clickThis = $(clickThis);

				//	get root div for this search
				var $search_root_jq = $clickThis.closest(".search_web_link_root_jq");

				var searchwebLinkId = $search_root_jq.attr("searchwebLinkId");	



				var $delete_search_web_link_confirm_button = $("#delete_search_web_link_confirm_button");
				$delete_search_web_link_confirm_button.data("searchwebLinkId", searchwebLinkId);
				
				// Position dialog over clicked delete icon
				
				//  get position of div containing the dialog that is inline in the page
				var $delete_search_web_link_overlay_containing_outermost_div_inline_div = $("#delete_search_web_link_overlay_containing_outermost_div_inline_div");
				
				var offset__containing_outermost_div_inline_div = $delete_search_web_link_overlay_containing_outermost_div_inline_div.offset();
				var offsetTop__containing_outermost_div_inline_div = offset__containing_outermost_div_inline_div.top;
				
				var offset__ClickedDeleteIcon = $clickThis.offset();
				var offsetTop__ClickedDeleteIcon = offset__ClickedDeleteIcon.top;
				
				var offsetDifference = offsetTop__ClickedDeleteIcon - offsetTop__containing_outermost_div_inline_div;
				
				//  adjust vertical position of dialog 
				
				var $delete_search_web_link_overlay_container = $("#delete_search_web_link_overlay_container");
				
				var height__delete_search_web_link_overlay_container = $delete_search_web_link_overlay_container.outerHeight( true /* [includeMargin ] */ );
				
				var positionAdjust = offsetDifference - ( height__delete_search_web_link_overlay_container / 2 );
				
				$delete_search_web_link_overlay_container.css( "top", positionAdjust );

				
				var $delete_search_web_link_overlay_background = $("#delete_search_web_link_overlay_background"); 
				$delete_search_web_link_overlay_background.show();
				$delete_search_web_link_overlay_container.show();
			};

			//////////	/

			var closeConfirmDeleteSearchWebLinkOverlay = function(clickThis, eventObject) {

				var $delete_search_web_link_confirm_button = $("#delete_search_web_link_confirm_button");
				$delete_search_web_link_confirm_button.data("searchwebLinkId", null);

				$(".delete_search_web_link_overlay_show_hide_parts_jq").hide();
			};


			/////////////////

			//	put click handler for this on #delete_search_web_link_confirm_button

			var deleteSearchWebLinkConfirmed = function(clickThis, eventObject) {
				

				var $clickThis = $(clickThis);

				var searchwebLinkId = $clickThis.data("searchwebLinkId");
				
				if ( searchwebLinkId === undefined || searchwebLinkId === null ) {
					
					throw " searchwebLinkId === undefined || searchwebLinkId === null ";
				}

				if ( searchwebLinkId === "" ) {
					
					throw ' searchwebLinkId === "" ';
				}

				var _URL = contextPathJSVar + "/services/searchWebLinks/delete";

//				var request = 
				$.ajax({
				        type: "POST",
				        url: _URL,
				        data: { 'id' : searchwebLinkId },
				        dataType: "json",
				        success: function(data)	{
					        $( "div#web-links-" + searchwebLinkId ).hide(200, function() { $( "div#web-links-" + searchwebLinkId ).remove(); });
						},
				        failure: function(errMsg) {
				        	handleAJAXFailure( errMsg );
				        },
						error: function(jqXHR, textStatus, errorThrown) {	
						
							handleAJAXError( jqXHR, textStatus, errorThrown );

//								alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ", textStatus: " + textStatus );
						}
				  });
					
				
				closeConfirmDeleteSearchWebLinkOverlay();
			};
			
			

			//   END   Delete Search Web Link processing
			
			
			
						
			
						
			
			
			
			//////////
			
			function checkSearchCheckboxes( searchId) {

				
				if( $( "input#search-checkbox-" + searchId ).is( ":checked" ) ) {
					if( searchesToMerge.indexOf( searchId ) == -1 ) { searchesToMerge.push( searchId ); }
				} else {
					var index = searchesToMerge.indexOf( searchId );
					if( index != -1 ) {
						searchesToMerge.splice( index, 1 );
					}
				}
				
				updateButtonsBasedOnCheckedSearches();
			}
			
			
			//////////
			
			function updateButtonsBasedOnCheckedSearches ( ) {
				
				var count = 0;
				
				$( ".search-checkbox" ).each( function() {
					if( $( this ).is( ":checked" ) ) {
						count++;
					}
				});				

				if( count < 2 ) { 
					disableButtons(); 
				} else { 
					enableButtons(); 
				}
				
				//  The following function is only on the page for admin so an exception will occur for non admin
				
				try {
					updateMoveSearchesButtonFromSearchCheckboxes( count );
				}
				catch(err) {
				    
				}					
				
			}
			
			
			//////////
			
			function disableButtons() {
				$( ".merge-button" ).attr("disabled", "disabled"); 
				//  show covering div
				$(".merge_button_disabled_cover_div_jq").show();
			}
			
			function enableButtons() {
				$( ".merge-button" ).removeAttr("disabled"); 
				//  hide covering div
				$(".merge_button_disabled_cover_div_jq").hide();
			}
			
			function viewMergedPeptides() {
				$( "form#viewMergedDataForm" ).attr("action", contextPathJSVar + "/viewMergedPeptide.do");
				$( "form#viewMergedDataForm" ).submit();
			}
			
			function viewMergedProteins() {
				$( "form#viewMergedDataForm" ).attr("action", contextPathJSVar + "/viewMergedCrosslinkProtein.do");
				$( "form#viewMergedDataForm" ).submit();
			}

			
			function showSearchDetails( id ) {
				
				if( $( "table#search-details-" + id ).is( ":visible" ) ) {
					$( "table#search-details-" + id ).hide();
//					$( "a#search-details-link-" + id ).html( "[+]" );
					$( "a#search-details-link-" + id ).html( '<img src="' + contextPathJSVar + '/images/icon-expand-small.png">' );
					
				} else {
					$( "table#search-details-" + id ).show();
//					$( "a#search-details-link-" + id ).html( "[-]" );
					$( "a#search-details-link-" + id ).html( '<img src="' + contextPathJSVar + '/images/icon-collapse-small.png">' );
				}
				
			}
			
			function expandAll() {
				$( "table.search-details" ).show();
				$( "a.expand-link" ).html( '<img src="' + contextPathJSVar + '/images/icon-collapse-small.png">' );
			}
			
			function collapseAll() {
				$( "table.search-details" ).hide();
				$( "a.expand-link" ).html( '<img src="' + contextPathJSVar + '/images/icon-expand-small.png">' );
			}
			
			
			function showSearchNameForm( id ) {
				$( "span#search-name-normal-" + id ).hide();
				$( "span#search-name-edit-" + id ).show();	
			}
			
			function cancelNameEdit( id ) {
				$( "span#search-name-edit-" + id ).hide();
				$( "span#search-name-normal-" + id ).show();
				
				$( "input#search-name-value-" + id ).val( $( "span#search-name-display-" + id ).html() );
			}
			
			
			//////////

			function saveName( searchId ) {
				var _URL = contextPathJSVar + "/services/searchName/save";
				
				var name = $( "input#search-name-value-" + searchId ).val();
				if( name == undefined || name == "" ) { return; }
				
//				var request = 
				$.ajax({
				        type: "POST",
				        url: _URL,
				        data: { 'searchId' : searchId, 'name' : name },
				        dataType: "json",
				        success: function(data)	{
														
							$( "span#search-name-display-" + searchId ).html( name );
							$( "span#search-name-edit-" + searchId ).hide();
							$( "span#search-name-normal-" + searchId ).show();
														
						},
				        failure: function(errMsg) {
				        	handleAJAXFailure( errMsg );
				        },
				        error: function(jqXHR, textStatus, errorThrown) {	
				        	
							handleAJAXError( jqXHR, textStatus, errorThrown );

//								alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ", textStatus: " + textStatus );
						}
				  });
			
			}
			
			
			
			//////////
			
			function viewImage( id ) {
				var items = { };

				items.psmQValueCutoff = 0.01;
				items.peptideQValueCutoff = 0.01;
				

				
				var excludeType = new Array();
				excludeType.push(0);
				items.excludeType = excludeType;
				
				var pageURL = contextPathJSVar + "/";
				
				

				var defaultURL = $( "#viewMergedImageDefaultPageUrl_" + id ).val();
				
				if ( defaultURL === "" ) {
				          
					pageURL += "viewMergedImage.do?project_id=" + _project_id + "&searchIds=" + id + "#" + encodeURI( JSON.stringify( items ) );
					
				} else {
					
					pageURL += defaultURL;
					
				}
				
				document.location.href = pageURL;
			}
			
			
			function viewStructure( id ) {
				var items = { };

				items.psmQValueCutoff = 0.01;
				items.peptideQValueCutoff = 0.01;
				
				var excludeType = new Array();
				excludeType.push(0);
				items.excludeType = excludeType;
				
				
				var pageURL = contextPathJSVar + "/";
				
				

				var defaultURL = $( "#viewMergedStructureDefaultPageUrl_" + id ).val();
				
				if ( defaultURL === "" ) {
				          
					pageURL += "viewMergedStructure.do?project_id=" + _project_id + "&searchIds=" + id + "#" + encodeURI( JSON.stringify( items ) );
					
				} else {
					
					pageURL += defaultURL;
					
				}
				
				document.location.href = pageURL;				
			}
			
			//////////
			
			function viewMergedImage() {
				var items = { };

				items.searches = searchesToMerge;
				items.psmQValueCutoff = 0.01;
				items.peptideQValueCutoff = 0.01;


				var excludeType = new Array();
				excludeType.push(0);
				items.excludeType = excludeType;
				


				var searchIds = "";
				for ( var searchesToMergeIndex = 0; searchesToMergeIndex < searchesToMerge.length; searchesToMergeIndex++ ) {
					
					if ( searchesToMergeIndex > 0 ) {
						searchIds += "&";
					}
					searchIds += "searchIds=" + searchesToMerge[ searchesToMergeIndex ];
				}
				
				var url = contextPathJSVar + "/viewMergedImage.do?project_id=" + _project_id + "&" 
						+ searchIds + "#" + encodeURI( JSON.stringify( items ) );
				
				document.location.href = url;
			}
			
			
			function viewMergedStructure() {
				var items = { };

				items.psmQValueCutoff = 0.01;
				items.peptideQValueCutoff = 0.01;


				var searchIds = "";
				for ( var searchesToMergeIndex = 0; searchesToMergeIndex < searchesToMerge.length; searchesToMergeIndex++ ) {
					
					if ( searchesToMergeIndex > 0 ) {
						searchIds += "&";
					}
					searchIds += "searchIds=" + searchesToMerge[ searchesToMergeIndex ];
				}
				
				var excludeType = new Array();
				excludeType.push(0);
				items.excludeType = excludeType;
				
				var url = contextPathJSVar + "/viewMergedStructure.do?project_id=" + _project_id + "&" 
						+ searchIds + "#" + encodeURI( JSON.stringify( items ) );
				
				document.location.href = url;
			}
			