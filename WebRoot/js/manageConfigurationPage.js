
//   manageConfigurationPage.js



//  Javascript for the project admin section of the page manageUsersPage.jsp

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";




/////////////////

var getListConfiguration = function() {

	var requestData = {

	};

	var _URL = contextPathJSVar + "/services/config/list";

//	var request =
	$.ajax({
		type : "GET",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(data) {

			getListConfigurationResponse(requestData, data);
		},
		failure: function(errMsg) {
			handleAJAXFailure( errMsg );
		},
		error : function(jqXHR, textStatus, errorThrown) {

			handleAJAXError(jqXHR, textStatus, errorThrown);

//			alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
//			textStatus: " + textStatus );
		}
	});

};


///////

var getListConfigurationResponse = function(requestData, responseData) {

	var configList = responseData.configList;
	
	var $config_key_footer_center_of_page_html = $( "#config_key_footer_center_of_page_html" );
	var $config_key_email_from_address = $( "#config_key_email_from_address" );
	var $config_key_email_smtp_server_url = $( "#config_key_email_smtp_server_url" );

	var config_key_footer_center_of_page_html_Val = $config_key_footer_center_of_page_html.val();
	var config_key_email_from_address_Val = $config_key_email_from_address.val();
	var config_key_email_smtp_server_url_Val = $config_key_email_smtp_server_url.val();
	
	
	var $input_footer_center_of_page_html = $( "#input_footer_center_of_page_html" );
	var $input_email_from_address = $( "#input_email_from_address" );
	var $input_email_smtp_server_url = $( "#input_email_smtp_server_url" );

	for ( var configListIndex = 0; configListIndex < configList.length; configListIndex++ ) {
	
		var configListItem = configList[ configListIndex ];
		
		if ( configListItem.configKey === config_key_footer_center_of_page_html_Val ) {
			
			$input_footer_center_of_page_html.val( configListItem.configValue );
			
		} else if ( configListItem.configKey === config_key_email_from_address_Val ) {
			
			$input_email_from_address.val( configListItem.configValue );
			
		} else if ( configListItem.configKey === config_key_email_smtp_server_url_Val ) {
			
			$input_email_smtp_server_url.val( configListItem.configValue );
		}
	}

};


function saveListConfiguration() {
	
	
	
	
}





function initPage() {
	
//	<input type="button" value="Save" id="save_button">
//	<input type="button" value="Reset" id="reset_button">
	
	$("#save_button").click(function(eventObject) {

		var clickThis = this;
		
		saveListConfiguration();

		return false;
	});

	$("#reset_button").click(function(eventObject) {

		var clickThis = this;

		getListConfiguration();
		
		return false;
	});

	getListConfiguration();

};


///////////////

$(document).ready(function() {

	initPage();

});
