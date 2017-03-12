package org.yeastrc.xlink.www.database_update_with_transaction_services;

import java.sql.Connection;
import org.apache.log4j.Logger;
import org.yeastrc.auth.dao.AuthSharedObjectUsersDAO;
import org.yeastrc.auth.dao.AuthUserDAO;
import org.yeastrc.auth.dao.AuthUserInviteTrackingDAO;
import org.yeastrc.auth.dto.AuthSharedObjectUsersDTO;
import org.yeastrc.auth.dto.AuthUserDTO;
import org.yeastrc.auth.dto.AuthUserInviteTrackingDTO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.dao.ZzUserDataMirrorDAO;
import org.yeastrc.xlink.www.dto.ZzUserDataMirrorDTO;

/**
 * 
 *
 */
public class AddNewUserUsingDBTransactionService {
	
	private static final Logger log = Logger.getLogger(AddNewUserUsingDBTransactionService.class);
	AddNewUserUsingDBTransactionService() { }
	private static AddNewUserUsingDBTransactionService _INSTANCE = new AddNewUserUsingDBTransactionService();
	public static AddNewUserUsingDBTransactionService getInstance() { return _INSTANCE; }
	
	/**
	 * @param authUserDTO
	 * @param zzUserDataMirrorDTO
	 * @param authSharedObjectUsersDTO
	 * @param authUserInviteTrackingDTO
	 * @throws Exception
	 */
	public void addNewUserAddAuthSharedObjectUsersDTOForUserInvite( 
			AuthUserDTO authUserDTO, 
			ZzUserDataMirrorDTO zzUserDataMirrorDTO,
			AuthSharedObjectUsersDTO authSharedObjectUsersDTO,
			AuthUserInviteTrackingDTO authUserInviteTrackingDTO ) throws Exception {
		Connection dbConnection = null;
		try {
			dbConnection = getConnectionWithAutocommitTurnedOff();
			AuthUserDAO.getInstance().save( authUserDTO, dbConnection );
			//  set user id here since was just set when called addNewUserInternal(...)
			authSharedObjectUsersDTO.setUserId( authUserDTO.getId() );
			AuthSharedObjectUsersDAO.getInstance().save( authSharedObjectUsersDTO, dbConnection );
			int authUserInviteTrackingId = authUserInviteTrackingDTO.getId();
			int authUserIdUsingInvite = authUserDTO.getId();
			String authUserInviteTrackingUseIP = authUserInviteTrackingDTO.getUseIP();
			AuthUserInviteTrackingDAO.getInstance().updateUsedInviteFields( authUserInviteTrackingId, authUserIdUsingInvite, authUserInviteTrackingUseIP, dbConnection );
			zzUserDataMirrorDTO.setAuthUserId( authUserDTO.getId() );
			ZzUserDataMirrorDAO.getInstance().save( zzUserDataMirrorDTO, dbConnection );
			dbConnection.commit();
		} catch ( Exception e ) {
			String msg = "Failed addNewUserAddAuthSharedObjectUsersDTOForUserInvite(...)";
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
	 * @param authUserDTO
	 * @param zzUserDataMirrorDTO
	 * @param authUserInviteTrackingDTO
	 * @throws Exception
	 */
	public void addNewUserForUserInvite( AuthUserDTO authUserDTO,
			ZzUserDataMirrorDTO zzUserDataMirrorDTO,
			AuthUserInviteTrackingDTO authUserInviteTrackingDTO ) throws Exception {
		Connection dbConnection = null;
		try {
			dbConnection = getConnectionWithAutocommitTurnedOff();
			AuthUserDAO.getInstance().save( authUserDTO, dbConnection );
			if ( authUserInviteTrackingDTO != null ) {
				int authUserInviteTrackingId = authUserInviteTrackingDTO.getId();
				int authUserIdUsingInvite = authUserDTO.getId();
				String authUserInviteTrackingUseIP = authUserInviteTrackingDTO.getUseIP();
				AuthUserInviteTrackingDAO.getInstance().updateUsedInviteFields( authUserInviteTrackingId, authUserIdUsingInvite, authUserInviteTrackingUseIP, dbConnection );
			}
			zzUserDataMirrorDTO.setAuthUserId( authUserDTO.getId() );
			ZzUserDataMirrorDAO.getInstance().save( zzUserDataMirrorDTO, dbConnection );
			dbConnection.commit();
		} catch ( Exception e ) {
			String msg = "Failed addNewUserForUserInvite(...)";
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
	 * @param authUserDTO
	 * @throws Exception
	 */
	public void addNewUser( AuthUserDTO authUserDTO, ZzUserDataMirrorDTO zzUserDataMirrorDTO ) throws Exception {
		Connection dbConnection = null;
		try {
			dbConnection = getConnectionWithAutocommitTurnedOff();
			AuthUserDAO.getInstance().save( authUserDTO, dbConnection );
			zzUserDataMirrorDTO.setAuthUserId( authUserDTO.getId() );
			ZzUserDataMirrorDAO.getInstance().save( zzUserDataMirrorDTO, dbConnection );
			dbConnection.commit();
		} catch ( Exception e ) {
			String msg = "Failed addNewUser(...)";
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
					String msg = "Failed dbConnection.setAutoCommit(true) in addNewUser(...)";
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
