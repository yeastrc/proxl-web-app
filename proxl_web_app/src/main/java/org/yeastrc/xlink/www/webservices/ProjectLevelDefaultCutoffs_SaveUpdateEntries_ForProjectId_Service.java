package org.yeastrc.xlink.www.webservices;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.database_update_with_transaction_services.ProjectLevelDefaultCutoffs_SaveUpdate_UsingDBTransactionService;
import org.yeastrc.xlink.www.dto.ProjectLevelDefaultFltrAnnCutoffs_CutoffAsStringValue_DTO;
import org.yeastrc.xlink.www.dto.ProjectLevelDefaultFltrAnnCutoffs_DTO;
import org.yeastrc.xlink.www.dto.ProjectLevelDefaultFltr_MinPSMs_DTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.www.user_session_management.UserSession;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.enum_classes.PsmPeptideAnnotationType;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import org.yeastrc.xlink.www.web_utils.UnmarshalJSON_ToObject;

/**
 * Save/Update entries in projectLevelDefaultCutoffs for Project Id
 *
 */
@Path("/projectLevelDefaultCutoffs_SaveUpdateEntries_ForProjectId")
public class ProjectLevelDefaultCutoffs_SaveUpdateEntries_ForProjectId_Service {
	
	private static final Logger log = LoggerFactory.getLogger( ProjectLevelDefaultCutoffs_SaveUpdateEntries_ForProjectId_Service.class);

	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces(MediaType.APPLICATION_JSON)
	public WebserviceResult webserviceMethod( 
			byte[] requestJSONBytes,
			@Context HttpServletRequest request )
	throws Exception {

		if ( requestJSONBytes == null || requestJSONBytes.length == 0 ) {
			String msg = "requestJSONBytes is null or requestJSONBytes is empty";
			log.warn( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
//		    	        .entity(  )
		    	        .build()
		    	        );
		}
		WebserviceRequest webserviceRequest = null;
		try {
			webserviceRequest =
					UnmarshalJSON_ToObject.getInstance().getObjectFromJSONByteArray( requestJSONBytes, WebserviceRequest.class );
		} catch ( Exception e ) {
			String msg = "parse request failed";
			log.warn( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
//		    	        .entity(  )
		    	        .build()
		    	        );
		}

		Integer projectId = webserviceRequest.projectId;
		
		if ( projectId == null || projectId == 0 ) {
			String msg = ": Provided projectId is not provided or is zero";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
		    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT )
		    	        .build()
		    	        );
		}
		
		Integer minPSMs = webserviceRequest.minPSMs;

//		if ( minPSMs == null || minPSMs == 0 ) {
//			String msg = ": Provided minPSMs is not provided or is zero";
//			log.error( msg );
//		    throw new WebApplicationException(
//		    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
//		    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT )
//		    	        .build()
//		    	        );
//		}

		WebserviceRequest_CutoffValues cutoffValues = webserviceRequest.cutoffValues;

		if ( cutoffValues == null ) {
			String msg = ": cutoffValues not provided";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
		    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT )
		    	        .build()
		    	        );
		}
		
		try {
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
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

			UserSession userSession = accessAndSetupWebSessionResult.getUserSession();

			////////   Auth complete
			//////////////////////////////////////////
			
			if ( userSession == null ) {
				String msg = "userSession == null";
				log.error( msg );
				throw new ProxlWebappInternalErrorException(msg);
			}
			if ( ! userSession.isActualUser() ) {
				String msg = "! userSession.isActualUser()";
				log.error( msg );
				throw new ProxlWebappInternalErrorException(msg);
			}
			
			Integer authUserId = userSession.getAuthUserId();
			if ( authUserId == null ) {
				String msg = "authUserId == null";
				log.error( msg );
				throw new ProxlWebappInternalErrorException(msg);
			}
			
			int arrayLengthsTotal = 0;
			
			if ( cutoffValues.reportedPeptideEntriesList != null ) {
				arrayLengthsTotal += cutoffValues.reportedPeptideEntriesList.size();
			}
			if ( cutoffValues.psmEntriesList != null ) {
				arrayLengthsTotal += cutoffValues.psmEntriesList.size();
			}
			
			List<ProjectLevelDefaultCutoffs_SaveUpdate_UsingDBTransactionService.Entry> entriesToSave = new ArrayList<>( arrayLengthsTotal );

			if ( cutoffValues.reportedPeptideEntriesList != null ) {
				for ( WebserviceRequestEntry webserviceRequestEntry : cutoffValues.reportedPeptideEntriesList ) {
					ProjectLevelDefaultFltrAnnCutoffs_DTO projectLevelDefaultFltrAnnCutoffs_DTO = new ProjectLevelDefaultFltrAnnCutoffs_DTO();
					ProjectLevelDefaultFltrAnnCutoffs_CutoffAsStringValue_DTO projectLevelDefaultFltrAnnCutoffs_CutoffAsStringValue_DTO = new ProjectLevelDefaultFltrAnnCutoffs_CutoffAsStringValue_DTO();
					projectLevelDefaultFltrAnnCutoffs_DTO.setProjectId( projectId );
					projectLevelDefaultFltrAnnCutoffs_DTO.setPsmPeptideAnnotationType( PsmPeptideAnnotationType.PEPTIDE );
					projectLevelDefaultFltrAnnCutoffs_DTO.setSearchProgramName( webserviceRequestEntry.searchProgramName );
					projectLevelDefaultFltrAnnCutoffs_DTO.setAnnotationTypeName( webserviceRequestEntry.annotationTypeName );
					projectLevelDefaultFltrAnnCutoffs_DTO.setAnnotationCutoffValue( webserviceRequestEntry.annotationCutoffValue );
					projectLevelDefaultFltrAnnCutoffs_DTO.setCreatedAuthUserId( authUserId );
					projectLevelDefaultFltrAnnCutoffs_DTO.setLastUpdatedAuthUserId( authUserId );
					projectLevelDefaultFltrAnnCutoffs_CutoffAsStringValue_DTO.setAnnotationCutoffValueString( webserviceRequestEntry.annotationCutoffValueString );
					
					ProjectLevelDefaultCutoffs_SaveUpdate_UsingDBTransactionService.Entry entry = new ProjectLevelDefaultCutoffs_SaveUpdate_UsingDBTransactionService.Entry();
					entry.projectLevelDefaultFltrAnnCutoffs_DTO = projectLevelDefaultFltrAnnCutoffs_DTO;
					entry.projectLevelDefaultFltrAnnCutoffs_CutoffAsStringValue_DTO = projectLevelDefaultFltrAnnCutoffs_CutoffAsStringValue_DTO;
					entriesToSave.add( entry );
				}
			}

			if ( cutoffValues.psmEntriesList != null ) {
				for ( WebserviceRequestEntry webserviceRequestEntry : cutoffValues.psmEntriesList ) {
					ProjectLevelDefaultFltrAnnCutoffs_DTO projectLevelDefaultFltrAnnCutoffs_DTO = new ProjectLevelDefaultFltrAnnCutoffs_DTO();
					ProjectLevelDefaultFltrAnnCutoffs_CutoffAsStringValue_DTO projectLevelDefaultFltrAnnCutoffs_CutoffAsStringValue_DTO = new ProjectLevelDefaultFltrAnnCutoffs_CutoffAsStringValue_DTO();
					projectLevelDefaultFltrAnnCutoffs_DTO.setProjectId( projectId );
					projectLevelDefaultFltrAnnCutoffs_DTO.setPsmPeptideAnnotationType( PsmPeptideAnnotationType.PSM );
					projectLevelDefaultFltrAnnCutoffs_DTO.setSearchProgramName( webserviceRequestEntry.searchProgramName );
					projectLevelDefaultFltrAnnCutoffs_DTO.setAnnotationTypeName( webserviceRequestEntry.annotationTypeName );
					projectLevelDefaultFltrAnnCutoffs_DTO.setAnnotationCutoffValue( webserviceRequestEntry.annotationCutoffValue );
					projectLevelDefaultFltrAnnCutoffs_DTO.setCreatedAuthUserId( authUserId );
					projectLevelDefaultFltrAnnCutoffs_DTO.setLastUpdatedAuthUserId( authUserId );
					projectLevelDefaultFltrAnnCutoffs_CutoffAsStringValue_DTO.setAnnotationCutoffValueString( webserviceRequestEntry.annotationCutoffValueString );
					
					ProjectLevelDefaultCutoffs_SaveUpdate_UsingDBTransactionService.Entry entry = new ProjectLevelDefaultCutoffs_SaveUpdate_UsingDBTransactionService.Entry();
					entry.projectLevelDefaultFltrAnnCutoffs_DTO = projectLevelDefaultFltrAnnCutoffs_DTO;
					entry.projectLevelDefaultFltrAnnCutoffs_CutoffAsStringValue_DTO = projectLevelDefaultFltrAnnCutoffs_CutoffAsStringValue_DTO;
					entriesToSave.add( entry );
				}
			}
			ProjectLevelDefaultFltr_MinPSMs_DTO projectLevelDefaultFltr_MinPSMs_DTO = null;
			if ( minPSMs != null ){
				projectLevelDefaultFltr_MinPSMs_DTO = new ProjectLevelDefaultFltr_MinPSMs_DTO();
				projectLevelDefaultFltr_MinPSMs_DTO.setProjectId( projectId );
				projectLevelDefaultFltr_MinPSMs_DTO.setMinPSMs( minPSMs );
				projectLevelDefaultFltr_MinPSMs_DTO.setCreatedAuthUserId( authUserId );
				projectLevelDefaultFltr_MinPSMs_DTO.setLastUpdatedAuthUserId( authUserId );
			}
			
			ProjectLevelDefaultCutoffs_SaveUpdate_UsingDBTransactionService.getInstance().saveUpdate( projectId, entriesToSave, projectLevelDefaultFltr_MinPSMs_DTO );
			
			WebserviceResult result = new WebserviceResult();
			result.status = true;
			
			return result;
			
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
	 * Input to function webserviceMethod(..)
	 */
	public static class WebserviceRequest {
		
		private Integer projectId;
		private WebserviceRequest_CutoffValues cutoffValues;
		private Integer minPSMs;

		public void setProjectId(Integer projectId) {
			this.projectId = projectId;
		}
		public void setCutoffValues(WebserviceRequest_CutoffValues cutoffValues) {
			this.cutoffValues = cutoffValues;
		}
		public void setMinPSMs(Integer minPSMs) {
			this.minPSMs = minPSMs;
		}
	}

	/**
	 * cutoffValues in WebserviceRequest
	 *
	 */
	public static class WebserviceRequest_CutoffValues {

		private List<WebserviceRequestEntry> reportedPeptideEntriesList;
		private List<WebserviceRequestEntry> psmEntriesList;
		
		public void setReportedPeptideEntriesList(List<WebserviceRequestEntry> reportedPeptideEntriesList) {
			this.reportedPeptideEntriesList = reportedPeptideEntriesList;
		}
		public void setPsmEntriesList(List<WebserviceRequestEntry> psmEntriesList) {
			this.psmEntriesList = psmEntriesList;
		}
	}

	/**
	 * Entry in WebserviceRequest_CutoffValues
	 *
	 */
	public static class WebserviceRequestEntry {
		
		private String searchProgramName;
		private String annotationTypeName;
		private double annotationCutoffValue;
		private String annotationCutoffValueString;
		
		public void setSearchProgramName(String searchProgramName) {
			this.searchProgramName = searchProgramName;
		}
		public void setAnnotationTypeName(String annotationTypeName) {
			this.annotationTypeName = annotationTypeName;
		}
		public void setAnnotationCutoffValue(double annotationCutoffValue) {
			this.annotationCutoffValue = annotationCutoffValue;
		}
		public void setAnnotationCutoffValueString(String annotationCutoffValueString) {
			this.annotationCutoffValueString = annotationCutoffValueString;
		}
		
	}
	
	/**
	 * result from Webservice
	 *
	 */
	public static class WebserviceResult {
		
		private boolean status;

		public boolean isStatus() {
			return status;
		}
	}
	
}
