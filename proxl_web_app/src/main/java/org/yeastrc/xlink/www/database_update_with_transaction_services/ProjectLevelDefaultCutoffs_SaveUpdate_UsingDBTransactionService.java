package org.yeastrc.xlink.www.database_update_with_transaction_services;

import java.sql.Connection;
import java.util.List;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.dao.ProjectLevelDefaultFltrAnnCutoffs_CutoffAsStringValue_DAO;
import org.yeastrc.xlink.www.dao.ProjectLevelDefaultFltrAnnCutoffs_DAO;
import org.yeastrc.xlink.www.dao.ProjectLevelDefaultFltr_MinPSMs_DAO;
import org.yeastrc.xlink.www.dto.ProjectLevelDefaultFltrAnnCutoffs_CutoffAsStringValue_DTO;
import org.yeastrc.xlink.www.dto.ProjectLevelDefaultFltrAnnCutoffs_DTO;
import org.yeastrc.xlink.www.dto.ProjectLevelDefaultFltr_MinPSMs_DTO;
import org.yeastrc.xlink.www.project_level_default_cutoffs.ProjectLevelDefaultCutoffsAndOthers_Cache;
/**
 * 
 * Accept new Project Level Default Cutoffs
 * 
 * Copy existing entries to 'prev' tables
 * Delete existing entries
 * Insert new entries
 * 
 */
public class ProjectLevelDefaultCutoffs_SaveUpdate_UsingDBTransactionService {

	private static final Logger log = LoggerFactory.getLogger( ProjectLevelDefaultCutoffs_SaveUpdate_UsingDBTransactionService.class);
	ProjectLevelDefaultCutoffs_SaveUpdate_UsingDBTransactionService() { }
	private static ProjectLevelDefaultCutoffs_SaveUpdate_UsingDBTransactionService _INSTANCE = new ProjectLevelDefaultCutoffs_SaveUpdate_UsingDBTransactionService();
	public static ProjectLevelDefaultCutoffs_SaveUpdate_UsingDBTransactionService getInstance() { return _INSTANCE; }
	
	/**
	 * 
	 *
	 */
	public static class Entry {
		public ProjectLevelDefaultFltrAnnCutoffs_DTO projectLevelDefaultFltrAnnCutoffs_DTO;
		public ProjectLevelDefaultFltrAnnCutoffs_CutoffAsStringValue_DTO projectLevelDefaultFltrAnnCutoffs_CutoffAsStringValue_DTO;
	}
	
	/**
	 * @param projectId
	 * @param entries
	 * @throws Exception
	 */
	public void saveUpdate( int projectId, List<Entry> entries, ProjectLevelDefaultFltr_MinPSMs_DTO projectLevelDefaultFltr_MinPSMs_DTO ) throws Exception {
		
		Connection dbConnection = null;
		try {
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			dbConnection.setAutoCommit(false);
			
			ProjectLevelDefaultFltrAnnCutoffs_DAO projectLevelDefaultFltrAnnCutoffs_DAO = ProjectLevelDefaultFltrAnnCutoffs_DAO.getInstance();
			ProjectLevelDefaultFltrAnnCutoffs_CutoffAsStringValue_DAO projectLevelDefaultFltrAnnCutoffs_CutoffAsStringValue_DAO = ProjectLevelDefaultFltrAnnCutoffs_CutoffAsStringValue_DAO.getInstance();
			
			projectLevelDefaultFltrAnnCutoffs_DAO.copyToPrevTable_ForProjectId( projectId, dbConnection );
			projectLevelDefaultFltrAnnCutoffs_CutoffAsStringValue_DAO.copyToPrevTable_ForProjectId( projectId, dbConnection );
			
			projectLevelDefaultFltrAnnCutoffs_DAO.deleteAllFor_ProjectId( projectId, dbConnection );
			
			for ( Entry entry : entries ) {

				projectLevelDefaultFltrAnnCutoffs_DAO.save( entry.projectLevelDefaultFltrAnnCutoffs_DTO, dbConnection);
				
				entry.projectLevelDefaultFltrAnnCutoffs_CutoffAsStringValue_DTO.setProjectLevelDefaultFltrAnnCutoffs_CutoffAsStringValue_Id( entry.projectLevelDefaultFltrAnnCutoffs_DTO.getId() );
				projectLevelDefaultFltrAnnCutoffs_CutoffAsStringValue_DAO.save( entry.projectLevelDefaultFltrAnnCutoffs_CutoffAsStringValue_DTO, dbConnection);
			}
			
			ProjectLevelDefaultFltr_MinPSMs_DAO.getInstance().copyToPrevTable_ForProjectId( projectId, dbConnection );
			ProjectLevelDefaultFltr_MinPSMs_DAO.getInstance().deleteAllFor_ProjectId( projectId, dbConnection );
			if ( projectLevelDefaultFltr_MinPSMs_DTO != null ) {
				ProjectLevelDefaultFltr_MinPSMs_DAO.getInstance().save( projectLevelDefaultFltr_MinPSMs_DTO, dbConnection );
			}
			
			dbConnection.commit();
			
			///  Invalidate
			
			ProjectLevelDefaultCutoffsAndOthers_Cache.getSingletonInstance().invalidateProjectId( projectId );
			
			
		} catch ( Exception e ) {
			String msg = "Failed saveUpdate(...)";
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
					String msg = "Failed dbConnection.setAutoCommit(true) in saveUpdate(...)";
					log.error( msg );
					throw new Exception(msg);
				}
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
		}
	}
	
}
