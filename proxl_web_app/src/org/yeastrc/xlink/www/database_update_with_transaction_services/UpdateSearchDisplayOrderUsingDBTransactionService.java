package org.yeastrc.xlink.www.database_update_with_transaction_services;

import java.sql.Connection;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.dao.ProjectSearchDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * 
 *
 */
public class UpdateSearchDisplayOrderUsingDBTransactionService {

	private static final Logger log = Logger.getLogger(UpdateSearchDisplayOrderUsingDBTransactionService.class);
	UpdateSearchDisplayOrderUsingDBTransactionService() { }
	private static UpdateSearchDisplayOrderUsingDBTransactionService _INSTANCE = new UpdateSearchDisplayOrderUsingDBTransactionService();
	public static UpdateSearchDisplayOrderUsingDBTransactionService getInstance() { return _INSTANCE; }
	
	/**
	 * Update the display_order based on the order of the search ids
	 * 
	 * @param searchIdList
	 * @throws Exception
	 */
	public void updateSearchDisplayOrder( int[] searchIdList ) throws Exception {
		Connection dbConnection = null;
		try {
			dbConnection = getConnectionWithAutocommitTurnedOff();
			ProjectSearchDAO projectSearchDAO = ProjectSearchDAO.getInstance();
			int newDisplayOrder = 0;
			for ( int searchId : searchIdList ) {
				newDisplayOrder++;  //  increment for each search id
				projectSearchDAO.updateDisplayOrderForProjectSearch( searchId, newDisplayOrder, dbConnection );
			}
			dbConnection.commit();
		} catch ( Exception e ) {
			String msg = "Failed updateSearchDisplayOrder(...)";
			log.error( msg, e );
			if ( dbConnection != null ) {
				dbConnection.rollback();
			}
			throw e;
		} finally {
			if( dbConnection != null ) {
				try {
					dbConnection.setAutoCommit(true);  /// reset for next user of connection
				} catch (Exception ex) {
					String msg = "Failed dbConnection.setAutoCommit(true) in updateSearchDisplayOrder(...)";
					log.error( msg );
					throw new Exception(msg);
				}
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
		}
	}
	
	/**
	 * @return
	 * @throws Exception
	 */
	private Connection getConnectionWithAutocommitTurnedOff(  ) throws Exception {
		Connection dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
		dbConnection.setAutoCommit(false);
		return dbConnection;
	}
}
