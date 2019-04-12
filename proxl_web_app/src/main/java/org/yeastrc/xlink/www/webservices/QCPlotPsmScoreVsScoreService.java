package org.yeastrc.xlink.www.webservices;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import org.yeastrc.xlink.www.project_search__search__mapping.MapProjectSearchIdToSearchId;
import org.yeastrc.xlink.www.qc_plots.psm_score_vs_score.CreatePsmScoreVsScoreQCPlotData;
import org.yeastrc.xlink.www.qc_plots.psm_score_vs_score.CreatePsmScoreVsScoreQCPlotData.CreatePsmScoreVsScoreQCPlotDataRequest_SingleScoreType;
import org.yeastrc.xlink.www.qc_plots.psm_score_vs_score.CreatePsmScoreVsScoreQCPlotDataResults;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.constants.QC_Plot_ScoreVsScore_Constants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;


@Path("/qcplot")
public class QCPlotPsmScoreVsScoreService {

	private static final Logger log = LoggerFactory.getLogger( QCPlotPsmScoreVsScoreService.class);
	
	/**
	 * Format of JSON in request
	 *
	 */
	public static class WebserviceRequest {
		
		Integer projectSearchId;
		Integer scanFileId; // Optional
		Set<String> selectedLinkTypes;			
		String scoreType_1;
		String scoreType_2;
		Double psmScoreCutoff_1;
		Double psmScoreCutoff_2;
		
		public void setProjectSearchId(Integer projectSearchId) {
			this.projectSearchId = projectSearchId;
		}
		public void setScanFileId(Integer scanFileId) {
			this.scanFileId = scanFileId;
		}
		public void setSelectedLinkTypes(Set<String> selectedLinkTypes) {
			this.selectedLinkTypes = selectedLinkTypes;
		}
		public void setScoreType_1(String scoreType_1) {
			this.scoreType_1 = scoreType_1;
		}
		public void setScoreType_2(String scoreType_2) {
			this.scoreType_2 = scoreType_2;
		}
		public void setPsmScoreCutoff_1(Double psmScoreCutoff_1) {
			this.psmScoreCutoff_1 = psmScoreCutoff_1;
		}
		public void setPsmScoreCutoff_2(Double psmScoreCutoff_2) {
			this.psmScoreCutoff_2 = psmScoreCutoff_2;
		}
	
	}
	
	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getPsmScoreVsScore") 
	public QCPlotPsmScoreVsScoreServiceResult getPsmScoreVsScore( 
			WebserviceRequest webserviceRequest,
			@Context HttpServletRequest request )
	throws Exception {
		
		Integer projectSearchId =  webserviceRequest.projectSearchId;
		Integer scanFileId =  webserviceRequest.scanFileId; // Optional
		Set<String> selectedLinkTypes =  webserviceRequest.selectedLinkTypes;
		String scoreType_1 =  webserviceRequest.scoreType_1;
		String scoreType_2 =  webserviceRequest.scoreType_2;
		Double psmScoreCutoff_1 =  webserviceRequest.psmScoreCutoff_1;
		Double psmScoreCutoff_2 =  webserviceRequest.psmScoreCutoff_2;

		if ( webserviceRequest.projectSearchId == null || webserviceRequest.projectSearchId == 0 ) {
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
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			//  Test access to the project id
			WebSessionAuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getWebSessionAuthAccessLevel();
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
