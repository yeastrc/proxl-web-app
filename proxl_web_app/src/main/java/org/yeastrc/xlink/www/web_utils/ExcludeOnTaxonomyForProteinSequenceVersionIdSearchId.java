package org.yeastrc.xlink.www.web_utils;

import java.util.Collection;
import java.util.Set;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.objects.ProteinSequenceVersionObject;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_TaxonomyIdsFor_ProtSeqVersionId_SearchId;
import org.yeastrc.xlink.www.searcher_via_cached_data.request_objects_for_searchers_for_cached_data.TaxonomyIdsForProtSeqIdSearchId_Request;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.TaxonomyIdsForProtSeqIdSearchId_Result;

/**
 * The rules for if the passed in protein sequence version id and search id should be excluded
 * for the passed in taxonomy id
 *
 */
public class ExcludeOnTaxonomyForProteinSequenceVersionIdSearchId {

	private static final Logger log = Logger.getLogger(ExcludeOnTaxonomyForProteinSequenceVersionIdSearchId.class);
	//  private constructor
	private ExcludeOnTaxonomyForProteinSequenceVersionIdSearchId() { }
	/**
	 * @return newly created instance
	 */
	public static ExcludeOnTaxonomyForProteinSequenceVersionIdSearchId getInstance() { 
		return new ExcludeOnTaxonomyForProteinSequenceVersionIdSearchId(); 
	}
	
	/**
	 * The rules for if the passed in protein sequence version id and search id should be excluded
	 * for the passed in exclude taxonomy id Set.
	 * 
	 * return true if should be excluded based on taxonomy id
	 * 
	 * @param excludeTaxonomy_Ids
	 * @param proteinSequenceVersionObject
	 * @param searchId
	 * @return true if should be excluded based on taxonomy id
	 * @throws Exception 
	 * 
	 */
	public boolean excludeOnTaxonomyForProteinSequenceVersionIdSearchId(
			Collection<Integer> excludeTaxonomy_Ids,
			ProteinSequenceVersionObject proteinSequenceVersionObject,
			int searchId ) throws Exception {
		
		//  Return true if all the taxonomy ids for the search id and protein sequence id are to be excluded
		try {
			//  Get all taxonomy ids for protein sequence id and search id
			TaxonomyIdsForProtSeqIdSearchId_Request taxonomyIdsForProtSeqIdSearchId_Request =
					new TaxonomyIdsForProtSeqIdSearchId_Request();
			taxonomyIdsForProtSeqIdSearchId_Request.setSearchId( searchId );
			taxonomyIdsForProtSeqIdSearchId_Request.setProteinSequenceVersionId( proteinSequenceVersionObject.getProteinSequenceVersionId() );
			TaxonomyIdsForProtSeqIdSearchId_Result taxonomyIdsForProtSeqIdSearchId_Result =
					Cached_TaxonomyIdsFor_ProtSeqVersionId_SearchId.getInstance()
					.getTaxonomyIdsForProtSeqIdSearchId_Result( taxonomyIdsForProtSeqIdSearchId_Request );
			Set<Integer> taxonomyIds = taxonomyIdsForProtSeqIdSearchId_Result.getTaxonomyIds();
			boolean excludeOnTaxonomyId = true;
			for ( Integer taxonomyId : taxonomyIds ) {
				if ( ! excludeTaxonomy_Ids.contains( taxonomyId ) ) {
					excludeOnTaxonomyId = false;
				}
			}
			return excludeOnTaxonomyId;
		} catch ( Exception e ) {
			String msg = "Error processing in excludeOnTaxonomyForProteinSequenceVersionIdSearchId(...)";
			log.error( msg );
			throw e;
		}
	}
}
