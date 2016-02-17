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
	
	private static final String JNDI_NAME_SUFFIX__proxl_generic_fields = "proxl_generic_fields";


	private static final String JNDI_NAME_proxl = JNDI_NAME_PREFIX + JNDI_NAME_SUFFIX_proxl;
	
	private static final String JNDI_NAME_proxl_generic_fields = JNDI_NAME_PREFIX + JNDI_NAME_SUFFIX__proxl_generic_fields;
	
	

	private static String proxlJNDIName = JNDI_NAME_proxl;


	/**
	 * Change Proxl JNDI name to proxl_generic_fields (check the code for this string)
	 */
	public static void setProxlJNDINameTo_proxl_generic_fields() {
		
		proxlJNDIName = JNDI_NAME_proxl_generic_fields;

		if ( log.isInfoEnabled() ) {
			log.info("setProxlJNDINameTo_proxl_generic_fields() called. proxlJNDIName now: " + proxlJNDIName);
		}		
	}
	
	

	/**
	 * Change Proxl JNDI name to proxl_generic_fields (check the code for this string)
	 */
	public static void setProxlJNDINameTo_proxl_generic_fields_demo_feb_2016() {
		
		proxlJNDIName = JNDI_NAME_PREFIX +  "proxl_generic_fields_demo_feb_2016";

		if ( log.isInfoEnabled() ) {
			log.info("setProxlJNDINameTo_proxl_generic_fields_demo_feb_2016() called. proxlJNDIName now: " + proxlJNDIName);
		}		
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

			if (db.equals(DBConnectionFactory.CROSSLINKS)) {
				
				ds = (DataSource) ctx.lookup( proxlJNDIName );
				
			} else if (db.equals(DBConnectionFactory.YRC_NRSEQ)) {
				
				ds = (DataSource) ctx.lookup("java:comp/env/jdbc/nrseq");
			}

			else {
				throw new SQLException(
						"Invalid database name passed into DBConnectionManager.  db: " + db );
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
