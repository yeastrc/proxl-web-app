package org.yeastrc.proxl.import_xml_to_db.db;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.db.IDBConnectionFactory;


/**
 * Singleton 
 *
 */
public class ImportDBConnectionFactory implements IDBConnectionFactory {

	private static Logger log = Logger.getLogger(ImportDBConnectionFactory.class);
	
	private static final String _DEFAULT_PORT = "3306";
	
	private static final int COMMIT_AFTER_500_INSERTS = 500;
	
	
	//  Singleton 
	private static final ImportDBConnectionFactory _INSTANCE = new ImportDBConnectionFactory();
	
	// private constructor
	private ImportDBConnectionFactory() { }
	
	public static ImportDBConnectionFactory getInstance() { return _INSTANCE; }
	
	
	private Map<String, BasicDataSource> _dataSources = null;
	
	private Connection _insertControlCommitConnection = null;
	
	private int _insertControlCommitConnectionGetCount = 0;
	
	private IDBConnectionParametersProvider dbConnectionParametersProvider = null;
	
	private String proxlDatabaseName = DBConnectionFactory.PROXL;
	

	/**
	 * Allow setting a value for dbConnectionParametersProvider
	 * 
	 * @param dbConnectionParametersProvider
	 */
	public void setDbConnectionParametersProvider(
			IDBConnectionParametersProvider dbConnectionParametersProvider) {
		this.dbConnectionParametersProvider = dbConnectionParametersProvider;
	}
	
	public Connection getInsertControlCommitConnection() throws Exception {
		
		if ( _insertControlCommitConnection == null ) {
			
			_insertControlCommitConnection = getConnection( DBConnectionFactory.PROXL );
			
			_insertControlCommitConnection.setAutoCommit(false);
		}
		
		_insertControlCommitConnectionGetCount++;
		
		if ( _insertControlCommitConnectionGetCount > COMMIT_AFTER_500_INSERTS ) {
			
			commitInsertControlCommitConnection();
			
			_insertControlCommitConnectionGetCount = 0;
		}
		
		return _insertControlCommitConnection;
	}
	
	public void commitInsertControlCommitConnection() throws Exception {

		if ( _insertControlCommitConnection == null ) {
			
			return;
		}
		
		_insertControlCommitConnection.commit();
	}
	

	// get a connection to the requested database
	@Override
	public Connection getConnection( String db ) throws Exception {
		
		if ( dbConnectionParametersProvider == null ) {
			
			dbConnectionParametersProvider = new DBConnectionParametersProvider();
			
			dbConnectionParametersProvider.init();
		}
		
		//  Allow change of database
		
		if ( DBConnectionFactory.PROXL.equals(db) ) {
			
			db = proxlDatabaseName;
			
		}
		


		if( _dataSources == null ) {

			_dataSources = new HashMap<String, BasicDataSource>();

			Class.forName("com.mysql.jdbc.Driver");
		}

		BasicDataSource dataSource = _dataSources.get( db );

		if ( dataSource == null ) {

			//  create datasource for this db name 


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
		
		if ( _insertControlCommitConnection != null ) {
			
			boolean connectionAutoCommit = _insertControlCommitConnection.getAutoCommit();
			
			if ( ! connectionAutoCommit ) {
				_insertControlCommitConnection.commit();
			}
			
			_insertControlCommitConnection.close();
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

	

	
	public String getProxlDatabaseName() {
		return proxlDatabaseName;
	}

	public void setProxlDatabaseName(String proxlDatabaseName) {
		this.proxlDatabaseName = proxlDatabaseName;
	}
}
