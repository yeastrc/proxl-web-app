package org.yeastrc.xlink.www.actions;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.form_query_json_objects.A_QueryBase_JSONRoot;
import org.yeastrc.xlink.www.objects.PsmWebDisplayWebServiceResult;

/**
 * 
 *
 */
public class DownloadPSMs_Common {

	private static final Logger log = LoggerFactory.getLogger(  DownloadPSMs_Common.class );
	private DownloadPSMs_Common() { }
	public static DownloadPSMs_Common getInstance() { 
		return new DownloadPSMs_Common(); 
	}

	/**
	 * @param excludeLinksWith_JSONRoot
	 * @param search
	 * @param psmWebDisplayList
	 * @return updated list with PSMs possibly filtered out
	 * @throws Exception
	 */
	public List<PsmWebDisplayWebServiceResult> filterPSMs(
			A_QueryBase_JSONRoot a_QueryBase_JSONRoot,
			SearchDTO search, 
			List<PsmWebDisplayWebServiceResult> psmWebDisplayList ) throws Exception {

		//  Filter PSMs
		if ( search.isHasScanData() ) {
			//  Has scan data so can use this filter
			if ( a_QueryBase_JSONRoot.isRemoveNonUniquePSMs() ) {
				List<PsmWebDisplayWebServiceResult> psmWebDisplayList_Filtered = new ArrayList<>( psmWebDisplayList.size() );
				for ( PsmWebDisplayWebServiceResult psmWebDisplayItem : psmWebDisplayList ) {
					if ( a_QueryBase_JSONRoot.isRemoveNonUniquePSMs() ) {
						if ( psmWebDisplayItem.getPsmCountForOtherAssocScanId() != null
								&& psmWebDisplayItem.getPsmCountForOtherAssocScanId() > 0 ) {
							//  Drop this item from output list since not unique PSM
							continue; // EARLY CONTINUE
						}
					}
					psmWebDisplayList_Filtered.add( psmWebDisplayItem );
				}
				psmWebDisplayList = psmWebDisplayList_Filtered;
			}
		}
		return psmWebDisplayList;
	}
}
