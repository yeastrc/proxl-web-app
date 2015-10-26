
//    toggleVisibility.js


//  toggleVisibility.js

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";

// /////////////////////////////////////////


function toggleVisibility( elementParam ) {


	if (typeof elementParam == 'string' || elementParam instanceof String) {

		toggleVisibilityElementIdPassed( elementParam );
	
	} else {
		
		toggleVisibilityHTMLElement( elementParam );
	}

	return false;  // does not stop bubbling of click event
} 


///////////////////////////////////////////


function toggleVisibilityElementIdPassed( elementId ) {

	var $jitem = $( '#' + elementId ).next();
	
	if( $jitem.is(":visible" ) ) {
		$jitem.hide(); 
	} else { 
		$jitem.show(); 
	}
	
	return false;  // does not stop bubbling of click event

} 



///////////////////////////////////////////

//	Called by putting this in the HTML:  onclick="toggleVisibilityHTMLElement(this)"

//	It does NOT work to have href="javascript:toggleVisibilityHTMLElement(this)"


//  using  onclick="toggleVisibility(this)"
//    toggleVisibility(...) will call  toggleVisibilityHTMLElement(...)

function toggleVisibilityHTMLElement( htmlElement ) {

	var $htmlElement = $(htmlElement);

	var $itemToToggle;
	var toggle_visibility_associated_element_id = $htmlElement.attr("toggle_visibility_associated_element_id");
	if( toggle_visibility_associated_element_id ) {
		$itemToToggle = $('#' + toggle_visibility_associated_element_id).next();
	} else {
		$itemToToggle = $htmlElement.next();
	}
	
	
	if( $itemToToggle.is(":visible" ) ) {

		$itemToToggle.hide(); 
		
		$htmlElement.find(".toggle_visibility_expansion_span_jq").show();
		$htmlElement.find(".toggle_visibility_contraction_span_jq").hide();
	} else { 
		$itemToToggle.show();
		
		$htmlElement.find(".toggle_visibility_expansion_span_jq").hide();
		$htmlElement.find(".toggle_visibility_contraction_span_jq").show();

	}
	
	return false;  // does not stop bubbling of click event

} 


////////////////////////////



function addToggleVisibilityClickHandler( $parentElement ) {

	var $toggle_visibility_link_jq = $parentElement.find(".toggle_visibility_link_jq");
	
	$toggle_visibility_link_jq.click(function(eventObject) {

		var $this = $(this);

		var toggle_visibility_associated_element_id = $this.attr("toggle_visibility_associated_element_id");
		
		var $itemToToggle = $('#' + toggle_visibility_associated_element_id).next();
		
		
		if( $itemToToggle.is(":visible" ) ) {

			$itemToToggle.hide(); 
			
			$this.find(".toggle_visibility_expansion_span_jq").show();
			$this.find(".toggle_visibility_contraction_span_jq").hide();
		} else { 
			$itemToToggle.show();
			
			$this.find(".toggle_visibility_expansion_span_jq").hide();
			$this.find(".toggle_visibility_contraction_span_jq").show();

		}
//		<span class="toggle_visibility_expansion_span_jq" >[+]</span>
//		<span class="toggle_visibility_contraction_span_jq" >[-]</span></a></td>

		return false;
		
	});
}



////////////////////////////



function initToggleVisibility( ) {

	var $parentElement = $(document);
	
	addToggleVisibilityClickHandler( $parentElement );
}	

///////////////

$(document).ready(function() {

	initToggleVisibility();

});
