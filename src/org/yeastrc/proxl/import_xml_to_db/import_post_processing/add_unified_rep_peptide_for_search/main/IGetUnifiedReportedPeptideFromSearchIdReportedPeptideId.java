package org.yeastrc.proxl.import_xml_to_db.import_post_processing.add_unified_rep_peptide_for_search.main;

import org.yeastrc.xlink.dto.UnifiedReportedPeptideLookupDTO;

/**
 * This is primarily provided to assist with writing the conversion to generic
 *
 */
public interface IGetUnifiedReportedPeptideFromSearchIdReportedPeptideId {

	/**
	 * 
	 * 
	 * @param searchId
	 * @param reportedPeptideId
	 * @return null if no record found for search id and reported peptide id
	 */
	public UnifiedReportedPeptideLookupDTO getUnifiedReportedPeptideFromSearchIdReportedPeptideId(
			
			int searchId, int reportedPeptideId
			) throws Exception;
	
}