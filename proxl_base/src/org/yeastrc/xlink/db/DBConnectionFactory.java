package org.yeastrc.xlink.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * Get connections to the database. 
 * 
 * Set the IDBConnectionFactory implementer to be used to get connections to the database via
 * setDbConnectionFactoryImpl( your_implementation_here ) at the start of your program.

 * @author Mike
 *
 */
public class DBConnectionFactory  {
	
	private static final Logger log = Logger.getLogger(DBConnectionFactory.class);
			

	public static final String PROXL = "proxl";
	
	
		
	/**
	 * Instance variable holding the IDBConnectionFactory we want serving
	 * connections
	 */
	private static IDBConnectionFactory dbConnectionFactoryImpl = null;
	

	/**
	 * Get the IDBConnectionFactory we want serving connections.
	 * 
	 * @return
	 */
	public static IDBConnectionFactory getDbConnectionFactoryImpl() {
		return dbConnectionFactoryImpl;
	}

	/**
	 * Set the IDBConnectionFactory we want serving connections. If not set,
	 * JNDI will be used (I.e., the web site)
	 * 
	 * @param connectionFactoryImpl
	 */
	public static synchronized void setDbConnectionFactoryImpl(
			IDBConnectionFactory connectionFactoryImpl) {
		DBConnectionFactory.dbConnectionFactoryImpl = connectionFactoryImpl;
	}

	/**
	 * Get a connection to the specified database.
	 * 
	 * @param db
	 * @return
	 * @throws SQLException
	 */
	public static Connection getConnection(String db) throws Exception {

		if (dbConnectionFactoryImpl != null) {
			return dbConnectionFactoryImpl.getConnection(db);
		}

		String msg = "Not Initialized: dbConnectionFactoryImpl == null ";
		
		log.error( msg );
		
		throw new IllegalStateException(msg);
	}

	
	public static synchronized void closeAllConnections() throws Exception {
		
		log.info( " closeAllConnections() called." );
		
		if (dbConnectionFactoryImpl != null) {

			log.info( " closeAllConnections() called. dbConnectionFactoryImpl != null so calling dbConnectionFactoryImpl.closeAllConnections();" );

			try {
				dbConnectionFactoryImpl.closeAllConnections();


			} catch ( Exception e ) {

				log.error( "ERROR: closeAllConnections() ", e );

				throw e;
			}

		}
	}
	
}
