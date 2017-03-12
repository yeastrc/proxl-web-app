package org.yeastrc.xlink.www.database_update_with_transaction_services;

import java.sql.Connection;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.dao.FolderForProjectDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * 
 *
 */
public class UpdateProjectFolderDisplayOrderUsingDBTransactionService {

	private static final Logger log = Logger.getLogger(UpdateProjectFolderDisplayOrderUsingDBTransactionService.class);
	UpdateProjectFolderDisplayOrderUsingDBTransactionService() { }
	private static UpdateProjectFolderDisplayOrderUsingDBTransactionService _INSTANCE = new UpdateProjectFolderDisplayOrderUsingDBTransactionService();
	public static UpdateProjectFolderDisplayOrderUsingDBTransactionService getInstance() { return _INSTANCE; }
	
	/**
	 * Update the display_order based on the order of the project folder ids
	 * 
	 * @param folderIdList
	 * @throws Exception
	 */
	public void updateProjectFolderDisplayOrder( int[] folderIdList ) throws Exception {
		Connection dbConnection = null;
		try {
			dbConnection = getConnectionWithAutocommitTurnedOff();
			FolderForProjectDAO folderForProjectDAO = FolderForProjectDAO.getInstance();
			int newDisplayOrder = 0;
			for ( int folderId : folderIdList ) {
				newDisplayOrder++;  //  increment for each folder id
				folderForProjectDAO.updateDisplayOrder( folderId, newDisplayOrder, dbConnection );
			}
			dbConnection.commit();
		} catch ( Exception e ) {
			String msg = "Failed updateProjectFolderDisplayOrder(...)";
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
					String msg = "Failed dbConnection.setAutoCommit(true) in updateProjectFolderDisplayOrder(...)";
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
