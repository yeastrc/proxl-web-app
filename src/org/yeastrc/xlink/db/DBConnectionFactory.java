package org.yeastrc.xlink.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

/**
 * Get connections to the database. Unless using JNDI naming (such as with the web site), be sure
 * to set the IDBConnectionFactory implementer to be used to get connections to the database via
 * setDbConnectionFactoryImpl( your_implementation_here ) at the start of your program.

 * @author Mike
 *
 */
public class DBConnectionFactory  {
	
	private static final Logger log = Logger.getLogger(DBConnectionFactory.class);
			

	public static final String CROSSLINKS = "proxl";
	public static final String YRC_NRSEQ = "YRC_NRSEQ";

	
	private static final String JNDI_NAME_PREFIX = "java:comp/env/jdbc/";
	
	private static final String JNDI_NAME_SUFFIX_proxl = "proxl";
	
	private static final String JNDI_NAME_SUFFIX__proxl_old_crosslinks_runs = "proxl_old_crosslinks_runs";


	private static final String JNDI_NAME_proxl = JNDI_NAME_PREFIX + JNDI_NAME_SUFFIX_proxl;
	
	private static final String JNDI_NAME_proxl_old_crosslinks_runs = JNDI_NAME_PREFIX + JNDI_NAME_SUFFIX__proxl_old_crosslinks_runs;
	
	
		
	/**
	 * Instance variable holding the IDBConnectionFactory we want serving
	 * connections
	 */
	private static IDBConnectionFactory dbConnectionFactoryImpl = null;
	
	private static String proxlJNDIName = JNDI_NAME_proxl;


	public static void setProxlJNDINameTo_proxl_old_crosslinks_runs() {
		
		proxlJNDIName = JNDI_NAME_proxl_old_crosslinks_runs;

		if ( log.isInfoEnabled() ) {
			log.info("setProxlJNDINameTo_proxl_old_crosslinks_runs() called. proxlJNDIName now: " + proxlJNDIName);
		}		
	}

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

		return getConnectionWeb(db);
	}

	/**
	 * Get DataSource from JNDI as setup in Application Server and get database
	 * connection from it
	 * 
	 * @param db
	 * @return
	 * @throws SQLException
	 */
	private static Connection getConnectionWeb(String db) throws Exception {

		try {
			Context ctx = new InitialContext();
			DataSource ds;
			Connection conn;

			if (db.equals(CROSSLINKS)) {
				
				ds = (DataSource) ctx.lookup( proxlJNDIName );
				
			} else if (db.equals(YRC_NRSEQ)) {
				
				ds = (DataSource) ctx.lookup("java:comp/env/jdbc/nrseq");
			}

			else {
				throw new SQLException(
						"Invalid database name passed into DBConnectionManager.  db: " + db );
			}

			if (ds != null) {
				conn = ds.getConnection();
				if (conn != null) {
					
					boolean connectionAutoCommit = conn.getAutoCommit();

					return conn;
				} else {
					throw new SQLException("Got a null connection...");
				}
			}
			

			throw new SQLException("Got a null DataSource...");
		} catch (NamingException ne) {
			
			log.error( "ERROR: getting database connection: db: " + db, ne );

			throw new SQLException("Naming exception: " + ne.getMessage(), ne);
		
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: getting database connection: db: " + db, e );
			
			throw e;
		}
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
