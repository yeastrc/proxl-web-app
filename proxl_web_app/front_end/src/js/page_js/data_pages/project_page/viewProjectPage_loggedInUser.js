/**
 * viewProjectPage_loggedInUser.js
 * 
 * Javascript for projectView.jsp page  
 * 
 * Parts from viewProjectPage.js for logged in user
 * 
 * 
 */


//   Called by "onclick" on HTML element
window.expandAll = function() {
	try {
		var $folder_contents_block_jq = $(".folder_contents_block_jq");
		$folder_contents_block_jq.show();
		var $folder_hide_contents_link_jq = $(".folder_hide_contents_link_jq");
		$folder_hide_contents_link_jq.show();
		var $folder_show_contents_link_jq = $(".folder_show_contents_link_jq");
		$folder_show_contents_link_jq.hide();
		
		$( "table.search-details" ).show();
		$( "a.expand-link" ).html( '<img src="images/icon-collapse-small.png">' );
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
}

//   Called by "onclick" on HTML element
window.collapseAll = function() {
	try {
		var $folder_contents_block_jq = $(".folder_contents_block_jq");
		$folder_contents_block_jq.hide();
		var $folder_hide_contents_link_jq = $(".folder_hide_contents_link_jq");
		$folder_hide_contents_link_jq.hide();
		var $folder_show_contents_link_jq = $(".folder_show_contents_link_jq");
		$folder_show_contents_link_jq.show();

		$( "table.search-details" ).hide();
		$( "a.expand-link" ).html( '<img src="images/icon-expand-small.png">' );
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
}

var viewProjectPage_loggedInUser = null;

export { viewProjectPage_loggedInUser } //  Just something to be able to import
