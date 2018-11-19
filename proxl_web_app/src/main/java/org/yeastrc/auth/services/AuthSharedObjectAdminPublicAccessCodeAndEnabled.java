package org.yeastrc.auth.services;

import org.apache.log4j.Logger;
import org.yeastrc.auth.dao.AuthSharedObjectDAO;
import org.yeastrc.auth.dto.AuthSharedObjectDTO;
import org.yeastrc.auth.utils.GenerateRandomStringForCode;

/**
 * 
 *
 */
public class AuthSharedObjectAdminPublicAccessCodeAndEnabled {
	private static final Logger log = Logger.getLogger(AuthSharedObjectAdminPublicAccessCodeAndEnabled.class);
	
	//  private constructor
	private AuthSharedObjectAdminPublicAccessCodeAndEnabled() { }
	/**
	 * @return newly created instance
	 */
	public static AuthSharedObjectAdminPublicAccessCodeAndEnabled getInstance() { 
		return new AuthSharedObjectAdminPublicAccessCodeAndEnabled(); 
	}
	/**
	 * @param authShareableObjectId
	 * @return current or new public access code
	 * @throws IllegalArgumentException
	 * @throws Exception
	 */
	public String activatePublicAccessCode( int authShareableObjectId ) throws IllegalArgumentException, Exception {
		AuthSharedObjectDAO authSharedObjectDAO = AuthSharedObjectDAO.getInstance();
		AuthSharedObjectDTO authSharedObjectDTO = authSharedObjectDAO.getAuthSharedObjectDTOForSharedObjectId( authShareableObjectId );
		if ( authSharedObjectDTO == null ) {
			String msg = "authSharedObjectDTO not found for authShareableObjectId: " + authShareableObjectId;
			log.warn(msg);
			throw new IllegalArgumentException(msg);
		}
		String publicAccessCode = authSharedObjectDTO.getPublicAccessCode();
		if ( publicAccessCode == null || publicAccessCode.isEmpty() ) {
			publicAccessCode = GenerateRandomStringForCode.getInstance().generateRandomStringForCode();
			authSharedObjectDAO.updatePublicAccessCodeAndEnabled( authShareableObjectId, publicAccessCode, true /* publicAccessCodeEnabled */);
		} else {
			authSharedObjectDAO.updatePublicAccessCodeEnabled(authShareableObjectId, true /* publicAccessCodeEnabled */);
		}
		return publicAccessCode;
	}
	/**
	 * @param authShareableObjectId
	 * @throws IllegalArgumentException
	 * @throws Exception
	 */
	public void deactivatePublicAccessCode( int authShareableObjectId ) throws IllegalArgumentException, Exception {
		AuthSharedObjectDAO authSharedObjectDAO = AuthSharedObjectDAO.getInstance();
//		AuthSharedObjectDTO authSharedObjectDTO = authSharedObjectDAO.getAuthSharedObjectDTOForSharedObjectId( authShareableObjectId );
//		if ( authSharedObjectDTO == null ) {
//			String msg = "authSharedObjectDTO not found for authShareableObjectId: " + authShareableObjectId;
//			log.warn(msg);
//			throw new IllegalArgumentException(msg);
//		}
		authSharedObjectDAO.updatePublicAccessCodeEnabled(authShareableObjectId, false /* publicAccessCodeEnabled */);
	}
	/**
	 * @param authShareableObjectId
	 * @return new public access code
	 * @throws IllegalArgumentException
	 * @throws Exception
	 */
	public String regeneratePublicAccessCode( int authShareableObjectId ) throws IllegalArgumentException, Exception {
		AuthSharedObjectDAO authSharedObjectDAO = AuthSharedObjectDAO.getInstance();
//		AuthSharedObjectDTO authSharedObjectDTO = authSharedObjectDAO.getAuthSharedObjectDTOForSharedObjectId( authShareableObjectId );
//		if ( authSharedObjectDTO == null ) {
//			String msg = "authSharedObjectDTO not found for authShareableObjectId: " + authShareableObjectId;
//			log.warn(msg);
//			throw new IllegalArgumentException(msg);
//		}
		String publicAccessCode = GenerateRandomStringForCode.getInstance().generateRandomStringForCode();
		authSharedObjectDAO.updatePublicAccessCodeAndEnabled( authShareableObjectId, publicAccessCode, true /* publicAccessCodeEnabled */);
		return publicAccessCode;
	}
	
}
