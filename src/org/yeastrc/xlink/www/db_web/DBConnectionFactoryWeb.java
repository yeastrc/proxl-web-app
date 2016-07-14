package org.yeastrc.xlink.www.db_web;

import java.sql.Connection;
import java.sql.SQLException;

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
		
	}
}
