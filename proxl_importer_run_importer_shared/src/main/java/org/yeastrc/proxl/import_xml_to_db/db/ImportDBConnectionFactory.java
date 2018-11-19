package org.yeastrc.proxl.import_xml_to_db.db;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.dbcp2.BasicDataSource;
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
	
	private static final int MAX_TOTAL_OTHER_DB_CONNECTIONS = 2;
//	private static final int MAX_TOTAL_NRSEQ_DB_CONNECTIONS = 1;
	private static final int MAX_TOTAL_PROXL_DB_CONNECTIONS = 2;
	
	
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
	
	private boolean databaseConnectionTestOnBorrow = false;
	
	private int maxDBConnectionsProxl = MAX_TOTAL_PROXL_DB_CONNECTIONS;

	/**
	 * Allow setting a value for dbConnectionParametersProvider
	 * 
	 * @param dbConnectionParametersProvider
	 */
	public void setDbConnectionParametersProvider(
			IDBConnectionParametersProvider dbConnectionParametersProvider) {
		this.dbConnectionParametersProvider = dbConnectionParametersProvider;
		

		if ( StringUtils.isNotEmpty( dbConnectionParametersProvider.getProxlDbName() ) ) {

			if ( log.isInfoEnabled() ) {

				System.out.println( "Proxl DB Name from Connection Provider: " + dbConnectionParametersProvider.getProxlDbName() );
				log.info( "Proxl DB Name from Connection Provider: " + dbConnectionParametersProvider.getProxlDbName() );
			}
		}
		

		if ( StringUtils.isNotEmpty( dbConnectionParametersProvider.getNrseqDbName() ) ) {

			if ( log.isInfoEnabled() ) {

				System.out.println( "YRC_NRSEQ DB Name from Connection Provider: " + dbConnectionParametersProvider.getNrseqDbName() );
				log.info( "YRC_NRSEQ DB Name from Connection Provider: " + dbConnectionParametersProvider.getNrseqDbName() );
			}
		}

	}
	
	/**
	 * @return
	 * @throws Exception
	 */
	public Connection getInsertControlCommitConnection() throws Exception {
		
		if ( _insertControlCommitConnection == null ) {
			
			_insertControlCommitConnection = getConnectionInternal( DBConnectionFactory.PROXL, true /* inserterConnection */ );
			
			_insertControlCommitConnection.setAutoCommit(false);

			_insertControlCommitConnectionGetCount = 0;
		}
		
		_insertControlCommitConnectionGetCount++;
		
		if ( _insertControlCommitConnectionGetCount > COMMIT_AFTER_500_INSERTS ) {
			
			_insertControlCommitConnection.commit();
			
			_insertControlCommitConnectionGetCount = 0;
		}
		
		return _insertControlCommitConnection;
	}
	
	
	
	/**
	 * call commit() on the insert connection and return the connection to the pool 
	 * @throws Exception
	 */
	public void commitInsertControlCommitConnection() throws Exception {

		if ( _insertControlCommitConnection == null ) {
			
			return;
		}
		
		_insertControlCommitConnection.commit();
		
		_insertControlCommitConnection.close(); // Return connection to pool
		
		_insertControlCommitConnection = null;
	}
	

	// get a connection to the requested database
	@Override
	public Connection getConnection( String db ) throws Exception {
		
		
		return getConnectionInternal( db, false /* inserterConnection */ );
	}
	


	// get a connection to the requested database
	
	private Connection getConnectionInternal( String db, boolean inserterConnection ) throws Exception {
		
		if ( dbConnectionParametersProvider == null ) {
			
			dbConnectionParametersProvider = new DBConnectionParametersProviderFromPropertiesFile();
			
			dbConnectionParametersProvider.init();
			

			if ( StringUtils.isNotEmpty( dbConnectionParametersProvider.getProxlDbName() ) ) {

				if ( log.isInfoEnabled() ) {

					System.out.println( "Proxl DB Name from Connection Provider: " + dbConnectionParametersProvider.getProxlDbName() );
					log.info( "Proxl DB Name from Connection Provider: " + dbConnectionParametersProvider.getProxlDbName() );
				}
			}
			

			if ( StringUtils.isNotEmpty( dbConnectionParametersProvider.getNrseqDbName() ) ) {

				if ( log.isInfoEnabled() ) {

					System.out.println( "YRC_NRSEQ DB Name from Connection Provider: " + dbConnectionParametersProvider.getNrseqDbName() );
					log.info( "YRC_NRSEQ DB Name from Connection Provider: " + dbConnectionParametersProvider.getNrseqDbName() );
				}
			}
		}
		
		//  Allow change of database
		
		if ( DBConnectionFactory.PROXL.equals(db) ) {
			
			if ( StringUtils.isNotEmpty( dbConnectionParametersProvider.getProxlDbName() ) ) {

				db = dbConnectionParametersProvider.getProxlDbName();
			}
		}
		


		if( _dataSources == null ) {

			_dataSources = new HashMap<String, BasicDataSource>();

			Class.forName("com.mysql.jdbc.Driver");
		}
		
		
		String dbNameForMapLookup = db;
		
		if ( inserterConnection ) {
			
			dbNameForMapLookup += "__INSERTER";  //  Use different data source for Inserter
		}
		
		

		BasicDataSource dataSource = _dataSources.get( dbNameForMapLookup );
		

		if ( dataSource == null ) {

			//  create datasource for this db name 
			
			
			
			int maxDBConnections = maxDBConnectionsProxl;
			

			boolean poolPreparedStatements = true;
			int maxOpenPreparedStatements = 10;
			

			if ( inserterConnection ) {
				
				maxDBConnections = 1;
				maxOpenPreparedStatements = 10;

			} else if ( ! ( DBConnectionFactory.PROXL.equals( db ) 
					|| ( dbConnectionParametersProvider.getProxlDbName() != null 
						&& dbConnectionParametersProvider.getProxlDbName().equals( db ) ) ) ) {
				
//				if ( DBConnectionFactory.YRC_NRSEQ.equals(db) 
//						|| ( dbConnectionParametersProvider.getNrseqDbName() != null 
//								&& dbConnectionParametersProvider.getNrseqDbName().equals( db ) ) ) {
//					
//					maxDBConnections = MAX_TOTAL_NRSEQ_DB_CONNECTIONS;
//					
//					maxOpenPreparedStatements = 10;
//				} else {
					
					maxDBConnections = MAX_TOTAL_OTHER_DB_CONNECTIONS;
					
					maxOpenPreparedStatements = 1;
//				}
				
			}


			String username = dbConnectionParametersProvider.getUsername(); 
			String password = dbConnectionParametersProvider.getPassword();
			String dbURL = dbConnectionParametersProvider.getDBURL();
			String dbPort = dbConnectionParametersProvider.getDBPort();

			if ( StringUtils.isEmpty( username ) ) {

				String msg = "No provided DB username or DB username is empty string.";
				log.error( msg );
				throw new Exception(msg);
			} else {
				
				if ( log.isDebugEnabled() ) {
					
					log.debug( "DB Connection:  username: " + username );
				}
			}

			if ( StringUtils.isEmpty( password ) ) {
				String msg = "No provided DB password or DB password is empty string.";
				log.error( msg );
				throw new Exception(msg);
			} else {
				
				if ( log.isDebugEnabled() ) {
					
					log.debug( "DB Connection:  password: is populated" );
				}
			}

			if ( StringUtils.isEmpty( dbURL ) ) {
				String msg = "No provided DB URL or DB URL is empty string.";
				log.error( msg );
				throw new Exception(msg);
			} else {
				
				if ( log.isDebugEnabled() ) {
					
					log.debug( "DB Connection:  dbURL: " + dbURL);
				}
			}

			if ( StringUtils.isEmpty( dbPort ) ) {
				dbPort = _DEFAULT_PORT;  // set to default port
				
				if ( log.isDebugEnabled() ) {
					
					log.debug( "DB Connection:  dbPort set to default: " + dbPort);
				}
			} else {
				
				if ( log.isDebugEnabled() ) {
					
					log.debug( "DB Connection:  dbPort: " + dbPort);
				}
			}



			dataSource = new BasicDataSource();
			dataSource.setUrl("jdbc:mysql://" + dbURL + ":" + dbPort + "/" + db +
					"?useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&useSSL=false" );  // removed autoReconnect=true&

			dataSource.setUsername( username );
			dataSource.setPassword( password );
			
			dataSource.setMaxTotal( maxDBConnections );
			dataSource.setInitialSize( maxDBConnections );
			dataSource.setMaxIdle( maxDBConnections );
			dataSource.setMinIdle( 1 );
			
			dataSource.setMaxWaitMillis(100);
			
			if ( databaseConnectionTestOnBorrow ) {

				dataSource.setValidationQuery("select 1 from dual");

				dataSource.setTestOnBorrow( databaseConnectionTestOnBorrow );  // databaseConnectionTestOnBorrow is default to false;
				dataSource.setTestWhileIdle( true );

			}
			

//			dataSource.setMinEvictableIdleTimeMillis   ( 21600000 );
//			dataSource.setTimeBetweenEvictionRunsMillis(   30000 );
//			dataSource.setNumTestsPerEvictionRun( maxDBConnections ); // Test all of them
			

			dataSource.setMinEvictableIdleTimeMillis   ( 72000000 );  // 20 minutes
			dataSource.setTimeBetweenEvictionRunsMillis(   300000 );  //  5 minutes
			dataSource.setNumTestsPerEvictionRun( maxDBConnections ); // Test all of them, expensive if you also have setTestWhileIdle( true );
			
			
//			dataSource.setMinEvictableIdleTimeMillis   ( 5000 );
//			dataSource.setTimeBetweenEvictionRunsMillis(   3000 );
//			dataSource.setNumTestsPerEvictionRun( maxDBConnections ); // Test all of them
			
//			dataSource.setLifo( false );  // Set so is FIFO

			
//			dataSource.setLogAbandoned(true); //  Adds significant overhead
			
			
			dataSource.setMaxConnLifetimeMillis( 14400000 ); //  4 hours //  The maximum lifetime in milliseconds of a connection. 
			
			dataSource.setLogExpiredConnections(true);  //  Log connections that are removed for maxConnLifetimeMillis

			
			//  Pool prepared statements
			
			
//			dataSource.setPoolPreparedStatements( poolPreparedStatements );  // activate 
//			dataSource.setMaxOpenPreparedStatements( maxOpenPreparedStatements ); //  set max Open
			
			

			_dataSources.put( dbNameForMapLookup, dataSource );
		}
		
		
//		Exception e = new Exception();
//		
//		e.printStackTrace();

		try {
		
			return dataSource.getConnection();
			
		} catch ( Throwable t ) {
			
			log.error( "Failed to get connection for database: " + db, t );
			
			throw t;
		}
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

	public boolean isDatabaseConnectionTestOnBorrow() {
		return databaseConnectionTestOnBorrow;
	}

	public void setDatabaseConnectionTestOnBorrow(
			boolean databaseConnectionTestOnBorrow) {
		this.databaseConnectionTestOnBorrow = databaseConnectionTestOnBorrow;
	}

	public int getMaxDBConnectionsProxl() {
		return maxDBConnectionsProxl;
	}

	public void setMaxDBConnectionsProxl(int maxDBConnectionsProxl) {
		this.maxDBConnectionsProxl = maxDBConnectionsProxl;
	}

	
	

}
