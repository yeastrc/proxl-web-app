package org.yeastrc.xlink.www.webservices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.NRProteinDAO;
import org.yeastrc.xlink.dao.SearchDAO;
import org.yeastrc.xlink.dto.NRProteinDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.SearchProteinMonolink;
import org.yeastrc.xlink.www.searcher.ProjectIdsForSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.SearchProteinMonolinkSearcher;
import org.yeastrc.xlink.www.constants.QueryCriteriaValueCountsFieldValuesConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.QueryCriteriaValueCountsDAO;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;

@Path("/imageViewer")
public class ViewerMonolinkServiceForSpecificLinkInGraphService {

	private static final Logger log = Logger.getLogger(ViewerMonolinkServiceForSpecificLinkInGraphService.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getMonolinkDataForSpecificLinkInGraph") 
	public Map<Integer, SearchProteinMonolink> getViewerData( @QueryParam( "searchIds" ) List<Integer> searchIds,
										  @QueryParam( "psmQValueCutoff" ) Double psmQValueCutoff,
										  @QueryParam( "peptideQValueCutoff" ) Double peptideQValueCutoff,
										  @QueryParam( "proteinId" ) String proteinIdString,
										  @QueryParam( "proteinLinkPosition" ) String proteinLinkPositionString,
										  @Context HttpServletRequest request )
	throws Exception {
		
		if ( searchIds == null || searchIds.isEmpty() ) {

			String msg = "Provided searchIds is null or empty, searchIds = " + searchIds;

			log.error( msg );

		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		
		try {
			

			// Get the session first.  
			HttpSession session = request.getSession();


			if ( StringUtils.isEmpty( proteinIdString ) ) {
				
				String msg = "proteinId is empty or missing";

				log.error( msg );

			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			

			if ( StringUtils.isEmpty( proteinLinkPositionString ) ) {
				
				String msg = "proteinLinkPosition is empty or missing";

				log.error( msg );

			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}

			if( psmQValueCutoff == null )
				psmQValueCutoff = 0.01;
			
			if( peptideQValueCutoff == null )
				peptideQValueCutoff = 0.01;

//			if( excludeTaxonomy == null ) 
//				excludeTaxonomy = new ArrayList<Integer>();


			int proteinId = 0;
			int proteinLinkPosition = 0;


			
			QueryCriteriaValueCountsDAO.getInstance().saveOrIncrement( 
					QueryCriteriaValueCountsFieldValuesConstants.PSM_Q_VALUE_FIELD_VALUE, Double.toString( psmQValueCutoff ) );
			QueryCriteriaValueCountsDAO.getInstance().saveOrIncrement( 
					QueryCriteriaValueCountsFieldValuesConstants.PEPTIDE_Q_VALUE_FIELD_VALUE, Double.toString( peptideQValueCutoff ) );


			try {

				proteinId = Integer.parseInt( proteinIdString );

			} catch ( Exception e ) {


				String msg = "Cannot parse proteinId as Integer. value passed is: " + proteinIdString;

				log.error( msg );

				throw new WebApplicationException(
						Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
						.entity( msg )
						.build()
						);
			}


			try {

				proteinLinkPosition = Integer.parseInt( proteinLinkPositionString );

			} catch ( Exception e ) {


				String msg = "Cannot parse proteinLinkPosition as Integer. value passed is: " + proteinLinkPositionString;

				log.error( msg );

				throw new WebApplicationException(
						Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
						.entity( msg )
						.build()
						);
			}

			

			if ( searchIds.isEmpty() ) {
				
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}

			
			//   Get the project id for this search
			
			Collection<Integer> searchIdsCollection = new HashSet<Integer>( );
			
			for ( int searchId : searchIds ) {

				searchIdsCollection.add( searchId );
			}
			
			
			List<Integer> projectIdsFromSearchIds = ProjectIdsForSearchIdsSearcher.getInstance().getProjectIdsForSearchIds( searchIdsCollection );
			
			if ( projectIdsFromSearchIds.isEmpty() ) {
				
				// should never happen
				String msg = "No project ids for search ids: ";
				for ( int searchId : searchIds ) {

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

			//  Test access to the project id

			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {

				//  No Access Allowed for this project id

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);

			}


			List<SearchDTO> searches = new ArrayList<SearchDTO>();
			for( int searchId : searchIds ) {
				
				SearchDTO search = SearchDAO.getInstance().getSearch( searchId );
				
				if ( search == null ) {
					
					String msg = "Search not found in DB for searchId: " + searchId;
					
					log.error( msg );

					throw new WebApplicationException(
							Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_STATUS_CODE )  //  Send HTTP code
							.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_TEXT ) // This string will be passed to the client
							.build()
							);
				}
				

				searches.add( search );
			}
			

			
			NRProteinDTO nrProteinDTO = null;
			
			try {
				nrProteinDTO = NRProteinDAO.getInstance().getNrProtein( proteinId );
			
			} catch ( Exception e ) {
				
				String msg = "Exception getting NRProteinDTO record found for "
						+ " protein id: " + proteinId
						;

				log.error( msg, e );
				
				throw e;
			}

			if ( nrProteinDTO == null ) {

				String msg = "No NRProteinDTO record found for "
						+ " protein id: " + proteinId
						;

				log.error( msg );

				throw new WebApplicationException(
						Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
						.entity( msg )
						.build()
						);
			}
			
			

			Map<Integer, SearchProteinMonolink> searchProteinMonolinkMap = new TreeMap<>();

			SearchProteinMonolinkSearcher searchProteinMonolinkSearcher = SearchProteinMonolinkSearcher.getInstance();

			for ( SearchDTO search : searches ) {
				
				SearchProteinMonolink searchProteinMonolink =
						searchProteinMonolinkSearcher.search( search, psmQValueCutoff, peptideQValueCutoff, nrProteinDTO, proteinLinkPosition );

				if ( searchProteinMonolink == null ) {
					
					String msg = "No record found by 'SearchProteinMonolinkSearcher..search( search, psmQValueCutoff, peptideQValueCutoff, protein, proteinLinkPosition1, proteinLinkPosition2 );' " 
							+ " for "
							+ " protein id: " + proteinId
							+ ", proteinLinkPosition: " + proteinLinkPosition
							+ ", search id: " + search.getId() 
							+ ", psmQValueCutoff: " + psmQValueCutoff
							+ ", peptideQValueCutoff: " + peptideQValueCutoff
							;

					log.error( msg );

				    throw new WebApplicationException(
				    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
				    	        .entity( msg )
				    	        .build()
				    	        );
				}
				
				searchProteinMonolinkMap.put( search.getId(), searchProteinMonolink );

			}

			return searchProteinMonolinkMap;
			
		} catch ( WebApplicationException e ) {

			throw e;
			
		} catch ( Exception e ) {
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
		}
	}

}
