package org.yeastrc.auth.db;

import java.sql.Connection;
import java.sql.SQLException;
/**
 *
 */
public class AuthLibraryDBConnectionFactory {
	/**
	 * Instance variable holding the IAuthLibraryDBConnectionFactory we want serving
	 * connections
	 */
	private static IAuthLibraryDBConnectionFactory dbConnectionFactoryImpl = null;
	/**
	 * Set the IDBConnectionFactory we want serving connections.
	 * 
	 * @param connectionFactoryImpl
	 */
	public static void setDbConnectionFactoryImpl(
			IAuthLibraryDBConnectionFactory connectionFactoryImpl) {
		dbConnectionFactoryImpl = connectionFactoryImpl;
	}
	/**
	 * Get a connection to the database.
	 * 
	 * @param db
	 * @return
	 * @throws SQLException
	 */
	public static Connection getConnection() throws Exception {
		if (dbConnectionFactoryImpl == null) {
			throw new IllegalStateException( "Not initialized with a dbConnectionFactoryImpl object" );
		}
		return dbConnectionFactoryImpl.getConnection();
	}
}
