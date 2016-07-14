package org.yeastrc.xlink.www.webservices;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.factories.ProteinSequenceObjectFactory;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.ProteinSequenceObject;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.searcher.ProjectIdsForSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.TaxonomyIdsForProtSeqIdSearchIdSearcher;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;


@Path("/proteinTaxonomyId")
public class ViewerProteinNCBITaxonomyIdService {

	private static final Logger log = Logger.getLogger(ViewerProteinNCBITaxonomyIdService.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getDataForProtein") 
	public Map<Integer, Integer> getTaxonomyIdsDataForProteinIds( 
			@QueryParam( "searchIds" ) List<Integer> searchIdsParam,
			@QueryParam( "proteinIds" ) List<Integer>  proteinSequenceIds,  // "proteinIds" to remain compatible with JS code
			@Context HttpServletRequest request )
	throws Exception {


		if ( searchIdsParam == null || searchIdsParam.isEmpty() ) {

			String msg = "Provided searchIds is null or empty, searchIds = " + searchIdsParam;

			log.error( msg );

		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		

		if ( proteinSequenceIds == null || proteinSequenceIds.isEmpty() ) {

			String msg = "Provided proteinIds is null or empty";

			log.error( msg );

		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		
		
		try {
			

			// Get the session first.  
//			HttpSession session = request.getSession();

			//  Dedup SearchIds

			Set<Integer> searchIdsSet = new HashSet<Integer>( );
			
			for ( int searchId : searchIdsParam ) {

				searchIdsSet.add( searchId );
			}

			//   Get the project id for this search
						
			List<Integer> projectIdsFromSearchIds = ProjectIdsForSearchIdsSearcher.getInstance().getProjectIdsForSearchIds( searchIdsSet );
			
			if ( projectIdsFromSearchIds.isEmpty() ) {
				
				// should never happen
				String msg = "No project ids for search ids: ";
				for ( int searchId : searchIdsSet ) {

					msg += searchId + ", ";
				}				
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
			
			
			List<Integer> searchIdsListDedupedSorted = new ArrayList<>( searchIdsSet );

			
			Collections.sort( searchIdsListDedupedSorted );
			
			
			
			Map<Integer, Integer> proteinIdsTaxonomyIdsMap = new HashMap<Integer, Integer>();
			
			for ( Integer proteinSequenceId : proteinSequenceIds ) {

				//  don't load duplicates;

				if ( ! proteinIdsTaxonomyIdsMap.containsKey( proteinSequenceId ) ) {

					ProteinSequenceObject proteinSequenceObject = 
							ProteinSequenceObjectFactory.getProteinSequenceObject( proteinSequenceId );

					//  Get all taxonomy ids for protein sequence id and search id

					Set<Integer> taxonomyIds = null;
					
					for ( Integer searchId : searchIdsListDedupedSorted ) {
					
						taxonomyIds = TaxonomyIdsForProtSeqIdSearchIdSearcher.getInstance()
								.getTaxonomyIdsSingleSearch( proteinSequenceObject, searchId );

						if ( ! taxonomyIds.isEmpty() ) {

							//  Found at least 1 taxonomy id so exit
							break;
						}
					}

					if ( taxonomyIds == null || taxonomyIds.isEmpty() ) {

						String msg = "Failed to find a taxonomy id for proteinSequenceId: " + proteinSequenceId
								+ ", all search ids: " + searchIdsListDedupedSorted;
						log.error( msg );
						throw new ProxlWebappDataException(msg);
					}
					
					// Use the taxonomy id with the smallest non-zero value, unless they are all zero 
					
					List<Integer> taxonomyIdsSortedList = new ArrayList<>( taxonomyIds );
					
					Collections.sort( taxonomyIdsSortedList );
					
					Integer taxonomyId = null;
					
					for ( Integer taxonomyIdInSortedList : taxonomyIdsSortedList ) {
						
						if ( taxonomyIdInSortedList.intValue() != 0 ) {
							
							taxonomyId = taxonomyIdInSortedList;
							break;
						}
					}
					
					if ( taxonomyId == null ) {
						
						//  All zero so use first one
					
						taxonomyIdsSortedList.get( 0 );
					}
					
					proteinIdsTaxonomyIdsMap.put( proteinSequenceId, taxonomyId );
					
				}
			}
			
			return proteinIdsTaxonomyIdsMap;
			
		} catch ( WebApplicationException e ) {

			throw e;
			
		} catch ( Exception e ) {
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
		}	
	}
	
	
}
