package org.yeastrc.xlink.www.db_web;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicLong;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.db.IDBConnectionFactory;

/**
 * Connection Factory for web app
 *
 */
public class DBConnectionFactoryWeb implements IDBConnectionFactory {

	private static final Logger log = Logger.getLogger( DBConnectionFactoryWeb.class );

	private static final String JNDI_NAME_PREFIX = "java:comp/env/jdbc/";
	private static final String JNDI_NAME_SUFFIX_proxl = "proxl";
	private static final String JNDI_NAME_proxl = JNDI_NAME_PREFIX + JNDI_NAME_SUFFIX_proxl;

	private static String proxlJNDIName = JNDI_NAME_proxl;

	private static final AtomicLong dbConnectionRetrievalCount = new AtomicLong();

	private static volatile int prevDayOfYear = -1;

	private boolean debugLogLevelEnabled = false;

	/**
	 * Constructor
	 */
	public DBConnectionFactoryWeb() {
		if ( log.isDebugEnabled() ) {
			debugLogLevelEnabled = true;
			log.debug( "debug log level enabled" );
		}
	}

	/**
	 * Change Proxl JNDI name 
	 */
	public static void setProxlJNDIName( String newProxlJNDINameSuffix ) {
		proxlJNDIName = JNDI_NAME_PREFIX + newProxlJNDINameSuffix;
		log.warn("INFO:  setProxlJNDIName() called. proxlJNDIName now: " + proxlJNDIName);
	}

	/* 
	 * Get DataSource from JNDI as setup in Application Server and get database
	 * connection from it
	 */
	@Override
	public Connection getConnection(String db) throws Exception {

		//		 Get DataSource from JNDI as setup in Application Server and get database connection from it

		printGetConnectionCounts( false /* forcePrintNow */ );

		if ( debugLogLevelEnabled ) {
			dbConnectionRetrievalCount.incrementAndGet();
		}

		try {
			Context ctx = new InitialContext();
			DataSource ds;
			Connection conn;

			if (db.equals(DBConnectionFactory.PROXL)) {
				ds = (DataSource) ctx.lookup( proxlJNDIName );
			} else {
				throw new SQLException( "Invalid database name passed into DBConnectionManager.  db: " + db );
			}

			if (ds != null) {
				conn = ds.getConnection();
				if (conn != null) {
					//					boolean connectionAutoCommit = conn.getAutoCommit();
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

	@Override
	public void closeAllConnections() throws Exception {
		//  Not applicable for Web	
		printGetConnectionCounts( true /* forcePrintNow */ );
	}

	/**
	 * 
	 */
	private void printGetConnectionCounts( boolean forcePrintNow ) {
		Calendar now = Calendar.getInstance();
		int nowDayOfYear = now.get( Calendar.DAY_OF_YEAR );
		if ( prevDayOfYear != nowDayOfYear || forcePrintNow ) {
			if ( prevDayOfYear != -1 ) {
				if ( debugLogLevelEnabled ) {
					log.debug( "DB Connection Retrieval Count for previous day: " 
							+ dbConnectionRetrievalCount.intValue() );
				}
			}
			if ( forcePrintNow ) {
				if ( debugLogLevelEnabled ) {
					log.debug( "DB Connection Retrieval Count since last print: " 
							+ dbConnectionRetrievalCount.intValue() );
				}
			}
			prevDayOfYear = nowDayOfYear;
			//  Reset counter
			dbConnectionRetrievalCount.set(0);
		}
	}

	/**
	 * 
	 */
	public void printCurrentGetConnectionCounts(  ) {
		if ( debugLogLevelEnabled ) {
			log.debug( "DB Connection Retrieval Count since previous day or app startup: " 
					+ dbConnectionRetrievalCount.intValue() );
		} else {
			log.warn( "Debug not enabled so not tracking DB Connection Retrieval Count.");
		}
	}

}
