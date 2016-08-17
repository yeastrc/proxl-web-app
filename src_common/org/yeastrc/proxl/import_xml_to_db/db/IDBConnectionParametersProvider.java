package org.yeastrc.proxl.import_xml_to_db.db;

/**
 * Interface that the class that provides DB connection parameters must implement
 * 
 * 
 *
 */
public interface IDBConnectionParametersProvider {

	/**
	 * Called before any values are retrieved
	 */
	public void init() throws Exception;
	
	
	/**
	 * Username to use to log into DB
	 * 
	 * @return
	 */
	public String getUsername();
	
	/**
	 * Password to use to log into DB
	 * @return
	 */
	public String getPassword();
	
	/**
	 * URL of machine that DB running on.  DO NOT include the ":" and the port
	 * This will likely be an IP address, a DNS name, or "localhost"
	 * @return
	 */
	public String getDBURL();
	
	/**
	 * Port that DB is listening on.  Return null to use MySQL default of '3306'
	 * @return
	 */
	public String getDBPort();
	

	/**
	 * Database name for 'proxl'.  Return null to use Importer default of 'proxl'
	 * @return
	 */
	public String getProxlDbName();

	/**
	 * No longer used, can always return null
	 * @return
	 */
	public String getNrseqDbName();
	
	
}
