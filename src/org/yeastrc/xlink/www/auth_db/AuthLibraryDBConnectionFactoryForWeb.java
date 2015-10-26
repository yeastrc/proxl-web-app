package org.yeastrc.xlink.www.auth_db;

import java.sql.Connection;

import org.yeastrc.auth.db.IAuthLibraryDBConnectionFactory;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * concrete class that will be passed to AuthLibraryDBConnectionFactory in Auth Library
 * for getting a database connection in the Cross Linking web app
 *
 */
public class AuthLibraryDBConnectionFactoryForWeb implements IAuthLibraryDBConnectionFactory {

	/* (non-Javadoc)
	 * @see org.yeastrc.auth.db.IAuthLibraryDBConnectionFactory#getConnection()
	 */
	@Override
	public Connection getConnection() throws Exception {

		return DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
	}

}
