package org.yeastrc.xlink.www.database_update_with_transaction_services;

import java.sql.Connection;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.dao.FolderForProjectDAO;
import org.yeastrc.xlink.www.dao.ProjectSearchDAO;

/**
 * Delete a folder and reset the display order to zero of all the project_search records in it
 *
 */
public class DeleteFolderResetSearchDisplayOrderUsingDBTransactionService {

	private static final Logger log = LoggerFactory.getLogger( DeleteFolderResetSearchDisplayOrderUsingDBTransactionService.class);
	DeleteFolderResetSearchDisplayOrderUsingDBTransactionService() { }
	private static DeleteFolderResetSearchDisplayOrderUsingDBTransactionService _INSTANCE = new DeleteFolderResetSearchDisplayOrderUsingDBTransactionService();
	public static DeleteFolderResetSearchDisplayOrderUsingDBTransactionService getInstance() { return _INSTANCE; }
	
	/**
	 * Delete a folder and reset the display order to zero of all the project_search records in it  
	 * @param folderId
	 * @throws Exception
	 */
	public void deleteFolderResetSearchDisplayOrder( int folderId ) throws Exception {
		Connection dbConnection = null;
		try {
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			dbConnection.setAutoCommit(false);
			
			ProjectSearchDAO.getInstance().resetDisplayOrderForFolderId( folderId, dbConnection );
			FolderForProjectDAO.getInstance().delete( folderId, dbConnection );
			dbConnection.commit();
		} catch ( Exception e ) {
			String msg = "Failed deleteFolderResetSearchDisplayOrder(...)";
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
					String msg = "Failed dbConnection.setAutoCommit(true) in deleteFolderResetSearchDisplayOrder(...)";
					log.error( msg );
					throw new Exception(msg);
				}
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
		}
	}
	
}
