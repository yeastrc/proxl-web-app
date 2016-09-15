
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
	
	//  Process text inputs
	
	var $config_text_inputs_jq = $(".config_text_inputs_jq");
	
	$config_text_inputs_jq.each( function( index, element ) {
		
		var $configTextInput = $( this );
		
		var configKeyForInput = $configTextInput.attr("data-config-key");
		
//		var foundConfigValueForField = false;

		for ( var configListIndex = 0; configListIndex < configList.length; configListIndex++ ) {

			var configListItem = configList[ configListIndex ];

			if ( configListItem.configKey === configKeyForInput ) {

				$configTextInput.val( configListItem.configValue );
				
//				foundConfigValueForField = true;
			}
		}
	} );
	
	//  Process checkbox inputs

	var $config_checkbox_inputs_jq = $(".config_checkbox_inputs_jq");
	
	$config_checkbox_inputs_jq.each( function( index, element ) {
		
		var $configCheckboxInput = $( this );
		
		var configKeyForInput = $configCheckboxInput.attr("data-config-key");
		
//		var foundConfigValueForField = false;

		for ( var configListIndex = 0; configListIndex < configList.length; configListIndex++ ) {

			var configListItem = configList[ configListIndex ];

			if ( configListItem.configKey === configKeyForInput ) {

				var dataValueChecked = $configCheckboxInput.attr("data-value-checked");
//				var dataValueNOTChecked = $configCheckboxInput.attr("data-value-not-checked");
				
				if ( configListItem.configValue === dataValueChecked ) {
					
					$configCheckboxInput.prop( "checked", true );
					
				} else {
					
					$configCheckboxInput.prop( "checked", false );
				}
				
//				foundConfigValueForField = true;
			}
		}
	} );
};


function saveListConfiguration() {


	var configList = [];
	
	
	var input_footer_center_of_page_html_Val = null;
	

	//  Process text inputs
	
	var $config_text_inputs_jq = $(".config_text_inputs_jq");
	
	$config_text_inputs_jq.each( function( index, element ) {
		
		var $configTextInput = $( this );
		
		var configKeyForInput = $configTextInput.attr("data-config-key");

		var valueInInput = $configTextInput.val( );

		var configListItem = { 
				configKey: configKeyForInput,
				configValue : valueInInput };
		configList.push( configListItem );
		
		//  save special data for data-FOOTER_CENTER_OF_PAGE_HTML
		
		var data_FOOTER_CENTER_OF_PAGE_HTML_val = $configTextInput.attr("data-FOOTER_CENTER_OF_PAGE_HTML");
		
		if ( data_FOOTER_CENTER_OF_PAGE_HTML_val === "true" ) {
			
			input_footer_center_of_page_html_Val = valueInInput;
		}
	} );
	
	//  Process checkbox inputs

	var $config_checkbox_inputs_jq = $(".config_checkbox_inputs_jq");
	
	$config_checkbox_inputs_jq.each( function( index, element ) {
		
		var $configCheckboxInput = $( this );
		
		var configKeyForInput = $configCheckboxInput.attr("data-config-key");
		
		var dataValueChecked = $configCheckboxInput.attr("data-value-checked");
		var dataValueNOTChecked = $configCheckboxInput.attr("data-value-not-checked");

		var valueToSaveToConfig = dataValueNOTChecked;
		
		if ( $configCheckboxInput.prop( "checked" ) ) {
			
			valueToSaveToConfig = dataValueChecked;
		}

		var configListItem = { 
				configKey: configKeyForInput,
				configValue : valueToSaveToConfig };
		configList.push( configListItem );

	} );


	var requestObj = { configList : configList };
	
	var requestData = JSON.stringify( requestObj );

	var _URL = contextPathJSVar + "/services/config/save";

//	var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : requestData,
	    contentType: "application/json; charset=utf-8",
		dataType : "json",
		success : function(data) {

			saveListConfigurationResponse( { 
				requestData : requestData, 
				responseData : data, 
				input_footer_center_of_page_html_Val : input_footer_center_of_page_html_Val
			} );
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
	
}


function saveListConfigurationResponse( params ) {

//	var requestData = params.requestData;
//	var responseData = params.responseData;
	var input_footer_center_of_page_html_Val = params.input_footer_center_of_page_html_Val;
	
	
	var $element = $("#success_message_values_updated");
	
	showErrorMsg( $element );  //  Used for success messages as well
	
	if ( input_footer_center_of_page_html_Val !== null ) {
	
		//  Update footer text on current page

		var $footer_center_container = $("#footer_center_container");

		$footer_center_container.html( input_footer_center_of_page_html_Val );
	}
}




function initPage() {
	
//	<input type="button" value="Save" id="save_button">
//	<input type="button" value="Reset" id="reset_button">
	
	$("#save_button").click(function(eventObject) {

//		var clickThis = this;
		
		saveListConfiguration();

		return false;
	});

	$("#reset_button").click(function(eventObject) {

//		var clickThis = this;

		getListConfiguration();
		
		return false;
	});

	getListConfiguration();

};


///////////////

$(document).ready(function() {

	initPage();

});
