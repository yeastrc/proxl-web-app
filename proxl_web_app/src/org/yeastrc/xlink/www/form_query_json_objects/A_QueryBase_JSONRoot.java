package org.yeastrc.xlink.www.form_query_json_objects;

import org.yeastrc.xlink.www.annotation_display.AnnTypeIdDisplayJSONRoot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * Base class for the Root object for the query for All The Pages
 * 
 * @JsonIgnoreProperties(ignoreUnknown = true) is set so this class and all that extend from it will have this annotation
 * 
 *   @JsonIgnoreProperties(ignoreUnknown = true) :  It will ignore properties in the JSON not defined in the Java class
 *            
 */
@JsonIgnoreProperties(ignoreUnknown = true)  //  It will ignore properties in the JSON not defined in the Java class

public class A_QueryBase_JSONRoot {

	private CutoffValuesRootLevel cutoffs;
	
	private AnnTypeIdDisplayJSONRoot annTypeIdDisplay;
	

	////////
	
	//   Constuctors
	
	public A_QueryBase_JSONRoot() {}
	
	
	public A_QueryBase_JSONRoot( A_QueryBase_JSONRoot a_QueryBase_JSONRoot ) {
		
		this.cutoffs = a_QueryBase_JSONRoot.cutoffs;
		this.annTypeIdDisplay = a_QueryBase_JSONRoot.annTypeIdDisplay;
	}
		

	//////////////////////////////////////////
	
	///    getters setters
	
	public CutoffValuesRootLevel getCutoffs() {
		
		if ( cutoffs == null ) {
			
			cutoffs = new CutoffValuesRootLevel();
		}
		
		return cutoffs;
	}
	public void setCutoffs(CutoffValuesRootLevel cutoffs) {
		this.cutoffs = cutoffs;
	}


	public AnnTypeIdDisplayJSONRoot getAnnTypeIdDisplay() {
		return annTypeIdDisplay;
	}


	public void setAnnTypeIdDisplay(AnnTypeIdDisplayJSONRoot annTypeIdDisplay) {
		this.annTypeIdDisplay = annTypeIdDisplay;
	}
	
}
