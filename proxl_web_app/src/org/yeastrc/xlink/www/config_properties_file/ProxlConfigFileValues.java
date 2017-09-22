package org.yeastrc.xlink.www.config_properties_file;

/**
 * Values from proxl_config_defaults.properties or overlaid by proxl_config.properties
 *
 * Singleton instance
 */
public class ProxlConfigFileValues {

	private static final ProxlConfigFileValues instance = new ProxlConfigFileValues();
	
	/**
	 * @return Singleton instance
	 */
	public static ProxlConfigFileValues getInstance() {
		return instance;
	}
	
	/**
	 * private constructor
	 */
	private ProxlConfigFileValues() {}
	
	private String userAccountServerURLandAppContext;
	private String requestingWebappIdentifier;
	private String requestingWebappKey;
	private String requestingEncryptionKey;
	
 	/**
	 * special for when re-computing values for table unified_rp__search__rep_pept__generic_lookup
	 * First set annotation_type_filterable.default_filter_value_at_database_load = null where annotation_type_filterable.default_filter_at_database_load = 1
	 *     so that those records will always cause the determination for is cutoffs == default cutoffs to be false.
	 */
	private boolean allowAnnTypeFilterDefaultFilterValueAtDatabaseLoad_Null_When_isDefaultFilter_True;

	/**
	 * special for when re-computing values for table unified_rp__search__rep_pept__generic_lookup
	 * First set annotation_type_filterable.default_filter_value_at_database_load = null where annotation_type_filterable.default_filter_at_database_load = 1
	 *     so that those records will always cause the determination for is cutoffs == default cutoffs to be false.
	 * @return
	 */
	public boolean isAllowAnnTypeFilterDefaultFilterValueAtDatabaseLoad_Null_When_isDefaultFilter_True() {
		return allowAnnTypeFilterDefaultFilterValueAtDatabaseLoad_Null_When_isDefaultFilter_True;
	}

	/**
	 * special for when re-computing values for table unified_rp__search__rep_pept__generic_lookup
	 * First set annotation_type_filterable.default_filter_value_at_database_load = null where annotation_type_filterable.default_filter_at_database_load = 1
	 *     so that those records will always cause the determination for is cutoffs == default cutoffs to be false.
	 * @param allowAnnTypeFilterDefaultFilterValueAtDatabaseLoad_Null_When_isDefaultFilter_True
	 */
	public void setAllowAnnTypeFilterDefaultFilterValueAtDatabaseLoad_Null_When_isDefaultFilter_True(
			boolean allowAnnTypeFilterDefaultFilterValueAtDatabaseLoad_Null_When_isDefaultFilter_True) {
		this.allowAnnTypeFilterDefaultFilterValueAtDatabaseLoad_Null_When_isDefaultFilter_True = allowAnnTypeFilterDefaultFilterValueAtDatabaseLoad_Null_When_isDefaultFilter_True;
	}
	
	
	public String getRequestingEncryptionKey() {
		return requestingEncryptionKey;
	}
	public void setRequestingEncryptionKey(String requestingEncryptionKey) {
		this.requestingEncryptionKey = requestingEncryptionKey;
	}
	public String getRequestingWebappIdentifier() {
		return requestingWebappIdentifier;
	}
	public void setRequestingWebappIdentifier(String requestingWebappIdentifier) {
		this.requestingWebappIdentifier = requestingWebappIdentifier;
	}
	public String getRequestingWebappKey() {
		return requestingWebappKey;
	}
	public void setRequestingWebappKey(String requestingWebappKey) {
		this.requestingWebappKey = requestingWebappKey;
	}
	public String getUserAccountServerURLandAppContext() {
		return userAccountServerURLandAppContext;
	}
	public void setUserAccountServerURLandAppContext(String userAccountServerURLandAppContext) {
		this.userAccountServerURLandAppContext = userAccountServerURLandAppContext;
	}

}
