package org.yeastrc.xlink.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Interface for DBConnection factories that may be implemented by any programs
 * using Crosslinking_Base
 * 
 * @author Mike
 *
 */
public interface IDBConnectionFactory {

		/**
		 * Get a connection to the specified database
		 * @param db
		 * @return
		 * @throws SQLException
		 */
		public Connection getConnection(String db) throws Exception;

		/**
		 * Ensure all open connections are closed. This only needs implementing if
		 * connection pooling is being employed. Otherwise a stub will suffice.
		 * @throws SQLException
		 */
		public void closeAllConnections() throws Exception;

	
}
