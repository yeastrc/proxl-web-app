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
import org.yeastrc.xlink.enum_classes.PsmPeptideAnnotationType;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.searcher.ProjectLevelDefaultFltrAnnCutoffs_For_DisplayOnMgmtPage_Searcher;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import org.yeastrc.xlink.www.web_utils.UnmarshalJSON_ToObject;

/**
 * Get existing entries in projectLevelDefaultCutoffs for Project Id
 *
 */
@Path("/projectLevelDefaultCutoffs_GetExistingEntries_ForProjectId")
public class ProjectLevelDefaultCutoffs_GetExistingEntries_ForProjectId_Service {
	
	private static final Logger log = LoggerFactory.getLogger( ProjectLevelDefaultCutoffs_GetExistingEntries_ForProjectId_Service.class);

	/**
	 * Input to function webserviceMethod(..)
	 */
	public static class WebserviceRequest {
		private Integer projectId;

		public void setProjectId(Integer projectId) {
			this.projectId = projectId;
		}
	}

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
		
		if ( webserviceRequest.projectId == null || webserviceRequest.projectId == 0 ) {
			String msg = ": Provided projectId is not provided or is zero";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
		    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
		    	        .build()
		    	        );
		}
		
		Integer projectId = webserviceRequest.projectId;
		
		try {
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
//			UserSession userSession = accessAndSetupWebSessionResult.getUserSession();
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
			////////   Auth complete
			//////////////////////////////////////////
			
			List<ProjectLevelDefaultFltrAnnCutoffs_For_DisplayOnMgmtPage_Searcher.ResultItem> resultList = 
					ProjectLevelDefaultFltrAnnCutoffs_For_DisplayOnMgmtPage_Searcher.getInstance().getAllForProjectId( projectId );
			
			List<WebserviceResultEntry> reportedPeptideEntriesList = new ArrayList<>( resultList.size() );
			List<WebserviceResultEntry> psmEntriesList = new ArrayList<>( resultList.size() );

			for ( ProjectLevelDefaultFltrAnnCutoffs_For_DisplayOnMgmtPage_Searcher.ResultItem resultItem : resultList ) {
				
				WebserviceResultEntry webserviceResultEntry = new WebserviceResultEntry();
				webserviceResultEntry.id = resultItem.getId();
				webserviceResultEntry.searchProgramName = resultItem.getSearchProgramName();
				webserviceResultEntry.annotationTypeName = resultItem.getAnnotationTypeName();
				webserviceResultEntry.annotationCutoffValue = resultItem.getAnnotationCutoffValue();
				webserviceResultEntry.annotationCutoffValueString = resultItem.getAnnotationCutoffValueString();

				if ( resultItem.getPsmPeptideAnnotationType() == PsmPeptideAnnotationType.PEPTIDE ) {
					
					reportedPeptideEntriesList.add( webserviceResultEntry );
					
				} else if ( resultItem.getPsmPeptideAnnotationType() == PsmPeptideAnnotationType.PSM ) {
					
					psmEntriesList.add( webserviceResultEntry );
					
				} else {
					String msg = "Unknown value for resultItem.getPsmPeptideAnnotationType(): " + resultItem.getPsmPeptideAnnotationType();
					log.error( msg );
					throw new ProxlWebappDataException( msg );
				}
			}

			WebserviceResult result = new WebserviceResult();
			
			result.reportedPeptideEntriesList = reportedPeptideEntriesList;
			result.psmEntriesList = psmEntriesList;
			
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
	 * result from Webservice
	 *
	 */
	public static class WebserviceResult {
		
		private List<WebserviceResultEntry> reportedPeptideEntriesList;
		private List<WebserviceResultEntry> psmEntriesList;

		public List<WebserviceResultEntry> getReportedPeptideEntriesList() {
			return reportedPeptideEntriesList;
		}
		public List<WebserviceResultEntry> getPsmEntriesList() {
			return psmEntriesList;
		}
	}
	
	/**
	 * Entry in WebserviceResult
	 *
	 */
	public static class WebserviceResultEntry {
		
		private int id;
		private String searchProgramName;
		private String annotationTypeName;
		private double annotationCutoffValue;
		private String annotationCutoffValueString;
		
		public int getId() {
			return id;
		}
		public String getSearchProgramName() {
			return searchProgramName;
		}
		public String getAnnotationTypeName() {
			return annotationTypeName;
		}
		public double getAnnotationCutoffValue() {
			return annotationCutoffValue;
		}
		public String getAnnotationCutoffValueString() {
			return annotationCutoffValueString;
		}
	}
}
