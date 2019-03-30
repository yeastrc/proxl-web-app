package org.yeastrc.xlink.www.cached_data_mgmt;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;

/**
 * Registry of cached data that implement CachedDataCommonIF
 * 
 * This is to support a single point that can be called to clear all cached data from memory
 *
 */
public class CachedDataCentralRegistry {

	private static final Logger log = LoggerFactory.getLogger(  CachedDataCentralRegistry.class );

	private static final CachedDataCentralRegistry instance = new CachedDataCentralRegistry();

	private final Map<String,CachedDataCommonIF> cachedDataCommonItemMap = new ConcurrentHashMap<>();
	
	//  private constructor
	private CachedDataCentralRegistry() { }
	
	/**
	 * @return newly created instance
	 */
	public static CachedDataCentralRegistry getInstance() { 
		return instance;
	}
	
	/**
	 * @param cachedConfigDataCommonItem
	 */
	public void register( CachedDataCommonIF cachedConfigDataCommonItem ) {
		String className = cachedConfigDataCommonItem.getClass().getName();
		cachedDataCommonItemMap.put( className, cachedConfigDataCommonItem );
//		if ( log.isInfoEnabled() ) {
//			Exception e = new Exception();
//			log.info( "Adding cachedConfigDataCommonItem: " + cachedConfigDataCommonItem, e );
//		}
	}
	
	
	/**
	 * @throws Exception
	 */
	public void clearAllCacheData() throws Exception {
//		if ( log.isInfoEnabled() ) {
//			log.info( "clearAllCacheData() called" );
//		}
//		log.warn( "INFO: clearAllCacheData() called" );
		
		try {
			for ( Map.Entry<String,CachedDataCommonIF> entry : cachedDataCommonItemMap.entrySet() ) {
				CachedDataCommonIF item = entry.getValue();
				item.clearCacheData();
			}
		} catch (Exception e) {
			String msg = "Error clearAllCacheData(): ";
			log.error( msg, e );
			throw e;
		}
	}
	
	public void writeToLogAllCacheSizes() throws Exception {

		List<Map.Entry<String,CachedDataCommonIF>> cachedDataCommonItemList = new ArrayList<>( cachedDataCommonItemMap.size() );
		for ( Map.Entry<String,CachedDataCommonIF> entry : cachedDataCommonItemMap.entrySet() ) {
			cachedDataCommonItemList.add( entry );
		}
		//  Sort by class Name
		Collections.sort( cachedDataCommonItemList, new Comparator<Map.Entry<String,CachedDataCommonIF>>() {
			@Override
			public int compare(Entry<String, CachedDataCommonIF> o1, Entry<String, CachedDataCommonIF> o2) {
				return o1.getKey().compareTo( o2.getKey() );
			} } );
		
		NumberFormat numberFormatStandard = NumberFormat.getInstance();
		StringBuilder cacheSizesSB = new StringBuilder( 10000 );
		cacheSizesSB.append( "List of current Data Cache info " );
		try {
			for ( Map.Entry<String,CachedDataCommonIF> entry : cachedDataCommonItemList ) {
				CachedDataCommonIF cachedDataCommonIF = entry.getValue();
				CacheCurrentSizeMaxSizeResult cacheCurrentSizeMaxSizeResult = cachedDataCommonIF.getCurrentCacheSizeAndMax();
				cacheSizesSB.append( "\n class: " + entry.getKey()
						+ ", current cache size: " + numberFormatStandard.format( cacheCurrentSizeMaxSizeResult.getCurrentSize() ) 
						+ ", max cache size: " + numberFormatStandard.format( cacheCurrentSizeMaxSizeResult.getMaxSize() ) );
			}
		} catch (Exception e) {
			String msg = "Error writeToLogAllCacheSizes(): ";
			log.error( msg, e );
			throw e;
		}
		String cacheSizes = cacheSizesSB.toString();
		log.warn( cacheSizes );
	}
}
