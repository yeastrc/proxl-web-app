
//   connectToNoOperationService.js


//  This connects to the noOperation web service to search the server side JSON serialization


//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";


///////////////////////////////////////////



$(document).ready(function()  { 
	

				var _URL = contextPathJSVar + "/services/noOperation";
				
				$.ajax({
				        type: "GET",
				        url: _URL,
				        contentType: "application/json; charset=utf-8",
				        dataType: "json",
				        cache: false,
				        success: function(data)	{
				        	
				        	var z = 0;
				        	//  Do nothing with result
						},
				        failure: function(errMsg) {
				        	
				        	var z = 0;
				        	;
//				            alert(errMsg);
				        },
						error: function(jqXHR, textStatus, errorThrown) {	
						
				        	var z = 0;
							;
//								alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ", textStatus: " + textStatus );
						}
				  });
				 
					
					
});
				 