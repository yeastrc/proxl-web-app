package org.yeastrc.xlink.www.cookie_mgmt.main;

import java.util.List;
import org.apache.commons.lang.StringUtils;
//import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.auth.dao.AuthSharedObjectDAO;
import org.yeastrc.auth.dto.AuthSharedObjectDTO;
import org.yeastrc.xlink.www.dao.ProjectDAO;
import org.yeastrc.xlink.www.dto.ProjectDTO;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
/**
 * 
 *
 */
public class PublicAccessCodeSessionManagement {
	
//	private static final Logger log = LoggerFactory.getLogger( PublicAccessCodeSessionManagement.class);
	//  private constructor
	private PublicAccessCodeSessionManagement() { }
	/**
	 * @return newly created instance
	 */
	public static PublicAccessCodeSessionManagement getInstance() { 
		return new PublicAccessCodeSessionManagement(); 
	}
	
	/**
	 * @param projectPublicAccessCodeCookieList
	 * @param userSessionObject
	 * @throws Exception
	 */
	public void addPublicAccessCodesToUserSessionObjectUsingStringFromCookie( List<String> projectPublicAccessCodeCookieList, UserSessionObject userSessionObject ) throws Exception {
		for ( String projectPublicAccessCodeInCookie : projectPublicAccessCodeCookieList ) {
			Integer projectId = getProjectIdForPublicAccessCode( projectPublicAccessCodeInCookie );
			if ( projectId != null ) {
				userSessionObject.addAllowedReadAccessProjectId( projectId );
				userSessionObject.addAllowedReadAccessProjectPublicAccessCodes( projectPublicAccessCodeInCookie );
			}
		}
	}
	
	/**
	 * @param projectPublicAccessCode
	 * @return
	 * @throws Exception
	 */
	private Integer getProjectIdForPublicAccessCode( String projectPublicAccessCode ) throws Exception {
		if ( StringUtils.isEmpty( projectPublicAccessCode ) ) {
			return null;
		}
		AuthSharedObjectDTO authSharedObjectDTO = AuthSharedObjectDAO.getInstance().getForPublicAccessCode( projectPublicAccessCode );
		if ( authSharedObjectDTO == null ) {
			return null;
		}
		if ( ! authSharedObjectDTO.isPublicAccessCodeEnabled() ) {
			return null;
		}
		ProjectDTO projectDTO = ProjectDAO.getInstance().getProjectDTOForAuthShareableObjectId( authSharedObjectDTO.getSharedObjectId() );
		if ( projectDTO == null ) {
			return null;
		}
		return projectDTO.getId();
	}
}
