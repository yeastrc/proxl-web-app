package org.yeastrc.proxl.import_xml_to_db.db;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.db.IDBConnectionFactory;


public class ImportDBConnectionFactory implements IDBConnectionFactory {

	private static Logger log = Logger.getLogger(ImportDBConnectionFactory.class);
	
	private static final String _DEFAULT_PORT = "3306";
	
	private static Map<String, BasicDataSource> _dataSources = null;
	
	private static IDBConnectionParametersProvider dbConnectionParametersProvider = null;
	
	
	
	/**
	 * Allow setting a value for dbConnectionParametersProvider
	 * 
	 * @param dbConnectionParametersProvider
	 */
	public static void setDbConnectionParametersProvider(
			IDBConnectionParametersProvider dbConnectionParametersProvider) {
		ImportDBConnectionFactory.dbConnectionParametersProvider = dbConnectionParametersProvider;
	}

	// get a connection to the requested database
	@Override
	public Connection getConnection( String db ) throws Exception {
		
		if ( dbConnectionParametersProvider == null ) {
			
			dbConnectionParametersProvider = new DBConnectionParametersProvider();
			
			dbConnectionParametersProvider.init();
		}
		
		
		//  TODO  TEMP for TESTING
		
		if ( DBConnectionFactory.CROSSLINKS.equals(db) ) {
			
			//  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			
			//     Hard coding DB name 

			db = "proxl_generic_fields";
			
		}
		
		
		
			if( _dataSources == null ) {
				_dataSources = new HashMap<String, BasicDataSource>();
			}
			
			BasicDataSource dataSource = _dataSources.get( db );
			
			if ( dataSource == null ) {
				
				//  create datasource for this db name 
				
				Class.forName("com.mysql.jdbc.Driver");
			
//				private String username;
//				private String password;
//				private String dbURL;
//				private String dbPort;
				
				String username = dbConnectionParametersProvider.getUsername(); 
				String password = dbConnectionParametersProvider.getPassword();
				String dbURL = dbConnectionParametersProvider.getDBURL();
				String dbPort = dbConnectionParametersProvider.getDBPort();
				
				if ( StringUtils.isEmpty( username ) ) {
					
					String msg = "No provided DB username or DB username is empty string.";
					log.error( msg );
					throw new Exception(msg);
				}
				
				if ( StringUtils.isEmpty( password ) ) {
					String msg = "No provided DB password or DB password is empty string.";
					log.error( msg );
					throw new Exception(msg);
				}
				
				if ( StringUtils.isEmpty( dbURL ) ) {
					String msg = "No provided DB URL or DB URL is empty string.";
					log.error( msg );
					throw new Exception(msg);
				}
				
				if ( StringUtils.isEmpty( dbPort ) ) {
					dbPort = _DEFAULT_PORT;  // set to default port
				}
				
				
				
				dataSource = new BasicDataSource();
				dataSource.setUrl("jdbc:mysql://" + dbURL + ":" + dbPort + "/" + db +
						  "?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8" );
	
				dataSource.setUsername( username );
				dataSource.setPassword( password );
	
				dataSource.setValidationQuery("select 1 from dual");
				dataSource.setTestOnBorrow( true );
				dataSource.setMinEvictableIdleTimeMillis( 21600000 );
				dataSource.setTimeBetweenEvictionRunsMillis( 30000 );
				
				_dataSources.put( db, dataSource );
			}
			
			return dataSource.getConnection();
	}
	
	// close them all
	@Override
	public void closeAllConnections() throws Exception {
		
		if( _dataSources == null ) {
			return;
		}
		
		for( Map.Entry<String, BasicDataSource> dataSourcesEntry : _dataSources.entrySet() ) {
			try {
				dataSourcesEntry.getValue().close();
				
			} catch( Exception e ) { 
				; 
			}
			
		}
		_dataSources = null;
	}
	
}
