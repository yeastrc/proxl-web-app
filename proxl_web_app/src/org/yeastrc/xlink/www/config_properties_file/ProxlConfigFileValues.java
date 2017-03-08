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
