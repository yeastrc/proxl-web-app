package org.yeastrc.xlink.www.webservices;

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
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.factories.ProteinSequenceVersionObjectFactory;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.objects.ProteinSequenceVersionObject;
import org.yeastrc.xlink.www.project_search__search__mapping.MapProjectSearchIdToSearchId;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_TaxonomyIdsFor_ProtSeqVersionId_SearchId;
import org.yeastrc.xlink.www.searcher_via_cached_data.request_objects_for_searchers_for_cached_data.TaxonomyIdsForProtSeqIdSearchId_Request;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.TaxonomyIdsForProtSeqIdSearchId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;

@Path("/proteinTaxonomyId")
public class ViewerProteinNCBITaxonomyIdService {

	private static final Logger log = LoggerFactory.getLogger( ViewerProteinNCBITaxonomyIdService.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getDataForProtein") 
	public Map<Integer, Integer> getTaxonomyIdsDataForProteinIds( 
			@QueryParam( "projectSearchIds" ) List<Integer> projectSearchIdList,
			@QueryParam( "proteinIds" ) List<Integer>  proteinSequenceVersionIds,  // "proteinIds" to remain compatible with JS code
			@Context HttpServletRequest request )
	throws Exception {
		if ( projectSearchIdList == null || projectSearchIdList.isEmpty() ) {
			String msg = "Provided projectSearchIds is null or empty, projectSearchIds = " + projectSearchIdList;
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		if ( proteinSequenceVersionIds == null || proteinSequenceVersionIds.isEmpty() ) {
			String msg = "Provided proteinIds is null or empty";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		try {
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
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
//			UserSession userSession = accessAndSetupWebSessionResult.getUserSession();
			//  Test access to the project id
			WebSessionAuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getWebSessionAuthAccessLevel();
			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {
				if ( accessAndSetupWebSessionResult.isNoSession() ) {
					//  No User session 
					throw new WebApplicationException(
							Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
							.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
							.build()
							);
				}
				//  No Access Allowed for this project id
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			////////   Auth complete
			//////////////////////////////////////////
			
			Set<Integer> searchIdsSet = new HashSet<>();
			
			for ( Integer projectSearchId : projectSearchIdsSet ) {
				Integer searchId = MapProjectSearchIdToSearchId.getInstance().getSearchIdFromProjectSearchId( projectSearchId );
				if ( searchId == null ) {
					String msg = "Unable to find searchId for projectSearchId: " + projectSearchId;
					log.error( msg );
				    throw new WebApplicationException(
				    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
				    	        .entity( msg )
				    	        .build()
				    	        );
				}
				searchIdsSet.add( searchId );
			}
			
			Map<Integer, Integer> proteinIdsTaxonomyIdsMap = new HashMap<Integer, Integer>();
			for ( Integer proteinSequenceVersionId : proteinSequenceVersionIds ) {
				//  don't load duplicates;
				if ( ! proteinIdsTaxonomyIdsMap.containsKey( proteinSequenceVersionId ) ) {
					ProteinSequenceVersionObject proteinSequenceVersionObject = 
							ProteinSequenceVersionObjectFactory.getProteinSequenceVersionObject( proteinSequenceVersionId );
					//  Get a taxonomy id for protein sequence id and search id
					boolean foundTaxonomyIdZero = false;
					final int taxonomyIdSmallestNonZeroInitialValue = Integer.MAX_VALUE;
					int taxonomyIdSmallestNonZero = taxonomyIdSmallestNonZeroInitialValue;
					//  Get all taxonomy ids for protein sequence id and search id
					for ( Integer searchId : searchIdsSet ) {
						TaxonomyIdsForProtSeqIdSearchId_Request taxonomyIdsForProtSeqIdSearchId_Request =
								new TaxonomyIdsForProtSeqIdSearchId_Request();
						taxonomyIdsForProtSeqIdSearchId_Request.setSearchId( searchId );
						taxonomyIdsForProtSeqIdSearchId_Request.setProteinSequenceVersionId( proteinSequenceVersionObject.getProteinSequenceVersionId() );
						TaxonomyIdsForProtSeqIdSearchId_Result taxonomyIdsForProtSeqIdSearchId_Result =
								Cached_TaxonomyIdsFor_ProtSeqVersionId_SearchId.getInstance()
								.getTaxonomyIdsForProtSeqIdSearchId_Result( taxonomyIdsForProtSeqIdSearchId_Request );
						Set<Integer> taxonomyIds = taxonomyIdsForProtSeqIdSearchId_Result.getTaxonomyIds();

						if ( taxonomyIds.isEmpty() ) {
							//  did not find any taxonomy id so skip to next search id
							continue;  //  EARLY CONTINUE
						}
						for ( int taxonomyIdInList : taxonomyIds ) {
							if ( taxonomyIdInList == 0 ) {
								foundTaxonomyIdZero = true;
							} else if ( taxonomyIdSmallestNonZero > taxonomyIdInList ) {
								taxonomyIdSmallestNonZero = taxonomyIdInList;
							}
						}
					}
					if ( ( ! foundTaxonomyIdZero ) && taxonomyIdSmallestNonZero == taxonomyIdSmallestNonZeroInitialValue ) {
						String msg = "Failed to find a taxonomy id for proteinSequenceVersionId: " + proteinSequenceVersionId
								+ ", all search ids: " + searchIdsSet;
						log.error( msg );
						throw new WebApplicationException(
								Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
								.entity( msg )
								.build()
								);
					}
					int taxonomyId = taxonomyIdSmallestNonZero;
					if ( ( foundTaxonomyIdZero ) && taxonomyIdSmallestNonZero == taxonomyIdSmallestNonZeroInitialValue ) {
						taxonomyId = 0;
					}
					proteinIdsTaxonomyIdsMap.put( proteinSequenceVersionId, taxonomyId );
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
