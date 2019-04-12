/*
* Original author: Daniel Jaschob <djaschob .at. uw.edu>
*                  
* Copyright 2018 University of Washington - Seattle, WA
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.yeastrc.xlink.www.access_control.access_control_main;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.yeastrc.auth.dao.AuthSharedObjectUsersDAO;
import org.yeastrc.auth.exceptions.AuthSharedObjectRecordNotFoundException;
import org.yeastrc.xlink.www.access_control.common.AccessControl_GetUserSession_RefreshAccessEnabled;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevelBuilder;
import org.yeastrc.xlink.www.constants.AuthAccessLevelConstants;
import org.yeastrc.xlink.www.dao.ProjectDAO;
import org.yeastrc.xlink.www.dto.ProjectDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.user_session_management.UserSession;

/**
 * Get User (Or Public User) Access Level for Project Ids
 *
 */
public class GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId {

	private static final Logger log = LoggerFactory.getLogger( GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.class );
	

	private GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId() { }
	private static final GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId _INSTANCE = new GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId();
	public static GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId getSinglesonInstance() { return _INSTANCE; }
	
	/**
	 * 
	 *
	 */
	public static class GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result {
		
		private WebSessionAuthAccessLevel webSessionAuthAccessLevel;
		
		private boolean noSession;
		private UserSession userSession;
		
		private boolean projectNotEnabledOrIsMarkedForDeletion;
		
		@Override
		public String toString() {
			return "GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result [webSessionAuthAccessLevel="
					+ webSessionAuthAccessLevel + ", noSession=" + noSession + ", userSession=" + userSession
					+ ", projectNotEnabledOrIsMarkedForDeletion=" + projectNotEnabledOrIsMarkedForDeletion + "]";
		}

		public WebSessionAuthAccessLevel getWebSessionAuthAccessLevel() {
			return webSessionAuthAccessLevel;
		}
		public void setWebSessionAuthAccessLevel(WebSessionAuthAccessLevel webSessionAuthAccessLevel) {
			this.webSessionAuthAccessLevel = webSessionAuthAccessLevel;
		}
		public boolean isNoSession() {
			return noSession;
		}
		public void setNoSession(boolean noSession) {
			this.noSession = noSession;
		}
		public UserSession getUserSession() {
			return userSession;
		}
		public void setUserSession(UserSession userSession) {
			this.userSession = userSession;
		}
		public boolean isProjectNotEnabledOrIsMarkedForDeletion() {
			return projectNotEnabledOrIsMarkedForDeletion;
		}
		public void setProjectNotEnabledOrIsMarkedForDeletion(boolean projectNotEnabledOrIsMarkedForDeletion) {
			this.projectNotEnabledOrIsMarkedForDeletion = projectNotEnabledOrIsMarkedForDeletion;
		}
	}

	/**
	 * @param httpServletRequest
	 * @return
	 * @throws Exception
	 */
	public GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result getAccessAndSetupWebSessionNoProjectId(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse // NOT USED  
			) throws Exception {

		return getAccessAndSetupWebSessionNoProjectId( httpServletRequest );
	}
	
	/**
	 * @param httpServletRequest
	 * @return
	 * @throws Exception
	 */
	public GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result getAccessAndSetupWebSessionNoProjectId( HttpServletRequest httpServletRequest ) throws Exception {

		UserSession userSession =
				AccessControl_GetUserSession_RefreshAccessEnabled.getSinglesonInstance()
				.getUserSession_RefreshAccessEnabled( httpServletRequest );
		
		if ( userSession == null ) {
			//  No user logged in 

			WebSessionAuthAccessLevel webSessionAuthAccessLevel = 
					WebSessionAuthAccessLevelBuilder.getBuilder()
					.set_authAccessLevel( AuthAccessLevelConstants.ACCESS_LEVEL_NONE )
					.set_authAaccessLevelForProjectIdsIfNotLocked( AuthAccessLevelConstants.ACCESS_LEVEL_NONE )
					.build();

			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result result = new GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result();
			result.webSessionAuthAccessLevel = webSessionAuthAccessLevel;
			
			result.noSession = true;
			
			return result;  //  EARLY EXIT
		}

		////////////////////////////
		
		//  Signed in user or at least a user session

		//  Start at no access level
		int authAccessLevel = AuthAccessLevelConstants.ACCESS_LEVEL_NONE;
		
		//  Start at no access level
		int authAccessLevelForProjectIdsIfNotLocked = AuthAccessLevelConstants.ACCESS_LEVEL_NONE;
		
		if ( userSession.getUserAccessLevel() != null ) {
			
			//  User is admin
			
			authAccessLevel = userSession.getUserAccessLevel();
			authAccessLevelForProjectIdsIfNotLocked = userSession.getUserAccessLevel();
		}

		WebSessionAuthAccessLevel webSessionAuthAccessLevel = 
				WebSessionAuthAccessLevelBuilder.getBuilder()
				.set_authAccessLevel( authAccessLevel )
				.set_authAaccessLevelForProjectIdsIfNotLocked( authAccessLevelForProjectIdsIfNotLocked )
				.build();

		GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result result = new GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result();
		result.webSessionAuthAccessLevel = webSessionAuthAccessLevel;
		result.userSession = userSession;
		
		return result;
	}

	/**
	 * @param projectId
	 * @param httpServletRequest
	 * @param httpServletResponse - NOT USED
	 * @return
	 * @throws Exception
	 */
	public GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result getAccessAndSetupWebSessionWithProjectId( 
			int projectId, 
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse // NOT USED 
			) throws Exception {
		
		return getAccessAndSetupWebSessionWithProjectId( projectId, httpServletRequest );
	}
	
	/**
	 * @param projectId
	 * @param httpServletRequest
	 * @param httpServletResponse - NOT USED
	 * @return
	 * @throws Exception
	 */
	public GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result getAccessAndSetupWebSessionWithProjectId( 
			int projectId, 
			HttpServletRequest httpServletRequest ) throws Exception {
		
		ProjectDTO projectOnlyProjectLockedPublicAccessLevel = ProjectDAO.getInstance().getProjectLockedPublicAccessLevelPublicAccessLockedForProjectId( projectId );
		
		if ( projectOnlyProjectLockedPublicAccessLevel == null ) {
			throw new ProxlWebappDataException( "Project Id not found" );
		}
		
		if ( ( ! projectOnlyProjectLockedPublicAccessLevel.isEnabled() )
				|| projectOnlyProjectLockedPublicAccessLevel.isMarkedForDeletion() ) {
			
			//  Override auth level to none if project is not enabled or marked for deletion.
			
			WebSessionAuthAccessLevel webSessionAuthAccessLevel =
					WebSessionAuthAccessLevelBuilder
					.getBuilder()
					.set_authAccessLevel( AuthAccessLevelConstants.ACCESS_LEVEL_NONE )
					.set_authAaccessLevelForProjectIdsIfNotLocked( AuthAccessLevelConstants.ACCESS_LEVEL_NONE )
					.build();
			
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result result = new GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result();
			result.webSessionAuthAccessLevel = webSessionAuthAccessLevel;
			result.projectNotEnabledOrIsMarkedForDeletion = true;
			
			{
				UserSession userSession =
						AccessControl_GetUserSession_RefreshAccessEnabled.getSinglesonInstance()
						.getUserSession_RefreshAccessEnabled( httpServletRequest );
				if ( userSession == null ) {
					result.noSession = true;
				}
			}
			return result;  //  EARLY EXIT
		}
		
		UserSession userSession =
				AccessControl_GetUserSession_RefreshAccessEnabled.getSinglesonInstance()
				.getUserSession_RefreshAccessEnabled( httpServletRequest );
		
		if ( userSession == null || userSession.getAuthUserId() == null ) {
			//  No user logged in 
			
			if ( userSession != null ) {
				//  Check session for is allowed read access to this project id
				//           (From "public access code", "projectReadProcessCode.do?code=")
				if ( userSession.getAllowedReadAccessProjectIds() != null ) {
					if ( userSession.getAllowedReadAccessProjectIds().contains( projectId ) ) {
						//  Project Id found
						WebSessionAuthAccessLevel webSessionAuthAccessLevel = 
								WebSessionAuthAccessLevelBuilder.getBuilder()
								.set_authAccessLevel( AuthAccessLevelConstants.ACCESS_LEVEL__PUBLIC_ACCESS_CODE_READ_ONLY__PUBLIC_PROJECT_READ_ONLY )
								.set_authAaccessLevelForProjectIdsIfNotLocked( AuthAccessLevelConstants.ACCESS_LEVEL__PUBLIC_ACCESS_CODE_READ_ONLY__PUBLIC_PROJECT_READ_ONLY )
								.build();

						GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result result = new GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result();
						result.webSessionAuthAccessLevel = webSessionAuthAccessLevel;
						result.noSession = false;
						
						return result;  //  EARLY EXIT
					}
				}
			}
			
			
			//  No user logged in so all projects must be public projects

			Integer publicAccessLevel = projectOnlyProjectLockedPublicAccessLevel.getPublicAccessLevel();
			
			if ( publicAccessLevel != null && publicAccessLevel != AuthAccessLevelConstants.ACCESS_LEVEL_NONE ) {
				
				WebSessionAuthAccessLevel webSessionAuthAccessLevel = 
						WebSessionAuthAccessLevelBuilder.getBuilder()
						.set_authAccessLevel( publicAccessLevel )
						.set_authAaccessLevelForProjectIdsIfNotLocked( publicAccessLevel )
						.build();

				GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result result = new GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result();
				result.webSessionAuthAccessLevel = webSessionAuthAccessLevel;
				
				result.noSession = true;
				
				return result;  //  EARLY EXIT
			}
			
			//  No User session and not public project

			WebSessionAuthAccessLevel webSessionAuthAccessLevel = 
					WebSessionAuthAccessLevelBuilder.getBuilder()
					.set_authAccessLevel( AuthAccessLevelConstants.ACCESS_LEVEL_NONE )
					.set_authAaccessLevelForProjectIdsIfNotLocked( AuthAccessLevelConstants.ACCESS_LEVEL_NONE )
					.build();

			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result result = new GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result();
			result.webSessionAuthAccessLevel = webSessionAuthAccessLevel;
			result.noSession = true;
			
			return result;  //  EARLY EXIT
		}
		
		////////////////////////////
		
		//  Signed in user or at least a user session

		//  Start at no access level
		int authAccessLevel = AuthAccessLevelConstants.ACCESS_LEVEL_NONE;
		
		//  Start at no access level
		int authAccessLevelForProjectIdsIfNotLocked = AuthAccessLevelConstants.ACCESS_LEVEL_NONE;
		
		if ( userSession.getUserAccessLevel() != null 
				&& userSession.getUserAccessLevel() == AuthAccessLevelConstants.ACCESS_LEVEL_ADMIN ) {
			
			//  User is admin
			
			authAccessLevel = AuthAccessLevelConstants.ACCESS_LEVEL_ADMIN;
			authAccessLevelForProjectIdsIfNotLocked = AuthAccessLevelConstants.ACCESS_LEVEL_ADMIN;
		} else {
			int authUserId = userSession.getAuthUserId();
			
			Integer authShareableObjectId = ProjectDAO.getInstance().getAuthShareableObjectIdForProjectId( projectId );
			if ( authShareableObjectId == null ) {
				if ( log.isInfoEnabled() ) {
					String msg = "missing data, no authShareableObjectId found for projectId: " + projectId;
					log.info( msg );
//					throw new AuthSharedObjectRecordNotFoundException( msg );
				}
				authAccessLevel = AuthAccessLevelConstants.ACCESS_LEVEL_DEFAULT_FOR_NO_AUTH_SHARED_OBJECT_RECORD;
			} else {
				Integer authLevel = this.getAuthLevelForSharableObject( authUserId, authShareableObjectId );
				if( authLevel != null ) {	
					authAccessLevel = authLevel;
				} else {
					authAccessLevel = AuthAccessLevelConstants.ACCESS_LEVEL_DEFAULT_FOR_NO_AUTH_SHARED_OBJECT_RECORD;
				}
			}
		}

		if ( projectOnlyProjectLockedPublicAccessLevel.isProjectLocked() && authAccessLevel != AuthAccessLevelConstants.ACCESS_LEVEL_NONE ) {
			
			//  Override auth level to read only if project is locked.
			
			int authAccessLevelIfNotLocked_Local = authAccessLevel;
			authAccessLevel = AuthAccessLevelConstants.ACCESS_LEVEL__PUBLIC_ACCESS_CODE_READ_ONLY__PUBLIC_PROJECT_READ_ONLY;
			authAccessLevelForProjectIdsIfNotLocked = authAccessLevelIfNotLocked_Local;
		}
		
		WebSessionAuthAccessLevel webSessionAuthAccessLevel = 
				WebSessionAuthAccessLevelBuilder.getBuilder()
				.set_authAccessLevel( authAccessLevel )
				.set_authAaccessLevelForProjectIdsIfNotLocked( authAccessLevelForProjectIdsIfNotLocked )
				.build();
		
		GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result result = new GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result();
		result.userSession = userSession;
		result.webSessionAuthAccessLevel = webSessionAuthAccessLevel;
		
		return result; 
	}
	
	/**
	 * @param authUserId
	 * @param authShareableObjectId
	 * @return
	 * @throws AuthSharedObjectRecordNotFoundException
	 * @throws Exception 
	 */
	public Integer getAuthLevelForSharableObject( int authUserId, int authShareableObjectId  ) throws AuthSharedObjectRecordNotFoundException, Exception {
		
		Integer authAccessLevel = 
				AuthSharedObjectUsersDAO.getInstance().getAccessLevelForSharedObjectIdAndUserId( authShareableObjectId, authUserId );
		
		if ( authAccessLevel == null ) {
			if ( log.isInfoEnabled() ) {
				String msg = "no authShareableObject found for authUserId: " + authUserId + ", authShareableObjectId: " + authShareableObjectId;
				log.info( msg );
			}
			return null;
		}
		return authAccessLevel;
	}
}

