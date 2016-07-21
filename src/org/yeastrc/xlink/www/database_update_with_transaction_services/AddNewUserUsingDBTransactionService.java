package org.yeastrc.xlink.www.database_update_with_transaction_services;

import java.sql.Connection;

import org.apache.log4j.Logger;
import org.yeastrc.auth.dao.AuthSharedObjectUsersDAO;
import org.yeastrc.auth.dao.AuthUserInviteTrackingDAO;
import org.yeastrc.auth.dto.AuthSharedObjectUsersDTO;
import org.yeastrc.auth.dto.AuthUserInviteTrackingDTO;
import org.yeastrc.xlink.www.dao.XLinkUserDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.dto.XLinkUserDTO;

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
	 * @param xLinkUserDTO
	 * @param hashedPassword
	 * @param authSharedObjectUsersDTO
	 * @param authUserInviteTrackingDTO
	 * @throws Exception
	 */
	public void addNewUserAddAuthSharedObjectUsersDTOForUserInvite( 
			XLinkUserDTO xLinkUserDTO, 
			String hashedPassword,
			AuthSharedObjectUsersDTO authSharedObjectUsersDTO,
			AuthUserInviteTrackingDTO authUserInviteTrackingDTO ) throws Exception {
		
		Connection dbConnection = null;
		
		try {

			dbConnection = getConnectionWithAutocommitTurnedOff();

			addNewUserInternal( xLinkUserDTO, hashedPassword, dbConnection );
			
			//  set user id here since was just set when called addNewUserInternal(...)
			authSharedObjectUsersDTO.setUserId( xLinkUserDTO.getAuthUser().getId() );

			AuthSharedObjectUsersDAO.getInstance().save( authSharedObjectUsersDTO, dbConnection );
			
			int authUserInviteTrackingId = authUserInviteTrackingDTO.getId();

			int authUserIdUsingInvite = xLinkUserDTO.getAuthUser().getId();
			
			String authUserInviteTrackingUseIP = authUserInviteTrackingDTO.getUseIP();
			
			AuthUserInviteTrackingDAO.getInstance().updateUsedInviteFields( authUserInviteTrackingId, authUserIdUsingInvite, authUserInviteTrackingUseIP, dbConnection );
			
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
	 * @param xLinkUserDTO
	 * @param hashedPassword
	 * @param authSharedObjectUsersDTO
	 * @throws Exception
	 */
	public void addNewUserAddAuthSharedObjectUsersDTO( XLinkUserDTO xLinkUserDTO, String hashedPassword, AuthSharedObjectUsersDTO authSharedObjectUsersDTO ) throws Exception {
		
		Connection dbConnection = null;
		
		try {

			dbConnection = getConnectionWithAutocommitTurnedOff();

			addNewUserInternal( xLinkUserDTO, hashedPassword, dbConnection );
			
			//  set user id here since was just set when called addNewUserInternal(...)
			authSharedObjectUsersDTO.setUserId( xLinkUserDTO.getAuthUser().getId() );

			AuthSharedObjectUsersDAO.getInstance().save( authSharedObjectUsersDTO, dbConnection );
			
			dbConnection.commit();
			
		} catch ( Exception e ) {
			
			String msg = "Failed addNewUserAddProjectAccessRecord(...)";
			
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
	 * @param xLinkUserDTO
	 * @param hashedPassword
	 * @param authUserInviteTrackingDTO
	 * @throws Exception
	 */
	public void addNewUserForUserInvite( XLinkUserDTO xLinkUserDTO,
			String hashedPassword,
			AuthUserInviteTrackingDTO authUserInviteTrackingDTO ) throws Exception {
		
		Connection dbConnection = null;
		
		try {

			dbConnection = getConnectionWithAutocommitTurnedOff();

			addNewUserInternal( xLinkUserDTO, hashedPassword, dbConnection );
			
			if ( authUserInviteTrackingDTO != null ) {

				int authUserInviteTrackingId = authUserInviteTrackingDTO.getId();

				int authUserIdUsingInvite = xLinkUserDTO.getAuthUser().getId();

				String authUserInviteTrackingUseIP = authUserInviteTrackingDTO.getUseIP();

				AuthUserInviteTrackingDAO.getInstance().updateUsedInviteFields( authUserInviteTrackingId, authUserIdUsingInvite, authUserInviteTrackingUseIP, dbConnection );
			}
			
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
	 * @param xLinkUserDTO
	 * @param hashedPassword
	 * @throws Exception
	 */
	public void addNewUser( XLinkUserDTO xLinkUserDTO, String hashedPassword ) throws Exception {
		
		Connection dbConnection = null;
		
		try {

			dbConnection = getConnectionWithAutocommitTurnedOff();

			addNewUserInternal( xLinkUserDTO, hashedPassword, dbConnection );
			
			
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
	 * @param xLinkUserDTO
	 * @param dbConnection
	 * @throws Exception
	 */
	private void addNewUserInternal( XLinkUserDTO xLinkUserDTO, String hashedPassword, Connection dbConnection ) throws Exception {

		XLinkUserDAO.getInstance().save( xLinkUserDTO, hashedPassword, dbConnection );
	}
	
	
	private Connection getConnectionWithAutocommitTurnedOff(  ) throws Exception {
		
		Connection dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
		
		dbConnection.setAutoCommit(false);
		
		return dbConnection;
	}
}
