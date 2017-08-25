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

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.project_search__search__mapping.MapProjectSearchIdToSearchId;
import org.yeastrc.xlink.www.qc_plots.psm_score_vs_score.CreatePsmScoreVsScoreQCPlotData;
import org.yeastrc.xlink.www.qc_plots.psm_score_vs_score.CreatePsmScoreVsScoreQCPlotData.CreatePsmScoreVsScoreQCPlotDataRequest_SingleScoreType;
import org.yeastrc.xlink.www.qc_plots.psm_score_vs_score.CreatePsmScoreVsScoreQCPlotDataResults;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.constants.QC_Plot_ScoreVsScore_Constants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;


@Path("/qcplot")
public class QCPlotPsmScoreVsScoreService {

	private static final Logger log = Logger.getLogger(QCPlotPsmScoreVsScoreService.class);
	
	/**
	 * @param projectSearchId
	 * @param scanFileId - Optional
	 * @param selectedLinkTypes
	 * @param annotationTypeId_1
	 * @param annotationTypeId_2
	 * @param psmScoreCutoff_1
	 * @param psmScoreCutoff_2
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getPsmScoreVsScore") 
	public QCPlotPsmScoreVsScoreServiceResult getPsmScoreVsScore( 
			@QueryParam( "projectSearchId" ) int projectSearchId,
			@QueryParam( "scanFileId" ) Integer scanFileId,
			@QueryParam( "selectedLinkTypes" ) Set<String> selectedLinkTypes,			
			@QueryParam( "scoreType_1" ) String scoreType_1,
			@QueryParam( "scoreType_2" ) String scoreType_2,
			@QueryParam( "psmScoreCutoff_1" ) Double psmScoreCutoff_1,
			@QueryParam( "psmScoreCutoff_2" ) Double psmScoreCutoff_2,
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
			
			CreatePsmScoreVsScoreQCPlotDataRequest_SingleScoreType singleScoreType_1 = processScoreTypeParam( scoreType_1, "scoreType_1" );
			CreatePsmScoreVsScoreQCPlotDataRequest_SingleScoreType singleScoreType_2 = processScoreTypeParam( scoreType_2, "scoreType_2" );
			
			CreatePsmScoreVsScoreQCPlotDataResults results = 
					CreatePsmScoreVsScoreQCPlotData.getInstance()
					.createPsmScoreVsScoreQCPlotData( 
							searchId, scanFileId, selectedLinkTypes, singleScoreType_1, singleScoreType_2, psmScoreCutoff_1, psmScoreCutoff_2 );
			
			QCPlotPsmScoreVsScoreServiceResult webserviceResult = new QCPlotPsmScoreVsScoreServiceResult();
			webserviceResult.results = results;
			return webserviceResult;
			
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
	 * scoreType must be an allowed string or an int
	 * @param scoreType
	 * @return
	 */
	private CreatePsmScoreVsScoreQCPlotDataRequest_SingleScoreType processScoreTypeParam( String scoreType, String scoreTypeParamName ) {
		
		CreatePsmScoreVsScoreQCPlotDataRequest_SingleScoreType result = new CreatePsmScoreVsScoreQCPlotDataRequest_SingleScoreType();
		
		if ( StringUtils.isEmpty( scoreType ) ) {
			result.setNoValue( true );
			return result;  // EARLY EXIT
		}
		
		if ( QC_Plot_ScoreVsScore_Constants.SCORE_SELECTION_RETENTION_TIME.equals( scoreType ) 
				|| QC_Plot_ScoreVsScore_Constants.SCORE_SELECTION_CHARGE.equals( scoreType )
				|| QC_Plot_ScoreVsScore_Constants.SCORE_SELECTION_PRE_MZ.equals( scoreType ) ) {
			
			result.setAltScoreType( scoreType );
			return result;  // EARLY EXIT
		}
		
		try {
			int annotationTypeId = Integer.parseInt( scoreType );
			result.setAnnotationTypeId( annotationTypeId );
		} catch ( Exception e ) {
			String msg = ": '" + scoreTypeParamName + "' is not an allowed string and not an integer";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
		    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
		    	        .build()
		    	        );
		}
		
		return result;
	}
	
	/**
	 * Webservice result
	 *
	 */
	public static class QCPlotPsmScoreVsScoreServiceResult {
		private CreatePsmScoreVsScoreQCPlotDataResults results;

		public CreatePsmScoreVsScoreQCPlotDataResults getResults() {
			return results;
		}

		public void setResults(CreatePsmScoreVsScoreQCPlotDataResults results) {
			this.results = results;
		}
	}
	
}
