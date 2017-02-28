package org.yeastrc.xlink.www.cached_data_mgmt;

/**
 * Common interface all Cached Data classes will implement
 * 
 * This is to support a single point that can be called to clear all cached data from memory
 */
public interface CachedDataCommonIF {

	public void clearCacheData() throws Exception;
}
