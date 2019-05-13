package org.yeastrc.xlink.www.webservices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.dao.PDBFileDAO;
import org.yeastrc.xlink.www.dto.PDBFileDTO;
import org.yeastrc.xlink.www.constants.PDBFileConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.objects.WWWPDBFile;
import org.yeastrc.xlink.www.searcher.PDBFileSearcher;
import org.yeastrc.xlink.www.user_session_management.UserSession;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;

@Path("/pdb")
public class PDBService {

	private static final Logger log = LoggerFactory.getLogger( PDBService.class);
	
	/**
	 * @param projectId
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/listPDBFiles")
	public List<WWWPDBFile> listPDBFiles ( 
			@QueryParam("projectId") int projectId,
			@Context HttpServletRequest request ) throws Exception {
		try {
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
			UserSession userSession = accessAndSetupWebSessionResult.getUserSession();
		
			//  Test access to the project id
			WebSessionAuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getWebSessionAuthAccessLevel();
			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {
				if ( accessAndSetupWebSessionResult.isNoSession() ) {
					//  No User session 
					throw new WebApplicationException(
							Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
							.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
							.build()
							);
				}
				//  No Access Allowed for this project id
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			// if they're a public access code user
			if ( userSession == null || ( ! userSession.isActualUser() ) ) {
				return PDBFileSearcher.getInstance().getPDBFilesNoContent( projectId );
			}
			
			return PDBFileSearcher.getInstance().getPDBFilesNoContent( userSession.getAuthUserId(), projectId, authAccessLevel.isProjectOwnerAllowed() );
			
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT ) // This string will be passed to the client
					.build()
					);
		}
	}
	
	/**
	 * @param pdbFileId
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getContentForPDBFile")
	public Map<String, String> getContentForPDBFile ( 
			@QueryParam("pdbFileId") int pdbFileId,
			@Context HttpServletRequest request ) throws Exception {
		try {
			PDBFileDTO pdbFile = PDBFileDAO.getInstance().getPDBFile( pdbFileId );
			if ( pdbFile == null ) {
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			if ( PDBFileConstants.VISIBILITY_PUBLIC.equals( pdbFile.getVisibility() ) ) {
			} else if ( PDBFileConstants.VISIBILITY_PROJECT.equals( pdbFile.getVisibility() ) ) {
				// pdb file restricted to this project
				int projectId = pdbFile.getProjectId();
				GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
						GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
		
				//  Test access to the project id
				WebSessionAuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getWebSessionAuthAccessLevel();
				//  Test access to the project id, admin users are also allowed
				if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {
					if ( accessAndSetupWebSessionResult.isNoSession() ) {
						//  No User session 
						throw new WebApplicationException(
								Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
								.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
								.build()
								);
					}
					//  No Access Allowed for this project id
					throw new WebApplicationException(
							Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
							.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
							.build()
							);
				}
			} else {
				String msg = "Unknown value for visibility: " + pdbFile.getVisibility();
				log.error( msg );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			Map<String, String> retMap = new HashMap<String, String>();
			retMap.put( "content", pdbFile.getContent() );
			return retMap;
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT ) // This string will be passed to the client
					.build()
					);
		}
	}
	
	/**
	 * @param pdbFileId
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/deletePDBFile")
	public Map<String, String> deletePDBFile ( 
			@FormParam("pdbFileId") int pdbFileId,
			@Context HttpServletRequest request ) throws Exception {
		try {
			PDBFileDTO pdbFile = PDBFileDAO.getInstance().getPDBFileNoContent( pdbFileId );
			if ( pdbFile == null ) {
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			int projectId = pdbFile.getProjectId();
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
			UserSession userSession = accessAndSetupWebSessionResult.getUserSession();
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			//  Test access to the project id
			WebSessionAuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getWebSessionAuthAccessLevel();
			//  Restrict access to person who uploaded the file, admin users are also allowed
			if ( ( ! authAccessLevel.isAdminAllowed() ) 
					&&  userSession.getAuthUserId() != pdbFile.getUploadedBy() ) {
				//  No Access Allowed for this user
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			PDBFileDAO.getInstance().deletePDBFile( pdbFileId );
			Map<String, String> retMap = new HashMap<String, String>();
			retMap.put( "message", "success" );
			return retMap;
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT ) // This string will be passed to the client
					.build()
					);
		}
	}
	/* 
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/canEditPDBFile")
	public Map<String, Boolean> canEditPDBFile ( 
			@QueryParam("pdbFileId") int pdbFileId,
			@Context HttpServletRequest request ) throws Exception {
		Map<String, Boolean> retMap = new HashMap<String, Boolean>();
		try {
			PDBFileDTO dto = PDBFileDAO.getInstance().getPDBFileNoContent( pdbFileId );
			if( dto == null ) {
				retMap.put( "response", false );
				return retMap;
			}
			int projectId = dto.getProjectId();
			GetWebSessionAuthAccessLevelForProjectIds_Result accessAndSetupWebSessionResult =
					GetWebSessionAuthAccessLevelForProjectIds.getSinglesonInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
			UserSession userSession = accessAndSetupWebSessionResult.getUserSession();
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			//  Test access to the project id
			WebSessionAuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getWebSessionAuthAccessLevel();
			if( userSession.getUserDBObject() == null ) {
				retMap.put( "response", false );
				return retMap;
			}
			int userId = userSession.getAuthUserId();
			if( userId == dto.getUploadedBy() ) {
				retMap.put( "response", true );
				return retMap;
			}
			retMap.put( "response", false );
			return retMap;
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT ) // This string will be passed to the client
					.build()
					);
		}
	}
	*/
}
