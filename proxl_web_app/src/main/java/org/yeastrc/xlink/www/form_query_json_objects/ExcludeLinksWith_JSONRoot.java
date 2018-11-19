package org.yeastrc.xlink.www.form_query_json_objects;

import org.yeastrc.xlink.www.constants.MinimumPSMsConstants;

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
	private boolean removeNonUniquePSMs;
	
	/**
	 * Exclude if # PSMs is less than minPSMs
	 */
	private int minPSMs = MinimumPSMsConstants.MINIMUM_PSMS_DEFAULT;
	
	/**
	 * Exclude if # PSMs is less than minPSMs
	 * @return
	 */
	public int getMinPSMs() {
		return minPSMs;
	}
	/**
	 * Exclude if # PSMs is less than minPSMs
	 * @param minPSMs
	 */
	public void setMinPSMs(int minPSMs) {
		this.minPSMs = minPSMs;
	}
	
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
	public boolean isRemoveNonUniquePSMs() {
		return removeNonUniquePSMs;
	}
	public void setRemoveNonUniquePSMs(boolean removeNonUniquePSMs) {
		this.removeNonUniquePSMs = removeNonUniquePSMs;
	}

}
