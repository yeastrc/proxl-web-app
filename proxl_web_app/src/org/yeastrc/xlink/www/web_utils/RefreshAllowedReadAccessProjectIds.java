package org.yeastrc.xlink.www.web_utils;

import java.util.Set;
import org.yeastrc.auth.dao.AuthSharedObjectDAO;
import org.yeastrc.auth.dto.AuthSharedObjectDTO;
import org.yeastrc.xlink.www.dao.ProjectDAO;
import org.yeastrc.xlink.www.dto.ProjectDTO;
import org.yeastrc.xlink.www.user_account.UserSessionObject;

public class RefreshAllowedReadAccessProjectIds {
	/**
	 * @param userSessionObject
	 * @throws Exception 
	 */
	public static void refreshAllowedReadAccessProjectIds( UserSessionObject userSessionObject ) throws Exception {
		if ( userSessionObject == null ) {
			return;
		}
		Set<Integer> allowedReadAccessProjectIds = userSessionObject.getAllowedReadAccessProjectIds();
		Set<String> allowedReadAccessProjectPublicAccessCodes = userSessionObject.getAllowedReadAccessProjectPublicAccessCodes();
		if ( allowedReadAccessProjectIds != null ) {
			allowedReadAccessProjectIds.clear();
			if ( allowedReadAccessProjectPublicAccessCodes != null ) {
				for ( String publicAccessCode : allowedReadAccessProjectPublicAccessCodes ) {
					AuthSharedObjectDTO authSharedObjectDTO = AuthSharedObjectDAO.getInstance().getForPublicAccessCode( publicAccessCode );
					if ( authSharedObjectDTO != null && authSharedObjectDTO.isPublicAccessCodeEnabled() ) {
						ProjectDTO projectDTO = ProjectDAO.getInstance().getProjectDTOForAuthShareableObjectId( authSharedObjectDTO.getSharedObjectId() );
						if ( projectDTO != null ) {
							userSessionObject.addAllowedReadAccessProjectId( projectDTO.getId() );
						}
					}
				}
			}
		}	
	}
}
