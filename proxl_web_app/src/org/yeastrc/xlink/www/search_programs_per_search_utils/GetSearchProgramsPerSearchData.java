package org.yeastrc.xlink.www.search_programs_per_search_utils;

import java.util.concurrent.TimeUnit;
import org.yeastrc.xlink.dao.SearchProgramsPerSearchDAO;
import org.yeastrc.xlink.dto.SearchProgramsPerSearchDTO;
import org.yeastrc.xlink.www.cached_data_mgmt.CacheCurrentSizeMaxSizeResult;
import org.yeastrc.xlink.www.cached_data_mgmt.CachedDataCentralRegistry;
import org.yeastrc.xlink.www.cached_data_mgmt.CachedDataCommonIF;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Central place to get SearchProgramsPerSearchDTO data
 * 
 * Cached
 * 
 * Singleton instance
 */
public class GetSearchProgramsPerSearchData implements CachedDataCommonIF {

	private static final int CACHE_MAX_SIZE = 500;
	private static final int CACHE_TIMEOUT = 20; // in days
	
	/**
	 * Static get singleton instance
	 * @return
	 */
	public static GetSearchProgramsPerSearchData getInstance() {
		return _instance; 
	}
	
	/* 
	 * Clear all entries from the cache
	 */
	@Override
	public void clearCacheData() throws Exception {
		searchProgramsPerSearchDataCache.invalidateAll();
	}
	
	@Override
	public CacheCurrentSizeMaxSizeResult getCurrentCacheSizeAndMax() throws Exception {
		CacheCurrentSizeMaxSizeResult result = new CacheCurrentSizeMaxSizeResult();
		result.setCurrentSize( searchProgramsPerSearchDataCache.size() );
		result.setMaxSize( CACHE_MAX_SIZE );
		return result;
	}
	
	/**
	 * @param searchProgramsPerSearchId
	 * @return
	 * @throws Exception
	 */
	public SearchProgramsPerSearchDTO getSearchProgramsPerSearchDTO( Integer searchProgramsPerSearchId ) throws Exception {
		LocalCacheKey localCacheKey = new LocalCacheKey();
		localCacheKey.searchProgramsPerSearchId = searchProgramsPerSearchId;
		LocalCacheValue localCacheValue = searchProgramsPerSearchDataCache.get( localCacheKey );
		if ( localCacheValue == null ) {
			throw new Exception("Should be getting annotation data via cache internal load");
		}
		SearchProgramsPerSearchDTO searchProgramsPerSearchDTO = localCacheValue.searchProgramsPerSearchDTO;
		return searchProgramsPerSearchDTO;
	}
	
	/**
	 * Static singleton instance
	 */
	private static final GetSearchProgramsPerSearchData _instance = new GetSearchProgramsPerSearchData();
	
	/**
	 * constructor
	 */
	private GetSearchProgramsPerSearchData() {
		searchProgramsPerSearchDataCache = CacheBuilder.newBuilder()
				.expireAfterAccess( CACHE_TIMEOUT, TimeUnit.DAYS )
			    .maximumSize( CACHE_MAX_SIZE )
			    .build(
			    		new CacheLoader<LocalCacheKey, LocalCacheValue>() {
			    			public LocalCacheValue load(LocalCacheKey localCacheKey) throws Exception {
			    				//  value is NOT in cache so get it and return it
			    				return loadFromDB(localCacheKey);
			    			}
			    		});
//			    .build(); // no CacheLoader
		//  Register this class with the centralized Cached Data Registry, to support centralized cache clearing
		CachedDataCentralRegistry.getInstance().register( this );
	}
	
	/**
	 * classes for holding data in the cache
	 * 
	 * key to the cache
	 *
	 */
	private static class LocalCacheKey {
		Integer searchProgramsPerSearchId;
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime
					* result
					+ ((searchProgramsPerSearchId == null) ? 0
							: searchProgramsPerSearchId.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			LocalCacheKey other = (LocalCacheKey) obj;
			if (searchProgramsPerSearchId == null) {
				if (other.searchProgramsPerSearchId != null)
					return false;
			} else if (!searchProgramsPerSearchId
					.equals(other.searchProgramsPerSearchId))
				return false;
			return true;
		}
	}
	/**
	 * classes for holding data in the cache
	 * 
	 * value in the cache
	 *
	 */
	private static class LocalCacheValue {
		SearchProgramsPerSearchDTO searchProgramsPerSearchDTO;
	}
	
	/**
	 * cached annotation type data
	 */
	private LoadingCache<LocalCacheKey, LocalCacheValue> searchProgramsPerSearchDataCache = null;
	
	/**
	 * @param searchIds
	 * @param filterableDescriptiveAnnotationType
	 * @param psmPeptideAnnotationType
	 * @param localCacheKey
	 * @return
	 * @throws Exception
	 */
	private LocalCacheValue loadFromDB(   
			LocalCacheKey localCacheKey
			) throws Exception {
		//  value is NOT in cache so get it and return it
		SearchProgramsPerSearchDTO searchProgramsPerSearchDTO =
				SearchProgramsPerSearchDAO.getInstance().getSearchProgramDTOForId( localCacheKey.searchProgramsPerSearchId ) ;
		LocalCacheValue localCacheValue = new LocalCacheValue();
		localCacheValue.searchProgramsPerSearchDTO = searchProgramsPerSearchDTO;
		return localCacheValue;
	}
}
