package org.yeastrc.xlink.www.database_update_with_transaction_services;

import java.sql.Connection;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.SearchDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;


/**
 * 
 *
 */
public class UpdateSearchProjectIdUsingDBTransactionService {

	private static final Logger log = Logger.getLogger(UpdateSearchProjectIdUsingDBTransactionService.class);
	
	UpdateSearchProjectIdUsingDBTransactionService() { }
	private static UpdateSearchProjectIdUsingDBTransactionService _INSTANCE = new UpdateSearchProjectIdUsingDBTransactionService();
	public static UpdateSearchProjectIdUsingDBTransactionService getInstance() { return _INSTANCE; }
	
	/**
	 * Update the project_id associated with this search
	 * @param search
	 * @param newProjectId
	 * @throws Exception
	 */
	public void updateProjectIdForExistingProjectId( int[] searchIdList, int newProjectId ) throws Exception {
		

		Connection dbConnection = null;
		
		try {

			dbConnection = getConnectionWithAutocommitTurnedOff();
			
			SearchDAO searchDAO = SearchDAO.getInstance();
			
			for ( int searchId : searchIdList ) {
				
				searchDAO.updateProjectIdForSearch( searchId, newProjectId, dbConnection );
			}
			

			dbConnection.commit();
			
		} catch ( Exception e ) {
			
			String msg = "Failed updateProjectIdForSearch(...)";
			
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
					String msg = "Failed dbConnection.setAutoCommit(true) in updateProjectIdForSearch(...)";

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
