package org.yeastrc.xlink.www.web_utils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.searcher.SearchTaxonomySearcher;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_TaxonomyIdsForSearchId;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_TaxonomyNameStringForTaxonomyId;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.TaxonomyIdsForSearchId_Result;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.TaxonomyNameStringForTaxonomyId_Result;

/**
 * 
 *
 */
public class TaxonomiesForSearchOrSearches {
	
	private static final Logger log = LoggerFactory.getLogger(  TaxonomiesForSearchOrSearches.class );

	/**
	 * Static singleton instance
	 */
	private static TaxonomiesForSearchOrSearches _instance = new TaxonomiesForSearchOrSearches();

	/**
	 * Static get singleton instance
	 * @return
	 * @throws Exception 
	 */
	public static TaxonomiesForSearchOrSearches getInstance() throws Exception {
		return _instance; 
	}
	
	/**
	 * constructor
	 */
	private TaxonomiesForSearchOrSearches() { }
	

	/**
	 * @param searchId
	 * @return
	 * @throws Exception
	 */
	public Map<Integer, String> getTaxonomiesForSearchId( int searchId ) throws Exception {
	
		Map<Integer, String> taxonomies = new TreeMap<>();
		
		TaxonomyIdsForSearchId_Result taxonomyIdsForSearchId_Result = 
				Cached_TaxonomyIdsForSearchId.getInstance().getTaxonomyIdsForSearchId_Result( searchId );
		Set<Integer> taxonomyIds = taxonomyIdsForSearchId_Result.getTaxonomyIds();
		if ( taxonomyIds.isEmpty() ) {
			if ( log.isInfoEnabled() ) {
				String msg = "No taxonomyIds found for searchId: " + searchId;
				log.info( msg );
			}
			return taxonomies;  //  EARLY EXIT
		}
		
		for ( Integer taxonomyId : taxonomyIds ) {
			TaxonomyNameStringForTaxonomyId_Result taxonomyNameStringForTaxonomyId_Result =
					Cached_TaxonomyNameStringForTaxonomyId.getInstance().getTaxonomyNameStringForTaxonomyId_Result( taxonomyId );
			String taxonomyName = taxonomyNameStringForTaxonomyId_Result.getTaxonomyName();
			if ( taxonomyName == null ) {
				String msg = "No taxonomyName found for taxonomyId: " + taxonomyId;
				log.error( msg );
				throw new ProxlWebappDataException(msg);
			}
			taxonomies.put( taxonomyId, taxonomyName );
		}
		
		return taxonomies;
		
	}
	
	/**
	 * @param searchIds
	 * @return
	 * @throws Exception
	 */
	public Map<Integer, String> getTaxonomiesForSearchIds( Collection<Integer> searchIds ) throws Exception {
	
		Map<Integer, String> taxonomies = new TreeMap<>();
		
		for ( Integer searchId : searchIds ) {

			TaxonomyIdsForSearchId_Result taxonomyIdsForSearchId_Result = 
					Cached_TaxonomyIdsForSearchId.getInstance().getTaxonomyIdsForSearchId_Result( searchId );
			Set<Integer> taxonomyIds = taxonomyIdsForSearchId_Result.getTaxonomyIds();
			if ( taxonomyIds.isEmpty() ) {
				if ( log.isInfoEnabled() ) {
					String msg = "No taxonomyIds found for searchId: " + searchId;
					log.info( msg );
				}
				continue;  //  EARLY LOOP EXIT
			}
			
			for ( Integer taxonomyId : taxonomyIds ) {
				if ( ! taxonomies.containsKey( taxonomyId ) ) {
					TaxonomyNameStringForTaxonomyId_Result taxonomyNameStringForTaxonomyId_Result =
							Cached_TaxonomyNameStringForTaxonomyId.getInstance().getTaxonomyNameStringForTaxonomyId_Result( taxonomyId );
					String taxonomyName = taxonomyNameStringForTaxonomyId_Result.getTaxonomyName();
					if ( taxonomyName == null ) {
						String msg = "No taxonomyName found for taxonomyId: " + taxonomyId;
						log.error( msg );
						throw new ProxlWebappDataException(msg);
					}
					taxonomies.put( taxonomyId, taxonomyName );
				}
			}
		}
		
		return taxonomies;
		
	}
	


	/**
	 * @param searchId
	 * @return
	 * @throws Exception
	 */
	public Map<Integer, String> getTaxonomiesForSearchIdForAllLinkTypes( int searchId ) throws Exception {
	
		Map<Integer, String> taxonomies = new TreeMap<>();
		
		Set<Integer> taxonomyIds = SearchTaxonomySearcher.getInstance().getTaxonomiesForAllLinkTypes( searchId );
		if ( taxonomyIds.isEmpty() ) {
			if ( log.isInfoEnabled() ) {
				String msg = "No taxonomyIds found for searchId: " + searchId;
				log.info( msg );
			}
			return taxonomies;  //  EARLY EXIT
		}

		for ( Integer taxonomyId : taxonomyIds ) {
			TaxonomyNameStringForTaxonomyId_Result taxonomyNameStringForTaxonomyId_Result =
					Cached_TaxonomyNameStringForTaxonomyId.getInstance().getTaxonomyNameStringForTaxonomyId_Result( taxonomyId );
			String taxonomyName = taxonomyNameStringForTaxonomyId_Result.getTaxonomyName();
			if ( taxonomyName == null ) {
				String msg = "No taxonomyName found for taxonomyId: " + taxonomyId;
				log.error( msg );
				throw new ProxlWebappDataException(msg);
			}
			taxonomies.put( taxonomyId, taxonomyName );
		}
		
		return taxonomies;
	}
	

	/**
	 * @param searchId
	 * @return
	 * @throws Exception
	 */
	public Map<Integer, String> getTaxonomiesForSearchIdsForAllLinkTypes( Collection<Integer> searchIds ) throws Exception {
	
		Map<Integer, String> taxonomies = new TreeMap<>();

		for ( Integer searchId : searchIds ) {

			Set<Integer> taxonomyIds = SearchTaxonomySearcher.getInstance().getTaxonomiesForAllLinkTypes( searchId );
			if ( taxonomyIds.isEmpty() ) {
				if ( log.isInfoEnabled() ) {
					String msg = "No taxonomyIds found for searchId: " + searchId;
					log.info( msg );
				}
				continue;  //  EARLY LOOP EXIT
			}

			for ( Integer taxonomyId : taxonomyIds ) {
				if ( ! taxonomies.containsKey( taxonomyId ) ) {
					TaxonomyNameStringForTaxonomyId_Result taxonomyNameStringForTaxonomyId_Result =
							Cached_TaxonomyNameStringForTaxonomyId.getInstance().getTaxonomyNameStringForTaxonomyId_Result( taxonomyId );
					String taxonomyName = taxonomyNameStringForTaxonomyId_Result.getTaxonomyName();
					if ( taxonomyName == null ) {
						String msg = "No taxonomyName found for taxonomyId: " + taxonomyId;
						log.error( msg );
						throw new ProxlWebappDataException(msg);
					}
					taxonomies.put( taxonomyId, taxonomyName );
				}
			}
		}
		
		return taxonomies;
	}

}
