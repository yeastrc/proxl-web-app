package org.yeastrc.xlink.www.webservices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.CustomProteinRegionAnnotationDAO;
import org.yeastrc.xlink.www.dto.CustomProteinRegionAnnotationDTO;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;

@Path("/customRegionAnnotation")

public class CustomProteinRegionAnnotationServices {

	private static final Logger log = LoggerFactory.getLogger( CustomProteinRegionAnnotationServices.class);
	
	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces( MediaType.APPLICATION_JSON )
	@Path("/queryDataForProteins")
	public Map<Integer, List<CustomProteinRegionAnnotationDTO>> getDataForProteins( 
			DataRequestObject webServiceRequest, 
			@Context HttpServletRequest httpServletRequest ) throws Exception {

		
		try {
			
			int projectId = webServiceRequest.getProjectId();
			
			// Get the session first.  
			HttpSession session = httpServletRequest.getSession();
			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, httpServletRequest );
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
		
		Map<Integer, List<CustomProteinRegionAnnotationDTO>> retMap = new HashMap<>();
		
		int[] proteinIds = webServiceRequest.getProteinIds();
		int projectId = webServiceRequest.getProjectId();

		for( int proteinId : proteinIds ) {
			List<CustomProteinRegionAnnotationDTO> annotatedRegions = CustomProteinRegionAnnotationDAO.getInstance().
																		getAllCustomProteinRegionAnnotationDTO(proteinId, projectId);

			retMap.put( proteinId,  annotatedRegions );		
		}
		
		
		return retMap;
	}

	
	
	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces( MediaType.APPLICATION_JSON )
	@Path("/queryDataForProtein")
	public List<CustomProteinRegionAnnotationDTO> getDataForProtein( 
			DataRequestObject webServiceRequest, 
			@Context HttpServletRequest httpServletRequest ) throws Exception {

		
		try {
			
			int projectId = webServiceRequest.getProjectId();
			
			// Get the session first.  
			HttpSession session = httpServletRequest.getSession();
			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, httpServletRequest );
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
		
		
		int proteinId = webServiceRequest.getProteinId();
		int projectId = webServiceRequest.getProjectId();
		
		List<CustomProteinRegionAnnotationDTO> annotatedRegions = CustomProteinRegionAnnotationDAO.getInstance().
				                                                  getAllCustomProteinRegionAnnotationDTO(proteinId, projectId);
		
		
		
		return annotatedRegions;
	}

	
	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces( MediaType.APPLICATION_JSON )
	@Path("/saveDataForProtein")
	public List<CustomProteinRegionAnnotationDTO> saveDataForProtein( 
			DataRequestObject webServiceRequest, 
			@Context HttpServletRequest httpServletRequest ) throws Exception {

		int userId;
		
		try {
			
			int projectId = webServiceRequest.getProjectId();
			
			// Get the session first.  
			HttpSession session = httpServletRequest.getSession();
			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, httpServletRequest );
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
			if ( ! authAccessLevel.isWriteAllowed() ) {
				//  No Access Allowed for this project id
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			userId = userSessionObject.getUserDBObject().getAuthUser().getId();


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
		
		// add remaining necessary info to the DTOs
		for( CustomProteinRegionAnnotationDTO dto : webServiceRequest.getRegionData() ) {
			dto.setProjectId( webServiceRequest.getProjectId() );
			dto.setProteinSequenceVersionId( webServiceRequest.getProteinId() );
			dto.setCreatedBy( userId );
		}
		
		// save the data to the database.
		try {
			CustomProteinRegionAnnotationDAO.getInstance().insertNewListOfRegionAnnotationsForProteinAndProject(
					webServiceRequest.getProteinId(),
					webServiceRequest.getProjectId(),
					webServiceRequest.getRegionData()
					);
		} catch( Exception e ) {
			log.error( "Thrown exception from CustomProteinRegionAnnotationDAO.getInstance().insertNewListOfRegionAnnotationsForProteinAndProject(...): ProjectId: " 
					+ webServiceRequest.getProjectId(),  e );
			throw e;
		}
		
		
		return webServiceRequest.getRegionData();
	}
	
	public static class SaveResponseObject {
		
		/**
		 * @return the message
		 */
		public String getMessage() {
			return message;
		}

		/**
		 * @param message the message to set
		 */
		public void setMessage(String message) {
			this.message = message;
		}

		private String message;
	}
	
	public static class DataRequestObject {
		
		/**
		 * @return the proteinId
		 */
		public int getProteinId() {
			return proteinId;
		}

		/**
		 * @param proteinId the proteinId to set
		 */
		public void setProteinId(int proteinId) {
			this.proteinId = proteinId;
		}

		/**
		 * @return the projectId
		 */
		public int getProjectId() {
			return projectId;
		}

		/**
		 * @param projectId the projectId to set
		 */
		public void setProjectId(int projectId) {
			this.projectId = projectId;
		}
		/**
		 * @return the regionData
		 */
		public List<CustomProteinRegionAnnotationDTO> getRegionData() {
			return regionData;
		}
		/**
		 * @param regionData the regionData to set
		 */
		public void setRegionData(List<CustomProteinRegionAnnotationDTO> regionData) {
			this.regionData = regionData;
		}		
		/**
		 * @return the proteinIds
		 */
		public int[] getProteinIds() {
			return proteinIds;
		}

		/**
		 * @param proteinIds the proteinIds to set
		 */
		public void setProteinIds(int[] proteinIds) {
			this.proteinIds = proteinIds;
		}



		private int[] proteinIds;
		private int proteinId;
		private int projectId;
		private List<CustomProteinRegionAnnotationDTO> regionData;
	}
	
}
