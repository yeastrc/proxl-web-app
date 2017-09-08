package org.yeastrc.xlink.www.web_utils;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.yeastrc.xlink.www.dto.SearchDTO;

/**
 * Populate page request items for excludeLinksWith_Remove_NonUniquePSMs_Checkbox_Fragment.jsp
 *
 */
public class ExcludeLinksWith_Remove_NonUniquePSMs_Checkbox_PopRequestItems {

	private static final ExcludeLinksWith_Remove_NonUniquePSMs_Checkbox_PopRequestItems instance = new ExcludeLinksWith_Remove_NonUniquePSMs_Checkbox_PopRequestItems();
	private ExcludeLinksWith_Remove_NonUniquePSMs_Checkbox_PopRequestItems() { }
	public static ExcludeLinksWith_Remove_NonUniquePSMs_Checkbox_PopRequestItems getInstance() { return instance; }

	
	/**
	 * @param search
	 * @param request
	 * @throws Exception
	 */
	public void excludeLinksWith_Remove_NonUniquePSMs_Checkbox_PopRequestItems( SearchDTO search, HttpServletRequest request ) throws Exception {
		
		if ( search.isHasScanData() ) {
			request.setAttribute( "excludeLinksWith_Remove_NonUniquePSMs_Checkbox_searchHasScanData", true );
		}
	}

	/**
	 * @param searches
	 * @param request
	 * @throws Exception
	 */
	public void excludeLinksWith_Remove_NonUniquePSMs_Checkbox_PopRequestItems( Collection<SearchDTO> searches, HttpServletRequest request ) throws Exception {
		boolean anySearchHasScanData = false;
		for ( SearchDTO search : searches ) {
			if ( search.isHasScanData() ) {
				anySearchHasScanData = true;
				break;
			}
		}
		if ( anySearchHasScanData ) {
			request.setAttribute( "excludeLinksWith_Remove_NonUniquePSMs_Checkbox_searchHasScanData", true );
		}
	}
}
