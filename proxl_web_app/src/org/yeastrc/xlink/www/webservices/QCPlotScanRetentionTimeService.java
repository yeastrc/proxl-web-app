package org.yeastrc.xlink.www.webservices;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.qc_plots.scan_retention_time.CreateScanRetentionTimeQCPlotData;
import org.yeastrc.xlink.www.qc_plots.scan_retention_time.ScanRetentionTimeJSONRoot;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;

@Path("/qcplot")
public class QCPlotScanRetentionTimeService {
	
	private static final Logger log = Logger.getLogger(QCPlotScanRetentionTimeService.class);
	
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
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getScanRetentionTime") 
	public ScanRetentionTimeJSONRoot getScanRetentionTime( 
			@QueryParam( "projectSearchId" ) Integer projectSearchId,
			@QueryParam( "scanFileId" ) List<Integer> scanFileIdList,
			@QueryParam( "scanFileAll" ) String scanFileAllString, 
			@QueryParam( "scansForSelectedLinkTypes" ) List<String> scansForSelectedLinkTypes,			
			@QueryParam( "filterCriteria" ) String filterCriteria_JSONString,
			@QueryParam( "retentionTimeInMinutesCutoff" ) Double retentionTimeInMinutesCutoff,
			@Context HttpServletRequest request )
	throws Exception {
		
		if ( projectSearchId == null ) {
			String msg = "projectSearchId is not provided";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
		    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
		    	        .build()
		    	        );
		}
		if ( StringUtils.isEmpty( filterCriteria_JSONString ) ) {
			String msg = "Provided filterCriteria is null or filterCriteria is missing";
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
			// Get the session first.  
//			HttpSession session = request.getSession();
			//   Get the project id for this search
			Collection<Integer> projectSearchIdsCollection = new HashSet<Integer>( );
			projectSearchIdsCollection.add( projectSearchId );
			List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsCollection );
			if ( projectIdsFromSearchIds.isEmpty() ) {
				// should never happen
				String msg = "No project ids for projectSearchId: " + projectSearchId;
				log.error( msg );
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
			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
//			UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();
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
				//  No Access Allowed for this search id
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
			ScanRetentionTimeJSONRoot scanRetentionTimeJSONRoot = 
					CreateScanRetentionTimeQCPlotData.getInstance()
					.create( projectSearchId, scanFileIdList, scanFileAll, scansForSelectedLinkTypes, filterCriteria_JSONString, retentionTimeInSecondsCutoff );
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
