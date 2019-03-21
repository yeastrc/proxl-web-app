package org.yeastrc.xlink.www.webservices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import org.yeastrc.xlink.linker_data_processing_base.ILinker_Main;
import org.yeastrc.xlink.linker_data_processing_base.ILinkers_Main_ForSingleSearch;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.ProteinSequenceDAO;
import org.yeastrc.xlink.www.dao.ProteinSequenceVersionDAO;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.dto.ProteinSequenceDTO;
import org.yeastrc.xlink.www.dto.ProteinSequenceVersionDTO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.linkable_positions.ILinker_Main_Objects_ForSearchId_Cached;
import org.yeastrc.xlink.www.linkable_positions.ILinker_Main_Objects_ForSearchId_Cached.ILinker_Main_Objects_ForSearchId_Cached_Response;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.ProteinPositionPair;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;

@Path("/linkablePositions")
public class LinkablePositionsService {

	private static final Logger log = Logger.getLogger(LinkablePositionsService.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getLinkablePositionsBetweenProteins") 
	public Set<ProteinPositionPair> getLinkablePositionsBetweenChains( 
														@QueryParam("proteins") List<Integer> proteins,
														@QueryParam("projectSearchIds")List<Integer> projectSearchIdList,
														@Context HttpServletRequest request )
	throws Exception {
		try {
			if ( projectSearchIdList == null || projectSearchIdList.isEmpty() ) {
				String msg = "No 'projectSearchIds' parameter";
				log.warn( msg );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE ) // This string will be passed to the client
						.build()
						);
			}

			Set<ProteinPositionPair> positionPairs = new HashSet<>();
			
			if ( proteins == null || proteins.isEmpty() ) {
				//  no data to process so return
				return positionPairs;  // EARLY RETURN
			}
			

			//   Get the project id for this search
			Set<Integer> projectSearchIdsSet = new HashSet<Integer>( );
			projectSearchIdsSet.addAll( projectSearchIdList );
			List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsSet );
			if ( projectIdsFromSearchIds.isEmpty() ) {
				// should never happen
				String msg = "No project ids for projectSearchIdList: ";
				for ( int projectSearchId : projectSearchIdList ) {
					msg += projectSearchId + ", ";
				}				
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
			//  Test access to the project id
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

			Set<Integer> projectSearchIdsProcessedFromURL = new HashSet<>(); // add each projectSearchId as process in loop next
			
			Set<Integer> searchIdsSet = new HashSet<>( projectSearchIdsSet.size() );
			List<SearchDTO> searchList = new ArrayList<>( projectSearchIdsSet.size() );
			
			for ( Integer projectSearchId : projectSearchIdList ) {
				
				if ( projectSearchIdsProcessedFromURL.add( projectSearchId ) ) {
					//  Haven't processed this projectSearchId yet in this loop so process it now
					
					SearchDTO search = SearchDAO.getInstance().getSearchFromProjectSearchId( projectSearchId );
					if ( search == null ) {
						String msg = ": No search found for projectSearchId: " + projectSearchId;
						log.warn( msg );
						throw new WebApplicationException(
								Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
								.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
								.build()
								);
					}
					Integer searchId = search.getSearchId();
					searchIdsSet.add( searchId );
					searchList.add( search );
				}
			}
			
			//////////
			
			//  Cache ILinkers_Main_ForSingleSearch per search id and set allLinkersSupportedForLinkablePositions

			boolean allLinkersSupportedForLinkablePositions = true;
			
			Map<Integer, ILinkers_Main_ForSingleSearch> iLinkers_Main_ForSingleSearch_KeySearchId = new HashMap<>();
			{
				ILinker_Main_Objects_ForSearchId_Cached iLinker_Main_Objects_ForSearchId_Cached = ILinker_Main_Objects_ForSearchId_Cached.getInstance(); 
				for( SearchDTO searchDTO : searchList ) {
					int searchId = searchDTO.getSearchId();

					ILinker_Main_Objects_ForSearchId_Cached_Response iLinker_Main_Objects_ForSearchId_Cached_Response =
							iLinker_Main_Objects_ForSearchId_Cached.getSearchLinkers_ForSearchId_Response( searchId );
					ILinkers_Main_ForSingleSearch iLinkers_Main_ForSingleSearch = iLinker_Main_Objects_ForSearchId_Cached_Response.getiLinkers_Main_ForSingleSearch();

					iLinkers_Main_ForSingleSearch_KeySearchId.put( searchId, iLinkers_Main_ForSingleSearch );

					if ( ! iLinkers_Main_ForSingleSearch.isAllLinkersHave_LinkablePositions() ) {
						allLinkersSupportedForLinkablePositions = false;
					}
				}
			}
			
			if ( ! allLinkersSupportedForLinkablePositions ) {
				//  Not all linkers support Linkable positions so no Linkable positions will be computed
				return positionPairs;  //  EARLY RETURN
			}
			
			for( int proteinId1 : proteins ) {
				for( int proteinId2 : proteins ) {
					// get sequence for protein sequence version ids
					
					//  protein sequence version id 1
					ProteinSequenceVersionDTO proteinSequenceVersionDTO_1 = ProteinSequenceVersionDAO.getInstance().getFromId( proteinId1 );
					if ( proteinSequenceVersionDTO_1 == null ) {
						String msg = "No proteinSequenceVersionDTO found for proteinId 1: " + proteinId1;
						log.error( msg );
						throw new ProxlWebappDataException(msg);
					}
					String proteinSequence_1 = null;
					ProteinSequenceDTO proteinSequenceDTO_1 = 
							ProteinSequenceDAO.getInstance().getProteinSequenceDTOFromDatabase( proteinSequenceVersionDTO_1.getproteinSequenceId() );
					if ( proteinSequenceDTO_1 != null ) {
						proteinSequence_1 = proteinSequenceDTO_1.getSequence();
					}
					
					//  protein sequence version id 2
					ProteinSequenceVersionDTO proteinSequenceVersionDTO_2 = ProteinSequenceVersionDAO.getInstance().getFromId( proteinId2 );
					if ( proteinSequenceVersionDTO_2 == null ) {
						String msg = "No proteinSequenceVersionDTO found for proteinId 2: " + proteinId2;
						log.error( msg );
						throw new ProxlWebappDataException(msg);
					}
					String proteinSequence_2 = null;
					ProteinSequenceDTO proteinSequenceDTO_2 = 
							ProteinSequenceDAO.getInstance().getProteinSequenceDTOFromDatabase( proteinSequenceVersionDTO_2.getproteinSequenceId() );
					if ( proteinSequenceDTO_2 != null ) {
						proteinSequence_2 = proteinSequenceDTO_2.getSequence();
					}

					for ( Map.Entry<Integer, ILinkers_Main_ForSingleSearch> entry : iLinkers_Main_ForSingleSearch_KeySearchId.entrySet() ) {
						ILinkers_Main_ForSingleSearch iLinkers_Main_ForSingleSearch = entry.getValue();
						List<ILinker_Main> linker_MainList = iLinkers_Main_ForSingleSearch.getLinker_MainList();
						for ( ILinker_Main linker_Main : linker_MainList ) {
							
							for( int position1 : linker_Main.getLinkablePositions( proteinSequence_1 ) ) {
								for( int position2 : linker_Main.getLinkablePositions( proteinSequence_2, proteinSequence_1, position1 ) ) {					
									positionPairs.add( new ProteinPositionPair( proteinId1, position1, proteinId2, position2 ) );					
								}
							}
						}
					}
				}
			}

			return positionPairs;

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
