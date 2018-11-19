package org.yeastrc.auth.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 
 *
 */
public interface IAuthLibraryDBConnectionFactory {

	/**
	 * Get a connection to the database holding the authentication tables
	 * @return a Connection that close() will be called on when the database action is completed.
	 * @throws SQLException
	 */
	public Connection getConnection() throws Exception;

}
