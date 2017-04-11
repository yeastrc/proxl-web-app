package org.yeastrc.xlink.www.user_mgmt_db;

import java.sql.Connection;

import org.yeastrc.user_mgmt_central.main_code.db.IUserMgmtCentralMainDBConnectionFactory;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * concrete class that will be passed to AuthLibraryDBConnectionFactory in User Mgmt Embed Library
 * for getting a database connection in the Cross Linking web app
 *
 */
public class UserMgmtCentralMainDBConnectionFactory_For_Proxl implements IUserMgmtCentralMainDBConnectionFactory {

	/* (non-Javadoc)
	 * @see org.yeastrc.user_mgmt_central.main_code.db.IUserMgmtCentralMainDBConnectionFactory#getConnection()
	 */
	@Override
	public Connection getConnection() throws Exception {

		return DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
	}

}
