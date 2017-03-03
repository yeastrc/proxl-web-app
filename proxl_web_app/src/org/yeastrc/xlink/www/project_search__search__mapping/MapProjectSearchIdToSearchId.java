package org.yeastrc.xlink.www.project_search__search__mapping;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.cached_data_mgmt.CacheCurrentSizeMaxSizeResult;
import org.yeastrc.xlink.www.cached_data_mgmt.CachedDataCentralRegistry;
import org.yeastrc.xlink.www.cached_data_mgmt.CachedDataCommonIF;
import org.yeastrc.xlink.www.searcher.SearchIdForProjectSearchIdSearcher;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Provide ProjectSearchId to SearchId mapping
 * 
 * Singleton Class
 */
public class MapProjectSearchIdToSearchId implements CachedDataCommonIF {

	private static final Logger log = Logger.getLogger(MapProjectSearchIdToSearchId.class);
	
	private static final int CACHE_MAX_SIZE = 500;
	private static final int CACHE_TIMEOUT = 50; // in days

	/**
	 * singleton instance
	 */
	private static final MapProjectSearchIdToSearchId _INSTANCE = new MapProjectSearchIdToSearchId();
	
	/**
	 * Static get singleton instance
	 * @return
	 */
	public static MapProjectSearchIdToSearchId getInstance() { return _INSTANCE; }

	/* (non-Javadoc)
	 * @see org.yeastrc.xlink.www.cached_data_mgmt.CachedDataCommonIF#clearCacheData()
	 * 
	 * Clear all entries from the cache
	 */
	@Override
	public void clearCacheData() throws Exception {
		projectSearchIdToSearchIdMappingCache.invalidateAll();
	}

	@Override
	public CacheCurrentSizeMaxSizeResult getCurrentCacheSizeAndMax() throws Exception {
		CacheCurrentSizeMaxSizeResult result = new CacheCurrentSizeMaxSizeResult();
		result.setCurrentSize( projectSearchIdToSearchIdMappingCache.size() );
		result.setMaxSize( CACHE_MAX_SIZE );
		return result;
	}
	
	/**
	 * @param projectSearchId
	 * @return - Null if search id not found
	 * @throws Exception 
	 */
	public Integer getSearchIdFromProjectSearchId( int projectSearchId ) throws Exception {

		LocalCacheKey localCacheKey = new LocalCacheKey();
		
		localCacheKey.projectSearchId = projectSearchId;
		
		LocalCacheValue localCacheValue = projectSearchIdToSearchIdMappingCache.get( localCacheKey );
		
		if ( localCacheValue == null ) {
			throw new Exception("Should be getting searchId data via cache internal load");
		}

		int searchId = localCacheValue.searchId;

		return searchId;
	}
	

	
	/**
	 * private constructor
	 */
	private MapProjectSearchIdToSearchId() {
		
		projectSearchIdToSearchIdMappingCache = CacheBuilder.newBuilder()
				
				.expireAfterAccess( CACHE_TIMEOUT, TimeUnit.HOURS )
			    .maximumSize( CACHE_MAX_SIZE )
			    .build( new CacheLoader<LocalCacheKey, LocalCacheValue>() {
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
		
		int projectSearchId;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + projectSearchId;
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
			if (projectSearchId != other.projectSearchId)
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
		
		Integer searchId;
	}
	
	
	/**
	 * cached projectSearchId to searchId mapping data
	 */
	private LoadingCache<LocalCacheKey, LocalCacheValue> projectSearchIdToSearchIdMappingCache = null;
		
	/**
	 * Called from Cache to load a value from DB
	 * 
	 * @param searchIds
	 * @param filterableDescriptiveAnnotationType
	 * @param psmPeptideAnnotationType
	 * @param localCacheKey
	 * @return
	 * @throws Exception
	 */
	private LocalCacheValue loadFromDB( LocalCacheKey localCacheKey ) throws Exception {

		//  value is NOT in cache so get it and return it
		try {
			Integer searchId =
					SearchIdForProjectSearchIdSearcher.getInstance()
					.getSearchIdForProjectSearchId( localCacheKey.projectSearchId );

			LocalCacheValue localCacheValue = new LocalCacheValue();
			localCacheValue.searchId = searchId;
			return localCacheValue;
			
		} catch ( Exception e ) {
			String msg = "Failed to retrieve searchId for localCacheKey.projectSearchId: " + localCacheKey.projectSearchId;
			log.error( msg, e );
			throw e;
		}
	}
	
}
