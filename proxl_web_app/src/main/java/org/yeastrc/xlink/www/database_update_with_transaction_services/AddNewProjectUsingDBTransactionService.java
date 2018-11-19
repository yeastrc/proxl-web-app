package org.yeastrc.xlink.www.database_update_with_transaction_services;

import java.sql.Connection;
import org.apache.log4j.Logger;
import org.yeastrc.auth.dao.AuthSharedObjectDAO;
import org.yeastrc.auth.dao.AuthSharedObjectUsersDAO;
import org.yeastrc.auth.dto.AuthSharedObjectDTO;
import org.yeastrc.auth.dto.AuthSharedObjectUsersDTO;
import org.yeastrc.xlink.www.constants.AuthAccessLevelConstants;
import org.yeastrc.xlink.www.dao.ProjectDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.dto.ProjectDTO;

/**
 * 
 *
 */
public class AddNewProjectUsingDBTransactionService {

	private static final Logger log = Logger.getLogger(AddNewProjectUsingDBTransactionService.class);
	AddNewProjectUsingDBTransactionService() { }
	private static AddNewProjectUsingDBTransactionService _INSTANCE = new AddNewProjectUsingDBTransactionService();
	public static AddNewProjectUsingDBTransactionService getInstance() { return _INSTANCE; }
	
	/**
	 * @param projectDTO
	 * @param projectOwnerUserId
	 * @throws Exception
	 */
	public void addNewProjectAddAuthSharedObjectDTO( ProjectDTO projectDTO, int projectOwnerUserId ) throws Exception {
		Connection dbConnection = null;
		try {
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			dbConnection.setAutoCommit(false);

			AuthSharedObjectDTO authSharedObjectDTO = new AuthSharedObjectDTO();
			AuthSharedObjectDAO.getInstance().save( authSharedObjectDTO, dbConnection );
			//  set SharedObject id here since was just set when saved authSharedObjectDTO
			projectDTO.setAuthShareableObjectId( authSharedObjectDTO.getSharedObjectId() );
			ProjectDAO.getInstance().save( projectDTO, dbConnection );
			AuthSharedObjectUsersDTO authSharedObjectUsersDTO = new AuthSharedObjectUsersDTO();
			authSharedObjectUsersDTO.setUserId( projectOwnerUserId );
			authSharedObjectUsersDTO.setSharedObjectId( authSharedObjectDTO.getSharedObjectId() );
			//  Default access level to project owner for user adding the project
			authSharedObjectUsersDTO.setAccessLevel( AuthAccessLevelConstants.ACCESS_LEVEL_PROJECT_OWNER );
			AuthSharedObjectUsersDAO.getInstance().save( authSharedObjectUsersDTO, dbConnection );
			dbConnection.commit();
		} catch ( Exception e ) {
			String msg = "Failed addNewProjectAddAuthSharedObjectDTO(...)";
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
					String msg = "Failed dbConnection.setAutoCommit(true) in addNewProjectAddAuthSharedObjectDTO(...)";
					log.error( msg );
					throw new Exception(msg);
				}
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
		}
	}
	
}
