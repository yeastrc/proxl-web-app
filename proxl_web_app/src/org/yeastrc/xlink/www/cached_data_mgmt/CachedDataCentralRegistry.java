package org.yeastrc.xlink.www.cached_data_mgmt;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Registry of cached data that implement CachedDataCommonIF
 * 
 * This is to support a single point that can be called to clear all cached data from memory
 *
 */
public class CachedDataCentralRegistry {

	private static final Logger log = Logger.getLogger( CachedDataCentralRegistry.class );

	private static final CachedDataCentralRegistry instance = new CachedDataCentralRegistry();

	private List<CachedDataCommonIF> cachedDataCommonItemList = new ArrayList<>();
	
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
		cachedDataCommonItemList.add(cachedConfigDataCommonItem);
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
			for ( CachedDataCommonIF item : cachedDataCommonItemList ) {
				item.clearCacheData();
			}
		} catch (Exception e) {
			String msg = "Error clearAllCacheData(): ";
			log.error( msg, e );
			throw e;
		}
	}
}
