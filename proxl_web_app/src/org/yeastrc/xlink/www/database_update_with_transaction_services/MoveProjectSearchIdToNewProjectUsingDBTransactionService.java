package org.yeastrc.xlink.www.database_update_with_transaction_services;

import java.sql.Connection;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.dao.FolderProjectSearchDAO;
import org.yeastrc.xlink.www.dao.ProjectSearchDAO;
import org.yeastrc.xlink.www.searcher.ProjectSearchIdAssocSearchIdInProjectIdSearcher;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_Search_Core_DTO;
/**
 * 
 *
 */
public class MoveProjectSearchIdToNewProjectUsingDBTransactionService {

	private static final Logger log = Logger.getLogger(MoveProjectSearchIdToNewProjectUsingDBTransactionService.class);
	MoveProjectSearchIdToNewProjectUsingDBTransactionService() { }
	private static MoveProjectSearchIdToNewProjectUsingDBTransactionService _INSTANCE = new MoveProjectSearchIdToNewProjectUsingDBTransactionService();
	public static MoveProjectSearchIdToNewProjectUsingDBTransactionService getInstance() { return _INSTANCE; }
	
	private static final int DISPLAY_ORDER_NO_ORDER = 0;
	
	/**
	 * For each entry in projectSearchIdList, if it isn't in the new project, move it to the new project
	 * Set search_display_order to 0 to reset it.
	 * @param projectSearchIdList
	 * @param newProjectId
	 * @param moveAllSearches - if true, move those already in new project
	 * @throws Exception
	 */
	public void moveProjectSearchIdToNewProjectId( int[] projectSearchIdList, int newProjectId, boolean moveAllSearches ) throws Exception {
		Connection dbConnection = null;
		try {
			dbConnection = getConnectionWithAutocommitTurnedOff();
			for ( int projectSearchId : projectSearchIdList ) {
				boolean moveSearch = true;
				if ( ! moveAllSearches ) {
					// First determine if searchId for projectSearchId is already in newProjectId
					if ( ProjectSearchIdAssocSearchIdInProjectIdSearcher.getInstance()
							.isSearchIdAssocWithProjectSearchIdInProjectId(
									projectSearchId, newProjectId ) ) {
						//  already in newProjectId so do not move it there
						moveSearch = false;
					}
				}
				if ( moveSearch ) {
					ProjectSearchDAO.getInstance().updateProjectIdForProjectSearch( projectSearchId, newProjectId, dbConnection );
					// Clear display order so moved searches (project_search records) appear at the top or according to project_search.id order
					ProjectSearchDAO.getInstance().updateDisplayOrderForProjectSearch( projectSearchId, DISPLAY_ORDER_NO_ORDER, dbConnection);
					//  Remove folder mapping record related to old project, if it exists
					FolderProjectSearchDAO.getInstance().delete( projectSearchId );
				}

				Cached_Search_Core_DTO.getInstance().invalidateProjectSearchId( projectSearchId );
			}
			dbConnection.commit();
		} catch ( Exception e ) {
			String msg = "Failed moveProjectSearchIdToNewProjectId(...)";
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
					String msg = "Failed dbConnection.setAutoCommit(true) in moveProjectSearchIdToNewProjectId(...)";
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
