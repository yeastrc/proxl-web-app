package org.yeastrc.xlink.www.form_query_json_objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *  Root object for passing Exclude Links to all webservices.
 *  
 *  PSM and Reported Peptide Webservices only use some of the values
 * 
 * @JsonIgnoreProperties(ignoreUnknown = true) is set so this class and all that extend from it will have this annotation
 * 
 *   @JsonIgnoreProperties(ignoreUnknown = true) :  It will ignore properties in the JSON not defined in the Java class
 *            
 */
@JsonIgnoreProperties(ignoreUnknown = true)  //  It will ignore properties in the JSON not defined in the Java class
public class ExcludeLinksWith_JSONRoot {

	private boolean filterNonUniquePeptides;
	private boolean filterOnlyOnePeptide;
	private boolean filterOnlyOnePSM;
	private boolean removeNonUniquePSMs;
	
	public boolean isFilterNonUniquePeptides() {
		return filterNonUniquePeptides;
	}
	public void setFilterNonUniquePeptides(boolean filterNonUniquePeptides) {
		this.filterNonUniquePeptides = filterNonUniquePeptides;
	}
	public boolean isFilterOnlyOnePeptide() {
		return filterOnlyOnePeptide;
	}
	public void setFilterOnlyOnePeptide(boolean filterOnlyOnePeptide) {
		this.filterOnlyOnePeptide = filterOnlyOnePeptide;
	}
	public boolean isFilterOnlyOnePSM() {
		return filterOnlyOnePSM;
	}
	public void setFilterOnlyOnePSM(boolean filterOnlyOnePSM) {
		this.filterOnlyOnePSM = filterOnlyOnePSM;
	}
	public boolean isRemoveNonUniquePSMs() {
		return removeNonUniquePSMs;
	}
	public void setRemoveNonUniquePSMs(boolean removeNonUniquePSMs) {
		this.removeNonUniquePSMs = removeNonUniquePSMs;
	}
}
