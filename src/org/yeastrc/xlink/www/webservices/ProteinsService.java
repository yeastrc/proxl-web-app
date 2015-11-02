package org.yeastrc.xlink.www.webservices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import org.yeastrc.xlink.dao.SearchDAO;
import org.yeastrc.xlink.dto.NRProteinDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.SearchProteinLooplink;
import org.yeastrc.xlink.www.objects.SearchProteinLooplink_Search_WebserviceResult;
import org.yeastrc.xlink.www.objects.SearchProteinCrosslink;
import org.yeastrc.xlink.www.objects.SearchProteinCrosslink_Search_WebserviceResult;
import org.yeastrc.xlink.www.searcher.ProjectIdsForSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.SearchProteinCrosslinkSearcher;
import org.yeastrc.xlink.www.searcher.SearchProteinLooplinkSearcher;
import org.yeastrc.xlink.www.constants.QueryCriteriaValueCountsFieldValuesConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.QueryCriteriaValueCountsDAO;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;

@Path("/data")
public class ProteinsService {

	private static final Logger log = Logger.getLogger(ProteinsService.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getCrosslinkProteins") 
	public List<SearchProteinCrosslink_Search_WebserviceResult> getCrosslinkProteins( @QueryParam( "search_ids" ) List<Integer> searchIds,
										  @QueryParam( "project_id" ) Integer projectIdParam,
										  @QueryParam( "peptide_q_value_cutoff" ) Double peptideQValueCutoff,
										  @QueryParam( "psm_q_value_cutoff" ) Double psmQValueCutoff,
										  @QueryParam( "protein_1_id" ) Integer protein1Id,
										  @QueryParam( "protein_2_id" ) Integer protein2Id,
										  @QueryParam( "protein_1_position" ) Integer protein1Position,
										  @QueryParam( "protein_2_position" ) Integer protein2Position,
										  @Context HttpServletRequest request )
	throws Exception {
		
		if ( searchIds == null || searchIds.isEmpty() ) {

			String msg = "Provided search_ids is null or search_ids is missing";

			log.error( msg );

		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		
		if ( peptideQValueCutoff == null ) {

			String msg = "Provided peptide_q_value_cutoff is null or peptide_q_value_cutoff is missing";

			log.error( msg );

			throw new WebApplicationException(
					Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
					.entity( msg )
					.build()
					);
		}
		
		if ( psmQValueCutoff == null ) {

			String msg = "Provided psm_q_value_cutoff is null or psm_q_value_cutoff is missing";

			log.error( msg );

		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		
		///////////////////////
		
		
		if ( protein1Id == null ) {

			String msg = "Provided protein_1_id is null or protein_1_id is missing";

			log.error( msg );

		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}		
		if ( protein2Id == null ) {

			String msg = "Provided protein_2_id is null or protein_2_id is missing";

			log.error( msg );

		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}		
		
		  
		if ( protein1Position == null ) {

			String msg = "Provided protein_1_position is null or protein_1_position is missing";

			log.error( msg );

		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}		
		if ( protein2Position == null ) {

			String msg = "Provided protein_2_position is null or protein_2_position is missing";

			log.error( msg );

		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		
		


		try {


			
			QueryCriteriaValueCountsDAO.getInstance().saveOrIncrement( 
					QueryCriteriaValueCountsFieldValuesConstants.PSM_Q_VALUE_FIELD_VALUE, Double.toString( psmQValueCutoff ) );
			QueryCriteriaValueCountsDAO.getInstance().saveOrIncrement( 
					QueryCriteriaValueCountsFieldValuesConstants.PEPTIDE_Q_VALUE_FIELD_VALUE, Double.toString( peptideQValueCutoff ) );

			
			// Get the session first.  
//			HttpSession session = request.getSession();


//			if ( searchIds.isEmpty() ) {
//				
//				throw new WebApplicationException(
//						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
//						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
//						.build()
//						);
//			}

			
			//   Get the project id for this search
			
			Collection<Integer> searchIdsCollection = new HashSet<Integer>( searchIds );

			
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
			
			
			
			Collections.sort( searchIds );
			

			List<SearchProteinCrosslink_Search_WebserviceResult> linkProteins = new ArrayList<>();
			
			for ( Integer searchId : searchIds ) {
				
				SearchDTO search = SearchDAO.getInstance().getSearch( searchId );
				
				if ( search == null ) {

					String msg = "Provided search_id " + searchId + " is not found in database.";

					log.error( msg );

				    throw new WebApplicationException(
				    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
				    	        .entity( msg )
				    	        .build()
				    	        );
				}
				
				NRProteinDTO nrProteinDTO_1 = new NRProteinDTO();
				nrProteinDTO_1.setNrseqId( protein1Id );
				
				NRProteinDTO nrProteinDTO_2 = new NRProteinDTO();
				nrProteinDTO_2.setNrseqId( protein2Id );
			
				SearchProteinCrosslink searchProteinCrosslink = SearchProteinCrosslinkSearcher.getInstance().search( search, 
						psmQValueCutoff, 
						 peptideQValueCutoff, 
						 nrProteinDTO_1,
						 nrProteinDTO_2,
						 protein1Position,
						 protein2Position
						);

				if( searchProteinCrosslink != null ) {
					
					SearchProteinCrosslink_Search_WebserviceResult item = new SearchProteinCrosslink_Search_WebserviceResult();
				
					item.setSearchProteinCrosslink( searchProteinCrosslink );
					
					linkProteins.add( item );
				}

			}
			
			return linkProteins;

			
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
	
	

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getLooplinkProteins") 
	public List<SearchProteinLooplink_Search_WebserviceResult> getLooplinkProteins( @QueryParam( "search_ids" ) List<Integer> searchIds,
										  @QueryParam( "project_id" ) Integer projectIdParam,
										  @QueryParam( "peptide_q_value_cutoff" ) Double peptideQValueCutoff,
										  @QueryParam( "psm_q_value_cutoff" ) Double psmQValueCutoff,
										  @QueryParam( "protein_id" ) Integer proteinId,
										  @QueryParam( "protein_position_1" ) Integer proteinPosition1,
										  @QueryParam( "protein_position_2" ) Integer proteinPosition2,
										  @Context HttpServletRequest request )
	throws Exception {

		if ( searchIds == null || searchIds.isEmpty() ) {

			String msg = "Provided search_ids is null or search_ids is missing";

			log.error( msg );

		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		
		if ( peptideQValueCutoff == null ) {

			String msg = "Provided peptide_q_value_cutoff is null or peptide_q_value_cutoff is missing";

			log.error( msg );

			throw new WebApplicationException(
					Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
					.entity( msg )
					.build()
					);
		}
		
		if ( psmQValueCutoff == null ) {

			String msg = "Provided psm_q_value_cutoff is null or psm_q_value_cutoff is missing";

			log.error( msg );

		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		
		///////////////////////
		
		
		if ( proteinId == null ) {

			String msg = "Provided protein_id is null or protein_id is missing";

			log.error( msg );

		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}		
		
		  
		if ( proteinPosition1 == null ) {

			String msg = "Provided protein_position_1 is null or protein_position_1 is missing";

			log.error( msg );

		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}		
		if ( proteinPosition2 == null ) {

			String msg = "Provided protein_position_2 is null or protein_position_2 is missing";

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


//			if ( searchIds.isEmpty() ) {
//				
//				throw new WebApplicationException(
//						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
//						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
//						.build()
//						);
//			}

			
			//   Get the project id for this search
			
			Collection<Integer> searchIdsCollection = new HashSet<Integer>( searchIds );

			
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
			
			
			
			Collections.sort( searchIds );
			

			List<SearchProteinLooplink_Search_WebserviceResult> linkProteins = new ArrayList<>();
			
			for ( Integer searchId : searchIds ) {
				
				SearchDTO search = SearchDAO.getInstance().getSearch( searchId );
				
				if ( search == null ) {

					String msg = "Provided search_id " + searchId + " is not found in database.";

					log.error( msg );

				    throw new WebApplicationException(
				    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
				    	        .entity( msg )
				    	        .build()
				    	        );
				}
				
				NRProteinDTO nrProteinDTO = new NRProteinDTO();
				nrProteinDTO.setNrseqId( proteinId );
				
				SearchProteinLooplink searchProteinLooplink = SearchProteinLooplinkSearcher.getInstance().search( search, 
						psmQValueCutoff, 
						 peptideQValueCutoff, 
						 nrProteinDTO,
						 proteinPosition1,
						 proteinPosition2
						);

				if( searchProteinLooplink != null ) {
					
					SearchProteinLooplink_Search_WebserviceResult item = new SearchProteinLooplink_Search_WebserviceResult();
				
					item.setSearchProteinLooplink( searchProteinLooplink );
					
					linkProteins.add( item );
				}

			}

			return linkProteins;

			
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
