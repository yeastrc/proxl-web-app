
//   reportWebErrorToServer.js


///     Send errors detected in JS to server


// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";



//  Constructor

var ReportWebErrorToServer = function() {
	
	
	
	this.reportErrorObjectToServer = function( params ) {

//		var objectThis = this;
		
		var errorException = params.errorException;
		
		try {

			var userAgent = "unknown";
			
			if ( window.navigator ) {
				
				if ( window.navigator.userAgent ) {
					
					userAgent = window.navigator.userAgent
				}
			}
			
			var browserURL = window.location.href;

			var requestObj = { fdajklweRWOIUOPOP : true,
					errorMsg : errorException.message,
					stackString : errorException.stack,
					userAgent : userAgent,
					browserURL : browserURL };
			
			var requestData = JSON.stringify( requestObj );

			var _URL = contextPathJSVar + "/services/log_error/logBrowserJavascriptError";

//			var request =
			$.ajax({
				type : "POST",
				url : _URL,
				data : requestData,
			    contentType: "application/json; charset=utf-8",
				dataType : "json",
				success : function(data) {

					var z = 0;
				},
				failure: function(errMsg) {
					
					console.log("AJAX failure in reportWebErrorToServer.reportErrorObjectToServer(), errMsg: " + errMsg );
				},
				error : function(jqXHR, textStatus, errorThrown) {

					console.log("AJAX error in reportWebErrorToServer.reportErrorObjectToServer(), textStatus: " + textStatus );
				}
			});
			
			

		} catch( e ) {
			
			console.log("Exception in reportWebErrorToServer.reportErrorObjectToServer()");
		}
		
		
	};
	
	
};



//  Instance of class

var reportWebErrorToServer = new ReportWebErrorToServer();
