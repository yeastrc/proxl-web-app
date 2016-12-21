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
import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.PsmScoreVsScoreEntry;
import org.yeastrc.xlink.www.objects.PsmScoreVsScoreSearcherResults;
import org.yeastrc.xlink.www.searcher.ProjectIdsForSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.PsmAnnotationScoreScoreSearcher;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;


@Path("/qcplot")
public class QCPlotPsmScoreVsScoreService {

	private static final Logger log = Logger.getLogger(QCPlotPsmScoreVsScoreService.class);
	
	/**
	 * @param selectedLinkTypes
	 * @param searchId
	 * @param annotationTypeId
	 * @param psmScoreCutoff
	 * @param proteinSequenceIdsToIncludeList
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getPsmScoreVsScore") 
	public QCPlotPsmScoreVsScoreServiceResult getViewerData( 
			@QueryParam( "selectedLinkTypes" ) Set<String> selectedLinkTypes,			
			@QueryParam( "searchId" ) int searchId,
			@QueryParam( "annotationTypeId_1" ) int annotationTypeId_1,
			@QueryParam( "annotationTypeId_2" ) int annotationTypeId_2,
			@QueryParam( "psmScoreCutoff_1" ) Double psmScoreCutoff_1,
			@QueryParam( "psmScoreCutoff_2" ) Double psmScoreCutoff_2,
			@Context HttpServletRequest request )
	throws Exception {

		if ( searchId == 0 ) {
			String msg = ": Provided searchId is zero or wasn't provided";
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
			
			PsmScoreVsScoreSearcherResults searcherResults  = 
					PsmAnnotationScoreScoreSearcher.getInstance()
					.getPsmScoreVsScoreList( 
							searchId, 
							selectedLinkTypes,
							annotationTypeId_1, 
							psmScoreCutoff_1,
							annotationTypeId_2,
							psmScoreCutoff_2 );

			QCPlotPsmScoreVsScoreServiceResult webserviceResult = new QCPlotPsmScoreVsScoreServiceResult();
			
			webserviceResult.crosslinkChartData = searcherResults.getCrosslinkEntries();
			webserviceResult.looplinkChartData = searcherResults.getLooplinkEntries();
			webserviceResult.unlinkedChartData = searcherResults.getUnlinkedEntries();
			
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
	 * Webservice result
	 *
	 */
	public static class QCPlotPsmScoreVsScoreServiceResult {
		private List<PsmScoreVsScoreEntry> crosslinkChartData;
		private List<PsmScoreVsScoreEntry> looplinkChartData;
		private List<PsmScoreVsScoreEntry> unlinkedChartData;
		public List<PsmScoreVsScoreEntry> getCrosslinkChartData() {
			return crosslinkChartData;
		}
		public void setCrosslinkChartData(List<PsmScoreVsScoreEntry> crosslinkChartData) {
			this.crosslinkChartData = crosslinkChartData;
		}
		public List<PsmScoreVsScoreEntry> getLooplinkChartData() {
			return looplinkChartData;
		}
		public void setLooplinkChartData(List<PsmScoreVsScoreEntry> looplinkChartData) {
			this.looplinkChartData = looplinkChartData;
		}
		public List<PsmScoreVsScoreEntry> getUnlinkedChartData() {
			return unlinkedChartData;
		}
		public void setUnlinkedChartData(List<PsmScoreVsScoreEntry> unlinkedChartData) {
			this.unlinkedChartData = unlinkedChartData;
		}

	}
	
}
