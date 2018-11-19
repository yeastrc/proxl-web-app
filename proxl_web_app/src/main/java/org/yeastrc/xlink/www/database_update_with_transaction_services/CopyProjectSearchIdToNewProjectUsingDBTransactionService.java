package org.yeastrc.xlink.www.database_update_with_transaction_services;

import java.sql.Connection;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.dao.SearchComment_WebDAO;
import org.yeastrc.xlink.www.dao.SearchFileProjectSearch_WebDAO;
import org.yeastrc.xlink.www.dao.SearchWebLinksDAO;
import org.yeastrc.xlink.www.searcher.ProjectSearchIdAssocSearchIdInProjectIdSearcher;
import org.yeastrc.xlink.dao.ProjectSearch_Shared_DAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.ProjectSearch_Shared_DTO;

/**
 * 
 *
 */
public class CopyProjectSearchIdToNewProjectUsingDBTransactionService {

	private static final Logger log = Logger.getLogger(CopyProjectSearchIdToNewProjectUsingDBTransactionService.class);
	CopyProjectSearchIdToNewProjectUsingDBTransactionService() { }
	private static CopyProjectSearchIdToNewProjectUsingDBTransactionService _INSTANCE = new CopyProjectSearchIdToNewProjectUsingDBTransactionService();
	public static CopyProjectSearchIdToNewProjectUsingDBTransactionService getInstance() { return _INSTANCE; }
	
	/**
	 * For each entry in projectSearchIdList, if it isn't in the new project, copy it to the new project
	 * @param projectSearchIdList
	 * @param newProjectId
	 * @param copyAllSearches - if true, copy those already in new project
	 * @throws Exception
	 */
	public void copyProjectSearchIdToNewProjectId( int[] projectSearchIdList, int newProjectId, boolean copyAllSearches ) throws Exception {
		Connection dbConnection = null;
		try {
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			dbConnection.setAutoCommit(false);
			
			for ( int projectSearchId : projectSearchIdList ) {
				boolean copySearch = true;
				if ( ! copyAllSearches ) {
					// First determine if searchId for projectSearchId is already in newProjectId
					if ( ProjectSearchIdAssocSearchIdInProjectIdSearcher.getInstance()
							.isSearchIdAssocWithProjectSearchIdInProjectId(
									projectSearchId, newProjectId ) ) {
						//  already in newProjectId so do not move it there
						copySearch = false;
					}
				}
				if ( copySearch ) {
//					int insertedProjectsearchId = 
					copyProjectSearchIdToProjectId( projectSearchId, newProjectId, dbConnection );
				}
			}
			dbConnection.commit();
		} catch ( Exception e ) {
			String msg = "Failed copyProjectSearchIdToNewProjectId(...)";
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
					String msg = "Failed dbConnection.setAutoCommit(true) in copyProjectSearchIdToNewProjectId(...)";
					log.error( msg );
					throw new Exception(msg);
				}
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
		}
	}
	
	/**
	 * @param projectSearchId
	 * @param newProjectId
	 * @param dbConnection
	 * @return insertedProjectsearchId
	 * @throws Exception 
	 */
	private int copyProjectSearchIdToProjectId( int projectSearchId, int newProjectId, Connection dbConnection ) throws Exception {
		ProjectSearch_Shared_DTO projectSearch_Shared_DTO = ProjectSearch_Shared_DAO.getInstance().getFromId( projectSearchId );
		projectSearch_Shared_DTO.setProjectId(newProjectId);
		ProjectSearch_Shared_DAO.getInstance().saveToDatabase( projectSearch_Shared_DTO, dbConnection); 
		int insertedProjectsearchId = projectSearch_Shared_DTO.getId();
		SearchComment_WebDAO.getInstance()
		.duplicateRecordsForProjectSearchIdWithNewProjectSearchId( projectSearchId, insertedProjectsearchId, dbConnection );
		SearchWebLinksDAO.getInstance()
		.duplicateRecordsForProjectSearchIdWithNewProjectSearchId( projectSearchId, insertedProjectsearchId, dbConnection );
		SearchFileProjectSearch_WebDAO.getInstance()
		.duplicateRecordsForProjectSearchIdWithNewProjectSearchId( projectSearchId, insertedProjectsearchId, dbConnection );
		return insertedProjectsearchId;
	}
	
}
