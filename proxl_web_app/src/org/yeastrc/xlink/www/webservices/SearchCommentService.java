package org.yeastrc.xlink.www.webservices;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.SearchCommentDAO;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.dto.SearchCommentDTO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.SearchCommentSearcher;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;

@Path("/searchComment")
public class SearchCommentService {

	private static final Logger log = Logger.getLogger(SearchCommentService.class);
	
	
	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/delete")
	public String deleteSearchComment( @FormParam("id") int id, @Context HttpServletRequest request ) throws Exception {

		try {

			if ( id == 0 ) {

				String msg = "Provided id is zero, is = " + id;

				log.error( msg );

			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			

			// Get the session first.  
			HttpSession session = request.getSession();



			SearchCommentDTO comment = SearchCommentDAO.getInstance().load( id );
			
			if ( comment == null ) {
				
				String msg = "Failed to load comment, comment == null,  for id: " + id;
				
				log.error( msg );
				

			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
				
			int searchId = comment.getProjectSearchid();
			
			

			//   Get the project id for this search
			
			Collection<Integer> searchIdsCollection = new HashSet<Integer>( );

			searchIdsCollection.add( searchId );
			
			
			List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( searchIdsCollection );
			
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

			if ( ! authAccessLevel.isWriteAllowed() ) {

				//  No Access Allowed for this search id

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}

			
			SearchCommentDAO.getInstance().delete( id );		
			return null;


			
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
	
	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/add")
	public SearchCommentDTO saveSearchComment( @FormParam("searchId") int searchId, @FormParam("comment") String comment, @Context HttpServletRequest request ) throws Exception {


		if ( searchId == 0 ) {

			String msg = "Provided searchId is zero, searchId = " + searchId;

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



			//   Get the project id for this search
			
			Collection<Integer> searchIdsCollection = new HashSet<Integer>( );

			searchIdsCollection.add( searchId );
			
			
			List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( searchIdsCollection );
			
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

			if ( ! authAccessLevel.isWriteAllowed() ) {

				//  No Access Allowed for this search id

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}


			int authUserId = userSessionObject.getUserDBObject().getAuthUser().getId();
			
			SearchCommentDTO commentDTO  = new SearchCommentDTO( searchId, comment, authUserId );
			SearchCommentDAO.getInstance().save( commentDTO );	
			
			
			return commentDTO;


			
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
	

	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/updateText")
	public SearchCommentDTO updateSearchComment( @FormParam("id") int id, @FormParam("comment") String commentString, @Context HttpServletRequest request ) throws Exception {


		if ( id == 0 ) {

			String msg = "Provided id is zero, id = " + id;

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



			SearchCommentDTO searchCommentDTOToGetSearchId = SearchCommentDAO.getInstance().load( id );
			
			if ( searchCommentDTOToGetSearchId == null ) {
				
				String msg = "Failed to load comment, comment == null,  for id: " + id;
				
				log.error( msg );

			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
				
			int searchId = searchCommentDTOToGetSearchId.getProjectSearchid();
			
			

			//   Get the project id for this search
			
			Collection<Integer> searchIdsCollection = new HashSet<Integer>( );

			searchIdsCollection.add( searchId );
			
			
			List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( searchIdsCollection );
			
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

			if ( ! authAccessLevel.isWriteAllowed() ) {

				//  No Access Allowed for this search id

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}


			int authUserId = userSessionObject.getUserDBObject().getAuthUser().getId();
			
			SearchCommentDTO commentDTOForUpdate  = new SearchCommentDTO();
			
			commentDTOForUpdate.setId( id );
			commentDTOForUpdate.setComment( commentString );
			commentDTOForUpdate.setAuthUserId( authUserId );
					
			SearchCommentDAO.getInstance().updateComment( commentDTOForUpdate );	
			
			
			
			SearchCommentDTO commentDTOFromDB = SearchCommentDAO.getInstance().load( id );
			
			
			return commentDTOFromDB;


			
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
	
	//   Not Current,  Should use projectSearchId instead of searchId
//	
//	@GET
//	@Produces(MediaType.APPLICATION_JSON)
//	@Path("/getForSearch")
//	public List<SearchCommentDTO> getForSearch( @QueryParam("searchId") int searchId, @Context HttpServletRequest request ) throws Exception {
//
//
//		if ( searchId == 0 ) {
//
//			String msg = "Provided searchId is zero, searchId = " + searchId;
//
//			log.error( msg );
//
//		    throw new WebApplicationException(
//		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
//		    	        .entity( msg )
//		    	        .build()
//		    	        );
//		}
//		
//		
//		try {
//
//			// Get the session first.  
//			HttpSession session = request.getSession();
//
//
//
//			//   Get the project id for this search
//			
//			Collection<Integer> searchIdsCollection = new HashSet<Integer>( );
//
//			searchIdsCollection.add( searchId );
//			
//			
//			List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( searchIdsCollection );
//			
//			if ( projectIdsFromSearchIds.isEmpty() ) {
//				
//				// should never happen
//				
//				String msg = "No project ids for search id: " + searchId;
//				
//				log.error( msg );
//
//				throw new WebApplicationException(
//						Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_STATUS_CODE )  //  Send HTTP code
//						.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_TEXT ) // This string will be passed to the client
//						.build()
//						);
//			}
//			
//			if ( projectIdsFromSearchIds.size() > 1 ) {
//				
//				//  Invalid request, searches across projects
//				throw new WebApplicationException(
//						Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_STATUS_CODE )  //  Send HTTP code
//						.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_TEXT ) // This string will be passed to the client
//						.build()
//						);
//			}
//			
//
//			int projectId = projectIdsFromSearchIds.get( 0 );
//			
//
//			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
//					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
//			
//			UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();
//
//			if ( accessAndSetupWebSessionResult.isNoSession() ) {
//
//				//  No User session 
//
//				throw new WebApplicationException(
//						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
//						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
//						.build()
//						);
//			}
//			
//			//  Test access to the project id
//			
//			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();
//
//			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {
//
//				//  No Access Allowed for this search id
//
//				throw new WebApplicationException(
//						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
//						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
//						.build()
//						);
//			}
//
//
//			if ( log.isDebugEnabled() ) {
//				
//				log.debug( "getForSearch(): SearchId: " + searchId );
//			}
//			
//			
//			SearchDTO search = SearchDAO.getInstance().getSearchFromProjectSearchId( searchId );
//			
//			if ( search == null ) {
//				
//				String msg = "Search not found in DB for searchId: " + searchId;
//				
//				log.error( msg );
//
//				throw new WebApplicationException(
//						Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_STATUS_CODE )  //  Send HTTP code
//						.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_TEXT ) // This string will be passed to the client
//						.build()
//						);
//			}
//			
//			return SearchCommentSearcher.getInstance().getCommentsForSearch( search );
//
//
//			
//		} catch ( WebApplicationException e ) {
//
//			throw e;
//			
//		} catch ( Exception e ) {
//			
//			String msg = "Exception caught: " + e.toString();
//			
//			log.error( msg, e );
//			
//			
//			throw new WebApplicationException(
//					Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
//					.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT ) // This string will be passed to the client
//					.build()
//					);
//		}
//
//			
//	}

	
}
