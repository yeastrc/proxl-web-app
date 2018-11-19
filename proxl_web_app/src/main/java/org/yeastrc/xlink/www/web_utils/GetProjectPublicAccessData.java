package org.yeastrc.xlink.www.web_utils;

import org.apache.log4j.Logger;
import org.yeastrc.auth.dao.AuthSharedObjectDAO;
import org.yeastrc.auth.dto.AuthSharedObjectDTO;
import org.yeastrc.xlink.www.dao.ProjectDAO;
import org.yeastrc.xlink.www.dto.ProjectDTO;
import org.yeastrc.xlink.www.objects.ProjectPublicAccessData;
/**
 * 
 *
 */
public class GetProjectPublicAccessData {
	
	private static final Logger log = Logger.getLogger(GetProjectPublicAccessData.class);
	//  private constructor
	private GetProjectPublicAccessData() { }
	/**
	 * @return newly created instance
	 */
	public static GetProjectPublicAccessData getInstance() { 
		return new GetProjectPublicAccessData(); 
	}
	
	/**
	 * 
	 * @param projectId
	 * @return - return null if project or auth shared object is not found
	 * @throws Exception
	 */
	public ProjectPublicAccessData getProjectPublicAccessData( int projectId ) throws Exception {
		ProjectPublicAccessData projectPublicAccessData = new ProjectPublicAccessData();
		ProjectDTO projectDTO =
				ProjectDAO.getInstance().getProjectLockedPublicAccessLevelPublicAccessLockedForProjectId( projectId );
		if ( projectDTO == null ) {
			String msg = "Project not found for project id: " + projectId + ".  Returning null."; 
			log.error( msg );
			return null;
		}
		AuthSharedObjectDTO authSharedObjectDTO = 
				AuthSharedObjectDAO.getInstance().getAuthSharedObjectDTOForSharedObjectId( projectDTO.getAuthShareableObjectId() );
		if ( authSharedObjectDTO == null ) {
			String msg = "authSharedObjectDTO not found for AuthShareableObjectId: " 
					+ projectDTO.getAuthShareableObjectId()
					+ ", project id: " + projectId + ".  Returning null."; 
			log.error( msg );
			return null;
		}
		projectPublicAccessData.setPublicAccessCode( authSharedObjectDTO.getPublicAccessCode() );
		projectPublicAccessData.setPublicAccessCodeEnabled( authSharedObjectDTO.isPublicAccessCodeEnabled() );
		projectPublicAccessData.setPublicAccessLevel( projectDTO.getPublicAccessLevel() );
		projectPublicAccessData.setPublicAccessLocked( projectDTO.isPublicAccessLocked() );
		return projectPublicAccessData;
	}
}
