package org.yeastrc.xlink.www.search_programs_per_search_utils;

import java.util.concurrent.TimeUnit;

import org.yeastrc.xlink.dao.SearchProgramsPerSearchDAO;
import org.yeastrc.xlink.dto.SearchProgramsPerSearchDTO;

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
public class GetSearchProgramsPerSearchData {

	
	private static final int CACHE_MAX_SIZE = 60;

	private static final int CACHE_TIMEOUT = 200; // in seconds


	/**
	 * This is the time after which the cache entry is replaced
	 * 
	 * currently 60 minutes
	 */
	private static final int CACHE__FORCED_TIMEOUT = 60 * 60 * 1000; // in milliseconds
	
	
	/**
	 * Static get singleton instance
	 * @return
	 */
	public static GetSearchProgramsPerSearchData getInstance() {
		return _instance; 
	}
	
	/**
	 * @param searchProgramsPerSearchId
	 * @return
	 * @throws Exception
	 */
	public SearchProgramsPerSearchDTO getSearchProgramsPerSearchDTO( Integer searchProgramsPerSearchId ) throws Exception {

		LocalCacheKey localCacheKey = new LocalCacheKey();
		
		localCacheKey.searchProgramsPerSearchId = searchProgramsPerSearchId;
		
		LocalCacheValue localCacheValue = annotationTypeDataCache.get( localCacheKey );
		
		if ( localCacheValue == null ) {
			
			throw new Exception("Should be getting annotation data via cache internal load");
		}

		//  value is in cache or was just loaded

		if ( localCacheValue.lastAccessTime < ( System.currentTimeMillis() - CACHE__FORCED_TIMEOUT ) ) {

			//   Compare time when item was put in cache and if time exceeded, reload item from db
			
			localCacheValue =  loadFromDB( localCacheKey );

			annotationTypeDataCache.put( localCacheKey, localCacheValue );
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
		
		annotationTypeDataCache = CacheBuilder.newBuilder()
				
				.expireAfterAccess( CACHE_TIMEOUT, TimeUnit.SECONDS )
			    .maximumSize( CACHE_MAX_SIZE )
			    .build(
			    		new CacheLoader<LocalCacheKey, LocalCacheValue>() {

			    			public LocalCacheValue load(LocalCacheKey localCacheKey) throws Exception {

			    				//  value is NOT in cache so get it and return it

			    				return loadFromDB(localCacheKey);

			    			}

			    		});
			    
//			    .build(); // no CacheLoader
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
		
		LocalCacheKey localCacheKey;
		SearchProgramsPerSearchDTO searchProgramsPerSearchDTO;
		
		long lastAccessTime;
	}
	
	
	
	
	
	/**
	 * cached annotation type data
	 */
	private LoadingCache<LocalCacheKey, LocalCacheValue> annotationTypeDataCache = null;
	
	
	
	
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

		localCacheValue.localCacheKey = localCacheKey;
		localCacheValue.searchProgramsPerSearchDTO = searchProgramsPerSearchDTO;
		
		localCacheValue.lastAccessTime = System.currentTimeMillis();
		
		return localCacheValue;
		
	}


	
}
