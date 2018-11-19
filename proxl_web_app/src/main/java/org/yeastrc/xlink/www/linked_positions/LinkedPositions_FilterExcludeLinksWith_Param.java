package org.yeastrc.xlink.www.linked_positions;

import org.yeastrc.xlink.www.form_query_json_objects.A_QueryBase_JSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.ExcludeLinksWith_JSONRoot;

/**
 * For calls to Linked Positions classes.
 * 
 * The Filter Exclude Links with Parameters from User Input
 *
 */
public class LinkedPositions_FilterExcludeLinksWith_Param {

	private boolean removeNonUniquePSMs;
	
	//  Constructors
	public LinkedPositions_FilterExcludeLinksWith_Param() {}
	public LinkedPositions_FilterExcludeLinksWith_Param( A_QueryBase_JSONRoot a_QueryBase_JSONRoot ) {
		this.removeNonUniquePSMs = a_QueryBase_JSONRoot.isRemoveNonUniquePSMs();
	}
	public LinkedPositions_FilterExcludeLinksWith_Param( ExcludeLinksWith_JSONRoot excludeLinksWith_JSONRoot ) {
		this.removeNonUniquePSMs = excludeLinksWith_JSONRoot.isRemoveNonUniquePSMs();
	}
	
	
	public boolean isRemoveNonUniquePSMs() {
		return removeNonUniquePSMs;
	}
	public void setRemoveNonUniquePSMs(boolean removeNonUniquePSMs) {
		this.removeNonUniquePSMs = removeNonUniquePSMs;
	}

}
