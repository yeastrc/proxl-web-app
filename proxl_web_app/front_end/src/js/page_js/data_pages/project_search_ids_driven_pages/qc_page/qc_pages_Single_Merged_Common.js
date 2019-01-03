/*
 * qc_pages_Single_Merged_Common.js
 * 
 * Common code for QC pages for Single Search and Merged Searches
 * 
 * viewQC.jsp and viewQCMerged.jsp
 * 
 * page variable qc_pages_Single_Merged_Common
 * 
 */
var QC_Pages_Single_Merged_Common = function() {
	

	
	/*
	 * Takes a filename, mimetype, and string content and initiates a file download
	 * of the content from the current page, without leaving the page. 
	 * 
	 * It is assumed jquery is loaded.
	 * 
	 */
	this.submitDownloadForParams = function( params ) {
		var downloadStrutsAction = params.downloadStrutsAction;
		var project_search_ids = params.project_search_ids;
		var hash_json_Contents = params.hash_json_Contents;

		var urlQueryParamsArray = [];

		project_search_ids.forEach(function( projectSearchId, i, array) {
			urlQueryParamsArray.push( "projectSearchId=" + projectSearchId  );
		}, this );

		var hash_json_field_Contents_JSONString = JSON.stringify( hash_json_Contents );
		var hash_json_field_Contents_JSONString_encodeURIComponent = encodeURIComponent(hash_json_field_Contents_JSONString  );
		urlQueryParamsArray.push( "queryJSON=" + hash_json_field_Contents_JSONString_encodeURIComponent );

		var urlQueryParams = urlQueryParamsArray.join( "&" );

		var downloadURL = downloadStrutsAction + "?" + urlQueryParams;

		this.submitDownloadURL( { downloadURL : downloadURL } );
	};
	
	/*
	 * Takes a filename, mimetype, and string content and initiates a file download
	 * of the content from the current page, without leaving the page. 
	 * 
	 * It is assumed jquery is loaded.
	 * 
	 */
	this.submitDownloadURL = function( params ) {
		var downloadURL = params.downloadURL;

		var form = document.createElement( "form" );

		$( form ).hide();

		form.setAttribute( "method", "post" );
		form.setAttribute( "action", downloadURL );
		form.setAttribute( "target", "_blank" ); // Open in new tab allows for better handling of the download getting an error or the user is logged out.

		document.body.appendChild(form);    // Not entirely sure if this is necessary			

		form.submit();

		document.body.removeChild( form );
	};
	
//	
//	this.createSubmitDownloadDataForm = function( params ) {
//		
//		var projectSearchIds = params.projectSearchIds;
//		
//		var downloadStrutsActionDotDo = params.downloadStrutsActionDotDo; //  No leading '/'
//
//		var form = document.createElement( "form" );
//
//		$( form ).hide();
//
//		form.setAttribute( "method", "post" );
//		form.setAttribute( "action", contextPathJSVar + "/" + downloadStrutsActionDotDo );
//		// form.setAttribute( "target", "_blank" );
//
//		projectSearchIds.forEach(function( element_ProjectSearchId, index, array ) {
//			var projectSearchId_Field = document.createElement( "textarea" );
//			projectSearchId_Field.setAttribute("name", "projectSearchId");
//			$( projectSearchId_Field ).text( element_ProjectSearchId );
//
//			form.appendChild( projectSearchId_Field );
//
//		}, this );
//
//		var contentField = document.createElement( "textarea" );
//		contentField.setAttribute("name", "content");
//
//		$( contentField ).text( content );
//
//
//		document.body.appendChild(form);    // Not entirely sure if this is necessary			
//
////		form.submit();
//
////		document.body.removeChild( form );
//
//		
//		
//		
//	};
//	
	
};

//  instance of object
var qc_pages_Single_Merged_Common = new QC_Pages_Single_Merged_Common();

export { qc_pages_Single_Merged_Common }
