package org.yeastrc.xlink.www.database_update_with_transaction_services;

import java.sql.Connection;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.auth.dao.AuthSharedObjectUsersDAO;
import org.yeastrc.auth.dao.AuthUserInviteTrackingDAO;
import org.yeastrc.auth.dto.AuthSharedObjectUsersDTO;
import org.yeastrc.auth.dto.AuthUserInviteTrackingDTO;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * 
 *
 */
public class AddOrUpdateProjectAccessExistingUserUsingDBTransactionService {

	private static final Logger log = LoggerFactory.getLogger( AddOrUpdateProjectAccessExistingUserUsingDBTransactionService.class);
	AddOrUpdateProjectAccessExistingUserUsingDBTransactionService() { }
	private static AddOrUpdateProjectAccessExistingUserUsingDBTransactionService _INSTANCE = new AddOrUpdateProjectAccessExistingUserUsingDBTransactionService();
	public static AddOrUpdateProjectAccessExistingUserUsingDBTransactionService getInstance() { return _INSTANCE; }
	
	/**
	 * @param authSharedObjectUsersDTO
	 * @param authUserInviteTrackingDTO
	 * @throws Exception
	 */
	public void updateUserAddAuthSharedObjectUsersDTO( AuthSharedObjectUsersDTO authSharedObjectUsersDTO,
			AuthUserInviteTrackingDTO authUserInviteTrackingDTO ) throws Exception {
		Connection dbConnection = null;
		try {
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			dbConnection.setAutoCommit(false);
			
			AuthSharedObjectUsersDAO.getInstance().save( authSharedObjectUsersDTO, dbConnection );
			int authUserInviteTrackingId = authUserInviteTrackingDTO.getId();
			int authUserIdUsingInvite = authSharedObjectUsersDTO.getUserId();
			String authUserInviteTrackingUseIP = authUserInviteTrackingDTO.getUseIP();
			AuthUserInviteTrackingDAO.getInstance().updateUsedInviteFields( authUserInviteTrackingId, authUserIdUsingInvite, authUserInviteTrackingUseIP, dbConnection );
			dbConnection.commit();
		} catch ( Exception e ) {
			String msg = "Failed updateUserAddAuthSharedObjectUsersDTO(...)";
			log.error( msg, e );
			if ( dbConnection != null ) {
				dbConnection.rollback();
			}
			throw e;
		} finally {
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
		}
	}
	
	/**
	 * @param authSharedObjectUsersDTO
	 * @param authUserInviteTrackingDTO
	 * @throws Exception
	 */
	public void updateUserUpdateUserAccessLevel( AuthSharedObjectUsersDTO authSharedObjectUsersDTO,
			AuthUserInviteTrackingDTO authUserInviteTrackingDTO ) throws Exception {
		Connection dbConnection = null;
		try {
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			dbConnection.setAutoCommit(false);

			AuthSharedObjectUsersDAO.getInstance().updateUserAccessLevel( authSharedObjectUsersDTO, dbConnection );
			int authUserInviteTrackingId = authUserInviteTrackingDTO.getId();
			int authUserIdUsingInvite = authSharedObjectUsersDTO.getUserId();
			String authUserInviteTrackingUseIP = authUserInviteTrackingDTO.getUseIP();
			AuthUserInviteTrackingDAO.getInstance().updateUsedInviteFields( authUserInviteTrackingId, authUserIdUsingInvite, authUserInviteTrackingUseIP, dbConnection );
			dbConnection.commit();
		} catch ( Exception e ) {
			String msg = "Failed updateUserAddAuthSharedObjectUsersDTO(...)";
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
					String msg = "Failed dbConnection.setAutoCommit(true) in updateUserAddAuthSharedObjectUsersDTO(...)";
					log.error( msg );
					throw new Exception(msg);
				}
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
		}
	}
	
}
