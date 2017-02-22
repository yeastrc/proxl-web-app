package org.yeastrc.xlink.www.internal_services;

import org.apache.log4j.Logger;
import org.yeastrc.auth.exceptions.AuthSharedObjectRecordNotFoundException;
import org.yeastrc.auth.services.GetAuthLevel;
import org.yeastrc.xlink.www.constants.AuthAccessLevelConstants;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.dao.ProjectDAO;
import org.yeastrc.xlink.www.dto.XLinkUserDTO;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;

/**
 * 
 *
 */
public class GetAuthLevelFromXLinkData {


	private static final Logger log = Logger.getLogger(GetAuthLevelFromXLinkData.class);
	GetAuthLevelFromXLinkData() { }
	private static GetAuthLevelFromXLinkData _INSTANCE = new GetAuthLevelFromXLinkData();
	public static GetAuthLevelFromXLinkData getInstance() { return _INSTANCE; }
	
	

	/**
	 * @param xLinkUserDBObject
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public AuthAccessLevel  getAuthLevelForAuthUserIdProjectId( XLinkUserDTO xLinkUserDBObject, int projectId ) throws Exception {

		Integer authShareableObjectId = ProjectDAO.getInstance().getAuthShareableObjectIdForProjectId( projectId );
		
		
		if ( authShareableObjectId == null ) {
		

			if ( log.isInfoEnabled() ) {

				String msg = "missing data, no authShareableObjectId found for projectId: " + projectId;

				log.info( msg );
				
//				throw new AuthSharedObjectRecordNotFoundException( msg );
			}
			
			
			AuthAccessLevel authAccessLevel = new AuthAccessLevel( AuthAccessLevelConstants.ACCESS_LEVEL_DEFAULT_FOR_NO_AUTH_SHARED_OBJECT_RECORD );
			return authAccessLevel;
		}
		
		return getAuthLevelForAuthUserIdShareableObjectId( xLinkUserDBObject, authShareableObjectId );
	}
	
	/**
	 * @param xLinkUserDBObject
	 * @param shareableObjectId
	 * @return
	 * @throws Exception
	 */
	public AuthAccessLevel  getAuthLevelForAuthUserIdShareableObjectId( XLinkUserDTO xLinkUserDBObject, Integer authShareableObjectId ) throws Exception {
		
		try {
			
			int authLevel = GetAuthLevel.getInstance().getAuthLevelForSharableObject( xLinkUserDBObject.getAuthUser().getId(), authShareableObjectId );
			
			AuthAccessLevel authAccessLevel = new AuthAccessLevel( authLevel );
			return authAccessLevel;
			
		} catch ( AuthSharedObjectRecordNotFoundException e ) {
			
			AuthAccessLevel authAccessLevel = new AuthAccessLevel( AuthAccessLevelConstants.ACCESS_LEVEL_DEFAULT_FOR_NO_AUTH_SHARED_OBJECT_RECORD );
			return authAccessLevel;
		}
		
	}
	
	
	//  Removed since this method no longer exists:  SearchDAO.getInstance().getSearchProjectId( searchId );
//	/**
//	 * @param xLinkUserDBObject
//	 * @param searchId
//	 * @return
//	 * @throws Exception
//	 */
//	public AuthAccessLevel  getAuthLevelForAuthUserIdSearchId( XLinkUserDTO xLinkUserDBObject, int searchId ) throws Exception {
//		
//		Integer projectId = SearchDAO.getInstance().getSearchProjectId( searchId );
//		
//		if ( projectId == null ) {
//			
//			String msg = "Failed to get project id for search id: " + searchId;
//			
//			log.error( msg );
//			
//			throw new Exception( msg );
//		}
//		
//		return getAuthLevelForAuthUserIdProjectId( xLinkUserDBObject, projectId );
//	}
		
	
}
