
//   sharePageURLShortener.js

//   JS code for calling webservice with current URL and getting shortened URL which is displayed to user.

//   Used on sharePageURLShortenerButtonFragment.jsp

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";


//  Instance of this class:  sharePageURLShortener

//  Constructor

var SharePageURLShortener = function() {

	//  Called from 'Share Page' button

	this.sharePage = function( params ) {
		try {
			var objectThis = this;

			if ( ! params ) {
				console.log( "params cannot be empty" );
				throw Error( "sharePage(params): params cannot be empty" ); 
			}

			var clickedThis = params.clickedThis;

			if ( ! clickedThis ) {
				console.log( "clickedThis cannot be empty" );
				throw Error( "sharePage(params): clickedThis cannot be empty" ); 
			}

			var $clickedThis = $( clickedThis );

			var pageUrl = window.location.href;

			var requestObj = { pageUrl: pageUrl };
			
			//  Get search ids from attr on button
			
			var project_search_id_comma_delim_list = $clickedThis.attr("data-project_search_id_comma_delim_list");
			if ( project_search_id_comma_delim_list !== undefined && project_search_id_comma_delim_list !== "" ) {

				var projectSearchIdStringList = project_search_id_comma_delim_list.split(",");
				var projectSearchIdIntList = [];
				for ( var index = 0; index < projectSearchIdStringList.length; index++ ) {
					var projectSearchIdString = projectSearchIdStringList[ index ];
					var projectSearchIdInt = parseInt( projectSearchIdString, 10 );
					projectSearchIdIntList.push( projectSearchIdInt );
				}
				
				requestObj.projectSearchIdList = projectSearchIdIntList;
			}
		
			var requestData = JSON.stringify( requestObj );
			
			var _URL = contextPathJSVar + "/services/sharePageShortenURL/createAndSaveShortenedURL";

			$.ajax({
				type: "POST",
				url: _URL,
				data : requestData,
				contentType: "application/json; charset=utf-8",
				dataType: "json",
				success: function( responseData )	{
					try {
						var sharePageProcessAjaxResponseParams = {
								responseData : responseData,
								pageUrl : pageUrl,
								clickedThis : clickedThis
						};

						objectThis.sharePageProcessAjaxResponse( sharePageProcessAjaxResponseParams );

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
				}
			});

		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};

	this.sharePageProcessAjaxResponse = function( params ) {

		var objectThis = this;

		var clickedThis = params.clickedThis;
		var responseData = params.responseData;

		var $clickedThis = $( clickedThis );

		var shortenedURL = responseData.shortenedURL;
		
		//  shortenedURL is what will be displayed to the user
		
		$(".shortened_url_display_overlay_show_hide_parts_jq").show();
		
		var $shortened_url_display_overlay_url_display_div =  $("#shortened_url_display_overlay_url_display_div");
		
		$shortened_url_display_overlay_url_display_div.text( shortenedURL );
		
		this.SelectText( $shortened_url_display_overlay_url_display_div[ 0 ]);
	};
	
	this.SelectText = function( htmlElement) {
	    var doc = document
//	        , htmlElement = doc.getElementById(element)
	        , range, selection
	    ;    
	    if (doc.body.createTextRange) {
	        range = document.body.createTextRange();
	        range.moveToElementText(htmlElement);
	        range.select();
	    } else if (window.getSelection) {
	        selection = window.getSelection();        
	        range = document.createRange();
	        range.selectNodeContents(htmlElement);
	        selection.removeAllRanges();
	        selection.addRange(range);
	    }
	};
	
	
}

var sharePageURLShortener = new SharePageURLShortener();