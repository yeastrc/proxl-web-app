package org.yeastrc.xlink.www.webservices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import org.yeastrc.xlink.dao.SearchProgramsPerSearchDAO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.SearchProgramsPerSearchDTO;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.PSMAnnotationFilterableTypesForSearchIdServiceResult;
import org.yeastrc.xlink.www.objects.PSMAnnotationFilterableTypesForSearchIdServiceResult.PSMAnnotationFilterableTypesForSearchIdServiceResultEntry;
import org.yeastrc.xlink.www.project_search__search__mapping.MapProjectSearchIdToSearchId;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeData;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDBDataOutOfSyncException;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;

/**
 * 
 *
 */
@Path("/annotationTypes")
public class AnnotationTypesForSearchIdService {
	
	private static final Logger log = Logger.getLogger(AnnotationTypesForSearchIdService.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getAnnotationTypesPsmFilterableForSearchId") 
	public PSMAnnotationFilterableTypesForSearchIdServiceResult getPSMFilterableAnnTypesForSearchId( 
			@QueryParam( "searchId" ) int projectSearchId,
			@Context HttpServletRequest request )
	throws Exception {
		
		if ( projectSearchId == 0 ) {
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
			Collection<Integer> projectSearchIdsCollection = new HashSet<Integer>( );
			projectSearchIdsCollection.add( projectSearchId );
			List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsCollection );
			if ( projectIdsFromSearchIds.isEmpty() ) {
				// should never happen
				String msg = "No project ids for search id: " + projectSearchId;
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
			Collection<Integer> searchIdsCollection = new HashSet<Integer>( );
			searchIdsCollection.add( searchId );
			
			//  Get  Annotation Type records for PSM
			//    Filterable annotations
			Map<Integer, Map<Integer, AnnotationTypeDTO>> srchPgmFilterablePsmAnnotationTypeDTOListPerSearchIdMap =
					GetAnnotationTypeData.getInstance().getAll_Psm_Filterable_ForSearchIds( searchIdsCollection );
			Map<Integer, AnnotationTypeDTO> srchPgmFilterablePsmAnnotationTypeDTOMap = 
					srchPgmFilterablePsmAnnotationTypeDTOListPerSearchIdMap.get( searchId );
			if ( srchPgmFilterablePsmAnnotationTypeDTOMap == null ) {
				//  No records were found, probably an error   TODO
				srchPgmFilterablePsmAnnotationTypeDTOMap = new HashMap<>();
			}
			
			Map<Integer,SearchProgramsPerSearchDTO> searchProgramsPerSearchDTOMap = new HashMap<>();
			List<PSMAnnotationFilterableTypesForSearchIdServiceResultEntry> annotationTypeList = new ArrayList<>( srchPgmFilterablePsmAnnotationTypeDTOMap.size() );
			for ( Map.Entry<Integer, AnnotationTypeDTO> entry : srchPgmFilterablePsmAnnotationTypeDTOMap.entrySet() ) {
				AnnotationTypeDTO annotationTypeDTO = entry.getValue();
				Integer searchProgramsPerSearchId = annotationTypeDTO.getSearchProgramsPerSearchId();
				PSMAnnotationFilterableTypesForSearchIdServiceResultEntry psmAnnotationFilterableTypesForSearchIdServiceResultEntry = new PSMAnnotationFilterableTypesForSearchIdServiceResultEntry();
				psmAnnotationFilterableTypesForSearchIdServiceResultEntry.setAnnotationTypeDTO( annotationTypeDTO );
				SearchProgramsPerSearchDTO searchProgramsPerSearchDTO = searchProgramsPerSearchDTOMap.get( searchProgramsPerSearchId );
				if ( searchProgramsPerSearchDTO == null ) {
					searchProgramsPerSearchDTO = SearchProgramsPerSearchDAO.getInstance().getSearchProgramDTOForId( searchProgramsPerSearchId ) ;
					if ( searchProgramsPerSearchDTO == null ) {
						String msg = "No searchProgramsPerSearchDTO record found for searchProgramsPerSearchId: " + searchProgramsPerSearchId;
						log.error( msg );
						throw new ProxlWebappDBDataOutOfSyncException( msg );
					}
					searchProgramsPerSearchDTOMap.put( searchProgramsPerSearchId, searchProgramsPerSearchDTO );
				}
				psmAnnotationFilterableTypesForSearchIdServiceResultEntry.setSearchProgramsPerSearchDTO( searchProgramsPerSearchDTO );
				annotationTypeList.add( psmAnnotationFilterableTypesForSearchIdServiceResultEntry );
			}
			Collections.sort( annotationTypeList, new Comparator<PSMAnnotationFilterableTypesForSearchIdServiceResultEntry>() {
				@Override
				public int compare(PSMAnnotationFilterableTypesForSearchIdServiceResultEntry o1, PSMAnnotationFilterableTypesForSearchIdServiceResultEntry o2) {
					return o1.getAnnotationTypeDTO().getName().compareToIgnoreCase( o2.getAnnotationTypeDTO().getName() );
				}
			});
			
			PSMAnnotationFilterableTypesForSearchIdServiceResult result = new PSMAnnotationFilterableTypesForSearchIdServiceResult();
			result.setAnnotationTypeList( annotationTypeList );;
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
}
