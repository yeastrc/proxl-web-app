package org.yeastrc.xlink.www.webservices;

import java.util.Collection;
import java.util.HashSet;
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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.qc_data.a_enums.ForDownload_Enum;
import org.yeastrc.xlink.www.qc_data.a_request_json_root.QCPageRequestJSONRoot;
import org.yeastrc.xlink.www.qc_data.utils.QC_DeserializeRequestJSON_To_QCPageRequestJSONRoot;
import org.yeastrc.xlink.www.qc_plots.scan_retention_time.CreateScanRetentionTimeQCPlotData;
import org.yeastrc.xlink.www.qc_plots.scan_retention_time.ScanRetentionTimeJSONRoot;
import org.yeastrc.xlink.www.qc_plots.scan_retention_time.CreateScanRetentionTimeQCPlotData.CreateScanRetentionTimeQCPlotData_Result;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.form_query_json_objects.QCPageQueryJSONRoot;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;

@Path("/qcplot")
public class QCPlotScanRetentionTimeService {
	
	private static final Logger log = LoggerFactory.getLogger( QCPlotScanRetentionTimeService.class);
	
	/**
	 * @param projectSearchId
	 * @param scanFileIdList - Must be size 1 if scanFileAll not populated, if empty when scanFileAll is populated, lookup scanfile ids for projectSearchId
	 * @param scanFileAllString - If populated, assumed to be true
	 * @param scansForSelectedLinkTypes
	 * @param filterCriteria_JSONString
	 * @param retentionTimeInMinutesCutoff
	 * @param request
	 * @return
	 * @throws Exception
	 */

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getScanRetentionTime") 
	public ScanRetentionTimeJSONRoot getScanRetentionTime( 
			byte[] requestJSONBytes,
			@Context HttpServletRequest request )
	throws Exception {

		Integer projectSearchId = null;

		//  Must be size 1 if scanFileAll not populated, if empty when scanFileAll is populated, lookup scanfile ids for projectSearchId
		List<Integer> scanFileIdList = null;
		// If populated, assumed to be true
		String scanFileAllString = null; 
		
		Double retentionTimeInMinutesCutoff = null;

		QCPageRequestJSONRoot qcPageRequestJSONRoot = null;
		QCPageQueryJSONRoot qcPageQueryJSONRoot = null;
		
		{
			if ( requestJSONBytes == null || requestJSONBytes.length == 0 ) {
				String msg = "requestJSONBytes is null or requestJSONBytes is empty";
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
	//		    	        .entity(  )
			    	        .build()
			    	        );
			}
			try {
				qcPageRequestJSONRoot =
						QC_DeserializeRequestJSON_To_QCPageRequestJSONRoot.getInstance().deserializeRequestJSON_To_QCPageRequestJSONRoot( requestJSONBytes );
			} catch ( Exception e ) {
				String msg = "parse request failed";
				log.warn( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
	//		    	        .entity(  )
			    	        .build()
			    	        );
			}
	
			List<Integer> projectSearchIdList = qcPageRequestJSONRoot.getProjectSearchIds();

			if ( projectSearchIdList == null || projectSearchIdList.isEmpty() ) {
				String msg = "Provided projectSearchIds is null or projectSearchIds is missing";
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}

			if ( projectSearchIdList.size() != 1 ) {
				String msg = "Provided projectSearchIds size is not == 1 ";
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			
			projectSearchId = projectSearchIdList.get(0);
			scanFileIdList = qcPageRequestJSONRoot.getScanFileIdList();
			// If populated, assumed to be true
			scanFileAllString = qcPageRequestJSONRoot.getScanFileAllString(); 
			
			retentionTimeInMinutesCutoff = qcPageRequestJSONRoot.getRetentionTimeInMinutesCutoff();
			
			qcPageQueryJSONRoot = qcPageRequestJSONRoot.getQcPageQueryJSONRoot();
		}
		
		if ( projectSearchId == null ) {
			String msg = "projectSearchId is not provided";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
		    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
		    	        .build()
		    	        );
		}
		if ( qcPageQueryJSONRoot == null ) {
			String msg = "Provided qcPageQueryJSONRoot is null or is missing";
			log.error( msg );
			throw new WebApplicationException(
					Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
					.entity( msg )
					.build()
					);
		}
		if ( StringUtils.isEmpty( scanFileAllString ) ) {
			if ( scanFileIdList == null || scanFileIdList.isEmpty() ) {
				String msg = "scanFileId is not provided and scanFileAll is empty or not provided";
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
			    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
			    	        .build()
			    	        );
			}
			if ( scanFileIdList.size() > 1 ) {
				// scanFileId must be size 1 if scanFileAll not populated
				String msg = "More than 1 scanFileId and scanFileAll is empty or not provided";
				log.error( msg );
				throw new WebApplicationException(
						Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
						.build()
						);
			}
		}
		
		boolean scanFileAll = false;
		if ( StringUtils.isNotEmpty( scanFileAllString ) ) {
			scanFileAll = true;
		}

		try {
			//   Get the project id for this search
			Collection<Integer> projectSearchIdsCollection = new HashSet<Integer>( );
			projectSearchIdsCollection.add( projectSearchId );
			List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsCollection );
			if ( projectIdsFromSearchIds.isEmpty() ) {
				// should never happen
				String msg = "No project ids for projectSearchId: " + projectSearchId;
				log.warn( msg );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			if ( projectIdsFromSearchIds.size() > 1 ) {
				//  Invalid request, searches across projects
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			int projectId = projectIdsFromSearchIds.get( 0 );
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
			
			Double retentionTimeInSecondsCutoff = null;
			if ( retentionTimeInMinutesCutoff != null ) {
				retentionTimeInSecondsCutoff = ( retentionTimeInMinutesCutoff + 1 ) * 60;
			}
			
			CreateScanRetentionTimeQCPlotData_Result createScanRetentionTimeQCPlotData_Result = 
					CreateScanRetentionTimeQCPlotData.getInstance()
					.create( ForDownload_Enum.NO,  
							projectSearchId, scanFileIdList, scanFileAll, qcPageQueryJSONRoot, retentionTimeInSecondsCutoff );
			
			ScanRetentionTimeJSONRoot scanRetentionTimeJSONRoot = createScanRetentionTimeQCPlotData_Result.getScanRetentionTimeJSONRoot();
					
			return scanRetentionTimeJSONRoot;
			
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
}
