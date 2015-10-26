package org.yeastrc.xlink.www.webservices;






import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.qc_plots.scan_retention_time.CreateScanRetentionTimeQCPlotData;
import org.yeastrc.xlink.www.qc_plots.scan_retention_time.ScanRetentionTimeJSONRoot;
import org.yeastrc.xlink.www.searcher.ProjectIdsForSearchIdsSearcher;
import org.yeastrc.xlink.www.constants.QueryCriteriaValueCountsFieldValuesConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.QueryCriteriaValueCountsDAO;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;


@Path("/qcplot")
public class QCPlotScanRetentionTimeService {

	private static final Logger log = Logger.getLogger(QCPlotScanRetentionTimeService.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getScanRetentionTime") 
	public ScanRetentionTimeJSONRoot getViewerData( 
			@QueryParam( "scansForSelectedLinkTypes" ) List<String> scansForSelectedLinkTypes,			
			@QueryParam( "searchId" ) int searchId,
			@QueryParam( "scanFileId" ) int scanFileId,
			@QueryParam( "psmQValueCutoff" ) Double psmQValueCutoff,
			@QueryParam( "retentionTimeInMinutesCutoff" ) Double retentionTimeInMinutesCutoff,
			@Context HttpServletRequest request )
	throws Exception {
		

		if ( searchId == 0 ) {

			String msg = ": Provided searchId is zero";

			log.error( msg );

		    throw new WebApplicationException(
		    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
		    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
		    	        .build()
		    	        );
		}
		
		
		if ( scanFileId == 0 ) {

			String msg = ": Provided scanFileId is zero";

			log.error( msg );

		    throw new WebApplicationException(
		    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
		    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
		    	        .build()
		    	        );
		}
		
		if ( psmQValueCutoff == null ) {

			String msg = ":  psmQValueCutoff is not provided";

			log.error( msg );

		    throw new WebApplicationException(
		    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
		    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
		    	        .build()
		    	        );
		}
				
		try {

			
			QueryCriteriaValueCountsDAO.getInstance().saveOrIncrement( 
					QueryCriteriaValueCountsFieldValuesConstants.PSM_Q_VALUE_FIELD_VALUE, Double.toString( psmQValueCutoff ) );


			// Get the session first.  
			HttpSession session = request.getSession();


			//   Get the project id for this search
			
			Collection<Integer> searchIdsCollection = new HashSet<Integer>( );

			searchIdsCollection.add( searchId );
			
			
			List<Integer> projectIdsFromSearchIds = ProjectIdsForSearchIdsSearcher.getInstance().getProjectIdsForSearchIds( searchIdsCollection );
			
			if ( projectIdsFromSearchIds.isEmpty() ) {
				
				// should never happen
				
				String msg = "No project ids for search id: " + searchId;
				
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

				//  No Access Allowed for this search id

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}

			Double retentionTimeInSecondsCutoff = null;
			
			if ( retentionTimeInMinutesCutoff != null ) {

				retentionTimeInSecondsCutoff = ( retentionTimeInMinutesCutoff + 1 ) * 60;
			}
			
			ScanRetentionTimeJSONRoot scanRetentionTimeJSONRoot = 
					CreateScanRetentionTimeQCPlotData.getInstance().create( scansForSelectedLinkTypes, searchId, scanFileId, psmQValueCutoff, retentionTimeInSecondsCutoff );
			
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
