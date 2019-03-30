package org.yeastrc.xlink.www.webservices;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.qc_plots.psm_count_for_score.CreatePsmCountsVsScoreQCPlotData;
import org.yeastrc.xlink.www.qc_plots.psm_count_for_score.PsmCountsVsScoreQCPlotDataJSONRoot;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;


@Path("/qcplot")
public class QCPlotPsmCountsVsScoreService {

	private static final Logger log = LoggerFactory.getLogger( QCPlotPsmCountsVsScoreService.class);
	
	/**
	 * @param searchId
	 * @param scanFileId - optional
	 * @param selectedLinkTypes
	 * @param annotationTypeId
	 * @param psmScoreCutoff
	 * @param proteinSequenceVersionIdsToIncludeList
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getPsmCountsVsScore") 
	public PsmCountsVsScoreQCPlotDataJSONRoot getPsmCountsVsScore( 
			@QueryParam( "projectSearchId" ) int projectSearchId,
			@QueryParam( "scanFileId" ) Integer scanFileId,
			@QueryParam( "selectedLinkTypes" ) Set<String> selectedLinkTypes,			
			@QueryParam( "annotationTypeId" ) Integer annotationTypeId,
			@QueryParam( "psmScoreCutoff" ) Double psmScoreCutoff,
			@QueryParam( "iP" ) List<Integer> proteinSequenceVersionIdsToIncludeList,
			@QueryParam( "eP" ) List<Integer> proteinSequenceVersionIdsToExcludeList,
			@Context HttpServletRequest request )
	throws Exception {

		if ( projectSearchId == 0 ) {
			String msg = ": Provided projectSearchId is zero or wasn't provided";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
		    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
		    	        .build()
		    	        );
		}
		if ( selectedLinkTypes == null || selectedLinkTypes.isEmpty() ) {
			String msg = ": selectedLinkTypes is empty";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
		    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
		    	        .build()
		    	        );
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
			
			PsmCountsVsScoreQCPlotDataJSONRoot psmCountsVsScoreQCPlotDataJSONRoot = 
					CreatePsmCountsVsScoreQCPlotData.getInstance()
					.create( 
							projectSearchId, 
							scanFileId, 
							selectedLinkTypes, 
							annotationTypeId, 
							psmScoreCutoff, 
							proteinSequenceVersionIdsToIncludeList,
							proteinSequenceVersionIdsToExcludeList );
			
			return psmCountsVsScoreQCPlotDataJSONRoot;
			
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
