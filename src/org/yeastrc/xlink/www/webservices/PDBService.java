package org.yeastrc.xlink.www.webservices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.PDBFileDAO;
import org.yeastrc.xlink.dto.PDBFileDTO;
import org.yeastrc.xlink.www.constants.PDBFileConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.WWWPDBFile;
import org.yeastrc.xlink.www.searcher.PDBFileSearcher;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;

@Path("/pdb")
public class PDBService {

	private static final Logger log = Logger.getLogger(PDBService.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/listPDBFiles")
	public List<WWWPDBFile> listPDBFiles ( 
			@QueryParam("projectId") int projectId,
			@Context HttpServletRequest request ) throws Exception {

		try {

			// Get the session first.  
			HttpSession session = request.getSession();



			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
			
			UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();

			if ( accessAndSetupWebSessionResult.isNoSession() ) {

				//  No User session 

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			//  Test access to the project id
			
			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();

			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {

				//  No Access Allowed for this project id

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);

			}
			
			// if they're a public access code user
			if( userSessionObject.getUserDBObject() == null )
				return PDBFileSearcher.getInstance().getPDBFilesNoContent( projectId );

			return PDBFileSearcher.getInstance().getPDBFilesNoContent( userSessionObject.getUserDBObject().getAuthUser().getId(), projectId);
			
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
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getContentForPDBFile")
	public Map<String, String> getContentForPDBFile ( 
			@QueryParam("pdbFileId") int pdbFileId,
			@Context HttpServletRequest request ) throws Exception {

		try {

	
			// Get the session first.  
			HttpSession session = request.getSession();



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

				AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
						GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );

				if ( accessAndSetupWebSessionResult.isNoSession() ) {

					//  No User session 

					throw new WebApplicationException(
							Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
							.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
							.build()
							);
				}
				
				//  Test access to the project id
				
				AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();


				//  Test access to the project id, admin users are also allowed

				if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {

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
	
	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/deletePDBFile")
	public Map<String, String> deletePDBFile ( 
			@FormParam("pdbFileId") int pdbFileId,
			@Context HttpServletRequest request ) throws Exception {

		try {


			// Get the session first.  
			HttpSession session = request.getSession();

			PDBFileDTO pdbFile = PDBFileDAO.getInstance().getPDBFileNoContent( pdbFileId );
			
			if ( pdbFile == null ) {

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}


			int projectId = pdbFile.getProjectId();

			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
			
			UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();

			if ( accessAndSetupWebSessionResult.isNoSession() ) {

				//  No User session 

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			//  Test access to the project id
			
			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();


			//  Restrict access to person who uploaded the file, admin users are also allowed

			if ( ( ! authAccessLevel.isAdminAllowed() ) 
					&&  userSessionObject.getUserDBObject().getAuthUser().getId() != pdbFile.getUploadedBy() ) {

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

			// Get the session first.  
			HttpSession session = request.getSession();

			
			
			PDBFileDTO dto = PDBFileDAO.getInstance().getPDBFileNoContent( pdbFileId );
			if( dto == null ) {
				retMap.put( "response", false );
				return retMap;
			}
			

			int projectId = dto.getProjectId();

			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
			
			UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();

			if ( accessAndSetupWebSessionResult.isNoSession() ) {

				//  No User session 

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}

			
			//  Test access to the project id
			
			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();

			
			
			if( userSessionObject.getUserDBObject() == null ) {
				retMap.put( "response", false );
				return retMap;
			}
			

			
			
			int userId = userSessionObject.getUserDBObject().getAuthUser().getId();
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
