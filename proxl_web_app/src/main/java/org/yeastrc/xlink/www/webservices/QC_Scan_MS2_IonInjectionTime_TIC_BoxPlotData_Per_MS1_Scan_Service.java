package org.yeastrc.xlink.www.webservices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.project_search__search__mapping.MapProjectSearchIdToSearchId;
import org.yeastrc.xlink.www.qc_data.scan_level_data.main.QC_Scan_MS2_IonInjectionTime_TIC_BoxPlotData_Per_MS1_Scan;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.SearchIdScanFileIdCombinedRecordExistsSearcher;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import org.yeastrc.xlink.www.web_utils.UnmarshalJSON_ToObject;

@Path("/qc/dataPage")
public class QC_Scan_MS2_IonInjectionTime_TIC_BoxPlotData_Per_MS1_Scan_Service {
	
	private static final Logger log = LoggerFactory.getLogger( QC_Scan_MS2_IonInjectionTime_TIC_BoxPlotData_Per_MS1_Scan_Service.class);

	/**
	 * Web Service Request Object
	 */
	public static class WebserviceRequest {
		private List<Integer> projectSearchIds;
		private Integer scanFileId;
		public void setProjectSearchIds(List<Integer> projectSearchIds) {
			this.projectSearchIds = projectSearchIds;
		}
		public void setScanFileId(Integer scanFileId) {
			this.scanFileId = scanFileId;
		}
	}
	
	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getScan_MS2_IonInjectionTime_TIC_BoxPlotData_Per_MS1_Scan") 
	public byte[] getScan_MS2_IonInjectionTime_TIC_BoxPlotData_Per_MS1_Scan( 
			byte[] requestJSONBytes,
			@Context HttpServletRequest request,
			@Context HttpServletResponse response )
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
		
		List<Integer> projectSearchIdList = webserviceRequest.projectSearchIds;
		Integer scanFileId = webserviceRequest.scanFileId;
		
		if ( projectSearchIdList == null || projectSearchIdList.isEmpty() ) {
			String msg = "Provided project_search_id is null or project_search_id is missing";
			log.warn( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}

		if ( scanFileId == null ) {
			String msg = "Provided scan_file_id is null";
			log.warn( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}

//		String requestQueryString = request.getQueryString();
		
		try {
			//   Get the project id for this search
			//   Get the project id for these searches
			Set<Integer> projectSearchIdsSet = new HashSet<Integer>( );
			for ( int projectSearchId : projectSearchIdList ) {
				projectSearchIdsSet.add( projectSearchId );
			}
			List<Integer> projectSearchIdsListDeduppedSorted = new ArrayList<>( projectSearchIdsSet );
			Collections.sort( projectSearchIdsListDeduppedSorted );

			List<Integer> projectIdsFromProjectSearchIds = 
					ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsSet );
			if ( projectIdsFromProjectSearchIds.isEmpty() ) {
				// should never happen
				@SuppressWarnings("unchecked")
				String msg = "No project ids for project search ids: " + StringUtils.join( projectSearchIdList );
				log.error( msg );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			if ( projectIdsFromProjectSearchIds.size() > 1 ) {
				//  Invalid request, searches across projects
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			int projectId = projectIdsFromProjectSearchIds.get( 0 );
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
			
			//  Confirm the scan file id is in one of the project search ids
			
			boolean scanPsmRecordFoundForScanFileIdProjectSearchId = false;
			
			for ( Integer projectSearchId : projectSearchIdList ) {
				Integer searchId =
						MapProjectSearchIdToSearchId.getInstance().getSearchIdFromProjectSearchId( projectSearchId );
				if ( searchId == null ) {
					String msg = ": No searchId found for projectSearchId: " + projectSearchId;
					log.warn( msg );
					throw new WebApplicationException(
							Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
							.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
							.build()
							);
				}

				if ( SearchIdScanFileIdCombinedRecordExistsSearcher.getInstance()
						.recordExistsForSearchIdScanFileIdCombined( searchId, scanFileId ) ) {
					scanPsmRecordFoundForScanFileIdProjectSearchId = true;
					break;
				}
			}
			
			if ( ! scanPsmRecordFoundForScanFileIdProjectSearchId ) {
				@SuppressWarnings("unchecked")
				String msg = "Provided scan_file_id not for for any provided project search ids.  "
						+ "scan_file_id: " + scanFileId + ", projectSearchIds: " + StringUtils.join( projectSearchIdList );
				log.warn( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}

			byte[] resultsAsBytes = 
					QC_Scan_MS2_IonInjectionTime_TIC_BoxPlotData_Per_MS1_Scan.getInstance()
					.getQC_Scan_MS2_FillTime_TIC_BoxPlotData_Per_MS1_Scan( scanFileId, requestJSONBytes );
		
			
			return resultsAsBytes;
			
			//  Can return a response by writing it to response.getOutputStream() and returning null 
			
//			try ( OutputStream responseOutputStream = response.getOutputStream() ) {
//				
//				responseOutputStream.write( resultsAsBytes );
//			} catch ( Exception e ) {
//				String msg = "Exception Writing result to response.getOutputStream(): " + e.toString();
//				log.error( msg, e );
//				throw e;
//			}
//			
//			return null;
			
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
	
//	Not used since directly returning byte[] of JSON from internal code
//	
//	/**
//	 * Webservice returned object
//	 *
//	 */
//	public static class QC_Scan_MS_1_IonCurrent_Histograms_WebserviceResult {
//
//		private Scan_MS_1_IonCurrent_HistogramsResult scan_MS_1_IonCurrent_HistogramsResult;
//		
//		public Scan_MS_1_IonCurrent_HistogramsResult getScan_MS_1_IonCurrent_HistogramsResult() {
//			return scan_MS_1_IonCurrent_HistogramsResult;
//		}
//		public void setScan_MS_1_IonCurrent_HistogramsResult(
//				Scan_MS_1_IonCurrent_HistogramsResult scan_MS_1_IonCurrent_HistogramsResult) {
//			this.scan_MS_1_IonCurrent_HistogramsResult = scan_MS_1_IonCurrent_HistogramsResult;
//		}
//		
//	}
}
