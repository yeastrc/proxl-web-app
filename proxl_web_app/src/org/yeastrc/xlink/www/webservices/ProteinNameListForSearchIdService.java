package org.yeastrc.xlink.www.webservices;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.ProteinSequenceIdProteinAnnotationName;
import org.yeastrc.xlink.www.searcher.ProjectIdsForSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.ProteinSequenceIdAnnotationNameSearcher;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;


@Path("/proteinNames")
public class ProteinNameListForSearchIdService {

	private static final Logger log = Logger.getLogger(ProteinNameListForSearchIdService.class);
	
	/**
	 * @param searchId
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getProteinNameListForSearchId") 
	public ProteinNameListForSearchIdServiceResult getPSMFilterableAnnTypesForSearchId( 
			@QueryParam( "searchId" ) int searchId,
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
				//  No Access Allowed for this project id
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			////////   Auth complete
			//////////////////////////////////////////
			
			//  Get  ProteinSequenceId and AnnotationName for search
			
			List<ProteinSequenceIdProteinAnnotationName> proteinSequenceIdProteinAnnotationNameList = 
					ProteinSequenceIdAnnotationNameSearcher.getInstance()
					.getProteinSequenceIdAnnotationNameForSearch( searchId );

			Collections.sort( proteinSequenceIdProteinAnnotationNameList, new Comparator<ProteinSequenceIdProteinAnnotationName>() {
				@Override
				public int compare(ProteinSequenceIdProteinAnnotationName o1, ProteinSequenceIdProteinAnnotationName o2) {
					return o1.getAnnotationName().compareToIgnoreCase( o2.getAnnotationName() );
				}
			});
			ProteinNameListForSearchIdServiceResult result = new ProteinNameListForSearchIdServiceResult();
			result.proteinSequenceIdProteinAnnotationNameList = proteinSequenceIdProteinAnnotationNameList;
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
	
	
	public static final class ProteinNameListForSearchIdServiceResult {
		
		List<ProteinSequenceIdProteinAnnotationName> proteinSequenceIdProteinAnnotationNameList;

		public List<ProteinSequenceIdProteinAnnotationName> getProteinSequenceIdProteinAnnotationNameList() {
			return proteinSequenceIdProteinAnnotationNameList;
		}

		public void setProteinSequenceIdProteinAnnotationNameList(
				List<ProteinSequenceIdProteinAnnotationName> proteinSequenceIdProteinAnnotationNameList) {
			this.proteinSequenceIdProteinAnnotationNameList = proteinSequenceIdProteinAnnotationNameList;
		}
		
	}
}
